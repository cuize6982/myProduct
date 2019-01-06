package com.lzz.aspp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.lzz.aspp.vo.CarSourceFavoriteVO;
import com.lzz.aspp.vo.GoodsSourceFavoriteVO;
import com.lzz.lsp.base.qo.CarSourceQuery;
import com.lzz.lsp.base.qo.GoodsSourceQuery;

public interface UserFavoriteDAO {
	
	/**
	 * <p>查询收藏数量</p>
	 * @param queryObject
	 * @throws DataAccessException
	 * @author tangs 2015年8月10日
	 */
	public Integer queryFavoritesCount(@Param("userId") Integer userId,@Param("infoType") Integer infoType) throws DataAccessException;
	/**
	 * <p>查询用户货源收藏列表</p>
	 * @param queryObject
	 * @throws DataAccessException
	 * @author tangs 2015年8月10日
	 */
	public List<GoodsSourceFavoriteVO> queryUserGoodsSourceFavorites(GoodsSourceQuery queryObject) throws DataAccessException;
	
	
	/**
	 * <p>查询用户车源收藏列表</p>
	 * @param queryObject
	 * @throws DataAccessException
	 * @author tangs 2015年8月10日
	 */
	public List<CarSourceFavoriteVO> queryUserCarSourceFavorites(CarSourceQuery queryObject) throws DataAccessException;
}
