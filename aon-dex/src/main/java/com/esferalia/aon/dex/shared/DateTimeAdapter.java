package com.esferalia.aon.dex.shared;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter extends XmlAdapter<String, Date> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public String marshal(Date date) throws Exception {
        synchronized (dateFormat) {
            return dateFormat.format(date);
        }
    }

    @Override
    public Date unmarshal(String value) throws Exception {
        synchronized (dateFormat) {
            return dateFormat.parse(value);
        }
    }

}