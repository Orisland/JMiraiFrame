package org.orisland.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author: zhaolong
 * @Time: 00:40
 * @Date: 2021年10月01日 00:40
 **/
public class result {
    data data;
    header header;

    public org.orisland.bean.data getData() {
        return data;
    }

    public void setData(org.orisland.bean.data data) {
        this.data = data;
    }

    public org.orisland.bean.header getHeader() {
        return header;
    }

    public void setHeader(org.orisland.bean.header header) {
        this.header = header;
    }


}
