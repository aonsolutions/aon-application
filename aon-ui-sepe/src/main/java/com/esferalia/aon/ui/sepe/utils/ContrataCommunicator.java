package com.esferalia.aon.ui.sepe.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.contrata.enumeration.TERRORES;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;
import com.esferalia.aon.ui.sepe.file.ContrataResponseReader;


public class ContrataCommunicator implements ISepeCommunicator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataCommunicator.class.getName());
	
	private String document;
	private String user;
	private String mainUser;
	private String passwd;
	private boolean loginRemember;
	private boolean testEnv;
	private boolean passwdVisible;
	
	private String xmlResult;
	
	private boolean dataCommunication;
	private boolean dataQuery;
	
	
	public boolean isDataCommunication() {
		return dataCommunication;
	}

	@Override
	public void setDataCommunication(boolean dataCommunication) {
		this.dataCommunication = dataCommunication;
		this.dataQuery = !dataCommunication;
	}

	public boolean isDataQuery() {
		return dataQuery;
	}

	@Override
	public void setDataQuery(boolean dataQuery) {
		this.dataQuery = dataQuery;
		this.dataCommunication = !dataQuery;
	}

	public String getDocument() {
		return document;
	}

	@Override
	public void setDocument(String document) {
		this.document = document;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMainUser() {
		return mainUser;
	}

	public void setMainUser(String mainUser) {
		this.mainUser = mainUser;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean isTestEnv() {
		return testEnv;
	}

	public void setTestEnv(boolean testEnv) {
		this.testEnv = testEnv;
	}
	
	public boolean isLoginRemember() {
		return loginRemember;
	}
	public void setLoginRemember(boolean loginRemember) {
		this.loginRemember = loginRemember;
	}
	public boolean isPasswdVisible() {
		return passwdVisible;
	}
	public void setPasswdVisible(boolean passwdVisible) {
		this.passwdVisible = passwdVisible;
	}
	
	public String getXmlResult() {
		return xmlResult;
	}
	
	public void setXmlResult(String xmlResult) {
		this.xmlResult = xmlResult;
	}
	
	@Override
	public void initialize() {
		searchContrataLogin();
		searchTestEnvironment();
	}
	
	public boolean isLoginRequired() {
		return StringUtils.isBlank(user) || StringUtils.isBlank(passwd);
	}
	
	@Override
	public String communicate() {
		if(dataCommunication){
			return sendContrataFile();
		} else if(dataQuery){
			return contrataDataQuery();
		}
		return null;
	}
	
	@Override
	public String obtainCommunicationNumber(byte[] data) {
		String status = "";
		status += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Datos comunicados al SEPE</b></div>";
		if( data!=null ){
			String value = new String(data); 
			value = value.replaceAll("\n", "");
			value = StringUtils.removeStart(value, "<?xml version='1.0' encoding='ISO-8859-1'?>");
			value = StringUtils.removeStart(value, "<COMUNICACION>");
			value = StringUtils.removeEnd(value, "</COMUNICACION>");
			if(value.contains("<NUM_ENVIO>") && !value.contains("<ERROR>")){
				status += "NUM ENVIO:         " + value;
				value = StringUtils.substringBetween(value, "<NUM_ENVIO>", "</NUM_ENVIO>");
			} else {
				status += "ERROR:             " + value;
				value = StringUtils.substringBetween(value, "<ERROR>", "</ERROR>");
			}
			status += "<br /> ";
			
			return status;
		}
		return null;
	}
	
	public String obtainCommunicationStatus(byte[] data) {
		String status = "";
		status += "<br /> ";
		status += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Resultado obtenido del SEPE</b></div>";
		if(data!=null){
			String errorMsg = new String(data);
//			fichero no procesado: si se obtiene algun error (comunicacion, fichero no procesado, ...)
			if(errorMsg.contains("<COMUNICACION>") && errorMsg.contains("<ERROR>")){
				errorMsg = StringUtils.removeStart(errorMsg, "<?xml version='1.0' encoding='ISO-8859-1'?>");
				errorMsg = StringUtils.removeStart(errorMsg, "<COMUNICACION>");
				errorMsg = StringUtils.removeEnd(errorMsg, "</COMUNICACION>");
				errorMsg = StringUtils.removeStart(errorMsg, "<ERROR>");
				errorMsg = StringUtils.removeEnd(errorMsg, "</ERROR>");
				return status + errorMsg;
			}
//			fichero si procesado: si se obtiene el fichero con los datos procesados
			try {
				FICHEROCONTRATOS contratos = obtainFicheroContratos(data);
				status += "ESTADO FICHERO:      " + contratos.getESTADOFICHERO();
				status += "<br /> ";
				status += "NUMERO PROCESADOS:    " + contratos.getNUMEROPROCESADOS();
				status += "<br /> ";

				for(Object o: contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150()){
					IContratoType contrato = obtainContratoType(o);
					RESPUESTACONTRATOTYPE respuestaContratos = obtainRespuestaContrato(o);
					
					String bgColor = null;
					
					if(StringUtils.equals(respuestaContratos.getRESULTADO(),"ACEPTADO")){
						bgColor = "#E0F8E0";
					} else if(StringUtils.equals(respuestaContratos.getRESULTADO(),"ACEPTADO CON ERRORES")){
						bgColor = "#F6E3CE";
					} else if(StringUtils.equals(respuestaContratos.getRESULTADO(),"RECHAZADO")){
						bgColor = "#F8E0E0";
					} else {
						bgColor = "#E4E4E4";
					}
					
					status += "<br /> ";
					status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'><b>";
					status += contrato.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(1) + " - ";
					status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO();
					if(StringUtils.isNotBlank(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO())){
						status += " ";
						status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO();
					}
					status += ", ";
					status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE();
					status += "</b></div>";
					
					
					status += "FECHA ALTA:         " + respuestaContratos.getFECHAALTA();
					status += "<br /> ";
					status += "FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION();
					status += "<br /> ";
					status += "ID CONTRATO:        " + respuestaContratos.getIDCONTRATO();
					status += "<br /> ";
					status += "LEY BONIF:          " + respuestaContratos.getLEYBONIF();
					status += "<br /> ";
					status += "LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION();
					status += "<br /> ";
					status += "LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO();
					status += "<br /> ";
					status += "LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION();
					status += "<br /> ";
					status += "OBLIG B:            " + respuestaContratos.getOBLIGCB();
					status += "<br /> ";
					status += "RESULTADO:          " + respuestaContratos.getRESULTADO();
					status += "<br /> ";
					status += "USUARIO:            " + respuestaContratos.getUSUARIO();
					status += "<br /> ";
					
//			System.out.println("ESTADO FICHERO:    " + contratos.getESTADOFICHERO());
//			System.out.println("NUMERO PROCESADOS: " + contratos.getNUMEROPROCESADOS());
//			System.out.println("VERSION:           " + contratos.getVersion());
//			System.out.println("FECHA ALTA:         " + respuestaContratos.getFECHAALTA());
//			System.out.println("FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION());
//			System.out.println("ID CONTRATO:        " + respuestaContratos.getIDCONTRATO());
//			System.out.println("LEY BONIF:          " + respuestaContratos.getLEYBONIF());
//			System.out.println("LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION());
//			System.out.println("LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO());
//			System.out.println("LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION());
//			System.out.println("OBLIG B:            " + respuestaContratos.getOBLIGCB());
//			System.out.println("RESULTADO:          " + respuestaContratos.getRESULTADO());
//			System.out.println("USUARIO:            " + respuestaContratos.getUSUARIO());
					
					List<String> errores = respuestaContratos.getERRORES().getERROR();
					if(!errores.isEmpty()){
						status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b></div>";
						for(String error: respuestaContratos.getERRORES().getERROR()){
							status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
							status += "<br /> ";
//					System.out.println("ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription());
						}
					}
				}
			} catch (IOException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
			} catch (Throwable th) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
			}
		}
		return status;
	}

	public FICHEROCONTRATOS obtainFicheroContratos(byte[] data) throws IOException, JAXBException, SAXException, ParserConfigurationException {
		ContrataResponseReader reader = new ContrataResponseReader();
		reader.readFile(new ByteArrayInputStream(data));
		return reader.getFicheroContratos();
	}

	public RESPUESTACONTRATOTYPE obtainRespuestaContrato(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getRepuestaContrato(object);
	}

	public IContratoType obtainContratoType(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getContratoType(object);
	}
	
	
	private void searchContrataLogin() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
			user = appParams.getContrataUser();
			passwd = appParams.getContrataPassword();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener los datos identificativos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void searchTestEnvironment() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
			testEnv = appParams.getContrataTestEnviroment();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido verificar el entorno de trabajo. Se activa el entorno de pruebas (TEST).";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			testEnv = true;
		}
	}
	
	private String sendContrataFile(){
		xmlResult = SEPEConnectionProvider.processContrataCommunication(testEnv, document, user, user, passwd);
		System.out.println("RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  " );
		System.out.println(xmlResult);
		afterCommunication();
		return xmlResult;
	}

	private String contrataDataQuery(){
		xmlResult = SEPEConnectionProvider.processContrataQuery(testEnv, document, user, user, passwd);
		System.out.println("RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  " );
		System.out.println(xmlResult);
		afterCommunication();
		return xmlResult;
	}
	
	private void afterCommunication() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		if(StringUtils.isNotBlank(getUser()) && StringUtils.isNotBlank(getPasswd())){
			if(isLoginRemember()){
				appParams.setContrataUser(getUser());
				appParams.setContrataPassword(getPasswd());
				try {
					appParams.accept();
				} catch (ManagerBeanException e) {
					AonUtil.addInfoMessage("Los datos identificativos no se han podido guardar.");
				}
			}
		} 
	}
	
}
