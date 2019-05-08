package com.lib.web.user.main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.mail.Flags.Flag;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;

import com.lib.dto.JsonResult;
import com.lib.entity.UserInfo;
import com.lib.enums.Const;
import com.lib.exception.user.UserNoActiveException;
import com.lib.exception.user.UserNullAccountException;
import com.lib.exception.user.UserPasswordWrongException;
import com.lib.service.user.UserRegisterService;
import com.lib.service.user.UserService;
import com.lib.utils.NetworkUtil;

/**
 * 前台登录
 * 
 * @author l
 *
 */
@Controller
public class LoginAndRegisterController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRegisterService urService;
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	// @Autowired
	//
	/**
	 * 跳转到登录页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, HttpServletRequest request) {

		return "login";
	}

	/**
	 * 跳转到注册页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/registerUI", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("date", new Date());
		return "register/register";
	}

	@RequestMapping(value = "/login-submit", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	public @ResponseBody JsonResult loginSub(UserInfo user, HttpSession session,HttpServletRequest req) throws IOException {
		JsonResult<String> result = null;
		UserInfo userBasicInfo = null;
		try {
			userService.checkUserByEmail(user);
			result = new JsonResult(true, null);
			// 在session中保存用户基本信息
			userBasicInfo = userService.getBasicUserInfoByEmail(user.getUserEmail());
			session.setAttribute(Const.SESSION_USER, userBasicInfo);

		} catch (UserNullAccountException e) {
			result = new JsonResult(false, e.getMessage());
		} catch (UserPasswordWrongException e2) {
			result = new JsonResult(false, e2.getMessage());
		} catch (UserNoActiveException e3) {
			result = new JsonResult(false, e3.getMessage());
		}
		
		try {
			System.out.println(userBasicInfo.getUserEmail()+"在 ip:"+NetworkUtil.getIpAddress(req)+" 登录");
			LOG.info(userBasicInfo.getUserEmail()+"在 ip:"+NetworkUtil.getIpAddress(req)+" 登录");
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 退出
	 * 
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(Model model, HttpSession session) {
		session.invalidate();
		model.addAttribute("date", new Date());
		return "login";
	}

	@RequestMapping(value = "/testLogin", method = RequestMethod.GET)
	public @ResponseBody UserInfo test() {
		UserInfo user = userService.getUserById(2016001l);
		// JsonResult<UserInfo> result = new JsonResult<UserInfo>(true, user);
		return user;
	}

	@RequestMapping(value = "/illegal-view", method = RequestMethod.GET)
	public String illegalView(Model model, HttpServletRequest request) {
		model.addAttribute("message", "无权访问，请先登录！");
		return "message";
	}

	@RequestMapping(value = "/illegal-notConfirm", method = RequestMethod.GET)
	public String notConfirm(Model model, HttpServletRequest request) {
		model.addAttribute("message", "帐号未激活！");
		return "message";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws ParseException
	 * @throws MalformedURLException 
	 */
	@RequestMapping(value = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String load(String userName, String password, HttpServletRequest request, HttpServletResponse response,
			Model model) throws ParseException, MalformedURLException {
		String url = request.getRequestURL().toString();
		URL u = new URL(url);
		String userPassword = password;
		String action = request.getParameter("action");
		if ("register".equals(action)) {
			// 注册
			String email = request.getParameter("email");
			urService.processregister(userName, userPassword, email,u.getHost()+":"+u.getPort());// 发邮箱激活
			email = email.substring(email.lastIndexOf("@")+1,email.length());
			return "redirect:register-success?host="+email;
		} else if ("activate".equals(action)) {
			// System.out.println(request.getLocalAddr());
			// 激活
			String email = request.getParameter("email");// 获取email
			String validateCode = request.getParameter("validateCode");// 激活码
			try {
				urService.processActivate(email, validateCode);// 调用激活方法
				return "register/activate-success";
			} catch (Exception e) {
				request.setAttribute("message", "帐号已激活或失败，请联系管理员");
				return "message";
			}
		}
		return "register-success";
	}

	@RequestMapping(value = "/register-success", method = RequestMethod.GET)
	public String toSuccessPage(Model model,String host){
		return "register/register-success";
	}
	/**
	 * 注册校验
	 * 
	 * @param user
	 * @param repassword
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String checkRegister(UserInfo user, String repassword, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String result = null;
		if (user.getUserEmail() != null) {
			String regex = "^[\\w-]+(\\.[\\w-]+)*\\@([\\.\\w-]+)+$";
			boolean flg = Pattern.matches(regex, user.getUserEmail());
			if (flg) {
				if (userService.checkByEmail(user.getUserEmail())) {
					result = "<font class='am-btn-success'>该邮箱未被注册</font>";
				} else {
					result = "<font  class='am-btn-danger'>该邮箱已被注册</font>";
				}
			} else {
				result = "<font  class='am-btn-danger'>邮箱格式不正确</font>";
			}
		}
		if (user.getUserPassword() != null || user.getUserPassword() != null && repassword != null) {
			String regex = "^\\w{5,12}$";
			boolean flg = Pattern.matches(regex, user.getUserPassword());
			if (flg) {
				result = "<font class='am-btn-success'>正常</font>";
			} else {
				result = "<font  class='am-btn-danger'>密码必须5-12个字符组成 </font>";
			}
		} 
		if (user.getUserPassword() != null && repassword != null) {
			String strConfirm = new String(user.getUserPassword());
			String strPwd = new String(repassword);

			if (strConfirm.equals(strPwd)) {
				result = "<font class='am-btn-success'>密码一致</font>";
			} else {
				result = "<font  class='am-btn-danger'>两次密码输入不一致，请重新输入</font>";
			}
		}
		if (user.getUserName() != null) {
			result = "<font class='am-btn-success'>正常</font>";
		}
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().print(result);
		return null;
	}
}
