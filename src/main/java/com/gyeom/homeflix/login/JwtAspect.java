package com.gyeom.homeflix.login;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;


@Component
@Aspect
public class JwtAspect {

    private static final Set<String> authExcludedScreenUrls = new HashSet<>(){
        {
            add("/login"); add("/login/fail");
        }
    };

    private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());


    //PointCut: @Around
    //JointPoint: execution()에 정의되는 위치
    //Advide: 구현 내용
    @Around(value = "execution(public org.springframework.web.servlet.ModelAndView com.gyeom.homeflix.ScreenController.*(..)) " +
            "&& @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object handleNotAuthScreen(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        boolean isRequiredLogin = false;
        for(Object arg : args){
            if(arg instanceof HttpServletRequest){
                HttpServletRequest request = (HttpServletRequest) arg;
                if(authExcludedScreenUrls.contains(request.getServletPath())) break;
                if(request.getAttribute(JwtProperties.REQUIERED_LOGIN) != null){
                    isRequiredLogin = true;
                    break;
                }
            }
        }

        if(isRequiredLogin){
            ModelAndView mv = new ModelAndView();
            mv.setViewName("login.html");
            mv.addObject("fail", true);
            return mv;
        }

        return pjp.proceed();
    }
}
