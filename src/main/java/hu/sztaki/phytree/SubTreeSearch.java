package hu.sztaki.phytree;


import java.util.ArrayList;
import java.util.List;

import hu.sztaki.phytree.tree.Tree;
import hu.sztaki.phytree.tree.TreeNode;

import org.apache.commons.configuration.Configuration;

public class SubTreeSearch {
  Configuration conf;
  String pattern;
  static final int DEFAULT_MIN_PATTERN_PERCENT = 50;
  
  public void setConfig(Configuration conf) {
    this.conf = conf;
  }

  public List<TreeNode> findSubtrees(Tree tree) {
    ArrayList<TreeNode> ret = new ArrayList<TreeNode>();
    // simple case 1: not enough leaves
    if (!checkNumOfLeaves(tree.getRoot())) {
      System.out.println("no results because not enough leaves");
      return ret;
    }
    // simple case 2: not enough tree height
    if (!checkSubTreeHeight(tree.getRoot())) {
      System.out.println("no results because of height");
      return ret;
    }
    
    if (conf.containsKey("seqPattern")) {
      this.pattern = conf.getString("seqPattern");
      // check pattern percents in all possible subtrees (= nodes)
      // if a node is OK then its children don't have to be checked!
      // (we need the max possible subtrees)
      
      // check root
      TreeNode root = tree.getRoot();
      if (checkNode(root, ret)) {
        return ret;
      }
      
      // check children
      processChildren(root, ret);
    }
    return ret;
  }
  
  // recursive processing of smaller subtrees
  private void processChildren(TreeNode node, ArrayList<TreeNode> results) {
    if (checkNode(node, results)) {
      return;
    }
    for (TreeNode n: node.getChildren()) {
      processChildren(n, results);
    }
  }
  
  private boolean checkNode(TreeNode node, ArrayList<TreeNode> results) {
    if (checkNumOfLeaves(node) && checkSubTreeHeight(node) &&
        checkNodeForPattern(node)) {
      setHasPatternForLeaves(node);
      results.add(node);
      return true;
    }
    return false;
  }
  
  private boolean checkNodeForPattern(TreeNode n) {
    int okLeaves = n.getLeafNumWithPattern(pattern);
    int allLeaves = n.getLeafNum();
    double percent = 1.0 * okLeaves / allLeaves;
    int minPattPercent = DEFAULT_MIN_PATTERN_PERCENT;
    if (conf.containsKey("minPatternPercent")) {
      minPattPercent = conf.getInt("minPatternPercent");
      minPattPercent = Math.min(minPattPercent, 100);
      minPattPercent = Math.max(minPattPercent, 1);
    }
    if (percent >= 1.0* minPattPercent / 100.0) {
      return true;
    }
    return false;
  }
  
  private void setHasPatternForLeaves(TreeNode n) {
    if (n.isLeaf() &&  n.getSeqString().contains(pattern)) {
      n.setHasPattern(true);
    }
    if (! n.isLeaf()) {
      for (TreeNode child: n.getChildren()) {
        setHasPatternForLeaves(child);
      }
    }
  }

  private boolean checkNumOfLeaves(TreeNode treeNode) {
    int numOfAllLeaves = treeNode.getLeafNum();
    if (conf.containsKey("minLeafNum")) {
      if (conf.getInt("minLeafNum") > numOfAllLeaves) {
        return false;
      }
    }
    return true;
  }

  private boolean checkSubTreeHeight(TreeNode treeNode) {
    int treeHeight = treeNode.getSubTreeHeight();
    if (conf.containsKey("minHeightNum")) {
      if (conf.getInt("minHeightNum") > treeHeight) {
        return false;
      }
    }
    return true;
  }

  public List<FastaItem> getFastaResult(TreeNode subtreeRoot) {
    List<FastaItem> res = new ArrayList<FastaItem>();
    return subtreeRoot.addSubtreeFastaItemsToSet(res);
  }


}
