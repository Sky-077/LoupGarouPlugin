package fr.dmall.loupgarou.command;

import fr.dmall.loupgarou.command.subcommand.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class LGCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public LGCommand() {
        register(new HelpSubCommand());
        register(new InfoSubCommand());
        register(new StartSubCommand());
        register(new StopSubCommand());
        register(new RoleSubCommand());
    }

    private void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            return subCommands.get("help").execute(sender, args);
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());

        if (subCommand == null) {
            sender.sendMessage("§cSous-commande inconnue.");
            return true;
        }

        return subCommand.execute(sender, args);
    }
}