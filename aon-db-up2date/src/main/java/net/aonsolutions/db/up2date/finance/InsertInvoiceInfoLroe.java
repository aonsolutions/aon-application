package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.jooq.tables.DataResponse;
import com.esferalia.aon.jooq.tables.DataResponseDetail;
import com.esferalia.aon.jooq.tables.Invoice;
import com.esferalia.aon.jooq.tables.InvoiceInfo;

import net.aonsolutions.db.up2date.Update;

public class InsertInvoiceInfoLroe implements Update {

	public static final InsertInvoiceInfoLroe INSERT_INVOICE_INFO_LROE = new InsertInvoiceInfoLroe();

	private InsertInvoiceInfoLroe() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");

		HashMap<Integer, Communication> com = new HashMap<>();
		
		dslContext.select().from(DataResponse.DATA_RESPONSE).where(DataResponse.DATA_RESPONSE.SOURCE.eq((byte) 27)).fetch().stream().forEach(r -> {
			Integer dr = r.getValue(DataResponse.DATA_RESPONSE.ID);
			Integer domain = r.getValue(DataResponse.DATA_RESPONSE.DOMAIN);
			Integer invoice = r.getValue(DataResponse.DATA_RESPONSE.SOURCE_ID);
			Date date = r.getValue(DataResponse.DATA_RESPONSE.RESPONSE_DATE);
			String creation_user = r.getValue(DataResponse.DATA_RESPONSE.CREATION_USER);
			System.out.println("*********************");
			System.out.println("Data Response: " + dr);
			System.out.println("Domain: " + domain);
			System.out.println("Invioce: " + invoice);
			
			long count = dslContext.select(Invoice.INVOICE.ID).from(Invoice.INVOICE).where(Invoice.INVOICE.ID.eq(invoice)).fetch().stream().count();
			
			if(count > 0) {
				String jsonStr = dslContext.select(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE)
						.from(DataResponseDetail.DATA_RESPONSE_DETAIL)
						.where(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(dr))
						.and(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("json"))
						.fetch().stream().map(re-> re.getValue(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE))
						.findFirst().orElse("{}");
				System.out.println(jsonStr);
				String infoStr = dslContext.select(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE)
						.from(DataResponseDetail.DATA_RESPONSE_DETAIL)
						.where(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(dr))
						.and(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("info"))
						.fetch().stream().map(re-> re.getValue(DataResponseDetail.DATA_RESPONSE_DETAIL.DATA_VALUE))
						.findFirst().orElse("{}");
				System.out.println(infoStr);

				try {
					JSONParser parser = new JSONParser();
					JSONObject json =  (JSONObject) parser.parse(jsonStr);
					JSONObject info =  (JSONObject) parser.parse(infoStr);
				
					String op = (String) info.get("operacion");
					byte status = 0;
					byte operation = 0;
					if("A_00".equalsIgnoreCase(op) || "A_01".equalsIgnoreCase(op)) {
						boolean error = (boolean) json.getOrDefault("error", false);
						String lroeType = (String) json.get("eus-bizkaia-n3-tipo-respuesta");
						if(!error && !"Incorrecto".equalsIgnoreCase(lroeType)) {
							status = 1;
							if(com.containsKey(invoice))
								com.get(invoice).setSent(dr);
							else com.put(invoice, new Communication().setDomain(domain).setSent(dr));
						} else {
							status = 3;
							if(com.containsKey(invoice))
								com.get(invoice).setWrong(dr);
							else com.put(invoice, new Communication().setDomain(domain).setWrong(dr));
						}
					} else if("AN_0".equalsIgnoreCase(op)) {
						operation = 2;
						boolean error = (boolean) json.getOrDefault("error", false);
						String lroeType = (String) json.get("eus-bizkaia-n3-tipo-respuesta");
						if(!error && !"Incorrecto".equalsIgnoreCase(lroeType)) {
							if(com.containsKey(invoice))
								com.get(invoice).setAnnuled(dr);
							else com.put(invoice, new Communication().setDomain(domain).setAnnuled(dr));					
						} 
					}
					Integer invoiceBatch = dslContext.select(INVOICE_BATCH.ID).from(INVOICE_BATCH).where(INVOICE_BATCH.DATA_RESPONSE.eq(dr)).fetch().stream().map(rib -> rib.getValue(INVOICE_BATCH.ID)).findFirst().orElse(null);
					if(invoiceBatch == null) {
						invoiceBatch = dslContext.insertInto(INVOICE_BATCH)
								.set(INVOICE_BATCH.DOMAIN, domain)
								.set(INVOICE_BATCH.DATE, new Timestamp(date.getTime()))
								.set(INVOICE_BATCH.TYPE, (byte) 0)
								.set(INVOICE_BATCH.OPERATION, operation)
								.set(INVOICE_BATCH.DATA_RESPONSE, dr)
								.set(INVOICE_BATCH.CREATION_USER, creation_user)
								.returning(INVOICE_BATCH.ID).fetchOne().getId();
						System.out.println("INSERT INTO INVOICE_BATCH - " + invoiceBatch);
					}
					
					Integer invoiceBatchDetail = dslContext.select(INVOICE_BATCH_DETAIL.ID).from(INVOICE_BATCH_DETAIL).where(INVOICE_BATCH_DETAIL.INVOICE.eq(invoice))
							.and(INVOICE_BATCH_DETAIL.INVOICE_BATCH.eq(invoiceBatch))
							.fetch().stream().map(rib -> rib.getValue(INVOICE_BATCH.ID)).findFirst().orElse(null);
					if(invoiceBatchDetail == null) {
						invoiceBatchDetail = dslContext.insertInto(INVOICE_BATCH_DETAIL)
								.set(INVOICE_BATCH_DETAIL.DOMAIN, domain)
								.set(INVOICE_BATCH_DETAIL.INVOICE, invoice)
								.set(INVOICE_BATCH_DETAIL.INVOICE_BATCH, invoiceBatch)
								.set(INVOICE_BATCH_DETAIL.STATUS, status)
								.returning(INVOICE_BATCH_DETAIL.ID).fetchOne().getId();
						System.out.println("INSERT INTO INVOICE_BATCH_DETAIL - " + invoiceBatchDetail);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println(com.size());
		for (Integer key : com.keySet()) {
			boolean annulled = com.get(key).getAnnuled() != null;
			boolean accepted = com.get(key).getSent() != null;
			boolean wrong = com.get(key).getWrong() != null;
			byte status = 0;
			if(annulled) status = 4;
			else if(accepted) status = 1;
			else if(wrong) status = 3;
			
			Integer invoiceInfo = dslContext.select(InvoiceInfo.INVOICE_INFO.ID).from(InvoiceInfo.INVOICE_INFO).where(InvoiceInfo.INVOICE_INFO.INVOICE.eq(key))
					.fetch().stream().map(rib -> rib.getValue(InvoiceInfo.INVOICE_INFO.ID)).findFirst().orElse(null);
			if (invoiceInfo == null) {
				dslContext.insertInto(InvoiceInfo.INVOICE_INFO)
				.set(InvoiceInfo.INVOICE_INFO.DOMAIN, com.get(key).getDomain())
				.set(InvoiceInfo.INVOICE_INFO.INVOICE, key)
				.set(InvoiceInfo.INVOICE_INFO.TYPE, (byte) 0)
				.set(InvoiceInfo.INVOICE_INFO.STATUS, status)
				.execute();
				System.out.println("INSERT INTO INVOICE_INFO");
			}

		}
		
		System.out.println("[END]");
	}

	public class Communication {
		Integer domain;
		Integer sent;
		Integer annuled;
		Integer wrong;
		
		public Integer getDomain() {
			return domain;
		}
		
		public Communication setDomain(Integer domain) {
			this.domain = domain;
			return this;
		}
		
		public Integer getSent() {
			return sent;
		}
		public Communication setSent(Integer sent) {
			this.sent = sent;
			return this;
		}
		
		public Integer getAnnuled() {
			return annuled;
		}
		public Communication setAnnuled(Integer annuled) {
			this.annuled = annuled;
			return this;
		}
		
		public Integer getWrong() {
			return wrong;
		}
		public Communication setWrong(Integer wrong) {
			this.wrong = wrong;
			return this;
		}
	}
	
}
