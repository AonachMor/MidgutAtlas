// Generates an HTML page for a "Top" search, with or without results
// 15.01.2016

public class TopPage
{	
	private final int PAGE_POS = 3;				// position of page in menu
	private StringBuilder htmlBuilder;			// for accumulating html output
	private String intro = "Find genes that are differentially expressed in one section of the midgut of Drosophila larvae.<br />"
			+ "The transcript option finds cases where the transcript is differentially"
			+ " expressed in a particular section, but the gene, overall, is not (or only to a lesser extent).";
	
	// Constructor for page WITHOUT results
	public TopPage(TissueLists ftList)
	{		
					//-------- Build initial page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
	
		// Drop-down list of tissues
		htmlBuilder.append("<p class=\"standard\"> Gut Part: <select name=\"tissue\" id=\"tissue\">");			
		StringBuilder tissBuilder = new StringBuilder();
		for(int i=0; i<ftList.getSize(); i++)
		{
			FlyTissue ft = ftList.getFlyTissue(i);			// get next FlyTissue object in list
			if(i==0)
			{
				tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>");
			}
			else
			{
				tissBuilder.append("<option value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>");
			}
		}		
		htmlBuilder.append(tissBuilder.toString());	
		htmlBuilder.append("</select>");
		
		// Gene or Transcript option (hard-coded)
		htmlBuilder.append("&nbsp;<span class=\"lableft\">Gene or Transcript:</span><select id=\"GorT\">");
		htmlBuilder.append("<option selected=\"selected\" value=\"Gene\">Gene</option>");
		htmlBuilder.append("<option value=\"Transcript\">Transcript</option></select>");
		
		// Max No. of results to display
		htmlBuilder.append("<span class=\"lableft\">Display:</span><select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		htmlBuilder.append("<option selected=\"selected\" value=\"20\">20</option>");
		htmlBuilder.append("<option value=\"30\">30</option>");
		htmlBuilder.append("<option value=\"40\">40</option>");
		htmlBuilder.append("<option value=\"50\">50</option>");
		htmlBuilder.append("<option value=\"-1\">All</option></select>");		// All passes -1
		
		// Search button
		htmlBuilder.append("<button onclick=\"sendSearchTopForm();\">Search</button></p>\n");	
		
		// add key hide/show
		htmlBuilder.append("<div id=\"visible\"><strong>ANATOMICAL KEY</strong></div>");
		htmlBuilder.append("<div id=\"hideme\" style=\"display:none;\">");
		htmlBuilder.append("<p><img src=\"images/GutKey.jpg\" alt=\"\" width=\"412\" height=\"115\" /></p></div>");

		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		// Finish off with footer section	
		htmlBuilder.append(pu.getPageFoot());
	}

	// Constructor for page WITH results (displayMax chosen by user, but totalDisplayed could be less than displayMax if fewer found)
	public TopPage(Expression[] expressList, Gene[] geneList, int tissueID, boolean byGene, int displayMax,int totalDisplayed, TissueLists ftList)
	{	
								//-------- Build results page ----------//
		htmlBuilder = new StringBuilder();
		// Boiler-plate sections
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");	
		
		// Drop-down list of tissues
		htmlBuilder.append("<p class=\"standard\"> Gut Part: <select name=\"tissue\" id=\"tissue\">");			
		StringBuilder tissBuilder = new StringBuilder();
		for(int i=0; i<ftList.getSize(); i++)
		{
			FlyTissue ft = ftList.getFlyTissue(i);			// get next FlyTissue object in list
			if(ft.getTissueID() == tissueID)
			{
				tissBuilder.append("<option selected=\"selected\" value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>");
			}
			else
			{
				tissBuilder.append("<option value=\"" + ft.getTissueID() + "\">" + ft.getTissueName() + "</option>");
			}
		}		
		htmlBuilder.append(tissBuilder.toString());	
		htmlBuilder.append("</select>");
		
		// Gene or Transcript option
		htmlBuilder.append("&nbsp;<span class=\"lableft\">Gene or Transcript:</span><select id=\"GorT\">");
		if(byGene)
		{
			htmlBuilder.append("<option selected=\"selected\" value=\"Gene\">Gene</option>");
			htmlBuilder.append("<option value=\"Transcript\">Transcript</option></select>");
		}
		else
		{
			htmlBuilder.append("<option value=\"Gene\">Gene</option>");
			htmlBuilder.append("<option selected=\"selected\" value=\"Transcript\">Transcript</option></select>");		
		}
		
		// Max No. of results to display
/*		htmlBuilder.append("<span class=\"lableft\">Display:</span><select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		for(int i=20; i<60; i+=10)
		{
			if(i==displayMax)
			{
				htmlBuilder.append("<option selected=\"selected\" value=\"" + i + "\">" + i + "</option>");			
			}
			else
			{
				htmlBuilder.append("<option value=\"" + i + "\">" + i + "</option>");		
			}
		}
		htmlBuilder.append("</select>");*/
		
		htmlBuilder.append("<span class=\"lableft\">Display:</span><select name=\"maxdisplayed\" id=\"maxdisplayed\" dir=\"rtl\">");
		htmlBuilder.append("<option selected=\"selected\" value=\"20\">20</option>");
		htmlBuilder.append("<option value=\"30\">30</option>");
		htmlBuilder.append("<option value=\"40\">40</option>");
		htmlBuilder.append("<option value=\"50\">50</option>");
		htmlBuilder.append("<option value=\"-1\">All</option></select>");		// All passes -1
		
		// Search button
		htmlBuilder.append("<button onclick=\"sendSearchTopForm();\">Search</button></p>\n");
		
		// add key hide/show
		htmlBuilder.append("<div id=\"visible\"><strong>ANATOMICAL KEY</strong></div>");
		htmlBuilder.append("<div id=\"hideme\" style=\"display:none;\">");
		htmlBuilder.append("<p><img src=\"images/GutKey.jpg\" alt=\"\" width=\"412\" height=\"115\" /></p></div>");

		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		
		// Go through each of the Expression objects in list and format results
		for(int i=0; i<totalDisplayed; i++)
		{
			Expression express = expressList[i];
			String fbgn = express.getFBgn();
			Gene gene = geneList[i];	
			if(expressList[i]!=null)
			{
				htmlBuilder.append(express.getHTMLFormatted(gene, fbgn, ftList, PAGE_POS));
			}
		}
		
		// Finish off with footer section	
		htmlBuilder.append(pu.getPageFoot());
	}

	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}