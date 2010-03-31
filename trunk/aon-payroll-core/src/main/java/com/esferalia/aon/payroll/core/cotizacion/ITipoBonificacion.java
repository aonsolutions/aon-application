package com.esferalia.aon.payroll.core.cotizacion;

import com.code.aon.common.ITransferObject;


public interface ITipoBonificacion extends ITransferObject{
	
	Integer getId();
	void setId(Integer id);
	
	String getDescription();
	void setDescription(String description);

//	FormaCalculo getFormaCalculo();
//	void setFormaCalculo(String formaCalculo);
	
	Double getPorcentajeContingenciasGenerales();
	void setPorcentajeContingenciasGenerales(Double porcentajeContingenciasGenerales);
	
	Double getPorcentajeAccidentes();
	void setPorcentajeAccidentes(Double porcentajeAccidentes);
	
	Double getPorcentajeBaseConjunto();
	void setPorcentajeBaseConjunto(Double porcentajeBaseConjunto);
	
	String getPorcentajeBonificacionSS();
	void setPorcentajeBonificacionSS(String porcentajeBonificacionSS);
	
	String getMayor60();
	void setMayor60(String mayor60);
	
	String getRealDecretoLey052006();
	void setRealDecretoLey052006(String realDecretoLey052006);
	
	String getRestarIT();
	void setRestarIT(String restarIT);	
}
