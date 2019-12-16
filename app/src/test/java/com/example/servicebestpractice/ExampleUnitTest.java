package com.example.servicebestpractice;

import android.net.nsd.NsdManager;

import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Logger;

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
//        System.out.println("xxx");

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
        AbstractFactory abstractFactory1 = new ConcreteFactory1();
        AbstractFactory abstractFactory2 = new ConcreteFactory2();

        ProductA productA1WeNeed = abstractFactory1.createProductA();
        ProductA productA2WeNeed = abstractFactory2.createProductA();

        ProductB productB1WeNeed = abstractFactory1.createProductB();
        ProductB productB2WeNeed = abstractFactory2.createProductB();

    }

    @Test
    public void testStringBuilder() {
    }

    @Test
    public void testPrototype() {
        concretePrototype prototype1 = new concretePrototype("abc");
        concretePrototype prototype2 = (concretePrototype) prototype1.myClone();
        prototype2.filed = "123";
//        System.out.println(prototype1 + "     " + prototype2);
    }

    @Test
    public void testChainOfResponsibility() {
    }

    @Test
    public void testCommand() {
        Light light = new Light();
        Command commandOn = new LightOnCommand(light);
        Command commandOff = new LightOffCommand(light);
        Invoker invoker = new Invoker();
        invoker.setOnCommands(commandOn, 0);
        invoker.setOnCommands(commandOn, 1);
        invoker.setOnCommands(commandOff, 2);
        invoker.setOffCommands(commandOn, 0);
        invoker.setOffCommands(commandOn, 1);
        invoker.setOffCommands(commandOff, 2);

        invoker.onButtonWasPushed(1);
        invoker.offButtonWasPushed(1);

    }

    @Test
    public void testStatic() {
        Outer outer = new Outer();
        //测试内部类,静态内部类多些限制条件,外可以访问内,,变量跟随类生命周期,
        Outer.Inner1 inner1 = new Outer.Inner1();
        Outer.Inner2 inner2 = new Outer().new Inner2();
//        inner1.runInner1();
//        inner2.runInner2();
    }


}

/**
 * hjl
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

/**
 * 抽象工厂
 * xpath.xpathfactory ioc util.calendar
 */
abstract class ProductA {
}

abstract class ProductB {
}

class ProductA1 extends ProductA {
}

class ProductA2 extends ProductA {
}

class ProductB1 extends ProductB {
}

class ProductB2 extends ProductB {
}

abstract class AbstractFactory {
    abstract ProductA createProductA();

    abstract ProductB createProductB();
}

class ConcreteFactory1 extends AbstractFactory {
    //A1 B1为一家工厂生产 a2 b2为另一家工厂生产
    @Override
    ProductA createProductA() {
        return new ProductA1();
    }

    @Override
    ProductB createProductB() {
        return new ProductB1();
    }
}

class ConcreteFactory2 extends AbstractFactory {

    @Override
    ProductA createProductA() {
        return new ProductA2();
    }

    @Override
    ProductB createProductB() {
        return new ProductB2();
    }
}
/**
 * 建造者 核心是return this
 * nio.bytebuffer lang.stringbuild lang.stringbuffer
 */

/**
 * 原型模式 多例模式
 * lang.object.clone
 */
abstract class Prototype {

    abstract Prototype myClone();
}

class concretePrototype extends Prototype {
    public String filed;

    public concretePrototype(String filed) {
        this.filed = filed;
    }

    @Override
    Prototype myClone() {
        return new concretePrototype(filed);
    }

    @Override
    public String toString() {
        return "concretePrototype{" +
                "filed='" + filed + '\'' +
                '}';
    }
}

/**
 * 责任链模式  父类变量protected可以被继承      * 未完成
 * servlet.filter
 */
abstract class Handler {
    private Handler successor;

    public Handler(Handler successor) {
        this.successor = successor;
    }

    public abstract void handleRequest(Request request);
}

class Request {
    private RequestType type;
    private String name;

    public Request(RequestType type, String name) {
        this.type = type;
        this.name = name;
    }

