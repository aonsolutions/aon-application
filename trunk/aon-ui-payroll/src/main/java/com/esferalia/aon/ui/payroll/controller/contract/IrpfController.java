package com.esferalia.aon.ui.payroll.controller.contract;

import com.esferalia.aon.payroll.IrpfResult;

public class IrpfController extends AbstractIrpfController {
	
	private IrpfResult irpfResult;
	
	public IrpfController(IrpfResult irpfResult) {
		this.irpfResult = irpfResult;
	}
	
	@Override
	public IrpfResult getIrpfResult() {
		return irpfResult;
	}

}
