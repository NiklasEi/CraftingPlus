package me.nikl.craftingplus.recipes;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niklas on 1/15/17.
 *
 *
 */
public class CustomFurnaceRecipe extends FurnaceRecipe implements ICustomRecipe{
	private FurnaceRecipe vanilla;
	private boolean overwritesVanilla = false;
	
	private Map<String, ItemStack> results;
	private Map<String, ItemStack> sources;
	private Map<String, Float> experienceMap;
	
	public CustomFurnaceRecipe(ItemStack result, MaterialData source, float experience) {
		super(result, source, experience);
		this.results = new HashMap<>();
		this.experienceMap = new HashMap<>();
		this.sources = new HashMap<>();
		
		
	}
	
	@Override
	public Recipe getVanillaRecipe() {
		return vanilla;
	}
	
	public void addRecipe(String label, ItemStack result, ItemStack source, float experience){
		this.results.put(label, result);
		this.sources.put(label, source);
		this.experienceMap.put(label, experience);
	}
	
	@Override
	public void setVanilla(Recipe vanilla) {
		this.vanilla = (FurnaceRecipe) vanilla;
	}
	
	@Override
	public boolean isOverwritingVanillaRecipe() {
		return this.overwritesVanilla;
	}
	
	@Override
	public void setOverwritingVanillaRecipe(boolean overwritingVanillaRecipe) {
		this.overwritesVanilla = overwritingVanillaRecipe;
	}
	
	public Map<String, ItemStack> getResults(){
		return this.results;
	}
	
	public Map<String, ItemStack> getSources(){
		return this.sources;
	}
	
	public Map<String, Float> getExperienceMap(){
		return this.experienceMap;
	}
}
