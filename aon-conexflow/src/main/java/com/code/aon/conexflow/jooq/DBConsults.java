package com.code.aon.conexflow.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.sql.Date;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.jooq.Record1;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.XMLUtils;
import com.esferalia.aon.occam.api.AONContext;

public class DBConsults {

	public static String getEnterpriseId(String domain , Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			return  ctx.getDslContext()
			.select(ENTERPRISE.REGISTRY)
			.from(ENTERPRISE)
			.where(ENTERPRISE.DOMAIN.eq(domainId))
			.fetchOne().value1().toString();
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static void insertConexFlowOperation(String domain , Integer domainId, byte[] xmlFile, Integer project, String op){
		AONContext ctx = null;
		try {
			Date currentDate = new Date(Calendar.getInstance().getTime().getTime());
			ctx = AONContext.getAONContext(domain, domainId);
			Integer id = null;
			if(op.equals(ConexFlowConstant.CREATE_TOKEN_OP)){
				id = getConexFlowLastOperationId(domain, domainId, project,op);
				if(id != null){
					ctx.getDslContext().update(PROJECT_ATTACH).set(PROJECT_ATTACH.DATA, xmlFile)
						.where(PROJECT_ATTACH.ID.eq(id))
						.execute();
				}
			}
			if(id == null)
				ctx.getDslContext().insertInto(PROJECT_ATTACH, PROJECT_ATTACH.DOMAIN, PROJECT_ATTACH.PROJECT, PROJECT_ATTACH.MIMETYPE, PROJECT_ATTACH.DESCRIPTION, PROJECT_ATTACH.DATA, PROJECT_ATTACH.SECURITY_LEVEL, PROJECT_ATTACH.ATTACH_DATE, PROJECT_ATTACH.DRIVEID)
					.values(domainId, project,(byte) MimeType.MIME_XML.ordinal(), "CONEXFLOW-"+op, xmlFile,(byte) 1, currentDate, null)
					.returning(RATTACH.ID).fetchOne()
					.getId();
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static ConexFlow getConexFlowLastOperation(String domain , Integer domainId, Integer project){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Record1<byte[]> data = ctx.getDslContext().select(PROJECT_ATTACH.DATA)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.orderBy(PROJECT_ATTACH.ATTACH_DATE.desc())
				.limit(1).fetchOne();
			
			if(data != null && data.value1() != null){
				try {
					return  XMLUtils.readXml(data.value1(), new Query());
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static ConexFlow getConexFlowLastOperation(String domain , Integer domainId, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Record1<byte[]> data = ctx.getDslContext().select(PROJECT_ATTACH.DATA)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op))
				.orderBy(PROJECT_ATTACH.ATTACH_DATE.desc())
				.limit(1).fetchOne();
			
			if(data != null && data.value1() != null){
				try {
					return  XMLUtils.readXml(data.value1(), new Query());
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Boolean getConexFlowLastOperationBool(String domain , Integer domainId, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Record1<Integer> data = ctx.getDslContext().select(PROJECT_ATTACH.ID)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op))
				.orderBy(PROJECT_ATTACH.ATTACH_DATE.desc())
				.limit(1).fetchOne();
			
			return data != null && data.value1() != null;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Integer getConexFlowLastOperationId(String domain , Integer domainId, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			Record1<Integer> data = ctx.getDslContext().select(PROJECT_ATTACH.ID)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op))
				.orderBy(PROJECT_ATTACH.ATTACH_DATE.desc())
				.limit(1).fetchOne();
			
			if(data != null && data.value1() != null){
				return  data.value1();
			}
			return null;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
}
