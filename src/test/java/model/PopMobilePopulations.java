/*
 * Copyright (c) 2005, 2016, EVECOM Technology Co.,Ltd. All rights reserved.
 * EVECOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package model;

import java.util.Date;

/**
 * 人口流动（含境外）信息（基本）.
 * @author Iverson Cai      
 * @created 2017 /05/15 17:27:06
 */
public class PopMobilePopulations {
    /**
     * 标识.
     */
    private Long id;
    /**
     * 人员id.
     */
    private Long popId;
    /**
     * 是否境外人士.
     */
    private String isForeigner;
    /**
     * 流动类型.
     */
    private String mobileType;
    /**
     * 来华目的.
     */
    private String visaType;
    /**
     * 登记日期.
     */
    private Date dateOfRegister;
    /**
     * 登记的证件.
     */
    private Long regCertId;
    /**
     * 暂住事由(流入原因）.
     */
    private String stayType;
    /**
     * 流入日期.
     */
    private Date stayDate;
    /**
     * 拟暂住期限.
     */
    private Date stayLimit;
    /**
     * 离开时间.
     */
    private Date leaveDate;
    /**
     * 流出原因.
     */
    private String leaveType;
    /**
     * 来自的国家.
     */
    private String fromCountry;
    /**
     * 来自何地.
     */
    private String fromAddrRsn;
    /**
     * 来自地详址.
     */
    private String fromAddr;
    /**
     * 来自何地ID.
     */
    private Long fromAddrId;
    /**
     * 关注级别.
     */
    private String concernedLevel;
    /**
     * 住所类型.
     */
    private String resiType;
    /**
     * 办证类型.
     */
    private String certType;
    /**
     * 证件号码.
     */
    private String certIdNumber;
    /**
     * 证件到期时间.
     */
    private Date certExpirationDate;
    /**
     * 有无上岗证.
     */
    private String havingWorkLicense;
    /**
     * 有无就业证.
     */
    private String havingEmployCard;
    /**
     * 所属派出所.
     */
    private String policeStation;
    /**
     * 原属党组织.
     */
    private String originalParty;
    /**
     * 劳动合同签订情况.
     */
    private String contractStatus;
    /**
     * 建立健康档案.
     */
    private String isHealthRecord;
    /**
     * 接种疫苗.
     */
    private String isVaccine;
    /**
     * 接受孕产妇保健.
     */
    private String isMaternityCare;
    /**
     * 接受计划生育服务.
     */
    private String isFpService;
    /**
     * 接受健康教育.
     */
    private String isHealthEducation;
    /**
     * 传染病.
     */
    private String isInfection;
    /**
     * 接受儿童保健.
     */
    private String isChildHealthCare;
    /**
     * 是否领独生证
     */
    private String isSccIssued;
    /**
     * 创建人
     */
    private Long creatorId;
    /**
     * 创建机构
     */
    private Long createOrgId;
    /**
     * 创建时间
     */
    private Date timeCreated;
    /**
     * 修改人
     */
    private Long modifierId;
    /**
     * 修改时间
     */
    private Date timeModified;
    /**
     * 身份证
     */
    private String idCardNum;
    /**
     * 所属网格
     */
    private String gridName;
    /**
     * 证件类型名称
     */
    private String certTypeName;
    private String excelInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPopId() {
        return popId;
    }

    public void setPopId(Long popId) {
        this.popId = popId;
    }

    public String getIsForeigner() {
        return isForeigner;
    }

    public void setIsForeigner(String isForeigner) {
        this.isForeigner = isForeigner;
    }

    public String getMobileType() {
        return mobileType;
    }

    public void setMobileType(String mobileType) {
        this.mobileType = mobileType;
    }

    public String getVisaType() {
        return visaType;
    }

    public void setVisaType(String visaType) {
        this.visaType = visaType;
    }

    public Date getDateOfRegister() {
        return dateOfRegister;
    }

    public void setDateOfRegister(Date dateOfRegister) {
        this.dateOfRegister = dateOfRegister;
    }

    public Long getRegCertId() {
        return regCertId;
    }

    public void setRegCertId(Long regCertId) {
        this.regCertId = regCertId;
    }

    public String getStayType() {
        return stayType;
    }

    public void setStayType(String stayType) {
        this.stayType = stayType;
    }

    public Date getStayDate() {
        return stayDate;
    }

    public void setStayDate(Date stayDate) {
        this.stayDate = stayDate;
    }

    public Date getStayLimit() {
        return stayLimit;
    }

