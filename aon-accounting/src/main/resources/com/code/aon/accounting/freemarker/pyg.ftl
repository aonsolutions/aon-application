<#setting number_format="#,##0.00;(#,##0.00)">
<html>
<head>
<title>CUENTA DE PÉRDIDAS Y GANANCIAS ABREVIADA  DEL EJERCICIO ${ejercicio}</title>
<style type="text/css">
html, body {
	border:0;
	margin:0;
	padding:0;
}
body {
	font: 10px/1.333 "arial","lucida Grande","Trebuchet MS",sans-serif;
	color:#4b4b4b;
	background-color:#fff;
	overflow:visible;
}
.data_container {
	width: 100%;
	padding: 5px;
}
.data_container table {
	margin: 10px;
	border-collapse:collapse;
	width: 95%;
}
.data_container table caption {
	font-size: 1.2em;
	font-weight:bold;
	padding: 5px;
	border: solid #888888 1px;
}
.data_container table tr th {
	padding: 5px 1em;
	font-weight:bold;
 	font-size:0.9em;
 	text-transform:uppercase;
  	background-color: #EEEEEE;
  	border: #888888 1px solid;
}
.data_container table tr:nth-child(odd) {
  	background-color: #F8F8F8;
}
.data_container table tr td {
	border: solid #888888 0.5px;
	padding: 3px;
	empty-cells: show;
}
.data_container table tr:td(odd) {
  	background-color: #EFEFEF;
}

.data_container table tr td:first-child {
	text-align: center;
	width: 75px;
}
.data_container table tr td:nth-last-child(4){
	width: auto;
}
.data_container table tr td:nth-last-child(3){
	width: 75px;
}
.data_container table tr td:nth-last-child(1), td:nth-last-child(2){
	text-align: right;
	width: 150px;
}

 </style>
