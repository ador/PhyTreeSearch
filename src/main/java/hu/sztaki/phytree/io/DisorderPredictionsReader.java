package hu.sztaki.phytree.io;

import hu.sztaki.phytree.FastaItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DisorderPredictionsReader {

  private final BufferedReader reader;
  private FastaItem lastFastaItem = null;
  private boolean isFirst = true;
  private boolean wasLast = false;
  
  public DisorderPredictionsReader(InputStream input) {
    reader = new BufferedReader(new InputStreamReader(input));
  }

  private FastaItem createNewItem(String headerRow) {
    String[] fields = headerRow.split("\\|"); 
    String acNum = fields[1].trim();
    String fragId = fields[2].trim();
    FastaItem fastaItem = new FastaItem(headerRow, acNum, fragId);
    return fastaItem;
  }
  
  public FastaItem getNextFastaItem() throws IOException {
    String line;
    
    while ((line = reader.readLine()) != null) {
      if (line.charAt(0) == '>') {
        FastaItem newItem = createNewItem(line);
        // close & return previous item, if any
        if (isFirst) {
          isFirst = false;
          lastFastaItem = newItem;
        } else {
          lastFastaItem.closeSeq();
          FastaItem tmpItem = lastFastaItem;
          lastFastaItem = newItem;
          return tmpItem;
        }
      } else if (line.length() > 3) {
        String aminoAcid = line.trim().split("\t")[0];
        float prob = Float.parseFloat(line.trim().split("\t")[1]);
        lastFastaItem.addAminoAcidWithProbability(aminoAcid, prob); 
      }      
    }
    if (!wasLast) {
      lastFastaItem.closeSeq();
      wasLast = true;
      return lastFastaItem;
    } else {
      return null;
    }
  }
  
}
