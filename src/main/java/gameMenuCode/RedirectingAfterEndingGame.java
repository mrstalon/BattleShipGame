package gameMenuCode;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(
        urlPatterns = "/redirect-after-battle"
)
public class RedirectingAfterEndingGame extends HttpServlet{


    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/find-game.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
    }


}
