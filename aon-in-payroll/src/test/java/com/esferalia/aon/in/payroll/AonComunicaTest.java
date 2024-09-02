package com.esferalia.aon.in.payroll;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.payroll.Employee;

import solutions.aon.seg.social.TestSistemaREDI;

public class AonComunicaTest {

	@Test
	@Disabled
	public void addContractAndCommunicate() {
		try (final InputStream is = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {

				Employee employee = new Employee()
				.setRegime("0111")
				.setCcc("01105360062")
				.setName("JULIO GARCIA PEREZ")
				.setNaf("010022757387")
				.setDni("16262835H")
				.setStartDate(new Date("2021/11/30"))
				;
				
				//---------------------ADD CONTRACT_DATA----------------
				employee.addContractType("502", employee.getStartDate(), null);

				employee.addOccupation("a", employee.getStartDate(), null);
				
				employee.addFactor(Double.parseDouble("0.53"), employee.getStartDate(), null);
				
				employee.addQuoteGroup("01", employee.getStartDate(), null);
				
				employee.addRlce("0100", employee.getStartDate(), null);
				
				//--------------ADD CONTRACT_DATA ADDITIONAL---------------------------
				employee.addData("MODELO_COTIZACION_AGRARIO", "-1", employee.getStartDate(), null);
				//-----------ADD CONTRACT_INFO----------
				employee.addInfo("OPCION_CONTRATO", String.format("\"%s\"", "TEMPORARY_OPT1"), employee.getStartDate(), null);
			
				Domain domain = new Domain().setName("w3319674b-ayudat.rvasquez.net").setId(9122);

//			    AonComunica.communicateAlta(employee, certificate);
//				
//			    AonComunica.addContract(is.readAllBytes(), "jg@FNMT", "pkcs12", domain, employee, true);
			
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Disabled
	public void deleteContract() {
		try (final InputStream is = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {

				Employee employee = new Employee()
				.setRegime("0111")
				.setCcc("01105360062")
				.setNaf("010022757387")
				.setDni("16262835H")
				.setStartDate(new Date("2021/11/30"));

				// SETEAR SOLO SI TIENE FECHA DE BAJA
				// employee.setEndDate(new Date("2021/11/30")); 
		
				Domain domain = new Domain().setName("w3319674b-ayudat.rvasquez.net").setId(9122);

			    AonComunica.deleteContract(is.readAllBytes(), "jg@FNMT", "pkcs12", domain, employee , true);
			
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Disabled
	public void contractExist() {
		try {
			Employee data = new Employee()
			.setRegime("0111")
			.setCcc("01105360062")
			.setName("JULIO GARCIA PEREZ")
			.setNaf("010022757387")
			.setDni("16262835H")
			.setStartDate(new Date("2021/11/30"))
			;
		
			Domain domain = new Domain().setName("w3319674b-ayudat.rvasquez.net").setId(9122);
					
			Optional<Employee> employee = PAYROLL.getEmployee(domain.getName(), domain.getId(), "", 
					f->f.getDomainProperty().eq(domain.getId())
					.and(f.getCCCProperty().eq(data.getCcc()))
					.and(f.getNafProperty().eq(data.getNaf()))
					.and( 
							data.getEndDate().isPresent() ?
							f.getStartDateProperty().eq( new java.sql.Date(data.getStartDate().getTime()) ).and(f.getEndDateProperty().eq( new java.sql.Date(data.getEndDate().get().getTime()) ) )  :
							f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(new java.sql.Date(data.getStartDate().getTime()))) 
					)
				
			);
			System.out.println("IS_PRESENT "+employee.isPresent());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
