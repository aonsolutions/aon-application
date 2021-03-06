
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroAgenciasViajesType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroBienInversionType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroCobrosMetalicoType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroDetOperIntracomunitariasType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroEmitidasType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroOperacionesSegurosType;
import https.sii_araba_eus.documentos.consultalr.LRFiltroRecibidasType;
import https.sii_araba_eus.documentos.suministrolr.LRAgenciasViajesType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaAgenciasViajesType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaBienesInversionType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaCobrosMetalicoType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaExpedidasType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaOperacionIntracomunitariaType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaRecibidasType;
import https.sii_araba_eus.documentos.suministrolr.LRBajaRegistroLROperacionesSegurosType;
import https.sii_araba_eus.documentos.suministrolr.LRBienesInversionType;
import https.sii_araba_eus.documentos.suministrolr.LRCobrosMetalicoType;
import https.sii_araba_eus.documentos.suministrolr.LRFacturasRecibidasType;
import https.sii_araba_eus.documentos.suministrolr.LROperacionIntracomunitariaType;
import https.sii_araba_eus.documentos.suministrolr.LROperacionesSegurosType;
import https.sii_araba_eus.documentos.suministrolr.LRfacturasEmitidasType;


/**
 *  Información básica que contienen los registros del suministro de información 
 * 
 * <p>Clase Java para RegistroSii complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroSii"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PeriodoLiquidacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroSii", propOrder = {
    "periodoLiquidacion"
})
@XmlSeeAlso({
    LRfacturasEmitidasType.class,
    LRBajaExpedidasType.class,
    LRFacturasRecibidasType.class,
    LRBajaRecibidasType.class,
    LRBienesInversionType.class,
    LRBajaBienesInversionType.class,
    LRAgenciasViajesType.class,
    LRBajaAgenciasViajesType.class,
    LRCobrosMetalicoType.class,
    LRBajaCobrosMetalicoType.class,
    LROperacionesSegurosType.class,
    LRBajaRegistroLROperacionesSegurosType.class,
    LROperacionIntracomunitariaType.class,
    LRBajaOperacionIntracomunitariaType.class,
    LRFiltroEmitidasType.class,
    LRFiltroRecibidasType.class,
    LRFiltroBienInversionType.class,
    LRFiltroDetOperIntracomunitariasType.class,
    LRFiltroOperacionesSegurosType.class,
    LRFiltroCobrosMetalicoType.class,
    LRFiltroAgenciasViajesType.class
})
public class RegistroSii {

    @XmlElement(name = "PeriodoLiquidacion", required = true)
    protected RegistroSii.PeriodoLiquidacion periodoLiquidacion;

    /**
     * Obtiene el valor de la propiedad periodoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link RegistroSii.PeriodoLiquidacion }
     *     
     */
    public RegistroSii.PeriodoLiquidacion getPeriodoLiquidacion() {
        return periodoLiquidacion;
    }

    /**
     * Define el valor de la propiedad periodoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroSii.PeriodoLiquidacion }
     *     
     */
    public void setPeriodoLiquidacion(RegistroSii.PeriodoLiquidacion value) {
        this.periodoLiquidacion = value;
    }


    /**
     *  Período al que corresponden los apuntes. todos los apuntes deben corresponder al mismo período impositivo 
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ejercicio",
        "periodo"
    })
    public static class PeriodoLiquidacion {

        @XmlElement(name = "Ejercicio", required = true)
        protected String ejercicio;
        @XmlElement(name = "Periodo", required = true)
        protected String periodo;

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEjercicio(String value) {
            this.ejercicio = value;
        }

        /**
         * Obtiene el valor de la propiedad periodo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPeriodo() {
            return periodo;
        }

        /**
         * Define el valor de la propiedad periodo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPeriodo(String value) {
            this.periodo = value;
        }

    }

}
