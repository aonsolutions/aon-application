package com.esferalia.aon.seres.ftp.seres;

import static com.code.aon.customer.IEdiSupport.ALBARANES;
import static com.code.aon.customer.IEdiSupport.CABECERA;
import static com.code.aon.customer.IEdiSupport.DEPARTMENT;
import static com.code.aon.customer.IEdiSupport.EDI_CODES_PATTERN;
import static com.code.aon.customer.IEdiSupport.FACTURA;
import static com.code.aon.customer.IEdiSupport.FINANCIERA;
import static com.code.aon.customer.IEdiSupport.MEDIDA;
import static com.code.aon.customer.IEdiSupport.PEDIDOS;
import static com.code.aon.customer.IEdiSupport.PTO_ENTREGA;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SERES;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.ResponseMessageType;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.SeresFtpProcessThread;
import com.esferalia.aon.seres.writer.connect.ConnectDeliveryWriterOccam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.jcraft.jsch.JSchException;


public class FtpDeliveryUploadOccamHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpDeliveryUploadOccamHandler.class);
	
	private final String PARAM_FTP_SERVER_NAME = "SERES_FTP_SERVER_NAME";
	private final String PARAM_FTP_PORT = "SERES_FTP_SERVER_PORT";
	private final String PARAM_FTP_USER = "SERES_FTP_USER";
	private final String PARAM_FTP_PASSWORD = "SERES_FTP_PASSWORD";
	private final String PARAM_FTP_REMOTE_PATH = "SERES_FTP_PATH_DELIVERY";
		
	private String server;
	private Integer port;
	private String user;
	private String password;
	private String remotePath;

	private String domainName;
	private Integer domainId;
	private String login;
	
	public FtpDeliveryUploadOccamHandler(String domainName, Integer domainId, String login){
		this.domainName = domainName;
		this.domainId = domainId;
		this.login = login;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	private void initContext() {
		ApplicationParameter pServer = AON.getApplicationParameter(domainName, domainId, login, PARAM_FTP_SERVER_NAME);
		ApplicationParameter pPort = AON.getApplicationParameter(domainName, domainId, login, PARAM_FTP_PORT);
		ApplicationParameter pUser = AON.getApplicationParameter(domainName, domainId, login, PARAM_FTP_USER);
		ApplicationParameter pPasswd = AON.getApplicationParameter(domainName, domainId, login, PARAM_FTP_PASSWORD);
		ApplicationParameter pPath = AON.getApplicationParameter(domainName, domainId, login, PARAM_FTP_REMOTE_PATH);
		
		if (pServer != null)
			server = pServer.getValue();
		if (pPort != null && NumberUtils.isNumber(pPort.getValue()))
			port = Integer.valueOf(pPort.getValue());
		else 
			port = 21;
		if (pUser != null)
			user = pUser.getValue();
		if (pPasswd != null)
			password = pPasswd.getValue();
		if (pPath != null)
			remotePath = pPath.getValue();
	}

	public void checkValidLogin() throws JSchException {
		SeresFtpConnectionProvider.checkLogin(server, port, user, password);
	}
	
	public void onEdiFtpTransfer(Delivery delivery) throws JSchException {
		List<Delivery> list = new ArrayList<>();
		list.add(delivery);
		onEdiFtpTransfer(list);	
	}
	public void onEdiFtpTransfer(List<Delivery> deliveryList) throws JSchException {
		initContext();
		checkValidLogin();
		
		try {
			FtpStoreProcess fsp = new FtpStoreProcess(DataResponseSource.SERES_DELIVERY, domainName, domainId, login,
					this.remotePath, this.server, this.port, this.user, this.password);
			
			for(Delivery delivery: deliveryList) {
				String referenceCode = delivery.getSeries()+"_"+delivery.getNumber();
				FileOutput output = exportEdiFile(delivery);
				if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
					for(Exception e: output.getErrors()){
						Fd0Exception fd0 = (Fd0Exception) e;
						LOGGER.error(fd0.getMessage());
					}
					fsp.track(Level.SEVERE, ResponseMessageType.COMMIT, delivery.getId(), referenceCode);
				} else {
					byte[] data = output.getContent();
					fsp.put(delivery.getId(), data, referenceCode);
					fsp.track(Level.INFO, ResponseMessageType.COMMIT, delivery.getId(), referenceCode);
				}
			}
			
			SeresFtpProcessThread thread = fsp.new SeresFtpProcessThread(fsp); 
			thread.start();
		
		} catch (Throwable e) {
			throw new AonCoreException(e.getMessage(), e);
		}
	}
	
	
	public FileOutput exportEdiFile(Delivery delivery) throws AonException {
		FileOutput output = null;
		try {
			com.esferalia.aon.seres.writer.connect2.ConnectDeliveryWriterOccam writer = new com.esferalia.aon.seres.writer.connect2.ConnectDeliveryWriterOccam(domainName, domainId, login);
			EdiCodes codes = SERES.getEdiCodes(domainName, domainId, login, delivery);
			byte[] attachData = ConnectDeliveryWriterOccam.DeliveryPackages.obtainPackageDataAttach(
					domainName, domainId, login, delivery.getId()).getData();
			if(attachData==null || "".equals(attachData)){
				String remarks = delivery.getRemarks();
				if(remarks.replaceAll("\r|\n", "").matches(".*\\[ENV=.*\\].*")){
					remarks = remarks.substring(remarks.indexOf("["));
					remarks = remarks.substring(0, remarks.lastIndexOf("]")+1);
				}
				attachData = remarks.getBytes();
			}
			if(attachData==null || "".equals(attachData)){
				String msg = "Secuencia de embalajes NO definida";
				throw new AonException(msg);
			}
			
			// write file
			output = writer.createFile(delivery, new String(attachData), codes);
			return output;
		} catch (IOException e) {
			throw new AonException(e.getMessage(), e);
		}
	}
	
	public Map<String, String> obtainEdiCodes(Integer registryId, Integer rAddressId){
		String value = getRegistryNoteComments(rAddressId.toString(), registryId);
		Map<String, String> values = new HashMap<>();
		Matcher m;
		Pattern p = Pattern.compile(EDI_CODES_PATTERN);
		if (value != null && (m = p.matcher(value)).find()) {
			values.put(CABECERA, m.groupCount()>0 ? m.group(1) : null);
			values.put(PEDIDOS, m.groupCount()>1 ? m.group(2) : null);
			values.put(PTO_ENTREGA, m.groupCount()>2 ? m.group(3) : null);
			values.put(FACTURA, m.groupCount()>3 ? m.group(4) : null);
			values.put(FINANCIERA, m.groupCount()>4 ? m.group(5) : null);
			values.put(ALBARANES, m.groupCount()>5 ? m.group(6) : null);
			values.put(MEDIDA, m.groupCount()>6 ? m.group(7) : null);
			values.put(DEPARTMENT, m.groupCount()>7 ? m.group(8) : null);
		}
		return values;
	}
	
	public String obtainPackingTag(Integer registryId, Integer rAddressId) throws AonException{
		String value = getRegistryNoteComments(rAddressId.toString(), registryId);
		Matcher m;
		Pattern p = Pattern.compile(MEDIDA + "=([^;]*);");
		try {
			if (value != null && (m = p.matcher(value)).find()) {
				Integer tagId = Integer.valueOf(m.group(1));
				com.esferalia.aon.occam.api.model.office.Tag tag = AON.getTag(domainName, domainId, login, tagId);
				return tag!=null?tag.getName():null;
			}
		} catch (NumberFormatException e) {
			throw new AonException(e.getMessage(), e);
		}
		return null;
	}
	
	private String getRegistryNoteComments(String key, Integer registryId) {
		return AON.getRegistryNoteStream( domainName, domainId, login,
				f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().eq(registryId))
						.and(f.getDescriptionProperty().eq(key)))
			.findFirst().orElse(new com.esferalia.aon.occam.api.model.registry.RegistryNote())
			.getComments();		
	}

	
}
