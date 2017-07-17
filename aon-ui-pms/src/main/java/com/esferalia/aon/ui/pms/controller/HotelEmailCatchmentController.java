package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.PMS;
import com.esferalia.aon.occam.api.model.pms.HotelEmailCatchment;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.sql.ISQLConstants;

public class HotelEmailCatchmentController extends DataScrollerState implements ICollectionProvider, ISQLConstants{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Hotel[] hotels;
	private Integer year;

	public Hotel[] getHotels() {
		return hotels;
	}
	public void setHotels(Hotel[] hotels) {
		this.hotels = hotels;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	private List<HotelEmailCatchment> hotelEmailCatchmentList;	
	
	public List<HotelEmailCatchment> getHotelEmailCatchmentList() {
		return hotelEmailCatchmentList;
	}
	public void setHotelEmailCatchmentList(List<HotelEmailCatchment> hotelEmailCatchmentList) {
		this.hotelEmailCatchmentList = hotelEmailCatchmentList;
	}

	public String getHotelNames() {
		String hotelNames = "";
		for (Hotel hotel : getHotels()) {
			hotelNames += hotel.getWorkPlace().getDescription() + "; ";
		}
		return StringUtils.removeEnd(hotelNames, "; ");
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setHotels(null);
		setYear(2016);
	}

	public void onSearch(ActionEvent event) {
		try {
			buildHotelEmailCatchmentList();
		} catch (AonSQLException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getHotelEmailCatchmentList()));
	}

	private void buildHotelEmailCatchmentList() throws AonSQLException {
		setHotelEmailCatchmentList(new LinkedList<HotelEmailCatchment>());
		
		String domainName = AonUtil.getServerName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = AonUtil.getRemoteUser();
		Integer[] hotelArray = new Integer[getHotels().length];
		for (Integer i = 0; i< getHotels().length; i++) {
			hotelArray[i] = getHotels()[i].getId();
		}
		setHotelEmailCatchmentList(PMS.getHotelEmailCatchmentList(domainName, domainId, login, hotelArray, year));
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		return getHotelEmailCatchmentList();
	}
	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) {
		return getCollection();
	}
}
