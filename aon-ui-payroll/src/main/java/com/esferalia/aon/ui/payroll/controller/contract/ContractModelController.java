package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel;

public class ContractModelController {

	private DataModel model;

	public DataModel getModel() {
		if (model == null) {
			List<String> list = new LinkedList<String>();
		    list.add("Indefinido");
		    list.add("Temporal");
		    list.add("Formación");
		    list.add("Prácticas");		
			list.add("PE200 - Pacto de horas complementarias");
			list.add("PE192 - Comunicación de llamamiento a la actividad de los trabajadores fijos discontinuos");
			list.add("PE191 - Comunicación de prórroga de contrato de trabajo");
			model = new ListDataModel( list ); 
			 
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public void onReset(ActionEvent event) {
		setModel(null);
	}
	
	public boolean isPdfEnabled() {
		String name = (String) getModel().getRowData();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, AbstractContractModel.CONTRACT_DOCUMENT_PATH, name + ".pdf");
			URL url = urls!= null && urls.length > 0?urls[0]:null;
			return (url != null);
		} catch (IOException e) {
			return false;
		}
	}
	
	public void onDownloadContract(ActionEvent event ) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try {
			String name = (String) getModel().getRowData();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, AbstractContractModel.CONTRACT_DOCUMENT_PATH, name + ".pdf");
			URL url = urls[0];
			InputStream is = url.openStream();
			buf = new BufferedInputStream(is);
			stream = response.getOutputStream();
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1) {
				stream.write(readBytes);
			}
		    response.setContentType(MimeType.MIME_PDF.getName()); 
			response.flushBuffer();
			context.responseComplete();
		} catch (IOException ioe) {
			
		} finally {
			IOUtils.closeQuietly(stream);
			IOUtils.closeQuietly(buf);  
		}
	}
	
}
