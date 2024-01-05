package me.zbrunooow.bgguerreiro.lang;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public interface Language {
    String getIdentifier();
    FileConfiguration getFile();
    String getNoPermission();
    String getKill();
    String getStatusActionbar();
    String getInfoActionbar();
    String getSurvivorsLeft();
    String getTopValue();
    String getBoxJoin();
    String getBoxLeave();
    String getDeathWithoutReason();
    List<String> getYouWin();
    List<String> getGeneralCommand();
    List<String> getTopHeader();
    List<String> getTopFooter();
    List<String> getStarting();
    List<String> getCancelled();
    List<String> getStatus();
    List<String> getWaiting();
    List<String> getStarted();
    List<String> getDeathmatchComing();
    List<String> getDeathmatchStart();
    List<String> getExpired();
}
