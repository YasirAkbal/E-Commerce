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
	
	public void connect() throws SQLException {
		connection = DriverManager.getConnection(url, user, password);
	}
	
	public void disconnect() throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}


	protected DataResult<List<E>> parseList(ResultSet resultSet)  {
		List<E> entityList = new ArrayList<>();
		try {
			while(resultSet.next()) {
				DataResult<E> result = parse(resultSet);
				if(!result.isSuccess())
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
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}

		try {
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
			return find(statement);
		} catch(SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.INSERTION_FAILED, e).toString());
		}
	}
	
	protected DataResult<E> find(PreparedStatement statement) {
		E entity = null;

		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}

		try {
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				DataResult<E> result = parse(resultSet);
				if(!result.isSuccess())
					return new ErrorDataResult<>(result.getMessage());
				entity = result.getData();
				disconnect();
				return new SuccessDataResult<>(entity);
			} else {
				disconnect();
				return new ErrorDataResult<>(DbErrorMessages.RECORD_NOT_FOUND);
			}	
		} catch(SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.INSERTION_FAILED, e).toString());
		}
	}
	
	protected DataResult<List<E>> listAll(PreparedStatement statement) {
		List<E> entities = null;
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}

		try {
			ResultSet resultSet = statement.executeQuery();
			DataResult<List<E>> resultFromParse = parseList(resultSet);
			if(!resultFromParse.isSuccess())
				return resultFromParse;
			entities = resultFromParse.getData();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.SELECT_FAILED, e).toString());
		} finally {
			try {
				disconnect();
			} catch (SQLException e) {
				return new ErrorDataResult<>(new SQLException(DbErrorMessages.DISCONNECT_ERROR, e).toString());
			}
		}
		
		return new SuccessDataResult<>(entities);
	}
	
	protected DataResult<List<E>> listAll(String sql) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.SELECT_FAILED, e).toString());
		}
		return listAll(statement);
	}
	
	protected DataResult<List<E>> listAll(String sql, long id) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.CONNECTION_FAILED);
		}
		
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.SELECT_FAILED);
		}
		return listAll(statement);
	}
	
	protected DataResult<Long> insert(PreparedStatement statement) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(DbErrorMessages.CONNECTION_FAILED);
		}
		
		DataResult<Long> insertStatus = executeUpdateAndCheckStatus(statement);
		if(!insertStatus.isSuccess())
			return insertStatus;
		long affectedRows = insertStatus.getData();
		
		return new SuccessDataResult<>(affectedRows, DbSuccessMessages.INSERTION_SUCCESS);
	}
	
	protected DataResult<Long> insertAndReturnGeneratedKey(PreparedStatement statement) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		DataResult<Long> insertStatus = executeUpdateAndCheckStatus(statement);
		if(!insertStatus.isSuccess())
			return insertStatus;
		
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return new SuccessDataResult<>(generatedKeys.getLong(1));
            }
            else {
            	return new ErrorDataResult<>("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
        	new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);
		}
        
		return new ErrorDataResult<>(DbErrorMessages.INSERTION_FAILED);	
	}
	
	protected DataResult<Long> executeUpdateAndCheckStatus(PreparedStatement statement) {
		long affectedRows = -1;
		
		try {
			affectedRows = statement.executeUpdate();
		} catch (SQLException e) {
			new ErrorDataResult<>(DbErrorMessages.EXECUTE_UPDATE_FAILED);
		}
		
		if (affectedRows <= 0) {
			return new ErrorDataResult<>(DbErrorMessages.NO_AFFECTED_ROWS);
        } else {
        	return new SuccessDataResult<>(affectedRows, DbSuccessMessages.EXECUTE_UPDATE_SUCCESS);
        }
	}
	
	protected Result delete(PreparedStatement statement) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(statement);
		if(!deleteStatus.isSuccess())
			return deleteStatus;
		
		return new SuccessResult(DbSuccessMessages.DELETION_SUCCESS);
	}
	
	protected Result deleteById(String sql, long id) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.INSERTION_FAILED, e).toString());
		}
		
		DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(statement);
		if(!deleteStatus.isSuccess())
			return deleteStatus;
		
		return new SuccessResult(DbSuccessMessages.DELETION_SUCCESS);
	}
	
	protected DataResult<E> deleteAndReturn(PreparedStatement findStatement, PreparedStatement deleteStatement) {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		DataResult<E> findResult = find(findStatement);
		if(!findResult.isSuccess())
			return findResult;
		
		E deletedEntity = findResult.getData();
		
		DataResult<Long> deleteStatus = executeUpdateAndCheckStatus(deleteStatement);
		if(!deleteStatus.isSuccess())
			return new ErrorDataResult<>(deleteStatus.getMessage());
		

		return new SuccessDataResult<>(deletedEntity, DbSuccessMessages.DELETION_SUCCESS);
	}
	
	protected DataResult<PreparedStatement> createFindByIdStatement(String sql, Long id)  {
		try {
			connect();
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.CONNECTION_FAILED, e).toString());
		}
		
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(sql);
			statement.setLong(1, id);
		} catch (SQLException e) {
			return new ErrorDataResult<>(new SQLException(DbErrorMessages.SELECT_FAILED, e).toString());
		}

		return new SuccessDataResult<>(statement);
	}
}