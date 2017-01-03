package com.code.aon.ui.sales.importer.edi;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.seres.util.ftp.FtpFile;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;

public class FtpSalesImporterHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpSalesImporterHandler.class);
	private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyMMddhhmm");
	
	private IController controller;
	private boolean showEdiFtpWindow;
	
	private String ftpRemotePath;
	private String user;
	private String password;
	
	private List<FtpFile> unreadSalesList;
			
	private SerializableListDataModel unreadSalesModel;
	
	public FtpSalesImporterHandler(IController controller) {
		this.controller = controller;
		
		// TODO: this values must to be parametrized
		ftpRemotePath = "/recepcion/orders_d96a";
		user = "ftp1251";
		password = "x1xx0wub";
	}
	
	public SerializableListDataModel getUnreadSalesModel() {
		if(unreadSalesModel == null){
			unreadSalesModel = new SerializableListDataModel(unreadSalesList);
		}
		return unreadSalesModel;
	}
	
	public String getFtpRemotePath() {
		return ftpRemotePath;
	}

	public void setFtpRemotePath(String ftpRemotePath) {
		this.ftpRemotePath = ftpRemotePath;
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
	
	public boolean isShowEdiFtpWindow() {
		return showEdiFtpWindow;
	}

	public void setShowEdiFtpWindow(boolean showEdiFtpWindow) {
		this.showEdiFtpWindow = showEdiFtpWindow;
	}

	


	
	public void onShowFtpEdi(ActionEvent event) {
		controller.onReset(event);
		unreadSalesList = null;
		unreadSalesModel = null;
		getLogPanel().reset();
	}
	
	public void onRetrieveFtpEdi(ActionEvent event) {
		SeresFtpConnectionProvider seres = new SeresFtpConnectionProvider();
		String remotePath = "recepcion/orders_d96a";
		unreadSalesList = seres.obtainFiles(remotePath);
		unreadSalesList.sort(new Comparator<FtpFile>() {
			@Override
			public int compare(FtpFile file0, FtpFile file1) {
				return file1.getModificationDate().compareTo(file0.getModificationDate());
			}
		});
		unreadSalesModel = null;
	}
	
	
	public void onImportFileHide(ActionEvent event) {
		getLogPanel().finish();
	}
	
	public String onDownloadFile(ActionEvent event) {
		if(getUnreadSalesModel().isRowAvailable()){
			FtpFile ftpFile = (FtpFile) getUnreadSalesModel().getRowData();
			
			SeresFtpConnectionProvider seres = new SeresFtpConnectionProvider();
			String remotePath = "recepcion/orders_d96a";
			byte[] byteFile = seres.retrieveFile(remotePath, ftpFile.getName());
			System.out.println(new String(byteFile));
			
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
				out = DownloadUtil.initDownload(response, ftpFile.getName(), null, size);
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
		
		if(getUnreadSalesModel().isRowAvailable()){
			FtpFile file = (FtpFile) getUnreadSalesModel().getRowData();

			SeresFtpConnectionProvider seres = new SeresFtpConnectionProvider();
			String remotePath = "recepcion/orders_d96a";
			byte[] out = seres.retrieveFile(remotePath, file.getName());
			System.out.println(new String(out));
			
			getLogPanel()
					.info("Iniciando importacion de fichero EDI (CONNECT)");
			com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler connectHandler = new com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler(
					controller);
			connectHandler.setAonFile(new AonFile());
			connectHandler.getAonFile().setData(out);;
			try {
				connectHandler.onImportFile(event);
			} catch(Throwable th){
				AonUtil.addErrorMessage("No se reconoce el formato del fichero, o no se ajusta al formato CONNECT");
				getLogPanel()
						.error("No se reconoce el formato del fichero, o no se ajusta al formato CONNECT");
				getLogPanel().info(
						"Iniciando importacion de fichero EDI (UDAPA)");
				com.code.aon.ui.sales.udapa.EdiSalesImporterHandler udapaHandler = new com.code.aon.ui.sales.udapa.EdiSalesImporterHandler(
						controller);
				udapaHandler.setAonFile(new AonFile());
				udapaHandler.getAonFile().setData(out);;
				try {
					udapaHandler.onImportFile(event);
				} catch (Throwable th2) {
					getLogPanel()
							.error("No se reconoce el formato del fichero, o no se ajusta al formato CONNECT");
				}
			}
		}
		
	}
	
	
	private LogPanelController getLogPanel(){
		return LogPanelController.getInstance();
	}

	
}
