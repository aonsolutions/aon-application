package com.code.aon.webservice.warehouse;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.itextpdf.text.pdf.BarcodeQRCode;

@WebServlet(name = "PackingListNotification", urlPatterns = { "/packing_list_notification/*",
															  "/aon_gwt_aio/packing_list_notification/*"})
public class PackingListMailServlet extends HttpServlet{
	private static final long serialVersionUID = 7426471939221433842L;

	private static final Logger LOGGER  = Logger.getLogger(PackingListMailServlet.class.getName());
	private String msg; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("Notification Servlet - GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Notification Servlet - POST METHOD");
		
		String[] pathInfo = req.getPathInfo().split("/");
		String login = pathInfo[2];
		String domainName = pathInfo[1]; 
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
		JSONObject json = Utils.getRequestJSON(req);

		Integer carrier_packing = json.getInt(MSG.CARRIER_PACKING);
		Integer mail_account = json.getInt(MSG.MAIL_ACCOUNT);
		Integer signature_id = json.getInt(MSG.SIGNATURE);
		String type = json.getString(MSG.TYPE);
		String to = json.getString("to");
		Integer order = json.getInt("order");
		CarrierPacking carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, carrier_packing);
		Signature signature = null;
		if(!signature_id.equals(-1)){
			signature = AON.getSignature(domain.getName(), domain.getId(), login, signature_id);
		}
		String scheme = req.getParameter("scheme");
		MailAccount ma = AON.getMailAccount(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(mail_account));
		sendNotification(domain, login, carrierPacking, signature, ma, type, to, order, scheme);		
	}
		
	public void sendNotification(Domain domain, String login, CarrierPacking carrierPacking, Signature signature, MailAccount mailAccount, String type, String to, Integer order, String scheme){
		msg = "<div> Estimado Colaborador, </div><div><p></p></div>";
		Boolean isSC = CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType());
		String str = "domain="+ domain.getName() + "&login="+ login + "&id="+carrierPacking.getId();
		String base = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		String url = scheme + "://" + domain.getName() + "/aon_gwt_aio/download_packing_list/"+base;
		String pdf = "Le adjuntamos copia de la" + (isSC ? " solicitud de carga ": " hoja de ruta ")
				+ carrierPacking.getSeries() +"/" + carrierPacking.getNumber()
				+ " en formato pdf, pulse " + "<a href=\""+ url+"\"> AQUI </a>" + " para descargar.";

		msg = msg + "<div>"+ pdf +"</div>";
		if(MSG.CARRIER.equalsIgnoreCase(type)){
			printCarrierPacking(carrierPacking);
			if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())){
				AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
					f -> f.getCarrierPackingProperty().eq(carrierPacking.getId())
					.and(f.getDomainProperty().eq(domain.getId())))
				.forEach(purchase ->{
					RAddress address = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()));
					printAddress(address);

					AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getPurchaseProperty().eq(purchase.getId())
						.and(f.getCarrierPackingProperty().eq(carrierPacking.getId())))
					.forEach(detail -> 			
						printDetail(detail.getProductCode() + "-" + detail.getProductName(),Double.toString(detail.getQuantity()))
					);
					msg = msg + "</tbody></table>";
				});
			} else if(CarrierPackingType.WAYBILL.equals(carrierPacking.getType())){
				AON.getDeliveryStream(domain.getName(), domain.getId(), login, 
					f-> f.getCarrierPackingProperty().eq(carrierPacking.getId()))
				.forEach(delivery -> {
					RAddress address = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()));
					printAddress(address);

					AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getDelivery().eq(delivery.getId()))
					.forEach(detail -> 
						printDetail(detail.getProductCode() + "-" + detail.getProductName(),Double.toString(detail.getQuantity()))
					);
					msg = msg + "</tbody></table>";
				});
			}
			printSignature(domain, login, signature);
			msg = msg + "</div></div>";
			RegistryMedia rmedia = new RegistryMedia();
			if(to == null){
				rmedia = AON.getRMedia(domain.getName(), domain.getId(), login, 
					f -> f.getRegistryProperty().eq(carrierPacking.getCarrier())
					.and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
			}
			sendEmail(domain, login, mailAccount.getId(),(to != null) ? to : rmedia.getValue(), mailAccount.getEmail(), "Notificación Packing List", msg, scheme, carrierPacking, "Empresa de Transporte");
		} else if(MSG.REGISTRY.equalsIgnoreCase(type)){
			if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())){
				Stream<Purchase> stream = null;
				if(!order.equals(-1)){
					stream = AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
							f -> f.getCarrierPackingProperty().eq(carrierPacking.getId())
							.and(f.getDomainProperty().eq(domain.getId()))
							.and(f.getIdProperty().eq(order)));
				} else {
					stream = AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
							f -> f.getCarrierPackingProperty().eq(carrierPacking.getId())
							.and(f.getDomainProperty().eq(domain.getId())));
				}
				stream.forEach(purchase ->{
					printCarrierPacking(carrierPacking);
					RAddress address = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()));
					printAddress(address);

					AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getPurchaseProperty().eq(purchase.getId())
						.and(f.getCarrierPackingProperty().eq(carrierPacking.getId())))
					.forEach(detail -> 			
						printDetail(detail.getProductCode() + "-" + detail.getProductName(),Double.toString(detail.getQuantity()))
					);
					msg = msg + "</tbody></table>";
					printSignature(domain, login, signature);
					msg = msg + "</div></div>";
					RegistryMedia rmedia = new RegistryMedia();
					if(to == null){
						rmedia = AON.getRMedia(domain.getName(), domain.getId(), login, 
							f -> f.getRegistryProperty().eq(purchase.getSupplier())
							.and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
					}
					sendEmail(domain, login, mailAccount.getId(), (to != null) ? to : rmedia.getValue(), mailAccount.getEmail(), "Notificación Packing List", msg, scheme, carrierPacking, "Proveedor");
				});
			} else if(CarrierPackingType.WAYBILL.equals(carrierPacking.getType())){
				Stream<Delivery> stream = null;
				if(!order.equals(-1)){
					stream = AON.getDeliveryStream(domain.getName(), domain.getId(), login, 
							f-> f.getCarrierPackingProperty().eq(carrierPacking.getId())
							.and(f.getIdProperty().eq(order)));
				} else {
					stream = AON.getDeliveryStream(domain.getName(), domain.getId(), login, 
							f-> f.getCarrierPackingProperty().eq(carrierPacking.getId()));
				}
				stream.forEach(delivery -> {
					printCarrierPacking(carrierPacking);
					RAddress address = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()));
					printAddress(address);

					AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getDelivery().eq(delivery.getId()))
					.forEach(detail -> 
						printDetail(detail.getProductCode() + "-" + detail.getProductName(),Double.toString(detail.getQuantity()))
					);
					msg = msg + "</tbody></table>";
					msg = msg + "</div></div>";
					RegistryMedia rmedia = new RegistryMedia();
					if(to == null){
						rmedia = AON.getRMedia(domain.getName(), domain.getId(), login, 
							f -> f.getRegistryProperty().eq(delivery.getCustomer())
							.and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
					}
					sendEmail(domain, login, mailAccount.getId(), (to != null) ? to : rmedia.getValue(), mailAccount.getEmail(), "Notificación Packing List", msg, scheme, carrierPacking, "Cliente");
				});
			}
		}
	}
	
	private void printCarrierPacking(CarrierPacking carrierPacking){
		SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
		msg = msg + "<div style='margin-left: -30px;'>"
				+"<div style='margin: 7px 15px 14px 30px;line-height: 18px;font-size: 13px;box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.075);'>"
				+"<p></p><table style='border: 1px solid #CCC;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'><tbody>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Tipo</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + carrierPacking.getType().getName() + "</span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Fecha de Emisión</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" +(carrierPacking.getIssueDate() != null ? format.format(carrierPacking.getIssueDate()):"") + "</span>"
				+  	"</td>"
				+"</tr>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Serie/Número</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + carrierPacking.getSeries() + "/" + carrierPacking.getNumber() + "</span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Referencia</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + (carrierPacking.getCarrierReference() != null ? carrierPacking.getCarrierReference() : "") + "</span>"
				+  	"</td>"
				+"</tr>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Empresa de Transporte</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + (carrierPacking.getCarrierName() != null ? carrierPacking.getCarrierName() : "") + "</span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Fecha de Entrega</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + ( carrierPacking.getDeliveryDate() != null ? format.format(carrierPacking.getDeliveryDate()): "") + "</span>"
				+  	"</td>"
				+"</tr>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Matricula</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + (carrierPacking.getNumberPlate() != null ? carrierPacking.getNumberPlate() : "") + "</span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Conductor</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>" + (carrierPacking.getDriverDocument()!= null ? carrierPacking.getDriverDocument() : "")
								+ " - " + (carrierPacking.getDriverName()!= null ? carrierPacking.getDriverName() : "") + "</span>"
				+  	"</td>"
				+"</tr>"
				+ "</tbody></table>";
	}
	private void printAddress(RAddress address){
		msg = msg +"<p></p><table style='border: 1px solid #CCC;table-layout: fixed;width: 100%;min-width: 625px;border-collapse: collapse;' cellpadding='0'><tbody>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Destinatario</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<div>" + address.getRegistryName()  + "</div>"
				+		"<div>" + address.getStreet_type() + " " + address.getAddress() + " " 
								+ address.getNumber() +" " + address.getAddress2() +  " " + address.getAddress3() + "</div>"
				+		"<div>" + address.getZip() + " " + address.getCity() + " " + address.getGeozoneName() + "</div>"
				+  	"</td>"
				+"</tr>"
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Articulo</b></span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span><b>Cantidad</b></span>"
				+  	"</td>"
				+"</tr>";
	}
	
	private byte[] createQRImage(Domain domain, Integer cpId) {
		BarcodeQRCode qrcode2 = new BarcodeQRCode("https://" + domain.getName() +"/qr?cp=" + cpId, 100, 100, null);
		java.awt.Image im = qrcode2.createAwtImage(Color.BLACK, Color.WHITE);

		BufferedImage buffImg = new BufferedImage(im.getWidth(null), im.getWidth(null), BufferedImage.TYPE_4BYTE_ABGR);
		buffImg.getGraphics().drawImage(im, 0, 0, null);
		buffImg.getGraphics().dispose();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(buffImg, "png", outputStream);
		} catch (Exception e) { e.printStackTrace();}
		return outputStream.toByteArray();	
	}
	
	private void printDetail(String product, String quantity){
		msg = msg  
				+"<tr>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>"+ product + "</span>"
				+  	"</td>"
				+	"<td style=\"color: #222;border: 0px;font-family: Arial,sans-serif; padding: 5px 21px 5px 21px;vertical-align: top;\">"
				+		"<span>"+ quantity +"</span>"
				+  	"</td>"
				+"</tr>";
	}
	
	private void printSignature(Domain domain, String login, Signature signature){
		if(signature != null && signature.getSignature() != null
			&& signature.getSignature() != "")
			msg = msg+ "<p></p>"+ signature.getSignature();
		else msg = msg +"<p></p><table><tbody><tr><td>"	
				+"<p style=\"color: #222;\">Para cualquier aclaraciÃ³n o informaciÃ³n adicional, no dude en contactar con nosotros.</p>"
				+"<p style=\"color: #222;\">"
				+ "<div>"+getCompanyName(domain, login)+"</div>"
				+"<div><b>Gracias por confiar en nosotros</b></div>"
				+ "</p>"
				+ "</td></tr></tbody></table>"			
				+ "</div></div>";
	}
	
	public void sendEmail(Domain domain, String login, Integer mailAccountId, String to, String bcc, String issue, String message, String scheme, CarrierPacking carrierPacking, String type){
		try {
			JSONObject json = new JSONObject();
			json.put("mailAccountId", mailAccountId)
				.put("recipientsTo", to)
				.put("content", message)
				.put("subject", issue)
				.put("login", login)
				.put("domainName", domain.getName())
				.put("domainId", domain.getId())
				.put("md5", Base64.getEncoder().encodeToString(createQRImage(domain, carrierPacking.getId())))
				.put("attachName", "QR.png")
				.put("mimetype", MimeType.PNG.ordinal())
				.put("bcc", bcc);
			
			sendPostHttpClient(domain, login, json, scheme, carrierPacking, type);			
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	protected void sendPostHttpClient(Domain domain, String login, JSONObject json, String scheme, CarrierPacking carrierPacking, String type) {
		try{
			String url = scheme + "://"+domain.getName()+ "/send_email/";
			System.out.println(url);
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse resp = client.execute(post);
			
			if(resp.getStatusLine().getStatusCode() == 200) {
				DataResponse dr = new DataResponse()
						.setDomain(json.getInt("domainId"))
						.setCode("")
						.setResponseDate(new Date())
						.setSource(DataResponseSource.PACKING_LIST_NOTIFICATION)
						.setSourceId(carrierPacking.getId());
				dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, dr);
				
				AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, new DataResponseDetail()
						.setDomain(domain.getId())
						.setDataResponse(dr.getId())
						.setDataVariable("type")
						.setDataValue(type));
				
			}
		} catch (IOException e){
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
	private String getCompanyName(Domain domain, String login) {
		return AON.getCompanyForDomain(domain.getName(), domain.getId(), login).getName();
	}
}
