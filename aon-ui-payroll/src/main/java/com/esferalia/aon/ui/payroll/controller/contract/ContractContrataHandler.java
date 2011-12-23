package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;
import com.esferalia.aon.ui.payroll.file.ContractXmlReader;
import com.esferalia.aon.ui.payroll.file.ContractXmlWriter;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATOS;
import com.esferalia.aon.ui.payroll.utils.contractMojo.ObjectFactory;

public class ContractContrataHandler {

	private static final long serialVersionUID = 3733409240562499848L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractContrataHandler.class.getName());
//	private static final String IMAGE_URL_PREFIX0 = ".contractImage";
//	private static final String IMAGE_URL_PREFIX1 = "?model=";
//	private static final String IMAGE_URL_PREFIX2 = "&width=";
//	private static final String IMAGE_URL_PREFIX3 = "&height=";
//	private static final int MAX_FILE_SIZE_MB = 3;
//	private static final int MAX_FILE_SIZE = MAX_FILE_SIZE_MB*1024*1024;
	private static final String CONTRACT_XML_CONTEXT_PATH = "com.esferalia.aon.ui.payroll.utils.contractMojo";
	
//	private String imageUrl;
//
//	private ContractBuilder contractBuilder;

	private ContractXmlWriter xmlWriter;
	
	private ContractContrataFactory factory;
	
	private ContractController contractController;
	
	private CONTRATOS contratos;
	
	private ContractAttachment contrataAttach;
	
	public ContractContrataHandler(ContractController controller) {
		contractController = controller;
		factory = new ContractContrataFactory();
		factory.setParams(new ContrataParams());
		factory.setContractCode(ContractCode.getContractCodeByValue(getContractDataMap().get(ContextVariable.TC2.getName())));
		factory.setContract((Contract)contractController.getTo());
	}
	
	public void setContrataAttach(ContractAttachment contrataAttach) {
		this.contrataAttach = contrataAttach;
	}
	public ContractAttachment getContrataAttach(){
		return contrataAttach;
	}
	
	public CONTRATOS getContratos() {
		return contratos;
	}
	public void setContratos(CONTRATOS contratos) {
		this.contratos = contratos;
	}
	
	public ContractXmlWriter getXmlWriter() {
		return xmlWriter;
	}
	public void setXmlWriter(ContractXmlWriter xmlWriter) {
		this.xmlWriter = xmlWriter;
	}
	
	public ContractContrataFactory getFactory() {
		return factory;
	}
	public void setFactory(ContractContrataFactory factory) {
		this.factory = factory;
	}

	public ContrataParams getParams() {
		return getFactory().getParams();
	}

