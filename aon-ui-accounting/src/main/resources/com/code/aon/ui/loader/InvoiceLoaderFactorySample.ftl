<b>Ejemplo de carga de un fichero de facturas, líneas de facturas y vencimientos.</b>
<p/>
Se define la entidad FRA (facturas), DET (líneas de facturas) y VTO (vencimientos), utilizando el separador y la codificación por defecto, 
<pre>
1;FRA|id|serie|numero|referencia|idTitular|cuenta|documento|tipoDocumento|paisDocumento|   >>
	razonSocial|fechaFactura|fechaIva|tipo|transaccion|baseImponible|totalCuotaIVA|totalCuotaIRPF|totalFactura
1;DET|factura|linea|concepto|articulo|cantidad|precio|descuentos|baseImponible|   >>
	porcentajeIva|cuotaIva|re|cuotaRe|porcentajeIrpf|cuotaIrpf
1;VTO|id|factura|documento|tipoDocumento|paisDocumento|razonSocial|pago|importe|fechaVto
FRA|1|2014|1000|2014/1000|1|430000002|B05555555|1|ES|Granja LOPEZ, SL|15/12/2014   >>
	|15/12/2014|1|0|27842.88|5847.01|0.0|33689.890
DET|1|1|ARTICULO 1 DE LA FACTURA|P21|1|1000.0|10.0|900.0|21.0|189.0|0.0|0.0|0.0|0.0
DET|1|2|ARTICULO 2 DE LA FACTURA|A-23|5|7459.27|16+14|26942.88|21.0|5658.01|0.0|0.0|0.0|0.0
VTO|1|1|B05555555|1|ES|Granja LOPEZ, SL|0|33689.890|15/12/2014

</pre>
