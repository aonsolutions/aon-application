
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroAgenciasViajesType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroBienInversionType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroCobrosMetalicoType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroDetOperIntracomunitariasType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroOperacionesSegurosType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRFiltroRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRAgenciasViajesType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaAgenciasViajesType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaBienesInversionType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaCobrosMetalicoType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaExpedidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaOperacionIntracomunitariaType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBajaRegistroLROperacionesSegurosType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRBienesInversionType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRCobrosMetalicoType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRFacturasRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LROperacionIntracomunitariaType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LROperacionesSegurosType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.LRfacturasEmitidasType;


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
 *                   &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
     *         &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
