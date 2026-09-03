package com.code.aon.ui.loader;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public interface ILoaderFactory<E extends ILoadedPojo> {
	
	String CLI = "CLI";	// Clientes - Customer
	String ACR = "ACR";	// Acreedores - Creditor
	String PRO = "PRO";	// Proveedores - Supplier
	String CLP = "CLP";	// Clientes potenciales - Target
	
	String FRA = "FRA";	// Facturas - Invoice
	String DET = "DET";	// Lineas de facturas - InvoiceDetail
	String VTO = "VTO";	// Vencimientos - Finance

	String FRA_CTB = "FRACTB";	// Factura Contable
	
	String ASI = "ASI";	// Asientos - AccountEntry
	String APU = "APU";	// Apuntes - AccountEntryDetail
	
	String ITEM = "ITEM";	// Item - Artículos

	String PGC = "PGC";	// Cuentas Contables
	
	String EMP = "EMP";	// Datos Generales de la Empresa, Parametros Fiscales y Parametros Contables
	
	String AMC = "AMC";	// Fichas de Amortizacion - Cabeceras
	String AML = "AML";	// Fichas de Amortizacion - Lineas
	
	String BAN = "BAN";	// Bancos de la Empresa
	
	String REP = "REP";	// Representantes de la Empresa
	
	String CON = "CON";	// Conceptos automaticos contabilidad
	
	String CUO = "CUO";	// Cuotas
	
	String ACT = "ACT";	// Actividades de la Empresa
	
	public boolean accept( String key);
	public boolean accept(Class<? extends ILoadedPojo> clazz);

	public String getKey();
	public Column[] getSupportedColumns();
	
	public E getTargetBean();
	public Integer insert(LoaderParams params,E loaded) throws AonException;
	
	public ITransferObject get(Integer id) throws AonException;
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException;
	public void validate(LoaderParams params) throws AonException;

}
