package servlet;

import model.Candidate;
import store.MemStore;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class CandidateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", PsqlStore.instOf().findAllCandidates());
        //Object test = req.getSession().getAttribute("photo_id");
        req.setAttribute("photos", PsqlStore.instOf().findAllPhoto());
        req.getRequestDispatcher("/candidate/candidates.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        PsqlStore.instOf().saveCandidate(new Candidate(Integer.parseInt(req.getParameter("id")),
                                                                req.getParameter("name")));
                                                                //Integer.parseInt((String) req.getSession().getAttribute("photo_id"))));
                                                                 //(int) req.getSession().getAttribute("photo_id")));
       // int test = (int) req.getSession().getAttribute("photo_id");
        //req.getSession().getAttribute("photo_id");
        resp.sendRedirect( req.getContextPath() + "/candidates.do");
    }
}
