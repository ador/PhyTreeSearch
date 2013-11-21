package hu.sztaki.phytree;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestExample {

  @Test
  public void testEmpty() {
    Integer i = 5;
    int k = i.intValue();
    assertNotNull(i);
    int expected = 5;
    assertEquals(expected, k);
    try {
      i = null;
      i.toString();
      fail("Allows nullpointer");
    } catch (NullPointerException e) {}
  }

  @Test
  public void testSecond() {
    assertEquals(2 + 2, 2 * 2);
  }

}
