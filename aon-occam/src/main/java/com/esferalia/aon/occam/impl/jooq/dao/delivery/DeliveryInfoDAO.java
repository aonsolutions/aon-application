package com.esferalia.aon.occam.impl.jooq.dao.delivery;

import static com.esferalia.aon.jooq.tables.DeliveryInfo.DELIVERY_INFO;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.DeliveryInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.DeliveryInfoProperties;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryCommunicationStatus;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryCommunicationType;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryInfo;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DeliveryInfoDAO {
	
	private DeliveryInfoDAO() {
	
	}
	
	private static final DeliveryInfoPropertiesDAO DELIVERY_INFO_PROPERTIES = new DeliveryInfoPropertiesDAO();
	public static class DeliveryInfoPropertiesDAO implements DeliveryInfoProperties {
		
		public Condition[] getConditions(DeliveryInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_INFO.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(DELIVERY_INFO.DOMAIN);}
		@Override public Property<Integer> getDeliveryProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_INFO.DELIVERY);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_INFO.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_INFO.STATUS);}
	}
	
	
	public static SelectConditionStep<Record> select(AONContext ctx, DeliveryInfoFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(DELIVERY_INFO)
				.where(DELIVERY_INFO_PROPERTIES.getConditions(filter));
	}
	
	public static DeliveryInfo get(AONContext ctx, DeliveryInfoFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new DeliveryInfoFiller())
			.findFirst().orElse(new DeliveryInfo());
	}
	
	public static DeliveryInfo save(AONContext ctx, DeliveryInfo deliveryInfo) {
		DeliveryInfoValidation.autoComplete(ctx, deliveryInfo);
		DeliveryInfoValidation.validate(ctx, deliveryInfo);
		
		return deliveryInfo.getId() != null 
			? update(ctx, deliveryInfo)
			: insert(ctx, deliveryInfo);
	}
	
	public static DeliveryInfo update(AONContext ctx, DeliveryInfo deliveryInfo) {
		ctx.getDslContext().update(DELIVERY_INFO)
		.set(DELIVERY_INFO.DOMAIN, deliveryInfo.getDomain())
		.set(DELIVERY_INFO.DELIVERY, deliveryInfo.getDelivery())
		.set(DELIVERY_INFO.TYPE, deliveryInfo.getType().value())
		.set(DELIVERY_INFO.STATUS, deliveryInfo.getStatus().value())
		.where(DELIVERY_INFO.ID.eq(deliveryInfo.getId()))
		.execute();
		return deliveryInfo;
	}
	
	public static DeliveryInfo insert(AONContext ctx, DeliveryInfo deliveryInfo) {
		Integer id = ctx.getDslContext().insertInto(DELIVERY_INFO)
				.set(DELIVERY_INFO.DOMAIN, deliveryInfo.getDomain())
				.set(DELIVERY_INFO.DELIVERY, deliveryInfo.getDelivery())
				.set(DELIVERY_INFO.TYPE, deliveryInfo.getType().value())
				.set(DELIVERY_INFO.STATUS, deliveryInfo.getStatus().value())
			.returning(DELIVERY_INFO.ID).fetchOne().getId();
		return deliveryInfo.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, DeliveryInfoFilter filter){
		ctx.getDslContext().delete(DELIVERY_INFO)
		.where(DELIVERY_INFO_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class DeliveryInfoFiller extends Filler implements Function<Record, DeliveryInfo> {

		@Override
		public DeliveryInfo apply(Record r) {
			return build(r);
		}
		
		public static DeliveryInfo build(Record r) {
			return new DeliveryInfo()
				.setId(r.getValue(DELIVERY_INFO.ID))
				.setDomain(r.getValue(DELIVERY_INFO.DOMAIN))
				.setDelivery(r.getValue(DELIVERY_INFO.DELIVERY))
				.setType(DeliveryCommunicationType.safeValueOf(r.getValue(DELIVERY_INFO.TYPE)))
				.setStatus(DeliveryCommunicationStatus.safeValueOf(r.getValue(DELIVERY_INFO.STATUS)));
		}
	}
	
	private static class DeliveryInfoValidation {
		
		private DeliveryInfoValidation() {
	
		}
		
		public static final BiConsumer<AONContext, DeliveryInfo> EMPTY_DOMAIN = (ctx, deliveryInfo) -> {
			if (deliveryInfo.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext, DeliveryInfo> EMPTY_DELIVERY = (ctx, deliveryInfo) -> {
			if (deliveryInfo.getDelivery() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("delivery")) ;
		};
		
		public static final BiConsumer<AONContext, DeliveryInfo> EMPTY_TYPE = (ctx, deliveryInfo) -> {
			if (deliveryInfo.getType() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("type")) ;
		};
		
		public static final BiConsumer<AONContext, DeliveryInfo> EMPTY_STATUS = (ctx, deliveryInfo) -> {
			if (deliveryInfo.getStatus() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("status")) ;
		};
		
		public static final BiConsumer<AONContext, DeliveryInfo> NOT_EXIST_DELIVERY = (ctx, deliveryInfo) -> {
			Delivery delivery = DeliveryDAO.get(ctx, deliveryInfo.getDelivery());
			if (delivery.getId() == null) 
				throw new AonCoreException(AonError.NOT_EXIST.format("delivery")) ;
		};
		
		public static void validate(AONContext ctx, DeliveryInfo deliveryInfo) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_DELIVERY)
				.andThen(EMPTY_TYPE)
				.andThen(EMPTY_STATUS)
				.andThen(NOT_EXIST_DELIVERY)
				.accept(ctx, deliveryInfo);
		}
		
		public static final BiConsumer<AONContext, DeliveryInfo> COMPLETE_STATUS = (ctx, deliveryInfo) -> {
			if(deliveryInfo.getStatus() == null) {
				deliveryInfo.setStatus(DeliveryCommunicationStatus.PENDING);
			}
		};

		public static void autoComplete(AONContext ctx, DeliveryInfo deliveryInfo) throws AonCoreException {
			COMPLETE_STATUS
			.accept(ctx, deliveryInfo);

		}
		
	}
}
