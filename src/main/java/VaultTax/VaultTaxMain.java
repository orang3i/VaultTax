package VaultTax;

import VaultTax.filing.Deduct;
import VaultTax.filing.PlayerQuit;
import VaultTax.iridium.IridiumColorAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public final class VaultTaxMain extends JavaPlugin {

    public Long interval = getConfig().getLong("settings.interval");
    private static VaultTaxMain instance;

    public static int MAXR;


    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;



    public static VaultTaxMain getInstance() {

        return instance;
    }
    @Override
    public void onDisable() {


        Deduct.permissions.clear();
        FileConfiguration qui;
        File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
        qui = YamlConfiguration.loadConfiguration(fileq);
       fileq.delete();
        }



    @Override
    public void onEnable() {
        Deduct.permissions.clear();

        saveDefaultConfig();
        Logger.log(Logger.LogLevel.OUTLINE, "********************************************************************************");
        Logger.log(Logger.LogLevel.SUCCESS, IridiumColorAPI.process("<GRADIENT:9281fb>Thank you for using VaultTax!</GRADIENT:eb93fc>"));
        Logger.log(Logger.LogLevel.OUTLINE, "********************************************************************************");

        instance = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Deduct(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
        this.getCommand("debug").setExecutor(new Debug(this));


        MAXR = getConfig().getInt("settings.maxDelays");


        File playerdata;
        try {
            playerdata = new File(getDataFolder() + File.separator + "PlayerData");
            if(!playerdata.exists()){
                playerdata.mkdirs();
            }
        } catch(SecurityException e) {
            playerdata = null;
        }

        if(playerdata == null) {
            // do something...
        }
        FileConfiguration ple;
        File file = new File("plugins/VaultTax/PlayerData", "balance.yml"); // lifeonblack uuid.
        if (!file.exists()) { // if lifeonblack uuid is not on plugins/YourPlugin directory
            try {
                file.createNewFile(); // create the file lifeonblack uuid .yml
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ple = YamlConfiguration.loadConfiguration(file);

        if (!setupEconomy() ) {

            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {


                double taxpec = getConfig().getDouble("settings.taxPercentage");
                for(Player player : Bukkit.getOnlinePlayers()) {

                    PermissionAttachment attachment = player.addAttachment(getInstance());
                    Deduct.permissions.put(player.getUniqueId(), attachment);

                    PermissionAttachment pperms = Deduct.permissions.get(player.getUniqueId());
                    pperms.unsetPermission("VaultTax.BalReg");

                    UUID puuid = player.getUniqueId();
                    FileConfiguration ple;
                    File file = new File("plugins/VaultTax/PlayerData", "balance.yml"); // lifeonblack uuid.
                    FileConfiguration qui;
                    File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
                    qui = YamlConfiguration.loadConfiguration(fileq);

                    ple = YamlConfiguration.loadConfiguration(file);

                    for (String uuidd : qui.getKeys(false)) {

                        String name = Bukkit.getServer().getOfflinePlayer(uuidd).getName();
                        double baleo = ple.getDouble("Balance_" + uuidd);
                        Bukkit.broadcastMessage(String.valueOf(baleo));
                        double amtearnedo = VaultTaxMain.getEconomy().getBalance(name) - baleo;

                        double taxo = amtearnedo * taxpec / 100;
                        Bukkit.broadcastMessage(String.valueOf(amtearnedo));
                        double taxoo = Math.abs(taxo);

                        Bukkit.broadcastMessage(String.valueOf(taxoo));
                        ple.set("TBW_" + uuidd, taxoo);

                        Bukkit.broadcastMessage(String.valueOf(taxoo));
                        try {
                            ple.save(file);
                        } catch (Exception e) {
                        }

                        ple = YamlConfiguration.loadConfiguration(file);
                    }
                    //eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    double bale = ple.getDouble("Balance_" + player.getUniqueId().toString());

                    double amtearned = VaultTaxMain.getEconomy().getBalance(player) - bale;

                    double tax = amtearned * taxpec / 100;

                    if (amtearned > 0) {
                        player.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>You have earned $" + amtearned + " " + " and have been taxed $" + " " + tax + "</GRADIENT:eb93fc>"));
                    } else {
                        player.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>well... you didn't earn any money so you don't have to pay taxes!</GRADIENT:eb93fc>"));
                    }
                    VaultTaxMain.getEconomy().withdrawPlayer(player, tax);
                    //eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    ple.set("Balance_" + player.getUniqueId().toString(), VaultTaxMain.getEconomy().getBalance(player));
                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }

                    ple = YamlConfiguration.loadConfiguration(file);


                    //if here {





                    //if ends here}
                }


            }
        }, 0L, interval);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }



    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

}

