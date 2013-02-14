package com.esferalia.aon.payroll.ctsql2mysql;

import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_IT;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominait;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaitnu;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteit;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Parteitnu;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.expression.Period;

public class MyLeave extends DefaultCtsqlDBVisitor {

	private IContracts 			contracts;
	private MySalary 			salarys;
	private DefaultMysqlDB 		mysqlDB;
	
	private Integer				contractId;
	private Map<Date, Integer> 	parteIts;
	
	private Its					its;
	private Its					nuIts;
	
	public MyLeave(DefaultMysqlDB mysqlDB, IContracts contracts, MySalary salarys) 
	{
		this.mysqlDB = mysqlDB;
		this.contracts = contracts;
		this.salarys = salarys;
		this.its = new Its();
		this.nuIts = new Its();
		this.parteIts = new HashMap<Date, Integer>();
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitEmprper(this);
	}
	
	@Override
	public void visitEmprper(Emprper emprper) throws SQLException {
		contractId = 
				contracts.getContractId(emprper.getCdg());
		if ( contractId == null ) {
			return;
		}
		its.clear();
		nuIts.clear();
		
		emprper.visitNominait_emprper(this);
		emprper.visitRel_pit_epp(this);
		
		//emprper.visitNitnu_emprper(this);
		//emprper.visitPitnu_emprper(this);
		
	}
	
	@Override
	public void visitNominait_emprper(Nominait nominait, Emprper emprper)
			throws SQLException {
		its.add(nominait);
	}
	
	@Override
	public void visitRel_pit_epp(Parteit parteit, Emprper emprper)
			throws SQLException {
		
		
		java.sql.Date endDate = 
			DefaultMysqlDB.getEndDate(parteit.getFecfin());
		
		if ( contracts.outOfDate(endDate) ) {
			return;
		}

		Double dailyCgcBase = toDouble(parteit.getBasediacg());
		Double dailyCgpBase = toDouble(parteit.getBasediaacc());
		Double dailyRegBase = toDouble(parteit.getBaseregdia());
		
		
		LeaveType leaveType = 
				getLeaveType(parteit.getTipoit(), parteit.getRiesgo());
		Short type = 
			enum2short(leaveType);
		
		Date lastSalary = 
				salarys.getLastSalaryDate(); 
		
		Its.It  it = its.getIt(parteit.getFecini());
		if ( it == null   ) {
			if ( Period.compare(lastSalary, parteit.getFecfin()) > 0 ) {
				MysqlDB.error("parteit[{}]: {}..{} {}, hasn't 'nominait' counterpart (last nomina at {} )", 
						parteit.getCdg(), 
						parteit.getFecini(),
						parteit.getFecfin(),
						leaveType,
						lastSalary);
				return;
			}
			else {
				MysqlDB.error("parteit[{}]: {}..{} {}, hasn't 'nomina' counterpart (last nomina at {} )", 
						parteit.getCdg(), 
						parteit.getFecini(),
						parteit.getFecfin(),
						leaveType,
						lastSalary);
			}
		} else if ( it.type != leaveType ) {
			MysqlDB.error("parteit[{}]: {}..{} has different type {} than it's 'nominait' counterpart {}", 
					parteit.getCdg(), 
					parteit.getFecini(),
					parteit.getFecfin(),
					leaveType,
					it.type );
			return;
		} else if ( Period.compare(it.endDate, endDate) > 0 ) {
			MysqlDB.error("parteit[{}]: {}..{} {}  has different end date than it's 'nominait' counterpart {}", 
					parteit.getCdg(), 
					parteit.getFecini(),
					parteit.getFecfin(),
					leaveType,
					it.endDate );
		}
		
		Integer id = mysqlDB.insertContract_leave(
				type, 
				this.contractId, 
				null, 
				parteit.getFecini(), 
				endDate, 
				dailyCgcBase, 
				dailyCgpBase,
				parteIts.get(parteit.getFeciniori()),
				dailyRegBase,
				null);
		
		if ( parteit.getProret().equals("D") ) {
			mysqlDB.insertContract_data(
					QUOTE_IT.getName(), 
					this.contractId,
					"\"DIARIA\"", 
					parteit.getFecini(), 
					endDate);
		}
		
		parteIts.put(parteit.getFecini(), id);
	}
	
	
	private static LeaveType getLeaveType(String tipoIt, String riesgo ) {
		if ( "E".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.COMMON_DISEASE;
		}
		if ( "A".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.OCCUPATIONAL_DISEASE;
		}
		if ( "M".equalsIgnoreCase(tipoIt) ) {
			return LeaveType.MATERNITY;
		}
		if ( "S".equalsIgnoreCase(riesgo)) {
			return LeaveType.PREGNANCY_RISK;
		}
		return null;
	}
	
	private static class Its {
		
		
		private Map<Date, It> its = 
				new HashMap<Date, It>();
		
		public void clear(){
			its.clear();
		}
		
		public void add(Nominait nominait) 
		throws SQLException {
			Date fecIniIt = 
					nominait.getFeciniit();
			It it = its.get(fecIniIt);
			if ( it == null ) {
				it = new It(nominait);
				its.put ( fecIniIt , it );
			} else {
				Date endDate = 
						DefaultMysqlDB.getEndDate(nominait.getFecfin()); 
				it.endDate = Period.max(it.endDate, endDate );
			}
			
		}
		
		
		public void add(Nominaitnu nominaitnu) 
		throws SQLException {
			Date fecIniIt = 
					nominaitnu.getFeciniit();
			It it = its.get(fecIniIt);
			if ( it == null ) {
				it = new It(nominaitnu);
				its.put ( fecIniIt , it );
			} else {
				Date endDate = 
						DefaultMysqlDB.getEndDate(nominaitnu.getFecfin()); 
				it.endDate = Period.max(it.endDate, endDate );
			}
			
		}
		
		public It getIt( Date fecIniIt ) {
			return its.get(fecIniIt);
		}
		
		
		
		private static class It {
			
			private Date endDate;
			private LeaveType type;

			private It( Nominait nominait) 
			throws SQLException {
				endDate = DefaultMysqlDB.getEndDate(nominait.getFecfin()) ; 
				type = getLeaveType(nominait.getTipoit(), nominait.getRiesgo());
			}
			private It( Nominaitnu nominaitnu) 
			throws SQLException {
				endDate = DefaultMysqlDB.getEndDate(nominaitnu.getFecfin()) ; 
				type = getLeaveType(nominaitnu.getTipoit(), nominaitnu.getRiesgo());
			}
			
		}
		
	
	}
	
	
}
