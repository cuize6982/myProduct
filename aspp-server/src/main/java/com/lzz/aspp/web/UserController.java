package com.lzz.aspp.web;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.lzz.aspp.service.UserFavoriteService;
import com.lzz.aspp.service.UserService;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.FeedBack;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserAuthRec;
import com.lzz.lsp.cmpt.district.service.DistrictService;
import com.lzz.lsp.core.attachment.service.AttachmentBaseService;
import com.lzz.lsp.core.auth.service.AuthBaseService;
import com.lzz.lsp.core.favorite.service.FavoriteBaseService;
import com.lzz.lsp.core.feedback.service.FeedBackBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.psi.util.PsiAttachmentUtil;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;
import com.lzz.lton.util.StringUtils;
import com.lzz.ssmp.client.SSMPClient;
import com.lzz.ssmp.client.constant.ReturnCodes;

/**
 * <p>用户controller</p>
 * @author Liuyl 2015年8月5日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	@Resource(name = "ssmpClient")
	private SSMPClient ssmpClient;

	@Resource(name = "districtService")
	private DistrictService districtService;
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "favoriteBaseService")
	private FavoriteBaseService favoriteBaseService;
	
	@Resource(name = "attachmentBaseService")
	private AttachmentBaseService attachmentBaseService;

	@Resource(name = "userService")
	private UserService userService;
	
	@Resource(name = "feedBackBaseService")
	private FeedBackBaseService feedBackBaseService;
	
	@Resource(name = "authBaseService")
	private AuthBaseService authBaseService;
	
	@Resource(name = "userFavoriteService")
	private UserFavoriteService userFavoriteService;
	/**
	 * <p>忘记密码</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/forgetPwd.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> forgetPwd(HttpServletRequest request){
		Map<String, Object>	model = new HashMap<String, Object>();
		try {
			// 获取参数集合
			Map<String, Object> params = WebUtils.getParametersStartingWith(request, null);
			// 检验令牌
			CommonUtils.checkAccessToken(params.get("accessToken"), model);
			// 用户登录名
			String phoneNum = CommonUtils.checkParams("phoneNum", params.get("phoneNum"), model);
			// 密码
			String pwdWord = CommonUtils.checkParams("pwdWord", params.get("pwdWord"), model);
			// 验证码
			String valiDateCode = CommonUtils.checkParams("valiDateCode", params.get("valiDateCode"), model);
			
			if(CommonUtils.checkValidCode(request, model, valiDateCode, phoneNum)){
				int isUpdatePwd = ssmpClient.updateAccountPassword(phoneNum, pwdWord, ssmpClient.getAccessToken());
				if(0 != isUpdatePwd || isUpdatePwd == ReturnDatas.ACCOUNT_NAME_NOT_EXIST){
					model.put("code", ReturnDatas.ACCOUNT_NAME_NOT_EXIST);
					model.put("msg", ReturnDatas.ACCOUNT_NAME_NOT_EXIST_MSG);
					return model;
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * <p>注册成功后补全信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月10日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/registerAfterInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> registerAfterInfo(HttpServletRequest request){
		Map<String, Object>	model = new HashMap<String, Object>();
		try {
			// 获取参数集合
			Map<String, Object> params = WebUtils.getParametersStartingWith(request, null);
			// 检验令牌
			CommonUtils.checkAccessToken(params.get("accessToken"), model);
			CommonUtils.checkParams("userId", params.get("userId"), model);
			CommonUtils.checkParams("enterpriseName", params.get("enterpriseName"), model);
			CommonUtils.checkParams("belongArea", params.get("belongArea"), model);
			CommonUtils.checkParams("belongPCode", params.get("belongPCode"), model);
			//排除4个直辖市
			String  arr[]  =  {"110000","120000","310000","500000"};
			boolean contains = Arrays.asList(arr).contains(params.get("belongPCode").toString());
			if(!contains){//排除直辖市
				CommonUtils.checkParams("belongCCode", params.get("belongCCode"), model);
			}
			CommonUtils.checkParams("linkMan", params.get("linkMan"), model);
			CommonUtils.checkParams("method", params.get("method"), model);
			Euser euser = userBaseService.getEuser(Integer.valueOf(params.get("userId").toString()));
			if(null != euser){
				euser.setEnterpriseName(params.get("enterpriseName").toString());
				euser.setLinkman(params.get("linkMan").toString());
				euser.setBelongArea(params.get("belongArea").toString());
				euser.setBelongPCode(Integer.valueOf(params.get("belongPCode").toString()));
				if(!contains){//排除直辖市
					euser.setBelongCCode(Integer.valueOf(params.get("belongCCode").toString()));
				}
				if(null != params.get("belongTCode") && !StringUtils.isBlank(params.get("belongTCode").toString())){
					euser.setBelongTCode(Integer.valueOf(params.get("belongTCode").toString()));
				}
				userBaseService.updateEuser(euser);
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("linkMan", euser.getLinkman());
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * <p>获取我的信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月12日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/getMyInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getMyInfo(String accessToken, Integer userId){
		Map<String, Object>	model = new HashMap<String, Object>();
		try {
			// 检验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			Euser euser = userBaseService.getEuser(userId);
			User user = userBaseService.getUser(userId);
			if(null == user || null == euser){
				model.put("code", ReturnDatas.ACCOUNT_NAME_NOT_EXIST);
				model.put("msg", ReturnDatas.ACCOUNT_NAME_NOT_EXIST_MSG);
				return model;
			}
			Map<String, Object> userInfo = new HashMap<String, Object>();
			userInfo.put("enterpriseName", euser.getEnterpriseName());
			userInfo.put("linkMan", euser.getLinkman());
			userInfo.put("nickName", user.getNickName());
			userInfo.put("money", user.getRemainMoney());
			userInfo.put("isAuth", null == authBaseService.getUserLastAuthRec(userId) ? 0 : authBaseService.getUserLastAuthRec(userId).getAuthStatus());
			userInfo.put("headImage", user.getHeadImage());
			Integer favoritesCarCount = userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_CAR_SOURCE);
			Integer favoritesGoodsCount = userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_GOODS_SOURCE);
			userInfo.put("favoriteCount", (null != favoritesGoodsCount ? favoritesGoodsCount : 0) + (null != favoritesCarCount ? favoritesCarCount : 0));
			userInfo.put("linkMobile1", euser.getLinkMobile1());
			userInfo.put("linkMobile2", euser.getLinkMobile2());
			userInfo.put("linkPhone", euser.getLinkPhone());
			userInfo.put("address", euser.getAddress());
			// 账户积分
		    //long currentUserScore =	Long.valueOf(Long.valueOf(user.getScore()) - Long.valueOf(user.getUseScore()));
			Integer useScore = 0;
			if(user.getUseScore() != null){
				useScore = user.getUseScore();
			}
		    Integer currentUserScore = user.getScore() - useScore;
		    userInfo.put("score", currentUserScore);
		    //是否代理
		    userInfo.put("isAgent", user.getIsAgent());
//		    userInfo.put("isAgent", 0);
		    //会员等级
		    userInfo.put("userGrad", user.getUserGrad());
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("userInfo", userInfo);
			UserAuthRec authRec = authBaseService.getUserLastAuthRec(userId);
			if(authRec != null && authRec.getAuthStatus()==3){
				userInfo.put("authMark", authRec.getMark());
			}else{
				userInfo.put("authMark", null);
			}
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * <p>修改用户基本信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月12日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateUserInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateUserInfo(HttpServletRequest request){
		Map<String, Object>	model = new HashMap<String, Object>();
		try {
			// 获取参数集合
			Map<String, Object> params = WebUtils.getParametersStartingWith(request, null);
			
			// 准备返回参数
			Map<String, Object> map = new HashMap<String, Object>();
			// 检验令牌
			CommonUtils.checkAccessToken(params.get("accessToken"), model);
			CommonUtils.checkParams("userId", params.get("userId"), model);
			CommonUtils.checkParams("enterpriseName", params.get("enterpriseName"), model);
			CommonUtils.checkParams("linkMan", params.get("linkMan"), model);
			CommonUtils.checkParams("linkMobile1", params.get("linkMobile1"), model);
			CommonUtils.checkParams("loginUserName", params.get("loginUserName"), model);
			Euser euser = userBaseService.getEuser(Integer.valueOf(params.get("userId").toString()));
			User user = userBaseService.getUser(Integer.valueOf(params.get("userId").toString()));
			if(null != euser && null != user){
				euser.setEnterpriseName(params.get("enterpriseName").toString());
				euser.setLinkman(params.get("linkMan").toString());
				euser.setLinkMobile1(params.get("linkMobile1").toString());
				if(null != params.get("linkMobile2")){
					euser.setLinkMobile2(params.get("linkMobile2").toString());
				}
				if(null != params.get("linkPhone")){
					euser.setLinkPhone(params.get("linkPhone").toString());
				}
				if(null != params.get("address")){
					euser.setAddress(params.get("address").toString());
				}
				String img = PsiAttachmentUtil.uploadImg(request, params.get("loginUserName").toString(), false, false, "HEAD");
				euser.setHeadImage(!StringUtils.isBlank(img) ? img : null);
				euser.setUpdateUser(params.get("loginUserName").toString());
				euser.setUpdateTime(new Date());
				user.setNickName(euser.getLinkman());
				user.setHeadImage(!StringUtils.isBlank(img) ? img : null);
				user.setUpdateUser(params.get("loginUserName").toString());
				user.setUpdateTime(new Date());
				userService.saveUserAndEUser(user, euser);
				
				map.put("enterpriseName", euser.getEnterpriseName());
				map.put("linkman", euser.getLinkman());
				map.put("linkPhone", euser.getLinkPhone());
				map.put("linkMobile1", euser.getLinkMobile1());
				map.put("linkMobile2", euser.getLinkMobile2());
				map.put("address", euser.getAddress());
				map.put("address", user.getHeadImage());
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("myInfo", map);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	@RequestMapping(value = "/feedBack.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> feedBack(HttpServletRequest request){
		Map<String, Object>	model = new HashMap<String, Object>();
		try {
			// 获取参数集合
			Map<String, Object> params = WebUtils.getParametersStartingWith(request, null);
			// 检验令牌
			CommonUtils.checkAccessToken(params.get("accessToken"), model);
			// 检验参数
			CommonUtils.checkParams("loginUserName", params.get("loginUserName"), model);
			// 保存数据
			String notNullStr = "userId,userMobile,opinion,source";
			
			FeedBack feedBack = (FeedBack) CommonUtils.checkBean(JsonUtils.mapToJson(params), FeedBack.class, notNullStr, model);
			feedBack.setStatus(DataConstants.FEED_BACK_NOT_HANDLE);
			feedBack.setCreateUser(params.get("loginUserName").toString());
			feedBack.setCreateTime(new Date());
			feedBackBaseService.saveFeedBack(feedBack);
			// 返回数据
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	@RequestMapping(value = "/updatePwd.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updatePwd(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken").toString(), model);
			// 验证参数 
			CommonUtils.checkParams("accountName", paramsMap.get("accountName"), model);
			CommonUtils.checkParams("oldPwd", paramsMap.get("oldPwd"), model);
			CommonUtils.checkParams("newPwd", paramsMap.get("newPwd"), model);
			int success = ssmpClient.updatePwd(paramsMap.get("accountName").toString(),
					paramsMap.get("oldPwd").toString(), 
					paramsMap.get("newPwd").toString(), 
					ssmpClient.getAccessToken());
			// 旧密码是否匹配
			if(success == ReturnCodes.OLD_PASSWORD_INCORRECT){
				model.put("code", ReturnDatas.OLDPWD_NOT_SAME);
				model.put("msg", ReturnDatas.OLDPWD_NOT_SAME_MSG);
				return model;
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
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
			String url = "http://api.open.qingting.fm/v6/media/programs/"+1234+"?access_token="+"12345678999999";
			String str = HttpUtils.sendHttpRequest(url, HttpUtils.RequestMethod.GET,null);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
