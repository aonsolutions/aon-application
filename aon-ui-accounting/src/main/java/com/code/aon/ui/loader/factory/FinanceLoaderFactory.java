package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Finance;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedFinance;

public class FinanceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String VTO = "VTO";
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(VTO,"id"							,0,6	,true	,null)
		,new Column(VTO,"factura"						,0,6	,false	,null)
		,new Column(VTO,"tipo"							,2,1	,false	,null)
		,new Column(VTO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(VTO,"paisDocumento"					,2,2	,true	,null)
		,new Column(VTO,"documento"						,2,16	,true	,null)
		,new Column(VTO,"razonSocial"					,2,64	,true	,null)
		,new Column(VTO,"importe"						,1,10	,false	,null)
		,new Column(VTO,"concepto"						,2,64	,false	,null)
		,new Column(VTO,"fechaVto"						,3,32	,false	,null)
		,new Column(VTO,"formaPago"						,2,32	,false	,null)
		,new Column(VTO,"cuentaBanco"					,2,23	,false	,null)
		,new Column(VTO,"cuenta"						,2,9	,false	,null)
	};

	private Map<String, Column[]> columns;
	
	private ILoaderIdCache cache;
	
	public FinanceLoaderFactory(ILoaderIdCache cache) {
		this.cache = cache;
	}

	public FinanceLoaderFactory() {
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,VTO);
	}

	@Override
	public String getKey() {
		return VTO;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedFinance getTargetBean() {
		return new LoadedFinance();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedFinance loaded = (LoadedFinance) loadedPojo;
		if (loaded.getImporte() != 0) {
			return params.getLoaderUtils().insertFinance(cache,loaded);	
		}
		return null;
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		return bean.get(id);
	}

}
