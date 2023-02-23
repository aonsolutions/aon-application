package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Cno.CNO;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Cno;
import com.esferalia.aon.occam.api.model.Filter.CnoFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.CnoProperties;

public class CnoDAO {

	private static final CnoPropertiesDAO CNO_PROPERTIES = new CnoPropertiesDAO();
	
	private CnoDAO() {

	}
	
	private static class CnoPropertiesDAO implements CnoProperties {
		private Condition[] getConditions(CnoFilter filter) {
			if (filter == null) return new Condition[0];
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CNO.ID);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(CNO.CODE);}
		@Override public Property<String> getTitleProperty() {return new FilterDAO.PropertyDAO<>(CNO.TITLE);}
	}
	
	public static class CnoFiller extends Filler implements Function<Record,Cno> {
		
		@Override
		public Cno apply(Record r) {
			return build(r);
		}
		
		public static Cno build(Record r) {
			return new Cno()
					.setId(getValue(r, CNO.ID))
					.setCode(getValue(r, CNO.CODE))
					.setTitle(getValue(r, CNO.TITLE));
		}
	}
	
	public static List<Cno> getList(AONContext ctx, Optional<String> cnoSearch) {
		Condition condition = DSL.noCondition();
		
		if(cnoSearch.isPresent()) {
			String pattern = "(?i)\\b" + cnoSearch.get() + "\\b|" + cnoSearch.get();
			condition = CNO.CODE.likeRegex(pattern).or(CNO.TITLE.likeRegex(pattern));
		}
		
		return ctx.getDslContext().selectFrom(CNO)
			.where(condition)
			.fetch()
			.stream()
			.map(new CnoFiller())
			.collect(Collectors.toList());
	}
	
}




