package hu.sztaki.phytree.io;

import static org.junit.Assert.*;

import hu.sztaki.phytree.TreeParser;
import hu.sztaki.phytree.tree.Tree;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

public class TreeParserTest {

  @Test
  public void test1() {
    
    StringBuilder buffer = new StringBuilder();
    buffer.append("(A:0.1,B:0.2,(C:0.3,D:0.4)E:0.5)F;").append("\n");

    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(4, n);
  }

  @Test
  public void test2() {
    
    StringBuilder buffer = new StringBuilder();
    buffer.append("((raccoon, bear),((sea_lion,seal),((monkey,cat), weasel)),dog);").append("\n");

    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(8, n);
  }

  @Test
  public void test3() {
    
    StringBuilder buffer = new StringBuilder();
    buffer.append("((raccoon:19.19959,bear:6.80041):0.84600,((sea_lion:11.99700, ");
    buffer.append("seal:12.00300):7.52973,((monkey:100.85930,cat:47.14069):20.59201,");
    buffer.append(" weasel:18.87953):2.09460):3.87382,dog:25.46154);").append("\n");
    
    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    int n = t.getLeafCount();
    assertEquals(8, n);
  }

  @Test
  public void test4() {
    
    StringBuilder buffer = new StringBuilder();
    buffer.append("(((Korte-3,Korte-2),((Alma-NO,Alma-1),(Alma-2,Alma-3))),");
    buffer.append("((Barack1,(Barack-NO,Barack-2)),Szilva-NO));").append("\n");
    
    BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
    TreeParser parser = new TreeParser(br);
    Tree t = parser.tokenize();
    //System.out.println(t.drawTreeString(false));
    int n = t.getLeafCount();
    assertEquals(10, n);
  }
  
}


