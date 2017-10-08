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
        urlPatterns = "/end"
)
public class CheckIsGameEnded extends HttpServlet{


    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        boolean isGameEnded;
        int whichPlayerLose = 0;
        int counterFirst = 0;
        int counterSecond = 0;

        String query = "select * from users WHERE session_id = '"+httpServletRequest.getSession().getId()+"'";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
            Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.first();
            int connectedUser = resultSet.getInt(1);

            ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '"+connectedUser+"' || battles.user2_id = '"+connectedUser+"'");
            resultSetBattle.first();
            int battleId = resultSetBattle.getInt(1);


            ResultSet resultSetUserFields = statement.executeQuery("select * from userfields where battle_id = '"+battleId+"'");
            resultSetUserFields.first();
            String notParsedUser1Field = resultSetUserFields.getString(3),
                    notParsedUser2Field = resultSetUserFields.getString(4);
            int[][] parsedUser1Field = parsingStringToArray(notParsedUser1Field),
                    parsedUser2Field = parsingStringToArray(notParsedUser2Field);


            for(int i=0;i<12;i++){
                for(int j=0;j<12;j++){
                    if(parsedUser1Field[i][j]==11){
                        counterFirst++;
                    }
                    if(parsedUser2Field[i][j]==11){
                        counterSecond++;
                    }
                }
            }

            if(counterFirst == 0 || counterSecond == 0){
                isGameEnded = true;
            }else{
                isGameEnded = false;
            }

            if(counterFirst==0){
                whichPlayerLose = 1;
            }else if(counterSecond==0){
                whichPlayerLose = 2;
            }

            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement("update turn_checker set which_player_win = '"+whichPlayerLose+"' where battle_id = '"+battleId+"'");
            preparedStatement.execute();

            if(isGameEnded){
                httpServletResponse.getWriter().write("true");
            }else{
                httpServletResponse.getWriter().write("false");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        String query = "select * from users WHERE session_id = '"+httpServletRequest.getSession().getId()+"'";
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
            Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.first();
            int connectedUser = resultSet.getInt(1);

            ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '"+connectedUser+"' || battles.user2_id = '"+connectedUser+"'");
            resultSetBattle.first();
            int battleId = resultSetBattle.getInt(1);

            ResultSet resultSetTurnChecker = statement.executeQuery("select * from turn_checker where battle_id = '"+battleId+"'");
            resultSetTurnChecker.first();
            int whichPlayerLose = resultSetTurnChecker.getInt(7);

            if(whichPlayerLose==1){
                httpServletResponse.getWriter().write("first");
            }else if(whichPlayerLose==2){
                httpServletResponse.getWriter().write("second");
            }

        } catch (SQLException e) {
            try(Connection connection = DriverManager.getConnection(HOST, USERNAME,PASSWORD);
                Statement statement = connection.createStatement()
            ) {
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.first();
                int connectedUser = resultSet.getInt(1);

                ResultSet resultSetBattle = statement.executeQuery("select * from battles WHERE user1_id = '"+connectedUser+"' || battles.user2_id = '"+connectedUser+"'");
                resultSetBattle.first();
                int battleId = resultSetBattle.getInt(1);

               PreparedStatement preparedStatement;
               preparedStatement = connection.prepareStatement("delete from turn_checker where battle_id = '"+battleId+"'");
               preparedStatement.execute();
               preparedStatement = connection.prepareStatement("delete from userfields where battle_id = '"+battleId+"'");
               preparedStatement.execute();
               preparedStatement = connection.prepareStatement("delete from battles where id = '"+battleId+"'");
               preparedStatement.execute();
               connection.close();
            } catch (SQLException r) {
                r.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private static int[][] parsingStringToArray(String s){
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

    private static String parsingArrayToString(int[][] array){

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
}
