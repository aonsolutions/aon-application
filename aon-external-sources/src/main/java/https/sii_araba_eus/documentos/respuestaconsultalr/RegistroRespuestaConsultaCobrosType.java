
package https.sii_araba_eus.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.DatosPagoCobroType;
import https.sii_araba_eus.documentos.suministroinformacion.DatosPresentacion2Type;


/**
 * <p>Clase Java para RegistroRespuestaConsultaCobrosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaCobrosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosCobro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DatosPagoCobroType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaCobrosType", propOrder = {
    "datosCobro",
    "datosPresentacion"
})
public class RegistroRespuestaConsultaCobrosType {

    @XmlElement(name = "DatosCobro", required = true)
    protected DatosPagoCobroType datosCobro;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;

    /**
     * Obtiene el valor de la propiedad datosCobro.
     * 
     * @return
     *     possible object is
     *     {@link DatosPagoCobroType }
     *     
     */
    public DatosPagoCobroType getDatosCobro() {
        return datosCobro;
    }

    /**
     * Define el valor de la propiedad datosCobro.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosPagoCobroType }
     *     
     */
    public void setDatosCobro(DatosPagoCobroType value) {
        this.datosCobro = value;
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
