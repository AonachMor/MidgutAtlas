// Searches DB for gene info and experimental results
// DPL 14.01.2016

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeneSearch 
{		
	Expression expr;		// Expression object holding results for gene and transcript from search
	Gene gene;				// Gene object holding info on gene and transcripts
	TissueLists ftList; 
	
	// constructor takes search term for gene query and type of identifier (ID, symbol etc.)
	public GeneSearch(String searchTerm, String idType, TissueLists ftList)
	{			
		this.ftList = ftList;
		// Set names of two types of query on basis of idType
		String geneinfoQuery = "";
		if(idType.equals("fbgn"))
		{
			geneinfoQuery = "INFO_FROM_FBGN";
		}
		else if(idType.equals("cgnum"))
		{
			geneinfoQuery = "INFO_FROM_CGNUM";			
		}
		else if(idType.equals("symbol"))
		{
			geneinfoQuery = "INFO_FROM_SYMBOL";				
		}
		else if(idType.equals("name"))
		{
			geneinfoQuery = "INFO_FROM_NAME";			
		}

		// check valid search term and if so make queries
		if(!geneinfoQuery.equals(""))
		{
			// Make connection
			Connect cnt = new Connect();
			Connection conn = cnt.getConnection();
			// Make first query
			makeGeneInfoQuery(searchTerm, geneinfoQuery, conn);
	
			// Check that gene has been found and if so make other queries
			if(gene != null)
			{
				String fbgn = gene.getFBgn();
				makeTranscriptInfoQuery(fbgn, conn);

				expr = new Expression(fbgn);		// instantiate object to hold GeneDataset and TranscriptDataset		
				makeGeneDataQuery(fbgn, conn);
				makeTranscriptDataQuery(fbgn, conn);
				
				makeFBgnMaskingQuery(fbgn, conn);
				makeFBgnMaskedQuery(fbgn, conn);
			}
			
			// close connection
			if(conn != null)
			{
				try { conn.close();}
				catch(Exception e){System.out.println("Can't close.");}
			}
		}	
	}
	
	// Query for gene info for Gene object
	private void makeGeneInfoQuery(String searchTerm, String geneinfoQuery, Connection conn)
	{
		ParamQuery parIQ = DBQuery.getParamQuery(geneinfoQuery);
		try 
		{
			parIQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parIQ.getPrepStatement();
			prepStat.setString(1, searchTerm);
			if(geneinfoQuery == "INFO_FROM_SYMBOL" || geneinfoQuery == "INFO_FROM_NAME")
			{
				prepStat.setString(2, searchTerm);
			}
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())	// move to single tuple
			{
				String fbgn = resSet.getString("FBgn");
				String cgNum = resSet.getString("CGNum");
				String symbol = resSet.getString("Symbol");
				String name = resSet.getString("Name");	
				String locus = resSet.getString("Locus");			
				String biotype = resSet.getString("BioType");
				gene = new Gene(fbgn, cgNum, symbol, name, locus, biotype);			
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
		
	// Query for transcripts and then transcript info
	private void makeTranscriptInfoQuery(String fbgn, Connection conn)
	{
		// First find and order transcripts
		String transQuery = "FBTRS_FROM_FBGN";
		
		String [] fbtrNameList;						// array to hold names of FBtr
		final int NAME_LENGTH = 10;
		int nameListSize = 0;
		fbtrNameList = new String [NAME_LENGTH];
		
		ParamQuery parTrQ = DBQuery.getParamQuery(transQuery);
		try 
		{
			parTrQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	

		try 
		{
			PreparedStatement prepStat = parTrQ.getPrepStatement();
			prepStat.setString(1, fbgn);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{	
					String fbtr = resSet.getString("FBtr");
					//check for occupancy of array and expand as required
					if(nameListSize>fbtrNameList.length - 1)
					{
						String[] newList = new String[nameListSize*2];
						System.arraycopy(fbtrNameList, 0, newList, 0, nameListSize);
						fbtrNameList = newList;
					}
					fbtrNameList[nameListSize] = fbtr;
					nameListSize++;
				}
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
		
		// Now find transcript info for each FBtrm, create a Transcript object, and add to Transcript array in Gene object
		String transcriptInfoQuery = "INFO_FROM_FBTR";
		
		for(int i=0; i<nameListSize; i++)
		{
			ParamQuery parTIQ = DBQuery.getParamQuery(transcriptInfoQuery);
			try 
			{
				parTIQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	
			
			try 
			{
				String fbtr = fbtrNameList[i];
				PreparedStatement prepStat = parTIQ.getPrepStatement();
				prepStat.setString(1, fbtr);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())	// move to single tuple
				{
					String name = resSet.getString("TranscriptName");
					String strandString = resSet.getString("Strand");	// MySQL 25.4.4.3 indicates ENUM -> String OK.
					char strand = strandString.charAt(0);					
					int exonCount = resSet.getInt("ExonCount");
					String exonStarts = resSet.getString("ExonStarts");			
					String exonEnds = resSet.getString("ExonEnds");
					//create a Transcript object, and add to Transcript array in Gene object
					Transcript trans = new Transcript(fbtr, fbgn, name, strand, exonCount, exonStarts, exonEnds);
					gene.addTranscript(trans);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}			
		}
	}
	
	// Query for Experimental data for a gene
	private void makeGeneDataQuery(String fbgn, Connection conn)
	{
		GeneDataset gDataset = new GeneDataset(fbgn);
		
		String geneDataQuery = "GENE_DATA_FROM_FBGN";		
		ParamQuery parGDQ = DBQuery.getParamQuery(geneDataQuery);
		try 
		{
			parGDQ.setPrepStatement(conn);
		} 
		catch (SQLException e) 
		{System.out.println(e.toString());}	
		
		try 
		{
			PreparedStatement prepStat = parGDQ.getPrepStatement();
			prepStat.setString(1, fbgn);
			ResultSet resSet = prepStat.executeQuery();
			if(resSet.first())
			{				
				resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
				while (resSet.next())		// moves to next row while rows remain
				{
					int tissueID = resSet.getInt("TissueID");
					double fpkm = resSet.getDouble("FPKM");
					
					double replicate1 = resSet.getDouble("Replicate1");	
					double replicate2 = resSet.getDouble("Replicate2");	
					double replicate3 = resSet.getDouble("Replicate3");				
					double [] repFPKMlist = {replicate1, replicate2, replicate3};				
					
					String status = resSet.getString("Status");	
					double sd = resSet.getDouble("SD");	
					// Construct GeneTissuedata object from query
					GeneTissuedata geneData = new GeneTissuedata(fbgn, tissueID, fpkm, repFPKMlist, status, sd);
					// Add to GeneDataset
					gDataset.add(geneData);
				}
				// Add GeneDataset to Expression object
				expr.setGeneData(gDataset);
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQL Exception: " + e.toString());
		}
	}
	
	private void makeTranscriptDataQuery(String fbgn, Connection conn)
	{
		String transcriptDataQuery = "TRANSCRIPT_DATA_FROM_FBTR";
		
		// go through set of transcripts belonging to the gene
		for(int i=0; i<gene.getTranscriptListSize(); i++)
		{		
			String fbtr = gene.getTranscript(i).getFBtr();
			TranscriptDataset tDataset = new TranscriptDataset(fbgn, fbtr);
			
			ParamQuery parTDQ = DBQuery.getParamQuery(transcriptDataQuery);
			try 
			{
				parTDQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parTDQ.getPrepStatement();
				prepStat.setString(1, fbtr);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{	
						int tissueID = resSet.getInt("TissueID"); 
						double fpkm = resSet.getDouble("FPKM");
						String status = resSet.getString("Status");	
						double sd = resSet.getDouble("SD");	
						
						// Construct TranscriptTissuedata object from query
						TranscriptTissuedata transcriptData = new TranscriptTissuedata(fbgn, fbtr, tissueID, fpkm, status, sd);					
						// Add to TranscriptDataset
						tDataset.add(transcriptData);
					}
					expr.addTranscriptDataset(tDataset);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}	
		}
	}
	
	// If the FPKM data for this gene is masked, gets identity of any masking genes and sets this
		private void makeFBgnMaskingQuery(String fbgn, Connection conn)
		{
			String maskingQuery = "MASKING_FROM_MASKED";
			
			String [] maskingList;						// array to hold names of any masking genes
			final int LENGTH = 5;
			int maskingListSize = 0;
			maskingList = new String [LENGTH];
			
			ParamQuery parMgQ = DBQuery.getParamQuery(maskingQuery);
			try 
			{
				parMgQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parMgQ.getPrepStatement();
				prepStat.setString(1, fbgn);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{
						String maskingGene = resSet.getString("MaskingFBgn");
						maskingList[maskingListSize] = maskingGene;
						maskingListSize++;
					}
				}
				if(maskingListSize > 0)
				{
					gene.setMasking(maskingList, maskingListSize);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
		}
		
		// If the FPKM data for this gene masks another, gets identity of any masked genes and sets this
		private void makeFBgnMaskedQuery(String fbgn, Connection conn)
		{
			String maskedQuery = "MASKED_FROM_MASKING";
			
			String [] maskedList;						// array to hold names of any masked genes
			final int LENGTH = 5;
			int maskedListSize = 0;
			maskedList = new String [LENGTH];
			
			ParamQuery parMdQ = DBQuery.getParamQuery(maskedQuery);
			try 
			{
				parMdQ.setPrepStatement(conn);
			} 
			catch (SQLException e) 
			{System.out.println(e.toString());}	

			try 
			{
				PreparedStatement prepStat = parMdQ.getPrepStatement();
				prepStat.setString(1, fbgn);
				ResultSet resSet = prepStat.executeQuery();
				if(resSet.first())
				{
					resSet.beforeFirst();		// hack to reset cursor as 'if' moves it on a row!
					while (resSet.next())		// moves to next row while rows remain
					{
						String maskingGene = resSet.getString("MaskedFBgn");
						maskedList[maskedListSize] = maskingGene;
						maskedListSize++;
					}
				}
				if(maskedListSize > 0)
				{
					gene.setMasked(maskedList, maskedListSize);
				}
			}
			catch (SQLException e)
			{
				System.out.println("SQL Exception: " + e.toString());
			}
		}
	
	// 'Get' methods to give program access to query results
	
	public Gene getGene()
	{
		return gene;
	}
	
	public Expression getExpression()
	{
		return expr;
	}

}