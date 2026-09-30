package com.example.guessword.web;

import jakarta.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("userId") == null) { res.sendRedirect(req.getContextPath() + "/login"); return false; }
        if (req.getRequestURI().startsWith(req.getContextPath() + "/admin") && !"ADMIN".equals(s.getAttribute("role"))) {
            res.sendRedirect(req.getContextPath() + "/dashboard"); return false;
        }
        return true;
    }
}
