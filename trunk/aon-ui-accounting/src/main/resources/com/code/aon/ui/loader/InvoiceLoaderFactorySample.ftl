<b>Ejemplo de carga de un fichero de facturas, líneas de facturas y vencimientos.</b>
<p/>
Se define la entidad FRA (facturas), DET (líneas de facturas) y VTO (vencimientos), utilizando el separador y la codificación por defecto, 
<pre>
1;FRA|id|serie|numero|referencia|idTitular|cuenta|documento|tipoDocumento|paisDocumento|razonSocial|   >>
	fechaFactura|fechaIva|tipo|inversion|transaccion|comentario|baseImponible|totalCuotaIVA|totalCuotaIRPF| >>
	totalFactura
1;DET|factura|linea|concepto|articulo|cantidad|precio|baseImponible|porcentajeIva|cuotaIva|re|cuotaRe| >>
	porcentajeIrpf|cuotaIrpf|tipoDeduccionIva|tipoIrpf|cuenta|cuentaIva|cuentaIrpf
1;VTO|id|factura|pago|importe|fechaVto|formaPago|cuentaBanco


</pre>
