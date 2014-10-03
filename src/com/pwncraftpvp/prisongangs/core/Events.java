package com.pwncraftpvp.prisongangs.core;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.pwncraftpvp.prisongangs.utils.UTFUtils;
import com.pwncraftpvp.prisongangs.utils.Utils;
import com.pwncraftpvp.prisongangs.utils.Warzone;

public class Events implements Listener{
	
	Main main = Main.getInstance();
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	@EventHandler
	public void asyncPlayerChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		PPlayer pplayer = new PPlayer(player);
		if(!main.gangChat.contains(player.getName()) && !main.allyChat.contains(player.getName())){
			if(pplayer.hasGang() == true){
				String f = event.getFormat().replaceAll("\\{GANG\\}", Utils.getChatFormat().replaceAll("%g", pplayer.getGang().getName()) + " ")
						.replaceAll("\\{gang\\}", Utils.getChatFormat().replaceAll("%g", pplayer.getGang().getName()) + " ");
				event.setFormat(f);
			}else{
				String f = event.getFormat().replaceAll("\\{GANG\\}", "").replaceAll("\\{gang\\}", "");
				event.setFormat(f);
			}
		}else{
			event.setCancelled(true);
			if(main.gangChat.contains(player.getName())){
				pplayer.getGang().broadcastSoftMessage(gray + "[" + yellow + WordUtils.capitalizeFully(pplayer.getGangRank().toString()) + gray + "]" + yellow + " Gang Chat" + " " + 
			gray + UTFUtils.getSeperator() + " " + ChatColor.GOLD + player.getName() + ": " + gray + event.getMessage());
			}else if(main.allyChat.contains(player.getName())){
				pplayer.getGang().broadcastSoftAllyMessage(yellow + "Ally Chat" + " " + gray + UTFUtils.getSeperator() + " " + ChatColor.GOLD + player.getName() + ": " + gray + event.getMessage());
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null){
			if(event.getEntity().getKiller() instanceof Player){
				Player player = event.getEntity();
				PPlayer pplayer = new PPlayer(player);
				Player killer = (Player) player.getKiller();
				PPlayer pkiller = new PPlayer(killer);
				pplayer.setDeaths(pplayer.getDeaths() + 1);
				pkiller.setKills(pkiller.getKills() + 1);
			}
		}
	}
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			Player player = (Player) event.getEntity();
			PPlayer pplayer = new PPlayer(player);
			Player damager = (Player) event.getDamager();
			PPlayer pdamager = new PPlayer(damager);
			if(Warzone.check(player.getLocation()) == false){
				if(pplayer.hasGang() == true){
					if(pplayer.getGang().getID() == pdamager.getGang().getID() || pplayer.getGang().isAlly(pdamager.getGang().getID()) == true){
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
