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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class OnlinePlayer {

    private final Player target;
    private List<String> message = new ArrayList<>();

    public OnlinePlayer(Player target) {
        this.target = target;
        buildMessage();
    }

    private void buildMessage() {
        DataManager dataManager = DataManager.getInstance();
        TimeManager timeManager = TimeManager.getInstance();
        StatManager statManager = StatManager.getInstance();

        message = dataManager.getConfig().getStringList("playtime.message");

        long rawTime;
        if (statManager.isLegacy())
            rawTime = statManager.getPlayerStat(target.getUniqueId(), StatisticType.PLAYTIME);
        else
            rawTime = statManager.getStats().getOnlineStatistic(target, StatisticType.PLAYTIME);

        if (ServerUtils.hasPAPI()) {
            List<String> papiMessage = new ArrayList<>();
            for (String msg : message)
                papiMessage.add(PAPIHolders.getHolders(target, msg));
            message = papiMessage;
        }
        String timeFormat = timeManager.buildFormat(rawTime/20);
        List<String> newMessage = new ArrayList<>();
        for (String msg : message) {
            msg = StringUtils.replace(msg,"%time%", timeFormat);
            msg = StringUtils.replace(msg, "%player%", target.getName());
            msg = StringUtils.replace(msg, "%timesjoined%", Long.toString(statManager.getPlayerStat(target.getUniqueId(), StatisticType.LEAVE)));
            msg = StringUtils.replace(msg, "%joindate%", statManager.getJoinDate(target.getUniqueId()));
            newMessage.add(msg);
        }
        message = newMessage;
    }

    public void sendMessageToTarget() {
        for (String msg : message) {
            if (msg.contains("{\"text\":"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target + " " + ChatUtil.format(msg));
            else
                target.sendMessage(ChatUtil.format(msg));
        }
    }

}
