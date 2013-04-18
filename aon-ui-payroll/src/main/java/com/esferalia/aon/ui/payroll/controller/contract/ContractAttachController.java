package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;

public class ContractAttachController extends AttachmentController {
	
	private ArrayList<ContractAttachment> checks = new ArrayList<ContractAttachment>();
	
	private ContractAttachmentType type;
		
	private boolean show;
	
	public ContractAttachController() {
		this.show = true;
	}

	public ContractAttachmentType getType() {
		return type;
	}
	
	public void setType(ContractAttachmentType type) {
		this.type = type;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
	public int getCheckedCount(){
		return checks.size();
	}
	
	public void rowSelected(ActionEvent event) throws ManagerBeanException{
		ContractAttachment to = (ContractAttachment) getModel().getRowData();
		if (checks.contains(to)) {
			checks.remove(to);
		} else {
			checks.add(to);
		}
	}
	
	public void rowSelected(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() throws ManagerBeanException{
		ContractAttachment to = (ContractAttachment) getModel().getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) throws ManagerBeanException{
		if (rowChecked) {
			ContractAttachment to = (ContractAttachment) getModel().getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			ContractAttachment to = (ContractAttachment) getModel().getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (ITransferObject ito : this.getWrappedList()) {
			ContractAttachment o = (ContractAttachment)ito;
			if (!checks.contains(o)) {
				checks.add( o );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		checks = new ArrayList<ContractAttachment>();
	}
	
	public void onDownloadSelected(ActionEvent event){
//		for (ContractAttachment attach : checks) {
//			attach.getData();
//			
//		}
		
	
//		FacesContext context = FacesContext.getCurrentInstance();
//        String id = context.getExternalContext().getRequestParameterMap().get("index");
//        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
//        DownloadUtil.downloadAttachment( checks.get(0) );    
        
		byte[] data = mergeSelectedDocuments(); 
		
        
        InputStream in = new ByteArrayInputStream(data);
		long size = ArrayUtils.getLength(data);
		DownloadUtil.downloadAttachment("Contract-documents", MimeType.MIME_PDF, in, size);
	    
		
	}
	
	public byte[] mergeSelectedDocuments() {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Document document = null;
		PdfCopy writer = null;
		
		
		
		for (ContractAttachment attach : checks) {
//			attach.getData();
			
			try {
				PdfReader reader = new PdfReader(attach.getData());
				int numberOfPages = reader.getNumberOfPages();
				
//				if (document == null) {
					document = new Document(reader.getPageSizeWithRotation(1));
					writer = new PdfCopy(document, outStream); // new
					// FileOutputStream("C:\\Yuval@.pdf"));
					// //this.getOutputStream());
					document.open();
//				}
				PdfImportedPage page;
				for (int i = 0; i < numberOfPages;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
					
				}
				
				/**
				 * PdfReader reader = new PdfReader("existing.pdf"); Document
				 * document = new Document(reader.getPageSizeWithRotation(1));
				 * PdfCopy copy = new PdfCopy(document, new
				 * FileOutputStream(outFile)); document.open(); PdfImportedPage page =
				 * copy.getImportedPage(reader, i); copy.addPage(page);
				 * document.close();
				 */
				
				PRAcroForm form = reader.getAcroForm();
				if (form != null) {
					writer.copyAcroForm(reader);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
//		MergePdfByteArrays merger = new MergePdfByteArrays();
//		// we create a reader for a certain document
//		File docA = new File("c:\\1160895702325.pdf");
//		File docB = new File("c:\\1160895953517.pdf");
//
//		GetBytesFromFile byteFromfile = new GetBytesFromFile();
//		merger.add(byteFromfile.getBytesFromFile(docA));
//		merger.add(byteFromfile.getBytesFromFile(docB));
//		merger.close();
			
		}
		
		
		
		//tranform byteArrayOutputStream into file
		byte[] array = outStream.toByteArray();
		
		
		try {
			document.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}
	
}
