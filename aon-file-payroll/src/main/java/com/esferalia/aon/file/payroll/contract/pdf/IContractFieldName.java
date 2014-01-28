package com.esferalia.aon.file.payroll.contract.pdf;

import com.code.aon.common.enumeration.IStringEnum;

public interface IContractFieldName extends IStringEnum{
	public boolean isOverridable();
	public boolean isCheck();
	public IContractFieldName[] getCompositeValues();
}