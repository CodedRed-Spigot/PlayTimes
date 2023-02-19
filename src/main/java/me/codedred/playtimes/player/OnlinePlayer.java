package me.codedred.playtimes.player;

import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.PAPIHolders;
import me.codedred.playtimes.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlinePlayer {

    private final Player target;
    private final List<String> message;
    private final DataManager dataManager;
    private final TimeManager timeManager;
    private final StatManager statManager;

    public OnlinePlayer(Player target) {
        this.target = target;
        this.message = new ArrayList<>();
        this.dataManager = DataManager.getInstance();
        this.timeManager = TimeManager.getInstance();
        this.statManager = StatManager.getInstance();
        buildMessage();
    }

    private void buildMessage() {
        long rawTime;
        if (statManager.isLegacy()) {
            rawTime = statManager.getPlayerStat(target.getUniqueId(), StatisticType.PLAYTIME);
        } else {
            rawTime = statManager.getStats().getOnlineStatistic(target, StatisticType.PLAYTIME);
        }

        message.addAll(dataManager.getConfig().getStringList("playtime.message"));

        if (ServerUtils.hasPAPI()) {
            List<String> papiMessage = new ArrayList<>();
            for (String msg : message) {
                papiMessage.add(PAPIHolders.getHolders(target, msg));
            }
            message.clear();
            message.addAll(papiMessage);
        }

        if (message.isEmpty()) {
            return;
        }

        Map<String, String> replacements = new HashMap<>();
        String timeFormat = timeManager.buildFormat(rawTime / 20);
        replacements.put("%time%", timeFormat);
        replacements.put("%player%", target.getName());
        replacements.put("%timesjoined%", Long.toString(statManager.getPlayerStat(target.getUniqueId(), StatisticType.LEAVE)));
        replacements.put("%joindate%", statManager.getJoinDate(target.getUniqueId()));

        Pattern pattern = Pattern.compile("%(\\w+)%");
        List<String> newMessage = new ArrayList<>();
        for (String msg : message) {
            Matcher matcher = pattern.matcher(msg);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String replacement = replacements.get(matcher.group(1));
                if (replacement != null) {
                    matcher.appendReplacement(sb, replacement);
                }
            }
            matcher.appendTail(sb);
            newMessage.add(sb.toString());
        }
        message.clear();
        message.addAll(newMessage);
    }

    public void sendMessageToTarget() {
        String playerName = target.getName();
        for (String msg : message) {
            String formattedMsg = ChatUtil.format(msg)
                    .replace("%time%", TimeManager.getInstance().buildFormat(StatManager.getInstance().getStats().getOnlineStatistic(target, StatisticType.PLAYTIME) / 20))
                    .replace("%player%", playerName)
                    .replace("%timesjoined%", Long.toString(StatManager.getInstance().getPlayerStat(target.getUniqueId(), StatisticType.LEAVE)))
                    .replace("%joindate%", StatManager.getInstance().getJoinDate(target.getUniqueId()));

            if (formattedMsg.contains("{\"text\":")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target + " " + formattedMsg);
            } else {
                target.sendMessage(formattedMsg);
            }
        }
    }
}