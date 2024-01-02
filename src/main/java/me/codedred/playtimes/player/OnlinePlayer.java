package me.codedred.playtimes.player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.codedred.playtimes.afk.AFKManager;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.statistics.StatisticType;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import me.codedred.playtimes.utils.PAPIHolders;
import me.codedred.playtimes.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayer {

  private final Player target;
  private final List<String> message;
  private final DataManager dataManager;
  private final StatManager statManager;
  private final TimeManager timeManager;

  /**
   * Constructor for OnlinePlayer.
   * Initializes the required managers and builds the message for the player.
   *
   * @param target The player for whom this OnlinePlayer instance is created.
   */
  public OnlinePlayer(Player target) {
    this.target = target;
    this.dataManager = DataManager.getInstance();
    this.statManager = StatManager.getInstance();
    this.timeManager = TimeManager.getInstance();
    this.message = buildMessage();
  }

  /**
   * Builds the message to be sent to the player.
   * This involves fetching playtime statistics, formatting them,
   * and applying any necessary replacements and PlaceholderAPI placeholders.
   *
   * @return A list of formatted strings representing the message.
   */
  private List<String> buildMessage() {
    List<String> builtMessage = new ArrayList<>();
    long rawTime = getPlaytimeStat();
    long afkTime = AFKManager.getInstance().getAFKTime(target.getUniqueId());

    builtMessage.addAll(
      dataManager.getConfig().getStringList("playtime.message")
    );

    if (ServerUtils.hasPAPI()) {
      builtMessage = applyPAPIPlaceholders(builtMessage);
    }

    return processMessageWithReplacements(
      builtMessage,
      prepareReplacements(rawTime, afkTime)
    );
  }

  /**
   * Retrieves the playtime statistic for the player.
   *
   * @return The raw playtime statistic.
   */
  private long getPlaytimeStat() {
    return statManager.isLegacy()
      ? statManager.getPlayerStat(target.getUniqueId(), StatisticType.PLAYTIME)
      : statManager
        .getStats()
        .getOnlineStatistic(target, StatisticType.PLAYTIME);
  }

  /**
   * Applies PlaceholderAPI placeholders to the message if available.
   *
   * @param message The message list to process.
   * @return A list of messages with placeholders applied.
   */
  private List<String> applyPAPIPlaceholders(List<String> message) {
    List<String> papiMessage = new ArrayList<>();
    for (String msg : message) {
      papiMessage.add(PAPIHolders.getHolders(target, msg));
    }
    return papiMessage;
  }

  /**
   * Prepares a map of replacements to be used in the message.
   *
   * @param rawTime The playtime statistic to format and include in the replacements.
   * @return A map of placeholder keys and their replacement values.
   */
  private Map<String, String> prepareReplacements(long rawTime, long afkTime) {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("%time%", timeManager.buildFormat(rawTime / 20 - afkTime));
    replacements.put(
      "%playtime%",
      timeManager.buildFormat(rawTime / 20 - afkTime)
    );
    replacements.put("%rawtime%", timeManager.buildFormat(rawTime / 20));
    replacements.put("%afktime%", timeManager.buildFormat(afkTime));
    replacements.put("%player%", target.getName());
    replacements.put(
      "%timesjoined%",
      Long.toString(
        statManager.getPlayerStat(
          target.getUniqueId(),
          StatisticType.TIMES_JOINED
        )
      )
    );
    replacements.put(
      "%joindate%",
      statManager.getJoinDate(target.getUniqueId())
    );

    if (DataManager.getInstance().hasDatabase()) {
      replacements.put("%playtime_serverId%", "DYNAMIC");
      replacements.put("%afktime_serverId%", "DYNAMIC");
      replacements.put("%rawtime_serverId%", "DYNAMIC");
      replacements.put(
        "%global_playtime%",
        timeManager.buildFormat(
          DatabaseManager
            .getInstance()
            .getRawTotalPlaytime(target.getUniqueId())
        )
      );
      replacements.put(
        "%global_rawtime%",
        timeManager.buildFormat(
          DatabaseManager
            .getInstance()
            .getRawTotalPlaytime(target.getUniqueId())
        )
      );
      replacements.put(
        "%global_afktime%",
        timeManager.buildFormat(
          DatabaseManager.getInstance().getRawTotalAfktime(target.getUniqueId())
        )
      );
    }

    return replacements;
  }

  /**
   * Processes the message by applying the necessary replacements.
   *
   * @param message      The original message.
   * @param replacements The map of placeholders and their replacements.
   * @return A list of processed messages.
   */
  private List<String> processMessageWithReplacements(
    List<String> message,
    Map<String, String> replacements
  ) {
    List<String> newMessage = new ArrayList<>();

    for (String msg : message) {
      if (DataManager.getInstance().hasDatabase()) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern
          .compile("%(rawtime|afktime|playtime)_(\\w+)%")
          .matcher(msg);
        while (matcher.find()) {
          String placeholderType = matcher.group(1);
          String serverId = matcher.group(2);
          Long timeValue = DatabaseManager
            .getInstance()
            .getTimeForServer(target.getUniqueId(), serverId)
            .get(placeholderType != "rawtime" ? placeholderType : "playtime");

          if (placeholderType == "playtime") {
            timeValue -=
              DatabaseManager
                .getInstance()
                .getTimeForServer(target.getUniqueId(), serverId)
                .get("afktime");
          }

          matcher.appendReplacement(
            sb,
            timeValue != null
              ? timeManager.buildFormat(timeValue)
              : timeManager.buildFormat(0)
          );
        }
        matcher.appendTail(sb);
        msg = sb.toString();
      }

      // Replace other static placeholders
      for (Map.Entry<String, String> entry : replacements.entrySet()) {
        msg = msg.replace(entry.getKey(), entry.getValue());
      }

      newMessage.add(msg);
    }

    return newMessage;
  }

  /**
   * Sends the formatted message to the target player.
   * This method determines the appropriate method to send the message
   * (e.g., standard chat message or JSON formatted tellraw command).
   */
  public void sendMessageToTarget() {
    for (String msg : message) {
      String formattedMsg = ChatUtil.format(msg);
      if (formattedMsg.contains("{\"text\":")) {
        Bukkit.dispatchCommand(
          Bukkit.getConsoleSender(),
          "tellraw " + target.getName() + " " + formattedMsg
        );
      } else {
        target.sendMessage(formattedMsg);
      }
    }
  }
}
