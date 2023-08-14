package me.justindevb.serversecurity.acl;

import me.justindevb.serversecurity.ServerSecurity;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccessControlList implements Listener {

    /**
     * AccessControlList
     * Author: Justin
     * Date: 7/29/2023
     *
     * Limit very critical commands, listed in a file, to authorized users listed in a seperate file
     */

        private ServerSecurity serverSecurity;
        private FileConfiguration config;

        private List<UUID> authorizedUsers;
        private List<String> restrictedCommands;

        public AccessControlList(ServerSecurity serverSecurity) {
            this.serverSecurity = serverSecurity;
            this.authorizedUsers = new ArrayList<>();
            this.restrictedCommands = new ArrayList<>();

            loadFiles();
        }

        private void loadFiles() {
            AccessControlListIntegrity.getInstance();
            Bukkit.getScheduler().runTaskAsynchronously(serverSecurity, () -> {
                File file = new File(serverSecurity.getDataFolder(), "RestrictedCommands.yml");
                config = YamlConfiguration.loadConfiguration(file);

                List<String> defaultCommands = new ArrayList<>();
                defaultCommands.add("op");
                defaultCommands.add("deop");
                config.addDefault("RestrictedCommands", defaultCommands);


                try {
                    config.options().copyDefaults(true);
                    config.save(file);
                } catch (IOException e) {
                    serverSecurity.log("Error loading RestrictedCommands.yml", true);
                    e.printStackTrace();
                }

                for (String command : config.getStringList("RestrictedCommands"))
                    restrictedCommands.add(command);


                file = new File(serverSecurity.getDataFolder(), "AuthorizedUsers.yml");
                config = YamlConfiguration.loadConfiguration(file);

                List<String> defaultUsers = new ArrayList<>();
              defaultUsers.add("9bdd0741-2731-43a1-82b7-16c6a3631111"); // justin_393
  //TODO: Change              defaultUsers.add("3721147e-ddad-44ae-940c-b20e721c009e"); // ReborneLogik
                config.addDefault("AuthorizedUsers", defaultUsers);

                try {
                    config.options().copyDefaults(true);
                    config.save(file);
                } catch (IOException e) {
                    serverSecurity.log("Error loading AuthorizedUsers.yml", true);
                    e.printStackTrace();
                }

                for (String user : config.getStringList("AuthorizedUsers"))
                    authorizedUsers.add(UUID.fromString(user));
            });
            AccessControlListIntegrity.getInstance().saveNewChecksum();
        }

        @EventHandler
        public void onCommand(PlayerCommandPreprocessEvent event) {
            String[] message = event.getMessage().split(" ");
            String command = message[0].substring(1);   // Return command without preceding '/'

            /* Detect worldedit commands */
            if (!isRestrictedCommand(command) && !command.startsWith("/"))
                return;

            if (isAuthorizedUser(event.getPlayer().getUniqueId()))
                return;
            event.setCancelled(true);
        }


        /**
         * Check if provided command is restricted to authorized users
         * @param command
         * @return
         */
        private boolean isRestrictedCommand(String command) {
            return restrictedCommands.contains(command);
        }

        /**
         * Check if provided user is an authorized user
         * @param uuid
         * @return
         */
        private boolean isAuthorizedUser(UUID uuid) {
            return authorizedUsers.contains(uuid);
        }

    }



