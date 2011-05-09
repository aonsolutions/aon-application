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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.enumeration.Country;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.GenderNotFoundException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidEmailException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.InvalidTelephoneException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.MaritalStatusNotFoundException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.NullGenderException;
import com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.NullMaritalStatusException;

/**
 * @author rtrepiana
 *
 */
public class MyPerson extends DefaultCtsqlDBVisitor implements IPersons{

	
	private static class Person {
		private Integer id;
		private String numDoc;
		private String name;
		private Date fecNac;
		
		private Person ( Integer id, String numdoc, String name, Date fecNac ) {
			this.id = id;
			this.numDoc = numdoc;
			this.name = name;
			this.fecNac = fecNac;
		}
	}
	
	
	private DefaultMysqlDB mysqlDB;
	
	private Map<String, Integer> cifs = 
		new HashMap<String, Integer>();

	private Map<Integer, Person> persons = 
		new HashMap<Integer, Person>();

	public MyPerson(DefaultMysqlDB defaultMysqlDB) throws SQLException {
		this.mysqlDB = defaultMysqlDB;
	}
	
	@Override
	public Integer getPerson(Integer oldCdg) {
		Person person = persons.get(oldCdg);
		return person == null ? null: person.id;
	}
	
	@Override
	public String getNumDoc(Integer oldCdg) {
		Person person = persons.get(oldCdg);
		return person == null ? null: person.numDoc;
	}

	@Override
	public String getName(Integer oldCdg) {
		Person person = persons.get(oldCdg);
		return person == null ? null: person.name;
	}

	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitPersona(this);
	}
	
	@Override
	public void visitPersona(Persona persona) throws SQLException { 
		
		String numDoc = persona.getNumdoc();
		
		Integer registry = cifs.get(numDoc);
		if ( registry != null  ) {
			persons.put(persona.getCdg(), new Person ( registry, numDoc, persona.getNombre(), persona.getFecnac()));
			return;
		}
		
		Gender gender = Gender.UNKNOWN;
		try {
			gender = mysqlDB.getGender(persona.getSexo());
		} catch (NullGenderException e1) {
			MysqlDB.debug("persona[{}]: Null gender.", persona.getCdg());
		} catch (GenderNotFoundException e1) {
			MysqlDB.debug("persona[{}]: Not found gender {}", persona.getCdg(), persona.getSexo());
		}
		
		MaritalStatus maritalStatus = MaritalStatus.UNKNOWN;
		try {
			maritalStatus = mysqlDB.getMaritalStatus(persona.getEstciv());
		} catch (NullMaritalStatusException e1) {
			MysqlDB.debug("persona[{}]: Null marital status", persona.getCdg());
		} catch (MaritalStatusNotFoundException e1) {
			MysqlDB.debug("persona[{}]: Not found marital status {}", persona.getCdg(), persona.getEstciv());
		}
		
		
		DocumentType docType = mysqlDB.getDocumentType(persona.getInddoc());
		if ( docType == null )
			docType = DocumentType.NIF;
		
		Country country = mysqlDB.getCountry( persona.getPainac() );
		Country docCountry = mysqlDB.getCountry( persona.getPaiemi() );
		
		registry = mysqlDB.insertPerson(persona.getNumdoc(), 
				docType,
				docCountry,
				persona.getNombre(), 
				persona.getDescripcion(), 
				persona.getApellido2(), 
				persona.getAlias(), 		
				persona.getFecnac(),
				country,
				DefaultMysqlDB.enum2short(gender), 
				DefaultMysqlDB.enum2short(maritalStatus), 
				persona.getNumss());
	
		Integer geozone = 
			mysqlDB.getGeoZone(persona.getProvincia());
		
		Integer raddress = 
			mysqlDB.insertRaddress(registry, 
				DefaultMysqlDB.enum2short(AddressType.MAIN), 
				null, 									//TODO: raddress 'recipient'
				persona.getTipovia(), 
				persona.getNomvia(), 
				persona.getNumero(), 
				persona.getOtrdir(),
				null,
				persona.getCodpos(), 
				persona.getLocalidad(), 
				geozone,
				null);
			
		try {
			mysqlDB.insertEmail(registry, raddress, persona.getEmail()) ;
		} catch (InvalidEmailException e) {
			MysqlDB.debug("persona[{}] : Invalid email {}", 
					persona.getCdg(), persona.getEmail());
		}
		try {
			mysqlDB.insertTelephone (registry, raddress, persona.getTelefono() );
		} catch (InvalidTelephoneException e) {
			MysqlDB.debug("persona[{}] : Invalid telephone {}", 
					persona.getCdg(), persona.getTelefono());
		}

		persons.put(persona.getCdg(), new Person ( registry, numDoc, persona.getNombre(), persona.getFecnac()));
	}
	
	
}
