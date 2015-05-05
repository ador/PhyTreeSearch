package hu.sztaki.phytree.io;

import hu.sztaki.phytree.FastaItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FastaReader {

  private final InputStream input;
  private final BufferedReader reader;
  private String nextHeaderRow;

  public FastaReader(InputStream in) {
    input = in;
    reader = new BufferedReader(new InputStreamReader(input));
  }

  public FastaItem getNextFastaItem() throws IOException {
    String line;
    String headerRow;
    String acNum;
    String fragId = "0";
    int counter = 0;
    FastaItem fastaItem = null;
    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '>' && counter == 0) {
        headerRow = line;
        String[] fields = headerRow.split("\\|"); 
        acNum = fields[0].substring(1).trim(); // cut > from the beginning
        fragId = fields[1].trim();
        //System.out.println("AC num: " + acNum);
        fastaItem = new FastaItem(headerRow, acNum, fragId);
        ++counter;
      }
      else if(line.charAt(0) != '>' && counter == 0) {
        headerRow = nextHeaderRow;
        String[] fields = headerRow.split("\\|");
        acNum = fields[0].substring(1).trim();
        fragId = fields[1].trim();
        //System.out.println("AC num: " + acNum);
        fastaItem = new FastaItem(headerRow, acNum, fragId);
        fastaItem.addSeqRow(line);
        ++counter;
      }
      else if (line.charAt(0) == '>' && counter > 0) {
        nextHeaderRow = line;
        return fastaItem;
      }
      else {
       fastaItem.addSeqRow(line); 
      }
    }
    return fastaItem;
  }

}
