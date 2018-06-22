package com.vvechirko.toys.linkprewiev;

import android.support.annotation.Nullable;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

public class LinkGenerator {

    private TextCrawler textCrawler;

    public LinkGenerator() {
        textCrawler = new TextCrawler();
    }

    public void generatePreview(String link, PreviewGeneratedListener listener) {
//        Realm.getDefaultInstance().where(LinkPreviewModel.class)
//                .equalTo(LinkPreviewModel.ID, String.valueOf(link.hashCode()))
//                .findAll().asObservable().take(1)
//                .subscribe(list -> {
//                    if (list.isEmpty()) {
//                        generateNew(link, listener);
//                    } else {
//                        listener.onPreviewCached(list.get(0));
//                    }
//                }, t -> listener.onPreviewGenerateError(t));
        generateNew(link, listener);
    }

    private void generateNew(String link, PreviewGeneratedListener listener) {
        textCrawler.makePreview(new LinkPreviewCallback() {
            @Override
            public void onPre() {
                listener.onPreviewStartGenerate();
            }

            @Override
            public void onPos(SourceContent sourceContent, boolean b) {
                if (sourceContent != null) {
                    LinkPreviewModel lp = LinkPreviewModel.from(String.valueOf(link.hashCode()), sourceContent);
                    listener.onPreviewGenerated(lp);
//                    Realm.getDefaultInstance().executeTransaction(realm -> realm.copyToRealmOrUpdate(lp));
                } else {
                    listener.onPreviewGenerated(null);
                }
            }
        }, link);
    }

    public interface PreviewGeneratedListener {

        void onPreviewCached(LinkPreviewModel preview);

        void onPreviewStartGenerate();

        void onPreviewGenerated(@Nullable LinkPreviewModel preview);

        void onPreviewGenerateError(Throwable throwable);
    }
}
