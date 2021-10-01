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
public class AlternateLanguages {

    private String ja;
    private String en;
    public void setJa(String ja) {
         this.ja = ja;
     }
     public String getJa() {
         return ja;
     }

    public void setEn(String en) {
         this.en = en;
     }
     public String getEn() {
         return en;
     }

}