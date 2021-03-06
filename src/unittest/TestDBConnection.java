package edu.cmu.gizmo.unittest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;
import edu.cmu.gizmo.management.util.DBConnection;

public class TestDBConnection extends TestCase{
    Connection conn = null;
    PreparedStatement ptmt = null;
    ResultSet resultSet = null;
    
    public void testShouldGetConnection() {
		conn = DBConnection.getConnection("tasksdb");
		assertNotNull(conn);
		DBConnection.closeConnection();
	}
    
}
