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
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;

public class SQLITData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3128764870965847834L;
	
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

			String select = "SELECT person.registry,"
					+ " contract.id,"
					+ " person.name,"
					+ " person.first_surname,"
					+ " person.second_surname,"
					+ " contract.start_date,"
					+ " contract.end_date,"
					+ " ifnull(contract_leave.type, -1),"
					+ " contract_leave.start_date,"
					+ " contract_leave.end_date"
					+ " FROM contract"
					+ " left join contract_leave on "
					+ "contract.id = contract_leave.contract"
					+ " inner join person on "
					+ "contract.person = person.registry "
					+ " where contract.workplace = ?"
					+ " order by person.first_surname asc,"					
					+ " contract_leave.start_date asc";

			stmt = conn.prepareStatement(select);
			stmt.setInt(1, workplaceId);		

			rs = stmt.executeQuery();
			
			while (rs.next()) {			
			
				int registry = rs.getInt(PersonColumns.REGISTRY);
				int contractId = rs.getInt(ContractColumns.ID);
				String name = rs.getString(PersonColumns.NAME);
				String fSurname = rs.getString(PersonColumns.FIRST_SURNAME);
				String sSurname = rs.getString(PersonColumns.SECOND_SURNAME);
				
				if(sSurname == null) {
					sSurname = "";
				}	
				
				Date startContract = rs.getDate(ContractColumns.START_DATE);
				
				contractMin = DateUtils.before(contractMin, startContract);						
			
				Date endContract = rs.getDate(ContractColumns.END_DATE);
				contractMax = DateUtils.after(contractMax, endContract);		
				Integer type = rs.getInt(8);
				Date startContractLeave = rs.getDate(9);
				Date endContractLeave = rs.getDate(10);	
				
				if(!itData.getEmployees().containsKey(contractId)) {					
					
					Employee employee = new Employee();
					employee.setId(contractId);					
					employee.setPerson(registry);
					employee.setName(name);
					employee.setFirstSurname(fSurname);
					employee.setSecondSurName(sSurname);
					employee.setStartDate(startContract);
					employee.setEndDate(endContract);
					
					itData.setEmployee(contractId, employee);
				}
				
				if(type != -1) {
					ITDataPerson dataPerson = new ITDataPerson();
					dataPerson.setLeaveStartDate(startContractLeave);
					dataPerson.setLeaveEndDate(endContractLeave);
					dataPerson.setType(type);
					dataPerson.setContractId(contractId);
					
					itData.setITDataPerson(contractId, dataPerson);
								
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
	
 

}
