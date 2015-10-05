import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * UrlCache Class
 * 
 * @author Majid Ghaderi
 * @version 1.0, Sep 22, 2015
 *
 */
public class UrlCache {

	Socket httpSocket = null;
	PrintWriter outStream;
	InputStream inStream;
	byte[] myByteArr = new byte[1024];
	private Map<String, Long> catalog = new HashMap<String, Long>();
	private IOUtility inOut;

	String header;

	/**
	 * Default constructor to initialize data structures used for caching/etc If
	 * the cache already exists then load it. If any errors then throw
	 * exception.
	 *
	 * @throws UrlCacheException
	 *             if encounters any errors/exceptions
	 */
	public UrlCache() throws UrlCacheException {
		
		//Instantiate Input/Output utility object
		inOut = new IOUtility();
		
		//Check local cache, load the catalog if it exists
		if(inOut.checkLocalCache())
		{
			catalog = inOut.readCatalogFromFile();
		}
		
		//create catalog file if none exists
		else{
			
			try {
				inOut.createCatalogFile();				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(UrlCacheException u)
			{
				System.out.print(u.getMessage());
			}
		}	
	}

	/**
	 * Downloads the object specified by the parameter url if the local copy is
	 * out of date.
	 *
	 * @param url
	 *            URL of the object to be downloaded. It is a fully qualified
	 *            URL.
	 * @throws UrlCacheException
	 *             if encounters any errors/exceptions
	 */
	public void getObject(String url) throws UrlCacheException {

		String s;

		String hostName = parseUrl(url);
		int port = getPort(hostName);
		hostName = removePortFromHostname(hostName);
		String path = parsePath(url);
		String fileName = parseTail(url);
		String concatPath = path + "/" + fileName;
		
		//set the boolean flag to true if the file exists in the catalog,
		// false otherwise
		boolean fileExists = checkCatalogForFile(url);	

		downloadFile(hostName, port, concatPath, fileName, fileExists);
		
	}

	/**
	 * Returns the Last-Modified time associated with the object specified by
	 * the parameter url.
	 *
	 * @param url
	 *            URL of the object
	 * @return the Last-Modified time in millisecond as in Date.getTime()
	 * @throws UrlCacheException
	 *             if the specified url is not in the cache, or there are other
	 *             errors/exceptions
	 */
	public long getLastModified(String url) throws UrlCacheException {
		
			return catalog.get(url);		
	}
	
	private void downloadFile(String hostName, int port, String path, String fileName, boolean exists)
	{
		try {

			httpSocket = new Socket(InetAddress.getByName(hostName), port);
			outStream = new PrintWriter((httpSocket.getOutputStream()));
			outStream.print("GET " + path + " HTTP/1.1\r\n");
			outStream.print("Host: people.ucalgary.ca\r\n\r\n");
			if(exists)
			{
				long lastMod = getLastModified(hostName+path);
				String date = inOut.convertDateToString(lastMod);
				
				outStream.print("If-modified-since: " + date);
			}
			outStream.flush();
			inStream = httpSocket.getInputStream();
			
			String absPath = System.getProperty("user.dir")
					+ System.getProperty("file.separator") + path;
			File outFile = new File(absPath, fileName);
			outFile.getParentFile().mkdirs();
			getHeaderInfo(inStream, outFile);
			
			addToCatalog(hostName +path, getLastModDateFromHeader());			
			inOut.writeCatalogToFile(catalog);
			
			//catalog = inOut.readCatalogFromFile();

			inStream.close();
			outStream.close();
		} catch (UnknownHostException e) {
			System.out.println("ERROR: " + e.getMessage());

		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		catch (UrlCacheException u)
		{
			System.out.println(u.getMessage());
		}
		
	}

	private String parseUrl(String in) {
		String[] arr1 = in.split("//");
		String[] arr;
		if(arr1.length>1) { 
			arr = arr1[1].split("/");
		}
		else{
			 arr = in.split("/");
		}
		String url = arr[0];
		return url;
	}

	private int getPort(String in) {
		int port = 80;
		if (in.contains(":")) {
			int portIndex = in.indexOf(":") + 1;
			String stringPort = in.substring(portIndex);
			port = Integer.parseInt(stringPort);
		}
		return port;
	}
	
	private void downloadExistingFile(String hostName, int port, String path, String fileName)
	{
		try {
		httpSocket = new Socket(InetAddress.getByName(hostName), port);
		outStream = new PrintWriter((httpSocket.getOutputStream()));
		outStream.print("GET " + path + " HTTP/1.1\r\n");
		outStream.print("Host: people.ucalgary.ca\r\n\r\n");
		outStream.flush();
		
			inStream = httpSocket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String parsePath(String in) {
		String[] arr = in.split("/");

		String path = "";

		for (int i = 1; i < arr.length - 1; i++) {
			path = path + "/" + arr[i];
		}

		return path;
	}

	private String parseTail(String in) {
		String[] arr = in.split("/");
		int tailIndex = arr.length;
		String tail = arr[tailIndex - 1];

		return tail;
	}

	private String removePortFromHostname(String hostName) {

		if (hostName.contains(":")) {
			int portIndex = hostName.indexOf(":");
			hostName = hostName.substring(0, portIndex);
		}
		return hostName;
	}

	private void getHeaderInfo(InputStream in, File outFile) {
		OutputStream out;
		String head = "";

		int count, offset = 0, calcCount = 0;
		byte[] buffer = new byte[1024 * 25];
		byte[] img = new byte[1024 * 25];
		boolean eoh = false;

		try {
			outFile.createNewFile();
			out = new FileOutputStream(outFile.getAbsolutePath());

			while ((count = in.read(buffer)) > 0) {

				offset = 0;

				if (!(eoh)) {
					head = new String(buffer, 0, count);
					int indexOfEoh = head.indexOf("\r\n\r\n");
					if (indexOfEoh != -1) {
						offset = indexOfEoh + 4;
						eoh = true;
						header = new String(buffer, 0, offset);
						img = Arrays.copyOfRange(buffer, offset, count);
						out.write(img);
					} else {
						count = 0;
					}
				}
				
				else {
					out.write(buffer, 0, count);
				}				
				out.flush();

			}

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void addToCatalog(String path, long date)
	{
		catalog.put(path, date);		
	}
	
	private boolean checkCatalogForFile(String filePath)
	{
		return catalog.containsKey(filePath);
	}
	
	private long getLastModDateFromHeader()
	{
		int a = header.indexOf("Last-Modified:");
		String date = header.substring(a+15, a+44).trim();
	 
		Date d = inOut.convertToDate(date);
		return d.getTime();
		
		
		
	}

	

}
