package com.example;

import okhttp3.*;

import java.io.IOException;

public class SupabaseService {

    private static final String URL =
         "https://vodzuaibtcjlgsbfsewv.supabase.co/rest/v1/users";

     private static final String API_KEY =
             "sb_publishable_r9OQhS8qX8iXvUmrY_Cngw_TefKIjuF";

     private final OkHttpClient client = new OkHttpClient();

     // ===== РЕГИСТРАЦИЯ =====
     public boolean register(String username, String password) {

         String json =
                 "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

         Request request = new Request.Builder()
                 .url(URL)
                 .post(RequestBody.create(
                         json,
                         MediaType.parse("application/json")
                 ))
                 .addHeader("apikey", API_KEY)
                 .addHeader("Authorization", "Bearer " + API_KEY)
                 .build();

         try (Response response = client.newCall(request).execute()) {
             return response.isSuccessful();
         } catch (IOException e) {
             return false;
         }
     }

     // ===== ВХОД =====
     public boolean login(String username, String password) {

         HttpUrl url = HttpUrl.parse(URL).newBuilder()
                 .addQueryParameter("username", "eq." + username)
                 .addQueryParameter("password", "eq." + password)
                 .build();

         Request request = new Request.Builder()
                 .url(url)
                 .get()
                 .addHeader("apikey", API_KEY)
                 .addHeader("Authorization", "Bearer " + API_KEY)
                 .build();

         try (Response response = client.newCall(request).execute()) {

             String body = response.body().string();

             // если ответ содержит данные → пользователь есть
             return body.contains(username);

         } catch (Exception e) {
             return false;
         }
     }
}