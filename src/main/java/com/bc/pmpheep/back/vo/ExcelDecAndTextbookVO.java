package com.bc.pmpheep.back.vo;

import com.bc.pmpheep.annotation.ExcelHeader;

/**
 *职位遴选页面导出名单
 *@author Mr
 *@CreateDate 2017年12月25日 下午2:12:33
 *
 **/
public class ExcelDecAndTextbookVO{
	
	//textbookIds
	private Long textbookId;
	//遴选的职务
	@ExcelHeader(header = "遴选的职务")
	private Integer chosenPosition;
	//作家真实姓名
	@ExcelHeader(header = "姓名")
	private String realname;
	//作家账号
	@ExcelHeader(header = "作家账号")
	private String username;
	//申报单位
	@ExcelHeader(header = "申报单位")
	private String unitName;
	//工作单位
	@ExcelHeader(header = "工作单位")
	private String orgName;
	//作家职务
	@ExcelHeader(header = "作家职务")
	private String position;
    // 排位
	@ExcelHeader(header = "排位")
    private Integer rank;
    //是否为数字编辑
	@ExcelHeader(header = "是否数字编委")
    private Boolean isDigitalEditor;
	//作家职称
	@ExcelHeader(header = "职称")
	private String title;
	//作家手机
	@ExcelHeader(header = "手机")
	private String handphone;
	//作家邮箱
	@ExcelHeader(header = "邮箱")
	private String email;	
	//所选书籍与职务
	private String chooseBooksAndPostions;
	//学校审核进度
	@ExcelHeader(header = "审核进度")
	private Integer onlineProgress;
	//纸质表进度
	@ExcelHeader(header = "纸质表进度")
    private Integer offlineProgress;
    //书籍名称
    private String textbookName;
    //教材名称
    private String materialName;
    //作家证件类型
    @ExcelHeader(header = "证件类型")
  	private Short idtype;
  	//作家证件号码
    @ExcelHeader(header = "证件号码")
  	private String idcard;
    
	public Short getIdtype() {
		return idtype;
	}

	public void setIdtype(Short idtype) {
		this.idtype = idtype;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public Integer getChosenPosition() {
		return chosenPosition;
	}

	public void setChosenPosition(Integer chosenPosition) {
		this.chosenPosition = chosenPosition;
	}

	public String getMaterialName() {
		return materialName;
	}
	
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public Boolean getIsDigitalEditor() {
		return isDigitalEditor;
	}
	public void setIsDigitalEditor(Boolean isDigitalEditor) {
		this.isDigitalEditor = isDigitalEditor;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHandphone() {
		return handphone;
	}
	public void setHandphone(String handphone) {
		this.handphone = handphone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getChooseBooksAndPostions() {
		return chooseBooksAndPostions;
	}
	public void setChooseBooksAndPostions(String chooseBooksAndPostions) {
		this.chooseBooksAndPostions = chooseBooksAndPostions;
	}
	public Integer getOnlineProgress() {
		return onlineProgress;
	}
	public void setOnlineProgress(Integer onlineProgress) {
		this.onlineProgress = onlineProgress;
	}
	public Integer getOfflineProgress() {
		return offlineProgress;
	}
	public void setOfflineProgress(Integer offlineProgress) {
		this.offlineProgress = offlineProgress;
	}
	public String getTextbookName() {
		return textbookName;
	}
	public void setTextbookName(String textbookName) {
		this.textbookName = textbookName;
	}
	
	public Long getTextbookId() {
		return textbookId;
	}

	public void setTextbookId(Long textbookId) {
		this.textbookId = textbookId;
	}
	
	public ExcelDecAndTextbookVO() {
		super();
	}
	

	public ExcelDecAndTextbookVO(Long textbookId, Integer chosenPosition, String realname, String username,
			String unitName, String orgName, String position, Integer rank, Boolean isDigitalEditor, String title,
			String handphone, String email, String chooseBooksAndPostions, Integer onlineProgress,
			Integer offlineProgress, String textbookName, String materialName, Short idtype, String idcard) {
		super();
		this.textbookId = textbookId;
		this.chosenPosition = chosenPosition;
		this.realname = realname;
		this.username = username;
		this.unitName = unitName;
		this.orgName = orgName;
		this.position = position;
		this.rank = rank;
		this.isDigitalEditor = isDigitalEditor;
		this.title = title;
		this.handphone = handphone;
		this.email = email;
		this.chooseBooksAndPostions = chooseBooksAndPostions;
		this.onlineProgress = onlineProgress;
		this.offlineProgress = offlineProgress;
		this.textbookName = textbookName;
		this.materialName = materialName;
		this.idtype = idtype;
		this.idcard = idcard;
	}

	@Override
	public String toString() {
		return "ExcelDecAndTextbookVO [textbookId=" + textbookId + ", chosenPosition=" + chosenPosition + ", realname="
				+ realname + ", username=" + username + ", unitName=" + unitName + ", orgName=" + orgName
				+ ", position=" + position + ", rank=" + rank + ", isDigitalEditor=" + isDigitalEditor + ", title="
				+ title + ", handphone=" + handphone + ", email=" + email + ", chooseBooksAndPostions="
				+ chooseBooksAndPostions + ", onlineProgress=" + onlineProgress + ", offlineProgress=" + offlineProgress
				+ ", textbookName=" + textbookName + ", materialName=" + materialName + ", idtype=" + idtype
				+ ", idcard=" + idcard + "]";
	}

}
