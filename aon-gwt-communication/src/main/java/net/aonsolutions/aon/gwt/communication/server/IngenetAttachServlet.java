package net.aonsolutions.aon.gwt.communication.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.ingenet.servlet.DeliveryCreator;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;

@SuppressWarnings("serial")
@WebServlet(name = "IngenetAttachServlet", urlPatterns = { "/ingenet_attach/*", "/aon_gwt_aio/ingenet_attach/*" })
public class IngenetAttachServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(IngenetAttachServlet.class.getName());
	
	final String DELIVERY = "delivery";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Ingenet Attach FTP Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1];
		String userName = pathInfo[2];
		String md5 = Utils.getMd5(userName + domainName);
		
		Map<String, String[]> filterMap = req.getParameterMap();
		String[] idLsit = null;
		if (filterMap.containsKey("id_list")) {
			idLsit = filterMap.get("id_list");
		}
		
		if (accessToken.equals(md5)) {
			Domain domain = AON.getDomain(domainName, 1, userName, f -> f.getNameProperty().eq(domainName));
			if (pathInfo.length > 3) {
				Object object = new Object();
				JSONObject meta = new JSONObject();
				
				switch (pathInfo[3]) {
				case DELIVERY:
					object = processDeliveryAttach(domain, userName, req, resp, idLsit);
					break;
				default:
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Seres Servlet - POST METHOD");
	}

	
	public JSONArray processDeliveryAttach(Domain domain, String loggedUser, HttpServletRequest req, HttpServletResponse resp, String[] _idList) {
		List<Integer> idList = Arrays.asList(_idList).stream().map(o -> Integer.parseInt(o))
				.collect(Collectors.toCollection(LinkedList::new));
		
		Integer[] ids = idList.toArray(new Integer[idList.size()]);
		Stream<Attach> attachStream = AON.getAttachStream(domain.getName(), domain.getId(), loggedUser,
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().in(ids)), AttachType.DATA, true);
		
		List<String> messages = new LinkedList<>();
		messages.add("## RESULTADOS");
		attachStream.forEach(attach -> {
			DeliveryCreator creator = new DeliveryCreator(domain.getName(), domain.getId(), loggedUser);
			String xml = new String(attach.getData());
			try {
				creator.validateAlbaranesXmlPattern(xml);
				creator.create(xml);
			} catch (Exception e) {
				creator.getErrorList().add("Los datos no han pasado el proceso de validacion");
				creator.getErrorList().add(e.getMessage());
			}
			
			if(creator.getErrorList()!=null && !creator.getErrorList().isEmpty()) {
				messages.add("# Imposible procesar datos del " + attach.getCreationDate());
				creator.getErrorList().forEach(m->messages.add("[ERROR] "+m));
			} else {
				creator.processDataAttach(attach, creator.getDeliveryList());
				if(creator.getDeliveryList()!=null && !creator.getDeliveryList().isEmpty()) {
					messages.add("# Datos procesados correctamente");
					creator.getDeliveryList().forEach(d->messages.add("_ Nuevo albaran: "+d.getReferenceCode()));
				} else {
					messages.add("# No se han creado albaranes.");
				}
			}
			if(creator.getWarningList()!=null && !creator.getWarningList().isEmpty()) {
				creator.getWarningList().forEach(m->messages.add("[WARNING] "+m));
			}
		});
		
		JSONArray array = new JSONArray();
		messages.forEach(msg -> {
			try {
				JSONObject json = new JSONObject();
				array.put(json.put("name", msg));
				if(msg.contains("[WARNING]"))
					array.put(json.put("id", 1));
				else if(msg.contains("[ERROR]"))
					array.put(json.put("id", 0));					
			} catch (JSONException e) {
				LOGGER.severe(e.getMessage());
			}
		});
		
//		System.out.println(array);
		return array;
	}
	
	
	// /////////////////// 
	// SERVLET UTILS
	// ///////////////////
	static interface MSG {
		final static String ACCESS_TOKEN = "access_token";
		final static String CALLBACK = "callback";
	}
	
	static class Utils {
	
		public static String getMd5(String str){
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
	        md.update(str.getBytes());
	        byte byteData[] = md.digest();
	
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        return sb.toString();
		}
		
		public static void giveBack(HttpServletRequest req, HttpServletResponse resp,
				Object object, JSONObject meta) {
			try {
				String js = req.getParameter(MSG.CALLBACK);
				if(js != null){
					resp.setContentType("application/javascript; charset=utf-8");     
					PrintWriter out = resp.getWriter();
					out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
					out.flush();
				} else {
					resp.setContentType("application/json");     
					PrintWriter out = resp.getWriter();
					out.print(object);
					out.flush();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}
	
	}
	
	

}
