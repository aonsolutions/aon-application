package com.esferalia.aon.dex.shared;

import java.text.DecimalFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdapter extends XmlAdapter<String, Double> {

    private final DecimalFormat numberFormat = new DecimalFormat("#.#");

    @Override
    public String marshal(Double value) throws Exception {
        synchronized (numberFormat) {
            return numberFormat.format(value);
        }
    }

    @Override
    public Double unmarshal(String value) throws Exception {
        synchronized (numberFormat) {
        	return numberFormat.parse(value).doubleValue();
        }
    }

}