package com.code.aon.ui.audabridge.response;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;

public class CalculationDataResponse implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String wan;
	private String numeroValoracion;
	private String referencia;
	private String fechaValoracion;
	private String fechaTarifa;
	private String codigoAudaTransfer;
	private TotalGeneral totalGeneral;
	private List<Equipo> listaEquipo;
	private List<Pieza> listaPiezas;
	private List<Operacion> listaOperaciones;
	private List<Pintura> listaPintura;

	public String getWan() {
		return wan;
	}

	public void setWan(String wan) {
		this.wan = wan;
	}

	public String getNumeroValoracion() {
		return numeroValoracion;
	}

	public void setNumeroValoracion(String numeroValoracion) {
		this.numeroValoracion = numeroValoracion;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getFechaValoracion() {
		return fechaValoracion;
	}
	public void setFechaValoracion(String fechaValoracion) {
		this.fechaValoracion = fechaValoracion;
	}
	public Date getFechaValoracionAsDate() {
		return getIsoDate(getFechaValoracion());
	}

	public String getFechaTarifa() {
		return fechaTarifa;
	}
	public void setFechaTarifa(String fechaTarifa) {
		this.fechaTarifa = fechaTarifa;
	}
	public Date getFechaTarifaAsDate() {
		return getIsoDate(getFechaTarifa());
	}
	private Date getIsoDate(String date) {
		if (StringUtils.isBlank(date)) return null;
		String newDate = date.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddZ");
		try {
			return formatter.parse(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getCodigoAudaTransfer() {
		return codigoAudaTransfer;
	}

	public void setCodigoAudaTransfer(String codigoAudaTransfer) {
		this.codigoAudaTransfer = codigoAudaTransfer;
	}

	public TotalGeneral getTotalGeneral() {
		return totalGeneral;
	}

	public void setTotalGeneral(TotalGeneral totalGeneral) {
		this.totalGeneral = totalGeneral;
	}

	public List<Equipo> getListaEquipo() {
		if (listaEquipo == null) {
			listaEquipo = new LinkedList<Equipo>();
		}
		return listaEquipo;
	}

	public void setListaEquipo(List<Equipo> listaEquipo) {
		this.listaEquipo = listaEquipo;
	}

	public List<Pieza> getListaPiezas() {
		if (listaPiezas == null) {
			listaPiezas = new LinkedList<Pieza>();
		}
		return listaPiezas;
	}

	public void setListaPiezas(List<Pieza> listaPiezas) {
		this.listaPiezas = listaPiezas;
	}

	public List<Operacion> getListaOperaciones() {
		if (listaOperaciones == null) {
			listaOperaciones = new LinkedList<Operacion>();
		}
		return listaOperaciones;
	}

	public void setListaOperaciones(List<Operacion> listaOperaciones) {
		this.listaOperaciones = listaOperaciones;
	}

	public List<Pintura> getListaPintura() {
		if (listaPintura == null) {
			listaPintura = new LinkedList<Pintura>();
		}
		return listaPintura;
	}

	public void setListaPintura(List<Pintura> listaPintura) {
		this.listaPintura = listaPintura;
	}

	public void addEquipo(Equipo equipo) {
		getListaEquipo().add(equipo);
	}

	public void addPieza(Pieza pieza) {
		getListaPiezas().add(pieza);
	}

	public void addOperacion(Operacion operacion) {
		getListaOperaciones().add(operacion);
	}

	public void addPintura(Pintura pintura) {
		getListaPintura().add(pintura);
	}

}
