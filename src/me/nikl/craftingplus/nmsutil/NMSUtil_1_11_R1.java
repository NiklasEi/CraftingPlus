package me.nikl.craftingplus.nmsutil;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by niklas on 11/19/16.
 *
 *
 */
public class NMSUtil_1_11_R1 implements INMSUtil {
	
	@Override
	public void updateTitle(Player player, String newTitle) {
		EntityPlayer ep = ((CraftPlayer)player).getHandle();
		PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, "minecraft:chest", new ChatMessage(newTitle), player.getOpenInventory().getTopInventory().getSize());
		ep.playerConnection.sendPacket(packet);
		ep.updateInventory(ep.activeContainer);
	}
	
	@Override
	public ItemStack removeGlow(ItemStack item) {
		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (nmsStack.hasTag()) {
			tag = nmsStack.getTag();
			tag.remove("ench");
			nmsStack.setTag(tag);
			return CraftItemStack.asCraftMirror(nmsStack);
		}
		return item;
	}
	
	@Override
	public ItemStack addGlow(ItemStack item){
		if(item == null) return null;
		item.addUnsafeEnchantment(Enchantment.LUCK, 1);
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}
}
