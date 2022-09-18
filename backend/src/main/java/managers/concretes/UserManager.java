package managers.concretes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import entities.concretes.*;
import managers.abstracts.UserManagerI;
import managers.base.BasePostgreSqlManager;
import messages.DbErrorMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.ErrorResult;
import results.Result;
import results.SuccessDataResult;

public class UserManager extends BasePostgreSqlManager<User> implements UserManagerI {

	@Override
	public Result insert(User user) {
		DataResult<PreparedStatement> result = createInsertStatement(user);
		if (!result.isSuccess())
			return new ErrorResult(DbErrorMessages.INSERTION_FAILED);

		return super.insert(result.getData());
	}

	@Override
	public Result update(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(Long id) {
		return super.deleteById(SqlStrings.DELETE_BY_ID_STRING, id);
	}

	@Override
	public DataResult<User> find(Long id) {
		return super.find(SqlStrings.SELECT_BY_ID_STRING, id);
	}

	@Override
	public DataResult<Long> insertAndReturnGeneratedId(User user) {
		DataResult<PreparedStatement> result = createInsertStatement(user);

		if (!result.isSuccess())
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);

		return super.insertAndReturnGeneratedKey(result.getData());
	}

	@Override
	public DataResult<User> checkCredentials(String username, String password) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}

		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(SqlStrings.CHECK_USERNAME_PASSWORD_STRING);
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				DataResult<User> parseResult = parse(resultSet);
				if (!parseResult.isSuccess())
					return parseResult;
				User user = parseResult.getData();
				return new SuccessDataResult<>(user);
			} else {
				return new ErrorDataResult<>("Username or password is invalid");
			}
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		}
	}

	@Override
	protected DataResult<User> parse(ResultSet resultSet) {
		User user;

		long id;
		String username, password;
		try {
			id = resultSet.getLong(TableColumnNames.ID);
			username = resultSet.getString(TableColumnNames.USERNAME);
			password = resultSet.getString(TableColumnNames.PASSWORD);
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.RESULT_SET_PARSING_ERROR);
		}

		user = new User(id, username, password);

		return new SuccessDataResult<>(user);
	}

	private DataResult<PreparedStatement> createInsertStatement(User user) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}

		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(SqlStrings.INSERT_STRING, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getPassword());
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.INSERTION_FAILED, e).toString());
		}

		return new SuccessDataResult<>(statement);
	}

	@Override
	public DataResult<List<User>> listAll() {
		return super.listAll(SqlStrings.SELECT_ALL);
	}

	private static class SqlStrings {
		private static final String SELECT_ALL = "select * from users";
		private static final String SELECT_BY_ID_STRING = "select * from users where id = ?";
		private static final String SELECT_BY_USERNAME_STRING = "select * from users where id = ?";
		private static final String SELECT_STRING = "select * from users";
		private static final String CHECK_USERNAME_PASSWORD_STRING = "select * from users where username = ? and password = ?";
		private static final String INSERT_STRING = "insert into users(username, password) values(?,?)";
		private static final String DELETE_BY_ID_STRING = "delete from users where id = ?";
	}

	private static class TableColumnNames {
		private static final String ID = "id";
		private static final String USERNAME = "username";
		private static final String PASSWORD = "password";
	}
}
