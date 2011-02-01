package com.esferalia.aon.payroll;

import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.ICliente;
import com.esferalia.aon.payroll.core.IDivisa;
import com.esferalia.aon.payroll.core.IEmpresa;

@Entity
@Table(name = "emprnif")
public class Empresa implements ITransferObject, IEmpresa {

	private static final long serialVersionUID = -3266513951564213596L;
	
	private Integer id;
    private Registry registry;
	private String name;
	private ICliente cliente;
	private String representante;
	private String representanteDocument;
	private String cargo;
	private IDivisa divisa;
	
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	@Column(name = "descripcion", nullable = false, length = 60)
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
    @Embedded
	public Registry getRegistry() {
		return registry;
	}
	@Override
	public void setRegistry(IRegistry registry) {
		this.registry = (Registry) registry;
	}
	
	@ManyToOne(targetEntity = Cliente.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "codcli", nullable = false)
	@Override
	public ICliente getCliente() {
		return this.cliente;
	}
	
	@Override
	public void setCliente(ICliente cliente) {
		this.cliente = cliente;
	}
	
	@Override
	@Column(name = "representante", length = 60)
	public String getRepresentante() {
		return this.representante;
	}
	@Override
	public void setRepresentante(String representante) {
		this.representante = representante;
	}
	@Override
	@Transient
	public String getNombreRepresentante() {
		String linea = this.representante;
		StringTokenizer tokens = new StringTokenizer(linea);
		if(tokens.countTokens()<1){
			return "";
		}
		return tokens.nextToken();
	}
	@Override
	@Transient
	public String getApellido1Representante() {
		String linea = this.representante;
		StringTokenizer tokens = new StringTokenizer(linea);
		if(tokens.countTokens()<2){
			return "";
		}
		tokens.nextToken();
		return tokens.nextToken();
	}
	@Override
	@Transient
	public String getApellido2Representante() {
		String linea = this.representante;
		StringTokenizer tokens = new StringTokenizer(linea);
		if(tokens.countTokens()<3){
			return "";
		}
		tokens.nextToken();
		tokens.nextToken();
		return tokens.nextToken();
	}
	
	@Override
	@Column(name = "nrodocrep", length = 10)
	public String getRepresentanteDocument() {
		return representanteDocument;
	}
	@Override
	public void setRepresentanteDocument(String representanteDocument) {
		this.representanteDocument = representanteDocument;
	}

	@Override
	@Column(name = "cargo", length = 60)
	public String getCargo() {
		return this.cargo;
	}
	@Override
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Override
	@Transient
	public boolean isActive() {
		return !(getCliente().isInactivo());
	}

	@ManyToOne(targetEntity = Divisa.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "divisa")
	@Override
	public IDivisa getDivisa() {
		return this.divisa;
	}
	@Override
	public void setDivisa(IDivisa divisa) {
		this.divisa = divisa;
	}
	
}

