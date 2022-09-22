package managers.concretes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import entities.concretes.*;
import managers.abstracts.CartProductManagerI;
import managers.base.BasePostgreSqlManager;
import messages.DbErrorMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.ErrorResult;
import results.Result;
import results.SuccessDataResult;

class CartProductManager extends BasePostgreSqlManager<CartProduct> implements CartProductManagerI {

	@Override
	public Result insert(CartProduct cartProduct) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return connectionResult;
			
			DataResult<PreparedStatement> result = createInsertStatement(cartProduct);

			if (!result.isSuccess())
				return new ErrorResult(DbErrorMessages.INSERTION_FAILED);

			return super.insert(result.getData());
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<Long> insertAndReturnGeneratedId(CartProduct cartProduct) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			DataResult<PreparedStatement> result = createInsertStatement(cartProduct);

			if (!result.isSuccess())
				return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);

			return super.insertAndReturnGeneratedKey(result.getData());	
		} finally {
			disconnect();
		}
	}

	@Override
	public Result update(CartProduct entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(Long id) {
		Result connectionResult = connect();
		if(!connectionResult.isSuccess())
			return connectionResult;
		
		try {
			PreparedStatement statement = connection.prepareStatement(SqlStrings.DELETE_STRING);
			statement.setLong(1, id);
			
			return super.delete(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.DELETION_FAILED);
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<CartProduct> find(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			DataResult<PreparedStatement> result = createStatementAndSetId(SqlStrings.SELECT_BY_ID_STRING, id);

			if (!result.isSuccess())
				return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);

			return super.find(result.getData());
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<List<CartProduct>> listAllByCartId(long cartId) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			DataResult<PreparedStatement> result = createStatementAndSetId(SqlStrings.SELECT_BY_CART_ID_STRING, cartId);

			if (!result.isSuccess())
				return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);

			return super.listAll(result.getData());
		} finally {
			disconnect();
		}
	}

	@Override
	protected DataResult<CartProduct> parse(ResultSet resultSet) throws SQLException {
		CartProduct cartProduct;

		long id = resultSet.getLong(TableColumnNames.ID);
		long cartId = resultSet.getLong(TableColumnNames.CART_ID);
		long productId = resultSet.getLong(TableColumnNames.PRODUCT_ID);
		int salesQuantity = Integer.parseInt(resultSet.getString(TableColumnNames.SALES_QUANTITY));
		double salesPrice = Double.parseDouble(resultSet.getString(TableColumnNames.SALES_PRICE));
		double taxRate = Double.parseDouble(resultSet.getString(TableColumnNames.TAX_RATE));
		double lineAmount = Double.parseDouble(resultSet.getString(TableColumnNames.LINE_AMOUNT));

		cartProduct = new CartProduct(id, cartId, productId, salesQuantity, salesPrice, taxRate, lineAmount);

		return new SuccessDataResult<>(cartProduct);
	}

	private DataResult<PreparedStatement> createInsertStatement(CartProduct cartProduct) {
		try {
			PreparedStatement statement = connection.prepareStatement(SqlStrings.INSERT_STRING, Statement.RETURN_GENERATED_KEYS);
			
			statement.setLong(1, cartProduct.getCartId());
			statement.setLong(2, cartProduct.getProductId());
			statement.setInt(3, cartProduct.getSalesQuantity());
			statement.setDouble(4, cartProduct.getSalesPrice());
			statement.setDouble(5, cartProduct.getTaxRate());
			statement.setDouble(6, cartProduct.getLineAmount());
			
			return new SuccessDataResult<>(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}		
	}

	@Override
	public DataResult<CartProduct> deleteAndReturn(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			DataResult<PreparedStatement> resultForFindByIdStatement = createStatementAndSetId(SqlStrings.SELECT_BY_ID_STRING, id);
			if (!resultForFindByIdStatement.isSuccess())
				return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
			PreparedStatement findStatement = resultForFindByIdStatement.getData();
			
			DataResult<PreparedStatement> resultForCreateDeleteStatement = createStatementAndSetId(SqlStrings.DELETE_STRING, id);
			if (!resultForCreateDeleteStatement.isSuccess())
				return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
			PreparedStatement deleteStatement = resultForCreateDeleteStatement.getData();

			return super.deleteAndReturn(findStatement, deleteStatement);
		} finally {
			disconnect();
		}
	}

	private static class SqlStrings {
		private static final String SELECT_BY_ID_STRING = "select * from cart_products where id = ?";
		private static final String SELECT_BY_PRODUCT_ID_STRING = "select * from cart_products where product_id = ?";
		private static final String SELECT_BY_CART_ID_STRING = "select * from cart_products where cart_id = ?";
		private static final String INSERT_STRING = "insert into cart_products(cart_id,product_id,sales_quantity,sales_price,tax_rate,line_amount)"
				+ " values(?,?,?,?,?,?)";
		private static final String DELETE_STRING = "delete from cart_products where id = ?";
	}

	private static class TableColumnNames {
		private static final String ID = "id";
		private static final String CART_ID = "cart_id";
		private static final String PRODUCT_ID = "product_id";
		private static final String SALES_QUANTITY = "sales_quantity";
		private static final String SALES_PRICE = "sales_price";
		private static final String TAX_RATE = "tax_rate";
		private static final String LINE_AMOUNT = "line_amount";
	}

}
