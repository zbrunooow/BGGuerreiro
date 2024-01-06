package me.zbrunooow.bgguerreiro;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import me.zbrunooow.bgguerreiro.commands.WarriorCommand;
import me.zbrunooow.bgguerreiro.commands.completer.WarriorCommandCompleter;
import me.zbrunooow.bgguerreiro.hooks.LegendChatHook;
import me.zbrunooow.bgguerreiro.hooks.SimpleClansHook;
import me.zbrunooow.bgguerreiro.hooks.VaultHook;
import me.zbrunooow.bgguerreiro.lang.LanguageRegistry;
import me.zbrunooow.bgguerreiro.listener.hooks.LegendChatTagListener;
import me.zbrunooow.bgguerreiro.listener.hooks.NormalChatListener;
import me.zbrunooow.bgguerreiro.listener.hooks.UltimateChatListener;
import me.zbrunooow.bgguerreiro.listener.player.DamageListener;
import me.zbrunooow.bgguerreiro.listener.player.DeathListener;
import me.zbrunooow.bgguerreiro.listener.player.ItemInteractionListener;
import me.zbrunooow.bgguerreiro.listener.player.TotalInteractionListener;
import me.zbrunooow.bgguerreiro.listener.server.CommandExecutionListener;
import me.zbrunooow.bgguerreiro.listener.server.ConnectionListener;
import me.zbrunooow.bgguerreiro.manager.BoxManager;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.manager.KitManager;
import me.zbrunooow.bgguerreiro.util.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@Setter
public final class WarriorEngine extends JavaPlugin {

  public static WarriorEngine instance;
  private boolean simpleClans;
  private boolean update = false;
  private String updateVersion = "";
  private String prefix = ChatColor.GREEN + "[BGGuerreiro " + getDescription().getVersion() + "] ";
  private API api;
  private SimpleClansHook simpleClansHook;
  private AutoUpdater autoUpdate;
  private Config configuration;
  private Locations locations;
  private KitManager kitManager;
  private VaultHook vaultHook;
  private LegendChatHook legendChatHook;
  private Manager manager;
  private MySQL mySQL;
  private EventManager eventManager;
  private BoxManager boxManager;

  @Tolerate
  public static WarriorEngine getInstance() {
    return instance;
  }

  public static net.sacredlabyrinth.phaed.simpleclans.SimpleClans getSimpleClans() {
    return instance.isSimpleClans() ? net.sacredlabyrinth.phaed.simpleclans.SimpleClans.getInstance() : null;
  }

  @Override
  public void onEnable() {
    instance = this;
    this.api = new API();
    this.locations = new Locations();

    if (cantHookVault()) return;
    this.checkSimpleClans();

    this.setupChatHook();
    this.saveDefaultConfig();
    this.reloadPlugin();

    this.kitManager = new KitManager();
    this.manager = new Manager();
    this.eventManager = new EventManager();
    this.boxManager = new BoxManager();

    this.registerListeners(
        new DamageListener(),
        new DeathListener(),
        new CommandExecutionListener(),
        new TotalInteractionListener(),
        new ItemInteractionListener(),
        new ConnectionListener());

    PluginCommand warriorCommand = this.getCommand("guerreiro");
    warriorCommand.setExecutor(new WarriorCommand());
    warriorCommand.setTabCompleter(new WarriorCommandCompleter());

    this.mySQL =
        new MySQL(
            Config.get().getUser(),
            Config.get().getPass(),
            Config.get().getUrl(),
            Config.get().getPort(),
            Config.get().getDatabase());

    Manager.getCreated().setLastWinner(API.getCreated().getLW());
    AutoStart.load();
    this.autoUpdater();

    int pluginId = 20664;
    Metrics metrics = new Metrics(this, pluginId);

    Bukkit.getConsoleSender().sendMessage(prefix + "§aPlugin habilitado com sucesso!");
  }

  private void setupChatHook() {
    String chatAPI = "";
    if (isPluginPresent("nChat") || isPluginPresent("Legendchat")) {
      this.registerListeners(new LegendChatTagListener());
      this.loadLegendchat();
      chatAPI = "LegendChat/nChat";
    }

    if (isPluginPresent("UltimateChat")) {
      this.registerListeners(new UltimateChatListener());
      chatAPI = "UltimateChat";
    }

    if (!(Strings.isNullOrEmpty(chatAPI))) {
      Bukkit.getConsoleSender().sendMessage(prefix + "§a" + chatAPI + " encontrado! (Hooked).");
      return;
    }

    this.registerListeners(new NormalChatListener());
    Bukkit.getConsoleSender()
        .sendMessage(prefix + "§cChatAPI não carregada (A tag de chat não foi adicionada).");
  }

  private void checkSimpleClans() {
    if (isPluginPresent("SimpleClans")) {
      this.simpleclansHook();
      Bukkit.getConsoleSender().sendMessage(prefix + "§aSimpleClans encontrado! (Hooked).");
    } else {
      Bukkit.getConsoleSender().sendMessage(prefix + "§cSimpleClans não encontrado.");
    }
  }

  private boolean cantHookVault() {
    if (isPluginPresent("Vault")) {
      Bukkit.getConsoleSender().sendMessage(prefix + "§aVault encontrado! (Hooked).");
      startEconomy();
      return false;
    } else {
      Bukkit.getConsoleSender()
          .sendMessage(prefix + "§cVault não encontrado, desabilitando plugin.");
      Bukkit.getServer().getPluginManager().disablePlugin(this);
      return true;
    }
  }

  public void reloadPlugin() {
    this.reloadConfig();

    this.kitManager = new KitManager();
    this.locations = new Locations();
    this.configuration = new Config();

    LanguageRegistry.saveDefaults();
    LanguageRegistry.registerAll();

    if (LanguageRegistry.getDefined() == null) {
      Bukkit.getConsoleSender().sendMessage(prefix + "§cA linguagem selecionada não existe! - Selecionada: " + Config.get().getLanguage());
    }
  }

  @Override
  public void onDisable() {
    Bukkit.getConsoleSender().sendMessage(prefix + "§cPlugin desabilitado com sucesso!");
  }

  private void startEconomy() {
    this.vaultHook = new VaultHook();
    this.vaultHook.setupEconomy();
  }

  private void loadLegendchat() {
    this.legendChatHook = new LegendChatHook();
    this.legendChatHook.hookLegendChat();
  }

  private void simpleclansHook() {
    this.simpleClansHook = new SimpleClansHook();
    this.simpleClansHook.hookSimpleclans();
  }

  private void autoUpdater() {
    this.autoUpdate = new AutoUpdater();
    this.autoUpdate.autoUpdate();
  }

  private boolean isPluginPresent(String name) {
    return getServer().getPluginManager().getPlugin(name) != null;
  }

  private void registerListeners(Listener... listeners) {
    for (Listener listener : listeners) {
      getServer().getPluginManager().registerEvents(listener, this);
    }
  }

  @Tolerate
  public boolean isSimpleClans() {
    return simpleClans;
  }

}
