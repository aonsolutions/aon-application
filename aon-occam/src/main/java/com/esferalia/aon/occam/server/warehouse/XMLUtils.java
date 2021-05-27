package com.esferalia.aon.occam.server.warehouse;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLUtils {

	public static String writeXml(CarrierPackingParams params){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CarrierPackingParams.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
	    	marshaller.marshal(params, sw);

	    	String str = sw.toString();
	    	Integer a = str.indexOf("<params>");
			Integer z = str.indexOf("</params>") + 9;	
	    	return str.substring(a,z);
		} catch (JAXBException e) {
			e.printStackTrace();
		}	
		return "";
	}
	
	public static CarrierPackingParams readXml(String xml){
		if(xml == null || xml.equals("")){
			return new CarrierPackingParams();
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CarrierPackingParams.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			StringReader reader = new StringReader(xml);
			CarrierPackingParams params = (CarrierPackingParams) unmarshaller.unmarshal(reader);
			return params;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return new CarrierPackingParams();
	}
}
