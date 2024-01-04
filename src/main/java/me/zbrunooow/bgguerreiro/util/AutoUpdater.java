package me.zbrunooow.bgguerreiro.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import org.bukkit.Bukkit;

public class AutoUpdater {

  public boolean autoUpdate() {
    try {
      URL link = new URL("https://pastebin.com/raw/xCUfjJMq");
      URLConnection connection = link.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String text = in.readLine();
      if (text.equals(WarriorEngine.getInstance().getDescription().getVersion())) {
        Bukkit.getConsoleSender()
            .sendMessage(
                WarriorEngine.getInstance().getPrefix()
                    + "§aNao foram encontrados updates para este plugin. Versao mais recente ja instalada: "
                    + text);
        WarriorEngine.getInstance().setUpdateVersion(text);
      } else {
        Bukkit.getConsoleSender()
            .sendMessage(
                WarriorEngine.getInstance().getPrefix()
                    + "§cAtualizacao disponivel! Versao: "
                    + text
                    + " ja existente!");
        Bukkit.getConsoleSender()
            .sendMessage(WarriorEngine.getInstance().getPrefix() + "§cPara pegar a nova versao:");
        Bukkit.getConsoleSender()
            .sendMessage(
                WarriorEngine.getInstance().getPrefix()
                    + "§cAcesse a GB: https://gamersboard.com.br/profile/41269-zbrunooow/");
        WarriorEngine.getInstance().setUpdate(true);
      }
      in.close();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      Bukkit.getConsoleSender()
          .sendMessage(
              WarriorEngine.getInstance().getPrefix()
                  + "§cNao foi possivel a conexao com a internet, nao foi possivel checar por novas atualizacoes.");
      return false;
    }
  }
}
