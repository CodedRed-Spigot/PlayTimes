package me.codedred.playtimes.server;

import java.io.IOException;
import java.util.UUID;

public interface ServerStatus {

    public UUID getUUID(String name);
    public boolean isOnline();
    public String getName(UUID uuid) throws IOException;
}
