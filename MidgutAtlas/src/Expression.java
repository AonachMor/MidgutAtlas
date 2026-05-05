// Class to hold all the expression results for a single gene, including transcript data
// DPL 16.03.2016
// DPL 16.10.2021 Update for MVLS

public class Expression
{
	private String fbgn;								// FBgn of gene searched for
	private GeneDataset geneData;
	private TranscriptDataset [] transcriptDataList;
	private final int TRANSCRIPSET_LENGTH = 10;
	private int transcriptListSize = 0;
	
	private final int GENE = 2;		// Page position
	// private final int TOP = 3;
	
	public Expression(String fbgn)
	{
		this.fbgn = fbgn;
		transcriptDataList = new TranscriptDataset [TRANSCRIPSET_LENGTH];
	}
	
	public void addTranscriptDataset(TranscriptDataset set)
	{
		//check for occupancy of array and expand as required
		if(transcriptListSize>transcriptDataList.length - 1)
		{
			TranscriptDataset[] newList = new TranscriptDataset[transcriptListSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, transcriptListSize);
			transcriptDataList = newList;
		}
		transcriptDataList[transcriptListSize] = set;
		transcriptListSize++;
	}
		
	public void setGeneData(GeneDataset dataset)
	{
		geneData = dataset;
	}
	
	// 'Get' methods
	
	public GeneDataset getGeneData()
	{
		return geneData;
	}
	
	public TranscriptDataset getTransciptData(int pos)
	{
		return transcriptDataList[pos];
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	// Generates out HTML block with results
	public String getHTMLFormatted(Gene gene, String searchTerm, TissueLists ftList, int pagePos)
	{	
		// Explanatory and gene info
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=\"explanation\">Results found for ‘" + searchTerm + "’:</div><!-- end of explanation div -->\n");
		sb.append("<div class=\"results\">\n");		// changed to allow multiple instances
		sb.append(gene.getHTMLFormatted());
		
		// check if this gene is masked by others (would have null data)
		if(gene.getMaskingListSize() > 0) 
		{
			sb.append("<div class=\"ambiguity\">\nThe data for this gene is masked by ");
			if(gene.getMaskingListSize() == 1)
			{
				sb.append("gene: ");
			}
			else
			{
				sb.append("genes: ");
			}
			for(int i=0; i<gene.getMaskingListSize(); i++)
			{
				sb.append(gene.getMaskingGene(i) + " ");
			}
			sb.append("<br />Viewing the gene region in UCSC Genome Browser (link below) or FlyBase may clarify this.\n</div>\n");
		}
		else
		{
			// add SVG image code
			if(pagePos == GENE)		// Gene page
			{
				sb.append("<div id=\"svg\">\n");
			}
			else
			{
				sb.append("<div style=\"text-align:center;\">\n");			
			}
			sb.append(getSVG(ftList));
			sb.append("</div>\n");
			
			if(pagePos == GENE)		// Gene page
			{
				// add SVG download button
				sb.append("<div style=\"text-align:right;\">");
				sb.append("<button onclick=\"sendSVG();\">Save as SVG</button>");
				sb.append("</div>");
				// Start of section for individual replicates		
				sb.append("<div class=\"explanation\" id=\"visible2\">Replicate data for ‘" + gene.getFBgn() + "’ </div><!-- end of explanation div -->\n");
				sb.append("<div id=\"results\">\n");
				sb.append("<div id=\"hideme2\" style=\"display: none;\">\n");		// start of hiden section		
				// start table
				sb.append("<table class=\"feedbackR\">\n");	
				// th row
				sb.append("<tr><th>Midgut section</th>");
				for(int j=0; j<geneData.getNumReplicates(); j++)
				{
					sb.append("<th>Replicate " + (j+1) + "</th>\n");
				}
				sb.append("<th>Mean</th><th>SD</th><th>Status</th></tr>\n");	
				// td rows
				for(int i=0; i<geneData.getGeneDataSize(); i++)
				{
					sb.append(geneData.getGeneTissuedata(i).getHTMLFormatted(i, ftList));		// this will be wrong but what the hell
				}
				// close table
				sb.append("</table>\n");
				sb.append("</div>\n");		// end of hidden section		
				// End of section for individual replicates
			}
					
			// Transcripts: Explanatory line and clearer
			sb.append("<div class=\"explanation\">Data (FPKM) for individual transcripts:</div>\n");
			sb.append("<div class=\"clearer\"></div>");
			
			// Transcripts Table		
			sb.append("<table class=\"feedback\" cellspacing=\"0\">\n");
			// th row
			sb.append(gene.getTranscript(0).getTableHeadFormatted(transcriptDataList[0], ftList));		// take Head info from first as common to all
			sb.append("\n");
			// FPKM info for individual transcripts
			for(int i=0; i<transcriptListSize; i++)
			{
				sb.append(gene.getTranscript(i).getHTMLFormatted(transcriptDataList[i], ftList, i));
				sb.append("\n");
			}
			sb.append("</table>\n");
			
			// check if this gene masks any others
			if(gene.getMaskedListSize() > 0) 
			{
				sb.append("<div class=\"ambiguity\">\n<span style=\"color:red;\">These data should be interpreted with care as this gene masks ");
				if(gene.getMaskedListSize() == 1)
				{
					sb.append("gene: ");
				}
				else
				{
					sb.append("genes: ");
				}
				for(int i=0; i<gene.getMaskedListSize(); i++)
				{
					sb.append(gene.getMaskedGene(i) + " ");
				}
				sb.append("</span><br />Viewing the gene region in UCSC Genome Browser (link below) or FlyBase may clarify this.\n</div>\n");
			}
			
			// check if this is an RNA gene and if so alert user
			String prefix = gene.getAnnotationSymbol().substring(0, 2);
			if(prefix.equals("CR"))
			{
				sb.append("<div class=\"ambiguity\">\n<span style=\"color:red;\">These data are for an RNA transcript, which one would not expect to detect in this study.");
				sb.append("</span><br />FPKM values above background would suggest a small RNA with poor replicates or a longer RNA with an overlapping highly-expressed protein gene."
						+ " In these cases you are advised to examine the sequence reads in the UCSC Genome Browser (link below).\n</div>\n");
			}
		}
		
		// close div
		sb.append("</div><!-- end of results div -->\n");
		
		// Link-out to UCSC Browser
		sb.append(getUCSClink(gene));
		
		// close results div (rounded box)
		if(pagePos == GENE)		// Gene page
		{
			sb.append("</div> <!-- end of results div -->\n");
		}
		
		return sb.toString();
	}
	
