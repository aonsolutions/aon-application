package net.aonsolutions.dump;

import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;

public class ParentCallbackDump extends AbstractChaimCallbackDump{

	private CallbackDump  cb;
	private Integer parentDomain;
	private Integer id;
	
	public ParentCallbackDump(CallbackDump cb) {
		super(cb);
		this.cb = cb;
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		
		this.id = id;
		
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}

	@Override
	public void downloadParent(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition whereParent) {

		Condition where = ((Field<Integer>)fk.getKey().getTable().field("id")).in(dslContext.select((Field<Integer>)fk.getFields().get(0)).from(fk.getTable())
				.where(whereParent));
		
		
//		aondump.downloadTableReferenceDomain(fk.getKey().getTable(), fk.getKey().getTable().getReferences(), idsMap, cb, ciclica, where);
//		
//		parentDomain = dslContext.select(DOMAIN.PARENT).from(DOMAIN).where((DOMAIN.ID).equal(id)).fetchOne().value1();
//
//		Integer idDomain = idsMap.getOrder(DOMAIN.getName(), id);
//
//		idsMap.setEspecificOrder(DOMAIN.getName(), parentDomain, idDomain, false);
		
		this.cb.downloadParent(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
	}
}
