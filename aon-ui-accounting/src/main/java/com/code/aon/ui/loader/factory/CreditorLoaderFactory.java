package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.ILoaderIdCache;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCreditor;

public class CreditorLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final String ACR = "ACR";
	
	private static final Column[] SUPPORTED_COLUMNS = {
			 new Column(ACR,"id"							,0,6	,true	,null)
			,new Column(ACR,"razonSocial"					,2,64	,true	,null)
			,new Column(ACR,"alias"							,2,32	,false	,null)
			,new Column(ACR,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(ACR,"paisDocumento"					,2,2	,true	,null)
			,new Column(ACR,"documento"						,2,16	,true	,null)
			,new Column(ACR,"nacionalidad"					,2,2	,true	,null)
			,new Column(ACR,"cuenta"						,2,9	,false	,null)
			,new Column(ACR,"transaccion"					,0,1	,false	,new int[] {0,1,2,3})
			,new Column(ACR,"retencion"						,0,1	,false	,null)
			,new Column(ACR,"tipoVia"						,2,2	,false	,null)
			,new Column(ACR,"direccion"						,2,128	,false	,null)
			,new Column(ACR,"numero"						,0,6	,false	,null)
			,new Column(ACR,"direccion2"					,2,128	,false	,null)
			,new Column(ACR,"direccion3"					,2,128	,false	,null)
			,new Column(ACR,"cp"							,2,16	,false	,null)
			,new Column(ACR,"ciudad"						,2,64	,false	,null)
			,new Column(ACR,"provincia"						,2,3	,false	,null)
			,new Column(ACR,"nombreProvincia"				,2,32	,false	,null)
			,new Column(ACR,"pais"							,2,2	,false	,null)
			,new Column(ACR,"telefono1"						,2,64	,false	,null)
			,new Column(ACR,"telefono2"						,2,64	,false	,null)
			,new Column(ACR,"fax"							,2,64	,false	,null)
			,new Column(ACR,"email"							,2,64	,false	,null)
			,new Column(ACR,"web"							,2,64	,false	,null)
			,new Column(ACR,"banco"							,2,64	,false	,null)
			,new Column(ACR,"cuentaBanco"					,2,23	,false	,null)
			,new Column(ACR,"formaPago"						,2,32	,false	,null)
			,new Column(ACR,"numeroVtos"					,0,6	,false	,null)
			,new Column(ACR,"diasAlPrimerVto"				,0,6	,false	,null)
			,new Column(ACR,"diasEntreVtos"					,0,6	,false	,null)
			,new Column(ACR,"diasPago"						,2,8	,false	,null)
	};

	public CreditorLoaderFactory() {
	}
	public CreditorLoaderFactory(ILoaderIdCache cache) {
	}

	private Map<String, Column[]> columns;
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,ACR);
	}

	@Override
	public String getKey() {
		return ACR;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCreditor getTargetBean() {
		return new LoadedCreditor();
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedCreditor loaded = (LoadedCreditor) loadedPojo;
		return params.getLoaderUtils().insertCreditor(loaded);
	}
	
	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		return bean.get(id);
	}

}
