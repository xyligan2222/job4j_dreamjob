package servlet;

import model.User;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        User username = PsqlStore.instOf().findUserByEmail(email);
        System.out.println("2" +req.getParameter(PsqlStore.instOf().findUserByEmail(email).getEmail()));
        if (email.equals(username.getEmail()) && password.equals(username.getPassword())) {
            HttpSession sc = req.getSession();
            User user= new User();
            sc.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/index.do");
        } else {
            req.setAttribute("error", "Не верный email или пароль");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
