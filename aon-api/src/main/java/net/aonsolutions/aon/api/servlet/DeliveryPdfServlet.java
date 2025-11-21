package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadDeliveryPdf", urlPatterns = {"/ms/api/download_delivery_pdf/*",
														"/aon_gwt_aio/download_delivery_pdf/*"
														})
public class DeliveryPdfServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(DeliveryPdfServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD DELIVERY PDF");
		try {
			addCorsHeader(resp);
			JSONObject json = new JSONObject();
			String idStr = req.getParameter("id");
			if(idStr != null) {
				json.put(IJsonNames.ID, Integer.parseInt(idStr));
				String dn = req.getServerName();
				Domain domain = AON.getDomain(dn, 0, "", f-> f.getNameProperty().eq(dn));
				json.put("domain_name", domain.getName());
				json.put("domain_id", domain.getId());
				json.put("login", "");
			} else {
				String param = req.getParameter("json");
				param = new String(Base64.getDecoder().decode(param));
				json = new JSONObject(param); 
			}

			String domainName = json.optString("domain_name");
			Integer domainId = json.optInt("domain_id");
			Domain domain = new Domain().setName(domainName).setId(domainId);
			String login = json.optString("login");
			Integer id = json.optInt(IJsonNames.ID);
			PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domainName, domainId, login, true);

			Delivery delivery = AON.getDelivery(domain, login, f -> f.getIdProperty().eq(id), new Options().setFull(true));
			
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Attach logo = new Attach();
			
			if(config.isLogo()) {
				Integer logoId = company.getRegistry().getId();
				logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			}
			
//			CustomerFull customer = AON.getCustomerFull(domainName, domainId, login, delivery.getCustomer().getId());
			
			Warehouse warehouse;
			if(!delivery.getDetails().isEmpty() && delivery.getDetails().get(0).getWarehouse() != null)
				warehouse = AON.getWarehouse(domainName, domainId, login, f -> 
					f.getIdProperty().eq(delivery.getDetails().get(0).getWarehouse()));
			else warehouse = AON.getWarehouse(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId));

			PdfMaker.printDelivery(resp.getOutputStream(), company, delivery, warehouse, logo.getData());
			responseFile(resp, "Albarán " + delivery.getReferenceCode(), MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}	
}
