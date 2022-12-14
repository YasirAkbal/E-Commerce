package managers.concretes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import entities.concretes.*;
import managers.abstracts.ProductManagerI;
import managers.base.BasePostgreSqlManager;
import messages.DbErrorMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.Result;
import results.SuccessDataResult;

public class ProductManager extends BasePostgreSqlManager<Product> implements ProductManagerI {

	@Override
	protected DataResult<Product> parse(ResultSet resultSet) throws SQLException {
		Product product;

		long productId = resultSet.getLong(TableColumnNames.ID);
		String productName = resultSet.getString(TableColumnNames.NAME);
		double salesPrice = resultSet.getDouble(TableColumnNames.SALES_PRICE);
		String imagePath = resultSet.getString(TableColumnNames.IMAGE_PATH);

		Category category = new Category(resultSet.getLong(TableColumnNames.CATEGORY_ID),
				resultSet.getString(TableColumnNames.CATEGORY_NAME));
		product = new Product(productId, productName, salesPrice, imagePath, category);
		
		return new SuccessDataResult<>(product);
	}

	@Override
	public Result insert(Product product) {
		Result connectionResult = connect();
		if(!connectionResult.isSuccess())
			return connectionResult;
		
		try {
			PreparedStatement statement = connection.prepareStatement(SqlStrings.INSERT_STRING);
			
			statement.setLong(1, product.getProductId());
			statement.setString(2, product.getProductName());
			statement.setDouble(3, product.getSalesPrice());
			statement.setString(4, product.getImagePath());
			statement.setLong(5, product.getCategory().getCategoryId());
			
			return super.insert(statement);
		} catch (SQLException e) {
			disconnect();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		} finally {
			disconnect();
		}
	}

	@Override
	public Result update(Product entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return connectionResult;
			
			return super.deleteById(SqlStrings.DELETE_BY_ID, id);
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<Product> find(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			return find(SqlStrings.SELECT_PRODUCTS_BY_ID_WITH_CATEGORY_JOIN, id);
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<List<Product>> findByCategory(long categoryId) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			return listAll(SqlStrings.SELECT_PRODUCTS_BY_CATEGORY_ID_WITH_CATEGORY_JOIN, categoryId);
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<List<Product>> listAll() {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			return super.listAll(SqlStrings.SELECT_PRODUCTS_WITH_CATEGORY_JOIN);
		} finally {
			disconnect();
		}
	}

	private static class SqlStrings {
		private static final String SELECT_PRODUCTS_BY_ID_WITH_CATEGORY_JOIN = "select p.id, p.name, p.sales_price, p.image_path, c.id as category_id, c.name as category_name \r\n"
				+ " from products p \r\n" + " left join categories c on p.category_id = c.id \r\n" + " where p.id = ?";
		private static final String SELECT_PRODUCTS_BY_CATEGORY_ID_WITH_CATEGORY_JOIN = "select p.id, p.name, p.sales_price, p.image_path, c.id as category_id, c.name as category_name \r\n"
				+ " from products p \r\n" + " left join categories c on p.category_id = c.id \r\n"
				+ " where p.category_id = ?";
		private static final String SELECT_PRODUCTS_WITH_CATEGORY_JOIN = "select p.id, p.name, p.sales_price, p.image_path, c.id as category_id, c.name as category_name \r\n"
				+ " from products p \r\n" + " left join categories c on p.category_id = c.id \r\n";
		private static final String DELETE_BY_ID = "delete from products where id = ?";
		private static final String INSERT_STRING = "insert into products(id,name,sales_price,image_path,category_id) values(?,?,?,?,?)";
	}

	private static class TableColumnNames {
		private static final String ID = "id";
		private static final String NAME = "name";
		private static final String SALES_PRICE = "sales_price";
		private static final String IMAGE_PATH = "image_path";

		private static final String CATEGORY_ID = "category_id";
		private static final String CATEGORY_NAME = "category_name";
	}
}
