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
public class TitleCaptionTranslation {

    private String workTitle;
    private String workCaption;
    public void setWorkTitle(String workTitle) {
         this.workTitle = workTitle;
     }
     public String getWorkTitle() {
         return workTitle;
     }

    public void setWorkCaption(String workCaption) {
         this.workCaption = workCaption;
     }
     public String getWorkCaption() {
         return workCaption;
     }

}