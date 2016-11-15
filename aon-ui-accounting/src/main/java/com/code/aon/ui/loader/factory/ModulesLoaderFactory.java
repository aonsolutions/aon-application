package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedModules;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class ModulesLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(MOC,"id"		  ,0,4	,true	,null)  // id (solo para enlace de las lineas dentro del fichero de carga)
		,new Column(MOC,"ejercicio"	  ,0,4	,true	,null)  // Ejercicio
	    ,new Column(MOC,"agricola"	  ,0,1	,true	,new int[] {0,1})  // Actividad Agrícola (0,1)
		,new Column(MOC,"epigrafe"	  ,2,7	,true	,null)  // Epigrafe
		,new Column(MOC,"descripcion" ,2,128,true	,null)  // Descripción
		,new Column(MOC,"maxPersonas" ,1,17	,true	,null)  // Valor máximo personas
		,new Column(MOC,"maxImporte"  ,1,17	,true	,null)  // Valor máximo importe
		,new Column(MOC,"porcentaje"  ,1,17	,true 	,null)  // Porcentaje IVA aplicable
		,new Column(MOC,"codigoActividad",2,3,true	,null)  // Código actividad
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public ModulesLoaderFactory() {
	}
	
	public ModulesLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,MOC);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedModules.class);
	}

	@Override
	public String getKey() {
		return MOC;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedModules getTargetBean() {
		return new LoadedModules();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedModules loaded = (LoadedModules) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
		FiscalActivity fa = new FiscalActivity();
		
		fa.setYear(loaded.getEjercicio());
		fa.setFarmer(loaded.esAgricola());
		fa.setEpigraph(loaded.getEpigrafe());
		fa.setDescription(loaded.getDescripcion());
		fa.setMaxPerson(loaded.getMaxPersonas());
		fa.setMaxImport(loaded.getMaxImporte());
		fa.setVatPercent(loaded.getPorcentaje());
		
		EnterpriseActivity activity = (EnterpriseActivity) engine.getAonEntity(ILoaderFactory.ACT, loaded.getCodigoActividad());
		if (activity  == null) {
			throw new AonException("La actividad con codigo " + loaded.getCodigoActividad() + " no existe.");
		}
	
		fa.setActivity(activity);
		
		fa = (FiscalActivity) bean.insert(fa);
		return fa.getId();
		
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