</head>
<body>
<div class="data_container">
	<table cellspacing="0" cellpadding="0">
		<caption>
	    	CUENTA DE PÉRDIDAS Y GANANCIAS ABREVIADA  DEL EJERCICIO ${ejercicio}
	    </caption>
	    
	
		<tr>
			<th>Código</th>
			<th>Descripción</th>
			<th>Notas</th>
			<th>Ej. ${ejercicio}</th>
			<th>Ej. ${ejercicio_1!""}</th>
		</tr>
		<tr>
			<td>${codigo(c40100)}</td>
			<td>${descripcion(c40100)}</td>
			<td>${notas(c40100)}</td>
			<td>${saldo(c40100)}</td>
			<td>${saldo(c40100,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40200)}</td>
			<td>${descripcion(c40200)}</td>
			<td>${notas(c40200)}</td>
			<td>${saldo(c40200)}</td>
			<td>${saldo(c40200,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40300)}</td>
			<td>${descripcion(c40300)}</td>
			<td>${notas(c40300)}</td>
			<td>${saldo(c40300)}</td>
			<td>${saldo(c40300,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40400)}</td>
			<td>${descripcion(c40400)}</td>
			<td>${notas(c40400)}</td>
			<td>${saldo(c40400)}</td>
			<td>${saldo(c40400,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40500)}</td>
			<td>${descripcion(c40500)}</td>
			<td>${notas(c40500)}</td>
			<td>${saldo(c40500)}</td>
			<td>${saldo(c40500,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40600)}</td>
			<td>${descripcion(c40600)}</td>
			<td>${notas(c40600)}</td>
			<td>${saldo(c40600)}</td>
			<td>${saldo(c40600,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40700)}</td>
			<td>${descripcion(c40700)}</td>
			<td>${notas(c40700)}</td>
			<td>${saldo(c40700)}</td>
			<td>${saldo(c40700,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40800)}</td>
			<td>${descripcion(c40800)}</td>
			<td>${notas(c40800)}</td>
			<td>${saldo(c40800)}</td>
			<td>${saldo(c40800,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c40900)}</td>
			<td>${descripcion(c40900)}</td>
			<td>${notas(c40900)}</td>
			<td>${saldo(c40900)}</td>
			<td>${saldo(c40900,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41000)}</td>
			<td>${descripcion(c41000)}</td>
			<td>${notas(c41000)}</td>
			<td>${saldo(c41000)}</td>
			<td>${saldo(c41000,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41100)}</td>
			<td>${descripcion(c41100)}</td>
			<td>${notas(c41100)}</td>
			<td>${saldo(c41100)}</td>
			<td>${saldo(c41100,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41200)}</td>
			<td>${descripcion(c41200)}</td>
			<td>${notas(c41200)}</td>
			<td>${saldo(c41200)}</td>
			<td>${saldo(c41200,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41300)}</td>
			<td>${descripcion(c41300)}</td>
			<td>${notas(c41300)}</td>
			<td>${saldo(c41300)}</td>
			<td>${saldo(c41300,1)}</td>
		</tr>
		<tr style="font-weight: bold;">
			<td>${codigo(c49100)}</td>
			<td>${descripcion(c49100)}</td>
			<td>${notas(c49100)}</td>
			<td>${saldo(c49100)}</td>
			<td>${saldo(c49100,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41400)}</td>
			<td>${descripcion(c41400)}</td>
			<td>${notas(c41400)}</td>
			<td>${saldo(c41400)}</td>
			<td>${saldo(c41400,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41430)}</td>
			<td style="padding-left: 20px;">${descripcion(c41430)}</td>
			<td>${notas(c41430)}</td>
			<td>${saldo(c41430)}</td>
			<td>${saldo(c41430,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41490)}</td>
			<td style="padding-left: 20px;">${descripcion(c41490)}</td>
			<td>${notas(c41490)}</td>
			<td>${saldo(c41490)}</td>
			<td>${saldo(c41490,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41500)}</td>
			<td>${descripcion(c41500)}</td>
			<td>${notas(c41500)}</td>
			<td>${saldo(c41500)}</td>
			<td>${saldo(c41500,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41600)}</td>
			<td>${descripcion(c41600)}</td>
			<td>${notas(c41600)}</td>
			<td>${saldo(c41600)}</td>
			<td>${saldo(c41600,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41700)}</td>
			<td>${descripcion(c41700)}</td>
			<td>${notas(c41700)}</td>
			<td>${saldo(c41700)}</td>
			<td>${saldo(c41700,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41800)}</td>
			<td>${descripcion(c41800)}</td>
			<td>${notas(c41800)}</td>
			<td>${saldo(c41800)}</td>
			<td>${saldo(c41800,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c42100)}</td>
			<td>${descripcion(c42100)}</td>
			<td>${notas(c42100)}</td>
			<td>${saldo(c42100)}</td>
			<td>${saldo(c42100,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c42110)}</td>
			<td>${descripcion(c42110)}</td>
			<td>${notas(c42110)}</td>
			<td>${saldo(c42110)}</td>
			<td>${saldo(c42110,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c42120)}</td>
			<td>${descripcion(c42120)}</td>
			<td>${notas(c42120)}</td>
			<td>${saldo(c42120)}</td>
			<td>${saldo(c42120,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c42130)}</td>
			<td>${descripcion(c42130)}</td>
			<td>${notas(c42130)}</td>
			<td>${saldo(c42130)}</td>
			<td>${saldo(c42130,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c49200)}</td>
			<td>${descripcion(c49200)}</td>
			<td>${notas(c49200)}</td>
			<td>${saldo(c49200)}</td>
			<td>${saldo(c49200,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c49300)}</td>
			<td>${descripcion(c49300)}</td>
			<td>${notas(c49300)}</td>
			<td>${saldo(c49300)}</td>
			<td>${saldo(c49300,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c41900)}</td>
			<td>${descripcion(c41900)}</td>
			<td>${notas(c41900)}</td>
			<td>${saldo(c41900)}</td>
			<td>${saldo(c41900,1)}</td>
		</tr>
		<tr>
			<td>${codigo(c49500)}</td>
			<td>${descripcion(c49500)}</td>
			<td>${notas(c49500)}</td>
			<td>${saldo(c49500)}</td>
			<td>${saldo(c49500,1)}</td>
		</tr>
	</table>
</div>
</body>
</html>