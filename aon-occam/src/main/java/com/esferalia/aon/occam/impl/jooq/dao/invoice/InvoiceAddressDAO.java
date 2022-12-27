package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.InvoiceAddress.INVOICE_ADDRESS;

import java.util.function.Function;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.Geozone;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO.GeoZoneFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;

public class InvoiceAddressDAO {
	
	private InvoiceAddressDAO() {
	
	}
	
	public static RegistryAddress get(AONContext ctx, Integer invoiceId) {
		com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
		com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");

		return ctx.getDslContext()
			.select()
			.from(INVOICE_ADDRESS)
			.leftOuterJoin(child).on(child.ID.eq(INVOICE_ADDRESS.GEOZONE))
			.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(INVOICE_ADDRESS.GEOZONE))
			.leftOuterJoin(parent).on(parent.ID.eq(GEOTREE.PARENT))
			.where(INVOICE_ADDRESS.INVOICE.eq(invoiceId))
			.limit(1).fetch().stream().map(new InvoiceAddressFiller())
			.findFirst().orElse(new RegistryAddress());
	}
	
	public static RegistryAddress get(AONContext ctx, Invoice invoice) {
		RegistryAddress invoiceAddress = get(ctx, invoice.getId());
		return invoiceAddress.isEmpty() 
				? RegistryAddressDAO.get(ctx, invoice.getRegistryAddress())
				: invoiceAddress;
	}
	
	public static RegistryAddress save(AONContext ctx, RegistryAddress address, Integer invoice) {
		RegistryAddress invoiceAddress = get(ctx, invoice);
		return invoiceAddress.isEmpty()
			? insert(ctx, address, invoice)
			: update(ctx, address, invoice);
	}
	
	public static RegistryAddress update(AONContext ctx, RegistryAddress address, Integer invoice) {
		ctx.getDslContext().update(INVOICE_ADDRESS)
		.set(INVOICE_ADDRESS.DOMAIN, address.getDomain())
		.set(INVOICE_ADDRESS.INVOICE, invoice)
		.set(INVOICE_ADDRESS.STREET_TYPE, address.getStreetType().getAeatCode())
		.set(INVOICE_ADDRESS.ADDRESS, address.getAddress())
		.set(INVOICE_ADDRESS.NUMBER, address.getNumber())
		.set(INVOICE_ADDRESS.ADDRESS2, address.getAddress2())
		.set(INVOICE_ADDRESS.ZIP, address.getZip())
		.set(INVOICE_ADDRESS.CITY, address.getCity())
		.set(INVOICE_ADDRESS.PROVINCE, address.getProvince())
		.set(INVOICE_ADDRESS.GEOZONE, address.getGeozone())
		.where(INVOICE_ADDRESS.ID.eq(address.getId()))
		.execute();
		return address;
	}
	
	public static RegistryAddress insert(AONContext ctx, RegistryAddress address, Integer invoice) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_ADDRESS)
				.set(INVOICE_ADDRESS.DOMAIN, address.getDomain())
				.set(INVOICE_ADDRESS.INVOICE, invoice)
				.set(INVOICE_ADDRESS.STREET_TYPE, address.getStreetType() != null
							? address.getStreetType().getAeatCode() : null)
				.set(INVOICE_ADDRESS.ADDRESS, address.getAddress())
				.set(INVOICE_ADDRESS.NUMBER, address.getNumber())
				.set(INVOICE_ADDRESS.ADDRESS2, address.getAddress2())
				.set(INVOICE_ADDRESS.ZIP, address.getZip())
				.set(INVOICE_ADDRESS.CITY, address.getCity())
				.set(INVOICE_ADDRESS.PROVINCE, address.getProvince())
				.set(INVOICE_ADDRESS.GEOZONE, address.getGeozone())
			.returning(INVOICE_ADDRESS.ID).fetchOne().getId();
		return address.setId(id);
	}	

	public static void delete(AONContext ctx, Integer invoice){
		ctx.getDslContext().delete(INVOICE_ADDRESS)
		.where(INVOICE_ADDRESS.INVOICE.eq(invoice))
		.execute();
	}
	
	public static class InvoiceAddressFiller implements Function<Record, RegistryAddress> {

		@Override
		public RegistryAddress apply(Record r) {
			return build(r);
		}
		
		public static RegistryAddress build(Record r) {
			com.esferalia.aon.jooq.tables.Geozone parent = GEOZONE.as("parentGeozone");
			com.esferalia.aon.jooq.tables.Geozone child = GEOZONE.as("childGeozone");
			return build(r, parent, child);
		}
		
		public static RegistryAddress build(Record r, Geozone parent, Geozone child) {
			return new RegistryAddress()
					.setId(r.getValue(INVOICE_ADDRESS.ID))
					.setDomain(r.getValue(INVOICE_ADDRESS.DOMAIN))
					.setStreetType(StreetType.safeValueOf(r.getValue(INVOICE_ADDRESS.STREET_TYPE)))
					.setAddress(r.getValue(INVOICE_ADDRESS.ADDRESS))
					.setNumber(r.getValue(INVOICE_ADDRESS.NUMBER))
					.setAddress2(r.getValue(INVOICE_ADDRESS.ADDRESS2))
					.setZip(r.getValue(INVOICE_ADDRESS.ZIP))
					.setCity(r.getValue(INVOICE_ADDRESS.CITY))
					.setProvince(r.getValue(INVOICE_ADDRESS.PROVINCE))
					.setGeozone(r.getValue(INVOICE_ADDRESS.GEOZONE))
					.setGeozoneCode(r.getValue(child.CODE))
					.setGeozoneName(r.getValue(child.NAME))
					.setChild(GeoZoneFiller.build(r, child))
					.setParent(GeoZoneFiller.build(r, parent))
					.setDirty(false);
		}
	}
}
