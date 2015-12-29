<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://web-harvest.sourceforge.net/schema/1.0/config">

    <xsl:output method="xml" />

    <xsl:template match="/">
        <config charset="UTF-8">
            <xsl:apply-templates />
        </config>
    </xsl:template>

    <xsl:template match="pattern">

            <var-def name="baseUrl">
                <xsl:value-of select="@baseUrl"/>
            </var-def>
            <!-- var-def name="startUrl">
                <xsl:value-of select="@startUrl" />
            </var-def -->
            <var-def name="startDate"><![CDATA[2015-10-30]]></var-def>
            <var-def name="endDate"><![CDATA[2015-01-30]]></var-def>
            <xsl:for-each select="//grouping">
                <xsl:variable name="forVariable" select="concat('siteSnippet-',generate-id())" />
                <function name="subpage01">
                    <return>
                        <template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>
                        <empty>
                            <var-def>
                                <xsl:attribute name="name">
                                    <xsl:value-of select="$forVariable" />
                                </xsl:attribute>
                                <html-to-xml advancedxmlescape="0" specialentities="false" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="0" allowhtmlinsideattributes="0" >
                                    <http>
                                        <xsl:attribute name="url">
                                            <xsl:text>${pageUrl}</xsl:text>
                                        </xsl:attribute>
                                    </http>
                                </html-to-xml></var-def>
                        </empty>
                        <xsl:apply-templates>
                            <xsl:with-param name="xmlSource">
                                <xsl:value-of select="$forVariable" />
                            </xsl:with-param>
                        </xsl:apply-templates>
                    </return>
                </function>
            </xsl:for-each>
            <xsl:apply-templates />

    </xsl:template>

    <xsl:template match="eventlist">
        <xsl:variable name="loopVariable" select="concat('article-',generate-id())" />
        <loop item="article" index="i">
            <xsl:attribute name="item">
                <xsl:value-of select="$loopVariable" />
            </xsl:attribute>
            <list>
                <xpath>
                    <xsl:attribute name="expression">
                        <xsl:value-of select="@xpath" />
                    </xsl:attribute>
                    <html-to-xml treatdeprtagsascontent="1" treatunknowntagsascontent="0">
                        <http>
                            <xsl:attribute name="url">
                                <xsl:value-of select="ancestor::pattern[1]/@baseUrl" /><xsl:value-of select="ancestor::pattern[1]/@startUrl" />
                            </xsl:attribute>
                        </http>
                    </html-to-xml>
                </xpath>
            </list>
            <body>
                <xsl:apply-templates>
                    <xsl:with-param name="xmlSource" select="$loopVariable" />
                </xsl:apply-templates>
            </body>
        </loop>
    </xsl:template>

    <xsl:template match="grouping">
        <xsl:param name="xmlSource" />
        <call name="subpage01">
            <call-param name="pageUrl">
                <empty>
                    <var-def name="articleUrl">
                        <xpath>
                            <xsl:attribute name="expression">
                                <xsl:value-of select="@url"/>
                            </xsl:attribute>
                            <var name="article">
                                <xsl:attribute name="name">
                                    <xsl:value-of select="$xmlSource" />
                                </xsl:attribute>
                            </var>
                        </xpath>
                    </var-def>
                </empty>
                <!-- TODO: Replace with baseUrl from document -->
                <template>${baseUrl}${articleUrl}</template>
            </call-param>
        </call>
    </xsl:template>

    <xsl:template match="event">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<results>]]></template><xsl:apply-templates>
            <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:apply-templates><template><![CDATA[</results>]]></template>
    </xsl:template>

    <xsl:template match="title">
        <xsl:param name="xmlSource" />
        <template><![CDATA[<title>]]><!--/template -->
                <xsl:apply-templates>
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                </xsl:apply-templates>
        <!-- template --><![CDATA[</title>]]></template>
    </xsl:template>

    <xsl:template match="location">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<location>]]>
            <xsl:apply-templates>
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:apply-templates>
            <![CDATA[</location>]]>
        </template>
    </xsl:template>

    <xsl:template match="price">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<price>]]>
                <xsl:apply-templates>
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                </xsl:apply-templates>
            <![CDATA[</price>]]>
        </template>
    </xsl:template>

    <xsl:template match="pictures">
        <xsl:param name="xmlSource" />
        <loop item="pic" index="j">
            <list>
                <xsl:apply-templates>
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                </xsl:apply-templates>
            </list>
            <body>
            <template>
                <![CDATA[<pictures>]]><var name="pic" /><![CDATA[</pictures>]]>
            </template>
            </body>
        </loop>
    </xsl:template>

    <xsl:template match="start">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<start>]]><xsl:apply-templates>
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:apply-templates><![CDATA[</start>]]>
        </template>
    </xsl:template>

    <xsl:template match="url">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<url>]]><xsl:apply-templates>
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:apply-templates><![CDATA[</url>]]>
        </template>
    </xsl:template>

    <xsl:template match="regex">
        <regexp replace="true" >
            <regexp-pattern><xsl:value-of select="@pattern"/></regexp-pattern>
            <regexp-source><xsl:apply-templates /></regexp-source>
            <regexp-result><xsl:value-of select="@result"/></regexp-result>
        </regexp>
    </xsl:template>

    <xsl:template match="description">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<description>]]><xsl:apply-templates>
            <xsl:with-param name="xmlSource" select="$xmlSource" />
        </xsl:apply-templates>
            <![CDATA[</description>]]>
        </template>
    </xsl:template>

    <xsl:template match="eventlist//grouping//xpath" priority="5">
        <xsl:param name="xmlSource" />
        <xsl:call-template name="genericxpathresolve">
            <xsl:with-param name="xmlSource" select="$xmlSource" />
            <!-- xsl:with-param name="xmlSource">siteSnippet</xsl:with-param -->
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="eventlist//xpath" priority="4">
        <xsl:param name="xmlSource" />
        <xsl:call-template name="genericxpathresolve">
            <xsl:with-param name="xmlSource" select="$xmlSource" />
        </xsl:call-template>    
    </xsl:template>
    
    <xsl:template name="genericxpathresolve">
        <xsl:param name="xmlSource" />
        <xpath>
            <xsl:attribute name="expression">
                <xsl:value-of select="."/>
            </xsl:attribute>
            <var>
                <xsl:attribute name="name">
                    <xsl:value-of select="$xmlSource" />
                </xsl:attribute>
            </var>
            <!-- xsl:value-of select="concat(concat('${',ancestor::loop[1]/@item),'}')"/ -->
        </xpath>
    </xsl:template>

</xsl:stylesheet>