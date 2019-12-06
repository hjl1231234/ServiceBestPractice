package com.example.servicebestpractice;

import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSingletonOne() {
        SingletonOne singletonOne1 = SingletonOne.getSingletonOne();
        singletonOne1.setName("1");
        SingletonOne singletonOne2 = SingletonOne.getSingletonOne();
        singletonOne2.setName("2");
//测单例
//        System.out.println(singletonOne1.getName() + "  " + singletonOne2.getName());
        assertEquals(true, singletonOne1 == singletonOne2);
        //测反射
        //singletonone类型反射数组 这里没有用到
        SingletonOne[] singletonOneArr = (SingletonOne[]) Array.newInstance(SingletonOne.class, 2);

        try {
            //反射得到类,forname相比于classload直接进行了静态链接步骤,classlaod还需要多进行一次true确认
            Class clazz = Class.forName("com.example.servicebestpractice.ExampleUnitTest");
            //通过反射类反射拿到方法,然后进行调用.可能出现找不到方法异常和找到方法无法调用异常,不能实例化异常.
            //invoke中的参数是object类型.  class类型实例化后为object类型.
            clazz.getDeclaredMethod("addition_isCorrect").invoke(clazz.newInstance());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
//分别是appclassloader extclassloader bootclassloader(null)
        ClassLoader classLoaderGrandSon = ExampleUnitTest.class.getClassLoader();
        ClassLoader classLoaderSon = classLoaderGrandSon.getParent();
        ClassLoader classLoaderParent = classLoaderSon.getParent();

        try {
            Class clazz2 = classLoaderGrandSon.loadClass("com.example.servicebestpractice.ExampleUnitTest");
            ExampleUnitTest o1 = (ExampleUnitTest) clazz2.newInstance();
            ExampleUnitTest o2 = o1.getClass().newInstance();
//            System.out.println("显然o1 o2不是同一个对象    " + o1 + "  " + o2);


//            ClassLoader singleOneGrandSon = SingletonOne.class.getClassLoader();
//            Class clazz3 = singleOneGrandSon.loadClass("com.example.servicebestpractice.SingletonOne");
            /**
             * 上面两句和forname等价
             */
            Class clazz3 = Class.forName("com.example.servicebestpractice.SingletonOne");
            /**
             Object o3_1 = clazz3.newInstance();单例模式无效
             面对private必须用constructor和setaccessible
             *
             */
            Constructor cs = clazz3.getDeclaredConstructor();
            cs.setAccessible(true);
            Object o3_1 = cs.newInstance();
            Constructor cs2 = o3_1.getClass().getDeclaredConstructor();
            cs2.setAccessible(true);
            Object o3_2 = cs2.newInstance();
//            System.out.println("显然o3_1 o3_2不是同一个对象    " + o3_1 + "  " + o3_2);


/**
 * 体会枚举类单例的好例子
 */

//            Class clazz4 = Class.forName("com.example.servicebestpractice.SingleTonThree");
//            Constructor cs3 = clazz4.getDeclaredConstructor();
//            cs3.setAccessible(true);
//            Object o4_1 = cs.newInstance();
//            Constructor cs4 = o3_1.getClass().getDeclaredConstructor();
//            cs4.setAccessible(true);
//            Object o4_2 = cs2.newInstance();
//            System.out.println("显然o4_1 o4_2是同一个对象    " + o4_1 + "  " + o4_2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("会异常因为这是枚举类根本木有构造器,如果throw exception则try后面也不会执行");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


//        System.out.println(classLoaderGrandSon + "    " + classLoaderSon + "  " + classLoaderParent);
        assertEquals(false, classLoaderGrandSon == classLoaderSon);
        System.out.println("xxx");

    }

    @Test
    public void testSingletonTwo() {
        SingletonTwo singletonTwo1 = SingletonTwo.getInstance();
        SingletonTwo singletonTwo2 = SingletonTwo.getInstance();
        singletonTwo1.setName("1");
        singletonTwo2.setName("2");
        assertEquals(true, singletonTwo1 == singletonTwo2);


    }

    @Test
    public void testSingletonThree() {
        SingleTonThree singleTonThree1 = SingleTonThree.INSTANCE;
        SingleTonThree singleTonThree2 = SingleTonThree.INSTANCE;
        singleTonThree1.setName("1");
        singleTonThree2.setName("2");
        assertEquals(true, singleTonThree1.getName() == singleTonThree2.getName());
        SingleTonThree[] singleTonThreesArr = SingleTonThree.class.getEnumConstants();
        for (SingleTonThree k : singleTonThreesArr) {
//            System.out.println(k);
        }
    }

    @Test
    public void testAbstractFactory() {


    }

}

/**
 * 单例模式1
 */
class SingletonOne {
    private static volatile SingletonOne singletonOne;

    public static void setSingletonOne(SingletonOne singletonOne) {
        SingletonOne.singletonOne = singletonOne;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    private SingletonOne() {
    }

    //方便用静态方式调用
    public static SingletonOne getSingletonOne() {
        //双重加锁法
        if (singletonOne == null) {
            synchronized (SingletonOne.class) {
                if (singletonOne == null) {
                    singletonOne = new SingletonOne();
                }
            }

        }
        return singletonOne;
    }

}

/**
 * 单例模式2
 */
class SingletonTwo {
    private SingletonTwo() {
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //静态内部类法
    private static class SingletonHolder {
        private static SingletonTwo INSTANCE = new SingletonTwo();
    }

    //方便用静态方式调用
    public static SingletonTwo getInstance() {
        return SingletonHolder.INSTANCE;
    }

}

/**
 * 单例模式3
 * 序列化后仍然保证单例,其他必须用transient字段才行
 */
enum SingleTonThree {
    INSTANCE;
    private String name = "什么都没有";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
