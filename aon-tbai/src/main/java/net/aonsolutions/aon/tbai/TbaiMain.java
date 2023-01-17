package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.responses.ResponseHandler.HandleLroeResponse;
import static net.aonsolutions.aon.tbai.responses.ResponseHandler.HandleTbaiResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import net.aonsolutions.aon.sign.TbaiSigner;
import net.aonsolutions.aon.tbai.exceptions.TbaiException;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.lroe.LROE140_1_1;
import net.aonsolutions.aon.tbai.lroe.LROE240_1_1;
import net.aonsolutions.aon.tbai.lroe.LROEInfo;
import net.aonsolutions.aon.tbai.lroe.LROEInformation;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.sign.TbaiSign;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.emision.TicketBai;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

public class TbaiMain {
	
	public void createEmisionLROE(Company company, Invoice invoice, TbaiConfiguration tbaiConfiguration) throws Exception, TbaiException, JAXBException, ParserConfigurationException, SAXException, IOException {
		LROEInformation lroe = LroeData.get(company.getDomain(), new User().setLogin(""), invoice.getId());
		if (!lroe.getChapter1().isAccepted() && tbaiConfiguration.isBizkaia() && (!tbaiConfiguration.isTest() || "A99802019".equalsIgnoreCase(company.getDocument()) || "99980200M".equalsIgnoreCase(company.getDocument()))) {
			TbaiData tbaiData = TbaiData.getInstance(tbaiConfiguration); 
			byte[] xml = tbaiData.getTbaiRequestFile(company.getDomain(), "", invoice.getId());
			
			if(xml == null || lroe.getChapter1().isTbaiError()) {
				createEmisionTBAI(company, invoice, tbaiConfiguration);
			} else {
				LROEResponse lroeResponse = null;
				LROEInfo info = null;
				if (AonDocumentUtil.isValidCIF(company.getDocument())) {
					LROE240_1_1 lroe240 = new LROE240_1_1();
					info = lroe240.buildInfo(OperacionEnum.A_00);
					lroeResponse = lroe240.alta(company, tbaiConfiguration, invoice, xml);
				} else {
					Person person = AON.getPerson(company.getDomain(), "", f -> f.getIdProperty().eq(company.getId()));
					EnterpriseActivity ea = AON.getEnterpriseActivity(company.getDomain().getName(),
						company.getDomain().getId(), "", invoice.getActivity().getId());
					if(ea == null || ea.getId() == null) {
						ea = AON.getEnterpriseActivities(company.getDomain().getName(),
								company.getDomain().getId(), "").filter(f -> f.isPrincipal()).findFirst().orElse(new EnterpriseActivity());
					}
					invoice.setEpigraph(ea.getIae().getFullEpigraph());
					LROE140_1_1 lroe140 = new LROE140_1_1();
					info = lroe140.buildInfo(OperacionEnum.A_00);
					lroeResponse = lroe140.alta(tbaiConfiguration, person, invoice, xml);
				}
				LroeData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, lroeResponse, info);
				HandleLroeResponse(lroeResponse);
			}
		}
	}

	public void zuzenduTBAI(Company company, Invoice invoice, TbaiConfiguration tbaiConfiguration) throws Exception {
		TbaiData tbaiData = TbaiData.getInstance(tbaiConfiguration); 
		TicketBai ticketBai = tbaiData.getTicketBai(company.getDomain(), new User().setLogin(""), invoice.getId(), tbaiConfiguration);
		TbaiBlockchain blockchain = tbaiData.getInvoiceBlockchain(company.getDomain(), new User().setLogin(""), invoice.getId());
		final SubsanacionModificacionTicketBAI tbai = Invoice2tbai.buildZuzendu(company, invoice, tbaiConfiguration, ticketBai, blockchain);
		
		final JAXBContext jaxbContext = JAXBContext.newInstance(SubsanacionModificacionTicketBAI.class);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(tbai, bos);
		
		byte[] xml = bos.toByteArray();
		
		String uri = TbaiUri.getUrlZuzendu(tbaiConfiguration);
		TbaiResponse response = sendXML(uri, tbaiConfiguration, xml, false);
		

	}
	
	public void createEmisionTBAI(Company company, Invoice invoice, TbaiConfiguration tbaiConfiguration)
			throws Exception {
		TbaiData tbaiData = TbaiData.getInstance(tbaiConfiguration); 
		boolean send = true;
		if(!tbaiConfiguration.isBizkaia()) {
			TBAIInformation info = tbaiData.get(company.getDomain(),  new User().setLogin(""), invoice.getId());
			send = !info.isAccepted();
		}
		if(send) {
			TbaiBlockchain blockchain = tbaiData.getBlockchain(company.getDomain(), new User().setLogin(""), invoice.getId());
			final TicketBai tbai = Invoice2tbai.build(company, invoice, tbaiConfiguration, blockchain);

			final JAXBContext jaxbContext = JAXBContext.newInstance(TicketBai.class);
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(tbai, bos);

			byte[] data = bos.toByteArray();
			TbaiSign tbaiSign = new TbaiSign();
			byte[] xml = TbaiSigner.getInstance().sign(tbaiConfiguration, data);
			String sign = tbaiSign.getSign(xml);
			TbaiResponse response = new TbaiResponse().setResponseStatus("pending").setSign(sign)
				.setTbaiId(tbaiSign.buildTbaiId(tbai, sign));

			TbaiBlockchain bc = new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
				.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
				.setSignature(response.getSign().substring(0, 100));

			DataRequest request = tbaiData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, xml);
		
			String tbaiId = tbaiConfiguration.isBizkaia() ? URLEncoder.encode(response.getTbaiId()) : response.getTbaiId();
		
			String qrUrl = TbaiUri.getUrlQr(tbaiConfiguration) + "?id=" + tbaiId + "&s="
				+ (invoice.getSeries() != null ? invoice.getSeries() : "") + "&nf=" + invoice.getNumber() + "&i="
				+ tbai.getFactura().getDatosFactura().getImporteTotalFactura();
			
			String crc = CRC8.calculate(qrUrl);
			qrUrl = qrUrl + "&cr=" + crc;

			DataResponse dr = tbaiData.saveResponsePending(company.getDomain(), new User().setLogin(""), invoice, response,
				bc, request, qrUrl);

			if (!tbaiConfiguration.isBizkaia()) {
				String uri = TbaiUri.getUrlEmision(tbaiConfiguration);
				response = sendXML(uri, tbaiConfiguration, xml, true);
				tbaiData.saveResponse(company.getDomain(), new User().setLogin(""), response, dr);
				HandleTbaiResponse(response);
			} else if (tbaiConfiguration.isBizkaia() && (!tbaiConfiguration.isTest() || "A99802019".equalsIgnoreCase(company.getDocument()) || "99980200M".equalsIgnoreCase(company.getDocument()))) {
				LROEResponse lroeResponse = null;
				LROEInfo info = null;
				if(!isPersonaFisica(company.getDocument())) {
					LROE240_1_1 lroe240 = new LROE240_1_1();
					info = lroe240.buildInfo(OperacionEnum.A_00);
					lroeResponse = lroe240.alta(company, tbaiConfiguration, invoice, xml);
				} else if(AonDocumentUtil.isAssetCommunity(company.getDocument())) {
				    Person person = new Person().copy(company);
                    EnterpriseActivity ea = AON.getEnterpriseActivity(company.getDomain().getName(),
                        company.getDomain().getId(), "", invoice.getActivity().getId());
                    if(ea == null || ea.getId() == null) {
                        ea = AON.getEnterpriseActivities(company.getDomain().getName(),
                            company.getDomain().getId(), "").filter(f -> f.isPrincipal()).findFirst().orElse(new EnterpriseActivity());
                    }
                    invoice.setEpigraph(ea.getIae().getFullEpigraph());
                    LROE140_1_1 lroe140 = new LROE140_1_1();
                    info = lroe140.buildInfo(OperacionEnum.A_00);
                    lroeResponse = lroe140.alta(tbaiConfiguration, person, invoice, xml);
				} else {
					Person person = AON.getPerson(company.getDomain(), "", f -> f.getIdProperty().eq(company.getId()));
					EnterpriseActivity ea = AON.getEnterpriseActivity(company.getDomain().getName(),
						company.getDomain().getId(), "", invoice.getActivity().getId());
					if(ea == null || ea.getId() == null) {
						ea = AON.getEnterpriseActivities(company.getDomain().getName(),
							company.getDomain().getId(), "").filter(f -> f.isPrincipal()).findFirst().orElse(new EnterpriseActivity());
					}
					invoice.setEpigraph(ea.getIae().getFullEpigraph());
					LROE140_1_1 lroe140 = new LROE140_1_1();
					info = lroe140.buildInfo(OperacionEnum.A_00);
					lroeResponse = lroe140.alta(tbaiConfiguration, person, invoice, xml);
				}
				LroeData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, lroeResponse, info);
				if(lroeResponse.isError()) {
					dr.setSource(DataResponseSource.TBAI_TEST);
					AON.updateDataResponse(company.getDomain().getName(), company.getDomain().getId(),
							"", dr, f -> f.getIdProperty().eq(dr.getId()));
				}
				HandleLroeResponse(lroeResponse);
			}
		}
	}
	
	public boolean isPersonaFisica(String document) {
		return !AonDocumentUtil.isValidCIF(document) || AonDocumentUtil.isAssetCommunity(document)
			|| AonDocumentUtil.isOwnerCommunity(document) || AonDocumentUtil.isCivilSociety(document);
	}
	
	public void createAnulacionTBAI(Company company, Invoice invoice, TbaiConfiguration tbaiConfiguration)
			throws Exception {
		TbaiData tbaiData = TbaiData.getInstance(tbaiConfiguration); 
		final AnulaTicketBai tbai = Invoice2tbai.buildBaja(company, invoice, tbaiConfiguration);

		final JAXBContext jaxbContext = JAXBContext.newInstance(AnulaTicketBai.class);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		jaxbMarshaller.marshal(tbai, bos);
		byte[] data = bos.toByteArray();

		byte[] xml = TbaiSigner.getInstance().sign(tbaiConfiguration, data);
		DataRequest request = tbaiData.saveRequestAnulacion(company.getDomain(), new User().setLogin(""), invoice, xml);
		if (!tbaiConfiguration.isBizkaia()) {
			String uri = TbaiUri.getUrlAnulacion(tbaiConfiguration);
			TbaiResponse response = sendXML(uri, tbaiConfiguration, xml, true);
			tbaiData.saveResponseAnulacion(company.getDomain(), new User().setLogin(""), invoice, response, request);
			HandleTbaiResponse(response);
		} else if (tbaiConfiguration.isBizkaia() && (!tbaiConfiguration.isTest() || "A99802019".equalsIgnoreCase(company.getDocument()) || "99980200M".equalsIgnoreCase(company.getDocument()))) {
			LROEResponse lroeResponse = null;
			LROEInfo info = null;
			if (AonDocumentUtil.isValidCIF(company.getDocument())) {
				LROE240_1_1 lroe240 = new LROE240_1_1();
				info = lroe240.buildInfo(OperacionEnum.AN_0);
				lroeResponse = lroe240.anulacion(company, tbaiConfiguration, invoice, xml);
			} else {
				Person person = AON.getPerson(company.getDomain(), "", f -> f.getIdProperty().eq(company.getId()));
				LROE140_1_1 lroe140 = new LROE140_1_1();
				info = lroe140.buildInfo(OperacionEnum.AN_0);
				lroeResponse = lroe140.anulacion(tbaiConfiguration, person, invoice, xml);
			}
			LroeData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, lroeResponse, info);
			HandleLroeResponse(lroeResponse);
		}
	}

	public TbaiResponse sendXML(String uri, TbaiConfiguration tbaiConfiguration, byte[] xml, boolean withSign) throws StatusCodeException {
		URL url;
		try {
			Document doc = getDocument(xml);
			String sign = "";
			if(withSign) {
				sign = doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
			}

			ByteArrayInputStream key = new ByteArrayInputStream(tbaiConfiguration.getCertificate().getCertificate());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(key, tbaiConfiguration.getCertificate().getPassword().toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(keyStore, tbaiConfiguration.getCertificate().getPassword().toCharArray());

			TrustManager[] trustAll = new TrustManager[] { new TrustAllCertificates() };

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
			SSLContext.setDefault(sslContext);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			url = new URL(uri);
			URLConnection con = url.openConnection();
			HttpsURLConnection https = (HttpsURLConnection) con;

			https.setHostnameVerifier(new TrustAllHosts());
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/xml; charset=utf-8;");
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setUseCaches(false);

			System.out.println(
					"\n\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\t SERVICE REQUEST: ");
			System.out.println(
					"\t-------------------------------------------------------------------------------------------------------------------------------------------------");

			System.out.println(toString(doc));

			OutputStream os = https.getOutputStream();
			os.write(xml);
			os.close();

			System.out.println("\n\tServer status: \t" + https.getResponseCode() + ": " + https.getResponseMessage());
			System.out.println("\tMethod used: \t" + https.getRequestMethod());
			System.out.println("\tEncoding used: \t" + https.getRequestProperty("Content-Type"));

			System.out.println(
					"\n\t-------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println("\t SERVICE RESPONSE: ");
			System.out.println(
					"\t-------------------------------------------------------------------------------------------------------------------------------------------------");

			InputStream response = (InputStream) https.getContent();
			byte[] bytes = response.readAllBytes();
			return getTbaiResponse(bytes, sign);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return new TbaiResponse().setDescription(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			return new TbaiResponse().setDescription(e.getMessage());
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getMessage());
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class TrustAllCertificates implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private class TrustAllHosts implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}

	public TbaiResponse getTbaiResponse(byte[] bytes, String sign) {
		try {
			System.out.println("\t Parsing XML response.... ");

			InputStream is = new ByteArrayInputStream(bytes);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			System.out.println("\t XML version: \t " + doc.getXmlVersion());

			String idTbai;
			try {
				idTbai = doc.getElementsByTagName("IdentificadorTBAI").item(0).getTextContent();
			} catch (Exception e) {
				idTbai = null;
			}

			String estado;
			try {
				estado = doc.getElementsByTagName("Estado").item(0).getTextContent();
			} catch (Exception e) {
				estado = null;
			}

			String fecha_str;
			try {
				fecha_str = doc.getElementsByTagName("FechaRecepcion").item(0).getTextContent();
			} catch (Exception e) {
				fecha_str = null;
			}

			String descripcion;
			try {
				descripcion = doc.getElementsByTagName("Descripcion").item(0).getTextContent();
			} catch (Exception e) {
				descripcion = null;
			}

			String descripcion_eus;
			try {
				descripcion_eus = doc.getElementsByTagName("Azalpena").item(0).getTextContent();
			} catch (Exception e) {
				descripcion_eus = null;
			}

			String validation_code;
			try {
				validation_code = doc.getElementsByTagName("Codigo").item(0).getTextContent();
			} catch (Exception e) {
				validation_code = null;
			}

			String validation_desc;
			try {
				validation_desc = doc.getElementsByTagName("Descripcion").item(1).getTextContent();
			} catch (Exception e) {
				validation_desc = null;
			}

			String validation_desc_eus;
			try {
				validation_desc_eus = doc.getElementsByTagName("Azalpena").item(1).getTextContent();
			} catch (Exception e) {
				validation_desc_eus = null;
			}

			System.out.println(toString(doc));

			return new TbaiResponse().setSign(sign).setTbaiId(idTbai).setStatus(Integer.parseInt(estado))
					.setDescription(descripcion).setDescriptionEUS(descripcion_eus)
					.setReceptionDate(AonDateUtils.parse(fecha_str, "dd-MM-yyyy hh:mm:ss"))
					.setValidationCode(AonNumberUtils.toInteger(validation_code))
					.setValidationDescription(validation_desc).setValidationDescriptionEUS(validation_desc_eus)
					.setOk(idTbai != null).setData(bytes);

		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString(Document doc) {
		try {
			java.io.StringWriter sw = new java.io.StringWriter();
			javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new javax.xml.transform.dom.DOMSource(doc),
					new javax.xml.transform.stream.StreamResult(sw));
			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		}
	}
}
