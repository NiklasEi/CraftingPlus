package me.nikl.craftingplus;

import me.nikl.craftingplus.listeners.FurnaceListener;
import me.nikl.craftingplus.listeners.PrepareCustomCraft;
import me.nikl.craftingplus.nmsutil.*;
import me.nikl.craftingplus.recipes.CustomShaped;
import me.nikl.craftingplus.recipes.RecipesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by niklas on 1/2/17.
 *
 */
public class Main extends JavaPlugin{
	// config
	private File con;
	private FileConfiguration config;
	
	private RecipesManager recipesManager;
	
	private INMSUtil nmsUtil;
	public static final boolean debug = false;
	
	
	
	@Override
	public void onEnable(){
		if(!setupUpdater()){
			Bukkit.getLogger().log(Level.SEVERE, "Your server version is not supported by this plugin!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		this.con = new File(this.getDataFolder().toString() + File.separatorChar + "config.yml");
		
		reload();
	}
	
	
	
	@Override
	public void onDisable(){
		
	}
	
	
	private void reload(){
		if(!con.exists()){
			this.saveResource("config.yml", false);
		}
		
		reloadConfig();
		
		if(recipesManager != null){
			recipesManager.unregisterAll();
			recipesManager.shutDown();
		}
		recipesManager = new RecipesManager(this);
		
		// get new listener
		new PrepareCustomCraft(this);
		new FurnaceListener(this);
	}
	
	public void debugMessage(String message){
		if(!debug) return;
		Bukkit.getConsoleSender().sendMessage(message);
	}
	
	private boolean setupUpdater() {
		String version;
		
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			return false;
		}
		
		if(debug) getLogger().info("Your server is running version " + version);
		
		switch (version) {
			case "v1_11_R1":
				nmsUtil = new NMSUtil_1_11_R1();
				
				break;
			case "v1_10_R1":
				nmsUtil = new NMSUtil_1_10_R1();
				
				break;
			case "v1_9_R2":
				nmsUtil = new NMSUtil_1_9_R2();
				
				break;
			case "v1_9_R1":
				nmsUtil = new NMSUtil_1_9_R1();
				
				break;
			case "v1_8_R3":
				nmsUtil = new NMSUtil_1_8_R3();
				
				break;
			case "v1_8_R2":
				nmsUtil = new NMSUtil_1_8_R2();
				
				break;
			case "v1_8_R1":
				nmsUtil = new NMSUtil_1_8_R1();
				
				break;
		}
		return nmsUtil != null;
	}
	
	
	
	public void reloadConfig(){
		try {
			this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.con), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null){
			@SuppressWarnings("deprecation")
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.config.setDefaults(defConfig);
		}
	}
	
	public INMSUtil getNmsUtil(){
		return this.nmsUtil;
	}
	
	public RecipesManager getRecipesManager(){return this.recipesManager;}
	
	@Override
	public FileConfiguration getConfig(){
		return this.config;
	}
}
