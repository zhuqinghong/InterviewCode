package com.inteview.Code;

import java.util.Arrays;

/**
 * 题目描述
 * 给你一根长度为n的绳子，请把绳子剪成整数长的m段（m、n都是整数，n>1并且m>1，m<=n），
 * 每段绳子的长度记为k[1],...,k[m]。请问k[1]x...xk[m]可能的最大乘积是多少？例如，
 * 当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，此时得到的最大乘积是18。
 *
 *
 *
 *
 *   牛客网：
 *   https://www.nowcoder.com/practice/57d85990ba5b440ab888fc72b0751bf8?tpId=13&&tqId=33257&rp=1&ru=/ta/coding-interviews&qru=/ta/coding-interviews/question-ranking
 */


public class CutCope {
    public static void main(String[] args) {

        int result=cutRope(100);
        System.out.println(result);

    }
    public static int cutRope(int target) {
        if(target<=3){
            return target-1;
        }
        int market[]=new int[target+1];
        Arrays.fill(market,-1);
        return dfs(target,market);
    }




    public static int dfs(int target,int market[]){
        if(target<=4){
            return target;
        }

        if(market[target]!=-1){
            return market[target];
        }

        int ret=0;
        for(int i=1;i<target;i++){
            ret = Math.max(ret,i*dfs(target-i,market));
        }
        market[target]=ret;
        return ret;
    }



    public class Solution {
        public int cutRope(int target) {
            if(target==2){
                return 1;
            }else if(target==3){
                return 2;
            }
            int f[]=new int [target+1];
            f[0]=1;
            f[1]=1;
            f[2]=2;
            f[3]=3;

            for(int i=4;i<=target;i++){
                f[i]=0;
                for(int j=0;j<=i;j++){
                    f[i]=Math.max(f[i-j]*j,f[i]);
                }
            }

            return f[target];

        }
    }
}
