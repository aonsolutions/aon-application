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
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
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
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class WebpolExporterController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String APP_PMS_POLICE_COUNT = "PMS_POLICE_COUNT";
	
	
	private Hotel[] hotels;
	
	private Date fromDate;
	
	private Date toDate;
	
	private byte[] data;
	
	private int fileCount;
	
	private String issueEntityCode;
	
	private Hotel generationHotel;
	
	
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

	public int getFileCount() {
		return fileCount;
	}
	
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	private List<Hotel> getGenerationHotels(List<ITransferObject> list) {
		List<Hotel> hotels = list.stream()
				.map(o -> (ProjectReservationGuest) o)
				.map(ProjectReservationGuest::getProjectReservation)
				.map(ProjectReservation::getHotel).distinct().collect(Collectors.toList());
		return hotels;
	}
	
	private boolean isMultipleGenerationHotel(List<ITransferObject> list) {
		return getGenerationHotels(list).size() > 1;
	}

	
	public void onInit(ActionEvent event) throws ManagerBeanException {
		this.getModel().setWrappedData(null);
		data = null;
		hotels = null;
		fromDate = new Date();
		toDate = new Date();
		fileCount = -1;
		issueEntityCode = null;
		generationHotel = null;
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
			this.fileCount = 0;
			this.clearCriteria();
			this.setOrderList(null);
			this.getCriteria().addInExpression("ProjectReservationGuest.projectReservation.hotelReservation.id", getHotelIds());
			this.getCriteria().addNotNullExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT));
			this.getCriteria().addNotEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_DOCUMENT), "");
			this.getCriteria().addNotEqualExpression("ProjectReservationGuest.projectReservation.status", ReservationStatus.CANCELLED);
			this.getCriteria().addEqualExpression("ProjectReservationGuest.projectReservation.checkStatus", ReservationCheckStatus.CHECK_IN);
			this.getCriteria().addBetweenExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_START_DATE), getFromDate(), getToDate());
			this.getCriteria().addOrder(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_HOTEL_ID));
			super.onSearch(event);
			loadParams();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se ha podido obtener la lista de huespedes.");
		}
	}
	
	private void loadParams() throws ManagerBeanException {
		generationHotel = null;

		List<ITransferObject> list = getManagerBean().getList(getCriteria());
		if(!isMultipleGenerationHotel(list)){
			List<Hotel> hotels = getGenerationHotels(list);
			generationHotel = hotels!=null && hotels.size()==1?hotels.get(0):null;
		}
		
		issueEntityCode = obtainIssueEntityCode(generationHotel);
		fileCount = obtainFileCount(generationHotel);
		fileCount++;
	}
		
	public void onCreateDisk( ActionEvent event ) throws ManagerBeanException {
		updateFileCount(fileCount, generationHotel);
		try {
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			WebpolGuestsWriter writer = new WebpolGuestsWriter();
			FileOutput output = writer.createFile(getHotels(), list, Calendar.getInstance().getTime());
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
			try {
				String name = issueEntityCode + "." + StringUtils.leftPad(String.valueOf(fileCount), 3, "0");
				int size = data.length;
				response = DownloadUtil.getResponse();
				response.setCharacterEncoding(WebpolGuestsWriter.CHARSET_ENCODING);
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

	private String obtainIssueEntityCode(Hotel hotel) {
		if(hotel!=null){
			String value = null;
			if(!StringUtils.isBlank(hotel.getPoliceCode())){
				value = hotel.getPoliceCode();
			} else {
				value = "HOTEL_CODE_NO_DEF";
			}
			return value;
		} else {
			ApplicationParameter ap = AppParamUtil.getParameter(WebpolGuestsWriter.APP_PMS_POLICE_CODE);
			String value = null;
			if(ap!=null && !StringUtils.isBlank(ap.getValue())){
				value = ap.getValue();
				value = value.length()>10?value.substring(0, 10):value;
			} else {
				value = "GROUP_CODE_NO_DEF";
			}
			return value;
		}
	}

	private int obtainFileCount(Hotel hotel) throws ManagerBeanException {
		if(hotel!=null){
			return hotel.getPoliceCounter();
		} else {
			ApplicationParameter ap = AppParamUtil.getParameter(APP_PMS_POLICE_COUNT);
			int value = ap==null?1:Integer.parseInt(ap.getValue());
			value = value==999?1:value;
			return value;
		}
	}
	
	private void updateFileCount(int fileCount, Hotel hotel) throws ManagerBeanException {
		if(hotel!=null){
			hotel.setPoliceCounter(fileCount);
			IManagerBean bean = BeanManager.getManagerBean(Hotel.class);
			bean.update(hotel);
		} else {
			ApplicationParameter ap = AppParamUtil.getParameter(APP_PMS_POLICE_COUNT);
			if(ap==null){
				ap = new ApplicationParameter();
				ap.setName(APP_PMS_POLICE_COUNT);
			}
			ap.setValue(String.valueOf(fileCount));
			AppParamUtil.insertParameter(ap);
		}
	}
	

}