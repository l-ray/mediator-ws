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
            <var-def name="startDate"><![CDATA[2015-10-30]]></var-def>
            <var-def name="endDate"><![CDATA[2015-01-30]]></var-def>
            <xsl:for-each select="//grouping">
                <xsl:variable name="forVariable">siteSnippet-<xsl:number/></xsl:variable>
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
        <xsl:variable name="loopVariable">article-<xsl:number/></xsl:variable>
        <loop item="article" index="i">
            <xsl:attribute name="item">
                <xsl:value-of select="$loopVariable" />
            </xsl:attribute>
            <list>
                <xsl:choose>
                    <xsl:when test="string-length(@conditionXPath)>0">
                        <loop item="linkTags" filter="unique">
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
                                <empty>
                                    <var-def name="eventDate">
                                         <xpath>
                                             <xsl:attribute name="expression">
                                                 <xsl:value-of select="@conditionXPath" />
                                             </xsl:attribute>
                                             <var name="linkTags" />
                                         </xpath>
                                    </var-def>
                                    <var-def name="comparisonDate">
                                        <template>${<xsl:value-of select="@conditionCompareTo" />}</template>
                                    </var-def>
                                </empty>
                                <case>
                                    <if>
                                        <xsl:attribute name="condition"><xsl:text>${eventDate.toString().indexOf(comparisonDate.toString()) != -1}</xsl:text></xsl:attribute>
                                        <var name="linkTags" />
                                    </if>
                                </case>
                            </body>
                        </loop>
                    </xsl:when>
                    <xsl:otherwise>
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
                    </xsl:otherwise>
                </xsl:choose>

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
                <case>
                    <if>
                        <xsl:attribute name="condition">
                            <xsl:text><![CDATA[${articleUrl.toString().indexOf("http") != 0}]]></xsl:text>
                        </xsl:attribute>
                        <template><xsl:value-of select="ancestor::pattern[1]/@baseUrl" />${articleUrl}</template>
                    </if>
                    <else>
                        <template>${articleUrl}</template>
                    </else>
                </case>
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
        <template><![CDATA[<title>]]>
            <xsl:call-template name="cleanAmpersand">
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:call-template>
        <![CDATA[</title>]]></template>
    </xsl:template>

    <xsl:template match="location">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<location>]]>
            <xsl:call-template name="cleanAmpersand">
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:call-template>
            <![CDATA[</location>]]>
        </template>
    </xsl:template>

    <xsl:template match="price">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<price>]]>
                <xsl:call-template name="cleanAmpersand">
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                </xsl:call-template>
            <![CDATA[</price>]]>
        </template>
    </xsl:template>

    <xsl:template match="pictures">
        <xsl:param name="xmlSource" />
        <loop item="pic" index="j">
            <list>
                <xsl:call-template name="cleanAmpersand">
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                    <xsl:with-param name="skipAdjustment">true</xsl:with-param>
                </xsl:call-template>
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
        <xsl:param name="xmlSource" />
        <regexp replace="true" >
            <regexp-pattern><xsl:value-of select="@pattern"/></regexp-pattern>
            <regexp-source><xsl:apply-templates>
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:apply-templates>
            </regexp-source>
            <regexp-result><xsl:value-of select="@result"/></regexp-result>
        </regexp>
    </xsl:template>

    <xsl:template match="description">
        <xsl:param name="xmlSource" />
        <template>
            <![CDATA[<description>]]><xsl:call-template name="cleanAmpersand">
                <xsl:with-param name="xmlSource" select="$xmlSource" />
            </xsl:call-template>
            <![CDATA[</description>]]>
        </template>
    </xsl:template>

    <xsl:template match="eventlist//grouping//xpath" priority="5">
        <xsl:param name="xmlSource" />
        <xsl:param name="skipAdjustment">false</xsl:param>
        <xsl:call-template name="genericXPathResolve">
            <xsl:with-param name="xmlSource" select="$xmlSource" />
            <xsl:with-param name="skipAdjustment" select="$skipAdjustment" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="eventlist//xpath" priority="4">
        <xsl:param name="xmlSource" />
        <xsl:param name="skipAdjustment">false</xsl:param>
        <xsl:call-template name="genericXPathResolve">
            <xsl:with-param name="xmlSource" select="$xmlSource" />
            <xsl:with-param name="skipAdjustment" select="$skipAdjustment" />
        </xsl:call-template>    
    </xsl:template>
    
    <xsl:template name="genericXPathResolve">
        <xsl:param name="xmlSource" />
        <xsl:param name="skipAdjustment">false</xsl:param>
        <xpath>
            <xsl:attribute name="expression">
                <xsl:choose>
                    <xsl:when test="not($skipAdjustment = 'false')"><xsl:value-of select="."/></xsl:when>
                    <xsl:otherwise>normalize-space(data(<xsl:value-of select="."/>))</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <var>
                <xsl:attribute name="name">
                    <xsl:value-of select="$xmlSource" />
                </xsl:attribute>
            </var>
        </xpath>
    </xsl:template>

    <xsl:template name="cleanAmpersand">
        <xsl:param name="xmlSource" />
        <xsl:param name="skipAdjustment">false</xsl:param>
        <regexp replace="1">
            <regexp-pattern>&amp; </regexp-pattern>
            <regexp-source>
                <xsl:apply-templates>
                    <xsl:with-param name="xmlSource" select="$xmlSource" />
                    <xsl:with-param name="skipAdjustment" select="$skipAdjustment" />
                </xsl:apply-templates>
            </regexp-source>
            <regexp-result><template>&amp;amp; </template></regexp-result>
        </regexp>
    </xsl:template>

</xsl:stylesheet>