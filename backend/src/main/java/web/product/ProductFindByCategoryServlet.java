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

//http://localhost:8080/backend/api/products?categoryId=1
@WebServlet("/api/products")
public class ProductFindByCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final ProductManagerI productManager;
	private final XmlUtilI<Product> productXmlUtil;
	
	public ProductFindByCategoryServlet() {
		super();
		this.productManager = new ProductManager();
		this.productXmlUtil = new ProductXmlUtil();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			long categoryId = Long.parseLong(request.getParameter("categoryId"));
			
			List<Product> products = productManager.findByCategory(categoryId).getData();
			
			Document document = (Document) productXmlUtil.format(products).getData();
			
			response.setContentType(WebConstants.XML_CONTENT_TYPE);
			
			XmlUtils.dump(document, response.getOutputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}
