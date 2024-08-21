import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


class Result {

    /*
     * Complete the 'getWinnerTotalGoals' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. STRING competition
     *  2. INTEGER year
     */

  private static final String MATCH_URL = "https://jsonmock.hackerrank.com/api/football_matches";
    private static final String COMPETITION_URL = "https://jsonmock.hackerrank.com/api/football_competitions";
    
     public static int getWinnerTotalGoals(String competition, int year) {
         int totalGoals = 0;
           try {
               totalGoals = winnerGoals(competition,year );
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    return totalGoals;
        }
    
    public static int drawMatches(int year) throws Exception{
        
        int maxGoal = 10;
        int totalMatch=0;
        
        for(int goal=0; goal<=maxGoal; goal++) {
            totalMatch += getDrawMatchbyGoals(MATCH_URL+"?year="+year, goal);
        }
        
        return totalMatch;
    }
    
    private static int getDrawMatchbyGoals(String url, int goal) throws Exception{
        
        String endpoint = String.format(url+"&team1goals=%d&team2goals=%d",goal,goal);
        String response = getResponsePerPage(endpoint, 1);
        
        JsonObject res = new Gson().fromJson(response, JsonObject.class);
        return res.get("total").getAsInt();
    }

    public static int winnerGoals( String name,int year) throws Exception {
        String url = String.format(COMPETITION_URL+"?year=%d&name=%s", year, URLEncoder.encode(name,"UTF-8"));
        String teamName = getWinnerTeamName(url);
        System.out.println("Winner team :"+teamName);
        return totalGoals(name,year, teamName);
    }
    
    private static String getWinnerTeamName(String url) throws Exception{
        
        String response = getResponsePerPage(url, 1);
        
        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        JsonElement e = jsonResponse.getAsJsonArray("data").get(0);
        
        return e.getAsJsonObject().get("winner").getAsString();
    }

    public static int totalGoals(String competition,int year, String team) throws Exception {
        
        String team1Url = String.format(MATCH_URL+"?competition=%s&year=%d&team1=%s",URLEncoder.encode(competition,"UTF-8"), year, URLEncoder.encode(team,"UTF-8"));
        String team2Url = String.format(MATCH_URL+"?competition=%s&year=%d&team2=%s",URLEncoder.encode(competition,"UTF-8"), year, URLEncoder.encode(team,"UTF-8"));
        
        return getTeamGoals(team1Url,"team1", 1,0) + getTeamGoals(team2Url,"team2", 1,0);
    }

    private static  int getTeamGoals(String teamUrl, String teamtype, int page, int totalGoals) throws Exception {
        
        String response = getResponsePerPage(teamUrl, page);
        
        JsonObject jsonResponse = new Gson().fromJson(response, JsonObject.class);
        int totalPages = jsonResponse.get("total_pages").getAsInt();
        JsonArray data = jsonResponse.getAsJsonArray("data");
        for (JsonElement e : data) {
            totalGoals += e.getAsJsonObject().get(teamtype+"goals").getAsInt();        
        }

        System.out.println(page+"<---totalGoals =>"+totalGoals);
        return totalPages==page? totalGoals : getTeamGoals(teamUrl, teamtype, page+1, totalGoals);
    }

    private static String getResponsePerPage(String endpoint, int page) throws MalformedURLException, IOException, ProtocolException {
        
        System.out.println(String.format(" URL: %s and page: %d", endpoint, page));
        

        URL url = new URL(endpoint+"&page="+page);
        HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
        con.setRequestMethod("GET");
        con.addRequestProperty("Content-Type", "application/json");
        
        int status = con.getResponseCode();
        if(status<200 || status>=300) {
            throw new IOException("Error in reading data with status:"+status);
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String response;
        StringBuilder sb = new StringBuilder();
        while((response = br.readLine())!=null) {
            sb.append(response);
        }
        
        br.close();
        con.disconnect();
        System.out.println("sb.toString() ===>>>"+sb.toString());
        
        return sb.toString();
    }
    }
public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String competition = bufferedReader.readLine();

        int year = Integer.parseInt(bufferedReader.readLine().trim());

        int result = Result.getWinnerTotalGoals(competition, year);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}
