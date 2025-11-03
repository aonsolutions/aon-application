//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.10.01 a las 11:47:09 AM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod417_2025;

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
 * 					Nodo principal de la declaración, es común
 * 					a todas	las	declaraciones
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
 *         &lt;element name="IGI_DEV" type="{}T_DEVENGADO" minOccurs="0"/&gt;
 *         &lt;element name="IGI_DED" type="{}T_DEDUCIBLE" minOccurs="0"/&gt;
 *         &lt;element name="LIQ" type="{}T_LIQUIDACION" minOccurs="0"/&gt;
 *         &lt;element name="RES" type="{}RESULTADO_LIQUIDACION"/&gt;
 *         &lt;element name="ADI" type="{}T_INFO_ADICIONAL" minOccurs="0"/&gt;
 *         &lt;element name="RCC" type="{}T_OPERACIONES_RECC" minOccurs="0"/&gt;
 *         &lt;element name="ACT_EST" type="{}OPERACION_DATOS_ESTADISTICOS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="OPE" type="{}T_OPERACIONES_EJERCICIO" minOccurs="0"/&gt;
 *         &lt;element name="ACT_PRO" type="{}OPERACION_DATOS_ESTADISTICOS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ACT_DED" type="{}T_DEDUCIBLE" maxOccurs="unbounded" minOccurs="0"/&gt;
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
 *       &lt;attribute name="PER" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="FIM"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="8"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="COM"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="X"/&gt;
 *             &lt;enumeration value=""/&gt;
 *             &lt;minLength value="0"/&gt;
 *             &lt;maxLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="NJA" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="VER" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    "igidev",
    "igided",
    "liq",
    "res",
    "adi",
    "rcc",
    "actest",
    "ope",
    "actpro",
    "actded"
})
@XmlRootElement(name = "DEC")
public class DEC {

    @XmlElement(name = "AUX")
    protected TAUXILIAR aux;
    @XmlElement(name = "IDE", required = true)
    protected TIDENTIGIC ide;
    @XmlElement(name = "IGI_DEV")
    protected TDEVENGADO igidev;
    @XmlElement(name = "IGI_DED")
    protected TDEDUCIBLE igided;
    @XmlElement(name = "LIQ")
    protected TLIQUIDACION liq;
    @XmlElement(name = "RES", required = true)
    protected RESULTADOLIQUIDACION res;
    @XmlElement(name = "ADI")
    protected TINFOADICIONAL adi;
    @XmlElement(name = "RCC")
    protected TOPERACIONESRECC rcc;
    @XmlElement(name = "ACT_EST")
    protected List<OPERACIONDATOSESTADISTICOS> actest;
    @XmlElement(name = "OPE")
    protected TOPERACIONESEJERCICIO ope;
    @XmlElement(name = "ACT_PRO")
    protected List<OPERACIONDATOSESTADISTICOS> actpro;
    @XmlElement(name = "ACT_DED")
    protected List<TDEDUCIBLE> actded;
    @XmlAttribute(name = "NDE")
    protected String nde;
    @XmlAttribute(name = "MOD", required = true)
    protected String mod;
    @XmlAttribute(name = "ANY", required = true)
    protected String any;
    @XmlAttribute(name = "PER", required = true)
    protected String per;
    @XmlAttribute(name = "FIM")
    protected String fim;
    @XmlAttribute(name = "COM")
    protected String com;
    @XmlAttribute(name = "NJA")
    protected String nja;
    @XmlAttribute(name = "VER")
    protected String ver;

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
     * Obtiene el valor de la propiedad igidev.
     * 
     * @return
     *     possible object is
     *     {@link TDEVENGADO }
     *     
     */
    public TDEVENGADO getIGIDEV() {
        return igidev;
    }

    /**
     * Define el valor de la propiedad igidev.
     * 
     * @param value
     *     allowed object is
     *     {@link TDEVENGADO }
     *     
     */
    public void setIGIDEV(TDEVENGADO value) {
        this.igidev = value;
    }

    /**
     * Obtiene el valor de la propiedad igided.
     * 
     * @return
     *     possible object is
     *     {@link TDEDUCIBLE }
     *     
     */
    public TDEDUCIBLE getIGIDED() {
        return igided;
    }

    /**
     * Define el valor de la propiedad igided.
     * 
     * @param value
     *     allowed object is
     *     {@link TDEDUCIBLE }
     *     
     */
    public void setIGIDED(TDEDUCIBLE value) {
        this.igided = value;
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
     * Obtiene el valor de la propiedad res.
     * 
     * @return
     *     possible object is
     *     {@link RESULTADOLIQUIDACION }
     *     
     */
    public RESULTADOLIQUIDACION getRES() {
        return res;
    }

    /**
     * Define el valor de la propiedad res.
     * 
     * @param value
     *     allowed object is
     *     {@link RESULTADOLIQUIDACION }
     *     
     */
    public void setRES(RESULTADOLIQUIDACION value) {
        this.res = value;
    }

    /**
     * Obtiene el valor de la propiedad adi.
     * 
     * @return
     *     possible object is
     *     {@link TINFOADICIONAL }
     *     
     */
    public TINFOADICIONAL getADI() {
        return adi;
    }

    /**
     * Define el valor de la propiedad adi.
     * 
     * @param value
     *     allowed object is
     *     {@link TINFOADICIONAL }
     *     
     */
    public void setADI(TINFOADICIONAL value) {
        this.adi = value;
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
     * Gets the value of the actest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getACTEST().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OPERACIONDATOSESTADISTICOS }
     * 
     * 
     */
    public List<OPERACIONDATOSESTADISTICOS> getACTEST() {
        if (actest == null) {
            actest = new ArrayList<OPERACIONDATOSESTADISTICOS>();
        }
        return this.actest;
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
     * Gets the value of the actpro property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actpro property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getACTPRO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OPERACIONDATOSESTADISTICOS }
     * 
     * 
     */
    public List<OPERACIONDATOSESTADISTICOS> getACTPRO() {
        if (actpro == null) {
            actpro = new ArrayList<OPERACIONDATOSESTADISTICOS>();
        }
        return this.actpro;
    }

    /**
     * Gets the value of the actded property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actded property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getACTDED().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDEDUCIBLE }
     * 
     * 
     */
    public List<TDEDUCIBLE> getACTDED() {
        if (actded == null) {
            actded = new ArrayList<TDEDUCIBLE>();
        }
        return this.actded;
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
     * Obtiene el valor de la propiedad com.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOM() {
        return com;
    }

    /**
     * Define el valor de la propiedad com.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOM(String value) {
        this.com = value;
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

}
