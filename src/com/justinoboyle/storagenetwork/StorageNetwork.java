package com.justinoboyle.storagenetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class StorageNetwork implements Serializable, InventoryHolder {

	private static Material[] include = { Material.CHEST, Material.BLUE_SHULKER_BOX, Material.WHITE_SHULKER_BOX,
			Material.END_GATEWAY, Material.BLUE_GLAZED_TERRACOTTA, Material.END_ROD, Material.TRAPPED_CHEST,
			Material.PURPLE_GLAZED_TERRACOTTA, Material.MAGMA_BLOCK };

	private static BlockFace[] checks = { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
			BlockFace.EAST, BlockFace.WEST };

	private int size;
	public List<String> network = new ArrayList<String>();

	private static Map<String, StorageNetwork> NETWORKS = new HashMap<String, StorageNetwork>();

	private static long LAST_ACCESS;
	private boolean dirty = false;
	private String NETWORK_ID;
	private Inventory i = Bukkit.createInventory(this, 9 * 5, "Network Terminal");

	public static void blockClicked(Block b) {
		if (b.getType() == null)
			return;
		for (Material m : include)
			if (b.getType() == m) {
				new StorageNetwork(b.getLocation()).getItems();
				return;
			}
	}

	public static StorageNetwork getNetwork(Location origin) {
		StorageNetwork n = new StorageNetwork(origin);
		if (n.network.size() < 1)
			return null;
		return n;
	}

	public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<ItemStack>();
		for (String s : network) {
			Block b = StringLocation.destruct(s).getBlock();

			if (b.getType().toString().contains("CHEST")) {
				Container c = (Container) b.getState();
				b.getLocation().getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 1);
				if (c.getInventory() != null & c.getInventory().getContents() != null)
					for (ItemStack i : c.getInventory().getContents()) {
						if (i != null && i.getType() != null)
							items.add(i);
					}
			}
		}
		Bukkit.broadcastMessage(items.size() + " stacks");
		return items;
	}

	private StorageNetwork(Location origin) {
		if (!shouldBePartOfNetwork(origin.getBlock()))
			return;
		update(origin.getBlock());
		for (BlockFace rel : checks)
			updateRecur(origin.getBlock().getRelative(rel));
		for (String s : network) {
			if (StringLocation.destruct(s).getBlock().getType() == Material.END_ROD)
				return;
		}
		network.clear();
	}

	private void update(Block origin) {
		updateRecur(origin);
	}

	private void markDirty() {
		this.dirty = true;
	}

	private void addToNetwork(Block b) {
		network.add(StringLocation.construct(b.getLocation()));
	}

	private void updateRecur(Block origin) {
		if (isPartOfNetwork(origin))
			return;
		if (shouldBePartOfNetwork(origin)) {
			addToNetwork(origin);
		} else
			return;
		for (BlockFace rel : checks)
			updateRecur(origin.getRelative(rel));

	}

	private int getSize() {
		return size;
	}

	private boolean isPartOfNetwork(Block b) {
		return network.contains(StringLocation.construct(b.getLocation()));
	}

	public void updateBlock(Block b) {
		if (!shouldBePartOfNetwork(b)) {
			if (isPartOfNetwork(b)) {
				markDirty();
				return;
			}
		}
		for (BlockFace rel : checks)
			if (shouldBePartOfNetwork(b.getRelative(rel)))
				if (!isPartOfNetwork(b.getRelative(rel))) {
					update(b);
					return;
				}

	}

	private static boolean shouldBePartOfNetwork(Block b) {
		for (Material m : include)
			if (b.getType() == m)
				return true;
		return false;
	}

	private static boolean shouldBePartOfNetwork(Location l) {
		return shouldBePartOfNetwork(l.getBlock());
	}

	public void replaceItem(Inventory i, int slot, Material type) {
		for (String s : network) {
			Block b = StringLocation.destruct(s).getBlock();

			if (b.getType().toString().contains("CHEST")) {
				Container c = (Container) b.getState();
				b.getLocation().getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, 1);
				if (c.getInventory() != null & c.getInventory().getContents() != null)
					for (ItemStack i2 : c.getInventory().getContents()) {
						if (i2 != null && i2.getType() != null && i2.getType() == type) {
							i.setItem(slot, i2);
							c.getInventory().removeItem(i2);
							return;
						}
					}
			}
		}
	}

	@Override
	public Inventory getInventory() {
		i.clear();
		for (String s : network) {
			Block b = StringLocation.destruct(s).getBlock();

			if (b.getType() == Material.WHITE_SHULKER_BOX) {
				Bukkit.broadcastMessage("A");
				Container c = (Container) b.getState();
				Material m = Material.BARRIER;
				String name = "Untitled Container";
				if (c.getCustomName() != null) {
					name = c.getCustomName();
				}
				if (c.getInventory() != null && c.getInventory().getItem(0) != null) {
					m = c.getInventory().getItem(0).getType();
				}
				ItemStack it = new ItemStack(m);
				String lore = ChatColor.DARK_PURPLE + "" + ChatColor.MAGIC + s;
				List<String> n2 = new ArrayList<String>();
				n2.add(lore);
				it.setLore(n2);
				ItemMeta im = it.getItemMeta();
				im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
				im.setLore(n2);
				it.setItemMeta(im);
				i.addItem(it);
			}
		}
		return i;
	}

}
