package com.lzz.aspp.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.lzz.aspp.vo.CarSourceFavoriteVO;
import com.lzz.aspp.vo.GoodsSourceFavoriteVO;
import com.lzz.lsp.base.qo.CarSourceQuery;
import com.lzz.lsp.base.qo.GoodsSourceQuery;

public interface UserFavoriteService {

	/**
	 * <p>获取收藏数量</p>
	 * @param queryObject
	 * @throws DataAccessException
	 * @author tangs 2015年8月10日
	 */
	public Integer getFavoritesCount(Integer userId,Integer infoType) throws Exception;
	
	/**
	 * <p>获取用户货源收藏列表</p>
	 * @param queryObject
	 * @return
	 * @throws Exception
	 * @author tangs 2015年8月10日
	 */
	public List<GoodsSourceFavoriteVO> getUserGoodsSourceFavorites(GoodsSourceQuery queryObject) throws Exception;
	
	/**
	 * <p>获取车源收藏列表</p>
	 * @param queryObject
	 * @return
	 * @throws Exception
	 * @author tangs 2015年8月10日
	 */
	public List<CarSourceFavoriteVO> getUserCarSourceFavorites(CarSourceQuery queryObject) throws Exception;
}
