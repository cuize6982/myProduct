package com.lzz.aspp.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.dao.GoodsSourceDAO;
import com.lzz.aspp.service.GoodsSourceService;
import com.lzz.aspp.vo.AsppGoodsSourceVO;

@Service("goodsSourceService")
public class GoodsSourceServiceImpl implements GoodsSourceService {

	@Resource(name = "goodsSourceDAO")
	private GoodsSourceDAO goodsSourceDAO;
	@Override
	public List<AsppGoodsSourceVO> getPublishGoodsSource(AsppGoodsSourceVO vo) {
		return goodsSourceDAO.getPublishGoodsSource(vo);
	}
	@Override
	public List<AsppGoodsSourceVO> goodsSourceList(AsppGoodsSourceVO vo) {
		// TODO Auto-generated method stub
		return goodsSourceDAO.goodsSourceList(vo);
	}
	@Override
	public List<AsppGoodsSourceVO> getGoodsIsFavorite(
			Map<String, Object> map) {
		// TODO Auto-generated method stub
		return goodsSourceDAO.getGoodsIsFavorite(map);
	}
	@Override
	public AsppGoodsSourceVO getGoodsSourceInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return goodsSourceDAO.getGoodsSourceInfo(map);
	}

}
