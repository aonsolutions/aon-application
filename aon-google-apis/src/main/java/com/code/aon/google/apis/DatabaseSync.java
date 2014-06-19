package com.code.aon.google.apis;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.ConnectionInfo;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialActivity;
import com.esferalia.aon.google.sql.AbstractSQL.CommercialTracking;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Project;
import com.esferalia.aon.google.sql.AbstractSQL.Rattach;
import com.esferalia.aon.google.sql.AbstractSQL.Registry;
import com.esferalia.aon.google.sql.AbstractSQL.Task;
import com.esferalia.aon.google.sql.SQLConstants;
import com.esferalia.aon.google.sql.SQLConstants.CommercialActivityColumns;
import com.esferalia.aon.google.sql.SQLConstants.CommercialTrackingColumns;
import com.esferalia.aon.google.sql.SQLConstants.DomainColumns;
import com.esferalia.aon.google.sql.SQLConstants.DomainGserviceaccountColumns;
import com.esferalia.aon.google.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.google.sql.SQLConstants.MailAccountColumns;
import com.esferalia.aon.google.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.google.sql.SQLConstants.ProjectColumns;
import com.esferalia.aon.google.sql.SQLConstants.ProjectCommercialColumns;
import com.esferalia.aon.google.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.google.sql.SQLConstants.RmediaColumns;
import com.esferalia.aon.google.sql.SQLConstants.TaskColumns;
import com.esferalia.aon.google.sql.SQLConstants.UserColumns;
import com.esferalia.aon.google.sql.SQLConstants.UserScopeColumns;
import com.esferalia.aon.google.sql.SQLConstants.UserWorkgroupColumns;
import com.esferalia.aon.google.sql.SQLConstants.RattachColumns;


/**
 * @author aibanez
 */
public class DatabaseSync {

	/**
	 * 
	 * @return
	 * @throws AonConnectionException
	 */
	public static Map<String, String> getDomains() throws AonConnectionException {
		ConnectionInfo connectionInfo = ConnectionInfo
				.getDefaultConnectionInfo();
		
		return  connectionInfo.getDomains();
		
	}

