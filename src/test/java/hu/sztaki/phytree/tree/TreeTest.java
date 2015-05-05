package hu.sztaki.phytree.tree;

import static org.junit.Assert.*;
import hu.sztaki.phytree.FastaItem;
import hu.sztaki.phytree.TreeParser;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

public class TreeTest {

  @Test
  public void testOneNodePrint() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("A;").append("\n");

    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(1, n);
    
    String drawnTree = t.drawTreeString(false, false);
    
    // assembling expected value
    StringBuilder sb = new StringBuilder();
    sb.append("|").append("\n");
    sb.append(" -- A").append("\n");
    
    
    assertEquals(sb.toString(), drawnTree);
  }
  
  @Test
  public void testLargePrint() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("(Alma:0.1,B:0.2,(C:0.3,D:0.4)Elefant:2.5e-04)F;").append("\n");

    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(4, n);
    
    String drawnTree = t.drawTreeString(false, false);
    // assembling expected value
    StringBuilder sb = new StringBuilder();
    sb.append("|").append("\n");
    sb.append(" -- F --").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- Alma").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- B").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- Elefant --").append("\n");
    sb.append("                |").append("\n");
    sb.append("                 -- C").append("\n");
    sb.append("                |").append("\n");
    sb.append("                 -- D").append("\n");

    assertEquals(sb.toString(), drawnTree);
  }

  @Test
  public void testLargePrintWithSeq() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("(Alma:0.1,B:0.2,(C:0.3,D:0.4)Elefant:0.5)F;").append("\n");

    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(4, n);
    
    // add a sequence (fastaitem) to node C (a leaf)
    TreeNode cNode = t.getNodeByName("C");
    assertNotNull(cNode);
    FastaItem seq = new FastaItem(">sp|C|ize", "C", "ize");
    seq.addSeqRow("TTTDDAA");
    cNode.setSequence(seq);
    
    String drawnTree = t.drawTreeString(true, false);
    System.out.println("TREE: " + drawnTree);
    // assembling expected value
    StringBuilder sb = new StringBuilder();
    sb.append("|").append("\n");
    sb.append(" -- F --").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- Alma").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- B").append("\n");
    sb.append("        |").append("\n");
    sb.append("         -- Elefant --").append("\n");
    sb.append("                |").append("\n");
    sb.append("                 -- C (TTTDDAA)").append("\n");
    sb.append("                |").append("\n");
    sb.append("                 -- D").append("\n");

    assertEquals(sb.toString(), drawnTree);
  }

  @Test
  public void testDepthHeight() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("(Alma:0.1,B:0.2,(C:0.3,D:0.4)Elefant:0.5)F;").append("\n");
    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();    
    assertEquals(4, n);
        
    int height = t.getRoot().getSubTreeHeight();
    int depth = t.getDepth(); // maximum distance from the root
    assertEquals (height, depth);
  }

  @Test
  public void testNewick() {
    StringBuilder buffer = new StringBuilder();
    String expected = "(Alma:0.1,B:0.2,(C:0.3,D:0.4)Elefant:0.5)F;";
    buffer.append(expected).append("\n");
    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree tree = parser.tokenize();
    System.out.println(expected);
    System.out.println(tree.getNewick(true));
    assertEquals(expected, tree.getNewick(true));
  }

}
