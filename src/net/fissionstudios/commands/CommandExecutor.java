/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fissionstudios.commands;

import com.lordralex.SQL.SQL;
import com.lordralex.SQL.SQLDataException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.fissionstudios.Reporter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Joshua & Mad
 */
public class CommandExecutor implements org.bukkit.command.CommandExecutor {

    Reporter plugin;

    public CommandExecutor(Reporter aPlugin) {
        plugin = aPlugin;
    }

    //permissions:
    /*
     * report.warn report.cleardb report.help report.report
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("report-help") && sender.hasPermission("report.help")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.BLUE + "You must be a player!");
            }
            sender.sendMessage(ChatColor.GOLD + "== Reporter Help ==");
            if (sender.hasPermission("report.help")) {
                sender.sendMessage(ChatColor.GOLD + "== Type /report-help | for help ==");
            }
            if (sender.hasPermission("report.report")) {
                sender.sendMessage(ChatColor.GOLD + "== Type /report <player> <reason> | to report player to OP ==");
            }
            if (sender.hasPermission("report.cleardb")) {
                sender.sendMessage(ChatColor.GOLD + "== Type /cleardb | to clear the reports (OP Only)");
            }
            if (sender.hasPermission("report.warn")) {
                sender.sendMessage(ChatColor.GOLD + "== Type /warn <player> | to warn the player (OP Only)");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("cleardb") && sender.hasPermission("report.cleardb")) {
            SQL.runCommand("DELETE FROM reports"); //Deletes table data
            sender.sendMessage(ChatColor.GOLD + "Reports Cleared");
            
        }
        if (cmd.getName().equalsIgnoreCase("report") && sender.hasPermission("report.report")) {
            sender.sendMessage(ChatColor.GOLD + "You just sent a report. An Operator will view it shortly.");
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That person doesn't exist! Online players only!");
                return true;
            }
            SQL.runCommand("INSERT INTO reports (id, user, reportuser, report, time) VALUES ('0', '" + sender.getName() + "', '" + target.getName() + "', '" + grabStringFromInt(1, args) + "', NOW())");
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.isOp()) {
                    p.sendMessage(ChatColor.DARK_GREEN + sender.getName() + ChatColor.WHITE + " has reported " + ChatColor.DARK_BLUE + target.getName() + ChatColor.WHITE + " for" + ChatColor.DARK_RED + grabStringFromInt(1, args));
                    return true;
                }
                return true;
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("warn") && sender.hasPermission("report.warn")) {
            Player target = plugin.getServer().getPlayer(args[0]);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.isOp()) {
                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "That person doesn't exist! Online players only!");
                        return true;
                    }
                    p.sendMessage(ChatColor.DARK_GREEN + sender.getName() + ChatColor.WHITE + " has given a warning to " + ChatColor.DARK_BLUE + target.getName());
                    try {
                        SQL.addWarn(target.getName());
                        p.sendMessage(ChatColor.DARK_BLUE + target.getName() + ChatColor.WHITE + "'s warning is now at " + SQL.getWarns(target.getName()));
                    } catch (SQLDataException ex) {
                        Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return true;
                }
            }
        }
        if (!sender.hasPermission("report." + cmd.getName().toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
        return true;
    }

    public String grabStringFromInt(int start, String args[]) {
        String answer = " ";
        for (int x = start; x < args.length; x++) {
            answer = answer + args[x] + " ";
        }
        return answer;
    }
}
