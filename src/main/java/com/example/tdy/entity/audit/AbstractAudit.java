package com.example.tdy.entity.audit;


import com.example.tdy.entity.Video;
import com.example.tdy.enums.AuditStatus;
import com.example.tdy.enums.ContentType;
import com.example.tdy.service.FileService;
import com.example.tdy.service.audit.VideoPublishAuditServiceImpl;
import com.example.tdy.utils.QiniuUtil;
import com.qiniu.common.QiniuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */

@Component
public abstract class AbstractAudit{
    public static Logger logger = LoggerFactory.getLogger(AbstractAudit.class);

    protected AbstractAudit next;

    protected ContentType businessName;


    @Autowired
    public QiniuUtil qiniuUtil;

    @Autowired
    public FileService fileService;

    public AbstractAudit(){}


    public AbstractAudit setSelfBusinessName(ContentType businessName) {
        this.businessName = businessName;
        return this;
    }

    public void auditProcess(Video video) throws QiniuException {

        if(doAudit(video).getStatus() == AuditStatus.FAIL) {
            video.setAuditStatus(AuditStatus.FAIL.getCode());
            video.setAuditMsg(this.businessName.getDesc() + "审核不通过");

            logger.info(this.businessName.getDesc() + "审核不通过");
            return;
        }
        logger.info(this.getClass() + "审核器通过业务：" + this.businessName.getDesc());

        if(next != null) {
            next.auditProcess(video);
        } else {
            video.setAuditStatus(AuditStatus.PASS.getCode());
            video.setAuditMsg("审核通过");
        }
    }

    public abstract AuditResult doAudit(Video video) throws QiniuException;

    public static class Builder {
        public AbstractAudit head;
        public AbstractAudit tail;

        public AbstractAudit build() {
            return this.head;
        }

        public Builder add(AbstractAudit audit) {
            if(this.head == null) {
                this.head = this.tail = audit;
            } else {
                this.tail.next = audit;
                this.tail = audit;
            }

            return this;
        }
    }
}
