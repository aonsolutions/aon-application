package com.esferalia.aon.ui.pms.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.person.Person;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.PersonController;
import com.code.aon.ui.registry.controller.event.PersonFormListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.pms.writer.WebpolGuestsWriter;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class WebpolExporterController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel[] hotels;
	
	private Date fromDate;
	
	private Date toDate;
	
	private byte[] data;
	
	private int fileCount;
	
	

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
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	
	public void onInit(ActionEvent event) throws ManagerBeanException {
		this.getModel().setWrappedData(null);
		data = null;
		hotels = null;
		fromDate = new Date();
		fileCount = 0;
		toDate = DateUtils.addDays(new Date(), 1);
	}
	
	public void onGotoPerson(ActionEvent event) throws ManagerBeanException {
		ProjectReservationGuest guest = (ProjectReservationGuest) this.getModel().getRowData();
		PersonController personController = (PersonController) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_CONTROLLER_NAME);
		if(guest.getPerson().getId()==null){
			PersonFormListener personForm = (PersonFormListener) AonUtil.getRegisteredBean(IRegistryConstants.PERSON_FORM_CONTROLLER_NAME);
			personController.onReset(event);
			Person person = (Person) personController.getTo();
			person.setName(guest.getName());
			person.setFirstSurname(guest.getSurname());
			person.setSecondSurname(guest.getSurname2());
			person.getRegistry().setDocument(guest.getDocument());
			person.getRegistry().setDocumentType(guest.getDocumentType());
			person.getRegistry().setDocumentCountry(guest.getDocumentCountry());
			person.setBirthDate(guest.getBirthDate());
			personForm.getEmail().setValue(guest.getEmail());
			personForm.getPhone().setValue(guest.getPhone());
			personForm.getMainAddress().setAddress(guest.getAddress());
			personForm.getMainAddress().setAddress2(guest.getAddress2());
			personForm.getMainAddress().setNumber(guest.getNumber());
			personForm.getMainAddress().setZip(guest.getZip());
			personForm.getMainAddress().setCity(guest.getCity());
			personForm.getMainAddress().setProvince(guest.getProvince());
			person.getRegistry().setNationality(guest.getCountry());
		} else {
			personController.select(event, guest.getPerson().getId());
		}
		personController.setBackAction(this.getBeanName()+"_list");
	}
	
	public void onSearch(ActionEvent event) {
		try {
			this.data = null;
			this.clearCriteria();
			this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_HOTEL_ID), getHotelIds());
			this.getCriteria().addGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_START_DATE), fromDate);
			this.getCriteria().addLessThanOrEqualExpression(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_END_DATE), toDate);
			this.getCriteria().addOrder(this.getFieldName(IEntityAlias.PROJECT_RESERVATION_GUEST_PROJECT_RESERVATION_HOTEL_ID));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se ha podido obtener la lista de huespedes.");
		}
		super.onSearch(event);
	}
	
	public void onCreateDisk( ActionEvent event ) {
		try {
			WebpolGuestsWriter writer = new WebpolGuestsWriter();
			FileOutput output = writer.createFile(getHotels(), getWrappedList(), Calendar.getInstance().getTime());
			if (output != null && output.getContent() != null) {
				setData(output.getContent());
				fileCount++;
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
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("ddMMHHmm");
				// TODO CodigoEntidadEmisora
				String name = formatter.format(date) + "." + StringUtils.leftPad(String.valueOf(getFileCount()), 3, "0");
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
	
	
	
	/*
	 * 
	 */
	private ArrayList<Integer> checks = new ArrayList<Integer>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		ProjectReservation to = (ProjectReservation)model.getRowData();
		return checks.contains(to.getId());
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			ProjectReservation to = (ProjectReservation)model.getRowData();
			if (!checks.contains(to.getId())) {
				checks.add(to.getId());
			}
		} else {
			ProjectReservation to = (ProjectReservation)model.getRowData();
			if (checks.contains(to.getId())) {
				checks.remove(to.getId());
			}
		}
	}

	public ArrayList<Integer> getCheckedReservations() {
		return checks;
	}

	public void clearCheckedReservations() {
		checks = new ArrayList<Integer>();
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			ProjectReservation reservation = (ProjectReservation)ito;
			if (!checks.contains(reservation.getId())) {
				checks.add(reservation.getId());
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedReservations();
	}

	public int getCheckedCount() {
		return getCheckedReservations().size();
	}

}