	// generates html string for SVG graphic
	private String getSVG(TissueLists tissueList)
	{
		int listSize = geneData.getGeneDataSize();
		
		double[] values = new double[listSize];
		for(int i=0; i<listSize; i++)
		{
			values[i] = geneData.getGeneTissuedata(i).getFPKM();
		}
		
		CreateImage ci = new CreateImage(fbgn, tissueList, values);
		return ci.getSVG();
	}
	
	// generates link to load RNAseq bigwig tracks in UCSC browser with customization of other tracks FOR MVLS
	private String getUCSClink(Gene gene)
	{
		StringBuilder sb = new StringBuilder("<div class=\"explanation\">View reads and transcripts for ");
		sb.append("<a href=\"http://genome.ucsc.edu/cgi-bin/hgTracks?db=dm6&amp;position=");
		sb.append(gene.getLocus());
		sb.append("&amp;hgct_customText=http://motif.mvls.gla.ac.uk/fly/gutTracksDm6.txt");
		sb.append("&xenoRefGene=hide&phyloP27way_sel=0&phastCons27way_sel=0&multiz27way_sel=0&ensGene=pack&refGene=hide\"");
		sb.append(" onclick=\"window.open(this.href); return false;\">");
		sb.append(gene.getFBgn());
		sb.append("</a> in UCSC Genome Browser.</div>");
		return sb.toString();
	}
	
	// for testing
	public String geneDataToString()
	{
		return geneData.toString();
	}

	// for testing
	public String transcriptDataToString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<transcriptListSize; i++)
		{
			sb.append(transcriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
