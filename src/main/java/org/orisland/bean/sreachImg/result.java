package org.orisland.bean.sreachImg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @Author: zhaolong
 * @Time: 00:40
 * @Date: 2021年10月01日 00:40
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class result {
    data data;
    header header;

    public org.orisland.bean.sreachImg.data getData() {
        return data;
    }

    public void setData(org.orisland.bean.sreachImg.data data) {
        this.data = data;
    }

    public org.orisland.bean.sreachImg.header getHeader() {
        return header;
    }

    public void setHeader(org.orisland.bean.sreachImg.header header) {
        this.header = header;
    }


}
