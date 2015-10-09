
/**
 * UrlCacheException Class
 * 
 * @author 	Majid Ghaderi
 * @version	1.0, Sep 22, 2015
 */
public class UrlCacheException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor calls Exception super class with message
     */
    public UrlCacheException() {
        super("UrlCache exception");
    }

    /**
     * Constructor calls Exception super class with message
     * @param message The message of exception
     */
    public UrlCacheException(String message) {
        super(message);
    }
}
