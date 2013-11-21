package hu.sztaki.phytree.tree;

import static org.junit.Assert.*;
import hu.sztaki.phytree.FastaItem;
import hu.sztaki.phytree.TreeParser;
import hu.sztaki.phytree.io.FastaReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SeqsToTreeNodesTest {

  String fastaString = ">sp|Alma-NO|blabla\n" + 
  		"TRHHHHTTTRRTDDDHHHHR\n" + 
  		">sp|Korte-3|bla\n" + 
  		"TRHHHHTTTRRTDDHDHDHD\n" + 
  		">sp|Szilva-NO|1\n" + 
  		"TRHHHHTTTRRTDDDHHHHRAAAA\n" + 
  		">sp|Barack-1|2\n" + 
  		"HDTRHHHHTTTRRTDRAAAA\n" + 
  		">sp|Barack-2|3\n" + 
  		"TAARHDHHHTAAATTRRTDDHDAA\n" + 
  		">sp|Korte-2|3\n" + 
  		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
  		">sp|Barack-NO|1\n" + 
  		"TRHAAHTTTRRTDDAAHHHRAAAA\n" + 
  		">sp|Alma-2|0\n" + 
  		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
  		">sp|Alma-1|0|blabla\n" + 
  		"AATRHHHAHAHATTTRRTDDHDHAACCDC\n" + 
  		">sp|Alma-3|3|blabla\n" + 
  		"AATRHDHHAHAHDATTTRRTDDHDHAACCDC";
  InputStream fasta;
  FastaReader fastaReader;
  String treeString = "(((Korte-3_bla,Korte-2_3),((Alma-NO_blabla,Alma-1_0),(Alma-2_0,Alma-3_3))),((Barack-1_2,(Barack-NO_1,Barack-2_3)),Szilva-NO_1));";
  TreeParser treeParser;
  Tree tree;
  
  @Before
  public void setUp() {
    fasta = new ByteArrayInputStream(
        fastaString.getBytes());
    fastaReader = new FastaReader(fasta);
    BufferedReader br = new BufferedReader(new StringReader(treeString));
    treeParser = new TreeParser(br);
    tree = treeParser.tokenize();   
  }
  
  @Test
  public void test1() {
    SeqsToTreeNodes sttn = new SeqsToTreeNodes();
    sttn.setTree(tree);
    List<FastaItem> fastaItemList = new ArrayList<FastaItem>();
    try {
      FastaItem fastaItem = fastaReader.getNextFastaItem();
      while (fastaItem != null) {
        fastaItemList.add(fastaItem);
        fastaItem = fastaReader.getNextFastaItem();
      }
      assertEquals(10, fastaItemList.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    sttn.setFastaItems(fastaItemList);
    Tree modifiedTree = sttn.appendSeqsToNodes();
    assertNotNull(modifiedTree);
    // check some nodes
    TreeNode tnAlma1 = modifiedTree.getNodeByName("Alma-1_0");
    String almaSeq1 = tnAlma1.getSeqString();
    assertEquals("AATRHHHAHAHATTTRRTDDHDHAACCDC", almaSeq1);

    TreeNode tnSzilva0 = modifiedTree.getNodeByName("Szilva-NO_1");
    String szilvaSeq0 = tnSzilva0.getSeqString();
    assertEquals("TRHHHHTTTRRTDDDHHHHRAAAA", szilvaSeq0);
    
  }


}
