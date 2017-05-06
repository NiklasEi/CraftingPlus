package me.nikl.craftingplus.listeners;

import me.nikl.craftingplus.Main;
import me.nikl.craftingplus.recipes.CustomShaped;
import me.nikl.craftingplus.recipes.CustomShapelessRecipe;
import me.nikl.craftingplus.recipes.ICustomRecipe;
import me.nikl.craftingplus.recipes.RecipesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

/**
 * Created by niklas on 1/4/17.
 *
 *
 */
public class PrepareCustomCraft implements Listener {
	private Main plugin;
	private RecipesManager recipesManager;
	
	public PrepareCustomCraft(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
		this.recipesManager = plugin.getRecipesManager();
	}
	
	@EventHandler
	public void onCustomCraftPrepare(PrepareItemCraftEvent event) {
		if (event.isRepair())
			return;
		plugin.debugMessage("Called PrepareItemCraftEvent... testing for custom recipe");
		CustomShaped customShaped = recipesManager.getCustomShaped(event.getInventory());
		if (customShaped != null) {
			
			
			plugin.debugMessage("Found CustomShaped recipe");
			plugin.debugMessage(customShaped.getIngredientMap().values().toString());
			
			Map<ItemStack, Map<Character, ItemStack>> recipeMap = customShaped.getRecipeMap();
			Map<Character, ItemStack> ingredients;
			CraftingInventory inventory = event.getInventory();
			recipesLoop:
			for (ItemStack resultItem : recipeMap.keySet()) {
				ingredients = recipeMap.get(resultItem);
				/*
				if(inventory.getSize() != ingredients.keySet().size()){
					event.getInventory().setResult(null);
					plugin.debugMessage("recipe had a different size then the matrix");
					//ToDo: handle the case matrix 3x3, recipe 2x2 (only four different positions possible)
				}*/
				nextChar:
				for (Character key : ingredients.keySet()) {
					int keyInt = Integer.parseInt(key.toString());
					
					if (inventory.getItem(keyInt) == null && ingredients.get(key) == null) {
						plugin.debugMessage("key: " + key + " both null");
						continue nextChar;
					}
					if (inventory.getItem(keyInt) == null || ingredients.get(key) == null) {
						plugin.debugMessage("key: " + key + " one null");
						continue recipesLoop;
					}
					if (!recipesManager.isSameItem(inventory.getItem(keyInt), ingredients.get(key))) {
						plugin.debugMessage("found different itemStack... skipping recipe");
						continue recipesLoop;
					}
				}
				// all itemStacks passes comparison of Display name and lore
				event.getInventory().setResult(resultItem);
				plugin.debugMessage("found match and set result accordingly");
				return;
			}
			
			// no match found
			// falling back on possible vanilla recipe
			
			override:
			if (customShaped.isOverwritingVanillaRecipe()) {
				if (recipesManager.forceNoSpecialDisplayNameAndNoLore) {
					for (ItemStack item : event.getInventory().getMatrix()) {
						if (item.getItemMeta() == null)
							continue;
						if (item.getItemMeta().getDisplayName() != null)
							break override;
						if (item.getItemMeta().getLore() != null)
							break override;
					}
				}
				plugin.debugMessage("falling back to overwritten vanilla recipe");
				event.getInventory().setResult(customShaped.getVanillaRecipe().getResult());
				return;
			}
			plugin.debugMessage("no match found. setting result to null");
			event.getInventory().setResult(null);
		} else { // not a customShaped recipe
			if (recipesManager.forceNoSpecialDisplayNameAndNoLore) {
				for (ItemStack item : event.getInventory().getMatrix()) {
					if (item.getItemMeta() == null)
						continue;
					if (item.getItemMeta().getDisplayName() != null || item.getItemMeta().getLore() != null){
						event.getInventory().setResult(null);
						return;
					}
				}
			}
		}
	}
}