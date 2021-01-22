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
        req.setAttribute("candidates", PsqlStore.instOfCandidate().findAllCandidates());
        req.getRequestDispatcher("/candidate/candidates.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        PsqlStore.instOfCandidate().saveCandidate(new Candidate(Integer.parseInt(req.getParameter("id")), req.getParameter("name")));
        resp.sendRedirect( req.getContextPath() + "/candidates.do");
    }
}
