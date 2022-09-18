package xmlUtils.concretes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import messages.XmlMessages;
import results.*;
import entities.concretes.*;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;

public class ProductXmlUtil implements XmlUtilI<Product> {
	
	private final CategoryXmlUtil categoryXmlUtil;
	
	public ProductXmlUtil() {
		this.categoryXmlUtil = new CategoryXmlUtil();
	}
	
	@Override
	public DataResult<Document> format(Product product) {
		Document document = null;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.PRODUCTS);
			Element productElement = document.getDocumentElement();
			
			setFields(product, document, productElement);
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}

		return new SuccessDataResult<Document>(document);
	}

	@Override
	public DataResult<Document> format(List<Product> products) {
		Document document = null;
		Element productList;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.PRODUCTS);
			productList = document.getDocumentElement();
			
			for(Product product : products) {
				Element productElement = document.createElement(XmlAttributeNames.PRODUCT);
				setFields(product, document, productElement);
				productList.appendChild(productElement);
			}
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}
		
		return new SuccessDataResult<Document>(document);
	}

	@Override
	public Product parse(Document document) {
		Element productElement = document.getDocumentElement();
		
		Product product = parseFromXml(productElement);

		return product;
	}

	@Override
	public List<Product> parseList(Document document) {
		Element productListElement = document.getDocumentElement();
		
		List<Product> productList = new ArrayList<Product>(); 
		
		NodeList productsElement = productListElement.getElementsByTagName(XmlAttributeNames.PRODUCT);
		
		for(int i = 0; i < productsElement.getLength(); i++) {
			Element productElement = (Element)productsElement.item(i);
			Product product = parseFromXml(productElement);
			productList.add(product);
		}
		
		return productList;
	}
	
	private void setFields(Product product, Document document, Element productElement) {
		productElement.setAttribute(XmlAttributeNames.ID, String.valueOf(product.getProductId()));
		XmlUtils.addSingleElementText(document, productElement, XmlAttributeNames.NAME, product.getProductName());
		XmlUtils.addSingleElementText(document, productElement, XmlAttributeNames.SALES_PRICE, product.getSalesPrice());
		XmlUtils.addSingleElementText(document, productElement, XmlAttributeNames.IMAGE_PATH, product.getImagePath());
		
		Element categoryElement = document.createElement(XmlAttributeNames.CATEGORY);
		categoryXmlUtil.setFields(product.getCategory(), document, categoryElement);
		productElement.appendChild(categoryElement);
	}
	
	private Product parseFromXml(Element productElement) {
		long id = XmlUtils.getAttribute(productElement, XmlAttributeNames.ID, 0);
		String name = XmlUtils.getSingleElementText(productElement, XmlAttributeNames.NAME, "");
		double salesPrice = XmlUtils.getSingleElementText(productElement, XmlAttributeNames.SALES_PRICE, 0.0);
		String imagePath = XmlUtils.getSingleElementText(productElement, XmlAttributeNames.IMAGE_PATH, "");
		
		Element categoryElement = (Element) productElement.getElementsByTagName(XmlAttributeNames.CATEGORY).item(0);	
		Category category = categoryXmlUtil.parseFromXml(categoryElement);
		
		Product product = new Product(id, name, salesPrice, imagePath, category);
		
		return product;
	}
	
	private static class XmlAttributeNames {
		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String SALES_PRICE = "salesPrice";
		private static final String IMAGE_PATH = "imagePath";
		
		private static final String PRODUCT = "product";
		private static final String PRODUCTS = "products";
		private static final String CATEGORY = "category";
	}
	
}
