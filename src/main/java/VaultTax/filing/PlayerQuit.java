package VaultTax.filing;

import VaultTax.VaultTaxMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;



public class PlayerQuit implements Listener {

    private VaultTaxMain plugin;

    public PlayerQuit(VaultTaxMain pl) {

        plugin = pl;
    }
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        FileConfiguration qui;
        File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml"); // lifeonblack uuid.
        if (!fileq.exists()) { // if lifeonblack uuid is not on plugins/YourPlugin directory
            try {
                fileq.createNewFile(); // create the file lifeonblack uuid .yml
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        qui = YamlConfiguration.loadConfiguration(fileq);




        qui.set(uuid.toString(), player.getPlayer().getName());
        try {
            qui.save(fileq);
        } catch (Exception e) {
        }

        qui = YamlConfiguration.loadConfiguration(fileq);

        for(String uuidd : qui.getConfigurationSection("").getKeys(false)){

            Bukkit.broadcastMessage(uuidd);
        }



    }

}
