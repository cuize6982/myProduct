package com.lzz.aspp.service;

import java.util.List;
import java.util.Map;

import com.lzz.aspp.vo.AsppGoodsSourceVO;

/**
 * <p>查询货源Service</p>
 * @author Liuyl 2015年8月11日
 * @version 1.0
 * <p>注意事项： </p>
 */
public interface GoodsSourceService {

	/**
	 * <p>发布的货源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月11日
	 * @param vo
	 * @return
	 */
	public List<AsppGoodsSourceVO> getPublishGoodsSource(AsppGoodsSourceVO vo);
	
	/**
	 * <p>货源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月13日
	 * @param vo
	 * @return
	 */
	public List<AsppGoodsSourceVO> goodsSourceList(AsppGoodsSourceVO vo);
	
	/**
	 * 获取货源是否能收藏
	 * @param map
	 * @return
	 */
	public List<AsppGoodsSourceVO> getGoodsIsFavorite(Map<String, Object> map);
	
	/**
	 * 
	 * <p>获取货源详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月28日
	 * @param map
	 * @return
	 */
	public AsppGoodsSourceVO getGoodsSourceInfo(Map<String, Object> map);
}
