
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
 *         &lt;element name="servicioHorasComplementariasReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "servicioHorasComplementariasReturn"
})
@XmlRootElement(name = "servicioHorasComplementariasResponse")
public class ServicioHorasComplementariasResponse {

    @XmlElement(required = true, nillable = true)
    protected String servicioHorasComplementariasReturn;

    /**
     * Obtiene el valor de la propiedad servicioHorasComplementariasReturn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicioHorasComplementariasReturn() {
        return servicioHorasComplementariasReturn;
    }

    /**
     * Define el valor de la propiedad servicioHorasComplementariasReturn.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicioHorasComplementariasReturn(String value) {
        this.servicioHorasComplementariasReturn = value;
    }

}
