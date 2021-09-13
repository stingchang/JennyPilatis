package com.gym.pilatis;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HelperServlet
 */
@WebServlet(urlPatterns = { "" })
public class GymServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Router router = new Router();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GymServlet() {
        super();
    }

    @Override
    public void init() {
        try {
            // db.init();
            router.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        System.out.println(request.getRequestURL());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.append("Your string goes here");
        out.close();
//        HTTP http = new HTTP(request, response, getServletContext());
//        boolean routeSuccess = router.route(http);
//        if (!routeSuccess) {
//            // log
//        }
        return;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HTTP http = new HTTP(request, response, getServletContext());
        boolean routeSuccess = router.route(http);
        if (!routeSuccess) {
            // log
        }
        
        return;
    }

}
