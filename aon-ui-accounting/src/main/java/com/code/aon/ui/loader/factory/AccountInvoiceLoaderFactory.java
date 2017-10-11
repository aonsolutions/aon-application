package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccountEntry;
import com.code.aon.ui.loader.pojo.LoadedAccountEntryDetail;
import com.code.aon.ui.loader.pojo.LoadedAccountInvoice;
import com.code.aon.ui.loader.pojo.LoadedInvoice;
import com.code.aon.ui.loader.pojo.LoadedInvoiceDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountInvoiceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(FRA_CTB,"id"				,0,8	,true	,null)
		,new Column(FRA_CTB,"serie"				,2,5	,false	,null)
		,new Column(FRA_CTB,"numero"			,0,8	,false	,null)
		,new Column(FRA_CTB,"referencia"		,2,32	,false	,null)
		,new Column(FRA_CTB,"idTitular"			,0,6	,false	,null)
		,new Column(FRA_CTB,"cuenta"			,2,9	,false	,null)
		,new Column(FRA_CTB,"documento"			,2,16	,true	,null)
		,new Column(FRA_CTB,"tipoDocumento"		,0,1	,true	,new int[] {0,1,2,3,4,5,6})
		,new Column(FRA_CTB,"paisDocumento"		,2,2	,true	,null)
		,new Column(FRA_CTB,"razonSocial"		,2,128	,true	,null)
		,new Column(FRA_CTB,"fechaFactura"		,3,10	,true	,null)
		,new Column(FRA_CTB,"fechaIva"			,3,10	,false	,null)
		,new Column(FRA_CTB,"tipo"				,0,1	,true	,new int[] {0,1,2,3})
		,new Column(FRA_CTB,"inversion"			,0,1	,false	,new int[] {0,1})
		,new Column(FRA_CTB,"transaccion"		,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(FRA_CTB,"criterioCaja"		,0,1	,false	,new int[] {0,1})
		,new Column(FRA_CTB,"comentario"		,2,256	,false	,null)
		
		,new Column(FRA_CTB,"tipoVia"			,2,2	,false	,null)
		,new Column(FRA_CTB,"direccion"			,2,128	,false	,null)
		,new Column(FRA_CTB,"numeroDir"			,2,6	,false	,null)
		,new Column(FRA_CTB,"direccion2"		,2,128	,false	,null)
		,new Column(FRA_CTB,"cp"				,2,16	,false	,null)
		,new Column(FRA_CTB,"ciudad"			,2,64	,false	,null)
		,new Column(FRA_CTB,"provincia"			,2,3	,false	,null)
		,new Column(FRA_CTB,"nombreProvincia"	,2,32	,false	,null)

		// GRUPO 1
		,new Column(FRA_CTB,"baseImponible1"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva1"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva1"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re1"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA1"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE1"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuentaExplotacion"	,2,9	,true	,null)
		,new Column(FRA_CTB,"cuentaIva"			,2,9	,false	,null)
		 // GRUPO 2
		,new Column(FRA_CTB,"baseImponible2"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva2"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva2"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re2"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA2"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE2"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuentaExplotacion2",2,9	,false	,null)
		,new Column(FRA_CTB,"cuentaIva2"		,2,9	,false	,null)
		 // GRUPO 3
		,new Column(FRA_CTB,"baseImponible3"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva3"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva3"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re3"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA3"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE3"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuentaExplotacion3",2,9	,false	,null)
		,new Column(FRA_CTB,"cuentaIva3"		,2,9	,false	,null)
		 // GRUPO 4
		,new Column(FRA_CTB,"baseImponible4"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva4"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva4"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re4"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA4"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE4"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuentaExplotacion4",2,9	,false	,null)
		,new Column(FRA_CTB,"cuentaIva4"		,2,9	,false	,null)
		 // ------------
		,new Column(FRA_CTB,"irpf"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIRPF"			,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoIrpf"			,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(FRA_CTB,"totalFactura"		,1,17	,false	,null)
//		,new Column(FRA_CTB,"articulo"			,2,15	,false	,null)
		,new Column(FRA_CTB,"concepto"			,2,64	,false	,null)
		,new Column(FRA_CTB,"cuentaIrpf"		,2,9	,false	,null)
		,new Column(FRA_CTB,"fechaVto"			,3,10	,false	,null)
		,new Column(FRA_CTB,"formaPago"			,2,32	,false	,null)
		,new Column(FRA_CTB,"cuentaBanco"		,2,34	,false	,null)
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	public AccountInvoiceLoaderFactory() {
	}
	public AccountInvoiceLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,FRA_CTB);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccountInvoice.class);
	}

	@Override
	public String getKey() {
		return FRA_CTB;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccountInvoice getTargetBean() {
		return new LoadedAccountInvoice();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		return insertInvoice(params,loadedPojo);
	}
	
	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	private Integer insertInvoice(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountInvoice loaded = (LoadedAccountInvoice) loadedPojo;
		LoadedInvoice loadedInvoice = loaded.getLoadedInvoice();
		Integer invoiceId = engine.insertAonEntity(params, loadedInvoice );
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = (Invoice) invoiceBean.get(invoiceId);
		if (invoice == null) {
			throw new AonException("La factura no se ha grabado correctamente");
		}
		insertInvoiceAddress(loadedInvoice, invoice);
		insertInvoiceAccountEntry(params,invoice, loadedInvoice);
		for (LoadedInvoiceDetail detail : loaded.getLoadedInvoiceDetails()) {
			engine.insertAonEntity(params, detail);	
		}
		engine.insertAonEntity(params, loaded.getLoadedFinance());
		return invoiceId; 
	}
	
	private void insertInvoiceAddress(LoadedInvoice loaded, Invoice invoice) throws ManagerBeanException {
		if(loaded.getDireccion() != null) {
			IManagerBean bean2 = BeanManager.getManagerBean(InvoiceAddress.class);
			InvoiceAddress invoiceAddress = new InvoiceAddress();
			invoiceAddress.setAddress(loaded.getDireccion());
			invoiceAddress.setAddress2(loaded.getDireccion2());
			invoiceAddress.setCity(loaded.getCiudad());
			invoiceAddress.setDomain(invoice.getDomain());
			invoiceAddress.setInvoice(invoice);
			invoiceAddress.setNumber(loaded.getNumeroDir());
			invoiceAddress.setProvince(loaded.getNombreProvincia());
			invoiceAddress.setStreetType(StreetType.valueOf(loaded.getTipoVia()));
			invoiceAddress.setZip(loaded.getCp());
			invoiceAddress = (InvoiceAddress) bean2.insert(invoiceAddress);
		}
	}
	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountInvoice loaded = (LoadedAccountInvoice) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TYPE), loaded.getInvoiceType());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SERIES), loaded.getSerie());
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
		if (params.getAccountPeriod() == null) {
			throw new AonException("No se ha definido un ejercicio contable");
		}
	}
	
	private void insertInvoiceAccountEntry(LoaderParams params,Invoice invoice, LoadedInvoice loaded) throws AonException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		LoadedAccountEntry loadedAccountEntry = loaded.getLoadedAccountEntry();
		Integer entryId =  engine.insertAonEntity(params, loadedAccountEntry);
		AccountEntry entry = (AccountEntry) engine.get(ASI, entryId); 
		
		AccountEntryInvoice aei = new AccountEntryInvoice();
		aei.setAccountEntry(entry);
		aei.setInvoice(invoice);
		accountEntryInvoiceBean.insert(aei);
		
		String prefix = (invoice.getType() == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
		if (invoice.getTotal() < 0) {
			prefix += " " + "ABONO";
		}
		prefix += ": ";
		String concept = StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32); 
		
		LoadedAccountEntryDetail loadedAccountEntryDetail = loaded.getLoadedAccountEntryDetail();
		loadedAccountEntryDetail.setDocumento(invoice.getDocumentNumber());
		loadedAccountEntryDetail.setEntry( entry );
		loadedAccountEntryDetail.setConcepto( concept );
		engine.ensureAonEntity(params, loadedAccountEntryDetail);
	}
	
}
