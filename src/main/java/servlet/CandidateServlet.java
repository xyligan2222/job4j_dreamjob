package servlet;

import model.Candidate;
import model.City;
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
        req.setAttribute("city", PsqlStore.instOf().findAllCity());
        req.getRequestDispatcher("/candidate/candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        System.out.println("ID");
        System.out.println(req.getParameter("cityId"));
        Candidate candidate = new Candidate(Integer.parseInt(req.getParameter("id")),
                                            req.getParameter("name"),
                                            Integer.parseInt(req.getParameter("cityId")));
        PsqlStore.instOf().saveCandidate(candidate);
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }

}
