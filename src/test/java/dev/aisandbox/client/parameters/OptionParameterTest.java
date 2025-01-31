package dev.aisandbox.client.parameters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OptionParameterTest {

  @Test
  public void parseNumberTest() throws ParameterParseException {
    OptionParameter e =
        new OptionParameter(
            "key",
            new String[] {"Option 0", "Option 1", "Option 2"},
            "Test parameter",
            "description");
    e.setParsableValue("1");
    assertEquals("Number Value", 1, e.getOptionIndex());
    assertEquals("Test Value", "Option 1", e.getOptionString());
  }

  @Test
  public void parseTextTest() throws ParameterParseException {
    OptionParameter e =
        new OptionParameter(
            "key",
            new String[] {"Option 0", "Option 1", "Option 2"},
            "Test parameter",
            "description");
    e.setParsableValue("Option 1");
    assertEquals("Number Value", 1, e.getOptionIndex());
    assertEquals("Test Value", "Option 1", e.getOptionString());
  }
}
