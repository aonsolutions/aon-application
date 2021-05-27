package com.code.aon.fiscal.activity;

import java.util.List;

import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;

public interface IFiscalActivityContainer {
	
	FiscalActivity getFiscalActivity();
	List<FiscalActivityInfo> getActivityInfoList();
	List<FiscalActivityInfo> getIrpfModulesList();
	List<FiscalActivityInfo> getIrpfInfoList();
	List<FiscalActivityInfo> getVatModulesList();
	List<FiscalActivityInfo> getVatInfoList();
	List<FiscalActivityInfo>  getModulesDetailList();
	FiscalActivityInfo getInfoToDetail();
	
}
