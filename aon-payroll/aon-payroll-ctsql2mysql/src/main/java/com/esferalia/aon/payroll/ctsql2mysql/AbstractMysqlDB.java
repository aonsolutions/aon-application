package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

public class AbstractMysqlDB extends DefaultCtsqlDBVisitor {

	final static Logger LOGGER = 
		LoggerFactory.getLogger(AbstractMysqlDB.class);
	
	protected Connection mysqlConnection;

	
	private PreparedStatement leasingStmt;
	
	private void initLeasingStmt()
	throws SQLException {
		this.leasingStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO leasing ( leasing_date, supplier_name, supplier_document, description, term, interest_percent, review, amount, rbank, security_level, fixed_asset_account, vat) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLeasingStmt()
	throws SQLException {
		leasingStmt.close();
	}	
	/**
	 * Leasing
	 * @param leasing_date Fecha de Concesion
	 * @param supplier_name Razon Social del Proveedor
	 * @param supplier_document CIF del Proveedor
	 * @param description Descripcion
	 * @param term Plazo
	 * @param interest_percent Porcentaje de Interes
	 * @param review Revision
	 * @param amount Importe del leasing
	 * @param rbank Banco por el que se paga el leasing
	 * @param security_level Nivel de Seguridad
	 * @param fixed_asset_account Cuenta de inmobilizado
	 * @param vat IVA del leasing
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLeasing(Date leasing_date, String supplier_name, String supplier_document, String description, String term, String interest_percent, String review, Double amount, Integer rbank, Short security_level, String fixed_asset_account, Integer vat)
	throws SQLException {
	
		if ( leasing_date == null ) {
			this.leasingStmt.setNull(1, 91);
		}
		else {
			this.leasingStmt.setDate(1, leasing_date);
		}
		if ( supplier_name == null ) {
			this.leasingStmt.setNull(2, 12);
		}
		else {
			this.leasingStmt.setString(2, supplier_name);
		}
		if ( supplier_document == null ) {
			this.leasingStmt.setNull(3, 12);
		}
		else {
			this.leasingStmt.setString(3, supplier_document);
		}
		if ( description == null ) {
			this.leasingStmt.setNull(4, 12);
		}
		else {
			this.leasingStmt.setString(4, description);
		}
		if ( term == null ) {
			this.leasingStmt.setNull(5, 12);
		}
		else {
			this.leasingStmt.setString(5, term);
		}
		if ( interest_percent == null ) {
			this.leasingStmt.setNull(6, 12);
		}
		else {
			this.leasingStmt.setString(6, interest_percent);
		}
		if ( review == null ) {
			this.leasingStmt.setNull(7, 12);
		}
		else {
			this.leasingStmt.setString(7, review);
		}
		if ( amount == null ) {
			this.leasingStmt.setNull(8, 8);
		}
		else {
			this.leasingStmt.setDouble(8, amount);
		}
		if ( rbank == null ) {
			this.leasingStmt.setNull(9, 4);
		}
		else {
			this.leasingStmt.setInt(9, rbank);
		}
		if ( security_level == null ) {
			this.leasingStmt.setNull(10, -6);
		}
		else {
			this.leasingStmt.setShort(10, security_level);
		}
		if ( fixed_asset_account == null ) {
			this.leasingStmt.setNull(11, 1);
		}
		else {
			this.leasingStmt.setString(11, fixed_asset_account);
		}
		if ( vat == null ) {
			this.leasingStmt.setNull(12, 4);
		}
		else {
			this.leasingStmt.setInt(12, vat);
		}

		this.leasingStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.leasingStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement db_versionStmt;
	
	private void initDb_versionStmt()
	throws SQLException {
		this.db_versionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO db_version ( version_number) VALUES ( ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDb_versionStmt()
	throws SQLException {
		db_versionStmt.close();
	}	
	/**
	 * Db_version
	 * @param version_number Numero de Version de la Base de Datos
	 * @throws SQLException
	*/
	protected void insertDb_version(String version_number)
	throws SQLException {
	
		if ( version_number == null ) {
			this.db_versionStmt.setNull(1, 12);
		}
		else {
			this.db_versionStmt.setString(1, version_number);
		}

		this.db_versionStmt.executeUpdate();
		
	}
	
	private PreparedStatement composition_detailStmt;
	
	private void initComposition_detailStmt()
	throws SQLException {
		this.composition_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition_detail ( composition, item, description, quantity, price) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeComposition_detailStmt()
	throws SQLException {
		composition_detailStmt.close();
	}	
	/**
	 * Composition_detail
	 * @param composition Identificador de la Composicion
	 * @param item Identificador del Articulo subproducto
	 * @param description Descripcion del Articulo subproducto
	 * @param quantity Cantidad de Articulo subproducto
	 * @param price Precio del Articulo subproducto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertComposition_detail(Integer composition, Integer item, String description, Double quantity, Double price)
	throws SQLException {
	
		if ( composition == null ) {
			this.composition_detailStmt.setNull(1, 4);
		}
		else {
			this.composition_detailStmt.setInt(1, composition);
		}
		if ( item == null ) {
			this.composition_detailStmt.setNull(2, 4);
		}
		else {
			this.composition_detailStmt.setInt(2, item);
		}
		if ( description == null ) {
			this.composition_detailStmt.setNull(3, 12);
		}
		else {
			this.composition_detailStmt.setString(3, description);
		}
		if ( quantity == null ) {
			this.composition_detailStmt.setNull(4, 8);
		}
		else {
			this.composition_detailStmt.setDouble(4, quantity);
		}
		if ( price == null ) {
			this.composition_detailStmt.setNull(5, 8);
		}
		else {
			this.composition_detailStmt.setDouble(5, price);
		}

		this.composition_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.composition_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_evaluate_typeStmt;
	
	private void initCv_evaluate_typeStmt()
	throws SQLException {
		this.cv_evaluate_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate_type ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_evaluate_typeStmt()
	throws SQLException {
		cv_evaluate_typeStmt.close();
	}	
	/**
	 * Cv_evaluate_type
	 * @param name Nombre del Tipo de Evaluacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_evaluate_type(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.cv_evaluate_typeStmt.setNull(1, 12);
		}
		else {
			this.cv_evaluate_typeStmt.setString(1, name);
		}

		this.cv_evaluate_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_evaluate_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pcategoryStmt;
	
	private void initPcategoryStmt()
	throws SQLException {
		this.pcategoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory ( name, detail_pattern, pcategory_group) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePcategoryStmt()
	throws SQLException {
		pcategoryStmt.close();
	}	
	/**
	 * Pcategory
	 * @param name Nombre de la Categoria
	 * @param detail_pattern Patron para los detalles de Articulos
	 * @param pcategory_group Identificador del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory(String name, String detail_pattern, Integer pcategory_group)
	throws SQLException {
	
		if ( name == null ) {
			this.pcategoryStmt.setNull(1, 12);
		}
		else {
			this.pcategoryStmt.setString(1, name);
		}
		if ( detail_pattern == null ) {
			this.pcategoryStmt.setNull(2, 12);
		}
		else {
			this.pcategoryStmt.setString(2, detail_pattern);
		}
		if ( pcategory_group == null ) {
			this.pcategoryStmt.setNull(3, 4);
		}
		else {
			this.pcategoryStmt.setInt(3, pcategory_group);
		}

		this.pcategoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pcategoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement warehouseStmt;
	
	private void initWarehouseStmt()
	throws SQLException {
		this.warehouseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO warehouse ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWarehouseStmt()
	throws SQLException {
		warehouseStmt.close();
	}	
	/**
	 * Warehouse
	 * @param name Nombre del Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWarehouse(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.warehouseStmt.setNull(1, 12);
		}
		else {
			this.warehouseStmt.setString(1, name);
		}

		this.warehouseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.warehouseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement customerStmt;
	
	private void initCustomerStmt()
	throws SQLException {
		this.customerStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer ( registry, tariff, taxfree, surcharge, withholding, transaction, status, segment, scope, e_invoice, delivery_grouped, delivery_valuated) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCustomerStmt()
	throws SQLException {
		customerStmt.close();
	}	
	/**
	 * Customer
	 * @param registry Registro del Cliente
	 * @param tariff Tarifa asociada al Cliente
	 * @param taxfree Indica si el Cliente esta exento de Impuestos
	 * @param surcharge Indica si el Cliente tiene recargo de equivalencia
	 * @param withholding Indica si el Cliente aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Cliente
	 * @param status Estado del Cliente
	 * @param segment Segmento del Cliente
	 * @param scope Identificador del Ambito
	 * @param e_invoice Indica si el Cliente desea recibir Facturas electronicas
	 * @param delivery_grouped Indica si el Cliente desea agrupar Albaranes en una sola Factura
	 * @param delivery_valuated Indica si el Cliente desea imprimir el Albaran valorado
	 * @throws SQLException
	*/
	protected void insertCustomer(Integer registry, Integer tariff, Boolean taxfree, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer segment, Integer scope, Boolean e_invoice, Boolean delivery_grouped, Boolean delivery_valuated)
	throws SQLException {
	
		if ( registry == null ) {
			this.customerStmt.setNull(1, 4);
		}
		else {
			this.customerStmt.setInt(1, registry);
		}
		if ( tariff == null ) {
			this.customerStmt.setNull(2, 4);
		}
		else {
			this.customerStmt.setInt(2, tariff);
		}
		if ( taxfree == null ) {
			this.customerStmt.setNull(3, -7);
		}
		else {
			this.customerStmt.setBoolean(3, taxfree);
		}
		if ( surcharge == null ) {
			this.customerStmt.setNull(4, -7);
		}
		else {
			this.customerStmt.setBoolean(4, surcharge);
		}
		if ( withholding == null ) {
			this.customerStmt.setNull(5, -7);
		}
		else {
			this.customerStmt.setBoolean(5, withholding);
		}
		if ( transaction == null ) {
			this.customerStmt.setNull(6, -6);
		}
		else {
			this.customerStmt.setShort(6, transaction);
		}
		if ( status == null ) {
			this.customerStmt.setNull(7, -6);
		}
		else {
			this.customerStmt.setShort(7, status);
		}
		if ( segment == null ) {
			this.customerStmt.setNull(8, 4);
		}
		else {
			this.customerStmt.setInt(8, segment);
		}
		if ( scope == null ) {
			this.customerStmt.setNull(9, 4);
		}
		else {
			this.customerStmt.setInt(9, scope);
		}
		if ( e_invoice == null ) {
			this.customerStmt.setNull(10, -7);
		}
		else {
			this.customerStmt.setBoolean(10, e_invoice);
		}
		if ( delivery_grouped == null ) {
			this.customerStmt.setNull(11, -7);
		}
		else {
			this.customerStmt.setBoolean(11, delivery_grouped);
		}
		if ( delivery_valuated == null ) {
			this.customerStmt.setNull(12, -7);
		}
		else {
			this.customerStmt.setBoolean(12, delivery_valuated);
		}

		this.customerStmt.executeUpdate();
		
	}
	
	private PreparedStatement productionStmt;
	
	private void initProductionStmt()
	throws SQLException {
		this.productionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production ( description, lot_code, production_date, item, initial_quantity, quantity, price) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProductionStmt()
	throws SQLException {
		productionStmt.close();
	}	
	/**
	 * Production
	 * @param description Descripcion de la Produccion
	 * @param lot_code Codigo de lote de la Produccion
	 * @param production_date Fecha de Produccion
	 * @param item Identificador del Articulo producido
	 * @param initial_quantity Cantidad inicial del Articulo en la Composicion
	 * @param quantity Cantidad del Articulo producido
	 * @param price Precio del Articulo producido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduction(String description, String lot_code, Date production_date, Integer item, Double initial_quantity, Double quantity, Double price)
	throws SQLException {
	
		if ( description == null ) {
			this.productionStmt.setNull(1, 12);
		}
		else {
			this.productionStmt.setString(1, description);
		}
		if ( lot_code == null ) {
			this.productionStmt.setNull(2, 12);
		}
		else {
			this.productionStmt.setString(2, lot_code);
		}
		if ( production_date == null ) {
			this.productionStmt.setNull(3, 91);
		}
		else {
			this.productionStmt.setDate(3, production_date);
		}
		if ( item == null ) {
			this.productionStmt.setNull(4, 4);
		}
		else {
			this.productionStmt.setInt(4, item);
		}
		if ( initial_quantity == null ) {
			this.productionStmt.setNull(5, 8);
		}
		else {
			this.productionStmt.setDouble(5, initial_quantity);
		}
		if ( quantity == null ) {
			this.productionStmt.setNull(6, 8);
		}
		else {
			this.productionStmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			this.productionStmt.setNull(7, 8);
		}
		else {
			this.productionStmt.setDouble(7, price);
		}

		this.productionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.productionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement message_contentStmt;
	
	private void initMessage_contentStmt()
	throws SQLException {
		this.message_contentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO message_content ( content) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMessage_contentStmt()
	throws SQLException {
		message_contentStmt.close();
	}	
	/**
	 * Message_content
	 * @param content Contenido del Mensaje
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMessage_content(InputStream content)
	throws SQLException {
	
		if ( content == null ) {
			this.message_contentStmt.setNull(1, -1);
		}
		else {
			this.message_contentStmt.setAsciiStream(1, content);
		}

		this.message_contentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.message_contentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement posStmt;
	
	private void initPosStmt()
	throws SQLException {
		this.posStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pos ( description, raddress) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePosStmt()
	throws SQLException {
		posStmt.close();
	}	
	/**
	 * Pos
	 * @param description Descripcion del Centro de Venta
	 * @param raddress Identificador de la Direccion asociada al Centro de Venta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPos(String description, Integer raddress)
	throws SQLException {
	
		if ( description == null ) {
			this.posStmt.setNull(1, 12);
		}
		else {
			this.posStmt.setString(1, description);
		}
		if ( raddress == null ) {
			this.posStmt.setNull(2, 4);
		}
		else {
			this.posStmt.setInt(2, raddress);
		}

		this.posStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.posStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_taxStmt;
	
	private void initInvoice_taxStmt()
	throws SQLException {
		this.invoice_taxStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_tax ( invoice_detail, tax_type, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_quota) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_taxStmt()
	throws SQLException {
		invoice_taxStmt.close();
	}	
	/**
	 * Invoice_tax
	 * @param invoice_detail Identificador del Detalle de la Factura
	 * @param tax_type Tipo de Impuesto del Detalle de la Factura
	 * @param percentage Porcentaje de Impuesto del Detalle de la Factura
	 * @param surcharge Porcentaje del recargo de equivalencia del Detalle de la Factura
	 * @param quota Cuota de Impuesto del Detalle de la Factura
	 * @param surcharge_quota Cuota de recargo de equivalencia del Detalle de la Factura
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param deductible_quota Cuota deducible
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_tax(Integer invoice_detail, Short tax_type, Double percentage, Double surcharge, Double quota, Double surcharge_quota, Short vat_deduction_type, Short withholding_type, Double deductible_quota)
	throws SQLException {
	
		if ( invoice_detail == null ) {
			this.invoice_taxStmt.setNull(1, 4);
		}
		else {
			this.invoice_taxStmt.setInt(1, invoice_detail);
		}
		if ( tax_type == null ) {
			this.invoice_taxStmt.setNull(2, -6);
		}
		else {
			this.invoice_taxStmt.setShort(2, tax_type);
		}
		if ( percentage == null ) {
			this.invoice_taxStmt.setNull(3, 8);
		}
		else {
			this.invoice_taxStmt.setDouble(3, percentage);
		}
		if ( surcharge == null ) {
			this.invoice_taxStmt.setNull(4, 8);
		}
		else {
			this.invoice_taxStmt.setDouble(4, surcharge);
		}
		if ( quota == null ) {
			this.invoice_taxStmt.setNull(5, 8);
		}
		else {
			this.invoice_taxStmt.setDouble(5, quota);
		}
		if ( surcharge_quota == null ) {
			this.invoice_taxStmt.setNull(6, 8);
		}
		else {
			this.invoice_taxStmt.setDouble(6, surcharge_quota);
		}
		if ( vat_deduction_type == null ) {
			this.invoice_taxStmt.setNull(7, -6);
		}
		else {
			this.invoice_taxStmt.setShort(7, vat_deduction_type);
		}
		if ( withholding_type == null ) {
			this.invoice_taxStmt.setNull(8, -6);
		}
		else {
			this.invoice_taxStmt.setShort(8, withholding_type);
		}
		if ( deductible_quota == null ) {
			this.invoice_taxStmt.setNull(9, 8);
		}
		else {
			this.invoice_taxStmt.setDouble(9, deductible_quota);
		}

		this.invoice_taxStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_taxStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement iattachStmt;
	
	private void initIattachStmt()
	throws SQLException {
		this.iattachStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO iattach ( item, mimeType, description, data, type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeIattachStmt()
	throws SQLException {
		iattachStmt.close();
	}	
	/**
	 * Iattach
	 * @param item Identificador de Articulo
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIattach(Integer item, Short mimeType, String description, InputStream data, Short type)
	throws SQLException {
	
		if ( item == null ) {
			this.iattachStmt.setNull(1, 4);
		}
		else {
			this.iattachStmt.setInt(1, item);
		}
		if ( mimeType == null ) {
			this.iattachStmt.setNull(2, -6);
		}
		else {
			this.iattachStmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			this.iattachStmt.setNull(3, 12);
		}
		else {
			this.iattachStmt.setString(3, description);
		}
		if ( data == null ) {
			this.iattachStmt.setNull(4, -4);
		}
		else {
			this.iattachStmt.setBinaryStream(4, data);
		}
		if ( type == null ) {
			this.iattachStmt.setNull(5, -6);
		}
		else {
			this.iattachStmt.setShort(5, type);
		}

		this.iattachStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.iattachStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tas_itemStmt;
	
	private void initTas_itemStmt()
	throws SQLException {
		this.tas_itemStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_item ( model, publicCode, privateCode, description, add_info) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTas_itemStmt()
	throws SQLException {
		tas_itemStmt.close();
	}	
	/**
	 * Tas_item
	 * @param model Identificador del Modelo
	 * @param publicCode Codigo publico del Articulo
	 * @param privateCode Codigo privado del Articulo
	 * @param description Descripcion del Articulo
	 * @param add_info Informacion adicional del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTas_item(Integer model, String publicCode, String privateCode, String description, String add_info)
	throws SQLException {
	
		if ( model == null ) {
			this.tas_itemStmt.setNull(1, 4);
		}
		else {
			this.tas_itemStmt.setInt(1, model);
		}
		if ( publicCode == null ) {
			this.tas_itemStmt.setNull(2, 12);
		}
		else {
			this.tas_itemStmt.setString(2, publicCode);
		}
		if ( privateCode == null ) {
			this.tas_itemStmt.setNull(3, 12);
		}
		else {
			this.tas_itemStmt.setString(3, privateCode);
		}
		if ( description == null ) {
			this.tas_itemStmt.setNull(4, 12);
		}
		else {
			this.tas_itemStmt.setString(4, description);
		}
		if ( add_info == null ) {
			this.tas_itemStmt.setNull(5, 12);
		}
		else {
			this.tas_itemStmt.setString(5, add_info);
		}

		this.tas_itemStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tas_itemStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tas_offerStmt;
	
	private void initTas_offerStmt()
	throws SQLException {
		this.tas_offerStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_offer ( support_order, offer) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTas_offerStmt()
	throws SQLException {
		tas_offerStmt.close();
	}	
	/**
	 * Tas_offer
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param offer Identificador del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTas_offer(Integer support_order, Integer offer)
	throws SQLException {
	
		if ( support_order == null ) {
			this.tas_offerStmt.setNull(1, 4);
		}
		else {
			this.tas_offerStmt.setInt(1, support_order);
		}
		if ( offer == null ) {
			this.tas_offerStmt.setNull(2, 4);
		}
		else {
			this.tas_offerStmt.setInt(2, offer);
		}

		this.tas_offerStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tas_offerStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement salaryStmt;
	
	private void initSalaryStmt()
	throws SQLException {
		this.salaryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO salary ( contract, start_date, end_date, address, employee, category, registration, total_days_hours, total_payment, total_deduction, total_liquid, broadcast_date, remuneration, extra_pay_proration, total, common_base, professional_base, overtime_base, irpf_base) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSalaryStmt()
	throws SQLException {
		salaryStmt.close();
	}	
	/**
	 * Salary
	 * @param contract Contrato
	 * @param start_date Fecha de inicio liquidaciÛn
	 * @param end_date Fecha de finalizacion liquidaciÛn
	 * @param address Domicilio de la empresa
	 * @param employee Nombre del trabajador
	 * @param category Categoria o grupo profesional
	 * @param registration N˙mero libro de matricula
	 * @param total_days_hours total dias/horas
	 * @param total_payment Total devengado
	 * @param total_deduction Total a deducir
	 * @param total_liquid Liquido total a percibir
	 * @param broadcast_date Fecha de emisiÛn
	 * @param remuneration RemuneraciÛn mensual
	 * @param extra_pay_proration Prorrateo de pagas extras
	 * @param total Total
	 * @param common_base Base de cotizacion por contingencias comunes
	 * @param professional_base Base de cotizacion por contingencias profesionales
	 * @param overtime_base Base de cotizacion adicional por horas extraordinarias
	 * @param irpf_base Base sujeta a retenciÛn I.R.P.F
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary(Integer contract, Date start_date, Date end_date, String address, String employee, String category, Integer registration, Integer total_days_hours, Double total_payment, Double total_deduction, Double total_liquid, Date broadcast_date, Double remuneration, Double extra_pay_proration, Double total, Double common_base, Double professional_base, Double overtime_base, Double irpf_base)
	throws SQLException {
	
		if ( contract == null ) {
			this.salaryStmt.setNull(1, 4);
		}
		else {
			this.salaryStmt.setInt(1, contract);
		}
		if ( start_date == null ) {
			this.salaryStmt.setNull(2, 91);
		}
		else {
			this.salaryStmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			this.salaryStmt.setNull(3, 91);
		}
		else {
			this.salaryStmt.setDate(3, end_date);
		}
		if ( address == null ) {
			this.salaryStmt.setNull(4, 12);
		}
		else {
			this.salaryStmt.setString(4, address);
		}
		if ( employee == null ) {
			this.salaryStmt.setNull(5, 12);
		}
		else {
			this.salaryStmt.setString(5, employee);
		}
		if ( category == null ) {
			this.salaryStmt.setNull(6, 12);
		}
		else {
			this.salaryStmt.setString(6, category);
		}
		if ( registration == null ) {
			this.salaryStmt.setNull(7, 4);
		}
		else {
			this.salaryStmt.setInt(7, registration);
		}
		if ( total_days_hours == null ) {
			this.salaryStmt.setNull(8, 4);
		}
		else {
			this.salaryStmt.setInt(8, total_days_hours);
		}
		if ( total_payment == null ) {
			this.salaryStmt.setNull(9, 8);
		}
		else {
			this.salaryStmt.setDouble(9, total_payment);
		}
		if ( total_deduction == null ) {
			this.salaryStmt.setNull(10, 8);
		}
		else {
			this.salaryStmt.setDouble(10, total_deduction);
		}
		if ( total_liquid == null ) {
			this.salaryStmt.setNull(11, 8);
		}
		else {
			this.salaryStmt.setDouble(11, total_liquid);
		}
		if ( broadcast_date == null ) {
			this.salaryStmt.setNull(12, 91);
		}
		else {
			this.salaryStmt.setDate(12, broadcast_date);
		}
		if ( remuneration == null ) {
			this.salaryStmt.setNull(13, 8);
		}
		else {
			this.salaryStmt.setDouble(13, remuneration);
		}
		if ( extra_pay_proration == null ) {
			this.salaryStmt.setNull(14, 8);
		}
		else {
			this.salaryStmt.setDouble(14, extra_pay_proration);
		}
		if ( total == null ) {
			this.salaryStmt.setNull(15, 8);
		}
		else {
			this.salaryStmt.setDouble(15, total);
		}
		if ( common_base == null ) {
			this.salaryStmt.setNull(16, 8);
		}
		else {
			this.salaryStmt.setDouble(16, common_base);
		}
		if ( professional_base == null ) {
			this.salaryStmt.setNull(17, 8);
		}
		else {
			this.salaryStmt.setDouble(17, professional_base);
		}
		if ( overtime_base == null ) {
			this.salaryStmt.setNull(18, 8);
		}
		else {
			this.salaryStmt.setDouble(18, overtime_base);
		}
		if ( irpf_base == null ) {
			this.salaryStmt.setNull(19, 8);
		}
		else {
			this.salaryStmt.setDouble(19, irpf_base);
		}

		this.salaryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.salaryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commissionStmt;
	
	private void initCommissionStmt()
	throws SQLException {
		this.commissionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission ( name, start_date, end_date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommissionStmt()
	throws SQLException {
		commissionStmt.close();
	}	
	/**
	 * Commission
	 * @param name Descripcion de la Comision
	 * @param start_date Fecha de inicio de la Comision
	 * @param end_date Fecha de fin de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission(String name, Date start_date, Date end_date)
	throws SQLException {
	
		if ( name == null ) {
			this.commissionStmt.setNull(1, 12);
		}
		else {
			this.commissionStmt.setString(1, name);
		}
		if ( start_date == null ) {
			this.commissionStmt.setNull(2, 91);
		}
		else {
			this.commissionStmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			this.commissionStmt.setNull(3, 91);
		}
		else {
			this.commissionStmt.setDate(3, end_date);
		}

		this.commissionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commissionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement support_order_insuranceStmt;
	
	private void initSupport_order_insuranceStmt()
	throws SQLException {
		this.support_order_insuranceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO support_order_insurance ( support_order, insurance, appraiser, claim_number, policy_type, franchise) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSupport_order_insuranceStmt()
	throws SQLException {
		support_order_insuranceStmt.close();
	}	
	/**
	 * Support_order_insurance
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param insurance Identificador de la Compaùia de Seguros
	 * @param appraiser Identificador del Perito
	 * @param claim_number Numero de siniestro o reclamacion
	 * @param policy_type Tipo de poliza
	 * @param franchise Franquicia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupport_order_insurance(Integer support_order, Integer insurance, Integer appraiser, String claim_number, String policy_type, String franchise)
	throws SQLException {
	
		if ( support_order == null ) {
			this.support_order_insuranceStmt.setNull(1, 4);
		}
		else {
			this.support_order_insuranceStmt.setInt(1, support_order);
		}
		if ( insurance == null ) {
			this.support_order_insuranceStmt.setNull(2, 4);
		}
		else {
			this.support_order_insuranceStmt.setInt(2, insurance);
		}
		if ( appraiser == null ) {
			this.support_order_insuranceStmt.setNull(3, 4);
		}
		else {
			this.support_order_insuranceStmt.setInt(3, appraiser);
		}
		if ( claim_number == null ) {
			this.support_order_insuranceStmt.setNull(4, 12);
		}
		else {
			this.support_order_insuranceStmt.setString(4, claim_number);
		}
		if ( policy_type == null ) {
			this.support_order_insuranceStmt.setNull(5, 12);
		}
		else {
			this.support_order_insuranceStmt.setString(5, policy_type);
		}
		if ( franchise == null ) {
			this.support_order_insuranceStmt.setNull(6, 12);
		}
		else {
			this.support_order_insuranceStmt.setString(6, franchise);
		}

		this.support_order_insuranceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.support_order_insuranceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement supplierStmt;
	
	private void initSupplierStmt()
	throws SQLException {
		this.supplierStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier ( withholding, transaction, status, segment, scope) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSupplierStmt()
	throws SQLException {
		supplierStmt.close();
	}	
	/**
	 * Supplier
	 * @param withholding Indica si el Proveedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Proveedor
	 * @param status Estado del Proveedor
	 * @param segment Segmento del Proveedor
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier(Boolean withholding, Short transaction, Short status, Integer segment, Integer scope)
	throws SQLException {
	
		if ( withholding == null ) {
			this.supplierStmt.setNull(1, -7);
		}
		else {
			this.supplierStmt.setBoolean(1, withholding);
		}
		if ( transaction == null ) {
			this.supplierStmt.setNull(2, -6);
		}
		else {
			this.supplierStmt.setShort(2, transaction);
		}
		if ( status == null ) {
			this.supplierStmt.setNull(3, -6);
		}
		else {
			this.supplierStmt.setShort(3, status);
		}
		if ( segment == null ) {
			this.supplierStmt.setNull(4, 4);
		}
		else {
			this.supplierStmt.setInt(4, segment);
		}
		if ( scope == null ) {
			this.supplierStmt.setNull(5, 4);
		}
		else {
			this.supplierStmt.setInt(5, scope);
		}

		this.supplierStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.supplierStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement user_scopeStmt;
	
	private void initUser_scopeStmt()
	throws SQLException {
		this.user_scopeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user_scope ( user_id, scope) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeUser_scopeStmt()
	throws SQLException {
		user_scopeStmt.close();
	}	
	/**
	 * User_scope
	 * @param user_id Identificador del Usuario
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser_scope(Integer user_id, Integer scope)
	throws SQLException {
	
		if ( user_id == null ) {
			this.user_scopeStmt.setNull(1, 4);
		}
		else {
			this.user_scopeStmt.setInt(1, user_id);
		}
		if ( scope == null ) {
			this.user_scopeStmt.setNull(2, 4);
		}
		else {
			this.user_scopeStmt.setInt(2, scope);
		}

		this.user_scopeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.user_scopeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement assetStmt;
	
	private void initAssetStmt()
	throws SQLException {
		this.assetStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO asset ( description, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAssetStmt()
	throws SQLException {
		assetStmt.close();
	}	
	/**
	 * Asset
	 * @param description Descripcion del Activo
	 * @param name Nombre corto del Activo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAsset(String description, String name)
	throws SQLException {
	
		if ( description == null ) {
			this.assetStmt.setNull(1, 12);
		}
		else {
			this.assetStmt.setString(1, description);
		}
		if ( name == null ) {
			this.assetStmt.setNull(2, 12);
		}
		else {
			this.assetStmt.setString(2, name);
		}

		this.assetStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.assetStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement item_warehouseStmt;
	
	private void initItem_warehouseStmt()
	throws SQLException {
		this.item_warehouseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_warehouse ( item, warehouse, stock_max, stock_min, location) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItem_warehouseStmt()
	throws SQLException {
		item_warehouseStmt.close();
	}	
	/**
	 * Item_warehouse
	 * @param item Identificador de Articulo
	 * @param warehouse Identificador de Almacen
	 * @param stock_max Stock maximo del Articulo en el Almacen
	 * @param stock_min Stock minimo del Articulo en el Almacen
	 * @param location Localizacion del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_warehouse(Integer item, Integer warehouse, Double stock_max, Double stock_min, String location)
	throws SQLException {
	
		if ( item == null ) {
			this.item_warehouseStmt.setNull(1, 4);
		}
		else {
			this.item_warehouseStmt.setInt(1, item);
		}
		if ( warehouse == null ) {
			this.item_warehouseStmt.setNull(2, 4);
		}
		else {
			this.item_warehouseStmt.setInt(2, warehouse);
		}
		if ( stock_max == null ) {
			this.item_warehouseStmt.setNull(3, 8);
		}
		else {
			this.item_warehouseStmt.setDouble(3, stock_max);
		}
		if ( stock_min == null ) {
			this.item_warehouseStmt.setNull(4, 8);
		}
		else {
			this.item_warehouseStmt.setDouble(4, stock_min);
		}
		if ( location == null ) {
			this.item_warehouseStmt.setNull(5, 12);
		}
		else {
			this.item_warehouseStmt.setString(5, location);
		}

		this.item_warehouseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.item_warehouseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rnoteStmt;
	
	private void initRnoteStmt()
	throws SQLException {
		this.rnoteStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rnote ( registry, description, note_date, comments, note_type, security_level) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRnoteStmt()
	throws SQLException {
		rnoteStmt.close();
	}	
	/**
	 * Rnote
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param description Descripcion de la Nota
	 * @param note_date Fecha de la Nota
	 * @param comments Comentarios de la Nota
	 * @param note_type 
	 * @param security_level Nivel de seguridad de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRnote(Integer registry, String description, Date note_date, InputStream comments, Short note_type, Short security_level)
	throws SQLException {
	
		if ( registry == null ) {
			this.rnoteStmt.setNull(1, 4);
		}
		else {
			this.rnoteStmt.setInt(1, registry);
		}
		if ( description == null ) {
			this.rnoteStmt.setNull(2, 12);
		}
		else {
			this.rnoteStmt.setString(2, description);
		}
		if ( note_date == null ) {
			this.rnoteStmt.setNull(3, 91);
		}
		else {
			this.rnoteStmt.setDate(3, note_date);
		}
		if ( comments == null ) {
			this.rnoteStmt.setNull(4, -1);
		}
		else {
			this.rnoteStmt.setAsciiStream(4, comments);
		}
		if ( note_type == null ) {
			this.rnoteStmt.setNull(5, -6);
		}
		else {
			this.rnoteStmt.setShort(5, note_type);
		}
		if ( security_level == null ) {
			this.rnoteStmt.setNull(6, -6);
		}
		else {
			this.rnoteStmt.setShort(6, security_level);
		}

		this.rnoteStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rnoteStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement favoriteStmt;
	
	private void initFavoriteStmt()
	throws SQLException {
		this.favoriteStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO favorite ( favorite_category, description, url, user_id) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFavoriteStmt()
	throws SQLException {
		favoriteStmt.close();
	}	
	/**
	 * Favorite
	 * @param favorite_category Categoria a la que pertenece el Favorito
	 * @param description Descripcion del Favorito
	 * @param url Url del Favorito
	 * @param user_id Usuario al que pertenece el Favorito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFavorite(Integer favorite_category, String description, String url, Integer user_id)
	throws SQLException {
	
		if ( favorite_category == null ) {
			this.favoriteStmt.setNull(1, 4);
		}
		else {
			this.favoriteStmt.setInt(1, favorite_category);
		}
		if ( description == null ) {
			this.favoriteStmt.setNull(2, 12);
		}
		else {
			this.favoriteStmt.setString(2, description);
		}
		if ( url == null ) {
			this.favoriteStmt.setNull(3, 12);
		}
		else {
			this.favoriteStmt.setString(3, url);
		}
		if ( user_id == null ) {
			this.favoriteStmt.setNull(4, 4);
		}
		else {
			this.favoriteStmt.setInt(4, user_id);
		}

		this.favoriteStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.favoriteStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement daily_trackingStmt;
	
	private void initDaily_trackingStmt()
	throws SQLException {
		this.daily_trackingStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO daily_tracking ( user_id, tracking_date, tracking_duration, job_type, customer, dossier, activity, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDaily_trackingStmt()
	throws SQLException {
		daily_trackingStmt.close();
	}	
	/**
	 * Daily_tracking
	 * @param user_id Identificador del Usuario que realiza el Parte
	 * @param tracking_date Fecha del Parte
	 * @param tracking_duration Tiempo invertido en el Parte
	 * @param job_type Tipo de Trabajo realizado en el Parte
	 * @param customer Identificador del Cliente asociado al Parte
	 * @param dossier Identificador del Expediente asociado al Parte
	 * @param activity Identificador de la Actividad asociada al Parte
	 * @param comments Comentarios del Parte
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDaily_tracking(Integer user_id, Date tracking_date, Double tracking_duration, Integer job_type, Integer customer, Integer dossier, Integer activity, InputStream comments)
	throws SQLException {
	
		if ( user_id == null ) {
			this.daily_trackingStmt.setNull(1, 4);
		}
		else {
			this.daily_trackingStmt.setInt(1, user_id);
		}
		if ( tracking_date == null ) {
			this.daily_trackingStmt.setNull(2, 91);
		}
		else {
			this.daily_trackingStmt.setDate(2, tracking_date);
		}
		if ( tracking_duration == null ) {
			this.daily_trackingStmt.setNull(3, 8);
		}
		else {
			this.daily_trackingStmt.setDouble(3, tracking_duration);
		}
		if ( job_type == null ) {
			this.daily_trackingStmt.setNull(4, 4);
		}
		else {
			this.daily_trackingStmt.setInt(4, job_type);
		}
		if ( customer == null ) {
			this.daily_trackingStmt.setNull(5, 4);
		}
		else {
			this.daily_trackingStmt.setInt(5, customer);
		}
		if ( dossier == null ) {
			this.daily_trackingStmt.setNull(6, 4);
		}
		else {
			this.daily_trackingStmt.setInt(6, dossier);
		}
		if ( activity == null ) {
			this.daily_trackingStmt.setNull(7, 4);
		}
		else {
			this.daily_trackingStmt.setInt(7, activity);
		}
		if ( comments == null ) {
			this.daily_trackingStmt.setNull(8, -1);
		}
		else {
			this.daily_trackingStmt.setAsciiStream(8, comments);
		}

		this.daily_trackingStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.daily_trackingStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement loanStmt;
	
	private void initLoanStmt()
	throws SQLException {
		this.loanStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO loan ( description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLoanStmt()
	throws SQLException {
		loanStmt.close();
	}	
	/**
	 * Loan
	 * @param description Descripcion del Prestamo
	 * @param loan_date Fecha de Concesion del Prestamo
	 * @param term Plazo
	 * @param interest Interes
	 * @param review Revision
	 * @param amount Importe
	 * @param expenses Gastos asociados al Prestamo
	 * @param rbank Banco por el que se paga el Prestamo
	 * @param security_level Nivel de Seguridad
	 * @param fee_amount Importe de la cuota
	 * @param recurrence Periodicidad
	 * @param pay_day Dia de Pago
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLoan(String description, Date loan_date, String term, String interest, String review, Double amount, Double expenses, Integer rbank, Short security_level, Double fee_amount, Integer recurrence, Integer pay_day, Short status)
	throws SQLException {
	
		if ( description == null ) {
			this.loanStmt.setNull(1, 12);
		}
		else {
			this.loanStmt.setString(1, description);
		}
		if ( loan_date == null ) {
			this.loanStmt.setNull(2, 91);
		}
		else {
			this.loanStmt.setDate(2, loan_date);
		}
		if ( term == null ) {
			this.loanStmt.setNull(3, 12);
		}
		else {
			this.loanStmt.setString(3, term);
		}
		if ( interest == null ) {
			this.loanStmt.setNull(4, 12);
		}
		else {
			this.loanStmt.setString(4, interest);
		}
		if ( review == null ) {
			this.loanStmt.setNull(5, 12);
		}
		else {
			this.loanStmt.setString(5, review);
		}
		if ( amount == null ) {
			this.loanStmt.setNull(6, 8);
		}
		else {
			this.loanStmt.setDouble(6, amount);
		}
		if ( expenses == null ) {
			this.loanStmt.setNull(7, 8);
		}
		else {
			this.loanStmt.setDouble(7, expenses);
		}
		if ( rbank == null ) {
			this.loanStmt.setNull(8, 4);
		}
		else {
			this.loanStmt.setInt(8, rbank);
		}
		if ( security_level == null ) {
			this.loanStmt.setNull(9, -6);
		}
		else {
			this.loanStmt.setShort(9, security_level);
		}
		if ( fee_amount == null ) {
			this.loanStmt.setNull(10, 8);
		}
		else {
			this.loanStmt.setDouble(10, fee_amount);
		}
		if ( recurrence == null ) {
			this.loanStmt.setNull(11, 4);
		}
		else {
			this.loanStmt.setInt(11, recurrence);
		}
		if ( pay_day == null ) {
			this.loanStmt.setNull(12, 4);
		}
		else {
			this.loanStmt.setInt(12, pay_day);
		}
		if ( status == null ) {
			this.loanStmt.setNull(13, -6);
		}
		else {
			this.loanStmt.setShort(13, status);
		}

		this.loanStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.loanStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tas_deliveryStmt;
	
	private void initTas_deliveryStmt()
	throws SQLException {
		this.tas_deliveryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_delivery ( support_order, delivery, offer) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTas_deliveryStmt()
	throws SQLException {
		tas_deliveryStmt.close();
	}	
	/**
	 * Tas_delivery
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param delivery Identificador del Albaran
	 * @param offer Identificador del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTas_delivery(Integer support_order, Integer delivery, Integer offer)
	throws SQLException {
	
		if ( support_order == null ) {
			this.tas_deliveryStmt.setNull(1, 4);
		}
		else {
			this.tas_deliveryStmt.setInt(1, support_order);
		}
		if ( delivery == null ) {
			this.tas_deliveryStmt.setNull(2, 4);
		}
		else {
			this.tas_deliveryStmt.setInt(2, delivery);
		}
		if ( offer == null ) {
			this.tas_deliveryStmt.setNull(3, 4);
		}
		else {
			this.tas_deliveryStmt.setInt(3, offer);
		}

		this.tas_deliveryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tas_deliveryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement question_valueStmt;
	
	private void initQuestion_valueStmt()
	throws SQLException {
		this.question_valueStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO question_value ( question, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeQuestion_valueStmt()
	throws SQLException {
		question_valueStmt.close();
	}	
	/**
	 * Question_value
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertQuestion_value(Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
	
		if ( question == null ) {
			this.question_valueStmt.setNull(1, 4);
		}
		else {
			this.question_valueStmt.setInt(1, question);
		}
		if ( value_text == null ) {
			this.question_valueStmt.setNull(2, 12);
		}
		else {
			this.question_valueStmt.setString(2, value_text);
		}
		if ( value_number == null ) {
			this.question_valueStmt.setNull(3, 8);
		}
		else {
			this.question_valueStmt.setDouble(3, value_number);
		}
		if ( value_date == null ) {
			this.question_valueStmt.setNull(4, 93);
		}
		else {
			this.question_valueStmt.setTimestamp(4, value_date);
		}

		this.question_valueStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.question_valueStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement web_info_styleStmt;
	
	private void initWeb_info_styleStmt()
	throws SQLException {
		this.web_info_styleStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_style ( variable, value) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWeb_info_styleStmt()
	throws SQLException {
		web_info_styleStmt.close();
	}	
	/**
	 * Web_info_style
	 * @param variable Nombre de la variable del Estilo
	 * @param value Valor de la variable del Estilo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_style(String variable, String value)
	throws SQLException {
	
		if ( variable == null ) {
			this.web_info_styleStmt.setNull(1, 12);
		}
		else {
			this.web_info_styleStmt.setString(1, variable);
		}
		if ( value == null ) {
			this.web_info_styleStmt.setNull(2, 12);
		}
		else {
			this.web_info_styleStmt.setString(2, value);
		}

		this.web_info_styleStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.web_info_styleStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement salary_paymentStmt;
	
	private void initSalary_paymentStmt()
	throws SQLException {
		this.salary_paymentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO salary_payment ( salary, type, description, function, amount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSalary_paymentStmt()
	throws SQLException {
		salary_paymentStmt.close();
	}	
	/**
	 * Salary_payment
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de PercepciÛn Salarial
	 * @param description Descripcion
	 * @param function FÛrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary_payment(Integer salary, Short type, String description, String function, Double amount)
	throws SQLException {
	
		if ( salary == null ) {
			this.salary_paymentStmt.setNull(1, 4);
		}
		else {
			this.salary_paymentStmt.setInt(1, salary);
		}
		if ( type == null ) {
			this.salary_paymentStmt.setNull(2, -6);
		}
		else {
			this.salary_paymentStmt.setShort(2, type);
		}
		if ( description == null ) {
			this.salary_paymentStmt.setNull(3, 12);
		}
		else {
			this.salary_paymentStmt.setString(3, description);
		}
		if ( function == null ) {
			this.salary_paymentStmt.setNull(4, 12);
		}
		else {
			this.salary_paymentStmt.setString(4, function);
		}
		if ( amount == null ) {
			this.salary_paymentStmt.setNull(5, 8);
		}
		else {
			this.salary_paymentStmt.setDouble(5, amount);
		}

		this.salary_paymentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.salary_paymentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contract_dataStmt;
	
	private void initContract_dataStmt()
	throws SQLException {
		this.contract_dataStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_data ( contract, code, description, conditions, start_date, end_date) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_dataStmt()
	throws SQLException {
		contract_dataStmt.close();
	}	
	/**
	 * Contract_data
	 * @param contract Contrato
	 * @param code CÛdigo S.S (TC2)
	 * @param description Descripcion
	 * @param conditions Condiciones
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_data(Integer contract, String code, String description, String conditions, Date start_date, Date end_date)
	throws SQLException {
	
		if ( contract == null ) {
			this.contract_dataStmt.setNull(1, 4);
		}
		else {
			this.contract_dataStmt.setInt(1, contract);
		}
		if ( code == null ) {
			this.contract_dataStmt.setNull(2, 12);
		}
		else {
			this.contract_dataStmt.setString(2, code);
		}
		if ( description == null ) {
			this.contract_dataStmt.setNull(3, 12);
		}
		else {
			this.contract_dataStmt.setString(3, description);
		}
		if ( conditions == null ) {
			this.contract_dataStmt.setNull(4, 12);
		}
		else {
			this.contract_dataStmt.setString(4, conditions);
		}
		if ( start_date == null ) {
			this.contract_dataStmt.setNull(5, 91);
		}
		else {
			this.contract_dataStmt.setDate(5, start_date);
		}
		if ( end_date == null ) {
			this.contract_dataStmt.setNull(6, 91);
		}
		else {
			this.contract_dataStmt.setDate(6, end_date);
		}

		this.contract_dataStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_dataStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_workexperienceStmt;
	
	private void initCv_workexperienceStmt()
	throws SQLException {
		this.cv_workexperienceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_workexperience ( startingdate, endingdate, job, company, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_workexperienceStmt()
	throws SQLException {
		cv_workexperienceStmt.close();
	}	
	/**
	 * Cv_workexperience
	 * @param startingdate Fecha de inicio de la Experiencia Laboral
	 * @param endingdate Fecha de finalizaciùn de la Experiencia Laboral
	 * @param job Trabajo desempeùado en la Experiencia Laboral
	 * @param company Compaùùa donde se desempeùù la Experiencia Laboral
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_workexperience(Date startingdate, Date endingdate, String job, String company, Integer curriculum)
	throws SQLException {
	
		if ( startingdate == null ) {
			this.cv_workexperienceStmt.setNull(1, 91);
		}
		else {
			this.cv_workexperienceStmt.setDate(1, startingdate);
		}
		if ( endingdate == null ) {
			this.cv_workexperienceStmt.setNull(2, 91);
		}
		else {
			this.cv_workexperienceStmt.setDate(2, endingdate);
		}
		if ( job == null ) {
			this.cv_workexperienceStmt.setNull(3, 12);
		}
		else {
			this.cv_workexperienceStmt.setString(3, job);
		}
		if ( company == null ) {
			this.cv_workexperienceStmt.setNull(4, 12);
		}
		else {
			this.cv_workexperienceStmt.setString(4, company);
		}
		if ( curriculum == null ) {
			this.cv_workexperienceStmt.setNull(5, 4);
		}
		else {
			this.cv_workexperienceStmt.setInt(5, curriculum);
		}

		this.cv_workexperienceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_workexperienceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement incidence_typeStmt;
	
	private void initIncidence_typeStmt()
	throws SQLException {
		this.incidence_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO incidence_type ( alias, description, compute) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeIncidence_typeStmt()
	throws SQLException {
		incidence_typeStmt.close();
	}	
	/**
	 * Incidence_type
	 * @param alias Alias del Tipo de Incidencia
	 * @param description Descripciùn del Tipo de Incidencia
	 * @param compute Indica la forma de computar las horas de la Incidencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncidence_type(String alias, String description, Boolean compute)
	throws SQLException {
	
		if ( alias == null ) {
			this.incidence_typeStmt.setNull(1, 12);
		}
		else {
			this.incidence_typeStmt.setString(1, alias);
		}
		if ( description == null ) {
			this.incidence_typeStmt.setNull(2, 12);
		}
		else {
			this.incidence_typeStmt.setString(2, description);
		}
		if ( compute == null ) {
			this.incidence_typeStmt.setNull(3, -7);
		}
		else {
			this.incidence_typeStmt.setBoolean(3, compute);
		}

		this.incidence_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.incidence_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement item_posStmt;
	
	private void initItem_posStmt()
	throws SQLException {
		this.item_posStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_pos ( item, plu, barcode, desc_short, plu_product_type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItem_posStmt()
	throws SQLException {
		item_posStmt.close();
	}	
	/**
	 * Item_pos
	 * @param item Identificador de Articulo
	 * @param plu Codigo PLU del Articulo
	 * @param barcode Codigo de barras del Articulo
	 * @param desc_short Descripcion corta del Articulo
	 * @param plu_product_type Tipo de Articulo en Balanza
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_pos(Integer item, String plu, String barcode, String desc_short, Short plu_product_type)
	throws SQLException {
	
		if ( item == null ) {
			this.item_posStmt.setNull(1, 4);
		}
		else {
			this.item_posStmt.setInt(1, item);
		}
		if ( plu == null ) {
			this.item_posStmt.setNull(2, 12);
		}
		else {
			this.item_posStmt.setString(2, plu);
		}
		if ( barcode == null ) {
			this.item_posStmt.setNull(3, 12);
		}
		else {
			this.item_posStmt.setString(3, barcode);
		}
		if ( desc_short == null ) {
			this.item_posStmt.setNull(4, 12);
		}
		else {
			this.item_posStmt.setString(4, desc_short);
		}
		if ( plu_product_type == null ) {
			this.item_posStmt.setNull(5, -6);
		}
		else {
			this.item_posStmt.setShort(5, plu_product_type);
		}

		this.item_posStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.item_posStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement ec_paymethodStmt;
	
	private void initEc_paymethodStmt()
	throws SQLException {
		this.ec_paymethodStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_paymethod ( pay_method, user_name, password, signature) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEc_paymethodStmt()
	throws SQLException {
		ec_paymethodStmt.close();
	}	
	/**
	 * Ec_paymethod
	 * @param pay_method Identificador de la Forma de Pago
	 * @param user_name Nombre de Usuario
	 * @param password Contraseùa para la pasarela de pago
	 * @param signature Identificador unico de la empresa para pasarela
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_paymethod(Integer pay_method, String user_name, String password, String signature)
	throws SQLException {
	
		if ( pay_method == null ) {
			this.ec_paymethodStmt.setNull(1, 4);
		}
		else {
			this.ec_paymethodStmt.setInt(1, pay_method);
		}
		if ( user_name == null ) {
			this.ec_paymethodStmt.setNull(2, 12);
		}
		else {
			this.ec_paymethodStmt.setString(2, user_name);
		}
		if ( password == null ) {
			this.ec_paymethodStmt.setNull(3, 12);
		}
		else {
			this.ec_paymethodStmt.setString(3, password);
		}
		if ( signature == null ) {
			this.ec_paymethodStmt.setNull(4, 12);
		}
		else {
			this.ec_paymethodStmt.setString(4, signature);
		}

		this.ec_paymethodStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.ec_paymethodStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement mk_actionStmt;
	
	private void initMk_actionStmt()
	throws SQLException {
		this.mk_actionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_action ( campaign, media_type, start_date, end_date, survey) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMk_actionStmt()
	throws SQLException {
		mk_actionStmt.close();
	}	
	/**
	 * Mk_action
	 * @param campaign Identificador de la Campaùa
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action(Integer campaign, Integer media_type, Timestamp start_date, Timestamp end_date, Integer survey)
	throws SQLException {
	
		if ( campaign == null ) {
			this.mk_actionStmt.setNull(1, 4);
		}
		else {
			this.mk_actionStmt.setInt(1, campaign);
		}
		if ( media_type == null ) {
			this.mk_actionStmt.setNull(2, 4);
		}
		else {
			this.mk_actionStmt.setInt(2, media_type);
		}
		if ( start_date == null ) {
			this.mk_actionStmt.setNull(3, 93);
		}
		else {
			this.mk_actionStmt.setTimestamp(3, start_date);
		}
		if ( end_date == null ) {
			this.mk_actionStmt.setNull(4, 93);
		}
		else {
			this.mk_actionStmt.setTimestamp(4, end_date);
		}
		if ( survey == null ) {
			this.mk_actionStmt.setNull(5, 4);
		}
		else {
			this.mk_actionStmt.setInt(5, survey);
		}

		this.mk_actionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.mk_actionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement fs_vat_detailStmt;
	
	private void initFs_vat_detailStmt()
	throws SQLException {
		this.fs_vat_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fs_vat_detail ( fs_vat, key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFs_vat_detailStmt()
	throws SQLException {
		fs_vat_detailStmt.close();
	}	
	/**
	 * Fs_vat_detail
	 * @param fs_vat Identificador de la Declaracion
	 * @param key Clave de la Declaracion
	 * @param percent Porcentaje de IVA
	 * @param taxable_base Base Imponible
	 * @param quota Cuota
	 * @param deductible_quota Cuota Deducible
	 * @param adj_taxable_base Base Imponible Ajustada
	 * @param adj_quota Cuota Ajustada
	 * @param adj_deductible_quota Cuota Deducible Ajustada
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_vat_detail(Integer fs_vat, Short key, Double percent, Double taxable_base, Double quota, Double deductible_quota, Double adj_taxable_base, Double adj_quota, Double adj_deductible_quota)
	throws SQLException {
	
		if ( fs_vat == null ) {
			this.fs_vat_detailStmt.setNull(1, 4);
		}
		else {
			this.fs_vat_detailStmt.setInt(1, fs_vat);
		}
		if ( key == null ) {
			this.fs_vat_detailStmt.setNull(2, -6);
		}
		else {
			this.fs_vat_detailStmt.setShort(2, key);
		}
		if ( percent == null ) {
			this.fs_vat_detailStmt.setNull(3, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(3, percent);
		}
		if ( taxable_base == null ) {
			this.fs_vat_detailStmt.setNull(4, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(4, taxable_base);
		}
		if ( quota == null ) {
			this.fs_vat_detailStmt.setNull(5, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(5, quota);
		}
		if ( deductible_quota == null ) {
			this.fs_vat_detailStmt.setNull(6, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(6, deductible_quota);
		}
		if ( adj_taxable_base == null ) {
			this.fs_vat_detailStmt.setNull(7, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(7, adj_taxable_base);
		}
		if ( adj_quota == null ) {
			this.fs_vat_detailStmt.setNull(8, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(8, adj_quota);
		}
		if ( adj_deductible_quota == null ) {
			this.fs_vat_detailStmt.setNull(9, 8);
		}
		else {
			this.fs_vat_detailStmt.setDouble(9, adj_deductible_quota);
		}

		this.fs_vat_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.fs_vat_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_languagesStmt;
	
	private void initCv_languagesStmt()
	throws SQLException {
		this.cv_languagesStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_languages ( language, spoken, wrote, read_level, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_languagesStmt()
	throws SQLException {
		cv_languagesStmt.close();
	}	
	/**
	 * Cv_languages
	 * @param language Idioma
	 * @param spoken Nivel oral del Idioma
	 * @param wrote Nivel escrito del Idioma
	 * @param read_level Nivel leùdo del Idioma
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_languages(Short language, Short spoken, Short wrote, Short read_level, Integer curriculum)
	throws SQLException {
	
		if ( language == null ) {
			this.cv_languagesStmt.setNull(1, -6);
		}
		else {
			this.cv_languagesStmt.setShort(1, language);
		}
		if ( spoken == null ) {
			this.cv_languagesStmt.setNull(2, -6);
		}
		else {
			this.cv_languagesStmt.setShort(2, spoken);
		}
		if ( wrote == null ) {
			this.cv_languagesStmt.setNull(3, -6);
		}
		else {
			this.cv_languagesStmt.setShort(3, wrote);
		}
		if ( read_level == null ) {
			this.cv_languagesStmt.setNull(4, -6);
		}
		else {
			this.cv_languagesStmt.setShort(4, read_level);
		}
		if ( curriculum == null ) {
			this.cv_languagesStmt.setNull(5, 4);
		}
		else {
			this.cv_languagesStmt.setInt(5, curriculum);
		}

		this.cv_languagesStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_languagesStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement offer_detail_commissionStmt;
	
	private void initOffer_detail_commissionStmt()
	throws SQLException {
		this.offer_detail_commissionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_detail_commission ( offer_detail, commission, amount, status, pay_date) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeOffer_detail_commissionStmt()
	throws SQLException {
		offer_detail_commissionStmt.close();
	}	
	/**
	 * Offer_detail_commission
	 * @param offer_detail Identificador de la Linea de Presupuesto
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_detail_commission(Integer offer_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
	
		if ( offer_detail == null ) {
			this.offer_detail_commissionStmt.setNull(1, 4);
		}
		else {
			this.offer_detail_commissionStmt.setInt(1, offer_detail);
		}
		if ( commission == null ) {
			this.offer_detail_commissionStmt.setNull(2, 8);
		}
		else {
			this.offer_detail_commissionStmt.setDouble(2, commission);
		}
		if ( amount == null ) {
			this.offer_detail_commissionStmt.setNull(3, 8);
		}
		else {
			this.offer_detail_commissionStmt.setDouble(3, amount);
		}
		if ( status == null ) {
			this.offer_detail_commissionStmt.setNull(4, -6);
		}
		else {
			this.offer_detail_commissionStmt.setShort(4, status);
		}
		if ( pay_date == null ) {
			this.offer_detail_commissionStmt.setNull(5, 91);
		}
		else {
			this.offer_detail_commissionStmt.setDate(5, pay_date);
		}

		this.offer_detail_commissionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.offer_detail_commissionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement favorite_categoryStmt;
	
	private void initFavorite_categoryStmt()
	throws SQLException {
		this.favorite_categoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO favorite_category ( description, user_id) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFavorite_categoryStmt()
	throws SQLException {
		favorite_categoryStmt.close();
	}	
	/**
	 * Favorite_category
	 * @param description Descripcion de la Categoria
	 * @param user_id Usuario al que pertenece la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFavorite_category(String description, Integer user_id)
	throws SQLException {
	
		if ( description == null ) {
			this.favorite_categoryStmt.setNull(1, 12);
		}
		else {
			this.favorite_categoryStmt.setString(1, description);
		}
		if ( user_id == null ) {
			this.favorite_categoryStmt.setNull(2, 4);
		}
		else {
			this.favorite_categoryStmt.setInt(2, user_id);
		}

		this.favorite_categoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.favorite_categoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement balance_detailStmt;
	
	private void initBalance_detailStmt()
	throws SQLException {
		this.balance_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO balance_detail ( balance, code, description, accounts, sortKey, title, internal_calculation, visible, zeroFlag, creditNature) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeBalance_detailStmt()
	throws SQLException {
		balance_detailStmt.close();
	}	
	/**
	 * Balance_detail
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description Descripciùn del detalle de balance
	 * @param accounts Cuentas separadas por comas, que forman el acumulado.
	 * @param sortKey Orden el que aparecera en el listado.
	 * @param title 
	 * @param internal_calculation Indica si es un calculo interno, es decir si el contenido
                de accounts son referencias a la columna -code- de esta tabla
	 * @param visible Si aparece o no en la impresion.
	 * @param zeroFlag Flag que se activa cuando la cuenta o cuentas tienen valor 0.
	 * @param creditNature Si es verdadero se hace una haber menos debe de las cuentas indicadas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBalance_detail(Integer balance, String code, String description, InputStream accounts, Integer sortKey, Boolean title, Boolean internal_calculation, Boolean visible, Boolean zeroFlag, Boolean creditNature)
	throws SQLException {
	
		if ( balance == null ) {
			this.balance_detailStmt.setNull(1, 4);
		}
		else {
			this.balance_detailStmt.setInt(1, balance);
		}
		if ( code == null ) {
			this.balance_detailStmt.setNull(2, 12);
		}
		else {
			this.balance_detailStmt.setString(2, code);
		}
		if ( description == null ) {
			this.balance_detailStmt.setNull(3, 12);
		}
		else {
			this.balance_detailStmt.setString(3, description);
		}
		if ( accounts == null ) {
			this.balance_detailStmt.setNull(4, -1);
		}
		else {
			this.balance_detailStmt.setAsciiStream(4, accounts);
		}
		if ( sortKey == null ) {
			this.balance_detailStmt.setNull(5, 4);
		}
		else {
			this.balance_detailStmt.setInt(5, sortKey);
		}
		if ( title == null ) {
			this.balance_detailStmt.setNull(6, -7);
		}
		else {
			this.balance_detailStmt.setBoolean(6, title);
		}
		if ( internal_calculation == null ) {
			this.balance_detailStmt.setNull(7, -7);
		}
		else {
			this.balance_detailStmt.setBoolean(7, internal_calculation);
		}
		if ( visible == null ) {
			this.balance_detailStmt.setNull(8, -7);
		}
		else {
			this.balance_detailStmt.setBoolean(8, visible);
		}
		if ( zeroFlag == null ) {
			this.balance_detailStmt.setNull(9, -7);
		}
		else {
			this.balance_detailStmt.setBoolean(9, zeroFlag);
		}
		if ( creditNature == null ) {
			this.balance_detailStmt.setNull(10, -7);
		}
		else {
			this.balance_detailStmt.setBoolean(10, creditNature);
		}

		this.balance_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.balance_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cnae93rev1Stmt;
	
	private void initCnae93rev1Stmt()
	throws SQLException {
		this.cnae93rev1Stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cnae93rev1 ( code, title) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCnae93rev1Stmt()
	throws SQLException {
		cnae93rev1Stmt.close();
	}	
	/**
	 * Cnae93rev1
	 * @param code Codigo del CNAE
	 * @param title Titulo del CNAE
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCnae93rev1(String code, String title)
	throws SQLException {
	
		if ( code == null ) {
			this.cnae93rev1Stmt.setNull(1, 12);
		}
		else {
			this.cnae93rev1Stmt.setString(1, code);
		}
		if ( title == null ) {
			this.cnae93rev1Stmt.setNull(2, 12);
		}
		else {
			this.cnae93rev1Stmt.setString(2, title);
		}

		this.cnae93rev1Stmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cnae93rev1Stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_tax_accountStmt;
	
	private void initInvoice_tax_accountStmt()
	throws SQLException {
		this.invoice_tax_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_tax_account ( invoice_tax, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_tax_accountStmt()
	throws SQLException {
		invoice_tax_accountStmt.close();
	}	
	/**
	 * Invoice_tax_account
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_tax_account(Integer invoice_tax, String account)
	throws SQLException {
	
		if ( invoice_tax == null ) {
			this.invoice_tax_accountStmt.setNull(1, 4);
		}
		else {
			this.invoice_tax_accountStmt.setInt(1, invoice_tax);
		}
		if ( account == null ) {
			this.invoice_tax_accountStmt.setNull(2, 1);
		}
		else {
			this.invoice_tax_accountStmt.setString(2, account);
		}

		this.invoice_tax_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_tax_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entry_detailStmt;
	
	private void initAccount_entry_detailStmt()
	throws SQLException {
		this.account_entry_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_detail ( account_entry, line, account, concept, balancing_account, debit, credit, document_number) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entry_detailStmt()
	throws SQLException {
		account_entry_detailStmt.close();
	}	
	/**
	 * Account_entry_detail
	 * @param account_entry Identificador del Asiento
	 * @param line Numero de linea del Apunte dentro del Asiento
	 * @param account Cuenta Contable del Apunte
	 * @param concept Concepto del Apunte
	 * @param balancing_account Contrapartida del Apunte
	 * @param debit Debe del Apunte
	 * @param credit Haber del Apunte
	 * @param document_number Numero de documento asociado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_detail(Integer account_entry, Integer line, String account, String concept, String balancing_account, Double debit, Double credit, String document_number)
	throws SQLException {
	
		if ( account_entry == null ) {
			this.account_entry_detailStmt.setNull(1, 4);
		}
		else {
			this.account_entry_detailStmt.setInt(1, account_entry);
		}
		if ( line == null ) {
			this.account_entry_detailStmt.setNull(2, 4);
		}
		else {
			this.account_entry_detailStmt.setInt(2, line);
		}
		if ( account == null ) {
			this.account_entry_detailStmt.setNull(3, 1);
		}
		else {
			this.account_entry_detailStmt.setString(3, account);
		}
		if ( concept == null ) {
			this.account_entry_detailStmt.setNull(4, 12);
		}
		else {
			this.account_entry_detailStmt.setString(4, concept);
		}
		if ( balancing_account == null ) {
			this.account_entry_detailStmt.setNull(5, 1);
		}
		else {
			this.account_entry_detailStmt.setString(5, balancing_account);
		}
		if ( debit == null ) {
			this.account_entry_detailStmt.setNull(6, 8);
		}
		else {
			this.account_entry_detailStmt.setDouble(6, debit);
		}
		if ( credit == null ) {
			this.account_entry_detailStmt.setNull(7, 8);
		}
		else {
			this.account_entry_detailStmt.setDouble(7, credit);
		}
		if ( document_number == null ) {
			this.account_entry_detailStmt.setNull(8, 12);
		}
		else {
			this.account_entry_detailStmt.setString(8, document_number);
		}

		this.account_entry_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entry_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pcategory_treeStmt;
	
	private void initPcategory_treeStmt()
	throws SQLException {
		this.pcategory_treeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory_tree ( parent, child) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePcategory_treeStmt()
	throws SQLException {
		pcategory_treeStmt.close();
	}	
	/**
	 * Pcategory_tree
	 * @param parent Identificador de la Categoria padre
	 * @param child Identificador de la Categoria hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_tree(Integer parent, Integer child)
	throws SQLException {
	
		if ( parent == null ) {
			this.pcategory_treeStmt.setNull(1, 4);
		}
		else {
			this.pcategory_treeStmt.setInt(1, parent);
		}
		if ( child == null ) {
			this.pcategory_treeStmt.setNull(2, 4);
		}
		else {
			this.pcategory_treeStmt.setInt(2, child);
		}

		this.pcategory_treeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pcategory_treeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement product_accountStmt;
	
	private void initProduct_accountStmt()
	throws SQLException {
		this.product_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO product_account ( product, account, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProduct_accountStmt()
	throws SQLException {
		product_accountStmt.close();
	}	
	/**
	 * Product_account
	 * @param product Identificador del Producto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Producto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduct_account(Integer product, String account, Short type)
	throws SQLException {
	
		if ( product == null ) {
			this.product_accountStmt.setNull(1, 4);
		}
		else {
			this.product_accountStmt.setInt(1, product);
		}
		if ( account == null ) {
			this.product_accountStmt.setNull(2, 1);
		}
		else {
			this.product_accountStmt.setString(2, account);
		}
		if ( type == null ) {
			this.product_accountStmt.setNull(3, -6);
		}
		else {
			this.product_accountStmt.setShort(3, type);
		}

		this.product_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.product_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tariff_catalogueStmt;
	
	private void initTariff_catalogueStmt()
	throws SQLException {
		this.tariff_catalogueStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tariff_catalogue ( tariff, catalogue) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTariff_catalogueStmt()
	throws SQLException {
		tariff_catalogueStmt.close();
	}	
	/**
	 * Tariff_catalogue
	 * @param tariff Identificador de la Tarifa
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTariff_catalogue(Integer tariff, Integer catalogue)
	throws SQLException {
	
		if ( tariff == null ) {
			this.tariff_catalogueStmt.setNull(1, 4);
		}
		else {
			this.tariff_catalogueStmt.setInt(1, tariff);
		}
		if ( catalogue == null ) {
			this.tariff_catalogueStmt.setNull(2, 4);
		}
		else {
			this.tariff_catalogueStmt.setInt(2, catalogue);
		}

		this.tariff_catalogueStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tariff_catalogueStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement supplier_segmentStmt;
	
	private void initSupplier_segmentStmt()
	throws SQLException {
		this.supplier_segmentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier_segment ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSupplier_segmentStmt()
	throws SQLException {
		supplier_segmentStmt.close();
	}	
	/**
	 * Supplier_segment
	 * @param description Descripcion del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier_segment(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.supplier_segmentStmt.setNull(1, 12);
		}
		else {
			this.supplier_segmentStmt.setString(1, description);
		}

		this.supplier_segmentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.supplier_segmentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement amortization_detailStmt;
	
	private void initAmortization_detailStmt()
	throws SQLException {
		this.amortization_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization_detail ( amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAmortization_detailStmt()
	throws SQLException {
		amortization_detailStmt.close();
	}	
	/**
	 * Amortization_detail
	 * @param amortization Ficha de Amortizacion
	 * @param from_date Desde fecha
	 * @param to_date Hasta fecha
	 * @param coefficient Coeficiente de Amortizacion
	 * @param allocation Dotacion de la Amortizacion
	 * @param status Estatus del Detalle de Amortizacion
	 * @param account_entry Posicion del Apunte Contable
	 * @param fiscal_allocation Dotacion fiscal
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAmortization_detail(Integer amortization, Date from_date, Date to_date, Double coefficient, Double allocation, Short status, Integer account_entry, Double fiscal_allocation)
	throws SQLException {
	
		if ( amortization == null ) {
			this.amortization_detailStmt.setNull(1, 4);
		}
		else {
			this.amortization_detailStmt.setInt(1, amortization);
		}
		if ( from_date == null ) {
			this.amortization_detailStmt.setNull(2, 91);
		}
		else {
			this.amortization_detailStmt.setDate(2, from_date);
		}
		if ( to_date == null ) {
			this.amortization_detailStmt.setNull(3, 91);
		}
		else {
			this.amortization_detailStmt.setDate(3, to_date);
		}
		if ( coefficient == null ) {
			this.amortization_detailStmt.setNull(4, 8);
		}
		else {
			this.amortization_detailStmt.setDouble(4, coefficient);
		}
		if ( allocation == null ) {
			this.amortization_detailStmt.setNull(5, 8);
		}
		else {
			this.amortization_detailStmt.setDouble(5, allocation);
		}
		if ( status == null ) {
			this.amortization_detailStmt.setNull(6, -6);
		}
		else {
			this.amortization_detailStmt.setShort(6, status);
		}
		if ( account_entry == null ) {
			this.amortization_detailStmt.setNull(7, 4);
		}
		else {
			this.amortization_detailStmt.setInt(7, account_entry);
		}
		if ( fiscal_allocation == null ) {
			this.amortization_detailStmt.setNull(8, 8);
		}
		else {
			this.amortization_detailStmt.setDouble(8, fiscal_allocation);
		}

		this.amortization_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.amortization_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement noticeStmt;
	
	private void initNoticeStmt()
	throws SQLException {
		this.noticeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO notice ( date, sender, work_group, recipient, source, company, phone, subject, status, type, priority) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeNoticeStmt()
	throws SQLException {
		noticeStmt.close();
	}	
	/**
	 * Notice
	 * @param date Fecha y hora en la que se produjo el Aviso
	 * @param sender Remitente del Aviso
	 * @param work_group Grupo de Trabajo al que va dirigida el Aviso
	 * @param recipient Destinatario del Aviso
	 * @param source Origen del Aviso
	 * @param company Empresa para la que trabaja el origen del Aviso
	 * @param phone Telefono para contactar con el origen del Aviso
	 * @param subject Asunto del Aviso
	 * @param status Estado del Aviso
	 * @param type Tipo de Aviso
	 * @param priority Prioridad del Aviso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertNotice(Timestamp date, Integer sender, Integer work_group, Integer recipient, String source, String company, String phone, InputStream subject, Short status, Short type, Short priority)
	throws SQLException {
	
		if ( date == null ) {
			this.noticeStmt.setNull(1, 93);
		}
		else {
			this.noticeStmt.setTimestamp(1, date);
		}
		if ( sender == null ) {
			this.noticeStmt.setNull(2, 4);
		}
		else {
			this.noticeStmt.setInt(2, sender);
		}
		if ( work_group == null ) {
			this.noticeStmt.setNull(3, 4);
		}
		else {
			this.noticeStmt.setInt(3, work_group);
		}
		if ( recipient == null ) {
			this.noticeStmt.setNull(4, 4);
		}
		else {
			this.noticeStmt.setInt(4, recipient);
		}
		if ( source == null ) {
			this.noticeStmt.setNull(5, 12);
		}
		else {
			this.noticeStmt.setString(5, source);
		}
		if ( company == null ) {
			this.noticeStmt.setNull(6, 12);
		}
		else {
			this.noticeStmt.setString(6, company);
		}
		if ( phone == null ) {
			this.noticeStmt.setNull(7, 12);
		}
		else {
			this.noticeStmt.setString(7, phone);
		}
		if ( subject == null ) {
			this.noticeStmt.setNull(8, -1);
		}
		else {
			this.noticeStmt.setAsciiStream(8, subject);
		}
		if ( status == null ) {
			this.noticeStmt.setNull(9, -6);
		}
		else {
			this.noticeStmt.setShort(9, status);
		}
		if ( type == null ) {
			this.noticeStmt.setNull(10, -6);
		}
		else {
			this.noticeStmt.setShort(10, type);
		}
		if ( priority == null ) {
			this.noticeStmt.setNull(11, -6);
		}
		else {
			this.noticeStmt.setShort(11, priority);
		}

		this.noticeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.noticeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement production_detailStmt;
	
	private void initProduction_detailStmt()
	throws SQLException {
		this.production_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production_detail ( production, item, description, initial_quantity, quantity, price) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProduction_detailStmt()
	throws SQLException {
		production_detailStmt.close();
	}	
	/**
	 * Production_detail
	 * @param production Identificador de la Produccion
	 * @param item Identificador del Articulo subproducto
	 * @param description Descripcion del Articulo subproducto
	 * @param initial_quantity Cantidad inicial del Articulo subproducto en la Composicion
	 * @param quantity Cantidad del Articulo subproducto
	 * @param price Precio del Articulo subproducto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduction_detail(Integer production, Integer item, String description, Double initial_quantity, Double quantity, Double price)
	throws SQLException {
	
		if ( production == null ) {
			this.production_detailStmt.setNull(1, 4);
		}
		else {
			this.production_detailStmt.setInt(1, production);
		}
		if ( item == null ) {
			this.production_detailStmt.setNull(2, 4);
		}
		else {
			this.production_detailStmt.setInt(2, item);
		}
		if ( description == null ) {
			this.production_detailStmt.setNull(3, 12);
		}
		else {
			this.production_detailStmt.setString(3, description);
		}
		if ( initial_quantity == null ) {
			this.production_detailStmt.setNull(4, 8);
		}
		else {
			this.production_detailStmt.setDouble(4, initial_quantity);
		}
		if ( quantity == null ) {
			this.production_detailStmt.setNull(5, 8);
		}
		else {
			this.production_detailStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.production_detailStmt.setNull(6, 8);
		}
		else {
			this.production_detailStmt.setDouble(6, price);
		}

		this.production_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.production_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement delivery_detailStmt;
	
	private void initDelivery_detailStmt()
	throws SQLException {
		this.delivery_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery_detail ( delivery, line, item, description, warehouse, quantity, price, discount_expr, type, source, sales_detail) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDelivery_detailStmt()
	throws SQLException {
		delivery_detailStmt.close();
	}	
	/**
	 * Delivery_detail
	 * @param delivery Identificador del Albaran de Venta
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param type Tipo de Detalle de Albaran
	 * @param source Origen del Detalle de Albaran
	 * @param sales_detail Identificador del Detalle del Pedido de Venta asociado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery_detail(Integer delivery, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Short type, Short source, Integer sales_detail)
	throws SQLException {
	
		if ( delivery == null ) {
			this.delivery_detailStmt.setNull(1, 4);
		}
		else {
			this.delivery_detailStmt.setInt(1, delivery);
		}
		if ( line == null ) {
			this.delivery_detailStmt.setNull(2, 5);
		}
		else {
			this.delivery_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.delivery_detailStmt.setNull(3, 4);
		}
		else {
			this.delivery_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.delivery_detailStmt.setNull(4, 12);
		}
		else {
			this.delivery_detailStmt.setString(4, description);
		}
		if ( warehouse == null ) {
			this.delivery_detailStmt.setNull(5, 4);
		}
		else {
			this.delivery_detailStmt.setInt(5, warehouse);
		}
		if ( quantity == null ) {
			this.delivery_detailStmt.setNull(6, 8);
		}
		else {
			this.delivery_detailStmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			this.delivery_detailStmt.setNull(7, 8);
		}
		else {
			this.delivery_detailStmt.setDouble(7, price);
		}
		if ( discount_expr == null ) {
			this.delivery_detailStmt.setNull(8, 12);
		}
		else {
			this.delivery_detailStmt.setString(8, discount_expr);
		}
		if ( type == null ) {
			this.delivery_detailStmt.setNull(9, -6);
		}
		else {
			this.delivery_detailStmt.setShort(9, type);
		}
		if ( source == null ) {
			this.delivery_detailStmt.setNull(10, -6);
		}
		else {
			this.delivery_detailStmt.setShort(10, source);
		}
		if ( sales_detail == null ) {
			this.delivery_detailStmt.setNull(11, 4);
		}
		else {
			this.delivery_detailStmt.setInt(11, sales_detail);
		}

		this.delivery_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.delivery_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement amortization_typeStmt;
	
	private void initAmortization_typeStmt()
	throws SQLException {
		this.amortization_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization_type ( fixed_asset_account, accumulated_account, allocation_account, percentage, description) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAmortization_typeStmt()
	throws SQLException {
		amortization_typeStmt.close();
	}	
	/**
	 * Amortization_type
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la amortizacion
	 * @param percentage Porcentaje de amortizacion
	 * @param description Descripcion del Tipo de Amortizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAmortization_type(String fixed_asset_account, String accumulated_account, String allocation_account, Double percentage, String description)
	throws SQLException {
	
		if ( fixed_asset_account == null ) {
			this.amortization_typeStmt.setNull(1, 1);
		}
		else {
			this.amortization_typeStmt.setString(1, fixed_asset_account);
		}
		if ( accumulated_account == null ) {
			this.amortization_typeStmt.setNull(2, 1);
		}
		else {
			this.amortization_typeStmt.setString(2, accumulated_account);
		}
		if ( allocation_account == null ) {
			this.amortization_typeStmt.setNull(3, 1);
		}
		else {
			this.amortization_typeStmt.setString(3, allocation_account);
		}
		if ( percentage == null ) {
			this.amortization_typeStmt.setNull(4, 8);
		}
		else {
			this.amortization_typeStmt.setDouble(4, percentage);
		}
		if ( description == null ) {
			this.amortization_typeStmt.setNull(5, 12);
		}
		else {
			this.amortization_typeStmt.setString(5, description);
		}

		this.amortization_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.amortization_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement catalogueStmt;
	
	private void initCatalogueStmt()
	throws SQLException {
		this.catalogueStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue ( name, start_date, end_date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCatalogueStmt()
	throws SQLException {
		catalogueStmt.close();
	}	
	/**
	 * Catalogue
	 * @param name Nombre del Catalogo
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCatalogue(String name, Date start_date, Date end_date)
	throws SQLException {
	
		if ( name == null ) {
			this.catalogueStmt.setNull(1, 12);
		}
		else {
			this.catalogueStmt.setString(1, name);
		}
		if ( start_date == null ) {
			this.catalogueStmt.setNull(2, 91);
		}
		else {
			this.catalogueStmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			this.catalogueStmt.setNull(3, 91);
		}
		else {
			this.catalogueStmt.setDate(3, end_date);
		}

		this.catalogueStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.catalogueStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement paymentStmt;
	
	private void initPaymentStmt()
	throws SQLException {
		this.paymentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO payment ( type, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePaymentStmt()
	throws SQLException {
		paymentStmt.close();
	}	
	/**
	 * Payment
	 * @param type Tipo de PercepciÛn Salarial
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPayment(Short type, String description)
	throws SQLException {
	
		if ( type == null ) {
			this.paymentStmt.setNull(1, -6);
		}
		else {
			this.paymentStmt.setShort(1, type);
		}
		if ( description == null ) {
			this.paymentStmt.setNull(2, 12);
		}
		else {
			this.paymentStmt.setString(2, description);
		}

		this.paymentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.paymentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement action_entryStmt;
	
	private void initAction_entryStmt()
	throws SQLException {
		this.action_entryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_entry ( executionDate, action_id, session_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAction_entryStmt()
	throws SQLException {
		action_entryStmt.close();
	}	
	/**
	 * Action_entry
	 * @param executionDate Fecha de ejecucion
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_entry(Timestamp executionDate, Integer action_id, Integer session_id)
	throws SQLException {
	
		if ( executionDate == null ) {
			this.action_entryStmt.setNull(1, 93);
		}
		else {
			this.action_entryStmt.setTimestamp(1, executionDate);
		}
		if ( action_id == null ) {
			this.action_entryStmt.setNull(2, 4);
		}
		else {
			this.action_entryStmt.setInt(2, action_id);
		}
		if ( session_id == null ) {
			this.action_entryStmt.setNull(3, 4);
		}
		else {
			this.action_entryStmt.setInt(3, session_id);
		}

		this.action_entryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.action_entryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement absenceStmt;
	
	private void initAbsenceStmt()
	throws SQLException {
		this.absenceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO absence ( course_alumn, absence_date, comments, evaluation) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAbsenceStmt()
	throws SQLException {
		absenceStmt.close();
	}	
	/**
	 * Absence
	 * @param course_alumn Identificador del CursoAlumno
	 * @param absence_date Fecha de la Ausencia
	 * @param comments Comentarios de la Ausencia
	 * @param evaluation Numero de Evaluacion en que se produjo la Ausencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAbsence(Integer course_alumn, Date absence_date, InputStream comments, Short evaluation)
	throws SQLException {
	
		if ( course_alumn == null ) {
			this.absenceStmt.setNull(1, 4);
		}
		else {
			this.absenceStmt.setInt(1, course_alumn);
		}
		if ( absence_date == null ) {
			this.absenceStmt.setNull(2, 91);
		}
		else {
			this.absenceStmt.setDate(2, absence_date);
		}
		if ( comments == null ) {
			this.absenceStmt.setNull(3, -1);
		}
		else {
			this.absenceStmt.setAsciiStream(3, comments);
		}
		if ( evaluation == null ) {
			this.absenceStmt.setNull(4, -6);
		}
		else {
			this.absenceStmt.setShort(4, evaluation);
		}

		this.absenceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.absenceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement item_tariffStmt;
	
	private void initItem_tariffStmt()
	throws SQLException {
		this.item_tariffStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_tariff ( item, tariff, percentage) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItem_tariffStmt()
	throws SQLException {
		item_tariffStmt.close();
	}	
	/**
	 * Item_tariff
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @param percentage Porcentaje de descuento sobre el precio del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_tariff(Integer item, Integer tariff, Double percentage)
	throws SQLException {
	
		if ( item == null ) {
			this.item_tariffStmt.setNull(1, 4);
		}
		else {
			this.item_tariffStmt.setInt(1, item);
		}
		if ( tariff == null ) {
			this.item_tariffStmt.setNull(2, 4);
		}
		else {
			this.item_tariffStmt.setInt(2, tariff);
		}
		if ( percentage == null ) {
			this.item_tariffStmt.setNull(3, 8);
		}
		else {
			this.item_tariffStmt.setDouble(3, percentage);
		}

		this.item_tariffStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.item_tariffStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entry_fbatchStmt;
	
	private void initAccount_entry_fbatchStmt()
	throws SQLException {
		this.account_entry_fbatchStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_fbatch ( account_entry, fbatch) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entry_fbatchStmt()
	throws SQLException {
		account_entry_fbatchStmt.close();
	}	
	/**
	 * Account_entry_fbatch
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_fbatch(Integer account_entry, Integer fbatch)
	throws SQLException {
	
		if ( account_entry == null ) {
			this.account_entry_fbatchStmt.setNull(1, 4);
		}
		else {
			this.account_entry_fbatchStmt.setInt(1, account_entry);
		}
		if ( fbatch == null ) {
			this.account_entry_fbatchStmt.setNull(2, 4);
		}
		else {
			this.account_entry_fbatchStmt.setInt(2, fbatch);
		}

		this.account_entry_fbatchStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entry_fbatchStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement sellerStmt;
	
	private void initSellerStmt()
	throws SQLException {
		this.sellerStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO seller ( registry, commission_type, status) VALUES ( ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSellerStmt()
	throws SQLException {
		sellerStmt.close();
	}	
	/**
	 * Seller
	 * @param registry Registro del Agente Comercial
	 * @param commission_type Identificador del Tipo de Comision
	 * @param status Estado del Agente Comercial
	 * @throws SQLException
	*/
	protected void insertSeller(Integer registry, Integer commission_type, Short status)
	throws SQLException {
	
		if ( registry == null ) {
			this.sellerStmt.setNull(1, 4);
		}
		else {
			this.sellerStmt.setInt(1, registry);
		}
		if ( commission_type == null ) {
			this.sellerStmt.setNull(2, 4);
		}
		else {
			this.sellerStmt.setInt(2, commission_type);
		}
		if ( status == null ) {
			this.sellerStmt.setNull(3, -6);
		}
		else {
			this.sellerStmt.setShort(3, status);
		}

		this.sellerStmt.executeUpdate();
		
	}
	
	private PreparedStatement offer_attachStmt;
	
	private void initOffer_attachStmt()
	throws SQLException {
		this.offer_attachStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_attach ( offer, mimeType, description, data) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeOffer_attachStmt()
	throws SQLException {
		offer_attachStmt.close();
	}	
	/**
	 * Offer_attach
	 * @param offer Identificador del Presupuesto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_attach(Integer offer, Short mimeType, String description, InputStream data)
	throws SQLException {
	
		if ( offer == null ) {
			this.offer_attachStmt.setNull(1, 4);
		}
		else {
			this.offer_attachStmt.setInt(1, offer);
		}
		if ( mimeType == null ) {
			this.offer_attachStmt.setNull(2, -6);
		}
		else {
			this.offer_attachStmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			this.offer_attachStmt.setNull(3, 12);
		}
		else {
			this.offer_attachStmt.setString(3, description);
		}
		if ( data == null ) {
			this.offer_attachStmt.setNull(4, -4);
		}
		else {
			this.offer_attachStmt.setBinaryStream(4, data);
		}

		this.offer_attachStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.offer_attachStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement process_detailStmt;
	
	private void initProcess_detailStmt()
	throws SQLException {
		this.process_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_detail ( process, description, position, date_reference, days, alert_days, workgroup, priority, status, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProcess_detailStmt()
	throws SQLException {
		process_detailStmt.close();
	}	
	/**
	 * Process_detail
	 * @param process Identificador del Proceso
	 * @param description Descripcion del Detalle de Proceso
	 * @param position Orden de ejecucion del Detalle dentro del Proceso
	 * @param date_reference Referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param days Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param alert_days Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @param priority Prioridad de la Tarea
	 * @param status Estado de la Accion
	 * @param comments Comentarios del Detalle de Proceso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_detail(Integer process, String description, Integer position, Short date_reference, Integer days, Integer alert_days, Integer workgroup, Short priority, Short status, InputStream comments)
	throws SQLException {
	
		if ( process == null ) {
			this.process_detailStmt.setNull(1, 4);
		}
		else {
			this.process_detailStmt.setInt(1, process);
		}
		if ( description == null ) {
			this.process_detailStmt.setNull(2, 12);
		}
		else {
			this.process_detailStmt.setString(2, description);
		}
		if ( position == null ) {
			this.process_detailStmt.setNull(3, 4);
		}
		else {
			this.process_detailStmt.setInt(3, position);
		}
		if ( date_reference == null ) {
			this.process_detailStmt.setNull(4, -6);
		}
		else {
			this.process_detailStmt.setShort(4, date_reference);
		}
		if ( days == null ) {
			this.process_detailStmt.setNull(5, 4);
		}
		else {
			this.process_detailStmt.setInt(5, days);
		}
		if ( alert_days == null ) {
			this.process_detailStmt.setNull(6, 4);
		}
		else {
			this.process_detailStmt.setInt(6, alert_days);
		}
		if ( workgroup == null ) {
			this.process_detailStmt.setNull(7, 4);
		}
		else {
			this.process_detailStmt.setInt(7, workgroup);
		}
		if ( priority == null ) {
			this.process_detailStmt.setNull(8, -6);
		}
		else {
			this.process_detailStmt.setShort(8, priority);
		}
		if ( status == null ) {
			this.process_detailStmt.setNull(9, -6);
		}
		else {
			this.process_detailStmt.setShort(9, status);
		}
		if ( comments == null ) {
			this.process_detailStmt.setNull(10, -1);
		}
		else {
			this.process_detailStmt.setAsciiStream(10, comments);
		}

		this.process_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.process_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement relationshipStmt;
	
	private void initRelationshipStmt()
	throws SQLException {
		this.relationshipStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO relationship ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRelationshipStmt()
	throws SQLException {
		relationshipStmt.close();
	}	
	/**
	 * Relationship
	 * @param description Descripcion del Tipo de Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRelationship(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.relationshipStmt.setNull(1, 12);
		}
		else {
			this.relationshipStmt.setString(1, description);
		}

		this.relationshipStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.relationshipStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_budget_detailStmt;
	
	private void initAccount_budget_detailStmt()
	throws SQLException {
		this.account_budget_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_budget_detail ( account_budget, account_period, account, security_level, entry_date, debit, credit) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_budget_detailStmt()
	throws SQLException {
		account_budget_detailStmt.close();
	}	
	/**
	 * Account_budget_detail
	 * @param account_budget Identificador del Presupuesto
	 * @param account_period Ejercicio Contable del Presupuesto
	 * @param account Cuenta Contable del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @param entry_date Fecha del Presupuesto
	 * @param debit Debe del Presupuesto
	 * @param credit Haber del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_budget_detail(Integer account_budget, String account_period, String account, Short security_level, Date entry_date, Double debit, Double credit)
	throws SQLException {
	
		if ( account_budget == null ) {
			this.account_budget_detailStmt.setNull(1, 4);
		}
		else {
			this.account_budget_detailStmt.setInt(1, account_budget);
		}
		if ( account_period == null ) {
			this.account_budget_detailStmt.setNull(2, 1);
		}
		else {
			this.account_budget_detailStmt.setString(2, account_period);
		}
		if ( account == null ) {
			this.account_budget_detailStmt.setNull(3, 1);
		}
		else {
			this.account_budget_detailStmt.setString(3, account);
		}
		if ( security_level == null ) {
			this.account_budget_detailStmt.setNull(4, -6);
		}
		else {
			this.account_budget_detailStmt.setShort(4, security_level);
		}
		if ( entry_date == null ) {
			this.account_budget_detailStmt.setNull(5, 91);
		}
		else {
			this.account_budget_detailStmt.setDate(5, entry_date);
		}
		if ( debit == null ) {
			this.account_budget_detailStmt.setNull(6, 8);
		}
		else {
			this.account_budget_detailStmt.setDouble(6, debit);
		}
		if ( credit == null ) {
			this.account_budget_detailStmt.setNull(7, 8);
		}
		else {
			this.account_budget_detailStmt.setDouble(7, credit);
		}

		this.account_budget_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_budget_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entry_invoiceStmt;
	
	private void initAccount_entry_invoiceStmt()
	throws SQLException {
		this.account_entry_invoiceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_invoice ( account_entry, invoice) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entry_invoiceStmt()
	throws SQLException {
		account_entry_invoiceStmt.close();
	}	
	/**
	 * Account_entry_invoice
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_invoice(Integer account_entry, Integer invoice)
	throws SQLException {
	
		if ( account_entry == null ) {
			this.account_entry_invoiceStmt.setNull(1, 4);
		}
		else {
			this.account_entry_invoiceStmt.setInt(1, account_entry);
		}
		if ( invoice == null ) {
			this.account_entry_invoiceStmt.setNull(2, 4);
		}
		else {
			this.account_entry_invoiceStmt.setInt(2, invoice);
		}

		this.account_entry_invoiceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entry_invoiceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tax_accountStmt;
	
	private void initTax_accountStmt()
	throws SQLException {
		this.tax_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax_account ( tax, account, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTax_accountStmt()
	throws SQLException {
		tax_accountStmt.close();
	}	
	/**
	 * Tax_account
	 * @param tax Identificador del Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Impuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax_account(Integer tax, String account, Short type)
	throws SQLException {
	
		if ( tax == null ) {
			this.tax_accountStmt.setNull(1, 4);
		}
		else {
			this.tax_accountStmt.setInt(1, tax);
		}
		if ( account == null ) {
			this.tax_accountStmt.setNull(2, 1);
		}
		else {
			this.tax_accountStmt.setString(2, account);
		}
		if ( type == null ) {
			this.tax_accountStmt.setNull(3, -6);
		}
		else {
			this.tax_accountStmt.setShort(3, type);
		}

		this.tax_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tax_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement expenseStmt;
	
	private void initExpenseStmt()
	throws SQLException {
		this.expenseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense ( description, unit_price) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeExpenseStmt()
	throws SQLException {
		expenseStmt.close();
	}	
	/**
	 * Expense
	 * @param description Descripcion del Gasto
	 * @param unit_price Precio unitario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpense(String description, Double unit_price)
	throws SQLException {
	
		if ( description == null ) {
			this.expenseStmt.setNull(1, 12);
		}
		else {
			this.expenseStmt.setString(1, description);
		}
		if ( unit_price == null ) {
			this.expenseStmt.setNull(2, 8);
		}
		else {
			this.expenseStmt.setDouble(2, unit_price);
		}

		this.expenseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.expenseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement inventoryStmt;
	
	private void initInventoryStmt()
	throws SQLException {
		this.inventoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO inventory ( inventory_date, warehouse, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInventoryStmt()
	throws SQLException {
		inventoryStmt.close();
	}	
	/**
	 * Inventory
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInventory(Date inventory_date, Integer warehouse, String description)
	throws SQLException {
	
		if ( inventory_date == null ) {
			this.inventoryStmt.setNull(1, 91);
		}
		else {
			this.inventoryStmt.setDate(1, inventory_date);
		}
		if ( warehouse == null ) {
			this.inventoryStmt.setNull(2, 4);
		}
		else {
			this.inventoryStmt.setInt(2, warehouse);
		}
		if ( description == null ) {
			this.inventoryStmt.setNull(3, 12);
		}
		else {
			this.inventoryStmt.setString(3, description);
		}

		this.inventoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.inventoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commercial_termStmt;
	
	private void initCommercial_termStmt()
	throws SQLException {
		this.commercial_termStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_term ( line, name, description, term_general) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommercial_termStmt()
	throws SQLException {
		commercial_termStmt.close();
	}	
	/**
	 * Commercial_term
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condiciùn es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_term(Integer line, String name, InputStream description, Boolean term_general)
	throws SQLException {
	
		if ( line == null ) {
			this.commercial_termStmt.setNull(1, 5);
		}
		else {
			this.commercial_termStmt.setInt(1, line);
		}
		if ( name == null ) {
			this.commercial_termStmt.setNull(2, 12);
		}
		else {
			this.commercial_termStmt.setString(2, name);
		}
		if ( description == null ) {
			this.commercial_termStmt.setNull(3, -1);
		}
		else {
			this.commercial_termStmt.setAsciiStream(3, description);
		}
		if ( term_general == null ) {
			this.commercial_termStmt.setNull(4, -7);
		}
		else {
			this.commercial_termStmt.setBoolean(4, term_general);
		}

		this.commercial_termStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commercial_termStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement production_expenseStmt;
	
	private void initProduction_expenseStmt()
	throws SQLException {
		this.production_expenseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production_expense ( production, description, quantity, price) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProduction_expenseStmt()
	throws SQLException {
		production_expenseStmt.close();
	}	
	/**
	 * Production_expense
	 * @param production Identificador de la Produccion
	 * @param description Descripcion del Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Precio del Gasto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduction_expense(Integer production, String description, Double quantity, Double price)
	throws SQLException {
	
		if ( production == null ) {
			this.production_expenseStmt.setNull(1, 4);
		}
		else {
			this.production_expenseStmt.setInt(1, production);
		}
		if ( description == null ) {
			this.production_expenseStmt.setNull(2, 12);
		}
		else {
			this.production_expenseStmt.setString(2, description);
		}
		if ( quantity == null ) {
			this.production_expenseStmt.setNull(3, 8);
		}
		else {
			this.production_expenseStmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			this.production_expenseStmt.setNull(4, 8);
		}
		else {
			this.production_expenseStmt.setDouble(4, price);
		}

		this.production_expenseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.production_expenseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commercial_activityStmt;
	
	private void initCommercial_activityStmt()
	throws SQLException {
		this.commercial_activityStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_activity ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommercial_activityStmt()
	throws SQLException {
		commercial_activityStmt.close();
	}	
	/**
	 * Commercial_activity
	 * @param name Nombre de la Actividad Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_activity(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.commercial_activityStmt.setNull(1, 12);
		}
		else {
			this.commercial_activityStmt.setString(1, name);
		}

		this.commercial_activityStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commercial_activityStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement offer_termStmt;
	
	private void initOffer_termStmt()
	throws SQLException {
		this.offer_termStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_term ( offer, line, name, description, term_general) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeOffer_termStmt()
	throws SQLException {
		offer_termStmt.close();
	}	
	/**
	 * Offer_term
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea de la Condicion del Presupuesto
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condicion es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_term(Integer offer, Integer line, String name, InputStream description, Boolean term_general)
	throws SQLException {
	
		if ( offer == null ) {
			this.offer_termStmt.setNull(1, 4);
		}
		else {
			this.offer_termStmt.setInt(1, offer);
		}
		if ( line == null ) {
			this.offer_termStmt.setNull(2, 5);
		}
		else {
			this.offer_termStmt.setInt(2, line);
		}
		if ( name == null ) {
			this.offer_termStmt.setNull(3, 12);
		}
		else {
			this.offer_termStmt.setString(3, name);
		}
		if ( description == null ) {
			this.offer_termStmt.setNull(4, -1);
		}
		else {
			this.offer_termStmt.setAsciiStream(4, description);
		}
		if ( term_general == null ) {
			this.offer_termStmt.setNull(5, -7);
		}
		else {
			this.offer_termStmt.setBoolean(5, term_general);
		}

		this.offer_termStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.offer_termStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rsegmentStmt;
	
	private void initRsegmentStmt()
	throws SQLException {
		this.rsegmentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rsegment ( registry, segment) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRsegmentStmt()
	throws SQLException {
		rsegmentStmt.close();
	}	
	/**
	 * Rsegment
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRsegment(Integer registry, Integer segment)
	throws SQLException {
	
		if ( registry == null ) {
			this.rsegmentStmt.setNull(1, 4);
		}
		else {
			this.rsegmentStmt.setInt(1, registry);
		}
		if ( segment == null ) {
			this.rsegmentStmt.setNull(2, 4);
		}
		else {
			this.rsegmentStmt.setInt(2, segment);
		}

		this.rsegmentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rsegmentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement delivery_detail_labourStmt;
	
	private void initDelivery_detail_labourStmt()
	throws SQLException {
		this.delivery_detail_labourStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery_detail_labour ( delivery_detail, employee, quantity) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDelivery_detail_labourStmt()
	throws SQLException {
		delivery_detail_labourStmt.close();
	}	
	/**
	 * Delivery_detail_labour
	 * @param delivery_detail Identificador de la Linea de Albaran
	 * @param employee Identificador del Empleado
	 * @param quantity Numero de horas de mano de obra
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery_detail_labour(Integer delivery_detail, Integer employee, Double quantity)
	throws SQLException {
	
		if ( delivery_detail == null ) {
			this.delivery_detail_labourStmt.setNull(1, 4);
		}
		else {
			this.delivery_detail_labourStmt.setInt(1, delivery_detail);
		}
		if ( employee == null ) {
			this.delivery_detail_labourStmt.setNull(2, 4);
		}
		else {
			this.delivery_detail_labourStmt.setInt(2, employee);
		}
		if ( quantity == null ) {
			this.delivery_detail_labourStmt.setNull(3, 8);
		}
		else {
			this.delivery_detail_labourStmt.setDouble(3, quantity);
		}

		this.delivery_detail_labourStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.delivery_detail_labourStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement process_transition_typeStmt;
	
	private void initProcess_transition_typeStmt()
	throws SQLException {
		this.process_transition_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_transition_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProcess_transition_typeStmt()
	throws SQLException {
		process_transition_typeStmt.close();
	}	
	/**
	 * Process_transition_type
	 * @param description Descripcion del Tipo de Transicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_transition_type(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.process_transition_typeStmt.setNull(1, 12);
		}
		else {
			this.process_transition_typeStmt.setString(1, description);
		}

		this.process_transition_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.process_transition_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement leasing_accountStmt;
	
	private void initLeasing_accountStmt()
	throws SQLException {
		this.leasing_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO leasing_account ( leasing, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLeasing_accountStmt()
	throws SQLException {
		leasing_accountStmt.close();
	}	
	/**
	 * Leasing_account
	 * @param leasing Leasing
	 * @param account Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLeasing_account(Integer leasing, String account)
	throws SQLException {
	
		if ( leasing == null ) {
			this.leasing_accountStmt.setNull(1, 4);
		}
		else {
			this.leasing_accountStmt.setInt(1, leasing);
		}
		if ( account == null ) {
			this.leasing_accountStmt.setNull(2, 12);
		}
		else {
			this.leasing_accountStmt.setString(2, account);
		}

		this.leasing_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.leasing_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_knowledgeStmt;
	
	private void initCv_knowledgeStmt()
	throws SQLException {
		this.cv_knowledgeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_knowledge ( name, level, experience, lastuse, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_knowledgeStmt()
	throws SQLException {
		cv_knowledgeStmt.close();
	}	
	/**
	 * Cv_knowledge
	 * @param name Nombre o descripcion del Conocimiento
	 * @param level Nivel del Conocimiento
	 * @param experience Experiencia en el Conocimiento
	 * @param lastuse Ultimo uso del Conocimiento
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_knowledge(String name, Short level, Short experience, Short lastuse, Integer curriculum)
	throws SQLException {
	
		if ( name == null ) {
			this.cv_knowledgeStmt.setNull(1, 12);
		}
		else {
			this.cv_knowledgeStmt.setString(1, name);
		}
		if ( level == null ) {
			this.cv_knowledgeStmt.setNull(2, -6);
		}
		else {
			this.cv_knowledgeStmt.setShort(2, level);
		}
		if ( experience == null ) {
			this.cv_knowledgeStmt.setNull(3, -6);
		}
		else {
			this.cv_knowledgeStmt.setShort(3, experience);
		}
		if ( lastuse == null ) {
			this.cv_knowledgeStmt.setNull(4, -6);
		}
		else {
			this.cv_knowledgeStmt.setShort(4, lastuse);
		}
		if ( curriculum == null ) {
			this.cv_knowledgeStmt.setNull(5, 4);
		}
		else {
			this.cv_knowledgeStmt.setInt(5, curriculum);
		}

		this.cv_knowledgeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_knowledgeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contract_batchStmt;
	
	private void initContract_batchStmt()
	throws SQLException {
		this.contract_batchStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_batch ( date, red_notify_date, red_notify_id, red_response_date, red_response_id) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_batchStmt()
	throws SQLException {
		contract_batchStmt.close();
	}	
	/**
	 * Contract_batch
	 * @param date Fecha de la ultima remesa en la que fue incluido
	 * @param red_notify_date Fecha de notificacion al sistema red
	 * @param red_notify_id Identificador de la notificacion
	 * @param red_response_date Fecha de respuesta del sistema red
	 * @param red_response_id Identificador de la respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_batch(Date date, Date red_notify_date, Date red_notify_id, Date red_response_date, Date red_response_id)
	throws SQLException {
	
		if ( date == null ) {
			this.contract_batchStmt.setNull(1, 91);
		}
		else {
			this.contract_batchStmt.setDate(1, date);
		}
		if ( red_notify_date == null ) {
			this.contract_batchStmt.setNull(2, 91);
		}
		else {
			this.contract_batchStmt.setDate(2, red_notify_date);
		}
		if ( red_notify_id == null ) {
			this.contract_batchStmt.setNull(3, 91);
		}
		else {
			this.contract_batchStmt.setDate(3, red_notify_id);
		}
		if ( red_response_date == null ) {
			this.contract_batchStmt.setNull(4, 91);
		}
		else {
			this.contract_batchStmt.setDate(4, red_response_date);
		}
		if ( red_response_id == null ) {
			this.contract_batchStmt.setNull(5, 91);
		}
		else {
			this.contract_batchStmt.setDate(5, red_response_id);
		}

		this.contract_batchStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_batchStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement quality_skillStmt;
	
	private void initQuality_skillStmt()
	throws SQLException {
		this.quality_skillStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO quality_skill ( code, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeQuality_skillStmt()
	throws SQLException {
		quality_skillStmt.close();
	}	
	/**
	 * Quality_skill
	 * @param code Codigo de la Aptitud Calidad
	 * @param description Descripcion de la Aptitud Calidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertQuality_skill(String code, String description)
	throws SQLException {
	
		if ( code == null ) {
			this.quality_skillStmt.setNull(1, 1);
		}
		else {
			this.quality_skillStmt.setString(1, code);
		}
		if ( description == null ) {
			this.quality_skillStmt.setNull(2, 12);
		}
		else {
			this.quality_skillStmt.setString(2, description);
		}

		this.quality_skillStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.quality_skillStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement ec_targetStmt;
	
	private void initEc_targetStmt()
	throws SQLException {
		this.ec_targetStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_target ( target, login, password, type, last_access) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEc_targetStmt()
	throws SQLException {
		ec_targetStmt.close();
	}	
	/**
	 * Ec_target
	 * @param target Identificador del Cliente Potencial
	 * @param login Login del Cliente Potencial
	 * @param password Password del Cliente Potencial
	 * @param type Tipo de conexion
	 * @param last_access Ultima fecha de conexion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_target(Integer target, String login, String password, Short type, Date last_access)
	throws SQLException {
	
		if ( target == null ) {
			this.ec_targetStmt.setNull(1, 4);
		}
		else {
			this.ec_targetStmt.setInt(1, target);
		}
		if ( login == null ) {
			this.ec_targetStmt.setNull(2, 12);
		}
		else {
			this.ec_targetStmt.setString(2, login);
		}
		if ( password == null ) {
			this.ec_targetStmt.setNull(3, 12);
		}
		else {
			this.ec_targetStmt.setString(3, password);
		}
		if ( type == null ) {
			this.ec_targetStmt.setNull(4, -6);
		}
		else {
			this.ec_targetStmt.setShort(4, type);
		}
		if ( last_access == null ) {
			this.ec_targetStmt.setNull(5, 91);
		}
		else {
			this.ec_targetStmt.setDate(5, last_access);
		}

		this.ec_targetStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.ec_targetStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement balanceStmt;
	
	private void initBalanceStmt()
	throws SQLException {
		this.balanceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO balance ( name, removable, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeBalanceStmt()
	throws SQLException {
		balanceStmt.close();
	}	
	/**
	 * Balance
	 * @param name Nombre del Balance
	 * @param removable Indica se puede ser borrado por el usuario
	 * @param type Tipo de Balance
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBalance(String name, Boolean removable, Short type)
	throws SQLException {
	
		if ( name == null ) {
			this.balanceStmt.setNull(1, 12);
		}
		else {
			this.balanceStmt.setString(1, name);
		}
		if ( removable == null ) {
			this.balanceStmt.setNull(2, -7);
		}
		else {
			this.balanceStmt.setBoolean(2, removable);
		}
		if ( type == null ) {
			this.balanceStmt.setNull(3, -6);
		}
		else {
			this.balanceStmt.setShort(3, type);
		}

		this.balanceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.balanceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement qualificationStmt;
	
	private void initQualificationStmt()
	throws SQLException {
		this.qualificationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO qualification ( code, description, min_value, max_value) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeQualificationStmt()
	throws SQLException {
		qualificationStmt.close();
	}	
	/**
	 * Qualification
	 * @param code Codigo de la Calificacion
	 * @param description Descripcion de la Calificacion
	 * @param min_value Limite inferior de la Calificacion
	 * @param max_value Limite superior de la Calificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertQualification(String code, String description, Double min_value, Double max_value)
	throws SQLException {
	
		if ( code == null ) {
			this.qualificationStmt.setNull(1, 1);
		}
		else {
			this.qualificationStmt.setString(1, code);
		}
		if ( description == null ) {
			this.qualificationStmt.setNull(2, 12);
		}
		else {
			this.qualificationStmt.setString(2, description);
		}
		if ( min_value == null ) {
			this.qualificationStmt.setNull(3, 8);
		}
		else {
			this.qualificationStmt.setDouble(3, min_value);
		}
		if ( max_value == null ) {
			this.qualificationStmt.setNull(4, 8);
		}
		else {
			this.qualificationStmt.setDouble(4, max_value);
		}

		this.qualificationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.qualificationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tax_detailStmt;
	
	private void initTax_detailStmt()
	throws SQLException {
		this.tax_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax_detail ( tax, start_date, end_date, value, surcharge) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTax_detailStmt()
	throws SQLException {
		tax_detailStmt.close();
	}	
	/**
	 * Tax_detail
	 * @param tax Identificador del Impuesto
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @param value Porcentaje de recargo
	 * @param surcharge Porcentaje de recargo de equivalencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax_detail(Integer tax, Date start_date, Date end_date, Double value, Double surcharge)
	throws SQLException {
	
		if ( tax == null ) {
			this.tax_detailStmt.setNull(1, 4);
		}
		else {
			this.tax_detailStmt.setInt(1, tax);
		}
		if ( start_date == null ) {
			this.tax_detailStmt.setNull(2, 91);
		}
		else {
			this.tax_detailStmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			this.tax_detailStmt.setNull(3, 91);
		}
		else {
			this.tax_detailStmt.setDate(3, end_date);
		}
		if ( value == null ) {
			this.tax_detailStmt.setNull(4, 8);
		}
		else {
			this.tax_detailStmt.setDouble(4, value);
		}
		if ( surcharge == null ) {
			this.tax_detailStmt.setNull(5, 8);
		}
		else {
			this.tax_detailStmt.setDouble(5, surcharge);
		}

		this.tax_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tax_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement supplier_accountStmt;
	
	private void initSupplier_accountStmt()
	throws SQLException {
		this.supplier_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier_account ( supplier, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSupplier_accountStmt()
	throws SQLException {
		supplier_accountStmt.close();
	}	
	/**
	 * Supplier_account
	 * @param supplier Identificador del Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier_account(Integer supplier, String account)
	throws SQLException {
	
		if ( supplier == null ) {
			this.supplier_accountStmt.setNull(1, 4);
		}
		else {
			this.supplier_accountStmt.setInt(1, supplier);
		}
		if ( account == null ) {
			this.supplier_accountStmt.setNull(2, 1);
		}
		else {
			this.supplier_accountStmt.setString(2, account);
		}

		this.supplier_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.supplier_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement compositionStmt;
	
	private void initCompositionStmt()
	throws SQLException {
		this.compositionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition ( type, description, item, quantity, price, expenses_percent, expenses_fixed, price_in_details) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCompositionStmt()
	throws SQLException {
		compositionStmt.close();
	}	
	/**
	 * Composition
	 * @param type Tipo de Composicion
	 * @param description Descripcion de la Composicion
	 * @param item Identificador del Articulo a componer
	 * @param quantity Cantidad de Articulo a componer
	 * @param price Precio de la Composicion
	 * @param expenses_percent Gastos porcentuales de la Composicion
	 * @param expenses_fixed Gastos fijos de la Composicion
	 * @param price_in_details Indica si el precio lo forman la suma de los detalles de la Composicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertComposition(Short type, String description, Integer item, Double quantity, Double price, Double expenses_percent, Double expenses_fixed, Boolean price_in_details)
	throws SQLException {
	
		if ( type == null ) {
			this.compositionStmt.setNull(1, -6);
		}
		else {
			this.compositionStmt.setShort(1, type);
		}
		if ( description == null ) {
			this.compositionStmt.setNull(2, 12);
		}
		else {
			this.compositionStmt.setString(2, description);
		}
		if ( item == null ) {
			this.compositionStmt.setNull(3, 4);
		}
		else {
			this.compositionStmt.setInt(3, item);
		}
		if ( quantity == null ) {
			this.compositionStmt.setNull(4, 8);
		}
		else {
			this.compositionStmt.setDouble(4, quantity);
		}
		if ( price == null ) {
			this.compositionStmt.setNull(5, 8);
		}
		else {
			this.compositionStmt.setDouble(5, price);
		}
		if ( expenses_percent == null ) {
			this.compositionStmt.setNull(6, 8);
		}
		else {
			this.compositionStmt.setDouble(6, expenses_percent);
		}
		if ( expenses_fixed == null ) {
			this.compositionStmt.setNull(7, 8);
		}
		else {
			this.compositionStmt.setDouble(7, expenses_fixed);
		}
		if ( price_in_details == null ) {
			this.compositionStmt.setNull(8, -7);
		}
		else {
			this.compositionStmt.setBoolean(8, price_in_details);
		}

		this.compositionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.compositionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement bankStmt;
	
	private void initBankStmt()
	throws SQLException {
		this.bankStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO bank ( name, code) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeBankStmt()
	throws SQLException {
		bankStmt.close();
	}	
	/**
	 * Bank
	 * @param name Nombre de la Entidad Bancaria
	 * @param code Codigo de la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBank(String name, String code)
	throws SQLException {
	
		if ( name == null ) {
			this.bankStmt.setNull(1, 12);
		}
		else {
			this.bankStmt.setString(1, name);
		}
		if ( code == null ) {
			this.bankStmt.setNull(2, 12);
		}
		else {
			this.bankStmt.setString(2, code);
		}

		this.bankStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.bankStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement activity_typeStmt;
	
	private void initActivity_typeStmt()
	throws SQLException {
		this.activity_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity_type ( description, dossier_type) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeActivity_typeStmt()
	throws SQLException {
		activity_typeStmt.close();
	}	
	/**
	 * Activity_type
	 * @param description Descripcion del Tipo de Actividad
	 * @param dossier_type Tipo de Dossier
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity_type(String description, Integer dossier_type)
	throws SQLException {
	
		if ( description == null ) {
			this.activity_typeStmt.setNull(1, 12);
		}
		else {
			this.activity_typeStmt.setString(1, description);
		}
		if ( dossier_type == null ) {
			this.activity_typeStmt.setNull(2, 4);
		}
		else {
			this.activity_typeStmt.setInt(2, dossier_type);
		}

		this.activity_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.activity_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_evaluationStmt;
	
	private void initCourse_evaluationStmt()
	throws SQLException {
		this.course_evaluationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_evaluation ( course, quality_skill, evaluation, quantity) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_evaluationStmt()
	throws SQLException {
		course_evaluationStmt.close();
	}	
	/**
	 * Course_evaluation
	 * @param course Identificador de Curso
	 * @param quality_skill Identificador de Aptitudes Calidad
	 * @param evaluation Evaluaciones
	 * @param quantity Cantidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_evaluation(Integer course, Integer quality_skill, Double evaluation, Integer quantity)
	throws SQLException {
	
		if ( course == null ) {
			this.course_evaluationStmt.setNull(1, 4);
		}
		else {
			this.course_evaluationStmt.setInt(1, course);
		}
		if ( quality_skill == null ) {
			this.course_evaluationStmt.setNull(2, 4);
		}
		else {
			this.course_evaluationStmt.setInt(2, quality_skill);
		}
		if ( evaluation == null ) {
			this.course_evaluationStmt.setNull(3, 8);
		}
		else {
			this.course_evaluationStmt.setDouble(3, evaluation);
		}
		if ( quantity == null ) {
			this.course_evaluationStmt.setNull(4, 4);
		}
		else {
			this.course_evaluationStmt.setInt(4, quantity);
		}

		this.course_evaluationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_evaluationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entry_linkStmt;
	
	private void initAccount_entry_linkStmt()
	throws SQLException {
		this.account_entry_linkStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_link ( account_entry_from, account_entry_to) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entry_linkStmt()
	throws SQLException {
		account_entry_linkStmt.close();
	}	
	/**
	 * Account_entry_link
	 * @param account_entry_from Asiento original
	 * @param account_entry_to Asiento vinculado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_link(Integer account_entry_from, Integer account_entry_to)
	throws SQLException {
	
		if ( account_entry_from == null ) {
			this.account_entry_linkStmt.setNull(1, 4);
		}
		else {
			this.account_entry_linkStmt.setInt(1, account_entry_from);
		}
		if ( account_entry_to == null ) {
			this.account_entry_linkStmt.setNull(2, 4);
		}
		else {
			this.account_entry_linkStmt.setInt(2, account_entry_to);
		}

		this.account_entry_linkStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entry_linkStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement support_orderStmt;
	
	private void initSupport_orderStmt()
	throws SQLException {
		this.support_orderStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO support_order ( tas_item, target, series, number, description, final_date, status, start_date, employee, counterti, levelti, operation, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSupport_orderStmt()
	throws SQLException {
		support_orderStmt.close();
	}	
	/**
	 * Support_order
	 * @param tas_item Identificador del Articulo de la Orden de Reparacion
	 * @param target Identificador del Cliente Potencial
	 * @param series Serie de la Orden de Reparacion
	 * @param number Numero de la Orden de Reparacion
	 * @param description Descripcion de la Orden de Reparacion
	 * @param final_date Fecha de finalizacion de la Orden de Reparacion
	 * @param status Estado de la Orden de Reparacion
	 * @param start_date Fecha de inicio de la Orden de Reparacion
	 * @param employee Identificador del Empleado de la Orden de Reparacion
	 * @param counterti Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)
	 * @param levelti Nivel del Articulo de la Orden de Reparacion (p.e. Gasolina)
	 * @param operation Operacion a realizar con la Orden de Reparacion
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupport_order(Integer tas_item, Integer target, String series, Integer number, InputStream description, Date final_date, Short status, Date start_date, Integer employee, Double counterti, String levelti, Short operation, Integer workplace)
	throws SQLException {
	
		if ( tas_item == null ) {
			this.support_orderStmt.setNull(1, 4);
		}
		else {
			this.support_orderStmt.setInt(1, tas_item);
		}
		if ( target == null ) {
			this.support_orderStmt.setNull(2, 4);
		}
		else {
			this.support_orderStmt.setInt(2, target);
		}
		if ( series == null ) {
			this.support_orderStmt.setNull(3, 1);
		}
		else {
			this.support_orderStmt.setString(3, series);
		}
		if ( number == null ) {
			this.support_orderStmt.setNull(4, 4);
		}
		else {
			this.support_orderStmt.setInt(4, number);
		}
		if ( description == null ) {
			this.support_orderStmt.setNull(5, -1);
		}
		else {
			this.support_orderStmt.setAsciiStream(5, description);
		}
		if ( final_date == null ) {
			this.support_orderStmt.setNull(6, 91);
		}
		else {
			this.support_orderStmt.setDate(6, final_date);
		}
		if ( status == null ) {
			this.support_orderStmt.setNull(7, -6);
		}
		else {
			this.support_orderStmt.setShort(7, status);
		}
		if ( start_date == null ) {
			this.support_orderStmt.setNull(8, 91);
		}
		else {
			this.support_orderStmt.setDate(8, start_date);
		}
		if ( employee == null ) {
			this.support_orderStmt.setNull(9, 4);
		}
		else {
			this.support_orderStmt.setInt(9, employee);
		}
		if ( counterti == null ) {
			this.support_orderStmt.setNull(10, 8);
		}
		else {
			this.support_orderStmt.setDouble(10, counterti);
		}
		if ( levelti == null ) {
			this.support_orderStmt.setNull(11, 12);
		}
		else {
			this.support_orderStmt.setString(11, levelti);
		}
		if ( operation == null ) {
			this.support_orderStmt.setNull(12, -6);
		}
		else {
			this.support_orderStmt.setShort(12, operation);
		}
		if ( workplace == null ) {
			this.support_orderStmt.setNull(13, 4);
		}
		else {
			this.support_orderStmt.setInt(13, workplace);
		}

		this.support_orderStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.support_orderStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement process_detail_transitionStmt;
	
	private void initProcess_detail_transitionStmt()
	throws SQLException {
		this.process_detail_transitionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_detail_transition ( process_detail, process_transition_type, next_process_detail) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProcess_detail_transitionStmt()
	throws SQLException {
		process_detail_transitionStmt.close();
	}	
	/**
	 * Process_detail_transition
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_detail_transition(Integer process_detail, Integer process_transition_type, Integer next_process_detail)
	throws SQLException {
	
		if ( process_detail == null ) {
			this.process_detail_transitionStmt.setNull(1, 4);
		}
		else {
			this.process_detail_transitionStmt.setInt(1, process_detail);
		}
		if ( process_transition_type == null ) {
			this.process_detail_transitionStmt.setNull(2, 4);
		}
		else {
			this.process_detail_transitionStmt.setInt(2, process_transition_type);
		}
		if ( next_process_detail == null ) {
			this.process_detail_transitionStmt.setNull(3, 4);
		}
		else {
			this.process_detail_transitionStmt.setInt(3, next_process_detail);
		}

		this.process_detail_transitionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.process_detail_transitionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement customer_feeStmt;
	
	private void initCustomer_feeStmt()
	throws SQLException {
		this.customer_feeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_fee ( customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCustomer_feeStmt()
	throws SQLException {
		customer_feeStmt.close();
	}	
	/**
	 * Customer_fee
	 * @param customer Identificador del Cliente
	 * @param line Numero de linea de Cuota
	 * @param item Identificador del Articulo
	 * @param description Descripcion de la Cuota
	 * @param quantity Cantidad de la Cuota
	 * @param price Precio de la Cuota
	 * @param discount_expr Descuentos de la Cuota
	 * @param initial_date Fecha de inicio de la Cuota
	 * @param final_date Fecha de finalizacion de la Cuota
	 * @param billing_date Proxima fecha de facturaciùn de la Cuota
	 * @param period Periodo de facturacion en meses de la Cuota
	 * @param security_level Nivel de seguridad de la Cuota
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_fee(Integer customer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Date initial_date, Date final_date, Date billing_date, Integer period, Short security_level, Integer workplace)
	throws SQLException {
	
		if ( customer == null ) {
			this.customer_feeStmt.setNull(1, 4);
		}
		else {
			this.customer_feeStmt.setInt(1, customer);
		}
		if ( line == null ) {
			this.customer_feeStmt.setNull(2, 5);
		}
		else {
			this.customer_feeStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.customer_feeStmt.setNull(3, 4);
		}
		else {
			this.customer_feeStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.customer_feeStmt.setNull(4, 12);
		}
		else {
			this.customer_feeStmt.setString(4, description);
		}
		if ( quantity == null ) {
			this.customer_feeStmt.setNull(5, 8);
		}
		else {
			this.customer_feeStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.customer_feeStmt.setNull(6, 8);
		}
		else {
			this.customer_feeStmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			this.customer_feeStmt.setNull(7, 12);
		}
		else {
			this.customer_feeStmt.setString(7, discount_expr);
		}
		if ( initial_date == null ) {
			this.customer_feeStmt.setNull(8, 91);
		}
		else {
			this.customer_feeStmt.setDate(8, initial_date);
		}
		if ( final_date == null ) {
			this.customer_feeStmt.setNull(9, 91);
		}
		else {
			this.customer_feeStmt.setDate(9, final_date);
		}
		if ( billing_date == null ) {
			this.customer_feeStmt.setNull(10, 91);
		}
		else {
			this.customer_feeStmt.setDate(10, billing_date);
		}
		if ( period == null ) {
			this.customer_feeStmt.setNull(11, 5);
		}
		else {
			this.customer_feeStmt.setInt(11, period);
		}
		if ( security_level == null ) {
			this.customer_feeStmt.setNull(12, -6);
		}
		else {
			this.customer_feeStmt.setShort(12, security_level);
		}
		if ( workplace == null ) {
			this.customer_feeStmt.setNull(13, 4);
		}
		else {
			this.customer_feeStmt.setInt(13, workplace);
		}

		this.customer_feeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.customer_feeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement purchase_detailStmt;
	
	private void initPurchase_detailStmt()
	throws SQLException {
		this.purchase_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO purchase_detail ( purchase, line, item, description, quantity, price, discount_expr, taxes, status, delivered) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePurchase_detailStmt()
	throws SQLException {
		purchase_detailStmt.close();
	}	
	/**
	 * Purchase_detail
	 * @param purchase Identificador del Pedido de Compra
	 * @param line Numero de linea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPurchase_detail(Integer purchase, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Double delivered)
	throws SQLException {
	
		if ( purchase == null ) {
			this.purchase_detailStmt.setNull(1, 4);
		}
		else {
			this.purchase_detailStmt.setInt(1, purchase);
		}
		if ( line == null ) {
			this.purchase_detailStmt.setNull(2, 5);
		}
		else {
			this.purchase_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.purchase_detailStmt.setNull(3, 4);
		}
		else {
			this.purchase_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.purchase_detailStmt.setNull(4, 12);
		}
		else {
			this.purchase_detailStmt.setString(4, description);
		}
		if ( quantity == null ) {
			this.purchase_detailStmt.setNull(5, 8);
		}
		else {
			this.purchase_detailStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.purchase_detailStmt.setNull(6, 8);
		}
		else {
			this.purchase_detailStmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			this.purchase_detailStmt.setNull(7, 12);
		}
		else {
			this.purchase_detailStmt.setString(7, discount_expr);
		}
		if ( taxes == null ) {
			this.purchase_detailStmt.setNull(8, 8);
		}
		else {
			this.purchase_detailStmt.setDouble(8, taxes);
		}
		if ( status == null ) {
			this.purchase_detailStmt.setNull(9, -6);
		}
		else {
			this.purchase_detailStmt.setShort(9, status);
		}
		if ( delivered == null ) {
			this.purchase_detailStmt.setNull(10, 8);
		}
		else {
			this.purchase_detailStmt.setDouble(10, delivered);
		}

		this.purchase_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.purchase_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement holiday_detailStmt;
	
	private void initHoliday_detailStmt()
	throws SQLException {
		this.holiday_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO holiday_detail ( holiday, date, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeHoliday_detailStmt()
	throws SQLException {
		holiday_detailStmt.close();
	}	
	/**
	 * Holiday_detail
	 * @param holiday Identificador de Festividad
	 * @param date Fecha Festiva
	 * @param description Descripcion de Festividad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertHoliday_detail(Integer holiday, Date date, String description)
	throws SQLException {
	
		if ( holiday == null ) {
			this.holiday_detailStmt.setNull(1, 4);
		}
		else {
			this.holiday_detailStmt.setInt(1, holiday);
		}
		if ( date == null ) {
			this.holiday_detailStmt.setNull(2, 91);
		}
		else {
			this.holiday_detailStmt.setDate(2, date);
		}
		if ( description == null ) {
			this.holiday_detailStmt.setNull(3, 12);
		}
		else {
			this.holiday_detailStmt.setString(3, description);
		}

		this.holiday_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.holiday_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pm_type_detailStmt;
	
	private void initPm_type_detailStmt()
	throws SQLException {
		this.pm_type_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pm_type_detail ( type, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePm_type_detailStmt()
	throws SQLException {
		pm_type_detailStmt.close();
	}	
	/**
	 * Pm_type_detail
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail(Short type, String description)
	throws SQLException {
	
		if ( type == null ) {
			this.pm_type_detailStmt.setNull(1, -6);
		}
		else {
			this.pm_type_detailStmt.setShort(1, type);
		}
		if ( description == null ) {
			this.pm_type_detailStmt.setNull(2, 12);
		}
		else {
			this.pm_type_detailStmt.setString(2, description);
		}

		this.pm_type_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pm_type_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement target_itemStmt;
	
	private void initTarget_itemStmt()
	throws SQLException {
		this.target_itemStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_item ( target, item, status) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTarget_itemStmt()
	throws SQLException {
		target_itemStmt.close();
	}	
	/**
	 * Target_item
	 * @param target Identificador del Cliente Potencial
	 * @param item Identificador del Articulo
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_item(Integer target, Integer item, Short status)
	throws SQLException {
	
		if ( target == null ) {
			this.target_itemStmt.setNull(1, 4);
		}
		else {
			this.target_itemStmt.setInt(1, target);
		}
		if ( item == null ) {
			this.target_itemStmt.setNull(2, 4);
		}
		else {
			this.target_itemStmt.setInt(2, item);
		}
		if ( status == null ) {
			this.target_itemStmt.setNull(3, -6);
		}
		else {
			this.target_itemStmt.setShort(3, status);
		}

		this.target_itemStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.target_itemStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement holidayStmt;
	
	private void initHolidayStmt()
	throws SQLException {
		this.holidayStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO holiday ( description, holiday, editable) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeHolidayStmt()
	throws SQLException {
		holidayStmt.close();
	}	
	/**
	 * Holiday
	 * @param description Descripcion de la Festividad
	 * @param holiday Identificador de Festividad
	 * @param editable Indica si es editable o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertHoliday(String description, Integer holiday, Boolean editable)
	throws SQLException {
	
		if ( description == null ) {
			this.holidayStmt.setNull(1, 12);
		}
		else {
			this.holidayStmt.setString(1, description);
		}
		if ( holiday == null ) {
			this.holidayStmt.setNull(2, 4);
		}
		else {
			this.holidayStmt.setInt(2, holiday);
		}
		if ( editable == null ) {
			this.holidayStmt.setNull(3, -7);
		}
		else {
			this.holidayStmt.setBoolean(3, editable);
		}

		this.holidayStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.holidayStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pm_type_detail_accountStmt;
	
	private void initPm_type_detail_accountStmt()
	throws SQLException {
		this.pm_type_detail_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pm_type_detail_account ( pm_type_detail, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePm_type_detail_accountStmt()
	throws SQLException {
		pm_type_detail_accountStmt.close();
	}	
	/**
	 * Pm_type_detail_account
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail_account(Integer pm_type_detail, String account)
	throws SQLException {
	
		if ( pm_type_detail == null ) {
			this.pm_type_detail_accountStmt.setNull(1, 4);
		}
		else {
			this.pm_type_detail_accountStmt.setInt(1, pm_type_detail);
		}
		if ( account == null ) {
			this.pm_type_detail_accountStmt.setNull(2, 1);
		}
		else {
			this.pm_type_detail_accountStmt.setString(2, account);
		}

		this.pm_type_detail_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pm_type_detail_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commission_categoryStmt;
	
	private void initCommission_categoryStmt()
	throws SQLException {
		this.commission_categoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_category ( commission, category, quantity, rate) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommission_categoryStmt()
	throws SQLException {
		commission_categoryStmt.close();
	}	
	/**
	 * Commission_category
	 * @param commission Identificador de la Comision
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_category(Integer commission, Integer category, Double quantity, Double rate)
	throws SQLException {
	
		if ( commission == null ) {
			this.commission_categoryStmt.setNull(1, 4);
		}
		else {
			this.commission_categoryStmt.setInt(1, commission);
		}
		if ( category == null ) {
			this.commission_categoryStmt.setNull(2, 4);
		}
		else {
			this.commission_categoryStmt.setInt(2, category);
		}
		if ( quantity == null ) {
			this.commission_categoryStmt.setNull(3, 8);
		}
		else {
			this.commission_categoryStmt.setDouble(3, quantity);
		}
		if ( rate == null ) {
			this.commission_categoryStmt.setNull(4, 8);
		}
		else {
			this.commission_categoryStmt.setDouble(4, rate);
		}

		this.commission_categoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commission_categoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement academic_yearStmt;
	
	private void initAcademic_yearStmt()
	throws SQLException {
		this.academic_yearStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO academic_year ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAcademic_yearStmt()
	throws SQLException {
		academic_yearStmt.close();
	}	
	/**
	 * Academic_year
	 * @param description Descripcion del Aùo Academico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAcademic_year(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.academic_yearStmt.setNull(1, 12);
		}
		else {
			this.academic_yearStmt.setString(1, description);
		}

		this.academic_yearStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.academic_yearStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement creditor_accountStmt;
	
	private void initCreditor_accountStmt()
	throws SQLException {
		this.creditor_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO creditor_account ( creditor, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCreditor_accountStmt()
	throws SQLException {
		creditor_accountStmt.close();
	}	
	/**
	 * Creditor_account
	 * @param creditor Identificador del Acreedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCreditor_account(Integer creditor, String account)
	throws SQLException {
	
		if ( creditor == null ) {
			this.creditor_accountStmt.setNull(1, 4);
		}
		else {
			this.creditor_accountStmt.setInt(1, creditor);
		}
		if ( account == null ) {
			this.creditor_accountStmt.setNull(2, 1);
		}
		else {
			this.creditor_accountStmt.setString(2, account);
		}

		this.creditor_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.creditor_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement finance_trackingStmt;
	
	private void initFinance_trackingStmt()
	throws SQLException {
		this.finance_trackingStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO finance_tracking ( finance, tracking_date, type, description, pm_type_detail, rbank, amount, recorded) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFinance_trackingStmt()
	throws SQLException {
		finance_trackingStmt.close();
	}	
	/**
	 * Finance_tracking
	 * @param finance Identificador de Vencimiento
	 * @param tracking_date Fecha de Seguimiento
	 * @param type Tipo de Seguimiento
	 * @param description Descripcion del Seguimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la Compaùia
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFinance_tracking(Integer finance, Date tracking_date, Short type, String description, Integer pm_type_detail, Integer rbank, Double amount, Boolean recorded)
	throws SQLException {
	
		if ( finance == null ) {
			this.finance_trackingStmt.setNull(1, 4);
		}
		else {
			this.finance_trackingStmt.setInt(1, finance);
		}
		if ( tracking_date == null ) {
			this.finance_trackingStmt.setNull(2, 91);
		}
		else {
			this.finance_trackingStmt.setDate(2, tracking_date);
		}
		if ( type == null ) {
			this.finance_trackingStmt.setNull(3, -6);
		}
		else {
			this.finance_trackingStmt.setShort(3, type);
		}
		if ( description == null ) {
			this.finance_trackingStmt.setNull(4, 12);
		}
		else {
			this.finance_trackingStmt.setString(4, description);
		}
		if ( pm_type_detail == null ) {
			this.finance_trackingStmt.setNull(5, 4);
		}
		else {
			this.finance_trackingStmt.setInt(5, pm_type_detail);
		}
		if ( rbank == null ) {
			this.finance_trackingStmt.setNull(6, 4);
		}
		else {
			this.finance_trackingStmt.setInt(6, rbank);
		}
		if ( amount == null ) {
			this.finance_trackingStmt.setNull(7, 8);
		}
		else {
			this.finance_trackingStmt.setDouble(7, amount);
		}
		if ( recorded == null ) {
			this.finance_trackingStmt.setNull(8, -7);
		}
		else {
			this.finance_trackingStmt.setBoolean(8, recorded);
		}

		this.finance_trackingStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.finance_trackingStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rattachStmt;
	
	private void initRattachStmt()
	throws SQLException {
		this.rattachStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rattach ( registry, category, mimeType, description, data, type, scope) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRattachStmt()
	throws SQLException {
		rattachStmt.close();
	}	
	/**
	 * Rattach
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param category Categoria del Archivo Adjunto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRattach(Integer registry, Integer category, Short mimeType, String description, InputStream data, Short type, Integer scope)
	throws SQLException {
	
		if ( registry == null ) {
			this.rattachStmt.setNull(1, 4);
		}
		else {
			this.rattachStmt.setInt(1, registry);
		}
		if ( category == null ) {
			this.rattachStmt.setNull(2, 4);
		}
		else {
			this.rattachStmt.setInt(2, category);
		}
		if ( mimeType == null ) {
			this.rattachStmt.setNull(3, -6);
		}
		else {
			this.rattachStmt.setShort(3, mimeType);
		}
		if ( description == null ) {
			this.rattachStmt.setNull(4, 12);
		}
		else {
			this.rattachStmt.setString(4, description);
		}
		if ( data == null ) {
			this.rattachStmt.setNull(5, -4);
		}
		else {
			this.rattachStmt.setBinaryStream(5, data);
		}
		if ( type == null ) {
			this.rattachStmt.setNull(6, -6);
		}
		else {
			this.rattachStmt.setShort(6, type);
		}
		if ( scope == null ) {
			this.rattachStmt.setNull(7, 4);
		}
		else {
			this.rattachStmt.setInt(7, scope);
		}

		this.rattachStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rattachStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement sales_detailStmt;
	
	private void initSales_detailStmt()
	throws SQLException {
		this.sales_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales_detail ( sales, line, item, description, quantity, price, discount_expr, taxes, status, source, offer_detail, delivered) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSales_detailStmt()
	throws SQLException {
		sales_detailStmt.close();
	}	
	/**
	 * Sales_detail
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de lùnea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param source Origen del Detalle de Pedido
	 * @param offer_detail Identificador del Detalle del Presupuesto Origen
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSales_detail(Integer sales, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Short source, Integer offer_detail, Double delivered)
	throws SQLException {
	
		if ( sales == null ) {
			this.sales_detailStmt.setNull(1, 4);
		}
		else {
			this.sales_detailStmt.setInt(1, sales);
		}
		if ( line == null ) {
			this.sales_detailStmt.setNull(2, 5);
		}
		else {
			this.sales_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.sales_detailStmt.setNull(3, 4);
		}
		else {
			this.sales_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.sales_detailStmt.setNull(4, 12);
		}
		else {
			this.sales_detailStmt.setString(4, description);
		}
		if ( quantity == null ) {
			this.sales_detailStmt.setNull(5, 8);
		}
		else {
			this.sales_detailStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.sales_detailStmt.setNull(6, 8);
		}
		else {
			this.sales_detailStmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			this.sales_detailStmt.setNull(7, 12);
		}
		else {
			this.sales_detailStmt.setString(7, discount_expr);
		}
		if ( taxes == null ) {
			this.sales_detailStmt.setNull(8, 8);
		}
		else {
			this.sales_detailStmt.setDouble(8, taxes);
		}
		if ( status == null ) {
			this.sales_detailStmt.setNull(9, -6);
		}
		else {
			this.sales_detailStmt.setShort(9, status);
		}
		if ( source == null ) {
			this.sales_detailStmt.setNull(10, -6);
		}
		else {
			this.sales_detailStmt.setShort(10, source);
		}
		if ( offer_detail == null ) {
			this.sales_detailStmt.setNull(11, 4);
		}
		else {
			this.sales_detailStmt.setInt(11, offer_detail);
		}
		if ( delivered == null ) {
			this.sales_detailStmt.setNull(12, 8);
		}
		else {
			this.sales_detailStmt.setDouble(12, delivered);
		}

		this.sales_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.sales_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement curriculumStmt;
	
	private void initCurriculumStmt()
	throws SQLException {
		this.curriculumStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO curriculum ( registry, entrydate, birthdate, birthplace, residenceplace, geozone, city, zip, address, phone, driver_licenses, driver_license_date, gender, postcategory) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCurriculumStmt()
	throws SQLException {
		curriculumStmt.close();
	}	
	/**
	 * Curriculum
	 * @param registry Registro de la Persona
	 * @param entrydate Fecha de registro del Curriculum
	 * @param birthdate Fecha de nacimiento de la Persona
	 * @param birthplace Lugar de nacimiento de la Persona
	 * @param residenceplace Lugar de residencia de la Persona
	 * @param geozone Zona Geografica de residencia de la Persona
	 * @param city Ciudad de residencia de la Persona
	 * @param zip Codigo postal de residencia de la Persona
	 * @param address Direccion de residencia de la Persona
	 * @param phone Telefono de la Persona
	 * @param driver_licenses Permiso de conducir de la Persona
	 * @param driver_license_date Fecha de expedicion del Permiso de Conducir de la Persona
	 * @param gender Sexo de la Persona
	 * @param postcategory Rol del Curriculum dentro de la Empresa
	 * @throws SQLException
	*/
	protected void insertCurriculum(Integer registry, Date entrydate, Date birthdate, String birthplace, String residenceplace, Integer geozone, String city, String zip, String address, String phone, String driver_licenses, Date driver_license_date, Short gender, Short postcategory)
	throws SQLException {
	
		if ( registry == null ) {
			this.curriculumStmt.setNull(1, 4);
		}
		else {
			this.curriculumStmt.setInt(1, registry);
		}
		if ( entrydate == null ) {
			this.curriculumStmt.setNull(2, 91);
		}
		else {
			this.curriculumStmt.setDate(2, entrydate);
		}
		if ( birthdate == null ) {
			this.curriculumStmt.setNull(3, 91);
		}
		else {
			this.curriculumStmt.setDate(3, birthdate);
		}
		if ( birthplace == null ) {
			this.curriculumStmt.setNull(4, 1);
		}
		else {
			this.curriculumStmt.setString(4, birthplace);
		}
		if ( residenceplace == null ) {
			this.curriculumStmt.setNull(5, 1);
		}
		else {
			this.curriculumStmt.setString(5, residenceplace);
		}
		if ( geozone == null ) {
			this.curriculumStmt.setNull(6, 4);
		}
		else {
			this.curriculumStmt.setInt(6, geozone);
		}
		if ( city == null ) {
			this.curriculumStmt.setNull(7, 12);
		}
		else {
			this.curriculumStmt.setString(7, city);
		}
		if ( zip == null ) {
			this.curriculumStmt.setNull(8, 12);
		}
		else {
			this.curriculumStmt.setString(8, zip);
		}
		if ( address == null ) {
			this.curriculumStmt.setNull(9, 12);
		}
		else {
			this.curriculumStmt.setString(9, address);
		}
		if ( phone == null ) {
			this.curriculumStmt.setNull(10, 12);
		}
		else {
			this.curriculumStmt.setString(10, phone);
		}
		if ( driver_licenses == null ) {
			this.curriculumStmt.setNull(11, 12);
		}
		else {
			this.curriculumStmt.setString(11, driver_licenses);
		}
		if ( driver_license_date == null ) {
			this.curriculumStmt.setNull(12, 91);
		}
		else {
			this.curriculumStmt.setDate(12, driver_license_date);
		}
		if ( gender == null ) {
			this.curriculumStmt.setNull(13, -6);
		}
		else {
			this.curriculumStmt.setShort(13, gender);
		}
		if ( postcategory == null ) {
			this.curriculumStmt.setNull(14, -6);
		}
		else {
			this.curriculumStmt.setShort(14, postcategory);
		}

		this.curriculumStmt.executeUpdate();
		
	}
	
	private PreparedStatement makeStmt;
	
	private void initMakeStmt()
	throws SQLException {
		this.makeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO make ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMakeStmt()
	throws SQLException {
		makeStmt.close();
	}	
	/**
	 * Make
	 * @param name Nombre del Fabricante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMake(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.makeStmt.setNull(1, 12);
		}
		else {
			this.makeStmt.setString(1, name);
		}

		this.makeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.makeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rbank_accountStmt;
	
	private void initRbank_accountStmt()
	throws SQLException {
		this.rbank_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rbank_account ( rbank, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRbank_accountStmt()
	throws SQLException {
		rbank_accountStmt.close();
	}	
	/**
	 * Rbank_account
	 * @param rbank Identificador de la Cuenta Bancaria
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRbank_account(Integer rbank, String account)
	throws SQLException {
	
		if ( rbank == null ) {
			this.rbank_accountStmt.setNull(1, 4);
		}
		else {
			this.rbank_accountStmt.setInt(1, rbank);
		}
		if ( account == null ) {
			this.rbank_accountStmt.setNull(2, 1);
		}
		else {
			this.rbank_accountStmt.setString(2, account);
		}

		this.rbank_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rbank_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement modelStmt;
	
	private void initModelStmt()
	throws SQLException {
		this.modelStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO model ( make, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeModelStmt()
	throws SQLException {
		modelStmt.close();
	}	
	/**
	 * Model
	 * @param make Identificador del Fabricante
	 * @param name Nombre del Modelo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertModel(Integer make, String name)
	throws SQLException {
	
		if ( make == null ) {
			this.modelStmt.setNull(1, 4);
		}
		else {
			this.modelStmt.setInt(1, make);
		}
		if ( name == null ) {
			this.modelStmt.setNull(2, 12);
		}
		else {
			this.modelStmt.setString(2, name);
		}

		this.modelStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.modelStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cnaeStmt;
	
	private void initCnaeStmt()
	throws SQLException {
		this.cnaeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cnae ( code, title) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCnaeStmt()
	throws SQLException {
		cnaeStmt.close();
	}	
	/**
	 * Cnae
	 * @param code Codigo del CNAE
	 * @param title Titulo del CNAE
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCnae(String code, String title)
	throws SQLException {
	
		if ( code == null ) {
			this.cnaeStmt.setNull(1, 12);
		}
		else {
			this.cnaeStmt.setString(1, code);
		}
		if ( title == null ) {
			this.cnaeStmt.setNull(2, 12);
		}
		else {
			this.cnaeStmt.setString(2, title);
		}

		this.cnaeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cnaeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement departmentStmt;
	
	private void initDepartmentStmt()
	throws SQLException {
		this.departmentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO department ( parent, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDepartmentStmt()
	throws SQLException {
		departmentStmt.close();
	}	
	/**
	 * Department
	 * @param parent Identificador del Departamento padre
	 * @param description Descripcion del Departamento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDepartment(Integer parent, String description)
	throws SQLException {
	
		if ( parent == null ) {
			this.departmentStmt.setNull(1, 4);
		}
		else {
			this.departmentStmt.setInt(1, parent);
		}
		if ( description == null ) {
			this.departmentStmt.setNull(2, 12);
		}
		else {
			this.departmentStmt.setString(2, description);
		}

		this.departmentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.departmentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement target_profileStmt;
	
	private void initTarget_profileStmt()
	throws SQLException {
		this.target_profileStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_profile ( target, last_update, question, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTarget_profileStmt()
	throws SQLException {
		target_profileStmt.close();
	}	
	/**
	 * Target_profile
	 * @param target Identificador del Cliente Potencial
	 * @param last_update Fecha de la ultima modificacion del Perfil del Cliente Potencial
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_profile(Integer target, Timestamp last_update, Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
	
		if ( target == null ) {
			this.target_profileStmt.setNull(1, 4);
		}
		else {
			this.target_profileStmt.setInt(1, target);
		}
		if ( last_update == null ) {
			this.target_profileStmt.setNull(2, 93);
		}
		else {
			this.target_profileStmt.setTimestamp(2, last_update);
		}
		if ( question == null ) {
			this.target_profileStmt.setNull(3, 4);
		}
		else {
			this.target_profileStmt.setInt(3, question);
		}
		if ( value_text == null ) {
			this.target_profileStmt.setNull(4, 12);
		}
		else {
			this.target_profileStmt.setString(4, value_text);
		}
		if ( value_number == null ) {
			this.target_profileStmt.setNull(5, 8);
		}
		else {
			this.target_profileStmt.setDouble(5, value_number);
		}
		if ( value_date == null ) {
			this.target_profileStmt.setNull(6, 93);
		}
		else {
			this.target_profileStmt.setTimestamp(6, value_date);
		}

		this.target_profileStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.target_profileStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement purchaseStmt;
	
	private void initPurchaseStmt()
	throws SQLException {
		this.purchaseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO purchase ( supplier, series, number, address, discount_expr, issue_date, pay_method, document_type, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePurchaseStmt()
	throws SQLException {
		purchaseStmt.close();
	}	
	/**
	 * Purchase
	 * @param supplier Identificador del Proveedor
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param address Identificador de la Direccion del Proveedor
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPurchase(Integer supplier, String series, Integer number, Integer address, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
	
		if ( supplier == null ) {
			this.purchaseStmt.setNull(1, 4);
		}
		else {
			this.purchaseStmt.setInt(1, supplier);
		}
		if ( series == null ) {
			this.purchaseStmt.setNull(2, 1);
		}
		else {
			this.purchaseStmt.setString(2, series);
		}
		if ( number == null ) {
			this.purchaseStmt.setNull(3, 4);
		}
		else {
			this.purchaseStmt.setInt(3, number);
		}
		if ( address == null ) {
			this.purchaseStmt.setNull(4, 4);
		}
		else {
			this.purchaseStmt.setInt(4, address);
		}
		if ( discount_expr == null ) {
			this.purchaseStmt.setNull(5, 12);
		}
		else {
			this.purchaseStmt.setString(5, discount_expr);
		}
		if ( issue_date == null ) {
			this.purchaseStmt.setNull(6, 91);
		}
		else {
			this.purchaseStmt.setDate(6, issue_date);
		}
		if ( pay_method == null ) {
			this.purchaseStmt.setNull(7, 4);
		}
		else {
			this.purchaseStmt.setInt(7, pay_method);
		}
		if ( document_type == null ) {
			this.purchaseStmt.setNull(8, -6);
		}
		else {
			this.purchaseStmt.setShort(8, document_type);
		}
		if ( security_level == null ) {
			this.purchaseStmt.setNull(9, -6);
		}
		else {
			this.purchaseStmt.setShort(9, security_level);
		}
		if ( status == null ) {
			this.purchaseStmt.setNull(10, -6);
		}
		else {
			this.purchaseStmt.setShort(10, status);
		}
		if ( workplace == null ) {
			this.purchaseStmt.setNull(11, 4);
		}
		else {
			this.purchaseStmt.setInt(11, workplace);
		}
		if ( scope == null ) {
			this.purchaseStmt.setNull(12, 4);
		}
		else {
			this.purchaseStmt.setInt(12, scope);
		}
		if ( number_of_pymnts == null ) {
			this.purchaseStmt.setNull(13, 5);
		}
		else {
			this.purchaseStmt.setInt(13, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.purchaseStmt.setNull(14, 5);
		}
		else {
			this.purchaseStmt.setInt(14, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.purchaseStmt.setNull(15, 5);
		}
		else {
			this.purchaseStmt.setInt(15, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.purchaseStmt.setNull(16, 12);
		}
		else {
			this.purchaseStmt.setString(16, pymnt_days);
		}
		if ( bank == null ) {
			this.purchaseStmt.setNull(17, 4);
		}
		else {
			this.purchaseStmt.setInt(17, bank);
		}
		if ( bank_account == null ) {
			this.purchaseStmt.setNull(18, 12);
		}
		else {
			this.purchaseStmt.setString(18, bank_account);
		}

		this.purchaseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.purchaseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement web_infoStmt;
	
	private void initWeb_infoStmt()
	throws SQLException {
		this.web_infoStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info ( company, commercial_description, schedule, slogan) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWeb_infoStmt()
	throws SQLException {
		web_infoStmt.close();
	}	
	/**
	 * Web_info
	 * @param company Empresa
	 * @param commercial_description Descripcion comercial
	 * @param schedule Horario
	 * @param slogan Slogan
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info(Integer company, InputStream commercial_description, InputStream schedule, String slogan)
	throws SQLException {
	
		if ( company == null ) {
			this.web_infoStmt.setNull(1, 4);
		}
		else {
			this.web_infoStmt.setInt(1, company);
		}
		if ( commercial_description == null ) {
			this.web_infoStmt.setNull(2, -1);
		}
		else {
			this.web_infoStmt.setAsciiStream(2, commercial_description);
		}
		if ( schedule == null ) {
			this.web_infoStmt.setNull(3, -1);
		}
		else {
			this.web_infoStmt.setAsciiStream(3, schedule);
		}
		if ( slogan == null ) {
			this.web_infoStmt.setNull(4, 12);
		}
		else {
			this.web_infoStmt.setString(4, slogan);
		}

		this.web_infoStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.web_infoStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement lh_positionStmt;
	
	private void initLh_positionStmt()
	throws SQLException {
		this.lh_positionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_position ( employee, startingdate, endingdate, description, workplace, workactivity, calendar) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLh_positionStmt()
	throws SQLException {
		lh_positionStmt.close();
	}	
	/**
	 * Lh_position
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Cargo
	 * @param endingdate Fecha de finalizacion del Cargo
	 * @param description Descripcion del Cargo
	 * @param workplace Identificador del Centro de Trabajo
	 * @param workactivity Identificador de la Actividad
	 * @param calendar Identificador del Calendario Laboral
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLh_position(Integer employee, Date startingdate, Date endingdate, String description, Integer workplace, Integer workactivity, Integer calendar)
	throws SQLException {
	
		if ( employee == null ) {
			this.lh_positionStmt.setNull(1, 4);
		}
		else {
			this.lh_positionStmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			this.lh_positionStmt.setNull(2, 91);
		}
		else {
			this.lh_positionStmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			this.lh_positionStmt.setNull(3, 91);
		}
		else {
			this.lh_positionStmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			this.lh_positionStmt.setNull(4, 12);
		}
		else {
			this.lh_positionStmt.setString(4, description);
		}
		if ( workplace == null ) {
			this.lh_positionStmt.setNull(5, 4);
		}
		else {
			this.lh_positionStmt.setInt(5, workplace);
		}
		if ( workactivity == null ) {
			this.lh_positionStmt.setNull(6, 4);
		}
		else {
			this.lh_positionStmt.setInt(6, workactivity);
		}
		if ( calendar == null ) {
			this.lh_positionStmt.setNull(7, 4);
		}
		else {
			this.lh_positionStmt.setInt(7, calendar);
		}

		this.lh_positionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.lh_positionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_evaluateStmt;
	
	private void initCv_evaluateStmt()
	throws SQLException {
		this.cv_evaluateStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate ( type, value, curriculum) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_evaluateStmt()
	throws SQLException {
		cv_evaluateStmt.close();
	}	
	/**
	 * Cv_evaluate
	 * @param type Tipo de Evaluacion
	 * @param value Valor de la Evaluacion
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_evaluate(Integer type, Short value, Integer curriculum)
	throws SQLException {
	
		if ( type == null ) {
			this.cv_evaluateStmt.setNull(1, 4);
		}
		else {
			this.cv_evaluateStmt.setInt(1, type);
		}
		if ( value == null ) {
			this.cv_evaluateStmt.setNull(2, -6);
		}
		else {
			this.cv_evaluateStmt.setShort(2, value);
		}
		if ( curriculum == null ) {
			this.cv_evaluateStmt.setNull(3, 4);
		}
		else {
			this.cv_evaluateStmt.setInt(3, curriculum);
		}

		this.cv_evaluateStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_evaluateStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_alumnStmt;
	
	private void initCourse_alumnStmt()
	throws SQLException {
		this.course_alumnStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_alumn ( course, customer, status) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_alumnStmt()
	throws SQLException {
		course_alumnStmt.close();
	}	
	/**
	 * Course_alumn
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @param status Estado del alumno en el curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_alumn(Integer course, Integer customer, Short status)
	throws SQLException {
	
		if ( course == null ) {
			this.course_alumnStmt.setNull(1, 4);
		}
		else {
			this.course_alumnStmt.setInt(1, course);
		}
		if ( customer == null ) {
			this.course_alumnStmt.setNull(2, 4);
		}
		else {
			this.course_alumnStmt.setInt(2, customer);
		}
		if ( status == null ) {
			this.course_alumnStmt.setNull(3, -6);
		}
		else {
			this.course_alumnStmt.setShort(3, status);
		}

		this.course_alumnStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_alumnStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement questionStmt;
	
	private void initQuestionStmt()
	throws SQLException {
		this.questionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO question ( active, question_text, type, argument) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeQuestionStmt()
	throws SQLException {
		questionStmt.close();
	}	
	/**
	 * Question
	 * @param active Indica si la Pregunta esta activa o no
	 * @param question_text Texto de la Pregunta
	 * @param type Tipo de Pregunta
	 * @param argument Argumentacion de la Pregunta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertQuestion(Boolean active, String question_text, Short type, String argument)
	throws SQLException {
	
		if ( active == null ) {
			this.questionStmt.setNull(1, -7);
		}
		else {
			this.questionStmt.setBoolean(1, active);
		}
		if ( question_text == null ) {
			this.questionStmt.setNull(2, 12);
		}
		else {
			this.questionStmt.setString(2, question_text);
		}
		if ( type == null ) {
			this.questionStmt.setNull(3, -6);
		}
		else {
			this.questionStmt.setShort(3, type);
		}
		if ( argument == null ) {
			this.questionStmt.setNull(4, 12);
		}
		else {
			this.questionStmt.setString(4, argument);
		}

		this.questionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.questionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement record_dataStmt;
	
	private void initRecord_dataStmt()
	throws SQLException {
		this.record_dataStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO record_data ( registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRecord_dataStmt()
	throws SQLException {
		record_dataStmt.close();
	}	
	/**
	 * Record_data
	 * @param registry Registro de la Empresa
	 * @param creation_date Fecha de creacion del Dato Registral
	 * @param description Descripcion del Dato Registral
	 * @param notary Notario del Dato Registral
	 * @param number Numero del Dato Registral
	 * @param record_date Fecha de registro del Dato Registral
	 * @param volume Tomo del Dato Registral
	 * @param section Seccion del Dato Registral
	 * @param page Folio del Dato Registral
	 * @param sheet Hoja del Dato Registral
	 * @param registration Inscripcion del Dato Registral
	 * @param attach Archivo adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRecord_data(Integer registry, Date creation_date, String description, String notary, String number, Date record_date, String volume, String section, String page, String sheet, String registration, Integer attach)
	throws SQLException {
	
		if ( registry == null ) {
			this.record_dataStmt.setNull(1, 4);
		}
		else {
			this.record_dataStmt.setInt(1, registry);
		}
		if ( creation_date == null ) {
			this.record_dataStmt.setNull(2, 91);
		}
		else {
			this.record_dataStmt.setDate(2, creation_date);
		}
		if ( description == null ) {
			this.record_dataStmt.setNull(3, 12);
		}
		else {
			this.record_dataStmt.setString(3, description);
		}
		if ( notary == null ) {
			this.record_dataStmt.setNull(4, 12);
		}
		else {
			this.record_dataStmt.setString(4, notary);
		}
		if ( number == null ) {
			this.record_dataStmt.setNull(5, 12);
		}
		else {
			this.record_dataStmt.setString(5, number);
		}
		if ( record_date == null ) {
			this.record_dataStmt.setNull(6, 91);
		}
		else {
			this.record_dataStmt.setDate(6, record_date);
		}
		if ( volume == null ) {
			this.record_dataStmt.setNull(7, 12);
		}
		else {
			this.record_dataStmt.setString(7, volume);
		}
		if ( section == null ) {
			this.record_dataStmt.setNull(8, 12);
		}
		else {
			this.record_dataStmt.setString(8, section);
		}
		if ( page == null ) {
			this.record_dataStmt.setNull(9, 12);
		}
		else {
			this.record_dataStmt.setString(9, page);
		}
		if ( sheet == null ) {
			this.record_dataStmt.setNull(10, 12);
		}
		else {
			this.record_dataStmt.setString(10, sheet);
		}
		if ( registration == null ) {
			this.record_dataStmt.setNull(11, 12);
		}
		else {
			this.record_dataStmt.setString(11, registration);
		}
		if ( attach == null ) {
			this.record_dataStmt.setNull(12, 4);
		}
		else {
			this.record_dataStmt.setInt(12, attach);
		}

		this.record_dataStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.record_dataStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement employeeStmt;
	
	private void initEmployeeStmt()
	throws SQLException {
		this.employeeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO employee ( registry, workactivity, social_security_num, agreement_time, active) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEmployeeStmt()
	throws SQLException {
		employeeStmt.close();
	}	
	/**
	 * Employee
	 * @param registry Registro del Empleado
	 * @param workactivity Identificador de Actividad
	 * @param social_security_num Nùmero de Seguridad Social del Empleado
	 * @param agreement_time Horas del Convenio
	 * @param active Indica si el Empleado sigue vinculado a la Empresa o no
	 * @throws SQLException
	*/
	protected void insertEmployee(Integer registry, Integer workactivity, String social_security_num, Integer agreement_time, Boolean active)
	throws SQLException {
	
		if ( registry == null ) {
			this.employeeStmt.setNull(1, 4);
		}
		else {
			this.employeeStmt.setInt(1, registry);
		}
		if ( workactivity == null ) {
			this.employeeStmt.setNull(2, 4);
		}
		else {
			this.employeeStmt.setInt(2, workactivity);
		}
		if ( social_security_num == null ) {
			this.employeeStmt.setNull(3, 12);
		}
		else {
			this.employeeStmt.setString(3, social_security_num);
		}
		if ( agreement_time == null ) {
			this.employeeStmt.setNull(4, 4);
		}
		else {
			this.employeeStmt.setInt(4, agreement_time);
		}
		if ( active == null ) {
			this.employeeStmt.setNull(5, -7);
		}
		else {
			this.employeeStmt.setBoolean(5, active);
		}

		this.employeeStmt.executeUpdate();
		
	}
	
	private PreparedStatement alumn_loanStmt;
	
	private void initAlumn_loanStmt()
	throws SQLException {
		this.alumn_loanStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO alumn_loan ( customer, material, loan_date, end_date, comments) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAlumn_loanStmt()
	throws SQLException {
		alumn_loanStmt.close();
	}	
	/**
	 * Alumn_loan
	 * @param customer Alumno al que se le realizo el Prestamo
	 * @param material Material prestado
	 * @param loan_date Fecha del Prestamo
	 * @param end_date Fecha devolucion del material
	 * @param comments Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAlumn_loan(Integer customer, String material, Date loan_date, Date end_date, String comments)
	throws SQLException {
	
		if ( customer == null ) {
			this.alumn_loanStmt.setNull(1, 4);
		}
		else {
			this.alumn_loanStmt.setInt(1, customer);
		}
		if ( material == null ) {
			this.alumn_loanStmt.setNull(2, 12);
		}
		else {
			this.alumn_loanStmt.setString(2, material);
		}
		if ( loan_date == null ) {
			this.alumn_loanStmt.setNull(3, 91);
		}
		else {
			this.alumn_loanStmt.setDate(3, loan_date);
		}
		if ( end_date == null ) {
			this.alumn_loanStmt.setNull(4, 91);
		}
		else {
			this.alumn_loanStmt.setDate(4, end_date);
		}
		if ( comments == null ) {
			this.alumn_loanStmt.setNull(5, 12);
		}
		else {
			this.alumn_loanStmt.setString(5, comments);
		}

		this.alumn_loanStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.alumn_loanStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement activityStmt;
	
	private void initActivityStmt()
	throws SQLException {
		this.activityStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity ( dossier, activity_type, workgroup) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeActivityStmt()
	throws SQLException {
		activityStmt.close();
	}	
	/**
	 * Activity
	 * @param dossier Identificador del Expendiente
	 * @param activity_type Tipo de Actividad
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity(Integer dossier, Integer activity_type, Integer workgroup)
	throws SQLException {
	
		if ( dossier == null ) {
			this.activityStmt.setNull(1, 4);
		}
		else {
			this.activityStmt.setInt(1, dossier);
		}
		if ( activity_type == null ) {
			this.activityStmt.setNull(2, 4);
		}
		else {
			this.activityStmt.setInt(2, activity_type);
		}
		if ( workgroup == null ) {
			this.activityStmt.setNull(3, 4);
		}
		else {
			this.activityStmt.setInt(3, workgroup);
		}

		this.activityStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.activityStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement calendarStmt;
	
	private void initCalendarStmt()
	throws SQLException {
		this.calendarStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar ( holiday, source, source_id, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCalendarStmt()
	throws SQLException {
		calendarStmt.close();
	}	
	/**
	 * Calendar
	 * @param holiday Identificador de Festivos
	 * @param source Origen
	 * @param source_id Identificador unico de la fuente
	 * @param anual_hours Horas anuales del Calendario
	 * @param description Descripcion del Calendario
	 * @param comments Comentarios
	 * @param monday Tipo de dia
	 * @param monday_hours Numero de horas laborables
	 * @param tuesday Tipo de dia
	 * @param tuesday_hours Numero de horas laborables
	 * @param wednesday Tipo de dia
	 * @param wednesday_hours Numero de horas laborables
	 * @param thursday Tipo de dia
	 * @param thursday_hours Numero de horas laborables
	 * @param friday Tipo de dia
	 * @param friday_hours Numero de horas laborables
	 * @param saturday Tipo de dia
	 * @param saturday_hours Numero de horas laborables
	 * @param sunday Tipo de dia
	 * @param sunday_hours Numero de horas laborables
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCalendar(Integer holiday, Short source, Integer source_id, Double anual_hours, String description, InputStream comments, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours)
	throws SQLException {
	
		if ( holiday == null ) {
			this.calendarStmt.setNull(1, 4);
		}
		else {
			this.calendarStmt.setInt(1, holiday);
		}
		if ( source == null ) {
			this.calendarStmt.setNull(2, -6);
		}
		else {
			this.calendarStmt.setShort(2, source);
		}
		if ( source_id == null ) {
			this.calendarStmt.setNull(3, 4);
		}
		else {
			this.calendarStmt.setInt(3, source_id);
		}
		if ( anual_hours == null ) {
			this.calendarStmt.setNull(4, 8);
		}
		else {
			this.calendarStmt.setDouble(4, anual_hours);
		}
		if ( description == null ) {
			this.calendarStmt.setNull(5, 12);
		}
		else {
			this.calendarStmt.setString(5, description);
		}
		if ( comments == null ) {
			this.calendarStmt.setNull(6, -1);
		}
		else {
			this.calendarStmt.setAsciiStream(6, comments);
		}
		if ( monday == null ) {
			this.calendarStmt.setNull(7, -6);
		}
		else {
			this.calendarStmt.setShort(7, monday);
		}
		if ( monday_hours == null ) {
			this.calendarStmt.setNull(8, 8);
		}
		else {
			this.calendarStmt.setDouble(8, monday_hours);
		}
		if ( tuesday == null ) {
			this.calendarStmt.setNull(9, -6);
		}
		else {
			this.calendarStmt.setShort(9, tuesday);
		}
		if ( tuesday_hours == null ) {
			this.calendarStmt.setNull(10, 8);
		}
		else {
			this.calendarStmt.setDouble(10, tuesday_hours);
		}
		if ( wednesday == null ) {
			this.calendarStmt.setNull(11, -6);
		}
		else {
			this.calendarStmt.setShort(11, wednesday);
		}
		if ( wednesday_hours == null ) {
			this.calendarStmt.setNull(12, 8);
		}
		else {
			this.calendarStmt.setDouble(12, wednesday_hours);
		}
		if ( thursday == null ) {
			this.calendarStmt.setNull(13, -6);
		}
		else {
			this.calendarStmt.setShort(13, thursday);
		}
		if ( thursday_hours == null ) {
			this.calendarStmt.setNull(14, 8);
		}
		else {
			this.calendarStmt.setDouble(14, thursday_hours);
		}
		if ( friday == null ) {
			this.calendarStmt.setNull(15, -6);
		}
		else {
			this.calendarStmt.setShort(15, friday);
		}
		if ( friday_hours == null ) {
			this.calendarStmt.setNull(16, 8);
		}
		else {
			this.calendarStmt.setDouble(16, friday_hours);
		}
		if ( saturday == null ) {
			this.calendarStmt.setNull(17, -6);
		}
		else {
			this.calendarStmt.setShort(17, saturday);
		}
		if ( saturday_hours == null ) {
			this.calendarStmt.setNull(18, 8);
		}
		else {
			this.calendarStmt.setDouble(18, saturday_hours);
		}
		if ( sunday == null ) {
			this.calendarStmt.setNull(19, -6);
		}
		else {
			this.calendarStmt.setShort(19, sunday);
		}
		if ( sunday_hours == null ) {
			this.calendarStmt.setNull(20, 8);
		}
		else {
			this.calendarStmt.setDouble(20, sunday_hours);
		}

		this.calendarStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.calendarStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement ec_offer_pay_infoStmt;
	
	private void initEc_offer_pay_infoStmt()
	throws SQLException {
		this.ec_offer_pay_infoStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_offer_pay_info ( offer, payment_status, authorization_number) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEc_offer_pay_infoStmt()
	throws SQLException {
		ec_offer_pay_infoStmt.close();
	}	
	/**
	 * Ec_offer_pay_info
	 * @param offer Identificador del Presupuesto
	 * @param payment_status Estado del pago
	 * @param authorization_number Numero de autorizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_offer_pay_info(Integer offer, Short payment_status, Integer authorization_number)
	throws SQLException {
	
		if ( offer == null ) {
			this.ec_offer_pay_infoStmt.setNull(1, 4);
		}
		else {
			this.ec_offer_pay_infoStmt.setInt(1, offer);
		}
		if ( payment_status == null ) {
			this.ec_offer_pay_infoStmt.setNull(2, -6);
		}
		else {
			this.ec_offer_pay_infoStmt.setShort(2, payment_status);
		}
		if ( authorization_number == null ) {
			this.ec_offer_pay_infoStmt.setNull(3, 4);
		}
		else {
			this.ec_offer_pay_infoStmt.setInt(3, authorization_number);
		}

		this.ec_offer_pay_infoStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.ec_offer_pay_infoStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement asset_activityStmt;
	
	private void initAsset_activityStmt()
	throws SQLException {
		this.asset_activityStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO asset_activity ( asset, date, from_time, to_time, who, why, status) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAsset_activityStmt()
	throws SQLException {
		asset_activityStmt.close();
	}	
	/**
	 * Asset_activity
	 * @param asset Identificador del Activo
	 * @param date Fecha de la Actividad
	 * @param from_time Hora de inicio de la Actividad
	 * @param to_time Hora final de la Actividad
	 * @param who Quien solicita el Activo
	 * @param why Motivo de solicitud del Activo
	 * @param status Estado de la Solicitud
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAsset_activity(Integer asset, Date date, Timestamp from_time, Timestamp to_time, String who, String why, Short status)
	throws SQLException {
	
		if ( asset == null ) {
			this.asset_activityStmt.setNull(1, 4);
		}
		else {
			this.asset_activityStmt.setInt(1, asset);
		}
		if ( date == null ) {
			this.asset_activityStmt.setNull(2, 91);
		}
		else {
			this.asset_activityStmt.setDate(2, date);
		}
		if ( from_time == null ) {
			this.asset_activityStmt.setNull(3, 93);
		}
		else {
			this.asset_activityStmt.setTimestamp(3, from_time);
		}
		if ( to_time == null ) {
			this.asset_activityStmt.setNull(4, 93);
		}
		else {
			this.asset_activityStmt.setTimestamp(4, to_time);
		}
		if ( who == null ) {
			this.asset_activityStmt.setNull(5, 12);
		}
		else {
			this.asset_activityStmt.setString(5, who);
		}
		if ( why == null ) {
			this.asset_activityStmt.setNull(6, 12);
		}
		else {
			this.asset_activityStmt.setString(6, why);
		}
		if ( status == null ) {
			this.asset_activityStmt.setNull(7, -6);
		}
		else {
			this.asset_activityStmt.setShort(7, status);
		}

		this.asset_activityStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.asset_activityStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement calendar_periodStmt;
	
	private void initCalendar_periodStmt()
	throws SQLException {
		this.calendar_periodStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar_period ( calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCalendar_periodStmt()
	throws SQLException {
		calendar_periodStmt.close();
	}	
	/**
	 * Calendar_period
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Periodo
	 * @param month Mes del periodo
	 * @param start_day Dia inicio del periodo
	 * @param end_day Dia fin del periodo
	 * @param monday Tipo de dia
	 * @param monday_hours Numero de horas laborables
	 * @param tuesday Tipo de dia
	 * @param tuesday_hours Numero de horas laborables
	 * @param wednesday Tipo de dia
	 * @param wednesday_hours Numero de horas laborables
	 * @param thursday Tipo de dia
	 * @param thursday_hours Numero de horas laborables
	 * @param friday Tipo de dia
	 * @param friday_hours Numero de horas laborables
	 * @param saturday Tipo de dia
	 * @param saturday_hours Numero de horas laborables
	 * @param sunday Tipo de dia
	 * @param sunday_hours Numero de horas laborables
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCalendar_period(Integer calendar, String description, Short month, Short start_day, Short end_day, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours)
	throws SQLException {
	
		if ( calendar == null ) {
			this.calendar_periodStmt.setNull(1, 4);
		}
		else {
			this.calendar_periodStmt.setInt(1, calendar);
		}
		if ( description == null ) {
			this.calendar_periodStmt.setNull(2, 12);
		}
		else {
			this.calendar_periodStmt.setString(2, description);
		}
		if ( month == null ) {
			this.calendar_periodStmt.setNull(3, -6);
		}
		else {
			this.calendar_periodStmt.setShort(3, month);
		}
		if ( start_day == null ) {
			this.calendar_periodStmt.setNull(4, -6);
		}
		else {
			this.calendar_periodStmt.setShort(4, start_day);
		}
		if ( end_day == null ) {
			this.calendar_periodStmt.setNull(5, -6);
		}
		else {
			this.calendar_periodStmt.setShort(5, end_day);
		}
		if ( monday == null ) {
			this.calendar_periodStmt.setNull(6, -6);
		}
		else {
			this.calendar_periodStmt.setShort(6, monday);
		}
		if ( monday_hours == null ) {
			this.calendar_periodStmt.setNull(7, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(7, monday_hours);
		}
		if ( tuesday == null ) {
			this.calendar_periodStmt.setNull(8, -6);
		}
		else {
			this.calendar_periodStmt.setShort(8, tuesday);
		}
		if ( tuesday_hours == null ) {
			this.calendar_periodStmt.setNull(9, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(9, tuesday_hours);
		}
		if ( wednesday == null ) {
			this.calendar_periodStmt.setNull(10, -6);
		}
		else {
			this.calendar_periodStmt.setShort(10, wednesday);
		}
		if ( wednesday_hours == null ) {
			this.calendar_periodStmt.setNull(11, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(11, wednesday_hours);
		}
		if ( thursday == null ) {
			this.calendar_periodStmt.setNull(12, -6);
		}
		else {
			this.calendar_periodStmt.setShort(12, thursday);
		}
		if ( thursday_hours == null ) {
			this.calendar_periodStmt.setNull(13, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(13, thursday_hours);
		}
		if ( friday == null ) {
			this.calendar_periodStmt.setNull(14, -6);
		}
		else {
			this.calendar_periodStmt.setShort(14, friday);
		}
		if ( friday_hours == null ) {
			this.calendar_periodStmt.setNull(15, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(15, friday_hours);
		}
		if ( saturday == null ) {
			this.calendar_periodStmt.setNull(16, -6);
		}
		else {
			this.calendar_periodStmt.setShort(16, saturday);
		}
		if ( saturday_hours == null ) {
			this.calendar_periodStmt.setNull(17, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(17, saturday_hours);
		}
		if ( sunday == null ) {
			this.calendar_periodStmt.setNull(18, -6);
		}
		else {
			this.calendar_periodStmt.setShort(18, sunday);
		}
		if ( sunday_hours == null ) {
			this.calendar_periodStmt.setNull(19, 8);
		}
		else {
			this.calendar_periodStmt.setDouble(19, sunday_hours);
		}

		this.calendar_periodStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.calendar_periodStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement fbatchStmt;
	
	private void initFbatchStmt()
	throws SQLException {
		this.fbatchStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fbatch ( description, issue_date, type, status, rbank, payment, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFbatchStmt()
	throws SQLException {
		fbatchStmt.close();
	}	
	/**
	 * Fbatch
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la Compaùia utilizado en la Remesa
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFbatch(String description, Date issue_date, Short type, Short status, Integer rbank, Boolean payment, Short security_level)
	throws SQLException {
	
		if ( description == null ) {
			this.fbatchStmt.setNull(1, 12);
		}
		else {
			this.fbatchStmt.setString(1, description);
		}
		if ( issue_date == null ) {
			this.fbatchStmt.setNull(2, 91);
		}
		else {
			this.fbatchStmt.setDate(2, issue_date);
		}
		if ( type == null ) {
			this.fbatchStmt.setNull(3, -6);
		}
		else {
			this.fbatchStmt.setShort(3, type);
		}
		if ( status == null ) {
			this.fbatchStmt.setNull(4, -6);
		}
		else {
			this.fbatchStmt.setShort(4, status);
		}
		if ( rbank == null ) {
			this.fbatchStmt.setNull(5, 4);
		}
		else {
			this.fbatchStmt.setInt(5, rbank);
		}
		if ( payment == null ) {
			this.fbatchStmt.setNull(6, -7);
		}
		else {
			this.fbatchStmt.setBoolean(6, payment);
		}
		if ( security_level == null ) {
			this.fbatchStmt.setNull(7, -6);
		}
		else {
			this.fbatchStmt.setShort(7, security_level);
		}

		this.fbatchStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.fbatchStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_detail_accountStmt;
	
	private void initInvoice_detail_accountStmt()
	throws SQLException {
		this.invoice_detail_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_detail_account ( invoice_detail, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_detail_accountStmt()
	throws SQLException {
		invoice_detail_accountStmt.close();
	}	
	/**
	 * Invoice_detail_account
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_detail_account(Integer invoice_detail, String account)
	throws SQLException {
	
		if ( invoice_detail == null ) {
			this.invoice_detail_accountStmt.setNull(1, 4);
		}
		else {
			this.invoice_detail_accountStmt.setInt(1, invoice_detail);
		}
		if ( account == null ) {
			this.invoice_detail_accountStmt.setNull(2, 1);
		}
		else {
			this.invoice_detail_accountStmt.setString(2, account);
		}

		this.invoice_detail_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_detail_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commission_typeStmt;
	
	private void initCommission_typeStmt()
	throws SQLException {
		this.commission_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_type ( name, rate) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommission_typeStmt()
	throws SQLException {
		commission_typeStmt.close();
	}	
	/**
	 * Commission_type
	 * @param name Descripcion del Tipo de Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_type(String name, Double rate)
	throws SQLException {
	
		if ( name == null ) {
			this.commission_typeStmt.setNull(1, 12);
		}
		else {
			this.commission_typeStmt.setString(1, name);
		}
		if ( rate == null ) {
			this.commission_typeStmt.setNull(2, 8);
		}
		else {
			this.commission_typeStmt.setDouble(2, rate);
		}

		this.commission_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commission_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement enterprise_certificate_detailStmt;
	
	private void initEnterprise_certificate_detailStmt()
	throws SQLException {
		this.enterprise_certificate_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_certificate_detail ( enterprise_certificate, contract, expire_date, suspension_cause) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEnterprise_certificate_detailStmt()
	throws SQLException {
		enterprise_certificate_detailStmt.close();
	}	
	/**
	 * Enterprise_certificate_detail
	 * @param enterprise_certificate Identificador unico del certificado de empresa
	 * @param contract Identificador unico del contrato de empleado
	 * @param expire_date Fecha de baja del empleado
	 * @param suspension_cause Causa de la suspension del empleado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_certificate_detail(Integer enterprise_certificate, Integer contract, Date expire_date, String suspension_cause)
	throws SQLException {
	
		if ( enterprise_certificate == null ) {
			this.enterprise_certificate_detailStmt.setNull(1, 4);
		}
		else {
			this.enterprise_certificate_detailStmt.setInt(1, enterprise_certificate);
		}
		if ( contract == null ) {
			this.enterprise_certificate_detailStmt.setNull(2, 4);
		}
		else {
			this.enterprise_certificate_detailStmt.setInt(2, contract);
		}
		if ( expire_date == null ) {
			this.enterprise_certificate_detailStmt.setNull(3, 91);
		}
		else {
			this.enterprise_certificate_detailStmt.setDate(3, expire_date);
		}
		if ( suspension_cause == null ) {
			this.enterprise_certificate_detailStmt.setNull(4, 12);
		}
		else {
			this.enterprise_certificate_detailStmt.setString(4, suspension_cause);
		}

		this.enterprise_certificate_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.enterprise_certificate_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement alarmStmt;
	
	private void initAlarmStmt()
	throws SQLException {
		this.alarmStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO alarm ( description, alarm_date, status, source, source_id, user_id, priority) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAlarmStmt()
	throws SQLException {
		alarmStmt.close();
	}	
	/**
	 * Alarm
	 * @param description Descripcion de la Alarma
	 * @param alarm_date Fecha y hora de ejecucion de la Alarma
	 * @param status Estado de la Alarma
	 * @param source Origen de la Alarma
	 * @param source_id Identificador del origen de la Alarma
	 * @param user_id Identificador del Usuario asociado a la Alarma
	 * @param priority Prioridad de la Alarma
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAlarm(InputStream description, Timestamp alarm_date, Short status, Short source, Integer source_id, Integer user_id, Short priority)
	throws SQLException {
	
		if ( description == null ) {
			this.alarmStmt.setNull(1, -1);
		}
		else {
			this.alarmStmt.setAsciiStream(1, description);
		}
		if ( alarm_date == null ) {
			this.alarmStmt.setNull(2, 93);
		}
		else {
			this.alarmStmt.setTimestamp(2, alarm_date);
		}
		if ( status == null ) {
			this.alarmStmt.setNull(3, -6);
		}
		else {
			this.alarmStmt.setShort(3, status);
		}
		if ( source == null ) {
			this.alarmStmt.setNull(4, -6);
		}
		else {
			this.alarmStmt.setShort(4, source);
		}
		if ( source_id == null ) {
			this.alarmStmt.setNull(5, 4);
		}
		else {
			this.alarmStmt.setInt(5, source_id);
		}
		if ( user_id == null ) {
			this.alarmStmt.setNull(6, 4);
		}
		else {
			this.alarmStmt.setInt(6, user_id);
		}
		if ( priority == null ) {
			this.alarmStmt.setNull(7, -6);
		}
		else {
			this.alarmStmt.setShort(7, priority);
		}

		this.alarmStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.alarmStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pay_methodStmt;
	
	private void initPay_methodStmt()
	throws SQLException {
		this.pay_methodStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pay_method ( name, type) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePay_methodStmt()
	throws SQLException {
		pay_methodStmt.close();
	}	
	/**
	 * Pay_method
	 * @param name Nombre de Forma de Pago
	 * @param type Tipo de Forma de Pago
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPay_method(String name, Short type)
	throws SQLException {
	
		if ( name == null ) {
			this.pay_methodStmt.setNull(1, 12);
		}
		else {
			this.pay_methodStmt.setString(1, name);
		}
		if ( type == null ) {
			this.pay_methodStmt.setNull(2, -6);
		}
		else {
			this.pay_methodStmt.setShort(2, type);
		}

		this.pay_methodStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pay_methodStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement ec_catalogueStmt;
	
	private void initEc_catalogueStmt()
	throws SQLException {
		this.ec_catalogueStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_catalogue ( catalogue, catalogue_img, catalogue_icon, type, visible) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEc_catalogueStmt()
	throws SQLException {
		ec_catalogueStmt.close();
	}	
	/**
	 * Ec_catalogue
	 * @param catalogue Identificador del Catalogo
	 * @param catalogue_img Imagen para el Catalogo
	 * @param catalogue_icon Icono del Catalogo
	 * @param type Tipo de Catalogo
	 * @param visible Indica si es visible en internet
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_catalogue(Integer catalogue, InputStream catalogue_img, InputStream catalogue_icon, Short type, Boolean visible)
	throws SQLException {
	
		if ( catalogue == null ) {
			this.ec_catalogueStmt.setNull(1, 4);
		}
		else {
			this.ec_catalogueStmt.setInt(1, catalogue);
		}
		if ( catalogue_img == null ) {
			this.ec_catalogueStmt.setNull(2, -4);
		}
		else {
			this.ec_catalogueStmt.setBinaryStream(2, catalogue_img);
		}
		if ( catalogue_icon == null ) {
			this.ec_catalogueStmt.setNull(3, -4);
		}
		else {
			this.ec_catalogueStmt.setBinaryStream(3, catalogue_icon);
		}
		if ( type == null ) {
			this.ec_catalogueStmt.setNull(4, -6);
		}
		else {
			this.ec_catalogueStmt.setShort(4, type);
		}
		if ( visible == null ) {
			this.ec_catalogueStmt.setNull(5, -7);
		}
		else {
			this.ec_catalogueStmt.setBoolean(5, visible);
		}

		this.ec_catalogueStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.ec_catalogueStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement workactivityStmt;
	
	private void initWorkactivityStmt()
	throws SQLException {
		this.workactivityStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workactivity ( description, workplace, enterpriseCCC, active) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWorkactivityStmt()
	throws SQLException {
		workactivityStmt.close();
	}	
	/**
	 * Workactivity
	 * @param description Descripcion de la Actividad
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterpriseCCC Cuenta de Cotizaciùn asociada a la Actividad
	 * @param active Indica si la Actividad esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkactivity(String description, Integer workplace, Integer enterpriseCCC, Boolean active)
	throws SQLException {
	
		if ( description == null ) {
			this.workactivityStmt.setNull(1, 12);
		}
		else {
			this.workactivityStmt.setString(1, description);
		}
		if ( workplace == null ) {
			this.workactivityStmt.setNull(2, 4);
		}
		else {
			this.workactivityStmt.setInt(2, workplace);
		}
		if ( enterpriseCCC == null ) {
			this.workactivityStmt.setNull(3, 4);
		}
		else {
			this.workactivityStmt.setInt(3, enterpriseCCC);
		}
		if ( active == null ) {
			this.workactivityStmt.setNull(4, -7);
		}
		else {
			this.workactivityStmt.setBoolean(4, active);
		}

		this.workactivityStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.workactivityStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement ec_configStmt;
	
	private void initEc_configStmt()
	throws SQLException {
		this.ec_configStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_config ( active, name, skin, header_img, series, commerce, show_login, price, tax_in_price, discount, bank_transfer, cash_on_delivery, visa, paypal, bank_draft, legal_note1, legal_note2, legal_note3, tariff, header_color, telephone, row_items, left_banner, right_banner, welcome_banner, ecommerce_status, shipping_costs, free_shipping, title_note1, title_note2, title_note3, email) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEc_configStmt()
	throws SQLException {
		ec_configStmt.close();
	}	
	/**
	 * Ec_config
	 * @param active Indica si es la configuracion activa
	 * @param name Nombre del Catalogo de internet
	 * @param skin Tipo de skin a utilizar
	 * @param header_img Imagen de cabecera
	 * @param series Serie de los presupuestos que se van a grabar
	 * @param commerce Indica si el ECommerce permite grabar un presupuesto
	 * @param show_login Indica la forma de autenticarse en el ECommerce
	 * @param price Indica la forma de mostrar los precios en el Catalogo
	 * @param tax_in_price Indica si los precios van a mostrarse con Impuestos incluidos
	 * @param discount Indica si adicionalmente se va a mostrar el precio original del Producto
	 * @param bank_transfer Forma de pago por transferencia bancaria
	 * @param cash_on_delivery Forma de pago por contrarreembolso
	 * @param visa Forma de pago con tarjeta
	 * @param paypal Forma de pago por paypal
	 * @param bank_draft Forma de pago por giro bancario
	 * @param legal_note1 Politica de privacidad
	 * @param legal_note2 Nota legal
	 * @param legal_note3 Proteccion de datos
	 * @param tariff Identificador de Tarifa para Ecommerce
	 * @param header_color Color del background del header
	 * @param telephone Telefono de contacto
	 * @param row_items Numero de articulos por fila
	 * @param left_banner Banner de la izquierda
	 * @param right_banner Banner de la derecha
	 * @param welcome_banner Banner de bienvenida
	 * @param ecommerce_status Estado del comercio electronico
	 * @param shipping_costs Gastos de envio
	 * @param free_shipping Gastos de envio gratis a partir de esta cantidad
	 * @param title_note1 
	 * @param title_note2 
	 * @param title_note3 
	 * @param email Email de contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_config(Boolean active, String name, Short skin, InputStream header_img, String series, Boolean commerce, Short show_login, Short price, Short tax_in_price, Short discount, Integer bank_transfer, Integer cash_on_delivery, Integer visa, Integer paypal, Integer bank_draft, InputStream legal_note1, InputStream legal_note2, InputStream legal_note3, Integer tariff, String header_color, String telephone, Short row_items, InputStream left_banner, InputStream right_banner, InputStream welcome_banner, Short ecommerce_status, Double shipping_costs, Double free_shipping, String title_note1, String title_note2, String title_note3, String email)
	throws SQLException {
	
		if ( active == null ) {
			this.ec_configStmt.setNull(1, -7);
		}
		else {
			this.ec_configStmt.setBoolean(1, active);
		}
		if ( name == null ) {
			this.ec_configStmt.setNull(2, 12);
		}
		else {
			this.ec_configStmt.setString(2, name);
		}
		if ( skin == null ) {
			this.ec_configStmt.setNull(3, -6);
		}
		else {
			this.ec_configStmt.setShort(3, skin);
		}
		if ( header_img == null ) {
			this.ec_configStmt.setNull(4, -4);
		}
		else {
			this.ec_configStmt.setBinaryStream(4, header_img);
		}
		if ( series == null ) {
			this.ec_configStmt.setNull(5, 1);
		}
		else {
			this.ec_configStmt.setString(5, series);
		}
		if ( commerce == null ) {
			this.ec_configStmt.setNull(6, -7);
		}
		else {
			this.ec_configStmt.setBoolean(6, commerce);
		}
		if ( show_login == null ) {
			this.ec_configStmt.setNull(7, -6);
		}
		else {
			this.ec_configStmt.setShort(7, show_login);
		}
		if ( price == null ) {
			this.ec_configStmt.setNull(8, -6);
		}
		else {
			this.ec_configStmt.setShort(8, price);
		}
		if ( tax_in_price == null ) {
			this.ec_configStmt.setNull(9, -6);
		}
		else {
			this.ec_configStmt.setShort(9, tax_in_price);
		}
		if ( discount == null ) {
			this.ec_configStmt.setNull(10, -6);
		}
		else {
			this.ec_configStmt.setShort(10, discount);
		}
		if ( bank_transfer == null ) {
			this.ec_configStmt.setNull(11, 4);
		}
		else {
			this.ec_configStmt.setInt(11, bank_transfer);
		}
		if ( cash_on_delivery == null ) {
			this.ec_configStmt.setNull(12, 4);
		}
		else {
			this.ec_configStmt.setInt(12, cash_on_delivery);
		}
		if ( visa == null ) {
			this.ec_configStmt.setNull(13, 4);
		}
		else {
			this.ec_configStmt.setInt(13, visa);
		}
		if ( paypal == null ) {
			this.ec_configStmt.setNull(14, 4);
		}
		else {
			this.ec_configStmt.setInt(14, paypal);
		}
		if ( bank_draft == null ) {
			this.ec_configStmt.setNull(15, 4);
		}
		else {
			this.ec_configStmt.setInt(15, bank_draft);
		}
		if ( legal_note1 == null ) {
			this.ec_configStmt.setNull(16, -1);
		}
		else {
			this.ec_configStmt.setAsciiStream(16, legal_note1);
		}
		if ( legal_note2 == null ) {
			this.ec_configStmt.setNull(17, -1);
		}
		else {
			this.ec_configStmt.setAsciiStream(17, legal_note2);
		}
		if ( legal_note3 == null ) {
			this.ec_configStmt.setNull(18, -1);
		}
		else {
			this.ec_configStmt.setAsciiStream(18, legal_note3);
		}
		if ( tariff == null ) {
			this.ec_configStmt.setNull(19, 4);
		}
		else {
			this.ec_configStmt.setInt(19, tariff);
		}
		if ( header_color == null ) {
			this.ec_configStmt.setNull(20, 12);
		}
		else {
			this.ec_configStmt.setString(20, header_color);
		}
		if ( telephone == null ) {
			this.ec_configStmt.setNull(21, 12);
		}
		else {
			this.ec_configStmt.setString(21, telephone);
		}
		if ( row_items == null ) {
			this.ec_configStmt.setNull(22, -6);
		}
		else {
			this.ec_configStmt.setShort(22, row_items);
		}
		if ( left_banner == null ) {
			this.ec_configStmt.setNull(23, -4);
		}
		else {
			this.ec_configStmt.setBinaryStream(23, left_banner);
		}
		if ( right_banner == null ) {
			this.ec_configStmt.setNull(24, -4);
		}
		else {
			this.ec_configStmt.setBinaryStream(24, right_banner);
		}
		if ( welcome_banner == null ) {
			this.ec_configStmt.setNull(25, -4);
		}
		else {
			this.ec_configStmt.setBinaryStream(25, welcome_banner);
		}
		if ( ecommerce_status == null ) {
			this.ec_configStmt.setNull(26, -6);
		}
		else {
			this.ec_configStmt.setShort(26, ecommerce_status);
		}
		if ( shipping_costs == null ) {
			this.ec_configStmt.setNull(27, 8);
		}
		else {
			this.ec_configStmt.setDouble(27, shipping_costs);
		}
		if ( free_shipping == null ) {
			this.ec_configStmt.setNull(28, 8);
		}
		else {
			this.ec_configStmt.setDouble(28, free_shipping);
		}
		if ( title_note1 == null ) {
			this.ec_configStmt.setNull(29, 12);
		}
		else {
			this.ec_configStmt.setString(29, title_note1);
		}
		if ( title_note2 == null ) {
			this.ec_configStmt.setNull(30, 12);
		}
		else {
			this.ec_configStmt.setString(30, title_note2);
		}
		if ( title_note3 == null ) {
			this.ec_configStmt.setNull(31, 12);
		}
		else {
			this.ec_configStmt.setString(31, title_note3);
		}
		if ( email == null ) {
			this.ec_configStmt.setNull(32, 12);
		}
		else {
			this.ec_configStmt.setString(32, email);
		}

		this.ec_configStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.ec_configStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_budgetStmt;
	
	private void initAccount_budgetStmt()
	throws SQLException {
		this.account_budgetStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_budget ( account_period, account, security_level) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_budgetStmt()
	throws SQLException {
		account_budgetStmt.close();
	}	
	/**
	 * Account_budget
	 * @param account_period Ejercicio Contable del Presupuesto
	 * @param account Cuenta Contable del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_budget(String account_period, String account, Short security_level)
	throws SQLException {
	
		if ( account_period == null ) {
			this.account_budgetStmt.setNull(1, 1);
		}
		else {
			this.account_budgetStmt.setString(1, account_period);
		}
		if ( account == null ) {
			this.account_budgetStmt.setNull(2, 1);
		}
		else {
			this.account_budgetStmt.setString(2, account);
		}
		if ( security_level == null ) {
			this.account_budgetStmt.setNull(3, -6);
		}
		else {
			this.account_budgetStmt.setShort(3, security_level);
		}

		this.account_budgetStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_budgetStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_observationStmt;
	
	private void initCourse_observationStmt()
	throws SQLException {
		this.course_observationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_observation ( course, observation) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_observationStmt()
	throws SQLException {
		course_observationStmt.close();
	}	
	/**
	 * Course_observation
	 * @param course Identificador de Curso
	 * @param observation Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_observation(Integer course, String observation)
	throws SQLException {
	
		if ( course == null ) {
			this.course_observationStmt.setNull(1, 4);
		}
		else {
			this.course_observationStmt.setInt(1, course);
		}
		if ( observation == null ) {
			this.course_observationStmt.setNull(2, 12);
		}
		else {
			this.course_observationStmt.setString(2, observation);
		}

		this.course_observationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_observationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement survey_responseStmt;
	
	private void initSurvey_responseStmt()
	throws SQLException {
		this.survey_responseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_response ( creationDate, response_date, survey, target, user, campaign_action) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSurvey_responseStmt()
	throws SQLException {
		survey_responseStmt.close();
	}	
	/**
	 * Survey_response
	 * @param creationDate Fecha de la creacion en el sistema de la Respuesta del Cuestionario
	 * @param response_date Fecha de la Respuesta del Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param target Identificador del Cliente Potencial
	 * @param user Identificador del Usuario
	 * @param campaign_action Identificador de la Accion de la Campaùa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_response(Timestamp creationDate, Timestamp response_date, Integer survey, Integer target, Integer user, Integer campaign_action)
	throws SQLException {
	
		if ( creationDate == null ) {
			this.survey_responseStmt.setNull(1, 93);
		}
		else {
			this.survey_responseStmt.setTimestamp(1, creationDate);
		}
		if ( response_date == null ) {
			this.survey_responseStmt.setNull(2, 93);
		}
		else {
			this.survey_responseStmt.setTimestamp(2, response_date);
		}
		if ( survey == null ) {
			this.survey_responseStmt.setNull(3, 4);
		}
		else {
			this.survey_responseStmt.setInt(3, survey);
		}
		if ( target == null ) {
			this.survey_responseStmt.setNull(4, 4);
		}
		else {
			this.survey_responseStmt.setInt(4, target);
		}
		if ( user == null ) {
			this.survey_responseStmt.setNull(5, 4);
		}
		else {
			this.survey_responseStmt.setInt(5, user);
		}
		if ( campaign_action == null ) {
			this.survey_responseStmt.setNull(6, 4);
		}
		else {
			this.survey_responseStmt.setInt(6, campaign_action);
		}

		this.survey_responseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.survey_responseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement evaluation_observationStmt;
	
	private void initEvaluation_observationStmt()
	throws SQLException {
		this.evaluation_observationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO evaluation_observation ( alumn, evaluation, comments) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEvaluation_observationStmt()
	throws SQLException {
		evaluation_observationStmt.close();
	}	
	/**
	 * Evaluation_observation
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de Evaluacion
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEvaluation_observation(Integer alumn, Short evaluation, InputStream comments)
	throws SQLException {
	
		if ( alumn == null ) {
			this.evaluation_observationStmt.setNull(1, 4);
		}
		else {
			this.evaluation_observationStmt.setInt(1, alumn);
		}
		if ( evaluation == null ) {
			this.evaluation_observationStmt.setNull(2, -6);
		}
		else {
			this.evaluation_observationStmt.setShort(2, evaluation);
		}
		if ( comments == null ) {
			this.evaluation_observationStmt.setNull(3, -1);
		}
		else {
			this.evaluation_observationStmt.setAsciiStream(3, comments);
		}

		this.evaluation_observationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.evaluation_observationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement personStmt;
	
	private void initPersonStmt()
	throws SQLException {
		this.personStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO person ( registry, birth_date, gender, marital_status, social_security_num) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePersonStmt()
	throws SQLException {
		personStmt.close();
	}	
	/**
	 * Person
	 * @param registry Registro de la Persona
	 * @param birth_date Fecha de nacimiento de la Persona
	 * @param gender Sexo de la Persona
	 * @param marital_status Estado civil de la Persona
	 * @param social_security_num Numero de Seguridad Social de la Persona
	 * @throws SQLException
	*/
	protected void insertPerson(Integer registry, Date birth_date, Short gender, Short marital_status, String social_security_num)
	throws SQLException {
	
		if ( registry == null ) {
			this.personStmt.setNull(1, 4);
		}
		else {
			this.personStmt.setInt(1, registry);
		}
		if ( birth_date == null ) {
			this.personStmt.setNull(2, 91);
		}
		else {
			this.personStmt.setDate(2, birth_date);
		}
		if ( gender == null ) {
			this.personStmt.setNull(3, -6);
		}
		else {
			this.personStmt.setShort(3, gender);
		}
		if ( marital_status == null ) {
			this.personStmt.setNull(4, -6);
		}
		else {
			this.personStmt.setShort(4, marital_status);
		}
		if ( social_security_num == null ) {
			this.personStmt.setNull(5, 12);
		}
		else {
			this.personStmt.setString(5, social_security_num);
		}

		this.personStmt.executeUpdate();
		
	}
	
	private PreparedStatement contract_paymentStmt;
	
	private void initContract_paymentStmt()
	throws SQLException {
		this.contract_paymentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_payment ( type, contract, description, function, start_date, end_date) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_paymentStmt()
	throws SQLException {
		contract_paymentStmt.close();
	}	
	/**
	 * Contract_payment
	 * @param type Tipo de PercepciÛn Salarial
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param function FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_payment(Short type, Integer contract, String description, String function, Date start_date, Date end_date)
	throws SQLException {
	
		if ( type == null ) {
			this.contract_paymentStmt.setNull(1, -6);
		}
		else {
			this.contract_paymentStmt.setShort(1, type);
		}
		if ( contract == null ) {
			this.contract_paymentStmt.setNull(2, 4);
		}
		else {
			this.contract_paymentStmt.setInt(2, contract);
		}
		if ( description == null ) {
			this.contract_paymentStmt.setNull(3, 12);
		}
		else {
			this.contract_paymentStmt.setString(3, description);
		}
		if ( function == null ) {
			this.contract_paymentStmt.setNull(4, 12);
		}
		else {
			this.contract_paymentStmt.setString(4, function);
		}
		if ( start_date == null ) {
			this.contract_paymentStmt.setNull(5, 91);
		}
		else {
			this.contract_paymentStmt.setDate(5, start_date);
		}
		if ( end_date == null ) {
			this.contract_paymentStmt.setNull(6, 91);
		}
		else {
			this.contract_paymentStmt.setDate(6, end_date);
		}

		this.contract_paymentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_paymentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entryStmt;
	
	private void initAccount_entryStmt()
	throws SQLException {
		this.account_entryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry ( account_period, entry_date, entry_type, journal, security_level, comments) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entryStmt()
	throws SQLException {
		account_entryStmt.close();
	}	
	/**
	 * Account_entry
	 * @param account_period Ejercicio Contable del Asiento
	 * @param entry_date Fecha del Asiento
	 * @param entry_type Tipo de Asiento
	 * @param journal Numero de diario del Asiento
	 * @param security_level Nivel de seguridad del Asiento
	 * @param comments Comentarios del Asiento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry(String account_period, Date entry_date, Short entry_type, Integer journal, Short security_level, InputStream comments)
	throws SQLException {
	
		if ( account_period == null ) {
			this.account_entryStmt.setNull(1, 1);
		}
		else {
			this.account_entryStmt.setString(1, account_period);
		}
		if ( entry_date == null ) {
			this.account_entryStmt.setNull(2, 91);
		}
		else {
			this.account_entryStmt.setDate(2, entry_date);
		}
		if ( entry_type == null ) {
			this.account_entryStmt.setNull(3, -6);
		}
		else {
			this.account_entryStmt.setShort(3, entry_type);
		}
		if ( journal == null ) {
			this.account_entryStmt.setNull(4, 4);
		}
		else {
			this.account_entryStmt.setInt(4, journal);
		}
		if ( security_level == null ) {
			this.account_entryStmt.setNull(5, -6);
		}
		else {
			this.account_entryStmt.setShort(5, security_level);
		}
		if ( comments == null ) {
			this.account_entryStmt.setNull(6, -1);
		}
		else {
			this.account_entryStmt.setAsciiStream(6, comments);
		}

		this.account_entryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement geotreeStmt;
	
	private void initGeotreeStmt()
	throws SQLException {
		this.geotreeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO geotree ( parent, child) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeGeotreeStmt()
	throws SQLException {
		geotreeStmt.close();
	}	
	/**
	 * Geotree
	 * @param parent Identificador de la Zona Geografica Padre
	 * @param child Identificador de la Zona Geografica Hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertGeotree(Integer parent, Integer child)
	throws SQLException {
	
		if ( parent == null ) {
			this.geotreeStmt.setNull(1, 4);
		}
		else {
			this.geotreeStmt.setInt(1, parent);
		}
		if ( child == null ) {
			this.geotreeStmt.setNull(2, 4);
		}
		else {
			this.geotreeStmt.setInt(2, child);
		}

		this.geotreeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.geotreeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement sales_purchaseStmt;
	
	private void initSales_purchaseStmt()
	throws SQLException {
		this.sales_purchaseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales_purchase ( sales_detail, purchase_detail) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSales_purchaseStmt()
	throws SQLException {
		sales_purchaseStmt.close();
	}	
	/**
	 * Sales_purchase
	 * @param sales_detail Identificador del Detalle del Pedido de Venta
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSales_purchase(Integer sales_detail, Integer purchase_detail)
	throws SQLException {
	
		if ( sales_detail == null ) {
			this.sales_purchaseStmt.setNull(1, 4);
		}
		else {
			this.sales_purchaseStmt.setInt(1, sales_detail);
		}
		if ( purchase_detail == null ) {
			this.sales_purchaseStmt.setNull(2, 4);
		}
		else {
			this.sales_purchaseStmt.setInt(2, purchase_detail);
		}

		this.sales_purchaseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.sales_purchaseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement item_supplierStmt;
	
	private void initItem_supplierStmt()
	throws SQLException {
		this.item_supplierStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_supplier ( item, supplier, code, priority) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItem_supplierStmt()
	throws SQLException {
		item_supplierStmt.close();
	}	
	/**
	 * Item_supplier
	 * @param item Identificador de Articulo
	 * @param supplier Identificador de Proveedor
	 * @param code Codigo del Producto en el Proveedor
	 * @param priority Prioridad del Proveedor
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_supplier(Integer item, Integer supplier, String code, Short priority)
	throws SQLException {
	
		if ( item == null ) {
			this.item_supplierStmt.setNull(1, 4);
		}
		else {
			this.item_supplierStmt.setInt(1, item);
		}
		if ( supplier == null ) {
			this.item_supplierStmt.setNull(2, 4);
		}
		else {
			this.item_supplierStmt.setInt(2, supplier);
		}
		if ( code == null ) {
			this.item_supplierStmt.setNull(3, 12);
		}
		else {
			this.item_supplierStmt.setString(3, code);
		}
		if ( priority == null ) {
			this.item_supplierStmt.setNull(4, -6);
		}
		else {
			this.item_supplierStmt.setShort(4, priority);
		}

		this.item_supplierStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.item_supplierStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement customer_accountStmt;
	
	private void initCustomer_accountStmt()
	throws SQLException {
		this.customer_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_account ( customer, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCustomer_accountStmt()
	throws SQLException {
		customer_accountStmt.close();
	}	
	/**
	 * Customer_account
	 * @param customer Identificador del Cliente
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_account(Integer customer, String account)
	throws SQLException {
	
		if ( customer == null ) {
			this.customer_accountStmt.setNull(1, 4);
		}
		else {
			this.customer_accountStmt.setInt(1, customer);
		}
		if ( account == null ) {
			this.customer_accountStmt.setNull(2, 1);
		}
		else {
			this.customer_accountStmt.setString(2, account);
		}

		this.customer_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.customer_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement dossierStmt;
	
	private void initDossierStmt()
	throws SQLException {
		this.dossierStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO dossier ( customer, dossier_type, number, location, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDossierStmt()
	throws SQLException {
		dossierStmt.close();
	}	
	/**
	 * Dossier
	 * @param customer Identificador del Cliente
	 * @param dossier_type Tipo de Expediente
	 * @param number Numero de Expediente
	 * @param location Ubicacion del Expediente
	 * @param status Estado del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDossier(Integer customer, Integer dossier_type, String number, String location, Short status)
	throws SQLException {
	
		if ( customer == null ) {
			this.dossierStmt.setNull(1, 4);
		}
		else {
			this.dossierStmt.setInt(1, customer);
		}
		if ( dossier_type == null ) {
			this.dossierStmt.setNull(2, 4);
		}
		else {
			this.dossierStmt.setInt(2, dossier_type);
		}
		if ( number == null ) {
			this.dossierStmt.setNull(3, 12);
		}
		else {
			this.dossierStmt.setString(3, number);
		}
		if ( location == null ) {
			this.dossierStmt.setNull(4, 12);
		}
		else {
			this.dossierStmt.setString(4, location);
		}
		if ( status == null ) {
			this.dossierStmt.setNull(5, -6);
		}
		else {
			this.dossierStmt.setShort(5, status);
		}

		this.dossierStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.dossierStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement survey_questionStmt;
	
	private void initSurvey_questionStmt()
	throws SQLException {
		this.survey_questionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_question ( survey, question, position) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSurvey_questionStmt()
	throws SQLException {
		survey_questionStmt.close();
	}	
	/**
	 * Survey_question
	 * @param survey Identificador del Cuestionario
	 * @param question Identificador de la Pregunta
	 * @param position Posicion de la Pregunta dentro del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_question(Integer survey, Integer question, Integer position)
	throws SQLException {
	
		if ( survey == null ) {
			this.survey_questionStmt.setNull(1, 4);
		}
		else {
			this.survey_questionStmt.setInt(1, survey);
		}
		if ( question == null ) {
			this.survey_questionStmt.setNull(2, 4);
		}
		else {
			this.survey_questionStmt.setInt(2, question);
		}
		if ( position == null ) {
			this.survey_questionStmt.setNull(3, 4);
		}
		else {
			this.survey_questionStmt.setInt(3, position);
		}

		this.survey_questionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.survey_questionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_evaluate_summaryStmt;
	
	private void initCv_evaluate_summaryStmt()
	throws SQLException {
		this.cv_evaluate_summaryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate_summary ( strengths, weaknesses, profile, comments, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_evaluate_summaryStmt()
	throws SQLException {
		cv_evaluate_summaryStmt.close();
	}	
	/**
	 * Cv_evaluate_summary
	 * @param strengths Fortalezas
	 * @param weaknesses Debilidades
	 * @param profile Perfil
	 * @param comments Comentarios
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_evaluate_summary(String strengths, String weaknesses, Short profile, String comments, Integer curriculum)
	throws SQLException {
	
		if ( strengths == null ) {
			this.cv_evaluate_summaryStmt.setNull(1, 12);
		}
		else {
			this.cv_evaluate_summaryStmt.setString(1, strengths);
		}
		if ( weaknesses == null ) {
			this.cv_evaluate_summaryStmt.setNull(2, 12);
		}
		else {
			this.cv_evaluate_summaryStmt.setString(2, weaknesses);
		}
		if ( profile == null ) {
			this.cv_evaluate_summaryStmt.setNull(3, -6);
		}
		else {
			this.cv_evaluate_summaryStmt.setShort(3, profile);
		}
		if ( comments == null ) {
			this.cv_evaluate_summaryStmt.setNull(4, 12);
		}
		else {
			this.cv_evaluate_summaryStmt.setString(4, comments);
		}
		if ( curriculum == null ) {
			this.cv_evaluate_summaryStmt.setNull(5, 4);
		}
		else {
			this.cv_evaluate_summaryStmt.setInt(5, curriculum);
		}

		this.cv_evaluate_summaryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_evaluate_summaryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement courseStmt;
	
	private void initCourseStmt()
	throws SQLException {
		this.courseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course ( code, description, start_date, end_date, academic_year, subject, level, workplace, alumn_limit, status, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourseStmt()
	throws SQLException {
		courseStmt.close();
	}	
	/**
	 * Course
	 * @param code Alias del Curso
	 * @param description Descripcion del Curso
	 * @param start_date Fecha inicio del Curso
	 * @param end_date Fecha fin del Curso
	 * @param academic_year Aùo Academico del Curso
	 * @param subject Materia del Curso
	 * @param level Nivel del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @param alumn_limit Limite de Alumnos del Curso
	 * @param status Estado del Curso
	 * @param comments Comentarios sobre el Curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse(String code, String description, Date start_date, Date end_date, Integer academic_year, Integer subject, Integer level, Integer workplace, Integer alumn_limit, Short status, String comments)
	throws SQLException {
	
		if ( code == null ) {
			this.courseStmt.setNull(1, 12);
		}
		else {
			this.courseStmt.setString(1, code);
		}
		if ( description == null ) {
			this.courseStmt.setNull(2, 12);
		}
		else {
			this.courseStmt.setString(2, description);
		}
		if ( start_date == null ) {
			this.courseStmt.setNull(3, 91);
		}
		else {
			this.courseStmt.setDate(3, start_date);
		}
		if ( end_date == null ) {
			this.courseStmt.setNull(4, 91);
		}
		else {
			this.courseStmt.setDate(4, end_date);
		}
		if ( academic_year == null ) {
			this.courseStmt.setNull(5, 4);
		}
		else {
			this.courseStmt.setInt(5, academic_year);
		}
		if ( subject == null ) {
			this.courseStmt.setNull(6, 4);
		}
		else {
			this.courseStmt.setInt(6, subject);
		}
		if ( level == null ) {
			this.courseStmt.setNull(7, 4);
		}
		else {
			this.courseStmt.setInt(7, level);
		}
		if ( workplace == null ) {
			this.courseStmt.setNull(8, 4);
		}
		else {
			this.courseStmt.setInt(8, workplace);
		}
		if ( alumn_limit == null ) {
			this.courseStmt.setNull(9, 5);
		}
		else {
			this.courseStmt.setInt(9, alumn_limit);
		}
		if ( status == null ) {
			this.courseStmt.setNull(10, -6);
		}
		else {
			this.courseStmt.setShort(10, status);
		}
		if ( comments == null ) {
			this.courseStmt.setNull(11, 12);
		}
		else {
			this.courseStmt.setString(11, comments);
		}

		this.courseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.courseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement stockStmt;
	
	private void initStockStmt()
	throws SQLException {
		this.stockStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO stock ( warehouse, item, quantity) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeStockStmt()
	throws SQLException {
		stockStmt.close();
	}	
	/**
	 * Stock
	 * @param warehouse Identificador del Almacen
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertStock(Integer warehouse, Integer item, Double quantity)
	throws SQLException {
	
		if ( warehouse == null ) {
			this.stockStmt.setNull(1, 4);
		}
		else {
			this.stockStmt.setInt(1, warehouse);
		}
		if ( item == null ) {
			this.stockStmt.setNull(2, 4);
		}
		else {
			this.stockStmt.setInt(2, item);
		}
		if ( quantity == null ) {
			this.stockStmt.setNull(3, 8);
		}
		else {
			this.stockStmt.setDouble(3, quantity);
		}

		this.stockStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.stockStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rbankStmt;
	
	private void initRbankStmt()
	throws SQLException {
		this.rbankStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rbank ( registry, bank, bank_account, sufix) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRbankStmt()
	throws SQLException {
		rbankStmt.close();
	}	
	/**
	 * Rbank
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de Cuenta Bancaria de la Persona o Empresa
	 * @param sufix Sufijo de Cuenta Bancaria para Remesas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRbank(Integer registry, Integer bank, String bank_account, String sufix)
	throws SQLException {
	
		if ( registry == null ) {
			this.rbankStmt.setNull(1, 4);
		}
		else {
			this.rbankStmt.setInt(1, registry);
		}
		if ( bank == null ) {
			this.rbankStmt.setNull(2, 4);
		}
		else {
			this.rbankStmt.setInt(2, bank);
		}
		if ( bank_account == null ) {
			this.rbankStmt.setNull(3, 1);
		}
		else {
			this.rbankStmt.setString(3, bank_account);
		}
		if ( sufix == null ) {
			this.rbankStmt.setNull(4, 1);
		}
		else {
			this.rbankStmt.setString(4, sufix);
		}

		this.rbankStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rbankStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rpaymethodStmt;
	
	private void initRpaymethodStmt()
	throws SQLException {
		this.rpaymethodStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rpaymethod ( registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRpaymethodStmt()
	throws SQLException {
		rpaymethodStmt.close();
	}	
	/**
	 * Rpaymethod
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param pay_method Identificador de la Forma de Pago
	 * @param rbank Identificador de la Entidad Bancaria
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRpaymethod(Integer registry, Integer pay_method, Integer rbank, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days)
	throws SQLException {
	
		if ( registry == null ) {
			this.rpaymethodStmt.setNull(1, 4);
		}
		else {
			this.rpaymethodStmt.setInt(1, registry);
		}
		if ( pay_method == null ) {
			this.rpaymethodStmt.setNull(2, 4);
		}
		else {
			this.rpaymethodStmt.setInt(2, pay_method);
		}
		if ( rbank == null ) {
			this.rpaymethodStmt.setNull(3, 4);
		}
		else {
			this.rpaymethodStmt.setInt(3, rbank);
		}
		if ( number_of_pymnts == null ) {
			this.rpaymethodStmt.setNull(4, 5);
		}
		else {
			this.rpaymethodStmt.setInt(4, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.rpaymethodStmt.setNull(5, 5);
		}
		else {
			this.rpaymethodStmt.setInt(5, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.rpaymethodStmt.setNull(6, 5);
		}
		else {
			this.rpaymethodStmt.setInt(6, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.rpaymethodStmt.setNull(7, 12);
		}
		else {
			this.rpaymethodStmt.setString(7, pymnt_days);
		}

		this.rpaymethodStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rpaymethodStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement salesStmt;
	
	private void initSalesStmt()
	throws SQLException {
		this.salesStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales ( customer, series, number, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, pos, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSalesStmt()
	throws SQLException {
		salesStmt.close();
	}	
	/**
	 * Sales
	 * @param customer Identificador del Cliente
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param shipping_address Identificador de la Direccion de envio del Pedido
	 * @param seller Identificador del Agente Comercial
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param pos Identificador del Centro de Venta que realizo el Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSales(Integer customer, String series, Integer number, Integer shipping_address, Integer seller, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, Integer pos, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
	
		if ( customer == null ) {
			this.salesStmt.setNull(1, 4);
		}
		else {
			this.salesStmt.setInt(1, customer);
		}
		if ( series == null ) {
			this.salesStmt.setNull(2, 1);
		}
		else {
			this.salesStmt.setString(2, series);
		}
		if ( number == null ) {
			this.salesStmt.setNull(3, 4);
		}
		else {
			this.salesStmt.setInt(3, number);
		}
		if ( shipping_address == null ) {
			this.salesStmt.setNull(4, 4);
		}
		else {
			this.salesStmt.setInt(4, shipping_address);
		}
		if ( seller == null ) {
			this.salesStmt.setNull(5, 4);
		}
		else {
			this.salesStmt.setInt(5, seller);
		}
		if ( discount_expr == null ) {
			this.salesStmt.setNull(6, 12);
		}
		else {
			this.salesStmt.setString(6, discount_expr);
		}
		if ( issue_date == null ) {
			this.salesStmt.setNull(7, 91);
		}
		else {
			this.salesStmt.setDate(7, issue_date);
		}
		if ( pay_method == null ) {
			this.salesStmt.setNull(8, 4);
		}
		else {
			this.salesStmt.setInt(8, pay_method);
		}
		if ( document_type == null ) {
			this.salesStmt.setNull(9, -6);
		}
		else {
			this.salesStmt.setShort(9, document_type);
		}
		if ( security_level == null ) {
			this.salesStmt.setNull(10, -6);
		}
		else {
			this.salesStmt.setShort(10, security_level);
		}
		if ( status == null ) {
			this.salesStmt.setNull(11, -6);
		}
		else {
			this.salesStmt.setShort(11, status);
		}
		if ( pos == null ) {
			this.salesStmt.setNull(12, 4);
		}
		else {
			this.salesStmt.setInt(12, pos);
		}
		if ( workplace == null ) {
			this.salesStmt.setNull(13, 4);
		}
		else {
			this.salesStmt.setInt(13, workplace);
		}
		if ( scope == null ) {
			this.salesStmt.setNull(14, 4);
		}
		else {
			this.salesStmt.setInt(14, scope);
		}
		if ( number_of_pymnts == null ) {
			this.salesStmt.setNull(15, 5);
		}
		else {
			this.salesStmt.setInt(15, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.salesStmt.setNull(16, 5);
		}
		else {
			this.salesStmt.setInt(16, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.salesStmt.setNull(17, 5);
		}
		else {
			this.salesStmt.setInt(17, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.salesStmt.setNull(18, 12);
		}
		else {
			this.salesStmt.setString(18, pymnt_days);
		}
		if ( bank == null ) {
			this.salesStmt.setNull(19, 4);
		}
		else {
			this.salesStmt.setInt(19, bank);
		}
		if ( bank_account == null ) {
			this.salesStmt.setNull(20, 12);
		}
		else {
			this.salesStmt.setString(20, bank_account);
		}

		this.salesStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.salesStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement incomeStmt;
	
	private void initIncomeStmt()
	throws SQLException {
		this.incomeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO income ( series, number, supplier, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeIncomeStmt()
	throws SQLException {
		incomeStmt.close();
	}	
	/**
	 * Income
	 * @param series Serie del Albaran
	 * @param number Numero del Albaran
	 * @param supplier Identificador del Proveedor
	 * @param address Identificador de la Direccion del Proveedor
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncome(String series, Integer number, Integer supplier, Integer address, Date issue_time, Integer pay_method, Short security_level, Short status, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
	
		if ( series == null ) {
			this.incomeStmt.setNull(1, 1);
		}
		else {
			this.incomeStmt.setString(1, series);
		}
		if ( number == null ) {
			this.incomeStmt.setNull(2, 4);
		}
		else {
			this.incomeStmt.setInt(2, number);
		}
		if ( supplier == null ) {
			this.incomeStmt.setNull(3, 4);
		}
		else {
			this.incomeStmt.setInt(3, supplier);
		}
		if ( address == null ) {
			this.incomeStmt.setNull(4, 4);
		}
		else {
			this.incomeStmt.setInt(4, address);
		}
		if ( issue_time == null ) {
			this.incomeStmt.setNull(5, 91);
		}
		else {
			this.incomeStmt.setDate(5, issue_time);
		}
		if ( pay_method == null ) {
			this.incomeStmt.setNull(6, 4);
		}
		else {
			this.incomeStmt.setInt(6, pay_method);
		}
		if ( security_level == null ) {
			this.incomeStmt.setNull(7, -6);
		}
		else {
			this.incomeStmt.setShort(7, security_level);
		}
		if ( status == null ) {
			this.incomeStmt.setNull(8, -6);
		}
		else {
			this.incomeStmt.setShort(8, status);
		}
		if ( workplace == null ) {
			this.incomeStmt.setNull(9, 4);
		}
		else {
			this.incomeStmt.setInt(9, workplace);
		}
		if ( scope == null ) {
			this.incomeStmt.setNull(10, 4);
		}
		else {
			this.incomeStmt.setInt(10, scope);
		}
		if ( number_of_pymnts == null ) {
			this.incomeStmt.setNull(11, 5);
		}
		else {
			this.incomeStmt.setInt(11, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.incomeStmt.setNull(12, 5);
		}
		else {
			this.incomeStmt.setInt(12, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.incomeStmt.setNull(13, 5);
		}
		else {
			this.incomeStmt.setInt(13, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.incomeStmt.setNull(14, 12);
		}
		else {
			this.incomeStmt.setString(14, pymnt_days);
		}
		if ( bank == null ) {
			this.incomeStmt.setNull(15, 4);
		}
		else {
			this.incomeStmt.setInt(15, bank);
		}
		if ( bank_account == null ) {
			this.incomeStmt.setNull(16, 12);
		}
		else {
			this.incomeStmt.setString(16, bank_account);
		}

		this.incomeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.incomeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement enterprise_activityStmt;
	
	private void initEnterprise_activityStmt()
	throws SQLException {
		this.enterprise_activityStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_activity ( description, enterprise, cnae, type) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEnterprise_activityStmt()
	throws SQLException {
		enterprise_activityStmt.close();
	}	
	/**
	 * Enterprise_activity
	 * @param description Descripcion de la Actividad de la Empresa
	 * @param enterprise Identificador de la Empresa
	 * @param cnae Identificador del CNAE
	 * @param type Tipo de Actividad de la Empresa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_activity(String description, Integer enterprise, Integer cnae, Short type)
	throws SQLException {
	
		if ( description == null ) {
			this.enterprise_activityStmt.setNull(1, 12);
		}
		else {
			this.enterprise_activityStmt.setString(1, description);
		}
		if ( enterprise == null ) {
			this.enterprise_activityStmt.setNull(2, 4);
		}
		else {
			this.enterprise_activityStmt.setInt(2, enterprise);
		}
		if ( cnae == null ) {
			this.enterprise_activityStmt.setNull(3, 4);
		}
		else {
			this.enterprise_activityStmt.setInt(3, cnae);
		}
		if ( type == null ) {
			this.enterprise_activityStmt.setNull(4, -6);
		}
		else {
			this.enterprise_activityStmt.setShort(4, type);
		}

		this.enterprise_activityStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.enterprise_activityStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement web_info_page_resourceStmt;
	
	private void initWeb_info_page_resourceStmt()
	throws SQLException {
		this.web_info_page_resourceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page_resource ( web_info_page, rattach, content) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWeb_info_page_resourceStmt()
	throws SQLException {
		web_info_page_resourceStmt.close();
	}	
	/**
	 * Web_info_page_resource
	 * @param web_info_page Codigo de la Pagina
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param content Texto del Recurso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_page_resource(Integer web_info_page, Integer rattach, String content)
	throws SQLException {
	
		if ( web_info_page == null ) {
			this.web_info_page_resourceStmt.setNull(1, 4);
		}
		else {
			this.web_info_page_resourceStmt.setInt(1, web_info_page);
		}
		if ( rattach == null ) {
			this.web_info_page_resourceStmt.setNull(2, 4);
		}
		else {
			this.web_info_page_resourceStmt.setInt(2, rattach);
		}
		if ( content == null ) {
			this.web_info_page_resourceStmt.setNull(3, 12);
		}
		else {
			this.web_info_page_resourceStmt.setString(3, content);
		}

		this.web_info_page_resourceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.web_info_page_resourceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement fbatch_detailStmt;
	
	private void initFbatch_detailStmt()
	throws SQLException {
		this.fbatch_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fbatch_detail ( fbatch, finance, amount, status) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFbatch_detailStmt()
	throws SQLException {
		fbatch_detailStmt.close();
	}	
	/**
	 * Fbatch_detail
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @param amount Importe del Detalle de la Remesa
	 * @param status Estado del Detalle de la Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFbatch_detail(Integer fbatch, Integer finance, Double amount, Short status)
	throws SQLException {
	
		if ( fbatch == null ) {
			this.fbatch_detailStmt.setNull(1, 4);
		}
		else {
			this.fbatch_detailStmt.setInt(1, fbatch);
		}
		if ( finance == null ) {
			this.fbatch_detailStmt.setNull(2, 4);
		}
		else {
			this.fbatch_detailStmt.setInt(2, finance);
		}
		if ( amount == null ) {
			this.fbatch_detailStmt.setNull(3, 8);
		}
		else {
			this.fbatch_detailStmt.setDouble(3, amount);
		}
		if ( status == null ) {
			this.fbatch_detailStmt.setNull(4, -6);
		}
		else {
			this.fbatch_detailStmt.setShort(4, status);
		}

		this.fbatch_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.fbatch_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement calendar_holidayStmt;
	
	private void initCalendar_holidayStmt()
	throws SQLException {
		this.calendar_holidayStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar_holiday ( calendar, description, date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCalendar_holidayStmt()
	throws SQLException {
		calendar_holidayStmt.close();
	}	
	/**
	 * Calendar_holiday
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Festivo
	 * @param date Fecha del festivo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCalendar_holiday(Integer calendar, String description, Date date)
	throws SQLException {
	
		if ( calendar == null ) {
			this.calendar_holidayStmt.setNull(1, 4);
		}
		else {
			this.calendar_holidayStmt.setInt(1, calendar);
		}
		if ( description == null ) {
			this.calendar_holidayStmt.setNull(2, 12);
		}
		else {
			this.calendar_holidayStmt.setString(2, description);
		}
		if ( date == null ) {
			this.calendar_holidayStmt.setNull(3, 91);
		}
		else {
			this.calendar_holidayStmt.setDate(3, date);
		}

		this.calendar_holidayStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.calendar_holidayStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement workgroupStmt;
	
	private void initWorkgroupStmt()
	throws SQLException {
		this.workgroupStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workgroup ( description, status) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWorkgroupStmt()
	throws SQLException {
		workgroupStmt.close();
	}	
	/**
	 * Workgroup
	 * @param description Descripcion del Grupo de Trabajo
	 * @param status Estado del grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkgroup(String description, Short status)
	throws SQLException {
	
		if ( description == null ) {
			this.workgroupStmt.setNull(1, 12);
		}
		else {
			this.workgroupStmt.setString(1, description);
		}
		if ( status == null ) {
			this.workgroupStmt.setNull(2, -6);
		}
		else {
			this.workgroupStmt.setShort(2, status);
		}

		this.workgroupStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.workgroupStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement categoryStmt;
	
	private void initCategoryStmt()
	throws SQLException {
		this.categoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO category ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCategoryStmt()
	throws SQLException {
		categoryStmt.close();
	}	
	/**
	 * Category
	 * @param name Nombre de la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCategory(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.categoryStmt.setNull(1, 12);
		}
		else {
			this.categoryStmt.setString(1, name);
		}

		this.categoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.categoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement mk_action_targetStmt;
	
	private void initMk_action_targetStmt()
	throws SQLException {
		this.mk_action_targetStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_action_target ( action, target, status, survey_response) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMk_action_targetStmt()
	throws SQLException {
		mk_action_targetStmt.close();
	}	
	/**
	 * Mk_action_target
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de Campaùa
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action_target(Integer action, Integer target, Short status, Integer survey_response)
	throws SQLException {
	
		if ( action == null ) {
			this.mk_action_targetStmt.setNull(1, 4);
		}
		else {
			this.mk_action_targetStmt.setInt(1, action);
		}
		if ( target == null ) {
			this.mk_action_targetStmt.setNull(2, 4);
		}
		else {
			this.mk_action_targetStmt.setInt(2, target);
		}
		if ( status == null ) {
			this.mk_action_targetStmt.setNull(3, -6);
		}
		else {
			this.mk_action_targetStmt.setShort(3, status);
		}
		if ( survey_response == null ) {
			this.mk_action_targetStmt.setNull(4, 4);
		}
		else {
			this.mk_action_targetStmt.setInt(4, survey_response);
		}

		this.mk_action_targetStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.mk_action_targetStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement taxStmt;
	
	private void initTaxStmt()
	throws SQLException {
		this.taxStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax ( name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTaxStmt()
	throws SQLException {
		taxStmt.close();
	}	
	/**
	 * Tax
	 * @param name Nombre del Impuesto
	 * @param tax_type Tipo de Impuesto
	 * @param percentage Porcentaje de recargo actual
	 * @param surcharge Porcentaje de recargo de equivalencia actual
	 * @param start_date Fecha de inicio de vigencia
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax(String name, Short tax_type, Double percentage, Double surcharge, Date start_date, Short vat_deduction_type, Short withholding_type)
	throws SQLException {
	
		if ( name == null ) {
			this.taxStmt.setNull(1, 12);
		}
		else {
			this.taxStmt.setString(1, name);
		}
		if ( tax_type == null ) {
			this.taxStmt.setNull(2, -6);
		}
		else {
			this.taxStmt.setShort(2, tax_type);
		}
		if ( percentage == null ) {
			this.taxStmt.setNull(3, 8);
		}
		else {
			this.taxStmt.setDouble(3, percentage);
		}
		if ( surcharge == null ) {
			this.taxStmt.setNull(4, 8);
		}
		else {
			this.taxStmt.setDouble(4, surcharge);
		}
		if ( start_date == null ) {
			this.taxStmt.setNull(5, 91);
		}
		else {
			this.taxStmt.setDate(5, start_date);
		}
		if ( vat_deduction_type == null ) {
			this.taxStmt.setNull(6, -6);
		}
		else {
			this.taxStmt.setShort(6, vat_deduction_type);
		}
		if ( withholding_type == null ) {
			this.taxStmt.setNull(7, -6);
		}
		else {
			this.taxStmt.setShort(7, withholding_type);
		}

		this.taxStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.taxStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement sessionStmt;
	
	private void initSessionStmt()
	throws SQLException {
		this.sessionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO session ( endDate, remote_address, remote_host, session_id, startDate, application_id, user_id) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSessionStmt()
	throws SQLException {
		sessionStmt.close();
	}	
	/**
	 * Session
	 * @param endDate Fecha de finalizacion
	 * @param remote_address IP remota
	 * @param remote_host Equipo remoto
	 * @param session_id Identificador web de la sesiùn
	 * @param startDate Fecha de inicio
	 * @param application_id Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSession(Timestamp endDate, String remote_address, String remote_host, String session_id, Timestamp startDate, Integer application_id, Integer user_id)
	throws SQLException {
	
		if ( endDate == null ) {
			this.sessionStmt.setNull(1, 93);
		}
		else {
			this.sessionStmt.setTimestamp(1, endDate);
		}
		if ( remote_address == null ) {
			this.sessionStmt.setNull(2, 12);
		}
		else {
			this.sessionStmt.setString(2, remote_address);
		}
		if ( remote_host == null ) {
			this.sessionStmt.setNull(3, 12);
		}
		else {
			this.sessionStmt.setString(3, remote_host);
		}
		if ( session_id == null ) {
			this.sessionStmt.setNull(4, 12);
		}
		else {
			this.sessionStmt.setString(4, session_id);
		}
		if ( startDate == null ) {
			this.sessionStmt.setNull(5, 93);
		}
		else {
			this.sessionStmt.setTimestamp(5, startDate);
		}
		if ( application_id == null ) {
			this.sessionStmt.setNull(6, 4);
		}
		else {
			this.sessionStmt.setInt(6, application_id);
		}
		if ( user_id == null ) {
			this.sessionStmt.setNull(7, 4);
		}
		else {
			this.sessionStmt.setInt(7, user_id);
		}

		this.sessionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.sessionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement cv_studiesStmt;
	
	private void initCv_studiesStmt()
	throws SQLException {
		this.cv_studiesStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_studies ( startingdate, endingdate, degree, speciality, centre, curriculum) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCv_studiesStmt()
	throws SQLException {
		cv_studiesStmt.close();
	}	
	/**
	 * Cv_studies
	 * @param startingdate Fecha de inicio del Estudio
	 * @param endingdate Fecha de finalizaciùn del Estudio
	 * @param degree Nivel de Estudios
	 * @param speciality Especialidad de Estudios
	 * @param centre Centro de Estudios
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_studies(Date startingdate, Date endingdate, Short degree, String speciality, String centre, Integer curriculum)
	throws SQLException {
	
		if ( startingdate == null ) {
			this.cv_studiesStmt.setNull(1, 91);
		}
		else {
			this.cv_studiesStmt.setDate(1, startingdate);
		}
		if ( endingdate == null ) {
			this.cv_studiesStmt.setNull(2, 91);
		}
		else {
			this.cv_studiesStmt.setDate(2, endingdate);
		}
		if ( degree == null ) {
			this.cv_studiesStmt.setNull(3, -6);
		}
		else {
			this.cv_studiesStmt.setShort(3, degree);
		}
		if ( speciality == null ) {
			this.cv_studiesStmt.setNull(4, 12);
		}
		else {
			this.cv_studiesStmt.setString(4, speciality);
		}
		if ( centre == null ) {
			this.cv_studiesStmt.setNull(5, 12);
		}
		else {
			this.cv_studiesStmt.setString(5, centre);
		}
		if ( curriculum == null ) {
			this.cv_studiesStmt.setNull(6, 4);
		}
		else {
			this.cv_studiesStmt.setInt(6, curriculum);
		}

		this.cv_studiesStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.cv_studiesStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoicing_groupStmt;
	
	private void initInvoicing_groupStmt()
	throws SQLException {
		this.invoicing_groupStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoicing_group ( parent) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoicing_groupStmt()
	throws SQLException {
		invoicing_groupStmt.close();
	}	
	/**
	 * Invoicing_group
	 * @param parent Grupo de Facturacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group(Integer parent)
	throws SQLException {
	
		if ( parent == null ) {
			this.invoicing_groupStmt.setNull(1, 4);
		}
		else {
			this.invoicing_groupStmt.setInt(1, parent);
		}

		this.invoicing_groupStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoicing_groupStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement registryStmt;
	
	private void initRegistryStmt()
	throws SQLException {
		this.registryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO registry ( document, name, surname, alias, type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRegistryStmt()
	throws SQLException {
		registryStmt.close();
	}	
	/**
	 * Registry
	 * @param document Numero de Documento de la Persona o Empresa
	 * @param name Nombre de la Persona o Empresa
	 * @param surname Apellido de la Persona o Empresa
	 * @param alias Alias de la Persona o Empresa
	 * @param type Tipo (Persona o Empresa)
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRegistry(String document, String name, String surname, String alias, Short type)
	throws SQLException {
	
		if ( document == null ) {
			this.registryStmt.setNull(1, 12);
		}
		else {
			this.registryStmt.setString(1, document);
		}
		if ( name == null ) {
			this.registryStmt.setNull(2, 12);
		}
		else {
			this.registryStmt.setString(2, name);
		}
		if ( surname == null ) {
			this.registryStmt.setNull(3, 12);
		}
		else {
			this.registryStmt.setString(3, surname);
		}
		if ( alias == null ) {
			this.registryStmt.setNull(4, 12);
		}
		else {
			this.registryStmt.setString(4, alias);
		}
		if ( type == null ) {
			this.registryStmt.setNull(5, -6);
		}
		else {
			this.registryStmt.setShort(5, type);
		}

		this.registryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.registryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement deliveryStmt;
	
	private void initDeliveryStmt()
	throws SQLException {
		this.deliveryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery ( series, number, customer, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDeliveryStmt()
	throws SQLException {
		deliveryStmt.close();
	}	
	/**
	 * Delivery
	 * @param series Serie del Albaran
	 * @param number Nùmero del Albaran
	 * @param customer Identificador del Cliente
	 * @param address Identificador de la Direccion de envio del Albaran
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery(String series, Integer number, Integer customer, Integer address, Timestamp issue_time, Integer pay_method, Short security_level, Short status, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
	
		if ( series == null ) {
			this.deliveryStmt.setNull(1, 1);
		}
		else {
			this.deliveryStmt.setString(1, series);
		}
		if ( number == null ) {
			this.deliveryStmt.setNull(2, 4);
		}
		else {
			this.deliveryStmt.setInt(2, number);
		}
		if ( customer == null ) {
			this.deliveryStmt.setNull(3, 4);
		}
		else {
			this.deliveryStmt.setInt(3, customer);
		}
		if ( address == null ) {
			this.deliveryStmt.setNull(4, 4);
		}
		else {
			this.deliveryStmt.setInt(4, address);
		}
		if ( issue_time == null ) {
			this.deliveryStmt.setNull(5, 93);
		}
		else {
			this.deliveryStmt.setTimestamp(5, issue_time);
		}
		if ( pay_method == null ) {
			this.deliveryStmt.setNull(6, 4);
		}
		else {
			this.deliveryStmt.setInt(6, pay_method);
		}
		if ( security_level == null ) {
			this.deliveryStmt.setNull(7, -6);
		}
		else {
			this.deliveryStmt.setShort(7, security_level);
		}
		if ( status == null ) {
			this.deliveryStmt.setNull(8, -6);
		}
		else {
			this.deliveryStmt.setShort(8, status);
		}
		if ( workplace == null ) {
			this.deliveryStmt.setNull(9, 4);
		}
		else {
			this.deliveryStmt.setInt(9, workplace);
		}
		if ( scope == null ) {
			this.deliveryStmt.setNull(10, 4);
		}
		else {
			this.deliveryStmt.setInt(10, scope);
		}
		if ( number_of_pymnts == null ) {
			this.deliveryStmt.setNull(11, 5);
		}
		else {
			this.deliveryStmt.setInt(11, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.deliveryStmt.setNull(12, 5);
		}
		else {
			this.deliveryStmt.setInt(12, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.deliveryStmt.setNull(13, 5);
		}
		else {
			this.deliveryStmt.setInt(13, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.deliveryStmt.setNull(14, 12);
		}
		else {
			this.deliveryStmt.setString(14, pymnt_days);
		}
		if ( bank == null ) {
			this.deliveryStmt.setNull(15, 4);
		}
		else {
			this.deliveryStmt.setInt(15, bank);
		}
		if ( bank_account == null ) {
			this.deliveryStmt.setNull(16, 12);
		}
		else {
			this.deliveryStmt.setString(16, bank_account);
		}

		this.deliveryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.deliveryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement creditorStmt;
	
	private void initCreditorStmt()
	throws SQLException {
		this.creditorStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO creditor ( withholding, transaction, status, scope) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCreditorStmt()
	throws SQLException {
		creditorStmt.close();
	}	
	/**
	 * Creditor
	 * @param withholding Indica si el Acreedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Acreedor
	 * @param status Estado del Acreedor
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCreditor(Boolean withholding, Short transaction, Short status, Integer scope)
	throws SQLException {
	
		if ( withholding == null ) {
			this.creditorStmt.setNull(1, -7);
		}
		else {
			this.creditorStmt.setBoolean(1, withholding);
		}
		if ( transaction == null ) {
			this.creditorStmt.setNull(2, -6);
		}
		else {
			this.creditorStmt.setShort(2, transaction);
		}
		if ( status == null ) {
			this.creditorStmt.setNull(3, -6);
		}
		else {
			this.creditorStmt.setShort(3, status);
		}
		if ( scope == null ) {
			this.creditorStmt.setNull(4, 4);
		}
		else {
			this.creditorStmt.setInt(4, scope);
		}

		this.creditorStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.creditorStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_periodStmt;
	
	private void initAccount_periodStmt()
	throws SQLException {
		this.account_periodStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_period ( id, initiation_date, deadline, status) VALUES ( ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_periodStmt()
	throws SQLException {
		account_periodStmt.close();
	}	
	/**
	 * Account_period
	 * @param id Cùdigo del Ejercicio
	 * @param initiation_date Fecha de inicio del Ejercicio
	 * @param deadline Fecha final del Ejercicio
	 * @param status Estado del Ejercicio
	 * @throws SQLException
	*/
	protected void insertAccount_period(String id, Date initiation_date, Date deadline, Short status)
	throws SQLException {
	
		if ( id == null ) {
			this.account_periodStmt.setNull(1, 1);
		}
		else {
			this.account_periodStmt.setString(1, id);
		}
		if ( initiation_date == null ) {
			this.account_periodStmt.setNull(2, 91);
		}
		else {
			this.account_periodStmt.setDate(2, initiation_date);
		}
		if ( deadline == null ) {
			this.account_periodStmt.setNull(3, 91);
		}
		else {
			this.account_periodStmt.setDate(3, deadline);
		}
		if ( status == null ) {
			this.account_periodStmt.setNull(4, -6);
		}
		else {
			this.account_periodStmt.setShort(4, status);
		}

		this.account_periodStmt.executeUpdate();
		
	}
	
	private PreparedStatement seriesStmt;
	
	private void initSeriesStmt()
	throws SQLException {
		this.seriesStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO series ( id, description, workplace, security_level, active) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSeriesStmt()
	throws SQLException {
		seriesStmt.close();
	}	
	/**
	 * Series
	 * @param id Identificador unico
	 * @param description Descripcion de la Serie
	 * @param workplace Centro de Trabajo para el que se define la Serie
	 * @param security_level Nivel de seguridad de la Serie
	 * @param active Indica si la Serie esta activa o no
	 * @throws SQLException
	*/
	protected void insertSeries(String id, String description, Integer workplace, Short security_level, Boolean active)
	throws SQLException {
	
		if ( id == null ) {
			this.seriesStmt.setNull(1, 12);
		}
		else {
			this.seriesStmt.setString(1, id);
		}
		if ( description == null ) {
			this.seriesStmt.setNull(2, 12);
		}
		else {
			this.seriesStmt.setString(2, description);
		}
		if ( workplace == null ) {
			this.seriesStmt.setNull(3, 4);
		}
		else {
			this.seriesStmt.setInt(3, workplace);
		}
		if ( security_level == null ) {
			this.seriesStmt.setNull(4, -6);
		}
		else {
			this.seriesStmt.setShort(4, security_level);
		}
		if ( active == null ) {
			this.seriesStmt.setNull(5, -7);
		}
		else {
			this.seriesStmt.setBoolean(5, active);
		}

		this.seriesStmt.executeUpdate();
		
	}
	
	private PreparedStatement course_levelStmt;
	
	private void initCourse_levelStmt()
	throws SQLException {
		this.course_levelStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_level ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_levelStmt()
	throws SQLException {
		course_levelStmt.close();
	}	
	/**
	 * Course_level
	 * @param description Descripcion del Nivel
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_level(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.course_levelStmt.setNull(1, 12);
		}
		else {
			this.course_levelStmt.setString(1, description);
		}

		this.course_levelStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_levelStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement workplaceStmt;
	
	private void initWorkplaceStmt()
	throws SQLException {
		this.workplaceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workplace ( enterprise, description, address, economicAgreement, active, enterprise_activity) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWorkplaceStmt()
	throws SQLException {
		workplaceStmt.close();
	}	
	/**
	 * Workplace
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @param enterprise_activity Actividad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkplace(Integer enterprise, String description, Integer address, Short economicAgreement, Boolean active, Integer enterprise_activity)
	throws SQLException {
	
		if ( enterprise == null ) {
			this.workplaceStmt.setNull(1, 4);
		}
		else {
			this.workplaceStmt.setInt(1, enterprise);
		}
		if ( description == null ) {
			this.workplaceStmt.setNull(2, 12);
		}
		else {
			this.workplaceStmt.setString(2, description);
		}
		if ( address == null ) {
			this.workplaceStmt.setNull(3, 4);
		}
		else {
			this.workplaceStmt.setInt(3, address);
		}
		if ( economicAgreement == null ) {
			this.workplaceStmt.setNull(4, -6);
		}
		else {
			this.workplaceStmt.setShort(4, economicAgreement);
		}
		if ( active == null ) {
			this.workplaceStmt.setNull(5, -7);
		}
		else {
			this.workplaceStmt.setBoolean(5, active);
		}
		if ( enterprise_activity == null ) {
			this.workplaceStmt.setNull(6, 4);
		}
		else {
			this.workplaceStmt.setInt(6, enterprise_activity);
		}

		this.workplaceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.workplaceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement action_deniedStmt;
	
	private void initAction_deniedStmt()
	throws SQLException {
		this.action_deniedStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_denied ( action_id, user_id) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAction_deniedStmt()
	throws SQLException {
		action_deniedStmt.close();
	}	
	/**
	 * Action_denied
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_denied(Integer action_id, Integer user_id)
	throws SQLException {
	
		if ( action_id == null ) {
			this.action_deniedStmt.setNull(1, 4);
		}
		else {
			this.action_deniedStmt.setInt(1, action_id);
		}
		if ( user_id == null ) {
			this.action_deniedStmt.setNull(2, 4);
		}
		else {
			this.action_deniedStmt.setInt(2, user_id);
		}

		this.action_deniedStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.action_deniedStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contactStmt;
	
	private void initContactStmt()
	throws SQLException {
		this.contactStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contact ( user, name, organization, phone, cellular_phone, fax, email, address, note) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContactStmt()
	throws SQLException {
		contactStmt.close();
	}	
	/**
	 * Contact
	 * @param user Usuario al que pertenece el Contacto
	 * @param name Nombre del Contacto
	 * @param organization Organizacion a la que pertenece el Contacto
	 * @param phone Telefono del Contacto
	 * @param cellular_phone Telefono movil del Contacto
	 * @param fax Fax del Contacto
	 * @param email Correo electronico del Contacto
	 * @param address Direccion del Contacto
	 * @param note Notas sobre el Contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContact(Integer user, String name, String organization, String phone, String cellular_phone, String fax, String email, String address, InputStream note)
	throws SQLException {
	
		if ( user == null ) {
			this.contactStmt.setNull(1, 4);
		}
		else {
			this.contactStmt.setInt(1, user);
		}
		if ( name == null ) {
			this.contactStmt.setNull(2, 12);
		}
		else {
			this.contactStmt.setString(2, name);
		}
		if ( organization == null ) {
			this.contactStmt.setNull(3, 12);
		}
		else {
			this.contactStmt.setString(3, organization);
		}
		if ( phone == null ) {
			this.contactStmt.setNull(4, 12);
		}
		else {
			this.contactStmt.setString(4, phone);
		}
		if ( cellular_phone == null ) {
			this.contactStmt.setNull(5, 12);
		}
		else {
			this.contactStmt.setString(5, cellular_phone);
		}
		if ( fax == null ) {
			this.contactStmt.setNull(6, 12);
		}
		else {
			this.contactStmt.setString(6, fax);
		}
		if ( email == null ) {
			this.contactStmt.setNull(7, 12);
		}
		else {
			this.contactStmt.setString(7, email);
		}
		if ( address == null ) {
			this.contactStmt.setNull(8, 12);
		}
		else {
			this.contactStmt.setString(8, address);
		}
		if ( note == null ) {
			this.contactStmt.setNull(9, -1);
		}
		else {
			this.contactStmt.setAsciiStream(9, note);
		}

		this.contactStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contactStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement user_workgroupStmt;
	
	private void initUser_workgroupStmt()
	throws SQLException {
		this.user_workgroupStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user_workgroup ( user_id, workgroup) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeUser_workgroupStmt()
	throws SQLException {
		user_workgroupStmt.close();
	}	
	/**
	 * User_workgroup
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser_workgroup(Integer user_id, Integer workgroup)
	throws SQLException {
	
		if ( user_id == null ) {
			this.user_workgroupStmt.setNull(1, 4);
		}
		else {
			this.user_workgroupStmt.setInt(1, user_id);
		}
		if ( workgroup == null ) {
			this.user_workgroupStmt.setNull(2, 4);
		}
		else {
			this.user_workgroupStmt.setInt(2, workgroup);
		}

		this.user_workgroupStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.user_workgroupStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_scheduleStmt;
	
	private void initCourse_scheduleStmt()
	throws SQLException {
		this.course_scheduleStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_schedule ( course, day_of_week, start_time, end_time) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_scheduleStmt()
	throws SQLException {
		course_scheduleStmt.close();
	}	
	/**
	 * Course_schedule
	 * @param course Identificador del Curso
	 * @param day_of_week Dia de la semana
	 * @param start_time Hora de comienzo
	 * @param end_time Hora de fin
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_schedule(Integer course, Short day_of_week, Time start_time, Time end_time)
	throws SQLException {
	
		if ( course == null ) {
			this.course_scheduleStmt.setNull(1, 4);
		}
		else {
			this.course_scheduleStmt.setInt(1, course);
		}
		if ( day_of_week == null ) {
			this.course_scheduleStmt.setNull(2, -6);
		}
		else {
			this.course_scheduleStmt.setShort(2, day_of_week);
		}
		if ( start_time == null ) {
			this.course_scheduleStmt.setNull(3, 92);
		}
		else {
			this.course_scheduleStmt.setTime(3, start_time);
		}
		if ( end_time == null ) {
			this.course_scheduleStmt.setNull(4, 92);
		}
		else {
			this.course_scheduleStmt.setTime(4, end_time);
		}

		this.course_scheduleStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_scheduleStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rrelationshipStmt;
	
	private void initRrelationshipStmt()
	throws SQLException {
		this.rrelationshipStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rrelationship ( registry, related_registry, relationship, comments) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRrelationshipStmt()
	throws SQLException {
		rrelationshipStmt.close();
	}	
	/**
	 * Rrelationship
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de Relaciùn
	 * @param comments Comentarios de la Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRrelationship(Integer registry, Integer related_registry, Integer relationship, String comments)
	throws SQLException {
	
		if ( registry == null ) {
			this.rrelationshipStmt.setNull(1, 4);
		}
		else {
			this.rrelationshipStmt.setInt(1, registry);
		}
		if ( related_registry == null ) {
			this.rrelationshipStmt.setNull(2, 4);
		}
		else {
			this.rrelationshipStmt.setInt(2, related_registry);
		}
		if ( relationship == null ) {
			this.rrelationshipStmt.setNull(3, 4);
		}
		else {
			this.rrelationshipStmt.setInt(3, relationship);
		}
		if ( comments == null ) {
			this.rrelationshipStmt.setNull(4, 12);
		}
		else {
			this.rrelationshipStmt.setString(4, comments);
		}

		this.rrelationshipStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rrelationshipStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement web_info_pageStmt;
	
	private void initWeb_info_pageStmt()
	throws SQLException {
		this.web_info_pageStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page ( name, type, position, active) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWeb_info_pageStmt()
	throws SQLException {
		web_info_pageStmt.close();
	}	
	/**
	 * Web_info_page
	 * @param name Nombre de la Pagina.
	 * @param type Tipo de Pagina
	 * @param position Posicion de la Pagina en el menu
	 * @param active Indica si la Pagina esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_page(String name, Short type, Short position, Boolean active)
	throws SQLException {
	
		if ( name == null ) {
			this.web_info_pageStmt.setNull(1, 12);
		}
		else {
			this.web_info_pageStmt.setString(1, name);
		}
		if ( type == null ) {
			this.web_info_pageStmt.setNull(2, -6);
		}
		else {
			this.web_info_pageStmt.setShort(2, type);
		}
		if ( position == null ) {
			this.web_info_pageStmt.setNull(3, -6);
		}
		else {
			this.web_info_pageStmt.setShort(3, position);
		}
		if ( active == null ) {
			this.web_info_pageStmt.setNull(4, -7);
		}
		else {
			this.web_info_pageStmt.setBoolean(4, active);
		}

		this.web_info_pageStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.web_info_pageStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement enterprise_certificateStmt;
	
	private void initEnterprise_certificateStmt()
	throws SQLException {
		this.enterprise_certificateStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_certificate ( enterprise, date, status, sign) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEnterprise_certificateStmt()
	throws SQLException {
		enterprise_certificateStmt.close();
	}	
	/**
	 * Enterprise_certificate
	 * @param enterprise Identificador unico de la empresa
	 * @param date Fecha de la ultima remesa en la que fue incluido
	 * @param status Estado del certificado correspondiente a la ultima respuesta
	 * @param sign Estado del certificado correspondiente a la ultima respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_certificate(Integer enterprise, Date date, Integer status, String sign)
	throws SQLException {
	
		if ( enterprise == null ) {
			this.enterprise_certificateStmt.setNull(1, 4);
		}
		else {
			this.enterprise_certificateStmt.setInt(1, enterprise);
		}
		if ( date == null ) {
			this.enterprise_certificateStmt.setNull(2, 91);
		}
		else {
			this.enterprise_certificateStmt.setDate(2, date);
		}
		if ( status == null ) {
			this.enterprise_certificateStmt.setNull(3, 4);
		}
		else {
			this.enterprise_certificateStmt.setInt(3, status);
		}
		if ( sign == null ) {
			this.enterprise_certificateStmt.setNull(4, 12);
		}
		else {
			this.enterprise_certificateStmt.setString(4, sign);
		}

		this.enterprise_certificateStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.enterprise_certificateStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement campaignStmt;
	
	private void initCampaignStmt()
	throws SQLException {
		this.campaignStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO campaign ( description, process, activity_type, start_date, end_date, workgroup, type, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCampaignStmt()
	throws SQLException {
		campaignStmt.close();
	}	
	/**
	 * Campaign
	 * @param description Descripcion de la Campaùa
	 * @param process Identificador del Proceso
	 * @param activity_type Identificador del Tipo de Actividad
	 * @param start_date Fecha de inicio de la Campaùa
	 * @param end_date Fecha de finalizacion de la Campaùa
	 * @param workgroup Grupo de Trabajo supervisor de la Campaùa
	 * @param type Tipo de Campaùa
	 * @param status Estado de la Campaùa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign(String description, Integer process, Integer activity_type, Date start_date, Date end_date, Integer workgroup, Short type, Short status)
	throws SQLException {
	
		if ( description == null ) {
			this.campaignStmt.setNull(1, 12);
		}
		else {
			this.campaignStmt.setString(1, description);
		}
		if ( process == null ) {
			this.campaignStmt.setNull(2, 4);
		}
		else {
			this.campaignStmt.setInt(2, process);
		}
		if ( activity_type == null ) {
			this.campaignStmt.setNull(3, 4);
		}
		else {
			this.campaignStmt.setInt(3, activity_type);
		}
		if ( start_date == null ) {
			this.campaignStmt.setNull(4, 91);
		}
		else {
			this.campaignStmt.setDate(4, start_date);
		}
		if ( end_date == null ) {
			this.campaignStmt.setNull(5, 91);
		}
		else {
			this.campaignStmt.setDate(5, end_date);
		}
		if ( workgroup == null ) {
			this.campaignStmt.setNull(6, 4);
		}
		else {
			this.campaignStmt.setInt(6, workgroup);
		}
		if ( type == null ) {
			this.campaignStmt.setNull(7, -6);
		}
		else {
			this.campaignStmt.setShort(7, type);
		}
		if ( status == null ) {
			this.campaignStmt.setNull(8, -6);
		}
		else {
			this.campaignStmt.setShort(8, status);
		}

		this.campaignStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.campaignStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement fs_vatStmt;
	
	private void initFs_vatStmt()
	throws SQLException {
		this.fs_vatStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fs_vat ( year, period, type, comments, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFs_vatStmt()
	throws SQLException {
		fs_vatStmt.close();
	}	
	/**
	 * Fs_vat
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param type Tipo de Declaracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_vat(Integer year, Short period, Short type, InputStream comments, Short status)
	throws SQLException {
	
		if ( year == null ) {
			this.fs_vatStmt.setNull(1, 4);
		}
		else {
			this.fs_vatStmt.setInt(1, year);
		}
		if ( period == null ) {
			this.fs_vatStmt.setNull(2, -6);
		}
		else {
			this.fs_vatStmt.setShort(2, period);
		}
		if ( type == null ) {
			this.fs_vatStmt.setNull(3, -6);
		}
		else {
			this.fs_vatStmt.setShort(3, type);
		}
		if ( comments == null ) {
			this.fs_vatStmt.setNull(4, -1);
		}
		else {
			this.fs_vatStmt.setAsciiStream(4, comments);
		}
		if ( status == null ) {
			this.fs_vatStmt.setNull(5, -6);
		}
		else {
			this.fs_vatStmt.setShort(5, status);
		}

		this.fs_vatStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.fs_vatStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contract_deductionStmt;
	
	private void initContract_deductionStmt()
	throws SQLException {
		this.contract_deductionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_deduction ( type, contract, description, function, start_date, end_date) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_deductionStmt()
	throws SQLException {
		contract_deductionStmt.close();
	}	
	/**
	 * Contract_deduction
	 * @param type Tipo de DeducciÛn
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param function FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_deduction(Short type, Integer contract, String description, String function, Date start_date, Date end_date)
	throws SQLException {
	
		if ( type == null ) {
			this.contract_deductionStmt.setNull(1, -6);
		}
		else {
			this.contract_deductionStmt.setShort(1, type);
		}
		if ( contract == null ) {
			this.contract_deductionStmt.setNull(2, 4);
		}
		else {
			this.contract_deductionStmt.setInt(2, contract);
		}
		if ( description == null ) {
			this.contract_deductionStmt.setNull(3, 12);
		}
		else {
			this.contract_deductionStmt.setString(3, description);
		}
		if ( function == null ) {
			this.contract_deductionStmt.setNull(4, 12);
		}
		else {
			this.contract_deductionStmt.setString(4, function);
		}
		if ( start_date == null ) {
			this.contract_deductionStmt.setNull(5, 91);
		}
		else {
			this.contract_deductionStmt.setDate(5, start_date);
		}
		if ( end_date == null ) {
			this.contract_deductionStmt.setNull(6, 91);
		}
		else {
			this.contract_deductionStmt.setDate(6, end_date);
		}

		this.contract_deductionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_deductionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement noteStmt;
	
	private void initNoteStmt()
	throws SQLException {
		this.noteStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO note ( subject, date, owner, note) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeNoteStmt()
	throws SQLException {
		noteStmt.close();
	}	
	/**
	 * Note
	 * @param subject Descripcion corta de la Nota
	 * @param date Fecha de la Nota
	 * @param owner Destinatario de la Nota
	 * @param note Texto de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertNote(String subject, Timestamp date, Integer owner, InputStream note)
	throws SQLException {
	
		if ( subject == null ) {
			this.noteStmt.setNull(1, 12);
		}
		else {
			this.noteStmt.setString(1, subject);
		}
		if ( date == null ) {
			this.noteStmt.setNull(2, 93);
		}
		else {
			this.noteStmt.setTimestamp(2, date);
		}
		if ( owner == null ) {
			this.noteStmt.setNull(3, 4);
		}
		else {
			this.noteStmt.setInt(3, owner);
		}
		if ( note == null ) {
			this.noteStmt.setNull(4, -1);
		}
		else {
			this.noteStmt.setAsciiStream(4, note);
		}

		this.noteStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.noteStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_subjectStmt;
	
	private void initCourse_subjectStmt()
	throws SQLException {
		this.course_subjectStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_subject ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_subjectStmt()
	throws SQLException {
		course_subjectStmt.close();
	}	
	/**
	 * Course_subject
	 * @param description Descripcion de la Materia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_subject(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.course_subjectStmt.setNull(1, 12);
		}
		else {
			this.course_subjectStmt.setString(1, description);
		}

		this.course_subjectStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_subjectStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement observationStmt;
	
	private void initObservationStmt()
	throws SQLException {
		this.observationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO observation ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeObservationStmt()
	throws SQLException {
		observationStmt.close();
	}	
	/**
	 * Observation
	 * @param description Descripcion de la Observacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertObservation(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.observationStmt.setNull(1, 12);
		}
		else {
			this.observationStmt.setString(1, description);
		}

		this.observationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.observationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement action_favoriteStmt;
	
	private void initAction_favoriteStmt()
	throws SQLException {
		this.action_favoriteStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_favorite ( position, action_id, user_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAction_favoriteStmt()
	throws SQLException {
		action_favoriteStmt.close();
	}	
	/**
	 * Action_favorite
	 * @param position Posicion dentro de las Acciones Favoritas
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_favorite(Integer position, Integer action_id, Integer user_id)
	throws SQLException {
	
		if ( position == null ) {
			this.action_favoriteStmt.setNull(1, 4);
		}
		else {
			this.action_favoriteStmt.setInt(1, position);
		}
		if ( action_id == null ) {
			this.action_favoriteStmt.setNull(2, 4);
		}
		else {
			this.action_favoriteStmt.setInt(2, action_id);
		}
		if ( user_id == null ) {
			this.action_favoriteStmt.setNull(3, 4);
		}
		else {
			this.action_favoriteStmt.setInt(3, user_id);
		}

		this.action_favoriteStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.action_favoriteStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement loan_accountStmt;
	
	private void initLoan_accountStmt()
	throws SQLException {
		this.loan_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO loan_account ( loan, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLoan_accountStmt()
	throws SQLException {
		loan_accountStmt.close();
	}	
	/**
	 * Loan_account
	 * @param loan Prestamo
	 * @param account Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLoan_account(Integer loan, String account)
	throws SQLException {
	
		if ( loan == null ) {
			this.loan_accountStmt.setNull(1, 4);
		}
		else {
			this.loan_accountStmt.setInt(1, loan);
		}
		if ( account == null ) {
			this.loan_accountStmt.setNull(2, 1);
		}
		else {
			this.loan_accountStmt.setString(2, account);
		}

		this.loan_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.loan_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement survey_workflowStmt;
	
	private void initSurvey_workflowStmt()
	throws SQLException {
		this.survey_workflowStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_workflow ( questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSurvey_workflowStmt()
	throws SQLException {
		survey_workflowStmt.close();
	}	
	/**
	 * Survey_workflow
	 * @param questionValue Identificador del Valor de la Pregunta
	 * @param surveyQuestion Identificador de la Pregunta del Cuestionario
	 * @param nextSurveyQuestion Identificador de la siguiente Pregunta del Cuestionario
	 * @param operator Operador a utilizar con el Valor
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_workflow(Integer questionValue, Integer surveyQuestion, Integer nextSurveyQuestion, Short operator, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
	
		if ( questionValue == null ) {
			this.survey_workflowStmt.setNull(1, 4);
		}
		else {
			this.survey_workflowStmt.setInt(1, questionValue);
		}
		if ( surveyQuestion == null ) {
			this.survey_workflowStmt.setNull(2, 4);
		}
		else {
			this.survey_workflowStmt.setInt(2, surveyQuestion);
		}
		if ( nextSurveyQuestion == null ) {
			this.survey_workflowStmt.setNull(3, 4);
		}
		else {
			this.survey_workflowStmt.setInt(3, nextSurveyQuestion);
		}
		if ( operator == null ) {
			this.survey_workflowStmt.setNull(4, -6);
		}
		else {
			this.survey_workflowStmt.setShort(4, operator);
		}
		if ( value_text == null ) {
			this.survey_workflowStmt.setNull(5, 12);
		}
		else {
			this.survey_workflowStmt.setString(5, value_text);
		}
		if ( value_number == null ) {
			this.survey_workflowStmt.setNull(6, 8);
		}
		else {
			this.survey_workflowStmt.setDouble(6, value_number);
		}
		if ( value_date == null ) {
			this.survey_workflowStmt.setNull(7, 93);
		}
		else {
			this.survey_workflowStmt.setTimestamp(7, value_date);
		}

		this.survey_workflowStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.survey_workflowStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement processStmt;
	
	private void initProcessStmt()
	throws SQLException {
		this.processStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process ( description, status) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProcessStmt()
	throws SQLException {
		processStmt.close();
	}	
	/**
	 * Process
	 * @param description Descripcion del Proceso.
	 * @param status Estado del Proceso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess(String description, Short status)
	throws SQLException {
	
		if ( description == null ) {
			this.processStmt.setNull(1, 12);
		}
		else {
			this.processStmt.setString(1, description);
		}
		if ( status == null ) {
			this.processStmt.setNull(2, -6);
		}
		else {
			this.processStmt.setShort(2, status);
		}

		this.processStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.processStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement productStmt;
	
	private void initProductStmt()
	throws SQLException {
		this.productStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO product ( name, code, brand, category, inventoriable, status, vat, retention, type, composition) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeProductStmt()
	throws SQLException {
		productStmt.close();
	}	
	/**
	 * Product
	 * @param name Nombre del Producto
	 * @param code Codigo del Producto
	 * @param brand Marca Comercial del Producto
	 * @param category Categoria del Producto
	 * @param inventoriable Indica si el Producto es inventariable
	 * @param status Estado del Producto
	 * @param vat IVA del Producto
	 * @param retention Retencion del Producto
	 * @param type Tipo de Producto
	 * @param composition Indica si el Producto es una Composicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduct(String name, String code, Integer brand, Integer category, Boolean inventoriable, Short status, Integer vat, Integer retention, Short type, Boolean composition)
	throws SQLException {
	
		if ( name == null ) {
			this.productStmt.setNull(1, 12);
		}
		else {
			this.productStmt.setString(1, name);
		}
		if ( code == null ) {
			this.productStmt.setNull(2, 12);
		}
		else {
			this.productStmt.setString(2, code);
		}
		if ( brand == null ) {
			this.productStmt.setNull(3, 4);
		}
		else {
			this.productStmt.setInt(3, brand);
		}
		if ( category == null ) {
			this.productStmt.setNull(4, 4);
		}
		else {
			this.productStmt.setInt(4, category);
		}
		if ( inventoriable == null ) {
			this.productStmt.setNull(5, -7);
		}
		else {
			this.productStmt.setBoolean(5, inventoriable);
		}
		if ( status == null ) {
			this.productStmt.setNull(6, -6);
		}
		else {
			this.productStmt.setShort(6, status);
		}
		if ( vat == null ) {
			this.productStmt.setNull(7, 4);
		}
		else {
			this.productStmt.setInt(7, vat);
		}
		if ( retention == null ) {
			this.productStmt.setNull(8, 4);
		}
		else {
			this.productStmt.setInt(8, retention);
		}
		if ( type == null ) {
			this.productStmt.setNull(9, -6);
		}
		else {
			this.productStmt.setShort(9, type);
		}
		if ( composition == null ) {
			this.productStmt.setNull(10, -7);
		}
		else {
			this.productStmt.setBoolean(10, composition);
		}

		this.productStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.productStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement itemStmt;
	
	private void initItemStmt()
	throws SQLException {
		this.itemStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item ( product, detail, description, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItemStmt()
	throws SQLException {
		itemStmt.close();
	}	
	/**
	 * Item
	 * @param product Identificador del Producto
	 * @param detail Detalle del Articulo
	 * @param description Descripcion del Articulo
	 * @param price Precio del Articulo
	 * @param status Estado del Articulo
	 * @param expenses_percent Gastos porcentuales del Articulo
	 * @param expenses_fixed Gastos fijos del Articulo
	 * @param profit_percent Porcentaje de beneficio del Articulo
	 * @param purchase_price Precio de compra del Articulo
	 * @param internet Visible en internet
	 * @param barcode Codigo de barras del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem(Integer product, String detail, InputStream description, Double price, Short status, Double expenses_percent, Double expenses_fixed, Double profit_percent, Double purchase_price, Boolean internet, String barcode)
	throws SQLException {
	
		if ( product == null ) {
			this.itemStmt.setNull(1, 4);
		}
		else {
			this.itemStmt.setInt(1, product);
		}
		if ( detail == null ) {
			this.itemStmt.setNull(2, 12);
		}
		else {
			this.itemStmt.setString(2, detail);
		}
		if ( description == null ) {
			this.itemStmt.setNull(3, -1);
		}
		else {
			this.itemStmt.setAsciiStream(3, description);
		}
		if ( price == null ) {
			this.itemStmt.setNull(4, 8);
		}
		else {
			this.itemStmt.setDouble(4, price);
		}
		if ( status == null ) {
			this.itemStmt.setNull(5, -6);
		}
		else {
			this.itemStmt.setShort(5, status);
		}
		if ( expenses_percent == null ) {
			this.itemStmt.setNull(6, 8);
		}
		else {
			this.itemStmt.setDouble(6, expenses_percent);
		}
		if ( expenses_fixed == null ) {
			this.itemStmt.setNull(7, 8);
		}
		else {
			this.itemStmt.setDouble(7, expenses_fixed);
		}
		if ( profit_percent == null ) {
			this.itemStmt.setNull(8, 8);
		}
		else {
			this.itemStmt.setDouble(8, profit_percent);
		}
		if ( purchase_price == null ) {
			this.itemStmt.setNull(9, 8);
		}
		else {
			this.itemStmt.setDouble(9, purchase_price);
		}
		if ( internet == null ) {
			this.itemStmt.setNull(10, -7);
		}
		else {
			this.itemStmt.setBoolean(10, internet);
		}
		if ( barcode == null ) {
			this.itemStmt.setNull(11, 12);
		}
		else {
			this.itemStmt.setString(11, barcode);
		}

		this.itemStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.itemStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement accountStmt;
	
	private void initAccountStmt()
	throws SQLException {
		this.accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account ( id, description, alias, entryEnabled, level) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccountStmt()
	throws SQLException {
		accountStmt.close();
	}	
	/**
	 * Account
	 * @param id Identificador unico de la Cuenta
	 * @param description Descripcion de la Cuenta
	 * @param alias Alias de la Cuenta
	 * @param entryEnabled Indica si la Cuenta permite o no Apuntes
	 * @param level Nivel de la Cuenta
	 * @throws SQLException
	*/
	protected void insertAccount(String id, String description, String alias, Short entryEnabled, Short level)
	throws SQLException {
	
		if ( id == null ) {
			this.accountStmt.setNull(1, 1);
		}
		else {
			this.accountStmt.setString(1, id);
		}
		if ( description == null ) {
			this.accountStmt.setNull(2, 12);
		}
		else {
			this.accountStmt.setString(2, description);
		}
		if ( alias == null ) {
			this.accountStmt.setNull(3, 12);
		}
		else {
			this.accountStmt.setString(3, alias);
		}
		if ( entryEnabled == null ) {
			this.accountStmt.setNull(4, -6);
		}
		else {
			this.accountStmt.setShort(4, entryEnabled);
		}
		if ( level == null ) {
			this.accountStmt.setNull(5, -6);
		}
		else {
			this.accountStmt.setShort(5, level);
		}

		this.accountStmt.executeUpdate();
		
	}
	
	private PreparedStatement brandStmt;
	
	private void initBrandStmt()
	throws SQLException {
		this.brandStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO brand ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeBrandStmt()
	throws SQLException {
		brandStmt.close();
	}	
	/**
	 * Brand
	 * @param name Nombre de la Marca Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBrand(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.brandStmt.setNull(1, 12);
		}
		else {
			this.brandStmt.setString(1, name);
		}

		this.brandStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.brandStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement target_sellerStmt;
	
	private void initTarget_sellerStmt()
	throws SQLException {
		this.target_sellerStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_seller ( target, seller, start_date, end_date, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTarget_sellerStmt()
	throws SQLException {
		target_sellerStmt.close();
	}	
	/**
	 * Target_seller
	 * @param target Identificador del Cliente Potencial
	 * @param seller Identificador del Comercial
	 * @param start_date Fecha de Inicio
	 * @param end_date Fecha de Fin
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_seller(Integer target, Integer seller, Date start_date, Date end_date, Short status)
	throws SQLException {
	
		if ( target == null ) {
			this.target_sellerStmt.setNull(1, 4);
		}
		else {
			this.target_sellerStmt.setInt(1, target);
		}
		if ( seller == null ) {
			this.target_sellerStmt.setNull(2, 4);
		}
		else {
			this.target_sellerStmt.setInt(2, seller);
		}
		if ( start_date == null ) {
			this.target_sellerStmt.setNull(3, 91);
		}
		else {
			this.target_sellerStmt.setDate(3, start_date);
		}
		if ( end_date == null ) {
			this.target_sellerStmt.setNull(4, 91);
		}
		else {
			this.target_sellerStmt.setDate(4, end_date);
		}
		if ( status == null ) {
			this.target_sellerStmt.setNull(5, -6);
		}
		else {
			this.target_sellerStmt.setShort(5, status);
		}

		this.target_sellerStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.target_sellerStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contract_bonusStmt;
	
	private void initContract_bonusStmt()
	throws SQLException {
		this.contract_bonusStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_bonus ( contract, description, function, start_date, end_date) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_bonusStmt()
	throws SQLException {
		contract_bonusStmt.close();
	}	
	/**
	 * Contract_bonus
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param function FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_bonus(Integer contract, String description, String function, Date start_date, Date end_date)
	throws SQLException {
	
		if ( contract == null ) {
			this.contract_bonusStmt.setNull(1, 4);
		}
		else {
			this.contract_bonusStmt.setInt(1, contract);
		}
		if ( description == null ) {
			this.contract_bonusStmt.setNull(2, 12);
		}
		else {
			this.contract_bonusStmt.setString(2, description);
		}
		if ( function == null ) {
			this.contract_bonusStmt.setNull(3, 12);
		}
		else {
			this.contract_bonusStmt.setString(3, function);
		}
		if ( start_date == null ) {
			this.contract_bonusStmt.setNull(4, 91);
		}
		else {
			this.contract_bonusStmt.setDate(4, start_date);
		}
		if ( end_date == null ) {
			this.contract_bonusStmt.setNull(5, 91);
		}
		else {
			this.contract_bonusStmt.setDate(5, end_date);
		}

		this.contract_bonusStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_bonusStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement financeStmt;
	
	private void initFinanceStmt()
	throws SQLException {
		this.financeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO finance ( payment, registry, amount, expenses, concept, invoice, due_date, pay_method, bank, bank_account, status, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeFinanceStmt()
	throws SQLException {
		financeStmt.close();
	}	
	/**
	 * Finance
	 * @param payment Indica si es un pago o un cobro
	 * @param registry Identificador del Cliente o Proveedor
	 * @param amount Importe del Vencimiento
	 * @param expenses Gastos asociados al Vencimiento
	 * @param concept Concepto del Vencimiento
	 * @param invoice Identificador de la Factura
	 * @param due_date Fecha de Vencimiento
	 * @param pay_method Identificador de la Forma de Pago
	 * @param bank Identificador de la Entidad Bancaria del Vencimiento
	 * @param bank_account Numero de cuenta en la Entidad Bancaria del Vencimiento
	 * @param status Estado del Vencimiento
	 * @param security_level Nivel de seguridad del Vencimiento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFinance(Boolean payment, Integer registry, Double amount, Double expenses, String concept, Integer invoice, Date due_date, Integer pay_method, Integer bank, String bank_account, Short status, Short security_level)
	throws SQLException {
	
		if ( payment == null ) {
			this.financeStmt.setNull(1, -7);
		}
		else {
			this.financeStmt.setBoolean(1, payment);
		}
		if ( registry == null ) {
			this.financeStmt.setNull(2, 4);
		}
		else {
			this.financeStmt.setInt(2, registry);
		}
		if ( amount == null ) {
			this.financeStmt.setNull(3, 8);
		}
		else {
			this.financeStmt.setDouble(3, amount);
		}
		if ( expenses == null ) {
			this.financeStmt.setNull(4, 8);
		}
		else {
			this.financeStmt.setDouble(4, expenses);
		}
		if ( concept == null ) {
			this.financeStmt.setNull(5, 12);
		}
		else {
			this.financeStmt.setString(5, concept);
		}
		if ( invoice == null ) {
			this.financeStmt.setNull(6, 4);
		}
		else {
			this.financeStmt.setInt(6, invoice);
		}
		if ( due_date == null ) {
			this.financeStmt.setNull(7, 91);
		}
		else {
			this.financeStmt.setDate(7, due_date);
		}
		if ( pay_method == null ) {
			this.financeStmt.setNull(8, 4);
		}
		else {
			this.financeStmt.setInt(8, pay_method);
		}
		if ( bank == null ) {
			this.financeStmt.setNull(9, 4);
		}
		else {
			this.financeStmt.setInt(9, bank);
		}
		if ( bank_account == null ) {
			this.financeStmt.setNull(10, 12);
		}
		else {
			this.financeStmt.setString(10, bank_account);
		}
		if ( status == null ) {
			this.financeStmt.setNull(11, -6);
		}
		else {
			this.financeStmt.setShort(11, status);
		}
		if ( security_level == null ) {
			this.financeStmt.setNull(12, -6);
		}
		else {
			this.financeStmt.setShort(12, security_level);
		}

		this.financeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.financeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement dossier_typeStmt;
	
	private void initDossier_typeStmt()
	throws SQLException {
		this.dossier_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO dossier_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDossier_typeStmt()
	throws SQLException {
		dossier_typeStmt.close();
	}	
	/**
	 * Dossier_type
	 * @param description Descripcion del Tipo de Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDossier_type(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.dossier_typeStmt.setNull(1, 12);
		}
		else {
			this.dossier_typeStmt.setString(1, description);
		}

		this.dossier_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.dossier_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement geozoneStmt;
	
	private void initGeozoneStmt()
	throws SQLException {
		this.geozoneStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO geozone ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeGeozoneStmt()
	throws SQLException {
		geozoneStmt.close();
	}	
	/**
	 * Geozone
	 * @param name Nombre de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertGeozone(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.geozoneStmt.setNull(1, 12);
		}
		else {
			this.geozoneStmt.setString(1, name);
		}

		this.geozoneStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.geozoneStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement pcategory_groupStmt;
	
	private void initPcategory_groupStmt()
	throws SQLException {
		this.pcategory_groupStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory_group ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closePcategory_groupStmt()
	throws SQLException {
		pcategory_groupStmt.close();
	}	
	/**
	 * Pcategory_group
	 * @param name Nombre del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_group(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.pcategory_groupStmt.setNull(1, 12);
		}
		else {
			this.pcategory_groupStmt.setString(1, name);
		}

		this.pcategory_groupStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.pcategory_groupStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement segmentStmt;
	
	private void initSegmentStmt()
	throws SQLException {
		this.segmentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO segment ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSegmentStmt()
	throws SQLException {
		segmentStmt.close();
	}	
	/**
	 * Segment
	 * @param name Nombre del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSegment(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.segmentStmt.setNull(1, 12);
		}
		else {
			this.segmentStmt.setString(1, name);
		}

		this.segmentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.segmentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement salary_deductionStmt;
	
	private void initSalary_deductionStmt()
	throws SQLException {
		this.salary_deductionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO salary_deduction ( salary, type, description, function, amount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSalary_deductionStmt()
	throws SQLException {
		salary_deductionStmt.close();
	}	
	/**
	 * Salary_deduction
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de deducciÛn Salarial
	 * @param description Descripcion
	 * @param function FÛrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary_deduction(Integer salary, Short type, String description, String function, Double amount)
	throws SQLException {
	
		if ( salary == null ) {
			this.salary_deductionStmt.setNull(1, 4);
		}
		else {
			this.salary_deductionStmt.setInt(1, salary);
		}
		if ( type == null ) {
			this.salary_deductionStmt.setNull(2, -6);
		}
		else {
			this.salary_deductionStmt.setShort(2, type);
		}
		if ( description == null ) {
			this.salary_deductionStmt.setNull(3, 12);
		}
		else {
			this.salary_deductionStmt.setString(3, description);
		}
		if ( function == null ) {
			this.salary_deductionStmt.setNull(4, 12);
		}
		else {
			this.salary_deductionStmt.setString(4, function);
		}
		if ( amount == null ) {
			this.salary_deductionStmt.setNull(5, 8);
		}
		else {
			this.salary_deductionStmt.setDouble(5, amount);
		}

		this.salary_deductionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.salary_deductionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoiceStmt;
	
	private void initInvoiceStmt()
	throws SQLException {
		this.invoiceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice ( series, number, reference_code, registry, rdocument, rname, raddress, issue_date, tax_date, security_level, status, type, taxFree, surcharge, withholding, comments, investment, transaction, signed, scope) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoiceStmt()
	throws SQLException {
		invoiceStmt.close();
	}	
	/**
	 * Invoice
	 * @param series Serie de la Factura
	 * @param number Numero de la Factura
	 * @param reference_code Codigo de referencia de la Factura
	 * @param registry Identificador del Cliente o Proveedor
	 * @param rdocument Numero de Documento del Cliente o Proveedor
	 * @param rname Nombre completo del Cliente o Proveedor
	 * @param raddress Identificador de la Direccion de envio de la Factura
	 * @param issue_date Fecha de emision de la Factura
	 * @param tax_date Fecha de Impuestos de la Factura
	 * @param security_level Nivel de seguridad de la Factura
	 * @param status Estado de la Factura
	 * @param type Tipo de Factura (Compra o Venta)
	 * @param taxFree Indica si la Factura esta exenta de Impuestos
	 * @param surcharge Indica si la Factura tiene recargo de equivalencia
	 * @param withholding Indica si la Factura aplica retencion de impuestos
	 * @param comments Comentarios de la Factura
	 * @param investment Indica si la Factura es una inversion
	 * @param transaction Tipo de transaccion
	 * @param signed Indica si la Factura esta firmada electronicamente
	 * @param scope Ambito de la Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice(String series, Integer number, String reference_code, Integer registry, String rdocument, String rname, Integer raddress, Date issue_date, Date tax_date, Short security_level, Short status, Short type, Boolean taxFree, Boolean surcharge, Boolean withholding, InputStream comments, Boolean investment, Short transaction, Boolean signed, Integer scope)
	throws SQLException {
	
		if ( series == null ) {
			this.invoiceStmt.setNull(1, 1);
		}
		else {
			this.invoiceStmt.setString(1, series);
		}
		if ( number == null ) {
			this.invoiceStmt.setNull(2, 4);
		}
		else {
			this.invoiceStmt.setInt(2, number);
		}
		if ( reference_code == null ) {
			this.invoiceStmt.setNull(3, 12);
		}
		else {
			this.invoiceStmt.setString(3, reference_code);
		}
		if ( registry == null ) {
			this.invoiceStmt.setNull(4, 4);
		}
		else {
			this.invoiceStmt.setInt(4, registry);
		}
		if ( rdocument == null ) {
			this.invoiceStmt.setNull(5, 12);
		}
		else {
			this.invoiceStmt.setString(5, rdocument);
		}
		if ( rname == null ) {
			this.invoiceStmt.setNull(6, 12);
		}
		else {
			this.invoiceStmt.setString(6, rname);
		}
		if ( raddress == null ) {
			this.invoiceStmt.setNull(7, 4);
		}
		else {
			this.invoiceStmt.setInt(7, raddress);
		}
		if ( issue_date == null ) {
			this.invoiceStmt.setNull(8, 91);
		}
		else {
			this.invoiceStmt.setDate(8, issue_date);
		}
		if ( tax_date == null ) {
			this.invoiceStmt.setNull(9, 91);
		}
		else {
			this.invoiceStmt.setDate(9, tax_date);
		}
		if ( security_level == null ) {
			this.invoiceStmt.setNull(10, -6);
		}
		else {
			this.invoiceStmt.setShort(10, security_level);
		}
		if ( status == null ) {
			this.invoiceStmt.setNull(11, -6);
		}
		else {
			this.invoiceStmt.setShort(11, status);
		}
		if ( type == null ) {
			this.invoiceStmt.setNull(12, -6);
		}
		else {
			this.invoiceStmt.setShort(12, type);
		}
		if ( taxFree == null ) {
			this.invoiceStmt.setNull(13, -7);
		}
		else {
			this.invoiceStmt.setBoolean(13, taxFree);
		}
		if ( surcharge == null ) {
			this.invoiceStmt.setNull(14, -7);
		}
		else {
			this.invoiceStmt.setBoolean(14, surcharge);
		}
		if ( withholding == null ) {
			this.invoiceStmt.setNull(15, -7);
		}
		else {
			this.invoiceStmt.setBoolean(15, withholding);
		}
		if ( comments == null ) {
			this.invoiceStmt.setNull(16, -1);
		}
		else {
			this.invoiceStmt.setAsciiStream(16, comments);
		}
		if ( investment == null ) {
			this.invoiceStmt.setNull(17, -7);
		}
		else {
			this.invoiceStmt.setBoolean(17, investment);
		}
		if ( transaction == null ) {
			this.invoiceStmt.setNull(18, -6);
		}
		else {
			this.invoiceStmt.setShort(18, transaction);
		}
		if ( signed == null ) {
			this.invoiceStmt.setNull(19, -7);
		}
		else {
			this.invoiceStmt.setBoolean(19, signed);
		}
		if ( scope == null ) {
			this.invoiceStmt.setNull(20, 4);
		}
		else {
			this.invoiceStmt.setInt(20, scope);
		}

		this.invoiceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoiceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_attachStmt;
	
	private void initInvoice_attachStmt()
	throws SQLException {
		this.invoice_attachStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_attach ( invoice, mimeType, description, data) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_attachStmt()
	throws SQLException {
		invoice_attachStmt.close();
	}	
	/**
	 * Invoice_attach
	 * @param invoice Identificador de la Factura
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_attach(Integer invoice, Short mimeType, String description, InputStream data)
	throws SQLException {
	
		if ( invoice == null ) {
			this.invoice_attachStmt.setNull(1, 4);
		}
		else {
			this.invoice_attachStmt.setInt(1, invoice);
		}
		if ( mimeType == null ) {
			this.invoice_attachStmt.setNull(2, -6);
		}
		else {
			this.invoice_attachStmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			this.invoice_attachStmt.setNull(3, 12);
		}
		else {
			this.invoice_attachStmt.setString(3, description);
		}
		if ( data == null ) {
			this.invoice_attachStmt.setNull(4, -4);
		}
		else {
			this.invoice_attachStmt.setBinaryStream(4, data);
		}

		this.invoice_attachStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_attachStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_detailStmt;
	
	private void initInvoice_detailStmt()
	throws SQLException {
		this.invoice_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_detail ( invoice, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_detailStmt()
	throws SQLException {
		invoice_detailStmt.close();
	}	
	/**
	 * Invoice_detail
	 * @param invoice Identificador de la Factura
	 * @param line Numero de lùnea del Detalle dentro de la Factura
	 * @param item Identificador del Articulo del Detalle de Factura
	 * @param description Descripcion del Detalle de Factura
	 * @param quantity Cantidad del Detalle de Factura
	 * @param price Precio del Detalle de Factura
	 * @param discount_expr Descuentos del Detalle de Factura
	 * @param source Origen del Detalle de la Factura
	 * @param source_id Identificador del Origen del Detalle de la Factura
	 * @param taxable_base Base Imponible del Detalle de Factura
	 * @param taxes Tasas del Detalle de Factura
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_detail(Integer invoice, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short source, Integer source_id, Double taxable_base, Double taxes, Integer workplace)
	throws SQLException {
	
		if ( invoice == null ) {
			this.invoice_detailStmt.setNull(1, 4);
		}
		else {
			this.invoice_detailStmt.setInt(1, invoice);
		}
		if ( line == null ) {
			this.invoice_detailStmt.setNull(2, 5);
		}
		else {
			this.invoice_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.invoice_detailStmt.setNull(3, 4);
		}
		else {
			this.invoice_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.invoice_detailStmt.setNull(4, 12);
		}
		else {
			this.invoice_detailStmt.setString(4, description);
		}
		if ( quantity == null ) {
			this.invoice_detailStmt.setNull(5, 8);
		}
		else {
			this.invoice_detailStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.invoice_detailStmt.setNull(6, 8);
		}
		else {
			this.invoice_detailStmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			this.invoice_detailStmt.setNull(7, 12);
		}
		else {
			this.invoice_detailStmt.setString(7, discount_expr);
		}
		if ( source == null ) {
			this.invoice_detailStmt.setNull(8, -6);
		}
		else {
			this.invoice_detailStmt.setShort(8, source);
		}
		if ( source_id == null ) {
			this.invoice_detailStmt.setNull(9, 4);
		}
		else {
			this.invoice_detailStmt.setInt(9, source_id);
		}
		if ( taxable_base == null ) {
			this.invoice_detailStmt.setNull(10, 8);
		}
		else {
			this.invoice_detailStmt.setDouble(10, taxable_base);
		}
		if ( taxes == null ) {
			this.invoice_detailStmt.setNull(11, 8);
		}
		else {
			this.invoice_detailStmt.setDouble(11, taxes);
		}
		if ( workplace == null ) {
			this.invoice_detailStmt.setNull(12, 4);
		}
		else {
			this.invoice_detailStmt.setInt(12, workplace);
		}

		this.invoice_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_instructorStmt;
	
	private void initCourse_instructorStmt()
	throws SQLException {
		this.course_instructorStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_instructor ( course, employee, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_instructorStmt()
	throws SQLException {
		course_instructorStmt.close();
	}	
	/**
	 * Course_instructor
	 * @param course Identificador del Curso
	 * @param employee Identificador del Profesor
	 * @param type Tipo de Profesor
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_instructor(Integer course, Integer employee, Short type)
	throws SQLException {
	
		if ( course == null ) {
			this.course_instructorStmt.setNull(1, 4);
		}
		else {
			this.course_instructorStmt.setInt(1, course);
		}
		if ( employee == null ) {
			this.course_instructorStmt.setNull(2, 4);
		}
		else {
			this.course_instructorStmt.setInt(2, employee);
		}
		if ( type == null ) {
			this.course_instructorStmt.setNull(3, -6);
		}
		else {
			this.course_instructorStmt.setShort(3, type);
		}

		this.course_instructorStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_instructorStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_entry_finance_trackingStmt;
	
	private void initAccount_entry_finance_trackingStmt()
	throws SQLException {
		this.account_entry_finance_trackingStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_finance_tracking ( account_entry, finance_tracking) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_entry_finance_trackingStmt()
	throws SQLException {
		account_entry_finance_trackingStmt.close();
	}	
	/**
	 * Account_entry_finance_tracking
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_finance_tracking(Integer account_entry, Integer finance_tracking)
	throws SQLException {
	
		if ( account_entry == null ) {
			this.account_entry_finance_trackingStmt.setNull(1, 4);
		}
		else {
			this.account_entry_finance_trackingStmt.setInt(1, account_entry);
		}
		if ( finance_tracking == null ) {
			this.account_entry_finance_trackingStmt.setNull(2, 4);
		}
		else {
			this.account_entry_finance_trackingStmt.setInt(2, finance_tracking);
		}

		this.account_entry_finance_trackingStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_entry_finance_trackingStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement income_detailStmt;
	
	private void initIncome_detailStmt()
	throws SQLException {
		this.income_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO income_detail ( income, line, item, description, warehouse, quantity, price, discount_expr, type, source, purchase_detail) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeIncome_detailStmt()
	throws SQLException {
		income_detailStmt.close();
	}	
	/**
	 * Income_detail
	 * @param income Identificador del Albaran de Compra
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param type Tipo de Detalle de Albaran
	 * @param source Origen del Detalle de Albaran
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra asociado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncome_detail(Integer income, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Short type, Short source, Integer purchase_detail)
	throws SQLException {
	
		if ( income == null ) {
			this.income_detailStmt.setNull(1, 4);
		}
		else {
			this.income_detailStmt.setInt(1, income);
		}
		if ( line == null ) {
			this.income_detailStmt.setNull(2, 5);
		}
		else {
			this.income_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.income_detailStmt.setNull(3, 4);
		}
		else {
			this.income_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.income_detailStmt.setNull(4, 12);
		}
		else {
			this.income_detailStmt.setString(4, description);
		}
		if ( warehouse == null ) {
			this.income_detailStmt.setNull(5, 4);
		}
		else {
			this.income_detailStmt.setInt(5, warehouse);
		}
		if ( quantity == null ) {
			this.income_detailStmt.setNull(6, 8);
		}
		else {
			this.income_detailStmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			this.income_detailStmt.setNull(7, 8);
		}
		else {
			this.income_detailStmt.setDouble(7, price);
		}
		if ( discount_expr == null ) {
			this.income_detailStmt.setNull(8, 12);
		}
		else {
			this.income_detailStmt.setString(8, discount_expr);
		}
		if ( type == null ) {
			this.income_detailStmt.setNull(9, -6);
		}
		else {
			this.income_detailStmt.setShort(9, type);
		}
		if ( source == null ) {
			this.income_detailStmt.setNull(10, -6);
		}
		else {
			this.income_detailStmt.setShort(10, source);
		}
		if ( purchase_detail == null ) {
			this.income_detailStmt.setNull(11, 4);
		}
		else {
			this.income_detailStmt.setInt(11, purchase_detail);
		}

		this.income_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.income_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rmediaStmt;
	
	private void initRmediaStmt()
	throws SQLException {
		this.rmediaStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rmedia ( registry, media, value, comment, administrative, commercial, technical) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRmediaStmt()
	throws SQLException {
		rmediaStmt.close();
	}	
	/**
	 * Rmedia
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param media Tipo de Medio de Contacto de la Persona o Empresa
	 * @param value Valor del Medio de Contacto de la Persona o Empresa
	 * @param comment Comentarios acerca del Medio de Contacto de la Persona o Empresa
	 * @param administrative Indica si el Contacto es de caracter administrativo
	 * @param commercial Indica si el Contacto es de caracter comercial
	 * @param technical Indica si el Contacto es de caracter tecnico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRmedia(Integer registry, Short media, String value, String comment, Boolean administrative, Boolean commercial, Boolean technical)
	throws SQLException {
	
		if ( registry == null ) {
			this.rmediaStmt.setNull(1, 4);
		}
		else {
			this.rmediaStmt.setInt(1, registry);
		}
		if ( media == null ) {
			this.rmediaStmt.setNull(2, -6);
		}
		else {
			this.rmediaStmt.setShort(2, media);
		}
		if ( value == null ) {
			this.rmediaStmt.setNull(3, 12);
		}
		else {
			this.rmediaStmt.setString(3, value);
		}
		if ( comment == null ) {
			this.rmediaStmt.setNull(4, 12);
		}
		else {
			this.rmediaStmt.setString(4, comment);
		}
		if ( administrative == null ) {
			this.rmediaStmt.setNull(5, -7);
		}
		else {
			this.rmediaStmt.setBoolean(5, administrative);
		}
		if ( commercial == null ) {
			this.rmediaStmt.setNull(6, -7);
		}
		else {
			this.rmediaStmt.setBoolean(6, commercial);
		}
		if ( technical == null ) {
			this.rmediaStmt.setNull(7, -7);
		}
		else {
			this.rmediaStmt.setBoolean(7, technical);
		}

		this.rmediaStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rmediaStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commercial_trackingStmt;
	
	private void initCommercial_trackingStmt()
	throws SQLException {
		this.commercial_trackingStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_tracking ( date, seller, target, activity, comments, status, next_commercial_tracking, end_date, offer) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommercial_trackingStmt()
	throws SQLException {
		commercial_trackingStmt.close();
	}	
	/**
	 * Commercial_tracking
	 * @param date Fecha del Seguimiento Comercial
	 * @param seller Identificador del Comercial
	 * @param target Identificador del Cliente Potencial
	 * @param activity Identificador de la Actividad Comercial
	 * @param comments Comentarios del Seguimiento Comercial
	 * @param status Estado del Seguimiento Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param end_date Fecha de cierre del Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_tracking(Date date, Integer seller, Integer target, Integer activity, String comments, Short status, Integer next_commercial_tracking, Date end_date, Integer offer)
	throws SQLException {
	
		if ( date == null ) {
			this.commercial_trackingStmt.setNull(1, 91);
		}
		else {
			this.commercial_trackingStmt.setDate(1, date);
		}
		if ( seller == null ) {
			this.commercial_trackingStmt.setNull(2, 4);
		}
		else {
			this.commercial_trackingStmt.setInt(2, seller);
		}
		if ( target == null ) {
			this.commercial_trackingStmt.setNull(3, 4);
		}
		else {
			this.commercial_trackingStmt.setInt(3, target);
		}
		if ( activity == null ) {
			this.commercial_trackingStmt.setNull(4, 4);
		}
		else {
			this.commercial_trackingStmt.setInt(4, activity);
		}
		if ( comments == null ) {
			this.commercial_trackingStmt.setNull(5, 12);
		}
		else {
			this.commercial_trackingStmt.setString(5, comments);
		}
		if ( status == null ) {
			this.commercial_trackingStmt.setNull(6, -6);
		}
		else {
			this.commercial_trackingStmt.setShort(6, status);
		}
		if ( next_commercial_tracking == null ) {
			this.commercial_trackingStmt.setNull(7, 4);
		}
		else {
			this.commercial_trackingStmt.setInt(7, next_commercial_tracking);
		}
		if ( end_date == null ) {
			this.commercial_trackingStmt.setNull(8, 91);
		}
		else {
			this.commercial_trackingStmt.setDate(8, end_date);
		}
		if ( offer == null ) {
			this.commercial_trackingStmt.setNull(9, 4);
		}
		else {
			this.commercial_trackingStmt.setInt(9, offer);
		}

		this.commercial_trackingStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commercial_trackingStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement raddressStmt;
	
	private void initRaddressStmt()
	throws SQLException {
		this.raddressStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO raddress ( registry, type, recipient, street_type, address, address2, address3, zip, city, geozone) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRaddressStmt()
	throws SQLException {
		raddressStmt.close();
	}	
	/**
	 * Raddress
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param type Tipo de Direccion
	 * @param recipient Destinatario
	 * @param street_type Tipo de via
	 * @param address Primera parte de la Direccion
	 * @param address2 Segunda parte de la Direccion
	 * @param address3 Tercera parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRaddress(Integer registry, Short type, String recipient, Short street_type, String address, String address2, String address3, String zip, String city, Integer geozone)
	throws SQLException {
	
		if ( registry == null ) {
			this.raddressStmt.setNull(1, 4);
		}
		else {
			this.raddressStmt.setInt(1, registry);
		}
		if ( type == null ) {
			this.raddressStmt.setNull(2, -6);
		}
		else {
			this.raddressStmt.setShort(2, type);
		}
		if ( recipient == null ) {
			this.raddressStmt.setNull(3, 12);
		}
		else {
			this.raddressStmt.setString(3, recipient);
		}
		if ( street_type == null ) {
			this.raddressStmt.setNull(4, -6);
		}
		else {
			this.raddressStmt.setShort(4, street_type);
		}
		if ( address == null ) {
			this.raddressStmt.setNull(5, 12);
		}
		else {
			this.raddressStmt.setString(5, address);
		}
		if ( address2 == null ) {
			this.raddressStmt.setNull(6, 12);
		}
		else {
			this.raddressStmt.setString(6, address2);
		}
		if ( address3 == null ) {
			this.raddressStmt.setNull(7, 12);
		}
		else {
			this.raddressStmt.setString(7, address3);
		}
		if ( zip == null ) {
			this.raddressStmt.setNull(8, 12);
		}
		else {
			this.raddressStmt.setString(8, zip);
		}
		if ( city == null ) {
			this.raddressStmt.setNull(9, 12);
		}
		else {
			this.raddressStmt.setString(9, city);
		}
		if ( geozone == null ) {
			this.raddressStmt.setNull(10, 4);
		}
		else {
			this.raddressStmt.setInt(10, geozone);
		}

		this.raddressStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.raddressStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement actionStmt;
	
	private void initActionStmt()
	throws SQLException {
		this.actionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action ( menu, name, application_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeActionStmt()
	throws SQLException {
		actionStmt.close();
	}	
	/**
	 * Action
	 * @param menu Indica si la Accion esta o no dentro del menu
	 * @param name Nombre de la Accion
	 * @param application_id Aplicacion a la que pertenece la Accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction(Boolean menu, String name, Integer application_id)
	throws SQLException {
	
		if ( menu == null ) {
			this.actionStmt.setNull(1, -7);
		}
		else {
			this.actionStmt.setBoolean(1, menu);
		}
		if ( name == null ) {
			this.actionStmt.setNull(2, 12);
		}
		else {
			this.actionStmt.setString(2, name);
		}
		if ( application_id == null ) {
			this.actionStmt.setNull(3, 4);
		}
		else {
			this.actionStmt.setInt(3, application_id);
		}

		this.actionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.actionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement surveyStmt;
	
	private void initSurveyStmt()
	throws SQLException {
		this.surveyStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey ( active, creationDate, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSurveyStmt()
	throws SQLException {
		surveyStmt.close();
	}	
	/**
	 * Survey
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey(Boolean active, Timestamp creationDate, String description)
	throws SQLException {
	
		if ( active == null ) {
			this.surveyStmt.setNull(1, -7);
		}
		else {
			this.surveyStmt.setBoolean(1, active);
		}
		if ( creationDate == null ) {
			this.surveyStmt.setNull(2, 93);
		}
		else {
			this.surveyStmt.setTimestamp(2, creationDate);
		}
		if ( description == null ) {
			this.surveyStmt.setNull(3, 12);
		}
		else {
			this.surveyStmt.setString(3, description);
		}

		this.surveyStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.surveyStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement survey_response_detailStmt;
	
	private void initSurvey_response_detailStmt()
	throws SQLException {
		this.survey_response_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_response_detail ( value_text, value_number, value_date, question, surveyResponse) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeSurvey_response_detailStmt()
	throws SQLException {
		survey_response_detailStmt.close();
	}	
	/**
	 * Survey_response_detail
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @param question Identificador de la Pregunta
	 * @param surveyResponse Identificador de la Respuesta del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_response_detail(String value_text, Double value_number, Timestamp value_date, Integer question, Integer surveyResponse)
	throws SQLException {
	
		if ( value_text == null ) {
			this.survey_response_detailStmt.setNull(1, 12);
		}
		else {
			this.survey_response_detailStmt.setString(1, value_text);
		}
		if ( value_number == null ) {
			this.survey_response_detailStmt.setNull(2, 8);
		}
		else {
			this.survey_response_detailStmt.setDouble(2, value_number);
		}
		if ( value_date == null ) {
			this.survey_response_detailStmt.setNull(3, 93);
		}
		else {
			this.survey_response_detailStmt.setTimestamp(3, value_date);
		}
		if ( question == null ) {
			this.survey_response_detailStmt.setNull(4, 4);
		}
		else {
			this.survey_response_detailStmt.setInt(4, question);
		}
		if ( surveyResponse == null ) {
			this.survey_response_detailStmt.setNull(5, 4);
		}
		else {
			this.survey_response_detailStmt.setInt(5, surveyResponse);
		}

		this.survey_response_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.survey_response_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement raddinfoStmt;
	
	private void initRaddinfoStmt()
	throws SQLException {
		this.raddinfoStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO raddinfo ( registry, attribute, value, value_date) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRaddinfoStmt()
	throws SQLException {
		raddinfoStmt.close();
	}	
	/**
	 * Raddinfo
	 * @param registry Identificador de la Persona o Empresa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRaddinfo(Integer registry, String attribute, String value, Date value_date)
	throws SQLException {
	
		if ( registry == null ) {
			this.raddinfoStmt.setNull(1, 4);
		}
		else {
			this.raddinfoStmt.setInt(1, registry);
		}
		if ( attribute == null ) {
			this.raddinfoStmt.setNull(2, 12);
		}
		else {
			this.raddinfoStmt.setString(2, attribute);
		}
		if ( value == null ) {
			this.raddinfoStmt.setNull(3, 12);
		}
		else {
			this.raddinfoStmt.setString(3, value);
		}
		if ( value_date == null ) {
			this.raddinfoStmt.setNull(4, 91);
		}
		else {
			this.raddinfoStmt.setDate(4, value_date);
		}

		this.raddinfoStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.raddinfoStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoicing_group_detailStmt;
	
	private void initInvoicing_group_detailStmt()
	throws SQLException {
		this.invoicing_group_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoicing_group_detail ( invoicing_group, child, grouped) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoicing_group_detailStmt()
	throws SQLException {
		invoicing_group_detailStmt.close();
	}	
	/**
	 * Invoicing_group_detail
	 * @param invoicing_group Grupo de Facturacion al que pertenece
	 * @param child Componente asociado a un Grupo de Facturacion
	 * @param grouped Indica si agrupa facturas o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group_detail(Integer invoicing_group, Integer child, Boolean grouped)
	throws SQLException {
	
		if ( invoicing_group == null ) {
			this.invoicing_group_detailStmt.setNull(1, 4);
		}
		else {
			this.invoicing_group_detailStmt.setInt(1, invoicing_group);
		}
		if ( child == null ) {
			this.invoicing_group_detailStmt.setNull(2, 4);
		}
		else {
			this.invoicing_group_detailStmt.setInt(2, child);
		}
		if ( grouped == null ) {
			this.invoicing_group_detailStmt.setNull(3, -7);
		}
		else {
			this.invoicing_group_detailStmt.setBoolean(3, grouped);
		}

		this.invoicing_group_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoicing_group_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commission_itemStmt;
	
	private void initCommission_itemStmt()
	throws SQLException {
		this.commission_itemStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_item ( commission, item, quantity, amount, rate) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommission_itemStmt()
	throws SQLException {
		commission_itemStmt.close();
	}	
	/**
	 * Commission_item
	 * @param commission Identificador de la Comision
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param amount Importe de la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_item(Integer commission, Integer item, Double quantity, Double amount, Double rate)
	throws SQLException {
	
		if ( commission == null ) {
			this.commission_itemStmt.setNull(1, 4);
		}
		else {
			this.commission_itemStmt.setInt(1, commission);
		}
		if ( item == null ) {
			this.commission_itemStmt.setNull(2, 4);
		}
		else {
			this.commission_itemStmt.setInt(2, item);
		}
		if ( quantity == null ) {
			this.commission_itemStmt.setNull(3, 8);
		}
		else {
			this.commission_itemStmt.setDouble(3, quantity);
		}
		if ( amount == null ) {
			this.commission_itemStmt.setNull(4, 8);
		}
		else {
			this.commission_itemStmt.setDouble(4, amount);
		}
		if ( rate == null ) {
			this.commission_itemStmt.setNull(5, 8);
		}
		else {
			this.commission_itemStmt.setDouble(5, rate);
		}

		this.commission_itemStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commission_itemStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement academic_skillStmt;
	
	private void initAcademic_skillStmt()
	throws SQLException {
		this.academic_skillStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO academic_skill ( code, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAcademic_skillStmt()
	throws SQLException {
		academic_skillStmt.close();
	}	
	/**
	 * Academic_skill
	 * @param code Codigo de la Aptitud Academica
	 * @param description Descripcion de la Aptitud Academica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAcademic_skill(String code, String description)
	throws SQLException {
	
		if ( code == null ) {
			this.academic_skillStmt.setNull(1, 1);
		}
		else {
			this.academic_skillStmt.setString(1, code);
		}
		if ( description == null ) {
			this.academic_skillStmt.setNull(2, 12);
		}
		else {
			this.academic_skillStmt.setString(2, description);
		}

		this.academic_skillStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.academic_skillStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement appraiserStmt;
	
	private void initAppraiserStmt()
	throws SQLException {
		this.appraiserStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO appraiser ( registry) VALUES ( ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAppraiserStmt()
	throws SQLException {
		appraiserStmt.close();
	}	
	/**
	 * Appraiser
	 * @param registry Registro del Perito
	 * @throws SQLException
	*/
	protected void insertAppraiser(Integer registry)
	throws SQLException {
	
		if ( registry == null ) {
			this.appraiserStmt.setNull(1, 4);
		}
		else {
			this.appraiserStmt.setInt(1, registry);
		}

		this.appraiserStmt.executeUpdate();
		
	}
	
	private PreparedStatement catalogue_itemStmt;
	
	private void initCatalogue_itemStmt()
	throws SQLException {
		this.catalogue_itemStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue_item ( catalogue, item, quantity, price, discount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCatalogue_itemStmt()
	throws SQLException {
		catalogue_itemStmt.close();
	}	
	/**
	 * Catalogue_item
	 * @param catalogue Identificador del Catalogo
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica el precio o descuento
	 * @param price Precio del Articulo en el Catalogo
	 * @param discount Descuento del Articulo en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCatalogue_item(Integer catalogue, Integer item, Double quantity, Double price, Double discount)
	throws SQLException {
	
		if ( catalogue == null ) {
			this.catalogue_itemStmt.setNull(1, 4);
		}
		else {
			this.catalogue_itemStmt.setInt(1, catalogue);
		}
		if ( item == null ) {
			this.catalogue_itemStmt.setNull(2, 4);
		}
		else {
			this.catalogue_itemStmt.setInt(2, item);
		}
		if ( quantity == null ) {
			this.catalogue_itemStmt.setNull(3, 8);
		}
		else {
			this.catalogue_itemStmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			this.catalogue_itemStmt.setNull(4, 8);
		}
		else {
			this.catalogue_itemStmt.setDouble(4, price);
		}
		if ( discount == null ) {
			this.catalogue_itemStmt.setNull(5, 8);
		}
		else {
			this.catalogue_itemStmt.setDouble(5, discount);
		}

		this.catalogue_itemStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.catalogue_itemStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contractStmt;
	
	private void initContractStmt()
	throws SQLException {
		this.contractStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract ( person, workplace, ccc, start_date, end_date, document, status) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContractStmt()
	throws SQLException {
		contractStmt.close();
	}	
	/**
	 * Contract
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @param ccc Identificador de la Cuota de Cotizacion
	 * @param start_date Fecha de inicio del Contrato
	 * @param end_date Fecha de finalizacion del Contrato
	 * @param document Impreso (.pdf) del comtrato.
	 * @param status Estado de notificacion del contrato
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract(Integer person, Integer workplace, Integer ccc, Date start_date, Date end_date, InputStream document, Short status)
	throws SQLException {
	
		if ( person == null ) {
			this.contractStmt.setNull(1, 4);
		}
		else {
			this.contractStmt.setInt(1, person);
		}
		if ( workplace == null ) {
			this.contractStmt.setNull(2, 4);
		}
		else {
			this.contractStmt.setInt(2, workplace);
		}
		if ( ccc == null ) {
			this.contractStmt.setNull(3, 4);
		}
		else {
			this.contractStmt.setInt(3, ccc);
		}
		if ( start_date == null ) {
			this.contractStmt.setNull(4, 91);
		}
		else {
			this.contractStmt.setDate(4, start_date);
		}
		if ( end_date == null ) {
			this.contractStmt.setNull(5, 91);
		}
		else {
			this.contractStmt.setDate(5, end_date);
		}
		if ( document == null ) {
			this.contractStmt.setNull(6, -4);
		}
		else {
			this.contractStmt.setBinaryStream(6, document);
		}
		if ( status == null ) {
			this.contractStmt.setNull(7, -6);
		}
		else {
			this.contractStmt.setShort(7, status);
		}

		this.contractStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contractStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement offer_detailStmt;
	
	private void initOffer_detailStmt()
	throws SQLException {
		this.offer_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_detail ( offer, line, item, description, quantity, price, discount_expr, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeOffer_detailStmt()
	throws SQLException {
		offer_detailStmt.close();
	}	
	/**
	 * Offer_detail
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de lùnea del Detalle dentro del Presupuesto
	 * @param item Identificador del Articulo
	 * @param description Descripciùn del Articulo
	 * @param quantity Cantidad del Articulo
	 * @param price Precio del Articulo
	 * @param discount_expr Descuentos del Articulo
	 * @param status Estado del Detalle del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_detail(Integer offer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short status)
	throws SQLException {
	
		if ( offer == null ) {
			this.offer_detailStmt.setNull(1, 4);
		}
		else {
			this.offer_detailStmt.setInt(1, offer);
		}
		if ( line == null ) {
			this.offer_detailStmt.setNull(2, 5);
		}
		else {
			this.offer_detailStmt.setInt(2, line);
		}
		if ( item == null ) {
			this.offer_detailStmt.setNull(3, 4);
		}
		else {
			this.offer_detailStmt.setInt(3, item);
		}
		if ( description == null ) {
			this.offer_detailStmt.setNull(4, 12);
		}
		else {
			this.offer_detailStmt.setString(4, description);
		}
		if ( quantity == null ) {
			this.offer_detailStmt.setNull(5, 8);
		}
		else {
			this.offer_detailStmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			this.offer_detailStmt.setNull(6, 8);
		}
		else {
			this.offer_detailStmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			this.offer_detailStmt.setNull(7, 12);
		}
		else {
			this.offer_detailStmt.setString(7, discount_expr);
		}
		if ( status == null ) {
			this.offer_detailStmt.setNull(8, -6);
		}
		else {
			this.offer_detailStmt.setShort(8, status);
		}

		this.offer_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.offer_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement expendituresStmt;
	
	private void initExpendituresStmt()
	throws SQLException {
		this.expendituresStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expenditures ( resource, expenditures_item, date, amount) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeExpendituresStmt()
	throws SQLException {
		expendituresStmt.close();
	}	
	/**
	 * Expenditures
	 * @param resource Identificador del Recurso de Empresa
	 * @param expenditures_item Identificador del Tipo de Coste
	 * @param date Fecha del Coste
	 * @param amount Importe del Coste
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpenditures(Integer resource, Integer expenditures_item, Date date, Double amount)
	throws SQLException {
	
		if ( resource == null ) {
			this.expendituresStmt.setNull(1, 4);
		}
		else {
			this.expendituresStmt.setInt(1, resource);
		}
		if ( expenditures_item == null ) {
			this.expendituresStmt.setNull(2, 4);
		}
		else {
			this.expendituresStmt.setInt(2, expenditures_item);
		}
		if ( date == null ) {
			this.expendituresStmt.setNull(3, 91);
		}
		else {
			this.expendituresStmt.setDate(3, date);
		}
		if ( amount == null ) {
			this.expendituresStmt.setNull(4, 8);
		}
		else {
			this.expendituresStmt.setDouble(4, amount);
		}

		this.expendituresStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.expendituresStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement amortizationStmt;
	
	private void initAmortizationStmt()
	throws SQLException {
		this.amortizationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization ( description, amortization_type, initial_date, deadline, amount, fee_period, sale_amount, comments, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAmortizationStmt()
	throws SQLException {
		amortizationStmt.close();
	}	
	/**
	 * Amortization
	 * @param description Descripcion del inmovilizado
	 * @param amortization_type Tipo de Amortizacion
	 * @param initial_date Fecha de inicio de la Amortizacion
	 * @param deadline Fecha de baja de la Amortizacion
	 * @param amount Importe a amortizar.
	 * @param fee_period Periodo de las cuotas de Amortizacion
	 * @param sale_amount Importe de la venta
	 * @param comments Comentarios
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de Amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la Amortizacion
	 * @param percentage Porcentaje de Amortizacion
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAmortization(String description, Integer amortization_type, Date initial_date, Date deadline, Double amount, Short fee_period, Double sale_amount, InputStream comments, String fixed_asset_account, String accumulated_account, String allocation_account, Double percentage, Short security_level)
	throws SQLException {
	
		if ( description == null ) {
			this.amortizationStmt.setNull(1, 12);
		}
		else {
			this.amortizationStmt.setString(1, description);
		}
		if ( amortization_type == null ) {
			this.amortizationStmt.setNull(2, 4);
		}
		else {
			this.amortizationStmt.setInt(2, amortization_type);
		}
		if ( initial_date == null ) {
			this.amortizationStmt.setNull(3, 91);
		}
		else {
			this.amortizationStmt.setDate(3, initial_date);
		}
		if ( deadline == null ) {
			this.amortizationStmt.setNull(4, 91);
		}
		else {
			this.amortizationStmt.setDate(4, deadline);
		}
		if ( amount == null ) {
			this.amortizationStmt.setNull(5, 8);
		}
		else {
			this.amortizationStmt.setDouble(5, amount);
		}
		if ( fee_period == null ) {
			this.amortizationStmt.setNull(6, -6);
		}
		else {
			this.amortizationStmt.setShort(6, fee_period);
		}
		if ( sale_amount == null ) {
			this.amortizationStmt.setNull(7, 8);
		}
		else {
			this.amortizationStmt.setDouble(7, sale_amount);
		}
		if ( comments == null ) {
			this.amortizationStmt.setNull(8, -1);
		}
		else {
			this.amortizationStmt.setAsciiStream(8, comments);
		}
		if ( fixed_asset_account == null ) {
			this.amortizationStmt.setNull(9, 1);
		}
		else {
			this.amortizationStmt.setString(9, fixed_asset_account);
		}
		if ( accumulated_account == null ) {
			this.amortizationStmt.setNull(10, 1);
		}
		else {
			this.amortizationStmt.setString(10, accumulated_account);
		}
		if ( allocation_account == null ) {
			this.amortizationStmt.setNull(11, 1);
		}
		else {
			this.amortizationStmt.setString(11, allocation_account);
		}
		if ( percentage == null ) {
			this.amortizationStmt.setNull(12, 8);
		}
		else {
			this.amortizationStmt.setDouble(12, percentage);
		}
		if ( security_level == null ) {
			this.amortizationStmt.setNull(13, -6);
		}
		else {
			this.amortizationStmt.setShort(13, security_level);
		}

		this.amortizationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.amortizationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement enterpriseStmt;
	
	private void initEnterpriseStmt()
	throws SQLException {
		this.enterpriseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise ( registry, scope) VALUES ( ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEnterpriseStmt()
	throws SQLException {
		enterpriseStmt.close();
	}	
	/**
	 * Enterprise
	 * @param registry Registro de la Empresa
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertEnterprise(Integer registry, Integer scope)
	throws SQLException {
	
		if ( registry == null ) {
			this.enterpriseStmt.setNull(1, 4);
		}
		else {
			this.enterpriseStmt.setInt(1, registry);
		}
		if ( scope == null ) {
			this.enterpriseStmt.setNull(2, 4);
		}
		else {
			this.enterpriseStmt.setInt(2, scope);
		}

		this.enterpriseStmt.executeUpdate();
		
	}
	
	private PreparedStatement markStmt;
	
	private void initMarkStmt()
	throws SQLException {
		this.markStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mark ( subject, alumn, evaluation, mark) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMarkStmt()
	throws SQLException {
		markStmt.close();
	}	
	/**
	 * Mark
	 * @param subject Identificador de Asignatura
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de evaluacion
	 * @param mark Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMark(Integer subject, Integer alumn, Short evaluation, Double mark)
	throws SQLException {
	
		if ( subject == null ) {
			this.markStmt.setNull(1, 4);
		}
		else {
			this.markStmt.setInt(1, subject);
		}
		if ( alumn == null ) {
			this.markStmt.setNull(2, 4);
		}
		else {
			this.markStmt.setInt(2, alumn);
		}
		if ( evaluation == null ) {
			this.markStmt.setNull(3, -6);
		}
		else {
			this.markStmt.setShort(3, evaluation);
		}
		if ( mark == null ) {
			this.markStmt.setNull(4, 8);
		}
		else {
			this.markStmt.setDouble(4, mark);
		}

		this.markStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.markStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement expense_accountStmt;
	
	private void initExpense_accountStmt()
	throws SQLException {
		this.expense_accountStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense_account ( registry, expense_holder_type, status, issue_date, description, comments) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeExpense_accountStmt()
	throws SQLException {
		expense_accountStmt.close();
	}	
	/**
	 * Expense_account
	 * @param registry Registry que realiza los Gastos
	 * @param expense_holder_type Tipo de Registry
	 * @param status Estado del Gasto
	 * @param issue_date Fecha del Gasto
	 * @param description Descripcion del Gasto
	 * @param comments Comentarios acerca del Gasto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpense_account(Integer registry, Short expense_holder_type, Short status, Date issue_date, String description, InputStream comments)
	throws SQLException {
	
		if ( registry == null ) {
			this.expense_accountStmt.setNull(1, 4);
		}
		else {
			this.expense_accountStmt.setInt(1, registry);
		}
		if ( expense_holder_type == null ) {
			this.expense_accountStmt.setNull(2, -6);
		}
		else {
			this.expense_accountStmt.setShort(2, expense_holder_type);
		}
		if ( status == null ) {
			this.expense_accountStmt.setNull(3, -6);
		}
		else {
			this.expense_accountStmt.setShort(3, status);
		}
		if ( issue_date == null ) {
			this.expense_accountStmt.setNull(4, 91);
		}
		else {
			this.expense_accountStmt.setDate(4, issue_date);
		}
		if ( description == null ) {
			this.expense_accountStmt.setNull(5, 12);
		}
		else {
			this.expense_accountStmt.setString(5, description);
		}
		if ( comments == null ) {
			this.expense_accountStmt.setNull(6, -1);
		}
		else {
			this.expense_accountStmt.setAsciiStream(6, comments);
		}

		this.expense_accountStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.expense_accountStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement catalogue_categoryStmt;
	
	private void initCatalogue_categoryStmt()
	throws SQLException {
		this.catalogue_categoryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue_category ( catalogue, category, quantity, discount) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCatalogue_categoryStmt()
	throws SQLException {
		catalogue_categoryStmt.close();
	}	
	/**
	 * Catalogue_category
	 * @param catalogue Identificador del Catalogo
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica el descuento
	 * @param discount Descuento de la Categoria en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCatalogue_category(Integer catalogue, Integer category, Double quantity, Double discount)
	throws SQLException {
	
		if ( catalogue == null ) {
			this.catalogue_categoryStmt.setNull(1, 4);
		}
		else {
			this.catalogue_categoryStmt.setInt(1, catalogue);
		}
		if ( category == null ) {
			this.catalogue_categoryStmt.setNull(2, 4);
		}
		else {
			this.catalogue_categoryStmt.setInt(2, category);
		}
		if ( quantity == null ) {
			this.catalogue_categoryStmt.setNull(3, 8);
		}
		else {
			this.catalogue_categoryStmt.setDouble(3, quantity);
		}
		if ( discount == null ) {
			this.catalogue_categoryStmt.setNull(4, 8);
		}
		else {
			this.catalogue_categoryStmt.setDouble(4, discount);
		}

		this.catalogue_categoryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.catalogue_categoryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement lh_contractStmt;
	
	private void initLh_contractStmt()
	throws SQLException {
		this.lh_contractStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_contract ( employee, startingdate, endingdate, contract_type, gross_salary) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLh_contractStmt()
	throws SQLException {
		lh_contractStmt.close();
	}	
	/**
	 * Lh_contract
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Contrato
	 * @param endingdate Fecha de finalizacion del Contrato
	 * @param contract_type Tipo de Contrato
	 * @param gross_salary Salario bruto del Contrato
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLh_contract(Integer employee, Date startingdate, Date endingdate, Short contract_type, Double gross_salary)
	throws SQLException {
	
		if ( employee == null ) {
			this.lh_contractStmt.setNull(1, 4);
		}
		else {
			this.lh_contractStmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			this.lh_contractStmt.setNull(2, 91);
		}
		else {
			this.lh_contractStmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			this.lh_contractStmt.setNull(3, 91);
		}
		else {
			this.lh_contractStmt.setDate(3, endingdate);
		}
		if ( contract_type == null ) {
			this.lh_contractStmt.setNull(4, -6);
		}
		else {
			this.lh_contractStmt.setShort(4, contract_type);
		}
		if ( gross_salary == null ) {
			this.lh_contractStmt.setNull(5, 8);
		}
		else {
			this.lh_contractStmt.setDouble(5, gross_salary);
		}

		this.lh_contractStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.lh_contractStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_summaryStmt;
	
	private void initAccount_summaryStmt()
	throws SQLException {
		this.account_summaryStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_summary ( account_period, account, security_level, entry_date, debit, credit) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_summaryStmt()
	throws SQLException {
		account_summaryStmt.close();
	}	
	/**
	 * Account_summary
	 * @param account_period Ejercicio Contable del Acumulado
	 * @param account Cuenta Contable del Acumulado
	 * @param security_level Nivel de seguridad del Acumulado
	 * @param entry_date Fecha del Acumulado
	 * @param debit Debe del Acumulado
	 * @param credit Haber del Acumulado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_summary(String account_period, String account, Short security_level, Date entry_date, Double debit, Double credit)
	throws SQLException {
	
		if ( account_period == null ) {
			this.account_summaryStmt.setNull(1, 1);
		}
		else {
			this.account_summaryStmt.setString(1, account_period);
		}
		if ( account == null ) {
			this.account_summaryStmt.setNull(2, 1);
		}
		else {
			this.account_summaryStmt.setString(2, account);
		}
		if ( security_level == null ) {
			this.account_summaryStmt.setNull(3, -6);
		}
		else {
			this.account_summaryStmt.setShort(3, security_level);
		}
		if ( entry_date == null ) {
			this.account_summaryStmt.setNull(4, 91);
		}
		else {
			this.account_summaryStmt.setDate(4, entry_date);
		}
		if ( debit == null ) {
			this.account_summaryStmt.setNull(5, 8);
		}
		else {
			this.account_summaryStmt.setDouble(5, debit);
		}
		if ( credit == null ) {
			this.account_summaryStmt.setNull(6, 8);
		}
		else {
			this.account_summaryStmt.setDouble(6, credit);
		}

		this.account_summaryStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_summaryStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement lh_courseStmt;
	
	private void initLh_courseStmt()
	throws SQLException {
		this.lh_courseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_course ( employee, startingdate, endingdate, description) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLh_courseStmt()
	throws SQLException {
		lh_courseStmt.close();
	}	
	/**
	 * Lh_course
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Curso
	 * @param endingdate Fecha de finalizacion del Curso
	 * @param description Descripcion del Curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLh_course(Integer employee, Date startingdate, Date endingdate, String description)
	throws SQLException {
	
		if ( employee == null ) {
			this.lh_courseStmt.setNull(1, 4);
		}
		else {
			this.lh_courseStmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			this.lh_courseStmt.setNull(2, 91);
		}
		else {
			this.lh_courseStmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			this.lh_courseStmt.setNull(3, 91);
		}
		else {
			this.lh_courseStmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			this.lh_courseStmt.setNull(4, 12);
		}
		else {
			this.lh_courseStmt.setString(4, description);
		}

		this.lh_courseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.lh_courseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement companyStmt;
	
	private void initCompanyStmt()
	throws SQLException {
		this.companyStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO company ( active, surcharge, withholding, e_invoice) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCompanyStmt()
	throws SQLException {
		companyStmt.close();
	}	
	/**
	 * Company
	 * @param active Indica si la Compaùia es activa o inactiva
	 * @param surcharge Indica si la Compaùia tiene de recargo de equivalencia
	 * @param withholding Indica si la Compaùia aplica retencion de impuestos
	 * @param e_invoice Indica si la Compaùia desea emitir Facturas electronicas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCompany(Boolean active, Boolean surcharge, Boolean withholding, Boolean e_invoice)
	throws SQLException {
	
		if ( active == null ) {
			this.companyStmt.setNull(1, -7);
		}
		else {
			this.companyStmt.setBoolean(1, active);
		}
		if ( surcharge == null ) {
			this.companyStmt.setNull(2, -7);
		}
		else {
			this.companyStmt.setBoolean(2, surcharge);
		}
		if ( withholding == null ) {
			this.companyStmt.setNull(3, -7);
		}
		else {
			this.companyStmt.setBoolean(3, withholding);
		}
		if ( e_invoice == null ) {
			this.companyStmt.setNull(4, -7);
		}
		else {
			this.companyStmt.setBoolean(4, e_invoice);
		}

		this.companyStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.companyStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement lh_workStmt;
	
	private void initLh_workStmt()
	throws SQLException {
		this.lh_workStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_work ( employee, startingdate, endingdate, description) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeLh_workStmt()
	throws SQLException {
		lh_workStmt.close();
	}	
	/**
	 * Lh_work
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Trabajo
	 * @param endingdate Fecha de finalizacion del Trabajo
	 * @param description Descripcion del Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLh_work(Integer employee, Date startingdate, Date endingdate, String description)
	throws SQLException {
	
		if ( employee == null ) {
			this.lh_workStmt.setNull(1, 4);
		}
		else {
			this.lh_workStmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			this.lh_workStmt.setNull(2, 91);
		}
		else {
			this.lh_workStmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			this.lh_workStmt.setNull(3, 91);
		}
		else {
			this.lh_workStmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			this.lh_workStmt.setNull(4, 12);
		}
		else {
			this.lh_workStmt.setString(4, description);
		}

		this.lh_workStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.lh_workStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement scaleStmt;
	
	private void initScaleStmt()
	throws SQLException {
		this.scaleStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scale ( scale_model, program_path, inidate, enddate, serie, code1, code2, code3, code4, code5, verified) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeScaleStmt()
	throws SQLException {
		scaleStmt.close();
	}	
	/**
	 * Scale
	 * @param scale_model Modelo de Balanza
	 * @param program_path Ruta donde se encuentra la base de datos de la Balanza
	 * @param inidate Fecha de inicio de Transferencia de datos
	 * @param enddate Fecha de fin de Transferencia de datos
	 * @param serie Serie de Albaran para la importacion de albaranes
	 * @param code1 Primer codigo de control de Balanza
	 * @param code2 Segundo codigo de control de Balanza
	 * @param code3 Tercer codigo de control de Balanza
	 * @param code4 Cuarto codigo de control de Balanza
	 * @param code5 Quinto codigo de control de la Balanza
	 * @param verified Indica si esta verificado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertScale(Short scale_model, String program_path, Date inidate, Date enddate, String serie, String code1, String code2, String code3, String code4, String code5, Boolean verified)
	throws SQLException {
	
		if ( scale_model == null ) {
			this.scaleStmt.setNull(1, -6);
		}
		else {
			this.scaleStmt.setShort(1, scale_model);
		}
		if ( program_path == null ) {
			this.scaleStmt.setNull(2, 12);
		}
		else {
			this.scaleStmt.setString(2, program_path);
		}
		if ( inidate == null ) {
			this.scaleStmt.setNull(3, 91);
		}
		else {
			this.scaleStmt.setDate(3, inidate);
		}
		if ( enddate == null ) {
			this.scaleStmt.setNull(4, 91);
		}
		else {
			this.scaleStmt.setDate(4, enddate);
		}
		if ( serie == null ) {
			this.scaleStmt.setNull(5, 1);
		}
		else {
			this.scaleStmt.setString(5, serie);
		}
		if ( code1 == null ) {
			this.scaleStmt.setNull(6, 12);
		}
		else {
			this.scaleStmt.setString(6, code1);
		}
		if ( code2 == null ) {
			this.scaleStmt.setNull(7, 12);
		}
		else {
			this.scaleStmt.setString(7, code2);
		}
		if ( code3 == null ) {
			this.scaleStmt.setNull(8, 12);
		}
		else {
			this.scaleStmt.setString(8, code3);
		}
		if ( code4 == null ) {
			this.scaleStmt.setNull(9, 12);
		}
		else {
			this.scaleStmt.setString(9, code4);
		}
		if ( code5 == null ) {
			this.scaleStmt.setNull(10, 12);
		}
		else {
			this.scaleStmt.setString(10, code5);
		}
		if ( verified == null ) {
			this.scaleStmt.setNull(11, -7);
		}
		else {
			this.scaleStmt.setBoolean(11, verified);
		}

		this.scaleStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.scaleStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement commission_type_commissionStmt;
	
	private void initCommission_type_commissionStmt()
	throws SQLException {
		this.commission_type_commissionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_type_commission ( commission_type, commission) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCommission_type_commissionStmt()
	throws SQLException {
		commission_type_commissionStmt.close();
	}	
	/**
	 * Commission_type_commission
	 * @param commission_type Identificador del Tipo de Comision
	 * @param commission Identificador de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_type_commission(Integer commission_type, Integer commission)
	throws SQLException {
	
		if ( commission_type == null ) {
			this.commission_type_commissionStmt.setNull(1, 4);
		}
		else {
			this.commission_type_commissionStmt.setInt(1, commission_type);
		}
		if ( commission == null ) {
			this.commission_type_commissionStmt.setNull(2, 4);
		}
		else {
			this.commission_type_commissionStmt.setInt(2, commission);
		}

		this.commission_type_commissionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.commission_type_commissionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement inventory_detailStmt;
	
	private void initInventory_detailStmt()
	throws SQLException {
		this.inventory_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO inventory_detail ( inventory, item, actual_quantity, real_quantity, cost) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInventory_detailStmt()
	throws SQLException {
		inventory_detailStmt.close();
	}	
	/**
	 * Inventory_detail
	 * @param inventory Identificador de Inventario
	 * @param item Articulo Inventariado
	 * @param actual_quantity Cantidad actual del Articulo Inventariado
	 * @param real_quantity Cantidad real del Articulo Inventariado
	 * @param cost Coste del Articulo Inventariado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInventory_detail(Integer inventory, Integer item, Double actual_quantity, Double real_quantity, Double cost)
	throws SQLException {
	
		if ( inventory == null ) {
			this.inventory_detailStmt.setNull(1, 4);
		}
		else {
			this.inventory_detailStmt.setInt(1, inventory);
		}
		if ( item == null ) {
			this.inventory_detailStmt.setNull(2, 4);
		}
		else {
			this.inventory_detailStmt.setInt(2, item);
		}
		if ( actual_quantity == null ) {
			this.inventory_detailStmt.setNull(3, 8);
		}
		else {
			this.inventory_detailStmt.setDouble(3, actual_quantity);
		}
		if ( real_quantity == null ) {
			this.inventory_detailStmt.setNull(4, 8);
		}
		else {
			this.inventory_detailStmt.setDouble(4, real_quantity);
		}
		if ( cost == null ) {
			this.inventory_detailStmt.setNull(5, 8);
		}
		else {
			this.inventory_detailStmt.setDouble(5, cost);
		}

		this.inventory_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.inventory_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement scopeStmt;
	
	private void initScopeStmt()
	throws SQLException {
		this.scopeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scope ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeScopeStmt()
	throws SQLException {
		scopeStmt.close();
	}	
	/**
	 * Scope
	 * @param description Descripcion del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertScope(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.scopeStmt.setNull(1, 12);
		}
		else {
			this.scopeStmt.setString(1, description);
		}

		this.scopeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.scopeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement target_supplierStmt;
	
	private void initTarget_supplierStmt()
	throws SQLException {
		this.target_supplierStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_supplier ( target, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTarget_supplierStmt()
	throws SQLException {
		target_supplierStmt.close();
	}	
	/**
	 * Target_supplier
	 * @param target Identificador del Cliente Potencial
	 * @param supplier Identificador del Proveedor
	 * @param target_external_code Codigo del Cliente Potencial para el Proveedor
	 * @param tariff Identificador de Tarifa
	 * @param pay_method Identificador de la Forma de Pago
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_supplier(Integer target, Integer supplier, String target_external_code, Integer tariff, Integer pay_method, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
	
		if ( target == null ) {
			this.target_supplierStmt.setNull(1, 4);
		}
		else {
			this.target_supplierStmt.setInt(1, target);
		}
		if ( supplier == null ) {
			this.target_supplierStmt.setNull(2, 4);
		}
		else {
			this.target_supplierStmt.setInt(2, supplier);
		}
		if ( target_external_code == null ) {
			this.target_supplierStmt.setNull(3, 12);
		}
		else {
			this.target_supplierStmt.setString(3, target_external_code);
		}
		if ( tariff == null ) {
			this.target_supplierStmt.setNull(4, 4);
		}
		else {
			this.target_supplierStmt.setInt(4, tariff);
		}
		if ( pay_method == null ) {
			this.target_supplierStmt.setNull(5, 4);
		}
		else {
			this.target_supplierStmt.setInt(5, pay_method);
		}
		if ( number_of_pymnts == null ) {
			this.target_supplierStmt.setNull(6, 5);
		}
		else {
			this.target_supplierStmt.setInt(6, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.target_supplierStmt.setNull(7, 5);
		}
		else {
			this.target_supplierStmt.setInt(7, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.target_supplierStmt.setNull(8, 5);
		}
		else {
			this.target_supplierStmt.setInt(8, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.target_supplierStmt.setNull(9, 12);
		}
		else {
			this.target_supplierStmt.setString(9, pymnt_days);
		}
		if ( bank == null ) {
			this.target_supplierStmt.setNull(10, 4);
		}
		else {
			this.target_supplierStmt.setInt(10, bank);
		}
		if ( bank_account == null ) {
			this.target_supplierStmt.setNull(11, 12);
		}
		else {
			this.target_supplierStmt.setString(11, bank_account);
		}

		this.target_supplierStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.target_supplierStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement customer_segmentStmt;
	
	private void initCustomer_segmentStmt()
	throws SQLException {
		this.customer_segmentStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_segment ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCustomer_segmentStmt()
	throws SQLException {
		customer_segmentStmt.close();
	}	
	/**
	 * Customer_segment
	 * @param description Descripcion del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_segment(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.customer_segmentStmt.setNull(1, 12);
		}
		else {
			this.customer_segmentStmt.setString(1, description);
		}

		this.customer_segmentStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.customer_segmentStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement contract_batch_detailStmt;
	
	private void initContract_batch_detailStmt()
	throws SQLException {
		this.contract_batch_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_batch_detail ( contract_batch, contract) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeContract_batch_detailStmt()
	throws SQLException {
		contract_batch_detailStmt.close();
	}	
	/**
	 * Contract_batch_detail
	 * @param contract_batch Identificador unico de la remesa de contratos
	 * @param contract Identificador unico del contrato
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_batch_detail(Integer contract_batch, Integer contract)
	throws SQLException {
	
		if ( contract_batch == null ) {
			this.contract_batch_detailStmt.setNull(1, 4);
		}
		else {
			this.contract_batch_detailStmt.setInt(1, contract_batch);
		}
		if ( contract == null ) {
			this.contract_batch_detailStmt.setNull(2, 4);
		}
		else {
			this.contract_batch_detailStmt.setInt(2, contract);
		}

		this.contract_batch_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.contract_batch_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement auto_conceptStmt;
	
	private void initAuto_conceptStmt()
	throws SQLException {
		this.auto_conceptStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO auto_concept ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAuto_conceptStmt()
	throws SQLException {
		auto_conceptStmt.close();
	}	
	/**
	 * Auto_concept
	 * @param description Descripcion del Concepto Automatico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAuto_concept(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.auto_conceptStmt.setNull(1, 1);
		}
		else {
			this.auto_conceptStmt.setString(1, description);
		}

		this.auto_conceptStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.auto_conceptStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement web_info_page_detailStmt;
	
	private void initWeb_info_page_detailStmt()
	throws SQLException {
		this.web_info_page_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page_detail ( web_info_page, title, layout, content, extra) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeWeb_info_page_detailStmt()
	throws SQLException {
		web_info_page_detailStmt.close();
	}	
	/**
	 * Web_info_page_detail
	 * @param web_info_page Identificador de la Pagina a la que corresponde el detalle
	 * @param title Titulo del contenido de la Pagina
	 * @param layout Tipo de plantilla
	 * @param content Texto del contenido de la Pagina
	 * @param extra Campo reservado a otros datos de la Pagina
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_page_detail(Integer web_info_page, String title, Integer layout, InputStream content, String extra)
	throws SQLException {
	
		if ( web_info_page == null ) {
			this.web_info_page_detailStmt.setNull(1, 4);
		}
		else {
			this.web_info_page_detailStmt.setInt(1, web_info_page);
		}
		if ( title == null ) {
			this.web_info_page_detailStmt.setNull(2, 12);
		}
		else {
			this.web_info_page_detailStmt.setString(2, title);
		}
		if ( layout == null ) {
			this.web_info_page_detailStmt.setNull(3, 4);
		}
		else {
			this.web_info_page_detailStmt.setInt(3, layout);
		}
		if ( content == null ) {
			this.web_info_page_detailStmt.setNull(4, -1);
		}
		else {
			this.web_info_page_detailStmt.setAsciiStream(4, content);
		}
		if ( extra == null ) {
			this.web_info_page_detailStmt.setNull(5, 12);
		}
		else {
			this.web_info_page_detailStmt.setString(5, extra);
		}

		this.web_info_page_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.web_info_page_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement job_typeStmt;
	
	private void initJob_typeStmt()
	throws SQLException {
		this.job_typeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO job_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeJob_typeStmt()
	throws SQLException {
		job_typeStmt.close();
	}	
	/**
	 * Job_type
	 * @param description Descripcion del Tipo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertJob_type(String description)
	throws SQLException {
	
		if ( description == null ) {
			this.job_typeStmt.setNull(1, 12);
		}
		else {
			this.job_typeStmt.setString(1, description);
		}

		this.job_typeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.job_typeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement scale_relationStmt;
	
	private void initScale_relationStmt()
	throws SQLException {
		this.scale_relationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scale_relation ( aon_id, scale_id1, scale_id2, type, scale_model) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeScale_relationStmt()
	throws SQLException {
		scale_relationStmt.close();
	}	
	/**
	 * Scale_relation
	 * @param aon_id Identificador de la tabla en Aon
	 * @param scale_id1 Primer identificador de la tabla en la Balanza
	 * @param scale_id2 Segundo identificador de la tabla en la Balanza
	 * @param type Indica el tipo de tabla que se esta relacionando
	 * @param scale_model Modelo de Balanza
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertScale_relation(Integer aon_id, String scale_id1, String scale_id2, String type, Short scale_model)
	throws SQLException {
	
		if ( aon_id == null ) {
			this.scale_relationStmt.setNull(1, 4);
		}
		else {
			this.scale_relationStmt.setInt(1, aon_id);
		}
		if ( scale_id1 == null ) {
			this.scale_relationStmt.setNull(2, 12);
		}
		else {
			this.scale_relationStmt.setString(2, scale_id1);
		}
		if ( scale_id2 == null ) {
			this.scale_relationStmt.setNull(3, 12);
		}
		else {
			this.scale_relationStmt.setString(3, scale_id2);
		}
		if ( type == null ) {
			this.scale_relationStmt.setNull(4, 1);
		}
		else {
			this.scale_relationStmt.setString(4, type);
		}
		if ( scale_model == null ) {
			this.scale_relationStmt.setNull(5, -6);
		}
		else {
			this.scale_relationStmt.setShort(5, scale_model);
		}

		this.scale_relationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.scale_relationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement campaign_dossierStmt;
	
	private void initCampaign_dossierStmt()
	throws SQLException {
		this.campaign_dossierStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO campaign_dossier ( campaign, dossier) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCampaign_dossierStmt()
	throws SQLException {
		campaign_dossierStmt.close();
	}	
	/**
	 * Campaign_dossier
	 * @param campaign Identificador de la Campaùa
	 * @param dossier Identificador del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign_dossier(Integer campaign, Integer dossier)
	throws SQLException {
	
		if ( campaign == null ) {
			this.campaign_dossierStmt.setNull(1, 4);
		}
		else {
			this.campaign_dossierStmt.setInt(1, campaign);
		}
		if ( dossier == null ) {
			this.campaign_dossierStmt.setNull(2, 4);
		}
		else {
			this.campaign_dossierStmt.setInt(2, dossier);
		}

		this.campaign_dossierStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.campaign_dossierStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement tariffStmt;
	
	private void initTariffStmt()
	throws SQLException {
		this.tariffStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tariff ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTariffStmt()
	throws SQLException {
		tariffStmt.close();
	}	
	/**
	 * Tariff
	 * @param name Nombre de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTariff(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.tariffStmt.setNull(1, 12);
		}
		else {
			this.tariffStmt.setString(1, name);
		}

		this.tariffStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.tariffStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement deductionStmt;
	
	private void initDeductionStmt()
	throws SQLException {
		this.deductionStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO deduction ( type, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeDeductionStmt()
	throws SQLException {
		deductionStmt.close();
	}	
	/**
	 * Deduction
	 * @param type Tipo de DeducciÛn
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDeduction(Short type, String description)
	throws SQLException {
	
		if ( type == null ) {
			this.deductionStmt.setNull(1, -6);
		}
		else {
			this.deductionStmt.setShort(1, type);
		}
		if ( description == null ) {
			this.deductionStmt.setNull(2, 12);
		}
		else {
			this.deductionStmt.setString(2, description);
		}

		this.deductionStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.deductionStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement invoice_addressStmt;
	
	private void initInvoice_addressStmt()
	throws SQLException {
		this.invoice_addressStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_address ( invoice, address, address2, zip, city, geozone) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeInvoice_addressStmt()
	throws SQLException {
		invoice_addressStmt.close();
	}	
	/**
	 * Invoice_address
	 * @param invoice Identificador de la Factura
	 * @param address Primera parte de la Direccion
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_address(Integer invoice, String address, String address2, String zip, String city, Integer geozone)
	throws SQLException {
	
		if ( invoice == null ) {
			this.invoice_addressStmt.setNull(1, 4);
		}
		else {
			this.invoice_addressStmt.setInt(1, invoice);
		}
		if ( address == null ) {
			this.invoice_addressStmt.setNull(2, 12);
		}
		else {
			this.invoice_addressStmt.setString(2, address);
		}
		if ( address2 == null ) {
			this.invoice_addressStmt.setNull(3, 12);
		}
		else {
			this.invoice_addressStmt.setString(3, address2);
		}
		if ( zip == null ) {
			this.invoice_addressStmt.setNull(4, 12);
		}
		else {
			this.invoice_addressStmt.setString(4, zip);
		}
		if ( city == null ) {
			this.invoice_addressStmt.setNull(5, 12);
		}
		else {
			this.invoice_addressStmt.setString(5, city);
		}
		if ( geozone == null ) {
			this.invoice_addressStmt.setNull(6, 4);
		}
		else {
			this.invoice_addressStmt.setInt(6, geozone);
		}

		this.invoice_addressStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.invoice_addressStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement message_logStmt;
	
	private void initMessage_logStmt()
	throws SQLException {
		this.message_logStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO message_log ( message_id, message_content, recipient, type, sent_date, message_parts, username) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMessage_logStmt()
	throws SQLException {
		message_logStmt.close();
	}	
	/**
	 * Message_log
	 * @param message_id Identificador del Mensaje para el servidor de Esendex
	 * @param message_content Identificador del Contenido del Mensaje
	 * @param recipient Destinatario del Mensaje
	 * @param type Tipo de Mensaje
	 * @param sent_date Fecha y hora de envio del Mensaje
	 * @param message_parts Numero de partes que componen el Mensaje
	 * @param username Usuario que envia el mensaje
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMessage_log(String message_id, Integer message_content, String recipient, String type, Timestamp sent_date, Short message_parts, String username)
	throws SQLException {
	
		if ( message_id == null ) {
			this.message_logStmt.setNull(1, 12);
		}
		else {
			this.message_logStmt.setString(1, message_id);
		}
		if ( message_content == null ) {
			this.message_logStmt.setNull(2, 4);
		}
		else {
			this.message_logStmt.setInt(2, message_content);
		}
		if ( recipient == null ) {
			this.message_logStmt.setNull(3, 12);
		}
		else {
			this.message_logStmt.setString(3, recipient);
		}
		if ( type == null ) {
			this.message_logStmt.setNull(4, 12);
		}
		else {
			this.message_logStmt.setString(4, type);
		}
		if ( sent_date == null ) {
			this.message_logStmt.setNull(5, 93);
		}
		else {
			this.message_logStmt.setTimestamp(5, sent_date);
		}
		if ( message_parts == null ) {
			this.message_logStmt.setNull(6, -6);
		}
		else {
			this.message_logStmt.setShort(6, message_parts);
		}
		if ( username == null ) {
			this.message_logStmt.setNull(7, 12);
		}
		else {
			this.message_logStmt.setString(7, username);
		}

		this.message_logStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.message_logStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement applicationStmt;
	
	private void initApplicationStmt()
	throws SQLException {
		this.applicationStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO application ( audit_level, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeApplicationStmt()
	throws SQLException {
		applicationStmt.close();
	}	
	/**
	 * Application
	 * @param audit_level Nivel de auditoria
	 * @param name Nombre de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertApplication(Short audit_level, String name)
	throws SQLException {
	
		if ( audit_level == null ) {
			this.applicationStmt.setNull(1, -6);
		}
		else {
			this.applicationStmt.setShort(1, audit_level);
		}
		if ( name == null ) {
			this.applicationStmt.setNull(2, 12);
		}
		else {
			this.applicationStmt.setString(2, name);
		}

		this.applicationStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.applicationStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement rdir_staffStmt;
	
	private void initRdir_staffStmt()
	throws SQLException {
		this.rdir_staffStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rdir_staff ( registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeRdir_staffStmt()
	throws SQLException {
		rdir_staffStmt.close();
	}	
	/**
	 * Rdir_staff
	 * @param registry Identificador de la Empresa
	 * @param document Numero de Documento del Directivo
	 * @param name Nombre del Directivo
	 * @param shareholder Indica si el Directivo es socio
	 * @param representative Indica si el Directivo es representante legal
	 * @param director Indica si el Directivo es administrador
	 * @param percent_share Porcentaje de acciones (solo para socios)
	 * @param share_number Numero de Acciones
	 * @param nominal_value Valor Nominal
	 * @param due_date Fecha de vencimiento del cargo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRdir_staff(Integer registry, String document, String name, Boolean shareholder, Boolean representative, Boolean director, Double percent_share, Integer share_number, Double nominal_value, Date due_date)
	throws SQLException {
	
		if ( registry == null ) {
			this.rdir_staffStmt.setNull(1, 4);
		}
		else {
			this.rdir_staffStmt.setInt(1, registry);
		}
		if ( document == null ) {
			this.rdir_staffStmt.setNull(2, 12);
		}
		else {
			this.rdir_staffStmt.setString(2, document);
		}
		if ( name == null ) {
			this.rdir_staffStmt.setNull(3, 12);
		}
		else {
			this.rdir_staffStmt.setString(3, name);
		}
		if ( shareholder == null ) {
			this.rdir_staffStmt.setNull(4, -7);
		}
		else {
			this.rdir_staffStmt.setBoolean(4, shareholder);
		}
		if ( representative == null ) {
			this.rdir_staffStmt.setNull(5, -7);
		}
		else {
			this.rdir_staffStmt.setBoolean(5, representative);
		}
		if ( director == null ) {
			this.rdir_staffStmt.setNull(6, -7);
		}
		else {
			this.rdir_staffStmt.setBoolean(6, director);
		}
		if ( percent_share == null ) {
			this.rdir_staffStmt.setNull(7, 8);
		}
		else {
			this.rdir_staffStmt.setDouble(7, percent_share);
		}
		if ( share_number == null ) {
			this.rdir_staffStmt.setNull(8, 4);
		}
		else {
			this.rdir_staffStmt.setInt(8, share_number);
		}
		if ( nominal_value == null ) {
			this.rdir_staffStmt.setNull(9, 8);
		}
		else {
			this.rdir_staffStmt.setDouble(9, nominal_value);
		}
		if ( due_date == null ) {
			this.rdir_staffStmt.setNull(10, 91);
		}
		else {
			this.rdir_staffStmt.setDate(10, due_date);
		}

		this.rdir_staffStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.rdir_staffStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement taskStmt;
	
	private void initTaskStmt()
	throws SQLException {
		this.taskStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO task ( description, start_date, end_date, due_date, priority, status, percent, user_id, workgroup, source, dossier, activity, sender, comments, repeat_period) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTaskStmt()
	throws SQLException {
		taskStmt.close();
	}	
	/**
	 * Task
	 * @param description Descripcion de la Tarea
	 * @param start_date Fecha de inicio de la Tarea
	 * @param end_date Fecha de finalizacion de la Tarea
	 * @param due_date Fecha de vencimiento de la Tarea
	 * @param priority Prioridad de la Tarea
	 * @param status Estado de la Tarea
	 * @param percent Porcentaje de realizacion de la Tarea
	 * @param user_id Identificador del Usuario asociado a la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo asociado a la Tarea
	 * @param source Origen de la Tarea
	 * @param dossier Identificador del Expediente
	 * @param activity Identificador de la Actividad
	 * @param sender Remitente de la Tarea
	 * @param comments Comentarios de la Tarea
	 * @param repeat_period Periodo de repeticion de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTask(String description, Date start_date, Date end_date, Date due_date, Short priority, Short status, Short percent, Integer user_id, Integer workgroup, Short source, Integer dossier, Integer activity, Integer sender, InputStream comments, Short repeat_period)
	throws SQLException {
	
		if ( description == null ) {
			this.taskStmt.setNull(1, 12);
		}
		else {
			this.taskStmt.setString(1, description);
		}
		if ( start_date == null ) {
			this.taskStmt.setNull(2, 91);
		}
		else {
			this.taskStmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			this.taskStmt.setNull(3, 91);
		}
		else {
			this.taskStmt.setDate(3, end_date);
		}
		if ( due_date == null ) {
			this.taskStmt.setNull(4, 91);
		}
		else {
			this.taskStmt.setDate(4, due_date);
		}
		if ( priority == null ) {
			this.taskStmt.setNull(5, -6);
		}
		else {
			this.taskStmt.setShort(5, priority);
		}
		if ( status == null ) {
			this.taskStmt.setNull(6, -6);
		}
		else {
			this.taskStmt.setShort(6, status);
		}
		if ( percent == null ) {
			this.taskStmt.setNull(7, -6);
		}
		else {
			this.taskStmt.setShort(7, percent);
		}
		if ( user_id == null ) {
			this.taskStmt.setNull(8, 4);
		}
		else {
			this.taskStmt.setInt(8, user_id);
		}
		if ( workgroup == null ) {
			this.taskStmt.setNull(9, 4);
		}
		else {
			this.taskStmt.setInt(9, workgroup);
		}
		if ( source == null ) {
			this.taskStmt.setNull(10, -6);
		}
		else {
			this.taskStmt.setShort(10, source);
		}
		if ( dossier == null ) {
			this.taskStmt.setNull(11, 4);
		}
		else {
			this.taskStmt.setInt(11, dossier);
		}
		if ( activity == null ) {
			this.taskStmt.setNull(12, 4);
		}
		else {
			this.taskStmt.setInt(12, activity);
		}
		if ( sender == null ) {
			this.taskStmt.setNull(13, 4);
		}
		else {
			this.taskStmt.setInt(13, sender);
		}
		if ( comments == null ) {
			this.taskStmt.setNull(14, -1);
		}
		else {
			this.taskStmt.setAsciiStream(14, comments);
		}
		if ( repeat_period == null ) {
			this.taskStmt.setNull(15, -6);
		}
		else {
			this.taskStmt.setShort(15, repeat_period);
		}

		this.taskStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.taskStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement resourceStmt;
	
	private void initResourceStmt()
	throws SQLException {
		this.resourceStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO resource ( employee, workplace, workactivity, startingdate, endingdate) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeResourceStmt()
	throws SQLException {
		resourceStmt.close();
	}	
	/**
	 * Resource
	 * @param employee Identificador del Empleado
	 * @param workplace Identificador del Centro de Trabajo
	 * @param workactivity Identificador de la Actividad
	 * @param startingdate Fecha inicial del Recurso
	 * @param endingdate Fecha final del Recurso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertResource(Integer employee, Integer workplace, Integer workactivity, Date startingdate, Date endingdate)
	throws SQLException {
	
		if ( employee == null ) {
			this.resourceStmt.setNull(1, 4);
		}
		else {
			this.resourceStmt.setInt(1, employee);
		}
		if ( workplace == null ) {
			this.resourceStmt.setNull(2, 4);
		}
		else {
			this.resourceStmt.setInt(2, workplace);
		}
		if ( workactivity == null ) {
			this.resourceStmt.setNull(3, 4);
		}
		else {
			this.resourceStmt.setInt(3, workactivity);
		}
		if ( startingdate == null ) {
			this.resourceStmt.setNull(4, 91);
		}
		else {
			this.resourceStmt.setDate(4, startingdate);
		}
		if ( endingdate == null ) {
			this.resourceStmt.setNull(5, 91);
		}
		else {
			this.resourceStmt.setDate(5, endingdate);
		}

		this.resourceStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.resourceStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement offerStmt;
	
	private void initOfferStmt()
	throws SQLException {
		this.offerStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer ( target, series, number, version, address, tariff, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account, signed, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeOfferStmt()
	throws SQLException {
		offerStmt.close();
	}	
	/**
	 * Offer
	 * @param target Identificador del Cliente Potencial
	 * @param series Serie del Presupuesto
	 * @param number Numero del Presupuesto
	 * @param version Numero de version de Presupuesto
	 * @param address Identificador de la Direccion de envio del Presupuesto
	 * @param tariff Identificador de la Tarifa del Presupuesto
	 * @param seller Agente Comercial del Presupuesto
	 * @param supplier Identificador del Proveedor
	 * @param discount_expr Descuentos del Presupuesto
	 * @param issue_date Fecha de emision del Presupuesto
	 * @param pay_method Forma de Pago del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @param status Estado del Presupuesto
	 * @param type Tipo de Presupuesto
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Presupuesto
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @param signed Indica si el Presupuesto esta firmada electronicamente
	 * @param comments Comentarios del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer(Integer target, String series, Integer number, Integer version, Integer address, Integer tariff, Integer seller, Integer supplier, String discount_expr, Date issue_date, Integer pay_method, Short security_level, Short status, Short type, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account, Boolean signed, InputStream comments)
	throws SQLException {
	
		if ( target == null ) {
			this.offerStmt.setNull(1, 4);
		}
		else {
			this.offerStmt.setInt(1, target);
		}
		if ( series == null ) {
			this.offerStmt.setNull(2, 1);
		}
		else {
			this.offerStmt.setString(2, series);
		}
		if ( number == null ) {
			this.offerStmt.setNull(3, 4);
		}
		else {
			this.offerStmt.setInt(3, number);
		}
		if ( version == null ) {
			this.offerStmt.setNull(4, 5);
		}
		else {
			this.offerStmt.setInt(4, version);
		}
		if ( address == null ) {
			this.offerStmt.setNull(5, 4);
		}
		else {
			this.offerStmt.setInt(5, address);
		}
		if ( tariff == null ) {
			this.offerStmt.setNull(6, 4);
		}
		else {
			this.offerStmt.setInt(6, tariff);
		}
		if ( seller == null ) {
			this.offerStmt.setNull(7, 4);
		}
		else {
			this.offerStmt.setInt(7, seller);
		}
		if ( supplier == null ) {
			this.offerStmt.setNull(8, 4);
		}
		else {
			this.offerStmt.setInt(8, supplier);
		}
		if ( discount_expr == null ) {
			this.offerStmt.setNull(9, 12);
		}
		else {
			this.offerStmt.setString(9, discount_expr);
		}
		if ( issue_date == null ) {
			this.offerStmt.setNull(10, 91);
		}
		else {
			this.offerStmt.setDate(10, issue_date);
		}
		if ( pay_method == null ) {
			this.offerStmt.setNull(11, 4);
		}
		else {
			this.offerStmt.setInt(11, pay_method);
		}
		if ( security_level == null ) {
			this.offerStmt.setNull(12, -6);
		}
		else {
			this.offerStmt.setShort(12, security_level);
		}
		if ( status == null ) {
			this.offerStmt.setNull(13, -6);
		}
		else {
			this.offerStmt.setShort(13, status);
		}
		if ( type == null ) {
			this.offerStmt.setNull(14, -6);
		}
		else {
			this.offerStmt.setShort(14, type);
		}
		if ( workplace == null ) {
			this.offerStmt.setNull(15, 4);
		}
		else {
			this.offerStmt.setInt(15, workplace);
		}
		if ( scope == null ) {
			this.offerStmt.setNull(16, 4);
		}
		else {
			this.offerStmt.setInt(16, scope);
		}
		if ( number_of_pymnts == null ) {
			this.offerStmt.setNull(17, 5);
		}
		else {
			this.offerStmt.setInt(17, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			this.offerStmt.setNull(18, 5);
		}
		else {
			this.offerStmt.setInt(18, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			this.offerStmt.setNull(19, 5);
		}
		else {
			this.offerStmt.setInt(19, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			this.offerStmt.setNull(20, 12);
		}
		else {
			this.offerStmt.setString(20, pymnt_days);
		}
		if ( bank == null ) {
			this.offerStmt.setNull(21, 4);
		}
		else {
			this.offerStmt.setInt(21, bank);
		}
		if ( bank_account == null ) {
			this.offerStmt.setNull(22, 12);
		}
		else {
			this.offerStmt.setString(22, bank_account);
		}
		if ( signed == null ) {
			this.offerStmt.setNull(23, -7);
		}
		else {
			this.offerStmt.setBoolean(23, signed);
		}
		if ( comments == null ) {
			this.offerStmt.setNull(24, -1);
		}
		else {
			this.offerStmt.setAsciiStream(24, comments);
		}

		this.offerStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.offerStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement composition_expenseStmt;
	
	private void initComposition_expenseStmt()
	throws SQLException {
		this.composition_expenseStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition_expense ( composition, description, quantity, price) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeComposition_expenseStmt()
	throws SQLException {
		composition_expenseStmt.close();
	}	
	/**
	 * Composition_expense
	 * @param composition Identificador de la Composicion
	 * @param description Descripcion del Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Importe del Gasto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertComposition_expense(Integer composition, String description, Double quantity, Double price)
	throws SQLException {
	
		if ( composition == null ) {
			this.composition_expenseStmt.setNull(1, 4);
		}
		else {
			this.composition_expenseStmt.setInt(1, composition);
		}
		if ( description == null ) {
			this.composition_expenseStmt.setNull(2, 12);
		}
		else {
			this.composition_expenseStmt.setString(2, description);
		}
		if ( quantity == null ) {
			this.composition_expenseStmt.setNull(3, 8);
		}
		else {
			this.composition_expenseStmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			this.composition_expenseStmt.setNull(4, 8);
		}
		else {
			this.composition_expenseStmt.setDouble(4, price);
		}

		this.composition_expenseStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.composition_expenseStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement account_helperStmt;
	
	private void initAccount_helperStmt()
	throws SQLException {
		this.account_helperStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_helper ( counter, account, balancing_account) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeAccount_helperStmt()
	throws SQLException {
		account_helperStmt.close();
	}	
	/**
	 * Account_helper
	 * @param counter Contador, veces que se ha usado
	 * @param account Cuenta contable
	 * @param balancing_account Contrapartida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_helper(Integer counter, String account, String balancing_account)
	throws SQLException {
	
		if ( counter == null ) {
			this.account_helperStmt.setNull(1, 4);
		}
		else {
			this.account_helperStmt.setInt(1, counter);
		}
		if ( account == null ) {
			this.account_helperStmt.setNull(2, 1);
		}
		else {
			this.account_helperStmt.setString(2, account);
		}
		if ( balancing_account == null ) {
			this.account_helperStmt.setNull(3, 1);
		}
		else {
			this.account_helperStmt.setString(3, balancing_account);
		}

		this.account_helperStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.account_helperStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement course_academicskillStmt;
	
	private void initCourse_academicskillStmt()
	throws SQLException {
		this.course_academicskillStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_academicskill ( course, academic_skill, weight) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeCourse_academicskillStmt()
	throws SQLException {
		course_academicskillStmt.close();
	}	
	/**
	 * Course_academicskill
	 * @param course Curso
	 * @param academic_skill Aptitud Academica
	 * @param weight Peso de la Aptitud para calcular la Nota media
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_academicskill(Integer course, Integer academic_skill, Integer weight)
	throws SQLException {
	
		if ( course == null ) {
			this.course_academicskillStmt.setNull(1, 4);
		}
		else {
			this.course_academicskillStmt.setInt(1, course);
		}
		if ( academic_skill == null ) {
			this.course_academicskillStmt.setNull(2, 4);
		}
		else {
			this.course_academicskillStmt.setInt(2, academic_skill);
		}
		if ( weight == null ) {
			this.course_academicskillStmt.setNull(3, 4);
		}
		else {
			this.course_academicskillStmt.setInt(3, weight);
		}

		this.course_academicskillStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.course_academicskillStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement enterprise_cccStmt;
	
	private void initEnterprise_cccStmt()
	throws SQLException {
		this.enterprise_cccStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_ccc ( ccc, type, enterprise_activity, geozone) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeEnterprise_cccStmt()
	throws SQLException {
		enterprise_cccStmt.close();
	}	
	/**
	 * Enterprise_ccc
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param type Tipo de Cuenta Cotizacion
	 * @param enterprise_activity Identificador de la Actividad de Empresa
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_ccc(String ccc, Short type, Integer enterprise_activity, Integer geozone)
	throws SQLException {
	
		if ( ccc == null ) {
			this.enterprise_cccStmt.setNull(1, 1);
		}
		else {
			this.enterprise_cccStmt.setString(1, ccc);
		}
		if ( type == null ) {
			this.enterprise_cccStmt.setNull(2, -6);
		}
		else {
			this.enterprise_cccStmt.setShort(2, type);
		}
		if ( enterprise_activity == null ) {
			this.enterprise_cccStmt.setNull(3, 4);
		}
		else {
			this.enterprise_cccStmt.setInt(3, enterprise_activity);
		}
		if ( geozone == null ) {
			this.enterprise_cccStmt.setNull(4, 4);
		}
		else {
			this.enterprise_cccStmt.setInt(4, geozone);
		}

		this.enterprise_cccStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.enterprise_cccStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement targetStmt;
	
	private void initTargetStmt()
	throws SQLException {
		this.targetStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target ( registry, tariff, advertising, surcharge, withholding, transaction, status) VALUES ( ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeTargetStmt()
	throws SQLException {
		targetStmt.close();
	}	
	/**
	 * Target
	 * @param registry Registro del Cliente Potencial
	 * @param tariff Tarifa asociada al Cliente Potencial
	 * @param advertising Admision de Publicidad
	 * @param surcharge Indica si el Cliente Potencial tiene recargo de equivalencia
	 * @param withholding Indica si el Cliente Potencial aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Cliente Potencial
	 * @param status Estado del Cliente Potencial
	 * @throws SQLException
	*/
	protected void insertTarget(Integer registry, Integer tariff, Short advertising, Boolean surcharge, Boolean withholding, Short transaction, Short status)
	throws SQLException {
	
		if ( registry == null ) {
			this.targetStmt.setNull(1, 4);
		}
		else {
			this.targetStmt.setInt(1, registry);
		}
		if ( tariff == null ) {
			this.targetStmt.setNull(2, 4);
		}
		else {
			this.targetStmt.setInt(2, tariff);
		}
		if ( advertising == null ) {
			this.targetStmt.setNull(3, -6);
		}
		else {
			this.targetStmt.setShort(3, advertising);
		}
		if ( surcharge == null ) {
			this.targetStmt.setNull(4, -7);
		}
		else {
			this.targetStmt.setBoolean(4, surcharge);
		}
		if ( withholding == null ) {
			this.targetStmt.setNull(5, -7);
		}
		else {
			this.targetStmt.setBoolean(5, withholding);
		}
		if ( transaction == null ) {
			this.targetStmt.setNull(6, -6);
		}
		else {
			this.targetStmt.setShort(6, transaction);
		}
		if ( status == null ) {
			this.targetStmt.setNull(7, -6);
		}
		else {
			this.targetStmt.setShort(7, status);
		}

		this.targetStmt.executeUpdate();
		
	}
	
	private PreparedStatement mk_campaignStmt;
	
	private void initMk_campaignStmt()
	throws SQLException {
		this.mk_campaignStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_campaign ( active, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeMk_campaignStmt()
	throws SQLException {
		mk_campaignStmt.close();
	}	
	/**
	 * Mk_campaign
	 * @param active Indica si la Campaùa esta activa o no
	 * @param description Descripcion de la Campaùa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_campaign(Boolean active, String description)
	throws SQLException {
	
		if ( active == null ) {
			this.mk_campaignStmt.setNull(1, -7);
		}
		else {
			this.mk_campaignStmt.setBoolean(1, active);
		}
		if ( description == null ) {
			this.mk_campaignStmt.setNull(2, 12);
		}
		else {
			this.mk_campaignStmt.setString(2, description);
		}

		this.mk_campaignStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.mk_campaignStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement activity_processStmt;
	
	private void initActivity_processStmt()
	throws SQLException {
		this.activity_processStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity_process ( campaign, activity, process_detail, task) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeActivity_processStmt()
	throws SQLException {
		activity_processStmt.close();
	}	
	/**
	 * Activity_process
	 * @param campaign Identificador de la Campaùa
	 * @param activity Identificador de la Actividad
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity_process(Integer campaign, Integer activity, Integer process_detail, Integer task)
	throws SQLException {
	
		if ( campaign == null ) {
			this.activity_processStmt.setNull(1, 4);
		}
		else {
			this.activity_processStmt.setInt(1, campaign);
		}
		if ( activity == null ) {
			this.activity_processStmt.setNull(2, 4);
		}
		else {
			this.activity_processStmt.setInt(2, activity);
		}
		if ( process_detail == null ) {
			this.activity_processStmt.setNull(3, 4);
		}
		else {
			this.activity_processStmt.setInt(3, process_detail);
		}
		if ( task == null ) {
			this.activity_processStmt.setNull(4, 4);
		}
		else {
			this.activity_processStmt.setInt(4, task);
		}

		this.activity_processStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.activity_processStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement item_alternativeStmt;
	
	private void initItem_alternativeStmt()
	throws SQLException {
		this.item_alternativeStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_alternative ( item, alternative_item, priority) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeItem_alternativeStmt()
	throws SQLException {
		item_alternativeStmt.close();
	}	
	/**
	 * Item_alternative
	 * @param item Identificador de Articulo
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param priority Prioridad del Articulo Alternativo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_alternative(Integer item, Integer alternative_item, Short priority)
	throws SQLException {
	
		if ( item == null ) {
			this.item_alternativeStmt.setNull(1, 4);
		}
		else {
			this.item_alternativeStmt.setInt(1, item);
		}
		if ( alternative_item == null ) {
			this.item_alternativeStmt.setNull(2, 4);
		}
		else {
			this.item_alternativeStmt.setInt(2, alternative_item);
		}
		if ( priority == null ) {
			this.item_alternativeStmt.setNull(3, -6);
		}
		else {
			this.item_alternativeStmt.setShort(3, priority);
		}

		this.item_alternativeStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.item_alternativeStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement userStmt;
	
	private void initUserStmt()
	throws SQLException {
		this.userStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user ( name, login, available, validate, aon_key, status) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeUserStmt()
	throws SQLException {
		userStmt.close();
	}	
	/**
	 * User
	 * @param name Nombre del Usuario
	 * @param login Login del Usuario
	 * @param available Indica si el Usuario esta disponible o no
	 * @param validate Indica si el Usuario requiere validacion o no de la clave hardware
	 * @param aon_key Campo alfanumerico donde se guarda la ultima clave hardware generada
	 * @param status Estado del Usuario con respecto a su primera validacion de la clave hardware
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser(String name, String login, Boolean available, Boolean validate, String aon_key, Short status)
	throws SQLException {
	
		if ( name == null ) {
			this.userStmt.setNull(1, 12);
		}
		else {
			this.userStmt.setString(1, name);
		}
		if ( login == null ) {
			this.userStmt.setNull(2, 12);
		}
		else {
			this.userStmt.setString(2, login);
		}
		if ( available == null ) {
			this.userStmt.setNull(3, -7);
		}
		else {
			this.userStmt.setBoolean(3, available);
		}
		if ( validate == null ) {
			this.userStmt.setNull(4, -7);
		}
		else {
			this.userStmt.setBoolean(4, validate);
		}
		if ( aon_key == null ) {
			this.userStmt.setNull(5, 12);
		}
		else {
			this.userStmt.setString(5, aon_key);
		}
		if ( status == null ) {
			this.userStmt.setNull(6, -6);
		}
		else {
			this.userStmt.setShort(6, status);
		}

		this.userStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.userStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement expense_account_detailStmt;
	
	private void initExpense_account_detailStmt()
	throws SQLException {
		this.expense_account_detailStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense_account_detail ( expense_account, expense, quantity, price, amount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeExpense_account_detailStmt()
	throws SQLException {
		expense_account_detailStmt.close();
	}	
	/**
	 * Expense_account_detail
	 * @param expense_account Identidicador del Gasto
	 * @param expense Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Precio unitario del Gasto
	 * @param amount Precio total del Gasto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpense_account_detail(Integer expense_account, Integer expense, Double quantity, Double price, Double amount)
	throws SQLException {
	
		if ( expense_account == null ) {
			this.expense_account_detailStmt.setNull(1, 4);
		}
		else {
			this.expense_account_detailStmt.setInt(1, expense_account);
		}
		if ( expense == null ) {
			this.expense_account_detailStmt.setNull(2, 4);
		}
		else {
			this.expense_account_detailStmt.setInt(2, expense);
		}
		if ( quantity == null ) {
			this.expense_account_detailStmt.setNull(3, 8);
		}
		else {
			this.expense_account_detailStmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			this.expense_account_detailStmt.setNull(4, 8);
		}
		else {
			this.expense_account_detailStmt.setDouble(4, price);
		}
		if ( amount == null ) {
			this.expense_account_detailStmt.setNull(5, 8);
		}
		else {
			this.expense_account_detailStmt.setDouble(5, amount);
		}

		this.expense_account_detailStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.expense_account_detailStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}
	
	private PreparedStatement app_paramStmt;
	
	private void initApp_paramStmt()
	throws SQLException {
		this.app_paramStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO app_param ( name, value) VALUES ( ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeApp_paramStmt()
	throws SQLException {
		app_paramStmt.close();
	}	
	/**
	 * App_param
	 * @param name Nombre del Parametro
	 * @param value Valor del Parametro
	 * @throws SQLException
	*/
	protected void insertApp_param(String name, String value)
	throws SQLException {
	
		if ( name == null ) {
			this.app_paramStmt.setNull(1, 12);
		}
		else {
			this.app_paramStmt.setString(1, name);
		}
		if ( value == null ) {
			this.app_paramStmt.setNull(2, 12);
		}
		else {
			this.app_paramStmt.setString(2, value);
		}

		this.app_paramStmt.executeUpdate();
		
	}
	
	private PreparedStatement expenditures_itemsStmt;
	
	private void initExpenditures_itemsStmt()
	throws SQLException {
		this.expenditures_itemsStmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expenditures_items ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);
	}

	private void closeExpenditures_itemsStmt()
	throws SQLException {
		expenditures_itemsStmt.close();
	}	
	/**
	 * Expenditures_items
	 * @param name Nombre del Tipo de Coste
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpenditures_items(String name)
	throws SQLException {
	
		if ( name == null ) {
			this.expenditures_itemsStmt.setNull(1, 12);
		}
		else {
			this.expenditures_itemsStmt.setString(1, name);
		}

		this.expenditures_itemsStmt.executeUpdate();
		
		int generatedKey = 0;
		ResultSet generatedKeys = this.expenditures_itemsStmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		generatedKeys.close();
		return generatedKey;
	}

	public AbstractMysqlDB( Connection mysqlConnection) 
	throws SQLException {
		this.mysqlConnection = mysqlConnection;
		initLeasingStmt();
		initDb_versionStmt();
		initComposition_detailStmt();
		initCv_evaluate_typeStmt();
		initPcategoryStmt();
		initWarehouseStmt();
		initCustomerStmt();
		initProductionStmt();
		initMessage_contentStmt();
		initPosStmt();
		initInvoice_taxStmt();
		initIattachStmt();
		initTas_itemStmt();
		initTas_offerStmt();
		initSalaryStmt();
		initCommissionStmt();
		initSupport_order_insuranceStmt();
		initSupplierStmt();
		initUser_scopeStmt();
		initAssetStmt();
		initItem_warehouseStmt();
		initRnoteStmt();
		initFavoriteStmt();
		initDaily_trackingStmt();
		initLoanStmt();
		initTas_deliveryStmt();
		initQuestion_valueStmt();
		initWeb_info_styleStmt();
		initSalary_paymentStmt();
		initContract_dataStmt();
		initCv_workexperienceStmt();
		initIncidence_typeStmt();
		initItem_posStmt();
		initEc_paymethodStmt();
		initMk_actionStmt();
		initFs_vat_detailStmt();
		initCv_languagesStmt();
		initOffer_detail_commissionStmt();
		initFavorite_categoryStmt();
		initBalance_detailStmt();
		initCnae93rev1Stmt();
		initInvoice_tax_accountStmt();
		initAccount_entry_detailStmt();
		initPcategory_treeStmt();
		initProduct_accountStmt();
		initTariff_catalogueStmt();
		initSupplier_segmentStmt();
		initAmortization_detailStmt();
		initNoticeStmt();
		initProduction_detailStmt();
		initDelivery_detailStmt();
		initAmortization_typeStmt();
		initCatalogueStmt();
		initPaymentStmt();
		initAction_entryStmt();
		initAbsenceStmt();
		initItem_tariffStmt();
		initAccount_entry_fbatchStmt();
		initSellerStmt();
		initOffer_attachStmt();
		initProcess_detailStmt();
		initRelationshipStmt();
		initAccount_budget_detailStmt();
		initAccount_entry_invoiceStmt();
		initTax_accountStmt();
		initExpenseStmt();
		initInventoryStmt();
		initCommercial_termStmt();
		initProduction_expenseStmt();
		initCommercial_activityStmt();
		initOffer_termStmt();
		initRsegmentStmt();
		initDelivery_detail_labourStmt();
		initProcess_transition_typeStmt();
		initLeasing_accountStmt();
		initCv_knowledgeStmt();
		initContract_batchStmt();
		initQuality_skillStmt();
		initEc_targetStmt();
		initBalanceStmt();
		initQualificationStmt();
		initTax_detailStmt();
		initSupplier_accountStmt();
		initCompositionStmt();
		initBankStmt();
		initActivity_typeStmt();
		initCourse_evaluationStmt();
		initAccount_entry_linkStmt();
		initSupport_orderStmt();
		initProcess_detail_transitionStmt();
		initCustomer_feeStmt();
		initPurchase_detailStmt();
		initHoliday_detailStmt();
		initPm_type_detailStmt();
		initTarget_itemStmt();
		initHolidayStmt();
		initPm_type_detail_accountStmt();
		initCommission_categoryStmt();
		initAcademic_yearStmt();
		initCreditor_accountStmt();
		initFinance_trackingStmt();
		initRattachStmt();
		initSales_detailStmt();
		initCurriculumStmt();
		initMakeStmt();
		initRbank_accountStmt();
		initModelStmt();
		initCnaeStmt();
		initDepartmentStmt();
		initTarget_profileStmt();
		initPurchaseStmt();
		initWeb_infoStmt();
		initLh_positionStmt();
		initCv_evaluateStmt();
		initCourse_alumnStmt();
		initQuestionStmt();
		initRecord_dataStmt();
		initEmployeeStmt();
		initAlumn_loanStmt();
		initActivityStmt();
		initCalendarStmt();
		initEc_offer_pay_infoStmt();
		initAsset_activityStmt();
		initCalendar_periodStmt();
		initFbatchStmt();
		initInvoice_detail_accountStmt();
		initCommission_typeStmt();
		initEnterprise_certificate_detailStmt();
		initAlarmStmt();
		initPay_methodStmt();
		initEc_catalogueStmt();
		initWorkactivityStmt();
		initEc_configStmt();
		initAccount_budgetStmt();
		initCourse_observationStmt();
		initSurvey_responseStmt();
		initEvaluation_observationStmt();
		initPersonStmt();
		initContract_paymentStmt();
		initAccount_entryStmt();
		initGeotreeStmt();
		initSales_purchaseStmt();
		initItem_supplierStmt();
		initCustomer_accountStmt();
		initDossierStmt();
		initSurvey_questionStmt();
		initCv_evaluate_summaryStmt();
		initCourseStmt();
		initStockStmt();
		initRbankStmt();
		initRpaymethodStmt();
		initSalesStmt();
		initIncomeStmt();
		initEnterprise_activityStmt();
		initWeb_info_page_resourceStmt();
		initFbatch_detailStmt();
		initCalendar_holidayStmt();
		initWorkgroupStmt();
		initCategoryStmt();
		initMk_action_targetStmt();
		initTaxStmt();
		initSessionStmt();
		initCv_studiesStmt();
		initInvoicing_groupStmt();
		initRegistryStmt();
		initDeliveryStmt();
		initCreditorStmt();
		initAccount_periodStmt();
		initSeriesStmt();
		initCourse_levelStmt();
		initWorkplaceStmt();
		initAction_deniedStmt();
		initContactStmt();
		initUser_workgroupStmt();
		initCourse_scheduleStmt();
		initRrelationshipStmt();
		initWeb_info_pageStmt();
		initEnterprise_certificateStmt();
		initCampaignStmt();
		initFs_vatStmt();
		initContract_deductionStmt();
		initNoteStmt();
		initCourse_subjectStmt();
		initObservationStmt();
		initAction_favoriteStmt();
		initLoan_accountStmt();
		initSurvey_workflowStmt();
		initProcessStmt();
		initProductStmt();
		initItemStmt();
		initAccountStmt();
		initBrandStmt();
		initTarget_sellerStmt();
		initContract_bonusStmt();
		initFinanceStmt();
		initDossier_typeStmt();
		initGeozoneStmt();
		initPcategory_groupStmt();
		initSegmentStmt();
		initSalary_deductionStmt();
		initInvoiceStmt();
		initInvoice_attachStmt();
		initInvoice_detailStmt();
		initCourse_instructorStmt();
		initAccount_entry_finance_trackingStmt();
		initIncome_detailStmt();
		initRmediaStmt();
		initCommercial_trackingStmt();
		initRaddressStmt();
		initActionStmt();
		initSurveyStmt();
		initSurvey_response_detailStmt();
		initRaddinfoStmt();
		initInvoicing_group_detailStmt();
		initCommission_itemStmt();
		initAcademic_skillStmt();
		initAppraiserStmt();
		initCatalogue_itemStmt();
		initContractStmt();
		initOffer_detailStmt();
		initExpendituresStmt();
		initAmortizationStmt();
		initEnterpriseStmt();
		initMarkStmt();
		initExpense_accountStmt();
		initCatalogue_categoryStmt();
		initLh_contractStmt();
		initAccount_summaryStmt();
		initLh_courseStmt();
		initCompanyStmt();
		initLh_workStmt();
		initScaleStmt();
		initCommission_type_commissionStmt();
		initInventory_detailStmt();
		initScopeStmt();
		initTarget_supplierStmt();
		initCustomer_segmentStmt();
		initContract_batch_detailStmt();
		initAuto_conceptStmt();
		initWeb_info_page_detailStmt();
		initJob_typeStmt();
		initScale_relationStmt();
		initCampaign_dossierStmt();
		initTariffStmt();
		initDeductionStmt();
		initInvoice_addressStmt();
		initMessage_logStmt();
		initApplicationStmt();
		initRdir_staffStmt();
		initTaskStmt();
		initResourceStmt();
		initOfferStmt();
		initComposition_expenseStmt();
		initAccount_helperStmt();
		initCourse_academicskillStmt();
		initEnterprise_cccStmt();
		initTargetStmt();
		initMk_campaignStmt();
		initActivity_processStmt();
		initItem_alternativeStmt();
		initUserStmt();
		initExpense_account_detailStmt();
		initApp_paramStmt();
		initExpenditures_itemsStmt();
	}
	
	@Override
	protected void finalize() throws Throwable {
		closeLeasingStmt();
		closeDb_versionStmt();
		closeComposition_detailStmt();
		closeCv_evaluate_typeStmt();
		closePcategoryStmt();
		closeWarehouseStmt();
		closeCustomerStmt();
		closeProductionStmt();
		closeMessage_contentStmt();
		closePosStmt();
		closeInvoice_taxStmt();
		closeIattachStmt();
		closeTas_itemStmt();
		closeTas_offerStmt();
		closeSalaryStmt();
		closeCommissionStmt();
		closeSupport_order_insuranceStmt();
		closeSupplierStmt();
		closeUser_scopeStmt();
		closeAssetStmt();
		closeItem_warehouseStmt();
		closeRnoteStmt();
		closeFavoriteStmt();
		closeDaily_trackingStmt();
		closeLoanStmt();
		closeTas_deliveryStmt();
		closeQuestion_valueStmt();
		closeWeb_info_styleStmt();
		closeSalary_paymentStmt();
		closeContract_dataStmt();
		closeCv_workexperienceStmt();
		closeIncidence_typeStmt();
		closeItem_posStmt();
		closeEc_paymethodStmt();
		closeMk_actionStmt();
		closeFs_vat_detailStmt();
		closeCv_languagesStmt();
		closeOffer_detail_commissionStmt();
		closeFavorite_categoryStmt();
		closeBalance_detailStmt();
		closeCnae93rev1Stmt();
		closeInvoice_tax_accountStmt();
		closeAccount_entry_detailStmt();
		closePcategory_treeStmt();
		closeProduct_accountStmt();
		closeTariff_catalogueStmt();
		closeSupplier_segmentStmt();
		closeAmortization_detailStmt();
		closeNoticeStmt();
		closeProduction_detailStmt();
		closeDelivery_detailStmt();
		closeAmortization_typeStmt();
		closeCatalogueStmt();
		closePaymentStmt();
		closeAction_entryStmt();
		closeAbsenceStmt();
		closeItem_tariffStmt();
		closeAccount_entry_fbatchStmt();
		closeSellerStmt();
		closeOffer_attachStmt();
		closeProcess_detailStmt();
		closeRelationshipStmt();
		closeAccount_budget_detailStmt();
		closeAccount_entry_invoiceStmt();
		closeTax_accountStmt();
		closeExpenseStmt();
		closeInventoryStmt();
		closeCommercial_termStmt();
		closeProduction_expenseStmt();
		closeCommercial_activityStmt();
		closeOffer_termStmt();
		closeRsegmentStmt();
		closeDelivery_detail_labourStmt();
		closeProcess_transition_typeStmt();
		closeLeasing_accountStmt();
		closeCv_knowledgeStmt();
		closeContract_batchStmt();
		closeQuality_skillStmt();
		closeEc_targetStmt();
		closeBalanceStmt();
		closeQualificationStmt();
		closeTax_detailStmt();
		closeSupplier_accountStmt();
		closeCompositionStmt();
		closeBankStmt();
		closeActivity_typeStmt();
		closeCourse_evaluationStmt();
		closeAccount_entry_linkStmt();
		closeSupport_orderStmt();
		closeProcess_detail_transitionStmt();
		closeCustomer_feeStmt();
		closePurchase_detailStmt();
		closeHoliday_detailStmt();
		closePm_type_detailStmt();
		closeTarget_itemStmt();
		closeHolidayStmt();
		closePm_type_detail_accountStmt();
		closeCommission_categoryStmt();
		closeAcademic_yearStmt();
		closeCreditor_accountStmt();
		closeFinance_trackingStmt();
		closeRattachStmt();
		closeSales_detailStmt();
		closeCurriculumStmt();
		closeMakeStmt();
		closeRbank_accountStmt();
		closeModelStmt();
		closeCnaeStmt();
		closeDepartmentStmt();
		closeTarget_profileStmt();
		closePurchaseStmt();
		closeWeb_infoStmt();
		closeLh_positionStmt();
		closeCv_evaluateStmt();
		closeCourse_alumnStmt();
		closeQuestionStmt();
		closeRecord_dataStmt();
		closeEmployeeStmt();
		closeAlumn_loanStmt();
		closeActivityStmt();
		closeCalendarStmt();
		closeEc_offer_pay_infoStmt();
		closeAsset_activityStmt();
		closeCalendar_periodStmt();
		closeFbatchStmt();
		closeInvoice_detail_accountStmt();
		closeCommission_typeStmt();
		closeEnterprise_certificate_detailStmt();
		closeAlarmStmt();
		closePay_methodStmt();
		closeEc_catalogueStmt();
		closeWorkactivityStmt();
		closeEc_configStmt();
		closeAccount_budgetStmt();
		closeCourse_observationStmt();
		closeSurvey_responseStmt();
		closeEvaluation_observationStmt();
		closePersonStmt();
		closeContract_paymentStmt();
		closeAccount_entryStmt();
		closeGeotreeStmt();
		closeSales_purchaseStmt();
		closeItem_supplierStmt();
		closeCustomer_accountStmt();
		closeDossierStmt();
		closeSurvey_questionStmt();
		closeCv_evaluate_summaryStmt();
		closeCourseStmt();
		closeStockStmt();
		closeRbankStmt();
		closeRpaymethodStmt();
		closeSalesStmt();
		closeIncomeStmt();
		closeEnterprise_activityStmt();
		closeWeb_info_page_resourceStmt();
		closeFbatch_detailStmt();
		closeCalendar_holidayStmt();
		closeWorkgroupStmt();
		closeCategoryStmt();
		closeMk_action_targetStmt();
		closeTaxStmt();
		closeSessionStmt();
		closeCv_studiesStmt();
		closeInvoicing_groupStmt();
		closeRegistryStmt();
		closeDeliveryStmt();
		closeCreditorStmt();
		closeAccount_periodStmt();
		closeSeriesStmt();
		closeCourse_levelStmt();
		closeWorkplaceStmt();
		closeAction_deniedStmt();
		closeContactStmt();
		closeUser_workgroupStmt();
		closeCourse_scheduleStmt();
		closeRrelationshipStmt();
		closeWeb_info_pageStmt();
		closeEnterprise_certificateStmt();
		closeCampaignStmt();
		closeFs_vatStmt();
		closeContract_deductionStmt();
		closeNoteStmt();
		closeCourse_subjectStmt();
		closeObservationStmt();
		closeAction_favoriteStmt();
		closeLoan_accountStmt();
		closeSurvey_workflowStmt();
		closeProcessStmt();
		closeProductStmt();
		closeItemStmt();
		closeAccountStmt();
		closeBrandStmt();
		closeTarget_sellerStmt();
		closeContract_bonusStmt();
		closeFinanceStmt();
		closeDossier_typeStmt();
		closeGeozoneStmt();
		closePcategory_groupStmt();
		closeSegmentStmt();
		closeSalary_deductionStmt();
		closeInvoiceStmt();
		closeInvoice_attachStmt();
		closeInvoice_detailStmt();
		closeCourse_instructorStmt();
		closeAccount_entry_finance_trackingStmt();
		closeIncome_detailStmt();
		closeRmediaStmt();
		closeCommercial_trackingStmt();
		closeRaddressStmt();
		closeActionStmt();
		closeSurveyStmt();
		closeSurvey_response_detailStmt();
		closeRaddinfoStmt();
		closeInvoicing_group_detailStmt();
		closeCommission_itemStmt();
		closeAcademic_skillStmt();
		closeAppraiserStmt();
		closeCatalogue_itemStmt();
		closeContractStmt();
		closeOffer_detailStmt();
		closeExpendituresStmt();
		closeAmortizationStmt();
		closeEnterpriseStmt();
		closeMarkStmt();
		closeExpense_accountStmt();
		closeCatalogue_categoryStmt();
		closeLh_contractStmt();
		closeAccount_summaryStmt();
		closeLh_courseStmt();
		closeCompanyStmt();
		closeLh_workStmt();
		closeScaleStmt();
		closeCommission_type_commissionStmt();
		closeInventory_detailStmt();
		closeScopeStmt();
		closeTarget_supplierStmt();
		closeCustomer_segmentStmt();
		closeContract_batch_detailStmt();
		closeAuto_conceptStmt();
		closeWeb_info_page_detailStmt();
		closeJob_typeStmt();
		closeScale_relationStmt();
		closeCampaign_dossierStmt();
		closeTariffStmt();
		closeDeductionStmt();
		closeInvoice_addressStmt();
		closeMessage_logStmt();
		closeApplicationStmt();
		closeRdir_staffStmt();
		closeTaskStmt();
		closeResourceStmt();
		closeOfferStmt();
		closeComposition_expenseStmt();
		closeAccount_helperStmt();
		closeCourse_academicskillStmt();
		closeEnterprise_cccStmt();
		closeTargetStmt();
		closeMk_campaignStmt();
		closeActivity_processStmt();
		closeItem_alternativeStmt();
		closeUserStmt();
		closeExpense_account_detailStmt();
		closeApp_paramStmt();
		closeExpenditures_itemsStmt();
		super.finalize();
	}
	
	

}		
	
	
