# Activity 与 Fragment 共享 ViewModel + LiveData 教学 Demo

本文是一份独立教学文档，目标是讲清楚 Android 中 `Activity`、多个 `Fragment` 如何通过同一个 `ViewModel` 和 `LiveData` 共享页面状态。

这份 Demo 不依赖当前项目中的任何 `BaseActivity`、`BaseFragment`、资源、工具类或业务代码。你可以把它复制到任意新的 Android 工程中运行。示例包名统一使用：

```text
com.example.sharedvmldemo
```

## 1. 这套写法解决什么问题

在 Android 页面里，经常会遇到这种结构：

- 一个 `Activity` 作为页面容器
- 左边或上面的 `Fragment A` 负责输入、点击、筛选
- 右边或下面的 `Fragment B` 负责展示结果
- `Activity` 也可能需要同步显示页面状态

如果不用共享 `ViewModel`，常见做法是：

- Fragment 通过接口回调 Activity
- Activity 再调用另一个 Fragment 的方法
- 或者使用 `setFragmentResult`
- 或者用全局单例临时保存状态

这些方式不是不能用，但当状态越来越多时，调用链会越来越散。

共享 `ViewModel + LiveData` 的核心思想是：

```text
Fragment A 修改 ViewModel 中的数据
Fragment B 观察 ViewModel 中的数据
Activity 也可以观察同一个 ViewModel 中的数据
```

这样 Fragment 之间不需要互相持有引用，也不需要知道对方存在。

## 2. 核心知识点

### 2.1 ViewModel 是什么

`ViewModel` 是用来保存 UI 状态的类。它的生命周期比普通 `Activity` / `Fragment` 实例更稳定。

例如屏幕旋转时，`Activity` 可能会重建，但同一个作用域下的 `ViewModel` 可以继续保留原来的数据。

适合放在 `ViewModel` 里的内容：

- 页面计数、输入框内容、筛选条件、选中的 Tab
- 网络请求结果、列表数据、加载状态
- 页面级临时状态

不适合放在 `ViewModel` 里的内容：

- `Activity`、`Fragment`、`View`、`Context` 的强引用
- Dialog、Toast、Animator 这类和界面实例强绑定的对象
- 大量无法及时释放的资源

### 2.2 LiveData 是什么

`LiveData<T>` 是一个可观察的数据容器。

它的特点是：

- 有数据变化时，会通知观察者
- 观察者可以绑定生命周期
- 当 `Fragment` 的 View 销毁后，使用 `viewLifecycleOwner` 观察的回调会自动停止
- 新观察者会收到当前最新值

典型写法：

```kotlin
viewModel.count.observe(viewLifecycleOwner) { count ->
    textView.text = count.toString()
}
```

### 2.3 什么叫 Activity 和 Fragment 共享 ViewModel

共享的关键不是 `ViewModel` 类本身，而是 `ViewModelStoreOwner`。

在 Fragment 中有两种常见拿法：

```kotlin
private val viewModel: SharedCounterViewModel by viewModels()
```

这表示：当前 Fragment 自己独享一个 `ViewModel`。

```kotlin
private val viewModel: SharedCounterViewModel by activityViewModels()
```

这表示：使用宿主 Activity 的 `ViewModelStore`，所以同一个 Activity 下的多个 Fragment 会拿到同一个 `ViewModel` 实例。

本文的 Demo 使用：

```kotlin
private val viewModel: SharedCounterViewModel by activityViewModels()
```

### 2.4 为什么 Fragment 观察 LiveData 要用 viewLifecycleOwner

Fragment 有两个生命周期：

- Fragment 实例自己的生命周期
- Fragment 内部 View 的生命周期

`onDestroyView()` 之后，Fragment 实例可能还活着，但它的布局 View 已经销毁了。

因此在 Fragment 里观察 LiveData 时，推荐使用：

```kotlin
viewModel.count.observe(viewLifecycleOwner) { count ->
    // 这里访问 binding 或 findViewById 拿到的 View 才是安全的
}
```

不要在 Fragment 里直接用：

```kotlin
viewModel.count.observe(this) { ... }
```

因为 `this` 指的是 Fragment 实例生命周期，容易让回调在 View 销毁后继续访问旧 View。

