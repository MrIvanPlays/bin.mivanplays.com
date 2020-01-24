package com.mrivanplays.ivanbin;

import com.mrivanplays.ivanbin.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

  @Test
  public void test() {
    System.out.println(StringUtils.generateRandomString(11));
    Assert.assertFalse(false); // make the test run
  }
}
