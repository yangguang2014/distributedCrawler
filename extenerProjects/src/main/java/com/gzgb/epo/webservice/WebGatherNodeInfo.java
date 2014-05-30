package com.gzgb.epo.webservice;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 采集点bean
 */
@XmlRootElement(namespace = "http://guang.crawler.controller.webservice/")
public class WebGatherNodeInfo implements Serializable {

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
	 * 以换行符分割的URL种子列表
	 */
	private String wgnEntryUrl;

	/**
	 * 允许的站点URL的正则表达式列表，以换行符分割
	 */
	private String wgnAllowRule;

	/**
	 * 禁止的站点URL的正则表达式列表，以换行符分割
	 */
	private String wgnDenyRule;

	/**
	 * 采集权重
	 */
	private Byte wgnWeight;

	/**
	 * 所属类别
	 */
	private Byte wgnType;

	/**
	 * 采集延迟时间
	 */
	private Byte wgnDelay;

	/**
	 * 是否开启COOKIES
	 */
	private Byte wgnCookiesEnable;

	/**
	 * 代理列表
	 */
	private String wgnProxies;

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

	public String getWgnAllowRule() {
		return this.wgnAllowRule;
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

	public String getWgnProxies() {
		return this.wgnProxies;
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

	public void setWgnAllowRule(String wgnAllowRule) {
		this.wgnAllowRule = wgnAllowRule;
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

	public void setWgnProxies(String wgnProxies) {
		this.wgnProxies = wgnProxies;
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
