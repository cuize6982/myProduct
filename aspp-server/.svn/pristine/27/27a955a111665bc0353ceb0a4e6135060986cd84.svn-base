package com.lzz.aspp.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.google.gson.Gson;
import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.ScoreGift;
import com.lzz.lsp.base.domain.User;
import com.lzz.lsp.base.domain.UserScoreExchangeRec;
import com.lzz.lsp.base.domain.UserScoreRule;
import com.lzz.lsp.base.qo.ScoreCashRecQuery;
import com.lzz.lsp.base.qo.UserScoreExchangeRecQuery;
import com.lzz.lsp.base.qo.UserScoreRecQuery;
import com.lzz.lsp.base.qo.UserScoreRuleQuery;
import com.lzz.lsp.base.vo.AgentVO;
import com.lzz.lsp.base.vo.UserScoreExchangeRecVO;
import com.lzz.lsp.base.vo.UserScoreRecVO;
import com.lzz.lsp.core.agent.service.AgentBaseService;
import com.lzz.lsp.core.score.service.ScoreCashRecBaseService;
import com.lzz.lsp.core.score.service.ScoreGiftBaseService;
import com.lzz.lsp.core.score.service.UserScoreExchangeRecBaseService;
import com.lzz.lsp.core.score.service.UserScoreRecBaseService;
import com.lzz.lsp.core.score.service.UserScoreRuleBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.JsonUtils;

