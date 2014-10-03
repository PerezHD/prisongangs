package com.pwncraftpvp.prisongangs.core;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.pwncraftpvp.prisongangs.gang.Gang;
import com.pwncraftpvp.prisongangs.gang.Rank;
import com.pwncraftpvp.prisongangs.utils.UTFUtils;

public class PPlayer {
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
		
	Player player = null;
	String playerName = null;
	
	public PPlayer(Player p){
		player = p;
		playerName = p.getName();
	}
	
	public PPlayer(String pName){
		playerName = pName;
	}
	
	/**
	 * Get the player's file
	 */
	@SuppressWarnings("deprecation")
	public File getFile(){
		if(player != null){
			return new File(main.getDataFolder() + File.separator + "players", player.getUniqueId() + ".yml");
		}else if(playerName != null){
			return new File(main.getDataFolder() + File.separator + "players", Bukkit.getOfflinePlayer(playerName).getUniqueId() + ".yml");
		}else{
			return null;
		}
	}
	
	/**
	 * Get the player's config
	 */
	public FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a value in the player's config
	 * 
	 * @param key - The location of the value to set
	 * @param entry - The value to set
	 */
	public void setConfigValue(String key, Object entry){
		FileConfiguration fc = getConfig();
	    fc.set(key, entry);
	    try{
	      fc.save(getFile());
	    }catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	/**
	 * Send a message header to the player
	 * @param header - The header to be sent
	 */
	public void sendMessageHeader(String header){
		player.sendMessage(gray + "-=(" + yellow + "*" + gray + ")=-" + "  " + yellow + header + "  " + gray + "-=(" + yellow + "*" + gray + ")=-");
	}
	
	/**
	 * Send a message to the player
	 * @param message - The message to be sent
	 */
	public void sendMessage(String message){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + gray + " " + message);
	}
	
	/**
	 * Send an error message to the player
	 * @param error - The error message to be sent
	 */
	public void sendError(String error){
		player.sendMessage(ChatColor.GOLD + UTFUtils.getArrow() + ChatColor.DARK_RED + " " + error);
	}
	
	/**
	 * Send the command help page to the player
	 */
	public void sendCommandHelp(int page, Command cmd){
		this.sendMessageHeader("Command Help");
		if(page == 1){
			this.sendMessage(yellow + "/" + cmd.getName() + " create <name> " + gray + "- Create a new gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " join <gang> " + gray + "- Join a gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " leave " + gray + "- Leave your gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " invite <player> " + gray + "- Invite a player!");
			this.sendMessage(yellow + "/" + cmd.getName() + " chat " + gray + "- Switch chat channels!");
			this.sendMessage(yellow + "/" + cmd.getName() + " promote <player> " + gray + "- Promote a member!");
			this.sendMessage(yellow + "/" + cmd.getName() + " demote <player> " + gray + "- Demote a member!");
			player.sendMessage(" ");
			this.sendMessage("Type " + yellow + "/" + cmd.getName() + " help " + (page + 1) + gray + " for more commands!");
		}else if(page == 2){
			this.sendMessage(yellow + "/" + cmd.getName() + " info <gang> " + gray + "- Get the information of a gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " ally <gang> " + gray + "- Ally a gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " unally <gang> " + gray + "- Unally a gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " leader <player> " + gray + "- Make a member the leader!");
			this.sendMessage(yellow + "/" + cmd.getName() + " disband " + gray + "- Disband your gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " rename <name> " + gray + "- Rename your gang!");
			this.sendMessage(yellow + "/" + cmd.getName() + " kick <player> " + gray + "- Kick a member!");
			if(player.isOp() == true){
				this.sendMessage(yellow + "/" + cmd.getName() + " warzone <claim/unclaim> " + gray + "- Create a warzone!");
				this.sendMessage(yellow + "/" + cmd.getName() + " setformat <chat format> " + gray + "- Set the chat format!");
			}
		}
	}
	
	/**
	 * Send a gang's information to the player
	 * @param gang - The gang to send the information of
	 */
	public void sendGangInformation(Gang gang){
		this.sendMessageHeader(gang.getName() + " Information");
		this.sendMessage("Leader");
		player.sendMessage(gray + "  - " + yellow + gang.getLeader());
		
		this.sendMessage("Members (" + yellow + gang.getMembers().size() + gray + ")");
		String members = "";
		for(String s : gang.getMembers()){
			if(members.length() > 1){
				members = members + gray + ", " + yellow + s;
			}else{
				members = members + yellow + s;
			}
		}
		player.sendMessage(gray + "  - " + members);
		
		if(gang.getAllies().size() > 0){
			this.sendMessage("Allies (" + yellow + gang.getAllies().size() + gray + ")");
			String allies = "";
			for(int i : gang.getAllies()){
				Gang g = new Gang(i);
				if(allies.length() > 1){
					allies = allies + gray + ", " + yellow + g.getName();
				}else{
					allies = allies + yellow + g.getName();
				}
			}
			player.sendMessage(gray + "  - " + allies);
		}
		
		this.sendMessage("Statistics");
		player.sendMessage(gray + "  - Kills: " + yellow + (int)gang.getKills());
		player.sendMessage(gray + "  - Deaths: " + yellow + (int)gang.getDeaths());
		player.sendMessage(gray + "  - KDR: " + yellow + gang.getKDR());
	}
	
	/**
	 * Set the player's gang
	 * @param id - The id of the gang
	 */
	public void setGangID(int id){
		this.setConfigValue("gang", id);
	}
	
	/**
	 * Get the player's gang
	 * @return The id of the player's gang
	 */
	public int getGangID(){
		return this.getConfig().getInt("gang");
	}
	
	/**
	 * Check if the player has a gang
	 * @return True or false depending on if the player has a gang or not
	 */
	public boolean hasGang(){
		if(this.getGangID() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Get the gang of the player
	 * @return The gang of the player
	 */
	public Gang getGang(){
		return new Gang(this.getGangID());
	}
	
	/**
	 * Get the rank of the player
	 * @return The gang rank of the player
	 */
	public Rank getGangRank(){
		return this.getGang().getMemberRank(playerName);
	}
	
	/**
	 * Set the player's kills
	 * @param id - The kills to set
	 */
	public void setKills(double kills){
		this.setConfigValue("stats.kills", kills);
	}
	
	/**
	 * Get the player's kills
	 * @return The amount of kills
	 */
	public double getKills(){
		return this.getConfig().getDouble("stats.kills");
	}
	
	/**
	 * Set the player's deaths
	 * @param id - The deaths to set
	 */
	public void setDeaths(double deaths){
		this.setConfigValue("stats.deaths", deaths);
	}
	
	/**
	 * Get the player's deaths
	 * @return The amount of deaths
	 */
	public double getDeaths(){
		return this.getConfig().getDouble("stats.deaths");
	}
}
