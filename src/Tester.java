
/**
 * A simple test driver
 * 
 * @author 	Majid Ghaderi
 * @version	1.0, Sep 22, 2015
 *
 */
public class Tester {
	
	public static void main(String[] args) throws UrlCacheException {
		UrlCache cache = new UrlCache();

		String[] url = {"people.ucalgary.ca/~mghaderi/test/uc.gif", "people.ucalgary.ca/~mghaderi/majid.jpg",
						"people.ucalgary.ca/~mghaderi/cpsc441/index.html",
						"www.oracle.com:80/2015/08/mobile-web-app-state/Overview.pdf"};
		
		for (int i = 0; i < url.length; i++)
			cache.getObject(url[i]);
		
		//System.out.println("Last-Modified for " + url[i] + " is:" + cache.getLastModified(url[i]));
		//cache.getObject(url[i]);
		//System.out.println("Last-Modified for " + url[0] + " is:" + cache.getLastModified(url[0]));
	}
	
}
