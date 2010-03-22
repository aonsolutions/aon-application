package com.code.aon.payroll.principales;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.tipos.Tipovia;


@Entity
@Table(name="domicilio")
public class Domicilio  implements ITransferObject {

     private Integer cdg;
     private String nomvia;
     private String numero;
     private String otrdir;
     private String codpos;
     private String localidad;
     private String persona;
     private String telefono;
     private String telefono2;
     private String telefono3;
     private String fax;
     private String email;
     private String linea1;
     private String linea2;
     private String aclaracion;
     private String codnsz;
     private Cliente cliente;
     private Provincia provincia;
     private Tipovia tipovia;
   
   

    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=4)
    public Integer getCdg() {
        return this.cdg;
    }
    
    public void setCdg(Integer cdg) {
        this.cdg = cdg;
    }
    
    @Column(name="nomvia", length=40)
    public String getNomvia() {
        return this.nomvia;
    }
    
    public void setNomvia(String nomvia) {
        this.nomvia = nomvia;
    }
    
	
    @Column(name="numero", length=5)
    public String getNumero() {
        return this.numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    

    @Column(name="otrdir", length=12)
    public String getOtrdir() {
        return this.otrdir;
    }
    
    public void setOtrdir(String otrdir) {
        this.otrdir = otrdir;
    }
    
	
    @Column(name="codpos", length=5)
    public String getCodpos() {
        return this.codpos;
    }
    
    public void setCodpos(String codpos) {
        this.codpos = codpos;
    }
    
	
    @Column(name="localidad", length=30)
    public String getLocalidad() {
        return this.localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    

    @Column(name="persona", length=50)
    public String getPersona() {
        return this.persona;
    }
    
    public void setPersona(String persona) {
        this.persona = persona;
    }
    
	
    @Column(name="telefono", length=12)
    public String getTelefono() {
        return this.telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
	
    @Column(name="telefono2", length=12)
    public String getTelefono2() {
        return this.telefono2;
    }
    
    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }
    
	
    @Column(name="telefono3", length=12)
    public String getTelefono3() {
        return this.telefono3;
    }
    
    public void setTelefono3(String telefono3) {
        this.telefono3 = telefono3;
    }
    
	
    @Column(name="fax", length=12)
    public String getFax() {
        return this.fax;
    }
    
    public void setFax(String fax) {
        this.fax = fax;
    }
    
	
    @Column(name="email", length=50)
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    

    @Column(name="linea1", length=35)
    public String getLinea1() {
        return this.linea1;
    }
    
    public void setLinea1(String linea1) {
        this.linea1 = linea1;
    }
    
	
    @Column(name="linea2", length=35)
    public String getLinea2() {
        return this.linea2;
    }
    
    public void setLinea2(String linea2) {
        this.linea2 = linea2;
    }
    
	
    @Column(name="aclaracion", length=35)
    public String getAclaracion() {
        return this.aclaracion;
    }
    
    public void setAclaracion(String aclaracion) {
        this.aclaracion = aclaracion;
    }
    
	
    @Column(name="codnsz", length=7)
    public String getCodnsz() {
        return this.codnsz;
    }
    
    public void setCodnsz(String codnsz) {
        this.codnsz = codnsz;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codcli")
    public Cliente getCliente() {
        return this.cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="provincia")
    public Provincia getProvincia() {
        return this.provincia;
    }
    
    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="tipovia")
    public Tipovia getTipovia() {
        return this.tipovia;
    }
    
    public void setTipovia(Tipovia tipovia) {
        this.tipovia = tipovia;
    }


}


