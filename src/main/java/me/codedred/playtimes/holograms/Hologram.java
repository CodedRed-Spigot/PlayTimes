package me.codedred.playtimes.holograms;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private List<String> lines;

    private HoloType type;

    private Location location;

    public Hologram() {
        lines = new ArrayList<>();
        type = HoloType.CUSTOM;
        location = null;
    }

    private void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation(Location location) {
        return location;
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setType(HoloType type) {
        this.type = type;
    }

    public HoloType getType() {
        return type;
    }


}
