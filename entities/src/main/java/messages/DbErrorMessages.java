package messages;

public final class DbErrorMessages {
	private DbErrorMessages() {}
	
	public static String INSERTION_FAILED = "Insertion failed";
	public static String UPDATE_FAILED = "Update failed";
	public static String DELETION_FAILED = "Deletion failed";
	public static String RECORD_NOT_FOUND = "Record not found";
	public static String CONNECTION_FAILED = "Connection failed";
	public static String SELECT_FAILED = "Error when getting data";
	public static String DISCONNECT_ERROR = "Error when disconnecting DB";
	public static String EXECUTE_UPDATE_FAILED = "Execute Update failed";
	public static String NO_AFFECTED_ROWS = "No affected rows";
	public static String RESULT_SET_PARSING_ERROR = "Error when parsing result set";
}
