package VaultTax.filing;

import VaultTax.Logger;
import VaultTax.VaultTaxMain;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static VaultTax.VaultTaxMain.taxpec;
import static org.bukkit.Bukkit.getServer;

public class Deduct implements Listener {
    private VaultTaxMain plugin;

    public Deduct(VaultTaxMain pl) {

        plugin = pl;
    }

 // lifeonblack uuid.



    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                Player player = event.getPlayer();
                UUID puuid = player.getUniqueId();

                FileConfiguration ple ;
               File file = new File("plugins/VaultTax/PlayerData", "balance.yml");
                 ple = YamlConfiguration.loadConfiguration(file)  ;

                FileConfiguration perms ;
                File prm = new File("plugins/VaultTax/PlayerData", "perms.yml");
                perms = YamlConfiguration.loadConfiguration(prm)  ;
                Boolean ba =  perms.getBoolean(puuid + "BalReg");

                if (ba == true) {

                } else {


                    FileConfiguration qui;
                    File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
                    qui = YamlConfiguration.loadConfiguration(fileq);

                    qui.set(puuid.toString(), null);
                    try {
                        qui.save(fileq);
                    } catch (Exception e) {
                    }
                    qui = YamlConfiguration.loadConfiguration(fileq);


                    if (!file.exists()) { // if lifeonblack uuid is not on plugins/YourPlugin directory
                        try {
                            file.createNewFile(); // create the file lifeonblack uuid .yml
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ple = YamlConfiguration.loadConfiguration(file);


                    double tax = ple.getDouble("TBW_"+puuid.toString());

                    VaultTaxMain.getEconomy().withdrawPlayer(player, tax);

                    ple = YamlConfiguration.loadConfiguration(file);
                    ple.set("Balance_"+player.getUniqueId(), VaultTaxMain.getEconomy().getBalance(player));
                    ple.set("TBW_" + puuid.toString(), null);



                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }

                    ple = YamlConfiguration.loadConfiguration(file);
                    ple = YamlConfiguration.loadConfiguration(file);

                    for (String uuidd : qui.getKeys(false)) {


                        String name = Bukkit.getServer().getOfflinePlayer(uuidd).getName();
                        double baleo = ple.getDouble("Balance_" + uuidd);

                        double amtearnedo = VaultTaxMain.getEconomy().getBalance(name) - baleo;

                        double taxo = amtearnedo * taxpec / 100;

                        double taxoo = Math.abs(taxo);


                        ple.set("TBW_" + uuidd, taxoo);


                        try {
                            ple.save(file);
                        } catch (Exception e) {
                        }

                        ple = YamlConfiguration.loadConfiguration(file);


                    }


                    ple.set("Balance_" + player.getUniqueId().toString(), VaultTaxMain.getEconomy().getBalance(player));
                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }

                    ple = YamlConfiguration.loadConfiguration(file);


                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {

                            FileConfiguration ple ;
                            File file = new File("plugins/VaultTax/PlayerData", "perms.yml");
                            ple = YamlConfiguration.loadConfiguration(file)  ;
                            ple.set(puuid.toString() + "BalReg", true);
                            try {
                                ple.save(file);
                            } catch (Exception e) {
                            }
                            ple = YamlConfiguration.loadConfiguration(file);

                        }
                    },10L );


                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {

                            FileConfiguration ple ;
                            File file = new File("plugins/VaultTax/PlayerData", "perms.yml");
                            ple = YamlConfiguration.loadConfiguration(file)  ;
                            ple.set(puuid.toString() + "BalReg", false);
                            try {
                                ple.save(file);
                            } catch (Exception e) {
                            }
                            ple = YamlConfiguration.loadConfiguration(file);

                            }
                        }, VaultTaxMain.fordeduct);


                }

                if (ba == false) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {

                            FileConfiguration ple ;
                            File file = new File("plugins/VaultTax/PlayerData", "perms.yml");
                            ple = YamlConfiguration.loadConfiguration(file)  ;
                            ple.set(puuid.toString() + "BalReg", true);
                            try {
                                ple.save(file);
                            } catch (Exception e) {
                            }
                            ple = YamlConfiguration.loadConfiguration(file);

                        }
                    },0L );

                }

            }
        }, 40L);


    }
}

