package net.fissionstudios;

import com.lordralex.SQL.SQL;
import net.fissionstudios.commands.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Reporter extends JavaPlugin {

    /*
     * @author madcrazydrumma Copyright 2012 - Madcrazydrumma & Fission Studios
     */
    int warning = 0;

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        String user = this.getConfig().getString("DB.user");
        String pass = this.getConfig().getString("DB.pass");
        String host = this.getConfig().getString("DB.host");
        String port = this.getConfig().getString("DB.port");
        String db = this.getConfig().getString("DB.db");
        SQL.connect(host, port, db, user, pass);
        saveConfig();
        CommandExecutor cmdEx = new CommandExecutor(this);
        getCommand("report-help").setExecutor(cmdEx);
        getCommand("report").setExecutor(cmdEx);
        getCommand("cleardb").setExecutor(cmdEx);
        getCommand("warn").setExecutor(cmdEx);
        System.out.println(this + " was enabled successfully!");
    }

    @Override
    public void onDisable() {
        System.out.println(this + " was disabled successfully!");
    }
}