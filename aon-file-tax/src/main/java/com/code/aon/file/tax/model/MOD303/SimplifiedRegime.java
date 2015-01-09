package com.code.aon.file.tax.model.MOD303;

import org.apache.commons.lang.StringUtils;



public class SimplifiedRegime {

	private int agr1Code;
	private double agr1Ingreso;
	private double agr1Indice;
	private double agr1Cuota;
	private double agr1Porce;
	private double agr1IngCta;
	private double agr1CuotaSop4T;
	private double agr1CuotaDer4T;
	private String act1epi;
	private double act1Uni1;
	private double act1Imp1;
	private double act1Uni2;
	private double act1Imp2;
	private double act1Uni3;
	private double act1Imp3;
	private double act1Uni4;
	private double act1Imp4;
	private double act1Uni5;
	private double act1Imp5;
	private double act1Uni6;
	private double act1Imp6;
	private double act1Uni7;
	private double act1Imp7;
	private double act1Cuota;
	private double act1Reduc;
	private double act1IndTemp;
	private double act1Porce;
	private double act1IngCta;
	private int agr2Code;
	private double agr2Ingreso;
	private double agr2Indice;
	private double agr2Cuota;
	private double agr2Porce;
	private double agr2IngCta;
	private double agr2CuotaSop4T;
	private double agr2CuotaDer4T;
	private String act2epi;
	private double act2Uni1;
	private double act2Imp1;
	private double act2Uni2;
	private double act2Imp2;
	private double act2Uni3;
	private double act2Imp3;
	private double act2Uni4;
	private double act2Imp4;
	private double act2Uni5;
	private double act2Imp5;
	private double act2Uni6;
	private double act2Imp6;
	private double act2Uni7;
	private double act2Imp7;
	private double act2Cuota;
	private double act2Reduc;
	private double act2IndTemp;
	private double act2Porce;
	private double act2IngCta;
	private double sumIngCta;
	private double act1CuotaSop4T;
	private double act1IndTemp4T;
	private double act1Resultado4T;
	private double act1PorCuoMin4T;
	private double act1DevCuoPai4T;
	private double act1CuoMin4T;
	private double act1CuoAnuDer4T;
	private double act2CuotaSop4T;
	private double act2IndTemp4T;
	private double act2Resultado4T;
	private double act2PorCuoMin4T;
	private double act2DevCuoPai4T;
	private double act2CuoMin4T;
	private double act2CuoAnuDer4T;
	private double sumCuoDer4T;
	private double sumIngCta4T;
	private double result4T;
	private double adqIntrac;
	private double entrActFijo;
	private double ivaISP;
	private double totalCuota;
	private double adqActFijo;
	private double regularizacion;
	private double totalDeducible;
	private double result;
	
