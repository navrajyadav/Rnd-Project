package com.rnd;

import java.io.*;
import java.util.*;
import java.net.*;

import com.google.gson.*;
import static java.util.stream.Collectors.joining;


class Result {

    /*
     * Complete the 'apiResponseParser' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. STRING_ARRAY inputList
     *  2. INTEGER size
     */

    public static List<Integer> apiResponseParser(List<String> inputList, int size) {
        String property = inputList.get(0);
        String operation = inputList.get(1);
        String value = inputList.get(2);
        List<Integer>  listInteger = new ArrayList<>();
        try{
            System.out.println("inputList" + inputList +" : "+ size);
            URL url = new URL("https://raw.githubusercontent.com/arcjsonapi/ApiSampleData/master/api/users");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            InputStream inputStream = url.openStream();
            Scanner scanner = new Scanner(inputStream);
            String responseString = "";
            while(scanner.hasNext()){
                responseString += scanner.nextLine();
            }
            scanner.close();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JsonElement jsonElement = gson.fromJson(responseString, JsonElement.class);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            StringTokenizer st = new StringTokenizer(property,".");
            List<String> properties = new ArrayList<>();
            while (st.hasMoreTokens()){
                properties.add(st.nextToken());
            }
            Map<String,JsonObject> jsonObjectMap = new HashMap<>();
            for(int i=0; i< jsonArray.size(); i++){
                JsonObject parentObject = jsonArray.get(i).getAsJsonObject();
                jsonObjectMap.put("root", parentObject);
                String propertyName = "root";
                for(String property1 : properties){
                    propertyName = propertyName +"." + property1;
                    String parentProperty = propertyName.replace("." + property1, "");
                    parentObject = jsonObjectMap.get(parentProperty);
                    if(parentObject.get(property1).isJsonObject()){
                        JsonObject jsonObject = parentObject.get(property1).getAsJsonObject();
                        jsonObjectMap.put(propertyName, jsonObject);
                    }
                    else if(!parentObject.get(property1).isJsonObject()){
                        String propertyValue = parentObject.get(property1).getAsString();
                        filterData(operation, value, listInteger, jsonObjectMap, propertyValue);
                    }
                }
            }
        }catch(IOException  e){
            System.out.println(e);
        }
        Integer notFound = -1;
        if(listInteger.size() < 1 ){
            listInteger.add(notFound);
        }
        return listInteger;
    }

    private static void filterData(String operation, String value, List<Integer> listInteger, Map<String,JsonObject> jos, String propertyValue) {
        if(operation.equals("EQUALS") && propertyValue.equals(value)){
            Integer id = jos.get("root").get("id").getAsInt();
            listInteger.add(id);
        }
        if(operation.equals("IN")){
            String [] values = value.split(",");
            for(String v : values){
                if(v.trim().equals(propertyValue)){
                    Integer id = jos.get("root").get("id").getAsInt();
                    listInteger.add(id);
                }
            }
        }
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        List<String> inputList = new ArrayList<>();
        // username", "EQUALS", "vinayk"
        inputList.add("address.geo.lat");
        inputList.add("IN");
        inputList.add("29.4572,-31.8129");
        List<Integer> result = ResultV2.apiResponseParser(inputList, inputList.size());

        System.out.println(result);

    }
}
