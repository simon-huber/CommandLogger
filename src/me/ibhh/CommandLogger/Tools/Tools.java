package me.ibhh.CommandLogger.Tools;

public class Tools {

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception localException) {
        }
        return false;
    }

    public String[] stringtoArray(String s, String sep) {
        StringBuffer buf = new StringBuffer(s);
        int arraysize = 1;
        for (int i = 0; i < buf.length(); i++) {
            if (sep.indexOf(buf.charAt(i)) != -1) {
                arraysize++;
            }
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
                    } else if (buf.toString().lastIndexOf(sep) == y) {
                        elements[z] = buf.toString().substring(
                                0, buf.toString().indexOf(sep));
                        z++;
                        buf.delete(0, buf.toString().indexOf(sep) + 1);
                        elements[z] = buf.toString();
                        z++;
                        buf.delete(0, buf.length());
                    }
                }
            }
        } else {
            elements[0] = buf.toString();
        }
        buf = null;
        return elements;
    }

    public static long time(String timeString) throws IncorrectDatumException {
        String stringvalue = timeString.substring(timeString.length() - 1);
        int value = 0;
        try {
            value = Integer.parseInt(timeString.substring(0, timeString.length()-1));
        } catch (Exception e) {
            throw new IncorrectDatumException("not right formated: " + timeString);
        }
        if(stringvalue.equalsIgnoreCase("s")){
            return value * 1000;
        } else if(stringvalue.equalsIgnoreCase("m")) {
            return value * 1000 * 60;
        } else if(stringvalue.equalsIgnoreCase("h")) {
            return value * 1000 * 60 * 60;
        } else if(stringvalue.equalsIgnoreCase("d")) {
            return value * 1000 * 60 * 60 * 24;
        } else if(stringvalue.equalsIgnoreCase("w")) {
            return value * 1000 * 60 * 60 * 24 * 7;
        } else if(stringvalue.equalsIgnoreCase("y")) {
            return value * 1000 * 60 * 60 * 24 * 365;
        } 
        return 0;
    }
}