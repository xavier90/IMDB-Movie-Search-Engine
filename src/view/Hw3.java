package view;/**
 * Created by yaojianwang on 3/6/17.
 */

import data.Populate;
import data.Util;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.nashorn.internal.objects.annotations.Where;

import java.awt.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class Hw3 extends Application implements Initializable{
    Populate populateData;
    Connection connection;
    String searchLogic = "AND";

    //store genres
    HashSet<String> selectedGenres = new HashSet<>();
    HashSet<String> selectedCountries = new HashSet<>();
    HashSet<String> selectedLocation = new HashSet<>();
    HashSet<String> selectedMovieTag = new HashSet<>();

    //final result set
    ResultSet finalResult = null;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, IOException {
        //Util.openConnection();//open connection

        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        primaryStage.setTitle("Movie_by_YaojianWang");
        primaryStage.setScene(new Scene(root, 991, 680));

        primaryStage.show();



    }

    //button for opening the system
    @FXML
    private ToggleButton on;
    @FXML
    private Button getGenres;
    @FXML
    private AnchorPane genresPane;
    @FXML
    private VBox vboxGenres;
    @FXML
    private VBox vboxCountry;
    @FXML
    private VBox vboxLocation;
    @FXML
    private ComboBox logicComboBox;
    @FXML
    private ComboBox ratingCompareComboBox;
    @FXML
    private ComboBox numberOfReviewComboBox;
    @FXML
    private ComboBox tagWeightComboBox;
    @FXML
    private TextField ratingCompareValue;
    @FXML
    private TextField numberOfReviewValue;
    @FXML
    private TextField tagWeightValue;
    @FXML
    private DatePicker yearFrom;
    @FXML
    private DatePicker yearTo;
    @FXML
    private Button generateQuery;
    @FXML
    private Button executeQueryButton;
    @FXML
    private TextArea resultTextArea;
    @FXML
    private TextArea queryTextArea;
    @FXML
    private VBox vboxMovieTag;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateData = new Populate();
        try {
            connection = Util.openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        on.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (on.isSelected()) {;
                    populateData.execute();
                } else  {
                    //delete all the data
//                    try {
//                        connection = Util.openConnection();
//                        Util.deleteAllData(connection);
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    } finally {
//                        Util.closeConnection(connection);
//                    }

                }
            }
        });

        //after press getGenres, generate checkbox for genres
        getGenres.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {
                    //generate checkbox for genres
                    ResultSet genresResult = Util.fetchAllGenres(connection);
                    while (genresResult.next()) {
                        String genre = genresResult.getString("GENRES");
                        CheckBox checkBox = new CheckBox(genre);
                        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                if (newValue) {
                                    selectedGenres.add(genre);
                                } else {
                                    selectedGenres.remove(genre);
                                }
                                generateCountryPane(connection);
                            }
                        });
                        vboxGenres.getChildren().add(checkBox);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


        logicComboBox.getItems().addAll("AND", "OR");
        logicComboBox.setValue("AND");
        logicComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                if (newValue == null) {
                    System.out.println("Please choose AND or OR!");
                } else if (newValue.equals("AND")) {//choose and
                    searchLogic = "AND";
//                    if (oldValue != null && oldValue.equals("OR")) {
//                        selectedCountries.clear();
//                        vboxCountry.getChildren().clear();
//                        generateCountryPane(connection);
//
//
//                    }
                } else if (newValue.equals("OR")) {//choose or
                    searchLogic = "OR";
//                    if (oldValue != null && oldValue.equals("AND")) {
//                        System.out.println("or");
//                    }
                }
            }
        });

        //critics' rating pane
        ratingCompareComboBox.getItems().addAll("=", "<", ">", ">=", "<=");
        numberOfReviewComboBox.getItems().addAll("=", "<", ">", ">=", "<=");
        tagWeightComboBox.getItems().addAll("=", "<", ">", ">=", "<=");

        //generate query
        generateQuery.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    userExecuteQuery(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        //execute query button
        executeQueryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (finalResult != null) {
                    try {
                        printResult(finalResult);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    //after choose the checkbox in genres, generate checkbox for countries
    private void generateCountryPane(Connection connection) {
        //first clear the country pane
        vboxCountry.getChildren().clear();
        if (!selectedGenres.isEmpty()) {
            try {
                ResultSet countriesResult = Util.fetchCountryByGenres(connection, selectedGenres, searchLogic);
                HashSet<String> selectedCountries_copy = new HashSet<>();
                while (countriesResult.next()) {
                    String country = countriesResult.getString("country");
                    CheckBox checkBox = new CheckBox(country);

                    //once checkbox in genres been selected, generate a new countrypane
                    if (selectedCountries.contains(country)) {
                        checkBox.setSelected(true);
                        selectedCountries_copy.add(country);
                    }
                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) {
                                selectedCountries.add(country);
                            } else {
                                selectedCountries.remove(country);
                            }
                            generateLocationPane(connection);
                            generateMovieTag(connection);
                        }
                    });
                    vboxCountry.getChildren().add(checkBox);
                }
                selectedCountries = selectedCountries_copy;
            } catch (SQLException e) {
                System.err.println("Errors occurs when fetching data for country " + e.getMessage());
            }
        }
    }

    //generate filming location country based on genres and country
    private void generateLocationPane(Connection connection) {
        //first clear the country pane
        vboxLocation.getChildren().clear();
        if (!selectedCountries.isEmpty() && !logicComboBox.getValue().equals("OR")) {
            try {
                ResultSet filmingLocationResult = Util.fetchFilmingLocationByGenreCountry(connection, selectedGenres, selectedCountries, searchLogic);
                HashSet<String> selectedLocation_copy = new HashSet<>();
                while (filmingLocationResult.next()) {
                    String location = filmingLocationResult.getString("country");
                    CheckBox checkBox = new CheckBox(location);

                    //once checkbox in genres been selected, generate a new countrypane
                    if (selectedLocation.contains(location)) {
                        checkBox.setSelected(true);
                        selectedLocation_copy.add(location);
                    }
                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) {
                                selectedCountries.add(location);
                            } else {
                                selectedCountries.remove(location);
                            }
                            generateMovieTag(connection);
                        }
                    });
                    vboxLocation.getChildren().add(checkBox);
                }
                selectedLocation = selectedLocation_copy;
            } catch (SQLException e) {
                System.err.println("Errors occurs when fetching data for location" + e.getMessage());
            }

        }
    }

    private void generateMovieTag(Connection connection) {
        vboxMovieTag.getChildren().clear();

        try {
            ResultSet movieTagResult = fetchMovieTag();
            HashSet<String> selectedMovieTag_copy = new HashSet<>();
            while (movieTagResult.next()) {
                String tag = movieTagResult.getString("tagText");
                CheckBox checkBox = new CheckBox(tag);

                //once checkbox in genres been selected, generate a new countrypane
                if (selectedMovieTag.contains(tag)) {
                    checkBox.setSelected(true);
                    selectedMovieTag_copy.add(tag);
                }
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            selectedMovieTag.add(tag);
                        } else {
                            selectedMovieTag.remove(tag);
                        }
                    }
                });
                vboxMovieTag.getChildren().add(checkBox);
            }
            selectedMovieTag = selectedMovieTag_copy;
        } catch (SQLException e) {
            System.err.println("Errors occurs when fetching data for movie tag" + e.getMessage());
        }
    }

    private ResultSet fetchMovieTag() throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT tagtext " +
                " FROM movies M, movie_tags MT, tags T, " +
                " (SELECT DISTINCT MovieId, LISTAGG(country, ',') WITHIN GROUP (ORDER BY country) AS LOC\n" +
                " FROM (SELECT DISTINCT country, MovieId FROM movie_Locations) LOC2\n" +
                " GROUP BY MovieId) L, movie_countries MC,\n" +
                " (SELECT MovieId, LISTAGG(genres, ', ') WITHIN GROUP (ORDER BY genres) AS Genre\n" +
                " FROM movie_genres GROUP BY MovieId) G\n"

        );
        StringBuilder where = new StringBuilder(" WHERE M.MovieId = MC.MovieId AND M.MovieId = G.MovieId AND M.MovieId = L.MovieId" +
                                                        " AND MT.tagId = T.tagId AND M.MovieId = MT.MovieId ");


        if (selectedGenres.size() != 0) {
            helperForWhere(where, collectQuery(selectedGenres, "G.genre"));
        }

        if (selectedCountries.size() != 0) {
            helperForWhere(where, collectQuery(selectedCountries, "MC.country"));
        }
        if (selectedLocation.size() != 0) {
            helperForWhere(where, collectQuery(selectedLocation, "L.loc"));
        }

        //add critic rating
        if (!ratingCompareValue.getText().isEmpty()) {
            String value = ratingCompareValue.getText();
            where.append("  AND (M.RTACriticRating " + ratingCompareComboBox.getValue() + value + ") ");
        }

        //add number of reviews
        if (!numberOfReviewValue.getText().isEmpty()) {
            String number = numberOfReviewValue.getText();
            where.append(" AND (M.RTANumberOfReviews " + numberOfReviewComboBox.getValue() + number + ") ");
        }

        //add movie year
        if (yearFrom.getValue() != null || yearTo.getValue() != null) {
            if (yearFrom.getValue() != null) {
                where.append(" AND (M.year >= " + yearFrom.getValue() + ") ");
            }
            if (yearTo.getValue() != null) {
                where.append(" AND (M.year <= " + yearTo.getValue() + ") ");
            }
        }

        sql = sql.append(where.toString());
        //sql.append(" ORDER BY tagtext");
        System.out.println(String.format("fetch movie tag based on genres and country: %s", sql.toString()));
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql.toString());
    }

    private void userExecuteQuery(Connection connection) throws SQLException {
        //String logicWord = logicComboBox.getValue().equals("AND") ? "AND" : "OR";
        StringBuilder select = new StringBuilder();
        StringBuilder from = new StringBuilder();
        StringBuilder where = new StringBuilder();

        //select
        select.append(" SELECT DISTINCT M.title, G.genre AS genre, M.year, MC.country, L.loc AS allFilmingLocation," +
                    " TRUNC((RTACriticRating + RTTCriticRating + RTACriticRating)/3, 1) AS averageRating, " +
                    " TRUNC((RTANumberOfReviews + RTTNumberOfReviews + RTAudienceNumberOfRating)/3, 1) AS averageReviews ");

        //from
        from.append(" FROM movies M, " +
                                    " (SELECT DISTINCT movieID, LISTAGG(country, ',') WITHIN GROUP (ORDER BY country) AS LOC " +
                   " FROM (SELECT DISTINCT country, movieId FROM movie_locations) LOC2" +
                   " GROUP BY movieId) L, movie_countries MC, (SELECT movieID, LISTAGG(genres, ', ') WITHIN GROUP (ORDER BY genres) AS Genre" +
                   " FROM movie_genres GROUP BY movieID) G ");

        //where
        where.append(" WHERE M.movieID = MC.movieID AND M.movieID = G.movieID AND M.movieID = L.movieID ");


        //genres, countries and locations

        if (selectedGenres.size() != 0) {
            helperForWhere(where, collectQuery(selectedGenres, "G.genre"));
        }

        if (selectedCountries.size() != 0) {
            helperForWhere(where, collectQuery(selectedCountries, "MC.country"));
        }
        if (selectedLocation.size() != 0) {
            helperForWhere(where, collectQuery(selectedLocation, "L.loc"));
        }

        //add critic rating
        if (!ratingCompareValue.getText().isEmpty()) {
            String value = ratingCompareValue.getText();
            where.append("  AND (M.RTACriticRating " + ratingCompareComboBox.getValue() + value + ") ");
        }

        //add number of reviews
        if (!numberOfReviewValue.getText().isEmpty()) {
            String number = numberOfReviewValue.getText();
            where.append(" AND (M.RTANumberOfReviews " + numberOfReviewComboBox.getValue() + number + ") ");
        }

        //add movie year
        if (yearFrom.getValue() != null || yearTo.getValue() != null) {
            if (yearFrom.getValue() != null) {
                where.append(" AND (M.year >= " + yearFrom.getValue() + ") ");
            }
            if (yearTo.getValue() != null) {
                where.append(" AND (M.year <= " + yearTo.getValue() + ") ");
            }
        }

        //add movie tag
        if (selectedMovieTag.size() != 0) {

            helperForWhereMovieTag(where, collectQuery(selectedMovieTag, "T1.tagText"));
        }

        //add tag weight




        //combine query together and then return result set
        String sql = select.toString() + from.toString() + where.toString();
        Statement statement = connection.createStatement();
        queryTextArea.clear();
        queryTextArea.setText(sql);
        printResult(statement.executeQuery(sql));

    }



    //helper function for where
    private void helperForWhere(StringBuilder where, String sql) {
        where.append("AND (\n " + sql + ")");
    }

    //special helper for movie tag
    private void helperForWhereMovieTag(StringBuilder where, String sql) {
        where.append("AND ( M.movieId in (" +
                                        " SELECT MT1.movieId" +
                                        " FROM Movie_tags MT1, Tags T1" +
                                        " WHERE MT1.tagId = T1.tagId AND " + sql);
        if(tagWeightValue.getText().isEmpty()) {
            where.append("))");
        } else {
            String value = tagWeightValue.getText();
            where.append(searchLogic + " MT1.tagWeight " + tagWeightComboBox.getValue() + value +"))");
        }
    }

    private String collectQuery(HashSet<String> selectedSet, String item) {
        StringBuilder sb = new StringBuilder();
        String prefix = " ";
        for (String element : selectedSet) {
            if (sb.length() == 0) {
                sb.append(item + " LIKE '%" + element + "%' ");
            } else {
                sb.append(searchLogic + prefix + item + " LIKE '%" + element + "%' ");
            }


        }

        return sb.toString();
    }

    private void printResult(ResultSet result) throws SQLException {

        if (!result.isBeforeFirst()) {
            resultTextArea.setText("No data...");
        } else {
            ResultSetMetaData meta = result.getMetaData();
            StringBuilder sb = new StringBuilder();
            while (result.next()) {
                for (int col = 1; col <= meta.getColumnCount(); col++) {
                    sb.append(result.getString(col));
                    if (col != meta.getColumnCount()) {
                        sb.append(" \t ");
                    }
                }
                sb.append("\n");
            }
            //resultTextArea.clear();
            resultTextArea.setText(sb.toString());
        }
    }
}
