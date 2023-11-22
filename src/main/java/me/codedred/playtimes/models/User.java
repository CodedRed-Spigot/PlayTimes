package me.codedred.playtimes.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class User {

    private UUID uniqueId;
    private boolean enabled;
    private Map<String, Long> playtimes;

    public User(UUID uniqueId, boolean enabled) {
        this.uniqueId = uniqueId;
        this.enabled = enabled;
        this.playtimes = new HashMap<>();
    }

    public void addPlaytime(String serverId, long playtime) {
        playtimes.put(serverId, playtime);
    }

    public long getPlaytime(String serverId) {
        return playtimes.getOrDefault(serverId, 0L);
    }
}
