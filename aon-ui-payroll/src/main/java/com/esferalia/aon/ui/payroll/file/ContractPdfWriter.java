package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.code.aon.common.util.Classpath;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfFactory;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractPdfWriter {
	
	private static ContractPdfWriter instance;
	private URL contractModelUrl;
	private IContractPdfDocument pdfDocument;
	
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
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, pdfDocument.getDocumentPath(), file);
		contractModelUrl = urls[0];
		return contractModelUrl;
	}
	public Collection<ContractPdfField> getContractPdfFields() {
		return pdfDocument.getPdfFields();
	}
	public double getDocumentWidth() {
		return pdfDocument.getDocumentWidth();
	}
	public double getDocumentHeight() {
		return pdfDocument.getDocumentHeight();
	}
	public int getNumberOfDocumentPages() {
		return pdfDocument.getNumberOfDocumentPages();
	}
	
	
	public byte[] buildPdf() {
		return pdfDocument.buildPdf();
	}
	
	public void loadNewPdf(ContractModel model, Contract contract) throws IOException, UnsupportedContractDocumentException {
		loadNewPdf(model.toString(), contract);
	}
	public void loadNewPdf(String document, Contract contract) throws IOException, UnsupportedContractDocumentException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		PayrollUtils utils = new PayrollUtils();
		String tc2 = utils.getContractDataMap(contract).get(ContextVariable.TC2.getName());
		pdfDocument.loadPdfFields(ContractCode.getContractCodeByValue(tc2), contract);
	}

	public void loadExistingPdf(ContractAttachment contractPdfDraft, Contract contract) {
		loadExistingPdf(contract.getModel().toString(), contractPdfDraft);
	}
	public void loadExistingPdf(String document, ContractAttachment contractPdfDraft) {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		pdfDocument.loadPdfFields(contractPdfDraft);
	}
	
}
