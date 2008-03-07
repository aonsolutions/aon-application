package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;
import javax.faces.validator.LengthValidator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.xml.sax.SAXException;

import com.code.aon.cms.Config;
import com.code.aon.cms.TemplateObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FileUtil;
import com.code.aon.ui.cms.util.XMLHandler;
import com.code.aon.ui.cms.util.ZipUtil;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;

public class TemplateController extends GridController implements Constants {

	private ListDataModel templates = new ListDataModel();
	
	protected SAXParser saxParser;

    private int percent = -1;

	/** A list that contains the selected objects of the model. */
	private ArrayList<TemplateObject> checkList= new ArrayList<TemplateObject>();

	public TemplateController() {
		initSAX();
	}

	public void onInit(ActionEvent event) throws ManagerBeanException {
		loadTemplates();
	}

	public ListDataModel getTemplates() {
		if (templates == null) {
			loadTemplates();
		}
		return templates;
	}

	private void loadTemplates() {
		ArrayList<TemplateObject> list = new ArrayList<TemplateObject>();
		try {
			checkList = new ArrayList<TemplateObject>();
			list = getTemplateList();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		templates = new ListDataModel(list);
	}

	private ArrayList<TemplateObject> getTemplateList() throws ManagerBeanException {
		TemplateFileFilter tff = new TemplateFileFilter();
		ArrayList<TemplateObject> list = new ArrayList<TemplateObject>();
		String template_path = ControllerUtil.getTemplatePath(); 
		File dir = new File(template_path);
		if (!dir.exists()) dir.mkdirs();
		File[] dirs = dir.listFiles();
		int count = 0;
		for (int i = 0; i < dirs.length; i++) {
			File f = dirs[i];
			TemplateObject to = new TemplateObject();
			if (f.isDirectory()) {
				File[] files = f.listFiles(tff);
				if (files.length > 0) {
					File config = files[0];
					to.setId(f.getName());
					try {
						XMLHandler handler = new XMLHandler();
						saxParser.parse(new FileInputStream(config), handler);
						HashMap<String, String> map = handler.getDataMap();
						to.setName(map.get(TEMPLATE_NAME));
						to.setVersion(map.get(TEMPLATE_VERSION));
						to.setAuthor(map.get(TEMPLATE_AUTHOR));
						to.setCreationDate(map.get(TEMPLATE_DATE));
						to.setDefaultTemplate(isDefaultTemplate(to));
						list.add(to);
						++count;
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return list;
	}

	private boolean isDefaultTemplate(TemplateObject to) throws ManagerBeanException {
		return to.getId().equals(ControllerUtil.getCurrentConfig().getTemplate());
	}

	public void defaultTemplateChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		TemplateObject to = (TemplateObject) templates.getRowData();
		updateDefaultTemplate(to);
		loadTemplates();
	}
	private void updateDefaultTemplate(TemplateObject to) throws ManagerBeanException {
		Config c = ControllerUtil.getCurrentConfig();
		c.setTemplate(to.getId());
		ControllerUtil.getConfigController().getManagerBean().update(c);
	}

	public void onNewTemplate(ActionEvent event) throws ManagerBeanException {
		percent = -1;
	}

	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 */
	public boolean getRowChecked() {
		ITransferObject to = (ITransferObject) templates.getRowData();
		return checkList.contains( to );
	}
	
	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked the row checked
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			TemplateObject to = (TemplateObject)templates.getRowData();
			if (!checkList.contains( to )) {
				checkList.add( to );
			}
		} else {
			TemplateObject to = (TemplateObject)templates.getRowData();
			if (checkList.contains( to )) {
				checkList.remove( to );
			}
		}
	}

	/**
	 * Removes all the selected objects.
	 * 
	 * @param event the event
	 */
	public void onRemoveSelected(ActionEvent event){
		for (TemplateObject to: checkList) {
			FileUtil.delete(ControllerUtil.getTemplatePath() + "/" + to.getId());
		}
		loadTemplates();
	}

	/**
	 * Gets the check list empty status.
	 * 
	 * @return the check list is empty or not
	 */
	public boolean isChecklistEmpty() {
		return (this.checkList.size() <= 0);
	}

	/**
	 * Empty the check list
	 * 
	 */
	public void clearCheckList() {
		checkList= new ArrayList<TemplateObject>();
	}
	
	public String getUploadDirectory() {
		String temporal_path = ControllerUtil.getTemporalPath();
		File dir = new File(temporal_path);
		if (!dir.exists()) dir.mkdirs();
		return temporal_path;
	}
	
	public int getPercent() {
		return percent;
	}
	
	private void initSAX() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		try {
			saxParser = saxParserFactory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private class TemplateFileFilter implements FileFilter {
	    public boolean accept(File f) {
	        return (f.isFile() && f.getName().toLowerCase().equals(TEMPLATE_DETAILS_FILE));
	    }
	}

	private UploadedFile inputFile;
	private long maximumSize = -1;;

	public UploadedFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(UploadedFile inputFile) {
		this.inputFile = inputFile;
	}
	
	public void fileUploaded( ActionEvent event ) throws IOException {
		if ( this.inputFile!= null ) {
			long size = this.inputFile.getSize();
			String upload_name = inputFile.getName();
			String separator = "/";
			if (upload_name.lastIndexOf(separator) < 0) separator = "\\";
	        upload_name = upload_name.substring(upload_name.lastIndexOf(separator));
			File file = new File( getUploadDirectory()+File.separator+upload_name);
			if ( (maximumSize != -1) && (size > maximumSize) ) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				FacesMessage message = AonUtil.getMessage( ctx,
						LengthValidator.MAXIMUM_MESSAGE_ID, new Object[]{maximumSize, upload_name} );
				ctx.addMessage(AonUtil.AON_ERROR, message);
			} else {
				byte[] data = this.inputFile.getBytes();
		        FileOutputStream outputStream = new FileOutputStream(file);
		        outputStream.write(data);
		        outputStream.close();
				if (inputFile.getContentType().indexOf("zip") >= 0) {
					ZipUtil.uncompressZipFile(file.getAbsolutePath(), ControllerUtil.getTemplatePath(), TEMPLATE_DETAILS_FILE);
					FileUtil.delete(file.getAbsolutePath());
				}
			}
		}
		try {
			this.onInit(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error loading templates: "+e.getMessage());
		}
	}
}