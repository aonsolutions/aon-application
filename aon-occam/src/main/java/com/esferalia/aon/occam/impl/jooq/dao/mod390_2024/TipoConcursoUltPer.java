//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.25 a las 11:44:35 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_ConcursoUltPer complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_ConcursoUltPer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="ConcursoUltPer_SI"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ConcursoUltPer_NO"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_ConcursoUltPer", propOrder = {
    "concursoUltPerSI",
    "concursoUltPerNO"
})
public class TipoConcursoUltPer {

    @XmlElement(name = "ConcursoUltPer_SI")
    protected TipoConcursoUltPer.ConcursoUltPerSI concursoUltPerSI;
    @XmlElement(name = "ConcursoUltPer_NO")
    protected TipoConcursoUltPer.ConcursoUltPerNO concursoUltPerNO;

    /**
     * Obtiene el valor de la propiedad concursoUltPerSI.
     * 
     * @return
     *     possible object is
     *     {@link TipoConcursoUltPer.ConcursoUltPerSI }
     *     
     */
    public TipoConcursoUltPer.ConcursoUltPerSI getConcursoUltPerSI() {
        return concursoUltPerSI;
    }

    /**
     * Define el valor de la propiedad concursoUltPerSI.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoConcursoUltPer.ConcursoUltPerSI }
     *     
     */
    public void setConcursoUltPerSI(TipoConcursoUltPer.ConcursoUltPerSI value) {
        this.concursoUltPerSI = value;
    }

    /**
     * Obtiene el valor de la propiedad concursoUltPerNO.
     * 
     * @return
     *     possible object is
     *     {@link TipoConcursoUltPer.ConcursoUltPerNO }
     *     
     */
    public TipoConcursoUltPer.ConcursoUltPerNO getConcursoUltPerNO() {
        return concursoUltPerNO;
    }

    /**
     * Define el valor de la propiedad concursoUltPerNO.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoConcursoUltPer.ConcursoUltPerNO }
     *     
     */
    public void setConcursoUltPerNO(TipoConcursoUltPer.ConcursoUltPerNO value) {
        this.concursoUltPerNO = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ConcursoUltPerNO {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ConcursoUltPerSI {


    }

}
