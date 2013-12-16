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
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedSupplier;
import com.esferalia.aon.entity.IEntityAlias;

public class SupplierLoaderFactory extends RegistryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(PRO,"id"							,0,6	,false	,null)
		,new Column(PRO,"razonSocial"					,2,64	,true	,null)
		,new Column(PRO,"alias"							,2,32	,false	,null)
		,new Column(PRO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(PRO,"paisDocumento"					,2,2	,true	,null)
		,new Column(PRO,"documento"						,2,16	,true	,null)
		,new Column(PRO,"nacionalidad"					,2,2	,true	,null)
		,new Column(PRO,"cuenta"						,2,9	,false	,null)
		,new Column(PRO,"transaccion"					,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(PRO,"retencion"						,0,1	,false	,new int[] {0,1})
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
		,new Column(PRO,"cuentaBanco"					,2,34	,false	,null)
		,new Column(PRO,"formaPago"						,2,32	,false	,null)
		,new Column(PRO,"numeroVtos"					,0,6	,false	,null)
		,new Column(PRO,"diasAlPrimerVto"				,0,6	,false	,null)
		,new Column(PRO,"diasEntreVtos"					,0,6	,false	,null)
		,new Column(PRO,"diasPago"						,2,8	,false	,null)
	};

	private Map<String, Column[]> columns;
	private ILoaderEngine engine;

	public SupplierLoaderFactory() {
	}
	public SupplierLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,PRO);
	}
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedSupplier.class);
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
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedSupplier loaded = (LoadedSupplier) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Supplier supplier  = new Supplier ();
		
		supplier.setRegistry(populateRegistry(params,loaded));
		supplier.setScope(params.getScope());
		supplier.setTransaction(loaded.getInvoiceTransactionType());
		supplier.setWithholding(loaded.isWithholding());
		supplier.setStatus(SupplierStatus.ACTIVE);
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			supplier.setAccount(getLoaderUtils().ensureAccount(loaded.getCuenta(), supplier.getRegistry().getName()));
		}
		supplier = (Supplier) bean.insert(supplier);
		
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
			insertRegistryAddress(supplier.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.EMAIL, loaded.getEmail(),true,false,false);
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(engine,params,supplier.getRegistry(),loaded);	
		}
		if (rbank != null && StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(params,supplier.getRegistry(),rbank,loaded);
		}
		return supplier.getId();
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedSupplier loaded = (LoadedSupplier) loadedPojo;
		Supplier supplier = searchSupplierByDocument( params, loaded );
		if (supplier == null && StringUtils.isNotBlank( loaded.getCuenta())) {
			supplier = searchSupplierByAccount( params, loaded );	
		}
		return supplier;
	}
	
	private Supplier searchSupplierByDocument(LoaderParams params, LoadedSupplier loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_STATUS), SupplierStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un proveedor activo con el número de documento " + loaded.getDocumento());
			}
			return (Supplier) list.get(0);
		}
		return null;
	}
	
	private Supplier searchSupplierByAccount(LoaderParams params, LoadedSupplier loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_ACCOUNT_CODE), loaded.getCuenta());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_STATUS), SupplierStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un proveedor vinculado a la cuenta " + loaded.getCuenta());
			}
			return (Supplier) list.get(0);
		}
		return null;
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
}
