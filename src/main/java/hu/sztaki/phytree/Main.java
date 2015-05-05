package hu.sztaki.phytree;

import hu.sztaki.fileops.FileNumber;
import hu.sztaki.phytree.io.DisorderPredictionsReader;
import hu.sztaki.phytree.io.FastaReader;
import hu.sztaki.phytree.io.FastaWriter;
import hu.sztaki.phytree.tree.SeqsToTreeNodes;
import hu.sztaki.phytree.tree.Tree;
import hu.sztaki.phytree.tree.TreeNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Main {

  FastaReader fastaReader;
  TreeParser treeParser;
  Tree tree;
  Configuration config;
  boolean renameTreeSeqs = false;
  boolean treeColors = true;
  boolean intoFile = false;
  String pattern;
  int minDisorderedPatterns = 1;
  boolean fastaOutputForBlast = false;
  boolean disorderedPredictions = false;
  InputStream disorderedInfoStream;
  double minDistInTree = 0.05;


  private boolean checkRequiredConfigPropertiesExist() {
    if (!config.containsKey("treeFilesDir")) {
      System.out
          .println("Please specify a directory with .nwk tree files " +
              "with the \"treeFilesDir\" property!");
      return false;
    }
    if (!config.containsKey("fastaFilesDir")) {
      System.out
          .println("Please specify a directory with .fasta files " +
              "with the \"fastaFilesDir\" property!");    
      return false;
    }
    return true;
  }
  
  private void readConfig(String configFileName) {
    try {
      config = new PropertiesConfiguration(configFileName);
      if (!checkRequiredConfigPropertiesExist()) {
        System.exit(3);
      }
      if (!config.containsKey("seqPattern")) {
        System.out
            .println("Please specify a pattern to search for in sequences " +
                "with the \"seqPattern\" property!");
        return;
      }

      if (config.containsKey("renameSeqs")) {
        renameTreeSeqs = true;
      }

      if (config.containsKey("treeColors")) {
        if (config.getString("treeColors").toLowerCase().equals("no")) {
          treeColors = false;
          System.out.println("Output tree coloring is turned OFF");
        }
      }

      if (config.containsKey("intoFile")
          && !config.getString("intoFile").toLowerCase().equals("no")) {
        intoFile = true;
      }
      if (config.containsKey("disorderedPredictions")) {
        disorderedPredictions = true;
        disorderedInfoStream = new FileInputStream(new File(
            config.getString("disorderedPredictions")));
      }
      if (config.containsKey("fastaOutputForBlast")
          && !config.getString("fastaOutputForBlast").toLowerCase().equals("no")) {
        fastaOutputForBlast = true;
      }
      if (config.containsKey("minDistInTreeForBlast")) {
        minDistInTree = config.getDouble("minDistInTreeForBlast");
      }
    } catch (ConfigurationException | FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void readFasta(String fastaFileName) {
    try {
      InputStream fasta;
      fasta = new FileInputStream(new File(fastaFileName));
      fastaReader = new FastaReader(fasta);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void readTree(String newickFileName) {
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(newickFileName));
      treeParser = new TreeParser(br);
      tree = treeParser.tokenize();
      if (renameTreeSeqs) {
        System.out.println("Renaming leaves..."); 
        tree.getRoot().renameFromLongToSimple();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void outputResultSubTrees(TreeNode result, int number, int counter,
      SubTreeSearch ts) throws UnsupportedEncodingException, IOException {
    String lineSep = "------------------";
    if (intoFile) {
      String subtree = "(" + result.getNewickSubtree(treeColors) + ");";
      OutputStream outputTree = new FileOutputStream("sub" + number + 
          "tree" + counter + ".nwk");
      outputTree.write(subtree.getBytes("UTF-8"));
      outputTree.flush();
      outputTree.close();
      FastaWriter fastaWriter = new FastaWriter(new FileOutputStream("sub" +
          number + "tree" + counter + ".fasta"));

      List<FastaItem> fastaResult = ts.getFastaResult(result);
      fastaWriter.writeOrderedFastaList(fastaResult , config.getString("seqPattern"));
      
      OutputStream outputOrFile = new FileOutputStream(
          "sub" + number + "tree" + counter + ".txt");
      outputOrFile.write((ts.getOrStringResult(result, config.getString("seqPattern"))+ "\n").getBytes("UTF-8"));
      outputOrFile.flush();
      outputOrFile.close();
      
      System.out.println("sub" + number + "tree" + counter + ".nwk");
      System.out.println(lineSep);
    } else {
      System.out.println("(" + result.getNewickSubtree(treeColors) + ");");
      System.out.println(lineSep);
    }
    
    if (fastaOutputForBlast) {
      OutputStream outputBlastFile = new FileOutputStream(
          "sub" + number + "tree" + counter + ".bfasta");
      outputBlastFile.write(result.getBlastFastaString(config.getString("seqPattern"),
          minDistInTree).getBytes("UTF-8"));
      outputBlastFile.flush();
      outputBlastFile.close();
    } 
  }
  
  private void doSearchSubtrees(String treeFileName) {
    SeqsToTreeNodes sqtn = new SeqsToTreeNodes();
    sqtn.setTree(tree);
    List<FastaItem> fastaItemList = new ArrayList<FastaItem>();
    try {
      FastaItem fastaItem = fastaReader.getNextFastaItem();

      while (fastaItem != null) {
        fastaItemList.add(fastaItem);
        fastaItem = fastaReader.getNextFastaItem();
      }
      sqtn.setFastaItems(fastaItemList);
      tree = sqtn.appendSeqsToNodes();

      if (disorderedPredictions) {
        List<FastaItem> disorderedFastaItemList = new ArrayList<FastaItem>();
        DisorderPredictionsReader disorderReader =
            new DisorderPredictionsReader(disorderedInfoStream);
        FastaItem disordFastaItem = disorderReader.getNextFastaItem();
        while (disordFastaItem != null) {
          disorderedFastaItemList.add(disordFastaItem);
          disordFastaItem = disorderReader.getNextFastaItem();
        }
        SeqsToTreeNodes sqtn2 = new SeqsToTreeNodes();
        sqtn2.setTree(tree);
        sqtn2.setFastaItems(disorderedFastaItemList);
        // replace FastaItems of leaves that have disorder predictions
        System.out.println("Appending disorder info to tree...");
        tree = sqtn2.appendSeqsToNodes();
      }

      SubTreeSearch ts = new SubTreeSearch();
      ts.setConfig(config);
      List<TreeNode> results = ts.findSubtrees(tree);
      System.out.println("Number of result subtrees:" + results.size() + "\n");
      int counter = 0;

      FileNumber fileN = new FileNumber(treeFileName);
      int number = fileN.getNumber();
      for (TreeNode res : results) {
        outputResultSubTrees(res, number, counter, ts);
        counter++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void searchSubtrees() {
    // TODO
    String treeFileName;
    String fastaFileName;
    
    //for (all file pairs) {
      readTree(treeFileName);
      readFasta(fastaFileName);
      doSearchSubtrees(treeFileName);
    //}
  }
  
  public static void main(String[] args) {
    if (args.length == 1) {
      Main m = new Main();
      m.readConfig(args[0]);
      m.searchSubtrees();
    } else {
      System.out.println("Expecting 1 arguments: propertiesFile");
      return;
    }
  }
}
