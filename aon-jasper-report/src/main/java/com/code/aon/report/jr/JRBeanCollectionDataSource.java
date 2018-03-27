package com.code.aon.report.jr;

import java.util.Collection;

public class JRBeanCollectionDataSource extends net.sf.jasperreports.engine.data.JRBeanCollectionDataSource{

	public JRBeanCollectionDataSource(Collection<?> beanCollection, boolean isUseFieldDescription) {
		super(beanCollection, isUseFieldDescription);
	}

	public JRBeanCollectionDataSource(Collection<?> beanCollection) {
		super(beanCollection);
	}
	
}
