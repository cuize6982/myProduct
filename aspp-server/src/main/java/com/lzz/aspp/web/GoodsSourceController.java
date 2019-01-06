package com.lzz.aspp.web;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.lzz.aspp.service.GoodsSourceService;
import com.lzz.aspp.vo.AsppGoodsSourceVO;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.GoodsSource;
import com.lzz.lsp.base.domain.PublishInfo;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.qo.GoodsSourceQuery;
import com.lzz.lsp.base.vo.GoodsSourceVO;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.cmpt.district.service.DistrictService;
import com.lzz.lsp.core.complain.service.ComplainBaseService;
import com.lzz.lsp.core.goodssource.service.impl.GoodsSourceSolrQuery;
import com.lzz.lsp.core.publish.service.PublishInfoBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lsp.util.MongoUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;
import com.lzz.lton.util.StringUtils;
import com.lzz.ssmp.client.constant.ReturnCodes;
import com.mongodb.BasicDBObject;

/**
 * <p>货源controller</p>
 * @author Liuyl 2015年8月5日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/goodssource")
public class GoodsSourceController extends BaseController{
	
	private static Logger logger = Logger.getLogger(GoodsSourceController.class);
	
	@Resource(name = "districtService")
	private DistrictService districtService;
	
	@Resource(name = "publishInfoBaseService")
	private PublishInfoBaseService publishInfoBaseService;
	
	@Resource(name = "goodsSourceService")
	private GoodsSourceService goodsSourceService;
	
	@Resource(name = "dictService")
	private DictService dictService;
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "goodsSourceSolrQuery")
	private GoodsSourceSolrQuery goodsSourceSolrQuery;
	
	@Resource(name="complainBaseService")
	private ComplainBaseService complainBaseService;

	/**
	 * <p>货源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月11日
	 * @param accessToken
	 * @param pageNo
	 * @param fromPCode
	 * 
	 * @param fromCCode
	 * @param toPCode
	 * @param toCCode
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/goodsSourceList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> goodsSourceList(String accessToken,Integer pageNo, Integer fromPCode, Integer fromCCode,Integer fromTCode, 
			                          Integer toPCode, Integer toTCode, Integer toCCode, Integer userId, String keywords,String  
			                          carTypeFirstCode,String  carTypeSecondCode,String carTypeName,String carTypeThirdCode,String carLengthNeed ,boolean newVersion,Integer mongoPageNum){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 验证凭证
			CommonUtils.checkAccessToken(accessToken, model);
			//校验参数
			CommonUtils.checkParams("pageNo", pageNo, model);
			// set 查询条件
			GoodsSourceQuery queryObject = new GoodsSourceQuery();
			queryObject.setPageNo(pageNo);
			queryObject.setFromPCode(fromPCode);
			queryObject.setFromCCode(fromCCode);
			queryObject.setFromTCode(fromTCode);
			queryObject.setCarLengthNeed(carLengthNeed);
			queryObject.setKeywords(keywords);
			queryObject.setCarTypeNeedName(carTypeName);
			//System.out.println(new String(keywords.getBytes("ISO-8859-1") , "UTF-8").replace("-", "To"));//TODO
//			if(keywords!=null && keywords!=""){ 
//			 queryObject.setKeywords(keywords.replace("-", "To"));
//			//queryObject.setKeywords(new String(keywords.getBytes("ISO-8859-1") , "UTF-8").replace("-", "To"));
//			}
			queryObject.setNewVersion(newVersion);
			queryObject.setToPCode(toPCode);
			queryObject.setToCCode(toCCode);
			queryObject.setToTCode(toTCode);
			queryObject.setUserId(userId);
			queryObject.setCarTypeFirstCode(carTypeFirstCode);
			queryObject.setCarTypeSecondCode(carTypeSecondCode);
			queryObject.setCarTypeThirdCode(carTypeThirdCode);
			queryObject.setMongoPageNum(mongoPageNum);
			User presentUser = userBaseService.getUser(userId);
			Integer needSourceNum = queryObject.getPageSize();
			//返回移动端的数据list 
			List<GoodsSource> returnData = new ArrayList<GoodsSource>();
		//	queryObject.setPlaceKilometre(placeKilometre);
			// 获取信息
			MongoUtils.queryGoodsSources(queryObject,presentUser,userId,needSourceNum);
//			goodsSourceSolrQuery.queryGoodsSources(queryObject, AsppGoodsSourceVO.class);
			//从mongo获取数据
			List queryList = queryObject.getQueryList();
			List queryList1 = new ArrayList();
			while (true) {
				if(queryObject.getTotalNum() ==0){
					break;
				}
			if(queryList.size()<queryObject.getPageSize()){
				//页数+1  查询下一页
				queryObject.setPageNo(++pageNo);
				//需要查询数据条数
				needSourceNum = needSourceNum-queryList.size();
				//Mongo查询
				MongoUtils.queryGoodsSources(queryObject,presentUser,userId,needSourceNum);
				//获取Mongo查询数据
				queryList1 = queryObject.getQueryList();
			}
			//所有货源ID集合
//			List<Integer> publishInfoIds = new ArrayList<Integer>();
//			for (int i = 0; i < queryList.size(); i++) {
//				AsppGoodsSourceVO vo = (AsppGoodsSourceVO) queryList.get(i);
//				publishInfoIds.add(vo.getPublishInfoId());
//			}
			List<Integer> publishInfoIds = new ArrayList<Integer>();
			for (int i = 0; i < queryList.size(); i++) {
				GoodsSource vo = (GoodsSource) queryList.get(i);
				publishInfoIds.add(vo.getPublishId());
			}
			for (int i = 0; i < queryList1.size(); i++) {
				GoodsSource vo = (GoodsSource) queryList1.get(i);
				publishInfoIds.add(vo.getPublishId());
			}
		
			
//			if( publishInfoIds != null && 0 != publishInfoIds.size()){
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("userId", userId);
//				map.put("publishInfoIds", publishInfoIds);
//				//获取货源是否能收藏
//				List<AsppGoodsSourceVO> vos = goodsSourceService.getGoodsIsFavorite(map);
//				//遍历solr货源数据
//				for (int i = 0; i < queryList.size(); i++) {
//					//获取一条solr货源数据
//					AsppGoodsSourceVO vo = (AsppGoodsSourceVO) queryList.get(i);
//					for (AsppGoodsSourceVO asppGoodsSourceVO : vos) {
//						if(vo.getPublishInfoId().intValue() == asppGoodsSourceVO.getPublishInfoId().intValue()){
//							vo.setFavoriteId(asppGoodsSourceVO.getFavoriteId());
//							vo.setEnterpriseName(asppGoodsSourceVO.getEnterpriseName());
//				            if(null != userId && userId.intValue() == asppGoodsSourceVO.getUserId().intValue()){
//				            	vo.setIsComplain(2);
//				            } else {
//				            	vo.setIsComplain(complainBaseService.judgeComplain(userId, asppGoodsSourceVO.getInfoId(), asppGoodsSourceVO.getInfoType()) ? DataConstants.YES : DataConstants.NO );
//				            }
//							break;
//						}
//					}
//					//往solr查询出的货源数据里set必要的用户信息
//					User user = userBaseService.getUser(vo.getUserId());
//					Euser euser = this.userBaseService.getEuser(vo.getUserId());
//					vo.setUserPhoto(null != euser ? euser.getEnterprisePhoto() : null);
//					vo.setIsAuth(null != user ? user.getIsAuth() : 0);
//					vo.setGoodsClass(StringUtils.isBlank(vo.getGoodsClass()) ? "goods_common" : "goods_" + vo.getGoodsClass());
//					vo.setUserGrad(user!=null ? user.getUserGrad():null);
//					returnData.add(vo);
//				}
//			}
			if( publishInfoIds != null && 0 != publishInfoIds.size()){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", userId);
				map.put("publishInfoIds", publishInfoIds);
				//获取货源是否能收藏
				List<AsppGoodsSourceVO> vos = goodsSourceService.getGoodsIsFavorite(map);
				//遍历solr货源数据
				
				for (int i = 0; i < queryList.size(); i++) {
					//获取一条mongo货源数据
					GoodsSource vo = (GoodsSource) queryList.get(i);
					if(null != vo.getMongoPageNum() && !"".equals(vo.getMongoPageNum())){
						mongoPageNum = vo.getMongoPageNum();
					}else{
					for (AsppGoodsSourceVO asppGoodsSourceVO : vos) {
						if(vo.getPublishId().intValue() == asppGoodsSourceVO.getPublishInfoId().intValue()){
							vo.setFavoriteId(asppGoodsSourceVO.getFavoriteId());
							vo.setEnterpriseName(asppGoodsSourceVO.getEnterpriseName());
				            if(null != userId && userId.intValue() == asppGoodsSourceVO.getUserId().intValue()){
				            	vo.setIsComplain(2);
				            } else {
				            	vo.setIsComplain(complainBaseService.judgeComplain(userId, asppGoodsSourceVO.getInfoId(), asppGoodsSourceVO.getInfoType()) ? DataConstants.YES : DataConstants.NO );
				            }
							break;
						}
					}
					}
					//往solr查询出的货源数据里set必要的用户信息
					User user = userBaseService.getUser(vo.getUserId());
					Euser euser = this.userBaseService.getEuser(vo.getUserId());
					vo.setUserPhoto(null != euser ? euser.getEnterprisePhoto() : null);
					vo.setIsAuth(null != user ? user.getIsAuth() : 0);
					vo.setGoodsClass(StringUtils.isBlank(vo.getGoodsClass()) ? "goods_common" : "goods_" + vo.getGoodsClass());
					vo.setUserGrad(user!=null ? user.getUserGrad():null);

					returnData.add(vo);
				}
				if(0 != queryObject.getTotalPageNum() && queryObject.getPageNo()-1 < queryObject.getTotalPageNum()){
				for (int i = 0; i < queryList1.size(); i++) {
					//获取一条mongo货源数据
					GoodsSource vo = (GoodsSource) queryList1.get(i);
					if(null != vo.getMongoPageNum() && !"".equals(vo.getMongoPageNum())){
						mongoPageNum = vo.getMongoPageNum();
						break;
					}else{
						
						for (AsppGoodsSourceVO asppGoodsSourceVO : vos) {
							if(vo.getPublishId().intValue() == asppGoodsSourceVO.getPublishInfoId().intValue()){
								vo.setFavoriteId(asppGoodsSourceVO.getFavoriteId());
								vo.setEnterpriseName(asppGoodsSourceVO.getEnterpriseName());
					            if(null != userId && userId.intValue() == asppGoodsSourceVO.getUserId().intValue()){
					            	vo.setIsComplain(2);
					            } else {
					            	vo.setIsComplain(complainBaseService.judgeComplain(userId, asppGoodsSourceVO.getInfoId(), asppGoodsSourceVO.getInfoType()) ? DataConstants.YES : DataConstants.NO );
					            }
								break;
							}
						}
					}
					//往solr查询出的货源数据里set必要的用户信息
					User user = userBaseService.getUser(vo.getUserId());
					Euser euser = this.userBaseService.getEuser(vo.getUserId());
					vo.setUserPhoto(null != euser ? euser.getEnterprisePhoto() : null);
					vo.setIsAuth(null != user ? user.getIsAuth() : 0);
					vo.setGoodsClass(StringUtils.isBlank(vo.getGoodsClass()) ? "goods_common" : "goods_" + vo.getGoodsClass());
					vo.setUserGrad(user!=null ? user.getUserGrad():null);

					returnData.add(vo);
					}
				}else{
					break;	
				}
		}
				if(returnData.size()>=queryObject.getPageSize()){
					break;
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("goodsSources", returnData);
			model.put("goodsSourcesCount", publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_GOODS_SOURCE));
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("mongoPageNum", queryObject.getMongoPageNum());
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
	 * <p>发布货源列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月11日
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @param fromPCode
	 * @param fromCCode
	 * @param toPCode
	 * @param toCCode
	 * @return
	 */
	@RequestMapping(value = "/getPublishGoodsSource.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPublishGoodsSource(String accessToken, Integer userId,
			Integer pageNo, Integer fromPCode, Integer fromCCode, Integer fromTCode, 
			Integer toPCode, Integer toCCode, Integer toTCode){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 验证凭证
			CommonUtils.checkAccessToken(accessToken, model);
			//校验参数
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			// set 查询条件
			AsppGoodsSourceVO vo = new AsppGoodsSourceVO();
			vo.setUserId(userId);
			vo.setPageNo(pageNo);
			vo.setFromPCode(fromPCode);
			vo.setFromCCode(fromCCode);
			vo.setFromTCode(fromTCode);
			vo.setToPCode(toPCode);
			vo.setToCCode(toCCode);
			vo.setToTCode(toTCode);
			// 获取信息
			model.put("code", ReturnDatas.SUCCESS_CODE);
			List<AsppGoodsSourceVO> goodsSource = goodsSourceService.getPublishGoodsSource(vo);
			model.put("goodsSources", goodsSourceData(goodsSource));
			model.put("totalPageNum", vo.getTotalPageNum());
		} catch (Exception e) {
			if(!model.isEmpty()){
				model.clear();
			}
			model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
			model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * <p>组装货源数据</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月11日
	 * @param vo
	 * @return
	 * @throws Exception
	 */
	private List<AsppGoodsSourceVO> goodsSourceData(List<AsppGoodsSourceVO> goodsSources)throws Exception {
		try {
			for (AsppGoodsSourceVO goodsSourceVO : goodsSources) {
				// 公司名称
				Euser euser = userBaseService.getEuser(goodsSourceVO.getUserId());
				User user = userBaseService.getUser(goodsSourceVO.getUserId());
				if(null != euser){
					goodsSourceVO.setEnterpriseName(euser.getEnterpriseName());
				}
				goodsSourceVO.setGoodsClass(StringUtils.isBlank(goodsSourceVO.getGoodsClass()) ? "goods_common" : "goods_" + goodsSourceVO.getGoodsClass());
				goodsSourceVO.setUserGrad(user!=null ? user.getUserGrad():null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return goodsSources;
	}
	
	/**
	 * 发布货源信息
	 * @param request
	 * @return
	 * @author tangshuai
	 */
	@RequestMapping(value = "/publishGoodsSource.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> publishGoodsSource(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			// 验证参数
			String loginUserName = CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			
			Integer isVisible = 1;
			if(paramsMap.containsKey("isVisible")){
				isVisible = Integer.parseInt(paramsMap.get("isVisible").toString());
			}
					
			logger.info("user enter GoodsSourceController-publishGoodsSource,loginUserName="+loginUserName);
			//String placeKilometre = (String) paramsMap.get("placeKilometre");
			// 保存Publish发布信息
			String notNullStr = "userId";
			PublishInfo publishInfo = (PublishInfo) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), PublishInfo.class, notNullStr, model);
			if(null != publishInfo.getPublishId()){
				// 修改时间
				publishInfo.setUpdateTime(new Date());
				// 修改人
				publishInfo.setUpdateUser(loginUserName);
			}else{
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
			publishInfo.setStatus(DataConstants.PUBLISH_DISPLAY);
			publishInfo.setIsDelete(DataConstants.NO);
			publishInfo.setIsStruct(DataConstants.YES);
			GoodsSource goodsSource = new GoodsSource();
			/*if(placeKilometre!=null && !"".equals(placeKilometre)){
				// 保存goodsSource信息
				String goodsSourceNotNullStr = "fromPlace,toPlace,fromPCode,fromCCode,toPCode,toCCode,goodsType,goodsName,infoContent,linkMan,linkMobile1,placeKilometre";
				goodsSource = (GoodsSource) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), GoodsSource.class, goodsSourceNotNullStr, model);
			}else {
				String goodsSourceNotNullStr = "fromPlace,toPlace,fromPCode,fromCCode,toPCode,toCCode,goodsType,goodsName,infoContent,linkMan,linkMobile1";
				goodsSource = (GoodsSource) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), GoodsSource.class, goodsSourceNotNullStr, model);
			}*/
			// 保存goodsSource信息
//			String goodsSourceNotNullStr = "fromPlace,toPlace,fromPCode,fromCCode,toPCode,toCCode,goodsType,goodsName,infoContent,linkMan,linkMobile1";
			String goodsSourceNotNullStr = "fromPlace,toPlace,fromPCode,fromCCode,toPCode,toCCode";
			goodsSource = (GoodsSource) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), GoodsSource.class, goodsSourceNotNullStr, model);
			Euser euser = userBaseService.getEuser(publishInfo.getUserId());
			User user = userBaseService.getUser(publishInfo.getUserId());
			if(null != euser && null != user){
				// 发布人姓名
				if(!StringUtils.isBlank(euser.getLinkman())){
					goodsSource.setPublisher(euser.getEnterpriseName());
				} else if (!StringUtils.isBlank(user.getNickName())) {
					goodsSource.setPublisher(user.getNickName());
				} else {
					goodsSource.setPublisher(user.getAccountName());
				}
				if(null != paramsMap.get("linkMan") && "" != paramsMap.get("linkMan")){
					goodsSource.setLinkMan(paramsMap.get("linkMan").toString());
				}
				if(null != paramsMap.get("linkMobile1") && "" != paramsMap.get("linkMobile1")){
					goodsSource.setLinkMobile1((paramsMap.get("linkMobile1").toString()));
				}
//				if(null != euser.getLinkMobile2() && !"".equals(euser.getLinkMobile2())){
//					goodsSource.setLinkMobile2(euser.getLinkMobile2());
//						
//				}if(null != paramsMap.get("linkMobile2").toString() || !"".equals(paramsMap.get("linkMobile2").toString())){
//					goodsSource.setLinkMobile2(paramsMap.get("linkMobile2").toString());
//				}
//				goodsSource.setLinkMobile2(euser.getLinkMobile2());
//				goodsSource.setLinkMobile2(paramsMap.get("linkMobile2").toString());
				goodsSource.setLinkPhone(euser.getLinkPhone());
			}
			if(!StringUtils.isBlank(goodsSource.getWeightUnit()) && goodsSource.getWeightUnit().equals("2")){
				goodsSource.setVolume(goodsSource.getWeight());
				goodsSource.setVolumeUnit(goodsSource.getWeightUnit());
				goodsSource.setWeight(null);
				goodsSource.setWeightUnit(null);
			}
			if(null != goodsSource.getGoodsSourceId()){
				// 修改时间
				goodsSource.setUpdateTime(new Date());
				// 修改人
				goodsSource.setUpdateUser(loginUserName);
			} else {
				// 创建人
				goodsSource.setCreateUser(loginUserName);
				// 创建时间
				goodsSource.setCreateTime(new Date());
			}
			goodsSource.setSourceType(DataConstants.GOODS_SOURCE_INSTANT);
			// 发布时间
			publishInfo.setPublishTime(new Date());
			publishInfo.setGoodsSourceInfo(goodsSource);
			publishInfo.setInfoType(DataConstants.INFO_TYPE_GOODS_SOURCE);
			publishInfo.setBackstageWrite(DataConstants.NO);
			publishInfo.setIsVisible(isVisible);
			String carLengthNeed = publishInfo.getGoodsSourceInfo().getCarLengthNeed();
			if(carLengthNeed != null && carLengthNeed !="" && carLengthNeed != ","){
				if(!carLengthNeed.contains("~")){
					publishInfo.getGoodsSourceInfo().setCarLengthNeedMax(Float.valueOf(carLengthNeed));
					publishInfo.getGoodsSourceInfo().setCarLengthNeedMin(Float.valueOf(carLengthNeed));
				}else{
					
					publishInfo.getGoodsSourceInfo().setCarLengthNeedName(carLengthNeed+"米");
					Float carLengthNeedMin=Float.valueOf(content(carLengthNeed,"(.*)~",1));
					if(carLengthNeedMin != null){
						publishInfo.getGoodsSourceInfo().setCarLengthNeedMin(carLengthNeedMin);
					}
					Float carLengthNeedMax=Float.valueOf(content(carLengthNeed,"~(.*)",1));
					if(carLengthNeedMax != null){
						publishInfo.getGoodsSourceInfo().setCarLengthNeedMax(carLengthNeedMax);
					}
				}
				
			
				
			}
			//查询总值字段的最大值
			long max = publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_GOODS_SOURCE);
			publishInfo.setSum(Integer.parseInt(max+""));
			publishInfo.setRegisterPCode(user.getRegisterPCode());
			publishInfo.setRegisterCCode(user.getRegisterCCode());
			publishInfo.setRegisterTCode(user.getRegisterTCode());
			publishInfoBaseService.publishInfo(publishInfo);
			
			// 返回信息
			model.put("code", ReturnCodes.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(!model.isEmpty()){
				model.clear();
			}
			model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
			model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			e.printStackTrace();
		}
		return model;
	}
	
	//截取内容
	public static String content(String s , String regex, int rank){
		Pattern pattern=Pattern.compile(regex);
		Matcher matchs=pattern.matcher(s);
		String strcontent=null;
		if(matchs.find())
			strcontent=matchs.group(rank);
		return strcontent;
	
	}
	/**
	 * 关闭货源
	 * @param accessToken
	 * @param publishId
	 * @param loginUserName
	 * @return
	 * @author tangshuai
	 */
	@RequestMapping(value = "/closeGoodsSource.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> closeGoodsSource(String accessToken, String publishId, String loginUserName){
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
			params.put("status", DataConstants.PUBLISH_CLOSE);
			params.put("updateUser", loginUserName);
			params.put("updateTime", new Date());
//			goodsSourceSolrQuery.deleteByPublishId(publishId);
			publishInfoBaseService.updatePublishData(params);
			// 返回参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(!model.isEmpty()){
				model.clear();
			}
			model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
			model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * <p>删除货源信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月11日
	 * @param accessToken
	 * @param publishId
	 * @param loginUserName
	 * @return
	 */
	@RequestMapping(value = "/deleteGoodsSource.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> deleteGoodsSource(String accessToken, String publishId, String loginUserName){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("publishId", publishId, model);
			CommonUtils.checkParams("loginUserName", loginUserName, model);
			// 修改数据
			Map<String, Object> params = new HashMap<String, Object>();
			
			String[] split = publishId.split(",");
			
			
//			goodsSourceSolrQuery.deleteByPublishId(publishId);
			//拼接Mongo删除条件
			params.put("publishIds", split);
			params.put("isDelete", DataConstants.YES);
			params.put("updateUser", loginUserName);
			params.put("updateTime", new Date());
			params.put("publishTime", new Date());
			for (int i = 0; i < split.length; i++) {
				BasicDBObject query = new BasicDBObject();
				query.put("publishId", Integer.valueOf(split[i]));
				MongoUtils.deleteGoodsSource(query,params);
			}
			publishInfoBaseService.deletePublishData(params);
			// 返回参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(!model.isEmpty()){
				model.clear();
			}
			model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
			model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * 
	 * <p>将mysql数据 insert to mongo</p>
	 * <p>执行流程：goodsSource  carSource</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年9月19日
	 * @return
	 */
	@RequestMapping(value = "/mysqlInsertToMongo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> mysqlInsertToMongo(){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			
			MongoUtils.insertManyGoodsSources();
			MongoUtils.insertManyCarSources();
			MongoUtils.insertManyCars(null);
			// 返回参数
			model.put("code", ReturnDatas.SUCCESS_CODE);
			this.setSuccessFlag(model);
		} catch (Exception e) {
			if(!model.isEmpty()){
				model.clear();
			}
			model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
			model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * 
	 * <p>找货列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月28日
	 * @param accessToken
	 * @param pageNo
	 * @param fromPCode
	 * @param fromCCode
	 * @param fromTCode
	 * @param toPCode
	 * @param toTCode
	 * @param toCCode
	 * @param userId
	 * @param keywords
	 * @param carTypeFirstCode
	 * @param carTypeSecondCode
	 * @param carTypeName
	 * @param carTypeThirdCode
	 * @param carLengthNeed
	 * @param newVersion
	 * @param mongoPageNum
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/queryGoodsSourceList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryGoodsSourceList(String accessToken,Integer pageNo, Integer fromPCode, Integer fromCCode,Integer fromTCode, 
			                          Integer toPCode, Integer toTCode, Integer toCCode, Integer userId, String keywords,String  
			                          carTypeFirstCode,String  carTypeSecondCode,String carTypeName,String carTypeThirdCode,String carLengthNeed ,boolean newVersion,Integer mongoPageNum){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 验证凭证
			CommonUtils.checkAccessToken(accessToken, model);
			//校验参数
			CommonUtils.checkParams("pageNo", pageNo, model);
			// set 查询条件
			GoodsSourceQuery queryObject = new GoodsSourceQuery();
			queryObject.setPageNo(pageNo);
			queryObject.setFromPCode(fromPCode);
			queryObject.setFromCCode(fromCCode);
			queryObject.setFromTCode(fromTCode);
			queryObject.setCarLengthNeed(carLengthNeed);
			queryObject.setKeywords(keywords);
			queryObject.setCarTypeNeedName(carTypeName);
			queryObject.setNewVersion(newVersion);
			queryObject.setToPCode(toPCode);
			queryObject.setToCCode(toCCode);
			queryObject.setToTCode(toTCode);
			queryObject.setUserId(userId);
			queryObject.setCarTypeFirstCode(carTypeFirstCode);
			queryObject.setCarTypeSecondCode(carTypeSecondCode);
			queryObject.setCarTypeThirdCode(carTypeThirdCode);
			queryObject.setMongoPageNum(mongoPageNum);
			User presentUser = userBaseService.getUser(userId);
			Integer needSourceNum = queryObject.getPageSize();
			//返回移动端的数据list 
			List<GoodsSource> returnData = new ArrayList<GoodsSource>();
			GoodsSource vo = null;
			GoodsSource vo1 = null;
		//	queryObject.setPlaceKilometre(placeKilometre);
			// 获取信息
			logger.info("===============queryGoodsSourceList====start==================>>"+new Date());
			MongoUtils.queryGoodsSources(queryObject,presentUser,userId,needSourceNum);
			logger.info("===============queryGoodsSourceList=====stop===================>>"+new Date());
//			goodsSourceSolrQuery.queryGoodsSources(queryObject, AsppGoodsSourceVO.class);
			//从mongo获取数据
			List queryList = queryObject.getQueryList();
			List queryList1 = new ArrayList();
			while (true) {
				if(queryObject.getTotalNum() ==0 || queryObject.getTotalNum()<queryObject.getPageSize()){
					for (int i = 0; i < queryList.size(); i++) {
						vo = (GoodsSource) queryList.get(i);
						vo.setGoodsClass(StringUtils.isBlank(vo.getGoodsClass()) ? "goods_common" : "goods_" + vo.getGoodsClass());
						returnData.add(vo);
					}
					break;
				}
			if(queryList.size()<queryObject.getPageSize()){
				//页数+1  查询下一页
				queryObject.setPageNo(++pageNo);
				//需要查询数据条数
				needSourceNum = needSourceNum-queryList.size();
				//Mongo查询
				MongoUtils.queryGoodsSources(queryObject,presentUser,userId,needSourceNum);
				//获取Mongo查询数据
				queryList1 = queryObject.getQueryList();
			}
				for (int i = 0; i < queryList.size(); i++) {
					vo = (GoodsSource) queryList.get(i);
					vo.setGoodsClass(StringUtils.isBlank(vo.getGoodsClass()) ? "goods_common" : "goods_" + vo.getGoodsClass());
					returnData.add(vo);
				}
				for (int i = 0; i < queryList1.size(); i++) {
					vo1 = (GoodsSource) queryList1.get(i);
					vo1.setGoodsClass(StringUtils.isBlank(vo1.getGoodsClass()) ? "goods_common" : "goods_" + vo1.getGoodsClass());
					returnData.add(vo1);
				}
				
				if(returnData.size()>=queryObject.getPageSize()){
					break;
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("goodsSources", returnData);
			model.put("goodsSourcesCount", publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_GOODS_SOURCE));
			model.put("totalPageNum", queryObject.getTotalPageNum());
			model.put("mongoPageNum", queryObject.getMongoPageNum());
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
	 * <p>列表详情</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年10月28日
	 * @param accessToken
	 * @param userId
	 * @param publishId
	 * @return
	 */
	@RequestMapping(value = "/getGoodsSourcesInfo.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getGoodsSourcesInfo(String accessToken,Integer userId,Integer publishId,Integer infoId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 验证凭证
			CommonUtils.checkAccessToken(accessToken, model);
			//校验参数
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("publishId", publishId, model);
			CommonUtils.checkParams("infoId", infoId, model);
			Map<String, Object> map = new HashMap<String, Object>();
			//设置是否收藏状态/抢单状态
			List<Integer> publishInfoIds = new ArrayList<Integer>();
			publishInfoIds.add(publishId);
			map.put("userId", userId);
			map.put("publishInfoIds", publishInfoIds);
			map.put("infoId", infoId);
			AsppGoodsSourceVO goodsSourceInfo = goodsSourceService.getGoodsSourceInfo(map);
			//获取货源是否能收藏
			List<AsppGoodsSourceVO> vos = goodsSourceService.getGoodsIsFavorite(map);
			//遍历solr货源数据
			GoodsSource vo = new GoodsSource();
			for (AsppGoodsSourceVO asppGoodsSourceVO : vos) {
				if (publishId == asppGoodsSourceVO.getPublishInfoId().intValue()) {
					goodsSourceInfo.setPublishTime(goodsSourceInfo.getCreateTime());
					goodsSourceInfo.setFavoriteId(asppGoodsSourceVO.getFavoriteId());
					goodsSourceInfo.setEnterpriseName(asppGoodsSourceVO.getEnterpriseName());
					if (null != userId && userId.intValue() == asppGoodsSourceVO.getUserId().intValue()) {
						goodsSourceInfo.setIsComplain(2);
					} else {
						goodsSourceInfo.setIsComplain(complainBaseService.judgeComplain(userId, asppGoodsSourceVO.getInfoId(),asppGoodsSourceVO.getInfoType()) ? DataConstants.YES: DataConstants.NO);
					}
					break;
				}
			}
			// 往solr查询出的货源数据里set必要的用户信息
			User user = userBaseService.getUser(goodsSourceInfo.getUserId());
			Euser euser = this.userBaseService.getEuser(goodsSourceInfo.getUserId());
			goodsSourceInfo.setAddress(null != euser ? euser.getAddress() : null);
			goodsSourceInfo.setUserPhoto(null != euser ? euser.getEnterprisePhoto() : null);
			goodsSourceInfo.setIsAuth(null != user ? user.getIsAuth() : 0);
			goodsSourceInfo.setGoodsClass(StringUtils.isBlank(goodsSourceInfo.getGoodsClass()) ? "goods_common": "goods_" + goodsSourceInfo.getGoodsClass());
			goodsSourceInfo.setUserGrad(user != null ? user.getUserGrad() : null);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("goodsSources", goodsSourceInfo);
			model.put("goodsSourcesCount", publishInfoBaseService.getTotalPublishInfoNum(DataConstants.INFO_TYPE_GOODS_SOURCE));
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

		//String a = "http://localhost:8080/aspp-server/goodssource/goodsSourceList.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&pageNo=1";
		try {
			String str = HttpUtils.sendHttpRequest("http://192.168.1.87:9091/aspp-server/goodssource/mysqlInsertToMongo.do", HttpUtils.RequestMethod.GET, null);
//			MongoUtils.insertManyGoodsSources();
			//System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
