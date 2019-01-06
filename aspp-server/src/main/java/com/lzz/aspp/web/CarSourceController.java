package com.lzz.aspp.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.lzz.aspp.service.CarSourceService;
import com.lzz.aspp.util.CommonUtil;
import com.lzz.aspp.util.DateUtils;
import com.lzz.aspp.vo.AsppCarSourceVO;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.DictCodes;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Browse;
import com.lzz.lsp.base.domain.Car;
import com.lzz.lsp.base.domain.CarSource;
import com.lzz.lsp.base.domain.DictVal;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.Location;
import com.lzz.lsp.base.domain.PublishInfo;
import com.lzz.lsp.base.domain.Puser;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserAuthRec;
import com.lzz.lsp.base.qo.CarSourceQuery;
import com.lzz.lsp.base.vo.CarSourceVO;
import com.lzz.lsp.base.vo.CarVO;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.core.attachment.service.AttachmentBaseService;
import com.lzz.lsp.core.auth.service.AuthBaseService;
import com.lzz.lsp.core.car.service.CarBaseService;
import com.lzz.lsp.core.carsource.service.CarSourceBaseService;
import com.lzz.lsp.core.carsource.service.impl.CarSourceSolrQuery;
import com.lzz.lsp.core.complain.service.ComplainBaseService;
import com.lzz.lsp.core.location.service.LocationBaseService;
import com.lzz.lsp.core.publish.service.PublishInfoBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lsp.util.MongoUtils;
import com.lzz.lsp.util.SecurityHelper;
import com.lzz.lton.core.io.PropertiesSource;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;
import com.lzz.ssmp.client.constant.ReturnCodes;
import com.mongodb.BasicDBObject;

/**
 * <p>车源controller</p>
 * @author Liuyl 2015年8月5日
 * @version 1.0
 * <p>注意事项： </p>
 */
@RequestMapping("/carsource")
@Controller
public class CarSourceController extends BaseController{

	private static Logger logger = Logger.getLogger(CarSourceController.class);
	@Resource(name = "carSourceService")
	private CarSourceService carSourceService;
	
	@Resource(name = "publishInfoBaseService")
	private PublishInfoBaseService publishInfoBaseService;
	
	@Resource(name = "carBaseService")
	private CarBaseService carBaseService;
	
	@Resource(name = "attachmentBaseService")
	private AttachmentBaseService attachmentBaseService;
	
	@Resource(name = "dictService")
	private DictService dictService;
	
