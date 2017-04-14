package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;


/**
 * Created by yaojianwang on 3/5/17.
 */
public class Populate {
    private Connection connection;


    public void execute() {
        try {
            connection = Util.openConnection();
            populateTable();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //close connection
            Util.closeConnection(connection);
        }
    }


    //populate table
    private void populateTable() throws SQLException {
        System.out.println("Populate all data.");
        System.out.println("Inserting data for Movies");
        populateMovies(connection);
        System.out.println("Table Movies is finished");
        System.out.println("Inserting data for Movie_genres");
        populateMovie_genres(connection);
        System.out.println("Table Movie_genres is finished");
        System.out.println("Inserting data for Movie_directors");
        populateMovie_directors(connection);
        System.out.println("Table Movie_directors is finished");
        System.out.println("Inserting data for Movie_actors");
        populateMovie_actors(connection);
        System.out.println("Table Movie_actors is finished");
        System.out.println("Inserting data for Movie_countries");
        populateMovie_countries(connection);
        System.out.println("Table Movie_countries is finished");
        System.out.println("Inserting data for Movie_locations");
        populateMovie_locations(connection);
        System.out.println("Table Movie_locations is finished");
        System.out.println("Inserting data for Tags");
        populateTags(connection);
        System.out.println("Table Tags is finished");
        System.out.println("Inserting data for Movie_tags");
        populateMovie_tags(connection);
        System.out.println("Table Movie_tags is finished");
        System.out.println("Inserting data for User_taggedmovies");
        populateUser_taggedmovies(connection);
        System.out.println("Table User_taggedmovies is finished");
        System.out.println("Inserting data for User_ratedmovies");
        populateUser_ratedmovies(connection);
        System.out.println("Table User_ratedmovies is finished");

    }

    private void populateMovies(Connection connection) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader("src/movielens/movies.dat");
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement("INSERT INTO Movies VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            int[] index = new int[] {0, 1, 2, 5, 6, 7, 8, 11, 12, 13, 16, 17, 18, 19};
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");
                for (int i = 0; i < index.length; i++) {
                    //integer value
                    if (data[index[i]].equals("\\N")) {
                        statement.setObject(i + 1, null);
                    } else if (index[i] == 5 || index[i] == 8 || index[i] == 18) {
                        int integer = Integer.parseInt(data[index[i]]);
                        statement.setInt(i + 1, integer);
                    } else if (index[i] == 0 || index[i] == 1 || index[i] == 2 || index[i] == 6) {
                        statement.setString(i + 1, data[index[i]]);
                    } else {
                        double dble = Double.parseDouble(data[index[i]]);
                        statement.setDouble(i + 1, dble);
                    }
                }
                statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }
    }

    private void populateMovie_genres(Connection connection) throws SQLException {
        insertHelper("src/movielens/movie_genres.dat", "INSERT INTO Movie_genres VALUES (?,?)");

    }

    private void populateMovie_directors(Connection connection) throws SQLException {
        insertHelper("src/movielens/movie_directors.dat", "INSERT INTO Movie_directors VALUES (?,?,?)");

    }

    private void populateMovie_actors(Connection connection) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader("src/movielens/movie_actors.dat");
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement("INSERT INTO Movie_actors VALUES (?,?,?,?)");
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");
                for (int i = 0; i < 3; i++)  {
                    statement.setString(i + 1, data[i]);

                }
                int integer = Integer.parseInt(data[3]);
                statement.setInt(4, integer);
                statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }

    }

    private void populateMovie_countries(Connection connection) throws SQLException {
        insertHelper("src/movielens/movie_countries.dat", "INSERT INTO Movie_countries VALUES (?,?)");
    }

    //problem
    private void populateMovie_locations(Connection connection) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader("src/movielens/movie_locations.dat");
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement("INSERT INTO Movie_locations VALUES (?,?,?,?)");
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");

                for (int i = 0; i < 4; i++)  {
                    if (i < data.length) {
                        statement.setString(i + 1, data[i]);

                    } else {
                        statement.setString(i + 1, null);
                    }


                }

                statement.executeUpdate();

            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }
    }

    private void populateTags(Connection connection) throws SQLException {
        insertHelper("src/movielens/tags.dat", "INSERT INTO Tags VALUES (?,?)");

    }

    private void populateMovie_tags(Connection connection) throws SQLException {
        insertHelper("src/movielens/movie_tags.dat", "INSERT INTO Movie_tags VALUES (?,?,?)");

    }

    private void populateUser_taggedmovies(Connection connection) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader("src/movielens/user_taggedmovies-timestamps.dat");
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement("INSERT INTO User_taggedmovies VALUES (?,?,?,?)");
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");
                for (int i = 0; i < 3; i++)  {
                    statement.setString(i + 1, data[i]);

                }
                statement.setTimestamp(4, new Timestamp(Long.parseLong(data[3])));
                statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }

    }

    private void populateUser_ratedmovies(Connection connection) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader("src/movielens/user_ratedmovies-timestamps.dat");
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement("INSERT INTO User_ratedmovies VALUES (?,?,?,?)");
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");
                for (int i = 0; i < 2; i++)  {

                    statement.setString(i + 1, data[i]);

                }
                double dble = Double.parseDouble(data[2]);
                statement.setDouble(3, dble);
                statement.setTimestamp(4, new Timestamp(Long.parseLong(data[3])));
                statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }
    }


    private void insertHelper(String path, String sql) throws SQLException {
        String thisLine = null;
        PreparedStatement statement = null;

        try {

            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            thisLine = br.readLine();//skip the first line

            statement = connection.prepareStatement(sql);
            while ((thisLine = br.readLine()) != null) {
                String[] data = thisLine.trim().split("\\t");
                for (int i = 0; i < data.length; i++)  {

                    statement.setString(i + 1, data[i]);

                }
                statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
        }
    }

}
