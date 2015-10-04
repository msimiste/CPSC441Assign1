
public class Controller {

	public static void main(String[] args) {

		try {
			UrlCache test = new UrlCache();
			//test.getObject("people.ucalgary.ca/~mghaderi/cpsc441/index.html");
			test.getObject("www.oracle.com:80/2015/08/mobile-web-app-state/Overview.pdf");
		} catch (UrlCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	
	}
}
