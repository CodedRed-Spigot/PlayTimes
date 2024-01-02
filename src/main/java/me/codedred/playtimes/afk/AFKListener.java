package me.codedred.playtimes.afk;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AFKListener implements Listener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
      return;
    }
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    AFKManager.getInstance().updateActivity(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    AFKManager.getInstance().removePlayer(event.getPlayer());
  }
}
