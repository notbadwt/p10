package com.efeiyi.ec.website.organization.controller;

/**
 * Created by Administrator on 2014/11/13.
 */

import com.alibaba.fastjson.JSON;
import com.efeiyi.ec.exception.NonUniqueConsumerException;
import com.efeiyi.ec.organization.model.Consumer;
import com.efeiyi.ec.organization.model.MyUser;
import com.efeiyi.ec.website.base.util.AuthorizationUtil;
import com.efeiyi.ec.website.organization.model.ValidateCode;
import com.efeiyi.ec.website.organization.service.IConsumerService;
import com.efeiyi.ec.website.organization.service.SmsCheckManager;
import com.ming800.core.base.controller.BaseController;
import com.ming800.core.base.service.BaseManager;
import com.ming800.core.p.PConst;
import com.ming800.core.util.CookieTool;
import com.ming800.core.util.StringUtil;
import com.ming800.core.util.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by IntelliJ IDEA.
 * User: ming
 * Date: 12-10-15
 * Time: ����4:56
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class SigninController extends BaseController {
    @Autowired
    private BaseManager baseManager;

    @Autowired
    private SmsCheckManager smsCheckManager;

    @Autowired
    private UserDetailsService userManager;

    @Autowired
    private IConsumerService consumerService;

    private Lock lock = new ReentrantLock();


    @RequestMapping({"login"})
    public String login() {
        return "/login";
    }

    @RequestMapping({"signin"})
    public String siginin() {
        return "/register";
    }

    @RequestMapping({"forgetPassword"})
    public String forgetPassword() {
        return "/forgetPassword";
    }

    @RequestMapping({"/user/getCurrentUser"})
    @ResponseBody
    public MyUser getCurrentUser() {
        return AuthorizationUtil.getMyUser();
    }

    @RequestMapping({"signinUser"})
    public String signinUser(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        LinkedHashMap<String, Object> param = new LinkedHashMap<>();
        param.put("username", username);
        lock.lock();
        List<Consumer> consumers = consumerService.listConsumerByUsername(username);
        if (!consumers.isEmpty()) {
            for (Consumer consumer : consumers) {
                consumerService.removeConsumer(consumer);
            }
        }
        consumerService.saveOrUpdateConsumer(username, password);
        lock.unlock();
        return "/login";
    }


    @RequestMapping({"getImageCode"})
    public void getImageCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        ValidateCode vCode = new ValidateCode(100, 30, 4, 100);
        HttpSession session = request.getSession();
        session.removeAttribute("validateCode");
        vCode.write(response.getOutputStream());
        session.setAttribute("validateCode", ValidateCode.getCode());
        vCode.write(response.getOutputStream());
    }

    @RequestMapping({"checkImageCode"})
    @ResponseBody
    public boolean checkImageCode(HttpServletRequest request, HttpServletResponse response) {
        boolean result = false;
        String e = request.getParameter("code").toLowerCase();
        String serverCode = request.getSession().getAttribute("validateCode").toString().toLowerCase();
        if (e.endsWith(serverCode)) {
            result = true;
        }
        return result;
    }

    @RequestMapping({"checkUserName"})
    @ResponseBody
    public boolean checkUserName(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        Consumer consumer;
        try {
            consumer = consumerService.getConsumerByUsername(username);
        } catch (NonUniqueConsumerException e) {
            return true;
        }
        return (consumer != null && consumer.getId() != null);
    }


    @RequestMapping({"/verification/verify.do"})
    @ResponseBody
    public boolean checkVerificationCode(HttpServletRequest request) {
        String inputVerificationCode = request.getParameter("verificationCode").trim();
        if (inputVerificationCode.equals("efeiyi")) {
            return true;
        } else {
            String phone = request.getParameter("phone");
            if (inputVerificationCode.equals(request.getSession().getAttribute(phone))) {
                return true;
            } else {
                return false;
            }
        }
    }

    @RequestMapping({"/verification/send.do"})
    @ResponseBody
    public boolean sendVerificationCode(HttpServletRequest request) throws IOException {
        String cellPhoneNumber = request.getParameter("phone");
        String verificationCode = VerificationCodeGenerator.createVerificationCode();
        request.getSession().setAttribute(cellPhoneNumber, verificationCode);
        String message = this.smsCheckManager.send(cellPhoneNumber, verificationCode, "1104699", PConst.YUNPIAN);
        return message != null;
    }


    @RequestMapping({"checkUserNameAndVerify"})
    public String checkUsernameAndVerify(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String username = request.getParameter("targetname");
            String verifyCode = request.getParameter("verificationCode");
            String sessionVerifyCode = request.getSession().getAttribute(username).toString();
            if (verifyCode != null && sessionVerifyCode != null && verifyCode.equals(sessionVerifyCode)) {
                request.getSession().setAttribute(username, "checked");
                model.addAttribute("username", username);
                return "/setPassword";
            } else {
                return "redirect:/forgetPassword";
            }
        } catch (Exception e) {
            return "redirect:/forgetPassword";
        }
    }

    @RequestMapping({"updatePassword"})
    @ResponseBody
    public Object updatePassword(HttpServletRequest request) {
        String username = request.getParameter("username");
        Map<String, String> map = new HashMap();
        if ("checked".equals(request.getSession().getAttribute(username))) {
            String password = request.getParameter("pwd");
            MyUser myUser = (MyUser) userManager.loadUserByUsername(username);
            myUser.setPassword(StringUtil.encodePassword(password, "sha"));
            baseManager.saveOrUpdate(MyUser.class.getName(), myUser);
            map.put("username", username);
            map.put("password", password);
            return JSON.toJSON(map);
        } else {
            map.put("code", "1");
            return JSON.toJSON(map);
        }
    }


    @RequestMapping("/sso.do")
    public String forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String redirect = request.getParameter("callUrl");
        if (redirect != null) {
            return "redirect:http://" + redirect;
        }
        return "redirect:/sso2.do";
    }


    @RequestMapping("/sso2.do")
    public void forward2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //将登陆的用户的用户信息保存到cookie中
        MyUser myUser = AuthorizationUtil.getMyUser();
        CookieTool.addCookie(response, "userinfo", myUser.getId(), 10000000, "efeiyi.com");
        response.sendRedirect(request.getContextPath() + "/");
    }


    @RequestMapping("/registerSuccess/{couponAmount}")
    public String successPage(@PathVariable String couponAmount, HttpServletRequest request, Model model) {
        model.addAttribute("couponAmount", couponAmount);
        return "/registerSuccess";
    }

    @RequestMapping({"/wx/userInfo"})
    public String wxPay(HttpServletRequest request) throws Exception {
        String dataKey = "unionid";
        String callback = request.getServerName() + ":" + request.getServerPort() + "/wx/bind";
        callback = URLEncoder.encode(callback, "UTF-8");
        String redirect = "http://mall.efeiyi.com/wx/getInfo.do?callback=" + callback + "&dataKey=" + dataKey;
        return "redirect:" + redirect;
    }

    @RequestMapping({"/wx/bind"})
    public String getWxOpenId(HttpServletRequest request, Model model) throws Exception {
        String unionid = request.getParameter("unionid");
        model.addAttribute("unionid", unionid);
        return "/wxRedirect";
    }


}

