package hu.sztaki.phytree.tree;

import hu.sztaki.phytree.FastaItem;

import java.util.List;

public class SeqsToTreeNodes {

  Tree tree;
  List<FastaItem> fastaItems;
  
  public void setTree(Tree t) {
    tree = t;
  }

  public void setFastaItems(List<FastaItem> fastaItemList) {
    fastaItems = fastaItemList;
  }

  public Tree appendSeqsToNodes() {
    for (FastaItem fi : fastaItems) {
      String name = fi.getAcNum();
      String fragmentNum = fi.getFragId();
      String header = fi.getHeaderRow();      
      TreeNode tn = tree.getNodeByName(header.substring(1).trim());
      if (tn != null) {
        tn.setSequence(fi);
      } else {
        System.err.println("Warning : no node found for this sequence: " + fi.getHeaderRow());
      }
    }
    return tree;
  }

}
