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
public class ZoneConfig {

    private Responsive responsive;
    private Rectangle rectangle;
    private Header header;
    private Footer footer;
    private ExpandedFooter expandedFooter;
    private Logo logo;
    private Relatedworks relatedworks;
    public void setResponsive(Responsive responsive) {
         this.responsive = responsive;
     }
     public Responsive getResponsive() {
         return responsive;
     }

    public void setRectangle(Rectangle rectangle) {
         this.rectangle = rectangle;
     }
     public Rectangle getRectangle() {
         return rectangle;
     }


    public void setHeader(Header header) {
         this.header = header;
     }
     public Header getHeader() {
         return header;
     }

    public void setFooter(Footer footer) {
         this.footer = footer;
     }
     public Footer getFooter() {
         return footer;
     }

    public void setExpandedFooter(ExpandedFooter expandedFooter) {
         this.expandedFooter = expandedFooter;
     }
     public ExpandedFooter getExpandedFooter() {
         return expandedFooter;
     }

    public void setLogo(Logo logo) {
         this.logo = logo;
     }
     public Logo getLogo() {
         return logo;
     }

    public void setRelatedworks(Relatedworks relatedworks) {
         this.relatedworks = relatedworks;
     }
     public Relatedworks getRelatedworks() {
         return relatedworks;
     }

}