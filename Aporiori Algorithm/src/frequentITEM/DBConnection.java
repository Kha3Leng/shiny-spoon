package frequentITEM;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
Connection conn;
	public DBConnection(){
		
	}
Connection getDBConnection(String dbname){
	String url = "jdbc:mysql://localhost:3306/" + dbname;
	String dbUser = "root";
	String dbPwd = "";
	conn=null;
    try{
    	conn=DriverManager.getConnection(url,dbUser,dbPwd);
    }
    catch(SQLException e){
    	System.out.println("Cannot connect to your database "+dbname +"and "+ e.getMessage());
    }
    return conn;
}
}
