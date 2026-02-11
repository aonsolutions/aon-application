package com.esferalia.aon.occam.impl.jooq.dao.calendar;

import static com.esferalia.aon.jooq.tables.Holiday.HOLIDAY;
import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.HolidayFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.HolidayProperties;
import com.esferalia.aon.occam.api.model.calendar.Holiday;
import com.esferalia.aon.occam.api.model.calendar.HolidayDetail;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;

public class HolidayDAO {
	private static final HolidayPropertiesDAO HOLIDAY_PROPERTIES = new HolidayPropertiesDAO();
	
	protected static class HolidayPropertiesDAO implements HolidayProperties {
		protected Condition[] getConditions(HolidayFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY.DESCRIPTION);}
		@Override public Property<Integer> getHolidayParentProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY.HOLIDAY_);}
		@Override public Property<Byte> getEditableProperty(){return new FilterDAO.PropertyDAO<>(HOLIDAY.EDITABLE);}
		
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, HolidayFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(HOLIDAY)
				.where(HOLIDAY_PROPERTIES.getConditions(filter));
	}
	
	public static Optional<Holiday> get(AONContext ctx, HolidayFilter filter) {
		ctx.checkRead();
		Optional<Holiday> holiday = select(ctx, filter)
				.limit(1)
				.fetch()
				.stream()
				.map(new HolidayFiller())
				.findFirst();
		
		if(holiday.isPresent())
			holiday.get().setDetails( 
					HolidayDetailDAO.getStream(ctx, 
							f -> f.getDomainProperty().eq(holiday.get().getDomain())
								.and(f.getHolidayProperty().eq(holiday.get().getId()))
							)
							.collect(Collectors.toList()));
		
		return holiday;
		
	}
	
	public static Stream<Holiday> getStream(AONContext ctx, HolidayFilter filter){	
		List<Holiday> holidaysList = select(ctx, filter)
				.fetch()
				.stream()
				.map(new HolidayFiller())
				.collect(Collectors.toList());
		
		holidaysList.forEach(h -> h.setDetails( HolidayDetailDAO.getStream(ctx, f -> f.getDomainProperty().eq(h.getDomain()).and(f.getHolidayProperty().eq(h.getId()))).collect(Collectors.toList()) ));
		
		return holidaysList.stream();
	}
	
	public static Stream<Holiday> getStream(AONContext ctx, HolidayFilter filter, Integer page, Integer perPage){	
		List<Holiday> holidaysList  = select(ctx, filter)	
				.offset(perPage * (page -1))
				.fetch()
				.stream()
				.map(new HolidayFiller())
				.collect(Collectors.toList());
		
		holidaysList.forEach(h -> h.setDetails( HolidayDetailDAO.getStream(ctx, f -> f.getDomainProperty().eq(h.getDomain()).and(f.getHolidayProperty().eq(h.getId()))).collect(Collectors.toList()) ));
		
		return holidaysList.stream();
	}
	
	
	public static Holiday save(AONContext ctx, Holiday holiday) {
		return (holiday.getId() != null && holiday.getId() > 0)
			? update(ctx, holiday)
			: insert(ctx, holiday); 
	}
	
	private static Holiday update(AONContext ctx, Holiday holiday){
		ctx.getDslContext().update(HOLIDAY)
			.set(HOLIDAY.DESCRIPTION, holiday.getDescription())
			.set(HOLIDAY.HOLIDAY_, holiday.getHolidayParent())
			.set(HOLIDAY.EDITABLE, holiday.isEditable() ? (byte) 1 : (byte) 0)
			.where(HOLIDAY.ID.eq(holiday.getId()))
			.execute();
		

		List<HolidayDetail> details = holiday.getDetails();
		if(!details.isEmpty()) {
			details.stream().filter(dh -> dh.isDirty() || null == dh.getId()).forEach(hd -> HolidayDetailDAO.save(ctx, hd));
		}
		
		ctx.log().debug("UPDATE NOTE id: " + holiday.getId());		
		return holiday;
	}
	
	private static Holiday insert(AONContext ctx, Holiday holiday) {
		Integer id = ctx.getDslContext()
				.insertInto(HOLIDAY)
				.set(HOLIDAY.DOMAIN, holiday.getDomain())
				.set(HOLIDAY.DESCRIPTION, holiday.getDescription())
				.set(HOLIDAY.HOLIDAY_, holiday.getHolidayParent())
				.set(HOLIDAY.EDITABLE, holiday.isEditable() ? (byte) 1 : (byte) 0)
				.returning(HOLIDAY.ID)
				.fetchOne()
				.getId();
		
		holiday.getDetails().forEach(hd -> hd.setHoliday(id));
		
		List<HolidayDetail> details = holiday.getDetails();
		if(!details.isEmpty()) {
			details.stream().filter(dh -> dh.isDirty() || null == dh.getId()).forEach(hd -> HolidayDetailDAO.save(ctx, hd));
		}
		
		holiday.setId(id);
		ctx.log().debug("INSERT NOTE id: " + id);		
		return holiday;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		
		ctx.getDslContext().delete(HOLIDAY_DETAIL)
		.where(HOLIDAY_DETAIL.HOLIDAY.eq(id))
		.execute();	
		
		ctx.getDslContext().delete(HOLIDAY)
			.where(HOLIDAY.ID.eq(id))
			.execute();	
		
		ctx.log().debug("DELETE NOTE id: " + id);		
	}
	
	private static class HolidayFiller extends Filler implements Function<Record, Holiday> {
		@Override
		public Holiday apply(Record r) {
			return new Holiday()
					.setId(r.getValue(HOLIDAY.ID))
					.setDomain(r.getValue(HOLIDAY.DOMAIN))
					.setDescription(r.getValue(HOLIDAY.DESCRIPTION))
					.setHolidayParent(r.getValue(HOLIDAY.HOLIDAY_))
					.setEditable(r.getValue(HOLIDAY.EDITABLE) == (byte) 0 ? false : true)
					;
		}
	}
}
