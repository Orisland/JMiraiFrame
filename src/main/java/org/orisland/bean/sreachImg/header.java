package org.orisland.bean.sreachImg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @Author: zhaolong
 * @Time: 00:43
 * @Date: 2021年10月01日 00:43
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class header {
    String similarity;
    String thumbnail;
    long index_id;
    String index_name;
    long dupes;

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getIndex_id() {
        return index_id;
    }

    public void setIndex_id(long index_id) {
        this.index_id = index_id;
    }

    public String getIndex_name() {
        return index_name;
    }

    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }

    public long getDupes() {
        return dupes;
    }

    public void setDupes(long dupes) {
        this.dupes = dupes;
    }

    @Override
    public String toString() {
        return "header{" +
                "similarity='" + similarity + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", index_id=" + index_id +
                ", index_name='" + index_name + '\'' +
                ", dupes=" + dupes +
                '}';
    }
}
