package com.code.aon.ui.loader.factory;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCreditor;
import com.code.aon.ui.loader.pojo.LoadedCustomer;
import com.code.aon.ui.loader.pojo.LoadedInvoice;
import com.code.aon.ui.loader.pojo.LoadedSupplier;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(FRA,"id"			,0,6	,true	,null)
		,new Column(FRA,"serie"			,2,5	,true	,null)
		,new Column(FRA,"numero"		,0,8	,true	,null)
		,new Column(FRA,"referencia"	,2,32	,true	,null)
		,new Column(FRA,"idTitular"		,0,6	,true	,null)
		,new Column(FRA,"cuenta"		,2,9	,true	,null)
		,new Column(FRA,"documento"		,2,16	,true	,null)
		,new Column(FRA,"tipoDocumento"	,0,1	,true	,new int[] {0,1,2,3,4,5,6})
		,new Column(FRA,"paisDocumento"	,2,2	,true	,null)
		,new Column(FRA,"razonSocial"	,2,128	,true	,null)
		,new Column(FRA,"fechaFactura"	,3,10	,true	,null)
		,new Column(FRA,"fechaIva"		,3,10	,false	,null)
		,new Column(FRA,"tipo"			,0,1	,true	,new int[] {0,1,2,3})
		,new Column(FRA,"inversion"		,0,1	,false	,new int[] {0,1})
		,new Column(FRA,"transaccion"	,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(FRA,"criterioCaja"	,0,1	,false	,new int[] {0,1})
		,new Column(FRA,"comentario"	,2,256	,false	,null)
		,new Column(FRA,"baseImponible"	,1,17	,true	,null)
		,new Column(FRA,"totalCuotaIVA"	,1,17	,true	,null)
		,new Column(FRA,"totalCuotaIRPF",1,17	,true	,null)
		,new Column(FRA,"totalFactura"	,1,17	,true	,null)
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;
	private AccountBridgeUtil accountBridgeUtil;

	public InvoiceLoaderFactory() {
	}
	public InvoiceLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,FRA);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedInvoice.class);
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
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedInvoice loaded = (LoadedInvoice) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = new Invoice();
		InvoiceType type = InvoiceType.values()[loaded.getTipo()];
		if (type == InvoiceType.SALES ) {
			Series series = ensureInvoiceSeries(params,loaded.getSerie() ); 
			invoice.setSeries( series==null?null:series.getCode() );
			invoice.setNumber( loaded.getNumero() );
		} else {
			invoice.setReferenceCode(StringUtils.abbreviate(loaded.getReferencia(),16));
		}
		invoice.setType(type);
		Registry registry = obtainRegistry(params, type , loaded);
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(loaded.getDocumento());
		invoice.setRegistryDocumentType(DocumentType.values()[loaded.getTipoDocumento()]);
		invoice.setRegistryDocumentCountry(Country.valueOf( loaded.getPaisDocumento()));
		invoice.setRegistryName(loaded.getRazonSocial());
		invoice.setRegistryAddress(registry.getDefaultAddress());
		invoice.setIssueDate(loaded.getFechaFactura());
		invoice.setTaxDate(loaded.getFechaIva());
		invoice.setInvestment( loaded.isInvestment() );
		invoice.setTransaction(loaded.getInvoiceTransactionType());
		invoice.setVatAccrualPayment(loaded.isVatAccrualPayment());
		invoice.setComments(loaded.getComentario());
		Date now = new Date();
		invoice.setRemarks("Importada de fichero " + params.getDateFormatter().format(now) + " - " + params.getTimeFormatter().format(now));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setTaxableBase(loaded.getBaseImponible());
		invoice.setVatQuota(loaded.getTotalCuotaIVA());
		invoice.setRetentionQuota(loaded.getTotalCuotaIRPF()==null?0.0:loaded.getTotalCuotaIRPF());
		invoice.setTotal(loaded.getTotalFactura());
		if (loaded.isFromLoadedInvoiceAccount()) {
			if (StringUtils.isBlank(loaded.getCuenta())) {
				Account account = null;
				if (invoice.getType() == InvoiceType.SALES) {
					account = getAccountBridgeUtil().obtainCustomerAccount(invoice.getRegistry());
				} else if (invoice.getType() == InvoiceType.PURCHASE) {
					account = getAccountBridgeUtil().obtainSupplierAccount(invoice.getRegistry());
				} else if (invoice.getType() == InvoiceType.EXPENSES) {
					account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
				} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
					account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
				}
				loaded.setCuenta(account.getCode());
			}
			invoice.setStatus(InvoiceStatus.SCORED);
		}
		invoice.setUpdateEnabled(false);
		invoice = (Invoice) bean.insert(invoice);
		return invoice.getId();
	}

	private Series ensureInvoiceSeries(LoaderParams params,String serie) throws ManagerBeanException {
		if (StringUtils.isNotBlank(serie)) {
			IManagerBean bean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_CODE), serie);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), params.getScope().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if ( list.size() > 0 ) {
				Series series = (Series) list.get(0);
				return series;
			}
			Series series = new Series();
			series.setCode(serie);
			series.setDescription(serie);
			series.setInvoice(true);
			series.setActive(true);
			series.setScope( params.getScope() );
			series.setSecurityLevel(params.getSecurityLevel() == null ? SecurityLevel.OFFICIAL : params.getSecurityLevel());
			series = (Series) bean.insert(series);
			return series;
		}
		return null;
	}

	private Registry obtainRegistry(LoaderParams params,InvoiceType type, LoadedInvoice loaded) throws AonException {
		IRegistry r = null;
		if ( type == InvoiceType.SALES ) {
			LoadedCustomer loadedCustomer = loaded.getLoadedCustomer();
			Customer customer = (Customer) engine.ensureAonEntity(params,loadedCustomer);
			r = customer.getRegistry();
		} else if ( type == InvoiceType.PURCHASE ) {
			LoadedSupplier loadedSupplier = loaded.getLoadedSupplier();
			Supplier supplier = (Supplier) engine.ensureAonEntity(params,loadedSupplier);
			r = supplier.getRegistry();
		} else if ( type == InvoiceType.EXPENSES || type == InvoiceType.UNDEDUCTIBLE) {
			LoadedCreditor loadedCreditor= loaded.getLoadedCreditor();
			Creditor creditor = (Creditor) engine.ensureAonEntity(params,loadedCreditor);
			r = creditor.getRegistry();
		}
		return r.getRegistry();
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedInvoice loaded = (LoadedInvoice) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isEmpty( loaded.getSerie() )) {
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SERIES), loaded.getSerie());	
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_NUMBER), loaded.getNumero());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), loaded.getFechaFactura());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			return (Invoice) list.get(0);
		}
		return null;
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
