package com.lzz.aspp.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lzz.lsp.base.constants.DictCodes;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.ChinaWeatherCode;
import com.lzz.lsp.base.vo.ChinaWeatherDataDetailedVO;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.cmpt.district.service.DistrictService;
import com.lzz.lsp.cmpt.weather.service.ChinaWeatherService;
import com.lzz.lsp.core.chinaweathercode.service.ChinaWeatherCodeService;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lton.util.HttpUtils;
import com.lzz.lton.util.StringUtils;

/**
 * <p>基础服务Controller</p>
 * @author Liuyl 2015年8月5日
 * @version 1.0
 * <p>注意事项： </p>
 */
@Controller
@RequestMapping("/base/service")
public class BaseController {

	@Resource(name = "dictService")
	private DictService dictService;
	
	@Resource(name = "districtService")
	private DistrictService districtService;
	
	@Resource(name = "chinaWeatherCodeService")
	private ChinaWeatherCodeService chinaWeatherCodeService;
	
	@Resource(name = "chinaWeatherService")
	private ChinaWeatherService chinaWeatherService;
	
	

	/**
	 * <p>地图静态导航</p>
	 * <p>给出出发地、目的地、当前位置三个点，然后在地图上标记出来</p>
	 * @author tangs 2015年8月25日
	 * @param statLocation
	 * @param endLocation
	 * @param currentLocation
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/toMapStaticNavigation.do", method = RequestMethod.GET)
	public ModelAndView toMapStaticNavigation(String statLocation,String endLocation,String currentLocation) throws UnsupportedEncodingException{
		Map<String, Object> model = new HashMap<String, Object>();
		String result = "/map/staticNavigation";
		model.put("statLocation", statLocation);
		model.put("endLocation", endLocation);
		model.put("currentLocation", currentLocation);
		return new ModelAndView(result, model);
	}
	
	
	/**
 	 * <p>获取天气信息（首页）</p>
 	 * <p>执行流程：</p>
 	 * <p>注意事项：</p>
 	 * @author Liuyl 2015年7月31日
 	 * @param accessToken
 	 * @param cityCode
 	 * @param source
 	 * @return
 	 */
 	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping(value = "/getWeatherInfo.do", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getWeatherInfo(String accessToken, String cityCode, String source){
    	Map<String, Object> model = new HashMap<String, Object>();
    	Date date = new Date();
		try {
			// 验证访问凭证
			CommonUtils.checkAccessToken(accessToken, model);
			CommonUtils.checkParams("source", source, model);
			CommonUtils.checkParams(source+"--cityCode", cityCode, model);
			String chinaCode = "101010100";
			chinaCode = getChinaWeatherCode(Integer.valueOf(cityCode), chinaCode);
			// 天气数据
			List<ChinaWeatherDataDetailedVO> detailedVOs = (List<ChinaWeatherDataDetailedVO>) chinaWeatherService.getWeatherForecastByAreaId(chinaCode).get("forecastInfo");
			ChinaWeatherDataDetailedVO vo = new ChinaWeatherDataDetailedVO();
			if(null != detailedVOs && detailedVOs.size() != 0){
				vo = detailedVOs.get(0);
				if(date.getHours() < 18){
					vo.setWeatherNo(dictService.getDictVal(DictCodes.CHINA_WEATHER, vo.getDayNo()).getValName());
				} else {
					vo.setWeatherNo(dictService.getDictVal(DictCodes.CHINA_WEATHER, vo.getNightNo()).getValName());
				}
				if(StringUtils.isBlank(vo.getDayTemperature())){
					vo.setTemperature(vo.getNightTemperature()+"°C");
				} else if (StringUtils.isBlank(vo.getNightTemperature())){
					vo.setTemperature(vo.getDayTemperature()+"°C");
				} else {
					vo.setTemperature(vo.getDayTemperature()+"~"+vo.getNightTemperature()+"°C");
				}
			} else {
				vo.setWeatherNo("晴");
				vo.setTemperature("25~30°C");
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("weatherInfo", vo);
			return model;
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
     * <p>根据本地城市code获取中国天气城市code</p>
     * <p>执行流程：</p>
     * <p>注意事项：</p>
     * @author Liuyl 2015年7月6日
     * @param cityCode
     * @param chinaCode
     * @return
     */
	private String getChinaWeatherCode(Integer cityCode, String chinaCode) {
		try {
			ChinaWeatherCode chinaWeatherCode = chinaWeatherCodeService.getChinaWeatherCodeByLocalCityCode(cityCode);
			if(null != chinaWeatherCode){
				if(StringUtils.isBlank(chinaWeatherCode.getChinaWeatherCode())){
					ChinaWeatherCode weatherCode = chinaWeatherCodeService.getChinaWeatherCodeByLocalCityCode(chinaWeatherCode.getParentCode());
					chinaCode = null == weatherCode || StringUtils.isBlank(weatherCode.getChinaWeatherCode())
							? "101010100" : weatherCode.getChinaWeatherCode();
				} else {
					chinaCode = chinaWeatherCode.getChinaWeatherCode();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return chinaCode;
	}
	
	/**
	 * <p>地图定位</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author tangs 2015年11月3日
	 * @param localtion
	 * @return
	 */
	@RequestMapping(value = "/toMap.do", method = RequestMethod.GET)
	public ModelAndView toMapShow(String location){
		Map<String, Object> model = new HashMap<String, Object>();
		String result = "/map/location";
		model.put("location", location);
		return new ModelAndView(result, model);
	}
	
	/**
	 * <p>地图定位With信息POI</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author tangs 2015年8月13日
	 * @param poi
	 * @param localtion
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/toMapPoi.do", method = RequestMethod.GET)
	public ModelAndView toMapShow(String poi,String location) throws UnsupportedEncodingException{
		Map<String, Object> model = new HashMap<String, Object>();
		String result = "/map/locationPoi";
		model.put("location", location);
		model.put("poi", URLDecoder.decode(poi,"utf-8"));
		return new ModelAndView(result, model);
	}
	
	
	
	public static void main(String[] args) {
			try {
				String str = HttpUtils.sendHttpRequest("http://localhost:8080/aspp-server/base/service/getWeatherInfo.do?accessToken=5a8e5467f3a1434b7c265268bf76c338&cityCode=海淀&source=02", HttpUtils.RequestMethod.GET, null);
				System.out.println(str);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