### 2.5 为什么 ViewModel 里通常写两个 LiveData

推荐写法：

```kotlin
private val _count = MutableLiveData(0)
val count: LiveData<Int> = _count
```

含义是：

- `_count` 是内部可修改数据，只给 `ViewModel` 自己用
- `count` 是外部只读数据，只暴露给 `Activity` / `Fragment` 观察

这样可以避免外部页面随意改状态，状态修改入口都收敛到 `ViewModel` 的方法里。

## 3. Demo 效果

这个 Demo 有一个 Activity 和两个 Fragment：

| 文件 | 作用 |
| --- | --- |
| `SharedViewModelActivity.kt` | 宿主 Activity，加载两个 Fragment，也观察共享状态 |
| `SharedCounterViewModel.kt` | 共享 ViewModel，保存计数和昵称 |
| `CounterInputFragment.kt` | 输入区 Fragment，负责修改昵称、增加、减少、重置 |
| `CounterPreviewFragment.kt` | 展示区 Fragment，负责观察并展示共享状态 |
| `activity_shared_view_model.xml` | Activity 容器布局 |
| `fragment_counter_input.xml` | 输入区布局 |
| `fragment_counter_preview.xml` | 展示区布局 |

交互流程：

```text
用户在 CounterInputFragment 点击 +1
    -> 调用 viewModel.increase()
    -> ViewModel 修改 _count
    -> count LiveData 发出新值
    -> CounterPreviewFragment 自动刷新显示
    -> Activity 顶部标题也自动刷新
```

## 4. Gradle 依赖

如果你的工程还没有 Fragment KTX 和 Lifecycle 依赖，可以添加：

```kotlin
dependencies {
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
}
```

这几个依赖分别提供：

| 依赖 | 作用 |
| --- | --- |
| `activity-ktx` | 提供 Activity 中的 `by viewModels()` 等 Kotlin 扩展 |
| `fragment-ktx` | 提供 `by activityViewModels()`、`commit {}` 等 Kotlin 扩展 |
| `lifecycle-viewmodel-ktx` | 提供 ViewModel 相关 Kotlin 支持 |
| `lifecycle-livedata-ktx` | 提供 LiveData 相关 Kotlin 支持 |

## 5. 完整 Demo 目录

建议放在一个独立包下：

```text
app/src/main/java/com/example/sharedvmldemo/
    SharedViewModelActivity.kt
    SharedCounterViewModel.kt
    CounterInputFragment.kt
    CounterPreviewFragment.kt

app/src/main/res/layout/
    activity_shared_view_model.xml
    fragment_counter_input.xml
    fragment_counter_preview.xml
```

如果你要在真实工程里打开这个页面，还需要在 `AndroidManifest.xml` 注册 Activity。

## 6. 完整代码

### 6.1 SharedCounterViewModel.kt

```kotlin
package com.example.sharedvmldemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedCounterViewModel : ViewModel() {

    private val _count = MutableLiveData(0)
    val count: LiveData<Int> = _count

    private val _nickname = MutableLiveData("")
    val nickname: LiveData<String> = _nickname

    fun increase() {
        val current = _count.value ?: 0
        _count.value = current + 1
    }

    fun decrease() {
        val current = _count.value ?: 0
        _count.value = current - 1
    }

    fun reset() {
        _count.value = 0
        _nickname.value = ""
    }

    fun updateNickname(value: String) {
        if (_nickname.value == value) return
        _nickname.value = value
    }
}
```

这段代码要点：

- `_count` 和 `_nickname` 是内部可变状态
- `count` 和 `nickname` 是外部只读状态
- 页面只能通过 `increase()`、`decrease()`、`reset()`、`updateNickname()` 修改数据
- `LiveData` 一旦更新，所有观察者都会收到通知

### 6.2 SharedViewModelActivity.kt

```kotlin
package com.example.sharedvmldemo

import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.activity.viewModels

class SharedViewModelActivity : FragmentActivity() {

    private val viewModel: SharedCounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_view_model)

        val titleTextView = findViewById<TextView>(R.id.titleTextView)

        viewModel.count.observe(this) { count ->
            titleTextView.text = "Activity 也在观察同一个 ViewModel：count = $count"
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.inputContainer, CounterInputFragment())
                replace(R.id.previewContainer, CounterPreviewFragment())
            }
        }
    }
}
```

