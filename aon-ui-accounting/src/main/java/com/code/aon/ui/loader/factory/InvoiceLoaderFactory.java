package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedInvoice;

public class InvoiceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String FRA = "FRA";
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(FRA,"id"			,0,6	,true	,null)
		,new Column(FRA,"serie"			,2,5	,true	,null)
		,new Column(FRA,"numero"		,0,6	,true	,null)
		,new Column(FRA,"referencia"	,2,32	,true	,null)
		,new Column(FRA,"cuenta"		,2,9	,true	,null)
		,new Column(FRA,"documento"		,2,16	,true	,null)
		,new Column(FRA,"tipoDocumento"	,4,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(FRA,"paisDocumento"	,2,2	,true	,null)
		,new Column(FRA,"razonSocial"	,2,128	,true	,null)
		,new Column(FRA,"fechaFactura"	,3,10	,true	,null)
		,new Column(FRA,"fechaIva"		,3,10	,false	,null)
		,new Column(FRA,"tipo"			,0,1	,true	,new int[] {0,1,2,3})
		,new Column(FRA,"inversion"		,0,1	,false	,null)
		,new Column(FRA,"transaccion"	,0,1	,false	,new int[] {0,1,2,3})
		,new Column(FRA,"comentario"	,2,256	,false	,null)
		,new Column(FRA,"baseImponible"	,1,17	,true	,null)
		,new Column(FRA,"totalCuotaIVA"	,1,17	,true	,null)
		,new Column(FRA,"totalCuotaIRPF",1,17	,true	,null)
		,new Column(FRA,"totalFactura"	,1,17	,true	,null)
	};

	private Map<String, Column[]> columns;

	public InvoiceLoaderFactory() {
	}
	public InvoiceLoaderFactory(ILoaderIdCache cache) {
	}
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,FRA);
	}

	@Override
	public String getKey() {
		return FRA;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedInvoice getTargetBean() {
		return new LoadedInvoice();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedInvoice loaded = (LoadedInvoice) loadedPojo;
		return params.getLoaderUtils().insertInvoice(loaded);
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		return bean.get(id);
	}
}
