// Class to hold a set of GeneTissuedata objects for all the tissues of an expt
// DPL 01.02.2016

public class GeneDataset
{
	private String fbgn;
	private GeneTissuedata [] geneDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;							// occupancy i.e. number of tissues
	private final int NUM_REPLICATES = 3;			// This refers to the replicates in ea GeneTissuedata object which should be the same
	
	public GeneDataset(String fbgn)
	{
		this.fbgn = fbgn;
		geneDataList = new GeneTissuedata [LIST_LENGTH];
	}
	
	public void add(GeneTissuedata data)
	{
		//check for occupancy of array and expand as required
		if(listSize>geneDataList.length - 1)
		{
			GeneTissuedata[] newList = new GeneTissuedata[listSize*2];
			System.arraycopy(geneDataList, 0, newList, 0, listSize);
			geneDataList = newList;
		}
		geneDataList[listSize] = data;
		listSize++;
	}
	
	public GeneTissuedata[] getGeneTissuedata()
	{
		return geneDataList;
	}
	
	public GeneTissuedata getGeneTissuedata(int pos)
	{
		return geneDataList[pos];
	}
	
	public int getGeneDataSize()
	{
		return listSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	public int getNumReplicates()
	{
		return NUM_REPLICATES;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(geneDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
