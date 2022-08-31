package com.example.pdfread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class PDFGRABBER {
    ArrayList<PDFMODEL> pdfObj = new ArrayList();

    Connection.Response response;
    ArrayList HTML_L = new ArrayList();
/*    ArrayList PDF_L = new ArrayList();*/

    {
        try {
            response = Jsoup.connect("https://bdif.amf-france.org/back/api/v1/informations?typesInformation=VAD&from=0&size=20")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            JSONObject Response = new JSONObject(response.body());
          /*  JSONObject Data = Response.getJSONObject("hits").getJSONObject("total");
            int Total_Records = Data.getInt("value");*/
            JSONArray Base_Response = Response.getJSONObject("hits").getJSONArray("hits");
            for (int i = 0; i < Base_Response.length(); i++) {
                JSONObject Base_Object = Base_Response.getJSONObject(i);
                JSONArray Base_Array = Base_Object.getJSONObject("_source").getJSONArray("documents");
                for (int j = 0; j < Base_Array.length(); j++) {
                    String nested = (String) Base_Array.getJSONObject(j).get("path");
                    if (nested.contains(".html")) {
                        HTML_L.add(nested);
                    } /*else if (nested.contains(".pdf")) {
                        PDF_L.add(nested);
                    }*/
                }
            }

            for(int i=0;i<HTML_L.size();i++){
                String ele= (String) HTML_L.get(i);
             //   Connection.Response execute = Jsoup.connect("https://bdif.amf-france.org/back/api/v1/documents/"+ele).method(Connection.Method.GET).execute();//uses stringbuffer becase string created every time in pool when its alterso better to use buffer
                StringBuffer stringBuffer = new StringBuffer("https://bdif.amf-france.org/back/api/v1/documents/"+ele);
                Connection.Response execute = Jsoup.connect(String.valueOf(stringBuffer)).method(Connection.Method.GET).execute();
                String body = execute.body();

                Document document = Jsoup.parse(body);
                Elements amf = document.select("td:nth-child(2) > div > span:nth-child(22)");
               // System.out.println(amf.text());
                /*String text = amf.text();
                if (text.contains("-ANN") == true){
                    System.out.println(text.contains("-ANN"));//cancled france
                }*/
                Elements publicDisDate =document.select("td:nth-child(2) > div > span:nth-child(27) > span");
               // System.out.println(publicDisDate.text());
                Elements positionHolder= document.select("td:nth-child(2) > div > span:nth-child(23) > span");
               // System.out.println(positionHolder.text());
                Elements nameOfIssuer = document.select("td:nth-child(2) > div > span:nth-child(24) > span");
               // System.out.println(nameOfIssuer.text());
                Elements isin = document.select("td:nth-child(2) > div > span:nth-child(25) > span");
                //System.out.println(isin.text());
                Elements netShortPositionSixzeInPercentage= document.select("td:nth-child(2) > div > span:nth-child(26) > span");
               // System.out.println(netShortPositionSixzeInPercentage.text());
                Elements positionDate = document.select("td:nth-child(2) > div > span:nth-child(29) > span");
              //  System.out.println(positionDate.text());
              //  System.out.println("       -------------------       ");
              //  System.out.println("       -------------------       ");
               // System.out.println("       -------------------       ");

                pdfObj.add(new PDFMODEL(amf.text(),publicDisDate.text(),positionHolder.text(),isin.text(),nameOfIssuer.text(),netShortPositionSixzeInPercentage.text(),positionDate.text()));

            }


          /*  for(int i=0;i<PDF_L.size();i++){
                String ele = (String) PDF_L.get(i);
                Connection.Response execute = Jsoup.connect("https://bdif.amf-france.org/back/api/v1/documents/"+ele).method(Connection.Method.GET).execute();
                System.out.println(execute.body());
                break;
            }
*/

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(pdfObj);
    }

}
