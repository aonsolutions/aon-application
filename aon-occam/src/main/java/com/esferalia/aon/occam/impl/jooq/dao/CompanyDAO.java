package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.Record3;
import org.jooq.Record6;

import com.esferalia.aon.jooq.tables.Rmedia;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseFilter;
import com.esferalia.aon.occam.api.model.EnterpriseProperties;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class CompanyDAO {

	private static final EnterprisePropertiesDAO ENTERPRISE_PROPERTIES = new EnterprisePropertiesDAO();
	private static class EnterprisePropertiesDAO implements EnterpriseProperties {

		private Condition[] getConditions(EnterpriseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}

		@Override
		public Property<Integer> getIdProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ENTERPRISE.REGISTRY);
		}

		@Override
		public Property<Integer> getDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(ENTERPRISE.DOMAIN);
		}

		@Override
		public Property<Integer> getParentDomainProperty() {
			return new FilterDAO.PropertyDAO<Integer>(DOMAIN.PARENT);
		}

		@Override
		public Property<String> getNameProperty() {
			return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);
		}

		@Override
		public Property<String> getAliasProperty() {
			return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);
		}

		@Override
		public Property<String> getDocumentProperty() {
			return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);
		}

	}
	
	private static Rmedia PHONE = RMEDIA.as("rmedia_phone"); 
	private static Rmedia FAX = RMEDIA.as("rmedia_fax");
	private static Rmedia EMAIL = RMEDIA.as("rmedia_email");
	private static Rmedia WEB = RMEDIA.as("rmedia_web");
	
	public static LinkedList<Enterprise> getParentEnterprises(AONContext ctx,EnterpriseFilter filter) {
		LinkedList<Enterprise> list = new LinkedList<Enterprise>();
		ctx.getDslContext().select(
				ENTERPRISE.REGISTRY
				,ENTERPRISE.DOMAIN
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.DOCUMENT
				,REGISTRY.NAME
				,REGISTRY.ALIAS
			)
			.from(ENTERPRISE)
			.join(REGISTRY).on(ENTERPRISE.REGISTRY.equal(REGISTRY.ID))
			.join(DOMAIN).on(
						 DOMAIN.ID.equal(ENTERPRISE.DOMAIN)
					.and(DOMAIN.DOMAINMANAGEMENT.equal((byte) 0))
					.and(DOMAIN.ACTIVE.equal((byte) 1))
							)
			.leftOuterJoin(SCOPE).on(SCOPE.ID.equal(ENTERPRISE.SCOPE))
			.where(ENTERPRISE_PROPERTIES.getConditions(filter))
			.fetch()
			.forEach( record -> list.add( new Enterprise()
						.setId(record.getValue(ENTERPRISE.REGISTRY))
						.setDomain(record.getValue(ENTERPRISE.DOMAIN))
						.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(REGISTRY.DOCUMENT_TYPE)))
						.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
						.setDocument(record.getValue(REGISTRY.DOCUMENT))
						.setName(record.getValue(REGISTRY.NAME))
					)
				);
		return list;
	}

	public static Enterprise getEnterprise(AONContext ctx,int id) {
		return ctx.getDslContext().select(
				ENTERPRISE.REGISTRY
				,ENTERPRISE.DOMAIN
				,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY
				,REGISTRY.DOCUMENT
				,REGISTRY.NAME
				,REGISTRY.ALIAS
				,RADDRESS.STREET_TYPE
				,RADDRESS.ADDRESS
				,RADDRESS.NUMBER
				,RADDRESS.ADDRESS2
				,RADDRESS.ADDRESS3
				,RADDRESS.ZIP
				,RADDRESS.MUNICIPALITY_CODE
				,RADDRESS.CITY
				,GEOZONE.CODE
				,PHONE.VALUE
				,FAX.VALUE
				,EMAIL.VALUE
				,WEB.VALUE
			)
			.from(ENTERPRISE)
			.join(REGISTRY).on(ENTERPRISE.REGISTRY.equal(REGISTRY.ID))
			.leftOuterJoin(RADDRESS).on(RADDRESS.REGISTRY.equal(REGISTRY.ID)
					.and(RADDRESS.TYPE.equal((byte) 0)))
			.leftOuterJoin(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.leftOuterJoin(PHONE).on(PHONE.REGISTRY.equal(REGISTRY.ID)
					.and(PHONE.MEDIA.equal(MediaType.FIXED_PHONE.value())))
			.leftOuterJoin(FAX).on(FAX.REGISTRY.equal(REGISTRY.ID)
					.and(FAX.MEDIA.equal(MediaType.FAX.value())))
			.leftOuterJoin(EMAIL).on(EMAIL.REGISTRY.equal(REGISTRY.ID)
					.and(EMAIL.MEDIA.equal(MediaType.EMAIL.value())))
			.leftOuterJoin(WEB).on(WEB.REGISTRY.equal(REGISTRY.ID)
					.and(WEB.MEDIA.equal(MediaType.WEB.value())))
			.where(ENTERPRISE.REGISTRY.equal(id))
			.fetch()
			.stream()
			.findFirst()
			.get()
			.map( record -> new Enterprise().setId(record.getValue(ENTERPRISE.REGISTRY))
				.setDomain(record.getValue(ENTERPRISE.DOMAIN))
				.setDocumentType(AonEnumUtils.enumValue(DocumentType.class,record.getValue(REGISTRY.DOCUMENT_TYPE)))
				.setDocumentCountry(Country.safeValueOf(record.getValue(REGISTRY.DOCUMENT_COUNTRY)))
				.setDocument(record.getValue(REGISTRY.DOCUMENT))
				.setName(record.getValue(REGISTRY.NAME))
				.setAlias(record.getValue(REGISTRY.ALIAS))
				.setStreetType(StreetType.safeValueOf(record.getValue(RADDRESS.STREET_TYPE)))
				.setAddress(record.getValue(RADDRESS.ADDRESS))
				.setNumber(record.getValue(RADDRESS.NUMBER))
				.setAddress2(record.getValue(RADDRESS.ADDRESS2))
				.setAddress3(record.getValue(RADDRESS.ADDRESS3))
				.setProvince(Province.safeValueOf(record.getValue(GEOZONE.CODE)))
				.setZip(record.getValue(RADDRESS.ZIP))
				.setTown(record.getValue(RADDRESS.MUNICIPALITY_CODE))
				.setCity(record.getValue(RADDRESS.CITY))
				.setPhone(record.getValue(PHONE.VALUE))
				.setFax(record.getValue(FAX.VALUE))
				.setEmail(record.getValue(EMAIL.VALUE))
				.setWeb(record.getValue(WEB.VALUE))
				);
	}

	public static Company getCompany(AONContext ctx,int domain) {
		Record3<Integer,String,String> record = 
			ctx.getDslContext().select(COMPANY.REGISTRY,REGISTRY.DOCUMENT,REGISTRY.NAME)
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
	
	public static List<CompanyAdministrator> getDirStaff(AONContext ctx,int domain) {
		List<Record6<String,String,Byte,Byte,Double,Double>> record = 
			ctx.getDslContext().select(RDIR_STAFF.DOCUMENT,RDIR_STAFF.NAME,RDIR_STAFF.DIRECTOR,RDIR_STAFF.SHAREHOLDER,RDIR_STAFF.PERCENT_SHARE,RDIR_STAFF.NOMINAL_VALUE)
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
		
	public static LinkedList<CompanyBank> getBanks(AONContext ctx,int enterprise) {
		LinkedList<CompanyBank> list = new LinkedList<CompanyBank>();
		list.addAll(
			ctx.getDslContext().select(RBANK.ID,RBANK.BANK_ACCOUNT,RBANK.BIC,RBANK.ALIAS)
			.from(COMPANY)
			.join(RBANK).on( COMPANY.REGISTRY.equal(RBANK.REGISTRY) )
			.where(COMPANY.REGISTRY.equal(enterprise))
			.and(RBANK.ACTIVE.equal((byte) 1))
			.fetch()
			.stream()
			.map(record -> new CompanyBank()
				.setId(record.getValue(RBANK.ID) )
				.setBankAccount(record.getValue(RBANK.BANK_ACCOUNT) )
				.setBic(record.getValue(RBANK.BIC) )
				.setBic(record.getValue(RBANK.BIC) )
				.setAlias(record.getValue(RBANK.ALIAS) ) 
				)
			.collect(Collectors.toList()));
		return list;
	}

}
