/**
  * Copyright 2021 json.cn 
  */
package org.orisland.bean.pmodel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Auto-generated: 2021-10-01 14:0:3
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tags {

    private String authorId;
    private boolean isLocked;
    private List<Tags> tags;
    private boolean writable;
    public void setAuthorId(String authorId) {
         this.authorId = authorId;
     }
     public String getAuthorId() {
         return authorId;
     }

    public void setIsLocked(boolean isLocked) {
         this.isLocked = isLocked;
     }
     public boolean getIsLocked() {
         return isLocked;
     }

    public void setTags(List<Tags> tags) {
         this.tags = tags;
     }
     public List<Tags> getTags() {
         return tags;
     }

    public void setWritable(boolean writable) {
         this.writable = writable;
     }
     public boolean getWritable() {
         return writable;
     }

}