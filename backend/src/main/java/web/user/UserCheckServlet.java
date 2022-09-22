package web.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.UserManagerI;
import managers.concretes.UserManager;
import results.Result;
import utils.HashUtil;
import utils.StreamUtils;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.UserXmlUtil;

@WebServlet("/api/user/check")
public class UserCheckServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserManagerI userManager;
	private final XmlUtilI<User> userXmlUtil;

	public UserCheckServlet() {
		this.userManager = new UserManager();
		this.userXmlUtil = new UserXmlUtil();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);

		Document inputDocument, outputDocument;
		try {
			inputDocument = XmlUtils.parse(req.getInputStream());
			User user = userXmlUtil.parse(inputDocument);
			
			final String passwordHash = HashUtil.toSha3_256(user.getPassword());
			Result resultFromCheckCredentials = userManager.checkCredentials(user.getUsername(), passwordHash);
			
			outputDocument = XmlUtils.createStatusXml(resultFromCheckCredentials, resp.getOutputStream());
			
			StreamUtils.setResponseStatus(resultFromCheckCredentials, resp, HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST);
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
