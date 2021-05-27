<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:in="http://www.inteco.es/xbrl/pgc07/interfazES"
	xmlns:map="http://www.inteco.es/xbrl/pgc07/mapa" xmlns:gl="http://www.inteco.es/xbrl/pgc07/identificadores"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.pnp-software.com/XSLTdoc"
	xmlns:fnc="http://fnc" exclude-result-prefixes="#all">

	<!-- Format a Number as String with Commas Separation -->
	<xsl:decimal-format name="decimalFormatter" decimal-separator="," grouping-separator="."/>
	<xsl:function name="fnc:formatNumberWithDecimals" as="xs:string">
		<xsl:param name="numberAsString" as="xs:string" />
		<xsl:variable name="numberDouble" select="number($numberAsString) div 100" as="xs:double"/>
		<xsl:value-of select="format-number($numberDouble, '#.##0,00;(#.##0,00)', 'decimalFormatter')" />
	</xsl:function>
	
	<!-- The ID of the module to process. -->
	<xsl:param name="module" />

	<xd:doc type="stylesheet">
		<xd:short>Converts the PGC XML format into HTML.</xd:short>
		<xd:detail>This stylesheet takes the XML reporting format for the PGC
			XBRL forms and produces an HTML output view.
			The stylesheet takes two parameters - the module to transform and the
			path to the presentation configuration files.
		</xd:detail>
		<xd:author>Andromeda Wood</xd:author>
	</xd:doc>

	<xsl:output method="xhtml" encoding="ISO-8859-1" indent="yes"
		exclude-result-prefixes="#all" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<!-- Output method xhtml. Encoding UTF8. References to the xhtml DTD. -->
	</xsl:output>
	
	<!-- This variable contains the global configuration information document. -->
	<xsl:variable name="global" select="document('global.xml')" />
	
	<xsl:variable name="report" select="/in:report/@id"><!--The ID for this report -->
	</xsl:variable>
	<xd:doc>
		<xd:short>The template matching the root element of the XML input format.</xd:short>
		<xd:detail>This template matches the root of the XML format and starts the processign.
			It creates the html element, head information and the body element. It then applies 
			templates to all children.
		</xd:detail>
	</xd:doc>
	
	<xsl:template match="/">
		<html xml:lang="es" lang="es">
			<!-- Create the html element, head element and body element. Add the content 
				meta to the head along with the title and CSS link. -->
			<head>
				<meta content="text/html;charset=ISO-8859-1" http-equiv="Content-Type" />
				<title>
					<xsl:value-of select="$module"/> 
					- REPORT: <xsl:value-of select="$report"/>
				</title>
				<style type="text/css">
