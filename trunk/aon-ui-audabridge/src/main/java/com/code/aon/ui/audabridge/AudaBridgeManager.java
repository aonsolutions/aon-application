package com.code.aon.ui.audabridge;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.digester.Digester;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.code.aon.audabridge.AudaBridge;
import com.code.aon.audabridge.GetDataInfoResult;
import com.code.aon.audabridge.IAudaBridgeService;
import com.code.aon.ui.audabridge.response.CalculationDataResponse;
import com.code.aon.ui.audabridge.response.Equipo;
import com.code.aon.ui.audabridge.response.Operacion;
import com.code.aon.ui.audabridge.response.Pieza;
import com.code.aon.ui.audabridge.response.Pintura;
import com.code.aon.ui.audabridge.response.TotalGeneral;

public class AudaBridgeManager {
	private static String ANALISYS_ERROR = "Imposible realizar el análisis de la respuesta AudaBridge";

	private static String CREATE_ASSESSMENT_REQUEST_KEY = "CreateAssessmentRequest";

	private static String TOKEN_REQUEST_KEY = "TokenRequest";
	private static String TOKEN_REQUEST = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + "<Message>" + "<Header>"
			+ "<MessageTypeIdentifier>TokenRequest</MessageTypeIdentifier>" + "</Header>" + "<Body>" + "<TokenRequest>"
			+ "<Wan>{0}</Wan>" + "</TokenRequest>" + "</Body>" + "</Message>";

	private static String WAN_REQUEST_KEY = "GetWanRequest";
	private static String WAN_REQUEST = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + "<Message>" + "<Header>"
			+ "<MessageTypeIdentifier>GetWanRequest</MessageTypeIdentifier>" + "</Header>" + "<Body>"
			+ "<GetWanRequest>" + "<CalculationNumber>{0}</CalculationNumber>" + "<ControlCode>{1}</ControlCode>"
			+ "</GetWanRequest>" + "</Body>" + "</Message>";

	private static String AUDIT_CALCULATION_REQUEST_KEY = "AuditCalculationRequest";
	private static String AUDIT_CALCULATION_REQUEST = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + "<Message>"
			+ "<Header>" + "<MessageTypeIdentifier>AuditCalculationRequest</MessageTypeIdentifier>" + "</Header>"
			+ "<Body>" + "<AuditCalculationRequest>" + "<Wan>{0}</Wan>" + "</AuditCalculationRequest>" + "</Body>"
			+ "</Message>";

	private static String CALCULATION_DATA_REQUEST_KEY = "CalculationDataRequest";
	private static String CALCULATION_DATA_REQUEST = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + "<Message>"
			+ "<Header>" + "<MessageTypeIdentifier>CalculationDataRequest</MessageTypeIdentifier>" + "</Header>"
			+ "<Body>" + "<CalculationDataRequest>" + "<Wan>{0}</Wan>" + "</CalculationDataRequest>" + "</Body>"
			+ "</Message>";
	private static String CALCULATION_REPORT_REQUEST_KEY = "CalculationReportRequest";
	private static String CALCULATION_REPORT_REQUEST = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + "<Message>"
			+ "<Header>" + "<MessageTypeIdentifier>CalculationReportRequest</MessageTypeIdentifier>" + "</Header>"
			+ "<Body>" + "<CalculationReportRequest>" + "<Wan>{0}</Wan>" + "</CalculationReportRequest>" + "</Body>"
			+ "</Message>";

