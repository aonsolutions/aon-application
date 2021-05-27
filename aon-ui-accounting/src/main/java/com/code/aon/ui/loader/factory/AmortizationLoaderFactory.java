package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.InvestAsset;
import com.code.aon.company.enumeration.InvestAssetRegime;
import com.code.aon.company.enumeration.InvestAssetType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAmortization;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class AmortizationLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(AMC,"codigo"		,0,10	,true	,null)  // codigo (solo para enlace de las lineas dentro del fichero de carga)
		,new Column(AMC,"fechaInicio"	,3,10	,true	,null)  // Fecha inicio amortizacion
		,new Column(AMC,"descripcion"	,2,64	,true	,null)  // Descripción
		,new Column(AMC,"periodo"		,0,1	,true	,new int[] {0,1,2,3,4,5})  // Periodo 0..5 (Anual, Mensual, Bimensual, Trimestral, Cuatrimestral, Semestral)
		,new Column(AMC,"coeficiente" 	,1,17	,true	,null)  // Coeficiente (%)
		,new Column(AMC,"importe"	 	,1,17	,true	,null)  // Importe 
		,new Column(AMC,"cuentaInm"		,2,9	,true	,null)  // Cuenta contable inmovilizado
		,new Column(AMC,"cuentaAcu"		,2,9	,true	,null)  // Cuenta contable amortizacion acumulada
		,new Column(AMC,"cuentaDot"		,2,9	,true	,null)  // Cuenta contable dotación
		,new Column(AMC,"codigoActividad",2,3   ,false  ,null)  // Código actividad		 
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public AmortizationLoaderFactory() {
	}
	
	public AmortizationLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,AMC);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAmortization.class);
	}

	@Override
	public String getKey() {
		return AMC;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAmortization getTargetBean() {
		return new LoadedAmortization();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Amortization.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAmortization loaded = (LoadedAmortization) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Amortization.class);
		
		Amortization amor = new Amortization();		
		amor.setInitialDate(loaded.getFechaInicio());
		amor.setDescription(loaded.getDescripcion());
		amor.setFeePeriod(AmortizationPeriod.values()[loaded.getPeriodo()]);
		amor.setAmount(loaded.getImporte());
		
		LoaderUtils lu = new LoaderUtils();
		Account account = null; 
		
		account = lu.ensureAccount(loaded.getCuentaInm(), "");
		amor.setFixedAssetAccount(account);
		
		account = lu.ensureAccount(loaded.getCuentaAcu(), "");
		amor.setAccumulatedAccount(account);
		
		account = lu.ensureAccount(loaded.getCuentaDot(), "");
		amor.setAllocationAccount(account);
		
		// Se pone el porcentaje en un objeto de la clase AmortizationType, por que de ahí lo coge al hacer el insert
		AmortizationType at = new AmortizationType();
		at.setPercentage(loaded.getCoeficiente());
		amor.setAmortizationType(at);
		
		// Actividad: Si se indica actividad, es necesario crear un bien afecto, que es 
		// donde va la actividad y asignar el bien afecto creado a la amortizacion
		if (StringUtils.isNotBlank(loaded.getCodigoActividad())) {
			
			EnterpriseActivity activity = (EnterpriseActivity) engine.getAonEntity(ILoaderFactory.ACT, loaded.getCodigoActividad());
			if (activity  == null) {
				throw new AonException("La actividad con codigo " + loaded.getCodigoActividad() + " no existe.");
			}
					
			InvestAsset iv = new InvestAsset();
			iv.setActivity(activity);
			iv.setDescription(loaded.getDescripcion());
			
			// Estos dos campos son obligatorios, como no se leen en la carga de 
			// datos se pone por defecto los primeros valores que tienen 
			iv.setRegime(InvestAssetRegime.values()[0]);
			iv.setType(InvestAssetType.values()[0]);
			
			// Tambien le ponemos el 100% en los % Afectación
			iv.setVatPercent(100);
			iv.setRetentionPercent(100);
			
			IManagerBean beanIV = BeanManager.getManagerBean(InvestAsset.class);
			iv = (InvestAsset) beanIV.insert(iv);
			
			amor.setInvestAsset(iv);
			
		}		
		
		amor = (Amortization) bean.insert(amor);
		return amor.getId();
		
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
