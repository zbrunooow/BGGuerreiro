package me.zbrunooow.bgguerreiro.utils;

import me.zbrunooow.bgguerreiro.Core;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Mensagens {

    private String semPerm;
    private String abate;
    private String restam;
    private String ab1;
    private String ab2;
    private String topValue;
    private String camaroteJoin;
    private String camaroteLeave;
    private List<String> comandoGeral;
    private List<String> topHeader;
    private List<String> topFooter;
    private List<String> iniciando;
    private List<String> cancelado;
    private List<String> status;
    private List<String> espera;
    private List<String> valendo;
    private List<String> dmChegando;
    private List<String> dmStart;
    private List<String> finalizado;

    public Mensagens() {
        semPerm = replace("Sem-Permissao");
        abate = replace("Abate");
        restam = replace("Restam");
        ab1 = replace("ActionBar-1");
        ab2 = replace("ActionBar-2");
        camaroteJoin = replace("Camarote");
        camaroteLeave = replace("Camarote-Saiu");
        topValue = replace("Top.Value");
        topHeader = replaceList("Top.Header");
        topFooter = replaceList("Top.Footer");
        iniciando = replaceList("Iniciando");
        cancelado = replaceList("Cancelado");
        espera = replaceList("Espera");
        status = replaceList("Status");
        dmChegando = replaceList("DM-Chegando");
        dmStart = replaceList("DM-Start");
        valendo = replaceList("Valendo");
        finalizado = replaceList("Finalizado");
        comandoGeral = replaceList("Geral");
    }

    private String replace(String linha) {
        FileConfiguration config = Core.getInstance().getConfig();
        return config.getString("Mensagens." + linha).replace('&', '§');
    }

    private List<String> replaceList(String linha) {
        FileConfiguration config = Core.getInstance().getConfig();
        List<String> retornar = new ArrayList<>();
        for(String str : config.getStringList("Mensagens." + linha)) {
            retornar.add(str.replace('&', '§'));
        }
        return retornar;
    }

    public List<String> getComandoGeral() {
        return comandoGeral;
    }

    public String getSemPerm() {
        return semPerm;
    }
    public String getAbate() {
        return abate;
    }

    public List<String> getEspera() {
        return espera;
    }

    public List<String> getStatus() {
        return status;
    }

    public List<String> getIniciando() {
        return iniciando;
    }

    public List<String> getValendo() {
        return valendo;
    }

    public String getRestam() {
        return restam;
    }

    public List<String> getFinalizado() {
        return finalizado;
    }

    public String getAb1() {
        return ab1;
    }

    public String getAb2() {
        return ab2;
    }

    public String getTopValue() {
        return topValue;
    }

    public List<String> getDmChegando() {
        return dmChegando;
    }

    public List<String> getDmStart() {
        return dmStart;
    }

    public List<String> getTopHeader() {
        return topHeader;
    }

    public List<String> getTopFooter() {
        return topFooter;
    }

    public List<String> getCancelado() {
        return cancelado;
    }

    public static Mensagens get(){
        return Core.getInstance().getMsgs();
    }

    public String getCamaroteJoin() {
        return camaroteJoin;
    }

    public String getCamaroteLeave() {
        return camaroteLeave;
    }
}
