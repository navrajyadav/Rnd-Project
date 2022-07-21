package com.rnd.arcesium;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NavCalculatorV2 {

    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {


        calculateNav("20191219");

        System.exit(0);
        System.out.println("completed");

    }

    public static BigDecimal calculateNav(String date) throws URISyntaxException, ExecutionException, InterruptedException {
        List<String> apiResponse = getApiResponse();
        String holdingApiResponse = apiResponse.get(0);
        String priceApiResponse = apiResponse.get(1);

        Gson gson = new GsonBuilder().create();
        Holding[] holdings = gson.fromJson(holdingApiResponse, Holding[].class);
        Price[] prices = gson.fromJson(priceApiResponse, Price[].class);

        Map<String, List<Price>> priceMap = Arrays.stream(prices).filter(f-> f.getPrice() != null).collect(
                Collectors.groupingBy(p -> getGroupingByKey(p),
                        Collectors.mapping((Price pp)->pp,Collectors.toList())));
        List<Holding> filteredData =  Arrays.stream(holdings).filter(f -> f.getDate().equals(date)).collect(Collectors.toList());

        BigDecimal totalValue = new BigDecimal("0");
        for(Holding holding : filteredData){
            List<Price> prices1 = priceMap.get(holding.getDate()+"-"+holding.getSecurity());
            if (prices1 != null) {
                Price price = prices1.get(0);
                BigDecimal multiply = price.getPrice().multiply(holding.getQuantity());
                System.out.println(multiply.doubleValue());
                totalValue = totalValue.add(multiply);
            }else{
                System.out.println(holding);
            }
        }
        System.out.println("total Value :: "+totalValue);

        return totalValue;
    }

    private static String getGroupingByKey(Price p){
        return p.getDate()+"-"+p.getSecurity();
    }

    public static List<String> getApiResponse() throws URISyntaxException, ExecutionException, InterruptedException {


        String holdingListApi = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/holding";
        String priceListApi = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/pricing";

        List<URI> apiUris = Arrays.asList(
                new URI(holdingListApi),
                new URI(priceListApi));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        HttpClient client = HttpClient.newBuilder().executor(executorService).build();

        List<CompletableFuture<String>> response = apiUris.stream()
                .map(uri -> client.sendAsync(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body))
                .collect(Collectors.toList());

        List<String> collect = response.stream().map(CompletableFuture::join).collect(Collectors.toList());

//        for (CompletableFuture<String> future : response) {
//            apiResponse.add(future.get());
//        }

        return collect;
    }
}