	@Resource(name = "carSourceBaseService")
	private CarSourceBaseService carSourceBaseService;
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "authBaseService")
	private AuthBaseService authBaseService;

	@Resource(name = "locationBaseService")
	private LocationBaseService locationBaseService;
	
	@Resource(name = "carSourceSolrQuery")
	private CarSourceSolrQuery carSourceSolrQuery;
	
	@Resource(name="complainBaseService")
	private ComplainBaseService complainBaseService;

	/**
	 * <p>获取车源总数量</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param accessToken
	 * @param userId
	 * @param sourceType
	 * @return
	 */
	@RequestMapping(value = "/getCarSourceCount.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCarSourceCount(String accessToken, Integer userId,String sourceType) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("sourceType", sourceType, model);
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 查询结果
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("carSourceCount", carSourceBaseService.getCarSourcesCount(userId, sourceType));
			model.put("carCount", carBaseService.getCarCountByUserId(userId));
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
	 * <p>获取用户车源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param userId  用户id
	 * @param accessToken  访问凭证
	 * @param sourceType  车源类型 (01：本地车源 02：回程车源)
	 * @return
	 */
	@RequestMapping(value = "/getCarSources.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCarSources(String accessToken, String userId,String sourceType) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("sourceType", sourceType, model);
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 查询结果
			List<AsppCarSourceVO> carSources = carSourceService.getCarSources(Integer.valueOf(userId), sourceType);
			// 组织数据
			List<Map<String, Object>> returnList = this.returnBackCarData(carSources);
			// 参数返回
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("carSource", returnList);
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
	 * <p>获取用户可发布的车源</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param userId  用户id
	 * @param accessToken 访问凭证
	 * @param sourceType 车源类型(01：本地车源 02：回程车源)
	 * @return
	 */
	@RequestMapping(value = "/getCanPublishCars.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCanPublishCars(String accessToken, Integer userId,
			String sourceType) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("sourceType", sourceType, model);
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 查询结果
			List<CarVO> canPublishCars = carBaseService.queryCarsByUserId(userId);
		/*	for(CarVO carVo:canPublishCars){
				Integer carTypeFirstCode=carVo.getCarTypeFirstCode();
				Integer carTypeSecondCode=carVo.getCarTypeSecondCode();
				Integer carTypeThirdCode=carVo.getCarTypeThirdCode();
				String carTypeStr=null;
				if(StringUtils.isEmpty(carTypeFirstCode)){
					
				}else{
					if(StringUtils.isEmpty(carTypeSecondCode)){
					String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
					carVo.setCarTypeCode(carTypeFirstCode.toString());
					carTypeStr =TypeName1;
					}else{
						if(StringUtils.isEmpty(carTypeThirdCode)){
							String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
							String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
							carVo.setCarTypeCode(carTypeSecondCode.toString());
							carTypeStr =TypeName1+"-" +TypeName2;
						}else{
							String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
							String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
							String TypeName3 =dictService.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
							carVo.setCarTypeCode(carTypeThirdCode.toString());
							carTypeStr =TypeName1+"-" +TypeName2+"-"+TypeName3;
						}
					}
				}
				
				carVo.setCarTypeName(carTypeStr);
				
			}*/
			// 参数返回
			List<Map<String, Object>> returnList = this.returnCanPublishCarData(canPublishCars);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("publishCars", returnList);
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
	 * <p>发布车源</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/publishCarSource.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> publishCarSource(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			
			// 保存发布信息
			this.savePublishInfo(model, paramsMap);
			// 返回信息
			model.put("code", ReturnCodes.SUCCESS_CODE);
			model.put("carSourceCount", carSourceBaseService.getCarSourcesCount(Integer.valueOf(paramsMap.get("userId").toString()),
					paramsMap.get("sourceType").toString()));
			super.setSuccessFlag(model);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
			model.remove("scoreType");
		}
		return model;
	}
	
	/**
	 * <p>关闭发布的车源信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @return
	 */
	@RequestMapping(value = "/closeCarSource.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> closeCarSource(String accessToken, String publishId, String loginUserName){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("publishId", publishId, model);
			CommonUtils.checkParams("loginUserName", loginUserName, model);
			// 修改数据
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("publishIds", new String[]{publishId});
			params.put("status", "4");
			params.put("updateUser", loginUserName);
			params.put("updateTime", new Date());
			params.put("displayTime", new Date());
//			carSourceSolrQuery.deleteByPublishId(publishId);
			MongoUtils.updateManyCarSource(params);
			publishInfoBaseService.updatePublishData(params);
			// 返回参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			setSuccessFlag(model);
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
	 * <p>车源列表详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月6日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/carSourceList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> carSourceList(String accessToken, Integer pageNo, 
			Integer fromPCode, Integer fromCCode, Integer fromTCode,
			Integer toPCode, Integer toCCode, Integer toTCode, Integer userId, 
			String keywords, String sourceType,Integer  carTypeFirstCode,Integer  carTypeSecondCode, Integer carTypeThirdCode,String carLength ,boolean newVersion){
				
		  Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			CarSourceQuery queryObject = new CarSourceQuery();
			
			queryObject.setPageNo(pageNo);
			queryObject.setFromPCode(fromPCode);
			queryObject.setFromCCode(fromCCode);
			queryObject.setFromTCode(fromTCode);
			queryObject.setToPCode(toPCode);
			queryObject.setToCCode(toCCode);
			queryObject.setToTCode(toTCode);
			queryObject.setCarLength(carLength);
			queryObject.setCarTypeFirstCode(carTypeFirstCode);
			queryObject.setCarTypeSecondCode(carTypeSecondCode);
			queryObject.setCarTypeThirdCode(carTypeThirdCode);
			queryObject.setKeywords(keywords);
			queryObject.setSourceType(sourceType);
			queryObject.setUserId(userId);
			queryObject.setNewVersion(newVersion);
			
//			carSourceSolrQuery.queryCarSources(queryObject, AsppCarSourceVO.class);
			//Mongo查询车源列表
			logger.info("========queryCarSources====start======old>>"+new Date());
			MongoUtils.queryCarSources(queryObject);
			logger.info("========queryCarSources====stop======old>>"+new Date());
			List<CarSourceVO> carSourceList = queryObject.getQueryList();
			List<Integer> publishInfoIds = new ArrayList<Integer>();
			List<Integer> userIds = new ArrayList<Integer>();
			for (int i = 0; i < carSourceList.size(); i++) {
				CarSourceVO vo = (CarSourceVO) carSourceList.get(i);
				publishInfoIds.add(vo.getPublishId());
				userIds.add(vo.getUserId());
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("publishInfoIds", publishInfoIds);
			List<CarSourceVO> returnData = new ArrayList<CarSourceVO>();
	
			if(publishInfoIds.size() > 0){

				List<AsppCarSourceVO> vos = carSourceService.getCarSourceIsFavorite(map);

				for (int i = 0; i < carSourceList.size(); i++) {
					CarSourceVO vo = (CarSourceVO) carSourceList.get(i);
					if(0 == vo.getFavoriteId()){
						vo.setFavoriteId(null);
					}
					for (AsppCarSourceVO asppCarSourceVO : vos) {
						if(vo.getPublishId().intValue() == asppCarSourceVO.getPublishId().intValue()){
							vo.setFavoriteId(asppCarSourceVO.getFavoriteId());
							vo.setUserIDNO(CommonUtil.replaceIdCarNO(asppCarSourceVO.getUserIDNO()));
							vo.setCarNo(asppCarSourceVO.getCarNo());
							if(asppCarSourceVO.getCarLength() != 0.0){
								vo.setCarLengthName(asppCarSourceVO.getCarLength()+"米");
							}else{
								vo.setCarLengthName("");
							}
							vo.setCarTypeName(asppCarSourceVO.getCarTypeName());
							//vo.setCarLength(asppCarSourceVO.getCarLength());
							vo.setCarPhoto(asppCarSourceVO.getCarPhoto());
							if(!"".equals(asppCarSourceVO.getLoadWeight()) && null != asppCarSourceVO.getLoadWeight()){
								
								vo.setLoadWeight(asppCarSourceVO.getLoadWeight());
							}
							vo.setMark(asppCarSourceVO.getMark());
							if(null != asppCarSourceVO.getCarType() && !"".equals(asppCarSourceVO.getCarType()) && !"0".equals(asppCarSourceVO.getCarType())){
								vo.setCarType(asppCarSourceVO.getCarType());
							}else if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeFirstCode()) && StringUtils.isEmpty(asppCarSourceVO.getCarTypeSecondCode()) && StringUtils.isEmpty(asppCarSourceVO.getCarTypeThirdCode()) && StringUtils.isEmpty(asppCarSourceVO.getDefineCarType())){
								vo.setCarType(asppCarSourceVO.getCAR_TYPE_CODE());
							}
							if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeFirstCode())){
								
							}else{
								if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeSecondCode())){
									vo.setCarType(asppCarSourceVO.getCarTypeFirstCode().toString());
								}else{
									if(StringUtils.isEmpty(carTypeThirdCode)){
										vo.setCarType(asppCarSourceVO.getCarTypeSecondCode().toString());
									}else{
										vo.setCarType(asppCarSourceVO.getCarTypeThirdCode().toString());
									}
								}
							}
							if(!StringUtils.isEmpty(asppCarSourceVO.getReturnBackDate())){
							    
							    vo.setReturnBackDate(java.sql.Date.valueOf(asppCarSourceVO.getReturnBackDate()));
							}
							if(null != userId && userId.intValue() == asppCarSourceVO.getUserId().intValue()){
								vo.setIsComplain(2);
							} else {
								vo.setIsComplain(complainBaseService.judgeComplain(userId, asppCarSourceVO.getInfoId(), asppCarSourceVO.getInfoType()) ? DataConstants.YES : DataConstants.NO);
							}
							break;
						}
					}
					vo.setCarSourceId(vo.getId());
					vo.setLinkMobile(vo.getLinkMobile1());
					User user = userBaseService.getUser(vo.getUserId());
					// 获取发布人姓名
					this.getPublishUserNameForMongo(vo, user);
					returnData.add(vo);
				}
			}
