package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedSupplier;

public class SupplierLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String PRO = "PRO";
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(PRO,"id"							,0,6	,true	,null)
		,new Column(PRO,"razonSocial"					,2,64	,true	,null)
		,new Column(PRO,"alias"							,2,32	,false	,null)
		,new Column(PRO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(PRO,"paisDocumento"					,2,2	,true	,null)
		,new Column(PRO,"documento"						,2,16	,true	,null)
		,new Column(PRO,"nacionalidad"					,2,2	,true	,null)
		,new Column(PRO,"cuenta"						,2,9	,false	,null)
		,new Column(PRO,"transaccion"					,0,1	,false	,new int[] {0,1,2,3})
		,new Column(PRO,"retencion"						,0,1	,false	,null)
		,new Column(PRO,"tipoVia"						,2,2	,false	,null)
		,new Column(PRO,"direccion"						,2,128	,false	,null)
		,new Column(PRO,"numero"						,0,6	,false	,null)
		,new Column(PRO,"direccion2"					,2,128	,false	,null)
		,new Column(PRO,"direccion3"					,2,128	,false	,null)
		,new Column(PRO,"cp"							,2,16	,false	,null)
		,new Column(PRO,"ciudad"						,2,64	,false	,null)
		,new Column(PRO,"provincia"						,2,3	,false	,null)
		,new Column(PRO,"nombreProvincia"				,2,32	,false	,null)
		,new Column(PRO,"pais"							,2,2	,false	,null)
		,new Column(PRO,"telefono1"						,2,64	,false	,null)
		,new Column(PRO,"telefono2"						,2,64	,false	,null)
		,new Column(PRO,"fax"							,2,64	,false	,null)
		,new Column(PRO,"email"							,2,64	,false	,null)
		,new Column(PRO,"web"							,2,64	,false	,null)
		,new Column(PRO,"banco"							,2,64	,false	,null)
		,new Column(PRO,"cuentaBanco"					,2,23	,false	,null)
		,new Column(PRO,"formaPago"						,2,32	,false	,null)
		,new Column(PRO,"numeroVtos"					,0,6	,false	,null)
		,new Column(PRO,"diasAlPrimerVto"				,0,6	,false	,null)
		,new Column(PRO,"diasEntreVtos"					,0,6	,false	,null)
		,new Column(PRO,"diasPago"						,2,8	,false	,null)
	};

	public SupplierLoaderFactory() {
	}
	public SupplierLoaderFactory(ILoaderIdCache cache) {
	}

	private Map<String, Column[]> columns;
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,PRO);
	}

	@Override
	public String getKey() {
		return PRO;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedSupplier getTargetBean() {
		return new LoadedSupplier();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedSupplier loaded = (LoadedSupplier) loadedPojo;
		return params.getLoaderUtils().insertSupplier(loaded);
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		return bean.get(id);
	}
}
