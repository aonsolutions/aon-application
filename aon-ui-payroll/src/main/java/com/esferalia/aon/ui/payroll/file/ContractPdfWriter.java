package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import javax.faces.context.FacesContext;

import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfFactory;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractContrataController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;


public class ContractPdfWriter {
	
	private static ContractPdfWriter instance;
	private URL contractDocumentUrl;
	private IContractPdfDocument pdfDocument;
	
	private ContractPdfWriter(){
	}
	
	public static ContractPdfWriter getInstance(){
		if (instance == null) {
			instance = new ContractPdfWriter();
		}
		return instance;
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
	
	
	public byte[] buildPdf() {
		return pdfDocument.buildPdf();
	}
	
	public void loadNewPdf(ContractModel model, Contract contract, ContrataParams contrataParams) throws IOException, UnsupportedContractDocumentException {
		loadNewPdf(model.toString(), contract, contrataParams);
	}
	public void loadNewPdf(String document, Contract contract, ContrataParams contrataParams) throws IOException, UnsupportedContractDocumentException {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		PayrollUtils utils = new PayrollUtils();
		String tc2 = utils.getContractDataMap(contract).get(ContextVariable.TC2.getName());
		pdfDocument.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		// TODO: load pdf document with contrata data
//		pdfDocument.setContrataData(obtainContrataData());
		pdfDocument.loadPdfFields(ContractCode.getContractCodeByValue(tc2), contract, contrataParams);
	}

	public void loadExistingPdf(ContractAttachment contractPdfDraft, Contract contract) {
		loadExistingPdf(contract.getModel().toString(), contractPdfDraft);
	}
	public void loadExistingPdf(String document, ContractAttachment contractPdfDraft) {
		ContractPdfFactory factory = new ContractPdfFactory();
		pdfDocument = factory.createContractDocument(document);
		pdfDocument.setLocale(FacesContext.getCurrentInstance().getViewRoot().getLocale());
		pdfDocument.loadPdfFields(contractPdfDraft);
	}
	
	private ContrataParams obtainContrataData() {
		try {
			// TODO: obtain contrata data to load pdf document
//			ContractContrataController controller = (ContractContrataController) AonUtil.getRegisteredBean("contractContrata");
//			controller.onContrataDataShow(null);
//			return controller.getParams();
		} catch (Exception e){
			// no se carga ningun dato relacionado con contrata
		}
		return null;
	}
}
