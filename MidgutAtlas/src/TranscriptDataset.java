// Class to hold a set of TranscriptTissuedata objects for all the tissues of an expt.for one transcript
// DPL 01.12.2015

public class TranscriptDataset
{
	private String fbgn;
	private String fbtr;
	private TranscriptTissuedata [] transcriptDataList;
	private final int LIST_LENGTH = 30;
	private int listSize;
	
	public TranscriptDataset(String fbgn, String fbtr)
	{
		this.fbgn = fbgn;
		this.fbtr = fbtr;
		transcriptDataList = new TranscriptTissuedata [LIST_LENGTH];
	}
	
	public void add(TranscriptTissuedata data)
	{
		//check for occupancy of array and expand as required
		if(listSize>transcriptDataList.length - 1)
		{
			TranscriptTissuedata[] newList = new TranscriptTissuedata[listSize*2];
			System.arraycopy(transcriptDataList, 0, newList, 0, listSize);
			transcriptDataList = newList;
		}
		transcriptDataList[listSize] = data;
		listSize++;
	}
	
	public TranscriptTissuedata getTranscriptTissuedata(int pos)
	{
		return transcriptDataList[pos];
	}
	
	public int getTranscriptDataListSize()
	{
		return listSize;
	}
	
	public String getFBgn()
	{
		return fbgn;
	}
	
	
	public String getFBtr()
	{
		return fbtr;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<listSize; i++)
		{
			sb.append(transcriptDataList[i].toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
