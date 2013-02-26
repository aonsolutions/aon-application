package com.esferalia.aon.ui.payroll.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contract.model.IContratoType;
import com.esferalia.aon.file.payroll.contract.model.IProrrogaType;
import com.esferalia.aon.file.payroll.contract.model.ITransformacionType;
import com.esferalia.aon.file.payroll.contrata.ContractContrataFactory;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATOS;
import com.esferalia.aon.file.payroll.contrata.model.prorrogas.PRORROGAS;
import com.esferalia.aon.file.payroll.contrata.model.prorrogas.PRORROGATIPOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContrataWriter {
	
	private final String CONTRATA_XML_FILE_ENCODING = "ISO-8859-1";
	private final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.file.payroll.contrata.model.contratos";
	private final String CONTRATA_TRANSFORMACIONES_MODEL_PATH = "com.esferalia.aon.file.payroll.contrata.model.transformaciones";
	private final String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.file.payroll.contrata.model.prorrogas";
	private final String CONTRATOS_SCHEMA_FILE_NAME = "EsquemaContratos50.xsd";
	private final String TRANSFORMACIONES_SCHEMA_FILE_NAME = "EsquemaTransformaciones50.xsd";
	private final String PRORROGAS_SCHEMA_FILE_NAME = "EsquemaProrrogas50.xsd";
	
	private com.esferalia.aon.file.payroll.contrata.model.contratos.ObjectFactory contratoFactory = new com.esferalia.aon.file.payroll.contrata.model.contratos.ObjectFactory();

	private com.esferalia.aon.file.payroll.contrata.model.transformaciones.ObjectFactory transformacionFactory = new com.esferalia.aon.file.payroll.contrata.model.transformaciones.ObjectFactory();;
	
	private com.esferalia.aon.file.payroll.contrata.model.prorrogas.ObjectFactory prorrogaFactory = new com.esferalia.aon.file.payroll.contrata.model.prorrogas.ObjectFactory();;

	private String fileName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File createFile(ContrataParams params) throws ManagerBeanException, IOException{
		if(params.getContract()==null){
			String msg = "El contrato no se ha cargado correctamente.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		boolean contratoFile = false;
		boolean transfonacionFile = false;
		boolean prorrogaFile = false;
		
		String code = getContractDataMap(params.getContract()).get(ContextVariable.TC2.getName());

		if( StringUtils.isBlank(code) ) {
			String msg = "Contrato no reconocido";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else if(code.equals(ContractCode.C109.getValue())
				 || code.equals(ContractCode.C139.getValue())
				 || code.equals(ContractCode.C189.getValue())
				 || code.equals(ContractCode.C209.getValue())
				 || code.equals(ContractCode.C239.getValue())
				 || code.equals(ContractCode.C289.getValue())
				 || code.equals(ContractCode.C309.getValue())
//	TODO: nueva clave de contrato - Boletin Noticias RED 2012/05
//				 || code.equals(ContractCode.C339.getValue())
				 || code.equals(ContractCode.C389.getValue()) ){
			transfonacionFile = true;
		} else if (code.equals(ContractCode.C408.getValue())
				 || code.equals(ContractCode.C418.getValue())
				 || code.equals(ContractCode.C508.getValue())
				 || code.equals(ContractCode.C518.getValue()) ){
			prorrogaFile = true;
		} else {
			contratoFile = true;
		}
		setFileName(getFormatedDate(new Date()));
		
		CONTRATOS contratos = null;
		TRANSFORMACIONES transformaciones = null;
		PRORROGAS prorrogas = null;
		String modelPath = null;
		ContractContrataFactory factory = new ContractContrataFactory();
		if( contratoFile ){
			ContrataContratosWriter writer = new ContrataContratosWriter();
			IContratoType contratoType = writer.createFile(factory.createContratoModel(code), params);
			contratos = contratoFactory.createCONTRATOS();
			contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contratoType);
			modelPath = CONTRATA_CONTRATOS_MODEL_PATH;
		} else if( transfonacionFile ) {
			ContrataTransformacionesWriter writer = new ContrataTransformacionesWriter(); 
			ITransformacionType transformacionType = writer.createFile(factory.createTransformacionesType(code), params);
			transformaciones = transformacionFactory.createTRANSFORMACIONES();
			transformaciones.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().add(transformacionType);
			modelPath = CONTRATA_TRANSFORMACIONES_MODEL_PATH;
		} else if( prorrogaFile ) {
			ContrataProrrogasWriter writer = new ContrataProrrogasWriter(); 
			IProrrogaType prorrogaType = writer.createFile(factory.createProrrogasType(code), params);
			prorrogas = prorrogaFactory.createPRORROGAS();
			prorrogas.getPRORROGATIPO().add((PRORROGATIPOTYPE) prorrogaType);
			modelPath = CONTRATA_PRORROGAS_MODEL_PATH;
		}
		
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(modelPath);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
			File file = File.createTempFile("aon-temp", ".XML"); 
			if( contratoFile ){
				marshaller.marshal( contratos, file );
				validateXmlPattern(file, CONTRATOS_SCHEMA_FILE_NAME);
			} else if( transfonacionFile ) {
				marshaller.marshal( transformaciones, file );
				validateXmlPattern(file, TRANSFORMACIONES_SCHEMA_FILE_NAME);
			} else if( prorrogaFile ) {
				marshaller.marshal( prorrogas, file );
				validateXmlPattern(file, PRORROGAS_SCHEMA_FILE_NAME);
			}
			return file;
		} catch (JAXBException e) {
			String msg = "Error al generar el documento xml de contrata";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void validateXmlPattern(File xml, String SCHEMA) {
		File schemaFile = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/schema", SCHEMA);
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFile = getSchemaFile(SCHEMA, urls[0]);
			if( schemaFile!=null ){
				Schema schema = sf.newSchema(schemaFile);
				StreamSource source = new StreamSource(xml);
				Validator validator = schema.newValidator();
				validator.validate(source);
			} else {
				AonUtil.addErrorMessage("Imporsible obtener el esquema (XSD)");
				AonUtil.addErrorMessage("No se ha podido validar el fichero.");
			}
		} catch (Exception e) {
			String msg = "Error de formato al validar y generar el XML";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			schemaFile.delete();
		}
	}
	
	
	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap(Contract contract) {
		if(contractDataMap==null){
			PayrollUtils utils = new PayrollUtils();
			contractDataMap = utils.getContractDataMap(contract);
		}
		return contractDataMap;
	}
	protected Map<String, String> getContractDataMap() {
		return contractDataMap;
	}
	
	private String getFormatedDate(Date date){
		String pattern = "yyyyMMdd";
		if(date!=null){
			return DateFormatUtils.format(date, pattern);
		}
		return null;
	}
	
	private File getSchemaFile(String schema, URL url) {
		if( schema.equals(CONTRATOS_SCHEMA_FILE_NAME) ){
			return obtainContratosSchemaFile(url);
		} else if( schema.equals(TRANSFORMACIONES_SCHEMA_FILE_NAME) ){
			return obtainTransformacionesSchemaFile(url);
		} else if( schema.equals(PRORROGAS_SCHEMA_FILE_NAME) ){
			return null;	
		}
		return null;	
	}
	
	private File obtainContratosSchemaFile(URL url) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		map.put("CONTRATO_100", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C100.getValue()));
		map.put("CONTRATO_130", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C130.getValue()));
		map.put("CONTRATO_150", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C150.getValue()));
		map.put("CONTRATO_200", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C200.getValue()));
		map.put("CONTRATO_230", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C230.getValue()));
		map.put("CONTRATO_250", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C250.getValue()));
		map.put("CONTRATO_300", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C300.getValue()));
		map.put("CONTRATO_330", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C330.getValue()));
		map.put("CONTRATO_350", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C350.getValue()));
		map.put("CONTRATO_401", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C401.getValue()));
		map.put("CONTRATO_402", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C402.getValue()));
		map.put("CONTRATO_403", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C403.getValue()));
		map.put("CONTRATO_410", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C410.getValue()));
		map.put("CONTRATO_420", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C420.getValue()));
		map.put("CONTRATO_421", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C421.getValue()));
		map.put("CONTRATO_430", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C430.getValue()));
		map.put("CONTRATO_441", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C441.getValue()));
		map.put("CONTRATO_450", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C450.getValue()));
		map.put("CONTRATO_452", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C452.getValue()));
		map.put("CONTRATO_501", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C501.getValue()));
		map.put("CONTRATO_502", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C502.getValue()));
		map.put("CONTRATO_503", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C503.getValue()));
		map.put("CONTRATO_510", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C510.getValue()));
		map.put("CONTRATO_520", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C520.getValue()));
		map.put("CONTRATO_530", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C530.getValue()));
		map.put("CONTRATO_540", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C540.getValue()));
		map.put("CONTRATO_541", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C541.getValue()));
		map.put("CONTRATO_550", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C550.getValue()));
		map.put("CONTRATO_552", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C552.getValue()));
		map.put("CONTRATO_970", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C970.getValue()));
		map.put("CONTRATO_980", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C980.getValue()));
		map.put("CONTRATO_990", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C990.getValue()));
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
	
	private File obtainTransformacionesSchemaFile(URL url) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
				 
		map.put("T_109", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C109.getValue()));
		map.put("T_139", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C139.getValue()));
		map.put("T_189", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C189.getValue()));
		map.put("T_209", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C209.getValue()));
		map.put("T_239", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C239.getValue()));
		map.put("T_289", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C289.getValue()));
		map.put("T_309", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C309.getValue()));
		map.put("T_339", false);
		map.put("T_389", getContractDataMap().get(ContextVariable.TC2.getName()).equals(ContractCode.C389.getValue()));
		
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

