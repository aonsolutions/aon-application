package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CustomerBookingResumeExcelServlet", urlPatterns = { "/ms/api/customers-booking-resume-excel/*"})
public class CustomerBookingResumeExcelServlet extends AonApiHttpServlet {
	
	private static Logger logger = Logger.getLogger(CustomerBookingResumeExcelServlet.class.getName());
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		try {
			AonApiData api = initialize(req, false);
			
			Booking booking = AON.getBooking(api.getDomain(), api.getUser());
			 
			responseFile(resp, "Resumen Contrataci\u00f3", new FileInputStream(getBookingExcel(api, booking)), MimeType.MS_EXCEL);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private File getBookingExcel(AonApiData api, Booking booking) throws Exception {
		logger.info("[GET] ENTERPRISE CONTRACTS EXCEL");
		
		File file = File.createTempFile("Resumen Contrataci\u00f3", "");
		FileOutputStream outputStream = new FileOutputStream(file);
	
		SXSSFWorkbook wb = new SXSSFWorkbook(1);
		SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("Resumen Contrataci\u00f3");
		
		wb.write(outputStream);
		outputStream.close();
		wb.close();
		
		return file;
	}
	
}
