package me.nikl.craftingplus.recipes;

import me.nikl.craftingplus.Main;
import me.nikl.craftingplus.nmsutil.INMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by niklas on 1/3/17.
 *
 */
public class RecipesManager {
	private Main plugin;
	private INMSUtil nmsUtil;
	
	private FileConfiguration shaped2x2Config;
	private FileConfiguration shaped3x3Config;
	private FileConfiguration shapelessConfig;
	private FileConfiguration furnaceConfig;
	
	private Set<CustomShaped> shaped2x2Recipes;
	private Set<CustomShaped> shaped3x3Recipes;
	private Set<CustomShapelessRecipe> shapelessRecipes;
	private Set<CustomFurnaceRecipe> furnaceRecipes;
	
	
	public boolean forceNoSpecialDisplayNameAndNoLore;
	
	public RecipesManager(Main plugin){
		this.plugin = plugin;
		nmsUtil = plugin.getNmsUtil();
		
		loadFiles();
		
		shaped2x2Recipes = new HashSet<>();
		shaped3x3Recipes = new HashSet<>();
		shapelessRecipes = new HashSet<>();
		furnaceRecipes = new HashSet<>();
		
		loadAll();
		
		forceNoSpecialDisplayNameAndNoLore = plugin.getConfig().getBoolean("behaviour.forceNoSpecialDisplayNameAndNoLore", false);
		//registerAll();
	}
	
	private void loadAll() {
		// load CustomShaped3x3 recipes
		CustomShaped customShaped3x3;
		for(String key : shaped3x3Config.getKeys(false)){
			
			customShaped3x3 = loadShapedRecipe(key, false);
			if(customShaped3x3 == null){
				Bukkit.getLogger().log(Level.WARNING, "Could not load 3x3shaped recipe at: " + key);
				continue;
			}
			if(customShaped3x3.getRecipeMap().size()==1){
				shaped3x3Recipes.add(customShaped3x3);
				Bukkit.addRecipe(customShaped3x3);
				plugin.debugMessage("Recipe " + key + " was registered!");
			}
			if(Main.debug) {
				Bukkit.getConsoleSender().sendMessage("Added recipe " + customShaped3x3.toString() + " from: " + key);
				Bukkit.getConsoleSender().sendMessage( "ingredients: " + customShaped3x3.getIngredientMap().toString());
				for (int i = 0; i < customShaped3x3.getShape().length; i++) {
					Bukkit.getConsoleSender().sendMessage("shape: " + customShaped3x3.getShape()[i]);
				}
			}
		}
		// load CustomShaped2x2 recipes
		CustomShaped customShaped2x2;
		for(String key : shaped2x2Config.getKeys(false)){
			
			customShaped2x2 = loadShapedRecipe(key, true);
			if(customShaped2x2 == null){
				Bukkit.getLogger().log(Level.WARNING, "Could not load 2x2shaped recipe at: " + key);
				continue;
			}
			if(customShaped2x2.getRecipeMap().size()==1){
				shaped2x2Recipes.add(customShaped2x2);
				Bukkit.addRecipe(customShaped2x2);
				plugin.debugMessage("Recipe " + key + " was registered!");
			}
			if(Main.debug) {
				Bukkit.getConsoleSender().sendMessage("Added recipe " + customShaped2x2.toString() + " from: " + key);
				Bukkit.getConsoleSender().sendMessage("ingredients: " + customShaped2x2.getIngredientMap().toString());
				for (int i = 0; i < customShaped2x2.getShape().length; i++) {
					Bukkit.getConsoleSender().sendMessage("shape: " + customShaped2x2.getShape()[i]);
				}
			}
		}
	}
	
	/**
	 * Register all CustomRecipes
	 */
	private void registerAll(){
		plugin.debugMessage("Register all CustomRecipes");
		shaped3x3Recipes.forEach(Bukkit::addRecipe);
		shaped2x2Recipes.forEach(Bukkit::addRecipe);
		shapelessRecipes.forEach(Bukkit::addRecipe);
		furnaceRecipes.forEach(Bukkit::addRecipe);
	}
	
	/**
	 * Unregister all custom recipes
	 */
	public void unregisterAll() {
		Iterator<Recipe> iterator = plugin.getServer().recipeIterator();
		while(iterator.hasNext()){
			Recipe recipe = iterator.next();
			if(recipe instanceof ICustomRecipe) iterator.remove();
		}
	}
	
