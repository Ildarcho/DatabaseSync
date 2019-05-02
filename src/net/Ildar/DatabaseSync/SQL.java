package net.Ildar.DatabaseSync;

import java.sql.*;


/**
 * Responsible for all actions on SQL database
 */
public class SQL implements AutoCloseable {

    private Connection connection;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement insertStmt;

    private ResultSet resultSet;

    @Override
    public void close() throws SQLCustomException {
        try {
            if (connection != null)
                connection.close();
            if (updateStmt != null)
                updateStmt.close();
            if (deleteStmt != null)
                deleteStmt.close();
            if (insertStmt != null)
                insertStmt.close();
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * Connects to the database with given parameters
     *
     * @param DBURL  SQL database URL
     * @param DBUSER SQL user name
     * @param DBPASS SQL user password
     * @throws SQLCustomException when any error occured
     */
    public void connect(String DBURL, String DBUSER, String DBPASS) throws SQLCustomException {
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLCustomException("Error on registration of JDBC driver!", e);
            }
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            connection.setAutoCommit(false);
            updateStmt = connection.prepareStatement("UPDATE jobs SET Description = ? WHERE DepCode=? and DepJob=?");
            deleteStmt = connection.prepareStatement("DELETE FROM jobs WHERE DepCode=? and DepJob=?");
            insertStmt = connection.prepareStatement("INSERT INTO jobs (DepCode, DepJob, Description) VALUES(?, ?, ?)");
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }

    }

    /**
     * Updates description of the job
     *
     * @param job Job object
     * @throws SQLCustomException when any error occured
     */
    public void update(Job job) throws SQLCustomException {
        try {
            if (updateStmt == null)
                throw new SQLCustomException("to update connect to database first");
            if (job == null)
                throw new SQLCustomException("cannot update null object");
            updateStmt.setString(1, job.getDescription());
            updateStmt.setString(2, job.getDepCode());
            updateStmt.setString(3, job.getDepJob());
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * Inserts new job into the database
     *
     * @param job Job object
     * @throws SQLCustomException when any error occured
     */
    public void insert(Job job) throws SQLCustomException {
        try {
            if (insertStmt == null) {
                throw new SQLCustomException("to insert connect to database first");
            }
            if (job == null)
                throw new SQLCustomException("cannot insert null object");
            insertStmt.setString(1, job.getDepCode());
            insertStmt.setString(2, job.getDepJob());
            insertStmt.setString(3, job.getDescription());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * Deletes the job from the database
     *
     * @param job Job object
     * @throws SQLCustomException when any error occured
     */
    public void delete(Job job) throws SQLCustomException {
        try {
            if (deleteStmt == null) {
                throw new SQLCustomException("to delete connect to database first");
            }
            if (job == null)
                throw new SQLCustomException("cannot delete null object");
            deleteStmt.setString(1, job.getDepCode());
            deleteStmt.setString(2, job.getDepJob());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * Selects all jobs from database. Get them one by one using next() function
     *
     * @throws SQLCustomException when any error occured
     * @see SQL#next()
     */
    public void select() throws SQLCustomException {
        try {
            if (connection == null) {
                throw new SQLCustomException("to select connect to database first");
            }
            String query = "SELECT * FROM jobs";
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * gets next element from the database. You must call select() before any calls to this function
     *
     * @return Job
     * @throws SQLCustomException when any error occured
     * @see SQL#select()
     */
    public Job next() throws SQLCustomException {
        try {
            if (resultSet == null) {
                throw new SQLCustomException("data was not selected");
            }
            if (!resultSet.next())
                return null;
            String depCode = resultSet.getString("DepCode").trim();
            String depJob = resultSet.getString("DepJob").trim();
            String description = resultSet.getString("Description").trim();
            return new Job(depCode, depJob, description);
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }

    /**
     * Saves all changes to the database
     *
     * @throws SQLCustomException when any error occured
     */
    public void save() throws SQLCustomException {
        try {
            if (connection == null)
                throw new SQLCustomException("to save changes connect to database first");
            connection.commit();
        } catch (SQLException e) {
            throw new SQLCustomException(e);
        }
    }
}
