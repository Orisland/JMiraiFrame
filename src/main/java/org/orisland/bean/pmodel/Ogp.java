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
public class Ogp {

    private String description;
    private String image;
    private String title;
    private String type;
    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setImage(String image) {
         this.image = image;
     }
     public String getImage() {
         return image;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

}