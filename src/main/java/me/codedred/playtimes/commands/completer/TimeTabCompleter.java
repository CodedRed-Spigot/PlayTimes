package me.codedred.playtimes.commands.completer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TimeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("pt.use")) {
                completions.add("top");
                completions.addAll(getOnlinePlayerNames());
            }
            if (sender.hasPermission("pt.block")) {
                completions.add("block");
                completions.add("unblock");
            }
            if (sender.hasPermission("pt.reload")) {
                completions.add("reload");
                completions.add("debug");
                completions.add("help");
            }
        }

        return completions;

    }

    private List<String> getOnlinePlayerNames() {
        List<String> playerNames = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }
}
