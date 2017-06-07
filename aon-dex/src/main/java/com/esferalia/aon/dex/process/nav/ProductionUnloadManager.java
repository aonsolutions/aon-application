package com.esferalia.aon.dex.process.nav;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.messaging.Endpoint;
import javax.xml.messaging.URLEndpoint;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.code.aon.common.ICommonConstants;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.data.enumeration.DataAttachmentSource;
import com.code.aon.data.enumeration.DataAttachmentType;
import com.code.aon.data.enumeration.DataResponseSource;
import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.nav.produccion.BandejaDatosEstadisticos;
import com.esferalia.aon.dex.nav.produccion.BandejaImportesProduccion;
import com.esferalia.aon.dex.nav.produccion.Create;
import com.esferalia.aon.dex.nav.produccion.CreateResult;
import com.esferalia.aon.dex.nav.produccion.ObjectFactory;
import com.esferalia.aon.dex.nav.produccion.Produccion;
import com.esferalia.aon.dex.nav.produccion.ProduccionList;

public class ProductionUnloadManager implements IDataLoadConstants {

	private Connection connection = null;
	private final String[] productionConcepts = new String[] {"ALOJAMIENTO", "EXTRAS", "PENSION", "TASAS", "TPV"};

	public void processProductionList(Parameters params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(params.getDomainName());

			obtainApplicationParameters(params);
			defineAuthentication(params);

			DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
			ObjectFactory factory = new ObjectFactory();
			ProduccionList produccionList = factory.createProduccionList();
			Produccion produccion = null;
			int line = 0;
			Map<String, String> productionMap = new HashMap<String, String>();

			stmt = connection.prepareStatement(getProductionListSQL(params));
			stmt.setObject(1, params.getFromDate(), Types.DATE);
			stmt.setObject(2, params.getToDate(), Types.DATE);
			if (params.getHotelId() != 0) {
				stmt.setObject(3, params.getHotelId(), Types.INTEGER);
			}
			rs = stmt.executeQuery();
			while (rs.next()) {
				String production = rs.getString(PRODUCTION);
				if (produccion == null || !produccion.getKey().equals(production)) {
					produccion = factory.createProduccion();
					produccion.setKey(production);
					produccion.setIdentificativoHotel(rs.getString(HOTEL));
					produccion.setFechaProduccion(formatter.format(rs.getDate(ISSUE_DATE)));
					produccion.setBandejaImportesProduccion(factory.createBandejaImportesProduccionList());
					produccion.setBandejaDatosEstadisticos(factory.createBandejaDatosEstadisticosList());
					produccionList.getProduccion().add(produccion);
					line = 0;
					productionMap.put(produccion.getKey(), rs.getString(CODE));
				}

				String concept = rs.getString(VARIABLE).contains(".") ? StringUtils.substringBefore(rs.getString(VARIABLE), ".") : rs.getString(VARIABLE);
				String subconcept = rs.getString(VARIABLE).contains(".") ? StringUtils.substringAfter(rs.getString(VARIABLE), ".") : null;
				if (StringUtils.startsWithAny(concept, productionConcepts)) {
					BandejaImportesProduccion importes = factory.createBandejaImportesProduccion();
					importes.setKey(rs.getString(DETAIL));
					importes.setIdentificativoHotel(produccion.getIdentificativoHotel());
					importes.setFechaProduccion(produccion.getFechaProduccion());
					importes.setConcepto(concept);
					importes.setSubconcepto(subconcept);
					importes.setCantidad(BigDecimal.valueOf(1));
					importes.setDescripcion(rs.getString(VARIABLE));
					importes.setImporte(rs.getBigDecimal(VALUE));
					produccion.getBandejaImportesProduccion().getBandejaImportesProduccion().add(importes);
				} else {
					BandejaDatosEstadisticos datos = factory.createBandejaDatosEstadisticos();
					datos.setKey(rs.getString(DETAIL));
					datos.setIdentificativoHotel(produccion.getIdentificativoHotel());
					datos.setFechaProduccion(produccion.getFechaProduccion());
					datos.setCodigoEstadistico(concept);
					datos.setValor(rs.getBigDecimal(VALUE));
					datos.setNoLinea(++line);
					produccion.getBandejaDatosEstadisticos().getBandejaDatosEstadisticos().add(datos);
				}
			}

			for (Produccion prod : produccionList.getProduccion()) {
				Create create = factory.createCreate();
				create.setProduccion(prod);
				params.setProductionId(Integer.parseInt(prod.getKey()));
				params.setProductionDescription(productionMap.get(prod.getKey()));

				StringWriter writer = new StringWriter();
				JAXBContext context = JAXBContext.newInstance(Create.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.marshal(create, writer);
				sendData(params, writer.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
				connection.close();
			} catch (SQLException ex) {}
		}
	}

	private void obtainApplicationParameters(Parameters params) throws SQLException {
		String sql = "SELECT name, value FROM app_param WHERE domain = ? AND name IN (?, ?, ?)";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setObject(1, params.getDomainId(), Types.INTEGER);
			stmt.setObject(2, AppParam.PMS_NAV_PRODUCTION_URL.getValue(), Types.VARCHAR);
			stmt.setObject(3, AppParam.PMS_NAV_USERNAME.getValue(), Types.VARCHAR);
			stmt.setObject(4, AppParam.PMS_NAV_PASSWORD.getValue(), Types.VARCHAR);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(NAME);
				String value = rs.getString(VALUE);
				if (name.equals(AppParam.PMS_NAV_PRODUCTION_URL.getValue())) {
					params.setWsUrl(value);
				} else if (name.equals(AppParam.PMS_NAV_USERNAME.getValue())) {
					params.setUserName(value);
				} else if (name.equals(AppParam.PMS_NAV_PASSWORD.getValue())) {
					params.setPassword(value);
				}
			}
		} catch (SQLException ex) {
			throw ex;
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException ex) {}
		}
	}

	private void defineAuthentication(Parameters params) {
		NtlmAuthenticator authenticator = new NtlmAuthenticator(params.getUserName(), params.getPassword());
		Authenticator.setDefault(authenticator);
	}

	private String getProductionListSQL(Parameters params) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT DR.id AS " + PRODUCTION + ", H.code AS " + HOTEL + ", DR.response_date AS " + ISSUE_DATE + ", DR.code AS " + CODE);
		query.append(", DRD.id AS " + DETAIL + ", DRD.data_variable AS " + VARIABLE + ", DRD.data_value AS " + VALUE);
		query.append(" FROM data_response AS DR");
		query.append(" LEFT JOIN data_response_detail AS DRD ON DRD.data_response = DR.id");
		query.append(" LEFT JOIN hotel AS H ON H.id = DR.source_id");
		query.append(" WHERE DR.domain = " + params.getDomainId());
		query.append(" AND DR.source = " + DataResponseSource.HOTEL.ordinal());
		query.append(" AND DR.response_date BETWEEN ? AND ?");
		query.append(" AND DR.code LIKE 'PROD_%'");
		if (params.getHotelId() != 0) {
			query.append(" AND DR.source_id = ?");
		}
		query.append(" AND 0 = (SELECT COUNT(*) FROM data_attach AS DA WHERE DA.source_id = DR.id AND DA.source = " + DataAttachmentSource.PRODUCTION.ordinal());
		query.append("   AND DA.type = " + DataAttachmentType.RESPONSE_OK.ordinal() + ")");
		query.append(" ORDER BY " + HOTEL + "," + ISSUE_DATE + "," + VARIABLE);

		return query.toString();
	}

	private void sendData(Parameters params, String message) throws Exception {
		Endpoint endpoint = new URLEndpoint(new URL(params.getWsUrl()).toString());
		SOAPMessage soapRequest = MessageFactory.newInstance().createMessage();
		soapRequest.getSOAPBody().addDocument(obtainMessageDocument(message));
		SOAPEnvelope envelope = soapRequest.getSOAPPart().getEnvelope();
		envelope.addNamespaceDeclaration(XMLConstants.DEFAULT_NS_PREFIX, soapRequest.getSOAPBody().getFirstChild().getNamespaceURI());

		saveRequestData(soapRequest, params);
		SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();
		SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
		saveResponseData(soapResponse, params);
	}

	private Document obtainMessageDocument(String message) throws Exception {
		DocumentBuilderFactory builderfactory = DocumentBuilderFactory.newInstance();
		builderfactory.setNamespaceAware(true);
		DocumentBuilder builder = builderfactory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(message)));  
	}

	private void saveRequestData(SOAPMessage soapRequest, Parameters params) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		soapRequest.writeTo(output);

		String sql = "INSERT INTO data_attach(domain, source, source_id, description, data, type, mimeType, creation_user, creation_date) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setObject(1, params.getDomainId(), Types.INTEGER);
			stmt.setObject(2, DataAttachmentSource.PRODUCTION.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getProductionId(), Types.INTEGER);
			stmt.setObject(4, params.getProductionDescription(), Types.VARCHAR);
			stmt.setObject(5, output.toString(), Types.VARCHAR);
			stmt.setObject(6, DataAttachmentType.REQUEST.ordinal(), Types.INTEGER);
			stmt.setObject(7, MimeType.MIME_XML.ordinal(), Types.INTEGER);
			stmt.setObject(8, ICommonConstants.SYSTEM_USER, Types.VARCHAR);
			stmt.setObject(9, new Date(), Types.TIMESTAMP);
			stmt.executeUpdate();
		} catch (SQLException ex) {
			throw ex;
		} finally {
			try {
				stmt.close();
			} catch (SQLException ex) {}
		}
	}

	private void saveResponseData(SOAPMessage soapResponse, Parameters params) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		soapResponse.writeTo(output);

		String insert = "INSERT INTO data_attach(domain, source, source_id, description, data, type, mimeType, creation_user, creation_date) " +
							"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(insert);
			stmt.setObject(1, params.getDomainId(), Types.INTEGER);
			stmt.setObject(2, DataAttachmentSource.PRODUCTION.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getProductionId(), Types.INTEGER);
			stmt.setObject(4, params.getProductionDescription(), Types.VARCHAR);
			stmt.setObject(5, output.toString(), Types.VARCHAR);
			stmt.setObject(6, DataAttachmentType.RESPONSE_OK.ordinal(), Types.INTEGER);
			stmt.setObject(7, MimeType.MIME_XML.ordinal(), Types.INTEGER);
			stmt.setObject(8, ICommonConstants.SYSTEM_USER, Types.VARCHAR);
			stmt.setObject(9, new Date(), Types.TIMESTAMP);

			JAXBContext context = JAXBContext.newInstance(CreateResult.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());

			stmt.executeUpdate();
		} catch (Exception ex) {
			try {
				stmt.setObject(6, DataAttachmentType.RESPONSE_ERROR.ordinal(), Types.INTEGER);
				stmt.executeUpdate();
			} catch (Exception e) {
				throw e;
			}
		} finally {
			stmt.close();
		}
	}


	public class NtlmAuthenticator extends Authenticator {

		  private final String username;
		  private final char[] password;

		  public NtlmAuthenticator(final String username, final String password) {
		    super();
		    this.username = new String(username);
		    this.password = password.toCharArray(); 
		  }

		  @Override
		  public PasswordAuthentication getPasswordAuthentication() {
		    return (new PasswordAuthentication (username, password));
		  }

	}

}
