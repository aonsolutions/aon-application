package com.code.aon.ui.sales.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.jooq.Record2;
import org.jooq.Result;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.finance.Finance;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.registry.RegistryItem;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.product.controller.ItemController;
import com.code.aon.ui.sales.importer.AmazonSalesHandler;
import com.code.aon.ui.sales.importer.AmazonSalesReturnHandler;
import com.code.aon.ui.sales.importer.AmazonShipmentHandler;
import com.code.aon.ui.sales.importer.ImporterUtils;
import com.code.aon.ui.sales.importer.SalesImporterHandler;
import com.code.aon.ui.sales.importer.SalesImporterHandler.AmazonSales;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.util.AonUtil;

public class SalesImporterController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private AonFile aonFile;
	
	private DataModel nonExistentItems;

	private DataModel nonExistentSales;
	
	private DataModel importedSales;

	private DataModel existingSales;

	private DataModel generatedSales;

	private DataModel cancelledSales;

	private SalesImporterHandler handler;
	
	private String series;
	
	private boolean succesfullEnding;

	
	public boolean isSuccesfullEnding() {
		return succesfullEnding;
	}

	public void setSuccesfullEnding(boolean succesfullEnding) {
		this.succesfullEnding = succesfullEnding;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}
	
	public boolean isSalesImport(){
		return handler instanceof AmazonSalesHandler;
	}

	public boolean isSalesReturnImport(){
		return handler instanceof AmazonSalesReturnHandler;
	}

	public SalesImporterHandler getHandler() {
		return handler;
	}

	public DataModel getNonExistentItems() {
		if(nonExistentItems==null){
			nonExistentItems = new SerializableListDataModel(handler.getNonExistentItems());
		}
		return nonExistentItems;
	}
	
	public DataModel getNonExistentSales() {
		if(nonExistentSales==null){
			nonExistentSales = new SerializableListDataModel(handler.getNonExistentSales());
		}
		return nonExistentSales;
	}
	
	public DataModel getImportedSales() {
		if(importedSales==null){
			importedSales = new SerializableListDataModel(handler.getImportedSalesList());
		}
		return importedSales;
	}
	
	public DataModel getExistingSales() {
		if(existingSales==null){
			existingSales = new SerializableListDataModel(handler.getExistingSalesList());
		}
		return existingSales;
	}
	
	public DataModel getGeneratedSales() {
		if(generatedSales==null){
			generatedSales = new SerializableListDataModel(handler.getGeneratedSales());
		}
		return generatedSales;
	}

	public DataModel getExcludedSales() {
		if(cancelledSales==null){
			cancelledSales = new SerializableListDataModel(handler.getExcludedSales());
		}
		return cancelledSales;
	}
	
	
	public SalesImporterController() {
		
	}
	
	public AonFile getAonFile() {
		return this.aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		if ( this.aonFile != null ) {
			this.aonFile.clean();	
		}
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		setAonFile(AttachmentUtil.fileUploaded(event));
		reset();
	}
	
	public void onInitAmazonItem(ActionEvent event){
		onInit(event);
		handler = new AmazonSalesHandler();
	}
	public void onInitAmazonShipment(ActionEvent event){
		onInit(event);
		handler = new AmazonShipmentHandler();
	}
	public void onInitAmazonItemReturn(ActionEvent event){
		onInit(event);
		handler = new AmazonSalesReturnHandler();
	}
	public void onInit(ActionEvent event){
		aonFile = null;
		reset();
	}
	
	private void reset(){
		succesfullEnding = false;
		nonExistentItems = null;
		importedSales = null;
		existingSales = null;
		generatedSales = null;
		cancelledSales = null;
		nonExistentSales = null;
		nonExistentItems = null;
		if(handler!=null){
			handler.reset();
		}
	}
	
	public boolean isValidFile() {
		return handler.isValidFile(getAonFile());
	}
	
	
	public void loadData(ActionEvent event){
		reset();
		try {
			if(handler.isValidFile(getAonFile())){
				handler.loadData(getAonFile());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void onAcceptSales(ActionEvent event){
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			// begin process 
			
			handler.accept();
			
			// end process 
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			String msg = "Error al crear los pedidos.";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			throw new AbortProcessingException("No se han podido generar los pedidos", e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		
		reset();
		succesfullEnding = true;
	}
	
	public void onSelectNonexistentItem(ActionEvent event){
		RegistryItem tempRItem = (RegistryItem) getNonExistentItems().getRowData();
		
		ItemController controller = (ItemController) AonUtil.getRegisteredBean("item");
		controller.onReset(event);
		Item item = (Item) controller.getTo();
		item.setProduct(new Product());
		item.getProduct().setCode(tempRItem.getCode());
		item.getProduct().setName(tempRItem.getItem().getDescription());
		controller.setBackAction("salesImporter_form");
		controller.setBackActionListener("salesImporter.loadData");
		
	}

	public void onLoadSales(ActionEvent event){
		AmazonSales sales = (AmazonSales) getExistingSales().getRowData();
		Result<Record2<Integer, String>> record = null;
		if(isSalesReturnImport()){
			record = ImporterUtils.getSalesReturnRecords(Arrays.asList(sales.getPurchaseReference()));
		} else {
			record = ImporterUtils.getSalesRecords(Arrays.asList(sales.getPurchaseReference()));
		}
		try {
			if(record.size()==1){
				Integer id = record.get(0).value1();
				SalesController controller = (SalesController) AonUtil.getRegisteredBean(ISalesConstants.SALES_CONTROLLER_NAME);
				controller.onLoad(event, id, "salesImporter_form", null);
			} else if(record.size()>1){
				AonUtil.addErrorMessage("Existen múltiples pedidos con el mismo código: " + sales.getPurchaseReference());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("onLoadSales: " + e.getMessage());
		}
	}

	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getSalesSeriesIds();
	}
	
}
