package com.myspringboot.myspringbootfirstapplication.service;

import com.myspringboot.myspringbootfirstapplication.domain.ExcelPojo;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.Test;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Stream;

@Slf4j
public class MyServiceTest {

    @Test
    public void test01() {
        String str1 = "aaa";
        String str2 = "aaa";
        System.out.println(str1 == str2);
        System.out.println(str1.equals(str2));
        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());

        String str3 = new String("aaa");
        String str4 = new String("aaa");
        System.out.println(str3 == str4);
        System.out.println(str3.equals(str4));
        System.out.println(str3.hashCode());
        System.out.println(str4.hashCode());
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().setSoTimeout(60000);
        channel.configureBlocking(true);
        channel.bind(new InetSocketAddress(9000));
        System.out.println("启动9000端口!!!");
        for (;;){
            SocketChannel accept = channel.accept();
            System.out.println(accept.getRemoteAddress());
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int read = accept.read(byteBuffer);
                System.out.println(read);

                String receive = new String(byteBuffer.array(), 0, byteBuffer.limit());
                System.out.println("RECEIVED : " + receive);

                StringBuilder stringBuilder = new StringBuilder("HTTP/1.1 200 OK\r\n");
                stringBuilder.append("Content-Type:application/json\r\n");
                stringBuilder.append("\r\n");
                stringBuilder.append("success");

                accept.write(ByteBuffer.wrap(stringBuilder.toString().getBytes()));
                accept.shutdownOutput();
                accept.close();
            } catch (Exception ex) {
                log.error("ServerSocketChannel error", ex);
            }
        }
    }

    @Test
    public void test02() {
        Integer i = 100;
        Integer i2 = 100;
        System.out.println(i==i2);

        try {
            int a = 1 / 0;
        } catch (ParameterizedAssertionError error) {
            System.out.println("catched");
        } finally {
            System.out.println("fally runed");
        }
    }

    @Test
    public void test03() {
        ExcelPojo excelPojo = new ExcelPojo();
        excelPojo.setName("pojo");
        excelPojo.getArr().add("pojo");

        ExcelPojo clone = excelPojo.clone();
        System.out.println("name: " + excelPojo.getName() + "," + excelPojo);
        System.out.println("name: " + clone.getName() + "," + clone);

        System.out.println(excelPojo== clone);
        System.out.println(excelPojo.equals(clone) );
        System.out.println(excelPojo.hashCode() + "," + clone.hashCode());

        excelPojo.setName("pojo123");
        excelPojo.getArr().add("pojo123");
        System.out.println("name: " + excelPojo.getName() + "," + excelPojo);
        System.out.println("name: " + clone.getName() + "," + clone);
    }

    @Test
    public void test04() {
//        Stream<Integer> stream = Stream.of(100, 50, 20, 30, 100);
//        Optional<Integer> reduce = stream.reduce((b,a) -> a +100);
//        System.out.println(reduce.get());
//
//        IntStream intStream = IntStream.of(100, 50, 20, 30, 100);
//        IntSummaryStatistics summaryStatistics = intStream.summaryStatistics();
//        System.out.println("sum : "+summaryStatistics.getSum());
//        System.out.println("average : "+summaryStatistics.getAverage());
//
//        ExcelPojo excelPojo1 = ExcelPojo.builder().id(100L).build();
//        ExcelPojo excelPojo2 = ExcelPojo.builder().id(1L).build();
//        ExcelPojo excelPojo3 = ExcelPojo.builder().id(50L).build();
//        Stream<ExcelPojo> pojoStream = Stream.of(excelPojo1, excelPojo2, excelPojo3);
//        pojoStream.sorted((o1, o2) -> (int) (o1.getId() - o2.getId())).forEach(System.out::println);
//
//        List<Integer> arr1 = Arrays.asList(1,2,3);
//        List<Integer> arr2 = Arrays.asList(4,5,6);
//        List<Integer> arr3 = Arrays.asList(7,8,9);
//        Stream<List<Integer>> arrStream = Stream.of(arr1, arr2, arr3);
//        arrStream.flatMap(a->a.stream().limit(2)).sorted().forEach(System.out::println);
//
//        Stream<List<Integer>> arrStream2 = Stream.of(arr1, arr2, arr3);
//        List arrays = new ArrayList();
//        arrStream2.peek(arrays::addAll).flatMap(a->a.stream().limit(1)).sorted().forEach(System.out::println);
//        System.out.println("addAll list : "+arrays);
//
//        Stream<String> strStream = Stream.of("abc", "def", "efg");
//        List<String> collect = strStream.map(String::toUpperCase).peek(System.out::print).collect(Collectors.toList());
//        System.out.println(collect);

        Stream<String> strStream2 = Stream.of("abc", "def", "efg");
        strStream2.map(String::toUpperCase).peek(System.out::print).forEach(System.out::print);

//        Stream<List<String>> strStream3 = Stream.of(Arrays.asList("abc","def","efg"),Arrays.asList("abc"),Arrays.asList("zzz"));
//        strStream3.flatMap(st -> {
//            System.out.println(st);
//            return st.stream();
//        }).forEach(System.out::println);
    }

    @Test
    public void test05() {
        AtomicStampedReference<Integer> reference = new AtomicStampedReference<Integer>(1, 1);
        System.out.println("before : value=" + reference.getReference().intValue() + ",stamp : " + reference.getStamp());
        reference.compareAndSet(reference.getReference().intValue(), reference.getReference().intValue()+1,
                reference.getStamp(), reference.getStamp() + 1);
        System.out.println("after : value=" + reference.getReference().intValue() + ",stamp : " + reference.getStamp());
    }

    @Test
    public void test06() throws InterruptedException {
        long l1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Thread thread = new Thread(() -> {
            try {
                ByteBuffer allocate = ByteBuffer.allocate(1 * 1024 * 1024);
                byte[] aaa = new byte[1 * 1024 * 1024];
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        Thread.sleep(500);
        long l2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("total memory : " + Runtime.getRuntime().totalMemory());
        System.out.println("before memory : " + l1);
        System.out.println("after memory : " + l2);
        System.out.println("thread memory : " + (l2 - l1));
    }

    @Test
    public void test07(){
        String chineseText = "你好,世界!";
        String pinyin = convertToPinyin(chineseText);
        System.out.println("Pinyin of \"" + chineseText + "\": " + pinyin);
    }

    private String convertToPinyin(String chineseText) {
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        for (char c : chineseText.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    pinyin.append(pinyinArray[0].replaceAll("[^a-zA-Z]", "")); // 去除非字母字符
                } else {
                    pinyin.append(c); // 如果不是汉字，直接追加原字符
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 处理异常，如果发生异常可以根据实际情况进行处理
                log.error("BadHanyuPinyinOutputFormatCombination error", e);
                pinyin.append(c); // 异常时直接追加原字符
            }
        }
        return pinyin.toString().toLowerCase(); // 转换为小写
    }
}
