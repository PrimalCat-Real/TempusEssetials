package primalcat.tempusessential.RPNames;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import primalcat.tempusessential.TempusEssential;
import primalcat.tempusessential.utils.SQLUtils;

import java.util.Arrays;

public class SetRpNickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!sender.hasPermission("tempusessentials.setrpnick")) {
            return true;
        }
        ItemStack itemInHand = ((Player) sender).getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemInHand.getItemMeta();
            if (bookMeta != null  && bookMeta.getPageCount() >= 2) {
                String pageContent = bookMeta.getPage(2);
                // Разделяем содержимое на строки
                String[] lines = pageContent.split("\n");

                // Проверяем минимальное количество строк для извлечения данных
                if (lines.length >= 2) {
                    String playerNick = lines[1].trim(); // Ник игрока
                    String rpNick = lines[2].trim(); // RP ник

                    playerNick = playerNick.replaceFirst("^\\d+\\.\\s*", "");
                    rpNick = rpNick.replaceFirst("^\\d+\\.\\s*", "");

                    try {
                        SQLUtils.savePlayerData(TempusEssential.getPlugin().getDataFolder() + "/database.db", playerNick, rpNick);
                        TempusEssential.customRpNicks.put(playerNick, rpNick);
                        sender.sendMessage("Ник игрока " + playerNick + " сменился на " + rpNick);
                        return true;
                    }catch (Exception e){
                        sender.sendMessage("Ошибка сохранения в базу данных");
                        return false;
                    }
                } else {
                    sender.sendMessage("Информация на второй странице книги неполная.");
                    return false;
                }
            }
        }

        if (args.length < 2) {
            sender.sendMessage("Недостаточно аргументов. Используйте: /setrpnick <имя_игрока> <rp_ник>");
            return true;
        }

        // Извлечение имени игрока и RP ника из аргументов
        String targetPlayerName = args[0];
        String rpNick = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

       try {
           SQLUtils.savePlayerData(TempusEssential.getPlugin().getDataFolder() + "/database.db", targetPlayerName, rpNick);
           TempusEssential.customRpNicks.put(targetPlayerName, rpNick);
           sender.sendMessage("Ник игрока " + targetPlayerName + " сменился на " + rpNick);
           return true;
       }catch (Exception e){
           sender.sendMessage("Ошибка сохранения в базу данных");
           return false;
       }

    }
}