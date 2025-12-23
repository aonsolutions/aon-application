//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.18 a las 11:51:10 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 					Nodo principal de la declaración, es común a todas
 * 					las declaraciones.
 * 				
 * 
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AUX" type="{}T_AUXILIAR" minOccurs="0"/&gt;
 *         &lt;element name="IDE" type="{}T_IDENT_IGIC"/&gt;
 *         &lt;element name="EST" type="{}T_DATOS_ESTADISTICOS_425" minOccurs="0"/&gt;
 *         &lt;element name="REP" type="{}T_REPRESENTANTE" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="REG" type="{}T_REGIMEN_GENERAL_425" minOccurs="0"/&gt;
 *         &lt;element name="RES" type="{}T_REGIMEN_SIMPLIFICADO_425" minOccurs="0"/&gt;
 *         &lt;element name="LIQ" type="{}T_LIQUIDACION" minOccurs="0"/&gt;
 *         &lt;element name="AUT" type="{}T_AUTOLIQUIDACION_425" minOccurs="0"/&gt;
 *         &lt;element name="OPE" type="{}T_OPERACIONES_EJERCICIO" minOccurs="0"/&gt;
 *         &lt;element name="RCC" type="{}T_OPERACIONES_RECC" minOccurs="0"/&gt;
 *         &lt;element name="RPE" type="{}T_OPERACIONES_REPEP" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="NDE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MOD" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ANY" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="VER" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="SIS"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="1"/&gt;
 *             &lt;enumeration value="2"/&gt;
 *             &lt;enumeration value="3"/&gt;
 *             &lt;enumeration value="9"/&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="PER" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *             &lt;enumeration value="0A"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="FIM" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="8"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SUS" type="{}CHECKType" /&gt;
 *       &lt;attribute name="SUA" type="{}CHECKType" /&gt;
 *       &lt;attribute name="NJA" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="LUG" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "aux",
    "ide",
    "est",
    "rep",
    "reg",
    "res",
    "liq",
    "aut",
    "ope",
    "rcc",
    "rpe"
})
@XmlRootElement(name = "DEC")
public class DEC {

    @XmlElement(name = "AUX")
    protected TAUXILIAR aux;
    @XmlElement(name = "IDE", required = true)
    protected TIDENTIGIC ide;
    @XmlElement(name = "EST")
    protected TDATOSESTADISTICOS425 est;
    @XmlElement(name = "REP")
    protected List<TREPRESENTANTE> rep;
    @XmlElement(name = "REG")
    protected TREGIMENGENERAL425 reg;
    @XmlElement(name = "RES")
    protected TREGIMENSIMPLIFICADO425 res;
    @XmlElement(name = "LIQ")
    protected TLIQUIDACION liq;
    @XmlElement(name = "AUT")
    protected TAUTOLIQUIDACION425 aut;
    @XmlElement(name = "OPE")
    protected TOPERACIONESEJERCICIO ope;
    @XmlElement(name = "RCC")
    protected TOPERACIONESRECC rcc;
    @XmlElement(name = "RPE")
    protected TOPERACIONESREPEP rpe;
    @XmlAttribute(name = "NDE")
    protected String nde;
    @XmlAttribute(name = "MOD", required = true)
    protected String mod;
    @XmlAttribute(name = "ANY", required = true)
    protected String any;
    @XmlAttribute(name = "VER")
    protected String ver;
    @XmlAttribute(name = "SIS")
    protected String sis;
    @XmlAttribute(name = "PER", required = true)
    protected String per;
    @XmlAttribute(name = "FIM", required = true)
    protected String fim;
    @XmlAttribute(name = "SUS")
    protected String sus;
    @XmlAttribute(name = "SUA")
    protected String sua;
    @XmlAttribute(name = "NJA")
    protected String nja;
    @XmlAttribute(name = "LUG")
    protected String lug;

    /**
     * Obtiene el valor de la propiedad aux.
     * 
     * @return
     *     possible object is
     *     {@link TAUXILIAR }
     *     
     */
    public TAUXILIAR getAUX() {
        return aux;
    }

    /**
     * Define el valor de la propiedad aux.
     * 
     * @param value
     *     allowed object is
     *     {@link TAUXILIAR }
     *     
     */
    public void setAUX(TAUXILIAR value) {
        this.aux = value;
    }

    /**
     * Obtiene el valor de la propiedad ide.
     * 
     * @return
     *     possible object is
     *     {@link TIDENTIGIC }
     *     
     */
    public TIDENTIGIC getIDE() {
        return ide;
    }

    /**
     * Define el valor de la propiedad ide.
     * 
     * @param value
     *     allowed object is
     *     {@link TIDENTIGIC }
     *     
     */
    public void setIDE(TIDENTIGIC value) {
        this.ide = value;
    }

    /**
     * Obtiene el valor de la propiedad est.
     * 
     * @return
     *     possible object is
     *     {@link TDATOSESTADISTICOS425 }
     *     
     */
    public TDATOSESTADISTICOS425 getEST() {
        return est;
    }

    /**
     * Define el valor de la propiedad est.
     * 
     * @param value
     *     allowed object is
     *     {@link TDATOSESTADISTICOS425 }
     *     
     */
    public void setEST(TDATOSESTADISTICOS425 value) {
        this.est = value;
    }

    /**
     * Gets the value of the rep property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rep property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getREP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TREPRESENTANTE }
     * 
     * 
     */
    public List<TREPRESENTANTE> getREP() {
        if (rep == null) {
            rep = new ArrayList<TREPRESENTANTE>();
        }
        return this.rep;
    }

