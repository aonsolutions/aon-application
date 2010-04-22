package com.code.aon.calculator.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class IncomeTaxHandler extends DefaultHandler {
    private double grossSalary;
    private int offspringNumber;
    private StringBuffer taxPercent;

    public IncomeTaxHandler(double grossSalary, int offspringNumber, StringBuffer taxPercent) {
        this.grossSalary = grossSalary;
        this.offspringNumber = offspringNumber;
        this.taxPercent = taxPercent;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("segment")) {
            double from = Double.parseDouble(attributes.getValue("from"));
            double to = Double.parseDouble(attributes.getValue("to"));
            if (from <= grossSalary && to >= grossSalary) {
                taxPercent.append(attributes.getValue("p" + offspringNumber));
            }
        }
    }

}