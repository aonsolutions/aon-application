package com.esferalia.aon.occam.impl.jooq.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Rattach;
import com.esferalia.aon.jooq.tables.Rdoc;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.S3Document;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.S3DocumentFilter;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachPropertiesDAO.RattachPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.S3DocumentPropertiesDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class S3DocumentDAO {
	
	private static final S3DocumentPropertiesDAO S3DOCUMENT_PROPERTIES = new S3DocumentPropertiesDAO();
	private static final RattachPropertiesDAO ATTACH_PROPERTIES = new RattachPropertiesDAO();
	private static final Field<Integer> TYPE_DOC = DSL.field("type_doc", Integer.class);
	
	private S3DocumentDAO() {
		  throw new IllegalStateException("Utility class");
	}
	
	public static Stream<S3Document> getStream(AONContext ctx, S3DocumentFilter filter, AttachFilter attachFilter, Integer type, Optional<Integer> page, Optional<Integer> perPage) {
		ctx.checkRead();
		SelectConditionStep<Record> queryRDoc = ctx.getDslContext()
				.select(Rdoc.RDOC.ID)
				.select(DSL.inline((Integer) 0).as(TYPE_DOC))
				.select(Rdoc.RDOC.NAME)
				.select(Rdoc.RDOC.DOMAIN)
				.select(Rdoc.RDOC.MIMETYPE)
				.select(Rdoc.RDOC.DOCUMENT_DATE)
				.select(Rdoc.RDOC.S3)
				.select(Rdoc.RDOC.S3_BUCKET)
				.select(Rdoc.RDOC.SECURITY_LEVEL)
				.select(Rdoc.RDOC.CREATION_DATE)
				.select(Rdoc.RDOC.CREATION_USER)
				.select(Rdoc.RDOC.MODIFICATION_DATE)
				.select(Rdoc.RDOC.MODIFICATION_USER)
				.select(Rdoc.RDOC.CATEGORY)
				.select(Rdoc.RDOC.REGISTRY)
				.select(Rdoc.RDOC.SCOPE)
				.from(Rdoc.RDOC)
				.where(S3DOCUMENT_PROPERTIES.getConditions(filter));
		SelectConditionStep<Record> queryRAttach = ctx.getDslContext()
				.select(Rattach.RATTACH.ID.as(Rdoc.RDOC.ID))
				.select(DSL.inline((Integer) 1).as(TYPE_DOC))
				.select(Rattach.RATTACH.DESCRIPTION.as(Rdoc.RDOC.NAME))
				.select(Rattach.RATTACH.DOMAIN.as(Rdoc.RDOC.DOMAIN))
				.select(Rattach.RATTACH.MIMETYPE.as(Rdoc.RDOC.MIMETYPE))
				.select(Rattach.RATTACH.ATTACH_DATE.as(Rdoc.RDOC.DOCUMENT_DATE))
				.select(DSL.inline((String) null).as(Rdoc.RDOC.S3))
				.select(DSL.inline((String) null).as(Rdoc.RDOC.S3_BUCKET))
				.select(Rattach.RATTACH.SECURITY_LEVEL.as(Rdoc.RDOC.SECURITY_LEVEL))
				.select(Rattach.RATTACH.CREATION_DATE.as(Rdoc.RDOC.CREATION_DATE))
				.select(Rattach.RATTACH.CREATION_USER.as(Rdoc.RDOC.CREATION_USER))
				.select(Rattach.RATTACH.MODIFICATION_DATE.as(Rdoc.RDOC.MODIFICATION_DATE))
				.select(Rattach.RATTACH.MODIFICATION_USER.as(Rdoc.RDOC.MODIFICATION_USER))
				.select(Rattach.RATTACH.CATEGORY.as(Rdoc.RDOC.CATEGORY))
				.select(Rattach.RATTACH.REGISTRY.as(Rdoc.RDOC.REGISTRY))
				.select(Rattach.RATTACH.SCOPE.as(Rdoc.RDOC.SCOPE))
				.from(Rattach.RATTACH)
				.where(ATTACH_PROPERTIES.getConditions(attachFilter));
		Select<Record> fullQuery;
		if(type == null) {
			if(page.isPresent() && perPage.isPresent())
				fullQuery = queryRDoc.union(queryRAttach).limit(perPage.get()).offset(perPage.get() * (page.get() - 1));
			else
				fullQuery = queryRDoc.union(queryRAttach);
		}
		else if(type == 0)
			fullQuery = queryRDoc;
		else
			fullQuery = queryRAttach;
		return fullQuery
			.fetch()
			.stream()
			.map(new S3DocumentFiller());
	}
	
	public static long getCount(AONContext ctx, S3DocumentFilter filter, AttachFilter attachFilter) {
		ctx.checkRead();
		SelectConditionStep<Record1<Integer>> queryRDoc = ctx.getDslContext()
				.select(Rdoc.RDOC.ID)
				.from(Rdoc.RDOC)
				.where(S3DOCUMENT_PROPERTIES.getConditions(filter));
		SelectConditionStep<Record1<Integer>> queryRAttach = ctx.getDslContext()
				.select(Rattach.RATTACH.ID.as(Rdoc.RDOC.ID))
				.from(Rattach.RATTACH)
				.where(ATTACH_PROPERTIES.getConditions(attachFilter));
		return queryRDoc.union(queryRAttach).fetch().stream().count();
	}
	
	public static S3Document insert(AONContext ctx, S3Document document){
		ctx.checkWrite();
		validate(ctx,document);
		Integer id = ctx.getDslContext()
		.insertInto(Rdoc.RDOC)
		.set(Rdoc.RDOC.DOMAIN, document.getDomain())
		.set(Rdoc.RDOC.REGISTRY, document.getRegistry())
		.set(Rdoc.RDOC.CATEGORY, document.getCategory())
		.set(Rdoc.RDOC.MIMETYPE, document.getMimetype().value())
		.set(Rdoc.RDOC.NAME, document.getName())
		.set(Rdoc.RDOC.SCOPE, document.getScope())
		.set(Rdoc.RDOC.SECURITY_LEVEL, document.getSecurityLevel())
		.set(Rdoc.RDOC.DOCUMENT_DATE, new Date(document.getDocumentDate().getTime()))
		.set(Rdoc.RDOC.S3, document.getS3key())
		.set(Rdoc.RDOC.S3_BUCKET, document.getS3bucket())
		.set(Rdoc.RDOC.CREATION_DATE, new Timestamp(document.getCreationDate().getTime()))
		.set(Rdoc.RDOC.MODIFICATION_DATE, new Timestamp(document.getModificationDate().getTime()))
		.set(Rdoc.RDOC.CREATION_USER, document.getCreationUser())
		.set(Rdoc.RDOC.MODIFICATION_USER, document.getModificationUser())
		.returning(Rdoc.RDOC.ID).fetchOne().getId()
		;
		return document.setId(id).setType(0);
	}
	
	public static S3Document update(AONContext ctx, S3Document document, Integer type){
		ctx.checkWrite();
		validate(ctx,document);
		if(type == 0)
			ctx.getDslContext()
				.update(Rdoc.RDOC)
				.set(Rdoc.RDOC.DOMAIN, document.getDomain())
				.set(Rdoc.RDOC.REGISTRY, document.getRegistry())
				.set(Rdoc.RDOC.CATEGORY, document.getCategory())
				.set(Rdoc.RDOC.MIMETYPE, document.getMimetype().value())
				.set(Rdoc.RDOC.NAME, document.getName())
				.set(Rdoc.RDOC.SCOPE, document.getScope())
				.set(Rdoc.RDOC.SECURITY_LEVEL, document.getSecurityLevel())
				.set(Rdoc.RDOC.DOCUMENT_DATE, new Date(document.getDocumentDate().getTime()))
				.set(Rdoc.RDOC.MODIFICATION_DATE, new Timestamp(document.getModificationDate().getTime()))
				.set(Rdoc.RDOC.MODIFICATION_USER, document.getModificationUser())
				.where(Rdoc.RDOC.ID.eq(document.getId()))
				.execute();
		else if(type == 1)
			ctx.getDslContext()
				.update(Rattach.RATTACH)
				.set(Rattach.RATTACH.DOMAIN, document.getDomain())
				.set(Rattach.RATTACH.REGISTRY, document.getRegistry())
				.set(Rattach.RATTACH.CATEGORY, document.getCategory())
				.set(Rattach.RATTACH.MIMETYPE, document.getMimetype().value())
				.set(Rattach.RATTACH.DESCRIPTION, document.getName())
				.set(Rattach.RATTACH.SCOPE, document.getScope())
				.set(Rattach.RATTACH.SECURITY_LEVEL, document.getSecurityLevel())
				.set(Rattach.RATTACH.ATTACH_DATE, new Date(document.getDocumentDate().getTime()))
				.set(Rattach.RATTACH.MODIFICATION_DATE, new Timestamp(document.getModificationDate().getTime()))
				.set(Rattach.RATTACH.MODIFICATION_USER, document.getModificationUser())
				.where(Rattach.RATTACH.ID.eq(document.getId()))
				.execute();
		return document;
	}
	
	public static void delete(AONContext ctx, S3DocumentFilter filter, AttachFilter attachFilter, Integer type){
		ctx.checkWrite();
		if(type == 0)
			ctx.getDslContext().delete(Rdoc.RDOC)
				.where(S3DOCUMENT_PROPERTIES.getConditions(filter))
				.execute();
		else
			ctx.getDslContext().delete(Rattach.RATTACH)
				.where(ATTACH_PROPERTIES.getConditions(attachFilter))
				.execute();
	}
	
	public static byte[] getFile(AONContext ctx, Integer id) {
		ctx.checkWrite();
		return ctx.getDslContext().select(Rattach.RATTACH.DATA).from(Rattach.RATTACH).where(Rattach.RATTACH.ID.eq(id)).fetch().getFirst().value1();
	}
	
	public static class S3DocumentFiller extends Filler implements Function<Record, S3Document> {
		
		public S3Document apply(Record r) {
			return build(r);
		}
	
		public static S3Document build(Record r) {
			return new S3Document()
					.setId(r.get(Rdoc.RDOC.ID))
					.setDomain(r.get(Rdoc.RDOC.DOMAIN))
					.setMimetype(MimeType.safeValueOf(r.get(Rdoc.RDOC.MIMETYPE)))
					.setDocumentDate(r.get(Rdoc.RDOC.DOCUMENT_DATE))
					.setS3key(r.get(Rdoc.RDOC.S3))
					.setS3bucket(r.get(Rdoc.RDOC.S3_BUCKET))
					.setSecurityLevel(r.get(Rdoc.RDOC.SECURITY_LEVEL))
					.setCreationDate(r.get(Rdoc.RDOC.CREATION_DATE))
					.setCreationUser(r.get(Rdoc.RDOC.CREATION_USER))
					.setModificationDate(r.get(Rdoc.RDOC.MODIFICATION_DATE))
					.setModificationUser(r.get(Rdoc.RDOC.MODIFICATION_USER))
					.setCategory(r.get(Rdoc.RDOC.CATEGORY))
					.setName(r.get(Rdoc.RDOC.NAME))
					.setRegistry(r.get(Rdoc.RDOC.REGISTRY))
					.setType(r.get(TYPE_DOC))
					.setScope(r.get(Rdoc.RDOC.SCOPE))
					;
		}
	}
	
	public static final BiConsumer<AONContext, S3Document> IS_PARENT = (ctx, category) -> {
//		if (category.getDomain() != ctx.getDomainId())
//			throw new AonCoreException(AonError.WRITE_FORBIDDEN.format("operation"));
	};
	
	public static void validate(AONContext ctx, S3Document document) throws AonCoreException {
		IS_PARENT.accept(ctx, document);
	}
	
}
