
DROP TABLE IF EXISTS tx_lrmediator_categories;
CREATE TABLE tx_lrmediator_categories (
uid serial,
pid int DEFAULT '0',
tstamp int DEFAULT '0',
crdate int DEFAULT '0',
cruser_id int DEFAULT '0',
deleted int DEFAULT '0',
term varchar DEFAULT NULL,
category varchar DEFAULT NULL,
hidden int NOT NULL DEFAULT '0',
PRIMARY KEY (uid)
);

--
-- Dumping data for table `tx_lrmediator_categories`
--

INSERT INTO tx_lrmediator_categories VALUES (1,132,1257631192,1257631192,1,0,'ska','ska',0),(2,132,1258105999,1258105999,1,0,'metal','metal',0),(3,132,1258106312,1258106312,1,0,'rock','rock',0);

--
-- Table structure for table `tx_lrmediator_pattern`
--

DROP TABLE IF EXISTS tx_lrmediator_pattern;
CREATE TABLE tx_lrmediator_pattern (
uid serial,
pid  int NOT NULL DEFAULT '0',
name varchar DEFAULT '',
url varchar DEFAULT '',
starturl varchar DEFAULT '',
icon varchar DEFAULT '',
pattern text NOT NULL,
dateformat varchar DEFAULT 'dd.mm.yy',
tstamp int NOT NULL DEFAULT '0',
crdate int NOT NULL DEFAULT '0',
cruser_id int NOT NULL DEFAULT '0',
lft int NOT NULL DEFAULT '0',
rgt int NOT NULL DEFAULT '0',
deleted int NOT NULL DEFAULT '0',
hidden int NOT NULL DEFAULT '0',
countrycode varchar NOT NULL DEFAULT 'DE_de',
subpattern  int NOT NULL DEFAULT '0',
fe_group  int NOT NULL DEFAULT '0',
PRIMARY KEY (uid)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tx_lrmediator_pattern`
--

INSERT INTO tx_lrmediator_pattern VALUES (1,132,'groovestation','http://www.groovestation.de/','live.html','http://www.groovestation.de/favicon.ico',E'<loop item="event" index="i">				<list>		   <xpath						expression=\'//div[@id="main_content"]/div[@class="live"]\'>					<html-to-xml treatdeprtagsascontent="1"					treatunknowntagsascontent="1">						<http url="${baseUrl}${startUrl}">						</http>						</html-to-xml>	</xpath>				</list>				<!-- downloads each article and extract data from it -->			<body>					<empty>			<var-def name="test">							<xpath								expression=\'//div[@class="live_head"]/div[@class="head_content"]/table/tbody/tr/td/p/text()\'>								<var name="event" />						    </xpath></var-def>					</empty>			    <case>						        <if condition=\'${startDate.toString().equals(test.toString())}\'>				<template>							            <![CDATA[<article>]]>								            <![CDATA[<title>]]><xpath					expression=\'//div[@class="live_head"]/div[@class="head_content"]/table/tbody/tr/td/p/span[@class="white"]/text()\'><var 				name="event" /></xpath><![CDATA[</title>]]>				<![CDATA[<picture>]]>${baseUrl}<xpath					expression=\'//div[@class="live_content"]/p/img/@src\'>			<var name="event" />							</xpath>									<![CDATA[</picture>]]><![CDATA[<date>]]>${startDate}<![CDATA[</date>]]><![CDATA[<location>]]>Groovestation<![CDATA[</location>]]><![CDATA[<sourcelink>]]>${baseUrl}<![CDATA[</sourcelink>]]>					<![CDATA[</article>]]>						            </template>								</if>						    </case>		</body>			</loop>','EEEE, dd. MMMM',0,0,0,1,2,0,0,'DE_de',0,0),(2,132,'DoubleD_Town Main','http://www.doubled-town.de/forum/','viewforum.php?f=20','http://www.doubled-town.de/favicon.ico',E'<loop item="articleUrl" index="i">				<!-- collects URLs of all articles from the front page -->				<list>			<loop item="linkTags" filter="unique">					<list>						   <xpath		expression=\'//div[@class="mForumMain mForumMainItemBorderSmall"]/div[@class="mForumMainItem "]/div[@class="mForumMainItemText"]/span/a[@class="topictitle"]\'>								<html-to-xml treatdeprtagsascontent="1"						treatunknowntagsascontent="1">						<http url="${baseUrl}${startUrl}">						</http>								</html-to-xml>							</xpath>	</list>						<body>		<regexp>							    <regexp-pattern>([0-9]{2}\\.[0-9]{2}\\.[0-9]{2})</regexp-pattern>			    <regexp-source>    								<var name="linkTags" />							    </regexp-source>							    <regexp-result>        								    <case>								        <if condition=\'${startDate.toString().equals(_1.toString())}\'>		            <xpath expression="a/@href">					            	<var name="linkTags" />					            	</xpath>															        </if>								    </case>    </regexp-result>							</regexp>						</body>			</loop>								</list><!-- downloads each article and extract data from it -->			<body>					<template>			            <![CDATA[<article>]]>						    <call name="%uid:3%">						        <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>							</call><![CDATA[<sourcelink>]]>${baseUrl}<![CDATA[</sourcelink>]]>			            <![CDATA[</article>]]>		            </template>	</body>			</loop>','dd.MM.yy',0,0,0,3,6,0,0,'DE_de',0,0),(4,132,'exma-amt','http://www.exmatrikulationsamt.de/','?act=events&amp;d={startDay}&amp;m={startMonth}&amp;j={startYear}','http://www.exmatrikulationsamt.de/favicon.ico',e'<loop item="articleUrl" index="i">				<!-- collects URLs of all articles from the front page -->			<list>									   <xpath								expression="//table[@id=\'maintable\']/tbody/tr[1]/td[2]/div[@id=\'wrapper\']/div/table/tbody/tr/td[1]/div[@id=\'ev_cat_Partys\']/div/div/table/tbody/tr/td[2]/a[1]/@href">								<html-to-xml treatdeprtagsascontent="1"						treatunknowntagsascontent="1" advancedxmlescape="0">			<http url="${baseUrl}${startUrl}">						</http>								</html-to-xml>							</xpath>					</list>				<!-- downloads each article and extract data from it -->				<body>					<xquery>			<xq-param name="doc">							<html-to-xml  treatdeprtagsascontent="1" treatunknowntagsascontent="1" advancedxmlescape="0" allowhtmlinsideattributes="1">				<http url="${articleUrl}" />						</html-to-xml>						</xq-param><xq-param name="sourceLink" type="string">                                                	<var name="articleUrl" />                                                </xq-param>						<xq-expression><![CDATA[        				declare variable $doc as node() external;declare variable $sourceLink as xs:string external;                                                let $title := data($doc//table[@id=\'maintable\']/tbody/tr[1]/td[2]/div[@id=\'wrapper\']/div[1]/div[1]/p/text())let $location := data($doc//table[@id=\'maintable\']/tbody/tr[1]/td[2]/div[@id=\'wrapper\']/div[1]/div[2]/div/div[@id=\'post\']/div/div/a[1]/text())						let $picture := data($doc//table[@id=\'maintable\']/tbody/tr[1]/td[2]/div[@id=\'wrapper\']/div[1]/div[2]/div/div[@id=\'post\']/div//img/@src)                        return                                <article>                                                     					<location>{data($location)}</location>                                    <title>{data($title)}</title>{                                    for $x in $picture		return <picture>{data($x)}</picture>                                    }<sourcelink>{data($sourceLink)}</sourcelink>                                </article>                    ]]></xq-expression>			</xquery>				</body>			</loop>','0',0,0,0,7,8,0,0,'DE_de',0,0),(3,132,'DoubleD_Town Sub01','http://www.doubled-town.de/forum/','','',e'<template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>		<empty>		<var-def name="siteSnippet">				<html-to-xml advancedxmlescape="0" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="1" allowhtmlinsideattributes="1">			<http url="${pageUrl}" />				</html-to-xml>					</var-def>		</empty>			<template>            <![CDATA[<title>]]>            	<xpath expression="normalize-space(data(//div[2]/div[4]/div/div/span/b))">		<var name="siteSnippet"/>				</xpath>            <![CDATA[</title>]]>            <![CDATA[<date>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[1]/div[@class="mPostMainItemText"]/span))\'>					<var name="siteSnippet"/>				</xpath>            <![CDATA[</date>]]>            <![CDATA[<price>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[2]/div[@class="mPostMainItemText"]/span))\'>					<var name="siteSnippet"/></xpath>			<![CDATA[</price>]]>                        <![CDATA[<location>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[3]/div[@class="mPostMainItemText"]/span))\'><var name="siteSnippet"/>				</xpath>            <![CDATA[</location>]]>            <![CDATA[<picture>]]><xpath expression=\'normalize-space(data(//div[3]/div[3]/a/@href))\'><var name="siteSnippet"/></xpath><![CDATA[#.jpg</picture>]]>		</template>',NULL,0,0,0,4,5,0,0,'DE_de',0,0),(5,132,'banq','http://www.banq.de/','termine.php?date={startTimestamp}','http://www.banq.de/ico/favicon.ico',e'<loop item="item" filter="unique">					<list>			<xpath expression=\'//div[attribute::id="mwtext"]//tr/td[attribute::id="cptable1"]/div[attribute::id="cptable2"]/span/text()\'>				<html-to-xml>					<http url="${baseUrl}${startUrl}" />				</html-to-xml>		</xpath>		</list>		<body>			<var-def name="newItem">			<call name="%uid:6%"><call-param name="title"><var name="item" /></call-param><call-param name="sourcelink"><template>${sys.escapeXml(baseUrl.toString()+startUrl.toString())}</template></call-param>	        	<call-param name="document">		        	<html-to-xml>		<http url="${baseUrl}${startUrl}" />					</html-to-xml>       	</call-param>	</call></var-def>				</body>	</loop>','dd.MM.yy',0,0,0,9,14,0,0,'DE_de',0,0),(6,132,'Banq - Details','','','','<xquery>                    <xq-param name="doc"><var name="document" /></xq-param><xq-param name="title" type="string"><var name="title" /></xq-param><xq-param name="sourcelink" type="string"><var name="sourcelink" /></xq-param>				         <xq-expression><![CDATA[	                declare variable $doc as node() external;declare variable $title as xs:string external;declare variable $sourcelink as xs:string external;			                				                for $b in $doc//div[attribute::id="mwtext"], 			                	$t in $b//tr/td[attribute::id="cptable1"]/div[attribute::id="cptable2"]/span/child::text(),	                				                	$c in $t//ancestor::tr/preceding-sibling::tr/td[attribute::id="cptable"]/table//h3/child::text()where $t=$titlereturn		<article><title>{$t}</title><sourcelink>{$sourcelink}</sourcelink><date>{data($t//ancestor::tr/preceding-sibling::tr/td[attribute::id="cptable"]/table//h3)}</date>			                            <location>{data($t//ancestor::tr/following-sibling::tr/td[attribute::id="cptable"]/a)}</location>			                            <category>{data($c/ancestor::td/following-sibling::td/span[attribute::class="kategorie"])}</category>			                            <link>{data($c/ancestor::td/following-sibling::td/span/a/@href)}</link>		                            </article>			            ]]></xq-expression>	  </xquery>',NULL,0,0,0,10,13,0,0,'DE_de',0,0),(7,132,'Banq-PictureLinks','','','',' <xpath expression="//div/img">        <html-to-xml allowhtmlinsideattributes="true">            <http url="${pictureLink}" />        </html-to-xml>      </xpath>',NULL,0,0,0,11,12,0,0,'DE_de',0,0),(8,132,'tekknoost','http://www.tekknoost.de/forum/','viewforum.php?f=2','http://www.tekknoost.de/favicon.ico',e'<loop item="articleUrl" index="i">			<!-- collects URLs of all articles from the front page -->			<list>					<loop item="linkTags" filter="unique">						<list>		   <xpath								expression=\'//table[@class="viewforum"]/tbody/tr/td[2]/span/a\'>			<html-to-xml treatdeprtagsascontent="1"					treatunknowntagsascontent="1">						<http url="${baseUrl}${startUrl}">						</http>								</html-to-xml>							</xpath>	</list>						<body>		<regexp>							    <regexp-pattern>([0-9]{2}\\.[0-9]{2}\\.[0-9]{2})</regexp-pattern>			    <regexp-source>    								<var name="linkTags" />							    </regexp-source>							    <regexp-result>        								    <case>								        <if condition=\'${startDate.toString().equals(_1.toString())}\'>		            <xpath expression="a/@href">					            	<var name="linkTags" />					            	</xpath>															        </if>								    </case>    </regexp-result>							</regexp>						</body>			</loop>								</list><!-- downloads each article and extract data from it -->			<body>					<template>			            <![CDATA[<article>]]>						    <call name="%uid:9%">						        <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>							</call><![CDATA[</article>]]>		            </template>			</body>			</loop>','dd.MM.yy',0,0,0,15,18,0,0,'DE_de',0,0),(9,132,'tekknoost Sub01','http://www.tekknoost.de/forum/','','',e'<template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template><empty>			<var-def name="siteSnippet">		<html-to-xml advancedxmlescape="0" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="1" allowhtmlinsideattributes="1">	<http url="${pageUrl}" />				</html-to-xml>					</var-def>		</empty>            <template>            <![CDATA[<title>]]>		        <regexp replace="false" max="1">				    <regexp-pattern>[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}[ ]*[\\W]([^@]*)@?.*$</regexp-pattern>		    <regexp-source>				        <xpath expression="normalize-space(data(//table[@class=\'viewtopic\']/tbody/tr[2]/td/h2))">	<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>			    				</regexp>    	            <![CDATA[</title>]]>            <![CDATA[<date>]]>            	<regexp replace="false" max="1">				    <regexp-pattern>([0-9]{2}\\.[0-9]{2}\\.[0-9]{2})</regexp-pattern>				    <regexp-source>				        <xpath expression="normalize-space(data(//table[@class=\'viewtopic\']/tbody/tr[2]/td/h2))">		<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>			    				</regexp>            <![CDATA[</date>]]>                       <![CDATA[<location>]]>            	<regexp replace="false" max="1">				    <regexp-pattern>@(.*)$</regexp-pattern>				    <regexp-source>        <xpath expression="normalize-space(data(//table[@class=\'viewtopic\']/tbody/tr[2]/td/h2))">							<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>		</regexp>            <![CDATA[</location>]]>                        <loop item="picture" index="i">            		<list>	            	<xpath expression="data(//table[@class=\'viewtopic\']//td[@class=\'row1 four\' or @class=\'row2 four\']//tr[1]//p/img/@src)">		<var name="siteSnippet"/>					</xpath>	</list>					<body>			            <![CDATA[<picture>]]><var name="picture"/><![CDATA[</picture>]]>					</body>				</loop>		</template>','dd.MM.yyyy',0,0,0,16,17,0,0,'DE_de',0,0),(10,132,'tekknoforum','http://www.tekknoforum.de/','viewforum.php?f=2','http://www.triebwerk-dresden.de/templates/pwc-music/favicon.png',e'<loop item="articleUrl" index="i">				<!-- collects URLs of all articles from the front page -->				<list>		<loop item="linkTags" filter="unique">					<list>						   <xpath		expression=\'//div[@id="page-body"]//ul[@class="topiclist topics"]/li/dl/dt/a[@class="topictitle"]\'>							<html-to-xml treatdeprtagsascontent="1"					treatunknowntagsascontent="1">						<http url="${baseUrl}${startUrl}">						</http>								</html-to-xml>							</xpath>	</list>						<body>		<regexp>							    <regexp-pattern>([0-9]{2}\\.[0-9]{2}\\.[0-9]{2})</regexp-pattern>			    <regexp-source>    								<var name="linkTags" />							    </regexp-source>							    <regexp-result>        								    <case>								        <if condition=\'${startDate.toString().equals(_1.toString())}\'>		            <xpath expression="a/@href">					            	<var name="linkTags" />					            	</xpath>															        </if>								    </case>    </regexp-result>							</regexp>						</body>			</loop>								</list><!-- downloads each article and extract data from it -->			<body>					<template>			            <![CDATA[<article>]]>						    <call name="%uid:11%">						        <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>							</call><![CDATA[</article>]]>		            </template>			</body>			</loop>','dd.MM.yy',1247260531,1247260413,1,19,22,0,0,'DE_de',1,0),(11,132,'tekknoforum Sub01','http://www.tekknoforum.de/','','http://www.tekknoforum.de',e'<template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template><empty>			<var-def name="siteSnippet">				<html-to-xml advancedxmlescape="0" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="1" allowhtmlinsideattributes="1">			<http url="${pageUrl}" />				</html-to-xml>					</var-def>		</empty>            <template>            <![CDATA[<title>]]>		        <regexp replace="false" max="1">				    <regexp-pattern>[0-9]{2}\\.[0-9]{2}\\.[0-9]{2}[ ]*[\\W]([^@]*)@?.*$</regexp-pattern>		    <regexp-source>				        <xpath expression="normalize-space(data(//div[@id=\'page-body\']/div[4]/div[@class=\'inner\']/div[@class=\'postbody\']/h3))">							<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>			    				</regexp>    	            <![CDATA[</title>]]>            <![CDATA[<date>]]>            	<regexp replace="false" max="1">				    <regexp-pattern>([0-9]{2}\\.[0-9]{2}\\.[0-9]{2})</regexp-pattern>				    <regexp-source>				        <xpath expression="normalize-space(data(//div[@id=\'page-body\']/div[4]/div[@class=\'inner\']/div[@class=\'postbody\']/h3))">								<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>			    				</regexp>            <![CDATA[</date>]]>                       <![CDATA[<location>]]>            	<regexp replace="false" max="1">				    <regexp-pattern>@(.*)$</regexp-pattern>				    <regexp-source>        <xpath expression="normalize-space(data(//div[@id=\'page-body\']/div[4]/div[@class=\'inner\']/div[@class=\'postbody\']/h3))">			<var name="siteSnippet"/>						</xpath> 				    </regexp-source>			    <regexp-result><template>${_1}</template></regexp-result>		</regexp>            <![CDATA[</location>]]>                        <loop item="picture" index="i">            		<list>	            	<xpath expression="data(//div[@id=\'page-body\']/div[4]/div[@class=\'inner\']/div[@class=\'postbody\']//div[@class=\'content\']/img[1]/@src)">						<var name="siteSnippet"/></xpath>					</list>			<body>			            <![CDATA[<picture>]]><var name="picture"/><![CDATA[</picture>]]>					</body>				</loop>		</template>','dd.MM.yyyy',1247260566,1247260440,1,20,21,0,0,'DE_de',0,0),(12,132,'residentadvisor DD','http://www.residentadvisor.net/','events.aspx?ai=150&v=day&mn={startMonth}&yr={startYear}&dy={startDay}','http://www.residentadvisor.net/favicon.ico',e'<loop item="articleUrl" index="i">				<!-- collects URLs of all articles from the front page -->				<list>				   <xpath					expression=\'//div[@id="to"]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[2]/td/div/div[2]/a/@href\'>						<html-to-xml treatdeprtagsascontent="1"						treatunknowntagsascontent="1">						<http url="${baseUrl}${startUrl}">						</http>						</html-to-xml>	</xpath>				</list>				<!-- downloads each article and extract data from it -->			<body>					<template>			            <![CDATA[<article>]]>						    <call name="%uid:13%">						        <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>							</call>	<![CDATA[</article>]]>		            </template>		</body>			</loop>','dd.mm.yy',2009,0,0,23,26,0,0,'DE_de',0,0),(13,132,'residentadvisor DD sub01',e'.residentadvisor.net',NULL,NULL,e' <![CDATA[<title>]]><xpath expression="normalize-space(data(//span[@id=\'ctl00__subheader__title2\']))">								<var name="siteSnippet"/>						</xpath><![CDATA[</title>]]>            <![CDATA[<date>]]>            	        <xpath expression="normalize-space(data(//span[@id=\'ctl00__subheader__date\']/a))">								<var name="siteSnippet"/>						</xpath> 				    			                <![CDATA[</date>]]>                       <![CDATA[<location>]]><xpath expression="normalize-space(data(//span[@id=\'ctl00__subheader__venue\']/a))"><var name="siteSnippet"/>						</xpath><![CDATA[</location>]]>            	<loop item="picture" index="i">            		<list>	            	<xpath expression="data(//div[@id=\'ctl00__contentmain_EventDisplay\']/table/tbody/tr[1]/td[1]//a/img/@src)">						<var name="siteSnippet"/>					</xpath>			</list>					<body>			            <![CDATA[<picture>]]><var name="picture"/><![CDATA[</picture>]]>					</body>				</loop>            <template><![CDATA[<sourcelink>${pageUrl}</sourcelink>]]></template>','dd.mm.yy',2009,0,0,24,25,0,0,'DE_de',0,0);

--
-- Table structure for table `tx_lrmediator_pattern_mm`
--

DROP TABLE IF EXISTS `tx_lrmediator_pattern_mm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tx_lrmediator_pattern_mm` (
`uid` int NOT NULL AUTO_INCREMENT,
`pid`  NOT NULL DEFAULT '0',
`tstamp` int NOT NULL DEFAULT '0',
`crdate` int NOT NULL DEFAULT '0',
`cruser_id`  NOT NULL DEFAULT '0',
`parent`  DEFAULT NULL,
`child`  DEFAULT NULL,
`hidden` int NOT NULL DEFAULT '0',
PRIMARY KEY (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tx_lrmediator_pattern_mm`
--

LOCK TABLES `tx_lrmediator_pattern_mm` WRITE;
/*!40000 ALTER TABLE `tx_lrmediator_pattern_mm` DISABLE KEYS */;
INSERT INTO `tx_lrmediator_pattern_mm` VALUES (1,132,0,0,0,2,3,0),(2,132,0,0,0,5,6,0),(3,132,0,0,0,5,7,0),(4,132,0,0,0,8,9,0),(5,132,1247260413,1247260413,1,10,11,0);
/*!40000 ALTER TABLE `tx_lrmediator_pattern_mm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tx_lrmediator_term_weight`
--

DROP TABLE IF EXISTS `tx_lrmediator_term_weight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE tx_lrmediator_term_weight (
uid serial,
pid int DEFAULT '0',
term varchar DEFAULT NULL,
weight float NOT NULL DEFAULT '0',
tstamp int DEFAULT '0',
crdate int DEFAULT '0',
cruser_id int DEFAULT '0',
deleted int DEFAULT '0',
PRIMARY KEY (uid),
UNIQUE KEY term_5 (term),
KEY parent (pid)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tx_lrmediator_term_weight`
--

LOCK TABLES `tx_lrmediator_term_weight` WRITE;
/*!40000 ALTER TABLE `tx_lrmediator_term_weight` DISABLE KEYS */;
INSERT INTO `tx_lrmediator_term_weight` VALUES (1,132,'club',0.1,0,0,0,0),(2,132,'bar',0.1,0,0,0,0),(3,132,'lounge',0.1,0,0,0,0),(4,132,'e.v.',0.1,0,0,0,0),(5,132,'Gelände',0.2,0,0,0,0),(6,132,'alter',0.1,0,0,0,0),(7,132,'alte',0.1,0,0,0,0),(8,132,'altes',0.1,0,0,0,0),(9,132,'dresden',0.05,1250769753,1250769753,1,0),(10,132,'live',0.01,1250769804,1250769783,1,0);
/*!40000 ALTER TABLE `tx_lrmediator_term_weight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tx_lrmediator_user_pattern_mm`
--

DROP TABLE IF EXISTS `tx_lrmediator_user_pattern_mm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tx_lrmediator_user_pattern_mm` (
`uid` int NOT NULL AUTO_INCREMENT,
`pid` int NOT NULL DEFAULT '0',
`sid` int NOT NULL DEFAULT '0',
`pattern` int NOT NULL DEFAULT '0',
`tstamp` int NOT NULL DEFAULT '0',
`crdate` int NOT NULL DEFAULT '0',
`cruser_id`  NOT NULL DEFAULT '0',
`deleted` int NOT NULL DEFAULT '0',
`priority`  DEFAULT '0',
`additional` int NOT NULL DEFAULT '0',
`hidden` int NOT NULL DEFAULT '0',
`fe_user` int NOT NULL DEFAULT '0',
PRIMARY KEY (`uid`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tx_lrmediator_user_pattern_mm`
--

LOCK TABLES `tx_lrmediator_user_pattern_mm` WRITE;
/*!40000 ALTER TABLE `tx_lrmediator_user_pattern_mm` DISABLE KEYS */;
INSERT INTO `tx_lrmediator_user_pattern_mm` VALUES (1,132,0,1,0,0,0,0,100,0,0,3),(2,132,0,8,0,0,0,0,50,0,0,3),(3,132,0,2,0,0,0,0,100,0,0,3),(4,132,0,5,0,0,0,0,100,0,0,3),(5,132,0,10,0,0,0,0,30,0,0,3),(6,132,0,4,0,0,0,0,30,0,0,3);
/*!40000 ALTER TABLE `tx_lrmediator_user_pattern_mm` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tx_lrmediator_user_rules`
--

DROP TABLE IF EXISTS `tx_lrmediator_user_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tx_lrmediator_user_rules` (
`uid` int NOT NULL AUTO_INCREMENT,
`pid` int DEFAULT '0',
`rule_type` smallint(5) unsigned DEFAULT NULL,
`rule_input` varchar DEFAULT NULL,
`priority_change` smallint(6) DEFAULT '0',
`tstamp` int DEFAULT '0',
`crdate` int DEFAULT '0',
`cruser_id`  DEFAULT '0',
`deleted` int DEFAULT '0',
`fe_user` int NOT NULL DEFAULT '0',
`hidden` int NOT NULL DEFAULT '0',
PRIMARY KEY (`uid`),
KEY `parent` (`pid`)
) ENGINE=MyISAM AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tx_lrmediator_user_rules`
--

LOCK TABLES `tx_lrmediator_user_rules` WRITE;
/*!40000 ALTER TABLE `tx_lrmediator_user_rules` DISABLE KEYS */;
INSERT INTO `tx_lrmediator_user_rules` VALUES (1,132,0,'/Berlin/i',0,1247049000,1247049000,1,0,3,0),(2,132,0,'/Leipzig/i',0,1247049031,1247049031,1,0,3,0),(3,132,0,'/Mei.en/i',0,1247049320,1247049320,1,0,3,0),(4,132,1,'theater',0,1247049338,1247049338,1,0,3,0),(5,132,1,'radiosendung',0,1247049360,1247049360,1,0,3,0),(6,0,1,'film',-1000,0,0,0,0,3,0),(8,132,0,'/G.rlitz/i',0,1247239588,1247239588,1,0,3,0),(15,2,0,'/zebra disco/',0,0,0,0,0,3,0),(14,2,0,'/D.beln/',0,0,0,0,0,3,0),(16,2,0,'/zebra/',0,0,0,0,0,3,0),(19,2,0,'/afterwork.purobeach/',0,0,0,0,0,3,0);
/*!40000 ALTER TABLE `tx_lrmediator_user_rules` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-26 18:50:37