//			List carSourceList = queryObject.getQueryList();
//			List<Integer> publishInfoIds = new ArrayList<Integer>();
//			for (int i = 0; i < carSourceList.size(); i++) {
//				AsppCarSourceVO vo = (AsppCarSourceVO) carSourceList.get(i);
//				publishInfoIds.add(vo.getPublishId());
//			}
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("userId", userId);
//			map.put("publishInfoIds", publishInfoIds);
//			List<AsppCarSourceVO> returnData = new ArrayList<AsppCarSourceVO>();
//			if(publishInfoIds.size() > 0){
//				List<AsppCarSourceVO> vos = carSourceService.getCarSourceIsFavorite(map);
//				for (int i = 0; i < carSourceList.size(); i++) {
//					AsppCarSourceVO vo = (AsppCarSourceVO) carSourceList.get(i);
//					for (AsppCarSourceVO asppCarSourceVO : vos) {
//						if(vo.getPublishId().intValue() == asppCarSourceVO.getPublishId().intValue()){
//							vo.setFavoriteId(asppCarSourceVO.getFavoriteId());
//							vo.setUserIDNO(CommonUtil.replaceIdCarNO(asppCarSourceVO.getUserIDNO()));
//							vo.setCarNo(asppCarSourceVO.getCarNo());
//							if(asppCarSourceVO.getCarLength() != 0.0){
//								vo.setCarLengthName(asppCarSourceVO.getCarLength()+"米");
//							}else{
//								vo.setCarLengthName("");
//							}
//							vo.setCarTypeName(asppCarSourceVO.getCarTypeName());
//							//vo.setCarLength(asppCarSourceVO.getCarLength());
//							vo.setCarPhoto(asppCarSourceVO.getCarPhoto());
//							vo.setLoadWeight(asppCarSourceVO.getLoadWeight());
//							vo.setMark(asppCarSourceVO.getMark());
//							if(null != asppCarSourceVO.getCarType() && !"".equals(asppCarSourceVO.getCarType()) && !"0".equals(asppCarSourceVO.getCarType())){
//								vo.setCarType(asppCarSourceVO.getCarType());
//							}else if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeFirstCode()) && StringUtils.isEmpty(asppCarSourceVO.getCarTypeSecondCode()) && StringUtils.isEmpty(asppCarSourceVO.getCarTypeThirdCode()) && StringUtils.isEmpty(asppCarSourceVO.getDefineCarType())){
//								vo.setCarType(asppCarSourceVO.getCAR_TYPE_CODE());
//							}
//							if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeFirstCode())){
//								
//							}else{
//								if(StringUtils.isEmpty(asppCarSourceVO.getCarTypeSecondCode())){
//									vo.setCarType(asppCarSourceVO.getCarTypeFirstCode().toString());
//								}else{
//									if(StringUtils.isEmpty(carTypeThirdCode)){
//										vo.setCarType(asppCarSourceVO.getCarTypeSecondCode().toString());
//									}else{
//										vo.setCarType(asppCarSourceVO.getCarTypeThirdCode().toString());
//									}
//								}
//							}
//							
//							vo.setReturnBackDate(asppCarSourceVO.getReturnBackDate());
//							if(null != userId && userId.intValue() == asppCarSourceVO.getUserId().intValue()){
//								vo.setIsComplain(2);
//							} else {
//								vo.setIsComplain(complainBaseService.judgeComplain(userId, asppCarSourceVO.getInfoId(), asppCarSourceVO.getInfoType()) ? DataConstants.YES : DataConstants.NO);
//							}
//							break;
//						}
//					}
//					User user = userBaseService.getUser(vo.getPublishUserId());
//					// 获取发布人姓名
//					this.getPublishUserName(vo, user);
//					returnData.add(vo);
//				}
//			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("carSources", returnData);
			model.put("carSourcesCount", publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_CAR_SOURCE));
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
	 * <p>删除车源</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月10日
	 * @param accessToken
	 * @param publishId
	 * @return
	 */
	@RequestMapping(value = "/deletePublishInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> deletePublishInfo(String accessToken, String publishId, String loginUserName){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("publishId", publishId, model);
			CommonUtils.checkParams("loginUserName", loginUserName, model);
			// 修改数据
			Map<String, Object> params = new HashMap<String, Object>();
			String[] pids = publishId.split(",");
			params.put("publishIds", pids);
			params.put("isDelete", DataConstants.YES);
			params.put("updateUser", loginUserName);
			params.put("updateTime", new Date());
			for (int i = 0; i < pids.length; i++) {
				BasicDBObject bdj = new BasicDBObject();
				bdj.append("publishId", Integer.valueOf(pids[i]));
				MongoUtils.deleteManyCarSource(bdj,params);
			}
			publishInfoBaseService.updatePublishData(params);
			//拼接Mongo删除条件
//			BasicDBObject query = new BasicDBObject();
//			query.put("publishId", Integer.valueOf(publishId));
//			MongoUtils.deleteCarSource(query);
//			carSourceSolrQuery.deleteByPublishId(publishId);
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * <p>获取发布人姓名</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月6日
	 * @param carSourceVO
	 * @param user
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	private void getPublishUserName(AsppCarSourceVO carSourceVO, User user) throws Exception {
		if(user==null)throw new Exception("user is empty,please check your parameters! May be the problem of Solr ...");
		if(DataConstants.DRIVER == user.getUserType()
				|| DataConstants.P_GOODS_OWNER == user.getUserType()){
			Puser puser = userBaseService.getPuser(user.getUserId());
			if(null != puser){
				carSourceVO.setUserPhoto(user.getHeadImage());
				// 联系人名称
				carSourceVO.setPublishUserName(carSourceVO.getLinkMan());
				// 驾龄
				carSourceVO.setDriving(DateUtils.getDifferYear(puser.getDrivingLicenceApplyTime(), new Date()).toString());
				UserAuthRec userAuth = authBaseService.getUserLastAuthRec(puser.getUserId());
				// 认证信息   身份证 or 驾驶证
				if(null != userAuth && DataConstants.AUTH_STATUS_PASS == userAuth.getAuthStatus()){
					carSourceVO.setUserIDNO(CommonUtil.replaceIdCarNO(puser.getIdCardNo()));
					carSourceVO.setDrivingLicence("已上传");
					carSourceVO.setIsAuth(1);
				} else {
					carSourceVO.setUserIDNO("未实名认证");
					carSourceVO.setDrivingLicence("未上传");
					carSourceVO.setIsAuth(0);
				}
				// 位置信息
				Location location = locationBaseService.getLocation(puser.getUserId());
				if(null != location){
					carSourceVO.setPosition(location.getLocationName());
					carSourceVO.setPositionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(location.getLocateTime()));
					carSourceVO.setLongitude(location.getLongitude());
					carSourceVO.setLatitude(location.getLatitude());
					carSourceVO.setPositionUrl("/base/service/toMapPoi.do?location=" 
								+ location.getLongitude() + "," + location.getLatitude()
								+ "&poi=" + location.getLocationName());
					
				} else {
					carSourceVO.setPosition("未开启定位");
					carSourceVO.setPositionTime("未开启定位");
				}
			}
		} else {
			Euser euser = userBaseService.getEuser(user.getUserId());
			if(null != euser){
				// 发布人姓名
				carSourceVO.setPublishUserName(carSourceVO.getLinkMan() + "-" +euser.getEnterpriseName());
				UserAuthRec userAuth = authBaseService.getUserLastAuthRec(euser.getUserId());
				// 认证信息   身份证 or 驾驶证
				if(null != userAuth && DataConstants.AUTH_STATUS_PASS == userAuth.getAuthStatus()){
					carSourceVO.setUserIDNO(CommonUtil.replaceIdCarNO(euser.getIdcardNo()));
					carSourceVO.setIsAuth(1);
				} else {
					carSourceVO.setUserIDNO("未实名认证");
					carSourceVO.setIsAuth(0);
				}
				carSourceVO.setUserPhoto(euser.getEnterprisePhoto());
			}
		}
	}
	
	
	/**
	 * 
	 * <p>获取发布人姓名</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年9月13日
	 * @param vo
	 * @param user
	 * @throws Exception
	 */
	private void getPublishUserNameForMongo(CarSourceVO vo, User user) throws Exception {
		if(user==null)throw new Exception("user is empty,please check your parameters! May be the problem of Mongo ...");
		if(DataConstants.DRIVER == user.getUserType()
				|| DataConstants.P_GOODS_OWNER == user.getUserType()){
			Puser puser = userBaseService.getPuser(user.getUserId());
			if(null != puser){
				vo.setUserPhoto(user.getHeadImage());
				// 联系人名称
				vo.setPublishUserName(vo.getLinkMan());
				// 驾龄
				vo.setDriving(DateUtils.getDifferYear(puser.getDrivingLicenceApplyTime(), new Date()).toString());
				UserAuthRec userAuth = authBaseService.getUserLastAuthRec(puser.getUserId());
				// 认证信息   身份证 or 驾驶证
				if(null != userAuth && DataConstants.AUTH_STATUS_PASS == userAuth.getAuthStatus()){
					vo.setUserIDNO(CommonUtil.replaceIdCarNO(puser.getIdCardNo()));
					vo.setDrivingLicence("已上传");
					vo.setIsAuth(1);
				} else {
					vo.setUserIDNO("未实名认证");
					vo.setDrivingLicence("未上传");
					vo.setIsAuth(0);
				}
				// 位置信息
				Location location = locationBaseService.getLocation(puser.getUserId());
				if(null != location){
					vo.setPosition(location.getLocationName());
					vo.setPositionTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(location.getLocateTime()));
					if(!StringUtils.isEmpty(location.getLongitude())){
						
						vo.setLongitude(Float.valueOf(location.getLongitude()));
					}
					if(!StringUtils.isEmpty(location.getLatitude())){
						
						vo.setLatitude(Float.valueOf(location.getLatitude()));
					}
					vo.setPositionUrl("/base/service/toMapPoi.do?location=" 
								+ location.getLongitude() + "," + location.getLatitude()
								+ "&poi=" + location.getLocationName());
					
				} else {
					vo.setPosition("未开启定位");
					vo.setPositionTime("未开启定位");
				}
			}
		} else {
			Euser euser = userBaseService.getEuser(user.getUserId());
			if(null != euser){
				// 发布人姓名
				vo.setPublishUserName(vo.getLinkMan() + "-" +euser.getEnterpriseName());
				UserAuthRec userAuth = authBaseService.getUserLastAuthRec(euser.getUserId());
				// 认证信息   身份证 or 驾驶证
				if(null != userAuth && DataConstants.AUTH_STATUS_PASS == userAuth.getAuthStatus()){
					vo.setUserIDNO(CommonUtil.replaceIdCarNO(euser.getIdcardNo()));
					vo.setIsAuth(1);
				} else {
					vo.setUserIDNO("未实名认证");
					vo.setIsAuth(0);
				}
				vo.setUserPhoto(euser.getEnterprisePhoto());
			}
		}
	}
	
	
	/**
	 * <p>组织可以发布车源 的车辆信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param canPublishCars
	 * @return
	 * @throws Exception
	 */
	private List<Map<String, Object>> returnCanPublishCarData(List<CarVO> canPublishCars) throws Exception {
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		try {
			for (CarVO carVO : canPublishCars) {
				Map<String,Object> map = new HashMap<String, Object>();
				// 车辆id
				map.put("id", carVO.getId());
				// 随车司机
				map.put("driver", carVO.getDriver());
				// 车牌号
				map.put("carNo", carVO.getCarNo());
				// 车长
				map.put("carLength", carVO.getCarLength());
				// 载重
				map.put("loadWeight", carVO.getLoadWeight());
				// 车辆图片
				map.put("carPhoto", carVO.getCarPhoto());
				// 显示图片id
				map.put("showCarPhotIds", CommonUtil.changeStringToList(carVO.getCarPhoto()));
				// 随车电话 
				map.put("driverPhone", carVO.getDriverPhone());
				if(null != carVO.getValName() && !"".equals(carVO.getValName())){
					map.put("carTypeName", carVO.getValName());
				}else{
				// 车型
				map.put("carTypeName", carVO.getCarTypeName());
				}
				
				// 车型code
				map.put("carTypeCode", "car_"+carVO.getCarTypeCode());
				// 车牌号第一段code
				map.put("firstNoCode", carVO.getFirstNoCode());
				//自定义车型
				map.put("defineCarType", carVO.getDefineCarType());
				returnList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnList;
	}
	
	/**
	 * <p>组织车源列表数据</p>
	 * @author Liuyl 2015年8月5日
	 * @param carSources
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> returnBackCarData(List<AsppCarSourceVO> carSources) throws Exception {
		List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
		try {
			for (AsppCarSourceVO vo : carSources) {
				Map<String, Object> map = new HashMap<String, Object>();
				Integer carId = vo.getCarId();
				// 获取车辆信息
				Car car = carBaseService.getCar(carId);
				// 回程车ID
				map.put("carSourceId", vo.getCarSourceId());
				// 出发地
				map.put("fromPlace", vo.getFromPlace());
				// 目的地
				map.put("toPlace", vo.getToPlace());
				// 出发地的省编码
				map.put("fromPCode", vo.getFromPCode());
				// 出发地的市编码
				map.put("fromCCode", vo.getFromCCode());
				map.put("fromTCode", vo.getFromTCode());
				// 目的地的省编码
				map.put("toPCode", vo.getToPCode());
				// 目的地的市编码
				map.put("toCCode", vo.getToCCode());
				map.put("toTCode", vo.getToTCode());
				// 车辆ID
				map.put("carId", carId);
				// 回程日期
				map.put("returnBackDate", vo.getReturnBackDate());
				// 车牌号
				map.put("carNo", vo.getCarNo());
				// 常用语
				map.put("commonPhrase", vo.getCommonPhrase());
				// 说明
				map.put("infoContent", vo.getInfoContent());
				// 联系人
				map.put("linkMan", vo.getLinkMan());
				// 联系人手机
				map.put("linkMobile", vo.getLinkMobile());
				// 联系人QQ
				map.put("linkQQ", vo.getLinkQQ());
				// 用户id
				map.put("userId", vo.getUserId());
				// 信息类型（1:货源 2:车源 3:整车专线4：零担专线）
				map.put("infoType", vo.getInfoType());
				// 有效日期
				map.put("expiryDate", vo.getExpiryDate());
				// 信息状态
				map.put("status", vo.getStatus());
				// 发布时间
				map.put("publishTime", vo.getPublishTime());
				// 显示日期
				map.put("displayTime", vo.getDisplayTime());
				// 发布id
				map.put("publishId", vo.getPublishId());
				//自定义车型
				map.put("defineCarType", vo.getDefineCarType());
				// 车辆图片
				map.put("carPhoto", car.getCarPhoto());
				// 车辆图片地址
				map.put("showCarPhotIds", CommonUtil.changeStringToList(car.getCarPhoto()));
				// 车型
				if(null != car.getCarType() && !"".equals(car.getCarType()) && !"0".equals(car.getCarType())){
					
					map.put("carType", "car_" + car.getCarType());
				}
				// 车型名称
				String carType = car.getCarType();
				if(!StringUtils.isEmpty(carType)){
					DictVal valCode = dictService.getDictValByDictCodeAndValCode(DictCodes.CAR_TYPE, carType);
					if(!StringUtils.isEmpty(valCode)){
						String valName = valCode.getValName();
						map.put("carTypeName", valName);
					}else{
						map.put("carTypeName", "");
					}
				}if(!StringUtils.isEmpty(car.getCarTypeFirstCode())){
					Integer carTypeFirstCode=car.getCarTypeFirstCode();
					Integer carTypeSecondCode=car.getCarTypeSecondCode();
					Integer carTypeThirdCode=car.getCarTypeThirdCode();
					String carTypeStr=null;
					if(StringUtils.isEmpty(carTypeFirstCode)){
						
					}else{
						if(StringUtils.isEmpty(carTypeSecondCode)){
						String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
						car.setCarTypeCode(carTypeFirstCode.toString());
						map.put("carType", "car_" + carTypeFirstCode.toString());
						carTypeStr =TypeName1;
						}else{
							if(StringUtils.isEmpty(carTypeThirdCode)){
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								car.setCarTypeCode(carTypeSecondCode.toString());
								map.put("carType", "car_" +carTypeSecondCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2;
							}else{
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String TypeName3 =dictService.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
								car.setCarTypeCode(carTypeThirdCode.toString());
								map.put("carType", "car_" +carTypeThirdCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2+"-"+TypeName3;
							}
						}
					}
					
					map.put("carTypeName", carTypeStr);
				}else{
					map.put("carTypeName", null);
				}
				// 车长
				map.put("carLength", car.getCarLength());
				// 载重
				map.put("loadWeight", car.getLoadWeight());
				
				returnList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnList;
	}
	

	
	/**
	 * <p>保存车源发布信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月5日
	 * @param model
	 * @param paramsMap
	 * @throws Exception
	 */
	private void savePublishInfo(Map<String, Object> model,Map<String, Object> paramsMap) throws Exception {
		try {
			// 验证参数
			String loginUserName = CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			String linkMobile1 = CommonUtils.checkParams("linkMobile1", paramsMap.get("linkMobile1"), model);
			// 保存Publish发布信息
			String notNullStr = "userId,status";
			PublishInfo publishInfo = (PublishInfo) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), PublishInfo.class, notNullStr, model);
			if(null != publishInfo.getPublishId()){
				publishInfo.setPublishId(publishInfo.getPublishId());
				// 修改时间
				publishInfo.setUpdateTime(new Date());
				// 修改人
				publishInfo.setUpdateUser(loginUserName);
			} else {
				// 创建人
				publishInfo.setCreateUser(loginUserName);
				// 创建时间
				publishInfo.setCreateTime(new Date());
				publishInfo.setPublishFreq(0);
				publishInfo.setRepateTimes(0);
				publishInfo.setSurplusTimes(0);
				// 增加积分
				model.put("userId", publishInfo.getUserId());
				model.put("userType", DataConstants.LOGISTICS_COMPANY);
				model.put("scoreType", DataConstants.INTERCEPT_TYPE_PUBLISH);
				
			}
			// 保存carSource信息
			String carSourceNotNullStr = "fromPlace,toPlace,fromPCode,fromCCode,toPCode,toCCode,carId,sourceType,infoContent";
			CarSource carSource = (CarSource) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), CarSource.class, carSourceNotNullStr, model);
			if(DataConstants.CAR_SOURCE_BACKHAUL.equals(carSource.getSourceType())){
				carSource.setReturnBackDate(new SimpleDateFormat("yyyy-MM-dd").parse(CommonUtils.checkParams("returnBackDate", paramsMap.get("returnBackDate"), model)));
			}
			carSource.setLinkMobile1(linkMobile1);
			Euser euser = userBaseService.getEuser(publishInfo.getUserId());
			if(null != euser){
				carSource.setPublisher(euser.getEnterpriseName());
			} else {
				carSource.setPublisher(loginUserName);
			}
			if(null != carSource.getCarSourceId()){
				carSource.setCarSourceId(carSource.getCarSourceId());
				// 修改时间
				carSource.setUpdateTime(new Date());
				// 修改人
				carSource.setUpdateUser(loginUserName);
			} else {
				// 创建人
				carSource.setCreateUser(loginUserName);
				// 创建时间
				carSource.setCreateTime(new Date());
			}
			// 发布时间
			publishInfo.setIsDelete(0);
			publishInfo.setIsStruct(1);
			publishInfo.setPublishTime(new Date());
			publishInfo.setCarSourceInfo(carSource);
			publishInfo.setInfoType(DataConstants.INFO_TYPE_CAR_SOURCE);
			publishInfo.setBackstageWrite(DataConstants.NO);
			publishInfoBaseService.publishInfo(publishInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * <p>获取车源列表(新版本)</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月17日
	 * @param accessToken
	 * @param pageNo
	 * @param fromPCode
	 * @param fromCCode
	 * @param fromTCode
	 * @param toPCode
	 * @param toCCode
	 * @param toTCode
	 * @param userId
	 * @param keywords
	 * @param sourceType
	 * @param newVersion
	 * @return
	 */
	@RequestMapping(value = "/carSourceList_mongo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> carSourceList_mongo(String accessToken, Integer pageNo, 
			Integer fromPCode, Integer fromCCode, Integer fromTCode,
			Integer toPCode, Integer toCCode, Integer toTCode, Integer userId,String sourceType,String carLength,Integer  carTypeFirstCode,Integer  carTypeSecondCode, Integer carTypeThirdCode){
				
		  Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			CarSourceQuery queryObject = new CarSourceQuery();
			
			queryObject.setPageNo(pageNo);
			queryObject.setFromPCode(fromPCode);
			queryObject.setFromCCode(fromCCode);
			queryObject.setFromTCode(fromTCode);
			queryObject.setToPCode(toPCode);
			queryObject.setToCCode(toCCode);
			queryObject.setToTCode(toTCode);
			queryObject.setCarLength(carLength);
			queryObject.setCarTypeFirstCode(carTypeFirstCode);
			queryObject.setCarTypeSecondCode(carTypeSecondCode);
			queryObject.setCarTypeThirdCode(carTypeThirdCode);
			queryObject.setSourceType(sourceType);
			queryObject.setUserId(userId);
		
//			carSourceSolrQuery.queryCarSources(queryObject, AsppCarSourceVO.class);
			
			//Mongo查询车源列表
			logger.info("=============queryCarSources==start==>>"+new Date());
			MongoUtils.queryCarSources(queryObject);
			logger.info("=============queryCarSources==stop==>>"+new Date());
			List<CarSourceVO> carSourceList = queryObject.getQueryList();
			for (CarSourceVO carSourceVO : carSourceList) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("carId", carSourceVO.getCarId());
				carSourceVO.setLinkMobile(carSourceVO.getLinkMobile1());
				AsppCarSourceVO carSourcePhotos = carSourceService.getCarSourcePhotos(map);
				if (null != carSourcePhotos) {
					if (!StringUtils.isEmpty(carSourcePhotos.getCarPhoto())) {
					carSourceVO.setCarPhoto(carSourcePhotos.getCarPhoto());
					}
					if (!StringUtils.isEmpty(carSourcePhotos.getDrivingLicensePhoto())) {
						carSourceVO.setUserPhoto(carSourcePhotos
								.getDrivingLicensePhoto());
					}
				}
				if(StringUtils.isEmpty(carSourceVO.getCarTypeFirstCode())){
					
				}else{
					if(StringUtils.isEmpty(carSourceVO.getCarTypeSecondCode())){
						carSourceVO.setCarType(carSourceVO.getCarTypeFirstCode().toString());
					}else{
						if(StringUtils.isEmpty(carSourceVO.getCarTypeThirdCode())){
							carSourceVO.setCarType(carSourceVO.getCarTypeSecondCode().toString());
						}else{
							carSourceVO.setCarType(carSourceVO.getCarTypeThirdCode().toString());
						}
					}
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("carSources", carSourceList);
			model.put("carSourcesCount", publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_CAR_SOURCE));
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
	 * <p>获取车源信息详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月17日
	 * @param accessToken
	 * @param userId
	 * @param infoId
	 * @return
	 */
	@RequestMapping(value = "/getCarSourceInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCarSourceInfo(String accessToken,Integer userId,Integer publishId){
				
		  Map<String, Object> model = new HashMap<String, Object>();
		  Map<String, Object> map = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("publishId", publishId, model);
			if(publishId != null){
				AsppCarSourceVO carSource = carSourceService.getCarSourceContent(publishId);
				//查询该车源是否被收藏
				if(null != carSource){
					map.put("userId", userId);
					map.put("infoId", carSource.getInfoId());
					Integer carSourceFavoriteId = carSourceService.getCarSourceFavoriteId(map);
					if(null !=carSourceFavoriteId && !"".equals(carSourceFavoriteId) && 0 != carSourceFavoriteId){
						carSource.setFavoriteId(carSourceFavoriteId);
					}else{
						carSource.setFavoriteId(0);
					}
				}
				
				//车型
				if(!StringUtils.isEmpty(carSource.getDefineCarType())){
					carSource.setCarTypeName(carSource.getDefineCarType());
				}else{
					Integer carTypeFirstCode  = carSource.getCarTypeFirstCode();
					Integer carTypeSecondCode = carSource.getCarTypeSecondCode();
					Integer carTypeThirdCode  = carSource.getCarTypeThirdCode();
					if(!StringUtils.isEmpty(carTypeFirstCode)){
						if(!StringUtils.isEmpty(carTypeSecondCode)){
							if(!StringUtils.isEmpty(carTypeThirdCode)){
								String carTypeThirdName = dictService.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
								String carTypeSecondName = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String carTypeFirstName = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String carTypeNameStr = carTypeFirstName+"-"+carTypeSecondName+"-"+carTypeThirdName;
								carSource.setCarTypeName(carTypeNameStr);
							}else{
								String carTypeSecondName = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String carTypeFirstName = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String carTypeNameStr = carTypeFirstName+"-"+carTypeSecondName;
								carSource.setCarTypeName(carTypeNameStr);
							}
						}else{
							String carTypeFirstName = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
							String carTypeNameStr = carTypeFirstName;
							carSource.setCarTypeName(carTypeNameStr);
						}
					}else{
						carSource.setCarTypeName("");
					}
				}
				User user = userBaseService.getUser(carSource.getUserId());
				// 获取发布人姓名
				this.getPublishUserName(carSource, user);
				
				//获取投诉处理信息   false:未投诉; true:已投诉
				if(carSource != null){
				if(null != userId && userId.intValue() == carSource.getUserId().intValue()){
					model.put("complainResult",2);
				}else{
					boolean complainResult = complainBaseService.judgeComplain(userId,carSource.getCarSourceId(), DataConstants.INFO_TYPE_CAR_SOURCE);
					model.put("complainResult",complainResult);
				}
				
				}
				model.put("code", ReturnDatas.SUCCESS_CODE);
				model.put("data", carSource);
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
		try {
			/*Map<String, String> map = new HashMap<String, String>();
			map.put("accessToken", "cf8918f60571a1baf5b6d373e9c1df74");
			map.put("carSourceId", "64");
			map.put("publishId", "134");
			map.put("fromPlace", "haha");
			map.put("toPlace", "haha");
			map.put("fromPCode", "61");
			map.put("fromCCode", "661");
			map.put("toPCode", "81");
			map.put("toCCode", "881");
			map.put("sourceType", "02");
			map.put("returnBackDate", "2016-05-28");
			map.put("commonPhrase", "1");
			map.put("mark", "markLyl11");
			map.put("linkman", "联系人Lyl");
			map.put("linkMobile", "18600986127");
			map.put("linkQq", "942379810");
			map.put("loginUserName", "dbllyl");
			map.put("status", "3");
			map.put("carId", "66818");
			map.put("infoType", "2");
			map.put("publisher", "lylDBL");
			map.put("publishTime", "2015-05-19");
			
			map.put("userId", "110");
			*/
			String str = HttpUtils.sendHttpRequest("http://localhost:9090/aspp-server/carsource/getCarSources.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&userId=1039&sourceType=01", HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
