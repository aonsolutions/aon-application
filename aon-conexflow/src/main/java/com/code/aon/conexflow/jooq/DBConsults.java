package com.code.aon.conexflow.jooq;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;

import java.sql.Date;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.jooq.Condition;
import org.jooq.Record1;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.XMLUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;

public class DBConsults {

	//-------------------- GETS

	public static String getEnterpriseId(Domain domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
			return  ctx.getDslContext()
			.select(ENTERPRISE.REGISTRY)
			.from(ENTERPRISE)
			.where(ENTERPRISE.DOMAIN.eq(domain.getId()))
			.fetchOne().value1().toString();
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static ConexFlow getConexFlowLastOperation(Domain domain, Integer project){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
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
	
	public static ConexFlow getConexFlowLastOperation(Domain domain, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
			
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
	
	public static Boolean getConexFlowLastOperationBool(Domain domain, Integer project, String op){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
			Record1<Integer> data = ctx.getDslContext().select(PROJECT_ATTACH.ID)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op))
				.limit(1).fetchOne();
			
			return data != null && data.value1() != null;
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Integer getConexFlowLastOperationId(Domain domain, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
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
	
	public static ConexFlowConnection getConection(Domain domain){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId());
			ApplicationParameter server = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_PARAM);
			ApplicationParameter serverAck = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
			ApplicationParameter user = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_USER);
			ApplicationParameter paymethod = AppParamDAO.fetchOne(ctx, AppParam.PMS_CONEXFLOW_PAYMETHOD);
			
			ConexFlowConnection cfc = new ConexFlowConnection();
			cfc.setActive(server != null);
			if(cfc.getActive()){
				cfc.setServer(server.getValue());
				cfc.setServerAck(serverAck.getValue());
				cfc.setCfUser(user.getValue());
				cfc.setPayMethod(Integer.parseInt(paymethod.getValue()));
			}
			return cfc;
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	//-------------------- INSERTS
	
	public static void insertConexFlowOperation(Domain domain, byte[] xmlFile, Integer project, String op){
		Date currentDate = new Date(Calendar.getInstance().getTime().getTime());
		Integer id = getConexFlowLastOperationId(domain, project,op);
		Attach attach = Attach.projectAttach(project, domain, MimeType.XML, "CONEXFLOW-"+op, xmlFile, true, currentDate, null);
		if(id != null ){
			attach.setId(id);AON.update(attach);
			if(op.equals(ConexFlowConstant.CREATE_TOKEN_OP)){
				deletePreuthorization(domain, project);
			}
		}
		if(id == null) AON.insert(attach);
	}
	
	//-------------------- DELETES
	
	public static void deletePreuthorization(Domain domain, Integer projectId) {
		Attach attach = Attach.projectAttach(projectId, domain, MimeType.XML, "CONEXFLOW-P", null, true, null, null);
		Condition condition = PROJECT_ATTACH.PROJECT.eq(projectId).and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-P"));
		AON.delete(attach, condition);
	}
	
	public static void deletePreuthorization(Domain domain, Integer projectId, Attach attach) {
		Condition condition = PROJECT_ATTACH.PROJECT.eq(projectId).and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-P"));
		AON.delete(attach, condition);
	}
	

	
	
}
