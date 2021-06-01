package com.example.ryan.entity;

import com.stx.xhb.xbanner.entity.BaseBannerInfo;

/**
 * @author devonwong
 */
public class LandmarkViewInfo implements BaseBannerInfo {

    private String imageId;
    private String url;

    public LandmarkViewInfo(String imageId,boolean flag){
        this.imageId = imageId;
        if(flag){
            this.url = "https://devyn.wang/images/"+imageId+".jpeg";
        }else {
            this.url = "https://devyn.wang/images/p" + imageId + ".jpg";
        }
    }
    @Override
    public Object getXBannerUrl() {
        return this.url;
    }

    @Override
    public String getXBannerTitle() {
        return imageId+"";
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
