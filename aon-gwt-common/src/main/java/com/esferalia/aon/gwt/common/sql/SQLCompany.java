package com.esferalia.aon.gwt.common.sql;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Record6;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.Company;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyBank;

public class SQLCompany {
	
	public static Company getCompany(DSLContext ctx,int domain) throws AonSQLException {
		Record3<Integer,String,String> record = 
			ctx.select(COMPANY.REGISTRY,REGISTRY.DOCUMENT,REGISTRY.NAME)
				.from(COMPANY)
				.join(REGISTRY).onKey()
				.where(COMPANY.DOMAIN.equal(domain))
				.fetchOne();
		Company company = new Company();
		if (record != null) {
			company.setId(record.getValue(COMPANY.REGISTRY));
			company.setDocument(record.getValue(REGISTRY.DOCUMENT));
			company.setName(record.getValue(REGISTRY.NAME));
		}
		return company;
	}
	
	public static List<CompanyAdministrator> getDirStaff(DSLContext ctx,int domain) throws AonSQLException {
		List<Record6<String,String,Byte,Byte,Double,Double>> record = 
			ctx.select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME,RDIR_STAFF.DIRECTOR,RDIR_STAFF.SHAREHOLDER,RDIR_STAFF.PERCENT_SHARE,RDIR_STAFF.NOMINAL_VALUE)
				.from(COMPANY)
				.join(RDIR_STAFF).on( COMPANY.REGISTRY.equal(RDIR_STAFF.REGISTRY) )
				.where(COMPANY.DOMAIN.equal(domain))
				.fetch();
		List<CompanyAdministrator> list = new LinkedList<CompanyAdministrator>(); 
		for (Record6<String,String,Byte,Byte,Double,Double> rec : record) {
			CompanyAdministrator ca = new CompanyAdministrator();
			ca.setDocument(rec.getValue(RDIR_STAFF.DOCUMENT) );
			ca.setName(rec.getValue(RDIR_STAFF.NAME) );
			ca.setShareholder( rec.getValue(RDIR_STAFF.SHAREHOLDER) == 1 );
			ca.setAdministrator( rec.getValue(RDIR_STAFF.DIRECTOR) == 1 );
			ca.setPercent(rec.getValue(RDIR_STAFF.PERCENT_SHARE) );
			ca.setNominalValue(rec.getValue(RDIR_STAFF.NOMINAL_VALUE) );
			list.add(ca);
		}
		return list;
	}
		
	public static ArrayList<CompanyBank> getBanks(DSLContext ctx,int enterprise) throws AonSQLException {
		List<Record3<String,String,String>> record = 
			ctx.select(RBANK.BANK_ACCOUNT,RBANK.BIC,RBANK.ALIAS)
				.from(COMPANY)
				.join(RBANK).on( COMPANY.REGISTRY.equal(RBANK.REGISTRY) )
				.where(COMPANY.REGISTRY.equal(enterprise))
				.and(RBANK.ACTIVE.equal((byte) 1))
				.fetch();
		ArrayList<CompanyBank> list = new ArrayList<CompanyBank>(); 
		for (Record3<String,String,String> rec : record) {
			CompanyBank cb = new CompanyBank();
			cb.setBankAccount(rec.getValue(RBANK.BANK_ACCOUNT) );
			cb.setBic(rec.getValue(RBANK.BIC) );
			cb.setBic(rec.getValue(RBANK.BIC) );
			cb.setAlias(rec.getValue(RBANK.ALIAS) );
			list.add(cb);
		}
		return list;
	}

}
