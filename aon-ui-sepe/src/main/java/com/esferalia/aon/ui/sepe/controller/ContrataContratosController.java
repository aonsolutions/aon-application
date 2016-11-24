package com.esferalia.aon.ui.sepe.controller;

import java.io.IOException;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractSepeStatus;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.ui.sepe.controller.handler.ContrataContratosHandler;
import com.esferalia.aon.ui.sepe.utils.SEPEFileUtils;


public class ContrataContratosController extends ContrataAbstractController implements IContrataController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataContratosController.class.getName());

	@Override
	public ContrataFileType getContrataFileType() {
		return ContrataFileType.CONTRACT;
	}

	@Override
	public String getContrataModelName() {
		return "contract";
	}

	@Override
	protected String getSchemaFileName() {
		return SEPEFileUtils.CONTRATOS_SCHEMA_FILE_NAME;
	}

	@Override
	protected ContractAttachmentType getAttachmentType() {
		return ContractAttachmentType.SEPE_CONTRACT_FILE;
	}

	@Override
	protected void initContract() {
		List<IAttachment> list = obtainContrataList(ContractAttachmentType.SEPE_CONTRACT_FILE);
		if(list==null || list.isEmpty()){
			onContrataAccept(null);
			list = obtainContrataList(ContractAttachmentType.SEPE_CONTRACT_FILE);
		}
		setGeneratedFile(list.get(0));
	}

	@Override
	protected void initHandler() {
		setHandler( new ContrataContratosHandler() );
	}
	
	@Override
	protected void beforeContrataAccept() throws ManagerBeanException {
		if( isNevv() ){
			IManagerBean bean;
			try {
				bean = BeanManager.getManagerBean(ContractInfo.class);
			} catch (ManagerBeanException e) {
				String msg = "Imposible grabar los datos de contrato. (" +e.getMessage() + ")";
				throw new AbortProcessingException(msg,e);
			}
			ContractInfo info;
			info = new ContractInfo();
			info.setContract( getContract() );
			info.setStartDate( getContract().getStartDate() );
			info.setEndDate( getContract().getEndDate() );
			info.setName( ContractVariable.SEPE_CONTRACT.getValue() );
			info.setExpression( ContractSepeStatus.PENDING.getValue() );
			bean.insert(info);
		}
	}
	
	@Override
	protected void processSepeResult(String result) {
		
		final String ACCEPTED 				= "ACEPTADO";
		final String ACCEPTED_WITH_ERRORS 	= "ACEPTADO CON ERRORES";
		final String REJECTED 				= "RECHAZADO";
		final String WRONG_CONTRACT_ID 		= "E0000000000000";
		
		if(result!=null && (result.contains(ACCEPTED) || result.contains(ACCEPTED_WITH_ERRORS)) && !result.contains(REJECTED)){
			try {
				getCommunicator().setContrataFileType(getContrataFileType());
				FICHEROCONTRATOS contratos = getCommunicator().obtainFicheroContratos(result.getBytes());
				for(Object o: contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150()){
					RESPUESTACONTRATOTYPE respuestaContratos = getCommunicator().obtainRespuestaContrato(o);
					try {
						if(!respuestaContratos.getIDCONTRATO().equals(WRONG_CONTRACT_ID)){
							Contract contract = isBatchView()?obtainContract(respuestaContratos):getContract();
							IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
							ContractInfo info = new ContractInfo();
							info.setContract(contract);
							info.setStartDate(contract.getStartDate());
							info.setEndDate(contract.getEndDate());
							info.setName( ContractVariable.SEPE_CONTRACT_ID.getValue() );
							info.setExpression("\"" + respuestaContratos.getIDCONTRATO() + "\"");
							bean.insert(info);
						}
					} catch (ManagerBeanException e) {
						String msg = "Error al grabar el ID de contrato obtenido del SEPE. (" +e.getMessage() + ")";
						AonUtil.addErrorMessage(msg);
					}
				}
			} catch (IOException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (JAXBException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (SAXException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			} catch (ParserConfigurationException e) {
				String msg = "No se han podido procesar los datos de respuesta de Contrat@";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}
	
	private Contract obtainContract(RESPUESTACONTRATOTYPE respuestaContratos) {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}
