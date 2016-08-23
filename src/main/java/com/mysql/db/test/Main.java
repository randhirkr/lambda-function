package com.mysql.db.test;
 
public class Main {
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    //public static final String MYSQL_URL = "jdbc:mysql://localhost/<dbname>?"
                                            //+ "user=<username>&password=<password>";
    
    public static final String MYSQL_URL = "jdbc:mysql://<db url>/<dbname>?"
            + "user=<username>&password=<password>";
 
    public static void main(String[] args) throws Exception {
        MySQLJava dao = new MySQLJava(MYSQL_DRIVER,MYSQL_URL);
        dao.readData();
    }
}