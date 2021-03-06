package com.lzz.aspp.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.constants.TradeConstants;
import com.lzz.lsp.base.constants.VasServiceCodes;
import com.lzz.lsp.base.domain.LbsOpenRec;
import com.lzz.lsp.base.domain.Location;
import com.lzz.lsp.base.domain.TradeRec;
import com.lzz.lsp.base.domain.Vas;
import com.lzz.lsp.base.vo.LocationVO;
import com.lzz.lsp.core.lbs.service.LbsService;
import com.lzz.lsp.core.lbs.service.impl.LocationResult;
import com.lzz.lsp.core.lbs.service.impl.MobileRegisterResult;
import com.lzz.lsp.core.location.service.LocationBaseService;
import com.lzz.lsp.core.trade.service.TradeService;
import com.lzz.lsp.core.vas.service.VasService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.io.PropertiesSource;
import com.lzz.lton.util.DateUtils;
import com.lzz.lton.util.HttpUtils;

/**
 * 手机短信定位controller
 * @author tangshuai
 * 2015-10-21
 */
@Controller
@RequestMapping("/lbs")
public class LbsController {

	private static final Logger logger = Logger.getLogger(LbsController.class);
	
	@Resource(name="lbsService")
	private LbsService lbsService;
	
	@Resource(name = "vasService")
	private VasService vasService;
	
	@Resource(name = "tradeService")
	private TradeService tradeService;
	
	@Resource(name="locationBaseService")
	private LocationBaseService locationBaseService;
	