	public InputStream getPDFRequest(String accessKey, String wan) throws AudaBridgeException {
		String msg = MessageFormat.format(CALCULATION_REPORT_REQUEST, wan);
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		GetDataInfoResult result = service.getDataInfo(accessKey, CALCULATION_REPORT_REQUEST_KEY, msg);
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(result.getMessageEnvelope().getBytes());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(input);
			NodeList nodes = document.getElementsByTagName("PdfFile");
			if (nodes != null && nodes.getLength() > 0) {
				Node node = nodes.item(0);
				return new ByteArrayInputStream(Base64.decodeBase64(node.getTextContent().getBytes()));
			}
		} catch (ParserConfigurationException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (SAXException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (IOException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		}

		System.out.println(result.getMessageEnvelope());
		return null;
	}

	public Reader getAuditCalculationRequest(String accessKey, String wan) throws AudaBridgeException {
		String msg = MessageFormat.format(AUDIT_CALCULATION_REQUEST, wan);
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		GetDataInfoResult result = service.getDataInfo(accessKey, AUDIT_CALCULATION_REQUEST_KEY, msg);
		System.out.println(result.getMessageEnvelope());
		System.out.println(result.getAdditionalInfo());
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		return new StringReader(result.getMessageEnvelope());
	}

	public Reader getXMLRequest(String accessKey, String wan) throws AudaBridgeException {
		String msg = MessageFormat.format(CALCULATION_DATA_REQUEST, wan);
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		GetDataInfoResult result = service.getDataInfo(accessKey, CALCULATION_DATA_REQUEST_KEY, msg);
		System.out.println(result.getMessageEnvelope() );
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		return new StringReader(result.getMessageEnvelope());
	}
	public CalculationDataResponse getCalculationData(String accessKey, String wan) throws AudaBridgeException  {
		Reader reader = getXMLRequest(accessKey, wan);
		return parseCalculationDataResponse(reader);
	}
	
	public String getWanRequest(String accessKey, String number, String controlCode) throws AudaBridgeException {
		String msg = MessageFormat.format(WAN_REQUEST, number, controlCode);
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		GetDataInfoResult result = service.getDataInfo(accessKey, WAN_REQUEST_KEY, msg);
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		return result.getMessageEnvelope();
	}

	public String createAssessmentRequest(String accessKey, CreateAssessmentRequest request) throws AudaBridgeException {
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		StringWriter out = new StringWriter();
		request.createXMLRequest(out);
		String message = out.toString();
		GetDataInfoResult result = service.getDataInfo(accessKey, CREATE_ASSESSMENT_REQUEST_KEY, message);
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(result.getMessageEnvelope().getBytes());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(input);
			NodeList nodes = document.getElementsByTagName("Wan");
			if (nodes != null && nodes.getLength() > 0) {
				Node wanNode = nodes.item(0);
				return wanNode.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (SAXException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (IOException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		}
		throw new AudaBridgeException(ANALISYS_ERROR);
	}

	public String getToken(String accessKey, String wan) throws AudaBridgeException {
		String msg = MessageFormat.format(TOKEN_REQUEST, wan);
		AudaBridge audaBridge = new AudaBridge();
		IAudaBridgeService service = audaBridge.getAudaBridgeServicePort();
		GetDataInfoResult result = service.getDataInfo(accessKey, TOKEN_REQUEST_KEY, msg);
		System.out.println(result.getMessageEnvelope());
		System.out.println(result.getAdditionalInfo());
		if (result.getErrorCode() != 0) {
			throw new AudaBridgeException(result.getErrorCode() + " - " + result.getErrorMessage());
		}
		try {
			ByteArrayInputStream input = new ByteArrayInputStream(result.getMessageEnvelope().getBytes());
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(input);
			NodeList nodes = document.getElementsByTagName("Token");
			if (nodes != null && nodes.getLength() > 0) {
				Node tokenNode = nodes.item(0);
				return tokenNode.getTextContent();
			}
		} catch (ParserConfigurationException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (SAXException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (IOException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		}
		throw new AudaBridgeException(ANALISYS_ERROR);
	}

	public CalculationDataResponse parseCalculationDataResponse(Reader reader) throws AudaBridgeException {
		try {
			Digester digester = new Digester();
			digester.setUseContextClassLoader(true);
			CalculationDataResponse response = new CalculationDataResponse();
			digester.push(response);

			digester.addBeanPropertySetter("*/TotalesValoracion/Wan", "wan");
			digester.addBeanPropertySetter("*/TotalesValoracion/NumeroValoracion", "numeroValoracion");
			digester.addBeanPropertySetter("*/TotalesValoracion/Referencia", "referencia");
			digester.addBeanPropertySetter("*/TotalesValoracion/FechaValoracion", "fechaValoracion");
			digester.addBeanPropertySetter("*/TotalesValoracion/FechaTarifa", "fechaTarifa");
			digester.addBeanPropertySetter("*/TotalesValoracion/CodigoAudaTransfer", "codigoAudaTransfer");

			digester.addObjectCreate("*/TotalGeneral", TotalGeneral.class);
			digester.addBeanPropertySetter("*/TotalGeneral/NombreFabricante", "nombreFabricante");
			digester.addBeanPropertySetter("*/TotalGeneral/NombreModelo", "nombreModelo");
			digester.addBeanPropertySetter("*/TotalGeneral/NombreVariante", "nombreVariante");
			digester.addBeanPropertySetter("*/TotalGeneral/EstadoGeneral", "estadoGeneral");
			digester.addBeanPropertySetter("*/TotalGeneral/TipoPintura", "tipoPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/Chasis", "chasis");
			digester.addBeanPropertySetter("*/TotalGeneral/PrecioHoraChapa", "precioHoraChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/PrecioHoraPintura", "precioHoraPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/PrecioHoraMecanica", "precioHoraMecanica");
			digester.addBeanPropertySetter("*/TotalGeneral/TiempoBase", "tiempoBase");
			digester.addBeanPropertySetter("*/TotalGeneral/PosicionesIntroducidas", "posicionesIntroducidas");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalMo", "totalMo");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalPintura", "totalPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalPiezas", "totalPiezas");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalSinIva", "totalSinIva");
			digester.addBeanPropertySetter("*/TotalGeneral/Descuentos", "descuentos");
			digester.addBeanPropertySetter("*/TotalGeneral/BaseImponible", "baseImponible");
			digester.addBeanPropertySetter("*/TotalGeneral/PorcentajeIva", "porcentajeIva");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteIva", "importeIva");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalValoracion", "totalValoracion");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalPagar", "totalPagar");
			digester.addBeanPropertySetter("*/TotalGeneral/DanosOcultos", "danosOcultos");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteParcialRepuestos", "importeParcialRepuestos");
			digester.addBeanPropertySetter("*/TotalGeneral/NumeroUtChapa", "numeroUtChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteParcialChapa", "importeParcialChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFijoChapa", "importeFijoChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/UtAlineacion", "utAlineacion");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteParcialAlineacion", "importeParcialAlineacion");
			digester.addBeanPropertySetter("*/TotalGeneral/SumaVarios", "sumaVarios");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFijoAlineacion", "importeFijoAlineacion");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalTrabajosAuxiliares", "totalTrabajosAuxiliares");
			digester.addBeanPropertySetter("*/TotalGeneral/TiempoMoPintura", "tiempoMoPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/TiempoPreparacionPintura", "tiempoPreparacionPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/TiempoTotalMoPintura", "tiempoTotalMoPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteMoPintura", "importeMoPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/MaterialesPinturaSuperficie", "materialesPinturaSuperficie");
			digester.addBeanPropertySetter("*/TotalGeneral/ConstanteMaterialPintura", "constanteMaterialPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFijoPintura", "importeFijoPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/MaterialPintura", "materialPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/UtTratamientoBajos", "utTratamientoBajos");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteTratamientoBajos", "importeTratamientoBajos");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFijoBajos", "importeFijoBajos");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalTratamientoBajos", "totalTratamientoBajos");
			digester.addBeanPropertySetter("*/TotalGeneral/UtTratamientoAnticorrosion", "utTratamientoAnticorrosion");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteAnticorrosion", "importeAnticorrosion");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFijoAnticorrosion", "importeFijoAnticorrosion");
			digester.addBeanPropertySetter("*/TotalGeneral/TotalAnticorrosion", "totalAnticorrosion");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteFranquicia", "importeFranquicia");
			digester.addBeanPropertySetter("*/TotalGeneral/DescuentoAdicional", "descuentoAdicional");
			digester.addBeanPropertySetter("*/TotalGeneral/UtMoPinturaPlas", "utMoPinturaPlas");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteMoPinturaChapa", "importeMoPinturaChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteMoPinturaPlastico", "importeMoPinturaPlastico");
			digester.addBeanPropertySetter("*/TotalGeneral/ImportePreparacionPintura", "importePreparacionPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteMatPinturaPlastico", "importeMatPinturaPlastico");
			digester.addBeanPropertySetter("*/TotalGeneral/RepuestosAbonados", "repuestosAbonados");
			digester.addBeanPropertySetter("*/TotalGeneral/DescuentoSobreRecambios", "descuentoSobreRecambios");
			digester.addBeanPropertySetter("*/TotalGeneral/IncrementoRecambios", "incrementoRecambios");
			digester.addBeanPropertySetter("*/TotalGeneral/PequenoMaterial", "pequenoMaterial");
			digester.addBeanPropertySetter("*/TotalGeneral/ImporteMOAdicional", "importeMOAdicional");
			digester.addBeanPropertySetter("*/TotalGeneral/DtoMOChapa", "dtoMOChapa");
			digester.addBeanPropertySetter("*/TotalGeneral/DtoMaterialPintura", "dtoMaterialPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/DtoSobreTotalPintura", "dtoSobreTotalPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/DtoMOPintura", "dtoMOPintura");
			digester.addBeanPropertySetter("*/TotalGeneral/DtoAsignaDirecta", "dtoAsignaDirecta");
			digester.addBeanPropertySetter("*/TotalGeneral/TasaSigaus", "tasaSigaus");
			digester.addBeanPropertySetter("*/TotalGeneral/TasaSignus", "tasaSignus");
			digester.addSetNext("*/TotalGeneral", "setTotalGeneral", TotalGeneral.class.getName());

			digester.addObjectCreate("*/Equipo", Equipo.class);
			digester.addBeanPropertySetter("*/Equipo/CodigoEquipo", "codigoEquipo");
			digester.addBeanPropertySetter("*/Equipo/Descripcion", "descripcion");
			digester.addBeanPropertySetter("*/Equipo/Erroneo", "erroneo");
			digester.addSetNext("*/Equipo", "addEquipo", Equipo.class.getName());

			digester.addObjectCreate("*/Pieza", Pieza.class);
			digester.addBeanPropertySetter("*/Pieza/TipoLinea", "tipoLinea");
			digester.addBeanPropertySetter("*/Pieza/Simbolo", "simbolo");
			digester.addBeanPropertySetter("*/Pieza/PosicionDb", "posicionDb");
			digester.addBeanPropertySetter("*/Pieza/Descripcion", "descripcion");
			digester.addBeanPropertySetter("*/Pieza/NumeroPieza", "numeroPieza");
			digester.addBeanPropertySetter("*/Pieza/Precio", "precio");
			digester.addBeanPropertySetter("*/Pieza/CambioPrecio", "cambioPrecio");
			digester.addBeanPropertySetter("*/Pieza/Descuento", "descuento");
			digester.addSetNext("*/Pieza", "addPieza", Pieza.class.getName());

			digester.addObjectCreate("*/Operacion", Operacion.class);
			digester.addBeanPropertySetter("*/Operacion/TipoLinea", "tipoLinea");
			digester.addBeanPropertySetter("*/Operacion/NumeroOperacion", "numeroOperacion");
			digester.addBeanPropertySetter("*/Operacion/Descripcion", "descripcion");
			digester.addBeanPropertySetter("*/Operacion/NumeroUt", "numeroUt");
			digester.addBeanPropertySetter("*/Operacion/CambioUt", "cambioUt");
			digester.addBeanPropertySetter("*/Operacion/CambioImporte", "cambioImporte");
			digester.addBeanPropertySetter("*/Operacion/Importe", "importe");
			digester.addSetNext("*/Operacion", "addOperacion", Operacion.class.getName());

			digester.addObjectCreate("*/Pintura", Pintura.class);
			digester.addBeanPropertySetter("*/Pintura/Simbolo", "simbolo");
			digester.addBeanPropertySetter("*/Pintura/Posicion", "posicion");
			digester.addBeanPropertySetter("*/Pintura/NumeroOperacion", "numeroOperacion");
			digester.addBeanPropertySetter("*/Pintura/DescripcionPieza", "descripcionPieza");
			digester.addBeanPropertySetter("*/Pintura/DescripcionPintura", "descripcionPintura");
			digester.addBeanPropertySetter("*/Pintura/NumeroUt", "numeroUt");
			digester.addBeanPropertySetter("*/Pintura/ImporteMaterial", "importeMaterial");
			digester.addBeanPropertySetter("*/Pintura/Descuento", "descuento");
			digester.addBeanPropertySetter("*/Pintura/CambioImporte", "cambioImporte");
			digester.addSetNext("*/Pintura", "addPintura", Pintura.class.getName());

			digester.parse(reader);
			return response;
		} catch (IOException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		} catch (SAXException e) {
			throw new AudaBridgeException(ANALISYS_ERROR, e);
		}
	}
}
