package com.kidozh.npuhelper.bbsService;


import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class UploadImageResponseHandler extends ResponseHandler {

    private static Map<String, String> uploadImageErrors = new HashMap<String, String>() {{
        put("-1", "内部服务器错误");
        put("0", "上传成功");
        put("1", "不支持此类扩展名");
        put("2", "服务器限制无法上传那么大的附件");
        put("3", "用户组限制无法上传那么大的附件");
        put("4", "不支持此类扩展名");
        put("5", "文件类型限制无法上传那么大的附件");
        put("6", "今日您已无法上传更多的附件");
        put("7", "请选择图片文件");
        put("8", "附件文件无法保存");
        put("9", "没有合法的文件被上传");
        put("10", "非法操作");
        put("11", "今日您已无法上传那么大的附件");
    }};


    @Override
    public void onSuccess(byte[] response) {
        String res = new String(response);
        String errMsg;
        boolean success = false;
        Log.v("response", res);
        if (TextUtils.isEmpty(res) || !res.contains("|")) {
            errMsg = "上传失败，请稍后再试";
        } else {
            //DISCUZUPLOAD|1|0|939668|1|201801/30/221334oe2emm22geg0vmeq.jpg|1517321613953.jpg|0
            String[] ress = res.split("\\|");
            if ("DISCUZUPLOAD".equals(ress[0]) && "0".equals(ress[2])) {
                success = true;
                errMsg = ress[3];
            } else {
                if (Objects.equals(ress[7], "ban")) {
                    errMsg = "(附件类型被禁止)";
                } else if (Objects.equals(ress[7], "perday")) {
                    errMsg = "(不能超过 " + (Integer.parseInt(ress[8]) / 1024) + " K)";
                } else {
                    errMsg = "(不能超过 " + (Integer.parseInt(ress[7]) / 1024) + " K)";
                }

                if (uploadImageErrors.containsKey(ress[2])) {
                    errMsg = uploadImageErrors.get(ress[2]) + errMsg;
                } else {
                    errMsg = "我也不知道是什么原因上传失败了";
                }
            }
        }

        if (success) {
            onSuccess(errMsg);
        } else {
            onFailure(new Throwable(errMsg));
        }
    }

    // response 为上传完成的aid
    public abstract void onSuccess(String response);
}
