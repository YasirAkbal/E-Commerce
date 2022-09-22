package managers.concretes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entities.concretes.*;
import managers.abstracts.CategoryManagerI;
import managers.base.BasePostgreSqlManager;
import messages.DbErrorMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.Result;
import results.SuccessDataResult;

public class CategoryManager extends BasePostgreSqlManager<Category> implements CategoryManagerI {

	@Override
	protected DataResult<Category> parse(ResultSet resultSet) throws SQLException {
		Category category;

		long categoryId = resultSet.getLong(TableColumnNames.ID);
		String categoryName = resultSet.getString(TableColumnNames.NAME);

		category = new Category(categoryId, categoryName);

		return new SuccessDataResult<>(category);
	}

	@Override
	public Result insert(Category category) {
		Result connectionResult = connect();
		if(!connectionResult.isSuccess())
			return connectionResult;
		
		try {	
			PreparedStatement statement = connection.prepareStatement(SqlStrings.INSERT_STRING);
			statement.setString(1, category.getCategoryName());
			
			return super.insert(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		} finally {
			disconnect();
		}
	}

	@Override
	public Result update(Category entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return connectionResult;
			
			return super.deleteById(SqlStrings.DELETE_BY_ID_STRING, id);
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<Category> find(Long id) {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			return super.find(SqlStrings.SELECT_BY_ID_STRING, id);	
		} finally {
			disconnect();
		}
	}

	@Override
	public DataResult<List<Category>> listAll() {
		try {
			Result connectionResult = connect();
			if(!connectionResult.isSuccess())
				return new ErrorDataResult<>(connectionResult.getMessage());
			
			return super.listAll(SqlStrings.SELECT_STRING);
		} finally {
			disconnect();
		}
	}

	private static class SqlStrings {
		private static final String SELECT_BY_ID_STRING = "select * from categories where id = ?";
		private static final String SELECT_STRING = "select * from categories";
		private static final String DELETE_BY_ID_STRING = "delete from categories where id = ?";
		private static final String INSERT_STRING = "insert into categories(name) values(?)";
	}

	private static class TableColumnNames {
		private static final String ID = "id";
		private static final String NAME = "name";
	}
}
