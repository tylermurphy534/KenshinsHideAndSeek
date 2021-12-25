package net.tylermurphy.hideAndSeek.util;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.command.*;

public class CommandHandler {

	public static Map<String,ICommand> COMMAND_REGISTER = new LinkedHashMap<String,ICommand>();
	
	private static void registerCommand(ICommand command) {
		if(!COMMAND_REGISTER.containsKey(command.getLabel())) {
			COMMAND_REGISTER.put(command.getLabel().toLowerCase(), command);
		}
	}
	
	public static void registerCommands() {
		registerCommand(new About());
		registerCommand(new Help());
		registerCommand(new Setup());
		registerCommand(new Start());
		registerCommand(new Stop());
		registerCommand(new SetSpawnLocation());
		registerCommand(new SetLobbyLocation());
		registerCommand(new SetExitLocation());
		registerCommand(new SetBorder());
		registerCommand(new Reload());
		registerCommand(new SaveMap());
		registerCommand(new SetBounds());
		registerCommand(new Join());
		registerCommand(new Leave());
	}
	
	public static boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(errorPrefix + message("COMMAND_PLAYER_ONLY"));
		} else if(args.length < 1 || !COMMAND_REGISTER.containsKey(args[0].toLowerCase()) ) {
			if(permissionsRequired && !sender.hasPermission("hideandseek.about")) {
				sender.sendMessage(errorPrefix + LOCAL.get(""));
			} else {
				COMMAND_REGISTER.get("about").execute(sender, null);
			}
		} else {
			if(!args[0].equalsIgnoreCase("about") && !args[0].equalsIgnoreCase("help") && SaveMap.runningBackup) {
				sender.sendMessage(errorPrefix + message("MAPSAVE_INPROGRESS"));
			} else if(permissionsRequired && !sender.hasPermission("hideandseek."+args[0].toLowerCase())) {
				sender.sendMessage(errorPrefix + message("COMMAND_NOT_ALLOWED"));
			} else {
				try {
					COMMAND_REGISTER.get(args[0].toLowerCase()).execute(sender,Arrays.copyOfRange(args, 1, args.length));
				} catch (Exception e) {
					sender.sendMessage(errorPrefix + "An error has occured.");
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
}
