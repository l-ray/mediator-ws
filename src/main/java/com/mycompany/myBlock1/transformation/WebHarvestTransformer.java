package com.mycompany.myBlock1.transformation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cocoon.transformation.AbstractDOMTransformer;
import org.w3c.dom.Document;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

			// ScraperConfiguration config = new
			// ScraperConfiguration("c:/temp/scrapertest/configs/test2.xml");
			// ScraperConfiguration config = new
			// ScraperConfiguration("location-examples/banqxquery.xml");
			ScraperConfiguration config = new ScraperConfiguration(
					new InputSource(new StringReader(stringWriter.getBuffer()
							.toString())));
			// ScraperConfiguration config = new
			// ScraperConfiguration(this.getSource());
			// ScraperConfiguration config = new ScraperConfiguration( new
			// URL("http://localhost/scripts/test/sample8.xml") );
			Scraper scraper = new Scraper(config, "results");
			scraper.setDebug(true);
			/*
			 * if (_PROXY_HOST != "")
			 * scraper.getHttpClientManager().setHttpProxy(_PROXY_HOST,
			 * _PROXY_PORT);
			 */
			long startTime = System.currentTimeMillis();
			scraper.execute();

			System.out.println("time elapsed: "
					+ (System.currentTimeMillis() - startTime));

			String myXMLAnswer = "<results>"
					+ (scraper.getContext().getVar("result")).toString()
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
