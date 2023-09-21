package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CarrierPackingDAO {
	
	private CarrierPackingDAO() {
	
	}
	
	private static final CarrierPackingPropertiesDAO CARRIER_PACKING_PROPERTIES = new CarrierPackingPropertiesDAO();
	
	public static class CarrierPackingPropertiesDAO implements CarrierPackingProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.STATUS);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.TYPE);}
		@Override public Property<Timestamp> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ISSUE_DATE);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER);}
		@Override public Property<Timestamp> getDeliveryDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DELIVERY_DATE);}
		@Override public Property<String> getCarrierReferenceProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER_REFERENCE);}
		@Override public Property<String> getNumberPlateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER_PLATE);}
		@Override public Property<String> getDriverNameProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_NAME);}
		@Override public Property<String> getDriverDocumentProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_DOCUMENT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_DATE);}
		@Override public Property<Double> getGrossWeightProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.GROSS);}
		@Override public Property<Double> getTareProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.TARE);}
		@Override public Property<Double> getNetProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NET);}
		@Override public Property<Timestamp> getReceptionStartDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.RECEPTION_START_DATE);}
		@Override public Property<Timestamp> getReceptionEndDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.RECEPTION_END_DATE);}
	}

	/*
	 * CARRIER PACKING 
	 */
	
	private static SelectConditionStep<Record> select(AONContext ctx, CarrierPackingFilter filter) {
		 return ctx.getDslContext().select()
			.from(CARRIER_PACKING)
			.where(CARRIER_PACKING_PROPERTIES.getConditions(filter));
	}

	public static CarrierPacking get(AONContext ctx, CarrierPackingFilter filter, Options... options){
		return select(ctx, filter).limit(1).fetch().stream().map(new CarrierPackingFiller())
			.findFirst().orElse(new CarrierPacking());
	}
	
	public static Stream<CarrierPacking> getStream(AONContext ctx, CarrierPackingFilter filter, Options... options){
		if(options.length > 0) 
			return getStream(ctx, filter, options[0]);
		return select(ctx, filter).fetch().stream().map(new CarrierPackingFiller());
	}
	
	private static Stream<CarrierPacking> getStream(AONContext ctx, CarrierPackingFilter filter, Options options){
		if(options.isPagination())
			return getStream(ctx, filter, options.getPage(), options.getPerPage());
		else return getStream(ctx, filter);
	}
	
	public static Stream<CarrierPacking> getStream(AONContext ctx, CarrierPackingFilter filter, Integer page, Integer perPage) {
		return select(ctx, filter)
			.orderBy(CARRIER_PACKING.ISSUE_DATE.desc())
			.limit(perPage).offset(perPage * (page -1))
			.fetch().stream().map(new CarrierPackingFiller()); 
	}
	
	public static List<CarrierPacking> getList(AONContext ctx, CarrierPackingFilter filter, Options... options) {
		return getStream(ctx, filter, options)
			.collect(Collectors.toList());
	}


	public static int getNextNumber(AONContext ctx, String series ) {
		Integer next = ctx.getDslContext()
			.select( DSL.max(CARRIER_PACKING.NUMBER))
			.from(CARRIER_PACKING)
			.where(CARRIER_PACKING.DOMAIN.eq(ctx.getDomainId()))
			.and(AonStringUtils.isBlank(series)
					? CARRIER_PACKING.SERIES.isNull().or(DSL.trim(CARRIER_PACKING.SERIES).eq(""))
					: CARRIER_PACKING.SERIES.eq(series))
			.fetch()
			.stream()
			.mapToInt(rec -> (rec != null && rec.getValue(DSL.max(CARRIER_PACKING.NUMBER)) != null) 
					? rec.getValue(DSL.max(CARRIER_PACKING.NUMBER)) : 0)
			.findFirst()
			.orElse(0);
		if(next < 0) next = 0;
		return ++next;
	}
	
	/*
	 * FILLERS 
	 */

	public static class CarrierPackingFiller extends Filler implements Function<Record, CarrierPacking> {
		@Override
		public CarrierPacking apply(Record r) {
			return new CarrierPacking()
					.setCarrier(r.getValue(CARRIER_PACKING.CARRIER))
					.setCarrierReference(r.getValue(CARRIER_PACKING.CARRIER_REFERENCE))
					.setCreationDate(r.getValue(CARRIER_PACKING.CREATION_DATE))
					.setCreationUser(r.getValue(CARRIER_PACKING.CREATION_USER))
					.setDeliveryDate(r.getValue(CARRIER_PACKING.DELIVERY_DATE))
					.setDomain(r.getValue(CARRIER_PACKING.DOMAIN))
					.setDriverDocument(r.getValue(CARRIER_PACKING.DRIVER_DOCUMENT))
					.setDriverName(r.getValue(CARRIER_PACKING.DRIVER_NAME))
					.setId(r.getValue(CARRIER_PACKING.ID))
					.setIssueDate(r.getValue(CARRIER_PACKING.ISSUE_DATE))
					.setModificationDate(r.getValue(CARRIER_PACKING.MODIFICATION_DATE))
					.setModificationUser(r.getValue(CARRIER_PACKING.MODIFICATION_USER))
					.setNumber(r.getValue(CARRIER_PACKING.NUMBER))
					.setNumberPlate(r.getValue(CARRIER_PACKING.NUMBER_PLATE))
					.setSeries(r.getValue(CARRIER_PACKING.SERIES))
					.setStatus(CarrierPackingStatus.safeValueOf(r.getValue(CARRIER_PACKING.STATUS)))
					.setType(CarrierPackingType.safeValueOf(r.getValue(CARRIER_PACKING.TYPE)))
					
					.setCarrierName(r.getValue(REGISTRY.NAME))
					.setComments(r.getValue(CARRIER_PACKING.COMMENTS))
					
					.setGross(r.getValue(CARRIER_PACKING.GROSS))
					.setTare(r.getValue(CARRIER_PACKING.TARE))
					.setAdditionalTare(r.getValue(CARRIER_PACKING.ADDITIONAL_TARE))
					.setNet(r.getValue(CARRIER_PACKING.NET))						
					.setReceptionStartDate(r.getValue(CARRIER_PACKING.RECEPTION_START_DATE))
					.setReceptionEndDate(r.getValue(CARRIER_PACKING.RECEPTION_END_DATE))
					;
		}
	}

	
}
