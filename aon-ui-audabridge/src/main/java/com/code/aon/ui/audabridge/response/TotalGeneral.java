package com.code.aon.ui.audabridge.response;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class TotalGeneral implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String nombreFabricante; 
	private String nombreModelo;
	private String nombreVariante; 
	private String estadoGeneral;
	private String tipoPintura;
	private String chasis;
	private double precioHoraChapa;
	private double precioHoraPintura; 
	private double precioHoraMecanica; 
	private int tiempoBase;
	private int posicionesIntroducidas; 
	private double totalMo;
	private double totalPintura; 
	private double totalPiezas;
	private double totalSinIva;
	private double descuentos;
	private double baseImponible;
	private double porcentajeIva;
	private double importeIva;
	private double totalValoracion;
	private double totalPagar;
	private double danosOcultos;
	private double importeParcialRepuestos; 
	private double numeroUtChapa;
	private double importeParcialChapa;
	private double importeFijoChapa;
	private double utAlineacion;
	private double importeParcialAlineacion;
	private double sumaVarios;
	private double importeFijoAlineacion;
	private double totalTrabajosAuxiliares;
	private double tiempoMoPintura;
	private double tiempoPreparacionPintura;
	private double tiempoTotalMoPintura;
	private double importeMoPintura;
	private double materialesPinturaSuperficie;
	private double constanteMaterialPintura;
	private double importeFijoPintura;
	private double materialPintura;
	private double utTratamientoBajos;
	private double importeTratamientoBajos;
	private double importeFijoBajos;
	private double totalTratamientoBajos;
	private double utTratamientoAnticorrosion;
	private double importeAnticorrosion;
	private double importeFijoAnticorrosion;
	private double totalAnticorrosion;
	private double importeFranquicia;
	private double descuentoAdicional;
	private double utMoPinturaPlas;
	private double importeMoPinturaChapa;
	private double importeMoPinturaPlastico;
	private double importePreparacionPintura;
	private double importeMatPinturaPlastico;
	private double repuestosAbonados;
	private double descuentoSobreRecambios;
	private double incrementoRecambios;
	private double pequenoMaterial;
	private double importeMOAdicional;
	private double dtoMOChapa;
	private double dtoMaterialPintura;
	private double dtoSobreTotalPintura;
	private double dtoMOPintura;
	private double dtoAsignaDirecta;
	private double tasaSigaus;
	private double tasaSignus;
	
	public String getNombreFabricante() {
		return nombreFabricante;
	}
	public void setNombreFabricante(String nombreFabricante) {
		this.nombreFabricante = nombreFabricante;
	}
	public String getNombreModelo() {
		return nombreModelo;
	}
	public void setNombreModelo(String nombreModelo) {
		this.nombreModelo = nombreModelo;
	}
	public String getNombreVariante() {
		return nombreVariante;
	}
	public void setNombreVariante(String nombreVariante) {
		this.nombreVariante = nombreVariante;
	}
	public String getEstadoGeneral() {
		return estadoGeneral;
	}
	public void setEstadoGeneral(String estadoGeneral) {
		this.estadoGeneral = estadoGeneral;
	}
	public String getTipoPintura() {
		return tipoPintura;
	}
	public void setTipoPintura(String tipoPintura) {
		this.tipoPintura = tipoPintura;
	}
	public String getChasis() {
		return chasis;
	}
	public void setChasis(String chasis) {
		this.chasis = chasis;
	}
	public double getPrecioHoraChapa() {
		return precioHoraChapa;
	}
	public void setPrecioHoraChapa(double precioHoraChapa) {
		this.precioHoraChapa = precioHoraChapa;
	}
	public double getPrecioHoraPintura() {
		return precioHoraPintura;
	}
	public void setPrecioHoraPintura(double precioHoraPintura) {
		this.precioHoraPintura = precioHoraPintura;
	}
	public double getPrecioHoraMecanica() {
		return precioHoraMecanica;
	}
	public void setPrecioHoraMecanica(double precioHoraMecanica) {
		this.precioHoraMecanica = precioHoraMecanica;
	}
	public int getTiempoBase() {
		return tiempoBase;
	}
	public void setTiempoBase(int tiempoBase) {
		this.tiempoBase = tiempoBase;
	}
	public int getPosicionesIntroducidas() {
		return posicionesIntroducidas;
	}
	public void setPosicionesIntroducidas(int posicionesIntroducidas) {
		this.posicionesIntroducidas = posicionesIntroducidas;
	}
	public double getTotalMo() {
		return totalMo;
	}
	public void setTotalMo(double totalMo) {
		this.totalMo = totalMo;
	}
	public double getTotalPintura() {
		return totalPintura;
	}
	public void setTotalPintura(double totalPintura) {
		this.totalPintura = totalPintura;
	}
	public double getTotalPiezas() {
		return totalPiezas;
	}
	public void setTotalPiezas(double totalPiezas) {
		this.totalPiezas = totalPiezas;
	}
	public double getTotalSinIva() {
		return totalSinIva;
	}
	public void setTotalSinIva(double totalSinIva) {
		this.totalSinIva = totalSinIva;
	}
	public double getDescuentos() {
		return descuentos;
	}
	public void setDescuentos(double descuentos) {
		this.descuentos = descuentos;
	}
	public double getBaseImponible() {
		return baseImponible;
	}
	public void setBaseImponible(double baseImponible) {
		this.baseImponible = baseImponible;
	}
	public double getPorcentajeIva() {
		return porcentajeIva;
	}
	public void setPorcentajeIva(double porcentajeIva) {
		this.porcentajeIva = porcentajeIva;
	}
	public double getImporteIva() {
		return importeIva;
	}
	public void setImporteIva(double importeIva) {
		this.importeIva = importeIva;
	}
	public double getTotalValoracion() {
		return totalValoracion;
	}
	public void setTotalValoracion(double totalValoracion) {
		this.totalValoracion = totalValoracion;
	}
	public double getTotalPagar() {
		return totalPagar;
	}
	public void setTotalPagar(double totalPagar) {
		this.totalPagar = totalPagar;
	}
	public double getDanosOcultos() {
		return danosOcultos;
	}
	public void setDanosOcultos(double danosOcultos) {
		this.danosOcultos = danosOcultos;
	}
	public double getImporteParcialRepuestos() {
		return importeParcialRepuestos;
	}
	public void setImporteParcialRepuestos(double importeParcialRepuestos) {
		this.importeParcialRepuestos = importeParcialRepuestos;
	}
	public double getNumeroUtChapa() {
		return numeroUtChapa;
	}
	public void setNumeroUtChapa(double numeroUtChapa) {
		this.numeroUtChapa = numeroUtChapa;
	}
	public double getImporteParcialChapa() {
		return importeParcialChapa;
	}
	public void setImporteParcialChapa(double importeParcialChapa) {
		this.importeParcialChapa = importeParcialChapa;
	}
	public double getImporteFijoChapa() {
		return importeFijoChapa;
	}
	public void setImporteFijoChapa(double importeFijoChapa) {
		this.importeFijoChapa = importeFijoChapa;
	}
	public double getUtAlineacion() {
		return utAlineacion;
	}
	public void setUtAlineacion(double utAlineacion) {
		this.utAlineacion = utAlineacion;
	}
	public double getImporteParcialAlineacion() {
		return importeParcialAlineacion;
	}
	public void setImporteParcialAlineacion(double importeParcialAlineacion) {
		this.importeParcialAlineacion = importeParcialAlineacion;
	}
	public double getSumaVarios() {
		return sumaVarios;
	}
	public void setSumaVarios(double sumaVarios) {
		this.sumaVarios = sumaVarios;
	}
	public double getImporteFijoAlineacion() {
		return importeFijoAlineacion;
	}
	public void setImporteFijoAlineacion(double importeFijoAlineacion) {
		this.importeFijoAlineacion = importeFijoAlineacion;
	}
	public double getTotalTrabajosAuxiliares() {
		return totalTrabajosAuxiliares;
	}
	public void setTotalTrabajosAuxiliares(double totalTrabajosAuxiliares) {
		this.totalTrabajosAuxiliares = totalTrabajosAuxiliares;
	}
	public double getTiempoMoPintura() {
		return tiempoMoPintura;
	}
	public void setTiempoMoPintura(double tiempoMoPintura) {
		this.tiempoMoPintura = tiempoMoPintura;
	}
	public double getTiempoPreparacionPintura() {
		return tiempoPreparacionPintura;
	}
	public void setTiempoPreparacionPintura(double tiempoPreparacionPintura) {
		this.tiempoPreparacionPintura = tiempoPreparacionPintura;
	}
	public double getTiempoTotalMoPintura() {
		return tiempoTotalMoPintura;
	}
	public void setTiempoTotalMoPintura(double tiempoTotalMoPintura) {
		this.tiempoTotalMoPintura = tiempoTotalMoPintura;
	}
	public double getImporteMoPintura() {
		return importeMoPintura;
	}
	public void setImporteMoPintura(double importeMoPintura) {
		this.importeMoPintura = importeMoPintura;
	}
	public double getMaterialesPinturaSuperficie() {
		return materialesPinturaSuperficie;
	}
	public void setMaterialesPinturaSuperficie(double materialesPinturaSuperficie) {
		this.materialesPinturaSuperficie = materialesPinturaSuperficie;
	}
	public double getConstanteMaterialPintura() {
		return constanteMaterialPintura;
	}
	public void setConstanteMaterialPintura(double constanteMaterialPintura) {
		this.constanteMaterialPintura = constanteMaterialPintura;
	}
	public double getImporteFijoPintura() {
		return importeFijoPintura;
	}
	public void setImporteFijoPintura(double importeFijoPintura) {
		this.importeFijoPintura = importeFijoPintura;
	}
	public double getMaterialPintura() {
		return materialPintura;
	}
	public void setMaterialPintura(double materialPintura) {
		this.materialPintura = materialPintura;
	}
	public double getUtTratamientoBajos() {
		return utTratamientoBajos;
	}
	public void setUtTratamientoBajos(double utTratamientoBajos) {
		this.utTratamientoBajos = utTratamientoBajos;
	}
	public double getImporteTratamientoBajos() {
		return importeTratamientoBajos;
	}
	public void setImporteTratamientoBajos(double importeTratamientoBajos) {
		this.importeTratamientoBajos = importeTratamientoBajos;
	}
	public double getImporteFijoBajos() {
		return importeFijoBajos;
	}
	public void setImporteFijoBajos(double importeFijoBajos) {
		this.importeFijoBajos = importeFijoBajos;
	}
	public double getTotalTratamientoBajos() {
		return totalTratamientoBajos;
	}
	public void setTotalTratamientoBajos(double totalTratamientoBajos) {
		this.totalTratamientoBajos = totalTratamientoBajos;
	}
	public double getUtTratamientoAnticorrosion() {
		return utTratamientoAnticorrosion;
	}
	public void setUtTratamientoAnticorrosion(double utTratamientoAnticorrosion) {
		this.utTratamientoAnticorrosion = utTratamientoAnticorrosion;
	}
	public double getImporteAnticorrosion() {
		return importeAnticorrosion;
	}
	public void setImporteAnticorrosion(double importeAnticorrosion) {
		this.importeAnticorrosion = importeAnticorrosion;
	}
	public double getImporteFijoAnticorrosion() {
		return importeFijoAnticorrosion;
	}
	public void setImporteFijoAnticorrosion(double importeFijoAnticorrosion) {
		this.importeFijoAnticorrosion = importeFijoAnticorrosion;
	}
	public double getTotalAnticorrosion() {
		return totalAnticorrosion;
	}
	public void setTotalAnticorrosion(double totalAnticorrosion) {
		this.totalAnticorrosion = totalAnticorrosion;
	}
	public double getImporteFranquicia() {
		return importeFranquicia;
	}
	public void setImporteFranquicia(double importeFranquicia) {
		this.importeFranquicia = importeFranquicia;
	}
	public double getDescuentoAdicional() {
		return descuentoAdicional;
	}
	public void setDescuentoAdicional(double descuentoAdicional) {
		this.descuentoAdicional = descuentoAdicional;
	}
	public double getUtMoPinturaPlas() {
		return utMoPinturaPlas;
	}
	public void setUtMoPinturaPlas(double utMoPinturaPlas) {
		this.utMoPinturaPlas = utMoPinturaPlas;
	}
	public double getImporteMoPinturaChapa() {
		return importeMoPinturaChapa;
	}
	public void setImporteMoPinturaChapa(double importeMoPinturaChapa) {
		this.importeMoPinturaChapa = importeMoPinturaChapa;
	}
	public double getImporteMoPinturaPlastico() {
		return importeMoPinturaPlastico;
	}
	public void setImporteMoPinturaPlastico(double importeMoPinturaPlastico) {
		this.importeMoPinturaPlastico = importeMoPinturaPlastico;
	}
	public double getImportePreparacionPintura() {
		return importePreparacionPintura;
	}
	public void setImportePreparacionPintura(double importePreparacionPintura) {
		this.importePreparacionPintura = importePreparacionPintura;
	}
	public double getImporteMatPinturaPlastico() {
		return importeMatPinturaPlastico;
	}
	public void setImporteMatPinturaPlastico(double importeMatPinturaPlastico) {
		this.importeMatPinturaPlastico = importeMatPinturaPlastico;
	}
	public double getRepuestosAbonados() {
		return repuestosAbonados;
	}
	public void setRepuestosAbonados(double repuestosAbonados) {
		this.repuestosAbonados = repuestosAbonados;
	}
	public double getDescuentoSobreRecambios() {
		return descuentoSobreRecambios;
	}
	public void setDescuentoSobreRecambios(double descuentoSobreRecambios) {
		this.descuentoSobreRecambios = descuentoSobreRecambios;
	}
	public double getIncrementoRecambios() {
		return incrementoRecambios;
	}
	public void setIncrementoRecambios(double incrementoRecambios) {
		this.incrementoRecambios = incrementoRecambios;
	}
	public double getPequenoMaterial() {
		return pequenoMaterial;
	}
	public void setPequenoMaterial(double pequenoMaterial) {
		this.pequenoMaterial = pequenoMaterial;
	}
	public double getImporteMOAdicional() {
		return importeMOAdicional;
	}
	public void setImporteMOAdicional(double importeMOAdicional) {
		this.importeMOAdicional = importeMOAdicional;
	}
	public double getDtoMOChapa() {
		return dtoMOChapa;
	}
	public void setDtoMOChapa(double dtoMOChapa) {
		this.dtoMOChapa = dtoMOChapa;
	}
	public double getDtoMaterialPintura() {
		return dtoMaterialPintura;
	}
	public void setDtoMaterialPintura(double dtoMaterialPintura) {
		this.dtoMaterialPintura = dtoMaterialPintura;
	}
	public double getDtoSobreTotalPintura() {
		return dtoSobreTotalPintura;
	}
	public void setDtoSobreTotalPintura(double dtoSobreTotalPintura) {
		this.dtoSobreTotalPintura = dtoSobreTotalPintura;
	}
	public double getDtoMOPintura() {
		return dtoMOPintura;
	}
	public void setDtoMOPintura(double dtoMOPintura) {
		this.dtoMOPintura = dtoMOPintura;
	}
	public double getDtoAsignaDirecta() {
		return dtoAsignaDirecta;
	}
	public void setDtoAsignaDirecta(double dtoAsignaDirecta) {
		this.dtoAsignaDirecta = dtoAsignaDirecta;
	}
	public double getTasaSigaus() {
		return tasaSigaus;
	}
	public void setTasaSigaus(double tasaSigaus) {
		this.tasaSigaus = tasaSigaus;
	}
	public double getTasaSignus() {
		return tasaSignus;
	}
	public void setTasaSignus(double tasaSignus) {
		this.tasaSignus = tasaSignus;
	}

}
