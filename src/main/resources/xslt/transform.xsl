<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml"/>

    <xsl:template match="/">
        <config charset="UTF-8">"
            <xsl:for-each select="//grouping">
                <function name="subpage01">
                    <return>
                        <template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>
                        <empty>
                            <var-def name="siteSnippet">
                                <html-to-xml advancedxmlescape="0" specialentities="false" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="0" allowhtmlinsideattributes="0" >
                                    <http url="${blapageUrl}" />
                                </html-to-xml></var-def>
                        </empty>
                        <xsl:apply-templates />
                    </return>
                </function>
            </xsl:for-each>
            <xsl:apply-templates />
        </config>
    </xsl:template>

    <xsl:template match="eventlist">
        <loop item="article" index="i">
            <list>
                <xpath>
                    <xsl:attribute name="expression">
                        <xsl:value-of select="xpath" />
                    </xsl:attribute>
                    <html-to-xml treatdeprtagsascontent="1" treatunknowntagsascontent="0">
                        <http>
                            <xsl:attribute name="url">
                                <xsl:value-of select="@baseUrl" /><xsl:value-of select="@startUrl" />
                            </xsl:attribute>
                        </http>
                    </html-to-xml>
                </xpath>
            </list>
            <body>
                <xsl:apply-templates />
            </body>
        </loop>
    </xsl:template>

    <xsl:template match="grouping">
        <call name="subpage01">
            <call-param name="pageUrl">
                <empty>
                    <var-def name="articleUrl">
                        <xpath>
                            <xsl:attribute name="expression">
                                <xsl:value-of select="@url"/>
                            </xsl:attribute>
                            <var name="article" />
                        </xpath>
                    </var-def>
                </empty>
                <!-- TODO: Replace with baseUrl from document -->
                <template>${baseUrl}${articleUrl}</template>
            </call-param>
        </call>
    </xsl:template>

    <xsl:template match="event">
        <template>
            <![CDATA[<results>]]><xsl:apply-templates /><![CDATA[</results>]]>
        </template>
    </xsl:template>

    <xsl:template match="title">
        <template>
            <![CDATA[<title>]]>
                <xsl:apply-templates />
            <![CDATA[</title>]]>
        </template>
    </xsl:template>

    <xsl:template match="location">
        <template>
            <![CDATA[<location>]]><xsl:apply-templates /><![CDATA[</location>]]>
        </template>
    </xsl:template>

    <xsl:template match="price">
        <template>
            <![CDATA[<price>]]><xsl:apply-templates /><![CDATA[</price>]]>
        </template>
    </xsl:template>

    <xsl:template match="pictures">
        <loop item="article" index="i">
            <list><xsl:apply-templates /></list>
            <body>
            <template>
                <![CDATA[<pictures>]]><xsl:apply-templates /><![CDATA[</pictures>]]>
            </template>
            </body>
        </loop>
    </xsl:template>

    <xsl:template match="start">
        <template>
            <![CDATA[<start>]]><xsl:apply-templates /><![CDATA[</start>]]>
        </template>
    </xsl:template>

    <xsl:template match="url">
        <template>
            <![CDATA[<url>]]><xsl:apply-templates /><![CDATA[</url>]]>
        </template>
    </xsl:template>

    <xsl:template match="regex">
        <regexp replace="true" >
            <regexp-pattern><xsl:value-of select="@pattern"/></regexp-pattern>
            <regexp-source><xsl:apply-templates /></regexp-source>
            <regexp-result><xsl:value-of select="@result"/></regexp-result>
        </regexp>
    </xsl:template>

    <xsl:template match="xpath">
        <xpath>
            <xsl:attribute name="expression">
                <xsl:value-of select="."/>
            </xsl:attribute>
        </xpath>
    </xsl:template>

</xsl:stylesheet>