package me.codedred.playtimes.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.PAPIHolders;
import me.codedred.playtimes.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

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
    StatManager statManager = StatManager.getInstance();
    TimeManager timeManager = TimeManager.getInstance();

    message = dataManager.getConfig().getStringList("playtime.message");

    long rawTime = statManager.getPlayerStat(target, StatisticType.PLAYTIME);

    if (ServerUtils.hasPAPI()) {
      List<String> papiMessage = new ArrayList<>();
      for (String msg : message) {
        papiMessage.add(
          PAPIHolders.getHolders(Bukkit.getOfflinePlayer(target), msg)
        );
      }
      message = papiMessage;
    }

    String timeFormat = timeManager.buildFormat(rawTime / 20);
    List<String> newMessage = new ArrayList<>();
    for (String msg : message) {
      msg = msg.replace("%time%", timeFormat);
      msg = msg.replace("%player%", name);
      msg =
        msg.replace(
          "%timesjoined%",
          Long.toString(
            statManager.getPlayerStat(target, StatisticType.TIMES_JOINED)
          )
        );
      msg = msg.replace("%joindate%", statManager.getJoinDate(target));
      newMessage.add(msg);
    }
    message = newMessage;
  }

  public void sendMessageToTarget() {
    for (String msg : message) {
      if (msg.contains("{\"text\":")) {
        Bukkit.dispatchCommand(
          Bukkit.getConsoleSender(),
          "tellraw " + sender + " " + ChatUtil.format(msg)
        );
      } else {
        sender.sendMessage(ChatUtil.format(msg));
      }
    }
  }
}
