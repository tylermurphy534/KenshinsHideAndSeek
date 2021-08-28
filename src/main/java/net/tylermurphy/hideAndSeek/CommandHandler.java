package net.tylermurphy.hideAndSeek;

import static net.tylermurphy.hideAndSeek.Store.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.commands.*;
import net.tylermurphy.hideAndSeek.util.ICommand;

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
		registerCommand(new Start());
		registerCommand(new Stop());
		registerCommand(new SetSpawnLocation());
		registerCommand(new SetBorder());
		registerCommand(new Reload());
		registerCommand(new SaveMap());
	}
	
	public static boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player == false) {
			sender.sendMessage(errorPrefix + "This command can only be run as a player.");
		} else if(args.length < 1 || !COMMAND_REGISTER.containsKey(args[0].toLowerCase()) ) {
			if(permissionsRequired && !sender.hasPermission("hideandseek.about")) {
				sender.sendMessage(errorPrefix + "You are not allowed to run this command.");
			} else {
				COMMAND_REGISTER.get("about").execute(sender, null);
			}
		} else {
			if(!args[0].toLowerCase().equals("about") && !args[0].toLowerCase().equals("help") && runningBackup) {
				sender.sendMessage(errorPrefix + "Map save is currently in progress. Try again later.");
			} else if(permissionsRequired && !sender.hasPermission("hideandseek."+args[0].toLowerCase())) {
				sender.sendMessage(errorPrefix + "You are not allowed to run this command.");
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

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return CommandHandler.handleCommand(sender, command, label, args);
	}
	
}
