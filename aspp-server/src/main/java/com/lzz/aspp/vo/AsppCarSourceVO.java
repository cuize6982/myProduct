package com.lzz.aspp.vo;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.lzz.lton.core.query.QueryObject;


/**
 * 车源VO对象
 * <p>
 * 描述这个类的含义
 * </p>
 * 
 * @author Liuyl 2015年5月15日
 * @version 1.0
 *          <p>
 *          注意事项：
 *          </p>
 */
public class AsppCarSourceVO extends QueryObject<AsppCarSourceVO>{

	
	/** 回程车ID */
	@Field(value="id")
	private Integer carSourceId;

	/** 出发地 */
	@Field
	private String fromPlace;

	/** 目的地 */
	@Field
	private String toPlace;

	/** 出发地的省编码 */
	private Integer fromPCode;

	/** 出发地的市编码 */
	private Integer fromCCode;

	/** 出发地的区县编码 */
	private Integer fromTCode;

	/** 目的地的省编码 */
	private Integer toPCode;

	/** 目的地的市编码 */
	private Integer toCCode;

	/** 目的地的区县编码 */
	private Integer toTCode;

	/** 车辆ID */
	private Integer carId;

	/** 车牌号 */
	@Field
	private String carNo;

	/** 回程日期 */
	private String returnBackDate;

	/** 常用语 */
	private String commonPhrase;

	/** 说明 */
	@Field
	private String mark;

	/** 联系人 */
	@Field(value="linkman")
	private String linkMan;

	/** 联系人手机 */
	@Field(value="linkMobile1")
	private String linkMobile;

	/** 联系人QQ */
	private String linkQQ;

	/** 用户id */
	@Field
	private Integer userId;

	/** 信息类型（1货源 2车源 3整车专线4：零担专线） */
	@Field
	private Integer infoType;

	/** 有效日期 */
	private String expiryDate;

	/** 信息状态 */
	private Integer status;

	/** 发布时间 */
	@Field
	private Date publishTime;

	/** 显示日期 */
	private String displayTime;
	
	/** 发布信息ID */
	@Field
	private Integer publishId;
	
	/** 发布人id */
	@Field(value="userId")
	private Integer publishUserId;
	/** 发布人姓名 */
	private String publishUserName;
	/** 车长code */
	@Field
	private float carLength;
	/** 车长名称 */
	@Field
	private String carLengthName;
	/** 车型code */
	@Field
	private String carType;
	/** 车型名称 */
	@Field
	private String carTypeName;
	/** 载重 */
	@Field
	private Float loadWeight;
	/** 车源类型名称 */
	@Field
	private String sourceTypeName;
	/** 车源类型 */
	@Field
	private String sourceType;
	/** 车辆图片id */
	@Field
	private String carPhoto;
	/** 驾龄 */
	private String driving;
	/** 身份证 */
	@Field(value="idcardNo")
	private String userIDNO;
	/** 驾驶证 */
	@Field
	private String drivingLicence;
	/** 最近位置名称 */
	private String position;
	/** 定位时间 */
	private String positionTime;
	/** 定位url */
	private String positionUrl;
	/** 经度 */
	private String longitude;
	/** 维度 */
	private String latitude;
	/** 用户类型  */
	@Field
	private Integer userType;
	/** 用户类型 名称 */
	@Field
	private String userTypeName;
	/** 信息内容 */
	@Field
	private String infoContent;
	/** 是否认证 */
	@Field
	private Integer isAuth;
	/** 自定义车型 */
	@Field
	private String defineCarType;

	/** 车型图片code */
	@Field
	private String CAR_TYPE_CODE;
	
	
	
	
	public String getCAR_TYPE_CODE() {
		return CAR_TYPE_CODE;
	}

	public void setCAR_TYPE_CODE(String cAR_TYPE_CODE) {
		CAR_TYPE_CODE = cAR_TYPE_CODE;
	}

	public String getDefineCarType() {
		return defineCarType;
	}

	public void setDefineCarType(String defineCarType) {
		this.defineCarType = defineCarType;
	}

	/**收藏id */
	private Integer favoriteId;
	
	private String userPhoto;
	
	/**行驶证照片*/
	private String drivingLicensePhoto;
	
	private Integer infoId;
	
	
	private Integer isComplain;
	
	/**车型编码第一字段*/
	@Field
	private Integer carTypeFirstCode;
	
	/**车型编码第二字段*/
	@Field
	private Integer carTypeSecondCode;
	
	/**车型编码第三字段*/
	@Field
	private Integer carTypeThirdCode;
	
	/**信息来源*/
	private String publisher;
	
	
	/**联系人电话*/
	private String linkPhone;
	
	/**联系人手机1*/
	private String linkMobile1;
	
	/**联系人手机2*/
	private String linkMobile2;
	
	private Long minutes;
	
	/**信息ID*/
	private Integer id;
	
	/**发布频率【分钟/次】*/
	private Integer publishFreq;
	
	/**重发次数【0：表示不重发】*/
	private Integer repateTimes;
	
	private Long publishTimeLong;
	
	public Long getPublishTimeLong() {
		return publishTimeLong;
	}

	public void setPublishTimeLong(Long publishTimeLong) {
		this.publishTimeLong = publishTimeLong;
	}

	public Integer getPublishFreq() {
		return publishFreq;
	}

	public void setPublishFreq(Integer publishFreq) {
		this.publishFreq = publishFreq;
	}

	public Integer getRepateTimes() {
		return repateTimes;
	}

