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

public class BlastOutputTest {

  String treeString = "(((Q10901_7:-0.0,Q25605_7:-0.0):4.88688," +
  		"(((B8ACH9_5:-0.0,Q5N7A7_5:-0.0):2.305,O64668_5:2.305)" +
  		":2.125,(P33897_3:3.9875,(Q9W5D4_5:2.11,Q640S1_1:2.11)" +
  		":1.8775):0.4425):0.456875):0.0565624);";
  String fastaString = ">sp|O64668|5|181|271|1|1\n" + 
  		"FILVALALYDLVAVLAPGGPLKLLVELASSRDEELPAMVYEARPTVSSGNQRRNRGSSLRALVGGGGVSDSGSVELQAVRNHDVNQLGREN\n" + 
  		">sp|Q10901|7|400|490|1|1\n" + 
  		"IGAASVPSAGLVTMLLVLTAVGLPVKDVSLIVAVDWLLDRIRTSINVLGDAMGAGIVYHYSKADLDAHDRLAATTRSHSIAMNDEKRQLAV\n" + 
  		">sp|Q25605|7|389|479|1|1\n" + 
  		"IGAASVPSAGLVTMLLVLTAVGLPVKDVSLIVAVDWLLDRIRTSINVLGDAMGAGIVYHYSKADLDAHDRLAATTRSHSIAMNDEKRQLAV\n" + 
  		">sp|Q640S1|1|35|101|1|1\n" + 
  		"LKLVTVLGAGLLCGTALAVIVPEGVHALYEEVLEAKHHDMGDIHKAKDAETGAEISAAHEHDHSNLH\n" + 
  		">sp|B8ACH9|5|222|312|0|0\n" + 
  		"HYSVDVVVAWYTVNLVVFFIDNKLPEMPDRTNGSSLLPVTAKDKDGRTKEELHKLEKDCKMKEEFHKLLNGNTVDSTDRRQRVQMNGKHGE\n" + 
  		">sp|P33897|3|333|423|0|0\n" + 
  		"FLMKYVWSASGLLMVAVPIITATGYSESDAEAVKKAALEKKEEELVSERTEAFTIARNLLTAAADAIERIMSSYKEVTELAGYTARVHEMF\n" + 
  		">sp|Q5N7A7|5|222|312|0|0\n" + 
  		"HYSVDVVVAWYTVNLVVFFIDNKLPEMPDRTNGSSLLPVTAKDKDGRTKEELHKLEKDCKMKEEFHKLLNGNTVDSTDRRQRVQMNGKHGE\n" + 
  		">sp|Q9W5D4|5|268|358|0|0\n" + 
  		"ITLIVWPVLLYILFFYIHLSVLNRSGNGDGFYSSAFQSRLIGNSLYNASMPRDVAYGSLVTIKNHKTGGGYLHSHHHLYPKGSGARQQQVT";

  
  TreeParser treeParser;
  Tree tree;
  InputStream fasta;
  FastaReader fastaReader;
  
  
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
      assertEquals(8, fastaItemList.size());
    } catch (IOException e) {
      e.printStackTrace();
    }
    sqtn.setFastaItems(fastaItemList);
    tree = sqtn.appendSeqsToNodes();
  }
  
  @Test
  public void testGetBlastFastaString() {
    Configuration conf = new PropertiesConfiguration();
    conf.addProperty("minHeightNum", 1);
    conf.addProperty("minLeafNum", 2);
    conf.addProperty("minPatternPercent", 45);
    conf.addProperty("seqPattern", "HD");
    
    SubTreeSearch ts = new SubTreeSearch();
    ts.setConfig(conf);
    List<TreeNode> results = ts.findSubtrees(tree);
    assertEquals(1, results.size());
    TreeNode tn = results.get(0);
    //System.out.println(tn.drawSubtreeString(tn, 0, true, true));
    double minDistInTree = 0.005;
    String blastFasta = tn.getBlastFastaString("HD", minDistInTree);
    String expectedBlastFasta =  ">sp|Q10901|7|400|490|1|1\n" + 
    		"IGAASVPSAGLVTMLLVLTAVGLPVKDVSLIVAVDWLLDRIRTSINVLGDAMGAGIVYHYSKADLDAHDRLAATTRSHSIAMNDEKRQLAV\n" + 
    		">sp|O64668|5|181|271|1|1\n" + 
    		"FILVALALYDLVAVLAPGGPLKLLVELASSRDEELPAMVYEARPTVSSGNQRRNRGSSLRALVGGGGVSDSGSVELQAVRNHDVNQLGREN\n" + 
    		">sp|Q640S1|1|35|101|1|1\n" + 
    		"LKLVTVLGAGLLCGTALAVIVPEGVHALYEEVLEAKHHDMGDIHKAKDAETGAEISAAHEHDHSNLH\n"; 
    		
    assertEquals(expectedBlastFasta, blastFasta);
  }
}
