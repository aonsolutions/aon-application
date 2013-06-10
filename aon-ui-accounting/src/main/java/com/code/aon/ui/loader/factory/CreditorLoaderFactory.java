package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCreditor;
import com.esferalia.aon.entity.IEntityAlias;

public class CreditorLoaderFactory extends RegistryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
			 new Column(ACR,"id"							,0,6	,true	,null)
			,new Column(ACR,"razonSocial"					,2,64	,true	,null)
			,new Column(ACR,"alias"							,2,32	,false	,null)
			,new Column(ACR,"tipoDocumento"					,0,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(ACR,"paisDocumento"					,2,2	,true	,null)
			,new Column(ACR,"documento"						,2,16	,true	,null)
			,new Column(ACR,"nacionalidad"					,2,2	,true	,null)
			,new Column(ACR,"cuenta"						,2,9	,false	,null)
			,new Column(ACR,"transaccion"					,0,1	,false	,new int[] {0,1,2,3})
			,new Column(ACR,"retencion"						,0,1	,false	,new int[] {0,1})
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

	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	
	public CreditorLoaderFactory() {
	}
	public CreditorLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,ACR);
	}
	
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCreditor.class);
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
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedCreditor loaded = (LoadedCreditor) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Creditor creditor  = new Creditor();
		
		creditor.setRegistry(populateRegistry(params,loaded));
		creditor.setScope(params.getScope());
		creditor.setTransaction(loaded.getInvoiceTransactionType());
		creditor.setWithholding(loaded.isWithholding());
		creditor.setStatus(CreditorStatus.ACTIVE);
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			creditor.setAccount(getLoaderUtils().ensureAccount(loaded.getCuenta(), creditor.getRegistry().getName()));
		}
		creditor = (Creditor) bean.insert(creditor);
		
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(creditor.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.EMAIL, loaded.getEmail(),true,false,false);
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(engine,params,creditor.getRegistry(),loaded);	
		}
		if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(params,creditor.getRegistry(),rbank,loaded);
		}
		return creditor.getId();
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedCreditor loaded = (LoadedCreditor) loadedPojo;
		Creditor creditor = searchCreditorByDocument( params, loaded );
		if (creditor == null && StringUtils.isNotBlank( loaded.getCuenta())) {
			creditor = searchCreditorByAccount( params, loaded );	
		}
		return creditor;
	}
	
	private Creditor searchCreditorByDocument(LoaderParams params, LoadedCreditor loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_STATUS), CreditorStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un acreedor activo con el número de documento " + loaded.getDocumento());
			}
			return (Creditor) list.get(0);
		}
		return null;
	}
	
	private Creditor searchCreditorByAccount(LoaderParams params, LoadedCreditor loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_ACCOUNT_CODE), loaded.getCuenta());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_STATUS), CreditorStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un acreedor vinculado a la cuenta " + loaded.getCuenta());
			}
			return (Creditor) list.get(0);
		}
		return null;
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
}
