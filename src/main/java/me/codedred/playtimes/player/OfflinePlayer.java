package me.codedred.playtimes.player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.codedred.playtimes.data.DataManager;
import me.codedred.playtimes.data.database.manager.DatabaseManager;
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
  private List<String> message;

  /**
   * Constructor for OfflinePlayer.
   * Initializes the message for the offline player.
   *
   * @param sender The command sender.
   * @param target The UUID of the offline player.
   * @param name   The name of the offline player.
   */
  public OfflinePlayer(CommandSender sender, UUID target, String name) {
    this.sender = sender;
    this.target = target;
    this.name = name;
    this.message = buildMessage();
  }

  /**
   * Builds the message to be sent to the sender about the offline player.
   * This involves fetching playtime statistics, formatting them,
   * and applying any necessary replacements and PlaceholderAPI placeholders.
   *
   * @return A list of formatted strings representing the message.
   */
  private List<String> buildMessage() {
    DataManager dataManager = DataManager.getInstance();
    StatManager statManager = StatManager.getInstance();
    TimeManager timeManager = TimeManager.getInstance();

    List<String> builtMessage = dataManager
      .getConfig()
      .getStringList("playtime.message");
    long rawTime = statManager.getPlayerStat(target, StatisticType.PLAYTIME);

    if (ServerUtils.hasPAPI()) {
      builtMessage = applyPAPIPlaceholders(builtMessage);
    }

    return processMessageWithReplacements(
      builtMessage,
      prepareReplacements(rawTime, statManager, timeManager)
    );
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
      papiMessage.add(
        PAPIHolders.getHolders(Bukkit.getOfflinePlayer(target), msg)
      );
    }
    return papiMessage;
  }

  /**
   * Prepares a map of replacements to be used in the message.
   *
   * @param rawTime     The playtime statistic to format and include in the replacements.
   * @param statManager The StatManager instance to fetch additional statistics.
   * @param timeManager The TimeManager instance to format time.
   * @return A map of placeholder keys and their replacement values.
   */
  private Map<String, String> prepareReplacements(
    long rawTime,
    StatManager statManager,
    TimeManager timeManager
  ) {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("%time%", timeManager.buildFormat(rawTime / 20));
    replacements.put("%player%", name);
    replacements.put(
      "%timesjoined%",
      Long.toString(
        statManager.getPlayerStat(target, StatisticType.TIMES_JOINED)
      )
    );
    replacements.put("%joindate%", statManager.getJoinDate(target));

    if (
      DataManager
        .getInstance()
        .getDBConfig()
        .getBoolean("database-settings.enabled")
    ) {
      replacements.put("%PlayTimes_db_serverId%", "DYNAMIC");
      replacements.put(
        "%PlayTimes_total%",
        timeManager.buildFormat(
          DatabaseManager.getInstance().getTotalPlayTime(target)
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
    boolean dbEnabled = DataManager
      .getInstance()
      .getDBConfig()
      .getBoolean("database-settings.enabled");
    TimeManager timeManager = TimeManager.getInstance();

    for (String msg : message) {
      for (Map.Entry<String, String> entry : replacements.entrySet()) {
        if (entry.getKey().equals("%PlayTimes_db_serverId%") && dbEnabled) {
          // Handle dynamic replacement
          Pattern pattern = Pattern.compile("%PlayTimes_db_(\\w+)%");
          Matcher matcher = pattern.matcher(msg);
          StringBuffer sb = new StringBuffer();

          while (matcher.find()) {
            String serverId = matcher.group(1);
            Long playTime = DatabaseManager
              .getInstance()
              .getPlayTimeForServer(target, serverId);
            matcher.appendReplacement(
              sb,
              playTime != null
                ? timeManager.buildFormat(playTime)
                : timeManager.buildFormat(0)
            );
          }
          matcher.appendTail(sb);
          msg = sb.toString();
        } else {
          // Replace static placeholders
          msg = msg.replace(entry.getKey(), entry.getValue());
        }
      }
      newMessage.add(msg);
    }

    return newMessage;
  }

  /**
   * Sends the formatted message to the sender.
   * This method determines the appropriate method to send the message
   * (e.g., standard chat message or JSON formatted tellraw command).
   */
  public void sendMessageToTarget() {
    for (String msg : message) {
      String formattedMsg = ChatUtil.format(msg);
      if (formattedMsg.contains("{\"text\":")) {
        Bukkit.dispatchCommand(
          Bukkit.getConsoleSender(),
          "tellraw " + sender.getName() + " " + formattedMsg
        );
      } else {
        sender.sendMessage(formattedMsg);
      }
    }
  }
}
