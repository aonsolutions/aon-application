package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.PayMethodJSON;
import com.esferalia.aon.occam.api.json.RecordDataJSON;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.json.RegistryPaymethodJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.json.RegistrySegmentJSON;
import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiRegistryServlet", urlPatterns = {"/ms/api/registry/*"})
public class RegistryServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(RegistryServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API REGISTRY SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getRegistry(api, api.getData()));
				break;
			case "/address":
				response(req, resp, getRegistryAddress(api));
				break;
			case "/banks":
				response(req, resp, getRegistryBanks(api));
				break;
			case "/paymethod":
				response(req, resp, getRegistryPaymethod(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getRegistry(api, api.getData()));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("EXAMPLE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveRegistry(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getRegistry(AonApiData api, JSONObject json) {
		Integer id = json.opt(IJsonNames.REGISTRY) != null 
				? json.optInt(IJsonNames.REGISTRY)
				: json.optInt(IJsonNames.ID);
		Registry registry = AON.getRegistry(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(id));
		JSONObject object = RegistryJSON.toJSON(registry);
		
		return getRegistryAdditionalInfo(object, api, json, id, null);
	}


	
	public static JSONObject saveRegistry(AonApiData api) {
		Registry registry = RegistryJSON.fromJSON(api.getData());
		if(AonDocumentUtil.isValid(registry.getDocument())) {
			registry.setLegalPerson(AonDocumentUtil.isValidCIF(registry.getDocument()));
		}
		registry = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), registry);
		saveRegistryAdditionalInfo(api, registry.getId(), registry.getDomain().getId());
		return new JSONObject();
	}
	
	public static JSONObject getRegistryAdditionalInfo(JSONObject object, AonApiData api, JSONObject json, Integer registryId, LinkedList<RegistryAdditionalInfo> rais) {
		if(rais == null) {
			JSONArray addInfo = json.opt("additional_info") != null
				? json.optJSONArray("additional_info") : new JSONArray();
			rais = new LinkedList<>();
			for(Integer i = 0; i < addInfo.length(); i++) {
				rais.add(RegistryAdditionalInfo.safeValueOf(addInfo.optString(i)));
			}
		}
		if(registryId != null) {
			rais.stream().forEach(rai -> {
				if(RegistryAdditionalInfo.ADDRESS.equals(rai)) {
					RegistryAddress address = AON.getMain(api.getDomain(), api.getUser(), registryId);
					object.put(rai.name().toLowerCase(), RegistryAddressJSON.toJSON(address));
				}
			
				if(RegistryAdditionalInfo.ADDRESSES.equals(rai)) {
					RegistryAddressFilter filter = f -> f.getRegistryProperty().eq(registryId);
					Stream<RegistryAddress> addresses = AON.getStream(api.getDomain(), api.getUser(), filter);
					object.put(rai.name().toLowerCase(), RegistryAddressJSON.toJSON(addresses));
				}
			
				if(RegistryAdditionalInfo.BANKS.equals(rai)) {
					object.put(rai.name().toLowerCase(), RegistryBankJSON.toJSON(
						AON.getRegistryBankStream(api.getDomain(), api.getUser().getLogin(), f -> f.getRegistryProperty().eq(registryId))));
				}
			
				if(RegistryAdditionalInfo.PAYMETHOD.equals(rai)) {
				    RegistryPayMethod rpm = AON.getRegistryPayMethod(api.getDomain(), api.getUser(), f -> f.getRegistryProperty().eq(registryId));
					object.put(rai.name().toLowerCase(), RegistryPaymethodJSON.toJSON(rpm));
				}
			
				if(RegistryAdditionalInfo.MEDIA.equals(rai)) {
					RegistryMediaFilter filter  = f -> f.getRegistryProperty().eq(registryId);
					object.put(rai.name().toLowerCase(),
						RegistryMediaJSON.toJSON(AON.getStream(api.getDomain(), api.getUser(), filter)));
				}
				
				if(RegistryAdditionalInfo.RECORD_DATA.equals(rai)) {
					object.put(rai.name().toLowerCase(),
						RecordDataJSON.toJSON(
							AON.getRecordData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
								f -> f.getRegistryProperty().eq(registryId))));
				}
				
				if(RegistryAdditionalInfo.RSEGMENT.equals(rai)) {
					RegistrySegmentFilter filter  = f -> f.getRegistryProperty().eq(registryId);
					object.put(rai.name().toLowerCase(),
						RegistrySegmentJSON.toJSON(AON.getRegistrySegmentStream(api.getDomain(), api.getUser(), filter))
					);
				}
				
				if(RegistryAdditionalInfo.RRELATIONSHIP.equals(rai)) {
					RRelationshipFilter filter  = f -> f.getRegistryProperty().eq(registryId).and(f.getRelationshipProperty().eq(-1));
					object.put(rai.name().toLowerCase(),
						RegistryRelationshipJSON.toJSON(AON_SOLUTIONS.getRegistryRelationshipStream(api.getDomain(), api.getUser(), filter))
					);
				}
			});
		}
		return object;
	}
	
	public static void saveRegistryAdditionalInfo(AonApiData api, Integer registryId, Integer registryDomain) {
		JSONObject json = api.getData();
		if(json.opt(RegistryAdditionalInfo.ADDRESSES.name().toLowerCase()) != null) {
			JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.ADDRESSES.name().toLowerCase());
			RegistryAddressJSON.fromJSON(arr).stream().forEach(address -> {
				address.setDomain(registryDomain);
				if(address.getRegistry() == null) address.setRegistry(registryId);
				AON.save(api.getDomain(), api.getUser().getLogin(), address);
			});
		}
		
		if(json.opt(RegistryAdditionalInfo.MEDIA.name().toLowerCase()) != null) {
			JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.MEDIA.name().toLowerCase());
			RegistryMediaJSON.fromJSON(arr).stream().forEach( media -> {
				media.setDomain(registryDomain);
				if(media.getRegistry() == null) media.setRegistry(registryId);
				if(!AonStringUtils.isBlank(media.getValue()))
					AON.save(api.getDomain(), api.getUser().getLogin(), media);
			});
		}
		
		if(json.opt(RegistryAdditionalInfo.BANKS.name().toLowerCase()) != null) {
			JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.BANKS.name().toLowerCase());
			RegistryBankJSON.fromJSON(arr).stream().forEach( bank -> {
				bank.setDomain(registryDomain);
				if(bank.getRegistry() == null) bank.setRegistry(registryId);
				AON.saveRegistryBank(api.getDomain(), api.getUser().getLogin(), bank);
			});
		}
		
		if(json.opt(RegistryAdditionalInfo.PAYMETHOD.name().toLowerCase()) != null) {
			RegistryPayMethod rpaymethod = RegistryPaymethodJSON.fromJSON(json.optJSONObject(RegistryAdditionalInfo.PAYMETHOD.name().toLowerCase()));
			rpaymethod.setDomain(registryDomain);
			if(rpaymethod.getRegistry() == null) rpaymethod.setRegistry(registryId);
			if(rpaymethod.getPayMethod() != null) AON.saveRegistryPayMethod(api.getDomain(), api.getUser(), rpaymethod);
		}
		
		if(json.opt(RegistryAdditionalInfo.RECORD_DATA.name().toLowerCase()) != null) {
			RecordData recordData = RecordDataJSON.fromJSON(json.optJSONObject(RegistryAdditionalInfo.RECORD_DATA.name().toLowerCase()));
			recordData.setDomain(registryDomain);
			if(recordData.getRegistry() == null) recordData.setRegistry(registryId);
			AON.saveRecordData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), recordData);
		}
		
		if(json.opt(RegistryAdditionalInfo.RSEGMENT.name().toLowerCase()) != null) {
			JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.RSEGMENT.name().toLowerCase());
			RegistrySegmentJSON.fromJSON(arr)
			.stream()
			.filter(s-> !s.getSegment().isEmpty())
			.forEach(rsegment -> {
				if(rsegment.getRegistry() == null) rsegment.setRegistry(registryId);
				AON.saveRegistrySegment(rsegment.getDomain(), api.getUser(), rsegment);
			});
		}
	}
	
	private JSONObject getRegistryAddress(AonApiData api) {
		RegistryAddress address = api.getData().optBoolean("global")
			? AON.getMain("global.aonsolutions.net", 0, api.getUser().getLogin(), api.getData().optInt(IJsonNames.REGISTRY))
			: AON.getMain(api.getDomain(), api.getUser(), api.getData().optInt(IJsonNames.REGISTRY));
		return RegistryAddressJSON.toJSON(address);
	}
	
	private JSONArray getRegistryBanks(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		Stream<RegistryBank> rbanks = AON.getRBankStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId()).and(f.getRegistryProperty().eq(id)));
		return RegistryBankJSON.toJSON(rbanks);
	}
	
	private JSONObject getRegistryPaymethod(AonApiData api) {
		Integer registry = api.getData().optInt(IJsonNames.REGISTRY);
		RegistryPayMethod rpm = AON.getRPayMethod(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getRegistryProperty().eq(registry));
		PayMethod pm = AON.getPayMethod(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(rpm.getPayMethod()));
		RegistryBank rbank =  AON.getRBank(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(rpm.getRbank()));
		JSONObject json = new JSONObject();
		json.put(IJsonNames.PAYMETHOD, PayMethodJSON.toJSON(pm));
		json.put("rbank", RegistryBankJSON.toJSON(rbank));
		return json;
	}
}
