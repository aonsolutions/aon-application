package com.code.aon.calculator.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class IncomeTaxLoader {

    public static void load(InputStream input, double grossSalary, int offspringNumber, StringBuffer taxPercent) {
        try {
            SAXParser parser = FACTORY.newSAXParser();
            parser.parse(input, new IncomeTaxHandler(grossSalary, offspringNumber, taxPercent));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

}