	public void setRepateTimes(Integer repateTimes) {
		this.repateTimes = repateTimes;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getMinutes() {
		return minutes;
	}

	public void setMinutes(Long minutes) {
		this.minutes = minutes;
	}


	public String getLinkPhone() {
		return linkPhone;
	}

	public void setLinkPhone(String linkPhone) {
		this.linkPhone = linkPhone;
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

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
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

	public Integer getInfoId() {
		return infoId;
	}

	public void setInfoId(Integer infoId) {
		this.infoId = infoId;
	}

	public Integer getIsComplain() {
		return isComplain;
	}

	public void setIsComplain(Integer isComplain) {
		this.isComplain = isComplain;
	}

	public String getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
	}

	public Integer getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	public String getInfoContent() {
		return infoContent;
	}

	public void setInfoContent(String infoContent) {
		this.infoContent = infoContent;
	}

	public String getUserTypeName() {
		return userTypeName;
	}

	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getPositionUrl() {
		return positionUrl;
	}

	public void setPositionUrl(String positionUrl) {
		this.positionUrl = positionUrl;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getPublishUserName() {
		return publishUserName;
	}

	public void setPublishUserName(String publishUserName) {
		this.publishUserName = publishUserName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPositionTime() {
		return positionTime;
	}

	public void setPositionTime(String positionTime) {
		this.positionTime = positionTime;
	}

	public String getDrivingLicence() {
		return drivingLicence;
	}

	public void setDrivingLicence(String drivingLicence) {
		this.drivingLicence = drivingLicence;
	}

	public String getUserIDNO() {
		return userIDNO;
	}

	public void setUserIDNO(String userIDNO) {
		this.userIDNO = userIDNO;
	}

	public String getDriving() {
		return driving;
	}

	public void setDriving(String driving) {
		this.driving = driving;
	}

	public String getCarPhoto() {
		return carPhoto;
	}

	public void setCarPhoto(String carPhoto) {
		this.carPhoto = carPhoto;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceTypeName() {
		return sourceTypeName;
	}

	public void setSourceTypeName(String sourceTypeName) {
		this.sourceTypeName = sourceTypeName;
	}

	public Float getLoadWeight() {
		return loadWeight;
	}

	public void setLoadWeight(Float loadWeight) {
		this.loadWeight = loadWeight;
	}

	public float getCarLength() {
		return carLength;
	}

	public void setCarLength(float carLength) {
		this.carLength = carLength;
	}

	/*public String getCarLength() {
		return carLength;
	}

	public void setCarLength(String carLength) {
		this.carLength = carLength;
	}
*/
	public String getCarLengthName() {
		return carLengthName;
	}

	public void setCarLengthName(String carLengthName) {
		this.carLengthName = carLengthName;
	}
	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarTypeName() {
		return carTypeName;
	}

	public void setCarTypeName(String carTypeName) {
		this.carTypeName = carTypeName;
	}

	public Integer getPublishUserId() {
		return publishUserId;
	}

	public void setPublishUserId(Integer publishUserId) {
		this.publishUserId = publishUserId;
	}

	public Integer getCarSourceId() {
		return carSourceId;
	}

	public void setCarSourceId(Integer carSourceId) {
		this.carSourceId = carSourceId;
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

	public Integer getFromPCode() {
		return fromPCode;
	}

	public void setFromPCode(Integer fromPCode) {
		this.fromPCode = fromPCode;
	}

	public Integer getFromCCode() {
		return fromCCode;
	}

	public void setFromCCode(Integer fromCCode) {
		this.fromCCode = fromCCode;
	}

	public Integer getFromTCode() {
		return fromTCode;
	}

	public void setFromTCode(Integer fromTCode) {
		this.fromTCode = fromTCode;
	}

	public Integer getToPCode() {
		return toPCode;
	}

	public void setToPCode(Integer toPCode) {
		this.toPCode = toPCode;
	}

	public Integer getToCCode() {
		return toCCode;
	}

	public void setToCCode(Integer toCCode) {
		this.toCCode = toCCode;
	}

	public Integer getToTCode() {
		return toTCode;
	}

	public void setToTCode(Integer toTCode) {
		this.toTCode = toTCode;
	}

	public Integer getCarId() {
		return carId;
	}

	public void setCarId(Integer carId) {
		this.carId = carId;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getReturnBackDate() {
		return returnBackDate;
	}

	public void setReturnBackDate(String returnBackDate) {
		this.returnBackDate = returnBackDate;
	}

	public String getCommonPhrase() {
		return commonPhrase;
	}

	public void setCommonPhrase(String commonPhrase) {
		this.commonPhrase = commonPhrase;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getLinkMobile() {
		return linkMobile;
	}

	public void setLinkMobile(String linkMobile) {
		this.linkMobile = linkMobile;
	}

	public String getLinkQQ() {
		return linkQQ;
	}

	public void setLinkQQ(String linkQQ) {
		this.linkQQ = linkQQ;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getInfoType() {
		return infoType;
	}

	public void setInfoType(Integer infoType) {
		this.infoType = infoType;
	}


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}

	public Integer getPublishId() {
		return publishId;
	}

	public void setPublishId(Integer publishId) {
		this.publishId = publishId;
	}

	public String getDrivingLicensePhoto() {
		return drivingLicensePhoto;
	}

	public void setDrivingLicensePhoto(String drivingLicensePhoto) {
		this.drivingLicensePhoto = drivingLicensePhoto;
	}
}
