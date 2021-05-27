package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedModulesDetail;

public class ModulesDetailLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(MOL,"id"		,0,4	,true	,null)  // id (para enlace con la cabecera en el fichero de carga)
		,new Column(MOL,"clave"	  	,2,5	,true	,null)  // Clave de información (FiscalActivityInfoKey)
		,new Column(MOL,"linea"		,0,4	,true	,null)  // Número de linea
		,new Column(MOL,"tipo"		,0,2	,true	,null)  // Tipo de linea (FiscalActivityInfoType)
		,new Column(MOL,"valor"	  	,2,25	,false	,null)  // Valor de la informacion
		,new Column(MOL,"factor" 	,1,17	,false	,null)  // Factor 
		,new Column(MOL,"base" 		,1,17	,false	,null)  // Rendimiento neto
		,new Column(MOL,"unidades" 	,2,25	,false	,null)  // Unidades
	};
	
	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	
	public ModulesDetailLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,MOL);
	}
	
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedModulesDetail.class);
	}

	@Override
	public String getKey() {
		return MOL;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedModulesDetail getTargetBean() {
		return new LoadedModulesDetail();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
		return bean.get(id);
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		return null;
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedModulesDetail loaded = (LoadedModulesDetail) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivityInfo.class);
		FiscalActivityInfo detail = new FiscalActivityInfo();
		
		FiscalActivity fa = (FiscalActivity) engine.getAonEntity(ILoaderFactory.MOC, loaded.getId().toString());
		if (fa  == null) {
			throw new AonException("La linea de módulos con id " + loaded.getId() + " no existe.");
		}
		detail.setFiscalActivity(fa);
		detail.setInfoKey(FiscalActivityInfoKey.valueOf(loaded.getClave()));
		detail.setLine(loaded.getLinea());
		detail.setType(FiscalActivityInfoType.values()[loaded.getTipo()]);
		detail.setValue(loaded.getValor());
		detail.setFactor(loaded.getFactor());
		detail.setBase(loaded.getBase());
		detail.setUnit(loaded.getUnidades());
		
		detail = (FiscalActivityInfo) bean.insert(detail);

		return detail.getId();
	}	

	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
