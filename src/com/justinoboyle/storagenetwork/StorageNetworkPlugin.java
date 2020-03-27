package com.justinoboyle.storagenetwork;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StorageNetworkPlugin extends JavaPlugin {
	
	public static StorageNetworkPlugin plugin;

	public void onEnable() {
		plugin = this;
		Bukkit.getServer().getPluginManager().registerEvents(new InteractListener(), this);
	}
	
}
