package com.code.aon.ui.sales.udapa;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.sales.Sales;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;

public class SalesIngenetHandler implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static String INGENET_STATUS = "STATUS";
	
	private SalesController controller;
	private boolean showIngenetWindow;
	
	private Sales sales;
	private Date responseDate;
	
	
	public enum SalesIngenetStatus {
		PENDING,
		REOPENED,
		RETRIEVED,
		CLOSED,
		PROCESSED;
	}
	
	
	public SalesIngenetHandler(SalesController controller) {
		this.controller = controller;
		this.sales = (Sales) controller.getTo();
	}
	
	
	public Sales getSales() {
		return sales;
	}

	public void setSales(Sales sales) {
		this.sales = sales;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}
	
	public boolean isShowIngenetWindow() {
		return showIngenetWindow;
	}
	public void setShowIngenetWindow(boolean value) {
		this.showIngenetWindow = value;
	}
	

	public void onShowIngenetWindow(ActionEvent event) {
		setResponseDate(sales.getIssueDate());
	}
	
	public void onEnable(ActionEvent event){
		String customerName = StringUtils.abbreviate(sales.getCustomer().getRegistry().getFullName(), 30-sales.getReferenceCode().length());
		
		DataResponse response = new DataResponse();
		response.setDomain(sales.getDomain());
		response.setCode(sales.getReferenceCode() + ";" + customerName);
		response.setResponseDate(getResponseDate());
		response.setSource(DataResponseSource.INGENET_SALES);
		response.setSourceId(sales.getId());
		response = AON.insertDataResponse(AonUtil.getDomainName(),
				sales.getDomain(), AonUtil.getRemoteUser(), response);
		
		DataResponseDetail detail = new DataResponseDetail();
		detail.setDomain(response.getDomain());
		detail.setDataResponse(response.getId());
		detail.setDataVariable(INGENET_STATUS);
		detail.setDataValue(SalesIngenetStatus.PENDING.name());
		detail = AON.insertDataResponseDetail(AonUtil.getDomainName(),
				sales.getDomain(), AonUtil.getRemoteUser(), detail);
		
		// TODO create data_attach if needed
//		DataAttach attach = new DataAttach();
		
		// process the sales for Ingenet
		controller.onBlock(event);
	}
	
	public boolean isEnabled() {
		return SalesIngenetStatus.PENDING.name().equals(getLastStatus());
	}
	
	public boolean isReopened() {
		return SalesIngenetStatus.REOPENED.name().equals(getLastStatus());
	}
	
	public boolean isRetrieved() {
		return SalesIngenetStatus.RETRIEVED.name().equals(getLastStatus());
	}

	public boolean isClosed() {
		return SalesIngenetStatus.CLOSED.name().equals(getLastStatus());
	}

	public boolean isProcessed() {
		return SalesIngenetStatus.PROCESSED.name().equals(getLastStatus());
	}
	
	private String getLastStatus() {
		Integer responseId = getResponseId();
		if(responseId!=null) {
			String status = AON.getDataResponseDetailStream(
					AonUtil.getDomainName(),
					sales.getDomain(),
					AonUtil.getRemoteUser(),
					f -> f.getDataResponseProperty().eq(responseId).and(f.getDataVariableProperty().eq(INGENET_STATUS)))
					.sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
					.map(o -> o.getDataValue())
					.findFirst().orElse(null);
			return status;
		}
		return null;
	}
	
	private Integer getResponseId() {
		if(sales!=null && sales.getId()!=null) {
			DataResponse dr = AON.getDataResponse(AonUtil.getDomainName(),
					sales.getDomain(), AonUtil.getRemoteUser(),
					f -> f.getDomainProperty().eq(sales.getDomain())
					.and(f.getSourceProperty().eq(com.esferalia.aon.occam.api.model.type.DataResponseSource.INGENET_SALES.value()))
					.and(f.getSourceIdProperty().eq(sales.getId())));
			return dr!=null?dr.getId():null;
		}
		return null;
	}
	
}
