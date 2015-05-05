package hu.sztaki.phytree;

import static org.junit.Assert.*;
import hu.sztaki.phytree.io.FastaReader;
import hu.sztaki.phytree.tree.SeqsToTreeNodes;
import hu.sztaki.phytree.tree.Tree;
import hu.sztaki.phytree.tree.TreeNode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

public class SubTreeSearchTest {

  String fastaString = "> Alma-NO|0-10|b\n" 
      + "TRHHHHTTTRRTDDDHHHHR\n"
      + "> Korte-3|0-10|a\n"
      + "TRHHHHTTTRRTDDHDHDHD\n" 
      + "> Szilva-NO|3-20|a\n"
      + "TRHHHHTTTRRTDDDHHHHRAAAA\n" 
      + "> Barack-1|2-14|a\n" 
      + "HDTRHHHHTTTRRTDRAAAA\n"
      + "> Barack-2|1-19|b\n" 
      + "TAARHDHHHTAAATTRRTDDHDAA\n"
      + "> Korte-2|2-21|b\n"
      + "TRHDHHHTTTRRTDDHDHAACCAAAA\n" 
      + "> Barack-NO|0-30|c\n"
      + "TRHAAHTTTRRTDDAAHHHRAAAA\n"
      + "> Alma-2|3-16|a\n"
      + "TRHDHHHTTTRRTDDHDHAACCAAAA\n"
      + "> Alma-1|0-20|a\n"
      + "AATRHHHAHAHATTTRRTDDHDHAACCDC\n"
      + "> Alma-3|6-21|c\n"
      + "AATRHDHHAHAHDATTTRRTDDHDHAACCDC";
  InputStream fasta;
  FastaReader fastaReader;

  String treeString = "(((Korte-3|0-10|a:0.1,Korte-2|2-21|b:0.15):1.2,((Alma-NO|0-10|b:0.4,Alma-1|0-20|a:0.3):0.3,(Alma-2|3-16|a:0.02,Alma-3|6-21|c:0.03):0.1):0.03):0.8,((Barack-1|2-14|a:0.1,(Barack-NO|0-30|c:0.06,Barack-2|1-19|b:0.04):0.5):0.7,Szilva-NO|3-20|a):0.9);";
  TreeParser treeParser;
  Tree tree;

  @Before
  public void setUp() {
    fasta = new ByteArrayInputStream(fastaString.getBytes());
    fastaReader = new FastaReader(fasta);
    BufferedReader br = new BufferedReader(new StringReader(treeString));
    treeParser = new TreeParser(br);
    tree = treeParser.tokenize();
    SeqsToTreeNodes sqtn = new SeqsToTreeNodes();
    sqtn.setTree(tree);
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
    sqtn.setFastaItems(fastaItemList);
    tree = sqtn.appendSeqsToNodes();
  }

  @Test
  public void testSubtreeSearch1() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minLeafNum", 11);
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    // expecting no results because the test tree contains 10 leaves only
    assertEquals(0, results.size());
  }

  @Test
  public void testSubtreeSearch2() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minLeafNum", 8);
    conf.addProperty("minPatternPercent", 60);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    // expecting the whole tree
    assertEquals(1, results.size());
    TreeNode tsub = results.get(0);
    assertNotNull(tsub);
    assertEquals(tree.getRoot(), tsub);
  }

  @Test
  public void testSubtreeSearch3() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 100);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    // expecting two real subtrees with 2 leaves
    assertEquals(2, results.size());
    TreeNode tsub1 = results.get(0);
    assertNotNull(tsub1);
    assertEquals(2, tsub1.getLeafNum());

    TreeNode tsub2 = results.get(1);
    assertNotNull(tsub2);
    assertEquals(2, tsub2.getLeafNum());
  }

  @Test
  public void testSubtreeSearch4() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 3);
    conf.addProperty("minPatternPercent", 70);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    assertEquals(1, results.size());
    assertEquals(tree.getRoot(), results.get(0));
  }

  @Test
  public void testFastaOutput() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minPatternPercent", 100);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    // expecting two real subtrees with 2 leaves
    assertEquals(2, results.size());
    
    // get fasta items belonging to the leaves of a subtree of a node
    List<FastaItem> fastaResult1 = ts.getFastaResult(results.get(0));
    assertNotNull(fastaResult1);
    for (FastaItem fi : fastaResult1) {
      assertTrue(fi.getHeaderRow().contains("Korte"));
    }
    assertEquals(2, fastaResult1.size());

    List<FastaItem> fastaResult2 = ts.getFastaResult(results.get(1));
    assertNotNull(fastaResult2);
    for (FastaItem fi : fastaResult2) {
      assertTrue(fi.getHeaderRow().contains("Alma"));
    }
    assertEquals(2, fastaResult2.size());

  }
  
  @Test
  public void testFastaOutput2() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 3);
    conf.addProperty("minPatternPercent", 70);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    assertEquals(1, results.size());
    assertEquals(tree.getRoot(), results.get(0));
    
    List<FastaItem> fastaResult = ts.getFastaResult(results.get(0));
    assertNotNull(fastaResult);
    
    assertEquals(10, fastaResult.size());
  }

}
