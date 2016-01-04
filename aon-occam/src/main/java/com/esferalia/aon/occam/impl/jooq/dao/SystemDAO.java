package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.Record;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.api.model.BonusProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.type.BonusType;

public class SystemDAO {

	public static Stream<Bonus> getAvailableBonus(AONContext ctx,
			BonusFilter filter, Supplier<Bonus> supplier) {
		
		FilterDAO filterDAO = (FilterDAO)filter.filter(new BonusPropertiesDAO());
		Condition conditions [] = filterDAO == null ? 
				new Condition[0]:
				new Condition []{filterDAO.getCondition()} ; 
		
		//@formatter:off
		Cursor<Record> cursor =
		ctx.getDslContext()
		.select()
		.from(BONUS_CONCEPT)
		.where(conditions)
		.fetchLazy();
		//@formatter:on

		//@formatter:off
		return Seq.seq(cursor)
		.map(record-> {
			Bonus bonus = supplier.get();
			bonus.setId(record.getValue(BONUS_CONCEPT.ID));
			bonus.setDomain(record.getValue(BONUS_CONCEPT.DOMAIN));
			bonus.setExpression(record.getValue(BONUS_CONCEPT.EXPRESSION));
			bonus.setDescription(record.getValue(BONUS_CONCEPT.DESCRIPTION));
			Byte type = record.getValue(BONUS_CONCEPT.TYPE);
			if ( type != null )
				bonus.setType(BonusType.values()[type]);
			return bonus;
		});
		//@formatter:on
	}
	
	
	private static class BonusPropertiesDAO implements BonusProperties {
		
		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(BONUS_CONCEPT.ID);
		}
		
		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(BONUS_CONCEPT.DOMAIN);
		}
		
		@Override
		public Property<Boolean> getIsUnknowProperty() {
			return new FilterDAO.PropertyNullDAO(BONUS_CONCEPT.TYPE);
		}
		
		
	}

}
