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
      //TreeNode tn = tree.getNodeByName(name);
      //System.out.println("searching for: " + name + "_" + fragmentNum);
      TreeNode tn = tree.getNodeByName(name + "_" + fragmentNum);
      //TreeNode tn = tree.getNodeByName(name.substring(1));
      if (tn != null) {
        tn.setSequence(fi);
      } else {
        System.err.println("Warning : no node found for this sequence: " + fi.getHeaderRow());
      }
    }
    return tree;
  }

}
