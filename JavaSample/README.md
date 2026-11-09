# ADwhale Mediation SDK - Java 샘플

ADwhale Mediation SDK **2.7.10** 연동 예시를 Java로 작성한 샘플 앱입니다.

## 요구 사항

| 항목 | 값 |
|---|---|
| minSdk | 24 |
| compileSdk / targetSdk | 35 |
| Java | sourceCompatibility / targetCompatibility 11 |
| 빌드 JDK | 17 (Android Studio 내장 JDK 권장) |
| Android Gradle Plugin | 8.1.0 |
| Gradle | 8.0.2 |

## 의존성 (2.7.10 기준)

### 저장소 (`build.gradle`, 프로젝트 수준)

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        // AdWhale SDK
        maven { url 'https://dev-adwhale.github.io/adwhale-sdk-android-maven/maven-repo' }
        // Cauly SDK
        maven { url 'https://cauly.github.io/cauly-sdk-android-maven/maven-repo' }
        // Admize SDK
        maven { url 'https://cauly.github.io/admize-sdk-android-maven/maven-repo' }
        // AdFit SDK
        maven { url 'https://devrepo.kakao.com/nexus/content/groups/public/' }
    }
}
```

### 라이브러리 (`app/build.gradle`)

| 구분 | 라이브러리 | 버전 |
|---|---|---|
| 기본 | `net.adwhale.sdk.mediation:adwhale-mediation-sdk` | 2.7.10 |
| 기본 | `net.adwhale.sdk.cauly.adapter:cauly-sdk` | 3.5.47.1 |
| 옵션(제외 가능) | `net.adwhale.sdk.admize.adapter:admize-sdk` | 1.0.9.0 |
| 옵션(제외 가능) | `net.adwhale.sdk.adfit.adapter:adfit-sdk` | 3.21.17.7 |
| 옵션(제외 가능) | `net.adwhale.sdk.admob.adapter:admob-sdk` | 24.3.0.10 |
| 옵션(제외 가능) | `net.adwhale.sdk.levelplay.adapter:levelplay-sdk` | 8.12.0.7 |

```groovy
dependencies {
    implementation 'net.adwhale.sdk.mediation:adwhale-mediation-sdk:2.7.10'
    implementation 'net.adwhale.sdk.cauly.adapter:cauly-sdk:3.5.47.1'
    implementation 'net.adwhale.sdk.admize.adapter:admize-sdk:1.0.9.0'
    implementation 'net.adwhale.sdk.adfit.adapter:adfit-sdk:3.21.17.7'
    implementation 'net.adwhale.sdk.admob.adapter:admob-sdk:24.3.0.10'
    implementation 'net.adwhale.sdk.levelplay.adapter:levelplay-sdk:8.12.0.7'

    // 앱 오프닝 광고에서 ProcessLifecycleOwner 사용 시 필요
    implementation 'androidx.lifecycle:lifecycle-process:2.6.2'
}
```

## Manifest 설정

`app/src/main/AndroidManifest.xml`의 `<application>` 안에 다음 메타데이터를 넣습니다.

```xml
<meta-data
    android:name="net.adwhale.sdk.mediation.PUBLISHER_UID"
    android:value="@string/publisher_uid" />
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="@string/admob_app_id" />
```

| 키 | 값 |
|---|---|
| `net.adwhale.sdk.mediation.PUBLISHER_UID` | ADwhale에서 발급한 매체 키 |
| `com.google.android.gms.ads.APPLICATION_ID` | Google AdMob에서 발급한 앱 ID |

## 샘플 실행 방법

1. Android Studio에서 `JavaSample` 폴더를 프로젝트로 엽니다.
2. `app/src/main/res/values/strings.xml`의 값을 발급받은 키로 바꿉니다.
   - `publisher_uid`, `admob_app_id`
   - 지면 ID: `banner32050_placementUid`, `banner320100_placementUid`, `banner300250_placementUid`, `banner250250_placementUid`, `interstitial_placementUid`, `reward_placementUid`, `native_placement_uid`, `app_open_placement_uid`, `exit_popup_placement_uid`, `transition_popup_placement_uid`
3. Gradle Sync 후 `app`을 실행합니다. 첫 화면에서 광고 유형별 버튼으로 각 예시 화면에 들어갑니다.

## 화면별 소스

경로는 `app/src/main/java/kr/co/adwhale/sample/` 기준입니다.

| 광고 유형 | 소스 | 레이아웃 |
|---|---|---|
| SDK 초기화 / 종료 팝업 | `SampleMainActivity.java` | `activity_sample_main.xml` |
| 배너 (코드) | `banner/ProgrammaticBannerMainActivity.java` | `activity_programmatic_banner_main.xml` |
| 배너 (XML) | `banner/XmlBannerMainActivity.java` | `activity_xml_banner_main.xml` |
| 전면 | `interstitial/ProgrammaticInterstitialMainActivity.java` | `activity_programmatic_interstitial_main.xml` |
| 보상형 | `reward/ProgrammaticRewardAdMainActivity.java` | `activity_programmatic_reward_ad_main.xml` |
| 앱 오프닝 | `appopen/ProgrammaticAppOpenMainActivity.java` | `activity_programmatic_app_open_main.xml` |
| 네이티브 (커스텀 바인딩) | `nativead/ProgrammaticCustomBindingNativeMainActivity.java` | `activity_programmatic_custom_binding_native_main.xml`, `custom_native_ad_layout.xml` |
| 네이티브 (템플릿 바인딩) | `nativead/ProgrammaticTemplateBindingNativeMainActivity.java` | `activity_programmatic_template_binding_native_main.xml` |
| 네이티브 (스타일 템플릿 바인딩) | `nativead/StyledTemplateBindingNativeMainActivity.java` | `activity_styled_template_binding_native_main.xml` |
| 전환 팝업 | `transition/TransitionTestMainActivity.java` | `activity_transition_test_main.xml` |

## ProGuard / 릴리스 빌드

- 샘플은 `minifyEnabled false`이지만, 매체 앱에서 난독화(`minifyEnabled true`)를 켠다면 `app/proguard-rules.pro`의 AdWhale 블록을 매체 앱의 `proguard-rules.pro`에 복사합니다.
  - `AdWhale Mediation SDK` 블록은 필수입니다.
  - 어댑터 블록(Cauly, Admize, AdFit, Admob, LevelPlay)은 사용하는 어댑터에 해당하는 것만 복사합니다.
  - `AdManager Adapter SDK` 블록은 Admob 어댑터(`admob-sdk`)에 포함된 클래스용이므로, Admob 어댑터를 쓰면 `Admob Adapter SDK` 블록과 함께 복사합니다.
  - LevelPlay 미디에이션의 하위 네트워크(AppLovin, InMobi, Liftoff, Mintegral, Pangle, Unity 등)를 쓰는 경우 네트워크별 ProGuard 규칙이 추가로 필요합니다. 이 샘플에는 포함되어 있지 않으므로 ADwhale Mediation SDK 가이드에서 해당 네트워크를 선택해 생성된 블록을 사용합니다.
- `shrinkResources true`를 쓰면서 Admob 또는 LevelPlay 미디에이션으로 **Pangle·Mintegral**을 함께 쓰는 경우, 네트워크 SDK 리소스가 제거되지 않도록 `app/src/main/res/raw/keep.xml`을 추가합니다. (이 샘플에는 Pangle·Mintegral이 포함되어 있지 않습니다.)

  ```xml
  <!-- 이미 keep.xml 이 있다면 tools:keep 값에 아래 항목을 쉼표(,)로 이어 추가합니다.
       추가·변경 후에는 반드시 clean 빌드하세요. -->
  <resources xmlns:tools="http://schemas.android.com/tools"
      tools:keep="@drawable/tt_*,@string/tt_*,@layout/tt_*,@anim/tt_*,@color/tt_*,@style/tt_*,@id/tt_*,@dimen/tt_*,@layout/mbridge_*,@drawable/mbridge_*,@anim/mbridge_*,@color/mbridge_*,@string/mbridge_*,@dimen/mbridge_*,@style/mbridge_*,@id/mbridge_*" />
  ```

  Pangle만 쓰면 `tt_*` 항목만, Mintegral만 쓰면 `mbridge_*` 항목만 넣습니다.
