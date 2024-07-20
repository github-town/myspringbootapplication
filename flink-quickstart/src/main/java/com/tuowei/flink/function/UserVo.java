package com.tuowei.flink.function;

public class UserVo {

    private String name;
    private int age;

    public UserVo(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
