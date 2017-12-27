package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod390Key implements IFiscalModelKey  {
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF

	 CM_003("303-CM003",null,"Porcentaje de prorrata.")
	,CM_004("303-CM004",null,"Tipo de declaraci\u00F3n")
	
	// 	----------------------------------------------------------------------------------  
	// 	--------------------------------------------------------------------  BIZKAIA ----
	// 	----------------------------------------------------------------------------------
	,BZ_C001D("390-BZ001D","01" ,"Fraccionamiento de per\u00EDodo en concursal. Desde.")
	,BZ_C001H("390-BZ001H","01" ,"Fraccionamiento de per\u00EDodo en concursal. Hasta.")
	,BZ_C003 ("390-BZ003" ,"03" ,"Sujeto pasivo acogido al r\u00E9gimen especial del criterio de caja.")
	,BZ_C004 ("390-BZ004" ,"04" ,"Destinatario/a de operaciones a las que se aplica el r\u00E9gimen especial del criterio de caja")
	,BZ_C005 ("390-BZ005" ,"05" ,"Opci\u00F3n por la aplicaci\u00F3n de la prorrata especial")
	,BZ_C006 ("390-BZ006" ,"06" ,"Revocaci\u00F3n de la opci\u00F3n por la aplicaci\u00F3n de la prorrata especial")
	,BZ_C007 ("390-BZ007" ,"07" ,"Aplicar el r\u00E9gimen especial art. 163 Sexies. Cinco de la NF del IVA")
	,BZ_C080 ("390-BZ080" ,"80" ,"RDM o Grupos de entidades")
	,BZ_C020("390-BZ020"  ,"20" ,"R\u00E9gimen general - Base imponible")
	,BZ_X020("390-BZX020" ,null ,"R\u00E9gimen general - Tipo %")
	,BZ_C021("390-BZ021"  ,"21" ,"R\u00E9gimen general - Cuota")
	,BZ_C022("390-BZ022"  ,"22" ,"R\u00E9gimen general - Base imponible")
	,BZ_X022("390-BZX022" ,null ,"R\u00E9gimen general - Tipo %")
	,BZ_C023("390-BZ023"  ,"23" ,"R\u00E9gimen general - Cuota")
	,BZ_C024("390-BZ024"  ,"24" ,"R\u00E9gimen general - Base imponible")
	,BZ_X024("390-BZX024" ,null ,"R\u00E9gimen general - Tipo %")
	,BZ_C025("390-BZ025"  ,"25" ,"R\u00E9gimen general - Cuota")
	,BZ_C026("390-BZ026"  ,"26" ,"Operaciones intragrupo - Base imponible")
	,BZ_X026("390-BZX026" ,null ,"Operaciones intragrupo - Tipo %")
	,BZ_C027("390-BZ027"  ,"27" ,"Operaciones intragrupo - Cuota")
	,BZ_C028("390-BZ028"  ,"28" ,"Operaciones intragrupo - Base imponible")
	,BZ_X028("390-BZX028" ,null ,"Operaciones intragrupo - Tipo %")
	,BZ_C029("390-BZ029"  ,"29" ,"Operaciones intragrupo - Cuota")
	,BZ_C030("390-BZ030"  ,"30" ,"Operaciones intragrupo - Base imponible")
	,BZ_X030("390-BZX030" ,null ,"Operaciones intragrupo - Tipo %")
	,BZ_C031("390-BZ031"  ,"31" ,"Operaciones intragrupo - Cuota")
	,BZ_C032("390-BZ032"  ,"32" ,"Recargo equivalencia - Base imponible")
	,BZ_X032("390-BZX032" ,null ,"Recargo equivalencia - Tipo %")
	,BZ_C033("390-BZ033"  ,"33" ,"Recargo equivalencia - Cuota")
	,BZ_C034("390-BZ034"  ,"34" ,"Recargo equivalencia - Base imponible")
	,BZ_X034("390-BZX034" ,null ,"Recargo equivalencia - Tipo %")
	,BZ_C035("390-BZ035"  ,"35" ,"Recargo equivalencia - Cuota")
	,BZ_C036("390-BZ036"  ,"36" ,"Recargo equivalencia - Base imponible")
	,BZ_X036("390-BZX036" ,null ,"Recargo equivalencia - Tipo %")
	,BZ_C037("390-BZ037"  ,"37" ,"Recargo equivalencia - Cuota")
	,BZ_C038("390-BZ038"  ,"38" ,"Recargo equivalencia - Base imponible")
	,BZ_X038("390-BZX038" ,null ,"Recargo equivalencia - Tipo %")
	,BZ_C039("390-BZ039"  ,"39" ,"Recargo equivalencia - Cuota")
	,BZ_C040("390-BZ040"  ,"40" ,"Adquisiciones intracomunitarias - Base imponible")
	,BZ_C041("390-BZ041" ,"41" ,"Adquisiciones intracomunitarias - Cuota")
	,BZ_C042("390-BZ042" ,"42" ,"IVA devengado por inversi\u00F3n del sujeto pasivo - Base imponible")
	,BZ_C043("390-BZ043" ,"43" ,"IVA devengado por inversi\u00F3n del sujeto pasivo - Cuota")
	,BZ_C044("390-BZ044" ,"44" ,"Modificaci\u00F3n de bases y cuotas, general - Base imponible")
	,BZ_C045("390-BZ045" ,"45" ,"Modificaci\u00F3n de bases y cuotas, general - Cuota")
	,BZ_C046("390-BZ046" ,"46" ,"Modificaci\u00F3n de bases y cuotas, art\u00EDculo 80.3 y 80.4 NFIVA - Base imponible")
	,BZ_C047("390-BZ047" ,"47" ,"Modificaci\u00F3n de bases y cuotas, art\u00EDculo 80.3 y 80.4 NFIVA - Cuota")
	,BZ_C048("390-BZ048" ,"48" ,"Total cuota devengada")
	,BZ_C060("390-BZ060" ,"60" ,"IVA deducible en operaciones interiores, excluidas op. intragrupo")
	,BZ_C061("390-BZ061" ,"61" ,"IVA deducible en operaciones intragrupo")
	,BZ_C062("390-BZ062" ,"62" ,"IVA deducible en importaciones")
	,BZ_C063("390-BZ063" ,"63" ,"IVA deducible en adquisiciones intracomunitarias")
	,BZ_C064("390-BZ064" ,"64" ,"Compensaciones R\u00E9gimen Especial A.G. y P .")
	,BZ_C065("390-BZ065" ,"65" ,"Regularizaci\u00F3n Inversiones")
	,BZ_C066("390-BZ066" ,"66" ,"Total a deducir")
	,BZ_C095("390-BZ095" ,"95" ,"Diferencia")
	,BZ_C120("390-BZ120" ,"120","Regularizaci\u00F3n de cuotas (art.80.cinco.5a Norma Foral del IVA)")
	,BZ_C081("390-BZ081" ,"81" ,"Volumen de operaciones. Territ. com\u00FAn")
	,BZ_C082("390-BZ082" ,"82" ,"Porcentaje de tributaci\u00F3n. Territ. com\u00FAn")
	,BZ_C083("390-BZ083" ,"83" ,"Volumen de operaciones. \u00C1lava")
	,BZ_C084("390-BZ084" ,"84" ,"Porcentaje de tributaci\u00F3n. \u00C1lava")
	,BZ_C085("390-BZ085" ,"85" ,"Volumen de operaciones. Gipuzkoa")
	,BZ_C086("390-BZ086" ,"86" ,"Porcentaje de tributaci\u00F3n. Gipuzkoa")
	,BZ_C087("390-BZ087" ,"87" ,"Volumen de operaciones. Bizkaia")
	,BZ_C088("390-BZ088" ,"88" ,"Porcentaje de tributaci\u00F3n. Bizkaia")
	,BZ_C089("390-BZ089" ,"89" ,"Volumen de operaciones. Navarra")
	,BZ_C090("390-BZ090" ,"90" ,"Porcentaje de tributaci\u00F3n. Navarra")
	,BZ_C091("390-BZ091" ,"91" ,"Volumen de operaciones. Total.")
	,BZ_C092("390-BZ092" ,"92" ,"Porcentaje de tributaci\u00F3n.Total.")
	,BZ_C096("390-BZ096" ,"96" ,"Cuota atribuible a Bizkaia")
	,BZ_C097("390-BZ097" ,"97" ,"Cuota a compensar de ejercicios anuales anteriores")
	,BZ_C098("390-BZ098" ,"98" ,"Diferencia")
	,BZ_C099("390-BZ099" ,"99" ,"Ingresos efectuados en le Dip. Foral de Bizkaia")
	,BZ_C100("390-BZC100" ,"100","Devoluciones practicadas en la Dip. Foral de Bizkaia")
	,BZ_C110("390-BZC110" ,"110","Resultado")
	,BZ_C112("390-BZC112" ,"112","A compensar")
	,BZ_C113("390-BZC113" ,"113","A devolver")
	,BZ_C114("390-BZC114" ,"114","A ingresar")
	,BZ_C115("390-BZC115" ,"115","Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: ingresado anteriormente")
	,BZ_C116("390-BZC116" ,"116","Cumplimentar s\u00F3lo en caso de que se trate de una autoliquidaci\u00F3n complementaria: devuelto anteriormente")
	,BZ_C117("390-BZC117" ,"117","Total deuda tributaria")
	,BZ_C130("390-BZC130" ,"130","Criterio de Caja. Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo - Base Imponible")
	,BZ_C131("390-BZC131" ,"131","Criterio de Caja. Importes de las entregas de bienes y prestaciones de servicios a las que habi\u00E9ndoles sido aplicado el R\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo - Cuota")
	,BZ_C132("390-BZC132" ,"132","Criterio de Caja. Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja - Base Imponible")
	,BZ_C133("390-BZC133" ,"133","Criterio de Caja. Importes de adquisiciones de bienes y servicios a las que sea de aplicaci\u00F3n o afecte el R\u00E9gimen especial del criterio de caja - Cuota")
	,BZ_C140("390-BZC140" ,"140","Existencias iniciales (1 de enero)")
	,BZ_C141("390-BZC141" ,"141","Existencias finales (31 de diciembre)")
	,BZ_C142("390-BZC142" ,"142","Compras de bienes corrientes - Base imponible")
	,BZ_X142("390-BZX142",null ,"Compras de bienes corrientes - Tipo %")
	,BZ_C143("390-BZC143" ,"143","Compras de bienes corrientes - Cuota")
	,BZ_C144("390-BZC144" ,"144","Compras de bienes corrientes - Cuota deducible")
	,BZ_C145("390-BZC145" ,"145","Compras de bienes corrientes - Base imponible")
	,BZ_X145("390-BZX145",null ,"Compras de bienes corrientes - Tipo %")
	,BZ_C146("390-BZC146" ,"146","Compras de bienes corrientes - Cuota")
	,BZ_C147("390-BZC147" ,"147","Compras de bienes corrientes - Cuota deducible")
	,BZ_C148("390-BZC148" ,"148","Compras de bienes corrientes - Base imponible")
	,BZ_X148("390-BZX148",null ,"Compras de bienes corrientes - Tipo %")
	,BZ_C149("390-BZC149" ,"149","Compras de bienes corrientes - Cuota")
	,BZ_C150("390-BZC150" ,"150","Compras de bienes corrientes - Cuota deducible")
	,BZ_C151("390-BZC151" ,"151","Compras de bienes corrientes - Base imponible")
	,BZ_C152("390-BZC152" ,"152","Compras de bienes corrientes - Cuota")
	,BZ_C153("390-BZC153" ,"153","Compras de bienes corrientes - Cuota deducible")
	,BZ_C154("390-BZC154" ,"154","Compras de bienes corrientes - Base imponible")
	,BZ_C155("390-BZC155" ,"155","Compras de bienes corrientes - Cuota")
	,BZ_C156("390-BZC156" ,"156","Compras de bienes corrientes - Cuota deducible")
	,BZ_C157("390-BZC157" ,"157","Compras de bienes corrientes - Base imponible")
	,BZ_C158("390-BZC158" ,"158","Compras de bienes corrientes - Cuota")
	,BZ_C159("390-BZC159" ,"159","Compras de bienes corrientes - Cuota deducible")
	,BZ_C160("390-BZC160" ,"160","Gastos - Base imponible")
	,BZ_X160("390-BZX160",null ,"Gastos - Tipo %")
	,BZ_C161("390-BZC161" ,"161","Gastos - Cuota")
	,BZ_C162("390-BZC162" ,"162","Gastos - Cuota deducible")
	,BZ_C163("390-BZC163" ,"163","Gastos - Base imponible")
	,BZ_X163("390-BZX163",null ,"Gastos - Tipo %")
	,BZ_C164("390-BZC164" ,"164","Gastos - Cuota")
	,BZ_C165("390-BZC165" ,"165","Gastos - Cuota deducible")
	,BZ_C166("390-BZC166" ,"166","Gastos - Base imponible")
	,BZ_X166("390-BZX166",null ,"Gastos - Tipo %")
	,BZ_C167("390-BZC167" ,"167","Gastos - Cuota")
	,BZ_C168("390-BZC168" ,"168","Gastos - Cuota deducible")
	,BZ_C169("390-BZC169" ,"169","Gastos - Base imponible")
	,BZ_C170("390-BZC170" ,"170","Gastos - Cuota")
	,BZ_C171("390-BZC171" ,"171","Gastos - Cuota deducible")
	,BZ_C172("390-BZC172" ,"172","Gastos - Base imponible")
	,BZ_C173("390-BZC173" ,"173","Gastos - Cuota")
	,BZ_C174("390-BZC174" ,"174","Gastos - Cuota deducible")
	,BZ_C175("390-BZC175" ,"175","Bienes de inversi\u00F3n - Base imponible")
	,BZ_X175("390-BZX175",null ,"Bienes de inversi\u00F3n - Tipo %")
	,BZ_C176("390-BZC176" ,"176","Bienes de inversi\u00F3n - Cuota")
	,BZ_C177("390-BZC177" ,"177","Bienes de inversi\u00F3n - Cuota deducible")
	,BZ_C178("390-BZC178" ,"178","Bienes de inversi\u00F3n - Base imponible")
	,BZ_X178("390-BZX178",null ,"Bienes de inversi\u00F3n - Tipo %")
	,BZ_C179("390-BZC179" ,"179","Bienes de inversi\u00F3n - Cuota")
	,BZ_C180("390-BZC180" ,"180","Bienes de inversi\u00F3n - Cuota deducible")
	,BZ_C181("390-BZC181" ,"181","Bienes de inversi\u00F3n - Base imponible")
	,BZ_X181("390-BZX181",null ,"Bienes de inversi\u00F3n - Tipo %")
	,BZ_C182("390-BZC182" ,"182","Bienes de inversi\u00F3n - Cuota")
	,BZ_C183("390-BZC183" ,"183","Bienes de inversi\u00F3n - Cuota deducible")
	,BZ_C184("390-BZC184" ,"184","Bienes de inversi\u00F3n - Base imponible")
	,BZ_C185("390-BZC185" ,"185","Bienes de inversi\u00F3n - Cuota")
	,BZ_C186("390-BZC186" ,"186","Bienes de inversi\u00F3n - Cuota deducible")
	,BZ_C187("390-BZC187" ,"187","Bienes de inversi\u00F3n - Base imponible")
	,BZ_C188("390-BZC188" ,"188","Bienes de inversi\u00F3n - Cuota")
	,BZ_C189("390-BZC189" ,"189","Bienes de inversi\u00F3n - Cuota deducible")
	,BZ_C190("390-BZC190" ,"190","Total - Base imponible")
	,BZ_C191("390-BZC191" ,"191","Total - Cuota")
	,BZ_C192("390-BZC192" ,"192","Total - Cuota deducible")
	,BZ_C193("390-BZC193" ,"193","Prorrata general")
	,BZ_C194("390-BZC194" ,"194","Prorrata especial")
	,BZ_C200("390-BZC200" ,"200","Operaciones en r\u00E9gimen general")
	,BZ_C201("390-BZC201" ,"201","Operaciones a las que habi\u00E9ndoles sido aplicado el r\u00E9gimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 de la NFIVA")
	,BZ_C202("390-BZC202" ,"202","Entregas intracomunitarias exentas")
	,BZ_C203("390-BZC203" ,"203","Exportaciones y otras operaciones exentas con derecho a deducci\u00F3n")
	,BZ_C204("390-BZC204" ,"204","Operaciones exentas sin derecho a deducci\u00F3n") 
	,BZ_C205("390-BZC205" ,"205","Operaciones no sujetas por reglas de localizaci\u00F3n")
	,BZ_C206("390-BZC206" ,"206","Operaciones con inversi\u00F3n del sujeto pasivo")
	,BZ_C207("390-BZC207" ,"207","Entregas de bienes objeto de instalaci\u00F3n o montaje en otros Estados miembros")
	,BZ_C208("390-BZC208" ,"208","Operaciones en r\u00E9gimen simplificado")
	,BZ_C209("390-BZC209" ,"209","Operaciones en r\u00E9gimen especial de la agricultura, ganader\u00EDa o pesca")
	,BZ_C210("390-BZC210" ,"210","Operaciones realizadas por sujetos pasivos acogidos al r\u00E9gimen especial del recargo de equivalencia")
	,BZ_C211("390-BZC211" ,"211","Operaciones en r\u00E9gimen especial de bienes usados, objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n")
	,BZ_C212("390-BZC212" ,"212","Operaciones en r\u00E9gimen especial de agencias de viajes")
	,BZ_C213("390-BZC213" ,"213","Entregas de bienes inmuebles y operaciones financieras no habituales")
	,BZ_C214("390-BZC214" ,"214","Entregas de bienes de inversi\u00F3n")
	,BZ_C215("390-BZC215" ,"215","Total volumen de operaciones")
	,BZ_C220("390-BZC220" ,"220","Otras operaciones no sujetas con derecho a deducci\u00F3n")
	,BZ_C221("390-BZC221" ,"221","Otras operaciones no sujetas sin derecho a deducci\u00F3n")
	,BZ_C222("390-BZC222" ,"222","Subvenciones a la explotaci\u00F3n")
	,BZ_C223("390-BZC223" ,"223","Subvenciones de capital percibidas en el ejercicio")
	,BZ_SE1N("390-BZSE1N",null,"1.- Facturas emitidas. Serie")
	,BZ_SE1D("390-BZSE1D",null,"1.- Facturas emitidas. N. Inicio")
	,BZ_SE1H("390-BZSE1H",null,"1.- Facturas emitidas. N. Terminaci\u00F3n")
	,BZ_SE2N("390-BZSE2N",null,"2.- Facturas emitidas. Serie")
	,BZ_SE2D("390-BZSE2D",null,"2.- Facturas emitidas. N. Inicio")
	,BZ_SE2H("390-BZSE2H",null,"2.- Facturas emitidas. N. Terminaci\u00F3n")
	,BZ_SE3N("390-BZSE3N",null,"3.- Facturas emitidas. Serie")
	,BZ_SE3D("390-BZSE3D",null,"3.- Facturas emitidas. N. Inicio")
	,BZ_SE3H("390-BZSE3H",null,"3.- Facturas emitidas. N. Terminaci\u00F3n")
	,BZ_SE4N("390-BZSE4N",null,"4.- Facturas emitidas. Serie")
	,BZ_SE4D("390-BZSE4D",null,"4.- Facturas emitidas. N. Inicio")
	,BZ_SE4H("390-BZSE4H",null,"4.- Facturas emitidas. N. Terminaci\u00F3n")
	,BZ_SE5N("390-BZSE5N",null,"5.- Facturas emitidas. Serie")
	,BZ_SE5D("390-BZSE5D",null,"5.- Facturas emitidas. N. Inicio")
	,BZ_SE5H("390-BZSE5H",null,"5.- Facturas emitidas. N. Terminaci\u00F3n")
	,BZ_SR1N("390-BZSR1N",null,"1.- Facturas recibidas. Serie")
	,BZ_SR1D("390-BZSR1D",null,"1.- Facturas recibidas. N. Inicio")
	,BZ_SR1H("390-BZSR1H",null,"1.- Facturas recibidas. N. Terminaci\u00F3n")
	,BZ_SR2N("390-BZSR2N",null,"2.- Facturas recibidas. Serie")
	,BZ_SR2D("390-BZSR2D",null,"2.- Facturas recibidas. N. Inicio")
	,BZ_SR2H("390-BZSR2H",null,"2.- Facturas recibidas. N. Terminaci\u00F3n")
	,BZ_SR3N("390-BZSR3N",null,"3.- Facturas recibidas. Serie")
	,BZ_SR3D("390-BZSR3D",null,"3.- Facturas recibidas. N. Inicio")
	,BZ_SR3H("390-BZSR3H",null,"3.- Facturas recibidas. N. Terminaci\u00F3n")
	,BZ_SR4N("390-BZSR4N",null,"4.- Facturas recibidas. Serie")
	,BZ_SR4D("390-BZSR4D",null,"4.- Facturas recibidas. N. Inicio")
	,BZ_SR4H("390-BZSR4H",null,"4.- Facturas recibidas. N. Terminaci\u00F3n")
	,BZ_SR5N("390-BZSR5N",null,"5.- Facturas recibidas. Serie")
	,BZ_SR5D("390-BZSR5D",null,"5.- Facturas recibidas. N. Inicio")
	,BZ_SR5H("390-BZSR5H",null,"5.- Facturas recibidas. N. Terminaci\u00F3n")
	,BZ_P1C ("390-BZP1C" ,null,"CNAE")
	,BZ_P1I ("390-BZP1I" ,null,"Imp. tot. Operaciones ")
	,BZ_P1D ("390-BZP1D" ,null,"Imp. tot. Oper. con der. ded.")
	,BZ_P1T ("390-BZP1T" ,null,"Tipo")
	,BZ_P1P ("390-BZP1P" ,null,"% prorrata")
	,BZ_P2C ("390-BZP2C" ,null,"CNAE")
	,BZ_P2I ("390-BZP2I" ,null,"Imp. tot. Operaciones ")
	,BZ_P2D ("390-BZP2D" ,null,"Imp. tot. Oper. con der. ded.")
	,BZ_P2T ("390-BZP2T" ,null,"Tipo")
	,BZ_P2P ("390-BZP2P" ,null,"% prorrata")
	,BZ_P3C ("390-BZP3C" ,null,"CNAE")
	,BZ_P3I ("390-BZP3I" ,null,"Imp. tot. Operaciones ")
	,BZ_P3D ("390-BZP3D" ,null,"Imp. tot. Oper. con der. ded.")
	,BZ_P3T ("390-BZP3T" ,null,"Tipo")
	,BZ_P3P ("390-BZP3P" ,null,"% prorrata")
	,BZ_P4C ("390-BZP4C" ,null,"CNAE")
	,BZ_P4I ("390-BZP4I" ,null,"Imp. tot. Operaciones ")
	,BZ_P4D ("390-BZP4D" ,null,"Imp. tot. Oper. con der. ded.")
	,BZ_P4T ("390-BZP4T" ,null,"Tipo")
	,BZ_P4P ("390-BZP4P" ,null,"% prorrata")
	,BZ_P5C ("390-BZP5C" ,null,"CNAE")
	,BZ_P5I ("390-BZP5I" ,null,"Imp. tot. Operaciones ")
	,BZ_P5D ("390-BZP5D" ,null,"Imp. tot. Oper. con der. ded.")
	,BZ_P5T ("390-BZP5T" ,null,"Tipo")
	,BZ_P5P ("390-BZP5P" ,null,"% prorrata")
	
	;
	private String value;
	private String box;
	private String description;
	
	private Mod390Key(String value,String box,String description) {
		this.value = value;
		this.box = box;
		this.description = description;
	}
    
	@Override
	public String getValue() {
		return value;
	}
	@Override
	public int getBox() {
		if (AonStringUtils.isNumeric(box)) {
			return AonNumberUtils.toint(box);
		}
		return 0;
	}
	public String getBoxCode() {
		if (AonStringUtils.isNumeric(box)) {
			return AonStringUtils.leftPad(box, 3, AonStringUtils.ZERO); 
		}
		return box;
	}
	public String getDescription() {
		return description;
	}
	
	public static Mod390Key getKey(String value) {
		for (Mod390Key key : Mod390Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		for (Mod390Key key : Mod390Key.values() ) {
			int count = 0;
			for (Mod390Key key2 : Mod390Key.values() ) {
				count += AonStringUtils.equals(key.getValue(),key2.getValue())?1:0;
			}
			if (count != 1) {
				System.out.println( "," +key + "\t" + count);
			}
		}
		System.out.println( "END");
	}

}
