
public class Controller {

	public static void main(String[] args) {

		try {
			UrlCache test = new UrlCache();
			test.getObject("people.ucalgary.ca");
		} catch (UrlCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	
	}
}
