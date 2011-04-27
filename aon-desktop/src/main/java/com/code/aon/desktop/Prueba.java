package com.code.aon.desktop;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;

public class Prueba {

	/**
	 * @param args
	 * @throws ManagerBeanException 
	 */
	public static void main(String[] args) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Company.class);
		System.out.println( "Comapanies: " + bean.getCount(null) );
	}

}
