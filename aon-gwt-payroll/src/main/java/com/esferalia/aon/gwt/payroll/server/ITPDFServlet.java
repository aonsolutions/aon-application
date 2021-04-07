package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Certificate;

import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaREDITParts.PartType;
import solutions.aon.seg.social.exceptions.SegSocialException; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "IT-EXPORT", urlPatterns = { "/aon_gwt_payroll/it_export/*" })
public class ITPDFServlet extends HttpServlet {
	
	private SimpleDateFormat formatFullDate = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String domainName = req.getParameter("domainName");
		String userLogin = req.getParameter("userLogin");
		
		String affiliationNumber = req.getParameter("affiliationNumber");
		String regime = req.getParameter("regime");
		String contributionAccount = req.getParameter("contributionAccount");
		
		String dateFromStr = req.getParameter("dateFromStr");
		String dateToStr = req.getParameter("dateToStr");
		String startDateStr = req.getParameter("startDateStr");
		
		Byte itType = Byte.parseByte(req.getParameter("itType"));
		
		Date dateFrom = null;
		Date dateTo = null;
		Date startDate = null;
		Optional<Date> optionalStartDate = null;
		
		try {
			dateFrom = formatFullDate.parse(dateFromStr);
			dateTo = formatFullDate.parse(dateToStr);
			startDate = formatFullDate.parse(startDateStr);
			optionalStartDate = Optional.of(startDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (Connection connection = AonServletUtils.getConnection(domainName)){
			// Make sure to show the download dialog
	        res.setHeader("Content-Disposition", "attachment; filename=\"ConfirmationIT.pdf\"");
	        
	        String fileType = "application/pdf";
	        res.setContentType(fileType);
	        
	        Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
	        
	        ServletOutputStream output = res.getOutputStream();
	        
	        byte[] certificatePDF = null;
	        
	        if(isPartenityPart(itType))
	        	certificatePDF = SistemaRED.getCertificatePdf(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), affiliationNumber, regime, contributionAccount, dateFrom, dateTo, optionalStartDate);
	        else
	        	certificatePDF = SistemaRED.pdfIT(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, contributionAccount, affiliationNumber, PartType.BAJA, dateFrom, dateFrom);
	        
	        output.write(certificatePDF);
			res.flushBuffer();
		} catch (SQLException | SegSocialException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isPartenityPart(Byte itType) {
		return itType == (byte)2 || itType == (byte)3;
	}

}
