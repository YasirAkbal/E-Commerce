package web.product;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import constants.WebConstants;
import entities.concretes.*;
import managers.abstracts.ProductManagerI;
import managers.concretes.ProductManager;
import utils.XmlUtils;
import xmlUtils.abstracts.XmlUtilI;
import xmlUtils.concretes.ProductXmlUtil;

@WebServlet("/api/product")
public class ProductFindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ProductManagerI productManager;
	private final XmlUtilI<Product> productXmlUtil;

	public ProductFindServlet() {
		super();
		this.productManager = new ProductManager();
		this.productXmlUtil = new ProductXmlUtil();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			long productId = Long.parseLong(request.getParameter("productId"));

			Product product = productManager.find(productId).getData();

			Document document = (Document) productXmlUtil.format(product).getData();

			XmlUtils.setSuccessTrue(document);

			XmlUtils.dump(document, response.getOutputStream());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
