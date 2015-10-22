package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCustomerFee;

public class CustomerFeeLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(CUO,"cliente"		,0,5	,true	,null)  // Cliente (enlace con clientes en el fichero de carga. Se asume que viene tambien el cliente en el fichero de carga)
		,new Column(CUO,"linea"			,0,2	,true	,null)  // Número de linea
		,new Column(CUO,"fechaDesde"	,3,10	,true	,null)  // Fecha Desde
		,new Column(CUO,"fechaFactura"	,3,10	,true	,null)  // Fecha Proxima Factura
		,new Column(CUO,"periodo"		,0,2	,true	,new int[] {0,1,2,3,4,5,6})  // Periodo (0-Sin periodo, 1-Mensual, 2-Bimestral, 3-Trimestral, 4-Cuatrimestral, 5-Semestral, 6-Anual)
		,new Column(CUO,"item"			,2,15	,true	,null)  // Item (enlace con el item del fichero de carga. Se asume que el fichero de carga tambien trae el item)
		,new Column(CUO,"descripcion"	,2,1024	,false	,null)  // Descripcion
		,new Column(CUO,"cantidad"		,1,6	,true	,null)  // Cantidad
		,new Column(CUO,"precio" 		,1,17	,true	,null)  // Precio
	};
	
	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	
	public CustomerFeeLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,CUO);
	}
	
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCustomerFee.class);
	}

	@Override
	public String getKey() {
		return CUO;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCustomerFee getTargetBean() {
		return new LoadedCustomerFee();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(CustomerFee.class);
		return bean.get(id);
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		return null;
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedCustomerFee loaded = (LoadedCustomerFee) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(CustomerFee.class);
		CustomerFee fee = new CustomerFee();
		
		// Buscar el Cliente		
		Customer customer = (Customer) engine.getAonEntity(ILoaderFactory.CLI, loaded.getCliente());
		if (customer  == null) {
			throw new AonException("El cliente con codigo " + loaded.getCliente() + " no existe en el fichero de carga.");
		}
		
		// Buscar el Item		
		Item item = (Item) engine.getAonEntity(ILoaderFactory.ITEM, loaded.getItem());
		if (item == null) {
			throw new AonException("El item con codigo " + loaded.getItem() + " no existe en el fichero de carga.");
		}
		
		fee.setWorkPlace(params.getWorkPlace());
		fee.setCustomer(customer);
		fee.setLine(loaded.getLinea());
		fee.setInitialDate(loaded.getFechaDesde());
		fee.setBillingDate(loaded.getFechaFactura());
		fee.setPeriod(  BillingPeriod.values()[loaded.getPeriodo()]); 
		fee.setItem(item);
		fee.setDescription(loaded.getDescripcion());
		fee.setQuantity(loaded.getCantidad());
		fee.setPrice(loaded.getPrecio());
		fee.setDiscountExpression(new DiscountExpression());
		
		fee = (CustomerFee) bean.insert(fee);

		return fee.getId();
	}	

	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
