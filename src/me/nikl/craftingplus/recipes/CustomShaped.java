package me.nikl.craftingplus.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by niklas on 1/3/17.
 *
 */
public class CustomShaped extends ShapedRecipe implements ICustomRecipe {
	private Map<ItemStack, Map<Character, ItemStack>> recipeMap;
	private Map<String, ItemStack> labels;
	private boolean overwritingVanillaRecipe = false;
	private ShapedRecipe vanilla;
	
	public CustomShaped(ItemStack result) {
		super(result);
		labels = new HashMap<>();
		recipeMap = new HashMap<>();
	}
	
	public void addLabel(String label, ItemStack resultItem){
		labels.put(label, resultItem);
	}
	
	
	public boolean setIngredients(Map<Character, ItemStack> ingredients, ItemStack result){
		recipeMap.putIfAbsent(result, ingredients);
		if(recipeMap.size()==1) {
			for(Character key: ingredients.keySet()){
				if(ingredients.get(key) == null){
					continue;
				}
				this.setIngredient(key, ingredients.get(key).getData());
			}
		}
		return true;
	}
	
	
	public Map<Character, ItemStack> getIngredientMap() {
		/*
		HashMap result = new HashMap();
		Iterator var3 = this.recipeMap.entrySet().iterator();
		
		while(var3.hasNext()) {
			Map.Entry ingredient = (Map.Entry)var3.next();
			if(ingredient.getValue() == null) {
				result.put((Character)ingredient.getKey(), (Object)null);
			} else {
				result.put((Character)ingredient.getKey(), ((ItemStack)ingredient.getValue()).clone());
			}
		}*/
		HashMap result = new HashMap(recipeMap.get(recipeMap.keySet().iterator().next()));
		
		return result;
	}
	
	public Map<ItemStack, Map<Character, ItemStack>> getRecipeMap(){
		return this.recipeMap;
	}
	
	@Override
	public Recipe getVanillaRecipe() {
		return vanilla;
	}
	
	@Override
	public void setVanilla(Recipe vanilla) {
		this.vanilla = (ShapedRecipe) vanilla;
	}
	
	public void setOverwritingVanillaRecipe(boolean overwritingVanillaRecipe){
		this.overwritingVanillaRecipe = overwritingVanillaRecipe;
	}
	
	public boolean isOverwritingVanillaRecipe() {
		return this.overwritingVanillaRecipe;
	}
	
	public Map<String,ItemStack> getLabels() {
		return labels;
	}
}
