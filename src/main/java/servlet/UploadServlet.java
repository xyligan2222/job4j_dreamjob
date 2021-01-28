package servlet;


import model.Candidate;
import model.Photo;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import store.PsqlStore;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(UploadServlet.class.getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> images = new ArrayList<>();
        String redirect;
         String test = req.getParameter("photoId");
        if (req.getParameter("photoId") == null) {
            for (File name : new File("images").listFiles()) {
                images.add(name.getName());
            }
            req.setAttribute("images", images);
            redirect = "upload/upload.jsp";
            LOG.info(redirect);
        } else {
            redirect = "/candidate/edit.jsp";
            LOG.info(redirect);
        }
        //req.setAttribute("photo_id", PsqlStore.instOfPhoto().savePhoto());
        RequestDispatcher dispatcher = req.getRequestDispatcher(redirect);
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        System.out.println((String) req.getAttribute("candidateId"));
        Photo photo = null;
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("images");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                photo = PsqlStore.instOf().savePhoto(new Photo(item.getName()));
                Candidate candidate = PsqlStore.instOf().findByIdCandidate(
                        Integer.parseInt(req.getParameter("candidateId")));
                candidate.setPhoto_id(photo.getId());
                PsqlStore.instOf().updateCandidateWithPhoto(candidate);
                if (!item.isFormField()) {
                    File file = new File(folder + File.separator + item.getName());
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
//        if (photo != null) {
//            req.setAttribute("photo_id", String.valueOf(photo.getId()));
//
//        }
       doGet(req, resp);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/candidate/candidates.jsp");
       // dispatcher.include(req,resp);
       dispatcher.forward(req, resp);
    }
}
