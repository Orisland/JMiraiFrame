package org.orisland.bean.sreachImg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @Author: zhaolong
 * @Time: 00:37
 * @Date: 2021年10月01日 00:37
 **/
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class data {
    ArrayNode ext_urls;
    String title;
    String pixiv_id;
    String member_name;
    String member_id;
    String created_at;
    String tweet_id;
    String twitter_user_id;
    String twitter_user_handle;
    String material;
    String characters;
    String source;
    String author_name;
    String author_url;

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public ArrayNode getExt_urls() {
        return ext_urls;
    }

    public void setExt_urls(ArrayNode ext_urls) {
        this.ext_urls = ext_urls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPixiv_id() {
        return pixiv_id;
    }

    public void setPixiv_id(String pixiv_id) {
        this.pixiv_id = pixiv_id;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTweet_id() {
        return tweet_id;
    }

    public void setTweet_id(String tweet_id) {
        this.tweet_id = tweet_id;
    }

    public String getTwitter_user_id() {
        return twitter_user_id;
    }

    public void setTwitter_user_id(String twitter_user_id) {
        this.twitter_user_id = twitter_user_id;
    }

    public String getTwitter_user_handle() {
        return twitter_user_handle;
    }

    public void setTwitter_user_handle(String twitter_user_handle) {
        this.twitter_user_handle = twitter_user_handle;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "data{" +
                "ext_urls=" + ext_urls +
                ", title='" + title + '\'' +
                ", pixiv_id='" + pixiv_id + '\'' +
                ", member_name='" + member_name + '\'' +
                ", member_id='" + member_id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", tweet_id='" + tweet_id + '\'' +
                ", twitter_user_id='" + twitter_user_id + '\'' +
                ", twitter_user_handle='" + twitter_user_handle + '\'' +
                ", material='" + material + '\'' +
                ", characters='" + characters + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
