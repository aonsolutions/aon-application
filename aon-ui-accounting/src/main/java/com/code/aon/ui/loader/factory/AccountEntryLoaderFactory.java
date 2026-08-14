package com.code.aon.ui.loader.factory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccountEntry;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(ASI,"id"			,0,6	,true	,null)
		,new Column(ASI,"diaro"			,0,6	,true	,null)
		,new Column(ASI,"fecha"			,3,10	,true	,null)
		,new Column(ASI,"tipoAsiento"	,0,1	,false  ,new int[] {0,1,2,3})
		,new Column(ASI,"comentario"	,2,256	,false	,null)
	};
	
	private Map<String, Column[]> columns;
	private IManagerBean entryBean;
	private IManagerBean invoiceBean;
	private IManagerBean accountEntryInvoiceBean;
	
	public AccountEntryLoaderFactory() {
	}
	public AccountEntryLoaderFactory(ILoaderEngine engine) {
	}
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	private IManagerBean getEntryBean() throws ManagerBeanException {
		if (entryBean == null) {
			entryBean = BeanManager.getManagerBean(AccountEntry.class);
		}
		return entryBean;
	}
	private IManagerBean getInvoiceBean() throws ManagerBeanException {
		if (invoiceBean == null) {
			invoiceBean = BeanManager.getManagerBean(Invoice.class);
		}
		return invoiceBean;
	}
	private IManagerBean getAccountEntryInvoiceBean() throws ManagerBeanException {
		if (accountEntryInvoiceBean == null) {
			accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		}
		return accountEntryInvoiceBean;
	}
	@Override
	public boolean accept(String key) {
		if (StringUtils.equals(key,ASI)) {
			return true;	
		}
		return false;
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccountEntry.class);
	}

	@Override
	public String getKey() {
		return ASI;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccountEntry getTargetBean() {
		return new LoadedAccountEntry();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		return getEntryBean().get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntry loaded = (LoadedAccountEntry) loadedPojo;
		AccountEntry entry = new AccountEntry();
		entry.setType(loaded.getEntryType() != null?loaded.getEntryType():AccountEntryType.MANUAL);
		entry.setJournal(loaded.getDiario());
		entry.setEntryDate(loaded.getFecha());
		entry.setAccountPeriod( params.getAccountPeriod() );
		entry.setComments(loaded.getComentario());
		entry.setSecurityLevel(params.getSecurityLevel() == null ? SecurityLevel.OFFICIAL : params.getSecurityLevel());
		Invoice invoice = null;
		if (loaded.getEnlaceFactura() != null && loaded.getEnlaceFactura() == 1) {
			if (loaded.getEnlaceNumero() == null || loaded.getEnlaceTipoFactura() == null) {
				throw new AonException("Se ha definido un asiento como enlace a una factura, pero el tipo de la factura o el número no se ha definido.");	
			}
			InvoiceType type = InvoiceType.values()[loaded.getEnlaceTipoFactura()]; 
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getInvoiceBean().getFieldName(IEntityAlias.INVOICE_TYPE), type);
			Invoice.addNotAnnulledExpression(getInvoiceBean(), criteria);
			if (StringUtils.isEmpty( loaded.getEnlaceSerie() )) {
				criteria.addNullExpression(getInvoiceBean().getFieldName(IEntityAlias.INVOICE_SERIES));
			} else {
				criteria.addEqualExpression(getInvoiceBean().getFieldName(IEntityAlias.INVOICE_SERIES), loaded.getEnlaceSerie() );
			}
			criteria.addEqualExpression(getInvoiceBean().getFieldName(IEntityAlias.INVOICE_NUMBER), loaded.getEnlaceNumero());
			criteria.addEqualExpression(getInvoiceBean().getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), loaded.getFecha());
			for (ITransferObject to : getInvoiceBean().getList(criteria)) {
				invoice = (Invoice) to;
				if (invoice.getStatus() == InvoiceStatus.SCORED) {
					String msg = MessageFormat.format("La factura de tipo: {0}, serie: {1} y numero : {2} ya está contabilizada."
							, loaded.getEnlaceTipoFactura()
							, loaded.getEnlaceSerie()
							, loaded.getEnlaceNumero()
							);
					throw new AonException(msg);
				}
				break;
			}
			if (invoice == null) {
				String msg = MessageFormat.format("No ha sido posible enlazar con la factura de tipo: {0}, serie: {1} , numero : {2} y fecha: {3,date,dd/MM/yyyy}. No se ha encontrado."
						, loaded.getEnlaceTipoFactura()
						, loaded.getEnlaceSerie()
						, loaded.getEnlaceNumero()
						, loaded.getFecha()
						);
				throw new AonException(msg);
			}
			if ( type == InvoiceType.SALES) {
				entry.setType(AccountEntryType.SALES_INVOICE);	
			} else if ( type == InvoiceType.PURCHASE) { 
				entry.setType(AccountEntryType.PURCHASE_INVOICE);
			} else if ( type == InvoiceType.EXPENSES) { 
				entry.setType(AccountEntryType.EXPENSE_INVOICE);
			} else if ( type == InvoiceType.UNDEDUCTIBLE) { 
				entry.setType(AccountEntryType.EXPENSES);
			}
			
			String msg = MessageFormat.format(" [Asiento enlazado en carga de datos. Factura {0,choice,0#Compra|1#Venta|2#Gasto|3#Gasto no Deducible}: "
					+ "{0,choice,0#R|1#E|2#G|3#G}-{1}/{2,number,000000} de {3,date,dd/MM/yyyy}]"
					, loaded.getEnlaceTipoFactura()
					, loaded.getEnlaceSerie()
					, loaded.getEnlaceNumero()
					, loaded.getFecha()
					);
			entry.setComments( AonStringUtils.isNotEmpty(entry.getComments())
					?entry.getComments() + msg
					:msg);
		}
		entry = (AccountEntry) getEntryBean().insert(entry);
		if (loaded.getEnlaceFactura() != null && loaded.getEnlaceFactura() == 1 && invoice != null) {
			AccountEntryInvoice aei = new AccountEntryInvoice();
			aei.setAccountEntry(entry);
			aei.setInvoice(invoice);
			getAccountEntryInvoiceBean().insert(aei);
			invoice.setStatus(InvoiceStatus.SCORED);
			invoice.setUpdateEnabled(false);
			getInvoiceBean().update(invoice);
		}
		return entry.getId();
	}

	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntry loaded = (LoadedAccountEntry) loadedPojo;
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getEntryBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_JOURNAL), loaded.getDiario());
		criteria.addEqualExpression(getEntryBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENTRY_DATE), loaded.getFecha());
		List<ITransferObject> list = getEntryBean().getList(criteria); 
		if ( list.size() > 0 ) {
			return (AccountEntry) list.get(0);
		}
		return null;
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		if (params.getAccountPeriod() == null) {
			throw new AonException("No se ha definido un ejercicio contable");
		}
	}
	
}
