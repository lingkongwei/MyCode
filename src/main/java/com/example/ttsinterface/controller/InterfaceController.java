package com.example.ttsinterface.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.ttsinterface.entity.InterfaceParm;
import com.example.ttsinterface.entity.InterfaceResponse;
import com.example.ttsinterface.service.FtpUpload;
import com.example.ttsinterface.service.PlayAudioService;
import com.example.ttsinterface.service.TtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author sodream
 * @date 2022/6/2 10:02
 * @content 语音接口
 */
@RestController
@RequestMapping("interface")
public class InterfaceController {

    @Autowired
    TtsService ttsService;


    @PostMapping("/callVoice")
    public InterfaceResponse callVoice(@RequestBody InterfaceParm interfaceParm) {
        String id = IdUtil.simpleUUID();
        try {
            //1、生成语音文件
            String path = ttsService.audioSynthesis(id, interfaceParm.content);
            //2、上传语音文件至FTP服务器
            if (StrUtil.isNotEmpty(path)) {
                //3、调取发送语音接口
                FtpUpload ftpUpload = new FtpUpload();
                ftpUpload.fileUpload(path);
                String audioPath = "/soft/bea/tomcat4.0/webapps/tianr4/upload/mp3/" + id + ".wav";
                //删除队列
                PlayAudioService.deleteQueues("100001");
                //创建队列
                PlayAudioService.createQueues("100001", audioPath);
                //获取电话列表
                String[] mobiles = interfaceParm.mobiles.split(",");
                for (String phone : mobiles) {
                    //发送电话
                    PlayAudioService.putPlayAudio(phone, IdUtil.simpleUUID(), "100001");
                }
                return new InterfaceResponse(true, "呼叫完成");
            } else {
                return new InterfaceResponse(false, "生成音频文件失败");
            }
        } catch (Exception e) {
            return new InterfaceResponse(false, e.getMessage());
        }
    }
}
