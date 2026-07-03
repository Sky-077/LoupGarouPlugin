package fr.dmall.loupgarou.command.subcommand;

import org.bukkit.command.CommandSender;

public abstract class DebugSubCommand implements SubCommand {

    @Override
    public final boolean execute(CommandSender sender, String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cCette commande est réservée aux OP.");
            return true;
        }

        return executeDebug(sender, args);

    }

    protected abstract boolean executeDebug(CommandSender sender, String[] args);

}
