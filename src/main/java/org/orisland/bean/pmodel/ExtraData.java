/**
  * Copyright 2021 json.cn 
  */
package org.orisland.bean.pmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Auto-generated: 2021-10-01 14:0:3
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtraData {

    private Meta meta;
    public void setMeta(Meta meta) {
         this.meta = meta;
     }
     public Meta getMeta() {
         return meta;
     }

}