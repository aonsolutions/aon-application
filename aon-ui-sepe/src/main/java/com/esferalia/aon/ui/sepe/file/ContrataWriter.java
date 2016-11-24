package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataFactory;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGAS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGATIPOTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ContrataWriter implements IContrataWriter{

	private Contract contract;
	private String fileName;
	private ContrataFileType fileType;
	
	public ContrataWriter(Contract contract, ContrataFileType fileType) {
		this.contract = contract;
		this.fileType = fileType;
	}
	
	public String getFileName() {
		return fileName; 
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Contract getContract(){
		return contract;
	}

	@Override
	public File createFile(IContrataParams params) throws ManagerBeanException, IOException{
		if(contract==null){
			String msg = "El contrato no se ha cargado correctamente.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		if(fileType==null){
			String msg = "El tipo de documento no se ha cargado correctamente.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		SEPEUtils utils = SEPEUtils.getInstance();
		
		List<ITransferObject> list = utils.getContractData(contract, null, null, ContextVariable.TC2.getName(), false);
		list = list.stream().map(to -> ((ContractData)to))
			.sorted((cd1, cd2) -> cd1.getStartDate().compareTo(cd2.getStartDate()))
			.collect(Collectors.toList());
		
		String code = ((ContractData)list.get(0)).getExpression().replaceAll("\"", "");
		String codeTransform = list.size()>1?((ContractData)list.get(list.size()-1)).getExpression().replaceAll("\"", ""):"";

		if( StringUtils.isBlank(code) ) {
			String msg = "Contrato no reconocido";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 

		setFileName(getFormatedDate(new Date()));
		
		CONTRATOS contratos = null;
		TRANSFORMACIONES transformaciones = null;
		PRORROGAS prorrogas = null;
		String modelPath = null;
		ContrataFactory factory = new ContrataFactory();
		if( fileType == ContrataFileType.CONTRACT ){
			ContrataContratosWriter writer = new ContrataContratosWriter(contract);
			IContratoType contratoType = writer.createFile(factory.createContratoModel(code), (ContrataContratoParams) params);
			contratos = writer.getFactory().createCONTRATOS();
			contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contratoType);
			modelPath = writer.CONTRATA_CONTRATOS_MODEL_PATH;
		} else if( fileType == ContrataFileType.TRANSFORMATION ){
			ContrataTransformacionesWriter writer = new ContrataTransformacionesWriter(contract); 
			ITransformacionType transformacionType = writer.createFile(factory.createTransformacionesType(codeTransform), (ContrataTransformacionesParams) params);
			transformaciones = writer.getFactory().createTRANSFORMACIONES();
			transformaciones.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().add(transformacionType);
			modelPath = writer.CONTRATA_TRANSFORMACIONES_MODEL_PATH;
		} else if( fileType == ContrataFileType.EXTENSION ){
			ContrataProrrogasWriter writer = new ContrataProrrogasWriter(contract); 
			PRORROGATIPOTYPE prorrogaType = writer.createProrroga(factory.createProrrogasType(code), (ContrataProrrogaParams) params);
			prorrogas = writer.getFactory().createPRORROGAS();
			prorrogas.getPRORROGATIPO().add(prorrogaType);
			modelPath = writer.CONTRATA_PRORROGAS_MODEL_PATH;
		} else {
			String msg = "No se pueden procesar los datos, no se ha especificado el tipo de documento" ;
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);	
		}
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(modelPath);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, SEPEFileUtils.XML_FILE_ENCODING);
			File file = File.createTempFile("aon-temp", ".XML"); 
			if( fileType == ContrataFileType.CONTRACT ){
				marshaller.marshal( contratos, file );
			} else if( fileType == ContrataFileType.TRANSFORMATION ){
				marshaller.marshal( transformaciones, file );
			} else if( fileType == ContrataFileType.EXTENSION ){
				marshaller.marshal( prorrogas, file );
			}
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
	
	private String getFormatedDate(Date date){
		String pattern = "yyyyMMdd";
		if(date!=null){
			return DateFormatUtils.format(date, pattern);
		}
		return null;
	}
	
	
}

