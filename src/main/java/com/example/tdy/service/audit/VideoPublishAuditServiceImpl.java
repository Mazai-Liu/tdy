package com.example.tdy.service.audit;

import com.example.tdy.entity.Video;
import com.example.tdy.entity.audit.AbstractAudit;
import com.example.tdy.entity.audit.ImageAudit;
import com.example.tdy.entity.audit.TextAudit;
import com.example.tdy.entity.audit.VideoAudit;
import com.example.tdy.entity.task.VideoTask;
import com.example.tdy.enums.ContentType;
import com.example.tdy.mapper.VideoMapper;
import com.example.tdy.service.strategy.feed.FeedStrategy;
import com.example.tdy.utils.RedisUtil;
import com.example.tdy.utils.ThreadPoolUtil;
import com.qiniu.common.QiniuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */

@Service
public class VideoPublishAuditServiceImpl implements AuditService<VideoTask, VideoTask>, InitializingBean{
    public static Logger logger = LoggerFactory.getLogger(VideoPublishAuditServiceImpl.class);

    @Autowired
    protected ThreadPoolUtil threadPoolUtil;

    private AbstractAudit audit;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
   // @Lazy 会有问题
    private VideoAudit videoAudit;

    @Autowired
   // @Lazy
    private TextAudit textAudit;

    @Autowired
   // @Lazy
    private ImageAudit imageAudit;

    @Autowired
    private FeedStrategy feedStrategy;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public VideoTask audit(VideoTask videoTask) {

        threadPoolUtil.submit(() -> {
            Video video = videoTask.getVideo();

            // 判断是否需要审核，这里大部分情况下要审核
            boolean needAudit = false;
            if(videoTask.getIsAdd() && videoTask.getOldState() == videoTask.getNewState()) {
                needAudit = true;
            }else if (!videoTask.getIsAdd() && videoTask.getOldState() != videoTask.getNewState()){
                // 修改的情况下新老状态不一致,说明需要更新
                if (!videoTask.getNewState()){
                    needAudit = true;
                }
            }

            logger.info("需要审核：" + needAudit);
            if(needAudit) {
                try {
                    audit.auditProcess(video);

                    if(video.getAuditStatus() == 1) {
                        onVideoAuditPass(video);
                    }
                } catch (QiniuException e) {
                    throw new RuntimeException(e);
                }
            }

            videoMapper.update(video);
        });

        return null;
    }

    public void onVideoAuditPass(Video video) {
        // 加入用户发件箱、系统视频库
        redisUtil.addOutbox(video.getUserId(), video);
        redisUtil.addSystemStock(video);

        feedStrategy.onVideoPublish(video);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AbstractAudit.Builder builder = new AbstractAudit.Builder();
        audit = builder.add(imageAudit.setSelfBusinessName(ContentType.COVER)).
                add(textAudit.setSelfBusinessName(ContentType.Title)).
                add(videoAudit.setSelfBusinessName(ContentType.Video)).
                build();
    }
}
