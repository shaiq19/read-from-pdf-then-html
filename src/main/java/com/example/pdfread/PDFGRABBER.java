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
    int count= 0;
    int size=20;

    {

        try {
            response = Jsoup.connect("https://bdif.amf-france.org/back/api/v1/informations?typesInformation=VAD&from="+ count +"&size="+ size +"")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();

            getobjects();

            getfilesurl();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        System.out.println(pdfObj);
    }


    private void getobjects() throws JSONException {
        JSONObject Response = new JSONObject(response.body());
        JSONObject Data = Response.getJSONObject("hits").getJSONObject("total");
        int Total_Records = Data.getInt("value");

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
    }


    private void getfilesurl() throws IOException {
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
                System.out.println(text.contains("-ANN"));//cancled france----------
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
/*size++;
count++;
            response = (Connection.Response) Jsoup.connect("https://bdif.amf-france.org/back/api/v1/informations?typesInformation=VAD&from="+ count +"&size="+ size +"");*/
        }


          /*  for(int i=0;i<PDF_L.size();i++){
                String ele = (String) PDF_L.get(i);
                Connection.Response execute = Jsoup.connect("https://bdif.amf-france.org/back/api/v1/documents/"+ele).method(Connection.Method.GET).execute();
                System.out.println(execute.body());
                break;
            }
*/
    }

}




/*
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.hssf.record.formula.functions.T;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


public class ReadPdfFile {
    ArrayList<String> PDF_LIST = new ArrayList<>();
    ArrayList<String> HTML_LIST = new ArrayList<>();
    ArrayList<String> PDF_DATA = new ArrayList<>();

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";


    public JSONObject ApiConnection() throws IOException {
        final String API_URL = "https://bdif.amf-france.org/back/api/v1/informations?typesInformation=VAD&from=0&size=20";
        Connection.Response response = Jsoup.connect(API_URL)
                .userAgent(USER_AGENT)
                .method(Connection.Method.GET)
                .header("content-type", "application/json")
                .followRedirects(true)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .method(Connection.Method.GET)
                .timeout(0)
                .execute();
        JSONObject Response = new JSONObject(response.body());
        this.GetResponse(Response);
        return Response;
    }

    public void ReadPdf(ArrayList<String> PDF_LIST) throws IOException {
        String positionDate = null, NSP = null, ISIN = null, publicationDate = null, pdf_id = null;
        String text = null;
        BufferedInputStream bufferedInputStream;
        int counter = 0;
        for (int i = 0; i < PDF_LIST.size(); i++) {
            counter = i;
            StringBuffer REQ_URL = new StringBuffer("https://bdif.amf-france.org/back/api/v1/documents/" + PDF_LIST.get(i));
            bufferedInputStream = Jsoup.connect(String.valueOf(REQ_URL))
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.GET)
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute().bodyStream();
            try (InputStream inputStream = bufferedInputStream) {
                PdfReader reader = new PdfReader(inputStream);
                text = PdfTextExtractor.getTextFromPage(reader, 1);
                String[] splitPdf;
                splitPdf = text.split("\n");
                PDF_DATA.addAll(Arrays.asList(splitPdf));
                for (String strTemp : PDF_DATA) {
                    if (strTemp.contains("AMF references")) {
                        String[] id = strTemp.split(":");
                        pdf_id = id[1].trim();
                    } else if (strTemp.contains("Public disclosure date (YYYY-MM-DD)")) {
                        int publicationDateIndex = PDF_DATA.indexOf(strTemp);
                        publicationDateIndex++;
                        publicationDate = PDF_DATA.get(publicationDateIndex).trim();
                    } else if (strTemp.contains("NOM DE L'ÉMETTEUR")) {
                        String[] IssuerNameArray = strTemp.split(" ");
                        int lastIndex = IssuerNameArray.length - 1;
                        ISIN = IssuerNameArray[lastIndex];
                    } else if (strTemp.contains("POSITION COURTE NETTE DETENUE EN")) {
                        String[] SplitArr = strTemp.split("%");
                        int lastIndex = SplitArr.length - 1;
                        NSP = SplitArr[lastIndex].trim();
                    } else if (strTemp.contains("DATE DE POSITION (AAAA-MM-JJ)")) {
                        String[] SplitArr = strTemp.split(" ");
                        int lastIndex = SplitArr.length - 1;
                        positionDate = SplitArr[lastIndex].trim();
                    }
                }
                System.out.println(pdf_id + "-" + publicationDate + "-" + ISIN + "-" + NSP + "-" + positionDate);

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public void GetResponse(JSONObject Response) throws IOException {
        int Total_Records = Response.getJSONObject("hits").getJSONObject("total").getInt("value");
        int counter = 0;
        for (int outer = 0; outer < Total_Records; outer += 20) {
            counter += 20;
            final String API_URL = "https://bdif.amf-france.org/back/api/v1/informations?typesInformation=VAD&from=" + outer + "&size=" + counter;
            Connection.Response response = Jsoup.connect(API_URL)
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.GET)
                    .header("content-type", "application/json")
                    .followRedirects(true)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();
            JSONObject res = new JSONObject(response.body());
            try {
                JSONArray Base_Response = Response.getJSONObject("hits").getJSONArray("hits");
                for (int i = 0; i < Base_Response.length(); i++) {
                    JSONObject Base_Object = Base_Response.getJSONObject(i);
                    JSONArray Base_Array = Base_Object.getJSONObject("_source").getJSONArray("documents");
                    for (int inner = 0; inner < Base_Array.length(); inner++) {
                        String nested = (String) Base_Array.getJSONObject(inner).get("path");
                        if (nested.contains(".html")) {
                            HTML_LIST.add(nested);
                        } else if (nested.contains(".pdf")) {
                            PDF_LIST.add(nested);
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ReadPdf(PDF_LIST);
    }
}
 */
