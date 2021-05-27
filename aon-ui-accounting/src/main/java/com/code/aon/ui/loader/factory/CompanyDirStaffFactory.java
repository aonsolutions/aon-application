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
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCompanyDirStaff;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyDirStaffFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(REP,"documento"		,2,16	,true	,null)             // Documento
		,new Column(REP,"nombre"		,2,128	,true	,null)             // Nombre
		,new Column(REP,"socio"			,0,1	,true	,new int[] {0,1})  // Socio
		,new Column(REP,"porcentaje"	,1,17	,false	,null)   		   // Importe
	};
	
	//private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public CompanyDirStaffFactory() {
	}
	
	public CompanyDirStaffFactory(ILoaderEngine engine) {
		//this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,REP);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCompanyDirStaff.class);
	}

	@Override
	public String getKey() {
		return REP;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCompanyDirStaff getTargetBean() {
		return new LoadedCompanyDirStaff();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		
		// Obtener el registro de Company del dominio
		IManagerBean beanCompany = BeanManager.getManagerBean(Company.class);
		
		Criteria criteriaCompany = new Criteria();            
        criteriaCompany.addEqualExpression(beanCompany.getFieldName(IEntityAlias.COMPANY_DOMAIN), DomainManager.getCurrentDomain());
        List<ITransferObject> listCompany = beanCompany.getList(criteriaCompany);
        
        Company company = (Company) listCompany.get(0);
		
		LoadedCompanyDirStaff loaded = (LoadedCompanyDirStaff) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(RegistryDirStaff.class);
		
		// Si el registro ya existe (segun el NIF) se actualiza, sino se añade
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_REGISTRY_ID  ), company.getRegistry().getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DOCUMENT ), loaded.getDocumento() ); 
        
        List<ITransferObject> list = bean.getList(criteria);
        
        RegistryDirStaff rds = null;
        if (list==null || list.size()==0) {
        	rds = new RegistryDirStaff();        	
        }
        else {
        	rds = (RegistryDirStaff) list.get(0);
        }
		rds.setRegistry(company.getRegistry());
		rds.setDocument(loaded.getDocumento());
		rds.setName(loaded.getNombre());
		rds.setShareHolder(loaded.esSocio());
		rds.setPercentShare(loaded.getPorcentaje());
		
		rds = (RegistryDirStaff) bean.insertOrUpdate(rds);
		return rds.getId();	
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
