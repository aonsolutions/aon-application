package com.esferalia.aon.file.seres.util.ftp.seres;

import static com.code.aon.customer.IEdiSupport.ALBARANES;
import static com.code.aon.customer.IEdiSupport.CABECERA;
import static com.code.aon.customer.IEdiSupport.EDI_CODES_PATTERN;
import static com.code.aon.customer.IEdiSupport.FACTURA;
import static com.code.aon.customer.IEdiSupport.FINANCIERA;
import static com.code.aon.customer.IEdiSupport.MEDIDA;
import static com.code.aon.customer.IEdiSupport.PEDIDOS;
import static com.code.aon.customer.IEdiSupport.PTO_ENTREGA;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.customer.IEdiSupport;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.file.seres.util.ftp.FtpException;
import com.esferalia.aon.file.seres.util.ftp.FtpLoginException;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.file.seres.util.writer.connect.ConnectDeliveryWriterOccam;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

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

	public void checkValidLogin() throws FtpLoginException, FtpException {
		SeresFtpConnectionProvider.checkLogin(server, port, user, password);
	}
		
	
	public void transferEdiFtp(Delivery delivery) throws FtpLoginException, FtpException, AonException {
		FileOutput output = exportEdiFile(delivery);
		
		if(output!=null && output.getErrors()!=null && output.getErrors().size()>0) {
			for(Exception e: output.getErrors()){
				Fd0Exception fd0 = (Fd0Exception) e;
				LOGGER.info(fd0.getDetail());
			}
		} else {
			initContext();
			byte[] data = output.getContent();
			FtpStoreProcess sdp = new FtpStoreProcess(data, delivery);
			LongProcessThread thread = new LongProcessThread(sdp); 
			thread.start();
		}
	}
	
	public FileOutput exportEdiFile(Delivery delivery) throws AonException {
		FileOutput output = null;
		ConnectDeliveryWriterOccam writer = new ConnectDeliveryWriterOccam(domainName, domainId, login);
		Map<String, String> ediCodes = obtainEdiCodes(delivery.getCustomer(), delivery.getAddress());
		try {			
			String customerEdiCode = ediCodes.get(IEdiSupport.ALBARANES);
			String deliveryPointEdiCode = ediCodes.get(IEdiSupport.PTO_ENTREGA);
			String customerPackage = obtainPackingTag(
					delivery.getCustomer(),
					delivery.getAddress());
			String companyEdiCode = obtainEdiCompanyCode();
			
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
			output = writer.createFile(delivery, new String(attachData), companyEdiCode,
					customerEdiCode, deliveryPointEdiCode, customerPackage);
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
		}
		return values;
	}
	

	private String obtainEdiCompanyCode() {
		ApplicationParameter param = AON.getApplicationParameter(domainName, domainId, login, AppParam.EDI_COMPANY_CODE.getValue());
		return param!=null ? param.getValue() : null;
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
		return AON.getRNoteStream( domainName, domainId, login,
				f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().eq(registryId))
						.and(f.getDescriptionProperty().eq(key)))
			.findFirst().orElse(new com.esferalia.aon.occam.api.model.registry.RegistryNote())
			.getComments();		
	}


	public class FtpStoreProcess implements ILongProcess{
		private boolean success = false;
		private byte[] data;
		private String referenceCode;
		private Delivery delivery;
		
		public FtpStoreProcess(byte[] data, Delivery delivery) {
			this.data = data;
			this.delivery = delivery;
			this.referenceCode = delivery.getSeries()+"_"+delivery.getNumber();
		}

		@Override
		public void execute() {
			InputStream inputStream = new BufferedInputStream(
					new ByteArrayInputStream(data));
			String fileName = "alb-" + referenceCode + ".edi";
			String fileValue = inputStream.toString();
			success = storeFtpFile(fileName, inputStream);
			IOUtils.closeQuietly(inputStream);
			
			sendEmail2("DEBUG", "Envio al FTP de Seresnet", "Fichero enviado: "+fileName, fileName, fileValue, RECIPIENTS_TO_LOG);
			LOGGER.info("FTP STORE: " + success);
			if (success){
				sendEmail2("INFO", "Envio al FTP de Seresnet", "Envio correcto: "+fileName, null, null, RECIPIENTS_TO_SUCCESS);
				LOGGER.info("Fichero EDI generado y enviado CORRECTAMENTE a Seresnet ("+referenceCode+")");
				markEdiFileTransfered();
			} else {
				sendEmail2("ERROR", "Envio al FTP de Seresnet", "Envio NO correcto: "+fileName, null, null, RECIPIENTS_TO_FAILURES);
				LOGGER.info("El albaran no se ha podido enviar a Seresnet ("+referenceCode+")");
			}
		}
		
		private boolean storeFtpFile(String fileName, InputStream inputStream) {
			try {
				return SeresFtpConnectionProvider.storeFile(remotePath, fileName,
						inputStream, server, port, user, password);
			} catch (FtpLoginException e) {
				LOGGER.error(e.getMessage());
			} catch (FtpException e) {
				LOGGER.error(e.getMessage());
			}
			return false;
		}
		
		private void markEdiFileTransfered() {
			if(delivery!=null && delivery.getId()!=null){				
				delivery.setRemarks(delivery.getRemarks()+ "\nENVIADO A SERESNET");
				AON.updateDelivery(domainName, domainId, login, delivery);
			}
		}

	}
	
	public interface ILongProcess {
		void execute();
	}
	
	public class LongProcessThread implements Runnable {
		private ILongProcess longProcess;
		private Thread thread;
	    
	    public LongProcessThread(ILongProcess longProcess) {
	         this.longProcess = longProcess;
	    }

		public void start() {
	         thread = new Thread(this);
	         thread.start();
	    }
		
		public void interrupt() {
			if(thread!=null){
				thread.interrupt();
			}
		}
		
		public boolean isTerminated(){
			return thread.getState()==Thread.State.TERMINATED;
		}
			
		@Override
		public void run() {
	        if (thread != null) {
	        	longProcess.execute();
	        }
		}

	}
	
	protected static final String RECIPIENTS_TO_LOG = "udapalog@aonsolutions.es";
	protected static final String RECIPIENTS_TO_SUCCESS = "udapasuccess@aonsolutions.es";
	protected static final String RECIPIENTS_TO_FAILURES = "udapafailures@aonsolutions.es";
	
	protected void sendEmail2(String logLevel, String subject, String content, String attachName,
			String attachValue, String... recipients) {
		JSONObject json = new JSONObject();
		try {
			MailAccount mail = getAdminMailAccount();
			if(mail==null || mail.getId()==null){
				LOGGER.error("No ADMIN mail account defined, cannot continue with email sending!");
			} else {
				String recipientsTo = "";
//				if (isDevEnabled()) {
					recipientsTo = "eagirrezabal@aonsolutions.es";
					subject = "[AON/Test-"+logLevel+"] " + subject;
//					LOGGER.info("*** RUNNING TEST ENVIRONMENT, AVOID SPAM RECIPIENTS TO.");
//				} else {
//					if (recipients != null) {
//						for (String to : recipients) {
//							if (!recipientsTo.isEmpty())
//								recipientsTo += ",";
//							recipientsTo += to;
//						}
//					}
//					subject = "[AON-"+logLevel+"] " + subject;
//				}
				json.put("mailAccountId", mail.getId())
						.put("recipientsTo", recipientsTo)
						.put("content", content).put("subject", subject)
						.put("login", getUser()).put("domainName", domainName)
						.put("domainId", domainId)
						.put("bcc", "eagirrezabal@aonsolutions.es");

				if (attachValue == null || "".equals(attachValue)) {
					json.put("md5", "");
				} else {
					String encode = Base64.getEncoder().encodeToString(
							attachValue.getBytes());
					json.put("md5", encode)
							.put("attachName", attachName + ".xml")
							.put("mimetype", MimeType.XML.ordinal());
				}

//				String url = getScheme() + "://" + domainName
//						+ (isDevEnabled() ? ":8080/aon-aio" : "")
//						+ "/send_email/";
				String url = "https://" + domainName + "/send_email/";
				LOGGER.info("*** SEND EMAIL URL " + url);
				HttpClientBuilder base = HttpClientBuilder.create();
				HttpClient client = base.build();
				HttpPost post = new HttpPost(url);
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("details", json
						.toString()));
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				HttpResponse resp = client.execute(post);
				System.out.println(resp);
			}
		} catch (JSONException e) {
			LOGGER.error("Error on mailing", e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Error on mailing", e.getMessage());
		}
	}
	
	public MailAccount getAdminMailAccount() {
		LinkedList<MailAccount> list = AON.getMailAccountList(domainName, domainId, login, 
				f -> f.getDomainProperty().eq(0));
		return list!=null && !list.isEmpty()?list.getFirst():null;
	}
	
}
