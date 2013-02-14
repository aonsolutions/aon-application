<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml" indent="yes"/>

	<xsl:strip-space elements="*"/>

        <xsl:template match="@*|node()">
                <xsl:copy>
                        <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
        </xsl:template>

        <xsl:template match="/Server/Listener[last()]">
                <xsl:copy>
	                <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
		<xsl:comment>
    *************************
    Added by AON installation
    *************************
  </xsl:comment>
		<Listener className="com.code.aon.jaas.vendor.tomcat.SecurityLifecycleListener" 
			descriptors="/META-INF/mbeans-descriptors.xml"/>
        </xsl:template>

</xsl:stylesheet>
