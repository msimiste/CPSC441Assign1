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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

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

		BufferedReader bf = null;

		try {

			httpSocket = new Socket(InetAddress.getByName(hostName), 80);
			outStream = new PrintWriter((httpSocket.getOutputStream()));

			// outStream.print("GET " + path + "/" + fileName +
			// " HTTP/1.1\r\n");
			outStream.print("GET " + concatPath + " HTTP/1.1\r\n");
			outStream.print("Host: people.ucalgary.ca\r\n\r\n");
			outStream.flush();

			inStream = httpSocket.getInputStream();

			path = path.substring(1);
			path = path.replace("/", "\\");
			// path.replaceAll("\\" + fileName, "");
			String absPath = System.getProperty("user.dir")
					+ System.getProperty("file.separator") + path;
			File outFile = new File(absPath, fileName);
			outFile.getParentFile().mkdirs();
			getHeaderInfo(inStream, outFile);
	
			inStream.close();
			outStream.close();
		} catch (UnknownHostException e) {
			System.out.println("ERROR: " + e.getMessage());

		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		} 
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
		return 0;
	}

	private String parseUrl(String in) {
		String[] arr = in.split("/");
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
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			out = new FileOutputStream(outFile.getAbsolutePath());

			try {
				while ((count = in.read(buffer)) > 0) {
					offset = 0;
					if (!(eoh)) {
						head = new String(buffer, 0, count);
						int indexOfEoh = head.indexOf("\r\n\r\n");
						if (indexOfEoh != -1) {
							calcCount = count - indexOfEoh - 4;
							offset = indexOfEoh + 4;
							eoh = true;
							header = new String(buffer, 0, offset);
					        
						} else {
							//count = 0;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				out.write(img);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * BufferedReader br = new BufferedReader(new InputStreamReader(in));
		 * String temp; String header =""; int bytes = 0;
		 * 
		 * FileWriter fw;
		 * 
		 * 
		 * try { //temp = in.read(myByteArr) String tempPath =
		 * outFile.getAbsolutePath(); fw = new
		 * FileWriter(outFile.getAbsolutePath()); OutputStream testOut = new
		 * FileOutputStream (outFile.getAbsolutePath());
		 * 
		 * while(!(temp.equals(""))) { header += temp + ("\n"); temp =
		 * br.readLine(); }
		 * 
		 * while((bytes = in.read(myByteArr, 0 , bytes))>0) {
		 * testOut.write(myByteArr, 0, bytes); }
		 * 
		 * 
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * return header;
		 */

	}
}
