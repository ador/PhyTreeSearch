package hu.sztaki.phytree.io;

import static org.junit.Assert.*;
import hu.sztaki.phytree.FastaItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class DisorderReaderTest {

  String disorderString =">sp|Alma-NO|0\n" + 
      "T\t0.290085\n" + 
      "R\t0.291019\n" + 
      "H\t0.317524\n" + 
      "H\t0.350336\n" + 
      "H\t0.345120\n" + 
      "H\t0.333700\n" + 
      "T\t0.332146\n" + 
      "T\t0.328631\n" + 
      "T\t0.322844\n" + 
      "R\t0.327885\n" + 
      "R\t0.329309\n" + 
      "T\t0.343714\n" + 
      "D\t0.350632\n" + 
      "D\t0.351886\n" + 
      "D\t0.356743\n" + 
      "H\t0.383014\n" + 
      "H\t0.389121\n" + 
      "H\t0.389136\n" + 
      "H\t0.384773\n" + 
      "R\t0.403348\n" +
      ">sp|Barack-1|2\n" + 
      "H\t0.881940\n" + 
      "D\t0.818075\n" + 
      "T\t0.818063\n" + 
      "R\t0.818025\n" + 
      "H\t0.818065\n" + 
      "H\t0.794538\n" + 
      "H\t0.815020\n" + 
      "H\t0.815020\n" + 
      "T\t0.680474\n" + 
      "T\t0.752324\n" + 
      "T\t0.752124\n" + 
      "R\t0.752894\n" + 
      "R\t0.680004\n" + 
      "T\t0.660474\n" + 
      "D\t0.774582\n" + 
      "R\t0.794538\n" + 
      "A\t0.815022\n" + 
      "A\t0.815026\n" + 
      "A\t0.815020\n" + 
      "A\t0.815034\n" + 
      "C\t0.650970\n" +
      ">sp|Barack-0|2\n" + 
      "F\t0.881940\n" + 
      "D\t0.818075\n"; 

  
  private InputStream probs;

  @Before
  public void setUp() {
    probs = new ByteArrayInputStream(
        (disorderString).getBytes());
  }
 
  @Test
  public void testGetNextFastaItem() {
    DisorderPredictionsReader dReader = new DisorderPredictionsReader(probs);
    String headerRow1 = ">sp|Alma-NO|0";
    String sequence1 = "TRHHHHTTTRRTDDDHHHHR";
    String headerRow2 = ">sp|Barack-1|2";
    String sequence2 = "HDTRHHHHTTTRRTDRAAAAC";
    String headerRow3 = ">sp|Barack-0|2";
    String sequence3 = "FD";
    try {
      FastaItem fastaItem = dReader.getNextFastaItem();
      assertEquals("Alma-NO", fastaItem.getAcNum());
      assertEquals(headerRow1, fastaItem.getHeaderRow());
      assertEquals(sequence1, fastaItem.getSequenceString());
      
      FastaItem fastaItem2 = dReader.getNextFastaItem();
      assertEquals("Barack-1", fastaItem2.getAcNum());
      assertEquals(headerRow2, fastaItem2.getHeaderRow());
      assertEquals(sequence2, fastaItem2.getSequenceString());
      
      FastaItem fastaItem3 = dReader.getNextFastaItem();
      assertEquals("Barack-0", fastaItem3.getAcNum());
      assertEquals(headerRow3, fastaItem3.getHeaderRow());
      assertEquals(sequence3, fastaItem3.getSequenceString());
      
      FastaItem fastaItem4 = dReader.getNextFastaItem();
      assertNull(fastaItem4);
      FastaItem fastaItem5 = dReader.getNextFastaItem();
      assertNull(fastaItem5);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
