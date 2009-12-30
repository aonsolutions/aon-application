package com.code.aon.calculator;

import java.io.InputStream;

import com.code.aon.calculator.xml.IncomeTaxLoader;

public class IncomeTaxManager {

    private StringBuffer taxPercent = new StringBuffer();

    public IncomeTaxManager(double grossSalary, int offspringNumber) {
        InputStream input = IncomeTaxLoader.class.getResourceAsStream("IncomeTax.xml");
        IncomeTaxLoader.load(input, grossSalary, offspringNumber, taxPercent);
    }

    public double getIncomeTaxPercent() {
        return new Double(taxPercent.toString()).doubleValue();
    }

}