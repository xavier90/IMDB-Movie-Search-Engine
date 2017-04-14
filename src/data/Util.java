package data;

import java.sql.*;
import java.util.HashSet;

/**
 * Created by yaojianwang on 3/5/17.
 */
public class Util {

    //open connection
    public static Connection openConnection() throws SQLException {

        // load driver
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Error loading driver: " + e);
        }

        //define connection URL
        String host = "192.168.56.101";
        String dbName = "ywang";
        int port = 1521;
        String serviceName = "orcl";
        String oracleURL = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;


        //establish connection
        String username = "ywang"; //df
        String password = "ywang";
        Connection connection = DriverManager.getConnection(oracleURL, username, password);

        return connection;
    }

    //close connection
    public static void closeConnection(Connection connection) {
        try {
            connection.close();

        } catch (SQLException e) {
            System.err.println("Cannot close connection: " + e.getMessage());
        }
    }

    //delete all data from table
    public static void deleteAllData(Connection connection) throws SQLException {
        try {
            System.out.println("Deleting the data...");
            deleteData(connection, "Movies");
            deleteData(connection, "Movie_genres");
            deleteData(connection, "Movie_directors");
            deleteData(connection, "Movie_actors");
            deleteData(connection, "Movie_countries");
            deleteData(connection, "Movie_locations");
            deleteData(connection, "Tags");
            deleteData(connection, "Movie_tags");
            deleteData(connection, "User_taggedmovies");
            deleteData(connection, "User_ratedmovies");
            System.out.println("Table is cleared. ");
        } catch (SQLException e) {
            System.out.println("Error -- " + e.toString());
        }
    }

    public static void deleteData(Connection connection, String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM " + tableName);
        statement.close();
    }

    public static ResultSet fetchAllGenres(Connection connection) throws SQLException {
        String sql = "SELECT DISTINCT GENRES FROM Movie_genres WHERE GENRES IS NOT NULL ORDER BY GENRES";
        System.out.println(String.format("fetch all genres: %s", sql));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public static ResultSet fetchAllCountries(Connection connection) throws SQLException {
        String sql = "SELECT DISTINCT country FROM Movie_countries WHERE country IS NOT NULL ORDER BY country";
        System.out.println(String.format("fetch all countries: %s", sql));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    //get countries based on genres
    public static ResultSet fetchCountryByGenres(Connection connection, HashSet<String> selectGenres, String searchLogic) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT MC.COUNTRY" +
                        " FROM Movie_countries MC, (" +
                        " SELECT MG.MovieId, LISTAGG(MG.genres, ',')" +
                        " WITHIN GROUP(ORDER BY MG.MovieId) AS genres" +
                        " FROM Movie_genres MG" +
                        " GROUP BY MG.MovieId) MG2" +
                        " WHERE MC.country IS NOT NULL AND MC.MovieId = MG2.MovieId "
        );

        for (String genre : selectGenres) {
            sql.append(searchLogic + " MG2.genres LIKE '%").append(genre).append("%' ");
        }

        sql.append(" ORDER BY MC.country");
        System.out.println(String.format("fetch countries by genres: %s", sql.toString()));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql.toString());
    }

    //get location based on genres and country
    public static ResultSet fetchFilmingLocationByGenreCountry(Connection connection, HashSet<String> selectedGenres,
                                                               HashSet<String> selectedCountries, String searchLogic) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT ML.country" +
                        " FROM Movie_countries MC, Movie_locations ML, (" +
                        " SELECT MG.MovieId, LISTAGG(MG.genres, ',')" +
                        " WITHIN GROUP(ORDER BY MG.MovieId) AS genres" +
                        " FROM Movie_genres MG" +
                        " GROUP BY MG.MovieId) MG2" +
                        " WHERE ML.country IS NOT NULL AND MC.MovieId = MG2.MovieId AND MC.MovieId = ML.MovieId "
        );
        //to do how to get location
        for (String genre : selectedGenres) {
            sql.append(searchLogic + "  MG2.genres LIKE '%").append(genre).append("%' ");
        }
        for (String country : selectedCountries) {
            sql.append(searchLogic + " MC.country LIKE '%").append(country).append("%' ");
        }
        sql.append(" ORDER BY ML.country");
        System.out.println(String.format("fetch country based on genres and country: %s", sql.toString()));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql.toString());
    }

}
