package com.code.aon.ui.finance.util;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.IProgression;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.finance.invoicing.engine.InvoicingEngineFactory;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingDAO;
import com.code.aon.finance.invoicing.engine.delivery.DeliveryInvoicingEngine;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.finance.controller.DeliveryInvoicingController;

public class DeliveryInvoicingProcess implements ILongProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryInvoicingProcess.class.getName());

	private DeliveryInvoicingController controller;
	
	public DeliveryInvoicingProcess(DeliveryInvoicingController controller) {
		this.controller = controller;
	}
	
	private IInvoicingEngine getEngine() throws InvoicingException {
		InvoicingEngineFactory.register(InvoicingEngineFactory.DELIVERY_ENGINE_KEY, new DeliveryInvoicingEngine());
		return InvoicingEngineFactory.getInvoicingEngine(InvoicingEngineFactory.DELIVERY_ENGINE_KEY);
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
			engine.setInvoicingDAO(new DeliveryInvoicingDAO());
			engine.setInvoicingFeedBack(new InvoicingFeedBack());
			engine.setHibernateSession(HibernateUtil.getSession(sessionName));
			
			HibernateUtil.beginTransaction(sessionName);
			controller.updateSeries();
			engine.invoice(controller.getParams());
			HibernateUtil.commitTransaction(sessionName);

			Collection<Invoice> invoicedList = engine.getInvoicingDAO().getCollection();
			if (invoicedList.size() > 0) {
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
			e.printStackTrace();
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg =  "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg =  "Error invoicing deliveries. " + e.getMessage();
			controller.getProgressionState().setProgressionErrorMessage(msg);
			controller.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);			
		}
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
