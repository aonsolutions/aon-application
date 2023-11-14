package com.esferalia.aon.seres.ftp.seres;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;


public class FtpStoreProcess implements ILongProcess, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpStoreProcess.class);
	

	protected static final String RECIPIENTS_TO_LOG = "udapalog@aonsolutions.es";
	protected static final String RECIPIENTS_TO_SUCCESS = "udapasuccess@aonsolutions.es";
	protected static final String RECIPIENTS_TO_FAILURES = "udapafailures@aonsolutions.es";
	
	
	private boolean success = false;
	
	private Map<Integer, byte[]> dataMap;
	private Map<Integer, String> referenceCodeMap;
	private DataResponseSource source;
	
	private String domainName;
	private int domainId;
	private String loggedUser;
	
	private String ftpRemotePath;
	private String ftpServer;
	private Integer ftpPort;
	private String ftpUser;
	private String ftpPassword;
	
	
	
	public FtpStoreProcess(DataResponseSource source, String domainName, int domainId, String loggedUser, String ftpRemotePath,
			String ftpServer, Integer ftpPort, String ftpUser, String ftpPassword) {
		this.dataMap = new HashMap<>();
		this.referenceCodeMap = new HashMap<>();
		this.source = source;
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.loggedUser = loggedUser;
		
		this.ftpRemotePath = ftpRemotePath;
		this.ftpServer = ftpServer;
		this.ftpPort = ftpPort;
		this.ftpUser = ftpUser;
		this.ftpPassword = ftpPassword;
	}
	
	public void put(int id, byte[] data, String referenceCode) {
		dataMap.put(id, data);
		referenceCodeMap.put(id, referenceCode);
	}

//	@Override
//	public void execute() {
//		InputStream inputStream = new BufferedInputStream(
//				new ByteArrayInputStream(data));
//		String fileName = "alb-" + referenceCode + ".edi";
//		String fileValue = inputStream.toString();
//		success = storeFtpFile(fileName, inputStream);
//		IOUtils.closeQuietly(inputStream);
//		
//		sendEmail2("DEBUG", "Envio al FTP de Seresnet", "Fichero enviado: "+fileName, fileName, fileValue, RECIPIENTS_TO_LOG);
//		LOGGER.info("FTP STORE: " + success);
//		if (success){
//			sendEmail2("INFO", "Envio al FTP de Seresnet", "Envio correcto: "+fileName, null, null, RECIPIENTS_TO_SUCCESS);
//			LOGGER.info("Fichero EDI generado y enviado CORRECTAMENTE a Seresnet ("+referenceCode+")");
//			markEdiFileTransfered();
//		} else {
//			sendEmail2("ERROR", "Envio al FTP de Seresnet", "Envio NO correcto: "+fileName, null, null, RECIPIENTS_TO_FAILURES);
//			LOGGER.info("El albaran no se ha podido enviar a Seresnet ("+referenceCode+")");
//		}
//	}
	
	@Override
	public void execute() {
		for(Integer id: dataMap.keySet()){
			byte[] data = dataMap.get(id);
			String referenceCode = referenceCodeMap.get(id);
			
			InputStream inputStream = new BufferedInputStream(
					new ByteArrayInputStream(data));
			success = storeFtpFile(referenceCode + ".edi",
					inputStream);
			
			LOGGER.info("FTP STORE: " + success);
			if (success) {
				track(Level.INFO, ResponseMessageType.RESPONSE, id, referenceCode);
			} else {
				track(Level.SEVERE, ResponseMessageType.RESPONSE, id, referenceCode);
			}
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	private boolean storeFtpFile(String fileName, InputStream inputStream) {
		try {
			return SeresFtpConnectionProvider.storeFile(ftpRemotePath, fileName,
					inputStream, ftpServer, ftpPort, ftpUser, ftpPassword);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} 
		return false;
	}
	
	public void track(Level level, ResponseMessageType label, int sourceId, String referenceCode) {
		DataResponse dr = AON.getDataResponse(domainName, domainId, loggedUser, f->
			f.getDomainProperty().eq(domainId)
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getCodeProperty().eq(referenceCode)));
		if(dr.isEmpty()) {
			dr = new DataResponse();
			dr.setDomain(domainId);
			dr.setResponseDate(new Date());
			dr.setSource(source);
			dr.setCreationUser(loggedUser);
			dr.setCreationDate(new Date());
		}
		dr.setCode(referenceCode);
		dr.setSourceId(sourceId);
		if(dr.getId()!=null) {
			dr.setModificationUser(loggedUser);
			dr.setModificationDate(new Date());
			Integer drId = dr.getId();
			AON.updateDataResponse(domainName, domainId, loggedUser, dr, f->f.getIdProperty().eq(drId));
		} else {
			dr = AON.insertDataResponse(domainName, domainId, loggedUser, dr);
		}
		DataResponseDetail drd = new DataResponseDetail();
		drd.setDataResponse(dr.getId());
		drd.setDomain(domainId);
		drd.setDataVariable(label.name());
		drd.setDataValue(level.equals(Level.INFO)?"OK":"FAIL");
		drd.setCreationUser(loggedUser);
		drd.setCreationDate(new Date());
		AON.insertDataResponseDetail(domainName, domainId, loggedUser, drd);

		

//		if(DataResponseSource.SERES_DELIVERY.equals(source)) {
//			Domain domain = new Domain().setId(domainId).setName(domainName);
//			User user = new User().setLogin(loggedUser);
//			DeliveryInfo di = new DeliveryInfo()
//					.setDelivery(sourceId)
//					.setDomain(domain.getId())
//					.setType(DeliveryCommunicationType.SERES)
//					.setStatus(level.equals(Level.INFO)? DeliveryCommunicationStatus.ACCEPTED : DeliveryCommunicationStatus.WRONG);
//			AON.saveDeliveryInfo(domain, user, di);
//		}

		if( !level.equals(Level.INFO) ) {
			sendEmail2(domainName, domainId, loggedUser, "ERROR", "Envio al FTP de Seresnet", "Envio NO correcto: "+referenceCode, null, null, RECIPIENTS_TO_FAILURES);
		}
	}
	

	protected void sendEmail2(String domainName, Integer domainId, String loggedUser, String logLevel, String subject, String content, String attachName,
			String attachValue, String... recipients) {
		JSONObject json = new JSONObject();
		try {
			MailAccount mail = getAdminMailAccount(domainName, domainId, loggedUser);
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
						.put("content", content)
						.put("subject", subject)
						.put("login", loggedUser)
						.put("domainName", domainName)
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
	
	public MailAccount getAdminMailAccount(String domainName, Integer domainId, String login) {
		LinkedList<MailAccount> list = AON.getMailAccountList(domainName, domainId, login, 
				f -> f.getDomainProperty().eq(0));
		return list!=null && !list.isEmpty()?list.getFirst():null;
	}
	
	public enum ResponseMessageType {
		COMMIT, RESPONSE;
	}

	
	public class SeresFtpProcessThread implements Runnable {
		private ILongProcess longProcess;
		private Thread thread;
		
		public SeresFtpProcessThread(ILongProcess longProcess) {
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
}		
		
// *********************************
// *********************************
// *********************************
		
interface ILongProcess {
	void execute();
}
	
	
