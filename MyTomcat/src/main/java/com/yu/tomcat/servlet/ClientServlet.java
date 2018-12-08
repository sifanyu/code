package com.yu.tomcat.servlet;

import com.yu.tomcat.http.MyRequest;
import com.yu.tomcat.http.MyResponse;
import com.yu.tomcat.http.MyServlet;

public class ClientServlet extends MyServlet {

    @Override
    public void doGet(MyRequest request, MyResponse response) {
        try {
            response.write(request.getParameter("name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(MyRequest request, MyResponse response) {
        doGet(request, response);
    }
}
