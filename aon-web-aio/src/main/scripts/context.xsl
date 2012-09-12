<?xml version="1.0" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

        <xsl:output method="xml" indent="yes"/>

        <xsl:strip-space elements="*"/>

        <xsl:template match="@*|node()">
                <xsl:copy>
                        <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
        </xsl:template>

        <xsl:template match="/Context">
                <xsl:copy>
                        <xsl:apply-templates select="@*|node()"/>
                <xsl:comment>
    *************************
    Added by AON installation
    *************************
  </xsl:comment>

        <Realm appName="aon.security"
                className="org.apache.catalina.realm.JAASRealm"
                roleClassNames="com.code.aon.jaas.auth.SimpleGroup"
                useContextClassLoader="false"
                userClassNames="com.code.aon.jaas.auth.AuthPrincipal"/>

        <Resource name="bean/ConsoleFactoryClass"
                auth="Container"
                type="com.code.aon.bridge.jmx.mbean.core.TomcatConsoleAdminFactory"
                factory="org.apache.naming.factory.BeanFactory" />

                </xsl:copy>
        </xsl:template>

</xsl:stylesheet>
