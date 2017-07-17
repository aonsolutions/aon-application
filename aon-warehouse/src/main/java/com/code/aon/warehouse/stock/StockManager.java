package com.code.aon.warehouse.stock;

import java.io.Serializable;

import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import com.code.aon.AonVersion;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.product.Item;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.enumeration.PriceType;

public class StockManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockManager.class.getName());
	
	private String domainName;
	
	private Settings settings;
	
	public StockManager(String domainName, Settings settings) {
		this.domainName = domainName;
		this.settings = settings;
	}

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
		if (stock == null) {
			return 0.0;
		}
		if (stock.getItem() == null) {
			return 0.0;
		}
		return getPurchasePrice(stock.getItem());
	}
	
	public double getPurchasePrice( Item item ) {
		double price = 0.0;
		Connection connection = null; 
		try {
			connection = DatabaseUtil.getConnection(domainName);
			DSLContext ctx = DSL.using(connection, settings);
			Record1<Double> result = ctx.select(INVOICE_DETAIL.PRICE)
					.from(INCOME_DETAIL)
					.join(INVOICE_DETAIL).on(INVOICE_DETAIL.SOURCE_ID.equal(INCOME_DETAIL.ID).and(INVOICE_DETAIL.SOURCE.equal((byte)4)))
					.join(INVOICE).on(INVOICE.ID.equal(INVOICE_DETAIL.INVOICE))
					.where(INVOICE_DETAIL.SOURCE.equal((byte)4), INCOME_DETAIL.ITEM.equal(item.getId()))
					.orderBy(INVOICE.ISSUE_DATE.desc(), INVOICE.ID.desc())
					.limit(1)
					.fetchOne();
			if ( result != null ) {
				price = result.getValue(INCOME_DETAIL.PRICE);
			}
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
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

