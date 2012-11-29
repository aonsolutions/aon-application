package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.management.RuntimeErrorException;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Calculo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Comunica;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Lincomun;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Irpf_data;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Irpf_data_ascendants;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Irpf_data_descendients;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Irpf_regularization;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractMysqlDB.Irpf_result;
import com.esferalia.aon.payroll.enumeration.ContractType;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.payroll.enumeration.IrpfContractType;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.payroll.enumeration.IrpfDeductHomeLoan;
import com.esferalia.aon.salary.expression.Period;


import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.enum2short;
import static com.esferalia.aon.payroll.ctsql2mysql.DefaultMysqlDB.toDouble;;

public class MyIrpfData extends DefaultCtsqlDBVisitor {
	
	
	@SuppressWarnings("serial")
	private static Map<String, FamilySituation> FAMILY_SITUATION_MAP = 
		new HashMap<String, FamilySituation>() {
		{
			put("1", FamilySituation.NO_MARRIED_WITH_SONS);
			put("2", FamilySituation.MARRIED);
			put("3", FamilySituation.OTHER);
		}
	};
	
	@SuppressWarnings("serial")
	private static Map<String, DisabilityLevel> DISABILITY_LEVEL_MAP = 
		new HashMap<String, DisabilityLevel>() {
		{
			put("1", DisabilityLevel.GT_EQ_33_LT_65);
			put("2", DisabilityLevel.GT_EQ_33_LT_65);
			put("3", DisabilityLevel.GT_EQ_65);
		}
	};
	
	

	private class TrabajoIrpf  implements Comparable<TrabajoIrpf>{
		private Date 				fecIni;
		private Date 				fecFin;
		private IrpfContractType 	contractType;
		private boolean 			ceutaMelilla;
		
		@Override
		public int compareTo(TrabajoIrpf o) {
			Period p1 = 
				new Period(fecIni, fecFin);
			Period p2 = 
				new Period(o.fecIni, o.fecFin);
			return p1.compareTo(p2);
		}
	}
	
	private class Irpf_all{
		Irpf_data 						data;
		List<Irpf_data_ascendants> 		ascendants = 
			new LinkedList<Irpf_data_ascendants>();
		List<Irpf_data_descendients>	descendients =
			new LinkedList<Irpf_data_descendients>();
	}

	private DefaultMysqlDB 	mysqlDB;
	private IContracts		contracts;
	
	private Integer 		contractId;
	private Integer 		irpfDataId;
	
	private double 						irpfAsk;
	
	private Irpf_all 					irpfAll;
	private List<Irpf_all> 				irpfAlls;
	private List<Irpf_result>  			irpfResults;
	private List<Irpf_regularization> 	irpfRegularizations;
	private List<TrabajoIrpf> 			trabajoIrpfs;
	
	public MyIrpfData(DefaultMysqlDB mysqlDB, IContracts contracts) {
		this.mysqlDB = mysqlDB;
		this.contracts = contracts;
		this.irpfAlls = new ArrayList<Irpf_all>();
		this.irpfResults = new ArrayList<Irpf_result>();
		this.irpfRegularizations = new ArrayList<Irpf_regularization>();
		this.trabajoIrpfs = new ArrayList<MyIrpfData.TrabajoIrpf>();
	}
	
	@Override
	public void visitEmprper(Emprper emprper) throws SQLException {

		contractId = 
			contracts.getContractId(emprper.getCdg());
		if (contractId == null) {
			MysqlDB.error("comunica[]: Contract not found for {}.",emprper.getCdg());
			return;
		}
		
		
		irpfAlls.clear();
		trabajoIrpfs.clear();
		emprper.visitTrabajo_emprper(this);
		emprper.visitComunica_emprper(this);
		revisitComunica_emprper(emprper);

		this.irpfResults.clear();
		this.irpfRegularizations.clear();
		emprper.visitCalculo_emprper(this);
		revisitCalculo_emprper();
		
	}
	
