import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

public class IOUtility {

	private String catalogFilePath = "catalog.txt";
	private File catalogFile = new File(catalogFilePath); 

	public IOUtility() {
	

	}

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

		//while (catalog.put(in.next(), convertToDate(in.next())) != null);
		String[] lines= null;
		while(in.hasNextLine())
		{
			lines = in.nextLine().split("=");
		}
		for(int i =0; i< lines.length; i++)
		{
			catalog.put(lines[0],Long.parseLong(lines[1]));
		}
			return catalog;

	}

	public void writeCatalogToFile(Map<String, Long> catalog) {

		FileWriter fw = null;

		try {
			if (!(catalogFile.exists())) {
				catalogFile.createNewFile();
			}
			fw = new FileWriter(catalogFile.getAbsolutePath());
			PrintWriter pw = new PrintWriter(fw, true);
			
			for (Map.Entry<String, Long> i : catalog.entrySet()) {
				pw.println(i);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	public boolean checkLocalCache() {
		return catalogFile.exists();
	}
	
	public void createCatalogFile() throws IOException, UrlCacheException{
		
		try {
			catalogFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

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
	
	public Date convertToDate(long lastModDate){
		Date date = null;
		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));
		String temp = d.format(lastModDate);
		try {
			date =  d.parse(d.format(lastModDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public String convertDateToString(long lastMod) {
		
		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));
		String date = d.format(lastMod);
	
		return date;
	}
	

}
