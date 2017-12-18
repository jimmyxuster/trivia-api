package com.dummy.trivia.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationUtil {

    public static Authentication getCurrentUserAuthentication(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request
                .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        return securityContextImpl == null ? null : securityContextImpl.getAuthentication();
    }
}
