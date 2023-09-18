package com.esferalia.aon.ui.payroll.controller.contract;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.Classpath;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfModel;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;

public class ContractModelController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private DataModel model;
	private DataModel optionModel;
	
	private ContractPdfModel selectedModel;
	
	public ContractPdfModel getSelectedModel() {
		return selectedModel;
	}
	
	public void setSelectedModel(ContractPdfModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	public DataModel getModel() {
		if (model == null) {
			List<ContractPdfModel> list = new LinkedList<ContractPdfModel>();
			for(ContractPdfModel model: ContractPdfModel.values()){
				list.add(model);
			}
			model = new SerializableListDataModel( list ); 
			 
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public DataModel getOptionModel() {
		return optionModel;
	}

	public void setOptionModel(DataModel optionModel) {
		this.optionModel = optionModel;
	}

	public boolean isExpandable() {
		ContractPdfModel model = (ContractPdfModel) getModel().getRowData();
		return ContractPdfModel.PE200 != model && ContractPdfModel.PE192 != model && ContractPdfModel.PE191 != model;
	}

	public void onReset(ActionEvent event) {
		setModel(null);
	}
	
	public void onSelectModel(ActionEvent event ) {
		if(getSelectedModel() == getModel().getRowData()){
			optionModel = null;
			setSelectedModel(null);
		} else {
			setSelectedModel((ContractPdfModel) getModel().getRowData());
			List<ModelOption> list = new LinkedList<ModelOption>();
			for(ModelOption option: ModelOption.values()){
				if(getPdfModel(option.getPdfModel())==getSelectedModel()){
					list.add(option);
				}
			}
			optionModel = new SerializableListDataModel( list );
		}
	}
	
	public boolean isPdfEnabled() {
		String name = getPdfName((ContractPdfModel) getModel().getRowData());
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL[] urls = null;
			
			if(ContractPdfModel.PE200==getPdfModel(name)){
				urls = null;
			} else if(ContractPdfModel.PE192==getPdfModel(name)){
				urls = null;
			} else if(ContractPdfModel.PE191.toString()==name){
				urls = Classpath.search(cl, Extension.CONTRACT_EXTENSION_PATH, name + ".pdf");
			} else {
				urls = Classpath.search(cl, AbstractContractModel.CONTRACT_DOCUMENT_PATH, name + ".pdf");
			}
			
			URL url = urls!= null && urls.length > 0?urls[0]:null;
			return (url != null);
		} catch (IOException e) {
			return false;
		}
	}
	
	private String getPdfName(ContractPdfModel model){
		if(ContractPdfModel.INDEFINITE==model){
			return IndefiniteModel.MODEL_NAME;
		} else if(ContractPdfModel.LEARNING==model){
			return LearningModel.MODEL_NAME;
		} else if(ContractPdfModel.PRACTICE==model){
			return PracticeModel.MODEL_NAME;
		} else if(ContractPdfModel.TEMPORARY==model){
			return TemporaryModel.MODEL_NAME;
		} else if(ContractPdfModel.PE200==model){
			return null;
		} else if(ContractPdfModel.PE192==model){
			return null;
		} else if(ContractPdfModel.PE191==model){
			return Extension.EXTENSION_NAME;
		}
		return null;
	}
	private ContractPdfModel getPdfModel(String name){
		if(IndefiniteModel.MODEL_NAME.equals(name)){
			return ContractPdfModel.INDEFINITE;
		} else if(TemporaryModel.MODEL_NAME.equals(name)){
			return ContractPdfModel.TEMPORARY;
		} else if(PracticeModel.MODEL_NAME.equals(name)){
			return ContractPdfModel.PRACTICE;
		} else if(LearningModel.MODEL_NAME.equals(name)){
			return ContractPdfModel.LEARNING;
		}
		return null;
	}
	
	public void onDownloadContract(ActionEvent event ) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		BufferedInputStream buf = null;
		ServletOutputStream stream = null;
		try {
			String name = getPdfName((ContractPdfModel) getModel().getRowData());
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			
			URL[] urls = null;
			
			if(ContractPdfModel.PE200==getPdfModel(name)){
				urls = null;
			} else if(ContractPdfModel.PE192==getPdfModel(name)){
				urls = null;
			} else if(ContractPdfModel.PE191.toString()==name){
				urls = Classpath.search(cl, Extension.CONTRACT_EXTENSION_PATH, name + ".pdf");
			} else {
				urls = Classpath.search(cl, AbstractContractModel.CONTRACT_DOCUMENT_PATH, name + ".pdf");
			}
			
			
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
