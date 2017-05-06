package me.nikl.craftingplus.nmsutil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public interface INMSUtil {

	
	void updateTitle(Player player, String newTitle);
	
	ItemStack removeGlow(ItemStack item);
	
	ItemStack addGlow(ItemStack item);
}
