package com.code.aon.ui.form;

import java.io.Serializable;

import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class DataScrollerState implements ITemplateController, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String beanName;
	
	private Integer pageLimit;
	
	private int page;
	
	private DataModel model;
	
	public DataScrollerState() {
		setPage(1);
	}

	public DataScrollerState( DataModel model, String beanName ) {
		this();
		setModel(model);
		setBeanName(beanName);
	}
	
	@Override
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}	
	
	@Override
	public Integer getPageLimit() {
		if ( pageLimit == null ) {
			return AonUtil.getConfigurationController().getPageLimit();
		}
		return pageLimit;
	}
	
	public void setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
	}	

	@Override
	public int getPage() {
		return page;
	}

	@Override
	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public DataModel getModel() throws ManagerBeanException {
		return model;
	}
	
	public DataModel getDirectModel() {
		return model;
	}

	public void setModel(DataModel model) {
		setPage(1);
		this.model = model;
	}
	
}
