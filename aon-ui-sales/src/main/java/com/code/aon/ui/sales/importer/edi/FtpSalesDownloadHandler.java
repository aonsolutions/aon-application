package com.code.aon.ui.sales.importer.edi;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.seres.connect.sales.v2.data.RECTL;
import com.esferalia.aon.seres.ftp.FtpException;
import com.esferalia.aon.seres.ftp.FtpFile;
import com.esferalia.aon.seres.ftp.FtpLoginException;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.seres.reader.connect.ConnectSalesReader;

public class FtpSalesDownloadHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpSalesDownloadHandler.class);
	
	private IController controller;
	private boolean showEdiFtpWindow;
	
	private String server;
	private Integer port;
	private String user;
	private String password;
	private String remotePath;
	
	private Date date;
	private boolean deleteOnComplete;
	private boolean testing;
	
	private boolean showFtpServerConnectionData;
	
	private List<FtpFileOrder> unreadSalesList;
			
	private SerializableListDataModel unreadSalesModel;
	
	private List<String> remoteDirectoryList;
	
	private SerializableListDataModel remoteDirectoryModel;
	
	public FtpSalesDownloadHandler(IController controller) {
		this.controller = controller;
	}

	
	public SerializableListDataModel getUnreadSalesModel() {
		if(unreadSalesModel == null){
			unreadSalesModel = new SerializableListDataModel(unreadSalesList);
		}
		return unreadSalesModel;
	}
	
	public SerializableListDataModel getRemoteDirectoryModel() {
		if(remoteDirectoryModel == null){
			remoteDirectoryModel = new SerializableListDataModel(remoteDirectoryList);
		}
		return remoteDirectoryModel;
	}
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
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
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isDeleteOnComplete() {
		return deleteOnComplete;
	}

	public void setDeleteOnComplete(boolean deleteOnComplete) {
		this.deleteOnComplete = deleteOnComplete;
	}

	public boolean isTesting() {
		return testing;
	}

	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	public boolean isShowEdiFtpWindow() {
		return showEdiFtpWindow;
	}

	public void setShowEdiFtpWindow(boolean showEdiFtpWindow) {
		this.showEdiFtpWindow = showEdiFtpWindow;
	}
	
	public boolean isShowFtpServerConnectionData(){
		return showFtpServerConnectionData;
	}
	

	private void initContext() {
		ApplicationParameter pServer = AppParamUtil.getParameter("SERES_FTP_SERVER_NAME");
		ApplicationParameter pPort = AppParamUtil.getParameter("SERES_FTP_SERVER_PORT");
		ApplicationParameter pUser = AppParamUtil.getParameter("SERES_FTP_USER");
		ApplicationParameter pPasswd = AppParamUtil.getParameter("SERES_FTP_PASSWORD");
		ApplicationParameter pPath = AppParamUtil.getParameter("SERES_FTP_PATH_ORDER");
		
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
		AppParamUtil.insertParameter("SERES_FTP_SERVER_NAME", server);
		if (port != null && port!=21)
			AppParamUtil.insertParameter("SERES_FTP_SERVER_PORT", String.valueOf(port));
		AppParamUtil.insertParameter("SERES_FTP_USER", user);
		AppParamUtil.insertParameter("SERES_FTP_PASSWORD", password);
		AppParamUtil.insertParameter("SERES_FTP_PATH_ORDER", remotePath);
	}
	
	public void onShowFtpEdi(ActionEvent event) {
		controller.onReset(event);
		unreadSalesList = null;
		unreadSalesModel = null;
		remoteDirectoryList = null;
		remoteDirectoryModel = null;
		showFtpServerConnectionData = false;
		getLogPanel().reset();
		
		initContext();
		checkValidLogin();
		
		setDate(new Date());
		deleteOnComplete = true;
		testing = false;
		
		onRetrieveFtpEdi(event);
	}
	
	public void onShowFtpServerConnectionData(ActionEvent event) {
		showFtpServerConnectionData = !showFtpServerConnectionData;
	}
	
	public void onShowFtpDirectoryTree(ActionEvent event) {
		try {
			remoteDirectoryList = SeresFtpConnectionProvider.retrieveDirectoryList(null,
					server, port, user, password);
		} catch (FtpLoginException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} catch (FtpException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		}
		remoteDirectoryModel = null;
	}
	
	public void onSelectFtpDirectory(ActionEvent event) {
		if (remoteDirectoryModel.isRowAvailable()){
			setRemotePath((String) remoteDirectoryModel.getRowData());
		}
		remoteDirectoryList = null;
		remoteDirectoryModel = null;
	}
	
	public void onRetrieveFtpEdi(ActionEvent event) {
		if (showFtpServerConnectionData) {
			saveLoginInfo();
			checkValidLogin();
		}
		try {
			List<FtpFile> list = SeresFtpConnectionProvider.retrieveFileList(
					remotePath, date, date, server, port, user,
					password);
			ConnectSalesReader reader = new ConnectSalesReader();
			com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler connectHandler = new com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler(
					controller);
			unreadSalesList = new LinkedList<>();
			if(list!=null && !list.isEmpty()){
				list.forEach(ftpFile -> {
					unreadSalesList.add(obtainStrippedOrder(reader, connectHandler, ftpFile));
				});
			}
		} catch (FtpLoginException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		} catch (FtpException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
		}
		if(unreadSalesList!=null) {
			unreadSalesList.sort(new Comparator<FtpFileOrder>() {
				@Override
				public int compare(FtpFileOrder file0, FtpFileOrder file1) {
					return file1.getFtpFile().getModificationDate().compareTo(
							file0.getFtpFile().getModificationDate());
				}
			});
		}
		unreadSalesModel = null;
	}
	
	private FtpFileOrder obtainStrippedOrder(ConnectSalesReader reader, EdiSalesImporterHandler connectHandler, FtpFile ftpFile) {
		FtpFileOrder order = new FtpFileOrder();
		order.setFtpFile(ftpFile);
		
		byte[] byteFile = obtainFtpFile(ftpFile.getName());
		RECTL rectl = null;
		try {
			AonFile aonFile  = (new AonFile());
			aonFile.setData(byteFile);
			rectl = reader.readFile(aonFile.openStream());
			
			String customerCode = connectHandler.obtainCustomerCodeSales(rectl);
			List<RegistryNote> customerEdiRNoteList = connectHandler.searchCustomerRNote(customerCode);
			order.setCustomerCode(customerCode);
			if(customerEdiRNoteList!=null && customerEdiRNoteList.size()>0){
				order.setRegistryNoteList(customerEdiRNoteList);
				if(customerEdiRNoteList!=null && customerEdiRNoteList.size()==1){
					order.setRegistryNote(customerEdiRNoteList.get(0));
				}
			}
			order.setChargeDate(connectHandler.getDateTimeFormatter().parse(rectl.getFecha_horaDelMensaje()));
			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (Throwable th) {
			LOGGER.error(th.getMessage());
		}
		return order;
	}


	public void onImportFileHide(ActionEvent event) {
		getLogPanel().finish();
	}
	
	private byte[] obtainFtpFile(String name) {
		try {
			return SeresFtpConnectionProvider.retrieveFile(remotePath,
					name, server, port, user, password);
		} catch (FtpLoginException e) {
			LOGGER.error(e.getMessage());
			getLogPanel().error(e.getMessage());
		} catch (FtpException e) {
			LOGGER.error(e.getMessage());
			getLogPanel().error(e.getMessage());
		}
		return null;
	}
			
	public String onDownloadFile(ActionEvent event) {
		if(getUnreadSalesModel().isRowAvailable()){
			FtpFileOrder ftpFileOrder = (FtpFileOrder) getUnreadSalesModel().getRowData();
			
			byte[] byteFile = obtainFtpFile(ftpFileOrder.getFtpFile().getName());
			
			FileOutput output = null;
			HttpServletResponse response = null;
			OutputStream out = null;
			try {
				output = new FileOutput();
				output.setContent(byteFile);
				
				// download file
				byte[] data = output.getContent();
				int size = data.length;
				response = DownloadUtil.getResponse();
				out = DownloadUtil.initDownload(response, ftpFileOrder.getFtpFile().getName(), null, size);
				InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(data) );
				IOUtils.copy( fileIn, out );
				IOUtils.closeQuietly(fileIn);
			} catch (IOException e) {
	        	AonUtil.addErrorMessage(e.getMessage());
	        	throw new AbortProcessingException(e.getMessage(), e);
	        } catch (Throwable e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			} finally {
				DownloadUtil.finishDownload(response, out);
			}
		}
		return null;
	}
	
	public void onImportFile(ActionEvent event) {
		
		if(getUnreadSalesModel().isRowAvailable()) {
			if(! isTesting()){
				setShowEdiFtpWindow(false);
			}
			
			FtpFileOrder ftpFileOrder = (FtpFileOrder) getUnreadSalesModel().getRowData();
			
			byte[] byteFile = obtainFtpFile(ftpFileOrder.getFtpFile().getName());
			RegistryNote ediRNote = ftpFileOrder.getRegistryNote();
			
			getLogPanel()
					.info("Iniciando importacion de fichero EDI (CONNECT)");
			com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler connectHandler = new com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler(
					controller);
			connectHandler.setAonFile(new AonFile());
			connectHandler.getAonFile().setData(byteFile);
			try {
				connectHandler.importFile(event, ediRNote, isTesting());
				if( !connectHandler.isSuccess() ){
					setDeleteOnComplete(false);
				}
			} catch (Throwable th) {
				getLogPanel()
						.error("No se reconoce el formato del fichero, o no se ajusta al formato CONNECT");
				getLogPanel().info(
						"Iniciando importacion de fichero EDI (UDAPA)");
				com.code.aon.ui.sales.udapa.EdiSalesImporterHandler udapaHandler = new com.code.aon.ui.sales.udapa.EdiSalesImporterHandler(
						controller);
				udapaHandler.setAonFile(new AonFile());
				udapaHandler.getAonFile().setData(byteFile);
				try {
					udapaHandler.importFile(event, isTesting());
				} catch (Throwable th2) {
					getLogPanel()
							.error("No se reconoce el formato del fichero, o no se ajusta al formato CONNECT");
					setDeleteOnComplete(false);
				}
			}
			if(isDeleteOnComplete()){
				deleteFile(ftpFileOrder.getFtpFile());
			}
		}
		
	}
	
	public void onDeleteFile(ActionEvent event) {
		if (getUnreadSalesModel().isRowAvailable()) {
			FtpFileOrder ftpFileOrder = (FtpFileOrder) getUnreadSalesModel().getRowData();
			deleteFile(ftpFileOrder.getFtpFile());
			unreadSalesList.remove(ftpFileOrder);
			getUnreadSalesModel().setWrappedData(unreadSalesList);
		}
	}
		
	private boolean deleteFile(FtpFile ftpFile) {
		if (ftpFile!=null) {
			getLogPanel().info(
					"Borrando el fichero EDI del servidor FTP de SERESNET");
			try {
				String remotePath = this.remotePath;
				remotePath += remotePath != null && remotePath.endsWith("/") ? ""
						: "/";
				remotePath += ftpFile.getName();
				boolean completed = SeresFtpConnectionProvider.deleteFile(
						remotePath, server, port, user, password);
				if (!completed) {
					getLogPanel().error("El fichero no se ha podido borrar.");
				}
				return completed;
			} catch (Throwable th) {
				getLogPanel().error(th.getMessage());
			}
		}
		return false;
	}
	
	
	private LogPanelController getLogPanel(){
		return LogPanelController.getInstance();
	}


	public class FtpFileOrder implements Serializable {
		private static final long serialVersionUID = 1L;
		private FtpFile ftpFile;
		private String customerCode;
		private List<RegistryNote> registryNoteList;
		private RegistryNote registryNote;
		private Date chargeDate;
		
		public FtpFile getFtpFile() {
			return ftpFile;
		}
		public void setFtpFile(FtpFile ftpFile) {
			this.ftpFile = ftpFile;
		}
		public String getCustomerCode() {
			return customerCode;
		}
		public void setCustomerCode(String customerCode) {
			this.customerCode = customerCode;
		}
		public int getRegistryNoteListCount() {
			return registryNoteList!=null?registryNoteList.size():0;
		}
		public List<RegistryNote> getRegistryNoteList() {
			return registryNoteList;
		}
		public void setRegistryNoteList(List<RegistryNote> registryNoteList) {
			this.registryNoteList = registryNoteList;
		}
		public RegistryNote getRegistryNote() {
			return registryNote;
		}
		public void setRegistryNote(RegistryNote registryNote) {
			this.registryNote = registryNote;
		}
		public Date getChargeDate() {
			return chargeDate;
		}
		public void setChargeDate(Date chargeDate) {
			this.chargeDate = chargeDate;
		}
		public List<SelectItem> getRegistryNotes() {
			List<SelectItem> list = new LinkedList<>();
			if(registryNoteList!=null){
				registryNoteList.forEach(rNote -> {
					list.add(new SelectItem(rNote, getAddress(Integer.parseInt(rNote.getDescription())).getFullAddress()));
				});
			}
			return list;
		}
		public RegistryAddress getAddress() {
			return getAddress(Integer.parseInt(registryNote.getDescription()));		
		}
		public RegistryAddress getAddress(Integer addressId) {
			if(addressId!=null){
				try {
					IManagerBean addressBean = BeanManager.getManagerBean(RegistryAddress.class);
					return (RegistryAddress) addressBean.get(addressId);
				} catch (ManagerBeanException ex) {
					getLogPanel().error(ex.getMessage());
					LOGGER.error(ex.getMessage());
				}
			}
			return null;
		}
	}
	
}
