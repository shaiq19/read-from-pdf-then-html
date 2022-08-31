package com.example.pdfread;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PdfreadApplication {

    public static void main(String[] args) throws IOException, JSONException {
        SpringApplication.run(PdfreadApplication.class, args);
        PDFGRABBER pdfgrabber= new PDFGRABBER();
    }

}
