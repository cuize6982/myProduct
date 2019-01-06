package com.lzz.aspp.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.DriverRouteRec;
import com.lzz.lsp.base.vo.LocationVO;
import com.lzz.lsp.core.driverRouteRec.service.DriverRouteRecService;
import com.lzz.lsp.core.location.service.LocationBaseService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;

@Controller
@RequestMapping("/user/location")
public class UserLocationController extends BaseController{
	
	Logger logger = Logger.getLogger(UserLocationController.class);
	
	@Resource(name = "driverRouteRecService")
	private DriverRouteRecService driverRouteRecService;
	
	@Resource(name = "locationBaseService")
	private LocationBaseService locationBaseService;
	
	
	/**
	 * <p>保存位置信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年9月22日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addUserLocation.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addUserLocation(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			String userNotNullStr = "driver,longitude,latitude,locationName,mobile";
			// 参数赋值
			LocationVO locationVO = (LocationVO) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), LocationVO.class, userNotNullStr, model);
			locationVO.setLocateTime(new Date());
			locationBaseService.saveLocation(locationVO);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			super.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	
	
	/**
	 * <p>获取运单监控历史</p>
	 * @author Liuyanliang 
	 * 2015年9月22日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getUserRouteRecs.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getUserRouteRecs(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		// 返回参数
		model.put("code", ReturnDatas.SUCCESS_CODE);
		try {
			String waybillNo = request.getParameter("waybillNo");
			if(!StringUtils.isEmpty(waybillNo)){
				DriverRouteRec rec = new DriverRouteRec();
				rec.setWaybillNo(waybillNo);
				List<DriverRouteRec> recs = driverRouteRecService.queryDriverRouteRecs(rec);
				model.put("data", recs);
			}
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	
	
	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/asfp-server/user/location/addUserLocation.do";
		Map<String, String> map = new HashMap<String, String>();
		map.put("accessToken", "cf8918f60571a1baf5b6d373e9c1df74");
		map.put("driver", "79");
		map.put("longitude", "114.323231");
		map.put("latitude", "119.665852");
		map.put("locationName", "56789");
		try {
			String str = HttpUtils.sendHttpRequest(url, com.lzz.lton.util.HttpUtils.RequestMethod.POST, map);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
