package com.esferalia.aon.occam.impl.jooq.dao.calendar;

import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

import java.sql.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.HolidayDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.HolidayDetailProperties;
import com.esferalia.aon.occam.api.model.calendar.HolidayDetail;
import com.esferalia.aon.occam.impl.jooq.dao.Filler;
import com.esferalia.aon.occam.impl.jooq.dao.FilterDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class HolidayDetailDAO {
	private static final HolidayDetailPropertiesDAO HOLIDAY_DETAIL_PROPERTIES = new HolidayDetailPropertiesDAO();
	
	protected static class HolidayDetailPropertiesDAO implements HolidayDetailProperties {
		protected Condition[] getConditions(HolidayDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY_DETAIL.DOMAIN);}
		@Override public Property<Integer> getHolidayProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY_DETAIL.HOLIDAY);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY_DETAIL.DATE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(HOLIDAY_DETAIL.DESCRIPTION);}
		
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, HolidayDetailFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL_PROPERTIES.getConditions(filter));
	}
	
	public static Optional<HolidayDetail> get(AONContext ctx, HolidayDetailFilter filter) {
		ctx.checkRead();
		return select(ctx, filter)
				.limit(1)
				.fetch()
				.stream()
				.map(new HolidayDetailFiller())
				.findFirst();
	}
	
	public static Stream<HolidayDetail> getStream(AONContext ctx, HolidayDetailFilter filter){	
		return select(ctx, filter)
				.fetch()
				.stream()
				.map(new HolidayDetailFiller());
	}
	
	public static Stream<HolidayDetail> getStream(AONContext ctx, HolidayDetailFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)	
				.offset(perPage * (page -1))
				.fetch()
				.stream()
				.map(new HolidayDetailFiller());
	}
	
	
	public static HolidayDetail save(AONContext ctx, HolidayDetail holidayDetail) {
		return (holidayDetail.getId() != null && holidayDetail.getId() > 0)
			? update(ctx, holidayDetail)
			: insert(ctx, holidayDetail); 
	}
	
	private static HolidayDetail update(AONContext ctx, HolidayDetail holidayDetail){
		ctx.getDslContext().update(HOLIDAY_DETAIL)
			.set(HOLIDAY_DETAIL.HOLIDAY, holidayDetail.getHoliday())
			.set(HOLIDAY_DETAIL.DATE, holidayDetail.getDate() == null ? null : AonDateUtils.toSql(holidayDetail.getDate()))
			.set(HOLIDAY_DETAIL.DESCRIPTION, holidayDetail.getDescription())
			.where(HOLIDAY_DETAIL.ID.eq(holidayDetail.getId()))
			.execute();
		ctx.log().debug("UPDATE HOLIDAY DETAIL id: " + holidayDetail.getId());		
		return holidayDetail;
	}
	
	private static HolidayDetail insert(AONContext ctx, HolidayDetail holidayDetail) {
		Integer id = ctx.getDslContext()
				.insertInto(HOLIDAY_DETAIL)
				.set(HOLIDAY_DETAIL.DOMAIN, holidayDetail.getDomain())
				.set(HOLIDAY_DETAIL.HOLIDAY, holidayDetail.getHoliday())
				.set(HOLIDAY_DETAIL.DATE, holidayDetail.getDate() == null ? null : AonDateUtils.toSql(holidayDetail.getDate()))
				.set(HOLIDAY_DETAIL.DESCRIPTION, holidayDetail.getDescription())
				.returning(HOLIDAY_DETAIL.ID)
				.fetchOne()
				.getId();
		
		holidayDetail.setId(id);
		ctx.log().debug("INSERT HOLIDAY DETAIL id: " + id);		
		return holidayDetail;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.checkWrite();
		ctx.getDslContext().delete(HOLIDAY_DETAIL)
			.where(HOLIDAY_DETAIL.ID.eq(id))
			.execute();	
		ctx.log().debug("DELETE HOLIDAY DETAIL id: " + id);		
	}
	
	private static class HolidayDetailFiller extends Filler implements Function<Record, HolidayDetail> {
		@Override
		public HolidayDetail apply(Record r) {
			return new HolidayDetail()
					.setId(r.getValue(HOLIDAY_DETAIL.ID))
					.setDomain(r.getValue(HOLIDAY_DETAIL.DOMAIN))
					.setHoliday(r.getValue(HOLIDAY_DETAIL.HOLIDAY))
					.setDate(r.getValue(HOLIDAY_DETAIL.DATE))
					.setDescription(r.getValue(HOLIDAY_DETAIL.DESCRIPTION))
					;
		}
	}
}
