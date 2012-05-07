package com.esferalia.aon.pms.invoicing;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.Registry;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.reservation.IReservationConstants;

public class ReservationInvoiceTo implements IReservationConstants {

	private Date issueDate;
	private Hotel hotel;
	private String series;
	private int number;
	private ProjectReservationRoomDetail room;
	private ProjectReservationGuest guest;
	private boolean directCustomer;
	private Registry registry;
	private IAddress address;
	private String comments;
	private List<HotelService> services;
	private List<Finance> finances;

	public ReservationInvoiceTo() {
		setIssueDate(new Date());
		setRegistry(new Registry());
		setAddress(new InvoiceAddress());
		setComments(null);
		setServices(new LinkedList<HotelService>());
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

	public boolean isDirectCustomer() {
		return directCustomer;
	}

	public void setDirectCustomer(boolean directCustomer) {
		this.directCustomer = directCustomer;
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

	public List<HotelService> getServices() {
		return services;
	}

	public void setServices(List<HotelService> services) {
		this.services = services;
	}

	public List<Finance> getFinances() {
		return finances;
	}

	public void setFinances(List<Finance> finances) {
		this.finances = finances;
	}

	public HotelService getNewService() {
		return new HotelService();
	}

	public int getServicesCount() {
		return getServices().size();
	}

	public HotelService getFirstService() {
		return getServices().get(0);
	}

	public HotelService getLastService() {
		return getServices().get(getServices().size()-1);
	}

	public int getFinancesCount() {
		return getFinances().size();
	}

	public Finance getFirstFinance() {
		return getFinances().get(0);
	}

	public Finance getLastFinance() {
		return getFinances().get(getFinances().size()-1);
	}

	public class HotelService implements ICalculable {
		private Date fromDate;
		private Date toDate;
		private Item item;
		private double quantity;
		private double price;
		private double taxableBase;

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

		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}

		public double getQuantity() {
			return quantity;
		}
		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}

		public double getTaxableBase() {
			return taxableBase;
		}
		public void setTaxableBase(double taxableBase) {
			this.taxableBase = taxableBase;
		}

		public double getTotal() {
			return CommonUtil.round(taxableBase * (1 + (item.getProduct().getVat().getPercentage() / 100)));
		}


		@Override
		public DiscountExpression getDiscountExpression() {
			return new DiscountExpression("0.0");
		}

		@Override
		public double getTaxes() throws ManagerBeanException {
			return 0;
		}

	}

}
