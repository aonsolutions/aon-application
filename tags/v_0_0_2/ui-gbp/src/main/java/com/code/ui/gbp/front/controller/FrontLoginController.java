package com.code.ui.gbp.front.controller;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Supplier;
import com.code.gbp.dao.IGBPAlias;

public class FrontLoginController {
	
	private String login;
	
	private String password;
	
	private Supplier supplier;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@SuppressWarnings("unchecked")
	public String validate(){
		try {
			IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supplierBean.getFieldName(IGBPAlias.SUPPLIER_DOCUMENT), getLogin());
			Iterator iter = supplierBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				setSupplier((Supplier)iter.next());
				if(supplier.getPassWord().equals(getPassword())){
					return "valid";
				}else{
					setSupplier(null);
					setPassword(null);
					AonUtil.addErrorMessage("Incorrect Password");
					return null;
				}
			}else{
				AonUtil.addErrorMessage("User name not found");
				setLogin(null);
				setPassword(null);
				return null;
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			return null;
		}
	}
}