html, body {border:0;margin:0;padding:0;}
body {font-family: "arial","lucida Grande","Trebuchet MS",sans-serif;font-size: 10px;color:#4b4b4b;background-color:#fff;overflow:visible; padding: 5px;}
h1 {font-size: 1.3em;text-align: center;}
.data_container {width: 100%;}
table {margin-top: 10px;margin-bottom: 10px;border-collapse:collapse;width: 100%;}
table tr td {border: solid #888888 0.5px;padding: 3px;empty-cells: show;}
table tr th {padding: 5px 1em;font-weight:bold;text-transform:uppercase;background-color: #EEEEEE;border: #888888 1px solid; text-align: center;}
table.header{ }
table.header .innerHeaderContainer{ width: auto; }
table.header .innerHeader{ width: 100%; }
table.header .innerHeader .label {white-space: nowrap; font-size: 0.8em;font-style: italic; }
table.header .innerHeader .value {padding-left: 10px;font-weight:bold; font-size: 1.1em;}
table.header .innerHeader tr td {border: none;}
table.header .signSpace {width: 45%; vertical-align: middle;text-align:center; font-size: 0.9em;font-style: italic;}
table.header .units{ width: 150px; }
table.header .units caption{ font-style: italic; padding-bottom: 5px; font-size: 0.9em;}
table.header .units table { width: 90%;  }
table.header .units table tr td:first-child {width: 40%; white-space: nowrap; font-size: 0.8em;font-style: italic; }
table.header .units table tr td:nth-last-child(2) {width: 40%; text-align:center; font-weight: bold;}
table.header .units table tr td:nth-last-child(1) {width: 20%; text-align:center; font-weight: bold;}
table.data tr:nth-child(odd) {background-color: #F8F8F8;}
table.data tbody tr:hover {background-color: #FBEDB6;}
table.data tr td:first-child {width: auto;}
table.data tr td:nth-last-child(3) {width: 150px;}
table.data tr td:nth-last-child(2) {width: 200px;}
table.data tr td:nth-last-child(1) {width: 200px;}

table.data .itemDescription {}
table.data .level0 { padding-left: 5px;line-height: 2.0em; font-weight: bold; font-size: 1.1em;}
table.data .level1 { padding-left: 10px;line-height: 1.5em; font-weight: bold;}
table.data .level2 { padding-left: 20px;}
table.data .level3 { padding-left: 30px;}
table.data .level4 { padding-left: 40px;}
table.data .level5 { padding-left: 50px;}
table.data .itemCode {font-weight:bold; text-align:center;} 
table.data .unitsAligment {text-align:right;padding-right: 10px;}
				</style>
			</head>
			<body>
				<div class="data_container">
					<xsl:apply-templates />
				</div>
			</body>
		</html>
	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the root report element of the XML
			input format.</xd:short>
		<xd:detail>This template matches the report element. It then extracts
			information required for all statement processign such as reporting
			dates and header information.
			Finally it calls the relevant template for the transformation of the
			specified module.
		</xd:detail>
	</xd:doc>
	
	<xsl:template match="in:report">
		
		<xsl:variable name="reportingDates" as="node()*">
			<!-- This variable extracts the two reporting end dates for the report. 
				The end dates are used rather than the start date as they are required information. -->
			<xsl:choose>
				<xsl:when test="count(in:module) &gt; 1">
					<!-- When there are several modules reported then process the dates by ID -->
					<xsl:for-each-group select="in:module" group-by="@id">
						<!-- Group the modules by ID so we can see what has been reported and in how many periods. -->
						<xsl:if test="count(current-group()) &gt; 0">
							<!-- If there are two module elements reported for this statement 
								then it has been reported for both periods and we can get the necessary two 
								dates. -->
							<xsl:for-each select="current-group()">
								<xsl:if test="@id=$module">
									<!-- Foreach member of the current group get the reportingEndDate 
										attribute. -->
									<xsl:sequence select="@reportingDateEnd"></xsl:sequence>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each-group>
				</xsl:when>
				<xsl:otherwise>
					<!-- Otherwise get the dates available -->
					<xsl:sequence select="in:module/@reportingDateEnd"></xsl:sequence>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="reportingDatesAscending" as="node()*">
			<xsl:perform-sort select="$reportingDates">
				<xsl:sort select="." order="descending" />
			</xsl:perform-sort>
		</xsl:variable>

		<xsl:variable name="currentReportingDate">
			<!-- Now in this variable we determine which of the two dates is the later 
				and is therefore the reporting date for the current period and store it in 
				this variable. -->
			<xsl:choose>
				<!-- Choose based on date comparisons. -->
				<xsl:when test="count($reportingDatesAscending) &gt; 0">
					<!-- If this XML submission doesn't have a large enough number of statements 
						to do the comparison then assume the dates are for the current period. -->
					<xsl:value-of select="$reportingDatesAscending[1]"/>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$currentReportingDate!=''">
			<xsl:variable name="previousReportingDate">
				<!-- Now in this variable we determine which of the two dates is the 
					later and is therefore the reporting date for the current period and store 
					it in this variable. -->
				<xsl:choose>
					<!-- Choose based on date comparisons. -->
					<xsl:when test="count($reportingDatesAscending) &gt; 1">
						<!-- If this XML submission doesn't have a large enough number of statements 
							to do the comparison then assume the dates are for the current period. -->
						<xsl:value-of select="$reportingDatesAscending[2]"/>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="NIF"
				select="/in:report/in:module[@id = 'apartado0']/in:record[@id = '0100000']/in:item[@id = '01010']/in:value"><!-- The NIF (if reported) for the module headers. -->
			</xsl:variable>

			<!--mod mar-09 presentar celda 01902 -->
			<xsl:variable name="code01902Aux">
				<xsl:choose>
					<xsl:when test="$currentReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate) and  @id = 'apartado0' ]/in:item[@id = '01902']/in:value"></xsl:value-of>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>


			<xsl:variable name="code09001Unit">
				<xsl:choose>
					<xsl:when test="$currentReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09001']/in:value"></xsl:value-of>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="code09002Unit">
				<xsl:choose>
					<xsl:when test="$currentReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09002']/in:value"></xsl:value-of>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:variable name="code09003Unit">
				<xsl:choose>
					<xsl:when test="$currentReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09003']/in:value"></xsl:value-of>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="code09001UnitPrevious">
				<xsl:choose>
					<xsl:when test="$previousReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($previousReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09001']/in:value"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="code09002UnitPrevious">
				<xsl:choose>
					<xsl:when test="$previousReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($previousReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09002']/in:value"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="code09003UnitPrevious">
				<xsl:choose>
					<xsl:when test="$previousReportingDate castable as xs:date">
						<xsl:value-of
							select="/in:report/in:module[year-from-date(@reportingDateEnd) = year-from-date($previousReportingDate) and  @id = 'apartado0' ]/in:record[@id = '0900000']/in:item[@id = '09003']/in:value"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="code01902">
				<xsl:choose>

					<xsl:when test="$code01902Aux = 'true'  ">
						<xsl:text>SI</xsl:text>
					</xsl:when>

					<xsl:when test="$code01902Aux = 'false'  ">
						<xsl:text>NO</xsl:text>
					</xsl:when>

					<xsl:otherwise>
						<xsl:value-of select="$code01902Aux"></xsl:value-of>
					</xsl:otherwise>

				</xsl:choose>
			</xsl:variable>

			<xsl:variable name="DS" select="/in:report/in:entity/@id"/>

			<xsl:choose>
				<!-- Now we choose which templates to call based on which module has 
					been selected for transformation. -->
				<xsl:when test="$module = 'apartado0'">
					<!-- When the module is apartado 0 - the entity information - apply 
						the templates with the "information" mode. -->
					<xsl:apply-templates mode="information"
						select="in:module[@id = 'apartado0'][1]">
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"><!-- 
								The reporting date for the current period. -->
						</xsl:with-param>
						<xsl:with-param name="previousReportingDate"
							select="$previousReportingDate"><!-- The reporting date for the previous period -->
						</xsl:with-param>

						<!--mod mar-09 presentar celda 01902 -->
						<xsl:with-param name="code01902" select="$code01902"></xsl:with-param>

					</xsl:apply-templates>
				</xsl:when>
				
				<xsl:when test="$module = ('bal','pyg','patnetA','flujefec')">
					<!-- When the module is any of the non-dimensional modules process by 
						calling the general processModule template selecting the current module. -->
					<xsl:call-template name="processModule">
						<xsl:with-param name="modules" select="in:module[@id=$module]"/>
						<xsl:with-param name="NIF" select="$NIF"/>
						<xsl:with-param name="DS" select="$DS"/>
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"/>
						<xsl:with-param name="previousReportingDate" select="$previousReportingDate"/>
						<xsl:with-param name="code01902" select="$code01902"></xsl:with-param>
						<xsl:with-param name="code09001Unit" select="$code09001Unit"/>
						<xsl:with-param name="code09002Unit" select="$code09002Unit"/>
						<xsl:with-param name="code09003Unit" select="$code09003Unit"/>
					</xsl:call-template>
				</xsl:when>
				
				<xsl:when test="$module = 'patnetB'">
					<!-- When the module is the dimensional patNetB module then apply templates usign the "dimensional" mode. -->
					<xsl:apply-templates select="in:module[@id = 'patnetB']" mode="dimensional">
						<xsl:with-param name="NIF" select="$NIF"><!-- The NIF (if reported) 
								for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="DS" select="$DS"><!-- The Denominación 
								social (if reported) for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"><!-- 
								The reporting date for the current period. -->
						</xsl:with-param>
						<xsl:with-param name="previousReportingDate"
							select="$previousReportingDate"><!-- The reporting date for the previous period -->
						</xsl:with-param>
						<xsl:with-param name="code01902" select="$code01902"></xsl:with-param>

						<xsl:with-param name="code09001Unit" select="$code09001Unit"></xsl:with-param>
						<xsl:with-param name="code09002Unit" select="$code09002Unit"></xsl:with-param>
						<xsl:with-param name="code09003Unit" select="$code09003Unit"></xsl:with-param>

						<xsl:with-param name="code09001UnitPrevious"
							select="$code09001UnitPrevious"></xsl:with-param>
						<xsl:with-param name="code09002UnitPrevious"
							select="$code09002UnitPrevious"></xsl:with-param>
						<xsl:with-param name="code09003UnitPrevious"
							select="$code09003UnitPrevious"></xsl:with-param>



					</xsl:apply-templates>

				</xsl:when>



				<!--<xsl:when test="$report = 'pgc-07-a' or $report = 'pgc-07-p' and 
					$module = 'apartado14'"> -->
				<!--Mar-09 nuevas aplicaciones multiventana -->
				<xsl:when
					test="($report = 'pgc-07-a' or $report = 'pgc-07-p'  or $report = 'pgc07abreviado'  or $report = 'pgc07pymes' ) and $module = 'apartado14'">
					<!-- When this is an abbreviated or pymes report and there is a environmental 
						module then call the general process Module template selecting the apartado 
						14 module.. -->
					<xsl:call-template name="processModule">
						<xsl:with-param name="modules" select="in:module[@id='apartado14']"><!-- The module 
								to process -->
						</xsl:with-param>
						<xsl:with-param name="NIF" select="$NIF"><!-- The NIF (if reported) 
								for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="DS" select="$DS"><!-- The Denominación 
								social (if reported) for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"><!-- 
								The reporting date for the current period. -->
						</xsl:with-param>
						<xsl:with-param name="previousReportingDate"
							select="$previousReportingDate"><!-- The reporting date for the previous period -->
						</xsl:with-param>
						<xsl:with-param name="code01902" select="$code01902"></xsl:with-param>

						<xsl:with-param name="code09001Unit" select="$code09001Unit"></xsl:with-param>
						<xsl:with-param name="code09002Unit" select="$code09002Unit"></xsl:with-param>
						<xsl:with-param name="code09003Unit" select="$code09003Unit"></xsl:with-param>
					</xsl:call-template>
				</xsl:when>


				<!--<xsl:when test="$report = 'pgc-07-n' and $module = 'apartado15'"> -->
				<!--Mar-09 nuevas aplicaciones multiventana -->
				<xsl:when
					test=" ($report = 'pgc-07-n' or $report = 'pgc07normal' or $report = 'pgc-07-m'  or $report = 'pgc07mixto') and $module = 'apartado15'">
					<!-- When this is a normal report and there is a environmental module 
						then call the general process Module template selecting the apartado 15 module.. -->
					<xsl:call-template name="processModule">
						<xsl:with-param name="modules" select="in:module[@id='apartado15']"><!-- The module 
								to process -->
						</xsl:with-param>
						<xsl:with-param name="NIF" select="$NIF"><!-- The NIF (if reported) 
								for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="DS" select="$DS"><!-- The Denominación 
								social (if reported) for the module headers. -->
						</xsl:with-param>
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"><!-- 
								The reporting date for the current period. -->
						</xsl:with-param>
						<xsl:with-param name="previousReportingDate"
							select="$previousReportingDate"><!-- The reporting date for the previous period -->
						</xsl:with-param>
						<xsl:with-param name="code01902" select="$code01902"></xsl:with-param>

						<xsl:with-param name="code09001Unit" select="$code09001Unit"></xsl:with-param>
						<xsl:with-param name="code09002Unit" select="$code09002Unit"></xsl:with-param>
						<xsl:with-param name="code09003Unit" select="$code09003Unit"></xsl:with-param>


					</xsl:call-template>
				</xsl:when>

			</xsl:choose>
		</xsl:if>

	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the entity element of the XML input
			format.</xd:short>
		<xd:detail>This template matches the entity element but does not
			perform any processign.</xd:detail>
	</xd:doc>
	<xsl:template match="in:entity">
		<!-- Do nothing with it separately? -->
	</xsl:template>

	<xd:doc>
		<xd:short>The template named processModule template.</xd:short>
		<xd:detail>This template processes the modules for the non-dimensional
			statements (except those usign tuples). It creates the main table and
			then populates the labels from the
			presentation configuration mapping files. The data is extracted from the
			reporting XML.
		</xd:detail>
	</xd:doc>

	<xsl:template name="processModule">
		<xsl:param name="modules"/>
		<xsl:param name="NIF"/>
		<xsl:param name="DS"/>
		<xsl:param name="currentReportingDate"/>
		<xsl:param name="previousReportingDate"/>
		<xsl:param name="code01902"/>
		<xsl:param name="code09001Unit"/>
		<xsl:param name="code09002Unit"/>
		<xsl:param name="code09003Unit"/>

		<!-- The location of the presentation configuration file -->
		<xsl:variable name="configLocation">
			<xsl:choose>
				<xsl:when test="$report = 'pgc07abreviado'">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07normal'">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07pymes'">
					<xsl:value-of select="concat('pgc-07-p-',$module,'-presentation.xml')"/>
				</xsl:when>
				<!-- 20090610 trabajamos con el modelo mixto -->
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module = 'pyg' ">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module != 'pyg' ">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat($report,'-',$module,'-presentation.xml')"/>
				</xsl:otherwise>

			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="config" select="document($configLocation)"/>

		<!-- Process the statements -->
		<h1>
			<xsl:value-of select="$global/gl:global/gl:configReport[@id = $report]/gl:configModule[@id = $module]/@title"/>
		</h1>

		<table class="header">
			<tr>
				<td class="innerHeaderContainer">
					<table class="innerHeader">
						<tr><td class="label">NIF:</td></tr>
						<tr><td class="value"><xsl:value-of select="$NIF"/></td></tr>
						<tr><td class="label">DENOMINACIÓN SOCIAL:</td></tr>
						<tr><td class="value"><xsl:value-of select="$DS"/></td></tr>
					</table>
				</td>

				<td class="signSpace">
					<span>Espacio destinado para las firmas de los administradores</span>
				</td>

				<!--la tabla de unidades no se presenta en pymes -->
				<xsl:if test="$report != 'pgc07pymes' and $report != 'pgc-07-p'">
					<td class="units">
						<table>
							<caption>
								UNIDADES
							</caption>
							<tr>
								<td>Euros</td>
								<td>09001</td>
								<td>X</td>
							</tr>
							<tr>
								<td>Miles</td>
								<td>09002</td>
								<td></td>
							</tr>
							<tr>
								<td>Milliones</td>
								<td>09003</td>
								<td></td>
							</tr>
						</table>
					</td>
				</xsl:if>
			</tr>
		</table>

		<xsl:variable name="currentPeriodYear"
			select="year-from-date($currentReportingDate)"><!-- Get the year from the current reporting period -->
		</xsl:variable>

		<!--no siempre tendremos ejercicio anterior (mar2009) -->
		<!-- <xsl:variable name="previousPeriodYear" select="year-from-date($previousReportingDate)"></xsl:variable> -->


		<xsl:variable name="previousPeriodYear">
			<!-- And in this variable we determine which of the two dates is the earlier 
				and is therefore the reporting date for the previous period and store it 
				in this variable. -->
			<xsl:choose>
				<!-- Choose based on date comparisons. -->
				<xsl:when test="$previousReportingDate castable as xs:date">
					<xsl:value-of select="year-from-date($previousReportingDate)"></xsl:value-of>

				</xsl:when>
				<xsl:otherwise>
					0
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>



		<table class="data">
			<thead>
				<tr>
					<td colspan="2"></td>
					<th>NOTAS de la Memoria</th>
					<th>EJERCICIO <xsl:value-of select="$currentPeriodYear"/></th>
					<th>
						<xsl:choose>
							<xsl:when test="$previousReportingDate castable as xs:date">
								EJERCICIO <xsl:value-of select="$previousPeriodYear"/>
							</xsl:when>
							<xsl:otherwise>
								EJERCICIO ANTERIOR
							</xsl:otherwise>
						</xsl:choose>
					</th>
				</tr>
			</thead>
			
			<tbody>
				<xsl:for-each select="$config/map:statement/map:conceptMap[not(@parent = '') and not(@inputID = '00000')]">
					<xsl:variable name="currentPeriodValue" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/in:value"/>
					<xsl:variable name="previousPeriodValue" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/in:value"/> 
					<xsl:variable name="currentPeriodUnitAux" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/@unit"/>
					<xsl:variable name="currentPeriodDecimalAux" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/@decimals"/>
					<xsl:variable name="currentPeriodSignAux" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/@sign"/>

					<xsl:variable name="currentPeriodUnit">
						<xsl:choose>
							<xsl:when test="$currentPeriodUnitAux!=''">
								<xsl:value-of select="$currentPeriodUnitAux"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseUnit!=''">
										<xsl:value-of select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseUnit"/>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="currentPeriodDecimal">
						<xsl:choose>
							<xsl:when test="$currentPeriodDecimalAux!=''">
								<xsl:value-of select="$currentPeriodDecimalAux"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseDecimals!=''">
										<xsl:value-of
											select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseDecimals"></xsl:value-of>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="currentPeriodSign">
						<xsl:choose>
							<xsl:when test="$currentPeriodSignAux!=''">
								<xsl:value-of select="$currentPeriodSignAux"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseSign!=''">
										<xsl:value-of select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/@baseSign"/>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="previousPeriodUnitAux" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/@unit"/>
					<xsl:variable name="previousPeriodDecimalAux" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/@decimals"/>
					<xsl:variable name="previousPeriodSignAux" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/@sign"/>

					<xsl:variable name="previousPeriodUnit">
						<xsl:choose>
							<xsl:when test="$previousPeriodUnitAux!=''">
								<xsl:value-of select="$previousPeriodUnitAux"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@unit!=''">
										<xsl:value-of
											select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@unit"></xsl:value-of>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="previousPeriodDecimal">
						<xsl:choose>
							<xsl:when test="$previousPeriodDecimalAux!=''">
								<xsl:value-of select="$previousPeriodDecimalAux"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@decimals!=''">
										<xsl:value-of
											select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@decimals"></xsl:value-of>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="previousPeriodSign">
						<xsl:choose>
							<xsl:when test="$previousPeriodSignAux!=''">
								<xsl:value-of select="$previousPeriodSignAux"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@sign!=''">
										<xsl:value-of
											select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/@sign"></xsl:value-of>
									</xsl:when>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>



					<xsl:variable name="currentPeriodDecimalValue">
						<!-- In this variable we determine the number to normalise the unit 
							of the currentPeriodValue . -->
						<xsl:choose>
							<!-- Choose based on number comparisons. -->
							<xsl:when
								test="$currentPeriodDecimal='-3' and $currentPeriodUnit='euro'">
								<xsl:value-of select="1000"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$currentPeriodDecimal='-6' and $currentPeriodUnit='euro'">
										<xsl:value-of select="1000000"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when
												test="$currentPeriodDecimal='0' and $currentPeriodUnit='euro'">
												<xsl:value-of select="0"></xsl:value-of>
											</xsl:when>
											<xsl:otherwise>
												1
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>


					<xsl:variable name="previousPeriodDecimalValue">
						<!-- In this variable we determine the number to normalise the unit 
							of the previousPeriodValue -->
						<xsl:choose>
							<!-- Choose based on number comparisons. -->
							<xsl:when
								test="$previousPeriodDecimal='-3' and $previousPeriodUnit='euro'">
								<xsl:value-of select="1000"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when
										test="$previousPeriodDecimal='-6' and $previousPeriodUnit='euro'">
										<xsl:value-of select="1000000"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when
												test="$previousPeriodDecimal='0' and $previousPeriodUnit='euro'">
												<xsl:value-of select="0"></xsl:value-of>
											</xsl:when>
											<xsl:otherwise>
												1
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>


					<xsl:variable name="currentPeriodUnitValue">
						<xsl:choose>
							<xsl:when test="$currentPeriodDecimalValue=1">
								<xsl:value-of select="$currentPeriodValue"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$currentPeriodDecimalValue='0'">
										<xsl:choose>
											<xsl:when test="$currentPeriodValue!=''">
												<!--xsl:number value="$currentPeriodValue" format="1" grouping-separator 
													="," grouping-size="3"/ -->
												<xsl:number value="$currentPeriodValue" format="1" />
												<!--xsl:value-of select="$currentPeriodValue"></xsl:value-of -->
											</xsl:when>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$currentPeriodValue!=''">
												<xsl:number
													value="$currentPeriodValue div $currentPeriodDecimalValue"
													format="1" />
												<!--xsl:value-of select="$currentPeriodValue div $currentPeriodDecimalValue"></xsl:value-of -->
											</xsl:when>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="previousPeriodUnitValue">
						<xsl:choose>
							<xsl:when test="$previousPeriodDecimalValue=1">
								<xsl:value-of select="$previousPeriodValue"></xsl:value-of>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="$previousPeriodDecimalValue='0'">
										<xsl:choose>
											<xsl:when test="$previousPeriodValue!=''">
												<!--xsl:number value="$previousPeriodValue" format="1" grouping-separator 
													="," grouping-size="3"/ -->
												<xsl:number value="$previousPeriodValue" format="1" />
												<!--xsl:value-of select="$previousPeriodValue"></xsl:value-of -->
											</xsl:when>
										</xsl:choose>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$previousPeriodValue!=''">
												<xsl:number
													value="$previousPeriodValue div $previousPeriodDecimalValue"
													format="1" />
												<!--xsl:value-of select="$previousPeriodValue div $previousPeriodDecimalValue"></xsl:value-of -->
											</xsl:when>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="currentPeriodUnitValueString">
						<xsl:value-of select="$currentPeriodUnitValue" />
					</xsl:variable>

					<xsl:variable name="previousPeriodUnitValueString">
						<xsl:value-of select="$previousPeriodUnitValue" />
					</xsl:variable>

					<xsl:variable name="currentPeriodValuesign">
						<xsl:choose>
							<xsl:when test="$currentPeriodUnitValueString!=''">
								<xsl:value-of select="fnc:formatNumberWithDecimals( concat($currentPeriodSign,$currentPeriodUnitValueString))" />
							</xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="previousPeriodValuesign">
						<xsl:choose>
							<xsl:when test="$previousPeriodUnitValueString!=''">
								<xsl:value-of
									select="fnc:formatNumberWithDecimals(concat($previousPeriodSign,$previousPeriodUnitValueString))" />
							</xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:variable name="currentPeriodNote"
						select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/in:note/@text"></xsl:variable>

					<tr>
						<td class="itemDescription {@class}">
							<xsl:value-of select="@label"/>
						</td>
						<td class="itemCode">
							<xsl:value-of select="@inputID"/>
						</td>
						<td>
							<xsl:value-of select="$currentPeriodNote" />
						</td>
						<td class="unitsAligment">
							<xsl:value-of select="$currentPeriodValuesign" />
						</td>
						<td class="unitsAligment">
							<xsl:value-of select="$previousPeriodValuesign" />
						</td>
					</tr>
				</xsl:for-each>
			</tbody>
		</table>


	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the module element in the information
			mode.</xd:short>
		<xd:detail>This template processes the modules for the apartado 0
			information statements.
			It uses the html templates for this module and applies XSLT templates
			to this XHTML template.
		</xd:detail>
	</xd:doc>

	<xsl:template match="in:module" mode="information">
		<xsl:param name="currentReportingDate"><!-- The reporting date for the current period. -->
		</xsl:param>
		<xsl:param name="previousReportingDate"><!-- The reporting date 
				for the previous period. -->
		</xsl:param>
		<xsl:param name="code01902"></xsl:param>

		<!-- Process apartado0 - the information page -->
		<xsl:variable name="configLocation">
			<xsl:choose>
				<xsl:when test="$report = 'pgc07abreviado'">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07normal'">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07pymes'">
					<xsl:value-of select="concat('pgc-07-p-',$module,'-presentation.xml')"/>
				</xsl:when>


				<!-- 20090610 trabajamos con el modelo mixto -->
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module = 'pyg'  ">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module != 'pyg'  ">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>
				
				<xsl:otherwise>
					<xsl:value-of
						select="concat($report,'-',$module,'-presentation.xml')"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>


		<h1 class="info">DATOS GENERALES DE IDENTIFICACIÓN</h1>
		<!--<xsl:variable name="templatePath" select="concat($report,'-',$module,'-template.html')"></xsl:variable> -->
		<!--mar 09 nuevas instancias multiventana -->

		<!-- The path to the XHTML template -->
		<xsl:variable name="templatePath">
			<xsl:choose>
				<xsl:when test="$report = 'pgc07abreviado'">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07normal'">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07pymes'">
					<xsl:value-of select="concat('pgc-07-p-',$module,'-template.html')"/>
				</xsl:when>


				<!-- 20090610 trabajamos con el modelo mixto -->
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module = 'pyg' ">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-template.html')"/>
				</xsl:when>
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module != 'pyg'  ">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:otherwise>
					<xsl:value-of select="concat($report,'-',$module,'-template.html')"></xsl:value-of>
				</xsl:otherwise>

			</xsl:choose>
		</xsl:variable>






		<xsl:variable name="template" select="document($templatePath)"><!-- The XHTML template 
				for this module. -->
		</xsl:variable>
		<xsl:apply-templates select="$template/xhtml:div"
			mode="information">
			<!-- Apply templates to the enclosign div element in the XHTML template. -->
			<xsl:with-param name="module" select="../in:module[@id = 'apartado0']"
				as="node()*"><!-- The module to be processed. -->
			</xsl:with-param>
			<xsl:with-param name="currentReportingDate" tunnel="yes"
				select="$currentReportingDate"><!-- The reporting date for the current period. -->
			</xsl:with-param>
			<xsl:with-param name="previousReportingDate" tunnel="yes"
				select="$previousReportingDate"><!-- The reporting date for the previous 
					period. -->
			</xsl:with-param>

			<xsl:with-param name="code01902" tunnel="yes" select="$code01902"></xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the XHTML elements in the information
			mode.</xd:short>
		<xd:detail>This template processes the modules for the apartado 0
			information statements via the XHTML template. This template copies
			across the template format populating
			cells where specified by the template which has id attributes referencing
			item codes.
		</xd:detail>
	</xd:doc>
	<xsl:template match="xhtml:*" mode="information">
		<xsl:param name="currentReportingDate" tunnel="yes"><!-- The reporting 
				date for the current period. -->
		</xsl:param>
		<xsl:param name="previousReportingDate" tunnel="yes"><!-- The reporting 
				date for the previous period. -->
		</xsl:param>
		<xsl:param name="module"><!-- The module to be processed. -->
		</xsl:param>

		<xsl:param name="code01902" tunnel="yes"></xsl:param>



		<xsl:choose>
			<!-- Choose the processign for the current XHTML element depending on 
				the presence of attributes and the CSS class. -->
			<xsl:when test="exists(@id)">
				<!-- When there is an id attribute then this is a cell which will contain 
					data. -->
				<xsl:variable name="recordID"
					select="replace(substring-before(@id,'-'),'d','')"><!-- The code for the record element in the XML format -->
				</xsl:variable>



				<xsl:variable name="itemID">
					<!-- Get the code for the item in the XML format. -->
					<xsl:choose>
						<xsl:when test="contains(@id,'-e') or contains(@id, '-m')">
							<!-- Check for the e and m codes indicating date or variable codes. -->
							<xsl:value-of select="substring-before(substring-after(@id,'-'),'-')"></xsl:value-of>
						</xsl:when>
						<xsl:otherwise>
							<!-- Otherwise just extract the code from after the hyphen. -->
							<xsl:value-of select="substring-after(@id,'-')"></xsl:value-of>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:if test="not(@class = 'info')">
					<!-- There are some titles in apartado 0 which vary depending on what 
						is reported. These titles have codes but we do not want to output these in 
						the HTML. We test here is this item has a class of info which is the class 
						used for formatting the titles. If it does not we can carry on and output 
						the code. -->
					<xsl:element name="{name()}">
						<xsl:attribute name="class">code</xsl:attribute>
						<xsl:value-of select="$itemID"></xsl:value-of>
					</xsl:element>
				</xsl:if>
				<xsl:element name="{name()}">
					<xsl:copy-of select="@*" />
					<!-- Now we need the value from the xml file. -->
					<xsl:choose>

						<!--mod mar-09 presentar celda 02001 actividad CNAE -->
						<xsl:when test="contains(@id,'d0200000-02001-e2')">
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:record[@id = '02001']/in:item[contains(@id,'Xcode_ACC.CNAE.')]/in:value"></xsl:value-of>

							</xsl:if>
						</xsl:when>



						<!--mod mar-09 presentar celda 01061 sociedad dominante ultima del 
							grupo denominacion -->
						<xsl:when test="contains(@id,'d0103000-01061')">
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:record[@id = '0103200']/in:record[@id = '0103300']/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>


						<!--mod mar-09 presentar celda 01060 sociedad dominante ultima del 
							grupo NIF -->
						<xsl:when test="contains(@id,'d0103000-01060')">
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:record[@id = '0103200']/in:record[@id = '0103300']/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>


						<!--mod mar-09 presentar celda 01041 sociedad dominante directa denominacion -->
						<xsl:when test="contains(@id,'d0103000-01041')">
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:record[@id = '0103100']/in:record[@id = '0103300']/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>


						<!--mod mar-09 presentar celda 01040 sociedad dominante directa NIF -->
						<xsl:when test="contains(@id,'d0103000-01040')">
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:record[@id = '0103100']/in:record[@id = '0103300']/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>



						<xsl:when test="contains(@id,'-e1') ">
							<!-- If this is a cell for the previous reporting period get the correct 
								value -->
							<xsl:if test="$previousReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($previousReportingDate)]/in:record[@id = $recordID]/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>
						<xsl:when test="contains(@id,'-e2')">

							<!-- If this is a cell for the current reporting period get the correct 
								value -->
							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]/in:item[@id = $itemID]/in:value"></xsl:value-of>
							</xsl:if>
						</xsl:when>
						<xsl:when test="contains(@id,'mp')">
							<!-- The province field (among others) is a code reference to a list 
								of provinces. Rather than list all the possible codes in the template an 
								identifier has been added to the ID and the code is processed appropriately 
								to identify the value. An alternative plan would be to use a mapping file 
								from the codes to the labels to output the correct label. -->


							<!--<xsl:value-of select="$module/in:record[@id = $recordID]//in:item[contains(@id,$itemID)]/in:value"></xsl:value-of> -->
							<!--modificacion mar-09 solo obtenemos el valor para el ejercicio 
								actual -->

							<xsl:if test="$currentReportingDate castable as xs:date">
								<xsl:value-of
									select="$module[year-from-date(@reportingDateEnd) = year-from-date($currentReportingDate)]/in:record[@id = $recordID]//in:item[contains(@id,$itemID)]/in:value"></xsl:value-of>
							</xsl:if>



						</xsl:when>
						<xsl:when
							test="$module/in:record[@id = $recordID]//in:item[@id = $itemID]/in:value">
							<!-- When there is a recordID present in the XML reporting format 
								matching the ID extracted for the current cell then populate the cell with 
								that value. -->
							<xsl:value-of
								select="$module/in:record[@id = $recordID]//in:item[@id = $itemID]/in:value"></xsl:value-of>
						</xsl:when>
						<!--mod mar-09 presentar celda 01902 -->
						<xsl:when test="@id  = '01902'">
							<xsl:value-of select="$code01902"></xsl:value-of>

						</xsl:when>

						<xsl:otherwise><!-- Otherwise do not output a value. -->
							<xsl:text>&#160;&#160;</xsl:text>
						</xsl:otherwise>


					</xsl:choose>
					<xsl:apply-templates select="xhtml:*" mode="information">
						<!-- Apply templates to any children in the XHTML namespace (with the 
							information node). -->
						<xsl:with-param name="module" select="$module"></xsl:with-param>
						<xsl:with-param name="currentReportingDate" tunnel="yes"
							select="$currentReportingDate" />
						<xsl:with-param name="previousReportingDate"
							tunnel="yes" select="$previousReportingDate" />
					</xsl:apply-templates>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{name()}">
					<xsl:copy-of select="@* | text() " />
					<xsl:apply-templates select="xhtml:*" mode="information">
						<xsl:with-param name="module" select="$module"></xsl:with-param>
						<xsl:with-param name="currentReportingDate" tunnel="yes"
							select="$currentReportingDate" />
						<xsl:with-param name="previousReportingDate"
							tunnel="yes" select="$previousReportingDate" />
					</xsl:apply-templates>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the module element in the dimensional
			mode.</xd:short>
		<xd:detail>This template processes the modules for the dimensional
			statements.
			It uses the html templates for this module and applies XSLT templates
			to this XHTML template.
		</xd:detail>
	</xd:doc>

	<xsl:template match="in:module" mode="dimensional">
		<xsl:param name="NIF"></xsl:param>
		<xsl:param name="DS"></xsl:param>

		<xsl:param name="code09001Unit"></xsl:param>
		<xsl:param name="code09002Unit"></xsl:param>
		<xsl:param name="code09003Unit"></xsl:param>

		<xsl:param name="currentReportingDate"><!-- The reporting date for the current period. -->
		</xsl:param>
		<xsl:param name="previousReportingDate"><!-- The reporting date for the previous period. -->
		</xsl:param>

		<!-- The location of the presentation configuration file -->
		<xsl:variable name="configLocation">
			<xsl:choose>
				<xsl:when test="$report = 'pgc07abreviado'">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07normal'">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07pymes'">
					<xsl:value-of select="concat('pgc-07-p-',$module,'-presentation.xml')"/>
				</xsl:when>

				<!-- 20090610 trabajamos con el modelo mixto -->
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module = 'pyg' ">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-presentation.xml')"/>
				</xsl:when>
				<xsl:when test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module != 'pyg' ">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-presentation.xml')"/>
				</xsl:when>

				<xsl:otherwise>
					<xsl:value-of
						select="concat($report,'-',$module,'-presentation.xml')"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<h1 class="state">
			<xsl:value-of
				select="$global/gl:global/gl:configReport[@id = $report]/gl:configModule[@id = $module]/@title"></xsl:value-of>
		</h1>

		<table class="external">
			<!--patNetB -->
			<tr>
				<td>
					<table class="internal width90">
						<tr>
							<th class="labelTD">NIF:</th>
							<td>
								<xsl:value-of select="$NIF"></xsl:value-of>
							</td>
						</tr>
						<tr>
							<th class="labelTD">DENOMINACIÓN SOCIAL:</th>
							<td>
								<xsl:value-of select="$DS"></xsl:value-of>
							</td>
						</tr>
					</table>
				</td>
				<td class="sub">
					<span class="sub">Espacio destinado para las firmas de los
						administradores</span>
				</td>
				<td>
					Ejercicio:
					<xsl:value-of select="year-from-date(@reportingDateEnd)"></xsl:value-of>
				</td>

				<!-- Insert the unit information -->


				<!--la tabla de unidades no se presenta en pymes -->
				<xsl:if test="$report != 'pgc07pymes' and $report != 'pgc-07-p'">
					<xsl:if test="year-from-date(@reportingDateEnd)=year-from-date($currentReportingDate)">
						<td class="info">
							<table class="internal unitsAligment">
								<caption>
									UNIDADES
								</caption>
								<tr>
									<td class="units">Euros</td>
									<th class="code font70">09001</th>
									<td class="font70">
										<xsl:value-of select="$code09001Unit" />
									</td>
								</tr>
								<tr>
									<td class="units">Miles</td>
									<th class="code font70">09002</th>
									<td class="font70">
										<xsl:value-of select="$code09002Unit" />
									</td>
								</tr>
								<tr>
									<td class="units">Milliones</td>
									<th class="code font70">09003</th>
									<td class="font70">
										<xsl:value-of select="$code09003Unit" />
									</td>
								</tr>
							</table>
						</td>

					</xsl:if>  <!--test="year-from-date(@reportingDateEnd) -->
				</xsl:if>  <!--test="$report != 'pgc07pymes' and $report != 'pgc-07-p'" -->

			</tr>
		</table>

		<!-- The path to the XHTML template -->
		<xsl:variable name="templatePath">
			<xsl:choose>
				<xsl:when test="$report = 'pgc07abreviado'">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07normal'">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:when test="$report = 'pgc07pymes'">
					<xsl:value-of select="concat('pgc-07-p-',$module,'-template.html')"/>
				</xsl:when>
				
				<!-- 20090610 trabajamos con el modelo mixto -->
				<xsl:when
					test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module = 'pyg'  ">
					<xsl:value-of select="concat('pgc-07-a-',$module,'-template.html')"/>
				</xsl:when>
				<xsl:when
					test="($report = 'pgc-07-m'  or $report = 'pgc07mixto' )  and $module != 'pyg'  ">
					<xsl:value-of select="concat('pgc-07-n-',$module,'-template.html')"/>
				</xsl:when>

				<xsl:otherwise>
					<xsl:value-of select="concat($report,'-',$module,'-template.html')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>


		<xsl:variable name="template" select="document($templatePath)"></xsl:variable>

		<xsl:apply-templates select="$template/xhtml:div"
			mode="dimensional">
			<xsl:with-param name="module" select="." as="node()*"><!-- The module 
					to be processed. -->
			</xsl:with-param>
			<xsl:with-param name="currentReportingDate" select="$currentReportingDate"><!-- 
					The reporting date for the current period. -->
			</xsl:with-param>
			<xsl:with-param name="previousReportingDate" select="$previousReportingDate"><!-- 
					The reporting date for the previous period -->
			</xsl:with-param>

		</xsl:apply-templates>
	</xsl:template>

	<xd:doc>
		<xd:short>The template matching the XHTML elements in the dimensional
			mode.</xd:short>
		<xd:detail>This template processes the modules for the dimensional
			statements via the XHTML template. This template copies across the
			template format populating
			cells where specified by the template which has id attributes referencing
			item codes.
		</xd:detail>
	</xd:doc>

	<xsl:template match="xhtml:*" mode="dimensional">
		<xsl:param name="module"><!-- The module to be processed. -->
		</xsl:param>
		<xsl:choose>
			<xsl:when test="exists(@id)">
				<xsl:variable name="itemID"
					select="replace(substring-before(@id,'-'),'d','')"></xsl:variable>
				<xsl:variable name="rowID" select="substring-after(@id,'-')">
				</xsl:variable>

				<xsl:element name="{name()}">
					<xsl:copy-of select="@*[not(name() = 'id')]" />
					<!-- Now we need the value from the xml file. -->
					<xsl:choose>
						<xsl:when
							test="$module/in:table/in:row[@id = $rowID]/in:item[@id = $itemID]/in:value">
	
							<xsl:variable name="currentPeriodValue"
								select="$module/in:table/in:row[@id = $rowID]/in:item[@id = $itemID]/in:value"></xsl:variable>
							<xsl:variable name="currentPeriodUnitAux"
								select="$module/in:table/in:row[@id = $rowID]/in:item[@id = $itemID]/@unit"></xsl:variable>
							<xsl:variable name="currentPeriodDecimalAux"
								select="$module/in:table/in:row[@id = $rowID]/in:item[@id = $itemID]/@decimals"></xsl:variable>
							<xsl:variable name="currentPeriodSignAux"
								select="$module/in:table/in:row[@id = $rowID]/in:item[@id = $itemID]/@sign"></xsl:variable>

							<xsl:variable name="currentPeriodUnit">
								<xsl:choose>
									<xsl:when test="$currentPeriodUnitAux!=''">
										<xsl:value-of select="$currentPeriodUnitAux"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$module/@baseUnit!=''">
												<xsl:value-of select="$module/@baseUnit"></xsl:value-of>
											</xsl:when>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>

							<xsl:variable name="currentPeriodDecimal">
								<xsl:choose>
									<xsl:when test="$currentPeriodDecimalAux!=''">
										<xsl:value-of select="$currentPeriodDecimalAux"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$module/@baseDecimals!=''">
												<xsl:value-of select="$module/@baseDecimals"></xsl:value-of>
											</xsl:when>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>

							<xsl:variable name="currentPeriodSign">
								<xsl:choose>
									<xsl:when test="$currentPeriodSignAux!=''">
										<xsl:value-of select="$currentPeriodSignAux"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$module/@baseSign!=''">
												<xsl:value-of select="$module/@sign"></xsl:value-of>
											</xsl:when>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>




							<xsl:variable name="currentPeriodDecimalValue">
								<!-- In this variable we determine the number to normalise the unit 
									of the currentPeriodValue . -->
								<xsl:choose>
									<!-- Choose based on number comparisons. -->
									<xsl:when
										test="$currentPeriodDecimal='-3' and $currentPeriodUnit='euro'">
										<xsl:value-of select="1000"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when
												test="$currentPeriodDecimal='-6' and $currentPeriodUnit='euro'">
												<xsl:value-of select="1000000"></xsl:value-of>
											</xsl:when>
											<xsl:otherwise>
												<xsl:choose>
													<xsl:when
														test="$currentPeriodDecimal='0' and $currentPeriodUnit='euro'">
														<xsl:value-of select="0"></xsl:value-of>
													</xsl:when>
													<xsl:otherwise>
														1
													</xsl:otherwise>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>

							<xsl:variable name="currentPeriodUnitValue">
								<xsl:choose>
									<xsl:when test="$currentPeriodDecimalValue=1">
										<xsl:value-of select="$currentPeriodValue"></xsl:value-of>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when test="$currentPeriodDecimalValue='0'">
												<xsl:choose>
													<xsl:when test="$currentPeriodValue!=''">
														<!--xsl:number value="$currentPeriodValue" format="1" grouping-separator 
															="," grouping-size="3"/ -->
														<xsl:number value="$currentPeriodValue" format="1" />
														<!--xsl:value-of select="$currentPeriodValue"></xsl:value-of -->
													</xsl:when>
												</xsl:choose>
											</xsl:when>
											<xsl:otherwise>
												<xsl:choose>
													<xsl:when test="$currentPeriodValue!=''">
														<xsl:number
															value="$currentPeriodValue div $currentPeriodDecimalValue"
															format="1" />
														<!--xsl:value-of select="$currentPeriodValue div $currentPeriodDecimalValue"></xsl:value-of -->
													</xsl:when>
												</xsl:choose>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>


							<xsl:variable name="currentPeriodUnitValueString">
								<xsl:value-of select="$currentPeriodUnitValue" />
							</xsl:variable>

							<xsl:variable name="currentPeriodValuesign">
								<xsl:choose>
									<xsl:when test="$currentPeriodUnitValueString!=''">
										<xsl:value-of
											select="fnc:formatNumberWithDecimals(concat($currentPeriodSign,$currentPeriodUnitValueString))" />
									</xsl:when>
								</xsl:choose>
							</xsl:variable>
							<xsl:value-of select="$currentPeriodValuesign" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&#160;&#160;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:apply-templates select="xhtml:*" mode="dimensional">
						<xsl:with-param name="module" select="$module"><!-- 
								The module to be processed. -->
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{name()}">
					<xsl:copy-of select="@* | text() " />
					<xsl:apply-templates select="xhtml:*" mode="dimensional">
						<xsl:with-param name="module" select="$module"><!-- The module 
								to be processed. -->
						</xsl:with-param>
					</xsl:apply-templates>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
