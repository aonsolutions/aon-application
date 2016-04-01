package com.esferalia.aon.ui.pms.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.pms.writer.WebpolGuestsWriter;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class WebpolExporterController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String APP_PMS_POLICE_COUNT = "PMS_POLICE_COUNT";
	
	
	private Hotel[] hotels;
	
	private Date fromDate;
	
	private Date toDate;
	
	private byte[] data;
	
	private int fileCount;
	
	private boolean newFile; 
	
	
	public Hotel[] getHotels() {
		return hotels;
	}
	public void setHotels(Hotel[] hotels) {
		this.hotels = hotels;
	}
	
	private List<Integer> getHotelIds() throws ManagerBeanException {
		List<Integer> hotelIds = new LinkedList<>();
		for (Hotel hotel : getHotels()) {
			hotelIds.add(hotel.getId());
		}
		return hotelIds;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	
	public void onInit(ActionEvent event) throws ManagerBeanException {
		this.getModel().setWrappedData(null);
		data = null;
		hotels = null;
		fromDate = new Date();
		toDate = new Date();
	}
	
	public void onGotoReservation(ActionEvent event) throws ManagerBeanException {
		ProjectReservationGuest guest = (ProjectReservationGuest) this.getModel().getRowData();
		ProjectReservationController controller = (ProjectReservationController) AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		controller.select(event, guest.getProjectReservation().getId());
		controller.setBackAction(IPmsConstants.WEBPOL_EXPORTER_LIST_NAME);
	}
	
	public void onSearch(ActionEvent event) {
		try {
			this.data = null;
			this.newFile = true;
			this.fileCount = 0;
			this.clearCriteria();
			this.setOrderList(null);
			this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_HOTEL_ID), getHotelIds());
			this.getCriteria().addNotNullExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT));
			this.getCriteria().addNotEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT), "");
			Date insideDateTo = (getToDate() != null) ? getToDate() : getFromDate();
			this.getCriteria().addLessThanOrEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_START_DATE), insideDateTo);
			this.getCriteria().addGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_END_DATE), fromDate);
			this.getCriteria().addOrder(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_HOTEL_ID));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se ha podido obtener la lista de huespedes.");
		}
		super.onSearch(event);
	}
	
	public void onCreateDisk( ActionEvent event ) throws ManagerBeanException {
		try {
			WebpolGuestsWriter writer = new WebpolGuestsWriter();
			FileOutput output = writer.createFile(getHotels(), getManagerBean().getList(getCriteria()), Calendar.getInstance().getTime());
			if (output != null && output.getContent() != null) {
				setData(output.getContent());
			}
		} catch (FileNotFoundException e) {
			AonUtil.addErrorMessage("Error generando el fichero");
			AonUtil.addErrorMessage(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			AonUtil.addErrorMessage("Error generando el fichero");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onDownloadFile( ActionEvent event ) {
		if(getData()!=null){
			HttpServletResponse response = null;
			OutputStream out = null;
			if(this.newFile){
				fileCount = obtainFileCount();
				updateFileCount(++fileCount);
				this.newFile = false;
			}
			try {
				String name = obtainIssueEntityCode() + "." + StringUtils.leftPad(String.valueOf(fileCount), 3, "0");
				int size = data.length;
				response = DownloadUtil.getResponse();
				out = DownloadUtil.initDownload(response, name, null, size);
				InputStream fileIn = new BufferedInputStream( new ByteArrayInputStream(data) );
				IOUtils.copy( fileIn, out );
				IOUtils.closeQuietly(fileIn);
			} catch (Throwable e) {
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			} finally {
				DownloadUtil.finishDownload(response, out);
			}
		}
	}

	private String obtainIssueEntityCode() {
		ApplicationParameter ap = AppParamUtil.getParameter(WebpolGuestsWriter.APP_PMS_POLICE_CODE);
		String value = ap==null?"":ap.getValue();
		value = value.length()>10?value.substring(0, 10):value;
		return value;
	}

	private int obtainFileCount() {
		ApplicationParameter ap = AppParamUtil.getParameter(APP_PMS_POLICE_COUNT);
		int value = ap==null?1:Integer.parseInt(ap.getValue());
		value = value==999?1:value;
		return value;
	}
	
	private void updateFileCount(int fileCount) {
		ApplicationParameter ap = AppParamUtil.getParameter(APP_PMS_POLICE_COUNT);
		if(ap==null){
			ap = new ApplicationParameter();
			ap.setName(APP_PMS_POLICE_COUNT);
		}
		ap.setValue(String.valueOf(fileCount));
		AppParamUtil.insertParameter(ap);
	}
	

}