	public int getAgr1Code() {
		return agr1Code;
	}
	public void setAgr1Code(int agr1Code) {
		this.agr1Code = agr1Code;
	}
	public String getAgr1CodeString() {
		return agr1Code==0?"00":StringUtils.leftPad(Integer.toString(agr1Code), 2, '0');
	}
	public double getAgr1Ingreso() {
		return agr1Ingreso;
	}
	public void setAgr1Ingreso(double agr1Ingreso) {
		this.agr1Ingreso = agr1Ingreso;
	}
	public double getAgr1Indice() {
		return agr1Indice;
	}
	public void setAgr1Indice(double agr1Indice) {
		this.agr1Indice = agr1Indice;
	}
	public double getAgr1Cuota() {
		return agr1Cuota;
	}
	public void setAgr1Cuota(double agr1Cuota) {
		this.agr1Cuota = agr1Cuota;
	}
	public double getAgr1Porce() {
		return agr1Porce;
	}
	public void setAgr1Porce(double agr1Porce) {
		this.agr1Porce = agr1Porce;
	}
	public double getAgr1IngCta() {
		return agr1IngCta;
	}
	public void setAgr1IngCta(double agr1IngCta) {
		this.agr1IngCta = agr1IngCta;
	}
	public double getAgr1CuotaSop4T() {
		return agr1CuotaSop4T;
	}
	public void setAgr1CuotaSop4T(double agr1CuotaSop4T) {
		this.agr1CuotaSop4T = agr1CuotaSop4T;
	}
	public double getAgr1CuotaDer4T() {
		return agr1CuotaDer4T;
	}
	public void setAgr1CuotaDer4T(double agr1CuotaDer4T) {
		this.agr1CuotaDer4T = agr1CuotaDer4T;
	}
	public String getAct1epi() {
		return act1epi;
	}
	public void setAct1epi(String act1epi) {
		this.act1epi = act1epi;
	}
	public double getAct1Uni1() {
		return act1Uni1;
	}
	public void setAct1Uni1(double act1Uni1) {
		this.act1Uni1 = act1Uni1;
	}
	public double getAct1Imp1() {
		return act1Imp1;
	}
	public void setAct1Imp1(double act1Imp1) {
		this.act1Imp1 = act1Imp1;
	}
	public double getAct1Uni2() {
		return act1Uni2;
	}
	public void setAct1Uni2(double act1Uni2) {
		this.act1Uni2 = act1Uni2;
	}
	public double getAct1Imp2() {
		return act1Imp2;
	}
	public void setAct1Imp2(double act1Imp2) {
		this.act1Imp2 = act1Imp2;
	}
	public double getAct1Uni3() {
		return act1Uni3;
	}
	public void setAct1Uni3(double act1Uni3) {
		this.act1Uni3 = act1Uni3;
	}
	public double getAct1Imp3() {
		return act1Imp3;
	}
	public void setAct1Imp3(double act1Imp3) {
		this.act1Imp3 = act1Imp3;
	}
	public double getAct1Uni4() {
		return act1Uni4;
	}
	public void setAct1Uni4(double act1Uni4) {
		this.act1Uni4 = act1Uni4;
	}
	public double getAct1Imp4() {
		return act1Imp4;
	}
	public void setAct1Imp4(double act1Imp4) {
		this.act1Imp4 = act1Imp4;
	}
	public double getAct1Uni5() {
		return act1Uni5;
	}
	public void setAct1Uni5(double act1Uni5) {
		this.act1Uni5 = act1Uni5;
	}
	public double getAct1Imp5() {
		return act1Imp5;
	}
	public void setAct1Imp5(double act1Imp5) {
		this.act1Imp5 = act1Imp5;
	}
	public double getAct1Uni6() {
		return act1Uni6;
	}
	public void setAct1Uni6(double act1Uni6) {
		this.act1Uni6 = act1Uni6;
	}
	public double getAct1Imp6() {
		return act1Imp6;
	}
	public void setAct1Imp6(double act1Imp6) {
		this.act1Imp6 = act1Imp6;
	}
	public double getAct1Uni7() {
		return act1Uni7;
	}
	public void setAct1Uni7(double act1Uni7) {
		this.act1Uni7 = act1Uni7;
	}
	public double getAct1Imp7() {
		return act1Imp7;
	}
	public void setAct1Imp7(double act1Imp7) {
		this.act1Imp7 = act1Imp7;
	}
	public double getAct1Cuota() {
		return act1Cuota;
	}
	public void setAct1Cuota(double act1Cuota) {
		this.act1Cuota = act1Cuota;
	}
	public double getAct1Reduc() {
		return act1Reduc;
	}
	public void setAct1Reduc(double act1Reduc) {
		this.act1Reduc = act1Reduc;
	}
	public double getAct1IndTemp() {
		return act1IndTemp;
	}
	public void setAct1IndTemp(double act1IndTemp) {
		this.act1IndTemp = act1IndTemp;
	}
	public double getAct1Porce() {
		return act1Porce;
	}
	public void setAct1Porce(double act1Porce) {
		this.act1Porce = act1Porce;
	}
	public double getAct1IngCta() {
		return act1IngCta;
	}
	public void setAct1IngCta(double act1IngCta) {
		this.act1IngCta = act1IngCta;
	}
	public int getAgr2Code() {
		return agr2Code;
	}
	public String getAgr2CodeString() {
		return agr2Code==0?"00":StringUtils.leftPad(Integer.toString(agr2Code), 2, '0');
	}
	public void setAgr2Code(int agr2Code) {
		this.agr2Code = agr2Code;
	}
	public double getAgr2Ingreso() {
		return agr2Ingreso;
	}
	public void setAgr2Ingreso(double agr2Ingreso) {
		this.agr2Ingreso = agr2Ingreso;
	}
	public double getAgr2Indice() {
		return agr2Indice;
	}
	public void setAgr2Indice(double agr2Indice) {
		this.agr2Indice = agr2Indice;
	}
	public double getAgr2Cuota() {
		return agr2Cuota;
	}
	public void setAgr2Cuota(double agr2Cuota) {
		this.agr2Cuota = agr2Cuota;
	}
	public double getAgr2Porce() {
		return agr2Porce;
	}
	public void setAgr2Porce(double agr2Porce) {
		this.agr2Porce = agr2Porce;
	}
	public double getAgr2IngCta() {
		return agr2IngCta;
	}
	public void setAgr2IngCta(double agr2IngCta) {
		this.agr2IngCta = agr2IngCta;
	}
	public double getAgr2CuotaSop4T() {
		return agr2CuotaSop4T;
	}
	public void setAgr2CuotaSop4T(double agr2CuotaSop4T) {
		this.agr2CuotaSop4T = agr2CuotaSop4T;
	}
	public double getAgr2CuotaDer4T() {
		return agr2CuotaDer4T;
	}
	public void setAgr2CuotaDer4T(double agr2CuotaDer4T) {
		this.agr2CuotaDer4T = agr2CuotaDer4T;
	}
	public String getAct2epi() {
		return act2epi;
	}
	public void setAct2epi(String act2epi) {
		this.act2epi = act2epi;
	}
	public double getAct2Uni1() {
		return act2Uni1;
	}
	public void setAct2Uni1(double act2Uni1) {
		this.act2Uni1 = act2Uni1;
	}
	public double getAct2Imp1() {
		return act2Imp1;
	}
	public void setAct2Imp1(double act2Imp1) {
		this.act2Imp1 = act2Imp1;
	}
	public double getAct2Uni2() {
		return act2Uni2;
	}
	public void setAct2Uni2(double act2Uni2) {
		this.act2Uni2 = act2Uni2;
	}
	public double getAct2Imp2() {
		return act2Imp2;
	}
	public void setAct2Imp2(double act2Imp2) {
		this.act2Imp2 = act2Imp2;
	}
	public double getAct2Uni3() {
		return act2Uni3;
	}
	public void setAct2Uni3(double act2Uni3) {
		this.act2Uni3 = act2Uni3;
	}
	public double getAct2Imp3() {
		return act2Imp3;
	}
	public void setAct2Imp3(double act2Imp3) {
		this.act2Imp3 = act2Imp3;
	}
	public double getAct2Uni4() {
		return act2Uni4;
	}
	public void setAct2Uni4(double act2Uni4) {
		this.act2Uni4 = act2Uni4;
	}
	public double getAct2Imp4() {
		return act2Imp4;
	}
	public void setAct2Imp4(double act2Imp4) {
		this.act2Imp4 = act2Imp4;
	}
	public double getAct2Uni5() {
		return act2Uni5;
	}
	public void setAct2Uni5(double act2Uni5) {
		this.act2Uni5 = act2Uni5;
	}
	public double getAct2Imp5() {
		return act2Imp5;
	}
	public void setAct2Imp5(double act2Imp5) {
		this.act2Imp5 = act2Imp5;
	}
	public double getAct2Uni6() {
		return act2Uni6;
	}
	public void setAct2Uni6(double act2Uni6) {
		this.act2Uni6 = act2Uni6;
	}
	public double getAct2Imp6() {
		return act2Imp6;
	}
	public void setAct2Imp6(double act2Imp6) {
		this.act2Imp6 = act2Imp6;
	}
	public double getAct2Uni7() {
		return act2Uni7;
	}
	public void setAct2Uni7(double act2Uni7) {
		this.act2Uni7 = act2Uni7;
	}
	public double getAct2Imp7() {
		return act2Imp7;
	}
	public void setAct2Imp7(double act2Imp7) {
		this.act2Imp7 = act2Imp7;
	}
	public double getAct2Cuota() {
		return act2Cuota;
	}
	public void setAct2Cuota(double act2Cuota) {
		this.act2Cuota = act2Cuota;
	}
	public double getAct2Reduc() {
		return act2Reduc;
	}
	public void setAct2Reduc(double act2Reduc) {
		this.act2Reduc = act2Reduc;
	}
	public double getAct2IndTemp() {
		return act2IndTemp;
	}
	public void setAct2IndTemp(double act2IndTemp) {
		this.act2IndTemp = act2IndTemp;
	}
	public double getAct2Porce() {
		return act2Porce;
	}
	public void setAct2Porce(double act2Porce) {
		this.act2Porce = act2Porce;
	}
	public double getAct2IngCta() {
		return act2IngCta;
	}
	public void setAct2IngCta(double act2IngCta) {
		this.act2IngCta = act2IngCta;
	}
	public double getSumIngCta() {
		return sumIngCta;
	}
	public void setSumIngCta(double sumIngCta) {
		this.sumIngCta = sumIngCta;
	}
	public double getAct1CuotaSop4T() {
		return act1CuotaSop4T;
	}
	public void setAct1CuotaSop4T(double act1CuotaSop4T) {
		this.act1CuotaSop4T = act1CuotaSop4T;
	}
	public double getAct1IndTemp4T() {
		return act1IndTemp4T;
	}
	public void setAct1IndTemp4T(double act1IndTemp4T) {
		this.act1IndTemp4T = act1IndTemp4T;
	}
	public double getAct1Resultado4T() {
		return act1Resultado4T;
	}
	public void setAct1Resultado4T(double act1Resultado4T) {
		this.act1Resultado4T = act1Resultado4T;
	}
	public double getAct1PorCuoMin4T() {
		return act1PorCuoMin4T;
	}
	public void setAct1PorCuoMin4T(double act1PorCuoMin4T) {
		this.act1PorCuoMin4T = act1PorCuoMin4T;
	}
	public double getAct1DevCuoPai4T() {
		return act1DevCuoPai4T;
	}
	public void setAct1DevCuoPai4T(double act1DevCuoPai4T) {
		this.act1DevCuoPai4T = act1DevCuoPai4T;
	}
	public double getAct1CuoMin4T() {
		return act1CuoMin4T;
	}
	public void setAct1CuoMin4T(double act1CuoMin4T) {
		this.act1CuoMin4T = act1CuoMin4T;
	}
	public double getAct1CuoAnuDer4T() {
		return act1CuoAnuDer4T;
	}
	public void setAct1CuoAnuDer4T(double act1CuoAnuDer4T) {
		this.act1CuoAnuDer4T = act1CuoAnuDer4T;
	}
	public double getAct2CuotaSop4T() {
		return act2CuotaSop4T;
	}
	public void setAct2CuotaSop4T(double act2CuotaSop4T) {
		this.act2CuotaSop4T = act2CuotaSop4T;
	}
	public double getAct2IndTemp4T() {
		return act2IndTemp4T;
	}
	public void setAct2IndTemp4T(double act2IndTemp4T) {
		this.act2IndTemp4T = act2IndTemp4T;
	}
	public double getAct2Resultado4T() {
		return act2Resultado4T;
	}
	public void setAct2Resultado4T(double act2Resultado4T) {
		this.act2Resultado4T = act2Resultado4T;
	}
	public double getAct2PorCuoMin4T() {
		return act2PorCuoMin4T;
	}
	public void setAct2PorCuoMin4T(double act2PorCuoMin4T) {
		this.act2PorCuoMin4T = act2PorCuoMin4T;
	}
	public double getAct2DevCuoPai4T() {
		return act2DevCuoPai4T;
	}
	public void setAct2DevCuoPai4T(double act2DevCuoPai4T) {
		this.act2DevCuoPai4T = act2DevCuoPai4T;
	}
	public double getAct2CuoMin4T() {
		return act2CuoMin4T;
	}
	public void setAct2CuoMin4T(double act2CuoMin4T) {
		this.act2CuoMin4T = act2CuoMin4T;
	}
	public double getAct2CuoAnuDer4T() {
		return act2CuoAnuDer4T;
	}
	public void setAct2CuoAnuDer4T(double act2CuoAnuDer4T) {
		this.act2CuoAnuDer4T = act2CuoAnuDer4T;
	}
	public double getSumCuoDer4T() {
		return sumCuoDer4T;
	}
	public void setSumCuoDer4T(double sumCuoDer4T) {
		this.sumCuoDer4T = sumCuoDer4T;
	}
	public double getSumIngCta4T() {
		return sumIngCta4T;
	}
	public void setSumIngCta4T(double sumIngCta4T) {
		this.sumIngCta4T = sumIngCta4T;
	}
	public double getResult4T() {
		return result4T;
	}
	public void setResult4T(double result4t) {
		result4T = result4t;
	}
	public double getAdqIntrac() {
		return adqIntrac;
	}
	public void setAdqIntrac(double adqIntrac) {
		this.adqIntrac = adqIntrac;
	}
	public double getEntrActFijo() {
		return entrActFijo;
	}
	public void setEntrActFijo(double entrActFijo) {
		this.entrActFijo = entrActFijo;
	}
	public double getIvaISP() {
		return ivaISP;
	}
	public void setIvaISP(double ivaISP) {
		this.ivaISP = ivaISP;
	}
	public double getTotalCuota() {
		return totalCuota;
	}
	public void setTotalCuota(double totalCuota) {
		this.totalCuota = totalCuota;
	}
	public double getAdqActFijo() {
		return adqActFijo;
	}
	public void setAdqActFijo(double adqActFijo) {
		this.adqActFijo = adqActFijo;
	}
	public double getRegularizacion() {
		return regularizacion;
	}
	public void setRegularizacion(double regularizacion) {
		this.regularizacion = regularizacion;
	}
	public double getTotalDeducible() {
		return totalDeducible;
	}
	public void setTotalDeducible(double totalDeducible) {
		this.totalDeducible = totalDeducible;
	}
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
}
