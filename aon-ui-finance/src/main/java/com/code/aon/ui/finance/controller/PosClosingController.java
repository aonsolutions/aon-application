package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class PosClosingController implements IFinanceConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private PosShift posShift;
	private CashCalculator calculator;

	public PosShift getPosShift() {
		return posShift;
	}

	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
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

    public void onLoad(ActionEvent event) {
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
	}

	public void onRemoveCount(ActionEvent event) {
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onRemove(event);
	}

	public void onAccept(ActionEvent event) {
		if (getPosShift().getEndTime() == null && validateClosing(getPosShift().getPos(), getPosShift().getShift())) {
			getPosShift().setEndTime(new Date());
		}

		try {
			setPosShift((PosShift)BeanManager.getManagerBean(PosShift.class).insertOrUpdate(getPosShift()));
		} catch (ManagerBeanException ex) {
			String msg = "Error en el proceso de Cierre de Caja.";
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