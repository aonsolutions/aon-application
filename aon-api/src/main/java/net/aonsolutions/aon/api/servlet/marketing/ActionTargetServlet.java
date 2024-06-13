package net.aonsolutions.aon.api.servlet.marketing;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingSellerDistribution;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiActionTargetServlet", urlPatterns = {"/ms/api/action-target/*"})
public class ActionTargetServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ActionTargetServlet.class.getName());
	
	public static final String ACTION_TAGET = "/";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(ACTION_TAGET, ActionTargetServlet::saveActionTarget)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	public static JSONObject saveActionTarget(AonApiData api) {
		JSONObject actionTargetJson = api.getData().optJSONObject("actionTarget");
		JSONObject targetJson = actionTargetJson.optJSONObject("target");
		JSONObject marketingActionJson = actionTargetJson.optJSONObject("marketingAction");
		
		// Marketing Action Target
		Integer actionId = Integer.parseInt(marketingActionJson.getString("id"));
		Integer workgroupId = AonStringUtils.isBlank(marketingActionJson.getString("workgroup")) ? null : Integer.parseInt(marketingActionJson.getString("workgroup"));
		MarketingSellerDistribution sellerDistribution = AonStringUtils.isBlank(marketingActionJson.getString("sellerDistribution")) ? MarketingSellerDistribution.MANUAL : MarketingSellerDistribution.getSellerDistribution(Integer.parseInt(marketingActionJson.getString("sellerDistribution")));
		Integer sellerId = AonStringUtils.isBlank(marketingActionJson.getString("seller")) ? null : Integer.parseInt(marketingActionJson.getString("seller"));
		MarketingAction marketingAction = AON.getMarketingAction(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), actionId);
		
		// Target
		String name = targetJson.getString("name");
		String documentType = targetJson.getString("documentType");
		String documentCountry = targetJson.getString("documentCountry");
		String document = targetJson.getString("document");
		
		String streetType = targetJson.getString("streetType");
		String address = targetJson.getString("address");
		String number = targetJson.getString("number");
		String zip = targetJson.getString("zip");
		String geozoneCode = targetJson.getString("geozoneCode");
		String city = targetJson.getString("city");
		
		String phone = targetJson.getString("phone");
		String email = targetJson.getString("email");
		
		String comments = targetJson.getString("comments");

		Target target = new Target()
				.copy(
					new Registry()
						.setDomain(api.getDomain())
						.setName(name)
						.setDocumentType(DocumentType.values()[Integer.parseInt(documentType)])
						.setDocumentCountry(Country.safeValueOf(documentCountry))
						.setDocument(document)
						.setNationality(Country.safeValueOf(documentCountry))
						
				)
				.setScope(marketingAction.getMarketingCampaign().getScope())
				;
		
		target = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), target);
		
		if(AonStringUtils.isNotBlank(comments)) {
			RegistryNote note = new RegistryNote()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setDescription("Observaci\u00f3n")
					.setNoteDate(new Date())
					.setComments(comments)
					.setNoteType(NoteType.OBSERVATION)
					.setSecurityLevel(SecurityLevel.OFFICIAL)
					;
			
			AON.saveRegistryNote(api.getDomain(), api.getUser().getLogin(), note);
		}
		
		Integer raddressId = null;
		if(AonStringUtils.isNotBlank(address)) {
			Optional<GeoZone> geozoneOpt = AON.geozoneStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getCodeProperty().eq(geozoneCode))).findFirst();			
			
			RegistryAddress registryAddress = new RegistryAddress()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setMain(true)
					.setStreetType(StreetType.getForAeatCode(streetType, AonLanguage.SPANISH))
					.setAddress(address)
					.setNumber(number)
					.setZip(zip)
					.setCity(city)
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null)
					.setGeozoneCode(geozoneCode)
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null)
					;
			
			registryAddress = AON.save(api.getDomain(), api.getUser().getLogin(), registryAddress);
			raddressId = registryAddress.getId();
		}
		
		if(AonStringUtils.isNotBlank(phone)) {
			RegistryMedia registryMediaPhone = new RegistryMedia()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setMedia(MediaType.CELLULAR)
					.setValue(phone)
					.setCommercial(true)
					.setRaddress(raddressId)
					;
			
			AON.save(api.getDomain(), api.getUser().getLogin(), registryMediaPhone);
					
		}
		
		if(AonStringUtils.isNotBlank(email)) {
			RegistryMedia registryMediaEmail = new RegistryMedia()
					.setDomain(target.getDomain().getId())
					.setRegistry(target.getId())
					.setMedia(MediaType.EMAIL)
					.setValue(email)
					.setCommercial(true)
					.setRaddress(raddressId)
					;
			
			AON.save(api.getDomain(), api.getUser().getLogin(), registryMediaEmail);
		}
		
		// Marketing Action Target
		
		MarketingActionTarget mkActionTarget = new MarketingActionTarget()
				.copy(target)
				.setActionTargetDomain(target.getDomain().getId())
				.setMarketingAction(new MarketingAction().setId(actionId))
				.setActionTargetStatus((byte)0)
				.setComments(comments)
				;
		
		AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
		
		// Create Operacion Comercial
		if(sellerDistribution == MarketingSellerDistribution.MANUAL  && null != sellerId) {
			ProjectCommercial projectCommercial = new ProjectCommercial()
					.copy(new Project()
						.setDomain(target.getDomain())
						.setRegistry(target.get())
						.setName(marketingAction.getDescription())
						.setDate(new Date())
						.setTas(false)
						.setCommercial(true)
						.setReservation(false)
						.setActive(true)
					)
					.setTarget(target.getId())
					.setSeller(sellerId)
					.setComments(comments)
					.setSource((byte)8) // Marketing
					.setStatus((byte)0)
					.setStatusDate(new Date())
					.setProbability(0);
			
			AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), projectCommercial);
			
			mkActionTarget.setActionTargetStatus((byte)6); // Enviado
			AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
		} else if(sellerDistribution == MarketingSellerDistribution.AUTOMATIC && null != workgroupId) {
			Seller nextSeller = getNextLinealSellerByWorkgroup(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), workgroupId);
			if(null != nextSeller) {
				ProjectCommercial projectCommercial = new ProjectCommercial()
						.copy(new Project()
							.setDomain(target.getDomain())
							.setRegistry(target.get())
							.setName(marketingAction.getDescription())
							.setDate(new Date())
							.setTas(false)
							.setCommercial(true)
							.setReservation(false)
							.setActive(true)
						)
						.setTarget(target.getId())
						.setSeller(nextSeller.getId())
						.setComments(comments)
						.setSource((byte)8) // Marketing
						.setStatus((byte)0)
						.setStatusDate(new Date())
						.setProbability(0);
				
				AON.saveProjectCommercial(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), projectCommercial);
				
				mkActionTarget.setActionTargetStatus((byte)6); // Enviado
				AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
			}
		}
		
		return api.getData();
	}
	
	private static Seller getNextLinealSellerByWorkgroup(String domainName, int domain, String user, int workgroup) throws AonCoreException {
		List<TaskHolder> taskHolders = AON.getTaskHolderWorkgroupStream(new Domain().setName(domainName).setId(domain), new User().setLogin(user), f -> f.getDomainProperty().eq(domain), workgroup).filter(taskHolder -> taskHolder.isActive()).collect(Collectors.toList());
		Integer[] taskHolderIds = new Integer[taskHolders.size()];
		taskHolders.stream().map(taskHolder -> taskHolder.getId()).collect(Collectors.toList()).toArray(taskHolderIds);
		LinkedList<Seller> sellerList = AON.getSellerList(domainName, domain, user, f -> f.getStatusProperty().eq((byte)0).and(f.getTaskHolderProperty().in(taskHolderIds)));
		Map<Seller, Date> sellerProjects = new HashMap<Seller, Date>();
		
		for(Seller seller : sellerList) {
			LinkedList<ProjectCommercial> projectCommercials = AON.getProjectCommercialList(domainName, domain, user, f -> f.getSellerProperty().eq(seller.getId()));
			
			// Si esta activo y no tiene ninguna operacion comercial se devuelve este
			if(projectCommercials.isEmpty()) return seller;
			
			projectCommercials.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
			sellerProjects.put(seller, projectCommercials.get(0).getDate());
		}
		
		Optional<Date> oldestDate = sellerProjects.values().stream().sorted((d1, d2) -> d1.compareTo(d2)).findFirst();
		if(oldestDate.isEmpty()) return null;
		else {
			for(Entry<Seller, Date> entry : sellerProjects.entrySet()) {
				if(entry.getValue().equals(oldestDate.get())) return entry.getKey();
			}
			return null;
		}
	}
}
