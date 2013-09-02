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
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;
import com.esferalia.aon.ui.sepe.file.ContrataResponseReader;


public class CertificadosCommunicator implements ISepeCommunicator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificadosCommunicator.class.getName());
	
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
		searchCertificadosLogin();
		searchTestEnvironment();
	}
	
	public boolean isLoginRequired() {
		return StringUtils.isBlank(user) || StringUtils.isBlank(passwd);
	}
	
	@Override
	public String communicate() {
		if(dataCommunication){
			return sendCertificadosFile();
		} else if(dataQuery){
			return certificadosDataQuery();
		}
		return null;
	}
	
	@Override
	public String obtainCommunicationNumber(byte[] data) {
		String id = new String(data); 
		if( StringUtils.isNotBlank(id) ){
			id = id.replaceAll("\n", "");
			id = StringUtils.removeStart(id, "<?xml version='1.0' encoding='ISO-8859-1'?>");
			id = StringUtils.removeStart(id, "<COMUNICACION>");
			id = StringUtils.removeEnd(id, "</COMUNICACION>");
			if(id.contains("<NUM_ENVIO>")){
				id = StringUtils.removeStart(id, "<NUM_ENVIO>");
				id = StringUtils.removeEnd(id, "</NUM_ENVIO>");
			} else {
				id = StringUtils.removeStart(id, "<ERROR>");
				id = StringUtils.removeEnd(id, "</ERROR>");
			}
			return id;
		}
		return null;
	}
	
	public String obtainCommunicationStatus(byte[] data) {
		String status = "";
		status += "<br /> ";
//		status += attach.getAttachDate() + " - Resultado obtenido del SEPE";
		status += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Resultado obtenido del SEPE</b></div>";
//		status += "<hr /> ";
		if(data!=null){
			String errorMsg = new String(data);
			if(errorMsg.contains("<COMUNICACION>") && errorMsg.contains("<ERROR>")){
				errorMsg = StringUtils.removeStart(errorMsg, "<?xml version='1.0' encoding='ISO-8859-1'?>");
				errorMsg = StringUtils.removeStart(errorMsg, "<COMUNICACION>");
				errorMsg = StringUtils.removeEnd(errorMsg, "</COMUNICACION>");
				errorMsg = StringUtils.removeStart(errorMsg, "<ERROR>");
				errorMsg = StringUtils.removeEnd(errorMsg, "</ERROR>");
				return status + errorMsg;
			}
//			try {
//				FICHEROCONTRATOS contratos = obtainFicheroContratos(data);
//				status += "ESTADO FICHERO:    " + contratos.getESTADOFICHERO();
//				status += "<br /> ";
//
//				for(Object o: contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150()){
//					
//					RESPUESTACONTRATOTYPE respuestaContratos = obtainRespuestaContrato(o);
//					
//					status += "FECHA ALTA:         " + respuestaContratos.getFECHAALTA();
//					status += "<br /> ";
//					status += "FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION();
//					status += "<br /> ";
//					status += "ID CONTRATO:        " + respuestaContratos.getIDCONTRATO();
//					status += "<br /> ";
//					status += "LEY BONIF:          " + respuestaContratos.getLEYBONIF();
//					status += "<br /> ";
//					status += "LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION();
//					status += "<br /> ";
//					status += "LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO();
//					status += "<br /> ";
//					status += "LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION();
//					status += "<br /> ";
//					status += "OBLIG B:            " + respuestaContratos.getOBLIGCB();
//					status += "<br /> ";
//					status += "RESULTADO:          " + respuestaContratos.getRESULTADO();
//					status += "<br /> ";
//					status += "USUARIO:            " + respuestaContratos.getUSUARIO();
//					status += "<br /> ";
//					
////			System.out.println("ESTADO FICHERO:    " + contratos.getESTADOFICHERO());
////			System.out.println("NUMERO PROCESADOS: " + contratos.getNUMEROPROCESADOS());
////			System.out.println("VERSION:           " + contratos.getVersion());
////			System.out.println("FECHA ALTA:         " + respuestaContratos.getFECHAALTA());
////			System.out.println("FECHA COMUNICACION: " + respuestaContratos.getFECHACOMUNICACION());
////			System.out.println("ID CONTRATO:        " + respuestaContratos.getIDCONTRATO());
////			System.out.println("LEY BONIF:          " + respuestaContratos.getLEYBONIF());
////			System.out.println("LEY DEDUCCION:      " + respuestaContratos.getLEYDEDUCCION());
////			System.out.println("LEY FOMENTO:        " + respuestaContratos.getLEYFOMENTO());
////			System.out.println("LEY REDUCCION:      " + respuestaContratos.getLEYREDUCCION());
////			System.out.println("OBLIG B:            " + respuestaContratos.getOBLIGCB());
////			System.out.println("RESULTADO:          " + respuestaContratos.getRESULTADO());
////			System.out.println("USUARIO:            " + respuestaContratos.getUSUARIO());
//					
//					List<String> errores = respuestaContratos.getERRORES().getERROR();
//					if(!errores.isEmpty()){
//						status += "<br />";
//						status += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>ERRORES</b></div>";
//						for(String error: respuestaContratos.getERRORES().getERROR()){
//							status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
//							status += "<br /> ";
////					System.out.println("ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription());
//						}
//					}
//				}
//			} catch (IOException e) {
//				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
//				status += msg;
//				status += "<br /> ";
//			} catch (Throwable th) {
//				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
//				status += msg;
//				status += "<br /> ";
//			}
		}
		return status;
	}
	
	private void searchCertificadosLogin() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
		} catch (ManagerBeanException e) {
			// no se cargan los datos de login, se piden por pantalla
		}
		user = appParams.getCertifica2User();
		passwd = appParams.getCertifica2Password();
	}

	private void searchTestEnvironment() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido verificar el entorno de trabajo. Se activa el entorno de pruebas (TEST).";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			testEnv = true;
		}
		testEnv = appParams.getCertifica2TestEnviroment();
	}
	
	private String sendCertificadosFile(){
		xmlResult = SEPEConnectionProvider.processCertificadosCommunication(testEnv, document, user, user, passwd);
		System.out.println("RESPUESTA RESULTANTE DE LA COMUNICACION CON EL S.E.P.E. :  " );
		System.out.println(xmlResult);
		afterCommunication();
		return xmlResult;
	}

	private String certificadosDataQuery(){
		xmlResult = SEPEConnectionProvider.processCertificadosQuery(testEnv, document, user, user, passwd);
		System.out.println("RESPUESTA RESULTANTE DE LA CONSULTA AL S.E.P.E. :  " );
		System.out.println(xmlResult);
		afterCommunication();
		return xmlResult;
	}
	
	private void afterCommunication() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		if(StringUtils.isNotBlank(getUser()) && StringUtils.isNotBlank(getPasswd())){
			if(isLoginRemember()){
				appParams.setCertifica2User(getUser());
				appParams.setCertifica2Password(getPasswd());
				try {
					appParams.accept();
				} catch (ManagerBeanException e) {
					AonUtil.addInfoMessage("Los datos identificativos no se han podido guardar.");
				}
			}
		} 
	}

	
}
