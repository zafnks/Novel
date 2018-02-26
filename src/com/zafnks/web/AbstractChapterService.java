package com.zafnks.web;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.zafnks.db.DbPoolConnection;
import com.zafnks.entity.Chapter;
import com.zafnks.entity.ChapterTable;
import com.zafnks.server.ChapterService;
import com.zafnks.server.LogServer;
import com.zafnks.utils.HttpGetUtils;

public abstract class AbstractChapterService implements ChapterService {

    protected final static Logger log = Logger.getLogger(AbstractChapterService.class);

    private final static String QUERY_SQL = "select novel_id, catalog_url, last_chapter from %s limit %s, 1000";

    private final static String UPDATE_SQL = "update %s set last_chapter = ? where novel_id = %s";

    private final static String DROP_SQL = "DROP TABLE IF EXISTS %s.no_%s";

    private final static String CREATE_SQL = "CREATE TABLE %s.no_%s (chapter_id int(9) NOT NULL AUTO_INCREMENT,chapter_url varchar(255) DEFAULT NULL,name varchar(48) DEFAULT NULL, PRIMARY KEY (`chapter_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

    private final static String INSERT_SQL_PREFIX = "INSERT INTO %s.no_%s (chapter_url, name) VALUES ";

    private DbPoolConnection dbPoolConnection = DbPoolConnection.getInstance();

    @SuppressWarnings("unchecked")
    @Override
    public void catching() {
        LogServer logServer = LogServer.getInstance();
        HttpGetUtils httpUtil = new HttpGetUtils();
        String tableName = getNovelListTable();
        String chapterDB = getChapterDB();
        int cursor = 0;
        while (true) {
            List<ChapterTable> chapterList = (List<ChapterTable>) dbPoolConnection
                    .querySQL(String.format(QUERY_SQL, tableName, cursor), this::getChapterList);
            if (chapterList.isEmpty()) {
                break;
            }
            // 1000条一次查询
            for (int i = 0, size = chapterList.size(); i < size; i++) {
                ChapterTable table = chapterList.get(i);
                String lastChapter = table.getLastChapter();
                String url = table.getCatalogUrl();
                String newLastChapter = "";
                int updateNum = 0;
                while (true) {
                    if (null == url) {
                        break;
                    }
                    List<Chapter> list = null;
                    Document doc = null;
                    try {
                        String html = httpUtil.sendGet(url, getEncoding());
                        doc = Jsoup.parse(html);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null == doc) {
                        break;
                    }
                    if (StringUtils.isBlank(lastChapter) || "null".equals(lastChapter.toLowerCase())) {
                        list = getAllChapter(url, doc);
                        dropAndCreateTable(table.getNovelId());
                    } else {
                        list = getUpdateChapter(lastChapter, url, doc);
                    }
                    if (null != list && !list.isEmpty()) {
                        updateNum += list.size();
                        String prefix = String.format(INSERT_SQL_PREFIX, chapterDB, table.getNovelId());
                        StringBuffer sqlBuffer = new StringBuffer(prefix);
                        int bufferSize = 0;
                        Object[] param = new Object[20];
                        for (int j = 0, listSize = list.size(); j < listSize; j++) {
                            Chapter chapter = list.get(j);
                            sqlBuffer.append("(?,?),");
                            param[bufferSize * 2] = chapter.getChapterUrl();
                            param[bufferSize * 2 + 1] = chapter.getName();
                            bufferSize++;
                            // 每十条Insert一次
                            if (bufferSize > 9 || (bufferSize > 0 && j == listSize-1)) {
                                sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
                                dbPoolConnection.updateSQL(sqlBuffer.toString(), param);
                                logServer.addCount(bufferSize + 1);
                                sqlBuffer = new StringBuffer(prefix);
                                bufferSize = 0;
                                param = new Object[20];
                            }
                        }
                        newLastChapter = list.get(list.size() - 1).getName();
                    }

                    url = getNextURL(doc);
                }

                log.info(chapterDB + "：【书籍ID】 " + table.getNovelId() + " 更新了" + updateNum + "章");
                if (!"".equals(newLastChapter)) {
                    dbPoolConnection.updateSQL(String.format(UPDATE_SQL, tableName, table.getNovelId()),
                            new Object[] { newLastChapter });
                }

            }

            cursor += 1000;
        }

    }

    public Object getChapterList(ResultSet rs) {

        List<ChapterTable> chapterList = new ArrayList<ChapterTable>(50000);
        ResultSetMetaData m;
        try {
            m = rs.getMetaData();
            int columns = m.getColumnCount();
            while (rs.next()) {
                ChapterTable cat = new ChapterTable();
                for (int i = 1; i <= columns; i++) {
                    if ("novel_id".equals(m.getColumnName(i))) {
                        cat.setNovelId(rs.getString(i));
                    } else if ("catalog_url".equals(m.getColumnName(i))) {
                        cat.setCatalogUrl(rs.getString(i));
                    } else if ("last_chapter".equals(m.getColumnName(i))) {
                        cat.setLastChapter(rs.getString(i));
                    }
                }
                chapterList.add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chapterList;
    }

    public void dropAndCreateTable(String novelId) {
        String chapterDB = getChapterDB();
        String sql = String.format(DROP_SQL, chapterDB, novelId);
        dbPoolConnection.updateSQL(sql);
        sql = String.format(CREATE_SQL, chapterDB, novelId);
        dbPoolConnection.updateSQL(sql);
    }
    

    protected abstract String getNovelListTable();

    protected abstract String getChapterDB();

    protected abstract String getEncoding();

    protected abstract List<Chapter> getAllChapter(String rootUrl, Document doc);

    protected abstract List<Chapter> getUpdateChapter(String lastChapter, String rootUrl, Document doc);

    protected abstract String getNextURL(Document doc);

}
