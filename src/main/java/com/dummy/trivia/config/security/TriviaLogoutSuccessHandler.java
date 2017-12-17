package com.dummy.trivia.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TriviaLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        httpServletResponse.getWriter().print("{\"code\":0,\"message\":\"已注销\"}");
        httpServletResponse.getWriter().flush();
    }
}
