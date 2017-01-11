package com.code.aon.ui.finance.controller;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;


public class FeePrintController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<ITransferObject> orderedList;
	
	public List<ITransferObject> getOrderedList() {
		return orderedList;
	}

	public void setOrderedList(List<ITransferObject> orderedList) {
		this.orderedList = orderedList;
	}
	
	public void onOrderCustomerFeeByDate(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_BILLING_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public String getOnOrderCustomerFeeByDateLinkExcel() throws ManagerBeanException, ParseException {
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
		SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
		Integer i = getCriteria().getExpression().toString().indexOf("between ");
		Integer j  = getCriteria().getExpression().toString().indexOf(" and");
		String dateStr = getCriteria().getExpression().toString().substring(i+8, j);
		Date date = format.parse(dateStr);
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		String str = "domain="+ domain.getName() + "&login="+AonUtil.getRemoteUser() + "&date=" + format2.format(date);
		String base = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		return "/download_fee_projection_excel/"+ base;
	}
	
	public String getOnOrderCustomerFeeByDateLinkPdf() throws ManagerBeanException, ParseException {
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
		SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
		Integer i = getCriteria().getExpression().toString().indexOf("between ");
		Integer j  = getCriteria().getExpression().toString().indexOf(" and");
		String dateStr = getCriteria().getExpression().toString().substring(i+8, j);
		Date date = format.parse(dateStr);
		Domain domain = AON.getDomain(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), AonUtil.getRemoteUser());
		String str = "domain="+ domain.getName() + "&login="+AonUtil.getRemoteUser() + "&date=" + format2.format(date);
		String base = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		return "/download_fee_projection_pdf/"+ base;
	}
	
	public void onOrderCustomerFeeByItem(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderCustomerFeeByCustomer(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}

}