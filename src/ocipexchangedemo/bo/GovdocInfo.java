package ocipexchangedemo.bo;

import java.util.List;

public class GovdocInfo {
	
	/**
	 * 公文ID
	 */
	private String groupId;

	private String unitName;
	
	private String detailId;
	
	private String exchNo;

	/**
	 * 公文标题
	 */
	private String title;

	private String createTime;

	private String docMark;
	
	private String urgentLevel;
	
	private String secretLevel;
	
	private String copies;
	
	private String phone;

	private List<String> attrList;

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDocMark() {
		return docMark;
	}

	public void setDocMark(String docMark) {
		this.docMark = docMark;
	}

	public List<String> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<String> attrList) {
		this.attrList = attrList;
	}

	public String getUrgentLevel() {
		return urgentLevel;
	}

	public void setUrgentLevel(String urgentLevel) {
		this.urgentLevel = urgentLevel;
	}

	public String getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(String secretLevel) {
		this.secretLevel = secretLevel;
	}

	public String getCopies() {
		return copies;
	}

	public void setCopies(String copies) {
		this.copies = copies;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getExchNo() {
		return exchNo;
	}

	public void setExchNo(String exchNo) {
		this.exchNo = exchNo;
	}
	
	

}
