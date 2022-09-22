package web.cart;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.CartManagerI;
import managers.concretes.CartManager;
import results.DataResult;
import results.Result;
import utils.StreamUtils;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.CartXmlUtil;

@WebServlet("/api/cart/create")
public class CartCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CartManagerI cartManager;
	private final XmlUtilI<Cart> cartXmlUtil;

	public CartCreateServlet() {
		super();
		this.cartManager = new CartManager();
		this.cartXmlUtil = new CartXmlUtil();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);
		
		String customerName = req.getParameter("customerName");
		Cart cart = new Cart(0.0, customerName);
		DataResult<Long> resultFromManager = cartManager.insertAndReturnGeneratedId(cart);
		
		Document outputDocument;
		try {
			if (resultFromManager.isSuccess()) {
				long generatedId = resultFromManager.getData();
				cart.setId(generatedId);
				DataResult<Document> resultFromFormatter = cartXmlUtil.format(cart);
				
				outputDocument = XmlUtils.setSuccessTrueIfSuccessfulOtherwiseCreateSuccessFalseDocument(resultFromFormatter);
				
				StreamUtils.setResponseStatus(resultFromManager, resp, HttpServletResponse.SC_CREATED, HttpServletResponse.SC_BAD_REQUEST);
			} else {
				outputDocument = XmlUtils.createSuccessFalseXml();
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}		
			
			XmlUtils.dump(outputDocument, resp.getOutputStream());
		} catch (ParserConfigurationException | IOException | TransformerException e1) {
			e1.printStackTrace();
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
}
