package com.lzz.aspp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class CommonUtil {
	public static List<Integer> changeStringToList(String attIds){
		if(StringUtils.hasText(attIds)){
			List<Integer> idList = new ArrayList<Integer>();
			for(String id:attIds.split(",")){
				idList.add(Integer.parseInt(id));
			}
			return idList;
		}
		return null;
	}
	
	/**
	 * <p>隐藏身份证重要信息</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年8月7日
	 * @param idCarNO
	 * @return
	 */
	public static String replaceIdCarNO(String idCarNO){
		if(com.lzz.lton.util.StringUtils.isBlank(idCarNO)){
			return null;
		}
		String reIdCarNO = "";
		if(15 == idCarNO.length()){
			reIdCarNO = idCarNO.substring(0, idCarNO.length() - (idCarNO.substring(6)).length())
					+ "********" + idCarNO.substring(14); 
		} else if (18 == idCarNO.length()){
			reIdCarNO = idCarNO.substring(0, idCarNO.length() - (idCarNO.substring(8)).length())
					+ "*********" + idCarNO.substring(17); 
		} else {
			reIdCarNO = idCarNO;
		}
		return reIdCarNO;
	}
	
	/**
     * 根据经纬度，获取两点间的距离
     * @param slng 起始经度
     * @param slat 起始纬度
     * @param elng 终点经度
     * @param elat 终点纬度
     * @return
     * @author TAGNS 2015-8-13
     */
    public static double getDistance(double slng, double slat, double elng, double elat) {
        double sradLat = slat * Math.PI / 180;
        double eradLat = elat * Math.PI / 180;
        double a = sradLat - eradLat;
        double b = slng * Math.PI / 180 - elng * Math.PI / 180;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(sradLat)
                * Math.cos(eradLat) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;// 取WGS84标准参考椭球中的地球长半径(单位:km)
        s = Math.round(s * 10000) / 10000;
        return Math.ceil(s);
    }
	/**
	 * <p>是否为数字</p>
	 * <p>执行流程：</p>
	 * <p>注意事项：</p>
	 * @author Liuyl 2015年10月29日
	 * @param str
	 * @return
	 */
    public static boolean isNumeric(String str){ 
    	   return Pattern.compile("[0-9]*").matcher(str).matches();
    	}
	
	
	public static void main(String[] args) {
		String str = "3612345a678";
		System.out.println(isNumeric(str));
	}
}
