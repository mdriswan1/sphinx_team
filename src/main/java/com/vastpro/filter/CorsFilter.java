package com.vastpro.filter;


import javax.servlet.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        if (((HttpServletRequest) req).getMethod().equalsIgnoreCase("OPTIONS")) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }
        chain.doFilter(req, res);
    }
    
}