<b>Ejemplo de carga de un fichero de facturas en una sola línea.</b>
<p/>
Se define la entidad FRACTB (factura en una línea) utilizando el separador y la codificación por defecto, 

<pre>1;FRACTB|id|serie|numero|cuenta|documento|tipoDocumento|paisDocumento|   >>
	razonSocial|fechaFactura|tipo|baseImponible1|iva1|cuotaIVA1|totalFactura|   >>
	concepto|cuentaExplotacion
FRACTB|20000001||10000|430186000|A2800000|1|ES|RAZON SOCIAL CLI1|31/01/2013|1|   >>
	22.80|21|4.79|27.59|N/F 00001|700000000
FRACTB|20000002||10001|431000000|A2800001|1|ES|RAZON SOCIAL CLI2|31/01/2013|1|   >>
	118.00|21|24.78|142.78|N/F 00002|700000000
FRACTB|20000003||10002|430187000|A2800002|1|ES|RAZON SOCIAL CLI3|31/01/2013|1|   >>
	145.50|21|30.56|176.06|N/F 00003|700000000
FRACTB|20000004||10003|430188000|A2800003|1|ES|RAZON SOCIAL CLI4|31/01/2013|1|   >>
	240.00|21|50.40|290.40|N/F 00004|700000000</pre>
