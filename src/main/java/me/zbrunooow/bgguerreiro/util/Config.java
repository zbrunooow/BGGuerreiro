package me.zbrunooow.bgguerreiro.util;

import lombok.Getter;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigDecimal;
import java.util.List;

public class Config {

    private int tempoAnuncios;
    private int tempoFinal;
    private int tempoEntreAnuncios;
    private int tempoEspera;
    private int minimoParticipantes;
    private int topLimite;

    private boolean abateGeral;
    private boolean chat;
    private boolean drop;
    private boolean comandos;
    private boolean formatPrize;

    private String armazenamento;
    private String url;
    private String items;
    private Integer port;
    private String user;
    private String pass;
    private String database;

    private boolean dmHabilitado;
    private int dmTempo;
    private int dmSemPvP;

    private List<String> horarios;

    private Double premio;
    private String tag;

    @Getter
    private String language;

    public Config() {
        language = replace("Language");
        armazenamento = replaceSql("Tipo");
        url = replaceSql("url");
        port = replaceIntSql("port");
        user = replaceSql("user");
        pass = replaceSql("pass");
        database = replaceSql("database");

        topLimite = 5;

        tempoAnuncios = replaceInt("Tempo-Anuncios");
        tempoEntreAnuncios = replaceInt("Tempo-Entre");
        tempoFinal = replaceInt("Tempo-Final");
        tempoEspera = replaceInt("Tempo-Espera");
        dmTempo = replaceInt("Deathmatch.Tempo");
        dmSemPvP = replaceInt("Deathmatch.Sem-PvP");
        minimoParticipantes = replaceInt("Minimo-Participantes");

        horarios = replaceHorarios("Horarios");

        tag = replace("Tag");
        items = replace("Itens");
        formatPrize = replaceBool("Formatar-Premio");
        abateGeral = replaceBool("Abate-Geral");
        dmHabilitado = replaceBool("Deathmatch.Habilitado");
        comandos = replaceBool("Executar-Comandos");
        chat = replaceBool("Chat");
        drop = replaceBool("Dropar-Itens");
        premio = replaceDouble("Premio");

    }

    public static Config get(){
        return WarriorEngine.getInstance().getConfiguration();
    }

    private int replaceInt(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getInt("Config." + linha);
    }

    private int replaceTopLimite(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getInt("Mensagens." + linha);
    }

    private List<String> replaceHorarios(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getStringList("AutoStart." + linha);
    }

    private Boolean replaceBool(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getBoolean("Config." + linha);
    }

    private double replaceDouble(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getDouble("Config." + linha);
    }

    private String replace(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getString("Config." + linha).replace('&', '§');
    }

    private List<String> replaceList(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getStringList("Config." + linha);
    }

    private Integer replaceIntSql(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getInt("Armazenamento." + linha);
    }

    private String replaceSql(String linha) {
        FileConfiguration config = WarriorEngine.getInstance().getConfig();
        return config.getString("Armazenamento." + linha).replace('&', '§');
    }

    public String getArmazenamento() {
        return armazenamento;
    }

    public String getUrl() {
        return url;
    }

    public Integer getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getDatabase() {
        return database;
    }

    public int getTempoAnuncios() {
        return tempoAnuncios;
    }

    public boolean isAbateGeral() {
        return abateGeral;
    }

    public boolean isDmHabilitado() {
        return dmHabilitado;
    }

    public int getDmTempo() {
        return dmTempo;
    }

    public int getDmSemPvP() {
        return dmSemPvP;
    }

    public int getTempoEntreAnuncios() {
        return tempoEntreAnuncios;
    }

    public Double getPremio() {
        return premio;
    }

    public String getTag() {
        return tag;
    }

    public int getTempoFinal() {
        return tempoFinal;
    }

    public boolean isChat() {
        return chat;
    }

    public boolean isComandos() {
        return comandos;
    }

    public int getTopLimite() {
        return topLimite;
    }

    public boolean isFormatPrize() {
        return formatPrize;
    }

    public boolean isDrop() {
        return drop;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public int getMinimoParticipantes() {
        return minimoParticipantes;
    }

    public List<String> getHorarios() {
        return horarios;
    }

    public String getItems() {
        return items;
    }

}
