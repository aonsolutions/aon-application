package com.code.aon.ui.loader.pojo;

import com.code.aon.product.enumeration.ProductType;


public class LoadedItem implements ILoadedPojo{

	private String codigo;
	private String codigoBarras;
	private String nombre;
	private Double precioVentaBase;
	private Double precioCompra;
	private Double beneficioSobreCompra;
	private Double porcRetencion;
	private Double porcIva;
	private Integer inventariable;
	private String categoria;
	private String marca;
	private Integer naturaleza;
	private String cuentaVenta;
	private String cuentaCompra;

	@Override
	public String getIdentifier() {
		return getCodigo();
	}

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getCodigoBarras() {
		return codigoBarras;
	}
	public void setCodigoBarras(String codigoBarras) {
		this.codigoBarras = codigoBarras;
	}

	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecioVentaBase() {
		return precioVentaBase;
	}
	public void setPrecioVentaBase(Double precioVentaBase) {
		this.precioVentaBase = precioVentaBase;
	}

	public Double getPrecioCompra() {
		return precioCompra;
	}
	public void setPrecioCompra(Double precioCompra) {
		this.precioCompra = precioCompra;
	}

	public Double getBeneficioSobreCompra() {
		return beneficioSobreCompra;
	}
	public void setBeneficioSobreCompra(Double beneficioSobreCompra) {
		this.beneficioSobreCompra = beneficioSobreCompra;
	}

	public Double getPorcRetencion() {
		return porcRetencion;
	}
	public void setPorcRetencion(Double porcRetencion) {
		this.porcRetencion = porcRetencion;
	}

	public Double getPorcIva() {
		return porcIva;
	}
	public void setPorcIva(Double porcIva) {
		this.porcIva = porcIva;
	}

	public Integer getInventariable() {
		return inventariable;
	}
	public void setInventariable(Integer inventariable) {
		this.inventariable = inventariable;
	}
	public boolean isInventoriable() {
		return (getInventariable() != null && getInventariable() == 1); 
	}

	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Integer getNaturaleza() {
		return naturaleza;
	}
	public void setNaturaleza(Integer naturaleza) {
		this.naturaleza = naturaleza;
	}
	public ProductType getProductType() {
		if (getNaturaleza() ==  null) return ProductType.COMMERCIAL_PRODUCT; 
		return ProductType.values()[getNaturaleza()];
	}

	public String getCuentaVenta() {
		return cuentaVenta;
	}
	public void setCuentaVenta(String cuentaVenta) {
		this.cuentaVenta = cuentaVenta;
	}

	public String getCuentaCompra() {
		return cuentaCompra;
	}
	public void setCuentaCompra(String cuentaCompra) {
		this.cuentaCompra = cuentaCompra;
	}


}
