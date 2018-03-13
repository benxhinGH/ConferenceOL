package com.usiellau.conferenceol.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by UsielLau on 2018/3/13 0013 15:06.
 *
 * 将字符串型的成员id序列转换为set并提供操作方法
 */

public class IdConverter {
    private Set<Integer> set;

    public IdConverter(String s){
        set=new HashSet();
        String[] arr=null;
        if(s!=null){
            arr=s.split(",");
            for(String s1:arr){
                set.add(Integer.valueOf(s1));
            }
        }
    }
    public boolean remove(int i){
        return set.remove(i);
    }
    public boolean add(int i){
        return set.add(i);
    }

    public boolean contains(int i){
        return set.contains(i);
    }

    public List<Integer> getList(){
        List<Integer> list=new ArrayList<>();
        for(int i:set){
            list.add(i);
        }
        return list;
    }


    public String toString(){

        String res="";
        for(int i:set){
            res+=i+",";
        }
        if(res.length()==0)return "";
        res=res.substring(0,res.length()-1);
        return res;
    }
}