	/**
	 * Unregister all recipes
	 */
	public void shutDown() {
		plugin.debugMessage("shutdown called: unregister all recipes");
		unregisterAll();
	}
	
	private CustomFurnaceRecipe loadFurnaceRecipe(String path){
		if(!furnaceConfig.isConfigurationSection(path)) return null;
		ConfigurationSection recipeSection = furnaceConfig.getConfigurationSection(path);
		
		// ToDo: test for sections set and not null materials
		
		MaterialData resultMaterial = getMaterial(recipeSection.getString("result.material"));
		MaterialData sourceMaterial = getMaterial(recipeSection.getString("source.material"));
		
		
		// ToDo: experience load needed
		CustomFurnaceRecipe toReturn = new CustomFurnaceRecipe(resultMaterial.toItemStack(), sourceMaterial,0f);
		return toReturn;
	}
	
	
	/**
	 * Loads a CustomShaped recipe from the given path. The Ingredient Map is compared to all registered recipes.
	 * If a similar CustomShaped recipe is found the new one is added to the already registered recipe. Similar Vanilla recipes are also handled and saved in the object.
	 * @param path path to load the recipe from
	 * @param small 2x2 or 3x3 recipe
	 * @return CustomShaped object which includes the newly loaded recipe
	 */
	private CustomShaped loadShapedRecipe(String path, boolean small){
		
		ConfigurationSection recipeSection;
		if(!small) {
			if (!shaped3x3Config.isConfigurationSection(path))
				return null;
			recipeSection = shaped3x3Config.getConfigurationSection(path);
		} else {
			if (!shaped2x2Config.isConfigurationSection(path))
				return null;
			recipeSection = shaped2x2Config.getConfigurationSection(path);
		}
		
		if(!recipeSection.isSet("resultItem.material")){
			if(Main.debug) Bukkit.getLogger().log(Level.WARNING, "result item missing in: " + path + "resultItem.material");
			return null;
		}
		if(!recipeSection.isSet("recipe.1") && !recipeSection.isSet("recipe.2") && !recipeSection.isSet("recipe.3") && !recipeSection.isSet("recipe.4") && !recipeSection.isSet("recipe.5") && !recipeSection.isSet("recipe.6") && !recipeSection.isSet("recipe.7") && !recipeSection.isSet("recipe.8") && !recipeSection.isSet("recipe.9")){
			if(Main.debug) Bukkit.getLogger().log(Level.WARNING, "no item for the recipe set in: " + path + "recipe.");
			return null;
		}
		MaterialData resultMaterial = getMaterial(recipeSection.getString("resultItem.material"));
		
		if(resultMaterial == null){
			if(Main.debug) Bukkit.getLogger().log(Level.WARNING, "failed to load valid result material from: " + path + "resultItem.material");
			return null;
		}
		ItemStack resultItem = resultMaterial.toItemStack();
		resultItem.setAmount(1);
		
		if(recipeSection.getBoolean("resultItem.glow"))resultItem = nmsUtil.addGlow(resultItem);
		ItemMeta meta = resultItem.getItemMeta();
		if(recipeSection.isString("resultItem.displayName")){
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', recipeSection.getString("resultItem.displayName")));
		}
		if(recipeSection.isList("resultItem.lore")){
			ArrayList<String> lore = new ArrayList<>(recipeSection.getStringList("resultItem.lore"));
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
			meta.setLore(lore);
		}
		if(Main.debug)Bukkit.getConsoleSender().sendMessage("Result is: " + resultItem.toString());
		resultItem.setItemMeta(meta);
		
		// ConfigurationSection exists and resultItem was loaded
		
		CustomShaped toReturn = null;
		
		
		char key;
		
		ConfigurationSection keySection;
		Map<Character, ItemStack> ingredients = new HashMap<>();
		for(int i = 1; i < (small? 5 : 10);i++){
			key = Integer.toString(i).charAt(0);
			if(!recipeSection.isConfigurationSection("recipe." + key)){
				if(recipeSection.isString("recipe."+key) && recipeSection.getString("recipe."+key).length() == 1){
					if (Main.debug)
						Bukkit.getConsoleSender().sendMessage("found link to key: " + recipeSection.getString("recipe."+key));
					// ToDo test whether this key is set/ was already loaded
					keySection = recipeSection.getConfigurationSection("recipe." + recipeSection.getString("recipe."+key));
					
				} else {
					if (Main.debug)
						Bukkit.getConsoleSender().sendMessage("setting key " + key + " to null and skipping");
					ingredients.put(key, null);
					continue;
				}
			} else {
				keySection = recipeSection.getConfigurationSection("recipe." + key);
			}
			if (Main.debug)Bukkit.getConsoleSender().sendMessage("reading from: " +"recipe." + key + ".material"+ "       found: "+ keySection.getString("material"));
			MaterialData mat = getMaterial(keySection.getString("material"));
			if(mat == null){
				if(Main.debug) Bukkit.getLogger().log(Level.WARNING, "Material " + key + " in recipe " + path + " could not be loaded");
				return null;
			}
			if(Main.debug) Bukkit.getConsoleSender().sendMessage("setting " + key + " to " + mat.toString());
			ItemStack item = mat.toItemStack();
			item.setAmount(1);
			meta = item.getItemMeta();
			if(keySection.isString("displayName")){
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', keySection.getString("displayName")));
			}
			if(keySection.isList("lore")){
				ArrayList<String> lore = new ArrayList<>(keySection.getStringList("lore"));
				for (int j = 0; j < lore.size(); j++) {
					lore.set(j, ChatColor.translateAlternateColorCodes('&', lore.get(j)));
				}
				meta.setLore(lore);
			}
			item.setItemMeta(meta);
			
			ingredients.put(key, item);
		}
		// test for existing custom recipe or vanilla recipe with same IngredientMap
		Iterator<Recipe> iterator = Bukkit.recipeIterator();
		
		ItemStack[] items = new ItemStack[ingredients.keySet().size()];
		plugin.debugMessage("loaded " + items.length + " items");
		for(int a = 0; a < items.length; a++){
			items[a] = ingredients.get(Integer.toString(a+1).charAt(0));
			if(items[a]!= null)plugin.debugMessage("items[" + a + "] is " + items[a].toString());
		}
		
		if(getCustomShaped(items) != null){
			toReturn = getCustomShaped(items);
			toReturn.setIngredients(ingredients, resultItem);
			toReturn.addLabel(path, resultItem);
			return toReturn;
		}
		
		ShapedRecipe matched = null;
		
		
		plugin.debugMessage("looking for vanilla recipe...");
		int blub = 0;
		iterate:
		while(iterator.hasNext()){
			Recipe currentRecipe = iterator.next();
			if(!(currentRecipe instanceof ShapedRecipe)) continue iterate;
			blub++;
			if(blub > 10) break;
			ShapedRecipe currentShaped = (ShapedRecipe) currentRecipe;
			String[] shape = currentShaped.getShape();
			
			plugin.debugMessage("testing recipe for: " + currentRecipe.getResult().toString());
			/* test shape
			   only let 3x3 and 2x2 pass
			   ToDo find a way to also support other forms of recipes
			*/
			if(shape.length != (small?2:3)) continue iterate;
			for (int i=0;i<(small?2:3);i++){
				if(shape[i].length() != (small?2:3)) continue iterate;
			}
			
			plugin.debugMessage("passed shape test");
			// shape is ok, now test the ingredients
			Map<Character, ItemStack> currentIngredients = currentShaped.getIngredientMap();
			if (Main.debug)Bukkit.getConsoleSender().sendMessage(currentIngredients.toString());
			for(int a = 0; a<(small?2:3);a++){
				for(int b = 0; b<(small?2:3);b++){
					Character key1 = shape[a].charAt(b);
					Character key2 = String.valueOf(a*(small?2:3)+b+1).charAt(0);
					// compare entries
					// ToDo isSimilar takes lore and displayname into account?
					if((currentIngredients.get(key1) == null  || currentIngredients.get(key1).getType() == Material.AIR) && ingredients.get(key2) == null) continue;
					if((currentIngredients.get(key1) == null || currentIngredients.get(key1).getType() == Material.AIR)|| ingredients.get(key2) == null) continue iterate;
					plugin.debugMessage("                  testing: " + currentIngredients.get(key1).toString() + "   against   " + ingredients.get(key2).toString());
					if(!isSameMaterialData(currentIngredients.get(key1), ingredients.get(key2))) continue iterate;
				}
			}
			// the recipes have the same ingredient map
			plugin.debugMessage("found matching vanilla recipe");
			matched = currentShaped;
			iterator.remove();
			break;
		}
		
		// no custom and no vanilla match found
		// return a new CustomShaped recipe
		if(matched == null) {
			toReturn = new CustomShaped(resultItem);
			
			if(!small){
				toReturn.shape("123", "456", "789");
			} else {
				toReturn.shape("12", "34");
			}
			
			toReturn.setIngredients(ingredients, resultItem);
			toReturn.addLabel(path, resultItem);
			
			
			return toReturn;
		}
		// found matching vanilla recipe
		// there is no currently registered Custom recipe with the same map
		else{
			
			toReturn = new CustomShaped(resultItem);
			if(!small){
				toReturn.shape("123", "456", "789");
			} else {
				toReturn.shape("12", "34");
			}
			toReturn.setIngredients(ingredients, resultItem);
			toReturn.addLabel(path, resultItem);
			
			toReturn.setVanilla(matched);
			toReturn.setOverwritingVanillaRecipe(true);
			
			
			return toReturn;
		}
	}
	
	
	
	
	private MaterialData getMaterial(String matString){
		if(matString==null) return null;
		Material mat = null;
		byte data = 0;
		String[] obj = matString.split(":");
		
		if (obj.length == 2) {
			try {
				mat = Material.matchMaterial(obj[0]);
			} catch (Exception e) {
				// material name doesn't exist
			}
			
			try {
				data = Integer.valueOf(obj[1]).byteValue();
			} catch (NumberFormatException e) {
				// data not a number
			}
		} else {
			try {
				mat = Material.matchMaterial(matString);
			} catch (Exception e) {
				// material name doesn't exist
			}
		}
		if(mat == null) return null;
		@SuppressWarnings("deprecation") MaterialData toReturn = new MaterialData(mat, data);
		return toReturn;
	}
	
	
	/**
	 * Check for the files in the data folder and copy them if not there.
	 * Load the files to the File-configurations to read from.
	 */
	private void loadFiles() {
		File shaped2x2File = new File(plugin.getDataFolder().toString() + File.separatorChar + "shaped2x2Recipes.yaml");
		File shaped3x3File = new File(plugin.getDataFolder().toString() + File.separatorChar + "shaped3x3Recipes.yaml");
		File shapelessFile = new File(plugin.getDataFolder().toString() + File.separatorChar + "shapelessRecipes.yaml");
		File furnaceFile = new File(plugin.getDataFolder().toString() + File.separatorChar + "furnaceRecipes.yaml");
		
		//check for the files in the data folder
		if(!shaped2x2File.exists()){
			plugin.saveResource("shaped2x2Recipes.yaml", false);
		}
		if(!shaped3x3File.exists()){
			plugin.saveResource("shaped3x3Recipes.yaml", false);
		}
		if(!shapelessFile.exists()){
			plugin.saveResource("shapelessRecipes.yaml", false);
		}
		if(!furnaceFile.exists()){
			plugin.saveResource("furnaceRecipes.yaml", false);
		}
		try {
			this.shaped2x2Config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(shaped2x2File), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load the file: " + shaped2x2File.getPath());
			e.printStackTrace();
		}
		try {
			this.shaped3x3Config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(shaped3x3File), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load the file: " + shaped3x3File.getPath());
			e.printStackTrace();
		}
		try {
			this.shapelessConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(shapelessFile), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load the file: " + shapelessFile.getPath());
			e.printStackTrace();
		}
		try {
			this.furnaceConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(furnaceFile), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not load the file: " + furnaceFile.getPath());
			e.printStackTrace();
		}
	}
	
