package hu.sztaki.phytree.io;

import hu.sztaki.phytree.FastaItem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



public class FastaWriter {

  private OutputStream output;

  public FastaWriter(OutputStream os) {
    output = os;
  }

  public void closeOS() throws IOException {
    output.flush();
    output.close();
  }

  public void writeFastaItem(FastaItem fastaItem) throws IOException {
    output.write(fastaItem.getHeaderRow().getBytes(Charset.forName("UTF-8")));
    output.write("\n".getBytes(Charset.forName("UTF-8")));
    for (String sequenceRow : fastaItem.getSequenceRows()) {
      output.write(sequenceRow.getBytes(Charset.forName("UTF-8")));
      output.write("\n".getBytes(Charset.forName("UTF-8")));
    }
  }
  
  public void writeFastaItemWithMatch(FastaItem fastaItem, boolean matched) throws IOException {
    output.write(fastaItem.getHeaderRow().getBytes(Charset.forName("UTF-8")));
    if (matched) {
      output.write("|1".getBytes(Charset.forName("UTF-8")));
    } else {
      output.write("|0".getBytes(Charset.forName("UTF-8")));
    }
    output.write("\n".getBytes(Charset.forName("UTF-8")));
    for (String sequenceRow : fastaItem.getSequenceRows()) {
      output.write(sequenceRow.getBytes(Charset.forName("UTF-8")));
      output.write("\n".getBytes(Charset.forName("UTF-8")));
    }
  }
  
  public void writeFastaList(List<FastaItem> fastaList) throws IOException {
    for (FastaItem fastaItem : fastaList) {
      writeFastaItem(fastaItem);
    }
  }

  public void writeFastaListWithPatternMatchResult(List<FastaItem> fastaList, boolean matched) throws IOException {
    for (FastaItem fastaItem : fastaList) {
      writeFastaItemWithMatch(fastaItem, matched);
    }
  }
  
  // fasta items containing the pattern will be printed first, in alphabetical order(by AC num),
  // then the rest (also in AC-alphabetical order)
  public void writeOrderedFastaList(List<FastaItem> fastaList, String pattern) throws IOException {
    List<FastaItem> contains = new ArrayList<FastaItem>();
    List<FastaItem> notContains = new ArrayList<FastaItem>();
    
    for (FastaItem it: fastaList) {
      if (it.getSequenceString().contains(pattern)) {
        contains.add(it);
      } else {
        notContains.add(it);
      }
    }
    java.util.Collections.sort(contains);
    java.util.Collections.sort(notContains);
    
    writeFastaListWithPatternMatchResult(contains, true);
    writeFastaListWithPatternMatchResult(notContains, false);
  }

}

