package com.lzz.aspp.web;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.lzz.aspp.service.UserFavoriteService;
import com.lzz.aspp.util.CommonUtil;
import com.lzz.aspp.vo.CarSourceFavoriteVO;
import com.lzz.aspp.vo.GoodsSourceFavoriteVO;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Euser;
import com.lzz.lsp.base.domain.Favorite;
import com.lzz.lsp.base.domain.Puser;
import com.lzz.lsp.base.qo.CarSourceQuery;
import com.lzz.lsp.base.qo.GoodsSourceQuery;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.core.favorite.service.FavoriteBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;
import com.lzz.lton.util.StringUtils;

/**
 * <p>收藏Controller</p>
 * @author tangs 2015年8月10日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/user/favorite")
public class UserFavoriteController extends BaseController{
	
	@Resource(name = "userFavoriteService")
	private UserFavoriteService userFavoriteService;
	
	@Resource(name = "favoriteBaseService")
	private FavoriteBaseService favoriteBaseService;
	
	@Resource(name = "dictService")
	private DictService dictService;
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	/**
	 * <p>获取收藏列表</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @param fromPCode
	 * @param fromCCode
	 * @param toPCode
	 * @param toCCode
	 * @param type
	 * @return
	 * @author tangs 2015年8月10日 
	 */
	@RequestMapping(value = "/getFavoriteInfos.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getFavoriteInfos(String accessToken,
			Integer userId, Integer pageNo, Integer fromPCode, 
			Integer fromCCode, Integer fromTCode, Integer toPCode, 
			Integer toCCode, Integer toTCode, Integer type){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("pageNo", pageNo, model);
			CommonUtils.checkParams("type", type, model);
			if(DataConstants.INFO_TYPE_GOODS_SOURCE == type){
				GoodsSourceQuery queryObject = new GoodsSourceQuery();
				queryObject.setPageNo(pageNo);
				queryObject.setUserId(userId);
				queryObject.setFromPCode(fromPCode);
				queryObject.setFromCCode(fromCCode);
				queryObject.setFromTCode(fromTCode);
				queryObject.setToPCode(toPCode);
				queryObject.setToCCode(toCCode);
				queryObject.setToTCode(toTCode);
				List<GoodsSourceFavoriteVO> goodsSourceFavoriteVOList = userFavoriteService.getUserGoodsSourceFavorites(queryObject);
//				for (GoodsSourceFavoriteVO goodsSourceFavoriteVO : goodsSourceFavoriteVOList) {
//					if(goodsSourceFavoriteVO.getCarTypeNeedName() == null || "".equals(goodsSourceFavoriteVO.getCarTypeNeedName())){
//						String carTypeByNeedName = this.carTypeByNeedName(goodsSourceFavoriteVOList);
//						goodsSourceFavoriteVO.setCarTypeNeedName(carTypeByNeedName);
//					}
//				}
				model.put("goodsSourceFavoritesNum",queryObject.getTotalNum());
				model.put("carSourceFavoritesNum", userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_CAR_SOURCE));
				model.put("favorites", goodsSourceFavoriteVOList);
				model.put("totalPageNum", queryObject.getTotalPageNum());
			}else if(DataConstants.INFO_TYPE_CAR_SOURCE == type){
				CarSourceQuery queryObject = new CarSourceQuery();
				queryObject.setPageNo(pageNo);
				queryObject.setUserId(userId);
				queryObject.setFromPCode(fromPCode);
				queryObject.setFromCCode(fromCCode);
				queryObject.setFromTCode(fromTCode);
				queryObject.setToPCode(toPCode);
				queryObject.setToCCode(toCCode);
				queryObject.setToTCode(toTCode);
				List<CarSourceFavoriteVO> carSourceFavoriteVOList = userFavoriteService.getUserCarSourceFavorites(queryObject);
				
					
				for (CarSourceFavoriteVO carMonitorVO : carSourceFavoriteVOList) {
					if(null == carMonitorVO.getCarTypeName() || "".equals(carMonitorVO.getCarTypeName())){
						
					Integer carTypeFirstCode = carMonitorVO
							.getCarTypeFirstCode();
					Integer carTypeSecondCode = carMonitorVO
							.getCarTypeSecondCode();
					Integer carTypeThirdCode = carMonitorVO
							.getCarTypeThirdCode();
					String carTypeStr = null;
					if (carTypeFirstCode == null || "".equals(carTypeFirstCode)) {

					} else {
						if (carTypeSecondCode == null
								|| "".equals(carTypeSecondCode)) {
							String TypeName1 = dictService
									.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
							carMonitorVO.setCarTypeCode(carTypeFirstCode
									.toString());
							carTypeStr = TypeName1;
						} else {
							if (carTypeThirdCode == null
									|| "".equals(carTypeThirdCode)) {
								String TypeName1 = dictService
										.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String TypeName2 = dictService
										.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								carMonitorVO.setCarTypeCode(carTypeSecondCode
										.toString());
								carTypeStr = TypeName1 + "-" + TypeName2;
							} else {
								String TypeName1 = dictService
										.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String TypeName2 = dictService
										.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String TypeName3 = dictService
										.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
								carMonitorVO.setCarTypeCode(carTypeThirdCode
										.toString());
								carTypeStr = TypeName1 + "-" + TypeName2 + "-"
										+ TypeName3;
							}
						}
					}
					carMonitorVO.setCarTypeName(carTypeStr);
				}
				}
				for (CarSourceFavoriteVO carSourceFavoriteVO : carSourceFavoriteVOList) {
					if("1".equals(carSourceFavoriteVO.getIsAuth())){
						carSourceFavoriteVO.setIdcardNo(CommonUtil.replaceIdCarNO(carSourceFavoriteVO.getIdcardNo()));
					} else {
						carSourceFavoriteVO.setIdcardNo("未实名认证");
					}
					//定位访问url
					if(!StringUtils.isBlank(carSourceFavoriteVO.getDriverLocationName())){
						String locationUrl = "/base/service/toMapPoi.do?location="+carSourceFavoriteVO.getLongitude()+","+carSourceFavoriteVO.getLatitude()+"&poi="+URLEncoder.encode(carSourceFavoriteVO.getDriverLocationName(),"utf-8");
						carSourceFavoriteVO.setLocationUrl(locationUrl);
					}else{
						carSourceFavoriteVO.setDriverLocationName("未开启定位");
					}
					
					// 修改发布人显示名称 by liuyl 201508261600
					if("1".equals(carSourceFavoriteVO.getUserType())
							|| "2".equals(carSourceFavoriteVO.getUserType())){
						Puser puser = userBaseService.getPuser(carSourceFavoriteVO.getPpiUserId());
						if(null != puser){
							carSourceFavoriteVO.setPublisher(carSourceFavoriteVO.getLinkMan());
						}
					} else {
						Euser euser = userBaseService.getEuser(carSourceFavoriteVO.getPpiUserId());
						if(null != euser){
							// 发布人姓名
							carSourceFavoriteVO.setPublisher(carSourceFavoriteVO.getLinkMan() + "-" +euser.getEnterpriseName());
						}
					}
				}
				model.put("carSourceFavoritesNum",queryObject.getTotalNum());
				model.put("goodsSourceFavoritesNum", userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_GOODS_SOURCE));
				model.put("favorites", queryObject.getQueryList());
				model.put("totalPageNum", queryObject.getTotalPageNum());
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
	 * 
	 * <p>根据车型CODE查询对应的车型</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Cuiz 2016年6月8日
	 * @param goodsSourceFavoriteVOList
	 * @return
	 */
	@Deprecated
	public String carTypeByNeedName(List<GoodsSourceFavoriteVO> goodsSourceFavoriteVOList){
		
		String carTypeStr=null;	
		try {
			for (GoodsSourceFavoriteVO carVO : goodsSourceFavoriteVOList) {
				if(carVO.getCarTypeNeedName() == null || "".equals(carVO.getCarTypeNeedName())){
					Integer carTypeFirstCode=carVO.getCarTypeFirstCode();
					Integer carTypeSecondCode=carVO.getCarTypeSecondCode();
					Integer carTypeThirdCode=carVO.getCarTypeThirdCode();
					
					if(carTypeFirstCode == null || "".equals(carTypeFirstCode)){
						
					}else{
						if(carTypeSecondCode == null || "".equals(carTypeSecondCode)){
						String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
						carVO.setCarTypeCode(carTypeFirstCode.toString());
						carTypeStr =TypeName1;
						}else{
							if(carTypeThirdCode == null || "".equals(carTypeThirdCode)){
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								carVO.setCarTypeCode(carTypeSecondCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2;
							}else{
								String	TypeName1 = dictService.queryCarTypeNameByCarTypeCode(carTypeFirstCode);
								String  TypeName2 = dictService.queryCarTypeNameByCarTypeCode(carTypeSecondCode);
								String TypeName3 =dictService.queryCarTypeNameByCarTypeCode(carTypeThirdCode);
								carVO.setCarTypeCode(carTypeThirdCode.toString());
								carTypeStr =TypeName1+"-" +TypeName2+"-"+TypeName3;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return carTypeStr;
		
	}
	
	/**
	 * <p>收藏信息</p>
	 * @param request
	 * @return
	 * @author tangs 2015年8月10
	 */
	@RequestMapping(value = "/favoriteInfo.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> favoriteInfo(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 验证访问凭证
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			// 收藏
			String favoriteNotNullStr = "userId,infoId,infoType";
			Favorite favorite = (Favorite) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), Favorite.class, favoriteNotNullStr, model);
			favorite.setCreateUser(paramsMap.get("loginUserName").toString());
			// 校验重复收藏  by liuyl 20150606
			if(null != favoriteBaseService.getUserFavoriteByUserIdAndInfoId(favorite.getUserId(), 
					favorite.getInfoId(), favorite.getInfoType())){
				model.put("code", ReturnDatas.CAR_SOURCE_FAVORITE_EXIST);
				model.put("msg", ReturnDatas.CAR_SOURCE_FAVORITE_EXIST_MSG);
				return model;
			}
			// 保存收藏信息
			favoriteBaseService.saveFavorite(favorite);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("favoriteId", favoriteBaseService.getUserFavoriteByUserIdAndInfoId(favorite.getUserId(), favorite.getInfoId(), favorite.getInfoType()).getId());
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
	 * <p>删除用户收藏信息</p>
	 * @param userId
	 * @param favoriteId
	 * @return
	 * @author tangs 2015年8月10
	 */
	@RequestMapping(value = "/cancelFavorite.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> cancelFavorite(String accessToken, Integer userId, Integer favoriteId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("favoriteId", favoriteId, model);
			favoriteBaseService.deleteFavorite(favoriteId, userId);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("goodsSourceFavoritesNum", userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_GOODS_SOURCE));
			model.put("carSourceFavoritesNum", userFavoriteService.getFavoritesCount(userId, DataConstants.INFO_TYPE_CAR_SOURCE));
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
	public static void main(String[] args) {
		/*String getUrl = "http://127.0.0.1:9091/aspp-server/user/favorite/getFavoriteInfos.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&userId=1072&pageNo=1&type=1";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("accessToken", "cf8918f60571a1baf5b6d373e9c1df74");
		map.put("userId", "5");
		map.put("infoId", "79");
		map.put("infoType", "2");
		map.put("loginUserName", "tagns");*/
		String postUrl = "http://127.0.0.1:8080/aspp-server/user/favorite/getFavoriteInfos.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&userId=1072&type=2&pageNo=1";
		try {
			String str = HttpUtils.sendHttpRequest(postUrl, HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
