package com.code.aon.ui.finance.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.file.bank.model.CSB19.data.Lot;
import com.code.aon.file.bank.model.SEPA.SEPA19_14CoreXml;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.FinanceBatch;

public class SEPA19_14CoreXmlWriter {

	@SuppressWarnings("rawtypes")
	public FileOutput createXml(Company company, FinanceBatch fBatch, Collection fbatchDetailCollection) throws ManagerBeanException {
		AEB19Writer aeb19Writer = new AEB19Writer();
		Lot lot = aeb19Writer.getLot(company, fBatch, fbatchDetailCollection);
		try {
			File file = File.createTempFile("SEPA19_14_CORE_", ".xml");
			FileFiller sepa1914 = new SEPA19_14CoreXml(lot, file);
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(sepa1914.create());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}
	
}
