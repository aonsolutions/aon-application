package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCustomer;

public class CustomerLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String CLI = "CLI";
	private static final Column[] SUPPORTED_COLUMNS = {
			 new Column(CLI,"id"							,0,6	,true	,null)
			,new Column(CLI,"razonSocial"					,2,64	,true	,null)
			,new Column(CLI,"alias"							,2,32	,false	,null)
			,new Column(CLI,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(CLI,"paisDocumento"					,2,2	,true	,null)
			,new Column(CLI,"documento"						,2,16	,true	,null)
			,new Column(CLI,"nacionalidad"					,2,2	,true	,null)
			,new Column(CLI,"cuenta"						,2,9	,false	,null)
			,new Column(CLI,"re"							,0,1	,false	,null)
			,new Column(CLI,"transaccion"					,0,1	,false	,new int[] {0,1,2,3})
			,new Column(CLI,"retencion"						,0,1	,false	,null)
			,new Column(CLI,"facturarAlbaranesAgrupados"	,0,1	,false	,null)
			,new Column(CLI,"tipoVia"						,2,2	,false	,null)
			,new Column(CLI,"direccion"						,2,128	,false	,null)
			,new Column(CLI,"numero"						,0,6	,false	,null)
			,new Column(CLI,"direccion2"					,2,128	,false	,null)
			,new Column(CLI,"direccion3"					,2,128	,false	,null)
			,new Column(CLI,"cp"							,2,16	,false	,null)
			,new Column(CLI,"ciudad"						,2,64	,false	,null)
			,new Column(CLI,"provincia"						,2,3	,false	,null)
			,new Column(CLI,"nombreProvincia"				,2,32	,false	,null)
			,new Column(CLI,"pais"							,2,2	,false	,null)
			,new Column(CLI,"telefono1"						,2,64	,false	,null)
			,new Column(CLI,"telefono2"						,2,64	,false	,null)
			,new Column(CLI,"fax"							,2,64	,false	,null)
			,new Column(CLI,"email"							,2,64	,false	,null)
			,new Column(CLI,"web"							,2,64	,false	,null)
			,new Column(CLI,"banco"							,2,64	,false	,null)
			,new Column(CLI,"cuentaBanco"					,2,23	,false	,null)
			,new Column(CLI,"formaPago"						,2,32	,false	,null)
			,new Column(CLI,"numeroVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasAlPrimerVto"				,0,6	,false	,null)
			,new Column(CLI,"diasEntreVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasPago"						,2,8	,false	,null)
	};

	public CustomerLoaderFactory() {
	}
	public CustomerLoaderFactory(ILoaderIdCache cache) {
	}
	
	private Map<String, Column[]> columns;
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}


	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,CLI);
	}

	@Override
	public String getKey() {
		return CLI;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCustomer getTargetBean() {
		return new LoadedCustomer();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedCustomer loaded = (LoadedCustomer) loadedPojo;
		return params.getLoaderUtils().insertCustomer(loaded);
	}
	
	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		return bean.get(id);
	}
}
