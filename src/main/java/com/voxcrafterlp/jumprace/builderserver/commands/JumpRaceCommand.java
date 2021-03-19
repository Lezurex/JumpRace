package com.voxcrafterlp.jumprace.builderserver.commands;

import com.voxcrafterlp.jumprace.JumpRace;
import com.voxcrafterlp.jumprace.builderserver.objects.ModuleSetup;
import com.voxcrafterlp.jumprace.modules.objects.Module;
import com.voxcrafterlp.jumprace.modules.utils.ModuleEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 21.12.2020
 * Time: 12:49
 * Project: JumpRace
 */

public class JumpRaceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;

        Player player = (Player) commandSender;

        if(!player.hasPermission(JumpRace.getInstance().getJumpRaceConfig().getBuilderPermission())
                && !player.hasPermission(JumpRace.getInstance().getJumpRaceConfig().getAdminPermission())) {
            player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("no-permissions"));
            return false;
        }

        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "newmodule":
                        new ModuleSetup(player);
                        break;
                    case "list":
                        final List<Module> modules = JumpRace.getInstance().getModuleLoader().getModuleList();

                        if(modules.isEmpty()) {
                            player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("no-modules-found"));
                            return false;
                        }

                        player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("command-jumprace-list"));
                        modules.forEach(module -> player.sendMessage(JumpRace.getInstance().getPrefix() + "§8- §b" + module.getName()));
                        break;
                    default:
                        this.listCommands(player);
                        break;
                }
                break;

            case 2:
                switch (args[0].toLowerCase()) {
                    case "edit":
                        final String moduleName = args[1];
                        boolean found = false;
                        Module module = null;

                        for(Module modules : JumpRace.getInstance().getModuleLoader().getModuleList()) {
                            if(modules.getName().equalsIgnoreCase(moduleName)) {
                                found = true;
                                module = modules;
                            }
                        }

                        if(!found) {
                            player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("invalid-module"));
                            return false;
                        }

                        if(JumpRace.getInstance().getEditorSessions().containsKey(player)) {
                            player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("already-editing"));
                            return false;
                        }

                        if(!player.getWorld().getName().equalsIgnoreCase("jumprace")) {
                            player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("teleport"));
                            player.teleport(new Location(Bukkit.getWorld("jumprace"), 0, 100, 0));
                        }

                        new ModuleEditor(player, module).startEditor();
                        break;
                    default:
                        this.listCommands(player);
                        break;
                }
                break;

            default:
                this.listCommands(player);
                break;
        }
        return false;
    }

    private void listCommands(Player player) {
        player.sendMessage("§8===================================");
        player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("command-jumprace-help-1"));
        player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("command-jumprace-help-2"));
        player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("command-jumprace-help-3"));
        player.sendMessage(JumpRace.getInstance().getLanguageLoader().getTranslationByKeyWithPrefix("command-jumprace-help-4"));
        player.sendMessage("§8===================================");
    }

}
