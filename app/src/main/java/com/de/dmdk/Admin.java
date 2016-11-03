package com.de.dmdk;

import java.util.HashMap;
import java.util.Map;

public class Admin {

private String sno;
private String name;
private String membershipId;
private String phoneNum;
private String password;
private String email;
private String isSuperAdmin;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The sno
*/
public String getSno() {
return sno;
}

/**
* 
* @param sno
* The sno
*/
public void setSno(String sno) {
this.sno = sno;
}

/**
* 
* @return
* The name
*/
public String getName() {
return name;
}

/**
* 
* @param name
* The name
*/
public void setName(String name) {
this.name = name;
}

/**
* 
* @return
* The membershipId
*/
public String getMembershipId() {
return membershipId;
}

/**
* 
* @param membershipId
* The membership_id
*/
public void setMembershipId(String membershipId) {
this.membershipId = membershipId;
}

/**
* 
* @return
* The phoneNum
*/
public String getPhoneNum() {
return phoneNum;
}

/**
* 
* @param phoneNum
* The phone_num
*/
public void setPhoneNum(String phoneNum) {
this.phoneNum = phoneNum;
}

/**
* 
* @return
* The password
*/
public String getPassword() {
return password;
}

/**
* 
* @param password
* The password
*/
public void setPassword(String password) {
this.password = password;
}

/**
* 
* @return
* The email
*/
public String getEmail() {
return email;
}

/**
* 
* @param email
* The email
*/
public void setEmail(String email) {
this.email = email;
}

/**
* 
* @return
* The isSuperAdmin
*/
public String getIsSuperAdmin() {
return isSuperAdmin;
}

/**
* 
* @param isSuperAdmin
* The is_super_admin
*/
public void setIsSuperAdmin(String isSuperAdmin) {
this.isSuperAdmin = isSuperAdmin;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}