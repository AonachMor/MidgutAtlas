// Generates an HTML page for a Gene search, with or without results
// DPL 16.01.2016

public class GenePage
{
	private StringBuilder htmlBuilder;		// For building HTML
	private final int PAGE_POS = 2;			// Position of page in menu
	private String intro = "Find the expression of a particular gene "
			+ "in different sections of the midgut of Drosophila larvae.";
	
	// Instantiate initial page with no results using defaults
	public GenePage()
	{			
		htmlBuilder = new StringBuilder();
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		// idtype radio choice with fbgn selected
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation symbol (e.g. CG16858)<br />\n");
		htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		// gene descriptor field empty as default
		htmlBuilder.append("<p>\n<span class=\"rightPadGene\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"gene\" value=\"\" style=\"height:15px;\" onkeyup=\"findNames();\" />");
		htmlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");
		// Div with hidden table for autocomplete
		htmlBuilder.append(pu.getAutoCompleteDiv());
		htmlBuilder.append("</div> <!-- end of controls div -->\n");
		// Finish off with footer section	
		htmlBuilder.append(pu.getPageFoot());
	}

	// Instantiate a results page from Experiment object, 
	// rebuilding page using gene search term, idType (symbol, name etc)
	public GenePage(Gene gene, Expression expr, String searchTerm, String idType, TissueLists  ftList)
	{		
		// Build page starting with boiler-plate sections
		PageUtility pu = new PageUtility();	
		htmlBuilder = new StringBuilder(pu.getPageTop(PAGE_POS));
		htmlBuilder.append("<div class=\"explanation\">" + intro + "</div>\n");
		htmlBuilder.append("<div id=\"controls\">\n");
		// idtype radio choice  (disabling if appropriate to organism)
		if(idType.equals("symbol"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" checked=\"checked\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"symbol\" /> Gene Symbol (e.g. vkg) — start typing, then select from the autosuggest menu<br />\n");
		}

		
		if(idType.equals("name"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" checked=\"checked\"  /> Gene Name (e.g. viking)<br />\n");		
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"name\" /> Gene Name (e.g. viking)<br />\n");			
		}

		
		if(idType.equals("cgnum"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" checked=\"checked\" /> Annotation symbol (e.g. CG16858)<br />\n");		
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"cgnum\" /> Annotation symbol (e.g. CG16858)<br />\n");		
		}	

		if(idType.equals("fbgn"))
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" checked=\"checked\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		}
		else
		{
			htmlBuilder.append("<input type=\"radio\" name=\"idtype\" id=\"fbgn\" /> Gene ID (e.g. FBgn0016075)<br />\n");
		}
		
		// gene descriptor field with previous choice
		htmlBuilder.append("<p><span class=\"rightPadGene\">Gene: </span>");
		htmlBuilder.append("<input type=\"text\" autocomplete=\"off\" size=\"40\" id=\"gene\" value=\"" + searchTerm + "\" style=\"height:15px;\" onkeyup=\"findNames();\" />");
		htmlBuilder.append("<button onclick=\"sendSearchGeneForm();\">Search</button>\n</p>\n");
		
		// add key hide/show
		htmlBuilder.append("<div id=\"visible\"><strong>ANATOMICAL KEY</strong></div>");
		htmlBuilder.append("<div id=\"hideme\" style=\"display:none;\">");
		htmlBuilder.append("<p><img src=\"images/GutKey.jpg\" alt=\"\" width=\"412\" height=\"115\" /></p></div>");
		
		// Div with hidden table for autocomplete
		htmlBuilder.append(pu.getAutoCompleteDiv());
		htmlBuilder.append("</div> <!-- end of controls div -->\n");	
		
		// RESULTS FORMATTED
		if(expr!=null)
		{
			htmlBuilder.append(expr.getHTMLFormatted(gene, searchTerm, ftList, PAGE_POS));
		}
		else
		{
			htmlBuilder.append("<div class=\"explanation\">");
			htmlBuilder.append("No results found for ‘" + searchTerm +"’.");
			htmlBuilder.append("</div><!-- end of explanation div -->");
		}
		// Finish off with footer section
		htmlBuilder.append(pu.getPageFoot());
	}
	
	// returns page
	public String getHTML()
	{
		return htmlBuilder.toString();
	}

}
