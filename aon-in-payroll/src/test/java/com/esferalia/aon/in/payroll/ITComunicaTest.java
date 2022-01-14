package com.esferalia.aon.in.payroll;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.tgss.its.ITComunica;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;

import solutions.aon.seg.social.TestItRegister;
import solutions.aon.seg.social.TestSistemaREDI;

public class ITComunicaTest {
	
	@Test
	@Ignore
	public void syncUpITs() {
		try (final InputStream is = TestSistemaREDI.class.getResourceAsStream("AyudaTFNMT.p12")) {
			
				Optional<String> nss = Optional.empty();
				Domain domain = new Domain().setName("w3319674b-ayudat.rvasquez.net").setId(9122);
				
				ITComunica.syncUpITs(is.readAllBytes(), "123456", "pkcs12", domain, nss);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	@Ignore
	public void communicateITs() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			
			//PAMETERS REQUIRED
			EmployeeIT employeeIt =  new EmployeeIT()
			.setRegime("0111")
			.setCcc("01105360062")
			.setNss("010022757387")
			.setStartDate(new Date()) //FECHA DE BAJA
			.setType(ContractLeaveType.ENFERMEDAD_COMUN)
			;
			
			//EXAMPLE IT BAJA
			employeeIt.setDailyCgcBase(20.45).setQuoteDays(1);
			EmployeeITPart baja = new EmployeeITPart()
			.setDate(new Date()).setType(ContractLeaveDetailType.BAJA);
			employeeIt.addITPart(baja);
			
			
			//EXAMPLE IT CONFIRMATIONS
			EmployeeITPart confirmation1 = new EmployeeITPart()
			.setDate(new Date()).setType(ContractLeaveDetailType.CONFIRMACION).setConfirmOrder((byte)1);
			employeeIt.addITPart(confirmation1);
			
			EmployeeITPart confirmation2 = new EmployeeITPart()
			.setDate(new Date()).setType(ContractLeaveDetailType.CONFIRMACION).setConfirmOrder((byte)2);
			employeeIt.addITPart(confirmation2);
			
			//EXAMPLE IT ALTA
			employeeIt.setDischargeCause(ContractLeaveDischargeCause.CURACION);
			EmployeeITPart alta = new EmployeeITPart()
			.setDate(new Date()).setType(ContractLeaveDetailType.ALTA);
			employeeIt.addITPart(alta);
			
			List<String> messages = ITComunica.communicateITs(certificateInputStream.readAllBytes(), "jg@FNMT","pkcs12", employeeIt);
			
			for (String message:messages) {
				System.out.println(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void removeITs() {
		try (final InputStream certificateInputStream = TestItRegister.class.getResourceAsStream("FNMT.p12")){
			
			//PAMETERS REQUIRED
			EmployeeIT employeeIt =  new EmployeeIT()
			.setRegime("0111")
			.setCcc("01105360062")
			.setNss("010022757387")
			.setStartDate(new Date()) //FECHA DE BAJA
			;
			
			//EXAMPLE IT BAJA
			EmployeeITPart baja = new EmployeeITPart().setDate(new Date()).setType(ContractLeaveDetailType.BAJA);
			employeeIt.addITPart(baja);
			
			//EXAMPLE IT CONFIRMATIONS
			EmployeeITPart confirmation = new EmployeeITPart().setDate(new Date()).setType(ContractLeaveDetailType.CONFIRMACION);
			employeeIt.addITPart(confirmation);
			
			//EXAMPLE IT ALTA
			EmployeeITPart alta = new EmployeeITPart().setDate(new Date()).setType(ContractLeaveDetailType.ALTA);
			employeeIt.addITPart(alta);
			
			List<String> messages = ITComunica.removeITs(certificateInputStream.readAllBytes(), "jg@FNMT","pkcs12", employeeIt);
			
			for (String message:messages) {
				System.out.println(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
