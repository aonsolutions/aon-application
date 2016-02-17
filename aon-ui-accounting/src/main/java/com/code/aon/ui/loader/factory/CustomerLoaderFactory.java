package com.code.aon.ui.loader.factory;

 import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCustomer;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerLoaderFactory extends RegistryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
			 new Column(CLI,"id"							,0,6	,false	,null)
			,new Column(CLI,"razonSocial"					,2,64	,true	,null)
			,new Column(CLI,"alias"							,2,32	,false	,null)
			,new Column(CLI,"tipoDocumento"					,0,1	,true	,new int[] {0,1,2,3,4,5,6})
			,new Column(CLI,"paisDocumento"					,2,2	,true	,null)
			,new Column(CLI,"documento"						,2,16	,true	,null)
			,new Column(CLI,"nacionalidad"					,2,2	,true	,null)
			,new Column(CLI,"cuenta"						,2,9	,false	,null)
			,new Column(CLI,"re"							,0,1	,false	,new int[] {0,1})
			,new Column(CLI,"transaccion"					,0,1	,false	,new int[] {0,1,2,3,4})
			,new Column(CLI,"retencion"						,0,1	,false	,new int[] {0,1})
			,new Column(CLI,"facturarAlbaranesAgrupados"	,0,1	,false	,new int[] {0,1})
			,new Column(CLI,"tipoVia"						,2,2	,false	,null)
			,new Column(CLI,"direccion"						,2,128	,false	,null)
			,new Column(CLI,"numero"						,2,6	,false	,null)
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
			,new Column(CLI,"cuentaBanco"					,2,34	,false	,null)
			,new Column(CLI,"formaPago"						,2,32	,false	,null)
			,new Column(CLI,"numeroVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasAlPrimerVto"				,0,6	,false	,null)
			,new Column(CLI,"diasEntreVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasPago"						,2,8	,false	,null)
			,new Column(CLI,"segmento"						,2,32	,false	,null)
	};

	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	private IManagerBean customerBean;

	public CustomerLoaderFactory() {
	}
	public CustomerLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	public IManagerBean getBean() throws ManagerBeanException {
		if (customerBean == null) {
			customerBean = BeanManager.getManagerBean(Customer.class);
		}
		return customerBean;
	}

	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,CLI);
	}
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCustomer.class);
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
	public ITransferObject get(Integer id) throws AonException {
		return getBean().get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedCustomer loaded = (LoadedCustomer) loadedPojo;
		Customer customer  = new Customer ();
		
		customer.setRegistry(populateRegistry(params,loaded));
		customer.setScope(params.getScope());
		customer.setTransaction(loaded.getInvoiceTransactionType());
		customer.setSurcharge(loaded.isSurcharge());
		customer.setWithholding(loaded.isWithholding());
		customer.setDeliveryGrouped(loaded.isDeliveryGrouped());
		customer.setStatus(CustomerStatus.ACTIVE);
		
		boolean insert = true;
		if (!params.isForceRegistryInsert()) {
			Criteria c = new Criteria();
			c.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), loaded.getDocumento());
			List<ITransferObject> list = getBean().getList(c);
			if (list == null || list.size() == 0) {
				insert = true;
			} else {
				insert = false;
				customer = (Customer) list.get(0);
				engine.log("Cliente " + loaded.getDocumento() + " - " + loaded.getRazonSocial() 
						+ " encontrado en la base de datos. ["
						+ customer.getRegistry().getDocument() + " - " + customer.getRegistry().getFullName() 
						+ "]. Se ignora.");
			}
		}
		if (insert) {	
			if (StringUtils.isNotBlank(loaded.getCuenta())) {
				customer.setAccount(getLoaderUtils().ensureAccount(loaded.getCuenta(), customer.getRegistry().getName()));

			}
			customer = (Customer) getBean().insert(customer);

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
				insertRegistryAddress(customer.getRegistry(),loaded);	
			}
			
			if (StringUtils.isNotBlank(loaded.getTelefono1())) {
				insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
			}
			if (StringUtils.isNotBlank(loaded.getTelefono2())) {
				insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
			}
			if (StringUtils.isNotBlank(loaded.getFax())) {
				insertRegistryMedia(customer.getRegistry(),MediaType.FAX,loaded.getFax());
			}
			if (StringUtils.isNotBlank(loaded.getEmail())) {
				if (EmailValidator.getInstance().isValid(loaded.getEmail())) {
					insertRegistryMedia(customer.getRegistry(),MediaType.EMAIL,loaded.getEmail(),true,false,false);
				} else {
					insertRegistryMedia(customer.getRegistry(),MediaType.UNKNOWN,loaded.getEmail(),true,false,false);
				}
			}
			if (StringUtils.isNotBlank(loaded.getWeb())) {
				String web = loaded.getWeb();
				if (!StringUtils.startsWith(web, "http") ) {
					web = "http://" + web;
				}

				UrlValidator validator = new UrlValidator();
				if (validator.isValid(web)) {
					insertRegistryMedia(customer.getRegistry(),MediaType.WEB,loaded.getWeb());
				} else {
					insertRegistryMedia(customer.getRegistry(),MediaType.UNKNOWN,loaded.getWeb());
				}
			}
			if (StringUtils.isNotBlank(loaded.getSegmento())) {
				insertRegistrySegment(customer.getRegistry(),loaded.getSegmento());
			}
			RegistryBank rbank = null;
			if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
				rbank = insertRegistryBank(engine,params,customer.getRegistry(),loaded);	
			}
			if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
				insertRegistryPayMethod(params,customer.getRegistry(),rbank,loaded);
			}
		}
		return customer.getId();
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedCustomer loaded = (LoadedCustomer) loadedPojo;
		Customer customer = searchCustomerByDocument( params, loaded );
		if (customer == null && StringUtils.isNotBlank( loaded.getCuenta())) {
			customer = searchCustomerByAccount( params, loaded );	
		}
		return customer;
	}
	
	private Customer searchCustomerByDocument(LoaderParams params, LoadedCustomer loaded) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = getBean().getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				engine.log("WARNING: Más de un cliente activo con el número de documento " + loaded.getDocumento()+".");
				if (!StringUtils.isBlank(loaded.getCuenta())) {
					engine.log("Se intenta deduplicar por cuenta contable.");
					return searchCustomerByAccount( params, loaded );
				}
			}
			return (Customer) list.get(0);
		}
		return null;
	}
	
	private Customer searchCustomerByAccount(LoaderParams params, LoadedCustomer loaded) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_ACCOUNT_CODE), loaded.getCuenta());
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID), params.getScope().getId());
		List<ITransferObject> list = getBean().getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un cliente vinculado a la cuenta " + loaded.getCuenta());
			}
			return (Customer) list.get(0);
		}
		return null;
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
}
