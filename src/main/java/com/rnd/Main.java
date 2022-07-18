package com.rnd;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Hello world!");
        System.out.println(executeParallel());
        getSnacksForMovie();
    }

    public static String executeParallel() throws ExecutionException, InterruptedException {
        List<String> strings = List.of("test 1", "test 2");
        List<String> strings2 = List.of("test 1", "test 2");
        Executor executor = CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS);
        CompletableFuture<List<String>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            return strings;
        }, executor);

        CompletableFuture<List<String>> listCompletableFuture2 = CompletableFuture.supplyAsync(() -> {
            return strings2;
        }, executor);

        CompletableFuture<String> stringCompletableFuture = listCompletableFuture.thenCombine(listCompletableFuture2, (str1, str2) -> {
            System.out.println(str1 + "" + str2);
            return str1 + "    " + str2;
        });

        String s = stringCompletableFuture.get();
        System.out.println(s);
        return s;
    }

    static CompletableFuture<String> getPopCorn(){
        CompletableFuture<String> future =  CompletableFuture.supplyAsync(() -> {
            return("Popcorn ready");
        });
        return future;
    }
    static CompletableFuture<String> getDrink(){
        CompletableFuture<String> future =    CompletableFuture.supplyAsync(() -> {
            return("Drink ready");
        });
        return future;
    }
    public static String snackReady(){
        return "Order is ready - Enjoy your movie snacks";
    }
    //snacks are ready when popcorn and drink are ready
    public static void getSnacksForMovie(){
        CompletableFuture snacks = getPopCorn()
                .thenCombine(getDrink(),(str1,str2)->{return snackReady();}) ;
    }
}