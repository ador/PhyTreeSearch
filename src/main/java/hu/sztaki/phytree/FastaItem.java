package hu.sztaki.phytree;


import java.util.ArrayList;
import java.util.List;

public class FastaItem implements Comparable<FastaItem> {

  private final String headerRow;
  private final String acNum; // access number, works as an ID
  private List<String> sequenceRows = new ArrayList<String>();
  private String fragId;
  // contains as many numbers as total num of chars in sequendeRows
  private List<Double> disorderProbs = null;
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

  public List<Double> getDisorderProbs() {
    return disorderProbs;
  }

  public void setDisorderProbs(List<Double> disorderProbs) {
    this.disorderProbs = disorderProbs;
  }

  public boolean hasDisorderProbs() {
    return (disorderProbs != null);
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
    StringBuilder sb = new StringBuilder();
    for (String s : sequenceRows) {
      sb.append(s);
    }
    return sb.toString();
  }
  
  public List<String> getSequenceRows(){
    return sequenceRows;
  }

  @Override
  public int compareTo(FastaItem other) {
    return acNum.compareTo((other).acNum);      
  }

  public void addAminoAcidWithProbability(String aminoAcid, double prob) {
    if (null == seqBuilder) {
      seqBuilder = new StringBuilder();
    }
    seqBuilder.append(aminoAcid);
    if (null == disorderProbs) {
      disorderProbs = new ArrayList<Double>();
    }
    disorderProbs.add(prob);    
  }

  public void closeSeq() {
    sequenceRows.clear();
    sequenceRows.add(seqBuilder.toString());    
  }
  
  public boolean hasDisorderedPattern(String pattern, double threshold) {
    if (hasDisorderProbs()) {
      String seq = getSequenceString();
      int fromIdx = seq.indexOf(pattern);
      if (fromIdx >= 0) {
        // check all chars of the pattern
        boolean okSoFar = true;
        for (int i = 0; i < pattern.length() && okSoFar; i++) {
          if (disorderProbs.get(fromIdx + i) < threshold) {
            okSoFar = false;
          }
        }
        return okSoFar;
      }
    }
    return false;
  }
}

