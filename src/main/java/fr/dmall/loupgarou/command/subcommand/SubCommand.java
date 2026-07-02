package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    String getName();

    String getDescription();

    boolean execute(CommandSender sender, String[] args);

}