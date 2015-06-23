package com.clashwars.dvz.stats;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.mysql.MySQL;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.stats.internal.StatsData;
import com.clashwars.dvz.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    public static String GAME_TYPE = "dvz";

    private DvZ dvz;

    public StatsManager(final DvZ dvz) {
        this.dvz = dvz;

        //Save local stats to config every 30 seconds.
        new BukkitRunnable() {
            @Override
            public void run() {
                dvz.getStatsCfg().save();
            }
        }.runTaskTimer(dvz, 600, 600);
    }


    /* Local stat management */
    public void clearLocalStats() {
        dvz.getStatsCfg().removeStats();
    }

    public void uploadLocalStats() {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean statsSaved = false;
                if (dvz.getSql() != null) {
                    Connection connection = dvz.getSql();
                    if ((dvz.getStatsCfg().STATS != null && dvz.getStatsCfg().STATS.size() > 0) || (dvz.getStatsCfg().SERVER_STATS != null && !dvz.getStatsCfg().SERVER_STATS.isEmpty())) {
                        try {
                            //Create a new game record
                            PreparedStatement createGamePS = connection.prepareStatement("INSERT INTO Games(game_type,date) VALUES(?,?);");
                            createGamePS.setString(1, GAME_TYPE);
                            final Timestamp timestamp = MySQL.getCurrentTimeStamp();
                            createGamePS.setTimestamp(2, timestamp);
                            int rows = createGamePS.executeUpdate();

                            //If it inserted it get the game ID.
                            if (rows >= 1) {
                                PreparedStatement getGamePS = connection.prepareStatement("SELECT game_id FROM Games WHERE date=?;");
                                getGamePS.setTimestamp(1, timestamp);
                                ResultSet gameIdResult = getGamePS.executeQuery();
                                if (gameIdResult.next()) {
                                    //Game inserted and we retrieved the game ID.
                                    //Now insert all the server and player stats for the game.
                                    int gameID = gameIdResult.getInt("game_id");
                                    PreparedStatement statement = dvz.getSql().prepareStatement("INSERT INTO StatData(game_id,player_id,stat_id,value) VALUES(?, ?, ?, ?);");
                                    int i = 0;

                                    //Server stats
                                    if (dvz.getStatsCfg().SERVER_STATS != null && !dvz.getStatsCfg().SERVER_STATS.isEmpty()) {
                                        HashMap<Integer, Float> serverStats = dvz.getStatsCfg().getServerStats().getData();
                                        for (Map.Entry<Integer, Float> stat : serverStats.entrySet()) {
                                            statement.setInt(1, gameID);
                                            statement.setInt(2, 1);
                                            statement.setInt(3, stat.getKey());
                                            statement.setFloat(4, stat.getValue());

                                            statement.addBatch();
                                            i++;
                                        }
                                    }

                                    //Player stats
                                    if (dvz.getStatsCfg().STATS != null && dvz.getStatsCfg().STATS.size() > 0) {
                                        Map<UUID, StatsData> statDataMap = dvz.getStatsCfg().getStats();
                                        for (Map.Entry<UUID, StatsData> entry : statDataMap.entrySet()) {
                                            int playerID = dvz.getPM().getPlayer(entry.getKey()).getCharID();

                                            HashMap<Integer, Float> statsMap = entry.getValue().getData();
                                            for (Map.Entry<Integer, Float> stat : statsMap.entrySet()) {
                                                statement.setInt(1, gameID);
                                                statement.setInt(2, playerID);
                                                statement.setInt(3, stat.getKey());
                                                statement.setFloat(4, stat.getValue());

                                                statement.addBatch();
                                                i++;

                                                if (i % 1000 == 0) {
                                                    statement.executeBatch();
                                                }
                                            }
                                        }
                                    }
                                    if (i % 1000 != 0) {
                                        statement.executeBatch();
                                    }

                                    dvz.log("Inserted " + i + " stats in to the database for game " + gameID + "!");
                                    statsSaved = true;
                                }
                            }

                        } catch(SQLException e) {
                            dvz.log("Failed to upload local stats to MySQL database!");
                        }
                    } else {
                        //No stats to save
                        dvz.log("No stats were saved!");
                        cancel();
                        return;
                    }
                }

                if (!statsSaved) {
                    dvz.getServer().broadcastMessage(CWUtil.integrateColor("&4&lFailed at saving statistics to the database!"));
                    dvz.getServer().broadcastMessage(CWUtil.integrateColor("&4This might mean no stats are being stored for this game!"));
                    Util.broadcastAdmins(Util.formatMsg("&4&lStaff&8: &4If you start a new game now the statistics from this game will be gone forever! " +
                            "Please try using &c/dvz sync uploadall &4several times before opening a new game!"));
                } else {
                    dvz.getServer().broadcastMessage(CWUtil.integrateColor("&a&lStatistics from the previous game are uploaded!"));
                    clearLocalStats();
                }
            }
        }.runTaskAsynchronously(dvz);
    }


    public float getLocalStatVal(Player player, int statID) {
        return getLocalStatVal(player.getUniqueId(), statID);
    }

    public float getLocalStatVal(Player player, StatType statType) {
        return getLocalStatVal(player.getUniqueId(), statType.id);
    }

    public float getLocalStatVal(UUID player, StatType statType) {
        return getLocalStatVal(player, statType.id);
    }

    public float getLocalStatVal(UUID player, int statID) {
        if (statID > 0) {
            if (dvz.getStatsCfg().STATS.containsKey(player.toString())) {
                StatsData sData = dvz.getStatsCfg().getPlayerStats(player);
                return sData.get(statID);
            }
        }
        return -1;
    }


    public void changeLocalStatVal(Player player, int statID, float amount) {
        changeLocalStatVal(player.getUniqueId(), statID, amount);
    }

    public void changeLocalStatVal(Player player, StatType statType, float amount) {
        changeLocalStatVal(player.getUniqueId(), statType.id, amount);
    }

    public void changeLocalStatVal(UUID player, StatType statType, float amount) {
        changeLocalStatVal(player, statType.id, amount);
    }

    public void changeLocalStatVal(UUID player, int statID, float amount) {
        if (statID > 0) {
            if (dvz.getStatsCfg().STATS.containsKey(player.toString())) {
                StatsData sData = dvz.getStatsCfg().getPlayerStats(player);
                sData.change(statID, amount);
                dvz.getStatsCfg().setPlayerStats(player, sData);
            } else {
                StatsData sData = new StatsData();
                sData.set(statID, amount);
                dvz.getStatsCfg().setPlayerStats(player, sData);
            }
        }
    }

    public void changeLocalStatVal(StatType statType, float amount) {
        changeLocalStatVal(statType.id, amount);
    }

    public void changeLocalStatVal(int statID, float amount) {
        if (statID > 0) {
            if (!dvz.getStatsCfg().SERVER_STATS.isEmpty()) {
                StatsData sData = dvz.getStatsCfg().getServerStats();
                sData.change(statID, amount);
                dvz.getStatsCfg().setServerStats(sData);
            } else {
                StatsData sData = new StatsData();
                sData.set(statID, amount);
                dvz.getStatsCfg().setServerStats(sData);
            }
        }
    }


}
