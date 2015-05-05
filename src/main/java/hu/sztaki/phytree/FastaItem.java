package hu.sztaki.phytree;


import java.util.ArrayList;
import java.util.List;

public class FastaItem implements Comparable<FastaItem> {

  private final String headerRow;
  private final String acNum; // access number, works as an ID
  private List<String> sequenceRows = new ArrayList<String>();
  private String fragId;
  // contains as many numbers as total num of chars in sequendeRows
  private StringBuilder seqBuilder = null;

  public FastaItem(String header, String ac, String fragment) {
    if (!header.contains(ac)) {
      System.out.println("WARNING: fasta item header does not contain the given AC num (" + ac + ")\n" + header );
    }
    headerRow = header;
    acNum = ac;
    fragId = fragment;
  }
  
  public String getFragId() {
    return fragId;
  }

  public void setFragId(String fragId) {
    this.fragId = fragId;
  }

  public String getHeaderRow() {
    return headerRow;
  }

  public String getAcNum() {
    return acNum;
  }

  public void setSequenceRows(List<String> sequenceRows) {
    this.sequenceRows = sequenceRows;
  }

  public void addSeqRow(String s) {
    sequenceRows.add(s);
  }

  public String getSequenceString() {
    seqBuilder = new StringBuilder();
    for (String s : sequenceRows) {
      seqBuilder.append(s);
    }
    return seqBuilder.toString();
  }
  
  public List<String> getSequenceRows(){
    return sequenceRows;
  }

  @Override
  public int compareTo(FastaItem other) {
    return acNum.compareTo((other).acNum);      
  }

}

