
package https.sii_araba_eus.documentos.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.IdRegistroDeclaradoType;


/**
 *  Apunte correspondiente al libro venta de bienes en consigna. 
 * 
 * <p>Clase Java para LRBajaVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRBajaVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IdRegistroDeclarado" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}IdRegistroDeclaradoType"/&gt;
 *         &lt;element name="RefExterna" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRBajaVentaBienesConsignaType", propOrder = {
    "idRegistroDeclarado",
    "refExterna"
})
public class LRBajaVentaBienesConsignaType {

    @XmlElement(name = "IdRegistroDeclarado", required = true)
    protected IdRegistroDeclaradoType idRegistroDeclarado;
    @XmlElement(name = "RefExterna")
    protected String refExterna;

    /**
     * Obtiene el valor de la propiedad idRegistroDeclarado.
     * 
     * @return
     *     possible object is
     *     {@link IdRegistroDeclaradoType }
     *     
     */
    public IdRegistroDeclaradoType getIdRegistroDeclarado() {
        return idRegistroDeclarado;
    }

    /**
     * Define el valor de la propiedad idRegistroDeclarado.
     * 
     * @param value
     *     allowed object is
     *     {@link IdRegistroDeclaradoType }
     *     
     */
    public void setIdRegistroDeclarado(IdRegistroDeclaradoType value) {
        this.idRegistroDeclarado = value;
    }

    /**
     * Obtiene el valor de la propiedad refExterna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefExterna() {
        return refExterna;
    }

    /**
     * Define el valor de la propiedad refExterna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefExterna(String value) {
        this.refExterna = value;
    }

}