这段代码要点：

- Activity 使用 `by viewModels()` 获取自己的页面级 `ViewModel`
- 两个 Fragment 后面会使用 `by activityViewModels()` 获取 Activity 的这个同一个实例
- `savedInstanceState == null` 是为了避免屏幕旋转后重复添加 Fragment

### 6.3 CounterInputFragment.kt

```kotlin
package com.example.sharedvmldemo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class CounterInputFragment : Fragment() {

    private val viewModel: SharedCounterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_counter_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nicknameEditText = view.findViewById<EditText>(R.id.nicknameEditText)
        val increaseButton = view.findViewById<Button>(R.id.increaseButton)
        val decreaseButton = view.findViewById<Button>(R.id.decreaseButton)
        val resetButton = view.findViewById<Button>(R.id.resetButton)

        nicknameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                viewModel.updateNickname(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        increaseButton.setOnClickListener {
            viewModel.increase()
        }

        decreaseButton.setOnClickListener {
            viewModel.decrease()
        }

        resetButton.setOnClickListener {
            viewModel.reset()
        }

        viewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            if (nicknameEditText.text.toString() != nickname) {
                nicknameEditText.setText(nickname)
                nicknameEditText.setSelection(nickname.length)
            }
        }
    }
}
```

这段代码要点：

- `CounterInputFragment` 不直接找 `CounterPreviewFragment`
- 它只负责把用户操作写入共享 `ViewModel`
- 使用 `activityViewModels()`，所以拿到的是 Activity 作用域下的同一个 `SharedCounterViewModel`
- 观察 `nickname` 是为了处理 `reset()` 后输入框也能清空
- 设置输入框文本前先判断是否相等，避免重复 `setText()` 触发循环更新

### 6.4 CounterPreviewFragment.kt

```kotlin
package com.example.sharedvmldemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class CounterPreviewFragment : Fragment() {

    private val viewModel: SharedCounterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_counter_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countTextView = view.findViewById<TextView>(R.id.countTextView)
        val nicknameTextView = view.findViewById<TextView>(R.id.nicknameTextView)
        val summaryTextView = view.findViewById<TextView>(R.id.summaryTextView)

        viewModel.count.observe(viewLifecycleOwner) { count ->
            countTextView.text = count.toString()
            refreshSummary(
                summaryTextView = summaryTextView,
                count = count,
                nickname = viewModel.nickname.value.orEmpty()
            )
        }

        viewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            nicknameTextView.text = if (nickname.isBlank()) {
                "还没有输入昵称"
            } else {
                "昵称：$nickname"
            }

            refreshSummary(
                summaryTextView = summaryTextView,
                count = viewModel.count.value ?: 0,
                nickname = nickname
            )
        }
    }

    private fun refreshSummary(
        summaryTextView: TextView,
        count: Int,
        nickname: String
    ) {
        val displayName = nickname.ifBlank { "匿名用户" }
        summaryTextView.text = "$displayName 当前计数是 $count"
    }
}
```

这段代码要点：

- `CounterPreviewFragment` 只观察数据，不关心数据是谁改的
- `count` 和 `nickname` 任意一个变化，展示区都会刷新
- 观察 LiveData 时使用 `viewLifecycleOwner`

### 6.5 activity_shared_view_model.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="20dp">

    <TextView
        android:id="@+id/titleTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Activity 也在观察同一个 ViewModel：count = 0"
        android:textColor="#222222"
        android:textSize="18sp"
        android:textStyle="bold" />

    <FrameLayout
        android:id="@+id/inputContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp" />

    <FrameLayout
        android:id="@+id/previewContainer"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_marginTop="20dp"
        android:layout_weight="1" />

