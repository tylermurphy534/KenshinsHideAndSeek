package net.tylermurphy.hideAndSeek.util;

import net.tylermurphy.hideAndSeek.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UUIDFetcher {

    private static final Map<String,UUID> CACHE = new HashMap<>();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static int cacheTask;

    public static void init(){
        cacheTask = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, CACHE::clear,600*20, 600*20);
    }

    public static void cleanup(){
        Main.plugin.getServer().getScheduler().cancelTask(cacheTask);
    }

    public static UUID getUUID(String playername) {

        if(CACHE.containsKey(playername)) return CACHE.get(playername);

        String output = callURL(UUID_URL + playername);
        StringBuilder result = new StringBuilder();
        readData(output, result);
        String u = result.toString();
        StringBuilder uuid = new StringBuilder();
        for (int i = 0; i <= 31; i++) {
            uuid.append(u.charAt(i));
            if (i == 7 || i == 11 || i == 15 || i == 19) {
                uuid.append('-');
            }
        }

        CACHE.put(playername, UUID.fromString(uuid.toString()));

        return UUID.fromString(uuid.toString());
    }

    private static void readData(String toRead, StringBuilder result) {
        for (int i = toRead.length() - 3; i >= 0; i--) {
            if (toRead.charAt(i) != '"') {
                result.insert(0, toRead.charAt(i));
            } else {
                break;
            }
        }
    }

    private static String callURL(String urlStr) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn;
        InputStreamReader in;
        try {
            URL url = new URL(urlStr);
            urlConn = url.openConnection();
            if (urlConn != null) {
                urlConn.setReadTimeout(60 * 1000);
            }
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
