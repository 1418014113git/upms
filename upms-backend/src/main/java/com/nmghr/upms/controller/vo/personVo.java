package com.nmghr.upms.controller.vo;

import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class personVo {

	@ExcelCell(defaultValue = "用户权限", index = 0)
	private String role;

	@ExcelCell(defaultValue = "单位名称", index = 1)
	private String departCode;

	@ExcelCell(defaultValue = "姓名", index = 2)
	private String name;

	@ExcelCell(defaultValue = "警号", index = 3)
	private String userName;

	@ExcelCell(defaultValue = "身份证号", index = 4)
	private String idNumber;

	@ExcelCell(defaultValue = "联系电话", index = 5)
	private String phone;

	@ExcelCell(defaultValue = "人员类别", index = 6)
	private String userSort;//

	@ExcelCell(defaultValue = "性别", index = 7)
	private String sex;//

	@ExcelCell(defaultValue = "职级", index = 8)
	private String workGrade;//

	@ExcelCell(defaultValue = "职务", index = 9)
	private String workDuty;//

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDepartCode() {
		return departCode;
	}

	public void setDepartCode(String departCode) {
		this.departCode = departCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserSort() {
		return userSort;
	}

	public void setUserSort(String userSort) {
		this.userSort = userSort;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getWorkGrade() {
		return workGrade;
	}

	public void setWorkGrade(String workGrade) {
		this.workGrade = workGrade;
	}

	public String getWorkDuty() {
		return workDuty;
	}

	public void setWorkDuty(String workDuty) {
		this.workDuty = workDuty;
	}

}
