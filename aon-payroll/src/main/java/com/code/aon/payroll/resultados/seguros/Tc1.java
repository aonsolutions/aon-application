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
import com.code.aon.payroll.principales.personas.Persona;


@Entity
@Table(name="tc1")
public class Tc1  implements ITransferObject {

     private Integer cdg;
     private Actividad codact;
     private Tipccc codccc;
     private Integer numtra;
     private Integer desdeMes;
     private Integer desdeAnio;
     private Integer hastaMes;
     private Integer hastaAnio;
     private String mutuaccc;
     private BigDecimal baseConcom;
     private BigDecimal prcConcom;
     private BigDecimal cuotaConcom;
     private BigDecimal baseHexno;
     private BigDecimal prcHexno;
     private BigDecimal cuotaHexno;
     private BigDecimal baseHexest;
     private BigDecimal prcHexest;
     private BigDecimal cuotaHexest;
     private BigDecimal baseRedit;
     private BigDecimal baseRedcc;
     private BigDecimal baseReducc;
     private BigDecimal liqCotgen;
     private BigDecimal baseAcctra;
     private BigDecimal cuotasIt;
     private BigDecimal cuotasIms;
     private BigDecimal cuotasAcc;
     private BigDecimal compIt;
     private BigDecimal liqAcc;
     private BigDecimal prcDesem;
     private BigDecimal cuotaDesem;
     private BigDecimal redInem;
     private BigDecimal liqOtras;
     private BigDecimal baseMora;
     private BigDecimal prcMora;
     private BigDecimal cuotaMora;
     private BigDecimal importeTc1;
     private BigDecimal baseServcom;
     private BigDecimal prcServcom;
     private BigDecimal cuotaServcom;
     private BigDecimal baseDedcol;
     private BigDecimal prcDedcol;
     private BigDecimal cuotaDedcol;
     private Integer numero;
     private Persona codper;
     private Integer dias;
     private String codbas;
     private Tc2 codtc2;
     private String codepi;
     private Integer horas;
     private Integer diasit;
     private Integer diasmat;
     private BigDecimal baseAcc;
     private Date fecha;
     private String sitesp;
     private String nombre;
     private String apellidos;
     private String mostrar;
     private String cdgred;
     private String desglose;
     private Boolean comision;
     private String tipotc1;
     private Integer tc2;
     private String divisa;
     private Date fecnew;
     private Date hornew;
     private Date fecmod;
     private Date hormod;
     private String cdgOtrcon;
     private BigDecimal baseOtrcon;
     private BigDecimal prcOtrcon;
     private BigDecimal cuotaOtrcon;
     private BigDecimal baseConcomCe;
     private BigDecimal prcConcomCe;
     private BigDecimal cuotaConcomCe;
     private BigDecimal baseDesemCe;
     private BigDecimal prcDesemCe;
     private BigDecimal cuotaDesemCe;
     private BigDecimal baseDesem;
     private Integer horcomp;
     private BigDecimal impcomp;
     private Integer horpres;
     private BigDecimal imppres;
     private Integer hordist;
     private BigDecimal impdist;
     private String indregimen;

   
     public Tc1() {
    	 numtra=new Integer(0);
    	 baseConcom= new BigDecimal(0);
         prcConcom= new BigDecimal(0);
         cuotaConcom= new BigDecimal(0);
         baseHexno= new BigDecimal(0);
         prcHexno= new BigDecimal(0);
         cuotaHexno= new BigDecimal(0);
         baseHexest= new BigDecimal(0);
         prcHexest= new BigDecimal(0);
         cuotaHexest= new BigDecimal(0);
         baseRedit= new BigDecimal(0);
         baseRedcc= new BigDecimal(0);
         baseReducc= new BigDecimal(0);
         liqCotgen= new BigDecimal(0);
         baseAcctra= new BigDecimal(0);
         cuotasIt= new BigDecimal(0);
         cuotasIms= new BigDecimal(0);
         cuotasAcc= new BigDecimal(0);
         compIt= new BigDecimal(0);
         liqAcc= new BigDecimal(0);
         prcDesem= new BigDecimal(0);
         cuotaDesem= new BigDecimal(0);
         redInem= new BigDecimal(0);
         liqOtras= new BigDecimal(0);
         baseMora= new BigDecimal(0);
         prcMora= new BigDecimal(0);
         cuotaMora= new BigDecimal(0);
         importeTc1= new BigDecimal(0);
         baseServcom= new BigDecimal(0);
         prcServcom= new BigDecimal(0);
         cuotaServcom= new BigDecimal(0);
         baseDedcol= new BigDecimal(0);
         prcDedcol= new BigDecimal(0);
         cuotaDedcol= new BigDecimal(0);
         baseOtrcon= new BigDecimal(0);         
         prcOtrcon= new BigDecimal(0);
         cuotaOtrcon= new BigDecimal(0);
         baseConcomCe= new BigDecimal(0);
         prcConcomCe= new BigDecimal(0);
         cuotaConcomCe= new BigDecimal(0);
         baseDesemCe= new BigDecimal(0);
         prcDesemCe= new BigDecimal(0);
         cuotaDesemCe= new BigDecimal(0);
         baseDesem= new BigDecimal(0);
         impcomp= new BigDecimal(0);
         imppres= new BigDecimal(0);        
         impdist= new BigDecimal(0);
 	}
     
     
   //@DataDefinition(label="Codigo de TC1")
    @Id     
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
    @Column(name="codccc", nullable=false, length=1)
    public Tipccc getCodccc() {
        return this.codccc;
    }
    
