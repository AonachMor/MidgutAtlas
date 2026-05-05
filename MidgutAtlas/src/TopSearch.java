// Searches database for genes or transcripts expressed preferentially in a particular gut part
// 15.01.2016

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TopSearch 
{	
	String [] fbgnList;						// array to hold FBgns
	final int FBGN_LENGTH = 250;
	int fbgnListSize = 0;
	
	String [] fbtrList;						// array to hold FBgns from transcript query
	final int FBTR_LENGTH = 100;
	int fbtrListSize = 0;
	
	Expression [] expressList;
	final int EXPR_LENGTH = 250;
	int expressListSize = 0;	
	
	Gene [] geneList;
	int geneListSize = 0;	
	
	int actualDisplayed = 0;				// actual number to be displayed (can be less than maxDisplayed)
	
	public TopSearch(int tissueID, boolean byGene, int maxDisplayed, TissueLists ftList)
	{		
		expressList = new Expression[EXPR_LENGTH];
		geneList = new Gene[EXPR_LENGTH];			// same length as parallel array
		Connect cnt = new Connect();
		Connection conn = cnt.getConnection();
		
		if(byGene)
		{
			fbgnList = new String [FBGN_LENGTH];			
			makeDiffGeneQuery(tissueID, conn);
			
			// allow for fewer hits than user has selected as max to display or ALL (-1)
			if(maxDisplayed > fbgnListSize || maxDisplayed == -1)
			{
				actualDisplayed = fbgnListSize;
			}
			else
			{
				actualDisplayed = maxDisplayed;
			}
			
			for(int i=0; i<actualDisplayed; i++)
			{
				GeneSearch gs = new GeneSearch(fbgnList[i], "fbgn", ftList);
				Expression express = gs.getExpression();
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}
		}
		else
		{
			fbtrList = new String [FBTR_LENGTH];			
			makeDiffTranscriptQuery(tissueID, conn);
			
			if(maxDisplayed > fbtrListSize)
			{
				actualDisplayed = fbtrListSize;
			}
			else
			{
				actualDisplayed = maxDisplayed;			
			}
			
			for(int i=0; i<actualDisplayed; i++)
			{
				GeneSearch gs = new GeneSearch(fbtrList[i], "fbgn", ftList);
				Expression express = gs.getExpression();
				expressList[i] = express;
				expressListSize++;
				Gene gene = gs.getGene();
				geneList[i] = gene;
				geneListSize++;
			}		
		}
		
		// close connection
		if(conn != null)
		{
			try { conn.close();}
			catch(Exception e){System.out.println("Can't close.");}
		}		
	}
	
	private void makeDiffGeneQuery(int tissueID, Connection conn)
	{
		// Search for FBgn of genes that are differentially expressed in a particular tissue
		String diffGeneQuery = "DIFF_EXPRESS_GENE_BY_TISSUE";	
		ParamQuery parDGQ = DBQuery.getParamQuery(diffGeneQuery);
		try 
		{
			parDGQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parDGQ.getPrepStatement();
			prepStat.setInt(1, tissueID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbgn = resSet.getString("FBgn");
					// double ratio = resSet.getDouble("DiffRatio"); //Sorted in query so no need to hold
					//check for occupancy of array and expand as required
					if(fbgnListSize>fbgnList.length - 1)
					{
						String[] newList = new String[fbgnListSize*2];
						System.arraycopy(fbgnList, 0, newList, 0, fbgnListSize);
						fbgnList = newList;
					}
					fbgnList[fbgnListSize] = fbgn;
					fbgnListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	private void makeDiffTranscriptQuery(int tissueID, Connection conn)
	{
		// Search for FBgn of genes that are differentially expressed in a particular tissue
		String diffTranscriptQuery = "DIFF_EXPRESS_TRANSCRIPT_BY_TISSUE";
		ParamQuery parDTQ = DBQuery.getParamQuery(diffTranscriptQuery);
		try 
		{
			parDTQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parDTQ.getPrepStatement();
			prepStat.setInt(1, tissueID);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbgn = resSet.getString("FBgn");

					//check for occupancy of array and expand as required
					if(fbtrListSize>fbtrList.length - 1)
					{
						String[] newList = new String[fbtrListSize*2];
						System.arraycopy(fbtrList, 0, newList, 0, fbtrListSize);
						fbtrList = newList;
					}
					fbtrList[fbtrListSize] = fbgn;
					fbtrListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	// get array of Expression objects from this search
	public Expression[] getExpressList()
	{
		return expressList;
	}
	
	public int getActualDisplayed()
	{
		return actualDisplayed;
	}
	
	public Gene[] getGeneList()
	{
		return geneList;
	}
	
}
