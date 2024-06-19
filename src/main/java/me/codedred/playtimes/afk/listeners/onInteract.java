package me.codedred.playtimes.afk.listeners;

import me.codedred.playtimes.afk.AFKManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class onInteract implements Listener {

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }
}
