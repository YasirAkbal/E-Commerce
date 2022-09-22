package utils;

import java.io.*;
import java.util.List;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import entities.abstracts.EntityI;
import messages.XmlMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.SuccessDataResult;
import xmlUtils.abstracts.XmlFormatterI;

public final class XmlUtils {
	
	private XmlUtils() {}
	
	private static final String RESULT = "result";
	private static DocumentBuilderFactory factory;
	
	
	public static DocumentBuilderFactory getFactory() {
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}

		return factory;
	}
	
	public static Document parse(String path) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = getFactory().newDocumentBuilder();
		Document document = builder.parse(path);
		return document;
	}
	
	public static Document parse(InputStream in) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = getFactory().newDocumentBuilder();
		Document document = builder.parse(in);

		return document;
	}

	public static String getSingleElementText(Element parent, String tag, String defaultValue) {

		NodeList elementList = parent.getElementsByTagName(tag);
		if (elementList.getLength() == 0) {
			return defaultValue;
		}
		return elementList.item(0).getTextContent();
	}

	public static double getSingleElementText(Element parent, String tag, double defaultValue) {
		String string = getSingleElementText(parent, tag, Double.toString(defaultValue));
		return Double.parseDouble(string);
	}

	public static long getSingleElementText(Element parent, String tag, long defaultValue) {
		String string = getSingleElementText(parent, tag, Long.toString(defaultValue));
		return Long.parseLong(string);
	}
	
	public static int getSingleElementText(Element parent, String tag, int defaultValue) {
		String string = getSingleElementText(parent, tag, Integer.toString(defaultValue));
		return Integer.parseInt(string);
	}
	
	public static String getAttribute(Element parent, String name, String defaultValue) {
		if (parent.getAttribute(name) == null  || parent.getAttribute(name).isBlank()) {
			return defaultValue;
		}
		return parent.getAttribute(name);
	}

	public static long getAttribute(Element parent, String name, long defaultValue) {
		String string = getAttribute(parent, name, Long.toString(defaultValue));
		return Long.parseLong(string);
	}

	public static Document create(String root) throws ParserConfigurationException {
		DocumentBuilder builder = getFactory().newDocumentBuilder();
		Document document = builder.newDocument();
		Element element = document.createElement(root);
		document.appendChild(element);
		return document;
	}

	public static void addSingleElementText(Document document, Element parent, String tag, String content) {
		Element name = document.createElement(tag);
		name.setTextContent(content);
		parent.appendChild(name);
	}

	public static void addSingleElementText(Document document, Element parent, String tag, double content) {
		String string = Double.toString(content);
		addSingleElementText(document, parent, tag, string);
	}
	
	public static void addSingleElementText(Document document, Element parent, String tag, long content) {
		String string = Long.toString(content);
		addSingleElementText(document, parent, tag, string);
	}

	public static void dump(Document document, String path) throws IOException, TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMSource data = new DOMSource(document);
		FileWriter writer = new FileWriter(new File(path));
		StreamResult result = new StreamResult(writer);
		transformer.transform(data, result);
		writer.close();
	}

	public static void dump(Document document, OutputStream stream) throws IOException, TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMSource data = new DOMSource(document);
		StreamResult result = new StreamResult(stream);
		transformer.transform(data, result);
		stream.close();
	}
	
	private static void setStatus(Document document, String status) {
		Element documentElement = document.getDocumentElement();
		
		Element statusElement = document.createElement(XmlMessages.SUCCESS);
		statusElement.setTextContent(status);
		
		documentElement.insertBefore(statusElement, documentElement.getFirstChild());
	}
	
	public static void setSuccessTrue(Document document) {
		setStatus(document, XmlMessages.TRUE);
	}
	
	public static void setSuccessFalse(Document document) {
		setStatus(document, XmlMessages.FALSE);
	}
	
	public static void setStatus(Document document, results.Result result) {
		if(result.isSuccess()) {
			setSuccessTrue(document);
		} else {
			setSuccessFalse(document);
		}
	}
	
	public static Document createStatusXml(results.Result result, OutputStream outputStream) throws ParserConfigurationException {		
		if(result.isSuccess()) {
			return createSuccessTrueXml();
		} else {
			return createSuccessFalseXml();
		}
	}
	
	public static Document createSuccessTrueXml() throws ParserConfigurationException {
		Document outputDocument = XmlUtils.create(RESULT);
		
		XmlUtils.setSuccessTrue(outputDocument);
		
		return outputDocument;
	}
	
	public static Document createSuccessFalseXml() throws ParserConfigurationException {
		Document outputDocument = XmlUtils.create(RESULT);
		
		XmlUtils.setSuccessFalse(outputDocument);
		
		return outputDocument;
	}
	
	public static boolean getStatusFromXml(InputStream inputStream) {
		Document responseDocument;
		try {
			responseDocument = XmlUtils.parse(inputStream);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			return false;
		}
		Element responseElement = responseDocument.getDocumentElement();
		String status = XmlUtils.getSingleElementText(responseElement, XmlMessages.SUCCESS, "defaultValue"); 
		
		return status.equals(XmlMessages.TRUE);
	}
	
	public static<T extends EntityI> DataResult<Document> 
	createResponseDocumentFromDataResult(DataResult<T> result, XmlFormatterI<T> formatter) throws ParserConfigurationException {
		Document outputDocument;
		
		if(result.isSuccess()) {
			T entity = result.getData();
			DataResult<Document> resultFromFormatter = formatter.format(entity);
			
			outputDocument = setStatusToDocument(resultFromFormatter);
			return new SuccessDataResult<>(outputDocument);
		} else {
			outputDocument = createSuccessFalseXml();
			return new ErrorDataResult<>(outputDocument);
		}
	}
	
	public static<T extends EntityI> DataResult<Document> 
	createResponseDocumentFromDataResultList(DataResult<List<T>> result, XmlFormatterI<T> formatter) throws ParserConfigurationException {
		Document outputDocument;
		
		if(result.isSuccess()) {
			List<T> entities = result.getData();
			DataResult<Document> resultFromFormatter = formatter.format(entities);
			
			outputDocument = setStatusToDocument(resultFromFormatter);
			return new SuccessDataResult<>(outputDocument);
		} else {
			outputDocument = createSuccessFalseXml();
			return new ErrorDataResult<>(outputDocument);
		}
	}

	private static Document setStatusToDocument(DataResult<Document> resultFromFormatter) throws ParserConfigurationException {
		Document outputDocument;
		if(resultFromFormatter.isSuccess()) {
			outputDocument = resultFromFormatter.getData();
			setSuccessTrue(outputDocument);
		} else {
			outputDocument = createSuccessFalseXml();
		}
		return outputDocument;
	}

	public static Document setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(DataResult<Document> result) throws ParserConfigurationException  {
		Document document;
		
		if(result.isSuccess()) {
			document = result.getData();
			XmlUtils.setSuccessTrue(document);
		} else {
			document = createSuccessFalseXml();
		}
		
		return document;
	}
	
}
