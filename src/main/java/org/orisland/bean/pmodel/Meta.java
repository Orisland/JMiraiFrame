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
public class Meta {

    private String title;
    private String description;
    private String canonical;
    private AlternateLanguages alternateLanguages;
    private String descriptionHeader;
    private Ogp ogp;
    private Twitter twitter;
    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setCanonical(String canonical) {
         this.canonical = canonical;
     }
     public String getCanonical() {
         return canonical;
     }

    public void setAlternateLanguages(AlternateLanguages alternateLanguages) {
         this.alternateLanguages = alternateLanguages;
     }
     public AlternateLanguages getAlternateLanguages() {
         return alternateLanguages;
     }

    public void setDescriptionHeader(String descriptionHeader) {
         this.descriptionHeader = descriptionHeader;
     }
     public String getDescriptionHeader() {
         return descriptionHeader;
     }

    public void setOgp(Ogp ogp) {
         this.ogp = ogp;
     }
     public Ogp getOgp() {
         return ogp;
     }

    public void setTwitter(Twitter twitter) {
         this.twitter = twitter;
     }
     public Twitter getTwitter() {
         return twitter;
     }

}