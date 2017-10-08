import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(
        urlPatterns = "/shooting"
)
public class Shooting extends HttpServlet {

    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Первый игрок

        String query = "select * from users WHERE session_id = '" + request.getSession().getId() + "'";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.first();
            int connectedUser = resultSet.getInt(1);

            ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '" + connectedUser + "' || battles.user2_id = '" + connectedUser + "'");
            resultSetBattle.first();
            int battleId = resultSetBattle.getInt(1);
            int user1Id = resultSetBattle.getInt(2);
            int user2Id = resultSetBattle.getInt(3);

            ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '" + battleId + "'");
            resultSetTurnChecker.first();
            boolean firstUserTurn = resultSetTurnChecker.getBoolean(3),
                    secondUserTurn = resultSetTurnChecker.getBoolean(4);
            boolean hasPlayerMadeShot = resultSetTurnChecker.getBoolean(2);
            ResultSet resultSetUserFields = statement.executeQuery("select * from userfields where battle_id = '" + battleId + "'");
            resultSetUserFields.first();

            String notParsedUser1Field = resultSetUserFields.getString(3),
                    notParsedUser2Field = resultSetUserFields.getString(4);

            int[][] parsed1Array = parsingStringToArray(notParsedUser1Field);
            int[][] parsed2Array = parsingStringToArray(notParsedUser2Field);

