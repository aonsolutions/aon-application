package com.esferalia.aon.ui.sepe.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;
import com.esferalia.aon.payroll.enumeration.contrata.TERRORES;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.FICHEROPRORROGAS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.RESPUESTAPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.FICHEROTRANSFORMACIONES;
import com.esferalia.aon.sepe.api.contrata.transformaciones.RESPUESTATRANSFORMACIONTYPE;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.controller.SepeAppParamsController;
import com.esferalia.aon.ui.sepe.file.ContrataResponseReader;


public class ContrataCommunicator implements ISepeCommunicator, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataCommunicator.class.getName());
	
	private String document;
	private String user;
	private String mainUser;
	private String passwd;
	private boolean loginRemember;
	private boolean testEnv;
	private boolean sslEnv;
	private boolean passwdVisible;
	private ContrataFileType contrataFileType;
	
	private String xmlResult;
	
	private boolean dataCommunication;
	private boolean dataQuery;
	
	
	public ContrataFileType getContrataFileType() {
		return contrataFileType;
	}

	public void setContrataFileType(ContrataFileType contrataFileType) {
		this.contrataFileType = contrataFileType;
	}

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
		searchContrataLogin();
		searchEnvironmentParams();
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
	public boolean isCommunicationAccepted(byte[] data) {
		if( data!=null ){
			String value = new String(data); 
			value = value.replaceAll("\n", "");
			value = StringUtils.removeStart(value, "<?xml version='1.0' encoding='ISO-8859-1'?>");
			value = StringUtils.removeStart(value, "<COMUNICACION>");
			value = StringUtils.removeEnd(value, "</COMUNICACION>");
			if(value.contains("<NUM_ENVIO>") && !value.contains("<ERROR>")){
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
				FICHEROCONTRATOS contratos = obtainFicheroContratos(data);
				for(Object o: contratos.getCONTRATOSPROCESADOS().getENVIO100AndENVIO130AndENVIO150()){
					RESPUESTACONTRATOTYPE respuestaContratos = obtainRespuestaContrato(o);
					if(StringUtils.equals(respuestaContratos.getRESULTADO(),"ACEPTADO")){
						return true;
					} else if(StringUtils.equals(respuestaContratos.getRESULTADO(),"ACEPTADO CON ERRORES")){
						return true;
					}
					return false;
				}
			} catch (IOException e) {
			} catch (Throwable th) {
			}
		}
		return false;
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
	
	@Override
	public String obtainCommunicationStatus(byte[] data) {
		if(getContrataFileType()==ContrataFileType.CONTRACT){
			return obtainContractCommunicationStatus(data);
		} else if(getContrataFileType()==ContrataFileType.EXTENSION){
			return obtainExtensionCommunicationStatus(data);
		} else if(getContrataFileType()==ContrataFileType.TRANSFORMATION){
			return obtainTrasformationCommunicationStatus(data);
		}
		return null;
	}
	
	public String obtainContractCommunicationStatus(byte[] data) {
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
					status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'>";
					status += contrato.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(1) + " - ";
					status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO();
					if(StringUtils.isNotBlank(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO())){
						status += " ";
						status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO();
					}
					status += ", ";
					status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE();
					if(!StringUtils.equals(respuestaContratos.getRESULTADO(),"RECHAZADO")){
						status += " - <b>CONTRATO ACEPTADO</b>";
						status += " (" + respuestaContratos.getIDCONTRATO() + ")";
					} else {
						status += " - <b>CONTRATO RECHAZADO</b>";
					}
					status += "</div>";
					
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
						

					if(!StringUtils.equals(respuestaContratos.getRESULTADO(),"ACEPTADO")){
						List<String> errores = respuestaContratos.getERRORES().getERROR();
						if(!errores.isEmpty()){
							status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b> (";
							status += respuestaContratos.getERRORES().getERROR().size()+")</div>";
							for(String error: respuestaContratos.getERRORES().getERROR()){
								status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
								status += "<br /> ";
							}
						}
					}
				}
			} catch (IOException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += e.getMessage();
			} catch (Throwable th) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += th.getMessage();
			}
		}
		return status;
	}
	public String obtainExtensionCommunicationStatus(byte[] data) {
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
				FICHEROPRORROGAS prorroga = obtainFicheroProrrogas(data);
				status += "ESTADO FICHERO:      " + prorroga.getESTADOFICHERO();
				status += "<br /> ";
				status += "NUMERO PROCESADOS:    " + prorroga.getNUMEROPROCESADOS();
				status += "<br /> ";
				
				for(Object o: prorroga.getPRORROGASPROCESADAS().getENVIO()){
//					IContratoType contrato = obtainContratoType(o);
					RESPUESTAPRORROGATYPE respuestaProrroga = obtainRespuestaProrroga(o);
					
					String bgColor = null;
					
					if(StringUtils.equals(respuestaProrroga.getRESULTADO(),"ACEPTADO")){
						bgColor = "#E0F8E0";
					} else if(StringUtils.equals(respuestaProrroga.getRESULTADO(),"ACEPTADO CON ERRORES")){
						bgColor = "#F6E3CE";
					} else if(StringUtils.equals(respuestaProrroga.getRESULTADO(),"RECHAZADO")){
						bgColor = "#F8E0E0";
					} else {
						bgColor = "#E4E4E4";
					}
					
					status += "<br /> ";
					status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'>";
//					status += prorroga.getDATOSTRABAJADOR().getIDENTIFICADORPFISICA().substring(1) + " - ";
//					status += prorroga.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO();
//					if(StringUtils.isNotBlank(contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO())){
//						status += " ";
//						status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO();
//					}
//					status += ", ";
//					status += contrato.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE();
					if(!StringUtils.equals(respuestaProrroga.getRESULTADO(),"RECHAZADO")){
						status += " - <b>PRORROGA ACEPTADA</b>";
						status += " (" + respuestaProrroga.getNUMEROPRORROGA() + ")";
					} else {
						status += " - <b>PRORROGA RECHAZADA</b>";
					}
					status += "</div>";
					
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
					
					
					if(!StringUtils.equals(respuestaProrroga.getRESULTADO(),"ACEPTADO")){
						List<String> errores = respuestaProrroga.getERRORES().getERROR();
						if(!errores.isEmpty()){
							status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b> (";
							status += respuestaProrroga.getERRORES().getERROR().size()+")</div>";
							for(String error: respuestaProrroga.getERRORES().getERROR()){
								status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
								status += "<br /> ";
							}
						}
					}
				}
			} catch (IOException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += e.getMessage();
			} catch (Throwable th) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += th.getMessage();
			}
		}
		return status;
	}
	
	public String obtainTrasformationCommunicationStatus(byte[] data) {
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
				FICHEROTRANSFORMACIONES trasformaciones = obtainFicheroTrasformaciones(data);
				status += "ESTADO FICHERO:      " + trasformaciones.getESTADOFICHERO();
				status += "<br /> ";
				status += "NUMERO PROCESADOS:    " + trasformaciones.getNUMEROPROCESADOS();
				status += "<br /> ";

				for(Object o: trasformaciones.getTRANSFORMACIONESPROCESADAS().getENVIO109AndENVIO139AndENVIO189()){
					ITransformacionType trasformacion = obtainTrasformacionType(o);
					RESPUESTATRANSFORMACIONTYPE respuestaTrasformaciones = obtainRespuestaTrasformacion(o);
					
					String bgColor = null;
					
					if(StringUtils.equals(respuestaTrasformaciones.getRESULTADO(),"ACEPTADO")){
						bgColor = "#E0F8E0";
					} else if(StringUtils.equals(respuestaTrasformaciones.getRESULTADO(),"ACEPTADO CON ERRORES")){
						bgColor = "#F6E3CE";
					} else if(StringUtils.equals(respuestaTrasformaciones.getRESULTADO(),"RECHAZADO")){
						bgColor = "#F8E0E0";
					} else {
						bgColor = "#E4E4E4";
					}
					
					status += "<br /> ";
					status += "<div style='border-bottom:1px solid black;background-color:"+bgColor+"; width:100%; padding:5px;'>";
					if(trasformacion.getDATOSCONTRATO().getCLAVECONTRATO()!=null){
						status += trasformacion.getDATOSCONTRATO().getCLAVECONTRATO() + " - ";
					} else if(trasformacion.getDATOSCONTRATO().getIDENTIFICADORPFISICA()!=null){
						status += trasformacion.getDATOSCONTRATO().getIDENTIFICADORPFISICA().substring(1) + " - ";
					} else {
						status += "SIN CODIGO - ";
					}
//					status += trasformacion.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getPRIMERAPELLIDO();
//					if(StringUtils.isNotBlank(trasformacion.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO())){
//						status += " ";
//						status += trasformacion.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO();
//					}
//					status += ", ";
//					status += trasformacion.getDATOSTRABAJADOR().getNOMBREAPELLIDOS().getNOMBRE();
					if(!StringUtils.equals(respuestaTrasformaciones.getRESULTADO(),"RECHAZADO")){
						status += " - <b>TRASFORMACION ACEPTADA</b>";
						status += " (" + respuestaTrasformaciones.getNUMTRANSFORMACION() + ")";
					} else {
						status += " - <b>TRASFORMACION RECHAZADA</b>";
					}
					status += "</div>";
					
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
						

					if(!StringUtils.equals(respuestaTrasformaciones.getRESULTADO(),"ACEPTADO")){
						List<String> errores = respuestaTrasformaciones.getERRORES().getERROR();
						if(!errores.isEmpty()){
							status += "<div style='border-bottom:1px solid black; width:100%; padding:3px;'><b>ERRORES</b> (";
							status += respuestaTrasformaciones.getERRORES().getERROR().size()+")</div>";
							for(String error: respuestaTrasformaciones.getERRORES().getERROR()){
								status += "ERROR: " + error + " - " + TERRORES.getEnumByValue(error).getDescription();
								status += "<br /> ";
							}
						}
					}
				}
			} catch (IOException e) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += e.getMessage();
			} catch (Throwable th) {
				String msg = "No se ha podido obtener los datos del estado de las comunicaciones.";
				status += msg;
				status += "<br /> ";
				status += th.getMessage();
			}
		}
		return status;
	}

	public FICHEROCONTRATOS obtainFicheroContratos(byte[] data) throws IOException, JAXBException, SAXException, ParserConfigurationException {
		ContrataResponseReader reader = new ContrataResponseReader();
		reader.readContratoFile(new ByteArrayInputStream(data));
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
	
	public FICHEROPRORROGAS obtainFicheroProrrogas(byte[] data) throws IOException, JAXBException, SAXException, ParserConfigurationException {
		ContrataResponseReader reader = new ContrataResponseReader();
		reader.readProrrogaFile(new ByteArrayInputStream(data));
		return reader.getFicheroProrrogas();
	}
	
	public RESPUESTAPRORROGATYPE obtainRespuestaProrroga(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getRepuestaProrroga(object);
	}
	
	public FICHEROTRANSFORMACIONES obtainFicheroTrasformaciones(byte[] data) throws IOException, JAXBException, SAXException, ParserConfigurationException {
		ContrataResponseReader reader = new ContrataResponseReader();
		reader.readTrasformacionFile(new ByteArrayInputStream(data));
		return reader.getFicheroTransformaciones();
	}

	public RESPUESTATRANSFORMACIONTYPE obtainRespuestaTrasformacion(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getRepuestaTransformacion(object);
	}

	public ITransformacionType obtainTrasformacionType(Object object) {
		ContrataResponseReader reader = new ContrataResponseReader();
		return reader.getTransformacionType(object);
	}
	
	
	private void searchContrataLogin() {
		SepeAppParamsController appParams = (SepeAppParamsController) AonUtil.getRegisteredBean(ISepeConstants.SEPE_APP_PARAMS_CONTROLLER_NAME);
		try {
			appParams.loadParameters();
			if(appParams.isContrataLoginDefined()){
				user = appParams.getContrataUser();
				passwd = appParams.getContrataPassword();
			} else if(appParams.isParentContrataLoginDefined()){
				user = appParams.getParentContrataUser();
				passwd = appParams.getParentContrataPassword();
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
			if(appParams.isContrataLoginDefined()){
				testEnv = appParams.getContrataTestEnviroment();
				sslEnv = appParams.getContrataSSLEnviroment();
			} else if(appParams.isParentContrataLoginDefined()){
				testEnv = appParams.isParentContrataTestEnviroment();
				sslEnv = appParams.isParentContrataSSLEnviroment();
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido verificar el entorno de trabajo. Se activa el entorno de pruebas (TEST) sin seguridad (no SSL).";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			testEnv = true;
		}
	}
	
	private String sendContrataFile(){
		xmlResult = SEPEConnectionProvider.processContrataCommunication(sslEnv, testEnv, document, user, user, passwd, contrataFileType);
		afterCommunication();
		return xmlResult;
	}

	private String contrataDataQuery(){
		xmlResult = SEPEConnectionProvider.processContrataQuery(sslEnv, testEnv, document, user, user, passwd);
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
