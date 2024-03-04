package com.esferalia.aon.in.payroll.tgss.idc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

class PECListener  implements IdcParserListener {
	
	@FunctionalInterface
	private static interface CostProvider {
		Cost  newCost(			
				String nss, 
				String ccc, 
				String pec, 
				String quota, 
				String porTipo,
				String description, 
				Date start, 
				Date end);
	}
	
	@FunctionalInterface
	private static interface DeductionProvider {
		Deduction  newDeduction(			
				String nss, 
				String ccc, 
				String pec, 
				String quota, 
				String porTipo,
				String description, 
				Date start, 
				Date end);
	}
	
	private static interface AddPEC {
	    public void addTo(PECListener listener);
	}
	
	private static class Cost extends PEC.Cost implements AddPEC {
	    @Override
	    public void addTo(PECListener listener) {
		listener.ssPECs.add(this);
	    }
	}
	
	private static class Bonus extends PEC.Bonus implements AddPEC {
	    @Override
	    public void addTo(PECListener listener) {
		listener.ssPECs.add(this);
	    }
	    
	}

	private static class Deduction extends PEC.Deduction implements AddPEC {
	    @Override
	    public void addTo(PECListener listener) {
		listener.ssPECs.add(this);
	    }
	}

	private static class RemoveCost extends Cost {
	    @Override
	    public void addTo(PECListener listener) {
		// remove cost like this
		List<PEC> toRemove =
		listener.ssPECs.stream().filter(this::isThis).toList();
		listener.ssPECs.removeAll(toRemove);
		
		boolean hasMe =
		listener.ssPECs.stream().anyMatch(this::hasMe);
		if ( hasMe ) {
		    return;
		}
		
		listener.ssPECs.add(this);
	    }

	    private boolean isThis(PEC pec) {
		return pec.visit( new Visitor<Boolean>() {
		    @Override
		    public Boolean visitCost(Cost cost) {
			return 
		        Objects.equals(cost.getSsNum(), getSsNum())
		        && Objects.equals(cost.getName(), getName())
		        && Objects.equals(cost.getEndDate(), getEndDate())
		        && Objects.equals(cost.getStartDate(), getStartDate());
		    }
		    @Override
		    public Boolean visitBonus(Bonus bonus) {
		        return false;
		    }
		    @Override
		    public Boolean visitDeduction(Deduction deduction) {
		        return false;
		    }
		});
	    }
	    
	    private boolean hasMe(PEC pec) {
		return pec.visit( new Visitor<Boolean>() {
		    @Override
		    public Boolean visitCost(Cost cost) {
		        return false;
		    }
		    @Override
		    public Boolean visitBonus(Bonus bonus) {
		        return 
		        Objects.equals(bonus.getSsNum(), getSsNum())
		        && Objects.equals(bonus.getEndDate(), getEndDate())
		        && Objects.equals(bonus.getStartDate(), getStartDate())
		        && bonus.getFormula() != null && bonus.getFormula().contains(getName()) ;
		    }
		    @Override
		    public Boolean visitDeduction(Deduction deduction) {
		        return false;
		    }
		});
	    }
	}

	private static class BenefitsLoss extends Bonus {
	}

	private static class ZeroDeduction extends Deduction {
	}

	private static class RemoveDeduction extends Deduction {
	    @Override
	    public void addTo(PECListener listener) {
		// remove deduction like this
		List<PEC> toRemove =
		listener.ssPECs.stream().filter(this::isThis).toList();
		listener.ssPECs.removeAll(toRemove);

		boolean hasMe =
		listener.ssPECs.stream().anyMatch(this::hasMe);
		if ( hasMe ) {
		    return;
		}

		listener.ssPECs.add(this);
	    }

	    private boolean isThis(PEC pec) {
		return pec.visit( new Visitor<Boolean>() {
		    @Override
		    public Boolean visitCost(Cost cost) {
		        return false;
		    }
		    @Override
		    public Boolean visitBonus(Bonus bonus) {
		        return false;
		    }
		    @Override
		    public Boolean visitDeduction(Deduction deduction) {
		        return 
		        Objects.equals(deduction.getSsNum(), getSsNum())
		        && Objects.equals(deduction.getName(), getName())
		        && Objects.equals(deduction.getEndDate(), getEndDate())
		        && Objects.equals(deduction.getStartDate(), getStartDate())
		        ;
		    }
		});
	    }

