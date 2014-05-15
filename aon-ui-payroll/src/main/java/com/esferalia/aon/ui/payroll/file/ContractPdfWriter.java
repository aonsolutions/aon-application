package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;

import javax.faces.context.FacesContext;

import com.code.aon.AonVersion;
import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfFactory;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;


public class ContractPdfWriter implements Serializable  {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private URL contractDocumentUrl;
	private IContractPdfDocument pdfDocument;
	
	public IContractPdfDocument getPdfDocument(){
		return pdfDocument;
	}
	
	public URL getContractDocumentUrl() {
		return contractDocumentUrl;
	}
	public void setContractDocumentUrl(URL contractDocumentUrl) {
		this.contractDocumentUrl = contractDocumentUrl;
	}
	public URL getContractDocumentUrl(String file) throws IOException, UnsupportedContractDocumentException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, pdfDocument.getDocumentPath(), file);
		if(urls.length == 0) {
			String msg = "Nombre del fichero incorrecto. No se ha podido hallar la ruta especificada.";
			AonUtil.addErrorMessage(msg);
			throw new UnsupportedContractDocumentException(msg);
		}
		contractDocumentUrl = urls[0];
		return contractDocumentUrl;
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
	
	
	public byte[] buildPdf(boolean readOnly) {
		return pdfDocument.buildPdf(readOnly);
	}
	
	public void loadNewPdf(ContractModel model, Contract contract, IContrataParams contrataParams) throws IOException, UnsupportedContractDocumentException {
		loadNewPdf(model.toString(), contract, contrataParams);
	}
	public void loadNewPdf(String document, Contract contract, IContrataParams contrataParams) throws IOException, UnsupportedContractDocumentException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		if(pdfDocument == null) {
			String msg = "Documento incorrecto. No se ha podido hallar la factoria correspondiente a este tipo de documento";
			AonUtil.addErrorMessage(msg);
			throw new UnsupportedContractDocumentException(msg);
		}
		ContractUtils utils = ContractUtils.getInstance();
		String tc2 = utils.getContractDataMap(contract).get(ContextVariable.TC2.getName());
		pdfDocument.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		pdfDocument.loadPdfFieldValues(ContractCode.getContractCodeByValue(tc2), contract, contrataParams);
	}

	public void loadExistingPdf(ContractAttachment contractPdfDraft, Contract contract) throws UnsupportedContractDocumentException {
		loadExistingPdf(contract.getModel().toString(), contractPdfDraft);
	}
	public void loadExistingPdf(String document, ContractAttachment contractPdfDraft) throws UnsupportedContractDocumentException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		if(pdfDocument == null) {
			String msg = "Documento incorrecto. No se ha podido hallar la factoria correspondiente a este tipo de documento";
			AonUtil.addErrorMessage(msg);
			throw new UnsupportedContractDocumentException(msg);
		}
		pdfDocument.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		pdfDocument.loadPdfFields(contractPdfDraft);
	}
	
}
