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

		Document document;
		try {
			document = XmlUtils.parse(req.getInputStream());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		User user = userXmlUtil.parse(document);

		Result result = userManager.checkCredentials(user.getUsername(), user.getPassword());

		try {
			Document outputDocument = XmlUtils.createStatusXml(result, resp.getOutputStream());
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (ParserConfigurationException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
