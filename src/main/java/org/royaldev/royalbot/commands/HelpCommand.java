package org.royaldev.royalbot.commands;


import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.CommandHandler;
import org.royaldev.royalbot.RoyalBot;
import org.royaldev.royalbot.auth.Auth;

public class HelpCommand implements IRCCommand {

    final RoyalBot rb = RoyalBot.getInstance();

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "<command>";
    }

    @Override
    public String getDescription() {
        return "Displays all commands!";
    }

    @Override
    public void onCommand(GenericMessageEvent event, String message) {
        final CommandHandler ch = rb.getCommandHandler();
        final User u = event.getUser();
        final boolean userIsAdmin = Auth.checkAuth(u).isAuthed();
        u.send().message("Channel command prefix: \"" + rb.getCommandPrefix() + "\"");
        for (IRCCommand ic : ch.getAllCommands()) {
            if (ic.getAuthLevel() == AuthLevel.ADMIN && !userIsAdmin) {
                continue;
            }
            u.send().message(ic.getName() + " / Description: " + ic.getDescription() + " / Usage: " + ic.getUsage().replaceAll("(?i)<command>", ic.getName()) + " / Type: " + ic.getCommandType().getDescription());
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.BOTH;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.PUBLIC;
    }
}
