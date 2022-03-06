package VaultTax;

import VaultTax.filing.Deduct;
import VaultTax.filing.PlayerQuit;
import VaultTax.iridium.IridiumColorAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.MenuFunctionListener;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import static VaultTax.VaultTaxMain.taxpec;

public final class VaultTaxMain extends JavaPlugin {

    public static Long interval;
    public static int intervali;
    private static VaultTaxMain instance;

    public static int MAXR;


    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    public static long fordeduct;
    public static Player pe;
    public static double taxpec;
    public static int mode;

    public static VaultTaxMain getInstance() {

        return instance;
    }

    @Override
    public void onDisable() {


        FileConfiguration qui;
        File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
        qui = YamlConfiguration.loadConfiguration(fileq);
        fileq.delete();

        FileConfiguration ple;
        File file = new File("plugins/VaultTax/PlayerData", "perms.yml");
        ple = YamlConfiguration.loadConfiguration(file);
        file.delete();
    }


    @Override
    public void onEnable() {
        taxpec = getConfig().getDouble("settings.taxPercentage");
        interval = getConfig().getLong("settings.interval") * 20;
        intervali = getConfig().getInt("settings.interval") * 20;
        saveDefaultConfig();
        Logger.log(Logger.LogLevel.OUTLINE, "********************************************************************************");
        Logger.log(Logger.LogLevel.SUCCESS, IridiumColorAPI.process("<GRADIENT:9281fb>Thank you for using VaultTax!</GRADIENT:eb93fc>"));
        Logger.log(Logger.LogLevel.OUTLINE, "********************************************************************************");

        instance = this;
        int pluginId = 14531; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        mode = getConfig().getInt("settings.mode");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Deduct(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(this), this);
        this.getCommand("paytaxes").setExecutor(new PayTaxes());
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);


        MAXR = getConfig().getInt("settings.maxDelays");


        File playerdata;
        try {
            playerdata = new File(getDataFolder() + File.separator + "PlayerData");
            if (!playerdata.exists()) {
                playerdata.mkdirs();
            }
        } catch (SecurityException e) {
            playerdata = null;
        }

