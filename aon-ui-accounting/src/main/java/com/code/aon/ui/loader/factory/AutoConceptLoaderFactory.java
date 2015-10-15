package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.AutoConcept;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAutoConcept;
import com.esferalia.aon.entity.IEntityAlias;

public class AutoConceptLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(CON,"descripcion",2,32,true,null)  // Descripción		
	};
	
	//private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public AutoConceptLoaderFactory() {
	}
	
	public AutoConceptLoaderFactory(ILoaderEngine engine) {
		//this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,CON);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAutoConcept.class);
	}

	@Override
	public String getKey() {
		return CON;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAutoConcept getTargetBean() {
		return new LoadedAutoConcept();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(AutoConcept.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		
		LoadedAutoConcept loaded = (LoadedAutoConcept) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AutoConcept.class);
		
		// Si el concepto ya existe, no se hace nada
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AUTO_CONCEPT_DESCRIPTION), loaded.getDescripcion());
        
        List<ITransferObject> list = bean.getList(criteria);
        
        AutoConcept ac = null;
        if (list==null || list.size()==0) {
        	ac = new AutoConcept();
        	ac.setDescription(loaded.getDescripcion());
    		ac = (AutoConcept) bean.insert(ac);    		
        }
        else {
        	ac = (AutoConcept) list.get(0);
        }
		return ac.getId();		
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
