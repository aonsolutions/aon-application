package com.code.aon.ui.form;

import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class DataScrollerState implements ITemplateController {

	private String beanName;
	
	private Integer pageLimit;
	
	private int page;
	
	private DataModel model;
	
	public DataScrollerState() {
		setPage(1);
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
