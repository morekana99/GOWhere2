package com.example.ryan.entity;

import com.stx.xhb.xbanner.entity.BaseBannerInfo;

/**
 * @author devonwong
 */
public class LandmarkViewInfo implements BaseBannerInfo {

    private String imageId;

    public LandmarkViewInfo(String imageId){
        this.imageId = imageId;
    }
    @Override
    public Object getXBannerUrl() {
        return "http://106.12.199.128/images/"+imageId+".jpeg";
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
}
