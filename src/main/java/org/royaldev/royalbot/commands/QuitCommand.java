package org.royaldev.royalbot.commands;

import org.pircbotx.hooks.types.GenericMessageEvent;

public class QuitCommand implements IRCCommand {
    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getUsage() {
        return "<command> (reason)";
    }

    @Override
    public String getDescription() {
        return "Makes the bot quit. :(";
    }

    @Override
    public void onCommand(GenericMessageEvent event, String message) {
        if (message.isEmpty()) {
            event.getBot().sendIRC().quitServer();
        } else {
            event.getBot().sendIRC().quitServer(message);
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}
