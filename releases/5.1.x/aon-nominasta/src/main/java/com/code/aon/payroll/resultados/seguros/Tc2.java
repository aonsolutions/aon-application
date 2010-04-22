package com.code.aon.payroll.resultados.seguros;



import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Tipccc;
import com.code.aon.payroll.principales.empresa.Actividad;
 
@Entity
@Table(name="tc2")
public class Tc2  implements ITransferObject {

     private Integer cdg;
     private Actividad codact;
     private Tipccc codccc;
     private String codcon;
     private Integer numtra;
     private Integer mes;
     private Integer anio;
     private Integer mesref;
     private Integer anioref;
     private String tipo;
     private BigDecimal baseConcom;
     private BigDecimal baseAcctra;
     private BigDecimal baseHexno;
     private BigDecimal baseHexest;
     private BigDecimal baseCccemp;
     private BigDecimal baseOccemp;
     private BigDecimal compEcal;
     private BigDecimal compAcc;
     private BigDecimal redConcom;
     private BigDecimal redInem;
     private String imprime;
     private String divisa;
     private Boolean tc2red;
     private Date fecnew;
     private Date hornew;
     private Date fecmod;
     private Date hormod;
     private String indregimen;
   
     public Tc2() {
        tipo="0";
        numtra=0;
        baseConcom= new BigDecimal(0);
        baseAcctra = new BigDecimal(0);
        baseHexno = new BigDecimal(0);
        baseHexest = new BigDecimal(0);
        baseCccemp = new BigDecimal(0);
        baseOccemp = new BigDecimal(0);
        compEcal= new BigDecimal(0);
        compAcc = new BigDecimal(0);
        redConcom = new BigDecimal(0);
        redInem = new BigDecimal(0);

	}

   

    @Id     
	//@DataDefinition(label="Codigo de TC2")
    @Column(name="cdg", unique=true, nullable=false, length=4)
    public Integer getCdg() {
        return this.cdg;
    }
    
    public void setCdg(Integer cdg) {
        this.cdg = cdg;
    }
    
	//@DataDefinition(label="Codigo de Actividad")
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codact", nullable=false)
    public Actividad getCodact() {
        return this.codact;
    }
    
    public void setCodact(Actividad codact) {
        this.codact = codact;
    }
    
	//@DataDefinition(label="Tipo Cuenta Cotizacion")
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Tipccc")} )
    @Column(name="codccc", length=1)
    public Tipccc getCodccc() {
        return this.codccc;
    }
    
    public void setCodccc(Tipccc codccc) {
        this.codccc = codccc;
    }
    
	//@DataDefinition(label="Convenio Colectivo",descriptionColumn=true)

    @Column(name="codcon", length=7)
    public String getCodcon() {
        return this.codcon;
    }
    
    public void setCodcon(String codcon) {
        this.codcon = codcon;
    }
    
	//@DataDefinition(label="Numero Trabajadores")
    @Column(name="numtra", length=2)
    public Integer getNumtra() {
        return this.numtra;
    }
    
    public void setNumtra(Integer numtra) {
        this.numtra = numtra;
    }
    
	//@DataDefinition(label="Mes")
    @Column(name="mes", length=2)
    public Integer getMes() {
        return this.mes;
    }
    
    public void setMes(Integer mes) {
        this.mes = mes;
    }
    
	//@DataDefinition(label="Anio")
    @Column(name="anio", length=2)
    public Integer getAnio() {
        return this.anio;
    }
    
    public void setAnio(Integer anio) {
        this.anio = anio;
    }
    
	//@DataDefinition(label="Mes")
    @Column(name="mesref", length=2)
    public Integer getMesref() {
        return this.mesref;
    }
    
    public void setMesref(Integer mesref) {
        this.mesref = mesref;
    }
    
	//@DataDefinition(label="Año")
    @Column(name="anioref", length=2)
    public Integer getAnioref() {
        return this.anioref;
    }
    
    public void setAnioref(Integer anioref) {
        this.anioref = anioref;
    }
    
