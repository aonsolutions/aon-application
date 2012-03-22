package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.product.Item;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.invoicing.AdvanceInvoiceTo;
import com.esferalia.aon.pms.invoicing.AdvanceInvoicingManager;
import com.esferalia.aon.ui.pms.event.AdvanceInvoiceSearchListener;



public class AdvanceInvoiceController extends BasicController{

	private double percent;
	private Item item;
	private int daysToPayment;
	private PayMethod payMethod;
	private Date inavoiceDate;
	private List<Integer> checked;

	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	public int getDaysToPayment() {
		return daysToPayment;
	}
	public void setDaysToPayment(int daysToPayment) {
		this.daysToPayment = daysToPayment;
	}
	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}
	public Date getInvoiceDate() {
		return inavoiceDate;
	}
	public void setInvoiceDate(Date inavoiceDate) {
		this.inavoiceDate = inavoiceDate;
	}
	public Date getPaymentDate() {
		return DateUtils.addDays(getInvoiceDate(), getDaysToPayment());
	}
	
	public List<Integer> getChecked() {
		if (checked == null) {
			checked = new ArrayList<Integer>();
		}
		return checked;
	}
	public void setChecked(List<Integer> checked) {
		this.checked = checked;
	}
	
	public void onCheck(ActionEvent event) {
		try {
			ProjectReservation pr = (ProjectReservation) getModel().getRowData();
			if (getChecked().contains(pr.getId())) {
				getChecked().remove(pr.getId());
			} else {
				getChecked().add(pr.getId());
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible marcar/desmarcar la fila.";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void checkAll(ActionEvent event) {
		setChecked(null);
		try {
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			for (ITransferObject to : list) {
				ProjectReservation pr = (ProjectReservation) to;
				getChecked().add(pr.getId());
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible marcar todas las filas.";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void checkNone(ActionEvent event) {
		setChecked(null);
	}
	
	public boolean isRowChecked() {
		try {
			ProjectReservation pr = (ProjectReservation) getModel().getRowData();
			return getChecked().contains(pr.getId());		
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible identificar la fila.");
			return false;
		}
	}
	public void setRowChecked(boolean check) {
		
	}
	
	public int getCheckCounter() {
		return getChecked().size();
	}
	public void initialize() throws ManagerBeanException {
		setChecked(null);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setPercent(0);
		setDaysToPayment(0);
		setInvoiceDate(new Date());
		setPayMethod(null);
	}
	
	
	public void onInvoice(ActionEvent event) {
		try {
			AdvanceInvoiceTo advanceInvoiceTo = new AdvanceInvoiceTo();
			advanceInvoiceTo.setIssueDate(getInvoiceDate());
			advanceInvoiceTo.setFinanceDate(getPaymentDate());
			advanceInvoiceTo.setItem(getItem());
			advanceInvoiceTo.setPercent(getPercent());
			advanceInvoiceTo.setPayMethod(getPayMethod());
			AdvanceInvoiceSearchListener search = (AdvanceInvoiceSearchListener) AonUtil.getRegisteredBean(IPmsConstants.ADVANCE_INVOICE_CONTROLLER_SEARCH_LISTENER);
			advanceInvoiceTo.setGuestReservation(search.isGuestReservationSearch());
			validate(advanceInvoiceTo);
			AdvanceInvoicingManager manager = new AdvanceInvoicingManager();
			int count = manager.invoice(advanceInvoiceTo,getChecked());
			onSearch(event);
			String msg = count == 1 ? "Se generó 1 factura." : "Se generaron " + count + " facturas."; 
			AonUtil.addInfoMessage( msg );
		} catch (ManagerBeanException e) {
			String msg = "Se produjo un error al generar las facturas. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void validate(AdvanceInvoiceTo advanceInvoiceTo) {
		if (getChecked().size() == 0) {
			String msg = "Debe marcar al menos una reserva para generar las facturas de anticipos.";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg);
		}
		if (!advanceInvoiceTo.isGuestReservation() && (getPercent() <= 0 || getPercent() > 100)) {
			String msg = "El porcentaje debe ser un valor numérico entre 0 y 100.";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg);
		}
		if (getDaysToPayment() < 0) {
			String msg = "La fecha de vencimiento no puede ser inferior a la fecha de factura.";
			AonUtil.addErrorMessage( msg );
			throw new AbortProcessingException(msg);
		}
	}
	
}