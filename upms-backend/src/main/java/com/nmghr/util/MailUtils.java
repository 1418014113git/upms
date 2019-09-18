package com.nmghr.util;

import com.nmghr.upms.config.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
public class MailUtils {

    @Autowired
    MailProperties mailProperties;

    public void sendEmail(String email, String subject, String content) throws Exception {
        // 创建邮件配置
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", mailProperties.getProtocol()); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", mailProperties.getHost()); // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.port", mailProperties.getPort());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.auth", mailProperties.getSmtpAuth()); // 需要请求认证
        props.setProperty("mail.smtp.ssl.enable", mailProperties.getSslEnable());// 开启ssl
        // 根据邮件配置创建会话，注意session别导错包
        Session session = Session.getDefaultInstance(props);
        // 开启debug模式，可以看到更多详细的输入日志
        session.setDebug(true);
        //创建邮件
        MimeMessage message = createEmail(session, email, subject, content);
        //获取传输通道
        Transport transport = session.getTransport();
        transport.connect(mailProperties.getHost(), mailProperties.getAccount(), mailProperties.getPassword());
        //连接，并发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();

    }


    private MimeMessage createEmail(Session session, String email, String subject, String content) throws Exception {
        // 根据会话创建邮件
        MimeMessage msg = new MimeMessage(session);
        // address邮件地址, personal邮件昵称, charset编码方式
        InternetAddress fromAddress = new InternetAddress(mailProperties.getAccount(), mailProperties.getFrom(), mailProperties.getCharset());
        // 设置发送邮件方
        msg.setFrom(fromAddress);
        InternetAddress receiveAddress = new InternetAddress(email, "test", mailProperties.getCharset());
        // 设置邮件接收方
        msg.setRecipient(Message.RecipientType.TO, receiveAddress);
        // 设置邮件标题
        msg.setSubject(subject, mailProperties.getCharset());
        msg.setText(content);
        // 设置显示的发件时间
        msg.setSentDate(new Date());
        // 保存设置
        msg.saveChanges();
        return msg;
    }

}
