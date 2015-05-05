package hu.sztaki.phytree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import hu.sztaki.fileops.FileNumber;
import hu.sztaki.phytree.io.DisorderPredictionsReader;
import hu.sztaki.phytree.io.FastaReader;
import hu.sztaki.phytree.io.FastaWriter;
import hu.sztaki.phytree.tree.SeqsToTreeNodes;
import hu.sztaki.phytree.tree.Tree;
import hu.sztaki.phytree.tree.TreeNode;

public class TreeAndFastaFilesMatcher {
  
  private String pathOfFastaDir;
  private String pathOfTreeDir;
  
  public TreeAndFastaFilesMatcher(String treeDir, String fastaDir) {
    pathOfTreeDir = treeDir;
    pathOfFastaDir = fastaDir;
  }
  
  // by the last number of the filename
  // note: this will be slow for many files! O(n^2)
  private File matchFastaFileToTree(File treeFile, File[] fastaFiles) {
    String treeFileName = treeFile.getPath();
    FileNumber fileNumParser = new FileNumber(treeFileName);
    int tNumber = fileNumParser.getNumber();
    for (File f: fastaFiles) {
      String fastaName = f.getPath();
      FileNumber fileNumParserFasta = new FileNumber(fastaName);
      int fNumber = fileNumParserFasta.getNumber();
      if (tNumber == fNumber) {
        return f;
      }
    }
    return null;
  }

  private Tree readTree(File newickFile) {
    BufferedReader br;
    try {
      String path = newickFile.getPath();
      FileNumber fileNumParser = new FileNumber(path);
      int tNumber = fileNumParser.getNumber();
      br = new BufferedReader(new FileReader(newickFile));
      TreeParser treeParser = new TreeParser(br);
      Tree tree = treeParser.tokenize();
      /*if (renameTreeSeqs) {
        System.out.println("Renaming leaves..."); 
        tree.getRoot().renameFromLongToSimple();
      }*/
      tree.setKey(tNumber);
      return tree;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private File[] getFastaFilesFromDir(File dir) {
    return dir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".fasta") || 
                    name.toLowerCase().endsWith(".fas") ||
                    name.toLowerCase().endsWith(".fa"));
        }
    });
  }

  private File[] getNewickFilesFromDir(File dir) {
    return dir.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".newick") || 
                    name.toLowerCase().endsWith(".nwk"));
        }
    });
  }
  
  private List<FastaItem> readFastaItems(File fastaFile) {
    try {
      InputStream fastaIs = new FileInputStream(fastaFile);
      FastaReader fastaReader = new FastaReader(fastaIs);
      List<FastaItem> fastaItemList = new ArrayList<FastaItem>();
      FastaItem fastaItem = fastaReader.getNextFastaItem();
      while (fastaItem != null) {
        fastaItemList.add(fastaItem);
        fastaItem = fastaReader.getNextFastaItem();
      }
      return fastaItemList;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public List<Tree> getTreesWithSequences() {
    List<Tree> ret = new ArrayList<Tree>();
    File[] fastaFiles = getFastaFilesFromDir(new File(pathOfFastaDir));
    File[] treeFiles = getNewickFilesFromDir(new File(pathOfTreeDir));
    for (File treeFile: treeFiles) {
      Tree tree = readTree(treeFile);
      File fastaFileForTree = matchFastaFileToTree(treeFile, fastaFiles);
      List<FastaItem> fastaItemList = readFastaItems(fastaFileForTree);
      SeqsToTreeNodes sqtn = new SeqsToTreeNodes();
      sqtn.setTree(tree);
      sqtn.setFastaItems(fastaItemList);
      tree = sqtn.appendSeqsToNodes();
      ret.add(tree);
    }
    return ret;
  }

}