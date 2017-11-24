package com.walnutin.hardsdkdemo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 作者：MrJiang on 2016/5/14 09:38d
 */
public class SychromUtils {

	private Map<Long, Integer> phoneSteps = new TreeMap<Long, Integer>();
	private Map<Long, Integer> watchSteps = new TreeMap<Long, Integer>();
	long currentTimestamp; // 当前时间戳
	int phonePosition = 0;
	int wathchPosition = 0;
	int filterTime = 1000;

	public static void main(String[] args) {
		// init();
		SychromUtils util = new SychromUtils();
		util.init();
		util.sychrom();
	}

	// 1101 2201 3302 4405 8899 9999 10000 手表时间戳
	// 1100 2200 3300 6600 7700 8800 9999 10000 手机时间戳

	private void init() {
		watchSteps.put((long) 5555, 1);
		watchSteps.put((long) 6666, 1);
		watchSteps.put((long) 11010, 1);
		watchSteps.put((long) 22010, 1);
		watchSteps.put((long) 23010, 1);
		watchSteps.put((long) 24010, 1);
		watchSteps.put((long) 33020, 1);
		watchSteps.put((long) 44050, 1);
		watchSteps.put((long) 69000, 1);
		watchSteps.put((long) 88990, 1);
		watchSteps.put((long) 99990, 1);
		watchSteps.put((long) 100000, 1);
		watchSteps.put((long) 200000, 1);

		phoneSteps.put((long) 1000, 1);
		phoneSteps.put((long) 2000, 1);
		phoneSteps.put((long) 2800, 1);
		phoneSteps.put((long) 2900, 1);
		phoneSteps.put((long) 3000, 1);
		phoneSteps.put((long) 4000, 1);
		phoneSteps.put((long) 5000, 1);
		phoneSteps.put((long) 6000, 1);
		phoneSteps.put((long) 7000, 1);
		phoneSteps.put((long) 10080, 1);
		phoneSteps.put((long) 22000, 1);
		phoneSteps.put((long) 33000, 1);
		phoneSteps.put((long) 66000, 1);
		phoneSteps.put((long) 72000, 1);
		phoneSteps.put((long) 77000, 1);
		phoneSteps.put((long) 88000, 1);
		phoneSteps.put((long) 99990, 1);
		phoneSteps.put((long) 110000, 1);
		phoneSteps.put((long) 130000, 1);
		phoneSteps.put((long) 160000, 1);

		// }
	}

	List<Long> wathcListKeys = new ArrayList<Long>();
	List<Long> phoneListKeys = new ArrayList<Long>();
	int allSteps = 0;
	long watchNextGap = 0; // 下一次 手表的时间戳开始值
	long phoneNextGap = 0; // 下一次手机的时间戳开始值

	public void sychrom() {
		Set<Long> watchKeys = watchSteps.keySet();
		Set<Long> phoneKeys = phoneSteps.keySet();
		wathcListKeys.addAll(watchKeys);
		phoneListKeys.addAll(phoneKeys);

		long startPhoneTimestamp = phoneListKeys.get(0);
		long startWatchTimestamp = wathcListKeys.get(0);
		System.out.println("-==-=" + startPhoneTimestamp);

		if (startWatchTimestamp - startPhoneTimestamp < filterTime) { // 开始遍历
			// 手表的数据
			currentTimestamp = startWatchTimestamp; // 得到第一个键值
			watchNextGap = startWatchTimestamp;
			findWatch();
		} else { // 遍历手机的数据

			currentTimestamp = startPhoneTimestamp; // 得到第一个键值
			phoneNextGap = startPhoneTimestamp;
			System.out.println("phone1: " + phoneNextGap);
			findPhone();
		}

	}

	// 1101 2201 3302 4405 8899 9999 10000 手表时间戳
	// 1008 2200 3300 6600 7700 8800 9999 10000 手机时间戳

	int findCurrentTimestmpPosition(int startPosition, List<Long> list) {

		for (int i = startPosition; i < list.size(); i++) {
			if (list.get(i) >= currentTimestamp) {
				return i;
			}
		}
		return list.size() - 1;

	}

	// 1101 2201 3302 4405 8899 9998 10001 11111 13333 16666手表时间戳
	// 1008 2200 3300 6600 7700 8800 9999 10000 12222 手机时间戳

