package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Catalogue.CATALOGUE;
import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;

import java.sql.Date;
import java.util.LinkedList;

import org.jooq.Record2;
import org.jooq.Record7;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.CatalogueInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.warehouse.Department;



public class DBCatalogue {
	
	public static LinkedList<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record2<Integer, String>> record;
			
			if(isParentUser(domain, user)){
				record = ctx.getDslContext().select(WORKPLACE.ID, WORKPLACE.DESCRIPTION)
						.from(WORKPLACE)
						.where(WORKPLACE.DOMAIN.eq(domain.getId()))
						.and(WORKPLACE.ACTIVE.eq((byte)1))
						.orderBy(WORKPLACE.DESCRIPTION)
						.fetch();				
			}
			else{
				record = ctx.getDslContext().select(WORKPLACE.ID, WORKPLACE.DESCRIPTION)
					.from(WORKPLACE).join(USER_SCOPE).on(WORKPLACE.SCOPE.eq(USER_SCOPE.SCOPE))
					.where(WORKPLACE.DOMAIN.eq(domain.getId()))
					.and(WORKPLACE.ACTIVE.eq((byte)1))
					.and(USER_SCOPE.USER_ID.eq(user.getId()))
					.orderBy(WORKPLACE.DESCRIPTION)
					.fetch();
			}
			LinkedList<com.esferalia.aon.gwt.template.shared.WorkPlace> v = new LinkedList<com.esferalia.aon.gwt.template.shared.WorkPlace>();
			record.stream().forEach(r -> {
				com.esferalia.aon.gwt.template.shared.WorkPlace w = new com.esferalia.aon.gwt.template.shared.WorkPlace() ;
				w.setId(r.value1());
				w.setName(r.value2());
				v.add(w);
			});
			return v;
			
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static LinkedList<Department> getDepartments(Domain domain, Integer workplaceId, String login){
		return AON.getDepartmentList(domain.getName(), domain.getId(), login,
				workplaceId, f -> f.getDomainProperty().eq(domain.getId()));
	}
	
	public static Workplace getWorkplace(Domain domain, User user, String workplaceDescription) {
		return AON.getWorkplace(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getDescriptionProperty().eq(workplaceDescription));
	}
	
	public static Workplace getWorkplace(Domain domain, User user, Integer workplaceId){
		return AON.getWorkplace(domain.getName(), domain.getId(), user.getLogin(),
				filter -> filter.getIdProperty().eq(workplaceId));
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
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Date today = new Date(new java.util.Date().getTime());
			Result<Record7<Integer, String, Integer, Integer, String, String, String>> record = null;
			if(wp != null && dt != null){
				record = ctx.getDslContext().selectDistinct(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE,
						WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
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
				record = ctx.getDslContext().select(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
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
				record = ctx.getDslContext().select(ITEM.PRODUCT, PRODUCT.NAME, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
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
					OldProduct p = AON.getProduct(domain.getName(), domain.getId(), login,
							f -> f.getIdProperty().eq(r.getValue(ITEM.PRODUCT))); 
					c.setProductCode(p.getCode());
					c.setProductName(p.getName());
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
		if(user.getDomain() == null){
			user = AON.getUser(domain.getName(), domain.getId(), user.getLogin());
		}
		Domain domainAux = AON.getDomain(domain.getName(), domain.getId(), user.getLogin());
		return domainAux.getParentId() != null && domainAux.getParentId().equals(user.getDomain());
	}
	
}
