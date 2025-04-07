package com.mycompany.tsms;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        
        if (isPublicResource(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        
        if (session != null && session.getAttribute("username") != null) {
            String role = (String) session.getAttribute("role");
            
            if ("admin".equals(role)) {
                if (requestURI.contains("/user_")) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin_profile.html");
                    return;
                }
            } else {
                
                if (requestURI.contains("/admin_")) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/user_profile.html");
                    return;
                }
            }
            
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.html");
        }
    }
    
    private boolean isPublicResource(String uri) {
        
        if (uri.endsWith(".css") || 
            uri.endsWith(".js") || 
            uri.endsWith(".png") || 
            uri.endsWith(".jpg") || 
            uri.endsWith(".gif")) {
            return true;
        }
        
        
        if (uri.endsWith("login.html") || 
            uri.contains("ChkLoginServlet") || 
            uri.contains("RegisterServlet")) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void destroy() {}
} 