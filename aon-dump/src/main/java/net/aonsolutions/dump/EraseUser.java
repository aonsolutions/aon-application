package net.aonsolutions.dump;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.User;
import com.google.api.client.util.Base64;

public class EraseUser extends AbstractChaimCallbackDump{

	private CallbackDump  cb;
	private DSLContext dslContext;
	private String userPass;
	private String userLogin;
	int idOldDomain;
	IdsMap idsMap;
	
	// Consumer that erase all the users
	public EraseUser(CallbackDump cb, DSLContext dslContext, String pass, String login) {
		super(cb);
		this.cb = cb;	
		this.dslContext = dslContext;
		this.userPass = pass;
		this.userLogin = login;
		
	}
	
	@Override
	public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables, DSLContext dslContext,
			int id, IdsMap idsMap) {
		// TODO Auto-generated method stub
		this.idOldDomain = id;
		this.idsMap = idsMap;
		super.header(schema, hostName, domainTables, dslContext, id, idsMap);
	}
	
	@Override
	public void accept(InsertSetMoreStep<?> inSet, Table<?> table, List<Table<?>> ciclica, Integer numRows, String varTableName) {
		
		if (table.getName().equals("user_scope"))
			throw new SkipInsertException();
		if (table.getName().equals("user_workgroup"))
			throw new SkipInsertException();
		if (table.getName().equals("user")){
			throw new SkipInsertException();
		}
		cb.accept(inSet, table, ciclica, numRows, varTableName);
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
		
		
		Field<Integer> varDomain = this.idsMap.getOrder(Domain.DOMAIN.getName(), idOldDomain);
		String varUser = "@USER_" + idOldDomain;
		InsertSetStep<?> insert = this.dslContext.insertInto(User.USER);
		
		InsertSetMoreStep<?> inSet2 = insert
				.set(User.USER.ID, DSL.field(varUser+" - 1", Integer.class))
				.set(User.USER.DOMAIN, varDomain)
				.set(User.USER.NAME, this.userLogin)
				.set(User.USER.LOGIN, this.userLogin)
				.set(User.USER.PASSWORD, digestPasswd(this.userPass));
		
		
		cb.accept(inSet2, User.USER, Collections.emptyList(), 1, varUser);
		
		cb.footer();
	}
	
    private static String digestPasswd(String passwd) {
        try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update(passwd.getBytes("UTF-8"));
                byte raw[] = digest.digest();
                return new String(Base64.encodeBase64(raw), "UTF-8"); // step 5
        } catch (NoSuchAlgorithmException e) {
                return null;
        } catch (UnsupportedEncodingException e) {
                return null;
        }
}

	
}
