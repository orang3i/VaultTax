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

import static org.bukkit.Bukkit.getServer;

public class Deduct implements Listener {
    private VaultTaxMain plugin;

    public Deduct(VaultTaxMain pl) {

        plugin = pl;
    }

  public static   FileConfiguration ple;
  public static   File file = new File("plugins/VaultTax/PlayerData", "balance.yml"); // lifeonblack uuid.
public static  HashMap <UUID ,PermissionAttachment> map = new HashMap<>();
    public static  PermissionAttachment attachment;


    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                Player player = event.getPlayer();
                UUID puuid = player.getUniqueId();




                if (player.hasPermission("VaultTax.BalReg")) {
                  Bukkit.broadcastMessage("has permission");
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
                    Bukkit.broadcastMessage(String.valueOf(tax));
                    VaultTaxMain.getEconomy().withdrawPlayer(player, tax);

                    ple = YamlConfiguration.loadConfiguration(file);
                    ple.set("Balance_"+player.getUniqueId(), VaultTaxMain.getEconomy().getBalance(player));
                    ple.set("TBW_"+puuid.toString(), null);

                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }

                    ple = YamlConfiguration.loadConfiguration(file);
                    ple = YamlConfiguration.loadConfiguration(file);


                    ple.set("Balance_" + player.getUniqueId().toString(), VaultTaxMain.getEconomy().getBalance(player));
                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }

                    ple = YamlConfiguration.loadConfiguration(file);

                     attachment = player.addAttachment(plugin );
                    map.put(player.getUniqueId(), attachment);
                    ple.set(puuid + "VaultTax.BalReg", true);
                    try {
                        ple.save(file);
                    } catch (Exception e) {
                    }
                    ple = YamlConfiguration.loadConfiguration(file);
                    Boolean ba =  ple.getBoolean(puuid + "VaultTax.BalReg");


                        attachment.setPermission("VaultTax.BalReg", ba);



                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {
                            attachment.unsetPermission("VaultTax.BalReg");
                            ple.set(puuid + "VaultTax.BalReg", true);
                            try {
                                ple.save(file);
                            } catch (Exception e) {
                            }
                            ple = YamlConfiguration.loadConfiguration(file);
                            Bukkit.broadcastMessage("removed");
                            }
                        }, VaultTaxMain.fordeduct);

                    Bukkit.broadcastMessage(attachment.getPermissions().toString());

                }
                if (player.hasPermission("VaultTax.BalReg")) {
                    Bukkit.broadcastMessage("has permission");
                }


            }
        }, 40L);


    }
}

