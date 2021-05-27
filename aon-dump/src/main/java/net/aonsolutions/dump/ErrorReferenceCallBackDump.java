package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.Table;

public class ErrorReferenceCallBackDump extends AbstractChaimCallbackDump {

	public ErrorReferenceCallBackDump(CallbackDump cb) {
		super(cb);
	}

	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap,
			CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		
		//TODO: ver que mensaje de error mostrar para dar la maxima informacion posible
		
		System.err.println("onErrorFk : " + fk.getTable().getName() + ", " + r.getValue(fk.getFields().get(0)) + " -> "  + fk.getKey().getTable().getName() );
		
		return null;
		
	}

}
