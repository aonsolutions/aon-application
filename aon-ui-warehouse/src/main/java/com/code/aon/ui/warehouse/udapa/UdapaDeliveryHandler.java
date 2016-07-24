package com.code.aon.ui.warehouse.udapa;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.customer.controller.CustomerEdiSupportController;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.file.seres.util.writer.udapa.UdapaDeliveryWriter;
import com.esferalia.aon.ingenet.IngenetDeliveryManager;

public class UdapaDeliveryHandler implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UdapaDeliveryHandler.class);
	
	IController controller;

	private boolean showIngenetWindow;
	
	private ArrayList<Integer> checks = new ArrayList<Integer>();
	
	private DataModel model;
	
	private Warehouse warehouse;

	private Map<Integer, String> ingenetDeliveries;

	
	
	public UdapaDeliveryHandler(IController controller) {
		this.controller = controller;
	}

	public boolean isShowIngenetWindow() {
		return showIngenetWindow;
	}

	public void setShowIngenetWindow(boolean showIngenetWindow) {
		this.showIngenetWindow = showIngenetWindow;
	}
	
	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	
	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() {
		if(getModel().isRowAvailable()){
			return checks.contains(getModel().getRowData());
		}
		return false;
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			if (!checks.contains(getModel().getRowData())) {
				checks.add((Integer) getModel().getRowData());
			}
		} else {
			if (checks.contains(getModel().getRowData())) {
				checks.remove(getModel().getRowData());
			}
		}
	}

	public ArrayList<Integer> getCheckedList() {
		return checks;
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		Delivery delivery = ((Delivery)controller.getTo());
		if(delivery !=null && delivery.getId()!=null
				&& delivery.getWorkPlace() != null && delivery.getWorkPlace().getId() != null)
			return WarehouseCollectionsController.getWarehouses(delivery.getWorkPlace(), true);
		else return WarehouseCollectionsController.getWarehouses(null);
	}
	
	public boolean isIngenetPendingDeliveries() {
		return ingenetDeliveries!=null && ingenetDeliveries.keySet().size()>0;
	}

	public Map<Integer, String> getIngenetDeliveries() {
		return ingenetDeliveries;
	}
	
	public void onSincronizeIngenet(ActionEvent event) {
		try {
			ingenetDeliveries = IngenetDeliveryManager.getInstance().obtainUnreadDeliveries(
					AonUtil.getDomainName(), AonUtil.getRemoteUser());
			setModel(new ListDataModel(new ArrayList<Integer>(ingenetDeliveries.keySet())));
			this.checks.clear();
			ingenetDeliveries.keySet().forEach(id -> {this.checks.add(id);});
			this.setShowIngenetWindow(true);
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.error(e.getMessage());
		}
	}
	
	public void confirmIngenetDeliveries(ActionEvent event) {
		
		LogPanelController logPanel = LogPanelController.getInstance();
		logPanel.reset();
		
		try {
			// copy delivery to AON
			Warehouse warehouse = getWarehouse() != null && getWarehouse().getId() != null ? getWarehouse()
					: ((Warehouse) getWarehouses().get(0).getValue());  
			
			getLogPanel().info("Inicio del proceso de importacion");
			
			for(int idx=0; idx<checks.size();idx++){
				Integer ingenetDeliveryId = checks.get(idx);
				com.esferalia.aon.occam.api.model.warehouse.Delivery aonDelivery = IngenetDeliveryManager.getInstance().createAonDelivery(
						AonUtil.getDomainName(), 
						AonUtil.getRemoteUser(),
						DomainManager.getCurrentDomain(),
						ingenetDeliveryId,
						warehouse.getWorkPlace().getId(),
						warehouse.getId());
				if(aonDelivery==null || aonDelivery.getId()==null){
					throw new AbortProcessingException("No se ha podido crear el albaran " + ingenetDeliveryId);
				}
				getLogPanel().info("Albaran " + (aonDelivery.getSeries()!=null?aonDelivery.getSeries():"") + "/" + aonDelivery.getNumber() + " creado correctamente");
			}
			
			getLogPanel().info("Albaranes importados correctamente desde INGENET");
		} catch (Exception e) {
			getLogPanel().info("NO SE HA PODIDO PROCESAR EL TRASPASO");
			getLogPanel().error(e.getMessage());
		}
		getLogPanel().info("Proceso finalizado.");
	}
	
	public void onExportUdapaEdiFile(ActionEvent event) {
		FileOutput output = null;
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			Delivery delivery = (Delivery) controller.getTo();
			CustomerEdiSupportController ediSupport = (CustomerEdiSupportController) AonUtil.getRegisteredBean(ICustomerConstants.CUSTOMER_EDI_SUPPORT_CONTROLLER_NAME);
			String customerEdiCode = ediSupport.getEdiCodes(delivery.getCustomer().getRegistry(), delivery.getRegistryAddress()).get(CustomerEdiSupportController.ALBARANES);
			CompanyController company = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			String companyEdiCode = company.getEdiCompanyCode();
			
			// writer file
			UdapaDeliveryWriter writer = new UdapaDeliveryWriter();
			output = writer.createFile(delivery, companyEdiCode, customerEdiCode);
		
			// download file
        	String name = "albaran";
    		String number = delivery.getReferenceCode();
    		byte[] data = output.getContent();
        	int size = data.length;
			response = DownloadUtil.getResponse();
    		out = DownloadUtil.initDownload(response, name+"."+number, null, size);
        	InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(data) );
        	IOUtils.copy( fileIn, out );
        	IOUtils.closeQuietly(fileIn);
        } catch (IOException e) {
        	LOGGER.error(e.getMessage());
        	throw new AbortProcessingException(e.getMessage(), e);
        } catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}
	
	private LogPanelController getLogPanel(){
		return LogPanelController.getInstance();
	}
	
	public void onLogPanelFinish(ActionEvent event) {
		getLogPanel().finish();
		controller.onSearch(event);
	}
	
	
}
