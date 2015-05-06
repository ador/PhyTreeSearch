package hu.sztaki.phytree;

import hu.sztaki.fileops.FileNumber;
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

  Configuration config;
  boolean renameTreeSeqs = false;
  boolean treeColors = true;
  String outDirPath;
  String pattern;
  String treeDir;
  String fastaDir;


  private boolean checkRequiredConfigPropertiesExist() {
    if (!config.containsKey("treeFilesDir")) {
      System.out
          .println("Please specify a directory with .nwk tree files " +
              "with the \"treeFilesDir\" property!");
      return false;
    } else {
      this.treeDir = config.getString("treeFilesDir");
    }
    if (!config.containsKey("fastaFilesDir")) {
      System.out
          .println("Please specify a directory with .fasta files " +
              "with the \"fastaFilesDir\" property!");    
      return false;
    } else {
      this.fastaDir = config.getString("fastaFilesDir");
    }
    if (!config.containsKey("outputTreeFilesDir")) {
      System.out
          .println("Please specify output directory for results " +
              "with the \"outputTreeFilesDir\" property!");
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
      if (config.containsKey("treeColors")) {
        if (config.getString("treeColors").toLowerCase().equals("no")) {
          treeColors = false;
          System.out.println("Output tree coloring is turned OFF");
        }
      }
      outDirPath = config.getString("outputTreeFilesDir");
      
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  private void outputResultSubTrees(TreeNode result, int number, int counter,
    SubTreeSearch ts) throws UnsupportedEncodingException, IOException {
  
    File targetFile = new File(outDirPath); 
    targetFile.mkdirs();
    
    String subtree = "(" + result.getNewickSubtree(treeColors) + ");";
    String resultFileName = outDirPath + File.separator + "sub" + number + "tree" + counter;

    OutputStream outputTree = new FileOutputStream(resultFileName + ".nwk");
    outputTree.write(subtree.getBytes("UTF-8"));
    outputTree.flush();
    outputTree.close();
    FastaWriter fastaWriter = new FastaWriter(new FileOutputStream(resultFileName + ".fasta"));

    List<FastaItem> fastaResult = ts.getFastaResult(result);
    fastaWriter.writeOrderedFastaList(fastaResult , config.getString("seqPattern"));      
    System.out.println("Written: " + resultFileName + ".nwk and .fasta\n");
  }
  
  private int[] doSearchSubtrees(Tree tree) {
    int allNodeCnt = 0;
    int patternNodeCnt = 0;
    try {
      SubTreeSearch ts = new SubTreeSearch();
      ts.setConfig(config);
      List<TreeNode> results = ts.findSubtrees(tree);
      if (results.size() > 0) {
        int treeId = tree.getKey();
        System.out.println("Number of result subtrees for input tree :" + 
              treeId + " is " + results.size());
        int counter = 0;
        for (TreeNode res : results) {
          outputResultSubTrees(res, treeId, counter, ts);
          counter++;
          patternNodeCnt += res.getLeafNumWithPattern("HD");
          allNodeCnt += res.getLeafNum();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    int[] ret = {allNodeCnt, patternNodeCnt};
    return ret; 
  }
  
  private void searchSubtrees() {
    TreeAndFastaFilesMatcher filesMatcher = new TreeAndFastaFilesMatcher(treeDir, fastaDir);
    List<Tree> treeList = filesMatcher.getTreesWithSequences();
    int allNodes = 0;
    int patternNodes = 0;
    for (Tree tree : treeList) {
      int[] nums = doSearchSubtrees(tree);
      allNodes += nums[0];
      patternNodes += nums[1];
    }
    System.out.println("All nodes found in all subtrees: " + allNodes + " of which " +
        patternNodes + " contain the required pattern");
  }
  
  public static void main(String[] args) {
    if (args.length == 1) {
      Main m = new Main();
      m.readConfig(args[0]);
      m.searchSubtrees();
    } else {
      System.out.println("Expecting 1 arguments: propertiesFile");
      System.out.println("Found args: " + args.length);
      return;
    }
  }
}
