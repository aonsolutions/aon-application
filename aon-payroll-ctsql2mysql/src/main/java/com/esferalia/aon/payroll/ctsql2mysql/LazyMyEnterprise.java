package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calendar;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprctra;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;

public class LazyMyEnterprise extends MyEnterprise {

	public static interface EmprnifFilter {
		public boolean accept(Emprnif emprnif) throws SQLException;
	}
	
	
	static class CifEmprnifFilter implements EmprnifFilter {
		
		private List<String> cifs;
		
		
		public CifEmprnifFilter(List<String> cifs) {
			this.cifs = cifs;
		}
		
		public boolean accept(Emprnif emprnif) throws SQLException {
			String cif = emprnif.getNumdoc();
			return cif != null ? cifs.contains(cif): false ;
		};
	}
	
	private MyContract 		myContract;
	private EmprnifFilter 	emprnifFilter;

	public LazyMyEnterprise(DefaultMysqlDB mysqlDB, 
						IPersons persons,
						IConcepts concepts,
						ICalendars calendars,
						IAgreements agreements,
						File logosAndSignaturesDir,
						String passwdHash,
						String domainSuffix,
						Date fromDate,
						boolean checkFVsionado,
						EmprnifFilter emprnifFilter) {
		
		super(mysqlDB, 
				agreements, 
				calendars, 
				logosAndSignaturesDir,
				passwdHash,
				domainSuffix,
				false);
		this.emprnifFilter = emprnifFilter;
		
		this.myContract = 
				new MyContract(mysqlDB, 
						this, 
						persons, 
						concepts, 
						agreements,
						calendars,
						passwdHash,
						fromDate,
						checkFVsionado);
	}

	public LazyMyEnterprise(DefaultMysqlDB mysqlDB, 
			IPersons persons,
			IConcepts concepts,
			ICalendars calendars,
			IAgreements agreements,
			File logosAndSignaturesDir,
			String passwdHash,
			String domainSuffix,
			Date fromDate,
			boolean checkFVsionado,
			List<String> cifs) {
		this(mysqlDB, 
				persons, 
				concepts, 
				calendars, 
				agreements, 
				logosAndSignaturesDir, 
				passwdHash,
				domainSuffix,
				fromDate , 
				checkFVsionado,
				new CifEmprnifFilter(cifs));
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		super.visit(ctsqlDB);
		
		myContract.init(ctsqlDB);
		Set<Integer> cdgs =  
				getEmprnifCdgs();
		for (Integer cdg : cdgs) {
			ctsqlDB.visitEmprnif(cdg, new DefaultCtsqlDBVisitor (){
				@Override
				public void visitEmprnif(Emprnif emprnif) throws SQLException {
					emprnif.visitRel_epp_emp(this);
				}
				@Override
				public void visitRel_epp_emp(Emprper emprper, Emprnif emprnif)
						throws SQLException {
					myContract.visitEmprper(emprper);
				}
			});
		}
	}


	@Override
	public void visitRel_emp_cli(Emprnif emprnif, Cliente cliente)
			throws SQLException {
		if (!emprnifFilter.accept(emprnif)) {
			return;
		}
		super.visitRel_emp_cli(emprnif, cliente);
	}

	@Override
	public void visitEmprdom_domicilio(Emprdom emprdom, Domicilio domicilio)
			throws SQLException {

		if (getEnterprise(emprdom.getCodemp()) == null) {
			return;
		} // Counterpart emprnif didn't pass the filter or something was wrong with it.
		
		super.visitEmprdom_domicilio(emprdom, domicilio);

	}

	
}
