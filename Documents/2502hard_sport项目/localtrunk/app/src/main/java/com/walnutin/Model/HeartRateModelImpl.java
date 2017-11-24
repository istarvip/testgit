package com.walnutin.Model;

import android.content.Context;

import com.walnutin.entity.HeartRateModel;
import com.walnutin.entity.StepModel;
import com.walnutin.util.Conversion;
import com.walnutin.util.PreferenceSettings;
import com.walnutin.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 作者：MrJiang on 2016/8/3 14:07
 */
public class HeartRateModelImpl {
    List<HeartRateModel> heartRateModelList = new ArrayList<>();
    public HeartRateModel currentHeartRateModel = new HeartRateModel();
    private List<Integer> rightHeartList = new ArrayList<>();
    public int currentHeartIndex = 0;
    Context context;
    private PreferenceSettings mPedometerSettings;

    public HeartRateModelImpl(Context context) {
        this.context = context;
        mPedometerSettings = PreferenceSettings.getInstance(context);
    }

    public List loadTodayHeartModelList() {
        long laststampTime = mPedometerSettings.getLast_Seen();
        String lastDate = TimeUtil.timeStamp2YMDDate(laststampTime);
        boolean today = lastDate.equals(TimeUtil.nowDate());
        if (today) {
            heartRateModelList = (List<HeartRateModel>) Conversion.stringToList(mPedometerSettings.getString("devHeartRate", null));
            if (heartRateModelList == null) {
                heartRateModelList = new ArrayList<>();
            } else {
                currentHeartRateModel = heartRateModelList.get(0);
            }
        }
        return heartRateModelList;
    }

    public void addOrderHeartMode(HeartRateModel model) { //按从小到大心率排序，插入
        int index = 0;
        for (HeartRateModel heartRateModel : heartRateModelList) {
            if (heartRateModel.currentRate >= model.currentRate) {
                break;
            }
            index++;
        }
        currentHeartIndex = index;
        heartRateModelList.add(index, model);
    }

    public void addHeartMode(HeartRateModel model) {
        heartRateModelList.add(0, model); // 添加到列表头部
    }

    public void removeHeartMode(int i) {
        heartRateModelList.remove(i);
    }

    public HeartRateModel getHeartRateModelByCurrentRate(int current) {  // 根据心率值得到心率对象
        for (HeartRateModel model : heartRateModelList) {
            if (model.currentRate == current) {
                return model;
            }
        }
        return null;
    }

    public HeartRateModel getNextHeartRateModeByCurrentRate() {
        if (currentHeartIndex == heartRateModelList.size() - 1 || heartRateModelList.size() <= 0) {
            return null;
        }
//        int index = 0;
//        for (HeartRateModel model : heartRateModelList) {
//            if (model.currentRate == current) {
//                break;
//            }
//            index++;
//        }
        currentHeartIndex = currentHeartIndex + 1;
        return currentHeartRateModel = heartRateModelList.get(currentHeartIndex);
    }

    public HeartRateModel getBeforHeartRateModeByCurrentRate() {
        if (currentHeartIndex == 0) {
            return null;
        }
//        int index = 0;
//        for (HeartRateModel model : heartRateModelList) {
//            if (model.currentRate == current) {
//                break;
//
//            }
//            index++;
//        }
        currentHeartIndex = currentHeartIndex - 1;
        return currentHeartRateModel = heartRateModelList.get(currentHeartIndex);
    }

    public void createHeartRateModel() {
        currentHeartRateModel = new HeartRateModel();
    }

    public HeartRateModel getCurrentHeartRateModel() {
        return currentHeartRateModel;
    }

    public void addHeartRateValue(int value) {     //增加心率值
        currentHeartRateModel.heartTrendMap.put(System.currentTimeMillis() / 1000, value);
    }

    public void setDurationTime(int time) {
        currentHeartRateModel.durationTime = time;
    }

    public List<HeartRateModel> getHeartRateModelList() {
        return heartRateModelList;
    }

    public void setHeartRateModelList(List<HeartRateModel> heartRateModelList) {
        this.heartRateModelList = heartRateModelList;
    }

    public int getCurrentHeartIndex() {
        return currentHeartIndex;
    }

    public void setCurrentHeartIndex(int currentHeartIndex) {
        this.currentHeartIndex = currentHeartIndex;
    }

    public void saveTodayHeartModel() {
        mPedometerSettings.setString("devHeartRate", Conversion.listToString(heartRateModelList));

    }

    public void clearCurrentRateList() {
        currentHeartRateModel.heartTrendMap.clear();
    }

    public List<Integer> mapToList() {
        Map<Long, Integer> map = currentHeartRateModel.heartTrendMap;
        List<Integer> list = new ArrayList<>(map.values());
        Collections.reverse(list);    //反转排序

        rightHeartList = list;

        return list;
    }

    public List<Integer> getLeftBarHeartList() {
        List<Integer> list = new ArrayList<>();
        for (HeartRateModel model : heartRateModelList) {
            list.add(model.currentRate);
        }
        return list;
    }


    public void setRecentRateList(List<Integer> recentRateList) {
        currentHeartRateModel.setRecentRateList(recentRateList);
    }

    public void setRealRateList(List<Integer> realRateList) {
        currentHeartRateModel.setRealRateList(realRateList);
    }

    public List<Integer> getRecentRateList() {
        return currentHeartRateModel.recentRateList;
    }

    public List<Integer> getRealRateList() {
        return currentHeartRateModel.realRateList;
    }

    public void setHighRate(int highRate) {
        currentHeartRateModel.HighRate = highRate;
    }

    public void setLowRate(int lowRate) {
        currentHeartRateModel.lowRate = lowRate;
    }

    public void setRightLowRate() {

        if (rightHeartList.size() > 0) {
            currentHeartRateModel.lowRate = rightHeartList.get(0);
        }
    }

    public void setRightHighRate() {

        if (rightHeartList.size() > 0) {
            currentHeartRateModel.HighRate = rightHeartList.get(rightHeartList.size() - 1);
        }
    }

    public void setRightCurrentRate() {
        if (rightHeartList.size() > 0) {
            currentHeartRateModel.currentRate = rightHeartList.get(0);
        }

    }

    public void setCurrentRate(int currentRate) {
        currentHeartRateModel.currentRate = currentRate;
    }

    public int getLowRate() {
        return currentHeartRateModel.lowRate;
    }

    public int getHighRate() {
        return currentHeartRateModel.HighRate;
    }

    public int getCurrentRate() {
        return currentHeartRateModel.currentRate;
    }


    public int getOnLineCurrentRate() {
        return heartRateModelList.size() > 0 ? (int) heartRateModelList.get(heartRateModelList.size() - 1).currentRate : 0;
    }

    public int getHeartListLowRate() {
        int low = 1000;
        for (HeartRateModel heartRateModel : heartRateModelList) {
            if (heartRateModel.lowRate < low) {
                low = heartRateModel.lowRate;
            }
        }
        return low;
    }

    public int getHeartListHighRate() {
        int high = -1;
        for (HeartRateModel heartRateModel : heartRateModelList) {
            if (heartRateModel.getHighRate() > high) {
                high = heartRateModel.lowRate;
            }
        }
        return high;
    }


}
