package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AppParam;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.CompanyFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DomainCustomerDAO {
	
	private static Stream<DomainCompany> getDomains(AONContext ctx, Condition condition) {
		return ctx.getDslContext().select()
				.from(DOMAIN)
				.leftJoin(COMPANY).on(COMPANY.DOMAIN.eq(DOMAIN.ID))
				.leftJoin(REGISTRY).on(COMPANY.REGISTRY.eq(REGISTRY.ID))
				.leftJoin(AppParam.APP_PARAM).on(
						AppParam.APP_PARAM.DOMAIN.eq(DOMAIN.ID)
						.and(APP_PARAM.NAME.eq(com.esferalia.aon.occam.api.model.type.AppParam.AON_DOMAIN_PAYER.toString()))
						.and(APP_PARAM.VALUE.isNotNull())
						.and(DSL.trim(APP_PARAM.VALUE).ne(""))
				).where(condition)
				.fetch()
				.stream()
				.map(rec -> new DomainCompany()
						.setCompany(new CompanyFiller().apply(rec))
						.setDomain(new DomainFiller().apply(rec)));
	}
	
	public static Stream<DomainCompany> getAllDomains(AONContext ctx){
		return getDomains(ctx, DOMAIN.ID.gt(0).and(DOMAIN.PARENT.isNull().or(AppParam.APP_PARAM.ID.isNotNull())));
	}
	
	public static Stream<DomainCompany> getAllLinkedDomains(AONContext ctx){
		return getDomains(ctx, DOMAIN.PARENT.isNull().or(AppParam.APP_PARAM.ID.isNotNull()).and(DOMAIN.AONCUSTOMER.isNotNull()));
	}
	
	public static Stream<DomainCompany> getCustomerDomains(AONContext ctx, Integer customer){
		Condition condition = DOMAIN.PARENT.isNull().or(AppParam.APP_PARAM.ID.isNotNull());

		if (customer == null) {
			condition = condition.and(DOMAIN.AONCUSTOMER.isNull());
		} else {
			condition = condition.and(DOMAIN.AONCUSTOMER.eq(customer));
		}
		
		return getDomains(ctx, condition);
	}
	
	public static Stream<DomainCompany> getDomainsByDocument(AONContext ctx, String customerDocument, Integer customerId){
		Condition condition = DOMAIN.PARENT.isNull().or(AppParam.APP_PARAM.ID.isNotNull());
		
		Condition documentCondition = REGISTRY.DOCUMENT.isNull().or(DSL.trim(REGISTRY.DOCUMENT).eq(""));
		
		if (AonStringUtils.isNotBlank(customerDocument)) {
			documentCondition = documentCondition.or(DSL.lower(DSL.trim(REGISTRY.DOCUMENT)).eq(AonStringUtils.trim(customerDocument).toLowerCase()));
		}
		if (customerId != null && customerId > 0) {
			documentCondition = documentCondition.or(DOMAIN.AONCUSTOMER.eq(customerId));
		}
		condition = condition.and(documentCondition);
		
		return getDomains(ctx, condition);
	}
	
	public static List<Domain> updateDomains(AONContext ctx, List<DomainCompany> domainCompanies, Customer customer) {
		List<Domain> updatedDomains = new LinkedList<>();
		if (domainCompanies != null && !domainCompanies.isEmpty()) {
			for (DomainCompany domainCompany : domainCompanies) {
				Domain domain = domainCompany.getDomain();
				if (domain != null && domain.getId() != null && AonStringUtils.isNotBlank(domain.getName())) {
					AonStatus aonStatus = domain.getAonStatus();
					if (customer != null && customer.getId() != null) {						
						updatedDomains.add(DomainDAO.updateDomainCustomer(ctx, domain.getId(), domain.getName(), customer.getId(), aonStatus));
					} else {
						updatedDomains.add(DomainDAO.updateDomainCustomer(ctx, domain.getId(), domain.getName(), null, aonStatus));
					}
				}
			}
		}
		return updatedDomains;
	}
	
}
