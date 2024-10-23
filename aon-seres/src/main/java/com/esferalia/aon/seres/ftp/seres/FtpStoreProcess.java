package com.esferalia.aon.seres.ftp.seres;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;


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

		if( !level.equals(Level.INFO) ) {
			sendEmail("ERROR", "Envio al FTP de Seresnet", "Envio NO correcto: "+referenceCode, null, null, RECIPIENTS_TO_FAILURES);
		}
	}
	

	protected void sendEmail(String logLevel, String subject, String content, String attachName,
			String attachValue, String... recipients) {
		try {
			File file = null;
			if(!AonStringUtils.isBlank(attachValue)) {
				file = File.createTempFile(attachName, ".xml");
				AonFileUtils.writeByteArrayToFile(file, attachValue.getBytes());
			}
		
			SESMessage sesMessage = new SESMessage()
				.setAlias("Aon Solutions Dev")
				.setFrom("dev@aon.solutions")
				.setTo("aibanez@aonsolutions.es")
				.setSubject("[AON/Test-"+logLevel+"] " + subject)
				.setBody(content)
				.setFile(file);
		
			SES.sendEmail(sesMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	
