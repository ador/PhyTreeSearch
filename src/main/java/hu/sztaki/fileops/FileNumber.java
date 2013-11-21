package hu.sztaki.fileops;

public class FileNumber {

  // Parses the last integer from a file path,
  // for example, from "/home/zoo/foo/my55file123.nwk" it will return 123
  
  private String path_;

  public FileNumber(String path) {
    path_ = path;
  }

  public FileNumber() {
  }

  public void setPath(String path) {
    path_ = path;
  }

  public int getNumber() {
    String file = path_.split("/")[path_.split("/").length - 1];
    int len = file.length();
    int from = 0;
    int to = -1;
    int zeroASCII = 48;
    boolean newNum = true;
    for (int i = 0; i < len; ++i) {
      if (file.charAt(i) >= zeroASCII && file.charAt(i) <= zeroASCII + 9) {
        if (newNum) {
          from = i;
          to = i;
          newNum = false;
        } else {
          to = i;
        }
      } else {
        newNum = true;
      }
    }
    if (from > to) {
      return -1;
    } else {
      return Integer.parseInt(file.substring(from, to + 1));
    }
  }

}
