
package com.esferalia.aon.sepe.api.SWConsultaDatos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="servicioConsultaReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "servicioConsultaReturn"
})
@XmlRootElement(name = "servicioConsultaResponse")
public class ServicioConsultaResponse {

    @XmlElement(required = true, nillable = true)
    protected String servicioConsultaReturn;

    /**
     * Obtiene el valor de la propiedad servicioConsultaReturn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicioConsultaReturn() {
        return servicioConsultaReturn;
    }

    /**
     * Define el valor de la propiedad servicioConsultaReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicioConsultaReturn(String value) {
        this.servicioConsultaReturn = value;
    }

}
