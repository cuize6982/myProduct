package com.lzz.aspp.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.google.gson.Gson;
import com.lzz.aspp.service.UserAuthService;
import com.lzz.aspp.util.CommonUtil;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserAuthRec;
import com.lzz.lsp.core.attachment.service.AttachmentBaseService;
import com.lzz.lsp.core.auth.service.AuthBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.psi.util.PsiAttachmentUtil;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.util.HttpUtils;

/**
 * <p>用户认证controller</p>
 * @author Liuyl 2015年8月12日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/user/auth")
public class UserAuthController {

	
	Logger logger = Logger.getLogger(UserAuthController.class);
	
	Gson g = new Gson();
	
	@Resource( name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource( name = "userAuthService")
	private UserAuthService userAuthService;

	@Resource( name = "authBaseService")
	private AuthBaseService authBaseService;
	
	@Resource(name = "attachmentBaseService")
	private AttachmentBaseService attachmentBaseService;
	
	/**
	 * <p>申请认证</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月12日
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/applyAuth.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> applyAuth(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 校验
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			CommonUtils.checkParams("enterpriseName", paramsMap.get("enterpriseName"), model);
			CommonUtils.checkParams("userId", paramsMap.get("userId"), model);
			CommonUtils.checkParams("corporation", paramsMap.get("corporation"), model);
			CommonUtils.checkParams("idcardNo", paramsMap.get("idcardNo"), model);
			CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			User user = userBaseService.getUser(Integer.valueOf(paramsMap.get("userId").toString()));
			logger.info("user enter applyAuth----userId="+paramsMap.get("userId"));
			if(null != user){
				logger.info("user="+g.toJson(user));
				Euser euser = userBaseService.getEuser(user.getUserId());
				euser.setEnterpriseName(paramsMap.get("enterpriseName").toString());
				euser.setCorporation(paramsMap.get("corporation").toString());
				euser.setIdcardNo(paramsMap.get("idcardNo").toString());
				JSONObject jsonObject = PsiAttachmentUtil.uploalImgs(request, paramsMap.get("loginUserName").toString(), true, false, null);
				if(jsonObject != null){
					Iterator keys = jsonObject.keys();
					while (keys.hasNext()) { 
						String key = keys.next().toString();
						String value = jsonObject.getString(key);
						String[] temp = value.split("\\.");
						if(temp[0].equals("IDCARD")){
							euser.setIdcardPhoto(key);
						} else if(temp[0].equals("LICENSE")) {
							euser.setLicensePhoto(key);
						} else if(temp[0].equals("ENTERPRISE_PHOTO")){
							euser.setEnterprisePhoto(key);
						}
					}
				}
				
				euser.setUpdateUser(paramsMap.get("loginUserName").toString());
				euser.setUpdateTime(new Date());
				
				boolean  isReturnPrevious = false;
				// 设置 userAuthRec
				UserAuthRec authRec = authBaseService.getUserLastAuthRec(user.getUserId());
				if(authRec!=null && (authRec.getAuthStatus() == DataConstants.AUTH_STATUS_WRITE_INFO || authRec.getAuthStatus() == DataConstants.AUTH_STATUS_SUBMIT)){
					authRec.setUserId(euser.getUserId());
					authRec.setUpdateUser(paramsMap.get("loginUserName").toString());
					authRec.setUpdateTime(new Date());
					authRec.setAuthType(DataConstants.ENTERPRISE_AUTH);//企业认证
					authRec.setAuthNodeNo(DataConstants.AUTH_NODE_SECOND);//节点2
					authRec.setAuthStatus(DataConstants.AUTH_STATUS_SUBMIT);//提交认证
					isReturnPrevious = true;
				}else{
					authRec = new UserAuthRec();
					authRec.setUserId(euser.getUserId());
					authRec.setCreateUser(paramsMap.get("loginUserName").toString());
					authRec.setCreateTime(new Date());
					authRec.setAuthType(DataConstants.ENTERPRISE_AUTH);//企业认证
					authRec.setAuthNodeNo(DataConstants.AUTH_NODE_SECOND);//节点2
					authRec.setAuthStatus(DataConstants.AUTH_STATUS_SUBMIT);//提交认证
				}
				userAuthService.saveUserAuthInfo(euser, authRec, user, isReturnPrevious);
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("authStatus", DataConstants.AUTH_STATUS_SUBMIT);
		} catch (Exception e) {
			e.printStackTrace();
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
		}
		return model;
	}
	
	/**
	 * <p>获取认证信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月12日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/getAuthInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAuthInfo(String accessToken, Integer userId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 验证访问凭证
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			// 查询数据
			Euser euser = userBaseService.getEuser(userId);
			// 返回数据
			UserAuthRec authRec = authBaseService.getUserLastAuthRec(userId);
			Integer authStatus = null == authBaseService.getUserLastAuthRec(userId) ? 0 : authBaseService.getUserLastAuthRec(userId).getAuthStatus();
			if(DataConstants.AUTH_STATUS_PASS == authStatus){
				euser.setIdcardNo(CommonUtil.replaceIdCarNO(euser.getIdcardNo()));
			}
			if(authRec!=null){
				if(authRec.getAuthStatus()==3){
					model.put("authMark", authRec.getMark());
				}else{
					model.put("authMark", null);
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("authStatus", authStatus);
			model.put("userInfo", euser);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	public static void main(String[] args) {
		try {
			String str = HttpUtils.sendHttpRequest("http://localhost:8090/aspp-server/user/auth/getAuthInfo.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&userId=22", com.lzz.lton.util.HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
