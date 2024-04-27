package com.myworld.gradution_project_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @ClassName Algorithm
 * @Descripton TODO
 * @Author wkz
 * @Date 2024/4/2 20:29
 * @Version 1.0
 */
@SpringBootTest
public class Algorithm {
    @Test
    public void alg1(){
        Vector<Integer> nums = new Vector<Integer>(Arrays.asList(-1, 0, 3, 4, 7, 9));
//        List<Integer> nums = new ArrayList<>();
        int target = 4;
        int l=0,r=nums.size();
        while(l+1!=r)
        {
            int mid = (l+r)/2;
            if(nums.get(mid)<=target){
                l=mid;
            }
            else{
                r=mid;
            }
        }
       if(nums.get(l)==target){
            System.out.println(l);
       }else {
           System.out.println(-1);
       }
    }
//    @Test
    public void alg2(List<String> s){
        int l=0,r=s.size()-1;
        for(int i=0;i<s.size()/2;i++){
            String temp = s.get(l);
            s.set(l,s.get(r));
            s.set(r,temp);
            l++;
            r--;
        }
        System.out.println(s);
    }
    @Test
    public void testAlg2(){
        alg2(new ArrayList(Arrays.asList("h","e","l","l","o")));
        alg2(new ArrayList(Arrays.asList("a","b","c","d")));
        alg2(new ArrayList(Arrays.asList("H","a","n","n","a","h")));
        alg2(new ArrayList(Arrays.asList("a")));
    }
    Vector<Vector<String>> ans = new Vector();
    Vector<String> tmp = new Vector<>();
    public boolean isPalindrome(String s,int l,int r) {
        for(int i = l,j=r;i<j;i++,j--){
            if(s.charAt(i)!=s.charAt(j)){
                return false;
            }
        }
        return true;
    }

    public void findSubString(String s,int startIndex){
        if (startIndex>=s.length()){
            Vector<String> val = new Vector<>();
            val.addAll(tmp);
            ans.add(val);
            return;
        }
        for (int i = startIndex; i < s.length(); i++) {
            if (isPalindrome(s,startIndex,i)){
                tmp.add(s.substring(startIndex,i+1));
                findSubString(s,i+1);
                tmp.remove(tmp.size()-1);
            }
        }
    }
    @Test
    public void alg3(){
        String s = "aabcdcfg";
        findSubString(s,0);
    }

}
