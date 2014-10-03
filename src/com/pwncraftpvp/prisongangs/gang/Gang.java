package com.pwncraftpvp.prisongangs.gang;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.pwncraftpvp.prisongangs.core.Main;
import com.pwncraftpvp.prisongangs.core.PPlayer;

public class Gang {
	
	Main main = Main.getInstance();
	
	int id;
	public Gang(int gangID){
		id = gangID;
	}
	
	/**
	 * Get the gang file
	 */
	public File getFile(){
		return new File(main.getDataFolder() + File.separator + "gangs", this.getName() + ".yml");
	}
	
	/**
	 * Get the gang config
	 */
	public FileConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	/**
	 * Set a value in the gang config
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
	 * Get the gang's id
	 * @return The gang's id
	 */
	public int getID(){
		return id;
	}
	
	/**
	 * Get the gang's name
	 * @return The gang's name
	 */
	public String getName(){
		return main.getConfig().getString("gangs." + id + ".name");
	}
	
	/**
	 * Set the gang's name
	 * @param name - The name to set the gang's name as
	 */
	public void setName(String name){
		this.getFile().renameTo(new File(main.getDataFolder() + File.separator + "gangs", name + ".yml"));
		main.getConfig().set("gangs." + id + ".name", name);
		main.saveConfig();
	}
	
	/**
	 * Get the list of members
	 * @return - The list of members
	 */
	public List<String> getMembers(){
		return this.getConfig().getStringList("members");
	}
	
	/**
	 * Add a player to the gang
	 * @param player - The name of the player to add to the gang
	 */
	public void addMember(String player){
		List<String> members = this.getConfig().getStringList("members");
		members.add(player);
		this.setConfigValue("members", members);
		PPlayer pplayer = new PPlayer(player);
		pplayer.setGangID(id);
	}
	
	/**
	 * Remove a member from the gang
	 * @param player - The name of the member to remove from the gang
	 */
	public void removeMember(String player){
		List<String> members = this.getConfig().getStringList("members");
		members.remove(player);
		this.setConfigValue("members", members);
		this.setConfigValue("ranks." + player, null);
		PPlayer pplayer = new PPlayer(player);
		pplayer.setGangID(0);
	}
	
	/**
	 * Get the name of the gang's leader
	 * @return The gang's leader
	 */
	public String getLeader(){
		return this.getConfig().getString("leader");
	}
	
	/**
	 * Set the gang's leader
	 * @param player - The name of the player to set as the leader
	 */
	public void setLeader(String player){
		this.setConfigValue("leader", player);
	}
	
	/**
	 * Get a member's rank
	 * @param member - The member to get the rank of
	 * @return The rank of the member
	 */
	public Rank getMemberRank(String member){
		if(this.getConfig().getString("ranks." + member + ".rank") != null){
			Rank rank = null;
			for(Rank r : Rank.values()){
				if(this.getConfig().getString("ranks." + member + ".rank").equalsIgnoreCase(r.toString())){
					rank = r;
					break;
				}
			}
			return rank;
		}else{
			return Rank.MEMBER;
		}
	}
	
	/**
	 * Set a member's rank
	 * @param member - The member to set the rank of
	 * @param rank - The rank to set the member as
	 */
	public void setMemberRank(String member, Rank rank){
		this.setConfigValue("ranks." + member + ".rank", rank.toString().toLowerCase());
	}
	
	/**
	 * Get the list of allied gangs
	 * @return - The list of allied gangs
	 */
	public List<Integer> getAllies(){
		return this.getConfig().getIntegerList("allies");
	}
	
	/**
	 * Get the position of the ally's gang id in the list
	 * @param gangID - The ally's gang id
	 * @return The position in the list
	 */
	public int getAllyPositionInList(int gangID){
		int pos = 0;
		for(int x = 0; x <= (this.getAllies().size() - 1); x++){
			if(this.getAllies().get(x) == gangID){
				pos = x;
				break;
			}
		}
		return pos;
	}
	
	/**
	 * Add an ally to the gang
	 * @param player - The name of the gang to add as an ally
	 */
	public void addAlly(int gangID){
		List<Integer> allies = this.getConfig().getIntegerList("allies");
		allies.add(gangID);
		this.setConfigValue("allies", allies);
	}
	
	/**
	 * Remove an ally from the gang
	 * @param player - The name of the gang to remove as an ally
	 */
	public void removeAlly(int gangID){
		List<Integer> allies = this.getConfig().getIntegerList("allies");
		allies.remove(this.getAllyPositionInList(gangID));
		this.setConfigValue("allies", allies);
	}
	
	/**
	 * Check if another gang is an ally
	 * @param gangID - The gang to check
	 * @return True or false depending on if the gang is an ally or not
	 */
	public boolean isAlly(int gangID){
		return this.getAllies().contains(gangID);
	}
	
	/**
	 * Get the total kills of the gang
	 * @return The total kills of the gang
	 */
	public double getKills(){
		double kills = 0;
		for(String s : this.getMembers()){
			PPlayer pp = new PPlayer(s);
			kills = kills + pp.getKills();
		}
		return kills;
	}
	
	/**
	 * Get the total deaths of the gang
	 * @return The total deaths of the gang
	 */
	public double getDeaths(){
		double deaths = 0;
		for(String s : this.getMembers()){
			PPlayer pp = new PPlayer(s);
			deaths = deaths + pp.getDeaths();
		}
		return deaths;
	}
	
	/**
	 * Get the kill-to-death ratio of the gang
	 * @return The KDR of the entire gang
	 */
	public double getKDR(){
		double kdr = (this.getKills() / this.getDeaths());
		if(kdr > 0){
			return kdr;
		}else{
			return 0;
		}
	}
	
	/**
	 * Broadcast a message to all online members
	 * @param message - The message to broadcast
	 */
	public void broadcastMessage(String message){
		for(String s : this.getMembers()){
			if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline() == true){
				PPlayer pp = new PPlayer(Bukkit.getPlayer(s));
				pp.sendMessage(message);
			}
		}
	}
	
	/**
	 * Broadcast a message to all mods and above
	 * @param message - The message to broadcast
	 */
	public void broadcastModMessage(String message){
		for(String s : this.getMembers()){
			if(this.getMemberRank(s) == Rank.MOD || this.getMemberRank(s) == Rank.LEADER){
				if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline() == true){
					PPlayer pp = new PPlayer(Bukkit.getPlayer(s));
					pp.sendMessage(message);
				}
			}
		}
	}
	
	/**
	 * Broadcast a message that doesn't have anything originally in it to all online members
	 * @param message - The message to broadcast
	 */
	public void broadcastSoftMessage(String message){
		for(String s : this.getMembers()){
			if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline() == true){
				Bukkit.getPlayer(s).sendMessage(message);
			}
		}
	}
	
	/**
	 * Broadcast a message that doesn't have anything originally in it to all online members
	 * @param message - The message to broadcast
	 */
	public void broadcastSoftAllyMessage(String message){
		for(String s : this.getMembers()){
			if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline() == true){
				Bukkit.getPlayer(s).sendMessage(message);
			}
		}
		for(int a : this.getAllies()){
			Gang g = new Gang(a);
			for(String s : g.getMembers()){
				if(Bukkit.getPlayer(s) != null && Bukkit.getPlayer(s).isOnline() == true){
					Bukkit.getPlayer(s).sendMessage(message);
				}
			}
		}
	}
}