	@Override
	public void visitComunica_emprper(Comunica comunica, Emprper emprper)
			throws SQLException {
		
		Date startDate = comunica.getFecefe();
		Date issueDate = comunica.getFeccom();
		
		
		String sitFam = comunica.getSitfam();
		FamilySituation familySituation = 
			FAMILY_SITUATION_MAP.get(sitFam);
		if ( familySituation == null ) {
			familySituation = FamilySituation.OTHER;
		}
		
		
		String spouseDocument = 
			comunica.getNifcony();
		
		Date movingDate = 
			comunica.getMovilidad();
		
		String xMinus = 
			comunica.getXminus();
		
		Boolean dependence3 =
			"2".equals(xMinus);
		
		DisabilityLevel disabilityLevel =
			DISABILITY_LEVEL_MAP.get(xMinus);
		
		Boolean labourProlongation = 
			"S".equals(comunica.getProlongacion()) ? true : false;
		
		Double annualRemuneration = 
			toDouble( comunica.getImp_retr_est() );
		Double irregularReduction182 = 
			toDouble( comunica.getImp_irreg());
		Double deducciblesExpenses = 
			toDouble( comunica.getImp_ss());
		Double spousalSupport = 
			toDouble( comunica.getImp_pension());
		Double foodAnnuity = 
			toDouble( comunica.getImp_anual());
		Double requestIrpf = 
			toDouble(comunica.getSolicita());
		
		IrpfDeductHomeLoan deductHomeLoan = 
			"S".equals(comunica.getHipoteca()) ? IrpfDeductHomeLoan.TRANSIENT_REGIME : null;	
		
		Irpf_data irpfData = new Irpf_data();
		irpfData.contract = contractId;
		irpfData.family_situation = enum2short(familySituation);
		irpfData.spouse_document = spouseDocument;
		irpfData.disability_level= enum2short( disabilityLevel);
		irpfData.dependence = dependence3;
		irpfData.moving_date= movingDate;
		irpfData.labour_prolongation= labourProlongation;
		irpfData.descendient_count= null;
		irpfData.start_date= startDate;
		irpfData.end_date= null; // ???
		irpfData.fiscal_exclusion = false;
		irpfData.issue_date = issueDate;
		irpfData.annual_remuneration = annualRemuneration;
		irpfData.irregular_18_2_reduction = irregularReduction182;
		irpfData.irregular_18_3_reduction = null;
		irpfData.deduccibles_expenses = deducciblesExpenses;
		irpfData.spousal_support = spousalSupport;
		irpfData.food_annuity= foodAnnuity;
		irpfData.deduct_home_loan = enum2short(deductHomeLoan);
		irpfData.request_irpf = requestIrpf > 0.00 ? requestIrpf : null; 
		
		irpfAll =  new Irpf_all();
		irpfAll.data = irpfData;
		
		comunica.visitLincomun_comunica(this);
		
		irpfAlls.add(irpfAll);
		irpfAsk = toDouble(comunica.getSolicita());
	}
	
	@Override
	public void visitTrabajo_emprper(Trabajo trabajo, Emprper emprper)
			throws SQLException {
		
		if ( contracts.outOfDate(trabajo.getFecfin()) ) 
			return;
		
		
		
		TrabajoIrpf trabajoIrpf = 
			new TrabajoIrpf();
		
		trabajoIrpf.fecIni = trabajo.getFecini();
		trabajoIrpf.fecFin = getEndDate ( trabajo.getFecfin(), emprper);
		
		if ( trabajoIrpf.fecFin != null 
				&& trabajoIrpf.fecFin.before(trabajoIrpf.fecIni) ){
			MysqlDB.error("trabajo [{}]: start : {} must be <= than end : {}", 
					trabajo.getCdg(), trabajoIrpf.fecIni, trabajoIrpf.fecFin );
			return;
		}
		
		trabajoIrpf.ceutaMelilla = 
			"S".equalsIgnoreCase(trabajo.getIndceutamelilla());
		
		String indIrpf = trabajo.getIndirpf() != null ? 
				trabajo.getIndirpf() : "N";
		
		trabajoIrpf.contractType = IrpfContractType.GENERAL;
		
		if ( "G".equalsIgnoreCase(indIrpf)) {
			if ( lessThanOneYear(emprper )) {
				trabajoIrpf.contractType = IrpfContractType.LESS_THAN_ONE_YEAR;
			} else {
				trabajoIrpf.contractType = IrpfContractType.SPORADIC;
			}
		} else if ("DE".contains(indIrpf)) {
			trabajoIrpf.contractType = IrpfContractType.SPECIAL;
		}

		trabajoIrpfs.add(trabajoIrpf);
	}
	
