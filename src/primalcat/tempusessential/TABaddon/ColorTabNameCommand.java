package primalcat.tempusessential.TABaddon;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorTabNameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверка, что команду вызвал игрок и что переданы два аргумента
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        // Проверка наличия разрешения
        if (!player.hasPermission("tempusessential.colortabname")) {
            player.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /ttabname <#color1> <#color2>");
            return true;
        }

        String color1 = args[0];
        String color2 = args[1];

//        // Проверяем, что оба аргумента это корректные HEX-коды цветов
        if (!color1.matches("#[A-Fa-f0-9]{6}") || !color2.matches("#[A-Fa-f0-9]{6}")) {
            player.sendMessage("Invalid color format. Please use HEX colors like #FF0000.");
            return true;
        }

        String tabCommand = "tab player "+player.getName()+" customtabname <"+color1+">"+ "%essentials_nickname%"+"</"+color2 +">";
        // Формируем команду, которая будет отправлена от имени консоли
        // Экранируем символы < и >, чтобы они не воспринимались как спецификаторы форматирования
//        String tabCommand = String.format("tab player %s customtabname \\<" + color1 + "\\>%essentials_nickname%\\<" + color2 + "\\>", player.getName());

        // Выполнение команды от имени консоли
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), tabCommand);

        // Сообщение игроку, что команда выполнена
        player.sendMessage("Ваш ник в табе изменен!");

        return true;
    }
}
