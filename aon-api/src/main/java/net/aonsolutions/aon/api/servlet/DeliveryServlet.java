package net.aonsolutions.aon.api.servlet;
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
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.seres.EdiCodes;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.SerfruitDeliveryPackaging;
import com.esferalia.aon.seres.DeliveryUpload;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDeliveryServlet", urlPatterns = {"/ms/api/delivery/*"})
public class DeliveryServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(DeliveryServlet.class.getName());
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getDeliveries(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveDelivery(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteDelivery(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getDeliveries(AonApiData api) {
		return DeliveryJSON.toJSON(
			AON.getDeliveryStream(api.getDomain(), api.getUser(), f -> deliveryFilter(api, f)));
	}
	
	private JSONObject saveDelivery(AonApiData api) {
//		String a ="{\"address\":{\"address\":\"PLATAFORMA LOGISTICA VITORIA-GASTEIZ\",\"address2\":\" \",\"alias\":null,\"city\":\"VITORIA-GASTEIZ\",\"country\":\"\",\"dirty\":false,\"domain\":3049,\"fullAddress\":null,\"global\":false,\"id\":8301329,\"main\":false,\"number\":\"\",\"postal_code\":null,\"province\":\"\",\"registry\":466879,\"removed\":false,\"streetType\":\"XX\",\"zip\":\"01015\"},\"bankAccount\":\"\",\"bankAlias\":\"\",\"bic\":\"\",\"carrier\":{\"alias\":\"\",\"confidential\":false,\"dirty\":false,\"document\":\"\",\"documentCountry\":\"\",\"documentType\":\"\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":0,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"global\":false,\"id\":0,\"legalPerson\":false,\"name\":\"\",\"nationality\":\"\",\"scope\":\"\",\"status\":\"\"},\"packaging\":[{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016176805\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":72,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":72}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016176775\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":72,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":72}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177277\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":52,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":52}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016176768\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":72,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":72}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016176782\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":72,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":72}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016176799\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38480000690662\",\"name\":\"MIRALOBUENO GRANEL 8 KG CAJA V/G\"},\"quantity\":72,\"deliveryLine\":1}],\"product\":{\"code\":\"9\",\"name\":\"Logifruit 612\"},\"quantity\":72}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177260\",\"content\":[{\"content\":[{\"product\":{\"code\":\"28424273000561\",\"name\":\"MIRALOBUENO ROJA 2 KG CAJA VITORIA\"},\"quantity\":5,\"deliveryLine\":2}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":30}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177031\",\"content\":[{\"content\":[{\"product\":{\"code\":\"28424273000561\",\"name\":\"MIRALOBUENO ROJA 2 KG CAJA VITORIA\"},\"quantity\":48,\"deliveryLine\":2}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":288}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176447\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176515\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176522\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176492\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176539\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176423\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176553\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176430\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176546\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016176508\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":32,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":128}]},{\"product\":{\"code\":\"3\",\"name\":\"Logifruit 41\"},\"quantity\":1,\"sscc\":\"084242730016177109\",\"content\":[{\"content\":[{\"product\":{\"code\":\"58424273000524\",\"name\":\"HACENDADO FRESH 5KG\"},\"quantity\":12,\"deliveryLine\":3}],\"product\":{\"code\":\"11\",\"name\":\"Logifruit 624\"},\"quantity\":48}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177185\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177161\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177154\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177147\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177123\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177611\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":44,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":176}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016178120\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016178113\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177178\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177192\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177222\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177215\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177239\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]},{\"product\":{\"code\":\"2\",\"name\":\"Logifruit 81\"},\"quantity\":1,\"sscc\":\"084242730016177246\",\"content\":[{\"content\":[{\"product\":{\"code\":\"38424273000544\",\"name\":\"HACENDADO FRESH 3KG MERCADONA\"},\"quantity\":48,\"deliveryLine\":4}],\"product\":{\"code\":\"10\",\"name\":\"Logifruit 618\"},\"quantity\":192}]}],\"carrierPacking\":{\"date\":\"04/07/2023\",\"carrier\":{\"document\":\"73\",\"alias\":\"ACOTRAL S.A. A29094679 PG. ANTEQUERA C/CUEVA VIERA ANTEQUERA MALAGA 29400\",\"name\":\"TXEMA MARQUEZ DNI: 16280070A\"},\"carrierReference\":\"ACOTRAL S.A. A29094679 PG. ANTEQUERA C/CUEVA VIERA ANTEQUERA MALAGA 29400\",\"numberPlate\":\"5954LWX\",\"driverName\":\"TXEMA MARQUEZ DNI: 16280070A\",\"driverDocument\":\"73\"},\"comments\":\"\",\"confidential\":false,\"customer\":{\"account\":2278762,\"alias\":\"\",\"confidential\":false,\"creation_date\":0,\"creation_user\":null,\"deliveryGrouped\":false,\"deliveryValuated\":false,\"dirty\":false,\"document\":\"A46103834\",\"documentCountry\":\"ES\",\"documentType\":\"CIF\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":3049,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"eInvoice\":false,\"global\":false,\"id\":466879,\"invoicingGroup\":0,\"isRelationship\":false,\"legalPerson\":true,\"modification_date\":0.0,\"modification_user\":null,\"name\":\"MERCADONA, SA\",\"nationality\":\"ES\",\"projectGrouped\":false,\"scope\":null,\"status\":\"ACTIVE\",\"surcharge\":false,\"tariff\":0,\"transaction\":\"NAC\",\"withholding\":false},\"date\":\"2023-07-03T00:00:00\",\"daysBetweenPymnts\":0,\"daysToFirstPymnt\":0,\"details\":[{\"description\":\"69066 PATATA MIRALOBUENO 8KG. CAJA\",\"discount\":\"0.0\",\"domain\":3049,\"item\":{\"barcode\":\"38480000690662\",\"code\":null,\"creation_date\":null,\"creation_user\":null,\"description\":\"\",\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":3049,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"expensesFixed\":0.0,\"expensesPercent\":0.0,\"id\":1999487,\"internet\":false,\"itemComposition\":null,\"modification_date\":null,\"modification_user\":null,\"name\":null,\"packFormatTag\":2531,\"packMeasurement\":8.0,\"packMeasurementTag\":2533,\"packUnits\":1,\"packUnitsTag\":2531,\"price\":0,\"product\":{\"brand\":{\"domain\":0,\"id\":0,\"name\":\"\"},\"category\":{\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":3049,\"id\":11224,\"name\":\"PATATA \"},\"code\":\"08MLN082\",\"composition\":false,\"compositionPrice\":false,\"creation_date\":null,\"creation_user\":null,\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":0,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"id\":1999572,\"inventoriable\":true,\"kind\":\"SALE\",\"lotable\":true,\"manufactured\":true,\"modification_date\":null,\"modification_user\":null,\"name\":\"69066 PATATA MIRALOBUENO 8KG. CAJA\",\"packaged\":true,\"purchaseAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":0,\"level\":0},\"retention\":0.0,\"salesAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":2282422,\"level\":0},\"serializable\":true,\"status\":\"ACTIVE\",\"type\":\"COMMERCIAL_PRODUCT\",\"vat\":0.0},\"profitPercent\":0.0,\"purchasePrice\":0.0,\"removed\":false,\"serialDate\":\"04/07/2023 0:00:00\",\"serialNumber\":\"F230704005\",\"status\":\"ACTIVE\",\"stockUnitTag\":2533},\"line\":1,\"price\":1.25,\"purchaseReference\":\"04551666\",\"quantity\":412.0,\"salesDetail\":1830828,\"source\":\"ESPAÑA\",\"category\":\"CATL1\",\"quality\":\"CATEGORIA I\"},{\"description\":\"69098 PATATA MIRALOBUENO ROJA 2KG\",\"discount\":\"0.0\",\"domain\":3049,\"item\":{\"barcode\":\"28424273000561\",\"code\":null,\"creation_date\":null,\"creation_user\":null,\"description\":\"\",\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":3049,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"expensesFixed\":0.0,\"expensesPercent\":0.0,\"id\":1999483,\"internet\":false,\"itemComposition\":null,\"modification_date\":null,\"modification_user\":null,\"name\":null,\"packFormatTag\":2531,\"packMeasurement\":2.0,\"packMeasurementTag\":2533,\"packUnits\":6,\"packUnitsTag\":2532,\"price\":0,\"product\":{\"brand\":{\"domain\":0,\"id\":0,\"name\":\"\"},\"category\":{\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":3049,\"id\":11224,\"name\":\"PATATA \"},\"code\":\"08MLV022\",\"composition\":false,\"compositionPrice\":false,\"creation_date\":null,\"creation_user\":null,\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":0,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"id\":1999568,\"inventoriable\":true,\"kind\":\"SALE\",\"lotable\":true,\"manufactured\":true,\"modification_date\":null,\"modification_user\":null,\"name\":\"69098 PATATA MIRALOBUENO ROJA 2KG\",\"packaged\":true,\"purchaseAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":0,\"level\":0},\"retention\":0.0,\"salesAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":2282422,\"level\":0},\"serializable\":true,\"status\":\"ACTIVE\",\"type\":\"COMMERCIAL_PRODUCT\",\"vat\":0.0},\"profitPercent\":0.0,\"purchasePrice\":0.0,\"removed\":false,\"serialDate\":\"04/07/2023 0:00:00\",\"serialNumber\":\"F230704013\",\"status\":\"ACTIVE\",\"stockUnitTag\":2533},\"line\":2,\"price\":1.375,\"purchaseReference\":\"04551666\",\"quantity\":53.0,\"salesDetail\":1830829,\"source\":\"ESPAÑA\",\"category\":\"CATL1\",\"quality\":\"CATEGORIA I\"},{\"description\":\"69099 PATATA HACENDADO FRESH 5KG. \",\"discount\":\"0.0\",\"domain\":3049,\"item\":{\"barcode\":\"58424273000524\",\"code\":null,\"creation_date\":null,\"creation_user\":null,\"description\":\"\",\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":3049,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"expensesFixed\":0.0,\"expensesPercent\":0.0,\"id\":1999480,\"internet\":false,\"itemComposition\":null,\"modification_date\":null,\"modification_user\":null,\"name\":null,\"packFormatTag\":2531,\"packMeasurement\":5.0,\"packMeasurementTag\":2533,\"packUnits\":4,\"packUnitsTag\":2532,\"price\":0,\"product\":{\"brand\":{\"domain\":0,\"id\":0,\"name\":\"\"},\"category\":{\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":3049,\"id\":11224,\"name\":\"PATATA \"},\"code\":\"08MLV052\",\"composition\":false,\"compositionPrice\":false,\"creation_date\":null,\"creation_user\":null,\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":0,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"id\":1999565,\"inventoriable\":true,\"kind\":\"SALE\",\"lotable\":true,\"manufactured\":true,\"modification_date\":null,\"modification_user\":null,\"name\":\"69099 PATATA MIRALOBUENO 5KG. \",\"packaged\":true,\"purchaseAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":0,\"level\":0},\"retention\":0.0,\"salesAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":2282422,\"level\":0},\"serializable\":true,\"status\":\"ACTIVE\",\"type\":\"COMMERCIAL_PRODUCT\",\"vat\":0.0},\"profitPercent\":0.0,\"purchasePrice\":0.0,\"removed\":false,\"serialDate\":\"04/07/2023 0:00:00\",\"serialNumber\":\"F230704002\",\"status\":\"ACTIVE\",\"stockUnitTag\":2533},\"line\":3,\"price\":1.05,\"purchaseReference\":\"04551666\",\"quantity\":332.0,\"salesDetail\":1830830,\"source\":\"ESPAÑA\",\"category\":\"CATL1\",\"quality\":\"CATEGORIA I\"},{\"description\":\"69166 PATATA HACENDADO FRESH 3KG. \",\"discount\":\"0.0\",\"domain\":3049,\"item\":{\"barcode\":\"38424273000544\",\"code\":null,\"creation_date\":null,\"creation_user\":null,\"description\":\"\",\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":3049,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"expensesFixed\":0.0,\"expensesPercent\":0.0,\"id\":1999478,\"internet\":false,\"itemComposition\":null,\"modification_date\":null,\"modification_user\":null,\"name\":null,\"packFormatTag\":2531,\"packMeasurement\":3.0,\"packMeasurementTag\":2533,\"packUnits\":4,\"packUnitsTag\":2532,\"price\":0,\"product\":{\"brand\":{\"domain\":0,\"id\":0,\"name\":\"\"},\"category\":{\"detail\":\"\",\"detail2\":\"\",\"detail3\":\"\",\"domain\":3049,\"id\":11224,\"name\":\"PATATA \"},\"code\":\"08MLV032\",\"composition\":false,\"compositionPrice\":false,\"creation_date\":null,\"creation_user\":null,\"domain\":{\"active\":false,\"definedUsers\":0,\"description\":\"\",\"disableDomainManagement\":false,\"domainManagement\":false,\"domainType\":0,\"enableHeredity\":false,\"id\":0,\"maxDefinedUsers\":0,\"name\":\"\",\"owner\":\"\",\"parentId\":0,\"scope\":0},\"id\":1999563,\"inventoriable\":true,\"kind\":\"SALE\",\"lotable\":true,\"manufactured\":true,\"modification_date\":null,\"modification_user\":null,\"name\":\"69166 PATATA MIRALOBUENO 3KG. \",\"packaged\":true,\"purchaseAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":0,\"level\":0},\"retention\":0.0,\"salesAccount\":{\"active\":false,\"alias\":\"\",\"code\":\"\",\"costCenter\":\"\",\"description\":\"\",\"domain\":0,\"entryEnabled\":false,\"id\":2282422,\"level\":0},\"serializable\":true,\"status\":\"ACTIVE\",\"type\":\"COMMERCIAL_PRODUCT\",\"vat\":0.0},\"profitPercent\":0.0,\"purchasePrice\":0.0,\"removed\":false,\"serialDate\":\"04/07/2023 0:00:00\",\"serialNumber\":\"F230704008\",\"status\":\"ACTIVE\",\"stockUnitTag\":2533},\"line\":4,\"price\":1.1,\"purchaseReference\":\"04551666\",\"quantity\":668.0,\"salesDetail\":1830831,\"source\":\"ESPAÑA\",\"category\":\"CATL1\",\"quality\":\"CATEGORIA I\"}],\"domain\":3049,\"driver\":\"\",\"driverDocument\":\"\",\"number\":704001,\"numberOfPymnts\":1,\"numberPlate\":\"\",\"pymntDays\":0,\"remarks\":\"RUEGO CONFIRMEN LA FECHA DE ENTREGA ASI COMO LOS ARTICULOS Y CANTIDADE, S SOLICITADAS.\\n\",\"scope\":{\"description\":\"GENERAL\",\"domain\":3049,\"id\":1492,\"name\":null},\"serfruit\":true,\"series\":\"S23\",\"shippingAlternativeAddress\":\"\",\"shippingAlternativeAddress2\":\"\",\"shippingAlternativeCity\":\"\",\"shippingAlternativePhone\":\"\",\"shippingAlternativeRecipient\":\"\",\"shippingAlternativeZip\":\"\",\"shippingContact\":\"\",\"shippingPeriod\":\"\",\"shippingStatus\":\"\",\"status\":\"PENDING\",\"statusModificationDate\":\"05/07/2023 10:59:30\",\"totalPackages\":4730.0,\"totalWeight\":18588.0,\"workplace\":{\"active\":true,\"address\":\"395728\",\"customer\":0,\"description\":\"PADULETA,1 \",\"domain\":3049,\"economicAgreement\":0,\"enterprise\":0,\"id\":3051,\"scope\":1492}}";
//		a = a.replaceAll("\"", "'");
//		JSONObject json = new JSONObject(a);
//		api.setData(json);

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
	
	private JSONObject deleteDelivery(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteSales(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private Filter deliveryFilter(AonApiData api, DeliveryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
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
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toTimestamp(to)));
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
	
	// ENVIAR ALBARÁN A SERES...
	
	private void seres(AonApiData api, Delivery delivery) {
		System.out.println("SEND DELIVERY TO SERES");
		RegistryNote rNote = AON.getRegistryNote(api.getDomain(), api.getUser().getLogin(), f -> 
			f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
			.and(f.getRegistryProperty().eq(delivery.getCustomer().getId()))
			.and(f.getDescriptionProperty().eq("SERES_AUTO_COMMIT_DELIVERY")));
		
		boolean autoSendDelivery = rNote!=null && Boolean.getBoolean(rNote.getComments());
		if(autoSendDelivery){
			System.out.println("SEND DELIVERY TO SERES IS TRUE");
			SeresInfo info = SERES.getSeresInfo(api.getDomain(), api.getUser());
			DeliveryUpload du = new DeliveryUpload(api.getDomain(), api.getUser().getLogin(), info);
			EdiCodes codes = SERES.getEdiCodes(api.getDomain(), api.getUser(), delivery);
			delivery.setEdiCodes(codes);
			
			Attach attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getSourceTypeProperty().eq(DataAttachSource.DELIVERY.value()))
					.and(f.getSourceBatchProperty().eq(delivery.getId()))
				, AttachType.DATA, true);
			delivery.setPackagingData(attach.getData());
			du.uploadDelivery(delivery);
		} else System.out.println("SEND DELIVERY TO SERES IS FALSE");

	}
	
}