	private void sortAndMerge(List<TrabajoIrpf> trabajoIrpfs, Emprper emprper ) 
	throws SQLException {
		
		if ( trabajoIrpfs.size() == 0 ) {
			MysqlDB.info("emprper [{}] : Without irpf data ", emprper.getCdg() );
			return;
		}
		
		Collections.sort(trabajoIrpfs);
		
		List<TrabajoIrpf> mergedTrabajoIrpfs = 
			new ArrayList<MyIrpfData.TrabajoIrpf>(trabajoIrpfs.size());
		
		TrabajoIrpf lastTrabajoIrpf = trabajoIrpfs.get(0);
		for (int i = 1; i < trabajoIrpfs.size(); i++) {
			TrabajoIrpf trabajoIrpf = trabajoIrpfs.get(i);
			if ( lastTrabajoIrpf.ceutaMelilla != trabajoIrpf.ceutaMelilla || 
					lastTrabajoIrpf.contractType!= trabajoIrpf.contractType ){
				mergedTrabajoIrpfs.add(lastTrabajoIrpf);
				lastTrabajoIrpf = trabajoIrpf;
			}
			else {
				lastTrabajoIrpf.fecFin = trabajoIrpf.fecFin;
			}
		}
		mergedTrabajoIrpfs.add(lastTrabajoIrpf);
		
		trabajoIrpfs.clear();
		
		trabajoIrpfs.addAll(mergedTrabajoIrpfs);
	}
	
	
	public void revisitComunica_emprper(Emprper emprper ) throws SQLException {
		Collections.sort(irpfAlls, 
				new Comparator<Irpf_all>() {
					@Override
					public int compare(Irpf_all o1, Irpf_all o2) {
						return o1.data.start_date.compareTo(o2.data.start_date);
					}
				});

		
		sortAndMerge(trabajoIrpfs, emprper);
		
		Date priorStartDate = null;
		for ( int i = irpfAlls.size() -1; i >= 0 ; i-- ){
			Irpf_all  irpfAll = irpfAlls.get(i);
			revisitComunica_emprper(irpfAll, priorStartDate, emprper);
			priorStartDate = irpfAll.data.start_date;
		}

	}
	
