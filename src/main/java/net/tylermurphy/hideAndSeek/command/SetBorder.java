/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.command;

import static net.tylermurphy.hideAndSeek.configuration.Config.*;

import net.tylermurphy.hideAndSeek.game.Game;
import net.tylermurphy.hideAndSeek.util.Status;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static net.tylermurphy.hideAndSeek.configuration.Localization.*;

public class SetBorder implements ICommand {

	public void execute(CommandSender sender, String[] args) {
		if(Game.status != Status.STANDBY) {
			sender.sendMessage(errorPrefix + message("GAME_INPROGRESS"));
			return;
		}
		if(spawnPosition == null) {
			sender.sendMessage(errorPrefix + message("ERROR_GAME_SPAWN"));
			return;
		}
		if(args.length < 3) {
			worldborderEnabled = false;
			addToConfig("worldBorder.enabled",false);
			saveConfig();
			sender.sendMessage(messagePrefix + message("WORLDBORDER_DISABLE"));
			Game.resetWorldborder(spawnWorld);
			return;
		}
		int num,delay,change;
		try { num = Integer.parseInt(args[0]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[0]));
			return;
		}
		try { delay = Integer.parseInt(args[1]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[1]));
			return;
		}
		try { change = Integer.parseInt(args[2]); } catch (Exception e) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_INVALID_INPUT").addAmount(args[2]));
			return;
		}
		if(num < 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_MIN_SIZE"));
			return;
		}
		if(change < 1) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_CHANGE_SIZE"));
			return;
		}
		Vector newWorldborderPosition = new Vector();
		Player player = (Player) sender;
		newWorldborderPosition.setX(player.getLocation().getBlockX());
		newWorldborderPosition.setY(0);
		newWorldborderPosition.setZ(player.getLocation().getBlockZ());
		if(spawnPosition.distance(newWorldborderPosition) > 100) {
			sender.sendMessage(errorPrefix + message("WORLDBORDER_POSITION"));
			return;
		}
		worldborderPosition = newWorldborderPosition;
		worldborderSize = num;
		worldborderDelay = delay;
		worldborderChange = change;
		worldborderEnabled = true;
		addToConfig("worldBorder.x", worldborderPosition.getBlockX());
		addToConfig("worldBorder.z", worldborderPosition.getBlockZ());
		addToConfig("worldBorder.delay", worldborderDelay);
		addToConfig("worldBorder.size", worldborderSize);
		addToConfig("worldBorder.enabled", true);
		addToConfig("worldBorder.move", worldborderChange);
		sender.sendMessage(messagePrefix + message("WORLDBORDER_ENABLE").addAmount(num).addAmount(delay));
		saveConfig();
		Game.resetWorldborder(spawnWorld);
	}

	public String getLabel() {
		return "setBorder";
	}
	
	public String getUsage() {
		return "<size> <delay> <move>";
	}

	public String getDescription() {
		return "Sets worldboarder's center location, size in blocks, and delay in minutes per shrink. Add no arguments to disable.";
	}

}
