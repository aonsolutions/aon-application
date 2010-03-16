package com.esferalia.aon.payroll;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.IPersona;



@Entity
@Table(name="persona")
public class Persona implements ITransferObject, IPersona  {

	private static final long serialVersionUID = 492566375940771381L;
	
	private Integer cdg;
//     private String numdoc;
     private String name;
     private String surname;
     private String lastName;
//     private String alias;
//     private String aliastc2;
//     private String nomvia;
//     private String numero;
//     private String otrdir;
//     private String codpos;
//     private String localidad;
//     private String telefono;
//     private String fax;
//     private String email;
//     private String lugnac;
//     private Date fecnac;
//     private String padre;
//     private String madre;
//     private String numss;
//     private EstadoCivil estciv;
//     private String obsper;
//     private Date fecnew;
//     private Date hornew;
//     private Date fecmod;
//     private Date hormod;
//     private Sexo sexo;
//     private Documento tipdoc;
//     private Nacion nacion;
//     private Pais pais;
//     private Pais pais1;
//     private Provincia provincia;
//     private Provincia provincia1;
//     private Tipovia tipovia;
//     private String nombreComp;


  

    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=4)
    public Integer getCdg() {
        return this.cdg;
    }
    
    public void setCdg(Integer cdg) {
        this.cdg = cdg;
    }

    @Override
    @Transient
	public IRegistry getRegistry() {
		return null;
	}
	@Override
	public void setRegistry(IRegistry registry) {
	}

	@Override
	@Column(name="descripcion", nullable=false, length=35)
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Column(name="nombre", length=25)
	public String getSurname() {
		return surname;
	}
	@Override
	public void setSurname(String surname) {
		this.surname = surname;
		
	}

	@Override
	@Column(name="apellido2", length=25)
	public String getLastName() {
		return lastName;
	}
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