    public void setCodccc(Tipccc codccc) {
        this.codccc = codccc;
    }
    
	//@DataDefinition(label="Numero Trabajadores")
    @Column(name="numtra", length=2)
    public Integer getNumtra() {
        return this.numtra;
    }
    
    public void setNumtra(Integer numtra) {
        this.numtra = numtra;
    }
    
	//@DataDefinition(label="Mes Desde")
    @Column(name="desde_mes", length=2)
    public Integer getDesdeMes() {
        return this.desdeMes;
    }
    
    public void setDesdeMes(Integer desdeMes) {
        this.desdeMes = desdeMes;
    }
    
	//@DataDefinition(label="Anio Desde")
    @Column(name="desde_anio", length=2)
    public Integer getDesdeAnio() {
        return this.desdeAnio;
    }
    
    public void setDesdeAnio(Integer desdeAnio) {
        this.desdeAnio = desdeAnio;
    }
    
	//@DataDefinition(label="Mes Hasta")
    @Column(name="hasta_mes", length=2)
    public Integer getHastaMes() {
        return this.hastaMes;
    }
    
    public void setHastaMes(Integer hastaMes) {
        this.hastaMes = hastaMes;
    }
    
	//@DataDefinition(label="Anio Hasta")
    @Column(name="hasta_anio", length=2)
    public Integer getHastaAnio() {
        return this.hastaAnio;
    }
    
    public void setHastaAnio(Integer hastaAnio) {
        this.hastaAnio = hastaAnio;
    }
    
	//@DataDefinition(label="Mutua Patronal",descriptionColumn=true)
    @Column(name="mutuaccc", length=3)
    public String getMutuaccc() {
        return this.mutuaccc;
    }
    
    public void setMutuaccc(String mutuaccc) {
        this.mutuaccc = mutuaccc;
    }
    
