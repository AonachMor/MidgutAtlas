
 /*
Class to hold HTML code and write specific HTML menus 
for different pages of MidgutAtlas
20.12.2015; 
07.11.2021
*/

public class PageUtility
{
	private PageDescriptor pageList[];			// list of page descriptor objects
	private final int LENGTH = 3;				// Number of pages
	
	private String head = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
		+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n"
		+ "<head>\n<title>MidgutAtlas</title>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
		+ "<script type=\"text/javascript\" src=\"scripts/midgutAtlas.js\"></script>\n"
		+ "<script type=\"text/javascript\" src=\"scripts/aCompleteMG.js\"></script>\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/midgutAtlas.css\" />\n"
		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"scripts/aCompleteMG.css\" />\n"
		+ "<link rel=\"icon\" type=\"image/x-ico\" href=\"images/fly.ico\">\n"
		+ "<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"images/fly.ico\">\n"
		+ "<link rel=\"apple-touch-icon\" href=\"images/flyatlas-touch-icon-57x57.png\">\n"
		+ "<link rel=\"apple-touch-icon\" sizes=\"72x72\" href=\"images/flyatlas-touch-icon-72x72.png\">\n"
		+ "<link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"images/flyatlas-touch-icon-114x114.png\">\n"
		+ "</head>\n\n";
	
	// utility boilerplate
	private String autoCompleteDiv =		
		"<div style=\"position:absolute;\" id=\"popup\">\n"
		+ "<table id=\"menuTable\" cellspacing=\"0\" cellpadding=\"0\">\n"           
		+ "<tbody id=\"menuTableBody\"><tr><td></td></tr></tbody>\n"
		+ "</table>\n</div><!-- end of autocomplete div -->\n";
		
	private String foot = 
			"\n<div style=\"float: right; padding-top: 10px;\">\n"
			+ "<img src=\"images/BBSRC.jpg\" alt=\"\" width=\"180\" height=\"70\" title=\"Funded by the BBSRC\" />\n</div>\n"
			+ "<div style=\"padding-top: 10px;\">\n<a href=\"http://www.gla.ac.uk/\"><img src=\"images/UofG.jpg\" alt=\"\" width=\"217\" height=\"70\" title=\"University of Glasgow\" /></a>\n"
			+ "</div>\n<div style=\"clear: both;\"></div>\n"
			+ "</div> <!-- end of wrapper div -->\n"
			+ "</body>\n</html>\n";

	public PageUtility()
	{
		pageList = new PageDescriptor[LENGTH];
		initializePageList();
	}
	
	// Also OK for combination with dynamic
	public String getPageFoot()
	{
		return foot;
	}
	
	// utility method
	public String getAutoCompleteDiv()
	{
		return autoCompleteDiv;
	}
	
			// Dynamic replacement for simple ACCESSOR methods //
	
	// builds top section of html page with appropriate names and links
	public String getPageTop(int pagePos)
	{
		StringBuilder pBuilder = new StringBuilder(head);		// boiler plate
		pBuilder.append(pageList[pagePos-1].getBodyLine());		// <body> line
		pBuilder.append("<div id=\"wrapper\">\n");				
		pBuilder.append("<h1>MidgutAtlas</h1>\n");
		pBuilder.append("<h2>");
		// links
		for(int i=0; i<LENGTH; i++)
		{
			if(pageList[i].getPagePos() == pagePos)		// Page calling the html block
			{
				pBuilder.append("<span class=\"inactive\">" + pageList[i].getPageName() + "</span>\n");		// no self-link
			}
			else
			{
				pBuilder.append("<a class=\"active\" href=\"javascript:" + pageList[i].getToMethodName() + ";\">" + pageList[i].getPageName() + "</a>\n");
			}
		}
		pBuilder.append("</h2>");
		return pBuilder.toString();
	}
	
	///////////////////////////////////////////////
	
	// initializes array of PageDescriptors
	private void initializePageList()
	{ 
		pageList[0] = new PageDescriptor(1, "Home", "toHomeForm()", "<body>\n");
		pageList[1] = new PageDescriptor(2, "Gene", "toGeneForm()", "<body onload=\"createLink();createLink2();\" onkeypress=\"geneKey(event);\">\n");
		pageList[2] = new PageDescriptor(3, "Differential", "toTopForm()", "<body onload=\"createLink();\" onkeypress=\"topKey(event);\">\n");
	}
	
	// inner class to hold a utility pageDescriptor object	
	class PageDescriptor
	{
		int pagePos;				// order of page in menu 1 to n
		String pageName;			// name of page as it appears on the menu
		String toMethodName;		// name of javascript method to generate new page	
		String bodyLine;			// html <body> line - differs depending on javascript
		PageDescriptor(int pagePos, String pageName, String toMethodName, String bodyLine)
		{
			this.pagePos = pagePos;
			this.pageName = pageName;
			this.toMethodName = toMethodName;
			this.bodyLine = bodyLine;
		}
		public int getPagePos()
		{
			return pagePos;
		}
		public String getPageName()
		{
			return pageName;
		}
		public String getToMethodName()
		{
			return toMethodName;
		}
		public String getBodyLine()
		{
			return bodyLine;
		}
	}
	
}
