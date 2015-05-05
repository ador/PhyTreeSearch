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

  String fastaString = "> Alma-NO|blab\n" + 
  		"TRHHHHTTTRRTDDDHHHHR\n" + 
  		"> Korte-3|bla\n" + 
  		"TRHHHHTTTRRTDDHDHDHD\n" + 
  		"> Szilva-NO|1\n" + 
  		"TRHHHHTTTRRTDDDHHHHRAAAA\n" + 
  		"> Barack-1|2\n" + 
  		"HDTRHHHHTTTRRTDRAAAA\n" + 
  		"> Barack-2|3\n" + 
  		"TAARHDHHHTAAATTRRTDDHDAA\n" + 
  		"> Korte-2|3\n" + 
  		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
  		"> Barack-NO|1\n" + 
  		"TRHAAHTTTRRTDDAAHHHRAAAA\n" + 
  		"> Alma-2|0\n" + 
  		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
  		"> Alma-1|0|b\n" + 
  		"AATRHHHAHAHATTTRRTDDHDHAACCDC\n" + 
  		"> Alma-3|3|a\n" + 
  		"AATRHDHHAHAHDATTTRRTDDHDHAACCDC";
  InputStream fasta;
  FastaReader fastaReader;
  String treeString = "(((Korte-3|bla,Korte-2|3),((Alma-NO|blab,Alma-1|0|b),(Alma-2|0,Alma-3|3|a))),((Barack-1|2,(Barack-NO|1,Barack-2|3)),Szilva-NO|1));";
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
    TreeNode tnAlma1 = modifiedTree.getNodeByName("Alma-1|0|b");
    String almaSeq1 = tnAlma1.getSeqString();
    assertEquals("AATRHHHAHAHATTTRRTDDHDHAACCDC", almaSeq1);

    TreeNode tnSzilva0 = modifiedTree.getNodeByName("Szilva-NO|1");
    String szilvaSeq0 = tnSzilva0.getSeqString();
    assertEquals("TRHHHHTTTRRTDDDHHHHRAAAA", szilvaSeq0);
    
  }


}
