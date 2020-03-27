package com.justinoboyle.storagenetwork;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InteractListener implements Listener, InventoryHolder {

	@EventHandler
	public void click(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		if (e.getClickedBlock() == null)
			return;

		StorageNetwork network = StorageNetwork.getNetwork(e.getClickedBlock().getLocation());
		if (network == null) {
			return;
		}
		
		if(e.getClickedBlock().getType() == Material.END_GATEWAY) {
			p.openInventory(network.getInventory());
		}

		if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.COMPASS) {
			e.setCancelled(true);
			Inventory i = Bukkit.createInventory(this, 9 * 4,
					ChatColor.translateAlternateColorCodes('&', "n = &d&l" + network.network.size()));

			for (String s : network.network) {
				Block b = StringLocation.destruct(s).getBlock();
				i.addItem(new ItemStack(b.getType()));
			}
			p.openInventory(i);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInventoryViewClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() instanceof StorageNetwork) {
			if(event.getCurrentItem() != null) {
				if(event.getCurrentItem().getItemMeta().hasLore()) {
					String loc = event.getCurrentItem().getItemMeta().getLore().get(0);
					loc = ChatColor.stripColor(loc);
					Block b = StringLocation.destruct(loc).getBlock();
					if(b.getType() == Material.WHITE_SHULKER_BOX) {
						Container c = (Container) b.getState();
						event.getWhoClicked().openInventory(c.getInventory());
						
					}
				}
			}
			event.setCancelled(true);
			return;
		}
		if (event.getClickedInventory().getHolder() == this) {
			event.setCancelled(true);
			return;
		}
		if (event.getClickedInventory().getType() == InventoryType.SHULKER_BOX) {
			if (event.getClickedInventory().getLocation().getBlock().getType() == Material.WHITE_SHULKER_BOX) {
				StorageNetwork n = StorageNetwork.getNetwork(event.getClickedInventory().getLocation());
				if (event.getCurrentItem() != null) {
					ItemStack current = event.getCurrentItem();
					Material type = current.getType();
					Inventory i = event.getClickedInventory();
					int slot = event.getSlot();
					new BukkitRunnable() {
						public void run() {
							if (i.getItem(slot) == null || i.getItem(slot).getType() == Material.AIR) {
								n.replaceItem(i, slot, type);
							}
						}
					}.runTaskLater(StorageNetworkPlugin.plugin, 1);

				}
			}
		}
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		Bukkit.broadcastMessage(e.getBlockAgainst().getType() + "");
		if (e.getBlockAgainst().getType() == Material.BLUE_GLAZED_TERRACOTTA) {
			if (e.getBlockPlaced().getType() == Material.BLACK_SHULKER_BOX) {

				e.getBlock().setType(Material.END_GATEWAY);
			}
		}
	}

	@EventHandler
	public void brk(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.BLUE_GLAZED_TERRACOTTA) {
			for (BlockFace f : BlockFace.values()) {
				if (e.getBlock().getRelative(f).getType() == Material.END_GATEWAY) {
					e.getBlock().getRelative(f).breakNaturally();
					e.getBlock().getDrops().add(new ItemStack(Material.BLACK_SHULKER_BOX));
				}
			}
		}
	}

	@Override
	public Inventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}
}
