package me.codedred.playtimes.afk.listeners;

import me.codedred.playtimes.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class onJoinQuit implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    AFKManager.getInstance().removePlayer(event.getPlayer().getUniqueId());
  }
}
