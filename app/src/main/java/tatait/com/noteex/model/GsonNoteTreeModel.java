package tatait.com.noteex.model;

import java.io.Serializable;
import java.util.List;

/**
 * 创建时间:15/6/21 下午11:16
 * 描述:
 */
public class GsonNoteTreeModel implements Serializable {
    public String code;
    public String info;
    public List<Tree> data;

    public class Tree implements Serializable {
        public int tid;
        public int uid;
        public int pid;
        public String name;
        public int count;
        public List<TreeSub> subs;

        public class TreeSub implements Serializable {
            public int tid;
            public int uid;
            public int pid;
            public String name;
            public int count;

            public int getTid() {
                return tid;
            }

            public void setTid(int tid) {
                this.tid = tid;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TreeSub> getSubs() {
            return subs;
        }

        public void setSubs(List<TreeSub> subs) {
            this.subs = subs;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
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

    public List<Tree> getData() {
        return data;
    }

    public void setData(List<Tree> data) {
        this.data = data;
    }
}