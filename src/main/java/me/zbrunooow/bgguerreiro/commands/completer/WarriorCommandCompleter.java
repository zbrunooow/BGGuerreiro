package me.zbrunooow.bgguerreiro.commands.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WarriorCommandCompleter implements TabCompleter {
  @Override
  public List<String> onTabComplete(
      CommandSender commandSender, Command command, String s, String[] args) {
    final List<String> completions = new ArrayList<>();

    boolean isAdmin = commandSender.hasPermission("bgguerreiro.admin");
    if (args.length == 1) {
      completions.addAll(Arrays.asList("entrar", "sair", "participar", "quit", "top", "camarote", "status"));
      if (isAdmin)
        completions.addAll(
            Arrays.asList(
                "iniciar",
                "resetkit",
                "forcestart",
                "start",
                "cancelar",
                "forcestop",
                "stop",
                "forcedeathmatch",
                "forcedm",
                "reload",
                "set"));
    } else if (args.length == 2) {
      String subAction = args[0];
      if (subAction.equals("set")) {
        completions.addAll(Arrays.asList("entrada", "saida", "deathmatch", "kit"));
      } else if (subAction.equals("top") || subAction.equals("ranking")) {
        completions.addAll(Arrays.asList("vitorias", "abates"));
      }
    }

    return completions;
  }
}
