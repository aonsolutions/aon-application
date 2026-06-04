package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.SignatureRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Signature;

public class SignatureDAO {

	private SignatureDAO() {}
	
	private static final SignaturePropertiesDAO SIGNATURE_PROPERTIES = new SignaturePropertiesDAO();

	protected static class SignaturePropertiesDAO implements SignatureProperties {
		protected Condition[] getConditions(SignatureFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.NAME);}
		@Override public Property<String> getSignatureProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.SIGNATURE_);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(SIGNATURE.USER_ID);}
	}
	
	public static Signature get(AONContext ctx, Integer signatureId){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE.ID.eq(signatureId)).limit(1).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).findFirst().orElse(new Signature());
	}
	
	public static Signature get(AONContext ctx, SignatureFilter filter){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE_PROPERTIES.getConditions(filter)).limit(1).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).findFirst().orElse(new Signature());
	}
	
	public static LinkedList<Signature> getList(AONContext ctx, SignatureFilter filter){
		return ctx.getDslContext().select().from(SIGNATURE)
				.where(SIGNATURE_PROPERTIES.getConditions(filter)).fetchInto(SIGNATURE)
				.stream().map(new FullSignatureFiller()).collect(Collectors.toCollection(LinkedList::new));
	}

	public static Signature save(AONContext ctx, Signature signature){
		return signature.getId() != null 
				? update(ctx, signature)
				: insert(ctx, signature);
	}
	
	private static Signature update(AONContext ctx, Signature signature){
		ctx.getDslContext().update(SIGNATURE)
			.set(SIGNATURE.NAME, signature.getName())
			.set(SIGNATURE.SIGNATURE_, signature.getSignature())
			.set(SIGNATURE.USER_ID, signature.getUserId())
			.where(SIGNATURE.ID.eq(signature.getId()))
			.execute();
		
		ctx.log().debug("UPDATE SIGNATURE id: " + signature.getId());	
		
		return signature;
	}
	
	private static Signature insert(AONContext ctx, Signature signature) {
		Integer newId = ctx.getDslContext()
				.insertInto(TAG)
				.set(SIGNATURE.DOMAIN, ctx.getDomainId())
				.set(SIGNATURE.NAME, signature.getName())
				.set(SIGNATURE.SIGNATURE_, signature.getSignature())
				.set(SIGNATURE.USER_ID, signature.getUserId())
				.returning(SIGNATURE.ID)
				.fetchOne()
				.getValue(SIGNATURE.ID);
		
		ctx.log().debug("CREATE SIGNATURE id: " + newId);	
		
		signature.setId(newId);
		
		return signature;
	}
	
	public static void delete(AONContext ctx, Integer deleteId){
		ctx.getDslContext().delete(SIGNATURE)
			.where(SIGNATURE.ID.eq(deleteId))
			.execute();
		
		ctx.log().debug("DELETE SIGNATURE id: " + deleteId);	
	}
	
	private static class FullSignatureFiller implements Function<SignatureRecord, Signature> {
		@Override
		public Signature apply(SignatureRecord r) {
			return new Signature()
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setName(r.getName())
					.setSignature(r.getSignature())
					.setUserId(r.getUserId())
					;
		}
	}
	
}
