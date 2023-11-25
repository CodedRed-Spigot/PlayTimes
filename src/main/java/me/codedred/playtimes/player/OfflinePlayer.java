package me.codedred.playtimes.player;

import java.util.*;
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
    replacements.forEach((placeholder, replacement) -> {
      List<String> processedMessage = new ArrayList<>();
      for (String msg : message) {
        processedMessage.add(msg.replace(placeholder, replacement));
      }
      newMessage.addAll(processedMessage);
    });
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
