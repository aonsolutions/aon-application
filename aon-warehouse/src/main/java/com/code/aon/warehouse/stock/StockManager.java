package com.code.aon.warehouse.stock;

import java.io.Serializable;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.enumeration.PriceType;

public class StockManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String incomeStmt = " SELECT invDet.price "
	+ " FROM income_detail incDet "
	+ " INNER JOIN invoice_detail invDet ON incDet.id = invDet.source_id AND invDet.source = 4 "
	+ " INNER JOIN invoice inv ON invDet.invoice = inv.id "
	+ " WHERE  incDet.item = ? "
	+ " ORDER BY inv.issue_date DESC, inv.id DESC LIMIT 1";

	
	public double getPrice(Stock stock, PriceType priceType ) throws ManagerBeanException{
		if (priceType == PriceType.COST_PRICE) {
			return getCostPrice(stock);
		} else if (priceType == PriceType.PURCHASE_PRICE) {
			return getPurchasePrice(stock);
		} else if (priceType == PriceType.AVERAGE_PURCHASE_PRICE) {
			return getAveragePurchasePrice(stock);
		}
		return 0.0;
	}
	public double getPurchasePrice(Stock stock ) throws ManagerBeanException{
		if (stock == null) return 0.0;
		if (stock.getItem() == null) return 0.0;
		String sessionName = HibernateUtil.getSessionFactoryName(Stock.class.getName());
		Session session = null;
		session = HibernateUtil.getSession(sessionName);
		Query query = session.createQuery(incomeStmt);
		query.setInteger(0, stock.getItem().getId());
		double price = 0.0;
		price = (Double) query.uniqueResult();
		return price;
	}
	
	public double getAveragePurchasePrice(Stock stock ){
		// TODO Implementar
		return 0.0;
	}
	
	public double getCostPrice(Stock stock ){
		if (stock == null) return 0.0;
		if (stock.getItem() == null) return 0.0;
		return stock.getItem().getPurchasePrice();
	}

	public double getAmount(Stock stock, PriceType priceType) throws ManagerBeanException {
		double price = getPrice(stock,priceType);
		return CommonUtil.round(price * stock.getQuantity());
	}
}

