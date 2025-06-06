package com.code.aon.ui.sales.util;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalesImportManager {

	public Sales copySales(Sales source, String series, int number, Customer customer, Date date) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Integer sourceId = source.getId();
		Sales sales = source;
		sales.setId(null);
		sales.setSeries(series);
		sales.setNumber(number);
		sales.setCustomer(customer);
		sales.setIssueDate(date);
		sales.setDeliveryDate(date);
		sales.setStatus(SalesStatus.PENDING);
		sales.setScope(null);
		sales.setLines(null);
		sales = (Sales) salesBean.insert(sales);

		importSales(sourceId, sales);
		return sales;
	}

	private void importSales(Integer sourceId, Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sourceId);
		List<ITransferObject> salesDetailList = salesDetailBean.getList(criteria);
		for (ITransferObject to : salesDetailList) {
			SalesDetail sourceDetail = (SalesDetail)to;
			boolean lastDetail = sourceDetail.equals(salesDetailList.get(salesDetailList.size()-1));
			SalesDetail salesDetail = sourceDetail;
			salesDetail.setId(null);
			salesDetail.setSales(sales);
			salesDetail.setItem(getBaseItem(salesDetail.getItem()));
			salesDetail.setDescription(getDescription(salesDetail.getItem()));
			salesDetail.setDeliveryDate(sales.getDeliveryDate());
			salesDetail.setDelivered(0.0);
			salesDetailBean.insert(salesDetail);
			if (lastDetail) {
				sales = salesDetail.getSales();
			}
		}
	}
	
	private Item getBaseItem(Item item) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
		criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		List<ITransferObject> toList = itemBean.getList(criteria);
		return !toList.isEmpty() ? (Item) toList.getFirst() : item;	
	}
	
	private String getDescription(Item item) throws ManagerBeanException {
		return item.getProduct().getFullName(); 
	}

}
