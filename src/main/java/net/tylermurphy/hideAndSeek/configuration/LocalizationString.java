package net.tylermurphy.hideAndSeek.configuration;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class LocalizationString {
	
	String message;
	
	public LocalizationString(String message) {
		this.message = message;
	}
	
	public LocalizationString addPlayer(Entity player) {
		this.message = message.replaceFirst("\\{PLAYER\\}", player.getName());
		return this;
	}
	
	public LocalizationString addPlayer(CommandSender player) {
		this.message = message.replaceFirst("\\{PLAYER\\}", player.getName());
		return this;
	}
	
	public LocalizationString addPlayer(String player) {
		this.message = message.replaceFirst("\\{PLAYER\\}", player);
		return this;
	}
	
	public LocalizationString addAmount(Integer value) {
		this.message = message.replaceFirst("\\{AMOUNT\\}", value.toString());
		return this;
	}
	
	public LocalizationString addAmount(String value) {
		this.message = message.replaceFirst("\\{AMOUNT\\}", value.toString());
		return this;
	}
	
	public LocalizationString addAmount(Float value) {
		this.message = message.replaceFirst("\\{AMOUNT\\}", value.toString());
		return this;
	}
	
	public String toString() {
		return message;
	}
	
}
