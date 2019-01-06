package com.lzz.aspp.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.lzz.aspp.vo.AsppCarSourceVO;
import com.lzz.lsp.base.vo.CarSourceVO;
import com.lzz.lsp.base.vo.CarVO;
/**
 * 
 * <p>车源查询VO</p>
 * @author Liuyl 2015年5月15日
 * @version 1.0
 * <p>注意事项： </p>
 */
public interface CarSourceDAO {
	
	/**
	 * <p>根据人员id、车源类型获取车源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年5月15日
	 * @param userId	用户id
	 * @param carSourceType	车源类型
	 * @return
	 */
	public List<AsppCarSourceVO> getCarSources(@Param("userId") Integer userId, 
			@Param("carSourceType") String carSourceType);
	
	/**
	 * 
	 * <p>根据人员id、车源类型获取可发布的车源</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年5月15日
	 * @param userId
	 * @param carSourceType
	 * @return
	 */
	public List<CarVO> getCanPublishCars(@Param("userId") Integer userId, 
			@Param("carSourceType") String carSourceType);
	
	/**
	 * <p>查询车源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月6日
	 * @param vo
	 * @return
	 */
	public List<AsppCarSourceVO> queryCarSources(AsppCarSourceVO vo);
	
	/**
	 * 获取车源是否能收藏
	 * @param map
	 * @return
	 */
	public List<AsppCarSourceVO> getCarSourceIsFavorite(Map<String, Object> map);
	
	/**
	 * 获取车源照片
	 * @param map
	 * @return
	 */
	public AsppCarSourceVO getCarSourcePhotos(Map<String, Object> map);
	
	/**
	 * 获取车源是否能收藏(新加车源详情列表)
	 * @param map
	 * @return
	 */
	public AsppCarSourceVO getCarSourceIsFavoriteByUserId(Integer userId,Integer infoId);
	
	/**
	 * 
	 * <p>获取车源详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月17日
	 * @param publishId
	 * @return
	 */
	public AsppCarSourceVO getCarSourceContent(Integer publishId);
	
	/**
	 * 
	 * <p>获取车源是否可以收藏</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月18日
	 * @param map
	 * @return
	 */
	public Integer getCarSourceFavoriteId(Map<String, Object> map);
}
