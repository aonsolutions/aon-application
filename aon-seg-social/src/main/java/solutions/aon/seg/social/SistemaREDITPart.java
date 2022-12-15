package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.ITPart;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.object.It.ItBuilder;
import solutions.aon.seg.social.object.ItPartId;
import solutions.aon.seg.social.toolkit.Toolkit;

class SistemaREDITPart extends ServicioREDPartRegeXML {
	
	//Toolkit.buildFile(htmlPage.asXml().getBytes(), System.getProperty("user.home")+"/Documentos/test.html");
	
	private static final String FORMAT_DATE = "dd/MM/yyyy";

	public static Collection<It> getIts(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc, Date from, Date to, Optional<String> nss)
			throws SegSocialException {
		String link = "";
		String ticket = "";

		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=IWXP0001");
			Toolkit.checkProsaError(body);
			checkAuthorization(body);
			
			link ="https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
			ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");

			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("SPM.ACC.CONTINUAR_CONSULTA", "CONTINUAR_CONSULTA"));
			params.add(new BasicNameValuePair("nafConsulta", ""));
			params.add(new BasicNameValuePair("fechaBajaMedConsulta", ""));
			params.add(new BasicNameValuePair("regimenConsulta", regime));
			params.add(new BasicNameValuePair("cccConsulta", ccc));
			
			nss.ifPresent(n-> params.add(new BasicNameValuePair("nafConsulta", n)));
	
			//DATES
			Toolkit.formatDate(from, FORMAT_DATE).ifPresent(d-> params.add(new BasicNameValuePair("fechaDesdeConsulta", d)));
			Toolkit.formatDate(to, FORMAT_DATE).ifPresent(d-> params.add(new BasicNameValuePair("fechaHastaConsulta", d)));
		
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
		
			try {			
			
				link = "https://w2.seg-social.es" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_6"), "action");
				ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");
				
				String xml = Toolkit.getBodyPOST(httpClient, httpPost);
				checkErrors(xml);
				
				return getParts(httpClient, xml, link, ticket);
			} catch (SAXException e) {
				e.printStackTrace();
				throw new SegSocialException(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
	}
	
	private static Collection<It> getParts(CloseableHttpClient httpClient, String xml, String link, String ticket) throws ParserConfigurationException, IOException, SegSocialException {
		List<ITPart> parts = new ArrayList<>();
		List<ITPart> aux = new ArrayList<>();
		String error = "";
		boolean next;
		do {
			next = false;
			try {
				List<ITPart> auxTwo = new ArrayList<>();
				Map<Integer, ITPart> map = getInfoPartsByXml(xml);
				for (Map.Entry<Integer, ITPart> entry : map.entrySet()) {
					Integer position = entry.getKey();
					ITPart part = entry.getValue();
					
					String newLink = link+"?SPM.CONTEXT=internet&ARQ.SPM.OUT=XML_STYLESHEET&ES_FW4=1&SPM.HAYJS=1&ARQ.SPM.TICKET="+ticket+"&SPM.ISPOPUP=0&SPM.ACC.DETALLES_PARTE_CONSULTA=DETALLES_PARTE_CONSULTA&position="+position;
					
					setInfoPart(Toolkit.getBodyGET(httpClient, newLink), part);
					
					if (!parts.contains(part)) {										
						auxTwo.add(part);
					}
					
					next = position >=10;
				}
		
				if(!aux.containsAll(auxTwo)) {
					parts.addAll(auxTwo);
					aux = auxTwo;
					if(next) {
						xml = Toolkit.getBodyGET(httpClient, link+"?SPM.CONTEXT=internet&ARQ.SPM.OUT=XML_STYLESHEET&ES_FW4=1&SPM.HAYJS=1&ARQ.SPM.TICKET="+ticket+"&SPM.ISPOPUP=0&SPM.ACC.PAG_SIG_CONSULTA=PAG_SIG_CONSULTA");
					}
				} else {
					next = false;
				}
		    } catch (SAXException e) {
				e.printStackTrace();
				error = e.getMessage();
				next = false;
			}
		} while(next);
		
		if(parts.isEmpty() && !error.isEmpty()) {
			throw new SegSocialException(error);
		} else {
			return orderByIT(parts);
		}
	}
	
	private static List<It> orderByIT(List<ITPart> itParts) {
		HashMap<ItPartId, Collection<ITPart>> orderedItParts = new HashMap<>();

		for (ITPart itp : itParts) {
			Optional<Date> workLeaveDate = itp.getWorkLeaveDate();
			Optional<String> naf = itp.getNaf();
			
			if( workLeaveDate.isPresent() && naf.isPresent() ) {
				
				ItPartId id = new ItPartId(workLeaveDate.get(), naf.get());
				
				if (orderedItParts.containsKey(id)) {					
					orderedItParts.get(id).add(itp);
				} else {
					ArrayList<ITPart> list = new ArrayList<>();
					list.add(itp);
					orderedItParts.put(id, list);
				}
			}
		}
		
		ArrayList<It> its = new ArrayList<>();
		Set<ItPartId> partIds = orderedItParts.keySet();
		ItBuilder builder = new ItBuilder();
		
		for (ItPartId id : partIds) {
	
			itParts =  (List<ITPart>) orderedItParts.get(id);
			ITPart end = null;
			ITPart start = null;
			ArrayList<ITPart> confirmations = new ArrayList<>();

			for (ITPart itp : itParts) {
				String partStr = itp.getPartType().toLowerCase();
				if (partStr.indexOf("baja")>-1 || partStr.indexOf("PB")>-1) {					
					start = itp;
				} else if ((partStr.indexOf("confirmaci\u00F3n")>-1 || partStr.indexOf("PC")>-1)  && !confirmations.contains(itp)) {					
					confirmations.add(itp);
				} else if (partStr.indexOf("alta")>-1 || partStr.indexOf("PA")>-1) {					
					end = itp;
				}
			}
			
			if(null!=start) {
				
				Optional<String> typeProcess = start.getTypeProcess();
				if(null==end && typeProcess.isPresent() && typeProcess.get().toLowerCase().contains("muy corto")) {
					ITPart tmp = new ITPart();
					tmp.setReceptionDate(start.getReceptionDate());
					tmp.setCauseRestart("6 Mejor\u00EDa permite trabajar");
					tmp.setPartType("Parte de alta");
					start.getNaf().ifPresent(tmp::setNaf);
					start.getWorkLeaveDate().ifPresent(workDate->{
						tmp.setWorkLeaveDate(workDate);
						tmp.setWorkRestartDate(Toolkit.addDays(workDate, 1));
					});
					end = tmp;
				}
				
				its.add(builder.setStart(start).setConfirmations(confirmations).setEnd(end).build());
			}
		}
		return its.stream().sorted((o1, o2)-> o1.getStart().getWorkLeaveDate().get().compareTo(o2.getStart().getWorkLeaveDate().get())).collect(Collectors.toList());
	}
}