        if (playerdata == null) {
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

        FileConfiguration perms;
        File prm = new File("plugins/VaultTax/PlayerData", "perms.yml");
        if (!prm.exists()) { // if lifeonblack uuid is not on plugins/YourPlugin directory
            try {
                prm.createNewFile(); // create the file lifeonblack uuid .yml
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        perms = YamlConfiguration.loadConfiguration(prm);


        if (!setupEconomy()) {

            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {


                double taxpec = getConfig().getDouble("settings.taxPercentage");
                int mode = getConfig().getInt("settings.mode");
                Long timeleft = getConfig().getLong("settings.timeleftToFile") * 20;

                FileConfiguration plc;
                try {


                    FileWriter fw = new FileWriter(new File("plugins/VaultTax/PlayerData", "perms.yml"));
                    PrintWriter pw = new PrintWriter(fw);
                    pw.write("");
                    pw.flush();
                    pw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                File filc = new File("plugins/VaultTax/PlayerData", "perms.yml");
                if (!filc.exists()) { // if lifeonblack uuid is not on plugins/YourPlugin directory
                    try {
                        filc.createNewFile(); // create the file lifeonblack uuid .yml
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                plc = YamlConfiguration.loadConfiguration(filc);



                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {

                        @Override
                        public void run() {
                            FileConfiguration qui;
                            File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
                            qui = YamlConfiguration.loadConfiguration(fileq);

                            for (String uuidd : qui.getKeys(false)) {


                                double finepercu = getConfig().getDouble("settings.finePercentage");
                                FileConfiguration peeu;
                                File filepeu = new File("plugins/VaultTax/PlayerData", "balance.yml");
                                peeu = YamlConfiguration.loadConfiguration(filepeu);
                                double baleou = peeu.getDouble("Balance_" + uuidd);

                                double amtearnedou = VaultTaxMain.getEconomy().getBalance(qui.getString(uuidd)) - baleou;

                                double taxou = amtearnedou * taxpec / 100;

                                double taxoou = Math.abs(taxou);

                                double fineu = (taxoou * finepercu / 100) + taxoou;

                                if(fineu > 0) {
                                    econ.withdrawPlayer(qui.getString(uuidd), fineu);
                                }

                                peeu.set("Balance_" + uuidd, VaultTaxMain.getEconomy().getBalance(qui.getString(uuidd)));
                                try {
                                    peeu.save(filepeu);
                                } catch (Exception e) {
                                }

                                peeu = YamlConfiguration.loadConfiguration(filepeu);
                            }
                        }
                    }, timeleft);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        FileConfiguration quide;
                        File fileqde = new File("plugins/VaultTax/PlayerData", "balance.yml");
                        quide = YamlConfiguration.loadConfiguration(fileqde);
                        quide.set("Clicked_" + p.getUniqueId().toString(), null);
                        try {
                            quide.save(fileqde);
                        } catch (Exception e) {
                        }
                        quide = YamlConfiguration.loadConfiguration(fileqde);

                        if(mode == 2) {
                        Bukkit.broadcastMessage(IridiumColorAPI.process("<GRADIENT:9281fb>please file your taxes within " + timeleft / 20 + " seconds</GRADIENT:eb93fc>"));
                        TextComponent text = new TextComponent(IridiumColorAPI.process("<GRADIENT:9281fb>click this to open the tax menu </GRADIENT:eb93fc>"));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("yes! this message").create()));
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/paytaxes"));

                            p.spigot().sendMessage(text);
                        }
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                FileConfiguration qui;
                                File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
                                qui = YamlConfiguration.loadConfiguration(fileq);

                                FileConfiguration quid;
                                File fileqd = new File("plugins/VaultTax/PlayerData", "balance.yml");
                                quid = YamlConfiguration.loadConfiguration(fileqd);

                                boolean clicked = quid.getBoolean("Clicked_" + p.getUniqueId().toString());
                                if (clicked == true) {

                                }
                                if (!clicked) {

                                    double fineperc = getConfig().getDouble("settings.finePercentage");
                                    FileConfiguration pee;
                                    File filepe = new File("plugins/VaultTax/PlayerData", "balance.yml");
                                    pee = YamlConfiguration.loadConfiguration(filepe);
                                    double baleo = pee.getDouble("Balance_" + p.getUniqueId());

                                    double amtearnedo = VaultTaxMain.getEconomy().getBalance(p.getName()) - baleo;

                                    double taxo = amtearnedo * taxpec / 100;

                                    double taxoo = Math.abs(taxo);

                                    double fine = (taxoo * fineperc / 100) + taxoo;

                                    if(fine > 0) {
                                        econ.withdrawPlayer(p, fine);
                                    }
                                    if (fine > 0 && mode == 2) {
                                        p.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>you have been fined " + fine + "</GRADIENT:eb93fc>"));
                                    }if(fine > 0 && mode == 1) {
                                        p.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>you have earned amount " + amtearnedo +" and you have paid taxes "+ fine + "</GRADIENT:eb93fc>"));
                                    }
                                    if(fine <= 0 && mode == 1) {
                                        p.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>you have not earned any money so you don't have to pay taxes! </GRADIENT:eb93fc>"));
                                    }
                                    pee.set("Balance_" + p.getUniqueId().toString(), VaultTaxMain.getEconomy().getBalance(p));
                                    try {
                                        pee.save(filepe);
                                    } catch (Exception e) {
                                    }

                                    pee = YamlConfiguration.loadConfiguration(filepe);


                                }
                            }
                        }, timeleft);
                    }



                //if ends here}

                timer();

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

    public void timer() {

        fordeduct = interval;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {


                --fordeduct;


            }
        }, 0L, 0L);
    }

    public static Menu createMenu() {
        return ChestMenu.builder(1)
                .title("Menu")
                .build();

    }

    public static void displayMenu(Player player1) {

            Menu menu = createMenu();
            menu.open(player1);
            FileConfiguration plr;
            File filer = new File("plugins/VaultTax/PlayerData", "balance.yml");
            plr = YamlConfiguration.loadConfiguration(filer);

            double baler = plr.getDouble("Balance_" + player1.getUniqueId().toString());
            double amtearnedca = VaultTaxMain.getEconomy().getBalance(player1) - baler;

            double taxe = amtearnedca * taxpec / 100;
            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("tax: " + taxe);
            item.setItemMeta(itemMeta);

            menu.getSlot(4).setItem(item);

            ClickOptions options = ClickOptions.builder()
                    .allow(ClickType.LEFT, ClickType.RIGHT)
                    .build();
            menu.getSlot(4).setClickOptions(options);
            menu.getSlot(4).setClickHandler((player2, information) -> {

                for (Player player : Bukkit.getOnlinePlayers()) {


                    UUID puuid = player.getUniqueId();
                    FileConfiguration ple;
                    File file = new File("plugins/VaultTax/PlayerData", "balance.yml"); // lifeonblack uuid.
                    FileConfiguration qui;
                    File fileq = new File("plugins/VaultTax/PlayerData", "playerQuits.yml");
                    qui = YamlConfiguration.loadConfiguration(fileq);

                    ple = YamlConfiguration.loadConfiguration(file);
                    FileConfiguration plce;
                    File filce = new File("plugins/VaultTax/PlayerData", "perms.yml");
                    plce = YamlConfiguration.loadConfiguration(filce);
                    ple = YamlConfiguration.loadConfiguration(file);


                    boolean clicked = ple.getBoolean("Clicked_" + player.getUniqueId().toString());
                    if (clicked != true) {
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
                        //eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

                        Slot slot = createMenu().getSlot(4);


                        FileConfiguration plee;
                        File filee = new File("plugins/VaultTax/PlayerData", "balance.yml"); // lifeonblack uuid.
                        plee = YamlConfiguration.loadConfiguration(filee);

                        double bale = plee.getDouble("Balance_" + player.getUniqueId().toString());

                        double amtearned = VaultTaxMain.getEconomy().getBalance(player) - bale;

                        double tax = amtearned * taxpec / 100;

                        if (amtearned > 0) {
                            player.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>You have earned $" + amtearned + " " + " and have been taxed $" + " " + tax + "</GRADIENT:eb93fc>"));
                        } else {
                            player.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>well... you didn't earn any money so you don't have to pay taxes!</GRADIENT:eb93fc>"));
                        }

                        if(tax > 0) {
                            VaultTaxMain.getEconomy().withdrawPlayer(player, tax);
                        }
                        //eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee

                        plee.set("Balance_" + player.getUniqueId().toString(), VaultTaxMain.getEconomy().getBalance(player));
                        try {
                            plee.save(filee);
                        } catch (Exception e) {
                        }

                        plee = YamlConfiguration.loadConfiguration(filee);


                        //if here {

                        plce.set(puuid.toString() + "BalReg", true);
                        try {
                            plce.save(filce);
                        } catch (Exception e) {
                        }
                        plce = YamlConfiguration.loadConfiguration(filce);

                        ple.set("Clicked_" + player.getUniqueId().toString(), true);
                        try {
                            ple.save(file);
                        } catch (Exception e) {
                        }
                        ple = YamlConfiguration.loadConfiguration(file);
                        plce = YamlConfiguration.loadConfiguration(filce);
                    }
                    if (clicked == true) {

                        player.sendMessage(IridiumColorAPI.process("<GRADIENT:9281fb>You have already filed your taxes</GRADIENT:eb93fc>"));
                    }

                }

            });
        }

    }

