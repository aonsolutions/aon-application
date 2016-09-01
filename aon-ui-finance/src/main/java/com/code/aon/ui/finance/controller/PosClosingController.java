package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_FROM;
import static com.code.aon.ui.common.ICommonMessages.DATE_TO;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_POS_SHIFT_IMBALANCE_ERROR;
import static com.code.aon.ui.common.ICommonMessages.TICKET;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.finance.invoicing.PosInvoicing;
import com.code.aon.finance.util.PosBalanceUtils;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class PosClosingController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PosShift posShift;
	private String fromTicket;
	private String toTicket;
	private CashCalculator calculator;
	private boolean showConfirmWindow;

	public PosShift getPosShift() {
		return posShift;
	}

	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}
	
	public String getFromTicket() {
		return fromTicket;
	}

	public void setFromTicket(String fromTicket) {
		this.fromTicket = fromTicket;
	}
	
	public String getToTicket() {
		return toTicket;
	}

	public void setToTicket(String toTicket) {
		this.toTicket = toTicket;
	}
	
	public CashCalculator getCalculator() {
		if (calculator == null) {
			calculator = new CashCalculator();
		}
		return calculator;
	}

	public void setCalculator(CashCalculator calculator) {
		this.calculator = calculator;
	}

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}

	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}

    public void onLoad(ActionEvent event) {    	
    	setFromTicket(null);
    	setToTicket(null);
    	setPosShift(PosUtils.getUserPosShift());
    	if (getPosShift() == null || getPosShift().getId() == null) {
			String msg = "No hay ninguna Caja abierta por el Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} else {
			try {
		        ((PosShiftController)FormUtil.getController(POS_SHIFT_CONTROLLER_NAME)).load(event, getPosShift().getId());
			} catch (ManagerBeanException ex) {
				String msg = "Error en el proceso de Cierre de Caja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
    }   

    public ITransferObject getTo() {
    	return getPosShift();
    }

    public void onShowCalculatorWindow(ActionEvent event) {
		getCalculator().initialize();
	}

	public void onAcceptCalculatorWindow(ActionEvent event) {
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onAcceptCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onAccept(event);
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onRemoveCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onRemove(event);
		getPosShift().setTotalShiftCountMap(null);
	}

	public void onConfirmClose(ActionEvent event) {
		if (!getPosShift().isClosed() && !getPosShift().getPos().isInvoiceable() && PosBalanceUtils.isPosShiftImbalance(getPosShift())) {
			setShowConfirmWindow(true);
		} else if (!getPosShift().isClosed()) {
			onClose(event);
		} else {
			onSave(event);
		}
	}

	public String getImbalancePayMethodsError() {
		String imbalance = "";
		for (PayMethod payMethod : getPosShift().getTotalShiftCountMap().keySet()) {
			double[] totals = posShift.getTotalShiftCountMap().get(payMethod);
			if (totals[0] != totals[1]) {
				imbalance += ", " + payMethod.getName();
			}
		}
		return AonUtil.getMessage(FINANCE_POS_SHIFT_IMBALANCE_ERROR, imbalance.replaceFirst(", ", ""));
	}

	public void onClose(ActionEvent event) {
		try {
			String ticketInfo = AonUtil.getMessage(TICKET) + " " + 
									AonUtil.getMessage(DATE_FROM) + ": " + getFromTicket() + " " + 
									AonUtil.getMessage(DATE_TO) + ": " + getToTicket();
			if (!getPosShift().isClosed() && validateClosing(getPosShift().getPos(), getPosShift().getShift())) {
				getPosShift().setEndTime(new Date());
				if (getPosShift().getPos().isInvoiceable()) {
					getPosShift().setRemarks(ticketInfo + "\n" + getPosShift().getRemarks());
				}
				onSave(event);

				if (getPosShift().getPos().isInvoiceable()) {
	                PosInvoicing posInvoicing = new PosInvoicing();
	                Invoice invoice = posInvoicing.completeInvoice(getPosShift(), getPosShift().getShift().getName(AonUtil.getCurrentLocale()), ticketInfo);
					onSave(event);
	
	        		AccountEntryInvoiceWriter entryWriter = new AccountEntryInvoiceWriter();
	        		entryWriter.recordAndUpdateInvoice(invoice);
	            }
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Cierre de Caja. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private boolean validateClosing(Pos pos, Shift shift) {
		if (!PosUtils.isPosShiftAlreadyOpened(pos, shift)) {
			String msg = "No es posible cerrar la Caja. Ya ha sido cerrada por otro Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

		return true;
	}

	public void onSave(ActionEvent event) {
		try {
			setPosShift((PosShift)BeanManager.getManagerBean(PosShift.class).update(getPosShift()));
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Cierre de Caja. " + ex.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}


	public class PayMethodCount implements Comparable<Object> {
		private PayMethod payMethod;
		private double countAmount;
		
		public PayMethod getPayMethod() {
			return payMethod;
		}

		public void setPayMethod(PayMethod payMethod) {
			this.payMethod = payMethod;
		}

		public double getCountAmount() {
			return countAmount;
		}

		public void setCountAmount(double countAmount) {
			this.countAmount = countAmount;
		}

		@Override
		public int compareTo(Object obj) {
			if (obj instanceof PayMethodCount) {
				PayMethodCount payMethodCount = (PayMethodCount)obj;
				return this.getPayMethod().getName().compareTo(payMethodCount.getPayMethod().getName());
			}
			return 0;
		}

	}

}