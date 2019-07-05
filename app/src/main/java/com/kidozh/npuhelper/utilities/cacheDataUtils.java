package com.kidozh.npuhelper.utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class cacheDataUtils {
    private static String TAG = cacheDataUtils.class.getSimpleName();

    private static String readDataByPath(String path){
        try{
            File file = new File(path);
            if(file.exists() && file.isFile()){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(reader);
                String string;
                StringBuffer stringBuffer;
                stringBuffer = new StringBuffer();
                while (null != (string = bufferedReader.readLine())){
                    stringBuffer.append(string);

                }
                return stringBuffer.toString();
            }
            else {
                Log.d(TAG,"File detail "+file.exists()+" "+file.isFile()+" "+file+" "+file.getAbsolutePath()+" "+getCurrentParentFilePath());
                return "";
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    private static String calendarJsonPath = "../CACHE_DATA/calendar.json";

    private static String getCurrentParentFilePath(){
        File directory = new File("");
        return directory.getAbsolutePath();
    }

    public static String getCalendarCacheData(){
        return "{\"result\":\"ok\",\"holiday\":{\"2018\":{\"workday\":{\"national_day\":[\"20180929\",\"20180930\"]},\"festival\":{\"national_day\":[\"20181001\",\"20181002\",\"20181003\",\"20181004\",\"20181005\",\"20181006\",\"20181007\"],\"dragon_boat_festival\":[\"20180618\"],\"mid_fall_festival\":[\"20180924\"]}},\"2019\":{\"workday\":{\"spring_festival\":[\"20190202\",\"20190203\"],\"larbor_day\":[\"20190505\",\"20190428\"],\"national_day\":[\"20190929\",\"20191012\"]},\"festival\":{\"spring_festival\":[\"20190204\",\"20190205\",\"20190206\",\"20190207\",\"20190208\",\"20190209\",\"20190210\"],\"qingming_festival\":[\"20190405\"],\"larbor_day\":[\"20190501\",\"20190502\",\"20190503\"],\"national_day\":[\"20191001\",\"20191002\",\"20191003\",\"20191004\",\"20191005\",\"20191006\",\"20191007\"],\"dragon_boat_festival\":[\"20190607\"],\"mid_fall_festival\":[\"20180913\"]}}},\"calendar\":{\"2017-2018\":{\"spring\":{\"start\":\"20180226\",\"end\":\"20180715\"}},\"2018-2019\":{\"fall\":{\"start\":\"20180903\",\"end\":\"20190120\"},\"spring\":{\"start\":\"20190225\",\"end\":\"20190714\"}}}}\n";
    }

    public static String getDepartmentPhoneCacheData(){
        return "{\"result\":\"ok\",\"党群部门\":{\"学校办公室\":\"88493361\",\"党委组织部\":\"88491644\",\"党委宣传部\":\"88460638\",\"党委统战部\":\"88491979\",\"纪委办/监察处\":\"88493708\",\"党委学工部(学生处、人民武装部)\":\"88430680\",\"党委研究生工作部\":\"88430691\",\"机关党委\":\"88495920\",\"工会\":\"88492256\",\"团委\":\"88430887\",\"离退休党委（离退休工作处）\":\"88495442\",\"社区居民委员会\":\"88492409\"},\"行政部门\":{\"发展规划处\":\"88492224\",\"研究生院（学科建设办公室）\":\"88430620\",\"教务处\":\"88430586\",\"科学技术管理部\":\"88494377\",\"国际合作处（国际教育学院）\":\"88492267\",\"人事处\":\"88460219\",\"财务处\":\"88491367\",\"国资处\":\"88495056\",\"基建处\":\"88492033\",\"审计处\":\"88492226\",\"保卫处\":\"88494110\",\"保密处\":\"88493297\",\"校友总会办公室\":\"88492542\",\"信息中心\":\"88491174\",\"长安校区管理办公室\":\"88430153\",\"后勤办公室\":\"88492571\",\"招标与设备采购中心\":\"88460760\"},\"教学单位\":{\"航空学院\":\"88460479\",\"航天学院\":\"88492781\",\"航海学院\":\"88492611\",\"材料学院\":\"88492642\",\"机电学院\":\"88495297\",\"力学与土木建筑学院\":\"88431000\",\"动力与能源学院\":\"88431112\",\"电子信息学院\":\"88431206\",\"自动化学院\":\"88431389\",\"计算机学院\":\"88431518\",\"理学院\":\"88431652\",\"管理学院\":\"88431781\",\"人文与经法学院\":\"88431900\",\"软件与微电子学院\":\"88460410\",\"生命学院\":\"88460332\",\"外国语学院\":\"88430908\",\"教育实验学院\":\"88430283\",\"马克思主义学院\":\"88431900\",\"西北工业大学伦敦玛丽女王大学工程学院\":\"88431985\",\"工程实践训练中心\":\"88430371\",\"体育部\":\"88493539\",\"继续教育学院、网络教育学院\":\"88493731\",\"国家保密学院\":\"88495027\",\"艺术教育中心\":\"88430220\",\"明德学院（独立学院）\":\"85603158\"},\"产学研、服务及其他部门\":{\"三六五研究所\":\"88451020\",\"资产经营公司\":\"88494062\",\"深圳研究院\":\"88460825\",\"西北工业技术研究院\":\"81113262\",\"无人系统发展战略研究中心\":\"88460896\",\"无人系统技术研究院\":\"88460895\",\"档案馆（校史馆）\":\"88493159\",\"图书馆\":\"88492361\",\"校医院\":\"88493471\",\"后勤产业集团\":\"88493099\",\"分析测试中心\":\"88460747\",\"出版社\":\"88492314\",\"附中\":\"88492401\",\"附小\":\"88460322\",\"幼儿园\":\"88493741\"}}";
    }
}
