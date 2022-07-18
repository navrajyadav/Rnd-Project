package com.rnd;

import com.google.gson.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Example1 {

    public static void main(String[] args) {

        List<CompletableFuture<String>>  futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("executing future 1");

            return "executing future 1";
        }, executorService);

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("executing future 2");
            return "executing future 2";
        }, executorService);

        CompletableFuture<String>  future3 = CompletableFuture.supplyAsync(()->{
            System.out.println("executing future 3");
            return "executing future 3";
        }, executorService);

        futures.add(future1);
        futures.add(future2);
        futures.add(future3);

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        CompletableFuture<List<String>> listCompletableFutures = voidCompletableFuture.thenApply(f -> futures.stream().map(future -> future.join()
        ).collect(Collectors.toList()));

        listCompletableFutures.thenAccept(results->
        {
            System.out.println("result " + results.get(0));
        });
//        List<String> collect = futures.stream().map(f -> f.join()).collect(Collectors.toList());
//        System.out.println(collect);

        GsonBuilder gson = new GsonBuilder();

        JsonArray ja = new JsonArray();
        JsonObject asJsonObject = ja.get(1).getAsJsonObject();
        JsonElement a = asJsonObject.get("a");
//        a.getAs();


    }
}