	void findWatch() {

		if (wathchPosition >= wathcListKeys.size()) {
			// return;
			recordPhoneNoCondition();
			return;
		}
		if (phoneNextGap > wathcListKeys.get(wathchPosition)) {
			wathchPosition += 1;
		}
		int tempWatchPosition = findCurrentTimestmpPosition(wathchPosition,
				wathcListKeys);
		for (int i = tempWatchPosition; i < wathcListKeys.size(); i++) {
			if (wathcListKeys.get(i) - currentTimestamp < filterTime
					|| Math.abs(wathcListKeys.get(i) - phoneNextGap) < filterTime) { // 去手机查找记步信息
				System.out.println("手表1: " + wathcListKeys.get(i));
				currentTimestamp = wathcListKeys.get(i);
				allSteps++;
				wathchPosition = i;
			} else if (phoneNextGap - wathcListKeys.get(i) > 0) {
				System.out.println("手表: " + wathcListKeys.get(i));
				currentTimestamp = wathcListKeys.get(i);
				allSteps++;
				wathchPosition = i;
			} else {
				watchNextGap = wathcListKeys.get(i); //
				break;

				// findPhone();
			}
		}

		if (wathchPosition < wathcListKeys.size() - 1) {
			findPhone();
		} else {
			recordPhoneNoCondition();
		}

	}

	// 88990
	// 66000 77000 88000

	boolean isPhoneFirst = true;

	void findPhone() {

		if (phonePosition >= phoneListKeys.size()) {
			recordWatchNoCondition();
			return;
		}
		if (isPhoneFirst)
			if (watchNextGap > phoneListKeys.get(phonePosition)) {
				phonePosition += 1;
			}
		isPhoneFirst = true;
		// System.out.println("PhonecurrentTimestamp : " +
		// currentTimestamp+" phonePosition: "+phonePosition);
		int tempPhonePosition = findCurrentTimestmpPosition(phonePosition,
				phoneListKeys); // 找到当前时间戳的 后一个的时间戳位置
		for (int i = tempPhonePosition; i < phoneListKeys.size(); i++) {
			if ((phoneListKeys.get(i) - currentTimestamp < filterTime || (phoneListKeys
					.get(i) < watchNextGap))
					&& (watchNextGap - phoneListKeys.get(i)) > filterTime) {
				currentTimestamp = phoneListKeys.get(i);
				allSteps++;
				phonePosition = i;
				System.out.println("phone1: " + phoneListKeys.get(i));
			} else if (watchNextGap - phoneListKeys.get(i) > filterTime) {
				System.out.println("phone: " + phoneListKeys.get(i));
			} else {
				phoneNextGap = phoneListKeys.get(i);
				// System.out.println("phoneNextGap -----------------------"
				// + phoneNextGap);
				// findWatch();
				break;
			}

		}
		if (phonePosition < phoneListKeys.size() - 1) {
			findWatch();
		} else {
			recordWatchNoCondition();
		}

	}

	// 1101 2201 3302 4405 8899 9999 10000 手表时间戳
	// 1008 2200 3300 6600 7700 8800 9999 11000 手机时间戳

	void recordPhoneNoCondition() {
		// System.out.println("PhonecurrentTimestamp : " + currentTimestamp
		// + " phonePosition: " + phonePosition);

		int tempPhonePosition = findCurrentTimestmpPosition(phonePosition,
				phoneListKeys); // 找到当前时间戳的 后一个的时间戳位置

		for (int i = tempPhonePosition; i < phoneListKeys.size(); i++) {
			currentTimestamp = phoneListKeys.get(i);
			allSteps++;
			phonePosition = i;
			System.out.println("phone: " + phoneListKeys.get(i));
		}
	}

	void recordWatchNoCondition() {
		int tempPhonePosition = findCurrentTimestmpPosition(wathchPosition,
				wathcListKeys); // 找到当前时间戳的 后一个的时间戳位置

		for (int i = tempPhonePosition; i < wathcListKeys.size(); i++) {
			currentTimestamp = wathcListKeys.get(i);
			allSteps++;
			wathchPosition = i;
			System.out.println("手表: " + wathcListKeys.get(i));
		}
	}

}
