package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedEnterpriseActivity;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class EnterpriseActivityLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(ACT,"descripcion"	,2,64	,true	,null)             // Descripción
		,new Column(ACT,"principal"		,0,1	,true	,new int[] {0,1})  // Principal		
	};
	
	//private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public EnterpriseActivityLoaderFactory() {
	}
	
	public EnterpriseActivityLoaderFactory(ILoaderEngine engine) {
		//this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,ACT);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedEnterpriseActivity.class);
	}

	@Override
	public String getKey() {
		return ACT;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedEnterpriseActivity getTargetBean() {
		return new LoadedEnterpriseActivity();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseActivity.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		
		// Obtener el registro de Enterprise del dominio
		IManagerBean beanEnterprise = BeanManager.getManagerBean(Enterprise.class);
		
		Criteria criteriaEnterprise = new Criteria();            
        criteriaEnterprise.addEqualExpression(beanEnterprise.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), DomainManager.getCurrentDomain());
        List<ITransferObject> listEnterprise = beanEnterprise.getList(criteriaEnterprise);
        
        Enterprise ep = (Enterprise) listEnterprise.get(0);
		
		LoadedEnterpriseActivity loaded = (LoadedEnterpriseActivity) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseActivity.class);
		
		// Si el registro ya existe (segun la descripción) se actualiza, sino se añade
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID ), ep.getRegistry().getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_DESCRIPTION ), loaded.getDescripcion() ); 
        
        List<ITransferObject> list = bean.getList(criteria);
        
        EnterpriseActivity ea = null;
        if (list==null || list.size()==0) {
        	ea = new EnterpriseActivity();        	
        }
        else {
        	ea = (EnterpriseActivity) list.get(0);
        }
		ea.setEnterprise(ep);
		ea.setDescription(loaded.getDescripcion());
		ea.setPrincipal(loaded.esPrincipal());
		
		ea = (EnterpriseActivity) bean.insertOrUpdate(ea);
		return ea.getId();	
	}
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo)
			throws AonException {		
		return null;
	}
	
}
