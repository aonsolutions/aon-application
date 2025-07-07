package solutions.aon.in.invoice.aws.lambda;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.AonInvofox;


public class InvofoxWebhookHandler implements RequestHandler<Object, String> {

	enum Type {

		ticket("Ticket"), invoice("Factura"), unknown("Desconocido"), deliveryNote("Albrán");

		private String decription;

		private Type(String description) {
			this.decription = description;
		}

		public String getDescription() {
			return decription;
		}
	}

	enum State {
		unknown("Desconocido", "gray"), approved("Aprobada", "green"), discarded("Descartada", "gray"),
		pendingCorrection("Pte.correción", "orange"), processing("Procesando", "gray"),
		pendingDecission("Pte.decisión", "orange"), rejected("Rechazado", "red"), exported("Exportada", "blue"),
		error("Error", "red");

		private String color;
		private String decription;

		private State(String description, String color) {
			this.color = color;
			this.decription = description;
		}

		public String getColor() {
			return color;
		}

		public String getDescription() {
			return decription;
		}

	}

	enum Confidence {
		low("red"), high("green"), medium("orange"), unknown("gray");

		private String color;

		private Confidence(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}

	}

	enum Severity {
		low("orange"), high("red"), medium("orange"), unknown("gray");

		private String color;

		private Severity(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}

	}

