package es.gob.facturae.formato.versiones.facturaev3_2_2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DoubleAdapter extends XmlAdapter<String, Double> {

    private static final DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));

    @Override
    public Double unmarshal(String v) throws Exception {
        return v != null ? Double.valueOf(v) : null;
    }

    @Override
    public String marshal(Double v) throws Exception {
        return v != null ? df.format(v) : null;
    }
}