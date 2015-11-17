package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.CatalogueItem.CATALOGUE_ITEM;
import static com.esferalia.aon.jooq.tables.Department.DEPARTMENT;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.WorkplaceDepartment.WORKPLACE_DEPARTMENT;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;


import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Result;

import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.esferalia.aon.gwt.template.server.CatalogueInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.security.User;



public class DBCatalogue {
	
	public static Vector<com.esferalia.aon.gwt.template.shared.WorkPlace> getWorkplaces(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record2<Integer, String>> record;
			if(isParentUser(ctx, user.getId(), domain.getId())){
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
			Vector<com.esferalia.aon.gwt.template.shared.WorkPlace> v = new Vector<com.esferalia.aon.gwt.template.shared.WorkPlace>();
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
	
	public static Vector<com.esferalia.aon.gwt.template.shared.Department> getDepartments(Integer domainId,String domain, Integer workplace, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Result<Record2<Integer, String>> record = ctx.getDslContext().selectDistinct(DEPARTMENT.ID, DEPARTMENT.NAME)
					.from(DEPARTMENT).join(WORKPLACE_DEPARTMENT).on(DEPARTMENT.ID.eq(WORKPLACE_DEPARTMENT.DEPARTMENT))
					.where(DEPARTMENT.DOMAIN.eq(domainId))
					.and(WORKPLACE_DEPARTMENT.WORKPLACE.eq(workplace))
					.fetch();
			
			Vector<com.esferalia.aon.gwt.template.shared.Department> v = new Vector<com.esferalia.aon.gwt.template.shared.Department>();
			record.stream().forEach(r -> {
				com.esferalia.aon.gwt.template.shared.Department d = new com.esferalia.aon.gwt.template.shared.Department() ;
				d.setId(r.value1());
				d.setName(r.value2());
				v.add(d);
			});
			return v;
			
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static WorkPlace getWorkplace(String workplace, Integer domainId, String domain, String login) {
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Record1<Integer> record = ctx.getDslContext().select(WORKPLACE.ID)
				.from(WORKPLACE)
				.where(WORKPLACE.DOMAIN.eq(domainId))
				.and(WORKPLACE.DESCRIPTION.eq(workplace))
				.fetchOne();
			if(record.value1() != null){
				WorkPlace w = new WorkPlace();
				w.setId(record.value1());
				w.setDescription(workplace);
				return w;
			}
			return null;
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	

	public static WorkPlace getWorkplace(Integer workplace, Integer domainId, String domain, String login){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Record2<String,Integer> record = ctx.getDslContext().select(WORKPLACE.DESCRIPTION,WORKPLACE.SCOPE)
				.from(WORKPLACE)
				.where(WORKPLACE.DOMAIN.eq(domainId))
				.and(WORKPLACE.ID.eq(workplace))
				.fetchOne();
			
			if(record != null && record.value1() != null){
				WorkPlace w = new WorkPlace();
				w.setId(workplace);
				w.setDescription(record.value1());
				Scope s = new Scope();
				s.setId(record.value2());
				w.setScope(s);
				return w;
			}
			return null;
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static Department getDepartment(WorkPlace wp, String department, Integer domainId, String domain, String login){
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Record1<Integer> record = ctx.getDslContext().selectDistinct(DEPARTMENT.ID)
				.from(DEPARTMENT).join(WORKPLACE_DEPARTMENT)
				.on(WORKPLACE_DEPARTMENT.DEPARTMENT.eq(DEPARTMENT.ID))
				.where(DEPARTMENT.DOMAIN.eq(domainId))
				.and(DEPARTMENT.NAME.eq(department))
				.and(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.fetchOne();
			
			if(record.value1() != null){
				Department d = new Department();
				d.setId(record.value1());
				d.setName(department);
				return d;
			}
			return null;
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
public static Department getDepartment(WorkPlace wp, Integer department, Integer domainId, String domain, String login) {
		
	AONContext ctx = null;
	try {
		ctx = AONContext.getAONContext(domain, domainId, login);
			
			Record1<String> record = ctx.getDslContext().selectDistinct(DEPARTMENT.NAME)
				.from(DEPARTMENT).join(WORKPLACE_DEPARTMENT)
				.on(WORKPLACE_DEPARTMENT.DEPARTMENT.eq(DEPARTMENT.ID))
				.where(DEPARTMENT.DOMAIN.eq(domainId))
				.and(DEPARTMENT.ID.eq(department))
				.and(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.fetchOne();
			
			if(record.value1() != null){
				Department d = new Department();
				d.setId(department);
				d.setName(record.value1());
				return d;
			}
			return null;
		} finally{
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<CatalogueInfo> getCatalogues(String domain, Integer domainId, WorkPlace wp, Department dt, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Result<Record6<Integer, Integer, Integer, String, String, String>> record = null;
			if(wp != null && dt != null){
				record = ctx.getDslContext().selectDistinct(ITEM.PRODUCT, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
				.from(CATALOGUE_ITEM).join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(RITEM).on(RITEM.ITEM.eq(ITEM.ID))
				.where(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.and(WORKPLACE_DEPARTMENT.DEPARTMENT.eq(dt.getId()))
				.and(CATALOGUE_ITEM.DOMAIN.eq(domainId))
				.and(PRODUCT.STATUS.eq((byte)0))
				.and(RITEM.WORKPLACE.eq(wp.getId()).or(RITEM.WORKPLACE.isNull()))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			else if(wp != null && dt == null){
				record = ctx.getDslContext().select(ITEM.PRODUCT, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
				.from(CATALOGUE_ITEM).join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.join(RITEM).on(RITEM.ITEM.eq(ITEM.ID))
				.where(WORKPLACE_DEPARTMENT.WORKPLACE.eq(wp.getId()))
				.and(CATALOGUE_ITEM.DOMAIN.eq(domainId))
				.and(PRODUCT.STATUS.eq((byte)0))
				.and(RITEM.WORKPLACE.eq(wp.getId()).or(RITEM.WORKPLACE.isNull()))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			else if(wp == null && dt == null){
				record = ctx.getDslContext().select(ITEM.PRODUCT, WORKPLACE_DEPARTMENT.WORKPLACE, WORKPLACE_DEPARTMENT.DEPARTMENT, ITEM.DETAIL, ITEM.DETAIL2, ITEM.DETAIL3)
				.from(CATALOGUE_ITEM).join(ITEM).on(ITEM.ID.eq(CATALOGUE_ITEM.ITEM))
				.join(WORKPLACE_DEPARTMENT).on(WORKPLACE_DEPARTMENT.CATALOGUE.eq(CATALOGUE_ITEM.CATALOGUE))
				.join(PRODUCT).on(PRODUCT.ID.eq(ITEM.PRODUCT))
				.where(CATALOGUE_ITEM.DOMAIN.eq(domainId))
				.and(PRODUCT.STATUS.eq((byte)0))
				.orderBy(PRODUCT.NAME)
				.fetch();
			}
			AONContext sctx = ctx;
			if(record != null){
				Vector<CatalogueInfo> cs = new Vector<CatalogueInfo>();
				record.stream().forEach(r -> {
					CatalogueInfo c = new CatalogueInfo();
					WorkPlace w = getWorkplace(r.value2(),domainId, domain, login);
					Department d = getDepartment(w, r.value3(), domainId, domain, login);
					c.setDepartment(d.getName());
					c.setWorkplace(w.getDescription());
					Product p = AON.getProduct(sctx, r.value1());
					c.setProductCode(p.getCode());
					c.setProductName(p.getName());
					if(r.value4() != null) c.setDetail(r.value4());
					else c.setDetail("");
					if(r.value5() != null) c.setDetail2(r.value5());
					else c.setDetail2("");
					if(r.value6() != null)c.setDetail3(r.value6());
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
	
	public  static  Boolean isParentUser(AONContext ctx, Integer userId, Integer domainId) {
				
				Record1<Integer> record = ctx.getDslContext().selectDistinct(USER.DOMAIN)
					.from(USER)
					.where(USER.ID.eq(userId))
					.fetchOne();
				
				Integer userDomain = record.value1();
				
				if(userDomain != domainId){
					Record1<Integer> record1 =	ctx.getDslContext().select(DOMAIN.PARENT)
					.from(DOMAIN)
					.where(DOMAIN.ID.eq(domainId))
					.fetchOne();
					
					Integer parentDomain =  record1.value1();
					
					return parentDomain == userDomain;
				}
				else return false;
	}
	
}
