package com.esferalia.aon.gwt.fiscal.server.fiscal.mod184;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.fiscal.MODEL184;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Certificate;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod184 Certificate Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model184CertificatePrint" })
public class Mod184CertificatePrint extends HttpServlet {
	
	private static final long serialVersionUID = -8907364032197795939L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("mod184"));
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod184 mod184 = MODEL184.get(occam, id);
			
			Map<String, Mod184Certificate> certificates = new HashMap<>();
			mod184.getPartners().forEach(partner -> createCertificate(mod184, partner, certificates));
						
			String s = sanitize(mod184.getName());
		    String fileName = "CertificadoAtribucionRentas_" + "_" + mod184.getYear() + "_" + s;
		    
		    resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".pdf\";");
			Mod184CertificatePDF print = new Mod184CertificatePDF();
			print.printMod184Certificate(resp.getOutputStream(), occam, certificates);
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
	
	private void createCertificate(Mod184 mod184, Mod184Partner partner, Map<String, Mod184Certificate> map){
		
		// Comprobar si ya lo tenemos creado		
		Mod184Certificate cert = null;
		if(map.containsKey(partner.getDocument())){
			cert = map.get(partner.getDocument());
		} else {
			cert = new Mod184Certificate();
			cert.setYear(mod184.getYear()); 
			cert.setEntityDocument(mod184.getDocument());  
			cert.setEntityName(mod184.getFullName());
			cert.setMemberDocument(partner.getDocument());
			cert.setMemberName(partner.getName());
			cert.setAddress(partner.getAddress()); 
			cert.setProvince(partner.getProvince());
			cert.setCountry(partner.getCountry());
			cert.setPartType(partner.getPartType());
			cert.setMemberEndOfYear(partner.isMemberEndOfYear());
			cert.setMemberDays(partner.getMemberDays());
			cert.setPartPercent(partner.getPartPercent());
			map.put(partner.getDocument(), cert);
		}
		
		// Añadir o acumular datos segun clave y subclave
		String key = AonStringUtils.trimToEmpty(partner.getKey()) + AonStringUtils.trimToEmpty(partner.getSubKey());
		if (AonStringUtils.isNotEmpty(key)) {
			if (cert.getDetail().containsKey(key)) {
				cert.getDetail().get(key)[0] = cert.getDetail().get(key)[0] + partner.getAmount();
				cert.getDetail().get(key)[1] = cert.getDetail().get(key)[1] + partner.getReduction();				
			} else {
				cert.getDetail().put(key, new double[]{partner.getAmount(),partner.getReduction()});				
			}			
		}
		
	}

}