	/**
	 * Connection getConnection(String domain), Devuelve la conexión con la BD.
	 * 
	 * @param domain, dominio para la conexión con la BD.
	 * @return Devuelve la conexión con la BD.
	 * @throws SQLException
	 */
	public static Connection getConnection(String domain) throws SQLException {
		try {
			
			Connection connection= DatabaseUtil.getConnection(domain);
			return connection;
		} catch (AonConnectionException e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	
	// ------------------------------------------ GOOGLE CALENDAR
	
	/**
	 * getEnterpriseEmail(int id,String domain), Busca el email de la empresa en
	 * la BD.
	 * 
	 * @param id
	 *            , Identificador del dominio de la empresa.
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve el correo electronico de la empresa.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static String getEnterpriseEmail(int id,String key)
			throws SQLException, AonConnectionException {
		
			ResultSet rs = null;
			Connection connection = null;
			PreparedStatement stmt = null;
			try {
				String sql = "SELECT RM.value FROM (" + SQLConstants.DOMAIN
						+ " AS D inner join " + SQLConstants.ENTERPRISE
						+ " AS E ON D." + DomainColumns.ID + "= E."
						+ EnterpriseColumns.DOMAIN + " )inner join "
						+ SQLConstants.RMEDIA + " AS RM ON RM."
						+ RmediaColumns.REGISTRY + "= E."
						+ EnterpriseColumns.REGISTRY + " WHERE RM."
						+ RmediaColumns.MEDIA + "=4 AND RM." + RmediaColumns.DOMAIN
						+ "= ?";

				connection = getConnection(key);
				stmt = connection.prepareStatement(sql);
				stmt.setInt(1, id);
				rs = stmt.executeQuery();

				String email;
				if (rs.next())
					email = rs.getString(RmediaColumns.VALUE);
				else
					email = null;

				return email;

			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			}
	}

	/**
	 * getSellerEmail(CommercialTracking commercialTracking,String domain),
	 * Busca el email del comercial del evento de la BD.
	 * 
	 * @param commercialTracking
	 *            , evento de la BD.
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve el correo electrónico del comercial.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static String getSellerEmail(CommercialTracking commercialTracking, String key) throws SQLException, AonConnectionException {
	
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT RM.value FROM " + SQLConstants.RMEDIA
					+ " AS RM inner join " + SQLConstants.COMMERCIAL_TRACKING
					+ " AS CT ON RM." + RmediaColumns.REGISTRY + "= CT."
					+ CommercialTrackingColumns.SELLER + " WHERE RM."
					+ RmediaColumns.MEDIA + "=4 AND CT."
					+ CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, commercialTracking.getId());
			rs = stmt.executeQuery();

			String email;
			if (rs.next())
				email = rs.getString(RmediaColumns.VALUE);
			else
				email = null;

			return email;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	/**
	 * 
	 * @param task
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Vector<String> getAtendeesEmail(Task task, String key) throws SQLException, AonConnectionException {
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql="SELECT RM."+RmediaColumns.VALUE
					+" FROM ((("+SQLConstants.TASK 
					+" AS T inner join "+SQLConstants.USER_WORKGROUP
					+" AS UW ON T."+TaskColumns.WORKGROUP+"= UW."+UserWorkgroupColumns.WORKGROUP
					+" ) inner join "+SQLConstants.USER
					+" AS U ON U."+ UserColumns.ID+"= UW."+ UserWorkgroupColumns.USER_ID
					+" ) inner join "+SQLConstants.RMEDIA
					+" AS RM ON RM."+RmediaColumns.REGISTRY+"=U."+UserColumns.REGISTRY
					+" ) WHERE RM."+RmediaColumns.MEDIA+"=4 AND T."+TaskColumns.WORKGROUP+ "= ?";
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, task.getWorkgroup());
			rs = stmt.executeQuery();
			
			Vector<String> emails = new Vector<String>();
			while(rs.next()){
				emails.add(rs.getString(RmediaColumns.VALUE));
			}
			
			return emails;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static CommercialTracking getCommercialTrackingOne(int id,String key) throws SQLException, AonConnectionException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT CT.*" + " FROM "
					+ SQLConstants.COMMERCIAL_TRACKING + " AS CT WHERE "
					+ CommercialTrackingColumns.ID + " = ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			rs.next();
				
			CommercialTracking commercialTracking = new CommercialTracking();
			commercialTracking.setId(rs
						.getInt(CommercialTrackingColumns.ID));
			commercialTracking.setDomain(rs
						.getInt(CommercialTrackingColumns.DOMAIN));
			commercialTracking.setDate(rs
					.getTimestamp(CommercialTrackingColumns.DATE));
			commercialTracking.setSeller(rs
					.getInt(CommercialTrackingColumns.SELLER));
			commercialTracking.setProjectCommercial(rs
					.getInt(CommercialTrackingColumns.PROJECT_COMMERCIAL));
			commercialTracking.setActivity(rs
					.getInt(CommercialTrackingColumns.ACTIVITY));
			commercialTracking.setComments(rs
					.getString(CommercialTrackingColumns.COMMENTS));
			commercialTracking.setStatus((short) rs
					.getInt(CommercialTrackingColumns.STATUS));
			commercialTracking
					.setNextCommercialTracking(rs
							.getInt(CommercialTrackingColumns.NEXT_COMMERCIAL_TRACKING));
			commercialTracking.setEndDate(rs
					.getTimestamp(CommercialTrackingColumns.END_DATE));
			commercialTracking.setOffer(rs
					.getInt(CommercialTrackingColumns.OFFER));
			commercialTracking.setAllDay(rs
					.getBoolean(CommercialTrackingColumns.ALLDAY));
			commercialTracking.setLocation(rs
					.getString(CommercialTrackingColumns.LOCATION));
			
		

			return commercialTracking;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Domain getDomainName(Integer id,String key) throws SQLException, AonConnectionException{
			ResultSet rs = null;
			Connection connection = null;
			PreparedStatement stmt = null;
			try {
				String sql="SELECT D.*"
						+" FROM "+SQLConstants.DOMAIN
						+" AS D inner join "+SQLConstants.COMMERCIAL_TRACKING
						+" AS CT ON CT."+CommercialTrackingColumns.DOMAIN+"=D."+DomainColumns.ID
						+" WHERE CT."+CommercialTrackingColumns.ID+"= ?";
				
				connection = getConnection(key);
				stmt = connection.prepareStatement(sql);
				stmt.setInt(1, id);
				rs = stmt.executeQuery();
				rs.next();
				
				Domain domain = new Domain();
				domain.setId(rs.getInt(DomainColumns.ID));
				domain.setName(rs.getString(DomainColumns.NAME));
				domain.setDescription(rs.getString(DomainColumns.DESCRIPTION));
				
				return domain;
				
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			}
		
	}

	/**
	 * getDomain(String domain), Busca todos los dominios de empresa en la BD
	 * 
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve un vector con todos los dominios de la BD.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Vector<Domain> getDomain(String domain) throws SQLException{
		ResultSet rs = null ;
		Connection connection = null ;
		PreparedStatement stmt = null;
		try {
			
			String sql="SELECT * FROM "+ SQLConstants.DOMAIN + " ORDER BY " + DomainColumns.NAME;
			
			connection = getConnection(domain);
			stmt= connection.prepareStatement(sql);
			
			rs= stmt.executeQuery();
			
			
			Vector<Domain> companies = new Vector<Domain>();
			
			while(rs.next()){
				Domain company = new Domain();
				company.setId(rs.getInt(DomainColumns.ID));
				company.setName(rs.getString(DomainColumns.NAME));
				company.setDescription(rs.getString(DomainColumns.DESCRIPTION));
				company.setParent(rs.getInt(DomainColumns.PARENT));
				company.setType(rs.getShort(DomainColumns.TYPE));
				company.setSubDomainSuffix(rs.getString(DomainColumns.SUBDOMAINSUFFIX));
				company.setEnableHeredity(rs.getBoolean(DomainColumns.ENABLEHEREDITY));
				company.setDomainManagement(rs.getBoolean(DomainColumns.DOMAINMANAGEMENT));
				company.setDisableDomainManagement(rs.getBoolean(DomainColumns.DISABLEDOMAINMANAGEMENT));
				company.setMaxDocumentSize(rs.getInt(DomainColumns.MAXDOCUMENTSIZE));
				company.setMaxDefinedUsers(rs.getInt(DomainColumns.MAXDEFINEDUSERS));
				company.setMaxTotalDocumentSize(rs.getInt(DomainColumns.MAXTOTALDOCUMENTSIZE));
				company.setActive(rs.getBoolean(DomainColumns.ACTIVE));
				company.setOwner(rs.getString(DomainColumns.OWNER));
				company.setCreationDate(rs.getTimestamp(DomainColumns.CREATION_DATE));
				company.setCreationUser(rs.getString(DomainColumns.CREATION_USER));
				company.setModificationDate(rs.getTimestamp(DomainColumns.MODIFICATION_DATE));
				company.setModificationUser(rs.getString(DomainColumns.MODIFICATION_USER));
				company.setExpirationDate(rs.getDate(DomainColumns.EXPIRATIONDATE));
				companies.add(company);
			}
			
			return companies;
		}finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
			if ( connection != null )
				connection.close();
		}
	}
	
	/**
	 * getCommercialTracking(Integer id, String domain), Busca todos los eventos
	 * de una empresa, mediante el dominio de la empresa.
	 * 
	 * @param id
	 *            , Identificador del dominio de la empresa.
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve un vector con todos los eventos de la empresa.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Map<Integer,Vector<CommercialTracking>> getCommercialTrackingAll(String key) throws SQLException, AonConnectionException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT *"+ " FROM "
					+ SQLConstants.COMMERCIAL_TRACKING ;

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			rs = stmt.executeQuery();

			Map<Integer,Vector<CommercialTracking>> map = new Hashtable<Integer,Vector<CommercialTracking>>();
			while (rs.next()) {
				CommercialTracking commercialTracking = new CommercialTracking();
				commercialTracking.setId(rs
						.getInt(CommercialTrackingColumns.ID));
				commercialTracking.setDomain(rs
						.getInt(CommercialTrackingColumns.DOMAIN));
				commercialTracking.setDate(rs
						.getTimestamp(CommercialTrackingColumns.DATE));
				commercialTracking.setSeller(rs
						.getInt(CommercialTrackingColumns.SELLER));
				commercialTracking.setProjectCommercial(rs
						.getInt(CommercialTrackingColumns.PROJECT_COMMERCIAL));
				commercialTracking.setActivity(rs
						.getInt(CommercialTrackingColumns.ACTIVITY));
				commercialTracking.setComments(rs
						.getString(CommercialTrackingColumns.COMMENTS));
				commercialTracking.setStatus((short) rs
						.getInt(CommercialTrackingColumns.STATUS));
				commercialTracking
						.setNextCommercialTracking(rs
								.getInt(CommercialTrackingColumns.NEXT_COMMERCIAL_TRACKING));
				commercialTracking.setEndDate(rs
						.getTimestamp(CommercialTrackingColumns.END_DATE));
				commercialTracking.setOffer(rs
						.getInt(CommercialTrackingColumns.OFFER));
				commercialTracking.setAllDay(rs
						.getBoolean(CommercialTrackingColumns.ALLDAY));
				commercialTracking.setLocation(rs
						.getString(CommercialTrackingColumns.LOCATION));
			
				if (map.containsKey(commercialTracking.getDomain())){
					map.get(commercialTracking.getDomain()).add(commercialTracking);
				}
				else {
					Vector<CommercialTracking> events=new Vector<CommercialTracking>();
					events.add(commercialTracking);
					map.put(commercialTracking.getDomain(), events);
			
				}
			}

			return map;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Vector<CommercialTracking> getCommercialTracking(Integer id, String key) throws SQLException, AonConnectionException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT CT.*" + " FROM "
					+ SQLConstants.COMMERCIAL_TRACKING + " AS CT inner join "
					+ SQLConstants.DOMAIN + " AS D ON CT."
					+ CommercialTrackingColumns.DOMAIN + " = D."
					+ DomainColumns.ID + " WHERE "
					+ CommercialTrackingColumns.DOMAIN + " = ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			Vector<CommercialTracking> events = new Vector<CommercialTracking>();

			while (rs.next()) {
				CommercialTracking commercialTracking = new CommercialTracking();
				commercialTracking.setId(rs
						.getInt(CommercialTrackingColumns.ID));
				commercialTracking.setDomain(rs
						.getInt(CommercialTrackingColumns.DOMAIN));
				commercialTracking.setDate(rs
						.getTimestamp(CommercialTrackingColumns.DATE));
				commercialTracking.setSeller(rs
						.getInt(CommercialTrackingColumns.SELLER));
				commercialTracking.setProjectCommercial(rs
						.getInt(CommercialTrackingColumns.PROJECT_COMMERCIAL));
				commercialTracking.setActivity(rs
						.getInt(CommercialTrackingColumns.ACTIVITY));
				commercialTracking.setComments(rs
						.getString(CommercialTrackingColumns.COMMENTS));
				commercialTracking.setStatus((short) rs
						.getInt(CommercialTrackingColumns.STATUS));
				commercialTracking
						.setNextCommercialTracking(rs
								.getInt(CommercialTrackingColumns.NEXT_COMMERCIAL_TRACKING));
				commercialTracking.setEndDate(rs
						.getTimestamp(CommercialTrackingColumns.END_DATE));
				commercialTracking.setOffer(rs
						.getInt(CommercialTrackingColumns.OFFER));
				commercialTracking.setAllDay(rs
						.getBoolean(CommercialTrackingColumns.ALLDAY));
				commercialTracking.setLocation(rs
						.getString(CommercialTrackingColumns.LOCATION));
				events.add(commercialTracking);
			}

			return events;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	/**
	 * getProject(CommercialTracking commercialTracking,String domain), Busca el
	 * proyecto al que pertenece el evento.
	 * 
	 * @param commercialTracking
	 *            , evento de la BD.
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve el projecto al que pertenece el evento.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Project getProject(CommercialTracking commercialTracking,String key) throws SQLException, AonConnectionException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT P.* FROM " + SQLConstants.COMMERCIAL_TRACKING
					+ " AS CT inner join " + SQLConstants.PROJECT
					+ " AS P ON P." + ProjectColumns.ID + "= CT."
					+ CommercialTrackingColumns.PROJECT_COMMERCIAL
					+ " WHERE CT." + CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, commercialTracking.getId());

			rs = stmt.executeQuery();

			Project project = new Project();

			if (rs.next()) {
				project.setId(rs.getInt(ProjectColumns.ID));
				project.setDomain(rs.getInt(ProjectColumns.DOMAIN));
				project.setName(rs.getString(ProjectColumns.NAME));
				project.setAlias(rs.getString(ProjectColumns.ALIAS));
				project.setRegistry(rs.getInt(ProjectColumns.REGISTRY));
				project.setDate(rs.getDate(ProjectColumns.DATE));
				project.setProjectType(rs.getInt(ProjectColumns.PROJECT_TYPE));
				project.setTas(rs.getBoolean(ProjectColumns.TAS));
				project.setCommercial(rs.getBoolean(ProjectColumns.COMMERCIAL));
				project.setDossier(rs.getBoolean(ProjectColumns.DOSSIER));
				project.setReservation(rs
						.getBoolean(ProjectColumns.RESERVATION));
				project.setActive(rs.getBoolean(ProjectColumns.ACTIVE));
			} else
				project = null;

			return project;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}		
	}

	/**
	 * 
	 * @param key
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Vector<Project> getProjectAll(String key) throws SQLException, AonConnectionException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT * FROM " + SQLConstants.PROJECT;
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			

			rs = stmt.executeQuery();
			
			Vector<Project> projects = new Vector<Project>();
			
			while (rs.next()){
				Project project = new Project();
				project.setId(rs.getInt(ProjectColumns.ID));
				project.setDomain(rs.getInt(ProjectColumns.DOMAIN));
				project.setName(rs.getString(ProjectColumns.NAME));
				project.setAlias(rs.getString(ProjectColumns.ALIAS));
				project.setRegistry(rs.getInt(ProjectColumns.REGISTRY));
				project.setDate(rs.getDate(ProjectColumns.DATE));
				project.setProjectType(rs.getInt(ProjectColumns.PROJECT_TYPE));
				project.setTas(rs.getBoolean(ProjectColumns.TAS));
				project.setCommercial(rs.getBoolean(ProjectColumns.COMMERCIAL));
				project.setDossier(rs.getBoolean(ProjectColumns.DOSSIER));
				project.setReservation(rs
						.getBoolean(ProjectColumns.RESERVATION));
				project.setActive(rs.getBoolean(ProjectColumns.ACTIVE));
				projects.add(project);
			}
			return projects;

		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}		
	}
		
	/**
	 * getActivity(CommercialTracking commercialTracking , String domain), Busca
	 * la actividad que se va a realizar en el evento.
	 * 
	 * @param commercialTracking
	 *            , evento de la BD.
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve la actividad que se va a realizar en el evento.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static CommercialActivity getActivity(
			CommercialTracking commercialTracking, String key)
			throws SQLException, AonConnectionException {
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT CA.* FROM " + SQLConstants.COMMERCIAL_TRACKING
					+ " AS CT inner join " + SQLConstants.COMMERCIAL_ACTIVITY
					+ " AS CA ON CA." + CommercialActivityColumns.ID + "=CT."
					+ CommercialTrackingColumns.ACTIVITY + " WHERE CT."
					+ CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, commercialTracking.getId());

			rs = stmt.executeQuery();

			CommercialActivity activity = new CommercialActivity();

			if (rs.next()) {
				activity.setDomain(rs.getInt(CommercialActivityColumns.DOMAIN));
				activity.setName(rs.getString(CommercialActivityColumns.NAME));
				activity.setId(rs.getInt(CommercialActivityColumns.ID));
				activity.setProbability(rs
						.getInt(CommercialActivityColumns.PROBABILITY));
				activity.setSurvey(rs.getInt(CommercialActivityColumns.SURVEY));
			} else
				activity = null;

			return activity;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}

	/**
	 * getPotencialClient(CommercialTracking commercialTracking, String domain),
	 * Busca el cliente potencial del evento.
	 * 
	 * @param commercialTracking
	 *            , evento de la BD
	 * @param domain
	 *            , dominio para la conexión con la BD.
	 * @return Devuelve el cliente potencial del evento.
	 * @throws SQLException
	 * @throws AonConnectionException 
	 */
	public static Registry getPotencialClient(
			CommercialTracking commercialTracking, String key)
			throws SQLException, AonConnectionException {

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT R.* FROM (" + SQLConstants.COMMERCIAL_TRACKING
					+ " AS CT inner join " + SQLConstants.PROJECT_COMMERCIAL
					+ " AS PC ON PC." + ProjectCommercialColumns.PROJECT
					+ "= CT." + CommercialTrackingColumns.PROJECT_COMMERCIAL
					+ ")inner join " + SQLConstants.REGISTRY + " AS R ON R."
					+ RegistryColumns.ID + " = PC."
					+ ProjectCommercialColumns.TARGET + " WHERE CT."
					+ CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, commercialTracking.getId());

			rs = stmt.executeQuery();

			Registry registry = new Registry();

			if (rs.next()) {
				registry.setId(rs.getInt(RegistryColumns.ID));
				registry.setAlias(rs.getString(RegistryColumns.ALIAS));
				registry.setDocument(rs.getString(RegistryColumns.DOCUMENT));
				registry.setDocumentCountry(rs
						.getString(RegistryColumns.DOCUMENT_COUNTRY));
				registry.setDocumentType(rs
						.getShort(RegistryColumns.DOCUMENT_TYPE));
				registry.setDomain(rs.getInt(RegistryColumns.DOMAIN));
				registry.setName(rs.getString(RegistryColumns.NAME));
				registry.setNationality(rs
						.getString(RegistryColumns.NATIONALITY));
				registry.setType(rs.getShort(RegistryColumns.TYPE));
				registry.setSecurityLevel(rs
						.getShort(RegistryColumns.SECURITY_LEVEL));
			} else
				registry = null;

			return registry;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}

	public static DomainGserviceaccount getServiceAccount(String key) throws SQLException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT DG.* FROM "+SQLConstants.DOMAIN_GSERVICEACCOUNT
					+ " AS DG inner join "+SQLConstants.DOMAIN
					+ " AS D ON (DG."+DomainGserviceaccountColumns.DOMAIN
					+ "= D."+ DomainColumns.ID+" OR D."+DomainColumns.PARENT+" = DG."+DomainGserviceaccountColumns.DOMAIN+")"
					+ " WHERE D."+DomainColumns.NAME+"= ?";
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);

			rs = stmt.executeQuery();
		
			DomainGserviceaccount dgserviceaccount= new DomainGserviceaccount();
			
			if (rs.next()) {
			
				dgserviceaccount.setClientId(rs.getString(DomainGserviceaccountColumns.CLIENT_ID));
				dgserviceaccount.setClientSecret(rs.getAsciiStream(DomainGserviceaccountColumns.CLIENT_SECRET));
				dgserviceaccount.setDomain(rs.getInt(DomainGserviceaccountColumns.DOMAIN));
				dgserviceaccount.setEmailAddress(rs.getString(DomainGserviceaccountColumns.EMAIL_ADDRESS));
				dgserviceaccount.setPublicKey(rs.getString(DomainGserviceaccountColumns.PUBLIC_KEY));
				dgserviceaccount.setPrivateKey(rs.getAsciiStream(DomainGserviceaccountColumns.PRIVATE_KEY));
			
			}
			
		
		
			return dgserviceaccount;
		}finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	
	public static DomainGserviceaccount getGeneralServiceAccount(String key) throws SQLException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT DG.* FROM "+SQLConstants.DOMAIN_GSERVICEACCOUNT
					+ " WHERE DG."+DomainGserviceaccountColumns.DOMAIN+"= NULL";
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);

			rs = stmt.executeQuery();
		
			DomainGserviceaccount dgserviceaccount= new DomainGserviceaccount();
			
			if (rs.next()) {
			
				dgserviceaccount.setClientId(rs.getString(DomainGserviceaccountColumns.CLIENT_ID));
				dgserviceaccount.setClientSecret(rs.getAsciiStream(DomainGserviceaccountColumns.CLIENT_SECRET));
				dgserviceaccount.setDomain(rs.getInt(DomainGserviceaccountColumns.DOMAIN));
				dgserviceaccount.setEmailAddress(rs.getString(DomainGserviceaccountColumns.EMAIL_ADDRESS));
				dgserviceaccount.setPublicKey(rs.getString(DomainGserviceaccountColumns.PUBLIC_KEY));
				dgserviceaccount.setPrivateKey(rs.getAsciiStream(DomainGserviceaccountColumns.PRIVATE_KEY));
			
			}
			else dgserviceaccount= null;
		
		
			return dgserviceaccount;
		}finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	// ------------------------------------------ TEST GOOGLE CALENDAR
	
	public static void addCommercialTracking(String key) throws SQLException, AonConnectionException {
	
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "INSERT INTO " + SQLConstants.COMMERCIAL_TRACKING
					+ " VALUES(?,?,?,?,?,?,?,?," + null + "," + null + ","
					+ null + ",?,? )";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, 62);
			stmt.setInt(2, 35);
			stmt.setTimestamp(3, new Timestamp(new Date().getTime()));
			stmt.setInt(4, 6171);
			stmt.setInt(5, 188);
			stmt.setInt(6, 31);
			stmt.setString(7, "reunion...");
			stmt.setInt(8, 0);
			stmt.setInt(9, 1);
			stmt.setString(10, "VITORIA");

			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}

	public static void deleteCommercialTracking(String key)
			throws SQLException, AonConnectionException {
		
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "DELETE FROM " + SQLConstants.COMMERCIAL_TRACKING
					+ " WHERE " + CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, 62);
			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}

	public static void updateCommercialTracking(String key)
			throws SQLException, AonConnectionException {

		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "UPDATE " + SQLConstants.COMMERCIAL_TRACKING + " SET "
					+ CommercialTrackingColumns.LOCATION + "= ?" + " WHERE "
					+ CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, "SAN SEBASTIAN");
			stmt.setInt(2, 62);
			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}

	}

	public static CommercialTracking getCommercialTrackingTest()
			throws SQLException, AonConnectionException {
		Map<String, String> domains=getDomains();
		
		for (String key : domains.keySet()) {
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {

			String sql = "SELECT CT.*" + " FROM "
					+ SQLConstants.COMMERCIAL_TRACKING + " AS CT" + " WHERE "
					+ CommercialTrackingColumns.DOMAIN + " = ? AND "
					+ CommercialTrackingColumns.ID + "= ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, 35);
			stmt.setInt(2, 62);
			rs = stmt.executeQuery();

			rs.next();
			CommercialTracking commercialTracking = new CommercialTracking();
			commercialTracking.setId(rs.getInt(CommercialTrackingColumns.ID));
			commercialTracking.setDomain(rs
					.getInt(CommercialTrackingColumns.DOMAIN));
			commercialTracking.setDate(rs
					.getTimestamp(CommercialTrackingColumns.DATE));
			commercialTracking.setSeller(rs
					.getInt(CommercialTrackingColumns.SELLER));
			commercialTracking.setProjectCommercial(rs
					.getInt(CommercialTrackingColumns.PROJECT_COMMERCIAL));
			commercialTracking.setActivity(rs
					.getInt(CommercialTrackingColumns.ACTIVITY));
			commercialTracking.setComments(rs
					.getString(CommercialTrackingColumns.COMMENTS));
			commercialTracking.setStatus((short) rs
					.getInt(CommercialTrackingColumns.STATUS));
			commercialTracking
					.setNextCommercialTracking(rs
							.getInt(CommercialTrackingColumns.NEXT_COMMERCIAL_TRACKING));
			commercialTracking.setEndDate(rs
					.getTimestamp(CommercialTrackingColumns.END_DATE));
			commercialTracking.setOffer(rs
					.getInt(CommercialTrackingColumns.OFFER));
			commercialTracking.setAllDay(rs
					.getBoolean(CommercialTrackingColumns.ALLDAY));
			commercialTracking.setLocation(rs
					.getString(CommercialTrackingColumns.LOCATION));

			return commercialTracking;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		}
		return null;
	}

	
	// ------------------------------------------ GOOGLE TASK
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Vector<Task> getTask() throws SQLException, AonConnectionException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
			String sql = "SELECT T.* FROM " + SQLConstants.TASK 
					+" AS T inner join " + SQLConstants.USER
					+" AS U ON T." + TaskColumns.TASK_HOLDER
					+" = U." + UserColumns.REGISTRY
					+" WHERE U." + UserColumns.LOGIN + "=?";
			
			connection = getConnection("demo.aonsolutions.net");// CONSEGUIR EL DOMINIO DE LA CONEXIÓN!!!!!!!!
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, "ander");// CONSEGUIR EL USERNAME DE LA CONEXIÓN!!!!!!!!
			rs = stmt.executeQuery();
			
			Vector<Task> tasks = new Vector<Task>();
			while (rs.next()){
				Task task= new Task();
				task.setActivityType(rs.getInt(TaskColumns.ACTIVITY_TYPE));
				task.setComments(rs.getString(TaskColumns.COMMENTS));
				task.setDescription(rs.getString(TaskColumns.DESCRIPTION));
				task.setDomain(rs.getInt(TaskColumns.DOMAIN));
				task.setDueDate(rs.getTimestamp(TaskColumns.DUE_DATE));
				task.setEndDate(rs.getTimestamp(TaskColumns.END_DATE));
				task.setId(rs.getInt(TaskColumns.ID));
				task.setPercent(rs.getShort(TaskColumns.PERCENT));
				task.setPriority(rs.getShort(TaskColumns.PRIORITY));
				task.setProject(rs.getInt(TaskColumns.PROJECT));
				task.setRegistry(rs.getInt(TaskColumns.REGISTRY));
				task.setRepeatPeriod(rs.getShort(TaskColumns.REPEAT_PERIOD));
				task.setSender(rs.getInt(TaskColumns.SENDER));
				task.setSource(rs.getShort(TaskColumns.SOURCE));
				task.setStartDate(rs.getTimestamp(TaskColumns.START_DATE));
				task.setStatus(rs.getShort(TaskColumns.STATUS));
				task.setTaskHolder(rs.getInt(TaskColumns.TASK_HOLDER));
				task.setWorkgroup(rs.getInt(TaskColumns.WORKGROUP));
				tasks.add(task);
			}
			return tasks;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}
	
	/**
	 * 
	 * @param taskId
	 * @return
	 * @throws SQLException
	 * @throws AonConnectionException
	 */
	public static Project getProjectTask(Integer taskId) throws SQLException, AonConnectionException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
			String sql = "SELECT P.* FROM " + SQLConstants.TASK 
					+" AS T inner join " + SQLConstants.PROJECT
					+" AS P ON T." + TaskColumns.PROJECT
					+" = P." + ProjectColumns.ID
					+" WHERE T." + TaskColumns.ID + "=?";
			
			connection = getConnection("demo.aonsolutions.net");// CONSEGUIR EL DOMINIO DE LA CONEXIÓN!!!!!!!!
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, taskId);
			rs = stmt.executeQuery();
			
			Project project= new Project();
			
			if (rs.next()) {
				project.setId(rs.getInt(ProjectColumns.ID));
				project.setDomain(rs.getInt(ProjectColumns.DOMAIN));
				project.setName(rs.getString(ProjectColumns.NAME));
				project.setAlias(rs.getString(ProjectColumns.ALIAS));
				project.setRegistry(rs.getInt(ProjectColumns.REGISTRY));
				project.setDate(rs.getDate(ProjectColumns.DATE));
				project.setProjectType(rs.getInt(ProjectColumns.PROJECT_TYPE));
				project.setTas(rs.getBoolean(ProjectColumns.TAS));
				project.setCommercial(rs.getBoolean(ProjectColumns.COMMERCIAL));
				project.setDossier(rs.getBoolean(ProjectColumns.DOSSIER));
				project.setReservation(rs
						.getBoolean(ProjectColumns.RESERVATION));
				project.setActive(rs.getBoolean(ProjectColumns.ACTIVE));
			} else
				project = null;

			return project;
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}
	
	
	// ------------------------------------------ GOOGLE DRIVE
	
	public static Vector<Rattach> getFiles(String key) throws SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT RA.* FROM " + SQLConstants.RATTACH 
					+" AS RA inner join "+SQLConstants.DOMAIN+" AS D ON RA."+RattachColumns.DOMAIN+" = D."+DomainColumns.ID
					+" WHERE D."+DomainColumns.NAME+" = ?";
			connection = getConnection(key);// CONSEGUIR EL DOMINIO DE LA CONEXIÓN!!!!!!!!
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			rs = stmt.executeQuery();
			
			Vector<Rattach> rattachs = new Vector<Rattach>();
			while (rs.next()){
				Rattach rattach= new Rattach();
				//todos los atributos de la tabla... error en el proyecto aon.sql.google
				rattach.setAttachDate(rs.getDate(RattachColumns.ATTACH_DATE));
				rattach.setId(rs.getInt(RattachColumns.ID));
				rattach.setDomain(rs.getInt(RattachColumns.DOMAIN));
				rattach.setRegistry(rs.getInt(RattachColumns.REGISTRY));
				rattach.setCategory(rs.getInt(RattachColumns.CATEGORY));
				rattach.setMimeType(rs.getShort(RattachColumns.MIMETYPE));
				rattach.setDescription(rs.getString(RattachColumns.DESCRIPTION));
				rattach.setData(rs.getAsciiStream(RattachColumns.DATA));
				rattach.setType(rs.getShort(RattachColumns.TYPE));
				rattach.setScope(rs.getInt(RattachColumns.SCOPE));
				rattach.setSecurityLevel(rs.getShort(RattachColumns.SECURITY_LEVEL));
				rattach.setAttachDate(rs.getDate(RattachColumns.ATTACH_DATE));
				rattach.setDriveId(rs.getString(RattachColumns.DRIVE_ID));

				rattachs.add(rattach);
			}
			return rattachs;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	public static Map<Integer,Vector<Rattach>> getFilesAll(String key) throws SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT RA.* FROM " + SQLConstants.RATTACH 
					+" AS RA ";
			connection = getConnection(key);// CONSEGUIR EL DOMINIO DE LA CONEXIÓN!!!!!!!!
			stmt = connection.prepareStatement(sql);
			rs = stmt.executeQuery();

			Map<Integer,Vector<Rattach>> map = new Hashtable<Integer,Vector<Rattach>>();
			while (rs.next()){
				Rattach rattach= new Rattach();
				//todos los atributos de la tabla... error en el proyecto aon.sql.google
				rattach.setAttachDate(rs.getDate(RattachColumns.ATTACH_DATE));
				rattach.setId(rs.getInt(RattachColumns.ID));
				rattach.setDomain(rs.getInt(RattachColumns.DOMAIN));
				rattach.setRegistry(rs.getInt(RattachColumns.REGISTRY));
				rattach.setCategory(rs.getInt(RattachColumns.CATEGORY));
				rattach.setMimeType(rs.getShort(RattachColumns.MIMETYPE));
				rattach.setDescription(rs.getString(RattachColumns.DESCRIPTION));
				rattach.setData(rs.getAsciiStream(RattachColumns.DATA));
				rattach.setType(rs.getShort(RattachColumns.TYPE));
				rattach.setScope(rs.getInt(RattachColumns.SCOPE));
				rattach.setSecurityLevel(rs.getShort(RattachColumns.SECURITY_LEVEL));
				rattach.setAttachDate(rs.getDate(RattachColumns.ATTACH_DATE));
				rattach.setDriveId(rs.getString(RattachColumns.DRIVE_ID));

				
				if (map.containsKey(rattach.getDomain())){
					map.get(rattach.getDomain()).add(rattach);
				}
				else {
					Vector<Rattach> events=new Vector<Rattach>();
					events.add(rattach);
					map.put(rattach.getDomain(), events);
			
				}
			}
			return map;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	public static Rattach getFile(int id,String key) throws SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT RA."+RattachColumns.ID+",RA."+RattachColumns.DOMAIN+",RA."+RattachColumns.MIMETYPE+",RA."+RattachColumns.DESCRIPTION+",RA."+RattachColumns.TYPE+",RA."
					+RattachColumns.DRIVE_ID + " FROM " + SQLConstants.RATTACH 
					+" AS RA inner join "+SQLConstants.DOMAIN+" AS D ON RA."+RattachColumns.DOMAIN+" = D."+DomainColumns.ID
					+" WHERE D."+DomainColumns.NAME+" = ? AND RA."+RattachColumns.ID+" = ?";
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setInt(2, id);
			rs = stmt.executeQuery();
			
			Rattach rattach = new Rattach();
			
			rattach.setId(rs.getInt(RattachColumns.ID));
			rattach.setDomain(rs.getInt(RattachColumns.DOMAIN));
			rattach.setMimeType(rs.getShort(RattachColumns.MIMETYPE));
			rattach.setDescription(rs.getString(RattachColumns.DESCRIPTION));
			rattach.setType(rs.getShort(RattachColumns.TYPE));
			rattach.setDriveId(rs.getString(RattachColumns.DRIVE_ID));
			
			return rattach;
		
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	public static InputStream getFileData(int id,String key) throws SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT "+RattachColumns.DATA +" FROM " + SQLConstants.RATTACH 
					+" WHERE "+RattachColumns.ID+" = ?";
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			
			InputStream data;
			if (rs.next())
				data = rs.getAsciiStream(RattachColumns.DATA);
			else
				data = null;
			
			
	
			return data;
		
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
		
	}
	
	public static DriveData getDomainFiles(String key) throws SQLException{
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT RA."+RattachColumns.ID+",RA."+RattachColumns.MIMETYPE+",RA."+RattachColumns.DESCRIPTION+",RA."+RattachColumns.TYPE+",RA."
								+RattachColumns.DRIVE_ID+",G.*, D."+DomainColumns.NAME
					+ " FROM (" + SQLConstants.RATTACH +" AS RA inner join "+SQLConstants.DOMAIN+" AS D ON RA."+RattachColumns.DOMAIN+" = D."+DomainColumns.ID
					+ ") inner join "+SQLConstants.DOMAIN_GSERVICEACCOUNT+" AS G ON (G."+DomainGserviceaccountColumns.DOMAIN+" = D."+DomainColumns.ID+" OR D."
					+ DomainColumns.PARENT+" = G."+DomainGserviceaccountColumns.DOMAIN+") "
					+ "WHERE D."+DomainColumns.NAME+" = ? OR D."+DomainColumns.PARENT+ " IN(SELECT "+DomainColumns.ID
																							+" FROM "+SQLConstants.DOMAIN
																							+" WHERE "+DomainColumns.NAME+" = ?)" ;
																
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, key);
			stmt.setString(2, key);
			rs = stmt.executeQuery();
			
			
			Vector<Rattach> rattachs = new Vector<Rattach>();
			DomainGserviceaccount dgserviceaccount= new DomainGserviceaccount();

			while (rs.next()){
				Rattach rattach= new Rattach();
				//todos los atributos de la tabla... error en el proyecto aon.sql.google
				rattach.setId(rs.getInt(RattachColumns.ID));
				rattach.setDomain(rs.getInt(RattachColumns.DOMAIN));
				rattach.setMimeType(rs.getShort(RattachColumns.MIMETYPE));
				rattach.setDescription(rs.getString(RattachColumns.DESCRIPTION));
				rattach.setType(rs.getShort(RattachColumns.TYPE));
				rattach.setDriveId(rs.getString(RattachColumns.DRIVE_ID));
				
				
		
				
				dgserviceaccount.setClientId(rs.getString(DomainGserviceaccountColumns.CLIENT_ID));
				dgserviceaccount.setClientSecret(rs.getAsciiStream(DomainGserviceaccountColumns.CLIENT_SECRET));
				dgserviceaccount.setDomain(rs.getInt(DomainGserviceaccountColumns.DOMAIN));
				dgserviceaccount.setEmailAddress(rs.getString(DomainGserviceaccountColumns.EMAIL_ADDRESS));
				dgserviceaccount.setPublicKey(rs.getString(DomainGserviceaccountColumns.PUBLIC_KEY));
				dgserviceaccount.setPrivateKey(rs.getAsciiStream(DomainGserviceaccountColumns.PRIVATE_KEY));
				
				rattachs.add(rattach);
				
				
				
			}
			
			DriveData dd=new DriveData(dgserviceaccount, rattachs);
			return dd;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	
	
	public static void deleteBlob(int id,String key) throws SQLException{
		
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "UPDATE "+ SQLConstants.RATTACH+" SET "+RattachColumns.DATA+"=null "
					+"WHERE "+RattachColumns.ID+" = ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);


			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
	public static void deleteDriveID(int id,String key) throws SQLException{
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "UPDATE "+ SQLConstants.RATTACH+" SET "+RattachColumns.DRIVE_ID+"=null "
					+"WHERE "+RattachColumns.ID+" = ?";

			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);


			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	//--amoldar a rattach
	public static void addDriveIds(String driveId, String parentId) throws SQLException, AonConnectionException {
		
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "INSERT INTO " + SQLConstants.RATTACH
					+"("+RattachColumns.DRIVE_ID+","+RattachColumns.DPARENT_ID 
					+") VALUES(?,?)";

			connection = getConnection("demo.aonsolutions.net");
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, driveId);
			stmt.setString(2, parentId);


			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static Vector<String> getEmails(int id,String key) throws SQLException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT DISTINCT MA."+MailAccountColumns.EMAIL
					+" FROM "+SQLConstants.RATTACH+" AS R inner join "+SQLConstants.ENTERPRISE+" AS E ON ( E."
					+EnterpriseColumns.REGISTRY+"= R."+RattachColumns.REGISTRY+") inner join "+SQLConstants.MAIL_ACCOUNT
					+" AS MA ON ( MA."+MailAccountColumns.DOMAIN+"= E."+EnterpriseColumns.DOMAIN+") inner join "
					+SQLConstants.USER_SCOPE+" AS US ON (US."+UserScopeColumns.USER_ID+"=MA."+MailAccountColumns.USER_ID
					+") WHERE R."+RattachColumns.ID+"= ? AND (R."+RattachColumns.SCOPE+" IS NULL OR R."+RattachColumns.SCOPE
					+"= US."+UserScopeColumns.SCOPE+")";
																
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);

			rs = stmt.executeQuery();
			
			
			Vector<String> emails = new Vector<String>();

			while (rs.next()){
				String email=rs.getString(MailAccountColumns.EMAIL);
	
				emails.add(email);	
			}
			
			return emails;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	
