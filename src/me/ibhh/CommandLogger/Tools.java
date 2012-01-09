package me.ibhh.CommandLogger;

public class Tools
{
  public static boolean isInteger(String input)
  {
    try
    {
      Integer.parseInt(input);
      return true;
    } catch (Exception localException) {
    }
    return false;
  }

  public String[] stringtoArray(String s, String sep)
  {
    StringBuffer buf = new StringBuffer(s);
    int arraysize = 1;
    for (int i = 0; i < buf.length(); i++) {
      if (sep.indexOf(buf.charAt(i)) != -1)
        arraysize++;
    }
    String[] elements = new String[arraysize];
    int z = 0;
    if (buf.toString().indexOf(sep) != -1) {
      while (buf.length() > 0) {
        if (buf.toString().indexOf(sep) != -1) {
          int y = buf.toString().indexOf(sep);
          if (y != buf.toString().lastIndexOf(sep)) {
            elements[z] = buf.toString().substring(0, y);
            z++;
            buf.delete(0, y + 1);
          }
          else if (buf.toString().lastIndexOf(sep) == y) {
            elements[z] = buf.toString().substring(
              0, buf.toString().indexOf(sep));
            z++;
            buf.delete(0, buf.toString().indexOf(sep) + 1);
            elements[z] = buf.toString(); z++;
            buf.delete(0, buf.length());
          }
        }
      }
    }
    else {
      elements[0] = buf.toString();
    }
    buf = null;
    return elements;
  }
}