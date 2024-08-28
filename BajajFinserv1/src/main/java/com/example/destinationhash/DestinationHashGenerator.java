package com.example.destinationhash;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import org.json.*;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("need 2 input params");
            return;
        }

        String prnNumber = args[0].toLowerCase();
        String filePath = args[1];
        String destinationValue;

        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(jsonString);
            destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("No 'destination' key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString();
            String concatenatedString = prnNumber + destinationValue + randomString;
            String hash = md5Hash(concatenatedString);
            
            System.out.println(hash + ";" + randomString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
        	e.printStackTrace();
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } 
            else if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                	return result;
                }
            } 
        }
        return null;
    }

    private static String generateRandomString() {
        String characters = "1234567890qwertyuiopasdfghjklzxcvbnm";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String md5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
//            System.out.println(messageDigest);
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