public static Vector<String> getPersonEmails(int id, String key) throws SQLException{
		
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			String sql = "SELECT DISTINCT RM."+RmediaColumns.VALUE
					+" FROM "+SQLConstants.RATTACH+" AS R inner join "+SQLConstants.PERSON+" AS P ON ( P."
					+PersonColumns.REGISTRY+"= R."+RattachColumns.REGISTRY+") inner join "+SQLConstants.RMEDIA
					+" AS RM ON ( RM."+RmediaColumns.REGISTRY+"= P."+PersonColumns.REGISTRY
					+") WHERE R."+RattachColumns.ID+"= ? AND RM."+RmediaColumns.MEDIA+"= ? ";
																
			
			connection = getConnection(key);
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2,4);
			rs = stmt.executeQuery();
			
			
			Vector<String> emails = new Vector<String>();

			while (rs.next()){
				String email=rs.getString(RmediaColumns.VALUE);
	
				emails.add(email);	
			}
			
			return emails;
			
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
	}
	

	
	public static void addDriveId(String driveId,int id, String domain) throws SQLException, AonConnectionException {
		
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			
			String sql = "UPDATE " + SQLConstants.RATTACH
					+" SET "+RattachColumns.DRIVE_ID + "= ? "
					+ "WHERE "+RattachColumns.ID+" = ?";
			
			
			connection = getConnection(domain);
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, driveId);
			stmt.setLong(2, id);


			stmt.executeUpdate();

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		
	}

	
}
