package servlet;

import com.google.gson.Gson;
import model.City;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CitiesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        List<City> cities = new ArrayList<>(PsqlStore.instOf().findAllCity());
        String gson = new Gson().toJson(cities);
        PrintWriter printWriter = new PrintWriter(resp.getOutputStream(), true, StandardCharsets.UTF_8);
        printWriter.println(gson);
        printWriter.flush();
    }
}
