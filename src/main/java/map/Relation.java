package map;

import Proxy.ProxyUtils;
import device.Airconditioner;
import device.Gree;
import device.Panasonic;
import device.Philips;
import device.Midea;
import device.Opple;
import runtime.AirCleaner;
import runtime.AirCleanerImpl;
import runtime.AirCondition;
import runtime.AirConditionImpl;
import runtime.AirConditioners;
import runtime.Light;
import runtime.LightImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runtime.AirCleanersImpl;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

/**
 * @author chenshihong02
 * @version 1.0
 * @created 2018/12/23 下午6:50
 **/
public class Relation {

	// 类之间的映射 即 k - 底层设备类 v - 运行时设备类
	public static Map<String, String> classMaps = new HashMap<>();

	// 方法之间的映射 即 k - 运行时api v - 底层设备api
	public static Map<String, List<String>> apiMaps = new HashMap<>();

	// 底层设备对象与运行时对象之间的映射 k - 运行时对象 v - 底层设备对象
	public static Map<Object, Object> objMaps = new HashMap<>();

	/**
	 * 这边应该是读取配置文件得到映射关系 ，但我这边直接初始化映射关系
	 */
	public static void config() throws Exception {

		// 类之间的映射关系
		// 模拟配置
		String packageUnderDevice = "device";
		String configUnderDevice = "Gree";
		String UDString = packageUnderDevice + "." + configUnderDevice;

		String packageRuntimeDevice = "runtime";
		String configRutimeDevice = "AirConditionImpl";
		String RTString = packageRuntimeDevice + "." + configRutimeDevice;

		// 空调
		Class<?> underDevice = Class.forName(UDString);
		Class<?> runtimeDevice = Class.forName(RTString);
		classMaps.put(underDevice.getName(), runtimeDevice.getName());
		classMaps.put(Panasonic.class.getName(), AirConditionImpl.class.getName());
		// 电灯
		classMaps.put(Midea.class.getName(), LightImpl.class.getName());
		classMaps.put(Opple.class.getName(), LightImpl.class.getName());
		// 空气净化器
		classMaps.put(Philips.class.getName(), AirCleanerImpl.class.getName());

		// 方法之间的映射关系
		// 1.空调的降温方法
		apiMaps.put(AirCondition.class.getName() + "." + AirCondition.class.getMethod("cool").getName(),
				Arrays.asList(new String[] { Gree.class.getName() + "." + Gree.class.getMethod("cool").getName(),
						Panasonic.class.getName() + "." + Panasonic.class.getMethod("down").getName() }));
		apiMaps.put(AirCondition.class.getName() + "." + AirCondition.class.getMethod("setT", float.class).getName(),
				Arrays.asList(new String[] {
						Gree.class.getName() + "." + Gree.class.getMethod("setTemperature", float.class).getName(),
						Panasonic.class.getName() + "."
								+ Panasonic.class.getMethod("setTemperature", float.class).getName() }));
		apiMaps.put(AirCondition.class.getName() + "." + AirCondition.class.getMethod("setID", String.class).getName(),
				Arrays.asList(new String[] {
						Gree.class.getName() + "." + Gree.class.getMethod("setId", String.class).getName(),
						Panasonic.class.getName() + "."
								+ Panasonic.class.getMethod("setId", String.class).getName() }));
		apiMaps.put(AirCondition.class.getName() + "." + AirCondition.class.getMethod("getID").getName(),
				Arrays.asList(new String[] { Gree.class.getName() + "." + Gree.class.getMethod("getId").getName(),
						Panasonic.class.getName() + "." + Panasonic.class.getMethod("getId").getName() }));

		// 2.电灯的提高亮度方法
		apiMaps.put(Light.class.getName() + "." + Light.class.getMethod("illumine").getName(),
				Arrays.asList(new String[] {
						Midea.class.getName() + "." + Midea.class.getMethod("IncreaseLedBrightness").getName(),
						Opple.class.getName() + "." + Opple.class.getMethod("RaiseBrightness").getName() }));
		// 3.电灯的降低亮度方法
		apiMaps.put(Light.class.getName() + "." + Light.class.getMethod("darken").getName(),
				Arrays.asList(new String[] {
						Midea.class.getName() + "." + Midea.class.getMethod("ReduceLedBrightness").getName(),
						Opple.class.getName() + "." + Opple.class.getMethod("LowerBrightness").getName() }));

		// 4.空气净化器的净化方法

		apiMaps.put(AirCleaner.class.getName() + "." + AirCleaner.class.getMethod("purify").getName(), Arrays.asList(
				new String[] { Philips.class.getName() + "." + Philips.class.getMethod("ReducePM2_5").getName() }));

		// 5.空气净化器的获得当前PM2.5方法
		apiMaps.put(AirCleaner.class.getName() + "." + AirCleaner.class.getMethod("getPM2_5").getName(), Arrays.asList(
				new String[] { Philips.class.getName() + "." + Philips.class.getMethod("getPM2_5").getName() }));
		// 5.空气净化器的获得设置PM2.5方法
		apiMaps.put(AirCleaner.class.getName() + "." + AirCleaner.class.getMethod("setPM2_5", float.class).getName(),
				Arrays.asList(new String[] {
						Philips.class.getName() + "." + Philips.class.getMethod("setPM2_5", float.class).getName() }));

//		for (List<String> s : apiMaps.values()) {
//			System.out.println(s);
//		}
		
//		
//		apiMaps.put(AirConditioners.class.getName() + "." + AirConditioners.class.getMethod("addlist",Object.class).getName(),
//				Arrays.asList(new String[] { Airconditioner.class.getName() + "." + Airconditioner.class.getMethod("addlist",Object.class).getName()}));
//		apiMaps.put(AirConditioners.class.getName() + "." + AirConditioners.class.getMethod("list").getName(),
//				Arrays.asList(new String[] { Airconditioner.class.getName() + "." + Airconditioner.class.getMethod("list").getName()}));

	}

//	public static <T> 

