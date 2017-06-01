package com.esferalia.aon.dex.process.nav;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.data.enumeration.DataAttachmentSource;
import com.code.aon.data.enumeration.DataAttachmentType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.nav.facturas.BandejaLineasFacVenta;
import com.esferalia.aon.dex.nav.facturas.Create;
import com.esferalia.aon.dex.nav.facturas.CreateResult;
import com.esferalia.aon.dex.nav.facturas.Facturas;
import com.esferalia.aon.dex.nav.facturas.FacturasList;
import com.esferalia.aon.dex.nav.facturas.ObjectFactory;

public class InvoiceUnloadManager implements IDataLoadConstants {

	private Connection connection = null;

	public void processInvoiceList(Parameters params) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = DatabaseUtil.getConnection(params.getDomainName());

			obtainApplicationParameters(params);
			defineAuthentication(params);

			DateFormat formatter = new SimpleDateFormat("ddMMyyyy");
			ObjectFactory factory = new ObjectFactory();
			FacturasList facturasList = factory.createFacturasList();
			Facturas facturas = null;
			int limit = params.getLimit();
			int line = 0;

			stmt = connection.prepareStatement(getInvoiceListSQL(params));
			stmt.setObject(1, params.getFromDate(), Types.DATE);
			stmt.setObject(2, params.getToDate(), Types.DATE);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String invoice = rs.getString(INVOICE);
				if (facturas == null || !facturas.getKey().equals(invoice)) {
					if (limit == 0) break;
					--limit;

					facturas = factory.createFacturas();
					facturas.setKey(invoice);
					facturas.setNoFactura(StringUtils.defaultIfEmpty(rs.getString(NUMBER), null));
					facturas.setNoSerie(StringUtils.defaultIfEmpty(rs.getString(SERIES), null));
					facturas.setTipoDocumentoFactura(0);
					facturas.setTipoFactura((rs.getInt(RECTIFICATION_TYPE)==1) ? 1 : 0);
					facturas.setCodigoCliente(StringUtils.defaultIfEmpty(rs.getString(CUSTOMER), null));
					facturas.setNombreCliente(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CUSTOMER_NAME), null), 0, 50));
					facturas.setCIFCliente(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CUSTOMER_DOCUMENT), null), 0, 20));
					facturas.setDireccion(getAddress(rs.getString(STREET_TYPE), rs.getString(ADDRESS), rs.getString(ADDRESS_NUMBER), rs.getString(ADDRESS_EXT)));
					facturas.setCP(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(ZIP), null), 0, 20));
					facturas.setPoblacion(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CITY), null), 0, 50));
					facturas.setProvincia(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(PROVINCE), null), 0, 30));
					facturas.setPais(StringUtils.isNotBlank(rs.getString(COUNTRY)) ? Country.obtainCountry(rs.getString(COUNTRY)).getValue() : null);
					facturas.setFechaRegistro(formatter.format(rs.getDate(ISSUE_DATE)));
					facturas.setHotel(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(HOTEL), null), 0, 20));
					facturas.setReserva(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(RESERVATION), null), 0, 30));
					facturas.setBono(StringUtils.substring(StringUtils.defaultIfEmpty(rs.getString(CODE), null), 0, 30));
					facturas.setFechaEntrada((rs.getDate(START_DATE)!=null) ? formatter.format(rs.getDate(START_DATE)) : null);
					facturas.setFechaSalida((rs.getDate(END_DATE)!=null) ? formatter.format(rs.getDate(END_DATE)) : null);
					facturas.setImporteTotalInclIVA(rs.getBigDecimal(TOTAL));
					facturas.setDivisa(EURO);
					facturas.setNoFacturaCorregida((rs.getInt(RECTIFICATION_TYPE)==1) ? rs.getString(RECTIFICATION_INVOICE) : null);
					facturas.setBandejaLineasFacVenta(factory.createBandejaLineasFacVentaList());
					facturasList.getFacturas().add(facturas);
					line = 0;
				}

				if (rs.getBigDecimal(TAXABLE_BASE) != null) {
					BandejaLineasFacVenta lineas = factory.createBandejaLineasFacVenta();
					lineas.setKey(facturas.getKey() + "/" + (++line));
					lineas.setNoSerie(facturas.getNoSerie());
					lineas.setNoFactura(facturas.getNoFactura());
					lineas.setNoLinea(line);
					lineas.setDescripcion(getLineDescription(rs.getInt(ADVANCE), rs.getBigDecimal(TAX_PERCENT)));
					lineas.setCantidad(BigDecimal.valueOf(1));
					lineas.setImporteSinIva(rs.getBigDecimal(TAXABLE_BASE));
					lineas.setTipoIVA(rs.getString(TAX));
					lineas.setPorcentajeIVA(rs.getBigDecimal(TAX_PERCENT));
					lineas.setImporteIVA(rs.getBigDecimal(TAX_QUOTA));
					checkDetail(lineas);
					facturas.getBandejaLineasFacVenta().getBandejaLineasFacVenta().add(lineas);
				}
			}

			for (Facturas factura : facturasList.getFacturas()) {
				Create create = factory.createCreate();
				create.setFacturas(factura);
				params.setInvoiceId(Integer.parseInt(factura.getKey()));
				params.setInvoiceReferenceCode(StringUtils.leftPad(factura.getNoFactura(), 6, "0"));
				if (!StringUtils.isBlank(factura.getNoSerie())) {
					params.setInvoiceReferenceCode(factura.getNoSerie() + "/" + params.getInvoiceReferenceCode());
				}

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
			stmt.setObject(2, AppParam.PMS_NAV_SALE_INVOICE_URL.getValue(), Types.VARCHAR);
			stmt.setObject(3, AppParam.PMS_NAV_USERNAME.getValue(), Types.VARCHAR);
			stmt.setObject(4, AppParam.PMS_NAV_PASSWORD.getValue(), Types.VARCHAR);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString(NAME);
				String value = rs.getString(VALUE);
				if (name.equals(AppParam.PMS_NAV_SALE_INVOICE_URL.getValue())) {
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

	private String getInvoiceListSQL(Parameters params) {
		StringBuffer query = new StringBuffer();
		query.append("SELECT I.id AS " + INVOICE + ", I.series AS " + SERIES + ", I.number AS " + NUMBER + ", I.issue_date AS " + ISSUE_DATE);
		query.append(", I.rectification_type AS " + RECTIFICATION_TYPE + ", I.rectification_invoice AS " + RECTIFICATION_INVOICE);
		query.append(", I.rname AS " + CUSTOMER_NAME + ", I.rdocument AS " + CUSTOMER_DOCUMENT + ", I.total AS " + TOTAL);
		query.append(", (SELECT MAX(RA.value) FROM raddinfo AS RA WHERE RA.registry = I.registry AND RA.attribute = '" + NAV_ACCOUNT+ "') AS " + CUSTOMER); 
		query.append(", IA.street_type AS " + STREET_TYPE + ", IA.address AS " + ADDRESS + ", IA.number AS " + ADDRESS_NUMBER + ", IA.address2 AS " + ADDRESS_EXT);
		query.append(", IA.zip AS " + ZIP + ", IA.city AS " + CITY + ", IFNULL(IA.province, G.name) AS " + PROVINCE + ", IFNULL(G2.code,G.code) AS " + COUNTRY);
		query.append(", PR.project AS " + RESERVATION + ", PR.code AS " + CODE + ", PR.start_date AS " + START_DATE + ", PR.end_date AS " + END_DATE);
		query.append(", H.code AS " + HOTEL + ", IFNULL(AP.id,0) AS " + ADVANCE + ", SUM(ID.taxable_base) AS " + TAXABLE_BASE);
		query.append(", IT.percentage AS " + TAX_PERCENT + ", SUM(IT.quota) AS " + TAX_QUOTA);
		query.append(", (SELECT MIN(T.id) FROM tax AS T WHERE T.percentage = IT.percentage AND T.tax_type = " + TaxType.VAT.ordinal() + ") AS " + TAX); 
		query.append(" FROM invoice AS I");
		query.append(" LEFT JOIN invoice_address AS IA ON IA.invoice = I.id");
		query.append(" LEFT JOIN geozone AS G ON G.id = IA.geozone");
		query.append(" LEFT JOIN geotree AS GT ON GT.child = G.id");
		query.append(" LEFT JOIN geozone AS G2 ON G2.id = GT.parent");
		query.append(" LEFT JOIN project_reservation AS PR ON PR.project = I.project");
		query.append(" LEFT JOIN invoice_detail AS ID ON ID.invoice = I.id");
		query.append(" LEFT JOIN item AS I2 ON I2.id = ID.item");
		query.append(" LEFT JOIN product AS P ON P.id = I2.product");
		query.append(" LEFT JOIN app_param AS AP ON AP.name = '" + AppParam.PMS_ADVANCE_ITEM.getValue() + "' AND AP.value = P.code");
		query.append(" LEFT JOIN invoice_tax AS IT ON IT.invoice_detail = ID.id AND IT.tax_type = " + TaxType.VAT.ordinal());
		query.append(" LEFT JOIN hotel AS H ON H.workplace = ID.workplace");
		query.append(" LEFT JOIN data_attach AS DA ON DA.source = " + DataAttachmentSource.INVOICE.ordinal() + " AND DA.source_id = I.id");
		query.append("   AND DA.type = " + DataAttachmentType.RESPONSE_OK.ordinal());
		query.append(" WHERE I.domain = " + params.getDomainId());
		query.append(" AND I.type = " + InvoiceType.SALES.ordinal());
		query.append(" AND I.status = " + InvoiceStatus.SCORED.ordinal());
		query.append(" AND I.signed = 0");
		query.append(" AND I.issue_date BETWEEN ? AND ?");
		query.append(" AND DA.id IS NULL");
		query.append(" GROUP BY " + INVOICE + "," + ADVANCE + "," + TAX_PERCENT);
		query.append(" ORDER BY " + ISSUE_DATE + "," + INVOICE + "," + ADVANCE + "," + TAX_PERCENT);

		return query.toString();
	}

	private String getAddress(String streetType, String address, String number, String address2) {
    	StringBuffer buf = new StringBuffer();
    	if (StringUtils.isNotBlank(address) || StringUtils.isNotBlank(address2)) {
        	buf.append(streetType!=null ? streetType : "");
        	buf.append(streetType!=null ? ". " : "");
        	buf.append(StringUtils.isEmpty(address) ? "" : address);
        	buf.append(StringUtils.isEmpty(number) ? "" : " ");
        	buf.append(StringUtils.isEmpty(number) ? "" : number);
        	buf.append(StringUtils.isEmpty(address2) ? "" : ", ");
        	buf.append(StringUtils.isEmpty(address2) ? "" : address2);
    	}
    	return StringUtils.substring(StringUtils.defaultIfEmpty(buf.toString(), null), 0, 50);
    }

	private String getLineDescription(int advance, BigDecimal taxPercent) {
		String description = (advance == 0) ? SERVICIO : ANTICIPO;
		if (advance == 0) {
			NumberFormat formatter = new DecimalFormat("##0.##");
			description += " " + formatter.format(taxPercent.doubleValue()) + "%";
		}
		return description;
	}

	private void checkDetail(BandejaLineasFacVenta lineas) {
    	if (lineas.getImporteIVA().doubleValue() == 0 && lineas.getImporteSinIva().doubleValue() != 0) {
    		double quota = CommonUtil.round(lineas.getImporteSinIva().doubleValue() * lineas.getPorcentajeIVA().doubleValue() / 100, 4);
    		lineas.setImporteIVA(new BigDecimal(quota).setScale(4, RoundingMode.HALF_UP));
    	}
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
			stmt.setObject(2, DataAttachmentSource.INVOICE.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getInvoiceId(), Types.INTEGER);
			stmt.setObject(4, params.getInvoiceReferenceCode(), Types.VARCHAR);
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
		String update = "UPDATE invoice SET signed = 1 WHERE id = ?";
		PreparedStatement stmt = null;
		PreparedStatement updStmt = null;
		try {
			stmt = connection.prepareStatement(insert);
			stmt.setObject(1, params.getDomainId(), Types.INTEGER);
			stmt.setObject(2, DataAttachmentSource.INVOICE.ordinal(), Types.INTEGER);
			stmt.setObject(3, params.getInvoiceId(), Types.INTEGER);
			stmt.setObject(4, params.getInvoiceReferenceCode(), Types.VARCHAR);
			stmt.setObject(5, output.toString(), Types.VARCHAR);
			stmt.setObject(6, DataAttachmentType.RESPONSE_OK.ordinal(), Types.INTEGER);
			stmt.setObject(7, MimeType.MIME_XML.ordinal(), Types.INTEGER);
			stmt.setObject(8, ICommonConstants.SYSTEM_USER, Types.VARCHAR);
			stmt.setObject(9, new Date(), Types.TIMESTAMP);

			updStmt = connection.prepareStatement(update);
			updStmt.setObject(1, params.getInvoiceId(), Types.INTEGER);

			JAXBContext context = JAXBContext.newInstance(CreateResult.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());

			stmt.executeUpdate();
			updStmt.executeUpdate();
		} catch (Exception ex) {
			try {
				stmt.setObject(6, DataAttachmentType.RESPONSE_ERROR.ordinal(), Types.INTEGER);
				stmt.executeUpdate();
			} catch (Exception e) {
				throw e;
			}
		} finally {
			stmt.close();
			updStmt.close();
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
