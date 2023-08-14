package me.justindevb.serversecurity.acl;


import me.justindevb.serversecurity.ServerSecurity;
import org.bukkit.Bukkit;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccessControlListIntegrity {

    private static AccessControlListIntegrity instance = null;
    private ServerSecurity serverSecurity;

    private String calculatedCheckSum;

    public AccessControlListIntegrity(ServerSecurity serverSecurity){
        this.serverSecurity = serverSecurity;
        this.calculatedCheckSum = calculateChecksum();

        Bukkit.getScheduler().runTaskLater(serverSecurity, () -> {
            saveNewChecksum();
        }, 20L *5);
    }

    private String calculateChecksum() {
        File file = new File(serverSecurity.getDataFolder(), "AuthorizedUsers.yml");

        if (!file.exists())
            return " ";

        byte[] hash = new byte[0];
        try {
            byte[] data = Files.readAllBytes(Paths.get(file.toURI()));
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            serverSecurity.log("Unable to validate AuthorizedUsers checksum!", true);
        }
        String checksum = new BigInteger(1, hash).toString();
        String validChecksum = getValidChecksum();


        if (!checksum.equalsIgnoreCase(validChecksum)) {
            serverSecurity.log("AuthorizedUsers file has been modified!", true);
        }

        return checksum;

    }

    public void saveNewChecksum() {

        File file = new File(serverSecurity.getDataFolder(), "Checksum.data");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Unable to create Checksum.data");
        }

        if (!file.exists())
            return;
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(this.calculatedCheckSum);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getValidChecksum() {

        File file = new File(serverSecurity.getDataFolder(), "Checksum.data");

        if (!file.exists())
            return " ";

        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            reader.close();
            return line;

        } catch (IOException e) {
            e.printStackTrace();
            serverSecurity.log("Unable to validate AuthorizedUsers checksum!", true);
        }

        return null;
    }

    public static AccessControlListIntegrity getInstance() {
        if (instance == null)
            instance = new AccessControlListIntegrity(ServerSecurity.getInstance());
        return instance;

    }
}
