package com.esferalia.aon.gwt.fiscal.server.fiscal.mod193;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.fiscal.MODEL193;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.RetentionCertificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.DocumentException;

@WebServlet(name = "Mod193 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model193CertificatePrint" })
public class Mod193CertificatePrint extends HttpServlet {
	
	private static final long serialVersionUID = 8783020051282266915L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("mod193"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod193 mod193 = MODEL193.get(occam, id);
			
			Map<String, RetentionCertificate> certificates = new TreeMap<>();			
			mod193.getDetails().forEach(detail -> {createCertificate(mod193, detail, certificates);});
						
			String s = sanitize(mod193.getName());
		    String fileName = "CertificadoRetenciones_" + "_" + mod193.getYear() + "_" + s;
		    
		    resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			Mod193CertificatePDF print = new Mod193CertificatePDF();
			print.printMod193Certificate(resp.getOutputStream(), occam, certificates);
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

	private void createCertificate(Mod193 mod193, Mod193Detail detail, Map<String, RetentionCertificate> map){
		
		if (detail.getKey().equals("A") || detail.getKey().equals("B") || detail.getKey().equals("C") || detail.getKey().equals("D")) {
			RetentionCertificate cert = null;
			String mapKey = AonStringUtils.trimToEmpty(detail.getDocument()) + '_' + AonNumberUtils.emptyIfNull(detail.getAccrualYear());
			if (map.containsKey(mapKey)){
				cert = map.get(mapKey);
			} else {
				cert = new RetentionCertificate();
				cert.setProf1(new RetentionCertificate());  // Clave A
				cert.setProf2(new RetentionCertificate());  // Clave B
				cert.setProf3(new RetentionCertificate());  // Clave C
				cert.setProf4(new RetentionCertificate());  // Clave D
			}
			
			cert = completeCertificate(mod193, detail, cert);  // Totales
			
			if (detail.getKey().equals("A")) {
				cert.setProf1(completeCertificate(mod193, detail, cert.getProf1()));			 
			} else if(detail.getKey().equals("B")) {
				cert.setProf2(completeCertificate(mod193, detail, cert.getProf2()));
			} else if(detail.getKey().equals("C")) {
				cert.setProf3(completeCertificate(mod193, detail, cert.getProf3()));
			} else if(detail.getKey().equals("D")) {				
				cert.setProf4(completeCertificate(mod193, detail, cert.getProf4()));
			}
			
			map.put(mapKey, cert);
		}
	}
		
	private RetentionCertificate completeCertificate(Mod193 mod193, Mod193Detail detail, RetentionCertificate cert){
		cert.setId(null);
		cert.setDomain(mod193.getDomain());
		cert.setName(mod193.getName());
		cert.setYear(detail.getAccrualYear()>0?detail.getAccrualYear():mod193.getYear());
		cert.setEnterpriseName(mod193.getName());
		cert.setEnterpriseDocument(mod193.getDocument());
		cert.setEmployeeName(detail.getName());
		cert.setEmployeeDocument(detail.getDocument());
		
		if (detail.isInKind()) {
			cert.setInKindPerception(cert.getInKindPerception() + detail.getRetentionBase());
			cert.setInKindDeposit(cert.getInKindDeposit() + detail.getRetention());
		} else {
			cert.setPerception(cert.getPerception() + detail.getRetentionBase() );
			cert.setRetention(cert.getRetention() + detail.getRetention());
		}
		
		cert.setRefundAmount(cert.getRefundAmount() + detail.getLenderAmount());
		cert.setRefundReduction(cert.getRefundReduction() + detail.getReduction());
			
		return cert;
	}

}
