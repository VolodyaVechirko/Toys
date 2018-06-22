package com.vvechirko.toys.linkprewiev;

import com.leocardz.link.preview.library.SourceContent;

//import io.realm.RealmModel;
//import io.realm.annotations.Ignore;
//import io.realm.annotations.PrimaryKey;
//import io.realm.annotations.RealmClass;

//@RealmClass
//public class LinkPreviewModel implements RealmModel {
public class LinkPreviewModel {

//    @Ignore
    public static final String ID = "id";

//    @PrimaryKey
    private String id;  // link hash value;
    private String url;
    private String title;
    private String description;
    private String previewUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            return getId().equals(((LinkPreviewModel) obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public static LinkPreviewModel from(String id, SourceContent sc) {
        LinkPreviewModel lp = new LinkPreviewModel();
        lp.setId(id);
        lp.setUrl(sc.getUrl());
        lp.setTitle(sc.getTitle());
        lp.setDescription(sc.getDescription());
        if (sc.getImages().size() > 0) {
            lp.setPreviewUrl(sc.getImages().get(0));
        }

        return lp;
    }
}
