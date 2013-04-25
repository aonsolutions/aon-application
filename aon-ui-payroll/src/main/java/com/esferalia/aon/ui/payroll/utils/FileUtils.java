package com.esferalia.aon.ui.payroll.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.ContractCode;



public class FileUtils {
	
	public final static String CONTRATA_XML_FILE_ENCODING = "ISO-8859-1";
	
	public final static String CONTRATOS_SCHEMA_FILE_NAME = "EsquemaContratos50.xsd";
	
	public final static String TRANSFORMACIONES_SCHEMA_FILE_NAME = "EsquemaTransformaciones50.xsd";
	
	public final static String PRORROGAS_SCHEMA_FILE_NAME = "EsquemaProrrogas50.xsd";
	
	
	public static void validateContrataXmlPattern(File xmlFile, String SCHEMA, String contractCode) throws IOException, SAXException {
		validateContrataXmlPattern(new FileInputStream(xmlFile), SCHEMA, contractCode);
	}
	
	public static void validateContrataXmlPattern(InputStream xmlStream, String SCHEMA, String contractCode) throws IOException, SAXException {
		File schemaFile = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/schema", SCHEMA);
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFile = getSchemaFile(SCHEMA, urls[0], contractCode);
			if (StringUtils.isBlank(contractCode)) {
				Schema schema = sf.newSchema(urls[0]);
				StreamSource source = new StreamSource(xmlStream);
				Validator validator = schema.newValidator();
				validator.validate(source);
			} else if( schemaFile!=null ){
				Schema schema = sf.newSchema(schemaFile);
				StreamSource source = new StreamSource(xmlStream);
				Validator validator = schema.newValidator();
				validator.validate(source);
			} else {
				AonUtil.addErrorMessage("Imporsible obtener el esquema (XSD) de validación.");
				AonUtil.addErrorMessage("No se ha podido validar el fichero.");
			}
		} catch (SAXException saxe) {
			throw new SAXException(saxe);
		} catch (IOException ioe) {
			throw new IOException(ioe);
		} catch (Exception e) {
			String msg = "Error de formato al validar y generar el XML";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage("*** ERROR *** :" + e );
			throw new AbortProcessingException(msg, e);
		} finally {
			schemaFile.delete();
		}
	}
	
	private static File getSchemaFile(String schema, URL url, String contractCode) {
		if( schema.equals(CONTRATOS_SCHEMA_FILE_NAME) ){
			return obtainContratosSchemaFile(url, contractCode);
		} else if( schema.equals(TRANSFORMACIONES_SCHEMA_FILE_NAME) ){
			return obtainTransformacionesSchemaFile(url, contractCode);
		} else if( schema.equals(PRORROGAS_SCHEMA_FILE_NAME) ){
			return null;	
		}
		return null;	
	}
	
	private static File obtainContratosSchemaFile(URL url, String contractCode) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("CONTRATO_100", contractCode.equals(ContractCode.C100.getValue()));
		map.put("CONTRATO_130", contractCode.equals(ContractCode.C130.getValue()));
		map.put("CONTRATO_150", contractCode.equals(ContractCode.C150.getValue()));
		map.put("CONTRATO_200", contractCode.equals(ContractCode.C200.getValue()));
		map.put("CONTRATO_230", contractCode.equals(ContractCode.C230.getValue()));
		map.put("CONTRATO_250", contractCode.equals(ContractCode.C250.getValue()));
		map.put("CONTRATO_300", contractCode.equals(ContractCode.C300.getValue()));
		map.put("CONTRATO_330", contractCode.equals(ContractCode.C330.getValue()));
		map.put("CONTRATO_350", contractCode.equals(ContractCode.C350.getValue()));
		map.put("CONTRATO_401", contractCode.equals(ContractCode.C401.getValue()));
		map.put("CONTRATO_402", contractCode.equals(ContractCode.C402.getValue()));
		map.put("CONTRATO_403", contractCode.equals(ContractCode.C403.getValue()));
		map.put("CONTRATO_410", contractCode.equals(ContractCode.C410.getValue()));
		map.put("CONTRATO_420", contractCode.equals(ContractCode.C420.getValue()));
		map.put("CONTRATO_421", contractCode.equals(ContractCode.C421.getValue()));
		map.put("CONTRATO_430", contractCode.equals(ContractCode.C430.getValue()));
		map.put("CONTRATO_441", contractCode.equals(ContractCode.C441.getValue()));
		map.put("CONTRATO_450", contractCode.equals(ContractCode.C450.getValue()));
		map.put("CONTRATO_452", contractCode.equals(ContractCode.C452.getValue()));
		map.put("CONTRATO_501", contractCode.equals(ContractCode.C501.getValue()));
		map.put("CONTRATO_502", contractCode.equals(ContractCode.C502.getValue()));
		map.put("CONTRATO_503", contractCode.equals(ContractCode.C503.getValue()));
		map.put("CONTRATO_510", contractCode.equals(ContractCode.C510.getValue()));
		map.put("CONTRATO_520", contractCode.equals(ContractCode.C520.getValue()));
		map.put("CONTRATO_530", contractCode.equals(ContractCode.C530.getValue()));
		map.put("CONTRATO_540", contractCode.equals(ContractCode.C540.getValue()));
		map.put("CONTRATO_541", contractCode.equals(ContractCode.C541.getValue()));
		map.put("CONTRATO_550", contractCode.equals(ContractCode.C550.getValue()));
		map.put("CONTRATO_552", contractCode.equals(ContractCode.C552.getValue()));
		map.put("CONTRATO_970", contractCode.equals(ContractCode.C970.getValue()));
		map.put("CONTRATO_980", contractCode.equals(ContractCode.C980.getValue()));
		map.put("CONTRATO_990", contractCode.equals(ContractCode.C990.getValue()));
		try {
			File tempFile = new File("tmpEsquemaContratos50.xsd");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), CONTRATA_XML_FILE_ENCODING));
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile), CONTRATA_XML_FILE_ENCODING);

			String currentLine;

			while((currentLine = reader.readLine()) != null) {
				if( (StringUtils.contains(currentLine, "CONTRATO_100") && !map.get("CONTRATO_100")) ||
					(StringUtils.contains(currentLine, "CONTRATO_130") && !map.get("CONTRATO_130")) ||
					(StringUtils.contains(currentLine, "CONTRATO_150") && !map.get("CONTRATO_150")) ||
					(StringUtils.contains(currentLine, "CONTRATO_200") && !map.get("CONTRATO_200")) ||
					(StringUtils.contains(currentLine, "CONTRATO_230") && !map.get("CONTRATO_230")) ||
					(StringUtils.contains(currentLine, "CONTRATO_250") && !map.get("CONTRATO_250")) ||
					(StringUtils.contains(currentLine, "CONTRATO_300") && !map.get("CONTRATO_300")) ||
					(StringUtils.contains(currentLine, "CONTRATO_330") && !map.get("CONTRATO_330")) ||
					(StringUtils.contains(currentLine, "CONTRATO_350") && !map.get("CONTRATO_350")) ||
					(StringUtils.contains(currentLine, "CONTRATO_401") && !map.get("CONTRATO_401")) ||
					(StringUtils.contains(currentLine, "CONTRATO_402") && !map.get("CONTRATO_402")) ||
					(StringUtils.contains(currentLine, "CONTRATO_403") && !map.get("CONTRATO_403")) || 
					(StringUtils.contains(currentLine, "CONTRATO_410") && !map.get("CONTRATO_410")) ||
					(StringUtils.contains(currentLine, "CONTRATO_420") && !map.get("CONTRATO_420")) ||
					(StringUtils.contains(currentLine, "CONTRATO_421") && !map.get("CONTRATO_421")) ||
					(StringUtils.contains(currentLine, "CONTRATO_430") && !map.get("CONTRATO_430")) ||
					(StringUtils.contains(currentLine, "CONTRATO_441") && !map.get("CONTRATO_441")) ||
					(StringUtils.contains(currentLine, "CONTRATO_450") && !map.get("CONTRATO_450")) ||
					(StringUtils.contains(currentLine, "CONTRATO_452") && !map.get("CONTRATO_452")) ||
					(StringUtils.contains(currentLine, "CONTRATO_501") && !map.get("CONTRATO_501")) ||
					(StringUtils.contains(currentLine, "CONTRATO_502") && !map.get("CONTRATO_502")) ||
					(StringUtils.contains(currentLine, "CONTRATO_503") && !map.get("CONTRATO_503")) ||
					(StringUtils.contains(currentLine, "CONTRATO_510") && !map.get("CONTRATO_510")) ||
					(StringUtils.contains(currentLine, "CONTRATO_520") && !map.get("CONTRATO_520")) ||
					(StringUtils.contains(currentLine, "CONTRATO_530") && !map.get("CONTRATO_530")) ||
					(StringUtils.contains(currentLine, "CONTRATO_540") && !map.get("CONTRATO_540")) ||
					(StringUtils.contains(currentLine, "CONTRATO_541") && !map.get("CONTRATO_541")) ||
					(StringUtils.contains(currentLine, "CONTRATO_550") && !map.get("CONTRATO_550")) ||
					(StringUtils.contains(currentLine, "CONTRATO_552") && !map.get("CONTRATO_552")) ||
					(StringUtils.contains(currentLine, "CONTRATO_970") && !map.get("CONTRATO_970")) ||
					(StringUtils.contains(currentLine, "CONTRATO_980") && !map.get("CONTRATO_980")) ||
					(StringUtils.contains(currentLine, "CONTRATO_990") && !map.get("CONTRATO_990")) ){
					continue;
				}
			    writer.write(currentLine);
			}
			writer.close();
			return tempFile;
		} catch (FileNotFoundException e) {
			String msg = "Error al obtener el esquema de validacion";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "Error al obtener el esquema de validacion";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}	
	
	private static File obtainTransformacionesSchemaFile(URL url, String contractCode) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
				 
		map.put("T_109", contractCode.equals(ContractCode.C109.getValue()));
		map.put("T_139", contractCode.equals(ContractCode.C139.getValue()));
		map.put("T_189", contractCode.equals(ContractCode.C189.getValue()));
		map.put("T_209", contractCode.equals(ContractCode.C209.getValue()));
		map.put("T_239", contractCode.equals(ContractCode.C239.getValue()));
		map.put("T_289", contractCode.equals(ContractCode.C289.getValue()));
		map.put("T_309", contractCode.equals(ContractCode.C309.getValue()));
		map.put("T_339", false);
		map.put("T_389", contractCode.equals(ContractCode.C389.getValue()));
		
		File tempFile = null;
		try {
			tempFile = new File("tmpEsquemaTransformaciones50.xsd");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), CONTRATA_XML_FILE_ENCODING));
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile), CONTRATA_XML_FILE_ENCODING);
			
			String currentLine;
			
			while((currentLine = reader.readLine()) != null) {
				if( ((StringUtils.contains(currentLine, "TRANSFORMACION_109") && StringUtils.contains(currentLine, "element")) && !map.get("T_109")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_139") && StringUtils.contains(currentLine, "element")) && !map.get("T_139")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_189") && StringUtils.contains(currentLine, "element")) && !map.get("T_189")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_209") && StringUtils.contains(currentLine, "element")) && !map.get("T_209")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_239") && StringUtils.contains(currentLine, "element")) && !map.get("T_239")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_289") && StringUtils.contains(currentLine, "element")) && !map.get("T_289")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_309") && StringUtils.contains(currentLine, "element")) && !map.get("T_309")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_339") && StringUtils.contains(currentLine, "element")) && !map.get("T_339")) ||
						((StringUtils.contains(currentLine, "TRANSFORMACION_389") && StringUtils.contains(currentLine, "element")) && !map.get("T_389")) 
						){
					continue;
				}
				writer.write(currentLine);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			String msg = "Error al obtener el esquema de validacion";
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "Error al obtener el esquema de validacion";
			AonUtil.addErrorMessage(msg);
		}
		return tempFile;
	}	
}
