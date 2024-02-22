# Android自定义View圆形进度条  满足绝大部分业务需求 自带了进度条动画
### 如果对你有帮助点个star⭐️行不行 谢谢大佬！

#### 样式预览：
 ![image](https://github.com/yuqiaodan/AndroidCircleProgressBar/assets/51314874/28d541d0-7816-4fc7-9797-0e9f9cfe085c)

#### 相关自定义属性：

| 属性  | 作用 |
| ---------- | -----------|
| `progress`   | 当前进度   |
| `maxProgress`   | 最大进度   |
| `progressWidth`   | 进度条宽度（dp）   |
| `mainProgressColor`   | 主进度条颜色   |
| `childProgressColor`   | 子进度条颜色（随进度增加而减少的那个进度条）   |
| `isShowChildProgress`   | 是否显示子进度条   |
| `progressRepelAngle`   | 子进度条和主进度条之间的排斥角度   |
| `pathColor`   | 进度条轨道颜色   |
| `pathPadding`   | 轨道内边距距离（dp）   |
| `isClockwise`   | 是否为顺时针方向   |

#### PopCircleProgress相关方法：

| 方法  | 作用 |
| ---------- | -----------|
| `setProgress` `setProgress`  | 设置当前进度   |
| `setMaxProgress` `getMaxProgress`  | 设置最大进度   |
| `setProgressSmooth(startProgress: Int, endProgress: Int, during: Long)`   |   从进度a变化到进度b（自带动画）  |

#### 用法：
- 1.将PopCircleProgress拷贝到你的项目中
- 2.将attrs的内容拷贝到你的项目attrs中
- 3.在你的布局中使用：
 ```
   <com.tomato.amelia.customviewstudy.view.PopCircleProgress
                android:id="@+id/circle_progress"
                android:layout_width="200dp"
                android:layout_height="200dp"
                android:layout_marginTop="20dp"
                app:childProgressColor="#81B29A"
                app:isShowChildProgress="true"
                app:mainProgressColor="#E07A5F"
                app:maxProgress="1000"
                app:pathColor="#3D405B"
                app:pathPadding="5dp"
                app:progress="100"
                app:progressRepelAngle="3"
                app:progressWidth="20dp" />

```

  跑个动画：
 ``` kotlin
      fun playProgressAnim(progressBar:PopCircleProgress){
        val anim = ValueAnimator.ofInt(0, progressBar.getMaxProgress())
        anim.interpolator = LinearInterpolator()
        anim.addUpdateListener {
            val value = it.animatedValue as Int
            progressBar.setProgress(value)
        }
        anim.duration = 5 * 1000L
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
    }
 ```


