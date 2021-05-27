<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" 
	xmlns:in="http://www.inteco.es/xbrl/pgc07/interfazES"
	xmlns:map="http://www.inteco.es/xbrl/pgc07/mapa" 
	xmlns:gl="http://www.inteco.es/xbrl/pgc07/identificadores"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:ui="http://java.sun.com/jsf/facelets" 
	xmlns:f="http://java.sun.com/jsf/core" 
	xmlns:aon="http://www.code.es/aon-rich-components" 
	xmlns:h="http://java.sun.com/jsf/html" 
	xmlns:c="http://java.sun.com/jstl/core"	
	xmlns:fnc="http://fnc">

	<!-- Format a Number as String with Commas Separation -->
	<xsl:decimal-format name="decimalFormatter" decimal-separator="," grouping-separator="."/>
	<xsl:function name="fnc:formatNumberWithDecimals" as="xs:string">
		<xsl:param name="numberAsString" as="xs:string" />
		<xsl:variable name="numberDouble" select="number($numberAsString) div 100" as="xs:double"/>
		<xsl:value-of select="format-number($numberDouble, '#.##0,00;(#.##0,00)', 'decimalFormatter')" />
	</xsl:function>
	
	<!-- The ID of the module to process. -->
	<xsl:param name="module" />

	<xsl:output method="html" encoding="ISO-8859-1" indent="yes"
		exclude-result-prefixes="#all" 
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<!-- Output method xhtml. Encoding UTF8. References to the xhtml DTD. -->
	</xsl:output>
	
	<!-- This variable contains the global configuration information document. -->
	<xsl:variable name="global" select="document('global.xml')" />
	<!--The ID for this report -->
	<xsl:variable name="report" select="/in:report/@id"/>
	
	<xsl:template match="/">
		<ui:composition xmlns="http://www.w3.org/1999/xhtml" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:f="http://java.sun.com/jsf/core" xmlns:aon="http://www.code.es/aon-rich-components" xmlns:h="http://java.sun.com/jsf/html" xmlns:c="http://java.sun.com/jstl/core">
			<xsl:apply-templates />
		</ui:composition>
	</xsl:template>
	
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
			
			<xsl:variable name="DS" select="/in:report/in:entity/@id"/>

			<xsl:choose>
				<!-- Now we choose which templates to call based on which module has been selected for transformation. -->
				<xsl:when test="$module = ('bal','pyg','patnetA','flujefec')">
					<!-- When the module is any of the non-dimensional modules process by 
						calling the general processModule template selecting the current module. -->
					<xsl:call-template name="processModule">
						<xsl:with-param name="modules" select="in:module[@id=$module]"/>
						<xsl:with-param name="NIF" select="$NIF"/>
						<xsl:with-param name="DS" select="$DS"/>
						<xsl:with-param name="currentReportingDate" select="$currentReportingDate"/>
						<xsl:with-param name="previousReportingDate" select="$previousReportingDate"/>
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
		</xsl:if>

	</xsl:template>

	<xsl:template match="in:entity">
		<!-- Do nothing with it separately? -->
	</xsl:template>

	<xsl:template name="processModule">
		<xsl:param name="modules"/>
		<xsl:param name="NIF"/>
		<xsl:param name="DS"/>
		<xsl:param name="currentReportingDate"/>
		<xsl:param name="previousReportingDate"/>

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
		<aon:div styleClass="aon-text-center aon-margin-bottom aon-bold" style="font-size:1.1em;">
			<xsl:value-of select="$global/gl:global/gl:configReport[@id = $report]/gl:configModule[@id = $module]/@title"/>
		</aon:div>

		<!-- Get the year from the current reporting period -->
		<xsl:variable name="currentPeriodYear" select="year-from-date($currentReportingDate)"/>

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

		<aon:panelGrid id="dataGrid" columns="5" style="width: 100%;"
			columnClasses="aon-panelGrid-even aon-width-auto
			  			  ,aon-panelGrid-even aon-width-50 aon-text-center aon-bold
						  ,aon-panelGrid-even aon-width-100
						  ,aon-panelGrid-even aon-width-150 aon-text-right
						  ,aon-panelGrid-even aon-width-150 aon-text-right">
					 
				<xsl:for-each select="$config/map:statement/map:conceptMap[not(@parent = '') and not(@inputID = '00000')]">
				
					<xsl:variable name="currentPeriodValue" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/in:value"/>
					<xsl:variable name="currentPeriodSign" select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/@sign"/>
					
					<xsl:variable name="previousPeriodValue" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/in:value"/> 
					<xsl:variable name="previousPeriodSign" select="$modules[year-from-date(@reportingDateEnd) = $previousPeriodYear]/in:item[@id=current()/@inputID]/@sign"/>


					<xsl:variable name="currentValue">
						<xsl:if test="$currentPeriodValue!=''">
							<xsl:value-of select="fnc:formatNumberWithDecimals( concat($currentPeriodSign,$currentPeriodValue))" />
						</xsl:if>
					</xsl:variable>

					<xsl:variable name="previousValue">
						<xsl:if test="$previousPeriodValue!=''">
							<xsl:value-of
								select="fnc:formatNumberWithDecimals(concat($previousPeriodSign,$previousPeriodValue))" />
						</xsl:if>
					</xsl:variable>

					<xsl:variable name="currentPeriodNote" 
						select="$modules[year-from-date(@reportingDateEnd) = $currentPeriodYear]/in:item[@id=current()/@inputID]/in:note/@text">
					</xsl:variable>

					<aon:outputText value="{@label}" escape="true"/>
					<aon:outputText value="{@inputID}"/>
					<aon:outputText value="{$currentPeriodNote}"/>
					<aon:outputText value="{$currentValue}"/>
					<aon:outputText value="{$previousValue}"/>
				</xsl:for-each>
		</aon:panelGrid>
	</xsl:template>
</xsl:stylesheet>
