package com.lzz.aspp.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.google.gson.Gson;
import com.lzz.aspp.util.CommonUtil;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.OrderStatus;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Complain;
import com.lzz.lsp.base.domain.DriverRouteRec;
import com.lzz.lsp.base.domain.Location;
import com.lzz.lsp.base.domain.MutualEvaluation;
import com.lzz.lsp.base.domain.PayRecord;
import com.lzz.lsp.base.domain.Puser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.VersionMap;
import com.lzz.lsp.base.domain.Waybill;
import com.lzz.lsp.base.qo.WaybillQuery;
import com.lzz.lsp.base.vo.CarVO;
import com.lzz.lsp.base.vo.WaybillVO;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.core.attachment.service.AttachmentBaseService;
import com.lzz.lsp.core.car.service.CarBaseService;
import com.lzz.lsp.core.complain.service.ComplainBaseService;
import com.lzz.lsp.core.driverRouteRec.service.DriverRouteRecService;
import com.lzz.lsp.core.evaluation.service.MutualEvaluationService;
import com.lzz.lsp.core.lbs.service.LbsService;
import com.lzz.lsp.core.lbs.service.impl.LocationResult;
import com.lzz.lsp.core.location.service.LocationBaseService;
import com.lzz.lsp.core.payment.service.WaybillTradeService;
import com.lzz.lsp.core.publish.service.PublishInfoBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.core.waybill.service.WaybillBaseService;
import com.lzz.lsp.psi.util.PsiAttachmentUtil;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.io.PropertiesSource;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.DateUtils;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;
import com.lzz.lton.util.StringUtils;

/**
 * <p>运单controller</p>
 * @author Liuyl 2015年8月12日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/waybill")
public class WaybillController extends BaseController{
	
	private static Logger logger = Logger.getLogger(WaybillController.class);
	private static Gson g = new Gson();

	
	@Resource(name = "dictService")
	private DictService dictService;

	@Resource(name = "waybillBaseService")
	private WaybillBaseService waybillBaseService;
	
	@Resource(name = "carBaseService")
	private CarBaseService carBaseService;

	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "locationBaseService")
	private LocationBaseService locationBaseService;
	
	@Resource(name = "attachmentBaseService")
	private AttachmentBaseService attachmentBaseService;
	
	@Resource(name = "publishInfoBaseService")
	private PublishInfoBaseService publishInfoBaseService;
	
	@Resource(name = "mutualEvaluationService")
	private MutualEvaluationService mutualEvaluationService;
	
	@Resource(name = "complainBaseService")
	private ComplainBaseService complainBaseService;
	
	@Resource(name = "lbsService")
	private LbsService lbsService;
	
	@Resource(name = "driverRouteRecService")
	private DriverRouteRecService driverRouteRecService;
	
	@Resource(name = "waybillTradeService")
	private WaybillTradeService waybillTradeService;
	
	/**
	 * <p>获得已成交运单列表(老数据)<p>
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @return
	 * @author tangs 2015年8月13日
	 */
	@RequestMapping(value = "/getCompletedWaybillList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCompletedWaybillList(String accessToken,Integer userId, Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setShipper(userId);
			queryObject.setStatus(99);
			queryObject.setFlag(1);
			queryObject.setProcessType(1);
			queryObject.setPageNo(pageNo);
			List<WaybillVO> waybillVOList = waybillBaseService.queryAppCompletedWaybill(queryObject);
			for(WaybillVO waybillVO:waybillVOList){
				//距离计算
				String distanceValue = "";
				Double elng = 0.0;
				Double elat = 0.0;
				//通过目的地名称，获取目的地坐标
				if(!StringUtils.isBlank(waybillVO.getToPlace())){
					String eRequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getToPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
					JSONObject  eJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(eRequestUrl, HttpUtils.RequestMethod.GET, null));
					elng = eJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
					elat = eJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
					
					//获取剩余距离：当前位置到目的地的距离
					if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
						 distanceValue = "距到达城市"+Double.valueOf(CommonUtil.getDistance(Double.valueOf(waybillVO.getLongitude()), Double.valueOf(waybillVO.getLatitude()), elng, elat)).longValue()+"公里";
					}else{
						distanceValue = "距离不详";
					}
				}else{
					distanceValue = "距离不详";
				}
				waybillVO.setDistance(distanceValue);
				
				
				
				//通过出发地名称，获取出发地坐标
				String fJequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getFromPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
				JSONObject  fJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(fJequestUrl, HttpUtils.RequestMethod.GET, null));
				Double flng = fJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
				Double flat = fJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
				
				//设置百度地图路线url
				if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
					String locationUrl = "/base/service/toMapStaticNavigation.do?"
							+"statLocation="+flng+","+flat
							+"&currentLocation="+waybillVO.getLongitude()+","+waybillVO.getLatitude()
							+"&endLocation="+elng+","+elat;
					waybillVO.setLocationUrl(locationUrl);
				}else{
					waybillVO.setLocationName("未开启定位");
				}
			}
			model.put("completedWaybills", queryObject.getQueryList());
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>获得已成交运单列表(新版本)<p>
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @return
	 * @author tangs 2015年8月13日
	 */
	@RequestMapping(value = "/getCompletedWaybillListNewVersion.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCompletedWaybillListNewVersion(String accessToken,Integer userId, Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setShipper(userId);
			queryObject.setStatus(99);
			//queryObject.setFlag(1);
			queryObject.setProcessType(1);
			queryObject.setPageNo(pageNo);
			List<WaybillVO> waybillVOList = waybillBaseService.queryAsppCompletedWaybill(queryObject);
			for(WaybillVO waybillVO:waybillVOList){
				//距离计算
				String distanceValue = "";
				Double elng = 0.0;
				Double elat = 0.0;
				if(!StringUtils.isBlank(waybillVO.getToPlace())){
					String eRequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getToPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
					JSONObject  eJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(eRequestUrl, HttpUtils.RequestMethod.GET, null));
					elng = eJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
					elat = eJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
					if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
						 distanceValue = "距到达城市"+Double.valueOf(CommonUtil.getDistance(Double.valueOf(waybillVO.getLongitude()), Double.valueOf(waybillVO.getLatitude()), elng, elat)).longValue()+"公里";
					}else{
						distanceValue = "距离不详";
					}
				}else{
					distanceValue = "距离不详";
				}
				waybillVO.setDistance(distanceValue);
				
				//定位访问url
				String fJequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getFromPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
				JSONObject  fJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(fJequestUrl, HttpUtils.RequestMethod.GET, null));
				Double flng = fJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
				Double flat = fJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
				if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
					String locationUrl = "/base/service/toMapStaticNavigation.do"
				    +"?statLocation="+flng+","+flat
					+"&currentLocation="+waybillVO.getLongitude()+","+waybillVO.getLatitude()
					+"&endLocation="+elng+","+elat;
					waybillVO.setLocationUrl(locationUrl);
				}else{
					waybillVO.setLocationName("未开启定位");
				}
			}
			model.put("completedWaybills", queryObject.getQueryList());
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>查看已成交运单详情</p>
	 * @author zhaoyx 2016年6月2日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/checkWaybillInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> checkWaybillInfo(String accessToken,String waybillNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setWaybillNo(waybillNo);
			List<WaybillVO> waybillVOList = waybillBaseService.getAsppCompletedWaybillInfo(queryObject);
			for(WaybillVO waybillVO:waybillVOList){
				//距离计算
				String distanceValue = "";
				Double elng = 0.0;
				Double elat = 0.0;
				if(!StringUtils.isBlank(waybillVO.getToPlace())){
					String eRequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getToPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
					JSONObject  eJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(eRequestUrl, HttpUtils.RequestMethod.GET, null));
					elng = eJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
					elat = eJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
					if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
						 distanceValue = "距到达城市"+Double.valueOf(CommonUtil.getDistance(Double.valueOf(waybillVO.getLongitude()), Double.valueOf(waybillVO.getLatitude()), elng, elat)).longValue()+"公里";
					}else{
						distanceValue = "距离不详";
					}
				}else{
					distanceValue = "距离不详";
				}
				waybillVO.setDistance(distanceValue);
				
				//定位访问url
				String fJequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getFromPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
				JSONObject  fJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(fJequestUrl, HttpUtils.RequestMethod.GET, null));
				Double flng = fJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
				Double flat = fJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
				if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
					String locationUrl = "/base/service/toMapStaticNavigation.do?statLocation="+flng+","+flat+"&currentLocation="+waybillVO.getLongitude()+","+waybillVO.getLatitude()+"&endLocation="+elng+","+elat;
					waybillVO.setLocationUrl(locationUrl);
				}else{
					waybillVO.setLocationName("未开启定位");
				}
			}
