package me.ibhh.CommandLogger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

class Update {

    /**
     *
     */
    public static final long serialVersionUID = 1L;
    public String s; // stored
    transient int i; // transient: not stored
    public CommandLogger plugin;

    public Update(CommandLogger up) {
        plugin = up;
    }

    /**
     * Checks version with a http-connection
     *
     * @param
     * @return float: latest recommend build.
     */
    public float getNewVersion(String url) {
        if (plugin.getConfig().getBoolean("internet")) {
            float rt2 = 0;
            String zeile;
            try {
                URL myConnection = new URL(url);
                URLConnection connectMe = myConnection.openConnection();

                InputStreamReader lineReader = new InputStreamReader(connectMe.getInputStream());
                BufferedReader br = new BufferedReader(new BufferedReader(lineReader));
                zeile = br.readLine();
                rt2 = Float.parseFloat(zeile);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                plugin.Logger("Exception: IOException!", "Error");
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
                plugin.Logger("Exception: Exception!", "");
                return 0;
            }
            return rt2;
        } else {
            return plugin.aktuelleVersion();
        }
    }

    /**
     * Checks version with a http-connection
     *
     * @param
     * @return float: latest recommend build.
     */
    public String[] getBlacklisted(String url) {
            try {
                URL myConnection = new URL(url);
                URLConnection connectMe = myConnection.openConnection();

                InputStreamReader lineReader = new InputStreamReader(connectMe.getInputStream());
                BufferedReader br = new BufferedReader(new BufferedReader(lineReader));
                String Zeile;
                String[] rt;
                for (i = 0; ((Zeile = br.readLine()) != null) && i < 101; i++) {
                    rt = Zeile.split(":");
                    if (rt[0].equalsIgnoreCase(Float.toString(plugin.Version))) {
                        return rt;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                plugin.Logger("Exception: IOException!", "Error");
            } catch (Exception e) {
                e.printStackTrace();
                plugin.Logger("Exception: Exception!", "");
            }
        return null;
    }

    public static String readAll(Reader in) throws IOException {
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        try {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int charsRead;
            while ((charsRead = in.read(buf)) != -1) {
                sb.append(buf, 0, charsRead);
            }
            return sb.toString();
        } finally {
            in.close();
        }
    }

    public void autoDownload(String url, String path, String name, String type) throws Exception {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(path + name);

        if (file.exists()) {
            file.delete();
            try {
                URL newurl = new URL(url);
                //Eingehender Stream wird "erzeugt"
                BufferedInputStream buffin = new BufferedInputStream(newurl.openStream());
                BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[200000];
                int len;
                //Ausgelesene Daten in die Datei schreiben
                while ((len = buffin.read(buffer)) != -1) {
                    buffout.write(buffer, 0, len);
                }
                buffout.flush();
                buffout.close();
                buffin.close();
                plugin.Logger("New " + name + " downloaded, Look up under " + path, "Warning");
            } finally {
            }
            return;
        }
        if (!file.exists()) {
            try {
                URL newurl = new URL(url);
                //Eingehender Stream wird "erzeugt"
                BufferedInputStream buffin = new BufferedInputStream(newurl.openStream());
                BufferedOutputStream buffout = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[200000];
                int len;
                //Ausgelesene Daten in die Datei schreiben
                while ((len = buffin.read(buffer)) != -1) {
                    buffout.write(buffer, 0, len);
                }
                buffout.flush();
                buffout.close();
                buffin.close();
                plugin.Logger("New " + name + " downloaded, Look up under " + path, "Warning");
            } finally {
            }
        }
    }
}