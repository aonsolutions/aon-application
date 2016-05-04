package com.esferalia.aon.dex.process;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.finance.invoicing.WsPosInvoicing;
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

	private com.code.aon.finance.PosShift posShiftBD;

	public String processPosShift(PosShiftDex posShiftDex, int domain) {
		int numRegsOk = 0;
		int numRegsDup = 0;
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.startSession(sessionName);

			for (PosShift ps: posShiftDex.getPosShift()) {
				if (validatePosShift(ps, domain)) {
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

		            if (posShiftBD.getPos().isInvoiceable()) {
		            	Invoice invoice = generateInvoice(ps, domain);
		            	if (invoice.getTotal() != 0 && invoice.getTotal() == obtainFinanceAmount(ps.getPosShiftCount())) {
							HibernateUtil.beginTransaction(sessionName);

							AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
			        		entryWriter.recordAndUpdateInvoice(invoice);

							HibernateUtil.getSession(sessionName).flush();
							HibernateUtil.commitTransaction(sessionName);
		            	}
		            }

		            ++numRegsOk;
				} else {
					++numRegsDup;
				}
			}
			return documentSuccess(numRegsOk, numRegsDup);
		} catch (Exception ex) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
				if (posShiftBD != null && posShiftBD.getId() != null) {
					HibernateUtil.beginTransaction(sessionName);

					removeCurrentPosShift();

					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				}
			} catch (Exception e) {
			}
			return documentError(ex.getMessage(), numRegsOk, numRegsDup);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private boolean validatePosShift(PosShift ps, int domain) throws Exception {
		IManagerBean posShiftBean = BeanManager.getManagerBean(com.code.aon.finance.PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_DOMAIN), domain);
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_POS_ID), ps.getPos());
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_SHIFT), Shift.values()[ps.getShift()]);
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_USERNAME), ps.getUsername());
		criteria.addEqualExpression(posShiftBean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME), ps.getStartTime());
		return posShiftBean.getCount(criteria) == 0;
	}

	private void insertPosShift(PosShift ps, int domain) throws Exception {
		posShiftBD = populatePosShift(ps, domain);
		posShiftBD = (com.code.aon.finance.PosShift)BeanManager.getManagerBean(com.code.aon.finance.PosShift.class).insert(posShiftBD);
	}

	private com.code.aon.finance.PosShift populatePosShift(PosShift ps, int domain) throws Exception {
		String dexInfo = LOADED_FROM_WS_MSG + " [" + getDateTimeAdapter().marshal(new Date()) + "]";
		String ticketInfo = TICKET_MSG + " " + FROM_MSG + ": " + ps.getTicketStart() + " " + TO_MSG + ": " + ps.getTicketEnd();

		posShiftBD = new com.code.aon.finance.PosShift();
		posShiftBD.setDomain(domain);
		posShiftBD.setPos((Pos)BeanManager.getManagerBean(Pos.class).get(ps.getPos()));
		posShiftBD.setShift(Shift.values()[ps.getShift()]);
		posShiftBD.setUsername(ps.getUsername());
		posShiftBD.setStartTime(ps.getStartTime());
		posShiftBD.setEndTime(ps.getEndTime());
		posShiftBD.setInitialAmount(ps.getInitialAmount());
		posShiftBD.setImbalance(ps.isImbalance());
		posShiftBD.setRemarks(dexInfo + "\n" + ticketInfo + "\n" + ps.getRemarks());
		return posShiftBD;
	}

	private void insertPosShiftCount(PosShiftDeclaredDetail psDeclaredDetail, int domain) throws Exception {
		if (psDeclaredDetail.getAmount() != 0) {
			com.code.aon.finance.PosShiftCount posShiftCountBD = populatePosShiftCount(psDeclaredDetail, domain);
			BeanManager.getManagerBean(com.code.aon.finance.PosShiftCount.class).insert(posShiftCountBD);
		}
	}

	private com.code.aon.finance.PosShiftCount populatePosShiftCount(PosShiftDeclaredDetail psDeclaredDetail, int domain) throws Exception {
		com.code.aon.finance.PosShiftCount posShiftCountBD = new com.code.aon.finance.PosShiftCount();
		posShiftCountBD.setDomain(domain);
		posShiftCountBD.setPosShift(posShiftBD);
		posShiftCountBD.setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).get(psDeclaredDetail.getPayMethod()));
		posShiftCountBD.setAmount(psDeclaredDetail.getAmount());
		return posShiftCountBD;
	}

	private Invoice generateInvoice(PosShift ps, int domain) throws Exception {
		WsPosInvoicing posInvoicing = new WsPosInvoicing();
		return posInvoicing.createInvoice(posShiftBD, new Date(), getComments(ps), obtainDetailList(ps.getItems()), obtainFinanceList(ps.getPosShiftCount()));
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
					com.code.aon.product.Item itemBD = (com.code.aon.product.Item)BeanManager.getManagerBean(com.code.aon.product.Item.class).get(item.getId());
					if (itemBD == null) {
						throw new Exception("El Producto con ID = " + item.getId() + " no existe!");
					}

					InvoiceDetail invoiceDetail = new InvoiceDetail();
					invoiceDetail.setItem(itemBD);
					invoiceDetail.setQuantity(item.getQuantity());
					if (StringUtils.isNotBlank(item.getDiscountExpr())) {
						invoiceDetail.setDiscountExpression(new DiscountExpression(item.getDiscountExpr()));
					} else {
						invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
					}
					invoiceDetail.setTaxableBase(item.getTaxableBase());
					invoiceDetail.setVatPercent(invoiceDetail.getItem().getVat().getDatedPercentage(new Date()));
					invoiceDetail.setVatQuota(item.getTaxes());

					if (invoiceDetail.getVatQuota() == CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceDetail.getVatPercent() / 100)) {
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

	private double obtainFinanceAmount(PosShiftCount psCount) throws Exception {
		double financeAmount = 0;
		for (PosShiftCountDetail psCountDetail : psCount.getPosShiftCountDetail()) {
			if (psCountDetail.getAmount() != 0) {
				financeAmount = CommonUtil.round(financeAmount + psCountDetail.getAmount());
			}
		}
		return financeAmount;
	}

	private void removeCurrentPosShift() throws ManagerBeanException {
		IManagerBean posShiftCountBean = BeanManager.getManagerBean(com.code.aon.finance.PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), posShiftBD.getId());
		for (ITransferObject ito : posShiftCountBean.getList(criteria)) {
			posShiftCountBean.remove(ito);
		}

		BeanManager.getManagerBean(com.code.aon.finance.PosShift.class).remove(posShiftBD);
	}

}