</LinearLayout>
```

这段布局要点：

- `inputContainer` 用来放输入 Fragment
- `previewContainer` 用来放展示 Fragment
- Activity 自己也有一个 `titleTextView`，用来证明 Activity 和 Fragment 观察的是同一个数据源

### 6.6 fragment_counter_input.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#F5F5F5"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="输入区 Fragment"
        android:textColor="#333333"
        android:textSize="16sp"
        android:textStyle="bold" />

    <EditText
        android:id="@+id/nicknameEditText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:hint="请输入昵称"
        android:inputType="text"
        android:maxLines="1"
        android:textSize="15sp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:orientation="horizontal">

        <Button
            android:id="@+id/decreaseButton"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="-1" />

        <Button
            android:id="@+id/increaseButton"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_weight="1"
            android:text="+1" />

        <Button
            android:id="@+id/resetButton"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:layout_weight="1"
            android:text="重置" />

    </LinearLayout>

</LinearLayout>
```

### 6.7 fragment_counter_preview.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#EAF4FF"
    android:gravity="center_horizontal"
    android:orientation="vertical"
    android:padding="24dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center"
        android:text="展示区 Fragment"
        android:textColor="#333333"
        android:textSize="16sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/countTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="28dp"
        android:gravity="center"
        android:text="0"
        android:textColor="#1E88E5"
        android:textSize="56sp"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/nicknameTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:gravity="center"
        android:text="还没有输入昵称"
        android:textColor="#333333"
        android:textSize="16sp" />

    <TextView
        android:id="@+id/summaryTextView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:gravity="center"
        android:text="匿名用户 当前计数是 0"
        android:textColor="#666666"
        android:textSize="14sp" />

</LinearLayout>
```

### 6.8 AndroidManifest.xml 注册示例

如果要运行这个 Activity，需要在 `AndroidManifest.xml` 中注册：

```xml
<activity
    android:name="com.example.sharedvmldemo.SharedViewModelActivity"
    android:exported="false" />
```

如果它是你的启动页，可以写成：

```xml
<activity
    android:name="com.example.sharedvmldemo.SharedViewModelActivity"
    android:exported="true">

    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>

