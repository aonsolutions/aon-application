package com.code.aon.ui.sales.importer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryItem;
import com.code.aon.sales.Sales;

public interface SalesImporterHandler extends Serializable {
		
	char SEPARATOR_VALUE = '\t';
//	char SEPARATOR_VALUE = ';'; 
	
	boolean isValidFile(AonFile aonFile);
	
	void loadData(AonFile aonFile) throws IOException;
	
	void accept() throws ManagerBeanException;

	void reset();
	
	String getModuleLabel();
	
	List<Sales> getExistingSalesList();
	
	List<Sales> getImportedSalesList();
	
	List<RegistryItem> getNonExistentItems();

	List<Sales> getNonExistentSales();
	
}
