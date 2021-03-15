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
        System.out.println("doPost RegServlet: password - " + req.getParameter("password"));
        if (req.getParameter("email").equals(PsqlStore.instOf().findUserByEmail(req.getParameter("email")))){
            resp.sendRedirect(req.getContextPath() + "/reg.jsp");
        } else {
            User user = new User(
                    req.getParameter("login"),
                    req.getParameter("email"),
                    req.getParameter("password"));
            System.out.println(user);
            PsqlStore.instOf().save(user);
            HttpSession sc = req.getSession();
            sc.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}
