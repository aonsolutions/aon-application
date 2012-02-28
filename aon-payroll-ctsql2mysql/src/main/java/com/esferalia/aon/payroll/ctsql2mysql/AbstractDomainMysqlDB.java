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
import java.util.LinkedList;

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

public class AbstractDomainMysqlDB extends AbstractMysqlDB  {

	public AbstractDomainMysqlDB( Connection mysqlConnection) 
	throws SQLException {
		super(mysqlConnection);
	}
	


	/**
	 * Pcategory
	 * @param id Identificador unico de la Categoria
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Categoria
	 * @param detail_pattern Patron para los detalles de Articulos
	 * @param pcategory_group Identificador del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory(String name, String detail_pattern, Integer pcategory_group)
	throws SQLException {
		return super.insertPcategory( 1, name, detail_pattern, pcategory_group );
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
	protected int insertAgreement_data(String name, Integer agreement, String expression, Date start_date, Date end_date)
	throws SQLException {
		return super.insertAgreement_data( 1, name, agreement, expression, start_date, end_date );
	}

	/**
	 * Warehouse
	 * @param id Identificador unico del Almacen
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Almacen
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWarehouse(String name, Integer workplace)
	throws SQLException {
		return super.insertWarehouse( 1, name, workplace );
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
	 * @param segment Segmento del Cliente
	 * @param scope Identificador del Ambito
	 * @param e_invoice Indica si el Cliente desea recibir Facturas electronicas
	 * @param delivery_grouped Indica si el Cliente desea agrupar Albaranes en una sola Factura
	 * @param delivery_valuated Indica si el Cliente desea imprimir el Albaran valorado
	 * @throws SQLException
	*/
	protected void insertCustomer(Integer registry, Integer tariff, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer segment, Integer scope, Boolean e_invoice, Boolean delivery_grouped, Boolean delivery_valuated)
	throws SQLException {
		 super.insertCustomer( registry, 1, tariff, surcharge, withholding, transaction, status, segment, scope, e_invoice, delivery_grouped, delivery_valuated );
	}

	/**
	 * Pos
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param workplace Identificador del Centro de Trabajo
	 * @param name Nombre
	 * @param invoiceable Indicador de si es facturable
	 * @param item Identificador del Producto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPos(Integer workplace, String name, Boolean invoiceable, Integer item)
	throws SQLException {
		return super.insertPos( 1, workplace, name, invoiceable, item );
	}

	/**
	 * Message_content
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param content Contenido del Mensaje
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMessage_content(String content)
	throws SQLException {
		return super.insertMessage_content( 1, content );
	}

	/**
	 * Invoice_tax
	 * @param id Identificador unico del Impuesto de la Factura
	 * @param domain Identificador del Dominio
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
		return super.insertInvoice_tax( 1, invoice_detail, tax_type, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_quota );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_attach(Integer contract, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Date attach_date)
	throws SQLException {
		return super.insertContract_attach( 1, contract, mimeType, description, data, type, scope, security_level, attach_date );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIattach(Integer item, Short mimeType, String description, Blob data, Short type)
	throws SQLException {
		return super.insertIattach( 1, item, mimeType, description, data, type );
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
	protected int insertTas_item(Integer model, String publicCode, String privateCode, String description, String add_info)
	throws SQLException {
		return super.insertTas_item( 1, model, publicCode, privateCode, description, add_info );
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
	protected int insertPayment_concept(String code, String description, Short type, Short description_decorable, String expression, String irpf_expression, String quote_expression)
	throws SQLException {
		return super.insertPayment_concept( 1, code, description, type, description_decorable, expression, irpf_expression, quote_expression );
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
	protected int insertCost_profile(String description, Double cost)
	throws SQLException {
		return super.insertCost_profile( 1, description, cost );
	}

	/**
	 * Feature
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Caracteristica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFeature(String name)
	throws SQLException {
		return super.insertFeature( 1, name );
	}

	/**
	 * Salary
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Nomina
	 * @param contract Contrato
	 * @param start_date Fecha de inicio liquidaciùn
	 * @param end_date Fecha de finalizacion liquidaciùn
	 * @param enterprise_name Nombre de la empresa
	 * @param enterprise_address Domicilio de la empresa
	 * @param enterprise_document Numero de Documento de la Empresa
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param employee_name Nombre del trabajador
	 * @param social_security_number Numero de la seguridad social
	 * @param employee_document Numero de Documento de la Persona
	 * @param seniority_date Fecha de antiguedad
	 * @param quote_group Grupo de Cotizaciùn
	 * @param category Categoria o grupo profesional
	 * @param registration Nùmero libro de matricula
	 * @param time_units total dias/horas
	 * @param total_payment Total devengado
	 * @param total_deduction Total a deducir
	 * @param total_liquid Liquido total a percibir
	 * @param total_enterprise Cuota total de la empresa
	 * @param issue_date Fecha de emisiùn
	 * @param remuneration Remuneraciùn mensual
	 * @param pro_ext_base Base prorraterreada de pagas extras
	 * @param it_base Base de IT
	 * @param raw_cgc_base Base efectiva de cotizacion por contingencias comunes 
	 * @param cgc_base Base de cotizacion por contingencias comunes
	 * @param hextra_base Base de cotizacion adicional por horas extraordinarias estructurales
	 * @param non_hextra_base Base de cotizacion adicional por horas extraordinarias no estructurales
	 * @param cgp_base Base de cotizacion por contingencias profesionales
	 * @param money_irpf_base Salario en dinero sujeto a retenciùn I.R.P.F
	 * @param inkind_irpf_base Salario en especie sujeto a retenciùn I.R.P.F
	 * @param irpf_base Base sujeta a retenciùn I.R.P.F
	 * @param social_security_contributions Aportaciones a la Seguridad Social
	 * @param total_irpf Total retenciùn aplicada 
	 * @param charge_date Fecha de cobro
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary(Short type, Integer contract, Date start_date, Date end_date, String enterprise_name, String enterprise_address, String enterprise_document, String ccc, String employee_name, String social_security_number, String employee_document, Date seniority_date, String quote_group, String category, Integer registration, Integer time_units, Double total_payment, Double total_deduction, Double total_liquid, Double total_enterprise, Date issue_date, Double remuneration, Double pro_ext_base, Double it_base, Double raw_cgc_base, Double cgc_base, Double hextra_base, Double non_hextra_base, Double cgp_base, Double money_irpf_base, Double inkind_irpf_base, Double irpf_base, Double social_security_contributions, Double total_irpf, Date charge_date)
	throws SQLException {
		return super.insertSalary( 1, type, contract, start_date, end_date, enterprise_name, enterprise_address, enterprise_document, ccc, employee_name, social_security_number, employee_document, seniority_date, quote_group, category, registration, time_units, total_payment, total_deduction, total_liquid, total_enterprise, issue_date, remuneration, pro_ext_base, it_base, raw_cgc_base, cgc_base, hextra_base, non_hextra_base, cgp_base, money_irpf_base, inkind_irpf_base, irpf_base, social_security_contributions, total_irpf, charge_date );
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
	protected int insertCommission(String name, Date start_date, Date end_date)
	throws SQLException {
		return super.insertCommission( 1, name, start_date, end_date );
	}

	/**
	 * Supplier
	 * @param registry Registro del Proveedor
	 * @param domain Identificador del Dominio
	 * @param withholding Indica si el Proveedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Proveedor
	 * @param status Estado del Proveedor
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertSupplier(Integer registry, Boolean withholding, Short transaction, Short status, Integer scope)
	throws SQLException {
		 super.insertSupplier( registry, 1, withholding, transaction, status, scope );
	}

	/**
	 * Signature
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Firma
	 * @param signature Texto de la Firma de la Cuenta de Correo
	 * @param source Origen de la Firma
	 * @param source_id Identificador del origen de la Firma
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSignature(String name, String signature, Short source, Integer source_id)
	throws SQLException {
		return super.insertSignature( 1, name, signature, source, source_id );
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
	protected int insertUser_scope(Integer user_id, Integer scope)
	throws SQLException {
		return super.insertUser_scope( 1, user_id, scope );
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
	protected int insertAsset(String description, String name)
	throws SQLException {
		return super.insertAsset( 1, description, name );
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
	protected int insertItem_warehouse(Integer item, Integer warehouse, Double stock_max, Double stock_min, String location)
	throws SQLException {
		return super.insertItem_warehouse( 1, item, warehouse, stock_max, stock_min, location );
	}

	/**
	 * Pos_shift
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pos Identificador del TPV
	 * @param shift Turno de trabajo
	 * @param user Identificador del Usuario
	 * @param start_time Fecha-hora de apertura
	 * @param end_time Fecha-hora de cierre
	 * @param initial_amount Efectivo inicial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPos_shift(Integer pos, Short shift, Integer user, Timestamp start_time, Timestamp end_time, Double initial_amount)
	throws SQLException {
		return super.insertPos_shift( 1, pos, shift, user, start_time, end_time, initial_amount );
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
	protected int insertRnote(Integer registry, String description, Date note_date, String comments, Short note_type, Short security_level)
	throws SQLException {
		return super.insertRnote( 1, registry, description, note_date, comments, note_type, security_level );
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
	protected int insertFavorite(Integer favorite_category, String description, String url, Integer user_id)
	throws SQLException {
		return super.insertFavorite( 1, favorite_category, description, url, user_id );
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
	protected int insertDaily_tracking(Integer task_holder, Date tracking_date, Double tracking_duration, Integer job_type, Integer registry, Integer project, Integer activity_type, String comments, Integer task, Double cost)
	throws SQLException {
		return super.insertDaily_tracking( 1, task_holder, tracking_date, tracking_duration, job_type, registry, project, activity_type, comments, task, cost );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLoan(String description, Date loan_date, String term, String interest, String review, Double amount, Double expenses, Integer rbank, Short security_level, Double fee_amount, Integer recurrence, Integer pay_day, Short status)
	throws SQLException {
		return super.insertLoan( 1, description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status );
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
	 * @param dossier Indica si se trata de un Expediente de Cliente
	 * @param reservation Indica si se trata de una Reserva
	 * @param active Indica si el Proyecto esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProject(String name, String alias, Integer registry, Date date, Integer project_type, Boolean tas, Boolean commercial, Boolean dossier, Boolean reservation, Boolean active)
	throws SQLException {
		return super.insertProject( 1, name, alias, registry, date, project_type, tas, commercial, dossier, reservation, active );
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
	protected int insertQuestion_value(Integer question, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		return super.insertQuestion_value( 1, question, value_text, value_number, value_date );
	}

	/**
	 * Fs_mod347_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_mod347 Identificador de la Declaracion
	 * @param type Clave de operacion
	 * @param document NIF del declarado
	 * @param registry Identificador del Declarado
	 * @param name Apellidos  y Nombre del declarado
	 * @param province Provincia del declarado
	 * @param country Pais del declarado
	 * @param amount Importe de las operaciones
	 * @param first_quarter_amount Importe de las operaciones primer trimestre
	 * @param second_quarter_amount Importe de las operaciones segundo trimestre
	 * @param third_quarter_amount Importe de las operaciones tercer trimestre
	 * @param fourth_quarter_amount Importe de las operaciones cuarto trimestre
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_mod347_detail(Integer fs_mod347, String type, String document, Integer registry, String name, Integer province, String country, Double amount, Double first_quarter_amount, Double second_quarter_amount, Double third_quarter_amount, Double fourth_quarter_amount)
	throws SQLException {
		return super.insertFs_mod347_detail( 1, fs_mod347, type, document, registry, name, province, country, amount, first_quarter_amount, second_quarter_amount, third_quarter_amount, fourth_quarter_amount );
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
	protected int insertWeb_info_style(String variable, String value)
	throws SQLException {
		return super.insertWeb_info_style( 1, variable, value );
	}

	/**
	 * Salary_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de Percepciùn Salarial
	 * @param payment_concept Codigo del concepto
	 * @param description Descripcion
	 * @param expression Fùrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary_payment(Integer salary, Short type, String payment_concept, String description, String expression, Double amount)
	throws SQLException {
		return super.insertSalary_payment( 1, salary, type, payment_concept, description, expression, amount );
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
	protected int insertContract_data(String name, Integer contract, String expression, Date start_date, Date end_date)
	throws SQLException {
		return super.insertContract_data( 1, name, contract, expression, start_date, end_date );
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
	protected int insertEnterprise_data(Integer enterprise, String name, String expression, Date start_date, Date end_date)
	throws SQLException {
		return super.insertEnterprise_data( 1, enterprise, name, expression, start_date, end_date );
	}

	/**
	 * Fs_renting_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param fs_renting Identificador de la Declaracion
	 * @param type Modalidad
	 * @param document NIF
	 * @param name Apellidos  y Nombre
	 * @param paid_returns Rendimientos satisfechos
	 * @param percent Porcentaje de retencion
	 * @param account_deposit Ingresos a cuenta
	 * @param accrual_period Periodo de devengo
	 * @param address Direccion
	 * @param city Municipio
	 * @param province Provincia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_renting_detail(Integer fs_renting, Short type, String document, String name, Double paid_returns, Double percent, Double account_deposit, Integer accrual_period, String address, String city, String province)
	throws SQLException {
		return super.insertFs_renting_detail( 1, fs_renting, type, document, name, paid_returns, percent, account_deposit, accrual_period, address, city, province );
	}

	/**
	 * Mk_action
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la Campaùa
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @param template Identificador de la Plantilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action(Integer campaign, Integer media_type, Timestamp start_date, Timestamp end_date, Integer survey, Integer template)
	throws SQLException {
		return super.insertMk_action( 1, campaign, media_type, start_date, end_date, survey, template );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProposal_detail(Integer proposal, Integer item, String description, Double quantity, Double price, String discount_expr, Short status, Integer supplier)
	throws SQLException {
		return super.insertProposal_detail( 1, proposal, item, description, quantity, price, discount_expr, status, supplier );
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
	protected int insertFs_vat_detail(Integer fs_vat, String vat_key, Double percent, Double taxable_base, Double quota, Double deductible_quota, Double adj_taxable_base, Double adj_quota, Double adj_deductible_quota, Double acu_taxable_base, Double acu_quota, Double acu_deductible_quota, Double dec_taxable_base, Double dec_quota, Double dec_deductible_quota, Double res_taxable_base, Double res_quota, Double res_deductible_quota)
	throws SQLException {
		return super.insertFs_vat_detail( 1, fs_vat, vat_key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota, acu_taxable_base, acu_quota, acu_deductible_quota, dec_taxable_base, dec_quota, dec_deductible_quota, res_taxable_base, res_quota, res_deductible_quota );
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
	protected int insertOffer_detail_commission(Integer offer_detail, Double commission, Double amount, Short status, Date pay_date)
	throws SQLException {
		return super.insertOffer_detail_commission( 1, offer_detail, commission, amount, status, pay_date );
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
	protected int insertFavorite_category(String description, Integer user_id)
	throws SQLException {
		return super.insertFavorite_category( 1, description, user_id );
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
	protected int insertProject_type(String description, Boolean active)
	throws SQLException {
		return super.insertProject_type( 1, description, active );
	}

	/**
	 * Balance_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description Descripciùn del detalle de balance
	 * @param accounts Cuentas separadas por comas, que forman el acumulado.
	 * @param sortKey Orden el que aparecera en el listado.
	 * @param title 
	 * @param internal_calculation Indica si es un calculo interno, es decir si el contenido de accounts son referencias a la columna -code- de esta tabla
	 * @param visible Si aparece o no en la impresion.
	 * @param zeroFlag Flag que se activa cuando la cuenta o cuentas tienen valor 0.
	 * @param creditNature Si es verdadero se hace una haber menos debe de las cuentas indicadas
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBalance_detail(Integer balance, String code, String description, String accounts, Integer sortKey, Boolean title, Boolean internal_calculation, Boolean visible, Boolean zeroFlag, Boolean creditNature)
	throws SQLException {
		return super.insertBalance_detail( 1, balance, code, description, accounts, sortKey, title, internal_calculation, visible, zeroFlag, creditNature );
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
	protected int insertInvoice_tax_account(Integer invoice_tax, Integer account)
	throws SQLException {
		return super.insertInvoice_tax_account( 1, invoice_tax, account );
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
	 * @param number Numero de Decl.
	 * @param replaced_number Numero de Decl. complementada o sustituida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_mod347(Integer year, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, Integer number, Integer replaced_number)
	throws SQLException {
		return super.insertFs_mod347( 1, year, administration, comments, status, security_level, complementary, replacement, number, replaced_number );
	}

	/**
	 * Product_account
	 * @param id Identificador unico de la Cuenta Contable del Producto
	 * @param domain Identificador del Dominio
	 * @param product Identificador del Producto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Producto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduct_account(Integer product, Integer account, Short type)
	throws SQLException {
		return super.insertProduct_account( 1, product, account, type );
	}

	/**
	 * Pcategory_tree
	 * @param id Identificador unico del Nodo del Arbol de Categorias
	 * @param domain Identificador del Dominio
	 * @param parent Identificador de la Categoria padre
	 * @param child Identificador de la Categoria hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_tree(Integer parent, Integer child)
	throws SQLException {
		return super.insertPcategory_tree( 1, parent, child );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_detail(Integer account_entry, Integer line, Integer account, String concept, Integer balancing_account, Double debit, Double credit, String document_number)
	throws SQLException {
		return super.insertAccount_entry_detail( 1, account_entry, line, account, concept, balancing_account, debit, credit, document_number );
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
	protected int insertTariff_catalogue(Integer tariff, Integer catalogue)
	throws SQLException {
		return super.insertTariff_catalogue( 1, tariff, catalogue );
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
	protected int insertAmortization_detail(Integer amortization, Date from_date, Date to_date, Double coefficient, Double allocation, Short status, Integer account_entry, Double fiscal_allocation)
	throws SQLException {
		return super.insertAmortization_detail( 1, amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertNotice(Timestamp date, Integer sender, Integer work_group, Integer recipient, String source, String company, String phone, String subject, Short status, Short type, Short priority)
	throws SQLException {
		return super.insertNotice( 1, date, sender, work_group, recipient, source, company, phone, subject, status, type, priority );
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
	protected int insertSalary_bonus(Integer salary, String bonus_concept, Double amount, String description)
	throws SQLException {
		return super.insertSalary_bonus( 1, salary, bonus_concept, amount, description );
	}

	/**
	 * Certifica2_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param domain Identificador del Dominio
	 * @param certifica2_batch Identificador de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCertifica2_batch_attach(Integer certifica2_batch, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date)
	throws SQLException {
		return super.insertCertifica2_batch_attach( 1, certifica2_batch, mimeType, description, data, type, scope, attach_date );
	}

	/**
	 * Process_task
	 * @param id Identificador unico de la Relacion entre Campaùas, Actividades y Tareas
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la Campaùa
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_task(Integer campaign, Integer process_detail, Integer task)
	throws SQLException {
		return super.insertProcess_task( 1, campaign, process_detail, task );
	}

	/**
	 * Bonus_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param expression Importe
	 * @param description Descripcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBonus_concept(String expression, String description)
	throws SQLException {
		return super.insertBonus_concept( 1, expression, description );
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
	protected int insertAmortization_type(Integer fixed_asset_account, Integer accumulated_account, Integer allocation_account, Double percentage, String description)
	throws SQLException {
		return super.insertAmortization_type( 1, fixed_asset_account, accumulated_account, allocation_account, percentage, description );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery_detail(Integer delivery, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer sales_detail)
	throws SQLException {
		return super.insertDelivery_detail( 1, delivery, line, item, description, warehouse, quantity, price, discount_expr, sales_detail );
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
	protected int insertPayroll_workplace(Integer workplace, Integer agreement, Integer enterprise_activity, Integer calendar)
	throws SQLException {
		return super.insertPayroll_workplace( 1, workplace, agreement, enterprise_activity, calendar );
	}

	/**
	 * Catalogue
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Catalogo
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCatalogue(String name, Date start_date, Date end_date)
	throws SQLException {
		return super.insertCatalogue( 1, name, start_date, end_date );
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
	protected int insertAction_entry(Timestamp executionDate, Integer action_id, Integer session_id)
	throws SQLException {
		return super.insertAction_entry( 1, executionDate, action_id, session_id );
	}


	/**
	 * System_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Percepciùn Salarial
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
	protected int insertSystem_payment(Short type, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		return super.insertSystem_payment( 1, type, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type );
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
	protected int insertItem_tariff(Integer item, Integer tariff, Short type, Double profit_percent, Double price)
	throws SQLException {
		return super.insertItem_tariff( 1, item, tariff, type, profit_percent, price );
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
	protected int insertAccount_entry_fbatch(Integer account_entry, Integer fbatch)
	throws SQLException {
		return super.insertAccount_entry_fbatch( 1, account_entry, fbatch );
	}

	/**
	 * Seller
	 * @param registry Registro del Agente Comercial
	 * @param domain Identificador del Dominio
	 * @param commission_type Identificador del Tipo de Comision
	 * @param status Estado del Agente Comercial
	 * @throws SQLException
	*/
	protected void insertSeller(Integer registry, Integer commission_type, Short status)
	throws SQLException {
		 super.insertSeller( registry, 1, commission_type, status );
	}