	public ArrayList<ICustomRecipe> getCustomRecipes(ItemStack result){
		ArrayList<ICustomRecipe> toReturn = new ArrayList<>();
		for(CustomShaped recipe : shaped3x3Recipes){
			if(isSameItem(result, recipe.getResult())) toReturn.add(recipe);
		}
		for(CustomShaped recipe : shaped2x2Recipes){
			if(isSameItem(result, recipe.getResult())) toReturn.add(recipe);
		}
		for(CustomFurnaceRecipe recipe : furnaceRecipes){
			if(isSameItem(result, recipe.getResult())) toReturn.add(recipe);
		}
		for(CustomShapelessRecipe recipe : shapelessRecipes){
			if(isSameItem(result, recipe.getResult())) toReturn.add(recipe);
		}
		return toReturn;
	}
	
	/**
	 * compare MaterialData, lore and display name.
	 *
	 * @param stack1 first ItemStack to compare
	 * @param stack2 second ItemStack to compare
	 * @return true if same MaterialData, lore and display name
	 */
	public boolean isSameItem(ItemStack stack1, ItemStack stack2) {
		if(stack1 == null && stack2 == null) return true;
		if(stack1 == null || stack2 == null) return false;
		if(Main.debug) Bukkit.getConsoleSender().sendMessage("testing stacks: " + stack1.toString() + "    " + stack2.toString());
		if(!stack1.getData().equals(stack2.getData())) return false;
		ItemMeta meta1 = stack1.getItemMeta(), meta2 = stack2.getItemMeta();
		if(meta1.hasDisplayName() && !meta2.hasDisplayName() || meta2.hasDisplayName() && !meta1.hasDisplayName()) return false;
		if(meta1.hasLore() && !meta2.hasLore() || meta2.hasLore() && !meta1.hasLore()) return false;
		if(meta1.hasDisplayName()){
			if(!meta1.getDisplayName().equals(meta2.getDisplayName())) return false;
		}
		
		if(meta1.hasLore()){
			List<String> lore1 = meta1.getLore(), lore2 = meta2.getLore();
			if(lore1.size() != lore2.size()) return false;
			for(int i = 0; i < lore1.size(); i++){
				if(!lore1.get(i).equals(lore2.get(i))) return false;
			}
		}
		return true;
	}
	
