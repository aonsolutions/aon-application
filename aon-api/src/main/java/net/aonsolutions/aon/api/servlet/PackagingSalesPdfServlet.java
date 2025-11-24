package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;


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
			
			Domain domain = new Domain().setName(domainName).setId(domainId);
			
			if(JsonUtils.has(json, IJsonNames.SALES)) {
				Integer salesId = JsonUtils.getInteger(json, IJsonNames.SALES);
				Sales sales = AON.getSales(domain, login, f -> f.getIdProperty().eq(salesId), new Options().setFull(true));				
				sales.setShippingAddress(AON.getRegistryAddress(domain, new User().setLogin(login), f -> f.getIdProperty().eq(sales.getShippingAddress().getId())));		
				Integer deliveryId = JsonUtils.has(json, IJsonNames.DELIVERY)
						? JsonUtils.getInteger(json, IJsonNames.DELIVERY) 
						: sales.getDetails().get(0).getDelivery();
				Delivery delivery = AON.getDelivery(domain, login, f -> f.getIdProperty().eq(deliveryId));
				PdfMaker.printSalesPackaging(resp.getOutputStream(), sales, delivery);
			} else if(JsonUtils.has(json, IJsonNames.DELIVERY)) {
				Integer deliveryId = JsonUtils.getInteger(json, IJsonNames.DELIVERY); 
				Delivery delivery = AON.getDelivery(domain, login, f -> f.getIdProperty().eq(deliveryId), new Options().setFull(true));
				DeliveryDetail detail = delivery.getDetails().stream().filter(f -> f.getSalesDetail() != null).findFirst().orElse(null);
				if(detail != null && detail.getSalesDetail() != null) { 
					SalesDetail salesDetail = AON.getSalesDetailStream(domain.getName(), domain.getId(), login, f ->
						f.getIdProperty().eq(detail.getSalesDetail())).findFirst().orElse(null);
					Sales sales = AON.getSales(domain, login, f -> f.getIdProperty().eq(salesDetail.getSales().getId()), new Options().setFull(true));
					sales.setShippingAddress(AON.getRegistryAddress(domain, new User().setLogin(login), f -> f.getIdProperty().eq(sales.getShippingAddress().getId())));
					PdfMaker.printSalesPackaging(resp.getOutputStream(), sales, delivery);
				} else {
					SalesDetail salesDetail = AON.getSalesDetailStream(domain.getName(), domain.getId(), login, f ->
						f.getDeliveryProperty().eq(deliveryId)).findFirst().orElse(null);
					Sales sales = AON.getSales(domain, login, f -> f.getIdProperty().eq(salesDetail.getSales().getId()), new Options().setFull(true));
					sales.setShippingAddress(AON.getRegistryAddress(domain, new User().setLogin(login), f -> f.getIdProperty().eq(sales.getShippingAddress().getId())));
					PdfMaker.printSalesPackaging(resp.getOutputStream(), sales, delivery);
				}
			}	

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
