package com.code.aon.warehouse.stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.enumeration.PriceType;

public class StockManager {
	private String incomeStmt = " SELECT invDet.price "
	+ " FROM income_detail incDet "
	+ " INNER JOIN invoice_detail invDet ON incDet.id = invDet.source_id AND invDet.source = 4 "
	+ " INNER JOIN invoice inv ON invDet.invoice = inv.id "
	+ " WHERE  incDet.item = ? "
	+ " ORDER BY inv.issue_date DESC, inv.id DESC ";

	
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
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(incomeStmt,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, stock.getItem().getId());
			rs = ps.executeQuery();
			double price = 0.0;
			if (rs.next()) {
				price = rs.getDouble(1);
			}
			return price;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
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

