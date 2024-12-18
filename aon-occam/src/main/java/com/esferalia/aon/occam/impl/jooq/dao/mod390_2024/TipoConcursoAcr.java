//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.12.05 a las 04:40:27 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_ConcursoAcr complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_ConcursoAcr"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="ConcursoAcr_SI" type="{}tipo_ConcursoUltPer"/&gt;
 *         &lt;element name="ConcursoAcr_NO"&gt;
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
@XmlType(name = "tipo_ConcursoAcr", propOrder = {
    "concursoAcrSI",
    "concursoAcrNO"
})
public class TipoConcursoAcr {

    @XmlElement(name = "ConcursoAcr_SI")
    protected TipoConcursoUltPer concursoAcrSI;
    @XmlElement(name = "ConcursoAcr_NO")
    protected TipoConcursoAcr.ConcursoAcrNO concursoAcrNO;

    /**
     * Obtiene el valor de la propiedad concursoAcrSI.
     * 
     * @return
     *     possible object is
     *     {@link TipoConcursoUltPer }
     *     
     */
    public TipoConcursoUltPer getConcursoAcrSI() {
        return concursoAcrSI;
    }

    /**
     * Define el valor de la propiedad concursoAcrSI.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoConcursoUltPer }
     *     
     */
    public void setConcursoAcrSI(TipoConcursoUltPer value) {
        this.concursoAcrSI = value;
    }

    /**
     * Obtiene el valor de la propiedad concursoAcrNO.
     * 
     * @return
     *     possible object is
     *     {@link TipoConcursoAcr.ConcursoAcrNO }
     *     
     */
    public TipoConcursoAcr.ConcursoAcrNO getConcursoAcrNO() {
        return concursoAcrNO;
    }

    /**
     * Define el valor de la propiedad concursoAcrNO.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoConcursoAcr.ConcursoAcrNO }
     *     
     */
    public void setConcursoAcrNO(TipoConcursoAcr.ConcursoAcrNO value) {
        this.concursoAcrNO = value;
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
    public static class ConcursoAcrNO {


    }

}
