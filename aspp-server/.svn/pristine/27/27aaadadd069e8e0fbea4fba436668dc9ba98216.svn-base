package com.lzz.aspp.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.lzz.aspp.dao.MonitorCarDAO;
import com.lzz.aspp.service.MonitorCarService;
import com.lzz.lsp.base.qo.CarMonitorQuery;
import com.lzz.lsp.base.vo.CarMonitorVO;

@Service("monitorCarService")
public class MonitorCarServiceImpl implements MonitorCarService {

	@Resource(name = "monitorCarDAO")
	private MonitorCarDAO monitorCarDAO;
	@Override
	public List<CarMonitorVO> queryAddMonitorCar(CarMonitorQuery carMonitorQuery) {
		return monitorCarDAO.queryAddMonitorCar(carMonitorQuery);
	}

}
