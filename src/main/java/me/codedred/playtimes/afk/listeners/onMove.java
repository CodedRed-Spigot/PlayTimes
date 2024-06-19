package me.codedred.playtimes.afk.listeners;

import me.codedred.playtimes.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class onMove implements Listener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
      return;
    }
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }
}
