package ink.anh.nazzyyoo.lootboxes.commands;

import ink.anh.nazzyyoo.lootboxes.lootbox.LootTableManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCreatingTabCompleter implements TabCompleter {

    private final LootTableManager lootTableManager;

    public ConfigCreatingTabCompleter() {
        this.lootTableManager = LootTableManager.getInstance();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        
    	// Список основних команд
    	if (args.length == 1) {
            return Arrays.asList("create", "delete", "add", "remove", "tool", "reload");
        // Підказки для основних команд
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            // Повертає список таблиць для delete, add, remove, tool
            if (subCommand.equals("delete") || subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals("tool")) {
                return getLootTables();
            }
        
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            
            // Обирання методу зберігання предмета в таблицю луту
            if (subCommand.equals("add") || subCommand.equals("remove")) {
                return Arrays.asList("hand", "offhand");
            }
            // Встановлення часу кд для інструмента
            if (subCommand.equals("tool")) {
            	return Arrays.asList("<int>");
            }
        } else if (args.length == 4) {
        	String subCommand = args[0].toLowerCase();
        	// Додавання підказки, що цей аргумент має бути числом цілим або дробовим
        	if (subCommand.equals("add")) {
        		
        		return Arrays.asList("<double>");
        	}
            // Додавання підказки на тип існуючі типи лутбокса
        	if (subCommand.equals("tool")) {
        		
        		return Arrays.asList("<boolean>");
        	}
        } else if (args.length == 5) {
        	String subCommand = args[0].toLowerCase();
        	// Додавання підказки, що цей аргумент може має бути тільки цілим числом
        	if (subCommand.equals("add")) {
        		
        		return Arrays.asList("<int>");
        	}
        } else if (args.length == 6) {
        	String subCommand = args[0].toLowerCase();
        	// Додавання підказки, що цей аргумент може має бути тільки цілим числом
        	if (subCommand.equals("add")) {
        		
        		return Arrays.asList("<int>");
        	}
        }
        // Повернути пустий список
        return Collections.emptyList();
    }

    private List<String> getLootTables() {
        return lootTableManager.getLootTableCache().keySet().stream().collect(Collectors.toList());
    }
}
