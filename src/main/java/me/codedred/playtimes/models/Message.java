package me.codedred.playtimes.models;

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
import org.bukkit.command.CommandSender;

public class Message {

  private final CommandSender sender;
  private final UUID targetUUID;
  private final String targetName;
  private List<String> message;

  private final DataManager dataManager;
  private final StatManager statManager;
  private final TimeManager timeManager;

  /**
   * Constructor for Message.
   * Initializes the message for the target player.
   *
   * @param sender The command sender.
   * @param targetUUID The UUID of the offline player.
   * @param targetName   The name of the offline player.
   */
  public Message(CommandSender sender, UUID targetUUID, String targetName) {
    this.sender = sender;
    this.targetUUID = targetUUID;
    this.targetName = targetName;

    this.dataManager = DataManager.getInstance();
    this.statManager = StatManager.getInstance();
    this.timeManager = TimeManager.getInstance();

    this.message = buildMessage();
  }

  /**
   * Builds the message to be sent to the sender about the target player.
   * This involves fetching playtime statistics, formatting them,
   * and applying any necessary replacements and PlaceholderAPI placeholders.
   *
   * @return A list of formatted strings representing the message.
   */
  private List<String> buildMessage() {
    List<String> builtMessage = dataManager
      .getConfig()
      .getStringList("playtime.message");
    long rawTime = statManager.getPlayerStat(
      targetUUID,
      StatisticType.PLAYTIME
    );
    long afkTime = AFKManager.getInstance().getAFKTime(targetUUID);

    if (dataManager.hasDatabase()) {
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.updatePlaytime(targetUUID, rawTime / 20, afkTime);
    }

    if (ServerUtils.hasPAPI()) {
      builtMessage = applyPAPIPlaceholders(builtMessage);
    }

    return processMessageWithReplacements(
      builtMessage,
      prepareReplacements(rawTime, afkTime)
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
        PAPIHolders.getHolders(Bukkit.getOfflinePlayer(targetUUID), msg)
      );
    }
    return papiMessage;
  }

  /**
   * Prepares a map of replacements to be used in the message.
   *
   * @param rawTime     The playtime statistic to format and include in the replacements.
   * @param afkTime     The afktime statistic to format and include in the replacements.
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
    replacements.put("%player%", targetName);
    replacements.put(
      "%timesjoined%",
      Long.toString(
        statManager.getPlayerStat(targetUUID, StatisticType.TIMES_JOINED)
      )
    );
    replacements.put("%joindate%", statManager.getJoinDate(targetUUID));

    if (dataManager.hasDatabase()) {
      DatabaseManager dbManager = DatabaseManager.getInstance();

      long globalPlaytime = dbManager.getTotalPlaytime(targetUUID);
      long globalRawtime = dbManager.getTotalRawtime(targetUUID);
      long globalAfktime = dbManager.getTotalAfktime(targetUUID);

      replacements.put(
        "%global_playtime%",
        timeManager.buildFormat(globalPlaytime)
      );
      replacements.put(
        "%global_rawtime%",
        timeManager.buildFormat(globalRawtime)
      );
      replacements.put(
        "%global_afktime%",
        timeManager.buildFormat(globalAfktime)
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
    boolean hasDatabase = dataManager.hasDatabase();
    DatabaseManager dbManager = hasDatabase
      ? DatabaseManager.getInstance()
      : null;
    Pattern pattern = Pattern.compile("%(rawtime|afktime|playtime)_(\\w+)%");

    for (String msg : message) {
      StringBuilder sb = new StringBuilder();
      if (hasDatabase && dbManager != null) {
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
          String placeholderType = matcher.group(1);
          String serverId = matcher.group(2);
          Long timeValue = 0L;

          if (dbManager.hasTimeForServer(targetUUID, serverId)) {
            Map<String, Long> timeForServer = dbManager.getTimeForServer(
              targetUUID,
              serverId
            );
            timeValue =
              timeForServer.getOrDefault(
                placeholderType.equals("rawtime")
                  ? "playtime"
                  : placeholderType,
                0L
              );
            if (placeholderType.equals("playtime")) {
              timeValue -= timeForServer.getOrDefault("afktime", 0L);
            }
          }

          matcher.appendReplacement(sb, timeManager.buildFormat(timeValue));
        }
        matcher.appendTail(sb);
        msg = sb.toString();
      }

      for (Map.Entry<String, String> entry : replacements.entrySet()) {
        msg =
          msg.replace(
            entry.getKey(),
            entry.getValue() != null ? entry.getValue() : entry.getKey()
          );
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
