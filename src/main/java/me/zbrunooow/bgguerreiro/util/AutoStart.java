package me.zbrunooow.bgguerreiro.util;

import java.util.Calendar;
import me.zbrunooow.bgguerreiro.WarriorEngine;
import me.zbrunooow.bgguerreiro.manager.EventManager;
import me.zbrunooow.bgguerreiro.sample.EventStatus;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoStart {
  public static void load() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (String hora : Config.get().getHorarios()) {
          if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                  == Integer.parseInt(hora.substring(0, 2))
              && Calendar.getInstance().get(Calendar.MINUTE)
                  == Integer.parseInt(hora.substring(3, 5))
              && EventManager.getCreated().getStatus() == EventStatus.OFF) {
            EventManager.getCreated().startEvent();
          }
        }
      }
    }.runTaskTimerAsynchronously(WarriorEngine.getInstance(), 0L, 1000L);
  }
}
