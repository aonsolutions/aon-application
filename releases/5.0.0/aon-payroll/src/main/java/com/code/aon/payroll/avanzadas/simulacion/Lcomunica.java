package com.code.aon.payroll.avanzadas.simulacion;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Ascdes;
import com.code.aon.payroll.enumeration.Minusvalia;



@Entity
@Table(name="lcomunica")
public class Lcomunica  implements ITransferObject {

	
     private LcomunicaId id;
     private Integer anionac;
     private Minusvalia xminus;
     private Ascdes desAsc;
     private Boolean descenEnt;
     private Integer conviv;
     private Costes costes;

    @EmbeddedId 
    @AttributeOverrides( {
        @AttributeOverride(name="cdg", column=@Column(name="cdg", nullable=false, length=4) ), 
        @AttributeOverride(name="numero", column=@Column(name="numero", nullable=false, length=4) ), 
        @AttributeOverride(name="orden", column=@Column(name="orden", nullable=false, length=2) ) } )
    public LcomunicaId getId() {
        return this.id;
    }
    
    public void setId(LcomunicaId id) {
        this.id = id;
    }
    
	//@DataDefinition(label="Anio de Nacimiento Hijo")
    @Column(name="anionac", nullable=false, length=2)
    public Integer getAnionac() {
        return this.anionac;
    }
    
    public void setAnionac(Integer anionac) {
        this.anionac = anionac;
    }
    
	//@DataDefinition(label="Minusvalia Hijo")
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Minusvalia")} )
    @Column(name="xminus", nullable=false, length=1)
    public Minusvalia getXminus() {
        return this.xminus;
    }
    
    public void setXminus(Minusvalia xminus) {
        this.xminus = xminus;
    }
    
	//@DataDefinition(label="Descendiente o ascendiente")
    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Ascdes")} )
    @Column(name="des_asc", nullable=false, length=1)
    public Ascdes getDesAsc() {
        return this.desAsc;
    }
    
    public void setDesAsc(Ascdes desAsc) {
        this.desAsc = desAsc;
    }
    
	//@DataDefinition(label="Descendiente por entero")
    @Type(type="siNoType" )
    @Column(name="descen_ent", nullable=false, length=1)
    public Boolean getDescenEnt() {
        return this.descenEnt;
    }
    
    public void setDescenEnt(Boolean descenEnt) {
        this.descenEnt = descenEnt;
    }
    
	//@DataDefinition(label="Convivencia")
    @Column(name="conviv", length=2)
    public Integer getConviv() {
        return this.conviv;
    }
    
    public void setConviv(Integer conviv) {
        this.conviv = conviv;
    }
    
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumns( { 
        @JoinColumn(name="cdg", referencedColumnName="cdg", insertable=false, updatable=false), 
        @JoinColumn(name="numero", referencedColumnName="numero", insertable=false, updatable=false) } )
    public Costes getCostes() {
        return this.costes;
    }
    
    public void setCostes(Costes costes) {
        this.costes = costes;
    }


}


