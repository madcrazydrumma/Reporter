package net.fissionstudios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {
	
	/*
	 * @author madcrazydrumma
	 * Copyright 2012 - Madcrazydrumma & Fission Studios
	 */
	
	String user;
	String pass;
	String url; //localhost: Your MySQL host, 3306: Your MySQL port (3306 is the main), database: Your MySQL database
	int warning = 0;
	
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		user = this.getConfig().getString("DB.user");
		pass = this.getConfig().getString("DB.pass");
		url = this.getConfig().getString("DB.url");
		saveConfig();
		System.out.println(this + " was enabled successfully!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("report-help")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.BLUE + "You must be a player!");
			}
			sender.sendMessage(ChatColor.GOLD + "== Reporter Help ==");
			sender.sendMessage(ChatColor.GOLD + "== Type /report-help | for help ==");
			sender.sendMessage(ChatColor.GOLD + "== Type /report <player> <reason> | to report player to OP ==");
			sender.sendMessage(ChatColor.GOLD + "== Type /cleardb | to clear the reports (OP Only)");
			sender.sendMessage(ChatColor.GOLD + "== Type /warn <player> | to warn the player (OP Only)");
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("cleardb")) {
			try {
				Connection conn = DriverManager.getConnection(url, user, pass); //Creates the connection
				PreparedStatement query = conn.prepareStatement("DELETE FROM reports"); //Deletes table data
				query.executeUpdate(); //Executes the query
				sender.sendMessage(ChatColor.GOLD + "Reports Cleared");
				query.close(); //Closes the query
				conn.close(); //Closes the connection
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		if(cmd.getName().equalsIgnoreCase("report")) {
			sender.sendMessage(ChatColor.GOLD + "You just sent a report. An Operator will view it shortly.");
			Player target = getServer().getPlayer(args[0]);
			if (target==null){
                sender.sendMessage(ChatColor.RED + "That person doesn't exist! Online players only!");
                return true;
			}
			try {
				Connection conn = DriverManager.getConnection(url, user, pass); //Creates the connection
				PreparedStatement query = conn.prepareStatement("INSERT INTO reports (id, user, reportuser, report, time) VALUES ('0', '"+sender.getName()+"', '"+target.getName()+"', '"+grabStringFromInt(1, args)+"', NOW())");
				query.executeUpdate(); //Executes the query
				query.close(); //Closes the query
				conn.close(); //Closes the connection
			} catch(SQLException e) {
				e.printStackTrace();
			}
			for(Player p: getServer().getOnlinePlayers()) {
				if(p.isOp()) {
					p.sendMessage(ChatColor.DARK_GREEN + sender.getName() + ChatColor.WHITE + " has reported " + ChatColor.DARK_BLUE + target.getName() + ChatColor.WHITE + " for" + ChatColor.DARK_RED + grabStringFromInt(1, args));
					return true;
				}
				return true;
			}
			return true;
		}
		if(cmd.getName().equalsIgnoreCase("warn")) {
			Player target = getServer().getPlayer(args[0]);
			for(Player p: getServer().getOnlinePlayers()) {
				if(p.isOp()) {
					if (target==null){
		                sender.sendMessage(ChatColor.RED + "That person doesn't exist! Online players only!");
		                return true;
					}
					warning++;
					p.sendMessage(ChatColor.DARK_GREEN + sender.getName() + ChatColor.WHITE + " has given a warning to " + ChatColor.DARK_BLUE + target.getName());
					p.sendMessage(ChatColor.DARK_BLUE + target.getName() + ChatColor.WHITE +  "'s warning is now at " + warning);
					return true;
				}
			}
		}
		return true;
	}
	
	public String grabStringFromInt(int start, String args[]) {
		String answer = " ";
		for(int x = start; x < args.length; x++) {
			answer = answer + args[x] + " ";
		}
		return answer;
	}
	
	public void onDisable() {
		System.out.println(this + " was disabled successfully!");
	}
}