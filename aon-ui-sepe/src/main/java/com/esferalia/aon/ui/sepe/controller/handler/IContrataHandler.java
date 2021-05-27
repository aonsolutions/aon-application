package com.esferalia.aon.ui.sepe.controller.handler;

import java.io.IOException;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public interface IContrataHandler {
	
	Contract getContract();
	
	IContrataParams getParams();
	
	ContractCode getContractCode();
	
	boolean isCommunicationAvailable();
	
	void initialize(Contract contract);

	void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException;

	
}
