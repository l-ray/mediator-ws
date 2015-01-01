package com.mycompany.myBlock1.transformation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.w3c.dom.Document;
import org.webharvest.Harvest;
import org.webharvest.HarvestLoadCallback;
import org.webharvest.Harvester;
import org.webharvest.definition.*;
import org.webharvest.events.*;
import org.webharvest.ioc.ContextFactory;
import org.webharvest.ioc.HarvesterFactory;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.*;
import org.webharvest.runtime.processors.Processor;
import org.webharvest.runtime.processors.ProcessorResolver;
import org.webharvest.runtime.web.HttpClientManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 * Generates source-answer to the given template. It uses the webharvester for doing so.
 * @author lray
 */
public class WebHarvestTransformer extends AbstractDOMTransformer {

	private static String _PROXY_HOST;

	private static int _PROXY_PORT;

	protected Document transform(Document document) {

		try {

			Source source = new DOMSource(document);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(source, result);

            final Injector injector = Guice.createInjector(
                    new ScraperModule("."),
                    new HttpModule(HttpClientManager.ProxySettings.NO_PROXY_SET)
            );

            final Harvest harvest = injector.getInstance(Harvest.class);

            ConfigSource configSource = new BufferConfigSource(stringWriter.getBuffer()
							.toString());

            Harvester harvester = harvest.getHarvester(configSource,
                    new HarvestLoadCallback() {
                        @Override
                        public void onSuccess(List<IElementDef> elements) {
                            // TODO: Auto-generated method stub :-/
                        }
                    });
            //ScrapingHarvester scraper = new ScrapingHarvester(configSource, "results");
			//scraper.setDebug(true);
			/*
			 * if (_PROXY_HOST != "")
			 * scraper.getHttpClientManager().setHttpProxy(_PROXY_HOST,
			 * _PROXY_PORT);
			 */
			long startTime = System.currentTimeMillis();
			DynamicScopeContext scraperContext = harvester.execute(new Harvester.ContextInitCallback() {
                @Override
                public void onSuccess(DynamicScopeContext context) {
                    // TODO: add initial variables to the scrapers content, if any
                }
            });

			System.out.println("time elapsed: "
					+ (System.currentTimeMillis() - startTime));

			String myXMLAnswer = "<results>"
					+ (scraperContext.getVar("result")).toString()
					+ "</results>";
			
			System.out.println(myXMLAnswer);

			final DocumentBuilderFactory factory2 = DocumentBuilderFactory
					.newInstance();
			factory2.setValidating(false);
			//factory2.setAttribute(OutputKeys.ENCODING, "UTF-8");
			/*factory2.setIgnoringElementContentWhitespace(true);*/
			//factory2.set
			final DocumentBuilder builder = factory2.newDocumentBuilder();
			
			StringReader sr = new StringReader(myXMLAnswer);
			InputSource myIS = new InputSource(sr);
			
			//InputSource myIS = new InputSource(new InputStreamReader(new StringBufferInputStream(myXMLAnswer),"UTF-8"));
			
			document = builder.parse(myIS);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;
	}
}
