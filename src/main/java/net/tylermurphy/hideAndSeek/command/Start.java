package net.tylermurphy.hideAndSeek.command;
import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.tylermurphy.hideAndSeek.Main;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import java.util.Optional;
import java.util.Random;

public class Start implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.isNotSetup()) {
			sender.sendMessage(errorPrefix + message("GAME_SETUP"));
			return;
		}
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(!Board.isPlayer(sender)) {
			sender.sendMessage(errorPrefix + message("GAME_NOT_INGAME"));
			return;
		}
		if(Board.size() < minPlayers) {
			sender.sendMessage(errorPrefix + message("START_MIN_PLAYERS").addAmount(minPlayers));
			return;
		}
		String seekerName;
		if(args.length < 1) {
			Optional<Player> rand = Board.getPlayers().stream().skip(new Random().nextInt(Board.size())).findFirst();
			if(!rand.isPresent()){
				Main.plugin.getLogger().warning("Failed to select random seeker.");
				return;
			}
			seekerName = rand.get().getName();
		} else {
			seekerName = args[0];
		}
		Player seeker = Board.getPlayer(seekerName);
		if(seeker == null) {
			sender.sendMessage(errorPrefix + message("START_INVALID_NAME").addPlayer(seekerName));
			return;
		}
		Game.start(seeker);
	}
	
	public String getLabel() {
		return "start";
	}
	
	public String getUsage() {
		return "<player>";
	}

	public String getDescription() {
		return "Starts the game either with a random seeker or chosen one";
	}

}
