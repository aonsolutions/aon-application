
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DatosInmuebleType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DatosPresentacion2Type;


/**
 * <p>Clase Java para RegistroRespuestaConsultaInmueblesAdicionalesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaInmueblesAdicionalesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosInmueblesAdicionales" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DatosInmuebleType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaInmueblesAdicionalesType", propOrder = {
    "datosInmueblesAdicionales",
    "datosPresentacion"
})
public class RegistroRespuestaConsultaInmueblesAdicionalesType {

    @XmlElement(name = "DatosInmueblesAdicionales", required = true)
    protected DatosInmuebleType datosInmueblesAdicionales;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;

    /**
     * Obtiene el valor de la propiedad datosInmueblesAdicionales.
     * 
     * @return
     *     possible object is
     *     {@link DatosInmuebleType }
     *     
     */
    public DatosInmuebleType getDatosInmueblesAdicionales() {
        return datosInmueblesAdicionales;
    }

    /**
     * Define el valor de la propiedad datosInmueblesAdicionales.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosInmuebleType }
     *     
     */
    public void setDatosInmueblesAdicionales(DatosInmuebleType value) {
        this.datosInmueblesAdicionales = value;
    }

    /**
     * Obtiene el valor de la propiedad datosPresentacion.
     * 
     * @return
     *     possible object is
     *     {@link DatosPresentacion2Type }
     *     
     */
    public DatosPresentacion2Type getDatosPresentacion() {
        return datosPresentacion;
    }

    /**
     * Define el valor de la propiedad datosPresentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosPresentacion2Type }
     *     
     */
    public void setDatosPresentacion(DatosPresentacion2Type value) {
        this.datosPresentacion = value;
    }

}
