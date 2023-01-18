package com.code.aon.ui.finance.util;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.IProgression;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.User;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingDAO;
import com.code.aon.finance.invoicing.engine.fee.CustomerFeeInvoicingEngine;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.finance.controller.FeeInvoicingController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.CertificateType;

import net.aonsolutions.aon.tbai.TbaiMain;

public class FeeInvoicingProcess implements ILongProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeeInvoicingProcess.class.getName());

	private FeeInvoicingController controller;
	private User user;
	
	public FeeInvoicingProcess(FeeInvoicingController controller) {
		this.controller = controller;
	}
	
	public FeeInvoicingProcess(FeeInvoicingController controller, User user) {
		this.controller = controller;
		this.user = user;
	}
	
	private IInvoicingEngine getEngine() throws InvoicingException {
		InvoicingEngineFactory.register(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY, new CustomerFeeInvoicingEngine());
		return InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.CUSTOMER_FEE_ENGINE_KEY);
	}	
	
	private void updateProgress(int current, int total) {
		if (total > 0) {
			int pro = (int) CommonUtil.round(((current * 100 / total) / 2)+50);
			controller.getProgressionState().setProgressionCurrentValue(new Long(pro));
		}
	}
	
	@Override
	public void execute() {
		controller.setInvoiceIds(null);
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());		
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			IInvoicingEngine engine = getEngine();
			engine.setInvoicingDAO(new CustomerFeeInvoicingDAO());
			engine.setInvoicingFeedBack(new InvoicingFeedBack());
			engine.setHibernateSession(HibernateUtil.getSession(sessionName));
			
			
			HibernateUtil.beginTransaction(sessionName);
			controller.updateSeries();
			engine.invoice(controller.getParams());
			HibernateUtil.commitTransaction(sessionName);

			Collection<Invoice> invoicedList = engine.getInvoicingDAO().getCollection();
			if (invoicedList.size() > 0) {
				for (Invoice invoice : invoicedList) {
					invoice = ticketbai(invoice);
				}
				if (controller.getParams().isInvoiceRecordable()) {
					HibernateUtil.beginTransaction(sessionName);
					int invoicesToRecord = invoicedList.size();
					int recordingInvoice = 0;
					AccountEntryInvoiceWriter accountWriter = new AccountEntryInvoiceWriter();
					for (Invoice invoice : invoicedList) {
						invoice = (Invoice)HibernateUtil.getSession(sessionName).merge(invoice);
						accountWriter.recordAndUpdateInvoice(invoice);
						recordingInvoice++;
						if (recordingInvoice % 20 == 0) {
							HibernateUtil.getSession(sessionName).flush();
							HibernateUtil.getSession(sessionName).clear();
						}
						updateProgress(recordingInvoice, invoicesToRecord);
					}
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				}
				Invoice[] array = invoicedList.toArray(new Invoice[invoicedList.size()]);
				Integer[] ids = new Integer[]{array[0].getId(), array[array.length-1].getId()};
				controller.setInvoiceIds(ids);
			}
			controller.getProgressionState().setProgressionCurrentValue(FINISH_VALUE);
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg =  "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg =  "Error invoicing fees. " + e.getMessage();
			controller.getProgressionState().setProgressionErrorMessage(msg);
			controller.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);			
		}
	}
	
	private Invoice ticketbai(Invoice inv) {
		String domainName = AonUtil.getDomainName();
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domainName, inv.getDomain(), user.getLogin());
		if(tbaiConfiguration.isActive()) {
			com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), user.getLogin(), inv.getId());

			Company company = AON.getCompanyForDomain(domainName, invoice.getDomain(), user.getLogin());
			tbaiConfiguration.setCertificate(AON.getCertificate(domainName, invoice.getDomain(), user.getLogin(), user.getId(), CertificateType.AEAT.name()));
			try {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionTBAI(company, invoice, tbaiConfiguration);
			} catch (Exception e ) {
				e.printStackTrace();
			}
		}
		
		// SII
		return inv;
	}

	private class InvoicingFeedBack implements IInvoicingFeedBack {

		private int currentRow;
		private int rowCount;

		public void addMessage(String message) {
		}

		public List<String> getMessages() {
			return null;
		}

		@Override
		public int getCurrentRow() {
			return currentRow;
		}
		@Override
		public void setCurrentRow(int currentRow) {
			this.currentRow = currentRow;
			if (rowCount > 0) {
				int pro = (int) CommonUtil.round(currentRow * 100 / rowCount);
				if (controller.getParams().isInvoiceRecordable()) {
					pro = pro / 2;
				}
				controller.getProgressionState().setProgressionCurrentValue(new Long(pro));
			}
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}
		
	}
	
}
