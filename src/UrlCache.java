import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UrlCache Class
 * 
 * @author Majid Ghaderi
 * @version 1.0, Sep 22, 2015
 *
 */
public class UrlCache {

	private Socket httpSocket = null;
	private PrintWriter outStream;
	private InputStream inStream;
	private Map<String, Long> catalog = new HashMap<String, Long>();
	private IOUtility inOut;

	String header;

	/**
	 * Default constructor to initialize data structures used for caching/etc If
	 * the cache already exists then load it. If any errors then throw
	 * exception.
	 *
	 * @throws UrlCacheException if encounters any errors/exceptions
	 */
	public UrlCache() throws UrlCacheException {

		
		inOut = new IOUtility();

		// Check local cache, load the catalog if it exists
		if (inOut.checkLocalCache()) {
			catalog = inOut.readCatalogFromFile();
		}

		// create catalog file if none exists
		else {

			try {
				inOut.createCatalogFile();
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
			} 
		}
	}

	/**
	 * Downloads the object specified by the parameter url if the local copy is
	 * out of date.
	 *
	 * @param url - URL of the object to be downloaded. It is a fully qualified
	 *            URL.
	 * @throws UrlCacheException
	 *             if encounters any errors/exceptions
	 */
	public void getObject(String url) throws UrlCacheException {

		// split the url into usable pieces
		String hostName = parseUrl(url);
		int port = getPort(hostName);
		hostName = removePortFromHostname(hostName);
		String path = parsePath(url);
		String fileName = parseTail(url);
		String concatPath = path + "/" + fileName;
		url = hostName + concatPath;

		// set the boolean flag to true if the file exists in the catalog,
		// false otherwise
		boolean fileExists = checkCatalogForFile(url);

		beginDownloadStream(hostName, port, concatPath, fileName, fileExists);
	}

	/**
	 * Returns the Last-Modified time associated with the object specified by
	 * the parameter url.
	 *
	 * @param url URL of the object
	 * @return the Last-Modified time in millisecond as in Date.getTime()
	 * @throws UrlCacheException
	 *             if the specified url is not in the cache, or there are other
	 *             errors/exceptions
	 */
	public long getLastModified(String url) throws UrlCacheException {

		Long temp = catalog.get(url);
		 if (temp == null){ throw new UrlCacheException();
		 }
		return temp;
		
	}
	
	
/**
 * Initiates communication with the server, determines if a regular GET or a conditional GET should be used
 * ie determines if there is a copy of the file in local cache and if so issues a conditional GET request
 * 
 * @param hostName - the hostName(server) to be used in the GET request
 * @param port - the port to be used in the server/client communication
 * @param path - the path/directory where the file exists locally and on the server
 * @param fileName - the name of the file including its extension
 * @param exists - boolean indicating if the file exists in local cache or not
 * @throws UrlCacheException
 */
	private void beginDownloadStream(String hostName, int port, String path,
			String fileName, boolean exists) throws UrlCacheException {
		
		try {

			httpSocket = new Socket(InetAddress.getByName(hostName), port);
			outStream = new PrintWriter((httpSocket.getOutputStream()));
			outStream.print("GET " + path + " HTTP/1.1\r\n");

			// add this part to create a conditional get as the file exists
			// locally
			if (exists) {
				long lastMod = getLastModified(hostName + path);
				String date = inOut.convertDateToString(lastMod);

				outStream.print("If-modified-since: " + date + "\r\n");
				outStream.print("Host: "+hostName+"\r\n\r\n");

			}

			else {
				outStream.print("Host: "+hostName+"\r\n\r\n");
			}
			
			outStream.flush();
			inStream = httpSocket.getInputStream();

			path = hostName + path;
			
			writeToFile(path, fileName, inStream);	

			inStream.close();
			outStream.close();
		} catch (UnknownHostException e) {
			System.out.println("ERROR: " + e.getMessage());

		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		} 

	}
	
	/**
	 * 
	 * @param in- URL to be parsed
	 * @return parsed URL which is a string
	 */     
	private String parseUrl(String in) {
		String[] arr1 = in.split("//");
		String[] arr;
		if (arr1.length > 1) {
			arr = arr1[1].split("/");
		} else {
			arr = in.split("/");
		}
		String url = arr[0];
		return url;
	}

	/**
	 * 
	 * @param in URL which may or may not contain a port number to be parsed
	 * @return the port number parsed from a URL or the default port 80
	 */
	private int getPort(String in) {
		int port = 80;
		if (in.contains(":")) {
			int portIndex = in.indexOf(":") + 1;
			String stringPort = in.substring(portIndex);
			port = Integer.parseInt(stringPort);
		}
		return port;
	}

