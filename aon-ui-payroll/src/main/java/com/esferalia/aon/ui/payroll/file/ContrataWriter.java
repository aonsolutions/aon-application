package com.esferalia.aon.ui.payroll.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contrata.ContractContrataFactory;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.ui.payroll.utils.SEPEFileUtils;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContrataWriter {

	private final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	private final String CONTRATA_TRANSFORMACIONES_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.transformaciones";
//	private final String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.prorrogas";

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
//		boolean prorrogaFile = false;
		
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
//			prorrogaFile = true;
		} else {
			contratoFile = true;
		}
		setFileName(getFormatedDate(new Date()));
		
		CONTRATOS contratos = null;
		TRANSFORMACIONES transformaciones = null;
//		PRORROGAS prorrogas = null;
		String modelPath = null;
		ContractContrataFactory factory = new ContractContrataFactory();
		if( contratoFile ){
			ContrataContratosWriter writer = new ContrataContratosWriter();
			IContratoType contratoType = writer.createFile(factory.createContratoModel(code), params);
			contratos = writer.getFactory().createCONTRATOS();
			contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contratoType);
			modelPath = CONTRATA_CONTRATOS_MODEL_PATH;
		} else if( transfonacionFile ) {
			ContrataTransformacionesWriter writer = new ContrataTransformacionesWriter(); 
			ITransformacionType transformacionType = writer.createFile(factory.createTransformacionesType(code), params);
			transformaciones = writer.getFactory().createTRANSFORMACIONES();
			transformaciones.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().add(transformacionType);
			modelPath = CONTRATA_TRANSFORMACIONES_MODEL_PATH;
		} 
//		else if( prorrogaFile ) {
//			ContrataProrrogasWriter writer = new ContrataProrrogasWriter(); 
//			IProrrogaType prorrogaType = writer.createFile(factory.createProrrogasType(code), params);
//			prorrogas = prorrogaFactory.createPRORROGAS();
//			prorrogas.getPRORROGATIPO().add((PRORROGATIPOTYPE) prorrogaType);
//			modelPath = CONTRATA_PRORROGAS_MODEL_PATH;
//		}
		
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(modelPath);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, SEPEFileUtils.XML_FILE_ENCODING);
			File file = File.createTempFile("aon-temp", ".XML"); 
			if( contratoFile ){
				marshaller.marshal( contratos, file );
				//FileUtils.validateContrataXmlPattern(file, FileUtils.CONTRATOS_SCHEMA_FILE_NAME, getContractDataMap().get(ContextVariable.TC2.getName()));
			} else if( transfonacionFile ) {
				marshaller.marshal( transformaciones, file );
//				FileUtils.validateContrataXmlPattern(file, FileUtils.TRANSFORMACIONES_SCHEMA_FILE_NAME, getContractDataMap().get(ContextVariable.TC2.getName()));
			} 
//			else if( prorrogaFile ) {
//				marshaller.marshal( prorrogas, file );
////			FileUtils.validateContrataXmlPattern(file, FileUtils.PRORROGAS_SCHEMA_FILE_NAME, getContractDataMap().get(ContextVariable.TC2.getName()));
//			}
			return file;
		} catch (JAXBException e) {
			String msg = "Error al generar el documento xml de contrata." ;
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage("[" + e + "]");
			throw new AbortProcessingException(msg, e);
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
	
	
}

