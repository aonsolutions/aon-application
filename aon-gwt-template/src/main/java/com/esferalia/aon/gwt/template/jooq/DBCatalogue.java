package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.jooq.Record8;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.CatalogueInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;



public class DBCatalogue {
	
	public static LinkedList<Workplace> getWorkplaces(Domain domain, User user){
		try (CloseableAONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin())) {
			return WorkplaceDAO.getStream(ctx, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toCollection(LinkedList::new));
		}
	}
	
	public static LinkedList<Department> getDepartments(Domain domain, Integer workplaceId, String login){
		return AON.getDepartmentList(domain.getName(), domain.getId(), login,
				workplaceId, f -> f.getDomainProperty().eq(domain.getId()));
	}
	
	public static Workplace getWorkplace(Domain domain, User user, String workplaceDescription) {
		Occam occam = new Occam().setDomainName(domain.getName()).setDomain(domain.getId()).setUser(user.getLogin());
		return AON.getWorkplace(occam, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getDescriptionProperty().eq(workplaceDescription)),
			new Options().setSecurity(false));
	}
	
	public static Workplace getWorkplace(Domain domain, User user, Integer workplaceId){
		Occam occam = new Occam().setDomainName(domain.getName()).setDomain(domain.getId()).setUser(user.getLogin());
		return AON.getWorkplace(occam, f -> f.getIdProperty().eq(workplaceId), new Options().setSecurity(false));
	}
	
	public static Department getDepartment(Domain domain, Workplace wp, String departmentName, String login){
		return AON.getDepartment(domain.getName(), domain.getId(), login, wp.getId(), 
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(departmentName)));
	}
	
	public static Department getDepartment(Domain domain, Workplace wp, Integer departmentId, String login) {
		return AON.getDepartment(domain.getName(), domain.getId(), login, wp.getId(), 
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(departmentId)));
	}
	
	public static LinkedList<CatalogueInfo> getCatalogues(Domain domain, Workplace wp, Department dt, String login){
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Date today = new Date(new java.util.Date().getTime());
			Result<Record8<Integer, String, Integer, Integer, String, String, String, String>> record = null;
			if(wp != null && dt != null){
				record = ctx.getDslContext().selectDistinct(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE,
						WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.CODE)
				.from(CATALOGUE).join(CATALOGUE_ITEM).on(CATALOGUE.ID.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(RITEM).on(RITEM.ITEM.eq(ITEM.ID))
				.where(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.and(WORKPLACE_DEPARTMENT.DEPARTMENT.eq(dt.getId()))
				.and(CATALOGUE_ITEM.DOMAIN.eq(domain.getId()))
				.and(PRODUCT.STATUS.eq((byte)0))
				.and(RITEM.WORKPLACE.eq(wp.getId()).or(RITEM.WORKPLACE.isNull()))
				.and(WORKPLACE_DEPARTMENT.ACTIVE.eq((byte)1))
				.and(CATALOGUE.PURCHASE.eq((byte) 1))
				.and(CATALOGUE.START_DATE.lessThan(today))
				.and(CATALOGUE.END_DATE.greaterThan(today).or(CATALOGUE.END_DATE.isNull()))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			else if(wp != null && dt == null){
				record = ctx.getDslContext().select(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.CODE)
				.from(CATALOGUE).join(CATALOGUE_ITEM).on(CATALOGUE.ID.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(RITEM).on(RITEM.ITEM.eq(ITEM.ID))
				.where(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.and(CATALOGUE_ITEM.DOMAIN.eq(domain.getId()))
				.and(PRODUCT.STATUS.eq((byte)0))
				.and(RITEM.WORKPLACE.eq(wp.getId()).or(RITEM.WORKPLACE.isNull()))
				.and(WORKPLACE_DEPARTMENT.ACTIVE.eq((byte)0))
				.and(CATALOGUE.PURCHASE.eq((byte) 1))
				.and(CATALOGUE.START_DATE.lessThan(today))
				.and(CATALOGUE.END_DATE.greaterThan(today).or(CATALOGUE.END_DATE.isNull()))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			else if(wp == null && dt == null){
				record = ctx.getDslContext().select(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3, PRODUCT.CODE)
				.from(CATALOGUE).join(CATALOGUE_ITEM).on(CATALOGUE.ID.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(CATALOGUE_ITEM.DOMAIN.eq(domain.getId()))
				.and(PRODUCT.STATUS.eq((byte)0))
				.and(WORKPLACE_DEPARTMENT.ACTIVE.eq((byte)0))
				.and(CATALOGUE.PURCHASE.eq((byte) 1))
				.and(CATALOGUE.START_DATE.lessThan(today))
				.and(CATALOGUE.END_DATE.greaterThan(today).or(CATALOGUE.END_DATE.isNull()))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			
			if(record != null){
				LinkedList<CatalogueInfo> cs = new LinkedList<CatalogueInfo>();
				record.stream().forEach(r -> {
					CatalogueInfo c = new CatalogueInfo();
					Workplace w = getWorkplace(domain, new User().setLogin(login), r.value3());
					Department d = getDepartment(domain, w, r.value4(), login);
					c.setDepartment(d.getName());
					c.setWorkplace(w.getDescription());
					c.setProductCode(r.getValue(PRODUCT.CODE));
					c.setProductName(r.getValue(PRODUCT.NAME));
					if(r.value5() != null) c.setDetail(r.value5());
					else c.setDetail("");
					if(r.value6() != null) c.setDetail2(r.value6());
					else c.setDetail2("");
					if(r.value7() != null)c.setDetail3(r.value7());
					else c.setDetail3("");
					cs.add(c);
				});
				return cs;
			}
			return null;
		} finally{
			if (ctx != null) ctx.close();	
		}
	}
	
	public static Boolean isParentUser(Domain domain, User user) {
		if(user.getDomain().getId() == null){
			user = AON.getUser(domain.getName(), domain.getId(), user.getLogin());
		}
		Domain domainAux = AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
		return domainAux.getParentId() != null && domainAux.getParentId().equals(user.getDomain().getId());
	}
	
}
