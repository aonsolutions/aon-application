package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;

import java.sql.Timestamp;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.ContractAttachProperties;
import com.esferalia.aon.occam.api.model.payroll.ContractAttach;
import com.esferalia.aon.occam.api.model.type.ContractAttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class ContractAttachDAO {

	private ContractAttachDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static final ContractAttachPropertiesDAO CONTRACT_ATTACH_PROPERTIES = new ContractAttachPropertiesDAO();
	protected static class ContractAttachPropertiesDAO implements ContractAttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ContractAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ContractAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DOMAIN);}
		@Override public Property<Integer> getContractProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.CONTRACT);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateProperty() {return new FilterDAO.TimestampPropertyDAO(CONTRACT_ATTACH.ATTACH_DATE);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DRIVEID);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, ContractAttachFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<ContractAttach> getStream(AONContext ctx, ContractAttachFilter filter){	
		return select(ctx, filter).fetch().stream().map(new ContractAttachFiller());
	}
	
	public static ContractAttach get(AONContext ctx, ContractAttachFilter filter) {
		ctx.checkRead();
		return select(ctx, filter).limit(1)
			.stream().map(new ContractAttachFiller())
			.findFirst().orElse(new ContractAttach());
	}
	
	public static ContractAttach save(AONContext ctx, ContractAttach ct) {
		return ct.getId() !=null ? update(ctx, ct) : insert(ctx, ct);
	}
	
	private static ContractAttach insert(AONContext ctx, ContractAttach attach) {
		ctx.checkWrite();

		Integer id = ctx.getDslContext().insertInto(CONTRACT_ATTACH)
		.set(CONTRACT_ATTACH.DOMAIN, attach.getDomain())
		.set(CONTRACT_ATTACH.CONTRACT, attach.getContract())
		.set(CONTRACT_ATTACH.MIMETYPE, attach.getMimeType().value())
		.set(CONTRACT_ATTACH.DESCRIPTION, attach.getDescription())
		.set(CONTRACT_ATTACH.DATA, attach.getData())
		.set(CONTRACT_ATTACH.TYPE, attach.getType().value())
		.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp( System.currentTimeMillis()))
		.returning(CONTRACT_ATTACH.ID).fetchOne().getId();
		ctx.log().debug("INSERT CONTRACT_ATTACH id: " + id);	
		
		return attach.setId(id);
	}
	
	private static ContractAttach update(AONContext ctx, ContractAttach c) {
		ctx.checkWrite();
		ctx.getDslContext()
			.update(CONTRACT_ATTACH)
			.set(CONTRACT_ATTACH.MIMETYPE, c.getMimeType().value())
			.set(CONTRACT_ATTACH.DESCRIPTION, c.getDescription())
			.set(CONTRACT_ATTACH.DATA, c.getData())
			.set(CONTRACT_ATTACH.TYPE, c.getType().value())
			.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp( System.currentTimeMillis()))
			.where(CONTRACT_ATTACH.ID.eq(c.getId()))
			.execute();		
		ctx.log().debug("UPDATE CONTRACT_ATTACH id: " + c.getId());		
		return c;
	}

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE CONTRACT_ATTACH id:" + id);
	}
	
	private static void delete(AONContext ctx, ContractAttachFilter filter) {
		ctx.getDslContext()
			.delete(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static class ContractAttachFiller extends Filler implements Function<Record, ContractAttach> {

		@Override
		public ContractAttach apply(Record r) {
			return new ContractAttach()
					.setId(r.getValue(CONTRACT_ATTACH.ID))
					.setDomain(r.getValue(CONTRACT_ATTACH.DOMAIN))
					.setContract(r.getValue(CONTRACT_ATTACH.CONTRACT))
					.setMimeType(MimeType.safeValueOf(r.getValue(CONTRACT_ATTACH.MIMETYPE)))
					.setData(r.getValue(CONTRACT_ATTACH.DATA))
					.setDescription(r.getValue(CONTRACT_ATTACH.DESCRIPTION))
					.setType(ContractAttachType.safeValueOf( r.getValue(CONTRACT_ATTACH.TYPE)))
					.setAttachDate(r.getValue(CONTRACT_ATTACH.ATTACH_DATE))
					;
		}
	}
}
