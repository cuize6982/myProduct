package com.lzz.aspp.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.service.UserService;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.core.user.service.UserBaseService;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;

	@Override
	public void saveUserAndEUser(User user, Euser euser) throws Exception{
		userBaseService.updateEuser(euser);
		userBaseService.updateNickNameAndHeadImage(user);
	}
	
	
}
