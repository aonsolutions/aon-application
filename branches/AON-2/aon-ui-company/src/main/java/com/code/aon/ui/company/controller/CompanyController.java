package com.code.aon.ui.company.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the company maintenance.
 */
public class CompanyController extends CompanyParentController {
	

	/** The uploaded file. */
	private UploadedFile file;
	
	
    /**
     * The empty constructor.
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    public CompanyController() throws ManagerBeanException {
        super();
    }
    
    /**
     * Gets the uploaded file.
     * 
     * @return the file
     */
    public UploadedFile getFile() {
		return this.file;
	}

	
	/**
	 * Sets the file.
	 * 
	 * @param file the file
	 */
	public void setFile(UploadedFile file) {
		this.file = file;
	}

	
}