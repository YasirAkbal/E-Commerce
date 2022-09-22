package managers.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import entities.concretes.*;
import entities.abstracts.EntityI;
import managers.base.ManagerI;
import messages.DbErrorMessages;
import messages.DbSuccessMessages;
import results.DataResult;
import results.ErrorDataResult;
import results.ErrorResult;
import results.Result;
import results.SuccessDataResult;
import results.SuccessResult;

public abstract class BaseManager<E extends EntityI> {

	private String url;
	private String user;
	private String password;
	private String driver;

	protected Connection connection = null;

	protected abstract DataResult<E> parse(ResultSet resultSet) throws SQLException;

	public BaseManager(String url, String user, String password, String driver) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.driver = driver;

		initDriver();
	}

	private void initDriver() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Result connect() {
		try {
			connection = DriverManager.getConnection(url, user, password);
			
			return new SuccessResult();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ErrorResult(DbErrorMessages.CONNECTION_FAILED);
		}
	}

	public Result disconnect(){
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return new ErrorResult(DbErrorMessages.CONNECTION_FAILED);
			}
		} 
		
		return new SuccessResult();
	}
	
	protected DataResult<List<E>> parseList(ResultSet resultSet) {
		List<E> entityList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				DataResult<E> result = parse(resultSet);
				if (!result.isSuccess())
					return new ErrorDataResult<>(result.getMessage());
				E entity = result.getData();
				entityList.add(entity);
			}
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.RESULT_SET_PARSING_ERROR);
		}

		return new SuccessDataResult<>(entityList);
	}

	protected DataResult<E> find(String sql, long id) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
			
			return find(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}
	}

	protected DataResult<E> find(PreparedStatement statement) {
		E entity = null;
		
		try {
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				DataResult<E> result = parse(resultSet);
				if (!result.isSuccess())
					return new ErrorDataResult<>(result.getMessage());
				entity = result.getData();
				return new SuccessDataResult<>(entity);
			} else {
				return new ErrorDataResult<>(DbErrorMessages.RECORD_NOT_FOUND);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}
	}

	protected DataResult<List<E>> listAll(PreparedStatement statement) {
		List<E> entities = null;
		
		try {
			ResultSet resultSet = statement.executeQuery();
			
			DataResult<List<E>> resultFromParse = parseList(resultSet);
			if (!resultFromParse.isSuccess())
				return resultFromParse;
			
			entities = resultFromParse.getData();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		} 

		return new SuccessDataResult<>(entities);
	}

	protected DataResult<List<E>> listAll(String sql) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			return listAll(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		}
	}

	protected DataResult<List<E>> listAll(String sql, long id) {
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
			
			return listAll(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		}
	}

	protected DataResult<Long> insert(PreparedStatement statement) {
		DataResult<Long> insertStatus = executeUpdateAndCheckStatus(statement);
		if (!insertStatus.isSuccess())
			return insertStatus;
		
		long affectedRows = insertStatus.getData();

		return new SuccessDataResult<>(affectedRows, DbSuccessMessages.INSERTION_SUCCESS);
	}

	protected DataResult<Long> insertAndReturnGeneratedKey(PreparedStatement statement) {
		try {
			DataResult<Long> insertStatus = executeUpdateAndCheckStatus(statement);
			if (!insertStatus.isSuccess())
				return insertStatus;
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return new SuccessDataResult<>(generatedKeys.getLong(1));
				} else {
					return new ErrorDataResult<>("Creating user failed, no ID obtained.");
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}
	}

	protected DataResult<Long> executeUpdateAndCheckStatus(PreparedStatement statement) {
		long affectedRows = -1;
		
		try {
			affectedRows = statement.executeUpdate();
			
			if (affectedRows <= 0) {
				return new ErrorDataResult<>(DbErrorMessages.NO_AFFECTED_ROWS);
			} else {
				return new SuccessDataResult<>(affectedRows, DbSuccessMessages.EXECUTE_UPDATE_SUCCESS);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.EXECUTE_UPDATE_FAILED);
		}
	}

	protected Result delete(PreparedStatement statement) {
		DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(statement);
		if (!deleteStatus.isSuccess())
			return deleteStatus;

		return new SuccessResult(DbSuccessMessages.DELETION_SUCCESS);
	}

	protected Result deleteById(String sql, long id) {	
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
			
			DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(statement);
			if (!deleteStatus.isSuccess())
				return deleteStatus;
			else
				return new SuccessResult(DbSuccessMessages.DELETION_SUCCESS);
		} catch (SQLException e1) {
			e1.printStackTrace();
			return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}
	}

	protected DataResult<E> deleteAndReturn(PreparedStatement findStatement, PreparedStatement deleteStatement) {
		DataResult<E> findResult = find(findStatement);
		if (!findResult.isSuccess())
			return findResult;

		E deletedEntity = findResult.getData();

		DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(deleteStatement);
		if (!deleteStatus.isSuccess())
			return new ErrorDataResult<>(deleteStatus.getMessage());

		return new SuccessDataResult<>(deletedEntity, DbSuccessMessages.DELETION_SUCCESS);
	}

	protected DataResult<PreparedStatement> createStatementAndSetId(String sql, Long id) {	
		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
			
			return new SuccessDataResult<>(statement);
		} catch (SQLException e1) {
			e1.printStackTrace();
			
			return new ErrorDataResult<>(DbErrorMessages.PREPARED_STATEMENT_ERROR);
		}
	}
}