package com.esferalia.aon.ui.sepe.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.certificados.Terrores;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RESPUESTACERTIFICADOEMPRESATYPE.CuentaCotizacion;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RESPUESTACERTIFICADOEMPRESATYPE.CuentaCotizacion.DatosTrabajador;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RESPUESTACERTIFICADOEMPRESATYPE.CuentaCotizacion.DescripcionResultado;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa.Resultado;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;
import com.esferalia.aon.ui.sepe.file.CertificadosResponseReader;
import com.esferalia.aon.ui.sepe.utils.SEPEConnectionProvider.SEPECommunicationError;


public class CertificadosCommunicator implements ISepeCommunicator, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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

			Pattern p = Pattern.compile("(?<=\\<COD_ERROR>)(.*?)(?=\\</COD_ERROR>)");
			Matcher m = p.matcher(value);
			while(m.find()) {
				if(StringUtils.isNotBlank(m.group())){
					return false; 
				}
			}
			
			p = Pattern.compile("(?<=\\<NUM_ENVIO>)(.*?)(?=\\</NUM_ENVIO>)");
			m = p.matcher(value);
			while(m.find()) {
				if(StringUtils.isNotBlank(m.group())){
					return true; 
				}
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
					if(cuentaCotizacion.getDescripcionResultado()==DescripcionResultado.PROCESADO){
						return true;
					} else if(cuentaCotizacion.getDescripcionResultado()==DescripcionResultado.PROCESADO_PARCIALMENTE){
						return true;
					}
				}
			} catch (Throwable th) {
				LOGGER.error(th.getMessage());
			}
		}
		return false;
	}
	
	@Override
	public String obtainCommunicationNumber(byte[] data) {
		String _data = new String(data);
		_data = _data.replaceAll("\r|\n|\t", "");
		String code = "";
		if( StringUtils.isNotBlank(_data) ){
			if(_data.matches(".*<NUM_ENVIO>.+</NUM_ENVIO>.*")){
				code += _data.replaceAll(".*<NUM_ENVIO>(.*)</NUM_ENVIO>.*", "$1");
			}
			if(_data.matches(".*<COD_ERROR>.+</COD_ERROR>.*")){
				code += _data.replaceAll(".*<COD_ERROR>(.*)</COD_ERROR>.*", "$1");
				if(code!=null && code.startsWith("DEX")){
					try {
						code = "<br/>" + code + " - " + SEPECommunicationError.valueOf(code).getDescription();
					} catch (Exception e) {
						return code + "(error no reconicido)";
					}
				} else {
					code = "<p><b>RESPUESTA OBTENIDA DE SEPE: </b>" + code + "</p>";
					if(getUser()==null){
						searchCertificadosLogin();
					}
					if(getUser()==null || !getUser().matches("[0-9A-Z]\\d{7}[0-9A-Z]")){
						code += "<p><b>Revise y valide el usuario y contraseña del servicio de Contrat@</b></p>";
					}
				}
			}
			if(_data.matches(".*<DESC_ERROR>.+</DESC_ERROR>.*")){
				code += _data.replaceAll(".*<DESC_ERROR>(.*)</DESC_ERROR>.*", "$1");
			}
			return code;
		}
		return null;
	}
	
	private boolean isContractResponse(EMPRESATYPE empresaType, TRABAJADORTYPE trabajadorType, Contract contract) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
		String ccc = empresaType.getCCC();
		Date startDate;
		try {
			startDate = sdf.parse(trabajadorType.getFechaAltaEmpresa());
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		String document = trabajadorType.getDNINIE();
		return ccc.equals(contract.getActivity().getType().getCode() + contract.getEnterpriseCCC().getCcc())
				&& startDate.equals(contract.getStartDate())
				&& document.equals(contract.getPerson().getRegistry().getDocument());
	}
	
	@Override
	public String obtainCommunicationStatus(byte[] data, Contract contract) {
		String status = "";
		status += "<br /> ";
		status += "<div style='background-color:#E4E4E4; width:100%; padding:5px;'><b>Resultado obtenido del SEPE</b></div>";
		if(data!=null){
			String errorMsg = new String(data);
//			fichero no procesado: si se obtiene algun error (comunicacion, fichero no procesado, ...)
			if(errorMsg.matches(".*<COMUNICACION>.*<ERROR>.*</ERROR>.*</COMUNICACION>.*")){
				errorMsg = errorMsg.replaceAll(".*<ERROR>(.*)</ERROR>.*", "$1");
				try {
					errorMsg += " - " + SEPECommunicationError.valueOf(errorMsg).getDescription();
				} catch (Exception e) {
					errorMsg = "<p><b>RESPUESTA OBTENIDA DE SEPE: </b>" + errorMsg + "</p>";
				}
				return status + errorMsg;
			}
//			fichero si procesado: si se obtiene el fichero con los datos procesados
			try {
				String validation = new String(data);
				validation = StringUtils.substring(validation, StringUtils.indexOf(validation, "<Respuesta_Certificado_empresa"), validation.length());
				validation = StringUtils.substring(validation, 0, StringUtils.indexOf(validation, "</Respuesta_Certificado_empresa>"));
				validation += "</Respuesta_Certificado_empresa>";
				data = validation.getBytes();
				RespuestaCertificadoEmpresa respuestaCertificado = obtainFicheroCertificado(data);
			
				for(CuentaCotizacion cuentaCotizacion: respuestaCertificado.getResultado().getCuentaCotizacion()){
					String ccc = cuentaCotizacion.getCCC();
					String bgColor = null;
					if(contract==null){
						if(cuentaCotizacion.getDescripcionResultado()==DescripcionResultado.PROCESADO){
							bgColor = BGCOLOR_PROCESSED;
						} else if(cuentaCotizacion.getDescripcionResultado()==DescripcionResultado.PROCESADO_PARCIALMENTE){
							bgColor = BGCOLOR_PROCESSED_PARTIALLY;
						} else if(cuentaCotizacion.getDescripcionResultado()==DescripcionResultado.RECHAZADO){
							bgColor = BGCOLOR_REFUSED;
						} else {
							bgColor = BGCOLOR_OTHER;
						}
						
						status += "CCC:      " + cuentaCotizacion.getCCC();
						status += "<br /> ";
						status += "RESULTADO:           " + cuentaCotizacion.getDescripcionResultado();
						status += "<br /> ";
						status += "TRAB. PROCESADOS:    " + cuentaCotizacion.getNumTrabajadoresProcesados();
						status += "<br /> ";
						status += "TOTAL TRABAJADORES:  " + cuentaCotizacion.getNumTrabajadoresTotal();
						status += "<br /> ";
					}
					
					if(cuentaCotizacion.getErroresGeneral()!=null 
							&& cuentaCotizacion.getErroresGeneral().getError()!=null 
							&& !cuentaCotizacion.getErroresGeneral().getError().isEmpty()){
						status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES GENERALES</b></div>";
						for(String error: cuentaCotizacion.getErroresGeneral().getError()){
							status += "ERROR: " + error + " - " + Terrores.getEnumByValue(error).getDescription();
							status += "<br /> ";
						}
					}
					
					for(CUENTACOTIZACIONTYPE cuentaCotizacionType: respuestaCertificado.getCertificadoEmpresa().getCuentaCotizacion()){
						if(cuentaCotizacionType.getDatosEmpresa().getCCC().equals(ccc)){
							for(TRABAJADORTYPE trabajadorType: cuentaCotizacionType.getDatosTrabajador()){
								if(contract==null || isContractResponse(cuentaCotizacionType.getDatosEmpresa(), trabajadorType, contract)){
									
									List<DatosTrabajador> trabajadorErrorList = cuentaCotizacion.getDatosTrabajador().stream()
											.filter(o -> o.getDNINIE().equals(trabajadorType.getDNINIE()))
											.collect(Collectors.toList());
									
									bgColor = trabajadorErrorList==null || trabajadorErrorList.isEmpty()?BGCOLOR_PROCESSED:BGCOLOR_REFUSED;
									status += "<br /> ";
									status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'><b>";
									status += trabajadorType.getApellido1() + " ";
									status += trabajadorType.getApellido2() + ", ";
									status += trabajadorType.getNombre();
									status += " ("+trabajadorType.getDNINIE() + ")";
									status += "</b>";
									status += " - ";
									status += "<b> CERTIFICADO ";
									status += trabajadorErrorList==null || trabajadorErrorList.isEmpty()?DescripcionResultado.PROCESADO:DescripcionResultado.RECHAZADO;
									status += "</b>";
									status += "</div>";
									if(trabajadorErrorList!=null && !trabajadorErrorList.isEmpty()){
										DatosTrabajador trabajador = trabajadorErrorList.get(0);
										if(!trabajador.getError().isEmpty()){
											status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b> (";
											status += trabajador.getError().size()+")</div>";
											for(Object error: trabajador.getError()){
												status += "ERROR: " + error + " - " + Terrores.getEnumByValue(error.toString()).getDescription();
												status += "<br /> ";
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (Throwable th) {
				status += "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += "<br /> ";
				status += th.getMessage();
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
			if(appParams.isCertifica2LoginDefined()){
				user = appParams.getCertifica2User();
				passwd = appParams.getCertifica2Password();
			} else if(appParams.isParentCertifica2LoginDefined()){
				user = appParams.getParentCertifica2User();
				passwd = appParams.getParentCertifica2Password();
			}
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
			if(appParams.isCertifica2LoginDefined()){
				testEnv = appParams.getCertifica2TestEnviroment();
				sslEnv = appParams.getCertifica2SSLEnviroment();
			} else if(appParams.isParentCertifica2LoginDefined()){
				testEnv = appParams.isParentCertifica2TestEnviroment();
				sslEnv = appParams.isParentCertifica2SSLEnviroment();
			}
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
