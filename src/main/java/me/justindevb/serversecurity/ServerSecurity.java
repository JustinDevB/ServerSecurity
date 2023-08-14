package me.justindevb.serversecurity;

import me.justindevb.serversecurity.acl.AccessControlList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ServerSecurity extends JavaPlugin {
    private static ServerSecurity instance;

    @Override
    public void onEnable() {
        instance = this;
        registerListeners();
    }


    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new AccessControlList(this), this);
    }

    /**
     * Log a message to console
     * @param string Message to log
     * @param severe If message should be logged as severe
     */
    public void log(String string, boolean severe) {
        if (severe)
            getLogger().log(Level.SEVERE, string);
        else
            getLogger().log(Level.INFO, string);
    }


    public static ServerSecurity getInstance() {
        return instance;
    }

}
