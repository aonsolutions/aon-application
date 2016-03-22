package com.esferalia.aon.occam.server.product;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.esferalia.aon.occam.api.model.product.EcommerceProduct;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class XMLUtils {

	public static byte[] writeXml(EcommerceProduct ecommerceProduct) throws JAXBException, IOException{
		JAXBContext ctx = JAXBContext.newInstance(EcommerceProduct.class);
		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		FileOutputStream fos = new FileOutputStream("/tmp/ecommerceProduct.xml");
		
		marshaller.marshal(ecommerceProduct, fos);
		fos.close();
		FileInputStream fis = new FileInputStream("/tmp/ecommerceProduct.xml");

		return AonIOUtils.toByteArray(fis);
	}
	
	public static EcommerceProduct readXml(byte[] xmlFile) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(EcommerceProduct.class);
		
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		EcommerceProduct ecommerceProduct = (EcommerceProduct) unmarshaller.unmarshal(input);
		
		return ecommerceProduct;
	}
}
