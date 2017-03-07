package com.code.aon.conexflow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.code.aon.conexflow.ConexFlow.Query;
import com.esferalia.aon.watson.server.io.AonIOUtils;

public class XMLUtils {
	
	
	public static byte[] writeXml(ConexFlow conexFlow) throws JAXBException, IOException{
		JAXBContext ctx = JAXBContext.newInstance(ConexFlow.class);
		
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		File file = File.createTempFile("conexFlow", ".xml");
		FileOutputStream fos = new FileOutputStream(file);		
		marshaller.marshal(conexFlow, fos);
		fos.close();
		FileInputStream fis = new FileInputStream(file);

		return AonIOUtils.toByteArray(fis);
	}
	
	public static ConexFlow readXml(byte[] xmlFile) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(ConexFlow.class);
		
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		ConexFlow conexFlow = (ConexFlow) unmarshaller.unmarshal(input);
		
		return conexFlow;
	}
	
	public static ConexFlow readXml(byte[] xmlFile, Query query) throws JAXBException{
		JAXBContext ctx = JAXBContext.newInstance(ConexFlow.class);
		
		Unmarshaller unmarshaller = ctx.createUnmarshaller();
	
		InputStream input = new ByteArrayInputStream(xmlFile);
		ConexFlow conexFlow = (ConexFlow) unmarshaller.unmarshal(input);
		conexFlow.setQuery(query);
		conexFlow.setData(xmlFile);
		return conexFlow;
	}
	
}
