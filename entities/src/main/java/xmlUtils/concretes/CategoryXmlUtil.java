package xmlUtils.concretes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import results.*;
import entities.concretes.*;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import messages.XmlMessages;


public class CategoryXmlUtil implements XmlUtilI<Category> {
	
	@Override
	public DataResult<Document> format(Category category) {
		Document document = null;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CATEGORIES);
			Element categoryElement = document.getDocumentElement();
			
			setFields(category, document, categoryElement);
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}

		return new SuccessDataResult<Document>(document);
	}

	@Override
	public DataResult<Document> format(List<Category> categories) {
		Document document = null;
		Element categoryList;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.CATEGORIES);
			categoryList = document.getDocumentElement();
			
			for(Category category : categories) {
				Element categoryElement = document.createElement(XmlAttributeNames.CATEGORY);
				setFields(category, document, categoryElement);
				categoryList.appendChild(categoryElement);
			}
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}
		
		return new SuccessDataResult<Document>(document);
	}

	@Override
	public Category parse(Document document) {
		Element categoryElement = document.getDocumentElement();
		
		Category category = parseFromXml(categoryElement);

		return category;
	}

	@Override
	public List<Category> parseList(Document document) {
		Element categoryListElement = document.getDocumentElement();
		
		List<Category> categoryList = new ArrayList<Category>(); 
		
		NodeList categoriesElement = categoryListElement.getElementsByTagName(XmlAttributeNames.CATEGORY);
		
		for(int i = 0; i < categoriesElement.getLength(); i++) {
			Element categoryElement = (Element)categoriesElement.item(i);
			Category category = parseFromXml(categoryElement);
			categoryList.add(category);
		}
		
		return categoryList;
	}

	public void setFields(Category category, Document document, Element categoryElement) {
		categoryElement.setAttribute(XmlAttributeNames.ID, String.valueOf(category.getCategoryId()));
		XmlUtils.addSingleElementText(document, categoryElement, XmlAttributeNames.NAME, category.getCategoryName());
	}
	
	public Category parseFromXml(Element categoryElement) {
		long id = XmlUtils.getAttribute(categoryElement, XmlAttributeNames.ID, 0);
		String name = XmlUtils.getSingleElementText(categoryElement, XmlAttributeNames.NAME, "");
		Category category = new Category(id, name);
		return category;
	}
	
	private static class XmlAttributeNames {
		private static final String ID = "id";
		private static final String NAME = "name";
		
		private static final String CATEGORY = "category";
		private static final String CATEGORIES = "categories";
	}
}
