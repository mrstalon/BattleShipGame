package gameMenuCode;

import org.json.JSONArray;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(
        urlPatterns = "/findingGame"
)
public class FindingGame extends HttpServlet{

    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";


    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
            Statement statement = connection.createStatement()
        ) {

            ResultSet resultSetToFindUser = null;

                resultSetToFindUser = statement.executeQuery("select * from users where session_id = '"+httpServletRequest.getSession().getId()+"'");
                resultSetToFindUser.first();
                int user1Id = resultSetToFindUser.getInt(1);
                PreparedStatement preparedStatement = null;
                preparedStatement = connection.prepareStatement("insert into battles(user1_id) values(?)");
                preparedStatement.setInt(1, user1Id);
                preparedStatement.execute();

                preparedStatement = null;

            ResultSet resultSet = statement.executeQuery("SELECT * from battles WHERE user1_id = '"+user1Id+"'");
            resultSet.first();
            int battleId = resultSet.getInt(1);

            int[][] userArray = new int[12][12];
            userArray = initializeArray(userArray);
            JSONArray parentJsonArray = new JSONArray();
            // loop through your elements
            for (int i=0; i<12; i++){
                JSONArray childJsonArray = new JSONArray();
                for (int j =0; j<12; j++){
                    childJsonArray.put(userArray [i][j]);
                }
                parentJsonArray.put(childJsonArray);
            }
            String parsedArray = parentJsonArray.toString();

            preparedStatement = null;
            preparedStatement = connection.prepareStatement("insert into userfields(battle_id, first_user_field) VALUES(?,?)");
            preparedStatement.setInt(1, battleId);
            preparedStatement.setString(2, parsedArray);
            preparedStatement.execute();


            RequestDispatcher rd = getServletContext().getRequestDispatcher("/wait-second-player.jsp");
            rd.forward(httpServletRequest, httpServletResponse);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String query = "select * from battles";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
            Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet;
            ResultSet resultSetToFindUser;
            resultSet = statement.executeQuery(query);

            boolean isGameFounded = false;

            while (resultSet.next()) {
                int user1 = resultSet.getInt(2),
                        user2 = resultSet.getInt(3),
                        battleId = resultSet.getInt(1);

                if (user2 == 0) {
                    resultSetToFindUser = statement.executeQuery("select * from users where session_id = '" + httpServletRequest.getSession().getId() + "'");
                    resultSetToFindUser.first();
                    int currentUserId = resultSetToFindUser.getInt(1);
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("UPDATE battles set user2_id = '"+currentUserId+"' WHERE id = "+battleId+"");
                    preparedStatement.execute();


                    int[][] userArray = new int[12][12];
                    userArray = initializeArray(userArray);
                    JSONArray parentJsonArray = new JSONArray();
                    // loop through your elements
                    for (int i=0; i<12; i++){
                        JSONArray childJsonArray = new JSONArray();
                        for (int j =0; j<12; j++){
                            childJsonArray.put(userArray [i][j]);
                        }
                        parentJsonArray.put(childJsonArray);
                    }
                    String parsedArray = parentJsonArray.toString();

                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("UPDATE userfields set second_user_field = '"+parsedArray+"' WHERE battle_id = "+battleId+"");
                    preparedStatement.execute();

                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement("insert into turn_checker values(?,?,?,?,?,?,?)");
                    preparedStatement.setInt(1,battleId);
                    preparedStatement.setBoolean(2,false);
                    preparedStatement.setBoolean(3,false);
                    preparedStatement.setBoolean(4,false);
                    preparedStatement.setBoolean(5,false);
                    preparedStatement.setBoolean(6,false);
                    preparedStatement.setInt(7,0);
                    preparedStatement.execute();

                    isGameFounded = true;

                    httpServletRequest.setAttribute("userArray", parsedArray);
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/main.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                    break;
                }
            }

            if(!isGameFounded){
                httpServletRequest.setAttribute("game", "Нет свободных игр");
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int[][] initializeArray(int[][] array){
        for(int r=0; r<12; r++){
            for(int i=0; i<12;i++){
                array[i][r]= 0;
            }
        }

        return  initializeBordersOfArray(array);
    }

    private static int[][] initializeBordersOfArray(int[][] array){
        for (int i = 0; i < 11; i++) {
            array[0][i] = i;
            array[i][0] = i;
        }

        array[0][0] = 20;
        return array;
    }
}
