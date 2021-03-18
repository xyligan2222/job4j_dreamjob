package servlet;

import model.User;
import store.PsqlStore;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        if (email != null && !email.equals("") &&
                email.equals(PsqlStore.instOf().findUserByEmail(email))){
            resp.sendRedirect(req.getContextPath() + "/reg.jsp");
        } else if (email != null && !email.equals("") ) {
            User user = new User(
                    req.getParameter("login"),
                    email,
                    req.getParameter("password"));
            System.out.println(user);
            PsqlStore.instOf().save(user);
            HttpSession sc = req.getSession();
            sc.setAttribute("user", user);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }


    }
}