	    private boolean hasMe(PEC pec) {
		return pec.visit( new Visitor<Boolean>() {
		    @Override
		    public Boolean visitCost(Cost cost) {
		        return false;
		    }
		    @Override
		    public Boolean visitBonus(Bonus bonus) {
		        return false;
		    }
		    @Override
		    public Boolean visitDeduction(Deduction deduction) {
			return
		        Objects.equals(deduction.getSsNum(), getSsNum())
		        && Objects.equals(deduction.getEndDate(), getEndDate())
		        && Objects.equals(deduction.getStartDate(), getStartDate())
		        && deduction.getFormula() != null && deduction.getFormula().contains(getName()) ;
		    }
		});
	    }
	}

	private static class NegativeDeduction extends Deduction {
	}

	private static final NumberFormat NUMBER_FORMAT = DecimalFormat.getNumberInstance(new Locale("es", "ES"));

	static final int[] ssBonusCodes = new int[] { 1, 2, 13, 15, 16, 37, 41, 46, 48, 51, 52, 54, 55 };
		
	@SuppressWarnings("serial")
	static final Map<String, String> BONUS_QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("01", "CGC_E + IT_E + IMS_E + FP_E + DESMPL_E + FOGASA_E"); 	// Cuota empresarial por AT y EP, Cuotas de recaudación	conjunta
			put("03", "CGC_E"); 							// Cuota empresarial por Contingencias Comunes
			put("43", "CGC_E"); 							// Contingencias Comunes  
			put("51", "CUOTA_EMPRESARIAL"); 					// Cuota Empresarial - Horas extras			
			put("57", "CUOTA_EMPRESARIAL"); 					// Cuota Total
			put("68", "CGC_E + IT_E + IMS_E"); 					// Contingencias Comunes y Profesionales - Cuota Total
			//put("81", "");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> DEDUCTION_QUOTA_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("08", "-CGC -FP -DESMPL"); 	// 
			put("43", "-CGC"); 	// 
		}
	};

	static final Collection<DeductionProvider> REMOVE_ALL_DEDUCTIONS =  collection(
			newRemoveDeduction(ContextVariable.CGC_EMPLOYEE),
			newRemoveDeduction(ContextVariable.FP_EMPLOYEE),
			newRemoveDeduction(ContextVariable.UNEMPLOY_EMPLOYEE)
	);

	@SuppressWarnings("serial")
	static final Map<String, Collection<DeductionProvider>> DEDUCTION_QUOTA_PROVIDER_MAP = new HashMap<String, Collection<DeductionProvider>>() {
		{
			put("08", REMOVE_ALL_DEDUCTIONS);
			put("10", collection(
				newCgcITDeduction(ContextVariable.CGC_EMPLOYEE),
				//newRemoveDeduction(ContextVariable.CGC_EMPLOYEE),
				newRemoveDeduction(ContextVariable.MEI_EMPLOYEE),
				newRemoveDeduction(ContextVariable.FP_EMPLOYEE),
				newRemoveDeduction(ContextVariable.UNEMPLOY_EMPLOYEE)
				) );
			put("12", collection(
					newRemoveDeduction(ContextVariable.UNEMPLOY_EMPLOYEE)));
			put("53", collection(
					newRemoveDeduction(ContextVariable.FP_EMPLOYEE),
					newRemoveDeduction(ContextVariable.UNEMPLOY_EMPLOYEE)));
			put("68", collection(newNegativeDeduction(ContextVariable.CGC_EMPLOYEE)));
			put("78", collection(newRemoveDeduction(ContextVariable.FP_EMPLOYEE)));
		}
	};
	
	static final Collection<CostProvider> REMOVE_ALL_COSTS =  collection(
			newRemoveCost(ContextVariable.CGC_ENTERPRISE),
			newRemoveCost(ContextVariable.FP_ENTERPRISE),
			newRemoveCost(ContextVariable.UNEMPLOY_ENTERPRISE),
			newRemoveCost(ContextVariable.IT_ENTERPRISE),
			newRemoveCost(ContextVariable.IMS_ENTERPRISE),
			newRemoveCost(ContextVariable.FOGASA_ENTERPRISE));
	
	@SuppressWarnings("serial")
	static final Map<String, Collection<CostProvider>> COST_QUOTA_PROVIDERS_MAP = new HashMap<String, Collection<CostProvider>>() {
		{
			put("01", REMOVE_ALL_COSTS );
			put("10", collection(
				newCgcITCost(ContextVariable.CGC_ENTERPRISE),
				//newRemoveCost(ContextVariable.CGC_ENTERPRISE),
				newRemoveCost(ContextVariable.MEI_ENTERPRISE),
				newRemoveCost(ContextVariable.FP_ENTERPRISE),
				newRemoveCost(ContextVariable.FOGASA_ENTERPRISE),
				newRemoveCost(ContextVariable.UNEMPLOY_ENTERPRISE)
				) );
			put("12", collection(
					newRemoveCost(ContextVariable.UNEMPLOY_ENTERPRISE),
					newRemoveCost(ContextVariable.FOGASA_ENTERPRISE)));
			put("53", collection(
					newRemoveCost(ContextVariable.FP_ENTERPRISE),
					newRemoveCost(ContextVariable.FOGASA_ENTERPRISE),
					newRemoveCost(ContextVariable.UNEMPLOY_ENTERPRISE)));
			put("62", collection(
					newRemoveCost(ContextVariable.FP_ENTERPRISE),
					newRemoveCost(ContextVariable.FOGASA_ENTERPRISE)));
			put("78", collection(newRemoveCost(ContextVariable.FP_ENTERPRISE)));
		}
	};
	
	@SuppressWarnings("serial")
	static final Map<String, String> PEC_TYPE_T_49_MAP = new HashMap<String, String>() {
		{
			put("01", "BONIFICACIÓN INEM");
			put("03", "RED.CUOTA SS-PORCENT");
			put("13", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO-PORCENTAJE");
			put("16", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO. CUANTÍA");
			put("15", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL");
			put("37", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO COMPLETO");
			put("40", "TIPO COTIZACIÓN ESPECIAL.SEA");
			put("41", "BONIFICACIÓN PROGRAMA FOMENTO DE EMPLEO. CUANTÍA DIARIA");
			put("42", "RED.CUOTA SS-CUANTÍA");
		}
	};
		
	@SuppressWarnings("serial")
	static final Map<String, String> PEC_BONUS_MAP = new HashMap<String, String>() {
		{
			put("01", "BONIFICACIÓN INEM");
			put("03", "RED.CUOTA SS-PORCENT");
			put("13", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO-PORCENTAJE");
			put("16", "BONIFICACIÓN SPEE PROG FOMENTO DE EMPLEO. CUANTÍA");
			put("15", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL");
			put("37", "EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO COMPLETO");
			put("41", "BONIFICACIÓN PROGRAMA FOMENTO DE EMPLEO. CUANTÍA DIARIA");
			put("42", "RED.CUOTA SS-CUANTÍA");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_DEDUCTION_MAP = new HashMap<String, String>() {
		{
			put("01", "BONIFICACIÓN INEM");
			put("03", "RED.CUOTA SS-PORCENT");
			put("07", "EXONERACIÓN");
			put("09", "EXCLUSIONES");
			put("40", "TIPO COTIZACIÓN ESPECIAL.SEA");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_COST_MAP = new HashMap<String, String>() {
		{
			put("03", "RED.CUOTA SS-PORCENT");
			put("07", "EXONERACIÓN");
			put("09", "EXCLUSIONES");
			put("40", "TIPO COTIZACIÓN ESPECIAL.SEA");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("01", "( %s ) * %.2f / 100.00"); 															// BONIFICACIÓN INEM
			put("03", "( %s ) * %.2f / 100.00"); 															// BONIFICACIÓN INEM
			put("13", "( %s ) * %.2f / 100.00"); 															// BONIFICACIÓN INEM
			put("16",  String.format(Locale.ROOT,"TOTAL_BONF_SEPE_E=(isdef TOTAL_RED_CUOTA_SS ? TOTAL_BONF_SEPE_E : 0.00); BONF_SEPE_E=MIN(%%s, MIN(%%2$.2f, (%1$s == %2$s) ? %%2$.2f : MIN( %%2$.2f - TOTAL_BONF_SEPE_E, ROUND(%%2$.2f/30.00, 2)*%3$s) ));SELF.addVariable('TOTAL_BONF_SEPE_E', TOTAL_BONF_SEPE_E + BONF_SEPE_E ) ; BONF_SEPE_E", ContextVariable.SALARY_DAYS , ContextVariable.MONTH_DAYS, ContextVariable.QUOTE_DAYS)); 																	// 
			put("15", String.format(Locale.ROOT,"(%%s) * %%.2f / 100.00 * %1$s",ContextVariable.ERE_FACTOR_FORCE_OFF, ContextVariable.ERE_FACTOR_FORCE, ContextVariable.ERE_FACTOR )); 	// EXONERACIÓN E.R.E. FUERZA MAYOR. TIEMPO PARCIAL
			put("37", "( %s ) * %.2f / 100.00");
			put("41", "( %s ) * %.2f / 100.00");

			put("42",  String.format(Locale.ROOT,"TOTAL_RED_CUOTA_SS=(isdef TOTAL_RED_CUOTA_SS ? TOTAL_RED_CUOTA_SS : 0.00); RED_CUOTA_SS=MIN(%%s, MIN(%%2$.2f, (%1$s == %2$s) ? %%2$.2f : MIN( %%2$.2f - TOTAL_RED_CUOTA_SS, ROUND(%%2$.2f/30.00, 2)*%3$s) ));SELF.addVariable('TOTAL_RED_CUOTA_SS', TOTAL_RED_CUOTA_SS + RED_CUOTA_SS ) ; RED_CUOTA_SS", ContextVariable.SALARY_DAYS , ContextVariable.MONTH_DAYS, ContextVariable.QUOTE_DAYS)); 																	// 
			//put("42",  "MIN(%s, %.2f)"); 																	// 

			put("51",  String.format(Locale.ROOT,"%%2$.2f * %s * %s / %s", ContextVariable.PARTIAL_FACTOR, ContextVariable.QUOTE_DAYS, ContextVariable.MONTH_DAYS )); 															// 

		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_DEDUCTION_EXPRESSION_MAP = new HashMap<String, String>() {
		{
			put("16",  String.format(Locale.ROOT,"TOTAL_BONF_SEPE=(isdef TOTAL_RED_CUOTA_SS ? TOTAL_BONF_SEPE : 0.00); BONF_SEPE=MIN(-(%%s), MIN(%%2$.2f, (%1$s == %2$s) ? %%2$.2f : MIN( %%2$.2f - TOTAL_BONF_SEPE, ROUND(%%2$.2f/30.00, 2)*%3$s) ));SELF.addVariable('TOTAL_BONF_SEPE', TOTAL_BONF_SEPE + BONF_SEPE ) ; -BONF_SEPE", ContextVariable.SALARY_DAYS , ContextVariable.MONTH_DAYS, ContextVariable.QUOTE_DAYS)); 																	// 
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, String> PEC_DESCRIPTION_MAP = new HashMap<String, String>() {
		{
			put("16", "%s (%.2f)"); 															// 
			put("42", "%s (%.2f)"); 															// 
		}
	};
	
	private Date contractEnd;

	private Collection<PEC> ssPECs = new LinkedList<PEC>();
	
	public Collection<PEC> getSSBonuses() {
		return Collections.unmodifiableCollection(ssPECs);
	}

	// ------------------------------------------------------------ IdcParserListener
	
	@Override
	public void onContractEnd(Date end) {
		this.contractEnd = end;
	}
	
	@Override
	public void onEmployeeQuoteTRL(String nss, String ccc, String description, Date start, Date end) {
	    if ( description.matches("\\s*PROGRAMAS\\s*DE\\s*FORMACION\\s*")) {
        	    newRemovePEC(new Cost(), nss, ccc, "986", description, start, end, ContextVariable.MEI_ENTERPRISE).addTo(this);
        	    newRemovePEC(new Deduction(), nss, ccc, "986",description, start, end,  ContextVariable.MEI_EMPLOYEE).addTo(this);
	    }
	}
	@Override
	public void onEmployeeQuotePEC(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) {
		if ( hasBenefitsLoss( nss, ccc, code, quota))
		    return;
		
		Date pecEnd = Objects.equals(end, contractEnd) ? null : end;
		
		if ( PEC_BONUS_MAP.containsKey(code )) {
			try {
				if ( BONUS_QUOTA_EXPRESSION_MAP.containsKey(quota)) {
					newBonus(nss, ccc, code, description, portTipo, quota, start, pecEnd).addTo(this);
				}
				if ( DEDUCTION_QUOTA_EXPRESSION_MAP.containsKey(quota)) {
					newDeduction(nss, ccc, code, description, portTipo, quota, start, pecEnd).addTo(this); ;
				}
			} catch (ParseException e) {
			}
		}
		if ( PEC_COST_MAP.containsKey(code )) {
			if ( COST_QUOTA_PROVIDERS_MAP.containsKey(quota)) {
				COST_QUOTA_PROVIDERS_MAP.get(quota).forEach( f -> f.newCost(nss, ccc, code, quota, portTipo, description, start, pecEnd).addTo(PECListener.this)) ;
			}
		}
		if ( PEC_DEDUCTION_MAP.containsKey(code )) {
			if ( DEDUCTION_QUOTA_PROVIDER_MAP.containsKey(quota)) {
				DEDUCTION_QUOTA_PROVIDER_MAP.get(quota).forEach(f -> f.newDeduction(nss, ccc, code, quota, portTipo, description, start, pecEnd).addTo(PECListener.this)) ;			}
		}
	}
	
	
	@Override
	public void onEmployeeBenefitsLoss(String nss, String ccc, String cause, Date start, Date end) {
	    BenefitsLoss benefitsLoss = new BenefitsLoss();
	    benefitsLoss.setCcc(ccc);
	    benefitsLoss.setNss(nss);
	    benefitsLoss.setStartDate(start);
	    benefitsLoss.setEndDate(end);
	    benefitsLoss.setDescription(cause);
	    benefitsLoss.setFormula(String.format(""
	    	+ "/*epoch:%d*/AVISO(\"<div>PERDIDA DE BENEFICIOS: <span style='color:red;'>%s</span></div>"
	    	+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\")", Calendar.getInstance().getTimeInMillis(), cause));

	    benefitsLoss.addTo(this);
	    
	}
	
	// ------------------------------------------------------------------------

	private boolean hasBenefitsLoss(String nss, String ccc, String code, String quota) {
	    return ssPECs.stream()
	    .anyMatch(pec -> pec instanceof BenefitsLoss 
		    	&& AonStringUtils.equalsIgnoreCase(ccc, pec.getCcc()) 
		    	&& AonStringUtils.equalsIgnoreCase(nss, pec.getSsNum()) );
	}
	
	
	// ------------------------------------------------------------------------
	static String getBonusFormula(String code, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		return getBonusFormula(BONUS_QUOTA_EXPRESSION_MAP.get(quota), code, portTipo, quota, start, end);
		
	}
	
	static String getDeductionFormula(String code, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		return getDeductionFormula(DEDUCTION_QUOTA_EXPRESSION_MAP.get(quota), code, portTipo, quota, start, end);
	}
	
	// ------------------------------------------------------------------------
	
	private static Bonus newBonus(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		
		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		
		Bonus ssBonus = new Bonus();		
		ssBonus.setCcc(ccc);
		ssBonus.setNss(nss);
		ssBonus.setStartDate(start);
		ssBonus.setEndDate(end);
		ssBonus.setDescription(String.format(new Locale("es", "ES"),PEC_DESCRIPTION_MAP.getOrDefault(code, "%s (%.2f%%)"), description, percent));
		ssBonus.setFormula(getBonusFormula(code, portTipo, quota, start, end));
		
		
		return ssBonus;
	}

	private static Deduction newDeduction(String nss, String ccc, String code, String description, String portTipo,
			String quota, Date start, Date end) throws ParseException {
		
		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		
		Deduction ssDeduction = new Deduction();		
		ssDeduction.setCcc(ccc);
		ssDeduction.setNss(nss);
		ssDeduction.setStartDate(start);
		ssDeduction.setEndDate(end);
		ssDeduction.setName(ContextVariable.BONUS_EMPLOYEE.getName());
		ssDeduction.setDescription(String.format(new Locale("es", "ES"),PEC_DESCRIPTION_MAP.getOrDefault(code, "%s (%.2f%%)"), description, percent));
		ssDeduction.setFormula(getDeductionFormula(code, portTipo, quota, start, end));
		
		
		return ssDeduction;
	}

	private static String getBonusFormula(String expression, String code, String portTipo,
			String quota, Date start, Date end) throws ParseException {

		double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
		String formula ;
		
		formula = String.format(Locale.ROOT,
			"/*epoch:%d,pec:%s,quota:%s*//*read-only*/_D=(%s);_D == 0.00 ? REMOVE() : _D /**/",
			Calendar.getInstance().getTimeInMillis(),
			code, 
			quota,
			String.format(Locale.ROOT, PEC_EXPRESSION_MAP.get(code), expression, percent) 
			);
		
		return formula;
	}

	private static String getDeductionFormula(String expression, String code, String portTipo,
		String quota, Date start, Date end) throws ParseException {

        	double percent = NUMBER_FORMAT.parse(portTipo).doubleValue();
        	String formula ;
	
		formula = String.format(Locale.ROOT,
		"/*epoch:%d,pec:%s,quota:%s*//*read-only*/_D=(%s);_D == 0.00 ? REMOVE() : _D /**/",
		Calendar.getInstance().getTimeInMillis(),
		code, 
		quota,
		String.format(Locale.ROOT, PEC_DEDUCTION_EXPRESSION_MAP.getOrDefault(code, PEC_EXPRESSION_MAP.get(code)), expression, percent) 
		);
	
		return formula;
	}

	private static DeductionProvider newNegativeDeduction( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newNegativeDeduction(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static DeductionProvider newRemoveDeduction( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newRemoveDeduction(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static CostProvider newCgcITCost( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newCgcITCost(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static DeductionProvider newCgcITDeduction( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newCgcITDeduction(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static CostProvider newRemoveCost( ContextVariable var ) {
		return (nss, ccc, pec, quota, portTipo, description, start, end) -> newRemoveCost(nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static NegativeDeduction newNegativeDeduction(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
	    	NegativeDeduction deduction =  new NegativeDeduction();	
		return newNegativePEC(deduction, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static ZeroDeduction newZeroDeduction(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
	    	ZeroDeduction deduction =  new ZeroDeduction();	
		return newZeroPEC(deduction, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static RemoveDeduction newRemoveDeduction(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		RemoveDeduction deduction =  new RemoveDeduction();	
		return newRemovePEC(deduction, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static Cost newCgcITCost(
		String nss, 
		String ccc, 
		String pec, 
		String quota, 
		String portTipo,
		String description, 
		Date start, 
		Date end ,
		ContextVariable var){
        	Cost cost =  new Cost();	
		cost.setCcc(ccc);
		cost.setNss(nss);
		cost.setStartDate(start);
		cost.setEndDate(end);
		cost.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/BASE_CGC_E * (PORCENTAJE_CGC_E=1.30) / 100.00 /**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota
				));
		cost.setDescription(String.format(new Locale("es", "ES"),"%s %s (%s)", description, getDescription(var), portTipo));
		cost.setName(var.getName());
		
		return cost;
	}

	private static Deduction newCgcITDeduction(
		String nss, 
		String ccc, 
		String pec, 
		String quota, 
		String portTipo,
		String description, 
		Date start, 
		Date end ,
		ContextVariable var){
        	Deduction deduction =  new Deduction();	
		deduction.setCcc(ccc);
		deduction.setNss(nss);
		deduction.setStartDate(start);
		deduction.setEndDate(end);
		deduction.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/BASE_CGC * (PORCENTAJE_CGC=0.25) / 100.00 /**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota
				));
		deduction.setDescription(String.format(new Locale("es", "ES"),"%s %s (%s)", description, getDescription(var), portTipo));
		deduction.setName(var.getName());
		
		return deduction;
	}

	private static RemoveCost newRemoveCost(
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		RemoveCost cost =  new RemoveCost();	
		return newRemovePEC(cost, nss, ccc, pec, quota, portTipo, description, start, end, var);
	}

	private static <T extends PEC> T newZeroPEC(
			T t,
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		
		t.setCcc(ccc);
		t.setNss(nss);
		t.setStartDate(start);
		t.setEndDate(end);
		t.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/0.00/**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota
				));
		t.setDescription(String.format(new Locale("es", "ES"),"%s %s (%s%%)", description, getDescription(var), portTipo));
		t.setName(var.getName());
		
		return t;
	}

	private static <T extends PEC & AddPEC> T newRemovePEC(
		T t,
		String nss, 
		String ccc, 
		String trl,
		String description, 
		Date start, 
		Date end ,
		ContextVariable var){
	
        	t.setCcc(ccc);
        	t.setNss(nss);
        	t.setStartDate(start);
        	t.setEndDate(end);
        	t.setFormula(String.format(Locale.ROOT,
        			"/*epoch:%d,trl:%s*//*read-only*/REMOVE()/**/", 
        			Calendar.getInstance().getTimeInMillis(),
        			trl 
        			));
        	t.setDescription(String.format(new Locale("es", "ES"),"%s %s", description, getDescription(var)));
        	t.setName(var.getName());
        	
        	return t;
	}

	private static <T extends PEC & AddPEC> T newRemovePEC(
			T t,
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		
		t.setCcc(ccc);
		t.setNss(nss);
		t.setStartDate(start);
		t.setEndDate(end);
		t.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/REMOVE()/**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota
				));
		t.setDescription(String.format(new Locale("es", "ES"),"%s %s (%s)", description, getDescription(var), portTipo));
		t.setName(var.getName());
		
		return t;
	}
	
	private static <T extends PEC> T newNegativePEC(
			T t,
			String nss, 
			String ccc, 
			String pec, 
			String quota, 
			String portTipo,
			String description, 
			Date start, 
			Date end ,
			ContextVariable var){
		
		t.setCcc(ccc);
		t.setNss(nss);
		t.setStartDate(start);
		t.setEndDate(end);
		t.setFormula(String.format(Locale.ROOT,
				"/*epoch:%d,pec:%s,quota:%s*//*read-only*/-1 * %s/**/", 
				Calendar.getInstance().getTimeInMillis(),
				pec, 
				quota,
				var.getName()
				));
		t.setDescription(String.format(new Locale("es", "ES"),"%s (%s)", description, portTipo));
		//t.setName(getDeductionType(var).name());
		t.setName(ContextVariable.BONUS_EMPLOYEE.getName());
		
		return t;
	}

	private static  <T> Collection<T> collection (T ...ts) {
		ArrayList<T> list = new ArrayList<T>(ts.length);
		
		for (T t : ts)
			list.add(t);

		return list;
	}
	
	private static DeductionType getDeductionType(ContextVariable var) {
		switch (var) {
		case FP_EMPLOYEE:
		case FP_ENTERPRISE:
			return DeductionType.JOB_TRAINING;
		case UNEMPLOY_EMPLOYEE:
		case UNEMPLOY_ENTERPRISE:
			return DeductionType.UNEMPLOYMENT;
		case CGC_EMPLOYEE:
		case CGC_ENTERPRISE:
			return DeductionType.COMMON_CONTINGENCY;
		case IT_ENTERPRISE:
			return DeductionType.IT;
		case IMS_ENTERPRISE:
			return DeductionType.IMS;
		case FOGASA_ENTERPRISE:
			return DeductionType.FOGASA;
		default:
			return DeductionType.BONUS;
		}
	}
	
	private static String getDescription(ContextVariable var) {
		switch (var) {
		case IT_ENTERPRISE:
			return "IT";
		case IMS_ENTERPRISE:
			return "IMS";
		case MEI_EMPLOYEE:
		case MEI_ENTERPRISE:
			return "MEI";
		case FP_EMPLOYEE:
		case FP_ENTERPRISE:
			return "F.P";
		case CGC_EMPLOYEE:
		case CGC_ENTERPRISE:
			return "CONTINGENCIAS COMUNES";
		case FOGASA_ENTERPRISE:
			return "FOGASA";
		case UNEMPLOY_EMPLOYEE:
		case UNEMPLOY_ENTERPRISE:
			return "DESEMPLEO";

		default:
			return "";
		}
	}

}