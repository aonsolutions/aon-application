package com.esferalia.aon.payroll.core.cotizacion;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.enumeration.TipoProrrateo;

public interface IBaseCotizacion extends Serializable{

	String getCdg();
	void setCdg(String cdg);

	String getDescription();
	void setDescription(String description);

	TipoProrrateo getTipoProrrateo();
	void setTipoProrrateo(TipoProrrateo tipoProrrateo);

	
	
}
