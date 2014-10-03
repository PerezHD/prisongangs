package com.pwncraftpvp.prisongangs.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pwncraftpvp.prisongangs.core.Main;

public class Warzone {
	
	static Main main = Main.getInstance();
	
	/**
	 * Get the warzone file
	 * @return The warzone file
	 */
	public static File getFile(){
		return new File(main.getDataFolder(), "warzones.yml");
	}
	
	/**
	 * Get the warzone file config
	 * @return The warzone file config
	 */
	public static FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a config value
	 * @param key - The value to set
	 * @param entry - What to set the value as
	 */
	public static void setConfigValue(String key, Object entry){
		FileConfiguration fc = getConfig();
	    fc.set(key, entry);
	    try{
	      fc.save(getFile());
	    }catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	/**
	 * Check if a location is a warzone
	 * @param l - The location to check
	 * @return True or false depending on if it's a warzone or not
	 */
	public static boolean check(Location l){
		if(getConfig().getInt("chunks." + l.getChunk().getX() + ">" + l.getChunk().getZ() + ".x") != 0 || 
				getConfig().getInt("chunks." + l.getChunk().getX() + ">" + l.getChunk().getZ() + ".z") != 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Claim warzone at a location
	 * @param l - The location to claim at
	 */
	public static void claim(Location l){
		Chunk chunk = l.getChunk();
		if(check(l) == false){
			setConfigValue("chunks." + l.getChunk().getX() + ">" + l.getChunk().getZ() + ".x", chunk.getX());
			setConfigValue("chunks." + l.getChunk().getX() + ">" + l.getChunk().getZ() + ".z", chunk.getZ());
		}
	}
	
	/**
	 * Unclaim warzone at a location
	 * @param l - The location to unclaim at
	 */
	public static void unclaim(Location l){
		if(check(l) == true){
			setConfigValue("chunks." + l.getChunk().getX() + ">" + l.getChunk().getZ(), null);
		}
	}
}