</activity>
```

## 7. 数据流完整拆解

以点击 `+1` 为例：

```kotlin
increaseButton.setOnClickListener {
    viewModel.increase()
}
```

调用到 ViewModel：

```kotlin
fun increase() {
    val current = _count.value ?: 0
    _count.value = current + 1
}
```

`_count.value` 被更新后，所有观察 `count` 的地方都会收到新值：

```kotlin
viewModel.count.observe(viewLifecycleOwner) { count ->
    countTextView.text = count.toString()
}
```

Activity 也会收到：

```kotlin
viewModel.count.observe(this) { count ->
    titleTextView.text = "Activity 也在观察同一个 ViewModel：count = $count"
}
```

这就是共享 ViewModel 的关键价值：数据变化只发生在一个中心位置，页面展示自动跟随变化。

## 8. activityViewModels 和 viewModels 的区别

### 8.1 使用 viewModels

```kotlin
private val viewModel: SharedCounterViewModel by viewModels()
```

每个 Fragment 都会创建或获取自己的 `ViewModel`。

结果是：

```text
CounterInputFragment 的 ViewModel 实例 A
CounterPreviewFragment 的 ViewModel 实例 B
```

这两个实例不是同一个，所以数据不能共享。

### 8.2 使用 activityViewModels

```kotlin
private val viewModel: SharedCounterViewModel by activityViewModels()
```

两个 Fragment 都会使用宿主 Activity 的 `ViewModelStore`。

结果是：

```text
CounterInputFragment 拿到 Activity 作用域的 ViewModel
CounterPreviewFragment 也拿到 Activity 作用域的 ViewModel
```

所以它们拿到的是同一个实例，数据可以共享。

## 9. LiveData 的 setValue 和 postValue

在 ViewModel 中常见两种更新方式：

```kotlin
_count.value = 1
```

等价于 Java 中的 `setValue()`，要求在主线程调用。

```kotlin
_count.postValue(1)
```

可以在子线程调用，最终会切回主线程通知观察者。

简单记忆：

| 方法 | 使用场景 |
| --- | --- |
| `value = xxx` | 当前就在主线程，最常用 |
| `postValue(xxx)` | 当前在子线程，例如后台任务回调 |

本 Demo 中按钮点击和输入框回调都发生在主线程，所以直接使用：

```kotlin
_count.value = current + 1
```

## 10. 常见错误

### 10.1 Fragment 之间直接互相调用

不推荐：

```kotlin
val previewFragment = parentFragmentManager.findFragmentByTag("preview")
if (previewFragment is CounterPreviewFragment) {
    previewFragment.updateCount(1)
}
```

问题：

- Fragment 之间产生直接依赖
- tag、生命周期、重建时机都容易出错
- 页面结构一变，调用关系也要改

推荐：

```kotlin
viewModel.increase()
```

让另一个 Fragment 自己观察变化。

### 10.2 在 Fragment 里用 observe(this)

不推荐：

```kotlin
viewModel.count.observe(this) { count ->
    countTextView.text = count.toString()
}
```

推荐：

```kotlin
viewModel.count.observe(viewLifecycleOwner) { count ->
    countTextView.text = count.toString()
}
```

Fragment 的 View 可能被销毁并重建，使用 `viewLifecycleOwner` 更符合 View 的生命周期。

### 10.3 把 MutableLiveData 暴露给外部

不推荐：

```kotlin
val count = MutableLiveData(0)
```

这样 Activity 和 Fragment 都可以直接修改 `count`，状态来源会变乱。

推荐：

```kotlin
private val _count = MutableLiveData(0)
val count: LiveData<Int> = _count
```

外部只能观察，不能直接改。

### 10.4 ViewModel 持有 View 或 Activity

不推荐：

```kotlin
class SharedCounterViewModel : ViewModel() {
    var activity: Activity? = null
    var textView: TextView? = null
}
```

问题：

- 容易内存泄漏
- 屏幕旋转后引用可能指向旧 Activity 或旧 View

ViewModel 应该保存数据，不保存界面对象。

## 11. 什么时候适合用共享 ViewModel

适合：

- 同一个 Activity 下多个 Fragment 共享筛选条件
- 列表 Fragment 和详情 Fragment 同步选中项
- 输入 Fragment 和预览 Fragment 实时联动
- Activity 顶部栏需要跟随 Fragment 状态变化
- 多个 Fragment 共同维护一个页面级表单

不一定适合：

- 只需要一次性返回结果，可以考虑 Fragment Result API
- 跨 Activity 或全局共享状态，通常需要 Repository、数据库、DataStore 或依赖注入容器
- 复杂单次事件，例如导航、Toast、Snackbar，需要额外处理事件消费问题

## 12. LiveData 表示事件时要注意

`LiveData` 很适合表示状态，比如：

```text
count = 3
nickname = "Tom"
loading = true
```

但它不太适合直接表示一次性事件，比如：

```text
弹 Toast
跳转页面
显示 Snackbar
```

原因是 LiveData 会在新观察者注册时发送当前最新值。屏幕旋转后，旧事件可能再次触发。

如果要处理一次性事件，可以考虑：

- 使用 `Event` 包装类
- 使用 Kotlin `SharedFlow`
- 把导航交给 Activity 或 Navigation 组件管理

本文重点是 `ViewModel + LiveData` 的状态共享，所以不展开事件流。

## 13. 记忆模板

共享 ViewModel 的固定模板可以记成三步。

第一步，ViewModel 中私有可变、公开只读：

```kotlin
private val _state = MutableLiveData(initialValue)
val state: LiveData<Type> = _state
```

第二步，Activity 创建页面作用域 ViewModel：

```kotlin
private val viewModel: MyViewModel by viewModels()
```

第三步，Fragment 使用 Activity 作用域 ViewModel：

```kotlin
private val viewModel: MyViewModel by activityViewModels()
```

然后在 Fragment 里观察：

```kotlin
viewModel.state.observe(viewLifecycleOwner) { state ->
    // update views
}
```

修改数据时只调用 ViewModel 方法：

```kotlin
viewModel.updateState(newValue)
```

## 14. 这份 Demo 最应该掌握的点

- `ViewModel` 负责保存页面状态
- `LiveData` 负责把状态变化通知给界面
- `Activity` 使用 `by viewModels()` 获取自己的 ViewModel
- `Fragment` 使用 `by activityViewModels()` 获取宿主 Activity 的同一个 ViewModel
- Fragment 观察 LiveData 时使用 `viewLifecycleOwner`
- 外部只暴露 `LiveData`，不要暴露 `MutableLiveData`
- Fragment 之间不要直接互相调用，应该通过共享状态通信