	public String documentFinished(Object input, Context context) {
		try {
			JSONObject body = getBody(input);
			JSONObject data = body.getJSONObject("data");
			return documentFinished(data);
		} catch (InterruptedException e) {
			/* Clean up whatever needs to be handled before interrupting */
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String handleRequest(Object input, Context context) {

		try {
			JSONObject body = getBody(input);
			JSONObject data = body.getJSONObject("data");
			String type = body.getString("type");
			switch (type) {
			case "batch.finished":
				return data.toString(1);
			case "document.approved", "document.processed":
				return documentFinished(data);
			default:
				return type;
			}
		} catch (InterruptedException e) {
			/* Clean up whatever needs to be handled before interrupting */
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}


	private String documentFinished(JSONObject data) throws URISyntaxException, IOException, InterruptedException {
		String id = get(data, "_id");
		String publicState = get(data, "publicState");
		String userLogin = get(data, "clientData/loadS3/user");
		String domainName = get(data, "clientData/loadS3/domain");


		JSONObject json = new JSONObject();
		try {
			State state = valueOf(publicState, State.unknown);

			if (publicState != null && (State.pendingCorrection.equals(state) || 
					State.discarded.equals(state) || State.pendingDecission.equals(state) || 
					State.rejected.equals(state) || State.approved.equals(state))) {
				json = AonInvofox.rawdocInvofoxInvoice(domainName, userLogin, id);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return json.toString(1);
	}


	static String getFieldDescription(String fieldName) {
		Map<String, String> fieldNamesDescription = getFieldsDescriptionsMap();
		return fieldNamesDescription.getOrDefault(fieldName, fieldName);

	}

	/**
	 * @return
	 */
	static Map<String, String> getFieldsDescriptionsMap() {
		Map<String, String> fieldNamesDescriptionsMap = new HashMap<String, String>();
		fieldNamesDescriptionsMap.put("currency", "Divisa");
		fieldNamesDescriptionsMap.put("language", "Idioma");
		fieldNamesDescriptionsMap.put("isCreditNote", "isCreditNote");
		fieldNamesDescriptionsMap.put("numberFormat", "numberFormat");
		fieldNamesDescriptionsMap.put("invoiceRef", "Núm. factura");
		fieldNamesDescriptionsMap.put("seriesCode", "Núm. serie");
		fieldNamesDescriptionsMap.put("taxClass", "Tipo impositivo");
		fieldNamesDescriptionsMap.put("issuerCountry", "País emisor");
		fieldNamesDescriptionsMap.put("recipientCountry", "País receptor");
		fieldNamesDescriptionsMap.put("issuerAddressDetails", "issuerAddressDetails");
		fieldNamesDescriptionsMap.put("recipientAddressDetails", "recipientAddressDetails");
		fieldNamesDescriptionsMap.put("documentNumber", "Núm.factura");
		fieldNamesDescriptionsMap.put("issueDate", "Fecha emisión");
		fieldNamesDescriptionsMap.put("issuerName", "Nombre emisor");
		fieldNamesDescriptionsMap.put("issuerTaxId", "CIF emisor");
		fieldNamesDescriptionsMap.put("issuerAddress", "Dirección emisor");
		fieldNamesDescriptionsMap.put("issuerEmail", "Email emisor");
		fieldNamesDescriptionsMap.put("issuerPhoneNumber", "Teléfono emisor");
		fieldNamesDescriptionsMap.put("issuerWebsite", "Web emisor");
		fieldNamesDescriptionsMap.put("recipientName", "Nombbre receptor");
		fieldNamesDescriptionsMap.put("recipientTaxId", "CIF receptor");
		fieldNamesDescriptionsMap.put("recipientAddress", "Dirección receptor");
		fieldNamesDescriptionsMap.put("shippingAddress", "Dirección envío receptor");
		fieldNamesDescriptionsMap.put("recipientEmail", "Email receptor");
		fieldNamesDescriptionsMap.put("recipientWebsite", "Web receptor");
		fieldNamesDescriptionsMap.put("recipientPhoneNumber", "Teléfono receptor");
		fieldNamesDescriptionsMap.put("paymentMethod", "Método de pago");
		fieldNamesDescriptionsMap.put("IBAN", "IBAN");
		fieldNamesDescriptionsMap.put("SWIFT", "SWIFT");
		fieldNamesDescriptionsMap.put("clientCode", "Núm. cliente");
		fieldNamesDescriptionsMap.put("deliveryNoteRef", "Núm. albarán");
		fieldNamesDescriptionsMap.put("orderRef", "Núm. pedido");
		fieldNamesDescriptionsMap.put("contractRef", "Contrato");
		fieldNamesDescriptionsMap.put("incoterms", "InCoTerms");
		fieldNamesDescriptionsMap.put("documentType", "Tipo de documento");
		fieldNamesDescriptionsMap.put("additionalNotes", "Notas adicionales");
		fieldNamesDescriptionsMap.put("legalNotes", "Texto legal");
		fieldNamesDescriptionsMap.put("totalTaxAmount", "Cuota");
		fieldNamesDescriptionsMap.put("totalTaxBaseAmount", "Base imponible");
		fieldNamesDescriptionsMap.put("totalAmount", "Total");
		fieldNamesDescriptionsMap.put("totalGrossAmount", "Total");
		fieldNamesDescriptionsMap.put("totalDueAmount", "Total a pagar");
		fieldNamesDescriptionsMap.put("withholdingTaxRate", "withholdingTaxRate");
		fieldNamesDescriptionsMap.put("withholdingTaxAmount", "Tasas");
		fieldNamesDescriptionsMap.put("totalFeesAmount", "totalFeesAmount");
		fieldNamesDescriptionsMap.put("totalDiscountAmount", "Descuento");
		fieldNamesDescriptionsMap.put("reimbursableExpensesAmount", "reimbursableExpensesAmount");
		fieldNamesDescriptionsMap.put("additionalChargesAmount", "Gastos adicionales");
		fieldNamesDescriptionsMap.put("additionalDiscountsAmount", "Descuentos adicionales");
		fieldNamesDescriptionsMap.put("serviceAddress", "serviceAddress");
		fieldNamesDescriptionsMap.put("supplyNumber", "supplyNumber");
		fieldNamesDescriptionsMap.put("meterNumber", "meterNumber");
		fieldNamesDescriptionsMap.put("totalUsage", "totalUsage");
		fieldNamesDescriptionsMap.put("usageUnitOfMeasurement", "usageUnitOfMeasurement");

		// lines/breakdows
		fieldNamesDescriptionsMap.put("taxRate", "Tipo (%)");
		fieldNamesDescriptionsMap.put("taxAmount", "Cuota");
		fieldNamesDescriptionsMap.put("taxBaseAmount", "Base imponible");

		return fieldNamesDescriptionsMap;
	}

	static String getSeverityColor(JSONArray errors) {
		List<String> severities = errors.toList().stream().map(error -> (String) ((Map<?, ?>) error).get("severity"))
				.toList();
		if (severities.contains("medium")) {
			return "orange";
		} else if (severities.contains("low")) {
			return "green";
		} else {
			return "green";
		}
	}

	static String format(String pattern, Map<String, String> params) {
		return params.keySet().stream().reduce(pattern,
				(str, name) -> str.replace("${" + name + "}", params.get(name)));
	}

	static JSONObject getBody(Object input) {
		Map<String, ?> map = (Map<String, ?>) input;
		return new JSONObject((String) map.get("body"));
	}

	static <T> T get(JSONObject jsonObject, String path) {
		String[] keys = path.split("/");
		for (int i = 0; i < (keys.length - 1); i++) {
			try {
				jsonObject = jsonObject.getJSONObject(keys[i]);
			} catch (JSONException e) {
				return null;
			} catch (Exception e) {
				System.out.println(jsonObject.toString(1));
				throw e;
			}
		}

		try {
			Object obj = jsonObject.get(keys[keys.length - 1]);
			return JSONObject.NULL.equals(obj) ? null : (T) obj;
		} catch (JSONException e) {
			return null;
		}

	}

	static <T> T getOrDefault(T t, T defaultValue) {
		return Optional.ofNullable(t).orElse(defaultValue);
	}

	static Date parse(String str) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(str);
		} catch (Exception e) {
			return null;
		}
	}

	static String format(Date date, String defaultValue) {
		return format(date, "dd/MM/yyyy' 'HH:mm", defaultValue);
	}

	static String format(Date date, String pattern, String defaultValue) {
		try {
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	static String format(Number number, String defaultValue) {
		if (number == null) {
			return defaultValue;
		}
		try {
			return NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(number.doubleValue());
		} catch (IllegalArgumentException e) {
			return defaultValue;
		}
	}

	static String getErrorDescription(JSONObject error) {

		switch (error.getString("code")) {
		case "ERR_FILE_TOO_BIG":
			return "El archivo subido es demasiado grande (máximo 50 MB)";
		case "ERR_TOO_MANY_PAGES":
			return "El documento tiene demasiadas páginas (máximo 10)";
		case "ERR_UNSUPPORTED_FILE_FORMAT":
			return "El form ato del documento no es compatible.";
		case "ERR_EMPTY_VALUE": {
			String fieldName = getFieldName(error);
			return String.format("El campo <b>%s</b> no tiene valor", getFieldDescription(fieldName));
		}
		case "ERR_INCORRECT_VALUE": {
			String fieldName = getFieldName(error);
			int line = getLine(error);
			return String.format("Valor no permitido para el campo <b>%s</b>", fieldName);
		}
		case "ERR_INVALID_FORMAT":
			return "El valor extraído tiene un formato no válido";
		case "ERR_LOW_CONFIDENCE":
			return "El valor extraído tiene poca confianza";
		case "ERR_HANDWRITTEN_DOC":
			return "El documento está escrito a mano.";
		case "ERR_INVALID_DOC_TYPE":
			return "Se desconoce el tipo de documento (no factura, ticket, etc.)";
		case "ERR_CROPPED_DOC":
			return "El documento está recortado, por lo que falta información.";
		case "ERR_MULTI_DOC_PAGE_FOUND":
			return "El archivo contiene varios documentos.";
		case "ERR_PAGES_MISSING":
			return "Al documento le faltan páginas, por lo tanto está incompleto.";
		case "ERR_BAD_QUALITY":
			return "El documento tiene mala calidad.";
		case "ERR_DUPLICATED_DOCUMENT":
			return "El documento está duplicado.";
		case "ERR_MISSING_INFO":
			return "Al documento le falta información requerida.";
		case "ERR_BREAKDOWN_AMOUNT_MISSMATCH":
			int line = getLine(error);
			return String.format("Las cantidades %s de los desgloses no son  correctas.",
					line != -1 ? "de la línea " + line : "");
		case "ERR_CLASSIFIER_DISCARD":
			return "Este documento no tiene un tipo válido.";
		case "WARN_CLASSIFIER_FORCED_DEFTYPE":
			String forced = valueOf(get(error, "forced"), Type.unknown).getDescription();
			String detected = valueOf(get(error, "detected"), Type.unknown).getDescription();
			return String.format("El clasificador detecto %s pero estableció el tipo por defecto %s.", detected,
					forced);

		case "ERR_OTHER":
			return "Error generico";
		default:
			return "Error desconocido '" + error.getString("code") + "'";
		}

	}

	public static int getLine(JSONObject error) {
		JSONArray fields = error.getJSONArray("fields");
		for (int i = 0; i < fields.length(); i++) {
			JSONObject field = fields.getJSONObject(i);
			String prefix = field.getString("prefix");
			if ("breakdowns".equalsIgnoreCase(prefix)) {
				int index = field.getInt("index");
				return index + 1;
			}
		}
		return -1;
	}

	public static String getFieldName(JSONObject error) {
		JSONArray fields = error.getJSONArray("fields");
		for (int i = 0; i < fields.length(); i++) {
			JSONObject field = fields.getJSONObject(i);
			if (field.has("name")) {
				String fieldName = field.getString("name");
				if (AonStringUtils.isNotBlank(fieldName)) {
					return fieldName;
				}
			}
		}
		return "";
	}

	public static <T extends Enum<T>> T valueOf(String name, T defaultValue) {
		try {
			return Enum.valueOf(defaultValue.getDeclaringClass(), name);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
