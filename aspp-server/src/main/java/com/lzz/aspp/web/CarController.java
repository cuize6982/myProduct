package com.lzz.aspp.web;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.directwebremoting.export.Data;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzz.lsp.base.constants.DataConstants;
import com.lzz.lsp.base.constants.ReturnDatas;
import com.lzz.lsp.base.domain.Car;
import com.lzz.lsp.base.domain.Puser;
import com.lzz.lsp.base.qo.CarQuery;
import com.lzz.lsp.base.vo.CarVO;
import com.lzz.lsp.cmpt.dict.service.DictService;
import com.lzz.lsp.core.car.service.CarBaseService;
import com.lzz.lsp.core.user.service.UserBaseService;
import com.lzz.lsp.util.CleanChapUtil;
import com.lzz.lsp.util.CommonUtils;
import com.lzz.lsp.util.MongoUtils;
import com.lzz.lton.core.web.controller.BaseController;
import com.lzz.lton.util.HttpUtils;




/**
 * 车辆controller
 * @author tangshuai
 * 2015年8月7日
 */
@Controller
@RequestMapping("/car")
public class CarController extends BaseController{
	private static Logger logger = Logger.getLogger(CarController.class);
	@Resource(name="carBaseService")
	private CarBaseService carBaseService; 
	
	
	@Resource(name = "dictService")
	private DictService dictService;
	
