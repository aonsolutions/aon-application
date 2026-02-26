//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Elemento correspondiente al formulario de
 * 				solicitud de aplazamiento fraccionamiento.
 * 				Pago fraccionado en los pa.
 * 			
 * 
 * <p>Clase Java para T_FRACCIONAMIENTO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_FRACCIONAMIENTO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="INTERESADO" type="{}T_PERSONA"/&gt;
 *         &lt;element name="REPRESENTANTE" type="{}T_PERSONA" minOccurs="0"/&gt;
 *         &lt;element name="NOTIFICADO" type="{}T_PERSONA"/&gt;
 *         &lt;element name="CAUSAS" type="{}T_CAUSAS"/&gt;
 *         &lt;element name="SUPUESTOS" type="{}T_SUPUESTOS"/&gt;
 *         &lt;element name="DOCUMENTACION" type="{}T_DOCUMENTACION"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="VER" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="EXP" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="JUS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IMP" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="ING" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="ENT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IBAN" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="24"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="PRO" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_FRACCIONAMIENTO", propOrder = {
    "interesado",
    "representante",
    "notificado",
    "causas",
    "supuestos",
    "documentacion"
})
public class TFRACCIONAMIENTO {

    @XmlElement(name = "INTERESADO", required = true)
    protected TPERSONA interesado;
    @XmlElement(name = "REPRESENTANTE")
    protected TPERSONA representante;
    @XmlElement(name = "NOTIFICADO", required = true)
    protected TPERSONA notificado;
    @XmlElement(name = "CAUSAS", required = true)
    protected TCAUSAS causas;
    @XmlElement(name = "SUPUESTOS", required = true)
    protected TSUPUESTOS supuestos;
    @XmlElement(name = "DOCUMENTACION", required = true)
    protected TDOCUMENTACION documentacion;
    @XmlAttribute(name = "VER")
    protected String ver;
    @XmlAttribute(name = "EXP", required = true)
    protected String exp;
    @XmlAttribute(name = "JUS")
    protected String jus;
    @XmlAttribute(name = "IMP", required = true)
    protected String imp;
    @XmlAttribute(name = "ING", required = true)
    protected String ing;
    @XmlAttribute(name = "ENT")
    protected String ent;
    @XmlAttribute(name = "IBAN", required = true)
    protected String iban;
    @XmlAttribute(name = "PRO", required = true)
    protected String pro;
    @XmlAttribute(name = "FEC")
    protected String fec;

    /**
     * Obtiene el valor de la propiedad interesado.
     * 
     * @return
     *     possible object is
     *     {@link TPERSONA }
     *     
     */
    public TPERSONA getINTERESADO() {
        return interesado;
    }

    /**
     * Define el valor de la propiedad interesado.
     * 
     * @param value
     *     allowed object is
     *     {@link TPERSONA }
     *     
     */
    public void setINTERESADO(TPERSONA value) {
        this.interesado = value;
    }

    /**
     * Obtiene el valor de la propiedad representante.
     * 
     * @return
     *     possible object is
     *     {@link TPERSONA }
     *     
     */
    public TPERSONA getREPRESENTANTE() {
        return representante;
    }

    /**
     * Define el valor de la propiedad representante.
     * 
     * @param value
     *     allowed object is
     *     {@link TPERSONA }
     *     
     */
    public void setREPRESENTANTE(TPERSONA value) {
        this.representante = value;
    }

    /**
     * Obtiene el valor de la propiedad notificado.
     * 
     * @return
     *     possible object is
     *     {@link TPERSONA }
     *     
     */
    public TPERSONA getNOTIFICADO() {
        return notificado;
    }

    /**
     * Define el valor de la propiedad notificado.
     * 
     * @param value
     *     allowed object is
     *     {@link TPERSONA }
     *     
     */
    public void setNOTIFICADO(TPERSONA value) {
        this.notificado = value;
    }

    /**
     * Obtiene el valor de la propiedad causas.
     * 
     * @return
     *     possible object is
     *     {@link TCAUSAS }
     *     
     */
    public TCAUSAS getCAUSAS() {
        return causas;
    }

    /**
     * Define el valor de la propiedad causas.
     * 
     * @param value
     *     allowed object is
     *     {@link TCAUSAS }
     *     
     */
    public void setCAUSAS(TCAUSAS value) {
        this.causas = value;
    }

    /**
     * Obtiene el valor de la propiedad supuestos.
     * 
     * @return
     *     possible object is
     *     {@link TSUPUESTOS }
     *     
     */
    public TSUPUESTOS getSUPUESTOS() {
        return supuestos;
    }

    /**
     * Define el valor de la propiedad supuestos.
     * 
     * @param value
     *     allowed object is
     *     {@link TSUPUESTOS }
     *     
     */
    public void setSUPUESTOS(TSUPUESTOS value) {
        this.supuestos = value;
    }

    /**
     * Obtiene el valor de la propiedad documentacion.
     * 
     * @return
     *     possible object is
     *     {@link TDOCUMENTACION }
     *     
     */
    public TDOCUMENTACION getDOCUMENTACION() {
        return documentacion;
    }

    /**
     * Define el valor de la propiedad documentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link TDOCUMENTACION }
     *     
     */
    public void setDOCUMENTACION(TDOCUMENTACION value) {
        this.documentacion = value;
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
     * Obtiene el valor de la propiedad exp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEXP() {
        return exp;
    }

    /**
     * Define el valor de la propiedad exp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEXP(String value) {
        this.exp = value;
    }

    /**
     * Obtiene el valor de la propiedad jus.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJUS() {
        return jus;
    }

    /**
     * Define el valor de la propiedad jus.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJUS(String value) {
        this.jus = value;
    }

    /**
     * Obtiene el valor de la propiedad imp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMP() {
        return imp;
    }

    /**
     * Define el valor de la propiedad imp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMP(String value) {
        this.imp = value;
    }

    /**
     * Obtiene el valor de la propiedad ing.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getING() {
        return ing;
    }

    /**
     * Define el valor de la propiedad ing.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setING(String value) {
        this.ing = value;
    }

    /**
     * Obtiene el valor de la propiedad ent.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getENT() {
        return ent;
    }

    /**
     * Define el valor de la propiedad ent.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setENT(String value) {
        this.ent = value;
    }

    /**
     * Obtiene el valor de la propiedad iban.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Define el valor de la propiedad iban.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Obtiene el valor de la propiedad pro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRO() {
        return pro;
    }

    /**
     * Define el valor de la propiedad pro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRO(String value) {
        this.pro = value;
    }

    /**
     * Obtiene el valor de la propiedad fec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEC() {
        return fec;
    }

    /**
     * Define el valor de la propiedad fec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEC(String value) {
        this.fec = value;
    }

}
