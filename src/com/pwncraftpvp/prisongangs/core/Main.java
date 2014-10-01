package com.pwncraftpvp.prisongangs.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.pwncraftpvp.prisongangs.gang.Gang;
import com.pwncraftpvp.prisongangs.gang.Rank;
import com.pwncraftpvp.prisongangs.utils.Utils;

public class Main extends JavaPlugin{
	
	static Main instance;
	private String gray = ChatColor.GRAY + "";
	private String yellow = ChatColor.YELLOW + "";
	
	public List<String> gangChat = new ArrayList<String>();
	public List<String> allyChat = new ArrayList<String>();
	public HashMap<String, Gang> invited = new HashMap<String, Gang>();
	public HashMap<Integer, Integer> allyInvited = new HashMap<Integer, Integer>();
	
	/**
	 * Get the instance of this class
	 * @return - The instance of this class
	 */
	public static Main getInstance(){
		return instance;
	}
	
	public void onEnable(){
		instance = this;
		this.getServer().getPluginManager().registerEvents(new Events(), this);
		if(this.getConfig().getBoolean("doNotChangeMe") == false){
			this.getConfig().set("doNotChangeMe", true);
			this.saveConfig();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			PPlayer pplayer = new PPlayer(player);
			if(cmd.getName().equalsIgnoreCase("gang") || cmd.getName().equalsIgnoreCase("g")){
				if(args.length == 0){
					pplayer.sendCommandHelp(1, cmd);
				}else if(args.length > 0){
					if(args[0].equalsIgnoreCase("create")){
						if(args.length == 2){
							if(pplayer.hasGang() == false){
								String name = args[1];
								if(Utils.doesGangExist(name) == false){
									if(name.length() <= 16 && name.length() >= 4){
										Utils.createGang(player.getName(), name);
										pplayer.sendMessage("You have created a new gang named " + yellow + name + gray + "!");
									}else{
										pplayer.sendError("The gang name must be 4-16 characters long!");
									}
								}else{
									pplayer.sendError("That gang name is taken!");
								}
							}else{
								pplayer.sendError("You must leave your current gang to create a new one!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " create <name>");
						}
					}else if(args[0].equalsIgnoreCase("disband")){
						if(pplayer.hasGang() == true){
							if(pplayer.getGangRank() == Rank.LEADER){
								pplayer.getGang().broadcastMessage(yellow + player.getName() + gray + " has disbanded the gang!");
								Utils.disbandGang(pplayer.getGang().getID());
							}else{
								pplayer.sendError("You are not the gang leader, therefore you may not do this!");
							}
						}else{
							pplayer.sendError("You are not in a gang!");
						}
					}else if(args[0].equalsIgnoreCase("invite")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.MOD || pplayer.getGangRank() == Rank.LEADER){
									final Player iplayer = Bukkit.getPlayer(args[1]);
									if(iplayer != null && iplayer.isOnline() == true){
										final PPlayer piplayer = new PPlayer(iplayer);
										if(piplayer.hasGang() == false){
											if(!invited.containsKey(iplayer.getName())){
												invited.put(iplayer.getName(), pplayer.getGang());
												piplayer.sendMessage("You have been invited to join " + yellow + pplayer.getGang().getName() + gray + "!");
												iplayer.sendMessage(gray + "  Type " + yellow + "/" + cmd.getName() + " join " + pplayer.getGang().getName() + gray + " to accept!");
												pplayer.sendMessage("You have invited " + yellow + iplayer.getName() + gray + " to join your gang!");
												this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
													public void run(){
														if(invited.containsKey(iplayer.getName())){
															invited.remove(iplayer.getName());
															piplayer.sendMessage("Your pending invitation has expired!");
														}
													}
												}, 1200);
											}else{
												pplayer.sendError("This player currently has a pending invitation!");
											}
										}else{
											pplayer.sendError("This player already has a gang!");
										}
									}else{
										pplayer.sendError("That player is not online!");
									}
								}else{
									pplayer.sendError("You are not a moderator, therefore you may not do this!");
								}
							}else{
								pplayer.sendError("You are not in a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " invite <player>");
						}
					}else if(args[0].equalsIgnoreCase("join")){
						if(args.length == 2){
							if(pplayer.hasGang() == false){
								String gang = args[1];
								if(invited.containsKey(player.getName())){
									if(invited.get(player.getName()).getName().equalsIgnoreCase(gang)){
										invited.get(player.getName()).addMember(player.getName());
										invited.remove(player.getName());
										pplayer.sendMessage("You have joined " + yellow + gang + gray + "!");
										pplayer.getGang().broadcastMessage(yellow + player.getName() + gray + " has joined the gang!");
									}else{
										pplayer.sendError("You were not invited to join this gang!");
									}
								}else{
									pplayer.sendError("You were not invited to join this gang!");
								}
							}else{
								pplayer.sendError("You already have a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " join <gang>");
						}
					}else if(args[0].equalsIgnoreCase("leave")){
						if(pplayer.hasGang() == true){
							if(!pplayer.getGang().getLeader().equalsIgnoreCase(player.getName())){
								pplayer.getGang().broadcastMessage(yellow + player.getName() + gray + " has left the gang!");
								pplayer.sendMessage("You have left " + yellow + pplayer.getGang().getName() + gray + "!");
								pplayer.getGang().removeMember(player.getName());
								if(gangChat.contains(player.getName())){
									gangChat.remove(player.getName());
								}
								if(allyChat.contains(player.getName())){
									allyChat.remove(player.getName());
								}
							}else{
								pplayer.sendError("You must assign leader to another member or disband the gang to leave!");
							}
						}else{
							pplayer.sendError("You aren't in a gang!");
						}
					}else if(args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("c")){
						if(pplayer.hasGang() == true){
							if(!gangChat.contains(player.getName()) && !allyChat.contains(player.getName())){
								gangChat.add(player.getName());
								pplayer.sendMessage("You are now talking in gang chat!");
							}else if(!allyChat.contains(player.getName()) && gangChat.contains(player.getName())){
								gangChat.remove(player.getName());
								allyChat.add(player.getName());
								pplayer.sendMessage("You are now talking in ally chat!");
							}else{
								if(gangChat.contains(player.getName())){
									gangChat.remove(player.getName());
								}
								if(allyChat.contains(player.getName())){
									allyChat.remove(player.getName());
								}
								pplayer.sendMessage("You are now talking in global chat!");
							}
						}else{
							pplayer.sendError("You aren't in a gang!");
						}
					}else if(args[0].equalsIgnoreCase("promote")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.LEADER){
									String member = args[1];
									if(pplayer.getGang().getMembers().contains(member)){
										if(pplayer.getGang().getMemberRank(member) == Rank.MEMBER){
											pplayer.getGang().setMemberRank(member, Rank.MOD);
											if(Bukkit.getPlayer(member) != null && Bukkit.getPlayer(member).isOnline() == true){
												PPlayer bplayer = new PPlayer(Bukkit.getPlayer(member));
												bplayer.sendMessage("You have been promoted to " + yellow + "Mod" + gray + "!");
												member = Bukkit.getPlayer(member).getName();
											}
											pplayer.sendMessage("You have promoted " + yellow + member + gray + " to " + yellow + "Mod" + gray + "!");
										}else{
											pplayer.sendError("This member may not be promoted any more!");
										}
									}else{
										pplayer.sendError("That player is not a member in your gang!");
									}
								}else{
									pplayer.sendError("You aren't a high enough rank to do this!");
								}
							}else{
								pplayer.sendError("You aren't in a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " promote <player>");
						}
					}else if(args[0].equalsIgnoreCase("demote")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.LEADER){
									String member = args[1];
									if(pplayer.getGang().getMembers().contains(member)){
										if(pplayer.getGang().getMemberRank(member) == Rank.MOD){
											pplayer.getGang().setMemberRank(member, Rank.MEMBER);
											if(Bukkit.getPlayer(member) != null && Bukkit.getPlayer(member).isOnline() == true){
												PPlayer bplayer = new PPlayer(Bukkit.getPlayer(member));
												bplayer.sendMessage("You have been demoted to " + yellow + "Member" + gray + "!");
												member = Bukkit.getPlayer(member).getName();
											}
											pplayer.sendMessage("You have demoted " + yellow + member + gray + " to " + yellow + "Member" + gray + "!");
										}else{
											pplayer.sendError("This member may not be demoted any more!");
										}
									}else{
										pplayer.sendError("That player is not a member in your gang!");
									}
								}else{
									pplayer.sendError("You aren't a high enough rank to do this!");
								}
							}else{
								pplayer.sendError("You aren't in a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " demote <player>");
						}
					}else if(args[0].equalsIgnoreCase("leader")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.LEADER){
									String member = args[1];
									if(pplayer.getGang().getMembers().contains(member)){
										pplayer.getGang().setMemberRank(member, Rank.LEADER);
										pplayer.getGang().setMemberRank(player.getName(), Rank.MOD);
										pplayer.getGang().setLeader(member);
										if(Bukkit.getPlayer(member) != null && Bukkit.getPlayer(member).isOnline() == true){
											PPlayer bplayer = new PPlayer(Bukkit.getPlayer(member));
											bplayer.sendMessage("You have been promoted to " + yellow + "Leader" + gray + "!");
											member = Bukkit.getPlayer(member).getName();
										}
										pplayer.sendMessage("You have promoted " + yellow + member + gray + " to " + yellow + "Leader" + gray + "!");
										player.sendMessage(gray + "  You are no longer the leader of the gang!");
									}else{
										pplayer.sendError("That player is not a member in your gang!");
									}
								}else{
									pplayer.sendError("You aren't a high enough rank to do this!");
								}
							}else{
								pplayer.sendError("You aren't in a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " leader <player>");
						}
					}else if(args[0].equalsIgnoreCase("info")){
						if(args.length == 2){
							String name = args[1];
							if(Utils.doesGangExist(name) == true){
								Gang gang = Utils.getGangByName(name);
								pplayer.sendGangInformation(gang);
							}else if(new PPlayer(name).getFile().exists() == true){
								Gang gang = new PPlayer(name).getGang();
								pplayer.sendGangInformation(gang);
							}else{
								pplayer.sendError("That gang does not exist!");
							}
						}else if(args.length == 1){
							if(pplayer.hasGang() == true){
								pplayer.sendGangInformation(pplayer.getGang());
							}else{
								pplayer.sendError("Usage: /" + cmd.getName() + " info <gang>");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " info <gang>");
						}
					}else if(args[0].equalsIgnoreCase("ally")){
						if(args.length == 2){
							final String name = args[1];
							if(Utils.doesGangExist(name) == true || new PPlayer(name).getFile().exists() == true){
								boolean usePlayer = false;
								if(new PPlayer(name).getFile().exists() == true){
									usePlayer = true;
								}
								if(pplayer.hasGang() == true){
									if(pplayer.getGangRank() == Rank.LEADER || pplayer.getGangRank() == Rank.MOD){
										if(!allyInvited.containsKey(pplayer.getGang().getID())){
											Gang gang = null;
											if(usePlayer == false){
												gang = Utils.getGangByName(name);
											}else if(usePlayer == true){
												gang = new PPlayer(name).getGang();
											}
											if(!pplayer.getGang().getAllies().contains(gang.getID())){
												if(!allyInvited.containsKey(gang.getID())){
													allyInvited.put(gang.getID(), pplayer.getGang().getID());
													gang.broadcastModMessage("You have been invited to ally " + yellow + pplayer.getGang().getName() + gray + "!");
													gang.broadcastModMessage(gray + "  Type " + yellow + "/" + cmd.getName() + " ally " + pplayer.getGang().getName() + gray + " to accept!");
													pplayer.sendMessage("You have invited " + yellow + gang.getName() + gray + " to ally your gang!");
													this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
														public void run(){
															if(allyInvited.containsKey(Utils.getGangByName(name).getID())){
																allyInvited.remove(Utils.getGangByName(name).getID());
																if(Utils.doesGangExist(name) == true){
																	Utils.getGangByName(name).broadcastModMessage("Your pending ally invitation has expired!");
																}else if(new PPlayer(name).getFile().exists() == true){
																	new PPlayer(name).getGang().broadcastModMessage("Your pending ally invitation has expired!");
																}
															}
														}
													}, 1200);
												}else{
													pplayer.sendError("That gang already has a pending ally invitation!");
												}
											}else{
												pplayer.sendError("That gang is already allied with your gang!");
											}
										}else if(allyInvited.containsKey(pplayer.getGang().getID())){
											int gangID = allyInvited.get(pplayer.getGang().getID());
											Gang gang = new Gang(gangID);
											if(gang.getName().equalsIgnoreCase(name)){
												pplayer.getGang().addAlly(gangID);
												gang.addAlly(pplayer.getGang().getID());
												pplayer.getGang().broadcastMessage("Your gang is now allied to " + yellow + gang.getName() + gray + "!");
												gang.broadcastMessage("Your gang is now allied to " + yellow + pplayer.getGang().getName() + gray + "!");
												allyInvited.remove(pplayer.getGang().getID());
											}else{
												pplayer.sendError("You need to accept your pending ally invitation!");
											}
										}
									}else{
										pplayer.sendError("You are not a moderator, therefore you may not do this!");
									}
								}else{
									pplayer.sendError("You do not have a gang!");
								}
							}else{
								pplayer.sendError("That gang does not exist!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " ally <gang>");
						}
					}else if(args[0].equalsIgnoreCase("unally")){
						if(args.length == 2){
							String name = args[1];
							if(Utils.doesGangExist(name) == true || new PPlayer(name).getFile().exists() == true){
								boolean usePlayer = false;
								if(new PPlayer(name).getFile().exists() == true){
									usePlayer = true;
								}
								if(pplayer.hasGang() == true){
									if(pplayer.getGangRank() == Rank.LEADER || pplayer.getGangRank() == Rank.MOD){
										Gang gang = null;
										if(usePlayer == false){
											gang = Utils.getGangByName(name);
										}else if(usePlayer == true){
											gang = new PPlayer(name).getGang();
										}
										if(pplayer.getGang().getAllies().contains(gang.getID()) && gang.getAllies().contains(pplayer.getGang().getID())){
											pplayer.getGang().removeAlly(gang.getID());
											gang.removeAlly(pplayer.getGang().getID());
											pplayer.getGang().broadcastMessage("Your gang is no longer allied to " + yellow + gang.getName() + gray + "!");
											gang.broadcastMessage("Your gang is no longer allied to " + yellow + pplayer.getGang().getName() + gray + "!");
										}else{
											pplayer.sendError("That gang is not allied with your gang!");
										}
									}else{
										pplayer.sendError("You are not a moderator, therefore you may not do this!");
									}
								}else{
									pplayer.sendError("You do not have a gang!");
								}
							}else{
								pplayer.sendError("That gang does not exist!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " unally <gang>");
						}
					}else if(args[0].equalsIgnoreCase("rename")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.LEADER){
									String name = args[1];
									if(Utils.doesGangExist(name) == false){
										if(name.length() <= 16 && name.length() >= 4){
											pplayer.getGang().setName(name);
											pplayer.sendMessage("You have renamed the gang to " + yellow + name + gray + "!");
										}else{
											pplayer.sendError("The gang name must be 4-16 characters long!");
										}
									}else{
										pplayer.sendError("That gang name is taken!");
									}
								}else{
									pplayer.sendError("You must be the leader of your gang to rename it!");
								}
							}else{
								pplayer.sendError("You do not have a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " rename <name>");
						}
					}else if(args[0].equalsIgnoreCase("kick")){
						if(args.length == 2){
							if(pplayer.hasGang() == true){
								if(pplayer.getGangRank() == Rank.LEADER || pplayer.getGangRank() == Rank.MOD){
									String name = args[1];
									if(pplayer.getGang().getMembers().contains(name)){
										if(pplayer.getGangRank() == Rank.LEADER){
											pplayer.getGang().removeMember(name);
											if(Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline() == true){
												PPlayer bplayer = new PPlayer(Bukkit.getPlayer(name));
												bplayer.sendMessage("You have been kicked from your gang by " + yellow + player.getName() + gray + "!");
												name = Bukkit.getPlayer(name).getName();
											}
											pplayer.getGang().broadcastMessage(yellow + name + gray + " has been kicked from the gang!");
										}else if(pplayer.getGangRank() == Rank.MOD){
											if(pplayer.getGang().getMemberRank(name) != Rank.LEADER && pplayer.getGang().getMemberRank(name) != Rank.MOD){
												pplayer.getGang().removeMember(name);
												if(Bukkit.getPlayer(name) != null && Bukkit.getPlayer(name).isOnline() == true){
													PPlayer bplayer = new PPlayer(Bukkit.getPlayer(name));
													bplayer.sendMessage("You have been kicked from your gang by " + yellow + player.getName() + gray + "!");
													name = Bukkit.getPlayer(name).getName();
												}
												pplayer.getGang().broadcastMessage(yellow + name + gray + " has been kicked from the gang!");
											}else{
												pplayer.sendError("You may not kick another moderator!");
											}
										}
									}else{
										pplayer.sendError("That player is not in your gang!");
									}
								}else{
									pplayer.sendError("You are not a moderator, therefore you may not do this!");
								}
							}else{
								pplayer.sendError("You do not have a gang!");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " kick <player>");
						}
					}else if(args[0].equalsIgnoreCase("list")){
						List<Gang> gangs = Utils.getGangs();
						int page = 1;
						int gangsPerPage = 9;
						double totalpages = (Utils.roundUp(gangs.size(), gangsPerPage)) / gangsPerPage;
						if(args.length == 1){
							pplayer.sendMessageHeader("Gang List - Page " + page + "/" + (int)totalpages);
							int gangsDisplayed = 0;
							for(Gang g : gangs){
								gangsDisplayed++;
								if(gangsDisplayed <= (page*gangsPerPage) && gangsDisplayed > ((page*gangsPerPage) - gangsPerPage)){
									pplayer.sendMessage(g.getName());
								}else{
									break;
								}
							}
						}else if(args.length == 2){
							if(Utils.isInteger(args[1]) == true){
								page = Integer.parseInt(args[1]);
								if(page > totalpages){
									page = (int) totalpages;
								}
								pplayer.sendMessageHeader("Gang List - Page " + page + "/" + (int)totalpages);
								int gangsDisplayed = 0;
								for(Gang g : gangs){
									gangsDisplayed++;
									if(gangsDisplayed <= (page*gangsPerPage) && gangsDisplayed > ((page*gangsPerPage) - gangsPerPage)){
										pplayer.sendMessage(g.getName());
									}else{
										break;
									}
								}
							}else{
								pplayer.sendError("Usage: /" + cmd.getName() + " list <page>");
							}
						}
					}else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")){
						if(args.length == 1){
							pplayer.sendCommandHelp(1, cmd);
						}else if(args.length == 2){
							if(Utils.isInteger(args[1]) == true){
								int page = Integer.parseInt(args[1]);
								if(page <= 2){
									pplayer.sendCommandHelp(page, cmd);
								}else{
									pplayer.sendCommandHelp(2, cmd);
								}
							}else{
								pplayer.sendError("Usage: /" + cmd.getName() + " help <page>");
							}
						}else{
							pplayer.sendError("Usage: /" + cmd.getName() + " help <page>");
						}
					}else{
						pplayer.sendCommandHelp(1, cmd);
					}
				}
			}
		}
		return false;
	}
}
