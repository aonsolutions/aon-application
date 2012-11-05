package com.code.aon.ui.loader.factory;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.Tax;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedItem;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(ITEM,"codigo"				,2,15,true	,null)
		,new Column(ITEM,"codigoBarras"			,2,32,false	,null)
		,new Column(ITEM,"nombre"				,2,64,true	,null)
		,new Column(ITEM,"precioVentaBase"		,1,16,false	,null)
		,new Column(ITEM,"precioCompra"			,1,16,false	,null)
		,new Column(ITEM,"beneficioSobreCompra"	,1,16,false	,null)
		,new Column(ITEM,"porcRetencion"		,1,16,false	,null)
		,new Column(ITEM,"porcIva"				,1,16,false	,null)
		,new Column(ITEM,"inventariable"		,0,1 ,false	,new int[] {0,1})
		,new Column(ITEM,"categoria"			,2,32,false	,null)
		,new Column(ITEM,"marca"				,2,32,false	,null)
		,new Column(ITEM,"naturaleza"			,0,1 ,false	,new int[] {0,1,2,3,4})
		,new Column(ITEM,"cuentaVenta"			,2,9,false	,null)
		,new Column(ITEM,"cuentaCompra"			,2,9,false	,null)
	};
	
	private LoaderUtils loaderUtils;
	private Map<String, Column[]> columns;
	//private ILoaderEngine engine;
	
	public ItemLoaderFactory() {
	}
	
	public ItemLoaderFactory(ILoaderEngine engine) {
		//this.engine = engine;
	}

	private LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new LoaderUtils();
		}
		return loaderUtils;
	}
	
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,ITEM);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedItem.class);
	}

	@Override
	public String getKey() {
		return ITEM;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedItem getTargetBean() {
		return new LoadedItem();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedItem loaded = (LoadedItem) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		IManagerBean paBean = BeanManager.getManagerBean(ProductAccount.class);
		Item item = new Item();
		item.setBarcode(loaded.getCodigoBarras());
		item.setPrice(loaded.getPrecioVentaBase());
		item.setPurchasePrice(loaded.getPrecioCompra());
		item.setProfitPercent(loaded.getBeneficioSobreCompra());
		item.setStatus( ProductStatus.ACTIVE );
		
		Product product = new Product ();
		product.setCode(loaded.getCodigo());
		product.setName(loaded.getNombre());
		product.setInventoriable(loaded.isInventoriable());
		product.setStatus( ProductStatus.ACTIVE );
		product.setType(loaded.getProductType());
		product.setBrand( getLoaderUtils().ensureBrand( loaded.getMarca() ) );
		product.setCategory( getLoaderUtils().ensureProductCategory( loaded.getCategoria() ) );
		product.setCategory( getLoaderUtils().ensureProductCategory( loaded.getCategoria() ) );
		
		if ( loaded.getPorcRetencion() != null ) {
			Tax retention = getLoaderUtils().ensureRetention(loaded.getPorcRetencion());
			product.setRetention(retention);	
		}
		if ( loaded.getPorcIva() != null ) {
			Tax vat = getLoaderUtils().ensureVat(loaded.getPorcIva());
			product.setVat(vat);	
		}

		item.setProduct(product);
		item = (Item) bean.insert(item);
		if (StringUtils.isNotBlank( loaded.getCuentaVenta())) {
			ProductAccount productAccount = new ProductAccount();
			productAccount.setProduct(item.getProduct());
			productAccount.setType(ProductAccountType.SALES);
			Account account = getLoaderUtils().ensureAccount(loaded.getCuentaVenta(), null);
			if (account == null) {
				account = getLoaderUtils().ensureAccount(loaded.getCuentaVenta(), loaded.getNombre());	
			}
			productAccount.setAccount(account);
			paBean.insert(productAccount);	
		}
		if (StringUtils.isNotBlank( loaded.getCuentaCompra())) {
			ProductAccount productAccount = new ProductAccount();
			productAccount.setProduct(item.getProduct());
			productAccount.setType(ProductAccountType.PURCHASE);
			Account account = getLoaderUtils().ensureAccount(loaded.getCuentaCompra(), null);
			if (account == null) {
				account = getLoaderUtils().ensureAccount(loaded.getCuentaCompra(), loaded.getNombre());	
			}
			productAccount.setAccount(account);
			paBean.insert(productAccount);	
		}
		return item.getId();
	}

	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedItem loaded = (LoadedItem) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), loaded.getCodigo());	
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			return (Item) list.get(0);
		}
		return null;
	}
	
}
