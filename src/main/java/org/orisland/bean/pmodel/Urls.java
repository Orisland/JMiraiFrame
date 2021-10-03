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
public class Urls {

    private String mini;
    private String thumb;
    private String small;
    private String regular;
    private String original;
    public void setMini(String mini) {
         this.mini = mini;
     }
     public String getMini() {
         return mini;
     }

    public void setThumb(String thumb) {
         this.thumb = thumb;
     }
     public String getThumb() {
         return thumb;
     }

    public void setSmall(String small) {
         this.small = small;
     }
     public String getSmall() {
         return small;
     }

    public void setRegular(String regular) {
         this.regular = regular;
     }
     public String getRegular() {
         return regular;
     }

    public void setOriginal(String original) {
         this.original = original;
     }
     public String getOriginal() {
         return original;
     }

}