package me.zbrunooow.bgguerreiro;

import me.zbrunooow.bgguerreiro.comandos.Guerreiro;
import me.zbrunooow.bgguerreiro.guerreiro.CamaroteManager;
import me.zbrunooow.bgguerreiro.guerreiro.Evento;
import me.zbrunooow.bgguerreiro.hooks.LegendChatHook;
import me.zbrunooow.bgguerreiro.hooks.SimpleClansHook;
import me.zbrunooow.bgguerreiro.hooks.VaultHook;
import me.zbrunooow.bgguerreiro.listeners.*;
import me.zbrunooow.bgguerreiro.utils.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

  public static Core instance;
  private static Mensagens messages;
  public boolean sc;
  public boolean update = false;
  public String updateversion = "";
  public String prefix = ChatColor.GREEN + "[BGGuerreiro " + getDescription().getVersion() + "] ";
  private API api;
  private SimpleClansHook simpleClansHook;
  private AutoUpdater autoUpdate;
  private Config configuration;
  private Locations locs;
  private VaultHook vaultHook;
  private LegendChatHook lcHook;
  private Manager manager;
  private MySQL mysql;
  private Evento evento;
  private CamaroteManager cm;

  public static SimpleClans getSC() {
    return SimpleClans.getInstance();
  }

  public static Mensagens getMessages() {
    return messages;
  }

  public static Core getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    instance = this;
    api = new API();
    locs = new Locations();
    manager = new Manager();
    evento = new Evento();
    cm = new CamaroteManager();

    if (getServer().getPluginManager().getPlugin("Vault") != null) {
      Bukkit.getConsoleSender().sendMessage(prefix + "§aVault encontrado! (Hooked).");
      startEconomy();
    } else {
      Bukkit.getConsoleSender()
          .sendMessage(prefix + "§cVault não encontrado, desabilitando plugin.");
      Bukkit.getServer().getPluginManager().disablePlugin(this);
      return;
    }

    if (getServer().getPluginManager().getPlugin("SimpleClans") != null) {
      simpleclansHook();
      Bukkit.getConsoleSender().sendMessage(prefix + "§aSimpleClans encontrado! (Hooked).");
    } else {
      Bukkit.getConsoleSender().sendMessage(prefix + "§cSimpleClans não encontrado.");
    }

    if (getServer().getPluginManager().getPlugin("nChat") != null
        || getServer().getPluginManager().getPlugin("Legendchat") != null) {
      Bukkit.getConsoleSender()
          .sendMessage(prefix + "§aChatAPI carregada (A tag vai funcionar) [LegendChat/nChat]");
      getServer().getPluginManager().registerEvents(new LegendChatTagEvent(), this);
      loadLegendchat();
    } else if (getServer().getPluginManager().getPlugin("UltimateChat") != null) {
      Bukkit.getConsoleSender()
          .sendMessage(prefix + "§aChatAPI carregada (A tag vai funcionar) [UltimateChat]");
      getServer().getPluginManager().registerEvents(new UltimateChatListener(), this);
    } else {
      getServer().getPluginManager().registerEvents(new NormalChat(), this);
      Bukkit.getConsoleSender()
          .sendMessage(prefix + "§cChatAPI não carregada (A TAG NÃO VAI FUNCIONAR)");
    }

    saveDefaultConfig();
    reloadPlugin();

    getServer().getPluginManager().registerEvents(new Damage(), this);
    getServer().getPluginManager().registerEvents(new Death(), this);
    getServer().getPluginManager().registerEvents(new ExecuteCmd(), this);
    getServer().getPluginManager().registerEvents(new InteractEvent(), this);
    getServer().getPluginManager().registerEvents(new ItemDrop(), this);
    getServer().getPluginManager().registerEvents(new ItemPickup(), this);
    getServer().getPluginManager().registerEvents(new PlayerEntrou(), this);
    getServer().getPluginManager().registerEvents(new Quit(), this);

    getCommand("guerreiro").setExecutor(new Guerreiro());

    mysql =
        new MySQL(
            Config.get().getUser(),
            Config.get().getPass(),
            Config.get().getUrl(),
            Config.get().getPort(),
            Config.get().getDatabase());

    Manager.get().setLastWinner(API.get().getLW());

    AutoStart.load();

    Bukkit.getConsoleSender().sendMessage(prefix + "§aPlugin habilitado com sucesso!");

    autoUpdater();
    messages = new Mensagens();
  }

  public void reloadPlugin() {
    reloadConfig();

    this.locs = new Locations();
    this.configuration = new Config();
    messages = new Mensagens();
  }

  @Override
  public void onDisable() {
    Bukkit.getConsoleSender().sendMessage(prefix + "§cPlugin desabilitado com sucesso!");
  }

  public Config getConfiguration() {
    return configuration;
  }

  private void startEconomy() {
    this.vaultHook = new VaultHook();
    this.vaultHook.setupEconomy();
  }

  private void loadLegendchat() {
    this.lcHook = new LegendChatHook();
    this.lcHook.hookLegendChat();
  }

  private void simpleclansHook() {
    this.simpleClansHook = new SimpleClansHook();
    this.simpleClansHook.hookSimpleclans();
  }

  private void autoUpdater() {
    this.autoUpdate = new AutoUpdater();
    this.autoUpdate.autoUpdate();
  }

  public API getApi() {
    return api;
  }

  public MySQL getMySQL() {
    return mysql;
  }

  public CamaroteManager getCM() {
    return cm;
  }

  public Manager getManager() {
    return manager;
  }

  public Locations getLocs() {
    return locs;
  }

  public Evento getEvento() {
    return evento;
  }
}