//			//取到货物描述并将出发地目的地截取
//			String infoContent = queryObject.getQueryList().get(0).getInfoContent();
//			String getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
//			queryObject.getQueryList().get(0).setInfoContent(getSignInfo);
			
			model.put("completedWaybills", queryObject.getQueryList());
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>获取未完成运单列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月13日
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/getNoDealWaybillList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getNoDealWaybillList(String accessToken,Integer userId, Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setUserId(userId);
			queryObject.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			queryObject.setFlag(1);
			queryObject.setPageNo(pageNo);
			List<WaybillVO> queryGrabUnconfirmedWaybillInfo = waybillBaseService.queryGrabUnconfirmedWaybillInfo(queryObject);
//			for (WaybillVO waybillVO : queryGrabUnconfirmedWaybillInfo) {
//				String infoContent = waybillVO.getInfoContent();
//				String getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
//				waybillVO.setInfoContent(getSignInfo);
//			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybills", queryGrabUnconfirmedWaybillInfo);
			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * <p>获取未完成运单详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月13日
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/getNoDealWaybillDetailsList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getNoDealWaybillDetailsList(String accessToken, Integer infoId,Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("infoId", infoId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setInfoId(infoId);
			queryObject.setPageNo(pageNo);
			queryObject.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			queryObject.setFlag(0);
			queryObject.setDriverFlag(1);
			List<WaybillVO> waybillDetails = waybillBaseService.queryAppDriverGrabWaybill(queryObject);
			for (WaybillVO waybillVO : waybillDetails) {
				Integer userId = waybillVO.getHaulier();
				List<CarVO> cars = waybillVO.getCarVOs();
				String[] carTypeName = new String[cars.size()];
				for (int i = 0; i < cars.size(); i++) {
					carTypeName[i] = cars.get(i).getCarTypeName();
				}
				waybillVO.setCarTypeName(carTypeName);
				Puser puser = userBaseService.getPuser(userId);
				waybillVO.setIdCarNo("未实名认证");
				if(null != puser && !StringUtils.isBlank(puser.getIdCardNo())){
					waybillVO.setIdCarNo(CommonUtil.replaceIdCarNO(puser.getIdCardNo()));
				}
				Location location = locationBaseService.getLocation(userId);
				if(null != location){
					waybillVO.setLocationName(location.getLocationName());
					waybillVO.setLocateTime(new Date());
					waybillVO.setLongitude(location.getLongitude());
					waybillVO.setLatitude(location.getLatitude());
					waybillVO.setLocationUrl("/base/service/toMapPoi.do?location=" 
							+ location.getLongitude() + "," + location.getLatitude()
							+ "&poi=" + location.getLocationName());
				} else {
					waybillVO.setLocationName("未开启定位");
					waybillVO.setLocateTime(null);
				}
			}
			Integer publishStatus = publishInfoBaseService.getPublishStatus(infoId,DataConstants.INFO_TYPE_GOODS_SOURCE);
			if(publishStatus!=null && publishStatus==4){
				model.put("isClosed", ReturnDatas.CAN_NOT_BE_CLOSED);
			}else{
				model.put("isClosed", ReturnDatas.CAN_CLOSE);
			}
//			//取到货物描述并将出发地目的地截取
//			String infoContent = waybillDetails.get(0).getInfoContent();
//			String getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
//			waybillDetails.get(0).setInfoContent(getSignInfo);
			
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybillDetails", waybillDetails);
			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * <p>获取司机名下的车辆</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月13日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/getDriverCars.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getDriverCars(String accessToken,Integer userId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			List<CarVO> queryCarsByUserId = carBaseService.queryCarsByUserId(userId);
			for (CarVO car : queryCarsByUserId) {
				if(null != car.getValName() && !"".equals(car.getValName()) ){
					car.setCarTypeName(car.getValName());
				}else{
					Integer carTypeFirstCode=car.getCarTypeFirstCode();
					Integer carTypeSecondCode=car.getCarTypeSecondCode();
					Integer carTypeThirdCode=car.getCarTypeThirdCode();
					String carTypeStr=null;
					if(carTypeFirstCode==null || "".equals(carTypeFirstCode)){
						
					}else{
						if(carTypeSecondCode == null || "".equals(carTypeSecondCode)){
						String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
						car.setCarTypeCode(carTypeFirstCode.toString());
						carTypeStr =TypeName1;
						}else{
							if(carTypeThirdCode == null || "".equals(carTypeThirdCode)){
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								car.setCarTypeCode(carTypeSecondCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2;
							}else{
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String TypeName3 =dictService.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
								car.setCarTypeCode(carTypeThirdCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2+"-"+TypeName3;
							}
						}
					}
					
					car.setCarTypeName(carTypeStr);
//					map.put("carTypeName", carTypeStr);
				}
			}
			
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("cars", queryCarsByUserId);
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
	 * <p>确认成交运单【1.0抢货端抢1.0配货端,一键成交】</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月13日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/dealWaybill.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> dealWaybill(HttpServletRequest request, String accessToken,String loginUserName, 
			String waybillNo, String carIds, Integer userId){
		logger.info("user enter WaybillController-dealWaybill!");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("loginUserName", loginUserName, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			CommonUtils.checkParams("carIds", carIds, model);
			logger.info("dealWaybill----carIds="+g.toJson(carIds.split(",")));
			waybillBaseService.confirmDriverWaybill(waybillNo, loginUserName, carIds.split(","));
			// 送积分
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("scoreType", DataConstants.INTERCEPT_TYPE_WAYBILL);
			model.put("userId", userId);
			model.put("userType", DataConstants.LOGISTICS_COMPANY);
			model.put("payStage", DataConstants.PAY_STAGE_ZQ);
			model.put("waybillNo", waybillNo);
			//运单成交的标志
			model.put("isDeal", DataConstants.YES);
			this.setSuccessFlag(model);
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
	 * 
	 * <p>物流公司确认下单</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param waybillNo
	 * @param accountName
	 * @param carIds
	 * @return
	 */
	@RequestMapping(value = "/confirmWaybillTransaction.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> confirmWaybillTransaction(String accessToken, String waybillNo ,String accountName, String... carIds){
		logger.info("user enter WaybillController-confirmWaybillTransaction!");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			CommonUtils.checkParams("accountName", accountName, model);
			WaybillVO waybill = waybillBaseService.getWaybillMessage(waybillNo);
			boolean isDeal = waybillBaseService.confirmWaybillTransaction(waybill, accountName,carIds);
			logger.info("confirmWaybillTransaction,isDeal="+isDeal);
			//推送消息
			model.put("accountName", accountName);
			model.put("pushSource", DataConstants.PUSH_SOURCE_CONFIRM_WAYBILL);
			//公共部分
			model.put("waybillNo", waybillNo);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			//送积分
			if(isDeal){
				model.put("userId", waybill.getShipper());
				model.put("scoreType", DataConstants.INTERCEPT_TYPE_WAYBILL);
				model.put("userType", DataConstants.LOGISTICS_COMPANY);
				model.put("isDeal", DataConstants.YES);
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
	
	
	
	
	/**
	 * 线下支付
	 * LiuYanliang 修改注释
	 * @param accessToken 令牌
	 * @param waybillNo 运单号
	 * @param paymentPhase 付款阶段  1:预付款   2:中期款  3:尾款
	 * @return 
	 * 2016年9月26日 下午5:48:31
	 */
	@RequestMapping(value = "/waybillOfflinePayment.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> waybillOfflinePayment(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			Map<String, Object> params = new HashMap<String, Object>();
			WaybillVO waybillVO = new WaybillVO();
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			String waybillNo = CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			//保存参数
			params.put("waybillNo", waybillNo);
			params.put("paymentPhase", Integer.parseInt(paramsMap.get("paymentPhase").toString()));
			//获取用户运单余额
			WaybillVO queryWaybillMoney = waybillBaseService.queryWaybillMoney(params);
			
			VersionMap versionMapForHaulier = new VersionMap();
			VersionMap versionMapForShipper = new VersionMap();
			//配货端
			WaybillVO queryLoginSessionForShipper = waybillBaseService.queryLoginSession(queryWaybillMoney.getShipper());
			//抢货端
			WaybillVO queryLoginSessionForHaulier = waybillBaseService.queryLoginSession(queryWaybillMoney.getHaulier());
			
			//找货端终端版本
			Map<String, Object> params_haulier = new HashMap<String, Object>();
			params_haulier.put("terminalVersion", queryLoginSessionForHaulier.getTerminalVersion());
			params_haulier.put("funName", "TRADE");
			versionMapForHaulier = waybillBaseService.getVersionMap(params_haulier);
			
			//配货端终端版本
			Map<String, Object> params_shipper = new HashMap<String, Object>();
			params_shipper.put("terminalVersion", queryLoginSessionForShipper.getTerminalVersion());
			params_shipper.put("funName", "TRADE");
			versionMapForShipper = waybillBaseService.getVersionMap(params_shipper);
			
			//paymentStatus支付状态  paymentStatus<>null:支付完成  1：支付完成
			if(null != paramsMap.get("paymentStatus") && Integer.valueOf(versionMapForShipper.getFunVersion()) > 2){
				
				//用户运单余额
				Float waybillMoney = queryWaybillMoney.getWaybillMoney();
				//扣款后余额
				Float newWaybillMoney = (float) 0.0;
				if(Integer.parseInt(params.get("paymentPhase").toString()) == DataConstants.PAY_STAGE_YF 
						&& waybillMoney > queryWaybillMoney.getAdvancePrice()){
					//改运单预付款
					newWaybillMoney = waybillMoney - queryWaybillMoney.getAdvancePrice();
				}else if(Integer.parseInt(params.get("paymentPhase").toString()) == DataConstants.PAY_STAGE_ZQ
						&& waybillMoney > queryWaybillMoney.getMidPrice()){
					//中期款
					newWaybillMoney = waybillMoney - queryWaybillMoney.getMidPrice();
				}else if(Integer.parseInt(params.get("paymentPhase").toString()) == DataConstants.PAY_STAGE_WK
						&& waybillMoney > queryWaybillMoney.getFinalPrice()){
					//尾款
					newWaybillMoney = waybillMoney - queryWaybillMoney.getFinalPrice();
				}else{
					waybillVO.setWaybillMoney(waybillMoney);
				}
				//拼装需要条件
				waybillVO.setWaybillMoney(newWaybillMoney);
				waybillVO.setUserId(queryWaybillMoney.getUserId());
				if(waybillMoney != newWaybillMoney){
					
					//修改用户余额
					waybillBaseService.updateWaybillMoney(waybillVO);
				}
			}
			WaybillQuery waybillQuery = new WaybillQuery();
			waybillQuery.setWaybillNo(waybillNo);
			waybillQuery.setUpdateTime(new Date());
			
			String paymentPhase = CommonUtils.checkParams("paymentPhase ", paramsMap.get("paymentPhase").toString(), model);
			Integer payStage = Integer.parseInt(paymentPhase);
			switch (payStage) {
			case 1:
				waybillQuery.setOrderStatus(OrderStatus.NO_RECEIVE_GOODS);
				break;
			case 2:
				if(Integer.valueOf(versionMapForHaulier.getFunVersion()) == 3
						&& Integer.valueOf(versionMapForShipper.getFunVersion()) == 3){
					waybillQuery.setOrderStatus(OrderStatus.NO_PAYMENT_FINAL);
					break;
				}else if((Integer.valueOf(versionMapForHaulier.getFunVersion()) == 2
						&& Integer.valueOf(versionMapForShipper.getFunVersion()) == 3) 
						|| (Integer.valueOf(versionMapForHaulier.getFunVersion()) == 3
						&& Integer.valueOf(versionMapForShipper.getFunVersion()) == 2)){
					waybillQuery.setOrderStatus(OrderStatus.FINISH);
					waybillQuery.setIsFinalPay(DataConstants.WAYBILL_FINAL_PAY_VERSION_2);
					model.put("isDeal", DataConstants.YES);
					break;
				}else{
					waybillQuery.setOrderStatus(OrderStatus.FINISH);
					waybillQuery.setTransactionTimeStart(new Date());
					model.put("isDeal", DataConstants.YES);
					break;
				}
			case 3:
				if((Integer.valueOf(versionMapForHaulier.getFunVersion()) == 2
				&& Integer.valueOf(versionMapForShipper.getFunVersion()) == 3) 
				|| (Integer.valueOf(versionMapForHaulier.getFunVersion()) == 3
				&& Integer.valueOf(versionMapForShipper.getFunVersion()) == 2)){
				waybillQuery.setOrderStatus(OrderStatus.FINISH);
				waybillQuery.setIsFinalPay(DataConstants.WAYBILL_FINAL_PAY_VERSION_3);
				waybillQuery.setTransactionTimeStart(new Date());
				model.put("isDeal", DataConstants.YES);
				break;
			}else{
				waybillQuery.setOrderStatus(OrderStatus.FINISH);
				waybillQuery.setTransactionTimeStart(new Date());
				model.put("isDeal", DataConstants.YES);
				break;
			}
			default:
				break;
			}
			waybillBaseService.receivePrePayment(waybillQuery);
			
			WaybillVO waybill = waybillBaseService.getWaybillMessage(waybillNo);
		    if(waybill != null){
		    	// 增加积分
				model.put("userId", waybill.getShipper());
				model.put("userType", DataConstants.LOGISTICS_COMPANY);
				model.put("waybillNo", waybillNo);
				model.put("scoreType", DataConstants.INTERCEPT_TYPE_WAYBILL);
		    }
		    model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>上传运单图片</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年9月18日
	 * @param accessToken
	 * @param loginUserName
	 * @param waybillNo
	 * @param carIds
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/uploadWaybillImg.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadWaybillImg(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			// 准备修改数据
			String protocolPhotos = PsiAttachmentUtil.uploadImg(request, paramsMap.get("loginUserName").toString(), false, false, "WAYBILL_PROTOCOL_PHOTO");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("protocolPhoto", protocolPhotos);
			params.put("waybillNo", paramsMap.get("waybillNo").toString());
			params.put("updateUser", paramsMap.get("loginUserName").toString());
			params.put("updateTime", new Date());
			waybillBaseService.updateWaybillInfo(params);
			// 返回数据
			model.put("waybillNo", paramsMap.get("waybillNo").toString());
			model.put("protocolPhoto", protocolPhotos);
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>删除运单图片</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年9月21日
	 * @param accessToken
	 * @param waybillNo
	 * @param imgIds
	 * @param loginUserName
	 * @return
	 */
	@RequestMapping(value = "/deleteWaybillImg.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> deleteWaybillImg(String accessToken, String waybillNo, String imgIds,
			String loginUserName){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			// 验证访问凭证
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			CommonUtils.checkParams("imgId", imgIds, model);
			// 准备修改数据
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("protocolPhoto", imgIds);
			params.put("waybillNo", waybillNo);
			params.put("updateUser", loginUserName);
			params.put("updateTime", new Date());
			waybillBaseService.updateWaybillInfo(params);
			// 返回数据
			model.put("waybillNo", waybillNo);
			model.put("protocolPhoto", imgIds);
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
	 * 
	 * <p>物流公司评论司机</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月29日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addEvaluateWaybill.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addEvaluateWaybill(HttpServletRequest request){
		logger.info("------user enter addEvaluateWaybill !");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			//获取传入参数
			Integer evaluateFrom = Integer.valueOf(paramsMap.get("evaluateFrom").toString()) ;
			Integer evaluateTo =  Integer.valueOf(paramsMap.get("evaluateTo").toString());
			String content = paramsMap.get("content").toString();
			Integer star = Integer.valueOf(paramsMap.get("star").toString());
			String waybillNo = paramsMap.get("waybillNo").toString();
			Date date = new Date();
			
			//保存参数
			MutualEvaluation mutualEvaluation  = new MutualEvaluation();
			mutualEvaluation.setEvaluateFrom(evaluateFrom);
			mutualEvaluation.setEvaluateTo(evaluateTo);
			mutualEvaluation.setContent(content);
			mutualEvaluation.setStar(star);
			mutualEvaluation.setWaybillNo(waybillNo);
			mutualEvaluation.setCreateTime(date);
			mutualEvaluation.setType(DataConstants.EVALUATE_TYPE_E);
			logger.info("addEvaluateWaybill-------star="+star);
			mutualEvaluationService.saveMutualEvaluation(mutualEvaluation);
			
			// 推送所需参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybill", waybillNo);
//			super.setSuccessFlag(model);
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
	 * 
	 * <p>物流公司获取未下单运单列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/queryUnconfirmedList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryUnconfirmedList(String accessToken,Integer userId, Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setUserId(userId);
			queryObject.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			queryObject.setFlag(1);
			queryObject.setPageNo(pageNo);
			List<WaybillVO> queryUnconfirmedList = waybillBaseService.queryUnconfirmedList(queryObject);
			//截取货源信息地址
			for (WaybillVO waybillVO : queryUnconfirmedList) {
				String infoContent = waybillVO.getInfoContent();
				String getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
				waybillVO.setInfoContent(getSignInfo);
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybills", queryUnconfirmedList);
			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * 
	 * <p>未下单运单详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param infoId
	 * @param pageNo
	 * @return
	 */
	@RequestMapping(value = "/queryDriverGrabWaybillInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryDriverGrabWaybillInfo(String accessToken, Integer infoId,Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("infoId", infoId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			WaybillQuery queryObject = new WaybillQuery();
			queryObject.setInfoId(infoId);
			queryObject.setPageNo(pageNo);
			queryObject.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			queryObject.setFlag(0);
			queryObject.setDriverFlag(1);
			List<WaybillVO> waybillDetails = waybillBaseService.queryDriverGrabWaybillInfo(queryObject);
			for (WaybillVO waybillVO : waybillDetails) {
				//查询抢单用户是否认证
				Integer haulier = waybillVO.getHaulier();
				User user = this.userBaseService.getUser(haulier);
				waybillVO.setIsAuth(user.getIsAuth());
				Integer userId = waybillVO.getHaulier();
				List<CarVO> cars = waybillVO.getCarVOs();
				String[] carTypeName = new String[cars.size()];
				for (int i = 0; i < cars.size(); i++) {
					carTypeName[i] = cars.get(i).getCarTypeName();
				}
				waybillVO.setCarTypeName(carTypeName);
				Puser puser = userBaseService.getPuser(userId);
				waybillVO.setIdCarNo("未实名认证");
				if(null != puser && !StringUtils.isBlank(puser.getIdCardNo())){
					waybillVO.setIdCarNo(CommonUtil.replaceIdCarNO(puser.getIdCardNo()));
				}
				Location location = locationBaseService.getLocation(userId);
				if(null != location){
					waybillVO.setLocationName(location.getLocationName());
					waybillVO.setLocateTime(new Date());
					waybillVO.setLongitude(location.getLongitude());
					waybillVO.setLatitude(location.getLatitude());
					waybillVO.setLocationUrl("/base/service/toMapPoi.do?location=" 
							+ location.getLongitude() + "," + location.getLatitude()
							+ "&poi=" + location.getLocationName());
				} else {
					waybillVO.setLocationName("未开启定位");
					waybillVO.setLocateTime(null);
				}
//				//取到货物描述并将出发地目的地截取
//				String infoContent = waybillVO.getInfoContent();
//				String getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
//				waybillVO.setInfoContent(getSignInfo);
			}
			Integer publishStatus = publishInfoBaseService.getPublishStatus(infoId,DataConstants.INFO_TYPE_GOODS_SOURCE);
			if(publishStatus!=null && publishStatus==4){
				model.put("isClosed", ReturnDatas.CAN_NOT_BE_CLOSED);
			}else{
				model.put("isClosed", ReturnDatas.CAN_CLOSE);
			}
			
			
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybillDetails", waybillDetails);
			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * 
	 * <p>物流公司拒绝下单</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/refuseTransaction.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> refuseTransaction(String accessToken, String waybillNo){
		logger.info("user enter WaybillController-refuseTransaction!");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			Integer orderStatus = OrderStatus.REFUSE_WAYBILL;
			waybillBaseService.refuseTransaction(waybillNo,orderStatus);
			// 推送所需数据
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybillNos", waybillNo);
			model.put("pushSource", DataConstants.PUSH_SOURCE_REFUSE_WAYBILL);
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
	 * 
	 * <p>获取未出发、未收货、已成交的列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @param orderStatus
	 * @return
	 */
	@RequestMapping(value = "/queryDriverGrabWaybillList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryDriverGrabWaybillList(String accessToken,Integer userId, Integer pageNo, Integer orderStatus, Integer evaluate, Integer status){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			
			//根据用户ID获取用户终端版本号
			WaybillVO haulierLoginSession = waybillBaseService.queryLoginSession(userId);
			//保存参数
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("terminalVersion", haulierLoginSession.getTerminalVersion());
			paramsMap.put("funName", "TRADE");
			//查询功能版本号
			VersionMap versionMapForHaulier = waybillBaseService.getVersionMap(paramsMap);
			
			WaybillQuery queryObject = new WaybillQuery();
			//保存查询条件
			if(evaluate != null && !"".equals(evaluate)){
				queryObject.setIsEEvaluated(evaluate);
			}
			queryObject.setShipper(userId);
			queryObject.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			queryObject.setFlag(1);
			queryObject.setPageNo(pageNo);
			queryObject.setOrderStatus(orderStatus);
			queryObject.setStatus(status);
			queryObject.setFunctionVersion(Integer.valueOf(versionMapForHaulier.getFunVersion()));
			List<WaybillVO> queryDriverGrabWaybillList = waybillBaseService.queryDriverGrabWaybillList(queryObject);
			for (WaybillVO waybillVO : queryDriverGrabWaybillList) {
				if(null != waybillVO.getOrderStatus()){
					if(waybillVO.getOrderStatus() == OrderStatus.FINISH
							&& waybillVO.getIsFinalPay() == DataConstants.WAYBILL_FINAL_PAY_VERSION_2
							&& Integer.valueOf(versionMapForHaulier.getFunVersion()) >= 3){
						
						waybillVO.setOrderStatus(OrderStatus.NO_PAYMENT_FINAL);
					}else if(waybillVO.getOrderStatus() == OrderStatus.WAYBILL_COMPLETED_DRIVER_DEL_WAYBILL
							&& waybillVO.getIsFinalPay() == DataConstants.WAYBILL_FINAL_PAY_VERSION_2
							&& Integer.valueOf(versionMapForHaulier.getFunVersion()) >= 3){
						waybillVO.setOrderStatus(OrderStatus.NO_PAYMENT_FINAL);
					}
				}
				
				String infoContent = waybillVO.getInfoContent();
				String getSignInfo = "";
				getSignInfo = infoContent.substring(infoContent.indexOf("，") + 1);//获取开始截取的位置，之后截取逗号后面的所有内容
				if(null == getSignInfo || " ".equals(getSignInfo)){
					getSignInfo = infoContent.substring(infoContent.indexOf(",") + 1);
				}
				waybillVO.setInfoContent(getSignInfo);
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybills", queryDriverGrabWaybillList);
			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * 
	 * <p>获取未出发、未收货、已成交的列表详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/getDriverGrabWaybillInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getDriverGrabWaybillInfo(String accessToken, String waybillNo, Integer userId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			params.put("userId", userId);
			params.put("waybillNo", waybillNo);
			WaybillVO waybillVO = waybillBaseService.getDriverGrabWaybillInfo(waybillNo);
			//判断是否已支付
			if(waybillVO !=null){
				if(OrderStatus.NO_PAY_BEFOREHAND_MONEY.equals(waybillVO.getOrderStatus()) 
						|| OrderStatus.NO_PAYMENT_RECEIVED.equals(waybillVO.getOrderStatus())
						|| OrderStatus.NO_PAYMENT_FINAL.equals(waybillVO.getOrderStatus()) 
						|| (OrderStatus.FINISH.equals(waybillVO.getOrderStatus()) 
								&& DataConstants.WAYBILL_FINAL_PAY_VERSION_2 == waybillVO.getIsFinalPay() )
						|| OrderStatus.WAYBILL_COMPLETED_DRIVER_DEL_WAYBILL  == waybillVO.getOrderStatus()){
					Map<String,Object> param = new HashMap<String, Object>();
					param.put("waybillNo", waybillNo);
					if(waybillVO.getOrderStatus().equals(OrderStatus.NO_PAY_BEFOREHAND_MONEY)){
						param.put("payStage", DataConstants.PAY_STAGE_YF);
					}else if(waybillVO.getOrderStatus().equals(OrderStatus.NO_PAYMENT_RECEIVED)){//中期款
						
						param.put("payStage", DataConstants.PAY_STAGE_ZQ);
					}else{//尾款
						param.put("payStage", DataConstants.PAY_STAGE_WK);
					}
					PayRecord payRecord = waybillTradeService.getWaybillTradeRec(param);
					if(payRecord !=null){//已支付
						waybillVO.setCompletedPay(true);
					}else{//未支付、支付失败
						waybillVO.setCompletedPay(false);
					}
				}
				if(null != waybillVO.getOrderStatus()){
					if(waybillVO.getOrderStatus() == OrderStatus.FINISH
							&& waybillVO.getIsFinalPay() == DataConstants.WAYBILL_FINAL_PAY_VERSION_2){
						
						waybillVO.setOrderStatus(OrderStatus.NO_PAYMENT_FINAL);
					}else if(waybillVO.getOrderStatus() == OrderStatus.WAYBILL_COMPLETED_DRIVER_DEL_WAYBILL
							&& waybillVO.getIsFinalPay() == DataConstants.WAYBILL_FINAL_PAY_VERSION_2){
						waybillVO.setOrderStatus(OrderStatus.NO_PAYMENT_FINAL);
					}
				}
			}
			Integer haulier = waybillVO.getHaulier();
			User user = this.userBaseService.getUser(haulier);
			waybillVO.setIsAuth(user.getIsAuth());
			//查询该运单是否被投诉  true：投诉   false：未投诉
			boolean judgeIsComplained = complainBaseService.judgeIsComplained(params);
			waybillVO.setJudgeIsComplained(judgeIsComplained);
				
				Puser puser = userBaseService.getPuser(userId);
				waybillVO.setIdCarNo("未实名认证");
				if(null != puser && !StringUtils.isBlank(puser.getIdCardNo())){
					waybillVO.setIdCarNo(CommonUtil.replaceIdCarNO(puser.getIdCardNo()));
				}
					//距离计算
					String distanceValue = "";
					Double elng = 0.0;
					Double elat = 0.0;
					//通过目的地名称，获取目的地坐标
					if(!StringUtils.isBlank(waybillVO.getToPlace())){
						String eRequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getToPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
						JSONObject  eJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(eRequestUrl, HttpUtils.RequestMethod.GET, null));
						elng = eJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
						elat = eJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
						
						//获取剩余距离：当前位置到目的地的距离
						if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
							 distanceValue = "距到达城市"+Double.valueOf(CommonUtil.getDistance(Double.valueOf(waybillVO.getLongitude()), Double.valueOf(waybillVO.getLatitude()), elng, elat)).longValue()+"公里";
						}else{
							distanceValue = "距离不详";
						}
					}else{
						distanceValue = "距离不详";
					}
					waybillVO.setDistance(distanceValue);
					
					
					
					//通过出发地名称，获取出发地坐标
					String fJequestUrl = "http://api.map.baidu.com/geocoder/v2/?address="+waybillVO.getFromPlace()+"&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
					JSONObject  fJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(fJequestUrl, HttpUtils.RequestMethod.GET, null));
					Double flng = fJson.getJSONObject("result").getJSONObject("location").getDouble("lng");
					Double flat = fJson.getJSONObject("result").getJSONObject("location").getDouble("lat");
					
					//设置百度地图路线url
					if(waybillVO.getLongitude()!=null&&waybillVO.getLatitude()!=null){
						String locationUrl = "/base/service/toMapStaticNavigation.do?"
								+"statLocation="+flng+","+flat
								+"&currentLocation="+waybillVO.getLongitude()+","+waybillVO.getLatitude()
								+"&endLocation="+elng+","+elat;
						logger.info("-------->location map url="+locationUrl);
						waybillVO.setLocationUrl(locationUrl);
					}else{
						waybillVO.setLocationName("未开启定位");
					}
					model.put("code", ReturnDatas.SUCCESS_CODE);
					model.put("noDealWaybills", waybillVO);
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
	 * 
	 * <p>物流公司删除运单</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月30日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/deleteWaybill.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> deleteWaybill(String accessToken, String waybillNo, Integer userId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			Map<String, Object> params = new HashMap<String, Object>();
			//获取当前用户信息
			User user = this.userBaseService.getUser(userId);
//			String[] split = waybillNo.split(",");
//			List<String> list = Arrays.asList(split); 
			params.put("waybillNo", waybillNo);
			params.put("isEDelete", 1);
			params.put("userType", user.getUserType());
			waybillBaseService.deleteWaybill(params);
			model.put("code", ReturnDatas.SUCCESS_CODE);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG); 
			}
			e.printStackTrace();
		}		return model;
	}
	
	/**
	 * 
	 * <p>添加投诉</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月1日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addComplain.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addComplain(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			//获取参数
			Integer complainPerson   = Integer.valueOf(CommonUtils.checkParams("complainPerson",paramsMap.get("complainPerson").toString(),model));
			Integer infoType         = Integer.valueOf(CommonUtils.checkParams("infoType",paramsMap.get("infoType").toString(),model));
			Integer infoId           = Integer.valueOf(CommonUtils.checkParams("infoId",paramsMap.get("infoId").toString(),model));
			String complainContent   = paramsMap.get("complainContent").toString();
			Date complainTime        = new Date();
			Integer byComplainPerson = Integer.valueOf(CommonUtils.checkParams("byComplainPerson",paramsMap.get("byComplainPerson").toString(),model));
			String waybillNo         = CommonUtils.checkParams("waybillNo",paramsMap.get("waybillNo").toString(),model);
			
			//保存参数
			Complain complain = new Complain();
			complain.setComplainPerson(complainPerson);
			complain.setInfoType(infoType);
			complain.setComplainContent(complainContent);
			complain.setComplainTime(complainTime);
			complain.setByComplainPerson(byComplainPerson);
			complain.setWaybillNo(waybillNo);
			complain.setIsDispose(0);
			complain.setInfoId(infoId);
			waybillBaseService.addComplain(complain);
			// 推送所需参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybill", waybillNo);
//			super.setSuccessFlag(model);
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
	 * 
	 * <p>编辑运单保险相关的信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月1日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateInsuranceMes.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateInsuranceMes(HttpServletRequest request){
		logger.info("---------user enter WaybillController-updateInsuranceMes");
		Map<String, Object> model = new HashMap<String, Object>();
			
		try {
			//运单状态
			Integer orderStatus = null;
			//公司地址
			String address = "";
			//运单号
			String waybillNo  = "";
			//负责人
			String principal  = "";
			//货运公司
			String enterpriseName = "";
			//负责人电话
			String principalPhone = "";
			//合同款
			Double transationPrice = null;
			//付款阶段  1:预付款 2：中期款 3:尾款
			Integer payStage = null;
			Long startTime = System.currentTimeMillis();
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			
			//获取参数
			if(null != paramsMap.get("address")){
				
				address         = paramsMap.get("address").toString();
			}if(null != paramsMap.get("waybillNo")){
				
				waybillNo       = paramsMap.get("waybillNo").toString();
			}if(null != paramsMap.get("principal")){
				
				principal       = paramsMap.get("principal").toString();
			}if(null != paramsMap.get("enterpriseName")){
				
				enterpriseName  = paramsMap.get("enterpriseName").toString();
			}if(null != paramsMap.get("principalPhone")){
				
				principalPhone  = paramsMap.get("principalPhone").toString();
			}if(null != paramsMap.get("transationPrice")){
				
				//合同款
				transationPrice = Double.valueOf(paramsMap.get("transationPrice").toString());
			}
			
			if(null == paramsMap.get("payStage")){
				orderStatus    = OrderStatus.LOADING_TO_CONFIRMED;
			}
			if(null != paramsMap.get("payStage")){
				
				payStage  = Integer.parseInt(paramsMap.get("payStage").toString());
			}
		
			
			//保存参数
			Waybill waybill = new Waybill();
			waybill.setAddress(address);
			waybill.setWaybillNo(waybillNo);
			waybill.setUpdateTime(new Date());
			waybill.setPrincipal(principal);
			waybill.setEnterpriseName(enterpriseName);
			waybill.setPrincipalPhone(principalPhone);
			waybill.setTransationPrice(transationPrice);
			waybill.setOrderStatus(orderStatus);
			waybill.setPayStage(payStage);
			//本次需支付金额
			if(null != paramsMap.get("changePrice") && !"".equals(paramsMap.get("changePrice"))){
				
				//保存预付款金额
				waybill.setChangePrice(Float.valueOf(paramsMap.get("changePrice").toString()));
			}
			waybillBaseService.updateInsuranceMes(waybill);
			logger.info("update orderStatus="+orderStatus+",waybillNo="+waybillNo);
			logger.info("WaybillController-updateInsuranceMes-params:"+g.toJson(waybill));
			// 推送所需参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybillNo", waybillNo);
			model.put("userType", DataConstants.LOGISTICS_COMPANY);
			Long endTime = System.currentTimeMillis();
			logger.info("updateInsuranceMes cost time="+(endTime-startTime));
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
	 * 
	 * <p>上传保险照片</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月1日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadWaybillInsurance.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadWaybillInsurance(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			// 准备修改数据
			String insurancePhoto = PsiAttachmentUtil.uploadImg(request, paramsMap.get("loginUserName").toString(), false, false, "WAYBILL_INSURANCE_PHOTO");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("insurancePhoto", insurancePhoto);
			params.put("waybillNo", paramsMap.get("waybillNo").toString());
			params.put("updateUser", paramsMap.get("loginUserName").toString());
			params.put("updateTime", new Date());
			waybillBaseService.uploadWaybillSafeImg(params);
			// 返回数据
			model.put("waybillNo", paramsMap.get("waybillNo").toString());
			model.put("insurancePhoto", insurancePhoto);
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * 
	 * <p>上传运单照片</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月1日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadWaybillImgs.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadWaybillImgs(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			// 准备修改数据
			String protocolPhotos = PsiAttachmentUtil.uploadImg(request, paramsMap.get("loginUserName").toString(), false, false, "WAYBILL_PROTOCOL_PHOTO");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("protocolPhoto", protocolPhotos);
			params.put("waybillNo", paramsMap.get("waybillNo").toString());
			params.put("updateUser", paramsMap.get("loginUserName").toString());
			params.put("updateTime", new Date());
			waybillBaseService.uploadWaybillImgs(params);
			// 返回数据
			model.put("waybillNo", paramsMap.get("waybillNo").toString());
			model.put("protocolPhoto", protocolPhotos);
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * 
	 * <p>物流公司确认收货</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月1日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateWaybillOrderStatus.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateWaybillOrderStatus(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			// 校验参数
			String updateUser = CommonUtils.checkParams("updateUser", paramsMap.get("updateUser"), model);
			String waybillNo = CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put("updateUser", updateUser);
			params.put("waybillNo", waybillNo);
			params.put("updateTime", new Date());
			params.put("receiveTime", new Date());
			params.put("orderStatus", OrderStatus.NO_PAYMENT_RECEIVED);
			waybillBaseService.updateWaybillOrderStatus(params);
			
			

			//对运单车辆进行监控
			WaybillVO waybillVo = waybillBaseService.getWaybillInfo(waybillNo);
			if(waybillVo != null){
				logger.info("begin to location driver,waybillNo="+waybillNo+",phone="+waybillVo.getHaulierPhone());
				LocationResult locationResult = lbsService.sendLocation(
						PropertiesSource.getProperty("companyid"),
						PropertiesSource.getProperty("companypwd"), 
						waybillVo.getHaulierPhone(), 
						DataConstants.LBS_LOCATION_OPERTYPE_N);
				logger.info("locationResult="+g.toJson(locationResult));
				
				if(DataConstants.LBS_RESULT_SUCCESS.equals(locationResult.getResult())){
					//insert pisp_driver_route_rec
					DriverRouteRec rec = new DriverRouteRec();
					rec.setMobile(waybillVo.getHaulierPhone());
					rec.setDriver(waybillVo.getHaulier());
					rec.setWaybillNo(waybillNo);
					rec.setLatitude(locationResult.getY());
					rec.setLongitude(locationResult.getX());
					rec.setLocationName(locationResult.getAddress());
					rec.setLocateTime(DateUtils.parse(locationResult.getTimestamp(),"yyyy-MM-dd HH:mm:ss"));
					driverRouteRecService.saveDriverRouteRec(rec);
					logger.info(" save  driverRouteRec  ok !");
				}
			}
			
			
			// 推送所需参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybillNo", waybillNo);
			model.put("userType", DataConstants.LOGISTICS_COMPANY);
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
	 * 
	 * <p>获取上传保险及合同信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月4日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/queryAppDriverGrabWaybillAll.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryAppDriverGrabWaybillAll(String accessToken, String waybillNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
//			WaybillQuery queryObject = new WaybillQuery();
//			queryObject.setWaybillNo(waybillNo);
			WaybillVO waybillVO = waybillBaseService.toUploadContractAndInsurancePage(waybillNo);
			if(null != waybillVO){
				Map<String,Object> param = new HashMap<String, Object>();
				param.put("waybillNo", waybillNo);
				if(OrderStatus.NO_PAY_BEFOREHAND_MONEY.equals(waybillVO.getOrderStatus())){
					param.put("payStage", DataConstants.PAY_STAGE_YF);
				}else if(OrderStatus.NO_PAYMENT_RECEIVED.equals(waybillVO.getOrderStatus())){//中期款
					
					param.put("payStage", DataConstants.PAY_STAGE_ZQ);
				}else{//尾款
					param.put("payStage", DataConstants.PAY_STAGE_WK);
				}
				PayRecord payRecord = waybillTradeService.getWaybillTradeRec(param);
				if(payRecord !=null){//已支付
					waybillVO.setCompletedPay(true);
				}else{//未支付、支付失败
					waybillVO.setCompletedPay(false);
				}
			}
			
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("noDealWaybillDetails", waybillVO);
//			model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * <p>编辑运单的出发时间和到达时间</p>
	 * @author LiuYanliang 2016年6月14日
	 * @return
	 */
	@RequestMapping(value = "/updateWayBillTime.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateWayBillTime(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			Map<String, Object> params = new HashMap<String, Object>();
			if(paramsMap.get("startTime") != null){
				params.put("startTime", format.parse(paramsMap.get("startTime").toString()));
			}
			if(paramsMap.get("arriveTime") != null){
				params.put("arriveTime", format.parse(paramsMap.get("arriveTime").toString()));
			}
			if(paramsMap.get("waybillNo") != null){
				params.put("waybillNo", paramsMap.get("waybillNo").toString());
			}
			waybillBaseService.updateWaybillInfo(params);
			// 返回数据
			model.put("errorCode", 0);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("errorCode", 1);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	
	/**
	 * 开启运单车辆监控
	 * LiuYanliang
	 * @param request
	 * @return 
	 * 2016年11月22日 上午10:28:53
	 */
	/*@RequestMapping(value = "/startLbs.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> startLbs(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			Map<String, Object> params = new HashMap<String, Object>();
			String waybillNo = CommonUtils.checkParams("waybillNo", paramsMap.get("waybillNo"), model);
			params.put("isLbs", DataConstants.YES);
			params.put("waybillNo", waybillNo);
			//更新定位标识
			waybillBaseService.updateWaybillInfo(params);
			
			WaybillVO watbill = waybillBaseService.getWaybillInfo(waybillNo);
			if(watbill == null){
				return model;
			}
			//判断司机是否已经在白名单
			String mobile = watbill.getDriversPhone();
			logger.info("user enter startLbs,waybillN="+waybillNo+",mobile="+mobile);
			LbsOpenRec openRec  =lbsService.getLbsOpenRecByMobile(mobile);
			if(openRec == null){
				whiteListRequest(mobile,true);
				return model;
			}
			Integer isWhiteList = openRec.getIsWhiteList();
			if(isWhiteList != 1){
				//调用第三方接口，验证白名单
				MobileRegisterResult lbsResult = lbsService.sendList(
						   PropertiesSource.getProperty("companyid"), 
						   PropertiesSource.getProperty("companypwd"), 
						   mobile,
						   null, 
						   DataConstants.LBS_MOBILE_REGISTER_USERTYPE_P,
						   DataConstants.LBS_MOBILE_REGISTER_OPERTYPE_Q);
				if(DataConstants.LBS_RESULT_SUCCESS.equals(lbsResult.getResult()) && "1".equals(lbsResult.getLststate())){
					//更新白名单状态
					openRec = new LbsOpenRec();
					openRec.setMobile(mobile);
					openRec.setIsWhiteList(DataConstants.YES);
					lbsService.updateLbsOpenRec(openRec);
				}else{
					whiteListRequest(mobile,false);
				}
				return model;
			}else{
				logger.info("startLbs,mobile="+mobile+" is alreay in whiteList!");
				return model;
			}
			
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("errorCode", 1);
			}
			e.printStackTrace();
		}
		return model;
	}*/
	
	
	/**
	 * 下发白名单短信请求
	 * LiuYanliang
	 * @param mobile
	 * @param isSave 
	 * 2016年11月22日 上午11:42:01
	 *//*
	public void whiteListRequest(String mobile,boolean isSave){
		try {
		   MobileRegisterResult lbsResult = lbsService.sendList(
				   PropertiesSource.getProperty("companyid"), 
				   PropertiesSource.getProperty("companypwd"), 
				   mobile,
				   null, 
				   DataConstants.LBS_MOBILE_REGISTER_USERTYPE_P,
				   DataConstants.LBS_MOBILE_REGISTER_OPERTYPE_A);
		   logger.info("lbsResult="+g.toJson(lbsResult));
		   
		   //保存白名单记录
		   if(DataConstants.LBS_RESULT_SUCCESS.equals(lbsResult.getResult())){
				LbsOpenRec openRec = new LbsOpenRec();
				openRec.setMobile(mobile);
				openRec.setLastOpenTime(DateUtils.getCurrentTimestamp());
			   if(isSave){
					lbsService.saveLbsOpenRec(openRec);
			   }else{
					lbsService.updateLbsOpenRec(openRec); 
			   }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}*/
	/**
	 * 
	 * <p>两端进行版本比较:配货端VS找货端</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年7月26日
	 * @param accessToken
	 * @param waybillNo
	 * @return
	 */
	@RequestMapping(value = "/compareVersions.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> compareVersions(String accessToken, String waybillNo,Integer userType){
		Map<String, Object> model = new HashMap<String, Object>();
		Integer compareResult = null;
		String otherSideFunVersion = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			VersionMap versionMapForHaulier = new VersionMap();
			VersionMap versionMapForShipper = new VersionMap();
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("waybillNo", waybillNo, model);
			//查询交易用户ID
			WaybillVO queryWaybillInfoByWaynillNo = waybillBaseService.queryWaybillInfoByWaynillNo(waybillNo);
			//配货端
			WaybillVO queryLoginSessionForShipper = waybillBaseService.queryLoginSession(queryWaybillInfoByWaynillNo.getShipper());
			//抢货端
			WaybillVO queryLoginSessionForHaulier = waybillBaseService.queryLoginSession(queryWaybillInfoByWaynillNo.getHaulier());
			
			if(null != queryLoginSessionForHaulier){
				params.put("terminalVersion", queryLoginSessionForHaulier.getTerminalVersion());
				params.put("funName", "TRADE");
				versionMapForHaulier = waybillBaseService.getVersionMap(params);
				if(null == versionMapForHaulier){
					VersionMap versionMapForHaulier1 = new VersionMap();
					versionMapForHaulier1.setFunVersion("");
					versionMapForHaulier = versionMapForHaulier1;
				}
			}else{
				VersionMap versionMapForHaulier1 = new VersionMap();
				versionMapForHaulier1.setFunVersion("");
				versionMapForHaulier = versionMapForHaulier1;
			}
			
			if(null != queryLoginSessionForShipper){
				params.put("terminalVersion", queryLoginSessionForShipper.getTerminalVersion());
				params.put("funName", "TRADE");
				versionMapForShipper = waybillBaseService.getVersionMap(params);
				if(null == versionMapForShipper){
					VersionMap versionMapForShipper1 = new VersionMap();
					versionMapForShipper1.setFunVersion("");
					versionMapForShipper = versionMapForShipper1;
				}
			}else{
				VersionMap versionMapForShipper1 = new VersionMap();
				versionMapForShipper1.setFunVersion("");
				versionMapForShipper = versionMapForShipper1;
			}
				
			switch (userType) {
			//司机
			case 1:
				 if(Integer.valueOf(versionMapForHaulier.getFunVersion()) >Integer.valueOf(versionMapForShipper.getFunVersion()) ){
					 compareResult = 1;
				 }else if(Integer.valueOf(versionMapForHaulier.getFunVersion()) < Integer.valueOf(versionMapForShipper.getFunVersion())  ){
					 compareResult = -1;
				 }else if(Integer.valueOf(versionMapForHaulier.getFunVersion()) == Integer.valueOf(versionMapForShipper.getFunVersion()) ){
					 compareResult = 0;
				 }
				 otherSideFunVersion = versionMapForShipper.getFunVersion();
				break;
				//物流公司
			case 5:
				 if(Integer.valueOf(versionMapForShipper.getFunVersion()) > Integer.valueOf(versionMapForHaulier.getFunVersion()) ){
					 compareResult = 1;
				 }else if(Integer.valueOf(versionMapForShipper.getFunVersion()) < Integer.valueOf(versionMapForHaulier.getFunVersion())){
					 compareResult = -1;
				 }else if(Integer.valueOf(versionMapForShipper.getFunVersion()) == Integer.valueOf(versionMapForHaulier.getFunVersion())){
					 compareResult = 0;
				 }
				 otherSideFunVersion = versionMapForHaulier.getFunVersion();
				break;
			default:
				break;
			}
			
			model.put("code", ReturnDatas.SUCCESS_CODE);	
			model.put("compareResult", compareResult); 
			model.put("otherSideFunVersion", otherSideFunVersion); 
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
	 * 
	 * <p>提交运单保险表单</p>
	 * <p>执行流程：不更改运单状态</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年12月6日
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateInsuranceMesForDeposition.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateInsuranceMesForDeposition(HttpServletRequest request){
		logger.info("---------user enter WaybillController-updateInsuranceMes");
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			Long startTime = System.currentTimeMillis();
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			//获取参数
			String address         = paramsMap.get("address").toString();
			String waybillNo       = paramsMap.get("waybillNo").toString();
			String principal       = paramsMap.get("principal").toString();
//			Integer orderStatus    = OrderStatus.LOADING_TO_CONFIRMED;
			String enterpriseName  = paramsMap.get("enterpriseName").toString();
			String principalPhone  = paramsMap.get("principalPhone").toString();
			Double transationPrice = Double.valueOf(paramsMap.get("transationPrice").toString());
			//保存参数
			Waybill waybill = new Waybill();
			waybill.setAddress(address);
			waybill.setWaybillNo(waybillNo);
			waybill.setUpdateTime(new Date());
			waybill.setPrincipal(principal);
			waybill.setEnterpriseName(enterpriseName);
			waybill.setPrincipalPhone(principalPhone);
			waybill.setTransationPrice(transationPrice);
			waybillBaseService.updateInsuranceMes(waybill);
//			logger.info("update orderStatus="+orderStatus+",waybillNo="+waybillNo);
			logger.info("WaybillController-updateInsuranceMes-params:"+g.toJson(waybill));
			// 推送所需参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("waybillNo", waybillNo);
			model.put("userType", DataConstants.LOGISTICS_COMPANY);
			Long endTime = System.currentTimeMillis();
			logger.info("updateInsuranceMes cost time="+(endTime-startTime));
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	public static void main(String[] args){
		try {
//			String eRequestUrl = "http://api.map.baidu.com/geocoder/v2/?address=北京海淀&output=json&ak=9ZqUjBdvxyLHxdl2vnv0XhSe";
//			JSONObject  eJson = JsonUtils.strToJson(HttpUtils.sendHttpRequest(eRequestUrl, HttpUtils.RequestMethod.GET, null));
//			Gson g = new Gson();
			String str = HttpUtils.sendHttpRequest("http://localhost:9091/aspp-server/waybill/queryAppDriverGrabWaybillAll.do?accessToken=cf8918f60571a1baf5b6d373e9c1df74&userId=1092&pageNo=1", HttpUtils.RequestMethod.POST, null);
			System.out.println(str);
//			System.out.println(g.toJson(eJson));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
