package net.aonsolutions.aon.api.servlet.warehouse;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SERES;
import com.esferalia.aon.occam.api.SERFRUIT;
import com.esferalia.aon.occam.api.json.CarrierPackingJSON;
import com.esferalia.aon.occam.api.json.DeliveryJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.SerfruitDeliveryPackagingJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.seres.IEdiSupport;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.SerfruitDeliveryPackaging;
import com.esferalia.aon.seres.DeliveryUpload;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.error.AonApiNotFoundException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDeliveriesServlet", urlPatterns = {
		"/ms/api/deliveries/*", 
		"/ms/api/ingenet/deliveries/*", 
		"/ms/api/serfruit/deliveries/*"})
public class DeliveriesServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DeliveriesServlet.class.getName());

	private static final String INGENET_REQUEST_URI = "/ms/api/ingenet/deliveries";
	private static final String SERFRUIT_REQUEST_URI = "/ms/api/serfruit/deliveries";
	
	public static final String DELIVERIES = "/";
	public static final String DELIVERY = "/:id";
	
	private boolean isIngenetRequestUri(HttpServletRequest req) {
		return req.getRequestURI().contains(INGENET_REQUEST_URI);
	}

	private boolean isSerfruitRequestUri(HttpServletRequest req) {
		return req.getRequestURI().contains(SERFRUIT_REQUEST_URI);
	}
	
	private boolean isUdapaRequestUri(HttpServletRequest req) {
		return isIngenetRequestUri(req) || isSerfruitRequestUri(req);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		if(isUdapaRequestUri(req)) {
			error(req, resp, new AonApiException(AonApiError.UNAUTHORIZED.getMessage()));
		} else put(req, resp);
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		if(isIngenetRequestUri(req)) {
			error(req, resp, new AonApiException(AonApiError.UNAUTHORIZED.getMessage()));
		} else if(isSerfruitRequestUri(req)) {
			putSerfruit(req, resp);
		} else put(req, resp);
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		if(isUdapaRequestUri(req)) {
			error(req, resp, new AonApiException(AonApiError.UNAUTHORIZED.getMessage()));
		} else delete(req, resp);
	}
	
	// GET 
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(DELIVERIES, DeliveriesServlet::getDeliveries)
                    .addRoute(DELIVERY, DeliveriesServlet::getDelivery)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
	
	private static JSONArray getDeliveries(AonApiData api) {
		return DeliveryJSON.toJSON(
			AON.getDeliveryStream(api.getDomain(), api.getUser(), f -> deliveryFilter(api, f), options(api)));
	}
	
	private static JSONObject getDelivery(AonApiData api) {
		Integer id = deliveryId(api, true);
		Delivery delivery = AON.getDelivery(api.getDomain(), api.getUser(),
				f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getIdProperty().eq(id)), options(api));
		if(delivery == null || delivery.getId() == null)
			throw new AonApiNotFoundException("No existe el albarán con identificador " + id + ".");
		return DeliveryJSON.toJSON(delivery);
	}
	
	private static Filter deliveryFilter(AonApiData api, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
	
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		if(id != null) {
			filter = filter.and(f.getIdProperty().eq(id));
		}

		String value = JsonUtils.getString(api.getData(), IJsonNames.VALUE);
		if(AonStringUtils.isNotBlank(value)) {
			Filter valueFilter = f.getRegistryNameProperty().like("%" + value + "%");
			if(AonNumberUtils.isInteger(value))
				valueFilter = valueFilter.or(f.getNumberProperty().like(AonNumberUtils.toInteger(value)));
			filter = filter.and(valueFilter);
		}
		
		String series = JsonUtils.getString(api.getData(), IJsonNames.SERIES);
		if(!AonStringUtils.isBlank(series)) {
			filter = filter.and(f.getSeriesProperty().eq(series));
		}
		
		Integer number = JsonUtils.getInteger(api.getData(), IJsonNames.NUMBER);
		if(number != null) {
			filter = filter.and(f.getNumberProperty().eq(number));
		}
		
		DeliveryStatus status = DeliveryStatus.safeValueOf(JsonUtils.getString(api.getData(), IJsonNames.STATUS));
		if(status != null) {
			filter = filter.and(f.getStatusProperty().eq(status.value()));
		}
	 	
		Date from = JsonUtils.getDate(api.getData(), IJsonNames.FROM);
		if(from != null) {
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(from)));
		}
		
		Date to = JsonUtils.getDate(api.getData(), IJsonNames.TO);
		if(to != null) {
			filter = filter.and(f.getIssueTimeProperty().le(AonDateUtils.toTimestamp(to)));
		}
		
		Date fromCreation = JsonUtils.getDate(api.getData(), IJsonNames.FROM_CREATION);
		if(fromCreation != null) {
			filter = filter.and(f.getCreationDateProperty().ge(AonDateUtils.toTimestamp(fromCreation)));
		}
		
		Date toCreation = JsonUtils.getDate(api.getData(), IJsonNames.TO_CREATION);
		if(toCreation != null) {
			filter = filter.and(f.getCreationDateProperty().le(AonDateUtils.toTimestamp(toCreation)));
		}
		
		Date fromModification = JsonUtils.getDate(api.getData(), IJsonNames.FROM_MODIFICATION);
		if(fromModification != null) {
			filter = filter.and(f.getModificationDateProperty().ge(AonDateUtils.toTimestamp(fromModification)));
		}
		
		Date toModification = JsonUtils.getDate(api.getData(), IJsonNames.TO_MODIFICATION);
		if(toModification != null) {
			filter = filter.and(f.getModificationDateProperty().le(AonDateUtils.toTimestamp(toModification)));
		}
		
		Integer customer = JsonUtils.getInteger(api.getData(), IJsonNames.CUSTOMER);
		if(customer != null) {
			filter = filter.and(f.getCustomerProperty().eq(customer));
		}

		Integer carrierPacking = JsonUtils.getInteger(api.getData(), IJsonNames.CARRIER_PACKING);
		if(carrierPacking != null) {
			filter = filter.and(f.getCarrierPackingProperty().eq(carrierPacking));
		}
		
		return filter;
	}
	
	// POST & PUT
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(DELIVERIES, DeliveriesServlet::saveDelivery)
                    .addRoute(DELIVERY, DeliveriesServlet::updateDelivery)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
	}
	
	private static JSONObject saveDelivery(AonApiData api) {
		Delivery delivery = DeliveryJSON.fromJSON(api.getData());
		delivery = AON.saveDelivery(api.getDomain(), api.getUser(), delivery);
		return DeliveryJSON.toJSON(delivery);
	}

	private static JSONObject updateDelivery(AonApiData api) {
		deliveryId(api, false); // valida el id de la ruta antes de guardar
		return saveDelivery(api);
	}
	
	// DELETE
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(DELIVERY, DeliveriesServlet::deleteDelivery)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }		
	}
	
	private static JSONObject deleteDelivery(AonApiData api) {
		Integer id = deliveryId(api, false);
		AON.deleteDelivery(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}

	// UTILS

	/**
	 * Identificador del albarï¿½n de la ruta '/:id'.
	 * Comprueba que tambiï¿½n viene en los datos de la peticiï¿½n y que ambos coinciden.
	 */
	private static Integer deliveryId(AonApiData api, boolean get) {
		Integer pathId = JsonUtils.getInteger(JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES), IJsonNames.ID);
		if(pathId == null)
			throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());

		Integer dataId = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		if(dataId == null && get)
			return pathId;
		
		if(dataId == null && !get)
			throw new AonApiException("No se ha indicado el identificador del albarï¿½n.");
		
		if(!pathId.equals(dataId))
			throw new AonApiException("El identificador del albarï¿½n de la ruta (" + pathId
					+ ") no coincide con el de los datos (" + dataId + ").");

		return pathId;
	}
	
	// SERFRUIT POST & SEND TO SERES
	
	private void putSerfruit(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
            AonApiData api = initialize(req);
            Object object = new AonRouting(api)
                    .addRoute(DELIVERIES, DeliveriesServlet::saveSerfruitDelivery)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
	}
	
	private static JSONObject saveSerfruitDelivery(AonApiData api) {
		Delivery delivery = DeliveryJSON.fromJSON(api.getData());
		boolean serfruit = JsonUtils.getboolean(api.getData(), IJsonNames.SERFRUIT);
		if(serfruit) {
			delivery = SERFRUIT.saveDelivery(api.getDomain(), api.getUser(), delivery);
		} else delivery = AON.saveDelivery(api.getDomain(), api.getUser(), delivery);
			
		if(JsonUtils.has(api.getData(), IJsonNames.PACKAGING)) {
			List<SerfruitDeliveryPackaging> list =  SerfruitDeliveryPackagingJSON.fromJSON(JsonUtils.getJSONArray(api.getData(), IJsonNames.PACKAGING));
			SERFRUIT.saveDeliveryPackaging(api.getDomain(), api.getUser()
					, delivery, list);
		}
		
		if(JsonUtils.has(api.getData(), IJsonNames.CARRIER_PACKING)) {
			CarrierPacking carrierPacking = CarrierPackingJSON.fromJSON(JsonUtils.getJSONObject(api.getData(), IJsonNames.CARRIER_PACKING));
			SERFRUIT.saveCarrierPacking(api.getDomain(), api.getUser(), delivery, carrierPacking);
		}
		
		if(serfruit) {
			try {
				seres(api, delivery);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return DeliveryJSON.toJSON(delivery);
	}
	
	// ENVIAR ALBARï¿½N A SERES...

	private static void seres(AonApiData api, Delivery delivery) {
		Delivery d = AON.getDelivery(api.getDomain(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(delivery.getDomain()).and(f.getIdProperty().eq(delivery.getId())),
				new Options().setFull(true));

		RegistryNote rNote = AON.getRegistryNote(api.getDomain(), api.getUser().getLogin(),
				f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
						.and(f.getRegistryProperty().eq(d.getCustomer().getId()))
						.and(f.getDescriptionProperty().eq(IEdiSupport.SERES_AUTO_COMMIT_DELIVERY)));
 
		boolean autoSendDelivery = rNote != null && rNote.getComments() != null
				&& rNote.getComments().trim().equalsIgnoreCase("true");
		if (autoSendDelivery) {
			SeresInfo info = SERES.getSeresInfo(api.getDomain(), api.getUser(), d);
			DeliveryUpload du = new DeliveryUpload(api.getDomain(), api.getUser().getLogin(), info);
			d.setEdiCodes(info.getEdiCodes());

			Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getId())
							.and(f.getSourceTypeProperty().eq(DataAttachSource.DELIVERY.value()))
							.and(f.getSourceBatchProperty().eq(d.getId())),
					AttachType.DATA, true);
			d.setPackagingData(attach.getData());
			du.uploadDelivery(d);
		}
	}

}
