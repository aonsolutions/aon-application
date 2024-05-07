package net.aonsolutions.aon.api.servlet.marketing;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.StreetType;
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
				;
		
		AON.saveMarketingActionTarget(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), mkActionTarget);
		
		return api.getData();
	}
}
