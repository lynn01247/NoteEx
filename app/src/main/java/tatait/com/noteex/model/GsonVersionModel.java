package tatait.com.noteex.model;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间:15/6/21 下午11:16
 * 描述:
 */
public class GsonVersionModel implements Serializable {
    public String code;
    public String info;
    public List<VersionModel> data;
    public int page;
    public int pageSize;
    public int total;

    public class VersionModel implements Serializable {
        public String android_version;
        public String android_link;
        public String ios_version;
        public String ios_link;
        public String mainImg;
        public String mustUpdate;
        public String adLink;
        // 跟图片混合使用
        public String title;
        public String path;
        public String id;

        public String getAndroid_version() {
            return android_version;
        }

        public void setAndroid_version(String android_version) {
            this.android_version = android_version;
        }

        public String getAndroid_link() {
            return android_link;
        }

        public void setAndroid_link(String android_link) {
            this.android_link = android_link;
        }

        public String getIos_version() {
            return ios_version;
        }

        public void setIos_version(String ios_version) {
            this.ios_version = ios_version;
        }

        public String getIos_link() {
            return ios_link;
        }

        public void setIos_link(String ios_link) {
            this.ios_link = ios_link;
        }

        public String getMainImg() {
            return mainImg;
        }

        public void setMainImg(String mainImg) {
            this.mainImg = mainImg;
        }

        public String getMustUpdate() {
            return mustUpdate;
        }

        public void setMustUpdate(String mustUpdate) {
            this.mustUpdate = mustUpdate;
        }

        public String getAdLink() {
            return adLink;
        }

        public void setAdLink(String adLink) {
            this.adLink = adLink;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

    public List<VersionModel> getData() {
        return data;
    }

    public void setData(List<VersionModel> data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}