package com.gzgb.epo.webservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 采集点bean
 */
@XmlRootElement(namespace = "http://guang.crawler.controller.webservice/")
public class WebGatherNodeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 自增ID
	 */
	private Long id;

	/**
	 * 临时站点名称
	 */
	private String weBSiteName;

	/**
	 * 临时站点ID
	 */
	private Long webSiteID;

	/**
	 * 采集点名称
	 */
	private String wgnName;

	/**
	 * 唯一标识
	 */
	private String wgnUniqueId;

	/**
	 * 入口地址
	 */
	private String wgnEntryUrl;

	/**
	 * 允许链接格式
	 */
	private String wgnAllowRule;

	/**
	 * 禁止链接格式
	 */
	private String wgnDenyRule;

	/**
	 * 分页匹配规则
	 */
	private String wgnNextPage;

	/**
	 * 采集域
	 */
	private String wgnAllowDomain;

	/**
	 * 采集权重
	 */
	private Byte wgnWeight;

	/**
	 * 所属类别
	 */
	private Byte wgnType;

	/**
	 * 是否有效
	 */
	private Byte wgnSpiderEnable;

	/**
	 * 采集延迟时间
	 */
	private Byte wgnDelay;

	/**
	 * 是否开启COOKIES
	 */
	private Byte wgnCookiesEnable;

	/**
	 * 处理链接
	 */
	private String wgnProcessLinks;

	/**
	 * 代理列表
	 */
	private String wgnProxies;

	/**
	 * 采集字段配置
	 */
	private ArrayList<Object> wgnConfigItem;

	/**
	 * 是否登录
	 */
	private Byte wgnLoginEnable;

	/**
	 * 登录入口
	 */
	private String wgnLoginEntry;

	/**
	 * 登录账号
	 */
	private String wgnLoginAccount;

	/**
	 * 登录密码
	 */
	private String wgnLoginPassword;

	/**
	 * 采集深度
	 */
	private Byte wgnDepthLimit;

	/**
	 * 采集频率
	 */
	private Integer wgnTimeInterval;

	/**
	 * 采集更新时间
	 */
	private Integer wgnTimeRefresh;

	/**
	 * 用户代理
	 */
	private String wgnUserAgents;

	/**
	 * 是否繁体采集点
	 */
	private Byte wgnTraditional;

	/**
	 * 是否过滤掉网页
	 */
	private Byte wgnIsClean;

	/**
	 * 蜘蛛任务ID
	 */
	private String wgnJobId;

	/**
	 * 更新时间
	 */
	private Date wgnUpdateTime;

	/**
	 * 创建时间
	 */
	private Date wgnCreateTime;

	/**
	 * 是否删除
	 */
	private Byte wgnDelete;

	public Long getId() {
		return this.id;
	}

	public Long getWebSiteID() {
		return this.webSiteID;
	}

	public String getWeBSiteName() {
		return this.weBSiteName;
	}

	public String getWgnAllowDomain() {
		return this.wgnAllowDomain;
	}

	public String getWgnAllowRule() {
		return this.wgnAllowRule;
	}

	public ArrayList<Object> getWgnConfigItem() {
		return this.wgnConfigItem;
	}

	public Byte getWgnCookiesEnable() {
		return this.wgnCookiesEnable;
	}

	public Date getWgnCreateTime() {
		return this.wgnCreateTime;
	}

	public Byte getWgnDelay() {
		return this.wgnDelay;
	}

	public Byte getWgnDelete() {
		return this.wgnDelete;
	}

	public String getWgnDenyRule() {
		return this.wgnDenyRule;
	}

	public Byte getWgnDepthLimit() {
		return this.wgnDepthLimit;
	}

	public String getWgnEntryUrl() {
		return this.wgnEntryUrl;
	}

	public Byte getWgnIsClean() {
		return this.wgnIsClean;
	}

	public String getWgnJobId() {
		return this.wgnJobId;
	}

	public String getWgnLoginAccount() {
		return this.wgnLoginAccount;
	}

	public Byte getWgnLoginEnable() {
		return this.wgnLoginEnable;
	}

	public String getWgnLoginEntry() {
		return this.wgnLoginEntry;
	}

	public String getWgnLoginPassword() {
		return this.wgnLoginPassword;
	}

	public String getWgnName() {
		return this.wgnName;
	}

	public String getWgnNextPage() {
		return this.wgnNextPage;
	}

	public String getWgnProcessLinks() {
		return this.wgnProcessLinks;
	}

	public String getWgnProxies() {
		return this.wgnProxies;
	}

	public Byte getWgnSpiderEnable() {
		return this.wgnSpiderEnable;
	}

	public Integer getWgnTimeInterval() {
		return this.wgnTimeInterval;
	}

	public Integer getWgnTimeRefresh() {
		return this.wgnTimeRefresh;
	}

	public Byte getWgnTraditional() {
		return this.wgnTraditional;
	}

	public Byte getWgnType() {
		return this.wgnType;
	}

	public String getWgnUniqueId() {
		return this.wgnUniqueId;
	}

	public Date getWgnUpdateTime() {
		return this.wgnUpdateTime;
	}

	public String getWgnUserAgents() {
		return this.wgnUserAgents;
	}

	public Byte getWgnWeight() {
		return this.wgnWeight;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setWebSiteID(Long webSiteID) {
		this.webSiteID = webSiteID;
	}

	public void setWeBSiteName(String weBSiteName) {
		this.weBSiteName = weBSiteName;
	}

	public void setWgnAllowDomain(String wgnAllowDomain) {
		this.wgnAllowDomain = wgnAllowDomain;
	}

	public void setWgnAllowRule(String wgnAllowRule) {
		this.wgnAllowRule = wgnAllowRule;
	}

	public void setWgnConfigItem(ArrayList<Object> wgnConfigItem) {
		this.wgnConfigItem = wgnConfigItem;
	}

	public void setWgnCookiesEnable(Byte wgnCookiesEnable) {
		this.wgnCookiesEnable = wgnCookiesEnable;
	}

	public void setWgnCreateTime(Date wgnCreateTime) {
		this.wgnCreateTime = wgnCreateTime;
	}

	public void setWgnDelay(Byte wgnDelay) {
		this.wgnDelay = wgnDelay;
	}

	public void setWgnDelete(Byte wgnDelete) {
		this.wgnDelete = wgnDelete;
	}

	public void setWgnDenyRule(String wgnDenyRule) {
		this.wgnDenyRule = wgnDenyRule;
	}

	public void setWgnDepthLimit(Byte wgnDepthLimit) {
		this.wgnDepthLimit = wgnDepthLimit;
	}

	public void setWgnEntryUrl(String wgnEntryUrl) {
		this.wgnEntryUrl = wgnEntryUrl;
	}

	public void setWgnIsClean(Byte wgnIsClean) {
		this.wgnIsClean = wgnIsClean;
	}

	public void setWgnJobId(String wgnJobId) {
		this.wgnJobId = wgnJobId;
	}

	public void setWgnLoginAccount(String wgnLoginAccount) {
		this.wgnLoginAccount = wgnLoginAccount;
	}

	public void setWgnLoginEnable(Byte wgnLoginEnable) {
		this.wgnLoginEnable = wgnLoginEnable;
	}

	public void setWgnLoginEntry(String wgnLoginEntry) {
		this.wgnLoginEntry = wgnLoginEntry;
	}

	public void setWgnLoginPassword(String wgnLoginPassword) {
		this.wgnLoginPassword = wgnLoginPassword;
	}

	public void setWgnName(String wgnName) {
		this.wgnName = wgnName;
	}

	public void setWgnNextPage(String wgnNextPage) {
		this.wgnNextPage = wgnNextPage;
	}

	public void setWgnProcessLinks(String wgnProcessLinks) {
		this.wgnProcessLinks = wgnProcessLinks;
	}

	public void setWgnProxies(String wgnProxies) {
		this.wgnProxies = wgnProxies;
	}

	public void setWgnSpiderEnable(Byte wgnSpiderEnable) {
		this.wgnSpiderEnable = wgnSpiderEnable;
	}

	public void setWgnTimeInterval(Integer wgnTimeInterval) {
		this.wgnTimeInterval = wgnTimeInterval;
	}

	public void setWgnTimeRefresh(Integer wgnTimeRefresh) {
		this.wgnTimeRefresh = wgnTimeRefresh;
	}

	public void setWgnTraditional(Byte wgnTraditional) {
		this.wgnTraditional = wgnTraditional;
	}

	public void setWgnType(Byte wgnType) {
		this.wgnType = wgnType;
	}

	public void setWgnUniqueId(String wgnUniqueId) {
		this.wgnUniqueId = wgnUniqueId;
	}

	public void setWgnUpdateTime(Date wgnUpdateTime) {
		this.wgnUpdateTime = wgnUpdateTime;
	}

	public void setWgnUserAgents(String wgnUserAgents) {
		this.wgnUserAgents = wgnUserAgents;
	}

	public void setWgnWeight(Byte wgnWeight) {
		this.wgnWeight = wgnWeight;
	}

}
