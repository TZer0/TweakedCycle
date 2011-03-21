package tzer0.TweakedCycle.tzer0.TweakedCycle;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Day/night controls!
 *
 * @author TZer0
 */
public class TweakedCycle extends JavaPlugin {
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private LinkedList<Schedule> schedList = new LinkedList<Schedule>();
    String[] states = {"normal", "day", "night"};

    public void onDisable() {
        schedList = new LinkedList<Schedule>();
        getServer().getScheduler().cancelTasks(this);
        System.out.println("TweakedCycle disabled.");
    }

    public void onEnable() {
        reloadWorlds();
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void reloadWorlds() {
        int mode;
        schedList = new LinkedList<Schedule>();
        getServer().getScheduler().cancelTasks(this);
        for (World tmp : getServer().getWorlds()) {
            mode = getConfiguration().getInt(tmp.getName(), 0);
            Schedule sched = new Schedule(mode, tmp);
            schedList.add(sched);
            getServer().getScheduler().scheduleAsyncRepeatingTask(this, sched, 0L, 1000L);
        } 
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    public boolean validMode(int mode) {
        return mode < states.length && mode >= 0;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (commandLabel.equalsIgnoreCase("tc")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED+"You do not have access to that command.");
                return true;
            }
            int i;
            int l = args.length;
            if (l == 0) {
                sender.sendMessage(ChatColor.YELLOW+"General usage:");
                sender.sendMessage(ChatColor.YELLOW+"reload - reloads worlds");
                sender.sendMessage(ChatColor.YELLOW+"list - gives you a list of worlds");
                sender.sendMessage(ChatColor.YELLOW+"set worldname mode(0,1,2/normal,day,night)");
            } else if (args[0].equalsIgnoreCase("list")) {
                int page;
                if (l == 2) {
                    page = Integer.parseInt(args[1]);
                } else {
                    page = 0;
                }
                int limit = Math.min(schedList.size(), (page+1)*10);
                sender.sendMessage(String.format(ChatColor.YELLOW+"Showing worlds from %d to %d", page*10+1, limit));
                int tmp;
                String desc;
                for (i = page*10; i < limit; i++) {
                    tmp = schedList.get(i).getMode();
                    if (validMode(tmp)) {
                        desc = states[schedList.get(i).getMode()];
                    } else {
                        desc = "Invalid mode!";
                    }
                    sender.sendMessage(ChatColor.YELLOW+"Name: " + schedList.get(i).world.getName() + ", mode: " + desc);
                }
                if ((page+1)*10 < limit) {
                    sender.sendMessage(String.format(ChatColor.YELLOW+"/tc list %d to see the next page", page+1 ));
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                reloadWorlds();
                sender.sendMessage(ChatColor.GREEN+"Done");
            } else if (args[0].equalsIgnoreCase("set")) {
                boolean error = false;
                if (l != 3) {
                    error = true;
                } else {
                    Schedule current = null;
                    for (Schedule sched : schedList) {
                        if (sched.world.getName().equalsIgnoreCase(args[1])) {
                            current = sched;
                        }
                    }
                    if (current != null) {
                        int newmode = 0;
                        boolean failed = true;
                        for (int j = 0; j < states.length; j++) {
                            if (states[j].equalsIgnoreCase(args[2])) {
                                newmode = j;
                                failed = false;
                            }
                        }
                        if (failed){
                            for (char c : args[2].toCharArray()) {
                                error = !Character.isDigit(c);
                                if (error) {
                                    break;
                                }
                            }
                            if (!error) {
                                newmode = Integer.parseInt(args[2]);
                            }
                        }
                        if (!error) {
                            if (validMode(newmode)) {
                                current.setMode(newmode);
                                getConfiguration().setProperty(current.world.getName(), newmode);
                                getConfiguration().save();
                                sender.sendMessage(ChatColor.GREEN+"Done.");
                            } else {
                                error = true;
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED+"Invalid world name!");
                    }
                } 
                if (error) {
                    sender.sendMessage(ChatColor.RED+"Usage: /tc set worldname mode, 0 = normal, 1 = day, 2 = night, You may use either normal, day and night or 0, 1, 2 when defining mode");
                }
            }
            return true;
        }
        return false;

    }
    class Schedule extends Thread {
        volatile int mode;
        World world;
        public Schedule(int mode, World world) {
            this.world = world;
            this.mode = mode;
        }
        public void run() {
            settime();
        }
        public void settime() {
            if (mode == 1) {
                world.setFullTime(5500);
            } else if (mode == 2) {
                world.setFullTime(17500);
            }
        }
        public void setMode(int mode) {
            this.mode = mode;
            settime();
        }
        public int getMode() {
            return mode;
        }
    }
}