	@Resource(name = "userBaseService")
	private UserBaseService userBaseService;
	/**
	 * 获取附近平台的车辆(老版本)
	 * @param accessToken
	 * @param pageNo
	 * @param slng
	 * @param slat
	 * @param carType
	 * @param carLength
	 * @return
	 * @author tangshuai
	 */
	@RequestMapping(value = "/getNearCarsOfPlatform.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getNearCarsOfPlatform(String accessToken, CarQuery queryObject){
		
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			queryObject.setUserType(DataConstants.DRIVER);
			// 获取信息
			List<CarVO> carVoList = carBaseService.queryCars(queryObject);
			for (CarVO carVo : carVoList) {
				//定位访问url
				if(!StringUtils.isEmpty(carVo.getLocationName())){
					carVo.setLocationUrl("/base/service/toMapPoi.do?location="+carVo.getLongitude()+","+carVo.getLatitude()+"&poi="+URLEncoder.encode(carVo.getLocationName(),"utf-8"));
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("nearCarsOfPlatformList", carVoList);
			model.put("nearCarsCount", carBaseService.queryCarsCount(DataConstants.DRIVER) + 100000);
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
	 * <p>获取附近平台的车辆(新版本)</p>
	 * @author zhaoyx 2016年6月16日
	 * @param accessToken
	 * @param queryObject
	 * @return
	 */
	@RequestMapping(value = "/getPlatformCars.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getPlatformCars(String accessToken, CarQuery queryObject){
		logger.info("========================getPlatformCars========start=====>>"+new Date());
		Map<String, Object> model = new HashMap<String, Object>();
		if(queryObject.getCarLengths()!=null && queryObject.getCarLengths().length>0){
			String carLength=queryObject.getCarLengths()[0];
			String[] carLengths= null;
			if(carLength.contains("以下") || carLength.contains("以内")){
				carLength=CleanChapUtil.cleanspchar(carLength);
			    carLengths=carLength.split("\\-");
			    if(carLengths.length>0){
			    	queryObject.setCarLengthMin(Float.valueOf(carLengths[0].trim()));
			    }
			}else if(carLength.contains("以上") || carLength.contains("以外")){
				carLength=CleanChapUtil.cleanspchar(carLength);
				carLengths=carLength.split("\\-");
				if(carLengths.length>0){
					queryObject.setCarLengthMax(Float.valueOf(carLengths[0].trim()));
				}	
			}else if(carLength.contains("不限")){
				carLength=CleanChapUtil.cleanspchar(carLength);
			}else{
				carLength=CleanChapUtil.cleanspchar(carLength);
				carLengths=carLength.split("\\-");
				if(carLengths.length>1){
					queryObject.setCarLengthMin(Float.valueOf(carLengths[0].trim()));
					queryObject.setCarLengthMax(Float.valueOf(carLengths[1].trim()));
				}else{
					queryObject.setCarLengthMin(Float.valueOf(carLengths[0].trim()));
					queryObject.setCarLengthMax(Float.valueOf(carLengths[0].trim()));
				}	
			}
			
		}
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			queryObject.setUserType(DataConstants.DRIVER);
			// 获取信息
//			List<CarVO> carVoList = carBaseService.queryMobilePlatformCars(queryObject);
			logger.info("========================getPlatformCars====MongoDB====start=====>>"+new Date());
			List<CarVO> carVoList = MongoUtils.queryCars(queryObject);
			logger.info("========================getPlatformCars====MongoDB====stop=====>>"+new Date());
		
			
			for (CarVO carVo : carVoList) {
				carVo.setDriveAge(0);
				if(null != carVo.getCarLength()){
					carVo.setCarLengthName(carVo.getCarLength()+"米");
				}
				//根据经纬度计算距离
				Double _lat1 = Double.valueOf(queryObject.getSlat());
				Double _lon1 = Double.valueOf(queryObject.getSlng());
				
				Double _lat2 = Double.valueOf(carVo.getLatitude());
				Double _lon2 = Double.valueOf(carVo.getLongitude());
				double lat1 = (Math.PI / 180) * _lat1;
				double lat2 = (Math.PI / 180) * _lat2;

				double lon1 = (Math.PI / 180) * _lon1;
				double lon2 = (Math.PI / 180) * _lon2;

				// 地球半径
				double R = 6378.1;

				double d = Math.acos(Math.sin(lat1) * Math.sin(lat2)
						+ Math.cos(lat1) * Math.cos(lat2)
						* Math.cos(lon2 - lon1))
						* R;

				double doubleValue = new BigDecimal(d).setScale(4, BigDecimal.ROUND_HALF_UP)
						.doubleValue();
				double ceil = Math.ceil(doubleValue);
				
				carVo.setDistance("距离您大约"+ceil+"公里");
			    Integer carTypeFirstCode=carVo.getCarTypeFirstCode();
				Integer carTypeSecondCode=carVo.getCarTypeSecondCode();
				Integer carTypeThirdCode=carVo.getCarTypeThirdCode();
				String carTypeCode = carVo.getCarTypeCode();
				String defineCarType = carVo.getDefineCarType();
				if(StringUtils.isEmpty(carTypeFirstCode) && StringUtils.isEmpty(carTypeSecondCode) && StringUtils.isEmpty(carTypeThirdCode) && StringUtils.isEmpty(defineCarType)){
					carVo.setCarTypeCode(carTypeCode);
				}
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
					carVo.setCarTypeName(carTypeStr);
				}
				
			//定位访问url
			if(!StringUtils.isEmpty(carVo.getLocationName())){
					carVo.setLocationUrl("/base/service/toMapPoi.do?location="+carVo.getLongitude()+","+carVo.getLatitude()+"&poi="+URLEncoder.encode(carVo.getLocationName(),"utf-8"));
				}
			}
			
//			//根据距离进行排序
//			 Collections.sort(carVoList, new Comparator<CarVO>(){  
//				  
//		            /*  
//		             * int compare(Student o1, Student o2) 返回一个基本类型的整型，  
//		             * 返回负数表示：o1 小于o2，  
//		             * 返回0 表示：o1和o2相等，  
//		             * 返回正数表示：o1大于o2。  
//		             */  
//		            public int compare(CarVO o1, CarVO o2) {  
//		              
//		                if(Double.valueOf(o1.getDistance()) > Double.valueOf(o2.getDistance())){  
//		                    return 1;  
//		                }  
//		                if(Double.valueOf(o1.getDistance()) == Double.valueOf(o2.getDistance())){  
//		                    return 0;  
//		                }  
//		                return -1;  
//		            }
//
//		        }); 
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("nearCarsOfPlatformList", carVoList);
			model.put("nearCarsCount", carBaseService.queryCarsCount(DataConstants.DRIVER));
			model.put("totalPageNum", queryObject.getTotalPageNum());
			logger.info("========================getPlatformCars========stop=====>>"+new Date());
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
	 * <p>查看平台车辆详情</p>
	 * @author zhaoyx 2016年5月31日
	 * @param accessToken
	 * @param queryObject
	 * @return
	 */
	@RequestMapping(value = "/getCarInfoByCarId.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getCarInfoByCarId(String accessToken, CarQuery queryObject){
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			// 校验令牌
			CommonUtils.checkAccessToken(accessToken, model);
			// 获取平台车辆信息
//			List<CarVO> carVos = carBaseService.getCarInfoByCarId(queryObject);
			List<CarVO> carVos = MongoUtils.queryCars(queryObject);
			for (CarVO carVo : carVos) {
				if(null != carVo.getCarLength()){
					carVo.setCarLengthName(carVo.getCarLength()+"米");
				}
				if(!StringUtils.isEmpty(carVo.getCarTypeThirdCode())){
					carVo.setCarTypeCode(carVo.getCarTypeThirdCode().toString());
				}else if(!StringUtils.isEmpty(carVo.getCarTypeSecondCode())){
					carVo.setCarTypeCode(carVo.getCarTypeSecondCode().toString());
				}else if(!StringUtils.isEmpty(carVo.getCarTypeFirstCode())){
					carVo.setCarTypeCode(carVo.getCarTypeFirstCode().toString());
				}else{
					carVo.setCarTypeCode("");
				}	
				
				if(StringUtils.isEmpty(carVo.getDrivingLicensePhoto())){
					
					carVo.setDrivingLicensePhoto("未上传");
					
				}else{
					carVo.setUserPhoto(carVo.getDrivingLicensePhoto());
					carVo.setDrivingLicensePhoto("已上传");
				}
				//查询车辆信息
				CarVO car = carBaseService.queryBaseCarsByCarId(carVo.getId());
				if(null != car){
					CarVO runCityByUserId = carBaseService.getRunCityByUserId(car.getUserId());
					if(!StringUtils.isEmpty(car.getCarTypeName())){
						carVo.setCarTypeName(car.getCarTypeName());
					}
					if(null != runCityByUserId){
						
						carVo.setDriverRunCityName(runCityByUserId.getDriverRunCityName());
					}
					carVo.setIdcardNo(car.getIdCardNo());
					if(StringUtils.isEmpty(car.getDrivingLicensePhoto())){
						
						carVo.setDrivingLicensePhoto("未上传");
						
					}else{
						carVo.setUserPhoto(car.getDrivingLicensePhoto());
						carVo.setDrivingLicensePhoto("已上传");
					}
				}
			
				//定位访问url
				if(!StringUtils.isEmpty(carVo.getLocationName())){
					carVo.setLocationUrl("/base/service/toMapPoi.do?location="+carVo.getLongitude()+","+carVo.getLatitude()+"&poi="+URLEncoder.encode(carVo.getLocationName(),"utf-8"));
				}
			}
			model.put("code", ReturnDatas.SUCCESS_CODE);
			model.put("carVo", carVos);
		} catch (Exception e) {
			if(model.isEmpty()){
				model.put("code", ReturnDatas.SYSTEM_EXCEPTION);
				model.put("msg", ReturnDatas.SYSTEM_EXCEPTION_MSG);
			}
			e.printStackTrace();
		}
		return model;
	}
	
	public static void main(String[]args){
		try {
			//http://www.lzz56.com:9090/aspp-server/car/getNearCarsOfPlatform.do?pageNo=1&slng=116.315095&slat=40.04395&dirverRunCityCode=&carTypes=&carLengths=&accessToken=5a8e5467f3a1434b7c265268bf76c338
			//&slng=116.315095&slat=40.04395
			String str = HttpUtils.sendHttpRequest("http://127.0.0.1:8080/aspp-server/car/getNearCarsOfPlatform.do?pageNo=1&dirverRunCityCode=&carTypes=&carLengths=&accessToken=5a8e5467f3a1434b7c265268bf76c338", HttpUtils.RequestMethod.GET, null);
			System.out.println(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//判断参数编码格式
		public static String getEncoding(String str) {      
		       String encode = "GB2312";      
		      try {      
		          if (str.equals(new String(str.getBytes(encode), encode))) {      
		               String s = encode;      
		              return s;      
		           }      
		       } catch (Exception exception) {      
		       }      
		       encode = "ISO-8859-1";      
		      try {      
		          if (str.equals(new String(str.getBytes(encode), encode))) {      
		               String s1 = encode;      
		              return s1;      
		           }      
		       } catch (Exception exception1) {      
		       }      
		       encode = "UTF-8";      
		      try {      
		          if (str.equals(new String(str.getBytes(encode), encode))) {      
		               String s2 = encode;      
		              return s2;      
		           }      
		       } catch (Exception exception2) {      
		       }      
		       encode = "GBK";      
		      try {      
		          if (str.equals(new String(str.getBytes(encode), encode))) {      
		               String s3 = encode;      
		              return s3;      
		           }      
		       } catch (Exception exception3) {      
		       }      
		      return "";      
		   }      
}
