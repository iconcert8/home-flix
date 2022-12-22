package com.gyeom.homeflix.login;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class LoginFileRepository implements LoginRepository{

    Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public static final String USERS_FILE_NAME = "home-flix-users.json";
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "1234";

    @Nullable
    @Override
    public User findByUsername(String username) {
        Map<String, User> users = getUsers();
        if(users.containsKey(username)) return users.get(username);
        return null;
    }

    /**
     * List of users in file USERS.
     *
     * @return Key: {@link User#getUsername() username}, Value: {@link User}
     */
    private Map<String, User> getUsers(){
        File file = getUsersFile();
        if(file == null) return new HashMap<>();

        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr);) {
            String str = null;
            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            log.error("Can't not read USERS file '"+file.getName()+"'");
            return new HashMap<>();
        }

        List<User> users = jsonToUsers(stringBuilder.toString());
        Map<String, User> map = new HashMap<>();
        for(User user : users) map.put(user.getUsername(), user);
        return map;
    }

    @Nullable
    private File getUsersFile(){
        File file = new File("./"+USERS_FILE_NAME);
        if(!file.exists()){
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                log.error("Can't not create USERS file. '" + file.getName() +"'");
                return null;
            }

            try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw);) {
                JsonArray json = new JsonArray();
                json.add(userToJson(new User(DEFAULT_USERNAME, DEFAULT_PASSWORD)));
                bw.write(json.toString());
            } catch (IOException e) {
                log.error("Can't not write default user to USERS file. '" + file.getName() +"'");
                return null;
            }
        }

        return file;
    }

    private JsonObject userToJson(User user){
        JsonObject json = new JsonObject();
        json.add(User.STR_USERNAME, new JsonPrimitive(user.getUsername()));
        json.add(User.STR_PASSWORD, new JsonPrimitive(user.getPassword()));
        return json;
    }

    private User jsonToUser(JsonObject json){
        String username = json.getAsJsonPrimitive(User.STR_USERNAME).getAsString();
        String password = json.getAsJsonPrimitive(User.STR_PASSWORD).getAsString();
        return new User(username, password);
//        User user = new User();
//        user.setUsername(username);
//        return user;
    }

    private List<User> jsonToUsers(String jsonStr){
        JsonArray root = JsonParser.parseString(jsonStr).getAsJsonArray();
        List<User> list = new LinkedList<>();
        root.forEach(j ->list.add(jsonToUser(j.getAsJsonObject())));
        return list;
    }
}
