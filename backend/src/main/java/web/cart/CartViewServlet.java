package web.cart;

import java.io.IOException;
import java.util.List;

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
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.CartXmlUtil;
import xmlUtils.concretes.ProductXmlUtil;

@WebServlet("/api/cart/view")
public class CartViewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CartManagerI cartManager;
	private final XmlUtilI<Cart> cartXmlUtil;

	public CartViewServlet() {
		this.cartManager = new CartManager();
		this.cartXmlUtil = new CartXmlUtil();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(WebConstants.XML_CONTENT_TYPE);

		long cartId = Long.parseLong(req.getParameter("cartId"));

		DataResult<Cart> result = cartManager.find(cartId);
		Document document;
		if (!result.isSuccess()) {
			try {
				document = XmlUtils.createStatusXml(result, resp.getOutputStream());
			} catch (ParserConfigurationException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else {
			Cart cart = result.getData();
			document = (Document) cartXmlUtil.format(cart).getData();
			XmlUtils.setSuccessTrue(document);
		}

		try {
			XmlUtils.dump(document, resp.getOutputStream());
		} catch (IOException | TransformerException e) {
			e.printStackTrace();
		}
	}
}
