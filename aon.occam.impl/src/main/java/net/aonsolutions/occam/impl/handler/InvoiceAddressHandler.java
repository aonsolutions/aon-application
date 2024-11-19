package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.InvoiceAddress.INVOICE_ADDRESS;

import java.util.Optional;

import org.jooq.Record;

import net.aonsolutions.occam.api.model.Geozone;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceAddress;
import net.aonsolutions.occam.api.model.type.StreetType;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.GeozoneHandler.GeozoneFiller;

class InvoiceAddressHandler {
	private static final com.esferalia.aon.jooq.tables.Geozone PARENT = GEOZONE.as("parentGeozone");
	private static final com.esferalia.aon.jooq.tables.Geozone CHILD = GEOZONE.as("childGeozone");

	
	private InvoiceAddressHandler() {
	
	}
	
	static Invoice fillInvoice(AONContext ctx, Invoice invoice) {
		get(ctx, invoice.getId())
			.ifPresent( invoice::setInvoiceAddress );
		return invoice;
	}
	
	private static Optional<InvoiceAddress> get(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext()
			.select()
			.from(INVOICE_ADDRESS)
			.leftOuterJoin(CHILD).on(CHILD.ID.eq(INVOICE_ADDRESS.GEOZONE))
			.leftOuterJoin(GEOTREE).on(GEOTREE.CHILD.eq(INVOICE_ADDRESS.GEOZONE))
			.leftOuterJoin(PARENT).on(PARENT.ID.eq(GEOTREE.PARENT))
			.where(INVOICE_ADDRESS.INVOICE.eq(invoiceId))
			.limit(1)
			.fetch()
			.stream()
			.map(new InvoiceAddressFiller())
			.findFirst();
	}
	
	static Invoice save(AONContext ctx, Invoice invoice) {
		Optional<InvoiceAddress> opt = invoice.getInvoiceAddress();
		if (opt.isPresent()) {
			InvoiceAddress invoiceAddress = opt.get(); 
			if (invoiceAddress.getId() == null) {
				insert(ctx, invoice, invoiceAddress);
			} else {
				update(ctx, invoice, invoiceAddress);
			}
		}
		return invoice;
	}
	
	private static InvoiceAddress update(AONContext ctx, Invoice invoice, InvoiceAddress address) {
		ctx.getDslContext().update(INVOICE_ADDRESS)
			.set(INVOICE_ADDRESS.DOMAIN, invoice.getDomain())
			.set(INVOICE_ADDRESS.INVOICE, invoice.getId() )
			.set(INVOICE_ADDRESS.STREET_TYPE, StreetType.value(address.getStreetType()))
			.set(INVOICE_ADDRESS.ADDRESS, address.getAddress())
			.set(INVOICE_ADDRESS.NUMBER, address.getNumber())
			.set(INVOICE_ADDRESS.ADDRESS2, address.getAddress2())
			.set(INVOICE_ADDRESS.ZIP, address.getZip())
			.set(INVOICE_ADDRESS.CITY, address.getCity())
			.set(INVOICE_ADDRESS.PROVINCE, address.getProvince())
			.set(INVOICE_ADDRESS.GEOZONE, address.getGeozone().map( Geozone::getId ).orElse(null))
			.where(INVOICE_ADDRESS.ID.eq(address.getId()))
			.execute();
		return address;
	}
	
	private static InvoiceAddress insert(AONContext ctx, Invoice invoice, InvoiceAddress address) {
		Integer id = ctx.getDslContext().insertInto(INVOICE_ADDRESS)
				.set(INVOICE_ADDRESS.DOMAIN, address.getDomain())
				.set(INVOICE_ADDRESS.INVOICE, invoice.getId() )
				.set(INVOICE_ADDRESS.STREET_TYPE, StreetType.value(address.getStreetType()))
				.set(INVOICE_ADDRESS.ADDRESS, address.getAddress())
				.set(INVOICE_ADDRESS.NUMBER, address.getNumber())
				.set(INVOICE_ADDRESS.ADDRESS2, address.getAddress2())
				.set(INVOICE_ADDRESS.ZIP, address.getZip())
				.set(INVOICE_ADDRESS.CITY, address.getCity())
				.set(INVOICE_ADDRESS.PROVINCE, address.getProvince())
				.set(INVOICE_ADDRESS.GEOZONE, address.getGeozone().map( Geozone::getId ).orElse(null))
			.returning(INVOICE_ADDRESS.ID)
			.fetchOne()
			.getId();
		return address.setId(id);
	}	

	static void delete(AONContext ctx, Integer invoice){
		ctx.getDslContext().delete(INVOICE_ADDRESS)
			.where(INVOICE_ADDRESS.INVOICE.eq(invoice))
			.execute();
	}
	
	static class InvoiceAddressFiller extends Filler<InvoiceAddress> {

		@Override
		public InvoiceAddress apply(Record r) {
			return build(r);
		}
		
		static InvoiceAddress build(Record r) {
			return build(r, PARENT, CHILD);
		}
		
		static InvoiceAddress build(Record r
				, com.esferalia.aon.jooq.tables.Geozone parent
				, com.esferalia.aon.jooq.tables.Geozone child) {
			if (isNull(r, INVOICE_ADDRESS.ID)) return null;
			return new InvoiceAddress()
				.setId(getValue(r, INVOICE_ADDRESS.ID))
				.setDomain(getValue(r, INVOICE_ADDRESS.DOMAIN))
				.setStreetType(StreetType.value(getValue(r, INVOICE_ADDRESS.STREET_TYPE)).orElse(null))
				.setAddress(getValue(r, INVOICE_ADDRESS.ADDRESS))
				.setNumber(getValue(r, INVOICE_ADDRESS.NUMBER))
				.setAddress2(getValue(r, INVOICE_ADDRESS.ADDRESS2))
				.setZip(getValue(r, INVOICE_ADDRESS.ZIP))
				.setCity(getValue(r, INVOICE_ADDRESS.CITY))
				.setProvince(getValue(r, INVOICE_ADDRESS.PROVINCE))
				.setGeozone(GeozoneFiller.build(r, child))
				.setParent(GeozoneFiller.build(r, parent))
				.markAsClean();
		}
		
	}

}
