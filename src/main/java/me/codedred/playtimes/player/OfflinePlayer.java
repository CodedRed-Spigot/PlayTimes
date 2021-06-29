package me.codedred.playtimes.player;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.PAPIHolders;
import me.codedred.playtimes.utils.ServerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OfflinePlayer {

    private final CommandSender sender;
    private final UUID target;
    private final String name;

    private List<String> message = new ArrayList<>();

    public OfflinePlayer(CommandSender sender, UUID target, String name) {
        this.target = target;
        this.sender = sender;
        this.name = name;
        buildMessage();
    }

    private void buildMessage() {
        DataManager dataManager = DataManager.getInstance();
        TimeManager timeManager = TimeManager.getInstance();
        StatManager statManager = StatManager.getInstance();

        message = dataManager.getConfig().getStringList("playtime.message");

        long rawTime = statManager.getPlayerStat(target, StatisticType.PLAYTIME);

        if (ServerUtils.hasPAPI()) {
            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);
            List<String> papiMessage = new ArrayList<>();
            if (offlinePlayer != null) {
                for (String msg : message)
                    papiMessage.add(PAPIHolders.getHolders(offlinePlayer, msg));
            message = papiMessage;
            }
        }
        String timeFormat = timeManager.buildFormat(rawTime/20);
        List<String> newMessage = new ArrayList<>();
        for (String msg : message) {
            msg = StringUtils.replace(msg,"%time%", timeFormat);
            msg = StringUtils.replace(msg, "%player%", name);
            msg = StringUtils.replace(msg, "%timesjoined%", Long.toString(statManager.getPlayerStat(target, StatisticType.LEAVE)));
            msg = StringUtils.replace(msg, "%joindate%", statManager.getJoinDate(target));
            newMessage.add(msg);
        }
        message = newMessage;
    }

    public void sendMessageToTarget() {
        for (String msg : message) {
            if (msg.contains("{\"text\":"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender + " " + ChatUtil.format(msg));
            else
                sender.sendMessage(ChatUtil.format(msg));
        }
    }

}
