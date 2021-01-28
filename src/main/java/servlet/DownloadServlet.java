package servlet;

import model.Photo;
import store.PsqlStore;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("photoId");
        System.out.println(req.getParameterNames());
        Photo photo = null;
        if (Integer.parseInt(id) != 0) {
             photo = PsqlStore.instOf().findByIdPhoto(Integer.parseInt(id));
        }
        if (photo != null) {
            resp.setContentType("name=" + photo.getName());
            resp.setContentType("image/png");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + photo.getName() + "\"");
            File file = new File("images" + File.separator + photo.getName());
            try (FileInputStream in = new FileInputStream(file)) {
                resp.getOutputStream().write(in.readAllBytes());
            }
        }
    }
}
