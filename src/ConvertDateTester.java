import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class ConvertDateTester {
	
	public ConvertDateTester()
	{}
	
	public Date convertToDate(String in) {
		Date date = null;
		
		DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		d.setTimeZone(TimeZone.getTimeZone("GMT"));
		//String test = d.format(in);
		try {
		
			date = d.parse(in);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		String test1 = date.toString();
		String test2 = d.format(date);
		return date;

	}

	public static void main(String[] args) {
		
		ConvertDateTester t = new ConvertDateTester();
		
		Date test = t.convertToDate("Fri, 31 Aug 2007 04:21:06 GMT");
		//DateFormat d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		Date compare = t.convertToDate("Fri, 31 Aug 2007 04:21:06 GMT");
		
		long tim = test.getTime();
		long compar = compare.getTime();
		
		boolean comp = (test == compare);
		comp = test.equals(compare);
		System.out.println(comp);
		System.out.println(test.toGMTString());
		

	}
	


}
