package VaultTax;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayTaxes implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (sender instanceof Player) {
        Player player = (Player) sender;
        VaultTaxMain.displayMenu(player);
    }
        return true;
    }
}