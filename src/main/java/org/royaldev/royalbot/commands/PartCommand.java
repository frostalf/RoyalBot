package org.royaldev.royalbot.commands;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class PartCommand implements IRCCommand {
    public String getName() {
        return "part";
    }

    public String getUsage() {
        return "part [channel] (reason)";
    }

    public String getDescription() {
        return "Makes the bot part a channel";
    }

    public void onCommand(GenericMessageEvent event, String[] args) {
        if (args.length < 1) {
            event.respond("Not enough arguments.");
            return;
        }
        final String channel = args[0];
        if (!channel.startsWith("#")) {
            event.respond("Channel did not start with \"#\".");
            return;
        }
        final PircBotX bot = event.getBot();
        final Channel c = bot.getUserChannelDao().getChannel(args[0]);
        if (!bot.getUserBot().getChannels().contains(c)) {
            event.respond("Not in that channel!");
            return;
        }
        if (args.length > 1) c.send().part(StringUtils.join(args, 1, args.length));
        else c.send().part();
        event.respond("Parted from " + channel + ".");
    }

    public CommandType getCommandType() {
        return CommandType.PRIVATE;
    }

    public AuthLevel getAuthLevel() {
        return AuthLevel.ADMIN;
    }
}