//    @Column(name="numdoc", nullable=false, length=10)
//    public String getNumdoc() {
//        return this.numdoc;
//    }
//    
//    public void setNumdoc(String numdoc) {
//        this.numdoc = numdoc;
//    }
//    
//
//    public String getDescripcion() {
//        return this.descripcion;
//    }
//    
//    public void setDescripcion(String descripcion) {
//        this.descripcion = descripcion;
//    }
//
//    @Column(name="alias", length=25)
//    public String getAlias() {
//        return this.alias;
//    }
//    
//    public void setAlias(String alias) {
//        this.alias = alias;
//    }
//    
//
//    @Column(name="aliastc2", length=5)
//    public String getAliastc2() {
//        return this.aliastc2;
//    }
//    
//    public void setAliastc2(String aliastc2) {
//        this.aliastc2 = aliastc2;
//    }
//    
//
//    @Column(name="nomvia", length=40)
//    public String getNomvia() {
//        return this.nomvia;
//    }
//    
//    public void setNomvia(String nomvia) {
//        this.nomvia = nomvia;
//    }
//    
//
//    @Column(name="numero", length=5)
//    public String getNumero() {
//        return this.numero;
//    }
//    
//    public void setNumero(String numero) {
//        this.numero = numero;
//    }
//    
//
//    @Column(name="otrdir", length=12)
//    public String getOtrdir() {
//        return this.otrdir;
//    }
//    
//    public void setOtrdir(String otrdir) {
//        this.otrdir = otrdir;
//    }
//    
//
//    @Column(name="codpos", length=5)
//    public String getCodpos() {
//        return this.codpos;
//    }
//    
//    public void setCodpos(String codpos) {
//        this.codpos = codpos;
//    }
//    
//
//    @Column(name="localidad", length=30)
//    public String getLocalidad() {
//        return this.localidad;
//    }
//    
//    public void setLocalidad(String localidad) {
//        this.localidad = localidad;
//    }
//    
//
//    @Column(name="telefono", length=12)
//    public String getTelefono() {
//        return this.telefono;
//    }
//    
//    public void setTelefono(String telefono) {
//        this.telefono = telefono;
//    }
//    
//
//    @Column(name="fax", length=12)
//    public String getFax() {
//        return this.fax;
//    }
//    
//    public void setFax(String fax) {
//        this.fax = fax;
//    }
//    
//
//    @Column(name="email", length=40)
//    public String getEmail() {
//        return this.email;
//    }
//    
//    public void setEmail(String email) {
//        this.email = email;
//    }
//    
//
//    @Column(name="lugnac", length=30)
//    public String getLugnac() {
//        return this.lugnac;
//    }
//    
//    public void setLugnac(String lugnac) {
//        this.lugnac = lugnac;
//    }
//    @Temporal(TemporalType.DATE)
//    @Column(name="fecnac")
//    public Date getFecnac() {
//        return this.fecnac;
//    }
//    
//    public void setFecnac(Date fecnac) {
//        this.fecnac = fecnac;
//    }
//    
//
//    @Column(name="padre", length=25)
//    public String getPadre() {
//        return this.padre;
//    }
//    
//    public void setPadre(String padre) {
//        this.padre = padre;
//    }
//    
//
//    @Column(name="madre", length=25)
//    public String getMadre() {
//        return this.madre;
//    }
//    
//    public void setMadre(String madre) {
//        this.madre = madre;
//    }
//    
//
//    @Column(name="numss", length=12)
//    public String getNumss() {
//        return this.numss;
//    }
//    
//    public void setNumss(String numss) {
//        this.numss = numss;
//    }
//    
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.esferalia.aon.payroll.enumeration.EstadoCivil")} )
//    @Column(name="estciv", length=1)
//    public EstadoCivil getEstciv() {
//        return this.estciv;
//    }
//    
//    public void setEstciv(EstadoCivil estciv) {
//        this.estciv = estciv;
//    }
//    
//
//    @Column(name="obsper", length=100)
//    public String getObsper() {
//        return this.obsper;
//    }
//    
//    public void setObsper(String obsper) {
//        this.obsper = obsper;
//    }
//    
//    
//    @Temporal(TemporalType.DATE)
//    @Column(name="fecnew")
//    public Date getFecnew() {
//        return this.fecnew;
//    }
//    
//    public void setFecnew(Date fecnew) {
//        this.fecnew = fecnew;
//    }
//    
//    
//    @Temporal(TemporalType.TIME)
//    @Column(name="hornew")
//    public Date getHornew() {
//        return this.hornew;
//    }
//    
//    public void setHornew(Date hornew) {
//        this.hornew = hornew;
//    }
//    
//    
//    @Temporal(TemporalType.DATE)
//    @Column(name="fecmod")
//    public Date getFecmod() {
//        return this.fecmod;
//    }
//    
//    public void setFecmod(Date fecmod) {
//        this.fecmod = fecmod;
//    }
//    
//    
//    @Temporal(TemporalType.TIME)
//    @Column(name="hormod")
//    public Date getHormod() {
//        return this.hormod;
//    }
//    
//    public void setHormod(Date hormod) {
//        this.hormod = hormod;
//    }
//    
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.esferalia.aon.payroll.enumeration.Sexo")} )
//    @Column(name="sexo", length=1)
//    public Sexo getSexo() {
//        return this.sexo;
//    }
//    
//    public void setSexo(Sexo sexo) {
//        this.sexo = sexo;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="inddoc")
//    public Documento getTipdoc() {
//        return this.tipdoc;
//    }
//    
//    public void setTipdoc(Documento tipdoc) {
//        this.tipdoc = tipdoc;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="nacion")
//    public Nacion getNacion() {
//        return this.nacion;
//    }
//    
//    public void setNacion(Nacion nacion) {
//        this.nacion = nacion;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="paiemi")
//    public Pais getPais() {
//        return this.pais;
//    }
//    
//    public void setPais(Pais pais) {
//        this.pais = pais;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="painac")
//    public Pais getPais1() {
//        return this.pais1;
//    }
//    
//    public void setPais1(Pais pais1) {
//        this.pais1 = pais1;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="provincia")
//    public Provincia getProvincia() {
//        return this.provincia;
//    }
//    
//    public void setProvincia(Provincia provincia) {
//        this.provincia = provincia;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="pronac")
//    public Provincia getProvincia1() {
//        return this.provincia1;
//    }
//    
//    public void setProvincia1(Provincia provincia1) {
//        this.provincia1 = provincia1;
//    }
//    
//	@ManyToOne(fetch=FetchType.EAGER)
//    @JoinColumn(name="tipovia")
//    public Tipovia getTipovia() {
//        return this.tipovia;
//    }
//    
//    public void setTipovia(Tipovia tipovia) {
//        this.tipovia = tipovia;
//    }
//
//    @Transient
//	public String getNombreComp() {
//		//return nombreComp;
//    	return this.descripcion + " " + this.apellido2 + ", " + this.nombre;
//	}
//
//	public void setNombreComp(String nombreComp) {
//		//this.nombreComp = nombreComp;
//		this.nombreComp = this.descripcion + " " + this.apellido2 + ", " + this.nombre; 
//	}
//
//

}


