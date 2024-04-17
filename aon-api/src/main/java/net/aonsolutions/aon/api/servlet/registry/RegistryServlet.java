package net.aonsolutions.aon.api.servlet.registry;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.DomainLinkedJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RecordDataJSON;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.json.RegistryPaymethodJSON;
import com.esferalia.aon.occam.api.json.RegistryProfileJSON;
import com.esferalia.aon.occam.api.json.RegistryRelationshipJSON;
import com.esferalia.aon.occam.api.json.RegistrySegmentJSON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.Filter.RRelationshipFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
			case "/suggestedAccount":
				response(req, resp, getRegistrySuggestedAccount(api));
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
		LOGGER.info("EXAMPLE SERVLET - PUT METHOD");
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
	
	public static JSONArray getRegistryAdditionalInfo(AonApiData api, Stream<Customer> stream) {
		JSONArray arr = new JSONArray();
		stream.forEach(c -> {
			JSONObject json = CustomerJSON.toJSON(c);
			getRegistryAdditionalInfo(json, api, api.getData(), c.getId(), null);
			arr.put(json);
		});
		return arr;
	}
	
	public static JSONObject getRegistryAdditionalInfo(JSONObject object, AonApiData api, JSONObject json, Integer registryId, LinkedList<RegistryAdditionalInfo> rais) {
		if(rais == null) {
			JSONArray addInfo = json.opt("additional_info") != null
				? json.optJSONArray("additional_info") : new JSONArray();
			if (addInfo == null) {
				String stringArray = json.optString("additional_info");
				String[] values = AonStringUtils.split(stringArray, ',');
				addInfo = new JSONArray(values);
			}
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
				    Options options = new Options().setFull(true);
				    RegistryPayMethod rpm = AON.getRegistryPayMethod(api.getDomain(), api.getUser(), f -> f.getRegistryProperty().eq(registryId), options);
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
				
				if(RegistryAdditionalInfo.BILLABLE.equals(rai)) {
					Optional<RegistryAddInfo> addinfo = AON.getRegistryAddInfo(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getAttributeProperty().eq("AON_BILLABLE").and(f.getRegistryProperty().eq(registryId)));
					object.put(IJsonNames.BILLABLE, addinfo.isEmpty());
				}
				
				if (RegistryAdditionalInfo.DOMAIN_LINKED.equals(rai)) {
					List<DomainLinked> domainsLinked = AON.getDomainLinkedList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), registryId);
					object.put(IJsonNames.DOMAIN_LINKED, DomainLinkedJSON.toJSON(domainsLinked));
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
			if(!rpaymethod.getPayMethod().isEmpty())
			    AON.saveRegistryPayMethod(api.getDomain(), api.getUser(), rpaymethod);
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
			.stream().filter(s-> !s.getSegment().isEmpty()).forEach(rsegment -> {
				if(rsegment.getRegistry() == null) rsegment.setRegistry(registryId);
				AON.saveRegistrySegment(rsegment.getDomain(), api.getUser(), rsegment);
			});
		}
		
		if(json.opt(RegistryAdditionalInfo.RPROFILE.name().toLowerCase()) != null) {
			JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.RPROFILE.name().toLowerCase());
			RegistryProfileJSON.fromJSON(arr)
			.stream().forEach(rprofile -> {
				AON.saveRegistryProfile(api.getDomain(), api.getUser().getLogin(), registryId, rprofile.getQuestionAlias(), rprofile.getValue());
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
        Options options = new Options().setFull(true);
	    RegistryPayMethod rpm = AON.getRegistryPayMethod(api.getDomain(), api.getUser(), f -> f.getRegistryProperty().eq(registry), options);
	    return RegistryPaymethodJSON.toJSON(rpm);

	}
	
	private JSONObject getRegistrySuggestedAccount(AonApiData api) {
		Integer registry = JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY);
		InvoiceType type = InvoiceType.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.TYPE));
		Account account = ACCOUNTING.getSuggestedAccounts(api.getDomain(), api.getUser(), registry, type)
				.stream().findFirst().orElse(new Account());		
	    return AccountJSON.toJSON(account);
	}
	
}
