package web.cart;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String customerName = req.getParameter("customerName");
		Cart cart = new Cart(0.0, customerName);
		DataResult<Long> result = cartManager.insertAndReturnGeneratedId(cart);

		resp.setContentType(WebConstants.XML_CONTENT_TYPE);

		if (result.isSuccess()) {
			long generatedId = result.getData();
			cart.setId(generatedId);

			Document document = (Document) cartXmlUtil.format(cart).getData();
			try {
				XmlUtils.dump(document, resp.getOutputStream());
			} catch (IOException | TransformerException e) {
				e.printStackTrace();
			}
		}
	}
}
