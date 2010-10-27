package com.code.aon.employee.enumeration;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractType implements IResourceable {
	
	PE170(ContractModel.PE170, 
			ContractCode.C100, ContractCode.C200),
	
	PE174_EXCLUSION(ContractModel.PE174, 
			ContractCode.C150, ContractCode.C250,
			ContractCode.C350, ContractCode.C450,
			ContractCode.C550),
	
	PE174_VIOLENCE(ContractModel.PE174, 
			ContractCode.C150, ContractCode.C250,
			ContractCode.C350, ContractCode.C450,
			ContractCode.C550),

	PE181(ContractModel.PE181, 
			ContractCode.C350, ContractCode.C300),

	PE183(ContractModel.PE183, 
			ContractCode.C109, ContractCode.C209, 
			ContractCode.C189, ContractCode.C289),

	PE185(ContractModel.PE185, 
			ContractCode.C309, ContractCode.C389),
	
	PE202 (ContractModel.PE202, 
			ContractCode.C150, ContractCode.C100,
			ContractCode.C450, ContractCode.C401,
			ContractCode.C402, ContractCode.C410,
			ContractCode.C421, ContractCode.C420,
			ContractCode.C990),
	
	PE221_GT_45(ContractModel.PE221, 
			ContractCode.C150, ContractCode.C250),
	
	PE221_16_30(ContractModel.PE221, 
			ContractCode.C150, ContractCode.C250),

	PE213_SHOE(ContractModel.PE213, 
			ContractCode.C150, ContractCode.C250, 
			ContractCode.C350),
	
	PE213_T0Y_FURNITURE(ContractModel.PE213, 
			ContractCode.C150, ContractCode.C250, 
			ContractCode.C350),
					
	PE218_PE218_ORDINARY(ContractModel.PE218),
	PE218_PE218_EXCLUSIVE(ContractModel.PE218),
	;
	
	private ContractModel model;
	private ContractCode [] codes;
	
	private ContractType(ContractModel model, ContractCode... codes) {
		this.model = model;
		this.codes = codes;
	}
	
	@Override
	public String getName(Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
