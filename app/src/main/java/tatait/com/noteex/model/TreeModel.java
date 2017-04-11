package tatait.com.noteex.model;

import tatait.com.noteex.tree.TreeNodeId;
import tatait.com.noteex.tree.TreeNodeLabel;
import tatait.com.noteex.tree.TreeNodePid;

public class TreeModel {
    @TreeNodeId
    private int _id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    private int id;
    private int level;
    private int pid;
    private boolean isExpand;
    private long length;
    private String desc;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TreeModel(int _id, int parentId, String name) {
        super();
        this._id = _id;
        this.parentId = parentId;
        this.name = name;
    }

}
