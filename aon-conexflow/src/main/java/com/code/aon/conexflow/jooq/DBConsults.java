package com.code.aon.conexflow.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Hotel.HOTEL;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectReservation.PROJECT_RESERVATION;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowConstant;
import com.code.aon.conexflow.XMLUtils;
import com.code.aon.customer.Customer;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.Registry;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.BookingHolder;

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
	
	public static List<String> getHotels(AONContext ctx, String domainName, Integer domainId) {

			Result<Record2<Integer, String>> result = ctx.getDslContext().select(WORKPLACE.ID, WORKPLACE.DESCRIPTION )
				.from(HOTEL).join(WORKPLACE).on(HOTEL.WORKPLACE.eq(WORKPLACE.ID))
				.where(WORKPLACE.DOMAIN.eq(domainId))
				.and(WORKPLACE.ACTIVE.eq((byte)1))
				.orderBy(WORKPLACE.DESCRIPTION)
				.fetch();
			
			List<String> hs = new ArrayList<String>();
			
			result.stream().forEach(r ->{
				if(r.value2() != null) hs.add(r.value2());
			});
			return hs;
	}
	
	public static Domain getDomain(AONContext ctx, String domainCon){

			Result<Record3<Integer, String, String>> data = ctx.getDslContext().select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION)
				.from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainCon))
				.fetch();

			Domain domain = new Domain();
			if(data.get(0).value1()!= null)
				domain.setId(data.get(0).value1());
			if(data.get(0).value2()!= null)
				domain.setName(data.get(0).value2());
			if(data.get(0).value3()!= null)
				domain.setDescription(data.get(0).value3());
			return domain;
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
	
	public static void delete(Domain domain, Integer projectId, String op) {
		Attach attach = Attach.projectAttach(projectId, domain, MimeType.XML, "CONEXFLOW-"+op, null, true, null, null);
		Condition condition = PROJECT_ATTACH.PROJECT.eq(projectId).and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op));
		AON.delete(attach, condition);
	}
	
	
	public static Boolean estaHotel(AONContext ctx,Domain domain,String hotel){
		Result<Record1<Integer>> result = ctx.getDslContext().select(WORKPLACE.ID)
		.from(HOTEL).join(WORKPLACE).on(HOTEL.WORKPLACE.eq(WORKPLACE.ID))
		.where(WORKPLACE.DOMAIN.eq(domain.getId()))
		.and(WORKPLACE.ACTIVE.eq((byte)1))
		.and(WORKPLACE.DESCRIPTION.eq(hotel))
		.fetch();
		return result.isNotEmpty();
	}

	/**
	 * Connection getConnection(String domain), Devuelve la conexión con la BD.
	 * 
	 * @param domain, dominio para la conexión con la BD.
	 * @return Devuelve la conexión con la BD.
	 * @throws SQLException
	 */
	public static Connection getConnection(String domain){
		try {
			
			Connection connection= DatabaseUtil.getConnection(domain);
			return connection;
		} catch (AonConnectionException e) {
			try {
				throw new SQLException(e.getMessage(), e);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	
	public static List<ProjectReservationRecord> getProjectReservationList(AONContext ctx, Domain domain, Integer hotelId){
		Calendar calendar = Calendar.getInstance();
		Date currentDate = new Date(calendar.getTime().getTime());
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		Date date = new Date(calendar.getTime().getTime());
		
		Result<ProjectReservationRecord> result = ctx.getDslContext().select()
				.from(PROJECT_RESERVATION)
				.where(PROJECT_RESERVATION.DOMAIN.eq(domain.getId()))
				.and(PROJECT_RESERVATION.HOTEL.eq(hotelId))
				.and(PROJECT_RESERVATION.START_DATE.greaterOrEqual(currentDate))
				.and(PROJECT_RESERVATION.START_DATE.lessOrEqual(date))
				.fetchInto(PROJECT_RESERVATION);
		
		List<ProjectReservationRecord> l = new ArrayList<ProjectReservationRecord>();
		l.addAll(result);
		return l;
	}
	
	public static Integer getHotelId(AONContext ctx, Domain domain, String hotel){

		Result<Record1<Integer>> result = ctx.getDslContext().select(WORKPLACE.ID)
				.from(HOTEL).join(WORKPLACE).on(HOTEL.WORKPLACE.eq(WORKPLACE.ID))
				.where(WORKPLACE.DOMAIN.eq(domain.getId()))
				.and(WORKPLACE.ACTIVE.eq((byte)1))
				.and(WORKPLACE.DESCRIPTION.eq(hotel))
				.fetch();
		
		if(result.isNotEmpty()){
			if(result.get(0).value1() != null) return result.get(0).value1();
		}
		return null;
	}
	
	public static String getProjectName(AONContext ctx, Integer projectId){
		Record1<String> result = ctx.getDslContext().select(PROJECT.NAME).from(PROJECT).where(PROJECT.ID.eq(projectId)).limit(1).fetchOne();
		return result.value1();
	}
	
	public static String getHotelCode(AONContext ctx, Integer hotelId){
		Record1<String> result = ctx.getDslContext().select(HOTEL.CODE).from(HOTEL).where(HOTEL.ID.eq(hotelId)).limit(1).fetchOne();
		return result.value1();
	}
	
	
	
	
	public static ProjectReservation newProjectReservation(AONContext ctx, ProjectReservationRecord pr){
		ProjectReservation reservation = new ProjectReservation();
		reservation.setId(pr.getProject());
		reservation.setAdvance(pr.getAdvance());
		reservation.setAdvancedAmount(pr.getAdvance());
		reservation.setAdvanceInvoiced(pr.getAdvanceInvoiced() == 0);
		Customer customer = new Customer();
		customer.setId(pr.getAgency());
		Registry reg = new Registry();
		reg.setId(pr.getAgency());
		customer.setRegistry(reg);
		reservation.setAgency(customer);
		reservation.setBookingHolder(BookingHolder.values()[pr.getBookingHolder()]);
		Hotel hotel = new Hotel();
		hotel.setId(pr.getHotel());
		hotel.setCode(getHotelCode(ctx, pr.getHotel()));
		reservation.setHotel(hotel);
		reservation.setStartDate(pr.getStartDate());
		reservation.setEndDate(pr.getEndDate());
		reservation.setPenaltyDays(pr.getPenaltyDays());
		reservation.setDomain(pr.getDomain());
		return reservation;
	}
}
