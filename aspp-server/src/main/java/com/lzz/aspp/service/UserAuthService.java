package com.lzz.aspp.service;

import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserAuthRec;

/**
 * <p>用户认证service</p>
 * @author Liuyl 2015年8月12日
 * @version 1.0
 * <p>注意事项： </p>
 */
public interface UserAuthService {

	/**
	 * <p>保存用户认证信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月12日
	 * @param euser
	 * @param authRec
	 * @throws Exception
	 */
	public void saveUserAuthInfo(Euser euser, UserAuthRec authRec, User user, boolean isAdd)throws Exception;
}
