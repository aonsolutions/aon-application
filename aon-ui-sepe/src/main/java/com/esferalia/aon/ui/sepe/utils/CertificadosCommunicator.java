package com.esferalia.aon.ui.sepe.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.certificados.enumeration.Terrores;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RESPUESTACERTIFICADOEMPRESATYPE.CuentaCotizacion;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RESPUESTACERTIFICADOEMPRESATYPE.CuentaCotizacion.DatosTrabajador;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa.Resultado;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;
import com.esferalia.aon.ui.sepe.file.CertificadosResponseReader;


public class CertificadosCommunicator implements ISepeCommunicator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CertificadosCommunicator.class.getName());
	
	private String document;
	private String user;
	private String mainUser;
	private String passwd;
	private boolean loginRemember;
	private boolean testEnv;
	private boolean sslEnv;
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
	
	public boolean isTestEnv() {
		return testEnv;
	}

	public void setTestEnv(boolean testEnv) {
		this.testEnv = testEnv;
	}

	public boolean isSslEnv() {
		return sslEnv;
	}

	public void setSslEnv(boolean sslEnv) {
		this.sslEnv = sslEnv;
	}

	@Override
	public void initialize() {
		searchCertificadosLogin();
		searchEnvironmentParams();
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
	public boolean isCommunicationAccepted(byte[] data) {
		if( data!=null ){
			String value = new String(data); 
			value = value.replaceAll("\n", "");
			value = StringUtils.removeStart(value, "<?xml version='1.0' encoding='ISO-8859-1'?>");
			value = StringUtils.removeStart(value, "<COMUNICACION>");
			value = StringUtils.removeEnd(value, "</COMUNICACION>");
			if(value.contains("<NUM_ENVIO>") && !value.contains("<COD_ERROR>")){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isCommunicationFinished(byte[] data) {
		if(data!=null){
			String errorMsg = new String(data);
//			fichero no procesado: si se obtiene algun error (comunicacion, fichero no procesado, ...)
			if(errorMsg.contains("<COMUNICACION>") && errorMsg.contains("<ERROR>")){
				return false;
			}
//			fichero si procesado: si se obtiene el fichero con los datos procesados
			try {
				RespuestaCertificadoEmpresa certificado = obtainFicheroCertificado(data);
				for(CuentaCotizacion cuentaCotizacion: certificado.getResultado().getCuentaCotizacion()){
					if(StringUtils.equals(cuentaCotizacion.getDescripcionResultado(),"PROCESADO")){
						return true;
					} else if(StringUtils.equals(cuentaCotizacion.getDescripcionResultado(),"PROCESADO PARCIALMENTE")){
						return true;
					}
				}
			} catch (IOException e) {
			} catch (Throwable th) {
			}
		}
		return false;
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
			if(value.contains("NUM_ENVIO") && !value.contains("ERROR")){
				status += "NUM ENVIO:         " + value;
				value = StringUtils.substringBetween(value, "<NUM_ENVIO>", "</NUM_ENVIO>");
			} else {
				status += "ERROR:             " + value;
				value = StringUtils.substringBetween(value, "<COD_ERROR>", "</COD_ERROR>");
			}
			status += "<br /> ";
			return status;
		}
		return null;
	}
	
	@Override
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
				RespuestaCertificadoEmpresa certificado = obtainFicheroCertificado(data);
			
				for(CuentaCotizacion cuentaCotizacion: certificado.getResultado().getCuentaCotizacion()){
					
					String bgColor = null;
					
					if(StringUtils.equals(cuentaCotizacion.getDescripcionResultado(),"PROCESADO")){
						bgColor = "#E0F8E0";
					} else if(StringUtils.equals(cuentaCotizacion.getDescripcionResultado(),"PROCESADO PARCIALMENTE")){
						bgColor = "#F6E3CE";
					} else if(StringUtils.equals(cuentaCotizacion.getDescripcionResultado(),"RECHAZADO")){
						bgColor = "#F8E0E0";
					} else {
						bgColor = "#E4E4E4";
					}
					
					status += "<br /> ";
					status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'><b>";
					String ccc = ((Element) cuentaCotizacion.getCCC()).getFirstChild().getNodeValue();
					status += "CCC:                 " + ccc;
					status += "</b></div>";
					
					status += "RESULTADO:           " + cuentaCotizacion.getDescripcionResultado();
					status += "<br /> ";
					status += "TRAB: PROCESADOS:    " + cuentaCotizacion.getNumTrabajadoresProcesados();
					status += "<br /> ";
					status += "TOTAL TRABAJADORES:  " + cuentaCotizacion.getNumTrabajadoresTotal();
					status += "<br /> ";
					
//					System.out.println("CCC:                 " + cuentaCotizacion.getCCC());
//					System.out.println("RESULTADO:           " + cuentaCotizacion.getDescripcionResultado());
//					System.out.println("TRAB: PROCESADOS:    " + cuentaCotizacion.getNumTrabajadoresProcesados());
//					System.out.println("TOTAL TRABAJADORES:  " + cuentaCotizacion.getNumTrabajadoresTotal());
//					System.out.println("ERRORES GENERALES:   ");

					status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES GENERALES</b></div>";
					for(String error: cuentaCotizacion.getErroresGeneral().getError()){
						status += "ERROR: " + error + " - " + Terrores.getEnumByValue(error).getDescription();
//						System.out.println("ERROR: " + error + " - " + Terrores.getEnumByValue(error).getDescription());
						status += "<br /> ";
					}
					for(DatosTrabajador trabajador: cuentaCotizacion.getDatosTrabajador()){
//						System.out.println("TRABAJADOR:         " + trabajador.getDNINIE());
						status += "<br /> ";
						status += "<div style='background-color:#E4E4E4; width:100%; padding:1px;'><b>"+"TRABAJADOR: "+trabajador.getDNINIE()+"</b></div>";
						status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b></div>";
						for(Object e: trabajador.getError()){
							String error = ((Element) e).getFirstChild().getNodeValue();
							status += "ERROR: " + error + " - " + Terrores.getEnumByValue(error).getDescription();
//							System.out.println("ERROR:    " +  error + " - " + Terrores.getEnumByValue(error).getDescription());
							status += "<br /> ";
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
	
	public RespuestaCertificadoEmpresa obtainFicheroCertificado(byte[] data) throws IOException, JAXBException, SAXException, ParserConfigurationException {
		CertificadosResponseReader reader = new CertificadosResponseReader();
		reader.readFile(new ByteArrayInputStream(data));
		return reader.getFicheroCertificado();
	}

	public Resultado obtainRespuestaContrato(Object object) {
		CertificadosResponseReader reader = new CertificadosResponseReader();
		return reader.getRepuestaCertificado(object);
	}
	
	private void searchCertificadosLogin() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
			user = appParams.getCertifica2User();
			passwd = appParams.getCertifica2Password();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener los datos identificativos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void searchEnvironmentParams() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
			testEnv = appParams.getCertifica2TestEnviroment();
			sslEnv = appParams.getCertifica2SSLEnviroment();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido verificar el entorno de trabajo. Se activa el entorno de pruebas (TEST) sin seguridad (no SSL).";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			testEnv = true;
		}
	}
	
	private String sendCertificadosFile(){
		xmlResult = SEPEConnectionProvider.processCertificadosCommunication(sslEnv, testEnv, document, user, user, passwd);
		afterCommunication();
		return xmlResult;
	}

	private String certificadosDataQuery(){
		xmlResult = SEPEConnectionProvider.processCertificadosQuery(sslEnv, testEnv, document, user, user, passwd);
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
