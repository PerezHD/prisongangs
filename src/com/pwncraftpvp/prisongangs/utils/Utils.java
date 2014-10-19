package com.pwncraftpvp.prisongangs.utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.pwncraftpvp.prisongangs.core.Main;
import com.pwncraftpvp.prisongangs.gang.Gang;
import com.pwncraftpvp.prisongangs.gang.Rank;

public class Utils {
	
	static Main main = Main.getInstance();
	
	/**
	 * Check if a string is also an integer
	 * @param isIt - The string to check
	 * @return True or false depending on if the string is an integer or not
	 */
	public static boolean isInteger(String isIt){
		try{
			Integer.parseInt(isIt);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Round a number to two decimal points
	 * @param d - The number to round
	 * @return The rounded number
	 */
	public static float roundTwoDecimals(float d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Float.valueOf(twoDForm.format(d));
    }
	
	/**
	 * Round a number up
	 */
	public static long roundUp(long n, long m) {
	    return n >= 0 ? ((n + m - 1) / m) * m : (n / m) * m;
	}
	
	/**
	 * Check if a string contains symbols
	 * @param name - The string to check
	 * @return True or false depending on if the string has symbols or not
	 */
	public static boolean containsSymbols(String name){
		boolean symbols = false;
		if(name.contains("!") || name.contains("@") || name.contains("#") || name.contains("$") || name.contains("%") || name.contains("^") || name.contains("*") ||
				name.contains("(") || name.contains(")") || name.contains("+") || name.contains("-") || name.contains("?") || name.contains("<") ||
				name.contains(">") || name.contains("|") || name.contains("[") || name.contains("]") || name.contains("{") || name.contains("}") ||
				name.contains("/") || name.contains(":") || name.contains(";") || name.contains("\"") || name.contains("\\") || name.contains("'") || name.contains("&")){
			symbols = true;
		}
		return symbols;
	}
	
	/**
	 * Get the total amount of gangs created, not taking into account deleted gangs
	 * @return The total amount of gangs created
	 */
	public static int getTotalGangs(){
		int totalGangs = 0;
		File folder = new File(main.getDataFolder() + File.separator + "gangs");
		if(folder.exists() == true){
			for(int x = 1; x <= folder.listFiles().length; x++){
				totalGangs++;
			}
		}
		return totalGangs;
	}
	
	/**
	 * Get a new gang ID for use to create a new gang
	 * @return An available ID for a new gang to use
	 */
	public static int getNewGangID(){
		int newID = 1;
		for(int x = 1; x <= 5000; x++){
			if(main.getConfig().getString("gangs." + x + ".name") == null){
				newID = x;
				break;
			}
		}
		main.getConfig().set("newestID", newID);
		main.saveConfig();
		return newID;
	}
	
	/**
	 * Check if a gang exists
	 * @param name - The gang to check
	 * @return True or false depending on if the gang exists or not
	 */
	public static boolean doesGangExist(String name){
		boolean exists = false;
		for(int x = 1; x <= getTotalGangs(); x++){
			if(main.getConfig().getString("gangs." + x + ".name") != null){
				if(main.getConfig().getString("gangs." + x + ".name").equalsIgnoreCase(name)){
					exists = true;
					break;
				}
			}
		}
		return exists;
	}
	
	/**
	 * Get a gang by its name
	 * @param name - The gang to get by a name
	 * @return The gang retrieved by the name
	 */
	public static Gang getGangByName(String name){
		Gang gang = null;
		for(int x = 1; x <= getTotalGangs(); x++){
			if(main.getConfig().getString("gangs." + x + ".name").equalsIgnoreCase(name)){
				gang = new Gang(x);
				break;
			}
		}
		return gang;
	}
	
	/**
	 * Get a list of all the gangs
	 * @return A list of all the gangs
	 */
	public static List<Gang> getGangs(){
		List<Gang> gangs = new ArrayList<Gang>();
		for(int x = 1; x <= getTotalGangs(); x++){
			Gang g = new Gang(x);
			if(g.getFile().exists() == true){
				gangs.add(g);
			}
		}
		return gangs;
	}
	
	/**
	 * Create a new gang
	 * @param leader - The name of the gang creator
	 * @param name - The name of the gang
	 */
	public static void createGang(String leader, String name){
		int id = getNewGangID();
		main.getConfig().set("gangs." + id + ".name", name);
		main.saveConfig();
		Gang gang = new Gang(id);
		gang.setLeader(leader);
		gang.addMember(leader);
		gang.setMemberRank(leader, Rank.LEADER);
	}
	
	/**
	 * Disband a gang
	 * @param gangID - The ID of the gang to disband
	 */
	public static void disbandGang(int gangID){
		Gang gang = new Gang(gangID);
		for(String s : gang.getMembers()){
			gang.removeMember(s);
		}
		for(int s : gang.getAllies()){
			Gang g = new Gang(s);
			g.removeAlly(gang.getID());
		}
		gang.getFile().delete();
		main.getConfig().set("gangs." + gangID + ".name", null);
		main.saveConfig();
	}
	
	/**
	 * Get the chat format for gangs
	 * @return The chat format
	 */
	public static String getChatFormat(){
		return main.getConfig().getString("chatFormat");
	}
	
	/**
	 * Set the chat format for gangs
	 * @param format - The chat format
	 */
	public static void setChatFormat(String format){
		main.getConfig().set("chatFormat", format);
		main.saveConfig();
	}
}
