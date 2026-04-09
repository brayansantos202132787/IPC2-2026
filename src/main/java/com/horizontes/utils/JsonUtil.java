/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.horizontes.utils;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author braya
 */
public class JsonUtil {
    
    private static final Gson GSON = buildGson();
 
    private JsonUtil() {}
 
    /** Construye el Gson con adaptadores para tipos de fecha Java 8. */
    private static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, typeOfSrc, ctx) ->
                                new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class,
                        (JsonDeserializer<LocalDate>) (json, typeOfT, ctx) ->
                                LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .serializeNulls()
                .create();
    }
 
   
    public static void writeJson(HttpServletResponse resp, Object data) throws IOException {
        write(resp, 200, data);
    }
 
    
    public static void writeJson(HttpServletResponse resp, int status, Object data) throws IOException {
        write(resp, status, data);
    }
 
    
    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("error", message);
        write(resp, status, obj);
    }
 
    
    public static void writeSuccess(HttpServletResponse resp, String message, Object id) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("message", message);
        if (id != null) {
            obj.addProperty("id", id.toString());
        }
        write(resp, 200, obj);
    }
 
    private static void write(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // CORS básico – el filtro AuthFilter también lo pone, pero por si acaso
        resp.setHeader("Access-Control-Allow-Origin", "*");
 
        PrintWriter out = resp.getWriter();
        out.print(GSON.toJson(data));
        out.flush();
    }
 
    
    public static <T> T fromRequest(HttpServletRequest req, Class<T> clazz) throws IOException {
        JsonReader reader = new JsonReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
        return GSON.fromJson(reader, clazz);
    }
 
    /**
     * Lee el cuerpo como JsonObject genérico (útil cuando el body varía).
     */
    public static JsonObject bodyAsObject(HttpServletRequest req) throws IOException {
        JsonReader reader = new JsonReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
        return JsonParser.parseReader(reader).getAsJsonObject();
    }
 
   
 
    public static String toJson(Object obj)              { return GSON.toJson(obj);                   }
    public static <T> T fromJson(String json, Class<T> t){ return GSON.fromJson(json, t);             }
    public static <T> T fromJson(String json, Type t)    { return GSON.fromJson(json, t);             }
 
    public static Gson getGson() { return GSON; }
}

