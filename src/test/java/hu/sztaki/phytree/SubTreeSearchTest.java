package hu.sztaki.phytree;

import static org.junit.Assert.*;
import hu.sztaki.phytree.io.DisorderPredictionsReader;
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

  String fastaString = ">sp|Alma-NO|0\n" 
      + "TRHHHHTTTRRTDDDHHHHR\n"
      + ">sp|Korte-3|0\n"
      + "TRHHHHTTTRRTDDHDHDHD\n" 
      + ">sp|Szilva-NO|3|ggffsg\n"
      + "TRHHHHTTTRRTDDDHHHHRAAAA\n" 
      + ">sp|Barack-1|2\n" 
      + "HDTRHHHHTTTRRTDRAAAA\n"
      + ">sp|Barack-2|1\n" 
      + "TAARHDHHHTAAATTRRTDDHDAA\n"
      + ">sp|Korte-2|2\n"
      + "TRHDHHHTTTRRTDDHDHAACCAAAA\n" 
      + ">sp|Barack-NO|0\n"
      + "TRHAAHTTTRRTDDAAHHHRAAAA\n"
      + ">sp|Alma-2|1\n"
      + "TRHDHHHTTTRRTDDHDHAACCAAAA\n"
      + ">sp|Alma-1|0\n"
      + "AATRHHHAHAHATTTRRTDDHDHAACCDC\n"
      + ">sp|Alma-3|2|fg\n"
      + "AATRHDHHAHAHDATTTRRTDDHDHAACCDC";
  InputStream fasta;
  FastaReader fastaReader;

  String treeString = "(((Korte-3_0:0.1,Korte-2_2:0.15):1.2,((Alma-NO_0:0.4,Alma-1_0:0.3):0.3,(Alma-2_1:0.02,Alma-3_2:0.03):0.1):0.03):0.8,((Barack-1_2:0.1,(Barack-NO_0:0.06,Barack-2_1:0.04):0.5):0.7,Szilva-NO_3):0.9);";
  TreeParser treeParser;
  Tree tree;
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
      ">sp|Korte-3|0\n" +
      "T\t0.794538\n" + 
      "R\t0.815020\n" + 
      "H\t0.815020\n" + 
      "D\t0.680474\n" + 
      "H\t0.752324\n" + 
      "H\t0.752124\n" + 
      "H\t0.752894\n" + 
      "T\t0.680004\n" + 
      "T\t0.660474\n" + 
      "T\t0.774582\n" + 
      "R\t0.794538\n" + 
      "R\t0.815022\n" + 
      "T\t0.815026\n" + 
      "D\t0.815020\n" + 
      "D\t0.815034\n" + 
      "H\t0.650970\n" +
      "D\t0.815020\n" + 
      "H\t0.815034\n" + 
      "A\t0.650970\n" +
      "A\t0.815020\n" + 
      "C\t0.815034\n" + 
      "C\t0.650970\n" +
      "A\t0.881940\n" + 
      "A\t0.650970\n" +
      "A\t0.881940\n" + 
      "A\t0.818075\n" + 
      ">sp|Korte-2|2\n" +
      "T\t0.794538\n" + 
      "R\t0.815020\n" + 
      "H\t0.815020\n" + 
      "D\t0.680474\n" + 
      "H\t0.752324\n" + 
      "H\t0.752124\n" + 
      "H\t0.752894\n" + 
      "T\t0.680004\n" + 
      "T\t0.660474\n" + 
      "T\t0.774582\n" + 
      "R\t0.794538\n" + 
      "R\t0.815022\n" + 
      "T\t0.815026\n" + 
      "D\t0.815020\n" + 
      "D\t0.815034\n" + 
      "H\t0.650970\n" +
      "D\t0.815020\n" + 
      "H\t0.815034\n" + 
      "A\t0.650970\n" +
      "A\t0.815020\n" + 
      "C\t0.815034\n" + 
      "C\t0.650970\n" +
      "A\t0.881940\n" + 
      "A\t0.650970\n" +
      "A\t0.881940\n" + 
      "A\t0.818075\n"; 


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

  @Test
  public void testOrStringOutput() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minPatternPercent", 100);
    conf.addProperty("seqPattern", "HD");
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);

    assertEquals(2, results.size());
    
    String result1 = ts.getOrStringResult(results.get(0), "HD");
    assertNotNull(result1);
    
    assertEquals("Korte-3 OR Korte-2", result1);

    String result2 = ts.getOrStringResult(results.get(1), null);
    assertNotNull(result2);
    
    assertEquals("Alma-2 OR Alma-3", result2);
  }

  private Tree addDisorderProbs() {
    InputStream disorderStream = new ByteArrayInputStream(
        disorderString.getBytes());
    
    List<FastaItem> disordFastaItemList = new ArrayList<FastaItem>();
    DisorderPredictionsReader dReader =
        new DisorderPredictionsReader(disorderStream);
    try {
      FastaItem fastaItem = dReader.getNextFastaItem();
      while (fastaItem != null) {
        disordFastaItemList.add(fastaItem);
        fastaItem = dReader.getNextFastaItem();
      }
      assertEquals(4, disordFastaItemList.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    SeqsToTreeNodes sqtn = new SeqsToTreeNodes();
    sqtn.setTree(tree);
    sqtn.setFastaItems(disordFastaItemList);
    sqtn.appendSeqsToNodes();
    return tree; 
  }

  @Test
  public void testDisorderProbs1() {
    tree = addDisorderProbs();
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 20);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("disorderedPredictions", "bla"); //
    conf.addProperty("minDisorderedPatterns", 4); //
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(0, results.size());    
  }

  @Test
  public void testDisorderProbs2() {
    tree = addDisorderProbs();
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 1);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 90);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("disorderedPredictions", "bla"); //
    conf.addProperty("minDisorderedPatterns", 2); //
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(1, results.size());
    TreeNode tn = results.get(0);
    int leaves = tn.getLeafNum();
    assertEquals(2, leaves);
  }
  
  @Test
  public void testGetBlastFastaString1() {
    tree = addDisorderProbs();
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 60);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("disorderedPredictions", "bla"); //
    conf.addProperty("minDisorderedPatterns", 3); //
    conf.addProperty("disorderedThreshold", 0.5); //
    conf.addProperty("fastaOutputForBlast", "yes"); //
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(1, results.size());
    TreeNode tn = results.get(0);
    //System.out.println(tn.drawSubtreeString(tn, 0, true, true));
    double minDistInTree = 0.001; 
    String blastFasta = tn.getBlastFastaString("HD", minDistInTree);
    // NOTE: the sequences are overwritten by the "disordered" predictions
    // but in real life the sequences should and will be the same
    String expectedBlastFasta =  ">sp|Korte-3|0\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
    		">sp|Korte-2|2\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
    		">sp|Alma-1|0\n" + 
    		"AATRHHHAHAHATTTRRTDDHDHAACCDC\n" + 
    		">sp|Alma-2|1\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
    		">sp|Alma-3|2|fg\n" + 
    		"AATRHDHHAHAHDATTTRRTDDHDHAACCDC\n" + 
    		">sp|Barack-1|2\n" + 
    		"HDTRHHHHTTTRRTDRAAAAC\n" + 
    		">sp|Barack-2|1\n" + 
    		"TAARHDHHHTAAATTRRTDDHDAA\n";
    assertEquals(expectedBlastFasta, blastFasta);
  }

  @Test
  public void testGetBlastFastaString2() {
    tree = addDisorderProbs();
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 60);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("disorderedPredictions", "bla");
    conf.addProperty("minDisorderedPatterns", 2);
    conf.addProperty("disorderedThreshold", 0.5);
    conf.addProperty("fastaOutputForBlast", "yes");
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(1, results.size());
    TreeNode tn = results.get(0);
    double minDistInTree = 0.3; 
    String blastFasta = tn.getBlastFastaString("HD", minDistInTree);
    String expectedBlastFasta =  ">sp|Korte-3|0\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
    		">sp|Alma-1|0\n" + 
    		"AATRHHHAHAHATTTRRTDDHDHAACCDC\n" + 
    		">sp|Alma-2|1\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n" + 
    		">sp|Barack-1|2\n" + 
    		"HDTRHHHHTTTRRTDRAAAAC\n";
    assertEquals(expectedBlastFasta, blastFasta);
  }

  @Test
  public void testGetBlastFastaString2NoDisorder() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 90);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("fastaOutputForBlast", "yes");
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(2, results.size());
    TreeNode tn1 = results.get(0);
    TreeNode tn2 = results.get(1);
    System.out.println(tn1.drawSubtreeString(tn1, 0, true, true));
    System.out.println(tn2.drawSubtreeString(tn2, 0, true, true));
    double minDistInTree = 0.3; 
    String blastFasta1 = tn1.getBlastFastaString("HD", minDistInTree);
    String expected1 = ">sp|Korte-3|0\n" + 
    		"TRHHHHTTTRRTDDHDHDHD\n";
    assertEquals(expected1, blastFasta1);
    
    String blastFasta2 = tn2.getBlastFastaString("HD", minDistInTree);
    String expected2 = ">sp|Alma-2|1\n" + 
    		"TRHDHHHTTTRRTDDHDHAACCAAAA\n"; 

    assertEquals(expected2, blastFasta2);
  }


  @Test
  public void testGetBlastFastaString3() {
    tree = addDisorderProbs();
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 2);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 60);
    conf.addProperty("seqPattern", "HD");
    conf.addProperty("disorderedPredictions", "bla"); //
    conf.addProperty("minDisorderedPatterns", 2); //
    conf.addProperty("disorderedThreshold", 0.5); //
    conf.addProperty("fastaOutputForBlast", "yes"); //
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(1, results.size());
    TreeNode tn = results.get(0);
    double minDistInTree = 10.0; 
    String blastFasta = tn.getBlastFastaString("HD", minDistInTree);
    String expectedBlastFasta =  ">sp|Korte-3|0\n"
        + "TRHDHHHTTTRRTDDHDHAACCAAAA\n"; 
    assertEquals(expectedBlastFasta, blastFasta);
  }


}
