package net.aonsolutions.db.up2date.management;

import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Sales.SALES;
import static com.esferalia.aon.jooq.tables.SalesDetail.SALES_DETAIL;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UdapaSalesFix implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(UdapaSalesFix.class.getName());

	public static final UdapaSalesFix UDAPA_SALES_FIX = new UdapaSalesFix();
	
	public static final String DOMAIN_NAME = "udapa.aonsolutions.net";
	public static final Integer DOMAIN_ID = 3049;
	public static final String INGENET = "ingenet";

	class DeliveryDetail {
		Integer salesDetail;
		Double quantity;

		public Integer getSalesDetail() {
			return salesDetail;
		}

		public DeliveryDetail setSalesDetail(Integer salesDetail) {
			this.salesDetail = salesDetail;
			return this;
		}

		public Double getQuantity() {
			return quantity;
		}

		public DeliveryDetail setQuantity(Double quantity) {
			this.quantity = quantity;
			return this;
		}
		
	}
	
	class SalesDetail {
		Integer id;
		Integer sales;
		Double quantity;
		Double delivered;
		Byte status;
		
		public Integer getId() {
			return id;
		}
		
		public SalesDetail setId(Integer id) {
			this.id = id;
			return this;
		}
		
		public Integer getSales() {
			return sales;
		}
		
		public SalesDetail setSales(Integer sales) {
			this.sales = sales;
			return this;
		}
		
		public Double getQuantity() {
			return quantity;
		}
		
		public SalesDetail setQuantity(Double quantity) {
			this.quantity = quantity;
			return this;
		}

		public Double getDelivered() {
			return delivered;
		}
		
		public SalesDetail setDelivered(Double delivered) {
			this.delivered = delivered;
			return this;
		}
		
		public Byte getStatus() {
			return status;
		}
		
		public SalesDetail setStatus(Byte status) {
			this.status = status;
			return this;
		}
		
	}
	
	private UdapaSalesFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Arreglo pedidos de Udapa");
		
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.NAME.eq(DOMAIN_NAME)).limit(1).fetch().stream().map(r -> r.getValue(DOMAIN.ID)).findFirst().orElse(null);
		if(domainId != null && DOMAIN_ID.equals(domainId)) {
			manageSalesDetail(dslContext);
		}
	}
	
	private void manageSalesDetail(DSLContext dslContext) {
		getDeliveries(dslContext).forEach(delivery -> {
			getDeliveryDetails(dslContext, delivery)
			.filter(d->d.getSalesDetail()!=null)
			.collect(Collectors.groupingBy(DeliveryDetail::getSalesDetail,
					Collectors.summingDouble(DeliveryDetail::getQuantity)))
			.forEach((salesDetailId, totalQuantity) -> {
				SalesDetail salesDetail = getSalesDetail(dslContext, salesDetailId);
				if(salesDetail!=null){
					Double delivered = salesDetail.getDelivered();
					delivered += totalQuantity;
					salesDetail.setDelivered(delivered);
					if(delivered>0.0 && delivered<=salesDetail.getQuantity()) {
						if(delivered<salesDetail.getQuantity()) {
							salesDetail.setStatus((byte) 1);
						} else {
							salesDetail.setStatus((byte) 2);
						}
					}
					updateSalesDetail(dslContext, salesDetail);
					manageSales(dslContext, salesDetail.getSales());
				}
			});
			return;
		});
	}
	
	private void manageSales(DSLContext dslContext, Integer sales) {
		Double totalSalesPending = getSalesDetails(dslContext, sales)
				.mapToDouble(o->o.getQuantity()-o.getDelivered()).sum();
		if(totalSalesPending==0.0){
			updateSales(dslContext, sales);
		}
	}
	
	private Stream<Integer> getDeliveries(DSLContext dslContext) {
		Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.DAY_OF_MONTH, 6);
        c1.set(Calendar.MONTH, 3);
        c1.set(Calendar.YEAR, 2022);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);

        Timestamp date1 = new Timestamp(c1.getTimeInMillis());
        
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.DAY_OF_MONTH, 8);
        c2.set(Calendar.MONTH, 3);
        c2.set(Calendar.YEAR, 2022);
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        
        Timestamp date2 = new Timestamp(c2.getTimeInMillis());

		return dslContext.select()
			.from(DELIVERY)
			.where(DELIVERY.DOMAIN.eq(DOMAIN_ID))
			.and(DELIVERY.CREATION_USER.eq(INGENET))
			.and(DELIVERY.REMARKS.like("Creado por 'ingenet'%"))
			.and(DELIVERY.CREATION_DATE.between(date1, date2))
			.fetch().stream().map(r -> r.getValue(DELIVERY.ID));
	}
	
	private Stream<DeliveryDetail> getDeliveryDetails(DSLContext dslContext, Integer delivery) {
		return dslContext.select()
			.from(DELIVERY_DETAIL)
			.where(DELIVERY_DETAIL.DOMAIN.eq(DOMAIN_ID))
			.and(DELIVERY_DETAIL.DELIVERY.eq(delivery))
			.fetch().stream().map(r -> new DeliveryDetail()
					.setQuantity(r.getValue(DELIVERY_DETAIL.QUANTITY))
					.setSalesDetail(r.getValue(DELIVERY_DETAIL.SALES_DETAIL)));
	}
	
	private Stream<SalesDetail> getSalesDetails(DSLContext dslContext, Integer sales) {
		return dslContext.select()
			.from(SALES_DETAIL)
			.where(SALES_DETAIL.DOMAIN.eq(DOMAIN_ID))
			.and(SALES_DETAIL.SALES.eq(sales))
			.fetch().stream().map(r -> new SalesDetail()
					.setId(r.getValue(SALES_DETAIL.ID))
					.setSales(r.getValue(SALES_DETAIL.SALES))
					.setStatus(r.getValue(SALES_DETAIL.STATUS))
					.setQuantity(r.getValue(SALES_DETAIL.QUANTITY))
					.setDelivered(r.getValue(SALES_DETAIL.DELIVERED)));
	}
	
	private SalesDetail getSalesDetail(DSLContext dslContext, Integer salesDetailId) {
		return dslContext.select()
			.from(SALES_DETAIL)
			.where(SALES_DETAIL.DOMAIN.eq(DOMAIN_ID))
			.and(SALES_DETAIL.ID.eq(salesDetailId))
			.fetch().stream().map(r -> new SalesDetail()
					.setId(r.getValue(SALES_DETAIL.ID))
					.setSales(r.getValue(SALES_DETAIL.SALES))
					.setStatus(r.getValue(SALES_DETAIL.STATUS))
					.setQuantity(r.getValue(SALES_DETAIL.QUANTITY))
					.setDelivered(r.getValue(SALES_DETAIL.DELIVERED)))
			.findFirst().orElse(null);
	}
	
	private void updateSalesDetail(DSLContext dslContext, SalesDetail salesDetail) {	
		dslContext.update(SALES_DETAIL)
		.set(SALES_DETAIL.DELIVERED, salesDetail.getDelivered())
		.set(SALES_DETAIL.STATUS, salesDetail.getStatus())
		.where(SALES_DETAIL.ID.eq(salesDetail.getId()))
		.execute();
	}
	
	private void updateSales(DSLContext dslContext, Integer sales) {	
		dslContext.update(SALES)
		.set(SALES.STATUS, (byte) 2)
		.where(SALES.ID.eq(sales))
		.execute();
	}

}
