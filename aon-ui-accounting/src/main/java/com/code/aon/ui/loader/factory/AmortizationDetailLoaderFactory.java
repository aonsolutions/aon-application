package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAmortizationDetail;

public class AmortizationDetailLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(AML,"codigo"		,0,10	,true	,null)  // Código (para enlace con la cabecera en el fichero de carga)
		,new Column(AML,"fechaDesde"	,3,10	,true	,null)  // Fecha Desde
		,new Column(AML,"fechaHasta"	,3,10	,true	,null)  // Fecha Hasta
		,new Column(AML,"coeficiente"	,1,6	,true	,null)  // Coeficiente (%)
		,new Column(AML,"dotacionContable" ,1,17,true	,null)  // Dotación Contable
		,new Column(AML,"dotacionFiscal"   ,1,17,true	,null)  // Dotación Fiscal
		,new Column(AML,"estado"		,0,1	,true	,new int[] {0,2})  // Estado 0-Pendiente, 2-Bloqueado
	};
	
	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	
	public AmortizationDetailLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,AML);
	}
	
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAmortizationDetail.class);
	}

	@Override
	public String getKey() {
		return AML;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAmortizationDetail getTargetBean() {
		return new LoadedAmortizationDetail();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(AmortizationDetail.class);
		return bean.get(id);
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		return null;
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAmortizationDetail loaded = (LoadedAmortizationDetail) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AmortizationDetail.class);
		AmortizationDetail detail = new AmortizationDetail();
		
		Amortization amor = (Amortization) engine.getAonEntity(ILoaderFactory.AMC, loaded.getCodigo());
		if (amor  == null) {
			throw new AonException("La linea de amortizacion con codigo " + loaded.getCodigo() + " no existe.");
		}
		detail.setAmortization(amor);
		detail.setFromDate(loaded.getFechaDesde());
		detail.setToDate(loaded.getFechaHasta());
		detail.setCoefficient(loaded.getCoeficiente());
		detail.setAllocation(loaded.getDotacionContable());
		detail.setFiscalAllocation(loaded.getDotacionFiscal());
		detail.setStatus(AmortizationDetailStatus.values()[loaded.getEstado()]);

		detail = (AmortizationDetail) bean.insert(detail);

		return detail.getId();
	}	

	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
