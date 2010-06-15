package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.enumeration.FijoVariable;
import com.esferalia.aon.payroll.core.enumeration.IndiceComplemento;
import com.esferalia.aon.payroll.core.enumeration.Retribucion;
import com.esferalia.aon.payroll.core.enumeration.TipoComplemento;

public interface IPercepcion extends Serializable{
	
	Serializable getId();

	Date getFechaInicio();
	void setFechaInicio(Date fechaInicio);

	Date getFechaFin();
	void setFechaFin(Date fechaFin);

	Date getFechaRetroactividad();
	void setFechaRetroactividad(Date fechaRetroactividad);

	String getDescripcionComplemento();
	void setDescripcionComplemento(String descripcionComplemento);

	String getDescripcionAbreviada();
	void setDescripcionAbreviada(String descripcionAbreviada);

	String getFormaCalculo();
	void setFormaCalculo(String formaCalculo);

	String getTipoCotizacion();
	void setTipoCotizacion(String tipoCotizacion);

	Double getUnidades();
	void setUnidades(Double unidades);

	Double getImporteUnitario();
	void setImporteUnitario(Double importeUnitario);

	Double getImporte();
	void setImporte(Double importe);

	Integer getMes();
	void setMes(Integer mes);

	Double getGarantizadoILT();
	void setGarantizadoILT(Double garantizadoILT);

	String getRedondeoPagaExtra();
	void setRedondeoPagaExtra(String redondeoPagaExtra);

	Date getFechaCreacion();
	void setFechaCreacion(Date fechaCreacion);

	Date getHoraCreacion();
	void setHoraCreacion(Date horaCreacion);

	Date getFechaModificacion();
	void setFechaModificacion(Date fechaModificacion);

	Date getHoraModificacion();
	void setHoraModificacion(Date horaModificacion);

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);

	IComplemento getComplemento();
	void setComplemento(IComplemento complemento);

	IComplemento getComplementoAplicar();
	void setComplementoAplicar(IComplemento complementoAplicar);

	FijoVariable getFijoVariable();
	void setFijoVariable(FijoVariable fijovar);

	IndiceComplemento getIndiceComplemento();
	void setIndiceComplemento(IndiceComplemento indiceComplemento);

	TipoComplemento getTipoComplemento();
	void setTipoComplemento(TipoComplemento tipoComplemento);

	Retribucion getRetribucion();
	void setRetribucion(Retribucion retribucion);
	
}
