package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.type.TagType;

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
	
	public static Tag updateTag(AONContext ctx, Tag tag){
		ctx.getDslContext().update(TAG)
			.set(TAG.NAME, tag.getName())
			.set(TAG.COLOR, tag.getColor())
			.set(TAG.DOMAIN, tag.getDomain())
			.set(TAG.TYPE, tag.getType())
			.where(TAG.ID.eq(tag.getId()))
			.execute();
		return tag;
	}
	
	public static Tag insertTag(AONContext ctx, Tag tag) {
		ctx.getDslContext()
				.insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR, (tag.getColor() != null) ? tag.getColor() : null);
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
					.setTagType(TagType.safeValueOf(r.getValue(alias.TYPE)));
		}
	
	}
	
	
}
