# 5 Nguyên lý lập trình hướng đối tượng
1. Single Responsibility Principle
2. Open/Closed Principle
3. Liskov Substitution Principle
4. Interface Segregation Principle
5. Dependency Inversion Principle
<br>Ở bài này, chúng ta sẽ giải quyết nguyên lý thứ 5: Dependency Inversion

# Dependency Inversion
__Nội dung nguyên lý__
```
1. Các module cấp cao không nên phụ thuộc vào các module cấp thấp. Cả 2 nên phụ thuộc vào abstraction.
2. Interface (abstraction) không nên phụ thuộc vào chi tiết, mà ngược lại. 
(Các class giao tiếp với nhau thông qua interface, không phải thông qua implementation.)
```
__Ví dụ__
<br>Chúng ta có 1 class Car đơn giản như sau.
<br>Tuy nhiên class Car hiện tại đang phi phạm nguyên lý do module cấp cao là Car đang phụ thuộc vào module cấp thấp là Engine và Wheels
```kotlin
class Car() {
    var engine: Engine? = null
    var wheels: Wheels? = null

    init {
        engine = Engine()
        wheels = Wheels()
    }
}
```
__Cách khắc phục__: Truyền engine và car thông qua hàm khởi tạo, như vậy chúng ta đã giải quyết được vấn đề trên
```kotlin
class Car() {
    var engine: Engine? = null
    var wheels: Wheels? = null

    constructor(engine: Engine, wheels: Wheels){
        this.engine = engine
        this.wheels = wheels
    }
}
```
Trong kotlin, chúng ta có thể viết gọn lại như sau. 
```kotlin
data class Car(
    val engine: Engine,
    val wheels: Wheels
)
```
Vậy là đã hoàn thành __manual dependency injection__ cho class Car rồi ^^ 
<br>Qua ví dụ trên, có thể nói rằng Car __depend on__ (phụ thuộc vào) Engine và Wheels. Engine và Wheels là __dependency of__(sự phụ thuộc của) Car. Engine và Wheels được inject vào Car.
<br>Với __manual dependency injection__ mỗi khi ta thay đổi constructor hay chỉnh sửa các class thì sẽ phải cập nhật thay đổi đó ở tất cả các nơi chúng ta sử dụng. Và đây là lúc __các dependency injection framework như Dagger2, Hilt, Koin__ vào cuộc!!

## Hilt
### Hilt là gì
___
```
Hilt được xây dựng dựa trên 1 dependency injection framework nổi tiếng là Dagger, được hưởng lợi từ tính chính xác của thời gian biên dịch, hiệu suất thời gian chạy, khả năng mở rộng và hỗ trợ Android Studio mà Dagger cung cấp
```

### Thêm dependencies
___
Trong _build.gradle_
```kotlin
buildscript {
    ...
    dependencies {
        ...
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'
    }
}
```
Trong _app/build.gradle_
```kotlin
...
apply plugin: 'kotlin-kapt'
apply plugin: 'dagger.hilt.android.plugin'

android {
    ...
    compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
}

dependencies {
    implementation "com.google.dagger:hilt-android:2.28-alpha"
    kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
}
```

### Hilt application class
___
Tất cả app sử dụng Hilt cần phải có 1 class Application annotated với @HiltAndroidApp.
```kotlin
@HiltAndroidApp
class ExampleApplication : Application() { ... }
```

### Inject dependencies into Android classes
___
Hilt có thể cung cấp dependencies cho các Android classes có @AndroidEntryPoint annotation:
```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() { ... }
```
Hilt hiện tại hỗ trợ các Android class:
* Application (bằng cách sử dụng @HiltAndroidApp)
* Activity
* Fragment
* View
* Service
* BroadcastReceiver

<br>Để nhận được dependencies từ 1 component nào đó, sử dụng @Inject
```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {

  @Inject lateinit var analytics: AnalyticsAdapter
  ...
}
```
Với ví dụ trên, chúng ta đang sử dụng kĩ thuật __field injection__
<br> Cùng xem 1 kĩ thuật khác là __constructor injection__ nhé. Đây là kĩ thuật nên sử dụng hơn là __field injection__ 
```kotlin
@AndroidEntryPoint
class ExampleActivity(
  private val analytics: AnalyticsAdapter
): AppCompatActivity()
```
Vậy làm sao để cung cấp AnalyticsAdapter cho ExampleActivity? Chúng ta cùng xem ví dụ dưới đây
```kotlin
class AnalyticsAdapter 
@Inject 
constructor(
  private val service: AnalyticsService
) { ... }
```
Ở đây, chúng ta vừa sử dụng __constructor injection__: cung cấp AnalyticsService cho AnalyticsAdapter. Đồng thời nói với Hilt hãy cung cấp AnalyticsAdapter cho bất cứ Android classes nào yêu cầu. 
### Hilt modules
___
Đôi khi chúng ta không thể làm __constructor-inject__ giống như trên. Có nhiều nguyên nhân cho việc này. Ví dụ, không thể constructor-inject 1 interface. Cũng không thể constructor-inject 1 class nào đó chúng ta không sở hữu, ví dụ như các class từ thư viện bên ngoài. Trong những trường hợp này, chúng ta có thể sử dụng Hilt Modules
<br>Ví dụ 
```kotlin
@InstallIn(ApplicationComponent::class)
@Module
object AppModule {
   
}
```
Có 2 cách để __provides (cung cấp)__ dependencies cho classes: Sử dụng _@Binds_ hoặc _@Provides_. Ở đây chúng ta sẽ chỉ sử dụng @Provides.
<br>__Inject instances with @Provides__
<br>Ví dụ provides glide dependency
```kotlin
@InstallIn(ApplicationComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideGlide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context).setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .error(R.drawable.ic_error)
        )
    }
}
```

