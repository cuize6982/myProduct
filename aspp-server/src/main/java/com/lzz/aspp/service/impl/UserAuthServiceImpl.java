package com.lzz.aspp.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.service.UserAuthService;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserAuthRec;
import com.lzz.lsp.core.attachment.service.AttachmentBaseService;
import com.lzz.lsp.core.auth.service.AuthBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lton.util.JsonUtils;

@Service("userAuthService")
public class UserAuthServiceImpl implements UserAuthService {

	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "authBaseService")
	private AuthBaseService authBaseService;
	
	@Resource(name = "attachmentBaseService")
	private AttachmentBaseService attachmentBaseService;
	
	
	@Override
	public void saveUserAuthInfo(Euser euser, UserAuthRec authRec, User user, boolean isAdd)
			throws Exception {
		//更新企业用户信息
		userBaseService.updateEuser(euser);
		authRec.setAuthContent(authDataSubmitProcess(user));
		if(isAdd){
			authBaseService.submitUserAuthRec(authRec);
		}else{
			authBaseService.saveAuthRec(authRec);
		}
	}

	private String authDataSubmitProcess(User user)throws Exception{
		Integer userId = user.getUserId();
		Integer userType = user.getUserType();
		String accountName = user.getAccountName();
		Euser euser = userBaseService.getEuser(userId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("accountName", accountName);
		params.put("userType", userType);
		params.put("enterpriseName", euser.getEnterpriseName());
		params.put("corporation", euser.getCorporation());
		params.put("linkman", euser.getLinkman());
		params.put("linkMobile1", euser.getLinkMobile1());
		params.put("linkPhone", euser.getLinkPhone());
		params.put("address", euser.getAddress());
		params.put("idcardNo",euser.getIdcardNo());
		params.put("idcardPhoto", euser.getIdcardPhoto());
		params.put("licensePhoto", euser.getLicensePhoto());
		params.put("qq",euser.getQq());
		params.put("belongArea", euser.getBelongArea());
		params.put("businessScope", euser.getBusinessScope());
		params.put("enterprisePhoto", euser.getEnterprisePhoto());
		return JsonUtils.mapToJson(params);
	}
}
