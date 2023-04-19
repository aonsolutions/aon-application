package com.esferalia.aon.in.payroll;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.tgss.its.ITComunica;
import com.esferalia.aon.in.payroll.tgss.its.ITParse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.EmployeeITPart;
import com.esferalia.aon.occam.api.model.payroll.CCCInfo;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDetailType;
import com.esferalia.aon.occam.api.model.type.ContractLeaveDischargeCause;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.occam.api.model.type.Gender;
import org.htmlunit.ElementNotFoundException;

import solutions.aon.seg.social.Paternity;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.TestItRegister;
import solutions.aon.seg.social.TestSistemaREDI;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.PaternityCertificate;

public class ITComunicaTest {
	
	private static final String CERTIFICATE_PASSWORD = "1234";
	private static final String CERTIFICATE_TYPE = "pkcs12"; 
	private static final String CERTIFICATE_PATH =  System.getProperty("user.home")+"/CERT.pfx"; 
	
	@Test
	@Ignore
	public void getIts() {
		try (final InputStream is = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			
				Optional<String> nss = Optional.empty();
//				Domain domain = new Domain().setName("b72384936-ayudat.aonsolutions.net").setId(7138);
				
				Date startDate = new Date("2021/12/01");
				Date endDate = new Date();
				Collection<It> ssIts = SistemaRED.getIts( is, "jg@FNMT", "pkcs12", 
						"0111", "01105360062", startDate, endDate, Optional.empty());
				for (It ssIt:ssIts) {
					System.out.println(ssIt);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
		
	//	---------------------------PATERNITY---------------------------------------------
	@Test
	@Ignore
	public void getITPaternity() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
				
				Optional<String> nss = Optional.empty();
				Domain domain = new Domain().setName("test-ayudat.rvasquez.net").setId(11950);
				
				Date startDate = new Date("2020/01/01");
				Date endDate = new Date("2021/12/01");
				
				Certificate certificate = new Certificate()
						.setData(certificateInputStream.readAllBytes())
						.setType(CERTIFICATE_TYPE)
						.setPassword(CERTIFICATE_PASSWORD);
				
				CCCInfo ccc = new CCCInfo();
				ccc.setCccRegimeCode("0111");
				ccc.setCccAccount("01105360062");
						
				List<EmployeeIT> list = getITFromTGSSPaternity(domain, certificate, startDate, endDate, ccc, nss);
				for (EmployeeIT employeeIT : list) {
					System.out.println(employeeIT);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//PATERNITY
	private static List<EmployeeIT> getITFromTGSSPaternity(Domain domain, Certificate certificate, Date startDate, Date endDate, CCCInfo ccc, Optional<String>nssOpt) throws SegSocialException, ElementNotFoundException, InterruptedException, IOException {
		List<EmployeeIT> ssIts = new ArrayList<>();
		
		List<PaternityCertificate> paternitys = Paternity.getPaternitys(new ByteArrayInputStream(certificate.getData()), certificate.getPassword(), certificate.getType(), 
				 ccc.getCccRegimeCode(), ccc.getCccAccount(), startDate, endDate, nssOpt, Optional.empty()
		);

		setEmployeeeData(domain, paternitys);
		
		paternitys.forEach(paternity->{
			ssIts.add( ITParse.parsePaternityTGSSToAon(paternity).setDomain(domain.getId()) );
		});
		
		if(!ssIts.isEmpty() && ssIts.get(0).getStartDate()!=null)
			return ssIts;
		return null;
	} 
	
	private static void setEmployeeeData(Domain domain, List<PaternityCertificate> paternitys) {
		List<PaternityCertificate> list = paternitys.stream()
				.filter(it-> it.getWorkerName().isEmpty() && !it.getWorkerNaf().isEmpty())
				.filter(ITComunica.distinctByKey(p-> p.getWorkerNaf().get())).collect(Collectors.toList());

		if(!list.isEmpty()) {
			String[] nssAll = list.stream().map(p-> p.getWorkerNaf().get()).toArray(String[]::new);
			
			AON.getPersonList(domain.getName(), domain.getId(), "", f->f.getSocialSecurityNumProperty().in(nssAll)).forEach(person->{
				paternitys
				.stream()
				.filter(n-> n.getWorkerNaf().get().contentEquals(person.getSocialSecurityNum()))
				.forEach(p-> {
					Gender gender = person.getGender();
					p.setWorkerName(person.getName())
					.setWorkerNif(person.getDocument())
					.setIsFather(gender!=null && gender.ordinal()==0 ? true : false);
				});
			});
		}
	}
}
