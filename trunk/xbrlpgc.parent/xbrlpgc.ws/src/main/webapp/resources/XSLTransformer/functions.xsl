<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet	xmlns="http://www.w3.org/1999/xhtml" 
				xmlns:xhtml="http://www.w3.org/1999/xhtml" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:fnc="http://fnc"
				version="2.0"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				extension-element-prefixes="fnc">

	<!-- Format a Number as String with Commas Separation -->
	<xsl:function	name="fnc:formatNumberWithDecimals"
					as="xs:string">
		<xsl:param	name="numberAsString"
					as="xs:string">
		</xsl:param>
		<xsl:choose>
			<xsl:when test="$numberAsString = 'null' or string-length(normalize-space($numberAsString)) = 0">
				<xsl:value-of select="$numberAsString"/>
				
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable	name="numberBeforeDot"
								select="if (contains($numberAsString, '.')) then substring-before($numberAsString,'.') else $numberAsString"
								as="xs:string">
				</xsl:variable>		
				<!-- select="if (contains($numberAsString, '.')) then concat('.',substring-after($numberAsString,',')) else ''"  -->
				<xsl:variable	name="numberAfterDot"
								select="if (contains($numberAsString, '.')) then concat(',',substring-after($numberAsString,'.')) else ',00'"			
								as="xs:string">
				</xsl:variable>						
							
				<xsl:value-of select="concat(fnc:formatNumberGroupSeparation($numberBeforeDot),$numberAfterDot)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:function	name="fnc:formatNumberGroupSeparation"
					as="xs:string">
		<xsl:param	name="numberAsString"
					as="xs:string">
		</xsl:param>
		<xsl:number value='$numberAsString' grouping-separator="." grouping-size="3"/>
	</xsl:function>

</xsl:stylesheet>