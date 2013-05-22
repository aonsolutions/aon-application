package com.code.aon.ui.finance.controller;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Pos;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.PosShiftCount;
import com.code.aon.finance.enumeration.Shift;
import com.code.aon.ui.finance.util.PosUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class PosClosingController implements IFinanceConstants {

	private PosShift posShift;
	private CashCalculator calculator;
	private DataModel totalShiftCountModel;

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

	public DataModel getTotalShiftCountModel() {
		if (totalShiftCountModel == null) {
			totalShiftCountModel = new ListDataModel(getTotalShiftCountList(getPosShift()));
		}
		return totalShiftCountModel;
	}

	public void setTotalShiftCountModel(DataModel totalShiftCountModel) {
		this.totalShiftCountModel = totalShiftCountModel;
	}

	private List<PayMethodCount> getTotalShiftCountList(PosShift posShift) {
		List<PayMethodCount> totalShiftCountList = new LinkedList<PayMethodCount>();
		for (PayMethod payMethod : posShift.getTotalShiftCountMap().keySet()) {
			double[] totals = posShift.getTotalShiftCountMap().get(payMethod);

			PayMethodCount payMethodCount = new PayMethodCount();
			payMethodCount.setPayMethod(payMethod);
			payMethodCount.setCountAmount(totals[0]);
			payMethodCount.setFinanceAmount(totals[1]);
			
			totalShiftCountList.add(payMethodCount);
		}
		Collections.sort(totalShiftCountList);
		return totalShiftCountList;
	}

    public void onLoad(ActionEvent event) {
    	setPosShift(PosUtils.getUserPosShift());
		if (getPosShift() == null || getPosShift().getId() == null) {
			String msg = "No hay ninguna Caja abierta por el Usuario.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setTotalShiftCountModel(null);
    }   

    public ITransferObject getTo() {
    	return getPosShift();
    }

    public void onShowCalculatorWindow(ActionEvent event) {
		getCalculator().initialize();
	}

	public void onAcceptCalculatorWindow(ActionEvent event) {
		getPosShift().setTotalShiftCountMap(null);
		setTotalShiftCountModel(null);
	}

	public void onAcceptCount(ActionEvent event) {
		((PosShiftCount)FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).getTo()).setPosShift(getPosShift());
		FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME).onAccept(event);
		getPosShift().setTotalShiftCountMap(null);
		setTotalShiftCountModel(null);
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
		private double financeAmount;
		
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

		public double getFinanceAmount() {
			return financeAmount;
		}

		public void setFinanceAmount(double financeAmount) {
			this.financeAmount = financeAmount;
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

/*   
    public void onAccept( ActionEvent event ){
        try {
            if(getPosShift().getPos().isInvoiceable()){
                PosInvoicing posInvoicing = new PosInvoicing();
                try {
                    posInvoicing.completeInvoice(getPosShift());
                } catch (Exception e) {
                    String msg = "Error al completar la factura de caja";
                    AonUtil.addErrorMessage(msg +"("+ e.getMessage()+")");
                    throw new AbortProcessingException(msg, e);
                }
            } else {
                getPosShift().setInvoice(null);
            }
            IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
            getPosShift().setEndTime(new Date());
            PosShift ps = (PosShift) bean.update(getPosShift());
            setPosShift((PosShift) bean.get(ps.getId()));
            selectPosShift( event );
        } catch (ManagerBeanException e) {
            String msg = "Error al grabar el cierre de caja";
            AonUtil.addErrorMessage(msg +"("+ e.getMessage()+")");
            throw new AbortProcessingException(msg, e);
        }
    }   
   
   */
           
}