            if (connectedUser == user1Id) {
                JSONParser jp = new JSONParser();
                String s = request.getParameterValues("data")[0];
                JSONArray arrayOfCoordinates = null;
                try {
                    arrayOfCoordinates = (JSONArray) jp.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int x = Integer.parseInt(arrayOfCoordinates.get(0).toString());
                int y = Integer.parseInt(arrayOfCoordinates.get(1).toString());

                if (parsed2Array[x][y] == 11) {
                    int[][] cloneOfArray = parsed2Array;

                    parsed2Array[x][y] = 14;
                    parsed2Array = roundTheShottedShip(parsed2Array, x, y);

                    for (int i = 0; i < 12; i++) {
                        for (int j = 0; j < 12; j++) {
                            if (cloneOfArray[i][j] == 13) {
                                parsed2Array[i][j] = 13;
                            } else if (cloneOfArray[i][j] == 11) {
                                parsed2Array[i][j] = 11;
                            }
                        }
                    }

                    hasPlayerMadeShot = true;
                    String parsedArray = parsingArrayToString(parsed2Array);


                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update userfields set second_user_field = '" + parsedArray + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update turn_checker set hasPlayerMadeShot = '" + convertBooleanToInt(hasPlayerMadeShot) + "', first_user_turn = '" + convertBooleanToInt(firstUserTurn) + "', second_user_turn = '" + convertBooleanToInt(secondUserTurn) + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();


                    response.getWriter().write(parsedArray);

                } else if (parsed2Array[x][y] == 0) {

                    parsed2Array[x][y] = 13;

                    firstUserTurn = false;
                    secondUserTurn = true;
                    hasPlayerMadeShot = true;
                    String parsedArray = parsingArrayToString(parsed2Array);

                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update userfields set second_user_field = '" + parsedArray + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update turn_checker set hasPlayerMadeShot = '" + convertBooleanToInt(hasPlayerMadeShot) + "', first_user_turn = '" + convertBooleanToInt(firstUserTurn) + "', second_user_turn = '" + convertBooleanToInt(secondUserTurn) + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();

                    response.getWriter().write(parsedArray);

                }
            } else if (connectedUser == user2Id) {
                JSONParser jp = new JSONParser();
                String s = request.getParameterValues("data")[0];
                JSONArray arrayOfCoordinates = null;
                try {
                    arrayOfCoordinates = (JSONArray) jp.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int x = Integer.parseInt(arrayOfCoordinates.get(0).toString());
                int y = Integer.parseInt(arrayOfCoordinates.get(1).toString());

                if (parsed1Array[x][y] == 11) {
                    int[][] cloneOfArray = parsed1Array;

                    parsed1Array[x][y] = 14;
                    parsed1Array = roundTheShottedShip(parsed1Array, x, y);

                    for (int i = 0; i < 12; i++) {
                        for (int j = 0; j < 12; j++) {
                            if (cloneOfArray[i][j] == 13) {
                                parsed1Array[i][j] = 13;
                            } else if (cloneOfArray[i][j] == 11) {
                                parsed1Array[i][j] = 11;
                            }
                        }
                    }


                    hasPlayerMadeShot = true;
                    String parsedArray = parsingArrayToString(parsed1Array);

                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update userfields set first_user_field = '" + parsedArray + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update turn_checker set hasPlayerMadeShot = '" + convertBooleanToInt(hasPlayerMadeShot) + "', first_user_turn = '" + convertBooleanToInt(firstUserTurn) + "', second_user_turn = '" + convertBooleanToInt(secondUserTurn) + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();

                    response.getWriter().write(parsedArray);

                } else if (parsed1Array[x][y] == 0) {

                    parsed1Array[x][y] = 13;

                    String parsedArray = parsingArrayToString(parsed1Array);

                    firstUserTurn = true;
                    secondUserTurn = false;
                    hasPlayerMadeShot = true;

                    PreparedStatement preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update userfields set first_user_field = '" + parsedArray + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();
                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("update turn_checker set hasPlayerMadeShot = '" + convertBooleanToInt(hasPlayerMadeShot) + "', first_user_turn = '" + convertBooleanToInt(firstUserTurn) + "', second_user_turn = '" + convertBooleanToInt(secondUserTurn) + "' where battle_id = '" + battleId + "'");
                    preparedStatement.execute();

                    response.getWriter().write(parsedArray);

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost(httpServletRequest, httpServletResponse);
    }

    private int[][] roundTheShottedShip(int[][] enemyField, int x, int y) {


        if (enemyField[x][y] == 14 &&
                enemyField[x + 1][y] != 14 && enemyField[x + 1][y] != 11 &&
                enemyField[x - 1][y] != 14 && enemyField[x - 1][y] != 11 &&
                enemyField[x][y - 1] != 14 && enemyField[x][y - 1] != 11 &&
                enemyField[x][y + 1] != 14 && enemyField[x][y + 1] != 11
                ) {
            enemyField[x + 1][y - 1] = 13;
            enemyField[x + 1][y] = 13;
            enemyField[x + 1][y + 1] = 13;
            enemyField[x][y - 1] = 13;
            enemyField[x][y + 1] = 13;
            enemyField[x - 1][y - 1] = 13;
            enemyField[x - 1][y] = 13;
            enemyField[x - 1][y + 1] = 13;
            enemyField[x][y - 1] = 13;
            enemyField[x][y + 1] = 13;
        } else if (enemyField[x - 1][y] == 14 || enemyField[x - 1][y] == 11 || enemyField[x + 1][y] == 14 || enemyField[x + 1][y] == 11) {

            if (enemyField[x][y] == 14 &&
                    enemyField[x + 1][y] != 11 &&
                    enemyField[x - 1][y] != 11
                    ) {
                //влево
                if (enemyField[x - 1][y] == 14) {
                    enemyField[x][y - 1] = 13;
                    enemyField[x - 1][y - 1] = 13;
                    enemyField[x - 2][y - 1] = 13;
                    enemyField[x - 2][y + 1] = 13;
                    enemyField[x - 1][y + 1] = 13;
                    enemyField[x][y + 1] = 13;
                    if (enemyField[x - 2][y] == 14) {
                        enemyField[x - 3][y - 1] = 13;
                        enemyField[x - 3][y + 1] = 13;
                        if (enemyField[x - 3][y] == 14) {
                            enemyField[x - 4][y - 1] = 13;
                            enemyField[x - 4][y] = 13;
                            enemyField[x - 4][y + 1] = 13;
                        } else {
                            if (enemyField[x - 3][y] == 11) {
                                enemyField[x][y - 1] = 0;
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x - 2][y - 1] = 0;
                                enemyField[x - 2][y + 1] = 0;
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x][y + 1] = 0;
                                enemyField[x - 3][y - 1] = 0;
                                enemyField[x - 3][y + 1] = 0;
                            } else {
                                enemyField[x - 3][y] = 13;
                            }
                        }
                    } else {
                        if(x!=10){
                            if (enemyField[x - 2][y] == 11 || enemyField[x + 2][y] == 11) {
                                enemyField[x][y - 1] = 0;
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x - 2][y - 1] = 0;
                                enemyField[x - 2][y + 1] = 0;
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x][y + 1] = 0;
                            } else {
                                enemyField[x - 2][y] = 13;
                            }
                        }else{
                            if(enemyField[x-2][y]!=11) {
                                enemyField[x - 2][y] = 13;
                            }
                        }

                    }
                } else {
                    if(x!=9){
                        if(x!=10){
                            if (enemyField[x + 2][y] == 11 || enemyField[x + 3][y] == 11) {
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x - 1][y] = 0;
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x][y - 1] = 0;
                                enemyField[x][y + 1] = 0;
                            } else {
                                enemyField[x - 1][y - 1] = 13;
                                enemyField[x - 1][y] = 13;
                                enemyField[x - 1][y + 1] = 13;
                                enemyField[x][y - 1] = 13;
                                enemyField[x][y + 1] = 13;
                            }
                        }else{
                            enemyField[x-1][y+1]=13;
                            enemyField[x-1][y-1]=13;
                            enemyField[x][y+1]=13;
                            enemyField[x][y-1]=13;
                        }

                    }else{
                        enemyField[x-1][y]=13;
                        enemyField[x-1][y+1]=13;
                        enemyField[x-1][y-1]=13;
                        enemyField[x][y+1]=13;
                        enemyField[x][y-1]=13;
                    }

                }

