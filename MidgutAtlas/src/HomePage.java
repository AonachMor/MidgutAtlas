import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// Class to generate HomePage HTML page
// DPL 13.12.2015

public class HomePage
{
	private final int PAGE_POS = 1;				// Position of page in menu
	
	public HomePage()
	{

	}
	
	public String getHome()
	{
		PageUtility pu = new PageUtility();
		StringBuilder htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append(readHTML("htmlText/home.txt"));	
		htmlBuilder.append(pu.getPageFoot());
		return htmlBuilder.toString();
	}
	
	// Reads a file into a utf-8 String - typically a file in the same directory, e.g. "htmlText/mypage"
	public String readHTML(String path)
	{
		String outString;
		InputStream stream = getClass().getResourceAsStream(path);
		if (stream !=null)
		{
			try
			{
				byte [] b = new byte[8092];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int i = 0;
				while( (i=stream.read(b)) > 0)
				{
					out.write(b, 0, i);
				}
				stream.close();
				outString = out.toString("UTF-8");	// !
			}
			catch (IOException x)
			{
				outString = "Text Misread. Please notify the site owner.";
			}
		}
		else
		{
			outString = "Text Misread. Please notify the site owner.";		
		}
		return outString;
	}
}
