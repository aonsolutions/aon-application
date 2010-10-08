package com.esferalia.aon.payroll.ctsql2mysql;

import java.util.HashMap;

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

	/**
	 * Absence
	 * @param course_alumn Identificador del CursoAlumno
	 * @param absence_date Fecha de la Ausencia
	 * @param comments Comentarios de la Ausencia
	 * @param evaluation Numero de Evaluacion en que se produjo la Ausencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAbsence( Integer course_alumn,  Date absence_date,  InputStream comments,  Short evaluation )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO absence ( course_alumn, absence_date, comments, evaluation) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO absence ( course_alumn, absence_date, comments, evaluation) VALUES (  '"+ course_alumn +"', '"+ absence_date +"', '"+ comments +"', '"+ evaluation +"')";
		LOGGER.debug(message);

		if ( course_alumn == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course_alumn);
		}
		if ( absence_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, absence_date);
		}
		if ( comments == null ) {
			stmt.setNull(3, -1);
		}
		else {
			stmt.setAsciiStream(3, comments);
		}
		if ( evaluation == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, evaluation);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> absenceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Absence
	 * @param id Identificador unico
	 * @param course_alumn Identificador del CursoAlumno
	 * @param absence_date Fecha de la Ausencia
	 * @param comments Comentarios de la Ausencia
	 * @param evaluation Numero de Evaluacion en que se produjo la Ausencia
	 * @throws SQLException
	*/
	protected int insertAbsenceOldIds( Integer oldId , Integer course_alumn , Date absence_date , InputStream comments , Short evaluation )
	throws SQLException {
		
		Integer newId = this.absenceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxabsenceIds.get(null);
		newId = this.insertAbsence( course_alumn,  absence_date,  comments,  evaluation );
		absenceIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Academic_skill
	 * @param code Codigo de la Aptitud Academica
	 * @param description Descripcion de la Aptitud Academica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAcademic_skill( String code,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO academic_skill ( code, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO academic_skill ( code, description) VALUES (  '"+ code +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, code);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> academic_skillIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Academic_skill
	 * @param id Identificador unico de la Aptitud Academica
	 * @param code Codigo de la Aptitud Academica
	 * @param description Descripcion de la Aptitud Academica
	 * @throws SQLException
	*/
	protected int insertAcademic_skillOldIds( Integer oldId , String code , String description )
	throws SQLException {
		
		Integer newId = this.academic_skillIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertAcademic_skill( code,  description );
		academic_skillIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Academic_year
	 * @param description Descripcion del Año Academico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAcademic_year( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO academic_year ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO academic_year ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> academic_yearIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Academic_year
	 * @param id Identificador unico del Año Academico
	 * @param description Descripcion del Año Academico
	 * @throws SQLException
	*/
	protected int insertAcademic_yearOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.academic_yearIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertAcademic_year( description );
		academic_yearIds.put(oldId,newId);
		
		return newId;
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
	protected void insertAccount( String id,  String description,  String alias,  Short entryEnabled,  Short level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account ( id, description, alias, entryEnabled, level) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account ( id, description, alias, entryEnabled, level) VALUES ( '"+ id +"', '"+ description +"', '"+ alias +"', '"+ entryEnabled +"', '"+ level +"')";
		LOGGER.debug(message);

		if ( id == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, id);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( alias == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, alias);
		}
		if ( entryEnabled == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, entryEnabled);
		}
		if ( level == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, level);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Account_budget
	 * @param account_period Ejercicio Contable del Presupuesto
	 * @param account Cuenta Contable del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_budget( String account_period,  String account,  Short security_level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_budget ( account_period, account, security_level) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_budget ( account_period, account, security_level) VALUES (  '"+ account_period +"', '"+ account +"', '"+ security_level +"')";
		LOGGER.debug(message);

		if ( account_period == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, account_period);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}
		if ( security_level == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, security_level);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_budgetIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_budget
	 * @param id Identificador unico
	 * @param account_period Ejercicio Contable del Presupuesto
	 * @param account Cuenta Contable del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @throws SQLException
	*/
	protected int insertAccount_budgetOldIds( Integer oldId , String account_period , String account , Short security_level )
	throws SQLException {
		
		Integer newId = this.account_budgetIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_budgetIds.get(null);
		newId = this.insertAccount_budget( account_period,  account,  security_level );
		account_budgetIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAccount_budget_detail( Integer account_budget,  String account_period,  String account,  Short security_level,  Date entry_date,  Double debit,  Double credit )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_budget_detail ( account_budget, account_period, account, security_level, entry_date, debit, credit) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_budget_detail ( account_budget, account_period, account, security_level, entry_date, debit, credit) VALUES (  '"+ account_budget +"', '"+ account_period +"', '"+ account +"', '"+ security_level +"', '"+ entry_date +"', '"+ debit +"', '"+ credit +"')";
		LOGGER.debug(message);

		if ( account_budget == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_budget);
		}
		if ( account_period == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account_period);
		}
		if ( account == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, account);
		}
		if ( security_level == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, security_level);
		}
		if ( entry_date == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, entry_date);
		}
		if ( debit == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, debit);
		}
		if ( credit == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, credit);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_budget_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_budget_detail
	 * @param id Identificador unico
	 * @param account_budget Identificador del Presupuesto
	 * @param account_period Ejercicio Contable del Presupuesto
	 * @param account Cuenta Contable del Presupuesto
	 * @param security_level Nivel de seguridad del Presupuesto
	 * @param entry_date Fecha del Presupuesto
	 * @param debit Debe del Presupuesto
	 * @param credit Haber del Presupuesto
	 * @throws SQLException
	*/
	protected int insertAccount_budget_detailOldIds( Integer oldId , Integer account_budget , String account_period , String account , Short security_level , Date entry_date , Double debit , Double credit )
	throws SQLException {
		
		Integer newId = this.account_budget_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_budget_detailIds.get(null);
		newId = this.insertAccount_budget_detail( account_budget,  account_period,  account,  security_level,  entry_date,  debit,  credit );
		account_budget_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAccount_entry( String account_period,  Date entry_date,  Short entry_type,  Integer journal,  Short security_level,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry ( account_period, entry_date, entry_type, journal, security_level, comments) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry ( account_period, entry_date, entry_type, journal, security_level, comments) VALUES (  '"+ account_period +"', '"+ entry_date +"', '"+ entry_type +"', '"+ journal +"', '"+ security_level +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( account_period == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, account_period);
		}
		if ( entry_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, entry_date);
		}
		if ( entry_type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, entry_type);
		}
		if ( journal == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, journal);
		}
		if ( security_level == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, security_level);
		}
		if ( comments == null ) {
			stmt.setNull(6, -1);
		}
		else {
			stmt.setAsciiStream(6, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_entry
	 * @param id Identificador unico del Asiento
	 * @param account_period Ejercicio Contable del Asiento
	 * @param entry_date Fecha del Asiento
	 * @param entry_type Tipo de Asiento
	 * @param journal Numero de diario del Asiento
	 * @param security_level Nivel de seguridad del Asiento
	 * @param comments Comentarios del Asiento
	 * @throws SQLException
	*/
	protected int insertAccount_entryOldIds( Integer oldId , String account_period , Date entry_date , Short entry_type , Integer journal , Short security_level , InputStream comments )
	throws SQLException {
		
		Integer newId = this.account_entryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entryIds.get(null);
		newId = this.insertAccount_entry( account_period,  entry_date,  entry_type,  journal,  security_level,  comments );
		account_entryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAccount_entry_detail( Integer account_entry,  Integer line,  String account,  String concept,  String balancing_account,  Double debit,  Double credit,  String document_number )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_detail ( account_entry, line, account, concept, balancing_account, debit, credit, document_number) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry_detail ( account_entry, line, account, concept, balancing_account, debit, credit, document_number) VALUES (  '"+ account_entry +"', '"+ line +"', '"+ account +"', '"+ concept +"', '"+ balancing_account +"', '"+ debit +"', '"+ credit +"', '"+ document_number +"')";
		LOGGER.debug(message);

		if ( account_entry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_entry);
		}
		if ( line == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( account == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, account);
		}
		if ( concept == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, concept);
		}
		if ( balancing_account == null ) {
			stmt.setNull(5, 1);
		}
		else {
			stmt.setString(5, balancing_account);
		}
		if ( debit == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, debit);
		}
		if ( credit == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, credit);
		}
		if ( document_number == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, document_number);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entry_detailIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertAccount_entry_detailOldIds( Integer oldId , Integer account_entry , Integer line , String account , String concept , String balancing_account , Double debit , Double credit , String document_number )
	throws SQLException {
		
		Integer newId = this.account_entry_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entry_detailIds.get(null);
		newId = this.insertAccount_entry_detail( account_entry,  line,  account,  concept,  balancing_account,  debit,  credit,  document_number );
		account_entry_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_entry_fbatch
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_fbatch( Integer account_entry,  Integer fbatch )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_fbatch ( account_entry, fbatch) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry_fbatch ( account_entry, fbatch) VALUES (  '"+ account_entry +"', '"+ fbatch +"')";
		LOGGER.debug(message);

		if ( account_entry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_entry);
		}
		if ( fbatch == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, fbatch);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entry_fbatchIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_entry_fbatch
	 * @param id Identificador unico
	 * @param account_entry Identificador de Asiento Contable
	 * @param fbatch Identificador de Remesa
	 * @throws SQLException
	*/
	protected int insertAccount_entry_fbatchOldIds( Integer oldId , Integer account_entry , Integer fbatch )
	throws SQLException {
		
		Integer newId = this.account_entry_fbatchIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entry_fbatchIds.get(null);
		newId = this.insertAccount_entry_fbatch( account_entry,  fbatch );
		account_entry_fbatchIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_entry_finance_tracking
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_finance_tracking( Integer account_entry,  Integer finance_tracking )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_finance_tracking ( account_entry, finance_tracking) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry_finance_tracking ( account_entry, finance_tracking) VALUES (  '"+ account_entry +"', '"+ finance_tracking +"')";
		LOGGER.debug(message);

		if ( account_entry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_entry);
		}
		if ( finance_tracking == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, finance_tracking);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entry_finance_trackingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_entry_finance_tracking
	 * @param id Identificador unico
	 * @param account_entry Identificador de Asiento Contable
	 * @param finance_tracking Identificador de Seguimiento de Vencimientos
	 * @throws SQLException
	*/
	protected int insertAccount_entry_finance_trackingOldIds( Integer oldId , Integer account_entry , Integer finance_tracking )
	throws SQLException {
		
		Integer newId = this.account_entry_finance_trackingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entry_finance_trackingIds.get(null);
		newId = this.insertAccount_entry_finance_tracking( account_entry,  finance_tracking );
		account_entry_finance_trackingIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_entry_invoice
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_invoice( Integer account_entry,  Integer invoice )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_invoice ( account_entry, invoice) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry_invoice ( account_entry, invoice) VALUES (  '"+ account_entry +"', '"+ invoice +"')";
		LOGGER.debug(message);

		if ( account_entry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_entry);
		}
		if ( invoice == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, invoice);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entry_invoiceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_entry_invoice
	 * @param id Identificador unico de Relacion
	 * @param account_entry Identificador de Asiento
	 * @param invoice Identificador de Factura
	 * @throws SQLException
	*/
	protected int insertAccount_entry_invoiceOldIds( Integer oldId , Integer account_entry , Integer invoice )
	throws SQLException {
		
		Integer newId = this.account_entry_invoiceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entry_invoiceIds.get(null);
		newId = this.insertAccount_entry_invoice( account_entry,  invoice );
		account_entry_invoiceIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_entry_link
	 * @param account_entry_from Asiento original
	 * @param account_entry_to Asiento vinculado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_entry_link( Integer account_entry_from,  Integer account_entry_to )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_entry_link ( account_entry_from, account_entry_to) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_entry_link ( account_entry_from, account_entry_to) VALUES (  '"+ account_entry_from +"', '"+ account_entry_to +"')";
		LOGGER.debug(message);

		if ( account_entry_from == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, account_entry_from);
		}
		if ( account_entry_to == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, account_entry_to);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_entry_linkIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_entry_link
	 * @param id Identificador unico
	 * @param account_entry_from Asiento original
	 * @param account_entry_to Asiento vinculado
	 * @throws SQLException
	*/
	protected int insertAccount_entry_linkOldIds( Integer oldId , Integer account_entry_from , Integer account_entry_to )
	throws SQLException {
		
		Integer newId = this.account_entry_linkIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_entry_linkIds.get(null);
		newId = this.insertAccount_entry_link( account_entry_from,  account_entry_to );
		account_entry_linkIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_helper
	 * @param counter Contador, veces que se ha usado
	 * @param account Cuenta contable
	 * @param balancing_account Contrapartida
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAccount_helper( Integer counter,  String account,  String balancing_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_helper ( counter, account, balancing_account) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_helper ( counter, account, balancing_account) VALUES (  '"+ counter +"', '"+ account +"', '"+ balancing_account +"')";
		LOGGER.debug(message);

		if ( counter == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, counter);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}
		if ( balancing_account == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, balancing_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_helperIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_helper
	 * @param id Identificador unico
	 * @param counter Contador, veces que se ha usado
	 * @param account Cuenta contable
	 * @param balancing_account Contrapartida
	 * @throws SQLException
	*/
	protected int insertAccount_helperOldIds( Integer oldId , Integer counter , String account , String balancing_account )
	throws SQLException {
		
		Integer newId = this.account_helperIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_helperIds.get(null);
		newId = this.insertAccount_helper( counter,  account,  balancing_account );
		account_helperIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Account_period
	 * @param id Código del Ejercicio
	 * @param initiation_date Fecha de inicio del Ejercicio
	 * @param deadline Fecha final del Ejercicio
	 * @param status Estado del Ejercicio
	 * @throws SQLException
	*/
	protected void insertAccount_period( String id,  Date initiation_date,  Date deadline,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_period ( id, initiation_date, deadline, status) VALUES ( ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_period ( id, initiation_date, deadline, status) VALUES ( '"+ id +"', '"+ initiation_date +"', '"+ deadline +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( id == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, id);
		}
		if ( initiation_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, initiation_date);
		}
		if ( deadline == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, deadline);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
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
	protected int insertAccount_summary( String account_period,  String account,  Short security_level,  Date entry_date,  Double debit,  Double credit )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO account_summary ( account_period, account, security_level, entry_date, debit, credit) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO account_summary ( account_period, account, security_level, entry_date, debit, credit) VALUES (  '"+ account_period +"', '"+ account +"', '"+ security_level +"', '"+ entry_date +"', '"+ debit +"', '"+ credit +"')";
		LOGGER.debug(message);

		if ( account_period == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, account_period);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}
		if ( security_level == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, security_level);
		}
		if ( entry_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, entry_date);
		}
		if ( debit == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, debit);
		}
		if ( credit == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, credit);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> account_summaryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Account_summary
	 * @param id Identificador unico del Acumulado
	 * @param account_period Ejercicio Contable del Acumulado
	 * @param account Cuenta Contable del Acumulado
	 * @param security_level Nivel de seguridad del Acumulado
	 * @param entry_date Fecha del Acumulado
	 * @param debit Debe del Acumulado
	 * @param credit Haber del Acumulado
	 * @throws SQLException
	*/
	protected int insertAccount_summaryOldIds( Integer oldId , String account_period , String account , Short security_level , Date entry_date , Double debit , Double credit )
	throws SQLException {
		
		Integer newId = this.account_summaryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaccount_summaryIds.get(null);
		newId = this.insertAccount_summary( account_period,  account,  security_level,  entry_date,  debit,  credit );
		account_summaryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Action
	 * @param menu Indica si la Accion esta o no dentro del menu
	 * @param name Nombre de la Accion
	 * @param application_id Aplicacion a la que pertenece la Accion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction( Boolean menu,  String name,  Integer application_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action ( menu, name, application_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO action ( menu, name, application_id) VALUES (  '"+ menu +"', '"+ name +"', '"+ application_id +"')";
		LOGGER.debug(message);

		if ( menu == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, menu);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}
		if ( application_id == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, application_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> actionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Action
	 * @param id Identificador unico
	 * @param menu Indica si la Accion esta o no dentro del menu
	 * @param name Nombre de la Accion
	 * @param application_id Aplicacion a la que pertenece la Accion
	 * @throws SQLException
	*/
	protected int insertActionOldIds( Integer oldId , Boolean menu , String name , Integer application_id )
	throws SQLException {
		
		Integer newId = this.actionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxactionIds.get(null);
		newId = this.insertAction( menu,  name,  application_id );
		actionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Action_denied
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_denied( Integer action_id,  Integer user_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_denied ( action_id, user_id) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO action_denied ( action_id, user_id) VALUES (  '"+ action_id +"', '"+ user_id +"')";
		LOGGER.debug(message);

		if ( action_id == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, action_id);
		}
		if ( user_id == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, user_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> action_deniedIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Action_denied
	 * @param id Identificador unico
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @throws SQLException
	*/
	protected int insertAction_deniedOldIds( Integer oldId , Integer action_id , Integer user_id )
	throws SQLException {
		
		Integer newId = this.action_deniedIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaction_deniedIds.get(null);
		newId = this.insertAction_denied( action_id,  user_id );
		action_deniedIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Action_entry
	 * @param executionDate Fecha de ejecucion
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_entry( Timestamp executionDate,  Integer action_id,  Integer session_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_entry ( executionDate, action_id, session_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO action_entry ( executionDate, action_id, session_id) VALUES (  '"+ executionDate +"', '"+ action_id +"', '"+ session_id +"')";
		LOGGER.debug(message);

		if ( executionDate == null ) {
			stmt.setNull(1, 93);
		}
		else {
			stmt.setTimestamp(1, executionDate);
		}
		if ( action_id == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, action_id);
		}
		if ( session_id == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, session_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> action_entryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Action_entry
	 * @param id Identificador unico
	 * @param executionDate Fecha de ejecucion
	 * @param action_id Identificador de la Accion
	 * @param session_id Identificador de la Sesion
	 * @throws SQLException
	*/
	protected int insertAction_entryOldIds( Integer oldId , Timestamp executionDate , Integer action_id , Integer session_id )
	throws SQLException {
		
		Integer newId = this.action_entryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaction_entryIds.get(null);
		newId = this.insertAction_entry( executionDate,  action_id,  session_id );
		action_entryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Action_favorite
	 * @param position Posicion dentro de las Acciones Favoritas
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAction_favorite( Integer position,  Integer action_id,  Integer user_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO action_favorite ( position, action_id, user_id) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO action_favorite ( position, action_id, user_id) VALUES (  '"+ position +"', '"+ action_id +"', '"+ user_id +"')";
		LOGGER.debug(message);

		if ( position == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, position);
		}
		if ( action_id == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, action_id);
		}
		if ( user_id == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, user_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> action_favoriteIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Action_favorite
	 * @param id Identificador unico
	 * @param position Posicion dentro de las Acciones Favoritas
	 * @param action_id Identificador de la Accion
	 * @param user_id Identificador del Usuario
	 * @throws SQLException
	*/
	protected int insertAction_favoriteOldIds( Integer oldId , Integer position , Integer action_id , Integer user_id )
	throws SQLException {
		
		Integer newId = this.action_favoriteIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxaction_favoriteIds.get(null);
		newId = this.insertAction_favorite( position,  action_id,  user_id );
		action_favoriteIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Activity
	 * @param dossier Identificador del Expendiente
	 * @param activity_type Tipo de Actividad
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity( Integer dossier,  Integer activity_type,  Integer workgroup )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity ( dossier, activity_type, workgroup) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO activity ( dossier, activity_type, workgroup) VALUES (  '"+ dossier +"', '"+ activity_type +"', '"+ workgroup +"')";
		LOGGER.debug(message);

		if ( dossier == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, dossier);
		}
		if ( activity_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, activity_type);
		}
		if ( workgroup == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, workgroup);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> activityIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Activity
	 * @param id Identificador unico de la Actividad
	 * @param dossier Identificador del Expendiente
	 * @param activity_type Tipo de Actividad
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @throws SQLException
	*/
	protected int insertActivityOldIds( Integer oldId , Integer dossier , Integer activity_type , Integer workgroup )
	throws SQLException {
		
		Integer newId = this.activityIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxactivityIds.get(null);
		newId = this.insertActivity( dossier,  activity_type,  workgroup );
		activityIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Activity_process
	 * @param campaign Identificador de la Campaña
	 * @param activity Identificador de la Actividad
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity_process( Integer campaign,  Integer activity,  Integer process_detail,  Integer task )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity_process ( campaign, activity, process_detail, task) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO activity_process ( campaign, activity, process_detail, task) VALUES (  '"+ campaign +"', '"+ activity +"', '"+ process_detail +"', '"+ task +"')";
		LOGGER.debug(message);

		if ( campaign == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, campaign);
		}
		if ( activity == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, activity);
		}
		if ( process_detail == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, process_detail);
		}
		if ( task == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, task);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> activity_processIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Activity_process
	 * @param id Identificador unico de la Relacion entre Campañas, Actividades y Tareas
	 * @param campaign Identificador de la Campaña
	 * @param activity Identificador de la Actividad
	 * @param process_detail Identificador del Detalle de Proceso
	 * @param task Identificador de la Tarea
	 * @throws SQLException
	*/
	protected int insertActivity_processOldIds( Integer oldId , Integer campaign , Integer activity , Integer process_detail , Integer task )
	throws SQLException {
		
		Integer newId = this.activity_processIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxactivity_processIds.get(null);
		newId = this.insertActivity_process( campaign,  activity,  process_detail,  task );
		activity_processIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Activity_type
	 * @param description Descripcion del Tipo de Actividad
	 * @param dossier_type Tipo de Dossier
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertActivity_type( String description,  Integer dossier_type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO activity_type ( description, dossier_type) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO activity_type ( description, dossier_type) VALUES (  '"+ description +"', '"+ dossier_type +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( dossier_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, dossier_type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> activity_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Activity_type
	 * @param id Identificador unico del Tipo de Actividad
	 * @param description Descripcion del Tipo de Actividad
	 * @param dossier_type Tipo de Dossier
	 * @throws SQLException
	*/
	protected int insertActivity_typeOldIds( Integer oldId , String description , Integer dossier_type )
	throws SQLException {
		
		Integer newId = this.activity_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxactivity_typeIds.get(null);
		newId = this.insertActivity_type( description,  dossier_type );
		activity_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAlarm( InputStream description,  Timestamp alarm_date,  Short status,  Short source,  Integer source_id,  Integer user_id,  Short priority )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO alarm ( description, alarm_date, status, source, source_id, user_id, priority) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO alarm ( description, alarm_date, status, source, source_id, user_id, priority) VALUES (  '"+ description +"', '"+ alarm_date +"', '"+ status +"', '"+ source +"', '"+ source_id +"', '"+ user_id +"', '"+ priority +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, -1);
		}
		else {
			stmt.setAsciiStream(1, description);
		}
		if ( alarm_date == null ) {
			stmt.setNull(2, 93);
		}
		else {
			stmt.setTimestamp(2, alarm_date);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}
		if ( source == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, source);
		}
		if ( source_id == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, source_id);
		}
		if ( user_id == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, user_id);
		}
		if ( priority == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, priority);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> alarmIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertAlarmOldIds( Integer oldId , InputStream description , Timestamp alarm_date , Short status , Short source , Integer source_id , Integer user_id , Short priority )
	throws SQLException {
		
		Integer newId = this.alarmIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxalarmIds.get(null);
		newId = this.insertAlarm( description,  alarm_date,  status,  source,  source_id,  user_id,  priority );
		alarmIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAlumn_loan( Integer customer,  String material,  Date loan_date,  Date end_date,  String comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO alumn_loan ( customer, material, loan_date, end_date, comments) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO alumn_loan ( customer, material, loan_date, end_date, comments) VALUES (  '"+ customer +"', '"+ material +"', '"+ loan_date +"', '"+ end_date +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( customer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, customer);
		}
		if ( material == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, material);
		}
		if ( loan_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, loan_date);
		}
		if ( end_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, end_date);
		}
		if ( comments == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> alumn_loanIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Alumn_loan
	 * @param id Identificador unico del Prestamo
	 * @param customer Alumno al que se le realizo el Prestamo
	 * @param material Material prestado
	 * @param loan_date Fecha del Prestamo
	 * @param end_date Fecha devolucion del material
	 * @param comments Observaciones
	 * @throws SQLException
	*/
	protected int insertAlumn_loanOldIds( Integer oldId , Integer customer , String material , Date loan_date , Date end_date , String comments )
	throws SQLException {
		
		Integer newId = this.alumn_loanIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxalumn_loanIds.get(null);
		newId = this.insertAlumn_loan( customer,  material,  loan_date,  end_date,  comments );
		alumn_loanIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAmortization( String description,  Integer amortization_type,  Date initial_date,  Date deadline,  Double amount,  Short fee_period,  Double sale_amount,  InputStream comments,  String fixed_asset_account,  String accumulated_account,  String allocation_account,  Double percentage,  Short security_level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization ( description, amortization_type, initial_date, deadline, amount, fee_period, sale_amount, comments, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO amortization ( description, amortization_type, initial_date, deadline, amount, fee_period, sale_amount, comments, fixed_asset_account, accumulated_account, allocation_account, percentage, security_level) VALUES (  '"+ description +"', '"+ amortization_type +"', '"+ initial_date +"', '"+ deadline +"', '"+ amount +"', '"+ fee_period +"', '"+ sale_amount +"', '"+ comments +"', '"+ fixed_asset_account +"', '"+ accumulated_account +"', '"+ allocation_account +"', '"+ percentage +"', '"+ security_level +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( amortization_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, amortization_type);
		}
		if ( initial_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, initial_date);
		}
		if ( deadline == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, deadline);
		}
		if ( amount == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, amount);
		}
		if ( fee_period == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, fee_period);
		}
		if ( sale_amount == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, sale_amount);
		}
		if ( comments == null ) {
			stmt.setNull(8, -1);
		}
		else {
			stmt.setAsciiStream(8, comments);
		}
		if ( fixed_asset_account == null ) {
			stmt.setNull(9, 1);
		}
		else {
			stmt.setString(9, fixed_asset_account);
		}
		if ( accumulated_account == null ) {
			stmt.setNull(10, 1);
		}
		else {
			stmt.setString(10, accumulated_account);
		}
		if ( allocation_account == null ) {
			stmt.setNull(11, 1);
		}
		else {
			stmt.setString(11, allocation_account);
		}
		if ( percentage == null ) {
			stmt.setNull(12, 8);
		}
		else {
			stmt.setDouble(12, percentage);
		}
		if ( security_level == null ) {
			stmt.setNull(13, -6);
		}
		else {
			stmt.setShort(13, security_level);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> amortizationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Amortization
	 * @param id Identificador unico
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
	 * @throws SQLException
	*/
	protected int insertAmortizationOldIds( Integer oldId , String description , Integer amortization_type , Date initial_date , Date deadline , Double amount , Short fee_period , Double sale_amount , InputStream comments , String fixed_asset_account , String accumulated_account , String allocation_account , Double percentage , Short security_level )
	throws SQLException {
		
		Integer newId = this.amortizationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxamortizationIds.get(null);
		newId = this.insertAmortization( description,  amortization_type,  initial_date,  deadline,  amount,  fee_period,  sale_amount,  comments,  fixed_asset_account,  accumulated_account,  allocation_account,  percentage,  security_level );
		amortizationIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAmortization_detail( Integer amortization,  Date from_date,  Date to_date,  Double coefficient,  Double allocation,  Short status,  Integer account_entry,  Double fiscal_allocation )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization_detail ( amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO amortization_detail ( amortization, from_date, to_date, coefficient, allocation, status, account_entry, fiscal_allocation) VALUES (  '"+ amortization +"', '"+ from_date +"', '"+ to_date +"', '"+ coefficient +"', '"+ allocation +"', '"+ status +"', '"+ account_entry +"', '"+ fiscal_allocation +"')";
		LOGGER.debug(message);

		if ( amortization == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, amortization);
		}
		if ( from_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, from_date);
		}
		if ( to_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, to_date);
		}
		if ( coefficient == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, coefficient);
		}
		if ( allocation == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, allocation);
		}
		if ( status == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, status);
		}
		if ( account_entry == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, account_entry);
		}
		if ( fiscal_allocation == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, fiscal_allocation);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> amortization_detailIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertAmortization_detailOldIds( Integer oldId , Integer amortization , Date from_date , Date to_date , Double coefficient , Double allocation , Short status , Integer account_entry , Double fiscal_allocation )
	throws SQLException {
		
		Integer newId = this.amortization_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxamortization_detailIds.get(null);
		newId = this.insertAmortization_detail( amortization,  from_date,  to_date,  coefficient,  allocation,  status,  account_entry,  fiscal_allocation );
		amortization_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAmortization_type( String fixed_asset_account,  String accumulated_account,  String allocation_account,  Double percentage,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO amortization_type ( fixed_asset_account, accumulated_account, allocation_account, percentage, description) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO amortization_type ( fixed_asset_account, accumulated_account, allocation_account, percentage, description) VALUES (  '"+ fixed_asset_account +"', '"+ accumulated_account +"', '"+ allocation_account +"', '"+ percentage +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( fixed_asset_account == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, fixed_asset_account);
		}
		if ( accumulated_account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, accumulated_account);
		}
		if ( allocation_account == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, allocation_account);
		}
		if ( percentage == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, percentage);
		}
		if ( description == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> amortization_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Amortization_type
	 * @param id Identificador unico
	 * @param fixed_asset_account Cuenta de inmovilizado
	 * @param accumulated_account Cuenta de amortizacion acumulada
	 * @param allocation_account Cuenta para la dotacion de la amortizacion
	 * @param percentage Porcentaje de amortizacion
	 * @param description Descripcion del Tipo de Amortizacion
	 * @throws SQLException
	*/
	protected int insertAmortization_typeOldIds( Integer oldId , String fixed_asset_account , String accumulated_account , String allocation_account , Double percentage , String description )
	throws SQLException {
		
		Integer newId = this.amortization_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxamortization_typeIds.get(null);
		newId = this.insertAmortization_type( fixed_asset_account,  accumulated_account,  allocation_account,  percentage,  description );
		amortization_typeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * App_param
	 * @param name Nombre del Parametro
	 * @param value Valor del Parametro
	 * @throws SQLException
	*/
	protected void insertApp_param( String name,  String value )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO app_param ( name, value) VALUES ( ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO app_param ( name, value) VALUES ( '"+ name +"', '"+ value +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( value == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, value);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Application
	 * @param audit_level Nivel de auditoria
	 * @param name Nombre de la Aplicacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertApplication( Short audit_level,  String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO application ( audit_level, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO application ( audit_level, name) VALUES (  '"+ audit_level +"', '"+ name +"')";
		LOGGER.debug(message);

		if ( audit_level == null ) {
			stmt.setNull(1, -6);
		}
		else {
			stmt.setShort(1, audit_level);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> applicationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Application
	 * @param id Identificador unico
	 * @param audit_level Nivel de auditoria
	 * @param name Nombre de la Aplicacion
	 * @throws SQLException
	*/
	protected int insertApplicationOldIds( Integer oldId , Short audit_level , String name )
	throws SQLException {
		
		Integer newId = this.applicationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertApplication( audit_level,  name );
		applicationIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Appraiser
	 * @param registry Registro del Perito
	 * @throws SQLException
	*/
	protected void insertAppraiser( Integer registry )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO appraiser ( registry) VALUES ( ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO appraiser ( registry) VALUES ( '"+ registry +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Asset
	 * @param description Descripcion del Activo
	 * @param name Nombre corto del Activo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAsset( String description,  String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO asset ( description, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO asset ( description, name) VALUES (  '"+ description +"', '"+ name +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> assetIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Asset
	 * @param id Identificador unico
	 * @param description Descripcion del Activo
	 * @param name Nombre corto del Activo
	 * @throws SQLException
	*/
	protected int insertAssetOldIds( Integer oldId , String description , String name )
	throws SQLException {
		
		Integer newId = this.assetIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertAsset( description,  name );
		assetIds.put(oldId,newId);
		
		return newId;
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
	protected int insertAsset_activity( Integer asset,  Date date,  Timestamp from_time,  Timestamp to_time,  String who,  String why,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO asset_activity ( asset, date, from_time, to_time, who, why, status) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO asset_activity ( asset, date, from_time, to_time, who, why, status) VALUES (  '"+ asset +"', '"+ date +"', '"+ from_time +"', '"+ to_time +"', '"+ who +"', '"+ why +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( asset == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, asset);
		}
		if ( date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, date);
		}
		if ( from_time == null ) {
			stmt.setNull(3, 93);
		}
		else {
			stmt.setTimestamp(3, from_time);
		}
		if ( to_time == null ) {
			stmt.setNull(4, 93);
		}
		else {
			stmt.setTimestamp(4, to_time);
		}
		if ( who == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, who);
		}
		if ( why == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, why);
		}
		if ( status == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> asset_activityIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertAsset_activityOldIds( Integer oldId , Integer asset , Date date , Timestamp from_time , Timestamp to_time , String who , String why , Short status )
	throws SQLException {
		
		Integer newId = this.asset_activityIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxasset_activityIds.get(null);
		newId = this.insertAsset_activity( asset,  date,  from_time,  to_time,  who,  why,  status );
		asset_activityIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Auto_concept
	 * @param description Descripcion del Concepto Automatico
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertAuto_concept( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO auto_concept ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO auto_concept ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> auto_conceptIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Auto_concept
	 * @param id Identificador unico del Concepto Automatico
	 * @param description Descripcion del Concepto Automatico
	 * @throws SQLException
	*/
	protected int insertAuto_conceptOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.auto_conceptIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertAuto_concept( description );
		auto_conceptIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Balance
	 * @param name Nombre del Balance
	 * @param removable Indica se puede ser borrado por el usuario
	 * @param type Tipo de Balance
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBalance( String name,  Boolean removable,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO balance ( name, removable, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO balance ( name, removable, type) VALUES (  '"+ name +"', '"+ removable +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( removable == null ) {
			stmt.setNull(2, -7);
		}
		else {
			stmt.setBoolean(2, removable);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> balanceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Balance
	 * @param id Identificador unico
	 * @param name Nombre del Balance
	 * @param removable Indica se puede ser borrado por el usuario
	 * @param type Tipo de Balance
	 * @throws SQLException
	*/
	protected int insertBalanceOldIds( Integer oldId , String name , Boolean removable , Short type )
	throws SQLException {
		
		Integer newId = this.balanceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertBalance( name,  removable,  type );
		balanceIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Balance_detail
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description Descripción del detalle de balance
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
	protected int insertBalance_detail( Integer balance,  String code,  String description,  InputStream accounts,  Integer sortKey,  Boolean title,  Boolean internal_calculation,  Boolean visible,  Boolean zeroFlag,  Boolean creditNature )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO balance_detail ( balance, code, description, accounts, sortKey, title, internal_calculation, visible, zeroFlag, creditNature) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO balance_detail ( balance, code, description, accounts, sortKey, title, internal_calculation, visible, zeroFlag, creditNature) VALUES (  '"+ balance +"', '"+ code +"', '"+ description +"', '"+ accounts +"', '"+ sortKey +"', '"+ title +"', '"+ internal_calculation +"', '"+ visible +"', '"+ zeroFlag +"', '"+ creditNature +"')";
		LOGGER.debug(message);

		if ( balance == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, balance);
		}
		if ( code == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, code);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( accounts == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, accounts);
		}
		if ( sortKey == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, sortKey);
		}
		if ( title == null ) {
			stmt.setNull(6, -7);
		}
		else {
			stmt.setBoolean(6, title);
		}
		if ( internal_calculation == null ) {
			stmt.setNull(7, -7);
		}
		else {
			stmt.setBoolean(7, internal_calculation);
		}
		if ( visible == null ) {
			stmt.setNull(8, -7);
		}
		else {
			stmt.setBoolean(8, visible);
		}
		if ( zeroFlag == null ) {
			stmt.setNull(9, -7);
		}
		else {
			stmt.setBoolean(9, zeroFlag);
		}
		if ( creditNature == null ) {
			stmt.setNull(10, -7);
		}
		else {
			stmt.setBoolean(10, creditNature);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> balance_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Balance_detail
	 * @param id Identificador unico
	 * @param balance Identificador del Balance
	 * @param code Codigo del Detalle en el Balance
	 * @param description Descripción del detalle de balance
	 * @param accounts Cuentas separadas por comas, que forman el acumulado.
	 * @param sortKey Orden el que aparecera en el listado.
	 * @param title 
	 * @param internal_calculation Indica si es un calculo interno, es decir si el contenido
                de accounts son referencias a la columna -code- de esta tabla
	 * @param visible Si aparece o no en la impresion.
	 * @param zeroFlag Flag que se activa cuando la cuenta o cuentas tienen valor 0.
	 * @param creditNature Si es verdadero se hace una haber menos debe de las cuentas indicadas
	 * @throws SQLException
	*/
	protected int insertBalance_detailOldIds( Integer oldId , Integer balance , String code , String description , InputStream accounts , Integer sortKey , Boolean title , Boolean internal_calculation , Boolean visible , Boolean zeroFlag , Boolean creditNature )
	throws SQLException {
		
		Integer newId = this.balance_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxbalance_detailIds.get(null);
		newId = this.insertBalance_detail( balance,  code,  description,  accounts,  sortKey,  title,  internal_calculation,  visible,  zeroFlag,  creditNature );
		balance_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Bank
	 * @param name Nombre de la Entidad Bancaria
	 * @param code Codigo de la Entidad Bancaria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBank( String name,  String code )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO bank ( name, code) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO bank ( name, code) VALUES (  '"+ name +"', '"+ code +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( code == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, code);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> bankIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Bank
	 * @param id Identificador unico de la Entidad Bancaria
	 * @param name Nombre de la Entidad Bancaria
	 * @param code Codigo de la Entidad Bancaria
	 * @throws SQLException
	*/
	protected int insertBankOldIds( Integer oldId , String name , String code )
	throws SQLException {
		
		Integer newId = this.bankIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertBank( name,  code );
		bankIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Bonus
	 * @param description Descripcion
	 * @param formula FÃ³rmula
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBonus( String description,  String formula )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO bonus ( description, formula) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO bonus ( description, formula) VALUES (  '"+ description +"', '"+ formula +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( formula == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, formula);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> bonusIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Bonus
	 * @param id Identificador unico
	 * @param description Descripcion
	 * @param formula FÃ³rmula
	 * @throws SQLException
	*/
	protected int insertBonusOldIds( Integer oldId , String description , String formula )
	throws SQLException {
		
		Integer newId = this.bonusIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertBonus( description,  formula );
		bonusIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Brand
	 * @param name Nombre de la Marca Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertBrand( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO brand ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO brand ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> brandIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Brand
	 * @param id Identificador unico de la Marca Comercial
	 * @param name Nombre de la Marca Comercial
	 * @throws SQLException
	*/
	protected int insertBrandOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.brandIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertBrand( name );
		brandIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCalendar( Integer holiday,  Short source,  Integer source_id,  Double anual_hours,  String description,  InputStream comments,  Short monday,  Double monday_hours,  Short tuesday,  Double tuesday_hours,  Short wednesday,  Double wednesday_hours,  Short thursday,  Double thursday_hours,  Short friday,  Double friday_hours,  Short saturday,  Double saturday_hours,  Short sunday,  Double sunday_hours )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar ( holiday, source, source_id, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO calendar ( holiday, source, source_id, anual_hours, description, comments, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  '"+ holiday +"', '"+ source +"', '"+ source_id +"', '"+ anual_hours +"', '"+ description +"', '"+ comments +"', '"+ monday +"', '"+ monday_hours +"', '"+ tuesday +"', '"+ tuesday_hours +"', '"+ wednesday +"', '"+ wednesday_hours +"', '"+ thursday +"', '"+ thursday_hours +"', '"+ friday +"', '"+ friday_hours +"', '"+ saturday +"', '"+ saturday_hours +"', '"+ sunday +"', '"+ sunday_hours +"')";
		LOGGER.debug(message);

		if ( holiday == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, holiday);
		}
		if ( source == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, source);
		}
		if ( source_id == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, source_id);
		}
		if ( anual_hours == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, anual_hours);
		}
		if ( description == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, description);
		}
		if ( comments == null ) {
			stmt.setNull(6, -1);
		}
		else {
			stmt.setAsciiStream(6, comments);
		}
		if ( monday == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, monday);
		}
		if ( monday_hours == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, monday_hours);
		}
		if ( tuesday == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, tuesday);
		}
		if ( tuesday_hours == null ) {
			stmt.setNull(10, 8);
		}
		else {
			stmt.setDouble(10, tuesday_hours);
		}
		if ( wednesday == null ) {
			stmt.setNull(11, -6);
		}
		else {
			stmt.setShort(11, wednesday);
		}
		if ( wednesday_hours == null ) {
			stmt.setNull(12, 8);
		}
		else {
			stmt.setDouble(12, wednesday_hours);
		}
		if ( thursday == null ) {
			stmt.setNull(13, -6);
		}
		else {
			stmt.setShort(13, thursday);
		}
		if ( thursday_hours == null ) {
			stmt.setNull(14, 8);
		}
		else {
			stmt.setDouble(14, thursday_hours);
		}
		if ( friday == null ) {
			stmt.setNull(15, -6);
		}
		else {
			stmt.setShort(15, friday);
		}
		if ( friday_hours == null ) {
			stmt.setNull(16, 8);
		}
		else {
			stmt.setDouble(16, friday_hours);
		}
		if ( saturday == null ) {
			stmt.setNull(17, -6);
		}
		else {
			stmt.setShort(17, saturday);
		}
		if ( saturday_hours == null ) {
			stmt.setNull(18, 8);
		}
		else {
			stmt.setDouble(18, saturday_hours);
		}
		if ( sunday == null ) {
			stmt.setNull(19, -6);
		}
		else {
			stmt.setShort(19, sunday);
		}
		if ( sunday_hours == null ) {
			stmt.setNull(20, 8);
		}
		else {
			stmt.setDouble(20, sunday_hours);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> calendarIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Calendar
	 * @param id Identificador unico
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
	 * @throws SQLException
	*/
	protected int insertCalendarOldIds( Integer oldId , Integer holiday , Short source , Integer source_id , Double anual_hours , String description , InputStream comments , Short monday , Double monday_hours , Short tuesday , Double tuesday_hours , Short wednesday , Double wednesday_hours , Short thursday , Double thursday_hours , Short friday , Double friday_hours , Short saturday , Double saturday_hours , Short sunday , Double sunday_hours )
	throws SQLException {
		
		Integer newId = this.calendarIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcalendarIds.get(null);
		newId = this.insertCalendar( holiday,  source,  source_id,  anual_hours,  description,  comments,  monday,  monday_hours,  tuesday,  tuesday_hours,  wednesday,  wednesday_hours,  thursday,  thursday_hours,  friday,  friday_hours,  saturday,  saturday_hours,  sunday,  sunday_hours );
		calendarIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Calendar_holiday
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Festivo
	 * @param date Fecha del festivo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCalendar_holiday( Integer calendar,  String description,  Date date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar_holiday ( calendar, description, date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO calendar_holiday ( calendar, description, date) VALUES (  '"+ calendar +"', '"+ description +"', '"+ date +"')";
		LOGGER.debug(message);

		if ( calendar == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, calendar);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> calendar_holidayIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Calendar_holiday
	 * @param id Identificador unico
	 * @param calendar Identificador del Calendario
	 * @param description Descripcion del Festivo
	 * @param date Fecha del festivo
	 * @throws SQLException
	*/
	protected int insertCalendar_holidayOldIds( Integer oldId , Integer calendar , String description , Date date )
	throws SQLException {
		
		Integer newId = this.calendar_holidayIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcalendar_holidayIds.get(null);
		newId = this.insertCalendar_holiday( calendar,  description,  date );
		calendar_holidayIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCalendar_period( Integer calendar,  String description,  Short month,  Short start_day,  Short end_day,  Short monday,  Double monday_hours,  Short tuesday,  Double tuesday_hours,  Short wednesday,  Double wednesday_hours,  Short thursday,  Double thursday_hours,  Short friday,  Double friday_hours,  Short saturday,  Double saturday_hours,  Short sunday,  Double sunday_hours )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO calendar_period ( calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO calendar_period ( calendar, description, month, start_day, end_day, monday, monday_hours, tuesday, tuesday_hours, wednesday, wednesday_hours, thursday, thursday_hours, friday, friday_hours, saturday, saturday_hours, sunday, sunday_hours) VALUES (  '"+ calendar +"', '"+ description +"', '"+ month +"', '"+ start_day +"', '"+ end_day +"', '"+ monday +"', '"+ monday_hours +"', '"+ tuesday +"', '"+ tuesday_hours +"', '"+ wednesday +"', '"+ wednesday_hours +"', '"+ thursday +"', '"+ thursday_hours +"', '"+ friday +"', '"+ friday_hours +"', '"+ saturday +"', '"+ saturday_hours +"', '"+ sunday +"', '"+ sunday_hours +"')";
		LOGGER.debug(message);

		if ( calendar == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, calendar);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( month == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, month);
		}
		if ( start_day == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, start_day);
		}
		if ( end_day == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, end_day);
		}
		if ( monday == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, monday);
		}
		if ( monday_hours == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, monday_hours);
		}
		if ( tuesday == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, tuesday);
		}
		if ( tuesday_hours == null ) {
			stmt.setNull(9, 8);
		}
		else {
			stmt.setDouble(9, tuesday_hours);
		}
		if ( wednesday == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, wednesday);
		}
		if ( wednesday_hours == null ) {
			stmt.setNull(11, 8);
		}
		else {
			stmt.setDouble(11, wednesday_hours);
		}
		if ( thursday == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, thursday);
		}
		if ( thursday_hours == null ) {
			stmt.setNull(13, 8);
		}
		else {
			stmt.setDouble(13, thursday_hours);
		}
		if ( friday == null ) {
			stmt.setNull(14, -6);
		}
		else {
			stmt.setShort(14, friday);
		}
		if ( friday_hours == null ) {
			stmt.setNull(15, 8);
		}
		else {
			stmt.setDouble(15, friday_hours);
		}
		if ( saturday == null ) {
			stmt.setNull(16, -6);
		}
		else {
			stmt.setShort(16, saturday);
		}
		if ( saturday_hours == null ) {
			stmt.setNull(17, 8);
		}
		else {
			stmt.setDouble(17, saturday_hours);
		}
		if ( sunday == null ) {
			stmt.setNull(18, -6);
		}
		else {
			stmt.setShort(18, sunday);
		}
		if ( sunday_hours == null ) {
			stmt.setNull(19, 8);
		}
		else {
			stmt.setDouble(19, sunday_hours);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> calendar_periodIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertCalendar_periodOldIds( Integer oldId , Integer calendar , String description , Short month , Short start_day , Short end_day , Short monday , Double monday_hours , Short tuesday , Double tuesday_hours , Short wednesday , Double wednesday_hours , Short thursday , Double thursday_hours , Short friday , Double friday_hours , Short saturday , Double saturday_hours , Short sunday , Double sunday_hours )
	throws SQLException {
		
		Integer newId = this.calendar_periodIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcalendar_periodIds.get(null);
		newId = this.insertCalendar_period( calendar,  description,  month,  start_day,  end_day,  monday,  monday_hours,  tuesday,  tuesday_hours,  wednesday,  wednesday_hours,  thursday,  thursday_hours,  friday,  friday_hours,  saturday,  saturday_hours,  sunday,  sunday_hours );
		calendar_periodIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Campaign
	 * @param description Descripcion de la Campaña
	 * @param process Identificador del Proceso
	 * @param activity_type Identificador del Tipo de Actividad
	 * @param start_date Fecha de inicio de la Campaña
	 * @param end_date Fecha de finalizacion de la Campaña
	 * @param workgroup Grupo de Trabajo supervisor de la Campaña
	 * @param type Tipo de Campaña
	 * @param status Estado de la Campaña
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign( String description,  Integer process,  Integer activity_type,  Date start_date,  Date end_date,  Integer workgroup,  Short type,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO campaign ( description, process, activity_type, start_date, end_date, workgroup, type, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO campaign ( description, process, activity_type, start_date, end_date, workgroup, type, status) VALUES (  '"+ description +"', '"+ process +"', '"+ activity_type +"', '"+ start_date +"', '"+ end_date +"', '"+ workgroup +"', '"+ type +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( process == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, process);
		}
		if ( activity_type == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, activity_type);
		}
		if ( start_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, end_date);
		}
		if ( workgroup == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, workgroup);
		}
		if ( type == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, type);
		}
		if ( status == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> campaignIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Campaign
	 * @param id Identificador unico de la Campaña
	 * @param description Descripcion de la Campaña
	 * @param process Identificador del Proceso
	 * @param activity_type Identificador del Tipo de Actividad
	 * @param start_date Fecha de inicio de la Campaña
	 * @param end_date Fecha de finalizacion de la Campaña
	 * @param workgroup Grupo de Trabajo supervisor de la Campaña
	 * @param type Tipo de Campaña
	 * @param status Estado de la Campaña
	 * @throws SQLException
	*/
	protected int insertCampaignOldIds( Integer oldId , String description , Integer process , Integer activity_type , Date start_date , Date end_date , Integer workgroup , Short type , Short status )
	throws SQLException {
		
		Integer newId = this.campaignIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcampaignIds.get(null);
		newId = this.insertCampaign( description,  process,  activity_type,  start_date,  end_date,  workgroup,  type,  status );
		campaignIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Campaign_dossier
	 * @param campaign Identificador de la Campaña
	 * @param dossier Identificador del Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCampaign_dossier( Integer campaign,  Integer dossier )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO campaign_dossier ( campaign, dossier) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO campaign_dossier ( campaign, dossier) VALUES (  '"+ campaign +"', '"+ dossier +"')";
		LOGGER.debug(message);

		if ( campaign == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, campaign);
		}
		if ( dossier == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, dossier);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> campaign_dossierIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Campaign_dossier
	 * @param id Identificador unico de la Relacion de Campañas y Expedientes
	 * @param campaign Identificador de la Campaña
	 * @param dossier Identificador del Expediente
	 * @throws SQLException
	*/
	protected int insertCampaign_dossierOldIds( Integer oldId , Integer campaign , Integer dossier )
	throws SQLException {
		
		Integer newId = this.campaign_dossierIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcampaign_dossierIds.get(null);
		newId = this.insertCampaign_dossier( campaign,  dossier );
		campaign_dossierIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Catalogue
	 * @param name Nombre del Catalogo
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCatalogue( String name,  Date start_date,  Date end_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue ( name, start_date, end_date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO catalogue ( name, start_date, end_date) VALUES (  '"+ name +"', '"+ start_date +"', '"+ end_date +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( start_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, end_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> catalogueIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Catalogue
	 * @param id Identificador unico
	 * @param name Nombre del Catalogo
	 * @param start_date Fecha de inicio del Catalogo
	 * @param end_date Fecha de fin del Catalogo
	 * @throws SQLException
	*/
	protected int insertCatalogueOldIds( Integer oldId , String name , Date start_date , Date end_date )
	throws SQLException {
		
		Integer newId = this.catalogueIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCatalogue( name,  start_date,  end_date );
		catalogueIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCatalogue_category( Integer catalogue,  Integer category,  Double quantity,  Double discount )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue_category ( catalogue, category, quantity, discount) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO catalogue_category ( catalogue, category, quantity, discount) VALUES (  '"+ catalogue +"', '"+ category +"', '"+ quantity +"', '"+ discount +"')";
		LOGGER.debug(message);

		if ( catalogue == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, catalogue);
		}
		if ( category == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, category);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( discount == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, discount);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> catalogue_categoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Catalogue_category
	 * @param id Identificador unico
	 * @param catalogue Identificador del Catalogo
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica el descuento
	 * @param discount Descuento de la Categoria en el Catalogo
	 * @throws SQLException
	*/
	protected int insertCatalogue_categoryOldIds( Integer oldId , Integer catalogue , Integer category , Double quantity , Double discount )
	throws SQLException {
		
		Integer newId = this.catalogue_categoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcatalogue_categoryIds.get(null);
		newId = this.insertCatalogue_category( catalogue,  category,  quantity,  discount );
		catalogue_categoryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCatalogue_item( Integer catalogue,  Integer item,  Double quantity,  Double price,  Double discount )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO catalogue_item ( catalogue, item, quantity, price, discount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO catalogue_item ( catalogue, item, quantity, price, discount) VALUES (  '"+ catalogue +"', '"+ item +"', '"+ quantity +"', '"+ price +"', '"+ discount +"')";
		LOGGER.debug(message);

		if ( catalogue == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, catalogue);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, price);
		}
		if ( discount == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, discount);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> catalogue_itemIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Catalogue_item
	 * @param id Identificador unico
	 * @param catalogue Identificador del Catalogo
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica el precio o descuento
	 * @param price Precio del Articulo en el Catalogo
	 * @param discount Descuento del Articulo en el Catalogo
	 * @throws SQLException
	*/
	protected int insertCatalogue_itemOldIds( Integer oldId , Integer catalogue , Integer item , Double quantity , Double price , Double discount )
	throws SQLException {
		
		Integer newId = this.catalogue_itemIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcatalogue_itemIds.get(null);
		newId = this.insertCatalogue_item( catalogue,  item,  quantity,  price,  discount );
		catalogue_itemIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Category
	 * @param name Nombre de la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCategory( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO category ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO category ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> categoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Category
	 * @param id Identificador unico de la Categoria
	 * @param name Nombre de la Categoria
	 * @throws SQLException
	*/
	protected int insertCategoryOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.categoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCategory( name );
		categoryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cnae
	 * @param code Codigo del CNAE
	 * @param title Titulo del CNAE
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCnae( String code,  String title )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cnae ( code, title) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cnae ( code, title) VALUES (  '"+ code +"', '"+ title +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, code);
		}
		if ( title == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, title);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cnaeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cnae
	 * @param id Identificador unico
	 * @param code Codigo del CNAE
	 * @param title Titulo del CNAE
	 * @throws SQLException
	*/
	protected int insertCnaeOldIds( Integer oldId , String code , String title )
	throws SQLException {
		
		Integer newId = this.cnaeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCnae( code,  title );
		cnaeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Commercial_activity
	 * @param name Nombre de la Actividad Comercial
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_activity( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_activity ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commercial_activity ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commercial_activityIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commercial_activity
	 * @param id Identificador unico
	 * @param name Nombre de la Actividad Comercial
	 * @throws SQLException
	*/
	protected int insertCommercial_activityOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.commercial_activityIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCommercial_activity( name );
		commercial_activityIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Commercial_term
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condición es particular o general
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommercial_term( Integer line,  String name,  InputStream description,  Boolean term_general )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_term ( line, name, description, term_general) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commercial_term ( line, name, description, term_general) VALUES (  '"+ line +"', '"+ name +"', '"+ description +"', '"+ term_general +"')";
		LOGGER.debug(message);

		if ( line == null ) {
			stmt.setNull(1, 5);
		}
		else {
			stmt.setInt(1, line);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}
		if ( description == null ) {
			stmt.setNull(3, -1);
		}
		else {
			stmt.setAsciiStream(3, description);
		}
		if ( term_general == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, term_general);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commercial_termIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commercial_term
	 * @param id Identificador unico
	 * @param line Numero de linea de Condicion
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condición es particular o general
	 * @throws SQLException
	*/
	protected int insertCommercial_termOldIds( Integer oldId , Integer line , String name , InputStream description , Boolean term_general )
	throws SQLException {
		
		Integer newId = this.commercial_termIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCommercial_term( line,  name,  description,  term_general );
		commercial_termIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCommercial_tracking( Date date,  Integer seller,  Integer target,  Integer activity,  String comments,  Short status,  Integer next_commercial_tracking,  Date end_date,  Integer offer )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commercial_tracking ( date, seller, target, activity, comments, status, next_commercial_tracking, end_date, offer) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commercial_tracking ( date, seller, target, activity, comments, status, next_commercial_tracking, end_date, offer) VALUES (  '"+ date +"', '"+ seller +"', '"+ target +"', '"+ activity +"', '"+ comments +"', '"+ status +"', '"+ next_commercial_tracking +"', '"+ end_date +"', '"+ offer +"')";
		LOGGER.debug(message);

		if ( date == null ) {
			stmt.setNull(1, 91);
		}
		else {
			stmt.setDate(1, date);
		}
		if ( seller == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, seller);
		}
		if ( target == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, target);
		}
		if ( activity == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, activity);
		}
		if ( comments == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, comments);
		}
		if ( status == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, status);
		}
		if ( next_commercial_tracking == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, next_commercial_tracking);
		}
		if ( end_date == null ) {
			stmt.setNull(8, 91);
		}
		else {
			stmt.setDate(8, end_date);
		}
		if ( offer == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, offer);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commercial_trackingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commercial_tracking
	 * @param id Identificador unico
	 * @param date Fecha del Seguimiento Comercial
	 * @param seller Identificador del Comercial
	 * @param target Identificador del Cliente Potencial
	 * @param activity Identificador de la Actividad Comercial
	 * @param comments Comentarios del Seguimiento Comercial
	 * @param status Estado del Seguimiento Comercial
	 * @param next_commercial_tracking Identificador del siguiente Seguimiento Comercial
	 * @param end_date Fecha de cierre del Seguimiento Comercial
	 * @param offer Identificador del Presupuesto
	 * @throws SQLException
	*/
	protected int insertCommercial_trackingOldIds( Integer oldId , Date date , Integer seller , Integer target , Integer activity , String comments , Short status , Integer next_commercial_tracking , Date end_date , Integer offer )
	throws SQLException {
		
		Integer newId = this.commercial_trackingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcommercial_trackingIds.get(null);
		newId = this.insertCommercial_tracking( date,  seller,  target,  activity,  comments,  status,  next_commercial_tracking,  end_date,  offer );
		commercial_trackingIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Commission
	 * @param name Descripcion de la Comision
	 * @param start_date Fecha de inicio de la Comision
	 * @param end_date Fecha de fin de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission( String name,  Date start_date,  Date end_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission ( name, start_date, end_date) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commission ( name, start_date, end_date) VALUES (  '"+ name +"', '"+ start_date +"', '"+ end_date +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( start_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, end_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commissionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commission
	 * @param id Identificador unico
	 * @param name Descripcion de la Comision
	 * @param start_date Fecha de inicio de la Comision
	 * @param end_date Fecha de fin de la Comision
	 * @throws SQLException
	*/
	protected int insertCommissionOldIds( Integer oldId , String name , Date start_date , Date end_date )
	throws SQLException {
		
		Integer newId = this.commissionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCommission( name,  start_date,  end_date );
		commissionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCommission_category( Integer commission,  Integer category,  Double quantity,  Double rate )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_category ( commission, category, quantity, rate) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commission_category ( commission, category, quantity, rate) VALUES (  '"+ commission +"', '"+ category +"', '"+ quantity +"', '"+ rate +"')";
		LOGGER.debug(message);

		if ( commission == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, commission);
		}
		if ( category == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, category);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( rate == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, rate);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commission_categoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commission_category
	 * @param id Identificador unico
	 * @param commission Identificador de la Comision
	 * @param category Identificador de la Categoria
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param rate Porcentaje de Comision
	 * @throws SQLException
	*/
	protected int insertCommission_categoryOldIds( Integer oldId , Integer commission , Integer category , Double quantity , Double rate )
	throws SQLException {
		
		Integer newId = this.commission_categoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcommission_categoryIds.get(null);
		newId = this.insertCommission_category( commission,  category,  quantity,  rate );
		commission_categoryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCommission_item( Integer commission,  Integer item,  Double quantity,  Double amount,  Double rate )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_item ( commission, item, quantity, amount, rate) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commission_item ( commission, item, quantity, amount, rate) VALUES (  '"+ commission +"', '"+ item +"', '"+ quantity +"', '"+ amount +"', '"+ rate +"')";
		LOGGER.debug(message);

		if ( commission == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, commission);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( amount == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, amount);
		}
		if ( rate == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, rate);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commission_itemIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commission_item
	 * @param id Identificador unico
	 * @param commission Identificador de la Comision
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad a partir de la cual se aplica la Comision
	 * @param amount Importe de la Comision
	 * @param rate Porcentaje de Comision
	 * @throws SQLException
	*/
	protected int insertCommission_itemOldIds( Integer oldId , Integer commission , Integer item , Double quantity , Double amount , Double rate )
	throws SQLException {
		
		Integer newId = this.commission_itemIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcommission_itemIds.get(null);
		newId = this.insertCommission_item( commission,  item,  quantity,  amount,  rate );
		commission_itemIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Commission_type
	 * @param name Descripcion del Tipo de Comision
	 * @param rate Porcentaje de Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_type( String name,  Double rate )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_type ( name, rate) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commission_type ( name, rate) VALUES (  '"+ name +"', '"+ rate +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( rate == null ) {
			stmt.setNull(2, 8);
		}
		else {
			stmt.setDouble(2, rate);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commission_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commission_type
	 * @param id Identificador unico
	 * @param name Descripcion del Tipo de Comision
	 * @param rate Porcentaje de Comision
	 * @throws SQLException
	*/
	protected int insertCommission_typeOldIds( Integer oldId , String name , Double rate )
	throws SQLException {
		
		Integer newId = this.commission_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCommission_type( name,  rate );
		commission_typeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Commission_type_commission
	 * @param commission_type Identificador del Tipo de Comision
	 * @param commission Identificador de la Comision
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCommission_type_commission( Integer commission_type,  Integer commission )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO commission_type_commission ( commission_type, commission) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO commission_type_commission ( commission_type, commission) VALUES (  '"+ commission_type +"', '"+ commission +"')";
		LOGGER.debug(message);

		if ( commission_type == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, commission_type);
		}
		if ( commission == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, commission);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> commission_type_commissionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Commission_type_commission
	 * @param id Identificador unico
	 * @param commission_type Identificador del Tipo de Comision
	 * @param commission Identificador de la Comision
	 * @throws SQLException
	*/
	protected int insertCommission_type_commissionOldIds( Integer oldId , Integer commission_type , Integer commission )
	throws SQLException {
		
		Integer newId = this.commission_type_commissionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcommission_type_commissionIds.get(null);
		newId = this.insertCommission_type_commission( commission_type,  commission );
		commission_type_commissionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Company
	 * @param registry Registro de la Compañia
	 * @param active Indica si la Compañia es activa o inactiva
	 * @param surcharge Indica si la Compañia tiene de recargo de equivalencia
	 * @param withholding Indica si la Compañia aplica retencion de impuestos
	 * @param e_invoice Indica si la Compañia desea emitir Facturas electronicas
	 * @throws SQLException
	*/
	protected void insertCompany( Integer registry,  Boolean active,  Boolean surcharge,  Boolean withholding,  Boolean e_invoice )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO company ( registry, active, surcharge, withholding, e_invoice) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO company ( registry, active, surcharge, withholding, e_invoice) VALUES ( '"+ registry +"', '"+ active +"', '"+ surcharge +"', '"+ withholding +"', '"+ e_invoice +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( active == null ) {
			stmt.setNull(2, -7);
		}
		else {
			stmt.setBoolean(2, active);
		}
		if ( surcharge == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, surcharge);
		}
		if ( withholding == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, withholding);
		}
		if ( e_invoice == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, e_invoice);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
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
	protected int insertComposition( Short type,  String description,  Integer item,  Double quantity,  Double price,  Double expenses_percent,  Double expenses_fixed,  Boolean price_in_details )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition ( type, description, item, quantity, price, expenses_percent, expenses_fixed, price_in_details) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO composition ( type, description, item, quantity, price, expenses_percent, expenses_fixed, price_in_details) VALUES (  '"+ type +"', '"+ description +"', '"+ item +"', '"+ quantity +"', '"+ price +"', '"+ expenses_percent +"', '"+ expenses_fixed +"', '"+ price_in_details +"')";
		LOGGER.debug(message);

		if ( type == null ) {
			stmt.setNull(1, -6);
		}
		else {
			stmt.setShort(1, type);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( quantity == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, quantity);
		}
		if ( price == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, price);
		}
		if ( expenses_percent == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, expenses_percent);
		}
		if ( expenses_fixed == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, expenses_fixed);
		}
		if ( price_in_details == null ) {
			stmt.setNull(8, -7);
		}
		else {
			stmt.setBoolean(8, price_in_details);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> compositionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Composition
	 * @param id Identificador unico de la Composicion
	 * @param type Tipo de Composicion
	 * @param description Descripcion de la Composicion
	 * @param item Identificador del Articulo a componer
	 * @param quantity Cantidad de Articulo a componer
	 * @param price Precio de la Composicion
	 * @param expenses_percent Gastos porcentuales de la Composicion
	 * @param expenses_fixed Gastos fijos de la Composicion
	 * @param price_in_details Indica si el precio lo forman la suma de los detalles de la Composicion
	 * @throws SQLException
	*/
	protected int insertCompositionOldIds( Integer oldId , Short type , String description , Integer item , Double quantity , Double price , Double expenses_percent , Double expenses_fixed , Boolean price_in_details )
	throws SQLException {
		
		Integer newId = this.compositionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcompositionIds.get(null);
		newId = this.insertComposition( type,  description,  item,  quantity,  price,  expenses_percent,  expenses_fixed,  price_in_details );
		compositionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertComposition_detail( Integer composition,  Integer item,  String description,  Double quantity,  Double price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition_detail ( composition, item, description, quantity, price) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO composition_detail ( composition, item, description, quantity, price) VALUES (  '"+ composition +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"')";
		LOGGER.debug(message);

		if ( composition == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, composition);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( quantity == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, quantity);
		}
		if ( price == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> composition_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Composition_detail
	 * @param id Identificador unico del Detalle de la Composicion
	 * @param composition Identificador de la Composicion
	 * @param item Identificador del Articulo subproducto
	 * @param description Descripcion del Articulo subproducto
	 * @param quantity Cantidad de Articulo subproducto
	 * @param price Precio del Articulo subproducto
	 * @throws SQLException
	*/
	protected int insertComposition_detailOldIds( Integer oldId , Integer composition , Integer item , String description , Double quantity , Double price )
	throws SQLException {
		
		Integer newId = this.composition_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcomposition_detailIds.get(null);
		newId = this.insertComposition_detail( composition,  item,  description,  quantity,  price );
		composition_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertComposition_expense( Integer composition,  String description,  Double quantity,  Double price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO composition_expense ( composition, description, quantity, price) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO composition_expense ( composition, description, quantity, price) VALUES (  '"+ composition +"', '"+ description +"', '"+ quantity +"', '"+ price +"')";
		LOGGER.debug(message);

		if ( composition == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, composition);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> composition_expenseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Composition_expense
	 * @param id Identificador unico del Gasto de la Composicion
	 * @param composition Identificador de la Composicion
	 * @param description Descripcion del Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Importe del Gasto
	 * @throws SQLException
	*/
	protected int insertComposition_expenseOldIds( Integer oldId , Integer composition , String description , Double quantity , Double price )
	throws SQLException {
		
		Integer newId = this.composition_expenseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcomposition_expenseIds.get(null);
		newId = this.insertComposition_expense( composition,  description,  quantity,  price );
		composition_expenseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertContact( Integer user,  String name,  String organization,  String phone,  String cellular_phone,  String fax,  String email,  String address,  InputStream note )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contact ( user, name, organization, phone, cellular_phone, fax, email, address, note) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO contact ( user, name, organization, phone, cellular_phone, fax, email, address, note) VALUES (  '"+ user +"', '"+ name +"', '"+ organization +"', '"+ phone +"', '"+ cellular_phone +"', '"+ fax +"', '"+ email +"', '"+ address +"', '"+ note +"')";
		LOGGER.debug(message);

		if ( user == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, user);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}
		if ( organization == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, organization);
		}
		if ( phone == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, phone);
		}
		if ( cellular_phone == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, cellular_phone);
		}
		if ( fax == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, fax);
		}
		if ( email == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, email);
		}
		if ( address == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, address);
		}
		if ( note == null ) {
			stmt.setNull(9, -1);
		}
		else {
			stmt.setAsciiStream(9, note);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> contactIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Contact
	 * @param id Identificador unico del Contacto
	 * @param user Usuario al que pertenece el Contacto
	 * @param name Nombre del Contacto
	 * @param organization Organizacion a la que pertenece el Contacto
	 * @param phone Telefono del Contacto
	 * @param cellular_phone Telefono movil del Contacto
	 * @param fax Fax del Contacto
	 * @param email Correo electronico del Contacto
	 * @param address Direccion del Contacto
	 * @param note Notas sobre el Contacto
	 * @throws SQLException
	*/
	protected int insertContactOldIds( Integer oldId , Integer user , String name , String organization , String phone , String cellular_phone , String fax , String email , String address , InputStream note )
	throws SQLException {
		
		Integer newId = this.contactIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcontactIds.get(null);
		newId = this.insertContact( user,  name,  organization,  phone,  cellular_phone,  fax,  email,  address,  note );
		contactIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Contract
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @param ccc Identificador de la Cuota de Cotizacion
	 * @param start_date Fecha de inicio del Contrato
	 * @param end_date Fecha de finalizacion del Contrato
	 * @param contract_type Tipo de contrato
	 * @param document Impreso (.pdf) del comtrato.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract( Integer person,  Integer workplace,  Integer ccc,  Date start_date,  Date end_date,  Integer contract_type,  InputStream document )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract ( person, workplace, ccc, start_date, end_date, contract_type, document) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO contract ( person, workplace, ccc, start_date, end_date, contract_type, document) VALUES (  '"+ person +"', '"+ workplace +"', '"+ ccc +"', '"+ start_date +"', '"+ end_date +"', '"+ contract_type +"', '"+ document +"')";
		LOGGER.debug(message);

		if ( person == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, person);
		}
		if ( workplace == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, workplace);
		}
		if ( ccc == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, ccc);
		}
		if ( start_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, end_date);
		}
		if ( contract_type == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, contract_type);
		}
		if ( document == null ) {
			stmt.setNull(7, -4);
		}
		else {
			stmt.setBinaryStream(7, document);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> contractIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Contract
	 * @param id Identificador unico
	 * @param person Identificador de la Persona
	 * @param workplace Identificador del Centro de Trabajo
	 * @param ccc Identificador de la Cuota de Cotizacion
	 * @param start_date Fecha de inicio del Contrato
	 * @param end_date Fecha de finalizacion del Contrato
	 * @param contract_type Tipo de contrato
	 * @param document Impreso (.pdf) del comtrato.
	 * @throws SQLException
	*/
	protected int insertContractOldIds( Integer oldId , Integer person , Integer workplace , Integer ccc , Date start_date , Date end_date , Integer contract_type , InputStream document )
	throws SQLException {
		
		Integer newId = this.contractIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcontractIds.get(null);
		newId = this.insertContract( person,  workplace,  ccc,  start_date,  end_date,  contract_type,  document );
		contractIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Contract_tracking
	 * @param contract Identificador del Contrato
	 * @param date Fecha del Seguimiento
	 * @param type Tipo de excepcion
	 * @param duration Duracion de la excepcion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_tracking( Integer contract,  Date date,  Short type,  Double duration )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_tracking ( contract, date, type, duration) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO contract_tracking ( contract, date, type, duration) VALUES (  '"+ contract +"', '"+ date +"', '"+ type +"', '"+ duration +"')";
		LOGGER.debug(message);

		if ( contract == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, contract);
		}
		if ( date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, date);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}
		if ( duration == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, duration);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> contract_trackingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Contract_tracking
	 * @param id Identificador unico
	 * @param contract Identificador del Contrato
	 * @param date Fecha del Seguimiento
	 * @param type Tipo de excepcion
	 * @param duration Duracion de la excepcion
	 * @throws SQLException
	*/
	protected int insertContract_trackingOldIds( Integer oldId , Integer contract , Date date , Short type , Double duration )
	throws SQLException {
		
		Integer newId = this.contract_trackingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcontract_trackingIds.get(null);
		newId = this.insertContract_tracking( contract,  date,  type,  duration );
		contract_trackingIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Contract_type
	 * @param code CÃ³digo del contrato
	 * @param model Modelo
	 * @param description Descripcion
	 * @param conditions Condiciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertContract_type( Integer code,  String model,  String description,  String conditions )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO contract_type ( code, model, description, conditions) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO contract_type ( code, model, description, conditions) VALUES (  '"+ code +"', '"+ model +"', '"+ description +"', '"+ conditions +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, code);
		}
		if ( model == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, model);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( conditions == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, conditions);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> contract_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Contract_type
	 * @param id Identificador unico
	 * @param code CÃ³digo del contrato
	 * @param model Modelo
	 * @param description Descripcion
	 * @param conditions Condiciones
	 * @throws SQLException
	*/
	protected int insertContract_typeOldIds( Integer oldId , Integer code , String model , String description , String conditions )
	throws SQLException {
		
		Integer newId = this.contract_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertContract_type( code,  model,  description,  conditions );
		contract_typeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course
	 * @param code Alias del Curso
	 * @param description Descripcion del Curso
	 * @param start_date Fecha inicio del Curso
	 * @param end_date Fecha fin del Curso
	 * @param academic_year Año Academico del Curso
	 * @param subject Materia del Curso
	 * @param level Nivel del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @param alumn_limit Limite de Alumnos del Curso
	 * @param status Estado del Curso
	 * @param comments Comentarios sobre el Curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse( String code,  String description,  Date start_date,  Date end_date,  Integer academic_year,  Integer subject,  Integer level,  Integer workplace,  Integer alumn_limit,  Short status,  String comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course ( code, description, start_date, end_date, academic_year, subject, level, workplace, alumn_limit, status, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course ( code, description, start_date, end_date, academic_year, subject, level, workplace, alumn_limit, status, comments) VALUES (  '"+ code +"', '"+ description +"', '"+ start_date +"', '"+ end_date +"', '"+ academic_year +"', '"+ subject +"', '"+ level +"', '"+ workplace +"', '"+ alumn_limit +"', '"+ status +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, code);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( start_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, end_date);
		}
		if ( academic_year == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, academic_year);
		}
		if ( subject == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, subject);
		}
		if ( level == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, level);
		}
		if ( workplace == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, workplace);
		}
		if ( alumn_limit == null ) {
			stmt.setNull(9, 5);
		}
		else {
			stmt.setInt(9, alumn_limit);
		}
		if ( status == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, status);
		}
		if ( comments == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> courseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course
	 * @param id Identificador unico del Curso
	 * @param code Alias del Curso
	 * @param description Descripcion del Curso
	 * @param start_date Fecha inicio del Curso
	 * @param end_date Fecha fin del Curso
	 * @param academic_year Año Academico del Curso
	 * @param subject Materia del Curso
	 * @param level Nivel del Curso
	 * @param workplace Identificador del Centro de Trabajo
	 * @param alumn_limit Limite de Alumnos del Curso
	 * @param status Estado del Curso
	 * @param comments Comentarios sobre el Curso
	 * @throws SQLException
	*/
	protected int insertCourseOldIds( Integer oldId , String code , String description , Date start_date , Date end_date , Integer academic_year , Integer subject , Integer level , Integer workplace , Integer alumn_limit , Short status , String comments )
	throws SQLException {
		
		Integer newId = this.courseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourseIds.get(null);
		newId = this.insertCourse( code,  description,  start_date,  end_date,  academic_year,  subject,  level,  workplace,  alumn_limit,  status,  comments );
		courseIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_academicskill
	 * @param course Curso
	 * @param academic_skill Aptitud Academica
	 * @param weight Peso de la Aptitud para calcular la Nota media
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_academicskill( Integer course,  Integer academic_skill,  Integer weight )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_academicskill ( course, academic_skill, weight) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_academicskill ( course, academic_skill, weight) VALUES (  '"+ course +"', '"+ academic_skill +"', '"+ weight +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( academic_skill == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, academic_skill);
		}
		if ( weight == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, weight);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_academicskillIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_academicskill
	 * @param id Identificador Unico
	 * @param course Curso
	 * @param academic_skill Aptitud Academica
	 * @param weight Peso de la Aptitud para calcular la Nota media
	 * @throws SQLException
	*/
	protected int insertCourse_academicskillOldIds( Integer oldId , Integer course , Integer academic_skill , Integer weight )
	throws SQLException {
		
		Integer newId = this.course_academicskillIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_academicskillIds.get(null);
		newId = this.insertCourse_academicskill( course,  academic_skill,  weight );
		course_academicskillIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_alumn
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @param status Estado del alumno en el curso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_alumn( Integer course,  Integer customer,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_alumn ( course, customer, status) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_alumn ( course, customer, status) VALUES (  '"+ course +"', '"+ customer +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( customer == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, customer);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_alumnIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_alumn
	 * @param id Identificador unico
	 * @param course Identificador del Curso
	 * @param customer Identificador del Alumno
	 * @param status Estado del alumno en el curso
	 * @throws SQLException
	*/
	protected int insertCourse_alumnOldIds( Integer oldId , Integer course , Integer customer , Short status )
	throws SQLException {
		
		Integer newId = this.course_alumnIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_alumnIds.get(null);
		newId = this.insertCourse_alumn( course,  customer,  status );
		course_alumnIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCourse_evaluation( Integer course,  Integer quality_skill,  Double evaluation,  Integer quantity )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_evaluation ( course, quality_skill, evaluation, quantity) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_evaluation ( course, quality_skill, evaluation, quantity) VALUES (  '"+ course +"', '"+ quality_skill +"', '"+ evaluation +"', '"+ quantity +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( quality_skill == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, quality_skill);
		}
		if ( evaluation == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, evaluation);
		}
		if ( quantity == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, quantity);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_evaluationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_evaluation
	 * @param id Identificador unico
	 * @param course Identificador de Curso
	 * @param quality_skill Identificador de Aptitudes Calidad
	 * @param evaluation Evaluaciones
	 * @param quantity Cantidad
	 * @throws SQLException
	*/
	protected int insertCourse_evaluationOldIds( Integer oldId , Integer course , Integer quality_skill , Double evaluation , Integer quantity )
	throws SQLException {
		
		Integer newId = this.course_evaluationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_evaluationIds.get(null);
		newId = this.insertCourse_evaluation( course,  quality_skill,  evaluation,  quantity );
		course_evaluationIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_instructor
	 * @param course Identificador del Curso
	 * @param employee Identificador del Profesor
	 * @param type Tipo de Profesor
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_instructor( Integer course,  Integer employee,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_instructor ( course, employee, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_instructor ( course, employee, type) VALUES (  '"+ course +"', '"+ employee +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( employee == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, employee);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_instructorIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_instructor
	 * @param id Identificador unico
	 * @param course Identificador del Curso
	 * @param employee Identificador del Profesor
	 * @param type Tipo de Profesor
	 * @throws SQLException
	*/
	protected int insertCourse_instructorOldIds( Integer oldId , Integer course , Integer employee , Short type )
	throws SQLException {
		
		Integer newId = this.course_instructorIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_instructorIds.get(null);
		newId = this.insertCourse_instructor( course,  employee,  type );
		course_instructorIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_level
	 * @param description Descripcion del Nivel
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_level( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_level ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_level ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_levelIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_level
	 * @param id Identificador unico del Nivel
	 * @param description Descripcion del Nivel
	 * @throws SQLException
	*/
	protected int insertCourse_levelOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.course_levelIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCourse_level( description );
		course_levelIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_observation
	 * @param course Identificador de Curso
	 * @param observation Observaciones
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_observation( Integer course,  String observation )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_observation ( course, observation) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_observation ( course, observation) VALUES (  '"+ course +"', '"+ observation +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( observation == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, observation);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_observationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_observation
	 * @param id Identificador unico
	 * @param course Identificador de Curso
	 * @param observation Observaciones
	 * @throws SQLException
	*/
	protected int insertCourse_observationOldIds( Integer oldId , Integer course , String observation )
	throws SQLException {
		
		Integer newId = this.course_observationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_observationIds.get(null);
		newId = this.insertCourse_observation( course,  observation );
		course_observationIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCourse_schedule( Integer course,  Short day_of_week,  Time start_time,  Time end_time )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_schedule ( course, day_of_week, start_time, end_time) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_schedule ( course, day_of_week, start_time, end_time) VALUES (  '"+ course +"', '"+ day_of_week +"', '"+ start_time +"', '"+ end_time +"')";
		LOGGER.debug(message);

		if ( course == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, course);
		}
		if ( day_of_week == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, day_of_week);
		}
		if ( start_time == null ) {
			stmt.setNull(3, 92);
		}
		else {
			stmt.setTime(3, start_time);
		}
		if ( end_time == null ) {
			stmt.setNull(4, 92);
		}
		else {
			stmt.setTime(4, end_time);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_scheduleIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_schedule
	 * @param id Identificador unico del Horario
	 * @param course Identificador del Curso
	 * @param day_of_week Dia de la semana
	 * @param start_time Hora de comienzo
	 * @param end_time Hora de fin
	 * @throws SQLException
	*/
	protected int insertCourse_scheduleOldIds( Integer oldId , Integer course , Short day_of_week , Time start_time , Time end_time )
	throws SQLException {
		
		Integer newId = this.course_scheduleIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcourse_scheduleIds.get(null);
		newId = this.insertCourse_schedule( course,  day_of_week,  start_time,  end_time );
		course_scheduleIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Course_subject
	 * @param description Descripcion de la Materia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCourse_subject( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO course_subject ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO course_subject ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> course_subjectIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Course_subject
	 * @param id Identificador unico de la Materia
	 * @param description Descripcion de la Materia
	 * @throws SQLException
	*/
	protected int insertCourse_subjectOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.course_subjectIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCourse_subject( description );
		course_subjectIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Creditor
	 * @param registry Registro del Acreedor
	 * @param withholding Indica si el Acreedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Acreedor
	 * @param status Estado del Acreedor
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertCreditor( Integer registry,  Boolean withholding,  Short transaction,  Short status,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO creditor ( registry, withholding, transaction, status, scope) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO creditor ( registry, withholding, transaction, status, scope) VALUES ( '"+ registry +"', '"+ withholding +"', '"+ transaction +"', '"+ status +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( withholding == null ) {
			stmt.setNull(2, -7);
		}
		else {
			stmt.setBoolean(2, withholding);
		}
		if ( transaction == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, transaction);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}
		if ( scope == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, scope);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Creditor_account
	 * @param creditor Identificador del Acreedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCreditor_account( Integer creditor,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO creditor_account ( creditor, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO creditor_account ( creditor, account) VALUES (  '"+ creditor +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( creditor == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, creditor);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> creditor_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Creditor_account
	 * @param id Identificador unico de la Cuenta Contable del Acreedor
	 * @param creditor Identificador del Acreedor
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertCreditor_accountOldIds( Integer oldId , Integer creditor , String account )
	throws SQLException {
		
		Integer newId = this.creditor_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcreditor_accountIds.get(null);
		newId = this.insertCreditor_account( creditor,  account );
		creditor_accountIds.put(oldId,newId);
		
		return newId;
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
	protected void insertCurriculum( Integer registry,  Date entrydate,  Date birthdate,  String birthplace,  String residenceplace,  Integer geozone,  String city,  String zip,  String address,  String phone,  String driver_licenses,  Date driver_license_date,  Short gender,  Short postcategory )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO curriculum ( registry, entrydate, birthdate, birthplace, residenceplace, geozone, city, zip, address, phone, driver_licenses, driver_license_date, gender, postcategory) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO curriculum ( registry, entrydate, birthdate, birthplace, residenceplace, geozone, city, zip, address, phone, driver_licenses, driver_license_date, gender, postcategory) VALUES ( '"+ registry +"', '"+ entrydate +"', '"+ birthdate +"', '"+ birthplace +"', '"+ residenceplace +"', '"+ geozone +"', '"+ city +"', '"+ zip +"', '"+ address +"', '"+ phone +"', '"+ driver_licenses +"', '"+ driver_license_date +"', '"+ gender +"', '"+ postcategory +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( entrydate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, entrydate);
		}
		if ( birthdate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, birthdate);
		}
		if ( birthplace == null ) {
			stmt.setNull(4, 1);
		}
		else {
			stmt.setString(4, birthplace);
		}
		if ( residenceplace == null ) {
			stmt.setNull(5, 1);
		}
		else {
			stmt.setString(5, residenceplace);
		}
		if ( geozone == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, geozone);
		}
		if ( city == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, city);
		}
		if ( zip == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, zip);
		}
		if ( address == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, address);
		}
		if ( phone == null ) {
			stmt.setNull(10, 12);
		}
		else {
			stmt.setString(10, phone);
		}
		if ( driver_licenses == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, driver_licenses);
		}
		if ( driver_license_date == null ) {
			stmt.setNull(12, 91);
		}
		else {
			stmt.setDate(12, driver_license_date);
		}
		if ( gender == null ) {
			stmt.setNull(13, -6);
		}
		else {
			stmt.setShort(13, gender);
		}
		if ( postcategory == null ) {
			stmt.setNull(14, -6);
		}
		else {
			stmt.setShort(14, postcategory);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
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
	protected void insertCustomer( Integer registry,  Integer tariff,  Boolean taxfree,  Boolean surcharge,  Boolean withholding,  Short transaction,  Short status,  Integer segment,  Integer scope,  Boolean e_invoice,  Boolean delivery_grouped,  Boolean delivery_valuated )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer ( registry, tariff, taxfree, surcharge, withholding, transaction, status, segment, scope, e_invoice, delivery_grouped, delivery_valuated) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO customer ( registry, tariff, taxfree, surcharge, withholding, transaction, status, segment, scope, e_invoice, delivery_grouped, delivery_valuated) VALUES ( '"+ registry +"', '"+ tariff +"', '"+ taxfree +"', '"+ surcharge +"', '"+ withholding +"', '"+ transaction +"', '"+ status +"', '"+ segment +"', '"+ scope +"', '"+ e_invoice +"', '"+ delivery_grouped +"', '"+ delivery_valuated +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( tariff == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, tariff);
		}
		if ( taxfree == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, taxfree);
		}
		if ( surcharge == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, surcharge);
		}
		if ( withholding == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, withholding);
		}
		if ( transaction == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, transaction);
		}
		if ( status == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, status);
		}
		if ( segment == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, segment);
		}
		if ( scope == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, scope);
		}
		if ( e_invoice == null ) {
			stmt.setNull(10, -7);
		}
		else {
			stmt.setBoolean(10, e_invoice);
		}
		if ( delivery_grouped == null ) {
			stmt.setNull(11, -7);
		}
		else {
			stmt.setBoolean(11, delivery_grouped);
		}
		if ( delivery_valuated == null ) {
			stmt.setNull(12, -7);
		}
		else {
			stmt.setBoolean(12, delivery_valuated);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Customer_account
	 * @param customer Identificador del Cliente
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_account( Integer customer,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_account ( customer, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO customer_account ( customer, account) VALUES (  '"+ customer +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( customer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, customer);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> customer_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Customer_account
	 * @param id Identificador unico de la Cuenta Contable del Cliente
	 * @param customer Identificador del Cliente
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertCustomer_accountOldIds( Integer oldId , Integer customer , String account )
	throws SQLException {
		
		Integer newId = this.customer_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcustomer_accountIds.get(null);
		newId = this.insertCustomer_account( customer,  account );
		customer_accountIds.put(oldId,newId);
		
		return newId;
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
	 * @param billing_date Proxima fecha de facturación de la Cuota
	 * @param period Periodo de facturacion en meses de la Cuota
	 * @param security_level Nivel de seguridad de la Cuota
	 * @param workplace Identificador del Centro de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_fee( Integer customer,  Integer line,  Integer item,  String description,  Double quantity,  Double price,  String discount_expr,  Date initial_date,  Date final_date,  Date billing_date,  Integer period,  Short security_level,  Integer workplace )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_fee ( customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO customer_fee ( customer, line, item, description, quantity, price, discount_expr, initial_date, final_date, billing_date, period, security_level, workplace) VALUES (  '"+ customer +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ initial_date +"', '"+ final_date +"', '"+ billing_date +"', '"+ period +"', '"+ security_level +"', '"+ workplace +"')";
		LOGGER.debug(message);

		if ( customer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, customer);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, discount_expr);
		}
		if ( initial_date == null ) {
			stmt.setNull(8, 91);
		}
		else {
			stmt.setDate(8, initial_date);
		}
		if ( final_date == null ) {
			stmt.setNull(9, 91);
		}
		else {
			stmt.setDate(9, final_date);
		}
		if ( billing_date == null ) {
			stmt.setNull(10, 91);
		}
		else {
			stmt.setDate(10, billing_date);
		}
		if ( period == null ) {
			stmt.setNull(11, 5);
		}
		else {
			stmt.setInt(11, period);
		}
		if ( security_level == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, security_level);
		}
		if ( workplace == null ) {
			stmt.setNull(13, 4);
		}
		else {
			stmt.setInt(13, workplace);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> customer_feeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Customer_fee
	 * @param id Identificador unico de la Cuota del Cliente
	 * @param customer Identificador del Cliente
	 * @param line Numero de linea de Cuota
	 * @param item Identificador del Articulo
	 * @param description Descripcion de la Cuota
	 * @param quantity Cantidad de la Cuota
	 * @param price Precio de la Cuota
	 * @param discount_expr Descuentos de la Cuota
	 * @param initial_date Fecha de inicio de la Cuota
	 * @param final_date Fecha de finalizacion de la Cuota
	 * @param billing_date Proxima fecha de facturación de la Cuota
	 * @param period Periodo de facturacion en meses de la Cuota
	 * @param security_level Nivel de seguridad de la Cuota
	 * @param workplace Identificador del Centro de Trabajo
	 * @throws SQLException
	*/
	protected int insertCustomer_feeOldIds( Integer oldId , Integer customer , Integer line , Integer item , String description , Double quantity , Double price , String discount_expr , Date initial_date , Date final_date , Date billing_date , Integer period , Short security_level , Integer workplace )
	throws SQLException {
		
		Integer newId = this.customer_feeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcustomer_feeIds.get(null);
		newId = this.insertCustomer_fee( customer,  line,  item,  description,  quantity,  price,  discount_expr,  initial_date,  final_date,  billing_date,  period,  security_level,  workplace );
		customer_feeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Customer_segment
	 * @param description Descripcion del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCustomer_segment( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO customer_segment ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO customer_segment ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> customer_segmentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Customer_segment
	 * @param id Identificador unico del Segmento
	 * @param description Descripcion del Segmento
	 * @throws SQLException
	*/
	protected int insertCustomer_segmentOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.customer_segmentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCustomer_segment( description );
		customer_segmentIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cv_evaluate
	 * @param type Tipo de Evaluacion
	 * @param value Valor de la Evaluacion
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_evaluate( Integer type,  Short value,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate ( type, value, curriculum) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_evaluate ( type, value, curriculum) VALUES (  '"+ type +"', '"+ value +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( type == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, type);
		}
		if ( value == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, value);
		}
		if ( curriculum == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_evaluateIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_evaluate
	 * @param id Identificador unico de la Evaluacion
	 * @param type Tipo de Evaluacion
	 * @param value Valor de la Evaluacion
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_evaluateOldIds( Integer oldId , Integer type , Short value , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_evaluateIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_evaluateIds.get(null);
		newId = this.insertCv_evaluate( type,  value,  curriculum );
		cv_evaluateIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCv_evaluate_summary( String strengths,  String weaknesses,  Short profile,  String comments,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate_summary ( strengths, weaknesses, profile, comments, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_evaluate_summary ( strengths, weaknesses, profile, comments, curriculum) VALUES (  '"+ strengths +"', '"+ weaknesses +"', '"+ profile +"', '"+ comments +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( strengths == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, strengths);
		}
		if ( weaknesses == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, weaknesses);
		}
		if ( profile == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, profile);
		}
		if ( comments == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, comments);
		}
		if ( curriculum == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_evaluate_summaryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_evaluate_summary
	 * @param id Identificador unico del Resumen
	 * @param strengths Fortalezas
	 * @param weaknesses Debilidades
	 * @param profile Perfil
	 * @param comments Comentarios
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_evaluate_summaryOldIds( Integer oldId , String strengths , String weaknesses , Short profile , String comments , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_evaluate_summaryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_evaluate_summaryIds.get(null);
		newId = this.insertCv_evaluate_summary( strengths,  weaknesses,  profile,  comments,  curriculum );
		cv_evaluate_summaryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cv_evaluate_type
	 * @param name Nombre del Tipo de Evaluacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_evaluate_type( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_evaluate_type ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_evaluate_type ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_evaluate_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_evaluate_type
	 * @param id Identificador unico del Tipo de Evaluacion
	 * @param name Nombre del Tipo de Evaluacion
	 * @throws SQLException
	*/
	protected int insertCv_evaluate_typeOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.cv_evaluate_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertCv_evaluate_type( name );
		cv_evaluate_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertCv_knowledge( String name,  Short level,  Short experience,  Short lastuse,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_knowledge ( name, level, experience, lastuse, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_knowledge ( name, level, experience, lastuse, curriculum) VALUES (  '"+ name +"', '"+ level +"', '"+ experience +"', '"+ lastuse +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( level == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, level);
		}
		if ( experience == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, experience);
		}
		if ( lastuse == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, lastuse);
		}
		if ( curriculum == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_knowledgeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_knowledge
	 * @param id Identificador unico del Conocimiento
	 * @param name Nombre o descripcion del Conocimiento
	 * @param level Nivel del Conocimiento
	 * @param experience Experiencia en el Conocimiento
	 * @param lastuse Ultimo uso del Conocimiento
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_knowledgeOldIds( Integer oldId , String name , Short level , Short experience , Short lastuse , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_knowledgeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_knowledgeIds.get(null);
		newId = this.insertCv_knowledge( name,  level,  experience,  lastuse,  curriculum );
		cv_knowledgeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cv_languages
	 * @param language Idioma
	 * @param spoken Nivel oral del Idioma
	 * @param wrote Nivel escrito del Idioma
	 * @param read_level Nivel leído del Idioma
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_languages( Short language,  Short spoken,  Short wrote,  Short read_level,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_languages ( language, spoken, wrote, read_level, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_languages ( language, spoken, wrote, read_level, curriculum) VALUES (  '"+ language +"', '"+ spoken +"', '"+ wrote +"', '"+ read_level +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( language == null ) {
			stmt.setNull(1, -6);
		}
		else {
			stmt.setShort(1, language);
		}
		if ( spoken == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, spoken);
		}
		if ( wrote == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, wrote);
		}
		if ( read_level == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, read_level);
		}
		if ( curriculum == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_languagesIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_languages
	 * @param id Identificador único del Idioma
	 * @param language Idioma
	 * @param spoken Nivel oral del Idioma
	 * @param wrote Nivel escrito del Idioma
	 * @param read_level Nivel leído del Idioma
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_languagesOldIds( Integer oldId , Short language , Short spoken , Short wrote , Short read_level , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_languagesIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_languagesIds.get(null);
		newId = this.insertCv_languages( language,  spoken,  wrote,  read_level,  curriculum );
		cv_languagesIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cv_studies
	 * @param startingdate Fecha de inicio del Estudio
	 * @param endingdate Fecha de finalización del Estudio
	 * @param degree Nivel de Estudios
	 * @param speciality Especialidad de Estudios
	 * @param centre Centro de Estudios
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_studies( Date startingdate,  Date endingdate,  Short degree,  String speciality,  String centre,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_studies ( startingdate, endingdate, degree, speciality, centre, curriculum) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_studies ( startingdate, endingdate, degree, speciality, centre, curriculum) VALUES (  '"+ startingdate +"', '"+ endingdate +"', '"+ degree +"', '"+ speciality +"', '"+ centre +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( startingdate == null ) {
			stmt.setNull(1, 91);
		}
		else {
			stmt.setDate(1, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, endingdate);
		}
		if ( degree == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, degree);
		}
		if ( speciality == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, speciality);
		}
		if ( centre == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, centre);
		}
		if ( curriculum == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_studiesIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_studies
	 * @param id Identificador único del Estudio
	 * @param startingdate Fecha de inicio del Estudio
	 * @param endingdate Fecha de finalización del Estudio
	 * @param degree Nivel de Estudios
	 * @param speciality Especialidad de Estudios
	 * @param centre Centro de Estudios
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_studiesOldIds( Integer oldId , Date startingdate , Date endingdate , Short degree , String speciality , String centre , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_studiesIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_studiesIds.get(null);
		newId = this.insertCv_studies( startingdate,  endingdate,  degree,  speciality,  centre,  curriculum );
		cv_studiesIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Cv_workexperience
	 * @param startingdate Fecha de inicio de la Experiencia Laboral
	 * @param endingdate Fecha de finalización de la Experiencia Laboral
	 * @param job Trabajo desempeñado en la Experiencia Laboral
	 * @param company Compañía donde se desempeñó la Experiencia Laboral
	 * @param curriculum Identificador del Curriculum Vitae
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertCv_workexperience( Date startingdate,  Date endingdate,  String job,  String company,  Integer curriculum )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO cv_workexperience ( startingdate, endingdate, job, company, curriculum) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO cv_workexperience ( startingdate, endingdate, job, company, curriculum) VALUES (  '"+ startingdate +"', '"+ endingdate +"', '"+ job +"', '"+ company +"', '"+ curriculum +"')";
		LOGGER.debug(message);

		if ( startingdate == null ) {
			stmt.setNull(1, 91);
		}
		else {
			stmt.setDate(1, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, endingdate);
		}
		if ( job == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, job);
		}
		if ( company == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, company);
		}
		if ( curriculum == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, curriculum);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> cv_workexperienceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Cv_workexperience
	 * @param id Identifador único de la Experiencia Laboral
	 * @param startingdate Fecha de inicio de la Experiencia Laboral
	 * @param endingdate Fecha de finalización de la Experiencia Laboral
	 * @param job Trabajo desempeñado en la Experiencia Laboral
	 * @param company Compañía donde se desempeñó la Experiencia Laboral
	 * @param curriculum Identificador del Curriculum Vitae
	 * @throws SQLException
	*/
	protected int insertCv_workexperienceOldIds( Integer oldId , Date startingdate , Date endingdate , String job , String company , Integer curriculum )
	throws SQLException {
		
		Integer newId = this.cv_workexperienceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxcv_workexperienceIds.get(null);
		newId = this.insertCv_workexperience( startingdate,  endingdate,  job,  company,  curriculum );
		cv_workexperienceIds.put(oldId,newId);
		
		return newId;
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
	protected int insertDaily_tracking( Integer user_id,  Date tracking_date,  Double tracking_duration,  Integer job_type,  Integer customer,  Integer dossier,  Integer activity,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO daily_tracking ( user_id, tracking_date, tracking_duration, job_type, customer, dossier, activity, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO daily_tracking ( user_id, tracking_date, tracking_duration, job_type, customer, dossier, activity, comments) VALUES (  '"+ user_id +"', '"+ tracking_date +"', '"+ tracking_duration +"', '"+ job_type +"', '"+ customer +"', '"+ dossier +"', '"+ activity +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( user_id == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, user_id);
		}
		if ( tracking_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, tracking_date);
		}
		if ( tracking_duration == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, tracking_duration);
		}
		if ( job_type == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, job_type);
		}
		if ( customer == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, customer);
		}
		if ( dossier == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, dossier);
		}
		if ( activity == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, activity);
		}
		if ( comments == null ) {
			stmt.setNull(8, -1);
		}
		else {
			stmt.setAsciiStream(8, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> daily_trackingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Daily_tracking
	 * @param id Identificador unico del Parte
	 * @param user_id Identificador del Usuario que realiza el Parte
	 * @param tracking_date Fecha del Parte
	 * @param tracking_duration Tiempo invertido en el Parte
	 * @param job_type Tipo de Trabajo realizado en el Parte
	 * @param customer Identificador del Cliente asociado al Parte
	 * @param dossier Identificador del Expediente asociado al Parte
	 * @param activity Identificador de la Actividad asociada al Parte
	 * @param comments Comentarios del Parte
	 * @throws SQLException
	*/
	protected int insertDaily_trackingOldIds( Integer oldId , Integer user_id , Date tracking_date , Double tracking_duration , Integer job_type , Integer customer , Integer dossier , Integer activity , InputStream comments )
	throws SQLException {
		
		Integer newId = this.daily_trackingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdaily_trackingIds.get(null);
		newId = this.insertDaily_tracking( user_id,  tracking_date,  tracking_duration,  job_type,  customer,  dossier,  activity,  comments );
		daily_trackingIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Db_version
	 * @param version_number Numero de Version de la Base de Datos
	 * @throws SQLException
	*/
	protected void insertDb_version( String version_number )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO db_version ( version_number) VALUES ( ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO db_version ( version_number) VALUES ( '"+ version_number +"')";
		LOGGER.debug(message);

		if ( version_number == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, version_number);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Delivery
	 * @param series Serie del Albaran
	 * @param number Número del Albaran
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
	protected int insertDelivery( String series,  Integer number,  Integer customer,  Integer address,  Timestamp issue_time,  Integer pay_method,  Short security_level,  Short status,  Integer workplace,  Integer scope,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery ( series, number, customer, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO delivery ( series, number, customer, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  '"+ series +"', '"+ number +"', '"+ customer +"', '"+ address +"', '"+ issue_time +"', '"+ pay_method +"', '"+ security_level +"', '"+ status +"', '"+ workplace +"', '"+ scope +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"')";
		LOGGER.debug(message);

		if ( series == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, series);
		}
		if ( number == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, number);
		}
		if ( customer == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, customer);
		}
		if ( address == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, address);
		}
		if ( issue_time == null ) {
			stmt.setNull(5, 93);
		}
		else {
			stmt.setTimestamp(5, issue_time);
		}
		if ( pay_method == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, pay_method);
		}
		if ( security_level == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, security_level);
		}
		if ( status == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, status);
		}
		if ( workplace == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, workplace);
		}
		if ( scope == null ) {
			stmt.setNull(10, 4);
		}
		else {
			stmt.setInt(10, scope);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(11, 5);
		}
		else {
			stmt.setInt(11, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(12, 5);
		}
		else {
			stmt.setInt(12, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(13, 5);
		}
		else {
			stmt.setInt(13, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(14, 12);
		}
		else {
			stmt.setString(14, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(15, 4);
		}
		else {
			stmt.setInt(15, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(16, 12);
		}
		else {
			stmt.setString(16, bank_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> deliveryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Delivery
	 * @param id Identificador unico del Albaran de Venta
	 * @param series Serie del Albaran
	 * @param number Número del Albaran
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
	 * @throws SQLException
	*/
	protected int insertDeliveryOldIds( Integer oldId , String series , Integer number , Integer customer , Integer address , Timestamp issue_time , Integer pay_method , Short security_level , Short status , Integer workplace , Integer scope , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account )
	throws SQLException {
		
		Integer newId = this.deliveryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdeliveryIds.get(null);
		newId = this.insertDelivery( series,  number,  customer,  address,  issue_time,  pay_method,  security_level,  status,  workplace,  scope,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account );
		deliveryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertDelivery_detail( Integer delivery,  Integer line,  Integer item,  String description,  Integer warehouse,  Double quantity,  Double price,  String discount_expr,  Short type,  Short source,  Integer sales_detail )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery_detail ( delivery, line, item, description, warehouse, quantity, price, discount_expr, type, source, sales_detail) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO delivery_detail ( delivery, line, item, description, warehouse, quantity, price, discount_expr, type, source, sales_detail) VALUES (  '"+ delivery +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ warehouse +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ type +"', '"+ source +"', '"+ sales_detail +"')";
		LOGGER.debug(message);

		if ( delivery == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, delivery);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( warehouse == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, warehouse);
		}
		if ( quantity == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, discount_expr);
		}
		if ( type == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, type);
		}
		if ( source == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, source);
		}
		if ( sales_detail == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, sales_detail);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> delivery_detailIds = 
		new HashMap<Integer, Integer>();
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
	 * @param type Tipo de Detalle de Albaran
	 * @param source Origen del Detalle de Albaran
	 * @param sales_detail Identificador del Detalle del Pedido de Venta asociado
	 * @throws SQLException
	*/
	protected int insertDelivery_detailOldIds( Integer oldId , Integer delivery , Integer line , Integer item , String description , Integer warehouse , Double quantity , Double price , String discount_expr , Short type , Short source , Integer sales_detail )
	throws SQLException {
		
		Integer newId = this.delivery_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdelivery_detailIds.get(null);
		newId = this.insertDelivery_detail( delivery,  line,  item,  description,  warehouse,  quantity,  price,  discount_expr,  type,  source,  sales_detail );
		delivery_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Delivery_detail_labour
	 * @param delivery_detail Identificador de la Linea de Albaran
	 * @param employee Identificador del Empleado
	 * @param quantity Numero de horas de mano de obra
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDelivery_detail_labour( Integer delivery_detail,  Integer employee,  Double quantity )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO delivery_detail_labour ( delivery_detail, employee, quantity) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO delivery_detail_labour ( delivery_detail, employee, quantity) VALUES (  '"+ delivery_detail +"', '"+ employee +"', '"+ quantity +"')";
		LOGGER.debug(message);

		if ( delivery_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, delivery_detail);
		}
		if ( employee == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, employee);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> delivery_detail_labourIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Delivery_detail_labour
	 * @param id Identificador unico
	 * @param delivery_detail Identificador de la Linea de Albaran
	 * @param employee Identificador del Empleado
	 * @param quantity Numero de horas de mano de obra
	 * @throws SQLException
	*/
	protected int insertDelivery_detail_labourOldIds( Integer oldId , Integer delivery_detail , Integer employee , Double quantity )
	throws SQLException {
		
		Integer newId = this.delivery_detail_labourIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdelivery_detail_labourIds.get(null);
		newId = this.insertDelivery_detail_labour( delivery_detail,  employee,  quantity );
		delivery_detail_labourIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Department
	 * @param parent Identificador del Departamento padre
	 * @param description Descripcion del Departamento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDepartment( Integer parent,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO department ( parent, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO department ( parent, description) VALUES (  '"+ parent +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( parent == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, parent);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> departmentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Department
	 * @param id Identificador unico del Departamento
	 * @param parent Identificador del Departamento padre
	 * @param description Descripcion del Departamento
	 * @throws SQLException
	*/
	protected int insertDepartmentOldIds( Integer oldId , Integer parent , String description )
	throws SQLException {
		
		Integer newId = this.departmentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdepartmentIds.get(null);
		newId = this.insertDepartment( parent,  description );
		departmentIds.put(oldId,newId);
		
		return newId;
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
	protected int insertDossier( Integer customer,  Integer dossier_type,  String number,  String location,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO dossier ( customer, dossier_type, number, location, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO dossier ( customer, dossier_type, number, location, status) VALUES (  '"+ customer +"', '"+ dossier_type +"', '"+ number +"', '"+ location +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( customer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, customer);
		}
		if ( dossier_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, dossier_type);
		}
		if ( number == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, number);
		}
		if ( location == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, location);
		}
		if ( status == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> dossierIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Dossier
	 * @param id Identificador unico del Expediente
	 * @param customer Identificador del Cliente
	 * @param dossier_type Tipo de Expediente
	 * @param number Numero de Expediente
	 * @param location Ubicacion del Expediente
	 * @param status Estado del Expediente
	 * @throws SQLException
	*/
	protected int insertDossierOldIds( Integer oldId , Integer customer , Integer dossier_type , String number , String location , Short status )
	throws SQLException {
		
		Integer newId = this.dossierIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxdossierIds.get(null);
		newId = this.insertDossier( customer,  dossier_type,  number,  location,  status );
		dossierIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Dossier_type
	 * @param description Descripcion del Tipo de Expediente
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertDossier_type( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO dossier_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO dossier_type ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> dossier_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Dossier_type
	 * @param id Identificador unico del Tipo de Expediente
	 * @param description Descripcion del Tipo de Expediente
	 * @throws SQLException
	*/
	protected int insertDossier_typeOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.dossier_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertDossier_type( description );
		dossier_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertEc_catalogue( Integer catalogue,  InputStream catalogue_img,  InputStream catalogue_icon,  Short type,  Boolean visible )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_catalogue ( catalogue, catalogue_img, catalogue_icon, type, visible) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO ec_catalogue ( catalogue, catalogue_img, catalogue_icon, type, visible) VALUES (  '"+ catalogue +"', '"+ catalogue_img +"', '"+ catalogue_icon +"', '"+ type +"', '"+ visible +"')";
		LOGGER.debug(message);

		if ( catalogue == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, catalogue);
		}
		if ( catalogue_img == null ) {
			stmt.setNull(2, -4);
		}
		else {
			stmt.setBinaryStream(2, catalogue_img);
		}
		if ( catalogue_icon == null ) {
			stmt.setNull(3, -4);
		}
		else {
			stmt.setBinaryStream(3, catalogue_icon);
		}
		if ( type == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, type);
		}
		if ( visible == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, visible);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> ec_catalogueIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Ec_catalogue
	 * @param id Identificador unico
	 * @param catalogue Identificador del Catalogo
	 * @param catalogue_img Imagen para el Catalogo
	 * @param catalogue_icon Icono del Catalogo
	 * @param type Tipo de Catalogo
	 * @param visible Indica si es visible en internet
	 * @throws SQLException
	*/
	protected int insertEc_catalogueOldIds( Integer oldId , Integer catalogue , InputStream catalogue_img , InputStream catalogue_icon , Short type , Boolean visible )
	throws SQLException {
		
		Integer newId = this.ec_catalogueIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxec_catalogueIds.get(null);
		newId = this.insertEc_catalogue( catalogue,  catalogue_img,  catalogue_icon,  type,  visible );
		ec_catalogueIds.put(oldId,newId);
		
		return newId;
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
	protected int insertEc_config( Boolean active,  String name,  Short skin,  InputStream header_img,  String series,  Boolean commerce,  Short show_login,  Short price,  Short tax_in_price,  Short discount,  Integer bank_transfer,  Integer cash_on_delivery,  Integer visa,  Integer paypal,  Integer bank_draft,  InputStream legal_note1,  InputStream legal_note2,  InputStream legal_note3,  Integer tariff,  String header_color,  String telephone,  Short row_items,  InputStream left_banner,  InputStream right_banner,  InputStream welcome_banner,  Short ecommerce_status,  Double shipping_costs,  Double free_shipping,  String title_note1,  String title_note2,  String title_note3,  String email )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_config ( active, name, skin, header_img, series, commerce, show_login, price, tax_in_price, discount, bank_transfer, cash_on_delivery, visa, paypal, bank_draft, legal_note1, legal_note2, legal_note3, tariff, header_color, telephone, row_items, left_banner, right_banner, welcome_banner, ecommerce_status, shipping_costs, free_shipping, title_note1, title_note2, title_note3, email) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO ec_config ( active, name, skin, header_img, series, commerce, show_login, price, tax_in_price, discount, bank_transfer, cash_on_delivery, visa, paypal, bank_draft, legal_note1, legal_note2, legal_note3, tariff, header_color, telephone, row_items, left_banner, right_banner, welcome_banner, ecommerce_status, shipping_costs, free_shipping, title_note1, title_note2, title_note3, email) VALUES (  '"+ active +"', '"+ name +"', '"+ skin +"', '"+ header_img +"', '"+ series +"', '"+ commerce +"', '"+ show_login +"', '"+ price +"', '"+ tax_in_price +"', '"+ discount +"', '"+ bank_transfer +"', '"+ cash_on_delivery +"', '"+ visa +"', '"+ paypal +"', '"+ bank_draft +"', '"+ legal_note1 +"', '"+ legal_note2 +"', '"+ legal_note3 +"', '"+ tariff +"', '"+ header_color +"', '"+ telephone +"', '"+ row_items +"', '"+ left_banner +"', '"+ right_banner +"', '"+ welcome_banner +"', '"+ ecommerce_status +"', '"+ shipping_costs +"', '"+ free_shipping +"', '"+ title_note1 +"', '"+ title_note2 +"', '"+ title_note3 +"', '"+ email +"')";
		LOGGER.debug(message);

		if ( active == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, active);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}
		if ( skin == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, skin);
		}
		if ( header_img == null ) {
			stmt.setNull(4, -4);
		}
		else {
			stmt.setBinaryStream(4, header_img);
		}
		if ( series == null ) {
			stmt.setNull(5, 1);
		}
		else {
			stmt.setString(5, series);
		}
		if ( commerce == null ) {
			stmt.setNull(6, -7);
		}
		else {
			stmt.setBoolean(6, commerce);
		}
		if ( show_login == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, show_login);
		}
		if ( price == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, price);
		}
		if ( tax_in_price == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, tax_in_price);
		}
		if ( discount == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, discount);
		}
		if ( bank_transfer == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, bank_transfer);
		}
		if ( cash_on_delivery == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, cash_on_delivery);
		}
		if ( visa == null ) {
			stmt.setNull(13, 4);
		}
		else {
			stmt.setInt(13, visa);
		}
		if ( paypal == null ) {
			stmt.setNull(14, 4);
		}
		else {
			stmt.setInt(14, paypal);
		}
		if ( bank_draft == null ) {
			stmt.setNull(15, 4);
		}
		else {
			stmt.setInt(15, bank_draft);
		}
		if ( legal_note1 == null ) {
			stmt.setNull(16, -1);
		}
		else {
			stmt.setAsciiStream(16, legal_note1);
		}
		if ( legal_note2 == null ) {
			stmt.setNull(17, -1);
		}
		else {
			stmt.setAsciiStream(17, legal_note2);
		}
		if ( legal_note3 == null ) {
			stmt.setNull(18, -1);
		}
		else {
			stmt.setAsciiStream(18, legal_note3);
		}
		if ( tariff == null ) {
			stmt.setNull(19, 4);
		}
		else {
			stmt.setInt(19, tariff);
		}
		if ( header_color == null ) {
			stmt.setNull(20, 12);
		}
		else {
			stmt.setString(20, header_color);
		}
		if ( telephone == null ) {
			stmt.setNull(21, 12);
		}
		else {
			stmt.setString(21, telephone);
		}
		if ( row_items == null ) {
			stmt.setNull(22, -6);
		}
		else {
			stmt.setShort(22, row_items);
		}
		if ( left_banner == null ) {
			stmt.setNull(23, -4);
		}
		else {
			stmt.setBinaryStream(23, left_banner);
		}
		if ( right_banner == null ) {
			stmt.setNull(24, -4);
		}
		else {
			stmt.setBinaryStream(24, right_banner);
		}
		if ( welcome_banner == null ) {
			stmt.setNull(25, -4);
		}
		else {
			stmt.setBinaryStream(25, welcome_banner);
		}
		if ( ecommerce_status == null ) {
			stmt.setNull(26, -6);
		}
		else {
			stmt.setShort(26, ecommerce_status);
		}
		if ( shipping_costs == null ) {
			stmt.setNull(27, 8);
		}
		else {
			stmt.setDouble(27, shipping_costs);
		}
		if ( free_shipping == null ) {
			stmt.setNull(28, 8);
		}
		else {
			stmt.setDouble(28, free_shipping);
		}
		if ( title_note1 == null ) {
			stmt.setNull(29, 12);
		}
		else {
			stmt.setString(29, title_note1);
		}
		if ( title_note2 == null ) {
			stmt.setNull(30, 12);
		}
		else {
			stmt.setString(30, title_note2);
		}
		if ( title_note3 == null ) {
			stmt.setNull(31, 12);
		}
		else {
			stmt.setString(31, title_note3);
		}
		if ( email == null ) {
			stmt.setNull(32, 12);
		}
		else {
			stmt.setString(32, email);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> ec_configIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Ec_config
	 * @param id Identificador unico
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
	 * @throws SQLException
	*/
	protected int insertEc_configOldIds( Integer oldId , Boolean active , String name , Short skin , InputStream header_img , String series , Boolean commerce , Short show_login , Short price , Short tax_in_price , Short discount , Integer bank_transfer , Integer cash_on_delivery , Integer visa , Integer paypal , Integer bank_draft , InputStream legal_note1 , InputStream legal_note2 , InputStream legal_note3 , Integer tariff , String header_color , String telephone , Short row_items , InputStream left_banner , InputStream right_banner , InputStream welcome_banner , Short ecommerce_status , Double shipping_costs , Double free_shipping , String title_note1 , String title_note2 , String title_note3 , String email )
	throws SQLException {
		
		Integer newId = this.ec_configIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxec_configIds.get(null);
		newId = this.insertEc_config( active,  name,  skin,  header_img,  series,  commerce,  show_login,  price,  tax_in_price,  discount,  bank_transfer,  cash_on_delivery,  visa,  paypal,  bank_draft,  legal_note1,  legal_note2,  legal_note3,  tariff,  header_color,  telephone,  row_items,  left_banner,  right_banner,  welcome_banner,  ecommerce_status,  shipping_costs,  free_shipping,  title_note1,  title_note2,  title_note3,  email );
		ec_configIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Ec_offer_pay_info
	 * @param offer Identificador del Presupuesto
	 * @param payment_status Estado del pago
	 * @param authorization_number Numero de autorizacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_offer_pay_info( Integer offer,  Short payment_status,  Integer authorization_number )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_offer_pay_info ( offer, payment_status, authorization_number) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO ec_offer_pay_info ( offer, payment_status, authorization_number) VALUES (  '"+ offer +"', '"+ payment_status +"', '"+ authorization_number +"')";
		LOGGER.debug(message);

		if ( offer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, offer);
		}
		if ( payment_status == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, payment_status);
		}
		if ( authorization_number == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, authorization_number);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> ec_offer_pay_infoIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Ec_offer_pay_info
	 * @param id Identificador unico
	 * @param offer Identificador del Presupuesto
	 * @param payment_status Estado del pago
	 * @param authorization_number Numero de autorizacion
	 * @throws SQLException
	*/
	protected int insertEc_offer_pay_infoOldIds( Integer oldId , Integer offer , Short payment_status , Integer authorization_number )
	throws SQLException {
		
		Integer newId = this.ec_offer_pay_infoIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxec_offer_pay_infoIds.get(null);
		newId = this.insertEc_offer_pay_info( offer,  payment_status,  authorization_number );
		ec_offer_pay_infoIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Ec_paymethod
	 * @param pay_method Identificador de la Forma de Pago
	 * @param user_name Nombre de Usuario
	 * @param password Contraseña para la pasarela de pago
	 * @param signature Identificador unico de la empresa para pasarela
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEc_paymethod( Integer pay_method,  String user_name,  String password,  String signature )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_paymethod ( pay_method, user_name, password, signature) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO ec_paymethod ( pay_method, user_name, password, signature) VALUES (  '"+ pay_method +"', '"+ user_name +"', '"+ password +"', '"+ signature +"')";
		LOGGER.debug(message);

		if ( pay_method == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, pay_method);
		}
		if ( user_name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, user_name);
		}
		if ( password == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, password);
		}
		if ( signature == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, signature);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> ec_paymethodIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Ec_paymethod
	 * @param id Identificador unico
	 * @param pay_method Identificador de la Forma de Pago
	 * @param user_name Nombre de Usuario
	 * @param password Contraseña para la pasarela de pago
	 * @param signature Identificador unico de la empresa para pasarela
	 * @throws SQLException
	*/
	protected int insertEc_paymethodOldIds( Integer oldId , Integer pay_method , String user_name , String password , String signature )
	throws SQLException {
		
		Integer newId = this.ec_paymethodIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxec_paymethodIds.get(null);
		newId = this.insertEc_paymethod( pay_method,  user_name,  password,  signature );
		ec_paymethodIds.put(oldId,newId);
		
		return newId;
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
	protected int insertEc_target( Integer target,  String login,  String password,  Short type,  Date last_access )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO ec_target ( target, login, password, type, last_access) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO ec_target ( target, login, password, type, last_access) VALUES (  '"+ target +"', '"+ login +"', '"+ password +"', '"+ type +"', '"+ last_access +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( login == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, login);
		}
		if ( password == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, password);
		}
		if ( type == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, type);
		}
		if ( last_access == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, last_access);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> ec_targetIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Ec_target
	 * @param id Identificador unico
	 * @param target Identificador del Cliente Potencial
	 * @param login Login del Cliente Potencial
	 * @param password Password del Cliente Potencial
	 * @param type Tipo de conexion
	 * @param last_access Ultima fecha de conexion
	 * @throws SQLException
	*/
	protected int insertEc_targetOldIds( Integer oldId , Integer target , String login , String password , Short type , Date last_access )
	throws SQLException {
		
		Integer newId = this.ec_targetIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxec_targetIds.get(null);
		newId = this.insertEc_target( target,  login,  password,  type,  last_access );
		ec_targetIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Employee
	 * @param registry Registro del Empleado
	 * @param workactivity Identificador de Actividad
	 * @param social_security_num Número de Seguridad Social del Empleado
	 * @param agreement_time Horas del Convenio
	 * @param active Indica si el Empleado sigue vinculado a la Empresa o no
	 * @throws SQLException
	*/
	protected void insertEmployee( Integer registry,  Integer workactivity,  String social_security_num,  Integer agreement_time,  Boolean active )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO employee ( registry, workactivity, social_security_num, agreement_time, active) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO employee ( registry, workactivity, social_security_num, agreement_time, active) VALUES ( '"+ registry +"', '"+ workactivity +"', '"+ social_security_num +"', '"+ agreement_time +"', '"+ active +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( workactivity == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, workactivity);
		}
		if ( social_security_num == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, social_security_num);
		}
		if ( agreement_time == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, agreement_time);
		}
		if ( active == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, active);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Enterprise
	 * @param registry Registro de la Empresa
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertEnterprise( Integer registry,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise ( registry, scope) VALUES ( ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO enterprise ( registry, scope) VALUES ( '"+ registry +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( scope == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, scope);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
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
	protected int insertEnterprise_activity( String description,  Integer enterprise,  Integer cnae,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_activity ( description, enterprise, cnae, type) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO enterprise_activity ( description, enterprise, cnae, type) VALUES (  '"+ description +"', '"+ enterprise +"', '"+ cnae +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( enterprise == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, enterprise);
		}
		if ( cnae == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, cnae);
		}
		if ( type == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> enterprise_activityIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Enterprise_activity
	 * @param id Identificador unico
	 * @param description Descripcion de la Actividad de la Empresa
	 * @param enterprise Identificador de la Empresa
	 * @param cnae Identificador del CNAE
	 * @param type Tipo de Actividad de la Empresa
	 * @throws SQLException
	*/
	protected int insertEnterprise_activityOldIds( Integer oldId , String description , Integer enterprise , Integer cnae , Short type )
	throws SQLException {
		
		Integer newId = this.enterprise_activityIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxenterprise_activityIds.get(null);
		newId = this.insertEnterprise_activity( description,  enterprise,  cnae,  type );
		enterprise_activityIds.put(oldId,newId);
		
		return newId;
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
	protected int insertEnterprise_ccc( String ccc,  Short type,  Integer enterprise_activity,  Integer geozone )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO enterprise_ccc ( ccc, type, enterprise_activity, geozone) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO enterprise_ccc ( ccc, type, enterprise_activity, geozone) VALUES (  '"+ ccc +"', '"+ type +"', '"+ enterprise_activity +"', '"+ geozone +"')";
		LOGGER.debug(message);

		if ( ccc == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, ccc);
		}
		if ( type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, type);
		}
		if ( enterprise_activity == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, enterprise_activity);
		}
		if ( geozone == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, geozone);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> enterprise_cccIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Enterprise_ccc
	 * @param id Identificador unico
	 * @param ccc Valor del Codigo Cuenta Cotizacion
	 * @param type Tipo de Cuenta Cotizacion
	 * @param enterprise_activity Identificador de la Actividad de Empresa
	 * @param geozone Identificador de la Zona Geografica
	 * @throws SQLException
	*/
	protected int insertEnterprise_cccOldIds( Integer oldId , String ccc , Short type , Integer enterprise_activity , Integer geozone )
	throws SQLException {
		
		Integer newId = this.enterprise_cccIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxenterprise_cccIds.get(null);
		newId = this.insertEnterprise_ccc( ccc,  type,  enterprise_activity,  geozone );
		enterprise_cccIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Evaluation_observation
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de Evaluacion
	 * @param comments Comentarios
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertEvaluation_observation( Integer alumn,  Short evaluation,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO evaluation_observation ( alumn, evaluation, comments) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO evaluation_observation ( alumn, evaluation, comments) VALUES (  '"+ alumn +"', '"+ evaluation +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( alumn == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, alumn);
		}
		if ( evaluation == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, evaluation);
		}
		if ( comments == null ) {
			stmt.setNull(3, -1);
		}
		else {
			stmt.setAsciiStream(3, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> evaluation_observationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Evaluation_observation
	 * @param id Identificador unico
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de Evaluacion
	 * @param comments Comentarios
	 * @throws SQLException
	*/
	protected int insertEvaluation_observationOldIds( Integer oldId , Integer alumn , Short evaluation , InputStream comments )
	throws SQLException {
		
		Integer newId = this.evaluation_observationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxevaluation_observationIds.get(null);
		newId = this.insertEvaluation_observation( alumn,  evaluation,  comments );
		evaluation_observationIds.put(oldId,newId);
		
		return newId;
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
	protected int insertExpenditures( Integer resource,  Integer expenditures_item,  Date date,  Double amount )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expenditures ( resource, expenditures_item, date, amount) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO expenditures ( resource, expenditures_item, date, amount) VALUES (  '"+ resource +"', '"+ expenditures_item +"', '"+ date +"', '"+ amount +"')";
		LOGGER.debug(message);

		if ( resource == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, resource);
		}
		if ( expenditures_item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, expenditures_item);
		}
		if ( date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, date);
		}
		if ( amount == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, amount);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> expendituresIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Expenditures
	 * @param id Identificador unico del Coste
	 * @param resource Identificador del Recurso de Empresa
	 * @param expenditures_item Identificador del Tipo de Coste
	 * @param date Fecha del Coste
	 * @param amount Importe del Coste
	 * @throws SQLException
	*/
	protected int insertExpendituresOldIds( Integer oldId , Integer resource , Integer expenditures_item , Date date , Double amount )
	throws SQLException {
		
		Integer newId = this.expendituresIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxexpendituresIds.get(null);
		newId = this.insertExpenditures( resource,  expenditures_item,  date,  amount );
		expendituresIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Expenditures_items
	 * @param name Nombre del Tipo de Coste
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpenditures_items( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expenditures_items ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO expenditures_items ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> expenditures_itemsIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Expenditures_items
	 * @param id Identificador unico del Tipo de Coste
	 * @param name Nombre del Tipo de Coste
	 * @throws SQLException
	*/
	protected int insertExpenditures_itemsOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.expenditures_itemsIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertExpenditures_items( name );
		expenditures_itemsIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Expense
	 * @param description Descripcion del Gasto
	 * @param unit_price Precio unitario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertExpense( String description,  Double unit_price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense ( description, unit_price) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO expense ( description, unit_price) VALUES (  '"+ description +"', '"+ unit_price +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( unit_price == null ) {
			stmt.setNull(2, 8);
		}
		else {
			stmt.setDouble(2, unit_price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> expenseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Expense
	 * @param id Identificador unico
	 * @param description Descripcion del Gasto
	 * @param unit_price Precio unitario
	 * @throws SQLException
	*/
	protected int insertExpenseOldIds( Integer oldId , String description , Double unit_price )
	throws SQLException {
		
		Integer newId = this.expenseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertExpense( description,  unit_price );
		expenseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertExpense_account( Integer registry,  Short expense_holder_type,  Short status,  Date issue_date,  String description,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense_account ( registry, expense_holder_type, status, issue_date, description, comments) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO expense_account ( registry, expense_holder_type, status, issue_date, description, comments) VALUES (  '"+ registry +"', '"+ expense_holder_type +"', '"+ status +"', '"+ issue_date +"', '"+ description +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( expense_holder_type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, expense_holder_type);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}
		if ( issue_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, issue_date);
		}
		if ( description == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, description);
		}
		if ( comments == null ) {
			stmt.setNull(6, -1);
		}
		else {
			stmt.setAsciiStream(6, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> expense_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Expense_account
	 * @param id Identificador unico
	 * @param registry Registry que realiza los Gastos
	 * @param expense_holder_type Tipo de Registry
	 * @param status Estado del Gasto
	 * @param issue_date Fecha del Gasto
	 * @param description Descripcion del Gasto
	 * @param comments Comentarios acerca del Gasto
	 * @throws SQLException
	*/
	protected int insertExpense_accountOldIds( Integer oldId , Integer registry , Short expense_holder_type , Short status , Date issue_date , String description , InputStream comments )
	throws SQLException {
		
		Integer newId = this.expense_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxexpense_accountIds.get(null);
		newId = this.insertExpense_account( registry,  expense_holder_type,  status,  issue_date,  description,  comments );
		expense_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertExpense_account_detail( Integer expense_account,  Integer expense,  Double quantity,  Double price,  Double amount )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO expense_account_detail ( expense_account, expense, quantity, price, amount) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO expense_account_detail ( expense_account, expense, quantity, price, amount) VALUES (  '"+ expense_account +"', '"+ expense +"', '"+ quantity +"', '"+ price +"', '"+ amount +"')";
		LOGGER.debug(message);

		if ( expense_account == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, expense_account);
		}
		if ( expense == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, expense);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, price);
		}
		if ( amount == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, amount);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> expense_account_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Expense_account_detail
	 * @param id Identificador unico
	 * @param expense_account Identidicador del Gasto
	 * @param expense Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Precio unitario del Gasto
	 * @param amount Precio total del Gasto
	 * @throws SQLException
	*/
	protected int insertExpense_account_detailOldIds( Integer oldId , Integer expense_account , Integer expense , Double quantity , Double price , Double amount )
	throws SQLException {
		
		Integer newId = this.expense_account_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxexpense_account_detailIds.get(null);
		newId = this.insertExpense_account_detail( expense_account,  expense,  quantity,  price,  amount );
		expense_account_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertFavorite( Integer favorite_category,  String description,  String url,  Integer user_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO favorite ( favorite_category, description, url, user_id) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO favorite ( favorite_category, description, url, user_id) VALUES (  '"+ favorite_category +"', '"+ description +"', '"+ url +"', '"+ user_id +"')";
		LOGGER.debug(message);

		if ( favorite_category == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, favorite_category);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( url == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, url);
		}
		if ( user_id == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, user_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> favoriteIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Favorite
	 * @param id Identificador unico de Favorito
	 * @param favorite_category Categoria a la que pertenece el Favorito
	 * @param description Descripcion del Favorito
	 * @param url Url del Favorito
	 * @param user_id Usuario al que pertenece el Favorito
	 * @throws SQLException
	*/
	protected int insertFavoriteOldIds( Integer oldId , Integer favorite_category , String description , String url , Integer user_id )
	throws SQLException {
		
		Integer newId = this.favoriteIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfavoriteIds.get(null);
		newId = this.insertFavorite( favorite_category,  description,  url,  user_id );
		favoriteIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Favorite_category
	 * @param description Descripcion de la Categoria
	 * @param user_id Usuario al que pertenece la Categoria
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFavorite_category( String description,  Integer user_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO favorite_category ( description, user_id) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO favorite_category ( description, user_id) VALUES (  '"+ description +"', '"+ user_id +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( user_id == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, user_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> favorite_categoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Favorite_category
	 * @param id Identificador unico
	 * @param description Descripcion de la Categoria
	 * @param user_id Usuario al que pertenece la Categoria
	 * @throws SQLException
	*/
	protected int insertFavorite_categoryOldIds( Integer oldId , String description , Integer user_id )
	throws SQLException {
		
		Integer newId = this.favorite_categoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfavorite_categoryIds.get(null);
		newId = this.insertFavorite_category( description,  user_id );
		favorite_categoryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Fbatch
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la Compañia utilizado en la Remesa
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFbatch( String description,  Date issue_date,  Short type,  Short status,  Integer rbank,  Boolean payment,  Short security_level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fbatch ( description, issue_date, type, status, rbank, payment, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO fbatch ( description, issue_date, type, status, rbank, payment, security_level) VALUES (  '"+ description +"', '"+ issue_date +"', '"+ type +"', '"+ status +"', '"+ rbank +"', '"+ payment +"', '"+ security_level +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( issue_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, issue_date);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}
		if ( rbank == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, rbank);
		}
		if ( payment == null ) {
			stmt.setNull(6, -7);
		}
		else {
			stmt.setBoolean(6, payment);
		}
		if ( security_level == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, security_level);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> fbatchIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Fbatch
	 * @param id Identificador unico de la Remesa
	 * @param description Descripcion de la Remesa
	 * @param issue_date Fecha de emision de la Remesa
	 * @param type Tipo de Remesa
	 * @param status Estado de la Remesa
	 * @param rbank Banco de la Compañia utilizado en la Remesa
	 * @param payment Indica si es un pago o un cobro
	 * @param security_level Nivel de seguridad
	 * @throws SQLException
	*/
	protected int insertFbatchOldIds( Integer oldId , String description , Date issue_date , Short type , Short status , Integer rbank , Boolean payment , Short security_level )
	throws SQLException {
		
		Integer newId = this.fbatchIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfbatchIds.get(null);
		newId = this.insertFbatch( description,  issue_date,  type,  status,  rbank,  payment,  security_level );
		fbatchIds.put(oldId,newId);
		
		return newId;
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
	protected int insertFbatch_detail( Integer fbatch,  Integer finance,  Double amount,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fbatch_detail ( fbatch, finance, amount, status) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO fbatch_detail ( fbatch, finance, amount, status) VALUES (  '"+ fbatch +"', '"+ finance +"', '"+ amount +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( fbatch == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, fbatch);
		}
		if ( finance == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, finance);
		}
		if ( amount == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, amount);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> fbatch_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Fbatch_detail
	 * @param id Identificador unico del Detalle de la Remesa
	 * @param fbatch Identificador de la Remesa
	 * @param finance Identificador del Vencimiento
	 * @param amount Importe del Detalle de la Remesa
	 * @param status Estado del Detalle de la Remesa
	 * @throws SQLException
	*/
	protected int insertFbatch_detailOldIds( Integer oldId , Integer fbatch , Integer finance , Double amount , Short status )
	throws SQLException {
		
		Integer newId = this.fbatch_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfbatch_detailIds.get(null);
		newId = this.insertFbatch_detail( fbatch,  finance,  amount,  status );
		fbatch_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertFinance( Boolean payment,  Integer registry,  Double amount,  Double expenses,  String concept,  Integer invoice,  Date due_date,  Integer pay_method,  Integer bank,  String bank_account,  Short status,  Short security_level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO finance ( payment, registry, amount, expenses, concept, invoice, due_date, pay_method, bank, bank_account, status, security_level) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO finance ( payment, registry, amount, expenses, concept, invoice, due_date, pay_method, bank, bank_account, status, security_level) VALUES (  '"+ payment +"', '"+ registry +"', '"+ amount +"', '"+ expenses +"', '"+ concept +"', '"+ invoice +"', '"+ due_date +"', '"+ pay_method +"', '"+ bank +"', '"+ bank_account +"', '"+ status +"', '"+ security_level +"')";
		LOGGER.debug(message);

		if ( payment == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, payment);
		}
		if ( registry == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, registry);
		}
		if ( amount == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, amount);
		}
		if ( expenses == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, expenses);
		}
		if ( concept == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, concept);
		}
		if ( invoice == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, invoice);
		}
		if ( due_date == null ) {
			stmt.setNull(7, 91);
		}
		else {
			stmt.setDate(7, due_date);
		}
		if ( pay_method == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, pay_method);
		}
		if ( bank == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(10, 12);
		}
		else {
			stmt.setString(10, bank_account);
		}
		if ( status == null ) {
			stmt.setNull(11, -6);
		}
		else {
			stmt.setShort(11, status);
		}
		if ( security_level == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, security_level);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> financeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Finance
	 * @param id Identificador unico del Vencimiento
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
	 * @throws SQLException
	*/
	protected int insertFinanceOldIds( Integer oldId , Boolean payment , Integer registry , Double amount , Double expenses , String concept , Integer invoice , Date due_date , Integer pay_method , Integer bank , String bank_account , Short status , Short security_level )
	throws SQLException {
		
		Integer newId = this.financeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfinanceIds.get(null);
		newId = this.insertFinance( payment,  registry,  amount,  expenses,  concept,  invoice,  due_date,  pay_method,  bank,  bank_account,  status,  security_level );
		financeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Finance_tracking
	 * @param finance Identificador de Vencimiento
	 * @param tracking_date Fecha de Seguimiento
	 * @param type Tipo de Seguimiento
	 * @param description Descripcion del Seguimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la Compaia
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertFinance_tracking( Integer finance,  Date tracking_date,  Short type,  String description,  Integer pm_type_detail,  Integer rbank,  Double amount,  Boolean recorded )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO finance_tracking ( finance, tracking_date, type, description, pm_type_detail, rbank, amount, recorded) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO finance_tracking ( finance, tracking_date, type, description, pm_type_detail, rbank, amount, recorded) VALUES (  '"+ finance +"', '"+ tracking_date +"', '"+ type +"', '"+ description +"', '"+ pm_type_detail +"', '"+ rbank +"', '"+ amount +"', '"+ recorded +"')";
		LOGGER.debug(message);

		if ( finance == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, finance);
		}
		if ( tracking_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, tracking_date);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( pm_type_detail == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, pm_type_detail);
		}
		if ( rbank == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, rbank);
		}
		if ( amount == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, amount);
		}
		if ( recorded == null ) {
			stmt.setNull(8, -7);
		}
		else {
			stmt.setBoolean(8, recorded);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> finance_trackingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Finance_tracking
	 * @param id Identificador unico
	 * @param finance Identificador de Vencimiento
	 * @param tracking_date Fecha de Seguimiento
	 * @param type Tipo de Seguimiento
	 * @param description Descripcion del Seguimiento
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param rbank Identificador de la Cuenta Bancaria de la Compaia
	 * @param amount Importe del Seguimiento
	 * @param recorded Indica si esta contabilizado o no
	 * @throws SQLException
	*/
	protected int insertFinance_trackingOldIds( Integer oldId , Integer finance , Date tracking_date , Short type , String description , Integer pm_type_detail , Integer rbank , Double amount , Boolean recorded )
	throws SQLException {
		
		Integer newId = this.finance_trackingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfinance_trackingIds.get(null);
		newId = this.insertFinance_tracking( finance,  tracking_date,  type,  description,  pm_type_detail,  rbank,  amount,  recorded );
		finance_trackingIds.put(oldId,newId);
		
		return newId;
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
	protected int insertFs_vat( Integer year,  Short period,  Short type,  InputStream comments,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fs_vat ( year, period, type, comments, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO fs_vat ( year, period, type, comments, status) VALUES (  '"+ year +"', '"+ period +"', '"+ type +"', '"+ comments +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( year == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, year);
		}
		if ( period == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, period);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}
		if ( comments == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, comments);
		}
		if ( status == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> fs_vatIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Fs_vat
	 * @param id Identificador unico
	 * @param year Ejercicio de la Declaracion
	 * @param period Periodo de la Declaracion
	 * @param type Tipo de Declaracion
	 * @param comments Comentarios de la Declaracion
	 * @param status Estado de la Declaracion
	 * @throws SQLException
	*/
	protected int insertFs_vatOldIds( Integer oldId , Integer year , Short period , Short type , InputStream comments , Short status )
	throws SQLException {
		
		Integer newId = this.fs_vatIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertFs_vat( year,  period,  type,  comments,  status );
		fs_vatIds.put(oldId,newId);
		
		return newId;
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
	protected int insertFs_vat_detail( Integer fs_vat,  Short key,  Double percent,  Double taxable_base,  Double quota,  Double deductible_quota,  Double adj_taxable_base,  Double adj_quota,  Double adj_deductible_quota )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO fs_vat_detail ( fs_vat, key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO fs_vat_detail ( fs_vat, key, percent, taxable_base, quota, deductible_quota, adj_taxable_base, adj_quota, adj_deductible_quota) VALUES (  '"+ fs_vat +"', '"+ key +"', '"+ percent +"', '"+ taxable_base +"', '"+ quota +"', '"+ deductible_quota +"', '"+ adj_taxable_base +"', '"+ adj_quota +"', '"+ adj_deductible_quota +"')";
		LOGGER.debug(message);

		if ( fs_vat == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, fs_vat);
		}
		if ( key == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, key);
		}
		if ( percent == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, percent);
		}
		if ( taxable_base == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, taxable_base);
		}
		if ( quota == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quota);
		}
		if ( deductible_quota == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, deductible_quota);
		}
		if ( adj_taxable_base == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, adj_taxable_base);
		}
		if ( adj_quota == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, adj_quota);
		}
		if ( adj_deductible_quota == null ) {
			stmt.setNull(9, 8);
		}
		else {
			stmt.setDouble(9, adj_deductible_quota);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> fs_vat_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Fs_vat_detail
	 * @param id Identificador unico
	 * @param fs_vat Identificador de la Declaracion
	 * @param key Clave de la Declaracion
	 * @param percent Porcentaje de IVA
	 * @param taxable_base Base Imponible
	 * @param quota Cuota
	 * @param deductible_quota Cuota Deducible
	 * @param adj_taxable_base Base Imponible Ajustada
	 * @param adj_quota Cuota Ajustada
	 * @param adj_deductible_quota Cuota Deducible Ajustada
	 * @throws SQLException
	*/
	protected int insertFs_vat_detailOldIds( Integer oldId , Integer fs_vat , Short key , Double percent , Double taxable_base , Double quota , Double deductible_quota , Double adj_taxable_base , Double adj_quota , Double adj_deductible_quota )
	throws SQLException {
		
		Integer newId = this.fs_vat_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxfs_vat_detailIds.get(null);
		newId = this.insertFs_vat_detail( fs_vat,  key,  percent,  taxable_base,  quota,  deductible_quota,  adj_taxable_base,  adj_quota,  adj_deductible_quota );
		fs_vat_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Geotree
	 * @param parent Identificador de la Zona Geografica Padre
	 * @param child Identificador de la Zona Geografica Hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertGeotree( Integer parent,  Integer child )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO geotree ( parent, child) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO geotree ( parent, child) VALUES (  '"+ parent +"', '"+ child +"')";
		LOGGER.debug(message);

		if ( parent == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, parent);
		}
		if ( child == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, child);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> geotreeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Geotree
	 * @param id Identificador unico
	 * @param parent Identificador de la Zona Geografica Padre
	 * @param child Identificador de la Zona Geografica Hijo
	 * @throws SQLException
	*/
	protected int insertGeotreeOldIds( Integer oldId , Integer parent , Integer child )
	throws SQLException {
		
		Integer newId = this.geotreeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxgeotreeIds.get(null);
		newId = this.insertGeotree( parent,  child );
		geotreeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Geozone
	 * @param name Nombre de la Zona Geografica
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertGeozone( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO geozone ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO geozone ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> geozoneIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Geozone
	 * @param id Identificador unico de la Zona Geografica
	 * @param name Nombre de la Zona Geografica
	 * @throws SQLException
	*/
	protected int insertGeozoneOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.geozoneIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertGeozone( name );
		geozoneIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Holiday
	 * @param description Descripcion de la Festividad
	 * @param holiday Identificador de Festividad
	 * @param editable Indica si es editable o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertHoliday( String description,  Integer holiday,  Boolean editable )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO holiday ( description, holiday, editable) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO holiday ( description, holiday, editable) VALUES (  '"+ description +"', '"+ holiday +"', '"+ editable +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( holiday == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, holiday);
		}
		if ( editable == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, editable);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> holidayIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Holiday
	 * @param id Identificador unico
	 * @param description Descripcion de la Festividad
	 * @param holiday Identificador de Festividad
	 * @param editable Indica si es editable o no
	 * @throws SQLException
	*/
	protected int insertHolidayOldIds( Integer oldId , String description , Integer holiday , Boolean editable )
	throws SQLException {
		
		Integer newId = this.holidayIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxholidayIds.get(null);
		newId = this.insertHoliday( description,  holiday,  editable );
		holidayIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Holiday_detail
	 * @param holiday Identificador de Festividad
	 * @param date Fecha Festiva
	 * @param description Descripcion de Festividad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertHoliday_detail( Integer holiday,  Date date,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO holiday_detail ( holiday, date, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO holiday_detail ( holiday, date, description) VALUES (  '"+ holiday +"', '"+ date +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( holiday == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, holiday);
		}
		if ( date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, date);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> holiday_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Holiday_detail
	 * @param id Identificador unico
	 * @param holiday Identificador de Festividad
	 * @param date Fecha Festiva
	 * @param description Descripcion de Festividad
	 * @throws SQLException
	*/
	protected int insertHoliday_detailOldIds( Integer oldId , Integer holiday , Date date , String description )
	throws SQLException {
		
		Integer newId = this.holiday_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxholiday_detailIds.get(null);
		newId = this.insertHoliday_detail( holiday,  date,  description );
		holiday_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertIattach( Integer item,  Short mimeType,  String description,  InputStream data,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO iattach ( item, mimeType, description, data, type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO iattach ( item, mimeType, description, data, type) VALUES (  '"+ item +"', '"+ mimeType +"', '"+ description +"', '"+ data +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( mimeType == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( data == null ) {
			stmt.setNull(4, -4);
		}
		else {
			stmt.setBinaryStream(4, data);
		}
		if ( type == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> iattachIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Iattach
	 * @param id Identificador unico del Archivo Adjunto del Articulo
	 * @param item Identificador de Articulo
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @param type Tipo de Archivo Adjunto
	 * @throws SQLException
	*/
	protected int insertIattachOldIds( Integer oldId , Integer item , Short mimeType , String description , InputStream data , Short type )
	throws SQLException {
		
		Integer newId = this.iattachIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxiattachIds.get(null);
		newId = this.insertIattach( item,  mimeType,  description,  data,  type );
		iattachIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Incidence_type
	 * @param alias Alias del Tipo de Incidencia
	 * @param description Descripción del Tipo de Incidencia
	 * @param compute Indica la forma de computar las horas de la Incidencia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertIncidence_type( String alias,  String description,  Boolean compute )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO incidence_type ( alias, description, compute) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO incidence_type ( alias, description, compute) VALUES (  '"+ alias +"', '"+ description +"', '"+ compute +"')";
		LOGGER.debug(message);

		if ( alias == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, alias);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( compute == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, compute);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> incidence_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Incidence_type
	 * @param id Identificador unico de Tipo de Incidencia
	 * @param alias Alias del Tipo de Incidencia
	 * @param description Descripción del Tipo de Incidencia
	 * @param compute Indica la forma de computar las horas de la Incidencia
	 * @throws SQLException
	*/
	protected int insertIncidence_typeOldIds( Integer oldId , String alias , String description , Boolean compute )
	throws SQLException {
		
		Integer newId = this.incidence_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertIncidence_type( alias,  description,  compute );
		incidence_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertIncome( String series,  Integer number,  Integer supplier,  Integer address,  Date issue_time,  Integer pay_method,  Short security_level,  Short status,  Integer workplace,  Integer scope,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO income ( series, number, supplier, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO income ( series, number, supplier, address, issue_time, pay_method, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  '"+ series +"', '"+ number +"', '"+ supplier +"', '"+ address +"', '"+ issue_time +"', '"+ pay_method +"', '"+ security_level +"', '"+ status +"', '"+ workplace +"', '"+ scope +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"')";
		LOGGER.debug(message);

		if ( series == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, series);
		}
		if ( number == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, number);
		}
		if ( supplier == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, supplier);
		}
		if ( address == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, address);
		}
		if ( issue_time == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, issue_time);
		}
		if ( pay_method == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, pay_method);
		}
		if ( security_level == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, security_level);
		}
		if ( status == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, status);
		}
		if ( workplace == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, workplace);
		}
		if ( scope == null ) {
			stmt.setNull(10, 4);
		}
		else {
			stmt.setInt(10, scope);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(11, 5);
		}
		else {
			stmt.setInt(11, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(12, 5);
		}
		else {
			stmt.setInt(12, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(13, 5);
		}
		else {
			stmt.setInt(13, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(14, 12);
		}
		else {
			stmt.setString(14, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(15, 4);
		}
		else {
			stmt.setInt(15, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(16, 12);
		}
		else {
			stmt.setString(16, bank_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> incomeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Income
	 * @param id Identificador unico del Albaran de Compra
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
	 * @throws SQLException
	*/
	protected int insertIncomeOldIds( Integer oldId , String series , Integer number , Integer supplier , Integer address , Date issue_time , Integer pay_method , Short security_level , Short status , Integer workplace , Integer scope , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account )
	throws SQLException {
		
		Integer newId = this.incomeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxincomeIds.get(null);
		newId = this.insertIncome( series,  number,  supplier,  address,  issue_time,  pay_method,  security_level,  status,  workplace,  scope,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account );
		incomeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertIncome_detail( Integer income,  Integer line,  Integer item,  String description,  Integer warehouse,  Double quantity,  Double price,  String discount_expr,  Short type,  Short source,  Integer purchase_detail )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO income_detail ( income, line, item, description, warehouse, quantity, price, discount_expr, type, source, purchase_detail) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO income_detail ( income, line, item, description, warehouse, quantity, price, discount_expr, type, source, purchase_detail) VALUES (  '"+ income +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ warehouse +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ type +"', '"+ source +"', '"+ purchase_detail +"')";
		LOGGER.debug(message);

		if ( income == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, income);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( warehouse == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, warehouse);
		}
		if ( quantity == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, discount_expr);
		}
		if ( type == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, type);
		}
		if ( source == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, source);
		}
		if ( purchase_detail == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, purchase_detail);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> income_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Income_detail
	 * @param id Identificador unico del Detalle del Albaran de Compra
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
	 * @throws SQLException
	*/
	protected int insertIncome_detailOldIds( Integer oldId , Integer income , Integer line , Integer item , String description , Integer warehouse , Double quantity , Double price , String discount_expr , Short type , Short source , Integer purchase_detail )
	throws SQLException {
		
		Integer newId = this.income_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxincome_detailIds.get(null);
		newId = this.insertIncome_detail( income,  line,  item,  description,  warehouse,  quantity,  price,  discount_expr,  type,  source,  purchase_detail );
		income_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Inventory
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInventory( Date inventory_date,  Integer warehouse,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO inventory ( inventory_date, warehouse, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO inventory ( inventory_date, warehouse, description) VALUES (  '"+ inventory_date +"', '"+ warehouse +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( inventory_date == null ) {
			stmt.setNull(1, 91);
		}
		else {
			stmt.setDate(1, inventory_date);
		}
		if ( warehouse == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, warehouse);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> inventoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Inventory
	 * @param id Identificador unico del Inventario
	 * @param inventory_date Fecha de Inventario
	 * @param warehouse Almacen Inventariado
	 * @param description Descripcion del Inventario
	 * @throws SQLException
	*/
	protected int insertInventoryOldIds( Integer oldId , Date inventory_date , Integer warehouse , String description )
	throws SQLException {
		
		Integer newId = this.inventoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertInventory( inventory_date,  warehouse,  description );
		inventoryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertInventory_detail( Integer inventory,  Integer item,  Double actual_quantity,  Double real_quantity,  Double cost )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO inventory_detail ( inventory, item, actual_quantity, real_quantity, cost) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO inventory_detail ( inventory, item, actual_quantity, real_quantity, cost) VALUES (  '"+ inventory +"', '"+ item +"', '"+ actual_quantity +"', '"+ real_quantity +"', '"+ cost +"')";
		LOGGER.debug(message);

		if ( inventory == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, inventory);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( actual_quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, actual_quantity);
		}
		if ( real_quantity == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, real_quantity);
		}
		if ( cost == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, cost);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> inventory_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Inventory_detail
	 * @param id Identificador unico del Detalle del Inventario
	 * @param inventory Identificador de Inventario
	 * @param item Articulo Inventariado
	 * @param actual_quantity Cantidad actual del Articulo Inventariado
	 * @param real_quantity Cantidad real del Articulo Inventariado
	 * @param cost Coste del Articulo Inventariado
	 * @throws SQLException
	*/
	protected int insertInventory_detailOldIds( Integer oldId , Integer inventory , Integer item , Double actual_quantity , Double real_quantity , Double cost )
	throws SQLException {
		
		Integer newId = this.inventory_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinventory_detailIds.get(null);
		newId = this.insertInventory_detail( inventory,  item,  actual_quantity,  real_quantity,  cost );
		inventory_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertInvoice( String series,  Integer number,  String reference_code,  Integer registry,  String rdocument,  String rname,  Integer raddress,  Date issue_date,  Date tax_date,  Short security_level,  Short status,  Short type,  Boolean taxFree,  Boolean surcharge,  Boolean withholding,  InputStream comments,  Boolean investment,  Short transaction,  Boolean signed,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice ( series, number, reference_code, registry, rdocument, rname, raddress, issue_date, tax_date, security_level, status, type, taxFree, surcharge, withholding, comments, investment, transaction, signed, scope) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice ( series, number, reference_code, registry, rdocument, rname, raddress, issue_date, tax_date, security_level, status, type, taxFree, surcharge, withholding, comments, investment, transaction, signed, scope) VALUES (  '"+ series +"', '"+ number +"', '"+ reference_code +"', '"+ registry +"', '"+ rdocument +"', '"+ rname +"', '"+ raddress +"', '"+ issue_date +"', '"+ tax_date +"', '"+ security_level +"', '"+ status +"', '"+ type +"', '"+ taxFree +"', '"+ surcharge +"', '"+ withholding +"', '"+ comments +"', '"+ investment +"', '"+ transaction +"', '"+ signed +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( series == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, series);
		}
		if ( number == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, number);
		}
		if ( reference_code == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, reference_code);
		}
		if ( registry == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, registry);
		}
		if ( rdocument == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, rdocument);
		}
		if ( rname == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, rname);
		}
		if ( raddress == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, raddress);
		}
		if ( issue_date == null ) {
			stmt.setNull(8, 91);
		}
		else {
			stmt.setDate(8, issue_date);
		}
		if ( tax_date == null ) {
			stmt.setNull(9, 91);
		}
		else {
			stmt.setDate(9, tax_date);
		}
		if ( security_level == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, security_level);
		}
		if ( status == null ) {
			stmt.setNull(11, -6);
		}
		else {
			stmt.setShort(11, status);
		}
		if ( type == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, type);
		}
		if ( taxFree == null ) {
			stmt.setNull(13, -7);
		}
		else {
			stmt.setBoolean(13, taxFree);
		}
		if ( surcharge == null ) {
			stmt.setNull(14, -7);
		}
		else {
			stmt.setBoolean(14, surcharge);
		}
		if ( withholding == null ) {
			stmt.setNull(15, -7);
		}
		else {
			stmt.setBoolean(15, withholding);
		}
		if ( comments == null ) {
			stmt.setNull(16, -1);
		}
		else {
			stmt.setAsciiStream(16, comments);
		}
		if ( investment == null ) {
			stmt.setNull(17, -7);
		}
		else {
			stmt.setBoolean(17, investment);
		}
		if ( transaction == null ) {
			stmt.setNull(18, -6);
		}
		else {
			stmt.setShort(18, transaction);
		}
		if ( signed == null ) {
			stmt.setNull(19, -7);
		}
		else {
			stmt.setBoolean(19, signed);
		}
		if ( scope == null ) {
			stmt.setNull(20, 4);
		}
		else {
			stmt.setInt(20, scope);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoiceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice
	 * @param id Identificador unico de la Factura
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
	 * @throws SQLException
	*/
	protected int insertInvoiceOldIds( Integer oldId , String series , Integer number , String reference_code , Integer registry , String rdocument , String rname , Integer raddress , Date issue_date , Date tax_date , Short security_level , Short status , Short type , Boolean taxFree , Boolean surcharge , Boolean withholding , InputStream comments , Boolean investment , Short transaction , Boolean signed , Integer scope )
	throws SQLException {
		
		Integer newId = this.invoiceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoiceIds.get(null);
		newId = this.insertInvoice( series,  number,  reference_code,  registry,  rdocument,  rname,  raddress,  issue_date,  tax_date,  security_level,  status,  type,  taxFree,  surcharge,  withholding,  comments,  investment,  transaction,  signed,  scope );
		invoiceIds.put(oldId,newId);
		
		return newId;
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
	protected int insertInvoice_address( Integer invoice,  String address,  String address2,  String zip,  String city,  Integer geozone )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_address ( invoice, address, address2, zip, city, geozone) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_address ( invoice, address, address2, zip, city, geozone) VALUES (  '"+ invoice +"', '"+ address +"', '"+ address2 +"', '"+ zip +"', '"+ city +"', '"+ geozone +"')";
		LOGGER.debug(message);

		if ( invoice == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice);
		}
		if ( address == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, address);
		}
		if ( address2 == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, address2);
		}
		if ( zip == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, zip);
		}
		if ( city == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, city);
		}
		if ( geozone == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, geozone);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_addressIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_address
	 * @param id Identificador unico
	 * @param invoice Identificador de la Factura
	 * @param address Primera parte de la Direccion
	 * @param address2 Segunda parte de la Direccion
	 * @param zip Codigo Postal
	 * @param city Localidad
	 * @param geozone Identificador de la Zona Geografica
	 * @throws SQLException
	*/
	protected int insertInvoice_addressOldIds( Integer oldId , Integer invoice , String address , String address2 , String zip , String city , Integer geozone )
	throws SQLException {
		
		Integer newId = this.invoice_addressIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_addressIds.get(null);
		newId = this.insertInvoice_address( invoice,  address,  address2,  zip,  city,  geozone );
		invoice_addressIds.put(oldId,newId);
		
		return newId;
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
	protected int insertInvoice_attach( Integer invoice,  Short mimeType,  String description,  InputStream data )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_attach ( invoice, mimeType, description, data) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_attach ( invoice, mimeType, description, data) VALUES (  '"+ invoice +"', '"+ mimeType +"', '"+ description +"', '"+ data +"')";
		LOGGER.debug(message);

		if ( invoice == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice);
		}
		if ( mimeType == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( data == null ) {
			stmt.setNull(4, -4);
		}
		else {
			stmt.setBinaryStream(4, data);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_attachIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_attach
	 * @param id Identificador unico
	 * @param invoice Identificador de la Factura
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @throws SQLException
	*/
	protected int insertInvoice_attachOldIds( Integer oldId , Integer invoice , Short mimeType , String description , InputStream data )
	throws SQLException {
		
		Integer newId = this.invoice_attachIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_attachIds.get(null);
		newId = this.insertInvoice_attach( invoice,  mimeType,  description,  data );
		invoice_attachIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Invoice_detail
	 * @param invoice Identificador de la Factura
	 * @param line Numero de línea del Detalle dentro de la Factura
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
	protected int insertInvoice_detail( Integer invoice,  Integer line,  Integer item,  String description,  Double quantity,  Double price,  String discount_expr,  Short source,  Integer source_id,  Double taxable_base,  Double taxes,  Integer workplace )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_detail ( invoice, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_detail ( invoice, line, item, description, quantity, price, discount_expr, source, source_id, taxable_base, taxes, workplace) VALUES (  '"+ invoice +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ source +"', '"+ source_id +"', '"+ taxable_base +"', '"+ taxes +"', '"+ workplace +"')";
		LOGGER.debug(message);

		if ( invoice == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, discount_expr);
		}
		if ( source == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, source);
		}
		if ( source_id == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, source_id);
		}
		if ( taxable_base == null ) {
			stmt.setNull(10, 8);
		}
		else {
			stmt.setDouble(10, taxable_base);
		}
		if ( taxes == null ) {
			stmt.setNull(11, 8);
		}
		else {
			stmt.setDouble(11, taxes);
		}
		if ( workplace == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, workplace);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_detail
	 * @param id Identificador unico del Detalle de la Factura
	 * @param invoice Identificador de la Factura
	 * @param line Numero de línea del Detalle dentro de la Factura
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
	 * @throws SQLException
	*/
	protected int insertInvoice_detailOldIds( Integer oldId , Integer invoice , Integer line , Integer item , String description , Double quantity , Double price , String discount_expr , Short source , Integer source_id , Double taxable_base , Double taxes , Integer workplace )
	throws SQLException {
		
		Integer newId = this.invoice_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_detailIds.get(null);
		newId = this.insertInvoice_detail( invoice,  line,  item,  description,  quantity,  price,  discount_expr,  source,  source_id,  taxable_base,  taxes,  workplace );
		invoice_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Invoice_detail_account
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_detail_account( Integer invoice_detail,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_detail_account ( invoice_detail, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_detail_account ( invoice_detail, account) VALUES (  '"+ invoice_detail +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( invoice_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice_detail);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_detail_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_detail_account
	 * @param id Identificador unico
	 * @param invoice_detail Identificador de la Linea de Factura
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertInvoice_detail_accountOldIds( Integer oldId , Integer invoice_detail , String account )
	throws SQLException {
		
		Integer newId = this.invoice_detail_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_detail_accountIds.get(null);
		newId = this.insertInvoice_detail_account( invoice_detail,  account );
		invoice_detail_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertInvoice_tax( Integer invoice_detail,  Short tax_type,  Double percentage,  Double surcharge,  Double quota,  Double surcharge_quota,  Short vat_deduction_type,  Short withholding_type,  Double deductible_quota )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_tax ( invoice_detail, tax_type, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_quota) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_tax ( invoice_detail, tax_type, percentage, surcharge, quota, surcharge_quota, vat_deduction_type, withholding_type, deductible_quota) VALUES (  '"+ invoice_detail +"', '"+ tax_type +"', '"+ percentage +"', '"+ surcharge +"', '"+ quota +"', '"+ surcharge_quota +"', '"+ vat_deduction_type +"', '"+ withholding_type +"', '"+ deductible_quota +"')";
		LOGGER.debug(message);

		if ( invoice_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice_detail);
		}
		if ( tax_type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, tax_type);
		}
		if ( percentage == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, percentage);
		}
		if ( surcharge == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, surcharge);
		}
		if ( quota == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quota);
		}
		if ( surcharge_quota == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, surcharge_quota);
		}
		if ( vat_deduction_type == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, vat_deduction_type);
		}
		if ( withholding_type == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, withholding_type);
		}
		if ( deductible_quota == null ) {
			stmt.setNull(9, 8);
		}
		else {
			stmt.setDouble(9, deductible_quota);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_taxIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_tax
	 * @param id Identificador unico del Impuesto de la Factura
	 * @param invoice_detail Identificador del Detalle de la Factura
	 * @param tax_type Tipo de Impuesto del Detalle de la Factura
	 * @param percentage Porcentaje de Impuesto del Detalle de la Factura
	 * @param surcharge Porcentaje del recargo de equivalencia del Detalle de la Factura
	 * @param quota Cuota de Impuesto del Detalle de la Factura
	 * @param surcharge_quota Cuota de recargo de equivalencia del Detalle de la Factura
	 * @param vat_deduction_type Tipo de deduccion del IVA
	 * @param withholding_type Tipo de retencion
	 * @param deductible_quota Cuota deducible
	 * @throws SQLException
	*/
	protected int insertInvoice_taxOldIds( Integer oldId , Integer invoice_detail , Short tax_type , Double percentage , Double surcharge , Double quota , Double surcharge_quota , Short vat_deduction_type , Short withholding_type , Double deductible_quota )
	throws SQLException {
		
		Integer newId = this.invoice_taxIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_taxIds.get(null);
		newId = this.insertInvoice_tax( invoice_detail,  tax_type,  percentage,  surcharge,  quota,  surcharge_quota,  vat_deduction_type,  withholding_type,  deductible_quota );
		invoice_taxIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Invoice_tax_account
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoice_tax_account( Integer invoice_tax,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoice_tax_account ( invoice_tax, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoice_tax_account ( invoice_tax, account) VALUES (  '"+ invoice_tax +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( invoice_tax == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoice_tax);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoice_tax_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoice_tax_account
	 * @param id Identificador unico
	 * @param invoice_tax Identificador de la Linea de Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertInvoice_tax_accountOldIds( Integer oldId , Integer invoice_tax , String account )
	throws SQLException {
		
		Integer newId = this.invoice_tax_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoice_tax_accountIds.get(null);
		newId = this.insertInvoice_tax_account( invoice_tax,  account );
		invoice_tax_accountIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Invoicing_group
	 * @param parent Grupo de Facturacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group( Integer parent )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoicing_group ( parent) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoicing_group ( parent) VALUES (  '"+ parent +"')";
		LOGGER.debug(message);

		if ( parent == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, parent);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoicing_groupIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoicing_group
	 * @param id Identificador unico
	 * @param parent Grupo de Facturacion
	 * @throws SQLException
	*/
	protected int insertInvoicing_groupOldIds( Integer oldId , Integer parent )
	throws SQLException {
		
		Integer newId = this.invoicing_groupIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoicing_groupIds.get(null);
		newId = this.insertInvoicing_group( parent );
		invoicing_groupIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Invoicing_group_detail
	 * @param invoicing_group Grupo de Facturacion al que pertenece
	 * @param child Componente asociado a un Grupo de Facturacion
	 * @param grouped Indica si agrupa facturas o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertInvoicing_group_detail( Integer invoicing_group,  Integer child,  Boolean grouped )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO invoicing_group_detail ( invoicing_group, child, grouped) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO invoicing_group_detail ( invoicing_group, child, grouped) VALUES (  '"+ invoicing_group +"', '"+ child +"', '"+ grouped +"')";
		LOGGER.debug(message);

		if ( invoicing_group == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, invoicing_group);
		}
		if ( child == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, child);
		}
		if ( grouped == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, grouped);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> invoicing_group_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Invoicing_group_detail
	 * @param id Identificador Unico
	 * @param invoicing_group Grupo de Facturacion al que pertenece
	 * @param child Componente asociado a un Grupo de Facturacion
	 * @param grouped Indica si agrupa facturas o no
	 * @throws SQLException
	*/
	protected int insertInvoicing_group_detailOldIds( Integer oldId , Integer invoicing_group , Integer child , Boolean grouped )
	throws SQLException {
		
		Integer newId = this.invoicing_group_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxinvoicing_group_detailIds.get(null);
		newId = this.insertInvoicing_group_detail( invoicing_group,  child,  grouped );
		invoicing_group_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertItem( Integer product,  String detail,  InputStream description,  Double price,  Short status,  Double expenses_percent,  Double expenses_fixed,  Double profit_percent,  Double purchase_price,  Boolean internet,  String barcode )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item ( product, detail, description, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item ( product, detail, description, price, status, expenses_percent, expenses_fixed, profit_percent, purchase_price, internet, barcode) VALUES (  '"+ product +"', '"+ detail +"', '"+ description +"', '"+ price +"', '"+ status +"', '"+ expenses_percent +"', '"+ expenses_fixed +"', '"+ profit_percent +"', '"+ purchase_price +"', '"+ internet +"', '"+ barcode +"')";
		LOGGER.debug(message);

		if ( product == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, product);
		}
		if ( detail == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, detail);
		}
		if ( description == null ) {
			stmt.setNull(3, -1);
		}
		else {
			stmt.setAsciiStream(3, description);
		}
		if ( price == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, price);
		}
		if ( status == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, status);
		}
		if ( expenses_percent == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, expenses_percent);
		}
		if ( expenses_fixed == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, expenses_fixed);
		}
		if ( profit_percent == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, profit_percent);
		}
		if ( purchase_price == null ) {
			stmt.setNull(9, 8);
		}
		else {
			stmt.setDouble(9, purchase_price);
		}
		if ( internet == null ) {
			stmt.setNull(10, -7);
		}
		else {
			stmt.setBoolean(10, internet);
		}
		if ( barcode == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, barcode);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> itemIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item
	 * @param id Identificador unico del Articulo
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
	 * @throws SQLException
	*/
	protected int insertItemOldIds( Integer oldId , Integer product , String detail , InputStream description , Double price , Short status , Double expenses_percent , Double expenses_fixed , Double profit_percent , Double purchase_price , Boolean internet , String barcode )
	throws SQLException {
		
		Integer newId = this.itemIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitemIds.get(null);
		newId = this.insertItem( product,  detail,  description,  price,  status,  expenses_percent,  expenses_fixed,  profit_percent,  purchase_price,  internet,  barcode );
		itemIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Item_alternative
	 * @param item Identificador de Articulo
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param priority Prioridad del Articulo Alternativo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_alternative( Integer item,  Integer alternative_item,  Short priority )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_alternative ( item, alternative_item, priority) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item_alternative ( item, alternative_item, priority) VALUES (  '"+ item +"', '"+ alternative_item +"', '"+ priority +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( alternative_item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, alternative_item);
		}
		if ( priority == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, priority);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> item_alternativeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item_alternative
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param alternative_item Identificador de Articulo Alternativo
	 * @param priority Prioridad del Articulo Alternativo
	 * @throws SQLException
	*/
	protected int insertItem_alternativeOldIds( Integer oldId , Integer item , Integer alternative_item , Short priority )
	throws SQLException {
		
		Integer newId = this.item_alternativeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitem_alternativeIds.get(null);
		newId = this.insertItem_alternative( item,  alternative_item,  priority );
		item_alternativeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertItem_pos( Integer item,  String plu,  String barcode,  String desc_short,  Short plu_product_type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_pos ( item, plu, barcode, desc_short, plu_product_type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item_pos ( item, plu, barcode, desc_short, plu_product_type) VALUES (  '"+ item +"', '"+ plu +"', '"+ barcode +"', '"+ desc_short +"', '"+ plu_product_type +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( plu == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, plu);
		}
		if ( barcode == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, barcode);
		}
		if ( desc_short == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, desc_short);
		}
		if ( plu_product_type == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, plu_product_type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> item_posIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item_pos
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param plu Codigo PLU del Articulo
	 * @param barcode Codigo de barras del Articulo
	 * @param desc_short Descripcion corta del Articulo
	 * @param plu_product_type Tipo de Articulo en Balanza
	 * @throws SQLException
	*/
	protected int insertItem_posOldIds( Integer oldId , Integer item , String plu , String barcode , String desc_short , Short plu_product_type )
	throws SQLException {
		
		Integer newId = this.item_posIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitem_posIds.get(null);
		newId = this.insertItem_pos( item,  plu,  barcode,  desc_short,  plu_product_type );
		item_posIds.put(oldId,newId);
		
		return newId;
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
	protected int insertItem_supplier( Integer item,  Integer supplier,  String code,  Short priority )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_supplier ( item, supplier, code, priority) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item_supplier ( item, supplier, code, priority) VALUES (  '"+ item +"', '"+ supplier +"', '"+ code +"', '"+ priority +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( supplier == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, supplier);
		}
		if ( code == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, code);
		}
		if ( priority == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, priority);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> item_supplierIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item_supplier
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param supplier Identificador de Proveedor
	 * @param code Codigo del Producto en el Proveedor
	 * @param priority Prioridad del Proveedor
	 * @throws SQLException
	*/
	protected int insertItem_supplierOldIds( Integer oldId , Integer item , Integer supplier , String code , Short priority )
	throws SQLException {
		
		Integer newId = this.item_supplierIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitem_supplierIds.get(null);
		newId = this.insertItem_supplier( item,  supplier,  code,  priority );
		item_supplierIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Item_tariff
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @param percentage Porcentaje de descuento sobre el precio del Articulo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertItem_tariff( Integer item,  Integer tariff,  Double percentage )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_tariff ( item, tariff, percentage) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item_tariff ( item, tariff, percentage) VALUES (  '"+ item +"', '"+ tariff +"', '"+ percentage +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( tariff == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, tariff);
		}
		if ( percentage == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, percentage);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> item_tariffIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item_tariff
	 * @param id Identificador unico de la Tarifa del Articulo
	 * @param item Identificador de Articulo
	 * @param tariff Identificador de Tarifa
	 * @param percentage Porcentaje de descuento sobre el precio del Articulo
	 * @throws SQLException
	*/
	protected int insertItem_tariffOldIds( Integer oldId , Integer item , Integer tariff , Double percentage )
	throws SQLException {
		
		Integer newId = this.item_tariffIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitem_tariffIds.get(null);
		newId = this.insertItem_tariff( item,  tariff,  percentage );
		item_tariffIds.put(oldId,newId);
		
		return newId;
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
	protected int insertItem_warehouse( Integer item,  Integer warehouse,  Double stock_max,  Double stock_min,  String location )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO item_warehouse ( item, warehouse, stock_max, stock_min, location) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO item_warehouse ( item, warehouse, stock_max, stock_min, location) VALUES (  '"+ item +"', '"+ warehouse +"', '"+ stock_max +"', '"+ stock_min +"', '"+ location +"')";
		LOGGER.debug(message);

		if ( item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, item);
		}
		if ( warehouse == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, warehouse);
		}
		if ( stock_max == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, stock_max);
		}
		if ( stock_min == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, stock_min);
		}
		if ( location == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, location);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> item_warehouseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Item_warehouse
	 * @param id Identificador unico
	 * @param item Identificador de Articulo
	 * @param warehouse Identificador de Almacen
	 * @param stock_max Stock maximo del Articulo en el Almacen
	 * @param stock_min Stock minimo del Articulo en el Almacen
	 * @param location Localizacion del Articulo en el Almacen
	 * @throws SQLException
	*/
	protected int insertItem_warehouseOldIds( Integer oldId , Integer item , Integer warehouse , Double stock_max , Double stock_min , String location )
	throws SQLException {
		
		Integer newId = this.item_warehouseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxitem_warehouseIds.get(null);
		newId = this.insertItem_warehouse( item,  warehouse,  stock_max,  stock_min,  location );
		item_warehouseIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Job_type
	 * @param description Descripcion del Tipo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertJob_type( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO job_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO job_type ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> job_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Job_type
	 * @param id Identificador unico del Tipo de Trabajo
	 * @param description Descripcion del Tipo de Trabajo
	 * @throws SQLException
	*/
	protected int insertJob_typeOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.job_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertJob_type( description );
		job_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLeasing( Date leasing_date,  String supplier_name,  String supplier_document,  String description,  String term,  String interest_percent,  String review,  Double amount,  Integer rbank,  Short security_level,  String fixed_asset_account,  Integer vat )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO leasing ( leasing_date, supplier_name, supplier_document, description, term, interest_percent, review, amount, rbank, security_level, fixed_asset_account, vat) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO leasing ( leasing_date, supplier_name, supplier_document, description, term, interest_percent, review, amount, rbank, security_level, fixed_asset_account, vat) VALUES (  '"+ leasing_date +"', '"+ supplier_name +"', '"+ supplier_document +"', '"+ description +"', '"+ term +"', '"+ interest_percent +"', '"+ review +"', '"+ amount +"', '"+ rbank +"', '"+ security_level +"', '"+ fixed_asset_account +"', '"+ vat +"')";
		LOGGER.debug(message);

		if ( leasing_date == null ) {
			stmt.setNull(1, 91);
		}
		else {
			stmt.setDate(1, leasing_date);
		}
		if ( supplier_name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, supplier_name);
		}
		if ( supplier_document == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, supplier_document);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( term == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, term);
		}
		if ( interest_percent == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, interest_percent);
		}
		if ( review == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, review);
		}
		if ( amount == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, amount);
		}
		if ( rbank == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, rbank);
		}
		if ( security_level == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, security_level);
		}
		if ( fixed_asset_account == null ) {
			stmt.setNull(11, 1);
		}
		else {
			stmt.setString(11, fixed_asset_account);
		}
		if ( vat == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, vat);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> leasingIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Leasing
	 * @param id Identificador Unico
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
	 * @throws SQLException
	*/
	protected int insertLeasingOldIds( Integer oldId , Date leasing_date , String supplier_name , String supplier_document , String description , String term , String interest_percent , String review , Double amount , Integer rbank , Short security_level , String fixed_asset_account , Integer vat )
	throws SQLException {
		
		Integer newId = this.leasingIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxleasingIds.get(null);
		newId = this.insertLeasing( leasing_date,  supplier_name,  supplier_document,  description,  term,  interest_percent,  review,  amount,  rbank,  security_level,  fixed_asset_account,  vat );
		leasingIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Leasing_account
	 * @param leasing Leasing
	 * @param account Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLeasing_account( Integer leasing,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO leasing_account ( leasing, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO leasing_account ( leasing, account) VALUES (  '"+ leasing +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( leasing == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, leasing);
		}
		if ( account == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> leasing_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Leasing_account
	 * @param id Identificador Unico
	 * @param leasing Leasing
	 * @param account Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertLeasing_accountOldIds( Integer oldId , Integer leasing , String account )
	throws SQLException {
		
		Integer newId = this.leasing_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxleasing_accountIds.get(null);
		newId = this.insertLeasing_account( leasing,  account );
		leasing_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLh_contract( Integer employee,  Date startingdate,  Date endingdate,  Short contract_type,  Double gross_salary )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_contract ( employee, startingdate, endingdate, contract_type, gross_salary) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO lh_contract ( employee, startingdate, endingdate, contract_type, gross_salary) VALUES (  '"+ employee +"', '"+ startingdate +"', '"+ endingdate +"', '"+ contract_type +"', '"+ gross_salary +"')";
		LOGGER.debug(message);

		if ( employee == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, endingdate);
		}
		if ( contract_type == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, contract_type);
		}
		if ( gross_salary == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, gross_salary);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> lh_contractIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Lh_contract
	 * @param id Identificador unico del Contrato
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Contrato
	 * @param endingdate Fecha de finalizacion del Contrato
	 * @param contract_type Tipo de Contrato
	 * @param gross_salary Salario bruto del Contrato
	 * @throws SQLException
	*/
	protected int insertLh_contractOldIds( Integer oldId , Integer employee , Date startingdate , Date endingdate , Short contract_type , Double gross_salary )
	throws SQLException {
		
		Integer newId = this.lh_contractIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxlh_contractIds.get(null);
		newId = this.insertLh_contract( employee,  startingdate,  endingdate,  contract_type,  gross_salary );
		lh_contractIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLh_course( Integer employee,  Date startingdate,  Date endingdate,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_course ( employee, startingdate, endingdate, description) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO lh_course ( employee, startingdate, endingdate, description) VALUES (  '"+ employee +"', '"+ startingdate +"', '"+ endingdate +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( employee == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> lh_courseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Lh_course
	 * @param id Identificador unico del Curso
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Curso
	 * @param endingdate Fecha de finalizacion del Curso
	 * @param description Descripcion del Curso
	 * @throws SQLException
	*/
	protected int insertLh_courseOldIds( Integer oldId , Integer employee , Date startingdate , Date endingdate , String description )
	throws SQLException {
		
		Integer newId = this.lh_courseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxlh_courseIds.get(null);
		newId = this.insertLh_course( employee,  startingdate,  endingdate,  description );
		lh_courseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLh_position( Integer employee,  Date startingdate,  Date endingdate,  String description,  Integer workplace,  Integer workactivity,  Integer calendar )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_position ( employee, startingdate, endingdate, description, workplace, workactivity, calendar) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO lh_position ( employee, startingdate, endingdate, description, workplace, workactivity, calendar) VALUES (  '"+ employee +"', '"+ startingdate +"', '"+ endingdate +"', '"+ description +"', '"+ workplace +"', '"+ workactivity +"', '"+ calendar +"')";
		LOGGER.debug(message);

		if ( employee == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( workplace == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, workplace);
		}
		if ( workactivity == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, workactivity);
		}
		if ( calendar == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, calendar);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> lh_positionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Lh_position
	 * @param id Identificador unico del Cargo
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Cargo
	 * @param endingdate Fecha de finalizacion del Cargo
	 * @param description Descripcion del Cargo
	 * @param workplace Identificador del Centro de Trabajo
	 * @param workactivity Identificador de la Actividad
	 * @param calendar Identificador del Calendario Laboral
	 * @throws SQLException
	*/
	protected int insertLh_positionOldIds( Integer oldId , Integer employee , Date startingdate , Date endingdate , String description , Integer workplace , Integer workactivity , Integer calendar )
	throws SQLException {
		
		Integer newId = this.lh_positionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxlh_positionIds.get(null);
		newId = this.insertLh_position( employee,  startingdate,  endingdate,  description,  workplace,  workactivity,  calendar );
		lh_positionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLh_work( Integer employee,  Date startingdate,  Date endingdate,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO lh_work ( employee, startingdate, endingdate, description) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO lh_work ( employee, startingdate, endingdate, description) VALUES (  '"+ employee +"', '"+ startingdate +"', '"+ endingdate +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( employee == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, employee);
		}
		if ( startingdate == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, endingdate);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> lh_workIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Lh_work
	 * @param id Identificador unico del Trabajo
	 * @param employee Identificador del Empleado
	 * @param startingdate Fecha de inicio del Trabajo
	 * @param endingdate Fecha de finalizacion del Trabajo
	 * @param description Descripcion del Trabajo
	 * @throws SQLException
	*/
	protected int insertLh_workOldIds( Integer oldId , Integer employee , Date startingdate , Date endingdate , String description )
	throws SQLException {
		
		Integer newId = this.lh_workIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxlh_workIds.get(null);
		newId = this.insertLh_work( employee,  startingdate,  endingdate,  description );
		lh_workIds.put(oldId,newId);
		
		return newId;
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
	protected int insertLoan( String description,  Date loan_date,  String term,  String interest,  String review,  Double amount,  Double expenses,  Integer rbank,  Short security_level,  Double fee_amount,  Integer recurrence,  Integer pay_day,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO loan ( description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO loan ( description, loan_date, term, interest, review, amount, expenses, rbank, security_level, fee_amount, recurrence, pay_day, status) VALUES (  '"+ description +"', '"+ loan_date +"', '"+ term +"', '"+ interest +"', '"+ review +"', '"+ amount +"', '"+ expenses +"', '"+ rbank +"', '"+ security_level +"', '"+ fee_amount +"', '"+ recurrence +"', '"+ pay_day +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( loan_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, loan_date);
		}
		if ( term == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, term);
		}
		if ( interest == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, interest);
		}
		if ( review == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, review);
		}
		if ( amount == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, amount);
		}
		if ( expenses == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, expenses);
		}
		if ( rbank == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, rbank);
		}
		if ( security_level == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, security_level);
		}
		if ( fee_amount == null ) {
			stmt.setNull(10, 8);
		}
		else {
			stmt.setDouble(10, fee_amount);
		}
		if ( recurrence == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, recurrence);
		}
		if ( pay_day == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, pay_day);
		}
		if ( status == null ) {
			stmt.setNull(13, -6);
		}
		else {
			stmt.setShort(13, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> loanIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertLoanOldIds( Integer oldId , String description , Date loan_date , String term , String interest , String review , Double amount , Double expenses , Integer rbank , Short security_level , Double fee_amount , Integer recurrence , Integer pay_day , Short status )
	throws SQLException {
		
		Integer newId = this.loanIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxloanIds.get(null);
		newId = this.insertLoan( description,  loan_date,  term,  interest,  review,  amount,  expenses,  rbank,  security_level,  fee_amount,  recurrence,  pay_day,  status );
		loanIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Loan_account
	 * @param loan Prestamo
	 * @param account Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertLoan_account( Integer loan,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO loan_account ( loan, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO loan_account ( loan, account) VALUES (  '"+ loan +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( loan == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, loan);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> loan_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Loan_account
	 * @param id Identificador Unico
	 * @param loan Prestamo
	 * @param account Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertLoan_accountOldIds( Integer oldId , Integer loan , String account )
	throws SQLException {
		
		Integer newId = this.loan_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxloan_accountIds.get(null);
		newId = this.insertLoan_account( loan,  account );
		loan_accountIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Make
	 * @param name Nombre del Fabricante
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMake( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO make ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO make ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> makeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Make
	 * @param id Identificador unico del Fabricante
	 * @param name Nombre del Fabricante
	 * @throws SQLException
	*/
	protected int insertMakeOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.makeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertMake( name );
		makeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertMark( Integer subject,  Integer alumn,  Short evaluation,  Double mark )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mark ( subject, alumn, evaluation, mark) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO mark ( subject, alumn, evaluation, mark) VALUES (  '"+ subject +"', '"+ alumn +"', '"+ evaluation +"', '"+ mark +"')";
		LOGGER.debug(message);

		if ( subject == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, subject);
		}
		if ( alumn == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, alumn);
		}
		if ( evaluation == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, evaluation);
		}
		if ( mark == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, mark);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> markIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Mark
	 * @param id Identificador unico
	 * @param subject Identificador de Asignatura
	 * @param alumn Identificador de Alumno
	 * @param evaluation Numero de evaluacion
	 * @param mark Nota
	 * @throws SQLException
	*/
	protected int insertMarkOldIds( Integer oldId , Integer subject , Integer alumn , Short evaluation , Double mark )
	throws SQLException {
		
		Integer newId = this.markIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxmarkIds.get(null);
		newId = this.insertMark( subject,  alumn,  evaluation,  mark );
		markIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Message_content
	 * @param content Contenido del Mensaje
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMessage_content( InputStream content )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO message_content ( content) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO message_content ( content) VALUES (  '"+ content +"')";
		LOGGER.debug(message);

		if ( content == null ) {
			stmt.setNull(1, -1);
		}
		else {
			stmt.setAsciiStream(1, content);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> message_contentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Message_content
	 * @param id Identificador unico
	 * @param content Contenido del Mensaje
	 * @throws SQLException
	*/
	protected int insertMessage_contentOldIds( Integer oldId , InputStream content )
	throws SQLException {
		
		Integer newId = this.message_contentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertMessage_content( content );
		message_contentIds.put(oldId,newId);
		
		return newId;
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
	protected int insertMessage_log( String message_id,  Integer message_content,  String recipient,  String type,  Timestamp sent_date,  Short message_parts,  String username )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO message_log ( message_id, message_content, recipient, type, sent_date, message_parts, username) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO message_log ( message_id, message_content, recipient, type, sent_date, message_parts, username) VALUES (  '"+ message_id +"', '"+ message_content +"', '"+ recipient +"', '"+ type +"', '"+ sent_date +"', '"+ message_parts +"', '"+ username +"')";
		LOGGER.debug(message);

		if ( message_id == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, message_id);
		}
		if ( message_content == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, message_content);
		}
		if ( recipient == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, recipient);
		}
		if ( type == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, type);
		}
		if ( sent_date == null ) {
			stmt.setNull(5, 93);
		}
		else {
			stmt.setTimestamp(5, sent_date);
		}
		if ( message_parts == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, message_parts);
		}
		if ( username == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, username);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> message_logIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Message_log
	 * @param id Identificador unico
	 * @param message_id Identificador del Mensaje para el servidor de Esendex
	 * @param message_content Identificador del Contenido del Mensaje
	 * @param recipient Destinatario del Mensaje
	 * @param type Tipo de Mensaje
	 * @param sent_date Fecha y hora de envio del Mensaje
	 * @param message_parts Numero de partes que componen el Mensaje
	 * @param username Usuario que envia el mensaje
	 * @throws SQLException
	*/
	protected int insertMessage_logOldIds( Integer oldId , String message_id , Integer message_content , String recipient , String type , Timestamp sent_date , Short message_parts , String username )
	throws SQLException {
		
		Integer newId = this.message_logIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxmessage_logIds.get(null);
		newId = this.insertMessage_log( message_id,  message_content,  recipient,  type,  sent_date,  message_parts,  username );
		message_logIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Mk_action
	 * @param campaign Identificador de la Campaña
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action( Integer campaign,  Integer media_type,  Timestamp start_date,  Timestamp end_date,  Integer survey )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_action ( campaign, media_type, start_date, end_date, survey) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO mk_action ( campaign, media_type, start_date, end_date, survey) VALUES (  '"+ campaign +"', '"+ media_type +"', '"+ start_date +"', '"+ end_date +"', '"+ survey +"')";
		LOGGER.debug(message);

		if ( campaign == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, campaign);
		}
		if ( media_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, media_type);
		}
		if ( start_date == null ) {
			stmt.setNull(3, 93);
		}
		else {
			stmt.setTimestamp(3, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(4, 93);
		}
		else {
			stmt.setTimestamp(4, end_date);
		}
		if ( survey == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, survey);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> mk_actionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Mk_action
	 * @param id Identificador unico
	 * @param campaign Identificador de la Campaña
	 * @param media_type Tipo de contacto de la Accion
	 * @param start_date Fecha de inicio
	 * @param end_date Fecha de finalizacion
	 * @param survey Identificador del Cuestionario
	 * @throws SQLException
	*/
	protected int insertMk_actionOldIds( Integer oldId , Integer campaign , Integer media_type , Timestamp start_date , Timestamp end_date , Integer survey )
	throws SQLException {
		
		Integer newId = this.mk_actionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxmk_actionIds.get(null);
		newId = this.insertMk_action( campaign,  media_type,  start_date,  end_date,  survey );
		mk_actionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Mk_action_target
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de Campaña
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_action_target( Integer action,  Integer target,  Short status,  Integer survey_response )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_action_target ( action, target, status, survey_response) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO mk_action_target ( action, target, status, survey_response) VALUES (  '"+ action +"', '"+ target +"', '"+ status +"', '"+ survey_response +"')";
		LOGGER.debug(message);

		if ( action == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, action);
		}
		if ( target == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, target);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}
		if ( survey_response == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, survey_response);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> mk_action_targetIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Mk_action_target
	 * @param id Identificador unico
	 * @param action Identificador de la Accion
	 * @param target Identificador del Cliente Potencial
	 * @param status Estado del Cliente Potencial de la Accion de Campaña
	 * @param survey_response Identificador de la Respuesta de Cuestionario
	 * @throws SQLException
	*/
	protected int insertMk_action_targetOldIds( Integer oldId , Integer action , Integer target , Short status , Integer survey_response )
	throws SQLException {
		
		Integer newId = this.mk_action_targetIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxmk_action_targetIds.get(null);
		newId = this.insertMk_action_target( action,  target,  status,  survey_response );
		mk_action_targetIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Mk_campaign
	 * @param active Indica si la Campaña esta activa o no
	 * @param description Descripcion de la Campaña
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertMk_campaign( Boolean active,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO mk_campaign ( active, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO mk_campaign ( active, description) VALUES (  '"+ active +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( active == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, active);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> mk_campaignIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Mk_campaign
	 * @param id Identificador unico
	 * @param active Indica si la Campaña esta activa o no
	 * @param description Descripcion de la Campaña
	 * @throws SQLException
	*/
	protected int insertMk_campaignOldIds( Integer oldId , Boolean active , String description )
	throws SQLException {
		
		Integer newId = this.mk_campaignIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertMk_campaign( active,  description );
		mk_campaignIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Model
	 * @param make Identificador del Fabricante
	 * @param name Nombre del Modelo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertModel( Integer make,  String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO model ( make, name) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO model ( make, name) VALUES (  '"+ make +"', '"+ name +"')";
		LOGGER.debug(message);

		if ( make == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, make);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> modelIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Model
	 * @param id Identificador unico del Modelo
	 * @param make Identificador del Fabricante
	 * @param name Nombre del Modelo
	 * @throws SQLException
	*/
	protected int insertModelOldIds( Integer oldId , Integer make , String name )
	throws SQLException {
		
		Integer newId = this.modelIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxmodelIds.get(null);
		newId = this.insertModel( make,  name );
		modelIds.put(oldId,newId);
		
		return newId;
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
	protected int insertNote( String subject,  Timestamp date,  Integer owner,  InputStream note )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO note ( subject, date, owner, note) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO note ( subject, date, owner, note) VALUES (  '"+ subject +"', '"+ date +"', '"+ owner +"', '"+ note +"')";
		LOGGER.debug(message);

		if ( subject == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, subject);
		}
		if ( date == null ) {
			stmt.setNull(2, 93);
		}
		else {
			stmt.setTimestamp(2, date);
		}
		if ( owner == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, owner);
		}
		if ( note == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, note);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> noteIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Note
	 * @param id Identificador unico de la Nota
	 * @param subject Descripcion corta de la Nota
	 * @param date Fecha de la Nota
	 * @param owner Destinatario de la Nota
	 * @param note Texto de la Nota
	 * @throws SQLException
	*/
	protected int insertNoteOldIds( Integer oldId , String subject , Timestamp date , Integer owner , InputStream note )
	throws SQLException {
		
		Integer newId = this.noteIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxnoteIds.get(null);
		newId = this.insertNote( subject,  date,  owner,  note );
		noteIds.put(oldId,newId);
		
		return newId;
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
	protected int insertNotice( Timestamp date,  Integer sender,  Integer work_group,  Integer recipient,  String source,  String company,  String phone,  InputStream subject,  Short status,  Short type,  Short priority )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO notice ( date, sender, work_group, recipient, source, company, phone, subject, status, type, priority) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO notice ( date, sender, work_group, recipient, source, company, phone, subject, status, type, priority) VALUES (  '"+ date +"', '"+ sender +"', '"+ work_group +"', '"+ recipient +"', '"+ source +"', '"+ company +"', '"+ phone +"', '"+ subject +"', '"+ status +"', '"+ type +"', '"+ priority +"')";
		LOGGER.debug(message);

		if ( date == null ) {
			stmt.setNull(1, 93);
		}
		else {
			stmt.setTimestamp(1, date);
		}
		if ( sender == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, sender);
		}
		if ( work_group == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, work_group);
		}
		if ( recipient == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, recipient);
		}
		if ( source == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, source);
		}
		if ( company == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, company);
		}
		if ( phone == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, phone);
		}
		if ( subject == null ) {
			stmt.setNull(8, -1);
		}
		else {
			stmt.setAsciiStream(8, subject);
		}
		if ( status == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, status);
		}
		if ( type == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, type);
		}
		if ( priority == null ) {
			stmt.setNull(11, -6);
		}
		else {
			stmt.setShort(11, priority);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> noticeIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertNoticeOldIds( Integer oldId , Timestamp date , Integer sender , Integer work_group , Integer recipient , String source , String company , String phone , InputStream subject , Short status , Short type , Short priority )
	throws SQLException {
		
		Integer newId = this.noticeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxnoticeIds.get(null);
		newId = this.insertNotice( date,  sender,  work_group,  recipient,  source,  company,  phone,  subject,  status,  type,  priority );
		noticeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Observation
	 * @param description Descripcion de la Observacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertObservation( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO observation ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO observation ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> observationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Observation
	 * @param id Identificador unico de la Observacion
	 * @param description Descripcion de la Observacion
	 * @throws SQLException
	*/
	protected int insertObservationOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.observationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertObservation( description );
		observationIds.put(oldId,newId);
		
		return newId;
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
	protected int insertOffer( Integer target,  String series,  Integer number,  Integer version,  Integer address,  Integer tariff,  Integer seller,  Integer supplier,  String discount_expr,  Date issue_date,  Integer pay_method,  Short security_level,  Short status,  Short type,  Integer workplace,  Integer scope,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account,  Boolean signed,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer ( target, series, number, version, address, tariff, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account, signed, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO offer ( target, series, number, version, address, tariff, seller, supplier, discount_expr, issue_date, pay_method, security_level, status, type, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account, signed, comments) VALUES (  '"+ target +"', '"+ series +"', '"+ number +"', '"+ version +"', '"+ address +"', '"+ tariff +"', '"+ seller +"', '"+ supplier +"', '"+ discount_expr +"', '"+ issue_date +"', '"+ pay_method +"', '"+ security_level +"', '"+ status +"', '"+ type +"', '"+ workplace +"', '"+ scope +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"', '"+ signed +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( series == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, series);
		}
		if ( number == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, number);
		}
		if ( version == null ) {
			stmt.setNull(4, 5);
		}
		else {
			stmt.setInt(4, version);
		}
		if ( address == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, address);
		}
		if ( tariff == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, tariff);
		}
		if ( seller == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, seller);
		}
		if ( supplier == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, supplier);
		}
		if ( discount_expr == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, discount_expr);
		}
		if ( issue_date == null ) {
			stmt.setNull(10, 91);
		}
		else {
			stmt.setDate(10, issue_date);
		}
		if ( pay_method == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, pay_method);
		}
		if ( security_level == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, security_level);
		}
		if ( status == null ) {
			stmt.setNull(13, -6);
		}
		else {
			stmt.setShort(13, status);
		}
		if ( type == null ) {
			stmt.setNull(14, -6);
		}
		else {
			stmt.setShort(14, type);
		}
		if ( workplace == null ) {
			stmt.setNull(15, 4);
		}
		else {
			stmt.setInt(15, workplace);
		}
		if ( scope == null ) {
			stmt.setNull(16, 4);
		}
		else {
			stmt.setInt(16, scope);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(17, 5);
		}
		else {
			stmt.setInt(17, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(18, 5);
		}
		else {
			stmt.setInt(18, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(19, 5);
		}
		else {
			stmt.setInt(19, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(20, 12);
		}
		else {
			stmt.setString(20, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(21, 4);
		}
		else {
			stmt.setInt(21, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(22, 12);
		}
		else {
			stmt.setString(22, bank_account);
		}
		if ( signed == null ) {
			stmt.setNull(23, -7);
		}
		else {
			stmt.setBoolean(23, signed);
		}
		if ( comments == null ) {
			stmt.setNull(24, -1);
		}
		else {
			stmt.setAsciiStream(24, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> offerIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Offer
	 * @param id Identificador unico del Presupuesto
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
	 * @throws SQLException
	*/
	protected int insertOfferOldIds( Integer oldId , Integer target , String series , Integer number , Integer version , Integer address , Integer tariff , Integer seller , Integer supplier , String discount_expr , Date issue_date , Integer pay_method , Short security_level , Short status , Short type , Integer workplace , Integer scope , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account , Boolean signed , InputStream comments )
	throws SQLException {
		
		Integer newId = this.offerIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxofferIds.get(null);
		newId = this.insertOffer( target,  series,  number,  version,  address,  tariff,  seller,  supplier,  discount_expr,  issue_date,  pay_method,  security_level,  status,  type,  workplace,  scope,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account,  signed,  comments );
		offerIds.put(oldId,newId);
		
		return newId;
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
	protected int insertOffer_attach( Integer offer,  Short mimeType,  String description,  InputStream data )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_attach ( offer, mimeType, description, data) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO offer_attach ( offer, mimeType, description, data) VALUES (  '"+ offer +"', '"+ mimeType +"', '"+ description +"', '"+ data +"')";
		LOGGER.debug(message);

		if ( offer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, offer);
		}
		if ( mimeType == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, mimeType);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( data == null ) {
			stmt.setNull(4, -4);
		}
		else {
			stmt.setBinaryStream(4, data);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> offer_attachIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Offer_attach
	 * @param id Identificador unico
	 * @param offer Identificador del Presupuesto
	 * @param mimeType Mime Type del Archivo Adjunto
	 * @param description Descripcion del Archivo Adjunto
	 * @param data Archivo Adjunto en binario
	 * @throws SQLException
	*/
	protected int insertOffer_attachOldIds( Integer oldId , Integer offer , Short mimeType , String description , InputStream data )
	throws SQLException {
		
		Integer newId = this.offer_attachIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxoffer_attachIds.get(null);
		newId = this.insertOffer_attach( offer,  mimeType,  description,  data );
		offer_attachIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Offer_detail
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de línea del Detalle dentro del Presupuesto
	 * @param item Identificador del Articulo
	 * @param description Descripción del Articulo
	 * @param quantity Cantidad del Articulo
	 * @param price Precio del Articulo
	 * @param discount_expr Descuentos del Articulo
	 * @param status Estado del Detalle del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertOffer_detail( Integer offer,  Integer line,  Integer item,  String description,  Double quantity,  Double price,  String discount_expr,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_detail ( offer, line, item, description, quantity, price, discount_expr, status) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO offer_detail ( offer, line, item, description, quantity, price, discount_expr, status) VALUES (  '"+ offer +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( offer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, offer);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, discount_expr);
		}
		if ( status == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> offer_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Offer_detail
	 * @param id Identificador unico del Detalle de Presupuesto
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de línea del Detalle dentro del Presupuesto
	 * @param item Identificador del Articulo
	 * @param description Descripción del Articulo
	 * @param quantity Cantidad del Articulo
	 * @param price Precio del Articulo
	 * @param discount_expr Descuentos del Articulo
	 * @param status Estado del Detalle del Presupuesto
	 * @throws SQLException
	*/
	protected int insertOffer_detailOldIds( Integer oldId , Integer offer , Integer line , Integer item , String description , Double quantity , Double price , String discount_expr , Short status )
	throws SQLException {
		
		Integer newId = this.offer_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxoffer_detailIds.get(null);
		newId = this.insertOffer_detail( offer,  line,  item,  description,  quantity,  price,  discount_expr,  status );
		offer_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertOffer_detail_commission( Integer offer_detail,  Double commission,  Double amount,  Short status,  Date pay_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_detail_commission ( offer_detail, commission, amount, status, pay_date) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO offer_detail_commission ( offer_detail, commission, amount, status, pay_date) VALUES (  '"+ offer_detail +"', '"+ commission +"', '"+ amount +"', '"+ status +"', '"+ pay_date +"')";
		LOGGER.debug(message);

		if ( offer_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, offer_detail);
		}
		if ( commission == null ) {
			stmt.setNull(2, 8);
		}
		else {
			stmt.setDouble(2, commission);
		}
		if ( amount == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, amount);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}
		if ( pay_date == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, pay_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> offer_detail_commissionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Offer_detail_commission
	 * @param id Identificador unico
	 * @param offer_detail Identificador de la Linea de Presupuesto
	 * @param commission Porcentaje de Comision
	 * @param amount Importe de la Comision
	 * @param status Estado de la Comision
	 * @param pay_date Fecha de liquidacion
	 * @throws SQLException
	*/
	protected int insertOffer_detail_commissionOldIds( Integer oldId , Integer offer_detail , Double commission , Double amount , Short status , Date pay_date )
	throws SQLException {
		
		Integer newId = this.offer_detail_commissionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxoffer_detail_commissionIds.get(null);
		newId = this.insertOffer_detail_commission( offer_detail,  commission,  amount,  status,  pay_date );
		offer_detail_commissionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertOffer_term( Integer offer,  Integer line,  String name,  InputStream description,  Boolean term_general )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO offer_term ( offer, line, name, description, term_general) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO offer_term ( offer, line, name, description, term_general) VALUES (  '"+ offer +"', '"+ line +"', '"+ name +"', '"+ description +"', '"+ term_general +"')";
		LOGGER.debug(message);

		if ( offer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, offer);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( name == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, name);
		}
		if ( description == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, description);
		}
		if ( term_general == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, term_general);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> offer_termIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Offer_term
	 * @param id Identificador unico
	 * @param offer Identificador del Presupuesto
	 * @param line Numero de linea de la Condicion del Presupuesto
	 * @param name Nombre de la Condicion Comercial
	 * @param description Descripcion de la Condicion Comercial
	 * @param term_general Indica si la Condicion es particular o general
	 * @throws SQLException
	*/
	protected int insertOffer_termOldIds( Integer oldId , Integer offer , Integer line , String name , InputStream description , Boolean term_general )
	throws SQLException {
		
		Integer newId = this.offer_termIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxoffer_termIds.get(null);
		newId = this.insertOffer_term( offer,  line,  name,  description,  term_general );
		offer_termIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pay_method
	 * @param name Nombre de Forma de Pago
	 * @param type Tipo de Forma de Pago
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPay_method( String name,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pay_method ( name, type) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pay_method ( name, type) VALUES (  '"+ name +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pay_methodIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pay_method
	 * @param id Identificador unico de la Forma de Pago
	 * @param name Nombre de Forma de Pago
	 * @param type Tipo de Forma de Pago
	 * @throws SQLException
	*/
	protected int insertPay_methodOldIds( Integer oldId , String name , Short type )
	throws SQLException {
		
		Integer newId = this.pay_methodIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertPay_method( name,  type );
		pay_methodIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pcategory
	 * @param name Nombre de la Categoria
	 * @param detail_pattern Patron para los detalles de Articulos
	 * @param pcategory_group Identificador del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory( String name,  String detail_pattern,  Integer pcategory_group )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory ( name, detail_pattern, pcategory_group) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pcategory ( name, detail_pattern, pcategory_group) VALUES (  '"+ name +"', '"+ detail_pattern +"', '"+ pcategory_group +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( detail_pattern == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, detail_pattern);
		}
		if ( pcategory_group == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, pcategory_group);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pcategoryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pcategory
	 * @param id Identificador unico de la Categoria
	 * @param name Nombre de la Categoria
	 * @param detail_pattern Patron para los detalles de Articulos
	 * @param pcategory_group Identificador del Grupo de Categorias
	 * @throws SQLException
	*/
	protected int insertPcategoryOldIds( Integer oldId , String name , String detail_pattern , Integer pcategory_group )
	throws SQLException {
		
		Integer newId = this.pcategoryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxpcategoryIds.get(null);
		newId = this.insertPcategory( name,  detail_pattern,  pcategory_group );
		pcategoryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pcategory_group
	 * @param name Nombre del Grupo de Categorias
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_group( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory_group ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pcategory_group ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pcategory_groupIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pcategory_group
	 * @param id Identificador unico del Grupo de Categorias
	 * @param name Nombre del Grupo de Categorias
	 * @throws SQLException
	*/
	protected int insertPcategory_groupOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.pcategory_groupIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertPcategory_group( name );
		pcategory_groupIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pcategory_tree
	 * @param parent Identificador de la Categoria padre
	 * @param child Identificador de la Categoria hijo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPcategory_tree( Integer parent,  Integer child )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pcategory_tree ( parent, child) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pcategory_tree ( parent, child) VALUES (  '"+ parent +"', '"+ child +"')";
		LOGGER.debug(message);

		if ( parent == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, parent);
		}
		if ( child == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, child);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pcategory_treeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pcategory_tree
	 * @param id Identificador unico del Nodo del Arbol de Categorias
	 * @param parent Identificador de la Categoria padre
	 * @param child Identificador de la Categoria hijo
	 * @throws SQLException
	*/
	protected int insertPcategory_treeOldIds( Integer oldId , Integer parent , Integer child )
	throws SQLException {
		
		Integer newId = this.pcategory_treeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxpcategory_treeIds.get(null);
		newId = this.insertPcategory_tree( parent,  child );
		pcategory_treeIds.put(oldId,newId);
		
		return newId;
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
	protected void insertPerson( Integer registry,  Date birth_date,  Short gender,  Short marital_status,  String social_security_num )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO person ( registry, birth_date, gender, marital_status, social_security_num) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO person ( registry, birth_date, gender, marital_status, social_security_num) VALUES ( '"+ registry +"', '"+ birth_date +"', '"+ gender +"', '"+ marital_status +"', '"+ social_security_num +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( birth_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, birth_date);
		}
		if ( gender == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, gender);
		}
		if ( marital_status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, marital_status);
		}
		if ( social_security_num == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, social_security_num);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Pm_type_detail
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail( Short type,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pm_type_detail ( type, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pm_type_detail ( type, description) VALUES (  '"+ type +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( type == null ) {
			stmt.setNull(1, -6);
		}
		else {
			stmt.setShort(1, type);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pm_type_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pm_type_detail
	 * @param id Identificador unico
	 * @param type Tipo de Forma de Pago
	 * @param description Descripcion del detalle
	 * @throws SQLException
	*/
	protected int insertPm_type_detailOldIds( Integer oldId , Short type , String description )
	throws SQLException {
		
		Integer newId = this.pm_type_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertPm_type_detail( type,  description );
		pm_type_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pm_type_detail_account
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPm_type_detail_account( Integer pm_type_detail,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pm_type_detail_account ( pm_type_detail, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pm_type_detail_account ( pm_type_detail, account) VALUES (  '"+ pm_type_detail +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( pm_type_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, pm_type_detail);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> pm_type_detail_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pm_type_detail_account
	 * @param id Identificador unico
	 * @param pm_type_detail Identificador del Detalle por Tipo de Forma de Pago
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertPm_type_detail_accountOldIds( Integer oldId , Integer pm_type_detail , String account )
	throws SQLException {
		
		Integer newId = this.pm_type_detail_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxpm_type_detail_accountIds.get(null);
		newId = this.insertPm_type_detail_account( pm_type_detail,  account );
		pm_type_detail_accountIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Pos
	 * @param description Descripcion del Centro de Venta
	 * @param raddress Identificador de la Direccion asociada al Centro de Venta
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertPos( String description,  Integer raddress )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO pos ( description, raddress) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO pos ( description, raddress) VALUES (  '"+ description +"', '"+ raddress +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( raddress == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, raddress);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> posIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Pos
	 * @param id Identificador unico del Centro de Venta
	 * @param description Descripcion del Centro de Venta
	 * @param raddress Identificador de la Direccion asociada al Centro de Venta
	 * @throws SQLException
	*/
	protected int insertPosOldIds( Integer oldId , String description , Integer raddress )
	throws SQLException {
		
		Integer newId = this.posIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxposIds.get(null);
		newId = this.insertPos( description,  raddress );
		posIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Process
	 * @param description Descripcion del Proceso.
	 * @param status Estado del Proceso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess( String description,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process ( description, status) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO process ( description, status) VALUES (  '"+ description +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( status == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> processIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Process
	 * @param id Identificador unico del Proceso
	 * @param description Descripcion del Proceso.
	 * @param status Estado del Proceso
	 * @throws SQLException
	*/
	protected int insertProcessOldIds( Integer oldId , String description , Short status )
	throws SQLException {
		
		Integer newId = this.processIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertProcess( description,  status );
		processIds.put(oldId,newId);
		
		return newId;
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
	protected int insertProcess_detail( Integer process,  String description,  Integer position,  Short date_reference,  Integer days,  Integer alert_days,  Integer workgroup,  Short priority,  Short status,  InputStream comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_detail ( process, description, position, date_reference, days, alert_days, workgroup, priority, status, comments) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO process_detail ( process, description, position, date_reference, days, alert_days, workgroup, priority, status, comments) VALUES (  '"+ process +"', '"+ description +"', '"+ position +"', '"+ date_reference +"', '"+ days +"', '"+ alert_days +"', '"+ workgroup +"', '"+ priority +"', '"+ status +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( process == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, process);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( position == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, position);
		}
		if ( date_reference == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, date_reference);
		}
		if ( days == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, days);
		}
		if ( alert_days == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, alert_days);
		}
		if ( workgroup == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, workgroup);
		}
		if ( priority == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, priority);
		}
		if ( status == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, status);
		}
		if ( comments == null ) {
			stmt.setNull(10, -1);
		}
		else {
			stmt.setAsciiStream(10, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> process_detailIds = 
		new HashMap<Integer, Integer>();
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
	 * @param status Estado de la Accion
	 * @param comments Comentarios del Detalle de Proceso
	 * @throws SQLException
	*/
	protected int insertProcess_detailOldIds( Integer oldId , Integer process , String description , Integer position , Short date_reference , Integer days , Integer alert_days , Integer workgroup , Short priority , Short status , InputStream comments )
	throws SQLException {
		
		Integer newId = this.process_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxprocess_detailIds.get(null);
		newId = this.insertProcess_detail( process,  description,  position,  date_reference,  days,  alert_days,  workgroup,  priority,  status,  comments );
		process_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Process_detail_transition
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_detail_transition( Integer process_detail,  Integer process_transition_type,  Integer next_process_detail )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_detail_transition ( process_detail, process_transition_type, next_process_detail) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO process_detail_transition ( process_detail, process_transition_type, next_process_detail) VALUES (  '"+ process_detail +"', '"+ process_transition_type +"', '"+ next_process_detail +"')";
		LOGGER.debug(message);

		if ( process_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, process_detail);
		}
		if ( process_transition_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, process_transition_type);
		}
		if ( next_process_detail == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, next_process_detail);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> process_detail_transitionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Process_detail_transition
	 * @param id Identificador unico
	 * @param process_detail Identificador del Detalle del Proceso.
	 * @param process_transition_type Identificador del Tipo de Transicion.
	 * @param next_process_detail Identificador del siguiente Detalle del Proceso.
	 * @throws SQLException
	*/
	protected int insertProcess_detail_transitionOldIds( Integer oldId , Integer process_detail , Integer process_transition_type , Integer next_process_detail )
	throws SQLException {
		
		Integer newId = this.process_detail_transitionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxprocess_detail_transitionIds.get(null);
		newId = this.insertProcess_detail_transition( process_detail,  process_transition_type,  next_process_detail );
		process_detail_transitionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Process_transition_type
	 * @param description Descripcion del Tipo de Transicion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProcess_transition_type( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO process_transition_type ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO process_transition_type ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> process_transition_typeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Process_transition_type
	 * @param id Identificador unico
	 * @param description Descripcion del Tipo de Transicion
	 * @throws SQLException
	*/
	protected int insertProcess_transition_typeOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.process_transition_typeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertProcess_transition_type( description );
		process_transition_typeIds.put(oldId,newId);
		
		return newId;
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
	protected int insertProduct( String name,  String code,  Integer brand,  Integer category,  Boolean inventoriable,  Short status,  Integer vat,  Integer retention,  Short type,  Boolean composition )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO product ( name, code, brand, category, inventoriable, status, vat, retention, type, composition) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO product ( name, code, brand, category, inventoriable, status, vat, retention, type, composition) VALUES (  '"+ name +"', '"+ code +"', '"+ brand +"', '"+ category +"', '"+ inventoriable +"', '"+ status +"', '"+ vat +"', '"+ retention +"', '"+ type +"', '"+ composition +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( code == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, code);
		}
		if ( brand == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, brand);
		}
		if ( category == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, category);
		}
		if ( inventoriable == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, inventoriable);
		}
		if ( status == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, status);
		}
		if ( vat == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, vat);
		}
		if ( retention == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, retention);
		}
		if ( type == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, type);
		}
		if ( composition == null ) {
			stmt.setNull(10, -7);
		}
		else {
			stmt.setBoolean(10, composition);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> productIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Product
	 * @param id Identificador unico del Producto
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
	 * @throws SQLException
	*/
	protected int insertProductOldIds( Integer oldId , String name , String code , Integer brand , Integer category , Boolean inventoriable , Short status , Integer vat , Integer retention , Short type , Boolean composition )
	throws SQLException {
		
		Integer newId = this.productIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxproductIds.get(null);
		newId = this.insertProduct( name,  code,  brand,  category,  inventoriable,  status,  vat,  retention,  type,  composition );
		productIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Product_account
	 * @param product Identificador del Producto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Producto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertProduct_account( Integer product,  String account,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO product_account ( product, account, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO product_account ( product, account, type) VALUES (  '"+ product +"', '"+ account +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( product == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, product);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> product_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Product_account
	 * @param id Identificador unico de la Cuenta Contable del Producto
	 * @param product Identificador del Producto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Producto
	 * @throws SQLException
	*/
	protected int insertProduct_accountOldIds( Integer oldId , Integer product , String account , Short type )
	throws SQLException {
		
		Integer newId = this.product_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxproduct_accountIds.get(null);
		newId = this.insertProduct_account( product,  account,  type );
		product_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertProduction( String description,  String lot_code,  Date production_date,  Integer item,  Double initial_quantity,  Double quantity,  Double price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production ( description, lot_code, production_date, item, initial_quantity, quantity, price) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO production ( description, lot_code, production_date, item, initial_quantity, quantity, price) VALUES (  '"+ description +"', '"+ lot_code +"', '"+ production_date +"', '"+ item +"', '"+ initial_quantity +"', '"+ quantity +"', '"+ price +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( lot_code == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, lot_code);
		}
		if ( production_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, production_date);
		}
		if ( item == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, item);
		}
		if ( initial_quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, initial_quantity);
		}
		if ( quantity == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, quantity);
		}
		if ( price == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> productionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Production
	 * @param id Identificador unico de la Produccion
	 * @param description Descripcion de la Produccion
	 * @param lot_code Codigo de lote de la Produccion
	 * @param production_date Fecha de Produccion
	 * @param item Identificador del Articulo producido
	 * @param initial_quantity Cantidad inicial del Articulo en la Composicion
	 * @param quantity Cantidad del Articulo producido
	 * @param price Precio del Articulo producido
	 * @throws SQLException
	*/
	protected int insertProductionOldIds( Integer oldId , String description , String lot_code , Date production_date , Integer item , Double initial_quantity , Double quantity , Double price )
	throws SQLException {
		
		Integer newId = this.productionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxproductionIds.get(null);
		newId = this.insertProduction( description,  lot_code,  production_date,  item,  initial_quantity,  quantity,  price );
		productionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertProduction_detail( Integer production,  Integer item,  String description,  Double initial_quantity,  Double quantity,  Double price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production_detail ( production, item, description, initial_quantity, quantity, price) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO production_detail ( production, item, description, initial_quantity, quantity, price) VALUES (  '"+ production +"', '"+ item +"', '"+ description +"', '"+ initial_quantity +"', '"+ quantity +"', '"+ price +"')";
		LOGGER.debug(message);

		if ( production == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, production);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( initial_quantity == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, initial_quantity);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> production_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Production_detail
	 * @param id Identificador unico del Detalle de la Produccion
	 * @param production Identificador de la Produccion
	 * @param item Identificador del Articulo subproducto
	 * @param description Descripcion del Articulo subproducto
	 * @param initial_quantity Cantidad inicial del Articulo subproducto en la Composicion
	 * @param quantity Cantidad del Articulo subproducto
	 * @param price Precio del Articulo subproducto
	 * @throws SQLException
	*/
	protected int insertProduction_detailOldIds( Integer oldId , Integer production , Integer item , String description , Double initial_quantity , Double quantity , Double price )
	throws SQLException {
		
		Integer newId = this.production_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxproduction_detailIds.get(null);
		newId = this.insertProduction_detail( production,  item,  description,  initial_quantity,  quantity,  price );
		production_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertProduction_expense( Integer production,  String description,  Double quantity,  Double price )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO production_expense ( production, description, quantity, price) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO production_expense ( production, description, quantity, price) VALUES (  '"+ production +"', '"+ description +"', '"+ quantity +"', '"+ price +"')";
		LOGGER.debug(message);

		if ( production == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, production);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}
		if ( price == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, price);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> production_expenseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Production_expense
	 * @param id Identificador unico del Gasto de la Produccion
	 * @param production Identificador de la Produccion
	 * @param description Descripcion del Gasto
	 * @param quantity Cantidad del Gasto
	 * @param price Precio del Gasto
	 * @throws SQLException
	*/
	protected int insertProduction_expenseOldIds( Integer oldId , Integer production , String description , Double quantity , Double price )
	throws SQLException {
		
		Integer newId = this.production_expenseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxproduction_expenseIds.get(null);
		newId = this.insertProduction_expense( production,  description,  quantity,  price );
		production_expenseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertPurchase( Integer supplier,  String series,  Integer number,  Integer address,  String discount_expr,  Date issue_date,  Integer pay_method,  Short document_type,  Short security_level,  Short status,  Integer workplace,  Integer scope,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO purchase ( supplier, series, number, address, discount_expr, issue_date, pay_method, document_type, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO purchase ( supplier, series, number, address, discount_expr, issue_date, pay_method, document_type, security_level, status, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  '"+ supplier +"', '"+ series +"', '"+ number +"', '"+ address +"', '"+ discount_expr +"', '"+ issue_date +"', '"+ pay_method +"', '"+ document_type +"', '"+ security_level +"', '"+ status +"', '"+ workplace +"', '"+ scope +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"')";
		LOGGER.debug(message);

		if ( supplier == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, supplier);
		}
		if ( series == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, series);
		}
		if ( number == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, number);
		}
		if ( address == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, address);
		}
		if ( discount_expr == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, discount_expr);
		}
		if ( issue_date == null ) {
			stmt.setNull(6, 91);
		}
		else {
			stmt.setDate(6, issue_date);
		}
		if ( pay_method == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, pay_method);
		}
		if ( document_type == null ) {
			stmt.setNull(8, -6);
		}
		else {
			stmt.setShort(8, document_type);
		}
		if ( security_level == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, security_level);
		}
		if ( status == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, status);
		}
		if ( workplace == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, workplace);
		}
		if ( scope == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, scope);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(13, 5);
		}
		else {
			stmt.setInt(13, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(14, 5);
		}
		else {
			stmt.setInt(14, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(15, 5);
		}
		else {
			stmt.setInt(15, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(16, 12);
		}
		else {
			stmt.setString(16, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(17, 4);
		}
		else {
			stmt.setInt(17, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(18, 12);
		}
		else {
			stmt.setString(18, bank_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> purchaseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Purchase
	 * @param id Identificador unico del Pedido de Compra
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
	 * @throws SQLException
	*/
	protected int insertPurchaseOldIds( Integer oldId , Integer supplier , String series , Integer number , Integer address , String discount_expr , Date issue_date , Integer pay_method , Short document_type , Short security_level , Short status , Integer workplace , Integer scope , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account )
	throws SQLException {
		
		Integer newId = this.purchaseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxpurchaseIds.get(null);
		newId = this.insertPurchase( supplier,  series,  number,  address,  discount_expr,  issue_date,  pay_method,  document_type,  security_level,  status,  workplace,  scope,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account );
		purchaseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertPurchase_detail( Integer purchase,  Integer line,  Integer item,  String description,  Double quantity,  Double price,  String discount_expr,  Double taxes,  Short status,  Double delivered )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO purchase_detail ( purchase, line, item, description, quantity, price, discount_expr, taxes, status, delivered) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO purchase_detail ( purchase, line, item, description, quantity, price, discount_expr, taxes, status, delivered) VALUES (  '"+ purchase +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ taxes +"', '"+ status +"', '"+ delivered +"')";
		LOGGER.debug(message);

		if ( purchase == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, purchase);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, discount_expr);
		}
		if ( taxes == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, taxes);
		}
		if ( status == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, status);
		}
		if ( delivered == null ) {
			stmt.setNull(10, 8);
		}
		else {
			stmt.setDouble(10, delivered);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> purchase_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Purchase_detail
	 * @param id Identificador unico del Detalle del Pedido de Compra
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
	 * @throws SQLException
	*/
	protected int insertPurchase_detailOldIds( Integer oldId , Integer purchase , Integer line , Integer item , String description , Double quantity , Double price , String discount_expr , Double taxes , Short status , Double delivered )
	throws SQLException {
		
		Integer newId = this.purchase_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxpurchase_detailIds.get(null);
		newId = this.insertPurchase_detail( purchase,  line,  item,  description,  quantity,  price,  discount_expr,  taxes,  status,  delivered );
		purchase_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertQualification( String code,  String description,  Double min_value,  Double max_value )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO qualification ( code, description, min_value, max_value) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO qualification ( code, description, min_value, max_value) VALUES (  '"+ code +"', '"+ description +"', '"+ min_value +"', '"+ max_value +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, code);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( min_value == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, min_value);
		}
		if ( max_value == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, max_value);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> qualificationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Qualification
	 * @param id Identificador unico de la Calificacion
	 * @param code Codigo de la Calificacion
	 * @param description Descripcion de la Calificacion
	 * @param min_value Limite inferior de la Calificacion
	 * @param max_value Limite superior de la Calificacion
	 * @throws SQLException
	*/
	protected int insertQualificationOldIds( Integer oldId , String code , String description , Double min_value , Double max_value )
	throws SQLException {
		
		Integer newId = this.qualificationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertQualification( code,  description,  min_value,  max_value );
		qualificationIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Quality_skill
	 * @param code Codigo de la Aptitud Calidad
	 * @param description Descripcion de la Aptitud Calidad
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertQuality_skill( String code,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO quality_skill ( code, description) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO quality_skill ( code, description) VALUES (  '"+ code +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( code == null ) {
			stmt.setNull(1, 1);
		}
		else {
			stmt.setString(1, code);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> quality_skillIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Quality_skill
	 * @param id Identificador unico de la Aptitud Calidad
	 * @param code Codigo de la Aptitud Calidad
	 * @param description Descripcion de la Aptitud Calidad
	 * @throws SQLException
	*/
	protected int insertQuality_skillOldIds( Integer oldId , String code , String description )
	throws SQLException {
		
		Integer newId = this.quality_skillIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertQuality_skill( code,  description );
		quality_skillIds.put(oldId,newId);
		
		return newId;
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
	protected int insertQuestion( Boolean active,  String question_text,  Short type,  String argument )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO question ( active, question_text, type, argument) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO question ( active, question_text, type, argument) VALUES (  '"+ active +"', '"+ question_text +"', '"+ type +"', '"+ argument +"')";
		LOGGER.debug(message);

		if ( active == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, active);
		}
		if ( question_text == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, question_text);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}
		if ( argument == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, argument);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> questionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Question
	 * @param id Identificador unico
	 * @param active Indica si la Pregunta esta activa o no
	 * @param question_text Texto de la Pregunta
	 * @param type Tipo de Pregunta
	 * @param argument Argumentacion de la Pregunta
	 * @throws SQLException
	*/
	protected int insertQuestionOldIds( Integer oldId , Boolean active , String question_text , Short type , String argument )
	throws SQLException {
		
		Integer newId = this.questionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertQuestion( active,  question_text,  type,  argument );
		questionIds.put(oldId,newId);
		
		return newId;
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
	protected int insertQuestion_value( Integer question,  String value_text,  Double value_number,  Timestamp value_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO question_value ( question, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO question_value ( question, value_text, value_number, value_date) VALUES (  '"+ question +"', '"+ value_text +"', '"+ value_number +"', '"+ value_date +"')";
		LOGGER.debug(message);

		if ( question == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, question);
		}
		if ( value_text == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, value_text);
		}
		if ( value_number == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, value_number);
		}
		if ( value_date == null ) {
			stmt.setNull(4, 93);
		}
		else {
			stmt.setTimestamp(4, value_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> question_valueIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Question_value
	 * @param id Identificador unico
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @throws SQLException
	*/
	protected int insertQuestion_valueOldIds( Integer oldId , Integer question , String value_text , Double value_number , Timestamp value_date )
	throws SQLException {
		
		Integer newId = this.question_valueIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxquestion_valueIds.get(null);
		newId = this.insertQuestion_value( question,  value_text,  value_number,  value_date );
		question_valueIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRaddinfo( Integer registry,  String attribute,  String value,  Date value_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO raddinfo ( registry, attribute, value, value_date) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO raddinfo ( registry, attribute, value, value_date) VALUES (  '"+ registry +"', '"+ attribute +"', '"+ value +"', '"+ value_date +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( attribute == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, attribute);
		}
		if ( value == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, value);
		}
		if ( value_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, value_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> raddinfoIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Raddinfo
	 * @param id Identificador unico
	 * @param registry Identificador de la Persona o Empresa
	 * @param attribute Atributo adicional
	 * @param value Valor del atributo adicional
	 * @param value_date Fecha del valor del atributo
	 * @throws SQLException
	*/
	protected int insertRaddinfoOldIds( Integer oldId , Integer registry , String attribute , String value , Date value_date )
	throws SQLException {
		
		Integer newId = this.raddinfoIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxraddinfoIds.get(null);
		newId = this.insertRaddinfo( registry,  attribute,  value,  value_date );
		raddinfoIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRaddress( Integer registry,  Short type,  String recipient,  Short street_type,  String address,  String address2,  String address3,  String zip,  String city,  Integer geozone )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO raddress ( registry, type, recipient, street_type, address, address2, address3, zip, city, geozone) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO raddress ( registry, type, recipient, street_type, address, address2, address3, zip, city, geozone) VALUES (  '"+ registry +"', '"+ type +"', '"+ recipient +"', '"+ street_type +"', '"+ address +"', '"+ address2 +"', '"+ address3 +"', '"+ zip +"', '"+ city +"', '"+ geozone +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, type);
		}
		if ( recipient == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, recipient);
		}
		if ( street_type == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, street_type);
		}
		if ( address == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, address);
		}
		if ( address2 == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, address2);
		}
		if ( address3 == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, address3);
		}
		if ( zip == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, zip);
		}
		if ( city == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, city);
		}
		if ( geozone == null ) {
			stmt.setNull(10, 4);
		}
		else {
			stmt.setInt(10, geozone);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> raddressIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Raddress
	 * @param id Identificador unico de la Direccion de la Persona o Empresa
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
	 * @throws SQLException
	*/
	protected int insertRaddressOldIds( Integer oldId , Integer registry , Short type , String recipient , Short street_type , String address , String address2 , String address3 , String zip , String city , Integer geozone )
	throws SQLException {
		
		Integer newId = this.raddressIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxraddressIds.get(null);
		newId = this.insertRaddress( registry,  type,  recipient,  street_type,  address,  address2,  address3,  zip,  city,  geozone );
		raddressIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRattach( Integer registry,  Integer category,  Short mimeType,  String description,  InputStream data,  Short type,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rattach ( registry, category, mimeType, description, data, type, scope) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rattach ( registry, category, mimeType, description, data, type, scope) VALUES (  '"+ registry +"', '"+ category +"', '"+ mimeType +"', '"+ description +"', '"+ data +"', '"+ type +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( category == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, category);
		}
		if ( mimeType == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, mimeType);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( data == null ) {
			stmt.setNull(5, -4);
		}
		else {
			stmt.setBinaryStream(5, data);
		}
		if ( type == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, type);
		}
		if ( scope == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, scope);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rattachIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertRattachOldIds( Integer oldId , Integer registry , Integer category , Short mimeType , String description , InputStream data , Short type , Integer scope )
	throws SQLException {
		
		Integer newId = this.rattachIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrattachIds.get(null);
		newId = this.insertRattach( registry,  category,  mimeType,  description,  data,  type,  scope );
		rattachIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRbank( Integer registry,  Integer bank,  String bank_account,  String sufix )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rbank ( registry, bank, bank_account, sufix) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rbank ( registry, bank, bank_account, sufix) VALUES (  '"+ registry +"', '"+ bank +"', '"+ bank_account +"', '"+ sufix +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( bank == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, bank_account);
		}
		if ( sufix == null ) {
			stmt.setNull(4, 1);
		}
		else {
			stmt.setString(4, sufix);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rbankIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Rbank
	 * @param id Identificador unico de la Cuenta Bancaria de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param bank Identificador de la Entidad Bancaria
	 * @param bank_account Numero de Cuenta Bancaria de la Persona o Empresa
	 * @param sufix Sufijo de Cuenta Bancaria para Remesas
	 * @throws SQLException
	*/
	protected int insertRbankOldIds( Integer oldId , Integer registry , Integer bank , String bank_account , String sufix )
	throws SQLException {
		
		Integer newId = this.rbankIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrbankIds.get(null);
		newId = this.insertRbank( registry,  bank,  bank_account,  sufix );
		rbankIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Rbank_account
	 * @param rbank Identificador de la Cuenta Bancaria
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRbank_account( Integer rbank,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rbank_account ( rbank, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rbank_account ( rbank, account) VALUES (  '"+ rbank +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( rbank == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, rbank);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rbank_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Rbank_account
	 * @param id Identificador unico de la Cuenta Contable de la Cuenta Bancaria
	 * @param rbank Identificador de la Cuenta Bancaria
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertRbank_accountOldIds( Integer oldId , Integer rbank , String account )
	throws SQLException {
		
		Integer newId = this.rbank_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrbank_accountIds.get(null);
		newId = this.insertRbank_account( rbank,  account );
		rbank_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRdir_staff( Integer registry,  String document,  String name,  Boolean shareholder,  Boolean representative,  Boolean director,  Double percent_share,  Integer share_number,  Double nominal_value,  Date due_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rdir_staff ( registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rdir_staff ( registry, document, name, shareholder, representative, director, percent_share, share_number, nominal_value, due_date) VALUES (  '"+ registry +"', '"+ document +"', '"+ name +"', '"+ shareholder +"', '"+ representative +"', '"+ director +"', '"+ percent_share +"', '"+ share_number +"', '"+ nominal_value +"', '"+ due_date +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( document == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, document);
		}
		if ( name == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, name);
		}
		if ( shareholder == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, shareholder);
		}
		if ( representative == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, representative);
		}
		if ( director == null ) {
			stmt.setNull(6, -7);
		}
		else {
			stmt.setBoolean(6, director);
		}
		if ( percent_share == null ) {
			stmt.setNull(7, 8);
		}
		else {
			stmt.setDouble(7, percent_share);
		}
		if ( share_number == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, share_number);
		}
		if ( nominal_value == null ) {
			stmt.setNull(9, 8);
		}
		else {
			stmt.setDouble(9, nominal_value);
		}
		if ( due_date == null ) {
			stmt.setNull(10, 91);
		}
		else {
			stmt.setDate(10, due_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rdir_staffIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertRdir_staffOldIds( Integer oldId , Integer registry , String document , String name , Boolean shareholder , Boolean representative , Boolean director , Double percent_share , Integer share_number , Double nominal_value , Date due_date )
	throws SQLException {
		
		Integer newId = this.rdir_staffIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrdir_staffIds.get(null);
		newId = this.insertRdir_staff( registry,  document,  name,  shareholder,  representative,  director,  percent_share,  share_number,  nominal_value,  due_date );
		rdir_staffIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRecord_data( Integer registry,  Date creation_date,  String description,  String notary,  String number,  Date record_date,  String volume,  String section,  String page,  String sheet,  String registration,  Integer attach )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO record_data ( registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO record_data ( registry, creation_date, description, notary, number, record_date, volume, section, page, sheet, registration, attach) VALUES (  '"+ registry +"', '"+ creation_date +"', '"+ description +"', '"+ notary +"', '"+ number +"', '"+ record_date +"', '"+ volume +"', '"+ section +"', '"+ page +"', '"+ sheet +"', '"+ registration +"', '"+ attach +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( creation_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, creation_date);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}
		if ( notary == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, notary);
		}
		if ( number == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, number);
		}
		if ( record_date == null ) {
			stmt.setNull(6, 91);
		}
		else {
			stmt.setDate(6, record_date);
		}
		if ( volume == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, volume);
		}
		if ( section == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, section);
		}
		if ( page == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, page);
		}
		if ( sheet == null ) {
			stmt.setNull(10, 12);
		}
		else {
			stmt.setString(10, sheet);
		}
		if ( registration == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, registration);
		}
		if ( attach == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, attach);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> record_dataIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertRecord_dataOldIds( Integer oldId , Integer registry , Date creation_date , String description , String notary , String number , Date record_date , String volume , String section , String page , String sheet , String registration , Integer attach )
	throws SQLException {
		
		Integer newId = this.record_dataIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrecord_dataIds.get(null);
		newId = this.insertRecord_data( registry,  creation_date,  description,  notary,  number,  record_date,  volume,  section,  page,  sheet,  registration,  attach );
		record_dataIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRegistry( String document,  String name,  String surname,  String alias,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO registry ( document, name, surname, alias, type) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO registry ( document, name, surname, alias, type) VALUES (  '"+ document +"', '"+ name +"', '"+ surname +"', '"+ alias +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( document == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, document);
		}
		if ( name == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, name);
		}
		if ( surname == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, surname);
		}
		if ( alias == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, alias);
		}
		if ( type == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> registryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Registry
	 * @param id Identificador unico de la Persona o Empresa
	 * @param document Numero de Documento de la Persona o Empresa
	 * @param name Nombre de la Persona o Empresa
	 * @param surname Apellido de la Persona o Empresa
	 * @param alias Alias de la Persona o Empresa
	 * @param type Tipo (Persona o Empresa)
	 * @throws SQLException
	*/
	protected int insertRegistryOldIds( Integer oldId , String document , String name , String surname , String alias , Short type )
	throws SQLException {
		
		Integer newId = this.registryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertRegistry( document,  name,  surname,  alias,  type );
		registryIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Relationship
	 * @param description Descripcion del Tipo de Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRelationship( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO relationship ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO relationship ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> relationshipIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Relationship
	 * @param id Identificador unico del Tipo de Relacion
	 * @param description Descripcion del Tipo de Relacion
	 * @throws SQLException
	*/
	protected int insertRelationshipOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.relationshipIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertRelationship( description );
		relationshipIds.put(oldId,newId);
		
		return newId;
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
	protected int insertResource( Integer employee,  Integer workplace,  Integer workactivity,  Date startingdate,  Date endingdate )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO resource ( employee, workplace, workactivity, startingdate, endingdate) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO resource ( employee, workplace, workactivity, startingdate, endingdate) VALUES (  '"+ employee +"', '"+ workplace +"', '"+ workactivity +"', '"+ startingdate +"', '"+ endingdate +"')";
		LOGGER.debug(message);

		if ( employee == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, employee);
		}
		if ( workplace == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, workplace);
		}
		if ( workactivity == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, workactivity);
		}
		if ( startingdate == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, startingdate);
		}
		if ( endingdate == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, endingdate);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> resourceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Resource
	 * @param id Identificador unico del Recurso
	 * @param employee Identificador del Empleado
	 * @param workplace Identificador del Centro de Trabajo
	 * @param workactivity Identificador de la Actividad
	 * @param startingdate Fecha inicial del Recurso
	 * @param endingdate Fecha final del Recurso
	 * @throws SQLException
	*/
	protected int insertResourceOldIds( Integer oldId , Integer employee , Integer workplace , Integer workactivity , Date startingdate , Date endingdate )
	throws SQLException {
		
		Integer newId = this.resourceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxresourceIds.get(null);
		newId = this.insertResource( employee,  workplace,  workactivity,  startingdate,  endingdate );
		resourceIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRmedia( Integer registry,  Short media,  String value,  String comment,  Boolean administrative,  Boolean commercial,  Boolean technical )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rmedia ( registry, media, value, comment, administrative, commercial, technical) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rmedia ( registry, media, value, comment, administrative, commercial, technical) VALUES (  '"+ registry +"', '"+ media +"', '"+ value +"', '"+ comment +"', '"+ administrative +"', '"+ commercial +"', '"+ technical +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( media == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, media);
		}
		if ( value == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, value);
		}
		if ( comment == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, comment);
		}
		if ( administrative == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, administrative);
		}
		if ( commercial == null ) {
			stmt.setNull(6, -7);
		}
		else {
			stmt.setBoolean(6, commercial);
		}
		if ( technical == null ) {
			stmt.setNull(7, -7);
		}
		else {
			stmt.setBoolean(7, technical);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rmediaIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertRmediaOldIds( Integer oldId , Integer registry , Short media , String value , String comment , Boolean administrative , Boolean commercial , Boolean technical )
	throws SQLException {
		
		Integer newId = this.rmediaIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrmediaIds.get(null);
		newId = this.insertRmedia( registry,  media,  value,  comment,  administrative,  commercial,  technical );
		rmediaIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRnote( Integer registry,  String description,  Date note_date,  InputStream comments,  Short note_type,  Short security_level )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rnote ( registry, description, note_date, comments, note_type, security_level) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rnote ( registry, description, note_date, comments, note_type, security_level) VALUES (  '"+ registry +"', '"+ description +"', '"+ note_date +"', '"+ comments +"', '"+ note_type +"', '"+ security_level +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( note_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, note_date);
		}
		if ( comments == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, comments);
		}
		if ( note_type == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, note_type);
		}
		if ( security_level == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, security_level);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rnoteIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Rnote
	 * @param id Identificador unico de la Nota de la Persona o Empresa
	 * @param registry Identificador del Registro de la Persona o Empresa
	 * @param description Descripcion de la Nota
	 * @param note_date Fecha de la Nota
	 * @param comments Comentarios de la Nota
	 * @param note_type 
	 * @param security_level Nivel de seguridad de la Nota
	 * @throws SQLException
	*/
	protected int insertRnoteOldIds( Integer oldId , Integer registry , String description , Date note_date , InputStream comments , Short note_type , Short security_level )
	throws SQLException {
		
		Integer newId = this.rnoteIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrnoteIds.get(null);
		newId = this.insertRnote( registry,  description,  note_date,  comments,  note_type,  security_level );
		rnoteIds.put(oldId,newId);
		
		return newId;
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
	protected int insertRpaymethod( Integer registry,  Integer pay_method,  Integer rbank,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rpaymethod ( registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rpaymethod ( registry, pay_method, rbank, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days) VALUES (  '"+ registry +"', '"+ pay_method +"', '"+ rbank +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( pay_method == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, pay_method);
		}
		if ( rbank == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, rbank);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(4, 5);
		}
		else {
			stmt.setInt(4, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(5, 5);
		}
		else {
			stmt.setInt(5, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(6, 5);
		}
		else {
			stmt.setInt(6, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, pymnt_days);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rpaymethodIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertRpaymethodOldIds( Integer oldId , Integer registry , Integer pay_method , Integer rbank , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days )
	throws SQLException {
		
		Integer newId = this.rpaymethodIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrpaymethodIds.get(null);
		newId = this.insertRpaymethod( registry,  pay_method,  rbank,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days );
		rpaymethodIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Rrelationship
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de Relación
	 * @param comments Comentarios de la Relacion
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRrelationship( Integer registry,  Integer related_registry,  Integer relationship,  String comments )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rrelationship ( registry, related_registry, relationship, comments) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rrelationship ( registry, related_registry, relationship, comments) VALUES (  '"+ registry +"', '"+ related_registry +"', '"+ relationship +"', '"+ comments +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( related_registry == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, related_registry);
		}
		if ( relationship == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, relationship);
		}
		if ( comments == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, comments);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rrelationshipIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Rrelationship
	 * @param id Identificador unico de la Relacion
	 * @param registry Identificador de la Persona o Empresa que tiene la Relacion
	 * @param related_registry Identificador de la Persona o Empresa relacionada
	 * @param relationship Identificador del Tipo de Relación
	 * @param comments Comentarios de la Relacion
	 * @throws SQLException
	*/
	protected int insertRrelationshipOldIds( Integer oldId , Integer registry , Integer related_registry , Integer relationship , String comments )
	throws SQLException {
		
		Integer newId = this.rrelationshipIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrrelationshipIds.get(null);
		newId = this.insertRrelationship( registry,  related_registry,  relationship,  comments );
		rrelationshipIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Rsegment
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertRsegment( Integer registry,  Integer segment )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO rsegment ( registry, segment) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO rsegment ( registry, segment) VALUES (  '"+ registry +"', '"+ segment +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( segment == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, segment);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> rsegmentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Rsegment
	 * @param id Identificador unico
	 * @param registry Identificador de Persona o Empresa
	 * @param segment Identificador del Segmento
	 * @throws SQLException
	*/
	protected int insertRsegmentOldIds( Integer oldId , Integer registry , Integer segment )
	throws SQLException {
		
		Integer newId = this.rsegmentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxrsegmentIds.get(null);
		newId = this.insertRsegment( registry,  segment );
		rsegmentIds.put(oldId,newId);
		
		return newId;
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
	protected int insertSales( Integer customer,  String series,  Integer number,  Integer shipping_address,  Integer seller,  String discount_expr,  Date issue_date,  Integer pay_method,  Short document_type,  Short security_level,  Short status,  Integer pos,  Integer workplace,  Integer scope,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales ( customer, series, number, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, pos, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO sales ( customer, series, number, shipping_address, seller, discount_expr, issue_date, pay_method, document_type, security_level, status, pos, workplace, scope, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  '"+ customer +"', '"+ series +"', '"+ number +"', '"+ shipping_address +"', '"+ seller +"', '"+ discount_expr +"', '"+ issue_date +"', '"+ pay_method +"', '"+ document_type +"', '"+ security_level +"', '"+ status +"', '"+ pos +"', '"+ workplace +"', '"+ scope +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"')";
		LOGGER.debug(message);

		if ( customer == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, customer);
		}
		if ( series == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, series);
		}
		if ( number == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, number);
		}
		if ( shipping_address == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, shipping_address);
		}
		if ( seller == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, seller);
		}
		if ( discount_expr == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, discount_expr);
		}
		if ( issue_date == null ) {
			stmt.setNull(7, 91);
		}
		else {
			stmt.setDate(7, issue_date);
		}
		if ( pay_method == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, pay_method);
		}
		if ( document_type == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, document_type);
		}
		if ( security_level == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, security_level);
		}
		if ( status == null ) {
			stmt.setNull(11, -6);
		}
		else {
			stmt.setShort(11, status);
		}
		if ( pos == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, pos);
		}
		if ( workplace == null ) {
			stmt.setNull(13, 4);
		}
		else {
			stmt.setInt(13, workplace);
		}
		if ( scope == null ) {
			stmt.setNull(14, 4);
		}
		else {
			stmt.setInt(14, scope);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(15, 5);
		}
		else {
			stmt.setInt(15, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(16, 5);
		}
		else {
			stmt.setInt(16, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(17, 5);
		}
		else {
			stmt.setInt(17, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(18, 12);
		}
		else {
			stmt.setString(18, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(19, 4);
		}
		else {
			stmt.setInt(19, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(20, 12);
		}
		else {
			stmt.setString(20, bank_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> salesIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Sales
	 * @param id Identificador unico del Pedido de Venta
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
	 * @throws SQLException
	*/
	protected int insertSalesOldIds( Integer oldId , Integer customer , String series , Integer number , Integer shipping_address , Integer seller , String discount_expr , Date issue_date , Integer pay_method , Short document_type , Short security_level , Short status , Integer pos , Integer workplace , Integer scope , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account )
	throws SQLException {
		
		Integer newId = this.salesIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsalesIds.get(null);
		newId = this.insertSales( customer,  series,  number,  shipping_address,  seller,  discount_expr,  issue_date,  pay_method,  document_type,  security_level,  status,  pos,  workplace,  scope,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account );
		salesIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Sales_detail
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de línea del Detalle dentro del Pedido
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
	protected int insertSales_detail( Integer sales,  Integer line,  Integer item,  String description,  Double quantity,  Double price,  String discount_expr,  Double taxes,  Short status,  Short source,  Integer offer_detail,  Double delivered )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales_detail ( sales, line, item, description, quantity, price, discount_expr, taxes, status, source, offer_detail, delivered) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO sales_detail ( sales, line, item, description, quantity, price, discount_expr, taxes, status, source, offer_detail, delivered) VALUES (  '"+ sales +"', '"+ line +"', '"+ item +"', '"+ description +"', '"+ quantity +"', '"+ price +"', '"+ discount_expr +"', '"+ taxes +"', '"+ status +"', '"+ source +"', '"+ offer_detail +"', '"+ delivered +"')";
		LOGGER.debug(message);

		if ( sales == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, sales);
		}
		if ( line == null ) {
			stmt.setNull(2, 5);
		}
		else {
			stmt.setInt(2, line);
		}
		if ( item == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, item);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( quantity == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, quantity);
		}
		if ( price == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, price);
		}
		if ( discount_expr == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, discount_expr);
		}
		if ( taxes == null ) {
			stmt.setNull(8, 8);
		}
		else {
			stmt.setDouble(8, taxes);
		}
		if ( status == null ) {
			stmt.setNull(9, -6);
		}
		else {
			stmt.setShort(9, status);
		}
		if ( source == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, source);
		}
		if ( offer_detail == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, offer_detail);
		}
		if ( delivered == null ) {
			stmt.setNull(12, 8);
		}
		else {
			stmt.setDouble(12, delivered);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> sales_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Sales_detail
	 * @param id Identificador unico del Detalle del Pedido de Venta
	 * @param sales Identificador del Pedido de Venta
	 * @param line Numero de línea del Detalle dentro del Pedido
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
	 * @throws SQLException
	*/
	protected int insertSales_detailOldIds( Integer oldId , Integer sales , Integer line , Integer item , String description , Double quantity , Double price , String discount_expr , Double taxes , Short status , Short source , Integer offer_detail , Double delivered )
	throws SQLException {
		
		Integer newId = this.sales_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsales_detailIds.get(null);
		newId = this.insertSales_detail( sales,  line,  item,  description,  quantity,  price,  discount_expr,  taxes,  status,  source,  offer_detail,  delivered );
		sales_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Sales_purchase
	 * @param sales_detail Identificador del Detalle del Pedido de Venta
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSales_purchase( Integer sales_detail,  Integer purchase_detail )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO sales_purchase ( sales_detail, purchase_detail) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO sales_purchase ( sales_detail, purchase_detail) VALUES (  '"+ sales_detail +"', '"+ purchase_detail +"')";
		LOGGER.debug(message);

		if ( sales_detail == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, sales_detail);
		}
		if ( purchase_detail == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, purchase_detail);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> sales_purchaseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Sales_purchase
	 * @param id Identificador unico de la Relacion de Pedidos de Compra y Venta
	 * @param sales_detail Identificador del Detalle del Pedido de Venta
	 * @param purchase_detail Identificador del Detalle del Pedido de Compra
	 * @throws SQLException
	*/
	protected int insertSales_purchaseOldIds( Integer oldId , Integer sales_detail , Integer purchase_detail )
	throws SQLException {
		
		Integer newId = this.sales_purchaseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsales_purchaseIds.get(null);
		newId = this.insertSales_purchase( sales_detail,  purchase_detail );
		sales_purchaseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertScale( Short scale_model,  String program_path,  Date inidate,  Date enddate,  String serie,  String code1,  String code2,  String code3,  String code4,  String code5,  Boolean verified )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scale ( scale_model, program_path, inidate, enddate, serie, code1, code2, code3, code4, code5, verified) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO scale ( scale_model, program_path, inidate, enddate, serie, code1, code2, code3, code4, code5, verified) VALUES (  '"+ scale_model +"', '"+ program_path +"', '"+ inidate +"', '"+ enddate +"', '"+ serie +"', '"+ code1 +"', '"+ code2 +"', '"+ code3 +"', '"+ code4 +"', '"+ code5 +"', '"+ verified +"')";
		LOGGER.debug(message);

		if ( scale_model == null ) {
			stmt.setNull(1, -6);
		}
		else {
			stmt.setShort(1, scale_model);
		}
		if ( program_path == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, program_path);
		}
		if ( inidate == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, inidate);
		}
		if ( enddate == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, enddate);
		}
		if ( serie == null ) {
			stmt.setNull(5, 1);
		}
		else {
			stmt.setString(5, serie);
		}
		if ( code1 == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, code1);
		}
		if ( code2 == null ) {
			stmt.setNull(7, 12);
		}
		else {
			stmt.setString(7, code2);
		}
		if ( code3 == null ) {
			stmt.setNull(8, 12);
		}
		else {
			stmt.setString(8, code3);
		}
		if ( code4 == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, code4);
		}
		if ( code5 == null ) {
			stmt.setNull(10, 12);
		}
		else {
			stmt.setString(10, code5);
		}
		if ( verified == null ) {
			stmt.setNull(11, -7);
		}
		else {
			stmt.setBoolean(11, verified);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> scaleIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Scale
	 * @param id Identificador unico de la Transferencia de datos entre Balanzas y Aon
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
	 * @throws SQLException
	*/
	protected int insertScaleOldIds( Integer oldId , Short scale_model , String program_path , Date inidate , Date enddate , String serie , String code1 , String code2 , String code3 , String code4 , String code5 , Boolean verified )
	throws SQLException {
		
		Integer newId = this.scaleIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertScale( scale_model,  program_path,  inidate,  enddate,  serie,  code1,  code2,  code3,  code4,  code5,  verified );
		scaleIds.put(oldId,newId);
		
		return newId;
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
	protected int insertScale_relation( Integer aon_id,  String scale_id1,  String scale_id2,  String type,  Short scale_model )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scale_relation ( aon_id, scale_id1, scale_id2, type, scale_model) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO scale_relation ( aon_id, scale_id1, scale_id2, type, scale_model) VALUES (  '"+ aon_id +"', '"+ scale_id1 +"', '"+ scale_id2 +"', '"+ type +"', '"+ scale_model +"')";
		LOGGER.debug(message);

		if ( aon_id == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, aon_id);
		}
		if ( scale_id1 == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, scale_id1);
		}
		if ( scale_id2 == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, scale_id2);
		}
		if ( type == null ) {
			stmt.setNull(4, 1);
		}
		else {
			stmt.setString(4, type);
		}
		if ( scale_model == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, scale_model);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> scale_relationIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Scale_relation
	 * @param id Identificador unico de la Relacion entre tablas de Balanzas y Aon
	 * @param aon_id Identificador de la tabla en Aon
	 * @param scale_id1 Primer identificador de la tabla en la Balanza
	 * @param scale_id2 Segundo identificador de la tabla en la Balanza
	 * @param type Indica el tipo de tabla que se esta relacionando
	 * @param scale_model Modelo de Balanza
	 * @throws SQLException
	*/
	protected int insertScale_relationOldIds( Integer oldId , Integer aon_id , String scale_id1 , String scale_id2 , String type , Short scale_model )
	throws SQLException {
		
		Integer newId = this.scale_relationIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertScale_relation( aon_id,  scale_id1,  scale_id2,  type,  scale_model );
		scale_relationIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Scope
	 * @param description Descripcion del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertScope( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO scope ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO scope ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> scopeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Scope
	 * @param id Identificador unico
	 * @param description Descripcion del Ambito
	 * @throws SQLException
	*/
	protected int insertScopeOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.scopeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertScope( description );
		scopeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Segment
	 * @param name Nombre del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSegment( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO segment ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO segment ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> segmentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Segment
	 * @param id Identificador unico
	 * @param name Nombre del Segmento
	 * @throws SQLException
	*/
	protected int insertSegmentOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.segmentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertSegment( name );
		segmentIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Seller
	 * @param registry Registro del Agente Comercial
	 * @param commission_type Identificador del Tipo de Comision
	 * @param status Estado del Agente Comercial
	 * @throws SQLException
	*/
	protected void insertSeller( Integer registry,  Integer commission_type,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO seller ( registry, commission_type, status) VALUES ( ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO seller ( registry, commission_type, status) VALUES ( '"+ registry +"', '"+ commission_type +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( commission_type == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, commission_type);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
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
	protected void insertSeries( String id,  String description,  Integer workplace,  Short security_level,  Boolean active )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO series ( id, description, workplace, security_level, active) VALUES ( ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO series ( id, description, workplace, security_level, active) VALUES ( '"+ id +"', '"+ description +"', '"+ workplace +"', '"+ security_level +"', '"+ active +"')";
		LOGGER.debug(message);

		if ( id == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, id);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( workplace == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, workplace);
		}
		if ( security_level == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, security_level);
		}
		if ( active == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, active);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Session
	 * @param endDate Fecha de finalizacion
	 * @param remote_address IP remota
	 * @param remote_host Equipo remoto
	 * @param session_id Identificador web de la sesión
	 * @param startDate Fecha de inicio
	 * @param application_id Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSession( Timestamp endDate,  String remote_address,  String remote_host,  String session_id,  Timestamp startDate,  Integer application_id,  Integer user_id )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO session ( endDate, remote_address, remote_host, session_id, startDate, application_id, user_id) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO session ( endDate, remote_address, remote_host, session_id, startDate, application_id, user_id) VALUES (  '"+ endDate +"', '"+ remote_address +"', '"+ remote_host +"', '"+ session_id +"', '"+ startDate +"', '"+ application_id +"', '"+ user_id +"')";
		LOGGER.debug(message);

		if ( endDate == null ) {
			stmt.setNull(1, 93);
		}
		else {
			stmt.setTimestamp(1, endDate);
		}
		if ( remote_address == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, remote_address);
		}
		if ( remote_host == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, remote_host);
		}
		if ( session_id == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, session_id);
		}
		if ( startDate == null ) {
			stmt.setNull(5, 93);
		}
		else {
			stmt.setTimestamp(5, startDate);
		}
		if ( application_id == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, application_id);
		}
		if ( user_id == null ) {
			stmt.setNull(7, 4);
		}
		else {
			stmt.setInt(7, user_id);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> sessionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Session
	 * @param id Identificador unico
	 * @param endDate Fecha de finalizacion
	 * @param remote_address IP remota
	 * @param remote_host Equipo remoto
	 * @param session_id Identificador web de la sesión
	 * @param startDate Fecha de inicio
	 * @param application_id Identificador de la Aplicacion
	 * @param user_id Identificador del Usuario
	 * @throws SQLException
	*/
	protected int insertSessionOldIds( Integer oldId , Timestamp endDate , String remote_address , String remote_host , String session_id , Timestamp startDate , Integer application_id , Integer user_id )
	throws SQLException {
		
		Integer newId = this.sessionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsessionIds.get(null);
		newId = this.insertSession( endDate,  remote_address,  remote_host,  session_id,  startDate,  application_id,  user_id );
		sessionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Stock
	 * @param warehouse Identificador del Almacen
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad del Articulo en el Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertStock( Integer warehouse,  Integer item,  Double quantity )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO stock ( warehouse, item, quantity) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO stock ( warehouse, item, quantity) VALUES (  '"+ warehouse +"', '"+ item +"', '"+ quantity +"')";
		LOGGER.debug(message);

		if ( warehouse == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, warehouse);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( quantity == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, quantity);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> stockIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Stock
	 * @param id Identificador unico del Stock
	 * @param warehouse Identificador del Almacen
	 * @param item Identificador del Articulo
	 * @param quantity Cantidad del Articulo en el Almacen
	 * @throws SQLException
	*/
	protected int insertStockOldIds( Integer oldId , Integer warehouse , Integer item , Double quantity )
	throws SQLException {
		
		Integer newId = this.stockIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxstockIds.get(null);
		newId = this.insertStock( warehouse,  item,  quantity );
		stockIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Supplier
	 * @param registry Registro del Proveedor
	 * @param withholding Indica si el Proveedor aplica retencion de impuestos
	 * @param transaction Tipo de transacciones del Proveedor
	 * @param status Estado del Proveedor
	 * @param segment Segmento del Proveedor
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected void insertSupplier( Integer registry,  Boolean withholding,  Short transaction,  Short status,  Integer segment,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier ( registry, withholding, transaction, status, segment, scope) VALUES ( ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO supplier ( registry, withholding, transaction, status, segment, scope) VALUES ( '"+ registry +"', '"+ withholding +"', '"+ transaction +"', '"+ status +"', '"+ segment +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( withholding == null ) {
			stmt.setNull(2, -7);
		}
		else {
			stmt.setBoolean(2, withholding);
		}
		if ( transaction == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, transaction);
		}
		if ( status == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, status);
		}
		if ( segment == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, segment);
		}
		if ( scope == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, scope);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Supplier_account
	 * @param supplier Identificador del Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier_account( Integer supplier,  String account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier_account ( supplier, account) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO supplier_account ( supplier, account) VALUES (  '"+ supplier +"', '"+ account +"')";
		LOGGER.debug(message);

		if ( supplier == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, supplier);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> supplier_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Supplier_account
	 * @param id Identificador unico de la Cuenta Contable del Proveedor
	 * @param supplier Identificador del Proveedor
	 * @param account Identificador de la Cuenta Contable
	 * @throws SQLException
	*/
	protected int insertSupplier_accountOldIds( Integer oldId , Integer supplier , String account )
	throws SQLException {
		
		Integer newId = this.supplier_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsupplier_accountIds.get(null);
		newId = this.insertSupplier_account( supplier,  account );
		supplier_accountIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Supplier_segment
	 * @param description Descripcion del Segmento
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupplier_segment( String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO supplier_segment ( description) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO supplier_segment ( description) VALUES (  '"+ description +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> supplier_segmentIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Supplier_segment
	 * @param id Identificador unico del Segmento
	 * @param description Descripcion del Segmento
	 * @throws SQLException
	*/
	protected int insertSupplier_segmentOldIds( Integer oldId , String description )
	throws SQLException {
		
		Integer newId = this.supplier_segmentIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertSupplier_segment( description );
		supplier_segmentIds.put(oldId,newId);
		
		return newId;
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
	protected int insertSupport_order( Integer tas_item,  Integer target,  String series,  Integer number,  InputStream description,  Date final_date,  Short status,  Date start_date,  Integer employee,  Double counterti,  String levelti,  Short operation,  Integer workplace )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO support_order ( tas_item, target, series, number, description, final_date, status, start_date, employee, counterti, levelti, operation, workplace) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO support_order ( tas_item, target, series, number, description, final_date, status, start_date, employee, counterti, levelti, operation, workplace) VALUES (  '"+ tas_item +"', '"+ target +"', '"+ series +"', '"+ number +"', '"+ description +"', '"+ final_date +"', '"+ status +"', '"+ start_date +"', '"+ employee +"', '"+ counterti +"', '"+ levelti +"', '"+ operation +"', '"+ workplace +"')";
		LOGGER.debug(message);

		if ( tas_item == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, tas_item);
		}
		if ( target == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, target);
		}
		if ( series == null ) {
			stmt.setNull(3, 1);
		}
		else {
			stmt.setString(3, series);
		}
		if ( number == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, number);
		}
		if ( description == null ) {
			stmt.setNull(5, -1);
		}
		else {
			stmt.setAsciiStream(5, description);
		}
		if ( final_date == null ) {
			stmt.setNull(6, 91);
		}
		else {
			stmt.setDate(6, final_date);
		}
		if ( status == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, status);
		}
		if ( start_date == null ) {
			stmt.setNull(8, 91);
		}
		else {
			stmt.setDate(8, start_date);
		}
		if ( employee == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, employee);
		}
		if ( counterti == null ) {
			stmt.setNull(10, 8);
		}
		else {
			stmt.setDouble(10, counterti);
		}
		if ( levelti == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, levelti);
		}
		if ( operation == null ) {
			stmt.setNull(12, -6);
		}
		else {
			stmt.setShort(12, operation);
		}
		if ( workplace == null ) {
			stmt.setNull(13, 4);
		}
		else {
			stmt.setInt(13, workplace);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> support_orderIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Support_order
	 * @param id Identificador unico de la Orden de Reparacion
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
	 * @throws SQLException
	*/
	protected int insertSupport_orderOldIds( Integer oldId , Integer tas_item , Integer target , String series , Integer number , InputStream description , Date final_date , Short status , Date start_date , Integer employee , Double counterti , String levelti , Short operation , Integer workplace )
	throws SQLException {
		
		Integer newId = this.support_orderIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsupport_orderIds.get(null);
		newId = this.insertSupport_order( tas_item,  target,  series,  number,  description,  final_date,  status,  start_date,  employee,  counterti,  levelti,  operation,  workplace );
		support_orderIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Support_order_insurance
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param insurance Identificador de la Compañia de Seguros
	 * @param appraiser Identificador del Perito
	 * @param claim_number Numero de siniestro o reclamacion
	 * @param policy_type Tipo de poliza
	 * @param franchise Franquicia
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSupport_order_insurance( Integer support_order,  Integer insurance,  Integer appraiser,  String claim_number,  String policy_type,  String franchise )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO support_order_insurance ( support_order, insurance, appraiser, claim_number, policy_type, franchise) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO support_order_insurance ( support_order, insurance, appraiser, claim_number, policy_type, franchise) VALUES (  '"+ support_order +"', '"+ insurance +"', '"+ appraiser +"', '"+ claim_number +"', '"+ policy_type +"', '"+ franchise +"')";
		LOGGER.debug(message);

		if ( support_order == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, support_order);
		}
		if ( insurance == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, insurance);
		}
		if ( appraiser == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, appraiser);
		}
		if ( claim_number == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, claim_number);
		}
		if ( policy_type == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, policy_type);
		}
		if ( franchise == null ) {
			stmt.setNull(6, 12);
		}
		else {
			stmt.setString(6, franchise);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> support_order_insuranceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Support_order_insurance
	 * @param id Identificador unico
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param insurance Identificador de la Compañia de Seguros
	 * @param appraiser Identificador del Perito
	 * @param claim_number Numero de siniestro o reclamacion
	 * @param policy_type Tipo de poliza
	 * @param franchise Franquicia
	 * @throws SQLException
	*/
	protected int insertSupport_order_insuranceOldIds( Integer oldId , Integer support_order , Integer insurance , Integer appraiser , String claim_number , String policy_type , String franchise )
	throws SQLException {
		
		Integer newId = this.support_order_insuranceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsupport_order_insuranceIds.get(null);
		newId = this.insertSupport_order_insurance( support_order,  insurance,  appraiser,  claim_number,  policy_type,  franchise );
		support_order_insuranceIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Survey
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey( Boolean active,  Timestamp creationDate,  String description )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey ( active, creationDate, description) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO survey ( active, creationDate, description) VALUES (  '"+ active +"', '"+ creationDate +"', '"+ description +"')";
		LOGGER.debug(message);

		if ( active == null ) {
			stmt.setNull(1, -7);
		}
		else {
			stmt.setBoolean(1, active);
		}
		if ( creationDate == null ) {
			stmt.setNull(2, 93);
		}
		else {
			stmt.setTimestamp(2, creationDate);
		}
		if ( description == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, description);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> surveyIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Survey
	 * @param id Identificador unico
	 * @param active Indica si el Cuestionario esta activa o no
	 * @param creationDate Fecha de creacion del Cuestionario
	 * @param description Descripcion del Cuestionario
	 * @throws SQLException
	*/
	protected int insertSurveyOldIds( Integer oldId , Boolean active , Timestamp creationDate , String description )
	throws SQLException {
		
		Integer newId = this.surveyIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertSurvey( active,  creationDate,  description );
		surveyIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Survey_question
	 * @param survey Identificador del Cuestionario
	 * @param question Identificador de la Pregunta
	 * @param position Posicion de la Pregunta dentro del Cuestionario
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_question( Integer survey,  Integer question,  Integer position )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_question ( survey, question, position) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO survey_question ( survey, question, position) VALUES (  '"+ survey +"', '"+ question +"', '"+ position +"')";
		LOGGER.debug(message);

		if ( survey == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, survey);
		}
		if ( question == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, question);
		}
		if ( position == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, position);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> survey_questionIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Survey_question
	 * @param id Identificador unico
	 * @param survey Identificador del Cuestionario
	 * @param question Identificador de la Pregunta
	 * @param position Posicion de la Pregunta dentro del Cuestionario
	 * @throws SQLException
	*/
	protected int insertSurvey_questionOldIds( Integer oldId , Integer survey , Integer question , Integer position )
	throws SQLException {
		
		Integer newId = this.survey_questionIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsurvey_questionIds.get(null);
		newId = this.insertSurvey_question( survey,  question,  position );
		survey_questionIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Survey_response
	 * @param creationDate Fecha de la creacion en el sistema de la Respuesta del Cuestionario
	 * @param response_date Fecha de la Respuesta del Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param target Identificador del Cliente Potencial
	 * @param user Identificador del Usuario
	 * @param campaign_action Identificador de la Accion de la Campaña
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertSurvey_response( Timestamp creationDate,  Timestamp response_date,  Integer survey,  Integer target,  Integer user,  Integer campaign_action )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_response ( creationDate, response_date, survey, target, user, campaign_action) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO survey_response ( creationDate, response_date, survey, target, user, campaign_action) VALUES (  '"+ creationDate +"', '"+ response_date +"', '"+ survey +"', '"+ target +"', '"+ user +"', '"+ campaign_action +"')";
		LOGGER.debug(message);

		if ( creationDate == null ) {
			stmt.setNull(1, 93);
		}
		else {
			stmt.setTimestamp(1, creationDate);
		}
		if ( response_date == null ) {
			stmt.setNull(2, 93);
		}
		else {
			stmt.setTimestamp(2, response_date);
		}
		if ( survey == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, survey);
		}
		if ( target == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, target);
		}
		if ( user == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, user);
		}
		if ( campaign_action == null ) {
			stmt.setNull(6, 4);
		}
		else {
			stmt.setInt(6, campaign_action);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> survey_responseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Survey_response
	 * @param id Identificador unico
	 * @param creationDate Fecha de la creacion en el sistema de la Respuesta del Cuestionario
	 * @param response_date Fecha de la Respuesta del Cuestionario
	 * @param survey Identificador del Cuestionario
	 * @param target Identificador del Cliente Potencial
	 * @param user Identificador del Usuario
	 * @param campaign_action Identificador de la Accion de la Campaña
	 * @throws SQLException
	*/
	protected int insertSurvey_responseOldIds( Integer oldId , Timestamp creationDate , Timestamp response_date , Integer survey , Integer target , Integer user , Integer campaign_action )
	throws SQLException {
		
		Integer newId = this.survey_responseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsurvey_responseIds.get(null);
		newId = this.insertSurvey_response( creationDate,  response_date,  survey,  target,  user,  campaign_action );
		survey_responseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertSurvey_response_detail( String value_text,  Double value_number,  Timestamp value_date,  Integer question,  Integer surveyResponse )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_response_detail ( value_text, value_number, value_date, question, surveyResponse) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO survey_response_detail ( value_text, value_number, value_date, question, surveyResponse) VALUES (  '"+ value_text +"', '"+ value_number +"', '"+ value_date +"', '"+ question +"', '"+ surveyResponse +"')";
		LOGGER.debug(message);

		if ( value_text == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, value_text);
		}
		if ( value_number == null ) {
			stmt.setNull(2, 8);
		}
		else {
			stmt.setDouble(2, value_number);
		}
		if ( value_date == null ) {
			stmt.setNull(3, 93);
		}
		else {
			stmt.setTimestamp(3, value_date);
		}
		if ( question == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, question);
		}
		if ( surveyResponse == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, surveyResponse);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> survey_response_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Survey_response_detail
	 * @param id Identificador unico
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @param question Identificador de la Pregunta
	 * @param surveyResponse Identificador de la Respuesta del Cuestionario
	 * @throws SQLException
	*/
	protected int insertSurvey_response_detailOldIds( Integer oldId , String value_text , Double value_number , Timestamp value_date , Integer question , Integer surveyResponse )
	throws SQLException {
		
		Integer newId = this.survey_response_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsurvey_response_detailIds.get(null);
		newId = this.insertSurvey_response_detail( value_text,  value_number,  value_date,  question,  surveyResponse );
		survey_response_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertSurvey_workflow( Integer questionValue,  Integer surveyQuestion,  Integer nextSurveyQuestion,  Short operator,  String value_text,  Double value_number,  Timestamp value_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO survey_workflow ( questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO survey_workflow ( questionValue, surveyQuestion, nextSurveyQuestion, operator, value_text, value_number, value_date) VALUES (  '"+ questionValue +"', '"+ surveyQuestion +"', '"+ nextSurveyQuestion +"', '"+ operator +"', '"+ value_text +"', '"+ value_number +"', '"+ value_date +"')";
		LOGGER.debug(message);

		if ( questionValue == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, questionValue);
		}
		if ( surveyQuestion == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, surveyQuestion);
		}
		if ( nextSurveyQuestion == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, nextSurveyQuestion);
		}
		if ( operator == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, operator);
		}
		if ( value_text == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, value_text);
		}
		if ( value_number == null ) {
			stmt.setNull(6, 8);
		}
		else {
			stmt.setDouble(6, value_number);
		}
		if ( value_date == null ) {
			stmt.setNull(7, 93);
		}
		else {
			stmt.setTimestamp(7, value_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> survey_workflowIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertSurvey_workflowOldIds( Integer oldId , Integer questionValue , Integer surveyQuestion , Integer nextSurveyQuestion , Short operator , String value_text , Double value_number , Timestamp value_date )
	throws SQLException {
		
		Integer newId = this.survey_workflowIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxsurvey_workflowIds.get(null);
		newId = this.insertSurvey_workflow( questionValue,  surveyQuestion,  nextSurveyQuestion,  operator,  value_text,  value_number,  value_date );
		survey_workflowIds.put(oldId,newId);
		
		return newId;
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
	protected void insertTarget( Integer registry,  Integer tariff,  Short advertising,  Boolean surcharge,  Boolean withholding,  Short transaction,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target ( registry, tariff, advertising, surcharge, withholding, transaction, status) VALUES ( ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO target ( registry, tariff, advertising, surcharge, withholding, transaction, status) VALUES ( '"+ registry +"', '"+ tariff +"', '"+ advertising +"', '"+ surcharge +"', '"+ withholding +"', '"+ transaction +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( registry == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, registry);
		}
		if ( tariff == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, tariff);
		}
		if ( advertising == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, advertising);
		}
		if ( surcharge == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, surcharge);
		}
		if ( withholding == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, withholding);
		}
		if ( transaction == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, transaction);
		}
		if ( status == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, status);
		}

		stmt.executeUpdate();
		
		stmt.close();
		
	}
		
	


	/**
	 * Target_item
	 * @param target Identificador del Cliente Potencial
	 * @param item Identificador del Articulo
	 * @param status Estado
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTarget_item( Integer target,  Integer item,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_item ( target, item, status) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO target_item ( target, item, status) VALUES (  '"+ target +"', '"+ item +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( item == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, item);
		}
		if ( status == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> target_itemIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Target_item
	 * @param id Identificador unico
	 * @param target Identificador del Cliente Potencial
	 * @param item Identificador del Articulo
	 * @param status Estado
	 * @throws SQLException
	*/
	protected int insertTarget_itemOldIds( Integer oldId , Integer target , Integer item , Short status )
	throws SQLException {
		
		Integer newId = this.target_itemIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtarget_itemIds.get(null);
		newId = this.insertTarget_item( target,  item,  status );
		target_itemIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTarget_profile( Integer target,  Timestamp last_update,  Integer question,  String value_text,  Double value_number,  Timestamp value_date )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_profile ( target, last_update, question, value_text, value_number, value_date) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO target_profile ( target, last_update, question, value_text, value_number, value_date) VALUES (  '"+ target +"', '"+ last_update +"', '"+ question +"', '"+ value_text +"', '"+ value_number +"', '"+ value_date +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( last_update == null ) {
			stmt.setNull(2, 93);
		}
		else {
			stmt.setTimestamp(2, last_update);
		}
		if ( question == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, question);
		}
		if ( value_text == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, value_text);
		}
		if ( value_number == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, value_number);
		}
		if ( value_date == null ) {
			stmt.setNull(6, 93);
		}
		else {
			stmt.setTimestamp(6, value_date);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> target_profileIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Target_profile
	 * @param id Identificador unico
	 * @param target Identificador del Cliente Potencial
	 * @param last_update Fecha de la ultima modificacion del Perfil del Cliente Potencial
	 * @param question Identificador de la Pregunta
	 * @param value_text Valor de tipo texto
	 * @param value_number Valor de tipo numerico
	 * @param value_date Valor de tipo fecha
	 * @throws SQLException
	*/
	protected int insertTarget_profileOldIds( Integer oldId , Integer target , Timestamp last_update , Integer question , String value_text , Double value_number , Timestamp value_date )
	throws SQLException {
		
		Integer newId = this.target_profileIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtarget_profileIds.get(null);
		newId = this.insertTarget_profile( target,  last_update,  question,  value_text,  value_number,  value_date );
		target_profileIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTarget_seller( Integer target,  Integer seller,  Date start_date,  Date end_date,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_seller ( target, seller, start_date, end_date, status) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO target_seller ( target, seller, start_date, end_date, status) VALUES (  '"+ target +"', '"+ seller +"', '"+ start_date +"', '"+ end_date +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( seller == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, seller);
		}
		if ( start_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, end_date);
		}
		if ( status == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> target_sellerIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Target_seller
	 * @param id Identificador unico
	 * @param target Identificador del Cliente Potencial
	 * @param seller Identificador del Comercial
	 * @param start_date Fecha de Inicio
	 * @param end_date Fecha de Fin
	 * @param status Estado
	 * @throws SQLException
	*/
	protected int insertTarget_sellerOldIds( Integer oldId , Integer target , Integer seller , Date start_date , Date end_date , Short status )
	throws SQLException {
		
		Integer newId = this.target_sellerIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtarget_sellerIds.get(null);
		newId = this.insertTarget_seller( target,  seller,  start_date,  end_date,  status );
		target_sellerIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTarget_supplier( Integer target,  Integer supplier,  String target_external_code,  Integer tariff,  Integer pay_method,  Integer number_of_pymnts,  Integer days_to_first_pymnt,  Integer days_between_pymnts,  String pymnt_days,  Integer bank,  String bank_account )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO target_supplier ( target, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO target_supplier ( target, supplier, target_external_code, tariff, pay_method, number_of_pymnts, days_to_first_pymnt, days_between_pymnts, pymnt_days, bank, bank_account) VALUES (  '"+ target +"', '"+ supplier +"', '"+ target_external_code +"', '"+ tariff +"', '"+ pay_method +"', '"+ number_of_pymnts +"', '"+ days_to_first_pymnt +"', '"+ days_between_pymnts +"', '"+ pymnt_days +"', '"+ bank +"', '"+ bank_account +"')";
		LOGGER.debug(message);

		if ( target == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, target);
		}
		if ( supplier == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, supplier);
		}
		if ( target_external_code == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, target_external_code);
		}
		if ( tariff == null ) {
			stmt.setNull(4, 4);
		}
		else {
			stmt.setInt(4, tariff);
		}
		if ( pay_method == null ) {
			stmt.setNull(5, 4);
		}
		else {
			stmt.setInt(5, pay_method);
		}
		if ( number_of_pymnts == null ) {
			stmt.setNull(6, 5);
		}
		else {
			stmt.setInt(6, number_of_pymnts);
		}
		if ( days_to_first_pymnt == null ) {
			stmt.setNull(7, 5);
		}
		else {
			stmt.setInt(7, days_to_first_pymnt);
		}
		if ( days_between_pymnts == null ) {
			stmt.setNull(8, 5);
		}
		else {
			stmt.setInt(8, days_between_pymnts);
		}
		if ( pymnt_days == null ) {
			stmt.setNull(9, 12);
		}
		else {
			stmt.setString(9, pymnt_days);
		}
		if ( bank == null ) {
			stmt.setNull(10, 4);
		}
		else {
			stmt.setInt(10, bank);
		}
		if ( bank_account == null ) {
			stmt.setNull(11, 12);
		}
		else {
			stmt.setString(11, bank_account);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> target_supplierIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Target_supplier
	 * @param id Identificador unico
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
	 * @throws SQLException
	*/
	protected int insertTarget_supplierOldIds( Integer oldId , Integer target , Integer supplier , String target_external_code , Integer tariff , Integer pay_method , Integer number_of_pymnts , Integer days_to_first_pymnt , Integer days_between_pymnts , String pymnt_days , Integer bank , String bank_account )
	throws SQLException {
		
		Integer newId = this.target_supplierIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtarget_supplierIds.get(null);
		newId = this.insertTarget_supplier( target,  supplier,  target_external_code,  tariff,  pay_method,  number_of_pymnts,  days_to_first_pymnt,  days_between_pymnts,  pymnt_days,  bank,  bank_account );
		target_supplierIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Tariff
	 * @param name Nombre de la Tarifa
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTariff( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tariff ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tariff ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tariffIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tariff
	 * @param id Identificador unico de la Tarifa
	 * @param name Nombre de la Tarifa
	 * @throws SQLException
	*/
	protected int insertTariffOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.tariffIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertTariff( name );
		tariffIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Tariff_catalogue
	 * @param tariff Identificador de la Tarifa
	 * @param catalogue Identificador del Catalogo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTariff_catalogue( Integer tariff,  Integer catalogue )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tariff_catalogue ( tariff, catalogue) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tariff_catalogue ( tariff, catalogue) VALUES (  '"+ tariff +"', '"+ catalogue +"')";
		LOGGER.debug(message);

		if ( tariff == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, tariff);
		}
		if ( catalogue == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, catalogue);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tariff_catalogueIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tariff_catalogue
	 * @param id Identificador unico
	 * @param tariff Identificador de la Tarifa
	 * @param catalogue Identificador del Catalogo
	 * @throws SQLException
	*/
	protected int insertTariff_catalogueOldIds( Integer oldId , Integer tariff , Integer catalogue )
	throws SQLException {
		
		Integer newId = this.tariff_catalogueIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtariff_catalogueIds.get(null);
		newId = this.insertTariff_catalogue( tariff,  catalogue );
		tariff_catalogueIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Tas_delivery
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param delivery Identificador del Albaran
	 * @param offer Identificador del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTas_delivery( Integer support_order,  Integer delivery,  Integer offer )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_delivery ( support_order, delivery, offer) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tas_delivery ( support_order, delivery, offer) VALUES (  '"+ support_order +"', '"+ delivery +"', '"+ offer +"')";
		LOGGER.debug(message);

		if ( support_order == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, support_order);
		}
		if ( delivery == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, delivery);
		}
		if ( offer == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, offer);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tas_deliveryIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tas_delivery
	 * @param id Identificador unico de la Relacion de Ordenes de Reparacion y Albaranes
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param delivery Identificador del Albaran
	 * @param offer Identificador del Presupuesto
	 * @throws SQLException
	*/
	protected int insertTas_deliveryOldIds( Integer oldId , Integer support_order , Integer delivery , Integer offer )
	throws SQLException {
		
		Integer newId = this.tas_deliveryIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtas_deliveryIds.get(null);
		newId = this.insertTas_delivery( support_order,  delivery,  offer );
		tas_deliveryIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTas_item( Integer model,  String publicCode,  String privateCode,  String description,  String add_info )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_item ( model, publicCode, privateCode, description, add_info) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tas_item ( model, publicCode, privateCode, description, add_info) VALUES (  '"+ model +"', '"+ publicCode +"', '"+ privateCode +"', '"+ description +"', '"+ add_info +"')";
		LOGGER.debug(message);

		if ( model == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, model);
		}
		if ( publicCode == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, publicCode);
		}
		if ( privateCode == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, privateCode);
		}
		if ( description == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, description);
		}
		if ( add_info == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, add_info);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tas_itemIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tas_item
	 * @param id Identificador unico del Articulo
	 * @param model Identificador del Modelo
	 * @param publicCode Codigo publico del Articulo
	 * @param privateCode Codigo privado del Articulo
	 * @param description Descripcion del Articulo
	 * @param add_info Informacion adicional del Articulo
	 * @throws SQLException
	*/
	protected int insertTas_itemOldIds( Integer oldId , Integer model , String publicCode , String privateCode , String description , String add_info )
	throws SQLException {
		
		Integer newId = this.tas_itemIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtas_itemIds.get(null);
		newId = this.insertTas_item( model,  publicCode,  privateCode,  description,  add_info );
		tas_itemIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Tas_offer
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param offer Identificador del Presupuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTas_offer( Integer support_order,  Integer offer )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tas_offer ( support_order, offer) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tas_offer ( support_order, offer) VALUES (  '"+ support_order +"', '"+ offer +"')";
		LOGGER.debug(message);

		if ( support_order == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, support_order);
		}
		if ( offer == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, offer);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tas_offerIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tas_offer
	 * @param id Identificador unico de la Relacion de Ordenes de Reparacion y Presupuestos
	 * @param support_order Identificador de la Orden de Reparacion
	 * @param offer Identificador del Presupuesto
	 * @throws SQLException
	*/
	protected int insertTas_offerOldIds( Integer oldId , Integer support_order , Integer offer )
	throws SQLException {
		
		Integer newId = this.tas_offerIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtas_offerIds.get(null);
		newId = this.insertTas_offer( support_order,  offer );
		tas_offerIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTask( String description,  Date start_date,  Date end_date,  Date due_date,  Short priority,  Short status,  Short percent,  Integer user_id,  Integer workgroup,  Short source,  Integer dossier,  Integer activity,  Integer sender,  InputStream comments,  Short repeat_period )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO task ( description, start_date, end_date, due_date, priority, status, percent, user_id, workgroup, source, dossier, activity, sender, comments, repeat_period) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO task ( description, start_date, end_date, due_date, priority, status, percent, user_id, workgroup, source, dossier, activity, sender, comments, repeat_period) VALUES (  '"+ description +"', '"+ start_date +"', '"+ end_date +"', '"+ due_date +"', '"+ priority +"', '"+ status +"', '"+ percent +"', '"+ user_id +"', '"+ workgroup +"', '"+ source +"', '"+ dossier +"', '"+ activity +"', '"+ sender +"', '"+ comments +"', '"+ repeat_period +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( start_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, end_date);
		}
		if ( due_date == null ) {
			stmt.setNull(4, 91);
		}
		else {
			stmt.setDate(4, due_date);
		}
		if ( priority == null ) {
			stmt.setNull(5, -6);
		}
		else {
			stmt.setShort(5, priority);
		}
		if ( status == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, status);
		}
		if ( percent == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, percent);
		}
		if ( user_id == null ) {
			stmt.setNull(8, 4);
		}
		else {
			stmt.setInt(8, user_id);
		}
		if ( workgroup == null ) {
			stmt.setNull(9, 4);
		}
		else {
			stmt.setInt(9, workgroup);
		}
		if ( source == null ) {
			stmt.setNull(10, -6);
		}
		else {
			stmt.setShort(10, source);
		}
		if ( dossier == null ) {
			stmt.setNull(11, 4);
		}
		else {
			stmt.setInt(11, dossier);
		}
		if ( activity == null ) {
			stmt.setNull(12, 4);
		}
		else {
			stmt.setInt(12, activity);
		}
		if ( sender == null ) {
			stmt.setNull(13, 4);
		}
		else {
			stmt.setInt(13, sender);
		}
		if ( comments == null ) {
			stmt.setNull(14, -1);
		}
		else {
			stmt.setAsciiStream(14, comments);
		}
		if ( repeat_period == null ) {
			stmt.setNull(15, -6);
		}
		else {
			stmt.setShort(15, repeat_period);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> taskIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Task
	 * @param id Identificador unico de la Tarea
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
	 * @throws SQLException
	*/
	protected int insertTaskOldIds( Integer oldId , String description , Date start_date , Date end_date , Date due_date , Short priority , Short status , Short percent , Integer user_id , Integer workgroup , Short source , Integer dossier , Integer activity , Integer sender , InputStream comments , Short repeat_period )
	throws SQLException {
		
		Integer newId = this.taskIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtaskIds.get(null);
		newId = this.insertTask( description,  start_date,  end_date,  due_date,  priority,  status,  percent,  user_id,  workgroup,  source,  dossier,  activity,  sender,  comments,  repeat_period );
		taskIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTax( String name,  Short tax_type,  Double percentage,  Double surcharge,  Date start_date,  Short vat_deduction_type,  Short withholding_type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax ( name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type) VALUES (  ?, ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tax ( name, tax_type, percentage, surcharge, start_date, vat_deduction_type, withholding_type) VALUES (  '"+ name +"', '"+ tax_type +"', '"+ percentage +"', '"+ surcharge +"', '"+ start_date +"', '"+ vat_deduction_type +"', '"+ withholding_type +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( tax_type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, tax_type);
		}
		if ( percentage == null ) {
			stmt.setNull(3, 8);
		}
		else {
			stmt.setDouble(3, percentage);
		}
		if ( surcharge == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, surcharge);
		}
		if ( start_date == null ) {
			stmt.setNull(5, 91);
		}
		else {
			stmt.setDate(5, start_date);
		}
		if ( vat_deduction_type == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, vat_deduction_type);
		}
		if ( withholding_type == null ) {
			stmt.setNull(7, -6);
		}
		else {
			stmt.setShort(7, withholding_type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> taxIds = 
		new HashMap<Integer, Integer>();
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
	 * @throws SQLException
	*/
	protected int insertTaxOldIds( Integer oldId , String name , Short tax_type , Double percentage , Double surcharge , Date start_date , Short vat_deduction_type , Short withholding_type )
	throws SQLException {
		
		Integer newId = this.taxIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertTax( name,  tax_type,  percentage,  surcharge,  start_date,  vat_deduction_type,  withholding_type );
		taxIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Tax_account
	 * @param tax Identificador del Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Impuesto
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertTax_account( Integer tax,  String account,  Short type )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax_account ( tax, account, type) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tax_account ( tax, account, type) VALUES (  '"+ tax +"', '"+ account +"', '"+ type +"')";
		LOGGER.debug(message);

		if ( tax == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, tax);
		}
		if ( account == null ) {
			stmt.setNull(2, 1);
		}
		else {
			stmt.setString(2, account);
		}
		if ( type == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, type);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tax_accountIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tax_account
	 * @param id Identificador unico de la Cuenta Contable del Impuesto
	 * @param tax Identificador del Impuesto
	 * @param account Identificador de la Cuenta Contable
	 * @param type Tipo de Cuenta Contable del Impuesto
	 * @throws SQLException
	*/
	protected int insertTax_accountOldIds( Integer oldId , Integer tax , String account , Short type )
	throws SQLException {
		
		Integer newId = this.tax_accountIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtax_accountIds.get(null);
		newId = this.insertTax_account( tax,  account,  type );
		tax_accountIds.put(oldId,newId);
		
		return newId;
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
	protected int insertTax_detail( Integer tax,  Date start_date,  Date end_date,  Double value,  Double surcharge )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO tax_detail ( tax, start_date, end_date, value, surcharge) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO tax_detail ( tax, start_date, end_date, value, surcharge) VALUES (  '"+ tax +"', '"+ start_date +"', '"+ end_date +"', '"+ value +"', '"+ surcharge +"')";
		LOGGER.debug(message);

		if ( tax == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, tax);
		}
		if ( start_date == null ) {
			stmt.setNull(2, 91);
		}
		else {
			stmt.setDate(2, start_date);
		}
		if ( end_date == null ) {
			stmt.setNull(3, 91);
		}
		else {
			stmt.setDate(3, end_date);
		}
		if ( value == null ) {
			stmt.setNull(4, 8);
		}
		else {
			stmt.setDouble(4, value);
		}
		if ( surcharge == null ) {
			stmt.setNull(5, 8);
		}
		else {
			stmt.setDouble(5, surcharge);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> tax_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Tax_detail
	 * @param id Identificador unico del Historico de Impuestos
	 * @param tax Identificador del Impuesto
	 * @param start_date Fecha de inicio de vigencia
	 * @param end_date Fecha de fin de vigencia
	 * @param value Porcentaje de recargo
	 * @param surcharge Porcentaje de recargo de equivalencia
	 * @throws SQLException
	*/
	protected int insertTax_detailOldIds( Integer oldId , Integer tax , Date start_date , Date end_date , Double value , Double surcharge )
	throws SQLException {
		
		Integer newId = this.tax_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxtax_detailIds.get(null);
		newId = this.insertTax_detail( tax,  start_date,  end_date,  value,  surcharge );
		tax_detailIds.put(oldId,newId);
		
		return newId;
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
	protected int insertUser( String name,  String login,  Boolean available,  Boolean validate,  String aon_key,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user ( name, login, available, validate, aon_key, status) VALUES (  ?, ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO user ( name, login, available, validate, aon_key, status) VALUES (  '"+ name +"', '"+ login +"', '"+ available +"', '"+ validate +"', '"+ aon_key +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( login == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, login);
		}
		if ( available == null ) {
			stmt.setNull(3, -7);
		}
		else {
			stmt.setBoolean(3, available);
		}
		if ( validate == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, validate);
		}
		if ( aon_key == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, aon_key);
		}
		if ( status == null ) {
			stmt.setNull(6, -6);
		}
		else {
			stmt.setShort(6, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> userIds = 
		new HashMap<Integer, Integer>();
	/**
	 * User
	 * @param id Identificador unico
	 * @param name Nombre del Usuario
	 * @param login Login del Usuario
	 * @param available Indica si el Usuario esta disponible o no
	 * @param validate Indica si el Usuario requiere validacion o no de la clave hardware
	 * @param aon_key Campo alfanumerico donde se guarda la ultima clave hardware generada
	 * @param status Estado del Usuario con respecto a su primera validacion de la clave hardware
	 * @throws SQLException
	*/
	protected int insertUserOldIds( Integer oldId , String name , String login , Boolean available , Boolean validate , String aon_key , Short status )
	throws SQLException {
		
		Integer newId = this.userIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertUser( name,  login,  available,  validate,  aon_key,  status );
		userIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * User_scope
	 * @param user_id Identificador del Usuario
	 * @param scope Identificador del Ambito
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser_scope( Integer user_id,  Integer scope )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user_scope ( user_id, scope) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO user_scope ( user_id, scope) VALUES (  '"+ user_id +"', '"+ scope +"')";
		LOGGER.debug(message);

		if ( user_id == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, user_id);
		}
		if ( scope == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, scope);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> user_scopeIds = 
		new HashMap<Integer, Integer>();
	/**
	 * User_scope
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param scope Identificador del Ambito
	 * @throws SQLException
	*/
	protected int insertUser_scopeOldIds( Integer oldId , Integer user_id , Integer scope )
	throws SQLException {
		
		Integer newId = this.user_scopeIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxuser_scopeIds.get(null);
		newId = this.insertUser_scope( user_id,  scope );
		user_scopeIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * User_workgroup
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertUser_workgroup( Integer user_id,  Integer workgroup )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO user_workgroup ( user_id, workgroup) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO user_workgroup ( user_id, workgroup) VALUES (  '"+ user_id +"', '"+ workgroup +"')";
		LOGGER.debug(message);

		if ( user_id == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, user_id);
		}
		if ( workgroup == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, workgroup);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> user_workgroupIds = 
		new HashMap<Integer, Integer>();
	/**
	 * User_workgroup
	 * @param id Identificador unico
	 * @param user_id Identificador del Usuario
	 * @param workgroup Identificador del Grupo de Trabajo
	 * @throws SQLException
	*/
	protected int insertUser_workgroupOldIds( Integer oldId , Integer user_id , Integer workgroup )
	throws SQLException {
		
		Integer newId = this.user_workgroupIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxuser_workgroupIds.get(null);
		newId = this.insertUser_workgroup( user_id,  workgroup );
		user_workgroupIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Warehouse
	 * @param name Nombre del Almacen
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWarehouse( String name )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO warehouse ( name) VALUES (  ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO warehouse ( name) VALUES (  '"+ name +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> warehouseIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Warehouse
	 * @param id Identificador unico del Almacen
	 * @param name Nombre del Almacen
	 * @throws SQLException
	*/
	protected int insertWarehouseOldIds( Integer oldId , String name )
	throws SQLException {
		
		Integer newId = this.warehouseIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertWarehouse( name );
		warehouseIds.put(oldId,newId);
		
		return newId;
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
	protected int insertWeb_info( Integer company,  InputStream commercial_description,  InputStream schedule,  String slogan )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info ( company, commercial_description, schedule, slogan) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO web_info ( company, commercial_description, schedule, slogan) VALUES (  '"+ company +"', '"+ commercial_description +"', '"+ schedule +"', '"+ slogan +"')";
		LOGGER.debug(message);

		if ( company == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, company);
		}
		if ( commercial_description == null ) {
			stmt.setNull(2, -1);
		}
		else {
			stmt.setAsciiStream(2, commercial_description);
		}
		if ( schedule == null ) {
			stmt.setNull(3, -1);
		}
		else {
			stmt.setAsciiStream(3, schedule);
		}
		if ( slogan == null ) {
			stmt.setNull(4, 12);
		}
		else {
			stmt.setString(4, slogan);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> web_infoIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Web_info
	 * @param id Identificador Unico
	 * @param company Empresa
	 * @param commercial_description Descripcion comercial
	 * @param schedule Horario
	 * @param slogan Slogan
	 * @throws SQLException
	*/
	protected int insertWeb_infoOldIds( Integer oldId , Integer company , InputStream commercial_description , InputStream schedule , String slogan )
	throws SQLException {
		
		Integer newId = this.web_infoIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxweb_infoIds.get(null);
		newId = this.insertWeb_info( company,  commercial_description,  schedule,  slogan );
		web_infoIds.put(oldId,newId);
		
		return newId;
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
	protected int insertWeb_info_page( String name,  Short type,  Short position,  Boolean active )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page ( name, type, position, active) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO web_info_page ( name, type, position, active) VALUES (  '"+ name +"', '"+ type +"', '"+ position +"', '"+ active +"')";
		LOGGER.debug(message);

		if ( name == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, name);
		}
		if ( type == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, type);
		}
		if ( position == null ) {
			stmt.setNull(3, -6);
		}
		else {
			stmt.setShort(3, position);
		}
		if ( active == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, active);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> web_info_pageIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Web_info_page
	 * @param id Codigo de la Pagina
	 * @param name Nombre de la Pagina.
	 * @param type Tipo de Pagina
	 * @param position Posicion de la Pagina en el menu
	 * @param active Indica si la Pagina esta activa o no
	 * @throws SQLException
	*/
	protected int insertWeb_info_pageOldIds( Integer oldId , String name , Short type , Short position , Boolean active )
	throws SQLException {
		
		Integer newId = this.web_info_pageIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertWeb_info_page( name,  type,  position,  active );
		web_info_pageIds.put(oldId,newId);
		
		return newId;
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
	protected int insertWeb_info_page_detail( Integer web_info_page,  String title,  Integer layout,  InputStream content,  String extra )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page_detail ( web_info_page, title, layout, content, extra) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO web_info_page_detail ( web_info_page, title, layout, content, extra) VALUES (  '"+ web_info_page +"', '"+ title +"', '"+ layout +"', '"+ content +"', '"+ extra +"')";
		LOGGER.debug(message);

		if ( web_info_page == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, web_info_page);
		}
		if ( title == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, title);
		}
		if ( layout == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, layout);
		}
		if ( content == null ) {
			stmt.setNull(4, -1);
		}
		else {
			stmt.setAsciiStream(4, content);
		}
		if ( extra == null ) {
			stmt.setNull(5, 12);
		}
		else {
			stmt.setString(5, extra);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> web_info_page_detailIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Web_info_page_detail
	 * @param id Codigo del Detalle de la Pagina
	 * @param web_info_page Identificador de la Pagina a la que corresponde el detalle
	 * @param title Titulo del contenido de la Pagina
	 * @param layout Tipo de plantilla
	 * @param content Texto del contenido de la Pagina
	 * @param extra Campo reservado a otros datos de la Pagina
	 * @throws SQLException
	*/
	protected int insertWeb_info_page_detailOldIds( Integer oldId , Integer web_info_page , String title , Integer layout , InputStream content , String extra )
	throws SQLException {
		
		Integer newId = this.web_info_page_detailIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxweb_info_page_detailIds.get(null);
		newId = this.insertWeb_info_page_detail( web_info_page,  title,  layout,  content,  extra );
		web_info_page_detailIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Web_info_page_resource
	 * @param web_info_page Codigo de la Pagina
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param content Texto del Recurso
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_page_resource( Integer web_info_page,  Integer rattach,  String content )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_page_resource ( web_info_page, rattach, content) VALUES (  ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO web_info_page_resource ( web_info_page, rattach, content) VALUES (  '"+ web_info_page +"', '"+ rattach +"', '"+ content +"')";
		LOGGER.debug(message);

		if ( web_info_page == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, web_info_page);
		}
		if ( rattach == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, rattach);
		}
		if ( content == null ) {
			stmt.setNull(3, 12);
		}
		else {
			stmt.setString(3, content);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> web_info_page_resourceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Web_info_page_resource
	 * @param id Codigo del Recurso de la Pagina
	 * @param web_info_page Codigo de la Pagina
	 * @param rattach Identificador del Archivo Adjunto calificado como Recurso
	 * @param content Texto del Recurso
	 * @throws SQLException
	*/
	protected int insertWeb_info_page_resourceOldIds( Integer oldId , Integer web_info_page , Integer rattach , String content )
	throws SQLException {
		
		Integer newId = this.web_info_page_resourceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxweb_info_page_resourceIds.get(null);
		newId = this.insertWeb_info_page_resource( web_info_page,  rattach,  content );
		web_info_page_resourceIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Web_info_style
	 * @param variable Nombre de la variable del Estilo
	 * @param value Valor de la variable del Estilo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWeb_info_style( String variable,  String value )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO web_info_style ( variable, value) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO web_info_style ( variable, value) VALUES (  '"+ variable +"', '"+ value +"')";
		LOGGER.debug(message);

		if ( variable == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, variable);
		}
		if ( value == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, value);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> web_info_styleIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Web_info_style
	 * @param id Codigo del Estilo de la Pagina
	 * @param variable Nombre de la variable del Estilo
	 * @param value Valor de la variable del Estilo
	 * @throws SQLException
	*/
	protected int insertWeb_info_styleOldIds( Integer oldId , String variable , String value )
	throws SQLException {
		
		Integer newId = this.web_info_styleIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertWeb_info_style( variable,  value );
		web_info_styleIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Workactivity
	 * @param description Descripcion de la Actividad
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterpriseCCC Cuenta de Cotización asociada a la Actividad
	 * @param active Indica si la Actividad esta activa o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkactivity( String description,  Integer workplace,  Integer enterpriseCCC,  Boolean active )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workactivity ( description, workplace, enterpriseCCC, active) VALUES (  ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO workactivity ( description, workplace, enterpriseCCC, active) VALUES (  '"+ description +"', '"+ workplace +"', '"+ enterpriseCCC +"', '"+ active +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( workplace == null ) {
			stmt.setNull(2, 4);
		}
		else {
			stmt.setInt(2, workplace);
		}
		if ( enterpriseCCC == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, enterpriseCCC);
		}
		if ( active == null ) {
			stmt.setNull(4, -7);
		}
		else {
			stmt.setBoolean(4, active);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> workactivityIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Workactivity
	 * @param id Identificador unico de la Actividad
	 * @param description Descripcion de la Actividad
	 * @param workplace Identificador del Centro de Trabajo
	 * @param enterpriseCCC Cuenta de Cotización asociada a la Actividad
	 * @param active Indica si la Actividad esta activa o no
	 * @throws SQLException
	*/
	protected int insertWorkactivityOldIds( Integer oldId , String description , Integer workplace , Integer enterpriseCCC , Boolean active )
	throws SQLException {
		
		Integer newId = this.workactivityIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxworkactivityIds.get(null);
		newId = this.insertWorkactivity( description,  workplace,  enterpriseCCC,  active );
		workactivityIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Workgroup
	 * @param description Descripcion del Grupo de Trabajo
	 * @param status Estado del grupo de Trabajo
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkgroup( String description,  Short status )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workgroup ( description, status) VALUES (  ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO workgroup ( description, status) VALUES (  '"+ description +"', '"+ status +"')";
		LOGGER.debug(message);

		if ( description == null ) {
			stmt.setNull(1, 12);
		}
		else {
			stmt.setString(1, description);
		}
		if ( status == null ) {
			stmt.setNull(2, -6);
		}
		else {
			stmt.setShort(2, status);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> workgroupIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Workgroup
	 * @param id Identificador unico del Grupo de Trabajo
	 * @param description Descripcion del Grupo de Trabajo
	 * @param status Estado del grupo de Trabajo
	 * @throws SQLException
	*/
	protected int insertWorkgroupOldIds( Integer oldId , String description , Short status )
	throws SQLException {
		
		Integer newId = this.workgroupIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		newId = this.insertWorkgroup( description,  status );
		workgroupIds.put(oldId,newId);
		
		return newId;
	}

	/**
	 * Workplace
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @returns auto-generated key
	 * @throws SQLException
	*/
	protected int insertWorkplace( Integer enterprise,  String description,  Integer address,  Short economicAgreement,  Boolean active )
	throws SQLException {
	
		PreparedStatement stmt = 
			mysqlConnection.prepareStatement(
			"INSERT INTO workplace ( enterprise, description, address, economicAgreement, active) VALUES (  ?, ?, ?, ?, ?)", 
			PreparedStatement.RETURN_GENERATED_KEYS);

		String message = "INSERT INTO workplace ( enterprise, description, address, economicAgreement, active) VALUES (  '"+ enterprise +"', '"+ description +"', '"+ address +"', '"+ economicAgreement +"', '"+ active +"')";
		LOGGER.debug(message);

		if ( enterprise == null ) {
			stmt.setNull(1, 4);
		}
		else {
			stmt.setInt(1, enterprise);
		}
		if ( description == null ) {
			stmt.setNull(2, 12);
		}
		else {
			stmt.setString(2, description);
		}
		if ( address == null ) {
			stmt.setNull(3, 4);
		}
		else {
			stmt.setInt(3, address);
		}
		if ( economicAgreement == null ) {
			stmt.setNull(4, -6);
		}
		else {
			stmt.setShort(4, economicAgreement);
		}
		if ( active == null ) {
			stmt.setNull(5, -7);
		}
		else {
			stmt.setBoolean(5, active);
		}

		stmt.executeUpdate();
		
		int 		generatedKey = 0;
		ResultSet generatedKeys = stmt.getGeneratedKeys();
		if ( generatedKeys.next() ){
			generatedKey = generatedKeys.getInt(1);
		}
		stmt.close();
		
		return generatedKey;
		
	}
		
	

	private HashMap<Integer, Integer> workplaceIds = 
		new HashMap<Integer, Integer>();
	/**
	 * Workplace
	 * @param id Identificador unico del Centro de Trabajo
	 * @param enterprise Empresa asociada al Centro de Trabajo
	 * @param description Descripcion del Centro de Trabajo
	 * @param address Identificador de la Direccion
	 * @param economicAgreement Concierto Economico del Centro de Trabajo
	 * @param active Indica si el Centro de Trabajo esta activo o no
	 * @throws SQLException
	*/
	protected int insertWorkplaceOldIds( Integer oldId , Integer enterprise , String description , Integer address , Short economicAgreement , Boolean active )
	throws SQLException {
		
		Integer newId = this.workplaceIds.get(oldId);
		
		if ( newId != null ) 
			return newId;

		xxxxxxxxxxxxxworkplaceIds.get(null);
		newId = this.insertWorkplace( enterprise,  description,  address,  economicAgreement,  active );
		workplaceIds.put(oldId,newId);
		
		return newId;
	}


	public AbstractMysqlDB( Connection mysqlConnection) {
		this.mysqlConnection = mysqlConnection;
	}

}		
	
	
