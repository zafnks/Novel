package com.zafnks.web.bbdushu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zafnks.entity.Chapter;
import com.zafnks.web.AbstractChapterService;

public class BbDuShuChapterService extends AbstractChapterService {

    private final static String TABLENAME = "novel_bbdushu";

    private final static String CHAPTER_DB = "bbdushuDB";

    private final static String ECODING = "GBK";
    
    private final static String WEB = "bbdushu";

    @Override
    protected String getNovelListTable() {
        return TABLENAME;
    }

    @Override
    protected String getChapterDB() {
        return CHAPTER_DB;
    }

    @Override
    protected String getEncoding() {
        return ECODING;
    }

    @Override
    protected List<Chapter> getAllChapter(String rootUrl, Document doc) {
        List<Chapter> result = new ArrayList<Chapter>(1200);
        try {
            Element mulu = doc.getElementsByClass("mulu").get(0);
            Elements lis = mulu.getElementsByTag("li");
            for (int i = 0, size = lis.size(); i < size; i++) {
                Element li = lis.get(i);
                String name = li.getElementsByTag("a").html();
                if (StringUtils.isBlank(name)) {
                    continue;
                }
                Chapter chapter = new Chapter();
                chapter.setChapterUrl(rootUrl + li.getElementsByTag("a").attr("href"));
                chapter.setName(name);
                result.add(chapter);
            }
        } catch (Exception e) {
            log.error(doc.html(), e);
        }

        return result;
    }

    @Override
    protected List<Chapter> getUpdateChapter(String lastChapter, String rootUrl, Document doc) {
        List<Chapter> result = new ArrayList<Chapter>(20);
        try {
            Element mulu = doc.getElementsByClass("mulu").get(0);
            Elements lis = mulu.getElementsByTag("li");
            boolean isUpdate = false;
            for (int i = 0, size = lis.size(); i < size; i++) {
                Element li = lis.get(i);
                String name = li.getElementsByTag("a").html();
                if (StringUtils.isBlank(name)) {
                    continue;
                }
                if (isUpdate) {
                    Chapter chapter = new Chapter();
                    chapter.setChapterUrl(rootUrl + li.getElementsByTag("a").attr("href"));
                    chapter.setName(name);
                    result.add(chapter);
                }
                if (!isUpdate && null != lastChapter && lastChapter.equals(name)) {
                    isUpdate = true;
                }
            }
        } catch (Exception e) {
            log.error(doc.html(), e);
        }
        return result;
    }

    @Override
    protected String getNextURL(Document doc) {
        return null;
    }

    @Override
    public String getWebName() {
        return WEB;
    }

}
