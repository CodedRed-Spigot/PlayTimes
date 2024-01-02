package me.codedred.playtimes.afk.listeners;

import me.codedred.playtimes.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class onChat implements Listener {

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }
}
