package com.code.aon.finance.print;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.FinanceBatch;

public class ReportFinanceBatch implements ITransferObject {

	private static final long serialVersionUID = -929901176929210472L;

	private FinanceBatch financeBatch;
	
	private Integer regs;

	private Double total;

	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	public void setFinanceBatch(FinanceBatch batch) {
		financeBatch = batch;
	}

	public Integer getRegs() {
		return regs;
	}

	public void setRegs(Integer regs) {
		this.regs = regs;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}
