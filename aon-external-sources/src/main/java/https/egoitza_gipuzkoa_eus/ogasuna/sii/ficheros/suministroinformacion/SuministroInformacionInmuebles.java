
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.SuministroLRInmueblesAdicionales;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos 
 * 			                              de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para SuministroInformacionInmuebles complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SuministroInformacionInmuebles"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}CabeceraSiiCobrosPagos"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuministroInformacionInmuebles", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    SuministroLRInmueblesAdicionales.class
})
public class SuministroInformacionInmuebles {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraSiiCobrosPagos cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraSiiCobrosPagos }
     *     
     */
    public CabeceraSiiCobrosPagos getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraSiiCobrosPagos }
     *     
     */
    public void setCabecera(CabeceraSiiCobrosPagos value) {
        this.cabecera = value;
    }

}
