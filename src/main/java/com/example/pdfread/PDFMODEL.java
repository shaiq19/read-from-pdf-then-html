package com.example.pdfread;

import lombok.Data;
import org.jsoup.select.Elements;

import javax.swing.text.Element;


@Data
public class PDFMODEL {

    private String amfReference;
    private String publicationDate;
    private String positionHolder;
    private String issuerName;
    private String isin;
    private String netShortPositionSixzeInPercentage;
    private String positionDate;

    public PDFMODEL(String amfReference, String publicationDate, String positionHolder, String issuerName,String isin, String netShortPositionSixzeInPercentage, String positionDate) {
        this.amfReference = amfReference;
        this.publicationDate = publicationDate;
        this.positionHolder = positionHolder;
        this.issuerName = issuerName;
        this.isin=isin;
        this.netShortPositionSixzeInPercentage = netShortPositionSixzeInPercentage;
        this.positionDate = positionDate;
    }
}
