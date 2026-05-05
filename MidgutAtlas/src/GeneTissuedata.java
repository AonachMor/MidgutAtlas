// Class to hold FPKM data for a gene in a single tissue
// DPL 01.02.2016

import java.text.*;				// for number formatting

public class GeneTissuedata
{
	private String fbgn;
	private int tissueID;
	private double fpkm;
	private double [] repFPKMlist;			// This array should have an occupancy of 3 (i.e. triplicate)
	private final int NUM_REPLICATES = 3;	
	private String status;
	private double sd;
	
	public GeneTissuedata(String fbgn, int tissueID, double fpkm, double[] repFPKMlist, String status, double sd)
	{
		this.fbgn = fbgn;
		this.tissueID = tissueID;
		this.fpkm = fpkm;
		this.repFPKMlist = repFPKMlist;
		this.status = status;
		this.sd = sd;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public int getTissueID()
	{
		return tissueID;
	}
	
	public double getFPKM()
	{
		return fpkm;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public double getRepFPKM(int pos)
	{
		return repFPKMlist[pos];
	}
	
	public double getSD()
	{
		return sd;
	}
	
	// Formats data for HTML output as table row
	public String getHTMLFormatted(int rowNum, TissueLists ftList)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("<tr>");
		buffer.append("<td>" + ftList.getTissueNameByID(tissueID) + "</td>");

		NumberFormat N = NumberFormat.getInstance();
		N.setMaximumFractionDigits(2);	
		for(int i=0; i<NUM_REPLICATES; i++)
		{
			buffer.append("<td>" + padDecimals(N.format(repFPKMlist[i]), 2) + "</td>");
		}
		buffer.append("<td>" + padDecimals(N.format(fpkm), 2) + "</td>");
		buffer.append("<td>" + padDecimals(N.format(sd), 2) + "</td>");
		buffer.append("<td>" + status + "</td>");
		buffer.append("</tr>\n");		
		return buffer.toString();
	}
	
	// Adds zeros to decimals to pad out to fixed number of places
	private String padDecimals(String rawNum, int numPlaces)
	{
		int point = rawNum.indexOf(".");
		if(point == -1)
		{
			// add a decimal point if necessary for 0 or integral value (?)
			rawNum = rawNum + ".";
			point = rawNum.indexOf(".");	
		}
		String afterPoint = rawNum.substring(point+1, rawNum.length());
		int lenAfterPoint = afterPoint.length();	
		if(numPlaces > lenAfterPoint)
		{
			StringBuilder sb = new StringBuilder(rawNum);
			for(int i=0; i<numPlaces-lenAfterPoint; i++)
			{
				sb.append("0");
			}
			//return rawNum;
			return sb.toString();
		}
		else
		{
			return rawNum;
		}
	}
	
	// replaces default as tab-separated text suitable for output
	public String toString()
	{
		return fbgn + "\t" + tissueID + "\t" + fpkm + "\t" + "(" + repFPKMlist[0] + ", " +  repFPKMlist[1] + ", " +  repFPKMlist[2] + ")" + "\t" + status + "\t" + sd;
	}
}
