package tatait.com.noteex.model;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间:15/6/21 下午11:16
 * 描述:
 */
public class GsonUserModel implements Serializable {
    public String code;
    public String info;
    public List<UserModel> data;

    public class UserModel implements Serializable {
        public String uid;
        public String name;
        public String token;
        public String mobile;
        public String email;
        public String address;
        public String remark;
        public String imgurl;
        public String emid;
        public String defaultid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getEmid() {
            return emid;
        }

        public void setEmid(String emid) {
            this.emid = emid;
        }

        public String getDefaultid() {
            return defaultid;
        }

        public void setDefaultid(String defaultid) {
            this.defaultid = defaultid;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<UserModel> getData() {
        return data;
    }

    public void setData(List<UserModel> data) {
        this.data = data;
    }
}