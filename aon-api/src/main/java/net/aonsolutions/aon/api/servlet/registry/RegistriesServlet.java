package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.CustomerJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RecordDataJSON;
import com.esferalia.aon.occam.api.json.RegistryAddressJSON;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.RegistryMediaJSON;
import com.esferalia.aon.occam.api.json.RegistryPaymethodJSON;
import com.esferalia.aon.occam.api.json.RegistrySegmentJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySegmentFilter;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCustomersServlet", urlPatterns = {"/ms/api/customers/*"})
public class RegistriesServlet extends AonApiHttpServlet {
		
    private static final Logger LOGGER = Logger.getLogger(RegistriesServlet.class.getName());

    public static final String REGISTRIES = "/";
    public static final String REGISTRY = "/:id";
    public static final String REGISTRY_EMAILS = "/:id/emails";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }

    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(REGISTRIES, RegistriesServlet::getRegistries)
                    .addRoute(REGISTRY, RegistriesServlet::getRegistry)
                    .addRoute(REGISTRY_EMAILS, RegistriesServlet::getRegistryEmails)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private void put(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(REGISTRIES, RegistriesServlet::saveRegistry)
                    .addRoute(REGISTRY, RegistriesServlet::saveRegistry)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private static JSONArray getRegistries(AonApiData api) {
        return RegistryJSON.toJSON(
                AON.getRegistryStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
                        f -> registryFilter(api, f)));
    }

    private static JSONObject getRegistry(AonApiData api) {
        JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
        Integer registryId = JsonUtils.getInteger(vars, IJsonNames.ID);
        Registry registry = AON.getRegistry(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(registryId));
        JSONObject object = RegistryJSON.toJSON(registry);

        return getRegistryAdditionalInfo(object, api, api.getData(), registryId, null);
    }
    
    public static JSONArray getRegistryEmails(AonApiData api) {
        JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
        Integer customerId = JsonUtils.getInteger(vars, IJsonNames.ID);
        ArrayList<String> list =AON.getRegistryMediaStream(api.getDomain(), api.getUser(), f -> 
            f.getDomainProperty().eq(api.getDomain().getId())
            .and(f.getRegistryProperty().eq(customerId))
            .and(f.getMediaProperty().eq(MediaType.EMAIL.value())))
        .map(r -> r.getValue()).collect(Collectors.toCollection(ArrayList::new));
        return new JSONArray(list);
    }
    
    public static JSONArray getRegistryPhones(AonApiData api) {
        JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
        Integer customerId = JsonUtils.getInteger(vars, IJsonNames.ID);
        ArrayList<String> list =AON.getRegistryMediaStream(api.getDomain(), api.getUser(), f -> 
            f.getDomainProperty().eq(api.getDomain().getId())
            .and(f.getRegistryProperty().eq(customerId))
            .and(f.getMediaProperty().eq(MediaType.FIXED_PHONE.value())
               .or(f.getMediaProperty().eq(MediaType.CELLULAR.value()))))
        .map(r -> r.getValue()).collect(Collectors.toCollection(ArrayList::new));
        return new JSONArray(list);
    }

    public static JSONObject getRegistryAdditionalInfo(JSONObject object, AonApiData api, JSONObject json,
            Integer registryId, LinkedList<RegistryAdditionalInfo> rais) {
        if (rais == null) {
            JSONArray addInfo = json.opt("additional_info") != null
                    ? json.optJSONArray("additional_info")
                    : new JSONArray();
            rais = new LinkedList<>();
            for (Integer i = 0; i < addInfo.length(); i++) {
                rais.add(RegistryAdditionalInfo.safeValueOf(addInfo.optString(i)));
            }
        }
        if (registryId != null) {
            rais.stream().forEach(rai -> {
                if (RegistryAdditionalInfo.ADDRESS.equals(rai)) {
                    RegistryAddress address = AON.getMain(api.getDomain(), api.getUser(), registryId);
                    object.put(rai.name().toLowerCase(), RegistryAddressJSON.toJSON(address));
                }

                if (RegistryAdditionalInfo.ADDRESSES.equals(rai)) {
                    RegistryAddressFilter filter = f -> f.getRegistryProperty().eq(registryId);
                    Stream<RegistryAddress> addresses = AON.getStream(api.getDomain(), api.getUser(), filter);
                    object.put(rai.name().toLowerCase(), RegistryAddressJSON.toJSON(addresses));
                }

                if (RegistryAdditionalInfo.BANKS.equals(rai)) {
                    object.put(rai.name().toLowerCase(), RegistryBankJSON.toJSON(
                            AON.getRegistryBankStream(api.getDomain(), api.getUser().getLogin(),
                                    f -> f.getRegistryProperty().eq(registryId))));
                }

                if (RegistryAdditionalInfo.PAYMETHOD.equals(rai)) {
                    RegistryPayMethod rpm = AON.getRegistryPayMethod(api.getDomain(), api.getUser(),
                            f -> f.getRegistryProperty().eq(registryId));
                    object.put(rai.name().toLowerCase(), RegistryPaymethodJSON.toJSON(rpm));
                }

                if (RegistryAdditionalInfo.MEDIA.equals(rai)) {
                    RegistryMediaFilter filter = f -> f.getRegistryProperty().eq(registryId);
                    object.put(rai.name().toLowerCase(),
                            RegistryMediaJSON.toJSON(AON.getStream(api.getDomain(), api.getUser(), filter)));
                }

                if (RegistryAdditionalInfo.RECORD_DATA.equals(rai)) {
                    object.put(rai.name().toLowerCase(),
                            RecordDataJSON.toJSON(
                                    AON.getRecordData(api.getDomain().getName(), api.getDomain().getId(),
                                            api.getUser().getLogin(),
                                            f -> f.getRegistryProperty().eq(registryId))));
                }

                if (RegistryAdditionalInfo.RSEGMENT.equals(rai)) {
                    RegistrySegmentFilter filter = f -> f.getRegistryProperty().eq(registryId);
                    object.put(rai.name().toLowerCase(),
                            RegistrySegmentJSON
                                    .toJSON(AON.getRegistrySegmentStream(api.getDomain(), api.getUser(), filter)));
                }
            });
        }
        return object;
    }

    public static JSONObject saveRegistry(AonApiData api) {
        Registry registry = RegistryJSON.fromJSON(api.getData());
        if (AonDocumentUtil.isValid(registry.getDocument())) {
            registry.setLegalPerson(AonDocumentUtil.isValidCIF(registry.getDocument()));
        }
        registry = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), registry);
        saveRegistryAdditionalInfo(api, registry.getId(), registry.getDomain().getId());
        return new JSONObject();
    }

    public static void saveRegistryAdditionalInfo(AonApiData api, Integer registryId, Integer registryDomain) {
        JSONObject json = api.getData();
        if (json.opt(RegistryAdditionalInfo.ADDRESSES.name().toLowerCase()) != null) {
            JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.ADDRESSES.name().toLowerCase());
            RegistryAddressJSON.fromJSON(arr).stream().forEach(address -> {
                address.setDomain(registryDomain);
                if (address.getRegistry() == null)
                    address.setRegistry(registryId);
                AON.save(api.getDomain(), api.getUser().getLogin(), address);
            });
        }

        if (json.opt(RegistryAdditionalInfo.MEDIA.name().toLowerCase()) != null) {
            JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.MEDIA.name().toLowerCase());
            RegistryMediaJSON.fromJSON(arr).stream().forEach(media -> {
                media.setDomain(registryDomain);
                if (media.getRegistry() == null)
                    media.setRegistry(registryId);
                if (!AonStringUtils.isBlank(media.getValue()))
                    AON.save(api.getDomain(), api.getUser().getLogin(), media);
            });
        }

        if (json.opt(RegistryAdditionalInfo.BANKS.name().toLowerCase()) != null) {
            JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.BANKS.name().toLowerCase());
            RegistryBankJSON.fromJSON(arr).stream().forEach(bank -> {
                bank.setDomain(registryDomain);
                if (bank.getRegistry() == null)
                    bank.setRegistry(registryId);
                AON.saveRegistryBank(api.getDomain(), api.getUser().getLogin(), bank);
            });
        }

        if (json.opt(RegistryAdditionalInfo.PAYMETHOD.name().toLowerCase()) != null) {
            RegistryPayMethod rpaymethod = RegistryPaymethodJSON
                    .fromJSON(json.optJSONObject(RegistryAdditionalInfo.PAYMETHOD.name().toLowerCase()));
            rpaymethod.setDomain(registryDomain);
            if (rpaymethod.getRegistry() == null)
                rpaymethod.setRegistry(registryId);
            if (rpaymethod.getPayMethod() != null)
                AON.saveRegistryPayMethod(api.getDomain(), api.getUser(), rpaymethod);
        }

        if (json.opt(RegistryAdditionalInfo.RECORD_DATA.name().toLowerCase()) != null) {
            RecordData recordData = RecordDataJSON
                    .fromJSON(json.optJSONObject(RegistryAdditionalInfo.RECORD_DATA.name().toLowerCase()));
            recordData.setDomain(registryDomain);
            if (recordData.getRegistry() == null)
                recordData.setRegistry(registryId);
            AON.saveRecordData(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
                    recordData);
        }

        if (json.opt(RegistryAdditionalInfo.RSEGMENT.name().toLowerCase()) != null) {
            JSONArray arr = json.optJSONArray(RegistryAdditionalInfo.RSEGMENT.name().toLowerCase());
            RegistrySegmentJSON.fromJSON(arr)
                    .stream()
                    .filter(s -> !s.getSegment().isEmpty())
                    .forEach(rsegment -> {
                        if (rsegment.getRegistry() == null)
                            rsegment.setRegistry(registryId);
                        AON.saveRegistrySegment(rsegment.getDomain(), api.getUser(), rsegment);
                    });
        }
    }

    private static Filter registryFilter(AonApiData api, RegistryProperties f) {
        Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
        // .and(f.getStatusProperty().eq(RegistryStatus.ACTIVE.value()));

        if (api.getData().opt(IJsonNames.REGISTRY) != null) {
            filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY)));
        } else if (api.getData().opt(IJsonNames.ID) != null) {
            filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(api.getData(), IJsonNames.ID)));
        }

        if (api.getData().opt(IJsonNames.DOCUMENT) != null) {
            filter = filter.and(f.getDocumentProperty().eq(JsonUtils.getString(api.getData(), IJsonNames.DOCUMENT)));
        }

        if (api.getData().opt(IJsonNames.VALUE) != null) {
            String value = api.getData().optString(IJsonNames.VALUE);
            Filter valueFilter = f.getNameProperty().like("%" + value + "%")
                    .or(f.getDocumentProperty().like("%" + value + "%"))
                    .or(f.getAliasProperty().like("%" + value + "%"));
            filter = filter.and(valueFilter);
        }
        return filter;
    }

}
