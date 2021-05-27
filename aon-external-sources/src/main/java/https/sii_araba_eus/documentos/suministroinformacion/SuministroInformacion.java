
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRAgenciasViajes;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRBienesInversion;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRCobrosMetalico;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRFacturasEmitidas;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRFacturasRecibidas;
import https.sii_araba_eus.documentos.suministrolr.SuministroLROperacionesSeguros;
import https.sii_araba_eus.documentos.suministrolr.SuministroLRVentaBienesConsigna;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos 
 * 			                              de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para SuministroInformacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SuministroInformacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CabeceraSii"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuministroInformacion", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    SuministroLRVentaBienesConsigna.class,
    SuministroLRDetOperacionIntracomunitaria.class,
    SuministroLROperacionesSeguros.class,
    SuministroLRCobrosMetalico.class,
    SuministroLRAgenciasViajes.class,
    SuministroLRBienesInversion.class,
    SuministroLRFacturasRecibidas.class,
    SuministroLRFacturasEmitidas.class
})
public class SuministroInformacion {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraSii cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraSii }
     *     
     */
    public CabeceraSii getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraSii }
     *     
     */
    public void setCabecera(CabeceraSii value) {
        this.cabecera = value;
    }

}