/**
 * <p>用户积分Controller</p>
 * @author Liuyl 2015年8月12日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/user/score")
public class UserScoreController extends BaseController{

	private static final Logger logger = Logger.getLogger(UserScoreController.class);
	private static final Gson g = new Gson();
	
	
	
	@Resource(name = "userScoreExchangeRecBaseService")
	private UserScoreExchangeRecBaseService userScoreExchangeRecBaseService;
	
	@Resource(name = "scoreGiftBaseService")
	private ScoreGiftBaseService scoreGiftBaseService;
	
	@Resource(name = "userScoreRecBaseService")
	private UserScoreRecBaseService userScoreRecBaseService; 
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	
	@Resource(name = "userScoreRuleBaseService")
	private UserScoreRuleBaseService userScoreRuleBaseService;
	
	
	@Resource(name = "scoreCashRecBaseService")
	private ScoreCashRecBaseService scoreCashRecBaseService;
	
	@Resource(name = "agentBaseService")
	private AgentBaseService agentBaseService;
	
	
	/**
	 * <p>积分商城</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年6月23日
	 * @param accessToken
	 * @return
	 */
	@RequestMapping(value = "/scoreGifts.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> scoreGifts(String accessToken, Integer userId){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
		
			User user = userBaseService.getUser(userId);
			Integer currentUserScore = Long.valueOf(Long.valueOf(user.getScore()) - Long.valueOf(user.getUseScore())).intValue();
			
			BigDecimal unExchangeCash =  BigDecimal.valueOf(Long.valueOf(Long.valueOf(user.getScore()-Long.valueOf(user.getUseScore()))) ).divide(new BigDecimal(10), 2,RoundingMode.UP);
			
			Double currentUnExchangeCash = unExchangeCash.doubleValue();
			model.put("currentUserScore", currentUserScore);
			model.put("currentUnExchangeCash", currentUnExchangeCash);
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
	 * <p>积分明细</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年6月23日
	 * @return
	 */
	@RequestMapping(value = "/userScoreRec.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> userScoreRec(String accessToken, Integer userId,
			Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			UserScoreRecQuery query = new UserScoreRecQuery();
			query.setPageNo(pageNo);
			query.setUserId(userId);
			List<UserScoreRecVO> userScoreRecs = userScoreRecBaseService.queryUserScoreRecs(query);
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("userScoreRecs", userScoreRecs);
			model.put("totalPageNum", query.getTotalPageNum());
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
	 * <p>兑换记录</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年6月23日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/userScoreExchangeRec.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> userScoreExchangeRec(String accessToken, Integer userId,
			Integer pageNo){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 校验参数
			CommonUtils.checkParams("userId", userId, model);
			UserScoreExchangeRecQuery query = new UserScoreExchangeRecQuery();
			query.setUserId(userId);
			query.setStatus(DataConstants.SCORE_EXCHANGE_REC_STATUS_TRUE);
			query.setPageNo(pageNo);
			List<UserScoreExchangeRecVO> scoreExchangeList = userScoreExchangeRecBaseService.queryUserScoreExchangesForOsmp(query);
			for(UserScoreExchangeRecVO temp:scoreExchangeList){
				if(!StringUtils.isEmpty(temp)){
					temp.setExchangeScore(temp.getPayeeTransferMoney().multiply(new BigDecimal(100)).intValue());
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("userScoreExchangeRec", scoreExchangeList);
			model.put("totalPageNum", query.getTotalPageNum());
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
//		System.out.println("兑换记录执行时间(userScoreExchangeRec)："+DateUtil.getDiffTimeDesc(startTime, new Date()));
		return model;
	}
	
	/**
	 * <p>兑换礼物</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年6月24日
	 * @param accessToken
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/exchange.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> exchange(HttpServletRequest request){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 获取参数
			Map<String, Object> paramsMap = WebUtils.getParametersStartingWith(request, null);
			// 校验令牌
			CommonUtils.checkAccessToken(paramsMap.get("accessToken"), model);
			// 校验参数
			String loginUserName = CommonUtils.checkParams("loginUserName", paramsMap.get("loginUserName"), model);
			String giftType = CommonUtils.checkParams("giftType", paramsMap.get("giftType"), model);
			String giftId = CommonUtils.checkParams("giftId", paramsMap.get("giftId"), model);
			String userId = CommonUtils.checkParams("userId", paramsMap.get("userId"), model);
			// 校验是否可兑换
			String isUserExchangeFlag = this.isUserExchange(scoreGiftBaseService.getScoreGift(Integer.valueOf(giftId)), userBaseService.getUser(Integer.valueOf(userId)));
			if(isUserExchangeFlag.equals(DataConstants.SCORE_GIFT_BUTTON_IN)){
				model.put("code", ReturnDatas.SCORE_GIFT_GET_IN);
				model.put("msg", ReturnDatas.SCORE_GIFT_GET_IN_MSG);
				return model;
			} else if (isUserExchangeFlag.equals(DataConstants.SCORE_GIFT_BUTTON_NOT_INVENTORY)){
				model.put("code", ReturnDatas.SCORE_GIFT_GET_NOT_INVENTORY);
				model.put("msg", ReturnDatas.SCORE_GIFT_GET_NOT_INVENTORY_MSG);
				return model;
			} else if (isUserExchangeFlag.equals(DataConstants.SCORE_GIFT_BUTTON_NOT_SCORE)){
				model.put("code", ReturnDatas.SCORE_GIFT_GET_NOT_SCORE);
				model.put("msg", ReturnDatas.SCORE_GIFT_GET_NOT_SCORE_MSG);
				return model;
			}
			String notNullStr = "userId,giftId,giftName,exchangeScore";
			if(giftType.equals(DataConstants.SCORE_GIFT_TYPE_REALLY)){
				notNullStr += ",consigneeName,consigneePhone,consigneeAddress,zipCode";
			}
			UserScoreExchangeRec rec = (UserScoreExchangeRec) CommonUtils.checkBean(JsonUtils.mapToJson(paramsMap), UserScoreExchangeRec.class, notNullStr, model);
			rec.setCreateUser(loginUserName);
			rec.setCreateTime(new Date());
			rec.setExchangeTime(new Date());
			rec.setStatus(DataConstants.SCORE_EXCHANGE_REC_STATUS_FALSE);
			userScoreExchangeRecBaseService.saveUserScoreExchange(rec);
			// 准备返回
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("accountScore", userBaseService.getUser(rec.getUserId()).getScore());
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
	 * <p>用户是否可兑换该商品</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年6月26日
	 * @param scoreGift
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String isUserExchange(ScoreGift scoreGift, User user) throws Exception{
		try {
			UserScoreExchangeRecQuery queryObject = new UserScoreExchangeRecQuery();
			queryObject.setUserId(user.getUserId());
			queryObject.setGiftId(scoreGift.getId());
			queryObject.setStatus(DataConstants.SCORE_EXCHANGE_REC_STATUS_FALSE);
			List<UserScoreExchangeRecVO> userScoreExchanges = userScoreExchangeRecBaseService.queryUserScoreExchange(queryObject);
			if (null != userScoreExchanges && userScoreExchanges.size() > 0){
				return DataConstants.SCORE_GIFT_BUTTON_IN;
			} else if(null == scoreGift.getSurplusNum()
					|| 0 == scoreGift.getSurplusNum()){
				return DataConstants.SCORE_GIFT_BUTTON_NOT_INVENTORY;
			} else if (user.getScore() < scoreGift.getExchangeScore()){
				return DataConstants.SCORE_GIFT_BUTTON_NOT_SCORE;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataConstants.SCORE_GIFT_BUTTON_TRUE;
	}
	
	/**
	 * 积分明细
	 * LiuYanliang
	 * @param accessToken
	 * @param userId
	 * @param pageNo
	 * @param inOrOut
	 * @param scoreRuleId
	 * @param giftId
	 * @param cashMonth 兑现年月
	 * @return 
	 * 2016年9月22日 下午2:33:44
	 */
	@RequestMapping(value = "/queryUserScoreRec.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryUserScoreRec(String accessToken, Integer userId,Integer pageNo,Integer inOrOut,Integer scoreRuleId,Integer giftId,String cashMonth){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验参数
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("userId", userId, model);
		    CommonUtils.checkParams("inOrOut", userId, model);
			
			//积分收入明细
			if(DataConstants.SCORE_IN == inOrOut){
				//二代还是总代
				AgentVO agent = agentBaseService.getAgentInfoByAgentUserId(userId);
				Integer isGeneralAgent = 0;
				if(agent != null && agent.getAgentParentUserId() == 0){
					isGeneralAgent = 1;
				}
				logger.info("user enter UserScoreController,isGeneralAgent="+isGeneralAgent+",userId="+userId);
				//查询下拉框数据
				UserScoreRuleQuery scoreRule = new UserScoreRuleQuery();
				scoreRule.setUsePage(false);
				scoreRule.setStatus(DataConstants.YES);
				scoreRule.setIsGeneralAgent(isGeneralAgent);
				List<UserScoreRule> scoreRules = userScoreRuleBaseService.queryUserScoreRules(scoreRule);
				logger.info("UserScoreController-queryUserScoreRec,ruleList="+g.toJson(scoreRules));
				model.put("selectArgs", scoreRules);
				//查询XXX总积分
				UserScoreRecQuery scoreRecQuery = new UserScoreRecQuery();
				scoreRecQuery.setUserId(userId);
				if(scoreRuleId != null){
					scoreRecQuery.setScoreRuleId(scoreRuleId);
				}
				scoreRecQuery.setUsePage(false);
				scoreRecQuery.setPageNo(pageNo);
				List<UserScoreRecVO> myScoreInfo = userScoreRecBaseService.queryMyScoreInfo(scoreRecQuery);
				int totalScore = 0 ;
				for (UserScoreRecVO usr : myScoreInfo) {
					String scoreValue = usr.getScoreValue();
					totalScore = totalScore +Integer.parseInt(scoreValue);
				}
				model.put("totalScore", totalScore);
				//查询我的积分列表
				scoreRecQuery.setUsePage(true);
				scoreRecQuery.setCashMonth(cashMonth);
				userScoreRecBaseService.queryMyScoreInfo(scoreRecQuery);
				model.put("userScoreList", scoreRecQuery);
				model.put("code", ReturnDatas.SUCCESS_CODE);
				model.put("totalPageNum", scoreRecQuery.getTotalPageNum());
				
				
				
			}else if(DataConstants.SCORE_OUT_EXCHANGE == inOrOut){
				//积分兑换明细
//				//下拉列表数据
//				ScoreGiftQuery queryObject = new ScoreGiftQuery();
//				queryObject.setStatus(DataConstants.YES);
//				List<ScoreGift> scoreGifts = scoreGiftBaseService.queryScoreGift(queryObject);
//				model.put("selectArgs", scoreGifts);
//				
//				//查询XXX支出积分
//				UserScoreExchangeRecQuery scoreExchangeRecQuery = new UserScoreExchangeRecQuery();
//				scoreExchangeRecQuery.setUserId(userId);
//				scoreExchangeRecQuery.setStatus(DataConstants.SCORE_EXCHANGE_REC_STATUS_TRUE);
//				scoreExchangeRecQuery.setUsePage(false);
//				List<UserScoreExchangeRecVO> userScoreExchangeRecList = userScoreExchangeRecBaseService.queryUserScoreExchange(scoreExchangeRecQuery);
//				Integer totalScore = 0;//消费总积分
//				for(UserScoreExchangeRecVO userScoreExchangeRecVO:userScoreExchangeRecList){
//					totalScore = totalScore + Math.abs(userScoreExchangeRecVO.getExchangeScore());
//				}
//				model.put("totalScore", 0);
//				
//				//查询支出积分的列表
//				scoreExchangeRecQuery.setUsePage(true);
//				scoreExchangeRecQuery.setUserId(userId);
//				if(giftId != null){
//					scoreExchangeRecQuery.setGiftId(giftId);
//				}
//				scoreExchangeRecQuery.setStatus(DataConstants.SCORE_EXCHANGE_REC_STATUS_TRUE);
//				userScoreExchangeRecBaseService.queryUserScoreExchange(scoreExchangeRecQuery);
//				model.put("userScoreList", new ArrayList<UserScoreExchangeRecVO>());
//				model.put("code", ReturnDatas.SUCCESS_CODE);
//				model.put("totalPageNum", 0);
//				model.put("totalPageNum", scoreExchangeRecQuery.getTotalPageNum());
			}else if(DataConstants.SCORE_OUT_CASH == inOrOut){
				//查询兑换总积分
				/*ScoreCashRec scoreCashRec = new ScoreCashRec();
				scoreCashRec.setUserId(userId);
				List<ScoreCashRec> scoreCashResList = scoreCashRecBaseService.getScoreCashRecList(scoreCashRec);*/
				//兑现总积分
				/*Integer totalScore = 0;
				for(ScoreCashRec rec : scoreCashResList){
					totalScore = totalScore + Math.abs(rec.getScore());
				}
				model.put("totalScore", totalScore);*/
				
				ScoreCashRecQuery query = new ScoreCashRecQuery();
				query.setUsePage(true);
				query.setUserId(userId);
				query.setCashMonth(cashMonth);
				scoreCashRecBaseService.queryScoreCashRec(query);
				model.put("userScoreList", query);
				model.put("code", ReturnDatas.SUCCESS_CODE);
				model.put("totalPageNum", 0);
				model.put("totalPageNum", query.getTotalPageNum());
				logger.info("return data:"+g.toJson(model));
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
	
	public static void main(String[] args){
		String url = "http://127.0.0.1:8080/aspp-server/user/score/queryUserScoreRec.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&pageNo=1&userId=1012&inOrOut=0&scoreRuleId=5";
		try {
			String str = HttpUtils.sendHttpRequest(url, com.lzz.lton.util.HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