	//@DataDefinition(label="Tipo Liquidacion")
    @Column(name="tipo", length=3)
    public String getTipo() {
        return this.tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
	//@DataDefinition(label="Contingencias Comunes")
    @Column(name="base_concom", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseConcom() {
        return this.baseConcom;
    }
    
    public void setBaseConcom(BigDecimal baseConcom) {
        this.baseConcom = baseConcom;
    }
    
	//@DataDefinition(label="Accidentes Trabajo")
    @Column(name="base_acctra", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseAcctra() {
        return this.baseAcctra;
    }
    
    public void setBaseAcctra(BigDecimal baseAcctra) {
        this.baseAcctra = baseAcctra;
    }
    
	//@DataDefinition(label="Horas Extraordinarias Fuerza Mayor ( no Estruc )")
    @Column(name="base_hexno", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseHexno() {
        return this.baseHexno;
    }
    
    public void setBaseHexno(BigDecimal baseHexno) {
        this.baseHexno = baseHexno;
    }
    
	//@DataDefinition(label="Horas Extraordinarias Estructurales")
    @Column(name="base_hexest", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseHexest() {
        return this.baseHexest;
    }
    
    public void setBaseHexest(BigDecimal baseHexest) {
        this.baseHexest = baseHexest;
    }
    
	//@DataDefinition(label="Contig. Comunes Cot. Empresarial")
    @Column(name="base_cccemp", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseCccemp() {
        return this.baseCccemp;
    }
    
    public void setBaseCccemp(BigDecimal baseCccemp) {
        this.baseCccemp = baseCccemp;
    }
    
	//@DataDefinition(label="Otras Cotizaciones Cot. Empresarial")
    @Column(name="base_occemp", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseOccemp() {
        return this.baseOccemp;
    }
    
    public void setBaseOccemp(BigDecimal baseOccemp) {
        this.baseOccemp = baseOccemp;
    }
    
	//@DataDefinition(label="Compensaciones IT Enfermedad Comun")
    @Column(name="comp_ecal", nullable=false, scale=2, precision=11)
    public BigDecimal getCompEcal() {
        return this.compEcal;
    }
    
    public void setCompEcal(BigDecimal compEcal) {
        this.compEcal = compEcal;
    }
    
	//@DataDefinition(label="Compensaciones IT Accidentes Trabajo")
    @Column(name="comp_acc", nullable=false, scale=2, precision=11)
    public BigDecimal getCompAcc() {
        return this.compAcc;
    }
    
    public void setCompAcc(BigDecimal compAcc) {
        this.compAcc = compAcc;
    }
    
	//@DataDefinition(label="Reducciones contingencias Comunes")
    @Column(name="red_concom", nullable=false, scale=2, precision=11)
    public BigDecimal getRedConcom() {
        return this.redConcom;
    }
    
    public void setRedConcom(BigDecimal redConcom) {
        this.redConcom = redConcom;
    }
    
	//@DataDefinition(label="Bonif. Redudcc. INEM")
    @Column(name="red_inem", nullable=false, scale=2, precision=11)
    public BigDecimal getRedInem() {
        return this.redInem;
    }
    
    public void setRedInem(BigDecimal redInem) {
        this.redInem = redInem;
    }
    
	//@DataDefinition(label="Indicador de Impresion")
    @Column(name="imprime", nullable=false, length=1)
    public String getImprime() {
        return this.imprime;
    }
    
    public void setImprime(String imprime) {
        this.imprime = imprime;
    }
    
	//@DataDefinition(label="Divisa")
    @Column(name="divisa", length=3)
    public String getDivisa() {
        return this.divisa;
    }
    
    public void setDivisa(String divisa) {
        this.divisa = divisa;
    }
    
	//@DataDefinition(label="Se ha insertado en el fichero FAN")
	@Type(type="siNoType" )
    @Column(name="tc2red", nullable=false, length=1)
    public Boolean getTc2red() {
        return this.tc2red;
    }
    
    public void setTc2red(Boolean tc2red) {
        this.tc2red = tc2red;
    }
    @Temporal(TemporalType.DATE)
	//@DataDefinition(label="Fecha Creacion Fila")
    @Column(name="fecnew")
    public Date getFecnew() {
        return this.fecnew;
    }
    
    public void setFecnew(Date fecnew) {
        this.fecnew = fecnew;
    }
    @Temporal(TemporalType.TIME)
	//@DataDefinition(label="Hora Creacion Fila")
    @Column(name="hornew")
    public Date getHornew() {
        return this.hornew;
    }
    
    public void setHornew(Date hornew) {
        this.hornew = hornew;
    }
    @Temporal(TemporalType.DATE)
	//@DataDefinition(label="Fecha Modificacion Fila")
    @Column(name="fecmod")
    public Date getFecmod() {
        return this.fecmod;
    }
    
    public void setFecmod(Date fecmod) {
        this.fecmod = fecmod;
    }
    @Temporal(TemporalType.TIME)
	//@DataDefinition(label="Hora Modificacion Fila")
    @Column(name="hormod")
    public Date getHormod() {
        return this.hormod;
    }
    
    public void setHormod(Date hormod) {
        this.hormod = hormod;
    }
    
	//@DataDefinition(label="Régimen")
    @Column(name="indregimen", length=1)
    public String getIndregimen() {
        return this.indregimen;
    }
    
    public void setIndregimen(String indregimen) {
        this.indregimen = indregimen;
    }





}


