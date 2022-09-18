package xmlUtils.concretes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.concretes.*;
import messages.XmlMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.SuccessDataResult;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;

public class UserXmlUtil implements XmlUtilI<User> {
	
	@Override
	public DataResult<Document> format(User user) {
		Document document = null;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.USER);
			Element userElement = document.getDocumentElement();
			
			setFields(user, document, userElement);
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}

		return new SuccessDataResult<Document>(document);
	}

	@Override
	public DataResult<Document> format(List<User> users) {
		Document document = null;
		Element userList;
		
		try {
			document = XmlUtils.create(XmlAttributeNames.USERS);
			userList = document.getDocumentElement();
			
			for(User user : users) {
				Element userElement = document.createElement(XmlAttributeNames.USER);
				setFields(user, document, userElement);
				userList.appendChild(userElement);
			}
		} catch (ParserConfigurationException e) {
			return new ErrorDataResult<Document>(XmlMessages.PARSING_ERROR);
		}
		
		return new SuccessDataResult<Document>(document);
	}

	@Override
	public User parse(Document document) {
		Element userElement = document.getDocumentElement();
		
		User user = parseFromXml(userElement);

		return user;
	}

	@Override
	public List<User> parseList(Document document) {
		Element userListElement = document.getDocumentElement();
		
		List<User> userList = new ArrayList<User>(); 
		
		NodeList usersElement = userListElement.getElementsByTagName(XmlAttributeNames.USER);
		
		for(int i = 0; i < usersElement.getLength(); i++) {
			Element userElement = (Element)usersElement.item(i);
			User user = parseFromXml(userElement);
			userList.add(user);
		}
		
		return userList;
	}
	
	public void setFields(User user, Document document, Element userElement) {
		userElement.setAttribute(XmlAttributeNames.ID, String.valueOf(user.getId()));
		XmlUtils.addSingleElementText(document, userElement, XmlAttributeNames.USERNAME, user.getUsername());
		XmlUtils.addSingleElementText(document, userElement, XmlAttributeNames.PASSWORD, user.getPassword());
	}
		
	private User parseFromXml(Element userElement) {
		long id = XmlUtils.getAttribute(userElement, XmlAttributeNames.ID, 0);
		String username = XmlUtils.getSingleElementText(userElement, XmlAttributeNames.USERNAME, "");
		String password = XmlUtils.getSingleElementText(userElement, XmlAttributeNames.PASSWORD, "");
		
		User user = new User(id, username, password);
		
		return user;
	}
	
	private static class XmlAttributeNames {
		private static final String ID = "id";
		private static final String USERNAME = "username";
		private static final String PASSWORD = "password";
		
		private static final String USER = "user";
		private static final String USERS = "users";
	}
	
}
