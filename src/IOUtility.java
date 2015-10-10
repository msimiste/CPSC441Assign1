import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * 
 * @author msimiste
 * This Class was created in order to assist with the I/O involved in CPSC441 Assignment1
 * Additionally, some date conversion utilities were required and added to this class
 * @version 1.0 Oct 02, 2015
 */
public class IOUtility {

	private String catalogFilePath = "catalog.txt";
	private File catalogFile = new File(catalogFilePath);

	public IOUtility() {}

	/**
	 * 
	 * @return A catalog file which is stored locally
	 */
	public Map<String, Long> readCatalogFromFile() {

		FileReader fr = null;
		Map<String, Long> catalog = new HashMap<String, Long>();

		try {
			fr = new FileReader(catalogFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		Scanner in = new Scanner(br);

		// while (catalog.put(in.next(), convertToDate(in.next())) != null);
		String[] lines = null;

		while (in.hasNextLine()) {
			lines = in.nextLine().split("=");
			catalog.put(lines[0], Long.parseLong(lines[1]));
		}

		in.close();
		return catalog;
	}

	/**
	 * 
	 * @param catalog - A hashmap, the contents of which are stored in a file locally
	 */
	public void writeCatalogToFile(Map<String, Long> catalog) {

		FileWriter fw = null;
		PrintWriter pw = null;

		try {
			if (!(catalogFile.exists())) {
				catalogFile.createNewFile();
			}
			fw = new FileWriter(catalogFile.getAbsolutePath());
			pw = new PrintWriter(fw, true);

			for (Map.Entry<String, Long> i : catalog.entrySet()) {
				pw.println(i);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

	/**
	 * 
	 * @return A boolean value indicating if there is a catalog file stored
	 *         locally or not, ie if there is a local file it returns true,
	 *         otherwise it returns false
	 */
	public boolean checkLocalCache() {
		return catalogFile.exists();
	}
	
	/**
	 * Creates a catalog file, throwing exceptions if there is an error in the
	 * process
	 * 
	 * @throws IOException
	 * @throws UrlCacheException
	 */
	public void createCatalogFile() throws IOException, UrlCacheException {

		try {
			catalogFile.createNewFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param in - A date in the form of a String
	 * @return Date value which has been formatted
	 */
	public Date convertToDate(String in) {
		Date date = null;
		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			date = d.parse(in);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @param lastModDate
	 *            A Time to be formatted
	 * @return Date value which has been formatted
	 */
	public Date convertToDate(long lastModDate) {
		Date date = null;
		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));

		try {
			date = d.parse(d.format(lastModDate));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 
	 * @param lastMod
	 *            A Time value to be formatted
	 * @return String which has been converted to a date and formatted.
	 */
	public String convertDateToString(long lastMod) {

		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = d.format(lastMod);

		return date;
	}
}
