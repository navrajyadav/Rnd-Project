package com.rnd.arcesium;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NavCalculator {


    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException {
        System.out.println(calculateNAV("20190101"));
        List<String> apiResponse = getApiResponse();
        System.out.println(apiResponse);
    }

    /*
     * Complete the 'calculateNAV' function below.
     *
     * The function is expected to return a DOUBLE.
     * The function accepts STRING date as parameter.
     */

    public static Map<String , Map<String, HoldingData>> m;

    public static double calculateNAV(String date) {
        Double navValue = 0.0;
        if(m == null || m.isEmpty()){
            m = new HashMap<>();

            String holdingAllApi = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/holding";
            String holdingResponse  = getApiData(holdingAllApi);
            mapHoldingToResponse(holdingResponse);

            String allPriceAPi = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/pricing";
            String priceResponse  = getApiData(allPriceAPi);
            mapPriceToResponse(priceResponse);
        }

        String dateKey = date.replaceAll("[\\s\\-()]", "");

        if(m.containsKey(dateKey)){
            Map<String , HoldingData> map = m.get(dateKey);
            for(Map.Entry<String , HoldingData> e : map.entrySet()){
                navValue += e.getValue().getQuantity()*e.getValue().getPrice();
            }
        }


        return navValue;
    }

    public static void mapPriceToResponse(String priceResponse){
        try{
            Gson gson = new GsonBuilder().create();
            PriceData[] r = gson.fromJson(priceResponse , PriceData[].class);

            for (PriceData priceData : r) {
                if(m.containsKey(priceData.getDate()) && m.get(priceData.getDate()).containsKey(priceData.getSecurity() )){
                    //     System.out.println(priceData.getDate());
                    m.get(priceData.getDate()).get(priceData.getSecurity()).setPrice(priceData.getPrice());

                }
            }


        /*
        for(Map.Entry<String , Map<String ,HoldingData>> entry : m.entrySet()){
            System.out.println("XXX");
            System.out.println(entry.getKey()) ;

            for(Map.Entry<String , HoldingData> e : entry.getValue().entrySet()){

            System.out.println(e.getKey() + "--value-" + e.getValue().getPrice());

        }
        System.out.println("YYY");
        }
        */

        }catch(Exception ex){
            System.out.println("error in price gson parding =["+ex+"]");
        }
    }

    public static void mapHoldingToResponse(String holdingResponse){
        try{
            Gson gson = new GsonBuilder().create();
            HoldingData[] r = gson.fromJson(holdingResponse , HoldingData[].class);

            for (HoldingData holdingData : r) {
                if(m.containsKey(holdingData.getDate())){
                    m.get(holdingData.getDate()).put(holdingData.getSecurity() ,holdingData);

                }else{
                    Map<String , HoldingData> map = new HashMap<>();
                    map.put(holdingData.getSecurity(), holdingData);
                    m.put(holdingData.getDate(), map);

                }
            }

        }catch(Exception ex){
            System.out.println("error in holding gson parding =["+ex+"]");
        }
    }

    public static List<String> getApiResponse() throws URISyntaxException, ExecutionException, InterruptedException {

        List<String> apiResponse = new ArrayList<>();


        String holdingAllApi = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/holding";
        String all = "https://raw.githubusercontent.com/arcjsonapi/HoldingValueCalculator/master/api/pricing";

        List<URI>  apiUris = Arrays.asList(
                new URI(holdingAllApi),
                new URI(all));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        HttpClient client = HttpClient.newBuilder().executor(executorService).build();

        List<CompletableFuture<String>> response = apiUris.stream()
                .map(uri -> client.sendAsync(HttpRequest.newBuilder(uri).GET().build(), HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body))
                .collect(Collectors.toList());

        for (CompletableFuture<String> future : response) {
            apiResponse.add(future.get());
        }

        return apiResponse;
    }
    public static String getApiData(String urll){
        try{
            URL url = new URL(urll);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            //  System.out.println("response code ="+responseCode);

            String inline ="";
            Scanner scanner = new Scanner(url.openStream());

            while(scanner.hasNext()){
                inline+= scanner.nextLine();
            }

            scanner.close();

            return inline;

        }catch(Exception e){
            System.out.println("exception code ="+e);
        }
        return "";
    }

    public class PriceData{

        private String date;
        private String security;
        private Double price;


        @Override
        public String toString(){
            return this.date;
        }


        public String getDate(){
            return date;
        }

        public String getSecurity(){
            return security;
        }

        public void setDate(String data){
            this.date = data;
        }

        public void setSecurity(String security){
            this.security = security;
        }


        public Double getPrice(){
            return price;
        }

        public void setPrice(Double price){
            this.price = price;
        }
    }


    public class HoldingData{

        private String date;
        private String security;
        private Integer quantity;
        private Double price;
        private String portfolio;

        @Override
        public String toString(){
            return this.date;
        }


        public String getDate(){
            return date;
        }

        public String getSecurity(){
            return security;
        }
        public Integer getQuantity(){
            return quantity;
        }
        public String getPortfolio(){
            return portfolio;
        }

        public void setDate(String data){
            this.date = data;
        }

        public void setSecurity(String security){
            this.security = security;
        }

        public void setQuantity(Integer quantity){
            this.quantity = quantity;
        }

        public void getPortfolio(String portfolio){
            this.portfolio = portfolio;
        }


        public Double getPrice(){
            return price;
        }

        public void setPrice(Double price){
            this.price = price;
        }


    }

}
