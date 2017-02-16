package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_CUSTOMER_REQUIRED_ERROR;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.RectificationInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RectifierInvoiceController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showRectificationWindow;
	private String rectificationSeries;
	private int rectificationNumber;
	private Date rectificationDate;
	private String rectificationCause;
	private Customer customer;
	private Date fromDate;
	private Date toDate;
	private List<InvoiceWrapper> invoiceList;
	private DataModel model;
	private IPriceStrategy priceStrategy;
	
	public boolean isShowRectificationWindow() {
		return showRectificationWindow;
	}
	public void setShowRectificationWindow(boolean value) {
		this.showRectificationWindow = value;
	}

	public String getRectificationSeries() {
		return rectificationSeries;
	}
	public void setRectificationSeries(String rectificationSeries) {
		this.rectificationSeries = rectificationSeries;
	}

	public int getRectificationNumber() {
		return rectificationNumber;
	}
	public void setRectificationNumber(int rectificationNumber) {
		this.rectificationNumber = rectificationNumber;
	}

	public Date getRectificationDate() {
		return rectificationDate;
	}
	public void setRectificationDate(Date rectificationDate) {
		this.rectificationDate = rectificationDate;
	}
	
	public String getRectificationCause() {
		return rectificationCause;
	}
	public void setRectificationCause(String rectificationCause) {
		this.rectificationCause = rectificationCause;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

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
	
	public List<InvoiceWrapper> getInvoiceList() {
		return invoiceList;
	}
	public void setInvoiceList(List<InvoiceWrapper> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public void onReset(ActionEvent event) {
		try {
			setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		} catch (ManagerBeanException e) {
		}
		setFromDate(null);
		setToDate(null);
		setInvoiceList(null);
		setModel(null);
	}

	public void onSearch(ActionEvent event) {
		if (customer == null || customer.getId() == null) {
			String msg = AonUtil.addErrorMessageFromBundle(FINANCE_CUSTOMER_REQUIRED_ERROR);
			throw new AbortProcessingException(msg);
		}
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		Connection conn = null; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.id,sum(f.amount+f.expenses)");
			stmt.append(" FROM finance f");
			stmt.append(" INNER JOIN invoice i ON (f.invoice = i.id)");
			stmt.append(" WHERE f.payment = 0");
			stmt.append(" AND (f.status = 0 or  f.status = 2)");
			stmt.append(" AND f.invoice is not null");
			stmt.append(" AND 6 NOT IN (SELECT source FROM invoice_detail d where d.invoice = i.id)");
			stmt.append(" AND f.registry = ?");
			if (getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");	
			}
			if (getToDate() != null) {
				stmt.append(" AND i.issue_date <= ?");
			}
			stmt.append(" GROUP BY i.id ORDER BY i.id");
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, getCustomer().getId());
			if (getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date(getFromDate().getTime()));
			}
			if (getToDate() != null) {
				ps.setDate(++i, new java.sql.Date(getToDate().getTime()));	
			}
			rs = ps.executeQuery();
			setInvoiceList(new LinkedList<InvoiceWrapper>());
			IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
			while (rs.next()) {
				int id = rs.getInt(1);
				double pending = rs.getDouble(2);
				Invoice invoice = (Invoice) bean.get(id);
				double invoiceTotal = invoice.getTotal() != 0.0? invoice.getTotal() : getPriceStrategy().getTotalPrice(invoice, invoice); 
				InvoiceWrapper iw = new InvoiceWrapper(invoice, invoiceTotal, pending);
				getInvoiceList().add(iw);
			}
			setModel(new SerializableListDataModel(getInvoiceList()));

			HibernateUtil.commitTransaction(sessionName);
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
			}
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
			
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	public void onPendingChanged(ActionEvent event) {
		InvoiceWrapper iw = (InvoiceWrapper)getModel().getRowData();
		iw.calculatePercent();
	}
	public void onPercentChanged(ActionEvent event) {
		InvoiceWrapper iw = (InvoiceWrapper)getModel().getRowData();
		iw.calculatePending();
	}

	public void onRectificationShow(ActionEvent event) throws ManagerBeanException {
		boolean anySelected = false;
		for (InvoiceWrapper iw : getInvoiceList()) {
			if (iw.isEnabled()) {
				anySelected = true;		
				break;
			}
		}
		if (!anySelected) {
			setShowRectificationWindow(false);
			throw new AbortProcessingException("Debe seleccionar alguna factura.");
		}
		setShowRectificationWindow(true);
		setRectificationSeries(SeriesUtil.getFirstRectificationSeries());
		setRectificationNumber(obtainMaxRectificationNumber(getRectificationSeries()));
		setRectificationDate(new Date());
		setRectificationCause(null);
	}

	public void onRectificationSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setRectificationNumber(obtainMaxRectificationNumber((String)event.getNewValue()));
	}

	private int obtainMaxRectificationNumber(String seriesId) {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}
	
	public void onRectify(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);

			Invoice rectifier = null;
			int firstRectifierId = 0;
			int rectificationNumber = getRectificationNumber();
			for (InvoiceWrapper iw : getInvoiceList()) {
				if (iw.isEnabled()) {
					Invoice invoice = iw.getInvoice(); 
					RectificationInvoicingManager rectificationManager = new RectificationInvoicingManager();
					rectifier = rectificationManager.specialRectifyInvoice(invoice, getRectificationSeries(), rectificationNumber++, 
																			getRectificationDate(),	getRectificationCause(), iw.getPercent());
					firstRectifierId = (firstRectifierId == 0) ? rectifier.getId() : firstRectifierId;
				}
			}
			HibernateUtil.commitTransaction(sessionName);

			IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(invoiceController.getFieldName(IEntityAlias.INVOICE_ID), firstRectifierId, rectifier.getId());
			invoiceController.onEditSearch(event);
			invoiceController.setCriteria(criteria);
			invoiceController.onSearch(event);
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
			}
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
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

			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public String navigationRedirect() {
		return SALE_INVOICE_LIST_NAME;
	}

	public static class InvoiceWrapper implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Invoice invoice;
		private boolean enabled;
		private double invoiceTotal;
		private double invoicePending;
		private double pending;
		private double percent;
		
		public InvoiceWrapper(Invoice invoice, double invoiceTotal, double invoicePending) {
			this.invoice = invoice;
			setInvoiceTotal(invoiceTotal);
			setInvoicePending(invoicePending);
			setPending(invoicePending);
			setEnabled(false);
			calculatePercent();
		}

		public Invoice getInvoice() {
			return invoice;
		}
		public void setInvoice(Invoice invoice) {
			this.invoice = invoice;
		}

		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public double getInvoiceTotal() {
			return invoiceTotal;
		}
		public void setInvoiceTotal(double invoiceTotal) {
			this.invoiceTotal = invoiceTotal;
		}

		public double getInvoicePending() {
			return invoicePending;
		}
		public void setInvoicePending(double invoicePending) {
			this.invoicePending = invoicePending;
		}

		public double getPending() {
			return pending;
		}
		public void setPending(double pending) {
			this.pending = pending;
		}

		public double getPercent() {
			return this.percent;
		}
		public void setPercent(double percent) {
			this.percent = percent;
		}

		private void calculatePercent() {
			setPercent(CommonUtil.round((pending * 100 / invoiceTotal)));			
		}
		private void calculatePending() {
			setPending(CommonUtil.round(invoiceTotal - (invoiceTotal * percent / 100))); 
		}
	}

}