<br>__Provides dependency có cùng kiểu__
<br>Ví dụ chúng ta cần provides 2 string như sau
```kotlin
@Singleton
@Provides
fun provideHelloString(): String{
  return "Hello"
}

@Singleton
@Provides
fun provideGoodbyeString(): String{
  return "Goodbye"
}
```
Ở trường hợp này cả 2 dependency cùng kiểu string, Hilt không thể nhận ra được nên provides string nào. Trong trường hợp này xử lí như sau. 
<br>Tạo 2 Qualifier như dưới
```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HelloString

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoodbyeString
```
Và bây giờ provides với annotation vừa tạo
```kotlin
@HelloString
@Singleton
@Provides
fun provideHelloString(): String{
  return "Hello"
}

@GoodbyeString
@Singleton
@Provides
fun provideGoodbyeString(): String{
  return "Goodbye"
}
```

__Provides Context với Hilt__
<br>Bạn có thể cần Context class từ application hoặc activity, Hilt cung cấp cho chúng ta @ApplicationContext và @ActivityContext qualifiers để làm điều đó.
<br>Ví dụ
```kotlin
class AnalyticsAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private val service: AnalyticsService
) { ... }
```

### Generated components for Android classes
___
Ở ví dụ trên, chúng ta sử dụng @InstallIn(ApplicationComponent::class). Vậy nó có ý nghĩa gì
<br>__Các Component trong Hilt__
| Hilt Components    |  Injector for   |
|-----|-----|
|ApplicationComponent  |Application   |
|ActivityRetainedComponent  |ViewModel  |
|ActivityComponent  |Activity  |
|FragmentComponent  |Fragment  |
|ViewComponent  |View  |
|ViewWithFragmentComponent  |View annotated with @WithFragmentBindings  |
|ServiceComponent  |Service  |

__Component Lifetime__: Phần quan trọng đây, các dependency được inject vào các class Android sẽ được tạo hay hủy bỏ khi nào là phụ thuộc vào các component ở trên
| Hilt Components |Created At| Destroyed At   |
|-----|-----|-----|
|ApplicationComponent  |Application#onCreate()   | 	Application#onDestroy()|
|ActivityRetainedComponent  |Activity#onCreate()  |Activity#onDestroy()
|ActivityComponent  |Activity#onCreate()  |Activity#onDestroy()
|FragmentComponent  |Fragment#onAttach()| 	Fragment#onDestroy()
|ViewComponent  |View#super()| 	View destroyed
|ViewWithFragmentComponent  | 	View#super() |	View destroyed
|ServiceComponent  |Service#onCreate() |	Service#onDestroy()

<br>__Component scopes__
Mặc định, tất cả dependency được provides chưa có scope. Điều này có nghĩa là mỗi lần app yêu cầu 1 dependency nào đó, Hilt sẽ tạo ra 1 instance mới của dependency tương ứng
| Hilt Components    |  Scope  |
|-----|-----|
|ApplicationComponent  |@Singleton   |
|ActivityRetainedComponent  |@ActivityRetainedScope  |
|ActivityComponent  | 	@ActivityScoped  |
|FragmentComponent  |@FragmentScoped  |
|ViewComponent  |@ViewScoped  |
|ViewWithFragmentComponent  |@ViewScoped |
|ServiceComponent  |@ServiceScoped  |

```
Scoping a binding to a component can be costly because the provided object stays in memory until that component is destroyed. Minimize the use of scoped bindings in your application. It is appropriate to use component-scoped bindings for bindings with an internal state that requires that same instance to be used within a certain scope, for bindings that need synchronization, or for bindings that you have measured to be expensive to create.
```
__Component hierarchy__ Installing a module into a component allows its bindings to be accessed as a dependency of other bindings in that component or in any child component below it in the component hierarchy:
![Component hierarchy](https://developer.android.com/images/training/dependency-injection/hilt-hierarchy.svg)

### Inject class không được hỗ trợ bởi Hilt
___
Hilt hỗ trợ gần như tất cả Android classes. Tuy nhiên, chúng ta vẫn phải sử dụng field injection trong 1 vài class Hilt không hỗ trợ.
<br>Ví dụ, Hilt không hỗ trợ __content providers__. Nếu chúng ta muốn content provider sử dụng Hilt để lấy được dependencies nào đó, cần tạo 1 interface như sau
```kotlin
class ExampleContentProvider : ContentProvider() {

  @EntryPoint
  @InstallIn(ApplicationComponent::class)
  interface ExampleContentProviderEntryPoint {
    fun analyticsService(): AnalyticsService
  }

  override fun query(...): Cursor {
    val appContext = context?.applicationContext ?: throw IllegalStateException()
    val hiltEntryPoint =
      EntryPointAccessors.fromApplication(appContext, ExampleContentProviderEntryPoint::class.java)

    val analyticsService = hiltEntryPoint.analyticsService()
    ...
  }
}
```
### Viewmodel Injection
___
__Thêm dependency__
```kotlin
 implementation 'androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha01'
 kapt 'androidx.hilt:hilt-compiler:1.0.0-alpha01'
```

__Sử dụng__
```kotlin
class ExampleViewModel @ViewModelInject constructor(
  private val repository: ExampleRepository,
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  ...
}
```
1 tips khởi tạo viewmodel nhanh
```kotlin
@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {
  private val exampleViewModel: ExampleViewModel by viewModels()
  ...
}

```

# Tham khảo
https://developer.android.com/training/dependency-injection/hilt-android#kotlin














