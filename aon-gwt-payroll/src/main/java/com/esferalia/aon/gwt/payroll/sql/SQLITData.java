package com.esferalia.aon.gwt.payroll.sql;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.dbutils.DatabaseUtil;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractLeaveColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;

public class SQLITData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3128764870965847834L;
	
	private static final String SELECT = "SELECT ";
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	
	private static ITData itData;
	
	public static ITData getEnterpriseITData(Connection conn, int enterpriseId) {
		return null;
	}
	
	public static ITData getWorplaceItTData(Connection conn, int workplaceId) {
		
		itData = new ITData();		
		Date contractMin = null;
		Date contractMax = new Date(01,01,1900);
		ResultSet rs = null;
		PreparedStatement stmt = null;			

		try {
			
			String select = SELECT
					+ SQLConstants.REGISTRY+"."+RegistryColumns.DOCUMENT_COUNTRY+", "
					+ SQLConstants.REGISTRY+"."+RegistryColumns.DOCUMENT+", "					
					+ SQLConstants.CONTRACT+"."+ContractColumns.ID+", "
					+ SQLConstants.PERSON+"."+PersonColumns.REGISTRY+", "
					+ SQLConstants.PERSON+"."+PersonColumns.NAME+", "
					+ SQLConstants.PERSON+"."+PersonColumns.FIRST_SURNAME+", "
					+ SQLConstants.PERSON+"."+PersonColumns.SECOND_SURNAME+","
					+ SQLConstants.PERSON+"."+PersonColumns.SOCIAL_SECURITY_NUM+", "
					+ SQLConstants.CONTRACT+"."+ContractColumns.START_DATE+", "
					+ SQLConstants.CONTRACT+"."+ContractColumns.END_DATE+", "
					
					+" ifnull("+SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.TYPE+",-1) as type, "
					+ SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.ID+", "
					+ SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.START_DATE+", "
					+ SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.END_DATE+", "
					+" ifnull("+SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.DISCHARGE_CAUSE+",-1) as discharge_cause"
					
					+ FROM
					+ SQLConstants.CONTRACT
					+ " left join " + SQLConstants.CONTRACT_LEAVE + " on "
					+ SQLConstants.CONTRACT+"."+ContractColumns.ID +"="+ SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.CONTRACT
					+ " inner join " + SQLConstants.PERSON + " on "
					+ SQLConstants.CONTRACT+"."+ContractColumns.PERSON +"="+ SQLConstants.PERSON+"."+PersonColumns.REGISTRY
					+ " inner join " + SQLConstants.REGISTRY + " on " 
					+ SQLConstants.PERSON+"."+ SQLConstants.REGISTRY +"=" + SQLConstants.REGISTRY+"." + RegistryColumns.ID
					
					+ WHERE
					+ SQLConstants.CONTRACT+"."+ContractColumns.WORKPLACE+" = ? "
					+ " order by " + SQLConstants.PERSON+"."+PersonColumns.FIRST_SURNAME + " asc, "
					+ SQLConstants.PERSON+"."+PersonColumns.SECOND_SURNAME+" asc, "
					+ SQLConstants.PERSON+"."+PersonColumns.NAME+" asc, "
					+ SQLConstants.CONTRACT+"."+ContractColumns.START_DATE+" asc, "
					+ SQLConstants.CONTRACT_LEAVE+"."+ContractLeaveColumns.START_DATE+" asc";
				

			stmt = conn.prepareStatement(select);
			stmt.setInt(1, workplaceId);		

			rs = stmt.executeQuery();
			
			while (rs.next()) {			
			
				int registry = rs.getInt(PersonColumns.REGISTRY);
				String document_country = rs.getString(RegistryColumns.DOCUMENT_COUNTRY);
				String document = rs.getString(RegistryColumns.DOCUMENT);
				int contractId = rs.getInt(ContractColumns.ID);
				String name = rs.getString(PersonColumns.NAME);
				String fSurname = rs.getString(PersonColumns.FIRST_SURNAME);
				String sSurname = rs.getString(PersonColumns.SECOND_SURNAME);
				String social_security = rs.getString(PersonColumns.SOCIAL_SECURITY_NUM);				
				
				if(sSurname == null) {
					sSurname = "";
				}	
				
				Date startContract = rs.getDate(ContractColumns.START_DATE);
				
				contractMin = DateUtils.before(contractMin, startContract);						
			
				Date endContract = rs.getDate(ContractColumns.END_DATE);
				contractMax = DateUtils.after(contractMax, endContract);		
				Integer type = Integer.parseInt(rs.getString(SQLConstants.ContractLeaveColumns.TYPE));
				Date startContractLeave = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.START_DATE);
				Date endContractLeave = rs.getDate(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.END_DATE);
				int discharge_cause = Integer.parseInt(rs.getString(ContractLeaveColumns.DISCHARGE_CAUSE));
				int contractLeave_id = rs.getInt(SQLConstants.CONTRACT_LEAVE + "." + ContractLeaveColumns.ID);
				
				if(!itData.getEmployees().containsKey(contractId)) {					
					
					Employee employee = new Employee();
					employee.setId(contractId);
					employee.setDocument(document_country+"-"+document);
					employee.setPerson(registry);
					employee.setName(name);
					employee.setFirstSurname(fSurname);
					employee.setSecondSurName(sSurname);
					employee.setSocialSecurity(social_security);
					employee.setStartDate(startContract);
					employee.setEndDate(endContract);
					itData.setEmployee(contractId, employee);
				}
				
				if(type != -1) {
					ITDataPerson dataPerson = new ITDataPerson();
					dataPerson.setContractId(contractId);
					dataPerson.setContractLeaveId(contractLeave_id);
					dataPerson.setLeaveStartDate(startContractLeave);
					dataPerson.setLeaveEndDate(endContractLeave);
					dataPerson.setDischarge_cause(discharge_cause);
					dataPerson.setType(getEnumConstant(ITDataPerson.Type.class, type));
					
					itData.setITData(contractId, contractLeave_id, dataPerson);			
								
				}				
		}
		
			itData.yearsExistContracts(contractMin, contractMax);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(conn);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
		}
		return itData;		
	}
	
	// ------------------------------------------------------------------------
	// TODO: To EnumUtils ???
	public static <T extends Enum<?>> T getEnumConstant(Class<T> enumClass, Integer ordinal) {
		if ( ordinal == null )
			return null;
		if ( ordinal < 0 )
			return null;
		
		T constants [] = enumClass.getEnumConstants();
		if ( ordinal >= constants.length )
			return null;
		
		return constants[ordinal];
	}
	
 

}
