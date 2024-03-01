package primalcat.tempus.hidecommands;

import primalcat.tempus.TempusEssentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CommandBlocker {
    public CommandBlocker() {
    }

    public static boolean isBlocked(String incomingCommand, String group) {
        if (group == null) {
            return false;
        } else {
//            boolean isCMICommand = incomingCommand.toLowerCase().startsWith("/cmi:");

            // Если команда начинается с "/cmi:", обрезаем "/cmi:"
//            if (isCMICommand) {
//                incomingCommand = incomingCommand.substring(5); // Обрезаем первые 5 символов ("/cmi:")
//            }
            String section = "groups." + group;

            if(incomingCommand.toLowerCase().startsWith("/cmi")){
                incomingCommand = incomingCommand.toLowerCase().replaceFirst("/cmi ", "");
            }else{
                incomingCommand = incomingCommand.toLowerCase().replaceFirst("/", "");
            }
            System.out.println(group + " send command " + incomingCommand);


            String[] command = incomingCommand.split(" ");
            List<String> commandList = new ArrayList();
            boolean isListBlocking = TempusEssentials.getPlugin().getConfig().getString(section + ".group-mode").equalsIgnoreCase("blacklist");
            if (!TempusEssentials.getPlugin().getConfig().getStringList(section + ".included-groups").isEmpty() && !TempusEssentials.getPlugin().getConfig().getStringList(section + ".included-groups").contains("none")) {
                Iterator var6 = TempusEssentials.getPlugin().getConfig().getStringList(section + ".included-groups").iterator();

                while(var6.hasNext()) {
                    String includedGroup = (String)var6.next();
                    commandList.addAll(TempusEssentials.getPlugin().getConfig().getStringList("groups." + includedGroup + ".commands"));
                }
            }

            commandList.addAll(TempusEssentials.getPlugin().getConfig().getStringList(section + ".commands"));

            for(int i = 0; i < commandList.size(); ++i) {
                commandList.set(i, ((String)commandList.get(i)).replace("%space%", " "));
            }

            if (isListBlocking) {

                return isCommandInList(commandList, command);
            } else {
                return !isCommandInList(commandList, command);
            }
        }
    }

    private static boolean isCommandInList(List<String> commandList, String[] command) {
        Iterator var2 = commandList.iterator();

        while(var2.hasNext()) {
            String currentCommandFromList = (String)var2.next();
            int i = 1;
            String[] var5 = currentCommandFromList.split(" ");
            int var6 = var5.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                String currentCommandArg = var5[var7];
                if (currentCommandFromList.split(" ").length < i || command.length < i || !currentCommandArg.equalsIgnoreCase(command[i - 1])) {
                    break;
                }

                if (currentCommandFromList.split(" ").length == i) {
                    return true;
                }

                ++i;
            }
        }

        return false;
    }
}
