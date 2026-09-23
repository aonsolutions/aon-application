package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Fbatch.FBATCH;
import static com.esferalia.aon.jooq.tables.FbatchDetail.FBATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Raddress;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Generacion de ficheros SEPA de adeudo directo (pain.008.001.02) para remesas de cobro.
 *
 * Las remesas de pago (34-14, pain.001) siguen en SettleSalariesDAO.
 *
 * PUNTOS PENDIENTES DE CONFIRMAR (ver comentarios TODO en el codigo):
 *  - Origen del sufijo del identificador de acreedor (000 / 500 / 700).
 *  - Vigencia de LclInstrm COR1 en el 58 COBRO: el esquema COR1 se retiro en 2016.
 *  - SeqTp fijo a RCUR: un mandato nuevo deberia emitirse como FRST.
 *  - Formato exacto de MsgId (se respeta el historico).
 */
public class FBatchChargeSepaDAO {

	/** Fecha de firma de los mandatos heredados de la migracion SEPA. */
	private static final String MANDATE_SIGNATURE_DATE = "2009-10-31";

	// ---------------------------------------------------------- VARIANTES

	public static enum SepaChargeVariant {

		  CORE_19_14 ((byte)  8, "A"    , "CORE", true , true , false, "000")
		, ANTICIPO_58((byte) 12, "FSDDA", "CORE", false, false, true , "500")
		, COBRO_58   ((byte) 13, "A"    , "COR1", false, false, true , "700")
		;

		private final byte    code;
		private final String  msgIdPrefix;
		private final String  localInstrument;
		/** true -> <SchmeNm><Cd>CORE</Cd>; false -> <SchmeNm><Prtry>SEPA</Prtry> */
		private final boolean initiatingPartySchemeAsCode;
		/** true -> el bloque PmtInf lleva NbOfTxs y CtrlSum propios. */
		private final boolean paymentInfoTotals;
		/** true -> un PmtInf por fecha de vencimiento; false -> uno solo. */
		private final boolean groupByDueDate;
		private final String  defaultSuffix;

		private SepaChargeVariant(byte code, String msgIdPrefix, String localInstrument,
				boolean initiatingPartySchemeAsCode, boolean paymentInfoTotals,
				boolean groupByDueDate, String defaultSuffix) {
			this.code = code;
			this.msgIdPrefix = msgIdPrefix;
			this.localInstrument = localInstrument;
			this.initiatingPartySchemeAsCode = initiatingPartySchemeAsCode;
			this.paymentInfoTotals = paymentInfoTotals;
			this.groupByDueDate = groupByDueDate;
			this.defaultSuffix = defaultSuffix;
		}

		public byte getCode() { return code; }

		public static SepaChargeVariant of(Byte code) {
			if (code == null) return null;
			for (SepaChargeVariant v : values()) if (v.code == code) return v;
			return null;
		}
	}

	// ---------------------------------------------------------- ENTRADA

	public static Integer createSepaFile(CloseableAONContext ctx, Integer fbatchId) throws Exception {
		ctx.checkWrite();
		if (fbatchId == null) throw new AonCoreException("ID de remesa vacio");

		Record header = getHeaderRecord(ctx, fbatchId);

		SepaChargeVariant variant = SepaChargeVariant.of(header.get(FBATCH.TYPE));
		if (variant == null)
			throw new AonCoreException("El tipo de fichero de la remesa " + fbatchId
					+ " no corresponde a ningun adeudo SEPA");

		Result<Record> details = getDetailRecords(ctx, fbatchId);
		if (details == null || details.isEmpty())
			throw new AonCoreException("No existen vencimientos en la remesa sobre los que generar el fichero SEPA");

		validate(ctx, header, details);

		String xml = buildXml(ctx, variant, header, details);

		return SettleSalariesDAO.saveAttach(ctx, fbatchId, xml.getBytes("UTF-8"));
	}

	// ---------------------------------------------------------- VALIDACION