	/**
	 * Offer_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param offer Identificador del Presupuesto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_attach(Integer offer, Short mimeType, String description, Blob data)
	throws SQLException {
		return super.insertOffer_attach( 1, offer, mimeType, description, data );
	}

	/**
	 * Project_reservation
	 * @param project Identificador del Proyecto
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param code Localizador de la Reserva
	 * @param creation_date Fecha de creacion
	 * @param modification_date Fecha de modificacion
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
	 * @param crs Indica si el origen de la Reserva es un CRS
	 * @param crs_code Codigo de la Reserva en el CRS
	 * @param status Estado de la Reserva
	 * @throws SQLException
	*/
	protected void insertProject_reservation(Integer project, Integer hotel, String code, Timestamp creation_date, Timestamp modification_date, Date start_date, Timestamp start_time, Date end_date, Timestamp end_time, Integer seller, Integer agency, Double agency_commission_percent, Double agency_commission_amount, Boolean agency_rebate, Integer company, Double discount_percent, Double discount_amount, Short booking_holder, Double taxable_base, Double vat_quota, Double other_tax_quota, Double total, String comments, String remarks, Boolean crs, String crs_code, Short status)
	throws SQLException {
		 super.insertProject_reservation( project, 1, hotel, code, creation_date, modification_date, start_date, start_time, end_date, end_time, seller, agency, agency_commission_percent, agency_commission_amount, agency_rebate, company, discount_percent, discount_amount, booking_holder, taxable_base, vat_quota, other_tax_quota, total, comments, remarks, crs, crs_code, status );
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
	protected int insertProcess_detail(Integer process, String description, Integer position, Short date_reference, Integer days, Integer alert_days, Integer workgroup, Short priority, Boolean active, String comments)
	throws SQLException {
		return super.insertProcess_detail( 1, process, description, position, date_reference, days, alert_days, workgroup, priority, active, comments );
	}

	/**
	 * Relationship
	 * @param id Identificador unico del Tipo de Relacion
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRelationship(String description)
	throws SQLException {
		return super.insertRelationship( 1, description );
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
	protected int insertAccount_entry_invoice(Integer account_entry, Integer invoice)
	throws SQLException {
		return super.insertAccount_entry_invoice( 1, account_entry, invoice );
	}

	/**
	 * Tax_account
	 * @param id Identificador unico de la Cuenta Contable del Impuesto
	 * @param domain Identificador del Dominio
	 * @param tax Identificador del Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Impuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax_account(Integer tax, Integer account, Short type)
	throws SQLException {
		return super.insertTax_account( 1, tax, account, type );
	}

	/**
	 * Inventory
	 * @param id Identificador unico del Inventario
	 * @param domain Identificador del Dominio
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInventory(Date inventory_date, Integer warehouse, String description)
	throws SQLException {
		return super.insertInventory( 1, inventory_date, warehouse, description );
	}

	/**
	 * Commercial_term
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condiciùn es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_term(Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		return super.insertCommercial_term( 1, line, name, description, term_general );
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
	protected int insertOffer_term(Integer offer, Integer line, String name, String description, Boolean term_general)
	throws SQLException {
		return super.insertOffer_term( 1, offer, line, name, description, term_general );
	}

	/**
	 * Commercial_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Actividad Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_activity(String name)
	throws SQLException {
		return super.insertCommercial_activity( 1, name );
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
	protected int insertRsegment(Integer registry, Integer segment)
	throws SQLException {
		return super.insertRsegment( 1, registry, segment );
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
	protected void insertProject_tas(Integer project, String series, Integer number, Integer target, Integer tas_item, Double counter, Integer task_holder, String comments, Short status, Date status_date, Integer workplace)
	throws SQLException {
		 super.insertProject_tas( project, 1, series, number, target, tas_item, counter, task_holder, comments, status, status_date, workplace );
	}

	/**
	 * Process_transition_type
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Transicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_transition_type(String description)
	throws SQLException {
		return super.insertProcess_transition_type( 1, description );
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
	protected int insertDeduction_concept(String code, String description, Short type, Short description_decorable, String expression)
	throws SQLException {
		return super.insertDeduction_concept( 1, code, description, type, description_decorable, expression );
	}

	/**
	 * Certifica2_batch_detail
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param domain Identificador del Dominio
	 * @param certifica2_batch Identificador unico del certificado de empresa
	 * @param contract Identificador unico del contrato de empleado
	 * @param enterprise_nif NIF de la empresa
	 * @param ccc Codigo cuenta cotizacion
	 * @param document Documento de identidad
	 * @param name Nombre del trabajador
	 * @param first_surname Primer apellido
	 * @param second_surname Segundo apellido
	 * @param ss_number Numero seguridad social
	 * @param quote_group Grupo de CotizaciÛn
	 * @param contract_type Tipo de contrato
	 * @param contract_duration Duracion contrato
	 * @param contract_duration_indicator Indicador duracion contrato
	 * @param occupation_code Codidgo de profesion
	 * @param public_association_charge Cargo publico sindical
	 * @param dedication_percent Porcentual dedicacion
	 * @param enterprise_start_date Fecha alta empresa
	 * @param suspension_cause_code Codigo causa suspension
	 * @param expire_date Fecha suspension extincion
	 * @param expire_end_date Fecha suspension extincion
	 * @param ere ERE
	 * @param ere_reduction_percent Porcentual reduccion ERE
	 * @param other_reduction_percent Porcentual reduccion otros
	 * @param reduction_cause_code Codigo causa porcentaje reduccion
	 * @param salary_period_start_date Fecha desde periodo salarios
	 * @param salary_period_end_date Fecha hasta periodo salarios
	 * @param salary_processing_days Dias salario tramitacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCertifica2_batch_detail(Integer certifica2_batch, Integer contract, String enterprise_nif, String ccc, String document, String name, String first_surname, String second_surname, String ss_number, String quote_group, String contract_type, String contract_duration, String contract_duration_indicator, String occupation_code, String public_association_charge, String dedication_percent, Date enterprise_start_date, String suspension_cause_code, Date expire_date, Date expire_end_date, String ere, String ere_reduction_percent, String other_reduction_percent, String reduction_cause_code, Date salary_period_start_date, Date salary_period_end_date, String salary_processing_days)
	throws SQLException {
		return super.insertCertifica2_batch_detail( 1, certifica2_batch, contract, enterprise_nif, ccc, document, name, first_surname, second_surname, ss_number, quote_group, contract_type, contract_duration, contract_duration_indicator, occupation_code, public_association_charge, dedication_percent, enterprise_start_date, suspension_cause_code, expire_date, expire_end_date, ere, ere_reduction_percent, other_reduction_percent, reduction_cause_code, salary_period_start_date, salary_period_end_date, salary_processing_days );
	}

	/**
	 * Contract_batch
	 * @param id Identificador unico de la remesa de contratos
	 * @param domain Identificador del Dominio
	 * @param date Fecha de la ultima remesa en la que fue incluido
	 * @param red_notify_date Fecha de notificacion al sistema red
	 * @param red_notify_id Identificador de la notificacion
	 * @param red_response_date Fecha de respuesta del sistema red
	 * @param red_response_id Identificador de la respuesta
	 * @param status Indica el estado de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_batch(Date date, Date red_notify_date, String red_notify_id, Date red_response_date, String red_response_id, Short status)
	throws SQLException {
		return super.insertContract_batch( 1, date, red_notify_date, red_notify_id, red_response_date, red_response_id, status );
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
	protected int insertBalance(String name, Boolean removable, Short type)
	throws SQLException {
		return super.insertBalance( 1, name, removable, type );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax_detail(Integer tax, Date start_date, Date end_date, Double value, Double surcharge)
	throws SQLException {
		return super.insertTax_detail( 1, tax, start_date, end_date, value, surcharge );
	}

	/**
	 * Supplier_account
	 * @param id Identificador unico de la Cuenta Contable del Proveedor
	 * @param domain Identificador del Dominio
	 * @param supplier Identificador del Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier_account(Integer supplier, Integer account)
	throws SQLException {
		return super.insertSupplier_account( 1, supplier, account );
	}

	/**
	 * Bank
	 * @param id Identificador unico de la Entidad Bancaria
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Entidad Bancaria
	 * @param code Codigo de la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBank(String name, String code)
	throws SQLException {
		return super.insertBank( 1, name, code );
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
	protected int insertActivity_type(String description, Integer project_type, Boolean active)
	throws SQLException {
		return super.insertActivity_type( 1, description, project_type, active );
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
	 * @param issue_date Fecha de emisiùn
	 * @param annual_remuneration Retribuciones totales (dinerarias y en especie). Importe ùntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Atr. 18.2 LIRPF)
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Atr. 18.3: Disposiciones transitorias 11ù y 12 ù de la LIRPF)
	 * @param deduccibles_expenses Gastos deducibles ( Atr 19.2, letras a, b y c de la LINRPF: Seguridad Social, Mutualidades ...)
	 * @param spousal_support Pension compensatoria a favor del cùnyuge. Importe fijado judicialmente
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe fijado judicialmente
	 * @param deduct_home_loan Comunicaciùn de pagos por la adquisiùn o rehabilitaciùn de la vivienda habitual utilizando financiaciùn ajena
	 * @param request_irpf Tipo de retenciùn solicitado
	 * @param contract_type Contrato o relaciùn
	 * @param ceuta_melilla Los datos anteriores corresponden a rendimientos obtenidos en Ceuta o Melilla
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIrpf_data(Integer contract, Short family_situation, String spouse_document, Short disability_level, Boolean dependence, Date moving_date, Boolean labour_prolongation, Short descendient_count, Date start_date, Date end_date, Boolean fiscal_exclusion, Date issue_date, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double spousal_support, Double food_annuity, Short deduct_home_loan, Double request_irpf, Short contract_type, Boolean ceuta_melilla)
	throws SQLException {
		return super.insertIrpf_data( 1, contract, family_situation, spouse_document, disability_level, dependence, moving_date, labour_prolongation, descendient_count, start_date, end_date, fiscal_exclusion, issue_date, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, spousal_support, food_annuity, deduct_home_loan, request_irpf, contract_type, ceuta_melilla );
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
	protected int insertProcess_detail_transition(Integer process_detail, Integer process_transition_type, Integer next_process_detail)
	throws SQLException {
		return super.insertProcess_detail_transition( 1, process_detail, process_transition_type, next_process_detail );
	}

	/**
	 * Customer_fee
	 * @param id Identificador unico de la Cuota del Cliente
	 * @param domain Identificador del Dominio
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
		return super.insertCustomer_fee( 1, customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, workplace );
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
	 * @param proposal_detail Identificador del Detalle de Solicitud
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPurchase_detail(Integer purchase, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Integer proposal_detail, Double delivered)
	throws SQLException {
		return super.insertPurchase_detail( 1, purchase, project, line, item, description, quantity, price, discount_expr, taxes, status, proposal_detail, delivered );
	}

	/**
	 * Pm_type_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail(Short type, String description)
	throws SQLException {
		return super.insertPm_type_detail( 1, type, description );
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
	protected int insertHoliday_detail(Integer holiday, Date date, String description)
	throws SQLException {
		return super.insertHoliday_detail( 1, holiday, date, description );
	}

	/**
	 * Target_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param target Identificador del Cliente Potencial
	 * @param item Identificador del Articulo
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_item(Integer target, Integer item, Short status)
	throws SQLException {
		return super.insertTarget_item( 1, target, item, status );
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
	protected int insertAgreement_level_category(Integer agreement_level, String description)
	throws SQLException {
		return super.insertAgreement_level_category( 1, agreement_level, description );
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
	protected int insertAgreement_extra(Integer agreement, Integer agreement_payment, String start_date, String end_date, String issue_date)
	throws SQLException {
		return super.insertAgreement_extra( 1, agreement, agreement_payment, start_date, end_date, issue_date );
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
	protected int insertHoliday(String description, Integer holiday, Boolean editable)
	throws SQLException {
		return super.insertHoliday( 1, description, holiday, editable );
	}

	/**
	 * Pm_type_detail_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail_account(Integer pm_type_detail, Integer account)
	throws SQLException {
		return super.insertPm_type_detail_account( 1, pm_type_detail, account );
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
	protected int insertCommission_category(Integer commission, Integer category, Double quantity, Double rate)
	throws SQLException {
		return super.insertCommission_category( 1, commission, category, quantity, rate );
	}


	/**
	 * Bank_concept_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param bank_concept Identificador del Concepto bancario
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBank_concept_account(Integer bank_concept, Integer account)
	throws SQLException {
		return super.insertBank_concept_account( 1, bank_concept, account );
	}

	/**
	 * Creditor_account
	 * @param id Identificador unico de la Cuenta Contable del Acreedor
	 * @param domain Identificador del Dominio
	 * @param creditor Identificador del Acreedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCreditor_account(Integer creditor, Integer account)
	throws SQLException {
		return super.insertCreditor_account( 1, creditor, account );
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
	 * @param rbank Identificador de la Cuenta Bancaria de la Compaùia
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFinance_tracking(Integer finance, Date tracking_date, Short type, String description, Integer pm_type_detail, Integer rbank, Integer bank_statement_link, Double amount, Boolean recorded)
	throws SQLException {
		return super.insertFinance_tracking( 1, finance, tracking_date, type, description, pm_type_detail, rbank, bank_statement_link, amount, recorded );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRattach(Integer registry, Integer category, Short mimeType, String description, Blob data, Short type, Integer scope, Short security_level, Date attach_date)
	throws SQLException {
		return super.insertRattach( 1, registry, category, mimeType, description, data, type, scope, security_level, attach_date );
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
	protected int insertLeave_batch_detail(Integer leave_batch, Integer contract_leave_detail)
	throws SQLException {
		return super.insertLeave_batch_detail( 1, leave_batch, contract_leave_detail );
	}

	/**
	 * Sales_detail
	 * @param id Identificador unico del Detalle del Pedido de Venta
	 * @param domain Identificador del Dominio
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de lùnea del Detalle dentro del Pedido
	 * @param item Identificador del Articulo del Detalle de Pedido
	 * @param description Descripcion del Detalle de Pedido
	 * @param quantity Cantidad del Detalle de Pedido
	 * @param price Precio del Detalle de Pedido
	 * @param discount_expr Descuentos del Detalle de Pedido
	 * @param taxes Tasas del Detalle de Pedido
	 * @param status Estado del Detalle de Pedido
	 * @param offer_detail Identificador del Detalle del Presupuesto Origen
	 * @param delivered Cantidad entregada del Detalle de Pedido
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSales_detail(Integer sales, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Double taxes, Short status, Integer offer_detail, Double delivered)
	throws SQLException {
		return super.insertSales_detail( 1, sales, line, item, description, quantity, price, discount_expr, taxes, status, offer_detail, delivered );
	}

	/**
	 * System_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Deducciùn
	 * @param deduction_concept Identificador unico del concepto
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression Fùrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la deducciùn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSystem_deduction(Short type, Integer deduction_concept, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		return super.insertSystem_deduction( 1, type, deduction_concept, description, description_decorable, expression, start_date, end_date, month );
	}

	/**
	 * Make
	 * @param id Identificador unico del Fabricante
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Fabricante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMake(String name)
	throws SQLException {
		return super.insertMake( 1, name );
	}

	/**
	 * Room
	 * @param asset Identificador del Activo
	 * @param domain Identificador del Dominio
	 * @param hotel Identificador del Hotel
	 * @param item Identificador del Producto
	 * @throws SQLException
	*/
	protected void insertRoom(Integer asset, Integer hotel, Integer item)
	throws SQLException {
		 super.insertRoom( asset, 1, hotel, item );
	}

	/**
	 * Rbank_account
	 * @param id Identificador unico de la Cuenta Contable de la Cuenta Bancaria
	 * @param domain Identificador del Dominio
	 * @param rbank Identificador de la Cuenta Bancaria
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRbank_account(Integer rbank, Integer account)
	throws SQLException {
		return super.insertRbank_account( 1, rbank, account );
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
	protected int insertModel(Integer make, String name)
	throws SQLException {
		return super.insertModel( 1, make, name );
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
	protected int insertFan_batch_detail(Integer fan_batch, Integer enterprise_ccc)
	throws SQLException {
		return super.insertFan_batch_detail( 1, fan_batch, enterprise_ccc );
	}

	/**
	 * Department
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Departamento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDepartment(String name)
	throws SQLException {
		return super.insertDepartment( 1, name );
	}

	/**
	 * Target_profile
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertTarget_profile( 1, target, last_update, question, value_text, value_number, value_date );
	}

	/**
	 * Purchase
	 * @param id Identificador unico del Pedido de Compra
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
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
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
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
	protected int insertPurchase(Integer project, Integer supplier, String series, Integer number, Integer address, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
		return super.insertPurchase( 1, project, supplier, series, number, address, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account );
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
	protected int insertWeb_info(Integer company, String commercial_description, String schedule, String slogan)
	throws SQLException {
		return super.insertWeb_info( 1, company, commercial_description, schedule, slogan );
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
	protected int insertBank_statement_link(Integer bank_statement, Short source, Integer source_id, Date source_date, Double amount, Short status, Integer linked_bank_statement_link)
	throws SQLException {
		return super.insertBank_statement_link( 1, bank_statement, source, source_id, source_date, amount, status, linked_bank_statement_link );
	}


	/**
	 * Fan_batch
	 * @param id Identificador unico de la remesa
	 * @param domain Identificador del Dominio
	 * @param date Fecha de la ultima remesa en la que fue incluido
	 * @param status Indica el estado de la remesa
	 * @param liquidation_type Indica el tipo de liquidacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFan_batch(Date date, Short status, Short liquidation_type)
	throws SQLException {
		return super.insertFan_batch( 1, date, status, liquidation_type );
	}

	/**
	 * Fs_renting
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
	 * @param lessor_count_accumulated Numero de arrendadores acumulado
	 * @param lessor_count_declared Numero de arrendadores declarado
	 * @param lessor_count_result Numero de arrendadores resultado
	 * @param lessor_count_adjust Numero de arrendadores ajuste
	 * @param lessor_count Numero de arrendadores
	 * @param renting_amount_accumulated Importe de los arrendamientos acumulado
	 * @param renting_amount_declared Importe de los arrendamientos declarado
	 * @param renting_amount_result Importe de los arrendamientos resultado
	 * @param renting_amount_adjust Importe de los arrendamientos ajuste
	 * @param renting_amount Importe de los arrendamientos
	 * @param retention_accumulated Importe de la retencion acumulado
	 * @param retention_declared Importe de la retencion declarado
	 * @param retention_result Importe de la retencion resultado
	 * @param retention_adjust Importe de la retencion ajuste
	 * @param retention Importe de la retencion
	 * @param lessor_count_in_kind_accumulated Numero de arrendadores (especie) acumulado
	 * @param lessor_count_in_kind_declared Numero de arrendadores (especie) declarado
	 * @param lessor_count_in_kind_result Numero de arrendadores (especie) resultado
	 * @param lessor_count_in_kind_adjust Numero de arrendadores (especie) ajuste
	 * @param lessor_count_in_kind Numero de arrendadores (especie)
	 * @param remuneration_in_kind_accumulated Retribucion en especie acumulado
	 * @param remuneration_in_kind_declared Retribucion en especie declarado
	 * @param remuneration_in_kind_result Retribucion en especie resultado
	 * @param remuneration_in_kind_adjust Retribucion en especie ajuste
	 * @param remuneration_in_kind Retribucion en especie
	 * @param account_deposit_accumulated Ingresos a cuenta acumulado
	 * @param account_deposit_declared Ingresos a cuenta declarado
	 * @param account_deposit_result Ingresos a cuenta resultado
	 * @param account_deposit_adjust Ingresos a cuenta ajuste
	 * @param account_deposit Ingresos a cuenta
	 * @param extra_charge Recargo
	 * @param delay_interest Intereses de demora
	 * @param total_tax_debt Total deuda tributaria
	 * @param rbank Banco de la Compaùia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_renting(Integer year, Short period, Short administration, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, Double lessor_count_accumulated, Double lessor_count_declared, Double lessor_count_result, Double lessor_count_adjust, Double lessor_count, Double renting_amount_accumulated, Double renting_amount_declared, Double renting_amount_result, Double renting_amount_adjust, Double renting_amount, Double retention_accumulated, Double retention_declared, Double retention_result, Double retention_adjust, Double retention, Double lessor_count_in_kind_accumulated, Double lessor_count_in_kind_declared, Double lessor_count_in_kind_result, Double lessor_count_in_kind_adjust, Double lessor_count_in_kind, Double remuneration_in_kind_accumulated, Double remuneration_in_kind_declared, Double remuneration_in_kind_result, Double remuneration_in_kind_adjust, Double remuneration_in_kind, Double account_deposit_accumulated, Double account_deposit_declared, Double account_deposit_result, Double account_deposit_adjust, Double account_deposit, Double extra_charge, Double delay_interest, Double total_tax_debt, Integer rbank)
	throws SQLException {
		return super.insertFs_renting( 1, year, period, administration, comments, status, security_level, complementary, replacement, lessor_count_accumulated, lessor_count_declared, lessor_count_result, lessor_count_adjust, lessor_count, renting_amount_accumulated, renting_amount_declared, renting_amount_result, renting_amount_adjust, renting_amount, retention_accumulated, retention_declared, retention_result, retention_adjust, retention, lessor_count_in_kind_accumulated, lessor_count_in_kind_declared, lessor_count_in_kind_result, lessor_count_in_kind_adjust, lessor_count_in_kind, remuneration_in_kind_accumulated, remuneration_in_kind_declared, remuneration_in_kind_result, remuneration_in_kind_adjust, remuneration_in_kind, account_deposit_accumulated, account_deposit_declared, account_deposit_result, account_deposit_adjust, account_deposit, extra_charge, delay_interest, total_tax_debt, rbank );
	}

	/**
	 * Warehouse_transfer_detail
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param warehouse_transfer Identificador del Traspaso
	 * @param item Identificador del Articulo del Detalle de Traspaso
	 * @param quantity Cantidad del Detalle de Traspaso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWarehouse_transfer_detail(Integer warehouse_transfer, Integer item, Double quantity)
	throws SQLException {
		return super.insertWarehouse_transfer_detail( 1, warehouse_transfer, item, quantity );
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
	protected int insertQuestion(Boolean active, String question_text, Short type, String argument, String alias)
	throws SQLException {
		return super.insertQuestion( 1, active, question_text, type, argument, alias );
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
	protected int insertRecord_data(Integer registry, Date creation_date, String description, String notary, String number, Date record_date, String volume, String section, String page, String sheet, String registration, Integer attach)
	throws SQLException {
		return super.insertRecord_data( 1, registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach );
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
	protected int insertCalendar(Integer holiday, Double anual_hours, String description, String comments, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours, Boolean generic, Integer calendar)
	throws SQLException {
		return super.insertCalendar( 1, holiday, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours, generic, calendar );
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
	protected int insertAsset_activity(Integer asset, Date date, Timestamp from_time, Timestamp to_time, String who, String why, Short status)
	throws SQLException {
		return super.insertAsset_activity( 1, asset, date, from_time, to_time, who, why, status );
	}

	/**
	 * Project_reservation_guest
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param guest_index Numero de Huesped
	 * @param name Nombre
	 * @param surname Apellidos
	 * @param treatment Tratamiento
	 * @param document Numero de documento de identificacion
	 * @param document_type Tipo de documento
	 * @param document_country Pais del documento
	 * @param email Email
	 * @param phone Telefono
	 * @param address Direccion
	 * @param zip Codigo postal
	 * @param city Ciudad
	 * @param province Provincia
	 * @param country Pais
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProject_reservation_guest(Integer project_reservation, Short guest_index, String name, String surname, String treatment, String document, Short document_type, String document_country, String email, String phone, String address, String zip, String city, String province, String country)
	throws SQLException {
		return super.insertProject_reservation_guest( 1, project_reservation, guest_index, name, surname, treatment, document, document_type, document_country, email, phone, address, zip, city, province, country );
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
	protected int insertCalendar_period(Integer calendar, String description, Short month, Short start_day, Short end_day, Short monday, Double monday_hours, Short tuesday, Double tuesday_hours, Short wednesday, Double wednesday_hours, Short thursday, Double thursday_hours, Short friday, Double friday_hours, Short saturday, Double saturday_hours, Short sunday, Double sunday_hours)
	throws SQLException {
		return super.insertCalendar_period( 1, calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours );
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
	 * @param code Cùdigo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSystem_cost(Date start_date, Date end_date, String description, String expression, Short type, String code)
	throws SQLException {
		return super.insertSystem_cost( 1, start_date, end_date, description, expression, type, code );
	}

	/**
	 * Fbatch
	 * @param id Identificador unico de la Remesa
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la Compaùia utilizado en la Remesa
	 * @param bank_statement_link Identificador de la Linea del Extracto bancario
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFbatch(String description, Date issue_date, Short type, Short status, Integer rbank, Integer bank_statement_link, Boolean payment, Short security_level)
	throws SQLException {
		return super.insertFbatch( 1, description, issue_date, type, status, rbank, bank_statement_link, payment, security_level );
	}

	/**
	 * Certifica2_batch
	 * @param id Identificador unico del certificado de empresa de la remesa
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador unico del certificado de empresa de la empresa
	 * @param date Fecha de la ultima remesa en la que fue incluido
	 * @param status Estado del certificado correspondiente a la ultima respuesta
	 * @param sign Estado del certificado correspondiente a la ultima respuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCertifica2_batch(Integer enterprise, Date date, Integer status, String sign)
	throws SQLException {
		return super.insertCertifica2_batch( 1, enterprise, date, status, sign );
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
	protected int insertInvoice_detail_account(Integer invoice_detail, Integer account)
	throws SQLException {
		return super.insertInvoice_detail_account( 1, invoice_detail, account );
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
	protected int insertCommission_type(String name, Double rate)
	throws SQLException {
		return super.insertCommission_type( 1, name, rate );
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
	protected int insertAlarm(String description, Timestamp alarm_date, Short status, Short source, Integer source_id, Integer user_id, Short priority)
	throws SQLException {
		return super.insertAlarm( 1, description, alarm_date, status, source, source_id, user_id, priority );
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
	protected int insertPay_method(String name, Short type)
	throws SQLException {
		return super.insertPay_method( 1, name, type );
	}


	/**
	 * Workactivity
	 * @param id Identificador unico de la Actividad
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Actividad
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterpriseCCC Cuenta de Cotizaciùn asociada a la Actividad
	 * @param active Indica si la Actividad esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkactivity(String description, Integer workplace, Integer enterpriseCCC, Boolean active)
	throws SQLException {
		return super.insertWorkactivity( 1, description, workplace, enterpriseCCC, active );
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
	protected void insertProject_commercial(Integer project, Integer target, Integer seller, String comments, Short source, Short status, Date status_date, Integer probability)
	throws SQLException {
		 super.insertProject_commercial( project, 1, target, seller, comments, source, status, status_date, probability );
	}



	/**
	 * Salary_data
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre
	 * @param expression Importe
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary_data(Short name, String expression, Date start_date, Date end_date)
	throws SQLException {
		return super.insertSalary_data( 1, name, expression, start_date, end_date );
	}

	/**
	 * Survey_response
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertSurvey_response( 1, creationDate, response_date, survey, target, user, campaign_action );
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
	protected void insertPerson(Integer registry, Date birth_date, Short gender, Short marital_status, String social_security_num, String name, String first_surname, String second_surname)
	throws SQLException {
		 super.insertPerson( registry, 1, birth_date, gender, marital_status, social_security_num, name, first_surname, second_surname );
	}


	/**
	 * Contract_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Percepciùn Salarial
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
	protected int insertContract_payment(Short type, Integer contract, Integer payment_concept, String description, Short description_decorable, String expression, String irpf_expression, String quote_expression, Date start_date, Short month, Date end_date, Short salary_type)
	throws SQLException {
		return super.insertContract_payment( 1, type, contract, payment_concept, description, description_decorable, expression, irpf_expression, quote_expression, start_date, month, end_date, salary_type );
	}

	/**
	 * Irpf_regularization
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del contrato
	 * @param reason Causa de regularizaciùn
	 * @param effective_date Fecha de entrada en vigor
	 * @param paid_irpf Retenciones practicadas con anterioridad a la regularizaciùn.
	 * @param paid_remuneration Retribuciones ya satisfechas con anterioridad a la regularizaciùn.
	 * @param prior_annual_irpf Retenciones anuales anteriores a la regularizaciùn.
	 * @param prior_annual_remuneration Retribucines anulaes consideradas con anterioridad a la regularizaciùn.
	 * @param prior_base_irpf Base para calcular el tipo de retenciùn determinado antes de la regularizaciùn.
	 * @param prior_irpf Tipo de retenciùn aplicado antes de la regularizaciùn.
	 * @param prior_in_ceuta_melilla Los rendimientos anteriores a la regularizaciùn fueron obtenidos en Ceuta o Melilla
	 * @param prior_minimun_personal_family Mùnimo personal y familiar para calcular el tipo de retenciùn determinado antes de la regularizaciùn.
	 * @param prior_deduct_home_loan En algùn momento antes de la regularizaciùn se aplico la minoraciùn por pagos por la adquisiùn o rehabilitaciùn de la vivienda
	 * @param prior_deduct_home_loan_amount Importe de la minoraciùn por pagos por la adquisiùn o rehabilitaciùn de la vivienda antes de la regularizaciùn
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIrpf_regularization(Integer contract, Short reason, Date effective_date, Double paid_irpf, Double paid_remuneration, Double prior_annual_irpf, Double prior_annual_remuneration, Double prior_base_irpf, Double prior_irpf, Boolean prior_in_ceuta_melilla, Double prior_minimun_personal_family, Short prior_deduct_home_loan, Double prior_deduct_home_loan_amount)
	throws SQLException {
		return super.insertIrpf_regularization( 1, contract, reason, effective_date, paid_irpf, paid_remuneration, prior_annual_irpf, prior_annual_remuneration, prior_base_irpf, prior_irpf, prior_in_ceuta_melilla, prior_minimun_personal_family, prior_deduct_home_loan, prior_deduct_home_loan_amount );
	}

	/**
	 * Cashflow_forecast
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param payment Indica si es un pago o un cobro
	 * @param description Descripcion
	 * @param start_date Fecha de inicio de aplicacion
	 * @param due_date Fecha final de aplicacion
	 * @param rbank Identificador de Banco de la Compaùia
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
	protected int insertCashflow_forecast(Boolean payment, String description, Date start_date, Date due_date, Integer rbank, Double amount, Double payment_day, Boolean january, Boolean february, Boolean march, Boolean april, Boolean may, Boolean june, Boolean july, Boolean august, Boolean september, Boolean october, Boolean november, Boolean december)
	throws SQLException {
		return super.insertCashflow_forecast( 1, payment, description, start_date, due_date, rbank, amount, payment_day, january, february, march, april, may, june, july, august, september, october, november, december );
	}

	/**
	 * Account_entry
	 * @param id Identificador unico del Asiento
	 * @param domain Identificador del Dominio
	 * @param account_period Ejercicio Contable del Asiento
	 * @param entry_date Fecha del Asiento
	 * @param entry_type Tipo de Asiento
	 * @param journal Numero de diario del Asiento
	 * @param security_level Nivel de seguridad del Asiento
	 * @param comments Comentarios del Asiento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry(Integer account_period, Date entry_date, Short entry_type, Integer journal, Short security_level, String comments)
	throws SQLException {
		return super.insertAccount_entry( 1, account_period, entry_date, entry_type, journal, security_level, comments );
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
	protected int insertGeotree(Integer parent, Integer child)
	throws SQLException {
		return super.insertGeotree( 1, parent, child );
	}

	/**
	 * Item_supplier
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param item Identificador de Articulo
	 * @param supplier Identificador de Proveedor
	 * @param code Codigo del Producto en el Proveedor
	 * @param priority Prioridad del Proveedor
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_supplier(Integer item, Integer supplier, String code, Short priority, Integer workplace)
	throws SQLException {
		return super.insertItem_supplier( 1, item, supplier, code, priority, workplace );
	}

	/**
	 * Customer_account
	 * @param id Identificador unico de la Cuenta Contable del Cliente
	 * @param domain Identificador del Dominio
	 * @param customer Identificador del Cliente
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_account(Integer customer, Integer account)
	throws SQLException {
		return super.insertCustomer_account( 1, customer, account );
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
	protected int insertSurvey_question(Integer survey, Integer question, Integer position)
	throws SQLException {
		return super.insertSurvey_question( 1, survey, question, position );
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
	protected int insertStock(Integer warehouse, Integer item, Double quantity)
	throws SQLException {
		return super.insertStock( 1, warehouse, item, quantity );
	}

	/**
	 * Rbank
	 * @param id Identificador unico de la Cuenta Bancaria de la Persona o Empresa
	 * @param domain Identificador del Dominio
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de Cuenta Bancaria de la Persona o Empresa
	 * @param sufix Sufijo de Cuenta Bancaria para Remesas
	 * @param alias Alias de la Cuenta Bancaria
	 * @param active Indica si la Cuenta Bancaria esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRbank(Integer registry, Integer bank, String bank_account, String sufix, String alias, Boolean active)
	throws SQLException {
		return super.insertRbank( 1, registry, bank, bank_account, sufix, alias, active );
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
	protected int insertRpaymethod(Integer registry, Integer pay_method, Integer rbank, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days)
	throws SQLException {
		return super.insertRpaymethod( 1, registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days );
	}

	/**
	 * Sales
	 * @param id Identificador unico del Pedido de Venta
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
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
	 * @param comments Comentarios del Pedido
	 * @param remarks Observaciones del Pedido
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
	protected int insertSales(Integer project, Integer customer, String series, Integer number, Integer shipping_address, Integer seller, String discount_expr, Date issue_date, Integer pay_method, Short document_type, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
		return super.insertSales( 1, project, customer, series, number, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account );
	}

	/**
	 * Income
	 * @param id Identificador unico del Albaran de Compra
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param series Serie del Albaran
	 * @param number Numero del Albaran
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
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncome(Integer project, String series, Integer number, Integer supplier, Integer address, Date issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
		return super.insertIncome( 1, project, series, number, supplier, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account );
	}

	/**
	 * Enterprise_activity
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion de la Actividad de la Empresa
	 * @param enterprise Identificador de la Empresa
	 * @param cnae Identificador del CNAE
	 * @param type Tipo de Actividad de la Empresa
	 * @param cnae2009 Identificador del CNAE 2009
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_activity(String description, Integer enterprise, Integer cnae, Short type, Integer cnae2009)
	throws SQLException {
		return super.insertEnterprise_activity( 1, description, enterprise, cnae, type, cnae2009 );
	}

	/**
	 * Certifica2_batch_data
	 * @param id Identificador unico de los datos de cotizacion del certificado
	 * @param domain Identificador del Dominio
	 * @param certifica2_batch_detail Identificador unico del certificado de empresa de la remesa
	 * @param year Anio
	 * @param month Mes
	 * @param contribution_days Numero de dias cotizados
	 * @param cgc_contribution_base Base de cotizacion de contingencias comunes
	 * @param unemployment_contribution_base Base de cotizacion por desempleo
	 * @param comments Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCertifica2_batch_data(Integer certifica2_batch_detail, Integer year, Integer month, Integer contribution_days, Double cgc_contribution_base, Double unemployment_contribution_base, String comments)
	throws SQLException {
		return super.insertCertifica2_batch_data( 1, certifica2_batch_detail, year, month, contribution_days, cgc_contribution_base, unemployment_contribution_base, comments );
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
	protected int insertWeb_info_page_resource(Integer web_info_page, Integer rattach, String content)
	throws SQLException {
		return super.insertWeb_info_page_resource( 1, web_info_page, rattach, content );
	}


	/**
	 * Fbatch_detail
	 * @param id Identificador unico del Detalle de la Remesa
	 * @param domain Identificador del Dominio
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @param amount Importe del Detalle de la Remesa
	 * @param status Estado del Detalle de la Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFbatch_detail(Integer fbatch, Integer finance, Double amount, Short status)
	throws SQLException {
		return super.insertFbatch_detail( 1, fbatch, finance, amount, status );
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
	protected int insertContract_leave(Short type, Integer contract, String description, Date start_date, Date end_date, Double daily_cgc_base, Double daily_cgp_base, Integer parent, Double daily_reg_base, Short discharge_cause)
	throws SQLException {
		return super.insertContract_leave( 1, type, contract, description, start_date, end_date, daily_cgc_base, daily_cgp_base, parent, daily_reg_base, discharge_cause );
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
	protected int insertCalendar_holiday(Integer calendar, String description, Date date, Short day_type, Double hours)
	throws SQLException {
		return super.insertCalendar_holiday( 1, calendar, description, date, day_type, hours );
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
	protected int insertWorkgroup(String description, Short status)
	throws SQLException {
		return super.insertWorkgroup( 1, description, status );
	}

	/**
	 * Irpf_result
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Identificador del contrato
	 * @param effective_date Fecha de entrada en vigor
	 * @param base_irpf Base para calcular el tipo de retenciùn
	 * @param minimun_personal_family Mùnimo personal y familiar para calcular el tipo de retenciùn
	 * @param deduct_home_loan_amount Minoraciùn por pagos de prùstamo para vivienda habitual
	 * @param deduct_80_bis Deduccion Arttùculo 80 bis LIRPF
	 * @param irpf Tipo retenciùn apliclabe 
	 * @param annual_irpf Importe anual de las retenciones e ingresos a cuenta
	 * @param annual_remuneration Retribuciones anuales. Importe ùntegro
	 * @param irregular_18_2_reduction Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe
	 * @param irregular_18_3_reduction Reducciones por irregularidad ( Art. 18.3: DD.TT 11ù y 12 ù de la LIRPF). Importe
	 * @param deduccibles_expenses Gastos deducibles. Importe anual
	 * @param work_remuneration_reduction Reducciones por rendimiento del trabajo 
	 * @param work_prolongation_reduction Reducciones por prolongaciùn de la actividad 
	 * @param work_moving_reduction Reducciones por movilidad geografica 
	 * @param work_disability_reduction Reducciones por discapacidad 
	 * @param social_security_pensioner Por ser pensionista de la s. social/cl. Pasivas o desempleado
	 * @param two_or_more_descendents_min Por tener mùs de dos descendientes con derecho a mùnimo
	 * @param spousal_support Pension compensatoria a favor del cùnyuge. Importe anual
	 * @param food_annuity Anualidades por alimentos en favor de los hijos. Importe anual
	 * @param minimun_personal Mùnimo personal
	 * @param minimun_ascendents Mùnimo por descendientes
	 * @param minimun_descendents Mùnimo por descendientes
	 * @param minimun_disability Mùnimo por discapacidad
	 * @param descendents_minor_3_total Descendientes computados menores de tres aùos. Total
	 * @param descendents_minor_3_entirely Descendientes computados menores de tres aùos. Por entero
	 * @param descendents_remainder_total Resto de descendientes computados . Total
	 * @param descendents_remainder_entirely Resto de descendientes computados . Por entero
	 * @param descendents_33_65_total Descendientes con discapacidad >= 33% y < 65%. Total
	 * @param descendents_33_65_entirely Descendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param descendents_moving_total Descendientes con discapacidad, movilidad reducida. Total
	 * @param descendents_moving_entirely Descendientes con discapacidad, movilidad reducida. Por entero
	 * @param descendents_65_total Descendientes con discapacidad > 65%. Total
	 * @param descendents_65_entirely Descendientes con discapacidad > 65%. Por entero
	 * @param descendents_first Detalle del cùmputo de descendientes. Hijo 1ù
	 * @param descendents_second Detalle del cùmputo de descendientes. Hijo 2ù
	 * @param descendents_third Detalle del cùmputo de descendientes. Hijo 3ù
	 * @param descendents_fourth_subsequent_total Detalle del cùmputo de descendientes. Hijo 4ù y sucesivos. Total
	 * @param descendents_fourth_subsequent_entirely Detalle del cùmputo de descendientes. Hijo 4ù y sucesivos. Por entero
	 * @param ascendents_minor_75_total Ascendientes computados menores de 75 aùos. Total
	 * @param ascendents_minor_75_entirely Ascendientes computados menores de 75 aùos. Por entero
	 * @param ascendents_mayor_75_total Ascendientes computados mayores de 75 aùos. Total
	 * @param ascendents_mayor_75_entirely Ascendientes computados mayores de 75 aùos. Por entero
	 * @param ascendents_33_65_total Ascendientes con discapacidad >= 33% y < 65%. Total
	 * @param ascendents_33_65_entirely Ascendientes con discapacidad >= 33% y < 65%. Por entero
	 * @param ascendents_moving_total Ascendientes con discapacidad, movilidad reducida. Total
	 * @param ascendents_moving_entirely Ascendientes con discapacidad, movilidad reducida. Por entero
	 * @param ascendents_65_total Ascendientes con discapacidad > 65%. Total
	 * @param ascendents_65_entirely Ascendientes con discapacidad > 65%. Por entero
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIrpf_result(Integer contract, Date effective_date, Double base_irpf, Double minimun_personal_family, Double deduct_home_loan_amount, Double deduct_80_bis, Double irpf, Double annual_irpf, Double annual_remuneration, Double irregular_18_2_reduction, Double irregular_18_3_reduction, Double deduccibles_expenses, Double work_remuneration_reduction, Double work_prolongation_reduction, Double work_moving_reduction, Double work_disability_reduction, Double social_security_pensioner, Double two_or_more_descendents_min, Double spousal_support, Double food_annuity, Double minimun_personal, Double minimun_ascendents, Double minimun_descendents, Double minimun_disability, Short descendents_minor_3_total, Short descendents_minor_3_entirely, Short descendents_remainder_total, Short descendents_remainder_entirely, Short descendents_33_65_total, Short descendents_33_65_entirely, Short descendents_moving_total, Short descendents_moving_entirely, Short descendents_65_total, Short descendents_65_entirely, Short descendents_first, Short descendents_second, Short descendents_third, Short descendents_fourth_subsequent_total, Short descendents_fourth_subsequent_entirely, Short ascendents_minor_75_total, Short ascendents_minor_75_entirely, Short ascendents_mayor_75_total, Short ascendents_mayor_75_entirely, Short ascendents_33_65_total, Short ascendents_33_65_entirely, Short ascendents_moving_total, Short ascendents_moving_entirely, Short ascendents_65_total, Short ascendents_65_entirely)
	throws SQLException {
		return super.insertIrpf_result( 1, contract, effective_date, base_irpf, minimun_personal_family, deduct_home_loan_amount, deduct_80_bis, irpf, annual_irpf, annual_remuneration, irregular_18_2_reduction, irregular_18_3_reduction, deduccibles_expenses, work_remuneration_reduction, work_prolongation_reduction, work_moving_reduction, work_disability_reduction, social_security_pensioner, two_or_more_descendents_min, spousal_support, food_annuity, minimun_personal, minimun_ascendents, minimun_descendents, minimun_disability, descendents_minor_3_total, descendents_minor_3_entirely, descendents_remainder_total, descendents_remainder_entirely, descendents_33_65_total, descendents_33_65_entirely, descendents_moving_total, descendents_moving_entirely, descendents_65_total, descendents_65_entirely, descendents_first, descendents_second, descendents_third, descendents_fourth_subsequent_total, descendents_fourth_subsequent_entirely, ascendents_minor_75_total, ascendents_minor_75_entirely, ascendents_mayor_75_total, ascendents_mayor_75_entirely, ascendents_33_65_total, ascendents_33_65_entirely, ascendents_moving_total, ascendents_moving_entirely, ascendents_65_total, ascendents_65_entirely );
	}

	/**
	 * Category
	 * @param id Identificador unico de la Categoria
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCategory(String name)
	throws SQLException {
		return super.insertCategory( 1, name );
	}

	/**
	 * Mk_action_target
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de Campaùa
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @param comments Comentarios
	 * @param user Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action_target(Integer action, Integer target, Short status, Integer survey_response, String comments, Integer user)
	throws SQLException {
		return super.insertMk_action_target( 1, action, target, status, survey_response, comments, user );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax(String name, Short tax_type, Double percentage, Double surcharge, Date start_date, Short vat_deduction_type, Short withholding_type)
	throws SQLException {
		return super.insertTax( 1, name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type );
	}

	/**
	 * Session
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertSession( 1, endDate, remote_address, remote_host, session_id, startDate, application_id, user_id );
	}

	/**
	 * Invoicing_group
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param parent Grupo de Facturacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group(Integer parent)
	throws SQLException {
		return super.insertInvoicing_group( 1, parent );
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
	protected int insertProject_activity(Integer project, Integer activity_type, Boolean active)
	throws SQLException {
		return super.insertProject_activity( 1, project, activity_type, active );
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
	protected int insertRegistry(String document, Short document_type, String document_country, String name, String alias, Short type, String nationality, Short security_level)
	throws SQLException {
		return super.insertRegistry( 1, document, document_type, document_country, name, alias, type, nationality, security_level );
	}

	/**
	 * Mail_account
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Cuenta de Correo
	 * @param email Cuenta de correo
	 * @param replyto_mail Email de Respuesta
	 * @param host Host del servidor de correo
	 * @param protocol Protocolo utilizado (IMAP)
	 * @param incoming_host Host del correo entrante
	 * @param incoming_port Puerto del correo entrante
	 * @param incoming_ssl Indica si tiene SSL el correo entrante
	 * @param outgoing_verification Indica si hay autentificacion en el correo saliente
	 * @param outgoing_host Host del servidor de correo saliente
	 * @param outgoing_port Puerto del servidor de correo saliente
	 * @param outgoing_ssl Indica si tiene SSL el correo saliente
	 * @param mail_username Nombre del usuario
	 * @param password Clave del usuario
	 * @param default_account Indica si es la cuenta de correo por defecto
	 * @param draft_folder Ruta de Borrador
	 * @param sent_folder Ruta de Enviados
	 * @param trash_folder Ruta de Papelera
	 * @param spam_folder Ruta de Spam
	 * @param display_name Mostrar como
	 * @param signature Identificador de la Firma
	 * @param source Origen de la Firma
	 * @param source_id Identificador del origen de la Firma
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMail_account(String name, String email, String replyto_mail, String host, String protocol, String incoming_host, Integer incoming_port, Boolean incoming_ssl, Boolean outgoing_verification, String outgoing_host, Integer outgoing_port, Boolean outgoing_ssl, String mail_username, String password, Boolean default_account, String draft_folder, String sent_folder, String trash_folder, String spam_folder, String display_name, Integer signature, Short source, Integer source_id)
	throws SQLException {
		return super.insertMail_account( 1, name, email, replyto_mail, host, protocol, incoming_host, incoming_port, incoming_ssl, outgoing_verification, outgoing_host, outgoing_port, outgoing_ssl, mail_username, password, default_account, draft_folder, sent_folder, trash_folder, spam_folder, display_name, signature, source, source_id );
	}

	/**
	 * Creditor
	 * @param registry Registro del Acreedor
	 * @param domain Identificador del Dominio
	 * @param withholding Indica si el Acreedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Acreedor
	 * @param status Estado del Acreedor
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertCreditor(Integer registry, Boolean withholding, Short transaction, Short status, Integer scope)
	throws SQLException {
		 super.insertCreditor( registry, 1, withholding, transaction, status, scope );
	}

	/**
	 * Delivery
	 * @param id Identificador unico del Albaran de Venta
	 * @param domain Identificador del Dominio
	 * @param project Identificador del Proyecto
	 * @param series Serie del Albaran
	 * @param number Nùmero del Albaran
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
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de cuenta en la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery(Integer project, String series, Integer number, Integer customer, Integer address, Timestamp issue_time, Integer pay_method, Short security_level, Short status, String comments, String remarks, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account)
	throws SQLException {
		return super.insertDelivery( 1, project, series, number, customer, address, issue_time, pay_method, security_level, status, comments, remarks, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account );
	}

	/**
	 * Account_period
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del periodo
	 * @param initiation_date Fecha de inicio del Ejercicio
	 * @param deadline Fecha final del Ejercicio
	 * @param status Estado del Ejercicio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_period(String name, Date initiation_date, Date deadline, Short status)
	throws SQLException {
		return super.insertAccount_period( 1, name, initiation_date, deadline, status );
	}

	/**
	 * Mk_template
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Plantilla
	 * @param data Contenido de la Plantilla
	 * @param active Indica si la Plantilla esta activa o no
	 * @param creationDate Fecha de la creacion en el sistema de la Plantilla
	 * @param subject Asunto de la Plantilla
	 * @param append_signature Indica si la Plantilla incluye la firma o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_template(String name, String data, Boolean active, Timestamp creationDate, String subject, Boolean append_signature)
	throws SQLException {
		return super.insertMk_template( 1, name, data, active, creationDate, subject, append_signature );
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
	 * @param security_level Nivel de seguridad de la Serie
	 * @param active Indica si la Serie esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSeries(String code, Integer scope, String description, Boolean tas, Boolean offer, Boolean sales, Boolean delivery, Boolean invoice, Boolean rectification, Short security_level, Boolean active)
	throws SQLException {
		return super.insertSeries( code, 1, scope, description, tas, offer, sales, delivery, invoice, rectification, security_level, active );
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
	 * @param rbank Banco de la Compaùia
	 * @param compensable Compensar o devolver
	 * @param status Estado de la Declaracion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_vat_declaration(Integer fs_vat, Boolean without_activity, Short administration, Double percent, Double operations_volume, Double quota, Double prev_year_compensate_quota, Double done_deposits, Double done_refunds, Double extra_charge, Double delay_interest, Double compensate, Double pay_back, Double deposit, Double prev_deposit, Double prev_pay_back, Double total_tax_debt, Integer rbank, Boolean compensable, Short status)
	throws SQLException {
		return super.insertFs_vat_declaration( 1, fs_vat, without_activity, administration, percent, operations_volume, quota, prev_year_compensate_quota, done_deposits, done_refunds, extra_charge, delay_interest, compensate, pay_back, deposit, prev_deposit, prev_pay_back, total_tax_debt, rbank, compensable, status );
	}


	/**
	 * Workplace
	 * @param id Identificador unico del Centro de Trabajo
	 * @param domain Identificador del Dominio
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param scope Identificador del Ambito
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkplace(Integer enterprise, String description, Integer address, Integer scope, Short economicAgreement, Boolean active)
	throws SQLException {
		return super.insertWorkplace( 1, enterprise, description, address, scope, economicAgreement, active );
	}

	/**
	 * Project_reservation_room
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param room_index Numero de Habitacion
	 * @param item Identificador del Tipo de Habitacion
	 * @param tariff Identificador de la Tarifa
	 * @param adults Numero de adultos
	 * @param children Numero de niùos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProject_reservation_room(Integer project_reservation, Short room_index, Integer item, Integer tariff, Integer adults, Integer children)
	throws SQLException {
		return super.insertProject_reservation_room( 1, project_reservation, room_index, item, tariff, adults, children );
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
	protected int insertAction_denied(Integer action_id, Integer user_id)
	throws SQLException {
		return super.insertAction_denied( 1, action_id, user_id );
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
	protected int insertAgreement_level(Integer agreement, String description)
	throws SQLException {
		return super.insertAgreement_level( 1, agreement, description );
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
	protected int insertUser_workgroup(Integer user_id, Integer workgroup)
	throws SQLException {
		return super.insertUser_workgroup( 1, user_id, workgroup );
	}


	/**
	 * Rrelationship
	 * @param id Identificador unico de la Relacion
	 * @param domain Identificador del Dominio
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de Relaciùn
	 * @param comments Comentarios de la Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRrelationship(Integer registry, Integer related_registry, Integer relationship, String comments)
	throws SQLException {
		return super.insertRrelationship( 1, registry, related_registry, relationship, comments );
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
	protected void insertTask_holder(Integer registry, Short type, Boolean active, Integer user_id, Integer cost_profile)
	throws SQLException {
		 super.insertTask_holder( registry, 1, type, active, user_id, cost_profile );
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
	protected int insertWeb_info_page(String name, Short type, Short position, Boolean active)
	throws SQLException {
		return super.insertWeb_info_page( 1, name, type, position, active );
	}

	/**
	 * Campaign
	 * @param id Identificador unico de la Campaùa
	 * @param domain Identificador del Dominio
	 * @param campaign_type Identificador del Tipo de Campaùa
	 * @param description Descripcion de la Campaùa
	 * @param process Identificador del Proceso
	 * @param start_date Fecha de inicio de la Campaùa
	 * @param end_date Fecha de finalizacion de la Campaùa
	 * @param workgroup Grupo de Trabajo supervisor de la Campaùa
	 * @param manual Tipo de Campaùa
	 * @param status Estado de la Campaùa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign(Integer campaign_type, String description, Integer process, Date start_date, Date end_date, Integer workgroup, Boolean manual, Short status)
	throws SQLException {
		return super.insertCampaign( 1, campaign_type, description, process, start_date, end_date, workgroup, manual, status );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_vat(Integer year, Short period, String comments, Short status, Short security_level, Boolean complementary, Boolean replacement, Boolean tax_refund_registry, Integer number, Double prorata)
	throws SQLException {
		return super.insertFs_vat( 1, year, period, comments, status, security_level, complementary, replacement, tax_refund_registry, number, prorata );
	}

	/**
	 * Contract_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param type Tipo de Deducciùn
	 * @param deduction_concept Identificador unico del concepto
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param description_decorable 
	 * @param expression Fùrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param month Mes de la percepcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_deduction(Short type, Integer deduction_concept, Integer contract, String description, Short description_decorable, String expression, Date start_date, Date end_date, Short month)
	throws SQLException {
		return super.insertContract_deduction( 1, type, deduction_concept, contract, description, description_decorable, expression, start_date, end_date, month );
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
	protected int insertNote(String subject, Timestamp date, Integer owner, String note)
	throws SQLException {
		return super.insertNote( 1, subject, date, owner, note );
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
	protected int insertAction_favorite(Integer position, Integer action_id, Integer user_id)
	throws SQLException {
		return super.insertAction_favorite( 1, position, action_id, user_id );
	}

	/**
	 * Loan_account
	 * @param id Identificador Unico
	 * @param domain Identificador del Dominio
	 * @param loan Prestamo
	 * @param account Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLoan_account(Integer loan, Integer account)
	throws SQLException {
		return super.insertLoan_account( 1, loan, account );
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
	protected int insertSalary_embargo(Integer salary, Integer contract_embargo, Double amount, String description)
	throws SQLException {
		return super.insertSalary_embargo( 1, salary, contract_embargo, amount, description );
	}

	/**
	 * Fs_prof_retention
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de Empresa
	 * @param payment_date Fecha de Pago
	 * @param document Numero de Documento del Profesional
	 * @param document_type Tipo de documento (NIF, CIF...) del Profesional
	 * @param document_country Pais del documento del Profesional
	 * @param name Nombre completo del Profesional
	 * @param concept Concepto
	 * @param taxable_base Base Imponible
	 * @param percent Porcentaje de  retencion
	 * @param quota Cuota de retencion
	 * @param in_kind Indica si el importe es en especie (1) o dinerario (0)
	 * @param withholding_key Clave de retencion
	 * @param withholding_subkey Subclave de retencion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFs_prof_retention(Integer enterprise, Date payment_date, String document, Short document_type, String document_country, String name, String concept, Double taxable_base, Double percent, Double quota, Boolean in_kind, String withholding_key, String withholding_subkey)
	throws SQLException {
		return super.insertFs_prof_retention( 1, enterprise, payment_date, document, document_type, document_country, name, concept, taxable_base, percent, quota, in_kind, withholding_key, withholding_subkey );
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
	protected int insertIrpf_data_descendients(Integer irpf_data, Integer birth_year, Integer adoption_year, Short disability_level, Boolean dependence, Boolean unique_parent)
	throws SQLException {
		return super.insertIrpf_data_descendients( 1, irpf_data, birth_year, adoption_year, disability_level, dependence, unique_parent );
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
	protected int insertSurvey_workflow(Integer questionValue, Integer surveyQuestion, Integer nextSurveyQuestion, Short operator, String value_text, Double value_number, Timestamp value_date)
	throws SQLException {
		return super.insertSurvey_workflow( 1, questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date );
	}

	/**
	 * Product
	 * @param id Identificador unico del Producto
	 * @param domain Identificador del Dominio
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
	 * @param composition_price Indica si el Precio lo determina la Composicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduct(String name, String code, Integer brand, Integer category, Boolean inventoriable, Short status, Integer vat, Integer retention, Short type, Boolean composition, Boolean composition_price)
	throws SQLException {
		return super.insertProduct( 1, name, code, brand, category, inventoriable, status, vat, retention, type, composition, composition_price );
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
	protected int insertProcess(String description, Boolean active)
	throws SQLException {
		return super.insertProcess( 1, description, active );
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
	protected int insertContract_leave_detail(Short type, Integer contract_leave, String college_number, Short confirm_order, String cias, Date date, Short status)
	throws SQLException {
		return super.insertContract_leave_detail( 1, type, contract_leave, college_number, confirm_order, cias, date, status );
	}

	/**
	 * Proposal
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param issue_date Fecha de emision de la Propuesta
	 * @param workplace_department Identificador del Departamento
	 * @param workplace Identificador del Centro de Trabajo
	 * @param scope Identificador del Ambito
	 * @param remarks Observaciones de la Propuesta
	 * @param status Estado de la Propuesta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProposal(Date issue_date, Integer workplace_department, Integer workplace, Integer scope, String remarks, Short status)
	throws SQLException {
		return super.insertProposal( 1, issue_date, workplace_department, workplace, scope, remarks, status );
	}

	/**
	 * Item
	 * @param id Identificador unico del Articulo
	 * @param domain Identificador del Dominio
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
	protected int insertItem(Integer product, String detail, String description, Double price, Short status, Double expenses_percent, Double expenses_fixed, Double profit_percent, Double purchase_price, Boolean internet, String barcode)
	throws SQLException {
		return super.insertItem( 1, product, detail, description, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWarehouse_transfer(String series, Integer number, Timestamp issue_time, String comments, Integer source_warehouse, Integer target_warehouse)
	throws SQLException {
		return super.insertWarehouse_transfer( 1, series, number, issue_time, comments, source_warehouse, target_warehouse );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount(String code, String description, String alias, Short entryEnabled, Short level)
	throws SQLException {
		return super.insertAccount( 1, code, description, alias, entryEnabled, level );
	}

	/**
	 * Brand
	 * @param id Identificador unico de la Marca Comercial
	 * @param domain Identificador del Dominio
	 * @param name Nombre de la Marca Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBrand(String name)
	throws SQLException {
		return super.insertBrand( 1, name );
	}

	/**
	 * Target_seller
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertTarget_seller( 1, target, seller, start_date, end_date, status );
	}

	/**
	 * Contract_bonus
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param contract Contrato
	 * @param description Descripcion
	 * @param expression Fùrmula
	 * @param start_date Fecha de inicio 
	 * @param end_date Fecha de finalizacion
	 * @param bonus_concept Concepto de bonificacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_bonus(Integer contract, String description, String expression, Date start_date, Date end_date, Integer bonus_concept)
	throws SQLException {
		return super.insertContract_bonus( 1, contract, description, expression, start_date, end_date, bonus_concept );
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
	 * @param bank Identificador de la Entidad Bancaria del Vencimiento
	 * @param bank_account Numero de cuenta en la Entidad Bancaria del Vencimiento
	 * @param status Estado del Vencimiento
	 * @param security_level Nivel de seguridad del Vencimiento
	 * @param scope Ambito del Vencimiento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFinance(Boolean payment, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Double amount, Double expenses, String concept, Integer invoice, Date due_date, Integer pay_method, Integer bank, String bank_account, Short status, Short security_level, Integer scope)
	throws SQLException {
		return super.insertFinance( 1, payment, registry, rdocument, rdocument_type, rdocument_country, rname, amount, expenses, concept, invoice, due_date, pay_method, bank, bank_account, status, security_level, scope );
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
	protected int insertTask_holder_workgroup(Integer task_holder, Integer workgroup)
	throws SQLException {
		return super.insertTask_holder_workgroup( 1, task_holder, workgroup );
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
	protected int insertGeozone(String name, String code, Boolean system)
	throws SQLException {
		return super.insertGeozone( 1, name, code, system );
	}

	/**
	 * Pcategory_group
	 * @param id Identificador unico del Grupo de Categorias
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_group(String name)
	throws SQLException {
		return super.insertPcategory_group( 1, name );
	}

	/**
	 * Segment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSegment(String name)
	throws SQLException {
		return super.insertSegment( 1, name );
	}

	/**
	 * Salary_deduction
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param salary Recibo del pago de salarios
	 * @param type Tipo de deducciùn Salarial
	 * @param deduction_concept Codigo del concepto
	 * @param description Descripcion
	 * @param expression Fùrmula
	 * @param amount Importe
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSalary_deduction(Integer salary, Short type, String deduction_concept, String description, String expression, Double amount)
	throws SQLException {
		return super.insertSalary_deduction( 1, salary, type, deduction_concept, description, expression, amount );
	}

	/**
	 * Invoice
	 * @param id Identificador unico de la Factura
	 * @param domain Identificador del Dominio
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
	 * @param taxFree Indica si la Factura esta exenta de Impuestos
	 * @param surcharge Indica si la Factura tiene recargo de equivalencia
	 * @param withholding Indica si la Factura aplica retencion de impuestos
	 * @param comments Comentarios de la Factura
	 * @param remarks Observaciones de la Factura
	 * @param investment Indica si la Factura es una inversion
	 * @param transaction Tipo de transaccion
	 * @param signed Indica si la Factura esta firmada electronicamente
	 * @param scope Ambito de la Factura
	 * @param service Indica si es una Factura de servicios
	 * @param rectification_type Tipo de rectificacion (Normal o Especial)
	 * @param rectification_invoice Relacion de rectificacion de Facturas
	 * @param taxable_base Base Imponible de la Factura
	 * @param vat_quota Cuota de IVA de la Factura
	 * @param retention_quota Cuota de IRPF de la Factura
	 * @param total Total Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice(Integer project, String series, Integer number, String reference_code, Integer registry, String rdocument, Short rdocument_type, String rdocument_country, String rname, Integer raddress, Date issue_date, Date tax_date, Short security_level, Short status, Short type, Boolean taxFree, Boolean surcharge, Boolean withholding, String comments, String remarks, Boolean investment, Short transaction, Boolean signed, Integer scope, Boolean service, Short rectification_type, Integer rectification_invoice, Double taxable_base, Double vat_quota, Double retention_quota, Double total)
	throws SQLException {
		return super.insertInvoice( 1, project, series, number, reference_code, registry, rdocument, rdocument_type, rdocument_country, rname, raddress, issue_date, tax_date, security_level, status, type, taxFree, surcharge, withholding, comments, remarks, investment, transaction, signed, scope, service, rectification_type, rectification_invoice, taxable_base, vat_quota, retention_quota, total );
	}

	/**
	 * Invoice_attach
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param invoice Identificador de la Factura
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_attach(Integer invoice, Short mimeType, String description, Blob data)
	throws SQLException {
		return super.insertInvoice_attach( 1, invoice, mimeType, description, data );
	}

	/**
	 * Invoice_detail
	 * @param id Identificador unico del Detalle de la Factura
	 * @param domain Identificador del Dominio
	 * @param invoice Identificador de la Factura
	 * @param project Identificador del Proyecto
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
	 * @param warehouse Identificador del Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_detail(Integer invoice, Integer project, Integer line, Integer item, String description, Double quantity, Double price, String discount_expr, Short source, Integer source_id, Double taxable_base, Double taxes, Integer workplace, Integer warehouse)
	throws SQLException {
		return super.insertInvoice_detail( 1, invoice, project, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, workplace, warehouse );
	}

	/**
	 * Leave_batch
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha de la Remesa
	 * @param status Indica el estado de la remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLeave_batch(Timestamp date, Short status)
	throws SQLException {
		return super.insertLeave_batch( 1, date, status );
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
	protected int insertProject_reservation_room_detail(Integer project_reservation_room, Integer asset_activity)
	throws SQLException {
		return super.insertProject_reservation_room_detail( 1, project_reservation_room, asset_activity );
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
	protected int insertAccount_entry_finance_tracking(Integer account_entry, Integer finance_tracking)
	throws SQLException {
		return super.insertAccount_entry_finance_tracking( 1, account_entry, finance_tracking );
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
	protected int insertItem_composition(Integer item, Integer composition_item, Integer sequence, String description, Double quantity, String discount_expr)
	throws SQLException {
		return super.insertItem_composition( 1, item, composition_item, sequence, description, quantity, discount_expr );
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
	protected int insertAgreement_level_data(String name, Integer agreement_level, String expression, Date start_date, Date end_date)
	throws SQLException {
		return super.insertAgreement_level_data( 1, name, agreement_level, expression, start_date, end_date );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncome_detail(Integer income, Integer project, Integer line, Integer item, String description, Integer warehouse, Double quantity, Double price, String discount_expr, Integer purchase_detail)
	throws SQLException {
		return super.insertIncome_detail( 1, income, project, line, item, description, warehouse, quantity, price, discount_expr, purchase_detail );
	}

	/**
	 * Workplace_department
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param workplace Identificador del Centro de Trabajo
	 * @param department Identificador del Departamento
	 * @param catalogue Identificador del Catalogo
	 * @param active Indica si el Departamento esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkplace_department(Integer workplace, Integer department, Integer catalogue, Boolean active)
	throws SQLException {
		return super.insertWorkplace_department( 1, workplace, department, catalogue, active );
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
	protected int insertRmedia(Integer registry, Short media, String value, String comment, Boolean administrative, Boolean commercial, Boolean technical, Integer raddress)
	throws SQLException {
		return super.insertRmedia( 1, registry, media, value, comment, administrative, commercial, technical, raddress );
	}

	/**
	 * Commercial_tracking
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param date Fecha del Seguimiento Comercial
	 * @param seller Identificador del Comercial
	 * @param project Identificador del Proyecto
	 * @param activity Identificador de la Actividad Comercial
	 * @param comments Comentarios del Seguimiento Comercial
	 * @param status Estado del Seguimiento Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param end_date Fecha de cierre del Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @param allDay Indica si el Seguimiento Comercial dura todo el dia
	 * @param location Ubicacion del Seguimiento Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_tracking(Timestamp date, Integer seller, Integer project, Integer activity, String comments, Short status, Integer next_commercial_tracking, Timestamp end_date, Integer offer, Boolean allDay, String location)
	throws SQLException {
		return super.insertCommercial_tracking( 1, date, seller, project, activity, comments, status, next_commercial_tracking, end_date, offer, allDay, location );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRaddress(Integer registry, Short type, String recipient, String street_type, String address, String number, String address2, String address3, String zip, String city, Integer geozone, String alias)
	throws SQLException {
		return super.insertRaddress( 1, registry, type, recipient, street_type, address, number, address2, address3, zip, city, geozone, alias );
	}

	/**
	 * Action
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param menu Indica si la Accion esta o no dentro del menu
	 * @param name Nombre de la Accion
	 * @param application_id Aplicacion a la que pertenece la Accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction(Boolean menu, String name, Integer application_id)
	throws SQLException {
		return super.insertAction( 1, menu, name, application_id );
	}

	/**
	 * Survey
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey(Boolean active, Timestamp creationDate, String description)
	throws SQLException {
		return super.insertSurvey( 1, active, creationDate, description );
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
	protected int insertSurvey_response_detail(String value_text, Double value_number, Timestamp value_date, Integer question, Integer surveyResponse)
	throws SQLException {
		return super.insertSurvey_response_detail( 1, value_text, value_number, value_date, question, surveyResponse );
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
	protected int insertRaddinfo(Integer registry, String attribute, String value, Date value_date)
	throws SQLException {
		return super.insertRaddinfo( 1, registry, attribute, value, value_date );
	}

	/**
	 * Invoicing_group_detail
	 * @param id Identificador Unico
	 * @param domain Identificador del Dominio
	 * @param invoicing_group Grupo de Facturacion al que pertenece
	 * @param child Componente asociado a un Grupo de Facturacion
	 * @param grouped Indica si agrupa facturas o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group_detail(Integer invoicing_group, Integer child, Boolean grouped)
	throws SQLException {
		return super.insertInvoicing_group_detail( 1, invoicing_group, child, grouped );
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
	protected int insertCommission_item(Integer commission, Integer item, Double quantity, Double amount, Double rate)
	throws SQLException {
		return super.insertCommission_item( 1, commission, item, quantity, amount, rate );
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
	protected int insertCampaign_type(String description, Boolean active)
	throws SQLException {
		return super.insertCampaign_type( 1, description, active );
	}

	/**
	 * Catalogue_item
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertCatalogue_item( 1, catalogue, item, quantity, price, discount );
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
	 * @param document Impreso (.pdf) del contrato.
	 * @param description Descripcion
	 * @param status Estado de notificacion del contrato
	 * @param registration Nùmero libro de matricula
	 * @param seniority_date Fecha de antiguedad
	 * @param enterprise_activity Actividad
	 * @param ss_regime Regimen de la Seguridad Social
	 * @param agreement_level_category Identificador unico de la Categoria Profesional
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract(Integer person, Integer workplace, Integer enterprise_ccc, Date start_date, Date end_date, Integer calendar, Blob document, String description, Short status, Integer registration, Date seniority_date, Integer enterprise_activity, Short ss_regime, Integer agreement_level_category)
	throws SQLException {
		return super.insertContract( 1, person, workplace, enterprise_ccc, start_date, end_date, calendar, document, description, status, registration, seniority_date, enterprise_activity, ss_regime, agreement_level_category );
	}

	/**
	 * Offer_detail
	 * @param id Identificador unico del Detalle de Presupuesto
	 * @param domain Identificador del Dominio
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
		return super.insertOffer_detail( 1, offer, line, item, description, quantity, price, discount_expr, status );
	}

	/**
	 * Amortization
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
	protected int insertAmortization(String description, Integer amortization_type, Date initial_date, Date deadline, Double amount, Short fee_period, Double sale_amount, String comments, Integer fixed_asset_account, Integer accumulated_account, Integer allocation_account, Double percentage, Short security_level)
	throws SQLException {
		return super.insertAmortization( 1, description, amortization_type, initial_date, deadline, amount, fee_period, sale_amount, comments, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level );
	}

	/**
	 * Enterprise
	 * @param registry Registro de la Empresa
	 * @param domain Identificador del Dominio
	 * @param scope Identificador del Ambito
	 * @param calendar Calendario
	 * @throws SQLException
	*/
	protected void insertEnterprise(Integer registry, Integer scope, Integer calendar)
	throws SQLException {
		 super.insertEnterprise( registry, 1, scope, calendar );
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
	protected int insertCatalogue_category(Integer catalogue, Integer category, Double quantity, Double discount)
	throws SQLException {
		return super.insertCatalogue_category( 1, catalogue, category, quantity, discount );
	}


	/**
	 * Company
	 * @param registry Registro de la Compaùia
	 * @param domain Identificador del Dominio
	 * @param active Indica si la Compaùia es activa o inactiva
	 * @param surcharge Indica si la Compaùia tiene de recargo de equivalencia
	 * @param withholding Indica si la Compaùia aplica retencion de impuestos
	 * @param e_invoice Indica si la Compaùia desea emitir Facturas electronicas
	 * @throws SQLException
	*/
	protected void insertCompany(Integer registry, Boolean active, Boolean surcharge, Boolean withholding, Boolean e_invoice)
	throws SQLException {
		 super.insertCompany( registry, 1, active, surcharge, withholding, e_invoice );
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
	protected int insertAccount_entry_bank_statement(Integer account_entry, Integer bank_statement)
	throws SQLException {
		return super.insertAccount_entry_bank_statement( 1, account_entry, bank_statement );
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
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProject_reservation_service_detail(Integer project_reservation_service, Integer project_reservation_room_detail, Date effective_date, Double quantity, Double price, Double taxable_base, Integer invoice_detail)
	throws SQLException {
		return super.insertProject_reservation_service_detail( 1, project_reservation_service, project_reservation_room_detail, effective_date, quantity, price, taxable_base, invoice_detail );
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
	protected int insertSalary_cost(Integer salary, Double amount, String description, Short type, String cost_concept)
	throws SQLException {
		return super.insertSalary_cost( 1, salary, amount, description, type, cost_concept );
	}

	/**
	 * Project_dossier
	 * @param project Identificador unico del Expediente
	 * @param domain Identificador del Dominio
	 * @param customer Identificador del Cliente
	 * @param number Numero de Expediente
	 * @param location Ubicacion del Expediente
	 * @param status Estado del Expediente
	 * @throws SQLException
	*/
	protected void insertProject_dossier(Integer project, Integer customer, String number, String location, Short status)
	throws SQLException {
		 super.insertProject_dossier( project, 1, customer, number, location, status );
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
	protected int insertCommission_type_commission(Integer commission_type, Integer commission)
	throws SQLException {
		return super.insertCommission_type_commission( 1, commission_type, commission );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInventory_detail(Integer inventory, Integer item, Double actual_quantity, Double real_quantity, Double cost)
	throws SQLException {
		return super.insertInventory_detail( 1, inventory, item, actual_quantity, real_quantity, cost );
	}

	/**
	 * Scope
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertScope(String description)
	throws SQLException {
		return super.insertScope( 1, description );
	}

	/**
	 * Target_supplier
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertTarget_supplier( 1, target, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account );
	}

	/**
	 * Project_reservation_service
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param project_reservation Identificador de la Reserva
	 * @param service_index Numero de Servicio
	 * @param item Identificador del Servicio
	 * @param description Descripcion
	 * @param extra Indica si se trata de un Servicio extra
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProject_reservation_service(Integer project_reservation, Short service_index, Integer item, String description, Boolean extra)
	throws SQLException {
		return super.insertProject_reservation_service( 1, project_reservation, service_index, item, description, extra );
	}

	/**
	 * Contract_batch_detail
	 * @param id Identificador unico del detalle de la remesa
	 * @param domain Identificador del Dominio
	 * @param contract_batch Identificador unico de la remesa de contratos
	 * @param contract Identificador unico del contrato
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_batch_detail(Integer contract_batch, Integer contract)
	throws SQLException {
		return super.insertContract_batch_detail( 1, contract_batch, contract );
	}


	/**
	 * Leave_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param domain Identificador del Dominio
	 * @param leave_batch Identificador de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLeave_batch_attach(Integer leave_batch, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date)
	throws SQLException {
		return super.insertLeave_batch_attach( 1, leave_batch, mimeType, description, data, type, scope, attach_date );
	}

	/**
	 * Agreement_payment
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param agreement Convenio
	 * @param payment_concept Identificador unico del concepto
	 * @param type Tipo de complemento Salarial
	 * @param expression Script
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
	protected int insertAgreement_payment(Integer agreement, Integer payment_concept, Short type, String expression, String description, Date start_date, Date end_date, Short month, Short salary_type, Short description_decorable, String irpf_expression, String quote_expression)
	throws SQLException {
		return super.insertAgreement_payment( 1, agreement, payment_concept, type, expression, description, start_date, end_date, month, salary_type, description_decorable, irpf_expression, quote_expression );
	}

	/**
	 * Fan_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param domain Identificador del Dominio
	 * @param fan_batch Identificador de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFan_batch_attach(Integer fan_batch, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date)
	throws SQLException {
		return super.insertFan_batch_attach( 1, fan_batch, mimeType, description, data, type, scope, attach_date );
	}

	/**
	 * Auto_concept
	 * @param id Identificador unico del Concepto Automatico
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Concepto Automatico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAuto_concept(String description)
	throws SQLException {
		return super.insertAuto_concept( 1, description );
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
	protected int insertWeb_info_page_detail(Integer web_info_page, String title, Integer layout, String content, String extra)
	throws SQLException {
		return super.insertWeb_info_page_detail( 1, web_info_page, title, layout, content, extra );
	}

	/**
	 * Pos_shift_count
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param pos_shift Identificador del Turno de trabajo
	 * @param pay_method Identificador de la Forma de pago
	 * @param amount Total efectivo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPos_shift_count(Integer pos_shift, Integer pay_method, Double amount)
	throws SQLException {
		return super.insertPos_shift_count( 1, pos_shift, pay_method, amount );
	}

	/**
	 * Job_type
	 * @param id Identificador unico del Tipo de Trabajo
	 * @param domain Identificador del Dominio
	 * @param description Descripcion del Tipo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertJob_type(String description)
	throws SQLException {
		return super.insertJob_type( 1, description );
	}

	/**
	 * Tariff
	 * @param id Identificador unico de la Tarifa
	 * @param domain Identificador del Dominio
	 * @param code Codigo de la Tarifa
	 * @param name Nombre de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTariff(String code, String name)
	throws SQLException {
		return super.insertTariff( 1, code, name );
	}

	/**
	 * Contract_batch_attach
	 * @param id Identificador unico del Archivo Adjunto
	 * @param domain Identificador del Dominio
	 * @param contract_batch Identificador de la remesa
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @param scope Ambito del Archivo Adjunto
	 * @param attach_date Fecha del Archivo Adjunto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_batch_attach(Integer contract_batch, Short mimeType, String description, Blob data, Short type, Integer scope, Date attach_date)
	throws SQLException {
		return super.insertContract_batch_attach( 1, contract_batch, mimeType, description, data, type, scope, attach_date );
	}

	/**
	 * Enterprise_agreement
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param enterprise Identificador de la Empresa
	 * @param agreement Identificador del Convenio
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEnterprise_agreement(Integer enterprise, Integer agreement)
	throws SQLException {
		return super.insertEnterprise_agreement( 1, enterprise, agreement );
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
	protected int insertInvoice_address(Integer invoice, String street_type, String address, String number, String address2, String zip, String city, String province, Integer geozone)
	throws SQLException {
		return super.insertInvoice_address( 1, invoice, street_type, address, number, address2, zip, city, province, geozone );
	}

	/**
	 * Message_log
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
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
		return super.insertMessage_log( 1, message_id, message_content, recipient, type, sent_date, message_parts, username );
	}

	/**
	 * Campaign_project
	 * @param id Identificador unico de la Relacion de Campaùas y Expedientes
	 * @param domain Identificador del Dominio
	 * @param campaign Identificador de la Campaùa
	 * @param project Identificador del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign_project(Integer campaign, Integer project)
	throws SQLException {
		return super.insertCampaign_project( 1, campaign, project );
	}

	/**
	 * Application
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param audit_level Nivel de auditoria
	 * @param name Nombre de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertApplication(Short audit_level, String name)
	throws SQLException {
		return super.insertApplication( 1, audit_level, name );
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
	protected int insertContract_calendar_event(Integer contract, Date date, Short type, Double duration)
	throws SQLException {
		return super.insertContract_calendar_event( 1, contract, date, type, duration );
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
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRdir_staff(Integer registry, String document, String name, Boolean shareholder, Boolean representative, Boolean director, Double percent_share, Integer share_number, Double nominal_value, Date due_date, Boolean representative_labor)
	throws SQLException {
		return super.insertRdir_staff( 1, registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date, representative_labor );
	}

	/**
	 * Task
	 * @param id Identificador unico de la Tarea
	 * @param domain Identificador del Dominio
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
	 * @param project Identificador del Expediente
	 * @param registry 
	 * @param activity_type Identificador de la Actividad
	 * @param sender Remitente de la Tarea
	 * @param comments Comentarios de la Tarea
	 * @param repeat_period Periodo de repeticion de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTask(String description, Date start_date, Date end_date, Date due_date, Short priority, Short status, Short percent, Integer task_holder, Integer workgroup, Short source, Integer project, Integer registry, Integer activity_type, Integer sender, String comments, Short repeat_period)
	throws SQLException {
		return super.insertTask( 1, description, start_date, end_date, due_date, priority, status, percent, task_holder, workgroup, source, project, registry, activity_type, sender, comments, repeat_period );
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
	protected int insertAgreement(Integer calendar, String description)
	throws SQLException {
		return super.insertAgreement( 1, calendar, description );
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
	 * @param remarks Observaciones del Presupuesto
	 * @param external_reference Referencia externa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer(Integer project, Integer target, String series, Integer number, Integer version, Integer address, Integer tariff, Integer seller, Integer supplier, String discount_expr, Date issue_date, Integer pay_method, Short security_level, Short status, Short type, Integer workplace, Integer scope, Integer number_of_pymnts, Integer days_to_first_pymnt, Integer days_between_pymnts, String pymnt_days, Integer bank, String bank_account, Boolean signed, String comments, String remarks, String external_reference)
	throws SQLException {
		return super.insertOffer( 1, project, target, series, number, version, address, tariff, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account, signed, comments, remarks, external_reference );
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
	protected int insertAsset_feature(Integer asset, Integer feature)
	throws SQLException {
		return super.insertAsset_feature( 1, asset, feature );
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
	protected int insertContract_embargo(Integer contract, Date start_date, Date end_date, Double amount, String expression, String description)
	throws SQLException {
		return super.insertContract_embargo( 1, contract, start_date, end_date, amount, expression, description );
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
	protected int insertAccount_helper(Integer counter, Integer account, Integer balancing_account)
	throws SQLException {
		return super.insertAccount_helper( 1, counter, account, balancing_account );
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
	protected int insertSystem_data(String name, String expression, Date start_date, Date end_date, Boolean read_only, String comments)
	throws SQLException {
		return super.insertSystem_data( 1, name, expression, start_date, end_date, read_only, comments );
	}

	/**
	 * Bank_concept
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param name Nombre del Concepto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBank_concept(String name)
	throws SQLException {
		return super.insertBank_concept( 1, name );
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
	protected int insertEnterprise_ccc(String ccc, Short type, Integer enterprise_activity, Integer geozone)
	throws SQLException {
		return super.insertEnterprise_ccc( 1, ccc, type, enterprise_activity, geozone );
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
	 * @throws SQLException
	*/
	protected void insertTarget(Integer registry, Integer tariff, Short advertising, Boolean surcharge, Boolean withholding, Short transaction, Short status, Integer scope)
	throws SQLException {
		 super.insertTarget( registry, 1, tariff, advertising, surcharge, withholding, transaction, status, scope );
	}

	/**
	 * Mk_campaign
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param active Indica si la Campaùa esta activa o no
	 * @param description Descripcion de la Campaùa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_campaign(Boolean active, String description)
	throws SQLException {
		return super.insertMk_campaign( 1, active, description );
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
	protected int insertIrpf_data_ascendants(Integer irpf_data, Integer birth_year, Short disability_level, Boolean dependence, Short another_descendient)
	throws SQLException {
		return super.insertIrpf_data_ascendants( 1, irpf_data, birth_year, disability_level, dependence, another_descendient );
	}

	/**
	 * Bank_statement
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param rbank Identificador de Banco de la Compaùia
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
	protected int insertBank_statement(Integer rbank, Integer lot_number, Date operation_date, Short common_concept, String own_concept, Boolean payment, Double amount, Integer document, String reference1, String reference2, String description, Short reliability, Short security_level, Short status, String comments)
	throws SQLException {
		return super.insertBank_statement( 1, rbank, lot_number, operation_date, common_concept, own_concept, payment, amount, document, reference1, reference2, description, reliability, security_level, status, comments );
	}

	/**
	 * Hotel
	 * @param id Identificador unico
	 * @param domain Identificador del Dominio
	 * @param code Codigo del Hotel
	 * @param scope Identificador del Ambito
	 * @param workplace Identificador del Centro de Trabajo
	 * @param customer Identificador del Cliente
	 * @param service_catalogue Identificador del Catalogo de Servicios
	 * @param active Indica si el Hotel esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertHotel(String code, Integer scope, Integer workplace, Integer customer, Integer service_catalogue, Boolean active)
	throws SQLException {
		return super.insertHotel( 1, code, scope, workplace, customer, service_catalogue, active );
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
	protected int insertItem_alternative(Integer item, Integer alternative_item, Short priority)
	throws SQLException {
		return super.insertItem_alternative( 1, item, alternative_item, priority );
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
	 * @param password Contraseùa del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser(String name, String login, Integer enterprise, Integer registry, Boolean active, String password)
	throws SQLException {
		return super.insertUser( 1, name, login, enterprise, registry, active, password );
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
	protected int insertApp_param(String name, String value)
	throws SQLException {
		return super.insertApp_param( 1, name, value );
	}

	
}		
	
	
