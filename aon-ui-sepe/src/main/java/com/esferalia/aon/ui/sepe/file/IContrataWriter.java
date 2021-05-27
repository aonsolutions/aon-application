package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;

public interface IContrataWriter {

	Contract getContract();
	
	File createFile(IContrataParams params) throws ManagerBeanException, IOException;
	
}