//	public String getImageUrl() {
//		StringBuilder builder = new StringBuilder(getContractBuilder().getContractPage().toString());
//		builder.append(IMAGE_URL_PREFIX0);
//		builder.append(IMAGE_URL_PREFIX1);
////		builder.append(getContractModel());
//		builder.append(contractController.getParams().getContractType().getModel());
//		builder.append(IMAGE_URL_PREFIX2);
//		builder.append(getContractBuilder().getContractWidth());
//		builder.append(IMAGE_URL_PREFIX3);
//		builder.append(getContractBuilder().getContractHeight());
//		imageUrl = builder.toString();
//		return imageUrl;
//	}
//	public void setImageUrl(String imageUrl) {
//		this.imageUrl = imageUrl;
//	}
//	public ContractBuilder getContractBuilder() {
//		if(contractBuilder==null){
//			contractBuilder = ContractBuilder.getInstance();
//		}
//		return contractBuilder;
//	}
//	public void setContractBuilder(ContractBuilder contractBuilder) {
//		this.contractBuilder = contractBuilder;
//	}
	
	
	public void readXml() throws JAXBException, IOException {
		searchContrataAttach();
		if(getContrataAttach()!=null){
			byte[] f = getContrataAttach().getData();
			if(f!=null && f.length>0){
				File file = File.createTempFile("aon-temp", ".XML");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(f);
				fos.close();
				JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				setContratos((CONTRATOS) unmarshaller.unmarshal(file));
				ContractXmlReader reader = new ContractXmlReader();
				reader.completeContrataParams(getContratos(), getFactory().getParams());
				unmarshaller.setEventHandler(new ContractValidationEventHandler());
			}
		}
	}
	
	private void searchContrataAttach(){
		setContrataAttach(null);
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_CONTRACT_ID), ((Contract)contractController.getTo()).getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_ATTACHMENT_ATTACHMENT_TYPE), ContractAttachmentType.SPEE_CONTRATA);
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				setContrataAttach((ContractAttachment) list.get(0));
			}
		} catch (ManagerBeanException e) {
			// NADA
		}
	}
	
	public void generateXml() throws JAXBException, ManagerBeanException, IOException{
		JAXBContext jaxbContext = JAXBContext.newInstance(CONTRACT_XML_CONTEXT_PATH);
		
		ObjectFactory factory = new ObjectFactory();
		setContratos(factory.createCONTRATOS());
		setXmlWriter(new ContractXmlWriter());
		getXmlWriter().setContract((Contract) contractController.getTo());
		getXmlWriter().setParams(getFactory().getParams());
		
		getContratos().getCONTRATO100AndCONTRATO130AndCONTRATO150().add(getXmlWriter().execute());
		
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		File file = File.createTempFile("aon-temp", ".XML"); 
		marshaller.marshal( getContratos(), file );
		
		validateXmlPattern(file);
		
		FileInputStream fin = new FileInputStream(file);
		byte fileContent[] = new byte[(int)file.length()];
		fin.read(fileContent);
		if(getContrataAttach()==null){
			setContrataAttach(new ContractAttachment());
		}
		getContrataAttach().setContract((Contract) contractController.getTo());
		getContrataAttach().setData(fileContent);
		getContrataAttach().setAttachmentType(ContractAttachmentType.SPEE_CONTRATA);
		getContrataAttach().setMimeType(MimeType.MIME_XML);
		getContrataAttach().setDescription("fichero_contrata");
		fin.close();
	}
	
	private void validateXmlPattern(File xml) {
		final String SCHEMA = "EsquemaContratos50.xsd";
		
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, "META-INF/", SCHEMA);
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(getSchemaFile(urls[0]));
			Validator validator = schema.newValidator();
			StreamSource source = new StreamSource(xml);
			validator.validate(source);
		} catch (Exception e) {
			String msg = "Error de formato al generar el XML";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private File getSchemaFile(URL url) {
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

			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

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
			String msg = "Error al obtener el qsquema de validacion";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		} catch (IOException e) {
			String msg = "Error al obtener el qsquema de validacion";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}

	public class ContractValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap() {
		if(contractDataMap==null || contractDataMap.isEmpty()){
			contractDataMap = new HashMap<String, String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), ((Contract)contractController.getTo()).getId());
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
				for(ITransferObject to: bean.getList(criteria)){
					ContractData data = (ContractData) to;
					contractDataMap.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			} catch (ManagerBeanException e) {
				// NADA, se devuelve un mapa vacio
			}
		}
		return contractDataMap;
	}
	
	public List<SelectItem> getTownNames(){
		String BASE_NAME = "com.esferalia.aon.payroll.i18n.towns";
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		Contract contract = (Contract)contractController.getTo();
		List<SelectItem> towns = new LinkedList<SelectItem>();
		try {
			String geozone = contract.getPerson().getRegistry().getDefaultAddress().getGeozone().getCode();
			TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
			for(String key: tree){
				if(key.startsWith(geozone)){
					String name = bundle.getString(key);
					SelectItem item = new SelectItem(key, name);
					towns.add(item);
				}
			}
			return towns;
		} catch (ManagerBeanException e) {
			// NADA, se devuelve una lista vacia
		}
		return null;
	}
	
}
