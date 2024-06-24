package primalcat.tempusessential.BookManager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CopyBook implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("tempusessentials.copybook")) {
            return false;
        }

        Player player = (Player) sender;
        // Проверяем, что в обеих руках игрока есть предметы
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        // Проверяем, что в одной руке подписанная книга, а в другой не подписанная
        if (mainHand.getType() == Material.WRITTEN_BOOK && offHand.getType() == Material.WRITABLE_BOOK) {
            copyBook(mainHand, offHand);
            player.sendMessage("Контент скопирован из подписанной книги в не подписанную!");
        } else {
            player.sendMessage("В одной руке должна быть подписанная книга, а в другой - не подписанная!");
        }

        return true;
    }

    private void copyBook(ItemStack source, ItemStack destination) {
        if (source.getType() == Material.WRITTEN_BOOK && destination.getType() == Material.WRITABLE_BOOK) {
            BookMeta sourceMeta = (BookMeta) source.getItemMeta();
            BookMeta destMeta = (BookMeta) destination.getItemMeta();

            destMeta.setTitle(sourceMeta.getTitle());
            destMeta.setAuthor(sourceMeta.getAuthor());
            destMeta.setPages(sourceMeta.getPages());

            destination.setItemMeta(destMeta);
        }
    }
}
