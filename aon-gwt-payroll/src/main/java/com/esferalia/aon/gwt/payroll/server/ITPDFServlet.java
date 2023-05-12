package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.SistemaRED.PartType;
import solutions.aon.seg.social.exception.SegSocialException; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "IT-EXPORT", urlPatterns = { "/aon_gwt_payroll/it_export/*" })
public class ITPDFServlet extends HttpServlet {
	
	private SimpleDateFormat formatFullDate = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		String domainName = req.getParameter("domainName");
		String userLogin = req.getParameter("userLogin");
		
		String affiliationNumber = req.getParameter("affiliationNumber");
		String regime = req.getParameter("regime");
		String contributionAccount = req.getParameter("contributionAccount");
		
		String dateFromStr = req.getParameter("dateFromStr");
		String dateToStr = req.getParameter("dateToStr");
		String startDateStr = req.getParameter("startDateStr");
		
		Byte itType = Byte.parseByte(req.getParameter("itType"));
		
		PartType partType = getPartType(Byte.parseByte(req.getParameter("itPartType")));
		
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
	        Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);	

			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");

	        byte[] certificatePDF = null;
	        
	        if(isPartenityPart(itType)) {
	            certificatePDF = SistemaRED.getCertificatePdf(certificate.getData(), certificate.getPassword(), certificate.getType(), affiliationNumber, regime, contributionAccount, dateFrom, dateTo, optionalStartDate);
	        } else {
	            certificatePDF = SistemaRED.getITReport(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, contributionAccount, affiliationNumber, partType, dateFrom, dateTo);
	        }

	        new AonApiHttpServlet().responseFile(res, "PART.pdf", certificatePDF, MimeType.PDF);
		} catch (SQLException | SegSocialException | IOException e) {
		    new AonApiHttpServlet().error(req, res, e);
		}
	}
	
	private boolean isPartenityPart(Byte itType) {
		return itType == (byte)2 || itType == (byte)3;
	}

	private PartType getPartType(Byte partType) {
	    return partType!=null ? SistemaRED.PartType.safeValueOf(partType) : SistemaRED.PartType.BAJA;
    }
}
