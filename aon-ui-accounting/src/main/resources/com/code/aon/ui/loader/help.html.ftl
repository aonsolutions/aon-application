<?xml version="1.0" encoding="ISO-8859-1"?>
<html>
<head>
<meta http-equiv="Content-type" content="text/html;charset=ISO-8859-1"/>
<style type="text/css">

body {font-size: 12px; font-family: "arial","lucida Grande","Trebuchet MS",sans-serif;}
table {font: inherit; width: 95%; border-collapse:collapse;margin: 15px 0px 15px 15px;}
@media screen {
	body {font-size: 14px;}
}
@media print {
	body {font-size: 10px;}
}
th {text-align: center; background-color: #DDDDDD;border-bottom: solid black 0.2px;}
td {border-bottom: solid black 0.2px;}
pre {margin-left: 15px;font-size: 0.9em;}
ul {margin-bottom: 5px; margin-top: 0px;}
a {color:black;}
a:link {color:blue;}
h4 {page-break-before: always;}
.example {width: 95%;border: solid black 0.5px;background-color: #EEEEEE;margin: 15px 0px 15px 15px; page-break-inside:avoid;}
.example div {padding: 15px 0px 15px 15px;}
</style>
</head>
<body> 
<div style="text-align:right;"><span style="font-size: 0.8em;">${.now}</span></div>
<h1>Carga de datos desde fichero.</h1>

<h2>Formato del fichero de carga</h2>
La carga de datos se realiza desde un fichero de texto plano. <br/>
El fichero se divide en líneas y cada línea en datos, que llamaremos columnas, y  
que se encuentran separados por un delimitador. <br/>
Se pueden definir tres tipos de líneas, en función de su utilidad para el programa de carga.
Las líneas deberán estar identificados en su primera columna, por un código que las discrimina.
<p>Los tipos de línea son:</p>
<ol>
<li><a href="#line1">Líneas de metainformación del contenido del fichero</a></li>
<li><a href="#line2">Líneas de definición de datos</a></li>
<li><a href="#line3">Líneas de datos</a></li>
</ol>

<h3><a id="line1">Líneas de metainformación del contenido del fichero</a></h3>
Las líneas de metainformación informan al programa de carga, de ciertas 
directrices a seguir para que los datos se lean correctamente.<br/>
Son líneas opcionales, si se definen cambian el comportamiento por defecto.
En caso de indicarlas, deben ser las <b>primeras líneas</b> del archivo.<br/>
<p>El formato de estas líneas es:</p>
<div class="example">
<div>
<code>0;<i>directriz</i>=<i>valor</i></code>
</div>
</div>


<p>Actualmente existen dos directrices:</p>
<table>
	<thead>
		<th>Directriz</th>
		<th>Valor por defecto</th>
		<th>Descripción</th>
	</thead>
	<tbody>
	<tr>
		<td style="text-align: center">
			<code>Separador</code>
		</td>
		<td style="text-align: center">
			Carácter <i>pipe</i> ( <code>|</code> ).<br/> 
			En el teclado: <code>AltGr+1</code>.
		</td>
		<td>
			Uno o más caracteres utilizados para <br/> separar las columnas en las líneas de datos. <br/>
		</td>
	</tr>
	<tr>
		<td style="text-align: center">
			<code>Codificacion</code>
		</td>
		<td style="text-align: center">
			<code>ISO-8859-1</code>
		</td>
		<td>
			Codificacion. Codificación del fichero de texto. <br/>
			Debe ser un valor de codificación válido, UTF-8, ISO-8859-1, etc ..<br/>
			Para más información pulse <a href="http://es.wikipedia.org/wiki/Codificaci%C3%B3n_de_caracteres" target="_blank">aquí</a>.<br/>
		</td>
	</tr>
	</tbody>
</table>
<p>Ejemplos de definición de líneas de metainformación:</p>
<div class="example">
<div>
EJEMPLO 1: El separador es <code>#</code> y la codificación del archivo <code>UTF-8</code>.
<pre>0;Separador=#<br/>0;Codificacion=UTF-8<br/>[.......]</pre>
EJEMPLO 2: El separador es <code>%#%</code> y la codificación del archivo <code>ISO-8859-6</code>.
<pre>0;Separador=%#%<br/>0;Codificacion=ISO-8859-6<br/>[.......]</pre>
EJEMPLO 3: El separador es <code>#</code>. El contenido del fichero se leerá utilizando la codificación por defecto, ISO-8859-1.
<pre>0;Separador=#<br/>[.......]</pre>
</div>
</div>

<h3 style="page-break-before: always;"><a id="line2">Líneas de definición de datos</a></h3>
Mediante las líneas de definición de datos, se informa al programa de carga de qué entidades se van a cargar y para cada entidad, qué columnas.
<b>Deben estar presentes ANTES de las líneas de datos.</b> 
<p>El formato de estas líneas es :</p>
<div class="example">
<div>
EJEMPLO 1. Si se utiliza el separador por defecto:
<pre>1;Código_entidad|nombreColumna1|nombreColumna2|nombreColumna3...<br/>[.......]</pre>
EJEMPLO 2. Si se utiliza el separador personalizado <code>#</code> :
<pre>0;Separador=#<br/>1;Código_entidad#nombreColumna1#nombreColumna2#nombreColumna3 ...<br/>[.......]</pre>
</div>
</div>
  
A continuación se detallan las entidades soportadas. 
Consulte cada una de ellas para ver las columnas que contienen, junto con las caracteríasticas de las mismas. 

<table>
<thead>
	<th style="width: 20%;">Código Entidad</th>
	<th style="width: auto;">Descripción</th>
</thead>
<tbody>
<#list factories as factory>
<#if factory.getKey() != "ASI" && factory.getKey() != "EMP" && factory.getKey() != "AMC" && factory.getKey() != "AML" && factory.getKey() != "MOC" && factory.getKey() != "MOL" && factory.getKey() != "BAN" && factory.getKey() != "REP" && factory.getKey() != "CON" && factory.getKey() != "CUO" && factory.getKey() != "ACT">
<tr>
	<td style="text-align: center;"><a href="#${factory.getClass().getSimpleName()}"><code>${factory.getKey()}</code></a></td>
	<td>${bundle.getString( factory.getClass().getSimpleName())}</td>
</tr>	
</#if>
</#list>
</tbody>
</table>
<div class="example">
<div>
EJEMPLO 1: Se define la entidad CLI (clientes), utilizando el separador y la codificación por defecto, 
las líneas de datos contienen las columnas id,razonSocial,tipoDocumento,paisDocumento,documento,cuenta y telefono1.
<pre>1;CLI|id|razonSocial|tipoDocumento|paisDocumento|documento|cuenta|telefono1<br/>[.......]</pre>
EJEMPLO 2: Se define la entidad CLI (clientes), utilizando el separador <code>#</code> y la codificación <code>UTF-8</code>, 
las líneas de datos contienen las columnas id,razonSocial,tipoDocumento,paisDocumento,documento,cuenta y telefono1.
<pre>0;Separador=#<br/>0;Codificacion=UTF-8<br/>1;CLI#id#razonSocial#tipoDocumento#paisDocumento#documento#cuenta#telefono1<br/>[.......]</pre>
</div>
</div>
<p/>
<p/>

A continuación se detalla las columnas y sus características de cada una de las entidades soportadas. Deberá tener en cuenta:
<ul>
<li>El nombre de la columna es el que se debe utilizar en la línea de definición de datos.</li>
<li>Si el dato es obligatorio, deberá aparecer en la definición.</li>
<li>La longitud de la columna, se refiere a la longitud máxima que el dato puede tener en las líneas de datos. Es decir, puede tener una longitud menor, a no ser que en la descripción de la columna se indique lo contrario.</li>
<li>Si se indican valores posibles para la columna, el dato no podrá contener un valor no listado.</li>
</ul>
  



<#list factories as factory>
<#if factory.getKey() != "ASI" && factory.getKey() != "EMP" && factory.getKey() != "AMC" && factory.getKey() != "AML" && factory.getKey() != "MOC" && factory.getKey() != "MOL" && factory.getKey() != "BAN" && factory.getKey() != "REP" && factory.getKey() != "CON" && factory.getKey() != "CUO" && factory.getKey() != "ACT">
<#assign prefix = factory.getClass().getSimpleName()>
<h4>
		<a id="${prefix}">
			${bundle.getString( prefix )} ( ${factory.getKey()} )
		</a>
</h4>

<#if factory.getKey() == "FRA">
La carga de la entidad FRA, no tiene sentido si en el mismo fichero de carga no se indican las líneas de facturas (entidad DET).
En caso de no indicarse la entidad DET, únicamente se grabarían cabeceras de facturas.
</#if>

<table cellspacing="0" cellpadding="2">
	<thead>
		<tr>
			<th style="width: 20%;">Columna</th>
			<th style="width: 5%;">Oblig.</th>
			<th style="width: 10%;">Tipo</th>
			<th style="width: 5%;">Long.Max.</th>
			<th style="width: 60%;">Descripción</th>
	    </tr>
    </thead>
    <tbody>
    <#list factory.getSupportedColumns() as column>
	<tr>
    	<td><code>${column.getName()}</code></td>
    	<td style="text-align: center;">
    		<#if column.isRequired()>
    			<b>SI</b>
    		<#else>
    			<span style="font-size: 0.8em;">NO</span>
    		</#if>
    	</td>
    	<td>
    		<#if column.getType() == 0>
    			Numérico
    		</#if>
    		<#if column.getType() == 1>
    			Decimal
    		</#if>
    		<#if column.getType() == 2>
    			Carácter
    		</#if>
    		<#if column.getType() == 3>
    			Fecha 
    		</#if>
    	</td>
    	<td style="text-align: center;">
    		${column.getLength()}
    	</td>
    	<td>
    		<#assign descriptionKey = prefix+"_"+ column.getName()>  
			${bundle.getString( descriptionKey )}
    		
    		<#if column.getType() == 3>
    			Formato dd/mm/aaaa
    		</#if>
    		
    		<#if column.hasEnumValues()>
    			<br/>Valores posibles:
	    		<ul>
	    		<#list column.getEnumValues() as enumValue>
	    			<#assign enumKey = column.getName() + "_" + enumValue>
	    			<li>${enumValue} - ${bundle.getString( enumKey )}</li>
	    		</#list>
	    		</ul>
    		</#if>
    	</td>
    </tr>
    </#list>
    </tbody>
</table>
</#if>
</#list>

<h3 style="page-break-before: always;"><a id="line3">Líneas de datos</a></h3>
El programa de carga grabará una entidad por cada línea de datos encontrada en el fichero. El programa necesita identificar qué entidad 
está leyendo, con tal fin, <b>la primera columna deberá especificar la entidad a la que se refiere</b>.
A continuación se indicaran, en escrupuloso orden, las columnas definidas en la línea de definición de la entidad correspondiente.
 
<p/>
<p/>
<#list factories as factory>
<#attempt>
	<#assign sample_path = factory.getClass().getSimpleName() + "Sample.ftl">
	<div class="example">
	<div>
		<#include sample_path>
	</div>
	</div>
<#recover>
</#attempt>
</#list>


<h2 style="page-break-before: always;">Anexo</h2>
<h3><a id="tipo_via">Tipos de vías</a></h3>
<table id="tipoVia" style="width: auto;  text-align: center;">
<thead> 
<tr>
	<th style="width: 75px;">Código</th>
	<th style="width: 100px;">Descripción</th>
	<th style="width: 75px;">Código</th>
	<th style="width: 100px;">Descripción</th>
</tr>
</thead>
<tbody>
<tr>
	<td>AC</td><td>Acera</td>
	<td>MZ</td><td>Manzana</td>
</tr>
<tr>
	<td>AD</td><td>Aldea</td>
	<td>PA</td><td>Paseo alto</td>
</tr>
<tr>
	<td>AL</td><td>Alameda</td>
	<td>PB</td><td>Poblado</td>
</tr>
<tr>
	<td>AM</td><td>Ampliación</td>
	<td>PC</td><td>Particular</td>
</tr>
<tr>
	<td>AN</td><td>Angosta</td>
	<td>PD</td><td>Pasadizo</td>
</tr>
<tr>
	<td>AP</td><td>Apartamentos</td>
	<td>PE</td><td>Plazoleta</td>
</tr>
<tr>
	<td>AQ</td><td>Acequia</td>
	<td>PG</td><td>Polígono</td>
</tr>
<tr>
	<td>AT</td><td>Atajo</td>
	<td>PI</td><td>Pasillo</td>
</tr>
<tr>
	<td>AV</td><td>Avenida</td>
	<td>PJ</td><td>Pasaje</td>
</tr>
<tr>
	<td>BA</td><td>Bajada</td>
	<td>PL</td><td>Placeta</td>
</tr>
<tr>
	<td>BC</td><td>Barranco</td>
	<td>PN</td><td>Prolongación</td>
</tr>
<tr>
	<td>BD</td><td>Barriada</td>
	<td>PO</td><td>Paseo bajo</td>
</tr>
<tr>
	<td>BL</td><td>Bloques</td>
	<td>PQ</td><td>Parque</td>
</tr>
<tr>
	<td>BO</td><td>Barrio</td>
	<td>PR</td><td>Portales</td>
</tr>
<tr>
	<td>CA</td><td>Calleja</td>
	<td>PS</td><td>Paso</td>
</tr>
<tr>
	<td>CE</td><td>Callejuela</td>
	<td>PT</td><td>Patio</td>
</tr>
<tr>
	<td>CH</td><td>Chalet</td>
	<td>PU</td><td>Plazuela</td>
</tr>
<tr>
	<td>CJ</td><td>Callejón</td>
	<td>PV</td><td>Privada</td>
</tr>
<tr>
	<td>CL</td><td>Calle</td>
	<td>PZ</td><td>Plaza</td>
</tr>
<tr>
	<td>CM</td><td>Camino</td>
	<td>RA</td><td>Ramal</td>
</tr>
<tr>
	<td>CN</td><td>Costanilla</td>
	<td>RB</td><td>Rambla</td>
</tr>
<tr>
	<td>CO</td><td>Colonia</td>
	<td>RC</td><td>Rinconada</td>
</tr>
<tr>
	<td>CP</td><td>Cooperativa</td>
	<td>RD</td><td>Ronda</td>
</tr>
<tr>
	<td>CR</td><td>Carrera</td>
	<td>RN</td><td>Rincón</td>
</tr>
<tr>
	<td>CS</td><td>Caserio</td>
	<td>RP</td><td>Rampa</td>
</tr>
<tr>
	<td>CT</td><td>Carretera</td>
	<td>RR</td><td>Ribera</td>
</tr>
<tr>
	<td>CU</td><td>Cuesta</td>
	<td>SC</td><td>Sector</td>
</tr>
<tr>
	<td>CZ</td><td>Calzada</td>
	<td>SD</td><td>Senda</td>
</tr>
<tr>
	<td>EA</td><td>Escala</td>
	<td>SR</td><td>Sendero</td>
</tr>
<tr>
	<td>ED</td><td>Edificio</td>
	<td>SU</td><td>Subida</td>
</tr>
<tr>
	<td>EL</td><td>Escalinata</td>
	<td>TL</td><td>Transversal</td>
</tr>
<tr>
	<td>ES</td><td>Escalera</td>
	<td>TR</td><td>Travesía</td>
</tr>
<tr>
	<td>ET</td><td>Estrada</td>
	<td>TS</td><td>Trasera</td>
</tr>
<tr>
	<td>GL</td><td>Glorieta</td>
	<td>TT</td><td>Torrente</td>
</tr>
<tr>
	<td>GR</td><td>Grupo</td>
	<td>UR</td><td>Urbanización</td>
</tr>
<tr>
	<td>KO</td><td>Corral</td>
	<td>VI</td><td>Vía</td>
</tr>
<tr>
	<td>LG</td><td>Lugar</td>
	<td>VL</td><td>Villas</td>
</tr>
<tr>
	<td>LL</td><td>Llano</td>
	<td>XX</td><td>Sin datos</td>
</tr>
<tr>
	<td>MC</td><td>Mercado</td>
	<td>ZO</td><td>Zona</td>
</tr>
<tr>
	<td>MN</td><td>Municipio</td>
	<td>ZZ</td><td>Otros</td>
</tr>
<tr>
	<td>MO</td><td>Montaña</td>
	<td></td><td></td>
</tr>
</tbody>
</table>
</body>
</html>