package org.royaldev.royalbot;

import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.auth.Auth;
import org.royaldev.royalbot.commands.IRCCommand;

public class BaseListeners extends ListenerAdapter {

    private final RoyalBot rb;

    protected BaseListeners(RoyalBot instance) {
        rb = instance;
    }

    @Override
    public void onConnect(ConnectEvent e) {
        rb.getLogger().info("Connected!");
    }

    @Override
    public void onInvite(InviteEvent e) {
        e.getBot().sendIRC().joinChannel(e.getChannel());
        rb.getLogger().info("Invited to " + e.getChannel() + " by " + e.getUser() + ".");
    }

    @Override
    public void onJoin(JoinEvent e) {
        if (!e.getUser().getNick().equals(rb.getBot().getUserBot().getNick())) {
            return;
        }
        List<String> channels = rb.getConfig().getChannels();
        if (channels.contains(e.getChannel().getName())) {
            return;
        }
        channels.add(e.getChannel().getName());
        rb.getConfig().setChannels(channels);
        rb.getLogger().info("Joined " + e.getChannel().getName() + ".");
    }

    @Override
    public void onPart(PartEvent e) {
        if (!e.getUser().getNick().equals(rb.getBot().getUserBot().getNick())) {
            return;
        }
        List<String> channels = rb.getConfig().getChannels();
        if (channels.contains(e.getChannel().getName())) {
            channels.remove(e.getChannel().getName());
        }
        rb.getConfig().setChannels(channels);
        rb.getLogger().info("Parted from " + e.getChannel().getName() + ".");
    }

    @Override
    public void onKick(KickEvent e) {
        if (!e.getUser().getNick().equals(rb.getBot().getUserBot().getNick())) {
            return;
        }
        List<String> channels = rb.getConfig().getChannels();
        if (channels.contains(e.getChannel().getName())) {
            channels.remove(e.getChannel().getName());
        }
        rb.getConfig().setChannels(channels);
        rb.getLogger().info("Kicked from " + e.getChannel().getName() + ".");
    }

    @Override
    public void onGenericMessage(GenericMessageEvent e) {
        if (!(e instanceof MessageEvent) && !(e instanceof PrivateMessageEvent)) {
            return;
        }
        final boolean isPrivateMessage = e instanceof PrivateMessageEvent;
        if (e.getMessage().isEmpty()) {
            return;
        }
        if (e.getMessage().charAt(0) != rb.getCommandPrefix() && !isPrivateMessage) {
            return;
        }
        final String[] split = e.getMessage().trim().split(" ");
        final String commandString = (!isPrivateMessage) ? split[0].substring(1, split[0].length()) : split[0];
        final IRCCommand command = rb.getCommandHandler().getCommand(commandString);
        if (command == null) {
            if (isPrivateMessage) {
                e.respond("No such command.");
            }
            return;
        }
        final IRCCommand.CommandType commandType = command.getCommandType();
        if (!isPrivateMessage && commandType != IRCCommand.CommandType.MESSAGE && commandType != IRCCommand.CommandType.BOTH) {
            return;
        } else if (isPrivateMessage && commandType != IRCCommand.CommandType.PRIVATE && commandType != IRCCommand.CommandType.BOTH) {
            return;
        }
        final IRCCommand.AuthLevel authLevel = command.getAuthLevel();
        if (authLevel == IRCCommand.AuthLevel.ADMIN && !Auth.checkAuth(e.getUser()).isAuthed()) {
            e.respond("You are not an admin!");
            return;
        }
        if (authLevel == IRCCommand.AuthLevel.SUPERADMIN && (!rb.getConfig().getSuperAdmin().equalsIgnoreCase(e.getUser().getNick()) || !Auth.checkAuth(e.getUser()).isAuthed())) {
            e.respond("You are not a superadmin!");
            return;
        }
        rb.getLogger().info(((isPrivateMessage) ? "" : ((MessageEvent) e).getChannel().getName() + "/") + e.getUser().getNick() + ": " + e.getMessage());
        try {
            command.onCommand(e, ArrayUtils.subarray(split, 1, split.length));
        } catch (Exception ex) {
            final StringBuilder sb = new StringBuilder("Unhandled command exception! ");
            sb.append(ex.getClass().getSimpleName()).append(": ").append(ex.getMessage());
            if (rb.getConfig().getPastebinEnabled()) {
                final String pastebin = BotUtils.pastebin(BotUtils.getStackTrace(ex));
                if (pastebin != null) {
                    String url = null;
                    try {
                        url = BotUtils.shortenURL(pastebin);
                    } catch (Exception ignored) {
                    }
                    if (url != null) {
                        sb.append(" (").append(url).append(")");
                    }
                }
            } else {
                ex.printStackTrace();
            }
            e.respond(sb.toString());
        }
    }

}
