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

package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Blob;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;

import java.io.Reader;

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

public abstract class AbstractDomainMysqlDB extends AbstractMysqlDB  {

	public AbstractDomainMysqlDB( Connection mysqlConnection) 
	throws SQLException {
		super(mysqlConnection);
	}
	
	protected abstract Integer getDefaultDomain() ; 
	
	
	private Map<Integer,Integer> leave_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForLeave_batchPk(Integer id){
		return leave_batchDomains.get(id);
	}
	
	/**
	 * Leave_batch
	 * @returns domain's ID
	*/
	protected Integer getDomainForLeave_batch(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Leave_batch
	 * @param id Identificador unico
	 * @param date Fecha de la Remesa
	 * @param status Indica el estado de la remesa
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLeave_batch(Timestamp date, Short status, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer domain = getDomainForLeave_batch();
		Integer id =  super.insertLeave_batch( domain != null ? domain : getDefaultDomain(), date, status, communication_id, income_file, income_file_date, outcome_file, outcome_file_date );
		if ( domain != null ) { 
			leave_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Leave_batch
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha de la Remesa
	 * @param status Indica el estado de la remesa
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLeave_batch(Integer domain, Timestamp date, Short status, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer id =  super.insertLeave_batch(domain, date, status, communication_id, income_file, income_file_date, outcome_file, outcome_file_date);
		leave_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> inventory_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInventory_detailPk(Integer id){
		return inventory_detailDomains.get(id);
	}
	
	/**
	 * Inventory_detail
	 * @param inventory Identificador de Inventario
	 * @param item Articulo Inventariado
	 * @returns domain's ID
	*/
	protected Integer getDomainForInventory_detail( Integer inventory , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForInventoryPk( inventory ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Inventory_detail
	 * @param id Identificador unico del Detalle del Inventario
	 * @param inventory Identificador de Inventario
	 * @param item Articulo Inventariado
	 * @param actual_quantity Cantidad actual del Articulo Inventariado
	 * @param real_quantity Cantidad real del Articulo Inventariado
	 * @param cost Coste del Articulo Inventariado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInventory_detail(Integer inventory, Integer item, Double actual_quantity, Double real_quantity, Double cost, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForInventory_detail( inventory , item);
		Integer id =  super.insertInventory_detail( domain != null ? domain : getDefaultDomain(), inventory, item, actual_quantity, real_quantity, cost, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			inventory_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Inventory_detail
	 * @param id Identificador unico del Detalle del Inventario
	 * @param domain Identificador del Dominio
	 * @param inventory Identificador de Inventario
	 * @param item Articulo Inventariado
	 * @param actual_quantity Cantidad actual del Articulo Inventariado
	 * @param real_quantity Cantidad real del Articulo Inventariado
	 * @param cost Coste del Articulo Inventariado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInventory_detail(Integer domain, Integer inventory, Integer item, Double actual_quantity, Double real_quantity, Double cost, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertInventory_detail(domain, inventory, item, actual_quantity, real_quantity, cost, creation_user, creation_date, modification_user, modification_date);
		inventory_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pm_type_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPm_type_detailPk(Integer id){
		return pm_type_detailDomains.get(id);
	}
	
	/**
	 * Pm_type_detail
	 * @param account Identificador de la Cuenta Contable
	 * @returns domain's ID
	*/
	protected Integer getDomainForPm_type_detail( Integer account){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Pm_type_detail
	 * @param id Identificador unico
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPm_type_detail(Short type, String description, Integer account)
	throws SQLException {
		Integer domain = getDomainForPm_type_detail( account);
		Integer id =  super.insertPm_type_detail( domain != null ? domain : getDefaultDomain(), type, description, account );
		if ( domain != null ) { 
			pm_type_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pm_type_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPm_type_detail(Integer domain, Short type, String description, Integer account)
	throws SQLException {
		Integer id =  super.insertPm_type_detail(domain, type, description, account);
		pm_type_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rmediaDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRmediaPk(Integer id){
		return rmediaDomains.get(id);
	}
	
	/**
	 * Rmedia
	 * @param raddress Direccion del contacto
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRmedia( Integer raddress , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRaddressPk( raddress ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rmedia
	 * @param id Identificador unico del Medio de Contacto de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param media Tipo de Medio de Contacto de la Persona o Empresa
	 * @param value Valor del Medio de Contacto de la Persona o Empresa
	 * @param comment Comentarios acerca del Medio de Contacto de la Persona o Empresa
	 * @param administrative Indica si el Contacto es de caracter administrativo
	 * @param commercial Indica si el Contacto es de caracter comercial
	 * @param technical Indica si el Contacto es de caracter tecnico
	 * @param raddress Direccion del contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRmedia(Integer registry, Short media, String value, String comment, Boolean administrative, Boolean commercial, Boolean technical, Integer raddress)
	throws SQLException {
		Integer domain = getDomainForRmedia( raddress , registry);
		Integer id =  super.insertRmedia( domain != null ? domain : getDefaultDomain(), registry, media, value, comment, administrative, commercial, technical, raddress );
		if ( domain != null ) { 
			rmediaDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rmedia
	 * @param id Identificador unico del Medio de Contacto de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param media Tipo de Medio de Contacto de la Persona o Empresa
	 * @param value Valor del Medio de Contacto de la Persona o Empresa
	 * @param comment Comentarios acerca del Medio de Contacto de la Persona o Empresa
	 * @param administrative Indica si el Contacto es de caracter administrativo
	 * @param commercial Indica si el Contacto es de caracter comercial
	 * @param technical Indica si el Contacto es de caracter tecnico
	 * @param raddress Direccion del contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRmedia(Integer domain, Integer registry, Short media, String value, String comment, Boolean administrative, Boolean commercial, Boolean technical, Integer raddress)
	throws SQLException {
		Integer id =  super.insertRmedia(domain, registry, media, value, comment, administrative, commercial, technical, raddress);
		rmediaDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> proposal_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProposal_detailPk(Integer id){
		return proposal_detailDomains.get(id);
	}
	
	/**
	 * Proposal_detail
	 * @param item Identificador del Articulo
	 * @param proposal Identificador de la Propuesta de Compra
	 * @param supplier Identificador de Proveedor
	 * @returns domain's ID
	*/
	protected Integer getDomainForProposal_detail( Integer item , Integer proposal , Integer supplier){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProposalPk( proposal ) ) != null )
				return domain;
			if ( ( domain = getDomainForSupplierPk( supplier ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Proposal_detail
	 * @param id Identificador unico
	 * @param proposal Identificador de la Propuesta de Compra
	 * @param item Identificador del Articulo
	 * @param description Descripcion
	 * @param quantity Cantidad
	 * @param price Precio
	 * @param discount_expr Descuentos
	 * @param status Estado del Detalle de la Propuesta
	 * @param supplier Identificador de Proveedor
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProposal_detail(Integer proposal, Integer item, String description, Double quantity, Double price, String discount_expr, Short status, Integer supplier, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProposal_detail( item , proposal , supplier);
		Integer id =  super.insertProposal_detail( domain != null ? domain : getDefaultDomain(), proposal, item, description, quantity, price, discount_expr, status, supplier, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			proposal_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Proposal_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param proposal Identificador de la Propuesta de Compra
	 * @param item Identificador del Articulo
	 * @param description Descripcion
	 * @param quantity Cantidad
	 * @param price Precio
	 * @param discount_expr Descuentos
	 * @param status Estado del Detalle de la Propuesta
	 * @param supplier Identificador de Proveedor
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProposal_detail(Integer domain, Integer proposal, Integer item, String description, Double quantity, Double price, String discount_expr, Short status, Integer supplier, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProposal_detail(domain, proposal, item, description, quantity, price, discount_expr, status, supplier, creation_user, creation_date, modification_user, modification_date);
		proposal_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> reservation_request_roomDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForReservation_request_roomPk(Integer id){
		return reservation_request_roomDomains.get(id);
	}
	
	/**
	 * Reservation_request_room
	 * @param item Identificador del Tipo de Habitacion
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForReservation_request_room( Integer item , Integer reservation_request){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForReservation_requestPk( reservation_request ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Reservation_request_room
	 * @param id Identificador unico
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @param room_index Numero de Habitacion
	 * @param units Numero de Habitaciones
	 * @param item Identificador del Tipo de Habitacion
	 * @param adults Numero de adultos
	 * @param children Numero de niùos
	 * @param babies Numero de bebes
	 * @param crs_code Codigo de Reserva en CRS
	 * @param tariff_code Codigo de Tarifa
	 * @param tariff_description Descripcion de Tarifa
	 * @param inventory_code Codigo de Servicio
	 * @param room_code Codigo de Habitacion
	 * @param room_description Descripcion de Habitacion
	 * @param meal_plan Tipo de regimen
	 * @param daily_price Importe Diario
	 * @param total_price Importe Total
	 * @param agreed_price Importe Pactado
	 * @param cancel_penalty Penalizaciones por cancelacion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request_room(Integer reservation_request, Short room_index, Short units, Integer item, Integer adults, Integer children, Integer babies, String crs_code, String tariff_code, String tariff_description, String inventory_code, String room_code, String room_description, String meal_plan, Double daily_price, Double total_price, Double agreed_price, String cancel_penalty, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForReservation_request_room( item , reservation_request);
		Integer id =  super.insertReservation_request_room( domain != null ? domain : getDefaultDomain(), reservation_request, room_index, units, item, adults, children, babies, crs_code, tariff_code, tariff_description, inventory_code, room_code, room_description, meal_plan, daily_price, total_price, agreed_price, cancel_penalty, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			reservation_request_roomDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Reservation_request_room
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @param room_index Numero de Habitacion
	 * @param units Numero de Habitaciones
	 * @param item Identificador del Tipo de Habitacion
	 * @param adults Numero de adultos
	 * @param children Numero de niùos
	 * @param babies Numero de bebes
	 * @param crs_code Codigo de Reserva en CRS
	 * @param tariff_code Codigo de Tarifa
	 * @param tariff_description Descripcion de Tarifa
	 * @param inventory_code Codigo de Servicio
	 * @param room_code Codigo de Habitacion
	 * @param room_description Descripcion de Habitacion
	 * @param meal_plan Tipo de regimen
	 * @param daily_price Importe Diario
	 * @param total_price Importe Total
	 * @param agreed_price Importe Pactado
	 * @param cancel_penalty Penalizaciones por cancelacion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request_room(Integer domain, Integer reservation_request, Short room_index, Short units, Integer item, Integer adults, Integer children, Integer babies, String crs_code, String tariff_code, String tariff_description, String inventory_code, String room_code, String room_description, String meal_plan, Double daily_price, Double total_price, Double agreed_price, String cancel_penalty, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertReservation_request_room(domain, reservation_request, room_index, units, item, adults, children, babies, crs_code, tariff_code, tariff_description, inventory_code, room_code, room_description, meal_plan, daily_price, total_price, agreed_price, cancel_penalty, creation_user, creation_date, modification_user, modification_date);
		reservation_request_roomDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salary_paymentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_paymentPk(Integer id){
		return salary_paymentDomains.get(id);
	}
	
	/**
	 * Salary_payment
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_payment( Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_payment
	 * @param id Identificador unico
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de PercepciÛn Salarial
	 * @param payment_concept Codigo
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param amount Importe
	 * @param irpf Importe I.R.P.F
	 * @param quote Importe Cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_payment(Integer salary, Short type, String payment_concept, String description, String expression, Double amount, Double irpf, Double quote)
	throws SQLException {
		Integer domain = getDomainForSalary_payment( salary);
		Integer id =  super.insertSalary_payment( domain != null ? domain : getDefaultDomain(), salary, type, payment_concept, description, expression, amount, irpf, quote );
		if ( domain != null ) { 
			salary_paymentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de PercepciÛn Salarial
	 * @param payment_concept Codigo
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param amount Importe
	 * @param irpf Importe I.R.P.F
	 * @param quote Importe Cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_payment(Integer domain, Integer salary, Short type, String payment_concept, String description, String expression, Double amount, Double irpf, Double quote)
	throws SQLException {
		Integer id =  super.insertSalary_payment(domain, salary, type, payment_concept, description, expression, amount, irpf, quote);
		salary_paymentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> customer_feeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCustomer_feePk(Integer id){
		return customer_feeDomains.get(id);
	}
	
	/**
	 * Customer_fee
	 * @param customer Identificador del Cliente
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param item Identificador del Articulo
	 * @param project Identificador del Proyecto
	 * @param seller Identificador de Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForCustomer_fee( Integer customer , Integer invoicing_group , Integer item , Integer project , Integer seller , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicing_groupPk( invoicing_group ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Customer_fee
	 * @param id Identificador unico de la Cuota del Cliente
	 * @param project Identificador del Proyecto
	 * @param customer Identificador del Cliente
	 * @param line Numero de linea de Cuota
	 * @param item Identificador del Articulo
	 * @param description Descripcion de la Cuota
	 * @param quantity Cantidad de la Cuota
	 * @param price Precio de la Cuota
	 * @param discount_expr Descuentos de la Cuota
	 * @param initial_date Fecha de inicio de la Cuota
	 * @param final_date Fecha de finalizacion de la Cuota
	 * @param billing_date Proxima fecha de facturaciÛn de la Cuota
	 * @param period Periodo de facturacion en meses de la Cuota
	 * @param security_level Nivel de seguridad de la Cuota
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param seller Identificador de Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCustomer_fee(Integer project, Integer customer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Date initial_date, Date final_date, Date billing_date, Integer period, Short security_level, Integer invoicing_group, Integer seller, Integer workplace)
	throws SQLException {
		Integer domain = getDomainForCustomer_fee( customer , invoicing_group , item , project , seller , workplace);
		Integer id =  super.insertCustomer_fee( domain != null ? domain : getDefaultDomain(), project, customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, invoicing_group, seller, workplace );
		if ( domain != null ) { 
			customer_feeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Customer_fee
	 * @param id Identificador unico de la Cuota del Cliente
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param customer Identificador del Cliente
	 * @param line Numero de linea de Cuota
	 * @param item Identificador del Articulo
	 * @param description Descripcion de la Cuota
	 * @param quantity Cantidad de la Cuota
	 * @param price Precio de la Cuota
	 * @param discount_expr Descuentos de la Cuota
	 * @param initial_date Fecha de inicio de la Cuota
	 * @param final_date Fecha de finalizacion de la Cuota
	 * @param billing_date Proxima fecha de facturaciÛn de la Cuota
	 * @param period Periodo de facturacion en meses de la Cuota
	 * @param security_level Nivel de seguridad de la Cuota
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param seller Identificador de Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCustomer_fee(Integer domain, Integer project, Integer customer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Date initial_date, Date final_date, Date billing_date, Integer period, Short security_level, Integer invoicing_group, Integer seller, Integer workplace)
	throws SQLException {
		Integer id =  super.insertCustomer_fee(domain, project, customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, invoicing_group, seller, workplace);
		customer_feeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salaryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalaryPk(Integer id){
		return salaryDomains.get(id);
	}
	
	/**
	 * Salary
	 * @param contract Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary
	 * @param id Identificador unico
	 * @param type Tipo de Nomina
	 * @param contract Contrato
	 * @param start_date Fecha de inicio liquidaciÛn
	 * @param end_date Fecha de finalizacion liquidaciÛn
	 * @param enterprise_name Nombre de la empresa
	 * @param enterprise_address Domicilio de la empresa
	 * @param enterprise_document Numero de Documento de la Empresa
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param ss_regime Regimen de la Seguridad Social
	 * @param employee_name Nombre del trabajador
	 * @param social_security_number Numero de la seguridad social
	 * @param employee_document Numero de Documento de la Persona
	 * @param seniority_date Fecha de antiguedad
	 * @param quote_group Grupo de CotizaciÛn
	 * @param category Categoria o grupo profesional
	 * @param registration N˙mero libro de matricula
	 * @param time_units total dias/horas
	 * @param total_payment Total devengado
	 * @param total_deduction Total a deducir
	 * @param total_liquid Liquido total a percibir
	 * @param total_enterprise Cuota total de la empresa
	 * @param issue_date Fecha de emisiÛn
	 * @param remuneration RemuneraciÛn mensual
	 * @param pro_ext_base Base prorraterreada de pagas extras
	 * @param it_base Base de IT
	 * @param raw_cgc_base Base efectiva de cotizacion por contingencias comunes 
	 * @param cgc_base Base de cotizacion por contingencias comunes
	 * @param hextra_base Base de cotizacion adicional por horas extraordinarias estructurales
	 * @param non_hextra_base Base de cotizacion adicional por horas extraordinarias no estructurales
	 * @param cgp_base Base de cotizacion por contingencias profesionales
	 * @param money_irpf_base Salario en dinero sujeto a retenciÛn I.R.P.F
	 * @param inkind_irpf_base Salario en especie sujeto a retenciÛn I.R.P.F
	 * @param irpf_base Base sujeta a retenciÛn I.R.P.F
	 * @param social_security_contributions Aportaciones a la Seguridad Social
	 * @param total_irpf Total retenciÛn aplicada 
	 * @param charge_date Fecha de cobro
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary(Short type, Integer contract, Date start_date, Date end_date, String enterprise_name, String enterprise_address, String enterprise_document, String ccc, Short ss_regime, String employee_name, String social_security_number, String employee_document, Date seniority_date, String quote_group, String category, Integer registration, Integer time_units, Double total_payment, Double total_deduction, Double total_liquid, Double total_enterprise, Date issue_date, Double remuneration, Double pro_ext_base, Double it_base, Double raw_cgc_base, Double cgc_base, Double hextra_base, Double non_hextra_base, Double cgp_base, Double money_irpf_base, Double inkind_irpf_base, Double irpf_base, Double social_security_contributions, Double total_irpf, Date charge_date)
	throws SQLException {
		Integer domain = getDomainForSalary( contract);
		Integer id =  super.insertSalary( domain != null ? domain : getDefaultDomain(), type, contract, start_date, end_date, enterprise_name, enterprise_address, enterprise_document, ccc, ss_regime, employee_name, social_security_number, employee_document, seniority_date, quote_group, category, registration, time_units, total_payment, total_deduction, total_liquid, total_enterprise, issue_date, remuneration, pro_ext_base, it_base, raw_cgc_base, cgc_base, hextra_base, non_hextra_base, cgp_base, money_irpf_base, inkind_irpf_base, irpf_base, social_security_contributions, total_irpf, charge_date );
		if ( domain != null ) { 
			salaryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Nomina
	 * @param contract Contrato
	 * @param start_date Fecha de inicio liquidaciÛn
	 * @param end_date Fecha de finalizacion liquidaciÛn
	 * @param enterprise_name Nombre de la empresa
	 * @param enterprise_address Domicilio de la empresa
	 * @param enterprise_document Numero de Documento de la Empresa
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param ss_regime Regimen de la Seguridad Social
	 * @param employee_name Nombre del trabajador
	 * @param social_security_number Numero de la seguridad social
	 * @param employee_document Numero de Documento de la Persona
	 * @param seniority_date Fecha de antiguedad
	 * @param quote_group Grupo de CotizaciÛn
	 * @param category Categoria o grupo profesional
	 * @param registration N˙mero libro de matricula
	 * @param time_units total dias/horas
	 * @param total_payment Total devengado
	 * @param total_deduction Total a deducir
	 * @param total_liquid Liquido total a percibir
	 * @param total_enterprise Cuota total de la empresa
	 * @param issue_date Fecha de emisiÛn
	 * @param remuneration RemuneraciÛn mensual
	 * @param pro_ext_base Base prorraterreada de pagas extras
	 * @param it_base Base de IT
	 * @param raw_cgc_base Base efectiva de cotizacion por contingencias comunes 
	 * @param cgc_base Base de cotizacion por contingencias comunes
	 * @param hextra_base Base de cotizacion adicional por horas extraordinarias estructurales
	 * @param non_hextra_base Base de cotizacion adicional por horas extraordinarias no estructurales
	 * @param cgp_base Base de cotizacion por contingencias profesionales
	 * @param money_irpf_base Salario en dinero sujeto a retenciÛn I.R.P.F
	 * @param inkind_irpf_base Salario en especie sujeto a retenciÛn I.R.P.F
	 * @param irpf_base Base sujeta a retenciÛn I.R.P.F
	 * @param social_security_contributions Aportaciones a la Seguridad Social
	 * @param total_irpf Total retenciÛn aplicada 
	 * @param charge_date Fecha de cobro
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary(Integer domain, Short type, Integer contract, Date start_date, Date end_date, String enterprise_name, String enterprise_address, String enterprise_document, String ccc, Short ss_regime, String employee_name, String social_security_number, String employee_document, Date seniority_date, String quote_group, String category, Integer registration, Integer time_units, Double total_payment, Double total_deduction, Double total_liquid, Double total_enterprise, Date issue_date, Double remuneration, Double pro_ext_base, Double it_base, Double raw_cgc_base, Double cgc_base, Double hextra_base, Double non_hextra_base, Double cgp_base, Double money_irpf_base, Double inkind_irpf_base, Double irpf_base, Double social_security_contributions, Double total_irpf, Date charge_date)
	throws SQLException {
		Integer id =  super.insertSalary(domain, type, contract, start_date, end_date, enterprise_name, enterprise_address, enterprise_document, ccc, ss_regime, employee_name, social_security_number, employee_document, seniority_date, quote_group, category, registration, time_units, total_payment, total_deduction, total_liquid, total_enterprise, issue_date, remuneration, pro_ext_base, it_base, raw_cgc_base, cgc_base, hextra_base, non_hextra_base, cgp_base, money_irpf_base, inkind_irpf_base, irpf_base, social_security_contributions, total_irpf, charge_date);
		salaryDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> elaboration_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForElaboration_detailPk(Integer id){
		return elaboration_detailDomains.get(id);
	}
	
	/**
	 * Elaboration_detail
	 * @param elaboration Identificador de la elaboracion
	 * @param item Identificador del articulo no base elaborado
	 * @param warehouse Identificador del almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForElaboration_detail( Integer elaboration , Integer item , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForElaborationPk( elaboration ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Elaboration_detail
	 * @param id Identificador unico del detalle
	 * @param elaboration Identificador de la elaboracion
	 * @param date Fecha de elaboracion
	 * @param item Identificador del articulo no base elaborado
	 * @param quantity Cantidad elaborado
	 * @param warehouse Identificador del almacen
	 * @param add_info Informacion de uso interno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration_detail(Integer elaboration, Timestamp date, Integer item, Double quantity, Integer warehouse, String add_info, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForElaboration_detail( elaboration , item , warehouse);
		Integer id =  super.insertElaboration_detail( domain != null ? domain : getDefaultDomain(), elaboration, date, item, quantity, warehouse, add_info, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			elaboration_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Elaboration_detail
	 * @param id Identificador unico del detalle
	 * @param domain Identificador del dominio
	 * @param elaboration Identificador de la elaboracion
	 * @param date Fecha de elaboracion
	 * @param item Identificador del articulo no base elaborado
	 * @param quantity Cantidad elaborado
	 * @param warehouse Identificador del almacen
	 * @param add_info Informacion de uso interno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration_detail(Integer domain, Integer elaboration, Timestamp date, Integer item, Double quantity, Integer warehouse, String add_info, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertElaboration_detail(domain, elaboration, date, item, quantity, warehouse, add_info, creation_user, creation_date, modification_user, modification_date);
		elaboration_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> offerDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForOfferPk(Integer id){
		return offerDomains.get(id);
	}
	
	/**
	 * Offer
	 * @param pay_method Forma de Pago del Presupuesto
	 * @param project Identificador del Proyecto
	 * @param address Identificador de la Direccion de envio del Presupuesto
	 * @param scope Ambito del Presupuesto
	 * @param seller Agente Comercial del Presupuesto
	 * @param supplier Identificador del Proveedor
	 * @param target Identificador del Cliente Potencial
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForOffer( Integer pay_method , Integer project , Integer address , Integer scope , Integer seller , Integer supplier , Integer target , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
			if ( ( domain = getDomainForSupplierPk( supplier ) ) != null )
				return domain;
			if ( ( domain = getDomainForTargetPk( target ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Offer
	 * @param id Identificador unico del Presupuesto
	 * @param project Identificador del Proyecto
	 * @param target Identificador del Cliente Potencial
	 * @param series Serie del Presupuesto
	 * @param number Numero del Presupuesto
	 * @param version Numero de version de Presupuesto
	 * @param address Identificador de la Direccion de envio del Presupuesto
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
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param signed Indica si el Presupuesto esta firmada electronicamente
	 * @param comments Comentarios del Presupuesto
	 * @param remarks Observaciones del Presupuesto
	 * @param external_reference Referencia externa
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer(Integer project, Integer target, String series, Integer number, Integer version, Integer address, Integer seller, Integer supplier, String discount_expr, Date issue_date, Integer pay_method, Short security_level, Short status, Short type, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean signed, String comments, String remarks, String external_reference, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForOffer( pay_method , project , address , scope , seller , supplier , target , workplace);
		Integer id =  super.insertOffer( domain != null ? domain : getDefaultDomain(), project, target, series, number, version, address, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, signed, comments, remarks, external_reference, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			offerDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Offer
	 * @param id Identificador unico del Presupuesto
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param target Identificador del Cliente Potencial
	 * @param series Serie del Presupuesto
	 * @param number Numero del Presupuesto
	 * @param version Numero de version de Presupuesto
	 * @param address Identificador de la Direccion de envio del Presupuesto
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
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param signed Indica si el Presupuesto esta firmada electronicamente
	 * @param comments Comentarios del Presupuesto
	 * @param remarks Observaciones del Presupuesto
	 * @param external_reference Referencia externa
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer(Integer domain, Integer project, Integer target, String series, Integer number, Integer version, Integer address, Integer seller, Integer supplier, String discount_expr, Date issue_date, Integer pay_method, Short security_level, Short status, Short type, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean signed, String comments, String remarks, String external_reference, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertOffer(domain, project, target, series, number, version, address, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, signed, comments, remarks, external_reference, creation_user, creation_date, modification_user, modification_date);
		offerDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_evaluationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_evaluationPk(Integer id){
		return course_evaluationDomains.get(id);
	}
	
	/**
	 * Course_evaluation
	 * @param course Identificador de Curso
	 * @param quality_skill Identificador de Aptitudes Calidad
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_evaluation( Integer course , Integer quality_skill){
		Integer domain = null;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
			if ( ( domain = getDomainForQuality_skillPk( quality_skill ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_evaluation
	 * @param id Identificador unico
	 * @param course Identificador de Curso
	 * @param quality_skill Identificador de Aptitudes Calidad
	 * @param evaluation Evaluaciones
	 * @param quantity Cantidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_evaluation(Integer course, Integer quality_skill, Double evaluation, Integer quantity)
	throws SQLException {
		Integer domain = getDomainForCourse_evaluation( course , quality_skill);
		Integer id =  super.insertCourse_evaluation( domain != null ? domain : getDefaultDomain(), course, quality_skill, evaluation, quantity );
		if ( domain != null ) { 
			course_evaluationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_evaluation
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param course Identificador de Curso
	 * @param quality_skill Identificador de Aptitudes Calidad
	 * @param evaluation Evaluaciones
	 * @param quantity Cantidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_evaluation(Integer domain, Integer course, Integer quality_skill, Double evaluation, Integer quantity)
	throws SQLException {
		Integer id =  super.insertCourse_evaluation(domain, course, quality_skill, evaluation, quantity);
		course_evaluationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salary_deductionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_deductionPk(Integer id){
		return salary_deductionDomains.get(id);
	}
	
	/**
	 * Salary_deduction
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_deduction( Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_deduction
	 * @param id Identificador unico
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de deducciÛn Salarial
	 * @param deduction_concept Codigo del concepto
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_deduction(Integer salary, Short type, String deduction_concept, String description, String expression, Double amount)
	throws SQLException {
		Integer domain = getDomainForSalary_deduction( salary);
		Integer id =  super.insertSalary_deduction( domain != null ? domain : getDefaultDomain(), salary, type, deduction_concept, description, expression, amount );
		if ( domain != null ) { 
			salary_deductionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de deducciÛn Salarial
	 * @param deduction_concept Codigo del concepto
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_deduction(Integer domain, Integer salary, Short type, String deduction_concept, String description, String expression, Double amount)
	throws SQLException {
		Integer id =  super.insertSalary_deduction(domain, salary, type, deduction_concept, description, expression, amount);
		salary_deductionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> task_eventDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTask_eventPk(Integer id){
		return task_eventDomains.get(id);
	}
	
	/**
	 * Task_event
	 * @param task Identificador de la tarea
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask_event( Integer task){
		Integer domain = null;
			if ( ( domain = getDomainForTaskPk( task ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task_event
	 * @param id Identificador unico
	 * @param task Identificador de la tarea
	 * @param event Evento de la Tarea
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_event(Integer task, String event, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTask_event( task);
		Integer id =  super.insertTask_event( domain != null ? domain : getDefaultDomain(), task, event, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			task_eventDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Task_event
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param task Identificador de la tarea
	 * @param event Evento de la Tarea
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_event(Integer domain, Integer task, String event, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertTask_event(domain, task, event, creation_user, creation_date, modification_user, modification_date);
		task_eventDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> offer_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForOffer_detailPk(Integer id){
		return offer_detailDomains.get(id);
	}
	
	/**
	 * Offer_detail
	 * @param item Identificador del Articulo
	 * @param offer Identificador del Presupuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForOffer_detail( Integer item , Integer offer){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForOfferPk( offer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Offer_detail
	 * @param id Identificador unico del Detalle de Presupuesto
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea del Detalle dentro del Presupuesto
	 * @param item Identificador del Articulo
	 * @param description DescripciÛn del Articulo
	 * @param quantity Cantidad del Articulo
	 * @param price Precio del Articulo
	 * @param discount_expr Descuentos del Articulo
	 * @param status Estado del Detalle del Presupuesto
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_detail(Integer offer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForOffer_detail( item , offer);
		Integer id =  super.insertOffer_detail( domain != null ? domain : getDefaultDomain(), offer, line, item, description, quantity, price, discount_expr, status, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			offer_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Offer_detail
	 * @param id Identificador unico del Detalle de Presupuesto
	 * @param domain Identificador del Dominio
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea del Detalle dentro del Presupuesto
	 * @param item Identificador del Articulo
	 * @param description DescripciÛn del Articulo
	 * @param quantity Cantidad del Articulo
	 * @param price Precio del Articulo
	 * @param discount_expr Descuentos del Articulo
	 * @param status Estado del Detalle del Presupuesto
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_detail(Integer domain, Integer offer, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertOffer_detail(domain, offer, line, item, description, quantity, price, discount_expr, status, creation_user, creation_date, modification_user, modification_date);
		offer_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> brandDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBrandPk(Integer id){
		return brandDomains.get(id);
	}
	
	/**
	 * Brand
	 * @returns domain's ID
	*/
	protected Integer getDomainForBrand(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Brand
	 * @param id Identificador unico de la Marca Comercial
	 * @param name Nombre de la Marca Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBrand(String name)
	throws SQLException {
		Integer domain = getDomainForBrand();
		Integer id =  super.insertBrand( domain != null ? domain : getDefaultDomain(), name );
		if ( domain != null ) { 
			brandDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Brand
	 * @param id Identificador unico de la Marca Comercial
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Marca Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBrand(Integer domain, String name)
	throws SQLException {
		Integer id =  super.insertBrand(domain, name);
		brandDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> item_addinfoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItem_addinfoPk(Integer id){
		return item_addinfoDomains.get(id);
	}
	
	/**
	 * Item_addinfo
	 * @param item Identificador de Articulo
	 * @param product Identificador del Producto
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem_addinfo( Integer item , Integer product){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProductPk( product ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item_addinfo
	 * @param id Identificador unico
	 * @param product Identificador del Producto
	 * @param item Identificador de Articulo
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_addinfo(Integer product, Integer item, String attribute, String value, Date value_date)
	throws SQLException {
		Integer domain = getDomainForItem_addinfo( item , product);
		Integer id =  super.insertItem_addinfo( domain != null ? domain : getDefaultDomain(), product, item, attribute, value, value_date );
		if ( domain != null ) { 
			item_addinfoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item_addinfo
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param product Identificador del Producto
	 * @param item Identificador de Articulo
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_addinfo(Integer domain, Integer product, Integer item, String attribute, String value, Date value_date)
	throws SQLException {
		Integer id =  super.insertItem_addinfo(domain, product, item, attribute, value, value_date);
		item_addinfoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> account_entry_fbatchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entry_fbatchPk(Integer id){
		return account_entry_fbatchDomains.get(id);
	}
	
	/**
	 * Account_entry_fbatch
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry_fbatch( Integer account_entry , Integer fbatch){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForFbatchPk( fbatch ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry_fbatch
	 * @param id Identificador unico
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_fbatch(Integer account_entry, Integer fbatch)
	throws SQLException {
		Integer domain = getDomainForAccount_entry_fbatch( account_entry , fbatch);
		Integer id =  super.insertAccount_entry_fbatch( domain != null ? domain : getDefaultDomain(), account_entry, fbatch );
		if ( domain != null ) { 
			account_entry_fbatchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry_fbatch
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_fbatch(Integer domain, Integer account_entry, Integer fbatch)
	throws SQLException {
		Integer id =  super.insertAccount_entry_fbatch(domain, account_entry, fbatch);
		account_entry_fbatchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> certifica2_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCertifica2_batchPk(Integer id){
		return certifica2_batchDomains.get(id);
	}
	
	/**
	 * Certifica2_batch
	 * @param enterprise Identificador unico del certificado de empresa de la empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForCertifica2_batch( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Certifica2_batch
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param enterprise Identificador unico del certificado de empresa de la empresa
	 * @param date Fecha de la remesa
	 * @param status Estado de la remesa
	 * @param sign Huella digital obtenida de la respuesta
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCertifica2_batch(Integer enterprise, Timestamp date, Integer status, String sign, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer domain = getDomainForCertifica2_batch( enterprise);
		Integer id =  super.insertCertifica2_batch( domain != null ? domain : getDefaultDomain(), enterprise, date, status, sign, communication_id, income_file, income_file_date, outcome_file, outcome_file_date );
		if ( domain != null ) { 
			certifica2_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Certifica2_batch
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador unico del certificado de empresa de la empresa
	 * @param date Fecha de la remesa
	 * @param status Estado de la remesa
	 * @param sign Huella digital obtenida de la respuesta
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCertifica2_batch(Integer domain, Integer enterprise, Timestamp date, Integer status, String sign, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer id =  super.insertCertifica2_batch(domain, enterprise, date, status, sign, communication_id, income_file, income_file_date, outcome_file, outcome_file_date);
		certifica2_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pos_catalogueDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPos_cataloguePk(Integer id){
		return pos_catalogueDomains.get(id);
	}
	
	/**
	 * Pos_catalogue
	 * @param catalogue Identificador del Catalogo
	 * @param pos Identificador del TPV
	 * @returns domain's ID
	*/
	protected Integer getDomainForPos_catalogue( Integer catalogue , Integer pos){
		Integer domain = null;
			if ( ( domain = getDomainForCataloguePk( catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForPosPk( pos ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Pos_catalogue
	 * @param id Identificador unico
	 * @param pos Identificador del TPV
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_catalogue(Integer pos, Integer catalogue)
	throws SQLException {
		Integer domain = getDomainForPos_catalogue( catalogue , pos);
		Integer id =  super.insertPos_catalogue( domain != null ? domain : getDefaultDomain(), pos, catalogue );
		if ( domain != null ) { 
			pos_catalogueDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pos_catalogue
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pos Identificador del TPV
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_catalogue(Integer domain, Integer pos, Integer catalogue)
	throws SQLException {
		Integer id =  super.insertPos_catalogue(domain, pos, catalogue);
		pos_catalogueDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> proposalDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProposalPk(Integer id){
		return proposalDomains.get(id);
	}
	
	/**
	 * Proposal
	 * @param department Identificador del Departamento
	 * @param scope Identificador del Ambito
	 * @param transfer_proposal Identificador de la Solicitud de traspaso vinculada
	 * @param warehouse Identificador del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForProposal( Integer department , Integer scope , Integer transfer_proposal , Integer warehouse , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForDepartmentPk( department ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForProposalPk( transfer_proposal ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Proposal
	 * @param id Identificador unico
	 * @param issue_date Fecha de emision de la Propuesta
	 * @param department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param scope Identificador del Ambito
	 * @param remarks Observaciones de la Propuesta
	 * @param item_return Indica si es una devolucion
	 * @param status Estado de la Propuesta
	 * @param transfer_status Indica si es un traspaso y su estado
	 * @param transfer_proposal Identificador de la Solicitud de traspaso vinculada
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProposal(Date issue_date, Integer department, Integer workplace, Integer warehouse, Integer scope, String remarks, Boolean item_return, Short status, Short transfer_status, Integer transfer_proposal, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProposal( department , scope , transfer_proposal , warehouse , workplace);
		Integer id =  super.insertProposal( domain != null ? domain : getDefaultDomain(), issue_date, department, workplace, warehouse, scope, remarks, item_return, status, transfer_status, transfer_proposal, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			proposalDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Proposal
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param issue_date Fecha de emision de la Propuesta
	 * @param department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param scope Identificador del Ambito
	 * @param remarks Observaciones de la Propuesta
	 * @param item_return Indica si es una devolucion
	 * @param status Estado de la Propuesta
	 * @param transfer_status Indica si es un traspaso y su estado
	 * @param transfer_proposal Identificador de la Solicitud de traspaso vinculada
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProposal(Integer domain, Date issue_date, Integer department, Integer workplace, Integer warehouse, Integer scope, String remarks, Boolean item_return, Short status, Short transfer_status, Integer transfer_proposal, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProposal(domain, issue_date, department, workplace, warehouse, scope, remarks, item_return, status, transfer_status, transfer_proposal, creation_user, creation_date, modification_user, modification_date);
		proposalDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> alumn_loanDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAlumn_loanPk(Integer id){
		return alumn_loanDomains.get(id);
	}
	
	/**
	 * Alumn_loan
	 * @param customer Alumno al que se le realizo el Prestamo
	 * @returns domain's ID
	*/
	protected Integer getDomainForAlumn_loan( Integer customer){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Alumn_loan
	 * @param id Identificador unico del Prestamo
	 * @param customer Alumno al que se le realizo el Prestamo
	 * @param material Material prestado
	 * @param loan_date Fecha del Prestamo
	 * @param end_date Fecha devolucion del material
	 * @param comments Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAlumn_loan(Integer customer, String material, Date loan_date, Date end_date, String comments)
	throws SQLException {
		Integer domain = getDomainForAlumn_loan( customer);
		Integer id =  super.insertAlumn_loan( domain != null ? domain : getDefaultDomain(), customer, material, loan_date, end_date, comments );
		if ( domain != null ) { 
			alumn_loanDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Alumn_loan
	 * @param id Identificador unico del Prestamo
	 * @param domain Identificador del Dominio
	 * @param customer Alumno al que se le realizo el Prestamo
	 * @param material Material prestado
	 * @param loan_date Fecha del Prestamo
	 * @param end_date Fecha devolucion del material
	 * @param comments Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAlumn_loan(Integer domain, Integer customer, String material, Date loan_date, Date end_date, String comments)
	throws SQLException {
		Integer id =  super.insertAlumn_loan(domain, customer, material, loan_date, end_date, comments);
		alumn_loanDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreementDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreementPk(Integer id){
		return agreementDomains.get(id);
	}
	
	/**
	 * Agreement
	 * @param calendar Calendario
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement( Integer calendar){
		Integer domain = null;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement
	 * @param id Identificador unico
	 * @param calendar Calendario
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement(Integer calendar, String description)
	throws SQLException {
		Integer domain = getDomainForAgreement( calendar);
		Integer id =  super.insertAgreement( domain != null ? domain : getDefaultDomain(), calendar, description );
		if ( domain != null ) { 
			agreementDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param calendar Calendario
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement(Integer domain, Integer calendar, String description)
	throws SQLException {
		Integer id =  super.insertAgreement(domain, calendar, description);
		agreementDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_leave_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_leave_detailPk(Integer id){
		return contract_leave_detailDomains.get(id);
	}
	
	/**
	 * Contract_leave_detail
	 * @param contract_leave Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_leave_detail( Integer contract_leave){
		Integer domain = null;
			if ( ( domain = getDomainForContract_leavePk( contract_leave ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_leave_detail
	 * @param id Identificador unico
	 * @param type Tipo de parte
	 * @param contract_leave Contrato
	 * @param college_number Numero de colegiado
	 * @param confirm_order Numero de orden del parte de confirmacion
	 * @param cias codigo identificacion area sanitaria
	 * @param date Fecha del parte
	 * @param status Indica el estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_leave_detail(Short type, Integer contract_leave, String college_number, Short confirm_order, String cias, Date date, Short status)
	throws SQLException {
		Integer domain = getDomainForContract_leave_detail( contract_leave);
		Integer id =  super.insertContract_leave_detail( domain != null ? domain : getDefaultDomain(), type, contract_leave, college_number, confirm_order, cias, date, status );
		if ( domain != null ) { 
			contract_leave_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_leave_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de parte
	 * @param contract_leave Contrato
	 * @param college_number Numero de colegiado
	 * @param confirm_order Numero de orden del parte de confirmacion
	 * @param cias codigo identificacion area sanitaria
	 * @param date Fecha del parte
	 * @param status Indica el estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_leave_detail(Integer domain, Short type, Integer contract_leave, String college_number, Short confirm_order, String cias, Date date, Short status)
	throws SQLException {
		Integer id =  super.insertContract_leave_detail(domain, type, contract_leave, college_number, confirm_order, cias, date, status);
		contract_leave_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_academicskillDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_academicskillPk(Integer id){
		return course_academicskillDomains.get(id);
	}
	
	/**
	 * Course_academicskill
	 * @param academic_skill Aptitud Academica
	 * @param course Curso
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_academicskill( Integer academic_skill , Integer course){
		Integer domain = null;
			if ( ( domain = getDomainForAcademic_skillPk( academic_skill ) ) != null )
				return domain;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_academicskill
	 * @param id Identificador Unico
	 * @param course Curso
	 * @param academic_skill Aptitud Academica
	 * @param weight Peso de la Aptitud para calcular la Nota media
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_academicskill(Integer course, Integer academic_skill, Integer weight)
	throws SQLException {
		Integer domain = getDomainForCourse_academicskill( academic_skill , course);
		Integer id =  super.insertCourse_academicskill( domain != null ? domain : getDefaultDomain(), course, academic_skill, weight );
		if ( domain != null ) { 
			course_academicskillDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_academicskill
	 * @param id Identificador Unico
	 * @param domain Identificador del Dominio
	 * @param course Curso
	 * @param academic_skill Aptitud Academica
	 * @param weight Peso de la Aptitud para calcular la Nota media
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_academicskill(Integer domain, Integer course, Integer academic_skill, Integer weight)
	throws SQLException {
		Integer id =  super.insertCourse_academicskill(domain, course, academic_skill, weight);
		course_academicskillDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> profileDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProfilePk(Integer id){
		return profileDomains.get(id);
	}
	
	/**
	 * Profile
	 * @param application Identificador de la Aplicacion
	 * @returns domain's ID
	*/
	protected Integer getDomainForProfile( Integer application){
		Integer domain = null;
		return domain;
	}

	/**
	 * Profile
	 * @param id Identificador unico
	 * @param name Nombre del Perfil
	 * @param application Identificador de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile(String name, Integer application)
	throws SQLException {
		Integer domain = getDomainForProfile( application);
		Integer id =  super.insertProfile( name, application, domain != null ? domain : getDefaultDomain() );
		if ( domain != null ) { 
			profileDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Profile
	 * @param id Identificador unico
	 * @param name Nombre del Perfil
	 * @param application Identificador de la Aplicacion
	 * @param domain Identificador del Dominio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile(String name, Integer application, Integer domain)
	throws SQLException {
		Integer id =  super.insertProfile(name, application, domain);
		profileDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> quality_skillDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForQuality_skillPk(Integer id){
		return quality_skillDomains.get(id);
	}
	
	/**
	 * Quality_skill
	 * @returns domain's ID
	*/
	protected Integer getDomainForQuality_skill(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Quality_skill
	 * @param id Identificador unico de la Aptitud Calidad
	 * @param code Codigo de la Aptitud Calidad
	 * @param description Descripcion de la Aptitud Calidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuality_skill(String code, String description)
	throws SQLException {
		Integer domain = getDomainForQuality_skill();
		Integer id =  super.insertQuality_skill( domain != null ? domain : getDefaultDomain(), code, description );
		if ( domain != null ) { 
			quality_skillDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Quality_skill
	 * @param id Identificador unico de la Aptitud Calidad
	 * @param domain Identificador del Dominio
	 * @param code Codigo de la Aptitud Calidad
	 * @param description Descripcion de la Aptitud Calidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuality_skill(Integer domain, String code, String description)
	throws SQLException {
		Integer id =  super.insertQuality_skill(domain, code, description);
		quality_skillDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> survey_questionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSurvey_questionPk(Integer id){
		return survey_questionDomains.get(id);
	}
	
	/**
	 * Survey_question
	 * @param question Identificador de la Pregunta
	 * @param survey Identificador del Cuestionario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSurvey_question( Integer question , Integer survey){
		Integer domain = null;
			if ( ( domain = getDomainForQuestionPk( question ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurveyPk( survey ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Survey_question
	 * @param id Identificador unico
	 * @param survey Identificador del Cuestionario
	 * @param question Identificador de la Pregunta
	 * @param position Posicion de la Pregunta dentro del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_question(Integer survey, Integer question, Integer position)
	throws SQLException {
		Integer domain = getDomainForSurvey_question( question , survey);
		Integer id =  super.insertSurvey_question( domain != null ? domain : getDefaultDomain(), survey, question, position );
		if ( domain != null ) { 
			survey_questionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Survey_question
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param survey Identificador del Cuestionario
	 * @param question Identificador de la Pregunta
	 * @param position Posicion de la Pregunta dentro del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_question(Integer domain, Integer survey, Integer question, Integer position)
	throws SQLException {
		Integer id =  super.insertSurvey_question(domain, survey, question, position);
		survey_questionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> warehouseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWarehousePk(Integer id){
		return warehouseDomains.get(id);
	}
	
	/**
	 * Warehouse
	 * @param department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForWarehouse( Integer department , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForDepartmentPk( department ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Warehouse
	 * @param id Identificador unico del Almacen
	 * @param name Nombre del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @param department Identificador del Departamento
	 * @param active Indica si el Almacen esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse(String name, Integer workplace, Integer department, Boolean active)
	throws SQLException {
		Integer domain = getDomainForWarehouse( department , workplace);
		Integer id =  super.insertWarehouse( domain != null ? domain : getDefaultDomain(), name, workplace, department, active );
		if ( domain != null ) { 
			warehouseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Warehouse
	 * @param id Identificador unico del Almacen
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @param department Identificador del Departamento
	 * @param active Indica si el Almacen esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse(Integer domain, String name, Integer workplace, Integer department, Boolean active)
	throws SQLException {
		Integer id =  super.insertWarehouse(domain, name, workplace, department, active);
		warehouseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_alumnDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_alumnPk(Integer id){
		return course_alumnDomains.get(id);
	}
	
	/**
	 * Course_alumn
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_alumn( Integer course , Integer customer){
		Integer domain = null;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_alumn
	 * @param id Identificador unico
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @param status Estado del alumno en el curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_alumn(Integer course, Integer customer, Short status)
	throws SQLException {
		Integer domain = getDomainForCourse_alumn( course , customer);
		Integer id =  super.insertCourse_alumn( domain != null ? domain : getDefaultDomain(), course, customer, status );
		if ( domain != null ) { 
			course_alumnDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_alumn
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @param status Estado del alumno en el curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_alumn(Integer domain, Integer course, Integer customer, Short status)
	throws SQLException {
		Integer id =  super.insertCourse_alumn(domain, course, customer, status);
		course_alumnDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> item_tariffDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItem_tariffPk(Integer id){
		return item_tariffDomains.get(id);
	}
	
	/**
	 * Item_tariff
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem_tariff( Integer item , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item_tariff
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @param type Tipo de Tarifa
	 * @param profit_percent Porcentaje de beneficio
	 * @param price Precio de Venta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_tariff(Integer item, Integer tariff, Short type, Double profit_percent, Double price)
	throws SQLException {
		Integer domain = getDomainForItem_tariff( item , tariff);
		Integer id =  super.insertItem_tariff( domain != null ? domain : getDefaultDomain(), item, tariff, type, profit_percent, price );
		if ( domain != null ) { 
			item_tariffDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item_tariff
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @param type Tipo de Tarifa
	 * @param profit_percent Porcentaje de beneficio
	 * @param price Precio de Venta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_tariff(Integer domain, Integer item, Integer tariff, Short type, Double profit_percent, Double price)
	throws SQLException {
		Integer id =  super.insertItem_tariff(domain, item, tariff, type, profit_percent, price);
		item_tariffDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_detailPk(Integer id){
		return invoice_detailDomains.get(id);
	}
	
	/**
	 * Invoice_detail
	 * @param invest_asset Identificador del Bien afecto
	 * @param invoice Identificador de la Factura
	 * @param item Identificador del Articulo del Detalle de Factura
	 * @param project Identificador del Proyecto
	 * @param seller Identificador de Agente Comercial
	 * @param warehouse Identificador del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_detail( Integer invest_asset , Integer invoice , Integer item , Integer project , Integer seller , Integer warehouse , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForInvest_assetPk( invest_asset ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_detail
	 * @param id Identificador unico del Detalle de la Factura
	 * @param invoice Identificador de la Factura
	 * @param invest_asset Identificador del Bien afecto
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro de la Factura
	 * @param item Identificador del Articulo del Detalle de Factura
	 * @param description Descripcion del Detalle de Factura
	 * @param quantity Cantidad del Detalle de Factura
	 * @param price Precio del Detalle de Factura
	 * @param discount_expr Descuentos del Detalle de Factura
	 * @param source Origen del Detalle de la Factura
	 * @param source_id Identificador del Origen del Detalle de la Factura
	 * @param taxable_base Base Imponible del Detalle de Factura
	 * @param taxes Tasas del Detalle de Factura
	 * @param prepayment Indica si el Detalle de Factura es un Suplido
	 * @param seller Identificador de Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail(Integer invoice, Integer invest_asset, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short source, Integer source_id, Double taxable_base, Double taxes, Boolean prepayment, Integer seller, Integer workplace, Integer warehouse, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForInvoice_detail( invest_asset , invoice , item , project , seller , warehouse , workplace);
		Integer id =  super.insertInvoice_detail( domain != null ? domain : getDefaultDomain(), invoice, invest_asset, project, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, prepayment, seller, workplace, warehouse, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			invoice_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_detail
	 * @param id Identificador unico del Detalle de la Factura
	 * @param domain Identificador del Dominio
	 * @param invoice Identificador de la Factura
	 * @param invest_asset Identificador del Bien afecto
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro de la Factura
	 * @param item Identificador del Articulo del Detalle de Factura
	 * @param description Descripcion del Detalle de Factura
	 * @param quantity Cantidad del Detalle de Factura
	 * @param price Precio del Detalle de Factura
	 * @param discount_expr Descuentos del Detalle de Factura
	 * @param source Origen del Detalle de la Factura
	 * @param source_id Identificador del Origen del Detalle de la Factura
	 * @param taxable_base Base Imponible del Detalle de Factura
	 * @param taxes Tasas del Detalle de Factura
	 * @param prepayment Indica si el Detalle de Factura es un Suplido
	 * @param seller Identificador de Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail(Integer domain, Integer invoice, Integer invest_asset, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short source, Integer source_id, Double taxable_base, Double taxes, Boolean prepayment, Integer seller, Integer workplace, Integer warehouse, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertInvoice_detail(domain, invoice, invest_asset, project, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, prepayment, seller, workplace, warehouse, creation_user, creation_date, modification_user, modification_date);
		invoice_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> allotment_itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAllotment_itemPk(Integer id){
		return allotment_itemDomains.get(id);
	}
	
	/**
	 * Allotment_item
	 * @param allotment Identificador del Cupo de seguridad
	 * @param item Identificador del Tipo de Habitacion
	 * @returns domain's ID
	*/
	protected Integer getDomainForAllotment_item( Integer allotment , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForAllotmentPk( allotment ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Allotment_item
	 * @param id Identificador unico
	 * @param allotment Identificador del Cupo de seguridad
	 * @param item Identificador del Tipo de Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment_item(Integer allotment, Integer item)
	throws SQLException {
		Integer domain = getDomainForAllotment_item( allotment , item);
		Integer id =  super.insertAllotment_item( domain != null ? domain : getDefaultDomain(), allotment, item );
		if ( domain != null ) { 
			allotment_itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Allotment_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param allotment Identificador del Cupo de seguridad
	 * @param item Identificador del Tipo de Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment_item(Integer domain, Integer allotment, Integer item)
	throws SQLException {
		Integer id =  super.insertAllotment_item(domain, allotment, item);
		allotment_itemDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> fs_vatDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_vatPk(Integer id){
		return fs_vatDomains.get(id);
	}
	
	/**
	 * Fs_vat
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_vat(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Fs_vat
	 * @param id Identificador unico
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param tax_refund_registry Inscrito en registro de devolucion
	 * @param number Numero de Decl. complementaria o sustitutiva
	 * @param prorata Porcentaje de prorrata
	 * @param replaced_number Numero de Declaracion complementada o sustituida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat(Integer year, Short period, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, Boolean tax_refund_registry, Integer number, Double prorata, String replaced_number)
	throws SQLException {
		Integer domain = getDomainForFs_vat();
		Integer id =  super.insertFs_vat( domain != null ? domain : getDefaultDomain(), year, period, comments, status, security_level, complementary, replacement, tax_refund_registry, number, prorata, replaced_number );
		if ( domain != null ) { 
			fs_vatDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_vat
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param tax_refund_registry Inscrito en registro de devolucion
	 * @param number Numero de Decl. complementaria o sustitutiva
	 * @param prorata Porcentaje de prorrata
	 * @param replaced_number Numero de Declaracion complementada o sustituida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat(Integer domain, Integer year, Short period, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, Boolean tax_refund_registry, Integer number, Double prorata, String replaced_number)
	throws SQLException {
		Integer id =  super.insertFs_vat(domain, year, period, comments, status, security_level, complementary, replacement, tax_refund_registry, number, prorata, replaced_number);
		fs_vatDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> creditorDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCreditorPk(Integer registry){
		return creditorDomains.get(registry);
	}
	
	/**
	 * Creditor
	 * @param account Identificador de la Cuenta Contable
	 * @param registry Registro del Acreedor
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForCreditor( Integer account , Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Creditor
	 * @param registry Registro del Acreedor
	 * @param withholding Indica si el Acreedor aplica retencion de impuestos
	 * @param vat_accrual_payment Indica si el Acreedor esta acogido al Regimen Especial de Criterio de Caja
	 * @param transaction Tipo de transacciones del Acreedor
	 * @param status Estado del Acreedor
	 * @param scope Identificador del Ambito
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertCreditor(Integer registry, Boolean withholding, Boolean vat_accrual_payment, Short transaction, Short status, Integer scope, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForCreditor( account , registry , scope);
		 super.insertCreditor( registry, domain != null ? domain : getDefaultDomain(), withholding, vat_accrual_payment, transaction, status, scope, account, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			creditorDomains.put(registry, domain);
		}
	}

	/**
	 * Creditor
	 * @param registry Registro del Acreedor
	 * @param domain Identificador del Dominio
	 * @param withholding Indica si el Acreedor aplica retencion de impuestos
	 * @param vat_accrual_payment Indica si el Acreedor esta acogido al Regimen Especial de Criterio de Caja
	 * @param transaction Tipo de transacciones del Acreedor
	 * @param status Estado del Acreedor
	 * @param scope Identificador del Ambito
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertCreditor(Integer registry, Integer domain, Boolean withholding, Boolean vat_accrual_payment, Short transaction, Short status, Integer scope, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		 super.insertCreditor(registry, domain, withholding, vat_accrual_payment, transaction, status, scope, account, creation_user, creation_date, modification_user, modification_date);
		creditorDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> account_entryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entryPk(Integer id){
		return account_entryDomains.get(id);
	}
	
	/**
	 * Account_entry
	 * @param account_period Ejercicio Contable del Asiento
	 * @param activity Identificador de la Actividad
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry( Integer account_period , Integer activity){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_periodPk( account_period ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprise_activityPk( activity ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry
	 * @param id Identificador unico del Asiento
	 * @param account_period Ejercicio Contable del Asiento
	 * @param activity Identificador de la Actividad
	 * @param entry_date Fecha del Asiento
	 * @param entry_type Tipo de Asiento
	 * @param journal Numero de diario del Asiento
	 * @param security_level Nivel de seguridad del Asiento
	 * @param comments Comentarios del Asiento
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry(Integer account_period, Integer activity, Date entry_date, Short entry_type, Integer journal, Short security_level, String comments, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForAccount_entry( account_period , activity);
		Integer id =  super.insertAccount_entry( domain != null ? domain : getDefaultDomain(), account_period, activity, entry_date, entry_type, journal, security_level, comments, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			account_entryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry
	 * @param id Identificador unico del Asiento
	 * @param domain Identificador del Dominio
	 * @param account_period Ejercicio Contable del Asiento
	 * @param activity Identificador de la Actividad
	 * @param entry_date Fecha del Asiento
	 * @param entry_type Tipo de Asiento
	 * @param journal Numero de diario del Asiento
	 * @param security_level Nivel de seguridad del Asiento
	 * @param comments Comentarios del Asiento
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry(Integer domain, Integer account_period, Integer activity, Date entry_date, Short entry_type, Integer journal, Short security_level, String comments, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertAccount_entry(domain, account_period, activity, entry_date, entry_type, journal, security_level, comments, creation_user, creation_date, modification_user, modification_date);
		account_entryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> account_helperDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_helperPk(Integer id){
		return account_helperDomains.get(id);
	}
	
	/**
	 * Account_helper
	 * @param account Cuenta Contable
	 * @param balancing_account Contrapartida
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_helper( Integer account , Integer balancing_account){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( balancing_account ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_helper
	 * @param id Identificador unico
	 * @param counter Contador, veces que se ha usado
	 * @param account Cuenta Contable
	 * @param balancing_account Contrapartida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_helper(Integer counter, Integer account, Integer balancing_account)
	throws SQLException {
		Integer domain = getDomainForAccount_helper( account , balancing_account);
		Integer id =  super.insertAccount_helper( domain != null ? domain : getDefaultDomain(), counter, account, balancing_account );
		if ( domain != null ) { 
			account_helperDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_helper
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param counter Contador, veces que se ha usado
	 * @param account Cuenta Contable
	 * @param balancing_account Contrapartida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_helper(Integer domain, Integer counter, Integer account, Integer balancing_account)
	throws SQLException {
		Integer id =  super.insertAccount_helper(domain, counter, account, balancing_account);
		account_helperDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> calendar_holidayDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCalendar_holidayPk(Integer id){
		return calendar_holidayDomains.get(id);
	}
	
	/**
	 * Calendar_holiday
	 * @param calendar Identificador del Calendario
	 * @returns domain's ID
	*/
	protected Integer getDomainForCalendar_holiday( Integer calendar){
		Integer domain = null;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Calendar_holiday
	 * @param id Identificador unico
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Festivo
	 * @param date Fecha del festivo
	 * @param day_type Tipo de dia
	 * @param hours Numero de horas laborables
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCalendar_holiday(Integer calendar, String description, Date date, Short day_type, Double hours)
	throws SQLException {
		Integer domain = getDomainForCalendar_holiday( calendar);
		Integer id =  super.insertCalendar_holiday( domain != null ? domain : getDefaultDomain(), calendar, description, date, day_type, hours );
		if ( domain != null ) { 
			calendar_holidayDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Calendar_holiday
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Festivo
	 * @param date Fecha del festivo
	 * @param day_type Tipo de dia
	 * @param hours Numero de horas laborables
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCalendar_holiday(Integer domain, Integer calendar, String description, Date date, Short day_type, Double hours)
	throws SQLException {
		Integer id =  super.insertCalendar_holiday(domain, calendar, description, date, day_type, hours);
		calendar_holidayDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_embargoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_embargoPk(Integer id){
		return contract_embargoDomains.get(id);
	}
	
	/**
	 * Contract_embargo
	 * @param contract Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_embargo( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_embargo
	 * @param id Identificador unico
	 * @param contract Contrato
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param amount Importe
	 * @param expression Formula
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_embargo(Integer contract, Date start_date, Date end_date, Double amount, String expression, String description)
	throws SQLException {
		Integer domain = getDomainForContract_embargo( contract);
		Integer id =  super.insertContract_embargo( domain != null ? domain : getDefaultDomain(), contract, start_date, end_date, amount, expression, description );
		if ( domain != null ) { 
			contract_embargoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_embargo
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Contrato
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param amount Importe
	 * @param expression Formula
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_embargo(Integer domain, Integer contract, Date start_date, Date end_date, Double amount, String expression, String description)
	throws SQLException {
		Integer id =  super.insertContract_embargo(domain, contract, start_date, end_date, amount, expression, description);
		contract_embargoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> amortization_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAmortization_typePk(Integer id){
		return amortization_typeDomains.get(id);
	}
	
	/**
	 * Amortization_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForAmortization_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Amortization_type
	 * @param id Identificador unico
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la amortizacion
	 * @param percentage Porcentaje de amortizacion
	 * @param description Descripcion del Tipo de Amortizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization_type(String fixed_asset_account, String accumulated_account, String allocation_account, Double percentage, String description)
	throws SQLException {
		Integer domain = getDomainForAmortization_type();
		Integer id =  super.insertAmortization_type( domain != null ? domain : getDefaultDomain(), fixed_asset_account, accumulated_account, allocation_account, percentage, description );
		if ( domain != null ) { 
			amortization_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Amortization_type
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la amortizacion
	 * @param percentage Porcentaje de amortizacion
	 * @param description Descripcion del Tipo de Amortizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization_type(Integer domain, String fixed_asset_account, String accumulated_account, String allocation_account, Double percentage, String description)
	throws SQLException {
		Integer id =  super.insertAmortization_type(domain, fixed_asset_account, accumulated_account, allocation_account, percentage, description);
		amortization_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_mod347Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_mod347Pk(Integer id){
		return fs_mod347Domains.get(id);
	}
	
	/**
	 * Fs_mod347
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_mod347(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Fs_mod347
	 * @param id Identificador unico
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param number Numero de declaracion
	 * @param replaced_number Numero de declaracion anterior
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_phone Telefono Persona de Contacto
	 * @param contact_person Persona de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param representative_document NIF Representante Legal
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod347(Integer year, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, String number, String replaced_number, String document, String name, String contact_phone, String contact_person, String contact_mail, String representative_document, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_mod347();
		Integer id =  super.insertFs_mod347( domain != null ? domain : getDefaultDomain(), year, administration, comments, status, security_level, complementary, replacement, number, replaced_number, document, name, contact_phone, contact_person, contact_mail, representative_document, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_mod347Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_mod347
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param number Numero de declaracion
	 * @param replaced_number Numero de declaracion anterior
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_phone Telefono Persona de Contacto
	 * @param contact_person Persona de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param representative_document NIF Representante Legal
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod347(Integer domain, Integer year, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, String number, String replaced_number, String document, String name, String contact_phone, String contact_person, String contact_mail, String representative_document, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_mod347(domain, year, administration, comments, status, security_level, complementary, replacement, number, replaced_number, document, name, contact_phone, contact_person, contact_mail, representative_document, creation_user, creation_date, modification_user, modification_date);
		fs_mod347Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_levelDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_levelPk(Integer id){
		return course_levelDomains.get(id);
	}
	
	/**
	 * Course_level
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_level(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Course_level
	 * @param id Identificador unico del Nivel
	 * @param description Descripcion del Nivel
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_level(String description)
	throws SQLException {
		Integer domain = getDomainForCourse_level();
		Integer id =  super.insertCourse_level( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			course_levelDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_level
	 * @param id Identificador unico del Nivel
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Nivel
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_level(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertCourse_level(domain, description);
		course_levelDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_modelDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_modelPk(Integer id){
		return fs_modelDomains.get(id);
	}
	
	/**
	 * Fs_model
	 * @param finance Identificador de Vencimiento
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model( Integer finance){
		Integer domain = null;
			if ( ( domain = getDomainForFinancePk( finance ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model
	 * @param id Identificador unico
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param withoutActivity Sin Actividad
	 * @param model Tipo de modelo
	 * @param number Numero de Declaracion
	 * @param replaced_number Numero de Declaracion complementada o sustituida
	 * @param comments Comentarios de la Declaracion
	 * @param finance Identificador de Vencimiento
	 * @param document NIF
	 * @param surname Apellidos
	 * @param name Nombre
	 * @param street_initial Sigla via
	 * @param street_name Nombre de la via publica
	 * @param street_number Numero de la via publica
	 * @param street_stair Escalera
	 * @param street_floor Piso
	 * @param street_door Puerta
	 * @param phone Telefono
	 * @param town Municipio
	 * @param province Provincia
	 * @param zip Codigo Postal
	 * @param admon_aeat Codigo de Administracion AEAT
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo
	 * @param contact_cellular Telf. Movil
	 * @param contact_email Email
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model(Integer year, Short period, Short administration, Short status, Short security_level, Boolean complementary, Boolean replacement, Boolean withoutActivity, String model, String number, String replaced_number, String comments, Integer finance, String document, String surname, String name, String street_initial, String street_name, String street_number, String street_stair, String street_floor, String street_door, String phone, String town, String province, String zip, String admon_aeat, String contact_person, String contact_phone, String contact_cellular, String contact_email, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_model( finance);
		Integer id =  super.insertFs_model( domain != null ? domain : getDefaultDomain(), year, period, administration, status, security_level, complementary, replacement, withoutActivity, model, number, replaced_number, comments, finance, document, surname, name, street_initial, street_name, street_number, street_stair, street_floor, street_door, phone, town, province, zip, admon_aeat, contact_person, contact_phone, contact_cellular, contact_email, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_modelDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param withoutActivity Sin Actividad
	 * @param model Tipo de modelo
	 * @param number Numero de Declaracion
	 * @param replaced_number Numero de Declaracion complementada o sustituida
	 * @param comments Comentarios de la Declaracion
	 * @param finance Identificador de Vencimiento
	 * @param document NIF
	 * @param surname Apellidos
	 * @param name Nombre
	 * @param street_initial Sigla via
	 * @param street_name Nombre de la via publica
	 * @param street_number Numero de la via publica
	 * @param street_stair Escalera
	 * @param street_floor Piso
	 * @param street_door Puerta
	 * @param phone Telefono
	 * @param town Municipio
	 * @param province Provincia
	 * @param zip Codigo Postal
	 * @param admon_aeat Codigo de Administracion AEAT
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo
	 * @param contact_cellular Telf. Movil
	 * @param contact_email Email
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model(Integer domain, Integer year, Short period, Short administration, Short status, Short security_level, Boolean complementary, Boolean replacement, Boolean withoutActivity, String model, String number, String replaced_number, String comments, Integer finance, String document, String surname, String name, String street_initial, String street_name, String street_number, String street_stair, String street_floor, String street_door, String phone, String town, String province, String zip, String admon_aeat, String contact_person, String contact_phone, String contact_cellular, String contact_email, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_model(domain, year, period, administration, status, security_level, complementary, replacement, withoutActivity, model, number, replaced_number, comments, finance, document, surname, name, street_initial, street_name, street_number, street_stair, street_floor, street_door, phone, town, province, zip, admon_aeat, contact_person, contact_phone, contact_cellular, contact_email, creation_user, creation_date, modification_user, modification_date);
		fs_modelDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> task_holder_workgroupDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTask_holder_workgroupPk(Integer id){
		return task_holder_workgroupDomains.get(id);
	}
	
	/**
	 * Task_holder_workgroup
	 * @param task_holder Identificador del Responsable de la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask_holder_workgroup( Integer task_holder , Integer workgroup){
		Integer domain = null;
			if ( ( domain = getDomainForTask_holderPk( task_holder ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( workgroup ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task_holder_workgroup
	 * @param id Identificador unico
	 * @param task_holder Identificador del Responsable de la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_holder_workgroup(Integer task_holder, Integer workgroup)
	throws SQLException {
		Integer domain = getDomainForTask_holder_workgroup( task_holder , workgroup);
		Integer id =  super.insertTask_holder_workgroup( domain != null ? domain : getDefaultDomain(), task_holder, workgroup );
		if ( domain != null ) { 
			task_holder_workgroupDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Task_holder_workgroup
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param task_holder Identificador del Responsable de la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_holder_workgroup(Integer domain, Integer task_holder, Integer workgroup)
	throws SQLException {
		Integer id =  super.insertTask_holder_workgroup(domain, task_holder, workgroup);
		task_holder_workgroupDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> offer_detail_commissionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForOffer_detail_commissionPk(Integer id){
		return offer_detail_commissionDomains.get(id);
	}
	
	/**
	 * Offer_detail_commission
	 * @param offer_detail Identificador de la Linea de Presupuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForOffer_detail_commission( Integer offer_detail){
		Integer domain = null;
			if ( ( domain = getDomainForOffer_detailPk( offer_detail ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Offer_detail_commission
	 * @param id Identificador unico
	 * @param offer_detail Identificador de la Linea de Presupuesto
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_detail_commission(Integer offer_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
		Integer domain = getDomainForOffer_detail_commission( offer_detail);
		Integer id =  super.insertOffer_detail_commission( domain != null ? domain : getDefaultDomain(), offer_detail, commission, amount, status, pay_date );
		if ( domain != null ) { 
			offer_detail_commissionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Offer_detail_commission
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param offer_detail Identificador de la Linea de Presupuesto
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_detail_commission(Integer domain, Integer offer_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
		Integer id =  super.insertOffer_detail_commission(domain, offer_detail, commission, amount, status, pay_date);
		offer_detail_commissionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salary_bonusDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_bonusPk(Integer id){
		return salary_bonusDomains.get(id);
	}
	
	/**
	 * Salary_bonus
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_bonus( Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_bonus
	 * @param id Identificador unico
	 * @param salary Recibo del pago de salarios
	 * @param bonus_concept Codigo del concepto
	 * @param amount Importe
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_bonus(Integer salary, String bonus_concept, Double amount, String description)
	throws SQLException {
		Integer domain = getDomainForSalary_bonus( salary);
		Integer id =  super.insertSalary_bonus( domain != null ? domain : getDefaultDomain(), salary, bonus_concept, amount, description );
		if ( domain != null ) { 
			salary_bonusDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_bonus
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param bonus_concept Codigo del concepto
	 * @param amount Importe
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_bonus(Integer domain, Integer salary, String bonus_concept, Double amount, String description)
	throws SQLException {
		Integer id =  super.insertSalary_bonus(domain, salary, bonus_concept, amount, description);
		salary_bonusDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> amortization_invoiceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAmortization_invoicePk(Integer id){
		return amortization_invoiceDomains.get(id);
	}
	
	/**
	 * Amortization_invoice
	 * @param amortization Identificador de la Ficha de Amortizacion
	 * @param invoice Identificador de la Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForAmortization_invoice( Integer amortization , Integer invoice){
		Integer domain = null;
			if ( ( domain = getDomainForAmortizationPk( amortization ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Amortization_invoice
	 * @param id Identificador unico
	 * @param amortization Identificador de la Ficha de Amortizacion
	 * @param invoice Identificador de la Factura
	 * @param sales Indica si es venta de inmovilizado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization_invoice(Integer amortization, Integer invoice, Boolean sales)
	throws SQLException {
		Integer domain = getDomainForAmortization_invoice( amortization , invoice);
		Integer id =  super.insertAmortization_invoice( domain != null ? domain : getDefaultDomain(), amortization, invoice, sales );
		if ( domain != null ) { 
			amortization_invoiceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Amortization_invoice
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param amortization Identificador de la Ficha de Amortizacion
	 * @param invoice Identificador de la Factura
	 * @param sales Indica si es venta de inmovilizado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization_invoice(Integer domain, Integer amortization, Integer invoice, Boolean sales)
	throws SQLException {
		Integer id =  super.insertAmortization_invoice(domain, amortization, invoice, sales);
		amortization_invoiceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> bonus_conceptDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBonus_conceptPk(Integer id){
		return bonus_conceptDomains.get(id);
	}
	
	/**
	 * Bonus_concept
	 * @returns domain's ID
	*/
	protected Integer getDomainForBonus_concept(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Bonus_concept
	 * @param id Identificador unico
	 * @param expression Importe
	 * @param description Descripcion
	 * @param type Tipo de Bonificacion Salarial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBonus_concept(String expression, String description, Short type)
	throws SQLException {
		Integer domain = getDomainForBonus_concept();
		Integer id =  super.insertBonus_concept( domain != null ? domain : getDefaultDomain(), expression, description, type );
		if ( domain != null ) { 
			bonus_conceptDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Bonus_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param expression Importe
	 * @param description Descripcion
	 * @param type Tipo de Bonificacion Salarial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBonus_concept(Integer domain, String expression, String description, Short type)
	throws SQLException {
		Integer id =  super.insertBonus_concept(domain, expression, description, type);
		bonus_conceptDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> contact_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContact_dataPk(Integer id){
		return contact_dataDomains.get(id);
	}
	
	/**
	 * Contact_data
	 * @returns domain's ID
	*/
	protected Integer getDomainForContact_data(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Contact_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param surname Apellido
	 * @param address Direccion
	 * @param postalCode Codigo postal
	 * @param city Localidad
	 * @param contactState Estado
	 * @param country Pais
	 * @param phone Telefono
	 * @param cellularPhone Movil
	 * @param fax Fax
	 * @param email Email
	 * @param note Nota
	 * @param organization Organizaciùn
	 * @param title Cargo
	 * @param organizationAddress Direcciùn de la Organizaciùn
	 * @param organizationPostalCode Codigo postal de la Organizaciùn
	 * @param organizationCity Localidad de la Organizaciùn
	 * @param organizationState Estado de la Organizaciùn
	 * @param organizationPhone Telefono de la Organizaciùn
	 * @param organizationFax Fax de la Organizaciùn
	 * @param web Web
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact_data(String name, String surname, String address, String postalCode, String city, String contactState, String country, String phone, String cellularPhone, String fax, String email, String note, String organization, String title, String organizationAddress, String organizationPostalCode, String organizationCity, String organizationState, String organizationPhone, String organizationFax, String web)
	throws SQLException {
		Integer domain = getDomainForContact_data();
		Integer id =  super.insertContact_data( domain != null ? domain : getDefaultDomain(), name, surname, address, postalCode, city, contactState, country, phone, cellularPhone, fax, email, note, organization, title, organizationAddress, organizationPostalCode, organizationCity, organizationState, organizationPhone, organizationFax, web );
		if ( domain != null ) { 
			contact_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contact_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param surname Apellido
	 * @param address Direccion
	 * @param postalCode Codigo postal
	 * @param city Localidad
	 * @param contactState Estado
	 * @param country Pais
	 * @param phone Telefono
	 * @param cellularPhone Movil
	 * @param fax Fax
	 * @param email Email
	 * @param note Nota
	 * @param organization Organizaciùn
	 * @param title Cargo
	 * @param organizationAddress Direcciùn de la Organizaciùn
	 * @param organizationPostalCode Codigo postal de la Organizaciùn
	 * @param organizationCity Localidad de la Organizaciùn
	 * @param organizationState Estado de la Organizaciùn
	 * @param organizationPhone Telefono de la Organizaciùn
	 * @param organizationFax Fax de la Organizaciùn
	 * @param web Web
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact_data(Integer domain, String name, String surname, String address, String postalCode, String city, String contactState, String country, String phone, String cellularPhone, String fax, String email, String note, String organization, String title, String organizationAddress, String organizationPostalCode, String organizationCity, String organizationState, String organizationPhone, String organizationFax, String web)
	throws SQLException {
		Integer id =  super.insertContact_data(domain, name, surname, address, postalCode, city, contactState, country, phone, cellularPhone, fax, email, note, organization, title, organizationAddress, organizationPostalCode, organizationCity, organizationState, organizationPhone, organizationFax, web);
		contact_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> user_workgroupDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForUser_workgroupPk(Integer id){
		return user_workgroupDomains.get(id);
	}
	
	/**
	 * User_workgroup
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForUser_workgroup( Integer user_id , Integer workgroup){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( workgroup ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * User_workgroup
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser_workgroup(Integer user_id, Integer workgroup)
	throws SQLException {
		Integer domain = getDomainForUser_workgroup( user_id , workgroup);
		Integer id =  super.insertUser_workgroup( domain != null ? domain : getDefaultDomain(), user_id, workgroup );
		if ( domain != null ) { 
			user_workgroupDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * User_workgroup
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser_workgroup(Integer domain, Integer user_id, Integer workgroup)
	throws SQLException {
		Integer id =  super.insertUser_workgroup(domain, user_id, workgroup);
		user_workgroupDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_mod349Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_mod349Pk(Integer id){
		return fs_mod349Domains.get(id);
	}
	
	/**
	 * Fs_mod349
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_mod349(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Fs_mod349
	 * @param id Identificador unico
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param administration Administracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param number Numero de declaracion
	 * @param replaced_number Numero de declaracion anterior
	 * @param document NIF
	 * @param name Nombre
	 * @param representative_document NIF Representante Legal
	 * @param contact_phone Telefono Persona de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param contact_person Persona de Contacto
	 * @param periodicity_change Indicador Cambio Periodicidad
	 * @param diff_enabled Declaracion por diferencia
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod349(Integer year, Short period, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, String number, String replaced_number, String document, String name, String representative_document, String contact_phone, String contact_mail, String contact_person, Boolean periodicity_change, Boolean diff_enabled, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_mod349();
		Integer id =  super.insertFs_mod349( domain != null ? domain : getDefaultDomain(), year, period, administration, comments, status, security_level, complementary, replacement, number, replaced_number, document, name, representative_document, contact_phone, contact_mail, contact_person, periodicity_change, diff_enabled, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_mod349Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_mod349
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param administration Administracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param number Numero de declaracion
	 * @param replaced_number Numero de declaracion anterior
	 * @param document NIF
	 * @param name Nombre
	 * @param representative_document NIF Representante Legal
	 * @param contact_phone Telefono Persona de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param contact_person Persona de Contacto
	 * @param periodicity_change Indicador Cambio Periodicidad
	 * @param diff_enabled Declaracion por diferencia
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod349(Integer domain, Integer year, Short period, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, String number, String replaced_number, String document, String name, String representative_document, String contact_phone, String contact_mail, String contact_person, Boolean periodicity_change, Boolean diff_enabled, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_mod349(domain, year, period, administration, comments, status, security_level, complementary, replacement, number, replaced_number, document, name, representative_document, contact_phone, contact_mail, contact_person, periodicity_change, diff_enabled, creation_user, creation_date, modification_user, modification_date);
		fs_mod349Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> task_commentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTask_commentPk(Integer id){
		return task_commentDomains.get(id);
	}
	
	/**
	 * Task_comment
	 * @param task Identificador de la tarea
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask_comment( Integer task){
		Integer domain = null;
			if ( ( domain = getDomainForTaskPk( task ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task_comment
	 * @param id Identificador unico
	 * @param task Identificador de la tarea
	 * @param comment Comentario de la Tarea
	 * @param source Origen del comentario
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_comment(Integer task, String comment, Integer source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTask_comment( task);
		Integer id =  super.insertTask_comment( domain != null ? domain : getDefaultDomain(), task, comment, source, source_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			task_commentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Task_comment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param task Identificador de la tarea
	 * @param comment Comentario de la Tarea
	 * @param source Origen del comentario
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_comment(Integer domain, Integer task, String comment, Integer source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertTask_comment(domain, task, comment, source, source_id, creation_user, creation_date, modification_user, modification_date);
		task_commentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> posDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPosPk(Integer id){
		return posDomains.get(id);
	}
	
	/**
	 * Pos
	 * @param customer Identificador del Cliente
	 * @param department Identificador del Departamento
	 * @param item_invoice Identificador del Articulo facturable
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForPos( Integer customer , Integer department , Integer item_invoice , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForDepartmentPk( department ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item_invoice ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Pos
	 * @param id Identificador unico
	 * @param department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @param name Nombre
	 * @param series Serie del POS
	 * @param customer Identificador del Cliente
	 * @param price_editable Indica si el precio es editable
	 * @param discount_editable Indica si el descuento es editable
	 * @param invoiceable Indica si el Punto de Venta es facturable
	 * @param item_invoice Identificador del Articulo facturable
	 * @param display_mode Modo de visualizacion en pantalla
	 * @param num_rows Numero de filas en la vista tipo Hosteleria
	 * @param num_cols Numero de columnas en la vista tipo Hosteleria
	 * @param pin_pad Indicador de si es un Pin Pad
	 * @param commerce Clave de firma del comercio
	 * @param signature_password Clave de firma del comercio
	 * @param terminal Numero de terminal
	 * @param port_configuration Configuracion de puerto
	 * @param pos_version Version actual
	 * @param active Indica si el TPV esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos(Integer department, Integer workplace, String name, String series, Integer customer, Boolean price_editable, Boolean discount_editable, Boolean invoiceable, Integer item_invoice, Short display_mode, Integer num_rows, Integer num_cols, Boolean pin_pad, String commerce, String signature_password, String terminal, String port_configuration, String pos_version, Boolean active)
	throws SQLException {
		Integer domain = getDomainForPos( customer , department , item_invoice , workplace);
		Integer id =  super.insertPos( domain != null ? domain : getDefaultDomain(), department, workplace, name, series, customer, price_editable, discount_editable, invoiceable, item_invoice, display_mode, num_rows, num_cols, pin_pad, commerce, signature_password, terminal, port_configuration, pos_version, active );
		if ( domain != null ) { 
			posDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pos
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @param name Nombre
	 * @param series Serie del POS
	 * @param customer Identificador del Cliente
	 * @param price_editable Indica si el precio es editable
	 * @param discount_editable Indica si el descuento es editable
	 * @param invoiceable Indica si el Punto de Venta es facturable
	 * @param item_invoice Identificador del Articulo facturable
	 * @param display_mode Modo de visualizacion en pantalla
	 * @param num_rows Numero de filas en la vista tipo Hosteleria
	 * @param num_cols Numero de columnas en la vista tipo Hosteleria
	 * @param pin_pad Indicador de si es un Pin Pad
	 * @param commerce Clave de firma del comercio
	 * @param signature_password Clave de firma del comercio
	 * @param terminal Numero de terminal
	 * @param port_configuration Configuracion de puerto
	 * @param pos_version Version actual
	 * @param active Indica si el TPV esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos(Integer domain, Integer department, Integer workplace, String name, String series, Integer customer, Boolean price_editable, Boolean discount_editable, Boolean invoiceable, Integer item_invoice, Short display_mode, Integer num_rows, Integer num_cols, Boolean pin_pad, String commerce, String signature_password, String terminal, String port_configuration, String pos_version, Boolean active)
	throws SQLException {
		Integer id =  super.insertPos(domain, department, workplace, name, series, customer, price_editable, discount_editable, invoiceable, item_invoice, display_mode, num_rows, num_cols, pin_pad, commerce, signature_password, terminal, port_configuration, pos_version, active);
		posDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_roomDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_roomPk(Integer id){
		return project_reservation_roomDomains.get(id);
	}
	
	/**
	 * Project_reservation_room
	 * @param item Identificador del Tipo de Habitacion
	 * @param project_reservation Identificador de la Reserva
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_room( Integer item , Integer project_reservation , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservationPk( project_reservation ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_room
	 * @param id Identificador unico
	 * @param project_reservation Identificador de la Reserva
	 * @param room_index Numero de Habitacion
	 * @param room_code Codigo de Habitacion en origen
	 * @param item Identificador del Tipo de Habitacion
	 * @param allotment_rate_code Codigo de Cupo
	 * @param rate_plan Codigo de Tarifa en origen
	 * @param tariff Identificador de la Tarifa
	 * @param adults Numero de adultos
	 * @param children Numero de niùos
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_room(Integer project_reservation, Short room_index, String room_code, Integer item, String allotment_rate_code, String rate_plan, Integer tariff, Integer adults, Integer children, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_room( item , project_reservation , tariff);
		Integer id =  super.insertProject_reservation_room( domain != null ? domain : getDefaultDomain(), project_reservation, room_index, room_code, item, allotment_rate_code, rate_plan, tariff, adults, children, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			project_reservation_roomDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_room
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param room_index Numero de Habitacion
	 * @param room_code Codigo de Habitacion en origen
	 * @param item Identificador del Tipo de Habitacion
	 * @param allotment_rate_code Codigo de Cupo
	 * @param rate_plan Codigo de Tarifa en origen
	 * @param tariff Identificador de la Tarifa
	 * @param adults Numero de adultos
	 * @param children Numero de niùos
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_room(Integer domain, Integer project_reservation, Short room_index, String room_code, Integer item, String allotment_rate_code, String rate_plan, Integer tariff, Integer adults, Integer children, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProject_reservation_room(domain, project_reservation, room_index, room_code, item, allotment_rate_code, rate_plan, tariff, adults, children, creation_user, creation_date, modification_user, modification_date);
		project_reservation_roomDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> reservation_requestDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForReservation_requestPk(Integer id){
		return reservation_requestDomains.get(id);
	}
	
	/**
	 * Reservation_request
	 * @param agency Identificador de la agencia de viajes
	 * @param company Identificador de la empresa
	 * @param hotel Identificador del Hotel
	 * @returns domain's ID
	*/
	protected Integer getDomainForReservation_request( Integer agency , Integer company , Integer hotel){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( agency ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( company ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Reservation_request
	 * @param id Identificador unico
	 * @param hotel Identificador del Hotel
	 * @param code Localizador
	 * @param start_date Fecha de entrada
	 * @param end_date Fecha de salida
	 * @param agency Identificador de la agencia de viajes
	 * @param company Identificador de la empresa
	 * @param booking_holder Titular
	 * @param request_counter Numero de solicitudes enviadas
	 * @param remarks Observaciones
	 * @param active Indica si la Solicitud esta activa o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request(Integer hotel, String code, Date start_date, Date end_date, Integer agency, Integer company, Short booking_holder, Integer request_counter, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForReservation_request( agency , company , hotel);
		Integer id =  super.insertReservation_request( domain != null ? domain : getDefaultDomain(), hotel, code, start_date, end_date, agency, company, booking_holder, request_counter, remarks, active, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			reservation_requestDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Reservation_request
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param code Localizador
	 * @param start_date Fecha de entrada
	 * @param end_date Fecha de salida
	 * @param agency Identificador de la agencia de viajes
	 * @param company Identificador de la empresa
	 * @param booking_holder Titular
	 * @param request_counter Numero de solicitudes enviadas
	 * @param remarks Observaciones
	 * @param active Indica si la Solicitud esta activa o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request(Integer domain, Integer hotel, String code, Date start_date, Date end_date, Integer agency, Integer company, Short booking_holder, Integer request_counter, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertReservation_request(domain, hotel, code, start_date, end_date, agency, company, booking_holder, request_counter, remarks, active, creation_user, creation_date, modification_user, modification_date);
		reservation_requestDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> survey_response_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSurvey_response_detailPk(Integer id){
		return survey_response_detailDomains.get(id);
	}
	
	/**
	 * Survey_response_detail
	 * @param question Identificador de la Pregunta
	 * @param surveyResponse Identificador de la Respuesta del Cuestionario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSurvey_response_detail( Integer question , Integer surveyResponse){
		Integer domain = null;
			if ( ( domain = getDomainForQuestionPk( question ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurvey_responsePk( surveyResponse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Survey_response_detail
	 * @param id Identificador unico
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @param question Identificador de la Pregunta
	 * @param surveyResponse Identificador de la Respuesta del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_response_detail(String value_text, Double value_number, Timestamp value_date, Integer question, Integer surveyResponse)
	throws SQLException {
		Integer domain = getDomainForSurvey_response_detail( question , surveyResponse);
		Integer id =  super.insertSurvey_response_detail( domain != null ? domain : getDefaultDomain(), value_text, value_number, value_date, question, surveyResponse );
		if ( domain != null ) { 
			survey_response_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Survey_response_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @param question Identificador de la Pregunta
	 * @param surveyResponse Identificador de la Respuesta del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_response_detail(Integer domain, String value_text, Double value_number, Timestamp value_date, Integer question, Integer surveyResponse)
	throws SQLException {
		Integer id =  super.insertSurvey_response_detail(domain, value_text, value_number, value_date, question, surveyResponse);
		survey_response_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> activity_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForActivity_typePk(Integer id){
		return activity_typeDomains.get(id);
	}
	
	/**
	 * Activity_type
	 * @param project_type Tipo de Proyecto
	 * @returns domain's ID
	*/
	protected Integer getDomainForActivity_type( Integer project_type){
		Integer domain = null;
			if ( ( domain = getDomainForProject_typePk( project_type ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Activity_type
	 * @param id Identificador unico del Tipo de Actividad
	 * @param description Descripcion del Tipo de Actividad
	 * @param project_type Tipo de Proyecto
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertActivity_type(String description, Integer project_type, Boolean active)
	throws SQLException {
		Integer domain = getDomainForActivity_type( project_type);
		Integer id =  super.insertActivity_type( domain != null ? domain : getDefaultDomain(), description, project_type, active );
		if ( domain != null ) { 
			activity_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Activity_type
	 * @param id Identificador unico del Tipo de Actividad
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Actividad
	 * @param project_type Tipo de Proyecto
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertActivity_type(Integer domain, String description, Integer project_type, Boolean active)
	throws SQLException {
		Integer id =  super.insertActivity_type(domain, description, project_type, active);
		activity_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> hotelDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForHotelPk(Integer id){
		return hotelDomains.get(id);
	}
	
	/**
	 * Hotel
	 * @param scope Identificador del Ambito
	 * @param service_catalogue Identificador del Catalogo de Servicios
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForHotel( Integer scope , Integer service_catalogue , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForCataloguePk( service_catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Hotel
	 * @param id Identificador unico
	 * @param code Codigo del Hotel
	 * @param scope Identificador del Ambito
	 * @param workplace Identificador del Centro de Trabajo
	 * @param phone Telefono del Hotel
	 * @param fax Fax del Hotel
	 * @param email Email del Hotel
	 * @param web Web del Hotel
	 * @param service_catalogue Identificador del Catalogo de Servicios
	 * @param sheet_changing Dias entre cambio de sabanas
	 * @param police_code Codigo del Hotel para la policia
	 * @param police_counter Contador para envio de ficheros a la policia
	 * @param tourist_tax Indica si el Hotel aplica Tasa turistica o no
	 * @param active Indica si el Hotel esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHotel(String code, Integer scope, Integer workplace, String phone, String fax, String email, String web, Integer service_catalogue, Short sheet_changing, String police_code, Integer police_counter, Boolean tourist_tax, Boolean active)
	throws SQLException {
		Integer domain = getDomainForHotel( scope , service_catalogue , workplace);
		Integer id =  super.insertHotel( domain != null ? domain : getDefaultDomain(), code, scope, workplace, phone, fax, email, web, service_catalogue, sheet_changing, police_code, police_counter, tourist_tax, active );
		if ( domain != null ) { 
			hotelDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Hotel
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo del Hotel
	 * @param scope Identificador del Ambito
	 * @param workplace Identificador del Centro de Trabajo
	 * @param phone Telefono del Hotel
	 * @param fax Fax del Hotel
	 * @param email Email del Hotel
	 * @param web Web del Hotel
	 * @param service_catalogue Identificador del Catalogo de Servicios
	 * @param sheet_changing Dias entre cambio de sabanas
	 * @param police_code Codigo del Hotel para la policia
	 * @param police_counter Contador para envio de ficheros a la policia
	 * @param tourist_tax Indica si el Hotel aplica Tasa turistica o no
	 * @param active Indica si el Hotel esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHotel(Integer domain, String code, Integer scope, Integer workplace, String phone, String fax, String email, String web, Integer service_catalogue, Short sheet_changing, String police_code, Integer police_counter, Boolean tourist_tax, Boolean active)
	throws SQLException {
		Integer id =  super.insertHotel(domain, code, scope, workplace, phone, fax, email, web, service_catalogue, sheet_changing, police_code, police_counter, tourist_tax, active);
		hotelDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commissionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommissionPk(Integer id){
		return commissionDomains.get(id);
	}
	
	/**
	 * Commission
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommission(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Commission
	 * @param id Identificador unico
	 * @param name Descripcion de la Comision
	 * @param start_date Fecha de inicio de la Comision
	 * @param end_date Fecha de fin de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission(String name, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForCommission();
		Integer id =  super.insertCommission( domain != null ? domain : getDefaultDomain(), name, start_date, end_date );
		if ( domain != null ) { 
			commissionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commission
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Descripcion de la Comision
	 * @param start_date Fecha de inicio de la Comision
	 * @param end_date Fecha de fin de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission(Integer domain, String name, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertCommission(domain, name, start_date, end_date);
		commissionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rattach_tagDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRattach_tagPk(Integer id){
		return rattach_tagDomains.get(id);
	}
	
	/**
	 * Rattach_tag
	 * @param rattach Identificador del Archivo Adjunto
	 * @param tag Identificador de la Etiqueta
	 * @returns domain's ID
	*/
	protected Integer getDomainForRattach_tag( Integer rattach , Integer tag){
		Integer domain = null;
			if ( ( domain = getDomainForRattachPk( rattach ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( tag ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rattach_tag
	 * @param id Identificador unico
	 * @param rattach Identificador del Archivo Adjunto
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRattach_tag(Integer rattach, Integer tag)
	throws SQLException {
		Integer domain = getDomainForRattach_tag( rattach , tag);
		Integer id =  super.insertRattach_tag( domain != null ? domain : getDefaultDomain(), rattach, tag );
		if ( domain != null ) { 
			rattach_tagDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rattach_tag
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param rattach Identificador del Archivo Adjunto
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRattach_tag(Integer domain, Integer rattach, Integer tag)
	throws SQLException {
		Integer id =  super.insertRattach_tag(domain, rattach, tag);
		rattach_tagDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tariffDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTariffPk(Integer id){
		return tariffDomains.get(id);
	}
	
	/**
	 * Tariff
	 * @returns domain's ID
	*/
	protected Integer getDomainForTariff(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Tariff
	 * @param id Identificador unico de la Tarifa
	 * @param code Codigo de la Tarifa
	 * @param name Nombre de la Tarifa
	 * @param purchase Indica si se trata de una Tarifa de Compras o Ventas
	 * @param discount Descuento general de la Tarifa
	 * @param active Indica si la Tarifa esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff(String code, String name, Boolean purchase, Double discount, Boolean active)
	throws SQLException {
		Integer domain = getDomainForTariff();
		Integer id =  super.insertTariff( domain != null ? domain : getDefaultDomain(), code, name, purchase, discount, active );
		if ( domain != null ) { 
			tariffDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tariff
	 * @param id Identificador unico de la Tarifa
	 * @param domain Identificador del Dominio
	 * @param code Codigo de la Tarifa
	 * @param name Nombre de la Tarifa
	 * @param purchase Indica si se trata de una Tarifa de Compras o Ventas
	 * @param discount Descuento general de la Tarifa
	 * @param active Indica si la Tarifa esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff(Integer domain, String code, String name, Boolean purchase, Double discount, Boolean active)
	throws SQLException {
		Integer id =  super.insertTariff(domain, code, name, purchase, discount, active);
		tariffDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> bank_statementDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBank_statementPk(Integer id){
		return bank_statementDomains.get(id);
	}
	
	/**
	 * Bank_statement
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @returns domain's ID
	*/
	protected Integer getDomainForBank_statement( Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Bank_statement
	 * @param id Identificador unico
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @param lot_number Numero de lote
	 * @param operation_date Fecha de operacion
	 * @param common_concept Concepto comun
	 * @param own_concept Concepto propio
	 * @param payment Indica si es un pago
	 * @param amount Importe
	 * @param document Numero de documento
	 * @param reference1 Referencia 1
	 * @param reference2 Referencia 2
	 * @param description Descripcion
	 * @param reliability Fiabilidad del punteo
	 * @param security_level Nivel de seguridad
	 * @param status Estado
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_statement(Integer rbank, Integer lot_number, Date operation_date, Short common_concept, String own_concept, Boolean payment, Double amount, String document, String reference1, String reference2, String description, Short reliability, Short security_level, Short status, String comments)
	throws SQLException {
		Integer domain = getDomainForBank_statement( rbank);
		Integer id =  super.insertBank_statement( domain != null ? domain : getDefaultDomain(), rbank, lot_number, operation_date, common_concept, own_concept, payment, amount, document, reference1, reference2, description, reliability, security_level, status, comments );
		if ( domain != null ) { 
			bank_statementDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Bank_statement
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @param lot_number Numero de lote
	 * @param operation_date Fecha de operacion
	 * @param common_concept Concepto comun
	 * @param own_concept Concepto propio
	 * @param payment Indica si es un pago
	 * @param amount Importe
	 * @param document Numero de documento
	 * @param reference1 Referencia 1
	 * @param reference2 Referencia 2
	 * @param description Descripcion
	 * @param reliability Fiabilidad del punteo
	 * @param security_level Nivel de seguridad
	 * @param status Estado
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_statement(Integer domain, Integer rbank, Integer lot_number, Date operation_date, Short common_concept, String own_concept, Boolean payment, Double amount, String document, String reference1, String reference2, String description, Short reliability, Short security_level, Short status, String comments)
	throws SQLException {
		Integer id =  super.insertBank_statement(domain, rbank, lot_number, operation_date, common_concept, own_concept, payment, amount, document, reference1, reference2, description, reliability, security_level, status, comments);
		bank_statementDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> relationshipDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRelationshipPk(Integer id){
		return relationshipDomains.get(id);
	}
	
	/**
	 * Relationship
	 * @returns domain's ID
	*/
	protected Integer getDomainForRelationship(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Relationship
	 * @param id Identificador unico del Tipo de Relacion
	 * @param description Descripcion del Tipo de Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRelationship(String description)
	throws SQLException {
		Integer domain = getDomainForRelationship();
		Integer id =  super.insertRelationship( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			relationshipDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Relationship
	 * @param id Identificador unico del Tipo de Relacion
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRelationship(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertRelationship(domain, description);
		relationshipDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> calendarDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCalendarPk(Integer id){
		return calendarDomains.get(id);
	}
	
	/**
	 * Calendar
	 * @param calendar Calendario del que se hereda
	 * @param holiday Identificador de Festivos
	 * @returns domain's ID
	*/
	protected Integer getDomainForCalendar( Integer calendar , Integer holiday){
		Integer domain = null;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
			if ( ( domain = getDomainForHolidayPk( holiday ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Calendar
	 * @param id Identificador unico
	 * @param holiday Identificador de Festivos
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
	 * @param generic Indica si es editable o no
	 * @param calendar Calendario del que se hereda
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCalendar(Integer holiday, Double anual_hours, String description, String comments, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours, Boolean generic, Integer calendar)
	throws SQLException {
		Integer domain = getDomainForCalendar( calendar , holiday);
		Integer id =  super.insertCalendar( domain != null ? domain : getDefaultDomain(), holiday, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours, generic, calendar );
		if ( domain != null ) { 
			calendarDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Calendar
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param holiday Identificador de Festivos
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
	 * @param generic Indica si es editable o no
	 * @param calendar Calendario del que se hereda
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCalendar(Integer domain, Integer holiday, Double anual_hours, String description, String comments, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours, Boolean generic, Integer calendar)
	throws SQLException {
		Integer id =  super.insertCalendar(domain, holiday, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours, generic, calendar);
		calendarDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> training_centerDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTraining_centerPk(Integer registry){
		return training_centerDomains.get(registry);
	}
	
	/**
	 * Training_center
	 * @param registry Registro del Centro Formativo
	 * @returns domain's ID
	*/
	protected Integer getDomainForTraining_center( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Training_center
	 * @param registry Registro del Centro Formativo
	 * @param code Codigo del centro formativo
	 * @throws SQLException
	*/
	public void insertTraining_center(Integer registry, String code)
	throws SQLException {
		Integer domain = getDomainForTraining_center( registry);
		 super.insertTraining_center( registry, domain != null ? domain : getDefaultDomain(), code );
		if ( domain != null ) { 
			training_centerDomains.put(registry, domain);
		}
	}

	/**
	 * Training_center
	 * @param registry Registro del Centro Formativo
	 * @param domain Identificador del Dominio
	 * @param code Codigo del centro formativo
	 * @throws SQLException
	*/
	public void insertTraining_center(Integer registry, Integer domain, String code)
	throws SQLException {
		 super.insertTraining_center(registry, domain, code);
		training_centerDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> web_info_pageDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWeb_info_pagePk(Integer id){
		return web_info_pageDomains.get(id);
	}
	
	/**
	 * Web_info_page
	 * @returns domain's ID
	*/
	protected Integer getDomainForWeb_info_page(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Web_info_page
	 * @param id Codigo de la Pagina
	 * @param name Nombre de la Pagina.
	 * @param type Tipo de Pagina
	 * @param position Posicion de la Pagina en el menu
	 * @param active Indica si la Pagina esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page(String name, Short type, Short position, Boolean active)
	throws SQLException {
		Integer domain = getDomainForWeb_info_page();
		Integer id =  super.insertWeb_info_page( domain != null ? domain : getDefaultDomain(), name, type, position, active );
		if ( domain != null ) { 
			web_info_pageDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Web_info_page
	 * @param id Codigo de la Pagina
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Pagina.
	 * @param type Tipo de Pagina
	 * @param position Posicion de la Pagina en el menu
	 * @param active Indica si la Pagina esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page(Integer domain, String name, Short type, Short position, Boolean active)
	throws SQLException {
		Integer id =  super.insertWeb_info_page(domain, name, type, position, active);
		web_info_pageDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> daily_trackingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDaily_trackingPk(Integer id){
		return daily_trackingDomains.get(id);
	}
	
	/**
	 * Daily_tracking
	 * @param activity_type Identificador de la Actividad asociada al Parte
	 * @param job_type Tipo de Trabajo realizado en el Parte
	 * @param project Identificador del Expediente asociado al Parte
	 * @param registry Identificador del Cliente asociado al Parte
	 * @param task Identificador de la Tarea que provoca el Parte
	 * @param task_holder Identificador del Usuario que realiza el Parte
	 * @returns domain's ID
	*/
	protected Integer getDomainForDaily_tracking( Integer activity_type , Integer job_type , Integer project , Integer registry , Integer task , Integer task_holder){
		Integer domain = null;
			if ( ( domain = getDomainForActivity_typePk( activity_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForJob_typePk( job_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaskPk( task ) ) != null )
				return domain;
			if ( ( domain = getDomainForTask_holderPk( task_holder ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Daily_tracking
	 * @param id Identificador unico del Parte
	 * @param task_holder Identificador del Usuario que realiza el Parte
	 * @param tracking_date Fecha del Parte
	 * @param tracking_duration Tiempo invertido en el Parte
	 * @param job_type Tipo de Trabajo realizado en el Parte
	 * @param registry Identificador del Cliente asociado al Parte
	 * @param project Identificador del Expediente asociado al Parte
	 * @param activity_type Identificador de la Actividad asociada al Parte
	 * @param comments Comentarios del Parte
	 * @param task Identificador de la Tarea que provoca el Parte
	 * @param cost Costo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDaily_tracking(Integer task_holder, Date tracking_date, Double tracking_duration, Integer job_type, Integer registry, Integer project, Integer activity_type, String comments, Integer task, Double cost)
	throws SQLException {
		Integer domain = getDomainForDaily_tracking( activity_type , job_type , project , registry , task , task_holder);
		Integer id =  super.insertDaily_tracking( domain != null ? domain : getDefaultDomain(), task_holder, tracking_date, tracking_duration, job_type, registry, project, activity_type, comments, task, cost );
		if ( domain != null ) { 
			daily_trackingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Daily_tracking
	 * @param id Identificador unico del Parte
	 * @param domain Identificador del Dominio
	 * @param task_holder Identificador del Usuario que realiza el Parte
	 * @param tracking_date Fecha del Parte
	 * @param tracking_duration Tiempo invertido en el Parte
	 * @param job_type Tipo de Trabajo realizado en el Parte
	 * @param registry Identificador del Cliente asociado al Parte
	 * @param project Identificador del Expediente asociado al Parte
	 * @param activity_type Identificador de la Actividad asociada al Parte
	 * @param comments Comentarios del Parte
	 * @param task Identificador de la Tarea que provoca el Parte
	 * @param cost Costo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDaily_tracking(Integer domain, Integer task_holder, Date tracking_date, Double tracking_duration, Integer job_type, Integer registry, Integer project, Integer activity_type, String comments, Integer task, Double cost)
	throws SQLException {
		Integer id =  super.insertDaily_tracking(domain, task_holder, tracking_date, tracking_duration, job_type, registry, project, activity_type, comments, task, cost);
		daily_trackingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> stop_sales_tariffDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForStop_sales_tariffPk(Integer id){
		return stop_sales_tariffDomains.get(id);
	}
	
	/**
	 * Stop_sales_tariff
	 * @param stop_sales Identificador del Paro de ventas
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForStop_sales_tariff( Integer stop_sales , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForStop_salesPk( stop_sales ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Stop_sales_tariff
	 * @param id Identificador unico
	 * @param stop_sales Identificador del Paro de ventas
	 * @param tariff Identificador de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales_tariff(Integer stop_sales, Integer tariff)
	throws SQLException {
		Integer domain = getDomainForStop_sales_tariff( stop_sales , tariff);
		Integer id =  super.insertStop_sales_tariff( domain != null ? domain : getDefaultDomain(), stop_sales, tariff );
		if ( domain != null ) { 
			stop_sales_tariffDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Stop_sales_tariff
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param stop_sales Identificador del Paro de ventas
	 * @param tariff Identificador de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales_tariff(Integer domain, Integer stop_sales, Integer tariff)
	throws SQLException {
		Integer id =  super.insertStop_sales_tariff(domain, stop_sales, tariff);
		stop_sales_tariffDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> allotment_tariffDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAllotment_tariffPk(Integer id){
		return allotment_tariffDomains.get(id);
	}
	
	/**
	 * Allotment_tariff
	 * @param allotment Identificador del Cupo de seguridad
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForAllotment_tariff( Integer allotment , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForAllotmentPk( allotment ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Allotment_tariff
	 * @param id Identificador unico
	 * @param allotment Identificador del Cupo de seguridad
	 * @param tariff Identificador de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment_tariff(Integer allotment, Integer tariff)
	throws SQLException {
		Integer domain = getDomainForAllotment_tariff( allotment , tariff);
		Integer id =  super.insertAllotment_tariff( domain != null ? domain : getDefaultDomain(), allotment, tariff );
		if ( domain != null ) { 
			allotment_tariffDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Allotment_tariff
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param allotment Identificador del Cupo de seguridad
	 * @param tariff Identificador de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment_tariff(Integer domain, Integer allotment, Integer tariff)
	throws SQLException {
		Integer id =  super.insertAllotment_tariff(domain, allotment, tariff);
		allotment_tariffDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> task_tagDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTask_tagPk(Integer id){
		return task_tagDomains.get(id);
	}
	
	/**
	 * Task_tag
	 * @param tag Identificador de la Etiqueta
	 * @param task Identificador de la tarea
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask_tag( Integer tag , Integer task){
		Integer domain = null;
			if ( ( domain = getDomainForTagPk( tag ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaskPk( task ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task_tag
	 * @param id Identificador unico
	 * @param task Identificador de la tarea
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_tag(Integer task, Integer tag)
	throws SQLException {
		Integer domain = getDomainForTask_tag( tag , task);
		Integer id =  super.insertTask_tag( domain != null ? domain : getDefaultDomain(), task, tag );
		if ( domain != null ) { 
			task_tagDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Task_tag
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param task Identificador de la tarea
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask_tag(Integer domain, Integer task, Integer tag)
	throws SQLException {
		Integer id =  super.insertTask_tag(domain, task, tag);
		task_tagDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> data_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForData_attachPk(Integer id){
		return data_attachDomains.get(id);
	}
	
	/**
	 * Data_attach
	 * @returns domain's ID
	*/
	protected Integer getDomainForData_attach(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Data_attach
	 * @param id Identificador unico
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param description Descripcion
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param drive_id Identificador de Google Drive
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_attach(Short source, Integer source_id, String description, Blob data, Short type, Short mimeType, String drive_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForData_attach();
		Integer id =  super.insertData_attach( domain != null ? domain : getDefaultDomain(), source, source_id, description, data, type, mimeType, drive_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			data_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Data_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param description Descripcion
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param drive_id Identificador de Google Drive
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_attach(Integer domain, Short source, Integer source_id, String description, Blob data, Short type, Short mimeType, String drive_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertData_attach(domain, source, source_id, description, data, type, mimeType, drive_id, creation_user, creation_date, modification_user, modification_date);
		data_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pos_shiftDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPos_shiftPk(Integer id){
		return pos_shiftDomains.get(id);
	}
	
	/**
	 * Pos_shift
	 * @param pos Identificador del TPV
	 * @returns domain's ID
	*/
	protected Integer getDomainForPos_shift( Integer pos){
		Integer domain = null;
			if ( ( domain = getDomainForPosPk( pos ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Pos_shift
	 * @param id Identificador unico
	 * @param pos Identificador del TPV
	 * @param shift Turno de trabajo
	 * @param username Usuario del Turno
	 * @param start_time Fecha-hora de apertura
	 * @param end_time Fecha-hora de cierre
	 * @param initial_amount Efectivo inicial
	 * @param imbalance Indica si existen descuadres en el Turno
	 * @param remarks Observaciones del turno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_shift(Integer pos, Short shift, String username, Timestamp start_time, Timestamp end_time, Double initial_amount, Boolean imbalance, String remarks, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForPos_shift( pos);
		Integer id =  super.insertPos_shift( domain != null ? domain : getDefaultDomain(), pos, shift, username, start_time, end_time, initial_amount, imbalance, remarks, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			pos_shiftDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pos_shift
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pos Identificador del TPV
	 * @param shift Turno de trabajo
	 * @param username Usuario del Turno
	 * @param start_time Fecha-hora de apertura
	 * @param end_time Fecha-hora de cierre
	 * @param initial_amount Efectivo inicial
	 * @param imbalance Indica si existen descuadres en el Turno
	 * @param remarks Observaciones del turno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_shift(Integer domain, Integer pos, Short shift, String username, Timestamp start_time, Timestamp end_time, Double initial_amount, Boolean imbalance, String remarks, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertPos_shift(domain, pos, shift, username, start_time, end_time, initial_amount, imbalance, remarks, creation_user, creation_date, modification_user, modification_date);
		pos_shiftDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> seriesDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSeriesPk(Integer id){
		return seriesDomains.get(id);
	}
	
	/**
	 * Series
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForSeries( Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Series
	 * @param id Identificador unico
	 * @param code Serie
	 * @param scope Identificador del Ambito
	 * @param description Descripcion de la Serie
	 * @param tas Indica si es una Serie para Ordenes de Reparacion
	 * @param offer Indica si es una Serie para Presupuestos
	 * @param sales Indica si es una Serie para Pedidos
	 * @param delivery Indica si es una Serie para Albaranes
	 * @param invoice Indica si es una Serie para Facturas
	 * @param rectification Indica si es una Serie para Facturas rectificativas
	 * @param pos Indica si es una Serie para TPV
	 * @param security_level Nivel de seguridad de la Serie
	 * @param active Indica si la Serie esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSeries(String code, Integer scope, String description, Boolean tas, Boolean offer, Boolean sales, Boolean delivery, Boolean invoice, Boolean rectification, Boolean pos, Short security_level, Boolean active)
	throws SQLException {
		Integer domain = getDomainForSeries( scope);
		Integer id =  super.insertSeries( code, domain != null ? domain : getDefaultDomain(), scope, description, tas, offer, sales, delivery, invoice, rectification, pos, security_level, active );
		if ( domain != null ) { 
			seriesDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Series
	 * @param id Identificador unico
	 * @param code Serie
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param description Descripcion de la Serie
	 * @param tas Indica si es una Serie para Ordenes de Reparacion
	 * @param offer Indica si es una Serie para Presupuestos
	 * @param sales Indica si es una Serie para Pedidos
	 * @param delivery Indica si es una Serie para Albaranes
	 * @param invoice Indica si es una Serie para Facturas
	 * @param rectification Indica si es una Serie para Facturas rectificativas
	 * @param pos Indica si es una Serie para TPV
	 * @param security_level Nivel de seguridad de la Serie
	 * @param active Indica si la Serie esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSeries(String code, Integer domain, Integer scope, String description, Boolean tas, Boolean offer, Boolean sales, Boolean delivery, Boolean invoice, Boolean rectification, Boolean pos, Short security_level, Boolean active)
	throws SQLException {
		Integer id =  super.insertSeries(code, domain, scope, description, tas, offer, sales, delivery, invoice, rectification, pos, security_level, active);
		seriesDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> web_info_page_resourceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWeb_info_page_resourcePk(Integer id){
		return web_info_page_resourceDomains.get(id);
	}
	
	/**
	 * Web_info_page_resource
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param web_info_page Codigo de la Pagina
	 * @returns domain's ID
	*/
	protected Integer getDomainForWeb_info_page_resource( Integer rattach , Integer web_info_page){
		Integer domain = null;
			if ( ( domain = getDomainForRattachPk( rattach ) ) != null )
				return domain;
			if ( ( domain = getDomainForWeb_info_pagePk( web_info_page ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Web_info_page_resource
	 * @param id Codigo del Recurso de la Pagina
	 * @param web_info_page Codigo de la Pagina
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param content Texto del Recurso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page_resource(Integer web_info_page, Integer rattach, String content)
	throws SQLException {
		Integer domain = getDomainForWeb_info_page_resource( rattach , web_info_page);
		Integer id =  super.insertWeb_info_page_resource( domain != null ? domain : getDefaultDomain(), web_info_page, rattach, content );
		if ( domain != null ) { 
			web_info_page_resourceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Web_info_page_resource
	 * @param id Codigo del Recurso de la Pagina
	 * @param domain Identificador del Dominio
	 * @param web_info_page Codigo de la Pagina
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param content Texto del Recurso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page_resource(Integer domain, Integer web_info_page, Integer rattach, String content)
	throws SQLException {
		Integer id =  super.insertWeb_info_page_resource(domain, web_info_page, rattach, content);
		web_info_page_resourceDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> reservation_request_guestDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForReservation_request_guestPk(Integer id){
		return reservation_request_guestDomains.get(id);
	}
	
	/**
	 * Reservation_request_guest
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForReservation_request_guest( Integer reservation_request){
		Integer domain = null;
			if ( ( domain = getDomainForReservation_requestPk( reservation_request ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Reservation_request_guest
	 * @param id Identificador unico
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @param guest_index Numero de Huesped
	 * @param name Nombre
	 * @param surname Apellidos
	 * @param email Email
	 * @param phone Telefono
	 * @param address Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo postal
	 * @param city Ciudad
	 * @param province Provincia
	 * @param country Pais
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request_guest(Integer reservation_request, Short guest_index, String name, String surname, String email, String phone, String address, String number, String address2, String zip, String city, String province, String country, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForReservation_request_guest( reservation_request);
		Integer id =  super.insertReservation_request_guest( domain != null ? domain : getDefaultDomain(), reservation_request, guest_index, name, surname, email, phone, address, number, address2, zip, city, province, country, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			reservation_request_guestDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Reservation_request_guest
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param reservation_request Identificador de la Solicitud de Reserva
	 * @param guest_index Numero de Huesped
	 * @param name Nombre
	 * @param surname Apellidos
	 * @param email Email
	 * @param phone Telefono
	 * @param address Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo postal
	 * @param city Ciudad
	 * @param province Provincia
	 * @param country Pais
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertReservation_request_guest(Integer domain, Integer reservation_request, Short guest_index, String name, String surname, String email, String phone, String address, String number, String address2, String zip, String city, String province, String country, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertReservation_request_guest(domain, reservation_request, guest_index, name, surname, email, phone, address, number, address2, zip, city, province, country, creation_user, creation_date, modification_user, modification_date);
		reservation_request_guestDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> sellerDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSellerPk(Integer registry){
		return sellerDomains.get(registry);
	}
	
	/**
	 * Seller
	 * @param commission_type Identificador del Tipo de Comision
	 * @param registry Registro del Agente Comercial
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForSeller( Integer commission_type , Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForCommission_typePk( commission_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Seller
	 * @param registry Registro del Agente Comercial
	 * @param commission_type Identificador del Tipo de Comision
	 * @param status Estado del Agente Comercial
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	public void insertSeller(Integer registry, Integer commission_type, Short status, Integer scope)
	throws SQLException {
		Integer domain = getDomainForSeller( commission_type , registry , scope);
		 super.insertSeller( registry, domain != null ? domain : getDefaultDomain(), commission_type, status, scope );
		if ( domain != null ) { 
			sellerDomains.put(registry, domain);
		}
	}

	/**
	 * Seller
	 * @param registry Registro del Agente Comercial
	 * @param domain Identificador del Dominio
	 * @param commission_type Identificador del Tipo de Comision
	 * @param status Estado del Agente Comercial
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	public void insertSeller(Integer registry, Integer domain, Integer commission_type, Short status, Integer scope)
	throws SQLException {
		 super.insertSeller(registry, domain, commission_type, status, scope);
		sellerDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> agreement_level_categoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_level_categoryPk(Integer id){
		return agreement_level_categoryDomains.get(id);
	}
	
	/**
	 * Agreement_level_category
	 * @param agreement_level Nivel retributivo
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_level_category( Integer agreement_level){
		Integer domain = null;
			if ( ( domain = getDomainForAgreement_levelPk( agreement_level ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_level_category
	 * @param id Identificador unico
	 * @param agreement_level Nivel retributivo
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level_category(Integer agreement_level, String description)
	throws SQLException {
		Integer domain = getDomainForAgreement_level_category( agreement_level);
		Integer id =  super.insertAgreement_level_category( domain != null ? domain : getDefaultDomain(), agreement_level, description );
		if ( domain != null ) { 
			agreement_level_categoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_level_category
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param agreement_level Nivel retributivo
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level_category(Integer domain, Integer agreement_level, String description)
	throws SQLException {
		Integer id =  super.insertAgreement_level_category(domain, agreement_level, description);
		agreement_level_categoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> payroll_workplaceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPayroll_workplacePk(Integer id){
		return payroll_workplaceDomains.get(id);
	}
	
	/**
	 * Payroll_workplace
	 * @param agreement Identificador del Convenio
	 * @param calendar Identificador del Calendario
	 * @param enterprise_activity Identificador de la Actividad
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForPayroll_workplace( Integer agreement , Integer calendar , Integer enterprise_activity , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForAgreementPk( agreement ) ) != null )
				return domain;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprise_activityPk( enterprise_activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Payroll_workplace
	 * @param id Identificador unico
	 * @param workplace Identificador del Centro de Trabajo
	 * @param agreement Identificador del Convenio
	 * @param enterprise_activity Identificador de la Actividad
	 * @param calendar Identificador del Calendario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayroll_workplace(Integer workplace, Integer agreement, Integer enterprise_activity, Integer calendar)
	throws SQLException {
		Integer domain = getDomainForPayroll_workplace( agreement , calendar , enterprise_activity , workplace);
		Integer id =  super.insertPayroll_workplace( domain != null ? domain : getDefaultDomain(), workplace, agreement, enterprise_activity, calendar );
		if ( domain != null ) { 
			payroll_workplaceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Payroll_workplace
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param workplace Identificador del Centro de Trabajo
	 * @param agreement Identificador del Convenio
	 * @param enterprise_activity Identificador de la Actividad
	 * @param calendar Identificador del Calendario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayroll_workplace(Integer domain, Integer workplace, Integer agreement, Integer enterprise_activity, Integer calendar)
	throws SQLException {
		Integer id =  super.insertPayroll_workplace(domain, workplace, agreement, enterprise_activity, calendar);
		payroll_workplaceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> campaign_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCampaign_typePk(Integer id){
		return campaign_typeDomains.get(id);
	}
	
	/**
	 * Campaign_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForCampaign_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Campaign_type
	 * @param id Identificador unico
	 * @param description Descripcion
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign_type(String description, Boolean active)
	throws SQLException {
		Integer domain = getDomainForCampaign_type();
		Integer id =  super.insertCampaign_type( domain != null ? domain : getDefaultDomain(), description, active );
		if ( domain != null ) { 
			campaign_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Campaign_type
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign_type(Integer domain, String description, Boolean active)
	throws SQLException {
		Integer id =  super.insertCampaign_type(domain, description, active);
		campaign_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> income_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIncome_detailPk(Integer id){
		return income_detailDomains.get(id);
	}
	
	/**
	 * Income_detail
	 * @param income Identificador del Albaran de Compra
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param project Identificador del Proyecto
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra asociado
	 * @param warehouse Identificador del Almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForIncome_detail( Integer income , Integer item , Integer project , Integer purchase_detail , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForIncomePk( income ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForPurchase_detailPk( purchase_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Income_detail
	 * @param id Identificador unico del Detalle del Albaran de Compra
	 * @param income Identificador del Albaran de Compra
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIncome_detail(Integer income, Integer project, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer purchase_detail, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForIncome_detail( income , item , project , purchase_detail , warehouse);
		Integer id =  super.insertIncome_detail( domain != null ? domain : getDefaultDomain(), income, project, line, item, description, warehouse, quantity, price, discount_expr, purchase_detail, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			income_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Income_detail
	 * @param id Identificador unico del Detalle del Albaran de Compra
	 * @param domain Identificador del Dominio
	 * @param income Identificador del Albaran de Compra
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIncome_detail(Integer domain, Integer income, Integer project, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer purchase_detail, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertIncome_detail(domain, income, project, line, item, description, warehouse, quantity, price, discount_expr, purchase_detail, creation_user, creation_date, modification_user, modification_date);
		income_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> projectDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProjectPk(Integer id){
		return projectDomains.get(id);
	}
	
	/**
	 * Project
	 * @param project_type Tipo de Proyecto
	 * @param registry Identificador del Cliente (Potencial) asociado
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject( Integer project_type , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForProject_typePk( project_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project
	 * @param id Identificador unico
	 * @param name Nombre del Proyecto
	 * @param alias Alias del Proyecto
	 * @param registry Identificador del Cliente (Potencial) asociado
	 * @param date Fecha del Proyecto
	 * @param project_type Tipo de Proyecto
	 * @param tas Indica si se trata de una Orden de Reparacion o Fabricacion
	 * @param commercial Indica si se trata de una Operacion Comercial
	 * @param reservation Indica si se trata de una Reserva
	 * @param active Indica si el Proyecto esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject(String name, String alias, Integer registry, Date date, Integer project_type, Boolean tas, Boolean commercial, Boolean reservation, Boolean active)
	throws SQLException {
		Integer domain = getDomainForProject( project_type , registry);
		Integer id =  super.insertProject( domain != null ? domain : getDefaultDomain(), name, alias, registry, date, project_type, tas, commercial, reservation, active );
		if ( domain != null ) { 
			projectDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Proyecto
	 * @param alias Alias del Proyecto
	 * @param registry Identificador del Cliente (Potencial) asociado
	 * @param date Fecha del Proyecto
	 * @param project_type Tipo de Proyecto
	 * @param tas Indica si se trata de una Orden de Reparacion o Fabricacion
	 * @param commercial Indica si se trata de una Operacion Comercial
	 * @param reservation Indica si se trata de una Reserva
	 * @param active Indica si el Proyecto esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject(Integer domain, String name, String alias, Integer registry, Date date, Integer project_type, Boolean tas, Boolean commercial, Boolean reservation, Boolean active)
	throws SQLException {
		Integer id =  super.insertProject(domain, name, alias, registry, date, project_type, tas, commercial, reservation, active);
		projectDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> payment_conceptDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPayment_conceptPk(Integer id){
		return payment_conceptDomains.get(id);
	}
	
	/**
	 * Payment_concept
	 * @returns domain's ID
	*/
	protected Integer getDomainForPayment_concept(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Payment_concept
	 * @param id Identificador unico
	 * @param code Codigo
	 * @param description Descripcion
	 * @param type Tipo de Percepcion Salarial
	 * @param description_decorable 
	 * @param expression Importe
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayment_concept(String code, String description, Short type, Short description_decorable, String expression, String irpf_expression, String quote_expression)
	throws SQLException {
		Integer domain = getDomainForPayment_concept();
		Integer id =  super.insertPayment_concept( domain != null ? domain : getDefaultDomain(), code, description, type, description_decorable, expression, irpf_expression, quote_expression );
		if ( domain != null ) { 
			payment_conceptDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Payment_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo
	 * @param description Descripcion
	 * @param type Tipo de Percepcion Salarial
	 * @param description_decorable 
	 * @param expression Importe
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayment_concept(Integer domain, String code, String description, Short type, Short description_decorable, String expression, String irpf_expression, String quote_expression)
	throws SQLException {
		Integer id =  super.insertPayment_concept(domain, code, description, type, description_decorable, expression, irpf_expression, quote_expression);
		payment_conceptDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_activityDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_activityPk(Integer id){
		return fs_activityDomains.get(id);
	}
	
	/**
	 * Fs_activity
	 * @param activity Identificador de la Actividad
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_activity( Integer activity){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprise_activityPk( activity ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_activity
	 * @param id Identificador unico
	 * @param year Ejercicio del Lote
	 * @param activity Identificador de la Actividad
	 * @param epigraph Epigrafe IAE
	 * @param description Descripcion del epigrafe
	 * @param farmer Actividad agricola
	 * @param max_person Valor maximo de personas
	 * @param max_import Valor maximo de importe
	 * @param vat_percent IVA - porcentaje aplicable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_activity(Integer year, Integer activity, String epigraph, String description, Boolean farmer, Double max_person, Double max_import, Double vat_percent)
	throws SQLException {
		Integer domain = getDomainForFs_activity( activity);
		Integer id =  super.insertFs_activity( domain != null ? domain : getDefaultDomain(), year, activity, epigraph, description, farmer, max_person, max_import, vat_percent );
		if ( domain != null ) { 
			fs_activityDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param year Ejercicio del Lote
	 * @param activity Identificador de la Actividad
	 * @param epigraph Epigrafe IAE
	 * @param description Descripcion del epigrafe
	 * @param farmer Actividad agricola
	 * @param max_person Valor maximo de personas
	 * @param max_import Valor maximo de importe
	 * @param vat_percent IVA - porcentaje aplicable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_activity(Integer domain, Integer year, Integer activity, String epigraph, String description, Boolean farmer, Double max_person, Double max_import, Double vat_percent)
	throws SQLException {
		Integer id =  super.insertFs_activity(domain, year, activity, epigraph, description, farmer, max_person, max_import, vat_percent);
		fs_activityDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> irpf_data_descendientsDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIrpf_data_descendientsPk(Integer id){
		return irpf_data_descendientsDomains.get(id);
	}
	
	/**
	 * Irpf_data_descendients
	 * @param irpf_data Identificador del irpf
	 * @returns domain's ID
	*/
	protected Integer getDomainForIrpf_data_descendients( Integer irpf_data){
		Integer domain = null;
			if ( ( domain = getDomainForIrpf_dataPk( irpf_data ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Irpf_data_descendients
	 * @param id Identificador unico
	 * @param irpf_data Identificador del irpf
	 * @param birth_year Anio de nacimiento
	 * @param adoption_year Anio de adopcion
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param unique_parent Computo por entero de hijos o descendientes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data_descendients(Integer irpf_data, Integer birth_year, Integer adoption_year, Short disability_level, Boolean dependence, Boolean unique_parent)
	throws SQLException {
		Integer domain = getDomainForIrpf_data_descendients( irpf_data);
		Integer id =  super.insertIrpf_data_descendients( domain != null ? domain : getDefaultDomain(), irpf_data, birth_year, adoption_year, disability_level, dependence, unique_parent );
		if ( domain != null ) { 
			irpf_data_descendientsDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Irpf_data_descendients
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param irpf_data Identificador del irpf
	 * @param birth_year Anio de nacimiento
	 * @param adoption_year Anio de adopcion
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param unique_parent Computo por entero de hijos o descendientes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data_descendients(Integer domain, Integer irpf_data, Integer birth_year, Integer adoption_year, Short disability_level, Boolean dependence, Boolean unique_parent)
	throws SQLException {
		Integer id =  super.insertIrpf_data_descendients(domain, irpf_data, birth_year, adoption_year, disability_level, dependence, unique_parent);
		irpf_data_descendientsDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> system_deductionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSystem_deductionPk(Integer id){
		return system_deductionDomains.get(id);
	}
	
	/**
	 * System_deduction
	 * @param deduction_concept Identificador unico del concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForSystem_deduction( Integer deduction_concept){
		Integer domain = null;
			if ( ( domain = getDomainForDeduction_conceptPk( deduction_concept ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * System_deduction
	 * @param id Identificador unico
	 * @param type Tipo de DeducciÛn
	 * @param deduction_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la deducciÛn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_deduction(Short type, Integer deduction_concept, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		Integer domain = getDomainForSystem_deduction( deduction_concept);
		Integer id =  super.insertSystem_deduction( domain != null ? domain : getDefaultDomain(), type, deduction_concept, description, description_decorable, expression, start_date, end_date, month );
		if ( domain != null ) { 
			system_deductionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * System_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de DeducciÛn
	 * @param deduction_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la deducciÛn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_deduction(Integer domain, Short type, Integer deduction_concept, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		Integer id =  super.insertSystem_deduction(domain, type, deduction_concept, description, description_decorable, expression, start_date, end_date, month);
		system_deductionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model193_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model193_detailPk(Integer id){
		return fs_model193_detailDomains.get(id);
	}
	
	/**
	 * Fs_model193_detail
	 * @param fs_model193 Identificador del modelo 193
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model193_detail( Integer fs_model193){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model193Pk( fs_model193 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model193_detail
	 * @param id Identificador unico
	 * @param fs_model193 Identificador del modelo 193
	 * @param type Tipo de Registro P (perceptor) o G (Gastos), 
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param intermediary_payment Pago a un Mediador
	 * @param province Provincia
	 * @param key_code Clave Codigo
	 * @param issuing_code Codigo emisor
	 * @param key Clave de percepcion
	 * @param nature Naturaleza de la percepcion
	 * @param payment Pago
	 * @param code_type Tipo Codigo
	 * @param lender_amount 
	 * @param account_code Codigo de cuenta,Numero id. prestamo
	 * @param pending Pendiente
	 * @param accrual_year 
	 * @param in_kind Percepcion en especie
	 * @param perception 
	 * @param reduction 
	 * @param retention_base 
	 * @param percent 
	 * @param retention 
	 * @param deponent_nature Naturaleza del declarante
	 * @param loan_start_date Fecha de inicio del prestamo
	 * @param loan_due_date Fecha de vencimiento del prestamo
	 * @param compensation 
	 * @param guarantee 
	 * @param expenses 
	 * @param penalization Penalizaciones
	 * @param declarant_nature Naturaleza del declarante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model193_detail(Integer fs_model193, String type, String document, String name, String representative_document, Boolean intermediary_payment, Integer province, Boolean key_code, String issuing_code, String key, String nature, Boolean payment, String code_type, Double lender_amount, String account_code, Boolean pending, Integer accrual_year, Boolean in_kind, Double perception, Double reduction, Double retention_base, Double percent, Double retention, Boolean deponent_nature, Date loan_start_date, Date loan_due_date, Double compensation, Double guarantee, Double expenses, Double penalization, Boolean declarant_nature)
	throws SQLException {
		Integer domain = getDomainForFs_model193_detail( fs_model193);
		Integer id =  super.insertFs_model193_detail( domain != null ? domain : getDefaultDomain(), fs_model193, type, document, name, representative_document, intermediary_payment, province, key_code, issuing_code, key, nature, payment, code_type, lender_amount, account_code, pending, accrual_year, in_kind, perception, reduction, retention_base, percent, retention, deponent_nature, loan_start_date, loan_due_date, compensation, guarantee, expenses, penalization, declarant_nature );
		if ( domain != null ) { 
			fs_model193_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model193_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model193 Identificador del modelo 193
	 * @param type Tipo de Registro P (perceptor) o G (Gastos), 
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param intermediary_payment Pago a un Mediador
	 * @param province Provincia
	 * @param key_code Clave Codigo
	 * @param issuing_code Codigo emisor
	 * @param key Clave de percepcion
	 * @param nature Naturaleza de la percepcion
	 * @param payment Pago
	 * @param code_type Tipo Codigo
	 * @param lender_amount 
	 * @param account_code Codigo de cuenta,Numero id. prestamo
	 * @param pending Pendiente
	 * @param accrual_year 
	 * @param in_kind Percepcion en especie
	 * @param perception 
	 * @param reduction 
	 * @param retention_base 
	 * @param percent 
	 * @param retention 
	 * @param deponent_nature Naturaleza del declarante
	 * @param loan_start_date Fecha de inicio del prestamo
	 * @param loan_due_date Fecha de vencimiento del prestamo
	 * @param compensation 
	 * @param guarantee 
	 * @param expenses 
	 * @param penalization Penalizaciones
	 * @param declarant_nature Naturaleza del declarante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model193_detail(Integer domain, Integer fs_model193, String type, String document, String name, String representative_document, Boolean intermediary_payment, Integer province, Boolean key_code, String issuing_code, String key, String nature, Boolean payment, String code_type, Double lender_amount, String account_code, Boolean pending, Integer accrual_year, Boolean in_kind, Double perception, Double reduction, Double retention_base, Double percent, Double retention, Boolean deponent_nature, Date loan_start_date, Date loan_due_date, Double compensation, Double guarantee, Double expenses, Double penalization, Boolean declarant_nature)
	throws SQLException {
		Integer id =  super.insertFs_model193_detail(domain, fs_model193, type, document, name, representative_document, intermediary_payment, province, key_code, issuing_code, key, nature, payment, code_type, lender_amount, account_code, pending, accrual_year, in_kind, perception, reduction, retention_base, percent, retention, deponent_nature, loan_start_date, loan_due_date, compensation, guarantee, expenses, penalization, declarant_nature);
		fs_model193_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contactDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContactPk(Integer id){
		return contactDomains.get(id);
	}
	
	/**
	 * Contact
	 * @param contact_data Identificador de la Informaciùn del Contacto
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForContact( Integer contact_data , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForContact_dataPk( contact_data ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contact
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param displayName Mostrar Como
	 * @param contact_data Identificador de la Informaciùn del Contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact(Integer user_id, String displayName, Integer contact_data)
	throws SQLException {
		Integer domain = getDomainForContact( contact_data , user_id);
		Integer id =  super.insertContact( domain != null ? domain : getDefaultDomain(), user_id, displayName, contact_data );
		if ( domain != null ) { 
			contactDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contact
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param user_id Identificador del Usuario
	 * @param displayName Mostrar Como
	 * @param contact_data Identificador de la Informaciùn del Contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact(Integer domain, Integer user_id, String displayName, Integer contact_data)
	throws SQLException {
		Integer id =  super.insertContact(domain, user_id, displayName, contact_data);
		contactDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> segmentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSegmentPk(Integer id){
		return segmentDomains.get(id);
	}
	
	/**
	 * Segment
	 * @returns domain's ID
	*/
	protected Integer getDomainForSegment(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Segment
	 * @param id Identificador unico
	 * @param name Nombre del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSegment(String name)
	throws SQLException {
		Integer domain = getDomainForSegment();
		Integer id =  super.insertSegment( domain != null ? domain : getDefaultDomain(), name );
		if ( domain != null ) { 
			segmentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Segment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSegment(Integer domain, String name)
	throws SQLException {
		Integer id =  super.insertSegment(domain, name);
		segmentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> action_deniedDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAction_deniedPk(Integer id){
		return action_deniedDomains.get(id);
	}
	
	/**
	 * Action_denied
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForAction_denied( Integer action_id , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Action_denied
	 * @param id Identificador unico
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_denied(Integer action_id, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForAction_denied( action_id , user_id);
		Integer id =  super.insertAction_denied( domain != null ? domain : getDefaultDomain(), action_id, user_id );
		if ( domain != null ) { 
			action_deniedDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Action_denied
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_denied(Integer domain, Integer action_id, Integer user_id)
	throws SQLException {
		Integer id =  super.insertAction_denied(domain, action_id, user_id);
		action_deniedDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_detail_commissionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_detail_commissionPk(Integer id){
		return invoice_detail_commissionDomains.get(id);
	}
	
	/**
	 * Invoice_detail_commission
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_detail_commission( Integer invoice_detail){
		Integer domain = null;
			if ( ( domain = getDomainForInvoice_detailPk( invoice_detail ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_detail_commission
	 * @param id Identificador unico
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail_commission(Integer invoice_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
		Integer domain = getDomainForInvoice_detail_commission( invoice_detail);
		Integer id =  super.insertInvoice_detail_commission( domain != null ? domain : getDefaultDomain(), invoice_detail, commission, amount, status, pay_date );
		if ( domain != null ) { 
			invoice_detail_commissionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_detail_commission
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail_commission(Integer domain, Integer invoice_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
		Integer id =  super.insertInvoice_detail_commission(domain, invoice_detail, commission, amount, status, pay_date);
		invoice_detail_commissionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> irpf_resultDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIrpf_resultPk(Integer id){
		return irpf_resultDomains.get(id);
	}
	
	/**
	 * Irpf_result
	 * @param contract Identificador del contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForIrpf_result( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Irpf_result
	 * @param id Identificador unico
	 * @param contract Identificador del contrato
	 * @param effective_date Fecha de entrada en vigor
	 * @param base_irpf Base para calcular el tipo de retenciÛn
	 * @param minimun_personal_family MÌnimo personal y familiar para calcular el tipo de retenciÛn
	 * @param deduct_home_loan_amount MinoraciÛn por pagos de prÈstamo para vivienda habitual
	 * @param deduct_80_bis Deduccion ArttÌculo 80 bis LIRPF
	 * @param irpf Tipo retenciÛn apliclabe 
	 * @param annual_irpf Importe anual de las retenciones e ingresos a cuenta
	 * @param annual_remuneration Retribuciones anuales. Importe Ìntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Art. 18.3: DD.TT 11™ y 12 ™ de la LIRPF). Importe
	 * @param deduccibles_expenses Gastos deducibles. Importe anual
	 * @param work_remuneration_reduction Reducciones por rendimiento del trabajo 
	 * @param work_prolongation_reduction Reducciones por prolongaciÛn de la actividad 
	 * @param work_moving_reduction Reducciones por movilidad geografica 
	 * @param work_disability_reduction Reducciones por discapacidad 
	 * @param social_security_pensioner Por ser pensionista de la s. social/cl. Pasivas o desempleado
	 * @param two_or_more_descendents_min Por tener m·s de dos descendientes con derecho a mÌnimo
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge. Importe anual
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe anual
	 * @param minimun_personal MÌnimo personal
	 * @param minimun_ascendents MÌnimo por descendientes
	 * @param minimun_descendents MÌnimo por descendientes
	 * @param minimun_disability MÌnimo por discapacidad
	 * @param descendents_minor_3_total Descendientes computados menores de tres aÒos. Total
	 * @param descendents_minor_3_entirely Descendientes computados menores de tres aÒos. Por entero
	 * @param descendents_remainder_total Resto de descendientes computados . Total
	 * @param descendents_remainder_entirely Resto de descendientes computados . Por entero
	 * @param descendents_33_65_total Descendientes con discapacidad >= 33% y < 65%. Total
	 * @param descendents_33_65_entirely Descendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param descendents_moving_total Descendientes con discapacidad, movilidad reducida. Total
	 * @param descendents_moving_entirely Descendientes con discapacidad, movilidad reducida. Por entero
	 * @param descendents_65_total Descendientes con discapacidad > 65%. Total
	 * @param descendents_65_entirely Descendientes con discapacidad > 65%. Por entero
	 * @param descendents_first Detalle del cÛmputo de descendientes. Hijo 1∫
	 * @param descendents_second Detalle del cÛmputo de descendientes. Hijo 2∫
	 * @param descendents_third Detalle del cÛmputo de descendientes. Hijo 3∫
	 * @param descendents_fourth_subsequent_total Detalle del cÛmputo de descendientes. Hijo 4∫ y sucesivos. Total
	 * @param descendents_fourth_subsequent_entirely Detalle del cÛmputo de descendientes. Hijo 4∫ y sucesivos. Por entero
	 * @param ascendents_minor_75_total Ascendientes computados menores de 75 aÒos. Total
	 * @param ascendents_minor_75_entirely Ascendientes computados menores de 75 aÒos. Por entero
	 * @param ascendents_mayor_75_total Ascendientes computados mayores de 75 aÒos. Total
	 * @param ascendents_mayor_75_entirely Ascendientes computados mayores de 75 aÒos. Por entero
	 * @param ascendents_33_65_total Ascendientes con discapacidad >= 33% y < 65%. Total
	 * @param ascendents_33_65_entirely Ascendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param ascendents_moving_total Ascendientes con discapacidad, movilidad reducida. Total
	 * @param ascendents_moving_entirely Ascendientes con discapacidad, movilidad reducida. Por entero
	 * @param ascendents_65_total Ascendientes con discapacidad > 65%. Total
	 * @param ascendents_65_entirely Ascendientes con discapacidad > 65%. Por entero
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_result(Integer contract, Date effective_date, Double base_irpf, Double minimun_personal_family, Double deduct_home_loan_amount, Double deduct_80_bis, Double irpf, Double annual_irpf, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double work_remuneration_reduction, Double work_prolongation_reduction, Double work_moving_reduction, Double work_disability_reduction, Double social_security_pensioner, Double two_or_more_descendents_min, Double spousal_support, Double food_annuity, Double minimun_personal, Double minimun_ascendents, Double minimun_descendents, Double minimun_disability, Short descendents_minor_3_total, Short descendents_minor_3_entirely, Short descendents_remainder_total, Short descendents_remainder_entirely, Short descendents_33_65_total, Short descendents_33_65_entirely, Short descendents_moving_total, Short descendents_moving_entirely, Short descendents_65_total, Short descendents_65_entirely, Short descendents_first, Short descendents_second, Short descendents_third, Short descendents_fourth_subsequent_total, Short descendents_fourth_subsequent_entirely, Short ascendents_minor_75_total, Short ascendents_minor_75_entirely, Short ascendents_mayor_75_total, Short ascendents_mayor_75_entirely, Short ascendents_33_65_total, Short ascendents_33_65_entirely, Short ascendents_moving_total, Short ascendents_moving_entirely, Short ascendents_65_total, Short ascendents_65_entirely)
	throws SQLException {
		Integer domain = getDomainForIrpf_result( contract);
		Integer id =  super.insertIrpf_result( domain != null ? domain : getDefaultDomain(), contract, effective_date, base_irpf, minimun_personal_family, deduct_home_loan_amount, deduct_80_bis, irpf, annual_irpf, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, work_remuneration_reduction, work_prolongation_reduction, work_moving_reduction, work_disability_reduction, social_security_pensioner, two_or_more_descendents_min, spousal_support, food_annuity, minimun_personal, minimun_ascendents, minimun_descendents, minimun_disability, descendents_minor_3_total, descendents_minor_3_entirely, descendents_remainder_total, descendents_remainder_entirely, descendents_33_65_total, descendents_33_65_entirely, descendents_moving_total, descendents_moving_entirely, descendents_65_total, descendents_65_entirely, descendents_first, descendents_second, descendents_third, descendents_fourth_subsequent_total, descendents_fourth_subsequent_entirely, ascendents_minor_75_total, ascendents_minor_75_entirely, ascendents_mayor_75_total, ascendents_mayor_75_entirely, ascendents_33_65_total, ascendents_33_65_entirely, ascendents_moving_total, ascendents_moving_entirely, ascendents_65_total, ascendents_65_entirely );
		if ( domain != null ) { 
			irpf_resultDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Irpf_result
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del contrato
	 * @param effective_date Fecha de entrada en vigor
	 * @param base_irpf Base para calcular el tipo de retenciÛn
	 * @param minimun_personal_family MÌnimo personal y familiar para calcular el tipo de retenciÛn
	 * @param deduct_home_loan_amount MinoraciÛn por pagos de prÈstamo para vivienda habitual
	 * @param deduct_80_bis Deduccion ArttÌculo 80 bis LIRPF
	 * @param irpf Tipo retenciÛn apliclabe 
	 * @param annual_irpf Importe anual de las retenciones e ingresos a cuenta
	 * @param annual_remuneration Retribuciones anuales. Importe Ìntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Art. 18.3: DD.TT 11™ y 12 ™ de la LIRPF). Importe
	 * @param deduccibles_expenses Gastos deducibles. Importe anual
	 * @param work_remuneration_reduction Reducciones por rendimiento del trabajo 
	 * @param work_prolongation_reduction Reducciones por prolongaciÛn de la actividad 
	 * @param work_moving_reduction Reducciones por movilidad geografica 
	 * @param work_disability_reduction Reducciones por discapacidad 
	 * @param social_security_pensioner Por ser pensionista de la s. social/cl. Pasivas o desempleado
	 * @param two_or_more_descendents_min Por tener m·s de dos descendientes con derecho a mÌnimo
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge. Importe anual
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe anual
	 * @param minimun_personal MÌnimo personal
	 * @param minimun_ascendents MÌnimo por descendientes
	 * @param minimun_descendents MÌnimo por descendientes
	 * @param minimun_disability MÌnimo por discapacidad
	 * @param descendents_minor_3_total Descendientes computados menores de tres aÒos. Total
	 * @param descendents_minor_3_entirely Descendientes computados menores de tres aÒos. Por entero
	 * @param descendents_remainder_total Resto de descendientes computados . Total
	 * @param descendents_remainder_entirely Resto de descendientes computados . Por entero
	 * @param descendents_33_65_total Descendientes con discapacidad >= 33% y < 65%. Total
	 * @param descendents_33_65_entirely Descendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param descendents_moving_total Descendientes con discapacidad, movilidad reducida. Total
	 * @param descendents_moving_entirely Descendientes con discapacidad, movilidad reducida. Por entero
	 * @param descendents_65_total Descendientes con discapacidad > 65%. Total
	 * @param descendents_65_entirely Descendientes con discapacidad > 65%. Por entero
	 * @param descendents_first Detalle del cÛmputo de descendientes. Hijo 1∫
	 * @param descendents_second Detalle del cÛmputo de descendientes. Hijo 2∫
	 * @param descendents_third Detalle del cÛmputo de descendientes. Hijo 3∫
	 * @param descendents_fourth_subsequent_total Detalle del cÛmputo de descendientes. Hijo 4∫ y sucesivos. Total
	 * @param descendents_fourth_subsequent_entirely Detalle del cÛmputo de descendientes. Hijo 4∫ y sucesivos. Por entero
	 * @param ascendents_minor_75_total Ascendientes computados menores de 75 aÒos. Total
	 * @param ascendents_minor_75_entirely Ascendientes computados menores de 75 aÒos. Por entero
	 * @param ascendents_mayor_75_total Ascendientes computados mayores de 75 aÒos. Total
	 * @param ascendents_mayor_75_entirely Ascendientes computados mayores de 75 aÒos. Por entero
	 * @param ascendents_33_65_total Ascendientes con discapacidad >= 33% y < 65%. Total
	 * @param ascendents_33_65_entirely Ascendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param ascendents_moving_total Ascendientes con discapacidad, movilidad reducida. Total
	 * @param ascendents_moving_entirely Ascendientes con discapacidad, movilidad reducida. Por entero
	 * @param ascendents_65_total Ascendientes con discapacidad > 65%. Total
	 * @param ascendents_65_entirely Ascendientes con discapacidad > 65%. Por entero
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_result(Integer domain, Integer contract, Date effective_date, Double base_irpf, Double minimun_personal_family, Double deduct_home_loan_amount, Double deduct_80_bis, Double irpf, Double annual_irpf, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double work_remuneration_reduction, Double work_prolongation_reduction, Double work_moving_reduction, Double work_disability_reduction, Double social_security_pensioner, Double two_or_more_descendents_min, Double spousal_support, Double food_annuity, Double minimun_personal, Double minimun_ascendents, Double minimun_descendents, Double minimun_disability, Short descendents_minor_3_total, Short descendents_minor_3_entirely, Short descendents_remainder_total, Short descendents_remainder_entirely, Short descendents_33_65_total, Short descendents_33_65_entirely, Short descendents_moving_total, Short descendents_moving_entirely, Short descendents_65_total, Short descendents_65_entirely, Short descendents_first, Short descendents_second, Short descendents_third, Short descendents_fourth_subsequent_total, Short descendents_fourth_subsequent_entirely, Short ascendents_minor_75_total, Short ascendents_minor_75_entirely, Short ascendents_mayor_75_total, Short ascendents_mayor_75_entirely, Short ascendents_33_65_total, Short ascendents_33_65_entirely, Short ascendents_moving_total, Short ascendents_moving_entirely, Short ascendents_65_total, Short ascendents_65_entirely)
	throws SQLException {
		Integer id =  super.insertIrpf_result(domain, contract, effective_date, base_irpf, minimun_personal_family, deduct_home_loan_amount, deduct_80_bis, irpf, annual_irpf, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, work_remuneration_reduction, work_prolongation_reduction, work_moving_reduction, work_disability_reduction, social_security_pensioner, two_or_more_descendents_min, spousal_support, food_annuity, minimun_personal, minimun_ascendents, minimun_descendents, minimun_disability, descendents_minor_3_total, descendents_minor_3_entirely, descendents_remainder_total, descendents_remainder_entirely, descendents_33_65_total, descendents_33_65_entirely, descendents_moving_total, descendents_moving_entirely, descendents_65_total, descendents_65_entirely, descendents_first, descendents_second, descendents_third, descendents_fourth_subsequent_total, descendents_fourth_subsequent_entirely, ascendents_minor_75_total, ascendents_minor_75_entirely, ascendents_mayor_75_total, ascendents_mayor_75_entirely, ascendents_33_65_total, ascendents_33_65_entirely, ascendents_moving_total, ascendents_moving_entirely, ascendents_65_total, ascendents_65_entirely);
		irpf_resultDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model190_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model190_detailPk(Integer id){
		return fs_model190_detailDomains.get(id);
	}
	
	/**
	 * Fs_model190_detail
	 * @param fs_model190 Identificador del modelo 190
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model190_detail( Integer fs_model190){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model190Pk( fs_model190 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model190_detail
	 * @param id Identificador unico
	 * @param fs_model190 Identificador del modelo 190
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param province Provincia
	 * @param key Clave Percepcion
	 * @param subkey Subclave Percepcion
	 * @param perception 
	 * @param retention 
	 * @param in_kind_perception 
	 * @param in_kind_deposit 
	 * @param in_kind_output_deposit 
	 * @param accrual_year 
	 * @param ceuta_melilla Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla
	 * @param birth_year 
	 * @param family_situation Situacion familiar
	 * @param spouse_document Numero de Documento del conyuge
	 * @param disability Grado de discapacidad
	 * @param contract Contrato o relacion
	 * @param labour_prolongation Prolongacion de la actividad laboral
	 * @param geographic_mobility Movilidad geografica
	 * @param applicable_reduction Reducciones aplicables
	 * @param deducible_expenses Gastos deducibles 
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge.
	 * @param food_annuity Anualidades por alimentos en favor de los hijos.
	 * @param less_than_3_descendent 
	 * @param less_than_3_descendent_ratio 
	 * @param other_descendent 
	 * @param other_descendent_ratio 
	 * @param disability_descendent_33 
	 * @param disability_descendent_33_ratio 
	 * @param disability_descendent_dependence 
	 * @param disability_descendent_dependence_ratio 
	 * @param disability_descendent_65 
	 * @param disability_descendent_65_ratio 
	 * @param less_than_75_ascendant 
	 * @param less_than_75_ascendant_ratio 
	 * @param ascendant 
	 * @param ascendant_ratio 
	 * @param disability_ascendant_33 
	 * @param disability_ascendant_33_ratio 
	 * @param disability_ascendant_dependence 
	 * @param disability_ascendant_dependence_ratio 
	 * @param disability_ascendant_65 
	 * @param disability_ascendant_65_Ratio 
	 * @param first_child_calculation 
	 * @param second_child_calculation 
	 * @param third_child_calculation 
	 * @param home_loan_communnication 
	 * @param perception_il Percepci?n Integra/valoracion derivada de incapacidad laboral
	 * @param retention_il Retenciones practicadas/ingresos a cuenta efectuados derivadas de incapacidad laboral
	 * @param output_retention_il Ingresos a cuenta repercutidos derivados de incapacidad laboral
	 * @param in_kind_perception_il 
	 * @param in_kind_deposit_il 
	 * @param in_kind_output_deposit_il 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model190_detail(Integer fs_model190, String document, String name, String representative_document, Integer province, String key, String subkey, Double perception, Double retention, Double in_kind_perception, Double in_kind_deposit, Double in_kind_output_deposit, Integer accrual_year, Boolean ceuta_melilla, Integer birth_year, Short family_situation, String spouse_document, Short disability, Short contract, Boolean labour_prolongation, Boolean geographic_mobility, Double applicable_reduction, Double deducible_expenses, Double spousal_support, Double food_annuity, Boolean less_than_3_descendent, Boolean less_than_3_descendent_ratio, Boolean other_descendent, Boolean other_descendent_ratio, Boolean disability_descendent_33, Boolean disability_descendent_33_ratio, Boolean disability_descendent_dependence, Boolean disability_descendent_dependence_ratio, Boolean disability_descendent_65, Boolean disability_descendent_65_ratio, Boolean less_than_75_ascendant, Boolean less_than_75_ascendant_ratio, Boolean ascendant, Boolean ascendant_ratio, Boolean disability_ascendant_33, Boolean disability_ascendant_33_ratio, Boolean disability_ascendant_dependence, Boolean disability_ascendant_dependence_ratio, Boolean disability_ascendant_65, Boolean disability_ascendant_65_Ratio, Boolean first_child_calculation, Boolean second_child_calculation, Boolean third_child_calculation, Boolean home_loan_communnication, Double perception_il, Double retention_il, Double output_retention_il, Double in_kind_perception_il, Double in_kind_deposit_il, Double in_kind_output_deposit_il)
	throws SQLException {
		Integer domain = getDomainForFs_model190_detail( fs_model190);
		Integer id =  super.insertFs_model190_detail( domain != null ? domain : getDefaultDomain(), fs_model190, document, name, representative_document, province, key, subkey, perception, retention, in_kind_perception, in_kind_deposit, in_kind_output_deposit, accrual_year, ceuta_melilla, birth_year, family_situation, spouse_document, disability, contract, labour_prolongation, geographic_mobility, applicable_reduction, deducible_expenses, spousal_support, food_annuity, less_than_3_descendent, less_than_3_descendent_ratio, other_descendent, other_descendent_ratio, disability_descendent_33, disability_descendent_33_ratio, disability_descendent_dependence, disability_descendent_dependence_ratio, disability_descendent_65, disability_descendent_65_ratio, less_than_75_ascendant, less_than_75_ascendant_ratio, ascendant, ascendant_ratio, disability_ascendant_33, disability_ascendant_33_ratio, disability_ascendant_dependence, disability_ascendant_dependence_ratio, disability_ascendant_65, disability_ascendant_65_Ratio, first_child_calculation, second_child_calculation, third_child_calculation, home_loan_communnication, perception_il, retention_il, output_retention_il, in_kind_perception_il, in_kind_deposit_il, in_kind_output_deposit_il );
		if ( domain != null ) { 
			fs_model190_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model190_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model190 Identificador del modelo 190
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param province Provincia
	 * @param key Clave Percepcion
	 * @param subkey Subclave Percepcion
	 * @param perception 
	 * @param retention 
	 * @param in_kind_perception 
	 * @param in_kind_deposit 
	 * @param in_kind_output_deposit 
	 * @param accrual_year 
	 * @param ceuta_melilla Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla
	 * @param birth_year 
	 * @param family_situation Situacion familiar
	 * @param spouse_document Numero de Documento del conyuge
	 * @param disability Grado de discapacidad
	 * @param contract Contrato o relacion
	 * @param labour_prolongation Prolongacion de la actividad laboral
	 * @param geographic_mobility Movilidad geografica
	 * @param applicable_reduction Reducciones aplicables
	 * @param deducible_expenses Gastos deducibles 
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge.
	 * @param food_annuity Anualidades por alimentos en favor de los hijos.
	 * @param less_than_3_descendent 
	 * @param less_than_3_descendent_ratio 
	 * @param other_descendent 
	 * @param other_descendent_ratio 
	 * @param disability_descendent_33 
	 * @param disability_descendent_33_ratio 
	 * @param disability_descendent_dependence 
	 * @param disability_descendent_dependence_ratio 
	 * @param disability_descendent_65 
	 * @param disability_descendent_65_ratio 
	 * @param less_than_75_ascendant 
	 * @param less_than_75_ascendant_ratio 
	 * @param ascendant 
	 * @param ascendant_ratio 
	 * @param disability_ascendant_33 
	 * @param disability_ascendant_33_ratio 
	 * @param disability_ascendant_dependence 
	 * @param disability_ascendant_dependence_ratio 
	 * @param disability_ascendant_65 
	 * @param disability_ascendant_65_Ratio 
	 * @param first_child_calculation 
	 * @param second_child_calculation 
	 * @param third_child_calculation 
	 * @param home_loan_communnication 
	 * @param perception_il Percepci?n Integra/valoracion derivada de incapacidad laboral
	 * @param retention_il Retenciones practicadas/ingresos a cuenta efectuados derivadas de incapacidad laboral
	 * @param output_retention_il Ingresos a cuenta repercutidos derivados de incapacidad laboral
	 * @param in_kind_perception_il 
	 * @param in_kind_deposit_il 
	 * @param in_kind_output_deposit_il 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model190_detail(Integer domain, Integer fs_model190, String document, String name, String representative_document, Integer province, String key, String subkey, Double perception, Double retention, Double in_kind_perception, Double in_kind_deposit, Double in_kind_output_deposit, Integer accrual_year, Boolean ceuta_melilla, Integer birth_year, Short family_situation, String spouse_document, Short disability, Short contract, Boolean labour_prolongation, Boolean geographic_mobility, Double applicable_reduction, Double deducible_expenses, Double spousal_support, Double food_annuity, Boolean less_than_3_descendent, Boolean less_than_3_descendent_ratio, Boolean other_descendent, Boolean other_descendent_ratio, Boolean disability_descendent_33, Boolean disability_descendent_33_ratio, Boolean disability_descendent_dependence, Boolean disability_descendent_dependence_ratio, Boolean disability_descendent_65, Boolean disability_descendent_65_ratio, Boolean less_than_75_ascendant, Boolean less_than_75_ascendant_ratio, Boolean ascendant, Boolean ascendant_ratio, Boolean disability_ascendant_33, Boolean disability_ascendant_33_ratio, Boolean disability_ascendant_dependence, Boolean disability_ascendant_dependence_ratio, Boolean disability_ascendant_65, Boolean disability_ascendant_65_Ratio, Boolean first_child_calculation, Boolean second_child_calculation, Boolean third_child_calculation, Boolean home_loan_communnication, Double perception_il, Double retention_il, Double output_retention_il, Double in_kind_perception_il, Double in_kind_deposit_il, Double in_kind_output_deposit_il)
	throws SQLException {
		Integer id =  super.insertFs_model190_detail(domain, fs_model190, document, name, representative_document, province, key, subkey, perception, retention, in_kind_perception, in_kind_deposit, in_kind_output_deposit, accrual_year, ceuta_melilla, birth_year, family_situation, spouse_document, disability, contract, labour_prolongation, geographic_mobility, applicable_reduction, deducible_expenses, spousal_support, food_annuity, less_than_3_descendent, less_than_3_descendent_ratio, other_descendent, other_descendent_ratio, disability_descendent_33, disability_descendent_33_ratio, disability_descendent_dependence, disability_descendent_dependence_ratio, disability_descendent_65, disability_descendent_65_ratio, less_than_75_ascendant, less_than_75_ascendant_ratio, ascendant, ascendant_ratio, disability_ascendant_33, disability_ascendant_33_ratio, disability_ascendant_dependence, disability_ascendant_dependence_ratio, disability_ascendant_65, disability_ascendant_65_Ratio, first_child_calculation, second_child_calculation, third_child_calculation, home_loan_communnication, perception_il, retention_il, output_retention_il, in_kind_perception_il, in_kind_deposit_il, in_kind_output_deposit_il);
		fs_model190_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> raddressDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRaddressPk(Integer id){
		return raddressDomains.get(id);
	}
	
	/**
	 * Raddress
	 * @param geozone Identificador de la Zona Geografica
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRaddress( Integer geozone , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForGeozonePk( geozone ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Raddress
	 * @param id Identificador unico de la Direccion de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param type Tipo de Direccion
	 * @param recipient Destinatario
	 * @param street_type Tipo de via
	 * @param address Primera parte de la Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param address3 Tercera parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param geozone Identificador de la Zona Geografica
	 * @param alias Alias
	 * @param municipality_code Codigo del municipio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRaddress(Integer registry, Short type, String recipient, String street_type, String address, String number, String address2, String address3, String zip, String city, Integer geozone, String alias, String municipality_code)
	throws SQLException {
		Integer domain = getDomainForRaddress( geozone , registry);
		Integer id =  super.insertRaddress( domain != null ? domain : getDefaultDomain(), registry, type, recipient, street_type, address, number, address2, address3, zip, city, geozone, alias, municipality_code );
		if ( domain != null ) { 
			raddressDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Raddress
	 * @param id Identificador unico de la Direccion de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param type Tipo de Direccion
	 * @param recipient Destinatario
	 * @param street_type Tipo de via
	 * @param address Primera parte de la Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param address3 Tercera parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param geozone Identificador de la Zona Geografica
	 * @param alias Alias
	 * @param municipality_code Codigo del municipio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRaddress(Integer domain, Integer registry, Short type, String recipient, String street_type, String address, String number, String address2, String address3, String zip, String city, Integer geozone, String alias, String municipality_code)
	throws SQLException {
		Integer id =  super.insertRaddress(domain, registry, type, recipient, street_type, address, number, address2, address3, zip, city, geozone, alias, municipality_code);
		raddressDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_mod349_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_mod349_detailPk(Integer id){
		return fs_mod349_detailDomains.get(id);
	}
	
	/**
	 * Fs_mod349_detail
	 * @param fs_mod349 Identificador de la Declaracion
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_mod349_detail( Integer fs_mod349){
		Integer domain = null;
			if ( ( domain = getDomainForFs_mod349Pk( fs_mod349 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_mod349_detail
	 * @param id Identificador unico
	 * @param fs_mod349 Identificador de la Declaracion
	 * @param rectification Rectificacion
	 * @param type Clave de operacion
	 * @param document Documento del operador
	 * @param registry Identificador del Declarado
	 * @param name Apellidos y Nombre del Declarado
	 * @param country Pais del Declarado
	 * @param accumulated Importe acumulado de las operaciones
	 * @param declared Importe declarado de las operaciones
	 * @param amount Importe de las operaciones
	 * @param rectified_year Ejercicio de la Declaracion del importe rectificado
	 * @param rectified_period Periodo de la Declaracion del importe rectificado
	 * @param rectified_amount Importe rectificado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod349_detail(Integer fs_mod349, Boolean rectification, String type, String document, Integer registry, String name, String country, Double accumulated, Double declared, Double amount, Integer rectified_year, Short rectified_period, Double rectified_amount)
	throws SQLException {
		Integer domain = getDomainForFs_mod349_detail( fs_mod349);
		Integer id =  super.insertFs_mod349_detail( domain != null ? domain : getDefaultDomain(), fs_mod349, rectification, type, document, registry, name, country, accumulated, declared, amount, rectified_year, rectified_period, rectified_amount );
		if ( domain != null ) { 
			fs_mod349_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_mod349_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_mod349 Identificador de la Declaracion
	 * @param rectification Rectificacion
	 * @param type Clave de operacion
	 * @param document Documento del operador
	 * @param registry Identificador del Declarado
	 * @param name Apellidos y Nombre del Declarado
	 * @param country Pais del Declarado
	 * @param accumulated Importe acumulado de las operaciones
	 * @param declared Importe declarado de las operaciones
	 * @param amount Importe de las operaciones
	 * @param rectified_year Ejercicio de la Declaracion del importe rectificado
	 * @param rectified_period Periodo de la Declaracion del importe rectificado
	 * @param rectified_amount Importe rectificado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod349_detail(Integer domain, Integer fs_mod349, Boolean rectification, String type, String document, Integer registry, String name, String country, Double accumulated, Double declared, Double amount, Integer rectified_year, Short rectified_period, Double rectified_amount)
	throws SQLException {
		Integer id =  super.insertFs_mod349_detail(domain, fs_mod349, rectification, type, document, registry, name, country, accumulated, declared, amount, rectified_year, rectified_period, rectified_amount);
		fs_mod349_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> action_entryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAction_entryPk(Integer id){
		return action_entryDomains.get(id);
	}
	
	/**
	 * Action_entry
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @returns domain's ID
	*/
	protected Integer getDomainForAction_entry( Integer action_id , Integer session_id){
		Integer domain = null;
			if ( ( domain = getDomainForSessionPk( session_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Action_entry
	 * @param id Identificador unico
	 * @param executionDate Fecha de ejecucion
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_entry(Timestamp executionDate, Integer action_id, Integer session_id)
	throws SQLException {
		Integer domain = getDomainForAction_entry( action_id , session_id);
		Integer id =  super.insertAction_entry( domain != null ? domain : getDefaultDomain(), executionDate, action_id, session_id );
		if ( domain != null ) { 
			action_entryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Action_entry
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param executionDate Fecha de ejecucion
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_entry(Integer domain, Timestamp executionDate, Integer action_id, Integer session_id)
	throws SQLException {
		Integer id =  super.insertAction_entry(domain, executionDate, action_id, session_id);
		action_entryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commercial_activityDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommercial_activityPk(Integer id){
		return commercial_activityDomains.get(id);
	}
	
	/**
	 * Commercial_activity
	 * @param survey Identificador del Cuestionario
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommercial_activity( Integer survey){
		Integer domain = null;
			if ( ( domain = getDomainForSurveyPk( survey ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Commercial_activity
	 * @param id Identificador unico
	 * @param name Nombre de la Actividad Comercial
	 * @param probability Probabilidad de la Actividad
	 * @param survey Identificador del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_activity(String name, Integer probability, Integer survey)
	throws SQLException {
		Integer domain = getDomainForCommercial_activity( survey);
		Integer id =  super.insertCommercial_activity( domain != null ? domain : getDefaultDomain(), name, probability, survey );
		if ( domain != null ) { 
			commercial_activityDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commercial_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Actividad Comercial
	 * @param probability Probabilidad de la Actividad
	 * @param survey Identificador del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_activity(Integer domain, String name, Integer probability, Integer survey)
	throws SQLException {
		Integer id =  super.insertCommercial_activity(domain, name, probability, survey);
		commercial_activityDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_typePk(Integer id){
		return project_typeDomains.get(id);
	}
	
	/**
	 * Project_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Project_type
	 * @param id Identificador unico del Tipo de Expediente
	 * @param description Descripcion del Tipo de Expediente
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_type(String description, Boolean active)
	throws SQLException {
		Integer domain = getDomainForProject_type();
		Integer id =  super.insertProject_type( domain != null ? domain : getDefaultDomain(), description, active );
		if ( domain != null ) { 
			project_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_type
	 * @param id Identificador unico del Tipo de Expediente
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Expediente
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_type(Integer domain, String description, Boolean active)
	throws SQLException {
		Integer id =  super.insertProject_type(domain, description, active);
		project_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> payroll_batch_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPayroll_batch_attachPk(Integer id){
		return payroll_batch_attachDomains.get(id);
	}
	
	/**
	 * Payroll_batch_attach
	 * @param scope Ambito del Archivo Adjunto
	 * @returns domain's ID
	*/
	protected Integer getDomainForPayroll_batch_attach( Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Payroll_batch_attach
	 * @param id Identificador unico
	 * @param source_batch Identificador de la Remesa
	 * @param source_type Tipo de la Remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayroll_batch_attach(Integer source_batch, Short source_type, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date, String driveId)
	throws SQLException {
		Integer domain = getDomainForPayroll_batch_attach( scope);
		Integer id =  super.insertPayroll_batch_attach( domain != null ? domain : getDefaultDomain(), source_batch, source_type, mimeType, description, data, type, scope, attach_date, driveId );
		if ( domain != null ) { 
			payroll_batch_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Payroll_batch_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param source_batch Identificador de la Remesa
	 * @param source_type Tipo de la Remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPayroll_batch_attach(Integer domain, Integer source_batch, Short source_type, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date, String driveId)
	throws SQLException {
		Integer id =  super.insertPayroll_batch_attach(domain, source_batch, source_type, mimeType, description, data, type, scope, attach_date, driveId);
		payroll_batch_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> process_transition_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProcess_transition_typePk(Integer id){
		return process_transition_typeDomains.get(id);
	}
	
	/**
	 * Process_transition_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForProcess_transition_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Process_transition_type
	 * @param id Identificador unico
	 * @param description Descripcion del Tipo de Transicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_transition_type(String description)
	throws SQLException {
		Integer domain = getDomainForProcess_transition_type();
		Integer id =  super.insertProcess_transition_type( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			process_transition_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Process_transition_type
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Transicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_transition_type(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertProcess_transition_type(domain, description);
		process_transition_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservationPk(Integer project){
		return project_reservationDomains.get(project);
	}
	
	/**
	 * Project_reservation
	 * @param agency Identificador de la agencia de viajes
	 * @param company Identificador de la empresa
	 * @param hotel Identificador del Hotel de Produccion
	 * @param hotel_reservation Identificador del Hotel de la Reserva
	 * @param project Identificador del Proyecto
	 * @param seller Identificador del canal de venta
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation( Integer agency , Integer company , Integer hotel , Integer hotel_reservation , Integer project , Integer seller){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( agency ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( company ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel_reservation ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation
	 * @param project Identificador del Proyecto
	 * @param hotel Identificador del Hotel de Produccion
	 * @param hotel_reservation Identificador del Hotel de la Reserva
	 * @param code Localizador de la Reserva
	 * @param start_date Fecha de entrada
	 * @param start_time Hora de entrada
	 * @param end_date Fecha de salida
	 * @param end_time Hora de salida
	 * @param seller Identificador del canal de venta
	 * @param agency Identificador de la agencia de viajes
	 * @param agency_commission_percent Porcentaje de comision de la agencia
	 * @param agency_commission_amount Importe de comision de la agencia
	 * @param agency_rebate Indica si la agencia trabaja en modo descuento o no
	 * @param company Identificador de la empresa
	 * @param discount_percent Porcentaje de descuento
	 * @param discount_amount Importe de descuento
	 * @param booking_holder Titular de la Reserva
	 * @param taxable_base Base imponible
	 * @param vat_quota Cuota de IVA
	 * @param other_tax_quota Cuota de otros Impuestos
	 * @param total Importe Total
	 * @param comments Comentarios
	 * @param remarks Observaciones
	 * @param source Origen de la Reserva
	 * @param crs_code Codigo de la Reserva en el CRS
	 * @param advance Anticipo
	 * @param advance_invoiced Indica si el anticipo esta Facturado
	 * @param early_check_out Indica si se ha producido una salida anticipada
	 * @param prepay Indica si es un prepago
	 * @param bank_transaction Codigo de transaccion bancaria
	 * @param credit_card_number Ultimos 4 numeros de la tarjeta de credito
	 * @param credit_card_expiration_month Mes de expiracion de la tarjeta de credito
	 * @param credit_card_expiration_year AÒo de expiracion de la tarjeta de credito
	 * @param credit_card_type Tipo de tarjeta de credito
	 * @param token Token de preautorizacion de cobro
	 * @param penalty_value Valor de penalizacion (patron)
	 * @param penalty_amount Importe de penalizacion
	 * @param penalty_date Fecha de penalizacion
	 * @param tourist_tax_free Tipo de exencion de la Tasa turistica
	 * @param check_status Estado de registro en el Hotel
	 * @param status Estado de la Reserva
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @param cancellation_user Usuario de cancelacion
	 * @param cancellation_date Fecha de cancelacion
	 * @throws SQLException
	*/
	public void insertProject_reservation(Integer project, Integer hotel, Integer hotel_reservation, String code, Date start_date, Timestamp start_time, Date end_date, Timestamp end_time, Integer seller, Integer agency, Double agency_commission_percent, Double agency_commission_amount, Boolean agency_rebate, Integer company, Double discount_percent, Double discount_amount, Short booking_holder, Double taxable_base, Double vat_quota, Double other_tax_quota, Double total, String comments, String remarks, Short source, String crs_code, Double advance, Boolean advance_invoiced, Boolean early_check_out, Boolean prepay, String bank_transaction, String credit_card_number, String credit_card_expiration_month, String credit_card_expiration_year, String credit_card_type, String token, String penalty_value, Double penalty_amount, Timestamp penalty_date, Short tourist_tax_free, Short check_status, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date, String cancellation_user, Timestamp cancellation_date)
	throws SQLException {
		Integer domain = getDomainForProject_reservation( agency , company , hotel , hotel_reservation , project , seller);
		 super.insertProject_reservation( project, domain != null ? domain : getDefaultDomain(), hotel, hotel_reservation, code, start_date, start_time, end_date, end_time, seller, agency, agency_commission_percent, agency_commission_amount, agency_rebate, company, discount_percent, discount_amount, booking_holder, taxable_base, vat_quota, other_tax_quota, total, comments, remarks, source, crs_code, advance, advance_invoiced, early_check_out, prepay, bank_transaction, credit_card_number, credit_card_expiration_month, credit_card_expiration_year, credit_card_type, token, penalty_value, penalty_amount, penalty_date, tourist_tax_free, check_status, status, creation_user, creation_date, modification_user, modification_date, cancellation_user, cancellation_date );
		if ( domain != null ) { 
			project_reservationDomains.put(project, domain);
		}
	}

	/**
	 * Project_reservation
	 * @param project Identificador del Proyecto
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel de Produccion
	 * @param hotel_reservation Identificador del Hotel de la Reserva
	 * @param code Localizador de la Reserva
	 * @param start_date Fecha de entrada
	 * @param start_time Hora de entrada
	 * @param end_date Fecha de salida
	 * @param end_time Hora de salida
	 * @param seller Identificador del canal de venta
	 * @param agency Identificador de la agencia de viajes
	 * @param agency_commission_percent Porcentaje de comision de la agencia
	 * @param agency_commission_amount Importe de comision de la agencia
	 * @param agency_rebate Indica si la agencia trabaja en modo descuento o no
	 * @param company Identificador de la empresa
	 * @param discount_percent Porcentaje de descuento
	 * @param discount_amount Importe de descuento
	 * @param booking_holder Titular de la Reserva
	 * @param taxable_base Base imponible
	 * @param vat_quota Cuota de IVA
	 * @param other_tax_quota Cuota de otros Impuestos
	 * @param total Importe Total
	 * @param comments Comentarios
	 * @param remarks Observaciones
	 * @param source Origen de la Reserva
	 * @param crs_code Codigo de la Reserva en el CRS
	 * @param advance Anticipo
	 * @param advance_invoiced Indica si el anticipo esta Facturado
	 * @param early_check_out Indica si se ha producido una salida anticipada
	 * @param prepay Indica si es un prepago
	 * @param bank_transaction Codigo de transaccion bancaria
	 * @param credit_card_number Ultimos 4 numeros de la tarjeta de credito
	 * @param credit_card_expiration_month Mes de expiracion de la tarjeta de credito
	 * @param credit_card_expiration_year AÒo de expiracion de la tarjeta de credito
	 * @param credit_card_type Tipo de tarjeta de credito
	 * @param token Token de preautorizacion de cobro
	 * @param penalty_value Valor de penalizacion (patron)
	 * @param penalty_amount Importe de penalizacion
	 * @param penalty_date Fecha de penalizacion
	 * @param tourist_tax_free Tipo de exencion de la Tasa turistica
	 * @param check_status Estado de registro en el Hotel
	 * @param status Estado de la Reserva
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @param cancellation_user Usuario de cancelacion
	 * @param cancellation_date Fecha de cancelacion
	 * @throws SQLException
	*/
	public void insertProject_reservation(Integer project, Integer domain, Integer hotel, Integer hotel_reservation, String code, Date start_date, Timestamp start_time, Date end_date, Timestamp end_time, Integer seller, Integer agency, Double agency_commission_percent, Double agency_commission_amount, Boolean agency_rebate, Integer company, Double discount_percent, Double discount_amount, Short booking_holder, Double taxable_base, Double vat_quota, Double other_tax_quota, Double total, String comments, String remarks, Short source, String crs_code, Double advance, Boolean advance_invoiced, Boolean early_check_out, Boolean prepay, String bank_transaction, String credit_card_number, String credit_card_expiration_month, String credit_card_expiration_year, String credit_card_type, String token, String penalty_value, Double penalty_amount, Timestamp penalty_date, Short tourist_tax_free, Short check_status, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date, String cancellation_user, Timestamp cancellation_date)
	throws SQLException {
		 super.insertProject_reservation(project, domain, hotel, hotel_reservation, code, start_date, start_time, end_date, end_time, seller, agency, agency_commission_percent, agency_commission_amount, agency_rebate, company, discount_percent, discount_amount, booking_holder, taxable_base, vat_quota, other_tax_quota, total, comments, remarks, source, crs_code, advance, advance_invoiced, early_check_out, prepay, bank_transaction, credit_card_number, credit_card_expiration_month, credit_card_expiration_year, credit_card_type, token, penalty_value, penalty_amount, penalty_date, tourist_tax_free, check_status, status, creation_user, creation_date, modification_user, modification_date, cancellation_user, cancellation_date);
		project_reservationDomains.put(project, domain );
			}

	
	private Map<Integer,Integer> warehouse_transfer_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWarehouse_transfer_detailPk(Integer id){
		return warehouse_transfer_detailDomains.get(id);
	}
	
	/**
	 * Warehouse_transfer_detail
	 * @param item Identificador del Articulo del Detalle de Traspaso
	 * @param warehouse_transfer Identificador del Traspaso
	 * @returns domain's ID
	*/
	protected Integer getDomainForWarehouse_transfer_detail( Integer item , Integer warehouse_transfer){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehouse_transferPk( warehouse_transfer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Warehouse_transfer_detail
	 * @param id Identificador unico
	 * @param warehouse_transfer Identificador del Traspaso
	 * @param item Identificador del Articulo del Detalle de Traspaso
	 * @param quantity Cantidad del Detalle de Traspaso
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse_transfer_detail(Integer warehouse_transfer, Integer item, Double quantity, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForWarehouse_transfer_detail( item , warehouse_transfer);
		Integer id =  super.insertWarehouse_transfer_detail( domain != null ? domain : getDefaultDomain(), warehouse_transfer, item, quantity, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			warehouse_transfer_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Warehouse_transfer_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param warehouse_transfer Identificador del Traspaso
	 * @param item Identificador del Articulo del Detalle de Traspaso
	 * @param quantity Cantidad del Detalle de Traspaso
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse_transfer_detail(Integer domain, Integer warehouse_transfer, Integer item, Double quantity, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertWarehouse_transfer_detail(domain, warehouse_transfer, item, quantity, creation_user, creation_date, modification_user, modification_date);
		warehouse_transfer_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_serviceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_servicePk(Integer id){
		return project_reservation_serviceDomains.get(id);
	}
	
	/**
	 * Project_reservation_service
	 * @param item Identificador del Servicio
	 * @param project_reservation Identificador de la Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_service( Integer item , Integer project_reservation){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservationPk( project_reservation ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_service
	 * @param id Identificador unico
	 * @param project_reservation Identificador de la Reserva
	 * @param service_index Numero de Servicio
	 * @param service_code Codigo del Servicio en origen
	 * @param item Identificador del Servicio
	 * @param description Descripcion
	 * @param meal_plan Regimen
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param extra Indica si se trata de un Servicio extra
	 * @param removed Servicio borrado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_service(Integer project_reservation, Short service_index, String service_code, Integer item, String description, String meal_plan, Integer project_reservation_room, Boolean extra, Boolean removed, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_service( item , project_reservation);
		Integer id =  super.insertProject_reservation_service( domain != null ? domain : getDefaultDomain(), project_reservation, service_index, service_code, item, description, meal_plan, project_reservation_room, extra, removed, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			project_reservation_serviceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_service
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param service_index Numero de Servicio
	 * @param service_code Codigo del Servicio en origen
	 * @param item Identificador del Servicio
	 * @param description Descripcion
	 * @param meal_plan Regimen
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param extra Indica si se trata de un Servicio extra
	 * @param removed Servicio borrado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_service(Integer domain, Integer project_reservation, Short service_index, String service_code, Integer item, String description, String meal_plan, Integer project_reservation_room, Boolean extra, Boolean removed, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProject_reservation_service(domain, project_reservation, service_index, service_code, item, description, meal_plan, project_reservation_room, extra, removed, creation_user, creation_date, modification_user, modification_date);
		project_reservation_serviceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> warehouse_transferDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWarehouse_transferPk(Integer id){
		return warehouse_transferDomains.get(id);
	}
	
	/**
	 * Warehouse_transfer
	 * @param inventory Identificador del Inventario
	 * @param source_warehouse Identificador del Almacen Origen
	 * @param target_warehouse Identificador del Almacen Destino
	 * @returns domain's ID
	*/
	protected Integer getDomainForWarehouse_transfer( Integer inventory , Integer source_warehouse , Integer target_warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForInventoryPk( inventory ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( source_warehouse ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( target_warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Warehouse_transfer
	 * @param id Identificador unico
	 * @param series Serie del Traspaso
	 * @param number Numero del Traspaso
	 * @param issue_time Fecha de emision del Traspaso
	 * @param comments Comentarios del Traspaso
	 * @param source_warehouse Identificador del Almacen Origen
	 * @param target_warehouse Identificador del Almacen Destino
	 * @param inventory Identificador del Inventario
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse_transfer(String series, Integer number, Timestamp issue_time, String comments, Integer source_warehouse, Integer target_warehouse, Integer inventory, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForWarehouse_transfer( inventory , source_warehouse , target_warehouse);
		Integer id =  super.insertWarehouse_transfer( domain != null ? domain : getDefaultDomain(), series, number, issue_time, comments, source_warehouse, target_warehouse, inventory, source, source_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			warehouse_transferDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Warehouse_transfer
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param series Serie del Traspaso
	 * @param number Numero del Traspaso
	 * @param issue_time Fecha de emision del Traspaso
	 * @param comments Comentarios del Traspaso
	 * @param source_warehouse Identificador del Almacen Origen
	 * @param target_warehouse Identificador del Almacen Destino
	 * @param inventory Identificador del Inventario
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWarehouse_transfer(Integer domain, String series, Integer number, Timestamp issue_time, String comments, Integer source_warehouse, Integer target_warehouse, Integer inventory, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertWarehouse_transfer(domain, series, number, issue_time, comments, source_warehouse, target_warehouse, inventory, source, source_id, creation_user, creation_date, modification_user, modification_date);
		warehouse_transferDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> record_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRecord_dataPk(Integer id){
		return record_dataDomains.get(id);
	}
	
	/**
	 * Record_data
	 * @param attach Archivo adjunto
	 * @param registry Registro de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRecord_data( Integer attach , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRattachPk( attach ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Record_data
	 * @param id Identificador unico del Dato Registral
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
	public int insertRecord_data(Integer registry, Date creation_date, String description, String notary, String number, Date record_date, String volume, String section, String page, String sheet, String registration, Integer attach)
	throws SQLException {
		Integer domain = getDomainForRecord_data( attach , registry);
		Integer id =  super.insertRecord_data( domain != null ? domain : getDefaultDomain(), registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach );
		if ( domain != null ) { 
			record_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Record_data
	 * @param id Identificador unico del Dato Registral
	 * @param domain Identificador del Dominio
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
	public int insertRecord_data(Integer domain, Integer registry, Date creation_date, String description, String notary, String number, Date record_date, String volume, String section, String page, String sheet, String registration, Integer attach)
	throws SQLException {
		Integer id =  super.insertRecord_data(domain, registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach);
		record_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_service_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_service_detailPk(Integer id){
		return project_reservation_service_detailDomains.get(id);
	}
	
	/**
	 * Project_reservation_service_detail
	 * @param project_reservation_room_detail Identificador del Detalle de Habitacion de la Reserva
	 * @param project_reservation_service Identificador del Servicio de la Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_service_detail( Integer project_reservation_room_detail , Integer project_reservation_service){
		Integer domain = null;
			if ( ( domain = getDomainForProject_reservation_room_detailPk( project_reservation_room_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservation_servicePk( project_reservation_service ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_service_detail
	 * @param id Identificador unico
	 * @param project_reservation_service Identificador del Servicio de la Reserva
	 * @param project_reservation_room_detail Identificador del Detalle de Habitacion de la Reserva
	 * @param effective_date Fecha de efecto
	 * @param quantity Cantidad
	 * @param price Precio
	 * @param taxable_base Base imponible
	 * @param taxable_base_production Base imponible de Produccion
	 * @param total_production Total Produccion acumulado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_service_detail(Integer project_reservation_service, Integer project_reservation_room_detail, Date effective_date, Double quantity, Double price, Double taxable_base, Double taxable_base_production, Double total_production)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_service_detail( project_reservation_room_detail , project_reservation_service);
		Integer id =  super.insertProject_reservation_service_detail( domain != null ? domain : getDefaultDomain(), project_reservation_service, project_reservation_room_detail, effective_date, quantity, price, taxable_base, taxable_base_production, total_production );
		if ( domain != null ) { 
			project_reservation_service_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_service_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation_service Identificador del Servicio de la Reserva
	 * @param project_reservation_room_detail Identificador del Detalle de Habitacion de la Reserva
	 * @param effective_date Fecha de efecto
	 * @param quantity Cantidad
	 * @param price Precio
	 * @param taxable_base Base imponible
	 * @param taxable_base_production Base imponible de Produccion
	 * @param total_production Total Produccion acumulado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_service_detail(Integer domain, Integer project_reservation_service, Integer project_reservation_room_detail, Date effective_date, Double quantity, Double price, Double taxable_base, Double taxable_base_production, Double total_production)
	throws SQLException {
		Integer id =  super.insertProject_reservation_service_detail(domain, project_reservation_service, project_reservation_room_detail, effective_date, quantity, price, taxable_base, taxable_base_production, total_production);
		project_reservation_service_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commission_itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommission_itemPk(Integer id){
		return commission_itemDomains.get(id);
	}
	
	/**
	 * Commission_item
	 * @param commission Identificador de la Comision
	 * @param item Identificador del Articulo
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommission_item( Integer commission , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForCommissionPk( commission ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Commission_item
	 * @param id Identificador unico
	 * @param commission Identificador de la Comision
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param amount Importe de la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_item(Integer commission, Integer item, Double quantity, Double amount, Double rate)
	throws SQLException {
		Integer domain = getDomainForCommission_item( commission , item);
		Integer id =  super.insertCommission_item( domain != null ? domain : getDefaultDomain(), commission, item, quantity, amount, rate );
		if ( domain != null ) { 
			commission_itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commission_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param commission Identificador de la Comision
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param amount Importe de la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_item(Integer domain, Integer commission, Integer item, Double quantity, Double amount, Double rate)
	throws SQLException {
		Integer id =  super.insertCommission_item(domain, commission, item, quantity, amount, rate);
		commission_itemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_mod347_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_mod347_detailPk(Integer id){
		return fs_mod347_detailDomains.get(id);
	}
	
	/**
	 * Fs_mod347_detail
	 * @param fs_mod347 Identificador de la Declaracion
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_mod347_detail( Integer fs_mod347){
		Integer domain = null;
			if ( ( domain = getDomainForFs_mod347Pk( fs_mod347 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_mod347_detail
	 * @param id Identificador unico
	 * @param fs_mod347 Identificador de la Declaracion
	 * @param sheet Tipo de hoja (Declarado o Inmueble)
	 * @param type Clave de operacion
	 * @param document NIF del declarado
	 * @param representative_document NIF Representante Legal
	 * @param registry Identificador del Declarado
	 * @param name Apellidos  y Nombre del declarado
	 * @param province Provincia del declarado
	 * @param country Pais del declarado
	 * @param amount Importe de las operaciones
	 * @param first_quarter_amount Importe de las operaciones primer trimestre
	 * @param second_quarter_amount Importe de las operaciones segundo trimestre
	 * @param third_quarter_amount Importe de las operaciones tercer trimestre
	 * @param fourth_quarter_amount Importe de las operaciones cuarto trimestre
	 * @param asset_amount Importe de las operaciones de inmuebles
	 * @param asset_first_quarter_amount Importe de las operaciones de inmuebles del primer trimestre
	 * @param asset_second_quarter_amount Importe de las operaciones de inmuebles del segundo trimestre
	 * @param asset_third_quarter_amount Importe de las operaciones de inmuebles del tercer trimestre
	 * @param asset_fourth_quarter_amount Importe de las operaciones de inmuebles del cuarto trimestre
	 * @param cash_amount Importe de las operaciones en metalico
	 * @param cash_year Ejercicio en el que se hubieran declarado las operaciones en metalico.
	 * @param insurance_operation Las Entidades Aseguradoras maracaran este campo para identificar las operaciones de seguros
	 * @param business_premise_rental Se marcara este campo para operaciones de arrendamiento de locales de negocio,
	 * @param asset_location Situacion del inmueble
	 * @param cadasdral_reference Refercia catastral
	 * @param asset_street_type Tipo de via
	 * @param asset_street Nombre de la via
	 * @param asset_street_number_type Tipo de numero de via
	 * @param asset_street_number Numero de via
	 * @param asset_street_number_suffix Calificador del numero de via
	 * @param asset_street_block Direccion. Bloque.
	 * @param asset_street_hall Direccion. Portal.
	 * @param asset_street_stair Direccion. Escalera.
	 * @param asset_street_floor Direccion. Planta.
	 * @param asset_street_door Direccion. Puerta.
	 * @param asset_street_complement Direccion. Complemento.
	 * @param asset_street_city Direccion. Complemento.
	 * @param asset_street_town Direccion. Complemento.
	 * @param asset_street_town_code Direccion. Complemento.
	 * @param asset_street_province Direccion. Complemento.
	 * @param asset_street_zip Direccion. Complemento.
	 * @param operator_nif NIF del operador intracomunitario
	 * @param vat_accrual Regimen de critrio de caja
	 * @param isp Inversion de sujeto pasivo
	 * @param deposit_regime Regimen de deposito
	 * @param vat_accrual_amount Importe de las operaciones en reg, caja
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod347_detail(Integer fs_mod347, String sheet, String type, String document, String representative_document, Integer registry, String name, Integer province, String country, Double amount, Double first_quarter_amount, Double second_quarter_amount, Double third_quarter_amount, Double fourth_quarter_amount, Double asset_amount, Double asset_first_quarter_amount, Double asset_second_quarter_amount, Double asset_third_quarter_amount, Double asset_fourth_quarter_amount, Double cash_amount, Integer cash_year, Boolean insurance_operation, Boolean business_premise_rental, String asset_location, String cadasdral_reference, String asset_street_type, String asset_street, String asset_street_number_type, String asset_street_number, String asset_street_number_suffix, String asset_street_block, String asset_street_hall, String asset_street_stair, String asset_street_floor, String asset_street_door, String asset_street_complement, String asset_street_city, String asset_street_town, String asset_street_town_code, String asset_street_province, String asset_street_zip, String operator_nif, Boolean vat_accrual, Boolean isp, Boolean deposit_regime, Double vat_accrual_amount)
	throws SQLException {
		Integer domain = getDomainForFs_mod347_detail( fs_mod347);
		Integer id =  super.insertFs_mod347_detail( domain != null ? domain : getDefaultDomain(), fs_mod347, sheet, type, document, representative_document, registry, name, province, country, amount, first_quarter_amount, second_quarter_amount, third_quarter_amount, fourth_quarter_amount, asset_amount, asset_first_quarter_amount, asset_second_quarter_amount, asset_third_quarter_amount, asset_fourth_quarter_amount, cash_amount, cash_year, insurance_operation, business_premise_rental, asset_location, cadasdral_reference, asset_street_type, asset_street, asset_street_number_type, asset_street_number, asset_street_number_suffix, asset_street_block, asset_street_hall, asset_street_stair, asset_street_floor, asset_street_door, asset_street_complement, asset_street_city, asset_street_town, asset_street_town_code, asset_street_province, asset_street_zip, operator_nif, vat_accrual, isp, deposit_regime, vat_accrual_amount );
		if ( domain != null ) { 
			fs_mod347_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_mod347_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_mod347 Identificador de la Declaracion
	 * @param sheet Tipo de hoja (Declarado o Inmueble)
	 * @param type Clave de operacion
	 * @param document NIF del declarado
	 * @param representative_document NIF Representante Legal
	 * @param registry Identificador del Declarado
	 * @param name Apellidos  y Nombre del declarado
	 * @param province Provincia del declarado
	 * @param country Pais del declarado
	 * @param amount Importe de las operaciones
	 * @param first_quarter_amount Importe de las operaciones primer trimestre
	 * @param second_quarter_amount Importe de las operaciones segundo trimestre
	 * @param third_quarter_amount Importe de las operaciones tercer trimestre
	 * @param fourth_quarter_amount Importe de las operaciones cuarto trimestre
	 * @param asset_amount Importe de las operaciones de inmuebles
	 * @param asset_first_quarter_amount Importe de las operaciones de inmuebles del primer trimestre
	 * @param asset_second_quarter_amount Importe de las operaciones de inmuebles del segundo trimestre
	 * @param asset_third_quarter_amount Importe de las operaciones de inmuebles del tercer trimestre
	 * @param asset_fourth_quarter_amount Importe de las operaciones de inmuebles del cuarto trimestre
	 * @param cash_amount Importe de las operaciones en metalico
	 * @param cash_year Ejercicio en el que se hubieran declarado las operaciones en metalico.
	 * @param insurance_operation Las Entidades Aseguradoras maracaran este campo para identificar las operaciones de seguros
	 * @param business_premise_rental Se marcara este campo para operaciones de arrendamiento de locales de negocio,
	 * @param asset_location Situacion del inmueble
	 * @param cadasdral_reference Refercia catastral
	 * @param asset_street_type Tipo de via
	 * @param asset_street Nombre de la via
	 * @param asset_street_number_type Tipo de numero de via
	 * @param asset_street_number Numero de via
	 * @param asset_street_number_suffix Calificador del numero de via
	 * @param asset_street_block Direccion. Bloque.
	 * @param asset_street_hall Direccion. Portal.
	 * @param asset_street_stair Direccion. Escalera.
	 * @param asset_street_floor Direccion. Planta.
	 * @param asset_street_door Direccion. Puerta.
	 * @param asset_street_complement Direccion. Complemento.
	 * @param asset_street_city Direccion. Complemento.
	 * @param asset_street_town Direccion. Complemento.
	 * @param asset_street_town_code Direccion. Complemento.
	 * @param asset_street_province Direccion. Complemento.
	 * @param asset_street_zip Direccion. Complemento.
	 * @param operator_nif NIF del operador intracomunitario
	 * @param vat_accrual Regimen de critrio de caja
	 * @param isp Inversion de sujeto pasivo
	 * @param deposit_regime Regimen de deposito
	 * @param vat_accrual_amount Importe de las operaciones en reg, caja
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_mod347_detail(Integer domain, Integer fs_mod347, String sheet, String type, String document, String representative_document, Integer registry, String name, Integer province, String country, Double amount, Double first_quarter_amount, Double second_quarter_amount, Double third_quarter_amount, Double fourth_quarter_amount, Double asset_amount, Double asset_first_quarter_amount, Double asset_second_quarter_amount, Double asset_third_quarter_amount, Double asset_fourth_quarter_amount, Double cash_amount, Integer cash_year, Boolean insurance_operation, Boolean business_premise_rental, String asset_location, String cadasdral_reference, String asset_street_type, String asset_street, String asset_street_number_type, String asset_street_number, String asset_street_number_suffix, String asset_street_block, String asset_street_hall, String asset_street_stair, String asset_street_floor, String asset_street_door, String asset_street_complement, String asset_street_city, String asset_street_town, String asset_street_town_code, String asset_street_province, String asset_street_zip, String operator_nif, Boolean vat_accrual, Boolean isp, Boolean deposit_regime, Double vat_accrual_amount)
	throws SQLException {
		Integer id =  super.insertFs_mod347_detail(domain, fs_mod347, sheet, type, document, representative_document, registry, name, province, country, amount, first_quarter_amount, second_quarter_amount, third_quarter_amount, fourth_quarter_amount, asset_amount, asset_first_quarter_amount, asset_second_quarter_amount, asset_third_quarter_amount, asset_fourth_quarter_amount, cash_amount, cash_year, insurance_operation, business_premise_rental, asset_location, cadasdral_reference, asset_street_type, asset_street, asset_street_number_type, asset_street_number, asset_street_number_suffix, asset_street_block, asset_street_hall, asset_street_stair, asset_street_floor, asset_street_door, asset_street_complement, asset_street_city, asset_street_town, asset_street_town_code, asset_street_province, asset_street_zip, operator_nif, vat_accrual, isp, deposit_regime, vat_accrual_amount);
		fs_mod347_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> catalogueDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCataloguePk(Integer id){
		return catalogueDomains.get(id);
	}
	
	/**
	 * Catalogue
	 * @returns domain's ID
	*/
	protected Integer getDomainForCatalogue(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Catalogue
	 * @param id Identificador unico
	 * @param name Nombre del Catalogo
	 * @param purchase Indica si se trata de un Catalogo de Compras o Ventas
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue(String name, Boolean purchase, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForCatalogue();
		Integer id =  super.insertCatalogue( domain != null ? domain : getDefaultDomain(), name, purchase, start_date, end_date );
		if ( domain != null ) { 
			catalogueDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Catalogue
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Catalogo
	 * @param purchase Indica si se trata de un Catalogo de Compras o Ventas
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue(Integer domain, String name, Boolean purchase, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertCatalogue(domain, name, purchase, start_date, end_date);
		catalogueDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreement_levelDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_levelPk(Integer id){
		return agreement_levelDomains.get(id);
	}
	
	/**
	 * Agreement_level
	 * @param agreement Convenio
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_level( Integer agreement){
		Integer domain = null;
			if ( ( domain = getDomainForAgreementPk( agreement ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_level
	 * @param id Identificador unico
	 * @param agreement Convenio
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level(Integer agreement, String description)
	throws SQLException {
		Integer domain = getDomainForAgreement_level( agreement);
		Integer id =  super.insertAgreement_level( domain != null ? domain : getDefaultDomain(), agreement, description );
		if ( domain != null ) { 
			agreement_levelDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_level
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param agreement Convenio
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level(Integer domain, Integer agreement, String description)
	throws SQLException {
		Integer id =  super.insertAgreement_level(domain, agreement, description);
		agreement_levelDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_instructorDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_instructorPk(Integer id){
		return course_instructorDomains.get(id);
	}
	
	/**
	 * Course_instructor
	 * @param course Identificador del Curso
	 * @param task_holder Identificador del Profesor
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_instructor( Integer course , Integer task_holder){
		Integer domain = null;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
			if ( ( domain = getDomainForTask_holderPk( task_holder ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_instructor
	 * @param id Identificador unico
	 * @param course Identificador del Curso
	 * @param task_holder Identificador del Profesor
	 * @param type Tipo de Profesor
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_instructor(Integer course, Integer task_holder, Short type)
	throws SQLException {
		Integer domain = getDomainForCourse_instructor( course , task_holder);
		Integer id =  super.insertCourse_instructor( domain != null ? domain : getDefaultDomain(), course, task_holder, type );
		if ( domain != null ) { 
			course_instructorDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_instructor
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param course Identificador del Curso
	 * @param task_holder Identificador del Profesor
	 * @param type Tipo de Profesor
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_instructor(Integer domain, Integer course, Integer task_holder, Short type)
	throws SQLException {
		Integer id =  super.insertCourse_instructor(domain, course, task_holder, type);
		course_instructorDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> offer_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForOffer_attachPk(Integer id){
		return offer_attachDomains.get(id);
	}
	
	/**
	 * Offer_attach
	 * @param offer Identificador del Presupuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForOffer_attach( Integer offer){
		Integer domain = null;
			if ( ( domain = getDomainForOfferPk( offer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Offer_attach
	 * @param id Identificador unico
	 * @param offer Identificador del Presupuesto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_attach(Integer offer, Short mimeType, String description, Blob data, String driveId)
	throws SQLException {
		Integer domain = getDomainForOffer_attach( offer);
		Integer id =  super.insertOffer_attach( domain != null ? domain : getDefaultDomain(), offer, mimeType, description, data, driveId );
		if ( domain != null ) { 
			offer_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Offer_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param offer Identificador del Presupuesto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_attach(Integer domain, Integer offer, Short mimeType, String description, Blob data, String driveId)
	throws SQLException {
		Integer id =  super.insertOffer_attach(domain, offer, mimeType, description, data, driveId);
		offer_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> campaignDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCampaignPk(Integer id){
		return campaignDomains.get(id);
	}
	
	/**
	 * Campaign
	 * @param campaign_type Identificador del Tipo de CampaÒa
	 * @param process Identificador del Proceso
	 * @param workgroup Grupo de Trabajo supervisor de la CampaÒa
	 * @returns domain's ID
	*/
	protected Integer getDomainForCampaign( Integer campaign_type , Integer process , Integer workgroup){
		Integer domain = null;
			if ( ( domain = getDomainForCampaign_typePk( campaign_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForProcessPk( process ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( workgroup ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Campaign
	 * @param id Identificador unico de la CampaÒa
	 * @param campaign_type Identificador del Tipo de CampaÒa
	 * @param description Descripcion de la CampaÒa
	 * @param process Identificador del Proceso
	 * @param start_date Fecha de inicio de la CampaÒa
	 * @param end_date Fecha de finalizacion de la CampaÒa
	 * @param workgroup Grupo de Trabajo supervisor de la CampaÒa
	 * @param manual Tipo de CampaÒa
	 * @param status Estado de la CampaÒa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign(Integer campaign_type, String description, Integer process, Date start_date, Date end_date, Integer workgroup, Boolean manual, Short status)
	throws SQLException {
		Integer domain = getDomainForCampaign( campaign_type , process , workgroup);
		Integer id =  super.insertCampaign( domain != null ? domain : getDefaultDomain(), campaign_type, description, process, start_date, end_date, workgroup, manual, status );
		if ( domain != null ) { 
			campaignDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Campaign
	 * @param id Identificador unico de la CampaÒa
	 * @param domain Identificador del Dominio
	 * @param campaign_type Identificador del Tipo de CampaÒa
	 * @param description Descripcion de la CampaÒa
	 * @param process Identificador del Proceso
	 * @param start_date Fecha de inicio de la CampaÒa
	 * @param end_date Fecha de finalizacion de la CampaÒa
	 * @param workgroup Grupo de Trabajo supervisor de la CampaÒa
	 * @param manual Tipo de CampaÒa
	 * @param status Estado de la CampaÒa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign(Integer domain, Integer campaign_type, String description, Integer process, Date start_date, Date end_date, Integer workgroup, Boolean manual, Short status)
	throws SQLException {
		Integer id =  super.insertCampaign(domain, campaign_type, description, process, start_date, end_date, workgroup, manual, status);
		campaignDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> incomeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIncomePk(Integer id){
		return incomeDomains.get(id);
	}
	
	/**
	 * Income
	 * @param pay_method Identificador de la Forma de Pago
	 * @param project Identificador del Proyecto
	 * @param address Identificador de la Direccion del Proveedor
	 * @param scope Ambito del Albaran
	 * @param supplier Identificador del Proveedor
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForIncome( Integer pay_method , Integer project , Integer address , Integer scope , Integer supplier , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForSupplierPk( supplier ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Income
	 * @param id Identificador unico del Albaran de Compra
	 * @param project Identificador del Proyecto
	 * @param reference_code Codigo de referencia del Albaran
	 * @param supplier Identificador del Proveedor
	 * @param address Identificador de la Direccion del Proveedor
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param comments Comentarios del Albaran
	 * @param remarks Observaciones del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIncome(Integer project, String reference_code, Integer supplier, Integer address, Date issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForIncome( pay_method , project , address , scope , supplier , workplace);
		Integer id =  super.insertIncome( domain != null ? domain : getDefaultDomain(), project, reference_code, supplier, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, carrier_packing, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			incomeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Income
	 * @param id Identificador unico del Albaran de Compra
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param reference_code Codigo de referencia del Albaran
	 * @param supplier Identificador del Proveedor
	 * @param address Identificador de la Direccion del Proveedor
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param comments Comentarios del Albaran
	 * @param remarks Observaciones del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIncome(Integer domain, Integer project, String reference_code, Integer supplier, Integer address, Date issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertIncome(domain, project, reference_code, supplier, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, carrier_packing, creation_user, creation_date, modification_user, modification_date);
		incomeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> cra_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCra_batch_detailPk(Integer id){
		return cra_batch_detailDomains.get(id);
	}
	
	/**
	 * Cra_batch_detail
	 * @param cra_batch Identificador unico de la Remesa
	 * @param enterprise_ccc Identificador unico del ccc
	 * @returns domain's ID
	*/
	protected Integer getDomainForCra_batch_detail( Integer cra_batch , Integer enterprise_ccc){
		Integer domain = null;
			if ( ( domain = getDomainForCra_batchPk( cra_batch ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprise_cccPk( enterprise_ccc ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Cra_batch_detail
	 * @param id Identificador unico
	 * @param cra_batch Identificador unico de la Remesa
	 * @param enterprise_ccc Identificador unico del ccc
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCra_batch_detail(Integer cra_batch, Integer enterprise_ccc)
	throws SQLException {
		Integer domain = getDomainForCra_batch_detail( cra_batch , enterprise_ccc);
		Integer id =  super.insertCra_batch_detail( domain != null ? domain : getDefaultDomain(), cra_batch, enterprise_ccc );
		if ( domain != null ) { 
			cra_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Cra_batch_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param cra_batch Identificador unico de la Remesa
	 * @param enterprise_ccc Identificador unico del ccc
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCra_batch_detail(Integer domain, Integer cra_batch, Integer enterprise_ccc)
	throws SQLException {
		Integer id =  super.insertCra_batch_detail(domain, cra_batch, enterprise_ccc);
		cra_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> profile_action_deniedDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProfile_action_deniedPk(Integer id){
		return profile_action_deniedDomains.get(id);
	}
	
	/**
	 * Profile_action_denied
	 * @param action_id Identificador de la Accion
	 * @param profile Identificador del Perfil
	 * @returns domain's ID
	*/
	protected Integer getDomainForProfile_action_denied( Integer action_id , Integer profile){
		Integer domain = null;
			if ( ( domain = getDomainForProfilePk( profile ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Profile_action_denied
	 * @param id Identificador unico
	 * @param profile Identificador del Perfil
	 * @param action_id Identificador de la Accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_action_denied(Integer profile, Integer action_id)
	throws SQLException {
		Integer domain = getDomainForProfile_action_denied( action_id , profile);
		Integer id =  super.insertProfile_action_denied( domain != null ? domain : getDefaultDomain(), profile, action_id );
		if ( domain != null ) { 
			profile_action_deniedDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Profile_action_denied
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param profile Identificador del Perfil
	 * @param action_id Identificador de la Accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_action_denied(Integer domain, Integer profile, Integer action_id)
	throws SQLException {
		Integer id =  super.insertProfile_action_denied(domain, profile, action_id);
		profile_action_deniedDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> loanDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForLoanPk(Integer id){
		return loanDomains.get(id);
	}
	
	/**
	 * Loan
	 * @param account Identificador de la Cuenta Contable
	 * @param rbank Banco por el que se paga el Prestamo
	 * @returns domain's ID
	*/
	protected Integer getDomainForLoan( Integer account , Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Loan
	 * @param id Identificador Unico
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
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLoan(String description, Date loan_date, String term, String interest, String review, Double amount, Double expenses, Integer rbank, Short security_level, Double fee_amount, Integer recurrence, Integer pay_day, Short status, Integer account)
	throws SQLException {
		Integer domain = getDomainForLoan( account , rbank);
		Integer id =  super.insertLoan( domain != null ? domain : getDefaultDomain(), description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status, account );
		if ( domain != null ) { 
			loanDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Loan
	 * @param id Identificador Unico
	 * @param domain Identificador del Dominio
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
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLoan(Integer domain, String description, Date loan_date, String term, String interest, String review, Double amount, Double expenses, Integer rbank, Short security_level, Double fee_amount, Integer recurrence, Integer pay_day, Short status, Integer account)
	throws SQLException {
		Integer id =  super.insertLoan(domain, description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status, account);
		loanDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pos_shift_countDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPos_shift_countPk(Integer id){
		return pos_shift_countDomains.get(id);
	}
	
	/**
	 * Pos_shift_count
	 * @param pay_method Identificador de la Forma de pago
	 * @param pos_shift Identificador del Turno de trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForPos_shift_count( Integer pay_method , Integer pos_shift){
		Integer domain = null;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForPos_shiftPk( pos_shift ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Pos_shift_count
	 * @param id Identificador unico
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param pay_method Identificador de la Forma de pago
	 * @param amount Total efectivo
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_shift_count(Integer pos_shift, Integer pay_method, Double amount, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForPos_shift_count( pay_method , pos_shift);
		Integer id =  super.insertPos_shift_count( domain != null ? domain : getDefaultDomain(), pos_shift, pay_method, amount, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			pos_shift_countDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pos_shift_count
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param pay_method Identificador de la Forma de pago
	 * @param amount Total efectivo
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPos_shift_count(Integer domain, Integer pos_shift, Integer pay_method, Double amount, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertPos_shift_count(domain, pos_shift, pay_method, amount, creation_user, creation_date, modification_user, modification_date);
		pos_shift_countDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> absenceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAbsencePk(Integer id){
		return absenceDomains.get(id);
	}
	
	/**
	 * Absence
	 * @param course_alumn Identificador del CursoAlumno
	 * @returns domain's ID
	*/
	protected Integer getDomainForAbsence( Integer course_alumn){
		Integer domain = null;
			if ( ( domain = getDomainForCourse_alumnPk( course_alumn ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Absence
	 * @param id Identificador unico
	 * @param course_alumn Identificador del CursoAlumno
	 * @param absence_date Fecha de la Ausencia
	 * @param comments Comentarios de la Ausencia
	 * @param evaluation Numero de Evaluacion en que se produjo la Ausencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAbsence(Integer course_alumn, Date absence_date, String comments, Short evaluation)
	throws SQLException {
		Integer domain = getDomainForAbsence( course_alumn);
		Integer id =  super.insertAbsence( domain != null ? domain : getDefaultDomain(), course_alumn, absence_date, comments, evaluation );
		if ( domain != null ) { 
			absenceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Absence
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param course_alumn Identificador del CursoAlumno
	 * @param absence_date Fecha de la Ausencia
	 * @param comments Comentarios de la Ausencia
	 * @param evaluation Numero de Evaluacion en que se produjo la Ausencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAbsence(Integer domain, Integer course_alumn, Date absence_date, String comments, Short evaluation)
	throws SQLException {
		Integer id =  super.insertAbsence(domain, course_alumn, absence_date, comments, evaluation);
		absenceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> sessionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSessionPk(Integer id){
		return sessionDomains.get(id);
	}
	
	/**
	 * Session
	 * @param application Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSession( Integer application , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Session
	 * @param id Identificador unico
	 * @param endDate Fecha de finalizacion
	 * @param remote_address IP remota
	 * @param remote_host Equipo remoto
	 * @param session_id Identificador web de la sesiÛn
	 * @param startDate Fecha de inicio
	 * @param application Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSession(Timestamp endDate, String remote_address, String remote_host, String session_id, Timestamp startDate, Integer application, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForSession( application , user_id);
		Integer id =  super.insertSession( domain != null ? domain : getDefaultDomain(), endDate, remote_address, remote_host, session_id, startDate, application, user_id );
		if ( domain != null ) { 
			sessionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Session
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param endDate Fecha de finalizacion
	 * @param remote_address IP remota
	 * @param remote_host Equipo remoto
	 * @param session_id Identificador web de la sesiÛn
	 * @param startDate Fecha de inicio
	 * @param application Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSession(Integer domain, Timestamp endDate, String remote_address, String remote_host, String session_id, Timestamp startDate, Integer application, Integer user_id)
	throws SQLException {
		Integer id =  super.insertSession(domain, endDate, remote_address, remote_host, session_id, startDate, application, user_id);
		sessionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> balance_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBalance_detailPk(Integer id){
		return balance_detailDomains.get(id);
	}
	
	/**
	 * Balance_detail
	 * @param balance Identificador del Balance
	 * @returns domain's ID
	*/
	protected Integer getDomainForBalance_detail( Integer balance){
		Integer domain = null;
			if ( ( domain = getDomainForBalancePk( balance ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Balance_detail
	 * @param id Identificador unico
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description DescripciÛn del detalle de balance
	 * @param accounts Cuentas separadas por comas, que forman el acumulado.
	 * @param sortKey Orden el que aparecera en el listado.
	 * @param notes Notas en el Balance
	 * @param title 
	 * @param internal_calculation Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla
	 * @param visible Si aparece o no en la impresion.
	 * @param zeroFlag Flag que se activa cuando la cuenta o cuentas tienen valor 0.
	 * @param creditNature Si es verdadero se hace una haber menos debe de las cuentas indicadas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBalance_detail(Integer balance, String code, String description, String accounts, Integer sortKey, String notes, Boolean title, Boolean internal_calculation, Boolean visible, Boolean zeroFlag, Boolean creditNature)
	throws SQLException {
		Integer domain = getDomainForBalance_detail( balance);
		Integer id =  super.insertBalance_detail( domain != null ? domain : getDefaultDomain(), balance, code, description, accounts, sortKey, notes, title, internal_calculation, visible, zeroFlag, creditNature );
		if ( domain != null ) { 
			balance_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Balance_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description DescripciÛn del detalle de balance
	 * @param accounts Cuentas separadas por comas, que forman el acumulado.
	 * @param sortKey Orden el que aparecera en el listado.
	 * @param notes Notas en el Balance
	 * @param title 
	 * @param internal_calculation Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla
	 * @param visible Si aparece o no en la impresion.
	 * @param zeroFlag Flag que se activa cuando la cuenta o cuentas tienen valor 0.
	 * @param creditNature Si es verdadero se hace una haber menos debe de las cuentas indicadas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBalance_detail(Integer domain, Integer balance, String code, String description, String accounts, Integer sortKey, String notes, Boolean title, Boolean internal_calculation, Boolean visible, Boolean zeroFlag, Boolean creditNature)
	throws SQLException {
		Integer id =  super.insertBalance_detail(domain, balance, code, description, accounts, sortKey, notes, title, internal_calculation, visible, zeroFlag, creditNature);
		balance_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model180Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model180Pk(Integer id){
		return fs_model180Domains.get(id);
	}
	
	/**
	 * Fs_model180
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model180( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model180
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param receipt_total Importe declarado
	 * @param retention_total Importe declarado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model180(Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double receipt_total, Double retention_total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_model180( enterprise);
		Integer id =  super.insertFs_model180( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, receipt_total, retention_total, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_model180Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model180
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param receipt_total Importe declarado
	 * @param retention_total Importe declarado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model180(Integer domain, Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double receipt_total, Double retention_total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_model180(domain, enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, receipt_total, retention_total, creation_user, creation_date, modification_user, modification_date);
		fs_model180Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_infoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_infoPk(Integer id){
		return contract_infoDomains.get(id);
	}
	
	/**
	 * Contract_info
	 * @param contract Identificador del Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_info( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_info
	 * @param id Identificador unico
	 * @param contract Identificador del Contrato
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_info(Integer contract, String name, String expression, Date start_date, Date end_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForContract_info( contract);
		Integer id =  super.insertContract_info( domain != null ? domain : getDefaultDomain(), contract, name, expression, start_date, end_date, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			contract_infoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_info
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del Contrato
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_info(Integer domain, Integer contract, String name, String expression, Date start_date, Date end_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertContract_info(domain, contract, name, expression, start_date, end_date, creation_user, creation_date, modification_user, modification_date);
		contract_infoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> inventoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInventoryPk(Integer id){
		return inventoryDomains.get(id);
	}
	
	/**
	 * Inventory
	 * @returns domain's ID
	*/
	protected Integer getDomainForInventory(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Inventory
	 * @param id Identificador unico del Inventario
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @param status Estado del Inventario
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInventory(Date inventory_date, Integer warehouse, String description, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForInventory();
		Integer id =  super.insertInventory( domain != null ? domain : getDefaultDomain(), inventory_date, warehouse, description, status, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			inventoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Inventory
	 * @param id Identificador unico del Inventario
	 * @param domain Identificador del Dominio
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @param status Estado del Inventario
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInventory(Integer domain, Date inventory_date, Integer warehouse, String description, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertInventory(domain, inventory_date, warehouse, description, status, creation_user, creation_date, modification_user, modification_date);
		inventoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model184Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model184Pk(Integer id){
		return fs_model184Domains.get(id);
	}
	
	/**
	 * Fs_model184
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model184( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model184
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param entity_type Tipo de entidad nacional
	 * @param main_activity Actividad principal
	 * @param foreign_entity_type Tipo de entidad extranjera
	 * @param foreign_object Objeto
	 * @param country Pais
	 * @param resident_percent Porcentaje de renta atrib. a miembros residentes
	 * @param tax_is Tributacion en regimen del Impuesto sobre Sociedades
	 * @param net_sales_amount Importe neto cifra de negocios
	 * @param lrdocument NIF Representante
	 * @param lrname Nombre Representante
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model184(Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, String entity_type, String main_activity, String foreign_entity_type, String foreign_object, String country, Double resident_percent, Boolean tax_is, Double net_sales_amount, String lrdocument, String lrname, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_model184( enterprise);
		Integer id =  super.insertFs_model184( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, entity_type, main_activity, foreign_entity_type, foreign_object, country, resident_percent, tax_is, net_sales_amount, lrdocument, lrname, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_model184Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model184
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param entity_type Tipo de entidad nacional
	 * @param main_activity Actividad principal
	 * @param foreign_entity_type Tipo de entidad extranjera
	 * @param foreign_object Objeto
	 * @param country Pais
	 * @param resident_percent Porcentaje de renta atrib. a miembros residentes
	 * @param tax_is Tributacion en regimen del Impuesto sobre Sociedades
	 * @param net_sales_amount Importe neto cifra de negocios
	 * @param lrdocument NIF Representante
	 * @param lrname Nombre Representante
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model184(Integer domain, Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, String entity_type, String main_activity, String foreign_entity_type, String foreign_object, String country, Double resident_percent, Boolean tax_is, Double net_sales_amount, String lrdocument, String lrname, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_model184(domain, enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, entity_type, main_activity, foreign_entity_type, foreign_object, country, resident_percent, tax_is, net_sales_amount, lrdocument, lrname, creation_user, creation_date, modification_user, modification_date);
		fs_model184Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> web_info_styleDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWeb_info_stylePk(Integer id){
		return web_info_styleDomains.get(id);
	}
	
	/**
	 * Web_info_style
	 * @returns domain's ID
	*/
	protected Integer getDomainForWeb_info_style(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Web_info_style
	 * @param id Codigo del Estilo de la Pagina
	 * @param variable Nombre de la variable del Estilo
	 * @param value Valor de la variable del Estilo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_style(String variable, String value)
	throws SQLException {
		Integer domain = getDomainForWeb_info_style();
		Integer id =  super.insertWeb_info_style( domain != null ? domain : getDefaultDomain(), variable, value );
		if ( domain != null ) { 
			web_info_styleDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Web_info_style
	 * @param id Codigo del Estilo de la Pagina
	 * @param domain Identificador del Dominio
	 * @param variable Nombre de la variable del Estilo
	 * @param value Valor de la variable del Estilo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_style(Integer domain, String variable, String value)
	throws SQLException {
		Integer id =  super.insertWeb_info_style(domain, variable, value);
		web_info_styleDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> cashflow_forecastDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCashflow_forecastPk(Integer id){
		return cashflow_forecastDomains.get(id);
	}
	
	/**
	 * Cashflow_forecast
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @returns domain's ID
	*/
	protected Integer getDomainForCashflow_forecast( Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Cashflow_forecast
	 * @param id Identificador unico
	 * @param payment Indica si es un pago o un cobro
	 * @param description Descripcion
	 * @param start_date Fecha de inicio de aplicacion
	 * @param due_date Fecha final de aplicacion
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @param amount Importe
	 * @param payment_day Dia de pago
	 * @param january Aplicable en enero
	 * @param february Aplicable en febrero
	 * @param march Aplicable en marzo
	 * @param april Aplicable en abril
	 * @param may Aplicable en mayo
	 * @param june Aplicable en junio
	 * @param july Aplicable en julio
	 * @param august Aplicable en agosto
	 * @param september Aplicable en septiembre
	 * @param october Aplicable en octubre
	 * @param november Aplicable en noviembre
	 * @param december Aplicable en diciembre
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCashflow_forecast(Boolean payment, String description, Date start_date, Date due_date, Integer rbank, Double amount, Double payment_day, Boolean january, Boolean february, Boolean march, Boolean april, Boolean may, Boolean june, Boolean july, Boolean august, Boolean september, Boolean october, Boolean november, Boolean december)
	throws SQLException {
		Integer domain = getDomainForCashflow_forecast( rbank);
		Integer id =  super.insertCashflow_forecast( domain != null ? domain : getDefaultDomain(), payment, description, start_date, due_date, rbank, amount, payment_day, january, february, march, april, may, june, july, august, september, october, november, december );
		if ( domain != null ) { 
			cashflow_forecastDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Cashflow_forecast
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param payment Indica si es un pago o un cobro
	 * @param description Descripcion
	 * @param start_date Fecha de inicio de aplicacion
	 * @param due_date Fecha final de aplicacion
	 * @param rbank Identificador de Banco de la CompaÒia
	 * @param amount Importe
	 * @param payment_day Dia de pago
	 * @param january Aplicable en enero
	 * @param february Aplicable en febrero
	 * @param march Aplicable en marzo
	 * @param april Aplicable en abril
	 * @param may Aplicable en mayo
	 * @param june Aplicable en junio
	 * @param july Aplicable en julio
	 * @param august Aplicable en agosto
	 * @param september Aplicable en septiembre
	 * @param october Aplicable en octubre
	 * @param november Aplicable en noviembre
	 * @param december Aplicable en diciembre
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCashflow_forecast(Integer domain, Boolean payment, String description, Date start_date, Date due_date, Integer rbank, Double amount, Double payment_day, Boolean january, Boolean february, Boolean march, Boolean april, Boolean may, Boolean june, Boolean july, Boolean august, Boolean september, Boolean october, Boolean november, Boolean december)
	throws SQLException {
		Integer id =  super.insertCashflow_forecast(domain, payment, description, start_date, due_date, rbank, amount, payment_day, january, february, march, april, may, june, july, august, september, october, november, december);
		cashflow_forecastDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model200_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model200_detailPk(Integer id){
		return fs_model200_detailDomains.get(id);
	}
	
	/**
	 * Fs_model200_detail
	 * @param fs_model200 Identificador del modelo 200
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model200_detail( Integer fs_model200){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model200Pk( fs_model200 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model200_detail
	 * @param id Identificador unico
	 * @param fs_model200 Identificador del modelo 200
	 * @param key Clave Casilla
	 * @param value Valor de la casilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200_detail(Integer fs_model200, String key, Double value)
	throws SQLException {
		Integer domain = getDomainForFs_model200_detail( fs_model200);
		Integer id =  super.insertFs_model200_detail( domain != null ? domain : getDefaultDomain(), fs_model200, key, value );
		if ( domain != null ) { 
			fs_model200_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model200_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model200 Identificador del modelo 200
	 * @param key Clave Casilla
	 * @param value Valor de la casilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200_detail(Integer domain, Integer fs_model200, String key, Double value)
	throws SQLException {
		Integer id =  super.insertFs_model200_detail(domain, fs_model200, key, value);
		fs_model200_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_commercialDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_commercialPk(Integer project){
		return project_commercialDomains.get(project);
	}
	
	/**
	 * Project_commercial
	 * @param project Identificador del Proyecto
	 * @param seller Identificador del Comercial
	 * @param target Identificador del Cliente Potencial
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_commercial( Integer project , Integer seller , Integer target){
		Integer domain = null;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
			if ( ( domain = getDomainForTargetPk( target ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_commercial
	 * @param project Identificador del Proyecto
	 * @param target Identificador del Cliente Potencial
	 * @param seller Identificador del Comercial
	 * @param comments Comentarios
	 * @param source Origen del Proyecto
	 * @param status Estado del Proyecto
	 * @param status_date Fecha del Estado del Proyecto
	 * @param probability Probabilidad del Proyecto
	 * @throws SQLException
	*/
	public void insertProject_commercial(Integer project, Integer target, Integer seller, String comments, Short source, Short status, Date status_date, Integer probability)
	throws SQLException {
		Integer domain = getDomainForProject_commercial( project , seller , target);
		 super.insertProject_commercial( project, domain != null ? domain : getDefaultDomain(), target, seller, comments, source, status, status_date, probability );
		if ( domain != null ) { 
			project_commercialDomains.put(project, domain);
		}
	}

	/**
	 * Project_commercial
	 * @param project Identificador del Proyecto
	 * @param domain Identificador del Dominio
	 * @param target Identificador del Cliente Potencial
	 * @param seller Identificador del Comercial
	 * @param comments Comentarios
	 * @param source Origen del Proyecto
	 * @param status Estado del Proyecto
	 * @param status_date Fecha del Estado del Proyecto
	 * @param probability Probabilidad del Proyecto
	 * @throws SQLException
	*/
	public void insertProject_commercial(Integer project, Integer domain, Integer target, Integer seller, String comments, Short source, Short status, Date status_date, Integer probability)
	throws SQLException {
		 super.insertProject_commercial(project, domain, target, seller, comments, source, status, status_date, probability);
		project_commercialDomains.put(project, domain );
			}

	
	private Map<Integer,Integer> salary_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_dataPk(Integer id){
		return salary_dataDomains.get(id);
	}
	
	/**
	 * Salary_data
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_data( Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param expression Importe
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param salary Recibo del pago de salarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_data(String name, String expression, Date start_date, Date end_date, Integer salary)
	throws SQLException {
		Integer domain = getDomainForSalary_data( salary);
		Integer id =  super.insertSalary_data( domain != null ? domain : getDefaultDomain(), name, expression, start_date, end_date, salary );
		if ( domain != null ) { 
			salary_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param expression Importe
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param salary Recibo del pago de salarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_data(Integer domain, String name, String expression, Date start_date, Date end_date, Integer salary)
	throws SQLException {
		Integer id =  super.insertSalary_data(domain, name, expression, start_date, end_date, salary);
		salary_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_paymentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_paymentPk(Integer id){
		return contract_paymentDomains.get(id);
	}
	
	/**
	 * Contract_payment
	 * @param contract Contrato
	 * @param payment_concept Identificador unico del concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_payment( Integer contract , Integer payment_concept){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForPayment_conceptPk( payment_concept ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_payment
	 * @param id Identificador unico
	 * @param type Tipo de PercepciÛn Salarial
	 * @param contract Contrato
	 * @param payment_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression Importe
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @param start_date Fecha de inicio 
	 * @param month Mes de la percepcion
	 * @param end_date Fecha de finalizacion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_payment(Short type, Integer contract, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		Integer domain = getDomainForContract_payment( contract , payment_concept);
		Integer id =  super.insertContract_payment( domain != null ? domain : getDefaultDomain(), type, contract, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type );
		if ( domain != null ) { 
			contract_paymentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de PercepciÛn Salarial
	 * @param contract Contrato
	 * @param payment_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression Importe
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @param start_date Fecha de inicio 
	 * @param month Mes de la percepcion
	 * @param end_date Fecha de finalizacion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_payment(Integer domain, Short type, Integer contract, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		Integer id =  super.insertContract_payment(domain, type, contract, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type);
		contract_paymentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> scopeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForScopePk(Integer id){
		return scopeDomains.get(id);
	}
	
	/**
	 * Scope
	 * @returns domain's ID
	*/
	protected Integer getDomainForScope(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Scope
	 * @param id Identificador unico
	 * @param description Descripcion del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertScope(String description)
	throws SQLException {
		Integer domain = getDomainForScope();
		Integer id =  super.insertScope( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			scopeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Scope
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertScope(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertScope(domain, description);
		scopeDomains.put(id, domain );
		return id;
	}

	
	private Map<String,Integer> domain_gserviceaccountDomains = new HashMap<String,Integer>();

	protected Integer getDomainForDomain_gserviceaccountPk(String client_id){
		return domain_gserviceaccountDomains.get(client_id);
	}
	
	/**
	 * Domain_gserviceaccount
	 * @returns domain's ID
	*/
	protected Integer getDomainForDomain_gserviceaccount(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Domain_gserviceaccount
	 * @param client_id 
	 * @param email_address 
	 * @param public_key 
	 * @param private_key 
	 * @param client_secret 
	 * @param size 
	 * @param limit 
	 * @param google_account 
	 * @throws SQLException
	*/
	public void insertDomain_gserviceaccount(String client_id, String email_address, String public_key, Blob private_key, Blob client_secret, Double size, Double limit, String google_account)
	throws SQLException {
		Integer domain = getDomainForDomain_gserviceaccount();
		 super.insertDomain_gserviceaccount( client_id, email_address, public_key, private_key, client_secret, domain != null ? domain : getDefaultDomain(), size, limit, google_account );
		if ( domain != null ) { 
			domain_gserviceaccountDomains.put(client_id, domain);
		}
	}

	/**
	 * Domain_gserviceaccount
	 * @param client_id 
	 * @param email_address 
	 * @param public_key 
	 * @param private_key 
	 * @param client_secret 
	 * @param domain 
	 * @param size 
	 * @param limit 
	 * @param google_account 
	 * @throws SQLException
	*/
	public void insertDomain_gserviceaccount(String client_id, String email_address, String public_key, Blob private_key, Blob client_secret, Integer domain, Double size, Double limit, String google_account)
	throws SQLException {
		 super.insertDomain_gserviceaccount(client_id, email_address, public_key, private_key, client_secret, domain, size, limit, google_account);
		domain_gserviceaccountDomains.put(client_id, domain );
			}

	
	private Map<Integer,Integer> fs_activity_infoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_activity_infoPk(Integer id){
		return fs_activity_infoDomains.get(id);
	}
	
	/**
	 * Fs_activity_info
	 * @param fs_activity Actividad fiscal
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_activity_info( Integer fs_activity){
		Integer domain = null;
			if ( ( domain = getDomainForFs_activityPk( fs_activity ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_activity_info
	 * @param id Identificador unico
	 * @param fs_activity Actividad fiscal
	 * @param info_key Clave de informacion
	 * @param line Numero de linea
	 * @param type Tipo de linea
	 * @param value Valor de la informacion
	 * @param factor Factor
	 * @param base Rendimiento neto
	 * @param unit Unidades
	 * @param min_value Valor minimo
	 * @param max_value Valor maximo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_activity_info(Integer fs_activity, String info_key, Integer line, Short type, String value, Double factor, Double base, String unit, Double min_value, Double max_value)
	throws SQLException {
		Integer domain = getDomainForFs_activity_info( fs_activity);
		Integer id =  super.insertFs_activity_info( domain != null ? domain : getDefaultDomain(), fs_activity, info_key, line, type, value, factor, base, unit, min_value, max_value );
		if ( domain != null ) { 
			fs_activity_infoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_activity_info
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_activity Actividad fiscal
	 * @param info_key Clave de informacion
	 * @param line Numero de linea
	 * @param type Tipo de linea
	 * @param value Valor de la informacion
	 * @param factor Factor
	 * @param base Rendimiento neto
	 * @param unit Unidades
	 * @param min_value Valor minimo
	 * @param max_value Valor maximo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_activity_info(Integer domain, Integer fs_activity, String info_key, Integer line, Short type, String value, Double factor, Double base, String unit, Double min_value, Double max_value)
	throws SQLException {
		Integer id =  super.insertFs_activity_info(domain, fs_activity, info_key, line, type, value, factor, base, unit, min_value, max_value);
		fs_activity_infoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> process_detail_transitionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProcess_detail_transitionPk(Integer id){
		return process_detail_transitionDomains.get(id);
	}
	
	/**
	 * Process_detail_transition
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @returns domain's ID
	*/
	protected Integer getDomainForProcess_detail_transition( Integer next_process_detail , Integer process_detail , Integer process_transition_type){
		Integer domain = null;
			if ( ( domain = getDomainForProcess_detailPk( next_process_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForProcess_detailPk( process_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForProcess_transition_typePk( process_transition_type ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Process_detail_transition
	 * @param id Identificador unico
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_detail_transition(Integer process_detail, Integer process_transition_type, Integer next_process_detail)
	throws SQLException {
		Integer domain = getDomainForProcess_detail_transition( next_process_detail , process_detail , process_transition_type);
		Integer id =  super.insertProcess_detail_transition( domain != null ? domain : getDefaultDomain(), process_detail, process_transition_type, next_process_detail );
		if ( domain != null ) { 
			process_detail_transitionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Process_detail_transition
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_detail_transition(Integer domain, Integer process_detail, Integer process_transition_type, Integer next_process_detail)
	throws SQLException {
		Integer id =  super.insertProcess_detail_transition(domain, process_detail, process_transition_type, next_process_detail);
		process_detail_transitionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rpaymethodDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRpaymethodPk(Integer id){
		return rpaymethodDomains.get(id);
	}
	
	/**
	 * Rpaymethod
	 * @param pay_method Identificador de la Forma de Pago
	 * @param rbank Identificador de la Entidad Bancaria
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRpaymethod( Integer pay_method , Integer rbank , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rpaymethod
	 * @param id Identificador unico de la Forma de Pago de la Persona o Empresa
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
	public int insertRpaymethod(Integer registry, Integer pay_method, Integer rbank, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days)
	throws SQLException {
		Integer domain = getDomainForRpaymethod( pay_method , rbank , registry);
		Integer id =  super.insertRpaymethod( domain != null ? domain : getDefaultDomain(), registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days );
		if ( domain != null ) { 
			rpaymethodDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rpaymethod
	 * @param id Identificador unico de la Forma de Pago de la Persona o Empresa
	 * @param domain Identificador del Dominio
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
	public int insertRpaymethod(Integer domain, Integer registry, Integer pay_method, Integer rbank, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days)
	throws SQLException {
		Integer id =  super.insertRpaymethod(domain, registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days);
		rpaymethodDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> companyDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCompanyPk(Integer registry){
		return companyDomains.get(registry);
	}
	
	/**
	 * Company
	 * @param registry Registro de la CompaÒia
	 * @returns domain's ID
	*/
	protected Integer getDomainForCompany( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Company
	 * @param registry Registro de la CompaÒia
	 * @param active Indica si la CompaÒia es activa o inactiva
	 * @param surcharge Indica si la CompaÒia tiene de recargo de equivalencia
	 * @param withholding Indica si la CompaÒia aplica retencion de impuestos
	 * @param vat_accrual_payment Indica si la CompaÒia esta acogida al Regimen Especial de Criterio de Caja
	 * @param e_invoice Indica si la CompaÒia desea emitir Facturas electronicas
	 * @throws SQLException
	*/
	public void insertCompany(Integer registry, Boolean active, Boolean surcharge, Boolean withholding, Boolean vat_accrual_payment, Boolean e_invoice)
	throws SQLException {
		Integer domain = getDomainForCompany( registry);
		 super.insertCompany( registry, domain != null ? domain : getDefaultDomain(), active, surcharge, withholding, vat_accrual_payment, e_invoice );
		if ( domain != null ) { 
			companyDomains.put(registry, domain);
		}
	}

	/**
	 * Company
	 * @param registry Registro de la CompaÒia
	 * @param domain Identificador del Dominio
	 * @param active Indica si la CompaÒia es activa o inactiva
	 * @param surcharge Indica si la CompaÒia tiene de recargo de equivalencia
	 * @param withholding Indica si la CompaÒia aplica retencion de impuestos
	 * @param vat_accrual_payment Indica si la CompaÒia esta acogida al Regimen Especial de Criterio de Caja
	 * @param e_invoice Indica si la CompaÒia desea emitir Facturas electronicas
	 * @throws SQLException
	*/
	public void insertCompany(Integer registry, Integer domain, Boolean active, Boolean surcharge, Boolean withholding, Boolean vat_accrual_payment, Boolean e_invoice)
	throws SQLException {
		 super.insertCompany(registry, domain, active, surcharge, withholding, vat_accrual_payment, e_invoice);
		companyDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> contrata_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContrata_batch_detailPk(Integer id){
		return contrata_batch_detailDomains.get(id);
	}
	
	/**
	 * Contrata_batch_detail
	 * @param contract Identificador del Contrato
	 * @param contrata_batch Identificador de la Remesa
	 * @returns domain's ID
	*/
	protected Integer getDomainForContrata_batch_detail( Integer contract , Integer contrata_batch){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForContrata_batchPk( contrata_batch ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contrata_batch_detail
	 * @param id Identificador unico
	 * @param contrata_batch Identificador de la Remesa
	 * @param contract Identificador del Contrato
	 * @param status Estado de la linea de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContrata_batch_detail(Integer contrata_batch, Integer contract, Short status)
	throws SQLException {
		Integer domain = getDomainForContrata_batch_detail( contract , contrata_batch);
		Integer id =  super.insertContrata_batch_detail( domain != null ? domain : getDefaultDomain(), contrata_batch, contract, status );
		if ( domain != null ) { 
			contrata_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contrata_batch_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contrata_batch Identificador de la Remesa
	 * @param contract Identificador del Contrato
	 * @param status Estado de la linea de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContrata_batch_detail(Integer domain, Integer contrata_batch, Integer contract, Short status)
	throws SQLException {
		Integer id =  super.insertContrata_batch_detail(domain, contrata_batch, contract, status);
		contrata_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_attachPk(Integer id){
		return invoice_attachDomains.get(id);
	}
	
	/**
	 * Invoice_attach
	 * @param invoice Identificador de la Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_attach( Integer invoice){
		Integer domain = null;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_attach
	 * @param id Identificador unico
	 * @param invoice Identificador de la Factura
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_attach(Integer invoice, Short mimeType, String description, Blob data, Short type, Date attach_date, String driveId)
	throws SQLException {
		Integer domain = getDomainForInvoice_attach( invoice);
		Integer id =  super.insertInvoice_attach( domain != null ? domain : getDefaultDomain(), invoice, mimeType, description, data, type, attach_date, driveId );
		if ( domain != null ) { 
			invoice_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice Identificador de la Factura
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_attach(Integer domain, Integer invoice, Short mimeType, String description, Blob data, Short type, Date attach_date, String driveId)
	throws SQLException {
		Integer id =  super.insertInvoice_attach(domain, invoice, mimeType, description, data, type, attach_date, driveId);
		invoice_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreement_extraDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_extraPk(Integer id){
		return agreement_extraDomains.get(id);
	}
	
	/**
	 * Agreement_extra
	 * @param agreement Convenio
	 * @param agreement_payment Concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_extra( Integer agreement , Integer agreement_payment){
		Integer domain = null;
			if ( ( domain = getDomainForAgreementPk( agreement ) ) != null )
				return domain;
			if ( ( domain = getDomainForAgreement_paymentPk( agreement_payment ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_extra
	 * @param id Identificador unico
	 * @param agreement Convenio
	 * @param agreement_payment Concepto
	 * @param start_date Fecha de inicio dd mm [year offset]
	 * @param end_date Fecha de finalizacion dd mm [year offset]
	 * @param issue_date Fecha de emision dd mm
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_extra(Integer agreement, Integer agreement_payment, String start_date, String end_date, String issue_date)
	throws SQLException {
		Integer domain = getDomainForAgreement_extra( agreement , agreement_payment);
		Integer id =  super.insertAgreement_extra( domain != null ? domain : getDefaultDomain(), agreement, agreement_payment, start_date, end_date, issue_date );
		if ( domain != null ) { 
			agreement_extraDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_extra
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param agreement Convenio
	 * @param agreement_payment Concepto
	 * @param start_date Fecha de inicio dd mm [year offset]
	 * @param end_date Fecha de finalizacion dd mm [year offset]
	 * @param issue_date Fecha de emision dd mm
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_extra(Integer domain, Integer agreement, Integer agreement_payment, String start_date, String end_date, String issue_date)
	throws SQLException {
		Integer id =  super.insertAgreement_extra(domain, agreement, agreement_payment, start_date, end_date, issue_date);
		agreement_extraDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> purchase_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPurchase_detailPk(Integer id){
		return purchase_detailDomains.get(id);
	}
	
	/**
	 * Purchase_detail
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param project Identificador del Proyecto
	 * @param proposal_detail Identificador del Detalle de Solicitud
	 * @param purchase Identificador del Pedido de Compra
	 * @returns domain's ID
	*/
	protected Integer getDomainForPurchase_detail( Integer carrier , Integer carrier_packing , Integer item , Integer project , Integer proposal_detail , Integer purchase){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
			if ( ( domain = getDomainForCarrier_packingPk( carrier_packing ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForProposal_detailPk( proposal_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForPurchasePk( purchase ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Purchase_detail
	 * @param id Identificador unico del Detalle del Pedido de Compra
	 * @param purchase Identificador del Pedido de Compra
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param source Origen del Detalle de la Compra
	 * @param source_id Identificador del Origen del Detalle de la Compra
	 * @param proposal_detail Identificador del Detalle de Solicitud
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPurchase_detail(Integer purchase, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Short source, Integer source_id, Integer proposal_detail, Double delivered, Date delivery_date, Integer carrier, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForPurchase_detail( carrier , carrier_packing , item , project , proposal_detail , purchase);
		Integer id =  super.insertPurchase_detail( domain != null ? domain : getDefaultDomain(), purchase, project, line, item, description, quantity, price, discount_expr, taxes, status, source, source_id, proposal_detail, delivered, delivery_date, carrier, carrier_packing, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			purchase_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Purchase_detail
	 * @param id Identificador unico del Detalle del Pedido de Compra
	 * @param domain Identificador del Dominio
	 * @param purchase Identificador del Pedido de Compra
	 * @param project Identificador del Proyecto
	 * @param line Numero de linea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param source Origen del Detalle de la Compra
	 * @param source_id Identificador del Origen del Detalle de la Compra
	 * @param proposal_detail Identificador del Detalle de Solicitud
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPurchase_detail(Integer domain, Integer purchase, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Short source, Integer source_id, Integer proposal_detail, Double delivered, Date delivery_date, Integer carrier, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertPurchase_detail(domain, purchase, project, line, item, description, quantity, price, discount_expr, taxes, status, source, source_id, proposal_detail, delivered, delivery_date, carrier, carrier_packing, creation_user, creation_date, modification_user, modification_date);
		purchase_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> web_info_page_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWeb_info_page_detailPk(Integer id){
		return web_info_page_detailDomains.get(id);
	}
	
	/**
	 * Web_info_page_detail
	 * @param web_info_page Identificador de la Pagina a la que corresponde el detalle
	 * @returns domain's ID
	*/
	protected Integer getDomainForWeb_info_page_detail( Integer web_info_page){
		Integer domain = null;
			if ( ( domain = getDomainForWeb_info_pagePk( web_info_page ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Web_info_page_detail
	 * @param id Codigo del Detalle de la Pagina
	 * @param web_info_page Identificador de la Pagina a la que corresponde el detalle
	 * @param title Titulo del contenido de la Pagina
	 * @param layout Tipo de plantilla
	 * @param content Texto del contenido de la Pagina
	 * @param extra Campo reservado a otros datos de la Pagina
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page_detail(Integer web_info_page, String title, Integer layout, String content, String extra)
	throws SQLException {
		Integer domain = getDomainForWeb_info_page_detail( web_info_page);
		Integer id =  super.insertWeb_info_page_detail( domain != null ? domain : getDefaultDomain(), web_info_page, title, layout, content, extra );
		if ( domain != null ) { 
			web_info_page_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Web_info_page_detail
	 * @param id Codigo del Detalle de la Pagina
	 * @param domain Identificador del Dominio
	 * @param web_info_page Identificador de la Pagina a la que corresponde el detalle
	 * @param title Titulo del contenido de la Pagina
	 * @param layout Tipo de plantilla
	 * @param content Texto del contenido de la Pagina
	 * @param extra Campo reservado a otros datos de la Pagina
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info_page_detail(Integer domain, Integer web_info_page, String title, Integer layout, String content, String extra)
	throws SQLException {
		Integer id =  super.insertWeb_info_page_detail(domain, web_info_page, title, layout, content, extra);
		web_info_page_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commission_categoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommission_categoryPk(Integer id){
		return commission_categoryDomains.get(id);
	}
	
	/**
	 * Commission_category
	 * @param category Identificador de la Categoria
	 * @param commission Identificador de la Comision
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommission_category( Integer category , Integer commission){
		Integer domain = null;
			if ( ( domain = getDomainForPcategoryPk( category ) ) != null )
				return domain;
			if ( ( domain = getDomainForCommissionPk( commission ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Commission_category
	 * @param id Identificador unico
	 * @param commission Identificador de la Comision
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_category(Integer commission, Integer category, Double quantity, Double rate)
	throws SQLException {
		Integer domain = getDomainForCommission_category( category , commission);
		Integer id =  super.insertCommission_category( domain != null ? domain : getDefaultDomain(), commission, category, quantity, rate );
		if ( domain != null ) { 
			commission_categoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commission_category
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param commission Identificador de la Comision
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_category(Integer domain, Integer commission, Integer category, Double quantity, Double rate)
	throws SQLException {
		Integer id =  super.insertCommission_category(domain, commission, category, quantity, rate);
		commission_categoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> auto_conceptDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAuto_conceptPk(Integer id){
		return auto_conceptDomains.get(id);
	}
	
	/**
	 * Auto_concept
	 * @returns domain's ID
	*/
	protected Integer getDomainForAuto_concept(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Auto_concept
	 * @param id Identificador unico del Concepto Automatico
	 * @param description Descripcion del Concepto Automatico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAuto_concept(String description)
	throws SQLException {
		Integer domain = getDomainForAuto_concept();
		Integer id =  super.insertAuto_concept( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			auto_conceptDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Auto_concept
	 * @param id Identificador unico del Concepto Automatico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Concepto Automatico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAuto_concept(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertAuto_concept(domain, description);
		auto_conceptDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model184_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model184_detailPk(Integer id){
		return fs_model184_detailDomains.get(id);
	}
	
	/**
	 * Fs_model184_detail
	 * @param fs_model184 Identificador del modelo 184
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model184_detail( Integer fs_model184){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model184Pk( fs_model184 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model184_detail
	 * @param id Identificador unico
	 * @param fs_model184 Identificador del modelo 184
	 * @param type Tipo de detalle
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param key Clave Percepcion
	 * @param subkey Subclave Percepcion
	 * @param province Provincia
	 * @param country Pais
	 * @param regime Regimen de determinacion de rendimientos
	 * @param activity_type Tipo de actividad
	 * @param epigraph Epigrafe
	 * @param grantee_document NIF persona o entidad cesionaria
	 * @param grantee_name Nombre persona o entidad cesionaria
	 * @param adq_date Fecha adquisicion accion/participacion
	 * @param increase Ajustes: Aumentos
	 * @param decrease Ajustes: Disminuciones
	 * @param accounting_result Resultado contable
	 * @param expenses Gastos
	 * @param net_yield Renta atribuible / Rend. Neto atribuible
	 * @param reduction_percent Porc. Reduccion
	 * @param deduction_right_rent Renta atrib. con drcho. deduccion
	 * @param result Ganancias / Perdidas
	 * @param deduction_base Base de la deduccion / Importe
	 * @param retention Retenciones e ingresos a cuenta
	 * @param part_type Clave tipo de participe
	 * @param member_end_of_year Miembro a 31 diciembre
	 * @param member_days Numero dias miembro
	 * @param part_percent Porcentaje de participacion
	 * @param amount Importe (rendimiento / retencion / deduccion)
	 * @param reduction Reduccion
	 * @param address Direccion
	 * @param location Situacion del inmueble
	 * @param cadasdral_reference Referencia catastral
	 * @param staff_expenses Gastos de personal
	 * @param asset_acquisition Adquisicion a terceros de bienes y servicios
	 * @param tax_deduction Tributos fiscalmente deducibles y gastos financieros
	 * @param other_tax_deduction Otros gastos fiscalmente deducibles
	 * @param vat_accrual_payment Regimen Especial de Criterio de Caja
	 * @param declared_key Clave del declarado
	 * @param nature Naturaleza del inmueble
	 * @param asset_percent Porc. titularidad inmueble
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model184_detail(Integer fs_model184, String type, String document, String name, String representative_document, String key, String subkey, Integer province, String country, Boolean regime, Boolean activity_type, Integer epigraph, String grantee_document, String grantee_name, Date adq_date, Double increase, Double decrease, Double accounting_result, Double expenses, Double net_yield, Double reduction_percent, Double deduction_right_rent, Double result, Double deduction_base, Double retention, Boolean part_type, Boolean member_end_of_year, Integer member_days, Double part_percent, Double amount, Double reduction, String address, String location, String cadasdral_reference, Double staff_expenses, Double asset_acquisition, Double tax_deduction, Double other_tax_deduction, Boolean vat_accrual_payment, String declared_key, String nature, Double asset_percent)
	throws SQLException {
		Integer domain = getDomainForFs_model184_detail( fs_model184);
		Integer id =  super.insertFs_model184_detail( domain != null ? domain : getDefaultDomain(), fs_model184, type, document, name, representative_document, key, subkey, province, country, regime, activity_type, epigraph, grantee_document, grantee_name, adq_date, increase, decrease, accounting_result, expenses, net_yield, reduction_percent, deduction_right_rent, result, deduction_base, retention, part_type, member_end_of_year, member_days, part_percent, amount, reduction, address, location, cadasdral_reference, staff_expenses, asset_acquisition, tax_deduction, other_tax_deduction, vat_accrual_payment, declared_key, nature, asset_percent );
		if ( domain != null ) { 
			fs_model184_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model184_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model184 Identificador del modelo 184
	 * @param type Tipo de detalle
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param key Clave Percepcion
	 * @param subkey Subclave Percepcion
	 * @param province Provincia
	 * @param country Pais
	 * @param regime Regimen de determinacion de rendimientos
	 * @param activity_type Tipo de actividad
	 * @param epigraph Epigrafe
	 * @param grantee_document NIF persona o entidad cesionaria
	 * @param grantee_name Nombre persona o entidad cesionaria
	 * @param adq_date Fecha adquisicion accion/participacion
	 * @param increase Ajustes: Aumentos
	 * @param decrease Ajustes: Disminuciones
	 * @param accounting_result Resultado contable
	 * @param expenses Gastos
	 * @param net_yield Renta atribuible / Rend. Neto atribuible
	 * @param reduction_percent Porc. Reduccion
	 * @param deduction_right_rent Renta atrib. con drcho. deduccion
	 * @param result Ganancias / Perdidas
	 * @param deduction_base Base de la deduccion / Importe
	 * @param retention Retenciones e ingresos a cuenta
	 * @param part_type Clave tipo de participe
	 * @param member_end_of_year Miembro a 31 diciembre
	 * @param member_days Numero dias miembro
	 * @param part_percent Porcentaje de participacion
	 * @param amount Importe (rendimiento / retencion / deduccion)
	 * @param reduction Reduccion
	 * @param address Direccion
	 * @param location Situacion del inmueble
	 * @param cadasdral_reference Referencia catastral
	 * @param staff_expenses Gastos de personal
	 * @param asset_acquisition Adquisicion a terceros de bienes y servicios
	 * @param tax_deduction Tributos fiscalmente deducibles y gastos financieros
	 * @param other_tax_deduction Otros gastos fiscalmente deducibles
	 * @param vat_accrual_payment Regimen Especial de Criterio de Caja
	 * @param declared_key Clave del declarado
	 * @param nature Naturaleza del inmueble
	 * @param asset_percent Porc. titularidad inmueble
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model184_detail(Integer domain, Integer fs_model184, String type, String document, String name, String representative_document, String key, String subkey, Integer province, String country, Boolean regime, Boolean activity_type, Integer epigraph, String grantee_document, String grantee_name, Date adq_date, Double increase, Double decrease, Double accounting_result, Double expenses, Double net_yield, Double reduction_percent, Double deduction_right_rent, Double result, Double deduction_base, Double retention, Boolean part_type, Boolean member_end_of_year, Integer member_days, Double part_percent, Double amount, Double reduction, String address, String location, String cadasdral_reference, Double staff_expenses, Double asset_acquisition, Double tax_deduction, Double other_tax_deduction, Boolean vat_accrual_payment, String declared_key, String nature, Double asset_percent)
	throws SQLException {
		Integer id =  super.insertFs_model184_detail(domain, fs_model184, type, document, name, representative_document, key, subkey, province, country, regime, activity_type, epigraph, grantee_document, grantee_name, adq_date, increase, decrease, accounting_result, expenses, net_yield, reduction_percent, deduction_right_rent, result, deduction_base, retention, part_type, member_end_of_year, member_days, part_percent, amount, reduction, address, location, cadasdral_reference, staff_expenses, asset_acquisition, tax_deduction, other_tax_deduction, vat_accrual_payment, declared_key, nature, asset_percent);
		fs_model184_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> noticeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForNoticePk(Integer id){
		return noticeDomains.get(id);
	}
	
	/**
	 * Notice
	 * @param recipient Destinatario del Aviso
	 * @param sender Remitente del Aviso
	 * @param work_group Grupo de Trabajo al que va dirigida el Aviso
	 * @returns domain's ID
	*/
	protected Integer getDomainForNotice( Integer recipient , Integer sender , Integer work_group){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( recipient ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( sender ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( work_group ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Notice
	 * @param id Identificador unico del Aviso
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
	 * @param notice Aviso al que referencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNotice(Timestamp date, Integer sender, Integer work_group, Integer recipient, String source, String company, String phone, String subject, Short status, Short type, Short priority, Integer notice)
	throws SQLException {
		Integer domain = getDomainForNotice( recipient , sender , work_group);
		Integer id =  super.insertNotice( domain != null ? domain : getDefaultDomain(), date, sender, work_group, recipient, source, company, phone, subject, status, type, priority, notice );
		if ( domain != null ) { 
			noticeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Notice
	 * @param id Identificador unico del Aviso
	 * @param domain Identificador del Dominio
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
	 * @param notice Aviso al que referencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNotice(Integer domain, Timestamp date, Integer sender, Integer work_group, Integer recipient, String source, String company, String phone, String subject, Short status, Short type, Short priority, Integer notice)
	throws SQLException {
		Integer id =  super.insertNotice(domain, date, sender, work_group, recipient, source, company, phone, subject, status, type, priority, notice);
		noticeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> account_entry_finance_trackingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entry_finance_trackingPk(Integer id){
		return account_entry_finance_trackingDomains.get(id);
	}
	
	/**
	 * Account_entry_finance_tracking
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry_finance_tracking( Integer account_entry , Integer finance_tracking){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForFinance_trackingPk( finance_tracking ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry_finance_tracking
	 * @param id Identificador unico
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_finance_tracking(Integer account_entry, Integer finance_tracking)
	throws SQLException {
		Integer domain = getDomainForAccount_entry_finance_tracking( account_entry , finance_tracking);
		Integer id =  super.insertAccount_entry_finance_tracking( domain != null ? domain : getDefaultDomain(), account_entry, finance_tracking );
		if ( domain != null ) { 
			account_entry_finance_trackingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry_finance_tracking
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_finance_tracking(Integer domain, Integer account_entry, Integer finance_tracking)
	throws SQLException {
		Integer id =  super.insertAccount_entry_finance_tracking(domain, account_entry, finance_tracking);
		account_entry_finance_trackingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> newsDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForNewsPk(Integer id){
		return newsDomains.get(id);
	}
	
	/**
	 * News
	 * @param category Categoria de la Noticia
	 * @param template Identificador de la Plantilla de Marketing
	 * @param rattach Identificador del Archivo Adjunto
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForNews( Integer category , Integer template , Integer rattach , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForCategoryPk( category ) ) != null )
				return domain;
			if ( ( domain = getDomainForMk_templatePk( template ) ) != null )
				return domain;
			if ( ( domain = getDomainForRattachPk( rattach ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * News
	 * @param id Identificador unico
	 * @param title Titulo de la Noticia
	 * @param description Descripcion de la Noticia
	 * @param content Contenido de la Noticia
	 * @param url Url de la Noticia
	 * @param active Indica si la Noticia esta activa o no
	 * @param rss Indica si la Noticia se va a publicar en rss o no
	 * @param init_date Fecha Noticia
	 * @param end_date Fecha fin Noticia
	 * @param category Categoria de la Noticia
	 * @param rattach Identificador del Archivo Adjunto
	 * @param scope Identificador del Ambito
	 * @param type Tipo de Contenido
	 * @param template Identificador de la Plantilla de Marketing
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNews(String title, String description, String content, String url, Boolean active, Boolean rss, Timestamp init_date, Timestamp end_date, Integer category, Integer rattach, Integer scope, Short type, Integer template)
	throws SQLException {
		Integer domain = getDomainForNews( category , template , rattach , scope);
		Integer id =  super.insertNews( domain != null ? domain : getDefaultDomain(), title, description, content, url, active, rss, init_date, end_date, category, rattach, scope, type, template );
		if ( domain != null ) { 
			newsDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * News
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param title Titulo de la Noticia
	 * @param description Descripcion de la Noticia
	 * @param content Contenido de la Noticia
	 * @param url Url de la Noticia
	 * @param active Indica si la Noticia esta activa o no
	 * @param rss Indica si la Noticia se va a publicar en rss o no
	 * @param init_date Fecha Noticia
	 * @param end_date Fecha fin Noticia
	 * @param category Categoria de la Noticia
	 * @param rattach Identificador del Archivo Adjunto
	 * @param scope Identificador del Ambito
	 * @param type Tipo de Contenido
	 * @param template Identificador de la Plantilla de Marketing
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNews(Integer domain, String title, String description, String content, String url, Boolean active, Boolean rss, Timestamp init_date, Timestamp end_date, Integer category, Integer rattach, Integer scope, Short type, Integer template)
	throws SQLException {
		Integer id =  super.insertNews(domain, title, description, content, url, active, rss, init_date, end_date, category, rattach, scope, type, template);
		newsDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> deliveryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDeliveryPk(Integer id){
		return deliveryDomains.get(id);
	}
	
	/**
	 * Delivery
	 * @param carrier Identificador de la agencia de transporte
	 * @param customer Identificador del Cliente
	 * @param pay_method Identificador de la Forma de Pago
	 * @param project Identificador del Proyecto
	 * @param address Identificador de la Direccion de envio del Albaran
	 * @param scope Ambito del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForDelivery( Integer carrier , Integer customer , Integer pay_method , Integer project , Integer address , Integer scope , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Delivery
	 * @param id Identificador unico del Albaran de Venta
	 * @param project Identificador del Proyecto
	 * @param series Serie del Albaran
	 * @param number Numero del Albaran
	 * @param customer Identificador del Cliente
	 * @param address Identificador de la Direccion de envio del Albaran
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param comments Comentarios del Albaran
	 * @param remarks Observaciones del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param carrier Identificador de la agencia de transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param number_plate Numero de matricula
	 * @param driver Nombre del conductor
	 * @param driver_document Numero de Documento del conductor
	 * @param total_packages Numero total de bultos
	 * @param total_weight Peso total
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param tracking_number Numero de expedicion
	 * @param shipping_status Estado de la entrega
	 * @param status_modification_date Fecha de modificacion del estado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDelivery(Integer project, String series, Integer number, Integer customer, Integer address, Timestamp issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Integer carrier, Integer carrier_packing, String number_plate, String driver, String driver_document, Double total_packages, Double total_weight, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String tracking_number, Short shipping_status, Timestamp status_modification_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForDelivery( carrier , customer , pay_method , project , address , scope , workplace);
		Integer id =  super.insertDelivery( domain != null ? domain : getDefaultDomain(), project, series, number, customer, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, carrier, carrier_packing, number_plate, driver, driver_document, total_packages, total_weight, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, tracking_number, shipping_status, status_modification_date, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			deliveryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Delivery
	 * @param id Identificador unico del Albaran de Venta
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param series Serie del Albaran
	 * @param number Numero del Albaran
	 * @param customer Identificador del Cliente
	 * @param address Identificador de la Direccion de envio del Albaran
	 * @param issue_time Fecha de emision del Albaran
	 * @param pay_method Identificador de la Forma de Pago
	 * @param security_level Nivel de seguridad del Albaran
	 * @param status Estado del Albaran
	 * @param comments Comentarios del Albaran
	 * @param remarks Observaciones del Albaran
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Albaran
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param carrier Identificador de la agencia de transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param number_plate Numero de matricula
	 * @param driver Nombre del conductor
	 * @param driver_document Numero de Documento del conductor
	 * @param total_packages Numero total de bultos
	 * @param total_weight Peso total
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param tracking_number Numero de expedicion
	 * @param shipping_status Estado de la entrega
	 * @param status_modification_date Fecha de modificacion del estado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDelivery(Integer domain, Integer project, String series, Integer number, Integer customer, Integer address, Timestamp issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Integer carrier, Integer carrier_packing, String number_plate, String driver, String driver_document, Double total_packages, Double total_weight, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String tracking_number, Short shipping_status, Timestamp status_modification_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertDelivery(domain, project, series, number, customer, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, carrier, carrier_packing, number_plate, driver, driver_document, total_packages, total_weight, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, tracking_number, shipping_status, status_modification_date, creation_user, creation_date, modification_user, modification_date);
		deliveryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> registryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRegistryPk(Integer id){
		return registryDomains.get(id);
	}
	
	/**
	 * Registry
	 * @returns domain's ID
	*/
	protected Integer getDomainForRegistry(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Registry
	 * @param id Identificador unico de la Persona o Empresa
	 * @param document Numero de Documento de la Persona o Empresa
	 * @param document_type Tipo de documento (NIF, CIF...)
	 * @param document_country Pais del documento
	 * @param name Nombre de la Persona o Empresa
	 * @param alias Alias de la Persona o Empresa
	 * @param type Tipo (Persona o Empresa)
	 * @param nationality Nacionalidad
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRegistry(String document, Short document_type, String document_country, String name, String alias, Short type, String nationality, Short security_level)
	throws SQLException {
		Integer domain = getDomainForRegistry();
		Integer id =  super.insertRegistry( domain != null ? domain : getDefaultDomain(), document, document_type, document_country, name, alias, type, nationality, security_level );
		if ( domain != null ) { 
			registryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Registry
	 * @param id Identificador unico de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param document Numero de Documento de la Persona o Empresa
	 * @param document_type Tipo de documento (NIF, CIF...)
	 * @param document_country Pais del documento
	 * @param name Nombre de la Persona o Empresa
	 * @param alias Alias de la Persona o Empresa
	 * @param type Tipo (Persona o Empresa)
	 * @param nationality Nacionalidad
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRegistry(Integer domain, String document, Short document_type, String document_country, String name, String alias, Short type, String nationality, Short security_level)
	throws SQLException {
		Integer id =  super.insertRegistry(domain, document, document_type, document_country, name, alias, type, nationality, security_level);
		registryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tas_itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTas_itemPk(Integer id){
		return tas_itemDomains.get(id);
	}
	
	/**
	 * Tas_item
	 * @param model Identificador del Modelo
	 * @returns domain's ID
	*/
	protected Integer getDomainForTas_item( Integer model){
		Integer domain = null;
			if ( ( domain = getDomainForModelPk( model ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Tas_item
	 * @param id Identificador unico del Articulo
	 * @param model Identificador del Modelo
	 * @param publicCode Codigo publico del Articulo
	 * @param privateCode Codigo privado del Articulo
	 * @param description Descripcion del Articulo
	 * @param add_info Informacion adicional del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTas_item(Integer model, String publicCode, String privateCode, String description, String add_info)
	throws SQLException {
		Integer domain = getDomainForTas_item( model);
		Integer id =  super.insertTas_item( domain != null ? domain : getDefaultDomain(), model, publicCode, privateCode, description, add_info );
		if ( domain != null ) { 
			tas_itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tas_item
	 * @param id Identificador unico del Articulo
	 * @param domain Identificador del Dominio
	 * @param model Identificador del Modelo
	 * @param publicCode Codigo publico del Articulo
	 * @param privateCode Codigo privado del Articulo
	 * @param description Descripcion del Articulo
	 * @param add_info Informacion adicional del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTas_item(Integer domain, Integer model, String publicCode, String privateCode, String description, String add_info)
	throws SQLException {
		Integer id =  super.insertTas_item(domain, model, publicCode, privateCode, description, add_info);
		tas_itemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contact_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContact_detailPk(Integer id){
		return contact_detailDomains.get(id);
	}
	
	/**
	 * Contact_detail
	 * @param contact Identificador del Contacto
	 * @param contact_group Identificador del Grupo de Contactos
	 * @returns domain's ID
	*/
	protected Integer getDomainForContact_detail( Integer contact , Integer contact_group){
		Integer domain = null;
			if ( ( domain = getDomainForContactPk( contact ) ) != null )
				return domain;
			if ( ( domain = getDomainForContactPk( contact_group ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contact_detail
	 * @param id Identificador unico
	 * @param contact_group Identificador del Grupo de Contactos
	 * @param contact Identificador del Contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact_detail(Integer contact_group, Integer contact)
	throws SQLException {
		Integer domain = getDomainForContact_detail( contact , contact_group);
		Integer id =  super.insertContact_detail( domain != null ? domain : getDefaultDomain(), contact_group, contact );
		if ( domain != null ) { 
			contact_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contact_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contact_group Identificador del Grupo de Contactos
	 * @param contact Identificador del Contacto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContact_detail(Integer domain, Integer contact_group, Integer contact)
	throws SQLException {
		Integer id =  super.insertContact_detail(domain, contact_group, contact);
		contact_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> purchaseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPurchasePk(Integer id){
		return purchaseDomains.get(id);
	}
	
	/**
	 * Purchase
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param pay_method Identificador de la Forma de Pago
	 * @param project Identificador del Proyecto
	 * @param address Identificador de la Direccion del Proveedor
	 * @param scope Ambito del Pedido
	 * @param supplier Identificador del Proveedor
	 * @param warehouse Identificador del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForPurchase( Integer carrier , Integer pay_method , Integer project , Integer address , Integer scope , Integer supplier , Integer warehouse , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForSupplierPk( supplier ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Purchase
	 * @param id Identificador unico del Pedido de Compra
	 * @param project Identificador del Proyecto
	 * @param supplier Identificador del Proveedor
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param purchase_reference Codigo de referencia del Pedido de Compra
	 * @param address Identificador de la Direccion del Proveedor
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param email_communication Indica si se ha comunicado a traves de email
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPurchase(Integer project, Integer supplier, String series, Integer number, String purchase_reference, Integer address, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer warehouse, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean email_communication, Date delivery_date, Integer carrier, Integer carrier_packing, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForPurchase( carrier , pay_method , project , address , scope , supplier , warehouse , workplace);
		Integer id =  super.insertPurchase( domain != null ? domain : getDefaultDomain(), project, supplier, series, number, purchase_reference, address, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, warehouse, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, email_communication, delivery_date, carrier, carrier_packing, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			purchaseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Purchase
	 * @param id Identificador unico del Pedido de Compra
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param supplier Identificador del Proveedor
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param purchase_reference Codigo de referencia del Pedido de Compra
	 * @param address Identificador de la Direccion del Proveedor
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param warehouse Identificador del Almacen
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param email_communication Indica si se ha comunicado a traves de email
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPurchase(Integer domain, Integer project, Integer supplier, String series, Integer number, String purchase_reference, Integer address, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer warehouse, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean email_communication, Date delivery_date, Integer carrier, Integer carrier_packing, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertPurchase(domain, project, supplier, series, number, purchase_reference, address, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, warehouse, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, email_communication, delivery_date, carrier, carrier_packing, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, creation_user, creation_date, modification_user, modification_date);
		purchaseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model190Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model190Pk(Integer id){
		return fs_model190Domains.get(id);
	}
	
	/**
	 * Fs_model190
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model190( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model190
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param receipt_total Importe declarado
	 * @param retention_total Importe declarado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model190(Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double receipt_total, Double retention_total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_model190( enterprise);
		Integer id =  super.insertFs_model190( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, receipt_total, retention_total, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_model190Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model190
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param receipt_total Importe declarado
	 * @param retention_total Importe declarado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model190(Integer domain, Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double receipt_total, Double retention_total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_model190(domain, enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, receipt_total, retention_total, creation_user, creation_date, modification_user, modification_date);
		fs_model190Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> training_courseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTraining_coursePk(Integer id){
		return training_courseDomains.get(id);
	}
	
	/**
	 * Training_course
	 * @param cno CNO
	 * @param training_center Identificador del Centro Formativo
	 * @returns domain's ID
	*/
	protected Integer getDomainForTraining_course( Integer cno , Integer training_center){
		Integer domain = null;
			if ( ( domain = getDomainForTraining_centerPk( training_center ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Training_course
	 * @param id Identificador unico
	 * @param training_center Identificador del Centro Formativo
	 * @param code Codigo del Curso Formativo
	 * @param certification_name Denominacion de la certificacion
	 * @param cno CNO
	 * @param modality Modalidad del Curso Formativo
	 * @param fp_title_name Titulo de formacion profesional
	 * @param occupation_name Nombre de la ocupacion
	 * @param professional_certificate Certificado de profesionalidad
	 * @param fp_title Titulo de formacion profesional
	 * @param center_available Centro disponible
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTraining_course(Integer training_center, String code, String certification_name, Integer cno, Short modality, String fp_title_name, String occupation_name, Boolean professional_certificate, Boolean fp_title, Boolean center_available)
	throws SQLException {
		Integer domain = getDomainForTraining_course( cno , training_center);
		Integer id =  super.insertTraining_course( domain != null ? domain : getDefaultDomain(), training_center, code, certification_name, cno, modality, fp_title_name, occupation_name, professional_certificate, fp_title, center_available );
		if ( domain != null ) { 
			training_courseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Training_course
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param training_center Identificador del Centro Formativo
	 * @param code Codigo del Curso Formativo
	 * @param certification_name Denominacion de la certificacion
	 * @param cno CNO
	 * @param modality Modalidad del Curso Formativo
	 * @param fp_title_name Titulo de formacion profesional
	 * @param occupation_name Nombre de la ocupacion
	 * @param professional_certificate Certificado de profesionalidad
	 * @param fp_title Titulo de formacion profesional
	 * @param center_available Centro disponible
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTraining_course(Integer domain, Integer training_center, String code, String certification_name, Integer cno, Short modality, String fp_title_name, String occupation_name, Boolean professional_certificate, Boolean fp_title, Boolean center_available)
	throws SQLException {
		Integer id =  super.insertTraining_course(domain, training_center, code, certification_name, cno, modality, fp_title_name, occupation_name, professional_certificate, fp_title, center_available);
		training_courseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pay_methodDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPay_methodPk(Integer id){
		return pay_methodDomains.get(id);
	}
	
	/**
	 * Pay_method
	 * @returns domain's ID
	*/
	protected Integer getDomainForPay_method(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Pay_method
	 * @param id Identificador unico de la Forma de Pago
	 * @param name Nombre de Forma de Pago
	 * @param type Tipo de Forma de Pago
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPay_method(String name, Short type)
	throws SQLException {
		Integer domain = getDomainForPay_method();
		Integer id =  super.insertPay_method( domain != null ? domain : getDefaultDomain(), name, type );
		if ( domain != null ) { 
			pay_methodDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pay_method
	 * @param id Identificador unico de la Forma de Pago
	 * @param domain Identificador del Dominio
	 * @param name Nombre de Forma de Pago
	 * @param type Tipo de Forma de Pago
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPay_method(Integer domain, String name, Short type)
	throws SQLException {
		Integer id =  super.insertPay_method(domain, name, type);
		pay_methodDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_vat_declarationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_vat_declarationPk(Integer id){
		return fs_vat_declarationDomains.get(id);
	}
	
	/**
	 * Fs_vat_declaration
	 * @param fs_vat Identificador de la Declaracion
	 * @param rbank Banco de la CompaÒia
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_vat_declaration( Integer fs_vat , Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForFs_vatPk( fs_vat ) ) != null )
				return domain;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_vat_declaration
	 * @param id Identificador unico
	 * @param fs_vat Identificador de la Declaracion
	 * @param without_activity Sin actividad
	 * @param administration Administracion
	 * @param percent Porcentaje atribuible
	 * @param operations_volume Volumen de operaciones
	 * @param quota Cuota atribuible
	 * @param prev_year_compensate_quota Cuota a compensar de ejerc. anteriores
	 * @param done_deposits Ingresos efectuados
	 * @param done_refunds Devoluciones practicadas
	 * @param extra_charge Recargo
	 * @param delay_interest Intereses de demora
	 * @param compensate A compensar
	 * @param pay_back A devolver
	 * @param deposit A ingresar
	 * @param prev_deposit Ingresado anteriormente
	 * @param prev_pay_back Devuelto anteriormente
	 * @param total_tax_debt Total deuda tributaria
	 * @param rbank Banco de la CompaÒia
	 * @param compensable Compensar o devolver
	 * @param status Estado de la Declaracion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat_declaration(Integer fs_vat, Boolean without_activity, Short administration, Double percent, Double operations_volume, Double quota, Double prev_year_compensate_quota, Double done_deposits, Double done_refunds, Double extra_charge, Double delay_interest, Double compensate, Double pay_back, Double deposit, Double prev_deposit, Double prev_pay_back, Double total_tax_debt, Integer rbank, Boolean compensable, Short status)
	throws SQLException {
		Integer domain = getDomainForFs_vat_declaration( fs_vat , rbank);
		Integer id =  super.insertFs_vat_declaration( domain != null ? domain : getDefaultDomain(), fs_vat, without_activity, administration, percent, operations_volume, quota, prev_year_compensate_quota, done_deposits, done_refunds, extra_charge, delay_interest, compensate, pay_back, deposit, prev_deposit, prev_pay_back, total_tax_debt, rbank, compensable, status );
		if ( domain != null ) { 
			fs_vat_declarationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_vat_declaration
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_vat Identificador de la Declaracion
	 * @param without_activity Sin actividad
	 * @param administration Administracion
	 * @param percent Porcentaje atribuible
	 * @param operations_volume Volumen de operaciones
	 * @param quota Cuota atribuible
	 * @param prev_year_compensate_quota Cuota a compensar de ejerc. anteriores
	 * @param done_deposits Ingresos efectuados
	 * @param done_refunds Devoluciones practicadas
	 * @param extra_charge Recargo
	 * @param delay_interest Intereses de demora
	 * @param compensate A compensar
	 * @param pay_back A devolver
	 * @param deposit A ingresar
	 * @param prev_deposit Ingresado anteriormente
	 * @param prev_pay_back Devuelto anteriormente
	 * @param total_tax_debt Total deuda tributaria
	 * @param rbank Banco de la CompaÒia
	 * @param compensable Compensar o devolver
	 * @param status Estado de la Declaracion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat_declaration(Integer domain, Integer fs_vat, Boolean without_activity, Short administration, Double percent, Double operations_volume, Double quota, Double prev_year_compensate_quota, Double done_deposits, Double done_refunds, Double extra_charge, Double delay_interest, Double compensate, Double pay_back, Double deposit, Double prev_deposit, Double prev_pay_back, Double total_tax_debt, Integer rbank, Boolean compensable, Short status)
	throws SQLException {
		Integer id =  super.insertFs_vat_declaration(domain, fs_vat, without_activity, administration, percent, operations_volume, quota, prev_year_compensate_quota, done_deposits, done_refunds, extra_charge, delay_interest, compensate, pay_back, deposit, prev_deposit, prev_pay_back, total_tax_debt, rbank, compensable, status);
		fs_vat_declarationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> roomDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRoomPk(Integer asset){
		return roomDomains.get(asset);
	}
	
	/**
	 * Room
	 * @param asset Identificador del Activo
	 * @param hotel Identificador del Hotel
	 * @param item Identificador del Producto
	 * @returns domain's ID
	*/
	protected Integer getDomainForRoom( Integer asset , Integer hotel , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForAssetPk( asset ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Room
	 * @param asset Identificador del Activo
	 * @param hotel Identificador del Hotel
	 * @param item Identificador del Producto
	 * @param status Estado de la Habitacion
	 * @param last_cleaning_date Ultima fecha de limpieza
	 * @param active Indica si la Habitacion esta activa o no
	 * @throws SQLException
	*/
	public void insertRoom(Integer asset, Integer hotel, Integer item, Short status, Timestamp last_cleaning_date, Boolean active)
	throws SQLException {
		Integer domain = getDomainForRoom( asset , hotel , item);
		 super.insertRoom( asset, domain != null ? domain : getDefaultDomain(), hotel, item, status, last_cleaning_date, active );
		if ( domain != null ) { 
			roomDomains.put(asset, domain);
		}
	}

	/**
	 * Room
	 * @param asset Identificador del Activo
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param item Identificador del Producto
	 * @param status Estado de la Habitacion
	 * @param last_cleaning_date Ultima fecha de limpieza
	 * @param active Indica si la Habitacion esta activa o no
	 * @throws SQLException
	*/
	public void insertRoom(Integer asset, Integer domain, Integer hotel, Integer item, Short status, Timestamp last_cleaning_date, Boolean active)
	throws SQLException {
		 super.insertRoom(asset, domain, hotel, item, status, last_cleaning_date, active);
		roomDomains.put(asset, domain );
			}

	
	private Map<Integer,Integer> carrier_packingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCarrier_packingPk(Integer id){
		return carrier_packingDomains.get(id);
	}
	
	/**
	 * Carrier_packing
	 * @param carrier Identificador de la agencia de transporte
	 * @returns domain's ID
	*/
	protected Integer getDomainForCarrier_packing( Integer carrier){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Carrier_packing
	 * @param id Identificador unico de la hoja de ruta
	 * @param series Serie de la hoja de ruta
	 * @param number Numero de la hoja de ruta
	 * @param type Indica el tipo de la hoja de ruta
	 * @param status Indica el estado de la hoja de ruta
	 * @param issue_date Fecha de emision
	 * @param carrier Identificador de la agencia de transporte
	 * @param delivery_date Fecha de entrega
	 * @param carrier_reference Referencia de la agencia de transporte
	 * @param number_plate Numero de matricula del vehiculo
	 * @param driver_name Nombre del conductor
	 * @param driver_document Numero de documento del conductor
	 * @param comments Observaciones carrier packing
	 * @param gross Bruto
	 * @param tare Tara
	 * @param net Neto
	 * @param reception_start_date Fecha entrada transporte, recepcion
	 * @param reception_end_date Fecha salida transporte, recepcion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCarrier_packing(String series, Integer number, Short type, Short status, Timestamp issue_date, Integer carrier, Timestamp delivery_date, String carrier_reference, String number_plate, String driver_name, String driver_document, String comments, Double gross, Double tare, Double net, Timestamp reception_start_date, Timestamp reception_end_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForCarrier_packing( carrier);
		Integer id =  super.insertCarrier_packing( domain != null ? domain : getDefaultDomain(), series, number, type, status, issue_date, carrier, delivery_date, carrier_reference, number_plate, driver_name, driver_document, comments, gross, tare, net, reception_start_date, reception_end_date, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			carrier_packingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Carrier_packing
	 * @param id Identificador unico de la hoja de ruta
	 * @param domain Identificador del dominio
	 * @param series Serie de la hoja de ruta
	 * @param number Numero de la hoja de ruta
	 * @param type Indica el tipo de la hoja de ruta
	 * @param status Indica el estado de la hoja de ruta
	 * @param issue_date Fecha de emision
	 * @param carrier Identificador de la agencia de transporte
	 * @param delivery_date Fecha de entrega
	 * @param carrier_reference Referencia de la agencia de transporte
	 * @param number_plate Numero de matricula del vehiculo
	 * @param driver_name Nombre del conductor
	 * @param driver_document Numero de documento del conductor
	 * @param comments Observaciones carrier packing
	 * @param gross Bruto
	 * @param tare Tara
	 * @param net Neto
	 * @param reception_start_date Fecha entrada transporte, recepcion
	 * @param reception_end_date Fecha salida transporte, recepcion
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCarrier_packing(Integer domain, String series, Integer number, Short type, Short status, Timestamp issue_date, Integer carrier, Timestamp delivery_date, String carrier_reference, String number_plate, String driver_name, String driver_document, String comments, Double gross, Double tare, Double net, Timestamp reception_start_date, Timestamp reception_end_date, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertCarrier_packing(domain, series, number, type, status, issue_date, carrier, delivery_date, carrier_reference, number_plate, driver_name, driver_document, comments, gross, tare, net, reception_start_date, reception_end_date, creation_user, creation_date, modification_user, modification_date);
		carrier_packingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> evaluation_observationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForEvaluation_observationPk(Integer id){
		return evaluation_observationDomains.get(id);
	}
	
	/**
	 * Evaluation_observation
	 * @param alumn Identificador de Alumno
	 * @returns domain's ID
	*/
	protected Integer getDomainForEvaluation_observation( Integer alumn){
		Integer domain = null;
			if ( ( domain = getDomainForCourse_alumnPk( alumn ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Evaluation_observation
	 * @param id Identificador unico
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de Evaluacion
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEvaluation_observation(Integer alumn, Short evaluation, String comments)
	throws SQLException {
		Integer domain = getDomainForEvaluation_observation( alumn);
		Integer id =  super.insertEvaluation_observation( domain != null ? domain : getDefaultDomain(), alumn, evaluation, comments );
		if ( domain != null ) { 
			evaluation_observationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Evaluation_observation
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de Evaluacion
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEvaluation_observation(Integer domain, Integer alumn, Short evaluation, String comments)
	throws SQLException {
		Integer id =  super.insertEvaluation_observation(domain, alumn, evaluation, comments);
		evaluation_observationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model193Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model193Pk(Integer id){
		return fs_model193Domains.get(id);
	}
	
	/**
	 * Fs_model193
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model193( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model193
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param retention_base_total Base de retenciones e ingresos a cuenta
	 * @param retention_total Retenciones e ingresos a cuenta
	 * @param deposit_retention_total Retenciones e ingresos a cuenta ingresados
	 * @param expenses_total Gastos
	 * @param nature Naturaleza del declarante
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model193(Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double retention_base_total, Double retention_total, Double deposit_retention_total, Double expenses_total, Boolean nature, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFs_model193( enterprise);
		Integer id =  super.insertFs_model193( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, retention_base_total, retention_total, deposit_retention_total, expenses_total, nature, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fs_model193Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model193
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param contact_person Persona de Contacto
	 * @param contact_phone Telf. Fijo de Contacto
	 * @param contact_mail Mail Persona de Contacto
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de declaracion sustituida
	 * @param receiver_count_total Numero total de perceptores
	 * @param retention_base_total Base de retenciones e ingresos a cuenta
	 * @param retention_total Retenciones e ingresos a cuenta
	 * @param deposit_retention_total Retenciones e ingresos a cuenta ingresados
	 * @param expenses_total Gastos
	 * @param nature Naturaleza del declarante
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model193(Integer domain, Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, String contact_person, String contact_phone, String contact_mail, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, Integer receiver_count_total, Double retention_base_total, Double retention_total, Double deposit_retention_total, Double expenses_total, Boolean nature, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFs_model193(domain, enterprise, year, administration, status, security_level, document, name, contact_person, contact_phone, contact_mail, complementary, replacement, comments, receipt, replaced_receipt, receiver_count_total, retention_base_total, retention_total, deposit_retention_total, expenses_total, nature, creation_user, creation_date, modification_user, modification_date);
		fs_model193Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> targetDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTargetPk(Integer registry){
		return targetDomains.get(registry);
	}
	
	/**
	 * Target
	 * @param registry Registro del Cliente Potencial
	 * @param scope Identificador del Ambito
	 * @param tariff Tarifa asociada al Cliente Potencial
	 * @returns domain's ID
	*/
	protected Integer getDomainForTarget( Integer registry , Integer scope , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
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
	 * @param scope Identificador del Ambito
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertTarget(Integer registry, Integer tariff, Short advertising, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer scope, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTarget( registry , scope , tariff);
		 super.insertTarget( registry, domain != null ? domain : getDefaultDomain(), tariff, advertising, surcharge, withholding, transaction, status, scope, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			targetDomains.put(registry, domain);
		}
	}

	/**
	 * Target
	 * @param registry Registro del Cliente Potencial
	 * @param domain Identificador del Dominio
	 * @param tariff Tarifa asociada al Cliente Potencial
	 * @param advertising Admision de Publicidad
	 * @param surcharge Indica si el Cliente Potencial tiene recargo de equivalencia
	 * @param withholding Indica si el Cliente Potencial aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Cliente Potencial
	 * @param status Estado del Cliente Potencial
	 * @param scope Identificador del Ambito
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertTarget(Integer registry, Integer domain, Integer tariff, Short advertising, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer scope, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		 super.insertTarget(registry, domain, tariff, advertising, surcharge, withholding, transaction, status, scope, creation_user, creation_date, modification_user, modification_date);
		targetDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> invoice_addressDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_addressPk(Integer id){
		return invoice_addressDomains.get(id);
	}
	
	/**
	 * Invoice_address
	 * @param geozone Identificador de la Zona Geografica
	 * @param invoice Identificador de la Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_address( Integer geozone , Integer invoice){
		Integer domain = null;
			if ( ( domain = getDomainForGeozonePk( geozone ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_address
	 * @param id Identificador unico
	 * @param invoice Identificador de la Factura
	 * @param street_type Tipo de via
	 * @param address Primera parte de la Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param province Provincia
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_address(Integer invoice, String street_type, String address, String number, String address2, String zip, String city, String province, Integer geozone)
	throws SQLException {
		Integer domain = getDomainForInvoice_address( geozone , invoice);
		Integer id =  super.insertInvoice_address( domain != null ? domain : getDefaultDomain(), invoice, street_type, address, number, address2, zip, city, province, geozone );
		if ( domain != null ) { 
			invoice_addressDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_address
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice Identificador de la Factura
	 * @param street_type Tipo de via
	 * @param address Primera parte de la Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param province Provincia
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_address(Integer domain, Integer invoice, String street_type, String address, String number, String address2, String zip, String city, String province, Integer geozone)
	throws SQLException {
		Integer id =  super.insertInvoice_address(domain, invoice, street_type, address, number, address2, zip, city, province, geozone);
		invoice_addressDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> ritemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRitemPk(Integer id){
		return ritemDomains.get(id);
	}
	
	/**
	 * Ritem
	 * @param item Identificador del Articulo
	 * @param registry Identificador de Persona o Empresa
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForRitem( Integer item , Integer registry , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Ritem
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param item Identificador del Articulo
	 * @param type Tipo de relacion
	 * @param code Codigo del Producto
	 * @param edi_sales_code Codigo EAN de ventas para EDI
	 * @param price Precio del Producto
	 * @param discount_expr Descuentos del Producto
	 * @param priority Prioridad del Producto
	 * @param workplace Identificador del Centro de Trabajo
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRitem(Integer registry, Integer item, Short type, String code, String edi_sales_code, Double price, String discount_expr, Short priority, Integer workplace, Short status)
	throws SQLException {
		Integer domain = getDomainForRitem( item , registry , workplace);
		Integer id =  super.insertRitem( domain != null ? domain : getDefaultDomain(), registry, item, type, code, edi_sales_code, price, discount_expr, priority, workplace, status );
		if ( domain != null ) { 
			ritemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Ritem
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param item Identificador del Articulo
	 * @param type Tipo de relacion
	 * @param code Codigo del Producto
	 * @param edi_sales_code Codigo EAN de ventas para EDI
	 * @param price Precio del Producto
	 * @param discount_expr Descuentos del Producto
	 * @param priority Prioridad del Producto
	 * @param workplace Identificador del Centro de Trabajo
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRitem(Integer domain, Integer registry, Integer item, Short type, String code, String edi_sales_code, Double price, String discount_expr, Short priority, Integer workplace, Short status)
	throws SQLException {
		Integer id =  super.insertRitem(domain, registry, item, type, code, edi_sales_code, price, discount_expr, priority, workplace, status);
		ritemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rsellerDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRsellerPk(Integer id){
		return rsellerDomains.get(id);
	}
	
	/**
	 * Rseller
	 * @param registry Identificador de Persona o Empresa
	 * @param seller Identificador del Comercial
	 * @returns domain's ID
	*/
	protected Integer getDomainForRseller( Integer registry , Integer seller){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rseller
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param seller Identificador del Comercial
	 * @param start_date Fecha de Inicio
	 * @param end_date Fecha de Fin
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRseller(Integer registry, Integer seller, Date start_date, Date end_date, Short status)
	throws SQLException {
		Integer domain = getDomainForRseller( registry , seller);
		Integer id =  super.insertRseller( domain != null ? domain : getDefaultDomain(), registry, seller, start_date, end_date, status );
		if ( domain != null ) { 
			rsellerDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rseller
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param seller Identificador del Comercial
	 * @param start_date Fecha de Inicio
	 * @param end_date Fecha de Fin
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRseller(Integer domain, Integer registry, Integer seller, Date start_date, Date end_date, Short status)
	throws SQLException {
		Integer id =  super.insertRseller(domain, registry, seller, start_date, end_date, status);
		rsellerDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoicing_groupDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoicing_groupPk(Integer id){
		return invoicing_groupDomains.get(id);
	}
	
	/**
	 * Invoicing_group
	 * @param customer Identificador del Cliente
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoicing_group( Integer customer){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoicing_group
	 * @param id Identificador unico
	 * @param customer Identificador del Cliente
	 * @param description Descripcion del Grupo de Facturacion
	 * @param customer_grouped Indica si el Grupo de Facturacion agrupa los Clientes en una sola Factura
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoicing_group(Integer customer, String description, Boolean customer_grouped, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForInvoicing_group( customer);
		Integer id =  super.insertInvoicing_group( domain != null ? domain : getDefaultDomain(), customer, description, customer_grouped, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			invoicing_groupDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoicing_group
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param customer Identificador del Cliente
	 * @param description Descripcion del Grupo de Facturacion
	 * @param customer_grouped Indica si el Grupo de Facturacion agrupa los Clientes en una sola Factura
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoicing_group(Integer domain, Integer customer, String description, Boolean customer_grouped, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertInvoicing_group(domain, customer, description, customer_grouped, creation_user, creation_date, modification_user, modification_date);
		invoicing_groupDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_detail_accountDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_detail_accountPk(Integer id){
		return invoice_detail_accountDomains.get(id);
	}
	
	/**
	 * Invoice_detail_account
	 * @param account Identificador de la Cuenta Contable
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_detail_account( Integer account , Integer invoice_detail){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoice_detailPk( invoice_detail ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_detail_account
	 * @param id Identificador unico
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail_account(Integer invoice_detail, Integer account)
	throws SQLException {
		Integer domain = getDomainForInvoice_detail_account( account , invoice_detail);
		Integer id =  super.insertInvoice_detail_account( domain != null ? domain : getDefaultDomain(), invoice_detail, account );
		if ( domain != null ) { 
			invoice_detail_accountDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_detail_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_detail_account(Integer domain, Integer invoice_detail, Integer account)
	throws SQLException {
		Integer id =  super.insertInvoice_detail_account(domain, invoice_detail, account);
		invoice_detail_accountDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_bonusDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_bonusPk(Integer id){
		return contract_bonusDomains.get(id);
	}
	
	/**
	 * Contract_bonus
	 * @param bonus_concept Concepto de bonificacion
	 * @param contract Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_bonus( Integer bonus_concept , Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForBonus_conceptPk( bonus_concept ) ) != null )
				return domain;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_bonus
	 * @param id Identificador unico
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param bonus_concept Concepto de bonificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_bonus(Integer contract, String description, String expression, Date start_date, Date end_date, Integer bonus_concept)
	throws SQLException {
		Integer domain = getDomainForContract_bonus( bonus_concept , contract);
		Integer id =  super.insertContract_bonus( domain != null ? domain : getDefaultDomain(), contract, description, expression, start_date, end_date, bonus_concept );
		if ( domain != null ) { 
			contract_bonusDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_bonus
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param bonus_concept Concepto de bonificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_bonus(Integer domain, Integer contract, String description, String expression, Date start_date, Date end_date, Integer bonus_concept)
	throws SQLException {
		Integer id =  super.insertContract_bonus(domain, contract, description, expression, start_date, end_date, bonus_concept);
		contract_bonusDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salary_embargoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_embargoPk(Integer id){
		return salary_embargoDomains.get(id);
	}
	
	/**
	 * Salary_embargo
	 * @param contract_embargo Embargo
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_embargo( Integer contract_embargo , Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForContract_embargoPk( contract_embargo ) ) != null )
				return domain;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_embargo
	 * @param id Identificador unico
	 * @param salary Recibo del pago de salarios
	 * @param contract_embargo Embargo
	 * @param amount Importe
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_embargo(Integer salary, Integer contract_embargo, Double amount, String description)
	throws SQLException {
		Integer domain = getDomainForSalary_embargo( contract_embargo , salary);
		Integer id =  super.insertSalary_embargo( domain != null ? domain : getDefaultDomain(), salary, contract_embargo, amount, description );
		if ( domain != null ) { 
			salary_embargoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_embargo
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param contract_embargo Embargo
	 * @param amount Importe
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_embargo(Integer domain, Integer salary, Integer contract_embargo, Double amount, String description)
	throws SQLException {
		Integer id =  super.insertSalary_embargo(domain, salary, contract_embargo, amount, description);
		salary_embargoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> application_user_profileDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForApplication_user_profilePk(Integer id){
		return application_user_profileDomains.get(id);
	}
	
	/**
	 * Application_user_profile
	 * @param application_user Identificador del Usuario de la Aplicacion
	 * @param profile Identificador del Perfil
	 * @returns domain's ID
	*/
	protected Integer getDomainForApplication_user_profile( Integer application_user , Integer profile){
		Integer domain = null;
			if ( ( domain = getDomainForApplication_userPk( application_user ) ) != null )
				return domain;
			if ( ( domain = getDomainForProfilePk( profile ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Application_user_profile
	 * @param id Identificador unico
	 * @param application_user Identificador del Usuario de la Aplicacion
	 * @param profile Identificador del Perfil
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApplication_user_profile(Integer application_user, Integer profile)
	throws SQLException {
		Integer domain = getDomainForApplication_user_profile( application_user , profile);
		Integer id =  super.insertApplication_user_profile( domain != null ? domain : getDefaultDomain(), application_user, profile );
		if ( domain != null ) { 
			application_user_profileDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Application_user_profile
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param application_user Identificador del Usuario de la Aplicacion
	 * @param profile Identificador del Perfil
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApplication_user_profile(Integer domain, Integer application_user, Integer profile)
	throws SQLException {
		Integer id =  super.insertApplication_user_profile(domain, application_user, profile);
		application_user_profileDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> enterprise_cccDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForEnterprise_cccPk(Integer id){
		return enterprise_cccDomains.get(id);
	}
	
	/**
	 * Enterprise_ccc
	 * @param enterprise_activity Identificador de la Actividad de Empresa
	 * @param geozone Identificador de la Zona Geografica
	 * @returns domain's ID
	*/
	protected Integer getDomainForEnterprise_ccc( Integer enterprise_activity , Integer geozone){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprise_activityPk( enterprise_activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForGeozonePk( geozone ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Enterprise_ccc
	 * @param id Identificador unico
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param type Tipo de Cuenta Cotizacion
	 * @param enterprise_activity Identificador de la Actividad de Empresa
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_ccc(String ccc, Short type, Integer enterprise_activity, Integer geozone)
	throws SQLException {
		Integer domain = getDomainForEnterprise_ccc( enterprise_activity , geozone);
		Integer id =  super.insertEnterprise_ccc( domain != null ? domain : getDefaultDomain(), ccc, type, enterprise_activity, geozone );
		if ( domain != null ) { 
			enterprise_cccDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Enterprise_ccc
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param type Tipo de Cuenta Cotizacion
	 * @param enterprise_activity Identificador de la Actividad de Empresa
	 * @param geozone Identificador de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_ccc(Integer domain, String ccc, Short type, Integer enterprise_activity, Integer geozone)
	throws SQLException {
		Integer id =  super.insertEnterprise_ccc(domain, ccc, type, enterprise_activity, geozone);
		enterprise_cccDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_divertDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_divertPk(Integer id){
		return project_reservation_divertDomains.get(id);
	}
	
	/**
	 * Project_reservation_divert
	 * @param divert_hotel Identificador del Hotel destino
	 * @param project_reservation Identificador de la Reserva
	 * @param request_hotel Identificador del Hotel solicitante
	 * @param request_user Identificador del Usuario solicitante
	 * @param response_user Identificador del Usuario de respuesta
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_divert( Integer divert_hotel , Integer project_reservation , Integer request_hotel , Integer request_user , Integer response_user){
		Integer domain = null;
			if ( ( domain = getDomainForHotelPk( divert_hotel ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservationPk( project_reservation ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( request_hotel ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( request_user ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( response_user ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_divert
	 * @param id Identificador unico
	 * @param project_reservation Identificador de la Reserva
	 * @param request_hotel Identificador del Hotel solicitante
	 * @param divert_hotel Identificador del Hotel destino
	 * @param divert_date Fecha de Desvio
	 * @param status Estado del Desvio
	 * @param request_user Identificador del Usuario solicitante
	 * @param response_user Identificador del Usuario de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_divert(Integer project_reservation, Integer request_hotel, Integer divert_hotel, Date divert_date, Short status, Integer request_user, Integer response_user)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_divert( divert_hotel , project_reservation , request_hotel , request_user , response_user);
		Integer id =  super.insertProject_reservation_divert( domain != null ? domain : getDefaultDomain(), project_reservation, request_hotel, divert_hotel, divert_date, status, request_user, response_user );
		if ( domain != null ) { 
			project_reservation_divertDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_divert
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param request_hotel Identificador del Hotel solicitante
	 * @param divert_hotel Identificador del Hotel destino
	 * @param divert_date Fecha de Desvio
	 * @param status Estado del Desvio
	 * @param request_user Identificador del Usuario solicitante
	 * @param response_user Identificador del Usuario de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_divert(Integer domain, Integer project_reservation, Integer request_hotel, Integer divert_hotel, Date divert_date, Short status, Integer request_user, Integer response_user)
	throws SQLException {
		Integer id =  super.insertProject_reservation_divert(domain, project_reservation, request_hotel, divert_hotel, divert_date, status, request_user, response_user);
		project_reservation_divertDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> deduction_conceptDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDeduction_conceptPk(Integer id){
		return deduction_conceptDomains.get(id);
	}
	
	/**
	 * Deduction_concept
	 * @returns domain's ID
	*/
	protected Integer getDomainForDeduction_concept(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Deduction_concept
	 * @param id Identificador unico
	 * @param code Codigo
	 * @param description Descripcion
	 * @param type Tipo de Deduccion Salarial
	 * @param description_decorable 
	 * @param expression Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDeduction_concept(String code, String description, Short type, Short description_decorable, String expression)
	throws SQLException {
		Integer domain = getDomainForDeduction_concept();
		Integer id =  super.insertDeduction_concept( domain != null ? domain : getDefaultDomain(), code, description, type, description_decorable, expression );
		if ( domain != null ) { 
			deduction_conceptDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Deduction_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo
	 * @param description Descripcion
	 * @param type Tipo de Deduccion Salarial
	 * @param description_decorable 
	 * @param expression Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDeduction_concept(Integer domain, String code, String description, Short type, Short description_decorable, String expression)
	throws SQLException {
		Integer id =  super.insertDeduction_concept(domain, code, description, type, description_decorable, expression);
		deduction_conceptDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> financeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFinancePk(Integer id){
		return financeDomains.get(id);
	}
	
	/**
	 * Finance
	 * @param finance_group Identificador unico del Vencimiento agrupador
	 * @param invoice Identificador de la Factura
	 * @param pay_method Identificador de la Forma de Pago
	 * @param registry Identificador del Cliente o Proveedor
	 * @param scope Ambito del Vencimiento
	 * @returns domain's ID
	*/
	protected Integer getDomainForFinance( Integer finance_group , Integer invoice , Integer pay_method , Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForFinancePk( finance_group ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Finance
	 * @param id Identificador unico del Vencimiento
	 * @param payment Indica si es un pago o un cobro
	 * @param registry Identificador del Cliente o Proveedor
	 * @param rdocument Numero de Documento del Cliente o Proveedor
	 * @param rdocument_type Tipo de documento (NIF, CIF...)
	 * @param rdocument_country Pais del documento
	 * @param rname Nombre completo del Cliente o Proveedor
	 * @param amount Importe del Vencimiento
	 * @param expenses Gastos asociados al Vencimiento
	 * @param concept Concepto del Vencimiento
	 * @param invoice Identificador de la Factura
	 * @param due_date Fecha de Vencimiento
	 * @param pay_method Identificador de la Forma de Pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param cheque_number Numero de cheque
	 * @param status Estado del Vencimiento
	 * @param security_level Nivel de seguridad del Vencimiento
	 * @param remarks Observaciones del Vencimiento
	 * @param scope Ambito del Vencimiento
	 * @param manual Indica si el Vencimiento es manual
	 * @param advance Indica si el Vencimiento es un anticipo
	 * @param payroll Indica si el Vencimiento es de Nominas
	 * @param prepayment Indica si el Vencimiento es un Suplido
	 * @param source_id Identificador del Origen del Vencimiento
	 * @param finance_group Identificador unico del Vencimiento agrupador
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance(Boolean payment, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Double amount, Double expenses, String concept, Integer invoice, Date due_date, Integer pay_method, String bank_account, String bank_alias, String bic, String cheque_number, Short status, Short security_level, String remarks, Integer scope, Boolean manual, Boolean advance, Boolean payroll, Boolean prepayment, Integer source_id, Integer finance_group, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFinance( finance_group , invoice , pay_method , registry , scope);
		Integer id =  super.insertFinance( domain != null ? domain : getDefaultDomain(), payment, registry, rdocument, rdocument_type, rdocument_country, rname, amount, expenses, concept, invoice, due_date, pay_method, bank_account, bank_alias, bic, cheque_number, status, security_level, remarks, scope, manual, advance, payroll, prepayment, source_id, finance_group, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			financeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Finance
	 * @param id Identificador unico del Vencimiento
	 * @param domain Identificador del Dominio
	 * @param payment Indica si es un pago o un cobro
	 * @param registry Identificador del Cliente o Proveedor
	 * @param rdocument Numero de Documento del Cliente o Proveedor
	 * @param rdocument_type Tipo de documento (NIF, CIF...)
	 * @param rdocument_country Pais del documento
	 * @param rname Nombre completo del Cliente o Proveedor
	 * @param amount Importe del Vencimiento
	 * @param expenses Gastos asociados al Vencimiento
	 * @param concept Concepto del Vencimiento
	 * @param invoice Identificador de la Factura
	 * @param due_date Fecha de Vencimiento
	 * @param pay_method Identificador de la Forma de Pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param cheque_number Numero de cheque
	 * @param status Estado del Vencimiento
	 * @param security_level Nivel de seguridad del Vencimiento
	 * @param remarks Observaciones del Vencimiento
	 * @param scope Ambito del Vencimiento
	 * @param manual Indica si el Vencimiento es manual
	 * @param advance Indica si el Vencimiento es un anticipo
	 * @param payroll Indica si el Vencimiento es de Nominas
	 * @param prepayment Indica si el Vencimiento es un Suplido
	 * @param source_id Identificador del Origen del Vencimiento
	 * @param finance_group Identificador unico del Vencimiento agrupador
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance(Integer domain, Boolean payment, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Double amount, Double expenses, String concept, Integer invoice, Date due_date, Integer pay_method, String bank_account, String bank_alias, String bic, String cheque_number, Short status, Short security_level, String remarks, Integer scope, Boolean manual, Boolean advance, Boolean payroll, Boolean prepayment, Integer source_id, Integer finance_group, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFinance(domain, payment, registry, rdocument, rdocument_type, rdocument_country, rname, amount, expenses, concept, invoice, due_date, pay_method, bank_account, bank_alias, bic, cheque_number, status, security_level, remarks, scope, manual, advance, payroll, prepayment, source_id, finance_group, creation_user, creation_date, modification_user, modification_date);
		financeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> finance_posDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFinance_posPk(Integer id){
		return finance_posDomains.get(id);
	}
	
	/**
	 * Finance_pos
	 * @param finance Identificador de Vencimiento
	 * @param pos Identificador del TPV
	 * @returns domain's ID
	*/
	protected Integer getDomainForFinance_pos( Integer finance , Integer pos){
		Integer domain = null;
			if ( ( domain = getDomainForFinancePk( finance ) ) != null )
				return domain;
			if ( ( domain = getDomainForPosPk( pos ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Finance_pos
	 * @param id Identificador unico
	 * @param finance Identificador de Vencimiento
	 * @param pos Identificador del TPV
	 * @param code Codigo de autorizacion
	 * @param xml_response XML de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance_pos(Integer finance, Integer pos, String code, String xml_response)
	throws SQLException {
		Integer domain = getDomainForFinance_pos( finance , pos);
		Integer id =  super.insertFinance_pos( domain != null ? domain : getDefaultDomain(), finance, pos, code, xml_response );
		if ( domain != null ) { 
			finance_posDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Finance_pos
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param finance Identificador de Vencimiento
	 * @param pos Identificador del TPV
	 * @param code Codigo de autorizacion
	 * @param xml_response XML de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance_pos(Integer domain, Integer finance, Integer pos, String code, String xml_response)
	throws SQLException {
		Integer id =  super.insertFinance_pos(domain, finance, pos, code, xml_response);
		finance_posDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> markDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMarkPk(Integer id){
		return markDomains.get(id);
	}
	
	/**
	 * Mark
	 * @param alumn Identificador de Alumno
	 * @param subject Identificador de Asignatura
	 * @returns domain's ID
	*/
	protected Integer getDomainForMark( Integer alumn , Integer subject){
		Integer domain = null;
			if ( ( domain = getDomainForCourse_alumnPk( alumn ) ) != null )
				return domain;
			if ( ( domain = getDomainForCourse_academicskillPk( subject ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mark
	 * @param id Identificador unico
	 * @param subject Identificador de Asignatura
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de evaluacion
	 * @param mark Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMark(Integer subject, Integer alumn, Short evaluation, Double mark)
	throws SQLException {
		Integer domain = getDomainForMark( alumn , subject);
		Integer id =  super.insertMark( domain != null ? domain : getDefaultDomain(), subject, alumn, evaluation, mark );
		if ( domain != null ) { 
			markDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mark
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param subject Identificador de Asignatura
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de evaluacion
	 * @param mark Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMark(Integer domain, Integer subject, Integer alumn, Short evaluation, Double mark)
	throws SQLException {
		Integer id =  super.insertMark(domain, subject, alumn, evaluation, mark);
		markDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> newsletterDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForNewsletterPk(Integer id){
		return newsletterDomains.get(id);
	}
	
	/**
	 * Newsletter
	 * @param template Identificador de la Plantilla de Marketing
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForNewsletter( Integer template , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForMk_templatePk( template ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Newsletter
	 * @param id Identificador unico
	 * @param name Nombre del Boletin
	 * @param date Fecha del Boletin
	 * @param layout Disposicion del Boletin
	 * @param active Indica si el Boletin esta activo o no
	 * @param subject Asunto del Boletin
	 * @param scope Identificador del Ambito
	 * @param highlightFirst Indica si el Boletin destaca la primera noticia o no
	 * @param template Identificador de la Plantilla de Marketing
	 * @param newsSeparator Indica si el Boletin incluye un separador entre noticias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNewsletter(String name, Timestamp date, Short layout, Boolean active, String subject, Integer scope, Boolean highlightFirst, Integer template, Boolean newsSeparator)
	throws SQLException {
		Integer domain = getDomainForNewsletter( template , scope);
		Integer id =  super.insertNewsletter( domain != null ? domain : getDefaultDomain(), name, date, layout, active, subject, scope, highlightFirst, template, newsSeparator );
		if ( domain != null ) { 
			newsletterDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Newsletter
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Boletin
	 * @param date Fecha del Boletin
	 * @param layout Disposicion del Boletin
	 * @param active Indica si el Boletin esta activo o no
	 * @param subject Asunto del Boletin
	 * @param scope Identificador del Ambito
	 * @param highlightFirst Indica si el Boletin destaca la primera noticia o no
	 * @param template Identificador de la Plantilla de Marketing
	 * @param newsSeparator Indica si el Boletin incluye un separador entre noticias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNewsletter(Integer domain, String name, Timestamp date, Short layout, Boolean active, String subject, Integer scope, Boolean highlightFirst, Integer template, Boolean newsSeparator)
	throws SQLException {
		Integer id =  super.insertNewsletter(domain, name, date, layout, active, subject, scope, highlightFirst, template, newsSeparator);
		newsletterDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> elaboration_detail_compositionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForElaboration_detail_compositionPk(Integer id){
		return elaboration_detail_compositionDomains.get(id);
	}
	
	/**
	 * Elaboration_detail_composition
	 * @param elaboration_detail Identificador del lote elaborado
	 * @param item Identificador del articulo no base utilizado
	 * @param warehouse Identificador del almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForElaboration_detail_composition( Integer elaboration_detail , Integer item , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForElaboration_detailPk( elaboration_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Elaboration_detail_composition
	 * @param id Identificador unico de la composicion
	 * @param elaboration_detail Identificador del lote elaborado
	 * @param item Identificador del articulo no base utilizado
	 * @param quantity Cantidad utilizada
	 * @param warehouse Identificador del almacen
	 * @param add_info Informacion de uso interno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration_detail_composition(Integer elaboration_detail, Integer item, Double quantity, Integer warehouse, String add_info, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForElaboration_detail_composition( elaboration_detail , item , warehouse);
		Integer id =  super.insertElaboration_detail_composition( domain != null ? domain : getDefaultDomain(), elaboration_detail, item, quantity, warehouse, add_info, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			elaboration_detail_compositionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Elaboration_detail_composition
	 * @param id Identificador unico de la composicion
	 * @param domain Identificador del dominio
	 * @param elaboration_detail Identificador del lote elaborado
	 * @param item Identificador del articulo no base utilizado
	 * @param quantity Cantidad utilizada
	 * @param warehouse Identificador del almacen
	 * @param add_info Informacion de uso interno
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration_detail_composition(Integer domain, Integer elaboration_detail, Integer item, Double quantity, Integer warehouse, String add_info, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertElaboration_detail_composition(domain, elaboration_detail, item, quantity, warehouse, add_info, creation_user, creation_date, modification_user, modification_date);
		elaboration_detail_compositionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_activityDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_activityPk(Integer id){
		return project_activityDomains.get(id);
	}
	
	/**
	 * Project_activity
	 * @param activity_type Tipo de Actividad
	 * @param project Identificador del Expendiente
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_activity( Integer activity_type , Integer project){
		Integer domain = null;
			if ( ( domain = getDomainForActivity_typePk( activity_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_activity
	 * @param id Identificador unico de la Actividad
	 * @param project Identificador del Expendiente
	 * @param activity_type Tipo de Actividad
	 * @param active Activo, si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_activity(Integer project, Integer activity_type, Boolean active)
	throws SQLException {
		Integer domain = getDomainForProject_activity( activity_type , project);
		Integer id =  super.insertProject_activity( domain != null ? domain : getDefaultDomain(), project, activity_type, active );
		if ( domain != null ) { 
			project_activityDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_activity
	 * @param id Identificador unico de la Actividad
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Expendiente
	 * @param activity_type Tipo de Actividad
	 * @param active Activo, si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_activity(Integer domain, Integer project, Integer activity_type, Boolean active)
	throws SQLException {
		Integer id =  super.insertProject_activity(domain, project, activity_type, active);
		project_activityDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> sepe_batch_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSepe_batch_attachPk(Integer id){
		return sepe_batch_attachDomains.get(id);
	}
	
	/**
	 * Sepe_batch_attach
	 * @param scope Ambito del Archivo Adjunto
	 * @returns domain's ID
	*/
	protected Integer getDomainForSepe_batch_attach( Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Sepe_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param source_batch Identificador de la remesa
	 * @param source_type Tipo de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSepe_batch_attach(Integer source_batch, Short source_type, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date, String driveId)
	throws SQLException {
		Integer domain = getDomainForSepe_batch_attach( scope);
		Integer id =  super.insertSepe_batch_attach( domain != null ? domain : getDefaultDomain(), source_batch, source_type, mimeType, description, data, type, scope, attach_date, driveId );
		if ( domain != null ) { 
			sepe_batch_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Sepe_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param domain Identificador del Dominio
	 * @param source_batch Identificador de la remesa
	 * @param source_type Tipo de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSepe_batch_attach(Integer domain, Integer source_batch, Short source_type, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date, String driveId)
	throws SQLException {
		Integer id =  super.insertSepe_batch_attach(domain, source_batch, source_type, mimeType, description, data, type, scope, attach_date, driveId);
		sepe_batch_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> mail_accountDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMail_accountPk(Integer id){
		return mail_accountDomains.get(id);
	}
	
	/**
	 * Mail_account
	 * @param signature Identificador de la Firma
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForMail_account( Integer signature , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForSignaturePk( signature ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mail_account
	 * @param id Identificador unico
	 * @param name Nombre de la Cuenta de Correo
	 * @param email Cuenta de correo
	 * @param replyto_mail Email de Respuesta
	 * @param incoming_host Host del correo entrante
	 * @param protocol Protocolo utilizado (IMAP)
	 * @param incoming_port Puerto del correo entrante
	 * @param incoming_security Seguridad de conexiùn del correo entrante
	 * @param outgoing_verification Indica si hay autentificacion en el correo saliente
	 * @param outgoing_host Host del servidor de correo saliente
	 * @param outgoing_port Puerto del servidor de correo saliente
	 * @param outgoing_security Seguridad de conexiùn del correo saliente
	 * @param mail_username Nombre del usuario
	 * @param password Clave del usuario
	 * @param default_account Indica si es la cuenta de correo por defecto
	 * @param draft_folder Ruta de Borrador
	 * @param sent_folder Ruta de Enviados
	 * @param trash_folder Ruta de Papelera
	 * @param spam_folder Ruta de Spam
	 * @param display_name Mostrar como
	 * @param signature Identificador de la Firma
	 * @param user_id Identificador del Usuario
	 * @param type Tipo de la Cuenta de Correo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMail_account(String name, String email, String replyto_mail, String incoming_host, String protocol, Integer incoming_port, Short incoming_security, Boolean outgoing_verification, String outgoing_host, Integer outgoing_port, Short outgoing_security, String mail_username, String password, Boolean default_account, String draft_folder, String sent_folder, String trash_folder, String spam_folder, String display_name, Integer signature, Integer user_id, Short type)
	throws SQLException {
		Integer domain = getDomainForMail_account( signature , user_id);
		Integer id =  super.insertMail_account( domain != null ? domain : getDefaultDomain(), name, email, replyto_mail, incoming_host, protocol, incoming_port, incoming_security, outgoing_verification, outgoing_host, outgoing_port, outgoing_security, mail_username, password, default_account, draft_folder, sent_folder, trash_folder, spam_folder, display_name, signature, user_id, type );
		if ( domain != null ) { 
			mail_accountDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mail_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Cuenta de Correo
	 * @param email Cuenta de correo
	 * @param replyto_mail Email de Respuesta
	 * @param incoming_host Host del correo entrante
	 * @param protocol Protocolo utilizado (IMAP)
	 * @param incoming_port Puerto del correo entrante
	 * @param incoming_security Seguridad de conexiùn del correo entrante
	 * @param outgoing_verification Indica si hay autentificacion en el correo saliente
	 * @param outgoing_host Host del servidor de correo saliente
	 * @param outgoing_port Puerto del servidor de correo saliente
	 * @param outgoing_security Seguridad de conexiùn del correo saliente
	 * @param mail_username Nombre del usuario
	 * @param password Clave del usuario
	 * @param default_account Indica si es la cuenta de correo por defecto
	 * @param draft_folder Ruta de Borrador
	 * @param sent_folder Ruta de Enviados
	 * @param trash_folder Ruta de Papelera
	 * @param spam_folder Ruta de Spam
	 * @param display_name Mostrar como
	 * @param signature Identificador de la Firma
	 * @param user_id Identificador del Usuario
	 * @param type Tipo de la Cuenta de Correo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMail_account(Integer domain, String name, String email, String replyto_mail, String incoming_host, String protocol, Integer incoming_port, Short incoming_security, Boolean outgoing_verification, String outgoing_host, Integer outgoing_port, Short outgoing_security, String mail_username, String password, Boolean default_account, String draft_folder, String sent_folder, String trash_folder, String spam_folder, String display_name, Integer signature, Integer user_id, Short type)
	throws SQLException {
		Integer id =  super.insertMail_account(domain, name, email, replyto_mail, incoming_host, protocol, incoming_port, incoming_security, outgoing_verification, outgoing_host, outgoing_port, outgoing_security, mail_username, password, default_account, draft_folder, sent_folder, trash_folder, spam_folder, display_name, signature, user_id, type);
		mail_accountDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salary_costDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalary_costPk(Integer id){
		return salary_costDomains.get(id);
	}
	
	/**
	 * Salary_cost
	 * @param salary Recibo del pago de salarios
	 * @returns domain's ID
	*/
	protected Integer getDomainForSalary_cost( Integer salary){
		Integer domain = null;
			if ( ( domain = getDomainForSalaryPk( salary ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Salary_cost
	 * @param id Identificador unico
	 * @param salary Recibo del pago de salarios
	 * @param amount Importe
	 * @param description Descripcion
	 * @param type Tipo de deduccion Salarial
	 * @param cost_concept Codigo del concepto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_cost(Integer salary, Double amount, String description, Short type, String cost_concept)
	throws SQLException {
		Integer domain = getDomainForSalary_cost( salary);
		Integer id =  super.insertSalary_cost( domain != null ? domain : getDefaultDomain(), salary, amount, description, type, cost_concept );
		if ( domain != null ) { 
			salary_costDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Salary_cost
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param amount Importe
	 * @param description Descripcion
	 * @param type Tipo de deduccion Salarial
	 * @param cost_concept Codigo del concepto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSalary_cost(Integer domain, Integer salary, Double amount, String description, Short type, String cost_concept)
	throws SQLException {
		Integer id =  super.insertSalary_cost(domain, salary, amount, description, type, cost_concept);
		salary_costDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_attachPk(Integer id){
		return project_attachDomains.get(id);
	}
	
	/**
	 * Project_attach
	 * @param project Identificador del Proyecto
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_attach( Integer project){
		Integer domain = null;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_attach
	 * @param id Identificador unico
	 * @param project Identificador del Proyecto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param attach_type Tipo de Archivo Adjunto
	 * @param driveId 
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_attach(Integer project, Short mimeType, String description, Blob data, Short security_level, Date attach_date, Short attach_type, String driveId, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProject_attach( project);
		Integer id =  super.insertProject_attach( domain != null ? domain : getDefaultDomain(), project, mimeType, description, data, security_level, attach_date, attach_type, driveId, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			project_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param attach_type Tipo de Archivo Adjunto
	 * @param driveId 
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_attach(Integer domain, Integer project, Short mimeType, String description, Blob data, Short security_level, Date attach_date, Short attach_type, String driveId, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProject_attach(domain, project, mimeType, description, data, security_level, attach_date, attach_type, driveId, creation_user, creation_date, modification_user, modification_date);
		project_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> item_compositionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItem_compositionPk(Integer id){
		return item_compositionDomains.get(id);
	}
	
	/**
	 * Item_composition
	 * @param composition_item Identificador del Articulo componente
	 * @param item Identificador del Articulo compuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem_composition( Integer composition_item , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( composition_item ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item_composition
	 * @param id Identificador unico
	 * @param item Identificador del Articulo compuesto
	 * @param composition_item Identificador del Articulo componente
	 * @param sequence Numero de secuencia dentro de la Composicion
	 * @param description Descripcion del componente
	 * @param quantity Cantidad del componente
	 * @param discount_expr Descuentos del componente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_composition(Integer item, Integer composition_item, Integer sequence, String description, Double quantity, String discount_expr)
	throws SQLException {
		Integer domain = getDomainForItem_composition( composition_item , item);
		Integer id =  super.insertItem_composition( domain != null ? domain : getDefaultDomain(), item, composition_item, sequence, description, quantity, discount_expr );
		if ( domain != null ) { 
			item_compositionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item_composition
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param item Identificador del Articulo compuesto
	 * @param composition_item Identificador del Articulo componente
	 * @param sequence Numero de secuencia dentro de la Composicion
	 * @param description Descripcion del componente
	 * @param quantity Cantidad del componente
	 * @param discount_expr Descuentos del componente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_composition(Integer domain, Integer item, Integer composition_item, Integer sequence, String description, Double quantity, String discount_expr)
	throws SQLException {
		Integer id =  super.insertItem_composition(domain, item, composition_item, sequence, description, quantity, discount_expr);
		item_compositionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tariff_addinfoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTariff_addinfoPk(Integer id){
		return tariff_addinfoDomains.get(id);
	}
	
	/**
	 * Tariff_addinfo
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForTariff_addinfo( Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Tariff_addinfo
	 * @param id Identificador unico
	 * @param tariff Identificador de la Tarifa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff_addinfo(Integer tariff, String attribute, String value, Date value_date)
	throws SQLException {
		Integer domain = getDomainForTariff_addinfo( tariff);
		Integer id =  super.insertTariff_addinfo( domain != null ? domain : getDefaultDomain(), tariff, attribute, value, value_date );
		if ( domain != null ) { 
			tariff_addinfoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tariff_addinfo
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param tariff Identificador de la Tarifa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff_addinfo(Integer domain, Integer tariff, String attribute, String value, Date value_date)
	throws SQLException {
		Integer id =  super.insertTariff_addinfo(domain, tariff, attribute, value, value_date);
		tariff_addinfoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> academic_yearDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAcademic_yearPk(Integer id){
		return academic_yearDomains.get(id);
	}
	
	/**
	 * Academic_year
	 * @returns domain's ID
	*/
	protected Integer getDomainForAcademic_year(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Academic_year
	 * @param id Identificador unico del AÒo Academico
	 * @param description Descripcion del AÒo Academico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAcademic_year(String description)
	throws SQLException {
		Integer domain = getDomainForAcademic_year();
		Integer id =  super.insertAcademic_year( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			academic_yearDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Academic_year
	 * @param id Identificador unico del AÒo Academico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del AÒo Academico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAcademic_year(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertAcademic_year(domain, description);
		academic_yearDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> balanceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBalancePk(Integer id){
		return balanceDomains.get(id);
	}
	
	/**
	 * Balance
	 * @returns domain's ID
	*/
	protected Integer getDomainForBalance(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Balance
	 * @param id Identificador unico
	 * @param name Nombre del Balance
	 * @param removable Indica se puede ser borrado por el usuario
	 * @param type Tipo de Balance
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBalance(String name, Boolean removable, Short type)
	throws SQLException {
		Integer domain = getDomainForBalance();
		Integer id =  super.insertBalance( domain != null ? domain : getDefaultDomain(), name, removable, type );
		if ( domain != null ) { 
			balanceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Balance
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Balance
	 * @param removable Indica se puede ser borrado por el usuario
	 * @param type Tipo de Balance
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBalance(Integer domain, String name, Boolean removable, Short type)
	throws SQLException {
		Integer id =  super.insertBalance(domain, name, removable, type);
		balanceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_calendar_eventDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_calendar_eventPk(Integer id){
		return contract_calendar_eventDomains.get(id);
	}
	
	/**
	 * Contract_calendar_event
	 * @param contract Identificador del Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_calendar_event( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_calendar_event
	 * @param id Identificador unico
	 * @param contract Identificador del Contrato
	 * @param date Fecha de la incidencia
	 * @param type Tipo de incidencia
	 * @param duration Duracion de la incidencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_calendar_event(Integer contract, Date date, Short type, Double duration)
	throws SQLException {
		Integer domain = getDomainForContract_calendar_event( contract);
		Integer id =  super.insertContract_calendar_event( domain != null ? domain : getDefaultDomain(), contract, date, type, duration );
		if ( domain != null ) { 
			contract_calendar_eventDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_calendar_event
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del Contrato
	 * @param date Fecha de la incidencia
	 * @param type Tipo de incidencia
	 * @param duration Duracion de la incidencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_calendar_event(Integer domain, Integer contract, Date date, Short type, Double duration)
	throws SQLException {
		Integer id =  super.insertContract_calendar_event(domain, contract, date, type, duration);
		contract_calendar_eventDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> mk_actionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMk_actionPk(Integer id){
		return mk_actionDomains.get(id);
	}
	
	/**
	 * Mk_action
	 * @param campaign Identificador de la CampaÒa
	 * @param news Identificador de la Noticia
	 * @param newsletter Identificador del Boletin
	 * @param survey Identificador del Cuestionario
	 * @returns domain's ID
	*/
	protected Integer getDomainForMk_action( Integer campaign , Integer news , Integer newsletter , Integer survey){
		Integer domain = null;
			if ( ( domain = getDomainForMk_campaignPk( campaign ) ) != null )
				return domain;
			if ( ( domain = getDomainForNewsPk( news ) ) != null )
				return domain;
			if ( ( domain = getDomainForNewsletterPk( newsletter ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurveyPk( survey ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mk_action
	 * @param id Identificador unico
	 * @param campaign Identificador de la CampaÒa
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @param newsletter Identificador del Boletin
	 * @param description Descripcion de la Accion
	 * @param news Identificador de la Noticia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_action(Integer campaign, Integer media_type, Timestamp start_date, Timestamp end_date, Integer survey, Integer newsletter, String description, Integer news)
	throws SQLException {
		Integer domain = getDomainForMk_action( campaign , news , newsletter , survey);
		Integer id =  super.insertMk_action( domain != null ? domain : getDefaultDomain(), campaign, media_type, start_date, end_date, survey, newsletter, description, news );
		if ( domain != null ) { 
			mk_actionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mk_action
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la CampaÒa
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @param newsletter Identificador del Boletin
	 * @param description Descripcion de la Accion
	 * @param news Identificador de la Noticia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_action(Integer domain, Integer campaign, Integer media_type, Timestamp start_date, Timestamp end_date, Integer survey, Integer newsletter, String description, Integer news)
	throws SQLException {
		Integer id =  super.insertMk_action(domain, campaign, media_type, start_date, end_date, survey, newsletter, description, news);
		mk_actionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rrelationshipDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRrelationshipPk(Integer id){
		return rrelationshipDomains.get(id);
	}
	
	/**
	 * Rrelationship
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de RelaciÛn
	 * @returns domain's ID
	*/
	protected Integer getDomainForRrelationship( Integer registry , Integer related_registry , Integer relationship){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( related_registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForRelationshipPk( relationship ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rrelationship
	 * @param id Identificador unico de la Relacion
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de RelaciÛn
	 * @param comments Comentarios de la Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRrelationship(Integer registry, Integer related_registry, Integer relationship, String comments)
	throws SQLException {
		Integer domain = getDomainForRrelationship( registry , related_registry , relationship);
		Integer id =  super.insertRrelationship( domain != null ? domain : getDefaultDomain(), registry, related_registry, relationship, comments );
		if ( domain != null ) { 
			rrelationshipDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rrelationship
	 * @param id Identificador unico de la Relacion
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de RelaciÛn
	 * @param comments Comentarios de la Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRrelationship(Integer domain, Integer registry, Integer related_registry, Integer relationship, String comments)
	throws SQLException {
		Integer id =  super.insertRrelationship(domain, registry, related_registry, relationship, comments);
		rrelationshipDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> supplierDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSupplierPk(Integer registry){
		return supplierDomains.get(registry);
	}
	
	/**
	 * Supplier
	 * @param account Identificador de la Cuenta Contable
	 * @param registry Registro del Proveedor
	 * @param scope Identificador del Ambito
	 * @param tariff Tarifa asociada al Proveedor
	 * @returns domain's ID
	*/
	protected Integer getDomainForSupplier( Integer account , Integer registry , Integer scope , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Supplier
	 * @param registry Registro del Proveedor
	 * @param tariff Tarifa asociada al Proveedor
	 * @param withholding Indica si el Proveedor aplica retencion de impuestos
	 * @param withholding_farmer Indica si el Proveedor pertenece al Regimen Especial de Agricultura y Pesca
	 * @param vat_accrual_payment Indica si el Proveedor esta acogido al Regimen Especial de Criterio de Caja
	 * @param transaction Tipo de transacciones del Proveedor
	 * @param status Estado del Proveedor
	 * @param scope Identificador del Ambito
	 * @param purchase_valuated Indica si el Pedido se imprime valorado segun el Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertSupplier(Integer registry, Integer tariff, Boolean withholding, Boolean withholding_farmer, Boolean vat_accrual_payment, Short transaction, Short status, Integer scope, Boolean purchase_valuated, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForSupplier( account , registry , scope , tariff);
		 super.insertSupplier( registry, domain != null ? domain : getDefaultDomain(), tariff, withholding, withholding_farmer, vat_accrual_payment, transaction, status, scope, purchase_valuated, account, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			supplierDomains.put(registry, domain);
		}
	}

	/**
	 * Supplier
	 * @param registry Registro del Proveedor
	 * @param domain Identificador del Dominio
	 * @param tariff Tarifa asociada al Proveedor
	 * @param withholding Indica si el Proveedor aplica retencion de impuestos
	 * @param withholding_farmer Indica si el Proveedor pertenece al Regimen Especial de Agricultura y Pesca
	 * @param vat_accrual_payment Indica si el Proveedor esta acogido al Regimen Especial de Criterio de Caja
	 * @param transaction Tipo de transacciones del Proveedor
	 * @param status Estado del Proveedor
	 * @param scope Identificador del Ambito
	 * @param purchase_valuated Indica si el Pedido se imprime valorado segun el Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertSupplier(Integer registry, Integer domain, Integer tariff, Boolean withholding, Boolean withholding_farmer, Boolean vat_accrual_payment, Short transaction, Short status, Integer scope, Boolean purchase_valuated, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		 super.insertSupplier(registry, domain, tariff, withholding, withholding_farmer, vat_accrual_payment, transaction, status, scope, purchase_valuated, account, creation_user, creation_date, modification_user, modification_date);
		supplierDomains.put(registry, domain );
			}


	
	private Map<Integer,Integer> newsletter_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForNewsletter_detailPk(Integer id){
		return newsletter_detailDomains.get(id);
	}
	
	/**
	 * Newsletter_detail
	 * @param news Identificador de la Noticia
	 * @param newsletter Identificador del Boletin
	 * @returns domain's ID
	*/
	protected Integer getDomainForNewsletter_detail( Integer news , Integer newsletter){
		Integer domain = null;
			if ( ( domain = getDomainForNewsPk( news ) ) != null )
				return domain;
			if ( ( domain = getDomainForNewsletterPk( newsletter ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Newsletter_detail
	 * @param id Identificador unico
	 * @param news Identificador de la Noticia
	 * @param newsletter Identificador del Boletin
	 * @param position Posicion dentro del Boletin
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNewsletter_detail(Integer news, Integer newsletter, Integer position)
	throws SQLException {
		Integer domain = getDomainForNewsletter_detail( news , newsletter);
		Integer id =  super.insertNewsletter_detail( domain != null ? domain : getDefaultDomain(), news, newsletter, position );
		if ( domain != null ) { 
			newsletter_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Newsletter_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param news Identificador de la Noticia
	 * @param newsletter Identificador del Boletin
	 * @param position Posicion dentro del Boletin
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNewsletter_detail(Integer domain, Integer news, Integer newsletter, Integer position)
	throws SQLException {
		Integer id =  super.insertNewsletter_detail(domain, news, newsletter, position);
		newsletter_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> modelDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForModelPk(Integer id){
		return modelDomains.get(id);
	}
	
	/**
	 * Model
	 * @param make Identificador del Fabricante
	 * @returns domain's ID
	*/
	protected Integer getDomainForModel( Integer make){
		Integer domain = null;
			if ( ( domain = getDomainForMakePk( make ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Model
	 * @param id Identificador unico del Modelo
	 * @param make Identificador del Fabricante
	 * @param name Nombre del Modelo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertModel(Integer make, String name)
	throws SQLException {
		Integer domain = getDomainForModel( make);
		Integer id =  super.insertModel( domain != null ? domain : getDefaultDomain(), make, name );
		if ( domain != null ) { 
			modelDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Model
	 * @param id Identificador unico del Modelo
	 * @param domain Identificador del Dominio
	 * @param make Identificador del Fabricante
	 * @param name Nombre del Modelo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertModel(Integer domain, Integer make, String name)
	throws SQLException {
		Integer id =  super.insertModel(domain, make, name);
		modelDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> account_entry_invoiceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entry_invoicePk(Integer id){
		return account_entry_invoiceDomains.get(id);
	}
	
	/**
	 * Account_entry_invoice
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry_invoice( Integer account_entry , Integer invoice){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( invoice ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry_invoice
	 * @param id Identificador unico de Relacion
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_invoice(Integer account_entry, Integer invoice)
	throws SQLException {
		Integer domain = getDomainForAccount_entry_invoice( account_entry , invoice);
		Integer id =  super.insertAccount_entry_invoice( domain != null ? domain : getDefaultDomain(), account_entry, invoice );
		if ( domain != null ) { 
			account_entry_invoiceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry_invoice
	 * @param id Identificador unico de Relacion
	 * @param domain Identificador del Dominio
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_invoice(Integer domain, Integer account_entry, Integer invoice)
	throws SQLException {
		Integer id =  super.insertAccount_entry_invoice(domain, account_entry, invoice);
		account_entry_invoiceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rnoteDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRnotePk(Integer id){
		return rnoteDomains.get(id);
	}
	
	/**
	 * Rnote
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRnote( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rnote
	 * @param id Identificador unico de la Nota de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param description Descripcion de la Nota
	 * @param note_date Fecha de la Nota
	 * @param comments Comentarios de la Nota
	 * @param note_type 
	 * @param security_level Nivel de seguridad de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRnote(Integer registry, String description, Date note_date, String comments, Short note_type, Short security_level)
	throws SQLException {
		Integer domain = getDomainForRnote( registry);
		Integer id =  super.insertRnote( domain != null ? domain : getDefaultDomain(), registry, description, note_date, comments, note_type, security_level );
		if ( domain != null ) { 
			rnoteDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rnote
	 * @param id Identificador unico de la Nota de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param description Descripcion de la Nota
	 * @param note_date Fecha de la Nota
	 * @param comments Comentarios de la Nota
	 * @param note_type 
	 * @param security_level Nivel de seguridad de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRnote(Integer domain, Integer registry, String description, Date note_date, String comments, Short note_type, Short security_level)
	throws SQLException {
		Integer id =  super.insertRnote(domain, registry, description, note_date, comments, note_type, security_level);
		rnoteDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> task_holderDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTask_holderPk(Integer registry){
		return task_holderDomains.get(registry);
	}
	
	/**
	 * Task_holder
	 * @param cost_profile Identificador del Perfil de Costos
	 * @param registry Registro de la Entidad susceptible de Recibir Tareas
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask_holder( Integer cost_profile , Integer registry , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForCost_profilePk( cost_profile ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task_holder
	 * @param registry Registro de la Entidad susceptible de Recibir Tareas
	 * @param type Tipo de Entidad susceptible de Recibir Tareas
	 * @param active Indica si dicha Entidad esta activa o no
	 * @param user_id Identificador del Usuario
	 * @param cost_profile Identificador del Perfil de Costos
	 * @throws SQLException
	*/
	public void insertTask_holder(Integer registry, Short type, Boolean active, Integer user_id, Integer cost_profile)
	throws SQLException {
		Integer domain = getDomainForTask_holder( cost_profile , registry , user_id);
		 super.insertTask_holder( registry, domain != null ? domain : getDefaultDomain(), type, active, user_id, cost_profile );
		if ( domain != null ) { 
			task_holderDomains.put(registry, domain);
		}
	}

	/**
	 * Task_holder
	 * @param registry Registro de la Entidad susceptible de Recibir Tareas
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Entidad susceptible de Recibir Tareas
	 * @param active Indica si dicha Entidad esta activa o no
	 * @param user_id Identificador del Usuario
	 * @param cost_profile Identificador del Perfil de Costos
	 * @throws SQLException
	*/
	public void insertTask_holder(Integer registry, Integer domain, Short type, Boolean active, Integer user_id, Integer cost_profile)
	throws SQLException {
		 super.insertTask_holder(registry, domain, type, active, user_id, cost_profile);
		task_holderDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> geotreeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForGeotreePk(Integer id){
		return geotreeDomains.get(id);
	}
	
	/**
	 * Geotree
	 * @param child Identificador de la Zona Geografica Hijo
	 * @param parent Identificador de la Zona Geografica Padre
	 * @returns domain's ID
	*/
	protected Integer getDomainForGeotree( Integer child , Integer parent){
		Integer domain = null;
			if ( ( domain = getDomainForGeozonePk( child ) ) != null )
				return domain;
			if ( ( domain = getDomainForGeozonePk( parent ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Geotree
	 * @param id Identificador unico
	 * @param parent Identificador de la Zona Geografica Padre
	 * @param child Identificador de la Zona Geografica Hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertGeotree(Integer parent, Integer child)
	throws SQLException {
		Integer domain = getDomainForGeotree( child , parent);
		Integer id =  super.insertGeotree( domain != null ? domain : getDefaultDomain(), parent, child );
		if ( domain != null ) { 
			geotreeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Geotree
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param parent Identificador de la Zona Geografica Padre
	 * @param child Identificador de la Zona Geografica Hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertGeotree(Integer domain, Integer parent, Integer child)
	throws SQLException {
		Integer id =  super.insertGeotree(domain, parent, child);
		geotreeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItemPk(Integer id){
		return itemDomains.get(id);
	}
	
	/**
	 * Item
	 * @param product Identificador del Producto
	 * @param pack_format_tag Identificador de la Etiqueta de formato
	 * @param pack_measurement_tag Identificador de la Etiqueta de unidad de medida
	 * @param pack_units_tag Identificador de la Etiqueta de unidad de envase
	 * @param stock_unit_tag Identificador de la Etiqueta de unidad de Stock
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem( Integer product , Integer pack_format_tag , Integer pack_measurement_tag , Integer pack_units_tag , Integer stock_unit_tag){
		Integer domain = null;
			if ( ( domain = getDomainForProductPk( product ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( pack_format_tag ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( pack_measurement_tag ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( pack_units_tag ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( stock_unit_tag ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item
	 * @param id Identificador unico del Articulo
	 * @param product Identificador del Producto
	 * @param detail Detalle del Articulo
	 * @param detail2 Detalle 2 del Articulo
	 * @param detail3 Detalle 3 del Articulo
	 * @param description Descripcion del Articulo
	 * @param serial_number Numero de serie
	 * @param serial_date Fecha de serializacion
	 * @param price Precio del Articulo
	 * @param status Estado del Articulo
	 * @param expenses_percent Gastos porcentuales del Articulo
	 * @param expenses_fixed Gastos fijos del Articulo
	 * @param profit_percent Porcentaje de beneficio del Articulo
	 * @param purchase_price Precio de compra del Articulo
	 * @param internet Visible en internet
	 * @param barcode Codigo de barras del Articulo
	 * @param pack_format_tag Identificador de la Etiqueta de formato
	 * @param pack_units Numero de unidades por formato
	 * @param pack_units_tag Identificador de la Etiqueta de unidad de envase
	 * @param pack_measurement Medida envasada
	 * @param pack_measurement_tag Identificador de la Etiqueta de unidad de medida
	 * @param stock_unit_tag Identificador de la Etiqueta de unidad de Stock
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem(Integer product, String detail, String detail2, String detail3, String description, String serial_number, Date serial_date, Double price, Short status, Double expenses_percent, Double expenses_fixed, Double profit_percent, Double purchase_price, Boolean internet, String barcode, Integer pack_format_tag, Integer pack_units, Integer pack_units_tag, Double pack_measurement, Integer pack_measurement_tag, Integer stock_unit_tag, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForItem( product , pack_format_tag , pack_measurement_tag , pack_units_tag , stock_unit_tag);
		Integer id =  super.insertItem( domain != null ? domain : getDefaultDomain(), product, detail, detail2, detail3, description, serial_number, serial_date, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode, pack_format_tag, pack_units, pack_units_tag, pack_measurement, pack_measurement_tag, stock_unit_tag, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item
	 * @param id Identificador unico del Articulo
	 * @param domain Identificador del Dominio
	 * @param product Identificador del Producto
	 * @param detail Detalle del Articulo
	 * @param detail2 Detalle 2 del Articulo
	 * @param detail3 Detalle 3 del Articulo
	 * @param description Descripcion del Articulo
	 * @param serial_number Numero de serie
	 * @param serial_date Fecha de serializacion
	 * @param price Precio del Articulo
	 * @param status Estado del Articulo
	 * @param expenses_percent Gastos porcentuales del Articulo
	 * @param expenses_fixed Gastos fijos del Articulo
	 * @param profit_percent Porcentaje de beneficio del Articulo
	 * @param purchase_price Precio de compra del Articulo
	 * @param internet Visible en internet
	 * @param barcode Codigo de barras del Articulo
	 * @param pack_format_tag Identificador de la Etiqueta de formato
	 * @param pack_units Numero de unidades por formato
	 * @param pack_units_tag Identificador de la Etiqueta de unidad de envase
	 * @param pack_measurement Medida envasada
	 * @param pack_measurement_tag Identificador de la Etiqueta de unidad de medida
	 * @param stock_unit_tag Identificador de la Etiqueta de unidad de Stock
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem(Integer domain, Integer product, String detail, String detail2, String detail3, String description, String serial_number, Date serial_date, Double price, Short status, Double expenses_percent, Double expenses_fixed, Double profit_percent, Double purchase_price, Boolean internet, String barcode, Integer pack_format_tag, Integer pack_units, Integer pack_units_tag, Double pack_measurement, Integer pack_measurement_tag, Integer stock_unit_tag, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertItem(domain, product, detail, detail2, detail3, description, serial_number, serial_date, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode, pack_format_tag, pack_units, pack_units_tag, pack_measurement, pack_measurement_tag, stock_unit_tag, creation_user, creation_date, modification_user, modification_date);
		itemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rprofileDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRprofilePk(Integer id){
		return rprofileDomains.get(id);
	}
	
	/**
	 * Rprofile
	 * @param question Identificador de la Pregunta
	 * @param registry Identificador de Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRprofile( Integer question , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForQuestionPk( question ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rprofile
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param last_update Fecha de la ultima modificacion del Perfil del Cliente Potencial
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRprofile(Integer registry, Timestamp last_update, Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer domain = getDomainForRprofile( question , registry);
		Integer id =  super.insertRprofile( domain != null ? domain : getDefaultDomain(), registry, last_update, question, value_text, value_number, value_date );
		if ( domain != null ) { 
			rprofileDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rprofile
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param last_update Fecha de la ultima modificacion del Perfil del Cliente Potencial
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRprofile(Integer domain, Integer registry, Timestamp last_update, Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer id =  super.insertRprofile(domain, registry, last_update, question, value_text, value_number, value_date);
		rprofileDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> stop_sales_itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForStop_sales_itemPk(Integer id){
		return stop_sales_itemDomains.get(id);
	}
	
	/**
	 * Stop_sales_item
	 * @param item Identificador del Tipo de Habitacion
	 * @param stop_sales Identificador del Paro de ventas
	 * @returns domain's ID
	*/
	protected Integer getDomainForStop_sales_item( Integer item , Integer stop_sales){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForStop_salesPk( stop_sales ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Stop_sales_item
	 * @param id Identificador unico
	 * @param stop_sales Identificador del Paro de ventas
	 * @param item Identificador del Tipo de Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales_item(Integer stop_sales, Integer item)
	throws SQLException {
		Integer domain = getDomainForStop_sales_item( item , stop_sales);
		Integer id =  super.insertStop_sales_item( domain != null ? domain : getDefaultDomain(), stop_sales, item );
		if ( domain != null ) { 
			stop_sales_itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Stop_sales_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param stop_sales Identificador del Paro de ventas
	 * @param item Identificador del Tipo de Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales_item(Integer domain, Integer stop_sales, Integer item)
	throws SQLException {
		Integer id =  super.insertStop_sales_item(domain, stop_sales, item);
		stop_sales_itemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> cra_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCra_batchPk(Integer id){
		return cra_batchDomains.get(id);
	}
	
	/**
	 * Cra_batch
	 * @returns domain's ID
	*/
	protected Integer getDomainForCra_batch(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Cra_batch
	 * @param id Identificador unico
	 * @param date Fecha de creacion
	 * @param status Indica el estado de la Remesa
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCra_batch(Timestamp date, Short status, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer domain = getDomainForCra_batch();
		Integer id =  super.insertCra_batch( domain != null ? domain : getDefaultDomain(), date, status, communication_id, income_file, income_file_date, outcome_file, outcome_file_date );
		if ( domain != null ) { 
			cra_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Cra_batch
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha de creacion
	 * @param status Indica el estado de la Remesa
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCra_batch(Integer domain, Timestamp date, Short status, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer id =  super.insertCra_batch(domain, date, status, communication_id, income_file, income_file_date, outcome_file, outcome_file_date);
		cra_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contractDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContractPk(Integer id){
		return contractDomains.get(id);
	}
	
	/**
	 * Contract
	 * @param agreement_level 
	 * @param calendar Calendario
	 * @param enterprise_activity Actividad
	 * @param enterprise_ccc CCC
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract( Integer agreement_level , Integer calendar , Integer enterprise_activity , Integer enterprise_ccc , Integer person , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForAgreement_levelPk( agreement_level ) ) != null )
				return domain;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprise_activityPk( enterprise_activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprise_cccPk( enterprise_ccc ) ) != null )
				return domain;
			if ( ( domain = getDomainForPersonPk( person ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract
	 * @param id Identificador unico
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterprise_ccc CCC
	 * @param start_date Fecha de inicio del Contrato
	 * @param end_date Fecha de finalizacion del Contrato
	 * @param calendar Calendario
	 * @param description Descripcion
	 * @param sepe_status Estado de notificacion del contrato al SEPE
	 * @param registration N˙mero libro de matricula
	 * @param seniority_date Fecha de antiguedad
	 * @param enterprise_activity Actividad
	 * @param ss_regime Regimen de la Seguridad Social
	 * @param model Indica el modelo de documento del contrato
	 * @param category_description Categoria o grupo profesional
	 * @param ss_status Estado de notificacion del contrato a la Seguridad Social
	 * @param agreement_level 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract(Integer person, Integer workplace, Integer enterprise_ccc, Date start_date, Date end_date, Integer calendar, String description, Short sepe_status, Integer registration, Date seniority_date, Integer enterprise_activity, Short ss_regime, Short model, String category_description, Short ss_status, Integer agreement_level)
	throws SQLException {
		Integer domain = getDomainForContract( agreement_level , calendar , enterprise_activity , enterprise_ccc , person , workplace);
		Integer id =  super.insertContract( domain != null ? domain : getDefaultDomain(), person, workplace, enterprise_ccc, start_date, end_date, calendar, description, sepe_status, registration, seniority_date, enterprise_activity, ss_regime, model, category_description, ss_status, agreement_level );
		if ( domain != null ) { 
			contractDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterprise_ccc CCC
	 * @param start_date Fecha de inicio del Contrato
	 * @param end_date Fecha de finalizacion del Contrato
	 * @param calendar Calendario
	 * @param description Descripcion
	 * @param sepe_status Estado de notificacion del contrato al SEPE
	 * @param registration N˙mero libro de matricula
	 * @param seniority_date Fecha de antiguedad
	 * @param enterprise_activity Actividad
	 * @param ss_regime Regimen de la Seguridad Social
	 * @param model Indica el modelo de documento del contrato
	 * @param category_description Categoria o grupo profesional
	 * @param ss_status Estado de notificacion del contrato a la Seguridad Social
	 * @param agreement_level 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract(Integer domain, Integer person, Integer workplace, Integer enterprise_ccc, Date start_date, Date end_date, Integer calendar, String description, Short sepe_status, Integer registration, Date seniority_date, Integer enterprise_activity, Short ss_regime, Short model, String category_description, Short ss_status, Integer agreement_level)
	throws SQLException {
		Integer id =  super.insertContract(domain, person, workplace, enterprise_ccc, start_date, end_date, calendar, description, sepe_status, registration, seniority_date, enterprise_activity, ss_regime, model, category_description, ss_status, agreement_level);
		contractDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> data_response_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForData_response_detailPk(Integer id){
		return data_response_detailDomains.get(id);
	}
	
	/**
	 * Data_response_detail
	 * @param data_response Identificador de data_response
	 * @returns domain's ID
	*/
	protected Integer getDomainForData_response_detail( Integer data_response){
		Integer domain = null;
			if ( ( domain = getDomainForData_responsePk( data_response ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Data_response_detail
	 * @param id Identificador unico
	 * @param data_response Identificador de data_response
	 * @param data_variable Codigo de la variable
	 * @param data_value Valor de la variable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_response_detail(Integer data_response, String data_variable, String data_value, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForData_response_detail( data_response);
		Integer id =  super.insertData_response_detail( domain != null ? domain : getDefaultDomain(), data_response, data_variable, data_value, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			data_response_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Data_response_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param data_response Identificador de data_response
	 * @param data_variable Codigo de la variable
	 * @param data_value Valor de la variable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_response_detail(Integer domain, Integer data_response, String data_variable, String data_value, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertData_response_detail(domain, data_response, data_variable, data_value, creation_user, creation_date, modification_user, modification_date);
		data_response_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> irpf_regularizationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIrpf_regularizationPk(Integer id){
		return irpf_regularizationDomains.get(id);
	}
	
	/**
	 * Irpf_regularization
	 * @param contract Identificador del contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForIrpf_regularization( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Irpf_regularization
	 * @param id Identificador unico
	 * @param contract Identificador del contrato
	 * @param reason Causa de regularizaciÛn
	 * @param effective_date Fecha de entrada en vigor
	 * @param paid_irpf Retenciones practicadas con anterioridad a la regularizaciÛn.
	 * @param paid_remuneration Retribuciones ya satisfechas con anterioridad a la regularizaciÛn.
	 * @param prior_annual_irpf Retenciones anuales anteriores a la regularizaciÛn.
	 * @param prior_annual_remuneration Retribucines anulaes consideradas con anterioridad a la regularizaciÛn.
	 * @param prior_base_irpf Base para calcular el tipo de retenciÛn determinado antes de la regularizaciÛn.
	 * @param prior_irpf Tipo de retenciÛn aplicado antes de la regularizaciÛn.
	 * @param prior_in_ceuta_melilla Los rendimientos anteriores a la regularizaciÛn fueron obtenidos en Ceuta o Melilla
	 * @param prior_minimun_personal_family MÌnimo personal y familiar para calcular el tipo de retenciÛn determinado antes de la regularizaciÛn.
	 * @param prior_deduct_home_loan En alg˙n momento antes de la regularizaciÛn se aplico la minoraciÛn por pagos por la adquisiÛn o rehabilitaciÛn de la vivienda
	 * @param prior_deduct_home_loan_amount Importe de la minoraciÛn por pagos por la adquisiÛn o rehabilitaciÛn de la vivienda antes de la regularizaciÛn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_regularization(Integer contract, Short reason, Date effective_date, Double paid_irpf, Double paid_remuneration, Double prior_annual_irpf, Double prior_annual_remuneration, Double prior_base_irpf, Double prior_irpf, Boolean prior_in_ceuta_melilla, Double prior_minimun_personal_family, Short prior_deduct_home_loan, Double prior_deduct_home_loan_amount)
	throws SQLException {
		Integer domain = getDomainForIrpf_regularization( contract);
		Integer id =  super.insertIrpf_regularization( domain != null ? domain : getDefaultDomain(), contract, reason, effective_date, paid_irpf, paid_remuneration, prior_annual_irpf, prior_annual_remuneration, prior_base_irpf, prior_irpf, prior_in_ceuta_melilla, prior_minimun_personal_family, prior_deduct_home_loan, prior_deduct_home_loan_amount );
		if ( domain != null ) { 
			irpf_regularizationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Irpf_regularization
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del contrato
	 * @param reason Causa de regularizaciÛn
	 * @param effective_date Fecha de entrada en vigor
	 * @param paid_irpf Retenciones practicadas con anterioridad a la regularizaciÛn.
	 * @param paid_remuneration Retribuciones ya satisfechas con anterioridad a la regularizaciÛn.
	 * @param prior_annual_irpf Retenciones anuales anteriores a la regularizaciÛn.
	 * @param prior_annual_remuneration Retribucines anulaes consideradas con anterioridad a la regularizaciÛn.
	 * @param prior_base_irpf Base para calcular el tipo de retenciÛn determinado antes de la regularizaciÛn.
	 * @param prior_irpf Tipo de retenciÛn aplicado antes de la regularizaciÛn.
	 * @param prior_in_ceuta_melilla Los rendimientos anteriores a la regularizaciÛn fueron obtenidos en Ceuta o Melilla
	 * @param prior_minimun_personal_family MÌnimo personal y familiar para calcular el tipo de retenciÛn determinado antes de la regularizaciÛn.
	 * @param prior_deduct_home_loan En alg˙n momento antes de la regularizaciÛn se aplico la minoraciÛn por pagos por la adquisiÛn o rehabilitaciÛn de la vivienda
	 * @param prior_deduct_home_loan_amount Importe de la minoraciÛn por pagos por la adquisiÛn o rehabilitaciÛn de la vivienda antes de la regularizaciÛn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_regularization(Integer domain, Integer contract, Short reason, Date effective_date, Double paid_irpf, Double paid_remuneration, Double prior_annual_irpf, Double prior_annual_remuneration, Double prior_base_irpf, Double prior_irpf, Boolean prior_in_ceuta_melilla, Double prior_minimun_personal_family, Short prior_deduct_home_loan, Double prior_deduct_home_loan_amount)
	throws SQLException {
		Integer id =  super.insertIrpf_regularization(domain, contract, reason, effective_date, paid_irpf, paid_remuneration, prior_annual_irpf, prior_annual_remuneration, prior_base_irpf, prior_irpf, prior_in_ceuta_melilla, prior_minimun_personal_family, prior_deduct_home_loan, prior_deduct_home_loan_amount);
		irpf_regularizationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> account_entry_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entry_detailPk(Integer id){
		return account_entry_detailDomains.get(id);
	}
	
	/**
	 * Account_entry_detail
	 * @param account Cuenta Contable del Apunte
	 * @param account_entry Identificador del Asiento
	 * @param balancing_account Contrapartida del Apunte
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry_detail( Integer account , Integer account_entry , Integer balancing_account){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( balancing_account ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry_detail
	 * @param id Identificador unico del Apunte
	 * @param account_entry Identificador del Asiento
	 * @param line Numero de linea del Apunte dentro del Asiento
	 * @param account Cuenta Contable del Apunte
	 * @param concept Concepto del Apunte
	 * @param balancing_account Contrapartida del Apunte
	 * @param debit Debe del Apunte
	 * @param credit Haber del Apunte
	 * @param document_number Numero de documento asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_detail(Integer account_entry, Integer line, Integer account, String concept, Integer balancing_account, Double debit, Double credit, String document_number, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForAccount_entry_detail( account , account_entry , balancing_account);
		Integer id =  super.insertAccount_entry_detail( domain != null ? domain : getDefaultDomain(), account_entry, line, account, concept, balancing_account, debit, credit, document_number, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			account_entry_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry_detail
	 * @param id Identificador unico del Apunte
	 * @param domain Identificador del Dominio
	 * @param account_entry Identificador del Asiento
	 * @param line Numero de linea del Apunte dentro del Asiento
	 * @param account Cuenta Contable del Apunte
	 * @param concept Concepto del Apunte
	 * @param balancing_account Contrapartida del Apunte
	 * @param debit Debe del Apunte
	 * @param credit Haber del Apunte
	 * @param document_number Numero de documento asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_detail(Integer domain, Integer account_entry, Integer line, Integer account, String concept, Integer balancing_account, Double debit, Double credit, String document_number, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertAccount_entry_detail(domain, account_entry, line, account, concept, balancing_account, debit, credit, document_number, creation_user, creation_date, modification_user, modification_date);
		account_entry_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> geozoneDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForGeozonePk(Integer id){
		return geozoneDomains.get(id);
	}
	
	/**
	 * Geozone
	 * @returns domain's ID
	*/
	protected Integer getDomainForGeozone(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Geozone
	 * @param id Identificador unico de la Zona Geografica
	 * @param name Nombre de la Zona Geografica
	 * @param code Codigo de la Zona Geografica
	 * @param system Indica si es una Zona Geografica del sistema
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertGeozone(String name, String code, Boolean system)
	throws SQLException {
		Integer domain = getDomainForGeozone();
		Integer id =  super.insertGeozone( domain != null ? domain : getDefaultDomain(), name, code, system );
		if ( domain != null ) { 
			geozoneDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Geozone
	 * @param id Identificador unico de la Zona Geografica
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Zona Geografica
	 * @param code Codigo de la Zona Geografica
	 * @param system Indica si es una Zona Geografica del sistema
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertGeozone(Integer domain, String name, String code, Boolean system)
	throws SQLException {
		Integer id =  super.insertGeozone(domain, name, code, system);
		geozoneDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> system_paymentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSystem_paymentPk(Integer id){
		return system_paymentDomains.get(id);
	}
	
	/**
	 * System_payment
	 * @param payment_concept Identificador unico del concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForSystem_payment( Integer payment_concept){
		Integer domain = null;
			if ( ( domain = getDomainForPayment_conceptPk( payment_concept ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * System_payment
	 * @param id Identificador unico
	 * @param type Tipo de PercepciÛn Salarial
	 * @param payment_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression 
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @param start_date Fecha de inicio
	 * @param month Mes de la percepcion
	 * @param end_date Fecha de finalizacion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_payment(Short type, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		Integer domain = getDomainForSystem_payment( payment_concept);
		Integer id =  super.insertSystem_payment( domain != null ? domain : getDefaultDomain(), type, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type );
		if ( domain != null ) { 
			system_paymentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * System_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de PercepciÛn Salarial
	 * @param payment_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression 
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @param start_date Fecha de inicio
	 * @param month Mes de la percepcion
	 * @param end_date Fecha de finalizacion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_payment(Integer domain, Short type, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		Integer id =  super.insertSystem_payment(domain, type, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type);
		system_paymentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> workplace_departmentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWorkplace_departmentPk(Integer id){
		return workplace_departmentDomains.get(id);
	}
	
	/**
	 * Workplace_department
	 * @param catalogue Identificador del Catalogo
	 * @param department Identificador del Departamento
	 * @param warehouse Identificador del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForWorkplace_department( Integer catalogue , Integer department , Integer warehouse , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCataloguePk( catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForDepartmentPk( department ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Workplace_department
	 * @param id Identificador unico
	 * @param workplace Identificador del Centro de Trabajo
	 * @param department Identificador del Departamento
	 * @param warehouse Identificador del Almacen
	 * @param catalogue Identificador del Catalogo
	 * @param active Indica si el Departamento esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkplace_department(Integer workplace, Integer department, Integer warehouse, Integer catalogue, Boolean active)
	throws SQLException {
		Integer domain = getDomainForWorkplace_department( catalogue , department , warehouse , workplace);
		Integer id =  super.insertWorkplace_department( domain != null ? domain : getDefaultDomain(), workplace, department, warehouse, catalogue, active );
		if ( domain != null ) { 
			workplace_departmentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Workplace_department
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param workplace Identificador del Centro de Trabajo
	 * @param department Identificador del Departamento
	 * @param warehouse Identificador del Almacen
	 * @param catalogue Identificador del Catalogo
	 * @param active Indica si el Departamento esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkplace_department(Integer domain, Integer workplace, Integer department, Integer warehouse, Integer catalogue, Boolean active)
	throws SQLException {
		Integer id =  super.insertWorkplace_department(domain, workplace, department, warehouse, catalogue, active);
		workplace_departmentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model200_registryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model200_registryPk(Integer id){
		return fs_model200_registryDomains.get(id);
	}
	
	/**
	 * Fs_model200_registry
	 * @param fs_model200 Identificador del modelo 200
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model200_registry( Integer fs_model200){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model200Pk( fs_model200 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model200_registry
	 * @param id Identificador unico
	 * @param fs_model200 Identificador del modelo 200
	 * @param document NIF
	 * @param name Nombre
	 * @param province Provincia
	 * @param country Pais
	 * @param residence Residencia
	 * @param representative Representante
	 * @param type Tipo. Administrador/participacion 
	 * @param percent Porcentaje
	 * @param nominal_value Valor Nominal
	 * @param book_value Valor en libros
	 * @param incomes Ingresos por dividendos
	 * @param a_value Correccion de valor
	 * @param b_value Reversion por perdidas
	 * @param c_value Efecto de la correccion
	 * @param d_value Saldo de correcciones
	 * @param capital Capital
	 * @param reserve Reservas
	 * @param other_amounts Otras partidas
	 * @param result Resultado del ultimo ejercicio
	 * @param notary Notaria
	 * @param notary_date Fecha Notaria
	 * @param cc_value Eliminacion del deterioro contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200_registry(Integer fs_model200, String document, String name, Boolean province, String country, String residence, Boolean representative, Boolean type, Double percent, Double nominal_value, Double book_value, Double incomes, Double a_value, Double b_value, Double c_value, Double d_value, Double capital, Double reserve, Double other_amounts, Double result, String notary, Date notary_date, Double cc_value)
	throws SQLException {
		Integer domain = getDomainForFs_model200_registry( fs_model200);
		Integer id =  super.insertFs_model200_registry( domain != null ? domain : getDefaultDomain(), fs_model200, document, name, province, country, residence, representative, type, percent, nominal_value, book_value, incomes, a_value, b_value, c_value, d_value, capital, reserve, other_amounts, result, notary, notary_date, cc_value );
		if ( domain != null ) { 
			fs_model200_registryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model200_registry
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model200 Identificador del modelo 200
	 * @param document NIF
	 * @param name Nombre
	 * @param province Provincia
	 * @param country Pais
	 * @param residence Residencia
	 * @param representative Representante
	 * @param type Tipo. Administrador/participacion 
	 * @param percent Porcentaje
	 * @param nominal_value Valor Nominal
	 * @param book_value Valor en libros
	 * @param incomes Ingresos por dividendos
	 * @param a_value Correccion de valor
	 * @param b_value Reversion por perdidas
	 * @param c_value Efecto de la correccion
	 * @param d_value Saldo de correcciones
	 * @param capital Capital
	 * @param reserve Reservas
	 * @param other_amounts Otras partidas
	 * @param result Resultado del ultimo ejercicio
	 * @param notary Notaria
	 * @param notary_date Fecha Notaria
	 * @param cc_value Eliminacion del deterioro contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200_registry(Integer domain, Integer fs_model200, String document, String name, Boolean province, String country, String residence, Boolean representative, Boolean type, Double percent, Double nominal_value, Double book_value, Double incomes, Double a_value, Double b_value, Double c_value, Double d_value, Double capital, Double reserve, Double other_amounts, Double result, String notary, Date notary_date, Double cc_value)
	throws SQLException {
		Integer id =  super.insertFs_model200_registry(domain, fs_model200, document, name, province, country, residence, representative, type, percent, nominal_value, book_value, incomes, a_value, b_value, c_value, d_value, capital, reserve, other_amounts, result, notary, notary_date, cc_value);
		fs_model200_registryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fbatchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFbatchPk(Integer id){
		return fbatchDomains.get(id);
	}
	
	/**
	 * Fbatch
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param rbank Banco de la CompaÒia utilizado en la Remesa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFbatch( Integer bank_statement_link , Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForBank_statement_linkPk( bank_statement_link ) ) != null )
				return domain;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fbatch
	 * @param id Identificador unico de la Remesa
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la CompaÒia utilizado en la Remesa
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @param rattach Identificador del Archivo Adjunto
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFbatch(String description, Date issue_date, Short type, Short status, Integer rbank, Integer bank_statement_link, Boolean payment, Short security_level, Integer rattach, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFbatch( bank_statement_link , rbank);
		Integer id =  super.insertFbatch( domain != null ? domain : getDefaultDomain(), description, issue_date, type, status, rbank, bank_statement_link, payment, security_level, rattach, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fbatchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fbatch
	 * @param id Identificador unico de la Remesa
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la CompaÒia utilizado en la Remesa
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @param rattach Identificador del Archivo Adjunto
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFbatch(Integer domain, String description, Date issue_date, Short type, Short status, Integer rbank, Integer bank_statement_link, Boolean payment, Short security_level, Integer rattach, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFbatch(domain, description, issue_date, type, status, rbank, bank_statement_link, payment, security_level, rattach, creation_user, creation_date, modification_user, modification_date);
		fbatchDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> favorite_categoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFavorite_categoryPk(Integer id){
		return favorite_categoryDomains.get(id);
	}
	
	/**
	 * Favorite_category
	 * @param user_id Usuario al que pertenece la Categoria
	 * @returns domain's ID
	*/
	protected Integer getDomainForFavorite_category( Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Favorite_category
	 * @param id Identificador unico
	 * @param description Descripcion de la Categoria
	 * @param user_id Usuario al que pertenece la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFavorite_category(String description, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForFavorite_category( user_id);
		Integer id =  super.insertFavorite_category( domain != null ? domain : getDefaultDomain(), description, user_id );
		if ( domain != null ) { 
			favorite_categoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Favorite_category
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Categoria
	 * @param user_id Usuario al que pertenece la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFavorite_category(Integer domain, String description, Integer user_id)
	throws SQLException {
		Integer id =  super.insertFavorite_category(domain, description, user_id);
		favorite_categoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invest_assetDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvest_assetPk(Integer id){
		return invest_assetDomains.get(id);
	}
	
	/**
	 * Invest_asset
	 * @param activity Identificador de la Actividad
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvest_asset( Integer activity){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprise_activityPk( activity ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invest_asset
	 * @param id Identificador unico
	 * @param activity Identificador de la Actividad
	 * @param description Descripcion del Bien
	 * @param type Tipo de Bien
	 * @param regime Regimen
	 * @param start_date Fecha de alta
	 * @param end_date Fecha de baja
	 * @param vat_percent Porcentaje de afectacion de IVA
	 * @param retention_percent Porcentaje de afectacion de imposicion directa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvest_asset(Integer activity, String description, Short type, Short regime, Date start_date, Date end_date, Double vat_percent, Double retention_percent)
	throws SQLException {
		Integer domain = getDomainForInvest_asset( activity);
		Integer id =  super.insertInvest_asset( domain != null ? domain : getDefaultDomain(), activity, description, type, regime, start_date, end_date, vat_percent, retention_percent );
		if ( domain != null ) { 
			invest_assetDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invest_asset
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param activity Identificador de la Actividad
	 * @param description Descripcion del Bien
	 * @param type Tipo de Bien
	 * @param regime Regimen
	 * @param start_date Fecha de alta
	 * @param end_date Fecha de baja
	 * @param vat_percent Porcentaje de afectacion de IVA
	 * @param retention_percent Porcentaje de afectacion de imposicion directa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvest_asset(Integer domain, Integer activity, String description, Short type, Short regime, Date start_date, Date end_date, Double vat_percent, Double retention_percent)
	throws SQLException {
		Integer id =  super.insertInvest_asset(domain, activity, description, type, regime, start_date, end_date, vat_percent, retention_percent);
		invest_assetDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> data_responseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForData_responsePk(Integer id){
		return data_responseDomains.get(id);
	}
	
	/**
	 * Data_response
	 * @returns domain's ID
	*/
	protected Integer getDomainForData_response(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Data_response
	 * @param id Identificador unico
	 * @param code Codigo de referencia
	 * @param response_date Fecha
	 * @param source Origen
	 * @param source_id Identificador del Origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_response(String code, Date response_date, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForData_response();
		Integer id =  super.insertData_response( domain != null ? domain : getDefaultDomain(), code, response_date, source, source_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			data_responseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Data_response
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo de referencia
	 * @param response_date Fecha
	 * @param source Origen
	 * @param source_id Identificador del Origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertData_response(Integer domain, String code, Date response_date, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertData_response(domain, code, response_date, source, source_id, creation_user, creation_date, modification_user, modification_date);
		data_responseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> domain_application_moduleDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDomain_application_modulePk(Integer id){
		return domain_application_moduleDomains.get(id);
	}
	
	/**
	 * Domain_application_module
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @returns domain's ID
	*/
	protected Integer getDomainForDomain_application_module( Integer domain_application){
		Integer domain = null;
			if ( ( domain = getDomainForDomain_applicationPk( domain_application ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Domain_application_module
	 * @param id Identificador unico
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @param module Modulo de la Aplicacion del Dominio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDomain_application_module(Integer domain_application, Short module)
	throws SQLException {
		Integer domain = getDomainForDomain_application_module( domain_application);
		Integer id =  super.insertDomain_application_module( domain != null ? domain : getDefaultDomain(), domain_application, module );
		if ( domain != null ) { 
			domain_application_moduleDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Domain_application_module
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @param module Modulo de la Aplicacion del Dominio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDomain_application_module(Integer domain, Integer domain_application, Short module)
	throws SQLException {
		Integer id =  super.insertDomain_application_module(domain, domain_application, module);
		domain_application_moduleDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_taxDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_taxPk(Integer id){
		return invoice_taxDomains.get(id);
	}
	
	/**
	 * Invoice_tax
	 * @param invoice_detail Identificador del Detalle de la Factura
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_tax( Integer invoice_detail){
		Integer domain = null;
			if ( ( domain = getDomainForInvoice_detailPk( invoice_detail ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_tax
	 * @param id Identificador unico del Impuesto de la Factura
	 * @param invoice_detail Identificador del Detalle de la Factura
	 * @param tax_type Tipo de Impuesto del Detalle de la Factura
	 * @param base Base Imponible de Impuesto del Detalle de la Factura
	 * @param percentage Porcentaje de Impuesto del Detalle de la Factura
	 * @param surcharge Porcentaje del recargo de equivalencia del Detalle de la Factura
	 * @param quota Cuota de Impuesto del Detalle de la Factura
	 * @param surcharge_quota Cuota de recargo de equivalencia del Detalle de la Factura
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param deductible_percent Porcentaje de deducibilidad
	 * @param deductible_quota Cuota deducible
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_tax(Integer invoice_detail, Short tax_type, Double base, Double percentage, Double surcharge, Double quota, Double surcharge_quota, Short vat_deduction_type, Short withholding_type, Double deductible_percent, Double deductible_quota)
	throws SQLException {
		Integer domain = getDomainForInvoice_tax( invoice_detail);
		Integer id =  super.insertInvoice_tax( domain != null ? domain : getDefaultDomain(), invoice_detail, tax_type, base, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_percent, deductible_quota );
		if ( domain != null ) { 
			invoice_taxDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_tax
	 * @param id Identificador unico del Impuesto de la Factura
	 * @param domain Identificador del Dominio
	 * @param invoice_detail Identificador del Detalle de la Factura
	 * @param tax_type Tipo de Impuesto del Detalle de la Factura
	 * @param base Base Imponible de Impuesto del Detalle de la Factura
	 * @param percentage Porcentaje de Impuesto del Detalle de la Factura
	 * @param surcharge Porcentaje del recargo de equivalencia del Detalle de la Factura
	 * @param quota Cuota de Impuesto del Detalle de la Factura
	 * @param surcharge_quota Cuota de recargo de equivalencia del Detalle de la Factura
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param deductible_percent Porcentaje de deducibilidad
	 * @param deductible_quota Cuota deducible
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_tax(Integer domain, Integer invoice_detail, Short tax_type, Double base, Double percentage, Double surcharge, Double quota, Double surcharge_quota, Short vat_deduction_type, Short withholding_type, Double deductible_percent, Double deductible_quota)
	throws SQLException {
		Integer id =  super.insertInvoice_tax(domain, invoice_detail, tax_type, base, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_percent, deductible_quota);
		invoice_taxDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> item_alternativeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItem_alternativePk(Integer id){
		return item_alternativeDomains.get(id);
	}
	
	/**
	 * Item_alternative
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param item Identificador de Articulo
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem_alternative( Integer alternative_item , Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( alternative_item ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item_alternative
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param priority Prioridad del Articulo Alternativo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_alternative(Integer item, Integer alternative_item, Short priority)
	throws SQLException {
		Integer domain = getDomainForItem_alternative( alternative_item , item);
		Integer id =  super.insertItem_alternative( domain != null ? domain : getDefaultDomain(), item, alternative_item, priority );
		if ( domain != null ) { 
			item_alternativeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item_alternative
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param item Identificador de Articulo
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param priority Prioridad del Articulo Alternativo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_alternative(Integer domain, Integer item, Integer alternative_item, Short priority)
	throws SQLException {
		Integer id =  super.insertItem_alternative(domain, item, alternative_item, priority);
		item_alternativeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fan_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFan_batchPk(Integer id){
		return fan_batchDomains.get(id);
	}
	
	/**
	 * Fan_batch
	 * @returns domain's ID
	*/
	protected Integer getDomainForFan_batch(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Fan_batch
	 * @param id Identificador unico de la remesa
	 * @param date Fecha de creacion
	 * @param status Indica el estado de la remesa
	 * @param liquidation_type Indica el tipo de liquidacion
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFan_batch(Timestamp date, Short status, Short liquidation_type, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer domain = getDomainForFan_batch();
		Integer id =  super.insertFan_batch( domain != null ? domain : getDefaultDomain(), date, status, liquidation_type, communication_id, income_file, income_file_date, outcome_file, outcome_file_date );
		if ( domain != null ) { 
			fan_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fan_batch
	 * @param id Identificador unico de la remesa
	 * @param domain Identificador del Dominio
	 * @param date Fecha de creacion
	 * @param status Indica el estado de la remesa
	 * @param liquidation_type Indica el tipo de liquidacion
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFan_batch(Integer domain, Timestamp date, Short status, Short liquidation_type, String communication_id, Blob income_file, Timestamp income_file_date, Blob outcome_file, Timestamp outcome_file_date)
	throws SQLException {
		Integer id =  super.insertFan_batch(domain, date, status, liquidation_type, communication_id, income_file, income_file_date, outcome_file, outcome_file_date);
		fan_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> favoriteDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFavoritePk(Integer id){
		return favoriteDomains.get(id);
	}
	
	/**
	 * Favorite
	 * @param favorite_category Categoria a la que pertenece el Favorito
	 * @param user_id Usuario al que pertenece el Favorito
	 * @returns domain's ID
	*/
	protected Integer getDomainForFavorite( Integer favorite_category , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForFavorite_categoryPk( favorite_category ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Favorite
	 * @param id Identificador unico de Favorito
	 * @param favorite_category Categoria a la que pertenece el Favorito
	 * @param description Descripcion del Favorito
	 * @param url Url del Favorito
	 * @param user_id Usuario al que pertenece el Favorito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFavorite(Integer favorite_category, String description, String url, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForFavorite( favorite_category , user_id);
		Integer id =  super.insertFavorite( domain != null ? domain : getDefaultDomain(), favorite_category, description, url, user_id );
		if ( domain != null ) { 
			favoriteDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Favorite
	 * @param id Identificador unico de Favorito
	 * @param domain Identificador del Dominio
	 * @param favorite_category Categoria a la que pertenece el Favorito
	 * @param description Descripcion del Favorito
	 * @param url Url del Favorito
	 * @param user_id Usuario al que pertenece el Favorito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFavorite(Integer domain, Integer favorite_category, String description, String url, Integer user_id)
	throws SQLException {
		Integer id =  super.insertFavorite(domain, favorite_category, description, url, user_id);
		favoriteDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> bank_statement_linkDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBank_statement_linkPk(Integer id){
		return bank_statement_linkDomains.get(id);
	}
	
	/**
	 * Bank_statement_link
	 * @param bank_statement Identificador de Extracto bancario
	 * @param linked_bank_statement_link Identificador del Enlace de Extracto bancario asociado
	 * @returns domain's ID
	*/
	protected Integer getDomainForBank_statement_link( Integer bank_statement , Integer linked_bank_statement_link){
		Integer domain = null;
			if ( ( domain = getDomainForBank_statementPk( bank_statement ) ) != null )
				return domain;
			if ( ( domain = getDomainForBank_statement_linkPk( linked_bank_statement_link ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Bank_statement_link
	 * @param id Identificador unico
	 * @param bank_statement Identificador de Extracto bancario
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param source_date Fecha del origen
	 * @param amount Importe
	 * @param status Estado
	 * @param linked_bank_statement_link Identificador del Enlace de Extracto bancario asociado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_statement_link(Integer bank_statement, Short source, Integer source_id, Date source_date, Double amount, Short status, Integer linked_bank_statement_link)
	throws SQLException {
		Integer domain = getDomainForBank_statement_link( bank_statement , linked_bank_statement_link);
		Integer id =  super.insertBank_statement_link( domain != null ? domain : getDefaultDomain(), bank_statement, source, source_id, source_date, amount, status, linked_bank_statement_link );
		if ( domain != null ) { 
			bank_statement_linkDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Bank_statement_link
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param bank_statement Identificador de Extracto bancario
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param source_date Fecha del origen
	 * @param amount Importe
	 * @param status Estado
	 * @param linked_bank_statement_link Identificador del Enlace de Extracto bancario asociado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_statement_link(Integer domain, Integer bank_statement, Short source, Integer source_id, Date source_date, Double amount, Short status, Integer linked_bank_statement_link)
	throws SQLException {
		Integer id =  super.insertBank_statement_link(domain, bank_statement, source, source_id, source_date, amount, status, linked_bank_statement_link);
		bank_statement_linkDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commission_type_commissionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommission_type_commissionPk(Integer id){
		return commission_type_commissionDomains.get(id);
	}
	
	/**
	 * Commission_type_commission
	 * @param commission Identificador de la Comision
	 * @param commission_type Identificador del Tipo de Comision
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommission_type_commission( Integer commission , Integer commission_type){
		Integer domain = null;
			if ( ( domain = getDomainForCommissionPk( commission ) ) != null )
				return domain;
			if ( ( domain = getDomainForCommission_typePk( commission_type ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Commission_type_commission
	 * @param id Identificador unico
	 * @param commission_type Identificador del Tipo de Comision
	 * @param commission Identificador de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_type_commission(Integer commission_type, Integer commission)
	throws SQLException {
		Integer domain = getDomainForCommission_type_commission( commission , commission_type);
		Integer id =  super.insertCommission_type_commission( domain != null ? domain : getDefaultDomain(), commission_type, commission );
		if ( domain != null ) { 
			commission_type_commissionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commission_type_commission
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param commission_type Identificador del Tipo de Comision
	 * @param commission Identificador de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_type_commission(Integer domain, Integer commission_type, Integer commission)
	throws SQLException {
		Integer id =  super.insertCommission_type_commission(domain, commission_type, commission);
		commission_type_commissionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commercial_trackingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommercial_trackingPk(Integer id){
		return commercial_trackingDomains.get(id);
	}
	
	/**
	 * Commercial_tracking
	 * @param activity Identificador de la Actividad Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @param project_commercial Identificador del Proyecto
	 * @param seller Identificador del Comercial
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommercial_tracking( Integer activity , Integer next_commercial_tracking , Integer offer , Integer project_commercial , Integer seller){
		Integer domain = null;
			if ( ( domain = getDomainForCommercial_activityPk( activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForCommercial_trackingPk( next_commercial_tracking ) ) != null )
				return domain;
			if ( ( domain = getDomainForOfferPk( offer ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_commercialPk( project_commercial ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Commercial_tracking
	 * @param id Identificador unico
	 * @param date Fecha del Seguimiento Comercial
	 * @param seller Identificador del Comercial
	 * @param project_commercial Identificador del Proyecto
	 * @param activity Identificador de la Actividad Comercial
	 * @param comments Comentarios del Seguimiento Comercial
	 * @param status Estado del Seguimiento Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param end_date Fecha de cierre del Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @param allDay Indica si el Seguimiento Comercial dura todo el dia
	 * @param location Ubicacion del Seguimiento Comercial
	 * @param eventId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_tracking(Timestamp date, Integer seller, Integer project_commercial, Integer activity, String comments, Short status, Integer next_commercial_tracking, Timestamp end_date, Integer offer, Boolean allDay, String location, String eventId)
	throws SQLException {
		Integer domain = getDomainForCommercial_tracking( activity , next_commercial_tracking , offer , project_commercial , seller);
		Integer id =  super.insertCommercial_tracking( domain != null ? domain : getDefaultDomain(), date, seller, project_commercial, activity, comments, status, next_commercial_tracking, end_date, offer, allDay, location, eventId );
		if ( domain != null ) { 
			commercial_trackingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commercial_tracking
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha del Seguimiento Comercial
	 * @param seller Identificador del Comercial
	 * @param project_commercial Identificador del Proyecto
	 * @param activity Identificador de la Actividad Comercial
	 * @param comments Comentarios del Seguimiento Comercial
	 * @param status Estado del Seguimiento Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param end_date Fecha de cierre del Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @param allDay Indica si el Seguimiento Comercial dura todo el dia
	 * @param location Ubicacion del Seguimiento Comercial
	 * @param eventId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_tracking(Integer domain, Timestamp date, Integer seller, Integer project_commercial, Integer activity, String comments, Short status, Integer next_commercial_tracking, Timestamp end_date, Integer offer, Boolean allDay, String location, String eventId)
	throws SQLException {
		Integer id =  super.insertCommercial_tracking(domain, date, seller, project_commercial, activity, comments, status, next_commercial_tracking, end_date, offer, allDay, location, eventId);
		commercial_trackingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> certifica2_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCertifica2_batch_detailPk(Integer id){
		return certifica2_batch_detailDomains.get(id);
	}
	
	/**
	 * Certifica2_batch_detail
	 * @param certifica2_batch Identificador unico del certificado de empresa
	 * @param contract Identificador unico del contrato de empleado
	 * @returns domain's ID
	*/
	protected Integer getDomainForCertifica2_batch_detail( Integer certifica2_batch , Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForCertifica2_batchPk( certifica2_batch ) ) != null )
				return domain;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Certifica2_batch_detail
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param certifica2_batch Identificador unico del certificado de empresa
	 * @param contract Identificador unico del contrato de empleado
	 * @param suspension_cause_code Codigo causa suspension
	 * @param status Estado de la linea de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCertifica2_batch_detail(Integer certifica2_batch, Integer contract, String suspension_cause_code, Short status)
	throws SQLException {
		Integer domain = getDomainForCertifica2_batch_detail( certifica2_batch , contract);
		Integer id =  super.insertCertifica2_batch_detail( domain != null ? domain : getDefaultDomain(), certifica2_batch, contract, suspension_cause_code, status );
		if ( domain != null ) { 
			certifica2_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Certifica2_batch_detail
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param domain Identificador del Dominio
	 * @param certifica2_batch Identificador unico del certificado de empresa
	 * @param contract Identificador unico del contrato de empleado
	 * @param suspension_cause_code Codigo causa suspension
	 * @param status Estado de la linea de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCertifica2_batch_detail(Integer domain, Integer certifica2_batch, Integer contract, String suspension_cause_code, Short status)
	throws SQLException {
		Integer id =  super.insertCertifica2_batch_detail(domain, certifica2_batch, contract, suspension_cause_code, status);
		certifica2_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_batch_detailPk(Integer id){
		return contract_batch_detailDomains.get(id);
	}
	
	/**
	 * Contract_batch_detail
	 * @param contract Identificador unico del contrato
	 * @param contract_batch Identificador unico de la remesa de contratos
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_batch_detail( Integer contract , Integer contract_batch){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForContract_batchPk( contract_batch ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_batch_detail
	 * @param id Identificador unico del detalle de la remesa
	 * @param contract_batch Identificador unico de la remesa de contratos
	 * @param contract Identificador unico del contrato
	 * @param status Estado de la linea de la remesa
	 * @param action_type Tipo de accion a realizar
	 * @param leave_type Tipo de baja
	 * @param real_date Fecha real de la accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_batch_detail(Integer contract_batch, Integer contract, Short status, String action_type, String leave_type, Timestamp real_date)
	throws SQLException {
		Integer domain = getDomainForContract_batch_detail( contract , contract_batch);
		Integer id =  super.insertContract_batch_detail( domain != null ? domain : getDefaultDomain(), contract_batch, contract, status, action_type, leave_type, real_date );
		if ( domain != null ) { 
			contract_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_batch_detail
	 * @param id Identificador unico del detalle de la remesa
	 * @param domain Identificador del Dominio
	 * @param contract_batch Identificador unico de la remesa de contratos
	 * @param contract Identificador unico del contrato
	 * @param status Estado de la linea de la remesa
	 * @param action_type Tipo de accion a realizar
	 * @param leave_type Tipo de baja
	 * @param real_date Fecha real de la accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_batch_detail(Integer domain, Integer contract_batch, Integer contract, Short status, String action_type, String leave_type, Timestamp real_date)
	throws SQLException {
		Integer id =  super.insertContract_batch_detail(domain, contract_batch, contract, status, action_type, leave_type, real_date);
		contract_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> signatureDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSignaturePk(Integer id){
		return signatureDomains.get(id);
	}
	
	/**
	 * Signature
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSignature( Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Signature
	 * @param id Identificador unico
	 * @param name Nombre de la Firma
	 * @param signature Texto de la Firma de la Cuenta de Correo
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSignature(String name, String signature, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForSignature( user_id);
		Integer id =  super.insertSignature( domain != null ? domain : getDefaultDomain(), name, signature, user_id );
		if ( domain != null ) { 
			signatureDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Signature
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Firma
	 * @param signature Texto de la Firma de la Cuenta de Correo
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSignature(Integer domain, String name, String signature, Integer user_id)
	throws SQLException {
		Integer id =  super.insertSignature(domain, name, signature, user_id);
		signatureDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_clauseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_clausePk(Integer id){
		return contract_clauseDomains.get(id);
	}
	
	/**
	 * Contract_clause
	 * @param contract Identificador del Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_clause( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_clause
	 * @param id Identificador unico
	 * @param contract Identificador del Contrato
	 * @param line Numero de linea de la Clausula del Contrato
	 * @param name Titulo de la Clausula de Contrato
	 * @param description Descripcion de la Clausula de Contrato
	 * @param general Indica si la Clausula es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_clause(Integer contract, Integer line, String name, String description, Boolean general)
	throws SQLException {
		Integer domain = getDomainForContract_clause( contract);
		Integer id =  super.insertContract_clause( domain != null ? domain : getDefaultDomain(), contract, line, name, description, general );
		if ( domain != null ) { 
			contract_clauseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_clause
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del Contrato
	 * @param line Numero de linea de la Clausula del Contrato
	 * @param name Titulo de la Clausula de Contrato
	 * @param description Descripcion de la Clausula de Contrato
	 * @param general Indica si la Clausula es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_clause(Integer domain, Integer contract, Integer line, String name, String description, Boolean general)
	throws SQLException {
		Integer id =  super.insertContract_clause(domain, contract, line, name, description, general);
		contract_clauseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> enterpriseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForEnterprisePk(Integer registry){
		return enterpriseDomains.get(registry);
	}
	
	/**
	 * Enterprise
	 * @param calendar Calendario
	 * @param registry Registro de la Empresa
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForEnterprise( Integer calendar , Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Enterprise
	 * @param registry Registro de la Empresa
	 * @param scope Identificador del Ambito
	 * @param calendar Calendario
	 * @throws SQLException
	*/
	public void insertEnterprise(Integer registry, Integer scope, Integer calendar)
	throws SQLException {
		Integer domain = getDomainForEnterprise( calendar , registry , scope);
		 super.insertEnterprise( registry, domain != null ? domain : getDefaultDomain(), scope, calendar );
		if ( domain != null ) { 
			enterpriseDomains.put(registry, domain);
		}
	}

	/**
	 * Enterprise
	 * @param registry Registro de la Empresa
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param calendar Calendario
	 * @throws SQLException
	*/
	public void insertEnterprise(Integer registry, Integer domain, Integer scope, Integer calendar)
	throws SQLException {
		 super.insertEnterprise(registry, domain, scope, calendar);
		enterpriseDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> mk_templateDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMk_templatePk(Integer id){
		return mk_templateDomains.get(id);
	}
	
	/**
	 * Mk_template
	 * @param footer_template Identificador de la Plantilla de Pie de Pagina
	 * @param header_template Identificador de la Plantilla de Cabecera
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForMk_template( Integer footer_template , Integer header_template , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForRattachPk( footer_template ) ) != null )
				return domain;
			if ( ( domain = getDomainForRattachPk( header_template ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mk_template
	 * @param id Identificador unico
	 * @param scope Identificador del Ambito
	 * @param name Nombre de la Plantilla
	 * @param active Indica si la Plantilla esta activa o no
	 * @param creationDate Fecha de la creacion en el sistema de la Plantilla
	 * @param subject Asunto de la Plantilla
	 * @param width Ancho de la Plantilla
	 * @param title_color Color del titulo de la Plantilla
	 * @param background_color Color de fondo de la Plantilla
	 * @param header_template Identificador de la Plantilla de Cabecera
	 * @param footer_template Identificador de la Plantilla de Pie de Pagina
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_template(Integer scope, String name, Boolean active, Timestamp creationDate, String subject, String width, String title_color, String background_color, Integer header_template, Integer footer_template)
	throws SQLException {
		Integer domain = getDomainForMk_template( footer_template , header_template , scope);
		Integer id =  super.insertMk_template( domain != null ? domain : getDefaultDomain(), scope, name, active, creationDate, subject, width, title_color, background_color, header_template, footer_template );
		if ( domain != null ) { 
			mk_templateDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mk_template
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param name Nombre de la Plantilla
	 * @param active Indica si la Plantilla esta activa o no
	 * @param creationDate Fecha de la creacion en el sistema de la Plantilla
	 * @param subject Asunto de la Plantilla
	 * @param width Ancho de la Plantilla
	 * @param title_color Color del titulo de la Plantilla
	 * @param background_color Color de fondo de la Plantilla
	 * @param header_template Identificador de la Plantilla de Cabecera
	 * @param footer_template Identificador de la Plantilla de Pie de Pagina
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_template(Integer domain, Integer scope, String name, Boolean active, Timestamp creationDate, String subject, String width, String title_color, String background_color, Integer header_template, Integer footer_template)
	throws SQLException {
		Integer id =  super.insertMk_template(domain, scope, name, active, creationDate, subject, width, title_color, background_color, header_template, footer_template);
		mk_templateDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> mk_action_targetDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMk_action_targetPk(Integer id){
		return mk_action_targetDomains.get(id);
	}
	
	/**
	 * Mk_action_target
	 * @param action Identificador de la Accion
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @param target Identificador del Cliente Potencial
	 * @param user Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForMk_action_target( Integer action , Integer survey_response , Integer target , Integer user){
		Integer domain = null;
			if ( ( domain = getDomainForMk_actionPk( action ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurvey_responsePk( survey_response ) ) != null )
				return domain;
			if ( ( domain = getDomainForTargetPk( target ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mk_action_target
	 * @param id Identificador unico
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de CampaÒa
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @param comments Comentarios
	 * @param user Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_action_target(Integer action, Integer target, Short status, Integer survey_response, String comments, Integer user)
	throws SQLException {
		Integer domain = getDomainForMk_action_target( action , survey_response , target , user);
		Integer id =  super.insertMk_action_target( domain != null ? domain : getDefaultDomain(), action, target, status, survey_response, comments, user );
		if ( domain != null ) { 
			mk_action_targetDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mk_action_target
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de CampaÒa
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @param comments Comentarios
	 * @param user Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_action_target(Integer domain, Integer action, Integer target, Short status, Integer survey_response, String comments, Integer user)
	throws SQLException {
		Integer id =  super.insertMk_action_target(domain, action, target, status, survey_response, comments, user);
		mk_action_targetDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> amortizationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAmortizationPk(Integer id){
		return amortizationDomains.get(id);
	}
	
	/**
	 * Amortization
	 * @param accumulated_account Cuenta de Amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la Amortizacion
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param invest_asset Identificador del Bien afecto
	 * @returns domain's ID
	*/
	protected Integer getDomainForAmortization( Integer accumulated_account , Integer allocation_account , Integer fixed_asset_account , Integer invest_asset){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( accumulated_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( allocation_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( fixed_asset_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvest_assetPk( invest_asset ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Amortization
	 * @param id Identificador unico
	 * @param description Descripcion del inmovilizado
	 * @param initial_date Fecha de inicio de la Amortizacion
	 * @param deadline Fecha de baja de la Amortizacion
	 * @param amount Importe a amortizar.
	 * @param fee_period Periodo de las cuotas de Amortizacion
	 * @param sale_amount Importe de la venta
	 * @param comments Comentarios
	 * @param invest_asset Identificador del Bien afecto
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de Amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la Amortizacion
	 * @param percentage Porcentaje de Amortizacion
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization(String description, Date initial_date, Date deadline, Double amount, Short fee_period, Double sale_amount, String comments, Integer invest_asset, Integer fixed_asset_account, Integer accumulated_account, Integer allocation_account, Double percentage, Short security_level)
	throws SQLException {
		Integer domain = getDomainForAmortization( accumulated_account , allocation_account , fixed_asset_account , invest_asset);
		Integer id =  super.insertAmortization( domain != null ? domain : getDefaultDomain(), description, initial_date, deadline, amount, fee_period, sale_amount, comments, invest_asset, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level );
		if ( domain != null ) { 
			amortizationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Amortization
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del inmovilizado
	 * @param initial_date Fecha de inicio de la Amortizacion
	 * @param deadline Fecha de baja de la Amortizacion
	 * @param amount Importe a amortizar.
	 * @param fee_period Periodo de las cuotas de Amortizacion
	 * @param sale_amount Importe de la venta
	 * @param comments Comentarios
	 * @param invest_asset Identificador del Bien afecto
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de Amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la Amortizacion
	 * @param percentage Porcentaje de Amortizacion
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAmortization(Integer domain, String description, Date initial_date, Date deadline, Double amount, Short fee_period, Double sale_amount, String comments, Integer invest_asset, Integer fixed_asset_account, Integer accumulated_account, Integer allocation_account, Double percentage, Short security_level)
	throws SQLException {
		Integer id =  super.insertAmortization(domain, description, initial_date, deadline, amount, fee_period, sale_amount, comments, invest_asset, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level);
		amortizationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fan_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFan_batch_detailPk(Integer id){
		return fan_batch_detailDomains.get(id);
	}
	
	/**
	 * Fan_batch_detail
	 * @param enterprise_ccc Identificador unico del ccc
	 * @param fan_batch Identificador unico de la remesa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFan_batch_detail( Integer enterprise_ccc , Integer fan_batch){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprise_cccPk( enterprise_ccc ) ) != null )
				return domain;
			if ( ( domain = getDomainForFan_batchPk( fan_batch ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fan_batch_detail
	 * @param id Identificador unico del detalle
	 * @param fan_batch Identificador unico de la remesa
	 * @param enterprise_ccc Identificador unico del ccc
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFan_batch_detail(Integer fan_batch, Integer enterprise_ccc)
	throws SQLException {
		Integer domain = getDomainForFan_batch_detail( enterprise_ccc , fan_batch);
		Integer id =  super.insertFan_batch_detail( domain != null ? domain : getDefaultDomain(), fan_batch, enterprise_ccc );
		if ( domain != null ) { 
			fan_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fan_batch_detail
	 * @param id Identificador unico del detalle
	 * @param domain Identificador del Dominio
	 * @param fan_batch Identificador unico de la remesa
	 * @param enterprise_ccc Identificador unico del ccc
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFan_batch_detail(Integer domain, Integer fan_batch, Integer enterprise_ccc)
	throws SQLException {
		Integer id =  super.insertFan_batch_detail(domain, fan_batch, enterprise_ccc);
		fan_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> holidayDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForHolidayPk(Integer id){
		return holidayDomains.get(id);
	}
	
	/**
	 * Holiday
	 * @param holiday Identificador de Festividad
	 * @returns domain's ID
	*/
	protected Integer getDomainForHoliday( Integer holiday){
		Integer domain = null;
			if ( ( domain = getDomainForHolidayPk( holiday ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Holiday
	 * @param id Identificador unico
	 * @param description Descripcion de la Festividad
	 * @param holiday Identificador de Festividad
	 * @param editable Indica si es editable o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHoliday(String description, Integer holiday, Boolean editable)
	throws SQLException {
		Integer domain = getDomainForHoliday( holiday);
		Integer id =  super.insertHoliday( domain != null ? domain : getDefaultDomain(), description, holiday, editable );
		if ( domain != null ) { 
			holidayDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Holiday
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Festividad
	 * @param holiday Identificador de Festividad
	 * @param editable Indica si es editable o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHoliday(Integer domain, String description, Integer holiday, Boolean editable)
	throws SQLException {
		Integer id =  super.insertHoliday(domain, description, holiday, editable);
		holidayDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> app_paramDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForApp_paramPk(Integer id){
		return app_paramDomains.get(id);
	}
	
	/**
	 * App_param
	 * @returns domain's ID
	*/
	protected Integer getDomainForApp_param(){
		Integer domain = null;
		return domain;
	}

	/**
	 * App_param
	 * @param id Identificador unico
	 * @param name Nombre del Parametro
	 * @param value Valor del Parametro
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApp_param(String name, String value)
	throws SQLException {
		Integer domain = getDomainForApp_param();
		Integer id =  super.insertApp_param( domain != null ? domain : getDefaultDomain(), name, value );
		if ( domain != null ) { 
			app_paramDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * App_param
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Parametro
	 * @param value Valor del Parametro
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApp_param(Integer domain, String name, String value)
	throws SQLException {
		Integer id =  super.insertApp_param(domain, name, value);
		app_paramDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_vat_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_vat_detailPk(Integer id){
		return fs_vat_detailDomains.get(id);
	}
	
	/**
	 * Fs_vat_detail
	 * @param fs_vat Identificador de la Declaracion
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_vat_detail( Integer fs_vat){
		Integer domain = null;
			if ( ( domain = getDomainForFs_vatPk( fs_vat ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_vat_detail
	 * @param id Identificador unico
	 * @param fs_vat Identificador de la Declaracion
	 * @param vat_key Clave de la Declaracion
	 * @param percent Porcentaje de Iva
	 * @param taxable_base Base imponible
	 * @param quota Cuota
	 * @param deductible_quota Cuota deducible
	 * @param adj_taxable_base Base imponible ajustada
	 * @param adj_quota Cuota ajustada
	 * @param adj_deductible_quota Cuota deducible ajustada
	 * @param acu_taxable_base Base imponible acumulada
	 * @param acu_quota Cuota acumulada
	 * @param acu_deductible_quota Cuota deducible acumulada
	 * @param dec_taxable_base Base imponible declarado
	 * @param dec_quota Cuota declarado
	 * @param dec_deductible_quota Cuota deducible declarado
	 * @param res_taxable_base Base imponible resultado
	 * @param res_quota Cuota resultado
	 * @param res_deductible_quota Cuota deducible resultado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat_detail(Integer fs_vat, String vat_key, Double percent, Double taxable_base, Double quota, Double deductible_quota, Double adj_taxable_base, Double adj_quota, Double adj_deductible_quota, Double acu_taxable_base, Double acu_quota, Double acu_deductible_quota, Double dec_taxable_base, Double dec_quota, Double dec_deductible_quota, Double res_taxable_base, Double res_quota, Double res_deductible_quota)
	throws SQLException {
		Integer domain = getDomainForFs_vat_detail( fs_vat);
		Integer id =  super.insertFs_vat_detail( domain != null ? domain : getDefaultDomain(), fs_vat, vat_key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota, acu_taxable_base, acu_quota, acu_deductible_quota, dec_taxable_base, dec_quota, dec_deductible_quota, res_taxable_base, res_quota, res_deductible_quota );
		if ( domain != null ) { 
			fs_vat_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_vat_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_vat Identificador de la Declaracion
	 * @param vat_key Clave de la Declaracion
	 * @param percent Porcentaje de Iva
	 * @param taxable_base Base imponible
	 * @param quota Cuota
	 * @param deductible_quota Cuota deducible
	 * @param adj_taxable_base Base imponible ajustada
	 * @param adj_quota Cuota ajustada
	 * @param adj_deductible_quota Cuota deducible ajustada
	 * @param acu_taxable_base Base imponible acumulada
	 * @param acu_quota Cuota acumulada
	 * @param acu_deductible_quota Cuota deducible acumulada
	 * @param dec_taxable_base Base imponible declarado
	 * @param dec_quota Cuota declarado
	 * @param dec_deductible_quota Cuota deducible declarado
	 * @param res_taxable_base Base imponible resultado
	 * @param res_quota Cuota resultado
	 * @param res_deductible_quota Cuota deducible resultado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_vat_detail(Integer domain, Integer fs_vat, String vat_key, Double percent, Double taxable_base, Double quota, Double deductible_quota, Double adj_taxable_base, Double adj_quota, Double adj_deductible_quota, Double acu_taxable_base, Double acu_quota, Double acu_deductible_quota, Double dec_taxable_base, Double dec_quota, Double dec_deductible_quota, Double res_taxable_base, Double res_quota, Double res_deductible_quota)
	throws SQLException {
		Integer id =  super.insertFs_vat_detail(domain, fs_vat, vat_key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota, acu_taxable_base, acu_quota, acu_deductible_quota, dec_taxable_base, dec_quota, dec_deductible_quota, res_taxable_base, res_quota, res_deductible_quota);
		fs_vat_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> featureDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFeaturePk(Integer id){
		return featureDomains.get(id);
	}
	
	/**
	 * Feature
	 * @returns domain's ID
	*/
	protected Integer getDomainForFeature(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Feature
	 * @param id Identificador unico
	 * @param name Nombre de la Caracteristica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFeature(String name)
	throws SQLException {
		Integer domain = getDomainForFeature();
		Integer id =  super.insertFeature( domain != null ? domain : getDefaultDomain(), name );
		if ( domain != null ) { 
			featureDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Feature
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Caracteristica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFeature(Integer domain, String name)
	throws SQLException {
		Integer id =  super.insertFeature(domain, name);
		featureDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> application_userDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForApplication_userPk(Integer id){
		return application_userDomains.get(id);
	}
	
	/**
	 * Application_user
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForApplication_user( Integer domain_application , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForDomain_applicationPk( domain_application ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Application_user
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @param active Indica si la Aplicacion del Dominio esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApplication_user(Integer user_id, Integer domain_application, Boolean active)
	throws SQLException {
		Integer domain = getDomainForApplication_user( domain_application , user_id);
		Integer id =  super.insertApplication_user( domain != null ? domain : getDefaultDomain(), user_id, domain_application, active );
		if ( domain != null ) { 
			application_userDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Application_user
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param user_id Identificador del Usuario
	 * @param domain_application Identificador de la Aplicacion del Dominio
	 * @param active Indica si la Aplicacion del Dominio esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertApplication_user(Integer domain, Integer user_id, Integer domain_application, Boolean active)
	throws SQLException {
		Integer id =  super.insertApplication_user(domain, user_id, domain_application, active);
		application_userDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> product_tagDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProduct_tagPk(Integer id){
		return product_tagDomains.get(id);
	}
	
	/**
	 * Product_tag
	 * @param product Identificador del Producto
	 * @param tag Identificador de la Etiqueta
	 * @returns domain's ID
	*/
	protected Integer getDomainForProduct_tag( Integer product , Integer tag){
		Integer domain = null;
			if ( ( domain = getDomainForProductPk( product ) ) != null )
				return domain;
			if ( ( domain = getDomainForTagPk( tag ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Product_tag
	 * @param id Identificador unico
	 * @param product Identificador del Producto
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProduct_tag(Integer product, Integer tag)
	throws SQLException {
		Integer domain = getDomainForProduct_tag( product , tag);
		Integer id =  super.insertProduct_tag( domain != null ? domain : getDefaultDomain(), product, tag );
		if ( domain != null ) { 
			product_tagDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Product_tag
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param product Identificador del Producto
	 * @param tag Identificador de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProduct_tag(Integer domain, Integer product, Integer tag)
	throws SQLException {
		Integer id =  super.insertProduct_tag(domain, product, tag);
		product_tagDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> courseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCoursePk(Integer id){
		return courseDomains.get(id);
	}
	
	/**
	 * Course
	 * @param academic_year AÒo Academico del Curso
	 * @param level Nivel del Curso
	 * @param subject Materia del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse( Integer academic_year , Integer level , Integer subject , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForAcademic_yearPk( academic_year ) ) != null )
				return domain;
			if ( ( domain = getDomainForCourse_levelPk( level ) ) != null )
				return domain;
			if ( ( domain = getDomainForCourse_subjectPk( subject ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course
	 * @param id Identificador unico del Curso
	 * @param code Alias del Curso
	 * @param description Descripcion del Curso
	 * @param start_date Fecha inicio del Curso
	 * @param end_date Fecha fin del Curso
	 * @param academic_year AÒo Academico del Curso
	 * @param subject Materia del Curso
	 * @param level Nivel del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @param alumn_limit Limite de Alumnos del Curso
	 * @param status Estado del Curso
	 * @param comments Comentarios sobre el Curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse(String code, String description, Date start_date, Date end_date, Integer academic_year, Integer subject, Integer level, Integer workplace, Integer alumn_limit, Short status, String comments)
	throws SQLException {
		Integer domain = getDomainForCourse( academic_year , level , subject , workplace);
		Integer id =  super.insertCourse( domain != null ? domain : getDefaultDomain(), code, description, start_date, end_date, academic_year, subject, level, workplace, alumn_limit, status, comments );
		if ( domain != null ) { 
			courseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course
	 * @param id Identificador unico del Curso
	 * @param domain Identificador del Dominio
	 * @param code Alias del Curso
	 * @param description Descripcion del Curso
	 * @param start_date Fecha inicio del Curso
	 * @param end_date Fecha fin del Curso
	 * @param academic_year AÒo Academico del Curso
	 * @param subject Materia del Curso
	 * @param level Nivel del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @param alumn_limit Limite de Alumnos del Curso
	 * @param status Estado del Curso
	 * @param comments Comentarios sobre el Curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse(Integer domain, String code, String description, Date start_date, Date end_date, Integer academic_year, Integer subject, Integer level, Integer workplace, Integer alumn_limit, Short status, String comments)
	throws SQLException {
		Integer id =  super.insertCourse(domain, code, description, start_date, end_date, academic_year, subject, level, workplace, alumn_limit, status, comments);
		courseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model_detailPk(Integer id){
		return fs_model_detailDomains.get(id);
	}
	
	/**
	 * Fs_model_detail
	 * @param fs_model Identificador de la Declaracion
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model_detail( Integer fs_model){
		Integer domain = null;
			if ( ( domain = getDomainForFs_modelPk( fs_model ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model_detail
	 * @param id Identificador unico
	 * @param fs_model Identificador de la Declaracion
	 * @param type Clave de la Declaracion
	 * @param description Descripcion
	 * @param acu_amount Importe acumulado
	 * @param dec_amount Importe declarado
	 * @param res_amount Importe resultado
	 * @param adj_amount Importe ajustado
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model_detail(Integer fs_model, String type, String description, Double acu_amount, Double dec_amount, Double res_amount, Double adj_amount, Double amount)
	throws SQLException {
		Integer domain = getDomainForFs_model_detail( fs_model);
		Integer id =  super.insertFs_model_detail( domain != null ? domain : getDefaultDomain(), fs_model, type, description, acu_amount, dec_amount, res_amount, adj_amount, amount );
		if ( domain != null ) { 
			fs_model_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model Identificador de la Declaracion
	 * @param type Clave de la Declaracion
	 * @param description Descripcion
	 * @param acu_amount Importe acumulado
	 * @param dec_amount Importe declarado
	 * @param res_amount Importe resultado
	 * @param adj_amount Importe ajustado
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model_detail(Integer domain, Integer fs_model, String type, String description, Double acu_amount, Double dec_amount, Double res_amount, Double adj_amount, Double amount)
	throws SQLException {
		Integer id =  super.insertFs_model_detail(domain, fs_model, type, description, acu_amount, dec_amount, res_amount, adj_amount, amount);
		fs_model_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> calendar_periodDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCalendar_periodPk(Integer id){
		return calendar_periodDomains.get(id);
	}
	
	/**
	 * Calendar_period
	 * @param calendar Identificador del Calendario
	 * @returns domain's ID
	*/
	protected Integer getDomainForCalendar_period( Integer calendar){
		Integer domain = null;
			if ( ( domain = getDomainForCalendarPk( calendar ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Calendar_period
	 * @param id Identificador unico
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
	public int insertCalendar_period(Integer calendar, String description, Short month, Short start_day, Short end_day, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours)
	throws SQLException {
		Integer domain = getDomainForCalendar_period( calendar);
		Integer id =  super.insertCalendar_period( domain != null ? domain : getDefaultDomain(), calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours );
		if ( domain != null ) { 
			calendar_periodDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Calendar_period
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
	public int insertCalendar_period(Integer domain, Integer calendar, String description, Short month, Short start_day, Short end_day, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours)
	throws SQLException {
		Integer id =  super.insertCalendar_period(domain, calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours);
		calendar_periodDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commission_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommission_typePk(Integer id){
		return commission_typeDomains.get(id);
	}
	
	/**
	 * Commission_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommission_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Commission_type
	 * @param id Identificador unico
	 * @param name Descripcion del Tipo de Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_type(String name, Double rate)
	throws SQLException {
		Integer domain = getDomainForCommission_type();
		Integer id =  super.insertCommission_type( domain != null ? domain : getDefaultDomain(), name, rate );
		if ( domain != null ) { 
			commission_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commission_type
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Descripcion del Tipo de Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommission_type(Integer domain, String name, Double rate)
	throws SQLException {
		Integer id =  super.insertCommission_type(domain, name, rate);
		commission_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> workgroupDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWorkgroupPk(Integer id){
		return workgroupDomains.get(id);
	}
	
	/**
	 * Workgroup
	 * @returns domain's ID
	*/
	protected Integer getDomainForWorkgroup(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Workgroup
	 * @param id Identificador unico del Grupo de Trabajo
	 * @param description Descripcion del Grupo de Trabajo
	 * @param status Estado del grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkgroup(String description, Short status)
	throws SQLException {
		Integer domain = getDomainForWorkgroup();
		Integer id =  super.insertWorkgroup( domain != null ? domain : getDefaultDomain(), description, status );
		if ( domain != null ) { 
			workgroupDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Workgroup
	 * @param id Identificador unico del Grupo de Trabajo
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Grupo de Trabajo
	 * @param status Estado del grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkgroup(Integer domain, String description, Short status)
	throws SQLException {
		Integer id =  super.insertWorkgroup(domain, description, status);
		workgroupDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> productDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProductPk(Integer id){
		return productDomains.get(id);
	}
	
	/**
	 * Product
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @param brand Marca Comercial del Producto
	 * @param category Categoria del Producto
	 * @param retention Retencion del Producto
	 * @param vat IVA del Producto
	 * @returns domain's ID
	*/
	protected Integer getDomainForProduct( Integer purchase_account , Integer sales_account , Integer brand , Integer category , Integer retention , Integer vat){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( purchase_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( sales_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForBrandPk( brand ) ) != null )
				return domain;
			if ( ( domain = getDomainForPcategoryPk( category ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaxPk( retention ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaxPk( vat ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Product
	 * @param id Identificador unico del Producto
	 * @param name Nombre del Producto
	 * @param code Codigo del Producto
	 * @param kind Clase de Producto
	 * @param brand Marca Comercial del Producto
	 * @param category Categoria del Producto
	 * @param inventoriable Indica si el Producto es inventariable
	 * @param serializable Indica si el Producto es serializable
	 * @param lotable Indica si el Producto es loteable
	 * @param status Estado del Producto
	 * @param vat IVA del Producto
	 * @param retention Retencion del Producto
	 * @param type Tipo de Producto
	 * @param manufactured Indica si el Producto es elaborado
	 * @param composition Indica si el Producto es una Composicion
	 * @param composition_price Indica si el Precio lo determina la Composicion
	 * @param packaged Indica si el Producto es envasado
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProduct(String name, String code, Short kind, Integer brand, Integer category, Boolean inventoriable, Boolean serializable, Boolean lotable, Short status, Integer vat, Integer retention, Short type, Boolean manufactured, Boolean composition, Boolean composition_price, Boolean packaged, Integer sales_account, Integer purchase_account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProduct( purchase_account , sales_account , brand , category , retention , vat);
		Integer id =  super.insertProduct( domain != null ? domain : getDefaultDomain(), name, code, kind, brand, category, inventoriable, serializable, lotable, status, vat, retention, type, manufactured, composition, composition_price, packaged, sales_account, purchase_account, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			productDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Product
	 * @param id Identificador unico del Producto
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Producto
	 * @param code Codigo del Producto
	 * @param kind Clase de Producto
	 * @param brand Marca Comercial del Producto
	 * @param category Categoria del Producto
	 * @param inventoriable Indica si el Producto es inventariable
	 * @param serializable Indica si el Producto es serializable
	 * @param lotable Indica si el Producto es loteable
	 * @param status Estado del Producto
	 * @param vat IVA del Producto
	 * @param retention Retencion del Producto
	 * @param type Tipo de Producto
	 * @param manufactured Indica si el Producto es elaborado
	 * @param composition Indica si el Producto es una Composicion
	 * @param composition_price Indica si el Precio lo determina la Composicion
	 * @param packaged Indica si el Producto es envasado
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProduct(Integer domain, String name, String code, Short kind, Integer brand, Integer category, Boolean inventoriable, Boolean serializable, Boolean lotable, Short status, Integer vat, Integer retention, Short type, Boolean manufactured, Boolean composition, Boolean composition_price, Boolean packaged, Integer sales_account, Integer purchase_account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProduct(domain, name, code, kind, brand, category, inventoriable, serializable, lotable, status, vat, retention, type, manufactured, composition, composition_price, packaged, sales_account, purchase_account, creation_user, creation_date, modification_user, modification_date);
		productDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> iattachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIattachPk(Integer id){
		return iattachDomains.get(id);
	}
	
	/**
	 * Iattach
	 * @param item Identificador de Articulo
	 * @returns domain's ID
	*/
	protected Integer getDomainForIattach( Integer item){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Iattach
	 * @param id Identificador unico del Archivo Adjunto del Articulo
	 * @param item Identificador de Articulo
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIattach(Integer item, Short mimeType, String description, Blob data, Short type, String driveId)
	throws SQLException {
		Integer domain = getDomainForIattach( item);
		Integer id =  super.insertIattach( domain != null ? domain : getDefaultDomain(), item, mimeType, description, data, type, driveId );
		if ( domain != null ) { 
			iattachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Iattach
	 * @param id Identificador unico del Archivo Adjunto del Articulo
	 * @param domain Identificador del Dominio
	 * @param item Identificador de Articulo
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIattach(Integer domain, Integer item, Short mimeType, String description, Blob data, Short type, String driveId)
	throws SQLException {
		Integer id =  super.insertIattach(domain, item, mimeType, description, data, type, driveId);
		iattachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> questionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForQuestionPk(Integer id){
		return questionDomains.get(id);
	}
	
	/**
	 * Question
	 * @returns domain's ID
	*/
	protected Integer getDomainForQuestion(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Question
	 * @param id Identificador unico
	 * @param active Indica si la Pregunta esta activa o no
	 * @param question_text Texto de la Pregunta
	 * @param type Tipo de Pregunta
	 * @param argument Argumentacion de la Pregunta
	 * @param alias Alias de la Pregunta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuestion(Boolean active, String question_text, Short type, String argument, String alias)
	throws SQLException {
		Integer domain = getDomainForQuestion();
		Integer id =  super.insertQuestion( domain != null ? domain : getDefaultDomain(), active, question_text, type, argument, alias );
		if ( domain != null ) { 
			questionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Question
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param active Indica si la Pregunta esta activa o no
	 * @param question_text Texto de la Pregunta
	 * @param type Tipo de Pregunta
	 * @param argument Argumentacion de la Pregunta
	 * @param alias Alias de la Pregunta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuestion(Integer domain, Boolean active, String question_text, Short type, String argument, String alias)
	throws SQLException {
		Integer id =  super.insertQuestion(domain, active, question_text, type, argument, alias);
		questionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> amortization_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAmortization_detailPk(Integer id){
		return amortization_detailDomains.get(id);
	}
	
	/**
	 * Amortization_detail
	 * @param account_entry Posicion del Apunte Contable
	 * @param amortization Ficha de Amortizacion
	 * @returns domain's ID
	*/
	protected Integer getDomainForAmortization_detail( Integer account_entry , Integer amortization){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForAmortizationPk( amortization ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Amortization_detail
	 * @param id Identificador unico
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
	public int insertAmortization_detail(Integer amortization, Date from_date, Date to_date, Double coefficient, Double allocation, Short status, Integer account_entry, Double fiscal_allocation)
	throws SQLException {
		Integer domain = getDomainForAmortization_detail( account_entry , amortization);
		Integer id =  super.insertAmortization_detail( domain != null ? domain : getDefaultDomain(), amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation );
		if ( domain != null ) { 
			amortization_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Amortization_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
	public int insertAmortization_detail(Integer domain, Integer amortization, Date from_date, Date to_date, Double coefficient, Double allocation, Short status, Integer account_entry, Double fiscal_allocation)
	throws SQLException {
		Integer id =  super.insertAmortization_detail(domain, amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation);
		amortization_detailDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> action_favoriteDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAction_favoritePk(Integer id){
		return action_favoriteDomains.get(id);
	}
	
	/**
	 * Action_favorite
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForAction_favorite( Integer action_id , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Action_favorite
	 * @param id Identificador unico
	 * @param position Posicion dentro de las Acciones Favoritas
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_favorite(Integer position, Integer action_id, Integer user_id)
	throws SQLException {
		Integer domain = getDomainForAction_favorite( action_id , user_id);
		Integer id =  super.insertAction_favorite( domain != null ? domain : getDefaultDomain(), position, action_id, user_id );
		if ( domain != null ) { 
			action_favoriteDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Action_favorite
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param position Posicion dentro de las Acciones Favoritas
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAction_favorite(Integer domain, Integer position, Integer action_id, Integer user_id)
	throws SQLException {
		Integer id =  super.insertAction_favorite(domain, position, action_id, user_id);
		action_favoriteDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> observationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForObservationPk(Integer id){
		return observationDomains.get(id);
	}
	
	/**
	 * Observation
	 * @returns domain's ID
	*/
	protected Integer getDomainForObservation(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Observation
	 * @param id Identificador unico de la Observacion
	 * @param description Descripcion de la Observacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertObservation(String description)
	throws SQLException {
		Integer domain = getDomainForObservation();
		Integer id =  super.insertObservation( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			observationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Observation
	 * @param id Identificador unico de la Observacion
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Observacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertObservation(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertObservation(domain, description);
		observationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> academic_skillDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAcademic_skillPk(Integer id){
		return academic_skillDomains.get(id);
	}
	
	/**
	 * Academic_skill
	 * @returns domain's ID
	*/
	protected Integer getDomainForAcademic_skill(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Academic_skill
	 * @param id Identificador unico de la Aptitud Academica
	 * @param code Codigo de la Aptitud Academica
	 * @param description Descripcion de la Aptitud Academica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAcademic_skill(String code, String description)
	throws SQLException {
		Integer domain = getDomainForAcademic_skill();
		Integer id =  super.insertAcademic_skill( domain != null ? domain : getDefaultDomain(), code, description );
		if ( domain != null ) { 
			academic_skillDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Academic_skill
	 * @param id Identificador unico de la Aptitud Academica
	 * @param domain Identificador del Dominio
	 * @param code Codigo de la Aptitud Academica
	 * @param description Descripcion de la Aptitud Academica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAcademic_skill(Integer domain, String code, String description)
	throws SQLException {
		Integer id =  super.insertAcademic_skill(domain, code, description);
		academic_skillDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model390Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model390Pk(Integer id){
		return fs_model390Domains.get(id);
	}
	
	/**
	 * Fs_model390
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model390( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model390
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de Declaracion sustituida
	 * @param model Modelo XML de la Declaracion
	 * @param response Respuesta AEAT
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model390(Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, String model, String response)
	throws SQLException {
		Integer domain = getDomainForFs_model390( enterprise);
		Integer id =  super.insertFs_model390( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, security_level, document, name, complementary, replacement, comments, receipt, replaced_receipt, model, response );
		if ( domain != null ) { 
			fs_model390Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model390
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param security_level Nivel de seguridad
	 * @param document NIF
	 * @param name Nombre
	 * @param complementary Declaracion complementaria
	 * @param replacement Declaracion sustitutiva
	 * @param comments Comentarios de la Declaracion
	 * @param receipt Numero de Declaracion
	 * @param replaced_receipt Numero de Declaracion sustituida
	 * @param model Modelo XML de la Declaracion
	 * @param response Respuesta AEAT
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model390(Integer domain, Integer enterprise, Integer year, Short administration, Short status, Short security_level, String document, String name, Boolean complementary, Boolean replacement, String comments, String receipt, String replaced_receipt, String model, String response)
	throws SQLException {
		Integer id =  super.insertFs_model390(domain, enterprise, year, administration, status, security_level, document, name, complementary, replacement, comments, receipt, replaced_receipt, model, response);
		fs_model390Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> commercial_termDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCommercial_termPk(Integer id){
		return commercial_termDomains.get(id);
	}
	
	/**
	 * Commercial_term
	 * @returns domain's ID
	*/
	protected Integer getDomainForCommercial_term(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Commercial_term
	 * @param id Identificador unico
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la CondiciÛn es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_term(Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		Integer domain = getDomainForCommercial_term();
		Integer id =  super.insertCommercial_term( domain != null ? domain : getDefaultDomain(), line, name, description, term_general );
		if ( domain != null ) { 
			commercial_termDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Commercial_term
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la CondiciÛn es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCommercial_term(Integer domain, Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		Integer id =  super.insertCommercial_term(domain, line, name, description, term_general);
		commercial_termDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> item_warehouseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForItem_warehousePk(Integer id){
		return item_warehouseDomains.get(id);
	}
	
	/**
	 * Item_warehouse
	 * @param item Identificador de Articulo
	 * @param warehouse Identificador de Almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForItem_warehouse( Integer item , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Item_warehouse
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param warehouse Identificador de Almacen
	 * @param stock_max Stock maximo del Articulo en el Almacen
	 * @param stock_min Stock minimo del Articulo en el Almacen
	 * @param location Localizacion del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_warehouse(Integer item, Integer warehouse, Double stock_max, Double stock_min, String location)
	throws SQLException {
		Integer domain = getDomainForItem_warehouse( item , warehouse);
		Integer id =  super.insertItem_warehouse( domain != null ? domain : getDefaultDomain(), item, warehouse, stock_max, stock_min, location );
		if ( domain != null ) { 
			item_warehouseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Item_warehouse
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param item Identificador de Articulo
	 * @param warehouse Identificador de Almacen
	 * @param stock_max Stock maximo del Articulo en el Almacen
	 * @param stock_min Stock minimo del Articulo en el Almacen
	 * @param location Localizacion del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertItem_warehouse(Integer domain, Integer item, Integer warehouse, Double stock_max, Double stock_min, String location)
	throws SQLException {
		Integer id =  super.insertItem_warehouse(domain, item, warehouse, stock_max, stock_min, location);
		item_warehouseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rsegmentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRsegmentPk(Integer id){
		return rsegmentDomains.get(id);
	}
	
	/**
	 * Rsegment
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @returns domain's ID
	*/
	protected Integer getDomainForRsegment( Integer registry , Integer segment){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForSegmentPk( segment ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rsegment
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRsegment(Integer registry, Integer segment)
	throws SQLException {
		Integer domain = getDomainForRsegment( registry , segment);
		Integer id =  super.insertRsegment( domain != null ? domain : getDefaultDomain(), registry, segment );
		if ( domain != null ) { 
			rsegmentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rsegment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRsegment(Integer domain, Integer registry, Integer segment)
	throws SQLException {
		Integer id =  super.insertRsegment(domain, registry, segment);
		rsegmentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rsupplierDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRsupplierPk(Integer id){
		return rsupplierDomains.get(id);
	}
	
	/**
	 * Rsupplier
	 * @param pay_method Identificador de la Forma de Pago
	 * @param registry Identificador de Persona o Empresa
	 * @param supplier Identificador del Proveedor
	 * @param tariff Identificador de Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRsupplier( Integer pay_method , Integer registry , Integer supplier , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForSupplierPk( supplier ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rsupplier
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param supplier Identificador del Proveedor
	 * @param target_external_code Codigo del Cliente Potencial para el Proveedor
	 * @param tariff Identificador de Tarifa
	 * @param pay_method Identificador de la Forma de Pago
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRsupplier(Integer registry, Integer supplier, String target_external_code, Integer tariff, Integer pay_method, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic)
	throws SQLException {
		Integer domain = getDomainForRsupplier( pay_method , registry , supplier , tariff);
		Integer id =  super.insertRsupplier( domain != null ? domain : getDefaultDomain(), registry, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic );
		if ( domain != null ) { 
			rsupplierDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rsupplier
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param supplier Identificador del Proveedor
	 * @param target_external_code Codigo del Cliente Potencial para el Proveedor
	 * @param tariff Identificador de Tarifa
	 * @param pay_method Identificador de la Forma de Pago
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRsupplier(Integer domain, Integer registry, Integer supplier, String target_external_code, Integer tariff, Integer pay_method, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic)
	throws SQLException {
		Integer id =  super.insertRsupplier(domain, registry, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic);
		rsupplierDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> bank_conceptDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBank_conceptPk(Integer id){
		return bank_conceptDomains.get(id);
	}
	
	/**
	 * Bank_concept
	 * @param account Identificador de la Cuenta Contable
	 * @returns domain's ID
	*/
	protected Integer getDomainForBank_concept( Integer account){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Bank_concept
	 * @param id Identificador unico
	 * @param name Nombre del Concepto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_concept(String name, Integer account)
	throws SQLException {
		Integer domain = getDomainForBank_concept( account);
		Integer id =  super.insertBank_concept( domain != null ? domain : getDefaultDomain(), name, account );
		if ( domain != null ) { 
			bank_conceptDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Bank_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Concepto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBank_concept(Integer domain, String name, Integer account)
	throws SQLException {
		Integer id =  super.insertBank_concept(domain, name, account);
		bank_conceptDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> stop_salesDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForStop_salesPk(Integer id){
		return stop_salesDomains.get(id);
	}
	
	/**
	 * Stop_sales
	 * @param hotel Identificador del Hotel
	 * @returns domain's ID
	*/
	protected Integer getDomainForStop_sales( Integer hotel){
		Integer domain = null;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Stop_sales
	 * @param id Identificador unico
	 * @param hotel Identificador del Hotel
	 * @param start_date Fecha de inicio del Paro
	 * @param end_date Fecha de fin del Paro
	 * @param remarks Comentarios
	 * @param active Indica si el Paro esta activo o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales(Integer hotel, Date start_date, Date end_date, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForStop_sales( hotel);
		Integer id =  super.insertStop_sales( domain != null ? domain : getDefaultDomain(), hotel, start_date, end_date, remarks, active, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			stop_salesDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Stop_sales
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param start_date Fecha de inicio del Paro
	 * @param end_date Fecha de fin del Paro
	 * @param remarks Comentarios
	 * @param active Indica si el Paro esta activo o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStop_sales(Integer domain, Integer hotel, Date start_date, Date end_date, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertStop_sales(domain, hotel, start_date, end_date, remarks, active, creation_user, creation_date, modification_user, modification_date);
		stop_salesDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> assetDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAssetPk(Integer id){
		return assetDomains.get(id);
	}
	
	/**
	 * Asset
	 * @returns domain's ID
	*/
	protected Integer getDomainForAsset(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Asset
	 * @param id Identificador unico
	 * @param description Descripcion del Activo
	 * @param name Nombre corto del Activo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAsset(String description, String name)
	throws SQLException {
		Integer domain = getDomainForAsset();
		Integer id =  super.insertAsset( domain != null ? domain : getDefaultDomain(), description, name );
		if ( domain != null ) { 
			assetDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Asset
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Activo
	 * @param name Nombre corto del Activo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAsset(Integer domain, String description, String name)
	throws SQLException {
		Integer id =  super.insertAsset(domain, description, name);
		assetDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> workplaceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWorkplacePk(Integer id){
		return workplaceDomains.get(id);
	}
	
	/**
	 * Workplace
	 * @param customer Identificador del Cliente
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForWorkplace( Integer customer , Integer enterprise , Integer address , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Workplace
	 * @param id Identificador unico del Centro de Trabajo
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param customer Identificador del Cliente
	 * @param scope Identificador del Ambito
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkplace(Integer enterprise, String description, Integer address, Integer customer, Integer scope, Short economicAgreement, Boolean active)
	throws SQLException {
		Integer domain = getDomainForWorkplace( customer , enterprise , address , scope);
		Integer id =  super.insertWorkplace( domain != null ? domain : getDefaultDomain(), enterprise, description, address, customer, scope, economicAgreement, active );
		if ( domain != null ) { 
			workplaceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Workplace
	 * @param id Identificador unico del Centro de Trabajo
	 * @param domain Identificador del Dominio
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param customer Identificador del Cliente
	 * @param scope Identificador del Ambito
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWorkplace(Integer domain, Integer enterprise, String description, Integer address, Integer customer, Integer scope, Short economicAgreement, Boolean active)
	throws SQLException {
		Integer id =  super.insertWorkplace(domain, enterprise, description, address, customer, scope, economicAgreement, active);
		workplaceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model200Domains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model200Pk(Integer id){
		return fs_model200Domains.get(id);
	}
	
	/**
	 * Fs_model200
	 * @param enterprise Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model200( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model200
	 * @param id Identificador unico
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param document NIF
	 * @param name Nombre
	 * @param phone1 Telefono 1
	 * @param phone2 Telefono 2
	 * @param complementary Declaracion complementaria
	 * @param receipt Numero de Declaracion
	 * @param complementary_receipt Numero de Declaracion sustituida
	 * @param cnae Codigo CNAE
	 * @param period_type Tipo de periodo
	 * @param period_start Inicio periodo
	 * @param period_end Fin periodo
	 * @param fiscal_group Numero de grupo fiscal
	 * @param dominant_document NIF de la entidad dominante
	 * @param secretary_document NIF del secretario
	 * @param secretary_name NIF del secretario
	 * @param irnr Fecha IRNR
	 * @param result_type (D) Devolucion, (I) Ingreso, (C) Cuota cero
	 * @param dev_type (R) Renuncia, (T) Transferencia
	 * @param pay_type (E) Efectivo, (A) Adeudo
	 * @param amount Importe
	 * @param iban IBAN
	 * @param comments Comentarios de la Declaracion
	 * @param nrs_anexoIII NRS anexo III
	 * @param just_canarias Numero de justificante Canarias
	 * @param nrs_anexoIV NRS anexo IV
	 * @param nrs_anexoV NRS anexo V
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param just_activos Numero de justificante activos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200(Integer enterprise, Integer year, Short administration, Short status, String document, String name, String phone1, String phone2, Boolean complementary, String receipt, String complementary_receipt, String cnae, Boolean period_type, Date period_start, Date period_end, String fiscal_group, String dominant_document, String secretary_document, String secretary_name, Date irnr, String result_type, String dev_type, String pay_type, Double amount, String iban, String comments, String nrs_anexoIII, String just_canarias, String nrs_anexoIV, String nrs_anexoV, String bic, String just_activos)
	throws SQLException {
		Integer domain = getDomainForFs_model200( enterprise);
		Integer id =  super.insertFs_model200( domain != null ? domain : getDefaultDomain(), enterprise, year, administration, status, document, name, phone1, phone2, complementary, receipt, complementary_receipt, cnae, period_type, period_start, period_end, fiscal_group, dominant_document, secretary_document, secretary_name, irnr, result_type, dev_type, pay_type, amount, iban, comments, nrs_anexoIII, just_canarias, nrs_anexoIV, nrs_anexoV, bic, just_activos );
		if ( domain != null ) { 
			fs_model200Domains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model200
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param year Ejercicio de la Declaracion
	 * @param administration Administracion
	 * @param status Estado de la Declaracion
	 * @param document NIF
	 * @param name Nombre
	 * @param phone1 Telefono 1
	 * @param phone2 Telefono 2
	 * @param complementary Declaracion complementaria
	 * @param receipt Numero de Declaracion
	 * @param complementary_receipt Numero de Declaracion sustituida
	 * @param cnae Codigo CNAE
	 * @param period_type Tipo de periodo
	 * @param period_start Inicio periodo
	 * @param period_end Fin periodo
	 * @param fiscal_group Numero de grupo fiscal
	 * @param dominant_document NIF de la entidad dominante
	 * @param secretary_document NIF del secretario
	 * @param secretary_name NIF del secretario
	 * @param irnr Fecha IRNR
	 * @param result_type (D) Devolucion, (I) Ingreso, (C) Cuota cero
	 * @param dev_type (R) Renuncia, (T) Transferencia
	 * @param pay_type (E) Efectivo, (A) Adeudo
	 * @param amount Importe
	 * @param iban IBAN
	 * @param comments Comentarios de la Declaracion
	 * @param nrs_anexoIII NRS anexo III
	 * @param just_canarias Numero de justificante Canarias
	 * @param nrs_anexoIV NRS anexo IV
	 * @param nrs_anexoV NRS anexo V
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param just_activos Numero de justificante activos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model200(Integer domain, Integer enterprise, Integer year, Short administration, Short status, String document, String name, String phone1, String phone2, Boolean complementary, String receipt, String complementary_receipt, String cnae, Boolean period_type, Date period_start, Date period_end, String fiscal_group, String dominant_document, String secretary_document, String secretary_name, Date irnr, String result_type, String dev_type, String pay_type, Double amount, String iban, String comments, String nrs_anexoIII, String just_canarias, String nrs_anexoIV, String nrs_anexoV, String bic, String just_activos)
	throws SQLException {
		Integer id =  super.insertFs_model200(domain, enterprise, year, administration, status, document, name, phone1, phone2, complementary, receipt, complementary_receipt, cnae, period_type, period_start, period_end, fiscal_group, dominant_document, secretary_document, secretary_name, irnr, result_type, dev_type, pay_type, amount, iban, comments, nrs_anexoIII, just_canarias, nrs_anexoIV, nrs_anexoV, bic, just_activos);
		fs_model200Domains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_tasDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_tasPk(Integer project){
		return project_tasDomains.get(project);
	}
	
	/**
	 * Project_tas
	 * @param project Identificador del Proyecto
	 * @param target Identificador del Cliente Potencial
	 * @param task_holder Identificador del Empleado que ejecuta la Orden
	 * @param tas_item Identificador del Articulo susceptible de Asistencia Tecnica
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_tas( Integer project , Integer target , Integer task_holder , Integer tas_item , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForTargetPk( target ) ) != null )
				return domain;
			if ( ( domain = getDomainForTask_holderPk( task_holder ) ) != null )
				return domain;
			if ( ( domain = getDomainForTas_itemPk( tas_item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_tas
	 * @param project Identificador del Proyecto
	 * @param series Serie de la Orden de Reparacion
	 * @param number Numero de la Orden de Reparacion
	 * @param target Identificador del Cliente Potencial
	 * @param tas_item Identificador del Articulo susceptible de Asistencia Tecnica
	 * @param counter Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)
	 * @param task_holder Identificador del Empleado que ejecuta la Orden
	 * @param comments Comentarios
	 * @param status Estado de la Orden de Reparacion
	 * @param status_date Fecha del Estado de la Orden de Reparacion
	 * @param workplace Identificador del Centro de Trabajo
	 * @throws SQLException
	*/
	public void insertProject_tas(Integer project, String series, Integer number, Integer target, Integer tas_item, Double counter, Integer task_holder, String comments, Short status, Date status_date, Integer workplace)
	throws SQLException {
		Integer domain = getDomainForProject_tas( project , target , task_holder , tas_item , workplace);
		 super.insertProject_tas( project, domain != null ? domain : getDefaultDomain(), series, number, target, tas_item, counter, task_holder, comments, status, status_date, workplace );
		if ( domain != null ) { 
			project_tasDomains.put(project, domain);
		}
	}

	/**
	 * Project_tas
	 * @param project Identificador del Proyecto
	 * @param domain Identificador del Dominio
	 * @param series Serie de la Orden de Reparacion
	 * @param number Numero de la Orden de Reparacion
	 * @param target Identificador del Cliente Potencial
	 * @param tas_item Identificador del Articulo susceptible de Asistencia Tecnica
	 * @param counter Contador del Articulo de la Orden de Reparacion (p.e. Kilometraje)
	 * @param task_holder Identificador del Empleado que ejecuta la Orden
	 * @param comments Comentarios
	 * @param status Estado de la Orden de Reparacion
	 * @param status_date Fecha del Estado de la Orden de Reparacion
	 * @param workplace Identificador del Centro de Trabajo
	 * @throws SQLException
	*/
	public void insertProject_tas(Integer project, Integer domain, String series, Integer number, Integer target, Integer tas_item, Double counter, Integer task_holder, String comments, Short status, Date status_date, Integer workplace)
	throws SQLException {
		 super.insertProject_tas(project, domain, series, number, target, tas_item, counter, task_holder, comments, status, status_date, workplace);
		project_tasDomains.put(project, domain );
			}

	
	private Map<Integer,Integer> bookingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForBookingPk(Integer id){
		return bookingDomains.get(id);
	}
	
	/**
	 * Booking
	 * @param agency Identificador de la Agencia
	 * @param hotel Identificador del Hotel
	 * @param item Identificador del Producto
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForBooking( Integer agency , Integer hotel , Integer item , Integer project_reservation_room , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( agency ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservation_roomPk( project_reservation_room ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Booking
	 * @param id Identificador unico
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param hotel Identificador del Hotel
	 * @param agency Identificador de la Agencia
	 * @param item Identificador del Producto
	 * @param tariff Identificador de la Tarifa
	 * @param stay_date Fecha de estancia
	 * @param stay_type Indica si es una entrada, una salida o una permanencia
	 * @param guests Numero de Huespedes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBooking(Integer project_reservation_room, Integer hotel, Integer agency, Integer item, Integer tariff, Date stay_date, Short stay_type, Integer guests)
	throws SQLException {
		Integer domain = getDomainForBooking( agency , hotel , item , project_reservation_room , tariff);
		Integer id =  super.insertBooking( domain != null ? domain : getDefaultDomain(), project_reservation_room, hotel, agency, item, tariff, stay_date, stay_type, guests );
		if ( domain != null ) { 
			bookingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Booking
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param hotel Identificador del Hotel
	 * @param agency Identificador de la Agencia
	 * @param item Identificador del Producto
	 * @param tariff Identificador de la Tarifa
	 * @param stay_date Fecha de estancia
	 * @param stay_type Indica si es una entrada, una salida o una permanencia
	 * @param guests Numero de Huespedes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertBooking(Integer domain, Integer project_reservation_room, Integer hotel, Integer agency, Integer item, Integer tariff, Date stay_date, Short stay_type, Integer guests)
	throws SQLException {
		Integer id =  super.insertBooking(domain, project_reservation_room, hotel, agency, item, tariff, stay_date, stay_type, guests);
		bookingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_deductionDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_deductionPk(Integer id){
		return contract_deductionDomains.get(id);
	}
	
	/**
	 * Contract_deduction
	 * @param contract Contrato
	 * @param deduction_concept Identificador unico del concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_deduction( Integer contract , Integer deduction_concept){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForDeduction_conceptPk( deduction_concept ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_deduction
	 * @param id Identificador unico
	 * @param type Tipo de DeducciÛn
	 * @param deduction_concept Identificador unico del concepto
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la percepcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_deduction(Short type, Integer deduction_concept, Integer contract, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		Integer domain = getDomainForContract_deduction( contract , deduction_concept);
		Integer id =  super.insertContract_deduction( domain != null ? domain : getDefaultDomain(), type, deduction_concept, contract, description, description_decorable, expression, start_date, end_date, month );
		if ( domain != null ) { 
			contract_deductionDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de DeducciÛn
	 * @param deduction_concept Identificador unico del concepto
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression FÛrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la percepcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_deduction(Integer domain, Short type, Integer deduction_concept, Integer contract, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		Integer id =  super.insertContract_deduction(domain, type, deduction_concept, contract, description, description_decorable, expression, start_date, end_date, month);
		contract_deductionDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> delivery_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDelivery_detailPk(Integer id){
		return delivery_detailDomains.get(id);
	}
	
	/**
	 * Delivery_detail
	 * @param delivery Identificador del Albaran de Venta
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param sales_detail Identificador del Detalle del Pedido de Venta asociado
	 * @param warehouse Identificador del Almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForDelivery_detail( Integer delivery , Integer item , Integer sales_detail , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForDeliveryPk( delivery ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForSales_detailPk( sales_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Delivery_detail
	 * @param id Identificador unico del Detalle del Albaran de Venta
	 * @param delivery Identificador del Albaran de Venta
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param sales_detail Identificador del Detalle del Pedido de Venta asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDelivery_detail(Integer delivery, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer sales_detail, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForDelivery_detail( delivery , item , sales_detail , warehouse);
		Integer id =  super.insertDelivery_detail( domain != null ? domain : getDefaultDomain(), delivery, line, item, description, warehouse, quantity, price, discount_expr, sales_detail, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			delivery_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Delivery_detail
	 * @param id Identificador unico del Detalle del Albaran de Venta
	 * @param domain Identificador del Dominio
	 * @param delivery Identificador del Albaran de Venta
	 * @param line Numero de linea del Detalle dentro del Albaran
	 * @param item Identificador del Articulo del Detalle de Albaran
	 * @param description Descripcion del Detalle de Albaran
	 * @param warehouse Identificador del Almacen
	 * @param quantity Cantidad del Detalle de Albaran
	 * @param price Precio del Detalle de Albaran
	 * @param discount_expr Descuentos del Detalle de Albaran
	 * @param sales_detail Identificador del Detalle del Pedido de Venta asociado
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDelivery_detail(Integer domain, Integer delivery, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer sales_detail, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertDelivery_detail(domain, delivery, line, item, description, warehouse, quantity, price, discount_expr, sales_detail, creation_user, creation_date, modification_user, modification_date);
		delivery_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> irpf_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIrpf_dataPk(Integer id){
		return irpf_dataDomains.get(id);
	}
	
	/**
	 * Irpf_data
	 * @param contract Identificador del contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForIrpf_data( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Irpf_data
	 * @param id Identificador unico
	 * @param contract Identificador del contrato
	 * @param family_situation Situacion familiar
	 * @param spouse_document Numero de Documento del conyuge
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param moving_date Fecha de movilidad geografica
	 * @param labour_prolongation Prolongacion de la actividad laboral
	 * @param descendient_count Numero de hijos
	 * @param start_date Fecha inicio del modelo
	 * @param end_date Fecha fin del modelo
	 * @param fiscal_exclusion Exclusion a la obligacion de tributar
	 * @param issue_date Fecha de emisiÛn
	 * @param annual_remuneration Retribuciones totales (dinerarias y en especie). Importe Ìntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Atr. 18.2 LIRPF)
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11™ y 12 ™ de la LIRPF)
	 * @param deduccibles_expenses Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge. Importe fijado judicialmente
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente
	 * @param deduct_home_loan ComunicaciÛn de pagos por la adquisiÛn o rehabilitaciÛn de la vivienda habitual utilizando financiaciÛn ajena
	 * @param request_irpf Tipo de retenciÛn solicitado
	 * @param contract_type Contrato o relaciÛn
	 * @param ceuta_melilla Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data(Integer contract, Short family_situation, String spouse_document, Short disability_level, Boolean dependence, Date moving_date, Boolean labour_prolongation, Short descendient_count, Date start_date, Date end_date, Boolean fiscal_exclusion, Date issue_date, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double spousal_support, Double food_annuity, Short deduct_home_loan, Double request_irpf, Short contract_type, Boolean ceuta_melilla)
	throws SQLException {
		Integer domain = getDomainForIrpf_data( contract);
		Integer id =  super.insertIrpf_data( domain != null ? domain : getDefaultDomain(), contract, family_situation, spouse_document, disability_level, dependence, moving_date, labour_prolongation, descendient_count, start_date, end_date, fiscal_exclusion, issue_date, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, spousal_support, food_annuity, deduct_home_loan, request_irpf, contract_type, ceuta_melilla );
		if ( domain != null ) { 
			irpf_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Irpf_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del contrato
	 * @param family_situation Situacion familiar
	 * @param spouse_document Numero de Documento del conyuge
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param moving_date Fecha de movilidad geografica
	 * @param labour_prolongation Prolongacion de la actividad laboral
	 * @param descendient_count Numero de hijos
	 * @param start_date Fecha inicio del modelo
	 * @param end_date Fecha fin del modelo
	 * @param fiscal_exclusion Exclusion a la obligacion de tributar
	 * @param issue_date Fecha de emisiÛn
	 * @param annual_remuneration Retribuciones totales (dinerarias y en especie). Importe Ìntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Atr. 18.2 LIRPF)
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11™ y 12 ™ de la LIRPF)
	 * @param deduccibles_expenses Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)
	 * @param spousal_support Pension compensatoria a favor del cÛnyuge. Importe fijado judicialmente
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente
	 * @param deduct_home_loan ComunicaciÛn de pagos por la adquisiÛn o rehabilitaciÛn de la vivienda habitual utilizando financiaciÛn ajena
	 * @param request_irpf Tipo de retenciÛn solicitado
	 * @param contract_type Contrato o relaciÛn
	 * @param ceuta_melilla Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data(Integer domain, Integer contract, Short family_situation, String spouse_document, Short disability_level, Boolean dependence, Date moving_date, Boolean labour_prolongation, Short descendient_count, Date start_date, Date end_date, Boolean fiscal_exclusion, Date issue_date, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double spousal_support, Double food_annuity, Short deduct_home_loan, Double request_irpf, Short contract_type, Boolean ceuta_melilla)
	throws SQLException {
		Integer id =  super.insertIrpf_data(domain, contract, family_situation, spouse_document, disability_level, dependence, moving_date, labour_prolongation, descendient_count, start_date, end_date, fiscal_exclusion, issue_date, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, spousal_support, food_annuity, deduct_home_loan, request_irpf, contract_type, ceuta_melilla);
		irpf_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rdir_staffDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRdir_staffPk(Integer id){
		return rdir_staffDomains.get(id);
	}
	
	/**
	 * Rdir_staff
	 * @param registry Identificador de la Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRdir_staff( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rdir_staff
	 * @param id Identificador unico de la Relacion entre Empresas y sus Directivos
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
	 * @param representative_labor Indica si el Directivo es representante laboral
	 * @param charge_description Descripcion del cargo de Directivo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRdir_staff(Integer registry, String document, String name, Boolean shareholder, Boolean representative, Boolean director, Double percent_share, Integer share_number, Double nominal_value, Date due_date, Boolean representative_labor, String charge_description)
	throws SQLException {
		Integer domain = getDomainForRdir_staff( registry);
		Integer id =  super.insertRdir_staff( domain != null ? domain : getDefaultDomain(), registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date, representative_labor, charge_description );
		if ( domain != null ) { 
			rdir_staffDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rdir_staff
	 * @param id Identificador unico de la Relacion entre Empresas y sus Directivos
	 * @param domain Identificador del Dominio
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
	 * @param representative_labor Indica si el Directivo es representante laboral
	 * @param charge_description Descripcion del cargo de Directivo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRdir_staff(Integer domain, Integer registry, String document, String name, Boolean shareholder, Boolean representative, Boolean director, Double percent_share, Integer share_number, Double nominal_value, Date due_date, Boolean representative_labor, String charge_description)
	throws SQLException {
		Integer id =  super.insertRdir_staff(domain, registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date, representative_labor, charge_description);
		rdir_staffDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> irpf_data_ascendantsDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForIrpf_data_ascendantsPk(Integer id){
		return irpf_data_ascendantsDomains.get(id);
	}
	
	/**
	 * Irpf_data_ascendants
	 * @param irpf_data Identificador del irpf
	 * @returns domain's ID
	*/
	protected Integer getDomainForIrpf_data_ascendants( Integer irpf_data){
		Integer domain = null;
			if ( ( domain = getDomainForIrpf_dataPk( irpf_data ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Irpf_data_ascendants
	 * @param id Identificador unico
	 * @param irpf_data Identificador del irpf
	 * @param birth_year Anio de nacimiento
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param another_descendient Convivencia con otros descendientes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data_ascendants(Integer irpf_data, Integer birth_year, Short disability_level, Boolean dependence, Short another_descendient)
	throws SQLException {
		Integer domain = getDomainForIrpf_data_ascendants( irpf_data);
		Integer id =  super.insertIrpf_data_ascendants( domain != null ? domain : getDefaultDomain(), irpf_data, birth_year, disability_level, dependence, another_descendient );
		if ( domain != null ) { 
			irpf_data_ascendantsDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Irpf_data_ascendants
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param irpf_data Identificador del irpf
	 * @param birth_year Anio de nacimiento
	 * @param disability_level Grado de discapacidad
	 * @param dependence Dependencia de terceras personas
	 * @param another_descendient Convivencia con otros descendientes
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertIrpf_data_ascendants(Integer domain, Integer irpf_data, Integer birth_year, Short disability_level, Boolean dependence, Short another_descendient)
	throws SQLException {
		Integer id =  super.insertIrpf_data_ascendants(domain, irpf_data, birth_year, disability_level, dependence, another_descendient);
		irpf_data_ascendantsDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> process_taskDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProcess_taskPk(Integer id){
		return process_taskDomains.get(id);
	}
	
	/**
	 * Process_task
	 * @param campaign Identificador de la CampaÒa
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns domain's ID
	*/
	protected Integer getDomainForProcess_task( Integer campaign , Integer process_detail , Integer task){
		Integer domain = null;
			if ( ( domain = getDomainForCampaignPk( campaign ) ) != null )
				return domain;
			if ( ( domain = getDomainForProcess_detailPk( process_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaskPk( task ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Process_task
	 * @param id Identificador unico de la Relacion entre CampaÒas, Actividades y Tareas
	 * @param campaign Identificador de la CampaÒa
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_task(Integer campaign, Integer process_detail, Integer task)
	throws SQLException {
		Integer domain = getDomainForProcess_task( campaign , process_detail , task);
		Integer id =  super.insertProcess_task( domain != null ? domain : getDefaultDomain(), campaign, process_detail, task );
		if ( domain != null ) { 
			process_taskDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Process_task
	 * @param id Identificador unico de la Relacion entre CampaÒas, Actividades y Tareas
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la CampaÒa
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_task(Integer domain, Integer campaign, Integer process_detail, Integer task)
	throws SQLException {
		Integer id =  super.insertProcess_task(domain, campaign, process_detail, task);
		process_taskDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_batchPk(Integer id){
		return contract_batchDomains.get(id);
	}
	
	/**
	 * Contract_batch
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_batch(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Contract_batch
	 * @param id Identificador unico de la remesa de contratos
	 * @param date Fecha de creacion
	 * @param outcome_file_date Fecha de creacion
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file_date Fecha de respuesta
	 * @param red_response_id Identificador de la respuesta
	 * @param status Indica el estado de la remesa
	 * @param outcome_file Archivo Adjunto en binario
	 * @param income_file Archivo respuesta en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_batch(Timestamp date, Timestamp outcome_file_date, String communication_id, Timestamp income_file_date, String red_response_id, Short status, Blob outcome_file, Blob income_file)
	throws SQLException {
		Integer domain = getDomainForContract_batch();
		Integer id =  super.insertContract_batch( domain != null ? domain : getDefaultDomain(), date, outcome_file_date, communication_id, income_file_date, red_response_id, status, outcome_file, income_file );
		if ( domain != null ) { 
			contract_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_batch
	 * @param id Identificador unico de la remesa de contratos
	 * @param domain Identificador del Dominio
	 * @param date Fecha de creacion
	 * @param outcome_file_date Fecha de creacion
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param income_file_date Fecha de respuesta
	 * @param red_response_id Identificador de la respuesta
	 * @param status Indica el estado de la remesa
	 * @param outcome_file Archivo Adjunto en binario
	 * @param income_file Archivo respuesta en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_batch(Integer domain, Timestamp date, Timestamp outcome_file_date, String communication_id, Timestamp income_file_date, String red_response_id, Short status, Blob outcome_file, Blob income_file)
	throws SQLException {
		Integer id =  super.insertContract_batch(domain, date, outcome_file_date, communication_id, income_file_date, red_response_id, status, outcome_file, income_file);
		contract_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> prepaymentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPrepaymentPk(Integer id){
		return prepaymentDomains.get(id);
	}
	
	/**
	 * Prepayment
	 * @param creditor Identificador del Acreedor
	 * @param customer Identificador del Cliente
	 * @param finance Identificador del Vencimiento
	 * @returns domain's ID
	*/
	protected Integer getDomainForPrepayment( Integer creditor , Integer customer , Integer finance){
		Integer domain = null;
			if ( ( domain = getDomainForCreditorPk( creditor ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForFinancePk( finance ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Prepayment
	 * @param id Identificador unico
	 * @param creditor Identificador del Acreedor
	 * @param customer Identificador del Cliente
	 * @param finance Identificador del Vencimiento
	 * @param collect Localizacion del cobro del Suplido
	 * @param collect_id Identificador del cobro del Suplido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPrepayment(Integer creditor, Integer customer, Integer finance, Short collect, Integer collect_id)
	throws SQLException {
		Integer domain = getDomainForPrepayment( creditor , customer , finance);
		Integer id =  super.insertPrepayment( domain != null ? domain : getDefaultDomain(), creditor, customer, finance, collect, collect_id );
		if ( domain != null ) { 
			prepaymentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Prepayment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param creditor Identificador del Acreedor
	 * @param customer Identificador del Cliente
	 * @param finance Identificador del Vencimiento
	 * @param collect Localizacion del cobro del Suplido
	 * @param collect_id Identificador del cobro del Suplido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPrepayment(Integer domain, Integer creditor, Integer customer, Integer finance, Short collect, Integer collect_id)
	throws SQLException {
		Integer id =  super.insertPrepayment(domain, creditor, customer, finance, collect, collect_id);
		prepaymentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> salesDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSalesPk(Integer id){
		return salesDomains.get(id);
	}
	
	/**
	 * Sales
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param customer Identificador del Cliente
	 * @param pay_method Identificador de la Forma de Pago
	 * @param project Identificador del Proyecto
	 * @param shipping_address Identificador de la Direccion de envio del Pedido
	 * @param scope Ambito del Pedido
	 * @param seller Identificador del Agente Comercial
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForSales( Integer carrier , Integer customer , Integer pay_method , Integer project , Integer shipping_address , Integer scope , Integer seller , Integer workplace){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
			if ( ( domain = getDomainForCustomerPk( customer ) ) != null )
				return domain;
			if ( ( domain = getDomainForPay_methodPk( pay_method ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( shipping_address ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkplacePk( workplace ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Sales
	 * @param id Identificador unico del Pedido de Venta
	 * @param project Identificador del Proyecto
	 * @param customer Identificador del Cliente
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param purchase_reference Codigo de referencia del Pedido de Compra
	 * @param shipping_address Identificador de la Direccion de envio del Pedido
	 * @param seller Identificador del Agente Comercial
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param purchase_generated Indica si se han generado los Pedidos de Compra derivados
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSales(Integer project, Integer customer, String series, Integer number, String purchase_reference, Integer shipping_address, Integer seller, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean purchase_generated, Date delivery_date, Integer carrier, Integer carrier_packing, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForSales( carrier , customer , pay_method , project , shipping_address , scope , seller , workplace);
		Integer id =  super.insertSales( domain != null ? domain : getDefaultDomain(), project, customer, series, number, purchase_reference, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, purchase_generated, delivery_date, carrier, carrier_packing, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			salesDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Sales
	 * @param id Identificador unico del Pedido de Venta
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param customer Identificador del Cliente
	 * @param series Serie del Pedido
	 * @param number Numero del Pedido
	 * @param purchase_reference Codigo de referencia del Pedido de Compra
	 * @param shipping_address Identificador de la Direccion de envio del Pedido
	 * @param seller Identificador del Agente Comercial
	 * @param discount_expr Descuentos del Pedido
	 * @param issue_date Fecha de emision del Pedido
	 * @param pay_method Identificador de la Forma de Pago
	 * @param document_type Tipo de Pedido
	 * @param security_level Nivel de seguridad del Pedido
	 * @param status Estado del Pedido
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Ambito del Pedido
	 * @param number_of_pymnts Numero de Vencimientos
	 * @param days_to_first_pymnt Dias al primer Vencimiento
	 * @param days_between_pymnts Dias entre Vencimientos
	 * @param pymnt_days Dias de pago
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bank_alias Alias del Banco
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param purchase_generated Indica si se han generado los Pedidos de Compra derivados
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param shipping_alternative_address Primera parte de la Direccion de entrega
	 * @param shipping_alternative_address2 Segunda parte de la Direccion de entrega
	 * @param shipping_alternative_zip Codigo Postal de entrega
	 * @param shipping_alternative_city Localidad de entrega
	 * @param shipping_alternative_phone Telefono de contacto de la entrega
	 * @param shipping_alternative_recipient Destinatario de la entrega
	 * @param shipping_contact Nombre del contacto para la entrega
	 * @param shipping_period Tipo de periodo de entrega
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSales(Integer domain, Integer project, Integer customer, String series, Integer number, String purchase_reference, Integer shipping_address, Integer seller, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, String bank_account, String bank_alias, String bic, Boolean purchase_generated, Date delivery_date, Integer carrier, Integer carrier_packing, String shipping_alternative_address, String shipping_alternative_address2, String shipping_alternative_zip, String shipping_alternative_city, String shipping_alternative_phone, String shipping_alternative_recipient, String shipping_contact, Short shipping_period, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertSales(domain, project, customer, series, number, purchase_reference, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank_account, bank_alias, bic, purchase_generated, delivery_date, carrier, carrier_packing, shipping_alternative_address, shipping_alternative_address2, shipping_alternative_zip, shipping_alternative_city, shipping_alternative_phone, shipping_alternative_recipient, shipping_contact, shipping_period, creation_user, creation_date, modification_user, modification_date);
		salesDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> web_infoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForWeb_infoPk(Integer id){
		return web_infoDomains.get(id);
	}
	
	/**
	 * Web_info
	 * @param company Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForWeb_info( Integer company){
		Integer domain = null;
			if ( ( domain = getDomainForCompanyPk( company ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Web_info
	 * @param id Identificador Unico
	 * @param company Empresa
	 * @param commercial_description Descripcion comercial
	 * @param schedule Horario
	 * @param slogan Slogan
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info(Integer company, String commercial_description, String schedule, String slogan)
	throws SQLException {
		Integer domain = getDomainForWeb_info( company);
		Integer id =  super.insertWeb_info( domain != null ? domain : getDefaultDomain(), company, commercial_description, schedule, slogan );
		if ( domain != null ) { 
			web_infoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Web_info
	 * @param id Identificador Unico
	 * @param domain Identificador del Dominio
	 * @param company Empresa
	 * @param commercial_description Descripcion comercial
	 * @param schedule Horario
	 * @param slogan Slogan
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertWeb_info(Integer domain, Integer company, String commercial_description, String schedule, String slogan)
	throws SQLException {
		Integer id =  super.insertWeb_info(domain, company, commercial_description, schedule, slogan);
		web_infoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> finance_trackingDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFinance_trackingPk(Integer id){
		return finance_trackingDomains.get(id);
	}
	
	/**
	 * Finance_tracking
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param finance Identificador de Vencimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la CompaÒia
	 * @returns domain's ID
	*/
	protected Integer getDomainForFinance_tracking( Integer bank_statement_link , Integer finance , Integer pm_type_detail , Integer rbank){
		Integer domain = null;
			if ( ( domain = getDomainForBank_statement_linkPk( bank_statement_link ) ) != null )
				return domain;
			if ( ( domain = getDomainForFinancePk( finance ) ) != null )
				return domain;
			if ( ( domain = getDomainForPm_type_detailPk( pm_type_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForRbankPk( rbank ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Finance_tracking
	 * @param id Identificador unico
	 * @param finance Identificador de Vencimiento
	 * @param tracking_date Fecha de Seguimiento
	 * @param type Tipo de Seguimiento
	 * @param description Descripcion del Seguimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la CompaÒia
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance_tracking(Integer finance, Date tracking_date, Short type, String description, Integer pm_type_detail, Integer rbank, Integer bank_statement_link, Double amount, Boolean recorded, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFinance_tracking( bank_statement_link , finance , pm_type_detail , rbank);
		Integer id =  super.insertFinance_tracking( domain != null ? domain : getDefaultDomain(), finance, tracking_date, type, description, pm_type_detail, rbank, bank_statement_link, amount, recorded, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			finance_trackingDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Finance_tracking
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param finance Identificador de Vencimiento
	 * @param tracking_date Fecha de Seguimiento
	 * @param type Tipo de Seguimiento
	 * @param description Descripcion del Seguimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la CompaÒia
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFinance_tracking(Integer domain, Integer finance, Date tracking_date, Short type, String description, Integer pm_type_detail, Integer rbank, Integer bank_statement_link, Double amount, Boolean recorded, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFinance_tracking(domain, finance, tracking_date, type, description, pm_type_detail, rbank, bank_statement_link, amount, recorded, creation_user, creation_date, modification_user, modification_date);
		finance_trackingDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rattachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRattachPk(Integer id){
		return rattachDomains.get(id);
	}
	
	/**
	 * Rattach
	 * @param category Categoria del Archivo Adjunto
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param scope Ambito del Archivo Adjunto
	 * @returns domain's ID
	*/
	protected Integer getDomainForRattach( Integer category , Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForCategoryPk( category ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rattach
	 * @param id Identificador unico del Archivo Adjunto de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param category Categoria del Archivo Adjunto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param drive_id 
	 * @param dparent_id 
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRattach(Integer registry, Integer category, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Date attach_date, String drive_id, String dparent_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForRattach( category , registry , scope);
		Integer id =  super.insertRattach( domain != null ? domain : getDefaultDomain(), registry, category, mimeType, description, data, type, scope, security_level, attach_date, drive_id, dparent_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			rattachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rattach
	 * @param id Identificador unico del Archivo Adjunto de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param category Categoria del Archivo Adjunto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param drive_id 
	 * @param dparent_id 
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRattach(Integer domain, Integer registry, Integer category, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Date attach_date, String drive_id, String dparent_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertRattach(domain, registry, category, mimeType, description, data, type, scope, security_level, attach_date, drive_id, dparent_id, creation_user, creation_date, modification_user, modification_date);
		rattachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreement_level_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_level_dataPk(Integer id){
		return agreement_level_dataDomains.get(id);
	}
	
	/**
	 * Agreement_level_data
	 * @param agreement_level Nivel retributivo
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_level_data( Integer agreement_level){
		Integer domain = null;
			if ( ( domain = getDomainForAgreement_levelPk( agreement_level ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_level_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param agreement_level Nivel retributivo
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level_data(String name, Integer agreement_level, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForAgreement_level_data( agreement_level);
		Integer id =  super.insertAgreement_level_data( domain != null ? domain : getDefaultDomain(), name, agreement_level, expression, start_date, end_date );
		if ( domain != null ) { 
			agreement_level_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_level_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param agreement_level Nivel retributivo
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_level_data(Integer domain, String name, Integer agreement_level, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertAgreement_level_data(domain, name, agreement_level, expression, start_date, end_date);
		agreement_level_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> campaign_projectDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCampaign_projectPk(Integer id){
		return campaign_projectDomains.get(id);
	}
	
	/**
	 * Campaign_project
	 * @param campaign Identificador de la CampaÒa
	 * @param project Identificador del Expediente
	 * @returns domain's ID
	*/
	protected Integer getDomainForCampaign_project( Integer campaign , Integer project){
		Integer domain = null;
			if ( ( domain = getDomainForCampaignPk( campaign ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Campaign_project
	 * @param id Identificador unico de la Relacion de CampaÒas y Expedientes
	 * @param campaign Identificador de la CampaÒa
	 * @param project Identificador del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign_project(Integer campaign, Integer project)
	throws SQLException {
		Integer domain = getDomainForCampaign_project( campaign , project);
		Integer id =  super.insertCampaign_project( domain != null ? domain : getDefaultDomain(), campaign, project );
		if ( domain != null ) { 
			campaign_projectDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Campaign_project
	 * @param id Identificador unico de la Relacion de CampaÒas y Expedientes
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la CampaÒa
	 * @param project Identificador del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCampaign_project(Integer domain, Integer campaign, Integer project)
	throws SQLException {
		Integer id =  super.insertCampaign_project(domain, campaign, project);
		campaign_projectDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> question_valueDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForQuestion_valuePk(Integer id){
		return question_valueDomains.get(id);
	}
	
	/**
	 * Question_value
	 * @param question Identificador de la Pregunta
	 * @returns domain's ID
	*/
	protected Integer getDomainForQuestion_value( Integer question){
		Integer domain = null;
			if ( ( domain = getDomainForQuestionPk( question ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Question_value
	 * @param id Identificador unico
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuestion_value(Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer domain = getDomainForQuestion_value( question);
		Integer id =  super.insertQuestion_value( domain != null ? domain : getDefaultDomain(), question, value_text, value_number, value_date );
		if ( domain != null ) { 
			question_valueDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Question_value
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQuestion_value(Integer domain, Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer id =  super.insertQuestion_value(domain, question, value_text, value_number, value_date);
		question_valueDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> sales_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSales_detailPk(Integer id){
		return sales_detailDomains.get(id);
	}
	
	/**
	 * Sales_detail
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param offer_detail Identificador del Detalle del Presupuesto Origen
	 * @param sales Identificador del Pedido de Venta
	 * @returns domain's ID
	*/
	protected Integer getDomainForSales_detail( Integer carrier , Integer carrier_packing , Integer item , Integer offer_detail , Integer sales){
		Integer domain = null;
			if ( ( domain = getDomainForCarrierPk( carrier ) ) != null )
				return domain;
			if ( ( domain = getDomainForCarrier_packingPk( carrier_packing ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForOffer_detailPk( offer_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForSalesPk( sales ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Sales_detail
	 * @param id Identificador unico del Detalle del Pedido de Venta
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de linea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param offer_detail Identificador del Detalle del Presupuesto Origen
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSales_detail(Integer sales, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Integer offer_detail, Double delivered, Date delivery_date, Integer carrier, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForSales_detail( carrier , carrier_packing , item , offer_detail , sales);
		Integer id =  super.insertSales_detail( domain != null ? domain : getDefaultDomain(), sales, line, item, description, quantity, price, discount_expr, taxes, status, offer_detail, delivered, delivery_date, carrier, carrier_packing, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			sales_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Sales_detail
	 * @param id Identificador unico del Detalle del Pedido de Venta
	 * @param domain Identificador del Dominio
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de linea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param offer_detail Identificador del Detalle del Presupuesto Origen
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @param delivery_date Fecha de entrega
	 * @param carrier Identificador de la Agencia de Transporte
	 * @param carrier_packing Identificador de la Hoja de ruta
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSales_detail(Integer domain, Integer sales, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Integer offer_detail, Double delivered, Date delivery_date, Integer carrier, Integer carrier_packing, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertSales_detail(domain, sales, line, item, description, quantity, price, discount_expr, taxes, status, offer_detail, delivered, delivery_date, carrier, carrier_packing, creation_user, creation_date, modification_user, modification_date);
		sales_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> holiday_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForHoliday_detailPk(Integer id){
		return holiday_detailDomains.get(id);
	}
	
	/**
	 * Holiday_detail
	 * @param holiday Identificador de Festividad
	 * @returns domain's ID
	*/
	protected Integer getDomainForHoliday_detail( Integer holiday){
		Integer domain = null;
			if ( ( domain = getDomainForHolidayPk( holiday ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Holiday_detail
	 * @param id Identificador unico
	 * @param holiday Identificador de Festividad
	 * @param date Fecha Festiva
	 * @param description Descripcion de Festividad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHoliday_detail(Integer holiday, Date date, String description)
	throws SQLException {
		Integer domain = getDomainForHoliday_detail( holiday);
		Integer id =  super.insertHoliday_detail( domain != null ? domain : getDefaultDomain(), holiday, date, description );
		if ( domain != null ) { 
			holiday_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Holiday_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param holiday Identificador de Festividad
	 * @param date Fecha Festiva
	 * @param description Descripcion de Festividad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertHoliday_detail(Integer domain, Integer holiday, Date date, String description)
	throws SQLException {
		Integer id =  super.insertHoliday_detail(domain, holiday, date, description);
		holiday_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_leaveDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_leavePk(Integer id){
		return contract_leaveDomains.get(id);
	}
	
	/**
	 * Contract_leave
	 * @param contract Contrato
	 * @param parent Baja origen, si es recaida
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_leave( Integer contract , Integer parent){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForContract_leavePk( parent ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_leave
	 * @param id Identificador unico
	 * @param type Tipo de Baja
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param daily_cgc_base Base de cotizacion por contingencias comunes
	 * @param daily_cgp_base Base de cotizacion por contingencias profesionales
	 * @param parent Baja origen, si es recaida
	 * @param daily_reg_base Base reguladora
	 * @param discharge_cause Causa del alta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_leave(Short type, Integer contract, String description, Date start_date, Date end_date, Double daily_cgc_base, Double daily_cgp_base, Integer parent, Double daily_reg_base, Short discharge_cause)
	throws SQLException {
		Integer domain = getDomainForContract_leave( contract , parent);
		Integer id =  super.insertContract_leave( domain != null ? domain : getDefaultDomain(), type, contract, description, start_date, end_date, daily_cgc_base, daily_cgp_base, parent, daily_reg_base, discharge_cause );
		if ( domain != null ) { 
			contract_leaveDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_leave
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Baja
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param daily_cgc_base Base de cotizacion por contingencias comunes
	 * @param daily_cgp_base Base de cotizacion por contingencias profesionales
	 * @param parent Baja origen, si es recaida
	 * @param daily_reg_base Base reguladora
	 * @param discharge_cause Causa del alta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_leave(Integer domain, Short type, Integer contract, String description, Date start_date, Date end_date, Double daily_cgc_base, Double daily_cgp_base, Integer parent, Double daily_reg_base, Short discharge_cause)
	throws SQLException {
		Integer id =  super.insertContract_leave(domain, type, contract, description, start_date, end_date, daily_cgc_base, daily_cgp_base, parent, daily_reg_base, discharge_cause);
		contract_leaveDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> enterprise_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForEnterprise_dataPk(Integer id){
		return enterprise_dataDomains.get(id);
	}
	
	/**
	 * Enterprise_data
	 * @param enterprise Identificador de Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForEnterprise_data( Integer enterprise){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Enterprise_data
	 * @param id Identificador unico
	 * @param enterprise Identificador de Empresa
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_data(Integer enterprise, String name, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForEnterprise_data( enterprise);
		Integer id =  super.insertEnterprise_data( domain != null ? domain : getDefaultDomain(), enterprise, name, expression, start_date, end_date );
		if ( domain != null ) { 
			enterprise_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Enterprise_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de Empresa
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_data(Integer domain, Integer enterprise, String name, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertEnterprise_data(domain, enterprise, name, expression, start_date, end_date);
		enterprise_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> asset_activityDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAsset_activityPk(Integer id){
		return asset_activityDomains.get(id);
	}
	
	/**
	 * Asset_activity
	 * @param asset Identificador del Activo
	 * @returns domain's ID
	*/
	protected Integer getDomainForAsset_activity( Integer asset){
		Integer domain = null;
			if ( ( domain = getDomainForAssetPk( asset ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Asset_activity
	 * @param id Identificador unico
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
	public int insertAsset_activity(Integer asset, Date date, Timestamp from_time, Timestamp to_time, String who, String why, Short status)
	throws SQLException {
		Integer domain = getDomainForAsset_activity( asset);
		Integer id =  super.insertAsset_activity( domain != null ? domain : getDefaultDomain(), asset, date, from_time, to_time, who, why, status );
		if ( domain != null ) { 
			asset_activityDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Asset_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
	public int insertAsset_activity(Integer domain, Integer asset, Date date, Timestamp from_time, Timestamp to_time, String who, String why, Short status)
	throws SQLException {
		Integer id =  super.insertAsset_activity(domain, asset, date, from_time, to_time, who, why, status);
		asset_activityDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_attachDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_attachPk(Integer id){
		return contract_attachDomains.get(id);
	}
	
	/**
	 * Contract_attach
	 * @param contract Identificador del Registro del contrato
	 * @param scope Ambito del Archivo Adjunto
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_attach( Integer contract , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_attach
	 * @param id Identificador unico del Archivo Adjunto del contrato
	 * @param contract Identificador del Registro del contrato
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_attach(Integer contract, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Timestamp attach_date, String driveId)
	throws SQLException {
		Integer domain = getDomainForContract_attach( contract , scope);
		Integer id =  super.insertContract_attach( domain != null ? domain : getDefaultDomain(), contract, mimeType, description, data, type, scope, security_level, attach_date, driveId );
		if ( domain != null ) { 
			contract_attachDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_attach
	 * @param id Identificador unico del Archivo Adjunto del contrato
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del Registro del contrato
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param security_level Nivel de seguridad del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @param driveId 
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_attach(Integer domain, Integer contract, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Timestamp attach_date, String driveId)
	throws SQLException {
		Integer id =  super.insertContract_attach(domain, contract, mimeType, description, data, type, scope, security_level, attach_date, driveId);
		contract_attachDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tagDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTagPk(Integer id){
		return tagDomains.get(id);
	}
	
	/**
	 * Tag
	 * @returns domain's ID
	*/
	protected Integer getDomainForTag(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Tag
	 * @param id Identificador unico
	 * @param name Nombre de la Etiqueta
	 * @param type Tipo de Etiqueta
	 * @param color Color de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTag(String name, Short type, String color)
	throws SQLException {
		Integer domain = getDomainForTag();
		Integer id =  super.insertTag( domain != null ? domain : getDefaultDomain(), name, type, color );
		if ( domain != null ) { 
			tagDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tag
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Etiqueta
	 * @param type Tipo de Etiqueta
	 * @param color Color de la Etiqueta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTag(Integer domain, String name, Short type, String color)
	throws SQLException {
		Integer id =  super.insertTag(domain, name, type, color);
		tagDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> stockDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForStockPk(Integer id){
		return stockDomains.get(id);
	}
	
	/**
	 * Stock
	 * @param item Identificador del Articulo
	 * @param warehouse Identificador del Almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForStock( Integer item , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Stock
	 * @param id Identificador unico del Stock
	 * @param warehouse Identificador del Almacen
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStock(Integer warehouse, Integer item, Double quantity)
	throws SQLException {
		Integer domain = getDomainForStock( item , warehouse);
		Integer id =  super.insertStock( domain != null ? domain : getDefaultDomain(), warehouse, item, quantity );
		if ( domain != null ) { 
			stockDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Stock
	 * @param id Identificador unico del Stock
	 * @param domain Identificador del Dominio
	 * @param warehouse Identificador del Almacen
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertStock(Integer domain, Integer warehouse, Integer item, Double quantity)
	throws SQLException {
		Integer id =  super.insertStock(domain, warehouse, item, quantity);
		stockDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> pcategoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPcategoryPk(Integer id){
		return pcategoryDomains.get(id);
	}
	
	/**
	 * Pcategory
	 * @returns domain's ID
	*/
	protected Integer getDomainForPcategory(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Pcategory
	 * @param id Identificador unico de la Categoria
	 * @param name Nombre de la Categoria
	 * @param detail Nombre del detalle de los Articulos
	 * @param detail2 Nombre del detalle 2 de los Articulos
	 * @param detail3 Nombre del detalle 3 de los Articulos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPcategory(String name, String detail, String detail2, String detail3)
	throws SQLException {
		Integer domain = getDomainForPcategory();
		Integer id =  super.insertPcategory( domain != null ? domain : getDefaultDomain(), name, detail, detail2, detail3 );
		if ( domain != null ) { 
			pcategoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Pcategory
	 * @param id Identificador unico de la Categoria
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Categoria
	 * @param detail Nombre del detalle de los Articulos
	 * @param detail2 Nombre del detalle 2 de los Articulos
	 * @param detail3 Nombre del detalle 3 de los Articulos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertPcategory(Integer domain, String name, String detail, String detail2, String detail3)
	throws SQLException {
		Integer id =  super.insertPcategory(domain, name, detail, detail2, detail3);
		pcategoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> survey_workflowDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSurvey_workflowPk(Integer id){
		return survey_workflowDomains.get(id);
	}
	
	/**
	 * Survey_workflow
	 * @param nextSurveyQuestion Identificador de la siguiente Pregunta del Cuestionario
	 * @param questionValue Identificador del Valor de la Pregunta
	 * @param surveyQuestion Identificador de la Pregunta del Cuestionario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSurvey_workflow( Integer nextSurveyQuestion , Integer questionValue , Integer surveyQuestion){
		Integer domain = null;
			if ( ( domain = getDomainForSurvey_questionPk( nextSurveyQuestion ) ) != null )
				return domain;
			if ( ( domain = getDomainForQuestion_valuePk( questionValue ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurvey_questionPk( surveyQuestion ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Survey_workflow
	 * @param id Identificador unico
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
	public int insertSurvey_workflow(Integer questionValue, Integer surveyQuestion, Integer nextSurveyQuestion, Short operator, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer domain = getDomainForSurvey_workflow( nextSurveyQuestion , questionValue , surveyQuestion);
		Integer id =  super.insertSurvey_workflow( domain != null ? domain : getDefaultDomain(), questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date );
		if ( domain != null ) { 
			survey_workflowDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Survey_workflow
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
	public int insertSurvey_workflow(Integer domain, Integer questionValue, Integer surveyQuestion, Integer nextSurveyQuestion, Short operator, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		Integer id =  super.insertSurvey_workflow(domain, questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date);
		survey_workflowDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tariff_catalogueDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTariff_cataloguePk(Integer id){
		return tariff_catalogueDomains.get(id);
	}
	
	/**
	 * Tariff_catalogue
	 * @param catalogue Identificador del Catalogo
	 * @param tariff Identificador de la Tarifa
	 * @returns domain's ID
	*/
	protected Integer getDomainForTariff_catalogue( Integer catalogue , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForCataloguePk( catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Tariff_catalogue
	 * @param id Identificador unico
	 * @param tariff Identificador de la Tarifa
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff_catalogue(Integer tariff, Integer catalogue)
	throws SQLException {
		Integer domain = getDomainForTariff_catalogue( catalogue , tariff);
		Integer id =  super.insertTariff_catalogue( domain != null ? domain : getDefaultDomain(), tariff, catalogue );
		if ( domain != null ) { 
			tariff_catalogueDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tariff_catalogue
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param tariff Identificador de la Tarifa
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTariff_catalogue(Integer domain, Integer tariff, Integer catalogue)
	throws SQLException {
		Integer id =  super.insertTariff_catalogue(domain, tariff, catalogue);
		tariff_catalogueDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> allotmentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAllotmentPk(Integer id){
		return allotmentDomains.get(id);
	}
	
	/**
	 * Allotment
	 * @param agency Identificador de la agencia de viajes
	 * @param agency_group Identificador del Grupo de agencias
	 * @param hotel Identificador del Hotel
	 * @returns domain's ID
	*/
	protected Integer getDomainForAllotment( Integer agency , Integer agency_group , Integer hotel){
		Integer domain = null;
			if ( ( domain = getDomainForCustomerPk( agency ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicing_groupPk( agency_group ) ) != null )
				return domain;
			if ( ( domain = getDomainForHotelPk( hotel ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Allotment
	 * @param id Identificador unico
	 * @param hotel Identificador del Hotel
	 * @param rate_code Codigo de Cupo
	 * @param agency Identificador de la agencia de viajes
	 * @param agency_group Identificador del Grupo de agencias
	 * @param start_date Fecha de inicio del Cupo
	 * @param end_date Fecha de fin del Cupo
	 * @param quantity Cupo de Habitaciones
	 * @param remarks Comentarios
	 * @param active Indica si el Cupo esta activo o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment(Integer hotel, String rate_code, Integer agency, Integer agency_group, Date start_date, Date end_date, Integer quantity, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForAllotment( agency , agency_group , hotel);
		Integer id =  super.insertAllotment( domain != null ? domain : getDefaultDomain(), hotel, rate_code, agency, agency_group, start_date, end_date, quantity, remarks, active, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			allotmentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Allotment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param rate_code Codigo de Cupo
	 * @param agency Identificador de la agencia de viajes
	 * @param agency_group Identificador del Grupo de agencias
	 * @param start_date Fecha de inicio del Cupo
	 * @param end_date Fecha de fin del Cupo
	 * @param quantity Cupo de Habitaciones
	 * @param remarks Comentarios
	 * @param active Indica si el Cupo esta activo o no
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAllotment(Integer domain, Integer hotel, String rate_code, Integer agency, Integer agency_group, Date start_date, Date end_date, Integer quantity, String remarks, Boolean active, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertAllotment(domain, hotel, rate_code, agency, agency_group, start_date, end_date, quantity, remarks, active, creation_user, creation_date, modification_user, modification_date);
		allotmentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> elaborationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForElaborationPk(Integer id){
		return elaborationDomains.get(id);
	}
	
	/**
	 * Elaboration
	 * @param item Identificador del articulo base a elaborar
	 * @param warehouse Identificador del almacen
	 * @returns domain's ID
	*/
	protected Integer getDomainForElaboration( Integer item , Integer warehouse){
		Integer domain = null;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForWarehousePk( warehouse ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Elaboration
	 * @param id Identificador unico de la elaboracion
	 * @param series Serie de la elaboracion
	 * @param number Numero de la elaboracion
	 * @param date Fecha de elaboracion
	 * @param item Identificador del articulo base a elaborar
	 * @param description Descripcion
	 * @param warehouse Identificador del almacen
	 * @param quantity Cantidad a elaborar
	 * @param status Indica el estado de la elaboracion
	 * @param comments Comentarios
	 * @param remarks Observaciones
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration(String series, Integer number, Timestamp date, Integer item, String description, Integer warehouse, Double quantity, Short status, String comments, String remarks, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForElaboration( item , warehouse);
		Integer id =  super.insertElaboration( domain != null ? domain : getDefaultDomain(), series, number, date, item, description, warehouse, quantity, status, comments, remarks, source, source_id, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			elaborationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Elaboration
	 * @param id Identificador unico de la elaboracion
	 * @param domain Identificador del dominio
	 * @param series Serie de la elaboracion
	 * @param number Numero de la elaboracion
	 * @param date Fecha de elaboracion
	 * @param item Identificador del articulo base a elaborar
	 * @param description Descripcion
	 * @param warehouse Identificador del almacen
	 * @param quantity Cantidad a elaborar
	 * @param status Indica el estado de la elaboracion
	 * @param comments Comentarios
	 * @param remarks Observaciones
	 * @param source Origen
	 * @param source_id Identificador del origen
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertElaboration(Integer domain, String series, Integer number, Timestamp date, Integer item, String description, Integer warehouse, Double quantity, Short status, String comments, String remarks, Short source, Integer source_id, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertElaboration(domain, series, number, date, item, description, warehouse, quantity, status, comments, remarks, source, source_id, creation_user, creation_date, modification_user, modification_date);
		elaborationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> system_costDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSystem_costPk(Integer id){
		return system_costDomains.get(id);
	}
	
	/**
	 * System_cost
	 * @returns domain's ID
	*/
	protected Integer getDomainForSystem_cost(){
		Integer domain = null;
		return domain;
	}

	/**
	 * System_cost
	 * @param id Identificador unico
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param description Descripcion
	 * @param expression Expresion
	 * @param type Tipo de Costo
	 * @param code CÛdigo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_cost(Date start_date, Date end_date, String description, String expression, Short type, String code)
	throws SQLException {
		Integer domain = getDomainForSystem_cost();
		Integer id =  super.insertSystem_cost( domain != null ? domain : getDefaultDomain(), start_date, end_date, description, expression, type, code );
		if ( domain != null ) { 
			system_costDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * System_cost
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param description Descripcion
	 * @param expression Expresion
	 * @param type Tipo de Costo
	 * @param code CÛdigo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_cost(Integer domain, Date start_date, Date end_date, String description, String expression, Short type, String code)
	throws SQLException {
		Integer id =  super.insertSystem_cost(domain, start_date, end_date, description, expression, type, code);
		system_costDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> taxDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTaxPk(Integer id){
		return taxDomains.get(id);
	}
	
	/**
	 * Tax
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @returns domain's ID
	*/
	protected Integer getDomainForTax( Integer purchase_account , Integer sales_account){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( purchase_account ) ) != null )
				return domain;
			if ( ( domain = getDomainForAccountPk( sales_account ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Tax
	 * @param id Identificador unico del Impuesto
	 * @param name Nombre del Impuesto
	 * @param tax_type Tipo de Impuesto
	 * @param percentage Porcentaje de recargo actual
	 * @param surcharge Porcentaje de recargo de equivalencia actual
	 * @param start_date Fecha de inicio de vigencia
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTax(String name, Short tax_type, Double percentage, Double surcharge, Date start_date, Short vat_deduction_type, Short withholding_type, Integer sales_account, Integer purchase_account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTax( purchase_account , sales_account);
		Integer id =  super.insertTax( domain != null ? domain : getDefaultDomain(), name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type, sales_account, purchase_account, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			taxDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tax
	 * @param id Identificador unico del Impuesto
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Impuesto
	 * @param tax_type Tipo de Impuesto
	 * @param percentage Porcentaje de recargo actual
	 * @param surcharge Porcentaje de recargo de equivalencia actual
	 * @param start_date Fecha de inicio de vigencia
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param sales_account Identificador de la Cuenta Contable de Ventas
	 * @param purchase_account Identificador de la Cuenta Contable de Compras
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTax(Integer domain, String name, Short tax_type, Double percentage, Double surcharge, Date start_date, Short vat_deduction_type, Short withholding_type, Integer sales_account, Integer purchase_account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertTax(domain, name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type, sales_account, purchase_account, creation_user, creation_date, modification_user, modification_date);
		taxDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> user_scopeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForUser_scopePk(Integer id){
		return user_scopeDomains.get(id);
	}
	
	/**
	 * User_scope
	 * @param scope Identificador del Ambito
	 * @param user_id Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForUser_scope( Integer scope , Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * User_scope
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser_scope(Integer user_id, Integer scope)
	throws SQLException {
		Integer domain = getDomainForUser_scope( scope , user_id);
		Integer id =  super.insertUser_scope( domain != null ? domain : getDefaultDomain(), user_id, scope );
		if ( domain != null ) { 
			user_scopeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * User_scope
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param user_id Identificador del Usuario
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser_scope(Integer domain, Integer user_id, Integer scope)
	throws SQLException {
		Integer id =  super.insertUser_scope(domain, user_id, scope);
		user_scopeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fbatch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFbatch_detailPk(Integer id){
		return fbatch_detailDomains.get(id);
	}
	
	/**
	 * Fbatch_detail
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @returns domain's ID
	*/
	protected Integer getDomainForFbatch_detail( Integer fbatch , Integer finance){
		Integer domain = null;
			if ( ( domain = getDomainForFbatchPk( fbatch ) ) != null )
				return domain;
			if ( ( domain = getDomainForFinancePk( finance ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fbatch_detail
	 * @param id Identificador unico del Detalle de la Remesa
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @param amount Importe del Detalle de la Remesa
	 * @param status Estado del Detalle de la Remesa
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFbatch_detail(Integer fbatch, Integer finance, Double amount, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForFbatch_detail( fbatch , finance);
		Integer id =  super.insertFbatch_detail( domain != null ? domain : getDefaultDomain(), fbatch, finance, amount, status, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			fbatch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fbatch_detail
	 * @param id Identificador unico del Detalle de la Remesa
	 * @param domain Identificador del Dominio
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @param amount Importe del Detalle de la Remesa
	 * @param status Estado del Detalle de la Remesa
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFbatch_detail(Integer domain, Integer fbatch, Integer finance, Double amount, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertFbatch_detail(domain, fbatch, finance, amount, status, creation_user, creation_date, modification_user, modification_date);
		fbatch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> leave_batch_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForLeave_batch_detailPk(Integer id){
		return leave_batch_detailDomains.get(id);
	}
	
	/**
	 * Leave_batch_detail
	 * @param contract_leave_detail Identificador unico del parte
	 * @param leave_batch Identificador unico de la remesa
	 * @returns domain's ID
	*/
	protected Integer getDomainForLeave_batch_detail( Integer contract_leave_detail , Integer leave_batch){
		Integer domain = null;
			if ( ( domain = getDomainForContract_leave_detailPk( contract_leave_detail ) ) != null )
				return domain;
			if ( ( domain = getDomainForLeave_batchPk( leave_batch ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Leave_batch_detail
	 * @param id Identificador unico
	 * @param leave_batch Identificador unico de la remesa
	 * @param contract_leave_detail Identificador unico del parte
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLeave_batch_detail(Integer leave_batch, Integer contract_leave_detail)
	throws SQLException {
		Integer domain = getDomainForLeave_batch_detail( contract_leave_detail , leave_batch);
		Integer id =  super.insertLeave_batch_detail( domain != null ? domain : getDefaultDomain(), leave_batch, contract_leave_detail );
		if ( domain != null ) { 
			leave_batch_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Leave_batch_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param leave_batch Identificador unico de la remesa
	 * @param contract_leave_detail Identificador unico del parte
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertLeave_batch_detail(Integer domain, Integer leave_batch, Integer contract_leave_detail)
	throws SQLException {
		Integer id =  super.insertLeave_batch_detail(domain, leave_batch, contract_leave_detail);
		leave_batch_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> process_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProcess_detailPk(Integer id){
		return process_detailDomains.get(id);
	}
	
	/**
	 * Process_detail
	 * @param process Identificador del Proceso
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns domain's ID
	*/
	protected Integer getDomainForProcess_detail( Integer process , Integer workgroup){
		Integer domain = null;
			if ( ( domain = getDomainForProcessPk( process ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( workgroup ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Process_detail
	 * @param id Identificador unico del Detalle de Proceso
	 * @param process Identificador del Proceso
	 * @param description Descripcion del Detalle de Proceso
	 * @param position Orden de ejecucion del Detalle dentro del Proceso
	 * @param date_reference Referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param days Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param alert_days Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @param priority Prioridad de la Tarea
	 * @param active Activo si o no
	 * @param comments Comentarios del Detalle de Proceso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_detail(Integer process, String description, Integer position, Short date_reference, Integer days, Integer alert_days, Integer workgroup, Short priority, Boolean active, String comments)
	throws SQLException {
		Integer domain = getDomainForProcess_detail( process , workgroup);
		Integer id =  super.insertProcess_detail( domain != null ? domain : getDefaultDomain(), process, description, position, date_reference, days, alert_days, workgroup, priority, active, comments );
		if ( domain != null ) { 
			process_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Process_detail
	 * @param id Identificador unico del Detalle de Proceso
	 * @param domain Identificador del Dominio
	 * @param process Identificador del Proceso
	 * @param description Descripcion del Detalle de Proceso
	 * @param position Orden de ejecucion del Detalle dentro del Proceso
	 * @param date_reference Referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param days Numero de dias asociado a la referencia para el calculo de la fecha de vencimiento de la Tarea
	 * @param alert_days Numero de dias, previos a la fecha de vencimiento de la Tarea, para el calculo de la fecha de generacion de la Alarma
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @param priority Prioridad de la Tarea
	 * @param active Activo si o no
	 * @param comments Comentarios del Detalle de Proceso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess_detail(Integer domain, Integer process, String description, Integer position, Short date_reference, Integer days, Integer alert_days, Integer workgroup, Short priority, Boolean active, String comments)
	throws SQLException {
		Integer id =  super.insertProcess_detail(domain, process, description, position, date_reference, days, alert_days, workgroup, priority, active, comments);
		process_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> survey_responseDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSurvey_responsePk(Integer id){
		return survey_responseDomains.get(id);
	}
	
	/**
	 * Survey_response
	 * @param campaign_action Identificador de la Accion de la CampaÒa
	 * @param registry Identificador del Registro que responde al Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param user Identificador del Usuario
	 * @returns domain's ID
	*/
	protected Integer getDomainForSurvey_response( Integer campaign_action , Integer registry , Integer survey , Integer user){
		Integer domain = null;
			if ( ( domain = getDomainForMk_actionPk( campaign_action ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForSurveyPk( survey ) ) != null )
				return domain;
			if ( ( domain = getDomainForUserPk( user ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Survey_response
	 * @param id Identificador unico
	 * @param creationDate Fecha de la creacion en el sistema de la Respuesta del Cuestionario
	 * @param response_date Fecha de la Respuesta del Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param registry Identificador del Registro que responde al Cuestionario
	 * @param user Identificador del Usuario
	 * @param campaign_action Identificador de la Accion de la CampaÒa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_response(Timestamp creationDate, Timestamp response_date, Integer survey, Integer registry, Integer user, Integer campaign_action)
	throws SQLException {
		Integer domain = getDomainForSurvey_response( campaign_action , registry , survey , user);
		Integer id =  super.insertSurvey_response( domain != null ? domain : getDefaultDomain(), creationDate, response_date, survey, registry, user, campaign_action );
		if ( domain != null ) { 
			survey_responseDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Survey_response
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param creationDate Fecha de la creacion en el sistema de la Respuesta del Cuestionario
	 * @param response_date Fecha de la Respuesta del Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param registry Identificador del Registro que responde al Cuestionario
	 * @param user Identificador del Usuario
	 * @param campaign_action Identificador de la Accion de la CampaÒa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey_response(Integer domain, Timestamp creationDate, Timestamp response_date, Integer survey, Integer registry, Integer user, Integer campaign_action)
	throws SQLException {
		Integer id =  super.insertSurvey_response(domain, creationDate, response_date, survey, registry, user, campaign_action);
		survey_responseDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> mk_campaignDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMk_campaignPk(Integer id){
		return mk_campaignDomains.get(id);
	}
	
	/**
	 * Mk_campaign
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForMk_campaign( Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Mk_campaign
	 * @param id Identificador unico
	 * @param active Indica si la CampaÒa esta activa o no
	 * @param description Descripcion de la CampaÒa
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_campaign(Boolean active, String description, Integer scope)
	throws SQLException {
		Integer domain = getDomainForMk_campaign( scope);
		Integer id =  super.insertMk_campaign( domain != null ? domain : getDefaultDomain(), active, description, scope );
		if ( domain != null ) { 
			mk_campaignDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Mk_campaign
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param active Indica si la CampaÒa esta activa o no
	 * @param description Descripcion de la CampaÒa
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMk_campaign(Integer domain, Boolean active, String description, Integer scope)
	throws SQLException {
		Integer id =  super.insertMk_campaign(domain, active, description, scope);
		mk_campaignDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> qualificationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForQualificationPk(Integer id){
		return qualificationDomains.get(id);
	}
	
	/**
	 * Qualification
	 * @returns domain's ID
	*/
	protected Integer getDomainForQualification(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Qualification
	 * @param id Identificador unico de la Calificacion
	 * @param code Codigo de la Calificacion
	 * @param description Descripcion de la Calificacion
	 * @param min_value Limite inferior de la Calificacion
	 * @param max_value Limite superior de la Calificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQualification(String code, String description, Double min_value, Double max_value)
	throws SQLException {
		Integer domain = getDomainForQualification();
		Integer id =  super.insertQualification( domain != null ? domain : getDefaultDomain(), code, description, min_value, max_value );
		if ( domain != null ) { 
			qualificationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Qualification
	 * @param id Identificador unico de la Calificacion
	 * @param domain Identificador del Dominio
	 * @param code Codigo de la Calificacion
	 * @param description Descripcion de la Calificacion
	 * @param min_value Limite inferior de la Calificacion
	 * @param max_value Limite superior de la Calificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertQualification(Integer domain, String code, String description, Double min_value, Double max_value)
	throws SQLException {
		Integer id =  super.insertQualification(domain, code, description, min_value, max_value);
		qualificationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> taskDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTaskPk(Integer id){
		return taskDomains.get(id);
	}
	
	/**
	 * Task
	 * @param activity_type Identificador de la Actividad
	 * @param project Identificador del Expediente
	 * @param registry 
	 * @param sender Remitente de la Tarea
	 * @param task_holder Identificador del Usuario asociado a la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo asociado a la Tarea
	 * @returns domain's ID
	*/
	protected Integer getDomainForTask( Integer activity_type , Integer project , Integer registry , Integer sender , Integer task_holder , Integer workgroup){
		Integer domain = null;
			if ( ( domain = getDomainForActivity_typePk( activity_type ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForTask_holderPk( sender ) ) != null )
				return domain;
			if ( ( domain = getDomainForTask_holderPk( task_holder ) ) != null )
				return domain;
			if ( ( domain = getDomainForWorkgroupPk( workgroup ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Task
	 * @param id Identificador unico de la Tarea
	 * @param number Numero de la Tarea
	 * @param description Descripcion de la Tarea
	 * @param start_date Fecha de inicio de la Tarea
	 * @param end_date Fecha de finalizacion de la Tarea
	 * @param due_date Fecha de vencimiento de la Tarea
	 * @param priority Prioridad de la Tarea
	 * @param status Estado de la Tarea
	 * @param percent Porcentaje de realizacion de la Tarea
	 * @param task_holder Identificador del Usuario asociado a la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo asociado a la Tarea
	 * @param source Origen de la Tarea
	 * @param source_id Identificador del source
	 * @param project Identificador del Expediente
	 * @param registry 
	 * @param activity_type Identificador de la Actividad
	 * @param sender Remitente de la Tarea
	 * @param comments Comentarios de la Tarea
	 * @param repeat_period Periodo de repeticion de la Tarea
	 * @param gtask_id 
	 * @param gtasklist_id 
	 * @param parent Task parent
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask(Integer number, String description, Timestamp start_date, Timestamp end_date, Timestamp due_date, Short priority, Short status, Short percent, Integer task_holder, Integer workgroup, Short source, Integer source_id, Integer project, Integer registry, Integer activity_type, Integer sender, String comments, Short repeat_period, String gtask_id, String gtasklist_id, Integer parent, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTask( activity_type , project , registry , sender , task_holder , workgroup);
		Integer id =  super.insertTask( domain != null ? domain : getDefaultDomain(), number, description, start_date, end_date, due_date, priority, status, percent, task_holder, workgroup, source, source_id, project, registry, activity_type, sender, comments, repeat_period, gtask_id, gtasklist_id, parent, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			taskDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Task
	 * @param id Identificador unico de la Tarea
	 * @param domain Identificador del Dominio
	 * @param number Numero de la Tarea
	 * @param description Descripcion de la Tarea
	 * @param start_date Fecha de inicio de la Tarea
	 * @param end_date Fecha de finalizacion de la Tarea
	 * @param due_date Fecha de vencimiento de la Tarea
	 * @param priority Prioridad de la Tarea
	 * @param status Estado de la Tarea
	 * @param percent Porcentaje de realizacion de la Tarea
	 * @param task_holder Identificador del Usuario asociado a la Tarea
	 * @param workgroup Identificador del Grupo de Trabajo asociado a la Tarea
	 * @param source Origen de la Tarea
	 * @param source_id Identificador del source
	 * @param project Identificador del Expediente
	 * @param registry 
	 * @param activity_type Identificador de la Actividad
	 * @param sender Remitente de la Tarea
	 * @param comments Comentarios de la Tarea
	 * @param repeat_period Periodo de repeticion de la Tarea
	 * @param gtask_id 
	 * @param gtasklist_id 
	 * @param parent Task parent
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTask(Integer domain, Integer number, String description, Timestamp start_date, Timestamp end_date, Timestamp due_date, Short priority, Short status, Short percent, Integer task_holder, Integer workgroup, Short source, Integer source_id, Integer project, Integer registry, Integer activity_type, Integer sender, String comments, Short repeat_period, String gtask_id, String gtasklist_id, Integer parent, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertTask(domain, number, description, start_date, end_date, due_date, priority, status, percent, task_holder, workgroup, source, source_id, project, registry, activity_type, sender, comments, repeat_period, gtask_id, gtasklist_id, parent, creation_user, creation_date, modification_user, modification_date);
		taskDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> personDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForPersonPk(Integer registry){
		return personDomains.get(registry);
	}
	
	/**
	 * Person
	 * @param registry Registro de la Persona
	 * @returns domain's ID
	*/
	protected Integer getDomainForPerson( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Person
	 * @param registry Registro de la Persona
	 * @param birth_date Fecha de nacimiento de la Persona
	 * @param gender Sexo de la Persona
	 * @param marital_status Estado civil de la Persona
	 * @param social_security_num Numero de Seguridad Social de la Persona
	 * @param name Nombre
	 * @param first_surname Primer Apellido 
	 * @param second_surname Segundo Apellido
	 * @throws SQLException
	*/
	public void insertPerson(Integer registry, Date birth_date, Short gender, Short marital_status, String social_security_num, String name, String first_surname, String second_surname)
	throws SQLException {
		Integer domain = getDomainForPerson( registry);
		 super.insertPerson( registry, domain != null ? domain : getDefaultDomain(), birth_date, gender, marital_status, social_security_num, name, first_surname, second_surname );
		if ( domain != null ) { 
			personDomains.put(registry, domain);
		}
	}

	/**
	 * Person
	 * @param registry Registro de la Persona
	 * @param domain Identificador del Dominio
	 * @param birth_date Fecha de nacimiento de la Persona
	 * @param gender Sexo de la Persona
	 * @param marital_status Estado civil de la Persona
	 * @param social_security_num Numero de Seguridad Social de la Persona
	 * @param name Nombre
	 * @param first_surname Primer Apellido 
	 * @param second_surname Segundo Apellido
	 * @throws SQLException
	*/
	public void insertPerson(Integer registry, Integer domain, Date birth_date, Short gender, Short marital_status, String social_security_num, String name, String first_surname, String second_surname)
	throws SQLException {
		 super.insertPerson(registry, domain, birth_date, gender, marital_status, social_security_num, name, first_surname, second_surname);
		personDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> course_scheduleDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_schedulePk(Integer id){
		return course_scheduleDomains.get(id);
	}
	
	/**
	 * Course_schedule
	 * @param course Identificador del Curso
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_schedule( Integer course){
		Integer domain = null;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_schedule
	 * @param id Identificador unico del Horario
	 * @param course Identificador del Curso
	 * @param day_of_week Dia de la semana
	 * @param start_time Hora de comienzo
	 * @param end_time Hora de fin
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_schedule(Integer course, Short day_of_week, Time start_time, Time end_time)
	throws SQLException {
		Integer domain = getDomainForCourse_schedule( course);
		Integer id =  super.insertCourse_schedule( domain != null ? domain : getDefaultDomain(), course, day_of_week, start_time, end_time );
		if ( domain != null ) { 
			course_scheduleDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_schedule
	 * @param id Identificador unico del Horario
	 * @param domain Identificador del Dominio
	 * @param course Identificador del Curso
	 * @param day_of_week Dia de la semana
	 * @param start_time Hora de comienzo
	 * @param end_time Hora de fin
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_schedule(Integer domain, Integer course, Short day_of_week, Time start_time, Time end_time)
	throws SQLException {
		Integer id =  super.insertCourse_schedule(domain, course, day_of_week, start_time, end_time);
		course_scheduleDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> surveyDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSurveyPk(Integer id){
		return surveyDomains.get(id);
	}
	
	/**
	 * Survey
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForSurvey( Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Survey
	 * @param id Identificador unico
	 * @param scope Identificador del Ambito
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey(Integer scope, Boolean active, Timestamp creationDate, String description)
	throws SQLException {
		Integer domain = getDomainForSurvey( scope);
		Integer id =  super.insertSurvey( domain != null ? domain : getDefaultDomain(), scope, active, creationDate, description );
		if ( domain != null ) { 
			surveyDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Survey
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSurvey(Integer domain, Integer scope, Boolean active, Timestamp creationDate, String description)
	throws SQLException {
		Integer id =  super.insertSurvey(domain, scope, active, creationDate, description);
		surveyDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> profile_roleDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProfile_rolePk(Integer id){
		return profile_roleDomains.get(id);
	}
	
	/**
	 * Profile_role
	 * @param application_role Identificador del Role de la Aplicacion
	 * @param profile Identificador del Perfil
	 * @returns domain's ID
	*/
	protected Integer getDomainForProfile_role( Integer application_role , Integer profile){
		Integer domain = null;
			if ( ( domain = getDomainForProfilePk( profile ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Profile_role
	 * @param id Identificador unico
	 * @param profile Identificador del Perfil
	 * @param application_role Identificador del Role de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_role(Integer profile, Integer application_role)
	throws SQLException {
		Integer domain = getDomainForProfile_role( application_role , profile);
		Integer id =  super.insertProfile_role( domain != null ? domain : getDefaultDomain(), profile, application_role );
		if ( domain != null ) { 
			profile_roleDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Profile_role
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param profile Identificador del Perfil
	 * @param application_role Identificador del Role de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_role(Integer domain, Integer profile, Integer application_role)
	throws SQLException {
		Integer id =  super.insertProfile_role(domain, profile, application_role);
		profile_roleDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> job_typeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForJob_typePk(Integer id){
		return job_typeDomains.get(id);
	}
	
	/**
	 * Job_type
	 * @returns domain's ID
	*/
	protected Integer getDomainForJob_type(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Job_type
	 * @param id Identificador unico del Tipo de Trabajo
	 * @param description Descripcion del Tipo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertJob_type(String description)
	throws SQLException {
		Integer domain = getDomainForJob_type();
		Integer id =  super.insertJob_type( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			job_typeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Job_type
	 * @param id Identificador unico del Tipo de Trabajo
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertJob_type(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertJob_type(domain, description);
		job_typeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> noteDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForNotePk(Integer id){
		return noteDomains.get(id);
	}
	
	/**
	 * Note
	 * @param owner Destinatario de la Nota
	 * @returns domain's ID
	*/
	protected Integer getDomainForNote( Integer owner){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( owner ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Note
	 * @param id Identificador unico de la Nota
	 * @param subject Descripcion corta de la Nota
	 * @param date Fecha de la Nota
	 * @param owner Destinatario de la Nota
	 * @param note Texto de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNote(String subject, Timestamp date, Integer owner, String note)
	throws SQLException {
		Integer domain = getDomainForNote( owner);
		Integer id =  super.insertNote( domain != null ? domain : getDefaultDomain(), subject, date, owner, note );
		if ( domain != null ) { 
			noteDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Note
	 * @param id Identificador unico de la Nota
	 * @param domain Identificador del Dominio
	 * @param subject Descripcion corta de la Nota
	 * @param date Fecha de la Nota
	 * @param owner Destinatario de la Nota
	 * @param note Texto de la Nota
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertNote(Integer domain, String subject, Timestamp date, Integer owner, String note)
	throws SQLException {
		Integer id =  super.insertNote(domain, subject, date, owner, note);
		noteDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> system_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForSystem_dataPk(Integer id){
		return system_dataDomains.get(id);
	}
	
	/**
	 * System_data
	 * @returns domain's ID
	*/
	protected Integer getDomainForSystem_data(){
		Integer domain = null;
		return domain;
	}

	/**
	 * System_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param read_only Modificable
	 * @param comments Comentario de ayuda
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_data(String name, String expression, Date start_date, Date end_date, Boolean read_only, String comments)
	throws SQLException {
		Integer domain = getDomainForSystem_data();
		Integer id =  super.insertSystem_data( domain != null ? domain : getDefaultDomain(), name, expression, start_date, end_date, read_only, comments );
		if ( domain != null ) { 
			system_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * System_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param read_only Modificable
	 * @param comments Comentario de ayuda
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertSystem_data(Integer domain, String name, String expression, Date start_date, Date end_date, Boolean read_only, String comments)
	throws SQLException {
		Integer id =  super.insertSystem_data(domain, name, expression, start_date, end_date, read_only, comments);
		system_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_room_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_room_detailPk(Integer id){
		return project_reservation_room_detailDomains.get(id);
	}
	
	/**
	 * Project_reservation_room_detail
	 * @param asset_activity Identificador de la Actividad de la Habitacion
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_room_detail( Integer asset_activity , Integer project_reservation_room){
		Integer domain = null;
			if ( ( domain = getDomainForAsset_activityPk( asset_activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservation_roomPk( project_reservation_room ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_room_detail
	 * @param id Identificador unico
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param asset_activity Identificador de la Actividad de la Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_room_detail(Integer project_reservation_room, Integer asset_activity)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_room_detail( asset_activity , project_reservation_room);
		Integer id =  super.insertProject_reservation_room_detail( domain != null ? domain : getDefaultDomain(), project_reservation_room, asset_activity );
		if ( domain != null ) { 
			project_reservation_room_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_room_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation_room Identificador de la Habitacion de la Reserva
	 * @param asset_activity Identificador de la Actividad de la Habitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_room_detail(Integer domain, Integer project_reservation_room, Integer asset_activity)
	throws SQLException {
		Integer id =  super.insertProject_reservation_room_detail(domain, project_reservation_room, asset_activity);
		project_reservation_room_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> raddinfoDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRaddinfoPk(Integer id){
		return raddinfoDomains.get(id);
	}
	
	/**
	 * Raddinfo
	 * @param registry Identificador de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRaddinfo( Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Raddinfo
	 * @param id Identificador unico
	 * @param registry Identificador de la Persona o Empresa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRaddinfo(Integer registry, String attribute, String value, Date value_date)
	throws SQLException {
		Integer domain = getDomainForRaddinfo( registry);
		Integer id =  super.insertRaddinfo( domain != null ? domain : getDefaultDomain(), registry, attribute, value, value_date );
		if ( domain != null ) { 
			raddinfoDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Raddinfo
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de la Persona o Empresa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRaddinfo(Integer domain, Integer registry, String attribute, String value, Date value_date)
	throws SQLException {
		Integer id =  super.insertRaddinfo(domain, registry, attribute, value, value_date);
		raddinfoDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rtaxDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRtaxPk(Integer id){
		return rtaxDomains.get(id);
	}
	
	/**
	 * Rtax
	 * @param registry Identificador de Persona o Empresa
	 * @param tax Identificador del Impuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForRtax( Integer registry , Integer tax){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaxPk( tax ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rtax
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param tax Identificador del Impuesto
	 * @param percentage Porcentaje de recargo actual
	 * @param surcharge Porcentaje de recargo de equivalencia actual
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRtax(Integer registry, Integer tax, Double percentage, Double surcharge, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForRtax( registry , tax);
		Integer id =  super.insertRtax( domain != null ? domain : getDefaultDomain(), registry, tax, percentage, surcharge, start_date, end_date );
		if ( domain != null ) { 
			rtaxDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rtax
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de Persona o Empresa
	 * @param tax Identificador del Impuesto
	 * @param percentage Porcentaje de recargo actual
	 * @param surcharge Porcentaje de recargo de equivalencia actual
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRtax(Integer domain, Integer registry, Integer tax, Double percentage, Double surcharge, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertRtax(domain, registry, tax, percentage, surcharge, start_date, end_date);
		rtaxDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> cost_profileDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCost_profilePk(Integer id){
		return cost_profileDomains.get(id);
	}
	
	/**
	 * Cost_profile
	 * @returns domain's ID
	*/
	protected Integer getDomainForCost_profile(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Cost_profile
	 * @param id Identificador unico
	 * @param description Descripcion
	 * @param cost Costo por hora
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCost_profile(String description, Double cost)
	throws SQLException {
		Integer domain = getDomainForCost_profile();
		Integer id =  super.insertCost_profile( domain != null ? domain : getDefaultDomain(), description, cost );
		if ( domain != null ) { 
			cost_profileDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Cost_profile
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion
	 * @param cost Costo por hora
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCost_profile(Integer domain, String description, Double cost)
	throws SQLException {
		Integer id =  super.insertCost_profile(domain, description, cost);
		cost_profileDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> offer_termDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForOffer_termPk(Integer id){
		return offer_termDomains.get(id);
	}
	
	/**
	 * Offer_term
	 * @param offer Identificador del Presupuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForOffer_term( Integer offer){
		Integer domain = null;
			if ( ( domain = getDomainForOfferPk( offer ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Offer_term
	 * @param id Identificador unico
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea de la Condicion del Presupuesto
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condicion es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_term(Integer offer, Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		Integer domain = getDomainForOffer_term( offer);
		Integer id =  super.insertOffer_term( domain != null ? domain : getDefaultDomain(), offer, line, name, description, term_general );
		if ( domain != null ) { 
			offer_termDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Offer_term
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea de la Condicion del Presupuesto
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condicion es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertOffer_term(Integer domain, Integer offer, Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		Integer id =  super.insertOffer_term(domain, offer, line, name, description, term_general);
		offer_termDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> catalogue_categoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCatalogue_categoryPk(Integer id){
		return catalogue_categoryDomains.get(id);
	}
	
	/**
	 * Catalogue_category
	 * @param catalogue Identificador del Catalogo
	 * @param category Identificador de la Categoria
	 * @returns domain's ID
	*/
	protected Integer getDomainForCatalogue_category( Integer catalogue , Integer category){
		Integer domain = null;
			if ( ( domain = getDomainForCataloguePk( catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForPcategoryPk( category ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Catalogue_category
	 * @param id Identificador unico
	 * @param catalogue Identificador del Catalogo
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica el descuento
	 * @param discount Descuento de la Categoria en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue_category(Integer catalogue, Integer category, Double quantity, Double discount)
	throws SQLException {
		Integer domain = getDomainForCatalogue_category( catalogue , category);
		Integer id =  super.insertCatalogue_category( domain != null ? domain : getDefaultDomain(), catalogue, category, quantity, discount );
		if ( domain != null ) { 
			catalogue_categoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Catalogue_category
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param catalogue Identificador del Catalogo
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica el descuento
	 * @param discount Descuento de la Categoria en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue_category(Integer domain, Integer catalogue, Integer category, Double quantity, Double discount)
	throws SQLException {
		Integer id =  super.insertCatalogue_category(domain, catalogue, category, quantity, discount);
		catalogue_categoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> alarmDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAlarmPk(Integer id){
		return alarmDomains.get(id);
	}
	
	/**
	 * Alarm
	 * @param user_id Identificador del Usuario asociado a la Alarma
	 * @returns domain's ID
	*/
	protected Integer getDomainForAlarm( Integer user_id){
		Integer domain = null;
			if ( ( domain = getDomainForUserPk( user_id ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Alarm
	 * @param id Identificador unico de la Alarma
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
	public int insertAlarm(String description, Timestamp alarm_date, Short status, Short source, Integer source_id, Integer user_id, Short priority)
	throws SQLException {
		Integer domain = getDomainForAlarm( user_id);
		Integer id =  super.insertAlarm( domain != null ? domain : getDefaultDomain(), description, alarm_date, status, source, source_id, user_id, priority );
		if ( domain != null ) { 
			alarmDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Alarm
	 * @param id Identificador unico de la Alarma
	 * @param domain Identificador del Dominio
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
	public int insertAlarm(Integer domain, String description, Timestamp alarm_date, Short status, Short source, Integer source_id, Integer user_id, Short priority)
	throws SQLException {
		Integer id =  super.insertAlarm(domain, description, alarm_date, status, source, source_id, user_id, priority);
		alarmDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contract_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContract_dataPk(Integer id){
		return contract_dataDomains.get(id);
	}
	
	/**
	 * Contract_data
	 * @param contract Contrato
	 * @returns domain's ID
	*/
	protected Integer getDomainForContract_data( Integer contract){
		Integer domain = null;
			if ( ( domain = getDomainForContractPk( contract ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Contract_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param contract Contrato
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_data(String name, Integer contract, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForContract_data( contract);
		Integer id =  super.insertContract_data( domain != null ? domain : getDefaultDomain(), name, contract, expression, start_date, end_date );
		if ( domain != null ) { 
			contract_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contract_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param contract Contrato
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContract_data(Integer domain, String name, Integer contract, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertContract_data(domain, name, contract, expression, start_date, end_date);
		contract_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> rbankDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForRbankPk(Integer id){
		return rbankDomains.get(id);
	}
	
	/**
	 * Rbank
	 * @param account Identificador de la Cuenta Contable
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @returns domain's ID
	*/
	protected Integer getDomainForRbank( Integer account , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Rbank
	 * @param id Identificador unico de la Cuenta Bancaria de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param sufix Sufijo de Cuenta Bancaria para Remesas
	 * @param alias Alias de la Cuenta Bancaria
	 * @param active Indica si la Cuenta Bancaria esta activa o no
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRbank(Integer registry, String bank_account, String bic, String sufix, String alias, Boolean active, Integer account)
	throws SQLException {
		Integer domain = getDomainForRbank( account , registry);
		Integer id =  super.insertRbank( domain != null ? domain : getDefaultDomain(), registry, bank_account, bic, sufix, alias, active, account );
		if ( domain != null ) { 
			rbankDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Rbank
	 * @param id Identificador unico de la Cuenta Bancaria de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param bank_account IBAN - Numero de Cuenta Bancaria Internacional
	 * @param bic BIC - Codigo Identificador del Banco
	 * @param sufix Sufijo de Cuenta Bancaria para Remesas
	 * @param alias Alias de la Cuenta Bancaria
	 * @param active Indica si la Cuenta Bancaria esta activa o no
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertRbank(Integer domain, Integer registry, String bank_account, String bic, String sufix, String alias, Boolean active, Integer account)
	throws SQLException {
		Integer id =  super.insertRbank(domain, registry, bank_account, bic, sufix, alias, active, account);
		rbankDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> course_subjectDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_subjectPk(Integer id){
		return course_subjectDomains.get(id);
	}
	
	/**
	 * Course_subject
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_subject(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Course_subject
	 * @param id Identificador unico de la Materia
	 * @param description Descripcion de la Materia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_subject(String description)
	throws SQLException {
		Integer domain = getDomainForCourse_subject();
		Integer id =  super.insertCourse_subject( domain != null ? domain : getDefaultDomain(), description );
		if ( domain != null ) { 
			course_subjectDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_subject
	 * @param id Identificador unico de la Materia
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Materia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_subject(Integer domain, String description)
	throws SQLException {
		Integer id =  super.insertCourse_subject(domain, description);
		course_subjectDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> departmentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDepartmentPk(Integer id){
		return departmentDomains.get(id);
	}
	
	/**
	 * Department
	 * @returns domain's ID
	*/
	protected Integer getDomainForDepartment(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Department
	 * @param id Identificador unico
	 * @param name Nombre del Departamento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDepartment(String name)
	throws SQLException {
		Integer domain = getDomainForDepartment();
		Integer id =  super.insertDepartment( domain != null ? domain : getDefaultDomain(), name );
		if ( domain != null ) { 
			departmentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Department
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Departamento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDepartment(Integer domain, String name)
	throws SQLException {
		Integer id =  super.insertDepartment(domain, name);
		departmentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> makeDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForMakePk(Integer id){
		return makeDomains.get(id);
	}
	
	/**
	 * Make
	 * @returns domain's ID
	*/
	protected Integer getDomainForMake(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Make
	 * @param id Identificador unico del Fabricante
	 * @param name Nombre del Fabricante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMake(String name)
	throws SQLException {
		Integer domain = getDomainForMake();
		Integer id =  super.insertMake( domain != null ? domain : getDefaultDomain(), name );
		if ( domain != null ) { 
			makeDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Make
	 * @param id Identificador unico del Fabricante
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Fabricante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertMake(Integer domain, String name)
	throws SQLException {
		Integer id =  super.insertMake(domain, name);
		makeDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreement_paymentDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_paymentPk(Integer id){
		return agreement_paymentDomains.get(id);
	}
	
	/**
	 * Agreement_payment
	 * @param agreement Convenio
	 * @param payment_concept Identificador unico del concepto
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_payment( Integer agreement , Integer payment_concept){
		Integer domain = null;
			if ( ( domain = getDomainForAgreementPk( agreement ) ) != null )
				return domain;
			if ( ( domain = getDomainForPayment_conceptPk( payment_concept ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_payment
	 * @param id Identificador unico
	 * @param agreement Convenio
	 * @param payment_concept Identificador unico del concepto
	 * @param type Tipo de complemento Salarial
	 * @param expression Importe
	 * @param description Descripcion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la percepcion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @param description_decorable 
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_payment(Integer agreement, Integer payment_concept, Short type, String expression, String description, Date start_date, Date end_date, Short month, Short salary_type, Short description_decorable, String irpf_expression, String quote_expression)
	throws SQLException {
		Integer domain = getDomainForAgreement_payment( agreement , payment_concept);
		Integer id =  super.insertAgreement_payment( domain != null ? domain : getDefaultDomain(), agreement, payment_concept, type, expression, description, start_date, end_date, month, salary_type, description_decorable, irpf_expression, quote_expression );
		if ( domain != null ) { 
			agreement_paymentDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param agreement Convenio
	 * @param payment_concept Identificador unico del concepto
	 * @param type Tipo de complemento Salarial
	 * @param expression Importe
	 * @param description Descripcion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la percepcion
	 * @param salary_type Tipo de Nomina/Recibo
	 * @param description_decorable 
	 * @param irpf_expression Importe tributable
	 * @param quote_expression Importe cotizable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_payment(Integer domain, Integer agreement, Integer payment_concept, Short type, String expression, String description, Date start_date, Date end_date, Short month, Short salary_type, Short description_decorable, String irpf_expression, String quote_expression)
	throws SQLException {
		Integer id =  super.insertAgreement_payment(domain, agreement, payment_concept, type, expression, description, start_date, end_date, month, salary_type, description_decorable, irpf_expression, quote_expression);
		agreement_paymentDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> fs_model180_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForFs_model180_detailPk(Integer id){
		return fs_model180_detailDomains.get(id);
	}
	
	/**
	 * Fs_model180_detail
	 * @param fs_model180 Identificador del modelo 180
	 * @returns domain's ID
	*/
	protected Integer getDomainForFs_model180_detail( Integer fs_model180){
		Integer domain = null;
			if ( ( domain = getDomainForFs_model180Pk( fs_model180 ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Fs_model180_detail
	 * @param id Identificador unico
	 * @param fs_model180 Identificador del modelo 180
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param province Provincia
	 * @param inKind Percepcion en especie
	 * @param perception 
	 * @param percentage Porcentaje de retencion
	 * @param retention 
	 * @param accrual_year 
	 * @param location Situacion del inmueble
	 * @param cadasdral_reference Refercia catastral
	 * @param street_type Tipo de via
	 * @param street_name Nombre de la via
	 * @param number_type Tipo de numero de via
	 * @param number Numero de via
	 * @param number_suffix Calificador del numero de via
	 * @param block Direccion. Bloque.
	 * @param hall Direccion. Portal.
	 * @param stair Direccion. Escalera.
	 * @param floor Direccion. Planta.
	 * @param door Direccion. Puerta.
	 * @param complement Direccion. Complemento.
	 * @param city Localidad o poblacion.
	 * @param town Municipio.
	 * @param town_code Codigo de municipio..
	 * @param province_code Codigo de provincia.
	 * @param zip Codigo postal.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model180_detail(Integer fs_model180, String document, String name, String representative_document, Integer province, Boolean inKind, Double perception, Double percentage, Double retention, Integer accrual_year, String location, String cadasdral_reference, String street_type, String street_name, String number_type, String number, String number_suffix, String block, String hall, String stair, String floor, String door, String complement, String city, String town, String town_code, String province_code, String zip)
	throws SQLException {
		Integer domain = getDomainForFs_model180_detail( fs_model180);
		Integer id =  super.insertFs_model180_detail( domain != null ? domain : getDefaultDomain(), fs_model180, document, name, representative_document, province, inKind, perception, percentage, retention, accrual_year, location, cadasdral_reference, street_type, street_name, number_type, number, number_suffix, block, hall, stair, floor, door, complement, city, town, town_code, province_code, zip );
		if ( domain != null ) { 
			fs_model180_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Fs_model180_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_model180 Identificador del modelo 180
	 * @param document NIF Perceptor
	 * @param name Nombre
	 * @param representative_document NIF Representante
	 * @param province Provincia
	 * @param inKind Percepcion en especie
	 * @param perception 
	 * @param percentage Porcentaje de retencion
	 * @param retention 
	 * @param accrual_year 
	 * @param location Situacion del inmueble
	 * @param cadasdral_reference Refercia catastral
	 * @param street_type Tipo de via
	 * @param street_name Nombre de la via
	 * @param number_type Tipo de numero de via
	 * @param number Numero de via
	 * @param number_suffix Calificador del numero de via
	 * @param block Direccion. Bloque.
	 * @param hall Direccion. Portal.
	 * @param stair Direccion. Escalera.
	 * @param floor Direccion. Planta.
	 * @param door Direccion. Puerta.
	 * @param complement Direccion. Complemento.
	 * @param city Localidad o poblacion.
	 * @param town Municipio.
	 * @param town_code Codigo de municipio..
	 * @param province_code Codigo de provincia.
	 * @param zip Codigo postal.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertFs_model180_detail(Integer domain, Integer fs_model180, String document, String name, String representative_document, Integer province, Boolean inKind, Double perception, Double percentage, Double retention, Integer accrual_year, String location, String cadasdral_reference, String street_type, String street_name, String number_type, String number, String number_suffix, String block, String hall, String stair, String floor, String door, String complement, String city, String town, String town_code, String province_code, String zip)
	throws SQLException {
		Integer id =  super.insertFs_model180_detail(domain, fs_model180, document, name, representative_document, province, inKind, perception, percentage, retention, accrual_year, location, cadasdral_reference, street_type, street_name, number_type, number, number_suffix, block, hall, stair, floor, door, complement, city, town, town_code, province_code, zip);
		fs_model180_detailDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> account_entry_bank_statementDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_entry_bank_statementPk(Integer id){
		return account_entry_bank_statementDomains.get(id);
	}
	
	/**
	 * Account_entry_bank_statement
	 * @param account_entry Identificador de Asiento
	 * @param bank_statement Identificador de Extracto bancario
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_entry_bank_statement( Integer account_entry , Integer bank_statement){
		Integer domain = null;
			if ( ( domain = getDomainForAccount_entryPk( account_entry ) ) != null )
				return domain;
			if ( ( domain = getDomainForBank_statementPk( bank_statement ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Account_entry_bank_statement
	 * @param id Identificador unico
	 * @param account_entry Identificador de Asiento
	 * @param bank_statement Identificador de Extracto bancario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_bank_statement(Integer account_entry, Integer bank_statement)
	throws SQLException {
		Integer domain = getDomainForAccount_entry_bank_statement( account_entry , bank_statement);
		Integer id =  super.insertAccount_entry_bank_statement( domain != null ? domain : getDefaultDomain(), account_entry, bank_statement );
		if ( domain != null ) { 
			account_entry_bank_statementDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_entry_bank_statement
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param account_entry Identificador de Asiento
	 * @param bank_statement Identificador de Extracto bancario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_entry_bank_statement(Integer domain, Integer account_entry, Integer bank_statement)
	throws SQLException {
		Integer id =  super.insertAccount_entry_bank_statement(domain, account_entry, bank_statement);
		account_entry_bank_statementDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> domain_applicationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForDomain_applicationPk(Integer id){
		return domain_applicationDomains.get(id);
	}
	
	/**
	 * Domain_application
	 * @param application Identificador de la Aplicacion
	 * @returns domain's ID
	*/
	protected Integer getDomainForDomain_application( Integer application){
		Integer domain = null;
		return domain;
	}

	/**
	 * Domain_application
	 * @param id Identificador unico
	 * @param application Identificador de la Aplicacion
	 * @param active Indica si la Aplicacion del Dominio esta activa o no
	 * @param audit_level Nivel de auditoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDomain_application(Integer application, Boolean active, Short audit_level)
	throws SQLException {
		Integer domain = getDomainForDomain_application( application);
		Integer id =  super.insertDomain_application( domain != null ? domain : getDefaultDomain(), application, active, audit_level );
		if ( domain != null ) { 
			domain_applicationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Domain_application
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param application Identificador de la Aplicacion
	 * @param active Indica si la Aplicacion del Dominio esta activa o no
	 * @param audit_level Nivel de auditoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertDomain_application(Integer domain, Integer application, Boolean active, Short audit_level)
	throws SQLException {
		Integer id =  super.insertDomain_application(domain, application, active, audit_level);
		domain_applicationDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> processDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProcessPk(Integer id){
		return processDomains.get(id);
	}
	
	/**
	 * Process
	 * @returns domain's ID
	*/
	protected Integer getDomainForProcess(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Process
	 * @param id Identificador unico del Proceso
	 * @param description Descripcion del Proceso.
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess(String description, Boolean active)
	throws SQLException {
		Integer domain = getDomainForProcess();
		Integer id =  super.insertProcess( domain != null ? domain : getDefaultDomain(), description, active );
		if ( domain != null ) { 
			processDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Process
	 * @param id Identificador unico del Proceso
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Proceso.
	 * @param active Activo si o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProcess(Integer domain, String description, Boolean active)
	throws SQLException {
		Integer id =  super.insertProcess(domain, description, active);
		processDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> contrata_batchDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForContrata_batchPk(Integer id){
		return contrata_batchDomains.get(id);
	}
	
	/**
	 * Contrata_batch
	 * @returns domain's ID
	*/
	protected Integer getDomainForContrata_batch(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Contrata_batch
	 * @param id Identificador unico
	 * @param date Fecha de la Remesa
	 * @param status Indica el estado de la Remesa
	 * @param type Tipo de remesa a comunicar al SEPE
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContrata_batch(Timestamp date, Short status, Short type, String communication_id, Blob outcome_file, Timestamp outcome_file_date, Blob income_file, Timestamp income_file_date)
	throws SQLException {
		Integer domain = getDomainForContrata_batch();
		Integer id =  super.insertContrata_batch( domain != null ? domain : getDefaultDomain(), date, status, type, communication_id, outcome_file, outcome_file_date, income_file, income_file_date );
		if ( domain != null ) { 
			contrata_batchDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Contrata_batch
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha de la Remesa
	 * @param status Indica el estado de la Remesa
	 * @param type Tipo de remesa a comunicar al SEPE
	 * @param communication_id Identificador resultante de la comunicacion
	 * @param outcome_file Archivo Adjunto en binario
	 * @param outcome_file_date Fecha de creacion
	 * @param income_file Archivo respuesta en binario
	 * @param income_file_date Fecha de respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertContrata_batch(Integer domain, Timestamp date, Short status, Short type, String communication_id, Blob outcome_file, Timestamp outcome_file_date, Blob income_file, Timestamp income_file_date)
	throws SQLException {
		Integer id =  super.insertContrata_batch(domain, date, status, type, communication_id, outcome_file, outcome_file_date, income_file, income_file_date);
		contrata_batchDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> enterprise_activityDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForEnterprise_activityPk(Integer id){
		return enterprise_activityDomains.get(id);
	}
	
	/**
	 * Enterprise_activity
	 * @param cnae Identificador del CNAE
	 * @param cnae2009 Identificador del CNAE 2009
	 * @param enterprise Identificador de la Empresa
	 * @param iae Epigrafe IAE
	 * @param retention_tax Identificador del IRPF por defecto
	 * @param vat_tax Identificador del IVA por defecto
	 * @returns domain's ID
	*/
	protected Integer getDomainForEnterprise_activity( Integer cnae , Integer cnae2009 , Integer enterprise , Integer iae , Integer retention_tax , Integer vat_tax){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaxPk( retention_tax ) ) != null )
				return domain;
			if ( ( domain = getDomainForTaxPk( vat_tax ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Enterprise_activity
	 * @param id Identificador unico
	 * @param description Descripcion de la Actividad de la Empresa
	 * @param enterprise Identificador de la Empresa
	 * @param iae Epigrafe IAE
	 * @param cnae Identificador del CNAE
	 * @param type Tipo de Actividad de la Empresa
	 * @param cnae2009 Identificador del CNAE 2009
	 * @param surcharge Indica si la Actividad tiene de recargo de equivalencia
	 * @param vat_tax Identificador del IVA por defecto
	 * @param retention_tax Identificador del IRPF por defecto
	 * @param vat_regime Regimen de IVA
	 * @param retention_regime Regimen de IRPF
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de fin
	 * @param prorata Porcentaje de prorrata
	 * @param prorata_type Indica el tipo de prorrata
	 * @param principal Indica si es la Actividad principal
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_activity(String description, Integer enterprise, Integer iae, Integer cnae, Short type, Integer cnae2009, Boolean surcharge, Integer vat_tax, Integer retention_tax, Short vat_regime, Short retention_regime, Date start_date, Date end_date, Double prorata, Boolean prorata_type, Boolean principal)
	throws SQLException {
		Integer domain = getDomainForEnterprise_activity( cnae , cnae2009 , enterprise , iae , retention_tax , vat_tax);
		Integer id =  super.insertEnterprise_activity( domain != null ? domain : getDefaultDomain(), description, enterprise, iae, cnae, type, cnae2009, surcharge, vat_tax, retention_tax, vat_regime, retention_regime, start_date, end_date, prorata, prorata_type, principal );
		if ( domain != null ) { 
			enterprise_activityDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Enterprise_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Actividad de la Empresa
	 * @param enterprise Identificador de la Empresa
	 * @param iae Epigrafe IAE
	 * @param cnae Identificador del CNAE
	 * @param type Tipo de Actividad de la Empresa
	 * @param cnae2009 Identificador del CNAE 2009
	 * @param surcharge Indica si la Actividad tiene de recargo de equivalencia
	 * @param vat_tax Identificador del IVA por defecto
	 * @param retention_tax Identificador del IRPF por defecto
	 * @param vat_regime Regimen de IVA
	 * @param retention_regime Regimen de IRPF
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de fin
	 * @param prorata Porcentaje de prorrata
	 * @param prorata_type Indica el tipo de prorrata
	 * @param principal Indica si es la Actividad principal
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertEnterprise_activity(Integer domain, String description, Integer enterprise, Integer iae, Integer cnae, Short type, Integer cnae2009, Boolean surcharge, Integer vat_tax, Integer retention_tax, Short vat_regime, Short retention_regime, Date start_date, Date end_date, Double prorata, Boolean prorata_type, Boolean principal)
	throws SQLException {
		Integer id =  super.insertEnterprise_activity(domain, description, enterprise, iae, cnae, type, cnae2009, surcharge, vat_tax, retention_tax, vat_regime, retention_regime, start_date, end_date, prorata, prorata_type, principal);
		enterprise_activityDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> agreement_dataDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAgreement_dataPk(Integer id){
		return agreement_dataDomains.get(id);
	}
	
	/**
	 * Agreement_data
	 * @param agreement Convenio
	 * @returns domain's ID
	*/
	protected Integer getDomainForAgreement_data( Integer agreement){
		Integer domain = null;
			if ( ( domain = getDomainForAgreementPk( agreement ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Agreement_data
	 * @param id Identificador unico
	 * @param name Nombre
	 * @param agreement Convenio
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_data(String name, Integer agreement, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer domain = getDomainForAgreement_data( agreement);
		Integer id =  super.insertAgreement_data( domain != null ? domain : getDefaultDomain(), name, agreement, expression, start_date, end_date );
		if ( domain != null ) { 
			agreement_dataDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Agreement_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param agreement Convenio
	 * @param expression Expresion
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAgreement_data(Integer domain, String name, Integer agreement, String expression, Date start_date, Date end_date)
	throws SQLException {
		Integer id =  super.insertAgreement_data(domain, name, agreement, expression, start_date, end_date);
		agreement_dataDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoice_tax_accountDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoice_tax_accountPk(Integer id){
		return invoice_tax_accountDomains.get(id);
	}
	
	/**
	 * Invoice_tax_account
	 * @param account Identificador de la Cuenta Contable
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice_tax_account( Integer account , Integer invoice_tax){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoice_taxPk( invoice_tax ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice_tax_account
	 * @param id Identificador unico
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_tax_account(Integer invoice_tax, Integer account)
	throws SQLException {
		Integer domain = getDomainForInvoice_tax_account( account , invoice_tax);
		Integer id =  super.insertInvoice_tax_account( domain != null ? domain : getDefaultDomain(), invoice_tax, account );
		if ( domain != null ) { 
			invoice_tax_accountDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice_tax_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice_tax_account(Integer domain, Integer invoice_tax, Integer account)
	throws SQLException {
		Integer id =  super.insertInvoice_tax_account(domain, invoice_tax, account);
		invoice_tax_accountDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> profile_module_deniedDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProfile_module_deniedPk(Integer id){
		return profile_module_deniedDomains.get(id);
	}
	
	/**
	 * Profile_module_denied
	 * @param profile Identificador del Perfil
	 * @returns domain's ID
	*/
	protected Integer getDomainForProfile_module_denied( Integer profile){
		Integer domain = null;
			if ( ( domain = getDomainForProfilePk( profile ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Profile_module_denied
	 * @param id Identificador unico
	 * @param profile Identificador del Perfil
	 * @param module Modulo Inhabilitado para el Perfil
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_module_denied(Integer profile, Short module)
	throws SQLException {
		Integer domain = getDomainForProfile_module_denied( profile);
		Integer id =  super.insertProfile_module_denied( domain != null ? domain : getDefaultDomain(), profile, module );
		if ( domain != null ) { 
			profile_module_deniedDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Profile_module_denied
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param profile Identificador del Perfil
	 * @param module Modulo Inhabilitado para el Perfil
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProfile_module_denied(Integer domain, Integer profile, Short module)
	throws SQLException {
		Integer id =  super.insertProfile_module_denied(domain, profile, module);
		profile_module_deniedDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> carrierDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCarrierPk(Integer registry){
		return carrierDomains.get(registry);
	}
	
	/**
	 * Carrier
	 * @param registry Registro de la Agencia de Transporte
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForCarrier( Integer registry , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Carrier
	 * @param registry Registro de la Agencia de Transporte
	 * @param scope Identificador del Ambito
	 * @param status Estado de la Agencia de Transporte
	 * @throws SQLException
	*/
	public void insertCarrier(Integer registry, Integer scope, Short status)
	throws SQLException {
		Integer domain = getDomainForCarrier( registry , scope);
		 super.insertCarrier( registry, domain != null ? domain : getDefaultDomain(), scope, status );
		if ( domain != null ) { 
			carrierDomains.put(registry, domain);
		}
	}

	/**
	 * Carrier
	 * @param registry Registro de la Agencia de Transporte
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param status Estado de la Agencia de Transporte
	 * @throws SQLException
	*/
	public void insertCarrier(Integer registry, Integer domain, Integer scope, Short status)
	throws SQLException {
		 super.insertCarrier(registry, domain, scope, status);
		carrierDomains.put(registry, domain );
			}

	
	private Map<Integer,Integer> course_observationDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCourse_observationPk(Integer id){
		return course_observationDomains.get(id);
	}
	
	/**
	 * Course_observation
	 * @param course Identificador de Curso
	 * @returns domain's ID
	*/
	protected Integer getDomainForCourse_observation( Integer course){
		Integer domain = null;
			if ( ( domain = getDomainForCoursePk( course ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Course_observation
	 * @param id Identificador unico
	 * @param course Identificador de Curso
	 * @param observation Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_observation(Integer course, String observation)
	throws SQLException {
		Integer domain = getDomainForCourse_observation( course);
		Integer id =  super.insertCourse_observation( domain != null ? domain : getDefaultDomain(), course, observation );
		if ( domain != null ) { 
			course_observationDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Course_observation
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param course Identificador de Curso
	 * @param observation Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCourse_observation(Integer domain, Integer course, String observation)
	throws SQLException {
		Integer id =  super.insertCourse_observation(domain, course, observation);
		course_observationDomains.put(id, domain );
		return id;
	}


	
	private Map<Integer,Integer> account_periodDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccount_periodPk(Integer id){
		return account_periodDomains.get(id);
	}
	
	/**
	 * Account_period
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount_period(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Account_period
	 * @param id Identificador unico
	 * @param name Nombre del periodo
	 * @param initiation_date Fecha de inicio del Ejercicio
	 * @param deadline Fecha final del Ejercicio
	 * @param status Estado del Ejercicio
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_period(String name, Date initiation_date, Date deadline, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForAccount_period();
		Integer id =  super.insertAccount_period( domain != null ? domain : getDefaultDomain(), name, initiation_date, deadline, status, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			account_periodDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account_period
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del periodo
	 * @param initiation_date Fecha de inicio del Ejercicio
	 * @param deadline Fecha final del Ejercicio
	 * @param status Estado del Ejercicio
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount_period(Integer domain, String name, Date initiation_date, Date deadline, Short status, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertAccount_period(domain, name, initiation_date, deadline, status, creation_user, creation_date, modification_user, modification_date);
		account_periodDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> catalogue_itemDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCatalogue_itemPk(Integer id){
		return catalogue_itemDomains.get(id);
	}
	
	/**
	 * Catalogue_item
	 * @param catalogue Identificador del Catalogo
	 * @param item Identificador del Articulo
	 * @param product Identificador del Producto
	 * @returns domain's ID
	*/
	protected Integer getDomainForCatalogue_item( Integer catalogue , Integer item , Integer product){
		Integer domain = null;
			if ( ( domain = getDomainForCataloguePk( catalogue ) ) != null )
				return domain;
			if ( ( domain = getDomainForItemPk( item ) ) != null )
				return domain;
			if ( ( domain = getDomainForProductPk( product ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Catalogue_item
	 * @param id Identificador unico
	 * @param catalogue Identificador del Catalogo
	 * @param product Identificador del Producto
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica el precio o descuento
	 * @param price Precio del Articulo en el Catalogo
	 * @param discount Descuento del Articulo en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue_item(Integer catalogue, Integer product, Integer item, Double quantity, Double price, Double discount)
	throws SQLException {
		Integer domain = getDomainForCatalogue_item( catalogue , item , product);
		Integer id =  super.insertCatalogue_item( domain != null ? domain : getDefaultDomain(), catalogue, product, item, quantity, price, discount );
		if ( domain != null ) { 
			catalogue_itemDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Catalogue_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param catalogue Identificador del Catalogo
	 * @param product Identificador del Producto
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica el precio o descuento
	 * @param price Precio del Articulo en el Catalogo
	 * @param discount Descuento del Articulo en el Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCatalogue_item(Integer domain, Integer catalogue, Integer product, Integer item, Double quantity, Double price, Double discount)
	throws SQLException {
		Integer id =  super.insertCatalogue_item(domain, catalogue, product, item, quantity, price, discount);
		catalogue_itemDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> project_reservation_guestDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForProject_reservation_guestPk(Integer id){
		return project_reservation_guestDomains.get(id);
	}
	
	/**
	 * Project_reservation_guest
	 * @param person Identificador de la Persona
	 * @param project_reservation Identificador de la Reserva
	 * @returns domain's ID
	*/
	protected Integer getDomainForProject_reservation_guest( Integer person , Integer project_reservation){
		Integer domain = null;
			if ( ( domain = getDomainForPersonPk( person ) ) != null )
				return domain;
			if ( ( domain = getDomainForProject_reservationPk( project_reservation ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Project_reservation_guest
	 * @param id Identificador unico
	 * @param project_reservation Identificador de la Reserva
	 * @param guest_index Numero de Huesped
	 * @param name Nombre
	 * @param surname Apellido 1
	 * @param surname2 Apellido 2
	 * @param treatment Tratamiento
	 * @param document Numero de documento de identificacion
	 * @param document_type Tipo de documento
	 * @param document_country Pais del documento
	 * @param document_exp_date Fecha de expiracion del documento
	 * @param birth_date Fecha de nacimiento
	 * @param email Email
	 * @param phone Telefono
	 * @param address Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo postal
	 * @param city Ciudad
	 * @param province Provincia
	 * @param country Pais
	 * @param barcode Codigo de pulsera
	 * @param person Identificador de la Persona
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_guest(Integer project_reservation, Short guest_index, String name, String surname, String surname2, String treatment, String document, Short document_type, String document_country, Date document_exp_date, Date birth_date, String email, String phone, String address, String number, String address2, String zip, String city, String province, String country, String barcode, Integer person, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForProject_reservation_guest( person , project_reservation);
		Integer id =  super.insertProject_reservation_guest( domain != null ? domain : getDefaultDomain(), project_reservation, guest_index, name, surname, surname2, treatment, document, document_type, document_country, document_exp_date, birth_date, email, phone, address, number, address2, zip, city, province, country, barcode, person, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			project_reservation_guestDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Project_reservation_guest
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param guest_index Numero de Huesped
	 * @param name Nombre
	 * @param surname Apellido 1
	 * @param surname2 Apellido 2
	 * @param treatment Tratamiento
	 * @param document Numero de documento de identificacion
	 * @param document_type Tipo de documento
	 * @param document_country Pais del documento
	 * @param document_exp_date Fecha de expiracion del documento
	 * @param birth_date Fecha de nacimiento
	 * @param email Email
	 * @param phone Telefono
	 * @param address Direccion
	 * @param number Numero
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo postal
	 * @param city Ciudad
	 * @param province Provincia
	 * @param country Pais
	 * @param barcode Codigo de pulsera
	 * @param person Identificador de la Persona
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertProject_reservation_guest(Integer domain, Integer project_reservation, Short guest_index, String name, String surname, String surname2, String treatment, String document, Short document_type, String document_country, Date document_exp_date, Date birth_date, String email, String phone, String address, String number, String address2, String zip, String city, String province, String country, String barcode, Integer person, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertProject_reservation_guest(domain, project_reservation, guest_index, name, surname, surname2, treatment, document, document_type, document_country, document_exp_date, birth_date, email, phone, address, number, address2, zip, city, province, country, barcode, person, creation_user, creation_date, modification_user, modification_date);
		project_reservation_guestDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> tax_detailDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForTax_detailPk(Integer id){
		return tax_detailDomains.get(id);
	}
	
	/**
	 * Tax_detail
	 * @param tax Identificador del Impuesto
	 * @returns domain's ID
	*/
	protected Integer getDomainForTax_detail( Integer tax){
		Integer domain = null;
			if ( ( domain = getDomainForTaxPk( tax ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Tax_detail
	 * @param id Identificador unico del Historico de Impuestos
	 * @param tax Identificador del Impuesto
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @param value Porcentaje de recargo
	 * @param surcharge Porcentaje de recargo de equivalencia
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTax_detail(Integer tax, Date start_date, Date end_date, Double value, Double surcharge, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForTax_detail( tax);
		Integer id =  super.insertTax_detail( domain != null ? domain : getDefaultDomain(), tax, start_date, end_date, value, surcharge, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			tax_detailDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Tax_detail
	 * @param id Identificador unico del Historico de Impuestos
	 * @param domain Identificador del Dominio
	 * @param tax Identificador del Impuesto
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @param value Porcentaje de recargo
	 * @param surcharge Porcentaje de recargo de equivalencia
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertTax_detail(Integer domain, Integer tax, Date start_date, Date end_date, Double value, Double surcharge, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertTax_detail(domain, tax, start_date, end_date, value, surcharge, creation_user, creation_date, modification_user, modification_date);
		tax_detailDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> asset_featureDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAsset_featurePk(Integer id){
		return asset_featureDomains.get(id);
	}
	
	/**
	 * Asset_feature
	 * @param asset Identificador del Activo
	 * @param feature Identificador de la Caracteristica
	 * @returns domain's ID
	*/
	protected Integer getDomainForAsset_feature( Integer asset , Integer feature){
		Integer domain = null;
			if ( ( domain = getDomainForAssetPk( asset ) ) != null )
				return domain;
			if ( ( domain = getDomainForFeaturePk( feature ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Asset_feature
	 * @param id Identificador unico
	 * @param asset Identificador del Activo
	 * @param feature Identificador de la Caracteristica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAsset_feature(Integer asset, Integer feature)
	throws SQLException {
		Integer domain = getDomainForAsset_feature( asset , feature);
		Integer id =  super.insertAsset_feature( domain != null ? domain : getDefaultDomain(), asset, feature );
		if ( domain != null ) { 
			asset_featureDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Asset_feature
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param asset Identificador del Activo
	 * @param feature Identificador de la Caracteristica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAsset_feature(Integer domain, Integer asset, Integer feature)
	throws SQLException {
		Integer id =  super.insertAsset_feature(domain, asset, feature);
		asset_featureDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> invoiceDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForInvoicePk(Integer id){
		return invoiceDomains.get(id);
	}
	
	/**
	 * Invoice
	 * @param activity Identificador de la Actividad
	 * @param invest_asset Identificador del Bien afecto
	 * @param rectification_invoice Relacion de rectificacion de Facturas
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param project Identificador del Proyecto
	 * @param raddress Identificador de la Direccion de envio de la Factura
	 * @param registry Identificador del Cliente o Proveedor
	 * @param scope Ambito de la Factura
	 * @param seller Identificador de Agente Comercial
	 * @returns domain's ID
	*/
	protected Integer getDomainForInvoice( Integer activity , Integer invest_asset , Integer rectification_invoice , Integer pos_shift , Integer project , Integer raddress , Integer registry , Integer scope , Integer seller){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprise_activityPk( activity ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvest_assetPk( invest_asset ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicePk( rectification_invoice ) ) != null )
				return domain;
			if ( ( domain = getDomainForPos_shiftPk( pos_shift ) ) != null )
				return domain;
			if ( ( domain = getDomainForProjectPk( project ) ) != null )
				return domain;
			if ( ( domain = getDomainForRaddressPk( raddress ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForSellerPk( seller ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Invoice
	 * @param id Identificador unico de la Factura
	 * @param activity Identificador de la Actividad
	 * @param invest_asset Identificador del Bien afecto
	 * @param project Identificador del Proyecto
	 * @param series Serie de la Factura
	 * @param number Numero de la Factura
	 * @param reference_code Codigo de referencia de la Factura
	 * @param registry Identificador del Cliente o Proveedor
	 * @param rdocument Numero de Documento del Cliente o Proveedor
	 * @param rdocument_type Tipo de documento (NIF, CIF...)
	 * @param rdocument_country Pais del documento
	 * @param rname Nombre completo del Cliente o Proveedor
	 * @param raddress Identificador de la Direccion de envio de la Factura
	 * @param issue_date Fecha de emision de la Factura
	 * @param tax_date Fecha de Impuestos de la Factura
	 * @param security_level Nivel de seguridad de la Factura
	 * @param status Estado de la Factura
	 * @param type Tipo de Factura (Compra o Venta)
	 * @param surcharge Indica si la Factura tiene recargo de equivalencia
	 * @param withholding Indica si la Factura aplica retencion de impuestos
	 * @param withholding_farmer Indica si la Factura aplica retencion de Regimen Especial de Agricultura y Pesca
	 * @param vat_accrual_payment Indica si la Factura se incluye en el Regimen Especial de Criterio de Caja
	 * @param comments Comentarios de la Factura
	 * @param remarks Observaciones de la Factura
	 * @param investment Indica si la Factura es una inversion
	 * @param transaction Tipo de transaccion
	 * @param signed Indica si la Factura esta firmada electronicamente
	 * @param scope Ambito de la Factura
	 * @param service Indica si es una Factura de servicios
	 * @param rectification_type Tipo de rectificacion (Normal o Especial)
	 * @param rectification_invoice Relacion de rectificacion de Facturas
	 * @param advance Indica si la Factura es un anticipo
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param seller Identificador de Agente Comercial
	 * @param taxable_base Base Imponible de la Factura
	 * @param vat_quota Cuota de IVA de la Factura
	 * @param retention_quota Cuota de IRPF de la Factura
	 * @param total Total Factura
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice(Integer activity, Integer invest_asset, Integer project, String series, Integer number, String reference_code, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Integer raddress, Date issue_date, Date tax_date, Short security_level, Short status, Short type, Boolean surcharge, Boolean withholding, Boolean withholding_farmer, Boolean vat_accrual_payment, String comments, String remarks, Boolean investment, Short transaction, Boolean signed, Integer scope, Boolean service, Short rectification_type, Integer rectification_invoice, Boolean advance, Integer pos_shift, Integer seller, Double taxable_base, Double vat_quota, Double retention_quota, Double total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForInvoice( activity , invest_asset , rectification_invoice , pos_shift , project , raddress , registry , scope , seller);
		Integer id =  super.insertInvoice( domain != null ? domain : getDefaultDomain(), activity, invest_asset, project, series, number, reference_code, registry, rdocument, rdocument_type, rdocument_country, rname, raddress, issue_date, tax_date, security_level, status, type, surcharge, withholding, withholding_farmer, vat_accrual_payment, comments, remarks, investment, transaction, signed, scope, service, rectification_type, rectification_invoice, advance, pos_shift, seller, taxable_base, vat_quota, retention_quota, total, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			invoiceDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Invoice
	 * @param id Identificador unico de la Factura
	 * @param domain Identificador del Dominio
	 * @param activity Identificador de la Actividad
	 * @param invest_asset Identificador del Bien afecto
	 * @param project Identificador del Proyecto
	 * @param series Serie de la Factura
	 * @param number Numero de la Factura
	 * @param reference_code Codigo de referencia de la Factura
	 * @param registry Identificador del Cliente o Proveedor
	 * @param rdocument Numero de Documento del Cliente o Proveedor
	 * @param rdocument_type Tipo de documento (NIF, CIF...)
	 * @param rdocument_country Pais del documento
	 * @param rname Nombre completo del Cliente o Proveedor
	 * @param raddress Identificador de la Direccion de envio de la Factura
	 * @param issue_date Fecha de emision de la Factura
	 * @param tax_date Fecha de Impuestos de la Factura
	 * @param security_level Nivel de seguridad de la Factura
	 * @param status Estado de la Factura
	 * @param type Tipo de Factura (Compra o Venta)
	 * @param surcharge Indica si la Factura tiene recargo de equivalencia
	 * @param withholding Indica si la Factura aplica retencion de impuestos
	 * @param withholding_farmer Indica si la Factura aplica retencion de Regimen Especial de Agricultura y Pesca
	 * @param vat_accrual_payment Indica si la Factura se incluye en el Regimen Especial de Criterio de Caja
	 * @param comments Comentarios de la Factura
	 * @param remarks Observaciones de la Factura
	 * @param investment Indica si la Factura es una inversion
	 * @param transaction Tipo de transaccion
	 * @param signed Indica si la Factura esta firmada electronicamente
	 * @param scope Ambito de la Factura
	 * @param service Indica si es una Factura de servicios
	 * @param rectification_type Tipo de rectificacion (Normal o Especial)
	 * @param rectification_invoice Relacion de rectificacion de Facturas
	 * @param advance Indica si la Factura es un anticipo
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param seller Identificador de Agente Comercial
	 * @param taxable_base Base Imponible de la Factura
	 * @param vat_quota Cuota de IVA de la Factura
	 * @param retention_quota Cuota de IRPF de la Factura
	 * @param total Total Factura
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertInvoice(Integer domain, Integer activity, Integer invest_asset, Integer project, String series, Integer number, String reference_code, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Integer raddress, Date issue_date, Date tax_date, Short security_level, Short status, Short type, Boolean surcharge, Boolean withholding, Boolean withholding_farmer, Boolean vat_accrual_payment, String comments, String remarks, Boolean investment, Short transaction, Boolean signed, Integer scope, Boolean service, Short rectification_type, Integer rectification_invoice, Boolean advance, Integer pos_shift, Integer seller, Double taxable_base, Double vat_quota, Double retention_quota, Double total, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer id =  super.insertInvoice(domain, activity, invest_asset, project, series, number, reference_code, registry, rdocument, rdocument_type, rdocument_country, rname, raddress, issue_date, tax_date, security_level, status, type, surcharge, withholding, withholding_farmer, vat_accrual_payment, comments, remarks, investment, transaction, signed, scope, service, rectification_type, rectification_invoice, advance, pos_shift, seller, taxable_base, vat_quota, retention_quota, total, creation_user, creation_date, modification_user, modification_date);
		invoiceDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> categoryDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCategoryPk(Integer id){
		return categoryDomains.get(id);
	}
	
	/**
	 * Category
	 * @param rattach Identificador del Archivo Adjunto
	 * @param scope Identificador del Ambito
	 * @returns domain's ID
	*/
	protected Integer getDomainForCategory( Integer rattach , Integer scope){
		Integer domain = null;
			if ( ( domain = getDomainForRattachPk( rattach ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Category
	 * @param id Identificador unico de la Categoria
	 * @param name Nombre de la Categoria
	 * @param type Tipo de la Categoria
	 * @param scope Identificador del Ambito
	 * @param description Descripcion de la Categoria
	 * @param url Url de la Categoria
	 * @param rattach Identificador del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCategory(String name, Short type, Integer scope, String description, String url, Integer rattach)
	throws SQLException {
		Integer domain = getDomainForCategory( rattach , scope);
		Integer id =  super.insertCategory( domain != null ? domain : getDefaultDomain(), name, type, scope, description, url, rattach );
		if ( domain != null ) { 
			categoryDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Category
	 * @param id Identificador unico de la Categoria
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Categoria
	 * @param type Tipo de la Categoria
	 * @param scope Identificador del Ambito
	 * @param description Descripcion de la Categoria
	 * @param url Url de la Categoria
	 * @param rattach Identificador del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertCategory(Integer domain, String name, Short type, Integer scope, String description, String url, Integer rattach)
	throws SQLException {
		Integer id =  super.insertCategory(domain, name, type, scope, description, url, rattach);
		categoryDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> userDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForUserPk(Integer id){
		return userDomains.get(id);
	}
	
	/**
	 * User
	 * @param enterprise Identificador de la Empresa
	 * @param registry Identificador del Registry
	 * @returns domain's ID
	*/
	protected Integer getDomainForUser( Integer enterprise , Integer registry){
		Integer domain = null;
			if ( ( domain = getDomainForEnterprisePk( enterprise ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * User
	 * @param id Identificador unico
	 * @param name Nombre del Usuario
	 * @param login Login del Usuario
	 * @param enterprise Identificador de la Empresa
	 * @param registry Identificador del Registry
	 * @param active Indica si el Usuario esta activo o no
	 * @param allowConcurrent Indica si el Usuario admite Sesiones concurrentes
	 * @param password ContraseÒa del Usuario
	 * @param passwordExpiration Fecha de Expiracion de la Contraseùa
	 * @param toolbar Tipo de Barra de Herramientas del Usuario
	 * @param locale Locale del Usuario
	 * @param pageLimit Limite de filas en pantalla del Usuario
	 * @param linesPageLimit Limite de filas en pantalla en lineas del Usuario
	 * @param initAction Nombre la acicùn de inicio del Usuario
	 * @param lastAccess Fecha del ultimo acceso del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser(String name, String login, Integer enterprise, Integer registry, Boolean active, Boolean allowConcurrent, String password, Date passwordExpiration, Short toolbar, String locale, Integer pageLimit, Integer linesPageLimit, String initAction, Timestamp lastAccess)
	throws SQLException {
		Integer domain = getDomainForUser( enterprise , registry);
		Integer id =  super.insertUser( domain != null ? domain : getDefaultDomain(), name, login, enterprise, registry, active, allowConcurrent, password, passwordExpiration, toolbar, locale, pageLimit, linesPageLimit, initAction, lastAccess );
		if ( domain != null ) { 
			userDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * User
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Usuario
	 * @param login Login del Usuario
	 * @param enterprise Identificador de la Empresa
	 * @param registry Identificador del Registry
	 * @param active Indica si el Usuario esta activo o no
	 * @param allowConcurrent Indica si el Usuario admite Sesiones concurrentes
	 * @param password ContraseÒa del Usuario
	 * @param passwordExpiration Fecha de Expiracion de la Contraseùa
	 * @param toolbar Tipo de Barra de Herramientas del Usuario
	 * @param locale Locale del Usuario
	 * @param pageLimit Limite de filas en pantalla del Usuario
	 * @param linesPageLimit Limite de filas en pantalla en lineas del Usuario
	 * @param initAction Nombre la acicùn de inicio del Usuario
	 * @param lastAccess Fecha del ultimo acceso del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertUser(Integer domain, String name, String login, Integer enterprise, Integer registry, Boolean active, Boolean allowConcurrent, String password, Date passwordExpiration, Short toolbar, String locale, Integer pageLimit, Integer linesPageLimit, String initAction, Timestamp lastAccess)
	throws SQLException {
		Integer id =  super.insertUser(domain, name, login, enterprise, registry, active, allowConcurrent, password, passwordExpiration, toolbar, locale, pageLimit, linesPageLimit, initAction, lastAccess);
		userDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> accountDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForAccountPk(Integer id){
		return accountDomains.get(id);
	}
	
	/**
	 * Account
	 * @returns domain's ID
	*/
	protected Integer getDomainForAccount(){
		Integer domain = null;
		return domain;
	}

	/**
	 * Account
	 * @param id Identificador unico
	 * @param code Codigo Cuenta Contable
	 * @param description Descripcion de la Cuenta
	 * @param alias Alias de la Cuenta
	 * @param entryEnabled Indica si la Cuenta permite o no Apuntes
	 * @param level Nivel de la Cuenta
	 * @param active Indica si la Cuenta esta activo o no
	 * @param cost_center Centro de Costo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount(String code, String description, String alias, Short entryEnabled, Short level, Boolean active, String cost_center)
	throws SQLException {
		Integer domain = getDomainForAccount();
		Integer id =  super.insertAccount( domain != null ? domain : getDefaultDomain(), code, description, alias, entryEnabled, level, active, cost_center );
		if ( domain != null ) { 
			accountDomains.put(id, domain);
		}
		return id;
	}

	/**
	 * Account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo Cuenta Contable
	 * @param description Descripcion de la Cuenta
	 * @param alias Alias de la Cuenta
	 * @param entryEnabled Indica si la Cuenta permite o no Apuntes
	 * @param level Nivel de la Cuenta
	 * @param active Indica si la Cuenta esta activo o no
	 * @param cost_center Centro de Costo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	public int insertAccount(Integer domain, String code, String description, String alias, Short entryEnabled, Short level, Boolean active, String cost_center)
	throws SQLException {
		Integer id =  super.insertAccount(domain, code, description, alias, entryEnabled, level, active, cost_center);
		accountDomains.put(id, domain );
		return id;
	}

	
	private Map<Integer,Integer> customerDomains = new HashMap<Integer,Integer>();

	protected Integer getDomainForCustomerPk(Integer registry){
		return customerDomains.get(registry);
	}
	
	/**
	 * Customer
	 * @param account Identificador de la Cuenta Contable
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param registry Registro del Cliente
	 * @param scope Identificador del Ambito
	 * @param tariff Tarifa asociada al Cliente
	 * @returns domain's ID
	*/
	protected Integer getDomainForCustomer( Integer account , Integer invoicing_group , Integer registry , Integer scope , Integer tariff){
		Integer domain = null;
			if ( ( domain = getDomainForAccountPk( account ) ) != null )
				return domain;
			if ( ( domain = getDomainForInvoicing_groupPk( invoicing_group ) ) != null )
				return domain;
			if ( ( domain = getDomainForRegistryPk( registry ) ) != null )
				return domain;
			if ( ( domain = getDomainForScopePk( scope ) ) != null )
				return domain;
			if ( ( domain = getDomainForTariffPk( tariff ) ) != null )
				return domain;
		return domain;
	}

	/**
	 * Customer
	 * @param registry Registro del Cliente
	 * @param tariff Tarifa asociada al Cliente
	 * @param surcharge Indica si el Cliente tiene recargo de equivalencia
	 * @param withholding Indica si el Cliente aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Cliente
	 * @param status Estado del Cliente
	 * @param scope Identificador del Ambito
	 * @param e_invoice Indica si el Cliente desea recibir Facturas electronicas
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param project_grouped Indica si el Cliente desea agrupar Proyectos en una sola Factura
	 * @param delivery_grouped Indica si el Cliente desea agrupar Albaranes en una sola Factura
	 * @param delivery_valuated Indica si el Cliente desea imprimir el Albaran valorado
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertCustomer(Integer registry, Integer tariff, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer scope, Boolean e_invoice, Integer invoicing_group, Boolean project_grouped, Boolean delivery_grouped, Boolean delivery_valuated, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		Integer domain = getDomainForCustomer( account , invoicing_group , registry , scope , tariff);
		 super.insertCustomer( registry, domain != null ? domain : getDefaultDomain(), tariff, surcharge, withholding, transaction, status, scope, e_invoice, invoicing_group, project_grouped, delivery_grouped, delivery_valuated, account, creation_user, creation_date, modification_user, modification_date );
		if ( domain != null ) { 
			customerDomains.put(registry, domain);
		}
	}

	/**
	 * Customer
	 * @param registry Registro del Cliente
	 * @param domain Identificador del Dominio
	 * @param tariff Tarifa asociada al Cliente
	 * @param surcharge Indica si el Cliente tiene recargo de equivalencia
	 * @param withholding Indica si el Cliente aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Cliente
	 * @param status Estado del Cliente
	 * @param scope Identificador del Ambito
	 * @param e_invoice Indica si el Cliente desea recibir Facturas electronicas
	 * @param invoicing_group Identificador de Grupo de Facturacion
	 * @param project_grouped Indica si el Cliente desea agrupar Proyectos en una sola Factura
	 * @param delivery_grouped Indica si el Cliente desea agrupar Albaranes en una sola Factura
	 * @param delivery_valuated Indica si el Cliente desea imprimir el Albaran valorado
	 * @param account Identificador de la Cuenta Contable
	 * @param creation_user Usuario de creacion
	 * @param creation_date Fecha de creacion
	 * @param modification_user Usuario de modificacion
	 * @param modification_date Fecha de modificacion
	 * @throws SQLException
	*/
	public void insertCustomer(Integer registry, Integer domain, Integer tariff, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer scope, Boolean e_invoice, Integer invoicing_group, Boolean project_grouped, Boolean delivery_grouped, Boolean delivery_valuated, Integer account, String creation_user, Timestamp creation_date, String modification_user, Timestamp modification_date)
	throws SQLException {
		 super.insertCustomer(registry, domain, tariff, surcharge, withholding, transaction, status, scope, e_invoice, invoicing_group, project_grouped, delivery_grouped, delivery_valuated, account, creation_user, creation_date, modification_user, modification_date);
		customerDomains.put(registry, domain );
			}

	
}		
	
	
