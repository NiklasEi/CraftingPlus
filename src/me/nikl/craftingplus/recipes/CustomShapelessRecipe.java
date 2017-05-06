package me.nikl.craftingplus.recipes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Map;
import java.util.Set;

/**
 * Created by niklas on 1/15/17.
 *
 */
public class CustomShapelessRecipe extends ShapelessRecipe implements ICustomRecipe {
	private Map<ItemStack, Set<ItemStack>> recipeMap;
	private Map<String, ItemStack> labels;
	private boolean overwritingVanillaRecipe = false;
	private ShapelessRecipe vanilla;
	
	
	public CustomShapelessRecipe(ItemStack result) {
		super(result);
	}
	
	@Override
	public Recipe getVanillaRecipe() {
		return vanilla;
	}
	
	@Override
	public void setVanilla(Recipe vanilla) {
		this.vanilla = (ShapelessRecipe) vanilla;
	}
	
	@Override
	public boolean isOverwritingVanillaRecipe() {
		return overwritingVanillaRecipe;
	}
	
	@Override
	public void setOverwritingVanillaRecipe(boolean overwritingVanillaRecipe) {
		this.overwritingVanillaRecipe = overwritingVanillaRecipe;
	}
	
}