	//@DataDefinition(label="Base Contingencias Comunes")
    @Column(name="base_concom", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseConcom() {
        return this.baseConcom;
    }
    
    public void setBaseConcom(BigDecimal baseConcom) {
        this.baseConcom = baseConcom;
    }
    
	//@DataDefinition(label="% Contingencias Comunes")
    @Column(name="prc_concom", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcConcom() {
        return this.prcConcom;
    }
    
    public void setPrcConcom(BigDecimal prcConcom) {
        this.prcConcom = prcConcom;
    }
    
	//@DataDefinition(label="Cuota Contingencias Comunes")
    @Column(name="cuota_concom", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaConcom() {
        return this.cuotaConcom;
    }
    
    public void setCuotaConcom(BigDecimal cuotaConcom) {
        this.cuotaConcom = cuotaConcom;
    }
    
	//@DataDefinition(label="Base Horas Extras No Estruct.")
    @Column(name="base_hexno", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseHexno() {
        return this.baseHexno;
    }
    
    public void setBaseHexno(BigDecimal baseHexno) {
        this.baseHexno = baseHexno;
    }
    
	//@DataDefinition(label="% Horas Extras No Estruct.")
    @Column(name="prc_hexno", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcHexno() {
        return this.prcHexno;
    }
    
    public void setPrcHexno(BigDecimal prcHexno) {
        this.prcHexno = prcHexno;
    }
    
	//@DataDefinition(label="Cuota Horas Extras No Estruct.")
    @Column(name="cuota_hexno", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaHexno() {
        return this.cuotaHexno;
    }
    
    public void setCuotaHexno(BigDecimal cuotaHexno) {
        this.cuotaHexno = cuotaHexno;
    }
    
	//@DataDefinition(label="Base Horas Extras Estruct.")
    @Column(name="base_hexest", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseHexest() {
        return this.baseHexest;
    }
    
    public void setBaseHexest(BigDecimal baseHexest) {
        this.baseHexest = baseHexest;
    }
    
	//@DataDefinition(label="% Horas Extras Estruct.")
    @Column(name="prc_hexest", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcHexest() {
        return this.prcHexest;
    }
    
    public void setPrcHexest(BigDecimal prcHexest) {
        this.prcHexest = prcHexest;
    }
    
	//@DataDefinition(label="Cuota Horas Extras Estruct.")
    @Column(name="cuota_hexest", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaHexest() {
        return this.cuotaHexest;
    }
    
    public void setCuotaHexest(BigDecimal cuotaHexest) {
        this.cuotaHexest = cuotaHexest;
    }
    
	//@DataDefinition(label="Reducciones IT")
    @Column(name="base_redit", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseRedit() {
        return this.baseRedit;
    }
    
    public void setBaseRedit(BigDecimal baseRedit) {
        this.baseRedit = baseRedit;
    }
    
	//@DataDefinition(label="Reducciones Contingencias Comunes")
    @Column(name="base_redcc", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseRedcc() {
        return this.baseRedcc;
    }
    
    public void setBaseRedcc(BigDecimal baseRedcc) {
        this.baseRedcc = baseRedcc;
    }
    
	//@DataDefinition(label="Suma Bases Reducciones")
    @Column(name="base_reducc", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseReducc() {
        return this.baseReducc;
    }
    
    public void setBaseReducc(BigDecimal baseReducc) {
        this.baseReducc = baseReducc;
    }
    
	//@DataDefinition(label="Liquido Cotizaciones Generales")
    @Column(name="liq_cotgen", nullable=false, scale=2, precision=11)
    public BigDecimal getLiqCotgen() {
        return this.liqCotgen;
    }
    
    public void setLiqCotgen(BigDecimal liqCotgen) {
        this.liqCotgen = liqCotgen;
    }
    
	//@DataDefinition(label="Base Accidentes Trabajo")
    @Column(name="base_acctra", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseAcctra() {
        return this.baseAcctra;
    }
    
    public void setBaseAcctra(BigDecimal baseAcctra) {
        this.baseAcctra = baseAcctra;
    }
    
	//@DataDefinition(label="Suma Cuotas IT")
    @Column(name="cuotas_it", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotasIt() {
        return this.cuotasIt;
    }
    
    public void setCuotasIt(BigDecimal cuotasIt) {
        this.cuotasIt = cuotasIt;
    }
    
	//@DataDefinition(label="Suma Cuotas IMS")
    @Column(name="cuotas_ims", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotasIms() {
        return this.cuotasIms;
    }
    
    public void setCuotasIms(BigDecimal cuotasIms) {
        this.cuotasIms = cuotasIms;
    }
    
	//@DataDefinition(label="Suma cuotas IT e IMS")
    @Column(name="cuotas_acc", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotasAcc() {
        return this.cuotasAcc;
    }
    
    public void setCuotasAcc(BigDecimal cuotasAcc) {
        this.cuotasAcc = cuotasAcc;
    }
    
	//@DataDefinition(label="Compensacion IT, Acc.Tra. Enf.Prof.")
    @Column(name="comp_it", nullable=false, scale=2, precision=11)
    public BigDecimal getCompIt() {
        return this.compIt;
    }
    
    public void setCompIt(BigDecimal compIt) {
        this.compIt = compIt;
    }
    
	//@DataDefinition(label="Liquido Acc. Trabajo")
    @Column(name="liq_acc", nullable=false, scale=2, precision=11)
    public BigDecimal getLiqAcc() {
        return this.liqAcc;
    }
    
    public void setLiqAcc(BigDecimal liqAcc) {
        this.liqAcc = liqAcc;
    }
    
	//@DataDefinition(label="% Desempleo")
    @Column(name="prc_desem", scale=2, precision=5)
    public BigDecimal getPrcDesem() {
        return this.prcDesem;
    }
    
    public void setPrcDesem(BigDecimal prcDesem) {
        this.prcDesem = prcDesem;
    }
    
	//@DataDefinition(label="Cuota Desempleo")
    @Column(name="cuota_desem", scale=2, precision=11)
    public BigDecimal getCuotaDesem() {
        return this.cuotaDesem;
    }
    
    public void setCuotaDesem(BigDecimal cuotaDesem) {
        this.cuotaDesem = cuotaDesem;
    }
    
	//@DataDefinition(label="Bonif. Reducc. INEM")
    @Column(name="red_inem", nullable=false, scale=2, precision=11)
    public BigDecimal getRedInem() {
        return this.redInem;
    }
    
    public void setRedInem(BigDecimal redInem) {
        this.redInem = redInem;
    }
    
	//@DataDefinition(label="Liquido Otras Cotizaciones")
    @Column(name="liq_otras", nullable=false, scale=2, precision=11)
    public BigDecimal getLiqOtras() {
        return this.liqOtras;
    }
    
    public void setLiqOtras(BigDecimal liqOtras) {
        this.liqOtras = liqOtras;
    }
    
	//@DataDefinition(label="Base Recargo de Mora")
    @Column(name="base_mora", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseMora() {
        return this.baseMora;
    }
    
    public void setBaseMora(BigDecimal baseMora) {
        this.baseMora = baseMora;
    }
    
	//@DataDefinition(label="% recargo de mora")
    @Column(name="prc_mora", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcMora() {
        return this.prcMora;
    }
    
    public void setPrcMora(BigDecimal prcMora) {
        this.prcMora = prcMora;
    }
    
	//@DataDefinition(label="Cuota Recargo de mora")
    @Column(name="cuota_mora", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaMora() {
        return this.cuotaMora;
    }
    
    public void setCuotaMora(BigDecimal cuotaMora) {
        this.cuotaMora = cuotaMora;
    }
    
	//@DataDefinition(label="Resultado TC1")
    @Column(name="importe_tc1", nullable=false, scale=2, precision=11)
    public BigDecimal getImporteTc1() {
        return this.importeTc1;
    }
    
    public void setImporteTc1(BigDecimal importeTc1) {
        this.importeTc1 = importeTc1;
    }
    
	//@DataDefinition(label="Base Aportacion Servicios Comunes")
    @Column(name="base_servcom", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseServcom() {
        return this.baseServcom;
    }
    
    public void setBaseServcom(BigDecimal baseServcom) {
        this.baseServcom = baseServcom;
    }
    
	//@DataDefinition(label="Porcentaje Aportacion Servicios Comunes")
    @Column(name="prc_servcom", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcServcom() {
        return this.prcServcom;
    }
    
    public void setPrcServcom(BigDecimal prcServcom) {
        this.prcServcom = prcServcom;
    }
    
	//@DataDefinition(label="Cuota Aportacion Servicios Comunes")
    @Column(name="cuota_servcom", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaServcom() {
        return this.cuotaServcom;
    }
    
    public void setCuotaServcom(BigDecimal cuotaServcom) {
        this.cuotaServcom = cuotaServcom;
    }
    
	//@DataDefinition(label="Base Deducciones Colaboracion Voluntaria")
    @Column(name="base_dedcol", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseDedcol() {
        return this.baseDedcol;
    }
    
    public void setBaseDedcol(BigDecimal baseDedcol) {
        this.baseDedcol = baseDedcol;
    }
    
	//@DataDefinition(label="Porcentaje Deducciones Colaboracion Voluntaria")
    @Column(name="prc_dedcol", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcDedcol() {
        return this.prcDedcol;
    }
    
    public void setPrcDedcol(BigDecimal prcDedcol) {
        this.prcDedcol = prcDedcol;
    }
    
	//@DataDefinition(label="Cuota Deducciones Colaboracion Voluntaria")
    @Column(name="cuota_dedcol", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaDedcol() {
        return this.cuotaDedcol;
    }
    
    public void setCuotaDedcol(BigDecimal cuotaDedcol) {
        this.cuotaDedcol = cuotaDedcol;
    }
    
	//@DataDefinition(label="Numero Empresa Persona")
    @Column(name="numero", length=4)
    public Integer getNumero() {
        return this.numero;
    }
    
    public void setNumero(Integer numero) {
        this.numero = numero;
    }
   
    
	//@DataDefinition(label="Codigo Persona")
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codper", nullable=false)
    public Persona getCodper() {
        return this.codper;
    }
    
    public void setCodper(Persona codper) {
        this.codper = codper;
    }
    
	//@DataDefinition(label="Dias Alta")
    @Column(name="dias", length=2)
    public Integer getDias() {
        return this.dias;
    }
    
    public void setDias(Integer dias) {
        this.dias = dias;
    }
    
	//@DataDefinition(label="Grupo Tarifa")
    @Column(name="codbas", length=2)
    public String getCodbas() {
        return this.codbas;
    }
    
    public void setCodbas(String codbas) {
        this.codbas = codbas;
    }
    
	//@DataDefinition(label="Contrato TC2")
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="codtc2", nullable=false)
    public Tc2 getCodtc2() {
        return this.codtc2;
    }
    
    public void setCodtc2(Tc2 codtc2) {
        this.codtc2 = codtc2;
    }
    
	//@DataDefinition(label="Epigrafe")
    @Column(name="codepi", length=3)
    public String getCodepi() {
        return this.codepi;
    }
    
    public void setCodepi(String codepi) {
        this.codepi = codepi;
    }
    
	//@DataDefinition(label="Numero de Horas")
    @Column(name="horas", length=2)
    public Integer getHoras() {
        return this.horas;
    }
    
    public void setHoras(Integer horas) {
        this.horas = horas;
    }
    
	//@DataDefinition(label="Dias IT")
    @Column(name="diasit", length=2)
    public Integer getDiasit() {
        return this.diasit;
    }
    
    public void setDiasit(Integer diasit) {
        this.diasit = diasit;
    }
    
	//@DataDefinition(label="Dias Maternidad")
    @Column(name="diasmat", length=2)
    public Integer getDiasmat() {
        return this.diasmat;
    }
    
    public void setDiasmat(Integer diasmat) {
        this.diasmat = diasmat;
    }
    
	//@DataDefinition(label="Base Accidentes")
    @Column(name="base_acc", scale=2, precision=11)
    public BigDecimal getBaseAcc() {
        return this.baseAcc;
    }
    
    public void setBaseAcc(BigDecimal baseAcc) {
        this.baseAcc = baseAcc;
    }
    @Temporal(TemporalType.DATE)
	//@DataDefinition(label="Fecha Concesion Bonif.")
    @Column(name="fecha")
    public Date getFecha() {
        return this.fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
	//@DataDefinition(label="Situaciones Especiales")
    @Column(name="sitesp", length=6)
    public String getSitesp() {
        return this.sitesp;
    }
    
    public void setSitesp(String sitesp) {
        this.sitesp = sitesp;
    }
    
	//@DataDefinition(label="Nombre")
    @Column(name="nombre", length=25)
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
	//@DataDefinition(label="Apellidos")
    @Column(name="apellidos", length=25)
    public String getApellidos() {
        return this.apellidos;
    }
    
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    
	//@DataDefinition(label="Indicador de impresion de tc2 1")
    @Column(name="mostrar", length=1)
    public String getMostrar() {
        return this.mostrar;
    }
    
    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }
    
	//@DataDefinition(label="Clave Reducción")
    @Column(name="cdgred", length=1)
    public String getCdgred() {
        return this.cdgred;
    }
    
    public void setCdgred(String cdgred) {
        this.cdgred = cdgred;
    }
    
	//@DataDefinition(label="Desglose de cuotas")
    @Column(name="desglose", length=1)
    public String getDesglose() {
        return this.desglose;
    }
    
    public void setDesglose(String desglose) {
        this.desglose = desglose;
    }
    
	//@DataDefinition(label="Comision Mutua")
	@Type(type="siNoType" )
    @Column(name="comision", length=1)
    public Boolean getComision() {
        return this.comision;
    }
    
    public void setComision(Boolean comision) {
        this.comision = comision;
    }
    
	//@DataDefinition(label="Tipo de TC1")
    @Column(name="tipotc1", length=1)
    public String getTipotc1() {
        return this.tipotc1;
    }
    
    public void setTipotc1(String tipotc1) {
        this.tipotc1 = tipotc1;
    }
    
	//@DataDefinition(label="Codigo de TC2")
    @Column(name="tc2", length=4)
    public Integer getTc2() {
        return this.tc2;
    }
    
    public void setTc2(Integer tc2) {
        this.tc2 = tc2;
    }
    
	//@DataDefinition(label="Divisa")
    @Column(name="divisa", length=3)
    public String getDivisa() {
        return this.divisa;
    }
    
    public void setDivisa(String divisa) {
        this.divisa = divisa;
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
    
	//@DataDefinition(label="Código Otros Conceptos")
    @Column(name="cdg_otrcon", length=1)
    public String getCdgOtrcon() {
        return this.cdgOtrcon;
    }
    
    public void setCdgOtrcon(String cdgOtrcon) {
        this.cdgOtrcon = cdgOtrcon;
    }
    
	//@DataDefinition(label="Base Otros Conceptos")
    @Column(name="base_otrcon", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseOtrcon() {
        return this.baseOtrcon;
    }
    
    public void setBaseOtrcon(BigDecimal baseOtrcon) {
        this.baseOtrcon = baseOtrcon;
    }
    
	//@DataDefinition(label="% Otros Conceptos")
    @Column(name="prc_otrcon", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcOtrcon() {
        return this.prcOtrcon;
    }
    
    public void setPrcOtrcon(BigDecimal prcOtrcon) {
        this.prcOtrcon = prcOtrcon;
    }
    
	//@DataDefinition(label="Cuota Otros Conceptos")
    @Column(name="cuota_otrcon", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaOtrcon() {
        return this.cuotaOtrcon;
    }
    
    public void setCuotaOtrcon(BigDecimal cuotaOtrcon) {
        this.cuotaOtrcon = cuotaOtrcon;
    }
    
	//@DataDefinition(label="Base Contingencias Comunes Cotización Empresarial")
    @Column(name="base_concom_ce", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseConcomCe() {
        return this.baseConcomCe;
    }
    
    public void setBaseConcomCe(BigDecimal baseConcomCe) {
        this.baseConcomCe = baseConcomCe;
    }
    
	//@DataDefinition(label="% Contingencias Comunes Cotización Empresarial")
    @Column(name="prc_concom_ce", nullable=false, scale=2, precision=5)
    public BigDecimal getPrcConcomCe() {
        return this.prcConcomCe;
    }
    
    public void setPrcConcomCe(BigDecimal prcConcomCe) {
        this.prcConcomCe = prcConcomCe;
    }
    
	//@DataDefinition(label="Cuota Contingencias Comunes Cotización Empresarial")
    @Column(name="cuota_concom_ce", nullable=false, scale=2, precision=11)
    public BigDecimal getCuotaConcomCe() {
        return this.cuotaConcomCe;
    }
    
    public void setCuotaConcomCe(BigDecimal cuotaConcomCe) {
        this.cuotaConcomCe = cuotaConcomCe;
    }
    
	//@DataDefinition(label="Base Desempleo Cotización Empresarial")
    @Column(name="base_desem_ce", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseDesemCe() {
        return this.baseDesemCe;
    }
    
    public void setBaseDesemCe(BigDecimal baseDesemCe) {
        this.baseDesemCe = baseDesemCe;
    }
    
	//@DataDefinition(label="% Desempleo Cotización Empresarial")
    @Column(name="prc_desem_ce", scale=2, precision=5)
    public BigDecimal getPrcDesemCe() {
        return this.prcDesemCe;
    }
    
    public void setPrcDesemCe(BigDecimal prcDesemCe) {
        this.prcDesemCe = prcDesemCe;
    }
    
	//@DataDefinition(label="Cuota Desempleo Cotización Empresarial")
    @Column(name="cuota_desem_ce", scale=2, precision=11)
    public BigDecimal getCuotaDesemCe() {
        return this.cuotaDesemCe;
    }
    
    public void setCuotaDesemCe(BigDecimal cuotaDesemCe) {
        this.cuotaDesemCe = cuotaDesemCe;
    }
    
	//@DataDefinition(label="Base Desempleo")
    @Column(name="base_desem", nullable=false, scale=2, precision=11)
    public BigDecimal getBaseDesem() {
        return this.baseDesem;
    }
    
    public void setBaseDesem(BigDecimal baseDesem) {
        this.baseDesem = baseDesem;
    }
    
	//@DataDefinition(label="Horas Complementarias")
    @Column(name="horcomp", length=2)
    public Integer getHorcomp() {
        return this.horcomp;
    }
    
    public void setHorcomp(Integer horcomp) {
        this.horcomp = horcomp;
    }
    
	//@DataDefinition(label="Importe Horas Complementarias")
    @Column(name="impcomp", scale=2, precision=11)
    public BigDecimal getImpcomp() {
        return this.impcomp;
    }
    
    public void setImpcomp(BigDecimal impcomp) {
        this.impcomp = impcomp;
    }
    
	//@DataDefinition(label="Horas Presenciales")
    @Column(name="horpres", length=2)
    public Integer getHorpres() {
        return this.horpres;
    }
    
    public void setHorpres(Integer horpres) {
        this.horpres = horpres;
    }
    
	//@DataDefinition(label="Importe Horas Presenciales")
    @Column(name="imppres", scale=2, precision=11)
    public BigDecimal getImppres() {
        return this.imppres;
    }
    
    public void setImppres(BigDecimal imppres) {
        this.imppres = imppres;
    }
    
	//@DataDefinition(label="Horas Distancia")
    @Column(name="hordist", length=2)
    public Integer getHordist() {
        return this.hordist;
    }
    
    public void setHordist(Integer hordist) {
        this.hordist = hordist;
    }
    
	//@DataDefinition(label="Importe Horas Distancia")
    @Column(name="impdist", scale=2, precision=11)
    public BigDecimal getImpdist() {
        return this.impdist;
    }
    
    public void setImpdist(BigDecimal impdist) {
        this.impdist = impdist;
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


