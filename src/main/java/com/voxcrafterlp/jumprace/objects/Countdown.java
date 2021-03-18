package com.voxcrafterlp.jumprace.objects;

import com.voxcrafterlp.jumprace.JumpRace;
import com.voxcrafterlp.jumprace.minigameserver.scoreboard.PlayerScoreboard;
import com.voxcrafterlp.jumprace.utils.TitleUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 27.02.2021
 * Time: 09:06
 * Project: JumpRace
 */

@Getter @Setter
public class Countdown {

    private final Type type;
    private int timeLeft, taskID;
    private final Runnable runnable;
    private boolean running;

    public Countdown(Type type, Runnable runnable) {
        this.type = type;
        this.timeLeft = type.getDuration();
        this.runnable = runnable;
        this.running = false;
    }

    /**
     * Start the countdown
     */
    public void startCountdown() {
        this.running = true;

        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JumpRace.getInstance(), () -> {
            if(this.timeLeft == 0) {
                this.finish();
                Bukkit.getScheduler().cancelTask(this.taskID);
                return;
            }

            if(this.type == Type.LOBBY || this.type == Type.ENDING) {
                switch (timeLeft) {
                    case 60:
                    case 30:
                    case 15:
                    case 10:
                    case 5:
                        if(this.type == Type.LOBBY)
                            Bukkit.getOnlinePlayers().forEach(player -> new TitleUtil().sendTitle(player, "§bJumpRace", 10, 45, 10));
                    case 4:
                    case 3:
                    case 2:
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(JumpRace.getInstance().getPrefix() + "§7The game " + ((this.type == Type.LOBBY) ? "§bstarts" : "§cends") +
                                    " §7in " + ((this.type == Type.LOBBY) ? "§b" : "§c") + this.timeLeft +" seconds§8.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 10);
                        });
                        break;
                    case 1:
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(JumpRace.getInstance().getPrefix() + "§7The game " + ((this.type == Type.LOBBY) ? "§bstarts" : "§cends") +
                                    " §7in " + ((this.type == Type.LOBBY) ? "§b" : "§c") + this.timeLeft +" second§8.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 10);
                        });
                        break;
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setLevel(this.timeLeft);
                    player.setExp(this.timeLeft * ((float) 1 / this.type.getDuration()));
                });
            }

            if(this.type == Type.JUMPING && this.timeLeft < (this.type.getDuration() - 1)) {
                Bukkit.getOnlinePlayers().forEach(player -> new PlayerScoreboard().
                        updateScoreboard(player, JumpRace.getInstance().getGameManager().getTopScoreboardPlayers()));
            }

            if(this.type == Type.DEATHMATCH) {
                Bukkit.getOnlinePlayers().forEach(player -> new PlayerScoreboard().
                        updateScoreboard(player, JumpRace.getInstance().getGameManager().getTopArenaPlayers()));

                if(this.timeLeft == 60) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendMessage(JumpRace.getInstance().getPrefix() + "§7There is only §cone minute §7left§8! §7You have §c60 seconds§7 to be the closest one to the §cpoint§8!");
                        player.playSound(player.getLocation(), Sound.NOTE_BASS,1,1);
                    });

                    JumpRace.getInstance().getLocationManager().getSelectedMap().spawnEndPoint();
                }
            }

            this.timeLeft--;
        }, 0, 20);
    }

    /**
     * Reset the countdown
     * @param run Determines if the countdown should restart immediately
     */
    public void reset(boolean run) {
        Bukkit.getScheduler().cancelTask(this.taskID);
        this.running = false;
        this.timeLeft = this.type.getDuration();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setLevel(this.type.getDuration());
            player.setExp(this.type.getDuration() * ((float) 1 / this.type.getDuration()));
        });

        if(run) startCountdown();
    }

    /**
     * Stop the countdown
     */
    public void stop() {
        Bukkit.getScheduler().cancelTask(this.taskID);
    }

    /**
     * Execute the {@link Runnable}
     */
    private void finish() {
        this.running = false;
        this.runnable.run();
    }

    /**
     * Format the time left to be displayed in the {@link org.bukkit.scoreboard.Scoreboard}
     * @return Formatted time
     */
    public String getTimeLeftFormatted() {
        final int seconds = this.timeLeft % 60;
        final int minutes = this.timeLeft / 60;

        return ((minutes > 9) ? minutes: "0" + minutes) + ":" + ((seconds > 9) ? seconds: "0" + seconds);
    }

    @Getter
    public enum Type {

        LOBBY(10),
        ENDING(15),
        JUMPING(20),
        DEATHMATCH(480);

        private final int duration;

        Type(int duration) {
            this.duration = duration;
        }

    }
}
