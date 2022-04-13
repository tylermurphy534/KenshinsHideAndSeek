package net.tylermurphy.hideAndSeek.util;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Version {

    private static final Map<String,Boolean> CACHE = new HashMap<>();

    public static boolean atLeast(String testVersion){


        if(CACHE.containsKey(testVersion)) return CACHE.get(testVersion);

        String[] serverCheckTemp = Bukkit.getBukkitVersion().substring(2,Bukkit.getBukkitVersion().indexOf('-')).split("\\.");
        int[] serverCheck = new int[serverCheckTemp.length];
        for(int i=0; i<serverCheck.length; i++){
            serverCheck[i] = Integer.parseInt(serverCheckTemp[i]);
        }

        String[] customCheckTemp = testVersion.substring(2).split("\\.");
        int[] customCheck = new int[customCheckTemp.length];
        for(int i=0; i<customCheck.length; i++){
            customCheck[i] = Integer.parseInt(customCheckTemp[i]);
        }

        boolean result = getResult(customCheck, serverCheck);
        CACHE.put(testVersion, result);
        return result;
    }

    private static boolean getResult(int[] customCheck, int[] serverCheck){
        if(customCheck[0] > serverCheck[0]) return false;
        else if(customCheck[0] < serverCheck[0]) return true;
        else {
            if (customCheck.length == 1 && serverCheck.length == 1) return true;
            else if(customCheck.length == 2 && serverCheck.length == 2){
                return customCheck[1] <= serverCheck[1];
            }
            else return serverCheck.length == 2;
        }
    }
}
