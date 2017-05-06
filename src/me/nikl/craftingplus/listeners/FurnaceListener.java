package me.nikl.craftingplus.listeners;

import me.nikl.craftingplus.Main;
import me.nikl.craftingplus.recipes.CustomFurnaceRecipe;
import me.nikl.craftingplus.recipes.RecipesManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by niklas on 1/17/17.
 *
 */
public class FurnaceListener implements Listener{
	private Main plugin;
	private RecipesManager recipesManager;
	
	public FurnaceListener(Main plugin){
		this.plugin = plugin;
		this.recipesManager = plugin.getRecipesManager();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onFurnaceBurn(FurnaceSmeltEvent event){
		plugin.debugMessage("FurnaceSmeltEvent called... testing for custom recipe");
		if(!recipesManager.isCustomFurnaceRecipe(event.getSource())) return;
		plugin.debugMessage("Found custom recipe!");
		CustomFurnaceRecipe recipe = recipesManager.getCustomFurnaceRecipe(event.getSource());
		
		
		if(recipe.getResults().keySet().size() == 1 && !recipe.isOverwritingVanillaRecipe()){
			plugin.debugMessage("should be correct result already since there is only one recipe in the object");
			return;
		}
		
		// look for correct result
		
		Map<String, ItemStack> sources = recipe.getSources();
		String matchedKey = null;
		for(String key: sources.keySet()){
			if(recipesManager.isSameItem(sources.get(key), event.getSource())){
				matchedKey = key;
				plugin.debugMessage("found matching CustomFurnaceRecipe");
			}
		}
		
		if(matchedKey!=null){
			event.setResult(recipe.getResults().get(matchedKey));
			plugin.debugMessage("result was set accordingly");
		} else if(recipe.isOverwritingVanillaRecipe()){
			event.setResult(recipe.getVanillaRecipe().getResult());
			plugin.debugMessage("falling back to vanilla recipe");
		} else {
			event.setResult(null);
			event.setCancelled(true);
			plugin.debugMessage("no match! cancelled the event and set result to null");
		}
	}
}
