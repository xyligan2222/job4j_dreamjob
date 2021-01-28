package servlet;

import model.Candidate;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ImageServlet extends HttpServlet {
    private Candidate candidate;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("idChange",candidate);
        req.getRequestDispatcher("upload/uploadImage.jsp").forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        candidate = PsqlStore.instOf().findByIdCandidate(Integer.parseInt(req.getParameter("candidateId")));

        resp.sendRedirect( req.getContextPath() + "/image");
    }

}

