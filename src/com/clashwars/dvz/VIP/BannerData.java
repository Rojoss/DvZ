package com.clashwars.dvz.VIP;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.banner.Pattern;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BannerData {

    private DyeColor baseColor;
    private List<Pattern> patterns = new ArrayList<Pattern>();
    private List<Vector> bannerLocations;
    private boolean given = false;

    public BannerData() {
        //--
    }

    public BannerData(BannerData data) {
        baseColor = data.getBaseColor();
        patterns = data.getPatterns();
        bannerLocations = data.getBannerLocations();
    }

    public void setBaseColor(DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public DyeColor getBaseColor() {
        return baseColor;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }


    public Pattern getPattern(int index) {
        if (patterns.size() > index) {
            return patterns.get(index);
        }
        return null;
    }

    public void setPattern(int index, Pattern pattern) {
        patterns.set(index, pattern);
    }

    public void removePattern(int index) {
        patterns.remove(index);
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    public int getPatternCount() {
        return patterns.size();
    }


    public List<Vector> getBannerLocations() {
        return bannerLocations;
    }

    public void setBannerLocations(List<Vector> locs) {
        bannerLocations = locs;
    }

    public void addBannerLocation(Vector loc) {
        if (bannerLocations == null) {
            bannerLocations = new ArrayList<Vector>();
        }
        bannerLocations.add(loc);
    }

    public void removeBannerLocation(Vector loc) {
        if (bannerLocations != null) {
            bannerLocations.remove(loc);
        }
    }

    public void setGiven(boolean given) {
        this.given = given;
    }

    public boolean isGiven() {
        return given;
    }

}
