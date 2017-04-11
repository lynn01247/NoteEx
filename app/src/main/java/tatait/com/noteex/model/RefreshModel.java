package tatait.com.noteex.model;

/**
 * 创建时间:15/5/21 14:53
 * 描述:
 */
public class RefreshModel {
    public String title;
    public String detail;
    public String name;

    public RefreshModel() {
    }

    public RefreshModel(String title, String detail) {
        this.title = title;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}