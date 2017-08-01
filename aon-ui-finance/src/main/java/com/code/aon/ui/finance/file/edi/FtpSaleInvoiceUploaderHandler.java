package com.code.aon.ui.finance.file.edi;

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
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Tag;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.seres.util.ftp.FtpException;
import com.esferalia.aon.file.seres.util.ftp.FtpLoginException;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.file.seres.util.writer.connect.ConnectSaleInvoiceWriter;
import com.esferalia.aon.watson.error.AonCoreException;

public class FtpSaleInvoiceUploaderHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpSaleInvoiceUploaderHandler.class);
	
	private final String PARAM_FTP_SERVER_NAME = "SERES_FTP_SERVER_NAME";
	private final String PARAM_FTP_PORT = "SERES_FTP_SERVER_PORT";
	private final String PARAM_FTP_USER = "SERES_FTP_USER";
	private final String PARAM_FTP_PASSWORD = "SERES_FTP_PASSWORD";
	private final String PARAM_FTP_REMOTE_PATH = "SERES_FTP_PATH_PUSH_INVOICE";
	
	private IController controller;
	private boolean showEdiFtpWindow;
	
	private String server;
	private Integer port;
	private String user;
	private String password;
	private String remotePath;
	
	private boolean showFtpServerConnectionData;
	
	public FtpSaleInvoiceUploaderHandler(IController controller) {
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
		else 
			remotePath = "/";
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
	
	public void onEdiFtpTransfer(ActionEvent event) {
		FileOutput output = null;
		try {
			Invoice invoice = (Invoice) controller.getTo();
			output = exportEdiFile(invoice);
			
			if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
				for(Exception e: output.getErrors()){
					Fd0Exception fd0 = (Fd0Exception) e;
					AonUtil.addErrorMessage(fd0.getDetail());
				}
			} else {
				// upload file
				String referenceCode = invoice.getSeries()+"_"+invoice.getNumber();
				byte[] data = output.getContent();
				FtpStoreProcess sdp = new FtpStoreProcess(data, referenceCode);
				LongProcessThread thread = new LongProcessThread(sdp); 
				thread.start();
				// TODO: mark this invoice as sended 
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public FileOutput exportEdiFile(Invoice invoice){
		FileOutput output = null;
		try {
			if (invoice.getRegistryAddress() != null
					&& invoice.getRegistryAddress().getId() != null) {
				CustomerEdiSupportController ediSupport = (CustomerEdiSupportController) AonUtil
						.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
				try {
					Customer customer = (Customer) BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
					ediSupport.onRecover(customer);
					if(!ediSupport.isEnabled()){
						AonUtil.addErrorMessage("El cliente no tiene EDI habilitado");
						throw new AbortProcessingException("El cliente no tiene EDI habilitado");
					}
				} catch (ManagerBeanException e) {
					AonUtil.addErrorMessage(e.getMessage());
					throw new AbortProcessingException(e.getMessage());
				}
				String customerEdiCabeceraCode = ediSupport.getEdiCodes(
						invoice.getRegistry(), invoice.getRegistryAddress())
						.get(CustomerEdiSupportController.CABECERA);
				String customerEdiPtoEntregaCode = ediSupport.getEdiCodes(
						invoice.getRegistry(), invoice.getRegistryAddress())
						.get(CustomerEdiSupportController.PTO_ENTREGA);
				String customerEdiFacturaCode = ediSupport.getEdiCodes(
						invoice.getRegistry(), invoice.getRegistryAddress())
						.get(CustomerEdiSupportController.FACTURA);
				
				Tag packingTag = ediSupport.obtainPackingTag(
						invoice.getRegistry(),
						invoice.getRegistryAddress());
				if(packingTag==null || packingTag.getId()==null){
					AonUtil.addErrorMessage("No se ha definido el envase para 'mensajería EDI'");
					throw new AbortProcessingException("No se ha definido el envase para 'mensajería EDI'");
				}
				String customerPackage = packingTag.getName();

				CompanyController company = (CompanyController) AonUtil
						.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
				String companyEdiCode = company.getEdiCompanyCode();
				
				// writer file
				ConnectSaleInvoiceWriter writer = new ConnectSaleInvoiceWriter();
				output = writer.createFile(invoice, (Company)company.getTo(), companyEdiCode,
						customerEdiCabeceraCode, customerEdiPtoEntregaCode, customerEdiFacturaCode,
						customerPackage);

				return output;
			} else {
				throw new AonCoreException("La factura no tiene direccion.");
			}
		} catch (IOException e) {
        	AonUtil.addErrorMessage(e.getMessage());
        	throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public class FtpStoreProcess implements ILongProcess {

		private boolean success = false;
		private byte[] data;
		private String referenceCode;
		
		public FtpStoreProcess(byte[] data, String referenceCode) {
			this.data = data;
			this.referenceCode = referenceCode;
		}

		@Override
		public void execute() {
			InputStream inputStream = new BufferedInputStream(
					new ByteArrayInputStream(data));				
			success = storeFtpFile("factura-" + referenceCode + ".edi",
					inputStream);
			IOUtils.closeQuietly(inputStream);
			
			LOGGER.error("FTP STORE: " + success);
			if (success)
				AonUtil.addInfoMessage("Fichero EDI generado y enviado CORRECTAMENTE.");
			else
				AonUtil.addErrorMessage("El fichero no se ha podido enviar.");
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

	}
	
}
