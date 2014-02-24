package com.code.aon.ui.form;

import com.code.aon.ui.util.AonUtil;

public abstract class BasicTemplateController implements ITemplateController {

	private String beanName;
	
	private Integer pageLimit;
	
	private int page;
	
	public BasicTemplateController() {
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
	public int getPageLimit() {
		if ( pageLimit == null ) {
			return AonUtil.getConfigurationController().getPageLimit();
		}
		return pageLimit;
	}
	
	public void setPageLimit(int pageLimit) {
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
	
}
