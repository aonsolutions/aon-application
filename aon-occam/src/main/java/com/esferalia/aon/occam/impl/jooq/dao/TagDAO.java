package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.office.Tag;

public class TagDAO {
	private static final TagPropertiesDAO TAG_PROPERTIES = new TagPropertiesDAO();

	protected static class TagPropertiesDAO implements TagProperties {
		protected Condition[] getConditions(TagFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TAG.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TAG.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAG.TYPE);}
		@Override public Property<String> getColorProperty(){return new FilterDAO.PropertyDAO<String>(TAG.COLOR);}
		@Override public Property<String> getNameProperty(){return new FilterDAO.PropertyDAO<String>(TAG.NAME);}
	}
	
	public static Tag getTag(AONContext ctx, Integer tagId){
		return ctx.getDslContext().select().from(TAG).where(TAG.ID.eq(tagId)).fetchInto(TAG)
				.stream().map(new FullTagFiller()).findFirst().orElse(new Tag());
	}
	
	public static Stream<Tag> getTagStream(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().select().from(TAG).where(TAG_PROPERTIES.getConditions(filter))
				.fetchInto(TAG).stream().map(new FullTagFiller());
	}
	
	public static List<Tag> getList(AONContext ctx, TagFilter filter) {
		return getTagStream(ctx, filter).collect(Collectors.toList());
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
		TagRecord tagRecord = ctx.getDslContext()
				.insertInto(TAG)
				.set(TAG.DOMAIN, ctx.getDomainId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getType())
				.set(TAG.COLOR, (tag.getColor() != null) ? tag.getColor() : null)
				.returning()
				.fetchOne();
		return new FullTagFiller().apply(tagRecord);
	}

	public static void deleteTag(AONContext ctx, TagFilter filter){
		ctx.getDslContext().delete(TAG).where(TAG_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static class FullTagFiller implements Function<TagRecord, Tag> {
		@Override
		public Tag apply(TagRecord r) {
			return new Tag().setId(r.getId())
					.setColor(r.getColor())
					.setDomain(r.getDomain())
					.setName(r.getName())
					.setType(r.getType())
					;		
		}
		
		public static Tag build(Record r) {
			return buildTag(r, TAG);
		}
		
		private static Tag buildTag(Record r, com.esferalia.aon.jooq.tables.Tag tagTable) {
			return fillTag(r, new Tag(), tagTable);
		}

		private static Tag fillTag(Record r, Tag tag, com.esferalia.aon.jooq.tables.Tag tagTable) {
			return tag.setId(r.getValue(tagTable.ID))
					.setColor(r.getValue(tagTable.COLOR))
					.setDomain(r.getValue(tagTable.DOMAIN))
					.setName(r.getValue(tagTable.NAME))
					.setType(r.getValue(tagTable.TYPE));	
		}
	}
}
