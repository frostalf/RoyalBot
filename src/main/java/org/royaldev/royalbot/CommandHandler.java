package org.royaldev.royalbot;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.royaldev.royalbot.commands.IRCCommand;

public class CommandHandler {

    private final Map<String, IRCCommand> commands = new TreeMap<>();

    public void registerCommand(IRCCommand command) {
        final String name = command.getName().toLowerCase();
        synchronized (commands) {
            if (commands.containsKey(name)) {
                return;
            }
            commands.put(name, command);
        }
    }

    public void unregisterCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (!commands.containsKey(name)) {
                return;
            }
            commands.remove(name);
        }
    }

    public IRCCommand getCommand(String name) {
        name = name.toLowerCase();
        synchronized (commands) {
            if (!commands.containsKey(name)) {
                return null;
            }
            return commands.get(name);
        }
    }

    public Collection<IRCCommand> getAllCommands() {
        synchronized (commands) {
            return commands.values();
        }
    }
}
