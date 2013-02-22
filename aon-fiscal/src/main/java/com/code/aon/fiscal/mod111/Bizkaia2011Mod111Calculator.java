package com.code.aon.fiscal.mod111;

import com.code.aon.common.AonException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.FiscalModelDetailCalculator;

public class Bizkaia2011Mod111Calculator extends FiscalModelDetailCalculator implements IMod111Calculator {

	@Override
	public boolean accept(int year, Administration administration) {
		if ( year >= 2011 && administration == Administration.BIZKAIA) {
			return true;
		}
		return false;
	}

	@Override
	public void calculate(Mod111 mod111) throws AonException {
		calculateDetails(mod111.getDetails());
		double c03 = mod111.getDetail( Mod111Key.BZ_C03 ).getAmount();
		double c06 = mod111.getDetail( Mod111Key.BZ_C06 ).getAmount();
		double c09 = mod111.getDetail( Mod111Key.BZ_C09 ).getAmount();
		double c12 = mod111.getDetail( Mod111Key.BZ_C12 ).getAmount();
		double c15 = mod111.getDetail( Mod111Key.BZ_C15 ).getAmount();
		double c18 = mod111.getDetail( Mod111Key.BZ_C18 ).getAmount();
		double c21 = mod111.getDetail( Mod111Key.BZ_C21 ).getAmount();
		double c24 = mod111.getDetail( Mod111Key.BZ_C24 ).getAmount();
		double c27 = mod111.getDetail( Mod111Key.BZ_C27 ).getAmount();
		double c30 = mod111.getDetail( Mod111Key.BZ_C30 ).getAmount();
		double c33 = mod111.getDetail( Mod111Key.BZ_C33 ).getAmount();
		double c36 = mod111.getDetail( Mod111Key.BZ_C36 ).getAmount();
		FiscalModelDetail detail = mod111.getDetail( Mod111Key.BZ_C37 );
		detail.setAmount( CommonUtil.round(c03+c06+c09+c12+c15+c18+c21+c24+c27+c30+c33+c36) );
	}

	@Override
	public Mod111Key getKeyForReceivers() {
		return Mod111Key.BZ_C01;
	}

	@Override
	public Mod111Key getKeyForPerception() {
		return Mod111Key.BZ_C02;
	}

	@Override
	public Mod111Key getKeyForWitholding() {
		return Mod111Key.BZ_C03;
	}

	@Override
	public Mod111Key getKeyForInKindReceivers() {
		return Mod111Key.BZ_C01;
	}

	@Override
	public Mod111Key getKeyForInKindPerception() {
		return Mod111Key.BZ_C02;
	}

	@Override
	public Mod111Key getKeyForInKindWitholding() {
		return Mod111Key.BZ_C03;
	}
	
	@Override
	public double getResult(Mod111 mod111) {
		return mod111.getDetail( Mod111Key.BZ_C37 ).getAmount();
	}
	
}
