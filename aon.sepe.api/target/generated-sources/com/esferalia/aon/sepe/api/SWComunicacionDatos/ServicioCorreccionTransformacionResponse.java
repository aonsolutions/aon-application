
package com.esferalia.aon.sepe.api.SWComunicacionDatos;

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
 *         &lt;element name="servicioCorreccionTransformacionReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "servicioCorreccionTransformacionReturn"
})
@XmlRootElement(name = "servicioCorreccionTransformacionResponse")
public class ServicioCorreccionTransformacionResponse {

    @XmlElement(required = true, nillable = true)
    protected String servicioCorreccionTransformacionReturn;

    /**
     * Obtiene el valor de la propiedad servicioCorreccionTransformacionReturn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicioCorreccionTransformacionReturn() {
        return servicioCorreccionTransformacionReturn;
    }

    /**
     * Define el valor de la propiedad servicioCorreccionTransformacionReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicioCorreccionTransformacionReturn(String value) {
        this.servicioCorreccionTransformacionReturn = value;
    }

}
