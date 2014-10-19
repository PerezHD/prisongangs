package com.pwncraftpvp.prisongangs.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.pwncraftpvp.prisongangs.core.Main;
import com.pwncraftpvp.prisongangs.core.PPlayer;

public class TeleportTask extends BukkitRunnable{
	
	Main main = Main.getInstance();
	
	Player player;
	Location destination;
	public TeleportTask(Player p, Location dest){
		player = p;
		destination = dest;
	}
	
	int count = 6;
	public void run(){
		if(count > 0){
			count--;
		}else{
			player.teleport(destination);
			if(main.teleporting.containsKey(player.getName())){
				main.teleporting.remove(player.getName());
			}
			PPlayer pplayer = new PPlayer(player);
			pplayer.sendMessage("You have been teleported!");
			this.cancel();
		}
	}

}
