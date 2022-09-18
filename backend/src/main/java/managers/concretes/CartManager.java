package managers.concretes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import entities.concretes.*;
import managers.abstracts.CartManagerI;
import managers.abstracts.CartProductManagerI;
import managers.base.BasePostgreSqlManager;
import messages.DbErrorMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.ErrorResult;
import results.Result;
import results.SuccessDataResult;

public class CartManager extends BasePostgreSqlManager<Cart> implements CartManagerI {	
	
	private final CartProductManagerI cartProductManager;

	public CartManager() {
		super();
		this.cartProductManager = new CartProductManager();
	}

	@Override
	public Result insert(Cart cart) {
		PreparedStatement statement = null;
		
		DataResult<PreparedStatement> result = createInsertStatement(cart);
		if(!result.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		
		statement = result.getData();
		
		return super.insert(statement);
	}

	@Override
	public DataResult<Long> insertAndReturnGeneratedId(Cart cart) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		PreparedStatement statement = null;
		
		DataResult<PreparedStatement> result = createInsertStatement(cart);
		if(!result.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		
		statement = result.getData();
		
		return super.insertAndReturnGeneratedKey(statement);
	}

	@Override
	public Result update(Cart entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(Long id) {
		return super.deleteById(SqlStrings.DELETE_STRING, id);
	}

	@Override
	public DataResult<Cart> find(Long id) {
		DataResult<Cart> cartResult = findOnlyCart(id);
		if(!cartResult.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		
		DataResult<List<CartProduct>> cartProductResult = cartProductManager.listAllByCartId(id);
		if(!cartProductResult.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		
		Cart cart = cartResult.getData();
		List<CartProduct> cartProducts = cartProductResult.getData();
		cart.setCartProducts(cartProducts);
		
		return new SuccessDataResult<>(cart);
	}
	
	private DataResult<Cart> findOnlyCart(long id) {
		DataResult<PreparedStatement> result = createFindByIdStatement(SqlStrings.SELECT_BY_ID_STRING,id);
		
		if(!result.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		
		return super.find(result.getData());
	}

	@Override
	protected DataResult<Cart> parse(ResultSet resultSet) throws SQLException {
		Cart cart;
		
		long id = resultSet.getLong(TableColumnNames.ID);
		double totalAmount = resultSet.getDouble(TableColumnNames.TOTAL_AMOUNT);
		String customerName = resultSet.getString(TableColumnNames.CUSTOMER_NAME);

		DataResult<List<CartProduct>> resultFromCardProductManager = cartProductManager.listAllByCartId(id);
		if(!resultFromCardProductManager.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.RESULT_SET_PARSING_ERROR);
		
		cart = new Cart(id, totalAmount, customerName, resultFromCardProductManager.getData());
		
		return new SuccessDataResult<>(cart);
	}

	private DataResult<PreparedStatement> createInsertStatement(Cart cart)  {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(SqlStrings.INSERT_STRING, Statement.RETURN_GENERATED_KEYS);
			statement.setDouble(1, cart.getTotalAmount());
			statement.setString(2, cart.getCustomerName());
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.INSERTION_FAILED, e).toString());
		}

		return new SuccessDataResult<>(statement);
	}
	
	@Override
	public DataResult<Long> addCardProductToCart(CartProduct cardProduct) {	//transaction yapılmalı
		DataResult<Long> addCardProcutResult = cartProductManager.insertAndReturnGeneratedId(cardProduct);
		if(!addCardProcutResult.isSuccess())
			return addCardProcutResult;
		
		DataResult<PreparedStatement> createUpdateTotalAmountStatementResult = 
				createUpdateTotalAmountStatement(SqlStrings.UPDATE_TOTAL_AMOUNT, cardProduct.getLineAmount(), cardProduct.getCartId());
		if(!createUpdateTotalAmountStatementResult.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.UPDATE_FAILED);
		
		DataResult<Long> executeUpdataResult = super.executeUpdateAndCheckStatus(createUpdateTotalAmountStatementResult.getData());
		
		if(executeUpdataResult.isSuccess()) {
			return addCardProcutResult;
		} else {
			return new ErrorDataResult<>(DbErrorMessages.UPDATE_FAILED);
		}
	}

	@Override
	public DataResult<CartProduct> deleteCardProductFromCart(long cardProductId) {
		DataResult<CartProduct> result = cartProductManager.deleteAndReturn(cardProductId);
		
		if(!result.isSuccess())
			return new ErrorDataResult<>(result.getMessage());
		
		return new SuccessDataResult<>(result.getData());
	}
	
	@Override
	public DataResult<List<Cart>> listAll() {
		return super.listAll(SqlStrings.SELECT_ALL_STRING);
	}
	
	private DataResult<PreparedStatement> createUpdateTotalAmountStatement(String sql, double amount, long id) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.CONNECTION_FAILED);
		}
		
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setDouble(1, amount);
			statement.setLong(2, id);
			return new SuccessDataResult<>(statement);
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		}
	}
	
	
	private static class SqlStrings {
		private static final String INSERT_STRING = "insert into carts(total_amount,customer_name) values(?,?)";
		private static final String DELETE_STRING = "delete from carts where id = ?";
		private static final String SELECT_ALL_STRING = "select * from carts";
		private static final String SELECT_BY_ID_STRING = "select * from carts where id = ?";
		private static final String UPDATE_TOTAL_AMOUNT = "update carts set total_amount = total_amount+? where id = ?";
	}
	
	private static class TableColumnNames {
		private static final String ID = "id";
		private static final String TOTAL_AMOUNT = "total_amount";
		private static final String CUSTOMER_NAME = "customer_name";;
	}
}