    /**
     * Obtiene el valor de la propiedad reg.
     * 
     * @return
     *     possible object is
     *     {@link TREGIMENGENERAL425 }
     *     
     */
    public TREGIMENGENERAL425 getREG() {
        return reg;
    }

    /**
     * Define el valor de la propiedad reg.
     * 
     * @param value
     *     allowed object is
     *     {@link TREGIMENGENERAL425 }
     *     
     */
    public void setREG(TREGIMENGENERAL425 value) {
        this.reg = value;
    }

    /**
     * Obtiene el valor de la propiedad res.
     * 
     * @return
     *     possible object is
     *     {@link TREGIMENSIMPLIFICADO425 }
     *     
     */
    public TREGIMENSIMPLIFICADO425 getRES() {
        return res;
    }

    /**
     * Define el valor de la propiedad res.
     * 
     * @param value
     *     allowed object is
     *     {@link TREGIMENSIMPLIFICADO425 }
     *     
     */
    public void setRES(TREGIMENSIMPLIFICADO425 value) {
        this.res = value;
    }

    /**
     * Obtiene el valor de la propiedad liq.
     * 
     * @return
     *     possible object is
     *     {@link TLIQUIDACION }
     *     
     */
    public TLIQUIDACION getLIQ() {
        return liq;
    }

    /**
     * Define el valor de la propiedad liq.
     * 
     * @param value
     *     allowed object is
     *     {@link TLIQUIDACION }
     *     
     */
    public void setLIQ(TLIQUIDACION value) {
        this.liq = value;
    }

    /**
     * Obtiene el valor de la propiedad aut.
     * 
     * @return
     *     possible object is
     *     {@link TAUTOLIQUIDACION425 }
     *     
     */
    public TAUTOLIQUIDACION425 getAUT() {
        return aut;
    }

    /**
     * Define el valor de la propiedad aut.
     * 
     * @param value
     *     allowed object is
     *     {@link TAUTOLIQUIDACION425 }
     *     
     */
    public void setAUT(TAUTOLIQUIDACION425 value) {
        this.aut = value;
    }

    /**
     * Obtiene el valor de la propiedad ope.
     * 
     * @return
     *     possible object is
     *     {@link TOPERACIONESEJERCICIO }
     *     
     */
    public TOPERACIONESEJERCICIO getOPE() {
        return ope;
    }

    /**
     * Define el valor de la propiedad ope.
     * 
     * @param value
     *     allowed object is
     *     {@link TOPERACIONESEJERCICIO }
     *     
     */
    public void setOPE(TOPERACIONESEJERCICIO value) {
        this.ope = value;
    }

    /**
     * Obtiene el valor de la propiedad rcc.
     * 
     * @return
     *     possible object is
     *     {@link TOPERACIONESRECC }
     *     
     */
    public TOPERACIONESRECC getRCC() {
        return rcc;
    }

    /**
     * Define el valor de la propiedad rcc.
     * 
     * @param value
     *     allowed object is
     *     {@link TOPERACIONESRECC }
     *     
     */
    public void setRCC(TOPERACIONESRECC value) {
        this.rcc = value;
    }

    /**
     * Obtiene el valor de la propiedad rpe.
     * 
     * @return
     *     possible object is
     *     {@link TOPERACIONESREPEP }
     *     
     */
    public TOPERACIONESREPEP getRPE() {
        return rpe;
    }

    /**
     * Define el valor de la propiedad rpe.
     * 
     * @param value
     *     allowed object is
     *     {@link TOPERACIONESREPEP }
     *     
     */
    public void setRPE(TOPERACIONESREPEP value) {
        this.rpe = value;
    }

    /**
     * Obtiene el valor de la propiedad nde.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNDE() {
        return nde;
    }

    /**
     * Define el valor de la propiedad nde.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNDE(String value) {
        this.nde = value;
    }

    /**
     * Obtiene el valor de la propiedad mod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD() {
        return mod;
    }

    /**
     * Define el valor de la propiedad mod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD(String value) {
        this.mod = value;
    }

    /**
     * Obtiene el valor de la propiedad any.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getANY() {
        return any;
    }

    /**
     * Define el valor de la propiedad any.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setANY(String value) {
        this.any = value;
    }

    /**
     * Obtiene el valor de la propiedad ver.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVER() {
        return ver;
    }

    /**
     * Define el valor de la propiedad ver.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVER(String value) {
        this.ver = value;
    }

    /**
     * Obtiene el valor de la propiedad sis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIS() {
        return sis;
    }

    /**
     * Define el valor de la propiedad sis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIS(String value) {
        this.sis = value;
    }

    /**
     * Obtiene el valor de la propiedad per.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPER() {
        return per;
    }

    /**
     * Define el valor de la propiedad per.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPER(String value) {
        this.per = value;
    }

    /**
     * Obtiene el valor de la propiedad fim.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIM() {
        return fim;
    }

    /**
     * Define el valor de la propiedad fim.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIM(String value) {
        this.fim = value;
    }

    /**
     * Obtiene el valor de la propiedad sus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUS() {
        return sus;
    }

    /**
     * Define el valor de la propiedad sus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUS(String value) {
        this.sus = value;
    }

    /**
     * Obtiene el valor de la propiedad sua.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUA() {
        return sua;
    }

    /**
     * Define el valor de la propiedad sua.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUA(String value) {
        this.sua = value;
    }

    /**
     * Obtiene el valor de la propiedad nja.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNJA() {
        return nja;
    }

    /**
     * Define el valor de la propiedad nja.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNJA(String value) {
        this.nja = value;
    }

    /**
     * Obtiene el valor de la propiedad lug.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLUG() {
        return lug;
    }

    /**
     * Define el valor de la propiedad lug.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLUG(String value) {
        this.lug = value;
    }

}
