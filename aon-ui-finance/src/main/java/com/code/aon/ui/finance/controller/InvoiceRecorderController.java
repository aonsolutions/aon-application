package com.code.aon.ui.finance.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRecorderController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceRecorderController.class.getName());

	private static final String SALE_INVOICE_CONTROLLER = "saleInvoice";
	private static final String PURCHASE_INVOICE_CONTROLLER = "purchaseInvoice";
	private static final String EXPENSE_INVOICE_CONTROLLER = "expenseInvoice";
	private static final String SALE_INVOICE_VIEW = "saleInvoice_form";
	private static final String PURCHASE_INVOICE_VIEW = "purchaseInvoice_form";
	private static final String EXPENSE_INVOICE_VIEW = "expenseInvoice_form";
	private static final String SALE_VIEW_NAME = "saleInvoiceRecorder_list";
	private static final String PURCHASE_VIEW_NAME = "purchaseInvoiceRecorder_list";
	private static final String EXPENSE_VIEW_NAME = "expenseInvoiceRecorder_list";
	private String invoiceViewer;
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	public List<ITransferObject> search(int start, int count) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			List<ITransferObject> invoices = super.search(start, count);
			List<ITransferObject> list = new LinkedList<ITransferObject>();
			for (ITransferObject to : invoices) {
				Invoice invoice = (Invoice) to;
				InvoiceRecorder ir = new InvoiceRecorder();
				ir.setInvoice(invoice);
				// Se fuerza a calcular el total.
				ir.getInvoiceTotal();
				ir.setRefresh(true);
				list.add(ir);
			}
			HibernateUtil.commitTransaction(sessionName);
			return list;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "Error recurparando facturas";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public String getInvoiceViewer() {
		return invoiceViewer;
	}

	public void setInvoiceViewer(String invoiceViewer) {
		this.invoiceViewer = invoiceViewer;
	}

	public void onCheckAll(ActionEvent event) throws ManagerBeanException {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(true);
		}
	}

	public void onCheckNone(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(false);
		}
	}

	public void onCheckBrokendown(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(ir.isShowTaxBreakDowns() ? true : ir.isChecked());
		}
	}

	public void onCheckUnbrokendown(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(!ir.isShowTaxBreakDowns() ? true : ir.isChecked());
		}
	}

	public void onCheckRight(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked((ir.isRecordable() && !ir.isWarned()) ? true : ir.isChecked());
		}
	}

	public void onCheckWarned(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked((ir.isRecordable() && ir.isWarned()) ? true : ir.isChecked());
		}
	}

	public void onCheckEntryVisible(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(ir.isShowAccountEntry() ? true : ir.isChecked());
		}
	}

	public void onCheckEntryInvisible(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setChecked(!ir.isShowAccountEntry() ? true : ir.isChecked());
		}
	}

	@SuppressWarnings("unchecked")
	public List<InvoiceRecorder> getCurrentList() {
		try {
			return (List<InvoiceRecorder>) getModel().getWrappedData();
		} catch (ManagerBeanException e) {
			String msg = "Error obtaining model! ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onRecordSelected(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			for (InvoiceRecorder invoiceRecorder : getCurrentList()) {
				if (invoiceRecorder.isRecordable() && invoiceRecorder.isChecked()) {
					Invoice invoice = invoiceRecorder.getInvoice();
					if (invoice.getStatus() == InvoiceStatus.PENDING) {
						try {
							HibernateUtil.beginTransaction(sessionName);
							getAccountEntryInvoiceWriter().recordInvoice(invoice);
							invoice.setStatus(InvoiceStatus.SCORED);
							HibernateUtil.getSession(sessionName).merge(invoice);
							HibernateUtil.getSession(sessionName).flush();
							HibernateUtil.commitTransaction(sessionName);
						} catch (Exception e) {
							try {
								HibernateUtil.rollbackTransaction(sessionName);
							} catch (DAOException daoe) {
								String msg = "Unable to rollback transaction!";
								LOGGER.error(msg, e);
							}
							String msg = "Error recording invoice:  " + invoice.getReferenceCode();
							LOGGER.error(msg, e);
							AonUtil.addErrorMessage(msg);
							throw new AbortProcessingException(msg);
						} finally {
							HibernateUtil.closeSession(sessionName);
						}
					}
				}
			}
			this.onSearch(null);
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public String invoiceView() {
		return getInvoiceViewer();
	}

	public void onShowAccountEntry(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
			ir.setShowAccountEntry(true);
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowAllAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(true);
				ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onHideAllAccountEntry(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowAccountEntry(false);
		}
	}

	public void onShowCheckedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(ir.isChecked() ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowUncheckedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry(!ir.isChecked() ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowCorrectAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((ir.isRecordable() && !ir.isWarned()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowIncorrectAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((!ir.isRecordable()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowWarnedAccountEntry(ActionEvent event) {
		try {
			List<InvoiceRecorder> list = getCurrentList();
			for (InvoiceRecorder ir : list) {
				ir.setShowAccountEntry((ir.isRecordable() && ir.isWarned()) ? true : ir.isShowAccountEntry());
				if (ir.isShowAccountEntry()) {
					ir.setDetails(getAccountEntryInvoiceWriter().preRecordInvoice(ir.getInvoice()));
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible previsualizar el apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onHideAccountEntry(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowAccountEntry(false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible ocultar la previsualización del apunte: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowTaxBreakDowns(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowTaxBreakDowns(true);
			ir.getTaxBreakDowns();
		} catch (ManagerBeanException e) {
			String msg = "Imposible mostrar el deglose de la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onShowAllTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(true);
		}
	}

	public void onHideAllTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(false);
		}
	}

	public void onShowCheckedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(ir.isChecked() ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowUncheckedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns(!ir.isChecked() ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowCorrectTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((ir.isRecordable() && !ir.isWarned()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowIncorrectTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((!ir.isRecordable()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onShowWarnedTaxBreakDowns(ActionEvent event) {
		List<InvoiceRecorder> list = getCurrentList();
		for (InvoiceRecorder ir : list) {
			ir.setShowTaxBreakDowns((ir.isRecordable() && ir.isWarned()) ? true : ir.isShowTaxBreakDowns());
		}
	}

	public void onHideTaxBreakDowns(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			ir.setShowTaxBreakDowns(false);
		} catch (ManagerBeanException e) {
			String msg = "Imposible ocultar el deglose de la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onLoadInvoice(ActionEvent event) {
		try {
			InvoiceRecorder ir = (InvoiceRecorder) getModel().getRowData();
			InvoiceType type = ir.getInvoice().getType();
			String invoiceControllerName;
			String currentViewName;
			if (type == InvoiceType.SALES) {
				invoiceControllerName = SALE_INVOICE_CONTROLLER;
				setInvoiceViewer(SALE_INVOICE_VIEW);
				currentViewName = SALE_VIEW_NAME;
			} else if (type == InvoiceType.PURCHASE) {
				invoiceControllerName = PURCHASE_INVOICE_CONTROLLER;
				setInvoiceViewer(PURCHASE_INVOICE_VIEW);
				currentViewName = PURCHASE_VIEW_NAME;
			} else if (type == InvoiceType.EXPENSES) {
				invoiceControllerName = EXPENSE_INVOICE_CONTROLLER;
				setInvoiceViewer(EXPENSE_INVOICE_VIEW);
				currentViewName = EXPENSE_VIEW_NAME;
			} else {
				Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
				String msg = "No existe visor para el tipo de factura " + type.getName(locale);
				LOGGER.warn(msg);
				AonUtil.addWarningMessage(msg);
				throw new AbortProcessingException(msg);
			}
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.setBackAction(currentViewName);
			invoiceController.onEditSearch(event);
			String alias = invoiceController.getManagerBean().getFieldName(IFinanceAlias.INVOICE_ID);
			invoiceController.getCriteria().addEqualExpression(alias, ir.getInvoice().getId());
			ir.setRefresh(true);
			invoiceController.onSearch(event);
			invoiceController.getModel().setRowIndex(0);
			invoiceController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la factura: " + e.getMessage();
			LOGGER.warn(msg, e);
			AonUtil.addWarningMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

}