                //вправо
                if (enemyField[x + 1][y] == 14) {
                    enemyField[x][y - 1] = 13;
                    enemyField[x + 1][y - 1] = 13;
                    enemyField[x + 2][y - 1] = 13;
                    enemyField[x + 2][y + 1] = 13;
                    enemyField[x + 1][y + 1] = 13;
                    enemyField[x][y + 1] = 13;
                    if (enemyField[x + 2][y] == 14) {
                        enemyField[x + 3][y - 1] = 13;
                        enemyField[x + 3][y + 1] = 13;
                        if (enemyField[x + 3][y] == 14) {
                            enemyField[x + 4][y - 1] = 13;
                            enemyField[x + 4][y] = 13;
                            enemyField[x + 4][y + 1] = 13;
                        } else {
                            if (enemyField[x + 3][y] == 11) {
                                enemyField[x][y - 1] = 0;
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 2][y - 1] = 0;
                                enemyField[x + 2][y + 1] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x][y + 1] = 0;
                                enemyField[x + 3][y - 1] = 0;
                                enemyField[x + 3][y + 1] = 0;

                            } else {
                                enemyField[x + 3][y] = 13;
                            }
                        }
                    } else {
                        if(x!=1){
                            if (enemyField[x - 2][y] == 11 || enemyField[x + 2][y] == 11) {
                                enemyField[x][y - 1] = 0;
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 2][y - 1] = 0;
                                enemyField[x + 2][y + 1] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x][y + 1] = 0;
                            } else {
                                enemyField[x + 2][y] = 13;
                            }
                        }else{
                            if(enemyField[x+2][y]!=11) {
                                enemyField[x + 2][y] = 13;
                            }
                        }

                    }
                } else {
                    if(x!=1){
                        if(x!=2) {
                            if (enemyField[x - 2][y] == 11 || enemyField[x - 3][y] == 11) {
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x][y - 1] = 0;
                                enemyField[x][y + 1] = 0;
                            } else {
                                enemyField[x + 1][y - 1] = 13;
                                enemyField[x + 1][y] = 13;
                                enemyField[x + 1][y + 1] = 13;
                                enemyField[x][y - 1] = 13;
                                enemyField[x][y + 1] = 13;
                            }
                        }else{
                            enemyField[x+1][y]=13;
                            enemyField[x+1][y-1]=13;
                            enemyField[x+1][y+1]=13;
                            enemyField[x][y+1]=13;
                            enemyField[x][y-1]=13;
                        }
                    }else{
                        enemyField[x+1][y]=13;
                        enemyField[x+1][y-1]=13;
                        enemyField[x+1][y+1]=13;
                        enemyField[x][y-1]=13;
                    }

                }

            }
        } else if (enemyField[x][y - 1] == 14 || enemyField[x][y - 1] == 11 || enemyField[x][y + 1] == 14 || enemyField[x][y + 1] == 11) {

            if (enemyField[x][y] == 14 &&
                    enemyField[x][y + 1] != 11 &&
                    enemyField[x][y - 1] != 11
                    ) {
                //вверх
                if (enemyField[x][y - 1] == 14) {
                    enemyField[x - 1][y] = 13;
                    enemyField[x - 1][y - 1] = 13;
                    enemyField[x - 1][y - 2] = 13;
                    enemyField[x + 1][y] = 13;
                    enemyField[x + 1][y - 1] = 13;
                    enemyField[x + 1][y - 2] = 13;
                    if (enemyField[x][y - 2] == 14) {
                        enemyField[x + 1][y - 3] = 13;
                        enemyField[x - 1][y - 3] = 13;
                        if (enemyField[x][y - 3] == 14) {
                            enemyField[x - 1][y - 4] = 13;
                            enemyField[x][y - 4] = 13;
                            enemyField[x + 1][y - 4] = 13;
                        } else {
                            if (enemyField[x][y - 3] == 11) {
                                enemyField[x - 1][y] = 0;
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x - 1][y - 2] = 0;
                                enemyField[x + 1][y - 2] = 0;
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x + 1][y - 3] = 0;
                                enemyField[x - 1][y - 3] = 0;
                            } else {
                                enemyField[x][y - 3] = 13;
                            }
                        }
                    } else {
                        if(y!=10){
                            if (enemyField[x][y - 2] == 11 || enemyField[x][y + 2] == 11) {
                                enemyField[x - 1][y] = 0;
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x - 1][y - 2] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 1][y - 2] = 0;
                            } else {
                                enemyField[x][y - 2] = 13;
                            }
                        }else{
                            if(enemyField[x][y-2]!=11){
                                enemyField[x][y - 2] = 13;
                            }
                        }

                    }
                } else {
                    if(y!=9){
                        if(y!=10){
                            if (enemyField[x][y + 2] == 11 || enemyField[x][y + 3] == 11) {
                                enemyField[x - 1][y - 1] = 0;
                                enemyField[x][y - 1] = 0;
                                enemyField[x + 1][y - 1] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x - 1][y] = 0;
                            } else {
                                enemyField[x - 1][y - 1] = 13;
                                enemyField[x][y - 1] = 13;
                                enemyField[x + 1][y - 1] = 13;
                                enemyField[x + 1][y] = 13;
                                enemyField[x - 1][y] = 13;
                            }
                        }else{
                            enemyField[x-1][y]=13;
                            enemyField[x+1][y]=13;
                            enemyField[x-1][y-1]=13;
                            enemyField[x+1][y-1]=13;
                        }
                    }else{
                        enemyField[x][y-1]=13;
                        enemyField[x-1][y]=13;
                        enemyField[x+1][y]=13;
                        enemyField[x-1][y-1]=13;
                        enemyField[x+1][y-1]=13;
                    }

                }

                //вниз
                if (enemyField[x][y + 1] == 14) {
                    enemyField[x - 1][y] = 13;
                    enemyField[x - 1][y + 1] = 13;
                    enemyField[x - 1][y + 2] = 13;
                    enemyField[x + 1][y] = 13;
                    enemyField[x + 1][y + 1] = 13;
                    enemyField[x + 1][y + 2] = 13;
                    if (enemyField[x][y + 2] == 14) {
                        enemyField[x + 1][y + 3] = 13;
                        enemyField[x - 1][y + 3] = 13;
                        if (enemyField[x][y + 3] == 14) {
                            enemyField[x - 1][y + 4] = 13;
                            enemyField[x][y + 4] = 13;
                            enemyField[x + 1][y + 4] = 13;
                        } else {
                            if (enemyField[x][y + 3] == 11) {
                                enemyField[x - 1][y] = 0;
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x - 1][y + 2] = 0;
                                enemyField[x + 1][y + 2] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x + 1][y + 3] = 0;
                                enemyField[x - 1][y + 3] = 0;
                            } else {
                                enemyField[x][y + 3] = 13;
                            }
                        }
                    } else {
                        if(y!=1){
                            if (enemyField[x][y - 2] == 11 || enemyField[x][y + 2] == 11) {
                                enemyField[x - 1][y] = 0;
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x - 1][y + 2] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x + 1][y + 2] = 0;
                            } else {
                                enemyField[x][y + 2] = 13;
                            }
                        }else{
                            if(enemyField[x][y+2]!=11) {
                                enemyField[x][y + 2] = 13;
                            }
                        }
                    }
                } else {
                    if(y!=1){
                        if(y!=2){
                            if (enemyField[x][y - 2] == 11 || enemyField[x][y - 3] == 11) {
                                enemyField[x - 1][y + 1] = 0;
                                enemyField[x][y + 1] = 0;
                                enemyField[x + 1][y + 1] = 0;
                                enemyField[x + 1][y] = 0;
                                enemyField[x - 1][y] = 0;
                            } else {
                                enemyField[x - 1][y + 1] = 13;
                                enemyField[x][y + 1] = 13;
                                enemyField[x + 1][y + 1] = 13;
                                enemyField[x + 1][y] = 13;
                                enemyField[x - 1][y] = 13;
                            }
                        }else{
                            enemyField[x][y+1]=13;
                            enemyField[x-1][y]=13;
                            enemyField[x+1][y]=13;
                            enemyField[x-1][y+1]=13;
                            enemyField[x+1][y+1]=13;
                        }
                    }else{
                        enemyField[x-1][y]=13;
                        enemyField[x+1][y]=13;
                        enemyField[x-1][y+1]=13;
                        enemyField[x+1][y+1]=13;
                    }

                }
            }
        }


        enemyField = initializeBordersOfArray(enemyField);
        return enemyField;
    }

    private int[][] initializeBordersOfArray(int[][] array) {
        for (int i = 0; i < 11; i++) {
            array[0][i] = i;
            array[i][0] = i;
        }

        array[0][0] = 20;
        return array;
    }


    private static int[][] parsingStringToArray(String s) {
        int[][] parsedArray = new int[12][12];

        JSONParser jp = new JSONParser();
        JSONArray userArray = null;
        try {
            userArray = (JSONArray) jp.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < userArray.size(); i++) {
            JSONArray userArrayItem = (JSONArray) userArray.get(i);

            for (int r = 0; r < userArrayItem.size(); r++) {
                parsedArray[i][r] = Integer.parseInt(userArrayItem.get(r).toString());

            }
        }
        return parsedArray;
    }

    private static String parsingArrayToString(int[][] array) {

        org.json.JSONArray parentJsonArray = new org.json.JSONArray();
        // loop through your elements
        for (int i = 0; i < 12; i++) {
            org.json.JSONArray childJsonArray = new org.json.JSONArray();
            System.out.println();
            for (int j = 0; j < 12; j++) {
                childJsonArray.put(array[i][j]);
            }
            parentJsonArray.put(childJsonArray);
        }
        String parsedArray = parentJsonArray.toString();


        return parsedArray;
    }

    private int convertBooleanToInt(boolean bool) {
        int convertedBool;

        if (bool) {
            convertedBool = 1;
        } else {
            convertedBool = 0;
        }
        return convertedBool;
    }
}
