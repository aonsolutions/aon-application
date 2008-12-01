package com.code.aon.ui.sales.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.report.OutputFormat;
import com.code.aon.sales.Sales;
import com.code.aon.sales.Seller;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the sales maintenance.
 */
public class SalesController extends BasicController {

	private List<SelectItem> addresses;

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = SeriesNumberUtil.obtainSeries((String)event.getNewValue());
		if (this.getTo() != null) {
			((Sales)this.getTo()).setNumber(SeriesNumberUtil.obtainNumber((String)event.getNewValue(), StringUtils.capitalize(this.getBeanName())));
			((Sales)this.getTo()).setSecurityLevel((series!=null)?series.getSecurityLevel():null);
			((Sales)this.getTo()).setWorkPlace((series!=null)?series.getWorkPlace():null);
		}
	}

	public boolean isPending(){
		Sales sales = (Sales)this.getTo();
		if (sales.getStatus() != null) {
			return sales.getStatus().equals(SalesStatus.PENDING);
		}
		return false;
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			((Sales)this.getTo()).setCustomer(customer);
			loadAddresses(customer.getId());
		} else {
			setAddresses(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getAddress() + " " + address.getAddress2() + " " + address.getAddress3();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address.getId(), addressLabel);
				addresses.add(item);
			}
		}
		this.addresses = addresses;
	}

    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if (addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	public void sellerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Seller seller = (Seller)event.getNewValue();
			((Sales)this.getTo()).setSeller(seller);
		}
	}

	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("sales");
        manager.setOutputFormat(OutputFormat.PDF);
    }

}
