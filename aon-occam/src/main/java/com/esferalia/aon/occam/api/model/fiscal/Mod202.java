package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod202 extends FiscalModel implements Serializable {

	private static final long serialVersionUID = 3614782856588153510L;

//	private CNAE2009 cnae; // En el ejercicio 2026 se cambia al CNAE2025, lo cambio a String 
	private String cnae;
	private Date initialDate;
	
	public Mod202() {
		super();
		setModel(FiscalModelType.M202);
	}

	@Override
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Mod202.getResult(): Unsupported method! (use getDeclarationResult())");
	}
	
	@Override
	@Deprecated
	public Mod202Key getDeclarationTypeKey() {
		throw new UnsupportedOperationException("Mod202.getDeclarationTypeKey(): Unsupported method! (use getDeclarationResultType())");
	}
	
	@Override
	@Deprecated
	public void setDefaultDeclarationType(){
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}

	@Override
	@Deprecated
	public boolean isDiffCalculationAvailable() {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}
	
	@Override
	@Deprecated
	public boolean isDiffCalculationDisabled() {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}

	@Override
	@Deprecated
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}

//	public Mod202Key getDeclarationTypeKey() {
//		if (getAdministration() == null) return null;
//		else if (isAEAT()) return Mod202Key.P01;
//		return null;
//	}
//	

	/**
	 * El tipo de declaración para la presentación por lotes puede ser: 
	 * 		I (ingreso), 
	 * 		U (domiciliación), 
	 * 		G (Ingreso en C.C.T.) 
	 * 		N (Negativa/Sin actividad/Resultado cero)
	 */
	public String getAeatDeclarationType() {
		// CON EL REFACTOR LA KEY P01 YA NO SE GRABA, EL TIPO DE RESULTADO SE GRABA EN LA TABLA FS_MODEL
		// return getDescription(Mod202Key.P01);
		return getDeclarationResultType().getValue();
	}

//	public CNAE2009 getCnae() {
//		return cnae;
//	}
//	public void setCnae(CNAE2009 cnae) {
//		this.cnae = cnae;
//	}
	
	public String getCnae() {
		return cnae;
	}
	public void setCnae(String cnae) {
		this.cnae = cnae;
	}

	public Date getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public String getIban() {
		return getDescription(Mod202Key.P00);
	}
	public void setIban(String iban) {
		putDescription(Mod202Key.P00, iban);
	}
	
	public boolean isForal() {
		return (isForalNavarra() || isForalEuskadi());		
	}
	public boolean isForalNavarra() {
		return (getAmount(Mod202Key.X15) == 1);		
	}
	public boolean isForalEuskadi() {
		return (getAmount(Mod202Key.X16) == 1
			|| getAmount(Mod202Key.X17) == 1
			|| getAmount(Mod202Key.X18) == 1);		
	}
	
	public boolean isMethodA() {
		return AonMathUtils.isZero(getAmount(Mod202Key.X00));
	}
	public boolean isMethodB() {
		return AonMathUtils.isNotZero(getAmount(Mod202Key.X00));
	}
	public boolean isMethodB1() {
		return AonMathUtils.equals(getAmount(Mod202Key.X00),1);
	}
	public boolean isMethodB2() {
		return AonMathUtils.equals(getAmount(Mod202Key.X00),2);
	}
	
	@Override
	public boolean isStrictToDeposit() {
		return (isFinished() || isCustomerAccepted() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}

	//	@Override
//	public void setDefaultDeclarationType(){
//		if (isAEAT()) {
//			if (AonMathUtils.isGreatherThanZero(getResult() )) {
//				setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
//				setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
//			} else {
//				setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
//				setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
//			}
//		}
//	}
//
	
//	@Override
//	public void setDeclarationType(String type) {
//		// Nothing
//	}
	
}
