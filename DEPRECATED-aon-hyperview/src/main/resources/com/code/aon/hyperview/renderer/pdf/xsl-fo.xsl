<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:fox="http://xml.apache.org/fop/extensions" 
	version="1.0">

	<!--  *************************  -->
	<!--  ****** PARAMAETERS ******  -->
	<!--  *************************  -->

	<xsl:param name="today" />
	<xsl:param name="logo" />

	<!--  ********************  -->
	<!--  ****** STYLES ******  -->
	<!--  ********************  -->

	<xsl:attribute-set name="title">
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="line-height">16pt</xsl:attribute>
		<xsl:attribute name="font-size">16pt</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="padding-top">0.5cm</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.5cm</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
	</xsl:attribute-set>


	<xsl:attribute-set name="subTitleContainer">
		<xsl:attribute name="padding-bottom">0.5cm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="subTitle">
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="line-height">10pt</xsl:attribute>
		<xsl:attribute name="font-size">10pt</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="margin-right">0.5cm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
		<xsl:attribute name="border-bottom">solid black 1px</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="formTable">
		<xsl:attribute name="width">90%</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.5cm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="formTableLabel">
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="font-size">7pt</xsl:attribute>
		<xsl:attribute name="padding-top">3px</xsl:attribute>
		<xsl:attribute name="padding-bottom">3px</xsl:attribute>
		<xsl:attribute name="padding-left">5px</xsl:attribute>
		<xsl:attribute name="border">solid #DDDDDD 0.5px</xsl:attribute>
		<xsl:attribute name="background-color">#EEEEEE</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="formTableValue">
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="background-color">white</xsl:attribute>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="font-size">7pt</xsl:attribute>
		<xsl:attribute name="padding-left">5px</xsl:attribute>
		<xsl:attribute name="padding-top">3px</xsl:attribute>
		<xsl:attribute name="padding-bottom">3px</xsl:attribute>
		<xsl:attribute name="border">solid #DDDDDD 0.5px</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="currentDate">
		<xsl:attribute name="font-size">10pt</xsl:attribute>
		<xsl:attribute name="padding-top">0.2cm</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="pageNumber">
		<xsl:attribute name="font-size">10pt</xsl:attribute>
		<xsl:attribute name="padding-top">0.2cm</xsl:attribute>
		<xsl:attribute name="text-align">right</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="listTable">
		<xsl:attribute name="width">90%</xsl:attribute>
		<xsl:attribute name="padding-bottom">0.5cm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="headerCell">
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:attribute name="color">black</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="border">solid #CCCCCC 0.5px</xsl:attribute>
		<xsl:attribute name="background-color">#EEEEEE</xsl:attribute>
		<xsl:attribute name="padding-left">5px</xsl:attribute>
		<xsl:attribute name="padding-top">2px</xsl:attribute>
		<xsl:attribute name="padding-bottom">2px</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="columnCell">
		<xsl:attribute name="font-size">7pt</xsl:attribute>
		<xsl:attribute name="border">solid #DDDDDD 0.5px</xsl:attribute>
		<xsl:attribute name="background-color">#FFFFFF</xsl:attribute>
		<xsl:attribute name="padding-top">2pt</xsl:attribute>
		<xsl:attribute name="padding-left">2pt</xsl:attribute>
		<xsl:attribute name="text-align">left</xsl:attribute>
	</xsl:attribute-set>


	<!--  ******************  -->
	<!--  ****** FLOW ******  -->
	<!--  ******************  -->

	<xsl:template match="/hyperview">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="main" page-height="29.7cm" page-width="21cm" margin-top="0.5cm"
					margin-bottom="0.5cm" margin-left="1cm" margin-right="1cm">
					<fo:region-body margin-top="3.5cm" margin-bottom="1.5cm" />
					<fo:region-before extent="3.5cm" />
					<fo:region-after extent="1cm" />
				</fo:simple-page-master>

				<fo:page-sequence-master master-name="allPages">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference master-reference="main" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>

			<fox:outline internal-destination="hyvw">
				<fox:label>
					<xsl:value-of select="./@name" />
				</fox:label>
				<xsl:call-template name="tocRenderer" />
			</fox:outline>

			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before">
					<xsl:call-template name="header" />
				</fo:static-content>

				<fo:static-content flow-name="xsl-region-after">
					<xsl:call-template name="footer" />
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body" font-family="Helvetica" font-size="8pt">
					<fo:block>
						<xsl:apply-templates />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>

		</fo:root>

	</xsl:template>

	<xsl:template name="tocRenderer">
		<xsl:for-each select="node">
			<fox:outline>
				<xsl:attribute name="internal-destination">
					<xsl:value-of select="./@id" />
				</xsl:attribute>
				<fox:label>
					<xsl:value-of select="./@id" /><xsl:text> - </xsl:text><xsl:value-of select="./@name" />
				</fox:label>
				<xsl:call-template name="tocRenderer" />
			</fox:outline>
		</xsl:for-each>
	</xsl:template>

	<!--  ********************  -->
	<!--  ****** HEADER ******  -->
	<!--  ********************  -->

	<xsl:template name="header">
		<fo:table table-layout="fixed" padding-top="0.1cm" width="100%" padding-bottom="0.1cm"
			border-bottom="solid 1pt black">
			<fo:table-column column-width="2.5cm" />
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>
							<fo:external-graphic>
								<xsl:attribute name="src">
									<xsl:value-of select="$logo" />
								</xsl:attribute>
							</fo:external-graphic>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block xsl:use-attribute-sets="title">
							<xsl:value-of select="./@name" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>


	<!--  ********************  -->
	<!--  ****** FOOTER ******  -->
	<!--  ********************  -->

	<xsl:template name="footer">
		<fo:table table-layout="fixed" padding-top="0.1cm" width="100%" border-top="solid black 0.50pt">
			<fo:table-column column-width="10cm" />
			<fo:table-column column-width="proportional-column-width(1)" />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block xsl:use-attribute-sets="currentDate">
							<xsl:value-of select="$today" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block xsl:use-attribute-sets="pageNumber">
							-
							<fo:page-number />
							-
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--  ******************  -->
	<!--  ****** NODE ******  -->
	<!--  ******************  -->
	<xsl:template match="node">
		<fo:block xsl:use-attribute-sets="subTitleContainer">
			<fo:block xsl:use-attribute-sets="subTitle">
				<xsl:attribute name="id">
					<xsl:value-of select="./@id" />
				</xsl:attribute>
				<xsl:attribute name="margin-left">
					<xsl:value-of select="concat(string(number(./@depth)*0.5),'cm')" />
				</xsl:attribute>
				<xsl:value-of select="./@id" /><xsl:text> - </xsl:text><xsl:value-of select="./@name" />
			</fo:block>
		</fo:block>
		<xsl:apply-templates />
	</xsl:template>

	<!--  ******************  -->
	<!--  ****** FORM ******  -->
	<!--  ******************  -->
	<xsl:template match="form">
		<fo:table table-layout="fixed" xsl:use-attribute-sets="formTable">
			<fo:table-column column-width="proportional-column-width(20)" />
			<fo:table-column column-width="proportional-column-width(80)" />
			<fo:table-body>
				<xsl:apply-templates />
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<!--  *************************  -->
	<!--  ****** FORM FIELDS ******  -->
	<!--  *************************  -->
	<xsl:template match="field">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets="formTableLabel">
				<fo:block>
					<xsl:value-of select="./label" />
					:
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets="formTableValue">
				<fo:block>
					<xsl:value-of select="./value" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<!--  ******************  -->
	<!--  ****** LIST ******  -->
	<!--  ******************  -->
	<xsl:template match="list">
		<fo:table table-layout="fixed" xsl:use-attribute-sets="listTable">
			<xsl:apply-templates />
		</fo:table>
	</xsl:template>

	<!--  *************************  -->
	<!--  ****** LIST HEADER ******  -->
	<!--  *************************  -->
	<xsl:template match="header">
		<xsl:for-each select="headerCell">
			<fo:table-column column-width="proportional-column-width(1)" />
		</xsl:for-each>
		<fo:table-body>
			<fo:table-row>
				<xsl:apply-templates />
			</fo:table-row>
		</fo:table-body>
	</xsl:template>

	<!--  ******************************  -->
	<!--  ****** LIST HEADER CELL ******  -->
	<!--  ******************************  -->
	<xsl:template match="headerCell">
		<fo:table-cell xsl:use-attribute-sets="headerCell">
			<fo:block>
				<xsl:value-of select="." />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<!--  **********************  -->
	<!--  ****** LIST ROW ******  -->
	<!--  **********************  -->
	<xsl:template match="row">
		<fo:table-body>
			<fo:table-row>
				<xsl:apply-templates />
			</fo:table-row>
		</fo:table-body>
	</xsl:template>

	<!--  ***********************  -->
	<!--  ****** LIST CELL ******  -->
	<!--  ***********************  -->
	<xsl:template match="cell">
		<fo:table-cell xsl:use-attribute-sets="columnCell">
			<fo:block>
				<xsl:value-of select="." />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

</xsl:stylesheet>

