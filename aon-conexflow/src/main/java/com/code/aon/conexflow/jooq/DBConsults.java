package com.code.aon.conexflow.jooq;

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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Result;

import com.code.aon.conexflow.ConexFlow;
import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.ConexFlowConnection;
import com.code.aon.conexflow.ConexFlowStatus;
import com.code.aon.conexflow.XMLUtils;
import com.code.aon.customer.Customer;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.registry.Registry;
import com.esferalia.aon.jooq.tables.records.ProjectReservationRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class DBConsults {
	
	private static final Logger LOGGER  = Logger.getLogger(DBConsults.class.getName());
	
	public static Stream<String> getConexFlowDescription(Domain domain, String login, ConexFlowStatus status, Integer project){
		return AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getDescriptionProperty().like(getStatusDescriptionWithoutToken(status)))
				.and(f.getAttachModuleProperty().eq(project))
				, AttachType.PROJECT, false).map(r -> r.getDescription());
	}
	
	//-------------------- GETS

	public static Stream<ConexFlow> getConexFlowStreamX(Domain domain, String login, String description){
		return AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getDescriptionProperty().like(description)
					//.and(f.getTypeProperty().eq(ProjectAttachmentType.CONEXFLOW.value()))
				, AttachType.PROJECT, true).map( r-> {
					if(r.getData() == null && r.getDriveId() != null){
						r.setData(DriveUtils.getByteFile(domain.getName(), domain.getId(), login,
								r.getDriveId(), r.getId()));
					}
					try {
						return  XMLUtils.readXml(r.getData(), new Query())
								.setId(r.getId())
								.setDate(r.getDate())
								.setProject(r.getAttachModule())
								.setDescription(r.getDescription());
					} catch (JAXBException e) {
						e.printStackTrace();
					}
					return null;
					
				});
	}
	
	public static Stream<ConexFlow> getConexFlowStreamWD(Domain domain, String login, String description){
		return AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getDescriptionProperty().like(description)
					.and(f.getDomainProperty().eq(domain.getId()))
					//.and(f.getTypeProperty().eq(ProjectAttachmentType.CONEXFLOW.value()))
				, AttachType.PROJECT, false).map( r-> {
					return new ConexFlow()
					 	.setId(r.getId())
					 	.setDate(r.getDate())
					 	.setProject(r.getAttachModule())
					 	.setDescription(r.getDescription());					
				});
	}
	
	public static Boolean hasConexFlow(Domain domain, String login, Integer project, String[] descriptions){
		Optional<Attach> attach = AON.getAttachStream(domain.getName(), domain.getId(), login,
				f ->  hasConexFlowFilter(project, descriptions, f), AttachType.PROJECT, false).findFirst();
		return attach.isPresent();
	}
	
	public static Filter hasConexFlowFilter(Integer project, String[] descriptions, AttachProperties f) {
		Filter filter = f.getAttachModuleProperty().eq(project);
		Filter descriptionFilter = null;
		for(Integer i = 0; i < descriptions.length; i++){
			if(i == 0){
				descriptionFilter = f.getDescriptionProperty().like(descriptions[i]);
			} else {
				descriptionFilter = descriptionFilter.or(f.getDescriptionProperty().like(descriptions[i]));
			}
		}
		filter = filter.and(descriptionFilter);
		return filter;
	}
	
	public static String getStatusDescription(String token, ConexFlowStatus status) {
		return "CONEXFLOW_(" + token.substring(token.length()-5) + ")_" + status.getName() + "#%";
	}
	
	public static String getStatusDescriptionWithoutToken(ConexFlowStatus status) {
		return "CONEXFLOW%" + status.getName() + "#%";
	}
	
	public static ConexFlow getConexFlowX(Domain domain, String login, Integer project, String description){
		Optional<Attach> attach = AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getAttachModuleProperty().eq(project)
				.and(f.getDescriptionProperty().like(description))
				//.and(f.getTypeProperty().eq(ProjectAttachmentType.CONEXFLOW.value()))
				, AttachType.PROJECT, true)
			.sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate())).findFirst();
		if(attach.isPresent()){
			if(attach.get().getData() == null && attach.get().getDriveId() != null){
				attach.get().setData(DriveUtils.getByteFile(domain.getName(), domain.getId(), login,
						attach.get().getDriveId(), attach.get().getId()));
			}
			try {
				return  XMLUtils.readXml(attach.get().getData(), new Query())
						.setId(attach.get().getId())
						.setDate(attach.get().getDate())
						.setProject(attach.get().getAttachModule())
						.setDescription(attach.get().getDescription());
			} catch (JAXBException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}
		return null;
	}
	
	// Without Data
	public static ConexFlow getConexFlowWD(Domain domain, String login, Integer project, String description){
		Optional<Attach> attach = AON.getAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getAttachModuleProperty().eq(project)
				.and(f.getDescriptionProperty().like(description))
				, AttachType.PROJECT, false)
			.sorted((a1, a2) -> a2.getDate().compareTo(a1.getDate())).findFirst();
		if(attach.isPresent()){
			return new ConexFlow()
					.setId(attach.get().getId())
					.setDate(attach.get().getDate())
					.setProject(attach.get().getAttachModule())
					.setDescription(attach.get().getDescription());
		}
		return null;
	}
	/**
	 * Devuelve la última operación realizada.
	 * @param domain
	 * @param login
	 * @param project
	 * @param token
	 * @return
	 */
	public static ConexFlow getConexFlowLastX(Domain domain, String login, Integer project, String token){
		String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_%";
		return getConexFlowX(domain, login, project, description);
	}
	
	/**
	 * Devuelve la última operación realizada de tipo 'op', incluyendo fallidas, canceladas, ...
	 * @param domain 
	 * @param login -> user name 
	 * @param project -> Project id
	 * @param token
	 * @param op -> ( P | C | V | D | A | R | E | T | B | N | S ) 
	 * @return
	 */
	public static ConexFlow getConexFlowLastOperationX(Domain domain, String login, Integer project, String token, String op){
		String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_" + op + "%";
		return getConexFlowX(domain, login, project, description);
	}
	
	/**
	 * Devuelve la última operación realizada con el estado ('status') introducido.
	 * @param domain
	 * @param login
	 * @param project
	 * @param token
	 * @param status
	 * @return
	 */
	public static ConexFlow getConexFlowLastStatusX(Domain domain, String login, Integer project, String token, ConexFlowStatus status){
		String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_" + status.getName() + "#%";
		return getConexFlowX(domain, login, project, description);
	}
	
	public static ConexFlow getConexFlowLastStatusWD(Domain domain, String login, Integer project, String token, ConexFlowStatus status){
		String description = "CONEXFLOW_(" + token.substring(token.length()-5) + ")_" + status.getName() + "#%";
		return getConexFlowWD(domain, login, project, description);
	}
	
	public static Stream<ConexFlow> getConexFlowStatusStreamX(Domain domain, String login, ConexFlowStatus status){
		String description = "CONEXFLOW_(%)_" + status.getName() + "#%";
		return getConexFlowStreamX(domain, login, description);
	}
	
	public static void updateConexFlowDescription(Domain domain, String login, Integer attachId, String description){
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(attachId), AttachType.PROJECT);
		attach.setDescription(description);
		AON.updateAttach(domain.getName(), domain.getId(), login, attach);
	}
	
	public static void updateConexFlowPayslipDescription(Domain domain, String login, String filter, String description){
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> 
			f.getDescriptionProperty().eq(filter).and(f.getTypeProperty().eq(ProjectAttachmentType.PAYSLIP.value()))
			, AttachType.PROJECT);
		attach.setDescription(description);
		AON.updateAttach(domain.getName(), domain.getId(), login, attach);
	}
	
	public static ConexFlow getConexFlowLastOperation(Domain domain, Integer project){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
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
	
	public static ConexFlow getConexFlowLastOperation(Domain domain, String login, Integer project, String op){
		//TODO COGER LA ULTIMA OPERACION CONEXFLOW (POR FECHA) DE PROJECT_ATTACH
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			Record3<Integer, byte[], String> data = ctx.getDslContext().select(PROJECT_ATTACH.ID, PROJECT_ATTACH.DATA, PROJECT_ATTACH.DRIVEID)
				.from(PROJECT_ATTACH)
				.where(PROJECT_ATTACH.PROJECT.eq(project))
				.and(PROJECT_ATTACH.DESCRIPTION.eq("CONEXFLOW-"+op))
				.orderBy(PROJECT_ATTACH.ATTACH_DATE.desc())
				.limit(1).fetchOne();

			if(data != null){ 
				byte[] b = data.getValue(PROJECT_ATTACH.DATA); 
				if(b == null){
					b = DriveUtils.getByteFile(domain.getName(), domain.getId(), login, 
						data.getValue(PROJECT_ATTACH.DRIVEID), data.getValue(PROJECT_ATTACH.ID));
				}
				try {
					return  XMLUtils.readXml(b, new Query());
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
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
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
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
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
		ApplicationParameter server = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.PMS_CONEXFLOW_SERVER_PARAM);
		ApplicationParameter serverAck = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.PMS_CONEXFLOW_SERVER_ACK_PARAM);
		ApplicationParameter user = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.PMS_CONEXFLOW_USER);
		ApplicationParameter keyA = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.PMS_CONEXFLOW_KEY_A);
		ApplicationParameter keyB = AON.getApplicationParameter(domain.getName(), domain.getId(), "", AppParam.PMS_CONEXFLOW_KEY_B);

		return new ConexFlowConnection()
				.setActive(server != null && server.getValue() != null && !server.getValue().equals("Null"))
				.setServer(server.getValue())
				.setServerAck(serverAck.getValue())
				.setCfUser(user.getValue())
				.setKeyA(keyA.getValue())
				.setKeyB(keyB.getValue());
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
		return AON.getDomain(domainCon, 1, "", f-> f.getNameProperty().eq(domainCon));
	}
	
	//-------------------- INSERTS
	
	public static ConexFlow insertConexFlow(Domain domain, String login, ConexFlow conexFlow, Integer project, String description){		
		Integer id = AON.insertAttach(domain.getName(), domain.getId(), login, new Attach(AttachType.PROJECT)
				.setAttachModule(project)
				.setDomain(domain)
				.setMimeType(MimeType.XML)
				.setDescription(description)
				.setData(conexFlow.getData())
				.setConfidential(false)
				.setDate(AonDateUtils.toSql(new java.util.Date()))
				.setType(ProjectAttachmentType.CONEXFLOW.value()));
		return conexFlow.setId(id);
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
	
	public static String getProjectName(Domain domain, String login, Integer projectId){
		return AON.getProject(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(projectId)).getName();
	}
	
	public static String getHotelCode(AONContext ctx, Integer hotelId){
		Record1<String> result = ctx.getDslContext().select(HOTEL.CODE).from(HOTEL).where(HOTEL.ID.eq(hotelId)).limit(1).fetchOne();
		return result.value1();
	}
	
	public static String getCreditCardNumber(Domain domain, Integer project){
		com.esferalia.aon.occam.api.model.project.ProjectReservation pr = getProjectReservation(domain, new User().setLogin(""), project);
		return getSecureCreditCardNumber(pr.getCreditCardNumber());
	}
	
	public static String getCreditCardFechCad(Domain domain, Integer project){
		com.esferalia.aon.occam.api.model.project.ProjectReservation pr = getProjectReservation(domain, new User().setLogin(""), project);
		return pr.getCreditCardExpirationMonth() + "/" + pr.getCreditCardExpirationYear();
	}
	
	public static com.esferalia.aon.occam.api.model.project.ProjectReservation getProjectReservation(Domain domain, User user, Integer projectId){
		return AON.getProjectReservation(domain.getName(), domain.getId(), user.getLogin(), projectId);
	}
	
	public static com.esferalia.aon.occam.api.model.project.ProjectReservation getProjectReservation(Domain domain, User user, ProjectReservationFilter filter){
		return AON.getProjectReservation(domain.getName(), domain.getId(), user.getLogin(), filter);
	}
	
	public static Stream<com.esferalia.aon.occam.api.model.project.ProjectReservation> getProjectReservationStream(Domain domain, String login, ProjectReservationFilter filter){
		return AON.getProjectReservationStream(domain.getName(), domain.getId(), login, filter);
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
		reservation.setPenaltyValue(pr.getPenaltyValue());
		reservation.setDomain(pr.getDomain());
		return reservation;
	}
	
	public static ProjectReservation newProjectReservation(Domain domain, com.esferalia.aon.occam.api.model.project.ProjectReservation pr){
		AONContext ctx = null;
		try {
			ctx = new AONContext(DBConsults.getConnection(domain.getName()));
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
			reservation.setPenaltyValue(pr.getPenaltyValue());
			reservation.setDomain(domain.getId());
			return reservation;
		}finally{
			if(ctx != null) ctx.close();
		}
	}
	
	public static String getSecureCreditCardNumber(String cardNumber) {
		return StringUtils.repeat("*", 8) + cardNumber;
	}
}
