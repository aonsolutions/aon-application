package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollCollectionsController;

public class ContractModelController {

	private DataModel model;

	public DataModel getModel() {
		if (model == null) {
			PayrollCollectionsController pcc = (PayrollCollectionsController) AonUtil.getRegisteredBean("payrollCollections");
			model = new ListDataModel( pcc.getContractModels() ); 
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
		SelectItem item = (SelectItem) getModel().getRowData();
		ContractModel cm = (ContractModel) item.getValue();
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, IPayrollConstants.MODEL_PATH, cm + ".pdf");
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
			SelectItem item = (SelectItem) getModel().getRowData();
			ContractModel cm = (ContractModel) item.getValue();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = Classpath.search(cl, IPayrollConstants.MODEL_PATH, cm + ".pdf");
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
