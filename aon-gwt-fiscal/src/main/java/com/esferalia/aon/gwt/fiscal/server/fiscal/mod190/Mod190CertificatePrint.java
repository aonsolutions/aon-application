package com.esferalia.aon.gwt.fiscal.server.fiscal.mod190;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod190 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model190CertificatePrint" })
public class Mod190CertificatePrint extends HttpServlet {
	
	private static final long serialVersionUID = -534949591948520519L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("mod190"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod190 mod190 = MODEL190.get(occam, id);
			
			// Trabajadores
			Map<String, RetentionCertificate> employeeCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createEmployeeCertificate(mod190, detail, employeeCertificates);});
						
			// Profesionales
			Map<String, RetentionCertificate> professionalCertificates = new HashMap<>();
			mod190.getDetails().forEach(detail -> {createProfessionalCertificate(mod190, detail, professionalCertificates);});
						
			String s = sanitize(mod190.getName());
		    String fileName = "CertificadoRetenciones_" + "_" + mod190.getYear() + "_" + s;
		    
		    resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			Mod190CertificatePDF print = new Mod190CertificatePDF();
			print.printMod190Certificate(resp.getOutputStream(), occam, employeeCertificates, professionalCertificates);
			resp.flushBuffer();
		} catch (IOException | DocumentException e) {
			throw new ServletException(e);
		}
	}
		
	private String sanitize(String seq) {
		if (seq == null) return null;
		StringBuilder sb = new StringBuilder();
	    if(!Character.isJavaIdentifierStart(seq.charAt(0))) {
	        sb.append("_");
	    }
	    for (char c : seq.toCharArray()) {
	        if(Character.isJavaIdentifierPart(c)) {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
	
	public void createEmployeeCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		if (detail.getKey().equals("A") || detail.getKey().equals("L") || detail.getKey().equals("E") || detail.getKey().equals("F")) {
			RetentionCertificate cert = null;
			if(map.containsKey(detail.getDocument())){
				cert = map.get(detail.getDocument());
			} else {
				cert = new RetentionCertificate();
				cert.setDelay1(new RetentionCertificate());
				cert.setDelay2(new RetentionCertificate());
				cert.setDelay3(new RetentionCertificate());
				cert.setDelay4(new RetentionCertificate());
			}

			// Datos correspondientes al ejercicio de devengo del modelo
			if (detail.getAccrualYear() == 0 || detail.getAccrualYear() == mod190.getYear()) { 
	     		
				if (detail.getKey().equals("A") || detail.getKey().equals("E") || detail.getKey().equals("F")){
	     			cert = completeCertificate(mod190, detail, cert, detail.getKey());
	     			// Reducciones y Gastos
					cert.setApplicableReduction(cert.getApplicableReduction() + detail.getApplicableReduction());
					cert.setDeducibleExpense(cert.getDeducibleExpense() + detail.getDeducibleExpense());
				}
				
				if (detail.getKey().equals("L")){
					if(detail.getSubKey().equals("01")){
						cert.setJourneyDiet(cert.getJourneyDiet() + detail.getPerception());
					} else if(detail.getSubKey().equals("05") || detail.getSubKey().equals("20")){
						cert.setIncomeExemption(cert.getIncomeExemption() + detail.getPerception());
					}
				}
				
				cert.setForecastPlanContributions(cert.getForecastPlanContributions() + detail.getForecastPlanContributions()); // SOLO PARA EL CERTIFICADO DE RETENCIONES
				cert.setDependencyContributions(0);   // ESTE DATO NO ESTA EN EL MODELO 190
				
			} else {
				// Datos correspondientes a atrasos (devengo ejercicios anteriores)
				if (cert.getYear() == 0)
					cert = completeCertificate(mod190, detail, cert, null);				
				if (cert.getDelay1().getYear() == 0 || detail.getAccrualYear() == cert.getDelay1().getYear())
					cert.setDelay1(completeDelay(detail, cert.getDelay1()));				
				else if (cert.getDelay2().getYear() == 0 || detail.getAccrualYear() == cert.getDelay2().getYear())
					cert.setDelay2(completeDelay(detail, cert.getDelay2()));				
				else if (cert.getDelay3().getYear() == 0 || detail.getAccrualYear() == cert.getDelay3().getYear())
					cert.setDelay3(completeDelay(detail, cert.getDelay3()));				
				else if (cert.getDelay4().getYear() == 0 || detail.getAccrualYear() == cert.getDelay4().getYear())
					cert.setDelay4(completeDelay(detail, cert.getDelay4()));
			}
			
			// ESTOS DATOS NO ESTAN EN EL MODELO 190
//			cert.setRefund1(obtainRefunds(mod190.getYear()-1));
//			cert.setRefund2(obtainRefunds(mod190.getYear()-2));
//			cert.setRefund3(obtainRefunds(mod190.getYear()-3));
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
	

	private void createProfessionalCertificate(Mod190 mod190, Mod190Detail detail, Map<String, RetentionCertificate> map){
		if (detail.getKey().equals("G") || detail.getKey().equals("H") || detail.getKey().equals("I")) {
			RetentionCertificate cert = null;
			if (map.containsKey(detail.getDocument())){
				cert = map.get(detail.getDocument());
			} else {
				cert = new RetentionCertificate();
				cert.setProf1(new RetentionCertificate());
				cert.setProf2(new RetentionCertificate());
				cert.setProf3(new RetentionCertificate());
				cert.setProf4(new RetentionCertificate());
			}
			
			if (detail.getKey().equals("G")) {
				cert = completeCertificate(mod190, detail, cert, "G");			 
			} else if(detail.getKey().equals("H")) {
				cert = completeCertificate(mod190, detail, cert, null );
				if(detail.getSubKey().equals("01") || detail.getSubKey().equals("02")){
					cert.setProf1(completeCertificate(mod190, detail, cert.getProf1(), "H"));
				} else if(detail.getSubKey().equals("03")){
					cert.setProf2(completeCertificate(mod190, detail, cert.getProf2(), "H"));
				} else if(detail.getSubKey().equals("04")){
					cert.setProf3(completeCertificate(mod190, detail, cert.getProf3(), "H"));
				}				
			} else if(detail.getKey().equals("I")) {
				cert = completeCertificate(mod190, detail, cert, null );
				cert.setProf4(completeCertificate(mod190, detail, cert.getProf4(), "I"));
			}
			
			map.put(cert.getEmployeeDocument(), cert);
		}
	}
		
	private RetentionCertificate completeCertificate(Mod190 mod190, Mod190Detail detail, RetentionCertificate cert, String key){
		cert.setId(null);
		cert.setDomain(mod190.getDomain());
		cert.setName(mod190.getName());
		cert.setYear(mod190.getYear());
		cert.setEnterpriseName(mod190.getName());
		cert.setEnterpriseDocument(mod190.getDocument());
		cert.setEmployeeName(detail.getName());
		cert.setEmployeeDocument(detail.getDocument());
		
		if(AonStringUtils.isNotBlank(key) && detail.getKey().equals(key)){
			cert.setPerception(cert.getPerception() + detail.getPerception() + detail.getPerceptionIL());
			cert.setRetention(cert.getRetention() + detail.getRetention()  + detail.getRetentionIL());
			cert.setInKindPerception(cert.getInKindPerception() + detail.getInKindPerception() + detail.getInKindPerceptionIL());
			cert.setInKindDeposit(cert.getInKindDeposit() + detail.getInKindDeposit() + detail.getInKindDepositIL());
			cert.setInKindOutputDeposit(cert.getInKindOutputDeposit() + detail.getInKindOutputDeposit() + detail.getInKindOutputDepositIL());
		}
		
		return cert;
	}
	
	private RetentionCertificate completeDelay(Mod190Detail detail, RetentionCertificate cert) {
		cert.setYear(detail.getAccrualYear()); // Ejercicio de devengo
		cert.setPerception(cert.getPerception() + detail.getPerception() + detail.getPerceptionIL() + detail.getInKindPerception() + detail.getInKindPerceptionIL() ); // Importe integro satisfecho
		cert.setRetention(cert.getRetention() + detail.getRetention() + detail.getRetentionIL() + detail.getInKindOutputDeposit() + detail.getInKindOutputDepositIL()); // Retenciones practicadas
		cert.setApplicableReduction(cert.getApplicableReduction() + detail.getApplicableReduction()); // Reducciones 
		cert.setDeducibleExpense(cert.getDeducibleExpense() + detail.getDeducibleExpense());  // Gastos
		return cert;
	}

//	// Cantidades reintegradas por el perceptor en el ejercicio por haber sido
//	// indebida o excesivamente percibidas en ejercicios anteriores (reintegros)
//	private RetentionCertificate obtainRefunds(int year) {
//		return null;
//	}

}
