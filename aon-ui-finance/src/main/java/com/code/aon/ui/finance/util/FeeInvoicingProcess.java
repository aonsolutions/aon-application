package com.code.aon.ui.finance.util;

import static com.code.aon.common.IProgression.FINISH_VALUE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.IProgression;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
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
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.sii.SIIManager;
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
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		Occam occam = new Occam().setDomainName(domainName).setDomain(domainId).setUser(user.getLogin());
		InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(occam);
		controller.getParams().setInvoiceCommunicationConfiguration(config);
		
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
			
			
			// Grabacion de las facturas proforma
			HibernateUtil.beginTransaction(sessionName);
			controller.updateSeries();
			engine.invoice(controller.getParams());
			HibernateUtil.commitTransaction(sessionName);
			
			// Comunicación de las facturas proforma
			Collection<Invoice> invoicedList = engine.getInvoicingDAO().getCollection();
			if (invoicedList != null && !invoicedList.isEmpty()) {
				communication(sessionName, invoicedList);
			}
			
			// Contabilización de las facturas si procede
			if (invoicedList != null && !invoicedList.isEmpty()) {
				if (controller.getParams().isInvoiceRecordable()) {
					HibernateUtil.beginTransaction(sessionName);
					int invoicesToRecord = invoicedList.size();
					int recordingInvoice = 0;
					AccountEntryInvoiceWriter accountWriter = new AccountEntryInvoiceWriter();
					for (Invoice invoice : invoicedList) {
						if (!invoice.isProforma()) {
							invoice = (Invoice)HibernateUtil.getSession(sessionName).merge(invoice);
							accountWriter.recordAndUpdateInvoice(invoice);
						}
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
			String msg =  "Error invoicing fees. " + e.getMessage();
			controller.getProgressionState().setProgressionErrorMessage(msg);
			controller.getProgressionState().setProgressionCurrentValue(IProgression.ERROR_VALUE);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);			
		}
	}
	private void communication(String sessionName, Collection<Invoice> invoiceList) throws InvoiceCommunicationException, Exception {
		InvoiceCommunicationConfiguration config = controller.getInvoiceCommunicationConfiguration();
		if (config.isCertificateNeeded()) {
			config.setCertificate(controller.getCert());
		}

		String domainName = AonUtil.getDomainName();
		Integer domainId = AonCollectionUtils.stream(invoiceList).findFirst().orElse(new Invoice()).getDomain();
		Domain domain = new Domain().setName(domainName).setId(domainId);
		Company company = AON.getCompanyForDomain(domainName, domainId, user.getLogin());
		com.esferalia.aon.occam.api.model.security.User usr = new com.esferalia.aon.occam.api.model.security.User()
				.setLogin(user.getLogin())
				.setId(user.getId());
		if(config.isTbai() || config.isLroe() || config.isSii()) {
			// TODO HAY QUE ADAPTAR TICKET BAI PARA QUE PUEDA ENVIARSE TODAS LAS FACTURAS DE UNA.
			// Y USAR EL ELSE PARA TICKET BAI. 
			AonCollectionUtils.stream(invoiceList).forEach(inv -> ticketbai(config, company, inv));
		} else if(config.hasCommunication()) {
			LinkedList<com.esferalia.aon.occam.api.model.finance.Invoice> occamInvoices = AonCollectionUtils.stream(invoiceList)
				.map(i -> AON_SOLUTIONS.getInvoice(domainName, i.getDomain(), user.getLogin(), i.getId()) )
				.collect(Collectors.toCollection( LinkedList::new ))
			;
			InvoiceCommunicatorContext communicator = new InvoiceCommunicatorContext(domain, usr, controller.getCertificate(), occamInvoices)
				.setConfig(config)
				.setCompany(company)
				.setFailOnWrongValidation( AonCollectionUtils.size(occamInvoices) == 1 )
			;
			InvoiceCommunicator.issueInvoice(communicator);

			// Devolver el número a Hibernate para poder contabilizar si todo fue bien
			AonCollectionUtils.stream(invoiceList)
				.map(i -> new Pair<Invoice, com.esferalia.aon.occam.api.model.finance.Invoice> (i, AON.getInvoice(domainName, i.getDomain(), user.getLogin(), i.getId())))
				.forEach( pair -> {
					Invoice hibernateInvoice = pair.getLeft();
					com.esferalia.aon.occam.api.model.finance.Invoice afterInvoice = pair.getRight();
					hibernateInvoice.setNumber(afterInvoice.getNumber());
					hibernateInvoice.setReferenceCode(afterInvoice.getReferenceCode());
					hibernateInvoice = (Invoice) HibernateUtil.getSession(sessionName).merge(hibernateInvoice);
				});
				
		}
	}
	
	private Invoice ticketbai(InvoiceCommunicationConfiguration config, Company company, Invoice inv) {
		String domainName = AonUtil.getDomainName();
		Occam occam = new Occam().setDomainName(domainName).setDomain(inv.getDomain()).setUser(user.getLogin());
		Domain domain = new Domain().setName(domainName).setId(inv.getDomain());
		com.esferalia.aon.occam.api.model.finance.Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, inv.getDomain(), user.getLogin(), inv.getId());

		if(config.isTbai() || config.isLroe()) {
			if(config.getCertificate() == null)
				config.setCertificate(AON.getCertificate(domainName, invoice.getDomain(), user.getLogin(), user.getId(), CertificateType.AEAT.name()));
			try {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionTBAI(company, invoice, config);
			} catch (Exception e ) {
				e.printStackTrace();
				AonUtil.addErrorMessage("Error during SII invoice communication: " + e.getMessage());
			}
		} else if(config.isSii()) {
			try {
				SIIManager manager = SIIManager.getInstance(config);
					
				AccountingReportParams params = new AccountingReportParams();
				params.setDomain(inv.getDomain());
				params.setInvoices(new Integer[] {inv.getId()});
				LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(occam, params, "")
						.collect(Collectors.toCollection(LinkedList::new));		
				manager.suministroFacturas(domain, user.getLogin(), company, invoice, contextList, null);
			} catch (Exception e) {
				e.printStackTrace();
				AonUtil.addErrorMessage("Error during SII invoice communication: " + e.getMessage());
				throw new RuntimeException(e);
			}
		}
		
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
