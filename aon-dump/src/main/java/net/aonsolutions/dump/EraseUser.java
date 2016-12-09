package net.aonsolutions.dump;

import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.User;

public class EraseUser extends AbstractChaimCallbackDump{

	private CallbackDump  cb;
	private DSLContext dslContext;
	private String userPass;
	private String userLogin;
	
	// Consumer that erase all the users
	public EraseUser(CallbackDump cb, DSLContext dslContext, String pass, String login) {
		super(cb);
		this.cb = cb;	
		this.dslContext = dslContext;
		this.userPass = pass;
		this.userLogin = login;
	}
	
	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows) {
		
		if (table.getName().equals("user_scope"))
			throw new SkipInsertException();
		if (table.getName().equals("user_workgroup"))
			throw new SkipInsertException();
		if (table.getName().equals("user")){
			throw new SkipInsertException();
		}
		cb.accept(inSet, table, ciclica, numRows);
	}
	
	@Override
	public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump, IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
		
		Table<?> tableReference = fk.getKey().getTable();
		String fieldNameId = fk.getKey().getFields().get(0).getName();
		String tableReferenceName = fieldNameId.equals("id") ? tableReference.getName() : fieldNameId;
		
		if (tableReferenceName.equals("user")) 
			return fk.getFields().get(0).getDataType().nullable() ? DSL.castNull(Integer.class): null ;
		
		return this.cb.onErrFk(dslContext, r,fk, aondump, idsMap, cb, ciclica, references, where);	 
	}
	
	@Override
	public void footer() {
		
		Field<Integer> varDomain = DSL.field("@DOMAIN + 1", Integer.class);
		InsertSetStep<?> insert = this.dslContext.insertInto(User.USER);
		
		InsertSetMoreStep<?> inSet2 = insert
				.set(User.USER.DOMAIN, varDomain)
				.set(User.USER.NAME, this.userLogin)
				.set(User.USER.LOGIN, this.userLogin)
				.set(User.USER.PASSWORD, this.userPass);
		
		cb.accept(inSet2, User.USER, null, 0);
		
		cb.footer();
	}
	
}
