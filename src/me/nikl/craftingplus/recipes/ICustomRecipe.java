package me.nikl.craftingplus.recipes;

import org.bukkit.inventory.Recipe;

/**
 * Created by niklas on 1/14/17.
 *
 */
public interface ICustomRecipe{
	
	Recipe getVanillaRecipe();
	
	void setVanilla(Recipe vanilla);
	
	boolean isOverwritingVanillaRecipe();
	
	void setOverwritingVanillaRecipe(boolean overwritingVanillaRecipe);
	
	
}
