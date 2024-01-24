package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadPackagingSalesPdf", urlPatterns = {"/ms/api/download_packaging_sales_pdf/*"})
public class PackagingSalesPdfServlet extends AonApiHttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD PACKAGING PDF");
		try {
			String param = req.getParameter("json");
			param = new String(Base64.getDecoder().decode(param));
			JSONObject json = new JSONObject(param); 
			
			String domainName = json.optString("domain_name");
			Integer domainId = json.optInt("domain_id");
			String login = json.optString("login");
			Integer salesId = JsonUtils.getInteger(json, IJsonNames.SALES);
			
			Domain domain = new Domain().setName(domainName).setId(domainId);
		
			
			Sales sales = AON.getSales(domain, login, f -> f.getIdProperty().eq(salesId), new Options().setFull(true));
			Integer deliveryId = sales.getDetails().get(0).getDelivery();
			Delivery delivery = AON.getDelivery(domain, login, f -> f.getIdProperty().eq(deliveryId));
			PdfMaker.printSalesPackaging(resp.getOutputStream(), sales, delivery);

			responseFile(resp, "almacen", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private static final Logger LOGGER  = Logger.getLogger(PackagingSalesPdfServlet.class.getName());
}
