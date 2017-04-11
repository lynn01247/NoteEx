package tatait.com.noteex.model;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间:15/6/21 下午11:16
 * 描述:
 */
public class GsonNoteModel implements Serializable {
    public String code;
    public String info;
    public List<NoteModel> data;
    public int page;
    public int pageSize;
    public int total;

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

    public List<NoteModel> getData() {
        return data;
    }

    public void setData(List<NoteModel> data) {
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

    public class NoteModel implements Serializable {
        public int nid;
        public String title;
        public String content;
        public String category;
        public String tag;
        public String updatedTime;
        public UpdatedAt updatedAt;
        public String collectNum;
        public String collected;
        public String praisedNum;
        public String praised;
        public String viewsNum;
        public String type;
        public String name;
        public String imgurl;
        public String from;
        public String jj_userName;
        public String jj_user_img;
        public String jj_category;
        public String jj_originalUrl;
        public String uid;
        public String treeid;
        public String tagid;
        public String emid;

        public int getNid() {
            return nid;
        }

        public void setNid(int nid) {
            this.nid = nid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getUpdatedTime() {
            return updatedTime;
        }

        public void setUpdatedTime(String updatedTime) {
            this.updatedTime = updatedTime;
        }

        public UpdatedAt getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(UpdatedAt updatedAt) {
            this.updatedAt = updatedAt;
        }

        public class UpdatedAt {
            public String date;
            public String timezone_type;
            public String timezone;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTimezone_type() {
                return timezone_type;
            }

            public void setTimezone_type(String timezone_type) {
                this.timezone_type = timezone_type;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }
        }

        public String getCollected() {
            return collected;
        }

        public void setCollected(String collected) {
            this.collected = collected;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPraisedNum() {
            return praisedNum;
        }

        public void setPraisedNum(String praisedNum) {
            this.praisedNum = praisedNum;
        }

        public String getPraised() {
            return praised;
        }

        public void setPraised(String praised) {
            this.praised = praised;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getJj_userName() {
            return jj_userName;
        }

        public void setJj_userName(String jj_userName) {
            this.jj_userName = jj_userName;
        }

        public String getJj_user_img() {
            return jj_user_img;
        }

        public void setJj_user_img(String jj_user_img) {
            this.jj_user_img = jj_user_img;
        }

        public String getJj_category() {
            return jj_category;
        }

        public void setJj_category(String jj_category) {
            this.jj_category = jj_category;
        }

        public String getJj_originalUrl() {
            return jj_originalUrl;
        }

        public void setJj_originalUrl(String jj_originalUrl) {
            this.jj_originalUrl = jj_originalUrl;
        }

        public String getTreeid() {
            return treeid;
        }

        public void setTreeid(String treeid) {
            this.treeid = treeid;
        }

        public String getTagid() {
            return tagid;
        }

        public void setTagid(String tagid) {
            this.tagid = tagid;
        }

        public String getEmid() {
            return emid;
        }

        public void setEmid(String emid) {
            this.emid = emid;
        }

        public String getCollectNum() {
            return collectNum;
        }

        public void setCollectNum(String collectNum) {
            this.collectNum = collectNum;
        }

        public String getViewsNum() {
            return viewsNum;
        }

        public void setViewsNum(String viewsNum) {
            this.viewsNum = viewsNum;
        }
    }
}