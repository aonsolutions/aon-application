
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
 *         &lt;element name="servicioCorreccionHorasComplementariasReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "servicioCorreccionHorasComplementariasReturn"
})
@XmlRootElement(name = "servicioCorreccionHorasComplementariasResponse")
public class ServicioCorreccionHorasComplementariasResponse {

    @XmlElement(required = true, nillable = true)
    protected String servicioCorreccionHorasComplementariasReturn;

    /**
     * Obtiene el valor de la propiedad servicioCorreccionHorasComplementariasReturn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicioCorreccionHorasComplementariasReturn() {
        return servicioCorreccionHorasComplementariasReturn;
    }

    /**
     * Define el valor de la propiedad servicioCorreccionHorasComplementariasReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicioCorreccionHorasComplementariasReturn(String value) {
        this.servicioCorreccionHorasComplementariasReturn = value;
    }

}