	/**
	 * 通过配置文件生成底层设备，这边我就手动写设备
	 */
	public static void generateDeviceAndRuntime() throws Exception {
		Map<String, Object> idObjmaps=new HashMap<>();//运行时对象标识与运行时对象的映射
		Map<String, String> idmaps=new HashMap<>();//运行时对象标识与底层设备id的映射
		List<Object> oList=new ArrayList<Object>();
//		List<HashMap<String, Object>> idObjList = new ArrayList<HashMap<String,Object>>();
		
		// 底层设备生成 返回一个运行时对象
		AirCondition gree = (AirCondition) generate(Gree.class.getName());
		AirCondition panasonic = (AirCondition) generate(Panasonic.class.getName());
////
//		Light midea = (Light) generate(Midea.class.getName());
//		Light opple = (Light) generate(Opple.class.getName());
////		
		
//		AirCleaner philips=(AirCleaner) generate(Philips.class.getName());
		
//		HashMap<String, String>
//		runtime.AirCleanersImpl.add();
		// 运行时对象调用
		gree.setT(12);
		gree.cool();
		gree.setID("A1");
		
		idObjmaps.put(String.valueOf(gree.hashCode()),gree);
		idmaps.put(gree.getID(), String.valueOf(gree.hashCode()));
		oList.add(gree);
		panasonic.cool();
		panasonic.setID("A2");
		idObjmaps.put(String.valueOf(panasonic.hashCode()),panasonic);
		idmaps.put(panasonic.getID(),String.valueOf(panasonic.hashCode()));
		oList.add(panasonic);
		
		
		for(String kString:idObjmaps.keySet()) {
			Object o=idObjmaps.get(kString);
			System.out.println(o.hashCode());
		}
//		for(String iString:idmaps.keySet()) {
//			String s=idmaps.get(iString);
//			System.out.println(s);
//		}
		//运行时空调对象集合，有添加空调的方法addlist和列出运行时空调的方法list
		AirConditioners acs = new AirConditioners();
		//遍历运行时对象标识与底层设备id的映射，添加运行时设备对应的底层设备id
		for(Map.Entry<String, String> mEntry :idmaps.entrySet()) {
			acs.addlist(mEntry.getKey());
		}
		//列出运行时的空调对应的底层空调
		acs.list();
		

//		acs.addlist(panasonic);
//		List<Object> aList=acs.list();
//		System.out.println(aList.get(0));
//		List<String> aList=new ArrayList<>();
//		aList.add(gree.getClass().getName());
////		aList.add(panasonic);
//		System.out.println(aList);
//		System.out.println(aList.get(0).);
//		System.out.println(gree);
		
//		for(Object ob:aList)
		
		
//		panasonic.getID();

//		midea.illumine();
//		opple.illumine();
//		midea.darken();
//		opple.darken();
		
//		philips.setPM2_5(30);
//		philips.purify();
//		System.out.println(philips.getPM2_5());
		
	}

	/**
	 * device 底层设备类名
	 * 
	 * @param device
	 */
	private static Object generate(String device) throws Exception {
		// 生成底层设备对象
		Object deviceObj = Class.forName(device).newInstance();
		// 通过类映射关系 获取到底层设备 对应的 运行时类
		for (String deviceType : classMaps.keySet()) {

			if (deviceType.equals(device)) {
				String runtimeType = classMaps.get(deviceType);

				Class<?> runtimeClass = Class.forName(runtimeType);
				// 生成运行时对象
				Object runtimeObj = runtimeClass.newInstance();

				// 将运行时对象的类型设置成底层设备的类型
				Field type = runtimeClass.getDeclaredField("type");

				// 获得各个设备的方法
				Method[] methods = runtimeClass.getDeclaredMethods();

				String functionType = "";

				for (Method m : methods) {
					String temp = m.toString();
					String[] ff = temp.split("\\.");
					String[] ff2 = ff[2].split("\\(");
					functionType = ff2[0];
//				Method method = runtimeClass.getDeclaredMethod(functionType);

				}

				type.setAccessible(true);
				type.set(runtimeObj, deviceType);

				System.out.println(runtimeObj);
				// 生成运行时对象的代理对象
				Object proxyObj = ProxyUtils.getProxy(runtimeObj);

				// 将运行时对象代理与底层设备对象放入objMaps
				objMaps.put(proxyObj, deviceObj);
				return proxyObj;
			}
		}
		return null;
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		config();
		generateDeviceAndRuntime();
	}
}