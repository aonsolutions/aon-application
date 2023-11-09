package com.code.aon.ui.warehouse.importer;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SERES;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.seres.DeliveryPackages;
import com.esferalia.aon.seres.ftp.FtpException;
import com.esferalia.aon.seres.ftp.FtpLoginException;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.ResponseMessageType;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.SeresFtpProcessThread;
import com.esferalia.aon.seres.writer.connect2.ConnectDeliveryWriterOccam;;

public class FtpDeliveryUploadHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpDeliveryUploadHandler.class);
	
	private final String PARAM_FTP_SERVER_NAME = "SERES_FTP_SERVER_NAME";
	private final String PARAM_FTP_PORT = "SERES_FTP_SERVER_PORT";
	private final String PARAM_FTP_USER = "SERES_FTP_USER";
	private final String PARAM_FTP_PASSWORD = "SERES_FTP_PASSWORD";
	private final String PARAM_FTP_REMOTE_PATH = "SERES_FTP_PATH_DELIVERY";
	
	private IController controller;
	private boolean showEdiFtpWindow;
	
	private String server;
	private Integer port;
	private String user;
	private String password;
	private String remotePath;
	
	private boolean showFtpServerConnectionData;
	
	
	public FtpDeliveryUploadHandler(IController controller) {
		this.controller = controller;
	}
	
	
	public boolean isShowEdiFtpWindow() {
		return showEdiFtpWindow;
	}

	public void setShowEdiFtpWindow(boolean showEdiFtpWindow) {
		this.showEdiFtpWindow = showEdiFtpWindow;
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

	public boolean isShowFtpServerConnectionData(){
		return showFtpServerConnectionData;
	}
	
	private void initContext() {
		ApplicationParameter pServer = AppParamUtil.getParameter(PARAM_FTP_SERVER_NAME);
		ApplicationParameter pPort = AppParamUtil.getParameter(PARAM_FTP_PORT);
		ApplicationParameter pUser = AppParamUtil.getParameter(PARAM_FTP_USER);
		ApplicationParameter pPasswd = AppParamUtil.getParameter(PARAM_FTP_PASSWORD);
		ApplicationParameter pPath = AppParamUtil.getParameter(PARAM_FTP_REMOTE_PATH);
		
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

	private void checkValidLogin() {
		try {
			showFtpServerConnectionData = false;
			SeresFtpConnectionProvider.checkLogin(server, port, user, password);
		} catch (Exception e) {
			showFtpServerConnectionData = true;
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	private void saveLoginInfo() {
		AppParamUtil.insertParameter(PARAM_FTP_SERVER_NAME, server);
		if (port != null && port!=21)
			AppParamUtil.insertParameter(PARAM_FTP_PORT, String.valueOf(port));
		AppParamUtil.insertParameter(PARAM_FTP_USER, user);
		AppParamUtil.insertParameter(PARAM_FTP_PASSWORD, password);
		AppParamUtil.insertParameter(PARAM_FTP_REMOTE_PATH, remotePath);
	}
	
	public void onShowFtpEdi(ActionEvent event) {
		showFtpServerConnectionData = false;
		initContext();
		checkValidLogin();
	}
	
	public void onShowFtpServerConnectionData(ActionEvent event) {
		showFtpServerConnectionData = !showFtpServerConnectionData;
	}	
	
	public void onEdiFtpTransfer(ActionEvent event) {
		FileOutput output = null;
		try {
			Delivery delivery = (Delivery) controller.getTo();
			output = exportEdiFile(delivery);
			
			if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
				for(Exception e: output.getErrors()){
					Fd0Exception fd0 = (Fd0Exception) e;
					AonUtil.addErrorMessage(fd0.getDetail());
				}
			} else {				
				// upload file
				String referenceCode = delivery.getSeries()+"_"+delivery.getNumber();
				Integer sourceId = delivery.getId();
				String domainName = AonUtil.getDomainName();
				Integer domainId = DomainManager.getCurrentDomain();
				String loggedUser = UserUtils.getInstance().getLoggedUser().getLogin();
				
				FtpStoreProcess fsp = new FtpStoreProcess(DataResponseSource.SERES_DELIVERY, domainName, domainId, loggedUser,
						this.remotePath, this.server, this.port, this.user, this.password);
				if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
					for(Exception e: output.getErrors()){
						Fd0Exception fd0 = (Fd0Exception) e;
						LOGGER.error(fd0.getMessage());
					}
					fsp.track(Level.SEVERE, ResponseMessageType.COMMIT, sourceId, referenceCode);
				} else {
					byte[] data = output.getContent();
					fsp.put(sourceId, data, referenceCode);
					fsp.track(Level.INFO, ResponseMessageType.COMMIT, sourceId, referenceCode);
				}
				SeresFtpProcessThread thread = fsp.new SeresFtpProcessThread(fsp); 
				thread.start();
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public FileOutput exportEdiFile(Delivery delivery) {
		FileOutput output = null;
		try {
			CustomerEdiSupportController ediSupport = (CustomerEdiSupportController) AonUtil
					.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
			try {
				ediSupport.onRecover(delivery.getCustomer());
				if(!ediSupport.isEnabled()){
					AonUtil.addErrorMessage("El cliente no tiene EDI habilitado");
					throw new AbortProcessingException("El cliente no tiene EDI habilitado");
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			}
			
			if(delivery.getCarrierPacking() != null) {
				CarrierPacking cp = AON.getCarrierPacking(AonUtil.getDomainName(), delivery.getDomain(), "", f -> f.getIdProperty().eq(delivery.getCarrierPacking()));
				delivery.setNumberPlate(cp.getNumberPlate());
				delivery.setDriver(cp.getDriverName());
				delivery.setDriverDocument(cp.getDriverDocument());
				delivery.setTrackingNumber(cp.getCarrierReference());
			}

			byte[] attachData = DeliveryPackages.obtainPackageDataAttach(
					AonUtil.getDomainName(), delivery.getDomain(),
					AonUtil.getRemoteUser(), delivery.getId()).getData();
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
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			
			// write file
			
			com.esferalia.aon.occam.api.model.warehouse.Delivery del = AON.getDelivery(AonUtil.getDomainName(), delivery.getDomain(), AonUtil.getRemoteUser(),
					f -> f.getIdProperty().eq(delivery.getId()),
					new Options().setFull(true));

			EdiCodes codes = SERES.getEdiCodes(AonUtil.getDomainName(), del.getDomain(), AonUtil.getRemoteUser(), del);
			ConnectDeliveryWriterOccam writer2 = new ConnectDeliveryWriterOccam(AonUtil.getDomainName(), delivery.getDomain(), AonUtil.getRemoteUser());
			output = writer2.createFile(del, new String(attachData), codes);
			return output;
		} catch (IOException e) {
        	AonUtil.addErrorMessage(e.getMessage());
        	throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
}