	/**
	 * 状态查询
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/lbsLocationQuery.do", method = RequestMethod.GET)
    @ResponseBody
	public Map<String, Object> lbsLocationQuery(String accessToken,String mobile){
		Map<String,Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("mobile", mobile, model);
			Location location = locationBaseService.getLocationByMobile(mobile);
			if(location!=null){
				model.put("locationName", location.getLocationName());
				model.put("locationTime", location.getLocateTime());
				model.put("lngitude", location.getLongitude());
				model.put("latitude", location.getLatitude());
			}
			MobileRegisterResult mobileRegisterResult = lbsService.sendList(PropertiesSource.getProperty("companyid"), PropertiesSource.getProperty("companypwd"), 
					mobile, null, DataConstants.LBS_MOBILE_REGISTER_USERTYPE_P, DataConstants.LBS_MOBILE_REGISTER_OPERTYPE_Q);
			if(DataConstants.LBS_RESULT_SUCCESS.equals(mobileRegisterResult.getResult())){
				if(DataConstants.LBS_WHITE_LIST_VALID.equals(mobileRegisterResult.getLststate())){
					model.put("lbsResponseCode",ReturnDatas.OPENED_SUCCESS);
					model.put("lbsResponseMsg",ReturnDatas.OPENED_SUCCESS_MSG);
				}else{
					if(DataConstants.LBS_BLACK_LIST.equals(mobileRegisterResult.getLststate())){
						model.put("lbsResponseCode",ReturnDatas.DISAGREE_OPEN);
						model.put("lbsResponseMsg",ReturnDatas.DISAGREE_OPEN_MSG);
					}else{
						model.put("lbsResponseCode",ReturnDatas.WAITING_ANSWER);
						model.put("lbsResponseMsg",ReturnDatas.WAITING_ANSWER_MSG);
					}
				}
			}else{
				if("手机号码未注册".equals(mobileRegisterResult.getMessage())){
					model.put("lbsResponseCode",ReturnDatas.NO_OPEN);
					model.put("lbsResponseMsg",ReturnDatas.NO_OPEN_MSG);
				}else{
					model.put("lbsResponseCode",ReturnDatas.OPEN_FAILED);
					model.put("lbsResponseMsg",ReturnDatas.OPEN_FAILED_MSG);
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
		}catch (Exception e) {
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
	 * 定位服务开通
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/lbsLocationRegister.do")
	@ResponseBody
	public Map<String, Object> lbsLocationRegister(String accessToken,String mobile){
		logger.info("user enter lbsLocationRegister,mobile="+mobile);
		Map<String,Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("mobile", mobile, model);
			LbsOpenRec lbsOpenRec = lbsService.getLbsOpenRecByMobile(mobile);
			boolean inviteValid = true;
			if(lbsOpenRec!=null){
				Calendar openTime = Calendar .getInstance();
				openTime.setTime(lbsOpenRec.getLastOpenTime());
				Calendar validTime = Calendar .getInstance();
				if(openTime.before(validTime)){
					model.put("lbsResponseCode",ReturnDatas.INVITE_INVALID);
					long surplusMillis = openTime.getTimeInMillis()-validTime.getTimeInMillis();
					int surplusMinute = DataConstants.INVITE_VALID_TIME + new Long(surplusMillis/(1000*60)).intValue();
					if(surplusMinute>0){
						inviteValid = false;
						model.put("lbsResponseMsg","邀请无效，请"+surplusMinute+"分钟后再邀请。");
					}
				}
			}
			if(inviteValid){
				MobileRegisterResult mobileRegisterResult = lbsService.sendList(PropertiesSource.getProperty("companyid"), PropertiesSource.getProperty("companypwd"), 
						mobile, null, DataConstants.LBS_MOBILE_REGISTER_USERTYPE_P, DataConstants.LBS_MOBILE_REGISTER_OPERTYPE_A);
				if(DataConstants.LBS_RESULT_SUCCESS.equals(mobileRegisterResult.getResult())){
					LbsOpenRec openRec = new LbsOpenRec();
					openRec.setMobile(mobile);
					openRec.setLastOpenTime(DateUtils.getCurrentTimestamp());
					lbsService.saveLbsOpenRec(openRec);
					model.put("lbsResponseCode",ReturnDatas.WAITING_ANSWER);
					model.put("lbsResponseMsg",ReturnDatas.WAITING_ANSWER_MSG);
				}else{
					model.put("lbsResponseCode",ReturnDatas.INVITE_FAILED);
					model.put("lbsResponseMsg",ReturnDatas.INVITE_FAILED_MSG);
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	 * 定位
	 * @param mobile
	 * @param userId
	 * @param accountName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/lbsLocation.do")
	@ResponseBody
	public Map<String, Object> lbsLocation(String accessToken,String mobile,Integer userId,String accountName){
		Map<String,Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("mobile", mobile, model);
			CommonUtils.checkParams("userId", userId, model);
			CommonUtils.checkParams("accountName", accountName, model);
			logger.info("user enter lbsLocation,userId="+userId+",mobile="+mobile);
			LocationResult locationResult = lbsService.sendLocation(PropertiesSource.getProperty("companyid"), PropertiesSource.getProperty("companypwd"), 
					mobile, DataConstants.LBS_LOCATION_OPERTYPE_N);
			if(DataConstants.LBS_RESULT_SUCCESS.equals(locationResult.getResult())){
				Vas vas = vasService.getVasByServiceCode(VasServiceCodes.USER_CONSUME_AVS_LBS);
				TradeRec tradeRec = new TradeRec ();
				tradeRec.setUserId(userId);	
				tradeRec.setAccount(accountName);
				tradeRec.setTradeCode(vas.getServiceCode());
				tradeRec.setTradeName(vas.getServiceName());
				tradeRec.setTradeMoney(vas.getPrice());
				// 交易类型=消费
				tradeRec.setTradeType(TradeConstants.TRADE_TYPE_CONSUME);
				// 支付方式=账户内扣款
				tradeRec.setPayWay(TradeConstants.PAY_WAY_ZHZF);
				// 交易状态=完成
				tradeRec.setTradeStatus(TradeConstants.TRADE_STATUS_SUCCESS);
				tradeService.saveTradeRec(tradeRec, locationResult.getLbsRecId());
				
				LocationVO locationVO = new LocationVO();
				locationVO.setMobile(mobile);
				locationVO.setLatitude(locationResult.getY());
				locationVO.setLongitude(locationResult.getX());
				locationVO.setLocationName(locationResult.getAddress());
				locationVO.setLocateTime(DateUtils.parse(locationResult.getTimestamp(),"yyyy-MM-dd HH:mm:ss"));
				locationBaseService.saveLocation(locationVO);
				
				model.put("lbsResponseCode",ReturnDatas.LOCATION_SUCCESS);
				model.put("lbsResponseMsg",ReturnDatas.LOCATION_SUCCESS_MSG);
				model.put("locationName", locationResult.getAddress());
				model.put("locationTime", locationResult.getTimestamp());
				model.put("lngitude", locationResult.getX());
				model.put("latitude", locationResult.getY());
			}else{
				//定位失败
				model.put("lbsResponseCode",ReturnDatas.LOCATION_FAIL);
				model.put("lbsResponseMsg",locationResult.getMessage() + "，请联系客服处理");
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
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
	
	public static void main(String[] args) {
		try {
			String str = HttpUtils.sendHttpRequest("http://localhost:8080/aspp-server/lbs/lbsLocationQuery.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&mobile=18910539244", HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
