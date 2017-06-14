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
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.nav.remesas.BandejaDocRemesas;
import com.esferalia.aon.dex.nav.remesas.BandejaOtrosCobrosRemesas;
import com.esferalia.aon.dex.nav.remesas.Create;
import com.esferalia.aon.dex.nav.remesas.CreateResult;
import com.esferalia.aon.dex.nav.remesas.ObjectFactory;
import com.esferalia.aon.dex.nav.remesas.Remesas;
import com.esferalia.aon.dex.nav.remesas.RemesasList;

public class FbatchUnloadManager implements IDataLoadConstants {

	private Connection connection = null;

	public void processFbatchList(Parameters params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(params.getDomainName());

			obtainApplicationParameters(params);
			defineAuthentication(params);

			DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
			ObjectFactory factory = new ObjectFactory();
			RemesasList remesasList = factory.createRemesasList();
			Remesas remesas = null;
			int limit = params.getLimit();
			int line = 0;

			stmt = connection.prepareStatement(getFbatchListSQL(params));
			stmt.setObject(1, params.getFromDate(), Types.DATE);
			stmt.setObject(2, params.getToDate(), Types.DATE);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String fBatch = rs.getString(FBATCH);
				if (remesas == null || !remesas.getKey().equals(fBatch)) {
					if (limit == 0) break;
					--limit;

					remesas = factory.createRemesas();
					remesas.setKey(fBatch);
					remesas.setNoRemesa(fBatch);
					remesas.setDescripcion(rs.getString(DESCRIPTION));
					remesas.setFechaRegistroRemesa(formatter.format(rs.getDate(ISSUE_DATE)));
					remesas.setFormaDeCobro(rs.getString(PAY_METHOD));
					remesas.setImporteTotalRemesa(new BigDecimal(0));
					remesas.setBandejaDocRemesas(factory.createBandejaDocRemesasList());
					remesas.setBandejaOtrosCobrosRemesas(factory.createBandejaOtrosCobrosRemesasList());
					remesasList.getRemesas().add(remesas);
					line = 0;
				}

				if (rs.getString(INVOICE) != null) {
					BandejaDocRemesas lineas = factory.createBandejaDocRemesas();
					lineas.setKey(rs.getString(FBATCH_DETAIL));
					lineas.setNoRemesa(remesas.getNoRemesa());
					lineas.setNoLinea(++line);
					lineas.setHotel(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(HOTEL), null), 0, 20));
					lineas.setClienteNAV(StringUtils.defaultIfEmpty(rs.getString(CUSTOMER), null));
					lineas.setDocumento(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CONCEPT), null), 0, 20));
					lineas.setImporteCobrado(rs.getBigDecimal(AMOUNT));
					remesas.getBandejaDocRemesas().getBandejaDocRemesas().add(lineas);
					remesas.setImporteTotalRemesa(remesas.getImporteTotalRemesa().add(lineas.getImporteCobrado()));
				} else {
					BandejaOtrosCobrosRemesas lineas = factory.createBandejaOtrosCobrosRemesas();
					lineas.setKey(rs.getString(FBATCH_DETAIL));
					lineas.setNoRemesa(remesas.getNoRemesa());
					lineas.setNoLinea(++line);
					lineas.setClienteNAV(StringUtils.defaultIfEmpty(rs.getString(CUSTOMER), null));
					lineas.setConcepto(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CONCEPT), null), 0, 30));
					lineas.setImporteCobrado(rs.getBigDecimal(AMOUNT));
					remesas.getBandejaOtrosCobrosRemesas().getBandejaOtrosCobrosRemesas().add(lineas);
					remesas.setImporteTotalRemesa(remesas.getImporteTotalRemesa().add(lineas.getImporteCobrado()));
				}
			}

			for (Remesas remesa : remesasList.getRemesas()) {
				BandejaOtrosCobrosRemesas lineas = factory.createBandejaOtrosCobrosRemesas();
				lineas.setKey("0");
				lineas.setNoRemesa(remesa.getNoRemesa());
				lineas.setNoLinea(remesa.getBandejaDocRemesas().getBandejaDocRemesas().size() + remesa.getBandejaOtrosCobrosRemesas().getBandejaOtrosCobrosRemesas().size() + 1);
				lineas.setClienteNAV("0");
				lineas.setConcepto("FIN");
				lineas.setImporteCobrado(BigDecimal.valueOf(0));
				remesa.getBandejaOtrosCobrosRemesas().getBandejaOtrosCobrosRemesas().add(lineas);

				Create create = factory.createCreate();
				create.setRemesas(remesa);
				params.setFbatchId(Integer.parseInt(remesa.getNoRemesa()));
				params.setFbatchDescription(remesa.getDescripcion());

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
			stmt.setObject(2, AppParam.PMS_NAV_FINANCE_BATCH_URL.getValue(), Types.VARCHAR);
			stmt.setObject(3, AppParam.PMS_NAV_USERNAME.getValue(), Types.VARCHAR);
			stmt.setObject(4, AppParam.PMS_NAV_PASSWORD.getValue(), Types.VARCHAR);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(NAME);
				String value = rs.getString(VALUE);
				if (name.equals(AppParam.PMS_NAV_FINANCE_BATCH_URL.getValue())) {
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

	private String getFbatchListSQL(Parameters params) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT FB.id AS " + FBATCH + ", FB.description AS " + DESCRIPTION + ", FB.issue_date AS " + ISSUE_DATE);
		query.append(", RB.alias AS " + PAY_METHOD + ", FBD.id AS " + FBATCH_DETAIL);
		query.append(", I.id AS " + INVOICE + ", IFNULL(I.reference_code, F.concept) AS " + CONCEPT + ", F.amount AS " + AMOUNT);
		query.append(", (SELECT MAX(RA.value) FROM raddinfo AS RA WHERE RA.registry = F.registry AND RA.attribute = '" + NAV_ACCOUNT + "') AS " + CUSTOMER); 
		query.append(", (SELECT MIN(H.code) FROM hotel AS H, invoice_detail AS ID WHERE ID.invoice = I.id AND H.workplace = ID.workplace) AS " + HOTEL); 
		query.append(" FROM fbatch AS FB");
		query.append(" LEFT JOIN rbank AS RB ON RB.id = FB.rbank");
		query.append(" LEFT JOIN fbatch_detail AS FBD ON FBD.fbatch = FB.id");
		query.append(" LEFT JOIN finance AS F ON F.id = FBD.finance");
		query.append(" LEFT JOIN invoice AS I ON I.id = F.invoice");
		query.append(" WHERE FB.domain = " + params.getDomainId());
		query.append(" AND FB.status = " + FinanceBatchStatus.RECORDED.ordinal());
		query.append(" AND FB.payment = 0");
		query.append(" AND FB.issue_date BETWEEN ? AND ?");
		query.append(" AND 0 = (SELECT COUNT(*) FROM data_attach AS DA WHERE DA.source_id = FB.id AND DA.source = " + DataAttachmentSource.FBATCH.ordinal());
		query.append("   AND DA.type = " + DataAttachmentType.RESPONSE_OK.ordinal() + ")");
		query.append(" ORDER BY " + ISSUE_DATE + "," + FBATCH + "," + FBATCH_DETAIL);

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
			stmt.setObject(2, DataAttachmentSource.FBATCH.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getFbatchId(), Types.INTEGER);
			stmt.setObject(4, params.getFbatchDescription(), Types.VARCHAR);
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
			stmt.setObject(2, DataAttachmentSource.FBATCH.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getFbatchId(), Types.INTEGER);
			stmt.setObject(4, params.getFbatchDescription(), Types.VARCHAR);
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
