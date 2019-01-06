package com.lzz.aspp.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.dao.UserFavoriteDAO;
import com.lzz.aspp.service.UserFavoriteService;
import com.lzz.aspp.vo.CarSourceFavoriteVO;
import com.lzz.aspp.vo.GoodsSourceFavoriteVO;
import com.lzz.lsp.base.qo.CarSourceQuery;
import com.lzz.lsp.base.qo.GoodsSourceQuery;

@Service("userFavoriteService")
public class UserFavoriteServiceImpl implements UserFavoriteService{

	@Resource(name = "userFavoriteDAO")
	private UserFavoriteDAO userFavoriteDAO;
	
	@Override
	public Integer getFavoritesCount(Integer userId,Integer infoType) throws Exception{
		return userFavoriteDAO.queryFavoritesCount(userId,infoType);
	}
	
	@Override
	public List<GoodsSourceFavoriteVO> getUserGoodsSourceFavorites(GoodsSourceQuery queryObject) throws Exception {
		return userFavoriteDAO.queryUserGoodsSourceFavorites(queryObject);
	}

	@Override
	public List<CarSourceFavoriteVO> getUserCarSourceFavorites(CarSourceQuery queryObject) throws Exception {
		return userFavoriteDAO.queryUserCarSourceFavorites(queryObject);
	}
}
