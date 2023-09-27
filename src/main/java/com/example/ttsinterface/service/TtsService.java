package com.example.ttsinterface.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.example.ttsinterface.entity.CallClassTTSParms;
import com.example.ttsinterface.entity.CallClassTTSResponse;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author sodream
 * @date 2022/6/2 10:11
 * @content
 */
@Service
public class TtsService {
    /**
     * 语音合成
     *
     * @return
     */
    public String audioSynthesis(String id, String content) {
        try {
            String phyPath = "E://ttsaudio";
            File file = new File(phyPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            CallClassTTSParms parms = new CallClassTTSParms();
            parms.text = content;
            parms.path = phyPath;
            parms.fileName = id + ".wav";
            Gson gson = new Gson();
            CallClassTTSResponse response = gson.fromJson(HttpUtil.post("http://localhost:8085/api/TTS", gson.toJson(parms)), CallClassTTSResponse.class);
            if (response.success && StrUtil.isNotBlank(response.path)) {
                Thread.sleep(1000);
                return response.path;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }


}
