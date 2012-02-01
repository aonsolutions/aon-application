package com.esferalia.aon.pms.reservation;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;

public class ReservationInvoiceTo implements IReservationConstants {

	private Date issueDate;
	private Hotel hotel;
	private String series;
	private int number;
	private ProjectReservationRoomDetail room;
	private ProjectReservationGuest guest;
	private Registry registry;
	private IAddress address;
	private String comments;
	private Date serviceFromDate;
	private Date serviceToDate;
	private List<InvoiceDetail> services;
	private List<Finance> finances;

	public ReservationInvoiceTo() {
		setIssueDate(new Date());
		setServiceFromDate(new Date());
		setServiceToDate(new Date());

		setRegistry(new Registry());
		setAddress(new InvoiceAddress());
		setComments(null);
		
		setServices(new LinkedList<InvoiceDetail>());
		setFinances(new LinkedList<Finance>());
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public ProjectReservationRoomDetail getRoom() {
		return room;
	}

	public void setRoom(ProjectReservationRoomDetail room) {
		this.room = room;
	}

	public ProjectReservationGuest getGuest() {
		return guest;
	}

	public void setGuest(ProjectReservationGuest guest) {
		this.guest = guest;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public IAddress getAddress() {
		return address;
	}

	public void setAddress(IAddress address) {
		this.address = address;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getServiceFromDate() {
		return serviceFromDate;
	}

	public void setServiceFromDate(Date serviceFromDate) {
		this.serviceFromDate = serviceFromDate;
	}

	public Date getServiceToDate() {
		return serviceToDate;
	}

	public void setServiceToDate(Date serviceToDate) {
		this.serviceToDate = serviceToDate;
	}

	public List<InvoiceDetail> getServices() {
		return services;
	}

	public void setServices(List<InvoiceDetail> services) {
		this.services = services;
	}

	public List<Finance> getFinances() {
		return finances;
	}

	public void setFinances(List<Finance> finances) {
		this.finances = finances;
	}

	public InvoiceDetail getLastService() {
		return getServices().get(getServices().size()-1);
	}

	public Finance getLastFinance() {
		return getFinances().get(getFinances().size()-1);
	}

}