	private static void validate(CloseableAONContext ctx, Record header, Result<Record> details) {
		if (AonStringUtils.isBlank(header.get(RBANK.BANK_ACCOUNT)))
			throw new AonCoreException("La cuenta bancaria de la remesa no tiene IBAN");
		if (AonStringUtils.isBlank(header.get(RBANK.BIC)))
			throw new AonCoreException("La cuenta bancaria de la remesa no tiene BIC");
		if (AonStringUtils.isBlank(header.get(REGISTRY.DOCUMENT)))
			throw new AonCoreException("La empresa no tiene NIF informado");

		List<String> errors = new ArrayList<String>();

		for (Record d : details) {
			String who = d.get(FINANCE.RNAME) + " (vto. " + d.get(FINANCE.ID) + ")";

			if (d.get(INVOICE.REGISTRY) == null)
				errors.add(who + ": sin factura asociada, no se puede determinar el mandato");
			if (AonStringUtils.isBlank(d.get(FINANCE.BANK_ACCOUNT)))
				errors.add(who + ": sin cuenta bancaria");
			if (AonStringUtils.isBlank(d.get(FINANCE.BIC)))
				errors.add(who + ": sin BIC");
			if (d.get(FINANCE.DUE_DATE) == null)
				errors.add(who + ": sin fecha de vencimiento");
			if (d.get(FBATCH_DETAIL.AMOUNT) == null || d.get(FBATCH_DETAIL.AMOUNT) <= 0.00)
				errors.add(who + ": importe no positivo");

			// El mandato se toma de la factura; si el titular del vencimiento y el de la
			// factura no coinciden, el fichero mezclaria datos de dos registros.
			if (d.get(INVOICE.REGISTRY) != null && d.get(FINANCE.REGISTRY) != null
					&& !d.get(INVOICE.REGISTRY).equals(d.get(FINANCE.REGISTRY)))
				ctx.log().debug("AVISO: vencimiento " + d.get(FINANCE.ID)
						+ " con registry " + d.get(FINANCE.REGISTRY)
						+ " distinto del de su factura " + d.get(INVOICE.REGISTRY));
		}

		if (!errors.isEmpty())
			throw new AonCoreException("No se puede generar el fichero SEPA:\n- "
					+ errors.stream().collect(Collectors.joining("\n- ")));
	}

	// ---------------------------------------------------------- CONSULTAS

	private static Record getHeaderRecord(CloseableAONContext ctx, Integer fbatchId) {
		Record header = ctx.getDslContext().select()
			.from(FBATCH)
			.innerJoin(ENTERPRISE).on(ENTERPRISE.DOMAIN.eq(FBATCH.DOMAIN))
			.innerJoin(REGISTRY).on(REGISTRY.ID.eq(ENTERPRISE.REGISTRY))
			.innerJoin(RBANK).on(RBANK.ID.eq(FBATCH.RBANK))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.eq(REGISTRY.ID).and(RADDRESS.TYPE.eq((byte) 0)))
			.leftOuterJoin(GEOZONE).on(GEOZONE.ID.eq(RADDRESS.GEOZONE))
			.where(FBATCH.ID.eq(fbatchId))
			.and(FBATCH.DOMAIN.eq(ctx.getDomainId()))
			.orderBy(RADDRESS.ID)
			.limit(1)          // varias direcciones tipo 0 no deben romper la generacion
			.fetchOne();

		if (header == null)
			throw new AonCoreException("No existe la remesa " + fbatchId
					+ ", o no tiene cuenta bancaria o empresa asociada");

