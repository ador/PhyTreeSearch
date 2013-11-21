package hu.sztaki.fileops;

import static org.junit.Assert.*;

import org.junit.Test;

public class FileNumberTest {

  @Test
  public void testGetNumber() {
    FileNumber fileN = new FileNumber();
    fileN.setPath("/home/bachoreczm/valami2.txt");
    fileN = new FileNumber("/home/bachoreczm/valami0.txt");
    int number = fileN.getNumber();
    assertEquals(0, number);
    fileN = new FileNumber("/home/bachoreczm/valami2.txt");
    number = fileN.getNumber();
    assertEquals(2, number);
    fileN.setPath("/home/bachoreczm/valami23.txt");
    number = fileN.getNumber();
    assertEquals(23, number);
    fileN.setPath("/home/bachoreczm/val44ami56aaa.txt");
    number = fileN.getNumber();
    assertEquals(56, number);
  }

}
