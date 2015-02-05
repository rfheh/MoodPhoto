package com.mp.util;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

public class OS_BuildUtil {

	public static final String ABI_ARM = "armeabi";
	public static final String ABI_ARMv7a = "armeabi-v7a";
	public static final String ABI_MIPS = "mips";
	public static final String ABI_X86 = "x86";
	public static int ANDROID_UNKNOWN = 22;
	public static int FROYO = 8;
	public static int GINGERBREAD = 9;
	public static int GINGERBREAD_MR1 = 10;
	public static int HONEYCOMB = 11;
	public static int HONEYCOMB_MR1 = 12;
	public static int HONEYCOMB_MR2 = 13;
	public static int ICE_CREAM_SANDWICH = 14;
	public static int ICE_CREAM_SANDWICH_MR1 = 15;
	public static int JELLY_BEAN = 16;
	public static int JELLY_BEAN_MR1 = 17;
	public static int JELLY_BEAN_MR2 = 18;
	public static int KITKAT = 19;
	public static int KITKAT_WATCH = 20;
	public static int LOLLIPOP = 21;

	public static final String getParsedCpuAbiInfo() {
		StringBuilder localStringBuilder = new StringBuilder();
		String str1 = get_CPU_ABI();
		if (!TextUtils.isEmpty(str1)) {
			localStringBuilder.append("CPU ABI : ");
			localStringBuilder.append(str1);
			localStringBuilder.append("\n");
		}
		String str2 = get_CPU_ABI2();
		if (!TextUtils.isEmpty(str1)) {
			localStringBuilder.append("CPU ABI2 : ");
			localStringBuilder.append(str2);
			localStringBuilder.append("\n");
		}
		return localStringBuilder.toString();
	}

	public static final String getParsedExtraBuildInfo() {
		StringBuilder localStringBuilder = new StringBuilder();
		localStringBuilder.append("\nboard:");
		localStringBuilder.append(Build.BOARD);
		retrieveBootLoader(localStringBuilder);
		localStringBuilder.append("\nbrand:");
		localStringBuilder.append(Build.BRAND);
		localStringBuilder.append("\ndevice:");
		localStringBuilder.append(Build.DEVICE);
		localStringBuilder.append("\ndisplay:");
		localStringBuilder.append(Build.DISPLAY);
		localStringBuilder.append("\nfingerprint:");
		localStringBuilder.append(Build.FINGERPRINT);
		retrieveHardware(localStringBuilder);
		localStringBuilder.append("\nhost:");
		localStringBuilder.append(Build.HOST);
		localStringBuilder.append("\nid:");
		localStringBuilder.append(Build.ID);
		localStringBuilder.append("\nmanufacturer:");
		localStringBuilder.append(Build.MANUFACTURER);
		localStringBuilder.append("\nmodel:");
		localStringBuilder.append(Build.MODEL);
		localStringBuilder.append("\nproduct:");
		localStringBuilder.append(Build.PRODUCT);
		retrieveSerial(localStringBuilder);
		localStringBuilder.append("\ntag:");
		localStringBuilder.append(Build.TAGS);
		localStringBuilder.append("\ntime:");
		localStringBuilder.append(String.valueOf(Build.TIME));
		localStringBuilder.append("\ntype:");
		localStringBuilder.append(Build.TYPE);
		localStringBuilder.append("\nuser:");
		localStringBuilder.append(Build.USER);
		return localStringBuilder.toString();
	}

	public static final int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	public static final String get_CPU_ABI() {
		return Build.CPU_ABI;
	}

	public static final String get_CPU_ABI2() {
		try {
			Field localField = Build.class.getDeclaredField("CPU_ABI2");
			if (localField == null) {
				return null;
			}
			Object localObject = localField.get(null);
			if ((localField != null) && ((localObject instanceof String))) {
				String str = (String) localObject;
				return str;
			}
		} catch (Exception localException) {
			return null;
		}
		return null;
	}

	public static final boolean isApi10_GingerBreadOrEarlier() {
		return getSDKVersion() <= GINGERBREAD_MR1;
	}

	public static final boolean isApi11_13_HoneyComb() {
		int i = getSDKVersion();
		return (i >= HONEYCOMB) && (i <= HONEYCOMB_MR2);
	}

	public static final boolean isApi11_18_JELLY_BEAN_MR2() {
		int i = getSDKVersion();
		return (i >= HONEYCOMB) && (i <= JELLY_BEAN_MR2);
	}

	public static final boolean isApi11_HoneyCombOrLater() {
		return getSDKVersion() >= HONEYCOMB;
	}

	public static final boolean isApi13_HoneyCombMr2OrLater() {
		return getSDKVersion() >= HONEYCOMB_MR2;
	}

	public static final boolean isApi14_IceCreamSandwichOrLater() {
		return getSDKVersion() >= ICE_CREAM_SANDWICH;
	}

	public static final boolean isApi16_JellyBeanOrLater() {
		return getSDKVersion() >= JELLY_BEAN;
	}

	public static final boolean isApi17_JellyBeanMr1OrLater() {
		return getSDKVersion() >= JELLY_BEAN_MR1;
	}

	public static final boolean isApi18_JellyBeanMr2OrLater() {
		return getSDKVersion() >= JELLY_BEAN_MR2;
	}

	public static final boolean isApi19_KitkatOrLater() {
		return getSDKVersion() >= KITKAT;
	}

	public static final boolean isApi21_LollipopOrLater() {
		return getSDKVersion() >= LOLLIPOP;
	}

	public static final boolean isApi22_UnknownOrLater() {
		return getSDKVersion() >= ANDROID_UNKNOWN;
	}

	public static final boolean isApi8_FroyoOrLater() {
		return getSDKVersion() >= FROYO;
	}

	public static final boolean isApi9_GingerBreadOrLater() {
		return getSDKVersion() >= GINGERBREAD;
	}

	@TargetApi(8)
	private static final void retrieveBootLoader(
			StringBuilder paramStringBuilder) {
		if (isApi8_FroyoOrLater()) {
			paramStringBuilder.append("\nbootloader:");
			paramStringBuilder.append(Build.BOOTLOADER);
		}
	}

	@TargetApi(8)
	private static final void retrieveHardware(StringBuilder paramStringBuilder) {
		if (isApi8_FroyoOrLater()) {
			paramStringBuilder.append("\nhardware:");
			paramStringBuilder.append(Build.HARDWARE);
		}
	}

	@TargetApi(9)
	private static final void retrieveSerial(StringBuilder paramStringBuilder) {
		if (isApi9_GingerBreadOrLater()) {
			paramStringBuilder.append("\nhardware:");
			paramStringBuilder.append(Build.SERIAL);
		}
	}

	public static boolean supportABI(String paramString) {
		String str = get_CPU_ABI();
		if ((!TextUtils.isEmpty(str)) && (str.equalsIgnoreCase(paramString))) {
		}
		while ((!TextUtils.isEmpty(get_CPU_ABI2()))
				&& (str.equalsIgnoreCase(paramString))) {
			return true;
		}
		return false;
	}

	public static boolean supportARM() {
		return supportABI("armeabi");
	}

	public static boolean supportARMv7a() {
		return supportABI("armeabi-v7a");
	}

	public static boolean supportMIPS() {
		return supportABI("mips");
	}

	public static boolean supportX86() {
		return supportABI("x86");
	}
}