	public void revisitComunica_emprper(Irpf_all irpfAll, Date priorStartDate, Emprper emprper)
		throws SQLException {
		
		
		Irpf_data irpfData = irpfAll.data;
		
		Date endDate = emprper.getFecbaj() ;
		
		if ( priorStartDate != null ){
			Calendar calendar = 
				Calendar.getInstance();
			calendar.setTime(priorStartDate);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			endDate = new Date ( calendar.getTimeInMillis());
			
		}

		Date startDate = irpfData.start_date; 

		if ( endDate != null && startDate.compareTo(endDate) > 0 ) {
			MysqlDB.error("irpf_data [{}]: start : {} must be <= than end : {}", 
					irpfData.contract, startDate, endDate );
			return;
		}
		
		
		if ( contracts.outOfDate(endDate) ) 
			return;
		
		
		for (TrabajoIrpf trabajoIrpf : trabajoIrpfs) {

			if ( trabajoIrpf.fecFin != null && 
					trabajoIrpf.fecFin.before(startDate))
			{
				continue;
			}
			
			if ( endDate != null && 
					trabajoIrpf.fecIni.after(endDate))
			{
				continue;
			}
			
				
			java.util.Date start = Period.max(startDate, trabajoIrpf.fecIni);
			java.util.Date end = Period.min(endDate, trabajoIrpf.fecFin);
			
			Date sqlStart = new Date(start.getTime());
			Date sqlEnd = end != null ? new Date(end.getTime()) : null;
			
			
			irpfDataId = 
				mysqlDB.insertIrpf_data(irpfData.contract, 
						irpfData.family_situation, 
						irpfData.spouse_document, 
						irpfData.disability_level, 
						irpfData.dependence, 
						irpfData.moving_date, 
						irpfData.labour_prolongation, 
						null, //TODO: descendient_count, 
						sqlStart, 
						sqlEnd, 
						false, //TODO: fiscal_exclusion
						irpfData.issue_date, 
						irpfData.annual_remuneration, 
						irpfData.irregular_18_2_reduction, 
						null, //irregular_reduction_18_3, 
						irpfData.deduccibles_expenses, 
						irpfData.spousal_support, 
						irpfData.food_annuity,
						irpfData.deduct_home_loan,
						irpfData.request_irpf,
						enum2short(trabajoIrpf.contractType),
						trabajoIrpf.ceutaMelilla);
			
			
			
			for (Irpf_data_ascendants ascendant : irpfAll.ascendants) {
				mysqlDB.insertIrpf_data_ascendants(
						irpfDataId, 
						ascendant.birth_year, 
						ascendant.disability_level, 
						ascendant.dependence, 
						ascendant.another_descendient);
			}
			for (Irpf_data_descendients descendient : irpfAll.descendients) {
				mysqlDB.insertIrpf_data_descendients(
						irpfDataId, 
						descendient.birth_year, 
						descendient.adoption_year, 
						descendient.disability_level, 
						descendient.dependence, 
						descendient.unique_parent);
			}
			
			Period periodReal = new Period(start, end );
			Period periodComunica = new Period(startDate, endDate );

			if ( periodComunica.compareTo(periodReal) != 0 ) {
				MysqlDB.info("irpf_data [{}] : Split irpf_data due trabajo {}..{} {}..{}", 
						irpfData.contract, 
						startDate, endDate,
						trabajoIrpf.fecIni, trabajoIrpf.fecFin);
			}
			
		}
		
		
		

	}
	
	
	@Override
	public void visitLincomun_comunica(Lincomun lincomun, Comunica comunica)
			throws SQLException {
		
		Integer birthYear = lincomun.getAnionac();
		Integer adoptionYear = lincomun.getAnioaco();

		
		String xMinus = 
			lincomun.getXminus();
		
		Boolean dependence3 =
			"2".equals(xMinus);
		
		DisabilityLevel disabilityLevel =
			DISABILITY_LEVEL_MAP.get(xMinus);
		
		
		if ( "2".equals(lincomun.getDes_asc() ) ){
			Integer conviv = lincomun.getConviv();
			Short anotherDescendient = conviv != null ? conviv.shortValue() : null ;
			
			Irpf_data_ascendants ascendants = 
				new Irpf_data_ascendants();
			ascendants.birth_year = birthYear;
			ascendants.disability_level  = enum2short(disabilityLevel);
			ascendants.dependence = dependence3;
			ascendants.another_descendient = anotherDescendient;
			
			irpfAll.ascendants.add(ascendants);
		} else {
			Boolean uniqueParent = 
				"S".equals(lincomun.getDescen_ent());

			Irpf_data_descendients descendients= 
				new Irpf_data_descendients();
			descendients.birth_year = birthYear;
			descendients.adoption_year = adoptionYear;
			descendients.disability_level  = enum2short(disabilityLevel);
			descendients.dependence = dependence3;
			descendients.unique_parent = uniqueParent;

			irpfAll.descendients.add(descendients);
		}
	}
	
	
	@Override
	public void visitCalculo_emprper(Calculo calculo, Emprper emprper)
			throws SQLException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, calculo.getAnio());
		calendar.set( Calendar.MONTH, calculo.getMes() -1 );
		calendar.set( Calendar.DAY_OF_MONTH, calculo.getDia());
		Date effectiveDate = new Date ( calendar.getTimeInMillis());
		
		if ( contracts.outOfDate(effectiveDate)) {
			return;
		}
		
		Irpf_regularization irpfRegularization = 
			new Irpf_regularization();
		
		String regula = calculo.getRegula() ;
		irpfRegularization.id = regula != null && "N".equals(regula) ? -1 : null;
		
		irpfRegularization.contract = contractId;
		irpfRegularization.effective_date = effectiveDate;
		
		irpfRegularization.paid_irpf = toDouble(calculo.getIrpf_acu());
		irpfRegularization.paid_remuneration = toDouble(calculo.getRetr_acu_fij(),calculo.getRetr_acu_var() );
		
		irpfRegularization.prior_irpf = toDouble(calculo.getIrpf_anterior());
		irpfRegularization.prior_in_ceuta_melilla = false;
		

		this.irpfRegularizations.add(irpfRegularization);
		
		Irpf_result irpfResult = new Irpf_result();
		irpfResult.contract = contractId;
		irpfResult.effective_date = effectiveDate;

		irpfResult.minimun_personal_family = toDouble(
				calculo.getImp_personal(),
				calculo.getImp_asistencia(),
				calculo.getImp_familiar(), 
				calculo.getImp_ascen(),
				calculo.getImp_discapacidad());
		irpfResult.base_irpf = toDouble( calculo.getBase_calculo() ) + irpfResult.minimun_personal_family;
		//irpfResult.deduct_80_bis = 
		irpfResult.deduct_home_loan_amount = toDouble( calculo.getImpredhipoteca());
		
		irpfResult.irpf = getIrpf(calculo, irpfAsk);
		
		
		irpfResult.annual_irpf = toDouble(calculo.getRetanualb() );
		
		irpfResult.annual_remuneration = toDouble(calculo.getRetr_estimada());
		irpfResult.irregular_18_2_reduction = toDouble(calculo.getImp_irreg());
		//irpfResult.irregular_18_3_reduction
		irpfResult.deduccibles_expenses = toDouble(calculo.getImp_css());
		irpfResult.work_remuneration_reduction = toDouble(calculo.getImp_rentas());
		irpfResult.work_prolongation_reduction = toDouble(calculo.getImp_prolongacion());
		irpfResult.work_moving_reduction = toDouble(calculo.getImp_movilidad());
		irpfResult.work_disability_reduction= toDouble(calculo.getImp_discapacidadt());
		
		irpfResult.two_or_more_descendents_min= toDouble(calculo.getImp_descen());
		irpfResult.social_security_pensioner= toDouble(calculo.getImp_pensionista());
		irpfResult.spousal_support= toDouble(calculo.getImp_pension());
		irpfResult.food_annuity = toDouble(calculo.getImp_anualid());

		
		irpfResult.minimun_personal = toDouble(calculo.getImp_personal(), 
						calculo.getImp_asistencia());
		irpfResult.minimun_descendents = toDouble(calculo.getImp_familiar());
		irpfResult.minimun_ascendents = toDouble(calculo.getImp_ascen());
		irpfResult.minimun_disability = toDouble(calculo.getImp_discapacidad());
		
		
		this.irpfResults.add(irpfResult);
	}
	
	public void revisitCalculo_emprper () throws SQLException{
		
		
		Collections.sort(this.irpfResults, new Comparator<Irpf_result>() {
			@Override
			public int compare(Irpf_result o1, Irpf_result o2) {
				return o1.effective_date.compareTo(o2.effective_date);
			}
		});

		Collections.sort(this.irpfRegularizations, new Comparator<Irpf_regularization>() {
			@Override
			public int compare(Irpf_regularization o1, Irpf_regularization o2) {
				return o1.effective_date.compareTo(o2.effective_date);
			}
		});

		Irpf_result priorIrpfResult = null;
		for (int i = 0; i < this.irpfRegularizations.size(); i++) {
			
			Irpf_result irpfResult = this.irpfResults.get(i);
			Irpf_regularization irpfRegularization = this.irpfRegularizations.get(i);
			
			insertIrpfResult ( irpfResult );
			insertIrpfRegularization(irpfRegularization, priorIrpfResult, irpfResult );
			
			priorIrpfResult = irpfResult;
		}
	}
	

	private void insertIrpfRegularization( 
			Irpf_regularization irpfRegularization, 
			Irpf_result priorIrpfResult, 
			Irpf_result irpfResult ) throws SQLException{
		
		if ( irpfRegularization.id != null && irpfRegularization.id == -1 ) {
			return;
		}

		if ( priorIrpfResult != null && priorIrpfResult.effective_date.getYear() != irpfRegularization.effective_date.getYear() ) {
			MysqlDB.error("irpfRegularization[{}]: Regularization between different years {}..{}", 
					irpfRegularization.contract , priorIrpfResult.effective_date, irpfRegularization.effective_date );
			priorIrpfResult = null;
		}
		
		
		
		IrpfRegularizationReason irpfRegularizationReason = 
			getIrpfRegularizationReason(irpfResult, priorIrpfResult);
		
		
		
		if ( irpfRegularizationReason == IrpfRegularizationReason.OTHER ) {
			
			mysqlDB.insertIrpf_regularization(
					irpfRegularization.contract, 
					enum2short(irpfRegularizationReason), 
					irpfRegularization.effective_date, 
					irpfRegularization.paid_irpf, 
					irpfRegularization.paid_remuneration, 
					null, // prior_annual_irpf, 
					null, // prior_annual_remuneration, 
					null, // prior_base_irpf, 
					null, // prior_irpf, 
					null, // prior_in_ceuta_melilla, 
					null, // prior_minimun_personal_family, 
					null, // prior_deduct_homel_loan , 
					null  // prior_deduct_home_loan_amount
					);
			return;
		}

		IrpfDeductHomeLoan deductHomeLoan =  
			priorIrpfResult.deduct_home_loan_amount != null && priorIrpfResult.deduct_home_loan_amount > 0.00 ?
					IrpfDeductHomeLoan.TRANSIENT_REGIME : 
						null;
		
		mysqlDB.insertIrpf_regularization(
				irpfRegularization.contract, 
				enum2short(irpfRegularizationReason), 
				irpfRegularization.effective_date, 
				irpfRegularization.paid_irpf, 
				irpfRegularization.paid_remuneration, 
				priorIrpfResult.annual_irpf, 
				priorIrpfResult.annual_remuneration, 
				priorIrpfResult.base_irpf, 
				irpfRegularization.prior_irpf, 
				irpfRegularization.prior_in_ceuta_melilla, 
				priorIrpfResult.minimun_personal_family, 
				deductHomeLoan != null ? enum2short(deductHomeLoan): null, 
				priorIrpfResult.deduct_home_loan_amount);
		
		
	}
	
	private IrpfRegularizationReason getIrpfRegularizationReason(Irpf_result irpfResult, Irpf_result priorIrpfResult) {
		if ( priorIrpfResult == null ) {
			return IrpfRegularizationReason.OTHER;
		}
		
		if ( value( priorIrpfResult.base_irpf ) !=  value ( irpfResult.base_irpf ) ) {
			return IrpfRegularizationReason.BASE_IRPF_CHANGE;
		} else if ( value( priorIrpfResult.minimun_personal_family ) !=  value(irpfResult.minimun_personal_family )  ){
			return IrpfRegularizationReason.MIN_PERSONAL_CHANGE;
		} else if ( value ( priorIrpfResult.spousal_support ) == 0.00 && 
					value ( irpfResult.spousal_support ) > 0.00 ) {
			return IrpfRegularizationReason.SPOUSAL_SUPPORT_IN;
		}else if ( value ( priorIrpfResult.food_annuity ) == 0.00  && 
				value ( irpfResult.food_annuity ) > 0.00  ) {
			return IrpfRegularizationReason.FOOD_ANNUITY_IN;
		}
		/*else if ( value ( priorIrpfResult.deduct_home_loan_amount ) == 0.00  && 
				value ( irpfResult.deduct_home_loan_amount ) > 0.00  ) {
			return IrpfRegularizationReason.DEDUCT_HOME_LOAN_IN;
		}else if ( value ( priorIrpfResult.deduct_home_loan_amount ) > 0.00  && 
				value ( irpfResult.deduct_home_loan_amount ) == 0.00  ) {
			return IrpfRegularizationReason.DEDUCT_HOME_LOAN_OUT;
		}*/
		
		return IrpfRegularizationReason.OTHER;
	}
	
	private double getIrpf(Calculo calculo, double irpfAsk) throws SQLException {
		
		double irpf  = toDouble( calculo.getIrpf());
		double irpfCal = toDouble( calculo.getIrpf_cal());
		
		
		if ( irpf == irpfCal ) {
			return irpf;
		}
		
		int anio = calculo.getAnio();
		String indIrpf = calculo.getIndirpf();
		
		if ( indIrpf != null && irpf > irpfCal ) {
			if ( "G".equals(indIrpf) && irpf == 2.00 ) {
				MysqlDB.info("calculo[{}]: % min less than one year {} ", calculo.getCdg(), irpf  );
				return irpf;
			}
			if ( "DE".contains(indIrpf) ) {
				double min = anio >= 2003 ? 15.00 : 18.00;
				if ( irpf == min ) {
					MysqlDB.info("calculo[{}]: % min specials {} {} ", calculo.getCdg(), anio, irpf  );
					return irpf;
				}
			}
		}
		
		String regula = calculo.getRegula();
		if ( regula != null && "S".equals(regula) && irpfCal > irpf ) {
			double max = 48.00 ;
			if ( anio >= 2003 ) 
				max = 45.00 ;
			if ( anio >= 2007 ) 
				max = 43.00 ;
			if ( anio >= 2011 ) 
				max = 45.00 ;
			
			if ( irpf == max ) {
				MysqlDB.info("calculo[{}]: % max regularization {} {} ", calculo.getCdg(), anio, irpf  );
				return irpf;
			}
		}
		
		// ¿ que ocurre , cual escogemos ? por defecto el irpf 'asignado'
		// ¿ Cuando escogemos es irpf_cal ? 
		
		// Cuando  el irpf 'asignado' es exactamente igual al anterior y mayor al irpf 'calculado'
		double irpfPrior = 
			toDouble(calculo.getIrpf_anterior()); 
		if ( irpf > irpfCal && irpf ==  irpfPrior ) {
			MysqlDB.info("calculo[{}]: % min prior {} < {}", calculo.getCdg(), irpfCal, irpf  );
			return irpfCal;
		} // % min anterior 
		
		// Cuando  el irpf 'asignado' es exactamente igual al solicitado y mayor al irpf 'calculado'
		if ( irpf > irpfCal && irpf ==  irpfAsk ) {
			MysqlDB.info("calculo[{}]: % min ask {} < {}", calculo.getCdg(), irpfCal, irpf  );
			return irpfCal;
		} // % min solicitado
		
		
		return irpf;
		
		
	}

	private void insertIrpfResult( Irpf_result irpfResult) throws SQLException {
		mysqlDB.insertIrpf_result(
				irpfResult.contract, 
				irpfResult.effective_date, 
				irpfResult.base_irpf, 
				irpfResult.minimun_personal_family, 
				irpfResult.deduct_home_loan_amount, 
				irpfResult.deduct_80_bis, 
				irpfResult.irpf, 
				irpfResult.annual_irpf, 
				irpfResult.annual_remuneration, 
				irpfResult.irregular_18_2_reduction, 
				irpfResult.irregular_18_3_reduction, 
				irpfResult.deduccibles_expenses, 
				irpfResult.work_remuneration_reduction, 
				irpfResult.work_prolongation_reduction, 
				irpfResult.work_moving_reduction, 
				irpfResult.work_disability_reduction, 
				irpfResult.social_security_pensioner, 
				irpfResult.two_or_more_descendents_min, 
				irpfResult.spousal_support, 
				irpfResult.food_annuity, 
				irpfResult.minimun_personal, 
				irpfResult.minimun_ascendents, 
				irpfResult.minimun_descendents, 
				irpfResult.minimun_disability, 
				irpfResult.descendents_minor_3_total, 
				irpfResult.descendents_minor_3_entirely, 
				irpfResult.descendents_remainder_total, 
				irpfResult.descendents_remainder_entirely, 
				irpfResult.descendents_33_65_total, 
				irpfResult.descendents_33_65_entirely, 
				irpfResult.descendents_moving_total, 
				irpfResult.descendents_moving_entirely, 
				irpfResult.descendents_65_total, 
				irpfResult.descendents_65_entirely, 
				irpfResult.descendents_first, 
				irpfResult.descendents_second, 
				irpfResult.descendents_third, 
				irpfResult.descendents_fourth_subsequent_total, 
				irpfResult.descendents_fourth_subsequent_entirely, 
				irpfResult.ascendents_minor_75_total, 
				irpfResult.ascendents_minor_75_entirely, 
				irpfResult.ascendents_mayor_75_total, 
				irpfResult.ascendents_mayor_75_entirely, 
				irpfResult.ascendents_33_65_total, 
				irpfResult.ascendents_33_65_entirely, 
				irpfResult.ascendents_moving_total, 
				irpfResult.ascendents_moving_entirely, 
				irpfResult.ascendents_65_total, 
				irpfResult.ascendents_65_entirely);
	}
	
	private static double value(Double d) {
		return d != null ? d : 0.00;
	}

	private int getAge(int birthYear) {
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		return currentYear - birthYear;
	}
	
	private static boolean lessThanOneYear (Emprper emprper) 
	throws SQLException {
		
		if ( emprper.getFecbaj() == null ) 
			return false;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(emprper.getFecalt());
		calendar.add(Calendar.YEAR, +1);
		
		return !(emprper.getFecbaj().after(calendar.getTime()));
		
		
	}
	
	private Date getEndDate(Date fecFin, Emprper emprper) throws SQLException{
		return DefaultMysqlDB.is9999(fecFin)? emprper.getFecbaj(): fecFin;
	}
	

}
