//package com.vastpro.filter;
//
//import java.io.IOException;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * This class is used to allow cors request
// */
//public class CorsFilter implements Filter {
//	/**
//	 * Method is used to handle request and add cors headers
//	 * 
//	 * @param req
//	 *            incoming request
//	 * @param res
//	 *            response
//	 * @param chain
//	 *            next filter
//	 */
//	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//		HttpServletRequest request = (HttpServletRequest) req;
//		HttpServletResponse response = (HttpServletResponse) res;
//
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//		response.setHeader("Access-Control-Allow-Headers", "*");
//
//		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//			response.setStatus(HttpServletResponse.SC_OK);
//			return;
//		}
//		chain.doFilter(request, response);
//
//	}
//}

package com.vastpro.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

	// public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	//
	// HttpServletRequest request = (HttpServletRequest) req;
	// HttpServletResponse response = (HttpServletResponse) res;
	//
	// response.setHeader("Access-Control-Allow-Origin", "*");
	// response.setHeader("Access-Control-Allow-Credentials", "true");
	// response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	// response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
	//
	// // important for preflight
	// if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
	// response.setStatus(HttpServletResponse.SC_OK);
	// return;
	// }
	//
	// chain.doFilter(request, response);
	// }

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String origin = request.getHeader("Origin");

		if ("http://localhost:3000".equals(origin)) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		}

		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		chain.doFilter(request, response);
	}
}