package com.zafnks.run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.zafnks.server.ChapterService;
import com.zafnks.server.LogServer;
import com.zafnks.utils.PathContants;

public class CatchJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        List<ChapterService> servers = readXML();
        if (null != servers) {
            for (ChapterService server : servers) {
                LogServer.getInstance().startCount("更新" + server.getWebName());
                server.catching();
                LogServer.getInstance().finishEvent();
            }
        }
    }

    public List<ChapterService> readXML() {
        List<ChapterService> list = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(PathContants.getBinPath() + "server.xml");
            NodeList serviceList = document.getElementsByTagName("service");
            list = new ArrayList<ChapterService>(serviceList.getLength());
            // 遍历每一个service节点
            for (int i = 0; i < serviceList.getLength(); i++) {
                Node node = serviceList.item(i);
                String cls = node.getAttributes().getNamedItem("class").getNodeValue();
                try {
                    list.add((ChapterService) Class.forName(cls).newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
