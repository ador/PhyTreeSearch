package hu.sztaki.phytree.io;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import hu.sztaki.phytree.FastaItem;

public class FastaWriterTest {

  private OutputStream os = new ByteArrayOutputStream();
  
  @Test
  public void testWriteFastaItem() {
    FastaWriter fastaWriter = new FastaWriter(os);
    FastaItem fastaItem = new FastaItem(">sp|Q23456|something|0|12|34", "Q23456", "0");
    fastaItem.addSeqRow("AAAA");
    fastaItem.addSeqRow("BBBB");
    fastaItem.addSeqRow("CCCC");
    try {
      fastaWriter.writeFastaItem(fastaItem);
      fastaWriter.closeOS();
      String expected = ">sp|Q23456|something|0|12|34\nAAAA\nBBBB\nCCCC\n";
      assertTrue(os.toString().equals(expected));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testWriteFastaItemList() {
    FastaWriter fastaWriter = new FastaWriter(os);
    FastaItem fastaItem1 = new FastaItem(">sp|Q23456|something1", "Q23456", "1");
    fastaItem1.addSeqRow("AAAA");
    fastaItem1.addSeqRow("BBBB");
    fastaItem1.addSeqRow("CCCC");
    FastaItem fastaItem2 = new FastaItem(">sp|QBCDEF|something2", "QBCDEF", "0");
    fastaItem2.addSeqRow("DDDD");
    fastaItem2.addSeqRow("EEEE");
    fastaItem2.addSeqRow("FFFF");
    List<FastaItem> fastaList = new ArrayList<FastaItem>();
    fastaList.add(fastaItem1);
    fastaList.add(fastaItem2);
    try {
      fastaWriter.writeFastaList(fastaList);
      fastaWriter.closeOS();
      String expected = ">sp|Q23456|something1\nAAAA\nBBBB\nCCCC\n"
          + ">sp|QBCDEF|something2\nDDDD\nEEEE\nFFFF\n";
      assertEquals(expected, os.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testWriteOrderedFastaItemSet() {
    FastaWriter fastaWriter = new FastaWriter(os);
    FastaItem fastaItem1 = new FastaItem(">sp|A23456|something1|0", "A23456", "1");
    fastaItem1.addSeqRow("AAAAHD");
    FastaItem fastaItem2 = new FastaItem(">sp|ZBCDEF|something2|2", "ZBCDEF", "0");
    fastaItem2.addSeqRow("EEEEEE");
    FastaItem fastaItem3 = new FastaItem(">sp|C23456|something1|3", "C23456", "1");
    fastaItem3.addSeqRow("HHHHDA");
    FastaItem fastaItem4 = new FastaItem(">sp|BACDEF|something2|0", "BACDEF", "0");
    fastaItem4.addSeqRow("DDDD");
    List<FastaItem> fastaList = new ArrayList<FastaItem>();
    fastaList.add(fastaItem1);
    fastaList.add(fastaItem2);
    fastaList.add(fastaItem3);
    fastaList.add(fastaItem4);
    try {
      fastaWriter.writeOrderedFastaList(fastaList, "HD");
      fastaWriter.closeOS();
      String expected = ">sp|A23456|something1|0|1\n" +
          "AAAAHD\n" +
          ">sp|C23456|something1|3|1\n" +
          "HHHHDA\n" +
          ">sp|BACDEF|something2|0|0\n" +
          "DDDD\n" +
          ">sp|ZBCDEF|something2|2|0\n" +
          "EEEEEE\n";
      assertEquals(expected, os.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
