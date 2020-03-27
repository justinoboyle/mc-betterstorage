package com.justinoboyle.storagenetwork;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class StringLocation {

	public static String construct(Location l) {
		return l.getWorld().getName() + "@" + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getPitch() + "," + l.getYaw();
	}
	
	public static Location destruct(String s) {
		String[] a1 = s.split("@");
		World w = Bukkit.getWorld(a1[0]);
		String[] coords = a1[1].split(",");
		Location l = new Location(w, Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), Double.parseDouble(coords[2]));
		l.setPitch(Float.parseFloat(coords[3]));
		l.setYaw(Float.parseFloat(coords[4]));
		return l;
	}
}