    public RequestType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

enum RequestType {
    TYPE1, TYPE2;
}

class ConcreteHandler1 extends Handler {

    public ConcreteHandler1(Handler successor) {
        super(successor);
    }

    @Override
    public void handleRequest(Request request) {
        if (request.getType().equals(RequestType.TYPE1))
            System.out.println("this is type1");

    }
}
/**
 *
 */

/**
 * 命令模式
 * swing.Action hystrix lang.runnable
 */
interface Command {
    void execute();
}

class LightOnCommand implements Command {
    //开灯命令
    Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.turnOn();
    }
}

class LightOffCommand implements Command {
    Light light;

    @Override
    public void execute() {
        light.turnOff();
    }

    public LightOffCommand(Light light) {
        this.light = light;
    }
}

class Light {
    public void turnOn() {
        System.out.println("turnOn");
    }

    public void turnOff() {
        System.out.println("turnOff");
    }
}

class Invoker {
    //遥控
    private Command[] onCommands;
    private Command[] offCommands;
    //数组大小,但放这里有什么用呢?
    private final int soltNum = 7;

    //传入两个参数完成命令存储
    public void setOnCommands(Command onCommands, int soltNum) {
        this.onCommands[soltNum] = onCommands;
    }

    public void setOffCommands(Command offCommands, int soltNum) {
        this.offCommands[soltNum] = offCommands;
    }

    public Invoker() {
        //初始化两个命令数组
        this.onCommands = new Command[soltNum];
        this.offCommands = new Command[soltNum];
    }

    //具体命令动作调用对应命令数组的执行
    public void onButtonWasPushed(int soltNum) {
        onCommands[soltNum].execute();
    }

    public void offButtonWasPushed(int soltNum) {
        offCommands[soltNum].execute();
    }
}


/**
 * 备忘录模式 和命令模式结合可变为可撤销命令的功能
 *java.io.Serializable
 */


/***
 * hjl
 */

class Outer {

    static int num = 999;

    void runo() {
        System.out.println(num);
    }

    static class Inner1 {
        int num1 = 101;

        void runInner1() {
            //普通,静态内部类和js闭包用法类似,但其实有差别,
            // 差别在于js中,外封闭域中没有像Java中还要区别变量存在于实例变量(包含类变量),还是局部变量.
            // 如果是实例变量则和js闭包一样,外封闭域和内封闭域共享共享同一个变量,在堆上.如果是局部变量,则外封闭域和内封闭域不共享共享同一个变量.
            //即外封闭域的方法中的局部变量和内封闭域匿名内部类中的局部变量在不同的栈中.
            //如果内外封闭域中变量不一致则违反闭包原则,因此设为final不可变,使得内外封闭域变量一致
            num1 = Outer.num;
            System.out.println(num1 + "   ");

        }

    }

    class Inner2 {
        int inner2 = 201;
        Dog dog = new Dog();

        void runInner2() {
            //好玩的事情,变量放在方法外不需要final和方法内需要final
            //但利用数组仍然可以改变对象
            final int[] inner2Local = {2001};
            final Dog dogLocal = new Dog();
            final Dog[] dogLocalArr = {new Dog(), new Dog()};

            System.out.println(inner2);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    System.out.println(inner2 + " " + inner2Local[0] + "  " + inner2Local
                            + "  " + dog.name + " " + dogLocal.name + "  " + dogLocal + "   " + dogLocalArr[0]);
                    inner2++;
                    inner2Local[0]++;
                    dog.name = "cat";
                    dogLocal.name = "cat";
                    dogLocalArr[0] = new Dog();

                }
            }).start();

            try {
                //最好玩的事情出现了,有延时和没有延时完全是不同的结果,inner2被改变需要一定的时间
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(inner2 + " " + inner2Local[0] + "  " + inner2Local
                    + "  " + dog.name + " " + dogLocal.name + "  " + dogLocal + "   " + dogLocalArr[0]
                    + "开启thread后    ");
        }
    }

    class Dog {
        public String name = "dog";
    }

}

