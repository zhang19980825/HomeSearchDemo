package com.zhangyang.security;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.zhangyang.base.LoginUserUtil;
import com.zhangyang.entity.User;
import com.zhangyang.service.IUserSerivce;
import com.zhangyang.service.user.ISmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/31 16:00
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    private IUserSerivce userSerivce;

    @Autowired
    private ISmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String name=obtainUsername(request);
        if(!Strings.isNullOrEmpty(name)){
            request.setAttribute("username",name);
            return super.attemptAuthentication(request,response);
        }

        String telephone = request.getParameter("telephone");
        if(Strings.isNullOrEmpty(telephone)||!LoginUserUtil.checkTelephone(telephone)){
            throw new BadCredentialsException("wrong telephone number");
        }

        User user = userSerivce.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode=smsService.getSmsCode(telephone);
        if(Objects.equal(inputCode,sessionCode)){
            if(user==null){
                //如果用户第一次用手机登录 则自动注册改用户
                user=userSerivce.addUserByPhone(telephone);
            }
            return new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        }else {
            throw  new BadCredentialsException("smsCodeError");
        }
    }
}
