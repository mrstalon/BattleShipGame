package gameMenuCode;

import com.sun.org.apache.regexp.internal.RE;
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
        urlPatterns = "/waiting"
)
public class WaitingForOtherPlayer extends HttpServlet{

    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String query = "select * from users";
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
           ResultSet resultSetOfBattle = null;
           ResultSet resultSetOfUserFields = null;
            resultSetToFindUser = statement.executeQuery("select * from users where session_id = '"+httpServletRequest.getSession().getId()+"'");
            resultSetToFindUser.first();
            int waitingUserId = resultSetToFindUser.getInt(1);
            resultSetOfBattle = statement.executeQuery("select * from battles where user1_id = '"+waitingUserId+"'");
            resultSetOfBattle.first();
            int isCellEmpty = resultSetOfBattle.getInt(3),
                    ballteId = resultSetOfBattle.getInt(1);
            resultSetOfUserFields = statement.executeQuery("select * from userfields where battle_id = '"+ballteId+"'");
            resultSetOfUserFields.first();
            String parsedArray = resultSetOfUserFields.getString(3);

            if(isCellEmpty==0){
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/wait-second-player.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
            }else{
                PreparedStatement preparedStatement = null;



                httpServletRequest.setAttribute("userArray", parsedArray);
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/main.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost(httpServletRequest, httpServletResponse);
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
