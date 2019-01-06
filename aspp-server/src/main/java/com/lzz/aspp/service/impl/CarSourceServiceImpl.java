package com.lzz.aspp.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.dao.CarSourceDAO;
import com.lzz.aspp.service.CarSourceService;
import com.lzz.aspp.vo.AsppCarSourceVO;
import com.lzz.lsp.base.vo.CarSourceVO;
import com.lzz.lsp.base.vo.CarVO;

@Service("carSourceService")
public class CarSourceServiceImpl implements CarSourceService{

	@Resource(name = "carSourceDAO")
	private CarSourceDAO carSourceDAO;
	
	@Override
	public List<AsppCarSourceVO> getCarSources(Integer userId, String carSourceType) {
		return carSourceDAO.getCarSources(userId, carSourceType);
	}

	@Override
	public List<CarVO> getCanPublishCars(Integer userId, String carSourceType) {
		return carSourceDAO.getCanPublishCars(userId, carSourceType);
	}

	@Override
	public List<AsppCarSourceVO> queryCarSources(AsppCarSourceVO vo) {
		return carSourceDAO.queryCarSources(vo);
	}
	@Override
	public List<AsppCarSourceVO> getCarSourceIsFavorite(
			Map<String, Object> map) {
		return carSourceDAO.getCarSourceIsFavorite(map);
	}
	@Override
	public AsppCarSourceVO getCarSourcePhotos(Map<String, Object> map) {
		return carSourceDAO.getCarSourcePhotos(map);
	}
	@Override
	public AsppCarSourceVO getCarSourceIsFavoriteByUserId(Integer userId,Integer infoId) {
		return carSourceDAO.getCarSourceIsFavoriteByUserId(userId,infoId);
	}

	@Override
	public AsppCarSourceVO getCarSourceContent(Integer publishId) {
		return carSourceDAO.getCarSourceContent(publishId);
	}
	
	@Override
	public Integer getCarSourceFavoriteId(Map<String, Object> map){
		return carSourceDAO.getCarSourceFavoriteId(map);
	}
}
