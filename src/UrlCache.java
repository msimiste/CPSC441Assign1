import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


/**
 * UrlCache Class
 * 
 * @author 	Majid Ghaderi
 * @version	1.0, Sep 22, 2015
 *
 */
public class UrlCache {

    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw exception.
	 *
     * @throws UrlCacheException if encounters any errors/exceptions
     */
	public UrlCache() throws UrlCacheException{}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws UrlCacheException if encounters any errors/exceptions
     */
	public void getObject(String url) throws UrlCacheException
	{
		Socket httpSocket = null;
		PrintWriter outStream;
		Scanner inStream;
		String s;
		
		
		
	
		try{	
			
			httpSocket = new Socket(InetAddress.getByName(url),80);
			outStream =new PrintWriter(httpSocket.getOutputStream(), false);
			
			
			outStream.print("GET /~mghaderi/cpsc441/index.html HTTP/1.1\r\n");
			outStream.print("Host: people.ucalgary.ca\r\n\r\n");
			inStream = new Scanner(new InputStreamReader(httpSocket.getInputStream()));
			
				
			while(true)
			{				
				outStream.flush();
				s = inStream.nextLine();
				System.out.println(s);
				if(!(inStream.hasNext())) { break;}
				
			}
			inStream.close();
			outStream.close();
		}
		catch(UnknownHostException e){
			System.out.println("ERROR: " + e.getMessage());
			
		}
		catch(IOException e){
			System.out.println("ERROR: " + e.getMessage());
		}
	}
		
			
    /**
     * Returns the Last-Modified time associated with the object specified by the parameter url.
	 *
     * @param url 	URL of the object 
	 * @return the Last-Modified time in millisecond as in Date.getTime()
     * @throws UrlCacheException if the specified url is not in the cache, or there are other errors/exceptions
     */
	public long getLastModified(String url) throws UrlCacheException{return 0;}

}