    public void setStayLimit(Date stayLimit) {
        this.stayLimit = stayLimit;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(String fromCountry) {
        this.fromCountry = fromCountry;
    }

    public String getFromAddrRsn() {
        return fromAddrRsn;
    }

    public void setFromAddrRsn(String fromAddrRsn) {
        this.fromAddrRsn = fromAddrRsn;
    }

    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public Long getFromAddrId() {
        return fromAddrId;
    }

    public void setFromAddrId(Long fromAddrId) {
        this.fromAddrId = fromAddrId;
    }

    public String getConcernedLevel() {
        return concernedLevel;
    }

    public void setConcernedLevel(String concernedLevel) {
        this.concernedLevel = concernedLevel;
    }

    public String getResiType() {
        return resiType;
    }

    public void setResiType(String resiType) {
        this.resiType = resiType;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertIdNumber() {
        return certIdNumber;
    }

    public void setCertIdNumber(String certIdNumber) {
        this.certIdNumber = certIdNumber;
    }

    public Date getCertExpirationDate() {
        return certExpirationDate;
    }

    public void setCertExpirationDate(Date certExpirationDate) {
        this.certExpirationDate = certExpirationDate;
    }

    public String getHavingWorkLicense() {
        return havingWorkLicense;
    }

    public void setHavingWorkLicense(String havingWorkLicense) {
        this.havingWorkLicense = havingWorkLicense;
    }

    public String getHavingEmployCard() {
        return havingEmployCard;
    }

    public void setHavingEmployCard(String havingEmployCard) {
        this.havingEmployCard = havingEmployCard;
    }

    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }

    public String getOriginalParty() {
        return originalParty;
    }

    public void setOriginalParty(String originalParty) {
        this.originalParty = originalParty;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getIsHealthRecord() {
        return isHealthRecord;
    }

    public void setIsHealthRecord(String isHealthRecord) {
        this.isHealthRecord = isHealthRecord;
    }

    public String getIsVaccine() {
        return isVaccine;
    }

    public void setIsVaccine(String isVaccine) {
        this.isVaccine = isVaccine;
    }

    public String getIsMaternityCare() {
        return isMaternityCare;
    }

    public void setIsMaternityCare(String isMaternityCare) {
        this.isMaternityCare = isMaternityCare;
    }

    public String getIsFpService() {
        return isFpService;
    }

    public void setIsFpService(String isFpService) {
        this.isFpService = isFpService;
    }

    public String getIsHealthEducation() {
        return isHealthEducation;
    }

    public void setIsHealthEducation(String isHealthEducation) {
        this.isHealthEducation = isHealthEducation;
    }

    public String getIsInfection() {
        return isInfection;
    }

    public void setIsInfection(String isInfection) {
        this.isInfection = isInfection;
    }

    public String getIsChildHealthCare() {
        return isChildHealthCare;
    }

    public void setIsChildHealthCare(String isChildHealthCare) {
        this.isChildHealthCare = isChildHealthCare;
    }

    public String getIsSccIssued() {
        return isSccIssued;
    }

    public void setIsSccIssued(String isSccIssued) {
        this.isSccIssued = isSccIssued;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getCreateOrgId() {
        return createOrgId;
    }

    public void setCreateOrgId(Long createOrgId) {
        this.createOrgId = createOrgId;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public Date getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Date timeModified) {
        this.timeModified = timeModified;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getCertTypeName() {
        return certTypeName;
    }

    public void setCertTypeName(String certTypeName) {
        this.certTypeName = certTypeName;
    }

    public void setExcelInfo(String excelInfo) {
        this.excelInfo = excelInfo;
    }

    public String getExcelInfo() {
        return excelInfo;
    }

    @Override
    public String toString() {
        return "PopMobilePopulations{" +
                "id=" + id +
                ", popId=" + popId +
                ", isForeigner='" + isForeigner + '\'' +
                ", mobileType='" + mobileType + '\'' +
                ", visaType='" + visaType + '\'' +
                ", dateOfRegister=" + dateOfRegister +
                ", regCertId=" + regCertId +
                ", stayType='" + stayType + '\'' +
                ", stayDate=" + stayDate +
                ", stayLimit=" + stayLimit +
                ", leaveDate=" + leaveDate +
                ", leaveType='" + leaveType + '\'' +
                ", fromCountry='" + fromCountry + '\'' +
                ", fromAddrRsn='" + fromAddrRsn + '\'' +
                ", fromAddr='" + fromAddr + '\'' +
                ", fromAddrId=" + fromAddrId +
                ", concernedLevel='" + concernedLevel + '\'' +
                ", resiType='" + resiType + '\'' +
                ", certType='" + certType + '\'' +
                ", certIdNumber='" + certIdNumber + '\'' +
                ", certExpirationDate=" + certExpirationDate +
                ", havingWorkLicense='" + havingWorkLicense + '\'' +
                ", havingEmployCard='" + havingEmployCard + '\'' +
                ", policeStation='" + policeStation + '\'' +
                ", originalParty='" + originalParty + '\'' +
                ", contractStatus='" + contractStatus + '\'' +
                ", isHealthRecord='" + isHealthRecord + '\'' +
                ", isVaccine='" + isVaccine + '\'' +
                ", isMaternityCare='" + isMaternityCare + '\'' +
                ", isFpService='" + isFpService + '\'' +
                ", isHealthEducation='" + isHealthEducation + '\'' +
                ", isInfection='" + isInfection + '\'' +
                ", isChildHealthCare='" + isChildHealthCare + '\'' +
                ", isSccIssued='" + isSccIssued + '\'' +
                ", creatorId=" + creatorId +
                ", createOrgId=" + createOrgId +
                ", timeCreated=" + timeCreated +
                ", modifierId=" + modifierId +
                ", timeModified=" + timeModified +
                ", idCardNum='" + idCardNum + '\'' +
                ", gridName='" + gridName + '\'' +
                ", certTypeName='" + certTypeName + '\'' +
                ", excelInfo='" + excelInfo + '\'' +
                '}';
    }
}