		return header;
	}

	private static Result<Record> getDetailRecords(CloseableAONContext ctx, Integer fbatchId) {
		Raddress address  = RADDRESS.as("r");
		Raddress address2 = RADDRESS.as("r2");

		// Primera direccion (por ID) de tipo 0 de cada registro
		Table<?> addrMin = ctx.getDslContext()
			.select(address.REGISTRY.as("registry"), DSL.min(address.ID).as("min_id"))
			.from(address)
			.where(address.TYPE.eq((byte) 0))
			.groupBy(address.REGISTRY)
			.asTable("addr_min");

		return ctx.getDslContext()
			.select()
			.from(FBATCH_DETAIL)
			.join(FINANCE).on(FINANCE.ID.eq(FBATCH_DETAIL.FINANCE))
			.leftJoin(INVOICE).on(INVOICE.ID.eq(FINANCE.INVOICE))
			.leftJoin(addrMin).on(addrMin.field("registry", address.REGISTRY.getType()).eq(FINANCE.REGISTRY))
			.leftJoin(address2).on(address2.REGISTRY.eq(addrMin.field("registry", address.REGISTRY.getType()))
					.and(address2.ID.eq(addrMin.field("min_id", address.ID.getType()))))
			.leftJoin(GEOZONE).on(GEOZONE.ID.eq(address2.GEOZONE))
			.where(FBATCH_DETAIL.FBATCH.eq(fbatchId))
			.and(FBATCH_DETAIL.DOMAIN.eq(ctx.getDomainId()))
			.orderBy(FINANCE.DUE_DATE, FINANCE.ID)
			.fetch();
	}

	// ---------------------------------------------------------- XML

	private static String buildXml(CloseableAONContext ctx, SepaChargeVariant variant,
			Record header, Result<Record> details) {

		String nif       = header.get(REGISTRY.DOCUMENT);
		String suffix    = resolveSuffix(header.get(RBANK.SUFIX), variant);
		String creditorId = creditorId(nif, suffix);

		Date   issueDate  = header.get(FBATCH.ISSUE_DATE);
		double total      = details.stream().map(d -> d.get(FBATCH_DETAIL.AMOUNT)).reduce(0.00, (a, b) -> a + b);

		StringBuilder xml = new StringBuilder(4096);
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		xml.append("<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02\"")
		   .append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
		xml.append("<CstmrDrctDbtInitn>");

		// ---- Cabecera

		xml.append("<GrpHdr>");
		xml.append("<MsgId>").append(buildMsgId(variant, header)).append("</MsgId>");
		xml.append("<CreDtTm>").append(iso(issueDate)).append("T00:00:00</CreDtTm>");
		xml.append("<NbOfTxs>").append(details.size()).append("</NbOfTxs>");
		xml.append("<CtrlSum>").append(amount(total)).append("</CtrlSum>");
		xml.append("<InitgPty>");
		xml.append("<Nm>").append(clean(header.get(REGISTRY.NAME))).append("</Nm>");
		xml.append("<Id><OrgId><Othr><Id>").append(creditorId).append("</Id><SchmeNm>");
		xml.append(variant.initiatingPartySchemeAsCode ? "<Cd>CORE</Cd>" : "<Prtry>SEPA</Prtry>");
		xml.append("</SchmeNm></Othr></OrgId></Id>");
		xml.append("</InitgPty>");
		xml.append("</GrpHdr>");

		// ---- Bloques de pago

		for (Map.Entry<Date, List<Record>> group : groupDetails(variant, details, issueDate).entrySet())
			appendPaymentInfo(xml, variant, header, creditorId, group.getKey(), group.getValue());

		xml.append("</CstmrDrctDbtInitn></Document>");

		ctx.log().debug("SEPA " + variant.name() + " generado: " + details.size()
				+ " adeudos, " + amount(total) + " EUR");

		return xml.toString();
	}

	/**
	 * 19-14: un unico bloque con fecha de cobro = fecha de la remesa.
	 * 58: un bloque por fecha de vencimiento, con fecha de cobro = vencimiento + 1 dia.
	 */
	private static Map<Date, List<Record>> groupDetails(SepaChargeVariant variant,
			Result<Record> details, Date issueDate) {

		Map<Date, List<Record>> groups = new LinkedHashMap<Date, List<Record>>();

		if (!variant.groupByDueDate) {
			groups.put(issueDate, new ArrayList<Record>(details));
			return groups;
		}

		for (Record d : details) {
			Date collection = addDays(d.get(FINANCE.DUE_DATE), 1);
			List<Record> group = groups.get(collection);
			if (group == null) {
				group = new ArrayList<Record>();
				groups.put(collection, group);
			}
			group.add(d);
		}
		return groups;
	}

	private static void appendPaymentInfo(StringBuilder xml, SepaChargeVariant variant,
			Record header, String creditorId, Date collectionDate, List<Record> details) {

		double total = details.stream().map(d -> d.get(FBATCH_DETAIL.AMOUNT)).reduce(0.00, (a, b) -> a + b);

		xml.append("<PmtInf>");
		xml.append("<PmtInfId>").append(buildPmtInfId(header)).append("</PmtInfId>");
		xml.append("<PmtMtd>DD</PmtMtd>");

		if (variant.paymentInfoTotals) {
			xml.append("<NbOfTxs>").append(details.size()).append("</NbOfTxs>");
			xml.append("<CtrlSum>").append(amount(total)).append("</CtrlSum>");
		}

		xml.append("<PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl>");
		xml.append("<LclInstrm><Cd>").append(variant.localInstrument).append("</Cd></LclInstrm>");
		// TODO SeqTp: FRST para el primer adeudo de un mandato. Hoy siempre RCUR.
		xml.append("<SeqTp>RCUR</SeqTp></PmtTpInf>");
		xml.append("<ReqdColltnDt>").append(iso(collectionDate)).append("</ReqdColltnDt>");

		// Acreedor
		xml.append("<Cdtr>");
		xml.append("<Nm>").append(clean(header.get(REGISTRY.NAME))).append("</Nm>");
		xml.append("<PstlAdr><Ctry>ES</Ctry>");
		appendAddressLine(xml, header.get(RADDRESS.ID), header.get(RADDRESS.STREET_TYPE),
				header.get(RADDRESS.ADDRESS), header.get(RADDRESS.NUMBER), header.get(RADDRESS.ZIP),
				header.get(RADDRESS.CITY), header.get(GEOZONE.ID), header.get(GEOZONE.NAME));
		xml.append("</PstlAdr>");
		xml.append("</Cdtr>");

		xml.append("<CdtrAcct><Id><IBAN>").append(header.get(RBANK.BANK_ACCOUNT)).append("</IBAN></Id></CdtrAcct>");
		xml.append("<CdtrAgt><FinInstnId><BIC>").append(header.get(RBANK.BIC)).append("</BIC></FinInstnId></CdtrAgt>");
		xml.append("<ChrgBr>SLEV</ChrgBr>");
		xml.append("<CdtrSchmeId><Id><PrvtId><Othr><Id>").append(creditorId)
		   .append("</Id><SchmeNm><Prtry>SEPA</Prtry></SchmeNm></Othr></PrvtId></Id></CdtrSchmeId>");

		for (Record d : details) appendTransaction(xml, d);

		xml.append("</PmtInf>");
	}

	private static void appendTransaction(StringBuilder xml, Record d) {
		String document = d.get(FINANCE.RDOCUMENT);
		Byte documentType = d.get(FINANCE.RDOCUMENT_TYPE);

		xml.append("<DrctDbtTxInf>");
		xml.append("<PmtId><EndToEndId>")
		   .append(d.get(FINANCE.ID)).append("/").append(document).append("/")
		   .append(compact(d.get(FINANCE.DUE_DATE)))
		   .append("</EndToEndId></PmtId>");
		xml.append("<InstdAmt Ccy=\"EUR\">").append(amount(d.get(FBATCH_DETAIL.AMOUNT))).append("</InstdAmt>");

		// El mandato se identifica con el registro de la factura.
		xml.append("<DrctDbtTx><MndtRltdInf>");
		xml.append("<MndtId>").append(d.get(INVOICE.REGISTRY)).append("</MndtId>");
		xml.append("<DtOfSgntr>").append(MANDATE_SIGNATURE_DATE).append("</DtOfSgntr>");
		xml.append("</MndtRltdInf></DrctDbtTx>");

		xml.append("<DbtrAgt><FinInstnId><BIC>").append(d.get(FINANCE.BIC)).append("</BIC></FinInstnId></DbtrAgt>");

		xml.append("<Dbtr>");
		xml.append("<Nm>").append(clean(d.get(FINANCE.RNAME))).append("</Nm>");
		xml.append("<PstlAdr><Ctry>ES</Ctry>");
		appendAddressLine(xml, d.get(RADDRESS.ID), d.get(RADDRESS.STREET_TYPE), d.get(RADDRESS.ADDRESS),
				d.get(RADDRESS.NUMBER), d.get(RADDRESS.ZIP), d.get(RADDRESS.CITY),
				d.get(GEOZONE.ID), d.get(GEOZONE.NAME));
		xml.append("</PstlAdr>");

		// Persona juridica -> OrgId; persona fisica -> PrvtId.
		boolean isCompany = documentType != null && documentType == (byte) 1;
		xml.append("<Id>").append(isCompany ? "<OrgId>" : "<PrvtId>").append("<Othr>");
		xml.append("<Id>").append(document).append("</Id>");
		xml.append("<Issr>").append(documentTypeName(documentType)).append("</Issr>");
		xml.append("</Othr>").append(isCompany ? "</OrgId>" : "</PrvtId>").append("</Id>");
		xml.append("</Dbtr>");

		xml.append("<DbtrAcct><Id><IBAN>").append(d.get(FINANCE.BANK_ACCOUNT)).append("</IBAN></Id></DbtrAcct>");

		String reference = d.get(INVOICE.REFERENCE_CODE);
		xml.append("<RmtInf><Ustrd>COBRO")
		   .append(AonStringUtils.isBlank(reference) ? "" : " Factura: " + clean(reference))
		   .append("</Ustrd></RmtInf>");

		xml.append("</DrctDbtTxInf>");
	}

	private static void appendAddressLine(StringBuilder xml, Integer addressId, String streetType,
			String street, String number, String zip, String city, Integer geozoneId, String geozoneName) {

		if (addressId == null) return;

		String line = clean(streetType) + ". " + clean(street) + " " + clean(number) + " "
				+ (zip == null ? "" : zip) + " " + clean(city)
				+ (geozoneId != null ? " (" + clean(geozoneName) + ")" : "");

		// AdrLine admite como maximo 70 caracteres; se parte en dos lineas (maximo 2).
		line = line.trim().replaceAll("\\s+", " ");
		if (line.length() <= 70) {
			xml.append("<AdrLine>").append(line).append("</AdrLine>");
		} else {
			int cut = line.lastIndexOf(' ', 70);
			if (cut <= 0) cut = 70;
			xml.append("<AdrLine>").append(line.substring(0, cut)).append("</AdrLine>");
			String rest = line.substring(cut).trim();
			xml.append("<AdrLine>").append(rest.length() > 70 ? rest.substring(0, 70) : rest).append("</AdrLine>");
		}
	}

	// ---------------------------------------------------------- IDENTIFICADORES

	/**
	 * Identificador de acreedor SEPA: ES + digitos de control + sufijo + NIF.
	 * Los digitos de control se calculan con ISO 7064 MOD 97-10 sobre el NIF + "ES00",
	 * SIN incluir el sufijo.
	 */
	public static String creditorId(String nif, String suffix) {
		String clean = nif.replaceAll("[^A-Za-z0-9]", "").toUpperCase();

		StringBuilder base = new StringBuilder();
		for (char c : (clean + "ES00").toCharArray())
			base.append(Character.isDigit(c) ? String.valueOf(c) : String.valueOf(c - 55));

		int mod = new BigInteger(base.toString()).mod(BigInteger.valueOf(97)).intValue();

		return "ES" + AonStringUtils.leftPad(String.valueOf(98 - mod), 2, '0')
			 + AonStringUtils.rightPad(suffix, 3, '0') + clean;
	}

	/**
	 * TODO PENDIENTE DE CONFIRMAR CON EL BANCO.
	 * Los ficheros historicos usan 000 / 500 / 700 segun el tipo, y la misma cuenta
	 * aparece con 500 y con 700, asi que el sufijo no puede salir solo de RBANK.SUFIX.
	 * Se asume por tipo, dejando RBANK.SUFIX para el 19-14 si esta informado.
	 */
	private static String resolveSuffix(String rbankSuffix, SepaChargeVariant variant) {
		if (SepaChargeVariant.CORE_19_14 == variant && AonStringUtils.isNotBlank(rbankSuffix))
			return rbankSuffix;
		return variant.defaultSuffix;
	}

	/** Formato historico: prefijo + id remesa (10) + yyyyMMdd + "22" + id registro (14). */
	private static String buildMsgId(SepaChargeVariant variant, Record header) {
		return variant.msgIdPrefix
			 + AonStringUtils.leftPad(header.get(FBATCH.ID).toString(), 10, '0')
			 + compact(header.get(FBATCH.ISSUE_DATE))
			 + "22"
			 + AonStringUtils.leftPad(header.get(ENTERPRISE.REGISTRY).toString(), 14, '0');
	}

	/** Formato historico: "A" + id remesa (10) + yyyyMMdd + "22" + NIF (14). */
	private static String buildPmtInfId(Record header) {
		return "A"
			 + AonStringUtils.leftPad(header.get(FBATCH.ID).toString(), 10, '0')
			 + compact(header.get(FBATCH.ISSUE_DATE))
			 + "22"
			 + AonStringUtils.leftPad(header.get(REGISTRY.DOCUMENT), 14, '0');
	}

	// ---------------------------------------------------------- UTILIDADES

	private static String iso(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	private static String compact(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	private static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/** Importe con 2 decimales y punto decimal, sin separador de miles. */
	private static String amount(double value) {
		return String.format(java.util.Locale.US, "%.2f", value);
	}

	/** Quita acentos y escapa los caracteres reservados de XML. */
	private static String clean(String input) {
		String s = SettleSalariesDAO.removeSpecialCharacters(input);
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&apos;");
	}

	private static String documentTypeName(Byte documentType) {
		if (documentType == null) return "No Censado";
		switch (documentType) {
			case (byte) 0: return "DNI";
			case (byte) 1: return "CIF";
			case (byte) 2: return "NIE";
			case (byte) 3: return "Pasaporte";
			default:       return "No Censado";
		}
	}

}