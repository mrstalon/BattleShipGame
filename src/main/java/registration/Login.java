package registration;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;


@WebServlet(
        urlPatterns = "/login"
)
public class Login extends HttpServlet{

    private boolean isLoginValid = false;
    private boolean isPassValid = false;
    private   char[] convertedString;
    private static String login;
    private static String password;
    private static final String HOST = "jdbc:mysql://localhost:3306/mytest";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static ResultSet resultSet;
    private static int queuePosition = 0;


    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        login = httpServletRequest.getParameter("login");
        password = httpServletRequest.getParameter("password");

        isLoginValid = Registration.validChecker(login);
        isPassValid = Registration.validChecker(password);



        if(isLoginValid){
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
                resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    String promLogin = resultSet.getString(2);
                    if(promLogin.equals(login)){
                        if(md5Apache(password).equals(resultSet.getString(3))){
                            queuePosition = resultSet.getInt(1);
                            isPassValid = true;
                            PreparedStatement preparedStatement = null;
                            preparedStatement = connection.prepareStatement("UPDATE users set session_id = '"+httpServletRequest.getSession().getId()+"' WHERE id = "+queuePosition+"");
                            preparedStatement.execute();
                        }else{
                            isPassValid = false;
                        }
                        isLoginValid = true;
                        break;
                    }else{
                        isLoginValid = false;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(isLoginValid){
                if(isPassValid){
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                }else{
                    httpServletRequest.setAttribute("index", 3 );
                    httpServletRequest.setAttribute("resultPass", "Неверный пароль");
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
                    rd.forward(httpServletRequest, httpServletResponse);
                }
            }else{
                httpServletRequest.setAttribute("index", 2 );
                httpServletRequest.setAttribute("result", "Такого логина не существует");
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
                rd.forward(httpServletRequest, httpServletResponse);
            }
        }else{
            httpServletRequest.setAttribute("index", 1 );
            httpServletRequest.setAttribute("result", "В логине имеются недопустимые символы");
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
            rd.forward(httpServletRequest, httpServletResponse);
        }
    }

    public static String md5Apache(String st) {
        String md5Hex = DigestUtils.md5Hex(st);

        return md5Hex;
    }
}