	/**
	 * 
	 * @param in URL which contains a filepath
	 * @return a filepath string
	 */
	private String parsePath(String in) {
		String[] arr = in.split("/");

		String path = "";

		for (int i = 1; i < arr.length - 1; i++) {
			path = path + "/" + arr[i];
		}

		return path;
	}

	/**
	 * 
	 * @param in URL to be parsed
	 * @return returns the last component of a URL the file name including extension
	 */
	private String parseTail(String in) {
		String[] arr = in.split("/");
		int tailIndex = arr.length;
		String tail = arr[tailIndex - 1];

		return tail;
	}

	/**
	 * 
	 * @param hostName string hostname which may or may not contain a port
	 * @return hostname with the port removed if it exists
	 * 			
	 */
	private String removePortFromHostname(String hostName) {

		if (hostName.contains(":")) {
			int portIndex = hostName.indexOf(":");
			hostName = hostName.substring(0, portIndex);
		}
		return hostName;
	}

	/**
	 * 
	 * @param path The path which represents the folder structure on both locally and on the server
	 * @param fileName The name of the file
	 * @param in The input stream which is a bytestream
	 * @throws UrlCacheException
	 */
	private void writeToFile(String path, String fileName, InputStream in) throws UrlCacheException {

		// Stream to write to the file
		OutputStream out = null;
		String head = "";
		int count, offset = 0;
		byte[] buffer = new byte[1024 * 25];
		byte[] img = new byte[1024 * 25];
		
		//boolean determining if we have reached the end of the header, it is always initialized to false
		boolean eoh = false;

		try {

			while ((count = in.read(buffer)) > 0) {

				offset = 0;
					
				// if the bytestream contains the header, remove it
				if (!(eoh)) {
					head = new String(buffer, 0, count); // save the header to check it
					int indexOfEoh = head.indexOf("\r\n\r\n"); //save the index of the end of the header
					if (indexOfEoh != -1) {
						offset = indexOfEoh + 4; // this is where the bytes for the file begin
						eoh = true;		
						header = new String(buffer, 0, offset); 
						
						//check the header to see if the file has been modified or not
						if (!(inspectHeader())) {
							
							System.out.println("The file: "+ path + " is the most recent");
							break;
						}
						
						//if the header is ok create the file, as well as the outputstream
						//update the catalog and catalog file, 
						//start writing bytes to the file.
						if ((inspectHeader())) {
							String newPath = path.substring(0, path.lastIndexOf("/"));
							File outFile = makeFileAndDir(newPath, fileName);
							
							out = new FileOutputStream(
									outFile.getAbsolutePath());
							long date = getLastModDateFromHeader();
							addToCatalog(path, date);
							inOut.writeCatalogToFile(catalog);
						}
						img = Arrays.copyOfRange(buffer, offset, count);
						out.write(img);
					} else {
						count = 0;
					}
				}
				// if the inputstream was larger than the bytearray,  continue writing to the file
				else {
					out.write(buffer, 0, count);					
				}
				
				
				out.flush();
			}

			//only close the outputstream if it was opened originally, ie if there was an acceptable header
			if (inspectHeader()) {
				
				out.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * // checks header to see if it is ok to create a file
	 * @return true if it is ok false if not
	 * @throws UrlCacheException 
	 */
	private boolean inspectHeader() throws UrlCacheException {

		if(header.contains("HTTP/1.1 2")){
			return true;
		}
		else if(header.contains("HTTP/1.1 304"))
		{
			return false;
		}
		throw new UrlCacheException();
		
	}

	/**
	 * 
	* @param path The path which represents the folder structure on both locally and on the server
	 * @param fileName The name of the file
	 * @return a newly created file
	 */		
	private File makeFileAndDir(String path, String fileName) {
		// concatenate the file path
		String dir = System.getProperty("user.dir");
		String separator = System.getProperty("file.separator");
		String absPath = dir + separator + path;

		File outFile = new File(absPath, fileName);

		outFile.getParentFile().mkdirs();
		try {
			outFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outFile;
	}

	/**
	 * 
	 * @param path The path which represents the folder structure on both locally and on the server
	 * @param date the date when the file was last modified
	 */
	private void addToCatalog(String path, long date) {
		catalog.put(path, date);
	}

	/**	 * 
	 * @param filePath The path which represents the folder structure on both locally and on the server
	 * @return true if the catalog contains the filepath, false if not
	 */
	private boolean checkCatalogForFile(String filePath) {
		return catalog.containsKey(filePath);
	}

	/**
	 * 
	 * @return the date in integer format , ie the date is parsed from the header and converted to long
	 */
	private long getLastModDateFromHeader() {
		int a = header.indexOf("Last-Modified:");
		String date = header.substring(a + 15, a + 44).trim();

		Date d = inOut.convertToDate(date);
		return d.getTime();
	}

}
