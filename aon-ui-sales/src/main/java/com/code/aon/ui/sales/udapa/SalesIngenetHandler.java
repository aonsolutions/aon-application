package com.code.aon.ui.sales.udapa;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesIngenetHandler.class);
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private final static String INGENET_STATUS = "STATUS";
	private final static String INGENET_PENDING = "PENDING";
	private final static String INGENET_RETRIEVED = "RETRIEVED";
	private final static String INGENET_CLOSED = "CLOSED";
	
	private SalesController controller;
	private boolean showIngenetWindow;
	
	private Sales sales;
	private Date responseDate;
	
	
	
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
	
	public void onEnableForIngenet(ActionEvent event){
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
		detail.setDomain(sales.getDomain());
		detail.setDataResponse(response.getId());
		detail.setDataVariable(INGENET_STATUS);
		detail.setDataValue(INGENET_PENDING);
		detail = AON.insertDataResponseDetail(AonUtil.getDomainName(),
				sales.getDomain(), AonUtil.getRemoteUser(), detail);
		
		// TODO create data_attach if needed
//		DataAttach attach = new DataAttach();
//		attach.
		
		
		// TODO process the sales for Ingenet
		controller.onClose(event);
	}
	
	public boolean isEnabledForIngenet() {
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
			return INGENET_PENDING.equals(status);
		}
		return false;
	}
	
	public boolean isRetrievedByIngenet() {
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
			return INGENET_RETRIEVED.equals(status);
		}
		return false;
	}
	
	public boolean isClosedByIngenet() {
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
			return INGENET_CLOSED.equals(status);
		}
		return false;
	}
	
	private Integer getResponseId() {
		if(sales!=null && sales.getId()!=null) {
			DataResponse dr = AON.getDataResponse(AonUtil.getDomainName(),
					sales.getDomain(), AonUtil.getRemoteUser(),
					com.esferalia.aon.occam.api.model.type.DataResponseSource.INGENET_SALES,
					f -> f.getSourceIdProperty().eq(sales.getId()));
			return dr!=null?dr.getId():null;
		}
		return null;
	}
	
}
