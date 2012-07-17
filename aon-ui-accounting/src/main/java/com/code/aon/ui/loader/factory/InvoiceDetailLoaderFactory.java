package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedInvoiceDetail;

public class InvoiceDetailLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String DET = "DET";
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(DET,"factura"			,0,6	,true	,null)
		,new Column(DET,"linea"				,0,6	,true	,null)
		,new Column(DET,"articulo"			,2,15	,true	,null)
		,new Column(DET,"concepto"			,2,64	,true	,null)
		,new Column(DET,"cantidad"			,1,16	,true	,null)
		,new Column(DET,"precio"			,1,16	,true	,null)
		,new Column(DET,"baseImponible"		,1,17	,true	,null)
		,new Column(DET,"porcentajeIva"		,1,6	,true	,null)
		,new Column(DET,"cuotaIva"			,1,16	,true	,null)
		,new Column(DET,"re"				,1,6	,true	,null)
		,new Column(DET,"cuotaRe"			,1,16	,false	,null)
		,new Column(DET,"porcentajeIrpf"	,1,6	,true	,null)
		,new Column(DET,"cuotaIrpf"			,1,17	,false	,null)
		,new Column(DET,"tipoDeduccionIva"	,0,1	,false	,new int[] {0,1})
		,new Column(DET,"tipoIrpf"			,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(DET,"cuenta"			,2,9	,false	,null)
		,new Column(DET,"cuentaIva"			,2,9	,false	,null)
		,new Column(DET,"cuentaIrpf"		,2,9	,false	,null)
	};
	
	private Map<String, Column[]> columns;
	private ILoaderIdCache cache;
	
	public InvoiceDetailLoaderFactory(ILoaderIdCache cache) {
		this.cache = cache;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,DET);
	}

	@Override
	public String getKey() {
		return DET;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedInvoiceDetail getTargetBean() {
		return new LoadedInvoiceDetail();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedInvoiceDetail loaded = (LoadedInvoiceDetail) loadedPojo;
		return params.getLoaderUtils().insertInvoiceDetail(cache,loaded);
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		return bean.get(id);
	}
}
