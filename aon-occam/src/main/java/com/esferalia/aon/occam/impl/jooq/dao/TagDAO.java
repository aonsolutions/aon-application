package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TagDAO {
	
	private TagDAO() {
	
	}
	
	private static final TagPropertiesDAO TAG_PROPERTIES = new TagPropertiesDAO();

	protected static class TagPropertiesDAO implements TagProperties {
		protected Condition[] getConditions(TagFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TAG.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TAG.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TAG.TYPE);}
		@Override public Property<String> getColorProperty(){return new FilterDAO.PropertyDAO<>(TAG.COLOR);}
		@Override public Property<String> getNameProperty(){return new FilterDAO.PropertyDAO<>(TAG.NAME);}
	}
	
	public static Tag getTag(AONContext ctx, Integer tagId){
		return ctx.getDslContext().select().from(TAG).where(TAG.ID.eq(tagId)).fetch()
				.stream().map(new TagFiller()).findFirst().orElse(new Tag());
	}
	
	public static Stream<Tag> getTagStream(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().select().from(TAG).where(TAG_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new TagFiller());
	}
	
	public static List<Tag> getList(AONContext ctx, TagFilter filter) {
		return getTagStream(ctx, filter).toList();
	}
	
	public static LinkedList<Tag> getList(AONContext ctx, TagParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		SelectConditionStep<Record> select = ctx.getDslContext().select()
				.from(TAG)
				.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "description"))
				select.orderBy(TAG.NAME);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "description"))
				select.orderBy(TAG.NAME.desc());
		}
		
		return select
				.limit(params.getOffset(), params.getLimit())
				.fetchInto(TAG)
				.stream()
				.map(new TagFiller())
				.collect(Collectors.toCollection(LinkedList::new))
				;
	}
	
	private static Condition paramsToCondition(AONContext ctx, TagParams params) {
		Condition condition = DSL.trueCondition();
		
		condition = condition.and(TAG.DOMAIN.eq(params.getDomain()));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(TAG.NAME.like("%" + params.getDescription() + "%"));
		
		if(null != params.getTagType())
			condition = condition.and(TAG.TYPE.eq(params.getTagType().value()));
		
		return condition;
	}

	public static Tag save(AONContext ctx, Tag tag){
		return tag.getId() != null 
			? updateTag(ctx, tag)
			: insertTag(ctx, tag);
	}
	
	private static Tag updateTag(AONContext ctx, Tag tag){
		ctx.getDslContext().update(TAG)
			.set(TAG.NAME, tag.getName())
			.set(TAG.COLOR, tag.getColor())
			.set(TAG.DOMAIN, tag.getDomain())
			.set(TAG.TYPE, tag.getType().value())
			.where(TAG.ID.eq(tag.getId()))
			.execute();
		return tag;
	}
	
	private static Tag insertTag(AONContext ctx, Tag tag) {
		ctx.getDslContext()
				.insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType().value())
				.set(TAG.COLOR, (tag.getColor() != null) ? tag.getColor() : null)
				.execute();
		return tag;
	}

	public static void deleteTag(AONContext ctx, TagFilter filter){
		ctx.getDslContext().delete(TAG).where(TAG_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static class TagFiller extends Filler implements Function<Record, Tag> {

		@Override
		public Tag apply(Record r) {
			return build(r, TAG);
		}

		public static Tag build(Record r) {
			return build(r, TAG);
		}
		
		public static Tag build(Record r, com.esferalia.aon.jooq.tables.Tag alias) {
			return new Tag()
					.setId(r.getValue(alias.ID))
					.setColor(r.getValue(alias.COLOR))
					.setDomain(r.getValue(alias.DOMAIN))
					.setName(r.getValue(alias.NAME))
					.setType(TagType.safeValueOf(r.getValue(alias.TYPE)));
		}
	
	}
	
	
}
