
/* MidgutAtlas
Servlet for Drosophila Midgut RNAseq data
DPL 31.05.2016
Updated 07.11.2021 for Tomcat 8.5 UTF-8 handling of parameters
 */	

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class MidgutAtlas extends HttpServlet
{
	private TissueLists  ftList;				// stores info about all fly tissues and stages: passed to classes that need to display results
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
					throws ServletException, IOException 
	{	
		// Set Content type
		res.setContentType("text/html;charset=UTF-8");	
		res.setHeader("Cache-Control", "no-cache");
		
		// Do stuff and respond. NB Don't get PrintWriter until ContentType has been set
		PrintWriter writer = res.getWriter();
		
		/* BUILD START PAGE ON LAUNCH */
		if (req.getParameter("page") == null  && req.getParameter("search") == null)	// Defines startup 
		{		
			HomePage home = new HomePage();
			writer.println(home.getHome());	
			
			ftList = new TissueLists();			// Just at startup
		}
		
		/* OR BUILD START PAGES ACCESSED BY LINK FROM OTHER PAGE */
		else if (req.getParameter("page") != null)	// Page request identification to distinguish from results pages
		{		
			if (req.getParameter("page").equals("gene"))			// Gene page
			{
				GenePage genePage = new GenePage();
				writer.println(genePage.getHTML());
			}
			else if (req.getParameter("page").equals("top"))		// Top page
			{
				TopPage topPage = new TopPage(ftList);
				writer.println(topPage.getHTML());
			}
			else if (req.getParameter("page").equals("home"))		// Home page
			{
				HomePage home = new HomePage();
				writer.println(home.getHome());
			}
		}	
		
		/* OR BUILD SEARCH PAGE OF APPROPRIATE TYPE */
		else if (req.getParameter("search").equals("gene"))	
		{		
			String searchTerm = req.getParameter("gene");					// Parameter to specify value of gene id
			// searchTerm = new String(searchTerm.getBytes("8859_1"), "UTF-8");	// Tomcat 8.5 — Uncomment for Tomcat 6
			searchTerm = searchTerm.trim();									//trim whitespace
			// find type of gene identifier
			String idType = req.getParameter("idtype");			// Parameter to specify whether FBgn, Gene symbol, Gene name or CGnum)
			idType = idType.replaceAll("[^a-zA-Z0-9]", "");		// Prevent cross-scripting	
			// start search
			GeneSearch search = new GeneSearch(searchTerm, idType, ftList);		
			// retrieve info
			Gene gene = search.getGene(); 					
			// retrieve results
			Expression expr = search.getExpression();		
			// construct HTML page, return it, and close the print writer
    		GenePage genePage = new GenePage(gene, expr, searchTerm, idType, ftList);
    		writer.println(genePage.getHTML());
			writer.close();
		}
		else if (req.getParameter("search").equals("top"))
		{
			int tissueID = Integer.parseInt(req.getParameter("tissue"));
			String gORt = req.getParameter("GorT");
			boolean byGene = true;	
			if (gORt.equals("Transcript"))
			{
				byGene=false;
			}
			int maxdisplayed = Integer.parseInt(req.getParameter("maxdisplayed"));
			
			TopSearch search = new TopSearch(tissueID, byGene, maxdisplayed, ftList);
			Expression[] expressList = search.getExpressList();
			Gene[] geneList = search.getGeneList();
			int actualDisplayed = search.getActualDisplayed();
			
			TopPage topPage = new TopPage (expressList, geneList, tissueID, byGene, maxdisplayed, actualDisplayed, ftList);
    		writer.println(topPage.getHTML());
			writer.close();
		}
	}
}


