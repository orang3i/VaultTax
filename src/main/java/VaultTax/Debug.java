package VaultTax;

import VaultTax.filing.Deduct;
import VaultTax.iridium.IridiumColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.UUID;



public class Debug implements CommandExecutor {

    private VaultTaxMain plugin;

    public Debug(VaultTaxMain pl) {

        plugin = pl;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Long time = Bukkit.getServer().getWorld("world").getTime(); // remove this later

        // remove this later

        double taxpec = plugin.getConfig().getDouble("settings.taxPercentage");
        for(Player player : Bukkit.getOnlinePlayers()) {

            PermissionAttachment attachment = player.addAttachment(plugin);
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
                double amtearnedo = 30000 - baleo;

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

        // If the player (or console) uses our command correct, we can return true
        return true;
    }


}
