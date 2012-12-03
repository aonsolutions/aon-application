package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.code.aon.common.util.Classpath;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfFactory;
import com.esferalia.aon.file.payroll.contract.pdf.model.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.model.IContractPdfModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.UnsupportedContractModelException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractPdfWriter {
	
	private static ContractPdfWriter instance;
	private URL contractModelUrl;
	private IContractPdfModel pdfModel;
	
	private ContractPdfWriter(){
	}
	
	public static ContractPdfWriter getInstance(){
		if (instance == null) {
			instance = new ContractPdfWriter();
		}
		return instance;
	}
	
	public URL getContractModelUrl() {
		return contractModelUrl;
	}
	public void setContractModelUrl(URL contractModelUrl) {
		this.contractModelUrl = contractModelUrl;
	}
	public URL getContractModelUrl(String file) throws IOException {
		if(getContractModelUrl()==null){
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, IPayrollConstants.MODEL_PATH, file);
			contractModelUrl = urls[0];
		}
		return contractModelUrl;
	}
	public Collection<ContractPdfField> getContractPdfFields() {
		return pdfModel.getPdfFields();
	}
	public double getContractWidth() {
		return pdfModel.getContractWidth();
	}
	public double getContractHeight() {
		return pdfModel.getContractHeight();
	}
	public int getNumberOfContractPages() {
		return pdfModel.getNumberOfContractPages();
	}
	
	
	public byte[] buildPdf() {
		return pdfModel.buildPdf();
	}
	
	public void loadPdf(ContractModel model, Contract contract) throws IOException, UnsupportedContractModelException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(model.toString());
		PayrollUtils utils = new PayrollUtils();
		String tc2 = utils.getContractDataMap(contract).get(ContextVariable.TC2.getName());
		pdfModel.loadPdfFields(ContractCode.getContractCodeByValue(tc2), contract);
	}

	public void loadPdf(ContractAttachment contractPdfDraft, Contract contract) {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfModel = factory.createContractModel(contract.getModel().toString());
		pdfModel.loadPdfFields(contractPdfDraft);
	}
	
}
