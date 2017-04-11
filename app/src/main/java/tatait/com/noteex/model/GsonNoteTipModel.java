package tatait.com.noteex.model;

import java.io.Serializable;

/**
 * 创建时间:15/6/21 下午11:16
 * 描述:
 */
public class GsonNoteTipModel implements Serializable {
    public String code;
    public String info;

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
}