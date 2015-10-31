<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
            </head>
            <body>
                <ul>
                    <xsl:for-each select="//grouping">
                        <li><p>found a grouping</p>
                            <xsl:apply-templates />
                        </li>
                    </xsl:for-each>
                </ul>
                <div>
                    <xsl:apply-templates />
                </div>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="grouping">
        <b>grouping again</b>
    </xsl:template>

    <xsl:template match="title">
        <b>in a title</b>
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="location">
        <b>in a location</b>
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="date">
        <b>in a date</b>
        <xsl:apply-templates />
    </xsl:template>

</xsl:stylesheet>