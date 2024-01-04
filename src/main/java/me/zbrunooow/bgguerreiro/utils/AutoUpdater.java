package me.zbrunooow.bgguerreiro.utils;

import me.zbrunooow.bgguerreiro.Core;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AutoUpdater {

    public boolean autoUpdate() {
        try {
            URL link = new URL("https://pastebin.com/raw/xCUfjJMq");
            URLConnection connection = link.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String text = in.readLine();
            if (text.equals(Core.getInstance().getDescription().getVersion())) {
                Bukkit.getConsoleSender().sendMessage(Core.getInstance().prefix + "§aNao foram encontrados updates para este plugin. Versao mais recente ja instalada: " + text);
                Core.getInstance().updateversion = text;
            } else {
                Bukkit.getConsoleSender().sendMessage(Core.getInstance().prefix + "§cAtualizacao disponivel! Versao: " + text + " ja existente!");
                Bukkit.getConsoleSender().sendMessage(Core.getInstance().prefix + "§cPara pegar a nova versao:");
                Bukkit.getConsoleSender().sendMessage(Core.getInstance().prefix + "§cAcesse a GB: https://gamersboard.com.br/profile/41269-zbrunooow/");
                Core.getInstance().update = true;
            }
            in.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Core.getInstance().prefix + "§cNao foi possivel a conexao com a internet, nao foi possivel checar por novas atualizacoes.");
            return false;
        }
    }

}