	public boolean isCustomFurnaceRecipe(ItemStack source) {
		for(CustomFurnaceRecipe recipe: furnaceRecipes){
			if(recipe.getInput().isSimilar(source)) return true;
		}
		return false;
	}
	
	
	public CustomFurnaceRecipe getCustomFurnaceRecipe(ItemStack source) {
		for(CustomFurnaceRecipe recipe: furnaceRecipes){
			if(recipe.getInput().isSimilar(source)) return recipe;
		}
		return null;
	}
	
	public CustomShaped getCustomShaped(CraftingInventory inv) {
		return getCustomShaped(inv.getMatrix());
	}
	
	public CustomShaped getCustomShaped(ItemStack[] items){
		if(!(items.length == 9) && !(items.length == 4)){
			plugin.debugMessage("CraftingInventory has wrong size: " + items.length);
			return null;
		}
		for(int i = 0; i<9;i++) {
			if(items[i]!=null && Main.debug)Bukkit.getConsoleSender().sendMessage(items[i].toString());
		}
		recipes:
		for(CustomShaped customShaped : shaped3x3Recipes){
			plugin.debugMessage("recipe: "+customShaped.getLabels().keySet().toString());
			Map<Character, ItemStack> ingredients = customShaped.getRecipeMap().get(customShaped.getRecipeMap().keySet().iterator().next());
			for(Character key : ingredients.keySet()){
				int keyInt = Integer.parseInt(key.toString());
				if((items[keyInt-1] == null  || items[keyInt-1].getType() == Material.AIR) && ingredients.get(key) == null){
					plugin.debugMessage("key: " + key + " both null");
					continue;
				}
				if((items[keyInt-1] == null || items[keyInt-1].getType() == Material.AIR) || ingredients.get(key) == null){
					plugin.debugMessage("key: " + key + " one null");
					continue recipes;
				}
				if(!(isSameMaterialData(ingredients.get(key), items[keyInt-1]))){
					plugin.debugMessage("key: " + key + " not similar");
					continue recipes;
				}
			}
			return customShaped;
		}
		return null;
	}
	
	
	
	private boolean isSameMaterialData(ItemStack itemStack, ItemStack item) {
		if(itemStack.getType() != item.getType()) return false;
		if(itemStack.getDurability() != item.getDurability()) return false;
		return true;
	}
}
