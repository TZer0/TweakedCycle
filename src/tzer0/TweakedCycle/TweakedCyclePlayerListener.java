package tzer0.TweakedCycle;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

// TODO: Auto-generated Javadoc
/**
 * The is a listener interface for receiving PlayerCommandPreprocessEvent events.
 * 
 */
public class TweakedCyclePlayerListener extends PlayerListener  {
    TweakedCycle plugin;

    /**
     * New listener!
     *
     * @param plugin TweakedCycle
     */
    public TweakedCyclePlayerListener(TweakedCycle plugin) {
        this.plugin = plugin;
    }

    /** 
     * Checks if the command is valid and wether the player attempting to do the command has permissions to do so
     * 
     * @see org.bukkit.event.player.PlayerListener#onPlayerCommandPreprocess(org.bukkit.event.player.PlayerCommandPreprocessEvent)
     */
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] command = event.getMessage().split(" ");
        if (command[0].equalsIgnoreCase("/tc")) {
            event.setCancelled(true);
            if ((plugin.permissions == null && player.isOp()) || 
                    (plugin.permissions != null && plugin.permissions.has(player, "tweakedcycle.admin"))) {
                String []args = new String[command.length-1];
                for (int i = 0; i < args.length; i++) {
                    args[i] = command[i+1];
                }
                plugin.onCommand((CommandSender) player, null, command[0].replaceAll("/", ""), args);
            } else {
                player.sendMessage(ChatColor.RED+"You do not have access to this command.");
            }
        }
    }
}
