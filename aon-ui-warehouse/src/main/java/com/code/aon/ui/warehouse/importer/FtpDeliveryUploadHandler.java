package com.code.aon.ui.warehouse.importer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.file.seres.util.ftp.FtpException;
import com.esferalia.aon.file.seres.util.ftp.FtpLoginException;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.file.seres.util.writer.connect.ConnectDeliveryWriter;

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
		} catch (FtpLoginException e) {
			showFtpServerConnectionData = true;
			AonUtil.addErrorMessage(e.getMessage());
		} catch (FtpException e) {
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

	private boolean storeFtpFile(String fileName, InputStream inputStream) {
		if (showFtpServerConnectionData) {
			saveLoginInfo();
		}
		
		try {
			return SeresFtpConnectionProvider.storeFile(remotePath, fileName,
					inputStream, server, port, user, password);
		} catch (FtpLoginException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} catch (FtpException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		}
		return false;
	}
	
	
	public void onEdiFtpTransfer(ActionEvent event) {

		FileOutput output = null;
		try {
			Delivery delivery = (Delivery) controller.getTo();
			CustomerEdiSupportController ediSupport = (CustomerEdiSupportController) AonUtil
					.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
			String customerEdiCode = ediSupport.getEdiCodes(
					delivery.getCustomer().getRegistry(),
					delivery.getRegistryAddress()).get(
					CustomerEdiSupportController.ALBARANES);
			String deliveryPointEdiCode = ediSupport.getEdiCodes(
					delivery.getCustomer().getRegistry(),
					delivery.getRegistryAddress()).get(
							CustomerEdiSupportController.PTO_ENTREGA);
			CompanyController company = (CompanyController) AonUtil
					.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			String companyEdiCode = company.getEdiCompanyCode();

			// writer file
			ConnectDeliveryWriter writer = new ConnectDeliveryWriter();
			output = writer.createFile(delivery, companyEdiCode,
					customerEdiCode, deliveryPointEdiCode);

			// upload file
			String referenceCode = delivery.getSeries()+"_"+delivery.getNumber();
			byte[] data = output.getContent();
			InputStream inputStream = new BufferedInputStream(
					new ByteArrayInputStream(data));

			boolean success = storeFtpFile("albaran-" + referenceCode + ".edi",
					inputStream);

			// TODO: mark this delivery as sended 
			if (success)
				AonUtil.addInfoMessage("Fichero EDI generado y enviado CORRECTAMENTE.");
			else
				AonUtil.addErrorMessage("El fichero no se ha podido enviar.");

			IOUtils.closeQuietly(inputStream);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}

	}

	
}
