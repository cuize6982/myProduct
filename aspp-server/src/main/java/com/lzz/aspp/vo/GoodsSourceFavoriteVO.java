package com.lzz.aspp.vo;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class GoodsSourceFavoriteVO {

	/**
	 * 收藏id
	 */
	private Integer favoriteId;
	/**
	 * 用户id
	 */
	private Integer userId;
	/**
	 * 货物id
	 */
	private Integer goodsSourceId;
	/**
	 * 出发地
	 */
	private String fromPlace;
	/**
	 * 目的地
	 */
	private String toPlace;
	/**
	 * 信息描述
	 */
	private String infoContent;
	/**
	 * 货物类型
	 */
	private String goodsType;
	/**
	 * 货物类型名称
	 */
	private String goodsTypeName;
	/**
	 * 货物名称
	 */
	private String goodsName;
	/**
	 * 货物种类
	 */
	private String goodsClass;
	/**
	 * 载重
	 */
	private String weight;
	/**
	 * 载重单位
	 */
	private String weightUnit;
	/**
	 * 载重单位名称
	 */
	private String weightUnitName;
	/**
	 * 体积
	 */
	private String volume;
	/**
	 * 体积单位
	 */
	private String volumeUnit;
	/**
	 * 体积单位名称
	 */
	private String volumeUnitName;
	/**
	 * 车型
	 */
	private String carTypeNeed;
	/**
	 * 车型名称
	 */
	private String carTypeNeedName;
	/**
	 * 车长
	 */
	private String carLengthNeed;
	/**
	 * 车长名称
	 */
	private String carLengthNeedName;
	/**
	 * 运价
	 */
	private Double price;
	/**
	 * 运价单位
	 */
	private String priceUnit;
	/**
	 * 运价单位名称
	 */
	private String priceUnitName;
	/**
	 * 联系人
	 */
	private String linkMan;
	/**
	 * 联系人手机1
	 */
	private String linkMobile1;
	/**
	 * 联系人手机2
	 */
	private String linkMobile2;
	/**
	 * 固定电话
	 */
	private String linkPhone;
	/**
	 * 发布时间
	 */
	private Date publishTime;
	/**
	 * 企业名称
	 */
	private String enterpriseName;
	/**
	 * 是否结构化
	 */
	private String isStruct;
	
	private String isAuth;
	
	private Integer userGrad;
	
	/**两地距离/km*/
	@Field
	private String placeKilometre;
	
	/**
	 * 车型编码第一字段
	 */
	private Integer carTypeFirstCode;
	
	/**
	 * 车型编码第二字段
	 */
	private Integer carTypeSecondCode;
	
	/**
	 * 车型编码第三字段
	 */
	private Integer carTypeThirdCode;
	
	/**
	 * 车型 取值来源于车型表
	 */
	private String carTypeCode;
	
	/**自定义车型*/
	private String defineCarType;
	
	/**公司地址*/
	private String address;
	
	/**取货天数*/
	private Integer claimGoodsDays;
	
	/**货源类型【01:零担  02：整车】*/
	private String goodsSourceInfoType;
	
	public Integer getClaimGoodsDays() {
		return claimGoodsDays;
	}

	public void setClaimGoodsDays(Integer claimGoodsDays) {
		this.claimGoodsDays = claimGoodsDays;
	}

	public String getGoodsSourceInfoType() {
		return goodsSourceInfoType;
	}

	public void setGoodsSourceInfoType(String goodsSourceInfoType) {
		this.goodsSourceInfoType = goodsSourceInfoType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDefineCarType() {
		return defineCarType;
	}

	public void setDefineCarType(String defineCarType) {
		this.defineCarType = defineCarType;
	}

	public String getCarTypeCode() {
		return carTypeCode;
	}

	public void setCarTypeCode(String carTypeCode) {
		this.carTypeCode = carTypeCode;
	}

	public Integer getCarTypeFirstCode() {
		return carTypeFirstCode;
	}

	public void setCarTypeFirstCode(Integer carTypeFirstCode) {
		this.carTypeFirstCode = carTypeFirstCode;
	}

	public Integer getCarTypeSecondCode() {
		return carTypeSecondCode;
	}

	public void setCarTypeSecondCode(Integer carTypeSecondCode) {
		this.carTypeSecondCode = carTypeSecondCode;
	}

	public Integer getCarTypeThirdCode() {
		return carTypeThirdCode;
	}

	public void setCarTypeThirdCode(Integer carTypeThirdCode) {
		this.carTypeThirdCode = carTypeThirdCode;
	}

	public String getPlaceKilometre() {
		return placeKilometre;
	}

	public void setPlaceKilometre(String placeKilometre) {
		this.placeKilometre = placeKilometre;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public Integer getUserGrad() {
		return userGrad;
	}

	public void setUserGrad(Integer userGrad) {
		this.userGrad = userGrad;
	}

	public String getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(String isAuth) {
		this.isAuth = isAuth;
	}

	public String getGoodsClass() {
		return goodsClass;
	}

	public void setGoodsClass(String goodsClass) {
		this.goodsClass = goodsClass;
	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getGoodsSourceId() {
		return goodsSourceId;
	}

	public void setGoodsSourceId(Integer goodsSourceId) {
		this.goodsSourceId = goodsSourceId;
	}

	public String getFromPlace() {
		return fromPlace;
	}

	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	public String getToPlace() {
		return toPlace;
	}

	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}

	public String getInfoContent() {
		return infoContent;
	}

	public void setInfoContent(String infoContent) {
		this.infoContent = infoContent;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getGoodsTypeName() {
		return goodsTypeName;
	}

	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public String getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(String volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public String getCarTypeNeed() {
		return carTypeNeed;
	}

	public void setCarTypeNeed(String carTypeNeed) {
		this.carTypeNeed = carTypeNeed;
	}

	public String getCarLengthNeed() {
		return carLengthNeed;
	}

	public void setCarLengthNeed(String carLengthNeed) {
		this.carLengthNeed = carLengthNeed;
	}

	public String getCarTypeNeedName() {
		return carTypeNeedName;
	}

	public void setCarTypeNeedName(String carTypeNeedName) {
		this.carTypeNeedName = carTypeNeedName;
	}

	public String getCarLengthNeedName() {
		return carLengthNeedName;
	}

	public void setCarLengthNeedName(String carLengthNeedName) {
		this.carLengthNeedName = carLengthNeedName;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getLinkMobile1() {
		return linkMobile1;
	}

	public void setLinkMobile1(String linkMobile1) {
		this.linkMobile1 = linkMobile1;
	}

	public String getLinkMobile2() {
		return linkMobile2;
	}

	public void setLinkMobile2(String linkMobile2) {
		this.linkMobile2 = linkMobile2;
	}

	public String getLinkPhone() {
		return linkPhone;
	}

	public void setLinkPhone(String linkPhone) {
		this.linkPhone = linkPhone;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getIsStruct() {
		return isStruct;
	}

	public void setIsStruct(String isStruct) {
		this.isStruct = isStruct;
	}

	public String getWeightUnitName() {
		return weightUnitName;
	}

	public void setWeightUnitName(String weightUnitName) {
		this.weightUnitName = weightUnitName;
	}

	public String getVolumeUnitName() {
		return volumeUnitName;
	}

	public void setVolumeUnitName(String volumeUnitName) {
		this.volumeUnitName = volumeUnitName;
	}

	public String getPriceUnitName() {
		return priceUnitName;
	}

	public void setPriceUnitName(String priceUnitName) {
		this.priceUnitName = priceUnitName;
	}

}
