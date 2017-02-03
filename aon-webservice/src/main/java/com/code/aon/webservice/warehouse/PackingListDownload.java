package com.code.aon.webservice.warehouse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;

@WebServlet(name = "packinglistProjection", urlPatterns = {"/aon_gwt_aio/download_packing_list/*"})
public class PackingListDownload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String carrierPackingIdStr = parameters.get("id");
		Integer carrierPackingId = Integer.parseInt(carrierPackingIdStr);
		
		// GENERATE JSON //
		JSONObject json = new JSONObject();
		json.put("carrier_packing", ToJSON.carrierPackingToJSON(
				AON.getCarrierPacking(domain.getName(), domain.getId(), login, carrierPackingId)
		));
		
		JSONArray array = new JSONArray();
		AON.getDeliveryStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(carrierPackingId))
		.forEach(delivery -> {
			JSONObject deliveryJSON = ToJSON.deliveryToJSON(delivery);
			deliveryJSON.put("address", ToJSON.raddressToJSON(
					AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()))
			));
			
			JSONArray details = new JSONArray();
			AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDelivery().eq(delivery.getId()))
			.forEach(detail -> details.put(ToJSON.deliveryDetailToJSON(detail)));

			deliveryJSON.put("details", details);
			array.put(deliveryJSON);
		});
		json.put("orders", array);
		// --------------- //
		
		File file = PackingList.createPdf(json);
		
        long length = file.length();
        FileInputStream fis = new FileInputStream(file);
        
        resp.addHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
    	resp.setContentType("application/msexcel");

        if (length > 0 && length <= Integer.MAX_VALUE);
            resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(fis,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
            out.write(buffer, 0, bytes);
        
        bis.close();
        fis.close();
        out.flush();
        out.close();
	}
	

}
