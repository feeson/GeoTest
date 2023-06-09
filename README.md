# GeoTest
一种基于A* 算法实现具有一定初速的舰船从点A至点B的航迹规划算法  
  
  ## 参考经验公式：  
  - 阻力：
    阻力 = C * V^3
    - 滑行艇（船舶速度较高，如快艇）：C = 0.1 - 0.2
    - 航行艇（船舶速度中等，如游艇）：C = 0.2 - 0.4
    - 商船（船舶速度较低，如货船）：C = 0.4 - 0.6
    - 拖船（船舶速度较低，但牵引力较大）：C = 0.6 - 0.8
    - 战列舰：C = 0.6 - 0.8
    - 巡洋舰：C = 0.5 - 0.7
  - 回转半径：  
    回转半径 ≈ 0.06 × V² / A  
    A: 船舶的转向受力（单位：吨力）
  - 牵引力：  
    F=P/(v+f)
      
 ## 实验数据（USS Missouri）：  
 - 总功率：212,000马力（158,000千瓦）
 - 满载排水量：45,000吨
 - 阻力系数：0.8
 - 转向受力：0.021675
 - 起始点: (0,0)
 - 起始速度:(0,15)
 - 满功率运行
   
 ## 估计数值： 
  - 回转半径：300-2000m
  - 最大航速：18m/s
   
 ## 使用: 
  Main.class
    
## 实验结果： 
![GQP@7LP8HHLUR8{M`R6SSUC](https://github.com/feeson/GeoTest/assets/33180551/cf3287fc-25a0-4a6a-b8a6-659b6690d5d1)

![LD`0I`}@JW 3JHC7BQT3U(9](https://github.com/feeson/GeoTest/assets/33180551/d77115ef-f495-4ee1-9478-08e3f552ddeb)
