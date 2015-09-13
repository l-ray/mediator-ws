INSERT INTO pattern (id, type, name, url, starturl, icon, pattern, subpattern, dateformat, countrycode, deleted, hidden) VALUES
  ( 0, 'webharvest', 'dummyData l-ray',  'http://www.l-ray.de/fileadmin/',      'demodata.html',     'http://www.l-ray.de/fileadmin/template/css/images/ico_cached.gif', E'<loop item="event" index="i"><list><xpath expression=\'//div[@class="event"]\'><html-to-xml treatdeprtagsascontent="1" treatunknowntagsascontent="1"><http url="${baseUrl}${startUrl}" /></html-to-xml></xpath></list><body><empty><var-def name="test"><xpath expression=\'//div[@class="name"]/text()\'><var name="event"/></xpath></var-def></empty><template><![CDATA[<results>]]><![CDATA[<title>]]><xpath expression=\'//div[@class="name"]/text()\'><var name="event"/></xpath><![CDATA[</title>]]><![CDATA[<subtitle>]]><xpath expression=\'//div[@class="subtitle"]/text()\'><var name="event"/></xpath><![CDATA[</subtitle>]]><![CDATA[<price>]]><xpath expression=\'//span[@class="price"]/text()\'><var name="event"/></xpath><![CDATA[</price>]]><![CDATA[<pictures>]]>${baseUrl}<xpath expression=\'//img/@src\'><var name="event"/></xpath><![CDATA[</pictures>]]><![CDATA[<start>]]>${startDate}<![CDATA[</start>]]><![CDATA[<location>]]>dummyLocation<![CDATA[</location>]]><![CDATA[<url>]]>${baseUrl}<![CDATA[</url>]]><![CDATA[</results>]]></template></body></loop>', null, 'EEEE, dd. MMMM', 'DE_de', 0, 0),
  ( 2, 'webharvest', 'salsa.ie',    'http://salsa.ie/listings/','ByCounty/leinster/dublin/',          'http://salsa.ie/favicon.ico', E'<loop item="event" index="i">
    <list>
        <xpath expression="//table[@class=\'events-table\']/tbody/tr">
            <html-to-xml treatdeprtagsascontent="1" treatunknowntagsascontent="1">
                <http url="${baseUrl}${startUrl}"/>
            </html-to-xml>
        </xpath>
    </list>
    <body>
        <empty>
            <var-def name="test">
                <xpath expression="//td[2]/text()">
                    <var name="event"/>
                </xpath>
            </var-def>
        </empty>
        <case><if condition="${test.toString().indexOf(startDate.toString())!=-1}">
        <template><![CDATA[<results>]]>
        	
        	<![CDATA[<title>]]>
            <xpath expression="//td[1]/a/text()">
                <var name="event"/>
            </xpath>
            <![CDATA[</title>]]>
                    
            <![CDATA[<pictures>]]>
            <xpath expression="//img/@src">
                <var name="event"/>
            </xpath>
            <![CDATA[</pictures>]]>
            
            <![CDATA[<start>]]>
            <regexp replace="true">
			    <regexp-pattern>^([0-9]{1,2}:[0-9]{2} [ap]m).*</regexp-pattern>
			    <regexp-source><xpath expression="//td[2]/text()"><var name="event"/></xpath></regexp-source>
			    <regexp-result><template>${_1}</template></regexp-result>
			</regexp>
            <![CDATA[</start>]]>
            
            <![CDATA[<location>]]><xpath expression="//td[1]/i/text()">
                <var name="event"/>
            </xpath><![CDATA[</location>]]>
            
            <![CDATA[<url>]]><xpath expression="//td[1]/a/@href">
                <var name="event"/>
            </xpath><![CDATA[</url>]]>
            <![CDATA[</results>]]>
        </template>
        </if></case>
    </body>
</loop>', null, 'EEEE, d MMMM', 'EN_us', 0, 0),
  ( 3, 'webharvest', 'DoubleD_Town',     'http://www.doubled-town.de/forum/', 'viewforum.php?f=20', 'http://www.doubled-town.de/favicon.ico', E'<loop item="articleUrl" index="i">  <list> <loop item="linkTags" filter="unique">  	<list>	   <xpath expression=\'//div[@class="mForumMain mForumMainItemBorderSmall"]/div[@class="mForumMainItem"]/div[@class="mForumMainItemText"]/span/a[@class="topictitle"]\'>    <html-to-xml treatdeprtagsascontent="1"   treatunknowntagsascontent="1">   <http url="${baseUrl}${startUrl}">   </http></html-to-xml>   	</xpath>	</list>   <body> <regexp>	    <regexp-pattern>([0-9]{2}\.[0-9]{2}\.[0-9]{2})</regexp-pattern>     <regexp-source>        <var name="linkTags" />   	    </regexp-source>   	    <regexp-result>                <case>            <if condition=\'${startDate.toString().equals(_1.toString())}\'>             <xpath expression="a/@href">  	            	<var name="linkTags" />  	            	</xpath>        </if>        </case>    </regexp-result>   	</regexp>   </body> 	</loop></list><!-- downloads each article and extract data from it --> 	<body>  	<template> 	            <![CDATA[<results>]]>       <call name="DoubleD_Town_Sub01">           <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>   	</call><![CDATA[<url>]]>${baseUrl}<![CDATA[</url>]]> 	            <![CDATA[</results>]]>             </template>	</body> 	</loop>', E'<function name="DoubleD_Town_Sub01"><return><template><![CDATA[<url>${sys.escapeXml(pageUrl.toString())}</url>]]></template> <empty> <var-def name="siteSnippet"> <html-to-xml advancedxmlescape="0" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="1" allowhtmlinsideattributes="1"> 	<http url="${pageUrl}" />  </html-to-xml> </var-def> </empty> 	<template>            <![CDATA[<title>]]>            	<xpath expression="normalize-space(data(//div[2]/div[4]/div/div/span/b))"> <var name="siteSnippet"/> </xpath>            <![CDATA[</title>]]>            <![CDATA[<date>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[1]/div[@class="mPostMainItemText"]/span))\'>  	<var name="siteSnippet"/>  </xpath>            <![CDATA[</date>]]>            <![CDATA[<price>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[2]/div[@class="mPostMainItemText"]/span))\'> <var name="siteSnippet"/></xpath> 	<![CDATA[</price>]]>                        <![CDATA[<location>]]>            	<xpath expression=\'normalize-space(data(//div[2]/div[6]/div[3]/div[@class="mPostMainItemText"]/span))\'><var name="siteSnippet"/>  </xpath>            <![CDATA[</location>]]>            <![CDATA[<pictures>]]><xpath expression=\'normalize-space(data(//div[3]/div[3]/a/@href))\'><var name="siteSnippet"/></xpath><![CDATA[#.jpg</pictures>]]> </template></return></function>', 'dd.MM.yy', 'DE_de', 0, 1),
  ( 1, 'webharvest', 'exma-amt',         'http://www.exmatrikulationsamt.de/',e'?act=events\&d={startDay}\&m={startMonth}\&j={startYear}', 'http://www.exmatrikulationsamt.de/favicon.ico', e'<loop item="articleUrl" index="i"><list><xpath expression=\'//table[@id="maintable"]/tbody/tr[1]/td[2]/div[@id="wrapper"]/div/table/tbody/tr/td[1]/div[@id="ev_cat_Partys"]/div/div/table/tbody/tr/td[2]/a[1]/@href\'><html-to-xml treatdeprtagsascontent="1"						treatunknowntagsascontent="1" advancedxmlescape="0">			<http url="${baseUrl}${startUrl}">						</http>								</html-to-xml>							</xpath>					</list><body>					<xquery>			<xq-param name="doc">							<html-to-xml  treatdeprtagsascontent="1" treatunknowntagsascontent="1" advancedxmlescape="0" allowhtmlinsideattributes="1">				<http url="${articleUrl}" />						</html-to-xml>						</xq-param><xq-param name="url" type="string">                                                	<var name="articleUrl" />                                                </xq-param><xq-param name="startDate" type="string">
        <var name="startDate"/>
    </xq-param><xq-expression><![CDATA[        				declare variable $doc as node() external;declare variable $url as xs:string external;declare variable $startDate as xs:string external;                                                let $title :=    data($doc//table[@id="maintable"]/tbody/tr[1]/td[2]/div[@id="wrapper"]/div[1]/div[2]//span[1]/text())
        let $location := data($doc//table[@id="maintable"]/tbody/tr[1]/td[2]/div[@id="wrapper"]/div[1]/div[2]/div/div[@id="post"]/div/div[2]/div[3]/a[1]/text())
        let $picture :=  data($doc//table[@id="maintable"]/tbody/tr[1]/td[2]/div[@id="wrapper"]/div[1]/div[2]/div/div[@id="post"]/div//img/@src)                        return                                <results>                                                     					<location>{data($location)}</location>                                    <title>{data($title)}</title>{                                    for $x in $picture		return <pictures>{data($x)}</pictures>                                    }<url>{data($url)}</url>
        <start>{data($startDate)}</start>
        </results>                    ]]></xq-expression>			</xquery>				</body>			</loop>', null, 'dd.MM.yy','DE_de', 0, 1),
  ( 5, 'webharvest', 'banq',             'http://www.banq.de/',               'termine.php?date={startTimestamp}', 'http://www.banq.de/ico/favicon.ico', e'<loop item="item" filter="unique"><list><xpath expression=\'//div[attribute::id="mwtext"]//tr/td[attribute::id="cptable1"]/div[attribute::id="cptable2"]/span/text()\'><html-to-xml>					<http url="${baseUrl}${startUrl}" />				</html-to-xml>		</xpath>		</list>		<body>			<var-def name="newItem">			<call name="Banq_-_Details"><call-param name="title"><var name="item" /></call-param><call-param name="url"><template>${sys.escapeXml(baseUrl.toString()+startUrl.toString())}</template></call-param>	        	<call-param name="document">		        	<html-to-xml>		<http url="${baseUrl}${startUrl}" />					</html-to-xml>       	</call-param>	</call></var-def>				</body>	</loop>', '<function name="Banq_-_Details"><return><xquery><xq-param name="doc"><var name="document" />       </xq-param>            <xq-param name="title" type="string"><var name="title" />            </xq-param>            <xq-param name="url" type="string"><var name="url" />            </xq-param>            <xq-expression><![CDATA[			                declare variable $doc as node() external;declare variable $title as xs:string external;declare variable $url as xs:string external;			                				                for $b in $doc//div[attribute::id="mwtext"],				                	$t in $b//tr/td[attribute::id="cptable1"]/div[attribute::id="cptable2"]/span/child::text(),                					                	$c in $t//ancestor::tr/preceding-sibling::tr/td[attribute::id="cptable"]/table//h3/child::text()where $t=$title return 									<results><title>{$t}</title><url>{$url}</url><date>{data($t//ancestor::tr/preceding-sibling::tr/td[attribute::id="cptable"]/table//h3)}</date>			                            <location>{data($t//ancestor::tr/following-sibling::tr/td[attribute::id="cptable"]/a)}</location>			                            <category>{data($c/ancestor::td/following-sibling::td/span[attribute::class="kategorie"])}</category>			                            <link>{data($c/ancestor::td/following-sibling::td/span/a/@href)}</link>		                            </results>			            ]]></xq-expression>        </xquery>    </return></function><function name="Banq-PictureLinks"><return>    <xpath expression="//div/img">        <html-to-xml allowhtmlinsideattributes="true">            <http url="${pictureLink}" />        </html-to-xml>    </xpath></return></function><function name="Banq-PictureLinks"><return>    <xpath expression="//div/img">        <html-to-xml allowhtmlinsideattributes="true">            <http url="${pictureLink}" />        </html-to-xml>    </xpath></return></function>','dd.MM.yy', 'DE_de', 0, 1),
  ( 4, 'webharvest', 'workmans club','http://theworkmansclub.com/',               'events/', 'http://theworkmansclub.com/wp-content/themes/workmans/library/images/marker.png', e'<loop item="articleUrl" index="i">
	<list>
		<loop item="linkTags" filter="unique">
			<list>
				<xpath expression="//div[@class=''date_list'']/div">
					<html-to-xml treatdeprtagsascontent="1"   treatunknowntagsascontent="1">
						<http url="${baseUrl}${startUrl}"></http>
					</html-to-xml>
				</xpath>
			</list>
			<body>
				<empty>
            <var-def name="day"><xpath expression="//div[@class=''day'']/text()"><var name="linkTags" /></xpath></var-def>
            <var-def name="month"><xpath expression="//div[@class=''month'']/text()"><var name="linkTags" /></xpath></var-def>
            <var-def name="eventDate"><template>${day.toString()} ${month.toString()}</template></var-def>
          </empty>
          <case>
            <if condition="${eventDate.toString().equals(startDate.toString())}">
                <xpath expression="/div/a/@href">
                    <var name="linkTags" />
                </xpath>
            </if>
         </case>
			</body>
		</loop>
	</list>
	<body>
		<template>
			<![CDATA[<results>]]>
				<call name="wmc01">
					<call-param name="pageUrl"><template>${articleUrl}</template></call-param>
				</call>
				<![CDATA[<location>The Workmans Club</location>]]>
				<![CDATA[<url>]]>${articleUrl}<![CDATA[</url>]]>
			<![CDATA[</results>]]>
		</template>
	</body>
</loop>', '<function name="wmc01">
	<return>
		<template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>
		<empty>
			<var-def name="siteSnippet">
				<html-to-xml advancedxmlescape="0" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="0" allowhtmlinsideattributes="1">
						<http url="${pageUrl}" />
				</html-to-xml> </var-def>
		</empty>
		<template>
			<![CDATA[<title>]]><xpath expression="normalize-space(data(//div[@id=''contentsingle'']//article/header/div/div/h1[@class=''event_title'']))">
					<var name="siteSnippet"/>
				</xpath><![CDATA[</title>]]>
			<![CDATA[<start>]]><xpath expression="normalize-space(data(//div[@id=''contentsingle'']//article/header/div[2]/div/div[1]/div[@class=''eventdate'']/span))">
					<var name="siteSnippet"/>
				</xpath><![CDATA[</start>]]>
			<![CDATA[<price>]]><xpath expression="normalize-space(data(//div[@id=''contentsingle'']//article/header/div[2]/div/div[1]/div[@class=''ticket_price_single'']/span[1]))">
					<var name="siteSnippet"/>
				</xpath><![CDATA[</price>]]>
			<![CDATA[<description>]]><xpath expression="normalize-space(data(//div[@id=''contentsingle'']//article/section))">
					<var name="siteSnippet"/>
				</xpath><![CDATA[</description>]]>
			<![CDATA[<pictures>]]><xpath expression="normalize-space(data(//div[@id=''contentsingle'']/div/img/@src))"><var name="siteSnippet" /></xpath><![CDATA[</pictures>]]>
		</template>
	</return>
</function>','dd MMM', 'EN_us', 0, 0),
  ( 6, 'webharvest', 'Dublin Concerts','http://www.dublinconcerts.ie/', 'upcoming-gigs/', 'http://www.dublinconcerts.ie/content/themes/dublin/images/ico.png', e'<loop item="articleUrl" index="i">
<list>
<loop item="linkTags" filter="unique">
<list>
<xpath expression="//*[@itemtype=''http://data-vocabulary.org/Event'']">
<html-to-xml treatdeprtagsascontent="1"   treatunknowntagsascontent="0">
<http url="${baseUrl}${startUrl}"></http>
</html-to-xml>
</xpath>

</list>
<body>
<empty>
<var-def name="eventDate"><xpath expression="substring(data(//*[@itemprop=''startDate'']/@datetime),0,11)">
<var name="linkTags" />
</xpath></var-def>
</empty>
<case>
<if condition="${eventDate.toString().equals(startDate.toString())}">
<xpath expression="//*[@itemprop=''url'']/@href">
<var name="linkTags" />
</xpath>
</if>
</case>
</body>
</loop>
</list>
<body>
<template>
<![CDATA[<results>]]>
<call name="upcoming01">
<call-param name="pageUrl"><template>${articleUrl}</template></call-param>
</call>
<![CDATA[<start>]]>${startDate}<![CDATA[</start>]]>
<![CDATA[<url>]]>${articleUrl}<![CDATA[</url>]]>
<![CDATA[</results>]]>
</template>
</body>
</loop>
', '<function name="upcoming01">
        <return>
                <template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>
                <empty>
                        <var-def name="siteSnippet">
                                <html-to-xml specialentities="0" advancedxmlescape="1" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="0" prunetags="script,style">
                                                <http url="${pageUrl}" />
                                </html-to-xml> </var-def>

                </empty>
                <template>
                        <![CDATA[<title>]]>
                                <xpath expression="normalize-space(data(//*[@id=''main-content-inner'']/div/div[1]/section[1]/div/div/p[1]/a))">
                                        <var name="siteSnippet"/>
                                </xpath>
                        <![CDATA[</title>]]>
                        <![CDATA[<location>]]>
                                <xpath expression="normalize-space(data(//*[@id=''main-content-inner'']/div/div[1]/section[1]/div/div/p[3]/a[1]))">
                                        <var name="siteSnippet"/>
                                </xpath>
                        <![CDATA[</location>]]>
                        <![CDATA[<price>]]>
                          <regexp replace="true" >
                            <regexp-pattern>^Tickets:(.*)</regexp-pattern>
                            <regexp-source><xpath expression="normalize-space(data(//*[@id=''main-content-inner'']/div/div[1]/section[1]/div/div/p[4]))">
                                <var name="siteSnippet"/>
                            </xpath></regexp-source>
                            <regexp-result><template>${_1}</template></regexp-result>
                          </regexp>                        <![CDATA[</price>]]>
                        <![CDATA[<pictures>]]><xpath expression="normalize-space(data(//*[@id=''main-content-inner'']//section/div[@class=''inner-box'']/div/a[@rel=''prettyPhoto'']/img/@src))"><var name="siteSnippet" /></xpath><![CDATA[</pictures>]]>
                </template>
        </return>
</function>','YYYY-MM-dd', 'EN_us', 0, 0),
  ( 7, 'webharvest', 'Resident Advisor Dublin','http://www.residentadvisor.net', '/events.aspx?ai=43&v=day&mn={startMonth}&yr={startYear}&dy={startDay}', 'http://www.residentadvisor.net/favicon.ico', e'<loop item="articleUrl" index="i">
        <list>
                <loop item="linkTags" filter="unique">
                        <list>
                                <xpath expression="//*[@itemtype=''http://data-vocabulary.org/Event'']">
                                        <html-to-xml treatdeprtagsascontent="1"   treatunknowntagsascontent="0">
                                                <http url="${baseUrl}${startUrl}"></http>
                                        </html-to-xml>
                                </xpath>

                        </list>
                        <body>
                                <empty>
			                        <var-def name="eventDate">
			                        	<xpath expression="substring(data(//*[@itemprop=''startDate'']/@datetime),0,11)">
			                                        <var name="linkTags" />
			                        </xpath></var-def>
                                </empty>

								<xpath expression="//*[@itemprop=''url'']/@href">
                                    <var name="linkTags" />
                            	</xpath>

                        </body>
                </loop>
        </list>
        <body>
                <template>
                        <![CDATA[<results>]]>
                                <call name="ra01">
                                        <call-param name="pageUrl"><template>${baseUrl}${articleUrl}</template></call-param>
                                </call>
								<![CDATA[<start>]]>${eventDate}<![CDATA[</start>]]>

                                <![CDATA[<url>]]>${articleUrl}<![CDATA[</url>]]>
                        <![CDATA[</results>]]>
                </template>
        </body>
</loop>', '<function name="ra01">
        <return>
                <template><![CDATA[<sourcelink>${sys.escapeXml(pageUrl.toString())}</sourcelink>]]></template>
                <empty>
                        <var-def name="siteSnippet">
                                <html-to-xml advancedxmlescape="0" specialentities="false" treatdeprtagsascontent="1" unicodechars="0" treatunknowntagsascontent="0" allowhtmlinsideattributes="0" >
                                                <http url="${pageUrl}" />
                                </html-to-xml> </var-def>

                </empty>
                <template>
                        <![CDATA[<title>]]>
                          <regexp replace="1">
                              <regexp-pattern>&amp; </regexp-pattern>
                              <regexp-source>
                                <xpath expression="normalize-space(data(//header[@id=''header'']//div[@id=''sectionHead'']/h1/text()))">
                                        <var name="siteSnippet"/>
                                </xpath>
                              </regexp-source>
                              <regexp-result><template>&amp;amp; </template></regexp-result>
                          </regexp>
                        <![CDATA[</title>]]>
                        <![CDATA[<location>]]>
                          <regexp replace="1">
                              <regexp-pattern>&amp; </regexp-pattern>
                              <regexp-source>
                                <xpath expression="normalize-space(data(//aside[@id=''detail'']/ul/li[2]))">
                                        <var name="siteSnippet"/>
                                </xpath>
                              </regexp-source>
                              <regexp-result><template>&amp;amp; </template></regexp-result>
                          </regexp>
                        <![CDATA[</location>]]>
                        <![CDATA[<price>]]>
                                <xpath expression="normalize-space(data(//aside[@id=''detail'']/ul/li[3]))">
                                        <var name="siteSnippet"/>
                                </xpath>
                        <![CDATA[</price>]]>
                        <![CDATA[<pictures>]]><xpath expression="data(//*[@id=''event-item'']//div[@class=''flyer'']/a/img/@src)"><var name="siteSnippet" /></xpath><![CDATA[</pictures>]]>
                        <![CDATA[<description>]]>
                          <regexp replace="1">
                              <regexp-pattern>&amp; </regexp-pattern>
                              <regexp-source>
                                <xpath expression="normalize-space(data(string-join(//*[@id=''event-item'']/div[@class=''left'']/p/text(),'' '')))"><var name="siteSnippet" /></xpath>
                              </regexp-source>
                              <regexp-result><template>&amp;amp; </template></regexp-result>
                          </regexp>
                        <![CDATA[</description>]]>
                </template>
        </return>
</function>','YYYY-MM-dd', 'EN_us', 0, 0);