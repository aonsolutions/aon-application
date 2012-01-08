// AON-TEST-ENTITY ${date}
package com.esferalia.aon.test;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.ITransferObject;
import com.code.aon.entity.test.ITestEntity;

import ${aonPackage}.${aonEntity};

public class TestEntity${aonEntity} implements ITestEntity<${aonEntity}> {
	
	public void list() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		bean.getList(null);
	}		
		
	public void insert() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		//${aonEntity} to = (${aonEntity}) bean.createNewTo();
		${aonEntity} to = new ${aonEntity}();
		fillData(to);
		to = (${aonEntity}) bean.insert(to);
	<#if aonEntity == "Account">
		to = new ${aonEntity}();
		fillData(to);
		to = (${aonEntity}) bean.insert(to);
		to = new ${aonEntity}();
		fillData(to);
		to = (${aonEntity}) bean.insert(to);
		to = new ${aonEntity}();
		fillData(to);
		to = (${aonEntity}) bean.insert(to);
		to = new ${aonEntity}();
		fillData(to);
		to = (${aonEntity}) bean.insert(to);
	</#if>
	}
			
	public void update() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		${aonEntity} to = getEntity();
		to = (${aonEntity}) bean.update(to);
	}
					
	public void remove() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		${aonEntity} to = getEntity();
		bean.remove(to);
	}				
	
	public ${aonEntity} getEntity() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(${aonEntity}.class);
		List<ITransferObject> list = bean.getList(null);
		${aonEntity} to = (${aonEntity}) list.get(0);
		return to;		
	}
	
	public void fillData(${aonEntity} to) throws ManagerBeanException {
	<#list setters as setter>
		${setter}
	</#list>
	<#if aonEntity == "Account">
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		int count = bean.getCount(null);
		if (count==0) {
			to.setCode("1");
		} else if (count==1) {
			to.setCode("11");
		} else if (count==2) {
			to.setCode("111");
		} else if (count==3) {
			to.setCode("1111");
		} else {
			to.setCode("111111111");
		}
		to.setEntryEnabled(count==5);
	</#if>
	<#if aonEntity == "AmortizationType">
		IManagerBean bean = BeanManager.getManagerBean(com.code.aon.account.Account.class);
		com.code.aon.ql.Criteria criteria = new com.code.aon.ql.Criteria();
		criteria.addEqualExpression(bean.getFieldName(com.esferalia.aon.entity.IEntityAlias.ACCOUNT_CODE), "1111");
		com.code.aon.account.Account account = (com.code.aon.account.Account) bean.getList(criteria).get(0);
		to.setFixedAssetAccount(account);
		to.setAllocationAccount(account);
		to.setAccumulatedAccount(account);
	</#if>
	<#if aonEntity == "Series">
		to.setId("2012");
	</#if>
	}
}
