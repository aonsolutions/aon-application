package com.esferalia.aon.dex.process;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.finance.invoicing.WsPosInvoicing;
import com.code.aon.finance.util.PosBalanceUtils;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.shared.PosShiftDex;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.Items;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.Items.Item;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.PosShiftCount;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.PosShiftCount.PosShiftCountDetail;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.PosShiftDeclared;
import com.esferalia.aon.dex.shared.PosShiftDex.PosShift.PosShiftDeclared.PosShiftDeclaredDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class PosShiftLoadManager extends CommonLoadManager implements IDataLoadConstants {

	private com.code.aon.finance.PosShift posShiftDB;

	public String processPosShift(PosShiftDex posShiftDex, int domain) {
		int numRegsOk = 0;
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.startSession(sessionName);

			for (PosShift ps: posShiftDex.getPosShift()) {
				List<InvoiceDetail> details = obtainDetailList(ps.getItems());
				List<Finance> finances = obtainFinanceList(ps.getPosShiftCount());
				if (validatePosShift(ps, details, finances)) {
					posShiftDB = obtainPosShift(ps, domain);
					if (posShiftDB == null) {
						HibernateUtil.beginTransaction(sessionName);

						insertPosShift(ps, domain);
						PosShiftDeclared psDeclared = ps.getPosShiftDeclared();
						if (psDeclared != null) {
							for (PosShiftDeclaredDetail psDeclaredDetail : psDeclared.getPosShiftDeclaredDetail()) {
								insertPosShiftCount(psDeclaredDetail, domain);
							}
						}

						HibernateUtil.getSession(sessionName).flush();
						HibernateUtil.commitTransaction(sessionName);

			            if (posShiftDB.getPos().isInvoiceable()) {
			            	Invoice invoice = generateInvoice(ps, details, finances, domain);
			            	if (invoice != null) {
								HibernateUtil.beginTransaction(sessionName);
	
								AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
				        		entryWriter.recordAndUpdateInvoice(invoice);
	
								HibernateUtil.getSession(sessionName).flush();
								HibernateUtil.commitTransaction(sessionName);
			            	}
			            }
					} else {
						HibernateUtil.beginTransaction(sessionName);

						updatePosShift(ps);
						PosShiftDeclared psDeclared = ps.getPosShiftDeclared();
						if (psDeclared != null) {
							removePosShiftCount();
							for (PosShiftDeclaredDetail psDeclaredDetail : psDeclared.getPosShiftDeclaredDetail()) {
								insertPosShiftCount(psDeclaredDetail, domain);
							}
						}

						HibernateUtil.getSession(sessionName).flush();
						HibernateUtil.commitTransaction(sessionName);

			            if (posShiftDB.getPos().isInvoiceable()) {
			            	Invoice invoice = obtainInvoice();
			            	if (invoice == null) {
				            	invoice = generateInvoice(ps, details, finances, domain);
				            	if (invoice != null) {
									HibernateUtil.beginTransaction(sessionName);
		
									AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
					        		entryWriter.recordAndUpdateInvoice(invoice);
		
									HibernateUtil.getSession(sessionName).flush();
									HibernateUtil.commitTransaction(sessionName);
				            	}
			            	} else {
								HibernateUtil.beginTransaction(sessionName);

								if (invoice.getTotal() == obtainFinanceAmount(finances) && isAllFinancePending(invoice)) {
									removeFinances(invoice);
									insertFinances(invoice, finances);
								}
	
								HibernateUtil.getSession(sessionName).flush();
								HibernateUtil.commitTransaction(sessionName);
			            	}
			            }
					}

					HibernateUtil.beginTransaction(sessionName);

					posShiftDB = (com.code.aon.finance.PosShift)HibernateUtil.getSession(sessionName).merge(posShiftDB);
					updatePosShift(null);

					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);

					++numRegsOk;
				}
			}
			return documentSuccess(numRegsOk, 0);
		} catch (Exception ex) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (Exception e) {
			}
			return documentError(ex.getMessage(), numRegsOk, 0);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private boolean validatePosShift(PosShift ps, List<InvoiceDetail> details, List<Finance> finances) throws Exception {
		if (ps.getStartTime().after(new Date())) {
			throw new Exception("Fecha de Apertura de Caja incorrecta.");
		} else if (ps.getStartTime().after(ps.getEndTime())) {
			throw new Exception("Fecha de Cierre de Caja incorrecta.");
		} else {
			double diff = CommonUtil.round(obtainFinanceAmount(finances) - obtainDetailAmount(details));
			if (diff != 0) {
				if (Math.abs(diff) == 0.01) {
					for (int i=1; i<=50; i++) {
						InvoiceDetail invoiceDetail = details.get(details.size() - 1);
						if (invoiceDetail.getDiscountExpression().getDiscounts()[0] == 0) {
							invoiceDetail.setPrice(invoiceDetail.getPrice() + (diff / 100));
						}
						invoiceDetail.setTaxableBase(invoiceDetail.getTaxableBase() + (diff / 100));
						if (obtainDetailAmount(details) == obtainFinanceAmount(finances)) {
							return true;
						}
					}
				}
				throw new Exception("El importe de los Productos de la Factura no coincide con el importe de los Pagos.");
			}
		}
		return true;
	}

	private com.code.aon.finance.PosShift obtainPosShift(PosShift ps, int domain) throws Exception {
		IManagerBean posShiftBean = BeanManager.getManagerBean(com.code.aon.finance.PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_DOMAIN), domain);
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_ID), ps.getPos());
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_SHIFT), Shift.values()[ps.getShift()]);
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USERNAME), ps.getUsername());
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME), ps.getStartTime());
		for (ITransferObject ito : posShiftBean.getList(criteria)) {
			return (com.code.aon.finance.PosShift)ito;
		}
		return null;
	}

	private void insertPosShift(PosShift ps, int domain) throws Exception {
		posShiftDB = populatePosShift(ps, domain);
		posShiftDB = (com.code.aon.finance.PosShift)BeanManager.getManagerBean(com.code.aon.finance.PosShift.class).insert(posShiftDB);
	}

	private com.code.aon.finance.PosShift populatePosShift(PosShift ps, int domain) throws Exception {
		String dexInfo = LOADED_FROM_WS_MSG + " [" + getDateTimeAdapter().marshal(new Date()) + "]";
		String ticketInfo = TICKET_MSG + " " + FROM_MSG + ": " + ps.getTicketStart() + " " + TO_MSG + ": " + ps.getTicketEnd();

		posShiftDB = new com.code.aon.finance.PosShift();
		posShiftDB.setDomain(domain);
		posShiftDB.setPos((Pos)BeanManager.getManagerBean(Pos.class).get(ps.getPos()));
		posShiftDB.setShift(Shift.values()[ps.getShift()]);
		posShiftDB.setUsername(ps.getUsername());
		posShiftDB.setStartTime(ps.getStartTime());
		posShiftDB.setEndTime(ps.getEndTime());
		posShiftDB.setInitialAmount(ps.getInitialAmount());
		posShiftDB.setImbalance(ps.isImbalance());
		posShiftDB.setRemarks(dexInfo + "\n" + ticketInfo + "\n" + ps.getRemarks());
		posShiftDB.setCreationUser(WS_USER);
		posShiftDB.setCreationDate(new Date());
		posShiftDB.setSkipCheckPosShift(true);
		return posShiftDB;
	}

	private void updatePosShift(PosShift ps) throws Exception {
		if (ps != null) {
			String dexInfo = RELOADED_FROM_WS_MSG + " [" + getDateTimeAdapter().marshal(new Date()) + "]";
			posShiftDB.setImbalance(ps.isImbalance());
			posShiftDB.setRemarks(dexInfo + "\n" + posShiftDB.getRemarks());
			posShiftDB.setModificationUser(WS_USER);
			posShiftDB.setModificationDate(new Date());
			posShiftDB.setSkipCheckPosShift(true);
		} else {
			posShiftDB.setImbalance(PosBalanceUtils.isPosShiftImbalance(posShiftDB));
		}
		posShiftDB = (com.code.aon.finance.PosShift)BeanManager.getManagerBean(com.code.aon.finance.PosShift.class).update(posShiftDB);
	}

	private void insertPosShiftCount(PosShiftDeclaredDetail psDeclaredDetail, int domain) throws Exception {
		if (psDeclaredDetail.getAmount() != 0) {
			com.code.aon.finance.PosShiftCount posShiftCountDB = populatePosShiftCount(psDeclaredDetail, domain);
			BeanManager.getManagerBean(com.code.aon.finance.PosShiftCount.class).insert(posShiftCountDB);
		}
	}

	private com.code.aon.finance.PosShiftCount populatePosShiftCount(PosShiftDeclaredDetail psDeclaredDetail, int domain) throws Exception {
		com.code.aon.finance.PosShiftCount posShiftCountDB = new com.code.aon.finance.PosShiftCount();
		posShiftCountDB.setDomain(domain);
		posShiftCountDB.setPosShift(posShiftDB);
		posShiftCountDB.setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).get(psDeclaredDetail.getPayMethod()));
		posShiftCountDB.setAmount(psDeclaredDetail.getAmount());
		posShiftCountDB.setCreationUser(WS_USER);
		posShiftCountDB.setCreationDate(new Date());
		posShiftCountDB.setSkipCheckPosShift(true);
		return posShiftCountDB;
	}

	private void removePosShiftCount() throws Exception {
		IManagerBean posShiftCountBean = BeanManager.getManagerBean(com.code.aon.finance.PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), posShiftDB.getId());
		for (ITransferObject ito : posShiftCountBean.getList(criteria)) {
			com.code.aon.finance.PosShiftCount posShiftCount = (com.code.aon.finance.PosShiftCount)ito;
			posShiftCount.setSkipCheckPosShift(true);
			posShiftCountBean.remove(posShiftCount);
		}
	}

	private Invoice generateInvoice(PosShift ps, List<InvoiceDetail> details, List<Finance> finances, int domain) throws Exception {
		WsPosInvoicing posInvoicing = new WsPosInvoicing();
		return posInvoicing.createInvoice(posShiftDB, new Date(), getComments(ps), details, finances, WS_USER);
	}

	private String getComments(PosShift ps) throws Exception {
		String dexInfo = GENERATED_FROM_WS_MSG + " [" + getDateTimeAdapter().marshal(new Date()) + "]";
		String ticketInfo = TICKET_MSG + " " + FROM_MSG + ": " + ps.getTicketStart() + " " + TO_MSG + ": " + ps.getTicketEnd();
		return dexInfo + "\n" + ticketInfo;
	}

	private List<InvoiceDetail> obtainDetailList(Items items) throws Exception {
		List<InvoiceDetail> details = new LinkedList<InvoiceDetail>();
		if (items != null) {
			for (Item item : items.getItem()) {
				if (item.getTaxableBase() != 0) {
					com.code.aon.product.Item itemDB = (com.code.aon.product.Item)BeanManager.getManagerBean(com.code.aon.product.Item.class).get(item.getId());
					if (itemDB == null) {
						throw new Exception("El Producto con ID = " + item.getId() + " no existe!");
					}

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setItem(itemDB);
					invoiceDetail.setQuantity(item.getQuantity());
					if (StringUtils.isNotBlank(item.getDiscountExpr())) {
						invoiceDetail.setDiscountExpression(new DiscountExpression(item.getDiscountExpr()));
					} else {
						invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
					}
					invoiceDetail.setTaxableBase(item.getTaxableBase());
					invoiceDetail.setVatPercent(invoiceDetail.getItem().getVat().getDatedPercentage(new Date()));
					invoiceDetail.setVatQuota(item.getTaxes());

					double vatQuota = CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100);
					if (Math.abs(CommonUtil.round(invoiceDetail.getVatQuota() - vatQuota)) == 0.01) {
						invoiceDetail.setVatQuota(vatQuota);
					}

					if (invoiceDetail.getVatQuota() == vatQuota) {
						double price = invoiceDetail.getTaxableBase();
						for (int i=0; i<invoiceDetail.getDiscountExpression().getDiscounts().length; i++) {
							price = price / ( 1 - invoiceDetail.getDiscountExpression().getDiscounts()[i] /100);
						}
						invoiceDetail.setPrice(CommonUtil.round(price / invoiceDetail.getQuantity(), 4));
						details.add(invoiceDetail);
					} else {
						throw new Exception("La Cuota de IVA (" + item.getTaxes() + ") no concuerda con el Porcentaje (" + invoiceDetail.getVatPercent() + ")");
					}
				}
			}
		}
		return details;
	}

	private double obtainDetailAmount(List<InvoiceDetail> details) {
		double taxableBase = 0;
		for (InvoiceDetail invoiceDetail : details) {
			taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);
		}
		taxableBase = CommonUtil.round(taxableBase);

		Map<Double, Double> vatMap = new HashMap<Double, Double>();
		for (InvoiceDetail invoiceDetail : details) {
			double base = 0;
			if (vatMap.containsKey(invoiceDetail.getVatPercent())) {
				base = vatMap.get(invoiceDetail.getVatPercent());
			}
			base = CommonUtil.round(base + invoiceDetail.getTaxableBase(), 4);
			vatMap.put(invoiceDetail.getVatPercent(), base);
		}
		double vatQuota = 0;
		for (Double vatPercent : vatMap.keySet()) {
			vatQuota = CommonUtil.round(vatQuota + CommonUtil.round(vatMap.get(vatPercent) * vatPercent / 100));
		}
		vatQuota = CommonUtil.round(vatQuota);

		return CommonUtil.round(taxableBase + vatQuota);
	}

	private List<Finance> obtainFinanceList(PosShiftCount psCount) throws Exception {
		List<Finance> finances = new LinkedList<Finance>();
		for (PosShiftCountDetail psCountDetail : psCount.getPosShiftCountDetail()) {
			if (psCountDetail.getAmount() != 0) {
				Finance finance = new Finance();
				finance.setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).get(psCountDetail.getPayMethod()));
				finance.setAmount(psCountDetail.getAmount());
				finances.add(finance);
			}
		}
		return finances;
	}

	private double obtainFinanceAmount(List<Finance> finances) {
		double financeAmount = 0;
		for (Finance finance : finances) {
			financeAmount = CommonUtil.round(financeAmount + finance.getAmount());
		}
		return financeAmount;
	}

	private Invoice obtainInvoice() throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_POS_SHIFT_ID), posShiftDB.getId());
		for (ITransferObject ito : invoiceBean.getList(criteria)) {
			return (Invoice)ito;
		}
		return null;
	}

	private boolean isAllFinancePending(Invoice invoice) throws ManagerBeanException {
		if (!invoice.isAllFinancePending()) {
			return false;
		}

		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeTrackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_INVOICE_ID), invoice.getId());
		return financeTrackingBean.getCount(criteria) == 0;
	}

	private void removeFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			finance.setSkipCheckPosShift(true);
			financeBean.remove(finance);
		}
	}

	private void insertFinances(Invoice invoice, List<Finance> finances) throws ManagerBeanException {
		for (Finance finance : finances) {
			finance.setDomain(invoice.getDomain());
			finance.setPayment(false);
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setRegistryDocument(invoice.getRegistryDocument());
			finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
			finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
			finance.setRegistryName(invoice.getRegistryName());
	        finance.setConcept(invoice.getDocumentNumber()); 
			finance.setDueDate(invoice.getIssueDate());
			finance.setSecurityLevel(invoice.getSecurityLevel());
			finance.setScope(invoice.getScope());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			finance.setManual(true);
			finance.setCreationUser(WS_USER);
			finance.setCreationDate(new Date());
			finance.setSkipCheckPosShift(true);

			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			financeBean.insert(finance);
		}
	}

}
